package com.skunkworks.fastorm.processor.cache.template;

/**
 * stole on 23.06.17.
 */
public class ComplexKeyMethodData {
    private final String name;
    private final String returnType;
    private final String parameters;
    private final String keyName;
    private final String keyClass;

    public ComplexKeyMethodData(String name, String returnType, String parameters, String keyName, String keyClass) {
        this.name = name;
        this.returnType = returnType;
        this.parameters = parameters;
        this.keyName = keyName;
        this.keyClass = keyClass;
    }

    public String getName() {
        return name;
    }

    public String getReturnType() {
        return returnType;
    }

    public String getParameters() {
        return parameters;
    }

    public String getKeyName() {
        return keyName;
    }

    public String getKeyClass() {
        return keyClass;
    }
}
