package com.github.nekolr.slime;

import com.alibaba.ttl.TtlRunnable;
import com.github.nekolr.slime.concurrent.SpiderThreadPoolExecutor.SubThreadPoolExecutor;
import com.github.nekolr.slime.concurrent.SpiderThreadPoolExecutor;
import com.github.nekolr.slime.config.SpiderConfig;
import com.github.nekolr.slime.constant.ConditionType;
import com.github.nekolr.slime.constant.Constants;
import com.github.nekolr.slime.context.SpiderContext;
import com.github.nekolr.slime.context.SpiderContextHolder;
import com.github.nekolr.slime.domain.SpiderFlow;
import com.github.nekolr.slime.executor.NodeExecutor;
import com.github.nekolr.slime.listener.SpiderListener;
import com.github.nekolr.slime.model.SpiderNode;
import com.github.nekolr.slime.model.SpiderOutput;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.github.nekolr.slime.util.*;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 爬虫的主要逻辑都在这里
 */
@Component
@Slf4j
public class Spider {

    /**
     * 配置
     */
    @Resource
    private SpiderConfig spiderConfig;

    /**
     * 监听器集合
     */
    @SuppressWarnings("all")
    @Autowired(required = false)
    private List<SpiderListener> listeners;

    /**
     * 执行器工厂
     */
    @Resource
    private ExecutorFactory executorFactory;

    /**
     * 表达式解析器
     */
    @Resource
    private ExpressionParser expressionParser;

    /**
     * 线程池
     */
    @Getter
    private SpiderThreadPoolExecutor threadPool;


    @PostConstruct
    public void initialize() {
        initializeListeners();
        threadPool = new SpiderThreadPoolExecutor(spiderConfig.getMaxThreads());
    }

    /**
     * 初始化监听器集合
     */
    private void initializeListeners() {
        if (this.listeners == null) {
            this.listeners = Collections.EMPTY_LIST;
        }
    }

    /**
     * 执行测试
     *
     * @param root    根节点
     * @param context 执行上下文
     */
    public void runWithTest(SpiderNode root, SpiderContext context) {
        // 设置执行上下文
        SpiderContextHolder.set(context);
        // 死循环检测的计数器
        AtomicInteger executeCount = new AtomicInteger(0);
        // 存入到上下文中，以供后续检测
        context.getExtends_map().put(Constants.ATOMIC_DEAD_CYCLE, executeCount);
        // 执行
        doRun(root, context, new HashMap<>());
        // 当爬虫任务执行完毕时，判断是否超出预期
        if (executeCount.get() > spiderConfig.getDeadCycle()) {
            log.error("检测到可能出现死循环，测试终止");
        } else {
            log.info("测试完毕");
        }
        // 删除执行上下文，防止内存泄露
        SpiderContextHolder.remove();
    }


    /**
     * 入口方法
     *
     * @param spiderFlow 流程
     * @param context    执行上下文
     * @return 输出结果
     */
    public List<SpiderOutput> run(SpiderFlow spiderFlow, SpiderContext context) {
        return run(spiderFlow, context, new HashMap<>());
    }

    /**
     * 入口方法
     *
     * @param spiderFlow 流程
     * @param context    执行上下文
     * @param variables  传递的变量与值
     * @return 输出结果
     */
    public List<SpiderOutput> run(SpiderFlow spiderFlow, SpiderContext context, Map<String, Object> variables) {
        if (variables == null) {
            variables = new HashMap<>();
        }
        // 解析流程图为节点信息
        SpiderNode root = SpiderFlowUtils.parseXmlToSpiderNode(spiderFlow.getXml());
        // 真正的执行方法
        doRun(root, context, variables);
        // 返回输出结果
        return context.getOutputs();
    }


    /**
     * 真正的执行方法
     *
     * @param root      根节点
     * @param context   执行上下文
     * @param variables 传递的变量与值
     */
    private void doRun(SpiderNode root, SpiderContext context, Map<String, Object> variables) {
        // 获取用户配置的单个流程任务的线程数
        int threads = NumberUtils.toInt(root.getJsonProperty(Constants.THREAD_COUNT), spiderConfig.getDefaultThreads());
        // 创建子线程池，+1 是因为有一个线程作为调度线程
        SubThreadPoolExecutor subThreadPool = threadPool.createSubThreadPoolExecutor(Math.max(threads, 1) + 1);
        context.setSubThreadPool(subThreadPool);
        context.setRoot(root);
        // 触发监听器
        listeners.forEach(listener -> listener.beforeStart(context));
        // 将任务交由调度器运行
        Future<?> future = runWithScheduler(subThreadPool, root, context, variables);
        try {
            // 阻塞调度线程
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("出现严重异常", e);
        }
    }

