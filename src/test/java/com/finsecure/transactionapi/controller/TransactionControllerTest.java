package com.finsecure.transactionapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finsecure.transactionapi.dto.TransactionRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.finsecure.transactionapi.service.TransactionValidationService;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper mapper;
    @MockBean TransactionValidationService service;

    @Test
    void validRequest_returns200() throws Exception {
        // stub service to return no errors
        org.mockito.Mockito.when(service.validate(org.mockito.ArgumentMatchers.any()))
                .thenReturn(java.util.List.of());

        TransactionRequest req = new TransactionRequest(new BigDecimal("100"), "USD", "ACC-1");

        mockMvc.perform(post("/api/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true));
    }

    @Test
    void invalidRequest_returns400() throws Exception {
        org.mockito.Mockito.when(service.validate(org.mockito.ArgumentMatchers.any()))
                .thenReturn(java.util.List.of("Amount must be greater than zero"));

        TransactionRequest req = new TransactionRequest(BigDecimal.ZERO, "USD", "ACC-1");

        mockMvc.perform(post("/api/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.valid").value(false));
    }
}