package com.example.library.service;

import com.example.library.entity.Role;
import com.example.library.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleService {

    @Autowired
    RoleRepository getRepository;

    public void addRole(Role role) {
        getRepository.save(role);
    }
}
