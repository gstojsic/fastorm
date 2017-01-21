package com.skunkworks.serialization.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * stole on 11.12.16.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface JsonSerializable {
}
