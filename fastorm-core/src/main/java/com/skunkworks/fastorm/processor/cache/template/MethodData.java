package com.skunkworks.fastorm.processor.cache.template;

import java.util.Map;

/**
 * stole on 09.06.17.
 */
public class MethodData {
    private final String name;
    private final String returnType;
    private final Map<String, String> parameters;
    private final String keyName;
    private final String keyParameter;

    public MethodData(String name, String returnType, Map<String, String> parameters, String keyName, String keyParameter) {
        this.name = name;
        this.returnType = returnType;
        this.parameters = parameters;
        this.keyName = keyName;
        this.keyParameter = keyParameter;
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

    public String getKeyParameter() {
        return keyParameter;
    }

    @Override
    public String toString() {
        return "MethodData{" +
                "name='" + name + '\'' +
                ", returnType='" + returnType + '\'' +
                ", parameters='" + parameters + '\'' +
                ", keyName='" + keyName + '\'' +
                ", keyParameter='" + keyParameter + '\'' +
                '}';
    }
}
