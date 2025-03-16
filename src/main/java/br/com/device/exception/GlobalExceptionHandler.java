package br.com.device.exception;

import br.com.device.dto.ErrorData;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static java.time.Instant.now;
import static java.util.List.of;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.ResponseEntity.badRequest;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(DeviceInUseException.class)
    public ErrorData handleDeviceInUseException(final DeviceInUseException exception) {
        log.error("a=handleDeviceInUseException, e=DeviceInUseException, m={}", exception.getMessage());
        return new ErrorData(now(), BAD_REQUEST.value(), of(exception.getMessage()));
    }

    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(DeviceNotFoundException.class)
    public ErrorData handleDeviceNotFoundException(final DeviceNotFoundException exception) {
        log.error("a=handleDeviceNotFoundException, e=DeviceNotFoundException, m={}", exception.getMessage());
        return new ErrorData(now(), NOT_FOUND.value(), of(NOT_FOUND.getReasonPhrase()));
    }

    @ResponseStatus(SERVICE_UNAVAILABLE)
    @ExceptionHandler(CallNotPermittedException.class)
    public ErrorData handleCallNotPermittedException(final CallNotPermittedException exception) {
        log.error("a=handleCallNotPermittedException, e=CallNotPermittedException, m={}", exception.getMessage());
        return new ErrorData(now(), SERVICE_UNAVAILABLE.value(), of(SERVICE_UNAVAILABLE.getReasonPhrase()));
    }

    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(
            final MethodArgumentNotValidException exception,
            @NonNull final HttpHeaders headers,
            @NonNull final HttpStatusCode status,
            @NonNull final WebRequest request
    ) {
        final var errors = exception.getBindingResult()
                .getAllErrors()
                .stream()
                .map(ObjectError::getDefaultMessage)
                .toList();
        log.error("a=handleMethodArgumentNotValid, e=MethodArgumentNotValidException, m={}", errors);
        return badRequest().body(new ErrorData(now(), BAD_REQUEST.value(), errors));
    }
}
