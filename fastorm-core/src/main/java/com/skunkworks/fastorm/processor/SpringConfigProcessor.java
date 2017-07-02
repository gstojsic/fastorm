package com.skunkworks.fastorm.processor;

import com.skunkworks.fastorm.annotations.GenerateSpringCacheConfig;
import com.skunkworks.fastorm.annotations.GenerateSpringDaoConfig;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * stole on 02.07.17.
 */
public class SpringConfigProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        return false;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new LinkedHashSet<>();
        annotations.add(GenerateSpringCacheConfig.class.getCanonicalName());
        annotations.add(GenerateSpringDaoConfig.class.getCanonicalName());
        return annotations;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
