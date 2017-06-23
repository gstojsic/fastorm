package com.skunkworks.fastorm.processor.cache.template;

/**
 * stole on 23.06.17.
 */
public class ComplexKeyClass {
    private final String className;

    public ComplexKeyClass(String keyClassName) {
        this.className = keyClassName;
    }

    public String getClassName() {
        return className;
    }
}
