package com.intech.utils.antispam.annotations;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Афанасьев Евгений
 * @since 2021-09-07
 */
@Target({ElementType.METHOD, ElementType.LOCAL_VARIABLE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Check {
    /**
     * Название антиспам правила.
     * уникально для каждого аргумента проверяемого метода и каждой проверки
     */
    String queryType() default "";
    /**
     * Название аргумента передаваемого в методе,
     * который подвержен проверке
     */
    String variable() default "";

    /**
     * Настройки первичной проверки и
     * первичного попадания в блокировку
     */
    Settings properties() default @Settings;
    /**
     * Настройки для случая, если за последние 24 часа
     * пользователь попадал в блокировку
     */
    Settings repeatProperties() default @Settings;
}
