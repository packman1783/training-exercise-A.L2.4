package com.example.spring.dto;

import org.openapitools.jackson.nullable.JsonNullable;

public class AuthorUpdateDTO {
    private JsonNullable<String> firstName;

    private JsonNullable<String> lastName;

    public JsonNullable<String> getFirstName() {
        return firstName;
    }

    public void setFirstName(JsonNullable<String> firstName) {
        this.firstName = firstName;
    }

    public JsonNullable<String> getLastName() {
        return lastName;
    }

    public void setLastName(JsonNullable<String> lastName) {
        this.lastName = lastName;
    }
}
