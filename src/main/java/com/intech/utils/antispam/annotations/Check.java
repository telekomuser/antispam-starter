package com.intech.utils.antispam.annotations;

import com.intech.utils.antispam.models.CheckProperties;
import com.intech.utils.antispam.models.Strategy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.LOCAL_VARIABLE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Check {
    String queryType() default "";
    String variable() default "";
    CheckProperties properties() default CheckProperties.NONE;
    CheckProperties repeatProperties() default CheckProperties.NONE;

}
