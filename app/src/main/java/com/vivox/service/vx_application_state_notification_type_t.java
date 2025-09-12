/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

public final class vx_application_state_notification_type_t {
    private static int swigNext = 0;
    private static vx_application_state_notification_type_t[] swigValues;
    public static final vx_application_state_notification_type_t vx_application_state_notification_type_after_foreground;
    public static final vx_application_state_notification_type_t vx_application_state_notification_type_before_background;
    public static final vx_application_state_notification_type_t vx_application_state_notification_type_periodic_background_idle;
    private final String swigName;
    private final int swigValue;

    static {
        vx_application_state_notification_type_after_foreground = new vx_application_state_notification_type_t("vx_application_state_notification_type_after_foreground");
        vx_application_state_notification_type_before_background = new vx_application_state_notification_type_t("vx_application_state_notification_type_before_background");
        vx_application_state_notification_type_periodic_background_idle = new vx_application_state_notification_type_t("vx_application_state_notification_type_periodic_background_idle");
        swigValues = new vx_application_state_notification_type_t[]{vx_application_state_notification_type_before_background, vx_application_state_notification_type_after_foreground, vx_application_state_notification_type_periodic_background_idle};
    }

    private vx_application_state_notification_type_t(String string2) {
        this.swigName = string2;
        int n = swigNext;
        swigNext = n + 1;
        this.swigValue = n;
    }

    private vx_application_state_notification_type_t(String string2, int n) {
        this.swigName = string2;
        this.swigValue = n;
        swigNext = n + 1;
    }

    private vx_application_state_notification_type_t(String string2, vx_application_state_notification_type_t vx_application_state_notification_type_t2) {
        this.swigName = string2;
        this.swigValue = vx_application_state_notification_type_t2.swigValue;
        swigNext = this.swigValue + 1;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static vx_application_state_notification_type_t swigToEnum(int n) {
        if (n < swigValues.length && n >= 0 && vx_application_state_notification_type_t.swigValues[n].swigValue == n) {
            return swigValues[n];
        }
        int n2 = 0;
        while (n2 < swigValues.length) {
            if (vx_application_state_notification_type_t.swigValues[n2].swigValue == n) {
                return swigValues[n2];
            }
            ++n2;
        }
        throw new IllegalArgumentException("No enum " + vx_application_state_notification_type_t.class + " with value " + n);
    }

    public final int swigValue() {
        return this.swigValue;
    }

    public String toString() {
        return this.swigName;
    }
}

