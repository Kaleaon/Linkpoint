/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.VxClientProxyJNI;

public final class vx_sessiongroup_type {
    public static final vx_sessiongroup_type sessiongroup_type_normal = new vx_sessiongroup_type("sessiongroup_type_normal", VxClientProxyJNI.sessiongroup_type_normal_get());
    public static final vx_sessiongroup_type sessiongroup_type_playback = new vx_sessiongroup_type("sessiongroup_type_playback", VxClientProxyJNI.sessiongroup_type_playback_get());
    private static int swigNext = 0;
    private static vx_sessiongroup_type[] swigValues = new vx_sessiongroup_type[]{sessiongroup_type_normal, sessiongroup_type_playback};
    private final String swigName;
    private final int swigValue;

    private vx_sessiongroup_type(String string2) {
        this.swigName = string2;
        int n = swigNext;
        swigNext = n + 1;
        this.swigValue = n;
    }

    private vx_sessiongroup_type(String string2, int n) {
        this.swigName = string2;
        this.swigValue = n;
        swigNext = n + 1;
    }

    private vx_sessiongroup_type(String string2, vx_sessiongroup_type vx_sessiongroup_type2) {
        this.swigName = string2;
        this.swigValue = vx_sessiongroup_type2.swigValue;
        swigNext = this.swigValue + 1;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static vx_sessiongroup_type swigToEnum(int n) {
        if (n < swigValues.length && n >= 0 && vx_sessiongroup_type.swigValues[n].swigValue == n) {
            return swigValues[n];
        }
        int n2 = 0;
        while (n2 < swigValues.length) {
            if (vx_sessiongroup_type.swigValues[n2].swigValue == n) {
                return swigValues[n2];
            }
            ++n2;
        }
        throw new IllegalArgumentException("No enum " + vx_sessiongroup_type.class + " with value " + n);
    }

    public final int swigValue() {
        return this.swigValue;
    }

    public String toString() {
        return this.swigName;
    }
}

