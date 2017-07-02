package ${packageName};

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

<#list additionalImports as import>
import ${import};
</#list>

@Configuration
public class ${className} {

    <#list daoBeans as bean>
    @Bean
    ${bean.className} ${bean.name}(DataSource dataSource) {
        return new ${bean.className}(dataSource);
    }

    </#list>
    <#list cacheBeans as bean>
    @Bean
    ${bean.interfaceName} ${bean.name}(${bean.daoClass} dao) {
        return new ${bean.className}(dao);
    }

    </#list>
}
