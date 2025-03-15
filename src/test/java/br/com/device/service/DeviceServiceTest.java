package br.com.device.service;

import br.com.device.dto.DeviceData;
import br.com.device.mapper.DeviceDataMapper;
import br.com.device.mapper.StateMapper;
import br.com.device.model.Device;
import br.com.device.repository.DeviceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
    void shouldSaveDevice() {
        // Given
        final DeviceData device = DeviceData.builder()
                .name("Amazing Device")
                .brand("Ultimate Brand")
                .state("available")
                .build();

        when(this.repository.save(any())).thenReturn(new Device());

        // When
        final var result = this.service.save(device);

        // Then
        assertNotNull(result);
        verify(this.stateMapper, times(1)).fromString(anyString());
        verify(this.mapper, times(1)).toEntity(any(DeviceData.class));
        verify(this.repository, times(1)).save(any());
    }
}
