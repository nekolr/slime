package com.github.nekolr.slime.controller;

import com.github.nekolr.slime.domain.Variable;
import com.github.nekolr.slime.service.VariableService;
import com.github.nekolr.slime.support.PageRequest;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/variable")
public class VariableController {

    @Resource
    private VariableService variableService;

    @RequestMapping("/list")
    public ResponseEntity<Page<Variable>> list(@RequestParam(name = "page") Integer page, @RequestParam(name = "limit") Integer size) {
        PageRequest request = new PageRequest(page, size);
        request.addAscOrder("createTime");
        return ResponseEntity.ok(variableService.findAll(request.toPageable()));
    }

    @RequestMapping("save")
    public ResponseEntity save(Variable entity) {
        variableService.save(entity);
        return ResponseEntity.ok().build();
    }

    @RequestMapping("get")
    public ResponseEntity<Variable> get(Long id) {
        return ResponseEntity.ok(variableService.getById(id));
    }

    @RequestMapping("delete")
    public ResponseEntity delete(Long id) {
        variableService.removeById(id);
        return ResponseEntity.ok().build();
    }
}
