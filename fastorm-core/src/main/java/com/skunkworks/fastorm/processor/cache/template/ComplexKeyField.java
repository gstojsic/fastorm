package com.skunkworks.fastorm.processor.cache.template;

/**
 * stole on 23.06.17.
 */
public class ComplexKeyField {
    private final String name;
    private final String type;

    public ComplexKeyField(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "ComplexKeyField{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
