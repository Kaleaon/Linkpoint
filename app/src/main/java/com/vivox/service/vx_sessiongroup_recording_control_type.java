/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.VxClientProxyJNI;

public final class vx_sessiongroup_recording_control_type {
    public static final vx_sessiongroup_recording_control_type VX_SESSIONGROUP_RECORDING_CONTROL_FLUSH_TO_FILE = new vx_sessiongroup_recording_control_type("VX_SESSIONGROUP_RECORDING_CONTROL_FLUSH_TO_FILE", VxClientProxyJNI.VX_SESSIONGROUP_RECORDING_CONTROL_FLUSH_TO_FILE_get());
    public static final vx_sessiongroup_recording_control_type VX_SESSIONGROUP_RECORDING_CONTROL_START = new vx_sessiongroup_recording_control_type("VX_SESSIONGROUP_RECORDING_CONTROL_START", VxClientProxyJNI.VX_SESSIONGROUP_RECORDING_CONTROL_START_get());
    public static final vx_sessiongroup_recording_control_type VX_SESSIONGROUP_RECORDING_CONTROL_STOP = new vx_sessiongroup_recording_control_type("VX_SESSIONGROUP_RECORDING_CONTROL_STOP", VxClientProxyJNI.VX_SESSIONGROUP_RECORDING_CONTROL_STOP_get());
    private static int swigNext = 0;
    private static vx_sessiongroup_recording_control_type[] swigValues = new vx_sessiongroup_recording_control_type[]{VX_SESSIONGROUP_RECORDING_CONTROL_STOP, VX_SESSIONGROUP_RECORDING_CONTROL_START, VX_SESSIONGROUP_RECORDING_CONTROL_FLUSH_TO_FILE};
    private final String swigName;
    private final int swigValue;

    private vx_sessiongroup_recording_control_type(String string2) {
        this.swigName = string2;
        int n = swigNext;
        swigNext = n + 1;
        this.swigValue = n;
    }

    private vx_sessiongroup_recording_control_type(String string2, int n) {
        this.swigName = string2;
        this.swigValue = n;
        swigNext = n + 1;
    }

    private vx_sessiongroup_recording_control_type(String string2, vx_sessiongroup_recording_control_type vx_sessiongroup_recording_control_type2) {
        this.swigName = string2;
        this.swigValue = vx_sessiongroup_recording_control_type2.swigValue;
        swigNext = this.swigValue + 1;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static vx_sessiongroup_recording_control_type swigToEnum(int n) {
        if (n < swigValues.length && n >= 0 && vx_sessiongroup_recording_control_type.swigValues[n].swigValue == n) {
            return swigValues[n];
        }
        int n2 = 0;
        while (n2 < swigValues.length) {
            if (vx_sessiongroup_recording_control_type.swigValues[n2].swigValue == n) {
                return swigValues[n2];
            }
            ++n2;
        }
        throw new IllegalArgumentException("No enum " + vx_sessiongroup_recording_control_type.class + " with value " + n);
    }

    public final int swigValue() {
        return this.swigValue;
    }

    public String toString() {
        return this.swigName;
    }
}

