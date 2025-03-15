package br.com.device.exception;

public class DeviceInUseException extends RuntimeException {

    public DeviceInUseException(final String message) {
        super(message);
    }
}