    /**
     * 将任务交由调度器运行
     *
     * @param subThreadPool 子线程池
     * @param root          根节点
     * @param context       执行上下文
     * @param variables     传递的变量与值
     * @return 调度线程执行的任务
     */
    private Future<?> runWithScheduler(SubThreadPoolExecutor subThreadPool, SpiderNode root,
                                       SpiderContext context, Map<String, Object> variables) {
        Future<?> future = subThreadPool.submitAsync(TtlRunnable.get(() -> {
            try {
                // 先执行根节点
                this.executeNode(null, root, context, variables);
                // 接下来调度线程将根据任务的执行情况决定哪些任务继续向下执行
                Queue<Future<?>> queue = context.getFutureTaskQueue();
                // 自旋，直到队列为空
                while (!queue.isEmpty()) {
                    // 取执行完毕的第一个任务
                    Optional<Future<?>> firstTask = queue.stream().filter(Future::isDone).findFirst();
                    // 不存在完成的任务时跳过
                    if (firstTask.isPresent()) {
                        try {
                            // 移除完成的任务
                            queue.remove(firstTask.get());
                            // 是否应该停止运行
                            if (context.isRunning()) {
                                // 获取任务返回值
                                Task task = (Task) firstTask.get().get();
                                // 节点执行完毕，任务数 -1
                                task.node.decrement();
                                NodeExecutor executor = task.executor;
                                // 判断执行器是否允许执行后面的节点
                                if (executor.allowExecuteNext(task.node, context, task.variables)) {
                                    log.debug("执行节点 {} 完毕", task.node);
                                    // 执行后面的节点
                                    executeNextNodes(task.node, context, task.variables);
                                } else {
                                    log.debug("执行节点 {} 完毕，忽略执行下一个节点", task.node);
                                }
                            }

                        } catch (InterruptedException | ExecutionException e) {
                            log.error("获取任务的返回值失败，跳过该任务", e);
                        }
                    }
                }
                // 等待子线程池中所有的任务线程执行结束
                subThreadPool.awaitTermination();
            } finally {
                // 触发监听器
                listeners.forEach(listener -> listener.afterEnd(context));
            }
        }), null);

        return future;
    }

    /**
     * 执行该节点后面的节点
     *
     * @param node      当前节点
     * @param context   执行上下文
     * @param variables 传递的变量与值
     */
    public void executeNextNodes(SpiderNode node, SpiderContext context, Map<String, Object> variables) {
        List<SpiderNode> nextNodes = node.getNextNodes();
        if (nextNodes != null) {
            for (SpiderNode next : nextNodes) {
                this.executeNode(node, next, context, variables);
            }
        }
    }

