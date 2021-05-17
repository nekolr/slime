package com.github.nekolr.slime.support;

import com.github.nekolr.slime.config.ProxyConfig;
import com.github.nekolr.slime.domain.dto.ProxyDTO;
import com.github.nekolr.slime.service.ProxyService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Component
public class ProxyManager {

    @Resource
    private ProxyConfig proxyConfig;

    @Resource
    private ProxyService proxyService;


    private List<ProxyDTO> proxies = new CopyOnWriteArrayList<>();


    @PostConstruct
    private void initialize() {
        this.proxies.addAll(proxyService.findAll());
    }

    public void remove(ProxyDTO proxy) {
        this.proxies.remove(proxy);
        this.proxyService.removeById(proxy.getId());
    }

    public boolean add(ProxyDTO proxy) {
        if (proxy.getId() != null && this.proxies.contains(proxy)) {
            return true;
        }
        if (check(proxy) != -1) {
            ProxyDTO entity = proxyService.save(proxy);
            this.proxies.add(entity);
            return true;
        }
        return false;
    }

    /**
     * 随机获取一个 http 代理
     *
     * @return
     */
    public ProxyDTO getHttpProxy() {
        return getHttpProxy(true);
    }

    /**
     * 随机获取一个 https 代理
     *
     * @return
     */
    public ProxyDTO getHttpsProxy() {
        return getHttpsProxy(true);
    }

    /**
     * 随机获取一个 http 代理
     *
     * @return
     */
    public ProxyDTO getHttpProxy(boolean anonymous) {
        return random(get("http", anonymous));
    }

    /**
     * 随机获取一个 HTTPS 代理
     *
     * @return
     */
    public ProxyDTO getHttpsProxy(boolean anonymous) {
        return random(get("https", anonymous));
    }

    /**
     * 从代理列表中随机返回一个代理
     *
     * @param proxies 代理列表
     * @return 一个代理
     */
    private ProxyDTO random(List<ProxyDTO> proxies) {
        int size;
        if (proxies != null && (size = proxies.size()) > 0) {
            return proxies.get(RandomUtils.nextInt(0, size));
        }
        return null;
    }

    /**
     * 查询符合条件的代理 IP 列表
     *
     * @param type      代理类型 http 或 https
     * @param anonymous 是否支持匿名连接
     * @return 代理 IP 列表
     */
    private List<ProxyDTO> get(String type, boolean anonymous) {
        List<ProxyDTO> proxyList = new ArrayList<>();
        if (this.proxies != null) {
            for (ProxyDTO proxy : this.proxies) {
                if (type.equalsIgnoreCase(proxy.getType()) && proxy.getAnonymous().equals(anonymous)) {
                    proxyList.add(proxy);
                }
            }
        }
        return proxyList;
    }

    /**
     * 检测代理
     *
     * @param proxy 代理
     * @return 延迟时间
     */
    public long check(ProxyDTO proxy) {
        try {
            long st = System.currentTimeMillis();
            Jsoup.connect(proxyConfig.getCheckUrl())
                    .ignoreContentType(true)
                    .ignoreHttpErrors(true)
                    .timeout(proxyConfig.getCheckTimeout())
                    .proxy(proxy.getIp(), proxy.getPort())
                    .execute();
            st = System.currentTimeMillis() - st;
            log.info("检测代理：{}:{}，延迟：{} ms", proxy.getIp(), proxy.getPort(), st);
            return st;
        } catch (Exception e) {
            log.info("检测代理：{}:{}，超时", proxy.getIp(), proxy.getPort());
            return -1;
        }
    }

}
