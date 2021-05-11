package com.github.nekolr.slime.executor.function;

import com.github.nekolr.slime.annotation.Comment;
import com.github.nekolr.slime.annotation.Example;
import com.github.nekolr.slime.executor.FunctionExecutor;
import org.springframework.stereotype.Component;

@Component
@Comment("thread 常用方法")
public class ThreadFunctionExecutor implements FunctionExecutor {

    @Override
    public String getFunctionPrefix() {
        return "thread";
    }

    @Comment("线程休眠")
    @Example("${thread.sleep(1000L)}")
    public static void sleep(Long sleepTime) {
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
