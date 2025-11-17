package com.linkpoint.slproto

import android.os.Parcel
import android.os.Parcelable
import com.linkpoint.Debug
import com.linkpoint.slproto.messages.SLMessageFactory
import com.linkpoint.slproto.messages.SLMessageHandler
import com.linkpoint.slproto.types.LLQuaternion
import com.linkpoint.slproto.types.LLVector3
import com.linkpoint.slproto.types.LLVector3d
import com.linkpoint.slproto.types.LLVector4
import com.linkpoint.rawbuffers.DirectByteBuffer
import java.net.Inet4Address
import java.net.InetAddress
import java.net.UnknownHostException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.Charset
import java.util.UUID

/**
 * Base class for all Second Life protocol messages.
 *
 * Provides helper utilities for packing/unpacking message payloads and
 * implements common logic for reliable messaging, zero encoding, and parceling.
 */
abstract class SLMessage : Parcelable {

    var isReliable: Boolean = false
    var isResent: Boolean = false
    var zeroCoded: Boolean = false
    var seqNum: Int = 0
    var sentTimeMillis: Long = 0L
    var retries: Int = 0

    private var listener: SLMessageEventListener? = null

    abstract fun CalcPayloadSize(): Int

    open fun handleMessage(handler: SLMessageHandler) {
        handler.dispatch(this)
    }

    fun Handle(handler: SLMessageHandler) = handleMessage(handler)

    @Throws(Exception::class)
    abstract fun PackPayload(buffer: ByteBuffer)

    @Throws(Exception::class)
    abstract fun UnpackPayload(buffer: ByteBuffer)

    open fun handleMessageAcknowledged() {
        listener?.onMessageAcknowledged(this)
    }

    open fun handleMessageTimeout() {
        listener?.onMessageTimeout(this)
    }

    override fun describeContents(): Int = 0

    override fun writeToParcel(
        dest: Parcel,
        flags: Int,
    ) {
        val payloadSize = CalcPayloadSize()
        val data = ByteArray(payloadSize)
        val buffer = ByteBuffer.wrap(data).order(ByteOrder.nativeOrder())
        PackPayload(buffer)
        dest.writeInt(payloadSize)
        dest.writeByteArray(data)
    }

    fun Pack(packetBuffer: ByteBuffer, tempBuffer: ByteBuffer) {
        packetBuffer.clear()
        packetBuffer.order(ByteOrder.BIG_ENDIAN)

        var header: Byte = if (isReliable) LL_RELIABLE_FLAG else 0
        if (isResent) {
            header = (header.toInt() or LL_RESENT_FLAG.toInt()).toByte()
        }

        packetBuffer.put(header)
        packetBuffer.putInt(seqNum)
        packetBuffer.put(0) // Extra header bytes (unused)

        if (zeroCoded) {
            tempBuffer.clear()
            tempBuffer.order(ByteOrder.BIG_ENDIAN)
            PackPayloadLE(tempBuffer)
            tempBuffer.flip()

            val originalLimit = tempBuffer.limit()
            val payloadStart = packetBuffer.position()

            Companion.ZeroEncode(tempBuffer, packetBuffer)

            if (packetBuffer.position() - payloadStart < originalLimit) {
                packetBuffer.put(0, (packetBuffer.get(0).toInt() or LL_ZERO_CODE_FLAG.toInt()).toByte())
                return
            }

            // Zero encoding did not reduce size, fall back to raw payload
            packetBuffer.position(payloadStart)
            tempBuffer.rewind()
            packetBuffer.put(tempBuffer)
        } else {
            PackPayloadLE(packetBuffer)
        }
    }

    fun AppendPendingAcks(packetBuffer: ByteBuffer, pendingAcks: MutableList<Int>): Int {
        if (pendingAcks.isEmpty()) {
            return 0
        }

        var appended = 0
        synchronized(pendingAcks) {
            val iterator = pendingAcks.iterator()
            while (iterator.hasNext() && packetBuffer.position() <= MAX_PAYLOAD_SIZE) {
                packetBuffer.putInt(iterator.next())
                appended++
            }
        }

        if (appended > 0) {
            val header = packetBuffer.get(0)
            packetBuffer.put(0, (header.toInt() or LL_ACK_FLAG.toInt()).toByte())
            packetBuffer.put(appended.toByte())
        }

        return appended
    }

    fun setEventListener(listener: SLMessageEventListener?) {
        this.listener = listener
    }

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
        val previousOrder = buffer.order()
        buffer.order(ByteOrder.BIG_ENDIAN)
        buffer.putLong(uuid.mostSignificantBits)
        buffer.putLong(uuid.leastSignificantBits)
        buffer.order(previousOrder)
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

    protected fun packIPAddress(buffer: ByteBuffer, address: Inet4Address) {
        buffer.put(address.address)
    }

