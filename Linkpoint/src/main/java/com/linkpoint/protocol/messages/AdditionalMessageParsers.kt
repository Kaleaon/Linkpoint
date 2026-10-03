package com.linkpoint.protocol.messages

import android.util.Log
import com.linkpoint.protocol.types.LLVector3
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID

/**
 * Additional message parsers for UDP protocol messages.
 * These supplement the core parsers in MessageParser.kt
 */
object AdditionalMessageParsers {
    private const val TAG = "AdditionalParsers"
    private val MESSAGE_BYTE_ORDER = ByteOrder.LITTLE_ENDIAN

    // ==================== UUID/NAME REPLY ====================
    
    data class UUIDNameReplyData(
        val entries: List<UUIDNameEntry>
    )
    
    data class UUIDNameEntry(
        val id: UUID,
        val firstName: String,
        val lastName: String
    )
    
    fun parseUUIDNameReply(data: ByteArray): UUIDNameReplyData? {
        return try {
            val buffer = ByteBuffer.wrap(data).order(MESSAGE_BYTE_ORDER)
            val entries = mutableListOf<UUIDNameEntry>()
            
            val count = buffer.get().toInt() and 0xFF
            for (i in 0 until count) {
                if (buffer.remaining() < 16) break
                val id = buffer.getUUID()
                val firstName = buffer.readString1()
                val lastName = buffer.readString1()
                entries.add(UUIDNameEntry(id, firstName, lastName))
            }
            
            UUIDNameReplyData(entries)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse UUIDNameReply", e)
            null
        }
    }
    
    // ==================== REGION INFO ====================
    
    data class RegionInfoData(
        val regionName: String,
        val estateOwnerID: UUID,
        val estateID: Int,
        val parentEstateID: Int,
        val billableFactor: Float,
        val pricePerMeter: Int,
        val redirectGridX: Int,
        val redirectGridY: Int,
        val regionFlags: Long,
        val simAccess: Int,
        val maxAgents: Int,
        val objectBonus: Float,
        val waterHeight: Float,
        val terrainRaiseLimit: Float,
        val terrainLowerLimit: Float,
        val sunHour: Float,
        val productSKU: String,
        val productName: String
    )
    
