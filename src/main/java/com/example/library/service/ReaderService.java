package com.example.library.service;

import com.example.library.entity.Reader;
import com.example.library.repository.ReaderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class ReaderService {

    @Autowired
    ReaderRepository readerRepository;

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
}
