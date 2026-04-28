package com.linkpoint.linden.llmessage

import java.net.InetAddress
import java.net.InetSocketAddress

data class Host(val address: UInt, val port: UShort) : Comparable<Host> {

    fun isOk(): Boolean = address != 0u && port != 0.toUShort()

    fun toInetSocketAddress(): InetSocketAddress {
        val bytes = byteArrayOf(
            (address shr 24).toByte(),
            (address shr 16).toByte(),
            (address shr 8).toByte(),
            address.toByte()
        )
        return InetSocketAddress(InetAddress.getByAddress(bytes), port.toInt())
    }

    override fun toString(): String {
        val a = (address shr 24) and 0xFFu
        val b = (address shr 16) and 0xFFu
        val c = (address shr 8) and 0xFFu
        val d = address and 0xFFu
        return "$a.$b.$c.$d:${port.toInt()}"
    }

    override fun compareTo(other: Host): Int {
        val cmp = address.compareTo(other.address)
        return if (cmp != 0) cmp else port.compareTo(other.port)
    }

    companion object {
        val INVALID: Host = Host(0u, 0u)

        fun fromString(s: String): Host? {
            val colon = s.indexOf(':')
            return try {
                if (colon == -1) {
                    val ip = parseIpString(s) ?: return null
                    Host(ip, 0u)
                } else {
                    val ip = parseIpString(s.substring(0, colon)) ?: return null
                    val port = s.substring(colon + 1).toUShortOrNull() ?: return null
                    Host(ip, port)
                }
            } catch (_: Exception) {
                null
            }
        }

        fun fromInetAddress(addr: InetAddress, port: Int): Host {
            val raw = addr.address
            val ip = ((raw[0].toUInt() and 0xFFu) shl 24) or
                     ((raw[1].toUInt() and 0xFFu) shl 16) or
                     ((raw[2].toUInt() and 0xFFu) shl 8) or
                     (raw[3].toUInt() and 0xFFu)
            return Host(ip, port.toUShort())
        }

        private fun parseIpString(s: String): UInt? {
            val parts = s.trim().split('.')
            if (parts.size != 4) return null
            var result = 0u
            for (part in parts) {
                val byte = part.toUIntOrNull() ?: return null
                if (byte > 255u) return null
                result = (result shl 8) or byte
            }
            return result
        }
    }
}
