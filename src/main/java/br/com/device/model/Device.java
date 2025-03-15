package br.com.device.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@Document(collection = "devices")
public class Device {

    @MongoId
    private UUID id;

    private String name;

    private String brand;

    private State state;

    @CreatedDate
    private Instant creationTime;
}
