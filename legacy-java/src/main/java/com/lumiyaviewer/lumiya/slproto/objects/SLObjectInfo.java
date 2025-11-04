package com.lumiyaviewer.lumiya.slproto.objects;

import android.opengl.Matrix;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
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
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/* loaded from: classes.dex */
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

    @Nullable
    private volatile WeakReference<DrawListObjectEntry> drawListEntry;
    public int localID;
    public float objRadius;

    @Nullable
    private PayInfo payInfo;
    private PrimDrawParams primDrawParams;
    private LLQuaternion rotation;
    public int salePrice;
    protected UUID uuid;
    public float[] worldMatrix;
    public int parentID = 0;
    public String name = "(loading)";
    public String description = "";
    public String touchName = "";
    public UUID attachedToUUID = null;
    public UUID ownerUUID = null;
    public UUID creatorUUID = null;
    public byte saleType = 0;
    public int attachmentID = 0;
    private final Vector3Array objectCoords = new Vector3Array(4);
    private volatile HoverText hoverText = null;
    public volatile boolean isDead = false;
    public boolean isAttachment = false;
    public boolean nameKnown = false;
    public boolean nameRequested = false;
    public long nameRequestedAt = 0;
    public int hierLevel = 0;
    public final LinkedTreeNode<SLObjectInfo> treeNode = new LinkedTreeNode<>(this);

    /* JADX WARN: Failed to find 'out' block for switch in B:2:0x0012. Please report as an issue. */
    private void ParseObjectData(ByteBuffer byteBuffer) {
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        switch (byteBuffer.limit()) {
            case 16:
                this.objectCoords.set(0, LLVector3.parseU8Vec(byteBuffer, 384.0f, 384.0f, -256.0f, 4096.0f));
                this.objectCoords.set(2, LLVector3.parseU8Vec(byteBuffer, -256.0f, 256.0f, -256.0f, 256.0f));
                byteBuffer.position(byteBuffer.position() + 3);
                this.rotation = LLQuaternion.parseU8Vec3(byteBuffer, -1.0f, 1.0f);
                return;
            case 48:
                byteBuffer.position(byteBuffer.position() + 16);
            case 32:
                this.objectCoords.set(0, LLVector3.parseU16Vec(byteBuffer, -128.0f, 384.0f, -256.0f, 4096.0f));
                this.objectCoords.set(2, LLVector3.parseU16Vec(byteBuffer, -256.0f, 256.0f, -256.0f, 256.0f));
                byteBuffer.position(byteBuffer.position() + 6);
                this.rotation = LLQuaternion.parseU16Vec3(byteBuffer, -1.0f, 1.0f);
                return;
            case 76:
                byteBuffer.position(byteBuffer.position() + 16);
            case 60:
                this.objectCoords.set(0, LLVector3.parseFloatVec(byteBuffer));
                this.objectCoords.set(2, LLVector3.parseFloatVec(byteBuffer));
                byteBuffer.position(byteBuffer.position() + 12);
                this.rotation = LLQuaternion.parseFloatVec3(byteBuffer);
                return;
            default:
                return;
        }
    }

    private void applyHoverText(@Nullable HoverText hoverText) {
        if (Objects.equal(this.hoverText, hoverText)) {
            return;
        }
        this.hoverText = hoverText;
        DrawableObject drawableObject = getDrawableObject();
        if (drawableObject != null) {
            drawableObject.setHoverText(hoverText);
        }
    }

    private static int attachmentIDFromState(int i) {
        return (((i & 255) & AGENT_ATTACH_MASK) >> 4) | (((i & 255) & (-241)) << 4);
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

    public static SLObjectInfo create(UUID uuid, ObjectUpdate.ObjectData objectData, UUID uuid2) {
        SLObjectInfo sLObjectAvatarInfo = objectData.PCode == 47 ? new SLObjectAvatarInfo(uuid, UUIDPool.getUUID(objectData.FullID), uuid2.equals(objectData.FullID)) : new SLObjectPrimInfo();
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
        if (!isAvatar() || (drawableAvatar = SpatialIndex.getInstance().getDrawableAvatar(this)) == null) {
            return;
        }
        drawableAvatar.updateAttachments();
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
        if (isAvatar()) {
            Iterator<SLObjectInfo> it = this.treeNode.iterator();
            while (it.hasNext()) {
                it.next().updateWorldMatrix(true);
            }
        } else {
            Iterator<SLObjectInfo> it2 = this.treeNode.iterator();
            while (it2.hasNext()) {
                it2.next().updateSpatialIndex(z);
            }
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
        this.localID = objectData.ID;
        this.uuid = UUIDPool.getUUID(objectData.FullID);
        this.UpdateFlags = objectData.UpdateFlags;
        this.parentID = objectData.ParentID;
        this.attachmentID = attachmentIDFromState(objectData.State);
        if (objectData.OwnerID.getLeastSignificantBits() != 0 || objectData.OwnerID.getMostSignificantBits() != 0) {
            this.ownerUUID = UUIDPool.getUUID(objectData.OwnerID);
        }
        this.objectCoords.set(1, objectData.Scale);
        String stringFromVariableOEM = SLMessage.stringFromVariableOEM(objectData.Text);
        applyHoverText(Strings.isNullOrEmpty(stringFromVariableOEM) ? null : HoverText.create(stringFromVariableOEM, objectData.TextColor.length >= 4 ? (objectData.TextColor[0] & UnsignedBytes.MAX_VALUE) | ((objectData.TextColor[1] << 8) & MotionEventCompat.ACTION_POINTER_INDEX_MASK) | ((objectData.TextColor[2] << 16) & 16711680) | ((objectData.TextColor[3] << Ascii.CAN) & ViewCompat.MEASURED_STATE_MASK) : 0));
        PrimVolumeParams createFromObjectUpdate = PrimVolumeParams.createFromObjectUpdate(objectData);
        if (createFromObjectUpdate != null && objectData.ExtraParams != null) {
            createFromObjectUpdate.unpackExtraParams(ByteBuffer.wrap(objectData.ExtraParams).order(ByteOrder.LITTLE_ENDIAN));
        }
        ParseObjectData(ByteBuffer.wrap(objectData.ObjectData));
        PrimDrawParams primDrawParams = PrimParamsPool.get(new PrimDrawParams(createFromObjectUpdate != null ? PrimParamsPool.get(createFromObjectUpdate) : null, SLTextureEntry.create(ByteBuffer.wrap(objectData.TextureEntry), objectData.TextureEntry.length)));
        onTexturesUpdate(primDrawParams.getTextures());
        if (!Objects.equal(this.primDrawParams, primDrawParams)) {
            this.primDrawParams = primDrawParams;
            DrawableObject drawableObject = getDrawableObject();
            if (drawableObject != null) {
                drawableObject.setPrimDrawParams(this.primDrawParams);
            }
        }
        this.primDrawParams = PrimParamsPool.get(primDrawParams);
        parseNameValuePairs(SLMessage.stringFromVariableUTF(objectData.NameValue));
        updateSpatialIndex(false);
    }

    /* JADX WARN: Code restructure failed: missing block: B:45:0x00f8, code lost:
    
        if ((r5 & 512) != 0) goto L38;
     */
    /* JADX WARN: Code restructure failed: missing block: B:47:0x00fe, code lost:
    
        if (r4.get() != 0) goto L90;
     */
    /* JADX WARN: Code restructure failed: missing block: B:51:0x0102, code lost:
    
        if ((r5 & 8) == 0) goto L43;
     */
    /* JADX WARN: Code restructure failed: missing block: B:52:0x0104, code lost:
    
        r4.position(r4.position() + 86);
     */
    /* JADX WARN: Code restructure failed: missing block: B:53:0x010d, code lost:
    
        r2 = r4.position();
        r6 = r4.get() & com.google.common.primitives.UnsignedBytes.MAX_VALUE;
        r0 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:54:0x0118, code lost:
    
        if (r0 >= r6) goto L91;
     */
    /* JADX WARN: Code restructure failed: missing block: B:55:0x011a, code lost:
    
        r4.getShort();
        r4.position(r4.getInt() + r4.position());
        r0 = r0 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:58:0x014b, code lost:
    
        if ((r5 & 16) == 0) goto L56;
     */
    /* JADX WARN: Code restructure failed: missing block: B:59:0x014d, code lost:
    
        r4.position(r4.position() + 16);
        r4.position(((r4.position() + 4) + 1) + 4);
     */
    /* JADX WARN: Code restructure failed: missing block: B:61:0x0165, code lost:
    
        if ((r5 & 256) == 0) goto L60;
     */
    /* JADX WARN: Code restructure failed: missing block: B:63:0x016b, code lost:
    
        if (r4.get() != 0) goto L93;
     */
    /* JADX WARN: Code restructure failed: missing block: B:66:0x016d, code lost:
    
        r5 = com.lumiyaviewer.lumiya.slproto.prims.PrimVolumeParams.createFromPackedData(r4);
     */
    /* JADX WARN: Code restructure failed: missing block: B:68:0x0171, code lost:
    
        r0 = com.lumiyaviewer.lumiya.slproto.textures.SLTextureEntry.create(r4, r4.getInt());
     */
    /* JADX WARN: Code restructure failed: missing block: B:71:0x0179, code lost:
    
        onTexturesUpdate(r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:72:0x017c, code lost:
    
        if (r5 != null) goto L64;
     */
    /* JADX WARN: Code restructure failed: missing block: B:73:0x017e, code lost:
    
        r4.position(r2);
        r5.unpackExtraParams(r4);
     */
    /* JADX WARN: Code restructure failed: missing block: B:75:0x0186, code lost:
    
        if (r5 != null) goto L67;
     */
    /* JADX WARN: Code restructure failed: missing block: B:76:0x0188, code lost:
    
        r3 = com.lumiyaviewer.lumiya.slproto.prims.PrimParamsPool.get(r5);
     */
    /* JADX WARN: Code restructure failed: missing block: B:77:0x018c, code lost:
    
        r0 = com.lumiyaviewer.lumiya.slproto.prims.PrimParamsPool.get(new com.lumiyaviewer.lumiya.slproto.prims.PrimDrawParams(r3, r0));
     */
    /* JADX WARN: Code restructure failed: missing block: B:78:0x0199, code lost:
    
        if (com.google.common.base.Objects.equal(r12.primDrawParams, r0) == false) goto L70;
     */
    /* JADX WARN: Code restructure failed: missing block: B:79:0x019b, code lost:
    
        r12.primDrawParams = r0;
        r0 = getDrawableObject();
     */
    /* JADX WARN: Code restructure failed: missing block: B:80:0x01a1, code lost:
    
        if (r0 != null) goto L72;
     */
    /* JADX WARN: Code restructure failed: missing block: B:81:0x01a3, code lost:
    
        r0.setPrimDrawParams(r12.primDrawParams);
     */
    /* JADX WARN: Code restructure failed: missing block: B:82:0x01a8, code lost:
    
        updateSpatialIndex(false);
     */
    /* JADX WARN: Code restructure failed: missing block: B:83:0x01ab, code lost:
    
        return;
     */
    /* JADX WARN: Code restructure failed: missing block: B:86:0x01ae, code lost:
    
        com.lumiyaviewer.lumiya.Debug.Log("Failed to retrieve textures in compressed update");
     */
    /* JADX WARN: Code restructure failed: missing block: B:88:0x01ad, code lost:
    
        r0 = null;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void ApplyObjectUpdate(com.lumiyaviewer.lumiya.slproto.messages.ObjectUpdateCompressed.ObjectData r13) throws com.lumiyaviewer.lumiya.slproto.objects.UnsupportedObjectTypeException {
        /*
            Method dump skipped, instructions count: 442
            To view this dump add '--comments-level debug' option
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
            PrimDrawParams primDrawParams = this.primDrawParams;
            if (primDrawParams != null && !create.equals(primDrawParams.getTextures())) {
                PrimDrawParams primDrawParams2 = PrimParamsPool.get(new PrimDrawParams(primDrawParams.getVolumeParams(), create));
                if (!Objects.equal(this.primDrawParams, primDrawParams2)) {
                    this.primDrawParams = primDrawParams2;
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

    @Nonnull
    protected abstract DrawListObjectEntry createDrawListEntry();

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
        DrawListObjectEntry drawListObjectEntry = weakReference != null ? weakReference.get() : null;
        if (drawListObjectEntry == null) {
            synchronized (this) {
                WeakReference<DrawListObjectEntry> weakReference2 = this.drawListEntry;
                drawListObjectEntry = weakReference2 != null ? weakReference2.get() : null;
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
            return weakReference.get();
        }
        return null;
    }

    @Nullable
    public HoverText getHoverText() {
        return this.hoverText;
    }

    @Override // com.lumiyaviewer.lumiya.utils.Identifiable
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
        int elementOffset = this.objectCoords.getElementOffset(0);
        int elementOffset2 = this.objectCoords.getElementOffset(1);
        float[] data = this.objectCoords.getData();
        matrixStack.glPushMatrix();
        matrixStack.glTranslatef(data[elementOffset + 0], data[elementOffset + 1], data[elementOffset + 2]);
        matrixStack.glMultMatrixf(this.rotation.getInverseMatrix(), 0);
        float[] fArr = {(-data[elementOffset2 + 0]) / 2.0f, (-data[elementOffset2 + 1]) / 2.0f, (-data[elementOffset2 + 2]) / 2.0f, 1.0f};
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
            Iterator<SLObjectInfo> it = this.treeNode.iterator();
            while (it.hasNext()) {
                it.next().getObjectExtents(matrixStack, false, lLVector3, lLVector32);
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
        return (parent == null || parent.isAvatar()) ? this : parent.getRootPrim();
    }

    public final LLQuaternion getRotation() {
        return this.rotation;
    }

    public String getTouchName() {
        return this.touchName;
    }

    public boolean hasTouchableChildren() {
        try {
            Iterator<SLObjectInfo> it = this.treeNode.iterator();
            while (it.hasNext()) {
                if (it.next().isTouchable()) {
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
            for (SLObjectInfo sLObjectInfo : this.treeNode) {
                if ((sLObjectInfo instanceof SLObjectAvatarInfo) && ((SLObjectAvatarInfo) sLObjectInfo).isMyAvatar()) {
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

    protected void onTexturesUpdate(SLTextureEntry sLTextureEntry) {
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
        if (isAvatar()) {
            return;
        }
        Iterator<SLObjectInfo> it = this.treeNode.iterator();
        while (it.hasNext()) {
            it.next().removeFromSpatialIndex();
        }
    }

    public synchronized void setIsAttachmentAll(boolean z) {
        this.isAttachment = z;
        try {
            for (SLObjectInfo sLObjectInfo : this.treeNode) {
                if (!sLObjectInfo.isAvatar()) {
                    sLObjectInfo.setIsAttachmentAll(z);
                }
            }
        } catch (NoSuchElementException e) {
            e.printStackTrace();
        }
    }

    public void setPayInfo(@Nullable PayInfo payInfo) {
        this.payInfo = payInfo;
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
                    Iterator<SLObjectInfo> it = this.treeNode.iterator();
                    while (it.hasNext()) {
                        it.next().updateWorldMatrix(true);
                    }
                }
            }
        }
    }
}
