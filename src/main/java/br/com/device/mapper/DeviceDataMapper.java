package br.com.device.mapper;

import br.com.device.dto.DeviceData;
import br.com.device.model.Device;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING, uses = StateMapper.class)
public interface DeviceDataMapper {

    Device toEntity(DeviceData source);

    DeviceData toDTO(Device source);

    @Mapping(target = "id", source = "fallback.id")
    @Mapping(target = "creationTime", source = "fallback.creationTime")
    @Mapping(target = "name", source = "source.name", defaultExpression = "java(fallback.name())")
    @Mapping(target = "brand", source = "source.brand", defaultExpression = "java(fallback.brand())")
    @Mapping(target = "state", source = "source.state", defaultExpression = "java(fallback.state())")
    DeviceData toDTO(DeviceData source, DeviceData fallback);
}
