package com.skunkworks.fastorm.processor.template;

/**
 * stole on 18.02.17.
 */
public class QueryParameter {
    private final int index;
    private final String methodParameterName;
    private final String queryParameterType;

    public QueryParameter(int index, String methodParameterName, String queryParameterType) {
        this.index = index;
        this.methodParameterName = methodParameterName;
        this.queryParameterType = queryParameterType;
    }

    public int getIndex() {
        return index;
    }

    public String getMethodParameterName() {
        return methodParameterName;
    }

    public String getQueryParameterType() {
        return queryParameterType;
    }
}
