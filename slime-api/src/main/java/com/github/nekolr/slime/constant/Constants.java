package com.github.nekolr.slime.constant;

/**
 * 常量
 */
public interface Constants {

    //*************************************************************************************
    //                                    Spider Context
    //*************************************************************************************

    /**
     * 死锁检测对应的 AtomicInteger 的名称
     */
    String ATOMIC_DEAD_CYCLE = "__atomic_dead_cycle";

    /**
     * 节点执行出现异常时向上下文中存放的变量名称
     */
    String EXCEPTION_VARIABLE = "__ex";

    /**
     * 上次请求执行的时间对应的变量前缀
     */
    String LAST_REQUEST_EXECUTE_TIME = "__last_request_execute_time_";

    /**
     * 响应结果对应的变量名称
     */
    String RESPONSE_VARIABLE = "resp";

    /**
     * SQL 执行的结果对应的变量名称
     */
    String SQL_RESULT = "rs";


    //*************************************************************************************
    //                                     Thread Pool
    //*************************************************************************************

    /**
     * 线程组名称
     */
    String SLIME_THREAD_GROUP_NAME = "slime-thread-group";

    /**
     * 线程名前缀
     */
    String SLIME_THREAD_NAME_PREFIX = "slime-thread-";


    //*************************************************************************************
    //                                 Node Property Name
    //*************************************************************************************

    /**
     * 单个流程任务的线程数
     */
    String THREAD_COUNT = "threadCount";

    /**
     * 同步执行
     */
    String RUN_SYNC = "runSync";

    /**
     * 节点类型
     */
    String NODE_TYPE = "shape";

    /**
     * 节点循环执行次数
     */
    String NODE_LOOP_COUNT = "loopCount";

    /**
     * 节点循环执行时的下标
     */
    String NODE_LOOP_INDEX = "loopIndex";

    /**
     * 节点循环执行时的起始下标
     */
    String NODE_LOOP_START_INDEX = "loopStartIndex";

    /**
     * 节点循环执行时的结束下标
     */
    String NODE_LOOP_END_INDEX = "loopEndIndex";

    /**
     * 函数
     */
    String FUNCTION = "function";

    /**
     * 流程 ID
     */
    String FLOW_ID = "flowId";

    /**
     * 数据源 ID
     */
    String DATASOURCE_ID = "datasourceId";


    //*************************************************************************************
    //                                 Node Property Value
    //*************************************************************************************

    String YES = "1";


    //*************************************************************************************
    //                                    Quartz Job
    //*************************************************************************************

    /**
     * 定时任务名称前缀
     */
    String QUARTZ_JOB_NAME_PREFIX = "SLIME_TASK_";

    /**
     * 定时任务的上下文参数：SpiderFlow 的名称
     */
    String QUARTZ_SPIDER_FLOW_PARAM_NAME = "SLIME_SPIDER_FLOW";


    //*************************************************************************************
    //                                       Others
    //*************************************************************************************

    /**
     * 代理地址中域名与端口号之间的分隔符
     */
    String PROXY_HOST_PORT_SEPARATOR = ":";

    /**
     * 流程对应的日志目录前缀
     */
    String SPIDER_FLOW_LOG_DIR_PREFIX = "slime_spider_flow_";

    /**
     * 流程任务对应的日志目录前缀
     */
    String SPIDER_TASK_LOG_DIR_PREFIX = "slime_spider_task_";


    //*************************************************************************************
    //                                       JWT
    //*************************************************************************************

    /**
     * Token 在请求头中的 key
     */
    String TOKEN_HEADER_KEY = "Authorization";

    /**
     * Token 在请求头中的值的前缀
     */
    String TOKEN_HEADER_VALUE_PREFIX = "Bearer ";
}
