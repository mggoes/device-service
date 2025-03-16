package br.com.device.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(OpenApiProperties.class)
public class OpenApiConfig {

    @Bean
    public OpenAPI openApi(final OpenApiProperties properties) {
        final var contact = new Contact();
        contact.setName(properties.author());
        contact.email(properties.email());

        final var info = new Info();
        info.title(properties.title());
        info.version(properties.version());
        info.description(properties.description());
        info.contact(contact);

        return new OpenAPI().info(info);
    }
}
