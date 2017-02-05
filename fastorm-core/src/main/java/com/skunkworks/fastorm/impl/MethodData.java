package com.skunkworks.fastorm.impl;

/**
 * stole on 05.02.17.
 */
public class MethodData {
    private MethodType type = MethodType.UNRECOGNIZED;
    private String name;
    private String returnType = "void";
    private String parameters;

    public MethodType getType() {
        return type;
    }

    public void setType(MethodType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    public String getParameters() {
        return parameters;
    }
}
