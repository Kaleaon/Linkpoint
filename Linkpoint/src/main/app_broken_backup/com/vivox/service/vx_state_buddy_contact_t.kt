/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.VxClientProxyJNI
import com.vivox.service.vx_buddy_presence_state

class vx_state_buddy_contact_t {
    protected Boolean swigCMemOwn
    private Long swigCPtr

    vx_state_buddy_contact_t() {
        this(VxClientProxyJNI.new_vx_state_buddy_contact_t(), true)
    }

    protected vx_state_buddy_contact_t(Long l, Boolean bl) {
        this.swigCMemOwn = bl
        this.swigCPtr = l
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected Long getCPtr(vx_state_buddy_contact_t vx_state_buddy_contact_t2) {
        if (vx_state_buddy_contact_t2 != null) return vx_state_buddy_contact_t2.swigCPtr
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

    String getApplication() {
        return VxClientProxyJNI.vx_state_buddy_contact_t_application_get(this.swigCPtr, this)
    }

    String getContact() {
        return VxClientProxyJNI.vx_state_buddy_contact_t_contact_get(this.swigCPtr, this)
    }

    String getCustom_message() {
        return VxClientProxyJNI.vx_state_buddy_contact_t_custom_message_get(this.swigCPtr, this)
    }

    String getDisplay_name() {
        return VxClientProxyJNI.vx_state_buddy_contact_t_display_name_get(this.swigCPtr, this)
    }

    String getId() {
        return VxClientProxyJNI.vx_state_buddy_contact_t_id_get(this.swigCPtr, this)
    }

    vx_buddy_presence_state getPresence() {
        return vx_buddy_presence_state.swigToEnum(VxClientProxyJNI.vx_state_buddy_contact_t_presence_get(this.swigCPtr, this))
    }

    String getPriority() {
        return VxClientProxyJNI.vx_state_buddy_contact_t_priority_get(this.swigCPtr, this)
    }

    Unit setApplication(String string2) {
        VxClientProxyJNI.vx_state_buddy_contact_t_application_set(this.swigCPtr, this, string2)
    }

    Unit setContact(String string2) {
        VxClientProxyJNI.vx_state_buddy_contact_t_contact_set(this.swigCPtr, this, string2)
    }

    Unit setCustom_message(String string2) {
        VxClientProxyJNI.vx_state_buddy_contact_t_custom_message_set(this.swigCPtr, this, string2)
    }

    Unit setDisplay_name(String string2) {
        VxClientProxyJNI.vx_state_buddy_contact_t_display_name_set(this.swigCPtr, this, string2)
    }

    Unit setId(String string2) {
        VxClientProxyJNI.vx_state_buddy_contact_t_id_set(this.swigCPtr, this, string2)
    }

    Unit setPresence(vx_buddy_presence_state vx_buddy_presence_state2) {
        VxClientProxyJNI.vx_state_buddy_contact_t_presence_set(this.swigCPtr, this, vx_buddy_presence_state2.swigValue())
    }

    Unit setPriority(String string2) {
        VxClientProxyJNI.vx_state_buddy_contact_t_priority_set(this.swigCPtr, this, string2)
    }
}

