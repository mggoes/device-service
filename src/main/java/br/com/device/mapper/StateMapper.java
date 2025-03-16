package br.com.device.mapper;

import br.com.device.model.State;
import org.mapstruct.Mapper;

import static br.com.device.model.State.values;
import static java.util.Arrays.stream;
import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface StateMapper {

    default String toString(final State source) {
        return source.getDisplayName();
    }

    default State fromString(final String source) {
        return stream(values())
                .filter(state -> state.getDisplayName().equals(source))
                .findFirst()
                .orElse(null);
    }
}
