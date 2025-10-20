package com.linkpoint.slproto

/**
 * Stub class for SLMessage to resolve LLSD dependencies
 */
class SLMessage {
    @JvmStatic
    Byte[] stringToVariableUTF(String str) {
        if (str == null) return Byte[0]
        return str.getBytes(java.nio.charset.StandardCharsets.UTF_8)
    }
}
