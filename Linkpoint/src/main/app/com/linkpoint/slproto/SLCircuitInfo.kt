package com.linkpoint.slproto

import com.linkpoint.slproto.auth.SLAuthReply
import java.net.InetSocketAddress
import java.net.SocketAddress
import java.util.UUID

class SLCircuitInfo(authReply: SLAuthReply) {
    val socketAddress: SocketAddress = InetSocketAddress(authReply.simAddress, authReply.simPort)
    val sessionID: UUID = authReply.sessionID
    val agentID: UUID = authReply.agentID
    internal val circuitCode: Int = authReply.circuitCode
}
