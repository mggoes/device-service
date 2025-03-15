package br.com.device.service;

import br.com.device.dto.DeviceData;
import br.com.device.exception.DeviceNotFoundException;
import br.com.device.mapper.DeviceDataMapper;
import br.com.device.mapper.StateMapper;
import br.com.device.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static org.springframework.data.domain.Example.of;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceService {

    private final StateMapper stateMapper;
    private final DeviceDataMapper mapper;
    private final DeviceRepository repository;

    public DeviceData save(final DeviceData device) {
        final var entity = this.mapper.toEntity(device);
        log.info("a=save, e={}", entity);
        this.repository.save(entity);
        log.info("a=save, e={}", entity);
        return this.mapper.toDTO(entity);
    }

    public Page<DeviceData> readAll(final Pageable pageable, final DeviceData filter) {
        final var entityFilter = this.mapper.toEntity(filter);
        log.info("a=readAll, f={}", entityFilter);
        return this.repository.findAll(of(entityFilter), pageable)
                .map(this.mapper::toDTO);
    }

    /**
     * Reads one device using provided id.
     *
     * @param id device identifier.
     * @return device data.
     * @throws DeviceNotFoundException when device does not exist.
     */
    public DeviceData readOne(final UUID id) {
        return this.repository.findById(id)
                .map(this.mapper::toDTO)
                .orElseThrow(DeviceNotFoundException::new);
    }
}
