package com.teste_backend.teste_backend.infrastructure.web;

import com.teste_backend.teste_backend.domain.order.exception.InvalidTransitionException;
import com.teste_backend.teste_backend.domain.order.exception.OrderNotFoundException;
import com.teste_backend.teste_backend.domain.order.exception.OrderStatusConflictException;
import com.teste_backend.teste_backend.domain.time.ServerClock;
import com.teste_backend.teste_backend.infrastructure.web.dto.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
class GlobalExceptionHandler {

    private final ServerClock clock;

    GlobalExceptionHandler(ServerClock clock) {
        this.clock = clock;
    }

    @ExceptionHandler(OrderNotFoundException.class)
    ResponseEntity<ApiErrorResponse> handleNotFound(OrderNotFoundException ex, HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(OrderStatusConflictException.class)
    ResponseEntity<ApiErrorResponse> handleConflict(OrderStatusConflictException ex, HttpServletRequest request) {
        return build(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    @ExceptionHandler(InvalidTransitionException.class)
    ResponseEntity<ApiErrorResponse> handleInvalid(InvalidTransitionException ex, HttpServletRequest request) {
        return build(HttpStatus.UNPROCESSABLE_CONTENT, ex.getMessage(), request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<ApiErrorResponse> handleMalformedBody(HttpMessageNotReadableException ex, HttpServletRequest request) {
        return build(HttpStatus.UNPROCESSABLE_CONTENT, "Corpo da requisição inválido.", request);
    }

    private ResponseEntity<ApiErrorResponse> build(HttpStatus status, String message, HttpServletRequest request) {
        ApiErrorResponse body = new ApiErrorResponse(
                clock.now(), status.value(), status.getReasonPhrase(), message, request.getRequestURI());
        return ResponseEntity.status(status).body(body);
    }
}
