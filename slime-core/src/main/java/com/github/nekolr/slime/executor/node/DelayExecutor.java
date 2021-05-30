package com.github.nekolr.slime.executor.node;

import com.github.nekolr.slime.context.SpiderContext;
import com.github.nekolr.slime.executor.NodeExecutor;
import com.github.nekolr.slime.model.SpiderNode;
import com.github.nekolr.slime.support.ExpressionParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 延迟执行器
 */
@Component
@Slf4j
public class DelayExecutor implements NodeExecutor {

    /**
     * 延迟执行时间
     */
    private static final String DELAY_TIME = "delayTime";

    @Resource
    private ExpressionParser expressionParser;

    @Override
    public void execute(SpiderNode node, SpiderContext context, Map<String, Object> variables) {
        String delayTimes = node.getJsonProperty(DELAY_TIME);
        if (StringUtils.isNotBlank(delayTimes)) {
            try {
                Object value = expressionParser.parse(delayTimes, variables);
                Long times;
                if (value instanceof String) {
                    times = NumberUtils.toLong((String) value, 0L);
                } else if (value instanceof Integer) {
                    times = ((Integer) value).longValue();
                } else {
                    times = (Long) value;
                }
                if (times > 0) {
                    // 睡眠
                    try {
                        log.info("设置延迟执行时间：{} ms", times);
                        TimeUnit.MILLISECONDS.sleep(times);
                    } catch (Throwable t) {
                        log.error("设置延迟执行时间失败", t);
                    }
                }
            } catch (Exception e) {
                log.error("解析延迟执行时间：{} 失败", delayTimes, e);
            }
        }
    }

    @Override
    public String supportType() {
        return "delay";
    }
}
