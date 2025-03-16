package br.com.device.controller;

import br.com.device.config.MongoPopulatorConfig;
import br.com.device.dto.DeviceData;
import br.com.device.repository.DeviceRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static java.time.Instant.now;
import static java.util.UUID.fromString;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(MongoPopulatorConfig.class)
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
class DeviceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private DeviceRepository repository;

    @Test
    void shouldCreate() throws Exception {
        // Given
        final var device = DeviceData.builder()
                .name("Zenfone")
                .brand("Asus")
                .state("available")
                .build();

        // When and then
        this.mockMvc.perform(post("/devices")
                        .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                        .header(ACCEPT, APPLICATION_JSON_VALUE)
                        .content(this.mapper.writeValueAsString(device)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").value("Zenfone"))
                .andExpect(jsonPath("$.brand").value("Asus"))
                .andExpect(jsonPath("$.state").value("available"))
                .andExpect(jsonPath("$.creationTime").isNotEmpty());
    }

    @Test
    void shouldNotCreateWhenRequiredFieldsAreMissing() throws Exception {
        // Given
        final var device = DeviceData.builder()
                .brand("Asus")
                .build();

        // When and then
        this.mockMvc.perform(post("/devices")
                        .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                        .header(ACCEPT, APPLICATION_JSON_VALUE)
                        .content(this.mapper.writeValueAsString(device)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[*]").value(containsInAnyOrder("Device name is required", "Device state is required")));
    }

    @Test
    void shouldReadAll() throws Exception {
        // When and then
        this.mockMvc.perform(get("/devices")
                        .header(ACCEPT, APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(4)))
                .andExpect(jsonPath("$.totalElements").value(4))
                .andExpect(jsonPath("$.content[*].id").isNotEmpty())
                .andExpect(jsonPath("$.content[*].name").isNotEmpty())
                .andExpect(jsonPath("$.content[*].brand").isNotEmpty())
                .andExpect(jsonPath("$.content[*].state").isNotEmpty())
                .andExpect(jsonPath("$.content[*].creationTime").isNotEmpty());
    }

    @Test
    void shouldReadAllPaginated() throws Exception {
        // When and then
        this.mockMvc.perform(get("/devices")
                        .header(ACCEPT, APPLICATION_JSON_VALUE)
                        .queryParam("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements").value(4))
                .andExpect(jsonPath("$.content[*].id").isNotEmpty())
                .andExpect(jsonPath("$.content[*].name").isNotEmpty())
                .andExpect(jsonPath("$.content[*].brand").isNotEmpty())
                .andExpect(jsonPath("$.content[*].state").isNotEmpty())
                .andExpect(jsonPath("$.content[*].creationTime").isNotEmpty());
    }

    @Test
    void shouldReadAllByBrand() throws Exception {
        // When and then
        this.mockMvc.perform(get("/devices")
                        .header(ACCEPT, APPLICATION_JSON_VALUE)
                        .queryParam("brand", "Apple"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].id").value("2db19102-8bbc-43b6-afd2-993263ae6d1e"))
                .andExpect(jsonPath("$.content[0].name").value("iPhone"))
                .andExpect(jsonPath("$.content[0].brand").value("Apple"))
                .andExpect(jsonPath("$.content[0].state").value("available"))
                .andExpect(jsonPath("$.content[0].creationTime").value("2025-03-15T21:50:41.159Z"));
    }

    @Test
    void shouldReadAllByState() throws Exception {
        // When and then
        this.mockMvc.perform(get("/devices")
                        .header(ACCEPT, APPLICATION_JSON_VALUE)
                        .queryParam("state", "available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.content[*].id").value(containsInAnyOrder("2db19102-8bbc-43b6-afd2-993263ae6d1e", "0fa8debc-bd30-4bc1-8737-f8eb42ca88a8")))
                .andExpect(jsonPath("$.content[*].name").value(containsInAnyOrder("iPhone", "Redmi")))
                .andExpect(jsonPath("$.content[*].brand").value(containsInAnyOrder("Apple", "Xiaomi")))
                .andExpect(jsonPath("$.content[*].state").value(containsInAnyOrder("available", "available")))
                .andExpect(jsonPath("$.content[*].creationTime").value(containsInAnyOrder("2025-03-15T21:50:41.159Z", "2025-03-15T21:52:06.527Z")));
    }

    @Test
    void shouldReadOne() throws Exception {
        // When and then
        this.mockMvc.perform(get("/devices/8a37328e-8569-4810-af11-2e85cc67cbcb")
                        .header(ACCEPT, APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("8a37328e-8569-4810-af11-2e85cc67cbcb"))
                .andExpect(jsonPath("$.name").value("Edge"))
                .andExpect(jsonPath("$.brand").value("Motorola"))
                .andExpect(jsonPath("$.state").value("inactive"))
                .andExpect(jsonPath("$.creationTime").value("2025-03-15T21:51:34.789Z"));
    }

    @Test
    void shouldReturnErrorWhenDeviceNotFound() throws Exception {
        // When and then
        this.mockMvc.perform(get("/devices/28c0cf3c-c0c3-465e-9e65-8a24a06b4cab")
                        .header(ACCEPT, APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0]").value("Not Found"));
    }

    @Test
    void shouldUpdate() throws Exception {
        // Given
        final var device = DeviceData.builder()
                .name("iPhone Updated")
                .brand("Apple Updated")
                .state("in-use")
                .creationTime(now())
                .build();

        // When and then
        this.mockMvc.perform(put("/devices/2db19102-8bbc-43b6-afd2-993263ae6d1e")
                        .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                        .header(ACCEPT, APPLICATION_JSON_VALUE)
                        .content(this.mapper.writeValueAsString(device)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("2db19102-8bbc-43b6-afd2-993263ae6d1e"))
                .andExpect(jsonPath("$.name").value("iPhone Updated"))
                .andExpect(jsonPath("$.brand").value("Apple Updated"))
                .andExpect(jsonPath("$.state").value("in-use"))
                .andExpect(jsonPath("$.creationTime").value("2025-03-15T21:50:41.159Z"));
    }

    @Test
    void shouldNotUpdateWhenRequiredFieldsAreMissing() throws Exception {
        // Given
        final var device = DeviceData.builder()
                .name("iPhone Updated")
                .state("inactive")
                .build();

        // When and then
        this.mockMvc.perform(put("/devices/2db19102-8bbc-43b6-afd2-993263ae6d1e")
                        .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                        .header(ACCEPT, APPLICATION_JSON_VALUE)
                        .content(this.mapper.writeValueAsString(device)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]").value("Device brand is required"));
    }

    @Test
    void shouldPartiallyUpdate() throws Exception {
        // Given
        final var device = DeviceData.builder()
                .brand("Apple Updated")
                .creationTime(now())
                .build();

        // When and then
        this.mockMvc.perform(patch("/devices/2db19102-8bbc-43b6-afd2-993263ae6d1e")
                        .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                        .header(ACCEPT, APPLICATION_JSON_VALUE)
                        .content(this.mapper.writeValueAsString(device)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("2db19102-8bbc-43b6-afd2-993263ae6d1e"))
                .andExpect(jsonPath("$.name").value("iPhone"))
                .andExpect(jsonPath("$.brand").value("Apple Updated"))
                .andExpect(jsonPath("$.state").value("available"))
                .andExpect(jsonPath("$.creationTime").value("2025-03-15T21:50:41.159Z"));
    }

    @Test
    void shouldNotPartiallyUpdateWhenStateIsInvalid() throws Exception {
        // Given
        final var device = DeviceData.builder()
                .state("invalid")
                .build();

        // When and then
        this.mockMvc.perform(patch("/devices/2db19102-8bbc-43b6-afd2-993263ae6d1e")
                        .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                        .header(ACCEPT, APPLICATION_JSON_VALUE)
                        .content(this.mapper.writeValueAsString(device)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]").value("State must be one of the following: available, in-use, inactive"));
    }

    @Test
    void shouldNotPartiallyUpdateCreationDate() throws Exception {
        // Given
        final var device = DeviceData.builder()
                .creationTime(now())
                .build();

        // When and then
        this.mockMvc.perform(patch("/devices/2db19102-8bbc-43b6-afd2-993263ae6d1e")
                        .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                        .header(ACCEPT, APPLICATION_JSON_VALUE)
                        .content(this.mapper.writeValueAsString(device)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("2db19102-8bbc-43b6-afd2-993263ae6d1e"))
                .andExpect(jsonPath("$.name").value("iPhone"))
                .andExpect(jsonPath("$.brand").value("Apple"))
                .andExpect(jsonPath("$.state").value("available"))
                .andExpect(jsonPath("$.creationTime").value("2025-03-15T21:50:41.159Z"));
    }

    @ParameterizedTest
    @CsvSource({"Galaxy Updated,", ",Samsung Updated"})
    void shouldNotPartiallyUpdateNameOrBrandWhenDeviceIsInUse(final String name, final String brand) throws Exception {
        // Given
        final var device = DeviceData.builder()
                .name(name)
                .brand(brand)
                .build();

        // When and then
        this.mockMvc.perform(patch("/devices/a5225c14-29b4-4b42-bf5b-a09b257b57fb")
                        .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                        .header(ACCEPT, APPLICATION_JSON_VALUE)
                        .content(this.mapper.writeValueAsString(device)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]").value("Name or brand cannot be changed while device is in use"));
    }

    @Test
    void shouldDelete() throws Exception {
        // Given
        final var id = fromString("2db19102-8bbc-43b6-afd2-993263ae6d1e");

        // When
        this.mockMvc.perform(delete("/devices/{id}", id))
                .andExpect(status().isNoContent());

        // Then
        assertFalse(this.repository.existsById(id));
    }

    @Test
    void shouldNotDeleteWhenDeviceIsInUse() throws Exception {
        // Given
        final var id = fromString("a5225c14-29b4-4b42-bf5b-a09b257b57fb");

        // When
        this.mockMvc.perform(delete("/devices/{id}", id))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]").value("In use device cannot be removed"));

        // Then
        assertTrue(this.repository.existsById(id));
    }
}
