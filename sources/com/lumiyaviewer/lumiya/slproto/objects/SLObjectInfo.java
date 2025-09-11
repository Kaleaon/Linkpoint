package com.lumiyaviewer.lumiya.slproto.objects;

import android.opengl.Matrix;
import com.google.common.base.Ascii;
import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.render.DrawableObject;
import com.lumiyaviewer.lumiya.render.MatrixStack;
import com.lumiyaviewer.lumiya.render.avatar.DrawableAvatar;
import com.lumiyaviewer.lumiya.render.spatial.DrawListObjectEntry;
import com.lumiyaviewer.lumiya.render.spatial.DrawListPrimEntry;
import com.lumiyaviewer.lumiya.render.spatial.SpatialIndex;
import com.lumiyaviewer.lumiya.render.spatial.SpatialObjectIndex;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.messages.ImprovedTerseObjectUpdate;
import com.lumiyaviewer.lumiya.slproto.messages.ObjectProperties;
import com.lumiyaviewer.lumiya.slproto.messages.ObjectUpdate;
import com.lumiyaviewer.lumiya.slproto.messages.ObjectUpdateCompressed;
import com.lumiyaviewer.lumiya.slproto.prims.PrimDrawParams;
import com.lumiyaviewer.lumiya.slproto.prims.PrimParamsPool;
import com.lumiyaviewer.lumiya.slproto.prims.PrimVolumeParams;
import com.lumiyaviewer.lumiya.slproto.textures.SLTextureEntry;
import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import com.lumiyaviewer.lumiya.slproto.types.Vector3Array;
import com.lumiyaviewer.lumiya.utils.Identifiable;
import com.lumiyaviewer.lumiya.utils.IdentityMatrix;
import com.lumiyaviewer.lumiya.utils.LinkedTreeNode;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class SLObjectInfo implements Identifiable<UUID> {
    private static final int AGENT_ATTACH_MASK = 240;
    private static final int AGENT_ATTACH_OFFSET = 4;
    public static final int FLAGS_ALLOW_INVENTORY_DROP = 65536;
    public static final int FLAGS_ANIM_SOURCE = 2097152;
    public static final int FLAGS_CAMERA_DECOUPLED = 1048576;
    public static final int FLAGS_CAMERA_SOURCE = 4194304;
    public static final int FLAGS_CAST_SHADOWS = 8388608;
    public static final int FLAGS_CREATE_SELECTED = 2;
    public static final int FLAGS_HANDLE_TOUCH = 128;
    public static final int FLAGS_INCLUDE_IN_SEARCH = 32768;
    public static final int FLAGS_INVENTORY_EMPTY = 2048;
    public static final int FLAGS_JOINT_HINGE = 4096;
    public static final int FLAGS_JOINT_LP2P = 16384;
    public static final int FLAGS_JOINT_P2P = 8192;
    public static final int FLAGS_OBJECT_ANY_OWNER = 16;
    public static final int FLAGS_OBJECT_COPY = 8;
    public static final int FLAGS_OBJECT_GROUP_OWNED = 262144;
    public static final int FLAGS_OBJECT_MODIFY = 4;
    public static final int FLAGS_OBJECT_MOVE = 256;
    public static final int FLAGS_OBJECT_OWNER_MODIFY = 268435456;
    public static final int FLAGS_OBJECT_TRANSFER = 131072;
    public static final int FLAGS_OBJECT_YOU_OWNER = 32;
    public static final int FLAGS_PHANTOM = 1024;
    public static final int FLAGS_SCRIPTED = 64;
    public static final int FLAGS_TAKES_MONEY = 512;
    public static final int FLAGS_TEMPORARY = 1073741824;
    public static final int FLAGS_TEMPORARY_ON_REZ = 536870912;
    public static final int FLAGS_USE_PHYSICS = 1;
    public static final int FLAGS_ZLIB_COMPRESSED = Integer.MIN_VALUE;
    public static final int OBJ_COORD_POSITION = 0;
    public static final int OBJ_COORD_SCALE = 1;
    public static final int OBJ_COORD_VELOCITY = 2;
    public static final int OBJ_COORD_WORLD_CENTER = 3;
    public static final int PAY_DEFAULT = -2;
    public static final int PAY_HIDE = -1;
    public int UpdateFlags;
    public UUID attachedToUUID = null;
    public int attachmentID = 0;
    public UUID creatorUUID = null;
    public String description = "";
    @Nullable
    private volatile WeakReference<DrawListObjectEntry> drawListEntry;
    public int hierLevel = 0;
    private volatile HoverText hoverText = null;
    public boolean isAttachment = false;
    public volatile boolean isDead = false;
    public int localID;
    public String name = "(loading)";
    public boolean nameKnown = false;
    public boolean nameRequested = false;
    public long nameRequestedAt = 0;
    public float objRadius;
    private final Vector3Array objectCoords = new Vector3Array(4);
    public UUID ownerUUID = null;
    public int parentID = 0;
    @Nullable
    private PayInfo payInfo;
    private PrimDrawParams primDrawParams;
    private LLQuaternion rotation;
    public int salePrice;
    public byte saleType = 0;
    public String touchName = "";
    public final LinkedTreeNode<SLObjectInfo> treeNode = new LinkedTreeNode<>(this);
    protected UUID uuid;
    public float[] worldMatrix;

    private void ParseObjectData(ByteBuffer buffer) {
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        int bufferSize = buffer.limit();
        
        switch (bufferSize) {
            case 76:
                // Skip first 16 bytes for this size
                buffer.position(buffer.position() + 16);
                // Fall through to 60 case
            case 60:
                // Parse position and scale as float vectors
                this.objectCoords.set(0, LLVector3.parseFloatVec(buffer));
                this.objectCoords.set(2, LLVector3.parseFloatVec(buffer));
                // Skip 12 bytes
                buffer.position(buffer.position() + 12);
                // Parse rotation as float quaternion
                this.rotation = LLQuaternion.parseFloatVec3(buffer);
                break;
                
            case 48:
                // Skip first 16 bytes for this size
                buffer.position(buffer.position() + 16);
                // Fall through to 32 case
            case 32:
                // Parse position and scale as U16 vectors with specific ranges
                this.objectCoords.set(0, LLVector3.parseU16Vec(buffer, -128.0f, 384.0f, -256.0f, 4096.0f));
                this.objectCoords.set(2, LLVector3.parseU16Vec(buffer, -256.0f, 256.0f, -256.0f, 256.0f));
                // Skip 6 bytes
                buffer.position(buffer.position() + 6);
                // Parse rotation as U16 quaternion
                this.rotation = LLQuaternion.parseU16Vec3(buffer, -1.0f, 1.0f);
                break;
                
            case 16:
                // Parse position and scale as U8 vectors with specific ranges
                this.objectCoords.set(0, LLVector3.parseU8Vec(buffer, 384.0f, 384.0f, -256.0f, 4096.0f));
                this.objectCoords.set(2, LLVector3.parseU8Vec(buffer, -256.0f, 256.0f, -256.0f, 256.0f));
                // Skip 3 bytes
                buffer.position(buffer.position() + 3);
                // Parse rotation as U8 quaternion
                this.rotation = LLQuaternion.parseU8Vec3(buffer, -1.0f, 1.0f);
                break;
                
            default:
                // Unknown buffer size, do nothing
                break;
        }
    }

    private void applyHoverText(@Nullable HoverText hoverText2) {
        if (!Objects.equal(this.hoverText, hoverText2)) {
            this.hoverText = hoverText2;
            DrawableObject drawableObject = getDrawableObject();
            if (drawableObject != null) {
                drawableObject.setHoverText(hoverText2);
            }
        }
    }

    private static int attachmentIDFromState(int i) {
        return (((i & 255) & AGENT_ATTACH_MASK) >> 4) | (((i & 255) & -241) << 4);
    }

    private float[] calculateWorldMatrix(float[] fArr) {
        LLQuaternion lLQuaternion = this.rotation;
        if (lLQuaternion == null) {
            return null;
        }
        float[] fArr2 = new float[16];
        float[] fArr3 = new float[16];
        this.objectCoords.MatrixTranslate(fArr3, 0, fArr, 0, 0);
        Matrix.multiplyMM(fArr2, 0, fArr3, 0, lLQuaternion.getInverseMatrix(), 0);
        return fArr2;
    }

    public static SLObjectInfo create(ObjectUpdateCompressed.ObjectData objectData) throws UnsupportedObjectTypeException {
        SLObjectPrimInfo sLObjectPrimInfo = new SLObjectPrimInfo();
        sLObjectPrimInfo.ApplyObjectUpdate(objectData);
        return sLObjectPrimInfo;
    }

    public static SLObjectInfo create(UUID uuid2, ObjectUpdate.ObjectData objectData, UUID uuid3) {
        SLObjectInfo sLObjectAvatarInfo = objectData.PCode == 47 ? new SLObjectAvatarInfo(uuid2, UUIDPool.getUUID(objectData.FullID), uuid3.equals(objectData.FullID)) : new SLObjectPrimInfo();
        sLObjectAvatarInfo.ApplyObjectUpdate(objectData);
        return sLObjectAvatarInfo;
    }

    @Nullable
    private DrawableObject getDrawableObject() {
        DrawListObjectEntry existingDrawListEntry = getExistingDrawListEntry();
        if (existingDrawListEntry instanceof DrawListPrimEntry) {
            return ((DrawListPrimEntry) existingDrawListEntry).getDrawableObject();
        }
        return null;
    }

    public static int getLocalID(ImprovedTerseObjectUpdate.ObjectData objectData) {
        ByteBuffer wrap = ByteBuffer.wrap(objectData.Data);
        wrap.order(ByteOrder.LITTLE_ENDIAN);
        return wrap.getInt();
    }

    public static int getLocalID(ObjectUpdateCompressed.ObjectData objectData) {
        ByteBuffer wrap = ByteBuffer.wrap(objectData.Data);
        wrap.position(16);
        wrap.order(ByteOrder.LITTLE_ENDIAN);
        return wrap.getInt();
    }

    private void parseNameValuePairs(String str) {
        for (String str2 : str.split("\n")) {
            if (str2.startsWith("AttachItemID ")) {
                int i = 0;
                while (i < 4) {
                    int indexOf = str2.indexOf(32);
                    if (indexOf >= 0) {
                        str2 = str2.substring(indexOf + 1);
                    }
                    i++;
                    str2 = str2.trim();
                }
                try {
                    this.attachedToUUID = UUIDPool.getUUID(UUID.fromString(str2));
                } catch (Exception e) {
                    this.attachedToUUID = null;
                }
            } else if (str2.startsWith("DisplayName ")) {
                int i2 = 0;
                while (i2 < 4) {
                    int indexOf2 = str2.indexOf(32);
                    if (indexOf2 >= 0) {
                        str2 = str2.substring(indexOf2 + 1);
                    }
                    i2++;
                    str2 = str2.trim();
                }
                this.name = str2;
                this.nameKnown = true;
            }
        }
    }

    private void updateAttachments() {
        DrawableAvatar drawableAvatar;
        if (isAvatar() && (drawableAvatar = SpatialIndex.getInstance().getDrawableAvatar(this)) != null) {
            drawableAvatar.updateAttachments();
        }
    }

    private void updateSpatialIndex(SpatialObjectIndex spatialObjectIndex, boolean z) {
        updateWorldMatrix(false);
        if (z) {
            synchronized (this) {
                this.drawListEntry = null;
            }
        }
        if (spatialObjectIndex != null && (!this.isDead)) {
            spatialObjectIndex.updateObject(getDrawListEntry());
        }
        if (!isAvatar()) {
            for (SLObjectInfo updateSpatialIndex : this.treeNode) {
                updateSpatialIndex.updateSpatialIndex(z);
            }
            return;
        }
        for (SLObjectInfo updateWorldMatrix : this.treeNode) {
            updateWorldMatrix.updateWorldMatrix(true);
        }
    }

    public void ApplyObjectProperties(ObjectProperties.ObjectData objectData) {
        this.name = SLMessage.stringFromVariableOEM(objectData.Name);
        this.description = SLMessage.stringFromVariableUTF(objectData.Description);
        this.touchName = SLMessage.stringFromVariableUTF(objectData.TouchName);
        this.creatorUUID = objectData.CreatorID;
        this.ownerUUID = objectData.OwnerID;
        this.saleType = (byte) objectData.SaleType;
        this.salePrice = objectData.SalePrice;
        this.nameKnown = true;
        this.nameRequested = false;
    }

    public void ApplyObjectUpdate(ObjectUpdate.ObjectData objectData) {
        PrimVolumeParams primVolumeParams = null;
        this.localID = objectData.ID;
        this.uuid = UUIDPool.getUUID(objectData.FullID);
        this.UpdateFlags = objectData.UpdateFlags;
        this.parentID = objectData.ParentID;
        this.attachmentID = attachmentIDFromState(objectData.State);
        if (!(objectData.OwnerID.getLeastSignificantBits() == 0 && objectData.OwnerID.getMostSignificantBits() == 0)) {
            this.ownerUUID = UUIDPool.getUUID(objectData.OwnerID);
        }
        this.objectCoords.set(1, objectData.Scale);
        String stringFromVariableOEM = SLMessage.stringFromVariableOEM(objectData.Text);
        applyHoverText(Strings.isNullOrEmpty(stringFromVariableOEM) ? null : HoverText.create(stringFromVariableOEM, objectData.TextColor.length >= 4 ? (objectData.TextColor[0] & UnsignedBytes.MAX_VALUE) | ((objectData.TextColor[1] << 8) & 65280) | ((objectData.TextColor[2] << 16) & 16711680) | ((objectData.TextColor[3] << Ascii.CAN) & -16777216) : 0));
        PrimVolumeParams createFromObjectUpdate = PrimVolumeParams.createFromObjectUpdate(objectData);
        if (!(createFromObjectUpdate == null || objectData.ExtraParams == null)) {
            createFromObjectUpdate.unpackExtraParams(ByteBuffer.wrap(objectData.ExtraParams).order(ByteOrder.LITTLE_ENDIAN));
        }
        ParseObjectData(ByteBuffer.wrap(objectData.ObjectData));
        if (createFromObjectUpdate != null) {
            primVolumeParams = PrimParamsPool.get(createFromObjectUpdate);
        }
        PrimDrawParams primDrawParams2 = PrimParamsPool.get(new PrimDrawParams(primVolumeParams, SLTextureEntry.create(ByteBuffer.wrap(objectData.TextureEntry), objectData.TextureEntry.length)));
        onTexturesUpdate(primDrawParams2.getTextures());
        if (!Objects.equal(this.primDrawParams, primDrawParams2)) {
            this.primDrawParams = primDrawParams2;
            DrawableObject drawableObject = getDrawableObject();
            if (drawableObject != null) {
                drawableObject.setPrimDrawParams(this.primDrawParams);
            }
        }
        this.primDrawParams = PrimParamsPool.get(primDrawParams2);
        parseNameValuePairs(SLMessage.stringFromVariableUTF(objectData.NameValue));
        updateSpatialIndex(false);
    }

    /* JADX WARNING: Removed duplicated region for block: B:65:0x017e  */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x0188  */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x019b  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void ApplyObjectUpdate(com.lumiyaviewer.lumiya.slproto.messages.ObjectUpdateCompressed.ObjectData r13) throws com.lumiyaviewer.lumiya.slproto.objects.UnsupportedObjectTypeException {
        /*
            r12 = this;
            r10 = 0
            r3 = 0
            r1 = 0
            int r0 = r13.UpdateFlags
            r12.UpdateFlags = r0
            byte[] r0 = r13.Data
            java.nio.ByteBuffer r4 = java.nio.ByteBuffer.wrap(r0)
            java.nio.ByteOrder r0 = java.nio.ByteOrder.BIG_ENDIAN
            r4.order(r0)
            long r6 = r4.getLong()
            long r8 = r4.getLong()
            java.util.UUID r0 = r12.uuid
            java.util.UUID r0 = com.lumiyaviewer.lumiya.utils.UUIDPool.setUUID(r0, r6, r8)
            r12.uuid = r0
            java.nio.ByteOrder r0 = java.nio.ByteOrder.LITTLE_ENDIAN
            r4.order(r0)
            int r0 = r4.getInt()
            r12.localID = r0
            byte r0 = r4.get()
            r2 = 9
            if (r0 == r2) goto L_0x003c
            com.lumiyaviewer.lumiya.slproto.objects.UnsupportedObjectTypeException r1 = new com.lumiyaviewer.lumiya.slproto.objects.UnsupportedObjectTypeException
            r1.<init>(r0)
            throw r1
        L_0x003c:
            byte r0 = r4.get()
            int r0 = attachmentIDFromState(r0)
            r12.attachmentID = r0
            int r0 = r4.position()
            int r0 = r0 + 4
            int r0 = r0 + 1
            int r0 = r0 + 1
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
            int r5 = r4.getInt()
            java.nio.ByteOrder r0 = java.nio.ByteOrder.BIG_ENDIAN
            r4.order(r0)
            long r6 = r4.getLong()
            long r8 = r4.getLong()
            java.util.UUID r0 = r12.ownerUUID
            if (r0 == 0) goto L_0x0089
            int r0 = (r6 > r10 ? 1 : (r6 == r10 ? 0 : -1))
            if (r0 == 0) goto L_0x0091
            int r0 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1))
            if (r0 == 0) goto L_0x0091
        L_0x0089:
            java.util.UUID r0 = r12.ownerUUID
            java.util.UUID r0 = com.lumiyaviewer.lumiya.utils.UUIDPool.setUUID(r0, r6, r8)
            r12.ownerUUID = r0
        L_0x0091:
            java.nio.ByteOrder r0 = java.nio.ByteOrder.LITTLE_ENDIAN
            r4.order(r0)
            r0 = r5 & 128(0x80, float:1.794E-43)
            if (r0 == 0) goto L_0x00a3
            int r0 = r4.position()
            int r0 = r0 + 12
            r4.position(r0)
        L_0x00a3:
            r0 = r5 & 32
            if (r0 == 0) goto L_0x00ad
            int r0 = r4.getInt()
            r12.parentID = r0
        L_0x00ad:
            r0 = r5 & 2
            if (r0 == 0) goto L_0x012c
            int r0 = r4.position()
            int r0 = r0 + 1
            r4.position(r0)
        L_0x00ba:
            r0 = r5 & 4
            if (r0 == 0) goto L_0x00f6
            int r6 = r4.position()
            r0 = r1
        L_0x00c3:
            int r2 = r6 + r0
            int r7 = r4.capacity()
            if (r2 >= r7) goto L_0x00d3
            int r2 = r6 + r0
            byte r2 = r4.get(r2)
            if (r2 != 0) goto L_0x013e
        L_0x00d3:
            if (r0 == 0) goto L_0x01b7
            byte[] r7 = new byte[r0]
            r4.get(r7, r1, r0)
            java.lang.String r2 = new java.lang.String     // Catch:{ UnsupportedEncodingException -> 0x0141 }
            java.lang.String r8 = "ISO-8859-1"
            r2.<init>(r7, r8)     // Catch:{ UnsupportedEncodingException -> 0x0141 }
        L_0x00e2:
            int r0 = r0 + r6
            int r0 = r0 + 1
            r4.position(r0)
            int r0 = r4.getInt()
            boolean r6 = com.google.common.base.Strings.isNullOrEmpty(r2)
            if (r6 == 0) goto L_0x0144
            r0 = r3
        L_0x00f3:
            r12.applyHoverText(r0)
        L_0x00f6:
            r0 = r5 & 512(0x200, float:7.175E-43)
            if (r0 == 0) goto L_0x0100
        L_0x00fa:
            byte r0 = r4.get()
            if (r0 != 0) goto L_0x00fa
        L_0x0100:
            r0 = r5 & 8
            if (r0 == 0) goto L_0x010d
            int r0 = r4.position()
            int r0 = r0 + 86
            r4.position(r0)
        L_0x010d:
            int r2 = r4.position()
            byte r0 = r4.get()
            r6 = r0 & 255(0xff, float:3.57E-43)
            r0 = r1
        L_0x0118:
            if (r0 >= r6) goto L_0x0149
            r4.getShort()
            int r7 = r4.getInt()
            int r8 = r4.position()
            int r7 = r7 + r8
            r4.position(r7)
            int r0 = r0 + 1
            goto L_0x0118
        L_0x012c:
            r0 = r5 & 1
            if (r0 == 0) goto L_0x00ba
            byte r0 = r4.get()
            int r2 = r4.position()
            int r0 = r0 + r2
            r4.position(r0)
            goto L_0x00ba
        L_0x013e:
            int r0 = r0 + 1
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
            int r0 = r4.position()
            int r0 = r0 + 16
            r4.position(r0)
            int r0 = r4.position()
            int r0 = r0 + 4
            int r0 = r0 + 1
            int r0 = r0 + 4
            r4.position(r0)
        L_0x0163:
            r0 = r5 & 256(0x100, float:3.59E-43)
            if (r0 == 0) goto L_0x016d
        L_0x0167:
            byte r0 = r4.get()
            if (r0 != 0) goto L_0x0167
        L_0x016d:
            com.lumiyaviewer.lumiya.slproto.prims.PrimVolumeParams r5 = com.lumiyaviewer.lumiya.slproto.prims.PrimVolumeParams.createFromPackedData(r4)
            int r0 = r4.getInt()     // Catch:{ Exception -> 0x01ac }
            com.lumiyaviewer.lumiya.slproto.textures.SLTextureEntry r0 = com.lumiyaviewer.lumiya.slproto.textures.SLTextureEntry.create((java.nio.ByteBuffer) r4, (int) r0)     // Catch:{ Exception -> 0x01ac }
            r12.onTexturesUpdate(r0)     // Catch:{ Exception -> 0x01b5 }
        L_0x017c:
            if (r5 == 0) goto L_0x0184
            r4.position(r2)
            r5.unpackExtraParams(r4)
        L_0x0184:
            com.lumiyaviewer.lumiya.slproto.prims.PrimDrawParams r2 = new com.lumiyaviewer.lumiya.slproto.prims.PrimDrawParams
            if (r5 == 0) goto L_0x018c
            com.lumiyaviewer.lumiya.slproto.prims.PrimVolumeParams r3 = com.lumiyaviewer.lumiya.slproto.prims.PrimParamsPool.get((com.lumiyaviewer.lumiya.slproto.prims.PrimVolumeParams) r5)
        L_0x018c:
            r2.<init>(r3, r0)
            com.lumiyaviewer.lumiya.slproto.prims.PrimDrawParams r0 = com.lumiyaviewer.lumiya.slproto.prims.PrimParamsPool.get((com.lumiyaviewer.lumiya.slproto.prims.PrimDrawParams) r2)
            com.lumiyaviewer.lumiya.slproto.prims.PrimDrawParams r2 = r12.primDrawParams
            boolean r2 = com.google.common.base.Objects.equal(r2, r0)
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
        throw new UnsupportedOperationException("Method not decompiled: com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo.ApplyObjectUpdate(com.lumiyaviewer.lumiya.slproto.messages.ObjectUpdateCompressed$ObjectData):void");
    }

    public void ApplyTerseObjectUpdate(ImprovedTerseObjectUpdate.ObjectData objectData) {
        ByteBuffer wrap = ByteBuffer.wrap(objectData.Data);
        wrap.order(ByteOrder.LITTLE_ENDIAN);
        wrap.getInt();
        this.attachmentID = attachmentIDFromState(wrap.get());
        if (wrap.get() != 0) {
            wrap.position(wrap.position() + 16);
        }
        LLVector3 parseFloatVec = LLVector3.parseFloatVec(wrap);
        LLVector3 parseU16Vec = LLVector3.parseU16Vec(wrap, -128.0f, 128.0f, -128.0f, 128.0f);
        this.objectCoords.set(0, parseFloatVec);
        this.objectCoords.set(2, parseU16Vec);
        wrap.position(wrap.position() + 6);
        this.rotation = LLQuaternion.parseU16Vec3(wrap, -1.0f, 1.0f);
        wrap.position(wrap.position() + 6);
        if (objectData.TextureEntry.length > 4) {
            ByteBuffer wrap2 = ByteBuffer.wrap(objectData.TextureEntry);
            wrap2.position(4);
            SLTextureEntry create = SLTextureEntry.create(wrap2, wrap2.remaining());
            onTexturesUpdate(create);
            PrimDrawParams primDrawParams2 = this.primDrawParams;
            if (primDrawParams2 != null && !create.equals(primDrawParams2.getTextures())) {
                PrimDrawParams primDrawParams3 = PrimParamsPool.get(new PrimDrawParams(primDrawParams2.getVolumeParams(), create));
                if (!Objects.equal(this.primDrawParams, primDrawParams3)) {
                    this.primDrawParams = primDrawParams3;
                    DrawableObject drawableObject = getDrawableObject();
                    if (drawableObject != null) {
                        drawableObject.setPrimDrawParams(this.primDrawParams);
                    }
                }
            }
        }
        updateSpatialIndex(false);
    }

    public synchronized void addChild(SLObjectInfo sLObjectInfo) {
        SLObjectInfo attachedTo;
        this.treeNode.addChild(sLObjectInfo.treeNode);
        if (sLObjectInfo.isAttachment && (attachedTo = sLObjectInfo.getAttachedTo()) != null) {
            attachedTo.updateAttachments();
        }
    }

    public void clearDrawListEntry() {
        synchronized (this) {
            this.drawListEntry = null;
        }
    }

    /* access modifiers changed from: protected */
    @Nonnull
    public abstract DrawListObjectEntry createDrawListEntry();

    public LLVector3 getAbsolutePosition() {
        SLObjectInfo parentObject = getParentObject();
        LLVector3 lLVector3 = this.objectCoords.get(0);
        if (parentObject == null) {
            return lLVector3;
        }
        while (parentObject != null) {
            parentObject.objectCoords.addToVector(0, lLVector3);
            parentObject = parentObject.getParentObject();
        }
        return lLVector3;
    }

    public SLObjectInfo getAttachedTo() {
        SLObjectInfo parentObject = getParentObject();
        if (parentObject != null) {
            return parentObject.isAvatar() ? parentObject : parentObject.getAttachedTo();
        }
        return null;
    }

    public String getDescription() {
        return this.description;
    }

    @Nonnull
    public DrawListObjectEntry getDrawListEntry() {
        WeakReference<DrawListObjectEntry> weakReference = this.drawListEntry;
        DrawListObjectEntry drawListObjectEntry = weakReference != null ? (DrawListObjectEntry) weakReference.get() : null;
        if (drawListObjectEntry == null) {
            synchronized (this) {
                WeakReference<DrawListObjectEntry> weakReference2 = this.drawListEntry;
                drawListObjectEntry = weakReference2 != null ? (DrawListObjectEntry) weakReference2.get() : null;
                if (drawListObjectEntry == null) {
                    drawListObjectEntry = createDrawListEntry();
                    this.drawListEntry = new WeakReference<>(drawListObjectEntry);
                }
            }
        }
        return drawListObjectEntry;
    }

    @Nullable
    public DrawListObjectEntry getExistingDrawListEntry() {
        WeakReference<DrawListObjectEntry> weakReference = this.drawListEntry;
        if (weakReference != null) {
            return (DrawListObjectEntry) weakReference.get();
        }
        return null;
    }

    @Nullable
    public HoverText getHoverText() {
        return this.hoverText;
    }

    public UUID getId() {
        return this.uuid;
    }

    public String getName() {
        return this.name;
    }

    public Vector3Array getObjectCoords() {
        return this.objectCoords;
    }

    public void getObjectExtents(MatrixStack matrixStack, boolean z, LLVector3 lLVector3, LLVector3 lLVector32) {
        float[] fArr = new float[8];
        int elementOffset = this.objectCoords.getElementOffset(0);
        int elementOffset2 = this.objectCoords.getElementOffset(1);
        float[] data = this.objectCoords.getData();
        matrixStack.glPushMatrix();
        matrixStack.glTranslatef(data[elementOffset + 0], data[elementOffset + 1], data[elementOffset + 2]);
        matrixStack.glMultMatrixf(this.rotation.getInverseMatrix(), 0);
        fArr[0] = (-data[elementOffset2 + 0]) / 2.0f;
        fArr[1] = (-data[elementOffset2 + 1]) / 2.0f;
        fArr[2] = (-data[elementOffset2 + 2]) / 2.0f;
        fArr[3] = 1.0f;
        Matrix.multiplyMV(fArr, 4, matrixStack.getMatrixData(), matrixStack.getMatrixDataOffset(), fArr, 0);
        if (z) {
            lLVector3.x = fArr[4];
            lLVector3.y = fArr[5];
            lLVector3.z = fArr[6];
            lLVector32.x = fArr[4];
            lLVector32.y = fArr[5];
            lLVector32.z = fArr[6];
        } else {
            lLVector3.x = Math.min(lLVector3.x, fArr[4]);
            lLVector3.y = Math.min(lLVector3.y, fArr[5]);
            lLVector3.z = Math.min(lLVector3.z, fArr[6]);
            lLVector32.x = Math.max(lLVector32.x, fArr[4]);
            lLVector32.y = Math.max(lLVector32.y, fArr[5]);
            lLVector32.z = Math.max(lLVector32.z, fArr[6]);
        }
        fArr[0] = data[elementOffset2 + 0] / 2.0f;
        fArr[1] = data[elementOffset2 + 1] / 2.0f;
        fArr[2] = data[elementOffset2 + 2] / 2.0f;
        fArr[3] = 1.0f;
        Matrix.multiplyMV(fArr, 4, matrixStack.getMatrixData(), matrixStack.getMatrixDataOffset(), fArr, 0);
        lLVector3.x = Math.min(lLVector3.x, fArr[4]);
        lLVector3.y = Math.min(lLVector3.y, fArr[5]);
        lLVector3.z = Math.min(lLVector3.z, fArr[6]);
        lLVector32.x = Math.max(lLVector32.x, fArr[4]);
        lLVector32.y = Math.max(lLVector32.y, fArr[5]);
        lLVector32.z = Math.max(lLVector32.z, fArr[6]);
        try {
            for (SLObjectInfo objectExtents : this.treeNode) {
                objectExtents.getObjectExtents(matrixStack, false, lLVector3, lLVector32);
            }
        } catch (NoSuchElementException e) {
            e.printStackTrace();
        }
        matrixStack.glPopMatrix();
    }

    public UUID getOwnerUUID() {
        return (this.ownerUUID != null && this.ownerUUID.getLeastSignificantBits() == 0 && this.ownerUUID.getMostSignificantBits() == 0) ? this.creatorUUID : this.ownerUUID;
    }

    public SLObjectInfo getParentObject() {
        return this.treeNode.getParent();
    }

    @Nullable
    public PayInfo getPayInfo() {
        return this.payInfo;
    }

    public PrimDrawParams getPrimDrawParams() {
        return this.primDrawParams;
    }

    public SLObjectInfo getRootPrim() {
        SLObjectInfo parent = this.treeNode.getParent();
        return (parent != null && !parent.isAvatar()) ? parent.getRootPrim() : this;
    }

    public final LLQuaternion getRotation() {
        return this.rotation;
    }

    public String getTouchName() {
        return this.touchName;
    }

    public boolean hasTouchableChildren() {
        try {
            for (SLObjectInfo isTouchable : this.treeNode) {
                if (isTouchable.isTouchable()) {
                    return true;
                }
            }
            return false;
        } catch (NoSuchElementException e) {
            e.printStackTrace();
            return false;
        }
    }

    public abstract boolean isAvatar();

    public synchronized boolean isAvatarSittingOn() {
        try {
            for (SLObjectInfo next : this.treeNode) {
                if ((next instanceof SLObjectAvatarInfo) && ((SLObjectAvatarInfo) next).isMyAvatar()) {
                    return true;
                }
            }
        } catch (NoSuchElementException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isMyAttachment() {
        SLObjectInfo parentObject = getParentObject();
        if (parentObject instanceof SLObjectAvatarInfo) {
            return ((SLObjectAvatarInfo) parentObject).isMyAvatar();
        }
        return false;
    }

    public final boolean isPayable() {
        return (this.UpdateFlags & 512) != 0;
    }

    public final boolean isTouchable() {
        return (this.UpdateFlags & 128) != 0;
    }

    /* access modifiers changed from: protected */
    public void onTexturesUpdate(SLTextureEntry sLTextureEntry) {
    }

    public synchronized void removeChild(SLObjectInfo sLObjectInfo) {
        SLObjectInfo attachedTo;
        if (sLObjectInfo.isAttachment && (attachedTo = sLObjectInfo.getAttachedTo()) != null) {
            attachedTo.updateAttachments();
        }
        this.treeNode.removeChild(sLObjectInfo.treeNode);
    }

    public void removeFromSpatialIndex() {
        DrawListObjectEntry existingDrawListEntry = getExistingDrawListEntry();
        if (existingDrawListEntry != null) {
            existingDrawListEntry.requestEntryRemoval();
        }
        if (!isAvatar()) {
            for (SLObjectInfo removeFromSpatialIndex : this.treeNode) {
                removeFromSpatialIndex.removeFromSpatialIndex();
            }
        }
    }

    public synchronized void setIsAttachmentAll(boolean z) {
        this.isAttachment = z;
        try {
            for (SLObjectInfo next : this.treeNode) {
                if (!next.isAvatar()) {
                    next.setIsAttachmentAll(z);
                }
            }
        } catch (NoSuchElementException e) {
            e.printStackTrace();
        }
        return;
    }

    public void setPayInfo(@Nullable PayInfo payInfo2) {
        this.payInfo = payInfo2;
    }

    public void updateSpatialIndex(boolean z) {
        updateSpatialIndex(SpatialIndex.getInstance().getObjectIndex(), z);
    }

    public void updateWorldMatrix(boolean z) {
        SLObjectInfo parentObject = getParentObject();
        float[] matrix = parentObject == null ? IdentityMatrix.getMatrix() : parentObject.isAvatar() ? IdentityMatrix.getMatrix() : parentObject.worldMatrix;
        if (matrix != null) {
            this.objRadius = this.objectCoords.getMaxComponent(1) / 2.0f;
            float[] calculateWorldMatrix = calculateWorldMatrix(matrix);
            float[] fArr = this.worldMatrix;
            if (fArr == null || !Arrays.equals(calculateWorldMatrix, fArr)) {
                this.worldMatrix = calculateWorldMatrix;
                this.objectCoords.set(3, this.worldMatrix[12], this.worldMatrix[13], this.worldMatrix[14]);
                if (z) {
                    for (SLObjectInfo updateWorldMatrix : this.treeNode) {
                        updateWorldMatrix.updateWorldMatrix(true);
                    }
                }
            }
        }
    }
}
