// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto;

import java.net.InetSocketAddress;
import com.lumiyaviewer.lumiya.slproto.auth.SLAuthReply;
import java.net.SocketAddress;
import java.util.UUID;

public class SLCircuitInfo
{
    public final UUID agentID;
    final int circuitCode;
    public final UUID sessionID;
    final SocketAddress socketAddress;
    
    SLCircuitInfo(final SLAuthReply slAuthReply) {
        this.socketAddress = new InetSocketAddress(slAuthReply.simAddress, slAuthReply.simPort);
        this.sessionID = slAuthReply.sessionID;
        this.agentID = slAuthReply.agentID;
        this.circuitCode = slAuthReply.circuitCode;
    }
}