    fun parseRegionInfo(data: ByteArray): RegionInfoData? {
        return try {
            val buffer = ByteBuffer.wrap(data).order(MESSAGE_BYTE_ORDER)
            
            // AgentData block (skipped - we know who we are)
            buffer.getUUID() // AgentID
            buffer.getUUID() // SessionID
            
            // RegionInfo block
            val regionName = buffer.readString1()
            val estateOwnerID = buffer.getUUID()
            val estateID = buffer.int
            val parentEstateID = buffer.int
            val regionFlags = buffer.int.toLong() and 0xFFFFFFFFL
            val simAccess = buffer.get().toInt() and 0xFF
            val maxAgents = buffer.get().toInt() and 0xFF
            val billableFactor = buffer.float
            val objectBonus = buffer.float
            val waterHeight = buffer.float
            val terrainRaiseLimit = buffer.float
            val terrainLowerLimit = buffer.float
            val pricePerMeter = buffer.int
            val redirectGridX = buffer.int
            val redirectGridY = buffer.int
            val sunHour = buffer.float
            val productSKU = buffer.readString1()
            val productName = buffer.readString1()
            
            RegionInfoData(
                regionName, estateOwnerID, estateID, parentEstateID,
                billableFactor, pricePerMeter, redirectGridX, redirectGridY,
                regionFlags, simAccess, maxAgents, objectBonus, waterHeight,
                terrainRaiseLimit, terrainLowerLimit, sunHour, productSKU, productName
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse RegionInfo", e)
            null
        }
    }
    
    // ==================== SIM STATS ====================
    
    data class SimStatsData(
        val regionX: Int,
        val regionY: Int,
        val regionFlags: Long,
        val objectCapacity: Int,
        val stats: Map<Int, Float>
    )
    
    fun parseSimStats(data: ByteArray): SimStatsData? {
        return try {
            val buffer = ByteBuffer.wrap(data).order(MESSAGE_BYTE_ORDER)
            
            val regionX = buffer.int
            val regionY = buffer.int
            val regionFlags = buffer.int.toLong() and 0xFFFFFFFFL
            val objectCapacity = buffer.int
            
            val statCount = buffer.get().toInt() and 0xFF
            val stats = mutableMapOf<Int, Float>()
            
            for (i in 0 until statCount) {
                val statId = buffer.int
                val statValue = buffer.float
                stats[statId] = statValue
            }
            
            SimStatsData(regionX, regionY, regionFlags, objectCapacity, stats)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse SimStats", e)
            null
        }
    }
    
    // ==================== AVATAR APPEARANCE ====================
    
    data class AvatarAppearanceData(
        val senderID: UUID,
        val isTrial: Boolean,
        val textureEntries: ByteArray,
        val visualParams: ByteArray,
        val objectData: ByteArray
    )
    
    fun parseAvatarAppearance(data: ByteArray): AvatarAppearanceData? {
        return try {
            val buffer = ByteBuffer.wrap(data).order(MESSAGE_BYTE_ORDER)
            
            val senderID = buffer.getUUID()
            val isTrial = buffer.get() != 0.toByte()
            
            val textureLen = buffer.short.toInt() and 0xFFFF
            val textureEntries = ByteArray(textureLen)
            buffer.get(textureEntries)
            
            val visualParamsLen = buffer.get().toInt() and 0xFF
            val visualParams = ByteArray(visualParamsLen)
            buffer.get(visualParams)
            
            val objectDataLen = buffer.short.toInt() and 0xFFFF
            val objectData = ByteArray(objectDataLen)
            buffer.get(objectData)
            
            AvatarAppearanceData(senderID, isTrial, textureEntries, visualParams, objectData)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse AvatarAppearance", e)
            null
        }
    }
    
    // ==================== AVATAR PROPERTIES REPLY ====================
    
    data class AvatarPropertiesReplyData(
        val agentID: UUID,
        val avatarID: UUID,
        val imageID: UUID,
        val flImageID: UUID,
        val partnerID: UUID,
        val aboutText: String,
        val flAboutText: String,
        val bornOn: String,
        val profileURL: String,
        val charterMember: ByteArray,
        val flags: Int
    )
    
    fun parseAvatarPropertiesReply(data: ByteArray): AvatarPropertiesReplyData? {
        return try {
            val buffer = ByteBuffer.wrap(data).order(MESSAGE_BYTE_ORDER)
            
            val agentID = buffer.getUUID()
            val avatarID = buffer.getUUID()
            val imageID = buffer.getUUID()
            val flImageID = buffer.getUUID()
            val partnerID = buffer.getUUID()
            val aboutText = buffer.readString2()
            val flAboutText = buffer.readString1()
            val bornOn = buffer.readString1()
            val profileURL = buffer.readString1()
            val charterLen = buffer.get().toInt() and 0xFF
            val charterMember = ByteArray(charterLen)
            buffer.get(charterMember)
            val flags = buffer.int
            
            AvatarPropertiesReplyData(
                agentID, avatarID, imageID, flImageID, partnerID,
                aboutText, flAboutText, bornOn, profileURL, charterMember, flags
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse AvatarPropertiesReply", e)
            null
        }
    }
    
    // ==================== GROUP PROFILE REPLY ====================
    
    data class GroupProfileReplyData(
        val groupID: UUID,
        val name: String,
        val charter: String,
        val showInList: Boolean,
        val memberTitle: String,
        val powersMask: Long,
        val insigniaID: UUID,
        val founderID: UUID,
        val membershipFee: Int,
        val openEnrollment: Boolean,
        val money: Int,
        val groupMembershipCount: Int,
        val groupRolesCount: Int,
        val allowPublish: Boolean,
        val maturePublish: Boolean,
        val ownerRole: UUID
    )
    
    fun parseGroupProfileReply(data: ByteArray): GroupProfileReplyData? {
        return try {
            val buffer = ByteBuffer.wrap(data).order(MESSAGE_BYTE_ORDER)
            
            // AgentData
            buffer.getUUID() // AgentID
            
            // GroupData
            val groupID = buffer.getUUID()
            val name = buffer.readString1()
            val charter = buffer.readString2()
            val showInList = buffer.get() != 0.toByte()
            val memberTitle = buffer.readString1()
            val powersMask = buffer.long
            val insigniaID = buffer.getUUID()
            val founderID = buffer.getUUID()
            val membershipFee = buffer.int
            val openEnrollment = buffer.get() != 0.toByte()
            val money = buffer.int
            val groupMembershipCount = buffer.int
            val groupRolesCount = buffer.int
            val allowPublish = buffer.get() != 0.toByte()
            val maturePublish = buffer.get() != 0.toByte()
            val ownerRole = buffer.getUUID()
            
            GroupProfileReplyData(
                groupID, name, charter, showInList, memberTitle, powersMask,
                insigniaID, founderID, membershipFee, openEnrollment, money,
                groupMembershipCount, groupRolesCount, allowPublish, maturePublish, ownerRole
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse GroupProfileReply", e)
            null
        }
    }
    
    // ==================== MAP BLOCK REPLY ====================
    
    data class MapBlockReplyData(
        val agentID: UUID,
        val flags: Int,
        val blocks: List<MapBlockData>
    )
    
    data class MapBlockData(
        val x: Int,
        val y: Int,
        val name: String,
        val access: Int,
        val regionFlags: Long,
        val waterHeight: Int,
        val agents: Int,
        val mapImageID: UUID
    )
    
    fun parseMapBlockReply(data: ByteArray): MapBlockReplyData? {
        return try {
            val buffer = ByteBuffer.wrap(data).order(MESSAGE_BYTE_ORDER)
            
            val agentID = buffer.getUUID()
            val flags = buffer.int
            
            val blockCount = buffer.get().toInt() and 0xFF
            val blocks = mutableListOf<MapBlockData>()
            
            for (i in 0 until blockCount) {
                val x = buffer.short.toInt() and 0xFFFF
                val y = buffer.short.toInt() and 0xFFFF
                val name = buffer.readString1()
                val access = buffer.get().toInt() and 0xFF
                val regionFlags = buffer.int.toLong() and 0xFFFFFFFFL
                val waterHeight = buffer.get().toInt() and 0xFF
                val agents = buffer.get().toInt() and 0xFF
                val mapImageID = buffer.getUUID()
                
                blocks.add(MapBlockData(x, y, name, access, regionFlags, waterHeight, agents, mapImageID))
            }
            
            MapBlockReplyData(agentID, flags, blocks)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse MapBlockReply", e)
            null
        }
    }
    
    // ==================== DIR PLACES REPLY ====================
    
    data class DirPlacesReplyData(
        val agentID: UUID,
        val queryID: UUID,
        val places: List<DirPlaceData>
    )
    
    data class DirPlaceData(
        val parcelID: UUID,
        val name: String,
        val forSale: Boolean,
        val auction: Boolean,
        val dwell: Float
    )
    
    fun parseDirPlacesReply(data: ByteArray): DirPlacesReplyData? {
        return try {
            val buffer = ByteBuffer.wrap(data).order(MESSAGE_BYTE_ORDER)
            
            val agentID = buffer.getUUID()
            val queryID = buffer.getUUID()
            
            val placeCount = buffer.get().toInt() and 0xFF
            val places = mutableListOf<DirPlaceData>()
            
            for (i in 0 until placeCount) {
                val parcelID = buffer.getUUID()
                val name = buffer.readString1()
                val forSale = buffer.get() != 0.toByte()
                val auction = buffer.get() != 0.toByte()
                val dwell = buffer.float
                places.add(DirPlaceData(parcelID, name, forSale, auction, dwell))
            }
            
            DirPlacesReplyData(agentID, queryID, places)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse DirPlacesReply", e)
            null
        }
    }
    
    // ==================== PARCEL INFO REPLY ====================
    
    data class ParcelInfoReplyData(
        val parcelID: UUID,
        val ownerID: UUID,
        val name: String,
        val description: String,
        val actualArea: Int,
        val billableArea: Int,
        val flags: Long,
        val globalX: Float,
        val globalY: Float,
        val globalZ: Float,
        val simName: String,
        val snapshotID: UUID,
        val dwell: Float,
        val salePrice: Int,
        val auctionID: Int
    )
    
    fun parseParcelInfoReply(data: ByteArray): ParcelInfoReplyData? {
        return try {
            val buffer = ByteBuffer.wrap(data).order(MESSAGE_BYTE_ORDER)
            
            // AgentData
            buffer.getUUID() // AgentID
            
            // Data block
            val parcelID = buffer.getUUID()
            val ownerID = buffer.getUUID()
            val name = buffer.readString1()
            val description = buffer.readString1()
            val actualArea = buffer.int
            val billableArea = buffer.int
            val flags = buffer.get().toLong() and 0xFFL
            val globalX = buffer.float
            val globalY = buffer.float
            val globalZ = buffer.float
            val simName = buffer.readString1()
            val snapshotID = buffer.getUUID()
            val dwell = buffer.float
            val salePrice = buffer.int
            val auctionID = buffer.int
            
            ParcelInfoReplyData(
                parcelID, ownerID, name, description, actualArea, billableArea,
                flags, globalX, globalY, globalZ, simName, snapshotID, dwell,
                salePrice, auctionID
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse ParcelInfoReply", e)
            null
        }
    }
    
    // ==================== VIEWER EFFECT ====================
    
    data class ViewerEffectData(
        val agentID: UUID,
        val sessionID: UUID,
        val effects: List<EffectData>
    )
    
    data class EffectData(
        val id: UUID,
        val agentID: UUID,
        val type: Int,
        val duration: Float,
        val color: ByteArray,
        val typeData: ByteArray
    )
    
    fun parseViewerEffect(data: ByteArray): ViewerEffectData? {
        return try {
            val buffer = ByteBuffer.wrap(data).order(MESSAGE_BYTE_ORDER)
            
            val agentID = buffer.getUUID()
            val sessionID = buffer.getUUID()
            
            val effectCount = buffer.get().toInt() and 0xFF
            val effects = mutableListOf<EffectData>()
            
            for (i in 0 until effectCount) {
                val id = buffer.getUUID()
                val effectAgentID = buffer.getUUID()
                val type = buffer.get().toInt() and 0xFF
                val duration = buffer.float
                val color = ByteArray(4)
                buffer.get(color)
                val typeDataLen = buffer.get().toInt() and 0xFF
                val typeData = ByteArray(typeDataLen)
                buffer.get(typeData)
                
                effects.add(EffectData(id, effectAgentID, type, duration, color, typeData))
            }
            
            ViewerEffectData(agentID, sessionID, effects)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse ViewerEffect", e)
            null
        }
    }
    
    // ==================== SOUND MESSAGES ====================
    
    data class AttachedSoundData(
        val soundID: UUID,
        val objectID: UUID,
        val ownerID: UUID,
        val gain: Float,
        val flags: Int
    )
    
    fun parseAttachedSound(data: ByteArray): AttachedSoundData? {
        return try {
            val buffer = ByteBuffer.wrap(data).order(MESSAGE_BYTE_ORDER)
            
            val soundID = buffer.getUUID()
            val objectID = buffer.getUUID()
            val ownerID = buffer.getUUID()
            val gain = buffer.float
            val flags = buffer.get().toInt() and 0xFF
            
            AttachedSoundData(soundID, objectID, ownerID, gain, flags)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse AttachedSound", e)
            null
        }
    }
    
    data class PreloadSoundData(
        val objectID: UUID,
        val ownerID: UUID,
        val soundID: UUID
    )
    
    fun parsePreloadSound(data: ByteArray): PreloadSoundData? {
        return try {
            val buffer = ByteBuffer.wrap(data).order(MESSAGE_BYTE_ORDER)
            
            val objectID = buffer.getUUID()
            val ownerID = buffer.getUUID()
            val soundID = buffer.getUUID()
            
            PreloadSoundData(objectID, ownerID, soundID)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse PreloadSound", e)
            null
        }
    }
    
    // ==================== TRANSFER MESSAGES ====================
    
    data class TransferInfoData(
        val transferID: UUID,
        val channelType: Int,
        val targetType: Int,
        val status: Int,
        val size: Int,
        val params: ByteArray
    )
    
    fun parseTransferInfo(data: ByteArray): TransferInfoData? {
        return try {
            val buffer = ByteBuffer.wrap(data).order(MESSAGE_BYTE_ORDER)
            
            val transferID = buffer.getUUID()
            val channelType = buffer.int
            val targetType = buffer.int
            val status = buffer.int
            val size = buffer.int
            val paramsLen = buffer.short.toInt() and 0xFFFF
            val params = ByteArray(paramsLen)
            buffer.get(params)
            
            TransferInfoData(transferID, channelType, targetType, status, size, params)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse TransferInfo", e)
            null
        }
    }
    
    data class TransferPacketData(
        val transferID: UUID,
        val channelType: Int,
        val packet: Int,
        val status: Int,
        val data: ByteArray
    )
    
    fun parseTransferPacket(data: ByteArray): TransferPacketData? {
        return try {
            val buffer = ByteBuffer.wrap(data).order(MESSAGE_BYTE_ORDER)
            
            val transferID = buffer.getUUID()
            val channelType = buffer.int
            val packet = buffer.int
            val status = buffer.int
            val dataLen = buffer.short.toInt() and 0xFFFF
            val packetData = ByteArray(dataLen)
            buffer.get(packetData)
            
            TransferPacketData(transferID, channelType, packet, status, packetData)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse TransferPacket", e)
            null
        }
    }
    
    // ==================== AVATAR SIT RESPONSE ====================
    
    data class AvatarSitResponseData(
        val sitObjectID: UUID,
        val autopilot: Boolean,
        val sitPosition: LLVector3,
        val sitRotation: ByteArray,
        val cameraEyeOffset: LLVector3,
        val cameraAtOffset: LLVector3,
        val forceMouselook: Boolean
    )
    
    fun parseAvatarSitResponse(data: ByteArray): AvatarSitResponseData? {
        return try {
            val buffer = ByteBuffer.wrap(data).order(MESSAGE_BYTE_ORDER)
            
            val sitObjectID = buffer.getUUID()
            val autopilot = buffer.get() != 0.toByte()
            
            val sitPos = ByteArray(12)
            buffer.get(sitPos)
            val sitPosition = LLVector3.fromBytes(sitPos)
            
            val sitRotation = ByteArray(12)
            buffer.get(sitRotation)
            
            val eyeOffset = ByteArray(12)
            buffer.get(eyeOffset)
            val cameraEyeOffset = LLVector3.fromBytes(eyeOffset)
            
            val atOffset = ByteArray(12)
            buffer.get(atOffset)
            val cameraAtOffset = LLVector3.fromBytes(atOffset)
            
            val forceMouselook = buffer.get() != 0.toByte()
            
            AvatarSitResponseData(
                sitObjectID, autopilot, sitPosition, sitRotation,
                cameraEyeOffset, cameraAtOffset, forceMouselook
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse AvatarSitResponse", e)
            null
        }
    }
    
    // ==================== MEAN COLLISION ALERT ====================
    
    data class MeanCollisionAlertData(
        val collisions: List<CollisionData>
    )
    
    data class CollisionData(
        val perp: UUID,
        val time: Int,
        val mag: Float,
        val type: Int
    )
    
    fun parseMeanCollisionAlert(data: ByteArray): MeanCollisionAlertData? {
        return try {
            val buffer = ByteBuffer.wrap(data).order(MESSAGE_BYTE_ORDER)
            
            val count = buffer.get().toInt() and 0xFF
            val collisions = mutableListOf<CollisionData>()
            
            for (i in 0 until count) {
                val perp = buffer.getUUID()
                val time = buffer.int
                val mag = buffer.float
                val type = buffer.get().toInt() and 0xFF
                collisions.add(CollisionData(perp, time, mag, type))
            }
            
            MeanCollisionAlertData(collisions)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse MeanCollisionAlert", e)
            null
        }
    }
    
    // ==================== FRIENDSHIP MESSAGES ====================
    
    data class AcceptFriendshipData(
        val agentID: UUID,
        val transactionID: UUID,
        val folderData: List<UUID>
    )
    
    fun parseAcceptFriendship(data: ByteArray): AcceptFriendshipData? {
        return try {
            val buffer = ByteBuffer.wrap(data).order(MESSAGE_BYTE_ORDER)
            
            val agentID = buffer.getUUID()
            buffer.getUUID() // SessionID
            val transactionID = buffer.getUUID()
            
            val folderCount = buffer.get().toInt() and 0xFF
            val folderData = mutableListOf<UUID>()
            for (i in 0 until folderCount) {
                folderData.add(buffer.getUUID())
            }
            
            AcceptFriendshipData(agentID, transactionID, folderData)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse AcceptFriendship", e)
            null
        }
    }
    
    data class DeclineFriendshipData(
        val agentID: UUID,
        val transactionID: UUID
    )
    
    fun parseDeclineFriendship(data: ByteArray): DeclineFriendshipData? {
        return try {
            val buffer = ByteBuffer.wrap(data).order(MESSAGE_BYTE_ORDER)
            
            val agentID = buffer.getUUID()
            buffer.getUUID() // SessionID
            val transactionID = buffer.getUUID()
            
            DeclineFriendshipData(agentID, transactionID)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse DeclineFriendship", e)
            null
        }
    }
    
    data class FormFriendshipData(
        val fromAgentID: UUID,
        val toAgentID: UUID
    )
    
    fun parseFormFriendship(data: ByteArray): FormFriendshipData? {
        return try {
            val buffer = ByteBuffer.wrap(data).order(MESSAGE_BYTE_ORDER)
            
            val fromAgentID = buffer.getUUID()
            val toAgentID = buffer.getUUID()
            
            FormFriendshipData(fromAgentID, toAgentID)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse FormFriendship", e)
            null
        }
    }
    
    // ==================== INVENTORY MESSAGES ====================
    
    data class InventoryDescendentsData(
        val agentID: UUID,
        val folderID: UUID,
        val ownerID: UUID,
        val version: Int,
        val descendents: Int,
        val folders: List<InventoryFolderData>,
        val items: List<InventoryItemData>
    )
    
    data class InventoryFolderData(
        val folderID: UUID,
        val parentID: UUID,
        val type: Int,
        val name: String
    )
    
    data class InventoryItemData(
        val itemID: UUID,
        val folderID: UUID,
        val creatorID: UUID,
        val ownerID: UUID,
        val groupID: UUID,
        val baseMask: Int,
        val ownerMask: Int,
        val groupMask: Int,
        val everyoneMask: Int,
        val nextOwnerMask: Int,
        val groupOwned: Boolean,
        val assetID: UUID,
        val type: Int,
        val invType: Int,
        val flags: Int,
        val saleType: Int,
        val salePrice: Int,
        val name: String,
        val description: String,
        val creationDate: Int,
        val crc: Int
    )
    
    fun parseInventoryDescendents(data: ByteArray): InventoryDescendentsData? {
        return try {
            val buffer = ByteBuffer.wrap(data).order(MESSAGE_BYTE_ORDER)
            
            // AgentData
            val agentID = buffer.getUUID()
            val folderID = buffer.getUUID()
            val ownerID = buffer.getUUID()
            val version = buffer.int
            val descendents = buffer.int
            
            // FolderData
            val folderCount = buffer.get().toInt() and 0xFF
            val folders = mutableListOf<InventoryFolderData>()
            for (i in 0 until folderCount) {
                val fid = buffer.getUUID()
                val parentID = buffer.getUUID()
                val type = buffer.get().toInt()
                val name = buffer.readString1()
                folders.add(InventoryFolderData(fid, parentID, type, name))
            }
            
            // ItemData
            val itemCount = buffer.get().toInt() and 0xFF
            val items = mutableListOf<InventoryItemData>()
            for (i in 0 until itemCount) {
                val itemID = buffer.getUUID()
                val itemFolderID = buffer.getUUID()
                val creatorID = buffer.getUUID()
                val itemOwnerID = buffer.getUUID()
                val groupID = buffer.getUUID()
                val baseMask = buffer.int
                val ownerMask = buffer.int
                val groupMask = buffer.int
                val everyoneMask = buffer.int
                val nextOwnerMask = buffer.int
                val groupOwned = buffer.get() != 0.toByte()
                val assetID = buffer.getUUID()
                val type = buffer.get().toInt()
                val invType = buffer.get().toInt()
                val flags = buffer.int
                val saleType = buffer.get().toInt() and 0xFF
                val salePrice = buffer.int
                val name = buffer.readString1()
                val description = buffer.readString1()
                val creationDate = buffer.int
                val crc = buffer.int
                
                items.add(InventoryItemData(
                    itemID, itemFolderID, creatorID, itemOwnerID, groupID,
                    baseMask, ownerMask, groupMask, everyoneMask, nextOwnerMask,
                    groupOwned, assetID, type, invType, flags, saleType, salePrice,
                    name, description, creationDate, crc
                ))
            }
            
            InventoryDescendentsData(agentID, folderID, ownerID, version, descendents, folders, items)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse InventoryDescendents", e)
            null
        }
    }
    
    // ==================== LOGOUT REPLY ====================
    
    data class LogoutReplyData(
        val agentID: UUID,
        val sessionID: UUID,
        val inventoryData: List<UUID>
    )
    
    fun parseLogoutReply(data: ByteArray): LogoutReplyData? {
        return try {
            val buffer = ByteBuffer.wrap(data).order(MESSAGE_BYTE_ORDER)
            
            val agentID = buffer.getUUID()
            val sessionID = buffer.getUUID()
            
            val count = buffer.get().toInt() and 0xFF
            val inventoryData = mutableListOf<UUID>()
            for (i in 0 until count) {
                inventoryData.add(buffer.getUUID())
            }
            
            LogoutReplyData(agentID, sessionID, inventoryData)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse LogoutReply", e)
            null
        }
    }
    
    // ==================== OBJECT PROPERTIES ====================
    
    data class ObjectPropertiesFamilyData(
        val requestFlags: Int,
        val objectID: UUID,
        val ownerID: UUID,
        val groupID: UUID,
        val baseMask: Int,
        val ownerMask: Int,
        val groupMask: Int,
        val everyoneMask: Int,
        val nextOwnerMask: Int,
        val ownershipCost: Int,
        val saleType: Int,
        val salePrice: Int,
        val category: Int,
        val lastOwnerID: UUID,
        val name: String,
        val description: String
    )
    
    fun parseObjectPropertiesFamily(data: ByteArray): ObjectPropertiesFamilyData? {
        return try {
            val buffer = ByteBuffer.wrap(data).order(MESSAGE_BYTE_ORDER)
            
            val requestFlags = buffer.int
            val objectID = buffer.getUUID()
            val ownerID = buffer.getUUID()
            val groupID = buffer.getUUID()
            val baseMask = buffer.int
            val ownerMask = buffer.int
            val groupMask = buffer.int
            val everyoneMask = buffer.int
            val nextOwnerMask = buffer.int
            val ownershipCost = buffer.int
            val saleType = buffer.get().toInt() and 0xFF
            val salePrice = buffer.int
            val category = buffer.int
            val lastOwnerID = buffer.getUUID()
            val name = buffer.readString1()
            val description = buffer.readString1()
            
            ObjectPropertiesFamilyData(
                requestFlags, objectID, ownerID, groupID, baseMask, ownerMask,
                groupMask, everyoneMask, nextOwnerMask, ownershipCost, saleType,
                salePrice, category, lastOwnerID, name, description
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse ObjectPropertiesFamily", e)
            null
        }
    }
    
    // ==================== AGENT WEARABLES UPDATE ====================
    
    data class AgentWearablesUpdateData(
        val agentID: UUID,
        val sessionID: UUID,
        val serialNum: Int,
        val wearables: List<WearableData>
    )
    
    data class WearableData(
        val itemID: UUID,
        val assetID: UUID,
        val wearableType: Int
    )
    
    fun parseAgentWearablesUpdate(data: ByteArray): AgentWearablesUpdateData? {
        return try {
            val buffer = ByteBuffer.wrap(data).order(MESSAGE_BYTE_ORDER)
            
            val agentID = buffer.getUUID()
            val sessionID = buffer.getUUID()
            val serialNum = buffer.int
            
            val count = buffer.get().toInt() and 0xFF
            val wearables = mutableListOf<WearableData>()
            for (i in 0 until count) {
                val itemID = buffer.getUUID()
                val assetID = buffer.getUUID()
                val wearableType = buffer.get().toInt() and 0xFF
                wearables.add(WearableData(itemID, assetID, wearableType))
            }
            
            AgentWearablesUpdateData(agentID, sessionID, serialNum, wearables)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse AgentWearablesUpdate", e)
            null
        }
    }
    
    // ==================== GENERIC/SYSTEM MESSAGES ====================
    
    data class GenericMessageData(
        val methodName: String,
        val invoice: UUID,
        val params: List<ByteArray>
    )
    
    fun parseGenericMessage(data: ByteArray): GenericMessageData? {
        return try {
            val buffer = ByteBuffer.wrap(data).order(MESSAGE_BYTE_ORDER)
            
            // AgentData
            buffer.getUUID() // TransactionID
            
            // MethodData
            val methodName = buffer.readString1()
            val invoice = buffer.getUUID()
            
            // ParamList
            val paramCount = buffer.get().toInt() and 0xFF
            val params = mutableListOf<ByteArray>()
            for (i in 0 until paramCount) {
                val paramLen = buffer.get().toInt() and 0xFF
                val param = ByteArray(paramLen)
                buffer.get(param)
                params.add(param)
            }
            
            GenericMessageData(methodName, invoice, params)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse GenericMessage", e)
            null
        }
    }
    
    data class SystemMessageData(
        val method: String,
        val invoice: UUID,
        val digest: ByteArray,
        val params: List<ByteArray>
    )
    
    fun parseSystemMessage(data: ByteArray): SystemMessageData? {
        return try {
            val buffer = ByteBuffer.wrap(data).order(MESSAGE_BYTE_ORDER)
            
            val method = buffer.readString1()
            val invoice = buffer.getUUID()
            val digest = ByteArray(32)
            buffer.get(digest)
            
            val paramCount = buffer.get().toInt() and 0xFF
            val params = mutableListOf<ByteArray>()
            for (i in 0 until paramCount) {
                val paramLen = buffer.short.toInt() and 0xFFFF
                val param = ByteArray(paramLen)
                buffer.get(param)
                params.add(param)
            }
            
            SystemMessageData(method, invoice, digest, params)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse SystemMessage", e)
            null
        }
    }
    
    data class ErrorMessageData(
        val errorCode: Int,
        val errorMessage: String
    )
    
    fun parseErrorMessage(data: ByteArray): ErrorMessageData? {
        return try {
            val buffer = ByteBuffer.wrap(data).order(MESSAGE_BYTE_ORDER)
            
            val errorCode = buffer.int
            val errorMessage = buffer.readString2()
            
            ErrorMessageData(errorCode, errorMessage)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse ErrorMessage", e)
            null
        }
    }
    
    // ==================== USER INFO REPLY ====================
    
    data class UserInfoReplyData(
        val agentID: UUID,
        val imViaEmail: Boolean,
        val directoryVisibility: String,
        val email: String
    )
    
    fun parseUserInfoReply(data: ByteArray): UserInfoReplyData? {
        return try {
            val buffer = ByteBuffer.wrap(data).order(MESSAGE_BYTE_ORDER)
            
            val agentID = buffer.getUUID()
            val imViaEmail = buffer.get() != 0.toByte()
            val directoryVisibility = buffer.readString1()
            val email = buffer.readString2()
            
            UserInfoReplyData(agentID, imViaEmail, directoryVisibility, email)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse UserInfoReply", e)
            null
        }
    }
    
    // ==================== EXTENSION FUNCTIONS FOR BYTEBUFFER ====================
    
    private fun ByteBuffer.getUUID(): UUID {
        val bytes = ByteArray(16)
        this.get(bytes)
        val bb = ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN)
        return UUID(bb.long, bb.long)
    }
    
    private fun ByteBuffer.readString1(): String {
        val len = this.get().toInt() and 0xFF
        if (len == 0) return ""
        val bytes = ByteArray(len)
        this.get(bytes)
        return String(bytes, Charsets.UTF_8).trimEnd('\u0000')
    }
    
    private fun ByteBuffer.readString2(): String {
        val len = this.short.toInt() and 0xFFFF
        if (len == 0) return ""
        val bytes = ByteArray(len)
        this.get(bytes)
        return String(bytes, Charsets.UTF_8).trimEnd('\u0000')
    }
}
