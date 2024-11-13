package com.example.library.controller;

import com.example.library.entity.Author;
import com.example.library.entity.Reader;
import com.example.library.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/library")
public class LibraryController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping("/transaction-borrow")
    public ResponseEntity<String> transactionBorrow(@RequestParam("bookTitle") String bookTitle, @RequestParam("phoneReader") String phoneReader) {
        transactionService.borrowBook(bookTitle, phoneReader);
        return ResponseEntity.ok("Book " + bookTitle + " borrowed successfully");
    }

    @PostMapping("/transaction-return")
    public ResponseEntity<String> transactionReturn(@RequestParam("bookTitle") String bookTitle, @RequestParam("phoneReader") String phoneReader) {
        transactionService.returnBook(bookTitle, phoneReader);
        return ResponseEntity.ok("Book " + bookTitle + " returned successfully");
    }

    @GetMapping("/popular-author")
    public ResponseEntity<Author> getMostPopularAuthor(@RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate) {
        Author author = transactionService.getMostPopularAuthor(startDate, endDate);
        return ResponseEntity.ok(author);
    }

    @GetMapping("/active-reader")
    public ResponseEntity<Reader> getMostActiveReader() {
        Reader reader = transactionService.getMostActiveReader();
        return ResponseEntity.ok(reader);
    }

    @GetMapping("/sorted-all-readers-by-book-count")
    public ResponseEntity<List<Reader>> getSortedReadersByBooksCount() {
        List<Reader> readers = transactionService.getSortedReadersByBooksCount();
        return ResponseEntity.ok(readers);
    }
}
