package com.aleprimo.plantilla_backend.repository;

import com.aleprimo.plantilla_backend.model.Role;
import com.aleprimo.plantilla_backend.model.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role,Long> {
    Optional<Role> findByRoleName(RoleName role);
}
