package com.nala.sharding.Enum;

import lombok.Getter;

import java.util.stream.Stream;

@Getter
public enum StrategyEnum {

    A("A"),
    B("B"),
    C("C"),
    D("D");

    private final String name;

    StrategyEnum(String name) {
        this.name = name;
    }

    public static StrategyEnum of(String strategy) throws Exception {

        return Stream.of(values()).filter(bean -> bean.name().equals(strategy))
                .findAny()
                .orElseThrow(() -> new Exception("不存在!"));

    }
}
