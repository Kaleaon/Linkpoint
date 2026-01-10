/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.VxClientProxyJNI

class media_codec_type {
    media_codec_type media_codec_type_nm = media_codec_type("media_codec_type_nm", VxClientProxyJNI.media_codec_type_nm_get())
    media_codec_type media_codec_type_none = media_codec_type("media_codec_type_none", VxClientProxyJNI.media_codec_type_none_get())
    media_codec_type media_codec_type_pcmu = media_codec_type("media_codec_type_pcmu", VxClientProxyJNI.media_codec_type_pcmu_get())
    media_codec_type media_codec_type_siren14 = media_codec_type("media_codec_type_siren14", VxClientProxyJNI.media_codec_type_siren14_get())
    media_codec_type media_codec_type_speex = media_codec_type("media_codec_type_speex", VxClientProxyJNI.media_codec_type_speex_get())
    private Int swigNext = 0
    private media_codec_type[] swigValues = media_codec_type[]{media_codec_type_none, media_codec_type_siren14, media_codec_type_pcmu, media_codec_type_nm, media_codec_type_speex}
    private String swigName
    private Int swigValue

    private media_codec_type(String string2) {
        this.swigName = string2
        var n: Int = swigNext
        swigNext = n + 1
        this.swigValue = n
    }

    private media_codec_type(String string2, Int n) {
        this.swigName = string2
        this.swigValue = n
        swigNext = n + 1
    }

    private media_codec_type(String string2, media_codec_type media_codec_type2) {
        this.swigName = string2
        this.swigValue = media_codec_type2.swigValue
        swigNext = this.swigValue + 1
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    media_codec_type swigToEnum(Int n) {
        if (n < swigValues.length && n >= 0 && media_codec_type.swigValues[n].swigValue == n) {
            return swigValues[n]
        }
        var n2: Int = 0
        while (n2 < swigValues.length) {
            if (media_codec_type.swigValues[n2].swigValue == n) {
                return swigValues[n2]
            }
            ++n2
        }
        throw IllegalArgumentException("No enum " + media_codec_type.class + " with value " + n)
    }

    Int swigValue() {
        return this.swigValue
    }

    String toString() {
        return this.swigName
    }
}

