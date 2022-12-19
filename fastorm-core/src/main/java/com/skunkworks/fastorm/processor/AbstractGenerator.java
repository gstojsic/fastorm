package com.skunkworks.fastorm.processor;

import com.skunkworks.fastorm.processor.tool.Tools;
import freemarker.log.Logger;
import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.JavaFileObject;
import java.io.Writer;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * stole on 09.06.17.
 */
public class AbstractGenerator {
    protected final ProcessingEnvironment processingEnv;
    protected final Messager messager;
    private final Filer filer;

    public AbstractGenerator(ProcessingEnvironment processingEnv) {
        this.processingEnv = processingEnv;
        this.messager = processingEnv.getMessager();
        this.filer = processingEnv.getFiler();
    }

    protected void write(String filename, String templatePath, Map<String, Object> context) throws Exception {
        //Logger.selectLoggerLibrary(Logger.LIBRARY_NONE); //Disable logging
        //Logger.getLogger("").isDebugEnabled()
        JavaFileObject jfo = filer.createSourceFile(filename);
        try (Writer writer = jfo.openWriter()) {
            Configuration cfg = new Configuration(Configuration.VERSION_2_3_26);
            cfg.setClassForTemplateLoading(getClass(), "/freemarker");
            cfg.setDefaultEncoding("UTF-8");
            cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
            cfg.setLogTemplateExceptions(false);
            cfg.setLocale(Locale.ROOT);

            freemarker.template.Template temp = cfg.getTemplate(templatePath);
            temp.process(context, writer);
        }
    }

    protected String getTypeString(TypeElement returnTypeElement, DeclaredType declaredReturnType) {
        if (declaredReturnType.getTypeArguments().size() > 0) {
            String typeParameters = declaredReturnType.getTypeArguments().stream().
                    map(typeMirror -> {
                        TypeElement paramElement = getTypeElement(typeMirror.toString());
                        return paramElement.getSimpleName().toString();
                    }).
                    collect(Collectors.joining(", "));

            return returnTypeElement.getSimpleName().toString() +
                    '<' + String.join(", ", typeParameters) + '>';
        } else {
            return returnTypeElement.getSimpleName().toString();
        }
    }

    protected TypeElement getTypeElement(TypeMirror t) {
        return (TypeElement) processingEnv.getTypeUtils().asElement(t);
    }

    protected TypeElement getTypeElement(CharSequence name) {
        return processingEnv.getElementUtils().getTypeElement(name);
    }

    protected String getFieldType(Element field, Set<String> additionalImports) {
        TypeKind kind = field.asType().getKind();
        final String type;
        if (kind.isPrimitive()) {
            type = field.asType().toString();
        } else {
            String canonicalType = field.asType().toString();
            TypeElement fieldType = getTypeElement(canonicalType);
            if (!field.asType().toString().startsWith("java.lang")) {
                additionalImports.add(field.asType().toString());
            }
            type = fieldType.getSimpleName().toString();
        }
        return type;
    }

    protected void warn(String message) {
        Tools.warn(messager, message);
    }
}