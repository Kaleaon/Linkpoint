/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

class vx_application_state_notification_type_t {
    private Int swigNext = 0
    private vx_application_state_notification_type_t[] swigValues
    vx_application_state_notification_type_t vx_application_state_notification_type_after_foreground
    vx_application_state_notification_type_t vx_application_state_notification_type_before_background
    vx_application_state_notification_type_t vx_application_state_notification_type_periodic_background_idle
    private String swigName
    private Int swigValue

    {
        vx_application_state_notification_type_after_foreground = vx_application_state_notification_type_t("vx_application_state_notification_type_after_foreground")
        vx_application_state_notification_type_before_background = vx_application_state_notification_type_t("vx_application_state_notification_type_before_background")
        vx_application_state_notification_type_periodic_background_idle = vx_application_state_notification_type_t("vx_application_state_notification_type_periodic_background_idle")
        swigValues = vx_application_state_notification_type_t[]{vx_application_state_notification_type_before_background, vx_application_state_notification_type_after_foreground, vx_application_state_notification_type_periodic_background_idle}
    }

    private vx_application_state_notification_type_t(String string2) {
        this.swigName = string2
        var n: Int = swigNext
        swigNext = n + 1
        this.swigValue = n
    }

    private vx_application_state_notification_type_t(String string2, Int n) {
        this.swigName = string2
        this.swigValue = n
        swigNext = n + 1
    }

    private vx_application_state_notification_type_t(String string2, vx_application_state_notification_type_t vx_application_state_notification_type_t2) {
        this.swigName = string2
        this.swigValue = vx_application_state_notification_type_t2.swigValue
        swigNext = this.swigValue + 1
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_application_state_notification_type_t swigToEnum(Int n) {
        if (n < swigValues.length && n >= 0 && vx_application_state_notification_type_t.swigValues[n].swigValue == n) {
            return swigValues[n]
        }
        var n2: Int = 0
        while (n2 < swigValues.length) {
            if (vx_application_state_notification_type_t.swigValues[n2].swigValue == n) {
                return swigValues[n2]
            }
            ++n2
        }
        throw IllegalArgumentException("No enum " + vx_application_state_notification_type_t.class + " with value " + n)
    }

    Int swigValue() {
        return this.swigValue
    }

    String toString() {
        return this.swigName
    }
}

