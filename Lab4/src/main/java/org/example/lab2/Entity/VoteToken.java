package org.example.lab2.Entity;

import lombok.Data;

@Data
public class VoteToken {
    private String token;
    private long createdAt;
    private boolean used;

    public VoteToken() {}

    public VoteToken(String token, long createdAt) {
        this.token = token;
        this.createdAt = createdAt;
        this.used = false;
    }

    public VoteToken(String token, boolean used) {
        this.token = token;
        this.createdAt = System.currentTimeMillis();
        this.used = used;
    }

    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
    public String getTokenValue() {
        return token;
    }
    public long getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
    public boolean isUsed() {
        return used;
    }
    public void setUsed(boolean used) {
        this.used = used;
    }
}