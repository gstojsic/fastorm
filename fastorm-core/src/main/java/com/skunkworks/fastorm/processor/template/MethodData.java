package com.skunkworks.fastorm.processor.template;

import java.util.List;

/**
 * stole on 05.02.17.
 */
public class MethodData {
    private MethodType type = MethodType.UNRECOGNIZED;
    private String name;
    private String returnType = "void";
    private String parameters;
    private String query;
    private List<QueryParameter> queryParameters;

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

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public List<QueryParameter> getQueryParameters() {
        return queryParameters;
    }

    public void setQueryParameters(List<QueryParameter> queryParameters) {
        this.queryParameters = queryParameters;
    }
}
