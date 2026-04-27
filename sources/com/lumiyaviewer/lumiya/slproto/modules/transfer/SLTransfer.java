// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.modules.transfer;

import com.lumiyaviewer.lumiya.slproto.messages.TransferInfo;
import com.lumiyaviewer.lumiya.slproto.messages.TransferPacket;
import com.lumiyaviewer.lumiya.slproto.messages.TransferRequest;
import com.lumiyaviewer.lumiya.slproto.users.manager.assets.AssetKey;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.modules.transfer:
//            SLTransferManager

public class SLTransfer
{

    public static final int AT_ANIMATION = 20;
    public static final int AT_BODYPART = 13;
    public static final int AT_CALLINGCARD = 2;
    public static final int AT_CATEGORY = 8;
    public static final int AT_CLOTHING = 5;
    public static final int AT_GESTURE = 21;
    public static final int AT_IMAGE_JPEG = 19;
    public static final int AT_IMAGE_TGA = 18;
    public static final int AT_LANDMARK = 3;
    public static final int AT_LOST_AND_FOUND = 16;
    public static final int AT_LSL_BYTECODE = 11;
    public static final int AT_LSL_TEXT = 10;
    public static final int AT_NOTECARD = 7;
    public static final int AT_OBJECT = 6;
    public static final int AT_ROOT_CATEGORY = 9;
    public static final int AT_SCRIPT = 4;
    public static final int AT_SIMSTATE = 22;
    public static final int AT_SNAPSHOT_CATEGORY = 15;
    public static final int AT_SOUND = 1;
    public static final int AT_SOUND_WAV = 17;
    public static final int AT_TEXTURE = 0;
    public static final int AT_TEXTURE_TGA = 12;
    public static final int AT_TRASH = 14;
    public static final int LLTCT_ASSET = 2;
    public static final int LLTCT_MISC = 1;
    public static final int LLTCT_UNKNOWN = 0;
    public static final int LLTST_ASSET = 2;
    public static final int LLTST_FILE = 1;
    public static final int LLTST_SIM_ESTATE = 4;
    public static final int LLTST_SIM_INV_ITEM = 3;
    public static final int LLTST_UNKNOWN = 0;
    public static final int LLTS_ABORT = 3;
    public static final int LLTS_DONE = 1;
    public static final int LLTS_ERROR = -1;
    public static final int LLTS_INSUFFICIENT_PERMISSIONS = -3;
    public static final int LLTS_OK = 0;
    public static final int LLTS_SKIP = 2;
    public static final int LLTS_UNKNOWN_SOURCE = -2;
    public static final int LLTTT_FILE = 1;
    public static final int LLTTT_UNKNOWN = 0;
    public static final int LLTTT_VFILE = 2;
    private final UUID agentID;
    private final int assetType;
    private final UUID assetUUID;
    private final int channelType;
    private int currentSize;
    private byte data[];
    private final UUID itemUUID;
    private int nextPacket;
    private final UUID ownerUUID;
    private final float priority;
    private final Map queuedPackets = new ConcurrentHashMap();
    private final UUID sessionID;
    private int size;
    private final int sourceType;
    private int status;
    private boolean statusKnown;
    private final UUID taskUUID;
    private final UUID transferUUID = UUID.randomUUID();

    SLTransfer(UUID uuid, UUID uuid1, AssetKey assetkey, float f)
    {
        agentID = uuid;
        sessionID = uuid1;
        channelType = assetkey.channelType();
        sourceType = assetkey.sourceType();
        priority = f;
        assetUUID = assetkey.assetUUID();
        assetType = assetkey.assetType();
        ownerUUID = assetkey.ownerUUID();
        itemUUID = assetkey.itemUUID();
        taskUUID = assetkey.taskUUID();
        statusKnown = false;
        status = -1;
        size = 0;
        nextPacket = 0;
        currentSize = 0;
    }

