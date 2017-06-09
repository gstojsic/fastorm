package com.skunkworks.fastorm.processor;

import com.skunkworks.fastorm.processor.tool.Tools;
import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.tools.JavaFileObject;
import java.io.Writer;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * stole on 09.06.17.
 */
public class AbstractGenerator {
    protected final ProcessingEnvironment processingEnv;
    private final Messager messager;
    private final Filer filer;

    public AbstractGenerator(ProcessingEnvironment processingEnv) {
        this.processingEnv = processingEnv;
        this.messager = processingEnv.getMessager();
        this.filer = processingEnv.getFiler();
    }

    protected void write(String filename, String templatePath, Map<String, Object> context) throws Exception {
        JavaFileObject jfo = filer.createSourceFile(filename);
        warn("AbstractGenerator::write");
        try (Writer writer = jfo.openWriter()) {
            Configuration cfg = new Configuration(Configuration.VERSION_2_3_26);
            cfg.setClassForTemplateLoading(getClass(), "/freemarker");
            cfg.setDefaultEncoding("UTF-8");
            cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
            cfg.setLogTemplateExceptions(false);

            freemarker.template.Template temp = cfg.getTemplate(templatePath);
            temp.process(context, writer);
        }
    }

    protected String getTypeString(TypeElement returnTypeElement, DeclaredType declaredReturnType) {
        if (declaredReturnType.getTypeArguments().size() > 0) {
            String typeParameters = declaredReturnType.getTypeArguments().stream().
                    map(typeMirror -> {
                        TypeElement paramElement = processingEnv.getElementUtils().getTypeElement(typeMirror.toString());
                        return paramElement.getSimpleName().toString();
                        //return typeMirror.toString();
                    }).
                    collect(Collectors.joining(", "));

            return returnTypeElement.getSimpleName().toString() +
                    '<' + String.join(", ", typeParameters) + '>';
        } else {
            return returnTypeElement.getSimpleName().toString();
        }
    }

    protected void warn(String message) {
        Tools.warn(messager, message);
    }
}