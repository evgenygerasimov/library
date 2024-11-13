package com.example.library.service;

import com.example.library.entity.Author;
import com.example.library.repository.AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthorService {

    @Autowired
    private AuthorRepository authorRepository;

    public void addAuthor(Author author) {
        authorRepository.save(author);
    }
}
