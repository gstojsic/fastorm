package com.skunkworks.fastorm.processor.cache.template;

/**
 * stole on 09.06.17.
 */
public class FieldData {
    private final String name;
    private final String type;
    private final String getter;
    private final String setter;
    private final boolean id;
    private final boolean primitive;

    public FieldData(String name, String type, String getter, String setter, boolean isId, boolean isPrimitive) {
        this.name = name;
        this.type = type;
        this.getter = getter;
        this.setter = setter;
        this.id = isId;
        primitive = isPrimitive;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getGetter() {
        return getter;
    }

    public String getSetter() {
        return setter;
    }

    public boolean isId() {
        return id;
    }

    public boolean isPrimitive() {
        return primitive;
    }

    @Override
    public String toString() {
        return "FieldData{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", getter='" + getter + '\'' +
                ", setter='" + setter + '\'' +
                ", id=" + id +
                ", primitive=" + primitive +
                '}';
    }
}
