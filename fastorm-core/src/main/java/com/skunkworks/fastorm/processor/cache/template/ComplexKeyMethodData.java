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
    private final String constructorParams;

    public ComplexKeyMethodData(
            String name,
            String returnType,
            String parameters,
            String keyName,
            String keyClass,
            String constructorParams
    ) {
        this.name = name;
        this.returnType = returnType;
        this.parameters = parameters;
        this.keyName = keyName;
        this.keyClass = keyClass;
        this.constructorParams = constructorParams;
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

    public String getConstructorParams() {
        return constructorParams;
    }
}
