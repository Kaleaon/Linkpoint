/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.VxClientProxyJNI;

public final class vx_sessiongroup_playback_mode {
    public static final vx_sessiongroup_playback_mode VX_SESSIONGROUP_PLAYBACK_MODE_NORMAL = new vx_sessiongroup_playback_mode("VX_SESSIONGROUP_PLAYBACK_MODE_NORMAL", VxClientProxyJNI.VX_SESSIONGROUP_PLAYBACK_MODE_NORMAL_get());
    public static final vx_sessiongroup_playback_mode VX_SESSIONGROUP_PLAYBACK_MODE_VOX = new vx_sessiongroup_playback_mode("VX_SESSIONGROUP_PLAYBACK_MODE_VOX", VxClientProxyJNI.VX_SESSIONGROUP_PLAYBACK_MODE_VOX_get());
    private static int swigNext = 0;
    private static vx_sessiongroup_playback_mode[] swigValues = new vx_sessiongroup_playback_mode[]{VX_SESSIONGROUP_PLAYBACK_MODE_NORMAL, VX_SESSIONGROUP_PLAYBACK_MODE_VOX};
    private final String swigName;
    private final int swigValue;

    private vx_sessiongroup_playback_mode(String string2) {
        this.swigName = string2;
        int n = swigNext;
        swigNext = n + 1;
        this.swigValue = n;
    }

    private vx_sessiongroup_playback_mode(String string2, int n) {
        this.swigName = string2;
        this.swigValue = n;
        swigNext = n + 1;
    }

    private vx_sessiongroup_playback_mode(String string2, vx_sessiongroup_playback_mode vx_sessiongroup_playback_mode2) {
        this.swigName = string2;
        this.swigValue = vx_sessiongroup_playback_mode2.swigValue;
        swigNext = this.swigValue + 1;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static vx_sessiongroup_playback_mode swigToEnum(int n) {
        if (n < swigValues.length && n >= 0 && vx_sessiongroup_playback_mode.swigValues[n].swigValue == n) {
            return swigValues[n];
        }
        int n2 = 0;
        while (n2 < swigValues.length) {
            if (vx_sessiongroup_playback_mode.swigValues[n2].swigValue == n) {
                return swigValues[n2];
            }
            ++n2;
        }
        throw new IllegalArgumentException("No enum " + vx_sessiongroup_playback_mode.class + " with value " + n);
    }

    public final int swigValue() {
        return this.swigValue;
    }

    public String toString() {
        return this.swigName;
    }
}

