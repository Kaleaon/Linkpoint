// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.modules.transfer;

import java.nio.ByteOrder;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.messages.TransferRequest;
import com.lumiyaviewer.lumiya.slproto.messages.TransferInfo;
import java.util.concurrent.ConcurrentHashMap;
import com.lumiyaviewer.lumiya.slproto.users.manager.assets.AssetKey;
import com.lumiyaviewer.lumiya.slproto.messages.TransferPacket;
import java.util.Map;
import java.util.UUID;

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
    private byte[] data;
    private final UUID itemUUID;
    private int nextPacket;
    private final UUID ownerUUID;
    private final float priority;
    private final Map<Integer, TransferPacket> queuedPackets;
    private final UUID sessionID;
    private int size;
    private final int sourceType;
    private int status;
    private boolean statusKnown;
    private final UUID taskUUID;
    private final UUID transferUUID;
    
    SLTransfer(final UUID agentID, final UUID sessionID, final AssetKey assetKey, final float priority) {
        this.queuedPackets = new ConcurrentHashMap<Integer, TransferPacket>();
        this.agentID = agentID;
        this.sessionID = sessionID;
        this.channelType = assetKey.channelType();
        this.sourceType = assetKey.sourceType();
        this.priority = priority;
        this.assetUUID = assetKey.assetUUID();
        this.assetType = assetKey.assetType();
        this.ownerUUID = assetKey.ownerUUID();
        this.itemUUID = assetKey.itemUUID();
        this.taskUUID = assetKey.taskUUID();
        this.transferUUID = UUID.randomUUID();
        this.statusKnown = false;
        this.status = -1;
        this.size = 0;
        this.nextPacket = 0;
        this.currentSize = 0;
    }
    
    private void RunQueuedPackets(final SLTransferManager slTransferManager) {
        if (this.statusKnown && this.status == 0) {
            while (!this.queuedPackets.isEmpty()) {
                final TransferPacket transferPacket = this.queuedPackets.get(this.nextPacket);
                if (transferPacket == null) {
                    break;
                }
                this.queuedPackets.remove(this.nextPacket);
                ++this.nextPacket;
                final int length = transferPacket.TransferData_Field.Data.length;
                System.arraycopy(transferPacket.TransferData_Field.Data, 0, this.data, this.currentSize, length);
                this.currentSize += length;
                if (transferPacket.TransferData_Field.Status == 0) {
                    continue;
                }
                this.status = transferPacket.TransferData_Field.Status;
            }
        }
        if (this.statusKnown && this.status != 0) {
            slTransferManager.EndTransfer(this);
        }
    }
    
    void HandleTransferInfo(final SLTransferManager slTransferManager, final TransferInfo transferInfo) {
        this.statusKnown = true;
        this.status = transferInfo.TransferInfoData_Field.Status;
        this.size = transferInfo.TransferInfoData_Field.Size;
        if (this.status == 0) {
            this.data = new byte[this.size];
        }
        this.RunQueuedPackets(slTransferManager);
    }
    
    void HandleTransferPacket(final SLTransferManager slTransferManager, final TransferPacket transferPacket) {
        this.queuedPackets.put(transferPacket.TransferData_Field.Packet, transferPacket);
        this.RunQueuedPackets(slTransferManager);
    }
    
    int getAssetType() {
        return this.assetType;
    }
    
    UUID getAssetUUID() {
        return this.assetUUID;
    }
    
    int getChannelType() {
        return this.channelType;
    }
    
    byte[] getData() {
        return this.data;
    }
    
    float getPriority() {
        return this.priority;
    }
    
    int getStatus() {
        return this.status;
    }
    
    UUID getTransferUUID() {
        return this.transferUUID;
    }
    
    TransferRequest makeTransferRequest() {
        final TransferRequest transferRequest = new TransferRequest();
        transferRequest.TransferInfo_Field.TransferID = this.transferUUID;
        transferRequest.TransferInfo_Field.ChannelType = this.channelType;
        transferRequest.TransferInfo_Field.SourceType = this.sourceType;
        transferRequest.TransferInfo_Field.Priority = this.priority;
        final ByteBuffer allocate = ByteBuffer.allocate(1024);
        allocate.order(ByteOrder.BIG_ENDIAN);
        if (this.sourceType == 3) {
            allocate.putLong(this.agentID.getMostSignificantBits());
            allocate.putLong(this.agentID.getLeastSignificantBits());
            allocate.putLong(this.sessionID.getMostSignificantBits());
            allocate.putLong(this.sessionID.getLeastSignificantBits());
            allocate.putLong(this.ownerUUID.getMostSignificantBits());
            allocate.putLong(this.ownerUUID.getLeastSignificantBits());
            if (this.taskUUID != null) {
                allocate.putLong(this.taskUUID.getMostSignificantBits());
                allocate.putLong(this.taskUUID.getLeastSignificantBits());
            }
            else {
                allocate.putLong(0L);
                allocate.putLong(0L);
            }
            allocate.putLong(this.itemUUID.getMostSignificantBits());
            allocate.putLong(this.itemUUID.getLeastSignificantBits());
        }
        allocate.putLong(this.assetUUID.getMostSignificantBits());
        allocate.putLong(this.assetUUID.getLeastSignificantBits());
        allocate.order(ByteOrder.LITTLE_ENDIAN);
        allocate.putInt(this.assetType);
        allocate.flip();
        allocate.get(transferRequest.TransferInfo_Field.Params = new byte[allocate.limit()], 0, allocate.limit());
        transferRequest.isReliable = true;
        return transferRequest;
    }
}
