package com.aleprimo.plantilla_backend.controller;

import com.aleprimo.plantilla_backend.dto.UserDTO;
import com.aleprimo.plantilla_backend.models.Role;
import com.aleprimo.plantilla_backend.models.RoleName;
import com.aleprimo.plantilla_backend.security.auth.AuthRequest;
import com.aleprimo.plantilla_backend.security.auth.AuthResponse;
import com.aleprimo.plantilla_backend.security.auth.RegisterRequest;
import com.aleprimo.plantilla_backend.repository.RoleRepository;

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
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
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

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/auth";
        roleRepository.save(Role.builder().name(RoleName.ROLE_USER).build());
    }

    @Test
    void register_shouldReturnUserDTO_whenValidRequest() {
        RegisterRequest register = RegisterRequest.builder()
                .username("alejandro123")
                .email("alejandro@mail.com")
                .password("secreta123")
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RegisterRequest> request = new HttpEntity<>(register, headers);

        ResponseEntity<UserDTO> response = restTemplate.postForEntity(baseUrl + "/register", request, UserDTO.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getUsername()).isEqualTo("alejandro123");
    }

    @Test
    void login_shouldReturnAuthResponse_whenValidCredentials() {
        RegisterRequest register = RegisterRequest.builder()
                .username("alejandro123")
                .email("alejandro@mail.com")
                .password("secreta123")
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        restTemplate.postForEntity(baseUrl + "/register", new HttpEntity<>(register, headers), UserDTO.class);

        AuthRequest login = AuthRequest.builder()
                .email("alejandro@mail.com")
                .password("secreta123")
                .build();

        HttpEntity<AuthRequest> loginRequest = new HttpEntity<>(login, headers);
        ResponseEntity<AuthResponse> loginResponse = restTemplate.postForEntity(baseUrl + "/login", loginRequest, AuthResponse.class);

        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(loginResponse.getBody()).isNotNull();
        assertThat(loginResponse.getBody().getEmail()).isEqualTo("alejandro@mail.com");
        assertThat(loginResponse.getBody().getUsername()).isEqualTo("alejandro123");
        assertThat(loginResponse.getBody().getToken()).isNotBlank();
    }

    @Configuration
    @EnableWebSecurity
    static class TestSecurityConfiguration {
        @Bean
        public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
            http.csrf(AbstractHttpConfigurer::disable)
                    .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
            return http.build();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
            return configuration.getAuthenticationManager();
        }

        @Bean
        public ServletWebServerFactory servletWebServerFactory() {
            return new TomcatServletWebServerFactory();
        }
    }



}