package com.skunkworks.fastorm.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * TODO:implement
 *
 * This annotation used on a method on a @Dao interface will generate code to invoke a stored procedure.
 *
 * stole on 18.02.17.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface StoredProcedure {
}
