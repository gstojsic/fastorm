package com.skunkworks.fastorm.processor.cache.template;

import java.util.List;
import java.util.Map;

/**
 * stole on 23.06.17.
 */
public class ComplexKeyMethodData {
    private final String name;
    private final String returnType;
    private final Map<String, String> parameters;
    private final String keyName;
    private final String keyClass;
    private final List<String> constructorParams;

    public ComplexKeyMethodData(
            String name,
            String returnType,
            Map<String, String> parameters,
            String keyName,
            String keyClass,
            List<String> constructorParams
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

    public Map<String, String> getParameters() {
        return parameters;
    }

    public String getKeyName() {
        return keyName;
    }

    public String getKeyClass() {
        return keyClass;
    }

    public List<String> getConstructorParams() {
        return constructorParams;
    }

    @Override
    public String toString() {
        return "ComplexKeyMethodData{" +
                "name='" + name + '\'' +
                ", returnType='" + returnType + '\'' +
                ", parameters='" + parameters + '\'' +
                ", keyName='" + keyName + '\'' +
                ", keyClass='" + keyClass + '\'' +
                ", constructorParams='" + constructorParams + '\'' +
                '}';
    }
}
