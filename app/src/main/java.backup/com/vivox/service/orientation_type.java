/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.VxClientProxyJNI;

public final class orientation_type {
    public static final orientation_type orientation_default = new orientation_type("orientation_default", VxClientProxyJNI.orientation_default_get());
    public static final orientation_type orientation_legacy = new orientation_type("orientation_legacy", VxClientProxyJNI.orientation_legacy_get());
    public static final orientation_type orientation_vivox = new orientation_type("orientation_vivox", VxClientProxyJNI.orientation_vivox_get());
    private static int swigNext = 0;
    private static orientation_type[] swigValues = new orientation_type[]{orientation_default, orientation_legacy, orientation_vivox};
    private final String swigName;
    private final int swigValue;

    private orientation_type(String string2) {
        this.swigName = string2;
        int n = swigNext;
        swigNext = n + 1;
        this.swigValue = n;
    }

    private orientation_type(String string2, int n) {
        this.swigName = string2;
        this.swigValue = n;
        swigNext = n + 1;
    }

    private orientation_type(String string2, orientation_type orientation_type2) {
        this.swigName = string2;
        this.swigValue = orientation_type2.swigValue;
        swigNext = this.swigValue + 1;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static orientation_type swigToEnum(int n) {
        if (n < swigValues.length && n >= 0 && orientation_type.swigValues[n].swigValue == n) {
            return swigValues[n];
        }
        int n2 = 0;
        while (n2 < swigValues.length) {
            if (orientation_type.swigValues[n2].swigValue == n) {
                return swigValues[n2];
            }
            ++n2;
        }
        throw new IllegalArgumentException("No enum " + orientation_type.class + " with value " + n);
    }

    public final int swigValue() {
        return this.swigValue;
    }

    public String toString() {
        return this.swigName;
    }
}

