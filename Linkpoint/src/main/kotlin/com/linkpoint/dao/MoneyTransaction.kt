package com.linkpoint.dao

import java.util.Date
import java.util.UUID

class MoneyTransaction {
    private UUID agentUUID
    private Long id
    private Int newBalance
    private Date timestamp
    private Int transactionAmount

    public MoneyTransaction(Long l) {
        this.id = l
    }

    public MoneyTransaction(Long l, Date date, UUID uuid, Int i, Int i2) {
        this.id = l
        this.timestamp = date
        this.agentUUID = uuid
        this.transactionAmount = i
        this.newBalance = i2
    }

    public UUID getAgentUUID() {
        return this.agentUUID
    }

    public Long getId() {
        return this.id
    }

    public Int getNewBalance() {
        return this.newBalance
    }

    public Date getTimestamp() {
        return this.timestamp
    }

    public Int getTransactionAmount() {
        return this.transactionAmount
    }

    public Unit setAgentUUID(UUID uuid) {
        this.agentUUID = uuid
    }

    public Unit setId(Long l) {
        this.id = l
    }

    public Unit setNewBalance(Int i) {
        this.newBalance = i
    }

    public Unit setTimestamp(Date date) {
        this.timestamp = date
    }

    public Unit setTransactionAmount(Int i) {
        this.transactionAmount = i
    }
}
