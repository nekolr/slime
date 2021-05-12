package com.github.nekolr.slime.executor.node;

import com.github.nekolr.slime.Grammarly;
import com.github.nekolr.slime.constant.Constants;
import com.github.nekolr.slime.context.SpiderContext;
import com.github.nekolr.slime.model.Grammar;
import com.github.nekolr.slime.model.SpiderNode;
import com.github.nekolr.slime.websocket.WebSocketEvent;
import com.github.nekolr.slime.executor.NodeExecutor;
import com.github.nekolr.slime.io.HttpRequest;
import com.github.nekolr.slime.io.HttpResponse;
import com.github.nekolr.slime.io.SpiderResponse;
import com.github.nekolr.slime.util.ExpressionParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 请求执行器
 */
@Component
@Slf4j
public class RequestExecutor implements NodeExecutor, Grammarly {

    /**
     * 请求的延迟时间
     */
    private static final String REQUEST_SLEEP = "sleep";

    /**
     * 请求的 URL
     */
    private static final String REQUEST_URL = "url";

    /**
     * 请求的代理
     */
    private static final String REQUEST_PROXY = "proxy";

    /**
     * 请求的方法
     */
    private static final String REQUEST_METHOD = "method";

    /**
     * 请求的查询参数名称
     */
    private static final String REQUEST_QUERY_PARAM_NAME = "query-param-name";

    /**
     * 请求的查询参数值
     */
    private static final String REQUEST_QUERY_PARAM_VALUE = "query-param-value";

    /**
     * 请求的 Cookie 名称
     */
    private static final String REQUEST_COOKIE_NAME = "cookie-name";

    /**
     * 请求的 Cookie 值
     */
    private static final String REQUEST_COOKIE_VALUE = "cookie-value";

    /**
     * 请求头名称
     */
    private static final String REQUEST_HEADER_NAME = "header-name";

    /**
     * 请求头的值
     */
    private static final String REQUEST_HEADER_VALUE = "header-value";

    /**
     * 请求超时时间
     */
    private static final String REQUEST_TIMEOUT = "request-timeout";

    /**
     * 请求失败后的重试次数
     */
    private static final String REQUEST_RETRY_COUNT = "request-retry-count";

    /**
     * 重试间隔
     */
    private static final String REQUEST_RETRY_INTERVAL = "request-retry-interval";

    /**
     * 跟随重定向
     */
    private static final String REQUEST_FOLLOW_REDIRECT = "request-follow-redirect";

    /**
     * 自动管理 Cookie
     */
    private static final String REQUEST_AUTO_COOKIE = "request-cookie-auto";

    /**
     * 随机 User-Agent
     */
    private static final String RANDOM_USERAGENT = "request-random-useragent";

    /**
     * 响应内容编码
     */
    private static final String RESPONSE_CHARSET = "response-charset";


    @Resource
    private ExpressionParser expressionParser;

    @Override
    public void execute(SpiderNode node, SpiderContext context, Map<String, Object> variables) {
        // 设置延迟时间
        this.setupSleepTime(node, context);
        // 执行
        this.doExecute(node, context, variables);
    }

