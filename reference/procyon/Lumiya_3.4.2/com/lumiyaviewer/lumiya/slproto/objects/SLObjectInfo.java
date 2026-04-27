// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.objects;

import java.util.Arrays;
import com.lumiyaviewer.lumiya.utils.IdentityMatrix;
import java.util.NoSuchElementException;
import com.lumiyaviewer.lumiya.render.MatrixStack;
import javax.annotation.Nonnull;
import com.lumiyaviewer.lumiya.slproto.textures.SLTextureEntry;
import com.lumiyaviewer.lumiya.slproto.prims.PrimParamsPool;
import com.lumiyaviewer.lumiya.slproto.prims.PrimVolumeParams;
import com.google.common.base.Strings;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.messages.ObjectProperties;
import java.util.Iterator;
import com.lumiyaviewer.lumiya.render.spatial.SpatialObjectIndex;
import com.lumiyaviewer.lumiya.render.avatar.DrawableAvatar;
import com.lumiyaviewer.lumiya.render.spatial.SpatialIndex;
import com.lumiyaviewer.lumiya.slproto.messages.ImprovedTerseObjectUpdate;
import com.lumiyaviewer.lumiya.render.spatial.DrawListPrimEntry;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import com.lumiyaviewer.lumiya.slproto.messages.ObjectUpdate;
import com.lumiyaviewer.lumiya.slproto.messages.ObjectUpdateCompressed;
import android.opengl.Matrix;
import com.lumiyaviewer.lumiya.render.DrawableObject;
import com.google.common.base.Objects;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.nio.ByteOrder;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.utils.LinkedTreeNode;
import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion;
import com.lumiyaviewer.lumiya.slproto.prims.PrimDrawParams;
import com.lumiyaviewer.lumiya.slproto.types.Vector3Array;
import javax.annotation.Nullable;
import com.lumiyaviewer.lumiya.render.spatial.DrawListObjectEntry;
import java.lang.ref.WeakReference;
import java.util.UUID;
import com.lumiyaviewer.lumiya.utils.Identifiable;

public abstract class SLObjectInfo implements Identifiable<UUID>
{
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
    public UUID attachedToUUID;
    public int attachmentID;
    public UUID creatorUUID;
    public String description;
    @Nullable
    private volatile WeakReference<DrawListObjectEntry> drawListEntry;
    public int hierLevel;
    private volatile HoverText hoverText;
    public boolean isAttachment;
    public volatile boolean isDead;
    public int localID;
    public String name;
    public boolean nameKnown;
    public boolean nameRequested;
    public long nameRequestedAt;
    public float objRadius;
    private final Vector3Array objectCoords;
    public UUID ownerUUID;
    public int parentID;
    @Nullable
    private PayInfo payInfo;
    private PrimDrawParams primDrawParams;
    private LLQuaternion rotation;
    public int salePrice;
    public byte saleType;
    public String touchName;
    public final LinkedTreeNode<SLObjectInfo> treeNode;
    protected UUID uuid;
    public float[] worldMatrix;
    
    public SLObjectInfo() {
        this.parentID = 0;
        this.name = "(loading)";
        this.description = "";
        this.touchName = "";
        this.attachedToUUID = null;
        this.ownerUUID = null;
        this.creatorUUID = null;
        this.saleType = 0;
        this.attachmentID = 0;
        this.objectCoords = new Vector3Array(4);
        this.hoverText = null;
        this.isDead = false;
        this.isAttachment = false;
        this.nameKnown = false;
        this.nameRequested = false;
        this.nameRequestedAt = 0L;
        this.hierLevel = 0;
        this.treeNode = new LinkedTreeNode<SLObjectInfo>(this);
    }
    
    private void ParseObjectData(final ByteBuffer byteBuffer) {
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        switch (byteBuffer.limit()) {
            case 76: {
                byteBuffer.position();
            }
            case 60: {
                this.objectCoords.set(0, LLVector3.parseFloatVec(byteBuffer));
                this.objectCoords.set(2, LLVector3.parseFloatVec(byteBuffer));
                byteBuffer.position();
                this.rotation = LLQuaternion.parseFloatVec3(byteBuffer);
                break;
            }
            case 48: {
                byteBuffer.position();
            }
            case 32: {
                this.objectCoords.set(0, LLVector3.parseU16Vec(byteBuffer, -128.0f, 384.0f, -256.0f, 4096.0f));
                this.objectCoords.set(2, LLVector3.parseU16Vec(byteBuffer, -256.0f, 256.0f, -256.0f, 256.0f));
                byteBuffer.position();
                this.rotation = LLQuaternion.parseU16Vec3(byteBuffer, -1.0f, 1.0f);
                break;
            }
            case 16: {
                this.objectCoords.set(0, LLVector3.parseU8Vec(byteBuffer, 384.0f, 384.0f, -256.0f, 4096.0f));
                this.objectCoords.set(2, LLVector3.parseU8Vec(byteBuffer, -256.0f, 256.0f, -256.0f, 256.0f));
                byteBuffer.position();
                this.rotation = LLQuaternion.parseU8Vec3(byteBuffer, -1.0f, 1.0f);
                break;
            }
        }
    }
    
    private void applyHoverText(@Nullable final HoverText hoverText) {
        if (!Objects.equal(this.hoverText, hoverText)) {
            this.hoverText = hoverText;
            final DrawableObject drawableObject = this.getDrawableObject();
            if (drawableObject != null) {
                drawableObject.setHoverText(hoverText);
            }
        }
    }
    
    private static int attachmentIDFromState(final int n) {
        return (n & 0xFF & 0xF0) >> 4 | (n & 0xFF & 0xFFFFFF0F) << 4;
    }
    
