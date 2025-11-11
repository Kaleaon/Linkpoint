package com.linkpoint.slproto

import android.os.Parcel
import android.os.Parcelable
import com.linkpoint.slproto.messages.SLMessageFactory
import com.linkpoint.slproto.types.*
import java.net.Inet4Address
import java.net.InetAddress
import java.net.UnknownHostException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*

/**
 * Base class for all Second Life protocol messages
 * Modernized from original Lumiya Viewer
 */
abstract class SLMessage : Parcelable {
    // Message flags
    var isReliable: Boolean = false
    var isResent: Boolean = false
    var zeroCoded: Boolean = false
    var seqNum: Int = 0
    var retries: Int = 0
    var sentTimeMillis: Long = 0L

    private var listener: SLMessageEventListener? = null

    companion object {
        // Message flags
        private const val LL_ZERO_CODE_FLAG: Byte = 0x80.toByte()
        private const val LL_RELIABLE_FLAG: Byte = 0x40
        private const val LL_RESENT_FLAG: Byte = 0x20
        private const val LL_ACK_FLAG: Byte = 0x10

        // Size limits
        const val MAX_MESSAGE_SIZE = 65536
        const val MAX_PAYLOAD_SIZE = 1018
        const val MAX_TRANSMIT_SIZE = 1024

        /**
         * Decode message ID from buffer
         */
        fun decodeMessageID(buffer: ByteBuffer): Int {
            val firstByte = buffer.get()
            if (firstByte != 0xFF.toByte()) {
                return firstByte.toInt() and 0xFF
            }

            val secondByte = buffer.get()
            if (secondByte != 0xFF.toByte()) {
                return (secondByte.toInt() and 0xFF) or 0xFF00
            }

            return buffer.getShort().toInt() and 0xFFFF or 0xFFFF0000.toInt()
        }

        /**
         * Decode message ID (generic version)
         */
        fun decodeMessageIDGeneric(buffer: ByteBuffer): Int {
            val firstByte = buffer.get()
            if (firstByte != 0xFF.toByte()) {
                return firstByte.toInt() and 0xFF
            }

            val secondByte = buffer.get()
            if (secondByte != 0xFF.toByte()) {
                return (secondByte.toInt() and 0xFF) or 0xFF00
            }

            val highByte = buffer.get().toInt() and 0xFF
            val lowByte = buffer.get().toInt() and 0xFF
            return ((highByte shl 8) or lowByte) or 0xFFFF0000.toInt()
        }

        internal fun encodeMessageID(buffer: ByteBuffer, messageId: Int) {
            when {
                messageId and 0xFFFF0000.toInt() == 0xFFFF0000.toInt() -> {
                    buffer.put(0xFF.toByte())
                    buffer.put(0xFF.toByte())
                    buffer.put(((messageId ushr 8) and 0xFF).toByte())
                    buffer.put((messageId and 0xFF).toByte())
                }
                messageId and 0xFF00 == 0xFF00 -> {
                    buffer.put(0xFF.toByte())
                    buffer.put((messageId and 0xFF).toByte())
                }
                else -> buffer.put((messageId and 0xFF).toByte())
            }
        }

        /**
         * Unpack a message from network buffer
         */
        fun unpack(
            buffer: ByteBuffer,
            decodedBuffer: ByteBuffer,
            ackList: MutableList<Int>,
        ): SLMessage? {
            val originalLimit = buffer.limit()

            // Read header
            val flags = buffer.get().toInt() and 0xFF
            val sequenceNum = buffer.getInt()
            val extraHeaderBytes = buffer.get()

            // Skip extra header bytes
            if (extraHeaderBytes != 0.toByte()) {
                buffer.position(buffer.position() + extraHeaderBytes.toInt())
            }

            // Extract ACKs if present
            if ((flags and LL_ACK_FLAG.toInt()) != 0) {
                val ackByte = buffer.get(buffer.limit() - 1)
                val ackCount = ackByte.toInt() and 0xFF
                var ackPos = buffer.limit() - 1 - (ackCount * 4)

                for (i in 0 until ackCount) {
                    ackList.add(buffer.getInt(ackPos))
                    ackPos += 4
                }

                buffer.limit(buffer.limit() - 1 - (ackCount * 4))
            }

            // Decode zero-coded message if needed
            val payloadBuffer =
                if ((flags and LL_ZERO_CODE_FLAG.toInt()) != 0) {
                    decodedBuffer.clear()
                    decodedBuffer.order(ByteOrder.BIG_ENDIAN)
                    zeroDecode(decodedBuffer, buffer)
                    decodedBuffer.flip()
                    decodedBuffer as ByteBuffer
                } else {
                    buffer as ByteBuffer
                }

            // Create message instance
            val messageId = decodeMessageID(payloadBuffer)
            val message = SLMessageFactory.createByID(messageId) ?: SLDefaultMessage()

            // Set message properties
            message.seqNum = sequenceNum
            message.isReliable = (flags and LL_RELIABLE_FLAG.toInt()) != 0
            message.isResent = (flags and LL_RESENT_FLAG.toInt()) != 0
            message.zeroCoded = (flags and LL_ZERO_CODE_FLAG.toInt()) != 0

            // Unpack payload
            try {
                message.unpackPayloadLE(payloadBuffer)
            } catch (e: Exception) {
                println("Failed to unpack message: ${message.javaClass.simpleName}")
                e.printStackTrace()
                return null
            }

            buffer.limit(originalLimit)
            return message
        }

        /**
         * Zero-decode a buffer
         */
        private fun zeroDecode(
            output: ByteBuffer,
            input: ByteBuffer,
        ): ByteBuffer {
            while (input.hasRemaining()) {
                val byte = input.get()
                if (byte == 0.toByte()) {
                    if (!input.hasRemaining()) break
                    val count = input.get().toInt() and 0xFF
                    repeat(count) { output.put(0) }
                } else {
                    output.put(byte)
                }
            }
            return output
        }

        /**
         * Zero-encode a buffer.
         */
        internal fun zeroEncode(input: ByteBuffer, output: ByteBuffer) {
            var zeroCount = 0
            while (input.hasRemaining()) {
                val value = input.get()
                if (value == 0.toByte()) {
                    zeroCount++
                    if (zeroCount == 255) {
                        output.put(0)
                        output.put(255.toByte())
                        zeroCount = 0
                    }
                } else {
                    if (zeroCount > 0) {
                        output.put(0)
                        output.put(zeroCount.toByte())
                        zeroCount = 0
                    }
                    output.put(value)
                }
            }
            if (zeroCount > 0) {
                output.put(0)
                output.put(zeroCount.toByte())
            }
        }

        @JvmField
        val CREATOR =
            object : Parcelable.Creator<SLMessage> {
                override fun createFromParcel(parcel: Parcel): SLMessage? {
                    val size = parcel.readInt()
                    val data = ByteArray(size)
                    parcel.readByteArray(data)

                    val buffer = ByteBuffer.wrap(data).order(ByteOrder.nativeOrder())
                    val messageId = decodeMessageIDGeneric(buffer)
                    val message = SLMessageFactory.createByID(messageId) ?: return null

                    message.unpackPayload(buffer)
                    return message
                }

                override fun newArray(size: Int): Array<SLMessage?> {
                    return arrayOfNulls(size)
                }
            }
    }

