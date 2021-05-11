package com.github.nekolr.slime.executor.function.extension;

import com.github.nekolr.slime.annotation.Comment;
import com.github.nekolr.slime.annotation.Example;
import com.github.nekolr.slime.executor.FunctionExtension;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class MapFunctionExtension implements FunctionExtension {

    @Override
    public Class<?> support() {
        return Map.class;
    }

    @Comment("将 map 转换为 List")
    @Example("${mapVar.toList('=')}")
    public static List<String> toList(Map<?, ?> map, String separator) {
        return map.entrySet().stream().map(entry -> entry.getKey() + separator + entry.getValue()).collect(Collectors.toList());
    }
}
