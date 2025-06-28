package com.aleprimo.plantilla_backend.security.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Respuesta enviada al autenticarse correctamente")
public class AuthResponse {

    @Schema(description = "JWT generado para el usuario", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6...")
    private String token;

    @Schema(description = "Nombre de usuario", example = "alejandro123")
    private String username;

    @Schema(description = "Correo electrónico del usuario", example = "alejandro@mail.com")
    private String email;
}
