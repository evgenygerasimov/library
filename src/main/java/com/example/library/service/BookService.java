package com.example.library.service;

import com.example.library.entity.Book;
import com.example.library.repository.BookRepository;
import com.example.library.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    @Autowired
    BookRepository bookRepository;
    @Autowired
    TransactionRepository transactionRepository;

    public Book findBookByName(String title) {
        List<Book> books = bookRepository.findAll();
        for (Book book : books) {
            if (book.getTitle().equals(title)) {
                return book;
            }
        }
        return null;
    }

    public void addBook(Book book) {
        bookRepository.save(book);
    }
}
