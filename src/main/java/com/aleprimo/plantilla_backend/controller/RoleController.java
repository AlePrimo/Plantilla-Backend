package com.aleprimo.plantilla_backend.controller;

import com.aleprimo.plantilla_backend.controller.mappers.RoleMapper;
import com.aleprimo.plantilla_backend.dto.RoleDTO;
import com.aleprimo.plantilla_backend.entityServices.RoleService;

import com.aleprimo.plantilla_backend.models.Role;
import com.aleprimo.plantilla_backend.models.RoleName;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@Tag(name = "Roles", description = "Operaciones CRUD sobre roles de usuario")
public class RoleController {

    private final RoleService roleService;
    private final RoleMapper roleMapper;

    @Operation(summary = "Crear un nuevo rol")
    @ApiResponse(responseCode = "201", description = "Rol creado correctamente")
    @PostMapping("/create")
    public ResponseEntity<RoleDTO> createRole(@Valid @RequestBody RoleDTO roleDTO) {
        Role role = roleMapper.toEntity(roleDTO);
        Role saved = roleService.save(role);
        return ResponseEntity.status(HttpStatus.CREATED).body(roleMapper.toDto(saved));
    }

    @Operation(summary = "Obtener un rol por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rol encontrado"),
            @ApiResponse(responseCode = "404", description = "Rol no encontrado")
    })
    @GetMapping("/id/{id}")
    public ResponseEntity<RoleDTO> getRoleById(@PathVariable Long id) {
        return roleService.findById(id)
                .map(role -> ResponseEntity.ok(roleMapper.toDto(role)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Obtener un rol por nombre")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rol encontrado"),
            @ApiResponse(responseCode = "404", description = "Rol no encontrado")
    })
    @GetMapping("/name/{roleName}")
    public ResponseEntity<RoleDTO> getRoleByName(@PathVariable RoleName roleName) {
        return roleService.findByName(roleName)
                .map(role -> ResponseEntity.ok(roleMapper.toDto(role)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Listar todos los roles")
    @ApiResponse(responseCode = "200", description = "Lista de roles obtenida correctamente")
    @GetMapping("/all")
    public ResponseEntity<List<RoleDTO>> getAllRoles() {
        List<RoleDTO> roles = roleService.findAll()
                .stream()
                .map(roleMapper::toDto)
                .toList();
        return ResponseEntity.ok(roles);
    }

    @Operation(summary = "Eliminar un rol por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Rol eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "Rol no encontrado")
    })
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        roleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
