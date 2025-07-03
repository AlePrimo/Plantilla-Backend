package com.aleprimo.plantilla_backend.repository;

import com.aleprimo.plantilla_backend.models.Role;
import com.aleprimo.plantilla_backend.models.RoleName;
import com.aleprimo.plantilla_backend.models.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private UserEntity user;

    @BeforeEach
    void setUp() {
        Role role = Role.builder().name(RoleName.ROLE_USER).build();
        roleRepository.save(role);

        user = UserEntity.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password")
                .enabled(true)
                .roles(Set.of(role))
                .build();

        userRepository.save(user);
    }

    @Test
    void shouldFindByUsername() {
        assertThat(userRepository.findByUsername("testuser")).isPresent();
    }

    @Test
    void shouldFindByEmail() {
        assertThat(userRepository.findByEmail("test@example.com")).isPresent();
    }

    @Test
    void shouldCheckExistsByUsername() {
        assertThat(userRepository.existsByUsername("testuser")).isTrue();
    }

    @Test
    void shouldCheckExistsByEmail() {
        assertThat(userRepository.existsByEmail("test@example.com")).isTrue();
    }

    @Test
    void shouldFindByEnabledTrue() {
        List<UserEntity> enabledUsers = userRepository.findByEnabledTrue();
        assertThat(enabledUsers).hasSize(1);
    }

    @Test
    void shouldFindByRoleName() {
        List<UserEntity> users = userRepository.findByRoleName(RoleName.ROLE_USER);
        assertThat(users).hasSize(1);
    }
}
