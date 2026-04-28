package com.linkpoint.linden.llcommon

import java.util.zip.CRC32 as JavaCRC32

class CRC32 {
    private val crc = JavaCRC32()

    fun update(byte: UByte) {
        crc.update(byte.toInt())
    }

    fun update(data: ByteArray) {
        crc.update(data)
    }

    fun update(data: ByteArray, offset: Int, length: Int) {
        crc.update(data, offset, length)
    }

    fun getCRC(): UInt = crc.value.toUInt()

    fun reset() {
        crc.reset()
    }
}
