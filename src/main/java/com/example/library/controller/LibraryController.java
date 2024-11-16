package com.example.library.controller;

import com.example.library.entity.Author;
import com.example.library.entity.Book;
import com.example.library.entity.Reader;
import com.example.library.security.JwtService;
import com.example.library.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/library")
public class LibraryController {

    @Autowired
    private TransactionService transactionService;
    @Autowired
    private ReaderService readerService;
    @Autowired
    EmployeeService employeeService;
    @Autowired
    BookService bookService;
    @Autowired
    AuthorService authorService;
    @Autowired
    private JwtService jwtService;

    @PostMapping("/transaction-borrow")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<Book> transactionBorrow(@RequestParam("bookTitle") String bookTitle
            , @RequestParam("phoneReader") String phoneReader
            , @RequestParam("firstName") String firstName
            , @RequestParam("lastName") String lastName
            , @RequestParam("gender") String gender
            , @RequestParam("birthDate") String birthDate
            ,@RequestHeader("Authorization") String token) {
        if (!jwtService.getToken(token).isValid()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        transactionService.borrowBook(bookTitle, phoneReader, firstName, lastName, gender, birthDate);
        return ResponseEntity.ok(bookService.findBookByName(bookTitle));
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping("/transaction-return")
    public ResponseEntity<Book> transactionReturn(@RequestParam("bookTitle") String bookTitle, @RequestParam("phoneReader") String phoneReader, @RequestHeader("Authorization") String token) {
        if (!jwtService.getToken(token).isValid()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        transactionService.returnBook(bookTitle, phoneReader);
        return ResponseEntity.ok(bookService.findBookByName(bookTitle));
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/popular-author")
    public ResponseEntity<Author> getMostPopularAuthor(@RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate, @RequestHeader("Authorization") String token) {
        if (!jwtService.getToken(token).isValid()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Author author = authorService.getMostPopularAuthor(startDate, endDate);
        return ResponseEntity.ok(author);
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/active-reader")
    public ResponseEntity<Reader> getMostActiveReader(@RequestHeader("Authorization") String token) {
        if (!jwtService.getToken(token).isValid()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Reader reader = readerService.getMostActiveReader();
        return ResponseEntity.ok(reader);
    }

    @GetMapping("/sorted-all-readers-by-book-count")
    public ResponseEntity<List<Reader>> getSortedReadersByBooksCount() {
        List<Reader> readers = readerService.getSortedReadersByBooksCount();
        return ResponseEntity.ok(readers);
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping("/addNewBook")
    public ResponseEntity<Book> addNewBook(@RequestBody Book book, @RequestHeader("Authorization") String token) {
        if (!jwtService.getToken(token).isValid()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        bookService.addBook(book);
        return ResponseEntity.ok(bookService.findBookByName(book.getTitle()));
    }
}
