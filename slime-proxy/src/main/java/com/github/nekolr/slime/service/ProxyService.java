package com.github.nekolr.slime.service;

import com.github.nekolr.slime.domain.dto.ProxyDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProxyService {

    List<ProxyDTO> findAll();

    void removeById(Long id);

    ProxyDTO getById(Long id);

    ProxyDTO save(ProxyDTO entity);

    Page<ProxyDTO> findAll(Pageable pageable);
}
