package ${packageName};

import com.skunkworks.fastorm.Dao;

import javax.sql.DataSource;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

<#list additionalImports as import>
import ${import};
</#list>

public class ${className} implements Dao<${entityName}, ${idField.type}>, ${interfaceName} {

    private final DataSource dataSource;

    public ${className}(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public ${entityName} save(${entityName} entity) {
        return null;
    }

    @Override
    public List<${entityName}> save(Iterable<${entityName}> entities) {
        return null;
    }

    @Override
    public ${entityName} findOne(${idField.type} id) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("select ${selectColumns} from ${entityName} where ${idField.columnName} = ?");
            preparedStatement.set${idField.recordsetType}(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return mapToRow(resultSet);
            } else
                return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean exists(${idField.type} id) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("select 1 from ${entityName} where ${idField.columnName} = ?");
            preparedStatement.set${idField.recordsetType}(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<${entityName}> findAll() {
        try (Connection connection = dataSource.getConnection()) {
            CallableStatement callableStatement = connection.prepareCall("select ${selectColumns} from ${entityName}");
            ResultSet resultSet = callableStatement.executeQuery();
            List<${entityName}> items = new ArrayList<>();
            while (resultSet.next()) {
                items.add(mapToRow(resultSet));
            }
            return items;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<${entityName}> findAll(Iterable<${idField.type}> ids) {
        return null;
    }

    @Override
    public long count() {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("select count(*) from ${entityName}");
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getLong(1);
            } else
                return 0L;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(${idField.type} id) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("delete from ${entityName} where ${idField.columnName} = ?");
            preparedStatement.set${idField.recordsetType}(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(${entityName} entity) {
        if (entity.${idField.getter}() != null) {
            delete(entity.${idField.getter}());
        }
    }

    @Override
    public void delete(Iterable<${entityName}> entities) {

    }

    @Override
    public void deleteAll() {
        try (Connection connection = dataSource.getConnection()) {
            CallableStatement callableStatement = connection.prepareCall("delete from ${entityName}");
            callableStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //${selectColumns}
    public static ${entityName} mapToRow(ResultSet resultSet) throws SQLException {
        ${entityName} item = new ${entityName}();
        <#list fields as field>
        item.${field.setter}(resultSet.get${field.recordsetType}(${field.index}));
        </#list>

        return item;
    }

    <#list queryMethods as method>

    @Override
    public ${method.returnType} ${method.name}(${method.parameters}) {
        throw new UnsupportedOperationException("${method.name}");
    }
    </#list>
    <#list queryListMethods as method>

    @Override
    public ${method.returnType} ${method.name}(${method.parameters}) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("select ${selectColumns} from ${entityName} where ${method.query}");
            <#list method.queryParameters as qParam>
            preparedStatement.set${qParam.queryParameterType}(${qParam.index}, ${qParam.methodParameterName});
            </#list>

            ResultSet resultSet = preparedStatement.executeQuery();
            List<${entityName}> items = new ArrayList<>();
            while (resultSet.next()) {
                items.add(mapToRow(resultSet));
            }
            return items;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    </#list>
    <#list storedProcedureMethods as method>

    @Override
    public ${method.returnType} ${method.name}(${method.parameters}) {
        throw new UnsupportedOperationException("${method.name}");
    }
    </#list>
    <#list unrecognizedMethods as method>

    @Override
    public ${method.returnType} ${method.name}(${method.parameters}) {
        throw new UnsupportedOperationException("${method.name}");
    }
    </#list>

}