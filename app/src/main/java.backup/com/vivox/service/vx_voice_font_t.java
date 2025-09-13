/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.VxClientProxyJNI;
import com.vivox.service.vx_font_status;
import com.vivox.service.vx_font_type;

public class vx_voice_font_t {
    protected boolean swigCMemOwn;
    private long swigCPtr;

    public vx_voice_font_t() {
        this(VxClientProxyJNI.new_vx_voice_font_t(), true);
    }

    protected vx_voice_font_t(long l, boolean bl) {
        this.swigCMemOwn = bl;
        this.swigCPtr = l;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected static long getCPtr(vx_voice_font_t vx_voice_font_t2) {
        if (vx_voice_font_t2 != null) return vx_voice_font_t2.swigCPtr;
        return 0L;
    }

    public void delete() {
        synchronized (this) {
            if (this.swigCPtr != 0L && this.swigCMemOwn) {
                this.swigCMemOwn = false;
                VxClientProxyJNI.delete_vx_voice_font_t(this.swigCPtr);
            }
            this.swigCPtr = 0L;
            return;
        }
    }

    protected void finalize() {
        this.delete();
    }

    public String getDescription() {
        return VxClientProxyJNI.vx_voice_font_t_description_get(this.swigCPtr, this);
    }

    public String getExpiration_date() {
        return VxClientProxyJNI.vx_voice_font_t_expiration_date_get(this.swigCPtr, this);
    }

    public int getExpired() {
        return VxClientProxyJNI.vx_voice_font_t_expired_get(this.swigCPtr, this);
    }

    public String getFont_delta() {
        return VxClientProxyJNI.vx_voice_font_t_font_delta_get(this.swigCPtr, this);
    }

    public String getFont_rules() {
        return VxClientProxyJNI.vx_voice_font_t_font_rules_get(this.swigCPtr, this);
    }

    public int getId() {
        return VxClientProxyJNI.vx_voice_font_t_id_get(this.swigCPtr, this);
    }

    public String getName() {
        return VxClientProxyJNI.vx_voice_font_t_name_get(this.swigCPtr, this);
    }

    public int getParent_id() {
        return VxClientProxyJNI.vx_voice_font_t_parent_id_get(this.swigCPtr, this);
    }

    public vx_font_status getStatus() {
        return vx_font_status.swigToEnum(VxClientProxyJNI.vx_voice_font_t_status_get(this.swigCPtr, this));
    }

    public vx_font_type getType() {
        return vx_font_type.swigToEnum(VxClientProxyJNI.vx_voice_font_t_type_get(this.swigCPtr, this));
    }

    public void setDescription(String string2) {
        VxClientProxyJNI.vx_voice_font_t_description_set(this.swigCPtr, this, string2);
    }

    public void setExpiration_date(String string2) {
        VxClientProxyJNI.vx_voice_font_t_expiration_date_set(this.swigCPtr, this, string2);
    }

    public void setExpired(int n) {
        VxClientProxyJNI.vx_voice_font_t_expired_set(this.swigCPtr, this, n);
    }

    public void setFont_delta(String string2) {
        VxClientProxyJNI.vx_voice_font_t_font_delta_set(this.swigCPtr, this, string2);
    }

    public void setFont_rules(String string2) {
        VxClientProxyJNI.vx_voice_font_t_font_rules_set(this.swigCPtr, this, string2);
    }

    public void setId(int n) {
        VxClientProxyJNI.vx_voice_font_t_id_set(this.swigCPtr, this, n);
    }

    public void setName(String string2) {
        VxClientProxyJNI.vx_voice_font_t_name_set(this.swigCPtr, this, string2);
    }

    public void setParent_id(int n) {
        VxClientProxyJNI.vx_voice_font_t_parent_id_set(this.swigCPtr, this, n);
    }

    public void setStatus(vx_font_status vx_font_status2) {
        VxClientProxyJNI.vx_voice_font_t_status_set(this.swigCPtr, this, vx_font_status2.swigValue());
    }

    public void setType(vx_font_type vx_font_type2) {
        VxClientProxyJNI.vx_voice_font_t_type_set(this.swigCPtr, this, vx_font_type2.swigValue());
    }
}

