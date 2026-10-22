package com.finsecure.transactionapi.controller;

import com.finsecure.transactionapi.dto.TransactionRequest;
import com.finsecure.transactionapi.dto.ValidationResponse;
import com.finsecure.transactionapi.service.TransactionValidationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TransactionController {

    private final TransactionValidationService validationService;

    public TransactionController(TransactionValidationService validationService) {
        this.validationService = validationService;
    }

    @PostMapping("/validate")
    public ResponseEntity<ValidationResponse> validate(@RequestBody TransactionRequest request) {
        List<String> errors = validationService.validate(request);
        if (errors.isEmpty()) {
            return ResponseEntity.ok(new ValidationResponse(true, "Valid transaction - Jenkins is LIVE!!", null));
        }
        return ResponseEntity.badRequest()
                .body(new ValidationResponse(false, "Validation failed", errors));
    }
}