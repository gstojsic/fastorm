package com.skunkworks.fastorm.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Generates the Spring Config file for all generated classes so You don't have to ;)
 * <p>
 * stole on 02.07.17.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface GenerateFastOrmConfig {
    String DEFAULT_CONFIG_NAME = "FastOrmConfig";

    String value() default DEFAULT_CONFIG_NAME;
}