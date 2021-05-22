package com.github.nekolr.slime.job;

import com.github.nekolr.slime.config.ProxyConfig;
import com.github.nekolr.slime.domain.dto.ProxyDTO;
import com.github.nekolr.slime.service.ProxyService;
import com.github.nekolr.slime.support.ProxyManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.task.TaskSchedulerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class ProxyCleanJob {

    private ThreadPoolExecutor pool;

    private ScheduledFuture<?> future;

    @Resource
    private ProxyConfig proxyConfig;

    @Resource
    private ProxyService proxyService;

    @Resource
    private ProxyManager proxyManager;

    /**
     * 在使用了 @EnableWebSocket 之后，自动配置类就不会生效，需要手动创建
     */
    @Resource(name = "proxyCleanThreadPoolTaskScheduler")
    private ThreadPoolTaskScheduler scheduler;


    @Bean
    public ThreadPoolTaskScheduler proxyCleanThreadPoolTaskScheduler(TaskSchedulerBuilder builder) {
        return builder.build();
    }

    @PostConstruct
    public void initialize() {
        pool = new ThreadPoolExecutor(8, 8, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
    }


    public void run() {
        // 延迟 10 秒再开始执行
        Calendar now = Calendar.getInstance();
        now.add(Calendar.MILLISECOND, 10000);
        future = scheduler.scheduleWithFixedDelay(this::clean, now.getTime(), proxyConfig.getCheckInterval().toMillis());
    }

    public void stop() {
        if (future != null) {
            future.cancel(true);
            future = null;
        }
    }

    public boolean running() {
        if (future != null) {
            return !future.isDone();
        } else {
            return false;
        }
    }

    public void clean() {
        log.debug("开始检测内置代理 IP 的有效性");
        List<ProxyDTO> proxies = proxyService.findAll();
        if (!proxies.isEmpty()) {
            for (ProxyDTO proxy : proxies) {
                pool.submit(() -> {
                    if (proxyManager.check(proxy) == -1) {
                        proxyManager.remove(proxy);
                    } else {
                        // 更新验证时间
                        proxy.setValidTime(new Date());
                        proxyService.save(proxy);
                    }
                });
            }
        }
        log.debug("代理 IP 有效性检测完毕");
    }
}
