package org.example.lab2.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Запит на подання голосу в опитуванні")
public class VoteRequest {

    @Schema(description = "Ідентифікатор кандидата", example = "100")
    @NotBlank(message = "Ідентифікатор кандидата є обов'язковим")
    private String candidateId;

    @Schema(description = "Одноразовий токен виборця", example = "TOKEN-LAB4-001")
    @NotBlank(message = "Токен голосування є обов'язковим")
    private String token;

    public String getCandidateId() {
        return candidateId;
    }
    public void setCandidateId(String candidateId) {
        this.candidateId = candidateId;
    }
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
}