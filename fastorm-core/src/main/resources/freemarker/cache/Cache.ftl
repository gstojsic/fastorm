package ${packageName};

import com.skunkworks.fastorm.Dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
<#list additionalImports as import>
${import};
</#list>

public class ${className} implements ${interfaceName} {

    private final Dao<${entityName}, ${idField.type}> dao;
    //private Map<${idField.type}, ${entityName}> entitiesById = new HashMap<>();

    public ${className}(Dao<${entityName}, ${idField.type}> dao) {
        this.dao = dao;
        loadData();
    }

    private void loadData() {
        List<${entityName}> entities = dao.findAll();
        for (${entityName} entity : entities) {
        }
    }
    <#list unrecognizedMethods as method>

    @Override
    public ${method.returnType} ${method.name}(${method.parameters}) {
        throw new UnsupportedOperationException("${method.name}");
    }
    </#list>
}