package com.linkpoint.economy

import android.util.Log
import com.linkpoint.network.grid.GridInfoResolver
import com.linkpoint.protocol.capabilities.CapabilityManager
import com.linkpoint.protocol.capabilities.EventHandler
import com.linkpoint.protocol.capabilities.EventQueueDispatcher
import com.linkpoint.protocol.llsd.LLSDMap
import com.linkpoint.protocol.messages.ids.MessageIdRegistry
import com.linkpoint.protocol.messages.UDPConnectionFixed
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID
import java.util.concurrent.TimeUnit

/**
 * Economy Manager - Handles balance and financial transactions over HTTP REST and UDP.
 */
class EconomyManager(
    private val udpConnection: UDPConnectionFixed,
    private val capabilityManager: CapabilityManager,
    private val agentId: UUID
) : EventHandler {
    
    companion object {
        private const val TAG = "EconomyManager"

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

        fun transactionTypeName(type: Int): String = when (type) {
            TRANS_OBJECT_SALE -> "Object Sale"
            TRANS_GIFT -> "Gift / Stipend"
            TRANS_LAND_SALE -> "Land Sale"
            TRANS_REFER_BONUS -> "Referral Bonus"
            TRANS_INVENTORY_SALE -> "Inventory Sale"
            TRANS_REFUND_PURCHASE -> "Refund"
            TRANS_LAND_PASS_SALE -> "Land Pass Sale"
            TRANS_DWELL_BONUS -> "Dwell Bonus"
            TRANS_PAY_OBJECT -> "Paid Object"
            TRANS_OBJECT_PAYS -> "Object Paid You"
            TRANS_GROUP_LAND_DEED -> "Group Land Deed"
            TRANS_GROUP_OBJECT_DEED -> "Group Object Deed"
            TRANS_GROUP_LIABILITY -> "Group Liability"
            TRANS_GROUP_DIVIDEND -> "Group Dividend"
            TRANS_GROUP_MEMBERSHIP_DUES -> "Group Dues"
            else -> "Transaction (#$type)"
        }
    }
    
    private val scope = CoroutineScope(EventQueueDispatcher.dispatcher + SupervisorJob())
    
    // Balance state
    private val _balance = MutableStateFlow(0)
    val balance: StateFlow<Int> = _balance

    // Dynamic currency metadata state
    private val _currencySymbol = MutableStateFlow("L$")
    val currencySymbol: StateFlow<String> = _currencySymbol

    private val _isZeroCurrency = MutableStateFlow(false)
    val isZeroCurrency: StateFlow<Boolean> = _isZeroCurrency
    
    // Economy data (upload prices, etc.)
    private val _economyData = MutableStateFlow<EconomyData?>(null)
    val economyData: StateFlow<EconomyData?> = _economyData

    /**
     * Explicitly configure grid currency symbol and zero-currency flag.
     */
    fun setGridCurrency(symbol: String = "L$", zeroCurrency: Boolean = false) {
        _currencySymbol.value = symbol.ifEmpty { "L$" }
        _isZeroCurrency.value = zeroCurrency
    }

    /**
     * Get dynamically resolved economy URI from session state / GridInfo
     */
    fun getResolvedEconomyUri(): String? {
        return try {
            val app = com.linkpoint.LinkpointApp.getInstance()
            app.sessionManager.getEconomyUri() ?: app.gridManager.getSelectedGrid().economyUri
        } catch (e: Exception) {
            null
        }
    }
    
    // Transaction events
    private val _transactionEvents = MutableSharedFlow<TransactionEvent>(replay = 0, extraBufferCapacity = 16)
    val transactionEvents: SharedFlow<TransactionEvent> = _transactionEvents
    
    init {
        capabilityManager.registerEventHandler("MoneyBalanceReply", this, EventQueueDispatcher.dispatcher)
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
            
            Log.d(TAG, "Balance updated: $previousBalance -> $newBalance ${_currencySymbol.value} ($description)")
            
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
     * Request current balance using HTTP REST with automated UDP fallback.
     */
    suspend fun requestBalance() = withContext(Dispatchers.IO) {
        if (_isZeroCurrency.value) {
            _balance.value = 0
            Log.d(TAG, "Zero-currency grid active; balance set to 0.")
            return@withContext
        }

        val economyUri = getResolvedEconomyUri()
        if (GridInfoResolver.isValidHttpUrl(economyUri)) {
            val success = requestBalanceHttp(economyUri!!)
            if (success) return@withContext
            Log.w(TAG, "HTTP balance endpoint failed/timed out; falling back to UDP request.")
        }

        requestBalanceUdp()
    }

    private fun requestBalanceUdp() {
        try {
            val payload = ByteBuffer.allocate(48).order(ByteOrder.LITTLE_ENDIAN)
            writeUUID(payload, agentId)
            writeUUID(payload, udpConnection.getSessionId())
            writeUUID(payload, UUID.randomUUID())
            
            udpConnection.sendPacket(MessageIdRegistry.MONEY_BALANCE_REQUEST, payload.array(), reliable = true)
            Log.d(TAG, "Requested balance via UDP")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to request balance via UDP", e)
        }
    }

    private suspend fun requestBalanceHttp(economyUri: String): Boolean = withContext(Dispatchers.IO) {
        return@withContext withTimeoutOrNull(3000L) {
            try {
                val cleanUri = economyUri.trimEnd('/')
                val targetUrl = "$cleanUri/balance"
                val client = OkHttpClient.Builder()
                    .connectTimeout(3000, TimeUnit.MILLISECONDS)
                    .readTimeout(3000, TimeUnit.MILLISECONDS)
                    .build()

                val request = Request.Builder()
                    .url(targetUrl)
                    .header("User-Agent", "Linkpoint/1.0 Economy")
                    .header("Accept", "application/json, text/plain")
                    .get()
                    .build()

                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        val bodyStr = response.body?.string() ?: ""
                        if (bodyStr.isNotBlank()) {
                            val parsed = parseBalanceJson(bodyStr)
                            if (parsed != null) {
                                val previousBalance = _balance.value
                                _balance.value = parsed.first
                                parsed.second?.let { _currencySymbol.value = it }
                                _transactionEvents.emit(
                                    TransactionEvent.BalanceChanged(
                                        previousBalance = previousBalance,
                                        newBalance = parsed.first,
                                        change = parsed.first - previousBalance,
                                        description = "HTTP Balance Response"
                                    )
                                )
                                return@use true
                            }
                        }
                    }
                    false
                }
            } catch (e: Exception) {
                Log.d(TAG, "HTTP balance error: ${e.message}")
                false
            }
        } ?: false
    }

    private fun parseBalanceJson(body: String): Pair<Int, String?>? {
        return try {
            val trimmed = body.trim()
            if (trimmed.startsWith("{")) {
                val json = JSONObject(trimmed)
                val bal = when {
                    json.has("balance") -> json.getInt("balance")
                    json.has("MoneyBalance") -> json.getInt("MoneyBalance")
                    else -> null
                }
                val sym = when {
                    json.has("currency") -> json.getString("currency")
                    json.has("currencySymbol") -> json.getString("currencySymbol")
                    else -> null
                }
                if (bal != null) Pair(bal, sym) else null
            } else {
                val intVal = trimmed.toIntOrNull()
                if (intVal != null) Pair(intVal, null) else null
            }
        } catch (e: Exception) {
            null
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
            
            udpConnection.sendPacket(MessageIdRegistry.ECONOMY_DATA_REQUEST, payload.array(), reliable = true)
            Log.d(TAG, "Requested economy data")
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to request economy data", e)
        }
    }
    
    /**
     * Pay currency to an agent.
     */
    suspend fun payAgent(
        destinationId: UUID,
        amount: Int,
        description: String = ""
    ): Boolean {
        if (_isZeroCurrency.value) return false
        if (amount <= 0) return false
        if (amount > _balance.value) {
            Log.w(TAG, "Insufficient balance: ${_balance.value} < $amount")
            return false
        }
        
        return sendPayment(destinationId, amount, description, TRANS_GIFT)
    }
    
    /**
     * Pay currency to an object.
     */
    suspend fun payObject(
        objectId: UUID,
        amount: Int,
        description: String = ""
    ): Boolean {
        if (_isZeroCurrency.value) return false
        if (amount <= 0) return false
        if (amount > _balance.value) {
            Log.w(TAG, "Insufficient balance: ${_balance.value} < $amount")
            return false
        }
        
        return sendPayment(objectId, amount, description, TRANS_PAY_OBJECT)
    }
    
    /**
     * Send a payment via HTTP REST with UDP fallback.
     */
    private suspend fun sendPayment(
        destinationId: UUID,
        amount: Int,
        description: String,
        transactionType: Int
    ): Boolean = withContext(Dispatchers.IO) {
        if (_isZeroCurrency.value) {
            Log.w(TAG, "Suppressed payment attempt on zero-currency grid.")
            return@withContext false
        }

        val economyUri = getResolvedEconomyUri()
        if (GridInfoResolver.isValidHttpUrl(economyUri)) {
            val success = sendPaymentHttp(economyUri!!, destinationId, amount, description, transactionType)
            if (success) return@withContext true
            Log.w(TAG, "HTTP payment endpoint failed/timed out; falling back to UDP transfer request.")
        }

        return@withContext sendPaymentUdp(destinationId, amount, description, transactionType)
    }

    private suspend fun sendPaymentHttp(
        economyUri: String,
        destinationId: UUID,
        amount: Int,
        description: String,
        transactionType: Int
    ): Boolean = withContext(Dispatchers.IO) {
        return@withContext withTimeoutOrNull(3000L) {
            try {
                val cleanUri = economyUri.trimEnd('/')
                val targetUrl = "$cleanUri/transfer"
                val jsonPayload = JSONObject().apply {
                    put("agentId", agentId.toString())
                    put("destinationId", destinationId.toString())
                    put("amount", amount)
                    put("description", description)
                    put("transactionType", transactionType)
                }.toString()

                val requestBody = jsonPayload.toRequestBody("application/json; charset=utf-8".toMediaType())
                val client = OkHttpClient.Builder()
                    .connectTimeout(3000, TimeUnit.MILLISECONDS)
                    .readTimeout(3000, TimeUnit.MILLISECONDS)
                    .build()

                val request = Request.Builder()
                    .url(targetUrl)
                    .header("User-Agent", "Linkpoint/1.0 Economy")
                    .post(requestBody)
                    .build()

                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        val bodyStr = response.body?.string() ?: ""
                        val isOk = !bodyStr.contains("\"success\":false") && !bodyStr.contains("\"error\"")
                        if (isOk) {
                            val parsed = parseBalanceJson(bodyStr)
                            if (parsed != null) {
                                _balance.value = parsed.first
                            }
                            _transactionEvents.emit(
                                TransactionEvent.PaymentSent(
                                    destinationId = destinationId,
                                    amount = amount,
                                    description = description
                                )
                            )
                            return@use true
                        }
                    }
                    false
                }
            } catch (e: Exception) {
                Log.d(TAG, "HTTP payment error: ${e.message}")
                false
            }
        } ?: false
    }

    private suspend fun sendPaymentUdp(
        destinationId: UUID,
        amount: Int,
        description: String,
        transactionType: Int
    ): Boolean {
        try {
            val rawDesc = description.toByteArray(Charsets.UTF_8)
            val cappedDesc = if (rawDesc.size > 254) rawDesc.copyOf(254) else rawDesc
            val descBytes = cappedDesc + 0.toByte()

            val payload = ByteBuffer
                .allocate(36 + 16 + 16 + 1 + 4 + 1 + 1 + 4 + 1 + descBytes.size)
                .order(ByteOrder.LITTLE_ENDIAN)

            writeUUID(payload, agentId)
            writeUUID(payload, udpConnection.getSessionId())
            writeUUID(payload, agentId)
            writeUUID(payload, destinationId)
            payload.put(0.toByte())
            payload.putInt(amount)
            payload.put(0.toByte())
            payload.put(0.toByte())
            payload.putInt(transactionType)
            payload.put(descBytes.size.toByte())
            payload.put(descBytes)
            
            udpConnection.sendPacket(MessageIdRegistry.MONEY_TRANSFER_REQUEST, payload.array().copyOf(payload.position()), reliable = true)
            
            Log.i(TAG, "Sent payment via UDP: $amount ${_currencySymbol.value} to $destinationId")
            
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
            Log.e(TAG, "Failed to send payment via UDP", e)
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
