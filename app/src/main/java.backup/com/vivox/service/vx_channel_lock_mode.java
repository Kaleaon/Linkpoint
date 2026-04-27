/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.VxClientProxyJNI;

public final class vx_channel_lock_mode {
    public static final vx_channel_lock_mode channel_lock = new vx_channel_lock_mode("channel_lock");
    public static final vx_channel_lock_mode channel_unlock = new vx_channel_lock_mode("channel_unlock", VxClientProxyJNI.channel_unlock_get());
    private static int swigNext = 0;
    private static vx_channel_lock_mode[] swigValues = new vx_channel_lock_mode[]{channel_unlock, channel_lock};
    private final String swigName;
    private final int swigValue;

    private vx_channel_lock_mode(String string2) {
        this.swigName = string2;
        int n = swigNext;
        swigNext = n + 1;
        this.swigValue = n;
    }

    private vx_channel_lock_mode(String string2, int n) {
        this.swigName = string2;
        this.swigValue = n;
        swigNext = n + 1;
    }

    private vx_channel_lock_mode(String string2, vx_channel_lock_mode vx_channel_lock_mode2) {
        this.swigName = string2;
        this.swigValue = vx_channel_lock_mode2.swigValue;
        swigNext = this.swigValue + 1;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static vx_channel_lock_mode swigToEnum(int n) {
        if (n < swigValues.length && n >= 0 && vx_channel_lock_mode.swigValues[n].swigValue == n) {
            return swigValues[n];
        }
        int n2 = 0;
        while (n2 < swigValues.length) {
            if (vx_channel_lock_mode.swigValues[n2].swigValue == n) {
                return swigValues[n2];
            }
            ++n2;
        }
        throw new IllegalArgumentException("No enum " + vx_channel_lock_mode.class + " with value " + n);
    }

    public final int swigValue() {
        return this.swigValue;
    }

    public String toString() {
        return this.swigName;
    }
}

