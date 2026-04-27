package com.linkpoint.slproto.objects

import android.opengl.Matrix
import com.google.common.base.Ascii
import com.google.common.base.Objects
import com.google.common.base.Strings
import com.google.common.primitives.UnsignedBytes
import com.linkpoint.render.DrawableObject
import com.linkpoint.render.MatrixStack
import com.linkpoint.render.avatar.DrawableAvatar
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
import com.linkpoint.utils.IdentityMatrix
import com.linkpoint.utils.LinkedTreeNode
import com.linkpoint.utils.UUIDPool
import java.lang.ref.WeakReference
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.Arrays
import java.util.NoSuchElementException
import java.util.UUID
import javax.annotation.Nonnull
import javax.annotation.Nullable

abstract class SLObjectInfo : Identifiable<UUID> {
    private const val AGENT_ATTACH_MASK: Int = 240
    private const val AGENT_ATTACH_OFFSET: Int = 4
    const val FLAGS_ALLOW_INVENTORY_DROP: Int = 65536
    const val FLAGS_ANIM_SOURCE: Int = 2097152
    const val FLAGS_CAMERA_DECOUPLED: Int = 1048576
    const val FLAGS_CAMERA_SOURCE: Int = 4194304
    const val FLAGS_CAST_SHADOWS: Int = 8388608
    const val FLAGS_CREATE_SELECTED: Int = 2
    const val FLAGS_HANDLE_TOUCH: Int = 128
    const val FLAGS_INCLUDE_IN_SEARCH: Int = 32768
    const val FLAGS_INVENTORY_EMPTY: Int = 2048
    const val FLAGS_JOINT_HINGE: Int = 4096
    const val FLAGS_JOINT_LP2P: Int = 16384
    const val FLAGS_JOINT_P2P: Int = 8192
    const val FLAGS_OBJECT_ANY_OWNER: Int = 16
    const val FLAGS_OBJECT_COPY: Int = 8
    const val FLAGS_OBJECT_GROUP_OWNED: Int = 262144
    const val FLAGS_OBJECT_MODIFY: Int = 4
    const val FLAGS_OBJECT_MOVE: Int = 256
    const val FLAGS_OBJECT_OWNER_MODIFY: Int = 268435456
    const val FLAGS_OBJECT_TRANSFER: Int = 131072
    const val FLAGS_OBJECT_YOU_OWNER: Int = 32
    const val FLAGS_PHANTOM: Int = 1024
    const val FLAGS_SCRIPTED: Int = 64
    const val FLAGS_TAKES_MONEY: Int = 512
    const val FLAGS_TEMPORARY: Int = 1073741824
    const val FLAGS_TEMPORARY_ON_REZ: Int = 536870912
    const val FLAGS_USE_PHYSICS: Int = 1
    const val FLAGS_ZLIB_COMPRESSED: Int = Integer.MIN_VALUE
    const val OBJ_COORD_POSITION: Int = 0
    const val OBJ_COORD_SCALE: Int = 1
    const val OBJ_COORD_VELOCITY: Int = 2
    const val OBJ_COORD_WORLD_CENTER: Int = 3
    const val PAY_DEFAULT: Int = -2
    const val PAY_HIDE: Int = -1
    public Int UpdateFlags
    public UUID attachedToUUID = null
    public Int attachmentID = 0
    public UUID creatorUUID = null
    public String description = ""
    private volatile WeakReference<DrawListObjectEntry> drawListEntry
    public Int hierLevel = 0
    private volatile HoverText hoverText = null
    public Boolean isAttachment = false
    public volatile Boolean isDead = false
    public Int localID
    public String name = "(loading)"
    public Boolean nameKnown = false
    public Boolean nameRequested = false
    public Long nameRequestedAt = 0
    public Float objRadius
    private val Vector3Array objectCoords = Vector3Array(4)
    public UUID ownerUUID = null
    public Int parentID = 0
    private PayInfo payInfo
    private PrimDrawParams primDrawParams
    private LLQuaternion rotation
    public Int salePrice
    public Byte saleType = 0
    public String touchName = ""
    val LinkedTreeNode<SLObjectInfo> treeNode = LinkedTreeNode<>(this)
    protected UUID uuid
    public FloatArray worldMatrix

