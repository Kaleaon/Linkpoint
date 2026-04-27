/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.SWIGTYPE_p_double;
import com.vivox.service.VxClientProxyJNI;
import com.vivox.service.vx_evt_base_t;

public class vx_evt_session_updated_t {
    protected boolean swigCMemOwn;
    private long swigCPtr;

    public vx_evt_session_updated_t() {
        this(VxClientProxyJNI.new_vx_evt_session_updated_t(), true);
    }

    protected vx_evt_session_updated_t(long l, boolean bl) {
        this.swigCMemOwn = bl;
        this.swigCPtr = l;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected static long getCPtr(vx_evt_session_updated_t vx_evt_session_updated_t2) {
        if (vx_evt_session_updated_t2 != null) return vx_evt_session_updated_t2.swigCPtr;
        return 0L;
    }

    public void delete() {
        synchronized (this) {
            if (this.swigCPtr == 0L || !this.swigCMemOwn) {
                this.swigCPtr = 0L;
                return;
            }
            this.swigCMemOwn = false;
            UnsupportedOperationException unsupportedOperationException = new UnsupportedOperationException("C++ destructor does not have public access");
            throw unsupportedOperationException;
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public vx_evt_base_t getBase() {
        long l = VxClientProxyJNI.vx_evt_session_updated_t_base_get(this.swigCPtr, this);
        if (l != 0L) return new vx_evt_base_t(l, false);
        return null;
    }

    public int getIs_ad_playing() {
        return VxClientProxyJNI.vx_evt_session_updated_t_is_ad_playing_get(this.swigCPtr, this);
    }

    public int getIs_focused() {
        return VxClientProxyJNI.vx_evt_session_updated_t_is_focused_get(this.swigCPtr, this);
    }

    public int getIs_muted() {
        return VxClientProxyJNI.vx_evt_session_updated_t_is_muted_get(this.swigCPtr, this);
    }

    public int getIs_text_muted() {
        return VxClientProxyJNI.vx_evt_session_updated_t_is_text_muted_get(this.swigCPtr, this);
    }

    public int getSession_font_id() {
        return VxClientProxyJNI.vx_evt_session_updated_t_session_font_id_get(this.swigCPtr, this);
    }

    public String getSession_handle() {
        return VxClientProxyJNI.vx_evt_session_updated_t_session_handle_get(this.swigCPtr, this);
    }

    public String getSessiongroup_handle() {
        return VxClientProxyJNI.vx_evt_session_updated_t_sessiongroup_handle_get(this.swigCPtr, this);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public SWIGTYPE_p_double getSpeaker_position() {
        long l = VxClientProxyJNI.vx_evt_session_updated_t_speaker_position_get(this.swigCPtr, this);
        if (l != 0L) return new SWIGTYPE_p_double(l, false);
        return null;
    }

    public int getTransmit_enabled() {
        return VxClientProxyJNI.vx_evt_session_updated_t_transmit_enabled_get(this.swigCPtr, this);
    }

    public String getUri() {
        return VxClientProxyJNI.vx_evt_session_updated_t_uri_get(this.swigCPtr, this);
    }

    public int getVolume() {
        return VxClientProxyJNI.vx_evt_session_updated_t_volume_get(this.swigCPtr, this);
    }

    public void setBase(vx_evt_base_t vx_evt_base_t2) {
        VxClientProxyJNI.vx_evt_session_updated_t_base_set(this.swigCPtr, this, vx_evt_base_t.getCPtr(vx_evt_base_t2), vx_evt_base_t2);
    }

    public void setIs_ad_playing(int n) {
        VxClientProxyJNI.vx_evt_session_updated_t_is_ad_playing_set(this.swigCPtr, this, n);
    }

    public void setIs_focused(int n) {
        VxClientProxyJNI.vx_evt_session_updated_t_is_focused_set(this.swigCPtr, this, n);
    }

    public void setIs_muted(int n) {
        VxClientProxyJNI.vx_evt_session_updated_t_is_muted_set(this.swigCPtr, this, n);
    }

    public void setIs_text_muted(int n) {
        VxClientProxyJNI.vx_evt_session_updated_t_is_text_muted_set(this.swigCPtr, this, n);
    }

    public void setSession_font_id(int n) {
        VxClientProxyJNI.vx_evt_session_updated_t_session_font_id_set(this.swigCPtr, this, n);
    }

    public void setSession_handle(String string2) {
        VxClientProxyJNI.vx_evt_session_updated_t_session_handle_set(this.swigCPtr, this, string2);
    }

    public void setSessiongroup_handle(String string2) {
        VxClientProxyJNI.vx_evt_session_updated_t_sessiongroup_handle_set(this.swigCPtr, this, string2);
    }

    public void setSpeaker_position(SWIGTYPE_p_double sWIGTYPE_p_double) {
        VxClientProxyJNI.vx_evt_session_updated_t_speaker_position_set(this.swigCPtr, this, SWIGTYPE_p_double.getCPtr(sWIGTYPE_p_double));
    }

    public void setTransmit_enabled(int n) {
        VxClientProxyJNI.vx_evt_session_updated_t_transmit_enabled_set(this.swigCPtr, this, n);
    }

    public void setUri(String string2) {
        VxClientProxyJNI.vx_evt_session_updated_t_uri_set(this.swigCPtr, this, string2);
    }

    public void setVolume(int n) {
        VxClientProxyJNI.vx_evt_session_updated_t_volume_set(this.swigCPtr, this, n);
    }
}

