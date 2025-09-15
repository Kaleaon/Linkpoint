package com.lumiyaviewer.lumiya.slproto.modules.finance;

import com.google.common.logging.nano.Vr;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.dao.MoneyTransaction;
import com.lumiyaviewer.lumiya.dao.MoneyTransactionDao;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.events.SLBalanceChangedEvent;
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler;
import com.lumiyaviewer.lumiya.slproto.messages.EconomyData;
import com.lumiyaviewer.lumiya.slproto.messages.EconomyDataRequest;
import com.lumiyaviewer.lumiya.slproto.messages.MoneyBalanceReply;
import com.lumiyaviewer.lumiya.slproto.messages.MoneyBalanceRequest;
import com.lumiyaviewer.lumiya.slproto.messages.MoneyTransferRequest;
import com.lumiyaviewer.lumiya.slproto.modules.SLModule;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class SLFinancialInfo extends SLModule {
    private static final int DEFAULT_UPLOAD_COST = 10;
    private int balance = 0;
    private boolean balanceKnown = false;
    private final Object balanceLock = new Object();
    private final MoneyTransactionDao moneyTransactionDao;
    private final AtomicInteger uploadCost = new AtomicInteger(10);
    private final UserManager userManager;

    public SLFinancialInfo(SLAgentCircuit sLAgentCircuit) {
        super(sLAgentCircuit);
        this.userManager = UserManager.getUserManager(sLAgentCircuit.getAgentUUID());
        if (this.userManager != null) {
            this.moneyTransactionDao = this.userManager.getDaoSession().getMoneyTransactionDao();
            this.userManager.getBalanceManager().setFinancialInfo(this);
            return;
        }
        this.moneyTransactionDao = null;
    }

    private void RequestEconomyData() {
        EconomyDataRequest economyDataRequest = new EconomyDataRequest();
        economyDataRequest.isReliable = true;
        SendMessage(economyDataRequest);
    }

    private void setKnownBalance(int i) {
        synchronized (this.balanceLock) {
            this.balanceKnown = true;
            this.balance = i;
        }
        if (this.userManager != null) {
            this.userManager.getBalanceManager().updateBalance(i);
        }
    }

    public void AskForMoneyBalance() {
        MoneyBalanceRequest moneyBalanceRequest = new MoneyBalanceRequest();
        moneyBalanceRequest.AgentData_Field.AgentID = this.circuitInfo.agentID;
        moneyBalanceRequest.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        moneyBalanceRequest.MoneyData_Field.TransactionID = new UUID(0, 0);
        moneyBalanceRequest.isReliable = true;
        SendMessage(moneyBalanceRequest);
    }

    public void DoPayObject(UUID uuid, int i) {
        MoneyTransferRequest moneyTransferRequest = new MoneyTransferRequest();
        moneyTransferRequest.AgentData_Field.AgentID = this.circuitInfo.agentID;
        moneyTransferRequest.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        moneyTransferRequest.MoneyData_Field.SourceID = this.circuitInfo.agentID;
        moneyTransferRequest.MoneyData_Field.DestID = uuid;
        moneyTransferRequest.MoneyData_Field.Flags = 0;
        moneyTransferRequest.MoneyData_Field.Amount = i;
        moneyTransferRequest.MoneyData_Field.AggregatePermInventory = 0;
        moneyTransferRequest.MoneyData_Field.AggregatePermNextOwner = 0;
        moneyTransferRequest.MoneyData_Field.Description = SLMessage.stringToVariableOEM("");
        moneyTransferRequest.MoneyData_Field.TransactionType = 5008;
        moneyTransferRequest.isReliable = true;
        SendMessage(moneyTransferRequest);
    }

    public void DoPayUser(UUID uuid, int i, String str) {
        MoneyTransferRequest moneyTransferRequest = new MoneyTransferRequest();
        moneyTransferRequest.AgentData_Field.AgentID = this.circuitInfo.agentID;
        moneyTransferRequest.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        moneyTransferRequest.MoneyData_Field.SourceID = this.circuitInfo.agentID;
        moneyTransferRequest.MoneyData_Field.DestID = uuid;
        moneyTransferRequest.MoneyData_Field.Flags = 0;
        moneyTransferRequest.MoneyData_Field.Amount = i;
        moneyTransferRequest.MoneyData_Field.AggregatePermInventory = 0;
        moneyTransferRequest.MoneyData_Field.AggregatePermNextOwner = 0;
        moneyTransferRequest.MoneyData_Field.Description = SLMessage.stringToVariableOEM(str);
        moneyTransferRequest.MoneyData_Field.TransactionType = Vr.VREvent.EventType.LULLABY_UNMUTE;
        moneyTransferRequest.isReliable = true;
        SendMessage(moneyTransferRequest);
    }

    public void HandleCircuitReady() {
        super.HandleCircuitReady();
        AskForMoneyBalance();
        RequestEconomyData();
    }

    public void HandleCloseCircuit() {
        if (this.userManager != null) {
            this.userManager.getBalanceManager().clearFinancialInfo(this);
        }
        super.HandleCloseCircuit();
    }

    @SLMessageHandler
    public void HandleEconomyData(EconomyData economyData) {
        this.uploadCost.set(economyData.Info_Field.PriceUpload);
        Debug.Printf("Upload: upload cost %d", Integer.valueOf(this.uploadCost.get()));
    }

    @SLMessageHandler
    public void HandleMoneyBalanceReply(MoneyBalanceReply moneyBalanceReply) {
        UUID uuid;
        UUID uuid2 = null;
        SLBalanceChangedEvent sLBalanceChangedEvent = new SLBalanceChangedEvent(this.balanceKnown, this.balance, moneyBalanceReply.MoneyData_Field.MoneyBalance);
        setKnownBalance(moneyBalanceReply.MoneyData_Field.MoneyBalance);
        if (sLBalanceChangedEvent.oldBalanceValid && sLBalanceChangedEvent.oldBalance != sLBalanceChangedEvent.newBalance) {
            if (moneyBalanceReply.TransactionInfo_Field.SourceID.equals(this.circuitInfo.agentID)) {
                uuid = !moneyBalanceReply.TransactionInfo_Field.IsDestGroup ? moneyBalanceReply.TransactionInfo_Field.DestID : null;
                i = -moneyBalanceReply.TransactionInfo_Field.Amount;
            } else if (moneyBalanceReply.TransactionInfo_Field.DestID.equals(this.circuitInfo.agentID)) {
                uuid = !moneyBalanceReply.TransactionInfo_Field.IsSourceGroup ? moneyBalanceReply.TransactionInfo_Field.SourceID : null;
                i = moneyBalanceReply.TransactionInfo_Field.Amount;
            } else {
                i = sLBalanceChangedEvent.newBalance - sLBalanceChangedEvent.oldBalance;
                uuid = null;
            }
            if (uuid == null || !uuid.equals(UUIDPool.ZeroUUID)) {
                uuid2 = uuid;
            }
            this.agentCircuit.GenerateChatMoneyEvent(uuid2, i, sLBalanceChangedEvent.newBalance);
        }
        this.eventBus.publish(sLBalanceChangedEvent);
    }

    public void RecordChatEvent(UUID uuid, int i, int i2) {
        if (this.moneyTransactionDao != null) {
            this.moneyTransactionDao.insert(new MoneyTransaction((Long) null, new Date(), uuid, i, i2));
            if (this.userManager != null) {
                this.userManager.getBalanceManager().updateMoneyTransactions();
            }
        }
    }

    public int getBalance() {
        synchronized (this.balanceLock) {
            i = this.balance;
        }
        return i;
    }

    public boolean getBalanceKnown() {
        synchronized (this.balanceLock) {
            z = this.balanceKnown;
        }
        return z;
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
