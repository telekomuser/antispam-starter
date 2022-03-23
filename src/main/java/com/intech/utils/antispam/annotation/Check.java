package com.intech.utils.antispam.annotation;


import java.lang.annotation.*;

/**
 * @author Афанасьев Евгений
 * @since 2021-09-07
 */
@Target(ElementType.METHOD)
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

    String profile() default "";

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
