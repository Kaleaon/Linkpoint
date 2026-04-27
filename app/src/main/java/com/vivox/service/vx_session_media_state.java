/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.VxClientProxyJNI;

public final class vx_session_media_state {
    public static final vx_session_media_state session_media_connected;
    public static final vx_session_media_state session_media_connecting;
    public static final vx_session_media_state session_media_disconnected;
    public static final vx_session_media_state session_media_disconnecting;
    public static final vx_session_media_state session_media_hold;
    public static final vx_session_media_state session_media_none;
    public static final vx_session_media_state session_media_refer;
    public static final vx_session_media_state session_media_ringing;
    private static int swigNext;
    private static vx_session_media_state[] swigValues;
    private final String swigName;
    private final int swigValue;

    static {
        swigNext = 0;
        session_media_none = new vx_session_media_state("session_media_none", VxClientProxyJNI.session_media_none_get());
        session_media_disconnected = new vx_session_media_state("session_media_disconnected");
        session_media_connected = new vx_session_media_state("session_media_connected");
        session_media_ringing = new vx_session_media_state("session_media_ringing");
        session_media_hold = new vx_session_media_state("session_media_hold");
        session_media_refer = new vx_session_media_state("session_media_refer");
        session_media_connecting = new vx_session_media_state("session_media_connecting");
        session_media_disconnecting = new vx_session_media_state("session_media_disconnecting");
        swigValues = new vx_session_media_state[]{session_media_none, session_media_disconnected, session_media_connected, session_media_ringing, session_media_hold, session_media_refer, session_media_connecting, session_media_disconnecting};
    }

    private vx_session_media_state(String string2) {
        this.swigName = string2;
        int n = swigNext;
        swigNext = n + 1;
        this.swigValue = n;
    }

    private vx_session_media_state(String string2, int n) {
        this.swigName = string2;
        this.swigValue = n;
        swigNext = n + 1;
    }

    private vx_session_media_state(String string2, vx_session_media_state vx_session_media_state2) {
        this.swigName = string2;
        this.swigValue = vx_session_media_state2.swigValue;
        swigNext = this.swigValue + 1;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static vx_session_media_state swigToEnum(int n) {
        if (n < swigValues.length && n >= 0 && vx_session_media_state.swigValues[n].swigValue == n) {
            return swigValues[n];
        }
        int n2 = 0;
        while (n2 < swigValues.length) {
            if (vx_session_media_state.swigValues[n2].swigValue == n) {
                return swigValues[n2];
            }
            ++n2;
        }
        throw new IllegalArgumentException("No enum " + vx_session_media_state.class + " with value " + n);
    }

    public final int swigValue() {
        return this.swigValue;
    }

    public String toString() {
        return this.swigName;
    }
}