    private void doExecute(SpiderNode node, SpiderContext context, Map<String, Object> variables) {
        // 重试次数
        int retryCount = NumberUtils.toInt(node.getJsonProperty(REQUEST_RETRY_COUNT), 0);
        // 重试间隔，单位毫秒
        long retryInterval = NumberUtils.toLong(node.getJsonProperty(REQUEST_RETRY_INTERVAL), 0L);

        boolean success = false;
        for (int i = 0; i < retryCount + 1 && !success; i++) {
            HttpRequest request = HttpRequest.create();
            // 设置 URL
            String url = this.setupUrl(request, node, context, variables);
            // 设置请求超时时间
            this.setupTimeout(request, node);
            // 设置请求方法
            this.setupMethod(request, node);
            // 设置是否跟随重定向
            this.setupFollowRedirects(request, node);
            // 设置头部信息
            this.setupHeaders(request, node, context, variables);
            // 设置 Cookies
            this.setupCookies(request, node, context, variables);
            // 设置请求参数
            this.setupQueryParams(request, node, context, variables);
            // 设置代理
            this.setupProxy(request, node, context, variables);

            Throwable throwable = null;
            try {
                // 发起请求
                HttpResponse response = request.execute();
                if (success = response.getStatusCode() == 200) {
                    // 设置响应编码
                    String charset = node.getJsonProperty(RESPONSE_CHARSET);
                    if (StringUtils.isNotBlank(charset)) {
                        response.setCharset(charset);
                        log.debug("设置响应的编码：{}", charset);
                    }
                    // 是否自动管理 Cookie
                    String cookeAuto = node.getJsonProperty(REQUEST_AUTO_COOKIE);
                    if (Constants.YES.equals(cookeAuto)) {
                        // 将响应的 Cookie 放入 Cookie 上下文中
                        context.getCookieContext().putAll(response.getCookies());
                    }
                    // 将结果放入要传递的变量集合中
                    variables.put(Constants.RESPONSE_VARIABLE, response);
                }
            } catch (IOException e) {
                success = false;
                throwable = e;
            } finally {
                if (!success) {
                    if (i < retryCount) {
                        // 睡眠一段时间后重试
                        if (retryInterval > 0) {
                            try {
                                TimeUnit.MILLISECONDS.sleep(retryInterval);
                            } catch (InterruptedException ignored) {
                            }
                        }
                        log.info("第 {} 次重试 URL：{}", i + 1, url);
                    } else {
                        log.error("请求 URL：{} 出错", url, throwable);
                    }
                }
            }
        }
    }

    /**
     * 设置代理
     *
     * @param request   请求包装对象
     * @param node      节点
     * @param variables 传递的变量与值
     */
    private void setupProxy(HttpRequest request, SpiderNode node, SpiderContext context, Map<String, Object> variables) {
        String proxy = node.getJsonProperty(REQUEST_PROXY);
        if (StringUtils.isNotBlank(proxy)) {
            try {
                Object value = expressionParser.parse(proxy, variables);
                if (value != null) {
                    String[] proxyArr = StringUtils.split((String) value, Constants.PROXY_HOST_PORT_SEPARATOR);
                    if (proxyArr.length == 2) {
                        context.pause(node.getNodeId(), WebSocketEvent.COMMON_EVENT, REQUEST_PROXY, value);
                        log.info("设置代理地址：{}", value);
                        request.proxy(proxyArr[0], Integer.parseInt(proxyArr[1]));
                    }
                }
            } catch (Exception e) {
                log.error("设置代理出错", e);
            }
        }
    }

    /**
     * 设置 Cookies
     *
     * @param request   请求包装对象
     * @param node      节点
     * @param context   执行上下文
     * @param variables 传递的变量与值
     */
    private void setupCookies(HttpRequest request, SpiderNode node, SpiderContext context, Map<String, Object> variables) {
        // 获取根节点
        SpiderNode root = context.getRoot();

        // 根节点（全局）的 Cookie
        Map<String, String> cookies = this.getCookies(root.getJsonArrayProperty(REQUEST_COOKIE_NAME, REQUEST_COOKIE_VALUE), context, root, variables);
        request.cookies(cookies);

        // Cookie 上下文，包含之前设置的 Cookie
        Map<String, String> cookieContext = context.getCookieContext();
        String cookeAuto = node.getJsonProperty(REQUEST_AUTO_COOKIE);
        if (Constants.YES.equals(cookeAuto) && !context.getCookieContext().isEmpty()) {
            context.pause(node.getNodeId(), WebSocketEvent.REQUEST_AUTO_COOKIE_EVENT, REQUEST_AUTO_COOKIE, cookieContext);
            request.cookies(cookieContext);
            log.info("自动设置 Cookies：{}", cookieContext);
        }

        // 当前节点的 Cookie
        cookies = this.getCookies(node.getJsonArrayProperty(REQUEST_COOKIE_NAME, REQUEST_COOKIE_VALUE), context, node, variables);
        request.cookies(cookies);

        // 将当前设置的全局 Cookie 和节点 Cookie 都放入 Cookie 上下文中
        if (Constants.YES.equals(cookeAuto)) {
            cookieContext.putAll(cookies);
        }
    }