    /* JADX WARNING: Code restructure failed: missing block: B:3:0x001f, code lost:
        r7.objectCoords.set(0, com.lumiyaviewer.lumiya.slproto.types.LLVector3.parseFloatVec(r8))
        r7.objectCoords.set(2, com.lumiyaviewer.lumiya.slproto.types.LLVector3.parseFloatVec(r8))
        r8.position(r8.position() + 12)
        r7.rotation = com.lumiyaviewer.lumiya.slproto.types.LLQuaternion.parseFloatVec3(r8)
     */
    /* JADX WARNING: Code restructure failed: missing block: B:5:0x004b, code lost:
        r7.objectCoords.set(0, com.lumiyaviewer.lumiya.slproto.types.LLVector3.parseU16Vec(r8, -128.0f, 384.0f, -256.0f, 4096.0f))
        r7.objectCoords.set(2, com.lumiyaviewer.lumiya.slproto.types.LLVector3.parseU16Vec(r8, -256.0f, 256.0f, -256.0f, 256.0f))
        r8.position(r8.position() + 6)
        r7.rotation = com.lumiyaviewer.lumiya.slproto.types.LLQuaternion.parseU16Vec3(r8, -1.0f, 1.0f)
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:?, code lost:
        return
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:?, code lost:
        return
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private fun ParseObjectData(java.nio.ByteBuffer r8) {
        /*
            r7 = this
            r6 = 0
            r5 = 1166016512(0x45800000, Float:4096.0)
            r4 = 1136656384(0x43c00000, Float:384.0)
            r3 = 1132462080(0x43800000, Float:256.0)
            r2 = -1015021568(0xffffffffc3800000, Float:-256.0)
            java.nio.ByteOrder r0 = java.nio.ByteOrder.LITTLE_ENDIAN
            r8.order(r0)
            val r0: Int = r8.limit()
            switch(r0) {
                case 16: goto L_0x0074
                case 32: goto L_0x004b
                case 48: goto L_0x0042
                case 60: goto L_0x001f
                case 76: goto L_0x0016
                default: goto L_0x0015
            }
        L_0x0015:
            return
        L_0x0016:
            val r0: Int = r8.position()
            val r0: Int = r0 + 16
            r8.position(r0)
        L_0x001f:
            com.lumiyaviewer.lumiya.slproto.types.Vector3Array r0 = r7.objectCoords
            com.lumiyaviewer.lumiya.slproto.types.LLVector3 r1 = com.lumiyaviewer.lumiya.slproto.types.LLVector3.parseFloatVec(r8)
            r0.set(r6, r1)
            com.lumiyaviewer.lumiya.slproto.types.Vector3Array r0 = r7.objectCoords
            com.lumiyaviewer.lumiya.slproto.types.LLVector3 r1 = com.lumiyaviewer.lumiya.slproto.types.LLVector3.parseFloatVec(r8)
            r2 = 2
            r0.set(r2, r1)
            val r0: Int = r8.position()
            val r0: Int = r0 + 12
            r8.position(r0)
            com.lumiyaviewer.lumiya.slproto.types.LLQuaternion r0 = com.lumiyaviewer.lumiya.slproto.types.LLQuaternion.parseFloatVec3(r8)
            r7.rotation = r0
            goto L_0x0015
        L_0x0042:
            val r0: Int = r8.position()
            val r0: Int = r0 + 16
            r8.position(r0)
        L_0x004b:
            com.lumiyaviewer.lumiya.slproto.types.Vector3Array r0 = r7.objectCoords
            r1 = -1023410176(0xffffffffc3000000, Float:-128.0)
            com.lumiyaviewer.lumiya.slproto.types.LLVector3 r1 = com.lumiyaviewer.lumiya.slproto.types.LLVector3.parseU16Vec(r8, r1, r4, r2, r5)
            r0.set(r6, r1)
            com.lumiyaviewer.lumiya.slproto.types.Vector3Array r0 = r7.objectCoords
            com.lumiyaviewer.lumiya.slproto.types.LLVector3 r1 = com.lumiyaviewer.lumiya.slproto.types.LLVector3.parseU16Vec(r8, r2, r3, r2, r3)
            r2 = 2
            r0.set(r2, r1)
            val r0: Int = r8.position()
            val r0: Int = r0 + 6
            r8.position(r0)
            r0 = -1082130432(0xffffffffbf800000, Float:-1.0)
            r1 = 1065353216(0x3f800000, Float:1.0)
            com.lumiyaviewer.lumiya.slproto.types.LLQuaternion r0 = com.lumiyaviewer.lumiya.slproto.types.LLQuaternion.parseU16Vec3(r8, r0, r1)
            r7.rotation = r0
            goto L_0x0015
        L_0x0074:
            com.lumiyaviewer.lumiya.slproto.types.Vector3Array r0 = r7.objectCoords
            com.lumiyaviewer.lumiya.slproto.types.LLVector3 r1 = com.lumiyaviewer.lumiya.slproto.types.LLVector3.parseU8Vec(r8, r4, r4, r2, r5)
            r0.set(r6, r1)
            com.lumiyaviewer.lumiya.slproto.types.Vector3Array r0 = r7.objectCoords
            com.lumiyaviewer.lumiya.slproto.types.LLVector3 r1 = com.lumiyaviewer.lumiya.slproto.types.LLVector3.parseU8Vec(r8, r2, r3, r2, r3)
            r2 = 2
            r0.set(r2, r1)
            val r0: Int = r8.position()
            val r0: Int = r0 + 3
            r8.position(r0)
            r0 = -1082130432(0xffffffffbf800000, Float:-1.0)
            r1 = 1065353216(0x3f800000, Float:1.0)
            com.lumiyaviewer.lumiya.slproto.types.LLQuaternion r0 = com.lumiyaviewer.lumiya.slproto.types.LLQuaternion.parseU8Vec3(r8, r0, r1)
            r7.rotation = r0
            goto L_0x0015
        */
        throw UnsupportedOperationException("Method not decompiled: com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo.ParseObjectData(java.nio.ByteBuffer):Unit")
    }

     private fun applyHoverText(hoverText2: HoverText) {
        if (!Objects.equal(this.hoverText, hoverText2)) {
            this.hoverText = hoverText2
            val drawableObject: DrawableObject = getDrawableObject()
            if (drawableObject != null) {
                drawableObject.setHoverText(hoverText2)
            }
        }
    }

    @JvmStatic
 private fun attachmentIDFromState(i: Int): Int {
        return (((i & 255) & AGENT_ATTACH_MASK) >> 4) | (((i & 255) & -241) << 4)
    }

     private fun calculateWorldMatrix(fArr: FloatArray): FloatArray {
        val lLQuaternion: LLQuaternion = this.rotation
        if (lLQuaternion == null) {
            return null
        }
        val fArr2: FloatArray = Float[16]
        val fArr3: FloatArray = Float[16]
        this.objectCoords.MatrixTranslate(fArr3, 0, fArr, 0, 0)
        Matrix.multiplyMM(fArr2, 0, fArr3, 0, lLQuaternion.getInverseMatrix(), 0)
        return fArr2
    }

    @JvmStatic
     fun create(ObjectUpdateCompressed.ObjectData objectData) throws UnsupportedObjectTypeException {
        val sLObjectPrimInfo: SLObjectPrimInfo = SLObjectPrimInfo()
        sLObjectPrimInfo.ApplyObjectUpdate(objectData)
        return sLObjectPrimInfo
    }

    @JvmStatic
     fun create(uuid2: UUID, ObjectUpdate.ObjectData objectData, uuid3: UUID): SLObjectInfo {
        val sLObjectAvatarInfo: SLObjectInfo = objectData.PCode == 47 ? SLObjectAvatarInfo(uuid2, UUIDPool.getUUID(objectData.FullID), uuid3.equals(objectData.FullID)) : SLObjectPrimInfo()
        sLObjectAvatarInfo.ApplyObjectUpdate(objectData)
        return sLObjectAvatarInfo
    }

     private fun getDrawableObject(): DrawableObject {
        val existingDrawListEntry: DrawListObjectEntry = getExistingDrawListEntry()
        if (existingDrawListEntry instanceof DrawListPrimEntry) {
            return ((DrawListPrimEntry) existingDrawListEntry).getDrawableObject()
        }
        return null
    }

    @JvmStatic
     fun getLocalID(ImprovedTerseObjectUpdate.ObjectData objectData): Int {
        val wrap: ByteBuffer = ByteBuffer.wrap(objectData.Data)
        wrap.order(ByteOrder.LITTLE_ENDIAN)
        return wrap.getInt()
    }

    @JvmStatic
     fun getLocalID(ObjectUpdateCompressed.ObjectData objectData): Int {
        val wrap: ByteBuffer = ByteBuffer.wrap(objectData.Data)
        wrap.position(16)
        wrap.order(ByteOrder.LITTLE_ENDIAN)
        return wrap.getInt()
    }

     private fun parseNameValuePairs(str: String) {
        for (String str2 : str.split("\n")) {
            if (str2.startsWith("AttachItemID ")) {
                val i: Int = 0
                while (i < 4) {
                    val indexOf: Int = str2.indexOf(32)
                    if (indexOf >= 0) {
                        str2 = str2.substring(indexOf + 1)
                    }
                    i++
                    str2 = str2.trim()
                }
                try {
                    this.attachedToUUID = UUIDPool.getUUID(UUID.fromString(str2))
                } catch (Exception e) {
                    this.attachedToUUID = null
                }
            } else if (str2.startsWith("DisplayName ")) {
                val i2: Int = 0
                while (i2 < 4) {
                    val indexOf2: Int = str2.indexOf(32)
                    if (indexOf2 >= 0) {
                        str2 = str2.substring(indexOf2 + 1)
                    }
                    i2++
                    str2 = str2.trim()
                }
                this.name = str2
                this.nameKnown = true
            }
        }
    }

     private fun updateAttachments() {
        DrawableAvatar drawableAvatar
        if (isAvatar() && (drawableAvatar = SpatialIndex.getInstance().getDrawableAvatar(this)) != null) {
            drawableAvatar.updateAttachments()
        }
    }

     private fun updateSpatialIndex(spatialObjectIndex: SpatialObjectIndex, z: Boolean) {
        updateWorldMatrix(false)
        if (z) {
            synchronized (this) {
                this.drawListEntry = null
            }
        }
        if (spatialObjectIndex != null && (!this.isDead)) {
            spatialObjectIndex.updateObject(getDrawListEntry())
        }
        if (!isAvatar()) {
            for (SLObjectInfo updateSpatialIndex : this.treeNode) {
                updateSpatialIndex.updateSpatialIndex(z)
            }
            return
        }
        for (SLObjectInfo updateWorldMatrix : this.treeNode) {
            updateWorldMatrix.updateWorldMatrix(true)
        }
    }

    fun ApplyObjectProperties(ObjectProperties.ObjectData objectData) {
        this.name = SLMessage.stringFromVariableOEM(objectData.Name)
        this.description = SLMessage.stringFromVariableUTF(objectData.Description)
        this.touchName = SLMessage.stringFromVariableUTF(objectData.TouchName)
        this.creatorUUID = objectData.CreatorID
        this.ownerUUID = objectData.OwnerID
        this.saleType = (Byte) objectData.SaleType
        this.salePrice = objectData.SalePrice
        this.nameKnown = true
        this.nameRequested = false
    }

    fun ApplyObjectUpdate(ObjectUpdate.ObjectData objectData) {
        val primVolumeParams: PrimVolumeParams = null
        this.localID = objectData.ID
        this.uuid = UUIDPool.getUUID(objectData.FullID)
        this.UpdateFlags = objectData.UpdateFlags
        this.parentID = objectData.ParentID
        this.attachmentID = attachmentIDFromState(objectData.State)
        if (!(objectData.OwnerID.getLeastSignificantBits() == 0 && objectData.OwnerID.getMostSignificantBits() == 0)) {
            this.ownerUUID = UUIDPool.getUUID(objectData.OwnerID)
        }
        this.objectCoords.set(1, objectData.Scale)
        val stringFromVariableOEM: String = SLMessage.stringFromVariableOEM(objectData.Text)
        applyHoverText(Strings.isNullOrEmpty(stringFromVariableOEM) ? null : HoverText.create(stringFromVariableOEM, objectData.TextColor.length >= 4 ? (objectData.TextColor[0] & UnsignedBytes.MAX_VALUE) | ((objectData.TextColor[1] << 8) & 65280) | ((objectData.TextColor[2] << 16) & 16711680) | ((objectData.TextColor[3] << Ascii.CAN) & -16777216) : 0))
        val createFromObjectUpdate: PrimVolumeParams = PrimVolumeParams.createFromObjectUpdate(objectData)
        if (!(createFromObjectUpdate == null || objectData.ExtraParams == null)) {
            createFromObjectUpdate.unpackExtraParams(ByteBuffer.wrap(objectData.ExtraParams).order(ByteOrder.LITTLE_ENDIAN))
        }
        ParseObjectData(ByteBuffer.wrap(objectData.ObjectData))
        if (createFromObjectUpdate != null) {
            primVolumeParams = PrimParamsPool.get(createFromObjectUpdate)
        }
        val primDrawParams2: PrimDrawParams = PrimParamsPool.get(PrimDrawParams(primVolumeParams, SLTextureEntry.create(ByteBuffer.wrap(objectData.TextureEntry), objectData.TextureEntry.length)))
        onTexturesUpdate(primDrawParams2.getTextures())
        if (!Objects.equal(this.primDrawParams, primDrawParams2)) {
            this.primDrawParams = primDrawParams2
            val drawableObject: DrawableObject = getDrawableObject()
            if (drawableObject != null) {
                drawableObject.setPrimDrawParams(this.primDrawParams)
            }
        }
        this.primDrawParams = PrimParamsPool.get(primDrawParams2)
        parseNameValuePairs(SLMessage.stringFromVariableUTF(objectData.NameValue))
        updateSpatialIndex(false)
    }

    /* JADX WARNING: Removed duplicated region for block: B:65:0x017e  */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x0188  */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x019b  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    fun ApplyObjectUpdate(com.lumiyaviewer.lumiya.slproto.messages.ObjectUpdateCompressed.ObjectData r13) throws com.lumiyaviewer.lumiya.slproto.objects.UnsupportedObjectTypeException {
        /*
            r12 = this
            r10 = 0
            r3 = 0
            r1 = 0
            val r0: Int = r13.UpdateFlags
            r12.UpdateFlags = r0
            val r0: ByteArray = r13.Data
            java.nio.ByteBuffer r4 = java.nio.ByteBuffer.wrap(r0)
            java.nio.ByteOrder r0 = java.nio.ByteOrder.BIG_ENDIAN
            r4.order(r0)
            val r6: Long = r4.getLong()
            val r8: Long = r4.getLong()
            java.util.UUID r0 = r12.uuid
            java.util.UUID r0 = com.lumiyaviewer.lumiya.utils.UUIDPool.setUUID(r0, r6, r8)
            r12.uuid = r0
            java.nio.ByteOrder r0 = java.nio.ByteOrder.LITTLE_ENDIAN
            r4.order(r0)
            val r0: Int = r4.getInt()
            r12.localID = r0
            val r0: Byte = r4.get()
            r2 = 9
            if (r0 == r2) goto L_0x003c
            com.lumiyaviewer.lumiya.slproto.objects.UnsupportedObjectTypeException r1 = com.lumiyaviewer.lumiya.slproto.objects.UnsupportedObjectTypeException
            r1.<init>(r0)
            throw r1
        L_0x003c:
            val r0: Byte = r4.get()
            val r0: Int = attachmentIDFromState(r0)
            r12.attachmentID = r0
            val r0: Int = r4.position()
            val r0: Int = r0 + 4
            val r0: Int = r0 + 1
            val r0: Int = r0 + 1
            r4.position(r0)
            com.lumiyaviewer.lumiya.slproto.types.LLVector3 r0 = com.lumiyaviewer.lumiya.slproto.types.LLVector3.parseFloatVec(r4)
            com.lumiyaviewer.lumiya.slproto.types.LLVector3 r2 = com.lumiyaviewer.lumiya.slproto.types.LLVector3.parseFloatVec(r4)
            com.lumiyaviewer.lumiya.slproto.types.Vector3Array r5 = r12.objectCoords
            r6 = 1
            r5.set(r6, r0)
            com.lumiyaviewer.lumiya.slproto.types.Vector3Array r0 = r12.objectCoords
            r0.set(r1, r2)
            com.lumiyaviewer.lumiya.slproto.types.LLQuaternion r0 = com.lumiyaviewer.lumiya.slproto.types.LLQuaternion.parseFloatVec3(r4)
            r12.rotation = r0
            val r5: Int = r4.getInt()
            java.nio.ByteOrder r0 = java.nio.ByteOrder.BIG_ENDIAN
            r4.order(r0)
            val r6: Long = r4.getLong()
            val r8: Long = r4.getLong()
            java.util.UUID r0 = r12.ownerUUID
            if (r0 == 0) goto L_0x0089
            val r0: Int = (r6 > r10 ? 1 : (r6 == r10 ? 0 : -1))
            if (r0 == 0) goto L_0x0091
            val r0: Int = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1))
            if (r0 == 0) goto L_0x0091
        L_0x0089:
            java.util.UUID r0 = r12.ownerUUID
            java.util.UUID r0 = com.lumiyaviewer.lumiya.utils.UUIDPool.setUUID(r0, r6, r8)
            r12.ownerUUID = r0
        L_0x0091:
            java.nio.ByteOrder r0 = java.nio.ByteOrder.LITTLE_ENDIAN
            r4.order(r0)
            r0 = r5 & 128(0x80, Float:1.794E-43)
            if (r0 == 0) goto L_0x00a3
            val r0: Int = r4.position()
            val r0: Int = r0 + 12
            r4.position(r0)
        L_0x00a3:
            r0 = r5 & 32
            if (r0 == 0) goto L_0x00ad
            val r0: Int = r4.getInt()
            r12.parentID = r0
        L_0x00ad:
            r0 = r5 & 2
            if (r0 == 0) goto L_0x012c
            val r0: Int = r4.position()
            val r0: Int = r0 + 1
            r4.position(r0)
        L_0x00ba:
            r0 = r5 & 4
            if (r0 == 0) goto L_0x00f6
            val r6: Int = r4.position()
            r0 = r1
        L_0x00c3:
            val r2: Int = r6 + r0
            val r7: Int = r4.capacity()
            if (r2 >= r7) goto L_0x00d3
            val r2: Int = r6 + r0
            val r2: Byte = r4.get(r2)
            if (r2 != 0) goto L_0x013e
        L_0x00d3:
            if (r0 == 0) goto L_0x01b7
            val r7: ByteArray = Byte[r0]
            r4.get(r7, r1, r0)
            java.lang.String r2 = java.lang.String     // Catch:{ UnsupportedEncodingException -> 0x0141 }
            java.lang.String r8 = "ISO-8859-1"
            r2.<init>(r7, r8)     // Catch:{ UnsupportedEncodingException -> 0x0141 }
        L_0x00e2:
            val r0: Int = r0 + r6
            val r0: Int = r0 + 1
            r4.position(r0)
            val r0: Int = r4.getInt()
            val r6: Boolean = com.google.common.base.Strings.isNullOrEmpty(r2)
            if (r6 == 0) goto L_0x0144
            r0 = r3
        L_0x00f3:
            r12.applyHoverText(r0)
        L_0x00f6:
            r0 = r5 & 512(0x200, Float:7.175E-43)
            if (r0 == 0) goto L_0x0100
        L_0x00fa:
            val r0: Byte = r4.get()
            if (r0 != 0) goto L_0x00fa
        L_0x0100:
            r0 = r5 & 8
            if (r0 == 0) goto L_0x010d
            val r0: Int = r4.position()
            val r0: Int = r0 + 86
            r4.position(r0)
        L_0x010d:
            val r2: Int = r4.position()
            val r0: Byte = r4.get()
            r6 = r0 & 255(0xff, Float:3.57E-43)
            r0 = r1
        L_0x0118:
            if (r0 >= r6) goto L_0x0149
            r4.getShort()
            val r7: Int = r4.getInt()
            val r8: Int = r4.position()
            val r7: Int = r7 + r8
            r4.position(r7)
            val r0: Int = r0 + 1
            goto L_0x0118
        L_0x012c:
            r0 = r5 & 1
            if (r0 == 0) goto L_0x00ba
            val r0: Byte = r4.get()
            val r2: Int = r4.position()
            val r0: Int = r0 + r2
            r4.position(r0)
            goto L_0x00ba
        L_0x013e:
            val r0: Int = r0 + 1
            goto L_0x00c3
        L_0x0141:
            r2 = move-exception
            r2 = r3
            goto L_0x00e2
        L_0x0144:
            com.lumiyaviewer.lumiya.slproto.objects.HoverText r0 = com.lumiyaviewer.lumiya.slproto.objects.HoverText.create(r2, r0)
            goto L_0x00f3
        L_0x0149:
            r0 = r5 & 16
            if (r0 == 0) goto L_0x0163
            val r0: Int = r4.position()
            val r0: Int = r0 + 16
            r4.position(r0)
            val r0: Int = r4.position()
            val r0: Int = r0 + 4
            val r0: Int = r0 + 1
            val r0: Int = r0 + 4
            r4.position(r0)
        L_0x0163:
            r0 = r5 & 256(0x100, Float:3.59E-43)
            if (r0 == 0) goto L_0x016d
        L_0x0167:
            val r0: Byte = r4.get()
            if (r0 != 0) goto L_0x0167
        L_0x016d:
            com.lumiyaviewer.lumiya.slproto.prims.PrimVolumeParams r5 = com.lumiyaviewer.lumiya.slproto.prims.PrimVolumeParams.createFromPackedData(r4)
            val r0: Int = r4.getInt()     // Catch:{ Exception -> 0x01ac }
            com.lumiyaviewer.lumiya.slproto.textures.SLTextureEntry r0 = com.lumiyaviewer.lumiya.slproto.textures.SLTextureEntry.create((java.nio.ByteBuffer) r4, (Int) r0)     // Catch:{ Exception -> 0x01ac }
            r12.onTexturesUpdate(r0)     // Catch:{ Exception -> 0x01b5 }
        L_0x017c:
            if (r5 == 0) goto L_0x0184
            r4.position(r2)
            r5.unpackExtraParams(r4)
        L_0x0184:
            com.lumiyaviewer.lumiya.slproto.prims.PrimDrawParams r2 = com.lumiyaviewer.lumiya.slproto.prims.PrimDrawParams
            if (r5 == 0) goto L_0x018c
            com.lumiyaviewer.lumiya.slproto.prims.PrimVolumeParams r3 = com.lumiyaviewer.lumiya.slproto.prims.PrimParamsPool.get((com.lumiyaviewer.lumiya.slproto.prims.PrimVolumeParams) r5)
        L_0x018c:
            r2.<init>(r3, r0)
            com.lumiyaviewer.lumiya.slproto.prims.PrimDrawParams r0 = com.lumiyaviewer.lumiya.slproto.prims.PrimParamsPool.get((com.lumiyaviewer.lumiya.slproto.prims.PrimDrawParams) r2)
            com.lumiyaviewer.lumiya.slproto.prims.PrimDrawParams r2 = r12.primDrawParams
            val r2: Boolean = com.google.common.base.Objects.equal(r2, r0)
            if (r2 != 0) goto L_0x01a8
            r12.primDrawParams = r0
            com.lumiyaviewer.lumiya.render.DrawableObject r0 = r12.getDrawableObject()
            if (r0 == 0) goto L_0x01a8
            com.lumiyaviewer.lumiya.slproto.prims.PrimDrawParams r2 = r12.primDrawParams
            r0.setPrimDrawParams(r2)
        L_0x01a8:
            r12.updateSpatialIndex(r1)
            return
        L_0x01ac:
            r0 = move-exception
            r0 = r3
        L_0x01ae:
            java.lang.String r6 = "Failed to retrieve textures in compressed update"
            com.lumiyaviewer.lumiya.Debug.Log(r6)
            goto L_0x017c
        L_0x01b5:
            r6 = move-exception
            goto L_0x01ae
        L_0x01b7:
            r2 = r3
            goto L_0x00e2
        */
        throw UnsupportedOperationException("Method not decompiled: com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo.ApplyObjectUpdate(com.lumiyaviewer.lumiya.slproto.messages.ObjectUpdateCompressed$ObjectData):Unit")
    }

    fun ApplyTerseObjectUpdate(ImprovedTerseObjectUpdate.ObjectData objectData) {
        val wrap: ByteBuffer = ByteBuffer.wrap(objectData.Data)
        wrap.order(ByteOrder.LITTLE_ENDIAN)
        wrap.getInt()
        this.attachmentID = attachmentIDFromState(wrap.get())
        if (wrap.get() != 0) {
            wrap.position(wrap.position() + 16)
        }
        val parseFloatVec: LLVector3 = LLVector3.parseFloatVec(wrap)
        val parseU16Vec: LLVector3 = LLVector3.parseU16Vec(wrap, -128.0f, 128.0f, -128.0f, 128.0f)
        this.objectCoords.set(0, parseFloatVec)
        this.objectCoords.set(2, parseU16Vec)
        wrap.position(wrap.position() + 6)
        this.rotation = LLQuaternion.parseU16Vec3(wrap, -1.0f, 1.0f)
        wrap.position(wrap.position() + 6)
        if (objectData.TextureEntry.length > 4) {
            val wrap2: ByteBuffer = ByteBuffer.wrap(objectData.TextureEntry)
            wrap2.position(4)
            val create: SLTextureEntry = SLTextureEntry.create(wrap2, wrap2.remaining())
            onTexturesUpdate(create)
            val primDrawParams2: PrimDrawParams = this.primDrawParams
            if (primDrawParams2 != null && !create.equals(primDrawParams2.getTextures())) {
                val primDrawParams3: PrimDrawParams = PrimParamsPool.get(PrimDrawParams(primDrawParams2.getVolumeParams(), create))
                if (!Objects.equal(this.primDrawParams, primDrawParams3)) {
                    this.primDrawParams = primDrawParams3
                    val drawableObject: DrawableObject = getDrawableObject()
                    if (drawableObject != null) {
                        drawableObject.setPrimDrawParams(this.primDrawParams)
                    }
                }
            }
        }
        updateSpatialIndex(false)
    }

    public synchronized Unit addChild(SLObjectInfo sLObjectInfo) {
        SLObjectInfo attachedTo
        this.treeNode.addChild(sLObjectInfo.treeNode)
        if (sLObjectInfo.isAttachment && (attachedTo = sLObjectInfo.getAttachedTo()) != null) {
            attachedTo.updateAttachments()
        }
    }

    fun clearDrawListEntry() {
        synchronized (this) {
            this.drawListEntry = null
        }
    }

    /* access modifiers changed from: protected */
    public abstract DrawListObjectEntry createDrawListEntry()

     public fun getAbsolutePosition(): LLVector3 {
        val parentObject: SLObjectInfo = getParentObject()
        val lLVector3: LLVector3 = this.objectCoords.get(0)
        if (parentObject == null) {
            return lLVector3
        }
        while (parentObject != null) {
            parentObject.objectCoords.addToVector(0, lLVector3)
            parentObject = parentObject.getParentObject()
        }
        return lLVector3
    }

     public fun getAttachedTo(): SLObjectInfo {
        val parentObject: SLObjectInfo = getParentObject()
        if (parentObject != null) {
            return parentObject.isAvatar() ? parentObject : parentObject.getAttachedTo()
        }
        return null
    }

     public fun getDescription(): String {
        return this.description
    }

     public fun getDrawListEntry(): DrawListObjectEntry {
        val weakReference: WeakReference<DrawListObjectEntry> = this.drawListEntry
        val drawListObjectEntry: DrawListObjectEntry = weakReference != null ? (DrawListObjectEntry) weakReference.get() : null
        if (drawListObjectEntry == null) {
            synchronized (this) {
                val weakReference2: WeakReference<DrawListObjectEntry> = this.drawListEntry
                drawListObjectEntry = weakReference2 != null ? (DrawListObjectEntry) weakReference2.get() : null
                if (drawListObjectEntry == null) {
                    drawListObjectEntry = createDrawListEntry()
                    this.drawListEntry = WeakReference<>(drawListObjectEntry)
                }
            }
        }
        return drawListObjectEntry
    }

     public fun getExistingDrawListEntry(): DrawListObjectEntry {
        val weakReference: WeakReference<DrawListObjectEntry> = this.drawListEntry
        if (weakReference != null) {
            return (DrawListObjectEntry) weakReference.get()
        }
        return null
    }

     public fun getHoverText(): HoverText {
        return this.hoverText
    }

     public fun getId(): UUID {
        return this.uuid
    }

     public fun getName(): String {
        return this.name
    }

     public fun getObjectCoords(): Vector3Array {
        return this.objectCoords
    }

    fun getObjectExtents(matrixStack: MatrixStack, z: Boolean, lLVector3: LLVector3, lLVector32: LLVector3) {
        val fArr: FloatArray = Float[8]
        val elementOffset: Int = this.objectCoords.getElementOffset(0)
        val elementOffset2: Int = this.objectCoords.getElementOffset(1)
        val data: FloatArray = this.objectCoords.getData()
        matrixStack.glPushMatrix()
        matrixStack.glTranslatef(data[elementOffset + 0], data[elementOffset + 1], data[elementOffset + 2])
        matrixStack.glMultMatrixf(this.rotation.getInverseMatrix(), 0)
        fArr[0] = (-data[elementOffset2 + 0]) / 2.0f
        fArr[1] = (-data[elementOffset2 + 1]) / 2.0f
        fArr[2] = (-data[elementOffset2 + 2]) / 2.0f
        fArr[3] = 1.0f
        Matrix.multiplyMV(fArr, 4, matrixStack.getMatrixData(), matrixStack.getMatrixDataOffset(), fArr, 0)
        if (z) {
            lLVector3.x = fArr[4]
            lLVector3.y = fArr[5]
            lLVector3.z = fArr[6]
            lLVector32.x = fArr[4]
            lLVector32.y = fArr[5]
            lLVector32.z = fArr[6]
        } else {
            lLVector3.x = Math.min(lLVector3.x, fArr[4])
            lLVector3.y = Math.min(lLVector3.y, fArr[5])
            lLVector3.z = Math.min(lLVector3.z, fArr[6])
            lLVector32.x = Math.max(lLVector32.x, fArr[4])
            lLVector32.y = Math.max(lLVector32.y, fArr[5])
            lLVector32.z = Math.max(lLVector32.z, fArr[6])
        }
        fArr[0] = data[elementOffset2 + 0] / 2.0f
        fArr[1] = data[elementOffset2 + 1] / 2.0f
        fArr[2] = data[elementOffset2 + 2] / 2.0f
        fArr[3] = 1.0f
        Matrix.multiplyMV(fArr, 4, matrixStack.getMatrixData(), matrixStack.getMatrixDataOffset(), fArr, 0)
        lLVector3.x = Math.min(lLVector3.x, fArr[4])
        lLVector3.y = Math.min(lLVector3.y, fArr[5])
        lLVector3.z = Math.min(lLVector3.z, fArr[6])
        lLVector32.x = Math.max(lLVector32.x, fArr[4])
        lLVector32.y = Math.max(lLVector32.y, fArr[5])
        lLVector32.z = Math.max(lLVector32.z, fArr[6])
        try {
            for (SLObjectInfo objectExtents : this.treeNode) {
                objectExtents.getObjectExtents(matrixStack, false, lLVector3, lLVector32)
            }
        } catch (NoSuchElementException e) {
            e.printStackTrace()
        }
        matrixStack.glPopMatrix()
    }

     public fun getOwnerUUID(): UUID {
        return (this.ownerUUID != null && this.ownerUUID.getLeastSignificantBits() == 0 && this.ownerUUID.getMostSignificantBits() == 0) ? this.creatorUUID : this.ownerUUID
    }

     public fun getParentObject(): SLObjectInfo {
        return this.treeNode.getParent()
    }

     public fun getPayInfo(): PayInfo {
        return this.payInfo
    }

     public fun getPrimDrawParams(): PrimDrawParams {
        return this.primDrawParams
    }

     public fun getRootPrim(): SLObjectInfo {
        val parent: SLObjectInfo = this.treeNode.getParent()
        return (parent != null && !parent.isAvatar()) ? parent.getRootPrim() : this
    }

    val LLQuaternion getRotation() {
        return this.rotation
    }

     public fun getTouchName(): String {
        return this.touchName
    }

     public fun hasTouchableChildren(): Boolean {
        try {
            for (SLObjectInfo isTouchable : this.treeNode) {
                if (isTouchable.isTouchable()) {
                    return true
                }
            }
            return false
        } catch (NoSuchElementException e) {
            e.printStackTrace()
            return false
        }
    }

    public abstract Boolean isAvatar()

    public synchronized Boolean isAvatarSittingOn() {
        try {
            for (SLObjectInfo next : this.treeNode) {
                if ((next instanceof SLObjectAvatarInfo) && ((SLObjectAvatarInfo) next).isMyAvatar()) {
                    return true
                }
            }
        } catch (NoSuchElementException e) {
            e.printStackTrace()
        }
        return false
    }

     public fun isMyAttachment(): Boolean {
        val parentObject: SLObjectInfo = getParentObject()
        if (parentObject instanceof SLObjectAvatarInfo) {
            return ((SLObjectAvatarInfo) parentObject).isMyAvatar()
        }
        return false
    }

    val Boolean isPayable() {
        return (this.UpdateFlags & 512) != 0
    }

    val Boolean isTouchable() {
        return (this.UpdateFlags & 128) != 0
    }

    /* access modifiers changed from: protected */
    fun onTexturesUpdate(sLTextureEntry: SLTextureEntry) {
    }

    public synchronized Unit removeChild(SLObjectInfo sLObjectInfo) {
        SLObjectInfo attachedTo
        if (sLObjectInfo.isAttachment && (attachedTo = sLObjectInfo.getAttachedTo()) != null) {
            attachedTo.updateAttachments()
        }
        this.treeNode.removeChild(sLObjectInfo.treeNode)
    }

    fun removeFromSpatialIndex() {
        val existingDrawListEntry: DrawListObjectEntry = getExistingDrawListEntry()
        if (existingDrawListEntry != null) {
            existingDrawListEntry.requestEntryRemoval()
        }
        if (!isAvatar()) {
            for (SLObjectInfo removeFromSpatialIndex : this.treeNode) {
                removeFromSpatialIndex.removeFromSpatialIndex()
            }
        }
    }

    public synchronized Unit setIsAttachmentAll(Boolean z) {
        this.isAttachment = z
        try {
            for (SLObjectInfo next : this.treeNode) {
                if (!next.isAvatar()) {
                    next.setIsAttachmentAll(z)
                }
            }
        } catch (NoSuchElementException e) {
            e.printStackTrace()
        }
        return
    }

    fun setPayInfo(payInfo2: PayInfo) {
        this.payInfo = payInfo2
    }

    fun updateSpatialIndex(z: Boolean) {
        updateSpatialIndex(SpatialIndex.getInstance().getObjectIndex(), z)
    }

    fun updateWorldMatrix(z: Boolean) {
        val parentObject: SLObjectInfo = getParentObject()
        val matrix: FloatArray = parentObject == null ? IdentityMatrix.getMatrix() : parentObject.isAvatar() ? IdentityMatrix.getMatrix() : parentObject.worldMatrix
        if (matrix != null) {
            this.objRadius = this.objectCoords.getMaxComponent(1) / 2.0f
            val calculateWorldMatrix: FloatArray = calculateWorldMatrix(matrix)
            val fArr: FloatArray = this.worldMatrix
            if (fArr == null || !Arrays.equals(calculateWorldMatrix, fArr)) {
                this.worldMatrix = calculateWorldMatrix
                this.objectCoords.set(3, this.worldMatrix[12], this.worldMatrix[13], this.worldMatrix[14])
                if (z) {
                    for (SLObjectInfo updateWorldMatrix : this.treeNode) {
                        updateWorldMatrix.updateWorldMatrix(true)
                    }
                }
            }
        }
    }
}
