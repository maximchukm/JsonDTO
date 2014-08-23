package com.maximchuk.json.annotation.rest;

import com.maximchuk.json.JsonDTO;

import javax.ws.rs.Produces;
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
@Produces("application/json")
public @interface JsonProduces {
    Class<? extends JsonDTO>[] value() default {};
    String description() default "";
}
