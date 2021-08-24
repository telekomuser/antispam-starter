package com.intech.utils.antispam.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum AvailableType {
    MSISDN("msisdn"),
    HASH("hash");

    private final String name;

    public static AvailableType findByName(String name) {
        return Arrays.stream(values())
                .filter(value -> value.name.equals(name))
                .findFirst()
                .orElse(null);
    }
}
