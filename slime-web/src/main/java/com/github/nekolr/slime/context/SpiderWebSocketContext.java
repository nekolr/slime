package com.github.nekolr.slime.context;

import com.alibaba.fastjson.JSON;
import com.github.nekolr.slime.model.SpiderLog;
import com.github.nekolr.slime.model.SpiderOutput;
import com.github.nekolr.slime.websocket.WebSocketEvent;
import com.github.nekolr.slime.serializer.FastJsonSerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Date;

/**
 * WebSocket 通信时使用的爬虫上下文
 */
public class SpiderWebSocketContext extends SpiderContext {

    @Getter
    @Setter
    private boolean debug;

    private WebSocketSession session;

    private Object lock = new Object();


    public SpiderWebSocketContext(WebSocketSession session) {
        this.session = session;
    }

    @Override
    public void addOutput(SpiderOutput output) {
        this.write(new WebSocketEvent<>(WebSocketEvent.OUTPUT_EVENT_TYPE, output));
    }

    public void log(SpiderLog log) {
        write(new WebSocketEvent<>(WebSocketEvent.LOG_EVENT_TYPE,
                DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss.SSS"), log));
    }

    public <T> void write(WebSocketEvent<T> event) {
        try {
            String message = JSON.toJSONString(event, FastJsonSerializer.serializeConfig);
            if (session.isOpen()) {
                synchronized (session) {
                    session.sendMessage(new TextMessage(message));
                }
            }
        } catch (Throwable ignored) {
        }
    }

    @Override
    public void pause(String nodeId, String event, String key, Object value) {
        if (this.debug && this.isRunning()) {
            synchronized (this) {
                if (this.debug && this.isRunning()) {
                    synchronized (lock) {
                        try {
                            // 向客户端发送消息
                            write(new WebSocketEvent<>(WebSocketEvent.DEBUG_EVENT_TYPE,
                                    new DebugInfo(nodeId, event, key, value)));
                            lock.wait();
                        } catch (InterruptedException ignored) {
                        }
                    }
                }
            }
        }
    }

    @Override
    public void resume() {
        if (this.debug) {
            synchronized (lock) {
                lock.notify();
            }
        }
    }

    @Override
    public void stop() {
        if (this.debug) {
            synchronized (lock) {
                lock.notifyAll();
            }
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    class DebugInfo {
        /**
         * 节点 ID
         */
        private String nodeId;

        /**
         * 事件名称
         */
        private String event;

        /**
         * 属性名称（在调试时，客户端需要知道当前断点所在的属性的名称）
         */
        private String key;

        /**
         * 属性值（在调试时，客户端需要知道当前断点所在的属性的值，该值由表达式解析器计算得出）
         */
        private Object value;
    }
}
