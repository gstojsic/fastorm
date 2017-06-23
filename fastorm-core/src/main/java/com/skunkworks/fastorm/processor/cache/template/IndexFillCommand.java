package com.skunkworks.fastorm.processor.cache.template;

/**
 * stole on 23.06.17.
 */
public class IndexFillCommand {
    private final String indexName;
    private final String entityGetter;

    public IndexFillCommand(String indexName, String entityGetter) {
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
        return "IndexFillCommand{" +
                "indexName='" + indexName + '\'' +
                ", entityGetter='" + entityGetter + '\'' +
                '}';
    }
}