    private float[] calculateWorldMatrix(final float[] array) {
        final LLQuaternion rotation = this.rotation;
        if (rotation != null) {
            final float[] array2 = new float[16];
            final float[] array3 = new float[16];
            this.objectCoords.MatrixTranslate(array3, 0, array, 0, 0);
            Matrix.multiplyMM(array2, 0, array3, 0, rotation.getInverseMatrix(), 0);
            return array2;
        }
        return null;
    }
    
    public static SLObjectInfo create(final ObjectUpdateCompressed.ObjectData objectData) throws UnsupportedObjectTypeException {
        final SLObjectPrimInfo slObjectPrimInfo = new SLObjectPrimInfo();
        slObjectPrimInfo.ApplyObjectUpdate(objectData);
        return slObjectPrimInfo;
    }
    
    public static SLObjectInfo create(final UUID uuid, final ObjectUpdate.ObjectData objectData, final UUID uuid2) {
        int n;
        if (objectData.PCode == 47) {
            n = 1;
        }
        else {
            n = 0;
        }
        SLObjectInfo slObjectInfo;
        if (n != 0) {
            slObjectInfo = new SLObjectAvatarInfo(uuid, UUIDPool.getUUID(objectData.FullID), uuid2.equals(objectData.FullID));
        }
        else {
            slObjectInfo = new SLObjectPrimInfo();
        }
        slObjectInfo.ApplyObjectUpdate(objectData);
        return slObjectInfo;
    }
    
    @Nullable
    private DrawableObject getDrawableObject() {
        final DrawListObjectEntry existingDrawListEntry = this.getExistingDrawListEntry();
        if (existingDrawListEntry instanceof DrawListPrimEntry) {
            return ((DrawListPrimEntry)existingDrawListEntry).getDrawableObject();
        }
        return null;
    }
    
    public static int getLocalID(final ImprovedTerseObjectUpdate.ObjectData objectData) {
        final ByteBuffer wrap = ByteBuffer.wrap(objectData.Data);
        wrap.order(ByteOrder.LITTLE_ENDIAN);
        return wrap.getInt();
    }
    
    public static int getLocalID(final ObjectUpdateCompressed.ObjectData objectData) {
        final ByteBuffer wrap = ByteBuffer.wrap(objectData.Data);
        wrap.position();
        wrap.order(ByteOrder.LITTLE_ENDIAN);
        return wrap.getInt();
    }
    
    private void parseNameValuePairs(String s) {
        final String[] split = s.split("\n");
        final int length = split.length;
        int i = 0;
    Label_0093_Outer:
        while (i < length) {
            s = split[i];
            while (true) {
                Label_0108: {
                    if (!s.startsWith("AttachItemID ")) {
                        break Label_0108;
                    }
                    for (int j = 0; j < 4; ++j) {
                        final int index = s.indexOf(32);
                        String substring = s;
                        if (index >= 0) {
                            substring = s.substring(index + 1);
                        }
                        s = substring.trim();
                    }
                    try {
                        this.attachedToUUID = UUIDPool.getUUID(UUID.fromString(s));
                        ++i;
                        continue Label_0093_Outer;
                    }
                    catch (final Exception ex) {
                        this.attachedToUUID = null;
                        continue;
                    }
                }
                if (s.startsWith("DisplayName ")) {
                    for (int k = 0; k < 4; ++k) {
                        final int index2 = s.indexOf(32);
                        String substring2 = s;
                        if (index2 >= 0) {
                            substring2 = s.substring(index2 + 1);
                        }
                        s = substring2.trim();
                    }
                    this.name = s;
                    this.nameKnown = true;
                    continue;
                }
                continue;
            }
        }
    }
    
    private void updateAttachments() {
        if (this.isAvatar()) {
            final DrawableAvatar drawableAvatar = SpatialIndex.getInstance().getDrawableAvatar(this);
            if (drawableAvatar != null) {
                drawableAvatar.updateAttachments();
            }
        }
    }
    
    private void updateSpatialIndex(final SpatialObjectIndex spatialObjectIndex, final boolean b) {
        this.updateWorldMatrix(false);
        while (true) {
            if (b) {
                synchronized (this) {
                    this.drawListEntry = null;
                    monitorexit(this);
                    if (spatialObjectIndex != null && (this.isDead ^ true)) {
                        spatialObjectIndex.updateObject(this.getDrawListEntry());
                    }
                    if (!this.isAvatar()) {
                        final Iterator<Object> iterator = this.treeNode.iterator();
                        while (iterator.hasNext()) {
                            iterator.next().updateSpatialIndex(b);
                        }
                        return;
                    }
                }
                final Iterator<Object> iterator2 = this.treeNode.iterator();
                while (iterator2.hasNext()) {
                    iterator2.next().updateWorldMatrix(true);
                }
                return;
            }
            continue;
        }
    }
    
    public void ApplyObjectProperties(final ObjectProperties.ObjectData objectData) {
        this.name = SLMessage.stringFromVariableOEM(objectData.Name);
        this.description = SLMessage.stringFromVariableUTF(objectData.Description);
        this.touchName = SLMessage.stringFromVariableUTF(objectData.TouchName);
        this.creatorUUID = objectData.CreatorID;
        this.ownerUUID = objectData.OwnerID;
        this.saleType = (byte)objectData.SaleType;
        this.salePrice = objectData.SalePrice;
        this.nameKnown = true;
        this.nameRequested = false;
    }
    
