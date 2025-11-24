package com.lumiyaviewer.lumiya.slproto.users

import com.lumiyaviewer.lumiya.slproto.llsd.LLSDException
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode
import com.lumiyaviewer.lumiya.utils.UUIDPool
import java.io.Serializable
import java.util.UUID

class ParcelData(lLSDNode: LLSDNode) : Serializable {
    private var area: Int = 0
    private var description: String = ""
    private var isGroupOwned: Boolean = false
    private var mediaURL: String = ""
    private var name: String = ""
    private var ownerID: UUID = UUIDPool.ZeroUUID
    private val parcelBitmap: BooleanArray = BooleanArray(4096)
    private var parcelID: Int = 0
    private var snapshotUUID: UUID = UUIDPool.ZeroUUID

    init {
        this.parcelID = lLSDNode.byKey("LocalID")?.asInt() ?: 0
        this.name = lLSDNode.byKey("Name")?.asString() ?: ""
        this.description = lLSDNode.byKey("Desc")?.asString() ?: ""
        this.mediaURL = lLSDNode.byKey("MusicURL")?.asString() ?: ""
        
        var asUUID = lLSDNode.byKey("SnapshotID")?.asUUID()
        // Handle ZeroUUID case if needed, but let's assume nullable/ZeroUUID handling is consistent
        this.snapshotUUID = if (asUUID != null && asUUID != UUIDPool.ZeroUUID) asUUID else UUIDPool.ZeroUUID
        
        this.ownerID = if (lLSDNode.keyExists("OwnerID")) lLSDNode.byKey("OwnerID")?.asUUID() ?: UUIDPool.ZeroUUID else UUIDPool.ZeroUUID
        this.isGroupOwned = if (lLSDNode.keyExists("IsGroupOwned")) lLSDNode.byKey("IsGroupOwned")?.asBoolean() ?: false else false
        this.area = if (lLSDNode.keyExists("Area")) lLSDNode.byKey("Area")?.asInt() ?: 0 else 0
        
        val asBinary = lLSDNode.byKey("Bitmap")?.asBinary() ?: ByteArray(0)
        var i = 0
        while (i < asBinary.size && i < 512) {
            var b = asBinary[i].toInt()
            for (i2 in 0 until 8) {
                if ((b and 1) != 0) {
                    this.parcelBitmap[(i * 8) + i2] = true
                }
                b = b shr 1
            }
            i++
        }
    }

    fun getArea(): Int = area
    fun getDescription(): String = description
    fun getMediaURL(): String = mediaURL
    fun getName(): String = name
    fun getOwnerID(): UUID = ownerID
    fun getParcelBitmap(): BooleanArray = parcelBitmap
    fun getParcelID(): Int = parcelID
    fun getSnapshotUUID(): UUID = snapshotUUID
    fun isGroupOwned(): Boolean = isGroupOwned
}
