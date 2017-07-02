package com.skunkworks.fastorm.processor.springconfig.template;

/**
 * stole on 02.07.17.
 */
public class DaoBean {
    private final String name;
    private final String className;

    public DaoBean(String name, String className) {
        this.name = name;
        this.className = className;
    }

    public String getName() {
        return name;
    }

    public String getClassName() {
        return className;
    }

    @Override
    public String toString() {
        return "DaoBean{" +
                "name='" + name + '\'' +
                ", className='" + className + '\'' +
                '}';
    }
}
