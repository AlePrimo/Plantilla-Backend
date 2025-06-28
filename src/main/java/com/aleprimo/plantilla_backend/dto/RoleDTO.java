package com.aleprimo.plantilla_backend.dto;


import com.aleprimo.plantilla_backend.models.RoleName;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Schema(description = "DTO de Role")
public class RoleDTO {

    @EqualsAndHashCode.Include
    @Schema(description = "ID del Rol", example = "1")
    Long id;
    @NotNull(message = "El nombre del rol no puede ser nulo")
    @Schema(description = "Nombre del Rol", example = "ROLE_USER")
    RoleName name;

}
