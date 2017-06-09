package com.skunkworks.fastorm.processor.cache;

import com.skunkworks.fastorm.annotations.Cache;
import com.skunkworks.fastorm.processor.AbstractGenerator;
import com.skunkworks.fastorm.processor.cache.template.FieldData;
import com.skunkworks.fastorm.processor.cache.template.MethodData;
import com.skunkworks.fastorm.processor.tool.Tools;

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
import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * stole on 19.02.17.
 */
public class CacheGenerator extends AbstractGenerator {

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
        TypeElement cacheValueElement = processingEnv.getElementUtils().getTypeElement(cacheMirror.toString());

        List<FieldData> fields = new ArrayList<>();
        int fieldIndex = 1;
        for (Element enclosedElement : cacheValueElement.getEnclosedElements()) {
            String name = enclosedElement.getSimpleName().toString();
            if (enclosedElement.getKind().isField() && !"DEFAULT_VALUE".equals(name)) {

                fields.add(processField(enclosedElement, name, fieldIndex));
                fieldIndex++;
            }
        }

        //Process Methods of the interface
        //Interface Methods
        List<MethodData> queryMethods = new ArrayList<>();
        List<MethodData> queryListMethods = new ArrayList<>();
        List<MethodData> storedProcedureMethods = new ArrayList<>();
        List<MethodData> unrecognizedMethods = new ArrayList<>();

        annotatedElement.getEnclosedElements().forEach(enclosedElement -> {
            if (ElementKind.METHOD == enclosedElement.getKind()) {
                MethodData methodData = processMethod(enclosedElement);
//                if (MethodType.QUERY_SINGLE.equals(methodData.getType())) {
//                    queryMethods.add(methodData);
//                } else if (MethodType.QUERY_LIST.equals(methodData.getType())) {
//                    queryListMethods.add(methodData);
//                } else if (MethodType.STORED_PROCEDURE.equals(methodData.getType())) {
//                    storedProcedureMethods.add(methodData);
//                } else {
                    unrecognizedMethods.add(methodData);
//                }
            }
        });


        Set<String> additionalImports = new HashSet<>();

        String interfaceName = annotatedElement.getSimpleName().toString();
        String className = interfaceName + "Impl";
        PackageElement packageElement = (PackageElement) annotatedElement.getEnclosingElement();

        Map<String, Object> context = new HashMap<>();
        context.put("packageName", packageElement.getQualifiedName().toString());
        context.put("interfaceName", interfaceName);
        context.put("className", className);
        context.put("fields", fields);
        context.put("additionalImports", additionalImports);
        context.put("unrecognizedMethods", unrecognizedMethods);

        write(className, "cache/Cache.ftl", context);
    }

    private FieldData processField(Element field, String name, int fieldIndex) {
        //warn("element type:" + field.asType());
        //warn("element kind:" + field.asType().getKind());

//        String columnName = getColumnName(field, name);
//        String getterPrefix = field.asType().getKind().equals(TypeKind.BOOLEAN) ? "is" : "get";
//        String capitalizedName = name.substring(0, 1).toUpperCase() + name.substring(1);
//        String getter = getterPrefix + capitalizedName;
//        String setter = "set" + capitalizedName;
//        String recordsetType = Tools.getRecordsetType(field, messager);
        return new FieldData();
    }

    private MethodData processMethod(Element methodElement) {
        ExecutableElement method = (ExecutableElement) methodElement;
        MethodData methodData = new MethodData();
        methodData.setName(method.getSimpleName().toString());

        //Return type
        warn(method.getReturnType().toString());
        warn(method.getReturnType().getKind().toString());

        TypeElement returnTypeElement = (TypeElement) processingEnv.getTypeUtils().asElement(method.getReturnType());
        DeclaredType declaredReturnType = (DeclaredType) method.getReturnType();
        methodData.setReturnType(getTypeString(returnTypeElement, declaredReturnType));

        //method parameters
        ArrayList<String> parameters = new ArrayList<>();
        //List<QueryParameter> queryParameters = new ArrayList<>();
        //int index = 0;
        for (VariableElement param : method.getParameters()) {
            TypeElement paramElement = processingEnv.getElementUtils().getTypeElement(param.asType().toString());
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
        //processQueryMethod(methodData, declaredReturnType);
        return methodData;
    }
}
