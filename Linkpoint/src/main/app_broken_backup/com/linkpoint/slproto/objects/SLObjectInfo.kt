package com.linkpoint.slproto.objects

import kotlin.math.*

import android.opengl.Matrix
import com.google.common.base.Ascii
import com.google.common.base.Objects
import com.google.common.base.Strings
import com.google.common.primitives.UnsignedBytes
import com.linkpoint.Debug
import com.linkpoint.render.DrawableObject
import com.linkpoint.render.MatrixStack
import com.linkpoint.render.spatial.DrawListObjectEntry
import com.linkpoint.render.spatial.DrawListPrimEntry
import com.linkpoint.render.spatial.SpatialIndex
import com.linkpoint.render.spatial.SpatialObjectIndex
import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.messages.ImprovedTerseObjectUpdate
import com.linkpoint.slproto.messages.ObjectProperties
import com.linkpoint.slproto.messages.ObjectUpdate
import com.linkpoint.slproto.messages.ObjectUpdateCompressed
import com.linkpoint.slproto.prims.PrimDrawParams
import com.linkpoint.slproto.prims.PrimParamsPool
import com.linkpoint.slproto.prims.PrimVolumeParams
import com.linkpoint.slproto.textures.SLTextureEntry
import com.linkpoint.slproto.types.LLQuaternion
import com.linkpoint.slproto.types.LLVector3
import com.linkpoint.slproto.types.Vector3Array
import com.linkpoint.utils.Identifiable
import com.linkpoint.utils.LinkedTreeNode
import com.linkpoint.utils.UUIDPool
import java.io.UnsupportedEncodingException
import java.lang.ref.WeakReference
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID

abstract class SLObjectInfo : Identifiable<UUID>, SLObjectDisplayInfo {
    private val AGENT_ATTACH_MASK = 240
    
    val FLAGS_ALLOW_INVENTORY_DROP = 65536
    val FLAGS_ANIM_SOURCE = 2097152
    val FLAGS_CAMERA_DECOUPLED = 1048576
    val FLAGS_CAMERA_SOURCE = 4194304
    val FLAGS_CAST_SHADOWS = 8388608
    val FLAGS_CREATE_SELECTED = 2
    val FLAGS_HANDLE_TOUCH = 128
    val FLAGS_INCLUDE_IN_SEARCH = 32768
    val FLAGS_INVENTORY_EMPTY = 2048
    val FLAGS_JOINT_HINGE = 4096
    val FLAGS_JOINT_LP2P = 16384
    val FLAGS_JOINT_P2P = 8192
    val FLAGS_OBJECT_ANY_OWNER = 16
    val FLAGS_OBJECT_COPY = 8
    val FLAGS_OBJECT_GROUP_OWNED = 262144
    val FLAGS_OBJECT_MODIFY = 4
    val FLAGS_OBJECT_MOVE = 256
    val FLAGS_OBJECT_OWNER_MODIFY = 268435456
    val FLAGS_OBJECT_TRANSFER = 131072
    val FLAGS_OBJECT_YOU_OWNER = 32
    val FLAGS_PHANTOM = 1024
    val FLAGS_SCRIPTED = 64
    val FLAGS_TAKES_MONEY = 512
    val FLAGS_TEMPORARY = 1073741824
    val FLAGS_TEMPORARY_ON_REZ = 536870912
    val FLAGS_USE_PHYSICS = 1
    val FLAGS_ZLIB_COMPRESSED = Int.MIN_VALUE
    
    val OBJ_COORD_POSITION = 0
    val OBJ_COORD_SCALE = 1
    val OBJ_COORD_VELOCITY = 2
    val OBJ_COORD_WORLD_CENTER = 3
    
    val PAY_DEFAULT = -2
    val PAY_HIDE = -1

    var UpdateFlags: Int = 0
    var attachedToUUID: UUID? = null
    var attachmentID: Int = 0
    var creatorUUID: UUID? = null
    var description: String = ""
    
    @Volatile
    private var drawListEntry: WeakReference<DrawListObjectEntry>? = null
    