    /**
     * 解析 Cookies
     *
     * @param cookies   需要解析的 Cookies
     * @param variables 传递的变量与值
     * @return 解析后的 Cookies
     */
    private Map<String, String> getCookies(List<Map<String, String>> cookies, SpiderContext context, SpiderNode node, Map<String, Object> variables) {
        Map<String, String> result = new HashMap<>();
        if (cookies != null) {
            for (Map<String, String> cookie : cookies) {
                String cookieName = cookie.get(REQUEST_COOKIE_NAME);
                if (StringUtils.isNotBlank(cookieName)) {
                    String cookieValue = cookie.get(REQUEST_COOKIE_VALUE);
                    try {
                        Object value = expressionParser.parse(cookieValue, variables);
                        result.put(cookieName, (String) value);
                        context.pause(node.getNodeId(), WebSocketEvent.REQUEST_COOKIE_EVENT, cookieName, value);
                        log.info("设置 Cookie：{} = {}", cookieName, value);
                    } catch (Exception e) {
                        log.error("解析请求 Cookie：{} 出错", cookieName, e);
                    }
                }
            }
        }
        return result;
    }

    /**
     * 设置查询参数
     *
     * @param request   请求包装对象
     * @param node      节点
     * @param context   执行上下文
     * @param variables 传递的变量与值
     */
    private void setupQueryParams(HttpRequest request, SpiderNode node, SpiderContext context, Map<String, Object> variables) {
        // 获取根节点
        SpiderNode root = context.getRoot();
        // 设置根节点（全局）的查询参数
        List<Map<String, String>> rootParams = root.getJsonArrayProperty(REQUEST_QUERY_PARAM_NAME, REQUEST_QUERY_PARAM_VALUE);
        this.setQueryParams(request, root, rootParams, context, variables);
        // 设置当前节点的查询参数
        List<Map<String, String>> params = node.getJsonArrayProperty(REQUEST_QUERY_PARAM_NAME, REQUEST_QUERY_PARAM_VALUE);
        this.setQueryParams(request, node, params, context, variables);
    }

    /**
     * 设置查询参数
     *
     * @param request   请求包装对象
     * @param params    解析后的查询参数
     * @param variables 传递的变量与值
     */
    private void setQueryParams(HttpRequest request, SpiderNode node, List<Map<String, String>> params, SpiderContext context, Map<String, Object> variables) {
        if (params != null) {
            for (Map<String, String> param : params) {
                String paramName = param.get(REQUEST_QUERY_PARAM_NAME);
                if (StringUtils.isNotBlank(paramName)) {
                    String paramValue = param.get(REQUEST_QUERY_PARAM_VALUE);
                    try {
                        Object value = expressionParser.parse(paramValue, variables);
                        request.data(paramName, value);
                        context.pause(node.getNodeId(), WebSocketEvent.REQUEST_PARAM_EVENT, paramName, value);
                        log.info("设置请求查询参数：{} = {}", paramName, value);
                    } catch (Exception e) {
                        log.error("设置请求查询参数：{} 出错", paramName, e);
                    }
                }
            }
        }
    }


    /**
     * 设置头部信息
     *
     * @param request   请求包装对象
     * @param node      节点
     * @param context   执行上下文
     * @param variables 传递的变量与值
     */
    private void setupHeaders(HttpRequest request, SpiderNode node, SpiderContext context, Map<String, Object> variables) {
        // 获取根节点
        SpiderNode root = context.getRoot();
        // 设置根节点（全局）的头部信息
        List<Map<String, String>> rootHeaders = root.getJsonArrayProperty(REQUEST_HEADER_NAME, REQUEST_HEADER_VALUE);
        this.setHeaders(request, rootHeaders, context, root, variables);
        // 设置当前节点的头部信息
        List<Map<String, String>> headers = node.getJsonArrayProperty(REQUEST_HEADER_NAME, REQUEST_HEADER_VALUE);
        this.setHeaders(request, headers, context, node, variables);
    }

    /**
     * 设置头部信息
     *
     * @param request   请求包装对象
     * @param headers   解析后的头部信息
     * @param variables 传递的变量与值
     */
    private void setHeaders(HttpRequest request, List<Map<String, String>> headers, SpiderContext context, SpiderNode node, Map<String, Object> variables) {
        if (headers != null) {
            for (Map<String, String> header : headers) {
                String headerName = header.get(REQUEST_HEADER_NAME);
                if (StringUtils.isNotBlank(headerName)) {
                    String headerValue = header.get(REQUEST_HEADER_VALUE);
                    try {
                        Object value = expressionParser.parse(headerValue, variables);
                        request.header(headerName, value);
                        context.pause(node.getNodeId(), WebSocketEvent.REQUEST_HEADER_EVENT, headerName, value);
                        log.info("设置请求 Header：{} = {}", headerName, value);
                    } catch (Exception e) {
                        log.error("设置请求 Header：{} 出错", headerName, e);
                    }
                }
            }
        }
    }

