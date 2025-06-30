package com.aleprimo.plantilla_backend.handler.exceptions;

import com.aleprimo.plantilla_backend.models.RoleName;

public class RoleNotFoundException extends RuntimeException {
    public RoleNotFoundException(String message) {
        super(message);
    }

    public RoleNotFoundException(RoleName name) {
        super("Rol no encontrado con nombre: " + name.name());
    }
}
