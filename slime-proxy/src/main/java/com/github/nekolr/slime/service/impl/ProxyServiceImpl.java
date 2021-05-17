package com.github.nekolr.slime.service.impl;

import com.github.nekolr.slime.dao.ProxyRepository;
import com.github.nekolr.slime.domain.Proxy;
import com.github.nekolr.slime.domain.dto.ProxyDTO;
import com.github.nekolr.slime.domain.mapper.ProxyMapper;
import com.github.nekolr.slime.service.ProxyService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

@Service
public class ProxyServiceImpl implements ProxyService {

    @Resource
    private ProxyMapper mapper;

    @Resource
    private ProxyRepository proxyRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeById(Long id) {
        proxyRepository.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProxyDTO save(ProxyDTO entity) {
        return mapper.toDto(proxyRepository.save(mapper.toEntity(entity)));
    }

    @Override
    public Page<ProxyDTO> findAll(Pageable pageable) {
        return proxyRepository.findAll(pageable).map(e -> mapper.toDto(e));
    }

    @Override
    public ProxyDTO getById(Long id) {
        Optional<Proxy> optional = proxyRepository.findById(id);
        if (optional.isPresent()) {
            return mapper.toDto(optional.get());
        }
        return null;
    }

    @Override
    public List<ProxyDTO> findAll() {
        return mapper.toDto(proxyRepository.findAll());
    }
}
