package org.example.lab2.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VoteToken {
    String tokenValue;
    long timestamp;
}