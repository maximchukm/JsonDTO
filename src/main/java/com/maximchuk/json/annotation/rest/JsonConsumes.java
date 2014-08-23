package com.maximchuk.json.annotation.rest;

import com.maximchuk.json.JsonDTO;

import javax.ws.rs.Consumes;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Maxim Maximchuk
 *         date 22.08.2014.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Consumes("application/json")
public @interface JsonConsumes {
    Class<? extends JsonDTO>[] value() default {};
    String description() default "";
}