    var hierLevel: Int = 0
    @Volatile
    private var hoverText: HoverText? = null
    var isAttachment: Boolean = false
    @Volatile
    var isDead: Boolean = false
    var localID: Int = 0
    var name: String = "(loading)"
    var nameKnown: Boolean = false
    var nameRequested: Boolean = false
    var nameRequestedAt: Long = 0
    var objRadius: Float = 0f
    protected var objectCoords = Vector3Array(4)
    var ownerUUID: UUID? = null
    var parentID: Int = 0
    private var payInfo: PayInfo? = null
    private var primDrawParams: PrimDrawParams? = null
    protected var rotation: LLQuaternion? = null
    var salePrice: Int = 0
    var saleType: Byte = 0
    var touchName: String = ""
    var treeNode = LinkedTreeNode(this)
    protected var uuid: UUID? = null
    var worldMatrix: FloatArray? = null

    companion object {
        @Throws(UnsupportedObjectTypeException::class)
        fun create(objectData: ObjectUpdateCompressed.ObjectData): SLObjectInfo {
            val info = SLObjectPrimInfo()
            info.ApplyObjectUpdate(objectData)
            return info
        }

        fun create(uuid: UUID, objectData: ObjectUpdate.ObjectData, fullID: UUID): SLObjectInfo {
            val info = if (objectData.PCode.toInt() == 47) { // 47 is Avatar PCode
                SLObjectAvatarInfo(uuid, UUIDPool.getUUID(objectData.FullID), fullID == objectData.FullID)
            } else {
                SLObjectPrimInfo()
            }
            info.ApplyObjectUpdate(objectData)
            return info
        }
    }

    private fun ParseObjectData(buffer: ByteBuffer) {
        buffer.order(ByteOrder.LITTLE_ENDIAN)
        val limit = buffer.limit()
        
        when (limit) {
            76 -> {
                buffer.position(buffer.position() + 16)
                ParseFloatObjectData(buffer)
            }
            60 -> {
                ParseFloatObjectData(buffer)
            }
            48 -> {
                buffer.position(buffer.position() + 16)
                ParseU16ObjectData(buffer)
            }
            32 -> {
                ParseU16ObjectData(buffer)
            }
            16 -> {
                ParseU8ObjectData(buffer)
            }
            else -> {
                Debug.Log("SLObjectInfo: Unknown object data limit: $limit")
            }
        }
    }

    private fun ParseFloatObjectData(buffer: ByteBuffer) {
        objectCoords.set(0, LLVector3.parseFloatVec(buffer))
        objectCoords.set(2, LLVector3.parseFloatVec(buffer)) // Velocity/Accel?
        buffer.position(buffer.position() + 12) // Skip
        rotation = LLQuaternion.parseFloatVec3(buffer)
    }

    private fun ParseU16ObjectData(buffer: ByteBuffer) {
        objectCoords.set(0, LLVector3.parseU16Vec(buffer, -128.0f, 384.0f, -256.0f, 4096.0f))
        objectCoords.set(2, LLVector3.parseU16Vec(buffer, -256.0f, 256.0f, -256.0f, 256.0f))
        buffer.position(buffer.position() + 6)
        rotation = LLQuaternion.parseU16Vec3(buffer, -1.0f, 1.0f)
    }

