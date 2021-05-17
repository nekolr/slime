package com.github.nekolr.slime.service;

import com.github.nekolr.slime.domain.dto.ProxyDTO;

import java.util.List;

public interface ProxyService extends BaseService<ProxyDTO> {

    List<ProxyDTO> findAll();
}
