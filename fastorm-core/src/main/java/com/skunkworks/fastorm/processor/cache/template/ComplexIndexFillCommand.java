package com.skunkworks.fastorm.processor.cache.template;

/**
 * stole on 23.06.17.
 */
public class ComplexIndexFillCommand {
    private final String indexName;
    private final String keyClass;

    public ComplexIndexFillCommand(String indexName, String keyClass) {
        this.indexName = indexName;
        this.keyClass = keyClass;
    }

    public String getIndexName() {
        return indexName;
    }

    public String getKeyClass() {
        return keyClass;
    }
}
