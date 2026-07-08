package com.xmajer.librarymanagementsystem.exception;

public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String entityType, String parameterType, String parameterValue) {
        super(entityType + " with " + parameterType + ": '" + parameterValue + "' already exists");
    }
}
