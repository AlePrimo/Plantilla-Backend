package com.aleprimo.plantilla_backend.controller;

import com.aleprimo.plantilla_backend.dto.UserDTO;
import com.aleprimo.plantilla_backend.models.Role;
import com.aleprimo.plantilla_backend.models.RoleName;
import com.aleprimo.plantilla_backend.security.auth.AuthRequest;
import com.aleprimo.plantilla_backend.security.auth.AuthResponse;
import com.aleprimo.plantilla_backend.security.auth.RegisterRequest;
import com.aleprimo.plantilla_backend.repository.RoleRepository;
import com.aleprimo.plantilla_backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = {
                com.aleprimo.plantilla_backend.PlantillaBackendApplication.class,
                AuthControllerTest.TestSecurityConfiguration.class
        }
)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class AuthControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/auth";
        if (!roleRepository.existsByName(RoleName.ROLE_USER)) {
            Role role = Role.builder().name(RoleName.ROLE_USER).build();
            roleRepository.save(role);
        }
    }

    @Test
    void register_shouldReturnUserDTO_whenValidRequest() {
        RegisterRequest request = RegisterRequest.builder()
                .username("testuser")
                .email("testuser@email.com")
                .password("password")
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RegisterRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<UserDTO> response = restTemplate.postForEntity(baseUrl + "/register", entity, UserDTO.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getUsername()).isEqualTo("testuser");
    }

    @Test
    void login_shouldReturnToken_whenCredentialsAreValid() {
        RegisterRequest register = RegisterRequest.builder()
                .username("testuser")
                .email("testuser@email.com")
                .password("password")
                .build();

        restTemplate.postForEntity(baseUrl + "/register", new HttpEntity<>(register), UserDTO.class);

        AuthRequest auth = AuthRequest.builder()
                .username("testuser")
                .password("password")
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AuthRequest> entity = new HttpEntity<>(auth, headers);

        ResponseEntity<AuthResponse> response = restTemplate.postForEntity(baseUrl + "/login", entity, AuthResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getToken()).isNotBlank();
    }

    @Test
    void login_shouldReturnUnauthorized_whenCredentialsAreInvalid() {
        AuthRequest auth = AuthRequest.builder()
                .username("invalid")
                .password("invalid")
                .build();

        HttpEntity<AuthRequest> entity = new HttpEntity<>(auth);
        ResponseEntity<AuthResponse> response = restTemplate.postForEntity(baseUrl + "/login", entity, AuthResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Configuration
    @EnableWebSecurity
    static class TestSecurityConfiguration {
        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
            return http.build();
        }

        @Bean
        public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
            return http.getSharedObject(AuthenticationManager.class);
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }

        @Bean
        public ServletWebServerFactory webServerFactory() {
            return new TomcatServletWebServerFactory();
        }
    }
}