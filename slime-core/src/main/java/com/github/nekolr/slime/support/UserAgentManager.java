package com.github.nekolr.slime.support;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class UserAgentManager {

    private static final String USERAGENT_FILE_PATH = "fake_useragent.json";

    private static List<BrowserUserAgent> user_agents;

    @PostConstruct
    private void initialize() {
        InputStream input = null;
        try {
            input = ClassUtils.getDefaultClassLoader().getResourceAsStream(USERAGENT_FILE_PATH);
            String json = IOUtils.toString(input, StandardCharsets.UTF_8);
            user_agents = JSON.parseArray(json, BrowserUserAgent.class);
        } catch (IOException e) {
            log.error("读取 {} 文件出错", USERAGENT_FILE_PATH, e);
        } finally {
            try {
                IOUtils.close(input);
            } catch (IOException e) {
                log.error("关闭流失败", e);
            }
        }
    }

    @Data
    static class BrowserUserAgent {
        private String browser;
        private List<String> useragent = new ArrayList<>();
    }

    /**
     * 获取一个随机的 User-Agent
     *
     * @return 一个随机的 User-Agent
     */
    public String getRandom() {
        int browserIndex = RandomUtils.nextInt(0, user_agents.size());
        List<String> userAgents = user_agents.get(browserIndex).useragent;
        int useragentIndex = RandomUtils.nextInt(0, userAgents.size());
        return user_agents.get(browserIndex).useragent.get(useragentIndex);
    }
}
