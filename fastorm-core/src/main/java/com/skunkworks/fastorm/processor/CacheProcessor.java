package com.skunkworks.fastorm.processor;

import com.skunkworks.fastorm.annotations.Cache;
import com.skunkworks.fastorm.processor.cache.CacheGenerator;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * stole on 19.02.17.
 */
public class CacheProcessor extends AbstractProcessor {
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

        for (Element annotatedElement : roundEnvironment.getElementsAnnotatedWith(Cache.class)) {
            // Check if a class has been annotated with @Dao
            if (annotatedElement.getKind() != ElementKind.INTERFACE) {
                error(annotatedElement, "Only interfaces can be annotated with @%s", Cache.class.getSimpleName());
                return true; // Exit processing
            }

            try {
                new CacheGenerator(processingEnv).generateCache(annotatedElement);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return false;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new LinkedHashSet<>();
        annotations.add(Cache.class.getCanonicalName());
        return annotations;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    private void error(Element e, String msg, Object... args) {
        messager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args), e);
    }
}
