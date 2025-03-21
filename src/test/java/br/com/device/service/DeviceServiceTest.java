package br.com.device.service;

import br.com.device.dto.DeviceData;
import br.com.device.exception.DeviceInUseException;
import br.com.device.exception.DeviceNotFoundException;
import br.com.device.mapper.DeviceDataMapper;
import br.com.device.mapper.StateMapper;
import br.com.device.model.Device;
import br.com.device.repository.DeviceRepository;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.util.Optional;

import static br.com.device.model.State.AVAILABLE;
import static br.com.device.model.State.IN_USE;
import static java.util.List.of;
import static java.util.Optional.empty;
import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.data.domain.PageRequest.of;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@SpringBootTest
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
class DeviceServiceTest {

    @MockitoSpyBean
    private StateMapper stateMapper;

    @MockitoSpyBean
    private DeviceDataMapper mapper;

    @MockitoBean
    private DeviceRepository repository;

    @Autowired
    private DeviceService service;

    @Test
    void shouldSave() {
        // Given
        final DeviceData device = DeviceData.builder()
                .name("iPhone")
                .brand("Apple")
                .state("available")
                .build();

        when(this.repository.save(any())).thenReturn(new Device());

        // When
        final var result = this.service.save(device);

        // Then
        assertNotNull(result);

        verify(this.stateMapper).fromString(anyString());
        verify(this.mapper).toEntity(any(DeviceData.class));
        verify(this.mapper).toDTO(any(Device.class));
        verify(this.repository).save(any());
    }

    @Test
    void shouldNotSaveWhenCircuitIsOpen() {
        // Given
        final DeviceData device = DeviceData.builder()
                .name("iPhone")
                .brand("Apple")
                .state("available")
                .build();

        when(this.repository.save(any())).thenThrow(IllegalStateException.class);

        // When
        assertThrows(CallNotPermittedException.class, () -> this.service.save(device));

        // Then
        verify(this.stateMapper, times(2)).fromString(anyString());
        verify(this.mapper, times(2)).toEntity(any(DeviceData.class));
        verify(this.mapper, never()).toDTO(any(Device.class));
        verify(this.repository, times(2)).save(any());
    }

    @Test
    void shouldReadAll() {
        // Given
        final var pageable = of(0, 10);
        final var filter = DeviceData.builder().build();
        final var devices = of(
                Device.builder().name("iPhone").brand("Apple").state(AVAILABLE).build(),
                Device.builder().name("Galaxy").brand("Samsung").state(IN_USE).build()
        );
        final var page = new PageImpl<>(devices);

        when(this.repository.findAll(any(), any(Pageable.class))).thenReturn(page);

        // When
        final var result = this.service.readAll(pageable, filter);

        // Then
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());

