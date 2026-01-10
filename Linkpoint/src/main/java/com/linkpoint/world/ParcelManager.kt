package com.linkpoint.world

import android.util.Log
import com.linkpoint.protocol.messages.UDPConnection
import com.linkpoint.protocol.types.LLVector3
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Manages parcel/land information
 */
class ParcelManager(
    private val udpConnection: UDPConnection
) {
    companion object {
        private const val TAG = "ParcelManager"
        
        // Parcel flags
        const val FLAG_ALLOW_FLY = 0x00000001L
        const val FLAG_ALLOW_SCRIPTS = 0x00000002L
        const val FLAG_ALLOW_LANDMARK = 0x00000008L
        const val FLAG_ALLOW_TERRAFORM = 0x00000010L
        const val FLAG_ALLOW_DAMAGE = 0x00000020L
        const val FLAG_CREATE_OBJECTS = 0x00000040L
        const val FLAG_FOR_SALE = 0x00000080L
        const val FLAG_USE_ACCESS_GROUP = 0x00000100L
        const val FLAG_USE_ACCESS_LIST = 0x00000200L
        const val FLAG_USE_BAN_LIST = 0x00000400L
        const val FLAG_USE_LAND_PASS_LIST = 0x00000800L
        const val FLAG_SHOW_DIRECTORY = 0x00001000L
        const val FLAG_ALLOW_DEED_TO_GROUP = 0x00002000L
        const val FLAG_CONTRIBUTE_WITH_DEED = 0x00004000L
        const val FLAG_SOUND_LOCAL = 0x00008000L
        const val FLAG_SELL_PARCEL_OBJECTS = 0x00010000L
        const val FLAG_ALLOW_PUBLISH = 0x00020000L
        const val FLAG_MATURE_PUBLISH = 0x00040000L
        const val FLAG_URL_WEB_PAGE = 0x00080000L
        const val FLAG_URL_RAW_HTML = 0x00100000L
        const val FLAG_RESTRICT_PUSHOBJECT = 0x00200000L
        const val FLAG_DENY_ANONYMOUS = 0x00400000L
        const val FLAG_ALLOW_GROUP_SCRIPTS = 0x02000000L
        const val FLAG_CREATE_GROUP_OBJECTS = 0x04000000L
        const val FLAG_ALLOW_ALL_OBJECT_ENTRY = 0x08000000L
        const val FLAG_ALLOW_GROUP_OBJECT_ENTRY = 0x10000000L
        const val FLAG_ALLOW_VOICE_CHAT = 0x20000000L
        const val FLAG_USE_ESTATE_VOICE_CHAN = 0x40000000L
        const val FLAG_DENY_AGEUNVERIFIED = 0x80000000L
        
        // Landing types
        const val LANDING_ANYWHERE = 0
        const val LANDING_POINT = 1
        const val LANDING_NONE = 2
    }
    
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    
    // Cached parcels
    private val parcels = ConcurrentHashMap<Int, ParcelInfo>()
    
    // Current parcel
    private val _currentParcel = MutableStateFlow<ParcelInfo?>(null)
    val currentParcel: StateFlow<ParcelInfo?> = _currentParcel
    
    // Parcel overlay data (bitmap of parcel boundaries)
    private var parcelOverlay: ByteArray? = null
    
    /**
     * Handle ParcelOverlay message
     */
    fun handleParcelOverlay(sequence: Int, data: ByteArray) {
        // Parcel overlay is sent in chunks
        if (parcelOverlay == null) {
            parcelOverlay = ByteArray(256 * 256 / 4) // 4 parcels per byte
        }
        
        val offset = sequence * 1024
        data.copyInto(parcelOverlay!!, offset, 0, minOf(data.size, 1024))
    }
    
    /**
     * Handle ParcelProperties message
     */
    fun handleParcelProperties(
        localId: Int,
        ownerId: UUID,
        groupId: UUID?,
        name: String,
        description: String,
        claimDate: Long,
        claimPrice: Int,
        rentPrice: Int,
        aabbMin: LLVector3,
        aabbMax: LLVector3,
        area: Int,
        actualArea: Int,
        simWideMaxPrims: Int,
        simWideTotal: Int,
        maxPrims: Int,
        totalPrims: Int,
        ownerPrims: Int,
        groupPrims: Int,
        otherPrims: Int,
        selectedPrims: Int,
        parcelPrimBonus: Float,
        cleanTime: Int,
        flags: Long,
        landingType: Int,
        musicUrl: String?,
        mediaUrl: String?,
        mediaId: UUID?,
        mediaAutoScale: Boolean,
        groupPrimeOverride: Boolean,
        category: Int,
        snapshotId: UUID?,
        userLocation: LLVector3,
        userLookAt: LLVector3,
        status: Int,
        passPrice: Int,
        passHours: Float
    ) {
        val parcel = ParcelInfo(
            localId = localId,
            ownerId = ownerId,
            groupId = groupId,
            name = name,
            description = description,
            claimDate = claimDate,
            claimPrice = claimPrice,
            rentPrice = rentPrice,
            aabbMin = aabbMin,
            aabbMax = aabbMax,
            area = area,
            actualArea = actualArea,
            simWideMaxPrims = simWideMaxPrims,
            simWideTotal = simWideTotal,
            maxPrims = maxPrims,
            totalPrims = totalPrims,
            ownerPrims = ownerPrims,
            groupPrims = groupPrims,
            otherPrims = otherPrims,
            selectedPrims = selectedPrims,
            parcelPrimBonus = parcelPrimBonus,
            cleanTime = cleanTime,
            flags = flags,
            landingType = landingType,
            musicUrl = musicUrl,
            mediaUrl = mediaUrl,
            mediaId = mediaId,
            mediaAutoScale = mediaAutoScale,
            category = category,
            snapshotId = snapshotId,
            userLocation = userLocation,
            userLookAt = userLookAt,
            status = status,
            passPrice = passPrice,
            passHours = passHours
        )
        
        parcels[localId] = parcel
    }
    
    /**
     * Request parcel info at position
     */
    fun requestParcelInfo(position: LLVector3) {
        scope.launch {
            // Would send ParcelInfoRequest
        }
    }
    
    /**
     * Set current parcel
     */
    fun setCurrentParcel(localId: Int) {
        _currentParcel.value = parcels[localId]
    }
    
    /**
     * Get parcel at position
     */
    fun getParcelAtPosition(x: Float, y: Float): ParcelInfo? {
        val localX = x.toInt().coerceIn(0, 255)
        val localY = y.toInt().coerceIn(0, 255)
        
        parcelOverlay?.let { overlay ->
            val index = localY * 64 + localX / 4
            if (index < overlay.size) {
                val byte = overlay[index].toInt() and 0xFF
                val shift = (localX % 4) * 2
                val parcelId = (byte shr shift) and 0x03
                return parcels[parcelId]
            }
        }
        
        return null
    }
    
    /**
     * Buy land
     */
    fun buyLand(localId: Int, forGroup: Boolean = false) {
        scope.launch {
            // Would send ParcelBuy message
        }
    }
    
    /**
     * Deed land to group
     */
    fun deedToGroup(localId: Int, groupId: UUID) {
        scope.launch {
            // Would send ParcelDeedToGroup message
        }
    }
    
    /**
     * Release/abandon land
     */
    fun releaseLand(localId: Int) {
        scope.launch {
            // Would send ParcelRelease message
        }
    }
    
    /**
     * Set parcel for sale
     */
    fun setForSale(localId: Int, price: Int, forAll: Boolean = true) {
        scope.launch {
            // Would send ParcelSetOtherCleanTime with sale info
        }
    }
    
    /**
     * Return objects from parcel
     */
    fun returnObjects(localId: Int, returnType: ReturnType) {
        scope.launch {
            // Would send ParcelReturnObjects message
        }
    }
    
    /**
     * Set parcel name
     */
    fun setParcelName(localId: Int, name: String) {
        parcels[localId]?.let { parcel ->
            parcels[localId] = parcel.copy(name = name)
        }
        scope.launch {
            // Would send ParcelPropertiesUpdate message
        }
    }
    
    /**
     * Set parcel description
     */
    fun setParcelDescription(localId: Int, description: String) {
        parcels[localId]?.let { parcel ->
            parcels[localId] = parcel.copy(description = description)
        }
        scope.launch {
            // Would send update
        }
    }
    
    /**
     * Set parcel flags
     */
    fun setParcelFlags(localId: Int, flags: Long) {
        parcels[localId]?.let { parcel ->
            parcels[localId] = parcel.copy(flags = flags)
        }
        scope.launch {
            // Would send update
        }
    }
    
    /**
     * Set music URL
     */
    fun setMusicUrl(localId: Int, url: String?) {
        parcels[localId]?.let { parcel ->
            parcels[localId] = parcel.copy(musicUrl = url)
        }
        scope.launch {
            // Would send update
        }
    }
    
    /**
     * Set media URL
     */
    fun setMediaUrl(localId: Int, url: String?) {
        parcels[localId]?.let { parcel ->
            parcels[localId] = parcel.copy(mediaUrl = url)
        }
        scope.launch {
            // Would send update
        }
    }
    
    /**
     * Add to access list
     */
    fun addToAccessList(localId: Int, agentId: UUID, hours: Float = 0f) {
        scope.launch {
            // Would send ParcelAccessListUpdate
        }
    }
    
    /**
     * Remove from access list
     */
    fun removeFromAccessList(localId: Int, agentId: UUID) {
        scope.launch {
            // Would send update
        }
    }
    
    /**
     * Add to ban list
     */
    fun addToBanList(localId: Int, agentId: UUID) {
        scope.launch {
            // Would send ParcelAccessListUpdate
        }
    }
    
    /**
     * Remove from ban list
     */
    fun removeFromBanList(localId: Int, agentId: UUID) {
        scope.launch {
            // Would send update
        }
    }
    
    fun shutdown() {
        scope.cancel()
    }
}

