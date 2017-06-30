package com.skunkworks.fastorm.processor.cache;

import com.skunkworks.fastorm.annotations.Cache;
import com.skunkworks.fastorm.processor.AbstractGenerator;
import com.skunkworks.fastorm.processor.cache.template.ComplexIndexFillCommand;
import com.skunkworks.fastorm.processor.cache.template.ComplexKeyClass;
import com.skunkworks.fastorm.processor.cache.template.ComplexKeyMethodData;
import com.skunkworks.fastorm.processor.cache.template.FieldData;
import com.skunkworks.fastorm.processor.cache.template.Index;
import com.skunkworks.fastorm.processor.cache.template.IndexFillCommand;
import com.skunkworks.fastorm.processor.cache.template.MethodData;
import com.skunkworks.fastorm.processor.cache.template.MethodType;
import com.skunkworks.fastorm.processor.tool.Tools;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.skunkworks.fastorm.parser.CacheQueryLexer;
import org.skunkworks.fastorm.parser.CacheQueryParser;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.persistence.Id;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static java.util.stream.Collectors.*;

/**
 * stole on 19.02.17.
 */
public class CacheGenerator extends AbstractGenerator {
    private Set<String> additionalImports = new HashSet<>();

    public CacheGenerator(ProcessingEnvironment processingEnv) {
        super(processingEnv);
    }

    public void generateCache(Element annotatedElement) throws Exception {
        final String cacheName = Cache.class.getName();
        AnnotationValue cacheValue = null;
        for (AnnotationMirror am : annotatedElement.getAnnotationMirrors()) {
            if (cacheName.equals(am.getAnnotationType().toString())) {
                for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : am.getElementValues().entrySet()) {
                    if ("value".equals(entry.getKey().getSimpleName().toString())) {
                        cacheValue = entry.getValue();
                        break;
                    }
                }
            }
        }

        if (cacheValue == null) {
            return;
        }

        TypeMirror cacheMirror = (TypeMirror) cacheValue.getValue();
        TypeElement cacheValueElement = getTypeElement(cacheMirror.toString());

        List<FieldData> fields = cacheValueElement.getEnclosedElements().stream().
                filter(element -> element.getKind().isField()).
                filter(element -> !"DEFAULT_VALUE".equals(element.getSimpleName().toString())).
                map(element -> processField(element, element.getSimpleName().toString())).
                collect(toList());

        //Process Methods of the interface
        //Interface Methods
        List<MethodData> queryMethods = new ArrayList<>();
        List<ComplexKeyMethodData> complexKeyQueryMethods = new ArrayList<>();
        List<MethodData> unrecognizedMethods = new ArrayList<>();

        //indexes
        final List<Index> indexes = new ArrayList<>();
        final List<IndexFillCommand> indexFillCommands = new ArrayList<>();
        final List<IndexFillCommand> listIndexFillCommands = new ArrayList<>();

        final List<ComplexIndexFillCommand> indexComplexFillCommands = new ArrayList<>();

        final List<IndexFillCommand> indexUpdateCommands = new ArrayList<>();
        final List<IndexFillCommand> indexDeleteCommands = new ArrayList<>();

        final List<ComplexKeyClass> complexKeyClasses = new ArrayList<>();

