/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.VxClientProxyJNI;

public final class vx_session_text_state {
    public static final vx_session_text_state session_text_connected = new vx_session_text_state("session_text_connected");
    public static final vx_session_text_state session_text_connecting = new vx_session_text_state("session_text_connecting");
    public static final vx_session_text_state session_text_disconnected = new vx_session_text_state("session_text_disconnected", VxClientProxyJNI.session_text_disconnected_get());
    public static final vx_session_text_state session_text_disconnecting = new vx_session_text_state("session_text_disconnecting");
    private static int swigNext = 0;
    private static vx_session_text_state[] swigValues = new vx_session_text_state[]{session_text_disconnected, session_text_connected, session_text_connecting, session_text_disconnecting};
    private final String swigName;
    private final int swigValue;

    private vx_session_text_state(String string2) {
        this.swigName = string2;
        int n = swigNext;
        swigNext = n + 1;
        this.swigValue = n;
    }

    private vx_session_text_state(String string2, int n) {
        this.swigName = string2;
        this.swigValue = n;
        swigNext = n + 1;
    }

    private vx_session_text_state(String string2, vx_session_text_state vx_session_text_state2) {
        this.swigName = string2;
        this.swigValue = vx_session_text_state2.swigValue;
        swigNext = this.swigValue + 1;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static vx_session_text_state swigToEnum(int n) {
        if (n < swigValues.length && n >= 0 && vx_session_text_state.swigValues[n].swigValue == n) {
            return swigValues[n];
        }
        int n2 = 0;
        while (n2 < swigValues.length) {
            if (vx_session_text_state.swigValues[n2].swigValue == n) {
                return swigValues[n2];
            }
            ++n2;
        }
        throw new IllegalArgumentException("No enum " + vx_session_text_state.class + " with value " + n);
    }

    public final int swigValue() {
        return this.swigValue;
    }

    public String toString() {
        return this.swigName;
    }
}

