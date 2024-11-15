package com.example.library.service;

import com.example.library.entity.Reader;
import com.example.library.entity.Transaction;
import com.example.library.exception.ReaderNotFoundException;
import com.example.library.repository.ReaderRepository;
import com.example.library.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ReaderService {

    @Autowired
    ReaderRepository readerRepository;
    @Autowired
    TransactionRepository transactionRepository;

    public Reader findByPhone(String phone) {
        List<Reader> readers = readerRepository.findAll();
        for (Reader reader : readers) {
            if (reader.getPhone().equals(phone)) {
                return reader;
            }
        }
        return null;
    }

    public void addReader(Reader reader) {
        readerRepository.save(reader);
    }

    public List<Reader> getSortedReadersByBooksCount() {
        List<Reader> allReaders = readerRepository.findAll();
        allReaders.sort(new Comparator<Reader>() {
            @Override
            public int compare(Reader o1, Reader o2) {
                return o2.getBooks().size() - o1.getBooks().size();
            }
        });
        return allReaders;
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
            throw new ReaderNotFoundException("Reader not found");
        } else {
            return mostActiveReaderreader;
        }
    }
}
