package com.example.library.service;

import com.example.library.entity.Author;
import com.example.library.entity.Book;
import com.example.library.repository.AuthorRepository;
import com.example.library.repository.BookRepository;
import com.example.library.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BookService {

    @Autowired
    BookRepository bookRepository;
    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    AuthorRepository authorRepository;

    public Book findBookByName(String title) {
        List<Book> books = bookRepository.findAll();
        for (Book book : books) {
            if (book.getTitle().equals(title)) {
                return book;
            }
        }
        return null;
    }

    public void saveBook(Book book) {
        bookRepository.save(book);
    }

    @Transactional
    public void addBook(Book book) {
        List<Author> newAuthors = new ArrayList<>();
        for (Author author : book.getAuthors()) {
            Author existingAuthor = authorRepository.findByFirstNameAndLastNameAndBirthDate(
                    author.getFirstName(), author.getLastName(), author.getBirthDate());
            if (existingAuthor != null) {
                newAuthors.add(existingAuthor);
            } else {
                newAuthors.add(author);
            }
        }
        book.setAuthors(newAuthors);

        bookRepository.save(book);
    }
}
