package com.aleprimo.plantilla_backend.persistence.implementations;

import com.aleprimo.plantilla_backend.models.Role;
import com.aleprimo.plantilla_backend.models.RoleName;
import com.aleprimo.plantilla_backend.persistence.RoleDAO;
import com.aleprimo.plantilla_backend.repository.RoleRepository;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class RoleDAOImpl implements RoleDAO {

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
    public Optional<Role> findByName(RoleName role) {
        return this.roleRepository.findByName(role);
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
