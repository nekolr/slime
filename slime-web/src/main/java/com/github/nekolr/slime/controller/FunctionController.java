package com.github.nekolr.slime.controller;

import com.github.nekolr.slime.domain.Function;
import com.github.nekolr.slime.service.FunctionService;
import com.github.nekolr.slime.support.PageRequest;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/function")
public class FunctionController {

    @Resource
    private FunctionService functionService;

    @RequestMapping("/list")
    public ResponseEntity<Page<Function>> list(@RequestParam(name = "page") Integer page, @RequestParam(name = "limit") Integer size, Function function) {
        PageRequest request = new PageRequest(page, size);
        request.addDescOrder("createTime");
        return ResponseEntity.ok(functionService.findAll(function, request.toPageable()));
    }

    @RequestMapping("/save")
    public ResponseEntity save(Function function) {
        functionService.save(function);
        return ResponseEntity.ok().build();
    }

    @RequestMapping("/get")
    public ResponseEntity<Function> get(Long id) {
        return ResponseEntity.ok(functionService.getById(id));
    }

    @RequestMapping("/remove")
    public ResponseEntity remove(Long id) {
        functionService.removeById(id);
        return ResponseEntity.ok().build();
    }
}
