package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer

/**
 * StartPingCheck - Server requests ping response
 */
class StartPingCheck(
    var pingID: Byte = 0
) : SLMessage() {
    
    override fun getMessageName() = "StartPingCheck"
    
    override fun encode(buffer: ByteBuffer) {
        buffer.put(pingID)
    }
    
    override fun decode(buffer: ByteBuffer) {
        pingID = buffer.get()
    }
}

/**
 * CompletePingCheck - Response to ping request
 */
class CompletePingCheck(
    var pingID: Byte = 0
) : SLMessage() {
    
    override fun getMessageName() = "CompletePingCheck"
    
    override fun encode(buffer: ByteBuffer) {
        buffer.put(pingID)
    }
    
    override fun decode(buffer: ByteBuffer) {
        pingID = buffer.get()
    }
}
