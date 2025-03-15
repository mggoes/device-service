package br.com.device.service;

import br.com.device.dto.DeviceData;
import br.com.device.exception.DeviceInUseException;
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

import static br.com.device.model.State.IN_USE;
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
     * Reads one device using the provided id.
     *
     * @param id device identifier.
     * @return device data.
     * @throws DeviceNotFoundException if device does not exist.
     */
    public DeviceData readOne(final UUID id) {
        return this.repository.findById(id)
                .map(this.mapper::toDTO)
                .orElseThrow(DeviceNotFoundException::new);
    }

    /**
     * Updates a device partially or fully using the provided data.
     *
     * @param id     device identifier.
     * @param device data that should be replaced.
     * @return updated device.
     * @throws DeviceNotFoundException if device does not exist.
     * @throws DeviceInUseException    if there is an attempt to update the name or brand and device is in the {@link br.com.device.model.State#IN_USE IN_USE} state.
     */
    public DeviceData update(final UUID id, final DeviceData device) {
        final var foundDevice = this.readOne(id);
        if (this.isInUse(foundDevice) && (device.name() != null || device.brand() != null)) {
            throw new DeviceInUseException("Name or brand cannot be changed while device is in use");
        }
        final var deviceToUpdate = this.mapper.toDTO(device, foundDevice);
        log.info("a=update, d={}", deviceToUpdate);
        return this.save(deviceToUpdate);
    }

    /**
     * Deletes a device using the provided id.
     *
     * @param id device identifier.
     * @throws DeviceNotFoundException if device does not exist.
     * @throws DeviceInUseException    if device is in the {@link br.com.device.model.State#IN_USE IN_USE} state.
     */
    public void delete(final UUID id) {
        final var device = this.readOne(id);
        if (this.isInUse(device)) throw new DeviceInUseException("In use device cannot be removed");
        this.repository.deleteById(id);
    }

    private boolean isInUse(final DeviceData device) {
        return IN_USE == this.stateMapper.fromString(device.state());
    }
}
