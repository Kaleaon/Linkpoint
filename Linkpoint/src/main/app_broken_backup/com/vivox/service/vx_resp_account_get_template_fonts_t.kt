/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.SWIGTYPE_p_p_vx_voice_font
import com.vivox.service.VxClientProxyJNI
import com.vivox.service.vx_resp_base_t

class vx_resp_account_get_template_fonts_t {
    protected Boolean swigCMemOwn
    private Long swigCPtr

    vx_resp_account_get_template_fonts_t() {
        this(VxClientProxyJNI.new_vx_resp_account_get_template_fonts_t(), true)
    }

    protected vx_resp_account_get_template_fonts_t(Long l, Boolean bl) {
        this.swigCMemOwn = bl
        this.swigCPtr = l
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected Long getCPtr(vx_resp_account_get_template_fonts_t vx_resp_account_get_template_fonts_t2) {
        if (vx_resp_account_get_template_fonts_t2 != null) return vx_resp_account_get_template_fonts_t2.swigCPtr
        return 0L
    }

    Unit delete() {
        synchronized (this) {
            if (this.swigCPtr == 0L || !this.swigCMemOwn) {
                this.swigCPtr = 0L
                return
            }
            this.swigCMemOwn = false
            UnsupportedOperationException unsupportedOperationException = UnsupportedOperationException("C++ destructor does not have access")
            throw unsupportedOperationException
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_base_t getBase() {
        var l: Long = VxClientProxyJNI.vx_resp_account_get_template_fonts_t_base_get(this.swigCPtr, this)
        if (l != 0L) return vx_resp_base_t(l, false)
        return null
    }

    Int getTemplate_font_count() {
        return VxClientProxyJNI.vx_resp_account_get_template_fonts_t_template_font_count_get(this.swigCPtr, this)
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    SWIGTYPE_p_p_vx_voice_font getTemplate_fonts() {
        var l: Long = VxClientProxyJNI.vx_resp_account_get_template_fonts_t_template_fonts_get(this.swigCPtr, this)
        if (l != 0L) return SWIGTYPE_p_p_vx_voice_font(l, false)
        return null
    }

    Unit setBase(vx_resp_base_t vx_resp_base_t2) {
        VxClientProxyJNI.vx_resp_account_get_template_fonts_t_base_set(this.swigCPtr, this, vx_resp_base_t.getCPtr(vx_resp_base_t2), vx_resp_base_t2)
    }

    Unit setTemplate_font_count(Int n) {
        VxClientProxyJNI.vx_resp_account_get_template_fonts_t_template_font_count_set(this.swigCPtr, this, n)
    }

    Unit setTemplate_fonts(SWIGTYPE_p_p_vx_voice_font sWIGTYPE_p_p_vx_voice_font) {
        VxClientProxyJNI.vx_resp_account_get_template_fonts_t_template_fonts_set(this.swigCPtr, this, SWIGTYPE_p_p_vx_voice_font.getCPtr(sWIGTYPE_p_p_vx_voice_font))
    }
}

