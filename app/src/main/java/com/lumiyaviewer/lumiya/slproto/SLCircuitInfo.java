package com.lumiyaviewer.lumiya.slproto;

import com.lumiyaviewer.lumiya.slproto.auth.SLAuthReply;
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
