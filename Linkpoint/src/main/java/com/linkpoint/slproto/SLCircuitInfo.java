package com.linkpoint.slproto;

import com.linkpoint.slproto.auth.SLAuthReply;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.UUID;

public class SLCircuitInfo {
    public final UUID agentID;
    final int circuitCode;
    public final UUID sessionID;
    final SocketAddress socketAddress;

    SLCircuitInfo(SLAuthReply sLAuthReply) {
        this.socketAddress = new InetSocketAddress(sLAuthReply.simAddress, sLAuthReply.simPort);
        this.sessionID = sLAuthReply.sessionID;
        this.agentID = sLAuthReply.agentID;
        this.circuitCode = sLAuthReply.circuitCode;
    }
}
