// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.assets;

import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.assets:
//            AssetFormatException

public class SLLandmark
{
    public static class LandmarkFormatException extends AssetFormatException
    {

        private static final long serialVersionUID = 0xe53fb44c35f3e6a5L;

        public LandmarkFormatException()
        {
            super("Unsupported landmark format");
        }
    }


    public LLVector3 localPos;
    public UUID regionUUID;

    public SLLandmark(byte abyte0[])
        throws LandmarkFormatException
    {
        try
        {
            abyte0 = new String(abyte0, "ISO-8859-1");
        }
        // Misplaced declaration of an exception variable
        catch (byte abyte0[])
        {
            throw new LandmarkFormatException();
        }
        abyte0 = abyte0.trim().split("\n+");
        if (abyte0.length < 1)
        {
            throw new LandmarkFormatException();
        }
        if (!abyte0[0].trim().equalsIgnoreCase("Landmark version 2"))
        {
            throw new LandmarkFormatException();
        }
        int i = 1;
        while (i < abyte0.length) 
        {
            String as[] = abyte0[i].trim().split("\\s+");
            if (as.length >= 1)
            {
                if (as[0].equalsIgnoreCase("region_id"))
                {
                    regionUUID = UUID.fromString(as[1]);
                } else
                if (as[0].equalsIgnoreCase("local_pos"))
                {
                    localPos = new LLVector3();
                    localPos.x = Float.parseFloat(as[1]);
                    localPos.y = Float.parseFloat(as[2]);
                    localPos.z = Float.parseFloat(as[3]);
                }
            }
            i++;
        }
    }
}
