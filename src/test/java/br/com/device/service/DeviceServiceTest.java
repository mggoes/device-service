package br.com.device.service;

import br.com.device.dto.DeviceData;
import br.com.device.exception.DeviceNotFoundException;
import br.com.device.mapper.DeviceDataMapper;
import br.com.device.mapper.StateMapper;
import br.com.device.model.Device;
import br.com.device.repository.DeviceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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

@SpringBootTest
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
        verify(this.repository).findById(any());
        verify(this.mapper).toDTO(any(Device.class));
    }

    @Test
    void shouldThrowExceptionWhenDeviceNotFound() {
        // Given
        when(this.repository.findById(any())).thenReturn(empty());

        // When
        assertThrows(DeviceNotFoundException.class, () -> this.service.readOne(randomUUID()));

        // Then
        verify(this.repository).findById(any());
        verify(this.mapper, never()).toDTO(any(Device.class));
    }
}
