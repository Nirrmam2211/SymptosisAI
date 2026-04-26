package com.symptosis.backend.dto;

import java.time.LocalDateTime;

public class DatabaseProbeResponse {
    private boolean connected;
    private String message;
    private LocalDateTime testedAt;

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTestedAt() {
        return testedAt;
    }

    public void setTestedAt(LocalDateTime testedAt) {
        this.testedAt = testedAt;
    }
}
