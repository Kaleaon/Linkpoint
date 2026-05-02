package com.linkpoint.protocol.transport.nio

import java.net.InetSocketAddress
import java.net.StandardSocketOptions
import java.nio.ByteBuffer
import java.nio.channels.DatagramChannel
import java.nio.channels.SelectionKey
import java.nio.channels.Selector

class UdpDatagramTransport(
    private val datagramChannelFactory: () -> DatagramChannel = { DatagramChannel.open(java.net.StandardProtocolFamily.INET) },
    private val selectorFactory: () -> Selector = { Selector.open() }
) {
    var datagramChannel: DatagramChannel? = null
        private set
    var selector: Selector? = null
        private set
    var selectionKey: SelectionKey? = null
        private set

    fun connect(simIP: String, simPort: Int, bindPort: Int? = null) {
        close()
        val channel = datagramChannelFactory.invoke()
        channel.configureBlocking(false)
        channel.setOption(StandardSocketOptions.SO_REUSEADDR, true)
        bindPort?.let { channel.bind(InetSocketAddress(it)) }
        channel.connect(InetSocketAddress(simIP, simPort))
        val localSelector = selectorFactory.invoke()
        selectionKey = channel.register(localSelector, SelectionKey.OP_READ)
        datagramChannel = channel
        selector = localSelector
    }

    fun receive(buffer: ByteBuffer): Int = datagramChannel?.read(buffer) ?: -1
    fun send(buffer: ByteBuffer): Int = datagramChannel?.write(buffer) ?: -1
    fun close() { selectionKey = null; selector?.close(); selector = null; datagramChannel?.close(); datagramChannel = null }
}
