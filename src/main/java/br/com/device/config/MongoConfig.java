package br.com.device.config;

import br.com.device.model.Device;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertCallback;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import static java.util.Optional.ofNullable;
import static java.util.UUID.randomUUID;

@Configuration
@EnableMongoAuditing
@EnableMongoRepositories("br.com.device.repository")
public class MongoConfig {

    @Bean
    public BeforeConvertCallback<Device> fillDeviceIdCallback() {
        return (device, _) -> {
            final var id = ofNullable(device.getId()).orElse(randomUUID());
            device.setId(id);
            return device;
        };
    }
}