        verify(this.mapper).toEntity(any(DeviceData.class));
        verify(this.mapper, times(2)).toDTO(any(Device.class));
        verify(this.repository).findAll(any(), any(Pageable.class));
    }

    @Test
    void shouldReadAllByBrand() {
        // Given
        final var pageable = of(0, 10);
        final var filter = DeviceData.builder().brand("Apple").build();
        final var entityFilter = Device.builder().brand("Apple").build();
        final var devices = of(Device.builder().name("iPhone").brand("Apple").state(AVAILABLE).build());
        final var page = new PageImpl<>(devices);

        when(this.repository.findAll(any(), any(Pageable.class))).thenReturn(page);

        // When
        final var result = this.service.readAll(pageable, filter);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());

        verify(this.mapper).toEntity(any(DeviceData.class));
        verify(this.mapper).toDTO(any(Device.class));
        verify(this.repository).findAll(eq(Example.of(entityFilter)), any(Pageable.class));
    }

    @Test
    void shouldReadAllByState() {
        // Given
        final var pageable = of(0, 10);
        final var filter = DeviceData.builder().state("in-use").build();
        final var entityFilter = Device.builder().state(IN_USE).build();
        final var devices = of(Device.builder().name("iPhone").brand("Apple").state(IN_USE).build());
        final var page = new PageImpl<>(devices);

        when(this.repository.findAll(any(), any(Pageable.class))).thenReturn(page);

        // When
        final var result = this.service.readAll(pageable, filter);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());

        verify(this.mapper).toEntity(any(DeviceData.class));
        verify(this.mapper).toDTO(any(Device.class));
        verify(this.repository).findAll(eq(Example.of(entityFilter)), any(Pageable.class));
    }

    @Test
    void shouldRetryReadAllWhenThereIsAnError() {
        // Given
        final var pageable = of(0, 10);
        final var filter = DeviceData.builder().build();

        when(this.repository.findAll(any(), any(Pageable.class))).thenThrow(IllegalStateException.class);

        // When
        assertThrows(IllegalStateException.class, () -> this.service.readAll(pageable, filter));

        // Then
        verify(this.mapper, times(3)).toEntity(any(DeviceData.class));
        verify(this.mapper, never()).toDTO(any(Device.class));
        verify(this.repository, times(3)).findAll(any(), any(Pageable.class));
    }

    @Test
    void shouldReadOne() {
        // Given
        final var id = randomUUID();
        final var device = Device.builder().id(id).name("Galaxy").brand("Samsung").state(IN_USE).build();

        when(this.repository.findById(any())).thenReturn(Optional.of(device));

        // When
        final var result = this.service.readOne(id);

        // Then
        assertNotNull(result);
        assertEquals(id, result.id());

        verify(this.mapper).toDTO(any(Device.class));
        verify(this.repository).findById(eq(id));
    }

    @Test
    void shouldRetryReadOneWhenThereIsAnError() {
        // Given
        when(this.repository.findById(any())).thenThrow(IllegalStateException.class);

        // When
        assertThrows(IllegalStateException.class, () -> this.service.readOne(randomUUID()));

        // Then
        verify(this.mapper, never()).toDTO(any(Device.class));
        verify(this.repository, times(3)).findById(any());
    }

    @Test
    void shouldThrowExceptionWhenDeviceNotFound() {
        // Given
        final var id = randomUUID();

        when(this.repository.findById(any())).thenReturn(empty());

        // When
        assertThrows(DeviceNotFoundException.class, () -> this.service.readOne(id));

        // Then
        verify(this.mapper, never()).toDTO(any(Device.class));
        verify(this.repository).findById(eq(id));
    }

    @Test
    void shouldUpdate() {
        // Given
        final var id = randomUUID();
        final var device = Device.builder().id(id).name("Galaxy").brand("Samsung").state(AVAILABLE).build();
        final var updatedDevice = DeviceData.builder().name("iPhone").brand("Apple").state("in-use").build();

        when(this.repository.findById(any())).thenReturn(Optional.of(device));

        // When
        final var result = this.service.update(id, updatedDevice);

        // Then
        assertNotNull(result);
        assertEquals(id, result.id());
        assertEquals("iPhone", result.name());
        assertEquals("Apple", result.brand());
        assertEquals("in-use", result.state());

        verify(this.stateMapper, times(2)).fromString(anyString());
        verify(this.mapper, times(2)).toDTO(any(Device.class));
        verify(this.mapper).toDTO(any(DeviceData.class), any(DeviceData.class));
        verify(this.mapper).toEntity(any(DeviceData.class));
        verify(this.repository).findById(eq(id));
        verify(this.repository).save(any());
    }

    @Test
    void shouldUpdateInUseWhenNameOrBrandIsNotPresent() {
        // Given
        final var id = randomUUID();
        final var device = Device.builder().id(id).name("Galaxy").brand("Samsung").state(IN_USE).build();
        final var updatedDevice = DeviceData.builder().state("available").build();

        when(this.repository.findById(any())).thenReturn(Optional.of(device));

        // When
        final var result = this.service.update(id, updatedDevice);

        // Then
        assertNotNull(result);
        assertEquals(id, result.id());
        assertEquals("available", result.state());

        verify(this.stateMapper, times(2)).fromString(anyString());
        verify(this.mapper, times(2)).toDTO(any(Device.class));
        verify(this.mapper).toDTO(any(DeviceData.class), any(DeviceData.class));
        verify(this.mapper).toEntity(any(DeviceData.class));
        verify(this.repository).findById(eq(id));
        verify(this.repository).save(any());
    }

    @ParameterizedTest
    @CsvSource({"iPhone,", ",Apple"})
    void shouldNotUpdateNameOrBrandWhenDeviceIsInUse(final String name, final String brand) {
        // Given
        final var id = randomUUID();
        final var device = Device.builder().id(id).name("Galaxy").brand("Samsung").state(IN_USE).build();
        final var updatedDevice = DeviceData.builder().name(name).brand(brand).build();

        when(this.repository.findById(any())).thenReturn(Optional.of(device));

        // When
        final var result = assertThrows(DeviceInUseException.class, () -> this.service.update(id, updatedDevice));

        // Then
        assertEquals("Name or brand cannot be changed while device is in use", result.getMessage());

        verify(this.stateMapper).fromString(anyString());
        verify(this.mapper).toDTO(any(Device.class));
        verify(this.mapper, never()).toDTO(any(DeviceData.class), any(DeviceData.class));
        verify(this.mapper, never()).toEntity(any(DeviceData.class));
        verify(this.repository).findById(eq(id));
        verify(this.repository, never()).save(any());
    }

    @Test
    void shouldDelete() {
        // Given
        final var id = randomUUID();
        final var device = Device.builder().id(id).state(AVAILABLE).build();

        when(this.repository.findById(any())).thenReturn(Optional.of(device));

        // When
        this.service.delete(id);

        // Then
        verify(this.stateMapper).fromString(anyString());
        verify(this.mapper).toDTO(any(Device.class));
        verify(this.repository).findById(eq(id));
        verify(this.repository).deleteById(eq(id));
    }

    @Test
    void shouldNotDeleteWhenDeviceIsInUse() {
        // Given
        final var id = randomUUID();
        final var device = Device.builder().id(id).state(IN_USE).build();

        when(this.repository.findById(any())).thenReturn(Optional.of(device));

        // When
        final var result = assertThrows(DeviceInUseException.class, () -> this.service.delete(id));

        // Then
        assertEquals("In use device cannot be removed", result.getMessage());

        verify(this.stateMapper).fromString(anyString());
        verify(this.mapper).toDTO(any(Device.class));
        verify(this.repository).findById(eq(id));
        verify(this.repository, never()).deleteById(eq(id));
    }
}
