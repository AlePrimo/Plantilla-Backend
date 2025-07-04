package com.aleprimo.plantilla_backend.controller.mappers;

import com.aleprimo.plantilla_backend.dto.UserDTO;
import com.aleprimo.plantilla_backend.models.Role;
import com.aleprimo.plantilla_backend.models.RoleName;
import com.aleprimo.plantilla_backend.models.UserEntity;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    public UserDTO toDto(UserEntity user) {
        if (user == null) return null;

        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .password(user.getPassword())
                .enabled(user.getEnabled())
                .createdDate(user.getCreatedDate())
                .createdBy(user.getCreatedBy())
                .lastModifiedBy(user.getLastModifiedBy())
                .lastModifiedDate(user.getLastModifiedDate())
                .roles(
                        user.getRoles() != null ?
                                user.getRoles().stream()
                                        .map(role -> role.getName().name())
                                        .collect(Collectors.toSet()) : Set.of()
                )
                .build();
    }

    public UserEntity toEntity(UserDTO dto) {
        if (dto == null) return null;

        Set<Role> roles = dto.getRoles() != null ?
                dto.getRoles().stream()
                        .map(roleName -> Role.builder()
                                .name(RoleName.valueOf(roleName))
                                .build())
                        .collect(Collectors.toSet())
                : Set.of();

        return UserEntity.builder()
                .id(dto.getId())
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password(dto.getPassword())
                .enabled(dto.getEnabled())
                .createdDate(dto.getCreatedDate())
                .createdBy(dto.getCreatedBy())
                .lastModifiedBy(dto.getLastModifiedBy())
                .lastModifiedDate(dto.getLastModifiedDate())
                .roles(roles)
                .build();
    }


}
