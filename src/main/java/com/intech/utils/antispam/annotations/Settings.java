package com.intech.utils.antispam.annotations;

import java.time.temporal.ChronoUnit;

/**
 * @author Афанасьев Евгений
 * @since 2021-09-07
 */
public @interface Settings {
     /**
      * за какой период учитывать {@link com.intech.utils.antispam.models.QueryLogEntity}
      */
     int blockPeriod() default 0;
     /**
      * Количество запросов разрешенных
      * за указанный промежуток времени
      */
     int blockCount() default 0;
     /**
      * Единица времени для {@link Settings#blockPeriod()}
      */
     ChronoUnit blockPeriodTimeUnit() default ChronoUnit.SECONDS;
     /**
      * Исключуние выбрасываемое в случае,
      * если пользователь находится в активной блокировке
      */
     Class<? extends RuntimeException> exception() default RuntimeException.class;
}
