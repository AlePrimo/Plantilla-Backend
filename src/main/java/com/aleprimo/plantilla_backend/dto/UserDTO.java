package com.aleprimo.plantilla_backend.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDateTime;
import java.util.Set;



@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Schema(description = "DTO de Usuario")
public class UserDTO {
    @Schema(description = "ID del usuario", example = "1")
    private Long id;

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min = 3, max = 50, message = "El nombre debe tener entre 3 y 50 caracteres")
    @Schema(description = "Nombre de usuario", example = "alejandro123")
    private String username;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Debe ser un email válido")
    @Schema(description = "Correo electrónico", example = "alejandro@mail.com")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    @Schema(description = "Contraseña del usuario", example = "secreta123", accessMode = Schema.AccessMode.WRITE_ONLY)
    private String password;

    @PastOrPresent(message = "La fecha de creación no puede ser futura")
    @Schema(description = "Fecha de creación del usuario", example = "2025-06-22T15:30:00")
    private LocalDateTime createdDate;

    @Schema(description = "Roles asignados al usuario", example = "[\"ADMIN\", \"USER\"]")
    private Set<String> roles;

    @Schema(description = "Indica si el usuario está habilitado", example = "true")
    private Boolean enabled;


    @Schema(description = "Nombre del creador del usuario", example = "admin")
    private String createdBy;

    @Schema(description = "Último usuario que modificó", example = "moderador01")
    private String lastModifiedBy;

    @Schema(description = "Fecha de última modificación", example = "2025-07-01T10:15:00")
    private LocalDateTime lastModifiedDate;



}
