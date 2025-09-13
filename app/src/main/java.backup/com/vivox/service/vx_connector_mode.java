/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.VxClientProxyJNI;

public final class vx_connector_mode {
    public static final vx_connector_mode connector_mode_legacy = new vx_connector_mode("connector_mode_legacy");
    public static final vx_connector_mode connector_mode_normal = new vx_connector_mode("connector_mode_normal", VxClientProxyJNI.connector_mode_normal_get());
    private static int swigNext = 0;
    private static vx_connector_mode[] swigValues = new vx_connector_mode[]{connector_mode_normal, connector_mode_legacy};
    private final String swigName;
    private final int swigValue;

    private vx_connector_mode(String string2) {
        this.swigName = string2;
        int n = swigNext;
        swigNext = n + 1;
        this.swigValue = n;
    }

    private vx_connector_mode(String string2, int n) {
        this.swigName = string2;
        this.swigValue = n;
        swigNext = n + 1;
    }

    private vx_connector_mode(String string2, vx_connector_mode vx_connector_mode2) {
        this.swigName = string2;
        this.swigValue = vx_connector_mode2.swigValue;
        swigNext = this.swigValue + 1;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static vx_connector_mode swigToEnum(int n) {
        if (n < swigValues.length && n >= 0 && vx_connector_mode.swigValues[n].swigValue == n) {
            return swigValues[n];
        }
        int n2 = 0;
        while (n2 < swigValues.length) {
            if (vx_connector_mode.swigValues[n2].swigValue == n) {
                return swigValues[n2];
            }
            ++n2;
        }
        throw new IllegalArgumentException("No enum " + vx_connector_mode.class + " with value " + n);
    }

    public final int swigValue() {
        return this.swigValue;
    }

    public String toString() {
        return this.swigName;
    }
}

