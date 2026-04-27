/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.VxClientProxyJNI;

public final class vx_mute_scope {
    public static final vx_mute_scope mute_scope_all = new vx_mute_scope("mute_scope_all", VxClientProxyJNI.mute_scope_all_get());
    public static final vx_mute_scope mute_scope_audio = new vx_mute_scope("mute_scope_audio", VxClientProxyJNI.mute_scope_audio_get());
    public static final vx_mute_scope mute_scope_text = new vx_mute_scope("mute_scope_text", VxClientProxyJNI.mute_scope_text_get());
    private static int swigNext = 0;
    private static vx_mute_scope[] swigValues = new vx_mute_scope[]{mute_scope_all, mute_scope_audio, mute_scope_text};
    private final String swigName;
    private final int swigValue;

    private vx_mute_scope(String string2) {
        this.swigName = string2;
        int n = swigNext;
        swigNext = n + 1;
        this.swigValue = n;
    }

    private vx_mute_scope(String string2, int n) {
        this.swigName = string2;
        this.swigValue = n;
        swigNext = n + 1;
    }

    private vx_mute_scope(String string2, vx_mute_scope vx_mute_scope2) {
        this.swigName = string2;
        this.swigValue = vx_mute_scope2.swigValue;
        swigNext = this.swigValue + 1;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static vx_mute_scope swigToEnum(int n) {
        if (n < swigValues.length && n >= 0 && vx_mute_scope.swigValues[n].swigValue == n) {
            return swigValues[n];
        }
        int n2 = 0;
        while (n2 < swigValues.length) {
            if (vx_mute_scope.swigValues[n2].swigValue == n) {
                return swigValues[n2];
            }
            ++n2;
        }
        throw new IllegalArgumentException("No enum " + vx_mute_scope.class + " with value " + n);
    }

    public final int swigValue() {
        return this.swigValue;
    }

    public String toString() {
        return this.swigName;
    }
}

