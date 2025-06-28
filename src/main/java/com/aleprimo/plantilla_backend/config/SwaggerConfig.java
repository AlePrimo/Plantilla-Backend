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
                        .title("🔐 API REST - Gestión de Usuarios y Roles con Spring Boot")
                        .description("""
            Esta API REST permite gestionar usuarios, autenticación y roles de manera segura, 
            utilizando Spring Boot, JWT, y buenas prácticas de arquitectura.

            Funcionalidades principales:
            - Registro y login de usuarios
            - Asignación y consulta de roles
            - Validaciones, paginación y más

            Ideal como plantilla base para aplicaciones web modernas.
            """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Alejandro Julian")
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
