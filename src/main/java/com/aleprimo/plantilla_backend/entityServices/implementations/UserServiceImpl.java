package com.aleprimo.plantilla_backend.entityServices.implementations;

import com.aleprimo.plantilla_backend.dto.ChangePasswordRequest;
import com.aleprimo.plantilla_backend.handler.exceptions.RoleNotFoundException;
import com.aleprimo.plantilla_backend.handler.exceptions.UserNotFoundException;
import com.aleprimo.plantilla_backend.handler.exceptions.UsernameAlreadyExistsException;
import com.aleprimo.plantilla_backend.models.Role;
import com.aleprimo.plantilla_backend.models.RoleName;
import com.aleprimo.plantilla_backend.models.UserEntity;
import com.aleprimo.plantilla_backend.persistence.RoleDAO;
import com.aleprimo.plantilla_backend.persistence.UserDAO;

import com.aleprimo.plantilla_backend.entityServices.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserDAO userDAO;
    private final RoleDAO roleDAO;
    private final PasswordEncoder passwordEncoder;




    @Override
    public UserEntity save(UserEntity user) {
        if(this.userDAO.existsByUsername(user.getUsername())){
            throw new UsernameAlreadyExistsException(user.getUsername());
        }

        Role roleUser = this.roleDAO.findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new RoleNotFoundException(RoleName.ROLE_USER));

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(Set.of(roleUser));
        user.setEnabled(true);
        user.setCreatedDate(LocalDateTime.now());

        return this.userDAO.save(user);
    }

    @Override
    public Optional<UserEntity> findById(Long id) {
        return this.userDAO.findById(id);
    }

    @Override
    public List<UserEntity> findAll() {
        return this.userDAO.findAll();
    }

    @Override
    public void deleteById(Long id) {
this.userDAO.deleteById(id);
    }

    @Override
    public Optional<UserEntity> findByUsername(String username) {
        return this.userDAO.findByUsername(username);
    }

    @Override
    public Optional<UserEntity> findByEmail(String email) {
        return this.userDAO.findByEmail(email);
    }

    @Override
    public boolean existsByUsername(String username) {
        return this.userDAO.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return this.userDAO.existsByEmail(email);
    }

    @Override
    public List<UserEntity> findByEnabledTrue() {
        return this.userDAO.findByEnabledTrue();
    }

    @Override
    public List<UserEntity> findByRoleName(RoleName roleName) {
        return this.userDAO.findByRoleName(roleName);
    }

    @Override
    public void changePassword(String usernameOrEmail, ChangePasswordRequest request) {

        UserEntity user = userDAO.findByUsername(usernameOrEmail)
                .or(() -> userDAO.findByEmail(usernameOrEmail))
                .orElseThrow(() -> new UserNotFoundException("username o email", usernameOrEmail));


        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("La contraseña actual es incorrecta");
        }


        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userDAO.save(user);
    }
}
