package br.com.device.repository;

import br.com.device.model.Device;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface DeviceRepository extends MongoRepository<Device, UUID> {
}
