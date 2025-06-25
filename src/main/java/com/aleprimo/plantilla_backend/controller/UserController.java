package com.aleprimo.plantilla_backend.controller;


import com.aleprimo.plantilla_backend.dto.UserDTO;
import com.aleprimo.plantilla_backend.model.UserEntity;
import com.aleprimo.plantilla_backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated

public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserEntity> users = userService.findAll();
        List<UserDTO> dtoList = users.stream().map(userMapper::toDto).toList();
        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return userService.findById(id)
                .map(user -> ResponseEntity.ok(userMapper.toDto(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/enabled")
    public ResponseEntity<List<UserDTO>> getEnabledUsers() {
        List<UserEntity> enabledUsers = userService.findByEnabledTrue();
        return ResponseEntity.ok(enabledUsers.stream().map(userMapper::toDto).toList());
    }

    @GetMapping("/by-role")
    public ResponseEntity<List<UserDTO>> getUsersByRole(@RequestParam String role) {
        List<UserEntity> users = userService.findByRoleName(role);
        return ResponseEntity.ok(users.stream().map(userMapper::toDto).toList());
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO userDTO) {
        if (userService.existsByUsername(userDTO.getUsername())) {
            return ResponseEntity.badRequest().build();
        }

        UserEntity user = userMapper.toEntity(userDTO);
        user.setPassword(user.getPassword());
        user.setEnabled(true);
        UserEntity savedUser = userService.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.toDto(savedUser));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @Valid @RequestBody UserDTO userDTO) {
        return userService.findById(id).map(existingUser -> {
            existingUser.setUsername(userDTO.getUsername());
            existingUser.setEmail(userDTO.getEmail());
            existingUser.setPassword(userDTO.getPassword());
            existingUser.setEnabled(userDTO.getEnabled());

            UserEntity updatedUser = userService.save(existingUser);
            return ResponseEntity.ok(userMapper.toDto(updatedUser));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (userService.findById(id).isPresent()) {
            userService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }





}
