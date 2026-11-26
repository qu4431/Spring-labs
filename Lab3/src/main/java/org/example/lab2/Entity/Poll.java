package org.example.lab2.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Poll {
    String id;
    String title;
    String owner;
    boolean active = true;
    List<Candidate> candidates = new ArrayList<>();
}