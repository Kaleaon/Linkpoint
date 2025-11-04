// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.utils;

import java.security.NoSuchAlgorithmException;
import javax.annotation.Nonnull;
import java.security.MessageDigest;

public class HashUtils
{
    private static MessageDigest MD5;
    
    static {
        HashUtils.MD5 = null;
    }
    
    @Nonnull
    public static String MD5_Hash(@Nonnull String string) {
        int i = 0;
        try {
            MessageDigest md5;
            if ((md5 = HashUtils.MD5) == null) {
                md5 = (HashUtils.MD5 = MessageDigest.getInstance("MD5"));
            }
            md5.update(string.getBytes());
            final byte[] digest = md5.digest();
            string = "";
            while (i < digest.length) {
                string += String.format("%02x", digest[i]);
                ++i;
            }
            return string;
        }
        catch (final NoSuchAlgorithmException cause) {
            throw new IllegalStateException(cause);
        }
    }
}
