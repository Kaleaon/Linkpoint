/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.VxClientProxyJNI;

public final class vx_buddy_presence_state {
    public static final vx_buddy_presence_state buddy_presence_away = new vx_buddy_presence_state("buddy_presence_away", VxClientProxyJNI.buddy_presence_away_get());
    public static final vx_buddy_presence_state buddy_presence_brb = new vx_buddy_presence_state("buddy_presence_brb", VxClientProxyJNI.buddy_presence_brb_get());
    public static final vx_buddy_presence_state buddy_presence_busy = new vx_buddy_presence_state("buddy_presence_busy", VxClientProxyJNI.buddy_presence_busy_get());
    public static final vx_buddy_presence_state buddy_presence_closed = new vx_buddy_presence_state("buddy_presence_closed", VxClientProxyJNI.buddy_presence_closed_get());
    public static final vx_buddy_presence_state buddy_presence_custom = new vx_buddy_presence_state("buddy_presence_custom", VxClientProxyJNI.buddy_presence_custom_get());
    public static final vx_buddy_presence_state buddy_presence_offline = new vx_buddy_presence_state("buddy_presence_offline", VxClientProxyJNI.buddy_presence_offline_get());
    public static final vx_buddy_presence_state buddy_presence_online = new vx_buddy_presence_state("buddy_presence_online", VxClientProxyJNI.buddy_presence_online_get());
    public static final vx_buddy_presence_state buddy_presence_online_slc = new vx_buddy_presence_state("buddy_presence_online_slc", VxClientProxyJNI.buddy_presence_online_slc_get());
    public static final vx_buddy_presence_state buddy_presence_onthephone = new vx_buddy_presence_state("buddy_presence_onthephone", VxClientProxyJNI.buddy_presence_onthephone_get());
    public static final vx_buddy_presence_state buddy_presence_outtolunch = new vx_buddy_presence_state("buddy_presence_outtolunch", VxClientProxyJNI.buddy_presence_outtolunch_get());
    public static final vx_buddy_presence_state buddy_presence_pending = new vx_buddy_presence_state("buddy_presence_pending", VxClientProxyJNI.buddy_presence_pending_get());
    public static final vx_buddy_presence_state buddy_presence_unknown = new vx_buddy_presence_state("buddy_presence_unknown", VxClientProxyJNI.buddy_presence_unknown_get());
    private static int swigNext = 0;
    private static vx_buddy_presence_state[] swigValues = new vx_buddy_presence_state[]{buddy_presence_unknown, buddy_presence_pending, buddy_presence_online, buddy_presence_busy, buddy_presence_brb, buddy_presence_away, buddy_presence_onthephone, buddy_presence_outtolunch, buddy_presence_custom, buddy_presence_online_slc, buddy_presence_closed, buddy_presence_offline};
    private final String swigName;
    private final int swigValue;

    private vx_buddy_presence_state(String string2) {
        this.swigName = string2;
        int n = swigNext;
        swigNext = n + 1;
        this.swigValue = n;
    }

    private vx_buddy_presence_state(String string2, int n) {
        this.swigName = string2;
        this.swigValue = n;
        swigNext = n + 1;
    }

    private vx_buddy_presence_state(String string2, vx_buddy_presence_state vx_buddy_presence_state2) {
        this.swigName = string2;
        this.swigValue = vx_buddy_presence_state2.swigValue;
        swigNext = this.swigValue + 1;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static vx_buddy_presence_state swigToEnum(int n) {
        if (n < swigValues.length && n >= 0 && vx_buddy_presence_state.swigValues[n].swigValue == n) {
            return swigValues[n];
        }
        int n2 = 0;
        while (n2 < swigValues.length) {
            if (vx_buddy_presence_state.swigValues[n2].swigValue == n) {
                return swigValues[n2];
            }
            ++n2;
        }
        throw new IllegalArgumentException("No enum " + vx_buddy_presence_state.class + " with value " + n);
    }

    public final int swigValue() {
        return this.swigValue;
    }

    public String toString() {
        return this.swigName;
    }
}

