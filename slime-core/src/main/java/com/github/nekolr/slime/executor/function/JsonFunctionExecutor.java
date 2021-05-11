package com.github.nekolr.slime.executor.function;

import com.alibaba.fastjson.JSON;
import com.github.nekolr.slime.annotation.Comment;
import com.github.nekolr.slime.annotation.Example;
import com.github.nekolr.slime.executor.FunctionExecutor;
import org.springframework.stereotype.Component;

/**
 * Json 和 String 互相转换
 */
@Component
@Comment("json 常用方法")
public class JsonFunctionExecutor implements FunctionExecutor {

    @Override
    public String getFunctionPrefix() {
        return "json";
    }

    @Comment("将字符串转为 json 对象")
    @Example("${json.parse('{code : 1}')}")
    public static Object parse(String jsonString) {
        return jsonString != null ? JSON.parse(jsonString) : null;
    }

    @Comment("将对象转为 json 字符串")
    @Example("${json.stringify(objVar)}")
    public static String stringify(Object object) {
        return object != null ? JSON.toJSONString(object) : null;
    }
}
