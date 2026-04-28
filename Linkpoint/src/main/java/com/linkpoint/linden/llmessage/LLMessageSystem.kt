package com.linkpoint.linden.llmessage

import com.linkpoint.linden.llcommon.LLTimer
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean

class LLMessageSystem(
    private val port: Int = 0,
    private val maxMessageSize: Int = 4096
) {
    private val circuits: MutableMap<Host, LLCircuit> = ConcurrentHashMap()
    private val dispatcher = LLDispatcher()
    private var socket: DatagramSocket? = null
    private val running = AtomicBoolean(false)
    private var receiveThread: Thread? = null
    private val sendTimer = LLTimer()

    var maxSendBandwidth: Float = 512_000f
    var currentTemplate: MessageTemplate? = null
    private val buildBuffer = mutableMapOf<String, Any>()

    fun open(): Boolean = runCatching {
        socket = if (port == 0) DatagramSocket() else DatagramSocket(port)
        socket!!.soTimeout = 100
        true
    }.getOrDefault(false)

    fun close() {
        running.set(false)
        receiveThread?.interrupt()
        receiveThread = null
        socket?.close()
        socket = null
    }

    fun startReceiveThread() {
        if (!running.compareAndSet(false, true)) return
        receiveThread = Thread({
            val buf = ByteArray(maxMessageSize)
            while (running.get()) {
                try {
                    val packet = DatagramPacket(buf, buf.size)
                    socket?.receive(packet) ?: continue
                    val host = Host.fromInetAddress(packet.address, packet.port)
                    handleIncomingPacket(host, buf.copyOf(packet.length))
                } catch (_: java.net.SocketTimeoutException) {
                } catch (_: Exception) { if (!running.get()) break }
            }
        }, "LLMessageSystem-Receive").also { it.isDaemon = true; it.start() }
    }

    private fun handleIncomingPacket(host: Host, data: ByteArray) {
        if (data.size < 4) return
        val circuit = circuits.getOrPut(host) { LLCircuit(host) }
        circuit.packetsReceived++
        circuit.bytesIn += data.size
        circuit.updateLastPacketInTime()
        val msgName = extractMessageName(data) ?: return
        val fields = extractFields(data)
        dispatcher.dispatch(msgName, fields)
    }

    fun newMessage(name: String): Boolean {
        val tmpl = MessageTemplateCatalog[name] ?: return false
        currentTemplate = tmpl
        buildBuffer.clear()
        return true
    }

    fun addBlock(blockName: String) { buildBuffer["_block"] = blockName }

    fun addField(fieldName: String, value: Any) { buildBuffer[fieldName] = value }

    fun sendReliable(host: Host): Boolean = send(host, reliable = true)

    fun sendUnreliable(host: Host): Boolean = send(host, reliable = false)

    fun addMessageHandler(name: String, handler: LLMessageHandler) {
        dispatcher.addHandler(name, handler)
    }

    fun getOrCreateCircuit(host: Host): LLCircuit =
        circuits.getOrPut(host) { LLCircuit(host) }

    fun getCircuit(host: Host): LLCircuit? = circuits[host]

    fun getCircuitCount(): Int = circuits.size

    private fun send(host: Host, reliable: Boolean): Boolean {
        val tmpl = currentTemplate ?: return false
        val circuit = circuits.getOrPut(host) { LLCircuit(host) }
        val seq = circuit.nextSequenceNumber()
        val data = serializeMessage(tmpl.name, seq, reliable, buildBuffer)
        return runCatching {
            val packet = DatagramPacket(data, data.size, host.toInetSocketAddress())
            socket?.send(packet)
            circuit.packetsSent++
            circuit.bytesOut += data.size
            if (reliable) circuit.queueForResend(seq, data)
            true
        }.getOrDefault(false)
    }

    private fun serializeMessage(name: String, seq: UInt, reliable: Boolean, fields: Map<String, Any>): ByteArray {
        val buf = mutableListOf<Byte>()
        val flags: Byte = if (reliable) 0x40 else 0x00
        buf.add(flags)
        buf.add(((seq shr 24) and 0xFFu).toByte())
        buf.add(((seq shr 16) and 0xFFu).toByte())
        buf.add(((seq shr  8) and 0xFFu).toByte())
        buf.add((seq and 0xFFu).toByte())
        buf.add(0)
        val nameBytes = name.toByteArray(Charsets.UTF_8)
        buf.add(nameBytes.size.toByte())
        buf.addAll(nameBytes.toList())
        return buf.toByteArray()
    }

    private fun extractMessageName(data: ByteArray): String? {
        if (data.size < 7) return null
        return runCatching {
            val nameLen = data[6].toInt() and 0xFF
            if (nameLen <= 0 || 7 + nameLen > data.size) null
            else String(data, 7, nameLen, Charsets.UTF_8)
        }.getOrNull()
    }

    private fun extractFields(data: ByteArray): Map<String, Any> = mapOf(
        "_raw" to data,
        "_size" to data.size
    )
}
