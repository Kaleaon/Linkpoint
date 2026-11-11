package com.linkpoint.linden.llinventory

import com.linkpoint.linden.llcommon.LLSD
import com.linkpoint.linden.llcommon.LLUUID

typealias PermissionMask = UInt
typealias PermissionBit = UInt

object PermFlags {
    val TRANSFER: UInt    = 1u shl 13   // 0x00002000
    val MODIFY: UInt      = 1u shl 14   // 0x00004000
    val COPY: UInt        = 1u shl 15   // 0x00008000
    val EXPORT: UInt      = 1u shl 16   // 0x00010000  (OpenSim)
    val MOVE: UInt        = 1u shl 19   // 0x00080000
    val RESERVED: UInt    = 1u shl 31

    val NONE: UInt        = 0x00000000u
    val ALL: UInt         = 0x7FFFFFFFu
    val ITEM_UNRESTRICTED: UInt = MODIFY or COPY or TRANSFER

    // Field selectors for wire protocol
    val FIELD_BASE: UByte       = 0x01u
    val FIELD_OWNER: UByte      = 0x02u
    val FIELD_GROUP: UByte      = 0x04u
    val FIELD_EVERYONE: UByte   = 0x08u
    val FIELD_NEXT_OWNER: UByte = 0x10u
}

class Permissions(
    var creatorId: LLUUID = LLUUID.NULL,
    var ownerId: LLUUID = LLUUID.NULL,
    var lastOwnerId: LLUUID = LLUUID.NULL,
    var groupId: LLUUID = LLUUID.NULL,
    var baseMask: UInt = PermFlags.ALL,
    var ownerMask: UInt = PermFlags.ALL,
    var groupMask: UInt = PermFlags.NONE,
    var everyoneMask: UInt = PermFlags.NONE,
    var nextOwnerMask: UInt = PermFlags.COPY or PermFlags.TRANSFER,
    var isGroupOwned: Boolean = false
) {
    val isOwned: Boolean get() = ownerId != LLUUID.NULL || isGroupOwned

    fun allowOperationBy(op: UInt, agentId: LLUUID, groupId: LLUUID = LLUUID.NULL): Boolean {
        if (agentId == LLUUID.NULL) return true
        return when {
            isGroupOwned -> groupMask and op != 0u
            agentId == ownerId -> ownerMask and op != 0u
            groupId != LLUUID.NULL && groupId == this.groupId -> (ownerMask or groupMask) and op != 0u
            else -> everyoneMask and op != 0u
        }
    }

    fun allowModifyBy(agentId: LLUUID, groupId: LLUUID = LLUUID.NULL) =
        allowOperationBy(PermFlags.MODIFY, agentId, groupId)

    fun allowCopyBy(agentId: LLUUID, groupId: LLUUID = LLUUID.NULL) =
        allowOperationBy(PermFlags.COPY, agentId, groupId)

    fun allowMoveBy(agentId: LLUUID, groupId: LLUUID = LLUUID.NULL) =
        allowOperationBy(PermFlags.MOVE, agentId, groupId)

    fun allowTransferTo(agentId: LLUUID): Boolean = when {
        isGroupOwned -> allowOperationBy(PermFlags.TRANSFER, this.groupId, this.groupId)
        ownerId == agentId -> true
        else -> allowOperationBy(PermFlags.TRANSFER, ownerId)
    }

    fun allowExportBy(agentId: LLUUID): Boolean =
        agentId == ownerId && agentId == creatorId

    fun getSafeOwner(): LLUUID = when {
        !isGroupOwned -> ownerId
        groupId != LLUUID.NULL -> groupId
        else -> LLUUID.generate()
    }

    fun accumulate(other: Permissions) {
        baseMask = baseMask and other.baseMask
        ownerMask = ownerMask and other.ownerMask
        groupMask = groupMask and other.groupMask
        everyoneMask = everyoneMask and other.everyoneMask
        nextOwnerMask = nextOwnerMask and other.nextOwnerMask
        if (creatorId != other.creatorId) creatorId = LLUUID.NULL
        if (ownerId != other.ownerId) ownerId = LLUUID.NULL
        if (groupId != other.groupId) groupId = LLUUID.NULL
        if (lastOwnerId != other.lastOwnerId) lastOwnerId = LLUUID.NULL
    }

    fun toSD(): LLSD = LLSD.map(
        "creator_id"     to LLSD.uuid(creatorId),
        "owner_id"       to LLSD.uuid(ownerId),
        "last_owner_id"  to LLSD.uuid(lastOwnerId),
        "group_id"       to LLSD.uuid(groupId),
        "group_owned"    to LLSD.bool(isGroupOwned),
        "base_mask"      to LLSD.integer(baseMask.toInt()),
        "owner_mask"     to LLSD.integer(ownerMask.toInt()),
        "group_mask"     to LLSD.integer(groupMask.toInt()),
        "everyone_mask"  to LLSD.integer(everyoneMask.toInt()),
        "next_owner_mask" to LLSD.integer(nextOwnerMask.toInt())
    )

    companion object {
        val DEFAULT = Permissions(
            baseMask = PermFlags.ALL,
            ownerMask = PermFlags.ALL,
            everyoneMask = PermFlags.NONE,
            groupMask = PermFlags.NONE,
            nextOwnerMask = PermFlags.COPY or PermFlags.TRANSFER
        )

        fun fromSD(sd: LLSD): Permissions = Permissions(
            creatorId    = sd["creator_id"].asUUID(),
            ownerId      = sd["owner_id"].asUUID(),
            lastOwnerId  = sd["last_owner_id"].asUUID(),
            groupId      = sd["group_id"].asUUID(),
            isGroupOwned = sd["group_owned"].asBoolean(),
            baseMask     = sd["base_mask"].asInteger().toUInt(),
            ownerMask    = sd["owner_mask"].asInteger().toUInt(),
            groupMask    = sd["group_mask"].asInteger().toUInt(),
            everyoneMask = sd["everyone_mask"].asInteger().toUInt(),
            nextOwnerMask = sd["next_owner_mask"].asInteger().toUInt()
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Permissions) return false
        return creatorId == other.creatorId &&
            ownerId == other.ownerId &&
            lastOwnerId == other.lastOwnerId &&
            groupId == other.groupId &&
            baseMask == other.baseMask &&
            ownerMask == other.ownerMask &&
            groupMask == other.groupMask &&
            everyoneMask == other.everyoneMask &&
            nextOwnerMask == other.nextOwnerMask &&
            isGroupOwned == other.isGroupOwned
    }

    override fun hashCode(): Int = arrayOf(
        creatorId, ownerId, lastOwnerId, groupId,
        baseMask, ownerMask, groupMask, everyoneMask, nextOwnerMask, isGroupOwned
    ).contentHashCode()
}

