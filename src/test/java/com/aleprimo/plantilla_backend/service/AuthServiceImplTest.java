package com.aleprimo.plantilla_backend.service;

import com.aleprimo.plantilla_backend.security.auth.AuthRequest;
import com.aleprimo.plantilla_backend.security.auth.AuthResponse;
import com.aleprimo.plantilla_backend.security.auth.RegisterRequest;
import com.aleprimo.plantilla_backend.models.UserEntity;
import com.aleprimo.plantilla_backend.models.Role;
import com.aleprimo.plantilla_backend.models.RoleName;
import com.aleprimo.plantilla_backend.persistence.RoleDAO;
import com.aleprimo.plantilla_backend.persistence.UserDAO;
import com.aleprimo.plantilla_backend.security.jwt.JwtUtils;
import com.aleprimo.plantilla_backend.security.auth.AuthServiceImpl;
import com.aleprimo.plantilla_backend.handler.exceptions.EmailAlreadyExistsException;
import com.aleprimo.plantilla_backend.handler.exceptions.RoleNotFoundException;
import com.aleprimo.plantilla_backend.handler.exceptions.UserNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.User;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceImplTest {

    @Mock private AuthenticationManager authManager;
    @Mock private JwtUtils jwtUtils;
    @Mock private UserDAO userDAO;
    @Mock private RoleDAO roleDAO;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequest registerRequest;
    private AuthRequest authRequest;
    private UserEntity userEntity;
    private Role userRole;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        registerRequest = new RegisterRequest("ale", "ale@mail.com", "pass123");
        authRequest = new AuthRequest("ale@mail.com", "pass123");

        userRole = Role.builder()
                .id(1L)
                .name(RoleName.ROLE_USER)
                .build();

        userEntity = UserEntity.builder()
                .id(1L)
                .username("ale")
                .email("ale@mail.com")
                .password("encodedPass")
                .roles(Collections.singleton(userRole))
                .enabled(true)
                .build();
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        when(userDAO.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(roleDAO.findByName(RoleName.ROLE_USER)).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPass");
        when(userDAO.save(any(UserEntity.class))).thenReturn(userEntity);

        UserEntity result = authService.register(registerRequest);

        assertThat(result.getEmail()).isEqualTo("ale@mail.com");
        assertThat(result.getRoles()).contains(userRole);

        verify(userDAO).existsByEmail("ale@mail.com");
        verify(roleDAO).findByName(RoleName.ROLE_USER);
        verify(passwordEncoder).encode("pass123");
        verify(userDAO).save(any(UserEntity.class));
    }

    @Test
    void shouldThrowExceptionWhenEmailExists() {
        when(userDAO.existsByEmail(registerRequest.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessageContaining("ya está en uso");

        verify(userDAO).existsByEmail("ale@mail.com");
        verifyNoMoreInteractions(roleDAO, passwordEncoder, userDAO);
    }

    @Test
    void shouldThrowExceptionWhenRoleNotFound() {
        when(userDAO.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(roleDAO.findByName(RoleName.ROLE_USER)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(RoleNotFoundException.class);

        verify(userDAO).existsByEmail("ale@mail.com");
        verify(roleDAO).findByName(RoleName.ROLE_USER);
    }

    @Test
    void shouldLoginSuccessfully() {
        Authentication authMock = mock(Authentication.class);
        User springUser = new User("ale@mail.com", "encodedPass", Collections.emptyList());

        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authMock);
        when(authMock.getPrincipal()).thenReturn(springUser);
        when(jwtUtils.generateToken(springUser)).thenReturn("fake-jwt-token");
        when(userDAO.findByEmail("ale@mail.com")).thenReturn(Optional.of(userEntity));

        AuthResponse response = authService.login(authRequest);

        assertThat(response.getToken()).isEqualTo("fake-jwt-token");
        assertThat(response.getEmail()).isEqualTo("ale@mail.com");
        assertThat(response.getUsername()).isEqualTo("ale");

        verify(authManager).authenticate(any());
        verify(jwtUtils).generateToken(springUser);
        verify(userDAO).findByEmail("ale@mail.com");
    }

    @Test
    void shouldThrowExceptionWhenUserNotFoundInLogin() {
        Authentication authMock = mock(Authentication.class);
        User springUser = new User("ale@mail.com", "encodedPass", Collections.emptyList());

        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authMock);
        when(authMock.getPrincipal()).thenReturn(springUser);
        when(jwtUtils.generateToken(springUser)).thenReturn("fake-jwt-token");
        when(userDAO.findByEmail("ale@mail.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(authRequest))
                .isInstanceOf(UserNotFoundException.class);

        verify(authManager).authenticate(any());
        verify(userDAO).findByEmail("ale@mail.com");
    }
}
