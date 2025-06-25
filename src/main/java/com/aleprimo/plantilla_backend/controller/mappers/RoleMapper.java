package com.aleprimo.plantilla_backend.controller.mappers;

import com.aleprimo.plantilla_backend.dto.RoleDTO;
import com.aleprimo.plantilla_backend.model.Role;
import org.springframework.stereotype.Component;

@Component
public class RoleMapper {

    public RoleDTO toDto(Role role){
        if (role == null) {
            return null;
        }

        return RoleDTO.builder()
                .id(role.getId())
                .name(role.getName())
                .build();
    }



    public Role toEntity(RoleDTO roleDTO){
        if (roleDTO == null) {
            return null;
        }

        return Role.builder()
                .id(roleDTO.getId())
                .name(roleDTO.getName())
                .build();
    }




}