    public void ApplyObjectUpdate(final ObjectUpdate.ObjectData objectData) {
        final PrimVolumeParams primVolumeParams = null;
        this.localID = objectData.ID;
        this.uuid = UUIDPool.getUUID(objectData.FullID);
        this.UpdateFlags = objectData.UpdateFlags;
        this.parentID = objectData.ParentID;
        this.attachmentID = attachmentIDFromState(objectData.State);
        if (objectData.OwnerID.getLeastSignificantBits() != 0L || objectData.OwnerID.getMostSignificantBits() != 0L) {
            this.ownerUUID = UUIDPool.getUUID(objectData.OwnerID);
        }
        this.objectCoords.set(1, objectData.Scale);
        final String stringFromVariableOEM = SLMessage.stringFromVariableOEM(objectData.Text);
        int n;
        if (objectData.TextColor.length >= 4) {
            n = ((objectData.TextColor[0] & 0xFF) | (objectData.TextColor[1] << 8 & 0xFF00) | (objectData.TextColor[2] << 16 & 0xFF0000) | (objectData.TextColor[3] << 24 & 0xFF000000));
        }
        else {
            n = 0;
        }
        HoverText create;
        if (Strings.isNullOrEmpty(stringFromVariableOEM)) {
            create = null;
        }
        else {
            create = HoverText.create(stringFromVariableOEM, n);
        }
        this.applyHoverText(create);
        final PrimVolumeParams fromObjectUpdate = PrimVolumeParams.createFromObjectUpdate(objectData);
        if (fromObjectUpdate != null && objectData.ExtraParams != null) {
            fromObjectUpdate.unpackExtraParams(ByteBuffer.wrap(objectData.ExtraParams).order(ByteOrder.LITTLE_ENDIAN));
        }
        this.ParseObjectData(ByteBuffer.wrap(objectData.ObjectData));
        PrimVolumeParams value = primVolumeParams;
        if (fromObjectUpdate != null) {
            value = PrimParamsPool.get(fromObjectUpdate);
        }
        final PrimDrawParams value2 = PrimParamsPool.get(new PrimDrawParams(value, SLTextureEntry.create(ByteBuffer.wrap(objectData.TextureEntry), objectData.TextureEntry.length)));
        this.onTexturesUpdate(value2.getTextures());
        if (!Objects.equal(this.primDrawParams, value2)) {
            this.primDrawParams = value2;
            final DrawableObject drawableObject = this.getDrawableObject();
            if (drawableObject != null) {
                drawableObject.setPrimDrawParams(this.primDrawParams);
            }
        }
        this.primDrawParams = PrimParamsPool.get(value2);
        this.parseNameValuePairs(SLMessage.stringFromVariableUTF(objectData.NameValue));
        this.updateSpatialIndex(false);
    }
    
