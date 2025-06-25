package com.aleprimo.plantilla_backend.persistence;

import com.aleprimo.plantilla_backend.model.Role;
import com.aleprimo.plantilla_backend.model.RoleName;
import com.aleprimo.plantilla_backend.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class RoleDAOImpl implements RoleDAO{

    private final RoleRepository roleRepository;

    public RoleDAOImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Role save(Role role) {
        return this.roleRepository.save(role);
    }

    @Override
    public Optional<Role> findById(Long id) {
        return this.roleRepository.findById(id);
    }

    @Override
    public Optional<Role> findByRoleName(RoleName role) {
        return this.roleRepository.findByRoleName(role);
    }

    @Override
    public List<Role> findAll() {
        return this.roleRepository.findAll();
    }

    @Override
    public void delete(Long id) {
        this.roleRepository.deleteById(id);
    }
}
