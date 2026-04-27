/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

public final class ND_TEST_TYPE {
    public static final ND_TEST_TYPE ND_TEST_DNS = new ND_TEST_TYPE("ND_TEST_DNS");
    public static final ND_TEST_TYPE ND_TEST_ECHO = new ND_TEST_TYPE("ND_TEST_ECHO");
    public static final ND_TEST_TYPE ND_TEST_ECHO_MEDIA = new ND_TEST_TYPE("ND_TEST_ECHO_MEDIA");
    public static final ND_TEST_TYPE ND_TEST_ECHO_MEDIA_LARGE_PACKET = new ND_TEST_TYPE("ND_TEST_ECHO_MEDIA_LARGE_PACKET");
    public static final ND_TEST_TYPE ND_TEST_ECHO_SIP_FIRST_PORT = new ND_TEST_TYPE("ND_TEST_ECHO_SIP_FIRST_PORT");
    public static final ND_TEST_TYPE ND_TEST_ECHO_SIP_FIRST_PORT_INVITE_REQUEST = new ND_TEST_TYPE("ND_TEST_ECHO_SIP_FIRST_PORT_INVITE_REQUEST");
    public static final ND_TEST_TYPE ND_TEST_ECHO_SIP_FIRST_PORT_INVITE_RESPONSE = new ND_TEST_TYPE("ND_TEST_ECHO_SIP_FIRST_PORT_INVITE_RESPONSE");
    public static final ND_TEST_TYPE ND_TEST_ECHO_SIP_FIRST_PORT_REGISTER_REQUEST = new ND_TEST_TYPE("ND_TEST_ECHO_SIP_FIRST_PORT_REGISTER_REQUEST");
    public static final ND_TEST_TYPE ND_TEST_ECHO_SIP_FIRST_PORT_REGISTER_RESPONSE = new ND_TEST_TYPE("ND_TEST_ECHO_SIP_FIRST_PORT_REGISTER_RESPONSE");
    public static final ND_TEST_TYPE ND_TEST_ECHO_SIP_SECOND_PORT = new ND_TEST_TYPE("ND_TEST_ECHO_SIP_SECOND_PORT");
    public static final ND_TEST_TYPE ND_TEST_ECHO_SIP_SECOND_PORT_INVITE_REQUEST = new ND_TEST_TYPE("ND_TEST_ECHO_SIP_SECOND_PORT_INVITE_REQUEST");
    public static final ND_TEST_TYPE ND_TEST_ECHO_SIP_SECOND_PORT_INVITE_RESPONSE = new ND_TEST_TYPE("ND_TEST_ECHO_SIP_SECOND_PORT_INVITE_RESPONSE");
    public static final ND_TEST_TYPE ND_TEST_ECHO_SIP_SECOND_PORT_REGISTER_REQUEST = new ND_TEST_TYPE("ND_TEST_ECHO_SIP_SECOND_PORT_REGISTER_REQUEST");
    public static final ND_TEST_TYPE ND_TEST_ECHO_SIP_SECOND_PORT_REGISTER_RESPONSE = new ND_TEST_TYPE("ND_TEST_ECHO_SIP_SECOND_PORT_REGISTER_RESPONSE");
    public static final ND_TEST_TYPE ND_TEST_LOCATE_INTERFACE = new ND_TEST_TYPE("ND_TEST_LOCATE_INTERFACE");
    public static final ND_TEST_TYPE ND_TEST_PING_GATEWAY = new ND_TEST_TYPE("ND_TEST_PING_GATEWAY");
    public static final ND_TEST_TYPE ND_TEST_STUN = new ND_TEST_TYPE("ND_TEST_STUN");
    private static int swigNext = 0;
    private static ND_TEST_TYPE[] swigValues = new ND_TEST_TYPE[]{ND_TEST_LOCATE_INTERFACE, ND_TEST_PING_GATEWAY, ND_TEST_DNS, ND_TEST_STUN, ND_TEST_ECHO, ND_TEST_ECHO_SIP_FIRST_PORT, ND_TEST_ECHO_SIP_FIRST_PORT_INVITE_REQUEST, ND_TEST_ECHO_SIP_FIRST_PORT_INVITE_RESPONSE, ND_TEST_ECHO_SIP_FIRST_PORT_REGISTER_REQUEST, ND_TEST_ECHO_SIP_FIRST_PORT_REGISTER_RESPONSE, ND_TEST_ECHO_SIP_SECOND_PORT, ND_TEST_ECHO_SIP_SECOND_PORT_INVITE_REQUEST, ND_TEST_ECHO_SIP_SECOND_PORT_INVITE_RESPONSE, ND_TEST_ECHO_SIP_SECOND_PORT_REGISTER_REQUEST, ND_TEST_ECHO_SIP_SECOND_PORT_REGISTER_RESPONSE, ND_TEST_ECHO_MEDIA, ND_TEST_ECHO_MEDIA_LARGE_PACKET};
    private final String swigName;
    private final int swigValue;

    private ND_TEST_TYPE(String string2) {
        this.swigName = string2;
        int n = swigNext;
        swigNext = n + 1;
        this.swigValue = n;
    }

    private ND_TEST_TYPE(String string2, int n) {
        this.swigName = string2;
        this.swigValue = n;
        swigNext = n + 1;
    }

    private ND_TEST_TYPE(String string2, ND_TEST_TYPE nD_TEST_TYPE) {
        this.swigName = string2;
        this.swigValue = nD_TEST_TYPE.swigValue;
        swigNext = this.swigValue + 1;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static ND_TEST_TYPE swigToEnum(int n) {
        if (n < swigValues.length && n >= 0 && ND_TEST_TYPE.swigValues[n].swigValue == n) {
            return swigValues[n];
        }
        int n2 = 0;
        while (n2 < swigValues.length) {
            if (ND_TEST_TYPE.swigValues[n2].swigValue == n) {
                return swigValues[n2];
            }
            ++n2;
        }
        throw new IllegalArgumentException("No enum " + ND_TEST_TYPE.class + " with value " + n);
    }

    public final int swigValue() {
        return this.swigValue;
    }

    public String toString() {
        return this.swigName;
    }
}

