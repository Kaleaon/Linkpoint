package com.linkpoint.slproto.modules.transfer

import com.linkpoint.slproto.messages.TransferInfo
import com.linkpoint.slproto.messages.TransferPacket
import com.linkpoint.slproto.messages.TransferRequest
import com.linkpoint.slproto.users.manager.assets.AssetKey
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.Map
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class SLTransfer {
    const val Int AT_ANIMATION = 20
    const val Int AT_BODYPART = 13
    const val Int AT_CALLINGCARD = 2
    const val Int AT_CATEGORY = 8
    const val Int AT_CLOTHING = 5
    const val Int AT_GESTURE = 21
    const val Int AT_IMAGE_JPEG = 19
    const val Int AT_IMAGE_TGA = 18
    const val Int AT_LANDMARK = 3
    const val Int AT_LOST_AND_FOUND = 16
    const val Int AT_LSL_BYTECODE = 11
    const val Int AT_LSL_TEXT = 10
    const val Int AT_NOTECARD = 7
    const val Int AT_OBJECT = 6
    const val Int AT_ROOT_CATEGORY = 9
    const val Int AT_SCRIPT = 4
    const val Int AT_SIMSTATE = 22
    const val Int AT_SNAPSHOT_CATEGORY = 15
    const val Int AT_SOUND = 1
    const val Int AT_SOUND_WAV = 17
    const val Int AT_TEXTURE = 0
    const val Int AT_TEXTURE_TGA = 12
    const val Int AT_TRASH = 14
    const val Int LLTCT_ASSET = 2
    const val Int LLTCT_MISC = 1
    const val Int LLTCT_UNKNOWN = 0
    const val Int LLTST_ASSET = 2
    const val Int LLTST_FILE = 1
    const val Int LLTST_SIM_ESTATE = 4
    const val Int LLTST_SIM_INV_ITEM = 3
    const val Int LLTST_UNKNOWN = 0
    const val Int LLTS_ABORT = 3
    const val Int LLTS_DONE = 1
    const val Int LLTS_ERROR = -1
    const val Int LLTS_INSUFFICIENT_PERMISSIONS = -3
    const val Int LLTS_OK = 0
    const val Int LLTS_SKIP = 2
    const val Int LLTS_UNKNOWN_SOURCE = -2
    const val Int LLTTT_FILE = 1
    const val Int LLTTT_UNKNOWN = 0
    const val Int LLTTT_VFILE = 2
    private val UUID agentID
    private val Int assetType
    private val UUID assetUUID
    private val Int channelType
    private Int currentSize
    private Byte[] data
    private val UUID itemUUID
    private Int nextPacket
    private val UUID ownerUUID
    private val Float priority
    private val Map<Integer, TransferPacket> queuedPackets = ConcurrentHashMap()
    private val UUID sessionID
    private Int size
    private val Int sourceType
    private Int status
    private Boolean statusKnown
    private val UUID taskUUID
    private val UUID transferUUID

    SLTransfer(UUID uuid, UUID uuid2, AssetKey assetKey, Float f) {
        this.agentID = uuid
        this.sessionID = uuid2
        this.channelType = assetKey.channelType()
        this.sourceType = assetKey.sourceType()
        this.priority = f
        this.assetUUID = assetKey.assetUUID()
        this.assetType = assetKey.assetType()
        this.ownerUUID = assetKey.ownerUUID()
        this.itemUUID = assetKey.itemUUID()
        this.taskUUID = assetKey.taskUUID()
        this.transferUUID = UUID.randomUUID()
        this.statusKnown = false
        this.status = -1
        this.size = 0
        this.nextPacket = 0
        this.currentSize = 0
    }

    private Unit RunQueuedPackets(SLTransferManager sLTransferManager) {
        TransferPacket transferPacket
        if (this.statusKnown && this.status == 0) {
            while (!this.queuedPackets.isEmpty() && (transferPacket = this.queuedPackets.get(Integer.valueOf(this.nextPacket))) != null) {
                this.queuedPackets.remove(Integer.valueOf(this.nextPacket))
                this.nextPacket++
                Int length = transferPacket.TransferData_Field.Data.length
                System.arraycopy(transferPacket.TransferData_Field.Data, 0, this.data, this.currentSize, length)
                this.currentSize = length + this.currentSize
                if (transferPacket.TransferData_Field.Status != 0) {
                    this.status = transferPacket.TransferData_Field.Status
                }
            }
        }
        if (this.statusKnown && this.status != 0) {
            sLTransferManager.EndTransfer(this)
        }
    }

    /* access modifiers changed from: package-private */
    public Unit HandleTransferInfo(SLTransferManager sLTransferManager, TransferInfo transferInfo) {
        this.statusKnown = true
        this.status = transferInfo.TransferInfoData_Field.Status
        this.size = transferInfo.TransferInfoData_Field.Size
        if (this.status == 0) {
            this.data = Byte[this.size]
        }
        RunQueuedPackets(sLTransferManager)
    }

    /* access modifiers changed from: package-private */
    public Unit HandleTransferPacket(SLTransferManager sLTransferManager, TransferPacket transferPacket) {
        this.queuedPackets.put(Integer.valueOf(transferPacket.TransferData_Field.Packet), transferPacket)
        RunQueuedPackets(sLTransferManager)
    }

    /* access modifiers changed from: package-private */
    public Int getAssetType() {
        return this.assetType
    }

    /* access modifiers changed from: package-private */
    public UUID getAssetUUID() {
        return this.assetUUID
    }

    /* access modifiers changed from: package-private */
    public Int getChannelType() {
        return this.channelType
    }

    /* access modifiers changed from: package-private */
    public Byte[] getData() {
        return this.data
    }

    /* access modifiers changed from: package-private */
    public Float getPriority() {
        return this.priority
    }

    /* access modifiers changed from: package-private */
    public Int getStatus() {
        return this.status
    }

    /* access modifiers changed from: package-private */
    public UUID getTransferUUID() {
        return this.transferUUID
    }

    /* access modifiers changed from: package-private */
    public TransferRequest makeTransferRequest() {
        TransferRequest transferRequest = TransferRequest()
        transferRequest.TransferInfo_Field.TransferID = this.transferUUID
        transferRequest.TransferInfo_Field.ChannelType = this.channelType
        transferRequest.TransferInfo_Field.SourceType = this.sourceType
        transferRequest.TransferInfo_Field.Priority = this.priority
        ByteBuffer allocate = ByteBuffer.allocate(1024)
        allocate.order(ByteOrder.BIG_ENDIAN)
        if (this.sourceType == 3) {
            allocate.putLong(this.agentID.getMostSignificantBits())
            allocate.putLong(this.agentID.getLeastSignificantBits())
            allocate.putLong(this.sessionID.getMostSignificantBits())
            allocate.putLong(this.sessionID.getLeastSignificantBits())
            allocate.putLong(this.ownerUUID.getMostSignificantBits())
            allocate.putLong(this.ownerUUID.getLeastSignificantBits())
            if (this.taskUUID != null) {
                allocate.putLong(this.taskUUID.getMostSignificantBits())
                allocate.putLong(this.taskUUID.getLeastSignificantBits())
            } else {
                allocate.putLong(0)
                allocate.putLong(0)
            }
            allocate.putLong(this.itemUUID.getMostSignificantBits())
            allocate.putLong(this.itemUUID.getLeastSignificantBits())
        }
        allocate.putLong(this.assetUUID.getMostSignificantBits())
        allocate.putLong(this.assetUUID.getLeastSignificantBits())
        allocate.order(ByteOrder.LITTLE_ENDIAN)
        allocate.putInt(this.assetType)
        allocate.flip()
        transferRequest.TransferInfo_Field.Params = Byte[allocate.limit()]
        allocate.get(transferRequest.TransferInfo_Field.Params, 0, allocate.limit())
        transferRequest.isReliable = true
        return transferRequest
    }
}
