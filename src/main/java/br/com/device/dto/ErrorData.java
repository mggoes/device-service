package br.com.device.dto;

import java.time.Instant;
import java.util.List;

public record ErrorData(Instant timestamp, int status, List<String> errors) {
}
