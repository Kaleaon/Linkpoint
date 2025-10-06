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

    fun setAgentUUID(UUID uuid) {
        this.agentUUID = uuid
    }

    fun setId(Long l) {
        this.id = l
    }

    fun setNewBalance(Int i) {
        this.newBalance = i
    }

    fun setTimestamp(Date date) {
        this.timestamp = date
    }

    fun setTransactionAmount(Int i) {
        this.transactionAmount = i
    }
}
