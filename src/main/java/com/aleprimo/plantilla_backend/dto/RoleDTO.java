package com.aleprimo.plantilla_backend.dto;


import com.aleprimo.plantilla_backend.models.RoleName;

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

public class RoleDTO {

    @EqualsAndHashCode.Include
    Long id;
    @NotNull(message = "El nombre del rol no puede ser nulo")
    RoleName name;

}
