package com.lumiyaviewer.lumiya.dao;

import java.util.Date;
import java.util.UUID;

public class MoneyTransaction {
    private UUID agentUUID;
    private Long id;
    private int newBalance;
    private Date timestamp;
    private int transactionAmount;

    public MoneyTransaction(Long l) {
        this.id = l;
    }

    public MoneyTransaction(Long l, Date date, UUID uuid, int i, int i2) {
        this.id = l;
        this.timestamp = date;
        this.agentUUID = uuid;
        this.transactionAmount = i;
        this.newBalance = i2;
    }

    public UUID getAgentUUID() {
        return this.agentUUID;
    }

    public Long getId() {
        return this.id;
    }

    public int getNewBalance() {
        return this.newBalance;
    }

    public Date getTimestamp() {
        return this.timestamp;
    }

    public int getTransactionAmount() {
        return this.transactionAmount;
    }

    public void setAgentUUID(UUID uuid) {
        this.agentUUID = uuid;
    }

    public void setId(Long l) {
        this.id = l;
    }

    public void setNewBalance(int i) {
        this.newBalance = i;
    }

    public void setTimestamp(Date date) {
        this.timestamp = date;
    }

    public void setTransactionAmount(int i) {
        this.transactionAmount = i;
    }
}
