package br.com.maravilhasnoticias.backend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI applicationOpenApi() {
        String scheme = "bearerAuth";
        return new OpenAPI()
                .info(new Info().title("Maravilhas Notícias API").version("v1")
                        .description("API do portal local Maravilhas Notícias"))
                .components(new Components().addSecuritySchemes(scheme,
                        new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList(scheme));
    }
}
