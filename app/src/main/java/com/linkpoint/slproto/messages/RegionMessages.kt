package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.util.*

/**
 * Region handshake message
 */
class RegionHandshakeMessage : SLMessage() {
    var regionFlags: Int = 0
    var simAccess: Byte = 0
    var simName: String = ""
    var simOwner: UUID = UUID.randomUUID()
    var isEstateManager: Boolean = false
    var waterHeight: Float = 0f
    var billableFactor: Float = 0f
    var cacheId: UUID = UUID.randomUUID()
    var terrainBase0: UUID = UUID.randomUUID()
    var terrainBase1: UUID = UUID.randomUUID()
    var terrainBase2: UUID = UUID.randomUUID()
    var terrainBase3: UUID = UUID.randomUUID()
    var terrainDetail0: UUID = UUID.randomUUID()
    var terrainDetail1: UUID = UUID.randomUUID()
    var terrainDetail2: UUID = UUID.randomUUID()
    var terrainDetail3: UUID = UUID.randomUUID()
    var terrainStartHeight00: Float = 0f
    var terrainStartHeight01: Float = 0f
    var terrainStartHeight10: Float = 0f
    var terrainStartHeight11: Float = 0f
    var terrainHeightRange00: Float = 0f
    var terrainHeightRange01: Float = 0f
    var terrainHeightRange10: Float = 0f
    var terrainHeightRange11: Float = 0f
    var regionId: UUID = UUID.randomUUID()
    var cpuClassId: Int = 0
    var cpuRatio: Int = 0
    var coloName: String = ""
    var productSku: String = ""
    var productName: String = ""
    
    override fun packPayload(buffer: ByteBuffer) {
        // Region info
        buffer.putInt(regionFlags)
        buffer.put(simAccess)
        
        val simNameBytes = simName.toByteArray(StandardCharsets.UTF_8)
        buffer.put(simNameBytes.size.toByte())
        buffer.put(simNameBytes)
        
        buffer.putLong(simOwner.mostSignificantBits)
        buffer.putLong(simOwner.leastSignificantBits)
        buffer.put(if (isEstateManager) 1 else 0)
        buffer.putFloat(waterHeight)
        buffer.putFloat(billableFactor)
        
        buffer.putLong(cacheId.mostSignificantBits)
        buffer.putLong(cacheId.leastSignificantBits)
        
        // Terrain textures
        buffer.putLong(terrainBase0.mostSignificantBits)
        buffer.putLong(terrainBase0.leastSignificantBits)
        buffer.putLong(terrainBase1.mostSignificantBits)
        buffer.putLong(terrainBase1.leastSignificantBits)
        buffer.putLong(terrainBase2.mostSignificantBits)
        buffer.putLong(terrainBase2.leastSignificantBits)
        buffer.putLong(terrainBase3.mostSignificantBits)
        buffer.putLong(terrainBase3.leastSignificantBits)
        
        buffer.putLong(terrainDetail0.mostSignificantBits)
        buffer.putLong(terrainDetail0.leastSignificantBits)
        buffer.putLong(terrainDetail1.mostSignificantBits)
        buffer.putLong(terrainDetail1.leastSignificantBits)
        buffer.putLong(terrainDetail2.mostSignificantBits)
        buffer.putLong(terrainDetail2.leastSignificantBits)
        buffer.putLong(terrainDetail3.mostSignificantBits)
        buffer.putLong(terrainDetail3.leastSignificantBits)
        
        // Terrain heights
        buffer.putFloat(terrainStartHeight00)
        buffer.putFloat(terrainStartHeight01)
        buffer.putFloat(terrainStartHeight10)
        buffer.putFloat(terrainStartHeight11)
        buffer.putFloat(terrainHeightRange00)
        buffer.putFloat(terrainHeightRange01)
        buffer.putFloat(terrainHeightRange10)
        buffer.putFloat(terrainHeightRange11)
        
        // Region ID
        buffer.putLong(regionId.mostSignificantBits)
        buffer.putLong(regionId.leastSignificantBits)
        
        // CPU info
        buffer.putInt(cpuClassId)
        buffer.putInt(cpuRatio)
        
        val coloBytes = coloName.toByteArray(StandardCharsets.UTF_8)
        buffer.put(coloBytes.size.toByte())
        buffer.put(coloBytes)
        
        val skuBytes = productSku.toByteArray(StandardCharsets.UTF_8)
        buffer.put(skuBytes.size.toByte())
        buffer.put(skuBytes)
        
        val productBytes = productName.toByteArray(StandardCharsets.UTF_8)
        buffer.put(productBytes.size.toByte())
        buffer.put(productBytes)
    }
    
