package com.iglesia.exception;

public record FieldValidationError(
        String field,
        String message
) {
}
