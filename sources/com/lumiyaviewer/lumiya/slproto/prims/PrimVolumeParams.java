// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.prims;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.prims:
//            PrimPathParams, PrimParamsPool, PrimProfileParams, PrimFlexibleParams

public class PrimVolumeParams
{

    public static final byte LL_SCULPT_FLAG_INVERT = 64;
    public static final byte LL_SCULPT_FLAG_MIRROR = -128;
    public static final byte LL_SCULPT_TYPE_CYLINDER = 4;
    public static final byte LL_SCULPT_TYPE_MASK = 7;
    public static final byte LL_SCULPT_TYPE_MESH = 5;
    public static final byte LL_SCULPT_TYPE_NONE = 0;
    public static final byte LL_SCULPT_TYPE_PLANE = 3;
    public static final byte LL_SCULPT_TYPE_SPHERE = 1;
    public static final byte LL_SCULPT_TYPE_TORUS = 2;
    public static final short PARAMS_FLEXIBLE = 16;
    public static final short PARAMS_LIGHT = 32;
    public static final short PARAMS_LIGHT_IMAGE = 64;
    public static final short PARAMS_MESH = 96;
    public static final short PARAMS_RESERVED = 80;
    public static final short PARAMS_SCULPT = 48;
    public PrimFlexibleParams FlexiParams;
    public PrimPathParams PathParams;
    public PrimProfileParams ProfileParams;
    public UUID SculptID;
    public byte SculptType;

    public PrimVolumeParams()
    {
    }

    public static PrimVolumeParams createFromObjectUpdate(com.lumiyaviewer.lumiya.slproto.messages.ObjectUpdate.ObjectData objectdata)
    {
        PrimVolumeParams primvolumeparams = new PrimVolumeParams();
        primvolumeparams.PathParams = PrimParamsPool.get(new PrimPathParams(objectdata));
        primvolumeparams.ProfileParams = PrimParamsPool.get(PrimProfileParams.createFromObjectUpdate(objectdata));
        return primvolumeparams;
    }

    public static PrimVolumeParams createFromPackedData(ByteBuffer bytebuffer)
    {
        PrimVolumeParams primvolumeparams = new PrimVolumeParams();
        primvolumeparams.PathParams = PrimParamsPool.get(new PrimPathParams(bytebuffer));
        primvolumeparams.ProfileParams = PrimParamsPool.get(PrimProfileParams.createFromPackedData(bytebuffer));
        return primvolumeparams;
    }

    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }
        if (!(obj instanceof PrimVolumeParams))
        {
            return false;
        }
        obj = (PrimVolumeParams)obj;
        if (SculptType != ((PrimVolumeParams) (obj)).SculptType)
        {
            return false;
        }
        boolean flag;
        boolean flag1;
        if (SculptID == null)
        {
            flag = true;
        } else
        {
            flag = false;
        }
        if (((PrimVolumeParams) (obj)).SculptID == null)
        {
            flag1 = true;
        } else
        {
            flag1 = false;
        }
        if (flag != flag1)
        {
            return false;
        }
        if (SculptID != null && !SculptID.equals(((PrimVolumeParams) (obj)).SculptID))
        {
            return false;
        }
        if (ProfileParams == null)
        {
            flag = true;
        } else
        {
            flag = false;
        }
        if (((PrimVolumeParams) (obj)).ProfileParams == null)
        {
            flag1 = true;
        } else
        {
            flag1 = false;
        }
        if (flag != flag1)
        {
            return false;
        }
        if (ProfileParams != null && !ProfileParams.equals(((PrimVolumeParams) (obj)).ProfileParams))
        {
            return false;
        }
        if (PathParams == null)
        {
            flag = true;
        } else
        {
            flag = false;
        }
        if (((PrimVolumeParams) (obj)).PathParams == null)
        {
            flag1 = true;
        } else
        {
            flag1 = false;
        }
        if (flag != flag1)
        {
            return false;
        }
        if (PathParams != null && !PathParams.equals(((PrimVolumeParams) (obj)).PathParams))
        {
            return false;
        }
        if (FlexiParams == null)
        {
            flag = true;
        } else
        {
            flag = false;
        }
        if (((PrimVolumeParams) (obj)).FlexiParams == null)
        {
            flag1 = true;
        } else
        {
            flag1 = false;
        }
        if (flag != flag1)
        {
            return false;
        }
        return FlexiParams == null || FlexiParams.equals(((PrimVolumeParams) (obj)).FlexiParams);
    }

    public int hashCode()
    {
        int j = SculptType * 17 + 0;
        int i = j;
        if (SculptID != null)
        {
            i = j + SculptID.hashCode() * 3;
        }
        j = i + PathParams.hashCode() * 37 + ProfileParams.hashCode();
        i = j;
        if (FlexiParams != null)
        {
            i = j + FlexiParams.hashCode();
        }
        return i;
    }

    public boolean isFlexible()
    {
        return FlexiParams != null;
    }

    public boolean isMesh()
    {
        boolean flag1 = false;
        boolean flag = flag1;
        if (SculptID != null)
        {
            flag = flag1;
            if ((SculptType & 7) == 5)
            {
                flag = true;
            }
        }
        return flag;
    }

    public boolean isSculpt()
    {
        return SculptID != null;
    }

    public String toString()
    {
        StringBuilder stringbuilder = (new StringBuilder()).append("{Volume: SculptType 0x").append(Integer.toHexString(SculptType)).append(", SculptID ");
        String s;
        if (SculptID != null)
        {
            s = SculptID.toString();
        } else
        {
            s = "null";
        }
        return stringbuilder.append(s).append(", Path = (").append(PathParams.toString()).append("), Profile = (").append(ProfileParams.toString()).append(")}").toString();
    }

    public void unpackExtraParams(ByteBuffer bytebuffer)
    {
        byte byte0 = bytebuffer.get();
        int i = 0;
_L6:
        if (i >= byte0) goto _L2; else goto _L1
_L1:
        short word0;
        int j;
        word0 = bytebuffer.getShort();
        j = bytebuffer.getInt() + bytebuffer.position();
        word0;
        JVM INSTR lookupswitch 3: default 150
    //                   16: 133
    //                   48: 78
    //                   96: 78;
           goto _L3 _L4 _L5 _L5
_L3:
        break; /* Loop/switch isn't completed */
_L4:
        break MISSING_BLOCK_LABEL_133;
_L7:
        bytebuffer.position(j);
        i++;
          goto _L6
_L5:
        bytebuffer.order(ByteOrder.BIG_ENDIAN);
        SculptID = UUIDPool.getUUID(new UUID(bytebuffer.getLong(), bytebuffer.getLong()));
        bytebuffer.order(ByteOrder.LITTLE_ENDIAN);
        SculptType = bytebuffer.get();
          goto _L7
        bytebuffer;
        Debug.Warning(bytebuffer);
_L2:
        return;
        FlexiParams = new PrimFlexibleParams(bytebuffer, j);
          goto _L7
    }
}
