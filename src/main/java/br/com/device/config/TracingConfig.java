package br.com.device.config;

import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter.builder;

@Configuration
public class TracingConfig {

    @Bean
    public OtlpGrpcSpanExporter spanExporter(@Value("${management.otlp.tracing.endpoint}") final String url) {
        return builder().setEndpoint(url).build();
    }
}
