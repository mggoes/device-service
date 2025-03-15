package br.com.device.mapper;

import br.com.device.model.State;
import org.mapstruct.Mapper;

import static java.util.Optional.ofNullable;
import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface StateMapper {

    default String toString(State source) {
        return source.name().replace("_", "-").toLowerCase();
    }

    default State fromString(String source) {
        return ofNullable(source)
                .map(it -> it.replace("-", "_").toUpperCase())
                .map(State::valueOf)
                .orElse(null);
    }
}
