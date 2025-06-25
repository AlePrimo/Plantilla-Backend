package com.aleprimo.plantilla_backend.controller;

import com.aleprimo.plantilla_backend.controller.mappers.RoleMapper;
import com.aleprimo.plantilla_backend.dto.RoleDTO;
import com.aleprimo.plantilla_backend.model.Role;
import com.aleprimo.plantilla_backend.model.RoleName;
import com.aleprimo.plantilla_backend.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;
    private final RoleMapper roleMapper;


    @PostMapping("/create")
    public ResponseEntity<RoleDTO> createRole(@Valid @RequestBody RoleDTO roleDTO) {
        Role role = roleMapper.toEntity(roleDTO);
        Role saved = roleService.save(role);
        return ResponseEntity.status(HttpStatus.CREATED).body(roleMapper.toDto(saved));
    }


    @GetMapping("/id/{id}")
    public ResponseEntity<RoleDTO> getRoleById(@PathVariable Long id) {
        return roleService.findById(id)
                .map(role -> ResponseEntity.ok(roleMapper.toDto(role)))
                .orElse(ResponseEntity.notFound().build());
    }


    @GetMapping("/name/{roleName}")
    public ResponseEntity<RoleDTO> getRoleByName(@PathVariable RoleName roleName) {
        return roleService.findByRoleName(roleName)
                .map(role -> ResponseEntity.ok(roleMapper.toDto(role)))
                .orElse(ResponseEntity.notFound().build());
    }


    @GetMapping("/all")
    public ResponseEntity<List<RoleDTO>> getAllRoles() {
        List<RoleDTO> roles = roleService.findAll()
                .stream()
                .map(roleMapper::toDto)
                .toList();
        return ResponseEntity.ok(roles);
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        roleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
