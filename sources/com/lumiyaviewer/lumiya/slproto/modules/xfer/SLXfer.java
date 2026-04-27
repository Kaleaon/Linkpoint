// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.modules.xfer;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.messages.ConfirmXferPacket;
import com.lumiyaviewer.lumiya.slproto.messages.RequestXfer;
import com.lumiyaviewer.lumiya.slproto.messages.SendXferPacket;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.modules.xfer:
//            SLXferManager, ELLPath

public class SLXfer
{
    public static interface SLXferCompletionListener
    {

        public abstract void onXferComplete(Object obj, String s, byte abyte0[]);
    }

    private static class XferListenerInvocation
    {

        private SLXferCompletionListener listener;
        private Object tag;

        public void invokeListener(String s, byte abyte0[])
        {
            listener.onXferComplete(tag, s, abyte0);
        }

        public XferListenerInvocation(Object obj, SLXferCompletionListener slxfercompletionlistener)
        {
            tag = obj;
            listener = slxfercompletionlistener;
        }
    }


    private boolean deleteOnCompletion;
    private int expectedDataLen;
    private int expectedPacketNum;
    private String fileName;
    private ELLPath filePath;
    private boolean hasCompleted;
    private long id;
    private List listeners;
    private byte receivedData[];
    private int receivedDataLen;

    public SLXfer(long l, String s, ELLPath ellpath, boolean flag)
    {
        listeners = new LinkedList();
        hasCompleted = false;
        id = l;
        fileName = s;
        filePath = ellpath;
        deleteOnCompletion = flag;
        receivedData = null;
        receivedDataLen = 0;
        expectedDataLen = 0;
        expectedPacketNum = 0;
    }

    public void HandleDataPacket(SLXferManager slxfermanager, SendXferPacket sendxferpacket)
    {
        byte byte0 = 4;
        Debug.Printf("XferPacket: packetNum %d (0x%x), dataLen %d", new Object[] {
            Integer.valueOf(sendxferpacket.XferID_Field.Packet), Integer.valueOf(sendxferpacket.XferID_Field.Packet), Integer.valueOf(sendxferpacket.DataPacket_Field.Data.length)
        });
        int j = 0x7fffffff & sendxferpacket.XferID_Field.Packet;
        boolean flag;
        if ((sendxferpacket.XferID_Field.Packet & 0x80000000) != 0)
        {
            flag = true;
        } else
        {
            flag = false;
        }
        if (j == expectedPacketNum)
        {
            int i;
            if (j == 0)
            {
                if (sendxferpacket.DataPacket_Field.Data.length < 4)
                {
                    return;
                }
                expectedDataLen = sendxferpacket.DataPacket_Field.Data[0] & 0xff | sendxferpacket.DataPacket_Field.Data[1] << 8 & 0xff00 | sendxferpacket.DataPacket_Field.Data[2] << 16 & 0xff0000 | sendxferpacket.DataPacket_Field.Data[3] << 24 & 0xff000000;
                Debug.Printf("XferPacket: expected data len = %d (0x%x)", new Object[] {
                    Integer.valueOf(expectedDataLen), Integer.valueOf(expectedDataLen)
                });
                receivedData = new byte[expectedDataLen];
                i = sendxferpacket.DataPacket_Field.Data.length - 4;
            } else
            {
                i = sendxferpacket.DataPacket_Field.Data.length;
                byte0 = 0;
            }
            if (receivedDataLen + i > expectedDataLen)
            {
                return;
            }
            System.arraycopy(sendxferpacket.DataPacket_Field.Data, byte0, receivedData, receivedDataLen, i);
            receivedDataLen = i + receivedDataLen;
            expectedPacketNum = expectedPacketNum + 1;
            if (flag)
            {
                hasCompleted = true;
            }
        }
        if (j <= expectedPacketNum)
        {
            ConfirmXferPacket confirmxferpacket = new ConfirmXferPacket();
            confirmxferpacket.XferID_Field.ID = id;
            confirmxferpacket.XferID_Field.Packet = sendxferpacket.XferID_Field.Packet;
            confirmxferpacket.isReliable = true;
            slxfermanager.SendMessage(confirmxferpacket);
        }
    }

    public void StartTransfer(SLXferManager slxfermanager)
    {
        RequestXfer requestxfer = new RequestXfer();
        requestxfer.XferID_Field.ID = id;
        requestxfer.XferID_Field.Filename = SLMessage.stringToVariableOEM(fileName);
        requestxfer.XferID_Field.FilePath = filePath.getCode();
        requestxfer.XferID_Field.DeleteOnCompletion = deleteOnCompletion;
        requestxfer.XferID_Field.UseBigPackets = false;
        requestxfer.XferID_Field.VFileID = new UUID(0L, 0L);
        requestxfer.XferID_Field.VFileType = -1;
        requestxfer.isReliable = true;
        slxfermanager.SendMessage(requestxfer);
    }

    public void addListener(SLXferCompletionListener slxfercompletionlistener, Object obj)
    {
        listeners.add(new XferListenerInvocation(obj, slxfercompletionlistener));
    }

    public byte[] getData()
    {
        return receivedData;
    }

    public String getFilename()
    {
        return fileName;
    }

    public void invokeListeners()
    {
        for (Iterator iterator = listeners.iterator(); iterator.hasNext(); ((XferListenerInvocation)iterator.next()).invokeListener(fileName, receivedData)) { }
    }

    public boolean isCompleted()
    {
        return hasCompleted;
    }
}
