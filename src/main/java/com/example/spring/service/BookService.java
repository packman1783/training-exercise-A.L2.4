package com.example.spring.service;

import com.example.spring.dto.BookCreateDTO;
import com.example.spring.dto.BookDTO;
import com.example.spring.dto.BookUpdateDTO;
import com.example.spring.exeption.ResourceNotFoundException;
import com.example.spring.mapper.BookMapper;
import com.example.spring.model.Book;
import com.example.spring.repository.BookRepository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookService {
    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookMapper bookMapper;

    public List<BookDTO> getAll() {
        List<Book> books = bookRepository.findAll();

        return books.stream()
                .map(bookMapper::map)
                .toList();
    }

    public BookDTO findById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book with id " + id + " not found"));

        return bookMapper.map(book);
    }

    public BookDTO create(BookCreateDTO data) {
        Book book = bookMapper.map(data);
        bookRepository.save(book);

        return bookMapper.map(book);
    }

    public BookDTO update(BookUpdateDTO data, long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book with id " + id + " not found"));

        bookMapper.update(data, book);
        bookRepository.save(book);

        return bookMapper.map(book);
    }

    public void delete(long id) {
        bookRepository.deleteById(id);
    }
}
