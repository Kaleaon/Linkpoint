// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.objects;

import android.opengl.Matrix;
import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.render.DrawableObject;
import com.lumiyaviewer.lumiya.render.MatrixStack;
import com.lumiyaviewer.lumiya.render.avatar.DrawableAvatar;
import com.lumiyaviewer.lumiya.render.spatial.DrawListObjectEntry;
import com.lumiyaviewer.lumiya.render.spatial.DrawListPrimEntry;
import com.lumiyaviewer.lumiya.render.spatial.SpatialIndex;
import com.lumiyaviewer.lumiya.render.spatial.SpatialObjectIndex;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
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
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.objects:
//            UnsupportedObjectTypeException, SLObjectPrimInfo, SLObjectAvatarInfo, HoverText, 
//            PayInfo

public abstract class SLObjectInfo
    implements Identifiable
{

    private static final int AGENT_ATTACH_MASK = 240;
    private static final int AGENT_ATTACH_OFFSET = 4;
    public static final int FLAGS_ALLOW_INVENTORY_DROP = 0x10000;
    public static final int FLAGS_ANIM_SOURCE = 0x200000;
    public static final int FLAGS_CAMERA_DECOUPLED = 0x100000;
    public static final int FLAGS_CAMERA_SOURCE = 0x400000;
    public static final int FLAGS_CAST_SHADOWS = 0x800000;
    public static final int FLAGS_CREATE_SELECTED = 2;
    public static final int FLAGS_HANDLE_TOUCH = 128;
    public static final int FLAGS_INCLUDE_IN_SEARCH = 32768;
    public static final int FLAGS_INVENTORY_EMPTY = 2048;
    public static final int FLAGS_JOINT_HINGE = 4096;
    public static final int FLAGS_JOINT_LP2P = 16384;
    public static final int FLAGS_JOINT_P2P = 8192;
    public static final int FLAGS_OBJECT_ANY_OWNER = 16;
    public static final int FLAGS_OBJECT_COPY = 8;
    public static final int FLAGS_OBJECT_GROUP_OWNED = 0x40000;
    public static final int FLAGS_OBJECT_MODIFY = 4;
    public static final int FLAGS_OBJECT_MOVE = 256;
    public static final int FLAGS_OBJECT_OWNER_MODIFY = 0x10000000;
    public static final int FLAGS_OBJECT_TRANSFER = 0x20000;
    public static final int FLAGS_OBJECT_YOU_OWNER = 32;
    public static final int FLAGS_PHANTOM = 1024;
    public static final int FLAGS_SCRIPTED = 64;
    public static final int FLAGS_TAKES_MONEY = 512;
    public static final int FLAGS_TEMPORARY = 0x40000000;
    public static final int FLAGS_TEMPORARY_ON_REZ = 0x20000000;
    public static final int FLAGS_USE_PHYSICS = 1;
    public static final int FLAGS_ZLIB_COMPRESSED = 0x80000000;
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
    private volatile WeakReference drawListEntry;
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
    private final Vector3Array objectCoords = new Vector3Array(4);
    public UUID ownerUUID;
    public int parentID;
    private PayInfo payInfo;
    private PrimDrawParams primDrawParams;
    private LLQuaternion rotation;
    public int salePrice;
    public byte saleType;
    public String touchName;
    public final LinkedTreeNode treeNode = new LinkedTreeNode(this);
    protected UUID uuid;
    public float worldMatrix[];

    public SLObjectInfo()
    {
        parentID = 0;
        name = "(loading)";
        description = "";
        touchName = "";
        attachedToUUID = null;
        ownerUUID = null;
        creatorUUID = null;
        saleType = 0;
        attachmentID = 0;
        hoverText = null;
        isDead = false;
        isAttachment = false;
        nameKnown = false;
        nameRequested = false;
        nameRequestedAt = 0L;
        hierLevel = 0;
    }

    private void ParseObjectData(ByteBuffer bytebuffer)
    {
        bytebuffer.order(ByteOrder.LITTLE_ENDIAN);
        switch (bytebuffer.limit())
        {
        default:
            return;

        case 76: // 'L'
            bytebuffer.position(bytebuffer.position() + 16);
            // fall through

        case 60: // '<'
            objectCoords.set(0, LLVector3.parseFloatVec(bytebuffer));
            objectCoords.set(2, LLVector3.parseFloatVec(bytebuffer));
            bytebuffer.position(bytebuffer.position() + 12);
            rotation = LLQuaternion.parseFloatVec3(bytebuffer);
            return;

        case 48: // '0'
            bytebuffer.position(bytebuffer.position() + 16);
            // fall through

        case 32: // ' '
            objectCoords.set(0, LLVector3.parseU16Vec(bytebuffer, -128F, 384F, -256F, 4096F));
            objectCoords.set(2, LLVector3.parseU16Vec(bytebuffer, -256F, 256F, -256F, 256F));
            bytebuffer.position(bytebuffer.position() + 6);
            rotation = LLQuaternion.parseU16Vec3(bytebuffer, -1F, 1.0F);
            return;

        case 16: // '\020'
            objectCoords.set(0, LLVector3.parseU8Vec(bytebuffer, 384F, 384F, -256F, 4096F));
            objectCoords.set(2, LLVector3.parseU8Vec(bytebuffer, -256F, 256F, -256F, 256F));
            bytebuffer.position(bytebuffer.position() + 3);
            rotation = LLQuaternion.parseU8Vec3(bytebuffer, -1F, 1.0F);
            return;
        }
    }

    private void applyHoverText(HoverText hovertext)
    {
        if (!Objects.equal(hoverText, hovertext))
        {
            hoverText = hovertext;
            DrawableObject drawableobject = getDrawableObject();
            if (drawableobject != null)
            {
                drawableobject.setHoverText(hovertext);
            }
        }
    }

    private static int attachmentIDFromState(int i)
    {
        return (i & 0xff & 0xf0) >> 4 | (i & 0xff & 0xffffff0f) << 4;
    }

    private float[] calculateWorldMatrix(float af[])
    {
        LLQuaternion llquaternion = rotation;
        if (llquaternion != null)
        {
            float af1[] = new float[16];
            float af2[] = new float[16];
            objectCoords.MatrixTranslate(af2, 0, af, 0, 0);
            Matrix.multiplyMM(af1, 0, af2, 0, llquaternion.getInverseMatrix(), 0);
            return af1;
        } else
        {
            return null;
        }
    }

    public static SLObjectInfo create(com.lumiyaviewer.lumiya.slproto.messages.ObjectUpdateCompressed.ObjectData objectdata)
        throws UnsupportedObjectTypeException
    {
        SLObjectPrimInfo slobjectpriminfo = new SLObjectPrimInfo();
        slobjectpriminfo.ApplyObjectUpdate(objectdata);
        return slobjectpriminfo;
    }

    public static SLObjectInfo create(UUID uuid1, com.lumiyaviewer.lumiya.slproto.messages.ObjectUpdate.ObjectData objectdata, UUID uuid2)
    {
        boolean flag;
        if (objectdata.PCode == 47)
        {
            flag = true;
        } else
        {
            flag = false;
        }
        if (flag)
        {
            uuid1 = new SLObjectAvatarInfo(uuid1, UUIDPool.getUUID(objectdata.FullID), uuid2.equals(objectdata.FullID));
        } else
        {
            uuid1 = new SLObjectPrimInfo();
        }
        uuid1.ApplyObjectUpdate(objectdata);
        return uuid1;
    }

    private DrawableObject getDrawableObject()
    {
        DrawListObjectEntry drawlistobjectentry = getExistingDrawListEntry();
        if (drawlistobjectentry instanceof DrawListPrimEntry)
        {
            return ((DrawListPrimEntry)drawlistobjectentry).getDrawableObject();
        } else
        {
            return null;
        }
    }

    public static int getLocalID(com.lumiyaviewer.lumiya.slproto.messages.ImprovedTerseObjectUpdate.ObjectData objectdata)
    {
        objectdata = ByteBuffer.wrap(objectdata.Data);
        objectdata.order(ByteOrder.LITTLE_ENDIAN);
        return objectdata.getInt();
    }

    public static int getLocalID(com.lumiyaviewer.lumiya.slproto.messages.ObjectUpdateCompressed.ObjectData objectdata)
    {
        objectdata = ByteBuffer.wrap(objectdata.Data);
        objectdata.position(16);
        objectdata.order(ByteOrder.LITTLE_ENDIAN);
        return objectdata.getInt();
    }

    private void parseNameValuePairs(String s)
    {
        String as[] = s.split("\n");
        int l = as.length;
        int i = 0;
        while (i < l) 
        {
            s = as[i];
            if (s.startsWith("AttachItemID "))
            {
                for (int j = 0; j < 4; j++)
                {
                    int i1 = s.indexOf(' ');
                    String s1 = s;
                    if (i1 >= 0)
                    {
                        s1 = s.substring(i1 + 1);
                    }
                    s = s1.trim();
                }

                try
                {
                    attachedToUUID = UUIDPool.getUUID(UUID.fromString(s));
                }
                // Misplaced declaration of an exception variable
                catch (String s)
                {
                    attachedToUUID = null;
                }
            } else
            if (s.startsWith("DisplayName "))
            {
                for (int k = 0; k < 4; k++)
                {
                    int j1 = s.indexOf(' ');
                    String s2 = s;
                    if (j1 >= 0)
                    {
                        s2 = s.substring(j1 + 1);
                    }
                    s = s2.trim();
                }

                name = s;
                nameKnown = true;
            }
            i++;
        }
    }

    private void updateAttachments()
    {
        if (isAvatar())
        {
            DrawableAvatar drawableavatar = SpatialIndex.getInstance().getDrawableAvatar(this);
            if (drawableavatar != null)
            {
                drawableavatar.updateAttachments();
            }
        }
    }

    private void updateSpatialIndex(SpatialObjectIndex spatialobjectindex, boolean flag)
    {
        updateWorldMatrix(false);
        if (!flag) goto _L2; else goto _L1
_L1:
        this;
        JVM INSTR monitorenter ;
        drawListEntry = null;
        this;
        JVM INSTR monitorexit ;
_L2:
        if (spatialobjectindex != null && isDead ^ true)
        {
            spatialobjectindex.updateObject(getDrawListEntry());
        }
        if (!isAvatar())
        {
            for (spatialobjectindex = treeNode.iterator(); spatialobjectindex.hasNext(); ((SLObjectInfo)spatialobjectindex.next()).updateSpatialIndex(flag)) { }
        } else
        {
            for (spatialobjectindex = treeNode.iterator(); spatialobjectindex.hasNext(); ((SLObjectInfo)spatialobjectindex.next()).updateWorldMatrix(true)) { }
        }
        break MISSING_BLOCK_LABEL_121;
        spatialobjectindex;
        throw spatialobjectindex;
    }

    public void ApplyObjectProperties(com.lumiyaviewer.lumiya.slproto.messages.ObjectProperties.ObjectData objectdata)
    {
        name = SLMessage.stringFromVariableOEM(objectdata.Name);
        description = SLMessage.stringFromVariableUTF(objectdata.Description);
        touchName = SLMessage.stringFromVariableUTF(objectdata.TouchName);
        creatorUUID = objectdata.CreatorID;
        ownerUUID = objectdata.OwnerID;
        saleType = (byte)objectdata.SaleType;
        salePrice = objectdata.SalePrice;
        nameKnown = true;
        nameRequested = false;
    }

    public void ApplyObjectUpdate(com.lumiyaviewer.lumiya.slproto.messages.ObjectUpdate.ObjectData objectdata)
    {
        Object obj1 = null;
        localID = objectdata.ID;
        uuid = UUIDPool.getUUID(objectdata.FullID);
        UpdateFlags = objectdata.UpdateFlags;
        parentID = objectdata.ParentID;
        attachmentID = attachmentIDFromState(objectdata.State);
        if (objectdata.OwnerID.getLeastSignificantBits() != 0L || objectdata.OwnerID.getMostSignificantBits() != 0L)
        {
            ownerUUID = UUIDPool.getUUID(objectdata.OwnerID);
        }
        objectCoords.set(1, objectdata.Scale);
        Object obj = SLMessage.stringFromVariableOEM(objectdata.Text);
        PrimVolumeParams primvolumeparams;
        int i;
        if (objectdata.TextColor.length >= 4)
        {
            i = objectdata.TextColor[0] & 0xff | objectdata.TextColor[1] << 8 & 0xff00 | objectdata.TextColor[2] << 16 & 0xff0000 | objectdata.TextColor[3] << 24 & 0xff000000;
        } else
        {
            i = 0;
        }
        if (Strings.isNullOrEmpty(((String) (obj))))
        {
            obj = null;
        } else
        {
            obj = HoverText.create(((String) (obj)), i);
        }
        applyHoverText(((HoverText) (obj)));
        primvolumeparams = PrimVolumeParams.createFromObjectUpdate(objectdata);
        if (primvolumeparams != null && objectdata.ExtraParams != null)
        {
            primvolumeparams.unpackExtraParams(ByteBuffer.wrap(objectdata.ExtraParams).order(ByteOrder.LITTLE_ENDIAN));
        }
        ParseObjectData(ByteBuffer.wrap(objectdata.ObjectData));
        obj = obj1;
        if (primvolumeparams != null)
        {
            obj = PrimParamsPool.get(primvolumeparams);
        }
        obj = PrimParamsPool.get(new PrimDrawParams(((PrimVolumeParams) (obj)), SLTextureEntry.create(ByteBuffer.wrap(objectdata.TextureEntry), objectdata.TextureEntry.length)));
        onTexturesUpdate(((PrimDrawParams) (obj)).getTextures());
        if (!Objects.equal(primDrawParams, obj))
        {
            primDrawParams = ((PrimDrawParams) (obj));
            DrawableObject drawableobject = getDrawableObject();
            if (drawableobject != null)
            {
                drawableobject.setPrimDrawParams(primDrawParams);
            }
        }
        primDrawParams = PrimParamsPool.get(((PrimDrawParams) (obj)));
        parseNameValuePairs(SLMessage.stringFromVariableUTF(objectdata.NameValue));
        updateSpatialIndex(false);
    }

    public void ApplyObjectUpdate(com.lumiyaviewer.lumiya.slproto.messages.ObjectUpdateCompressed.ObjectData objectdata)
        throws UnsupportedObjectTypeException
    {
        PrimVolumeParams primvolumeparams;
        ByteBuffer bytebuffer;
        int i;
        int j;
        primvolumeparams = null;
        UpdateFlags = objectdata.UpdateFlags;
        bytebuffer = ByteBuffer.wrap(objectdata.Data);
        bytebuffer.order(ByteOrder.BIG_ENDIAN);
        long l1 = bytebuffer.getLong();
        long l2 = bytebuffer.getLong();
        uuid = UUIDPool.setUUID(uuid, l1, l2);
        bytebuffer.order(ByteOrder.LITTLE_ENDIAN);
        localID = bytebuffer.getInt();
        byte byte0 = bytebuffer.get();
        if (byte0 != 9)
        {
            throw new UnsupportedObjectTypeException(byte0);
        }
        attachmentID = attachmentIDFromState(bytebuffer.get());
        bytebuffer.position(bytebuffer.position() + 4 + 1 + 1);
        objectdata = LLVector3.parseFloatVec(bytebuffer);
        LLVector3 llvector3 = LLVector3.parseFloatVec(bytebuffer);
        objectCoords.set(1, objectdata);
        objectCoords.set(0, llvector3);
        rotation = LLQuaternion.parseFloatVec3(bytebuffer);
        j = bytebuffer.getInt();
        bytebuffer.order(ByteOrder.BIG_ENDIAN);
        l1 = bytebuffer.getLong();
        l2 = bytebuffer.getLong();
        if (ownerUUID == null || l1 != 0L && l2 != 0L)
        {
            ownerUUID = UUIDPool.setUUID(ownerUUID, l1, l2);
        }
        bytebuffer.order(ByteOrder.LITTLE_ENDIAN);
        if ((j & 0x80) != 0)
        {
            bytebuffer.position(bytebuffer.position() + 12);
        }
        if ((j & 0x20) != 0)
        {
            parentID = bytebuffer.getInt();
        }
        int k;
        byte byte1;
        if ((j & 2) != 0)
        {
            bytebuffer.position(bytebuffer.position() + 1);
        } else
        if ((j & 1) != 0)
        {
            bytebuffer.position(bytebuffer.get() + bytebuffer.position());
        }
        if ((j & 4) == 0) goto _L2; else goto _L1
_L1:
        k = bytebuffer.position();
        i = 0;
_L6:
        if (k + i < bytebuffer.capacity() && bytebuffer.get(k + i) != 0) goto _L4; else goto _L3
_L3:
        PrimVolumeParams primvolumeparams1;
        Exception exception;
        int l;
        if (i != 0)
        {
            objectdata = new byte[i];
            bytebuffer.get(objectdata, 0, i);
            try
            {
                objectdata = new String(objectdata, "ISO-8859-1");
            }
            // Misplaced declaration of an exception variable
            catch (com.lumiyaviewer.lumiya.slproto.messages.ObjectUpdateCompressed.ObjectData objectdata)
            {
                objectdata = null;
            }
        } else
        {
            objectdata = null;
        }
        bytebuffer.position(i + k + 1);
        i = bytebuffer.getInt();
        if (Strings.isNullOrEmpty(objectdata))
        {
            objectdata = null;
        } else
        {
            objectdata = HoverText.create(objectdata, i);
        }
        applyHoverText(objectdata);
_L2:
        if ((j & 0x200) != 0)
        {
            while (bytebuffer.get() != 0) ;
        }
        if ((j & 8) != 0)
        {
            bytebuffer.position(bytebuffer.position() + 86);
        }
        l = bytebuffer.position();
        byte1 = bytebuffer.get();
        for (i = 0; i < (byte1 & 0xff); i++)
        {
            bytebuffer.getShort();
            bytebuffer.position(bytebuffer.getInt() + bytebuffer.position());
        }

        break; /* Loop/switch isn't completed */
_L4:
        i++;
        if (true) goto _L6; else goto _L5
_L5:
        if ((j & 0x10) != 0)
        {
            bytebuffer.position(bytebuffer.position() + 16);
            bytebuffer.position(bytebuffer.position() + 4 + 1 + 4);
        }
        if ((j & 0x100) != 0)
        {
            while (bytebuffer.get() != 0) ;
        }
        primvolumeparams1 = PrimVolumeParams.createFromPackedData(bytebuffer);
        objectdata = SLTextureEntry.create(bytebuffer, bytebuffer.getInt());
        onTexturesUpdate(objectdata);
_L8:
        if (primvolumeparams1 != null)
        {
            bytebuffer.position(l);
            primvolumeparams1.unpackExtraParams(bytebuffer);
        }
        if (primvolumeparams1 != null)
        {
            primvolumeparams = PrimParamsPool.get(primvolumeparams1);
        }
        objectdata = PrimParamsPool.get(new PrimDrawParams(primvolumeparams, objectdata));
        if (!Objects.equal(primDrawParams, objectdata))
        {
            primDrawParams = objectdata;
            objectdata = getDrawableObject();
            if (objectdata != null)
            {
                objectdata.setPrimDrawParams(primDrawParams);
            }
        }
        updateSpatialIndex(false);
        return;
        objectdata;
        objectdata = null;
_L9:
        Debug.Log("Failed to retrieve textures in compressed update");
        if (true) goto _L8; else goto _L7
_L7:
        exception;
          goto _L9
    }

    public void ApplyTerseObjectUpdate(com.lumiyaviewer.lumiya.slproto.messages.ImprovedTerseObjectUpdate.ObjectData objectdata)
    {
        ByteBuffer bytebuffer = ByteBuffer.wrap(objectdata.Data);
        bytebuffer.order(ByteOrder.LITTLE_ENDIAN);
        bytebuffer.getInt();
        attachmentID = attachmentIDFromState(bytebuffer.get());
        if (bytebuffer.get() != 0)
        {
            bytebuffer.position(bytebuffer.position() + 16);
        }
        LLVector3 llvector3 = LLVector3.parseFloatVec(bytebuffer);
        LLVector3 llvector3_1 = LLVector3.parseU16Vec(bytebuffer, -128F, 128F, -128F, 128F);
        objectCoords.set(0, llvector3);
        objectCoords.set(2, llvector3_1);
        bytebuffer.position(bytebuffer.position() + 6);
        rotation = LLQuaternion.parseU16Vec3(bytebuffer, -1F, 1.0F);
        bytebuffer.position(bytebuffer.position() + 6);
        if (objectdata.TextureEntry.length > 4)
        {
            objectdata = ByteBuffer.wrap(objectdata.TextureEntry);
            objectdata.position(4);
            objectdata = SLTextureEntry.create(objectdata, objectdata.remaining());
            onTexturesUpdate(objectdata);
            PrimDrawParams primdrawparams = primDrawParams;
            if (primdrawparams != null && !objectdata.equals(primdrawparams.getTextures()))
            {
                objectdata = PrimParamsPool.get(new PrimDrawParams(primdrawparams.getVolumeParams(), objectdata));
                if (!Objects.equal(primDrawParams, objectdata))
                {
                    primDrawParams = objectdata;
                    objectdata = getDrawableObject();
                    if (objectdata != null)
                    {
                        objectdata.setPrimDrawParams(primDrawParams);
                    }
                }
            }
        }
        updateSpatialIndex(false);
    }

    public void addChild(SLObjectInfo slobjectinfo)
    {
        this;
        JVM INSTR monitorenter ;
        treeNode.addChild(slobjectinfo.treeNode);
        if (!slobjectinfo.isAttachment)
        {
            break MISSING_BLOCK_LABEL_33;
        }
        slobjectinfo = slobjectinfo.getAttachedTo();
        if (slobjectinfo == null)
        {
            break MISSING_BLOCK_LABEL_33;
        }
        slobjectinfo.updateAttachments();
        this;
        JVM INSTR monitorexit ;
        return;
        slobjectinfo;
        throw slobjectinfo;
    }

    public void clearDrawListEntry()
    {
        this;
        JVM INSTR monitorenter ;
        drawListEntry = null;
        this;
        JVM INSTR monitorexit ;
        return;
        Exception exception;
        exception;
        throw exception;
    }

    protected abstract DrawListObjectEntry createDrawListEntry();

    public LLVector3 getAbsolutePosition()
    {
        SLObjectInfo slobjectinfo1 = getParentObject();
        LLVector3 llvector3 = objectCoords.get(0);
        SLObjectInfo slobjectinfo = slobjectinfo1;
        if (slobjectinfo1 == null)
        {
            return llvector3;
        }
        for (; slobjectinfo != null; slobjectinfo = slobjectinfo.getParentObject())
        {
            slobjectinfo.objectCoords.addToVector(0, llvector3);
        }

        return llvector3;
    }

    public SLObjectInfo getAttachedTo()
    {
        SLObjectInfo slobjectinfo = getParentObject();
        if (slobjectinfo != null)
        {
            if (slobjectinfo.isAvatar())
            {
                return slobjectinfo;
            } else
            {
                return slobjectinfo.getAttachedTo();
            }
        } else
        {
            return null;
        }
    }

    public String getDescription()
    {
        return description;
    }

    public DrawListObjectEntry getDrawListEntry()
    {
        Object obj;
        obj = drawListEntry;
        DrawListObjectEntry drawlistobjectentry;
        if (obj != null)
        {
            obj = (DrawListObjectEntry)((WeakReference) (obj)).get();
        } else
        {
            obj = null;
        }
        drawlistobjectentry = ((DrawListObjectEntry) (obj));
        if (obj != null) goto _L2; else goto _L1
_L1:
        this;
        JVM INSTR monitorenter ;
        obj = drawListEntry;
        if (obj == null) goto _L4; else goto _L3
_L3:
        obj = (DrawListObjectEntry)((WeakReference) (obj)).get();
_L6:
        drawlistobjectentry = ((DrawListObjectEntry) (obj));
        if (obj != null)
        {
            break MISSING_BLOCK_LABEL_65;
        }
        drawlistobjectentry = createDrawListEntry();
        drawListEntry = new WeakReference(drawlistobjectentry);
        this;
        JVM INSTR monitorexit ;
_L2:
        return drawlistobjectentry;
_L4:
        obj = null;
        if (true) goto _L6; else goto _L5
_L5:
        Exception exception;
        exception;
        throw exception;
    }

    public DrawListObjectEntry getExistingDrawListEntry()
    {
        DrawListObjectEntry drawlistobjectentry = null;
        WeakReference weakreference = drawListEntry;
        if (weakreference != null)
        {
            drawlistobjectentry = (DrawListObjectEntry)weakreference.get();
        }
        return drawlistobjectentry;
    }

    public HoverText getHoverText()
    {
        return hoverText;
    }

    public volatile Object getId()
    {
        return getId();
    }

    public UUID getId()
    {
        return uuid;
    }

    public String getName()
    {
        return name;
    }

    public Vector3Array getObjectCoords()
    {
        return objectCoords;
    }

    public void getObjectExtents(MatrixStack matrixstack, boolean flag, LLVector3 llvector3, LLVector3 llvector3_1)
    {
        Object obj = new float[8];
        int i = objectCoords.getElementOffset(0);
        int j = objectCoords.getElementOffset(1);
        float af[] = objectCoords.getData();
        matrixstack.glPushMatrix();
        matrixstack.glTranslatef(af[i + 0], af[i + 1], af[i + 2]);
        matrixstack.glMultMatrixf(rotation.getInverseMatrix(), 0);
        obj[0] = -af[j + 0] / 2.0F;
        obj[1] = -af[j + 1] / 2.0F;
        obj[2] = -af[j + 2] / 2.0F;
        obj[3] = 1.0F;
        Matrix.multiplyMV(((float []) (obj)), 4, matrixstack.getMatrixData(), matrixstack.getMatrixDataOffset(), ((float []) (obj)), 0);
        if (flag)
        {
            llvector3.x = obj[4];
            llvector3.y = obj[5];
            llvector3.z = obj[6];
            llvector3_1.x = obj[4];
            llvector3_1.y = obj[5];
            llvector3_1.z = obj[6];
        } else
        {
            llvector3.x = Math.min(llvector3.x, obj[4]);
            llvector3.y = Math.min(llvector3.y, obj[5]);
            llvector3.z = Math.min(llvector3.z, obj[6]);
            llvector3_1.x = Math.max(llvector3_1.x, obj[4]);
            llvector3_1.y = Math.max(llvector3_1.y, obj[5]);
            llvector3_1.z = Math.max(llvector3_1.z, obj[6]);
        }
        obj[0] = af[j + 0] / 2.0F;
        obj[1] = af[j + 1] / 2.0F;
        obj[2] = af[j + 2] / 2.0F;
        obj[3] = 1.0F;
        Matrix.multiplyMV(((float []) (obj)), 4, matrixstack.getMatrixData(), matrixstack.getMatrixDataOffset(), ((float []) (obj)), 0);
        llvector3.x = Math.min(llvector3.x, obj[4]);
        llvector3.y = Math.min(llvector3.y, obj[5]);
        llvector3.z = Math.min(llvector3.z, obj[6]);
        llvector3_1.x = Math.max(llvector3_1.x, obj[4]);
        llvector3_1.y = Math.max(llvector3_1.y, obj[5]);
        llvector3_1.z = Math.max(llvector3_1.z, obj[6]);
        try
        {
            for (obj = treeNode.iterator(); ((Iterator) (obj)).hasNext(); ((SLObjectInfo)((Iterator) (obj)).next()).getObjectExtents(matrixstack, false, llvector3, llvector3_1)) { }
        }
        // Misplaced declaration of an exception variable
        catch (LLVector3 llvector3)
        {
            llvector3.printStackTrace();
        }
        matrixstack.glPopMatrix();
    }

    public UUID getOwnerUUID()
    {
        if (ownerUUID != null && ownerUUID.getLeastSignificantBits() == 0L && ownerUUID.getMostSignificantBits() == 0L)
        {
            return creatorUUID;
        } else
        {
            return ownerUUID;
        }
    }

    public SLObjectInfo getParentObject()
    {
        return (SLObjectInfo)treeNode.getParent();
    }

    public PayInfo getPayInfo()
    {
        return payInfo;
    }

    public PrimDrawParams getPrimDrawParams()
    {
        return primDrawParams;
    }

    public SLObjectInfo getRootPrim()
    {
        SLObjectInfo slobjectinfo = (SLObjectInfo)treeNode.getParent();
        if (slobjectinfo == null)
        {
            return this;
        }
        if (slobjectinfo.isAvatar())
        {
            return this;
        } else
        {
            return slobjectinfo.getRootPrim();
        }
    }

    public final LLQuaternion getRotation()
    {
        return rotation;
    }

    public String getTouchName()
    {
        return touchName;
    }

    public boolean hasTouchableChildren()
    {
        Iterator iterator = treeNode.iterator();
        boolean flag;
        do
        {
            if (!iterator.hasNext())
            {
                break MISSING_BLOCK_LABEL_43;
            }
            flag = ((SLObjectInfo)iterator.next()).isTouchable();
        } while (!flag);
        return true;
        NoSuchElementException nosuchelementexception;
        nosuchelementexception;
        nosuchelementexception.printStackTrace();
        return false;
    }

    public abstract boolean isAvatar();

    public boolean isAvatarSittingOn()
    {
        this;
        JVM INSTR monitorenter ;
        Iterator iterator = treeNode.iterator();
        boolean flag;
        do
        {
            SLObjectInfo slobjectinfo;
            do
            {
                if (!iterator.hasNext())
                {
                    break MISSING_BLOCK_LABEL_59;
                }
                slobjectinfo = (SLObjectInfo)iterator.next();
            } while (!(slobjectinfo instanceof SLObjectAvatarInfo));
            flag = ((SLObjectAvatarInfo)slobjectinfo).isMyAvatar();
        } while (!flag);
        this;
        JVM INSTR monitorexit ;
        return true;
        Object obj;
        obj;
        ((NoSuchElementException) (obj)).printStackTrace();
        this;
        JVM INSTR monitorexit ;
        return false;
        obj;
        throw obj;
    }

    public boolean isMyAttachment()
    {
        SLObjectInfo slobjectinfo = getParentObject();
        if (slobjectinfo instanceof SLObjectAvatarInfo)
        {
            return ((SLObjectAvatarInfo)slobjectinfo).isMyAvatar();
        } else
        {
            return false;
        }
    }

    public final boolean isPayable()
    {
        boolean flag = false;
        if ((UpdateFlags & 0x200) != 0)
        {
            flag = true;
        }
        return flag;
    }

    public final boolean isTouchable()
    {
        boolean flag = false;
        if ((UpdateFlags & 0x80) != 0)
        {
            flag = true;
        }
        return flag;
    }

    protected void onTexturesUpdate(SLTextureEntry sltextureentry)
    {
    }

    public void removeChild(SLObjectInfo slobjectinfo)
    {
        this;
        JVM INSTR monitorenter ;
        SLObjectInfo slobjectinfo1;
        if (!slobjectinfo.isAttachment)
        {
            break MISSING_BLOCK_LABEL_22;
        }
        slobjectinfo1 = slobjectinfo.getAttachedTo();
        if (slobjectinfo1 == null)
        {
            break MISSING_BLOCK_LABEL_22;
        }
        slobjectinfo1.updateAttachments();
        treeNode.removeChild(slobjectinfo.treeNode);
        this;
        JVM INSTR monitorexit ;
        return;
        slobjectinfo;
        throw slobjectinfo;
    }

    public void removeFromSpatialIndex()
    {
        DrawListObjectEntry drawlistobjectentry = getExistingDrawListEntry();
        if (drawlistobjectentry != null)
        {
            drawlistobjectentry.requestEntryRemoval();
        }
        if (!isAvatar())
        {
            for (Iterator iterator = treeNode.iterator(); iterator.hasNext(); ((SLObjectInfo)iterator.next()).removeFromSpatialIndex()) { }
        }
    }

    public void setIsAttachmentAll(boolean flag)
    {
        this;
        JVM INSTR monitorenter ;
        isAttachment = flag;
        Iterator iterator = treeNode.iterator();
        do
        {
            if (!iterator.hasNext())
            {
                break;
            }
            SLObjectInfo slobjectinfo = (SLObjectInfo)iterator.next();
            if (!slobjectinfo.isAvatar())
            {
                slobjectinfo.setIsAttachmentAll(flag);
            }
        } while (true);
        break MISSING_BLOCK_LABEL_56;
        Object obj;
        obj;
        ((NoSuchElementException) (obj)).printStackTrace();
        this;
        JVM INSTR monitorexit ;
        return;
        obj;
        throw obj;
    }

    public void setPayInfo(PayInfo payinfo)
    {
        payInfo = payinfo;
    }

    public void updateSpatialIndex(boolean flag)
    {
        updateSpatialIndex(SpatialIndex.getInstance().getObjectIndex(), flag);
    }

    public void updateWorldMatrix(boolean flag)
    {
        float af[] = getParentObject();
        if (af == null)
        {
            af = IdentityMatrix.getMatrix();
        } else
        if (af.isAvatar())
        {
            af = IdentityMatrix.getMatrix();
        } else
        {
            af = ((SLObjectInfo) (af)).worldMatrix;
        }
        if (af != null)
        {
            objRadius = objectCoords.getMaxComponent(1) / 2.0F;
            af = calculateWorldMatrix(af);
            float af1[] = worldMatrix;
            if (af1 != null && Arrays.equals(af, af1))
            {
                return;
            }
            worldMatrix = af;
            objectCoords.set(3, worldMatrix[12], worldMatrix[13], worldMatrix[14]);
            if (flag)
            {
                for (Iterator iterator = treeNode.iterator(); iterator.hasNext(); ((SLObjectInfo)iterator.next()).updateWorldMatrix(true)) { }
            }
        }
    }
}
