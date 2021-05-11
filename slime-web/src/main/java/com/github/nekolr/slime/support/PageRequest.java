package com.github.nekolr.slime.support;

import lombok.Getter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

/**
 * 分页请求
 */
@Getter
public class PageRequest {

    /**
     * 页号，第一页为 1
     */
    private int page;

    /**
     * 每页记录数
     */
    private int size;

    /**
     * 排序列表
     */
    private List<Sort.Order> sorts = new ArrayList<>();


    public PageRequest() {
        this.page = 1;
        this.size = 10;
    }

    public PageRequest(int page, int size) {
        // 当传递页号小于等于 0 时，默认为第一页
        if (page <= 0) {
            page = 1;
        }

        // 当传递每页记录数小于 1 时，默认为 1
        if (size < 1) {
            size = 1;
        }

        this.page = page;
        this.size = size;
    }

    public void setPage(int page) {
        if (page <= 0) {
            page = 1;
        }
        this.page = page;
    }

    public void setSize(int size) {
        if (size < 1) {
            size = 1;
        }
        this.size = size;
    }

    /**
     * 添加一个排序
     *
     * @param property 排序字段
     */
    public void addAscOrder(String property) {
        sorts.add(new Sort.Order(Sort.Direction.ASC, property));
    }

    /**
     * 添加一个排序
     *
     * @param property 排序字段
     */
    public void addDescOrder(String property) {
        sorts.add(new Sort.Order(Sort.Direction.DESC, property));
    }


    /**
     * 生成 Pageable
     *
     * @return
     */
    public Pageable toPageable() {
        return org.springframework.data.domain.PageRequest.of(this.page - 1, this.size, Sort.by(sorts));
    }
}
