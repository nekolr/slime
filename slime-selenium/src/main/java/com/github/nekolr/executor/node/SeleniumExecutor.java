package com.github.nekolr.executor.node;

import com.github.nekolr.driver.DriverProvider;
import com.github.nekolr.io.SeleniumResponse;
import com.github.nekolr.slime.context.SpiderContext;
import com.github.nekolr.slime.executor.NodeExecutor;
import com.github.nekolr.slime.model.Shape;
import com.github.nekolr.slime.model.SpiderNode;
import com.github.nekolr.slime.support.ExpressionEngine;
import com.github.nekolr.util.SeleniumResponseHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.URL;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Selenium 执行器
 */
@Slf4j
@Component
public class SeleniumExecutor implements NodeExecutor {

    public static final String NODE_VARIABLE_NAME = "nodeVariableName";

    public static final String DRIVER_TYPE = "driverType";

    public static final String URL = "url";

    public static final String PAGE_LOAD_TIMEOUT = "pageLoadTimeout";

    public static final String IMPLICITLY_WAIT_TIMEOUT = "implicitlyWaitTimeout";

    public static final String PROXY = "proxy";

    public static final String COOKIE_AUTO_SET = "cookie-auto-set";

    private final List<DriverProvider> driverProviders;

    private Map<String, DriverProvider> providerMap;

    private final ExpressionEngine expressionEngine;

    public SeleniumExecutor(List<DriverProvider> driverProviders, ExpressionEngine expressionEngine) {
        this.driverProviders = driverProviders;
        this.expressionEngine = expressionEngine;
    }

    @PostConstruct
    private void init() {
        providerMap = driverProviders.stream().filter(provider -> provider.support() != null).collect(Collectors.toMap(DriverProvider::support, value -> value));
    }

