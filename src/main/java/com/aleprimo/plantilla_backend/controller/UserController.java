package com.aleprimo.plantilla_backend.controller;


import com.aleprimo.plantilla_backend.controller.mappers.UserMapper;
import com.aleprimo.plantilla_backend.dto.ChangePasswordRequest;
import com.aleprimo.plantilla_backend.dto.UserDTO;
import com.aleprimo.plantilla_backend.models.RoleName;
import com.aleprimo.plantilla_backend.models.UserEntity;
import com.aleprimo.plantilla_backend.entityServices.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated
@Tag(name = "Usuarios", description = "Operaciones CRUD sobre usuarios")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @Operation(summary = "Crear un nuevo usuario")
    @ApiResponse(responseCode = "201", description = "Usuario creado correctamente")
    @PostMapping("/create")
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO userDTO) {
        UserEntity newUser = userMapper.toEntity(userDTO);
        UserEntity savedUser = userService.save(newUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.toDto(savedUser));
    }

    @Operation(summary = "Obtener un usuario por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/id/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return userService.findById(id)
                .map(user -> ResponseEntity.ok(userMapper.toDto(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Obtener un usuario por su nombre de usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/username/{username}")
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable String username) {
        return userService.findByUsername(username)
                .map(user -> ResponseEntity.ok(userMapper.toDto(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Obtener un usuario por email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/email/{email}")
    public ResponseEntity<UserDTO> getUserByEmail(@PathVariable String email) {
        return userService.findByEmail(email)
                .map(user -> ResponseEntity.ok(userMapper.toDto(user)))
                .orElse(ResponseEntity.notFound().build());
    }


    @Operation(summary = "Verificar si existe un usuario por username")
    @ApiResponse(responseCode = "200", description = "Estado de existencia del usuario")
    @GetMapping("/exists/username/{username}")
    public ResponseEntity<Boolean> existsByUsername(@PathVariable String username) {
        return ResponseEntity.ok(userService.existsByUsername(username));
    }

    @Operation(summary = "Verificar si existe un usuario por email")
    @ApiResponse(responseCode = "200", description = "Estado de existencia del usuario")
    @GetMapping("/exists/email/{email}")
    public ResponseEntity<Boolean> existsByEmail(@PathVariable String email) {
        return ResponseEntity.ok(userService.existsByEmail(email));
    }

    @Operation(summary = "Listar todos los usuarios")
    @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida correctamente")
    @GetMapping("/all")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.findAll()
                .stream()
                .map(userMapper::toDto)
                .toList();
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Listar usuarios habilitados")
    @ApiResponse(responseCode = "200", description = "Lista de usuarios habilitados obtenida correctamente")
    @GetMapping("/enabled")
    public ResponseEntity<List<UserDTO>> getEnabledUsers() {
        List<UserDTO> users = userService.findByEnabledTrue()
                .stream()
                .map(userMapper::toDto)
                .toList();
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Listar usuarios por rol")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuarios encontrados con ese rol"),
            @ApiResponse(responseCode = "404", description = "No se encontraron usuarios con ese rol")
    })
    @GetMapping("/role/{roleName}")
    public ResponseEntity<List<UserDTO>> getUsersByRole(@PathVariable RoleName roleName) {
        List<UserDTO> users = userService.findByRoleName(roleName)
                .stream()
                .map(userMapper::toDto)
                .toList();
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Actualizar un usuario existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario actualizado correctamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PutMapping("/update/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @Valid @RequestBody UserDTO userDTO) {
        return userService.findById(id)
                .map(existing -> {
                    UserEntity updated = userMapper.toEntity(userDTO);
                    updated.setId(id); // aseguramos que no cree uno nuevo
                    return ResponseEntity.ok(userMapper.toDto(userService.save(updated)));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Eliminar un usuario por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuario eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Cambiar la contraseña del usuario autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contraseña cambiada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Contraseña actual incorrecta"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })

    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody @Valid ChangePasswordRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName(); // ← el JWT devuelve el username
        userService.changePassword(username, request);
        return ResponseEntity.ok("Contraseña actualizada exitosamente");
    }



}
