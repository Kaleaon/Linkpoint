package com.lumiyaviewer.lumiya.slproto;

/**
 * Stub class for SLMessage to resolve LLSD dependencies
 */
public class SLMessage {
    public static byte[] stringToVariableUTF(String str) {
        if (str == null) return new byte[0];
        return str.getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }
}
