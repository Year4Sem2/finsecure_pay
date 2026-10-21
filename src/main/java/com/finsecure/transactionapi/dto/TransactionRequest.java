package com.finsecure.transactionapi.dto;

import java.math.BigDecimal;

public class TransactionRequest {
    private BigDecimal amount;
    private String currency;
    private String accountId;

    public TransactionRequest() {}
    public TransactionRequest(BigDecimal amount, String currency, String accountId) {
        this.amount = amount;
        this.currency = currency;
        this.accountId = accountId;
    }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }
}