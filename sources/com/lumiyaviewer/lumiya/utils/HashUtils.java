// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtils
{

    private static MessageDigest MD5 = null;

    public HashUtils()
    {
    }

    public static String MD5_Hash(String s)
    {
        int i = 0;
        MessageDigest messagedigest;
        byte abyte0[];
        MessageDigest messagedigest1;
        try
        {
            messagedigest1 = MD5;
        }
        // Misplaced declaration of an exception variable
        catch (String s)
        {
            throw new IllegalStateException(s);
        }
        messagedigest = messagedigest1;
        if (messagedigest1 != null)
        {
            break MISSING_BLOCK_LABEL_22;
        }
        messagedigest = MessageDigest.getInstance("MD5");
        MD5 = messagedigest;
        messagedigest.update(s.getBytes());
        abyte0 = messagedigest.digest();
        s = "";
_L2:
        if (i >= abyte0.length)
        {
            break; /* Loop/switch isn't completed */
        }
        s = (new StringBuilder()).append(s).append(String.format("%02x", new Object[] {
            Byte.valueOf(abyte0[i])
        })).toString();
        i++;
        if (true) goto _L2; else goto _L1
_L1:
        return s;
    }

}
