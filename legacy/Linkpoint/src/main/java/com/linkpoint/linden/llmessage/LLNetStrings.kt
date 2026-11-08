package com.linkpoint.linden.llmessage

import java.net.InetAddress
import java.nio.ByteBuffer
import java.nio.ByteOrder

object LLNetStrings {
    fun addressToString(ip: UInt, port: UShort): String =
        "${(ip shr 24) and 0xFFu}.${(ip shr 16) and 0xFFu}.${(ip shr 8) and 0xFFu}.${ip and 0xFFu}:$port"

    fun ipToString(ip: UInt): String =
        "${(ip shr 24) and 0xFFu}.${(ip shr 16) and 0xFFu}.${(ip shr 8) and 0xFFu}.${ip and 0xFFu}"

    fun stringToIP(s: String): UInt {
        val parts = s.trim().split('.')
        if (parts.size != 4) return 0u
        return parts.fold(0u) { acc, part ->
            (acc shl 8) or (part.toUIntOrNull() ?: 0u)
        }
    }

    fun hostToNetworkOrder(value: UInt): UInt {
        val buf = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN)
        buf.putInt(value.toInt())
        return buf.getInt(0).toUInt()
    }

    fun networkToHostOrder(value: UInt): UInt = hostToNetworkOrder(value)

    fun resolveHost(hostname: String): UInt = runCatching {
        val addr = InetAddress.getByName(hostname)
        val bytes = addr.address
        (bytes[0].toUInt() and 0xFFu shl 24) or
        (bytes[1].toUInt() and 0xFFu shl 16) or
        (bytes[2].toUInt() and 0xFFu shl 8) or
        (bytes[3].toUInt() and 0xFFu)
    }.getOrDefault(0u)
}
