package com.example.library.service;

import com.example.library.entity.Author;
import com.example.library.entity.Transaction;
import com.example.library.exception.AuthorNotFoundException;
import com.example.library.repository.AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AuthorService {

    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private TransactionService transactionService;

    public void addAuthor(Author author) {
        authorRepository.save(author);
    }

    public Author getMostPopularAuthor(String startData, String endDate) {
        List<Transaction> transactionList = transactionService.findAllByTakenBetween(startData, endDate);
        Map<Author, Integer> authorMap = new HashMap<>();
        List<Author> authorList = new ArrayList<>();
        int counter = 1;
        for (Transaction transaction : transactionList) {
            authorList.addAll(transaction.getBook().getAuthors());
        }
        for (Author author : authorList) {
            if (!authorMap.containsKey(author)) {
                authorMap.put(author, counter);
            } else {
                authorMap.put(author, authorMap.get(author) + 1);
            }
        }
        if (authorMap.isEmpty()) {
            throw new AuthorNotFoundException("Author not found");
        }
        return Collections.max(authorMap.entrySet(), Map.Entry.comparingByValue()).getKey();
    }
}
