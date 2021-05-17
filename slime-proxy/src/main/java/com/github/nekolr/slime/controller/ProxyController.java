package com.github.nekolr.slime.controller;

import com.github.nekolr.slime.domain.dto.ProxyDTO;
import com.github.nekolr.slime.job.ProxyCleanJob;
import com.github.nekolr.slime.service.ProxyService;
import com.github.nekolr.slime.support.PageRequest;
import com.github.nekolr.slime.support.ProxyManager;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/proxy")
public class ProxyController {

    @Resource
    private ProxyCleanJob cleanJob;

    @Resource
    private ProxyManager proxyManager;

    @Resource
    private ProxyService proxyService;

    @RequestMapping("/list")
    public ResponseEntity<Page<ProxyDTO>> list(@RequestParam(name = "page") Integer page, @RequestParam(name = "limit") Integer size) {
        PageRequest request = new PageRequest(page, size);
        return ResponseEntity.ok(proxyService.findAll(request.toPageable()));
    }

    @RequestMapping("/save")
    public ResponseEntity<Boolean> save(ProxyDTO proxy) {
        return ResponseEntity.ok(proxyManager.add(proxy));
    }

    @RequestMapping("/get")
    public ResponseEntity<ProxyDTO> get(Long id) {
        ProxyDTO proxy = proxyService.getById(id);
        return ResponseEntity.ok(proxy);
    }

    @RequestMapping("/remove")
    public ResponseEntity remove(Long id) {
        ProxyDTO proxy = proxyService.getById(id);
        if (proxy != null) {
            proxyManager.remove(proxy);
        }
        return ResponseEntity.ok().build();
    }

    @RequestMapping("/test")
    public ResponseEntity<Long> test(ProxyDTO proxy) {
        return ResponseEntity.ok(proxyManager.check(proxy));
    }

    @RequestMapping("/cleanIsRunning")
    public ResponseEntity<Boolean> cleanIsRunning() {
        return ResponseEntity.ok(cleanJob.running());
    }

    @RequestMapping("/startClean")
    public ResponseEntity startClean() {
        if (!cleanJob.running()) {
            cleanJob.run();
        }
        return ResponseEntity.ok().build();
    }

    @RequestMapping("/stopClean")
    public ResponseEntity stopClean() {
        if (cleanJob.running()) {
            cleanJob.stop();
        }
        return ResponseEntity.ok().build();
    }
}
