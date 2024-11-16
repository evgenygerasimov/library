package com.example.library.service;

import com.example.library.entity.Employee;
import com.example.library.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    public void addEmployee(Employee employee) {
        employeeRepository.save(employee);
    }

    public Employee getEmployeeByUsername(String name) {
        return employeeRepository.findByUsername(name);
    }

    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) {
                Employee employee = employeeRepository.findByUsername(username);
                if (employee == null) {
                    throw new UsernameNotFoundException("User not found with username: " + username);
                }
                return employee;
            }
        };
    }
}



