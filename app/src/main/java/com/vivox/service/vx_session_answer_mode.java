/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.VxClientProxyJNI;

public final class vx_session_answer_mode {
    public static final vx_session_answer_mode mode_auto_answer = new vx_session_answer_mode("mode_auto_answer", VxClientProxyJNI.mode_auto_answer_get());
    public static final vx_session_answer_mode mode_busy_answer = new vx_session_answer_mode("mode_busy_answer", VxClientProxyJNI.mode_busy_answer_get());
    public static final vx_session_answer_mode mode_none = new vx_session_answer_mode("mode_none", VxClientProxyJNI.mode_none_get());
    public static final vx_session_answer_mode mode_verify_answer = new vx_session_answer_mode("mode_verify_answer", VxClientProxyJNI.mode_verify_answer_get());
    private static int swigNext = 0;
    private static vx_session_answer_mode[] swigValues = new vx_session_answer_mode[]{mode_none, mode_auto_answer, mode_verify_answer, mode_busy_answer};
    private final String swigName;
    private final int swigValue;

    private vx_session_answer_mode(String string2) {
        this.swigName = string2;
        int n = swigNext;
        swigNext = n + 1;
        this.swigValue = n;
    }

    private vx_session_answer_mode(String string2, int n) {
        this.swigName = string2;
        this.swigValue = n;
        swigNext = n + 1;
    }

    private vx_session_answer_mode(String string2, vx_session_answer_mode vx_session_answer_mode2) {
        this.swigName = string2;
        this.swigValue = vx_session_answer_mode2.swigValue;
        swigNext = this.swigValue + 1;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static vx_session_answer_mode swigToEnum(int n) {
        if (n < swigValues.length && n >= 0 && vx_session_answer_mode.swigValues[n].swigValue == n) {
            return swigValues[n];
        }
        int n2 = 0;
        while (n2 < swigValues.length) {
            if (vx_session_answer_mode.swigValues[n2].swigValue == n) {
                return swigValues[n2];
            }
            ++n2;
        }
        throw new IllegalArgumentException("No enum " + vx_session_answer_mode.class + " with value " + n);
    }

    public final int swigValue() {
        return this.swigValue;
    }

    public String toString() {
        return this.swigName;
    }
}