    /**
     * 执行当前节点
     *
     * @param fromNode  触发调用的上一级节点
     * @param node      当前节点
     * @param context   执行上下文
     * @param variables 传递的变量与值
     */
    public void executeNode(SpiderNode fromNode, SpiderNode node, SpiderContext context, Map<String, Object> variables) {
        String nodeType = node.getJsonProperty(Constants.NODE_TYPE);
        // 如果当前节点类型为空则执行后面的节点
        if (StringUtils.isBlank(nodeType)) {
            this.executeNextNodes(node, context, variables);
            return;
        }
        // 验证流转条件是否满足
        if (!validateCondition(fromNode, node, variables)) {
            return;
        }
        // 获取节点对应的执行器
        NodeExecutor executor = executorFactory.getExecutor(nodeType);
        if (executor == null) {
            log.error("节点 {} 执行失败，没有找到对应的执行器：{}", node, nodeType);
            context.setRunning(Boolean.FALSE);
            return;
        }

        int loopCount = 1; // 循环次数默认情况下为 1
        int loopStartIndex = 0; // 开始的下标默认为 0
        int loopEndIndex = 0; // 结束的下标默认为 0
        // 取出节点执行的循环次数
        String loopCountValue = node.getJsonProperty(Constants.NODE_LOOP_COUNT);
        // 不为空说明需要循环执行
        if (StringUtils.isNotBlank(loopCountValue)) {
            // 解析循环次数
            Object loopCountObj = expressionParser.parse(loopCountValue, variables);
            // 尝试转换成数字，失败时返回 0
            loopCount = NumberUtils.toInt(loopCountObj.toString(), 0);

            if (loopCount > 0) {
                int start = NumberUtils.toInt(node.getJsonProperty(Constants.NODE_LOOP_START_INDEX), 0);
                // 确保起始下标不会出现负值
                loopStartIndex = Math.max(start, 0);
                // 确保起始下标不会超过循环次数
                loopStartIndex = (loopStartIndex >= loopCount ? 0 : loopStartIndex);
                // -1 表示默认使用 (循环次数 - 1) 作为结束下标
                int end = NumberUtils.toInt(node.getJsonProperty(Constants.NODE_LOOP_END_INDEX), -1);
                if (end >= 0) {
                    // 确保结束下标不超过循环次数
                    loopEndIndex = Math.min(end, loopCount - 1);
                } else {
                    loopEndIndex = loopCount - 1;
                }
            }
            log.info("获取循环次数 {} 的值为：{}", loopCountValue, loopCount);
        }

        if (loopCount > 0) {
            // 获取循环下标的变量名称
            String indexName = node.getJsonProperty(Constants.NODE_LOOP_INDEX);
            // 临时存放任务
            List<Task> tasks = Lists.newArrayListWithExpectedSize(loopCount);
            for (int i = loopStartIndex; i < loopEndIndex + 1; i++) {
                node.increment(); // 节点任务数 +1
                if (context.isRunning()) {
                    // 需要传递的变量和值
                    Map<String, Object> newVariables = new HashMap<>();
                    // 判断是否需要传递变量和值
                    if (fromNode == null || node.needTransmit(fromNode.getNodeId())) {
                        newVariables.putAll(variables);
                    }
                    // 放入循环下标的变量名称和对应的值
                    if (StringUtils.isNotBlank(indexName)) {
                        newVariables.put(indexName, i);
                    }
                    tasks.add(new Task(TtlRunnable.get(() -> {
                        if (context.isRunning()) {
                            try {
                                // 死循环检测
                                deadCycleCheck(context);
                                // 执行节点具体的逻辑
                                executor.execute(node, context, newVariables);
                                // 当没有发生异常时，移除上下文中的异常变量
                                newVariables.remove(Constants.EXCEPTION_VARIABLE);
                            } catch (Throwable t) {
                                newVariables.put(Constants.EXCEPTION_VARIABLE, t);
                                log.error("执行节点 {} 出现异常", node, t);
                            }
                        }
                    }), node, newVariables, executor));
                }
            }
            LinkedBlockingQueue<Future<?>> taskQueue = context.getFutureTaskQueue();
            for (Task task : tasks) {
                // 获取根节点
                SpiderNode root = context.getRoot();
                // 是否需要同步执行
                boolean runSync = Constants.YES.equals(root.getJsonProperty(Constants.RUN_SYNC));
                if (executor.isAsync() && !runSync) {
                    // 可以异步执行，则提交到线程池
                    taskQueue.add(context.getSubThreadPool().submitAsync(task.runnable, task));
                } else {
                    // 不可以异步执行，则直接在当前线程中执行
                    FutureTask<Task> futureTask = new FutureTask<>(task.runnable, task);
                    futureTask.run();
                    taskQueue.add(futureTask);
                }
            }
        }
    }

    /**
     * 死循环检测
     *
     * @param context 执行上下文
     */
    private void deadCycleCheck(SpiderContext context) {
        AtomicInteger count = (AtomicInteger) context.getExtends_map().get(Constants.ATOMIC_DEAD_CYCLE);
        if (count != null && count.incrementAndGet() > spiderConfig.getDeadCycle()) {
            log.error("发现疑似死循环，任务停止");
            context.setRunning(Boolean.FALSE);
        }
    }

    /**
     * 检查流转条件是否成立
     *
     * @param fromNode  触发调用的上一级节点
     * @param node      当前节点
     * @param variables 传递的变量与值
     * @return 条件是否成立
     */
    private boolean validateCondition(SpiderNode fromNode, SpiderNode node, Map<String, Object> variables) {
        if (fromNode != null) {
            // 是否存在异常
            boolean hasException = variables.get(Constants.EXCEPTION_VARIABLE) != null;
            String conditionType = node.getConditionType(fromNode.getNodeId());
            // 条件不成立
            if ((ConditionType.ON_EXCEPTION.getCode().equals(conditionType) && !hasException) ||
                    (ConditionType.NO_EXCEPTION.getCode().equals(conditionType) && hasException)) {
                return false;
            }
            // 之后判断条件表达式
            String condition = node.getCondition(fromNode.getNodeId());
            if (StringUtils.isNotBlank(condition)) {
                try {
                    // 解析条件表达式
                    Object result = expressionParser.parse(condition, variables);
                    if (result != null) {
                        boolean match = Boolean.toString(true).equals(result) || Objects.equals(result, true);
                        log.debug("条件表达式 {} 的结果为 {}", condition, match);
                        return match;
                    }
                } catch (Throwable t) {
                    log.error("判断条件表达式 {} 出错", condition, t);
                    return false;
                }
            }
        }
        return true;
    }


    @AllArgsConstructor
    class Task {

        /**
         * 任务
         */
        Runnable runnable;

        /**
         * 节点
         */
        SpiderNode node;

        /**
         * 传递的变量与值
         */
        Map<String, Object> variables;

        /**
         * 执行器
         */
        NodeExecutor executor;
    }
}