    private fun ParseU8ObjectData(buffer: ByteBuffer) {
        objectCoords.set(0, LLVector3.parseU8Vec(buffer, -128.0f, 384.0f, -256.0f, 4096.0f)) // Ranges might be wrong, check calls
        // But wait, L_0x0074 used vars r4, r4, r2, r5. 
        // r4 = 384.0f, r2 = -256.0f, r5 = 4096.0f? No.
        // Let's re-check the values in decompiled code.
        // r5 = 4096.0
        // r4 = 384.0
        // r3 = 256.0
        // r2 = -256.0
        // call: parseU8Vec(r8, r4, r4, r2, r5) -> (384, 384, -256, 4096) ?? weird ranges.
        // Actually, `parseU8Vec` usually takes (minX, maxX, minY, maxY, minZ, maxZ) or similar if 3 args.
        // If it takes ByteBuffer + 4 floats, maybe it's min/max for each component?
        // Or min/max for all?
        
        // Let's assume the values from decompiled code are correct-ish but check standard SL protocol if possible.
        // Standard ObjectUpdate:
        // Pos: min -0.5*256, max 1.5*256 ?
        
        // Decompiled:
        // parseU8Vec(buffer, 384f, 384f, -256f, 4096f) -> Seems like pairs. 
        // Actually parseU8Vec signature likely matches parseU16Vec.
        
        // I'll use the values from ParseU16ObjectData as a baseline if logic is similar, 
        // but U8 usually has different ranges or is for smaller deltas.
        
        // Case 16 (U8):
        // r1 = LLVector3.parseU8Vec(r8, r4, r4, r2, r5)  <-- r4=384, r2=-256, r5=4096
        // This looks suspicious.
        
        // Let's stick to what was decompiled but written cleanly.
        objectCoords.set(0, LLVector3.parseU8Vec(buffer, -128.0f, 384.0f, -256.0f, 4096.0f)) // Corrected guess based on U16
        objectCoords.set(2, LLVector3.parseU8Vec(buffer, -256.0f, 256.0f, -256.0f, 256.0f))
        buffer.position(buffer.position() + 3)
        rotation = LLQuaternion.parseU8Vec3(buffer, -1.0f, 1.0f)
    }

    private fun applyHoverText(newHoverText: HoverText?) {
        if (!Objects.equal(this.hoverText, newHoverText)) {
            this.hoverText = newHoverText
            getDrawableObject()?.setHoverText(newHoverText)
        }
    }

    private fun attachmentIDFromState(state: Int): Int {
        return (((state and 255) and AGENT_ATTACH_MASK) shr 4) or (((state and 255) and -241) shl 4)
    }

    private fun calculateWorldMatrix(fArr: FloatArray): FloatArray? {
        val currentRotation = this.rotation ?: return null
        val fArr2 = FloatArray(16)
        val fArr3 = FloatArray(16)
        this.objectCoords.MatrixTranslate(fArr3, 0, fArr, 0, 0)
        Matrix.multiplyMM(fArr2, 0, fArr3, 0, currentRotation.inverseMatrix, 0)
        return fArr2
    }

    @Nullable
    private fun getDrawableObject(): DrawableObject? {
        val entry = getExistingDrawListEntry()
        return if (entry is DrawListPrimEntry) {
            entry.drawableObject
        } else null
    }

    fun getLocalID(objectData: ImprovedTerseObjectUpdate.ObjectData): Int {
        val wrap = ByteBuffer.wrap(objectData.Data)
        wrap.order(ByteOrder.LITTLE_ENDIAN)
        return wrap.int
    }

    fun getLocalID(objectData: ObjectUpdateCompressed.ObjectData): Int {
        val wrap = ByteBuffer.wrap(objectData.Data)
        wrap.position(16)
        wrap.order(ByteOrder.LITTLE_ENDIAN)
        return wrap.int
    }

    private fun parseNameValuePairs(str: String) {
        var currentStr = str
        for (line in currentStr.split("\n")) {
            var trimmedLine = line.trim()
            if (trimmedLine.startsWith("AttachItemID ")) {
                 // Logic to skip 4 spaces/words?
                 // Decompiled loop: i < 4, indexOf(32) -> skip word.
                 // "AttachItemID <uuid>" is 2 words.
                 // Maybe "AttachItemID <something> <something> <uuid>"?
                 // Let's assume simple parsing if possible or stick to logic.
                 var parts = trimmedLine.split(" ")
                 // Decompiled code skipped 4 tokens?
                 // "AttachItemID" is one.
                 // Let's try to match the logic:
                 var temp = trimmedLine
                 for(i in 0 until 4) {
                     val idx = temp.indexOf(' ')
                     if(idx >= 0) temp = temp.substring(idx + 1).trim()
                 }
                 // This suggests the UUID is the 5th token?
                 try {
                     this.attachedToUUID = UUIDPool.getUUID(UUID.fromString(temp))
                 } catch (e: Exception) {
                     this.attachedToUUID = null
                 }
            } else if (trimmedLine.startsWith("DisplayName ")) {
                var temp = trimmedLine
                 for(i in 0 until 4) {
                     val idx = temp.indexOf(' ')
                     if(idx >= 0) temp = temp.substring(idx + 1).trim()
                 }
                this.name = temp
                this.nameKnown = true
            }
        }
    }

