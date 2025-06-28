package com.aleprimo.plantilla_backend.models;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Nombre del rol de usuario")
public enum RoleName {
    ROLE_USER,
    ROLE_ADMIN
}
