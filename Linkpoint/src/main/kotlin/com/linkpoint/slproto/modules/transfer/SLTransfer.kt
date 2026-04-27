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
    const val AT_ANIMATION: Int = 20
    const val AT_BODYPART: Int = 13
    const val AT_CALLINGCARD: Int = 2
    const val AT_CATEGORY: Int = 8
    const val AT_CLOTHING: Int = 5
    const val AT_GESTURE: Int = 21
    const val AT_IMAGE_JPEG: Int = 19
    const val AT_IMAGE_TGA: Int = 18
    const val AT_LANDMARK: Int = 3
    const val AT_LOST_AND_FOUND: Int = 16
    const val AT_LSL_BYTECODE: Int = 11
    const val AT_LSL_TEXT: Int = 10
    const val AT_NOTECARD: Int = 7
    const val AT_OBJECT: Int = 6
    const val AT_ROOT_CATEGORY: Int = 9
    const val AT_SCRIPT: Int = 4
    const val AT_SIMSTATE: Int = 22
    const val AT_SNAPSHOT_CATEGORY: Int = 15
    const val AT_SOUND: Int = 1
    const val AT_SOUND_WAV: Int = 17
    const val AT_TEXTURE: Int = 0
    const val AT_TEXTURE_TGA: Int = 12
    const val AT_TRASH: Int = 14
    const val LLTCT_ASSET: Int = 2
    const val LLTCT_MISC: Int = 1
    const val LLTCT_UNKNOWN: Int = 0
    const val LLTST_ASSET: Int = 2
    const val LLTST_FILE: Int = 1
    const val LLTST_SIM_ESTATE: Int = 4
    const val LLTST_SIM_INV_ITEM: Int = 3
    const val LLTST_UNKNOWN: Int = 0
    const val LLTS_ABORT: Int = 3
    const val LLTS_DONE: Int = 1
    const val LLTS_ERROR: Int = -1
    const val LLTS_INSUFFICIENT_PERMISSIONS: Int = -3
    const val LLTS_OK: Int = 0
    const val LLTS_SKIP: Int = 2
    const val LLTS_UNKNOWN_SOURCE: Int = -2
    const val LLTTT_FILE: Int = 1
    const val LLTTT_UNKNOWN: Int = 0
    const val LLTTT_VFILE: Int = 2
    private val UUID agentID
    private val Int assetType
    private val UUID assetUUID
    private val Int channelType
    private Int currentSize
    private ByteArray data
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

    private fun RunQueuedPackets(sLTransferManager: SLTransferManager) {
        TransferPacket transferPacket
        if (this.statusKnown && this.status == 0) {
            while (!this.queuedPackets.isEmpty() && (transferPacket = this.queuedPackets.get(Integer.valueOf(this.nextPacket))) != null) {
                this.queuedPackets.remove(Integer.valueOf(this.nextPacket))
                this.nextPacket++
                val length: Int = transferPacket.TransferData_Field.Data.length
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
    fun HandleTransferInfo(sLTransferManager: SLTransferManager, transferInfo: TransferInfo) {
        this.statusKnown = true
        this.status = transferInfo.TransferInfoData_Field.Status
        this.size = transferInfo.TransferInfoData_Field.Size
        if (this.status == 0) {
            this.data = Byte[this.size]
        }
        RunQueuedPackets(sLTransferManager)
    }

    /* access modifiers changed from: package-private */
    fun HandleTransferPacket(sLTransferManager: SLTransferManager, transferPacket: TransferPacket) {
        this.queuedPackets.put(Integer.valueOf(transferPacket.TransferData_Field.Packet), transferPacket)
        RunQueuedPackets(sLTransferManager)
    }

    /* access modifiers changed from: package-private */
     public fun getAssetType(): Int {
        return this.assetType
    }

    /* access modifiers changed from: package-private */
     public fun getAssetUUID(): UUID {
        return this.assetUUID
    }

    /* access modifiers changed from: package-private */
     public fun getChannelType(): Int {
        return this.channelType
    }

    /* access modifiers changed from: package-private */
     public fun getData(): ByteArray {
        return this.data
    }

    /* access modifiers changed from: package-private */
     public fun getPriority(): Float {
        return this.priority
    }

    /* access modifiers changed from: package-private */
     public fun getStatus(): Int {
        return this.status
    }

    /* access modifiers changed from: package-private */
     public fun getTransferUUID(): UUID {
        return this.transferUUID
    }

    /* access modifiers changed from: package-private */
     public fun makeTransferRequest(): TransferRequest {
        val transferRequest: TransferRequest = TransferRequest()
        transferRequest.TransferInfo_Field.TransferID = this.transferUUID
        transferRequest.TransferInfo_Field.ChannelType = this.channelType
        transferRequest.TransferInfo_Field.SourceType = this.sourceType
        transferRequest.TransferInfo_Field.Priority = this.priority
        val allocate: ByteBuffer = ByteBuffer.allocate(1024)
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
