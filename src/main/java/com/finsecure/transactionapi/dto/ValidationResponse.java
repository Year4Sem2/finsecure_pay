package com.finsecure.transactionapi.dto;

import java.util.List;

public class ValidationResponse {
    private boolean valid;
    private String message;
    private List<String> errors;

    public ValidationResponse(boolean valid, String message, List<String> errors) {
        this.valid = valid;
        this.message = message;
        this.errors = errors;
    }

    public boolean isValid() { return valid; }
    public String getMessage() { return message; }
    public List<String> getErrors() { return errors; }
}