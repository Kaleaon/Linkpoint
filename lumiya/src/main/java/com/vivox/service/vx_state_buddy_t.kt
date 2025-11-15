/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.SWIGTYPE_p_p_vx_state_buddy_contact
import com.vivox.service.VxClientProxyJNI

class vx_state_buddy_t {
    protected Boolean swigCMemOwn
    private Long swigCPtr

    vx_state_buddy_t() {
        this(VxClientProxyJNI.new_vx_state_buddy_t(), true)
    }

    protected vx_state_buddy_t(Long l, Boolean bl) {
        this.swigCMemOwn = bl
        this.swigCPtr = l
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected Long getCPtr(vx_state_buddy_t vx_state_buddy_t2) {
        if (vx_state_buddy_t2 != null) return vx_state_buddy_t2.swigCPtr
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

    String getBuddy_data() {
        return VxClientProxyJNI.vx_state_buddy_t_buddy_data_get(this.swigCPtr, this)
    }

    String getBuddy_uri() {
        return VxClientProxyJNI.vx_state_buddy_t_buddy_uri_get(this.swigCPtr, this)
    }

    String getDisplay_name() {
        return VxClientProxyJNI.vx_state_buddy_t_display_name_get(this.swigCPtr, this)
    }

    Int getParent_group_id() {
        return VxClientProxyJNI.vx_state_buddy_t_parent_group_id_get(this.swigCPtr, this)
    }

    Int getState_buddy_contact_count() {
        return VxClientProxyJNI.vx_state_buddy_t_state_buddy_contact_count_get(this.swigCPtr, this)
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    SWIGTYPE_p_p_vx_state_buddy_contact getState_buddy_contacts() {
        Long l = VxClientProxyJNI.vx_state_buddy_t_state_buddy_contacts_get(this.swigCPtr, this)
        if (l != 0L) return SWIGTYPE_p_p_vx_state_buddy_contact(l, false)
        return null
    }

    Unit setBuddy_data(String string2) {
        VxClientProxyJNI.vx_state_buddy_t_buddy_data_set(this.swigCPtr, this, string2)
    }

    Unit setBuddy_uri(String string2) {
        VxClientProxyJNI.vx_state_buddy_t_buddy_uri_set(this.swigCPtr, this, string2)
    }

    Unit setDisplay_name(String string2) {
        VxClientProxyJNI.vx_state_buddy_t_display_name_set(this.swigCPtr, this, string2)
    }

    Unit setParent_group_id(Int n) {
        VxClientProxyJNI.vx_state_buddy_t_parent_group_id_set(this.swigCPtr, this, n)
    }

    Unit setState_buddy_contact_count(Int n) {
        VxClientProxyJNI.vx_state_buddy_t_state_buddy_contact_count_set(this.swigCPtr, this, n)
    }

    Unit setState_buddy_contacts(SWIGTYPE_p_p_vx_state_buddy_contact sWIGTYPE_p_p_vx_state_buddy_contact) {
        VxClientProxyJNI.vx_state_buddy_t_state_buddy_contacts_set(this.swigCPtr, this, SWIGTYPE_p_p_vx_state_buddy_contact.getCPtr(sWIGTYPE_p_p_vx_state_buddy_contact))
    }
}

