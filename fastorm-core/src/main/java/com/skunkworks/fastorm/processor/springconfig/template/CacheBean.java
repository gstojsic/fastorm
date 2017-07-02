package com.skunkworks.fastorm.processor.springconfig.template;

/**
 * stole on 02.07.17.
 */
public class CacheBean {
    private final String name;
    private final String className;
    private final String interfaceName;
    private final String daoClass;

    public CacheBean(String name, String className, String interfaceName, String daoClass) {
        this.name = name;
        this.className = className;
        this.interfaceName = interfaceName;
        this.daoClass = daoClass;
    }

    public String getName() {
        return name;
    }

    public String getClassName() {
        return className;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public String getDaoClass() {
        return daoClass;
    }

    @Override
    public String toString() {
        return "CacheBean{" +
                "name='" + name + '\'' +
                ", className='" + className + '\'' +
                ", interfaceName='" + interfaceName + '\'' +
                ", daoClass='" + daoClass + '\'' +
                '}';
    }
}
