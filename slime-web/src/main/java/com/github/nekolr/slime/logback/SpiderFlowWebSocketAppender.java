package com.github.nekolr.slime.logback;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.ThrowableProxy;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import com.github.nekolr.slime.context.SpiderContext;
import com.github.nekolr.slime.context.SpiderContextHolder;
import com.github.nekolr.slime.context.SpiderWebSocketContext;
import com.github.nekolr.slime.model.SpiderLog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SpiderFlowWebSocketAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {

    @Override
    protected void append(ILoggingEvent event) {
        SpiderContext context = SpiderContextHolder.get();
        if (context instanceof SpiderWebSocketContext) {
            SpiderWebSocketContext socketContext = (SpiderWebSocketContext) context;
            Object[] argumentArray = event.getArgumentArray();
            List<Object> arguments = argumentArray == null ? Collections.emptyList() : new ArrayList<>(Arrays.asList(argumentArray));
            ThrowableProxy throwableProxy = (ThrowableProxy) event.getThrowableProxy();
            if (throwableProxy != null) {
                arguments.add(throwableProxy.getThrowable());
            }
            // 通过 WebSocket 输出日志
            socketContext.log(new SpiderLog(event.getLevel().levelStr.toLowerCase(), event.getMessage(), arguments));
        }
    }
}
