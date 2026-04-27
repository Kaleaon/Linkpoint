// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

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

    public MoneyTransaction()
    {
    }

    public MoneyTransaction(Long long1)
    {
        id = long1;
    }

    public MoneyTransaction(Long long1, Date date, UUID uuid, int i, int j)
    {
        id = long1;
        timestamp = date;
        agentUUID = uuid;
        transactionAmount = i;
        newBalance = j;
    }

    public UUID getAgentUUID()
    {
        return agentUUID;
    }

    public Long getId()
    {
        return id;
    }

    public int getNewBalance()
    {
        return newBalance;
    }

    public Date getTimestamp()
    {
        return timestamp;
    }

    public int getTransactionAmount()
    {
        return transactionAmount;
    }

    public void setAgentUUID(UUID uuid)
    {
        agentUUID = uuid;
    }

    public void setId(Long long1)
    {
        id = long1;
    }

    public void setNewBalance(int i)
    {
        newBalance = i;
    }

    public void setTimestamp(Date date)
    {
        timestamp = date;
    }

    public void setTransactionAmount(int i)
    {
        transactionAmount = i;
    }
}
