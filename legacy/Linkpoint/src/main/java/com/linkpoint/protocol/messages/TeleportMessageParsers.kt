package com.linkpoint.protocol.messages

import android.util.Log
import com.linkpoint.protocol.types.LLVector3
import java.nio.ByteBuffer
import java.nio.ByteOrder

internal object TeleportMessageParsers {
    private const val TAG = "TeleportMessageParsers"

    fun parseTeleportFinish(data: ByteArray): TeleportFinishData? = try {
        val buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN)
        val agentId = buffer.readUuid()
        val locationId = buffer.int
        val simIP = buffer.readIpv4String()
        val simPort = buffer.short.toInt() and 0xFFFF
        val regionHandle = buffer.long
        val seedLen = buffer.short.toInt() and 0xFFFF
        val seed = if (seedLen > 0 && buffer.remaining() >= seedLen) String(ByteArray(seedLen).also(buffer::get), Charsets.UTF_8).trimEnd('\u0000') else ""
        val simAccess = buffer.get().toInt() and 0xFF
        val teleportFlags = buffer.int
        TeleportFinishData(agentId, locationId, simIP, simPort, regionHandle, seed, simAccess, teleportFlags)
    } catch (e: Exception) {
        Log.e(TAG, "Failed to parse TeleportFinish", e)
        null
    }

    fun parseTeleportFailed(data: ByteArray): TeleportFailedData? = try {
        val buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN)
        val agentId = buffer.readUuid()
        val reasonLen = buffer.get().toInt() and 0xFF
        val reason = if (reasonLen > 0 && buffer.remaining() >= reasonLen) String(ByteArray(reasonLen).also(buffer::get), Charsets.UTF_8).trimEnd('\u0000') else "Unknown error"
        TeleportFailedData(agentId, reason)
    } catch (e: Exception) {
        Log.e(TAG, "Failed to parse TeleportFailed", e)
        null
    }

    fun parseTeleportProgress(data: ByteArray): TeleportProgressData? = try {
        val buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN)
        val agentId = buffer.readUuid()
        val teleportFlags = buffer.int
        val msgLen = buffer.get().toInt() and 0xFF
        val message = if (msgLen > 0 && buffer.remaining() >= msgLen) String(ByteArray(msgLen).also(buffer::get), Charsets.UTF_8).trimEnd('\u0000') else ""
        TeleportProgressData(agentId, teleportFlags, message)
    } catch (e: Exception) {
        Log.e(TAG, "Failed to parse TeleportProgress", e)
        null
    }

    fun parseEnableSimulator(data: ByteArray): EnableSimulatorData? = try {
        val buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN)
        EnableSimulatorData(buffer.long, buffer.readIpv4String(), buffer.short.toInt() and 0xFFFF)
    } catch (e: Exception) {
        Log.e(TAG, "Failed to parse EnableSimulator", e)
        null
    }

    fun parseCrossedRegion(data: ByteArray): CrossedRegionData? = try {
        val buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN)
        val agentId = buffer.readUuid()
        val sessionId = buffer.readUuid()
        val simIP = buffer.readIpv4String()
        val simPort = buffer.short.toInt() and 0xFFFF
        val regionHandle = buffer.long
        val seedLen = buffer.short.toInt() and 0xFFFF
        val seed = if (seedLen > 0 && buffer.remaining() >= seedLen) String(ByteArray(seedLen).also(buffer::get), Charsets.UTF_8).trimEnd('\u0000') else ""
        val position = LLVector3(buffer.float, buffer.float, buffer.float)
        val lookAt = LLVector3(buffer.float, buffer.float, buffer.float)
        CrossedRegionData(agentId, sessionId, simIP, simPort, regionHandle, seed, position, lookAt)
    } catch (e: Exception) {
        Log.e(TAG, "Failed to parse CrossedRegion", e)
        null
    }
}
