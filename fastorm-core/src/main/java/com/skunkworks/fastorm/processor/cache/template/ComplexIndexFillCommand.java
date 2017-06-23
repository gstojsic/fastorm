package com.skunkworks.fastorm.processor.cache.template;

/**
 * stole on 23.06.17.
 */
public class ComplexIndexFillCommand {
    private final String indexName;
    private final String keyClass;
    private final String constructorParams;

    public ComplexIndexFillCommand(
            String indexName,
            String keyClass,
            String constructorParams
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

    public String getConstructorParams() {
        return constructorParams;
    }
}
