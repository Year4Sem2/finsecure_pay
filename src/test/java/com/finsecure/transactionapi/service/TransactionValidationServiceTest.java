package com.finsecure.transactionapi.service;

import com.finsecure.transactionapi.dto.TransactionRequest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TransactionValidationServiceTest {

    private final TransactionValidationService service = new TransactionValidationService();

    @Test
    void validRequest_returnsNoErrors() {
        TransactionRequest req = new TransactionRequest(new BigDecimal("100"), "USD", "ACC-1");
        assertTrue(service.validate(req).isEmpty());
    }

    @Test
    void zeroAmount_returnsError() {
        TransactionRequest req = new TransactionRequest(BigDecimal.ZERO, "USD", "ACC-1");
        List<String> errors = service.validate(req);
        assertEquals(1, errors.size());
        assertTrue(errors.contains("Amount must be greater than zero"));
    }

    @Test
    void amountAboveMax_returnsError() {
        TransactionRequest req = new TransactionRequest(new BigDecimal("50000"), "USD", "ACC-1");
        assertTrue(service.validate(req).get(0).contains("exceeds maximum"));
    }

    @Test
    void unsupportedCurrency_returnsError() {
        TransactionRequest req = new TransactionRequest(new BigDecimal("100"), "XYZ", "ACC-1");
        assertTrue(service.validate(req).contains("Unsupported currency: XYZ"));
    }

    @Test
    void blankAccountId_returnsError() {
        TransactionRequest req = new TransactionRequest(new BigDecimal("100"), "USD", "   ");
        assertTrue(service.validate(req).contains("Account ID is required"));
    }
}