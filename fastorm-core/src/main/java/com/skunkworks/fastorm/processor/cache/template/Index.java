package com.skunkworks.fastorm.processor.cache.template;

/**
 * stole on 22.06.17.
 */
public class Index {
    private final String name;
    private final String keyType;
    private final String valueType;

    public Index(String name, String keyType, String valueType) {
        this.name = name;
        this.keyType = keyType;
        this.valueType = valueType;
    }

    public String getName() {
        return name;
    }

    public String getKeyType() {
        return keyType;
    }

    public String getValueType() {
        return valueType;
    }

    @Override
    public String toString() {
        return "Index{" +
                "name='" + name + '\'' +
                ", keyType='" + keyType + '\'' +
                ", valueType='" + valueType + '\'' +
                '}';
    }
}

