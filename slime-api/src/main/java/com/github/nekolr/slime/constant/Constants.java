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
     * 请求的延迟时间
     */
    String REQUEST_SLEEP = "sleep";

    /**
     * 请求的 URL
     */
    String REQUEST_URL = "url";

    /**
     * 请求的代理
     */
    String REQUEST_PROXY = "proxy";

    /**
     * 请求的方法
     */
    String REQUEST_METHOD = "method";

    /**
     * 请求的查询参数名称
     */
    String REQUEST_QUERY_PARAM_NAME = "query-param-name";

    /**
     * 请求的查询参数值
     */
    String REQUEST_QUERY_PARAM_VALUE = "query-param-value";

    /**
     * 请求的 Cookie 名称
     */
    String REQUEST_COOKIE_NAME = "cookie-name";

    /**
     * 请求的 Cookie 值
     */
    String REQUEST_COOKIE_VALUE = "cookie-value";

    /**
     * 请求头名称
     */
    String REQUEST_HEADER_NAME = "header-name";

    /**
     * 请求头的值
     */
    String REQUEST_HEADER_VALUE = "header-value";

    /**
     * 请求超时时间
     */
    String REQUEST_TIMEOUT = "request-timeout";

    /**
     * 请求失败后的重试次数
     */
    String REQUEST_RETRY_COUNT = "request-retry-count";

    /**
     * 重试间隔
     */
    String REQUEST_RETRY_INTERVAL = "request-retry-interval";

    /**
     * 跟随重定向
     */
    String REQUEST_FOLLOW_REDIRECT = "request-follow-redirect";

    /**
     * 自动管理 Cookie
     */
    String REQUEST_AUTO_COOKIE = "request-cookie-auto";

    /**
     * 响应内容编码
     */
    String RESPONSE_CHARSET = "response-charset";

    /**
     * 变量名称
     */
    String VARIABLE_NAME = "variable-name";

    /**
     * 变量值
     */
    String VARIABLE_VALUE = "variable-value";

    /**
     * 函数
     */
    String FUNCTION = "function";

    /**
     * 数据源 ID
     */
    String DATASOURCE_ID = "datasourceId";

    /**
     * SQL
     */
    String SQL = "sql";

    /**
     * 语句类型
     */
    String STATEMENT_TYPE = "statementType";

    /**
     * 是否输出到 SqlRowSet
     */
    String SELECT_RESULT_SQL_ROW_SET = "isSqlRowSet";

    /**
     * 流程 ID
     */
    String FLOW_ID = "flowId";

    /**
     * 输出其他变量
     */
    String OUTPUT_OTHERS = "output-others";

    /**
     * 输出项的名称
     */
    String OUTPUT_NAME = "output-name";

    /**
     * 输出项的值
     */
    String OUTPUT_VALUE = "output-value";

    /**
     * 输出到数据库中的表名
     */
    String OUTPUT_TABLE_NAME = "tableName";

    /**
     * CSV 文件的名称
     */
    String OUTPUT_CSV_NAME = "csvName";

    /**
     * CSV 文件的编码
     */
    String OUTPUT_CSV_ENCODING = "csvEncoding";


    //*************************************************************************************
    //                                 Node Property Value
    //*************************************************************************************

    /**
     * 需要将上一节点的变量和值传递到当前节点
     */
    String NEED_TRANSMIT_VARIABLE = "1";

    /**
     * 跟随重定向
     */
    String FOLLOW_REDIRECT = "1";

    /**
     * 跟随重定向
     */
    String COOKIE_AUTO = "1";

    /**
     * 同步执行
     */
    String IS_RUN_SYNC = "1";

    /**
     * 输出到 SqlRowSet 流
     */
    String IS_SQL_ROW_SET = "1";

    /**
     * 输出其他变量
     */
    String IS_OUTPUT_OTHERS = "1";

    /**
     * 输出到数据库
     */
    String IS_OUTPUT_DATABASE = "1";

    /**
     * 输出到 CSV 文件
     */
    String IS_OUTPUT_CSV = "1";


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
}
