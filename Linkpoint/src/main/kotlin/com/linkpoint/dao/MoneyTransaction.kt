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

     public fun getAgentUUID(): UUID {
        return this.agentUUID
    }

     public fun getId(): Long {
        return this.id
    }

     public fun getNewBalance(): Int {
        return this.newBalance
    }

     public fun getTimestamp(): Date {
        return this.timestamp
    }

     public fun getTransactionAmount(): Int {
        return this.transactionAmount
    }

    fun setAgentUUID(uuid: UUID) {
        this.agentUUID = uuid
    }

    fun setId(l: Long) {
        this.id = l
    }

    fun setNewBalance(i: Int) {
        this.newBalance = i
    }

    fun setTimestamp(date: Date) {
        this.timestamp = date
    }

    fun setTransactionAmount(i: Int) {
        this.transactionAmount = i
    }
}
