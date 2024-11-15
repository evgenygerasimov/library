package com.example.library.controller;

import com.example.library.entity.Employee;
import com.example.library.entity.Role;
import com.example.library.service.EmployeeService;
import com.example.library.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
public class EmployeeController {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private RoleService roleService;

    @PostMapping("/addNewEmployee")
    public ResponseEntity<Employee> addNewUser(@RequestBody Employee employee) {
        employee.setPassword(passwordEncoder.encode(employee.getPassword()));
        Role role = new Role();
        role.setUsername(employee.getUsername());
        role.setAuthority(employee.getRole());
        employeeService.addEmployee(employee);
        roleService.addRole(role);
        return ResponseEntity.ok(employee);
    }
}