package com.github.nekolr.slime.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Plugin {

    /**
     * 插件名称
     */
    private String name;

    /**
     * 插件地址
     */
    private String url;
}
