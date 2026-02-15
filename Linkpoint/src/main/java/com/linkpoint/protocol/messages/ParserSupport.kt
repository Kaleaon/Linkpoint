package com.linkpoint.protocol.messages

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID

internal val MESSAGE_BYTE_ORDER: ByteOrder = ByteOrder.LITTLE_ENDIAN

internal fun bytesToUuid(bytes: ByteArray): UUID {
    val bb = ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN)
    return UUID(bb.long, bb.long)
}

internal fun ByteBuffer.readUuid(): UUID {
    val bytes = ByteArray(16)
    get(bytes)
    return bytesToUuid(bytes)
}

internal fun ByteBuffer.readIpv4String(): String {
    val ipBytes = ByteArray(4)
    get(ipBytes)
    return "${ipBytes[0].toInt() and 0xFF}.${ipBytes[1].toInt() and 0xFF}.${ipBytes[2].toInt() and 0xFF}.${ipBytes[3].toInt() and 0xFF}"
}
