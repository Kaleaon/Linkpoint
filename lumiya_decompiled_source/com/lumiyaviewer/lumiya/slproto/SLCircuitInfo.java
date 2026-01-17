package com.lumiyaviewer.lumiya.slproto;

import com.lumiyaviewer.lumiya.slproto.auth.SLAuthReply;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.UUID;
/* loaded from: classes.dex */
public class SLCircuitInfo {
    public final UUID agentID;
    final int circuitCode;
    public final UUID sessionID;
    final SocketAddress socketAddress;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SLCircuitInfo(SLAuthReply sLAuthReply) {
        this.socketAddress = new InetSocketAddress(sLAuthReply.simAddress, sLAuthReply.simPort);
        this.sessionID = sLAuthReply.sessionID;
        this.agentID = sLAuthReply.agentID;
        this.circuitCode = sLAuthReply.circuitCode;
    }
}
