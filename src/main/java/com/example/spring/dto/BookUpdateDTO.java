package com.example.spring.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.openapitools.jackson.nullable.JsonNullable;

public class BookUpdateDTO {
    @NotNull
    JsonNullable<Long> authorId;

    @NotBlank
    JsonNullable<String> title;

    public JsonNullable<String> getTitle() {
        return title;
    }

    public void setTitle(JsonNullable<String> title) {
        this.title = title;
    }

    public void setAuthorId(JsonNullable<Long> authorId) {
        this.authorId = authorId;
    }
}
