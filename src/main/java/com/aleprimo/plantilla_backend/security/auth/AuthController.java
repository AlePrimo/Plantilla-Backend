package com.aleprimo.plantilla_backend.security.auth;


import com.aleprimo.plantilla_backend.dto.UserDTO;
import com.aleprimo.plantilla_backend.controller.mappers.UserMapper;
import com.aleprimo.plantilla_backend.models.UserEntity;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserMapper userMapper;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@Valid @RequestBody RegisterRequest request) {
        UserEntity newUser = authService.register(request);
        return ResponseEntity.ok(userMapper.toDto(newUser));
    }
}
