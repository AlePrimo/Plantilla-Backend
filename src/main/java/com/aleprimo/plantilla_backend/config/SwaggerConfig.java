package com.aleprimo.plantilla_backend.config;

import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.*;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.*;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI()
                .info(new Info()
                        .title("API - Plantilla Backend")
                        .description("Documentación de endpoints de la plantilla Java Spring Boot")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Alejandro")
                                .email("alejandrojuliancarullo@gmail.com")
                                .url("https://github.com/AlePrimo/Plantilla-Backend")));
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("public")
                .pathsToMatch("/api/**")
                .build();
    }
}
