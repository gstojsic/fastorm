package com.skunkworks.fastorm.processor.cache;

import com.skunkworks.fastorm.annotations.Cache;
import com.skunkworks.fastorm.processor.AbstractGenerator;
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
import java.util.Set;
import java.util.stream.Collectors;

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
                collect(Collectors.toList());

        //Process Methods of the interface
        //Interface Methods
        List<MethodData> queryMethods = new ArrayList<>();
        List<MethodData> queryListMethods = new ArrayList<>();
        List<MethodData> unrecognizedMethods = new ArrayList<>();

        //indexes
        final List<Index> uniqueIndexes = new ArrayList<>();
        final List<Index> nonuniqueIndexes = new ArrayList<>();
        final List<IndexFillCommand> indexFillCommands = new ArrayList<>();

        annotatedElement.getEnclosedElements().forEach(enclosedElement -> {
            if (ElementKind.METHOD == enclosedElement.getKind()) {
                MethodAnalysisData methodAnalysisData = processMethod(enclosedElement);
                if (MethodType.QUERY_SINGLE.equals(methodAnalysisData.getType())) {
                    //check against fields.
                    FieldData matchingField = fields.stream().
                            filter(field -> methodAnalysisData.getKeyName().equals(field.getName())).
                            findFirst().
                            orElseThrow(() -> new RuntimeException("Matching field not found:" + 2));

                    final Index uniqueIndex = new Index(matchingField.getName(), matchingField.getType(), methodAnalysisData.getReturnType());
                    uniqueIndexes.add(uniqueIndex);
                    queryMethods.add(prepareMethodData(methodAnalysisData));

                    //TODO: indexFillCommands
                    //indexFillCommands.add(new IndexFillCommand());
                } else if (MethodType.QUERY_LIST.equals(methodAnalysisData.getType())) {
                    FieldData matchingField = fields.stream().
                            filter(field -> methodAnalysisData.getKeyName().equals(field.getName())).
                            findFirst().
                            orElseThrow(() -> new RuntimeException("Matching field not found:" + 2));

                    final Index uniqueIndex = new Index(matchingField.getName(), matchingField.getType(), methodAnalysisData.getReturnType());
                    uniqueIndexes.add(uniqueIndex);
                    queryListMethods.add(prepareMethodData(methodAnalysisData));
                } else {
                    unrecognizedMethods.add(prepareMethodData(methodAnalysisData));
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
        context.put("uniqueIndexes", uniqueIndexes);

        context.put("queryMethods", queryMethods);
        context.put("queryListMethods", queryListMethods);

        context.put("indexFillCommands", indexFillCommands);
        context.put("unrecognizedMethods", unrecognizedMethods);

        write(className, "cache/Cache.ftl", context);
    }

    private MethodData prepareMethodData(MethodAnalysisData methodAnalysisData) {
        final String keyParameter = methodAnalysisData.getParameterNames().get(0); //TODO: more checks
        return new MethodData(
                methodAnalysisData.getName(),
                methodAnalysisData.getReturnType(),
                String.join(", ", methodAnalysisData.getParameters()),
                methodAnalysisData.getKeyName(),
                keyParameter);
    }

    private FieldData processField(Element field, String name) {

        Id idAnnotation = field.getAnnotation(Id.class);
        boolean isId = idAnnotation != null;
        String getterPrefix = field.asType().getKind().equals(TypeKind.BOOLEAN) ? "is" : "get";
        String capitalizedName = Tools.capitalizeFirstLetter(name);
        String getter = getterPrefix + capitalizedName;
        String setter = "set" + capitalizedName;
        String canonicalType = field.asType().toString();

        TypeElement fieldType = getTypeElement(canonicalType);

        if (!field.asType().toString().startsWith("java.lang")) {
            additionalImports.add(field.asType().toString());
        }

        return new FieldData(name, fieldType.getSimpleName().toString(), getter, setter, isId);
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
        ArrayList<String> parameters = new ArrayList<>();
        ArrayList<String> parameterNames = new ArrayList<>();
        for (VariableElement param : method.getParameters()) {
            TypeElement paramElement = getTypeElement(param.asType().toString());
            String methodParameterName = param.getSimpleName().toString();
            parameters.add(paramElement.getSimpleName().toString() + " " + methodParameterName);
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
                if (keyComponents.size() == 1) {
                    //process Simple key
                    methodData.setKeyName(Tools.lowercaseFirstLetter(keyComponents.get(0)));
                } else {
                    //process Complex key
                }

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
