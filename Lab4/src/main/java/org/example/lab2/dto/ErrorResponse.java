package org.example.lab2.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.List;

@Schema(description = "Детальна інформація про помилку HTTP-запиту")
public class ErrorResponse {

    @Schema(description = "Час виникнення помилки", example = "2026-11-23T10:15:30Z")
    private Instant timestamp;

    @Schema(description = "HTTP статус-код", example = "404")
    private int status;

    @Schema(description = "Назва HTTP статусу", example = "Not Found")
    private String error;

    @Schema(description = "Повідомлення про причину помилки", example = "Опитування з ID 99 не знайдено")
    private String message;

    @Schema(description = "Шлях запиту", example = "/api/v1/polls/99")
    private String path;

    @Schema(description = "Список помилок валідації окремих полів (за наявності)")
    private List<String> details;

    public ErrorResponse(int status, String error, String message, String path, List<String> details) {
        this.timestamp = Instant.now();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.details = details;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
    public int getStatus() {
        return status;
    }
    public String getError() {
        return error;
    }
    public String getMessage() {
        return message;
    }
    public String getPath() {
        return path;
    }
    public List<String> getDetails() {
        return details;
    }
}