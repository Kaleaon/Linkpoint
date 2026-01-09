package com.linkpoint.slproto.modules.finance

import com.google.common.logging.nano.Vr
import com.linkpoint.Debug
import com.linkpoint.dao.MoneyTransaction
import com.linkpoint.dao.MoneyTransactionDao
import com.linkpoint.slproto.SLAgentCircuit
import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.events.SLBalanceChangedEvent
import com.linkpoint.slproto.handler.SLMessageHandler
import com.linkpoint.slproto.messages.EconomyData
import com.linkpoint.slproto.messages.EconomyDataRequest
import com.linkpoint.slproto.messages.MoneyBalanceReply
import com.linkpoint.slproto.messages.MoneyBalanceRequest
import com.linkpoint.slproto.messages.MoneyTransferRequest
import com.linkpoint.slproto.modules.SLModule
import com.linkpoint.slproto.users.manager.UserManager
import com.linkpoint.utils.UUIDPool
import java.util.Date
import java.util.UUID
import java.util.concurrent.atomic.AtomicInteger

class SLFinancialInfo : SLModule {
    private val DEFAULT_UPLOAD_COST: Int = 10
    private Int balance = 0
    private Boolean balanceKnown = false
    private Any balanceLock = Any()
    private MoneyTransactionDao moneyTransactionDao
    private AtomicInteger uploadCost = AtomicInteger(10)
    private UserManager userManager

    SLFinancialInfo(SLAgentCircuit sLAgentCircuit) {
        super(sLAgentCircuit)
        this.userManager = UserManager.getUserManager(sLAgentCircuit.getAgentUUID())
        if (this.userManager != null) {
            this.moneyTransactionDao = this.userManager.getDaoSession().getMoneyTransactionDao()
            this.userManager.getBalanceManager().setFinancialInfo(this)
            return
        }
        this.moneyTransactionDao = null
    }

    private Unit RequestEconomyData() {
        EconomyDataRequest economyDataRequest = EconomyDataRequest()
        economyDataRequest.isReliable = true
        SendMessage(economyDataRequest)
    }

    private Unit setKnownBalance(Int i) {
        synchronized (this.balanceLock) {
            this.balanceKnown = true
            this.balance = i
        }
        if (this.userManager != null) {
            this.userManager.getBalanceManager().updateBalance(i)
        }
    }

    fun AskForMoneyBalance()  {
        MoneyBalanceRequest moneyBalanceRequest = MoneyBalanceRequest()
        moneyBalanceRequest.AgentData_Field.AgentID = this.circuitInfo.agentID
        moneyBalanceRequest.AgentData_Field.SessionID = this.circuitInfo.sessionID
        moneyBalanceRequest.MoneyData_Field.TransactionID = UUID(0, 0)
        moneyBalanceRequest.isReliable = true
        SendMessage(moneyBalanceRequest)
    }

    fun DoPayObject(UUID uuid, Int i)  {
        MoneyTransferRequest moneyTransferRequest = MoneyTransferRequest()
        moneyTransferRequest.AgentData_Field.AgentID = this.circuitInfo.agentID
        moneyTransferRequest.AgentData_Field.SessionID = this.circuitInfo.sessionID
        moneyTransferRequest.MoneyData_Field.SourceID = this.circuitInfo.agentID
        moneyTransferRequest.MoneyData_Field.DestID = uuid
        moneyTransferRequest.MoneyData_Field.Flags = 0
        moneyTransferRequest.MoneyData_Field.Amount = i
        moneyTransferRequest.MoneyData_Field.AggregatePermInventory = 0
        moneyTransferRequest.MoneyData_Field.AggregatePermNextOwner = 0
        moneyTransferRequest.MoneyData_Field.Description = SLMessage.stringToVariableOEM("")
        moneyTransferRequest.MoneyData_Field.TransactionType = 5008
        moneyTransferRequest.isReliable = true
        SendMessage(moneyTransferRequest)
    }

