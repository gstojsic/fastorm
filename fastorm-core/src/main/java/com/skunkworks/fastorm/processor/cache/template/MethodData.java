package com.skunkworks.fastorm.processor.cache.template;

/**
 * stole on 09.06.17.
 */
public class MethodData {
    private String name;
    private String returnType = "void";
    private String parameters;
    private MethodType type;

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

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    public MethodType getType() {
        return type;
    }

    public void setType(MethodType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "MethodData{" +
                "name='" + name + '\'' +
                ", returnType='" + returnType + '\'' +
                ", parameters='" + parameters + '\'' +
                ", type=" + type +
                '}';
    }
}
