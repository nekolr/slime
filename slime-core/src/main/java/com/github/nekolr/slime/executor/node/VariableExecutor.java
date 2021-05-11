package com.github.nekolr.slime.executor.node;

import com.github.nekolr.slime.constant.Constants;
import com.github.nekolr.slime.model.SpiderNode;
import com.github.nekolr.slime.websocket.WebSocketEvent;
import com.github.nekolr.slime.util.ExpressionParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import com.github.nekolr.slime.context.SpiderContext;
import com.github.nekolr.slime.executor.NodeExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 定义变量执行器
 */
@Component
@Slf4j
public class VariableExecutor implements NodeExecutor {

    @Resource
    private ExpressionParser expressionParser;

    @Override
    public void execute(SpiderNode node, SpiderContext context, Map<String, Object> variables) {
        List<Map<String, String>> variableList = node.getJsonArrayProperty(Constants.VARIABLE_NAME, Constants.VARIABLE_VALUE);
        for (Map<String, String> nameValue : variableList) {
            Object value = null;
            String variableName = nameValue.get(Constants.VARIABLE_NAME);
            String variableValue = nameValue.get(Constants.VARIABLE_VALUE);
            try {
                value = expressionParser.parse(variableValue, variables);
                log.debug("设置变量 {} = {}", variableName, value);
                context.pause(node.getNodeId(), WebSocketEvent.COMMON_EVENT, variableName, value);
            } catch (Exception e) {
                log.error("设置变量 {} 出错", variableName, e);
                ExceptionUtils.wrapAndThrow(e);
            }
            variables.put(variableName, value);
        }
    }

    @Override
    public String supportType() {
        return "variable";
    }

    @Override
    public boolean isAsync() {
        return false;
    }

}
