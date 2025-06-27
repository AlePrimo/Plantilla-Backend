package com.aleprimo.plantilla_backend.persistence.implementations;

import com.aleprimo.plantilla_backend.models.RoleName;
import com.aleprimo.plantilla_backend.models.UserEntity;
import com.aleprimo.plantilla_backend.persistence.UserDAO;
import com.aleprimo.plantilla_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
@RequiredArgsConstructor
public class UserDAOImpl implements UserDAO {

    private final UserRepository userRepository;

    @Override
    public UserEntity save(UserEntity user) {
        return this.userRepository.save(user);
    }

    @Override
    public Optional<UserEntity> findById(Long id) {
        return this.userRepository.findById(id);
    }

    @Override
    public List<UserEntity> findAll() {
        return this.userRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        this.userRepository.deleteById(id);

    }

    @Override
    public Optional<UserEntity> findByUsername(String username) {
        return this.userRepository.findByUsername(username);
    }

    @Override
    public Optional<UserEntity> findByEmail(String email) {
        return this.userRepository.findByEmail(email);
    }

    @Override
    public boolean existsByUsername(String username) {
        return this.userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return this.userRepository.existsByEmail(email);
    }

    @Override
    public List<UserEntity> findByEnabledTrue() {
        return this.userRepository.findByEnabledTrue();
    }

    @Override
    public List<UserEntity> findByRoleName(RoleName roleName) {
        return this.userRepository.findByRoleName(roleName);
    }
}
