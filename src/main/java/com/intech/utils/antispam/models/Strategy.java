package com.intech.utils.antispam.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Strategy {
    NONE(CheckProperties.NONE, CheckProperties.NONE);

    private final CheckProperties properties;
    private final CheckProperties repeatProperties;
}
