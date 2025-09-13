/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.VxClientProxyJNI;

public class vx_participant_t {
    protected boolean swigCMemOwn;
    private long swigCPtr;

    public vx_participant_t() {
        this(VxClientProxyJNI.new_vx_participant_t(), true);
    }

    protected vx_participant_t(long l, boolean bl) {
        this.swigCMemOwn = bl;
        this.swigCPtr = l;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected static long getCPtr(vx_participant_t vx_participant_t2) {
        if (vx_participant_t2 != null) return vx_participant_t2.swigCPtr;
        return 0L;
    }

    public void delete() {
        synchronized (this) {
            if (this.swigCPtr != 0L && this.swigCMemOwn) {
                this.swigCMemOwn = false;
                VxClientProxyJNI.delete_vx_participant_t(this.swigCPtr);
            }
            this.swigCPtr = 0L;
            return;
        }
    }

    protected void finalize() {
        this.delete();
    }

    public int getAccount_id() {
        return VxClientProxyJNI.vx_participant_t_account_id_get(this.swigCPtr, this);
    }

    public String getDisplay_name() {
        return VxClientProxyJNI.vx_participant_t_display_name_get(this.swigCPtr, this);
    }

    public String getFirst_name() {
        return VxClientProxyJNI.vx_participant_t_first_name_get(this.swigCPtr, this);
    }

    public int getIs_moderator() {
        return VxClientProxyJNI.vx_participant_t_is_moderator_get(this.swigCPtr, this);
    }

    public int getIs_moderator_muted() {
        return VxClientProxyJNI.vx_participant_t_is_moderator_muted_get(this.swigCPtr, this);
    }

    public int getIs_moderator_text_muted() {
        return VxClientProxyJNI.vx_participant_t_is_moderator_text_muted_get(this.swigCPtr, this);
    }

    public int getIs_muted_for_me() {
        return VxClientProxyJNI.vx_participant_t_is_muted_for_me_get(this.swigCPtr, this);
    }

    public int getIs_owner() {
        return VxClientProxyJNI.vx_participant_t_is_owner_get(this.swigCPtr, this);
    }

    public String getLast_name() {
        return VxClientProxyJNI.vx_participant_t_last_name_get(this.swigCPtr, this);
    }

    public String getUri() {
        return VxClientProxyJNI.vx_participant_t_uri_get(this.swigCPtr, this);
    }

    public String getUsername() {
        return VxClientProxyJNI.vx_participant_t_username_get(this.swigCPtr, this);
    }

    public void setAccount_id(int n) {
        VxClientProxyJNI.vx_participant_t_account_id_set(this.swigCPtr, this, n);
    }

    public void setDisplay_name(String string2) {
        VxClientProxyJNI.vx_participant_t_display_name_set(this.swigCPtr, this, string2);
    }

    public void setFirst_name(String string2) {
        VxClientProxyJNI.vx_participant_t_first_name_set(this.swigCPtr, this, string2);
    }

    public void setIs_moderator(int n) {
        VxClientProxyJNI.vx_participant_t_is_moderator_set(this.swigCPtr, this, n);
    }

    public void setIs_moderator_muted(int n) {
        VxClientProxyJNI.vx_participant_t_is_moderator_muted_set(this.swigCPtr, this, n);
    }

    public void setIs_moderator_text_muted(int n) {
        VxClientProxyJNI.vx_participant_t_is_moderator_text_muted_set(this.swigCPtr, this, n);
    }

    public void setIs_muted_for_me(int n) {
        VxClientProxyJNI.vx_participant_t_is_muted_for_me_set(this.swigCPtr, this, n);
    }

    public void setIs_owner(int n) {
        VxClientProxyJNI.vx_participant_t_is_owner_set(this.swigCPtr, this, n);
    }

    public void setLast_name(String string2) {
        VxClientProxyJNI.vx_participant_t_last_name_set(this.swigCPtr, this, string2);
    }

    public void setUri(String string2) {
        VxClientProxyJNI.vx_participant_t_uri_set(this.swigCPtr, this, string2);
    }

    public void setUsername(String string2) {
        VxClientProxyJNI.vx_participant_t_username_set(this.swigCPtr, this, string2);
    }
}