    @Override
    public void execute(SpiderNode node, SpiderContext context, Map<String, Object> variables) {
        String proxy = node.getJsonProperty(PROXY);
        String driverType = node.getJsonProperty(DRIVER_TYPE);
        String nodeVariableName = node.getJsonProperty(NODE_VARIABLE_NAME);
        if (StringUtils.isBlank(nodeVariableName)) {
            nodeVariableName = "resp";
        }
        if (StringUtils.isBlank(driverType) || !providerMap.containsKey(driverType)) {
            log.error("找不到驱动：{}", driverType);
            return;
        }
        if (StringUtils.isNotBlank(proxy)) {
            try {
                proxy = expressionEngine.execute(proxy, variables).toString();
                log.info("设置代理：{}", proxy);
            } catch (Exception e) {
                log.error("设置代理出错，异常信息：{}", e);
            }
        }
        Object oldResp = variables.get(nodeVariableName);
        // 一个任务流中只能有一个 Driver，在页面跳转操作可以使用 resp.toUrl。打开其他 Driver 时，原页面会关闭（同一个变量名）
        if (oldResp instanceof SeleniumResponse) {
            SeleniumResponse oldResponse = (SeleniumResponse) oldResp;
            oldResponse.quit();
        }
        WebDriver driver = null;
        try {
            String url = expressionEngine.execute(node.getJsonProperty(URL), variables).toString();
            log.info("设置请求 url：{}", url);
            driver = providerMap.get(driverType).getWebDriver(node, proxy);
            driver.manage().timeouts().pageLoadTimeout(NumberUtils.toInt(node.getJsonProperty(PAGE_LOAD_TIMEOUT), 60 * 1000), TimeUnit.MILLISECONDS);
            driver.manage().timeouts().implicitlyWait(NumberUtils.toInt(node.getJsonProperty(IMPLICITLY_WAIT_TIMEOUT), 3 * 1000), TimeUnit.MILLISECONDS);
            // 设置自动管理的 Cookie
            boolean cookieAutoSet = !"0".equals(node.getJsonProperty(COOKIE_AUTO_SET));
            Map<String, String> cookieContext = context.getCookieContext();
            // 初始化打开浏览器
            driver.get(url);
            // 设置浏览器 Cookies 环境
            if (cookieAutoSet) {
                driver.manage().deleteAllCookies();
                java.net.URL tempUrl = new URL(url);
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.MONTH, 1);
                for (Map.Entry<String, String> item : cookieContext.entrySet()) {
                    Cookie cookie = new Cookie(item.getKey(), item.getValue(), tempUrl.getHost(), "/", calendar.getTime(), false, false);
                    driver.manage().addCookie(cookie);
                }
                log.debug("自动设置Cookie：{}", cookieContext);
            }
            // 访问跳转 url 网站
            driver.get(url);
            SeleniumResponse response = new SeleniumResponse(driver);
            SeleniumResponseHolder.add(context, response);
            if (cookieAutoSet) {
                Map<String, String> cookies = response.getCookies();
                cookieContext.putAll(cookies);
            }
            variables.put(nodeVariableName, response);
        } catch (Exception e) {
            log.error("请求出错，异常信息：{}", e);
            if (driver != null) {
                try {
                    driver.quit();
                } catch (Exception ignored) {
                }
            }
            ExceptionUtils.wrapAndThrow(e);
        }
    }

    @Override
    public Shape shape() {
        Shape shape = new Shape();
        shape.setImage("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAAEnklEQVRYR7WXb0xbVRTAz7ktyLbyR9wGBMbKkDHjkE6gWwKBYrJlmk1xkcyone2iJiZzDjWL2RdZ4hejGZiQOOOHlpiZkGWkSCAL0fHaUGIIc3UDN3AITGpH9q9FdPxZ3zG3paOF1/ceTt7Hd88953fOPefccxFW+A3XmAyiCFUILA0B0ghBDwQeroYRCQXt3c6VqEQ1wtwoEXsfgGoAME3FHjtDsbnAIQhKsrIAV2tMeiS0IaBJSZHUOgEJhGR9yiGMxdsfF2B4/3MWYtSg0mMZPvKjiHVb2y/YpYQkAYZerLYD4pv/xWuZPfbCtgvWpevLAFbJeMTuMogYgHDYwfY/ex6jDkWwRh/HQ4BMm0XvdNzg5ZS6mgBct4hiXiQxowDMQs3INNZdCVSuNgCvjm1t3dXcTggg0/aGCZF1E1GgvcOHKfNiSjwITUaWN/2946Ns0+YixlgoWvRgKpDo77yScKslD1HMVuMAA9qBRsGzAHDIjgihrDdMzrgae29LRiHZ/JZLt2dfpc8P3vMD7PqlG5i0JgHmt2VR8JXS4I7kJExZM3rUzWbHy5UhqFljFCwLAGY/Ij48+yZhcnj7vfmt0Uoixm09zNU1CMUAi/JhOQrUltLAgRIqTxo/4dLcH1Q4SvJrjMLjGAl/jLG5oKe9w2eI/ONh3/jFV9mtF9F9tp/JerfnaXJZK8TKtVdf9iodB0Oqxkyb2YKIy0rvw5/v9O0fv2/kEOs/bXDPZOiL3mnWxs2NRQcocOqgOJ37oGUs8fZ38keBVIeZtkP1iPDJ0jNjIk04On0pPCEzmlunmnu1nq5BVFUhPAqWXf5i3XWzfEkTnYwLwIGq/vjHebL/blXWtw44cob13ZnGUESUvtx0cH9WGyxfd+0leVElAF6WZ7puTpd9fS57JQBP6Kiv6XXR+MgAHH/LvTl3h/V00UqP4LBxIn/t7+/K9wQegSyb+RggNsjF6oeqj11aTc6WD1pYCgAqJuKpg0Gv6iTcYHvNoEXtJTkAQ7p+qMX0UWHrRew5288q5GRrS0U37wWqypB3w3Arjm1EUgYajVbn8zklVbYe5uwaxKrlMjRVW0qXD5RQxZ+jn3cWzPa8oJCsAY2xm8+VHGCxFctt+nLnYWFv9rMmnx8mzg+wkWs+1HB53or3bhfzs9Ig5/vRrh9fnWkqje6s0jpjWrFFjyiOKpUXX8/VbZho2vX2WIEu8xnGeE4AiKI49dv0zctHfvpGfyL115F9qTMSEYrVzhIoD3cIYzHXMaJUaNVghWU2J8553U9OKt6GRNSm3SnU8D0xAwlA0KMcuvhAvfnevtzHRKVmFWAJZODexwAsJKPkvaAmBruT//7FtulusaIsUp2mTGiMyC0bStUm5FJDQ4UTw+s0FHOFS1RKaAaI/i85lq8U4uh6v/v4xr8UhpBw1i+Fivsw4R2SAOqVciKFBacGC70kIxcgoGNao6D+YRKh5JMyQNAuVx2ns28545cdNbMEqI8knFR+qHqchkHEev44jfY0TtkFAMjBEOxY9oiPUyni0N0BGhMBptlzJ5N262ZnQnIM/AzAo8ZotN5/AYJq57vIypCyAAAAAElFTkSuQmCC");
        shape.setName("selenium");
        shape.setTitle("Selenium");
        shape.setLabel("Selenium");
        return shape;
    }

    @Override
    public String supportType() {
        return "selenium";
    }
}