    private void RunQueuedPackets(SLTransferManager sltransfermanager)
    {
        if (!statusKnown || status != 0) goto _L2; else goto _L1
_L1:
        if (queuedPackets.isEmpty()) goto _L2; else goto _L3
_L3:
        TransferPacket transferpacket = (TransferPacket)queuedPackets.get(Integer.valueOf(nextPacket));
        if (transferpacket != null) goto _L4; else goto _L2
_L2:
        if (statusKnown && status != 0)
        {
            sltransfermanager.EndTransfer(this);
        }
        return;
_L4:
        queuedPackets.remove(Integer.valueOf(nextPacket));
        nextPacket = nextPacket + 1;
        int i = transferpacket.TransferData_Field.Data.length;
        System.arraycopy(transferpacket.TransferData_Field.Data, 0, data, currentSize, i);
        currentSize = i + currentSize;
        if (transferpacket.TransferData_Field.Status != 0)
        {
            status = transferpacket.TransferData_Field.Status;
        }
        if (true) goto _L1; else goto _L5
_L5:
    }

    void HandleTransferInfo(SLTransferManager sltransfermanager, TransferInfo transferinfo)
    {
        statusKnown = true;
        status = transferinfo.TransferInfoData_Field.Status;
        size = transferinfo.TransferInfoData_Field.Size;
        if (status == 0)
        {
            data = new byte[size];
        }
        RunQueuedPackets(sltransfermanager);
    }

    void HandleTransferPacket(SLTransferManager sltransfermanager, TransferPacket transferpacket)
    {
        queuedPackets.put(Integer.valueOf(transferpacket.TransferData_Field.Packet), transferpacket);
        RunQueuedPackets(sltransfermanager);
    }

    int getAssetType()
    {
        return assetType;
    }

    UUID getAssetUUID()
    {
        return assetUUID;
    }

    int getChannelType()
    {
        return channelType;
    }

    byte[] getData()
    {
        return data;
    }

    float getPriority()
    {
        return priority;
    }

    int getStatus()
    {
        return status;
    }

    UUID getTransferUUID()
    {
        return transferUUID;
    }

    TransferRequest makeTransferRequest()
    {
        TransferRequest transferrequest = new TransferRequest();
        transferrequest.TransferInfo_Field.TransferID = transferUUID;
        transferrequest.TransferInfo_Field.ChannelType = channelType;
        transferrequest.TransferInfo_Field.SourceType = sourceType;
        transferrequest.TransferInfo_Field.Priority = priority;
        ByteBuffer bytebuffer = ByteBuffer.allocate(1024);
        bytebuffer.order(ByteOrder.BIG_ENDIAN);
        if (sourceType == 3)
        {
            bytebuffer.putLong(agentID.getMostSignificantBits());
            bytebuffer.putLong(agentID.getLeastSignificantBits());
            bytebuffer.putLong(sessionID.getMostSignificantBits());
            bytebuffer.putLong(sessionID.getLeastSignificantBits());
            bytebuffer.putLong(ownerUUID.getMostSignificantBits());
            bytebuffer.putLong(ownerUUID.getLeastSignificantBits());
            if (taskUUID != null)
            {
                bytebuffer.putLong(taskUUID.getMostSignificantBits());
                bytebuffer.putLong(taskUUID.getLeastSignificantBits());
            } else
            {
                bytebuffer.putLong(0L);
                bytebuffer.putLong(0L);
            }
            bytebuffer.putLong(itemUUID.getMostSignificantBits());
            bytebuffer.putLong(itemUUID.getLeastSignificantBits());
        }
        bytebuffer.putLong(assetUUID.getMostSignificantBits());
        bytebuffer.putLong(assetUUID.getLeastSignificantBits());
        bytebuffer.order(ByteOrder.LITTLE_ENDIAN);
        bytebuffer.putInt(assetType);
        bytebuffer.flip();
        transferrequest.TransferInfo_Field.Params = new byte[bytebuffer.limit()];
        bytebuffer.get(transferrequest.TransferInfo_Field.Params, 0, bytebuffer.limit());
        transferrequest.isReliable = true;
        return transferrequest;
    }
}
