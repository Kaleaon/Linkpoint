// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.net.Inet4Address;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class DataServerLogout extends SLMessage
{
    public static class UserData
    {

        public UUID AgentID;
        public boolean Disconnect;
        public UUID SessionID;
        public Inet4Address ViewerIP;

        public UserData()
        {
        }
    }


    public UserData UserData_Field;

    public DataServerLogout()
    {
        zeroCoded = false;
        UserData_Field = new UserData();
    }

    public int CalcPayloadSize()
    {
        return 41;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleDataServerLogout(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)-5);
        packUUID(bytebuffer, UserData_Field.AgentID);
        packIPAddress(bytebuffer, UserData_Field.ViewerIP);
        packBoolean(bytebuffer, UserData_Field.Disconnect);
        packUUID(bytebuffer, UserData_Field.SessionID);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        UserData_Field.AgentID = unpackUUID(bytebuffer);
        UserData_Field.ViewerIP = unpackIPAddress(bytebuffer);
        UserData_Field.Disconnect = unpackBoolean(bytebuffer);
        UserData_Field.SessionID = unpackUUID(bytebuffer);
    }
}
