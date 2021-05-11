package com.github.nekolr.slime.domain.mapper;

import com.github.nekolr.slime.domain.DataSource;
import com.github.nekolr.slime.domain.dto.DataSourceDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DataSourceMapper extends EntityMapper<DataSource, DataSourceDTO> {

}