package com.linkpoint.economy

import android.util.Log
import com.linkpoint.protocol.capabilities.CapabilityManager
import com.linkpoint.protocol.capabilities.EventHandler
import com.linkpoint.protocol.llsd.LLSDMap
import com.linkpoint.protocol.messages.UDPConnection
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID

/**
 * Economy Manager - Handles L$ balance and transactions.
 * 
 * Based on Lumiya's SLFinancialInfo.java
 * 
 * Manages:
 * - L$ balance tracking
 * - Money transactions (payments, purchases)
 * - Balance change notifications
 * - Economy data (upload prices, etc.)
 */
class EconomyManager(
    private val udpConnection: UDPConnection,
    private val capabilityManager: CapabilityManager,
    private val agentId: UUID
) : EventHandler {
    
    companion object {
        private const val TAG = "EconomyManager"
        
        // Message IDs
        const val MSG_MONEY_BALANCE_REQUEST = 0xFF00CE
        const val MSG_MONEY_BALANCE_REPLY = 0xFF00CF
        const val MSG_MONEY_TRANSFER_REQUEST = 0xFF00D0
        const val MSG_ECONOMY_DATA_REQUEST = 0xFF00D1
        const val MSG_ECONOMY_DATA = 0xFF00D2
        
        // Transaction types
        const val TRANS_OBJECT_SALE = 5000
        const val TRANS_GIFT = 5001
        const val TRANS_LAND_SALE = 5002
        const val TRANS_REFER_BONUS = 5003
        const val TRANS_INVENTORY_SALE = 5004
        const val TRANS_REFUND_PURCHASE = 5005
        const val TRANS_LAND_PASS_SALE = 5006
        const val TRANS_DWELL_BONUS = 5007
        const val TRANS_PAY_OBJECT = 5008
        const val TRANS_OBJECT_PAYS = 5009
        const val TRANS_GROUP_LAND_DEED = 5010
        const val TRANS_GROUP_OBJECT_DEED = 5011
        const val TRANS_GROUP_LIABILITY = 5012
        const val TRANS_GROUP_DIVIDEND = 5013
        const val TRANS_GROUP_MEMBERSHIP_DUES = 5014
        
        // Money flags
        const val MONEY_FLAG_DESTINATION_AGGREGATES = 0x01
        const val MONEY_FLAG_SOURCE_AGGREGATES = 0x02
    }
    
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    
    // L$ balance
    private val _balance = MutableStateFlow(0)
    val balance: StateFlow<Int> = _balance
    
    // Economy data (upload prices, etc.)
    private val _economyData = MutableStateFlow<EconomyData?>(null)
    val economyData: StateFlow<EconomyData?> = _economyData
    
    // Transaction events
    private val _transactionEvents = MutableSharedFlow<TransactionEvent>(replay = 0, extraBufferCapacity = 16)
    val transactionEvents: SharedFlow<TransactionEvent> = _transactionEvents
    
    init {
        capabilityManager.registerEventHandler("MoneyBalanceReply", this)
    }
    
    override fun onEvent(message: String, body: LLSDMap) {
        when (message) {
            "MoneyBalanceReply" -> {
                val newBalance = body.getInt("MoneyBalance") ?: return
                val previousBalance = _balance.value
                _balance.value = newBalance
                
                scope.launch {
                    _transactionEvents.emit(
                        TransactionEvent.BalanceChanged(
                            previousBalance = previousBalance,
                            newBalance = newBalance,
                            change = newBalance - previousBalance
                        )
                    )
                }
            }
        }
    }
    
    /**
     * Handle MoneyBalanceReply message from UDP.
     */
    fun handleMoneyBalanceReply(payload: ByteArray) {
        try {
            val buffer = ByteBuffer.wrap(payload).order(ByteOrder.LITTLE_ENDIAN)
            
            // MoneyData block
            val requesterAgentId = readUUID(buffer)
            val transactionId = readUUID(buffer)
            val transactionSuccess = buffer.get() != 0.toByte()
            val newBalance = buffer.int
            val squareMetersCredit = buffer.int
            val squareMetersCommitted = buffer.int
            
            // Read description
            val descLen = buffer.get().toInt() and 0xFF
            val descBytes = ByteArray(descLen)
            buffer.get(descBytes)
            val description = String(descBytes, Charsets.UTF_8).trim('\u0000')
            
            val previousBalance = _balance.value
            _balance.value = newBalance
            
            Log.d(TAG, "Balance updated: $previousBalance -> $newBalance L$ ($description)")
            
            scope.launch {
                _transactionEvents.emit(
                    TransactionEvent.BalanceChanged(
                        previousBalance = previousBalance,
                        newBalance = newBalance,
                        change = newBalance - previousBalance,
                        description = description.ifEmpty { null },
                        transactionSuccess = transactionSuccess
                    )
                )
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing MoneyBalanceReply", e)
        }
    }
    
    /**
     * Handle EconomyData message from UDP.
     */
    fun handleEconomyData(payload: ByteArray) {
        try {
            val buffer = ByteBuffer.wrap(payload).order(ByteOrder.LITTLE_ENDIAN)
            
            // Info block
            val objectCapacity = buffer.int
            val objectCount = buffer.int
            val priceEnergyUnit = buffer.int
            val priceObjectClaim = buffer.int
            val pricePublicObjectDecay = buffer.int
            val pricePublicObjectDelete = buffer.int
            val priceParcelClaim = buffer.int
            val priceParcelClaimFactor = buffer.float
            val priceUpload = buffer.int
            val priceRentLight = buffer.int
            val teleportMinPrice = buffer.int
            val teleportPriceExponent = buffer.float
            val energyEfficiency = buffer.float
            val priceObjectRent = buffer.float
            val priceObjectScaleFactor = buffer.float
            val priceParcelRent = buffer.int
            val priceGroupCreate = buffer.int
            
            _economyData.value = EconomyData(
                priceUpload = priceUpload,
                priceGroupCreate = priceGroupCreate,
                teleportMinPrice = teleportMinPrice,
                teleportPriceExponent = teleportPriceExponent,
                priceParcelClaim = priceParcelClaim,
                priceParcelClaimFactor = priceParcelClaimFactor,
                priceParcelRent = priceParcelRent,
                objectCapacity = objectCapacity,
                objectCount = objectCount
            )
            
            Log.d(TAG, "Economy data updated: upload=$priceUpload, groupCreate=$priceGroupCreate")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing EconomyData", e)
        }
    }
    
    /**
     * Request current L$ balance.
     */
    suspend fun requestBalance() {
        try {
            val payload = ByteBuffer.allocate(48).order(ByteOrder.LITTLE_ENDIAN)
            
            // AgentData
            writeUUID(payload, agentId)
            writeUUID(payload, udpConnection.getSessionId())
            
            // MoneyData
            writeUUID(payload, UUID.randomUUID()) // TransactionID
            
            udpConnection.sendPacket(MSG_MONEY_BALANCE_REQUEST, payload.array(), reliable = true)
            Log.d(TAG, "Requested balance")
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to request balance", e)
        }
    }
    
    /**
     * Request economy data (prices, etc.).
     */
    suspend fun requestEconomyData() {
        try {
            val payload = ByteBuffer.allocate(32).order(ByteOrder.LITTLE_ENDIAN)
            
            // AgentData
            writeUUID(payload, agentId)
            writeUUID(payload, udpConnection.getSessionId())
            
            udpConnection.sendPacket(MSG_ECONOMY_DATA_REQUEST, payload.array(), reliable = true)
            Log.d(TAG, "Requested economy data")
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to request economy data", e)
        }
    }
    
    /**
     * Pay L$ to an agent.
     */
    suspend fun payAgent(
        destinationId: UUID,
        amount: Int,
        description: String = ""
    ): Boolean {
        if (amount <= 0) return false
        if (amount > _balance.value) {
            Log.w(TAG, "Insufficient balance: ${_balance.value} < $amount")
            return false
        }
        
        return sendPayment(destinationId, amount, description, TRANS_GIFT)
    }
    
    /**
     * Pay L$ to an object.
     */
    suspend fun payObject(
        objectId: UUID,
        amount: Int,
        description: String = ""
    ): Boolean {
        if (amount <= 0) return false
        if (amount > _balance.value) {
            Log.w(TAG, "Insufficient balance: ${_balance.value} < $amount")
            return false
        }
        
        return sendPayment(objectId, amount, description, TRANS_PAY_OBJECT)
    }
    
    /**
     * Send a payment.
     */
    private suspend fun sendPayment(
        destinationId: UUID,
        amount: Int,
        description: String,
        transactionType: Int
    ): Boolean {
        try {
            val descBytes = description.toByteArray(Charsets.UTF_8)
            val payload = ByteBuffer.allocate(68 + descBytes.size).order(ByteOrder.LITTLE_ENDIAN)
            
            // AgentData
            writeUUID(payload, agentId)
            writeUUID(payload, udpConnection.getSessionId())
            
            // MoneyData
            writeUUID(payload, destinationId)
            payload.put(0) // DestinationGroup (false)
            payload.putInt(amount)
            payload.putInt(0) // AggregatePermNextOwner
            payload.putInt(0) // AggregatePermInventory
            payload.putInt(transactionType)
            payload.put(descBytes.size.toByte())
            payload.put(descBytes)
            
            udpConnection.sendPacket(MSG_MONEY_TRANSFER_REQUEST, payload.array().copyOf(payload.position()), reliable = true)
            
            Log.i(TAG, "Sent payment: $amount L$ to $destinationId")
            
            scope.launch {
                _transactionEvents.emit(
                    TransactionEvent.PaymentSent(
                        destinationId = destinationId,
                        amount = amount,
                        description = description
                    )
                )
            }
            
            return true
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send payment", e)
            return false
        }
    }
    
    private fun readUUID(buffer: ByteBuffer): UUID {
        val msb = buffer.long
        val lsb = buffer.long
        return UUID(msb, lsb)
    }
    
    private fun writeUUID(buffer: ByteBuffer, uuid: UUID) {
        buffer.putLong(uuid.mostSignificantBits)
        buffer.putLong(uuid.leastSignificantBits)
    }
    
    /**
     * Shutdown the manager.
     */
    fun shutdown() {
        scope.cancel()
    }
}

/**
 * Economy data (prices, etc.).
 */
data class EconomyData(
    val priceUpload: Int,
    val priceGroupCreate: Int,
    val teleportMinPrice: Int,
    val teleportPriceExponent: Float,
    val priceParcelClaim: Int,
    val priceParcelClaimFactor: Float,
    val priceParcelRent: Int,
    val objectCapacity: Int,
    val objectCount: Int
)

/**
 * Transaction events.
 */
sealed class TransactionEvent {
    data class BalanceChanged(
        val previousBalance: Int,
        val newBalance: Int,
        val change: Int,
        val description: String? = null,
        val transactionSuccess: Boolean = true
    ) : TransactionEvent()
    
    data class PaymentSent(
        val destinationId: UUID,
        val amount: Int,
        val description: String
    ) : TransactionEvent()
    
    data class PaymentReceived(
        val sourceId: UUID,
        val sourceName: String,
        val amount: Int,
        val description: String
    ) : TransactionEvent()
}
