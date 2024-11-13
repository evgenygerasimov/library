package com.example.library.controller;

import com.example.library.entity.Employee;
import com.example.library.entity.Role;
import com.example.library.service.EmployeeService;
import com.example.library.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
public class EmployeeController {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private RoleService roleService;

    @GetMapping("/")
    public ResponseEntity<String> home() {
        if (!SecurityContextHolder.getContext().getAuthentication().getName().equals("anonymousUser")) {
            return ResponseEntity.ok("You are logged in");
        } else return ResponseEntity.ok("You are not logged in");
    }

    @PostMapping("/login")
    public void login(@RequestParam("username") String username, @RequestParam("password") String password) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
        Authentication authentication = authenticationManager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @PostMapping("/addNewEmployee")
    public ResponseEntity<Employee> addNewUser(@RequestBody Employee employee) {
        employee.setPassword("{bcrypt}" + passwordEncoder.encode(employee.getPassword()));
        Role role = new Role();
        role.setUsername(employee.getUsername());
        role.setAuthority(employee.getRole());
        employeeService.addEmployee(employee);
        roleService.addRole(role);
        return ResponseEntity.ok(employee);
    }
}
