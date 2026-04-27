/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.VxClientProxyJNI;
import com.vivox.service.vx_buddy_presence_state;

public class vx_state_buddy_contact_t {
    protected boolean swigCMemOwn;
    private long swigCPtr;

    public vx_state_buddy_contact_t() {
        this(VxClientProxyJNI.new_vx_state_buddy_contact_t(), true);
    }

    protected vx_state_buddy_contact_t(long l, boolean bl) {
        this.swigCMemOwn = bl;
        this.swigCPtr = l;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected static long getCPtr(vx_state_buddy_contact_t vx_state_buddy_contact_t2) {
        if (vx_state_buddy_contact_t2 != null) return vx_state_buddy_contact_t2.swigCPtr;
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

    public String getApplication() {
        return VxClientProxyJNI.vx_state_buddy_contact_t_application_get(this.swigCPtr, this);
    }

    public String getContact() {
        return VxClientProxyJNI.vx_state_buddy_contact_t_contact_get(this.swigCPtr, this);
    }

    public String getCustom_message() {
        return VxClientProxyJNI.vx_state_buddy_contact_t_custom_message_get(this.swigCPtr, this);
    }

    public String getDisplay_name() {
        return VxClientProxyJNI.vx_state_buddy_contact_t_display_name_get(this.swigCPtr, this);
    }

    public String getId() {
        return VxClientProxyJNI.vx_state_buddy_contact_t_id_get(this.swigCPtr, this);
    }

    public vx_buddy_presence_state getPresence() {
        return vx_buddy_presence_state.swigToEnum(VxClientProxyJNI.vx_state_buddy_contact_t_presence_get(this.swigCPtr, this));
    }

    public String getPriority() {
        return VxClientProxyJNI.vx_state_buddy_contact_t_priority_get(this.swigCPtr, this);
    }

    public void setApplication(String string2) {
        VxClientProxyJNI.vx_state_buddy_contact_t_application_set(this.swigCPtr, this, string2);
    }

    public void setContact(String string2) {
        VxClientProxyJNI.vx_state_buddy_contact_t_contact_set(this.swigCPtr, this, string2);
    }

    public void setCustom_message(String string2) {
        VxClientProxyJNI.vx_state_buddy_contact_t_custom_message_set(this.swigCPtr, this, string2);
    }

    public void setDisplay_name(String string2) {
        VxClientProxyJNI.vx_state_buddy_contact_t_display_name_set(this.swigCPtr, this, string2);
    }

    public void setId(String string2) {
        VxClientProxyJNI.vx_state_buddy_contact_t_id_set(this.swigCPtr, this, string2);
    }

    public void setPresence(vx_buddy_presence_state vx_buddy_presence_state2) {
        VxClientProxyJNI.vx_state_buddy_contact_t_presence_set(this.swigCPtr, this, vx_buddy_presence_state2.swigValue());
    }

    public void setPriority(String string2) {
        VxClientProxyJNI.vx_state_buddy_contact_t_priority_set(this.swigCPtr, this, string2);
    }
}

