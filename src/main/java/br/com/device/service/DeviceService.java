package br.com.device.service;

import br.com.device.dto.DeviceData;
import br.com.device.mapper.DeviceDataMapper;
import br.com.device.mapper.StateMapper;
import br.com.device.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
}
