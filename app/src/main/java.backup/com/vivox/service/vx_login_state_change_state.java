/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.VxClientProxyJNI;

public final class vx_login_state_change_state {
    public static final vx_login_state_change_state login_state_error = new vx_login_state_change_state("login_state_error", VxClientProxyJNI.login_state_error_get());
    public static final vx_login_state_change_state login_state_logged_in = new vx_login_state_change_state("login_state_logged_in", VxClientProxyJNI.login_state_logged_in_get());
    public static final vx_login_state_change_state login_state_logged_out = new vx_login_state_change_state("login_state_logged_out", VxClientProxyJNI.login_state_logged_out_get());
    public static final vx_login_state_change_state login_state_logging_in = new vx_login_state_change_state("login_state_logging_in", VxClientProxyJNI.login_state_logging_in_get());
    public static final vx_login_state_change_state login_state_logging_out = new vx_login_state_change_state("login_state_logging_out", VxClientProxyJNI.login_state_logging_out_get());
    public static final vx_login_state_change_state login_state_resetting = new vx_login_state_change_state("login_state_resetting", VxClientProxyJNI.login_state_resetting_get());
    private static int swigNext = 0;
    private static vx_login_state_change_state[] swigValues = new vx_login_state_change_state[]{login_state_logged_out, login_state_logged_in, login_state_logging_in, login_state_logging_out, login_state_resetting, login_state_error};
    private final String swigName;
    private final int swigValue;

    private vx_login_state_change_state(String string2) {
        this.swigName = string2;
        int n = swigNext;
        swigNext = n + 1;
        this.swigValue = n;
    }

    private vx_login_state_change_state(String string2, int n) {
        this.swigName = string2;
        this.swigValue = n;
        swigNext = n + 1;
    }

    private vx_login_state_change_state(String string2, vx_login_state_change_state vx_login_state_change_state2) {
        this.swigName = string2;
        this.swigValue = vx_login_state_change_state2.swigValue;
        swigNext = this.swigValue + 1;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static vx_login_state_change_state swigToEnum(int n) {
        if (n < swigValues.length && n >= 0 && vx_login_state_change_state.swigValues[n].swigValue == n) {
            return swigValues[n];
        }
        int n2 = 0;
        while (n2 < swigValues.length) {
            if (vx_login_state_change_state.swigValues[n2].swigValue == n) {
                return swigValues[n2];
            }
            ++n2;
        }
        throw new IllegalArgumentException("No enum " + vx_login_state_change_state.class + " with value " + n);
    }

    public final int swigValue() {
        return this.swigValue;
    }

    public String toString() {
        return this.swigName;
    }
}