        annotatedElement.getEnclosedElements().forEach(enclosedElement -> {
            if (ElementKind.METHOD == enclosedElement.getKind()) {
                MethodAnalysisData methodAnalysisData = processMethod(enclosedElement);

                if (MethodType.UNRECOGNIZED.equals(methodAnalysisData.getType())) {
                    unrecognizedMethods.add(prepareMethodData(methodAnalysisData, null));
                    return;
                }

                List<String> keyComponents = methodAnalysisData.getKeyComponents();
                if (keyComponents == null || keyComponents.isEmpty())
                    throw new RuntimeException("There are no key components.");

                //Check all keyComponents have fields.
                boolean allComponentsHaveFields = keyComponents.stream().
                        map(Tools::lowercaseFirstLetter).
                        allMatch(keyComponent -> fields.stream().anyMatch(fieldData -> keyComponent.equals(fieldData.getName())));

                if (!allComponentsHaveFields)
                    throw new RuntimeException("Not all key components have matching fields.");

                String capitalizedKeyName = String.join("And", keyComponents);
                String indexName = Tools.lowercaseFirstLetter(capitalizedKeyName);

                if (keyComponents.size() > 1) {
                    // Complex Key
                    String keyClassName = capitalizedKeyName + "Key";

                    final Index index = new Index(indexName, keyClassName, methodAnalysisData.getReturnType());
                    indexes.add(index);

                    complexKeyQueryMethods.add(prepareComplexKeyMethodData(methodAnalysisData, indexName, keyClassName));

                    //compute required fields
                    List<String> keyComponentsLowercase = keyComponents.stream().
                            map(Tools::lowercaseFirstLetter).
                            collect(toList());
                    List<FieldData> componentFields = keyComponentsLowercase.stream().
                            map(keyComponent -> fields.stream().
                                    filter(fieldData -> keyComponent.equals(fieldData.getName())).
                                    findFirst().
                                    orElseThrow(() -> new RuntimeException("Field not found:" + keyComponent))).
                            collect(toList());

                    List<String> constructorParams = componentFields.stream().
                            map(FieldData::getGetter).
                            collect(toList());
                    ComplexIndexFillCommand complexIndexFillCommand = new ComplexIndexFillCommand(indexName, keyClassName, constructorParams);

                    if (MethodType.QUERY_SINGLE.equals(methodAnalysisData.getType())) {
                        indexComplexFillCommands.add(complexIndexFillCommand);
                    } else if (MethodType.QUERY_LIST.equals(methodAnalysisData.getType())) {
//                        listIndexFillCommands.add(indexFillCommand);
//                        additionalImports.add(ArrayList.class.getCanonicalName());
                    } else {
                        throw new RuntimeException("Unknown method type:" + methodAnalysisData.getType());
                    }
                    Map<String, String> keyConstructorParams = componentFields.stream().
                            collect(toMap(FieldData::getName, FieldData::getType));

                    Map<String, String> complexKeyFields = componentFields.stream().
                            collect(toMap(FieldData::getName, FieldData::getType));

                    //equals Items
                    Map<Boolean, List<String>> partitionByPrimitive = componentFields.stream().
                            collect(partitioningBy(FieldData::isPrimitive, mapping(FieldData::getName, toList())));

                    //hashParams
                    List<String> hashParams = componentFields.stream().
                            map(FieldData::getName).
                            collect(toList());
                    additionalImports.add(Objects.class.getCanonicalName()); // Objects is needed for the hash method

                    //Setup complex key class
                    complexKeyClasses.add(new ComplexKeyClass(
                            keyClassName,
                            keyConstructorParams,
                            complexKeyFields,
                            keyComponentsLowercase,
                            partitionByPrimitive.get(true),
                            partitionByPrimitive.get(false),
                            hashParams
                    ));

                } else {
                    // Simple Key
                    //check against fields.
                    FieldData matchingField = fields.stream().
                            filter(field -> indexName.equals(field.getName())).
                            findFirst().
                            orElseThrow(() -> new RuntimeException("Matching field not found:" + 2));

                    final Index index = new Index(indexName, matchingField.getType(), methodAnalysisData.getReturnType());
                    indexes.add(index);

                    queryMethods.add(prepareMethodData(methodAnalysisData, indexName));

                    IndexFillCommand indexFillCommand = new IndexFillCommand(matchingField.getName(), matchingField.getGetter());
                    if (MethodType.QUERY_SINGLE.equals(methodAnalysisData.getType())) {
                        indexFillCommands.add(indexFillCommand);
                    } else if (MethodType.QUERY_LIST.equals(methodAnalysisData.getType())) {
                        listIndexFillCommands.add(indexFillCommand);
                        additionalImports.add(ArrayList.class.getCanonicalName());
                    } else {
                        throw new RuntimeException("Unknown method type:" + methodAnalysisData.getType());
                    }
                }
            }
        });


        String interfaceName = annotatedElement.getSimpleName().toString();
        String className = interfaceName + "Impl";
        PackageElement packageElement = (PackageElement) annotatedElement.getEnclosingElement();

        Map<String, Object> context = new HashMap<>();
        context.put("packageName", packageElement.getQualifiedName().toString());
        context.put("interfaceName", interfaceName);
        context.put("className", className);
        String entityName = cacheValueElement.getSimpleName().toString();
        context.put("entityName", entityName);

        FieldData idField = fields.stream().
                filter(FieldData::isId).
                findFirst().orElseGet(() -> fields.stream().
                filter(field -> "id".equals(field.getName().toLowerCase())).
                findFirst().
                orElseThrow(() -> new RuntimeException("Id field not found")));
        context.put("idField", idField);

        context.put("fields", fields);
        context.put("additionalImports", additionalImports);
        context.put("indexes", indexes);

        context.put("queryMethods", queryMethods);
        context.put("complexKeyQueryMethods", complexKeyQueryMethods);

