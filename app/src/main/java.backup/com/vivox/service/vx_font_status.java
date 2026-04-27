/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.VxClientProxyJNI;

public final class vx_font_status {
    public static final vx_font_status status_free = new vx_font_status("status_free", VxClientProxyJNI.status_free_get());
    public static final vx_font_status status_none = new vx_font_status("status_none", VxClientProxyJNI.status_none_get());
    public static final vx_font_status status_not_free = new vx_font_status("status_not_free", VxClientProxyJNI.status_not_free_get());
    private static int swigNext = 0;
    private static vx_font_status[] swigValues = new vx_font_status[]{status_none, status_free, status_not_free};
    private final String swigName;
    private final int swigValue;

    private vx_font_status(String string2) {
        this.swigName = string2;
        int n = swigNext;
        swigNext = n + 1;
        this.swigValue = n;
    }

    private vx_font_status(String string2, int n) {
        this.swigName = string2;
        this.swigValue = n;
        swigNext = n + 1;
    }

    private vx_font_status(String string2, vx_font_status vx_font_status2) {
        this.swigName = string2;
        this.swigValue = vx_font_status2.swigValue;
        swigNext = this.swigValue + 1;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static vx_font_status swigToEnum(int n) {
        if (n < swigValues.length && n >= 0 && vx_font_status.swigValues[n].swigValue == n) {
            return swigValues[n];
        }
        int n2 = 0;
        while (n2 < swigValues.length) {
            if (vx_font_status.swigValues[n2].swigValue == n) {
                return swigValues[n2];
            }
            ++n2;
        }
        throw new IllegalArgumentException("No enum " + vx_font_status.class + " with value " + n);
    }

    public final int swigValue() {
        return this.swigValue;
    }

    public String toString() {
        return this.swigName;
    }
}

