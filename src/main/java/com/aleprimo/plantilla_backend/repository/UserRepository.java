package com.aleprimo.plantilla_backend.repository;

import com.aleprimo.plantilla_backend.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByUsername(String username);
    Optional<UserEntity> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    List<UserEntity> findByEnabledTrue();
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName")
    List<UserEntity> findByRoleName(@Param("roleName") String roleName);




}
