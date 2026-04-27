package com.linkpoint.slproto

import com.linkpoint.slproto.types.LLQuaternion
import com.linkpoint.slproto.types.LLVector3
import com.linkpoint.slproto.types.LLVector3d
import com.linkpoint.slproto.types.LLVector4
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.UUID

/**
 * Base class for all Second Life protocol messages. Implements the helper
 * packing/unpacking utilities used by the generated message classes so they can
 * mirror the original Java implementation.
 */
abstract class SLMessage {

    var sequenceNumber: Int = 0
    var isReliable: Boolean = true
    var isResent: Boolean = false
    var zeroCoded: Boolean = false
    var sentTimeMillis: Long = 0
    var retries: Int = 0

    open fun getMessageName(): String = this::class.java.simpleName

    open fun CalcPayloadSize(): Int = 0

    open fun PackPayload(buffer: ByteBuffer) {}

    open fun UnpackPayload(buffer: ByteBuffer) {}

    open fun encode(buffer: ByteBuffer) {
        val previous = buffer.order()
        buffer.order(ByteOrder.LITTLE_ENDIAN)
        PackPayload(buffer)
        buffer.order(previous)
    }

    open fun decode(buffer: ByteBuffer) {
        val previous = buffer.order()
        buffer.order(ByteOrder.LITTLE_ENDIAN)
        UnpackPayload(buffer)
        buffer.order(previous)
    }

    open fun handleMessage(circuit: SLCircuit) {
        circuit.DefaultMessageHandler(this)
    }

    open fun handleMessageAcknowledged() {}

    open fun handleMessageTimeout() {
        com.linkpoint.Debug.Log("Message timeout: ${getMessageName()}")
    }

    open fun isHighFrequency(): Boolean = false

    open fun isMediumFrequency(): Boolean = true

    open fun isLowFrequency(): Boolean = false

    protected fun packBoolean(buffer: ByteBuffer, value: Boolean) {
        buffer.put(if (value) 1 else 0)
    }

    protected fun packByte(buffer: ByteBuffer, value: Byte) {
        buffer.put(value)
    }

    protected fun packShort(buffer: ByteBuffer, value: Short) {
        buffer.putShort(value)
    }

    protected fun packInt(buffer: ByteBuffer, value: Int) {
        buffer.putInt(value)
    }

    protected fun packLong(buffer: ByteBuffer, value: Long) {
        buffer.putLong(value)
    }

    protected fun packFloat(buffer: ByteBuffer, value: Float) {
        buffer.putFloat(value)
    }

    protected fun packDouble(buffer: ByteBuffer, value: Double) {
        buffer.putDouble(value)
    }

    protected fun packUUID(buffer: ByteBuffer, uuid: UUID) {
        buffer.putLong(uuid.mostSignificantBits)
        buffer.putLong(uuid.leastSignificantBits)
    }

    protected fun packLLVector3(buffer: ByteBuffer, value: LLVector3) {
        buffer.putFloat(value.x)
        buffer.putFloat(value.y)
        buffer.putFloat(value.z)
    }

    protected fun packLLVector3d(buffer: ByteBuffer, value: LLVector3d) {
        buffer.putDouble(value.x)
        buffer.putDouble(value.y)
        buffer.putDouble(value.z)
    }

    protected fun packLLVector4(buffer: ByteBuffer, value: LLVector4) {
        buffer.putFloat(value.x)
        buffer.putFloat(value.y)
        buffer.putFloat(value.z)
        buffer.putFloat(value.w)
    }

    protected fun packLLQuaternion(buffer: ByteBuffer, value: LLQuaternion) {
        buffer.putFloat(value.x)
        buffer.putFloat(value.y)
        buffer.putFloat(value.z)
    }

    protected fun packFixed(buffer: ByteBuffer, data: ByteArray, size: Int) {
        if (data.size == size) {
            buffer.put(data)
            return
        }
        repeat(size) { index ->
            buffer.put(if (index < data.size) data[index] else 0)
        }
    }

    protected fun packVariable(buffer: ByteBuffer, data: ByteArray, lengthBytes: Int) {
        require(lengthBytes == 1 || lengthBytes == 2) { "lengthBytes must be 1 or 2" }
        val length = data.size
        if (lengthBytes == 1) {
            buffer.put(length.toByte())
        } else {
            buffer.put((length and 0xFF).toByte())
            buffer.put(((length shr 8) and 0xFF).toByte())
        }
        buffer.put(data)
    }

    protected fun unpackBoolean(buffer: ByteBuffer): Boolean = buffer.get().toInt() != 0

    protected fun unpackByte(buffer: ByteBuffer): Byte = buffer.get()

    protected fun unpackShort(buffer: ByteBuffer): Short = buffer.getShort()

    protected fun unpackInt(buffer: ByteBuffer): Int = buffer.getInt()

    protected fun unpackLong(buffer: ByteBuffer): Long = buffer.getLong()

