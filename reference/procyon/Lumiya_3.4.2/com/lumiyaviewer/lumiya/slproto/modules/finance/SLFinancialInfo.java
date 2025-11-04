// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.modules.finance;

import de.greenrobot.dao.AbstractDao;
import com.lumiyaviewer.lumiya.dao.MoneyTransaction;
import java.util.Date;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import com.lumiyaviewer.lumiya.slproto.events.SLBalanceChangedEvent;
import com.lumiyaviewer.lumiya.slproto.messages.MoneyBalanceReply;
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.messages.EconomyData;
import com.lumiyaviewer.lumiya.slproto.messages.MoneyTransferRequest;
import java.util.UUID;
import com.lumiyaviewer.lumiya.slproto.messages.MoneyBalanceRequest;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.messages.EconomyDataRequest;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import java.util.concurrent.atomic.AtomicInteger;
import com.lumiyaviewer.lumiya.dao.MoneyTransactionDao;
import com.lumiyaviewer.lumiya.slproto.modules.SLModule;

public class SLFinancialInfo extends SLModule
{
    private static final int DEFAULT_UPLOAD_COST = 10;
    private int balance;
    private boolean balanceKnown;
    private final Object balanceLock;
    private final MoneyTransactionDao moneyTransactionDao;
    private final AtomicInteger uploadCost;
    private final UserManager userManager;
    
    public SLFinancialInfo(final SLAgentCircuit slAgentCircuit) {
        super(slAgentCircuit);
        this.balanceLock = new Object();
        this.balanceKnown = false;
        this.balance = 0;
        this.uploadCost = new AtomicInteger(10);
        this.userManager = UserManager.getUserManager(slAgentCircuit.getAgentUUID());
        if (this.userManager != null) {
            this.moneyTransactionDao = this.userManager.getDaoSession().getMoneyTransactionDao();
            this.userManager.getBalanceManager().setFinancialInfo(this);
        }
        else {
            this.moneyTransactionDao = null;
        }
    }
    
    private void RequestEconomyData() {
        final EconomyDataRequest economyDataRequest = new EconomyDataRequest();
        economyDataRequest.isReliable = true;
        this.SendMessage(economyDataRequest);
    }
    
    private void setKnownBalance(final int balance) {
        synchronized (this.balanceLock) {
            this.balanceKnown = true;
            this.balance = balance;
            monitorexit(this.balanceLock);
            if (this.userManager != null) {
                this.userManager.getBalanceManager().updateBalance(balance);
            }
        }
    }
    
    public void AskForMoneyBalance() {
        final MoneyBalanceRequest moneyBalanceRequest = new MoneyBalanceRequest();
        moneyBalanceRequest.AgentData_Field.AgentID = this.circuitInfo.agentID;
        moneyBalanceRequest.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        moneyBalanceRequest.MoneyData_Field.TransactionID = new UUID(0L, 0L);
        moneyBalanceRequest.isReliable = true;
        this.SendMessage(moneyBalanceRequest);
    }
    
    public void DoPayObject(final UUID destID, final int amount) {
        final MoneyTransferRequest moneyTransferRequest = new MoneyTransferRequest();
        moneyTransferRequest.AgentData_Field.AgentID = this.circuitInfo.agentID;
        moneyTransferRequest.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        moneyTransferRequest.MoneyData_Field.SourceID = this.circuitInfo.agentID;
        moneyTransferRequest.MoneyData_Field.DestID = destID;
        moneyTransferRequest.MoneyData_Field.Flags = 0;
        moneyTransferRequest.MoneyData_Field.Amount = amount;
        moneyTransferRequest.MoneyData_Field.AggregatePermInventory = 0;
        moneyTransferRequest.MoneyData_Field.AggregatePermNextOwner = 0;
        moneyTransferRequest.MoneyData_Field.Description = SLMessage.stringToVariableOEM("");
        moneyTransferRequest.MoneyData_Field.TransactionType = 5008;
        moneyTransferRequest.isReliable = true;
        this.SendMessage(moneyTransferRequest);
    }
    
