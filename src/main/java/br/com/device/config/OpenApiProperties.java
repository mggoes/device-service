package br.com.device.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("open-api")
public record OpenApiProperties(String title, String version, String description, String author, String email) {
}
