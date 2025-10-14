/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.VxClientProxyJNI

class vx_participant_t {
    protected Boolean swigCMemOwn
    private Long swigCPtr

    vx_participant_t() {
        this(VxClientProxyJNI.new_vx_participant_t(), true)
    }

    protected vx_participant_t(Long l, Boolean bl) {
        this.swigCMemOwn = bl
        this.swigCPtr = l
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected Long getCPtr(vx_participant_t vx_participant_t2) {
        if (vx_participant_t2 != null) return vx_participant_t2.swigCPtr
        return 0L
    }

    Unit delete() {
        synchronized (this) {
            if (this.swigCPtr != 0L && this.swigCMemOwn) {
                this.swigCMemOwn = false
                VxClientProxyJNI.delete_vx_participant_t(this.swigCPtr)
            }
            this.swigCPtr = 0L
            return
        }
    }

    protected Unit finalize() {
        this.delete()
    }

    Int getAccount_id() {
        return VxClientProxyJNI.vx_participant_t_account_id_get(this.swigCPtr, this)
    }

    String getDisplay_name() {
        return VxClientProxyJNI.vx_participant_t_display_name_get(this.swigCPtr, this)
    }

    String getFirst_name() {
        return VxClientProxyJNI.vx_participant_t_first_name_get(this.swigCPtr, this)
    }

    Int getIs_moderator() {
        return VxClientProxyJNI.vx_participant_t_is_moderator_get(this.swigCPtr, this)
    }

    Int getIs_moderator_muted() {
        return VxClientProxyJNI.vx_participant_t_is_moderator_muted_get(this.swigCPtr, this)
    }

    Int getIs_moderator_text_muted() {
        return VxClientProxyJNI.vx_participant_t_is_moderator_text_muted_get(this.swigCPtr, this)
    }

    Int getIs_muted_for_me() {
        return VxClientProxyJNI.vx_participant_t_is_muted_for_me_get(this.swigCPtr, this)
    }

    Int getIs_owner() {
        return VxClientProxyJNI.vx_participant_t_is_owner_get(this.swigCPtr, this)
    }

    String getLast_name() {
        return VxClientProxyJNI.vx_participant_t_last_name_get(this.swigCPtr, this)
    }

    String getUri() {
        return VxClientProxyJNI.vx_participant_t_uri_get(this.swigCPtr, this)
    }

    String getUsername() {
        return VxClientProxyJNI.vx_participant_t_username_get(this.swigCPtr, this)
    }

    Unit setAccount_id(Int n) {
        VxClientProxyJNI.vx_participant_t_account_id_set(this.swigCPtr, this, n)
    }

    Unit setDisplay_name(String string2) {
        VxClientProxyJNI.vx_participant_t_display_name_set(this.swigCPtr, this, string2)
    }

    Unit setFirst_name(String string2) {
        VxClientProxyJNI.vx_participant_t_first_name_set(this.swigCPtr, this, string2)
    }

    Unit setIs_moderator(Int n) {
        VxClientProxyJNI.vx_participant_t_is_moderator_set(this.swigCPtr, this, n)
    }

    Unit setIs_moderator_muted(Int n) {
        VxClientProxyJNI.vx_participant_t_is_moderator_muted_set(this.swigCPtr, this, n)
    }

    Unit setIs_moderator_text_muted(Int n) {
        VxClientProxyJNI.vx_participant_t_is_moderator_text_muted_set(this.swigCPtr, this, n)
    }

    Unit setIs_muted_for_me(Int n) {
        VxClientProxyJNI.vx_participant_t_is_muted_for_me_set(this.swigCPtr, this, n)
    }

    Unit setIs_owner(Int n) {
        VxClientProxyJNI.vx_participant_t_is_owner_set(this.swigCPtr, this, n)
    }

    Unit setLast_name(String string2) {
        VxClientProxyJNI.vx_participant_t_last_name_set(this.swigCPtr, this, string2)
    }

    Unit setUri(String string2) {
        VxClientProxyJNI.vx_participant_t_uri_set(this.swigCPtr, this, string2)
    }

    Unit setUsername(String string2) {
        VxClientProxyJNI.vx_participant_t_username_set(this.swigCPtr, this, string2)
    }
}

