package com.skunkworks.fastorm.processor.cache.template;

/**
 * stole on 01.07.17.
 */
public class TypeDeclaration {
    private final String name;
    private final String type;

    public TypeDeclaration(String name, String type) {
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
        return "TypeDeclaration{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
