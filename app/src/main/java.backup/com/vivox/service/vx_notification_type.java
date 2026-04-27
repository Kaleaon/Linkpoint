/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.VxClientProxyJNI;

public final class vx_notification_type {
    public static final vx_notification_type notification_hand_lowered = new vx_notification_type("notification_hand_lowered", VxClientProxyJNI.notification_hand_lowered_get());
    public static final vx_notification_type notification_hand_raised = new vx_notification_type("notification_hand_raised", VxClientProxyJNI.notification_hand_raised_get());
    public static final vx_notification_type notification_max = new vx_notification_type("notification_max", VxClientProxyJNI.notification_max_get());
    public static final vx_notification_type notification_min = new vx_notification_type("notification_min", VxClientProxyJNI.notification_min_get());
    public static final vx_notification_type notification_not_typing = new vx_notification_type("notification_not_typing", VxClientProxyJNI.notification_not_typing_get());
    public static final vx_notification_type notification_typing = new vx_notification_type("notification_typing", VxClientProxyJNI.notification_typing_get());
    private static int swigNext = 0;
    private static vx_notification_type[] swigValues = new vx_notification_type[]{notification_not_typing, notification_typing, notification_hand_lowered, notification_hand_raised, notification_min, notification_max};
    private final String swigName;
    private final int swigValue;

    private vx_notification_type(String string2) {
        this.swigName = string2;
        int n = swigNext;
        swigNext = n + 1;
        this.swigValue = n;
    }

    private vx_notification_type(String string2, int n) {
        this.swigName = string2;
        this.swigValue = n;
        swigNext = n + 1;
    }

    private vx_notification_type(String string2, vx_notification_type vx_notification_type2) {
        this.swigName = string2;
        this.swigValue = vx_notification_type2.swigValue;
        swigNext = this.swigValue + 1;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static vx_notification_type swigToEnum(int n) {
        if (n < swigValues.length && n >= 0 && vx_notification_type.swigValues[n].swigValue == n) {
            return swigValues[n];
        }
        int n2 = 0;
        while (n2 < swigValues.length) {
            if (vx_notification_type.swigValues[n2].swigValue == n) {
                return swigValues[n2];
            }
            ++n2;
        }
        throw new IllegalArgumentException("No enum " + vx_notification_type.class + " with value " + n);
    }

    public final int swigValue() {
        return this.swigValue;
    }

    public String toString() {
        return this.swigName;
    }
}

