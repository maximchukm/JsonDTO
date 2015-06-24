package com.maximchuk.json.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Maxim Maximchuk on 24.06.2015.
 */
@Target(value = ElementType.FIELD)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface JsonEnum {
    JsonEnumType value() default JsonEnumType.STRING;
}