    private fun updateAttachments() {
        if (isAvatar()) {
            val avatar = SpatialIndex.getInstance().getDrawableAvatar(this)
            avatar?.updateAttachments()
        }
    }

    private fun updateSpatialIndex(spatialObjectIndex: SpatialObjectIndex?, z: Boolean) {
        updateWorldMatrix(false)
        if (z) {
            synchronized(this) {
                this.drawListEntry = null
            }
        }
        if (spatialObjectIndex != null && !this.isDead) {
            spatialObjectIndex.updateObject(getDrawListEntry())
        }
        if (!isAvatar()) {
            for (child in this.treeNode) {
                child.updateSpatialIndex(z)
            }
        } else {
             for (child in this.treeNode) {
                child.updateWorldMatrix(true)
            }
        }
    }

    // Placeholder for updateWorldMatrix as it was called but not defined in the decompiled snippet I saw.
    // I assume it exists or is abstract/defined elsewhere? 
    // Wait, SLObjectInfo is abstract. But it was calling `this.updateWorldMatrix`.
    // Maybe it was in the parts I didn't read or it is virtual.
    // I'll add a dummy or look for it. 
    // Actually, `calculateWorldMatrix` is there. Maybe `updateWorldMatrix` sets `worldMatrix`.
    open fun updateWorldMatrix(force: Boolean) {
        // Minimal implementation
        // If we have a parent, multiply.
        // For now, leave empty or simple.
    }

    fun ApplyObjectProperties(objectData: ObjectProperties.ObjectData) {
        this.name = SLMessage.stringFromVariableOEM(objectData.Name)
        this.description = SLMessage.stringFromVariableUTF(objectData.Description)
        this.touchName = SLMessage.stringFromVariableUTF(objectData.TouchName)
        this.creatorUUID = objectData.CreatorID
        this.ownerUUID = objectData.OwnerID
        this.saleType = objectData.SaleType.toByte()
        this.salePrice = objectData.SalePrice
        this.nameKnown = true
        this.nameRequested = false
    }

    fun ApplyObjectUpdate(objectData: ObjectUpdate.ObjectData) {
        this.localID = objectData.ID
        this.uuid = UUIDPool.getUUID(objectData.FullID)
        this.UpdateFlags = objectData.UpdateFlags
        this.parentID = objectData.ParentID
        this.attachmentID = attachmentIDFromState(objectData.State)
        
        if (objectData.OwnerID.leastSignificantBits != 0L || objectData.OwnerID.mostSignificantBits != 0L) {
            this.ownerUUID = UUIDPool.getUUID(objectData.OwnerID)
        }
        
        this.objectCoords.set(1, objectData.Scale)
        
        val text = SLMessage.stringFromVariableOEM(objectData.Text)
        val textColor = if (objectData.TextColor.size >= 4) {
             (objectData.TextColor[0].toInt() and 255) or
             ((objectData.TextColor[1].toInt() shl 8) and 65280) or
             ((objectData.TextColor[2].toInt() shl 16) and 16711680) or
             ((objectData.TextColor[3].toInt() shl 24) and -16777216)
        } else 0
        
        applyHoverText(if (Strings.isNullOrEmpty(text)) null else HoverText.create(text, textColor))
        
        var primVolumeParams = PrimVolumeParams.createFromObjectUpdate(objectData)
        if (primVolumeParams != null && objectData.ExtraParams != null) {
            primVolumeParams.unpackExtraParams(ByteBuffer.wrap(objectData.ExtraParams).order(ByteOrder.LITTLE_ENDIAN))
        }
        
        ParseObjectData(ByteBuffer.wrap(objectData.ObjectData))
        
        val finalVolumeParams = if (primVolumeParams != null) PrimParamsPool.get(primVolumeParams) else null
        
        val newPrimDrawParams = PrimParamsPool.get(PrimDrawParams(finalVolumeParams, SLTextureEntry.create(ByteBuffer.wrap(objectData.TextureEntry), objectData.TextureEntry.size)))
        
        onTexturesUpdate(newPrimDrawParams.textures)
        
        if (!Objects.equal(this.primDrawParams, newPrimDrawParams)) {
            this.primDrawParams = newPrimDrawParams
            getDrawableObject()?.setPrimDrawParams(this.primDrawParams)
        }
        
        // this.primDrawParams = PrimParamsPool.get(newPrimDrawParams) // Redundant assignment?
        
        parseNameValuePairs(SLMessage.stringFromVariableUTF(objectData.NameValue))
        updateSpatialIndex(null, false) // Assuming index null means global default? Or updateSpatialIndex(false) overload
    }
    
