package com.skunkworks.fastorm.processor.cache;

import org.apache.commons.lang.NotImplementedException;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;

/**
 * stole on 19.02.17.
 */
public class CacheGenerator {
    private final ProcessingEnvironment processingEnv;
    private final Messager messager;
    private final Filer filer;

    public CacheGenerator(ProcessingEnvironment processingEnv) {
        this.processingEnv = processingEnv;
        messager = processingEnv.getMessager();
        filer = processingEnv.getFiler();
    }

    public void generateCache(Element annotatedElement) throws Exception {
        throw new NotImplementedException("public void generateCache(Element annotatedElement) throws Exception");
    }
}
