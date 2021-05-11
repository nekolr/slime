package com.github.nekolr.slime.executor.function.extension;

import org.apache.commons.lang3.StringUtils;
import com.github.nekolr.slime.annotation.Comment;
import com.github.nekolr.slime.annotation.Example;
import com.github.nekolr.slime.executor.FunctionExtension;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;


@Component
public class ListFunctionExtension implements FunctionExtension {

    @Override
    public Class<?> support() {
        return List.class;
    }

    @Comment("获取 list 的长度")
    @Example("${listVar.length()}")
    public static int length(List<?> list) {
        return list.size();
    }

    @Comment("将 list 拼接起来")
    @Example("${listVar.join()}")
    public static String join(List<?> list) {
        return StringUtils.join(list.toArray());
    }

    @Comment("将 list 用 separator 拼接起来")
    @Example("${listVar.join('-')}")
    public static String join(List<?> list, String separator) {
        if (list.size() == 1) {
            return list.get(0).toString();
        } else {
            return StringUtils.join(list.toArray(), separator);
        }
    }

    @Comment("将 list<String> 排序")
    @Example("${listVar.sort()}")
    public static List<String> sort(List<String> list) {
        Collections.sort(list);
        return list;
    }

    @Comment("将 list 打乱顺序")
    @Example("${listVar.shuffle()}")
    public static List<?> shuffle(List<?> list) {
        Collections.shuffle(list);
        return list;
    }

}
