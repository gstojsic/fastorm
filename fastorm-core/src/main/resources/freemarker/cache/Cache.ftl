package ${packageName};

import com.skunkworks.fastorm.Dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
<#list additionalImports as import>
${import};
</#list>

public class ${className} implements ${interfaceName} {

    protected final Dao<${entityName}, ${idField.type}> dao;

    <#list uniqueIndexes as index>
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
        //firstNameIndex.put(entity.getFirstName(), entity);
        </#list>
        }
    }
    <#list queryMethods as method>

    @Override
    public ${method.returnType} ${method.name}(${method.parameters}) {
        return ${method.keyName}Index.get(${method.keyParameter});
    }
    </#list>
    <#list queryListMethods as method>

    @Override
    public ${method.returnType} ${method.name}(${method.parameters}) {
        return ${method.keyName}Index.get(${method.keyParameter});
    }
    </#list>
    <#list unrecognizedMethods as method>

    @Override
    public ${method.returnType} ${method.name}(${method.parameters}) {
        throw new UnsupportedOperationException("${method.name}");
    }
    </#list>
}