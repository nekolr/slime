package com.github.nekolr.slime.model;

import lombok.Getter;
import lombok.Setter;


/**
 * 图形
 */
@Getter
@Setter
public class Shape {

    /**
     * 图形名称
     */
    private String name;

    /**
     * 图形标签
     */
    private String label;

    /**
     * 图形标题
     */
    private String title;

    /**
     * 图形的链接地址（可以是 BASE64 编码后的图片）
     */
    private String image;

    /**
     * 描述
     */
    private String desc;
}