data class ParcelInfo(
    val localId: Int,
    val ownerId: UUID,
    val groupId: UUID?,
    val name: String,
    val description: String,
    val claimDate: Long,
    val claimPrice: Int,
    val rentPrice: Int,
    val aabbMin: LLVector3,
    val aabbMax: LLVector3,
    val area: Int,
    val actualArea: Int,
    val simWideMaxPrims: Int,
    val simWideTotal: Int,
    val maxPrims: Int,
    val totalPrims: Int,
    val ownerPrims: Int,
    val groupPrims: Int,
    val otherPrims: Int,
    val selectedPrims: Int,
    val parcelPrimBonus: Float,
    val cleanTime: Int,
    val flags: Long,
    val landingType: Int,
    val musicUrl: String?,
    val mediaUrl: String?,
    val mediaId: UUID?,
    val mediaAutoScale: Boolean,
    val category: Int,
    val snapshotId: UUID?,
    val userLocation: LLVector3,
    val userLookAt: LLVector3,
    val status: Int,
    val passPrice: Int,
    val passHours: Float
) {
    // Helper properties
    val allowFly: Boolean get() = (flags and ParcelManager.FLAG_ALLOW_FLY) != 0L
    val allowScripts: Boolean get() = (flags and ParcelManager.FLAG_ALLOW_SCRIPTS) != 0L
    val allowBuild: Boolean get() = (flags and ParcelManager.FLAG_CREATE_OBJECTS) != 0L
    val allowDamage: Boolean get() = (flags and ParcelManager.FLAG_ALLOW_DAMAGE) != 0L
    val forSale: Boolean get() = (flags and ParcelManager.FLAG_FOR_SALE) != 0L
    val allowVoice: Boolean get() = (flags and ParcelManager.FLAG_ALLOW_VOICE_CHAT) != 0L
    val restrictPush: Boolean get() = (flags and ParcelManager.FLAG_RESTRICT_PUSHOBJECT) != 0L
    
    val sizeString: String get() = "$area sqm"
    val primUsage: String get() = "$totalPrims / $maxPrims"
}

enum class ReturnType {
    OWNER, GROUP, OTHER, LIST, SELL
}
