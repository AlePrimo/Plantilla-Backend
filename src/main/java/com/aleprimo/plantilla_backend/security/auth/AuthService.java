package com.aleprimo.plantilla_backend.security.auth;

import com.aleprimo.plantilla_backend.models.UserEntity;

public interface AuthService {
    AuthResponse login(AuthRequest request);
    UserEntity register(RegisterRequest request);
}
