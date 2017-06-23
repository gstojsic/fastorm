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
            ${command.indexName}Index.put(new ${command.keyClass}(${command.constructorParams}), entity);
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
    public ${method.returnType} ${method.name}(${method.parameters}) {
        return ${method.keyName}Index.get(${method.keyParameter});
    }
    </#list>
    <#list complexKeyQueryMethods as method>

    @Override
    public ${method.returnType} ${method.name}(${method.parameters}) {
        return ${method.keyName}Index.get(new ${method.keyClass}(${method.constructorParams}));
    }
    </#list>
    <#list unrecognizedMethods as method>

    @Override
    public ${method.returnType} ${method.name}(${method.parameters}) {
        throw new UnsupportedOperationException("${method.name}");
    }
    </#list>
    <#list complexKeyClasses as keyClass>

    private static final class ${keyClass.className} {
        <#list keyClass.fields as field>
        private final ${field.type} ${field.name};
        </#list>

        ${keyClass.className}(${keyClass.constructorParams}) {
            <#list keyClass.constructorInitializers as initializer>
            this.${initializer} = ${initializer};
            </#list>
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            //FirstNameAndLastNameKey that = (FirstNameAndLastNameKey) o;

            //if (firstName != null ? !firstName.equals(that.firstName) : that.firstName != null) return false;
            //if (lastName != null ? !lastName.equals(that.lastName) : that.lastName != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = firstName != null ? firstName.hashCode() : 0;
            result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
            return result;
        }
    }
    </#list>
}