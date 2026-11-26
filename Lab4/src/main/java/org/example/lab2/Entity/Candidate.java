package org.example.lab2.Entity;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class Candidate {
    private String id;
    @NotBlank(message = "Ім'я кандидата не може бути порожнім")
    private String name;
    @Min(value = 0, message = "Кількість голосів не може бути від'ємною")
    private int votes;
    public Candidate() {}

    public Candidate(String id, String name, int votes) {
        this.id = id;
        this.name = name;
        this.votes = votes;
    }
    public String getId() {
        return id;
    }
    @com.fasterxml.jackson.annotation.JsonIgnore
    public int getVotesCount() {
        return votes;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getVotes() {
        return votes;
    }
    public void setVotes(int votes) {
        this.votes = votes;
    }
}