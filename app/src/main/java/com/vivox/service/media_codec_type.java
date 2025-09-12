/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.VxClientProxyJNI;

public final class media_codec_type {
    public static final media_codec_type media_codec_type_nm = new media_codec_type("media_codec_type_nm", VxClientProxyJNI.media_codec_type_nm_get());
    public static final media_codec_type media_codec_type_none = new media_codec_type("media_codec_type_none", VxClientProxyJNI.media_codec_type_none_get());
    public static final media_codec_type media_codec_type_pcmu = new media_codec_type("media_codec_type_pcmu", VxClientProxyJNI.media_codec_type_pcmu_get());
    public static final media_codec_type media_codec_type_siren14 = new media_codec_type("media_codec_type_siren14", VxClientProxyJNI.media_codec_type_siren14_get());
    public static final media_codec_type media_codec_type_speex = new media_codec_type("media_codec_type_speex", VxClientProxyJNI.media_codec_type_speex_get());
    private static int swigNext = 0;
    private static media_codec_type[] swigValues = new media_codec_type[]{media_codec_type_none, media_codec_type_siren14, media_codec_type_pcmu, media_codec_type_nm, media_codec_type_speex};
    private final String swigName;
    private final int swigValue;

    private media_codec_type(String string2) {
        this.swigName = string2;
        int n = swigNext;
        swigNext = n + 1;
        this.swigValue = n;
    }

    private media_codec_type(String string2, int n) {
        this.swigName = string2;
        this.swigValue = n;
        swigNext = n + 1;
    }

    private media_codec_type(String string2, media_codec_type media_codec_type2) {
        this.swigName = string2;
        this.swigValue = media_codec_type2.swigValue;
        swigNext = this.swigValue + 1;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static media_codec_type swigToEnum(int n) {
        if (n < swigValues.length && n >= 0 && media_codec_type.swigValues[n].swigValue == n) {
            return swigValues[n];
        }
        int n2 = 0;
        while (n2 < swigValues.length) {
            if (media_codec_type.swigValues[n2].swigValue == n) {
                return swigValues[n2];
            }
            ++n2;
        }
        throw new IllegalArgumentException("No enum " + media_codec_type.class + " with value " + n);
    }

    public final int swigValue() {
        return this.swigValue;
    }

    public String toString() {
        return this.swigName;
    }
}

