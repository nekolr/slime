package com.github.nekolr.slime.executor.node;

import com.github.nekolr.slime.constant.Constants;
import com.github.nekolr.slime.model.SpiderNode;
import com.github.nekolr.slime.support.ExpressionParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import com.github.nekolr.slime.context.SpiderContext;
import com.github.nekolr.slime.executor.NodeExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 函数执行器
 */
@Component
@Slf4j
public class FunctionExecutor implements NodeExecutor {

    @Resource
    private ExpressionParser expressionParser;

    @Override
    public void execute(SpiderNode node, SpiderContext context, Map<String, Object> variables) {
        List<Map<String, String>> functions = node.getJsonArrayProperty(Constants.FUNCTION);
        for (Map<String, String> item : functions) {
            String function = item.get(Constants.FUNCTION);
            if (StringUtils.isNotBlank(function)) {
                try {
                    log.debug("执行函数 {}", function);
                    expressionParser.parse(function, variables);
                } catch (Exception e) {
                    log.error("执行函数 {} 失败", function, e);
                    ExceptionUtils.wrapAndThrow(e);
                }
            }
        }
    }

    @Override
    public String supportType() {
        return "function";
    }

}
