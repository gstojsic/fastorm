<#macro funcParams parameters><#list parameters as param>${param.type} ${param.name}<#sep>, </#list></#macro>

<#macro paramList parameters><#list parameters as param>${param}<#sep>, </#list></#macro>