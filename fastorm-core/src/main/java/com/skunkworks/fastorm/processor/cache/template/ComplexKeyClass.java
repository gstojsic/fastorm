package com.skunkworks.fastorm.processor.cache.template;

import java.util.List;
import java.util.Map;

/**
 * stole on 23.06.17.
 */
public class ComplexKeyClass {
    private final String className;
    private final Map<String, String> constructorParams;
    private final Map<String, String> fields;
    private final List<String> constructorInitializers;
    private final List<String> primitiveEquals;
    private final List<String> nonprimitiveEquals;
    private final List<String> hashParams;

    public ComplexKeyClass(
            String keyClassName,
            Map<String, String> constructorParams,
            Map<String, String> fields,
            List<String> constructorInitializers,
            List<String> primitiveEquals,
            List<String> nonprimitiveEquals,
            List<String> hashParams
    ) {
        this.className = keyClassName;
        this.constructorParams = constructorParams;
        this.fields = fields;
        this.constructorInitializers = constructorInitializers;
        this.primitiveEquals = primitiveEquals;
        this.nonprimitiveEquals = nonprimitiveEquals;
        this.hashParams = hashParams;
    }

    public String getClassName() {
        return className;
    }

    public Map<String, String> getConstructorParams() {
        return constructorParams;
    }

    public Map<String, String> getFields() {
        return fields;
    }

    public List<String> getConstructorInitializers() {
        return constructorInitializers;
    }

    public List<String> getPrimitiveEquals() {
        return primitiveEquals;
    }

    public List<String> getNonprimitiveEquals() {
        return nonprimitiveEquals;
    }

    public List<String> getHashParams() {
        return hashParams;
    }

    @Override
    public String toString() {
        return "ComplexKeyClass{" +
                "className='" + className + '\'' +
                ", constructorParams=" + constructorParams +
                ", fields=" + fields +
                ", constructorInitializers=" + constructorInitializers +
                ", hashParams=" + hashParams +
                '}';
    }
}
