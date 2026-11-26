package org.example.lab2.Entity;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Data
public class Poll {
    private String id;
    @NotBlank(message = "Назва опитування є обов'язковою")
    private String title;
    private String description;
    private String owner = "admin";
    private boolean active = true;
    @NotEmpty(message = "Опитування повинно містити хоча б одного кандидата")
    @Valid
    private List<Candidate> candidates = new ArrayList<>();
    public Poll() {}
    public Poll(String id, String title, String description, boolean active, List<Candidate> candidates) {
        this(id, title, description, "admin", active, candidates);
    }

    public Poll(String id, String title, String description, String owner, boolean active, List<Candidate> candidates) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.owner = owner != null ? owner : "admin";
        this.active = active;
        this.candidates = candidates != null ? candidates : new ArrayList<>();
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
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
    public boolean isActive() {
        return active;
    }
    public void setActive(boolean active) {
        this.active = active;
    }
    public List<Candidate> getCandidates() {
        return candidates;
    }
    public void setCandidates(List<Candidate> candidates) {
        this.candidates = candidates;
    }
}