package com.maximchuk.json.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Maxim L. Maximcuhk
 *         Date: 22.07.13
 */
@Deprecated
@Target(value = ElementType.FIELD)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface JsonDateParam {
    String pattern();
}
