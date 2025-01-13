package com.nhnacademy.bookapi.credentials;

import static org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class DatabaseCredentialsTest {

    @Test
    void testDatabaseCredentials_ValidInput() {
        // Given
        String input = "jdbc:mysql://localhost:3306/testdb\nuser\npassword";

        // When
        DatabaseCredentials credentials = new DatabaseCredentials(input);

        // Then
        assertEquals("jdbc:mysql://localhost:3306/testdb", credentials.getUrl());
        assertEquals("user", credentials.getUsername());
        assertEquals("password", credentials.getPassword());
    }

    @Test
    void testDatabaseCredentials_InvalidInput_MissingFields() {
        // Given
        String input = "jdbc:mysql://localhost:3306/testdb\nuser";

        // When & Then
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> {
            new DatabaseCredentials(input);
        });
    }

    @Test
    void testDatabaseCredentials_EmptyInput() {
        // Given
        String input = "";

        // When & Then
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> {
            new DatabaseCredentials(input);
        });
    }

    @Test
    void testDatabaseCredentials_NullInput() {
        // Given
        String input = null;

        // When & Then
        assertThrows(NullPointerException.class, () -> {
            new DatabaseCredentials(input);
        });
    }
}