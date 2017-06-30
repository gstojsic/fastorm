package com.skunkworks.fastorm.processor.cache;

import com.skunkworks.fastorm.processor.cache.template.MethodType;

import java.util.List;
import java.util.Map;

/**
 * stole on 23.06.17.
 */
public class MethodAnalysisData {
    private String name;
    private String returnType = "void";
    private Map<String, String> parameters;
    private MethodType type = MethodType.UNRECOGNIZED;
    private List<String> parameterNames;
    private List<String> keyComponents;

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

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public MethodType getType() {
        return type;
    }

    public void setType(MethodType type) {
        this.type = type;
    }

    public List<String> getParameterNames() {
        return parameterNames;
    }

    public void setParameterNames(List<String> parameterNames) {
        this.parameterNames = parameterNames;
    }

    public List<String> getKeyComponents() {
        return keyComponents;
    }

    public void setKeyComponents(List<String> keyComponents) {
        this.keyComponents = keyComponents;
    }

    @Override
    public String toString() {
        return "MethodAnalysisData{" +
                "name='" + name + '\'' +
                ", returnType='" + returnType + '\'' +
                ", parameters=" + parameters +
                ", type=" + type +
                ", parameterNames=" + parameterNames +
                ", keyComponents=" + keyComponents +
                '}';
    }
}
