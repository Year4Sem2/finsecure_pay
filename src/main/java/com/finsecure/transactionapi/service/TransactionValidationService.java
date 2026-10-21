package com.finsecure.transactionapi.service;

import com.finsecure.transactionapi.dto.TransactionRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class TransactionValidationService {

    private static final BigDecimal MAX_AMOUNT = new BigDecimal("10000");
    private static final Set<String> SUPPORTED_CURRENCIES = Set.of("USD", "EUR", "GBP");

    public List<String> validate(TransactionRequest request) {
        List<String> errors = new ArrayList<>();

        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            errors.add("Amount must be greater than zero");
        } else if (request.getAmount().compareTo(MAX_AMOUNT) > 0) {
            errors.add("Amount exceeds maximum allowed of " + MAX_AMOUNT);
        }
        if (request.getCurrency() == null || !SUPPORTED_CURRENCIES.contains(request.getCurrency())) {
            errors.add("Unsupported currency: " + request.getCurrency());
        }
        if (request.getAccountId() == null || request.getAccountId().isBlank()) {
            errors.add("Account ID is required");
        }
        return errors;
    }
}