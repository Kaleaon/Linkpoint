/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.SWIGTYPE_p_p_vx_state_buddy
import com.vivox.service.SWIGTYPE_p_p_vx_state_buddy_group
import com.vivox.service.SWIGTYPE_p_p_vx_state_sessiongroup
import com.vivox.service.VxClientProxyJNI
import com.vivox.service.vx_login_state_change_state

class vx_state_account_t {
    protected Boolean swigCMemOwn
    private Long swigCPtr

    vx_state_account_t() {
        this(VxClientProxyJNI.new_vx_state_account_t(), true)
    }

    protected vx_state_account_t(Long l, Boolean bl) {
        this.swigCMemOwn = bl
        this.swigCPtr = l
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected Long getCPtr(vx_state_account_t vx_state_account_t2) {
        if (vx_state_account_t2 != null) return vx_state_account_t2.swigCPtr
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

    String getAccount_handle() {
        return VxClientProxyJNI.vx_state_account_t_account_handle_get(this.swigCPtr, this)
    }

    String getAccount_uri() {
        return VxClientProxyJNI.vx_state_account_t_account_uri_get(this.swigCPtr, this)
    }

    String getDisplay_name() {
        return VxClientProxyJNI.vx_state_account_t_display_name_get(this.swigCPtr, this)
    }

    Int getIs_anonymous_login() {
        return VxClientProxyJNI.vx_state_account_t_is_anonymous_login_get(this.swigCPtr, this)
    }

    vx_login_state_change_state getState() {
        return vx_login_state_change_state.swigToEnum(VxClientProxyJNI.vx_state_account_t_state_get(this.swigCPtr, this))
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    SWIGTYPE_p_p_vx_state_buddy getState_buddies() {
        var l: Long = VxClientProxyJNI.vx_state_account_t_state_buddies_get(this.swigCPtr, this)
        if (l != 0L) return SWIGTYPE_p_p_vx_state_buddy(l, false)
        return null
    }

    Int getState_buddy_count() {
        return VxClientProxyJNI.vx_state_account_t_state_buddy_count_get(this.swigCPtr, this)
    }

    Int getState_buddy_group_count() {
        return VxClientProxyJNI.vx_state_account_t_state_buddy_group_count_get(this.swigCPtr, this)
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    SWIGTYPE_p_p_vx_state_buddy_group getState_buddy_groups() {
        var l: Long = VxClientProxyJNI.vx_state_account_t_state_buddy_groups_get(this.swigCPtr, this)
        if (l != 0L) return SWIGTYPE_p_p_vx_state_buddy_group(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    SWIGTYPE_p_p_vx_state_sessiongroup getState_sessiongroups() {
        var l: Long = VxClientProxyJNI.vx_state_account_t_state_sessiongroups_get(this.swigCPtr, this)
        if (l != 0L) return SWIGTYPE_p_p_vx_state_sessiongroup(l, false)
        return null
    }

    Int getState_sessiongroups_count() {
        return VxClientProxyJNI.vx_state_account_t_state_sessiongroups_count_get(this.swigCPtr, this)
    }

    Unit setAccount_handle(String string2) {
        VxClientProxyJNI.vx_state_account_t_account_handle_set(this.swigCPtr, this, string2)
    }

    Unit setAccount_uri(String string2) {
        VxClientProxyJNI.vx_state_account_t_account_uri_set(this.swigCPtr, this, string2)
    }

    Unit setDisplay_name(String string2) {
        VxClientProxyJNI.vx_state_account_t_display_name_set(this.swigCPtr, this, string2)
    }

    Unit setIs_anonymous_login(Int n) {
        VxClientProxyJNI.vx_state_account_t_is_anonymous_login_set(this.swigCPtr, this, n)
    }

    Unit setState(vx_login_state_change_state vx_login_state_change_state2) {
        VxClientProxyJNI.vx_state_account_t_state_set(this.swigCPtr, this, vx_login_state_change_state2.swigValue())
    }

    Unit setState_buddies(SWIGTYPE_p_p_vx_state_buddy sWIGTYPE_p_p_vx_state_buddy) {
        VxClientProxyJNI.vx_state_account_t_state_buddies_set(this.swigCPtr, this, SWIGTYPE_p_p_vx_state_buddy.getCPtr(sWIGTYPE_p_p_vx_state_buddy))
    }

    Unit setState_buddy_count(Int n) {
        VxClientProxyJNI.vx_state_account_t_state_buddy_count_set(this.swigCPtr, this, n)
    }

    Unit setState_buddy_group_count(Int n) {
        VxClientProxyJNI.vx_state_account_t_state_buddy_group_count_set(this.swigCPtr, this, n)
    }

    Unit setState_buddy_groups(SWIGTYPE_p_p_vx_state_buddy_group sWIGTYPE_p_p_vx_state_buddy_group) {
        VxClientProxyJNI.vx_state_account_t_state_buddy_groups_set(this.swigCPtr, this, SWIGTYPE_p_p_vx_state_buddy_group.getCPtr(sWIGTYPE_p_p_vx_state_buddy_group))
    }

    Unit setState_sessiongroups(SWIGTYPE_p_p_vx_state_sessiongroup sWIGTYPE_p_p_vx_state_sessiongroup) {
        VxClientProxyJNI.vx_state_account_t_state_sessiongroups_set(this.swigCPtr, this, SWIGTYPE_p_p_vx_state_sessiongroup.getCPtr(sWIGTYPE_p_p_vx_state_sessiongroup))
    }

    Unit setState_sessiongroups_count(Int n) {
        VxClientProxyJNI.vx_state_account_t_state_sessiongroups_count_set(this.swigCPtr, this, n)
    }
}