    fun DoPayUser(UUID uuid, Int i, String str)  {
        MoneyTransferRequest moneyTransferRequest = MoneyTransferRequest()
        moneyTransferRequest.AgentData_Field.AgentID = this.circuitInfo.agentID
        moneyTransferRequest.AgentData_Field.SessionID = this.circuitInfo.sessionID
        moneyTransferRequest.MoneyData_Field.SourceID = this.circuitInfo.agentID
        moneyTransferRequest.MoneyData_Field.DestID = uuid
        moneyTransferRequest.MoneyData_Field.Flags = 0
        moneyTransferRequest.MoneyData_Field.Amount = i
        moneyTransferRequest.MoneyData_Field.AggregatePermInventory = 0
        moneyTransferRequest.MoneyData_Field.AggregatePermNextOwner = 0
        moneyTransferRequest.MoneyData_Field.Description = SLMessage.stringToVariableOEM(str)
        moneyTransferRequest.MoneyData_Field.TransactionType = Vr.VREvent.EventType.LULLABY_UNMUTE
        moneyTransferRequest.isReliable = true
        SendMessage(moneyTransferRequest)
    }

    fun HandleCircuitReady()  {
        super.HandleCircuitReady()
        AskForMoneyBalance()
        RequestEconomyData()
    }

    fun HandleCloseCircuit()  {
        if (this.userManager != null) {
            this.userManager.getBalanceManager().clearFinancialInfo(this)
        }
        super.HandleCloseCircuit()
    }

    @SLMessageHandler
    fun HandleEconomyData(EconomyData economyData)  {
        this.uploadCost.set(economyData.Info_Field.PriceUpload)
        Debug.Printf("Upload: upload cost %d", Int.valueOf(this.uploadCost.get()))
    }

    @SLMessageHandler
    fun HandleMoneyBalanceReply(MoneyBalanceReply moneyBalanceReply)  {
        UUID uuid
        UUID uuid2 = null
        SLBalanceChangedEvent sLBalanceChangedEvent = SLBalanceChangedEvent(this.balanceKnown, this.balance, moneyBalanceReply.MoneyData_Field.MoneyBalance)
        setKnownBalance(moneyBalanceReply.MoneyData_Field.MoneyBalance)
        if (sLBalanceChangedEvent.oldBalanceValid && sLBalanceChangedEvent.oldBalance != sLBalanceChangedEvent.newBalance) {
            if (moneyBalanceReply.TransactionInfo_Field.SourceID.equals(this.circuitInfo.agentID)) {
                uuid = !moneyBalanceReply.TransactionInfo_Field.IsDestGroup ? moneyBalanceReply.TransactionInfo_Field.DestID : null
                i = -moneyBalanceReply.TransactionInfo_Field.Amount
            } else if (moneyBalanceReply.TransactionInfo_Field.DestID.equals(this.circuitInfo.agentID)) {
                uuid = !moneyBalanceReply.TransactionInfo_Field.IsSourceGroup ? moneyBalanceReply.TransactionInfo_Field.SourceID : null
                i = moneyBalanceReply.TransactionInfo_Field.Amount
            } else {
                i = sLBalanceChangedEvent.newBalance - sLBalanceChangedEvent.oldBalance
                uuid = null
            }
            if (uuid == null || !uuid.equals(UUIDPool.ZeroUUID)) {
                uuid2 = uuid
            }
            this.agentCircuit.GenerateChatMoneyEvent(uuid2, i, sLBalanceChangedEvent.newBalance)
        }
        this.eventBus.publish(sLBalanceChangedEvent)
    }

    fun RecordChatEvent(UUID uuid, Int i, Int i2)  {
        if (this.moneyTransactionDao != null) {
            this.moneyTransactionDao.insert(MoneyTransaction((Long) null, Date(), uuid, i, i2))
            if (this.userManager != null) {
                this.userManager.getBalanceManager().updateMoneyTransactions()
            }
        }
    }

    fun getBalance(): Int {
        synchronized (this.balanceLock) {
            i = this.balance
        }
        return i
    }

    fun getBalanceKnown(): Boolean {
        synchronized (this.balanceLock) {
            z = this.balanceKnown
        }
        return z
    }

    fun getUploadCost(): Int {
        return this.uploadCost.get()
    }

    fun reset()  {
        synchronized (this.balanceLock) {
            this.balanceKnown = false
            this.balance = 0
        }
    }
}
