package com.github.nekolr.slime.support;

import com.github.nekolr.slime.executor.NodeExecutor;
import com.github.nekolr.slime.model.Shape;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 执行器工厂
 */
@Component
public class ExecutorFactory {

    /**
     * 所有的节点执行器集合，通过容器注入
     */
    private List<NodeExecutor> executors;

    /**
     * 节点类型 -> 节点执行器
     */
    private Map<String, NodeExecutor> executor_map;

    @Autowired
    public ExecutorFactory(List<NodeExecutor> executors) {
        this.executors = executors;
    }


    @PostConstruct
    public void initialize() {
        executor_map = this.executors.stream().collect(Collectors.toMap(NodeExecutor::supportType, v -> v));
    }

    /**
     * 获取节点执行器
     *
     * @param type 节点类型名称
     * @return 节点执行器
     */
    public NodeExecutor getExecutor(String type) {
        return executor_map.get(type);
    }

    /**
     * 获取所有的扩展图形
     *
     * @return 所有的扩展图形
     */
    public List<Shape> shapes() {
        return executors.stream().filter(e -> e.shape() != null).map(executor -> executor.shape()).collect(Collectors.toList());
    }
}
