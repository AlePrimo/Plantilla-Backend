package com.aleprimo.plantilla_backend.service;
import com.aleprimo.plantilla_backend.models.UserEntity;
import com.aleprimo.plantilla_backend.models.Role;
import com.aleprimo.plantilla_backend.models.RoleName;
import com.aleprimo.plantilla_backend.persistence.UserDAO;
import com.aleprimo.plantilla_backend.persistence.RoleDAO;
import com.aleprimo.plantilla_backend.entityServices.implementations.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserDAO userDAO;

    @Mock
    private RoleDAO roleDAO;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private UserEntity user;
    private Role role;

    @BeforeEach
    void setUp() {
        role = Role.builder()
                .id(1L)
                .name(RoleName.ROLE_USER)
                .build();

        user = UserEntity.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("123456")
                .roles(Set.of(role))
                .enabled(true)
                .createdDate(LocalDateTime.now())
                .build();
    }

    @Test
    void shouldSaveUserSuccessfully() {
        when(userDAO.existsByUsername(user.getUsername())).thenReturn(false);
        when(roleDAO.findByName(RoleName.ROLE_USER)).thenReturn(Optional.of(role));
        when(passwordEncoder.encode(user.getPassword())).thenReturn("hashed");
        when(userDAO.save(any(UserEntity.class))).thenReturn(user);

        UserEntity result = userService.save(user);

        assertThat(result).isNotNull();
        verify(userDAO).save(any(UserEntity.class));
    }

    @Test
    void shouldThrowWhenUsernameExists() {
        when(userDAO.existsByUsername(user.getUsername())).thenReturn(true);

        assertThatThrownBy(() -> userService.save(user))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("El nombre de usuario ya existe");
    }

    @Test
    void shouldFindUserById() {
        when(userDAO.findById(1L)).thenReturn(Optional.of(user));

        Optional<UserEntity> found = userService.findById(1L);
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("testuser");
    }

    @Test
    void shouldReturnAllUsers() {
        when(userDAO.findAll()).thenReturn(List.of(user));

        List<UserEntity> users = userService.findAll();
        assertThat(users).hasSize(1);
    }

    @Test
    void shouldDeleteUserById() {
        userService.deleteById(1L);
        verify(userDAO).deleteById(1L);
    }

    @Test
    void shouldFindUserByUsername() {
        when(userDAO.findByUsername("testuser")).thenReturn(Optional.of(user));

        Optional<UserEntity> found = userService.findByUsername("testuser");
        assertThat(found).isPresent();
    }

    @Test
    void shouldFindUserByEmail() {
        when(userDAO.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        Optional<UserEntity> found = userService.findByEmail("test@example.com");
        assertThat(found).isPresent();
    }

    @Test
    void shouldReturnTrueIfUsernameExists() {
        when(userDAO.existsByUsername("testuser")).thenReturn(true);

        boolean exists = userService.existsByUsername("testuser");
        assertThat(exists).isTrue();
    }

    @Test
    void shouldReturnTrueIfEmailExists() {
        when(userDAO.existsByEmail("test@example.com")).thenReturn(true);

        boolean exists = userService.existsByEmail("test@example.com");
        assertThat(exists).isTrue();
    }

    @Test
    void shouldFindEnabledUsers() {
        when(userDAO.findByEnabledTrue()).thenReturn(List.of(user));

        List<UserEntity> users = userService.findByEnabledTrue();
        assertThat(users).hasSize(1);
    }

    @Test
    void shouldFindUsersByRole() {
        when(userDAO.findByRoleName(RoleName.ROLE_USER)).thenReturn(List.of(user));

        List<UserEntity> users = userService.findByRoleName(RoleName.ROLE_USER);
        assertThat(users).hasSize(1);
    }

}
