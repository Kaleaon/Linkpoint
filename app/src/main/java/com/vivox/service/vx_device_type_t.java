/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.VxClientProxyJNI;

public final class vx_device_type_t {
    private static int swigNext = 0;
    private static vx_device_type_t[] swigValues;
    public static final vx_device_type_t vx_device_type_default_system;
    public static final vx_device_type_t vx_device_type_null;
    public static final vx_device_type_t vx_device_type_specific_device;
    private final String swigName;
    private final int swigValue;

    static {
        vx_device_type_default_system = new vx_device_type_t("vx_device_type_default_system", VxClientProxyJNI.vx_device_type_default_system_get());
        vx_device_type_null = new vx_device_type_t("vx_device_type_null", VxClientProxyJNI.vx_device_type_null_get());
        vx_device_type_specific_device = new vx_device_type_t("vx_device_type_specific_device", VxClientProxyJNI.vx_device_type_specific_device_get());
        swigValues = new vx_device_type_t[]{vx_device_type_specific_device, vx_device_type_default_system, vx_device_type_null};
    }

    private vx_device_type_t(String string2) {
        this.swigName = string2;
        int n = swigNext;
        swigNext = n + 1;
        this.swigValue = n;
    }

    private vx_device_type_t(String string2, int n) {
        this.swigName = string2;
        this.swigValue = n;
        swigNext = n + 1;
    }

    private vx_device_type_t(String string2, vx_device_type_t vx_device_type_t2) {
        this.swigName = string2;
        this.swigValue = vx_device_type_t2.swigValue;
        swigNext = this.swigValue + 1;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static vx_device_type_t swigToEnum(int n) {
        if (n < swigValues.length && n >= 0 && vx_device_type_t.swigValues[n].swigValue == n) {
            return swigValues[n];
        }
        int n2 = 0;
        while (n2 < swigValues.length) {
            if (vx_device_type_t.swigValues[n2].swigValue == n) {
                return swigValues[n2];
            }
            ++n2;
        }
        throw new IllegalArgumentException("No enum " + vx_device_type_t.class + " with value " + n);
    }

    public final int swigValue() {
        return this.swigValue;
    }

    public String toString() {
        return this.swigName;
    }
}

