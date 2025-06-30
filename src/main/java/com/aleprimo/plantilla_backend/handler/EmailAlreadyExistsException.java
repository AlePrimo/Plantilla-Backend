package com.aleprimo.plantilla_backend.handler;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String email) {
        super("El email ya está en uso: " + email);
    }
}
