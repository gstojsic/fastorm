package com.skunkworks.fastorm.processor.cache.template;

import java.util.List;

/**
 * stole on 01.07.17.
 */
public class ComplexIndexUpdateCommand {
    private final String indexName;
    private final String keyClass;
    private final List<String> constructorParams;

    public ComplexIndexUpdateCommand(
            String indexName,
            String keyClass,
            List<String> constructorParams
    ) {
        this.indexName = indexName;
        this.keyClass = keyClass;
        this.constructorParams = constructorParams;
    }

    public String getIndexName() {
        return indexName;
    }

    public String getKeyClass() {
        return keyClass;
    }

    public List<String> getConstructorParams() {
        return constructorParams;
    }

    @Override
    public String toString() {
        return "ComplexIndexUpdateCommand{" +
                "indexName='" + indexName + '\'' +
                ", keyClass='" + keyClass + '\'' +
                ", constructorParams=" + constructorParams +
                '}';
    }
}