    /**
     * Pack message payload into buffer
     */
    abstract fun packPayload(buffer: ByteBuffer)

    /**
     * Unpack message payload from buffer
     */
    abstract fun unpackPayload(buffer: ByteBuffer)

    /**
     * Get message ID
     */
    abstract fun getMessageID(): Int

    /**
     * Get message name
     */
    abstract fun getMessageName(): String

    /**
     * Pack payload with little-endian byte order
     */
    internal fun packPayloadLE(buffer: ByteBuffer) {
        val originalOrder = buffer.order()
        buffer.order(ByteOrder.LITTLE_ENDIAN)
        packPayload(buffer)
        buffer.order(originalOrder)
    }

    /**
     * Unpack payload with little-endian byte order
     */
    internal fun unpackPayloadLE(buffer: ByteBuffer) {
        val originalOrder = buffer.order()
        buffer.order(ByteOrder.LITTLE_ENDIAN)
        unpackPayload(buffer)
        buffer.order(originalOrder)
    }

    /**
     * Write message ID and payload into the target buffer, applying zero coding if required.
     */
    internal fun writePayloadForTransmit(target: ByteBuffer, scratch: ByteBuffer) {
        if (zeroCoded) {
            scratch.clear()
            scratch.order(ByteOrder.BIG_ENDIAN)
            encodeMessageID(scratch, getMessageID())
            packPayloadLE(scratch)
            scratch.flip()
            zeroEncode(scratch, target)
        } else {
            encodeMessageID(target, getMessageID())
            packPayloadLE(target)
        }
    }

