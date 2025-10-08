package com.example.spring.service;

import com.example.spring.dto.AuthorCreateDTO;
import com.example.spring.dto.AuthorDTO;
import com.example.spring.dto.AuthorUpdateDTO;
import com.example.spring.exeption.ResourceNotFoundException;
import com.example.spring.mapper.AuthorMapper;
import com.example.spring.model.Author;
import com.example.spring.repository.AuthorRepository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthorService {
    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private AuthorMapper authorMapper;

    public List<AuthorDTO> getAll() {
        List<Author> authors = authorRepository.findAll();

        return authors.stream()
                .map(authorMapper::map)
                .toList();
    }

    public AuthorDTO findById(long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author with id " + id + " not found"));

        return authorMapper.map(author);
    }

    public AuthorDTO create(AuthorCreateDTO data) {
        Author author = authorMapper.map(data);
        authorRepository.save(author);

        return authorMapper.map(author);
    }

    public AuthorDTO update(AuthorUpdateDTO data, long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author with id " + id + " not found"));

        authorMapper.update(data, author);
        authorRepository.save(author);

        return authorMapper.map(author);
    }

    public void delete(long id) {
        authorRepository.deleteById(id);
    }
}
