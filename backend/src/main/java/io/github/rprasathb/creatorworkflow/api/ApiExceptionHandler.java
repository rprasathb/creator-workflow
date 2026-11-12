package io.github.rprasathb.creatorworkflow.api;

import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestControllerAdvice
public class ApiExceptionHandler {
    public record ApiError(String code, String message, List<String> details) {}

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiError> validation(MethodArgumentNotValidException error) {
        return ResponseEntity.badRequest().body(new ApiError("VALIDATION_ERROR", "Invalid request",
            error.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage()).toList()));
    }

    @ExceptionHandler(ResponseStatusException.class)
    ResponseEntity<ApiError> status(ResponseStatusException error) {
        HttpStatusCode code = error.getStatusCode();
        return ResponseEntity.status(code).body(new ApiError(
            code.value() == 404 ? "NOT_FOUND" : code.value() == 409 ? "CONFLICT" : "BAD_REQUEST",
            error.getReason(), List.of()));
    }

    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    ResponseEntity<ApiError> malformed(Exception error) {
        return ResponseEntity.badRequest().body(new ApiError("INVALID_JSON",
            "Invalid JSON or unsupported enum value", List.of()));
    }
}
