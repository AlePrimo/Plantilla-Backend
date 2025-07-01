package com.aleprimo.plantilla_backend.controller;

import com.aleprimo.plantilla_backend.dto.UserDTO;

import com.aleprimo.plantilla_backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/users";
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

    // Los siguientes test requieren datos existentes. Idealmente deberían usar @Sql o setup manual.

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
                .username("otro")
                .email("otro@email.com")
                .password("otro")
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
}
