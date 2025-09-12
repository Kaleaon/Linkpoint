/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.VxClientProxyJNI;

public final class vx_media_completion_type {
    public static final vx_media_completion_type aux_buffer_audio_capture = new vx_media_completion_type("aux_buffer_audio_capture", VxClientProxyJNI.aux_buffer_audio_capture_get());
    public static final vx_media_completion_type aux_buffer_audio_render = new vx_media_completion_type("aux_buffer_audio_render", VxClientProxyJNI.aux_buffer_audio_render_get());
    public static final vx_media_completion_type media_completion_type_none = new vx_media_completion_type("media_completion_type_none", VxClientProxyJNI.media_completion_type_none_get());
    public static final vx_media_completion_type sessiongroup_audio_injection = new vx_media_completion_type("sessiongroup_audio_injection", VxClientProxyJNI.sessiongroup_audio_injection_get());
    private static int swigNext = 0;
    private static vx_media_completion_type[] swigValues = new vx_media_completion_type[]{media_completion_type_none, aux_buffer_audio_capture, aux_buffer_audio_render, sessiongroup_audio_injection};
    private final String swigName;
    private final int swigValue;

    private vx_media_completion_type(String string2) {
        this.swigName = string2;
        int n = swigNext;
        swigNext = n + 1;
        this.swigValue = n;
    }

    private vx_media_completion_type(String string2, int n) {
        this.swigName = string2;
        this.swigValue = n;
        swigNext = n + 1;
    }

    private vx_media_completion_type(String string2, vx_media_completion_type vx_media_completion_type2) {
        this.swigName = string2;
        this.swigValue = vx_media_completion_type2.swigValue;
        swigNext = this.swigValue + 1;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static vx_media_completion_type swigToEnum(int n) {
        if (n < swigValues.length && n >= 0 && vx_media_completion_type.swigValues[n].swigValue == n) {
            return swigValues[n];
        }
        int n2 = 0;
        while (n2 < swigValues.length) {
            if (vx_media_completion_type.swigValues[n2].swigValue == n) {
                return swigValues[n2];
            }
            ++n2;
        }
        throw new IllegalArgumentException("No enum " + vx_media_completion_type.class + " with value " + n);
    }

    public final int swigValue() {
        return this.swigValue;
    }

    public String toString() {
        return this.swigName;
    }
}

