package com.nala.sharding.Enum;

import lombok.Getter;

import java.util.UUID;

@Getter
public enum IdGeneratorTypeEnum {

    UUID("uuid"),
    SNOWFLAKE("雪花算法");

    private final String name;

    IdGeneratorTypeEnum(String name) {
        this.name = name;
    }

    public static String getName(String value) {
        for (IdGeneratorTypeEnum c : IdGeneratorTypeEnum.values()) {
            if (c.getName().equals(value)) {
                return c.name;
            }
        }
        return null;
    }
}