    protected fun unpackBoolean(buffer: ByteBuffer): Boolean = buffer.get().toInt() != 0

    protected fun unpackByte(buffer: ByteBuffer): Byte = buffer.get()

    protected fun unpackShort(buffer: ByteBuffer): Short = buffer.getShort()

    protected fun unpackInt(buffer: ByteBuffer): Int = buffer.getInt()

    protected fun unpackLong(buffer: ByteBuffer): Long = buffer.getLong()

    protected fun unpackFloat(buffer: ByteBuffer): Float = buffer.getFloat()

    protected fun unpackDouble(buffer: ByteBuffer): Double = buffer.getDouble()

    protected fun unpackUUID(buffer: ByteBuffer): UUID {
        val previousOrder = buffer.order()
        buffer.order(ByteOrder.BIG_ENDIAN)
        val most = buffer.getLong()
        val least = buffer.getLong()
        buffer.order(previousOrder)
        return UUID(most, least)
    }

    protected fun unpackLLVector3(buffer: ByteBuffer): LLVector3 = LLVector3().apply {
        x = buffer.getFloat()
        y = buffer.getFloat()
        z = buffer.getFloat()
    }

    protected fun unpackLLVector3d(buffer: ByteBuffer): LLVector3d = LLVector3d().apply {
        x = buffer.getDouble()
        y = buffer.getDouble()
        z = buffer.getDouble()
    }

    protected fun unpackLLVector4(buffer: ByteBuffer): LLVector4 = LLVector4().apply {
        x = buffer.getFloat()
        y = buffer.getFloat()
        z = buffer.getFloat()
        w = buffer.getFloat()
    }

    protected fun unpackLLQuaternion(buffer: ByteBuffer): LLQuaternion = LLQuaternion().apply {
        x = buffer.getFloat()
        y = buffer.getFloat()
        z = buffer.getFloat()
    }

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

    protected fun unpackIPAddress(buffer: ByteBuffer): Inet4Address? {
        val bytes = ByteArray(4)
        buffer.get(bytes)
        return try {
            InetAddress.getByAddress(bytes) as? Inet4Address
        } catch (_: UnknownHostException) {
            null
        }
    }

