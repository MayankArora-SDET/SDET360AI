package com.sdet.sdet360.config.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark UUID parameters that should be handled safely
 * when they are null or invalid
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface SafeUUID {
    /**
     * Whether to allow null values
     */
    boolean allowNull() default true;
    
    /**
     * Default UUID value to use if the input is null or invalid
     * Empty string means no default value
     */
    String defaultValue() default "";
}