    /**
     * 设置是否跟随重定向
     *
     * @param request 请求包装对象
     * @param node    节点
     */
    private void setupFollowRedirects(HttpRequest request, SpiderNode node) {
        String followRedirect = node.getJsonProperty(REQUEST_FOLLOW_REDIRECT);
        boolean following = Constants.YES.equals(followRedirect);
        log.debug("设置是否跟随重定向：{}", following);
        request.followRedirects(following);
    }

    /**
     * 设置请求方法
     *
     * @param request 请求包装对象
     * @param node    节点
     */
    private void setupMethod(HttpRequest request, SpiderNode node) {
        String method = node.getJsonProperty(REQUEST_METHOD, "GET");
        log.debug("设置请求方法：{}", method);
        request.method(method);
    }

    /**
     * 设置请求超时时间
     *
     * @param request 请求包装对象
     * @param node    节点
     */
    private void setupTimeout(HttpRequest request, SpiderNode node) {
        // 默认 30s
        int timeout = NumberUtils.toInt(node.getJsonProperty(REQUEST_TIMEOUT), 30000);
        log.debug("设置请求超时时间：{} ms", timeout);
        request.timeout(timeout);
    }

    /**
     * 设置 URL
     *
     * @param request   请求包装对象
     * @param node      节点
     * @param variables 传递的变量与值
     */
    private String setupUrl(HttpRequest request, SpiderNode node, SpiderContext context, Map<String, Object> variables) {
        String url = null;
        try {
            url = (String) expressionParser.parse(node.getJsonProperty(REQUEST_URL), variables);
            context.pause(node.getNodeId(), WebSocketEvent.COMMON_EVENT, REQUEST_URL, url);
            log.info("设置请求 URL：{}", url);
            request.url(url);
        } catch (Exception e) {
            log.error("设置请求 URL 出错", e);
            // 直接抛出异常
            ExceptionUtils.wrapAndThrow(e);
        }
        return url;
    }

    /**
     * 设置延迟时间
     *
     * @param node    节点
     * @param context 执行上下文
     */
    private void setupSleepTime(SpiderNode node, SpiderContext context) {
        // 获取睡眠时间
        String sleep = node.getJsonProperty(REQUEST_SLEEP);
        long sleepTime = NumberUtils.toLong(sleep, 0L);
        try {
            // 实际等待的时间 = 上次执行的时间 + 睡眠的时间 - 当前时间
            Long lastTime = (Long) context.getExtends_map().get(Constants.LAST_REQUEST_EXECUTE_TIME + node.getNodeId());
            if (lastTime != null) {
                sleepTime = lastTime + sleepTime - System.currentTimeMillis();
            }
            if (sleepTime > 0) {
                context.pause(node.getNodeId(), WebSocketEvent.COMMON_EVENT, REQUEST_SLEEP, sleepTime);
                log.debug("设置延迟时间：{} ms", sleepTime);
                // 睡眠
                TimeUnit.MILLISECONDS.sleep(sleepTime);
            }
            // 更新上次执行的时间
            context.getExtends_map().put(Constants.LAST_REQUEST_EXECUTE_TIME + node.getNodeId(), System.currentTimeMillis());
        } catch (Throwable t) {
            log.error("设置延迟时间失败", t);
        }
    }

    @PostConstruct
    void initialize() {
        // 允许设置被限制的请求头
        System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
    }

    @Override
    public String supportType() {
        return "request";
    }

    @Override
    public List<Grammar> grammars() {
        List<Grammar> grammars = Grammar.findGrammars(SpiderResponse.class, "resp", "SpiderResponse", false);
        Grammar grammar = new Grammar();
        grammar.setFunction("resp");
        grammar.setComment("抓取结果");
        grammar.setOwner("SpiderResponse");
        grammars.add(grammar);
        return grammars;
    }
}
