/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.VxClientProxyJNI;

public final class vx_participant_type {
    public static final vx_participant_type part_focus = new vx_participant_type("part_focus", VxClientProxyJNI.part_focus_get());
    public static final vx_participant_type part_moderator = new vx_participant_type("part_moderator", VxClientProxyJNI.part_moderator_get());
    public static final vx_participant_type part_user = new vx_participant_type("part_user", VxClientProxyJNI.part_user_get());
    public static final vx_participant_type participant_moderator = new vx_participant_type("participant_moderator", VxClientProxyJNI.participant_moderator_get());
    public static final vx_participant_type participant_owner = new vx_participant_type("participant_owner", VxClientProxyJNI.participant_owner_get());
    public static final vx_participant_type participant_user = new vx_participant_type("participant_user", VxClientProxyJNI.participant_user_get());
    private static int swigNext = 0;
    private static vx_participant_type[] swigValues = new vx_participant_type[]{participant_user, part_user, participant_moderator, part_moderator, participant_owner, part_focus};
    private final String swigName;
    private final int swigValue;

    private vx_participant_type(String string2) {
        this.swigName = string2;
        int n = swigNext;
        swigNext = n + 1;
        this.swigValue = n;
    }

    private vx_participant_type(String string2, int n) {
        this.swigName = string2;
        this.swigValue = n;
        swigNext = n + 1;
    }

    private vx_participant_type(String string2, vx_participant_type vx_participant_type2) {
        this.swigName = string2;
        this.swigValue = vx_participant_type2.swigValue;
        swigNext = this.swigValue + 1;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static vx_participant_type swigToEnum(int n) {
        if (n < swigValues.length && n >= 0 && vx_participant_type.swigValues[n].swigValue == n) {
            return swigValues[n];
        }
        int n2 = 0;
        while (n2 < swigValues.length) {
            if (vx_participant_type.swigValues[n2].swigValue == n) {
                return swigValues[n2];
            }
            ++n2;
        }
        throw new IllegalArgumentException("No enum " + vx_participant_type.class + " with value " + n);
    }

    public final int swigValue() {
        return this.swigValue;
    }

    public String toString() {
        return this.swigName;
    }
}

