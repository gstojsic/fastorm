package com.skunkworks.fastorm.impl;

import com.skunkworks.fastorm.GenerateRepository;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.persistence.Column;

/**
 * stole on 21.01.17.
 */
public class RepositoryProcessor extends AbstractProcessor {
    private Messager messager;
    private Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
        filer = processingEnv.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) {

        for (Element annotatedElement : roundEnvironment.getElementsAnnotatedWith(GenerateRepository.class)) {

            // Check if a class has been annotated with @JsonSerializable
            if (annotatedElement.getKind() != ElementKind.CLASS) {
                error(annotatedElement, "Only classes can be annotated with @%s",
                        GenerateRepository.class.getSimpleName());
                return true; // Exit processing
            }

            try {
                generateRepository(annotatedElement);
            } catch (Exception e) {
                error(null, e.getMessage());
            }
        }
        return false;
    }

    private void generateRepository(Element annotatedElement) throws Exception {
        JavaFileObject jfo = filer.createSourceFile(
                annotatedElement.getSimpleName() + "Repository");

        messager.printMessage(
                Diagnostic.Kind.NOTE,
                "creating source file: " + jfo.toUri());

        Writer writer = jfo.openWriter();

        Properties props = new Properties();
        URL url = this.getClass().getClassLoader().getResource("velocity.properties");
        props.load(url.openStream());

        VelocityEngine ve = new VelocityEngine(props);
        ve.init();

        VelocityContext context = new VelocityContext();

        PackageElement packageElement = (PackageElement) annotatedElement.getEnclosingElement();

        warn("package element:" + packageElement.toString());

        context.put("packageName", packageElement.getQualifiedName().toString());
        context.put("className", annotatedElement.getSimpleName());

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

        Template vt = ve.getTemplate("velocity/repository.vm");

        messager.printMessage(
                Diagnostic.Kind.NOTE,
                "applying velocity template: " + vt.getName());
        vt.merge(context, writer);

        writer.close();
    }

    private FieldData processField(Element field, String name, int fieldIndex) {
        warn("element type:" + field.asType());
        warn("element kind:" + field.asType().getKind());

        String columnName = getColumnName(field, name);
        String getterPrefix = field.asType().getKind().equals(TypeKind.BOOLEAN) ? "is" : "get";
        String capitalizedName = name.substring(0, 1).toUpperCase() + name.substring(1);
        String getter = getterPrefix + capitalizedName;
        String setter = "set" + capitalizedName;
        String recordsetType = getRecordsetType(field);
        return new FieldData(fieldIndex, name, columnName, getter, setter, recordsetType);
    }

    private String getColumnName(Element field, String name) {
        Column columnAnnotation = field.getAnnotation(Column.class);
        String columnAnnotationName = (columnAnnotation != null && !"".equals(columnAnnotation.name())) ? columnAnnotation.name() : name;
        //warn("columnAnnotationName:" + columnAnnotationName);
        return columnAnnotationName;
    }

    private String getRecordsetType(Element field) {
        if ("java.lang.Long".equals(field.asType().toString())) {
            return "Long";
        } else if ("java.lang.String".equals(field.asType().toString())) {
            return "String";
        }

        error("Unrecognized type:" + field.asType());
        return null;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new LinkedHashSet<>();
        annotations.add(GenerateRepository.class.getCanonicalName());
        return annotations;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    private void error(Element e, String msg, Object... args) {
        messager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args), e);
    }

    private void warn(String message) {
        messager.printMessage(Diagnostic.Kind.MANDATORY_WARNING, message);
    }

    private void error(String message) {
        messager.printMessage(Diagnostic.Kind.ERROR, message);
    }
}
