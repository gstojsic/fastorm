package com.skunkworks.fastorm.processor.cache;

import com.skunkworks.fastorm.processor.cache.template.MethodType;
import com.skunkworks.fastorm.processor.cache.template.TypeDeclaration;

import java.util.List;

/**
 * stole on 23.06.17.
 */
public class MethodAnalysisData {
    private String name;
    private String returnType = "void";
    private List<TypeDeclaration> parameters;
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

    public List<TypeDeclaration> getParameters() {
        return parameters;
    }

    public void setParameters(List<TypeDeclaration> parameters) {
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
