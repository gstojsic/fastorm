package com.skunkworks.fastorm.processor.cache;

import com.skunkworks.fastorm.annotations.Cache;
import com.skunkworks.fastorm.processor.AbstractGenerator;
import com.skunkworks.fastorm.processor.cache.template.FieldData;
import com.skunkworks.fastorm.processor.cache.template.MethodData;
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

        annotatedElement.getEnclosedElements().forEach(enclosedElement -> {
            if (ElementKind.METHOD == enclosedElement.getKind()) {
                MethodData methodData = processMethod(enclosedElement);
//                if (MethodType.QUERY_SINGLE.equals(methodData.getType())) {
//                    queryMethods.add(methodData);
//                } else if (MethodType.QUERY_LIST.equals(methodData.getType())) {
//                    queryListMethods.add(methodData);
//                } else {
                unrecognizedMethods.add(methodData);
//                }
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
                findFirst().orElse(null));
        if (idField == null)
            throw new RuntimeException("Id field not found");
        context.put("idField", idField);


        context.put("fields", fields);
        context.put("additionalImports", additionalImports);
        context.put("unrecognizedMethods", unrecognizedMethods);

        write(className, "cache/Cache.ftl", context);
    }

    private FieldData processField(Element field, String name) {
        //warn("element type:" + field.asType());
        //warn("element kind:" + field.asType().getKind());

        Id idAnnotation = field.getAnnotation(Id.class);
        boolean isId = idAnnotation != null;
        String getterPrefix = field.asType().getKind().equals(TypeKind.BOOLEAN) ? "is" : "get";
        String capitalizedName = name.substring(0, 1).toUpperCase() + name.substring(1);
        String getter = getterPrefix + capitalizedName;
        String setter = "set" + capitalizedName;
        String canonicalType = field.asType().toString();
        //warn("canonicalType:" + canonicalType);

        TypeElement fieldType = getTypeElement(canonicalType);
        //warn("type:" + fieldType.getSimpleName().toString());

        if (!field.asType().toString().startsWith("java.lang")) {
            additionalImports.add(field.asType().toString());
        }
        return new FieldData(name, fieldType.getSimpleName().toString(), getter, setter, isId);
    }

    private MethodData processMethod(Element methodElement) {
        ExecutableElement method = (ExecutableElement) methodElement;
        MethodData methodData = new MethodData();
        methodData.setName(method.getSimpleName().toString());

        //Return type
//        warn("cache:" + method.getReturnType().toString());
//        warn("cache:" + method.getReturnType().getKind().toString());

        TypeElement returnTypeElement = getTypeElement(method.getReturnType());
        DeclaredType declaredReturnType = (DeclaredType) method.getReturnType();
        methodData.setReturnType(getTypeString(returnTypeElement, declaredReturnType));

        //method parameters
        ArrayList<String> parameters = new ArrayList<>();
        //List<QueryParameter> queryParameters = new ArrayList<>();
        //int index = 0;
        for (VariableElement param : method.getParameters()) {
            TypeElement paramElement = getTypeElement(param.asType().toString());
            String methodParameterName = param.getSimpleName().toString();
            parameters.add(paramElement.getSimpleName().toString() + " " + methodParameterName);

            //warn("Parameter:" + paramElement.getSimpleName().toString() + ", " + paramElement.asType().toString());
            //Tip
            //String queryParameterType = Tools.getRecordsetType(param, messager);
            //queryParameters.add(new QueryParameter(++index, methodParameterName, queryParameterType));
        }
        //warn(parameters.toString());
        methodData.setParameters(String.join(", ", parameters));
        //methodData.setQueryParameters(queryParameters);

        //query method
        processQueryMethod(methodData, declaredReturnType);
//        warn("cache:" + methodData.toString());
        return methodData;
    }

    private void processQueryMethod(MethodData methodData, DeclaredType declaredReturnType) {
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

                } else {
                    //process Complex key
                }

//                Query ctx = queryContext.ctx;
//                ArrayList<String> whereSegmentList = new ArrayList<>();
//                List<String> params = ctx.getQueryParams();
//                List<String> operators = ctx.getQueryOperators();
//                whereSegmentList.add(params.get(0) + " = ?");
//                for (int i = 1; i < params.size(); i++) {
//                    whereSegmentList.add(operators.get(i - 1));
//                    whereSegmentList.add(params.get(i) + " = ?");
//                }
//                StringBuilder query = new StringBuilder();
//                query.append(String.join(" ", whereSegmentList));
//                if (ctx.getOrderByParam() != null) {
//                    query.append(" order by ").append(ctx.getOrderByParam());
//                }
//                methodData.setQuery(query.toString());
//
//                if (declaredReturnType.getTypeArguments().size() > 0) {
//                    methodData.setType(MethodType.QUERY_LIST);
//                } else {
//                    methodData.setType(MethodType.QUERY_SINGLE);
//                }

                //TODO - fix prepared statement parameters and orderBy
//                methodData.setQueryParameters(queryParameters);
            } else {
                warn("Method " + methodData.getName() + " has not been succesfully parsed. Errors:" + parser.getNumberOfSyntaxErrors());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
