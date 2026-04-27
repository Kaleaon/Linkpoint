// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.dao;

import java.util.Date;
import java.util.UUID;

public class MoneyTransaction
{
    private UUID agentUUID;
    private Long id;
    private int newBalance;
    private Date timestamp;
    private int transactionAmount;
    
    public MoneyTransaction() {
    }
    
    public MoneyTransaction(final Long id) {
        this.id = id;
    }
    
    public MoneyTransaction(final Long id, final Date timestamp, final UUID agentUUID, final int transactionAmount, final int newBalance) {
        this.id = id;
        this.timestamp = timestamp;
        this.agentUUID = agentUUID;
        this.transactionAmount = transactionAmount;
        this.newBalance = newBalance;
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
    
    public void setAgentUUID(final UUID agentUUID) {
        this.agentUUID = agentUUID;
    }
    
    public void setId(final Long id) {
        this.id = id;
    }
    
    public void setNewBalance(final int newBalance) {
        this.newBalance = newBalance;
    }
    
    public void setTimestamp(final Date timestamp) {
        this.timestamp = timestamp;
    }
    
    public void setTransactionAmount(final int transactionAmount) {
        this.transactionAmount = transactionAmount;
    }
}
