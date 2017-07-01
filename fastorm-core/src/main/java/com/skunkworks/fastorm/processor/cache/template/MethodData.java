package com.skunkworks.fastorm.processor.cache.template;

import java.util.List;

/**
 * stole on 09.06.17.
 */
public class MethodData {
    private final String name;
    private final String returnType;
    private final List<TypeDeclaration> parameters;
    private final String keyName;
    private final String keyParameter;

    public MethodData(String name, String returnType, List<TypeDeclaration> parameters, String keyName, String keyParameter) {
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

    public List<TypeDeclaration> getParameters() {
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
