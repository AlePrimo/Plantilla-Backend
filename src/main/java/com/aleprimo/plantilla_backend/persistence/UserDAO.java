package com.aleprimo.plantilla_backend.persistence;

import com.aleprimo.plantilla_backend.model.UserEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserDAO {

    Optional<UserEntity> findByUsername(String username);
    Optional<UserEntity> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    List<UserEntity> findByEnabledTrue();
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName")
    List<UserEntity> findByRoleName(@Param("roleName") String roleName);

}
