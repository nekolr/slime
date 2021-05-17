package com.github.nekolr.slime.domain.mapper;

import com.github.nekolr.slime.domain.Proxy;
import com.github.nekolr.slime.domain.dto.ProxyDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProxyMapper extends EntityMapper<Proxy, ProxyDTO> {

}