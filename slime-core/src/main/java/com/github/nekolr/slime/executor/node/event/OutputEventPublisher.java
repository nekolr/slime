package com.github.nekolr.slime.executor.node.event;

import com.github.nekolr.slime.constant.Constants;
import com.github.nekolr.slime.constant.OutputType;
import com.github.nekolr.slime.context.SpiderContext;
import com.github.nekolr.slime.model.SpiderNode;
import com.github.nekolr.slime.model.SpiderOutput.OutputItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OutputEventPublisher {

    @Autowired
    @SuppressWarnings("all")
    private ApplicationEventPublisher eventPublisher;

    /**
     * 发布输出事件
     *
     * @param context     执行上下文
     * @param node        节点
     * @param outputItems 所有的输出项数据
     */
    public void publish(SpiderContext context, SpiderNode node, List<OutputItem> outputItems) {
        OutputType[] outputTypes = OutputType.values();
        for (OutputType outputType : outputTypes) {
            if (Constants.YES.equals(node.getJsonProperty(outputType.getVariableName()))) {
                eventPublisher.publishEvent(new OutputEventBean(context, node, outputItems, outputType.getVariableName()));
            }
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    class OutputEventBean {
        private SpiderContext context;
        private SpiderNode node;
        private List<OutputItem> outputItems;
        private String event;
    }

}
