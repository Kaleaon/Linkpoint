// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.net.Inet4Address;
import java.nio.ByteBuffer;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class EnableSimulator extends SLMessage
{
    public static class SimulatorInfo
    {

        public long Handle;
        public Inet4Address IP;
        public int Port;

        public SimulatorInfo()
        {
        }
    }


    public SimulatorInfo SimulatorInfo_Field;

    public EnableSimulator()
    {
        zeroCoded = false;
        SimulatorInfo_Field = new SimulatorInfo();
    }

    public int CalcPayloadSize()
    {
        return 18;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleEnableSimulator(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)-105);
        packLong(bytebuffer, SimulatorInfo_Field.Handle);
        packIPAddress(bytebuffer, SimulatorInfo_Field.IP);
        packShort(bytebuffer, (short)SimulatorInfo_Field.Port);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        SimulatorInfo_Field.Handle = unpackLong(bytebuffer);
        SimulatorInfo_Field.IP = unpackIPAddress(bytebuffer);
        SimulatorInfo_Field.Port = unpackShort(bytebuffer) & 0xffff;
    }
}
