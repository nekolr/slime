package com.github.nekolr.slime.executor.function.extension;

import org.apache.commons.lang3.time.DateFormatUtils;
import com.github.nekolr.slime.annotation.Comment;
import com.github.nekolr.slime.annotation.Example;
import com.github.nekolr.slime.executor.FunctionExtension;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class DateFunctionExtension implements FunctionExtension {

    @Override
    public Class<?> support() {
        return Date.class;
    }

    @Comment("格式化日期")
    @Example("${dateVar.format()}")
    public static String format(Date date) {
        return format(date, "yyyy-MM-dd HH:mm:ss");
    }

    @Comment("格式化日期")
    @Example("${dateVar.format('yyyy-MM-dd HH:mm:ss')}")
    public static String format(Date date, String pattern) {
        return DateFormatUtils.format(date, pattern);
    }
}
