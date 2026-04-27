/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.SWIGTYPE_p_vx_participant_diagnostic_state_t;
import com.vivox.service.VxClientProxyJNI;
import com.vivox.service.vx_evt_base_t;
import com.vivox.service.vx_participant_type;

public class vx_evt_participant_updated_t {
    protected boolean swigCMemOwn;
    private long swigCPtr;

    public vx_evt_participant_updated_t() {
        this(VxClientProxyJNI.new_vx_evt_participant_updated_t(), true);
    }

    protected vx_evt_participant_updated_t(long l, boolean bl) {
        this.swigCMemOwn = bl;
        this.swigCPtr = l;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected static long getCPtr(vx_evt_participant_updated_t vx_evt_participant_updated_t2) {
        if (vx_evt_participant_updated_t2 != null) return vx_evt_participant_updated_t2.swigCPtr;
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

    public int getActive_media() {
        return VxClientProxyJNI.vx_evt_participant_updated_t_active_media_get(this.swigCPtr, this);
    }

    public String getAlias_username() {
        return VxClientProxyJNI.vx_evt_participant_updated_t_alias_username_get(this.swigCPtr, this);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public vx_evt_base_t getBase() {
        long l = VxClientProxyJNI.vx_evt_participant_updated_t_base_get(this.swigCPtr, this);
        if (l != 0L) return new vx_evt_base_t(l, false);
        return null;
    }

    public int getDiagnostic_state_count() {
        return VxClientProxyJNI.vx_evt_participant_updated_t_diagnostic_state_count_get(this.swigCPtr, this);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public SWIGTYPE_p_vx_participant_diagnostic_state_t getDiagnostic_states() {
        long l = VxClientProxyJNI.vx_evt_participant_updated_t_diagnostic_states_get(this.swigCPtr, this);
        if (l != 0L) return new SWIGTYPE_p_vx_participant_diagnostic_state_t(l, false);
        return null;
    }

    public double getEnergy() {
        return VxClientProxyJNI.vx_evt_participant_updated_t_energy_get(this.swigCPtr, this);
    }

    public int getIs_moderator_muted() {
        return VxClientProxyJNI.vx_evt_participant_updated_t_is_moderator_muted_get(this.swigCPtr, this);
    }

    public int getIs_moderator_text_muted() {
        return VxClientProxyJNI.vx_evt_participant_updated_t_is_moderator_text_muted_get(this.swigCPtr, this);
    }

    public int getIs_muted_for_me() {
        return VxClientProxyJNI.vx_evt_participant_updated_t_is_muted_for_me_get(this.swigCPtr, this);
    }

    public int getIs_speaking() {
        return VxClientProxyJNI.vx_evt_participant_updated_t_is_speaking_get(this.swigCPtr, this);
    }

    public int getIs_text_muted_for_me() {
        return VxClientProxyJNI.vx_evt_participant_updated_t_is_text_muted_for_me_get(this.swigCPtr, this);
    }

    public String getParticipant_uri() {
        return VxClientProxyJNI.vx_evt_participant_updated_t_participant_uri_get(this.swigCPtr, this);
    }

    public String getSession_handle() {
        return VxClientProxyJNI.vx_evt_participant_updated_t_session_handle_get(this.swigCPtr, this);
    }

    public String getSessiongroup_handle() {
        return VxClientProxyJNI.vx_evt_participant_updated_t_sessiongroup_handle_get(this.swigCPtr, this);
    }

    public vx_participant_type getType() {
        return vx_participant_type.swigToEnum(VxClientProxyJNI.vx_evt_participant_updated_t_type_get(this.swigCPtr, this));
    }

    public int getVolume() {
        return VxClientProxyJNI.vx_evt_participant_updated_t_volume_get(this.swigCPtr, this);
    }

    public void setActive_media(int n) {
        VxClientProxyJNI.vx_evt_participant_updated_t_active_media_set(this.swigCPtr, this, n);
    }

    public void setAlias_username(String string2) {
        VxClientProxyJNI.vx_evt_participant_updated_t_alias_username_set(this.swigCPtr, this, string2);
    }

    public void setBase(vx_evt_base_t vx_evt_base_t2) {
        VxClientProxyJNI.vx_evt_participant_updated_t_base_set(this.swigCPtr, this, vx_evt_base_t.getCPtr(vx_evt_base_t2), vx_evt_base_t2);
    }

    public void setDiagnostic_state_count(int n) {
        VxClientProxyJNI.vx_evt_participant_updated_t_diagnostic_state_count_set(this.swigCPtr, this, n);
    }

    public void setDiagnostic_states(SWIGTYPE_p_vx_participant_diagnostic_state_t sWIGTYPE_p_vx_participant_diagnostic_state_t) {
        VxClientProxyJNI.vx_evt_participant_updated_t_diagnostic_states_set(this.swigCPtr, this, SWIGTYPE_p_vx_participant_diagnostic_state_t.getCPtr(sWIGTYPE_p_vx_participant_diagnostic_state_t));
    }

    public void setEnergy(double d) {
        VxClientProxyJNI.vx_evt_participant_updated_t_energy_set(this.swigCPtr, this, d);
    }

    public void setIs_moderator_muted(int n) {
        VxClientProxyJNI.vx_evt_participant_updated_t_is_moderator_muted_set(this.swigCPtr, this, n);
    }

    public void setIs_moderator_text_muted(int n) {
        VxClientProxyJNI.vx_evt_participant_updated_t_is_moderator_text_muted_set(this.swigCPtr, this, n);
    }

    public void setIs_muted_for_me(int n) {
        VxClientProxyJNI.vx_evt_participant_updated_t_is_muted_for_me_set(this.swigCPtr, this, n);
    }

    public void setIs_speaking(int n) {
        VxClientProxyJNI.vx_evt_participant_updated_t_is_speaking_set(this.swigCPtr, this, n);
    }

    public void setIs_text_muted_for_me(int n) {
        VxClientProxyJNI.vx_evt_participant_updated_t_is_text_muted_for_me_set(this.swigCPtr, this, n);
    }

    public void setParticipant_uri(String string2) {
        VxClientProxyJNI.vx_evt_participant_updated_t_participant_uri_set(this.swigCPtr, this, string2);
    }

    public void setSession_handle(String string2) {
        VxClientProxyJNI.vx_evt_participant_updated_t_session_handle_set(this.swigCPtr, this, string2);
    }

    public void setSessiongroup_handle(String string2) {
        VxClientProxyJNI.vx_evt_participant_updated_t_sessiongroup_handle_set(this.swigCPtr, this, string2);
    }

    public void setType(vx_participant_type vx_participant_type2) {
        VxClientProxyJNI.vx_evt_participant_updated_t_type_set(this.swigCPtr, this, vx_participant_type2.swigValue());
    }

    public void setVolume(int n) {
        VxClientProxyJNI.vx_evt_participant_updated_t_volume_set(this.swigCPtr, this, n);
    }
}

