package org.example.lab2.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

@Schema(description = "Дані для створення або повного оновлення опитування")
public class PollRequest {

    @Schema(description = "Заголовок (питання) опитування", example = "Вибір старости групи")
    @NotBlank(message = "Заголовок опитування не може бути порожнім")
    private String title;

    @Schema(description = "Детальний опис опитування", example = "Голосування за старосту на осінній семестр")
    private String description;

    @Schema(description = "Власник / автор опитування", example = "admin", defaultValue = "admin")
    private String owner = "admin";

    @Schema(description = "Чи відкрите опитування для голосування", example = "true", defaultValue = "true")
    private Boolean active = true;

    @Schema(description = "Список кандидатів")
    @NotEmpty(message = "Список кандидатів не може бути порожнім")
    @Valid
    private List<CandidateRequest> candidates;

    public static class CandidateRequest {
        @Schema(description = "Ім'я або назва варіанту кандидата", example = "Іван Петренко")
        @NotBlank(message = "Ім'я кандидата обов'язкове")
        private String name;
        public CandidateRequest() {}
        public CandidateRequest(String name) {
            this.name = name;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getOwner() {
        return owner;
    }
    public void setOwner(String owner) {
        this.owner = owner;
    }
    public Boolean getActive() {
        return active;
    }
    public void setActive(Boolean active) {
        this.active = active;
    }
    public List<CandidateRequest> getCandidates() {
        return candidates;
    }
    public void setCandidates(List<CandidateRequest> candidates) {
        this.candidates = candidates;
    }
}