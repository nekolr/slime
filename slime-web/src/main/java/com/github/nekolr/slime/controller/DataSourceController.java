package com.github.nekolr.slime.controller;

import com.github.nekolr.slime.domain.dto.DataSourceDTO;
import com.github.nekolr.slime.domain.dto.DataSourceDTO.Test;
import com.github.nekolr.slime.domain.dto.DataSourceDTO.Save;
import com.github.nekolr.slime.service.DataSourceService;
import com.github.nekolr.slime.support.PageRequest;
import com.github.nekolr.slime.support.DataSourceManager;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/datasource")
public class DataSourceController {

    @Resource
    private DataSourceService dataSourceService;

    @Resource
    private DataSourceManager dataSourceManager;

    @GetMapping("/list")
    public ResponseEntity<Page<DataSourceDTO>> list(@RequestParam(name = "page") Integer page, @RequestParam(name = "limit") Integer size) {
        PageRequest request = new PageRequest(page, size);
        request.addAscOrder("createTime");
        return ResponseEntity.ok(dataSourceService.findAll(request.toPageable()));
    }

    @GetMapping("/all")
    public ResponseEntity<List<DataSourceDTO>> all() {
        return ResponseEntity.ok(dataSourceService.findAll());
    }

    @PostMapping("/save")
    public ResponseEntity save(@Validated(value = Save.class) DataSourceDTO dataSource) {
        if (dataSource.getId() != null) {
            dataSourceManager.remove(dataSource.getId());
        }
        dataSourceService.save(dataSource);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/get")
    public ResponseEntity<DataSourceDTO> get(Long id) {
        DataSourceDTO dataSource = dataSourceService.getById(id);
        dataSource.setPassword(null);
        return ResponseEntity.ok(dataSource);
    }

    @PostMapping("/remove")
    public ResponseEntity remove(Long id) {
        dataSourceManager.remove(id);
        dataSourceService.removeById(id);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/test")
    public ResponseEntity test(@Validated(value = Test.class) DataSourceDTO dataSource) {
        dataSourceService.test(dataSource);
        return ResponseEntity.ok().build();
    }
}
