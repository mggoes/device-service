package br.com.device.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum State {
    AVAILABLE("available"),
    IN_USE("in-use"),
    INACTIVE("inactive");

    private final String displayName;
}
