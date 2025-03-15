package br.com.device.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;
import java.util.UUID;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;
import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME;

@Builder
public record DeviceData(
        @JsonProperty(access = READ_ONLY)
        UUID id,

        @NotBlank(message = "Device name is required", groups = BasicInfo.class)
        String name,

        @NotBlank(message = "Device brand is required", groups = BasicInfo.class)
        String brand,

        @NotBlank(message = "Device state is required", groups = BasicInfo.class)
        @Pattern(regexp = "available|in-use|inactive", message = "State must be one of the following: available, in-use, inactive", groups = {BasicInfo.class, StateInfo.class})
        String state,

        @JsonProperty(access = READ_ONLY)
        @DateTimeFormat(iso = DATE_TIME)
        Instant creationTime) {

    public interface BasicInfo {
    }

    public interface StateInfo {
    }
}


