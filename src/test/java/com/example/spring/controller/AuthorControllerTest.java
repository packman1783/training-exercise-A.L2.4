package com.example.spring.controller;

import com.example.spring.dto.AuthorCreateDTO;
import com.example.spring.mapper.AuthorMapper;
import com.example.spring.model.Author;
import com.example.spring.repository.AuthorRepository;

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
public class AuthorControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthorMapper authorMapper;

    @Autowired
    private AuthorRepository authorRepository;

    private Author testAuthor;

    @BeforeEach
    public void setUp() {
        authorRepository.deleteAll();

        testAuthor = new Author();
        testAuthor.setFirstName("John");
        testAuthor.setLastName("Wick");
        authorRepository.save(testAuthor);
    }

    @Test
    @DisplayName("GET /authors — returns a list of authors")
    public void testIndex() throws Exception {
        var result = mockMvc.perform(get("/authors"))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();

        assertThatJson(body).isArray();
    }

    @Test
    @DisplayName("GET /authors/{id} — returns author data")
    public void testShow() throws Exception {
        var request = get("/authors/{id}", testAuthor.getId());

        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("firstName").isEqualTo(testAuthor.getFirstName()),
                v -> v.node("lastName").isEqualTo(testAuthor.getLastName())
        );
    }

    @Test
    @DisplayName("POST /authors — creates a new author")
    public void testCreate() throws Exception {
        var dto = new AuthorCreateDTO();
        dto.setFirstName("Max");
        dto.setLastName("Payne");

        var request = post("/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto));

        mockMvc.perform(request)
                .andExpect(status().isCreated());

        var author = authorRepository.findByFirstNameAndLastName("Max", "Payne").get();

        assertThat(author).isNotNull();
        assertThat(author.getFirstName()).isEqualTo("Max");
        assertThat(author.getLastName()).isEqualTo("Payne");
    }

    @Test
    @DisplayName("POST /authors — rejects empty name")
    public void testCreateWithNotValidName() throws Exception {
        var dto = authorMapper.map(testAuthor);
        dto.setLastName("");

        var request = post("/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto));

        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /authors/{id} — update the author")
    public void testUpdate() throws Exception {
        var dto = authorMapper.map(testAuthor);

        dto.setFirstName("new name");
        dto.setLastName("new last name");

        var request = put("/authors/{id}", testAuthor.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        var author = authorRepository.findById(testAuthor.getId()).get();

        assertThat(author.getFirstName()).isEqualTo(dto.getFirstName());
        assertThat(author.getLastName()).isEqualTo(dto.getLastName());
    }

    @Test
    @DisplayName("PATCH /authors/{id} — partially updates the author")
    public void testPartialUpdate() throws Exception {
        var dto = new HashMap<String, String>();
        dto.put("firstName", "another first name");

        var request = put("/authors/{id}", testAuthor.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        var author = authorRepository.findById(testAuthor.getId()).get();

        assertThat(author.getLastName()).isEqualTo(testAuthor.getLastName());
        assertThat(author.getFirstName()).isEqualTo(dto.get("firstName"));
    }

    @Test
    @DisplayName("DELETE /authors/{id} — delete the author")
    public void testDestroy() throws Exception {
        var request = delete("/books/{id}", testAuthor.getId());

        mockMvc.perform(request)
                .andExpect(status().isNoContent());
    }
}
