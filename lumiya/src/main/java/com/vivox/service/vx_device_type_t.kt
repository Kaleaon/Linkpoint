/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.VxClientProxyJNI

class vx_device_type_t {
    private Int swigNext = 0
    private vx_device_type_t[] swigValues
    vx_device_type_t vx_device_type_default_system
    vx_device_type_t vx_device_type_null
    vx_device_type_t vx_device_type_specific_device
    private String swigName
    private Int swigValue

    {
        vx_device_type_default_system = vx_device_type_t("vx_device_type_default_system", VxClientProxyJNI.vx_device_type_default_system_get())
        vx_device_type_null = vx_device_type_t("vx_device_type_null", VxClientProxyJNI.vx_device_type_null_get())
        vx_device_type_specific_device = vx_device_type_t("vx_device_type_specific_device", VxClientProxyJNI.vx_device_type_specific_device_get())
        swigValues = vx_device_type_t[]{vx_device_type_specific_device, vx_device_type_default_system, vx_device_type_null}
    }

    private vx_device_type_t(String string2) {
        this.swigName = string2
        Int n = swigNext
        swigNext = n + 1
        this.swigValue = n
    }

    private vx_device_type_t(String string2, Int n) {
        this.swigName = string2
        this.swigValue = n
        swigNext = n + 1
    }

    private vx_device_type_t(String string2, vx_device_type_t vx_device_type_t2) {
        this.swigName = string2
        this.swigValue = vx_device_type_t2.swigValue
        swigNext = this.swigValue + 1
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_device_type_t swigToEnum(Int n) {
        if (n < swigValues.length && n >= 0 && vx_device_type_t.swigValues[n].swigValue == n) {
            return swigValues[n]
        }
        Int n2 = 0
        while (n2 < swigValues.length) {
            if (vx_device_type_t.swigValues[n2].swigValue == n) {
                return swigValues[n2]
            }
            ++n2
        }
        throw IllegalArgumentException("No enum " + vx_device_type_t.class + " with value " + n)
    }

    Int swigValue() {
        return this.swigValue
    }

    String toString() {
        return this.swigName
    }
}