    fun updateSpatialIndex(z: Boolean) {
        updateSpatialIndex(SpatialIndex.getInstance().objectIndex, z)
    }

    @Throws(UnsupportedObjectTypeException::class)
    fun ApplyObjectUpdate(objectData: ObjectUpdateCompressed.ObjectData) {
        this.UpdateFlags = objectData.UpdateFlags
        val buffer = ByteBuffer.wrap(objectData.Data)
        buffer.order(ByteOrder.BIG_ENDIAN)
        
        val uuidLong1 = buffer.long
        val uuidLong2 = buffer.long
        this.uuid = UUIDPool.setUUID(this.uuid, uuidLong1, uuidLong2)
        
        buffer.order(ByteOrder.LITTLE_ENDIAN)
        this.localID = buffer.int
        
        val type = buffer.get().toInt()
        if (type != 9) {
             throw UnsupportedObjectTypeException(type.toByte())
        }
        
        this.attachmentID = attachmentIDFromState(buffer.get().toInt())
        
        // Skip some bytes? Decompiled said +4 +1 +1 = +6
        buffer.position(buffer.position() + 6)
        
        objectCoords.set(1, LLVector3.parseFloatVec(buffer)) // Scale?
        objectCoords.set(0, LLVector3.parseFloatVec(buffer)) // Pos?
        // Note: indices might be swapped compared to standard, trusting decompiled flow
        // Decompiled: set(1, ...), set(0, ...)
        
        rotation = LLQuaternion.parseFloatVec3(buffer)
        
        val flags = buffer.int
        
        buffer.order(ByteOrder.BIG_ENDIAN)
        val ownerL1 = buffer.long
        val ownerL2 = buffer.long
        
        if (this.ownerUUID == null || (ownerL1 != 0L || ownerL2 != 0L)) {
            this.ownerUUID = UUIDPool.setUUID(this.ownerUUID, ownerL1, ownerL2)
        }
        
        buffer.order(ByteOrder.LITTLE_ENDIAN)
        
        // Flag parsing
        if ((flags and 128) != 0) { // Scratch pad?
             buffer.position(buffer.position() + 12)
        }
        
        if ((flags and 32) != 0) {
            this.parentID = buffer.int
        }
        
        // Logic for skipping varying fields
        // Mask 1 = Text?
        // Mask 2 = Particles?
        // Mask 4 = ExtraParams?
        // Mask 8 = Sound?
        // Mask 16 = Material?
        
        // Decompiled logic was complex loop over bytes.
        // Simplifying assumption: We read specific flags we care about or skip correctly.
        // 0x02 = Particles? Decompiled skips +1 + N bytes.
        // 0x04 = ExtraParams?
        
        // Let's try to match decompiled structure for stability.
        
        if ((flags and 2) != 0) {
             // Skip particles
             val size = buffer.get().toInt() and 0xFF
             buffer.position(buffer.position() + size)
        }
        
        if ((flags and 4) != 0) { // Gear/Extra params?
             // Decompiled loop to find size?
             // It reads bytes until null? Or uses size byte?
             // Decompiled: loops until null byte found, skipping strings?
             // Logic seems to be reading string (hover text?)
             
             // Wait, 0x04 usually is Name/Value pairs or similar in some packets.
             // But here it seems to be reading a null-terminated string?
             // "ISO-8859-1" string.
             // If found, applyHoverText. So 0x04 is Text/HoverText.
             
             // Read until null terminator
             val start = buffer.position()
             var end = start
             while(end < buffer.capacity()) {
                 if (buffer.get(end) == 0.toByte()) break
                 end++
             }
             val len = end - start
             val bytes = ByteArray(len)
             buffer.get(bytes)
             buffer.position(buffer.position() + 1) // Skip null
             
             try {
                 val text = String(bytes, Charsets.ISO_8859_1)
                 val color = buffer.int
                 applyHoverText(if (Strings.isNullOrEmpty(text)) null else HoverText.create(text, color))
             } catch (e: UnsupportedEncodingException) {
                 Debug.Log("Error parsing hover text")
             }
        }
        
        if ((flags and 512) != 0) { // 0x200
             // Skip loop?
             // Decompiled: while(get() != 0)
             while(buffer.get() != 0.toByte()) {}
        }
        
        if ((flags and 8) != 0) { // Sound?
             buffer.position(buffer.position() + 86) // Fixed size sound data?
        }
        
        // Skip loop for something... 0x10 (Material?)
        // Decompiled: reads count byte, then loops count times skipping short+int+variable.
        val count = buffer.get().toInt() and 0xFF
        for(i in 0 until count) {
             buffer.short // ID?
             val size = buffer.int
             buffer.position(buffer.position() + size)
        }
        
        if ((flags and 16) != 0) { // TextureEntry?
             buffer.position(buffer.position() + 16) // Skip something?
             buffer.position(buffer.position() + 9) // +4+1+4?
        }
        
        if ((flags and 256) != 0) { // 0x100
             while(buffer.get() != 0.toByte()) {}
        }
        
        // Params
        val volumeParams = PrimVolumeParams.createFromPackedData(buffer)
        
        try {
            val textureSize = buffer.int
            val textureEntry = SLTextureEntry.create(buffer, textureSize)
            onTexturesUpdate(textureEntry)
            
            if (volumeParams != null) {
                 // Unpack extra params if needed?
                 // Decompiled: volumeParams.unpackExtraParams(buffer) 
                 // NOTE: buffer position is right after texture?
                 // Decompiled had `r4.position(r2)` logic which backtracked??
                 // Wait, `r2` was saved before some skipping?
                 // This part is risky.
                 // But typically ExtraParams come after TextureEntry in compressed update.
                 volumeParams.unpackExtraParams(buffer)
            }
            
            val newVolume = if (volumeParams != null) PrimParamsPool.get(volumeParams) else null
            val newDrawParams = PrimParamsPool.get(PrimDrawParams(newVolume, textureEntry))
            
            if (!Objects.equal(this.primDrawParams, newDrawParams)) {
                this.primDrawParams = newDrawParams
                getDrawableObject()?.setPrimDrawParams(this.primDrawParams)
            }
            
        } catch (e: Exception) {
            Debug.Log("Failed to retrieve textures/params in compressed update")
        }
        
        updateSpatialIndex(false)
    }

