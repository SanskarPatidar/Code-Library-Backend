package com.sanskar.Code.Library.Backend.config;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI myCustomOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .name("Authorization")
                                        .description("JWT Bearer token authorization")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT") // Optional: for UI only
                        )
                )
                .info(new Info()
                        .title("CodeLibrary API")
                        .description("API documentation for the CodeLibrary app")
                        .contact(new Contact()
                                .name("Sanskar Patidar")
                                .email("sanskarpatidar00@gmail.com")
                                .url("https://leetcode.com/u/sanskarpatidar/"))
                );

    }
}
/*
.servers(List.of(
        new Server().url("http://localhost:8080").description("Local Server"),
        new Server().url("https://api.codelibrary.com").description("Production Server")
    ))
 */
