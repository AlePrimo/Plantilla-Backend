package com.aleprimo.plantilla_backend.controller;


import com.aleprimo.plantilla_backend.dto.ChangePasswordRequest;
import com.aleprimo.plantilla_backend.dto.UserDTO;
import com.aleprimo.plantilla_backend.models.Role;
import com.aleprimo.plantilla_backend.models.RoleName;
import com.aleprimo.plantilla_backend.models.UserEntity;
import com.aleprimo.plantilla_backend.repository.RoleRepository;
import com.aleprimo.plantilla_backend.repository.UserRepository;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;

import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;



import java.security.Principal;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = {
                com.aleprimo.plantilla_backend.PlantillaBackendApplication.class,
                UserControllerTest.TestSecurityConfiguration.class
        }
)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)

class UserControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private String baseUrl;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        baseUrl = "http://localhost:" + port + "/api/users";
        roleRepository.deleteAll(); // limpieza previa

        Role roleUser = new Role();
        roleUser.setName(RoleName.ROLE_USER);
        roleRepository.save(roleUser);
    }





    @Test
    void getAllUsers_shouldReturnOk() {
        ResponseEntity<UserDTO[]> response = restTemplate.getForEntity(baseUrl + "/all", UserDTO[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void getUserById_shouldReturnNotFound_whenIdInvalid() {
        ResponseEntity<UserDTO> response = restTemplate.getForEntity(baseUrl + "/id/9999", UserDTO.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void existsByUsername_shouldReturnFalse_whenUsernameDoesNotExist() {
        ResponseEntity<Boolean> response = restTemplate.getForEntity(baseUrl + "/exists/username/usuarioInexistente", Boolean.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isFalse();
    }

    @Test
    void existsByEmail_shouldReturnFalse_whenEmailDoesNotExist() {
        ResponseEntity<Boolean> response = restTemplate.getForEntity(baseUrl + "/exists/email/inexistente@mail.com", Boolean.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isFalse();
    }

    @Test
    void getEnabledUsers_shouldReturnOk() {
        ResponseEntity<UserDTO[]> response = restTemplate.getForEntity(baseUrl + "/enabled", UserDTO[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void getUsersByRole_shouldReturnOkOrEmpty() {
        ResponseEntity<UserDTO[]> response = restTemplate.getForEntity(baseUrl + "/role/ROLE_USER", UserDTO[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }



    @Test
    void createUser_shouldReturnCreated() {
        UserDTO user = UserDTO.builder()
                .username("testuser123")
                .email("testuser123@mail.com")
                .password("testpass")
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UserDTO> request = new HttpEntity<>(user, headers);

        ResponseEntity<UserDTO> response = restTemplate.postForEntity(baseUrl + "/create", request, UserDTO.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void updateUser_shouldReturnNotFound_whenIdDoesNotExist() {
        UserDTO user = UserDTO.builder()
                .username("usuarioInexistenteXYZ")
                .email("inexistenteXYZ@mail.com")
                .password("contrasenaValida123") // al menos 6 caracteres
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UserDTO> request = new HttpEntity<>(user, headers);

        ResponseEntity<UserDTO> response = restTemplate.exchange(
                baseUrl + "/update/9999", HttpMethod.PUT, request, UserDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void deleteUser_shouldReturnNoContent_whenUserNotExists() {
        ResponseEntity<Void> response = restTemplate.exchange(
                baseUrl + "/delete/9999", HttpMethod.DELETE, null, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }



    @Test
    void changePassword_shouldUpdatePasswordSuccessfully() {
        // Contraseña sin codificar
        String rawPassword = "originalPass123";

        // Crear usuario con contraseña codificada
        String encodedPassword = passwordEncoder.encode(rawPassword);

        UserEntity user = UserEntity.builder()
                .username("usuarioTestCambioPass")
                .email("usuario@test.com")
                .password(encodedPassword)
                .enabled(true)
                .roles(Set.of(roleRepository.findByName(RoleName.ROLE_USER).get()))
                .build();

        userRepository.save(user);

        // Simular autenticación del usuario en el SecurityContext
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user.getEmail(), null, List.of())
        );

        // Armar la solicitud
        ChangePasswordRequest changePasswordRequest = ChangePasswordRequest.builder()
                .currentPassword(rawPassword)
                .newPassword("nuevaClaveSegura456")
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ChangePasswordRequest> request = new HttpEntity<>(changePasswordRequest, headers);

        // Ejecutar el PUT al endpoint
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/change-password",
                HttpMethod.PUT,
                request,
                String.class
        );

        // Verificar respuesta
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Contraseña actualizada exitosamente");

        // Verificar persistencia de la nueva contraseña
        UserEntity updatedUser = userRepository.findByEmail(user.getEmail()).orElseThrow();
        assertThat(passwordEncoder.matches("nuevaClaveSegura456", updatedUser.getPassword())).isTrue();
    }




    @Configuration
    static class TestSecurityConfiguration {
        @Bean
        public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
            http.csrf(AbstractHttpConfigurer::disable)
                    .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
            return http.build();
        }


        @Bean
        public ServletWebServerFactory servletWebServerFactory() {
            return new TomcatServletWebServerFactory();
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
            return configuration.getAuthenticationManager();
        }
        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }
    }



}
