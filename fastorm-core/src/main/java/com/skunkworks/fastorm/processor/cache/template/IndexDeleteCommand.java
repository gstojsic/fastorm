package com.skunkworks.fastorm.processor.cache.template;

/**
 * stole on 01.07.17.
 */
public class IndexDeleteCommand {
    private final String indexName;
    private final String entityGetter;

    public IndexDeleteCommand(String indexName, String entityGetter) {
        this.indexName = indexName;
        this.entityGetter = entityGetter;
    }

    public String getIndexName() {
        return indexName;
    }

    public String getEntityGetter() {
        return entityGetter;
    }

    @Override
    public String toString() {
        return "IndexDeleteCommand{" +
                "indexName='" + indexName + '\'' +
                ", entityGetter='" + entityGetter + '\'' +
                '}';
    }
}
