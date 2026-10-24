package com.payflow.payflow.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


/**
 * Catches exceptions thrown by any Controller in the app and converts
 * them into a clean, consistent JSON error response — instead of Spring's
 * default verbose error output.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Triggered automatically when @Valid validation fails on a request body
     * (e.g. missing email, blank name).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)

public ResponseEntity<Map<String , Object>> handelValidtionErrors(MethodArgumentNotValidException ex)
    {
        Map<String , String> feildErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                feildErrors.put(error.getField() , error.getDefaultMessage()));

        Map<String , Object> response = new HashMap<>();
        response.put("timestamp" , LocalDateTime.now());
        response.put("status" , HttpStatus.BAD_REQUEST.value());
        response.put("error" , feildErrors);

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>>handelNotFound(ResourceNotFoundException ex)
    {
        Map<String , Object> response = new HashMap<>();
        response.put("timestamp" , LocalDateTime.now());
        response.put("status" , HttpStatus.NOT_FOUND.value());
        response.put("message" , ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }


    /**
     * Triggered when a status change is not allowed by the transaction lifecycle rules.
     *
     * @return 409 Conflict with a descriptive message
     */

    @ExceptionHandler(InvalidStatusTransitionException.class)
    public ResponseEntity<Map<String , Object>> handelInvalidTransition(InvalidStatusTransitionException ex)
    {
        Map<String , Object> response = new HashMap<>();
        response.put("timestamp" , LocalDateTime.now());
        response.put("status" , HttpStatus.CONFLICT.value());
        response.put("message" , ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }


    }
