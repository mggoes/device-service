package br.com.device.controller;

import br.com.device.dto.DeviceData;
import br.com.device.dto.DeviceData.BasicInfo;
import br.com.device.dto.DeviceData.StateInfo;
import br.com.device.service.DeviceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/devices")
public class DeviceController {

    private final DeviceService service;

    @PostMapping
    @ResponseStatus(CREATED)
    public DeviceData create(@RequestBody @Validated(BasicInfo.class) final DeviceData request) {
        log.info("a=create, r={}", request);
        return this.service.save(request);
    }

    @GetMapping
    public Page<DeviceData> readAll(@PageableDefault final Pageable pageable, final DeviceData filter) {
        log.info("a=readAll, p={}, f={}", pageable, filter);
        return this.service.readAll(pageable, filter);
    }

    @GetMapping("/{id}")
    public DeviceData readOne(@PathVariable final UUID id) {
        log.info("a=readOne, id={}", id);
        return this.service.readOne(id);
    }

    @PutMapping("/{id}")
    public DeviceData update(@PathVariable final UUID id, @RequestBody @Validated(BasicInfo.class) final DeviceData request) {
        log.info("a=update, id={}, r={}", id, request);
        return this.service.update(id, request);
    }

    @PatchMapping("/{id}")
    public DeviceData partiallyUpdate(@PathVariable final UUID id, @RequestBody @Validated(StateInfo.class) final DeviceData request) {
        log.info("a=partiallyUpdate, id={}, r={}", id, request);
        return this.service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    public void delete(@PathVariable final UUID id) {
        log.info("a=delete, id={}", id);
        this.service.delete(id);
    }
}
