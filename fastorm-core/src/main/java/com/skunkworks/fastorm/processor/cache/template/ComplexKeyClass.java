package com.skunkworks.fastorm.processor.cache.template;

import java.util.List;

/**
 * stole on 23.06.17.
 */
public class ComplexKeyClass {
    private final String className;
    private final String constructorParams;
    private final List<ComplexKeyField> fields;
    private final List<String> constructorInitializers;

    public ComplexKeyClass(
            String keyClassName,
            String constructorParams,
            List<ComplexKeyField> fields,
            List<String> constructorInitializers
    ) {
        this.className = keyClassName;
        this.constructorParams = constructorParams;
        this.fields = fields;
        this.constructorInitializers = constructorInitializers;
    }

    public String getClassName() {
        return className;
    }

    public String getConstructorParams() {
        return constructorParams;
    }

    public List<ComplexKeyField> getFields() {
        return fields;
    }

    public List<String> getConstructorInitializers() {
        return constructorInitializers;
    }

    @Override
    public String toString() {
        return "ComplexKeyClass{" +
                "className='" + className + '\'' +
                ", constructorParams='" + constructorParams + '\'' +
                ", fields=" + fields +
                ", constructorInitializers=" + constructorInitializers +
                '}';
    }
}