    // Helper packing utilities
      protected fun packByte(buffer: ByteBuffer, value: Int) {
        buffer.put(value.toByte())
      }

      protected fun packInt(buffer: ByteBuffer, value: Int) {
        buffer.putInt(value)
      }

      protected fun packUInt16(buffer: ByteBuffer, value: Int) {
        buffer.putShort(value.toShort())
      }

      protected fun packShort(buffer: ByteBuffer, value: Int) {
        buffer.putShort(value.toShort())
      }

      protected fun packLong(buffer: ByteBuffer, value: Long) {
        buffer.putLong(value)
      }

      protected fun packFloat(buffer: ByteBuffer, value: Float) {
        buffer.putFloat(value)
      }

      protected fun packUUID(buffer: ByteBuffer, uuid: UUID) {
        val originalOrder = buffer.order()
        buffer.order(ByteOrder.BIG_ENDIAN)
        buffer.putLong(uuid.mostSignificantBits)
        buffer.putLong(uuid.leastSignificantBits)
        buffer.order(originalOrder)
      }

      protected fun packVariable(
          buffer: ByteBuffer,
          data: ByteArray,
          lengthBytes: Int,
      ) {
        require(lengthBytes == 1 || lengthBytes == 2) { "lengthBytes must be 1 or 2" }
        val length = data.size
        when (lengthBytes) {
          1 -> {
            require(length <= 0xFF) { "Variable field too large for 1 byte length: $length" }
            buffer.put(length.toByte())
          }
          2 -> {
            require(length <= 0xFFFF) { "Variable field too large for 2 byte length: $length" }
            buffer.putShort(length.toShort())
          }
        }
        buffer.put(data)
      }

      protected fun packIPAddress(
          buffer: ByteBuffer,
          address: Inet4Address?,
      ) {
        val bytes = address?.address ?: ByteArray(4)
        buffer.put(bytes)
      }

      protected fun unpackByte(buffer: ByteBuffer): Int {
        return buffer.get().toInt() and 0xFF
      }

      protected fun unpackInt(buffer: ByteBuffer): Int {
        return buffer.getInt()
      }

      protected fun unpackUInt16(buffer: ByteBuffer): Int {
        return buffer.getShort().toInt() and 0xFFFF
      }

      protected fun unpackLong(buffer: ByteBuffer): Long {
        return buffer.getLong()
      }

      protected fun unpackFloat(buffer: ByteBuffer): Float {
        return buffer.getFloat()
      }

      protected fun unpackUUID(buffer: ByteBuffer): UUID {
        val originalOrder = buffer.order()
        buffer.order(ByteOrder.BIG_ENDIAN)
        val most = buffer.getLong()
        val least = buffer.getLong()
        buffer.order(originalOrder)
        return UUID(most, least)
      }

      protected fun unpackVariable(
          buffer: ByteBuffer,
          lengthBytes: Int,
      ): ByteArray {
        require(lengthBytes == 1 || lengthBytes == 2) { "lengthBytes must be 1 or 2" }
        val length =
            when (lengthBytes) {
              1 -> buffer.get().toInt() and 0xFF
              else -> buffer.getShort().toInt() and 0xFFFF
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

    /**
     * Set message event listener
     */
    fun setListener(listener: SLMessageEventListener?) {
        this.listener = listener
    }

    /**
     * Get message event listener
     */
    fun getListener(): SLMessageEventListener? = listener

    // Parcelable implementation
    override fun describeContents(): Int = 0

    override fun writeToParcel(
        dest: Parcel,
        flags: Int,
    ) {
        val buffer = ByteBuffer.allocate(MAX_MESSAGE_SIZE)
        packPayloadLE(buffer)
        buffer.flip()

        val data = ByteArray(buffer.remaining())
        buffer.get(data)

        dest.writeInt(data.size)
        dest.writeByteArray(data)
    }
}

/**
 * Message event listener interface
 */
interface SLMessageEventListener {
    fun onMessageSent(message: SLMessage)

    fun onMessageReceived(message: SLMessage)

    fun onMessageFailed(
        message: SLMessage,
        error: Exception,
    )
}

/**
 * Default message implementation for unknown message types
 */
class SLDefaultMessage : SLMessage() {
    private var rawData: ByteArray = ByteArray(0)

    override fun packPayload(buffer: ByteBuffer) {
        buffer.put(rawData)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        rawData = ByteArray(buffer.remaining())
        buffer.get(rawData)
    }

    override fun getMessageID(): Int = 0

    override fun getMessageName(): String = "Unknown"
}
