package br.com.device.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
import io.swagger.v3.oas.annotations.security.OAuthFlows;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

import static io.swagger.v3.oas.annotations.enums.SecuritySchemeType.OAUTH2;

@OpenAPIDefinition(info = @Info(
        title = "${springdoc.basic.title}",
        version = "${springdoc.basic.version}",
        description = "${springdoc.basic.description}",
        contact = @Contact(name = "${springdoc.basic.author}", email = "${springdoc.basic.email}")))
@SecurityScheme(name = "oauth2",
        type = OAUTH2,
        flows = @OAuthFlows(authorizationCode = @OAuthFlow(
                authorizationUrl = "${springdoc.auth-flow.authorization-url}",
                tokenUrl = "${springdoc.auth-flow.token-url}")))
public class OpenApiConfig {
}
