package com.aleprimo.plantilla_backend.entityServices.implementations;

import com.aleprimo.plantilla_backend.models.Role;
import com.aleprimo.plantilla_backend.models.RoleName;
import com.aleprimo.plantilla_backend.persistence.RoleDAO;
import com.aleprimo.plantilla_backend.entityServices.RoleService;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleDAO roleDAO;


    @Override
    public Role save(Role role) {
        return this.roleDAO.save(role);
    }

    @Override
    public Optional<Role> findById(Long id) {
        return this.roleDAO.findById(id);
    }

    @Override
    public Optional<Role> findByName(RoleName role) {
        return this.roleDAO.findByName(role);
    }

    @Override
    public List<Role> findAll() {
        return this.roleDAO.findAll();
    }

    @Override
    public void delete(Long id) {
this.roleDAO.delete(id);
    }
}
