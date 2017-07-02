package com.skunkworks.fastorm.processor.springconfig;

import com.skunkworks.fastorm.annotations.Cache;
import com.skunkworks.fastorm.annotations.Dao;
import com.skunkworks.fastorm.annotations.GenerateFastOrmConfig;
import com.skunkworks.fastorm.processor.AbstractGenerator;
import com.skunkworks.fastorm.processor.dao.DaoGenerator;
import com.skunkworks.fastorm.processor.springconfig.template.CacheBean;
import com.skunkworks.fastorm.processor.springconfig.template.DaoBean;
import com.skunkworks.fastorm.processor.tool.Tools;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

/**
 * stole on 02.07.17.
 */
public class SpringConfigGenerator extends AbstractGenerator {

    private Set<String> additionalImports = new HashSet<>();

    public SpringConfigGenerator(ProcessingEnvironment processingEnv) {
        super(processingEnv);
    }

    public void generateSpringConfig(Element annotatedElement, RoundEnvironment roundEnvironment) throws Exception {
        String configName = GenerateFastOrmConfig.DEFAULT_CONFIG_NAME;

        AnnotationMirror generateFastOrmConfigAnnotation =
                Tools.findAnnotation(annotatedElement.getAnnotationMirrors(), GenerateFastOrmConfig.class);

        AnnotationValue generateFastOrmConfigValue = Tools.getAnnotationData(generateFastOrmConfigAnnotation, "value");
        if (generateFastOrmConfigValue != null)
            configName = generateFastOrmConfigValue.getValue().toString();

        //Find Daos
        Set<? extends Element> daoElements = roundEnvironment.getElementsAnnotatedWith(Dao.class);
        List<DaoBean> daoBeans = daoElements.stream().
                map(this::createDaoBeanData).
                collect(toList());

        Map<String, String> entityDaoClassMap = daoElements.stream().
                map(this::createDaoClass).
                filter(Objects::nonNull).
                collect(toMap(EntityDaoClassMapping::getEntityClass, EntityDaoClassMapping::getDaoClass));

        //Find Caches
        Set<? extends Element> cacheElements = roundEnvironment.getElementsAnnotatedWith(Cache.class);
        List<CacheBean> cacheBeans = cacheElements.stream().
                map(cacheElement -> createCacheBeanData(cacheElement, entityDaoClassMap)).
                filter(Objects::nonNull).
                collect(toList());

        Map<String, Object> context = new HashMap<>();
        context.put("className", configName);
        context.put("packageName", annotatedElement.getEnclosingElement().toString()); // nadamo se da je enclosing element anotirane spring boot app klase nekakav package
        context.put("additionalImports", additionalImports);

        context.put("daoBeans", daoBeans);
        context.put("cacheBeans", cacheBeans);

        write(configName, "springconfig/config.ftl", context);
    }

    private DaoBean createDaoBeanData(Element daoElement) {
        String interfaceName = daoElement.getSimpleName().toString();
        String name = Tools.lowercaseFirstLetter(interfaceName);
        String className = DaoGenerator.getGeneratedClassName(interfaceName);
        additionalImports.add(daoElement.getEnclosingElement().toString() + "." + className); // side effect
        return new DaoBean(name, className);
    }

    private CacheBean createCacheBeanData(Element cacheElement, Map<String, String> entityDaoClassMap) {
        String interfaceName = cacheElement.getSimpleName().toString();
        String name = Tools.lowercaseFirstLetter(interfaceName);
        String className = DaoGenerator.getGeneratedClassName(interfaceName);
        additionalImports.add(cacheElement.toString());
        additionalImports.add(cacheElement.getEnclosingElement().toString() + "." + className);

        AnnotationMirror cacheElementAnnotation =
                Tools.findAnnotation(cacheElement.getAnnotationMirrors(), Cache.class);

        AnnotationValue value = Tools.getAnnotationData(cacheElementAnnotation, "value");
        if (value == null)
            return null;
        String daoClass = entityDaoClassMap.get(value.getValue().toString());
        return new CacheBean(name, className, interfaceName, daoClass);
    }

    private EntityDaoClassMapping createDaoClass(Element daoElement) {
        AnnotationMirror daoElementAnnotation =
                Tools.findAnnotation(daoElement.getAnnotationMirrors(), Dao.class);
        AnnotationValue value = Tools.getAnnotationData(daoElementAnnotation, "value");
        if (value == null)
            return null;
        String interfaceName = daoElement.getSimpleName().toString();
        String className = DaoGenerator.getGeneratedClassName(interfaceName);
        return new EntityDaoClassMapping(value.getValue().toString(), className);
    }

    private static class EntityDaoClassMapping {
        private final String entityClass;
        private final String daoClass;

        EntityDaoClassMapping(String entityClass, String daoClass) {

            this.entityClass = entityClass;
            this.daoClass = daoClass;
        }

        String getEntityClass() {
            return entityClass;
        }

        String getDaoClass() {
            return daoClass;
        }
    }
}
