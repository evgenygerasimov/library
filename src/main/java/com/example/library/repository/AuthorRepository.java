package com.example.library.repository;

import com.example.library.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface AuthorRepository extends JpaRepository<Author, Long> {
    Author findByFirstNameAndLastNameAndBirthDate(String firstName, String lastName, LocalDate birthDate);
}
