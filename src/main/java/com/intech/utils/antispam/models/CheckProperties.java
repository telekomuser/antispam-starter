package com.intech.utils.antispam.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.temporal.ChronoUnit;
import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum CheckProperties {

    NONE(0, 0, 0, ChronoUnit.SECONDS, RuntimeException.class);

    private final int blockTime;
    private final int blockPeriod;
    private final int blockCount;
    private final ChronoUnit blockPeriodTimeUnit;
    private final Class<? extends RuntimeException> extension;

}
