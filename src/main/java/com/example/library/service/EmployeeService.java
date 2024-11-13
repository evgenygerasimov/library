package com.example.library.service;

import com.example.library.entity.Employee;
import com.example.library.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    public void addEmployee(Employee employee) {
        employeeRepository.save(employee);
    }
}