enum class AggregatePermValue { EMPTY, NONE, SOME, ALL }

class AggregatePermissions {
    private val copy: AggregatePermValue get() = copyVal
    private val modify: AggregatePermValue get() = modifyVal
    private val transfer: AggregatePermValue get() = transferVal

    private var copyVal = AggregatePermValue.EMPTY
    private var modifyVal = AggregatePermValue.EMPTY
    private var transferVal = AggregatePermValue.EMPTY

    fun getValue(bit: UInt): AggregatePermValue = when (bit) {
        PermFlags.COPY     -> copyVal
        PermFlags.MODIFY   -> modifyVal
        PermFlags.TRANSFER -> transferVal
        else               -> AggregatePermValue.EMPTY
    }

    val isEmpty: Boolean get() =
        copyVal == AggregatePermValue.EMPTY &&
        modifyVal == AggregatePermValue.EMPTY &&
        transferVal == AggregatePermValue.EMPTY

    fun aggregate(mask: UInt) {
        copyVal    = aggregateBit(copyVal,    mask and PermFlags.COPY != 0u)
        modifyVal  = aggregateBit(modifyVal,  mask and PermFlags.MODIFY != 0u)
        transferVal = aggregateBit(transferVal, mask and PermFlags.TRANSFER != 0u)
    }

    fun aggregate(other: AggregatePermissions) {
        copyVal    = mergeValues(copyVal, other.copyVal)
        modifyVal  = mergeValues(modifyVal, other.modifyVal)
        transferVal = mergeValues(transferVal, other.transferVal)
    }

    private fun aggregateBit(current: AggregatePermValue, allowed: Boolean): AggregatePermValue = when (current) {
        AggregatePermValue.EMPTY -> if (allowed) AggregatePermValue.ALL else AggregatePermValue.NONE
        AggregatePermValue.ALL   -> if (allowed) AggregatePermValue.ALL else AggregatePermValue.SOME
        AggregatePermValue.NONE  -> if (allowed) AggregatePermValue.SOME else AggregatePermValue.NONE
        AggregatePermValue.SOME  -> AggregatePermValue.SOME
    }

    private fun mergeValues(a: AggregatePermValue, b: AggregatePermValue): AggregatePermValue = when {
        a == AggregatePermValue.EMPTY -> b
        b == AggregatePermValue.EMPTY -> a
        a == b -> a
        else -> AggregatePermValue.SOME
    }
}
