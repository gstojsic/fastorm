package com.skunkworks.fastorm.processor.dao.template;

/**
 * stole on 27.01.17.
 */
public class FieldData {
    private final int index;
    private final String name;
    private final String type;
    private final String columnName;
    private final String getter;
    private final String setter;
    private final String recordsetType;
    private final boolean id;

    public FieldData(
            int index,
            String name,
            String type,
            String columnName,
            String getter,
            String setter,
            String recordsetType,
            boolean isId
    ) {
        this.index = index;
        this.name = name;
        this.type = type;
        this.columnName = columnName;
        this.getter = getter;
        this.setter = setter;
        this.recordsetType = recordsetType;
        this.id = isId;
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getGetter() {
        return getter;
    }

    public String getSetter() {
        return setter;
    }

    public String getRecordsetType() {
        return recordsetType;
    }

    public boolean isId() {
        return id;
    }

    @Override
    public String toString() {
        return "FieldData{" +
                "index=" + index +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", columnName='" + columnName + '\'' +
                ", getter='" + getter + '\'' +
                ", setter='" + setter + '\'' +
                ", recordsetType='" + recordsetType + '\'' +
                ", id=" + id +
                '}';
    }
}
