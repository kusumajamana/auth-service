package com.demo.authentication_service.service;

import com.demo.authentication_service.dao.UserCredentialsDao;
import com.demo.authentication_service.dao.entity.UserCredentialsEntity;
import com.demo.authentication_service.dao.entity.Role;
import com.demo.authentication_service.dao.RoleDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Service
public class UserCredentialsService {
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserCredentialsDao authDao;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RoleDao roleDao;

    @Transactional
    public UserCredentialsEntity register(UserCredentialsEntity user) {
        // Encode password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Assign roles
        Set<Role> roles = new HashSet<>();
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            // Default to ROLE_USER if no roles are specified
            Role defaultRole = roleDao.findByName(Role.ERole.ROLE_USER)
                    .orElseGet(() -> {
                        Role newRole = new Role();
                        newRole.setName(Role.ERole.ROLE_USER);
                        return roleDao.save(newRole);
                    });
            roles.add(defaultRole);
        } else {
            // Fetch or create roles specified in the request
            for (Role role : user.getRoles()) {
                Role existingRole = roleDao.findByName(role.getName())
                        .orElseGet(() -> {
                            Role newRole = new Role();
                            newRole.setName(role.getName());
                            return roleDao.save(newRole);
                        });
                roles.add(existingRole);
            }
        }

        // Set roles and save the user
        user.setRoles(roles);
        return authDao.save(user);
    }

    public String generateToken(String name) {
        return jwtService.generateToken(name);
    }

    public boolean verifyToken(String token) {
        jwtService.validateToken(token);
        return true;
    }

    public UserCredentialsEntity findByName(String name) {
        return authDao.findByUsername(name)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
