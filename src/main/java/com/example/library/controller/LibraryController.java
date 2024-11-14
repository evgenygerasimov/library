package com.example.library.controller;

import com.example.library.entity.Author;
import com.example.library.entity.Reader;
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

    @PostMapping("/transaction-borrow")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<?> transactionBorrow(@RequestParam("bookTitle") String bookTitle, @RequestParam("phoneReader") String phoneReader) {
        transactionService.borrowBook(bookTitle, phoneReader);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping("/transaction-return")
    public ResponseEntity<String> transactionReturn(@RequestParam("bookTitle") String bookTitle, @RequestParam("phoneReader") String phoneReader) {
        transactionService.returnBook(bookTitle, phoneReader);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/popular-author")
    public ResponseEntity<Author> getMostPopularAuthor(@RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate) {
        Author author = transactionService.getMostPopularAuthor(startDate, endDate);
        return ResponseEntity.ok(author);
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/active-reader")
    public ResponseEntity<Reader> getMostActiveReader() {
        Reader reader = transactionService.getMostActiveReader();
        return ResponseEntity.ok(reader);
    }

    @GetMapping("/sorted-all-readers-by-book-count")
    public ResponseEntity<List<Reader>> getSortedReadersByBooksCount() {
        List<Reader> readers = readerService.getSortedReadersByBooksCount();
        return ResponseEntity.ok(readers);
    }
}