    public void DoPayUser(final UUID destID, final int amount, final String s) {
        final MoneyTransferRequest moneyTransferRequest = new MoneyTransferRequest();
        moneyTransferRequest.AgentData_Field.AgentID = this.circuitInfo.agentID;
        moneyTransferRequest.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        moneyTransferRequest.MoneyData_Field.SourceID = this.circuitInfo.agentID;
        moneyTransferRequest.MoneyData_Field.DestID = destID;
        moneyTransferRequest.MoneyData_Field.Flags = 0;
        moneyTransferRequest.MoneyData_Field.Amount = amount;
        moneyTransferRequest.MoneyData_Field.AggregatePermInventory = 0;
        moneyTransferRequest.MoneyData_Field.AggregatePermNextOwner = 0;
        moneyTransferRequest.MoneyData_Field.Description = SLMessage.stringToVariableOEM(s);
        moneyTransferRequest.MoneyData_Field.TransactionType = 5001;
        moneyTransferRequest.isReliable = true;
        this.SendMessage(moneyTransferRequest);
    }
    
    @Override
    public void HandleCircuitReady() {
        super.HandleCircuitReady();
        this.AskForMoneyBalance();
        this.RequestEconomyData();
    }
    
    @Override
    public void HandleCloseCircuit() {
        if (this.userManager != null) {
            this.userManager.getBalanceManager().clearFinancialInfo(this);
        }
        super.HandleCloseCircuit();
    }
    
    @SLMessageHandler
    public void HandleEconomyData(final EconomyData economyData) {
        this.uploadCost.set(economyData.Info_Field.PriceUpload);
        Debug.Printf("Upload: upload cost %d", this.uploadCost.get());
    }
    
    @SLMessageHandler
    public void HandleMoneyBalanceReply(final MoneyBalanceReply moneyBalanceReply) {
        final UUID uuid = null;
        final SLBalanceChangedEvent slBalanceChangedEvent = new SLBalanceChangedEvent(this.balanceKnown, this.balance, moneyBalanceReply.MoneyData_Field.MoneyBalance);
        this.setKnownBalance(moneyBalanceReply.MoneyData_Field.MoneyBalance);
        if (slBalanceChangedEvent.oldBalanceValid && slBalanceChangedEvent.oldBalance != slBalanceChangedEvent.newBalance) {
            int amount;
            UUID uuid2;
            if (moneyBalanceReply.TransactionInfo_Field.SourceID.equals(this.circuitInfo.agentID)) {
                UUID destID;
                if (!moneyBalanceReply.TransactionInfo_Field.IsDestGroup) {
                    destID = moneyBalanceReply.TransactionInfo_Field.DestID;
                }
                else {
                    destID = null;
                }
                amount = -moneyBalanceReply.TransactionInfo_Field.Amount;
                uuid2 = destID;
            }
            else if (moneyBalanceReply.TransactionInfo_Field.DestID.equals(this.circuitInfo.agentID)) {
                UUID sourceID;
                if (!moneyBalanceReply.TransactionInfo_Field.IsSourceGroup) {
                    sourceID = moneyBalanceReply.TransactionInfo_Field.SourceID;
                }
                else {
                    sourceID = null;
                }
                amount = moneyBalanceReply.TransactionInfo_Field.Amount;
                uuid2 = sourceID;
            }
            else {
                amount = slBalanceChangedEvent.newBalance - slBalanceChangedEvent.oldBalance;
                uuid2 = null;
            }
            if (uuid2 != null && uuid2.equals(UUIDPool.ZeroUUID)) {
                uuid2 = uuid;
            }
            this.agentCircuit.GenerateChatMoneyEvent(uuid2, amount, slBalanceChangedEvent.newBalance);
        }
        this.eventBus.publish(slBalanceChangedEvent);
    }
    
    public void RecordChatEvent(final UUID uuid, final int n, final int n2) {
        if (this.moneyTransactionDao != null) {
            ((AbstractDao<MoneyTransaction, K>)this.moneyTransactionDao).insert(new MoneyTransaction(null, new Date(), uuid, n, n2));
            if (this.userManager != null) {
                this.userManager.getBalanceManager().updateMoneyTransactions();
            }
        }
    }
    
    public int getBalance() {
        synchronized (this.balanceLock) {
            return this.balance;
        }
    }
    
    public boolean getBalanceKnown() {
        synchronized (this.balanceLock) {
            return this.balanceKnown;
        }
    }
    
    public int getUploadCost() {
        return this.uploadCost.get();
    }
    
    public void reset() {
        synchronized (this.balanceLock) {
            this.balanceKnown = false;
            this.balance = 0;
        }
    }
}