        context.put("indexFillCommands", indexFillCommands);
        context.put("listIndexFillCommands", listIndexFillCommands);
        context.put("indexComplexFillCommands", indexComplexFillCommands);

        context.put("indexUpdateCommands", indexUpdateCommands);
        context.put("indexDeleteCommands", indexDeleteCommands);

        context.put("complexKeyClasses", complexKeyClasses);

        context.put("unrecognizedMethods", unrecognizedMethods);

        write(className, "cache/Cache.ftl", context);
    }

    private MethodData prepareMethodData(MethodAnalysisData methodAnalysisData, String keyName) {
        final String keyParameter = methodAnalysisData.getParameterNames().get(0); //TODO: more checks
        return new MethodData(
                methodAnalysisData.getName(),
                methodAnalysisData.getReturnType(),
                methodAnalysisData.getParameters(),
                keyName,
                keyParameter
        );
    }

    private ComplexKeyMethodData prepareComplexKeyMethodData(
            MethodAnalysisData methodAnalysisData,
            String keyName,
            String keyClass
    ) {
        return new ComplexKeyMethodData(
                methodAnalysisData.getName(),
                methodAnalysisData.getReturnType(),
                methodAnalysisData.getParameters(),
                keyName,
                keyClass,
                methodAnalysisData.getParameterNames());
    }

    private FieldData processField(Element field, String name) {

        Id idAnnotation = field.getAnnotation(Id.class);
        boolean isId = idAnnotation != null;
        TypeKind kind = field.asType().getKind();
        String getterPrefix = kind.equals(TypeKind.BOOLEAN) ? "is" : "get";
        String capitalizedName = Tools.capitalizeFirstLetter(name);
        String getter = getterPrefix + capitalizedName;
        String setter = "set" + capitalizedName;
        String canonicalType = field.asType().toString();

        TypeElement fieldType = getTypeElement(canonicalType);

        if (!field.asType().toString().startsWith("java.lang")) {
            additionalImports.add(field.asType().toString());
        }

        return new FieldData(name, fieldType.getSimpleName().toString(), getter, setter, isId, kind.isPrimitive());
    }

    private MethodAnalysisData processMethod(Element methodElement) {
        ExecutableElement method = (ExecutableElement) methodElement;
        MethodAnalysisData methodData = new MethodAnalysisData();
        methodData.setName(method.getSimpleName().toString());

        //Return type
        TypeElement returnTypeElement = getTypeElement(method.getReturnType());
        DeclaredType declaredReturnType = (DeclaredType) method.getReturnType();
        methodData.setReturnType(getTypeString(returnTypeElement, declaredReturnType));

        //method parameters
        Map<String, String> parameters = new HashMap<>();
        ArrayList<String> parameterNames = new ArrayList<>();
        for (VariableElement param : method.getParameters()) {
            TypeElement paramElement = getTypeElement(param.asType().toString());
            String methodParameterName = param.getSimpleName().toString();
            parameters.put(methodParameterName, paramElement.getSimpleName().toString());
            parameterNames.add(methodParameterName);
        }
        methodData.setParameters(parameters);
        methodData.setParameterNames(parameterNames);

        //query method
        processQueryMethod(methodData, method.getReturnType());
        return methodData;
    }

    private void processQueryMethod(MethodAnalysisData methodData, TypeMirror declaredReturnType) {
        final InputStream is = new ByteArrayInputStream(methodData.getName().getBytes(Charset.forName("UTF-8")));
        try {
            final CharStream inputStream = CharStreams.fromStream(is);
            final CacheQueryLexer lexer = new CacheQueryLexer(inputStream);
            final CommonTokenStream tokens = new CommonTokenStream(lexer);
            final CacheQueryParser parser = new CacheQueryParser(tokens);

            ParseTree tree = parser.query();
            ParseTreeWalker walker = new ParseTreeWalker();
            CacheQueryListener listener = new CacheQueryListener();
            walker.walk(listener, tree);

            if (parser.getNumberOfSyntaxErrors() == 0) {

                List<String> keyComponents = listener.getKeyComponents();
                methodData.setKeyComponents(keyComponents);

                TypeElement returnTypeElement = getTypeElement(declaredReturnType);
                if (List.class.getCanonicalName().equals(returnTypeElement.getQualifiedName().toString()) ||
                        Collection.class.getCanonicalName().equals(returnTypeElement.getQualifiedName().toString())) {
                    methodData.setType(MethodType.QUERY_LIST);
                } else {
                    methodData.setType(MethodType.QUERY_SINGLE);
                }
            } else {
                warn("Method " + methodData.getName() + " has not been succesfully parsed. Errors:" + parser.getNumberOfSyntaxErrors());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
