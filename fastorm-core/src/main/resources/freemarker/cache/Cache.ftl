package ${packageName};

import com.skunkworks.fastorm.Dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
<#list additionalImports as import>
import ${import};
</#list>

public class ${className} implements ${interfaceName} {

    protected final Dao<${entityName}, ${idField.type}> dao;

    <#list indexes as index>
    protected final Map<${index.keyType}, ${index.valueType}> ${index.name}Index = new HashMap<>();
    </#list>

    public ${className}(Dao<${entityName}, ${idField.type}> dao) {
        this.dao = dao;
        loadData();
    }

    private void loadData() {
        List<${entityName}> entities = dao.findAll();
        for (${entityName} entity : entities) {
            <#list indexFillCommands as command>
            ${command.indexName}Index.put(entity.${command.entityGetter}(), entity);
            </#list>
            <#list listIndexFillCommands as command>
            ${command.indexName}Index.computeIfAbsent(entity.${command.entityGetter}(), s -> new ArrayList<>()).add(entity);
            </#list>
            <#list indexComplexFillCommands as command>
            ${command.indexName}Index.put(new ${command.keyClass}(<#list command.constructorParams as param>entity.${param}()<#sep>, </#list>), entity);
            </#list>
        }
    }

    private void updateIndexes(${entityName} entity) {
        <#list indexUpdateCommands as command>
        //
        </#list>
    }

    private void deleteFromIndexes(${entityName} entity) {
        <#list indexDeleteCommands as command>
        //
        </#list>
    }
    <#list queryMethods as method>

    @Override
    public ${method.returnType} ${method.name}(<#list method.parameters as name, type>${type} ${name}<#sep>, </#list>) {
        return ${method.keyName}Index.get(${method.keyParameter});
    }
    </#list>
    <#list complexKeyQueryMethods as method>

    @Override
    public ${method.returnType} ${method.name}(<#list method.parameters as name, type>${type} ${name}<#sep>, </#list>) {
        return ${method.keyName}Index.get(new ${method.keyClass}(<#list method.constructorParams as param>${param}<#sep>, </#list>));
    }
    </#list>
    <#list unrecognizedMethods as method>

    @Override
    public ${method.returnType} ${method.name}(<#list method.parameters as name, type>${type} ${name}<#sep>, </#list>) {
        throw new UnsupportedOperationException("${method.name}");
    }
    </#list>
    <#list complexKeyClasses as keyClass>

    private static final class ${keyClass.className} {
        <#list keyClass.fields as name, type>
        private final ${type} ${name};
        </#list>

        ${keyClass.className}(<#list keyClass.constructorParams as name, type>${type} ${name}<#sep>, </#list>) {
            <#list keyClass.constructorInitializers as initializer>
            this.${initializer} = ${initializer};
            </#list>
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ${keyClass.className} that = (${keyClass.className}) o;

            <#list keyClass.primitiveEquals as field>
            if (${field} != that.${field}) return false;
            </#list>

            <#list keyClass.nonprimitiveEquals as field>
            if (${field} != null ? !${field}.equals(that.${field}) : that.${field} != null) return false;
            </#list>

            return true;
        }

        @Override
        public int hashCode() {
            return Objects.hash(<#list keyClass.hashParams as param>${param}<#sep>, </#list>);
        }
    }
    </#list>
}