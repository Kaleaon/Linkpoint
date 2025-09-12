// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.modules.finance;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.dao.DaoSession;
import com.lumiyaviewer.lumiya.dao.MoneyTransaction;
import com.lumiyaviewer.lumiya.dao.MoneyTransactionDao;
import com.lumiyaviewer.lumiya.eventbus.EventBus;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.SLCircuitInfo;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.events.SLBalanceChangedEvent;
import com.lumiyaviewer.lumiya.slproto.messages.EconomyData;
import com.lumiyaviewer.lumiya.slproto.messages.EconomyDataRequest;
import com.lumiyaviewer.lumiya.slproto.messages.MoneyBalanceReply;
import com.lumiyaviewer.lumiya.slproto.messages.MoneyBalanceRequest;
import com.lumiyaviewer.lumiya.slproto.messages.MoneyTransferRequest;
import com.lumiyaviewer.lumiya.slproto.modules.SLModule;
import com.lumiyaviewer.lumiya.slproto.users.manager.BalanceManager;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class SLFinancialInfo extends SLModule
{

    private static final int DEFAULT_UPLOAD_COST = 10;
    private int balance;
    private boolean balanceKnown;
    private final Object balanceLock = new Object();
    private final MoneyTransactionDao moneyTransactionDao;
    private final AtomicInteger uploadCost = new AtomicInteger(10);
    private final UserManager userManager;

    public SLFinancialInfo(SLAgentCircuit slagentcircuit)
    {
        super(slagentcircuit);
        balanceKnown = false;
        balance = 0;
        userManager = UserManager.getUserManager(slagentcircuit.getAgentUUID());
        if (userManager != null)
        {
            moneyTransactionDao = userManager.getDaoSession().getMoneyTransactionDao();
            userManager.getBalanceManager().setFinancialInfo(this);
            return;
        } else
        {
            moneyTransactionDao = null;
            return;
        }
    }

    private void RequestEconomyData()
    {
        EconomyDataRequest economydatarequest = new EconomyDataRequest();
        economydatarequest.isReliable = true;
        SendMessage(economydatarequest);
    }

    private void setKnownBalance(int i)
    {
        Object obj = balanceLock;
        obj;
        JVM INSTR monitorenter ;
        balanceKnown = true;
        balance = i;
        obj;
        JVM INSTR monitorexit ;
        if (userManager != null)
        {
            userManager.getBalanceManager().updateBalance(i);
        }
        return;
        Exception exception;
        exception;
        throw exception;
    }

    public void AskForMoneyBalance()
    {
        MoneyBalanceRequest moneybalancerequest = new MoneyBalanceRequest();
        moneybalancerequest.AgentData_Field.AgentID = circuitInfo.agentID;
        moneybalancerequest.AgentData_Field.SessionID = circuitInfo.sessionID;
        moneybalancerequest.MoneyData_Field.TransactionID = new UUID(0L, 0L);
        moneybalancerequest.isReliable = true;
        SendMessage(moneybalancerequest);
    }

    public void DoPayObject(UUID uuid, int i)
    {
        MoneyTransferRequest moneytransferrequest = new MoneyTransferRequest();
        moneytransferrequest.AgentData_Field.AgentID = circuitInfo.agentID;
        moneytransferrequest.AgentData_Field.SessionID = circuitInfo.sessionID;
        moneytransferrequest.MoneyData_Field.SourceID = circuitInfo.agentID;
        moneytransferrequest.MoneyData_Field.DestID = uuid;
        moneytransferrequest.MoneyData_Field.Flags = 0;
        moneytransferrequest.MoneyData_Field.Amount = i;
        moneytransferrequest.MoneyData_Field.AggregatePermInventory = 0;
        moneytransferrequest.MoneyData_Field.AggregatePermNextOwner = 0;
        moneytransferrequest.MoneyData_Field.Description = SLMessage.stringToVariableOEM("");
        moneytransferrequest.MoneyData_Field.TransactionType = 5008;
        moneytransferrequest.isReliable = true;
        SendMessage(moneytransferrequest);
    }

    public void DoPayUser(UUID uuid, int i, String s)
    {
        MoneyTransferRequest moneytransferrequest = new MoneyTransferRequest();
        moneytransferrequest.AgentData_Field.AgentID = circuitInfo.agentID;
        moneytransferrequest.AgentData_Field.SessionID = circuitInfo.sessionID;
        moneytransferrequest.MoneyData_Field.SourceID = circuitInfo.agentID;
        moneytransferrequest.MoneyData_Field.DestID = uuid;
        moneytransferrequest.MoneyData_Field.Flags = 0;
        moneytransferrequest.MoneyData_Field.Amount = i;
        moneytransferrequest.MoneyData_Field.AggregatePermInventory = 0;
        moneytransferrequest.MoneyData_Field.AggregatePermNextOwner = 0;
        moneytransferrequest.MoneyData_Field.Description = SLMessage.stringToVariableOEM(s);
        moneytransferrequest.MoneyData_Field.TransactionType = 5001;
        moneytransferrequest.isReliable = true;
        SendMessage(moneytransferrequest);
    }

    public void HandleCircuitReady()
    {
        super.HandleCircuitReady();
        AskForMoneyBalance();
        RequestEconomyData();
    }

    public void HandleCloseCircuit()
    {
        if (userManager != null)
        {
            userManager.getBalanceManager().clearFinancialInfo(this);
        }
        super.HandleCloseCircuit();
    }

    public void HandleEconomyData(EconomyData economydata)
    {
        uploadCost.set(economydata.Info_Field.PriceUpload);
        Debug.Printf("Upload: upload cost %d", new Object[] {
            Integer.valueOf(uploadCost.get())
        });
    }

    public void HandleMoneyBalanceReply(MoneyBalanceReply moneybalancereply)
    {
        Object obj = null;
        SLBalanceChangedEvent slbalancechangedevent = new SLBalanceChangedEvent(balanceKnown, balance, moneybalancereply.MoneyData_Field.MoneyBalance);
        setKnownBalance(moneybalancereply.MoneyData_Field.MoneyBalance);
        if (slbalancechangedevent.oldBalanceValid && slbalancechangedevent.oldBalance != slbalancechangedevent.newBalance)
        {
            if (moneybalancereply.TransactionInfo_Field.SourceID.equals(circuitInfo.agentID))
            {
                UUID uuid;
                int i;
                if (!moneybalancereply.TransactionInfo_Field.IsDestGroup)
                {
                    uuid = moneybalancereply.TransactionInfo_Field.DestID;
                } else
                {
                    uuid = null;
                }
                i = -moneybalancereply.TransactionInfo_Field.Amount;
                moneybalancereply = uuid;
            } else
            if (moneybalancereply.TransactionInfo_Field.DestID.equals(circuitInfo.agentID))
            {
                if (!moneybalancereply.TransactionInfo_Field.IsSourceGroup)
                {
                    uuid = moneybalancereply.TransactionInfo_Field.SourceID;
                } else
                {
                    uuid = null;
                }
                i = moneybalancereply.TransactionInfo_Field.Amount;
                moneybalancereply = uuid;
            } else
            {
                i = slbalancechangedevent.newBalance - slbalancechangedevent.oldBalance;
                moneybalancereply = null;
            }
            if (moneybalancereply != null && moneybalancereply.equals(UUIDPool.ZeroUUID))
            {
                moneybalancereply = obj;
            }
            agentCircuit.GenerateChatMoneyEvent(moneybalancereply, i, slbalancechangedevent.newBalance);
        }
        eventBus.publish(slbalancechangedevent);
    }

    public void RecordChatEvent(UUID uuid, int i, int j)
    {
        if (moneyTransactionDao != null)
        {
            uuid = new MoneyTransaction(null, new Date(), uuid, i, j);
            moneyTransactionDao.insert(uuid);
            if (userManager != null)
            {
                userManager.getBalanceManager().updateMoneyTransactions();
            }
        }
    }

    public int getBalance()
    {
        Object obj = balanceLock;
        obj;
        JVM INSTR monitorenter ;
        int i = balance;
        obj;
        JVM INSTR monitorexit ;
        return i;
        Exception exception;
        exception;
        throw exception;
    }

    public boolean getBalanceKnown()
    {
        Object obj = balanceLock;
        obj;
        JVM INSTR monitorenter ;
        boolean flag = balanceKnown;
        obj;
        JVM INSTR monitorexit ;
        return flag;
        Exception exception;
        exception;
        throw exception;
    }

    public int getUploadCost()
    {
        return uploadCost.get();
    }

    public void reset()
    {
        Object obj = balanceLock;
        obj;
        JVM INSTR monitorenter ;
        balanceKnown = false;
        balance = 0;
        obj;
        JVM INSTR monitorexit ;
        return;
        Exception exception;
        exception;
        throw exception;
    }
}
