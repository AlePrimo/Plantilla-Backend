package com.aleprimo.plantilla_backend.security.auth;

import com.aleprimo.plantilla_backend.security.jwt.JwtUtils;
import com.aleprimo.plantilla_backend.models.Role;
import com.aleprimo.plantilla_backend.models.RoleName;
import com.aleprimo.plantilla_backend.models.UserEntity;
import com.aleprimo.plantilla_backend.persistence.RoleDAO;
import com.aleprimo.plantilla_backend.persistence.UserDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authManager;
    private final JwtUtils jwtUtils;
    private final UserDAO userDAO;
    private final RoleDAO roleDAO;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthResponse login(AuthRequest request) {
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtUtils.generateToken(userDetails);
        UserEntity user = userDAO.findByEmail(request.getEmail()).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }

    @Override
    public UserEntity register(RegisterRequest request) {
        if (userDAO.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está en uso");
        }

        Role roleUser = roleDAO.findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        UserEntity newUser = UserEntity.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(Collections.singleton(roleUser))
                .enabled(true)
                .build();

        return userDAO.save(newUser);
    }
}
