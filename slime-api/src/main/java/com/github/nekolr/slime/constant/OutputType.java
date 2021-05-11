package com.github.nekolr.slime.constant;

import lombok.Getter;

/**
 * 输出类型
 */
public enum OutputType {

    DATABASE("output-database"),
    CSV("output-csv");

    @Getter
    private String variableName;

    OutputType(String variableName) {
        this.variableName = variableName;
    }
}
