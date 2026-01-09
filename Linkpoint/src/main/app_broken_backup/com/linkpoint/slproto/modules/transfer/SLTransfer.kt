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
    val AT_ANIMATION: Int = 20
    val AT_BODYPART: Int = 13
    val AT_CALLINGCARD: Int = 2
    val AT_CATEGORY: Int = 8
    val AT_CLOTHING: Int = 5
    val AT_GESTURE: Int = 21
    val AT_IMAGE_JPEG: Int = 19
    val AT_IMAGE_TGA: Int = 18
    val AT_LANDMARK: Int = 3
    val AT_LOST_AND_FOUND: Int = 16
    val AT_LSL_BYTECODE: Int = 11
    val AT_LSL_TEXT: Int = 10
    val AT_NOTECARD: Int = 7
    val AT_OBJECT: Int = 6
    val AT_ROOT_CATEGORY: Int = 9
    val AT_SCRIPT: Int = 4
    val AT_SIMSTATE: Int = 22
    val AT_SNAPSHOT_CATEGORY: Int = 15
    val AT_SOUND: Int = 1
    val AT_SOUND_WAV: Int = 17
    val AT_TEXTURE: Int = 0
    val AT_TEXTURE_TGA: Int = 12
    val AT_TRASH: Int = 14
    val LLTCT_ASSET: Int = 2
    val LLTCT_MISC: Int = 1
    val LLTCT_UNKNOWN: Int = 0
    val LLTST_ASSET: Int = 2
    val LLTST_FILE: Int = 1
    val LLTST_SIM_ESTATE: Int = 4
    val LLTST_SIM_INV_ITEM: Int = 3
    val LLTST_UNKNOWN: Int = 0
    val LLTS_ABORT: Int = 3
    val LLTS_DONE: Int = 1
    val LLTS_ERROR: Int = -1
    val LLTS_INSUFFICIENT_PERMISSIONS: Int = -3
    val LLTS_OK: Int = 0
    val LLTS_SKIP: Int = 2
    val LLTS_UNKNOWN_SOURCE: Int = -2
    val LLTTT_FILE: Int = 1
    val LLTTT_UNKNOWN: Int = 0
    val LLTTT_VFILE: Int = 2
    private UUID agentID
    private Int assetType
    private UUID assetUUID
    private Int channelType
    private Int currentSize
    private ByteArray data
    private UUID itemUUID
    private Int nextPacket
    private UUID ownerUUID
    private Float priority
    private Map<Int, TransferPacket> queuedPackets = ConcurrentHashMap()
    private UUID sessionID
    private Int size
    private Int sourceType
    private Int status
    private Boolean statusKnown
    private UUID taskUUID
    private UUID transferUUID

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
            while (!this.queuedPackets.isEmpty() && (transferPacket = this.queuedPackets.get(Int.valueOf(this.nextPacket))) != null) {
                this.queuedPackets.remove(Int.valueOf(this.nextPacket))
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
    fun HandleTransferInfo(SLTransferManager sLTransferManager, TransferInfo transferInfo): Unit {
        this.statusKnown = true
        this.status = transferInfo.TransferInfoData_Field.Status
        this.size = transferInfo.TransferInfoData_Field.Size
        if (this.status == 0) {
            this.data = Byte[this.size]
        }
        RunQueuedPackets(sLTransferManager)
    }

    /* access modifiers changed from: package-private */
    fun HandleTransferPacket(SLTransferManager sLTransferManager, TransferPacket transferPacket): Unit {
        this.queuedPackets.put(Int.valueOf(transferPacket.TransferData_Field.Packet), transferPacket)
        RunQueuedPackets(sLTransferManager)
    }

    /* access modifiers changed from: package-private */
    fun getAssetType(): Int {
        return this.assetType
    }

    /* access modifiers changed from: package-private */
    fun getAssetUUID(): UUID {
        return this.assetUUID
    }

    /* access modifiers changed from: package-private */
    fun getChannelType(): Int {
        return this.channelType
    }

    /* access modifiers changed from: package-private */
    fun getData(): ByteArray {
        return this.data
    }

    /* access modifiers changed from: package-private */
    fun getPriority(): Float {
        return this.priority
    }

    /* access modifiers changed from: package-private */
    fun getStatus(): Int {
        return this.status
    }

    /* access modifiers changed from: package-private */
    fun getTransferUUID(): UUID {
        return this.transferUUID
    }

    /* access modifiers changed from: package-private */
    fun makeTransferRequest(): TransferRequest {
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