    public void ApplyObjectUpdate(final ObjectUpdateCompressed.ObjectData p0) throws UnsupportedObjectTypeException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: astore_2       
        //     2: aload_0        
        //     3: aload_1        
        //     4: getfield        com/lumiyaviewer/lumiya/slproto/messages/ObjectUpdateCompressed$ObjectData.UpdateFlags:I
        //     7: putfield        com/lumiyaviewer/lumiya/slproto/objects/SLObjectInfo.UpdateFlags:I
        //    10: aload_1        
        //    11: getfield        com/lumiyaviewer/lumiya/slproto/messages/ObjectUpdateCompressed$ObjectData.Data:[B
        //    14: invokestatic    java/nio/ByteBuffer.wrap:([B)Ljava/nio/ByteBuffer;
        //    17: astore_3       
        //    18: aload_3        
        //    19: getstatic       java/nio/ByteOrder.BIG_ENDIAN:Ljava/nio/ByteOrder;
        //    22: invokevirtual   java/nio/ByteBuffer.order:(Ljava/nio/ByteOrder;)Ljava/nio/ByteBuffer;
        //    25: pop            
        //    26: aload_3        
        //    27: invokevirtual   java/nio/ByteBuffer.getLong:()J
        //    30: lstore          4
        //    32: aload_3        
        //    33: invokevirtual   java/nio/ByteBuffer.getLong:()J
        //    36: lstore          6
        //    38: aload_0        
        //    39: aload_0        
        //    40: getfield        com/lumiyaviewer/lumiya/slproto/objects/SLObjectInfo.uuid:Ljava/util/UUID;
        //    43: lload           4
        //    45: lload           6
        //    47: invokestatic    com/lumiyaviewer/lumiya/utils/UUIDPool.setUUID:(Ljava/util/UUID;JJ)Ljava/util/UUID;
        //    50: putfield        com/lumiyaviewer/lumiya/slproto/objects/SLObjectInfo.uuid:Ljava/util/UUID;
        //    53: aload_3        
        //    54: getstatic       java/nio/ByteOrder.LITTLE_ENDIAN:Ljava/nio/ByteOrder;
        //    57: invokevirtual   java/nio/ByteBuffer.order:(Ljava/nio/ByteOrder;)Ljava/nio/ByteBuffer;
        //    60: pop            
        //    61: aload_0        
        //    62: aload_3        
        //    63: invokevirtual   java/nio/ByteBuffer.getInt:()I
        //    66: putfield        com/lumiyaviewer/lumiya/slproto/objects/SLObjectInfo.localID:I
        //    69: aload_3        
        //    70: invokevirtual   java/nio/ByteBuffer.get:()B
        //    73: istore          8
        //    75: iload           8
        //    77: bipush          9
        //    79: if_icmpeq       92
        //    82: new             Lcom/lumiyaviewer/lumiya/slproto/objects/UnsupportedObjectTypeException;
        //    85: dup            
        //    86: iload           8
        //    88: invokespecial   com/lumiyaviewer/lumiya/slproto/objects/UnsupportedObjectTypeException.<init>:(B)V
        //    91: athrow         
        //    92: aload_0        
        //    93: aload_3        
        //    94: invokevirtual   java/nio/ByteBuffer.get:()B
        //    97: invokestatic    com/lumiyaviewer/lumiya/slproto/objects/SLObjectInfo.attachmentIDFromState:(I)I
        //   100: putfield        com/lumiyaviewer/lumiya/slproto/objects/SLObjectInfo.attachmentID:I
        //   103: aload_3        
        //   104: aload_3        
        //   105: invokevirtual   java/nio/ByteBuffer.position:()I
        //   108: iconst_4       
        //   109: iadd           
        //   110: iconst_1       
        //   111: iadd           
        //   112: iconst_1       
        //   113: iadd           
        //   114: invokevirtual   java/nio/ByteBuffer.position:(I)Ljava/nio/Buffer;
        //   117: pop            
        //   118: aload_3        
        //   119: invokestatic    com/lumiyaviewer/lumiya/slproto/types/LLVector3.parseFloatVec:(Ljava/nio/ByteBuffer;)Lcom/lumiyaviewer/lumiya/slproto/types/LLVector3;
        //   122: astore          9
        //   124: aload_3        
        //   125: invokestatic    com/lumiyaviewer/lumiya/slproto/types/LLVector3.parseFloatVec:(Ljava/nio/ByteBuffer;)Lcom/lumiyaviewer/lumiya/slproto/types/LLVector3;
        //   128: astore_1       
        //   129: aload_0        
        //   130: getfield        com/lumiyaviewer/lumiya/slproto/objects/SLObjectInfo.objectCoords:Lcom/lumiyaviewer/lumiya/slproto/types/Vector3Array;
        //   133: iconst_1       
        //   134: aload           9
        //   136: invokevirtual   com/lumiyaviewer/lumiya/slproto/types/Vector3Array.set:(ILcom/lumiyaviewer/lumiya/slproto/types/LLVector3;)V
        //   139: aload_0        
        //   140: getfield        com/lumiyaviewer/lumiya/slproto/objects/SLObjectInfo.objectCoords:Lcom/lumiyaviewer/lumiya/slproto/types/Vector3Array;
        //   143: iconst_0       
        //   144: aload_1        
        //   145: invokevirtual   com/lumiyaviewer/lumiya/slproto/types/Vector3Array.set:(ILcom/lumiyaviewer/lumiya/slproto/types/LLVector3;)V
        //   148: aload_0        
        //   149: aload_3        
        //   150: invokestatic    com/lumiyaviewer/lumiya/slproto/types/LLQuaternion.parseFloatVec3:(Ljava/nio/ByteBuffer;)Lcom/lumiyaviewer/lumiya/slproto/types/LLQuaternion;
        //   153: putfield        com/lumiyaviewer/lumiya/slproto/objects/SLObjectInfo.rotation:Lcom/lumiyaviewer/lumiya/slproto/types/LLQuaternion;
        //   156: aload_3        
        //   157: invokevirtual   java/nio/ByteBuffer.getInt:()I
        //   160: istore          10
        //   162: aload_3        
        //   163: getstatic       java/nio/ByteOrder.BIG_ENDIAN:Ljava/nio/ByteOrder;
        //   166: invokevirtual   java/nio/ByteBuffer.order:(Ljava/nio/ByteOrder;)Ljava/nio/ByteBuffer;
        //   169: pop            
        //   170: aload_3        
        //   171: invokevirtual   java/nio/ByteBuffer.getLong:()J
        //   174: lstore          4
        //   176: aload_3        
        //   177: invokevirtual   java/nio/ByteBuffer.getLong:()J
        //   180: lstore          6
        //   182: aload_0        
        //   183: getfield        com/lumiyaviewer/lumiya/slproto/objects/SLObjectInfo.ownerUUID:Ljava/util/UUID;
        //   186: ifnull          203
        //   189: lload           4
        //   191: lconst_0       
        //   192: lcmp           
        //   193: ifeq            218
        //   196: lload           6
        //   198: lconst_0       
        //   199: lcmp           
        //   200: ifeq            218
        //   203: aload_0        
        //   204: aload_0        
        //   205: getfield        com/lumiyaviewer/lumiya/slproto/objects/SLObjectInfo.ownerUUID:Ljava/util/UUID;
        //   208: lload           4
        //   210: lload           6
        //   212: invokestatic    com/lumiyaviewer/lumiya/utils/UUIDPool.setUUID:(Ljava/util/UUID;JJ)Ljava/util/UUID;
        //   215: putfield        com/lumiyaviewer/lumiya/slproto/objects/SLObjectInfo.ownerUUID:Ljava/util/UUID;
        //   218: aload_3        
        //   219: getstatic       java/nio/ByteOrder.LITTLE_ENDIAN:Ljava/nio/ByteOrder;
        //   222: invokevirtual   java/nio/ByteBuffer.order:(Ljava/nio/ByteOrder;)Ljava/nio/ByteBuffer;
        //   225: pop            
        //   226: iload           10
        //   228: sipush          128
        //   231: iand           
        //   232: ifeq            247
        //   235: aload_3        
        //   236: aload_3        
        //   237: invokevirtual   java/nio/ByteBuffer.position:()I
        //   240: bipush          12
        //   242: iadd           
        //   243: invokevirtual   java/nio/ByteBuffer.position:(I)Ljava/nio/Buffer;
        //   246: pop            
        //   247: iload           10
        //   249: bipush          32
        //   251: iand           
        //   252: ifeq            263
        //   255: aload_0        
        //   256: aload_3        
        //   257: invokevirtual   java/nio/ByteBuffer.getInt:()I
        //   260: putfield        com/lumiyaviewer/lumiya/slproto/objects/SLObjectInfo.parentID:I
        //   263: iload           10
        //   265: iconst_2       
        //   266: iand           
        //   267: ifeq            474
        //   270: aload_3        
        //   271: aload_3        
        //   272: invokevirtual   java/nio/ByteBuffer.position:()I
        //   275: iconst_1       
        //   276: iadd           
        //   277: invokevirtual   java/nio/ByteBuffer.position:(I)Ljava/nio/Buffer;
        //   280: pop            
        //   281: iload           10
        //   283: iconst_4       
        //   284: iand           
        //   285: ifeq            387
        //   288: aload_3        
        //   289: invokevirtual   java/nio/ByteBuffer.position:()I
        //   292: istore          11
        //   294: iconst_0       
        //   295: istore          12
        //   297: iload           11
        //   299: iload           12
        //   301: iadd           
        //   302: aload_3        
        //   303: invokevirtual   java/nio/ByteBuffer.capacity:()I
        //   306: if_icmpge       321
        //   309: aload_3        
        //   310: iload           11
        //   312: iload           12
        //   314: iadd           
        //   315: invokevirtual   java/nio/ByteBuffer.get:(I)B
        //   318: ifne            498
        //   321: iload           12
        //   323: ifeq            689
        //   326: iload           12
        //   328: newarray        B
        //   330: astore          9
        //   332: aload_3        
        //   333: aload           9
        //   335: iconst_0       
        //   336: iload           12
        //   338: invokevirtual   java/nio/ByteBuffer.get:([BII)Ljava/nio/ByteBuffer;
        //   341: pop            
        //   342: new             Ljava/lang/String;
        //   345: astore_1       
        //   346: aload_1        
        //   347: aload           9
        //   349: ldc_w           "ISO-8859-1"
        //   352: invokespecial   java/lang/String.<init>:([BLjava/lang/String;)V
        //   355: aload_3        
        //   356: iload           12
        //   358: iload           11
        //   360: iadd           
        //   361: iconst_1       
        //   362: iadd           
        //   363: invokevirtual   java/nio/ByteBuffer.position:(I)Ljava/nio/Buffer;
        //   366: pop            
        //   367: aload_3        
        //   368: invokevirtual   java/nio/ByteBuffer.getInt:()I
        //   371: istore          12
        //   373: aload_1        
        //   374: invokestatic    com/google/common/base/Strings.isNullOrEmpty:(Ljava/lang/String;)Z
        //   377: ifeq            510
        //   380: aconst_null    
        //   381: astore_1       
        //   382: aload_0        
        //   383: aload_1        
        //   384: invokespecial   com/lumiyaviewer/lumiya/slproto/objects/SLObjectInfo.applyHoverText:(Lcom/lumiyaviewer/lumiya/slproto/objects/HoverText;)V
        //   387: iload           10
        //   389: sipush          512
        //   392: iand           
        //   393: ifeq            403
        //   396: aload_3        
        //   397: invokevirtual   java/nio/ByteBuffer.get:()B
        //   400: ifne            396
        //   403: iload           10
        //   405: bipush          8
        //   407: iand           
        //   408: ifeq            423
        //   411: aload_3        
        //   412: aload_3        
        //   413: invokevirtual   java/nio/ByteBuffer.position:()I
        //   416: bipush          86
        //   418: iadd           
        //   419: invokevirtual   java/nio/ByteBuffer.position:(I)Ljava/nio/Buffer;
        //   422: pop            
        //   423: aload_3        
        //   424: invokevirtual   java/nio/ByteBuffer.position:()I
        //   427: istore          13
        //   429: aload_3        
        //   430: invokevirtual   java/nio/ByteBuffer.get:()B
        //   433: istore          11
        //   435: iconst_0       
        //   436: istore          12
        //   438: iload           12
        //   440: iload           11
        //   442: sipush          255
        //   445: iand           
        //   446: if_icmpge       520
        //   449: aload_3        
        //   450: invokevirtual   java/nio/ByteBuffer.getShort:()S
        //   453: pop            
        //   454: aload_3        
        //   455: aload_3        
        //   456: invokevirtual   java/nio/ByteBuffer.getInt:()I
        //   459: aload_3        
        //   460: invokevirtual   java/nio/ByteBuffer.position:()I
        //   463: iadd           
        //   464: invokevirtual   java/nio/ByteBuffer.position:(I)Ljava/nio/Buffer;
        //   467: pop            
        //   468: iinc            12, 1
        //   471: goto            438
        //   474: iload           10
        //   476: iconst_1       
        //   477: iand           
        //   478: ifeq            281
        //   481: aload_3        
        //   482: aload_3        
        //   483: invokevirtual   java/nio/ByteBuffer.get:()B
        //   486: aload_3        
        //   487: invokevirtual   java/nio/ByteBuffer.position:()I
        //   490: iadd           
        //   491: invokevirtual   java/nio/ByteBuffer.position:(I)Ljava/nio/Buffer;
        //   494: pop            
        //   495: goto            281
        //   498: iinc            12, 1
        //   501: goto            297
        //   504: astore_1       
        //   505: aconst_null    
        //   506: astore_1       
        //   507: goto            355
        //   510: aload_1        
        //   511: iload           12
        //   513: invokestatic    com/lumiyaviewer/lumiya/slproto/objects/HoverText.create:(Ljava/lang/String;I)Lcom/lumiyaviewer/lumiya/slproto/objects/HoverText;
        //   516: astore_1       
        //   517: goto            382
        //   520: iload           10
        //   522: bipush          16
        //   524: iand           
        //   525: ifeq            555
        //   528: aload_3        
        //   529: aload_3        
        //   530: invokevirtual   java/nio/ByteBuffer.position:()I
        //   533: bipush          16
        //   535: iadd           
        //   536: invokevirtual   java/nio/ByteBuffer.position:(I)Ljava/nio/Buffer;
        //   539: pop            
        //   540: aload_3        
        //   541: aload_3        
        //   542: invokevirtual   java/nio/ByteBuffer.position:()I
        //   545: iconst_4       
        //   546: iadd           
        //   547: iconst_1       
        //   548: iadd           
        //   549: iconst_4       
        //   550: iadd           
        //   551: invokevirtual   java/nio/ByteBuffer.position:(I)Ljava/nio/Buffer;
        //   554: pop            
        //   555: iload           10
        //   557: sipush          256
        //   560: iand           
        //   561: ifeq            571
        //   564: aload_3        
        //   565: invokevirtual   java/nio/ByteBuffer.get:()B
        //   568: ifne            564
        //   571: aload_3        
        //   572: invokestatic    com/lumiyaviewer/lumiya/slproto/prims/PrimVolumeParams.createFromPackedData:(Ljava/nio/ByteBuffer;)Lcom/lumiyaviewer/lumiya/slproto/prims/PrimVolumeParams;
        //   575: astore          9
        //   577: aload_3        
        //   578: aload_3        
        //   579: invokevirtual   java/nio/ByteBuffer.getInt:()I
        //   582: invokestatic    com/lumiyaviewer/lumiya/slproto/textures/SLTextureEntry.create:(Ljava/nio/ByteBuffer;I)Lcom/lumiyaviewer/lumiya/slproto/textures/SLTextureEntry;
        //   585: astore_1       
        //   586: aload_0        
        //   587: aload_1        
        //   588: invokevirtual   com/lumiyaviewer/lumiya/slproto/objects/SLObjectInfo.onTexturesUpdate:(Lcom/lumiyaviewer/lumiya/slproto/textures/SLTextureEntry;)V
        //   591: aload           9
        //   593: ifnull          609
        //   596: aload_3        
        //   597: iload           13
        //   599: invokevirtual   java/nio/ByteBuffer.position:(I)Ljava/nio/Buffer;
        //   602: pop            
        //   603: aload           9
        //   605: aload_3        
        //   606: invokevirtual   com/lumiyaviewer/lumiya/slproto/prims/PrimVolumeParams.unpackExtraParams:(Ljava/nio/ByteBuffer;)V
        //   609: aload           9
        //   611: ifnull          620
        //   614: aload           9
        //   616: invokestatic    com/lumiyaviewer/lumiya/slproto/prims/PrimParamsPool.get:(Lcom/lumiyaviewer/lumiya/slproto/prims/PrimVolumeParams;)Lcom/lumiyaviewer/lumiya/slproto/prims/PrimVolumeParams;
        //   619: astore_2       
        //   620: new             Lcom/lumiyaviewer/lumiya/slproto/prims/PrimDrawParams;
        //   623: dup            
        //   624: aload_2        
        //   625: aload_1        
        //   626: invokespecial   com/lumiyaviewer/lumiya/slproto/prims/PrimDrawParams.<init>:(Lcom/lumiyaviewer/lumiya/slproto/prims/PrimVolumeParams;Lcom/lumiyaviewer/lumiya/slproto/textures/SLTextureEntry;)V
        //   629: invokestatic    com/lumiyaviewer/lumiya/slproto/prims/PrimParamsPool.get:(Lcom/lumiyaviewer/lumiya/slproto/prims/PrimDrawParams;)Lcom/lumiyaviewer/lumiya/slproto/prims/PrimDrawParams;
        //   632: astore_1       
        //   633: aload_0        
        //   634: getfield        com/lumiyaviewer/lumiya/slproto/objects/SLObjectInfo.primDrawParams:Lcom/lumiyaviewer/lumiya/slproto/prims/PrimDrawParams;
        //   637: aload_1        
        //   638: invokestatic    com/google/common/base/Objects.equal:(Ljava/lang/Object;Ljava/lang/Object;)Z
        //   641: ifne            666
        //   644: aload_0        
        //   645: aload_1        
        //   646: putfield        com/lumiyaviewer/lumiya/slproto/objects/SLObjectInfo.primDrawParams:Lcom/lumiyaviewer/lumiya/slproto/prims/PrimDrawParams;
        //   649: aload_0        
        //   650: invokespecial   com/lumiyaviewer/lumiya/slproto/objects/SLObjectInfo.getDrawableObject:()Lcom/lumiyaviewer/lumiya/render/DrawableObject;
        //   653: astore_1       
        //   654: aload_1        
        //   655: ifnull          666
        //   658: aload_1        
        //   659: aload_0        
        //   660: getfield        com/lumiyaviewer/lumiya/slproto/objects/SLObjectInfo.primDrawParams:Lcom/lumiyaviewer/lumiya/slproto/prims/PrimDrawParams;
        //   663: invokevirtual   com/lumiyaviewer/lumiya/render/DrawableObject.setPrimDrawParams:(Lcom/lumiyaviewer/lumiya/slproto/prims/PrimDrawParams;)V
        //   666: aload_0        
        //   667: iconst_0       
        //   668: invokevirtual   com/lumiyaviewer/lumiya/slproto/objects/SLObjectInfo.updateSpatialIndex:(Z)V
        //   671: return         
        //   672: astore_1       
        //   673: aconst_null    
        //   674: astore_1       
        //   675: ldc_w           "Failed to retrieve textures in compressed update"
        //   678: invokestatic    com/lumiyaviewer/lumiya/Debug.Log:(Ljava/lang/String;)V
        //   681: goto            591
        //   684: astore          14
        //   686: goto            675
        //   689: aconst_null    
        //   690: astore_1       
        //   691: goto            355
        //    Exceptions:
        //  throws com.lumiyaviewer.lumiya.slproto.objects.UnsupportedObjectTypeException
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                                  
        //  -----  -----  -----  -----  --------------------------------------
        //  342    355    504    510    Ljava/io/UnsupportedEncodingException;
        //  577    586    672    675    Ljava/lang/Exception;
        //  586    591    684    689    Ljava/lang/Exception;
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: Label_0591:
        //     at com.strobel.decompiler.ast.Error.expressionLinkedFromMultipleLocations(Error.java:27)
        //     at com.strobel.decompiler.ast.AstOptimizer.mergeDisparateObjectInitializations(AstOptimizer.java:2604)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:235)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:206)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:162)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:137)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:333)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:254)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:144)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void ApplyTerseObjectUpdate(final ImprovedTerseObjectUpdate.ObjectData objectData) {
        final ByteBuffer wrap = ByteBuffer.wrap(objectData.Data);
        wrap.order(ByteOrder.LITTLE_ENDIAN);
        wrap.getInt();
        this.attachmentID = attachmentIDFromState(wrap.get());
        if (wrap.get() != 0) {
            wrap.position();
        }
        final LLVector3 floatVec = LLVector3.parseFloatVec(wrap);
        final LLVector3 u16Vec = LLVector3.parseU16Vec(wrap, -128.0f, 128.0f, -128.0f, 128.0f);
        this.objectCoords.set(0, floatVec);
        this.objectCoords.set(2, u16Vec);
        wrap.position();
        this.rotation = LLQuaternion.parseU16Vec3(wrap, -1.0f, 1.0f);
        wrap.position();
        if (objectData.TextureEntry.length > 4) {
            final ByteBuffer wrap2 = ByteBuffer.wrap(objectData.TextureEntry);
            wrap2.position();
            final SLTextureEntry create = SLTextureEntry.create(wrap2, wrap2.remaining());
            this.onTexturesUpdate(create);
            final PrimDrawParams primDrawParams = this.primDrawParams;
            if (primDrawParams != null && !create.equals(primDrawParams.getTextures())) {
                final PrimDrawParams value = PrimParamsPool.get(new PrimDrawParams(primDrawParams.getVolumeParams(), create));
                if (!Objects.equal(this.primDrawParams, value)) {
                    this.primDrawParams = value;
                    final DrawableObject drawableObject = this.getDrawableObject();
                    if (drawableObject != null) {
                        drawableObject.setPrimDrawParams(this.primDrawParams);
                    }
                }
            }
        }
        this.updateSpatialIndex(false);
    }
    
    public void addChild(SLObjectInfo attachedTo) {
        synchronized (this) {
            this.treeNode.addChild(attachedTo.treeNode);
            if (attachedTo.isAttachment) {
                attachedTo = attachedTo.getAttachedTo();
                if (attachedTo != null) {
                    attachedTo.updateAttachments();
                }
            }
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
        final SLObjectInfo parentObject = this.getParentObject();
        final LLVector3 value = this.objectCoords.get(0);
        SLObjectInfo parentObject2 = parentObject;
        if (parentObject == null) {
            return value;
        }
        while (parentObject2 != null) {
            parentObject2.objectCoords.addToVector(0, value);
            parentObject2 = parentObject2.getParentObject();
        }
        return value;
    }
    
    public SLObjectInfo getAttachedTo() {
        final SLObjectInfo parentObject = this.getParentObject();
        if (parentObject == null) {
            return null;
        }
        if (parentObject.isAvatar()) {
            return parentObject;
        }
        return parentObject.getAttachedTo();
    }
    
    public String getDescription() {
        return this.description;
    }
    
    @Nonnull
    public DrawListObjectEntry getDrawListEntry() {
        final WeakReference<DrawListObjectEntry> drawListEntry = this.drawListEntry;
        Label_0071: {
            if (drawListEntry == null) {
                break Label_0071;
            }
            final Object o = drawListEntry.get();
            while (true) {
                Object drawListEntry2 = o;
                if (o != null) {
                    return (DrawListObjectEntry)drawListEntry2;
                }
                synchronized (this) {
                    final WeakReference<DrawListObjectEntry> drawListEntry3 = this.drawListEntry;
                    DrawListObjectEntry drawListObjectEntry;
                    if (drawListEntry3 != null) {
                        drawListObjectEntry = drawListEntry3.get();
                    }
                    else {
                        drawListObjectEntry = null;
                    }
                    drawListEntry2 = drawListObjectEntry;
                    if (drawListObjectEntry == null) {
                        drawListEntry2 = this.createDrawListEntry();
                        this.drawListEntry = new WeakReference<DrawListObjectEntry>((DrawListObjectEntry)drawListEntry2);
                    }
                    return (DrawListObjectEntry)drawListEntry2;
                    continue;
                }
                break;
            }
        }
    }
    
    @Nullable
    public DrawListObjectEntry getExistingDrawListEntry() {
        DrawListObjectEntry drawListObjectEntry = null;
        final WeakReference<DrawListObjectEntry> drawListEntry = this.drawListEntry;
        if (drawListEntry != null) {
            drawListObjectEntry = (DrawListObjectEntry)drawListEntry.get();
        }
        return drawListObjectEntry;
    }
    
    @Nullable
    public HoverText getHoverText() {
        return this.hoverText;
    }
    
    @Override
    public UUID getId() {
        return this.uuid;
    }
    
    public String getName() {
        return this.name;
    }
    
    public Vector3Array getObjectCoords() {
        return this.objectCoords;
    }
    
    public void getObjectExtents(final MatrixStack matrixStack, final boolean b, final LLVector3 llVector3, final LLVector3 llVector4) {
        final float[] array = new float[8];
        final int elementOffset = this.objectCoords.getElementOffset(0);
        final int elementOffset2 = this.objectCoords.getElementOffset(1);
        final float[] data = this.objectCoords.getData();
        matrixStack.glPushMatrix();
        matrixStack.glTranslatef(data[elementOffset + 0], data[elementOffset + 1], data[elementOffset + 2]);
        matrixStack.glMultMatrixf(this.rotation.getInverseMatrix(), 0);
        array[0] = -data[elementOffset2 + 0] / 2.0f;
        array[1] = -data[elementOffset2 + 1] / 2.0f;
        array[2] = -data[elementOffset2 + 2] / 2.0f;
        array[3] = 1.0f;
        Matrix.multiplyMV(array, 4, matrixStack.getMatrixData(), matrixStack.getMatrixDataOffset(), array, 0);
        if (b) {
            llVector3.x = array[4];
            llVector3.y = array[5];
            llVector3.z = array[6];
            llVector4.x = array[4];
            llVector4.y = array[5];
            llVector4.z = array[6];
        }
        else {
            llVector3.x = Math.min(llVector3.x, array[4]);
            llVector3.y = Math.min(llVector3.y, array[5]);
            llVector3.z = Math.min(llVector3.z, array[6]);
            llVector4.x = Math.max(llVector4.x, array[4]);
            llVector4.y = Math.max(llVector4.y, array[5]);
            llVector4.z = Math.max(llVector4.z, array[6]);
        }
        array[0] = data[elementOffset2 + 0] / 2.0f;
        array[1] = data[elementOffset2 + 1] / 2.0f;
        array[2] = data[elementOffset2 + 2] / 2.0f;
        array[3] = 1.0f;
        Matrix.multiplyMV(array, 4, matrixStack.getMatrixData(), matrixStack.getMatrixDataOffset(), array, 0);
        llVector3.x = Math.min(llVector3.x, array[4]);
        llVector3.y = Math.min(llVector3.y, array[5]);
        llVector3.z = Math.min(llVector3.z, array[6]);
        llVector4.x = Math.max(llVector4.x, array[4]);
        llVector4.y = Math.max(llVector4.y, array[5]);
        llVector4.z = Math.max(llVector4.z, array[6]);
        try {
            final Iterator<Object> iterator = this.treeNode.iterator();
            while (iterator.hasNext()) {
                iterator.next().getObjectExtents(matrixStack, false, llVector3, llVector4);
            }
        }
        catch (final NoSuchElementException ex) {
            ex.printStackTrace();
        }
        matrixStack.glPopMatrix();
    }
    
    public UUID getOwnerUUID() {
        if (this.ownerUUID != null && this.ownerUUID.getLeastSignificantBits() == 0L && this.ownerUUID.getMostSignificantBits() == 0L) {
            return this.creatorUUID;
        }
        return this.ownerUUID;
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
        final SLObjectInfo slObjectInfo = this.treeNode.getParent();
        if (slObjectInfo == null) {
            return this;
        }
        if (slObjectInfo.isAvatar()) {
            return this;
        }
        return slObjectInfo.getRootPrim();
    }
    
    public final LLQuaternion getRotation() {
        return this.rotation;
    }
    
    public String getTouchName() {
        return this.touchName;
    }
    
    public boolean hasTouchableChildren() {
        try {
            final Iterator<Object> iterator = this.treeNode.iterator();
            while (iterator.hasNext()) {
                if (iterator.next().isTouchable()) {
                    return true;
                }
            }
            return false;
        }
        catch (final NoSuchElementException ex) {
            ex.printStackTrace();
        }
        return false;
    }
    
    public abstract boolean isAvatar();
    
    public boolean isAvatarSittingOn() {
        synchronized (this) {
            try {
                for (final SLObjectInfo slObjectInfo : this.treeNode) {
                    if (slObjectInfo instanceof SLObjectAvatarInfo && ((SLObjectAvatarInfo)slObjectInfo).isMyAvatar()) {
                        return true;
                    }
                }
                return false;
            }
            catch (final NoSuchElementException ex) {
                ex.printStackTrace();
            }
            return false;
        }
    }
    
    public boolean isMyAttachment() {
        final SLObjectInfo parentObject = this.getParentObject();
        return parentObject instanceof SLObjectAvatarInfo && ((SLObjectAvatarInfo)parentObject).isMyAvatar();
    }
    
    public final boolean isPayable() {
        boolean b = false;
        if ((this.UpdateFlags & 0x200) != 0x0) {
            b = true;
        }
        return b;
    }
    
    public final boolean isTouchable() {
        boolean b = false;
        if ((this.UpdateFlags & 0x80) != 0x0) {
            b = true;
        }
        return b;
    }
    
    protected void onTexturesUpdate(final SLTextureEntry slTextureEntry) {
    }
    
    public void removeChild(final SLObjectInfo slObjectInfo) {
        synchronized (this) {
            if (slObjectInfo.isAttachment) {
                final SLObjectInfo attachedTo = slObjectInfo.getAttachedTo();
                if (attachedTo != null) {
                    attachedTo.updateAttachments();
                }
            }
            this.treeNode.removeChild(slObjectInfo.treeNode);
        }
    }
    
    public void removeFromSpatialIndex() {
        final DrawListObjectEntry existingDrawListEntry = this.getExistingDrawListEntry();
        if (existingDrawListEntry != null) {
            existingDrawListEntry.requestEntryRemoval();
        }
        if (!this.isAvatar()) {
            final Iterator<Object> iterator = this.treeNode.iterator();
            while (iterator.hasNext()) {
                iterator.next().removeFromSpatialIndex();
            }
        }
    }
    
    public void setIsAttachmentAll(final boolean b) {
        synchronized (this) {
            this.isAttachment = b;
            try {
                for (final SLObjectInfo slObjectInfo : this.treeNode) {
                    if (!slObjectInfo.isAvatar()) {
                        slObjectInfo.setIsAttachmentAll(b);
                    }
                }
            }
            catch (final NoSuchElementException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    public void setPayInfo(@Nullable final PayInfo payInfo) {
        this.payInfo = payInfo;
    }
    
    public void updateSpatialIndex(final boolean b) {
        this.updateSpatialIndex(SpatialIndex.getInstance().getObjectIndex(), b);
    }
    
    public void updateWorldMatrix(final boolean b) {
        final SLObjectInfo parentObject = this.getParentObject();
        float[] array;
        if (parentObject == null) {
            array = IdentityMatrix.getMatrix();
        }
        else if (parentObject.isAvatar()) {
            array = IdentityMatrix.getMatrix();
        }
        else {
            array = parentObject.worldMatrix;
        }
        if (array != null) {
            this.objRadius = this.objectCoords.getMaxComponent(1) / 2.0f;
            final float[] calculateWorldMatrix = this.calculateWorldMatrix(array);
            final float[] worldMatrix = this.worldMatrix;
            if (worldMatrix != null && Arrays.equals(calculateWorldMatrix, worldMatrix)) {
                return;
            }
            this.worldMatrix = calculateWorldMatrix;
            this.objectCoords.set(3, this.worldMatrix[12], this.worldMatrix[13], this.worldMatrix[14]);
            if (b) {
                final Iterator<Object> iterator = this.treeNode.iterator();
                while (iterator.hasNext()) {
                    iterator.next().updateWorldMatrix(true);
                }
            }
        }
    }
}
