// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.net.Inet4Address;
import java.nio.ByteBuffer;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class OpenCircuit extends SLMessage
{
    public static class CircuitInfo
    {

        public Inet4Address IP;
        public int Port;

        public CircuitInfo()
        {
        }
    }


    public CircuitInfo CircuitInfo_Field;

    public OpenCircuit()
    {
        zeroCoded = false;
        CircuitInfo_Field = new CircuitInfo();
    }

    public int CalcPayloadSize()
    {
        return 10;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleOpenCircuit(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)-1);
        bytebuffer.put((byte)-4);
        packIPAddress(bytebuffer, CircuitInfo_Field.IP);
        packShort(bytebuffer, (short)CircuitInfo_Field.Port);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        CircuitInfo_Field.IP = unpackIPAddress(bytebuffer);
        CircuitInfo_Field.Port = unpackShort(bytebuffer) & 0xffff;
    }
}
