package com.github.nekolr.slime.util;

import java.util.UUID;

/**
 * ID 生成器
 */
public class IdGenerator {

    /**
     * 获取随机 UUID
     *
     * @return
     */
    public static String randomUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}