    companion object {
        const val MAX_MESSAGE_SIZE: Int = 65536
        const val MAX_PAYLOAD_SIZE: Int = 1018
        const val MAX_TRANSMIT_SIZE: Int = 1024

        private const val LL_ZERO_CODE_FLAG: Byte = 0x80.toByte()
        private const val LL_RELIABLE_FLAG: Byte = 0x40
        private const val LL_RESENT_FLAG: Byte = 0x20
        private const val LL_ACK_FLAG: Byte = 0x10

        private val OEM_CHARSET: Charset = Charset.forName("ISO-8859-1")

        @JvmField
        val CREATOR: Parcelable.Creator<SLMessage> = object : Parcelable.Creator<SLMessage> {
            override fun createFromParcel(parcel: Parcel): SLMessage {
                val size = parcel.readInt()
                val data = ByteArray(size)
                parcel.readByteArray(data)
                val buffer = ByteBuffer.wrap(data).order(ByteOrder.nativeOrder())
                val messageId = DecodeMessageIDGeneric(buffer)
                val message = SLMessageFactory.CreateByID(messageId) ?: SLDefaultMessage()
                message.UnpackPayload(buffer)
                return message
            }

            override fun newArray(size: Int): Array<SLMessage?> = arrayOfNulls(size)
        }

        fun DecodeMessageID(buffer: ByteBuffer): Int {
            val first = buffer.get().toInt() and 0xFF
            if (first != 0xFF) {
                return first
            }

            val second = buffer.get().toInt() and 0xFF
            if (second != 0xFF) {
                return (0xFF shl 8) or second
            }

            val value = buffer.getShort().toInt() and 0xFFFF
            return (0xFFFF shl 16) or value
        }

        fun DecodeMessageIDGeneric(buffer: ByteBuffer): Int {
            val first = buffer.get().toInt() and 0xFF
            if (first != 0xFF) {
                return first
            }

            val second = buffer.get().toInt() and 0xFF
            if (second != 0xFF) {
                return (0xFF shl 8) or second
            }

            val high = buffer.get().toInt() and 0xFF
            val low = buffer.get().toInt() and 0xFF
            return (0xFFFF shl 16) or (high shl 8) or low
        }

        fun Unpack(
            packetBuffer: ByteBuffer,
            decodedBuffer: ByteBuffer,
            receivedAcks: MutableList<Int>,
        ): SLMessage? {
            val originalLimit = packetBuffer.limit()

            val flags = packetBuffer.get().toInt() and 0xFF
            val sequence = packetBuffer.getInt()
            val extraHeaderBytes = packetBuffer.get()

            if (extraHeaderBytes.toInt() != 0) {
                packetBuffer.position(packetBuffer.position() + extraHeaderBytes)
            }

            if (flags and LL_ACK_FLAG.toInt() != 0) {
                val ackCount = packetBuffer.get(packetBuffer.limit() - 1).toInt() and 0xFF
                var ackPos = packetBuffer.limit() - 1 - (ackCount * 4)

                repeat(ackCount) {
                    receivedAcks.add(packetBuffer.getInt(ackPos))
                    ackPos += 4
                }

                packetBuffer.limit(packetBuffer.limit() - 1 - (ackCount * 4))
            }

            val payloadBuffer =
                if (flags and LL_ZERO_CODE_FLAG.toInt() != 0) {
                    decodedBuffer.clear()
                    decodedBuffer.order(ByteOrder.BIG_ENDIAN)
                    ZeroDecode(decodedBuffer, packetBuffer)
                    decodedBuffer.flip()
                    decodedBuffer
                } else {
                    packetBuffer
                }

            val messageId = DecodeMessageID(payloadBuffer)
            val message = SLMessageFactory.CreateByID(messageId) ?: SLDefaultMessage()

            message.seqNum = sequence
            message.isReliable = flags and LL_RELIABLE_FLAG.toInt() != 0
            message.isResent = flags and LL_RESENT_FLAG.toInt() != 0
            message.zeroCoded = flags and LL_ZERO_CODE_FLAG.toInt() != 0

            try {
                message.UnpackPayloadLE(payloadBuffer)
            } catch (ex: Exception) {
                Debug.Log("Failed to unpack (${message.javaClass.simpleName}), zeroCoded = ${message.zeroCoded}")
                Debug.DumpBuffer("decodedPayload", payloadBuffer)
                Debug.DumpBuffer("origPacket w/o acks", packetBuffer)
                packetBuffer.limit(originalLimit)
                Debug.DumpBuffer("origPacket", packetBuffer)
                ex.printStackTrace()
                return null
            }

            packetBuffer.limit(originalLimit)
            return message
        }

        fun flipBytes(value: Int): Int = Integer.reverseBytes(value)

        fun stringToVariableUTF(text: String?): ByteArray =
            ((text ?: "") + '\u0000').toByteArray(Charsets.UTF_8)

        fun stringFromVariableUTF(data: ByteArray): String =
            String(data, Charsets.UTF_8).trimEnd('\u0000')

        fun stringToVariableOEM(text: String?): ByteArray =
            ((text ?: "") + '\u0000').toByteArray(OEM_CHARSET)

        fun stringFromVariableOEM(data: ByteArray): String =
            String(data, OEM_CHARSET).trimEnd('\u0000')

        private fun ZeroDecode(destination: ByteBuffer, source: ByteBuffer) {
            require(destination.hasArray()) { "Destination buffer must be backed by an array" }
            require(source.hasArray()) { "Source buffer must be backed by an array" }

            val destArray = destination.array()
            val destOffset = destination.arrayOffset() + destination.position()
            val destRemaining = destination.capacity() - destination.position()

            val srcArray = source.array()
            val srcOffset = source.arrayOffset() + source.position()
            val srcRemaining = source.remaining()

            val decodedLength = DirectByteBuffer.zeroDecode(destArray, destOffset, destRemaining, srcArray, srcOffset, srcRemaining)
            destination.position(destination.position() + decodedLength)
        }

        private fun ZeroEncode(source: ByteBuffer, destination: ByteBuffer) {
            var runLength = 0
            var zeroRunStarted = false

            while (source.hasRemaining()) {
                val value = source.get()
                if (value.toInt() == 0) {
                    if (!zeroRunStarted) {
                        destination.put(0)
                        zeroRunStarted = true
                    }
                    runLength++
                    if (runLength == 255) {
                        destination.put(runLength.toByte())
                        runLength = 0
                        zeroRunStarted = false
                    }
                } else {
                    if (runLength != 0) {
                        destination.put(runLength.toByte())
                        runLength = 0
                        zeroRunStarted = false
                    }
                    destination.put(value)
                }
            }

            if (runLength != 0) {
                destination.put(runLength.toByte())
            }
        }
    }

    private fun PackPayloadLE(buffer: ByteBuffer) {
        val previousOrder = buffer.order()
        buffer.order(ByteOrder.LITTLE_ENDIAN)
        PackPayload(buffer)
        buffer.order(previousOrder)
    }

    private fun UnpackPayloadLE(buffer: ByteBuffer) {
        val previousOrder = buffer.order()
        buffer.order(ByteOrder.LITTLE_ENDIAN)
        UnpackPayload(buffer)
        buffer.order(previousOrder)
    }
}