    override fun unpackPayload(buffer: ByteBuffer) {
        // Region info
        regionFlags = buffer.getInt()
        simAccess = buffer.get()
        
        val simNameLength = buffer.get().toInt() and 0xFF
        val simNameBytes = ByteArray(simNameLength)
        buffer.get(simNameBytes)
        simName = String(simNameBytes, StandardCharsets.UTF_8)
        
        val simOwnerMsb = buffer.getLong()
        val simOwnerLsb = buffer.getLong()
        simOwner = UUID(simOwnerMsb, simOwnerLsb)
        
        isEstateManager = buffer.get() != 0.toByte()
        waterHeight = buffer.getFloat()
        billableFactor = buffer.getFloat()
        
        val cacheMsb = buffer.getLong()
        val cacheLsb = buffer.getLong()
        cacheId = UUID(cacheMsb, cacheLsb)
        
        // Terrain textures
        terrainBase0 = UUID(buffer.getLong(), buffer.getLong())
        terrainBase1 = UUID(buffer.getLong(), buffer.getLong())
        terrainBase2 = UUID(buffer.getLong(), buffer.getLong())
        terrainBase3 = UUID(buffer.getLong(), buffer.getLong())
        terrainDetail0 = UUID(buffer.getLong(), buffer.getLong())
        terrainDetail1 = UUID(buffer.getLong(), buffer.getLong())
        terrainDetail2 = UUID(buffer.getLong(), buffer.getLong())
        terrainDetail3 = UUID(buffer.getLong(), buffer.getLong())
        
        // Terrain heights
        terrainStartHeight00 = buffer.getFloat()
        terrainStartHeight01 = buffer.getFloat()
        terrainStartHeight10 = buffer.getFloat()
        terrainStartHeight11 = buffer.getFloat()
        terrainHeightRange00 = buffer.getFloat()
        terrainHeightRange01 = buffer.getFloat()
        terrainHeightRange10 = buffer.getFloat()
        terrainHeightRange11 = buffer.getFloat()
        
        // Region ID
        regionId = UUID(buffer.getLong(), buffer.getLong())
        
        // CPU info
        cpuClassId = buffer.getInt()
        cpuRatio = buffer.getInt()
        
        val coloLength = buffer.get().toInt() and 0xFF
        val coloBytes = ByteArray(coloLength)
        buffer.get(coloBytes)
        coloName = String(coloBytes, StandardCharsets.UTF_8)
        
        val skuLength = buffer.get().toInt() and 0xFF
        val skuBytes = ByteArray(skuLength)
        buffer.get(skuBytes)
        productSku = String(skuBytes, StandardCharsets.UTF_8)
        
        val productLength = buffer.get().toInt() and 0xFF
        val productBytes = ByteArray(productLength)
        buffer.get(productBytes)
        productName = String(productBytes, StandardCharsets.UTF_8)
    }
    
    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.REGION_HANDSHAKE
    override fun getMessageName(): String = "RegionHandshake"
}

/**
 * Region handshake reply message
 */
class RegionHandshakeReplyMessage : SLMessage() {
    var agentId: UUID = UUID.randomUUID()
    var sessionId: UUID = UUID.randomUUID()
    var flags: Int = 0
    
    override fun packPayload(buffer: ByteBuffer) {
        buffer.putLong(agentId.mostSignificantBits)
        buffer.putLong(agentId.leastSignificantBits)
        buffer.putLong(sessionId.mostSignificantBits)
        buffer.putLong(sessionId.leastSignificantBits)
        buffer.putInt(flags)
    }
    
    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = UUID(buffer.getLong(), buffer.getLong())
        sessionId = UUID(buffer.getLong(), buffer.getLong())
        flags = buffer.getInt()
    }
    
    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.REGION_HANDSHAKE_REPLY
    override fun getMessageName(): String = "RegionHandshakeReply"
}