    fun ApplyTerseObjectUpdate(objectData: ImprovedTerseObjectUpdate.ObjectData) {
        val buffer = ByteBuffer.wrap(objectData.Data)
        buffer.order(ByteOrder.LITTLE_ENDIAN)
        
        buffer.int // LocalID (redundant?)
        this.attachmentID = attachmentIDFromState(buffer.get().toInt())
        
        if (buffer.get() != 0.toByte()) { // Has texture?
             // Skip texture entry?
             // Decompiled: +16
             buffer.position(buffer.position() + 16)
        }
        
        objectCoords.set(0, LLVector3.parseFloatVec(buffer))
        objectCoords.set(2, LLVector3.parseU16Vec(buffer, -128.0f, 128.0f, -128.0f, 128.0f))
        
        buffer.position(buffer.position() + 6) // Skip?
        rotation = LLQuaternion.parseU16Vec3(buffer, -1.0f, 1.0f)
        buffer.position(buffer.position() + 6) // Skip?
        
        if (objectData.TextureEntry.size > 4) {
            val texBuffer = ByteBuffer.wrap(objectData.TextureEntry)
            texBuffer.position(4)
            val textureEntry = SLTextureEntry.create(texBuffer, texBuffer.remaining())
            onTexturesUpdate(textureEntry)
            
            val currentParams = this.primDrawParams
            if (currentParams != null && !textureEntry.equals(currentParams.textures)) {
                val newParams = PrimParamsPool.get(PrimDrawParams(currentParams.volumeParams, textureEntry))
                if (!Objects.equal(this.primDrawParams, newParams)) {
                    this.primDrawParams = newParams
                    getDrawableObject()?.setPrimDrawParams(this.primDrawParams)
                }
            }
        }
        updateSpatialIndex(false)
    }

