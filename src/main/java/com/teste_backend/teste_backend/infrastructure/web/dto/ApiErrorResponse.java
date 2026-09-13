package com.teste_backend.teste_backend.infrastructure.web.dto;

import java.time.OffsetDateTime;

public record ApiErrorResponse(OffsetDateTime timestamp, int status, String error, String message, String path) {
}
