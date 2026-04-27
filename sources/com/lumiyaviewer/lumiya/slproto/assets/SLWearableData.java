// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.assets;

import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.Debug;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.assets:
//            AssetFormatException

public class SLWearableData
{
    static class WearableFormatException extends AssetFormatException
    {

        WearableFormatException()
        {
            super("Unsupported wearable format");
        }

        WearableFormatException(Throwable throwable)
        {
            super("Unsupported wearable format", throwable);
        }
    }

    public static class WearableParam
    {

        public final int paramIndex;
        public final float paramValue;

        WearableParam(int i, float f)
        {
            paramIndex = i;
            paramValue = f;
        }
    }

    public static class WearableTexture
    {

        public final int layer;
        public final UUID textureID;

        WearableTexture(int i, UUID uuid)
        {
            layer = i;
            textureID = uuid;
        }
    }


    public final String name;
    public final ImmutableList params;
    public final ImmutableList textures;

    SLWearableData(byte abyte0[])
        throws WearableFormatException
    {
        com.google.common.collect.ImmutableList.Builder builder;
        com.google.common.collect.ImmutableList.Builder builder1;
        Object obj;
        int i;
        try
        {
            abyte0 = new String(abyte0, "ISO-8859-1");
        }
        // Misplaced declaration of an exception variable
        catch (byte abyte0[])
        {
            throw new WearableFormatException(abyte0);
        }
        abyte0 = abyte0.trim().split("\n+");
        if (abyte0.length < 2)
        {
            throw new WearableFormatException();
        }
        if (!abyte0[0].trim().startsWith("LLWearable"))
        {
            throw new WearableFormatException();
        }
        try
        {
            name = abyte0[1];
            builder = ImmutableList.builder();
            builder1 = ImmutableList.builder();
        }
        // Misplaced declaration of an exception variable
        catch (byte abyte0[])
        {
            throw new WearableFormatException(abyte0);
        }
        i = 2;
_L14:
        if (i >= abyte0.length) goto _L2; else goto _L1
_L1:
        obj = abyte0[i].trim().split("\\s+");
        if (obj.length < 1)
        {
            break MISSING_BLOCK_LABEL_564;
        }
        if (!obj[0].equalsIgnoreCase("permissions") && !obj[0].equalsIgnoreCase("sale_info")) goto _L4; else goto _L3
_L3:
        i++;
        if (i >= abyte0.length)
        {
            throw new WearableFormatException();
        }
        int j = i;
        if (!abyte0[i].trim().equalsIgnoreCase("{"))
        {
            throw new WearableFormatException();
        }
_L12:
        i = j;
        if (j >= abyte0.length)
        {
            continue; /* Loop/switch isn't completed */
        }
        NumberFormatException numberformatexception;
        int k;
        int l;
        if (abyte0[j].trim().equalsIgnoreCase("}"))
        {
            i = j + 1;
            continue; /* Loop/switch isn't completed */
        }
        j++;
        continue; /* Loop/switch isn't completed */
_L4:
        if (!obj[0].equalsIgnoreCase("parameters"))
        {
            break MISSING_BLOCK_LABEL_380;
        }
        l = Integer.parseInt(obj[1]);
        j = i + 1;
        k = 0;
_L6:
        i = j;
        if (k >= l)
        {
            continue; /* Loop/switch isn't completed */
        }
        if (j >= abyte0.length)
        {
            throw new WearableFormatException();
        }
        obj = abyte0[j].trim().split("\\s+");
        if (obj.length < 2)
        {
            throw new WearableFormatException();
        }
        break; /* Loop/switch isn't completed */
        obj;
        Debug.Warning(((Throwable) (obj)));
_L7:
        j++;
        k++;
        if (true) goto _L6; else goto _L5
_L5:
        builder.add(new WearableParam(Integer.parseInt(obj[0]), Float.parseFloat(obj[1])));
          goto _L7
        obj;
        Debug.Warning(((Throwable) (obj)));
          goto _L7
        if (!obj[0].equalsIgnoreCase("textures"))
        {
            break; /* Loop/switch isn't completed */
        }
        l = Integer.parseInt(obj[1]);
        j = i + 1;
        k = 0;
_L9:
        i = j;
        if (k >= l)
        {
            continue; /* Loop/switch isn't completed */
        }
        if (j >= abyte0.length)
        {
            throw new WearableFormatException();
        }
        obj = abyte0[j].trim().split("\\s+");
        if (obj.length < 2)
        {
            throw new WearableFormatException();
        }
        break; /* Loop/switch isn't completed */
        obj;
        Debug.Warning(((Throwable) (obj)));
_L10:
        j++;
        k++;
        if (true) goto _L9; else goto _L8
_L8:
        builder1.add(new WearableTexture(Integer.parseInt(obj[0]), UUID.fromString(obj[1])));
          goto _L10
        numberformatexception;
        Debug.Warning(numberformatexception);
          goto _L10
_L2:
        params = builder.build();
        textures = builder1.build();
        return;
        if (true) goto _L12; else goto _L11
_L11:
        i++;
        continue; /* Loop/switch isn't completed */
        i++;
        if (true) goto _L14; else goto _L13
_L13:
    }
}
