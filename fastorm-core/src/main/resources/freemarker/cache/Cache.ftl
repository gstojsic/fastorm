package ${packageName};

import java.util.List;

public final class ${className} implements ${interfaceName} {
    <#list unrecognizedMethods as method>

    @Override
    public ${method.returnType} ${method.name}(${method.parameters}) {
        throw new UnsupportedOperationException("${method.name}");
    }
    </#list>
}