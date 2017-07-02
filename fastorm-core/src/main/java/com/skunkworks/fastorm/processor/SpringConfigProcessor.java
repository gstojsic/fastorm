package com.skunkworks.fastorm.processor;

import com.skunkworks.fastorm.annotations.GenerateFastOrmConfig;
import com.skunkworks.fastorm.processor.springconfig.SpringConfigGenerator;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * stole on 02.07.17.
 */
public class SpringConfigProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) {

        Set<? extends Element> annotatedElements = roundEnvironment.getElementsAnnotatedWith(GenerateFastOrmConfig.class);
        if (annotatedElements.size() < 1)
            return true;
        if (annotatedElements.size() != 1) {
            throw new RuntimeException("Only one element can be annotated with GenerateFastOrmConfig");
        }

        final Element annotatedElement = annotatedElements.iterator().next();

        try {
            new SpringConfigGenerator(processingEnv).generateSpringConfig(annotatedElement, roundEnvironment);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new LinkedHashSet<>();
        annotations.add(GenerateFastOrmConfig.class.getCanonicalName());
        return annotations;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
