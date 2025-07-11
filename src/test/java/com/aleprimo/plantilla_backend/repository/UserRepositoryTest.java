package com.aleprimo.plantilla_backend.repository;

import com.aleprimo.plantilla_backend.models.Role;
import com.aleprimo.plantilla_backend.models.RoleName;
import com.aleprimo.plantilla_backend.models.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@DataJpaTest
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private UserEntity user;

    @BeforeEach
    void setUp() {
        Role role = roleRepository.findByName(RoleName.valueOf("ROLE_USER")).orElseGet(() -> {
            Role newRole = new Role();
            newRole.setName(RoleName.valueOf("ROLE_USER"));
            return roleRepository.save(newRole);
        });

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
        Pageable pageable = PageRequest.of(0, 10); // página 0, tamaño 10

        Page<UserEntity> enabledUsersPage = userRepository.findByEnabledTrue(pageable);

        assertThat(enabledUsersPage.getContent()).hasSize(1);
        assertThat(enabledUsersPage.getContent().get(0).getEnabled()).isTrue();
    }


    @Test
    void shouldFindByRoleName() {
        List<UserEntity> users = userRepository.findByRoleName(RoleName.ROLE_USER);
        assertThat(users).hasSize(1);
    }
}
