package com.xmajer.librarymanagementsystem.exception;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String entityType, Long id) {
        super("Entity type: '" + entityType + "' with id: '" + id + "' was not found.");
    }
}
