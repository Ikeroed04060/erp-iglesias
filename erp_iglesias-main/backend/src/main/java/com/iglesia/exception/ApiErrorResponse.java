package com.iglesia.exception;

import java.time.LocalDateTime;
import java.util.List;

public record ApiErrorResponse(
        int status,
        String error,
        String message,
        String path,
        LocalDateTime timestamp,
        List<FieldValidationError> details
) {
    public static ApiErrorResponse of(int status, String error, String message, String path) {
        return new ApiErrorResponse(
                status,
                error,
                message,
                path,
                LocalDateTime.now(),
                List.of()
        );
    }

    public static ApiErrorResponse of(
            int status,
            String error,
            String message,
            String path,
            List<FieldValidationError> details
    ) {
        return new ApiErrorResponse(
                status,
                error,
                message,
                path,
                LocalDateTime.now(),
                details
        );
    }
}
