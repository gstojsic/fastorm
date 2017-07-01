package com.skunkworks.fastorm.processor.repository.template;

/**
 * stole on 01.07.17.
 */
public class FieldData {
    private final int index;
    private final String name;
    private final String columnName;
    private final String getter;
    private final String setter;
    private final String recordsetType;

    public FieldData(
            int index,
            String name,
            String columnName,
            String getter,
            String setter,
            String recordsetType
    ) {
        this.index = index;
        this.name = name;
        this.columnName = columnName;
        this.getter = getter;
        this.setter = setter;
        this.recordsetType = recordsetType;
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
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

    @Override
    public String toString() {
        return "FieldData{" +
                "index=" + index +
                ", name='" + name + '\'' +
                ", columnName='" + columnName + '\'' +
                ", getter='" + getter + '\'' +
                ", setter='" + setter + '\'' +
                ", recordsetType='" + recordsetType + '\'' +
                '}';
    }
}
