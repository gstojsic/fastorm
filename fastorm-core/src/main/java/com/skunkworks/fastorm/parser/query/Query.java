package com.skunkworks.fastorm.parser.query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * stole on 04.02.17.
 */
public class Query {
    private ArrayList<String> queryParams = new ArrayList<>();
    private ArrayList<String> queryOperators = new ArrayList<>();
    private String orderByParam;

    public List<String> getQueryParams() {
        return Collections.unmodifiableList(queryParams);
    }

    public void addQueryParam(String param) {
        queryParams.add(param);
    }

    public List<String> getQueryOperators() {
        return Collections.unmodifiableList(queryOperators);
    }

    public void addQueryOperator(String param) {
        queryOperators.add(param);
    }

    public String getOrderByParam() {
        return orderByParam;
    }

    public void setOrderByParam(String orderByParam) {
        this.orderByParam = orderByParam;
    }
}
