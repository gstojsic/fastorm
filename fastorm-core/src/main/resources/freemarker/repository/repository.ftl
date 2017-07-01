package ${packageName};

import com.skunkworks.fastorm.Repository;

import javax.sql.DataSource;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class ${className} implements Repository<${entityName}> {

    private final DataSource dataSource;

    public ${className}(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<${entityName}> loadAll() throws Exception {
        Connection connection = dataSource.getConnection();
        CallableStatement callableStatement = connection.prepareCall("select ${selectColumns} from ${entityName}");
        ResultSet resultSet = callableStatement.executeQuery();
        List<${entityName}> items = new ArrayList<>();
        while (resultSet.next()) {
            items.add(mapToRow(resultSet));
        }
        return items;
    }

    //${selectColumns}
    public static ${entityName} mapToRow(ResultSet resultSet) throws Exception {
        ${entityName} item = new ${entityName}();
        <#list fields as field>
        item.${field.setter}(resultSet.get${field.recordsetType}(${field.index}));
        </#list>
        return item;
    }
}