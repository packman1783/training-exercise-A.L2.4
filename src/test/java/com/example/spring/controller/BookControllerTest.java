package com.example.spring.controller;

import com.example.spring.dto.BookCreateDTO;
import com.example.spring.mapper.BookMapper;
import com.example.spring.model.Author;
import com.example.spring.model.Book;
import com.example.spring.repository.AuthorRepository;
import com.example.spring.repository.BookRepository;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class BookControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private BookRepository bookRepository;

    private Book testBook;

    private Author anotherAuthor;

    private Author author;

    @BeforeEach
    public void setUp() {
        authorRepository.deleteAll();
        bookRepository.deleteAll();

        anotherAuthor = new Author();
        anotherAuthor.setFirstName("Iggy");
        anotherAuthor.setLastName("Pop");
        authorRepository.save(anotherAuthor);

        author = new Author();
        author.setFirstName("Mad");
        author.setLastName("Max");
        authorRepository.save(author);

        testBook = new Book();
        testBook.setAuthor(author);
        testBook.setTitle("Rage of road");
        bookRepository.save(testBook);
    }

    @Test
    @DisplayName("GET /books — returns a list of books")
    public void testIndex() throws Exception {
        var result = mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();

        assertThatJson(body).isArray();
    }

    @Test
    @DisplayName("GET /books/{id} — returns book data")
    public void testShow() throws Exception {
        var request = get("/books/{id}", testBook.getId());

        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("title").isEqualTo(testBook.getTitle()),
                v -> v.node("authorId").isEqualTo(testBook.getAuthor().getId()),
                v -> v.node("authorFirstName").isEqualTo(testBook.getAuthor().getFirstName()),
                v -> v.node("authorLastName").isEqualTo(testBook.getAuthor().getLastName())
        );
    }

    @Test
    @DisplayName("POST /books — creates a new book")
    public void testCreate() throws Exception {
        var dto = new BookCreateDTO();
        dto.setAuthorId(author.getId());
        dto.setTitle("Cabin porn");

        var request = post("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto));

        mockMvc.perform(request)
                .andExpect(status().isCreated());

        var book = bookRepository.findByTitle("Cabin porn").get();

        assertThat(book).isNotNull();
        assertThat(book.getTitle()).isEqualTo("Cabin porn");
        assertThat(book.getAuthor().getId()).isEqualTo(author.getId());
    }

    @Test
    @DisplayName("PUT /books/{id} — update the book")
    public void testUpdate() throws Exception {
        var dto = bookMapper.map(testBook);

        dto.setTitle("new title");
        dto.setAuthorId(anotherAuthor.getId());

        var request = put("/books/{id}", testBook.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        var task = bookRepository.findById(testBook.getId()).get();

        assertThat(task.getTitle()).isEqualTo(dto.getTitle());
        assertThat(task.getAuthor().getId()).isEqualTo(dto.getAuthorId());
    }

    @Test
    @DisplayName("PATCH /books/{id} — partially updates the book")
    public void testPartialUpdate() throws Exception {
        var dto = new HashMap<String, Long>();

        dto.put("authorId", anotherAuthor.getId());

        var request = put("/books/{id}", testBook.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        var task = bookRepository.findById(testBook.getId()).get();

        assertThat(task.getTitle()).isEqualTo(testBook.getTitle());
        assertThat(task.getAuthor().getId()).isEqualTo(dto.get("authorId"));
    }

    @Test
    @DisplayName("DELETE /books/{id} — delete the book")
    public void testDestroy() throws Exception {
        var request = delete("/books/{id}", testBook.getId());

        mockMvc.perform(request)
                .andExpect(status().isNoContent());

        assertThat(bookRepository.existsById(testBook.getId())).isFalse();
    }
}
