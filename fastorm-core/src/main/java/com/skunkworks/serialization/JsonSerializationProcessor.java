package com.skunkworks.serialization;

import com.skunkworks.serialization.annotation.JsonSerializable;
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
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * stole on 11.12.16.
 */
public class JsonSerializationProcessor extends AbstractProcessor {
    private Messager messager;
    private Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
        filer = processingEnv.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(JsonSerializable.class)) {

            // Check if a class has been annotated with @JsonSerializable
            if (annotatedElement.getKind() != ElementKind.CLASS) {
                error(annotatedElement, "Only classes can be annotated with @%s",
                        JsonSerializable.class.getSimpleName());
                return true; // Exit processing
            }

            try {
                generateCode(annotatedElement, filer);
            } catch (Exception e) {
                error(null, e.getMessage());
            }
        }
        return false;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new LinkedHashSet<>();
        annotations.add(JsonSerializable.class.getCanonicalName());
        return annotations;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    private void generateCode(Element annotatedElement, Filer filer) throws Exception {
        List<FieldData> fields = new ArrayList<>();

        JavaFileObject jfo = filer.createSourceFile(
                annotatedElement.getSimpleName() + "JsonSerializer");

        messager.printMessage(
                Diagnostic.Kind.NOTE,
                "creating source file: " + jfo.toUri());

        Writer writer = jfo.openWriter();

        Properties props = new Properties();
        URL url = this.getClass().getClassLoader().getResource("velocity.properties");
        props.load(url.openStream());

        VelocityEngine ve = new VelocityEngine(props);
        ve.init();

        VelocityContext vc = new VelocityContext();

        PackageElement packageElement = (PackageElement) annotatedElement.getEnclosingElement();

        vc.put("packageName", packageElement.getQualifiedName().toString());
        vc.put("className", annotatedElement.getSimpleName());

        for (Element enclosedElement : annotatedElement.getEnclosedElements()) {
            String name = enclosedElement.getSimpleName().toString();
            if (enclosedElement.getKind().isField() && !"DEFAULT_VALUE".equals(name)) {
                String prefix = enclosedElement.asType().getKind().equals(TypeKind.BOOLEAN) ? "is" : "get";
                boolean isString = enclosedElement.asType().getKind().equals(TypeKind.DECLARED);
                fields.add(new FieldData(name, prefix + name.substring(0, 1).toUpperCase() + name.substring(1), isString));
            }
        }
        vc.put("fields", fields);

        Template vt = ve.getTemplate("velocity/jsonSerializer.vm");

        messager.printMessage(
                Diagnostic.Kind.NOTE,
                "applying velocity template: " + vt.getName());
        vt.merge(vc, writer);

        writer.close();
    }

    private void error(Element e, String msg, Object... args) {
        messager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args), e);
    }

    public static class FieldData {
        private String name;
        private String getter;
        private boolean string;

        public FieldData(String name, String getter, boolean string) {
            this.name = name;
            this.getter = getter;
            this.string = string;
        }

        public String getName() {
            return name;
        }

        public String getGetter() {
            return getter;
        }

        public boolean isString() {
            return string;
        }
    }
}
