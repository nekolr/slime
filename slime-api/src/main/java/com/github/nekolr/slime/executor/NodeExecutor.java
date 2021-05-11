package com.github.nekolr.slime.executor;

import com.github.nekolr.slime.model.Shape;
import com.github.nekolr.slime.model.SpiderNode;
import com.github.nekolr.slime.context.SpiderContext;

import java.util.Map;

/**
 * 节点执行器
 */
public interface NodeExecutor {

    /**
     * 执行器具体执行的逻辑
     *
     * @param node      节点
     * @param context   执行上下文
     * @param variables 传递的变量与值
     */
    void execute(SpiderNode node, SpiderContext context, Map<String, Object> variables);

    /**
     * 是否允许执行下一个节点
     *
     * @param node      节点
     * @param context   执行上下文
     * @param variables 传递的变量与值
     * @return 是否允许执行下一个节点
     */
    default boolean allowExecuteNext(SpiderNode node, SpiderContext context, Map<String, Object> variables) {
        return true;
    }

    /**
     * 节点对应的图形（只有扩展节点才会有该数据）
     *
     * @return 节点对应的图形
     */
    default Shape shape() {
        return null;
    }

    /**
     * 是否能够异步执行
     *
     * @return 是否开启新线程来执行
     */
    default boolean isAsync() {
        return true;
    }

    /**
     * 支持的节点类型
     *
     * @return 节点类型名称
     */
    String supportType();
}
