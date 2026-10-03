package com.linkpoint.protocol.types

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID

/**
 * Extension functions for ByteBuffer to handle Second Life protocol data types.
 * 
 * Per the SL protocol specification:
 * - UUIDs are stored as 16 raw bytes in big-endian (network) order
 * - Message body numeric values are little-endian
 * - Packet header values are big-endian
 * 
 * @see <a href="https://wiki.secondlife.com/wiki/Packet_Layout">SL Packet Layout</a>
 */

/**
 * Write a UUID to ByteBuffer in big-endian (SL protocol format).
 * UUIDs in SL are always stored as 16 raw bytes in big-endian order,
 * regardless of the buffer's current byte order.
 * 
 * @param uuid The UUID to write
 * @return This ByteBuffer for chaining
 */
fun ByteBuffer.putUUID(uuid: UUID): ByteBuffer {
    val originalOrder = order()
    order(ByteOrder.BIG_ENDIAN)
    putLong(uuid.mostSignificantBits)
    putLong(uuid.leastSignificantBits)
    order(originalOrder)
    return this
}

/**
 * Read a UUID from ByteBuffer in big-endian (SL protocol format).
 * 
 * @return The UUID read from the buffer
 */
fun ByteBuffer.getUUID(): UUID {
    val originalOrder = order()
    order(ByteOrder.BIG_ENDIAN)
    val msb = long
    val lsb = long
    order(originalOrder)
    return UUID(msb, lsb)
}
