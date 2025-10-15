package com.lumiyaviewer.lumiya.slproto

import android.os.SystemClock
import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.eventbus.EventBus
import com.lumiyaviewer.lumiya.slproto.auth.SLAuthReply
import com.lumiyaviewer.lumiya.slproto.caps.SLCapEventQueue.CapsEventType
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageRouter
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode
import com.lumiyaviewer.lumiya.slproto.messages.CompletePingCheck
import com.lumiyaviewer.lumiya.slproto.messages.PacketAck
import com.lumiyaviewer.lumiya.slproto.messages.PacketAck.Packets
import com.lumiyaviewer.lumiya.slproto.messages.SLMessageHandler
import com.lumiyaviewer.lumiya.slproto.messages.StartPingCheck
import com.lumiyaviewer.lumiya.slproto.messages.StartPingCheck.PingID
import com.lumiyaviewer.lumiya.slproto.modules.SLIdleHandler
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.channels.DatagramChannel
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.util.ArrayList
import java.util.Collections
import java.util.Iterator
import java.util.LinkedList
import java.util.List
import java.util.Queue
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicInteger

data class SLCircuit(
    var datagramChannel: DatagramChannel,
    var handledPackets: Queue<Integer>,
    var idleHandlers: List<SLIdleHandler> = LinkedList(),
    var lastPingID: Byte = (byte) 0,
    var lastPingSent: Long = 0,
    var lastReceivedPacketMillis: Long = 0,
    var lastReceivedSeqnum: Int = 0,
    var lastSeqNum: AtomicInteger,
    var messageRouter: SLMessageRouter = SLMessageRouter(),
    var outgoingQueue: ConcurrentLinkedQueue<SLMessage>,
    var pendingAcks: List<Integer>,
    var pingSentCount: Int = 0,
    var receivedAcks: List<Integer>,
    var rxBuffer: ByteBuffer,
    var selectionKey: SelectionKey,
    var tempBuffer: ByteBuffer,
    var timedOut: Boolean = false,
    var txBuffer: ByteBuffer,
    var unackedQueue: ConcurrentLinkedQueue<SLMessage>
)
