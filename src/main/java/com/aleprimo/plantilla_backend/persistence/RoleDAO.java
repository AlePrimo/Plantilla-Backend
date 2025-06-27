package com.aleprimo.plantilla_backend.persistence;

import com.aleprimo.plantilla_backend.models.Role;
import com.aleprimo.plantilla_backend.models.RoleName;

import java.util.List;
import java.util.Optional;

public interface RoleDAO {
    Role save(Role role);
    Optional<Role> findById(Long id);
    Optional<Role> findByRoleName(RoleName role);
    List<Role> findAll();
    void delete(Long id);
}
