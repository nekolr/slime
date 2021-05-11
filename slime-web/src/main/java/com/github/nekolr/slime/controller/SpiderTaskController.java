package com.github.nekolr.slime.controller;

import com.github.nekolr.slime.context.SpiderContext;
import com.github.nekolr.slime.domain.SpiderTask;
import com.github.nekolr.slime.job.SpiderJob;
import com.github.nekolr.slime.service.SpiderTaskService;
import com.github.nekolr.slime.support.PageRequest;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/task")
public class SpiderTaskController {

    @Resource
    private SpiderTaskService spiderTaskService;

    @RequestMapping("/list")
    public ResponseEntity<Page<SpiderTask>> list(@RequestParam(name = "page") Integer page, @RequestParam(name = "limit") Integer size, SpiderTask task) {
        PageRequest request = new PageRequest(page, size);
        request.addDescOrder("endTime");
        return ResponseEntity.ok(spiderTaskService.findAll(task, request.toPageable()));
    }

    @RequestMapping("/remove")
    public ResponseEntity remove(Long id) {
        // 删除任务记录之前先停止
        SpiderContext context = SpiderJob.getSpiderContext(id);
        if (context != null) {
            context.setRunning(false);
        }
        spiderTaskService.removeById(id);
        return ResponseEntity.ok().build();
    }
}
