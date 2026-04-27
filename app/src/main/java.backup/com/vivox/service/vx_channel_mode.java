/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.VxClientProxyJNI;

public final class vx_channel_mode {
    public static final vx_channel_mode channel_mode_auditorium = new vx_channel_mode("channel_mode_auditorium", VxClientProxyJNI.channel_mode_auditorium_get());
    public static final vx_channel_mode channel_mode_lecture = new vx_channel_mode("channel_mode_lecture", VxClientProxyJNI.channel_mode_lecture_get());
    public static final vx_channel_mode channel_mode_none = new vx_channel_mode("channel_mode_none", VxClientProxyJNI.channel_mode_none_get());
    public static final vx_channel_mode channel_mode_normal = new vx_channel_mode("channel_mode_normal", VxClientProxyJNI.channel_mode_normal_get());
    public static final vx_channel_mode channel_mode_open = new vx_channel_mode("channel_mode_open", VxClientProxyJNI.channel_mode_open_get());
    public static final vx_channel_mode channel_mode_presentation = new vx_channel_mode("channel_mode_presentation", VxClientProxyJNI.channel_mode_presentation_get());
    private static int swigNext = 0;
    private static vx_channel_mode[] swigValues = new vx_channel_mode[]{channel_mode_none, channel_mode_normal, channel_mode_presentation, channel_mode_lecture, channel_mode_open, channel_mode_auditorium};
    private final String swigName;
    private final int swigValue;

    private vx_channel_mode(String string2) {
        this.swigName = string2;
        int n = swigNext;
        swigNext = n + 1;
        this.swigValue = n;
    }

    private vx_channel_mode(String string2, int n) {
        this.swigName = string2;
        this.swigValue = n;
        swigNext = n + 1;
    }

    private vx_channel_mode(String string2, vx_channel_mode vx_channel_mode2) {
        this.swigName = string2;
        this.swigValue = vx_channel_mode2.swigValue;
        swigNext = this.swigValue + 1;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static vx_channel_mode swigToEnum(int n) {
        if (n < swigValues.length && n >= 0 && vx_channel_mode.swigValues[n].swigValue == n) {
            return swigValues[n];
        }
        int n2 = 0;
        while (n2 < swigValues.length) {
            if (vx_channel_mode.swigValues[n2].swigValue == n) {
                return swigValues[n2];
            }
            ++n2;
        }
        throw new IllegalArgumentException("No enum " + vx_channel_mode.class + " with value " + n);
    }

    public final int swigValue() {
        return this.swigValue;
    }

    public String toString() {
        return this.swigName;
    }
}

