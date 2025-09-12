// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.https;

import com.lumiyaviewer.lumiya.Debug;
import java.io.BufferedInputStream;
import java.io.IOException;

public class LLSDContentTypeDetector
{
    public static final class LLSDContentType extends Enum
    {

        private static final LLSDContentType $VALUES[];
        public static final LLSDContentType llsdBinary;
        public static final LLSDContentType llsdXML;

        public static LLSDContentType valueOf(String s)
        {
            return (LLSDContentType)Enum.valueOf(com/lumiyaviewer/lumiya/slproto/https/LLSDContentTypeDetector$LLSDContentType, s);
        }

        public static LLSDContentType[] values()
        {
            return $VALUES;
        }

        static 
        {
            llsdXML = new LLSDContentType("llsdXML", 0);
            llsdBinary = new LLSDContentType("llsdBinary", 1);
            $VALUES = (new LLSDContentType[] {
                llsdXML, llsdBinary
            });
        }

        private LLSDContentType(String s, int i)
        {
            super(s, i);
        }
    }


    public LLSDContentTypeDetector()
    {
    }

    public static LLSDContentType DetectContentType(BufferedInputStream bufferedinputstream, String s)
        throws IOException
    {
        byte abyte0[];
        byte abyte1[];
        int i;
        int j;
        bufferedinputstream.mark(64);
        abyte0 = new byte[3];
        abyte0;
        abyte0[0] = -17;
        abyte0[1] = -69;
        abyte0[2] = -65;
        abyte1 = new byte[32];
        j = bufferedinputstream.read(abyte1, 0, abyte1.length);
        if (j < abyte0.length)
        {
            break MISSING_BLOCK_LABEL_331;
        }
        i = 0;
_L6:
        if (i >= j || i >= abyte0.length) goto _L2; else goto _L1
_L1:
        if (abyte1[i] == abyte0[i]) goto _L4; else goto _L3
_L3:
        i = 0;
_L5:
        int k;
        if (i == 0)
        {
            break MISSING_BLOCK_LABEL_331;
        }
        k = abyte0.length;
_L7:
        String s2 = new String(abyte1, k, j - k, "UTF-8");
        bufferedinputstream.reset();
        bufferedinputstream.skip(k);
        String s1;
        boolean flag;
        if (s2.startsWith("<llsd>") || s2.startsWith("<?xml"))
        {
            flag = true;
            i = 0;
        } else
        if (s2.startsWith("<? LLSD/Binary ?>") || s2.startsWith("{") || s2.startsWith("<?llsd/binary"))
        {
            flag = false;
            i = 1;
        } else
        {
            flag = false;
            i = 0;
        }
        if (i != 0)
        {
            bufferedinputstream = "true";
        } else
        {
            bufferedinputstream = "false";
        }
        if (flag)
        {
            s1 = "true";
        } else
        {
            s1 = "false";
        }
        Debug.Printf("LLSD: contentType '%s', detected binary %s, xml %s, skipBytes %d, firstString '%s'", new Object[] {
            s, bufferedinputstream, s1, Integer.valueOf(k), s2
        });
        if (i != 0)
        {
            flag = true;
        }
        k = i;
        if (!flag)
        {
            k = i;
            if (s.equalsIgnoreCase("application/llsd+binary"))
            {
                k = 1;
            }
        }
        if (k != 0)
        {
            Debug.Printf("LLSD: using binary parser", new Object[0]);
            return LLSDContentType.llsdBinary;
        } else
        {
            Debug.Printf("LLSD: using XML parser", new Object[0]);
            return LLSDContentType.llsdXML;
        }
_L2:
        i = 1;
          goto _L5
_L4:
        i++;
          goto _L6
        k = 0;
          goto _L7
    }
}
