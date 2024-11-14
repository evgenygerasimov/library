package com.example.library.service;

import com.example.library.constants.BookStatus;
import com.example.library.entity.Author;
import com.example.library.entity.Book;
import com.example.library.entity.Reader;
import com.example.library.entity.Transaction;
import com.example.library.repository.BookRepository;
import com.example.library.repository.ReaderRepository;
import com.example.library.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

    public void borrowBook(String titleBook, String phone) {
        Book book = bookService.findBookByName(titleBook);
        Reader reader = readerService.findByPhone(phone);
        if (reader == null) {
            throw new RuntimeException("Reader not found");
        }
        if (book.getStatus().equals(BookStatus.BORROWED.toString())) {
            throw new RuntimeException("Book already borrowed");
        }
        Transaction transaction = new Transaction();
        transaction.setBook(book);
        transaction.setReader(reader);
        transaction.setOperation(BookStatus.BORROWED.toString());
        transaction.setTransactionDate(LocalDateTime.now());
        transactionRepository.save(transaction);
        book.setStatus(BookStatus.BORROWED.toString());
        book.getReaders().add(reader);
        bookRepository.save(book);
        reader.getBooks().add(book);
        readerRepository.save(reader);
    }


    public void returnBook(String titleBook, String readerPhone) {
        Book book = bookService.findBookByName(titleBook);
        if (book == null) {
            throw new RuntimeException("Book not found");
        }
        Reader reader = readerService.findByPhone(readerPhone);
        if (reader == null) {
            throw new RuntimeException("Reader not found");
        }
        if (!isTransactionExists(book, reader)) {
            throw new RuntimeException("Transaction not found");
        } else {
            Transaction returnTransaction = new Transaction();
            returnTransaction.setBook(book);
            returnTransaction.setReader(reader);
            returnTransaction.setOperation(BookStatus.AVAILABLE.toString());
            returnTransaction.setTransactionDate(LocalDateTime.now());
            transactionRepository.save(returnTransaction);
            book.setStatus(BookStatus.AVAILABLE.toString());
            book.getReaders().remove(reader);
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

    public Author getMostPopularAuthor(String startData, String endDate) {
        List<Transaction> transactionList = findAllByTakenBetween(startData, endDate);
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
        Author mostPopularAuthor = Collections.max(authorMap.entrySet(), Map.Entry.comparingByValue()).getKey();
        if (mostPopularAuthor == null) {
            throw new NullPointerException("No authors found");
        }else {
            return mostPopularAuthor;
        }
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

    public Reader getMostActiveReader() {
        List<Transaction> transactionList = transactionRepository.findAll();
        Map<Reader, Integer> readerMap = new HashMap<>();
        int counter = 1;
        for (Transaction transaction : transactionList) {
            if (!readerMap.containsKey(transaction.getReader())) {
                readerMap.put(transaction.getReader(), counter);
            } else {
                readerMap.put(transaction.getReader(), readerMap.get(transaction.getReader()) + 1);
            }
        }
        Reader mostActiveReaderreader = Collections.max(readerMap.entrySet(), Map.Entry.comparingByValue()).getKey();
        if (mostActiveReaderreader == null) {
            throw new NullPointerException("No readers found");
        } else {
            return mostActiveReaderreader;
        }
    }
}

