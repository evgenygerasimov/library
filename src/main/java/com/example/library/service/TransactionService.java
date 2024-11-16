package com.example.library.service;

import com.example.library.constants.BookStatus;
import com.example.library.entity.Book;
import com.example.library.entity.Reader;
import com.example.library.entity.Transaction;
import com.example.library.exception.BookNotFoundException;
import com.example.library.exception.ReaderNotFoundException;
import com.example.library.exception.TransactionException;
import com.example.library.repository.BookRepository;
import com.example.library.repository.ReaderRepository;
import com.example.library.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private ReaderRepository readerRepository;
    @Autowired
    private ReaderService readerService;
    @Autowired
    private BookService bookService;


    public void borrowBook(String titleBook, String phone, String firstName, String lastName, String gender, String birthDate) {

        Book book = bookService.findBookByName(titleBook);
        if (book == null) {
            throw new BookNotFoundException("Book not found");
        }

        if (book.getStatus().equals(BookStatus.BORROWED.toString())) {
            throw new TransactionException("Book already borrowed");
        }

        Reader reader = readerService.findByPhone(phone);
        if (reader == null) {
            reader = new Reader();
            reader.setPhone(phone);
            reader.setFirstName(firstName);
            reader.setLastName(lastName);
            reader.setGender(gender);
            LocalDate birthDateTime = LocalDate.parse(birthDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            reader.setBirthDate(birthDateTime);
            book.setStatus(BookStatus.BORROWED.toString());
            bookRepository.save(book);
        }
        book.setStatus(BookStatus.BORROWED.toString());
        bookRepository.save(book);

        reader.getBooks().add(book);
        readerRepository.save(reader);

        Transaction transaction = new Transaction();
        transaction.setBook(book);
        transaction.setReader(reader);
        transaction.setOperation(BookStatus.BORROWED.toString());
        transaction.setTransactionDate(LocalDateTime.now());
        transactionRepository.save(transaction);
    }

    public void returnBook(String titleBook, String readerPhone) {
        Book book = bookService.findBookByName(titleBook);
        if (book == null) {
            throw new BookNotFoundException("Book not found");
        }
        Reader reader = readerService.findByPhone(readerPhone);
        if (reader == null) {
            throw new ReaderNotFoundException("Reader not found");
        }
        if (!isTransactionExists(book, reader)) {
            throw new TransactionException("Transaction not found");
        } else {
            Transaction returnTransaction = new Transaction();
            returnTransaction.setBook(book);
            returnTransaction.setReader(reader);
            returnTransaction.setOperation(BookStatus.AVAILABLE.toString());
            returnTransaction.setTransactionDate(LocalDateTime.now());
            transactionRepository.save(returnTransaction);
            book.setStatus(BookStatus.AVAILABLE.toString());
            bookRepository.save(book);
            reader.getBooks().remove(book);
            readerRepository.save(reader);
        }
    }

    boolean isTransactionExists(Book book, Reader reader) {
        List<Transaction> transactionList = transactionRepository.findAll();
        for (Transaction transaction : transactionList) {
            if (transaction.getBook().equals(book) &&
                    transaction.getReader().equals(reader) &&
                    transaction.getOperation().equals(BookStatus.BORROWED.toString()) &&
                    book.getStatus().equals(BookStatus.BORROWED.toString())) {
                return true;
            }
        }
        return false;
    }

    public List<Transaction> findAllByTakenBetween(String startData, String endDate) {
        LocalDateTime startDateTime = LocalDateTime.parse(startData + "T00:00:00");
        LocalDateTime endDateTime = LocalDateTime.parse(endDate + "T23:59:59");
        List<Transaction> transactionList = transactionRepository.findAll();
        List<Transaction> filteredTransactionList = new ArrayList<>();
        for (Transaction transaction : transactionList) {
            if (transaction.getTransactionDate().isAfter(startDateTime) &&
                    transaction.getTransactionDate().isBefore(endDateTime)) {
                filteredTransactionList.add(transaction);
            }
        }
        return filteredTransactionList;
    }
}



