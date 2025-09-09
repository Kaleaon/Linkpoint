package com.lumiyaviewer.lumiya.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.annotation.Nonnull;

public class HashUtils {
    private static MessageDigest MD5 = null;

    @Nonnull
    public static String MD5_Hash(@Nonnull String str) {
        try {
            MessageDigest messageDigest = MD5;
            if (messageDigest == null) {
                messageDigest = MessageDigest.getInstance("MD5");
                MD5 = messageDigest;
            }
            messageDigest.update(str.getBytes());
            byte[] digest = messageDigest.digest();
            String str2 = "";
            for (int i = 0; i < digest.length; i++) {
                str2 = str2 + String.format("%02x", new Object[]{Byte.valueOf(digest[i])});
            }
            return str2;
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