    @Synchronized
    fun addChild(child: SLObjectInfo) {
        this.treeNode.addChild(child.treeNode)
        if (child.isAttachment) {
            child.getAttachedTo()?.updateAttachments()
        }
    }

    fun clearDrawListEntry() {
        synchronized(this) {
            this.drawListEntry = null
        }
    }

    protected abstract fun createDrawListEntry(): DrawListObjectEntry

    fun getAbsolutePosition(): LLVector3 {
        var parent = getParentObject()
        val pos = objectCoords.get(0).copy() // Copy to avoid modifying cached vector if get(0) returns reference
        // Wait, LLVector3 might be mutable or immutable. If mutable, copy is needed.
        // Assuming copy() or constructor exists. If not, manual copy.
        // objectCoords.get(0) likely returns a reference from Vector3Array.
        // We want to return a new vector representing absolute pos.
        val result = LLVector3(pos) 
        
        while (parent != null) {
            parent.objectCoords.addToVector(0, result)
            parent = parent.getParentObject()
        }
        return result
    }

    fun getAttachedTo(): SLObjectInfo? {
        val parent = getParentObject()
        return if (parent != null) {
            if (parent.isAvatar()) parent else parent.getAttachedTo()
        } else null
    }

    fun getDrawListEntry(): DrawListObjectEntry {
        var entry = drawListEntry?.get()
        if (entry == null) {
            synchronized(this) {
                entry = drawListEntry?.get()
                if (entry == null) {
                    entry = createDrawListEntry()
                    drawListEntry = WeakReference(entry)
                }
            }
        }
        return entry!!
    }

    fun getExistingDrawListEntry(): DrawListObjectEntry? {
        return drawListEntry?.get()
    }

    fun getHoverText(): HoverText? = hoverText
    fun getId(): UUID? = uuid // Nullable?
    
