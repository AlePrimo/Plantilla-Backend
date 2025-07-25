package com.aleprimo.plantilla_backend.entityServices;

import com.aleprimo.plantilla_backend.models.Role;
import com.aleprimo.plantilla_backend.models.RoleName;

import java.util.List;
import java.util.Optional;

public interface RoleService {
    Role save(Role role);
    Optional<Role> findById(Long id);
    Optional<Role> findByName(RoleName role);
    List<Role> findAll();
    void delete(Long id);
}
