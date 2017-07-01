package com.skunkworks.fastorm.processor.cache.template;

/**
 * stole on 01.07.17.
 */
public class IndexUpdateCommand {
    private final String indexName;
    private final String entityGetter;

    public IndexUpdateCommand(String indexName, String entityGetter) {
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
        return "IndexUpdateCommand{" +
                "indexName='" + indexName + '\'' +
                ", entityGetter='" + entityGetter + '\'' +
                '}';
    }
}