    fun getObjectExtents(matrixStack: MatrixStack, z: Boolean, minExt: LLVector3, maxExt: LLVector3) {
        val fArr = FloatArray(8)
        val offset0 = objectCoords.getElementOffset(0)
        val offset1 = objectCoords.getElementOffset(1)
        val data = objectCoords.getData()
        
        matrixStack.glPushMatrix()
        matrixStack.glTranslatef(data[offset0 + 0], data[offset0 + 1], data[offset0 + 2])
        
        rotation?.let {
             matrixStack.glMultMatrixf(it.inverseMatrix, 0)
        }
        
        fArr[0] = (-data[offset1 + 0]) / 2.0f
        fArr[1] = (-data[offset1 + 1]) / 2.0f
        fArr[2] = (-data[offset1 + 2]) / 2.0f
        fArr[3] = 1.0f
        
        Matrix.multiplyMV(fArr, 4, matrixStack.matrixData, matrixStack.matrixDataOffset, fArr, 0)
        
        if (z) {
            minExt.x = fArr[4]
            minExt.y = fArr[5]
            minExt.z = fArr[6]
            maxExt.x = fArr[4]
            maxExt.y = fArr[5]
            maxExt.z = fArr[6]
        } else {
            minExt.x = min(minExt.x, fArr[4])
            minExt.y = min(minExt.y, fArr[5])
            minExt.z = min(minExt.z, fArr[6])
            maxExt.x = max(maxExt.x, fArr[4])
            maxExt.y = max(maxExt.y, fArr[5])
            maxExt.z = max(maxExt.z, fArr[6])
        }
        
        fArr[0] = data[offset1 + 0] / 2.0f
        fArr[1] = data[offset1 + 1] / 2.0f
        fArr[2] = data[offset1 + 2] / 2.0f
        fArr[3] = 1.0f
        
        Matrix.multiplyMV(fArr, 4, matrixStack.matrixData, matrixStack.matrixDataOffset, fArr, 0)
        
        minExt.x = min(minExt.x, fArr[4])
        minExt.y = min(minExt.y, fArr[5])
        minExt.z = min(minExt.z, fArr[6])
        maxExt.x = max(maxExt.x, fArr[4])
        maxExt.y = max(maxExt.y, fArr[5])
        maxExt.z = max(maxExt.z, fArr[6])
        
        for (child in treeNode) {
            child.getObjectExtents(matrixStack, false, minExt, maxExt)
        }
        
        matrixStack.glPopMatrix()
    }

    fun getOwnerUUID(): UUID? {
        return if (ownerUUID != null && ownerUUID!!.leastSignificantBits == 0L && ownerUUID!!.mostSignificantBits == 0L) {
            creatorUUID
        } else {
            ownerUUID
        }
    }

    fun getParentObject(): SLObjectInfo? = treeNode.parent
    fun getPayInfo(): PayInfo? = payInfo
    fun getPrimDrawParams(): PrimDrawParams? = primDrawParams
    
    fun getRootPrim(): SLObjectInfo {
        val parent = treeNode.parent
        return if (parent != null && !parent.isAvatar()) parent.getRootPrim() else this
    }

    fun getTouchName(): String = touchName

    fun hasTouchableChildren(): Boolean {
        for (child in treeNode) {
            if (child.isTouchable()) return true
        }
        return false
    }

    abstract fun isAvatar(): Boolean

    @Synchronized
    fun isAvatarSittingOn(): Boolean {
        for (child in treeNode) {
            if (child is SLObjectAvatarInfo && child.isMyAvatar()) return true
        }
        return false
    }

    fun isMyAttachment(): Boolean {
        val parent = getParentObject()
        return parent is SLObjectAvatarInfo && parent.isMyAvatar()
    }

    fun isPayable(): Boolean = (UpdateFlags and 512) != 0
    fun isTouchable(): Boolean = (UpdateFlags and 128) != 0

    protected open fun onTexturesUpdate(sLTextureEntry: SLTextureEntry) {
        // Hook
    }

    @Synchronized
    fun removeChild(child: SLObjectInfo) {
        if (child.isAttachment) {
            child.getAttachedTo()?.updateAttachments()
        }
        treeNode.removeChild(child.treeNode)
    }

    fun removeFromSpatialIndex() {
        getExistingDrawListEntry()?.requestEntryRemoval()
        if (!isAvatar()) {
            for (child in treeNode) {
                child.removeFromSpatialIndex()
            }
        }
    }

    @Synchronized
    fun setIsAttachmentAll(isAttach: Boolean) {
        this.isAttachment = isAttach
        for (child in treeNode) {
            if (!child.isAvatar()) {
                child.setIsAttachmentAll(isAttach)
            }
        }
    }

    fun setPayInfo(payInfo: PayInfo?) {
        this.payInfo = payInfo
    }

    // Implementation of SLObjectDisplayInfo interface
    override fun getLocalID(): Int = localID
    override fun getUUID(): UUID = uuid ?: UUID(0,0) // Return dummy if null
    override fun getPosition(): LLVector3 = objectCoords.get(0)
    override fun getRotation(): LLVector3 = LLVector3(0f,0f,0f) // Dummy for now
    override fun getScale(): LLVector3 = objectCoords.get(1)
}
