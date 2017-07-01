package com.skunkworks.fastorm.processor.repository;

import com.skunkworks.fastorm.processor.AbstractGenerator;
import com.skunkworks.fastorm.processor.repository.template.FieldData;
import com.skunkworks.fastorm.processor.tool.Tools;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.type.TypeKind;
import javax.persistence.Column;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * stole on 01.07.17.
 */
public class RepositoryGenerator extends AbstractGenerator {
    private static final String CLASS_SUFIX = "Repository";

    public RepositoryGenerator(ProcessingEnvironment processingEnv) {
        super(processingEnv);
    }

    public void generateRepository(Element annotatedElement) throws Exception {

        PackageElement packageElement = (PackageElement) annotatedElement.getEnclosingElement();

        //warn("package element:" + packageElement.toString());

        String entityName = annotatedElement.getSimpleName().toString();
        String className = entityName + CLASS_SUFIX;
        Map<String, Object> context = new HashMap<>();
        context.put("packageName", packageElement.getQualifiedName().toString());
        context.put("className", className);
        context.put("entityName", entityName);

        List<FieldData> fields = new ArrayList<>();
        int fieldIndex = 1;
        for (Element enclosedElement : annotatedElement.getEnclosedElements()) {
            String name = enclosedElement.getSimpleName().toString();
            if (enclosedElement.getKind().isField() && !"DEFAULT_VALUE".equals(name)) {
                fields.add(processField(enclosedElement, name, fieldIndex));
                fieldIndex++;
            }
        }
        context.put("fields", fields);

        String selectedColumns = fields.stream().
                map(FieldData::getColumnName).
                collect(Collectors.joining(", "));
        context.put("selectColumns", selectedColumns);

        write(className, "repository/repository.ftl", context);
    }

    private FieldData processField(Element field, String name, int fieldIndex) {
        //warn("element type:" + field.asType());
        //warn("element kind:" + field.asType().getKind());

        String columnName = getColumnName(field, name);
        String getterPrefix = field.asType().getKind().equals(TypeKind.BOOLEAN) ? "is" : "get";
        String capitalizedName = name.substring(0, 1).toUpperCase() + name.substring(1);
        String getter = getterPrefix + capitalizedName;
        String setter = "set" + capitalizedName;
        String recordsetType = Tools.getRecordsetType(field, messager);

        return new FieldData(fieldIndex, name, columnName, getter, setter, recordsetType);
    }

    private String getColumnName(Element field, String name) {
        Column columnAnnotation = field.getAnnotation(Column.class);
        String columnAnnotationName = (columnAnnotation != null && !"".equals(columnAnnotation.name())) ? columnAnnotation.name() : name;
        //warn("columnAnnotationName:" + columnAnnotationName);
        return columnAnnotationName;
    }
}
