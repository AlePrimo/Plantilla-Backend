package com.aleprimo.plantilla_backend.controller;

import com.aleprimo.plantilla_backend.dto.RoleDTO;
import com.aleprimo.plantilla_backend.models.Role;
import com.aleprimo.plantilla_backend.repository.RoleRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
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
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import com.aleprimo.plantilla_backend.models.RoleName;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = {
                com.aleprimo.plantilla_backend.PlantillaBackendApplication.class,
                RoleControllerTest.TestSecurityConfiguration.class
        }
)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)

class RoleControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private RoleRepository roleRepository;

    private String baseUrl;
    private Role savedRole;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/roles";
        savedRole = roleRepository.saveAndFlush(Role.builder().name(RoleName.ROLE_TEST).build());
    }

    @Test
    void createRole_shouldReturnCreated() {
        RoleDTO role = RoleDTO.builder().name(RoleName.ROLE_ADMIN).build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RoleDTO> request = new HttpEntity<>(role, headers);

        ResponseEntity<RoleDTO> response = restTemplate.postForEntity(baseUrl + "/create", request, RoleDTO.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo(RoleName.ROLE_ADMIN);
    }

    @Test
    void getRoleById_shouldReturnFound_whenValidId() {
        ResponseEntity<RoleDTO> response = restTemplate.getForEntity(
                baseUrl + "/id/" + savedRole.getId(), RoleDTO.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo(RoleName.ROLE_TEST);
    }

    @Test
    void getRoleById_shouldReturnNotFound_whenInvalidId() {
        ResponseEntity<RoleDTO> response = restTemplate.getForEntity(baseUrl + "/id/9999", RoleDTO.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void getRoleByName_shouldReturnFound_whenValidName() {
        ResponseEntity<RoleDTO> response = restTemplate.getForEntity(baseUrl + "/name/ROLE_TEST", RoleDTO.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo(RoleName.ROLE_TEST);
    }

    @Test
    void getRoleByName_shouldReturnNotFound_whenInvalidName() {
        ResponseEntity<RoleDTO> response = restTemplate.getForEntity(baseUrl + "/name/ROLE_ADMIN", RoleDTO.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void getAllRoles_shouldReturnOk() {
        ResponseEntity<RoleDTO[]> response = restTemplate.getForEntity(baseUrl + "/all", RoleDTO[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
        assertThat(response.getBody()[0].getName()).isEqualTo(RoleName.ROLE_TEST);
    }

    @Test
    void deleteRole_shouldReturnNoContent_whenIdExists() {
        ResponseEntity<Void> response = restTemplate.exchange(
                baseUrl + "/delete/" + savedRole.getId(), HttpMethod.DELETE, null, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
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
