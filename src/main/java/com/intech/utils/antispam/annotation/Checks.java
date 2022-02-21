package com.intech.utils.antispam.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Афанасьев Евгений
 * @since 2021-09-07
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Checks {

    /**
     * Набор проверок антиспама в случае,
     * если для 1 метогда нужно больше 1 проверки
     */
    Check[] checks();
}