    protected fun unpackFloat(buffer: ByteBuffer): Float = buffer.getFloat()

    protected fun unpackDouble(buffer: ByteBuffer): Double = buffer.getDouble()

    protected fun unpackUUID(buffer: ByteBuffer): UUID {
        val most = buffer.getLong()
        val least = buffer.getLong()
        return UUID(most, least)
    }

    protected fun unpackLLVector3(buffer: ByteBuffer): LLVector3 = LLVector3(
        buffer.getFloat(),
        buffer.getFloat(),
        buffer.getFloat()
    )

    protected fun unpackLLVector3d(buffer: ByteBuffer): LLVector3d = LLVector3d(
        buffer.getDouble(),
        buffer.getDouble(),
        buffer.getDouble()
    )

    protected fun unpackLLVector4(buffer: ByteBuffer): LLVector4 = LLVector4(
        buffer.getFloat(),
        buffer.getFloat(),
        buffer.getFloat(),
        buffer.getFloat()
    )

    protected fun unpackLLQuaternion(buffer: ByteBuffer): LLQuaternion = LLQuaternion(
        buffer.getFloat(),
        buffer.getFloat(),
        buffer.getFloat(),
        0f
    )

    protected fun unpackFixed(buffer: ByteBuffer, length: Int): ByteArray {
        val data = ByteArray(length)
        buffer.get(data)
        return data
    }

    protected fun unpackVariable(buffer: ByteBuffer, lengthBytes: Int): ByteArray {
        val length = when (lengthBytes) {
            1 -> buffer.get().toInt() and 0xFF
            2 -> (buffer.get().toInt() and 0xFF) or ((buffer.get().toInt() and 0xFF) shl 8)
            else -> throw IllegalArgumentException("lengthBytes must be 1 or 2")
        }
        val data = ByteArray(length)
        buffer.get(data)
        return data
    }

    companion object {
        const val LL_ZERO_CODE_FLAG: Byte = 0x80.toByte()
        const val LL_RELIABLE_FLAG: Byte = 0x40
        const val LL_RESENT_FLAG: Byte = 0x20
        const val LL_ACK_FLAG: Byte = 0x10

        private val OEM_CHARSET: Charset = Charset.forName("ISO-8859-1")

        @JvmStatic
        fun stringToVariableUTF(str: String?): ByteArray {
            val safe = (str ?: "") + '\u0000'
            return safe.toByteArray(StandardCharsets.UTF_8)
        }

        @JvmStatic
        fun stringFromVariableUTF(data: ByteArray): String {
            val decoded = String(data, StandardCharsets.UTF_8)
            return decoded.trimEnd('\u0000')
        }

        @JvmStatic
        fun stringToVariableOEM(str: String?): ByteArray {
            val safe = (str ?: "") + '\u0000'
            return safe.toByteArray(OEM_CHARSET)
        }

        @JvmStatic
        fun stringFromVariableOEM(data: ByteArray): String {
            val decoded = String(data, OEM_CHARSET)
            return decoded.trimEnd('\u0000')
        }

        @JvmStatic
        fun writeVariableField(buffer: ByteBuffer, data: ByteArray) {
            buffer.put(data.size.toByte())
            buffer.put(data)
        }

        @JvmStatic
        fun readVariableField(buffer: ByteBuffer): ByteArray {
            val length = buffer.get().toInt() and 0xFF
            val data = ByteArray(length)
            buffer.get(data)
            return data
        }

        @JvmStatic
        fun writeVariableString(buffer: ByteBuffer, value: String?) {
            val bytes = stringToVariableUTF(value ?: "")
            writeVariableField(buffer, bytes)
        }

        @JvmStatic
        fun readVariableString(buffer: ByteBuffer): String {
            return stringFromVariableUTF(readVariableField(buffer))
        }

        @JvmStatic
        fun writeUUID(buffer: ByteBuffer, uuid: UUID) {
            buffer.putLong(uuid.mostSignificantBits)
            buffer.putLong(uuid.leastSignificantBits)
        }

        @JvmStatic
        fun readUUID(buffer: ByteBuffer): UUID {
            val msb = buffer.getLong()
            val lsb = buffer.getLong()
            return UUID(msb, lsb)
        }

        @JvmStatic
        fun writeIPAddress(buffer: ByteBuffer, ip: ByteArray) {
            if (ip.size == 4) {
                buffer.put(ip)
            } else {
                buffer.putInt(0)
            }
        }

        @JvmStatic
        fun readIPAddress(buffer: ByteBuffer): ByteArray {
            val ip = ByteArray(4)
            buffer.get(ip)
            return ip
        }
    }
}

class TestMessage : SLMessage() {
    var testData: String = ""

    override fun encode(buffer: ByteBuffer) {
        writeVariableString(buffer, testData)
    }

    override fun decode(buffer: ByteBuffer) {
        testData = readVariableString(buffer)
    }
}
