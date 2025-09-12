/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.VxClientProxyJNI;
import com.vivox.service.vx_channel_mode;

public class vx_channel_t {
    protected boolean swigCMemOwn;
    private long swigCPtr;

    public vx_channel_t() {
        this(VxClientProxyJNI.new_vx_channel_t(), true);
    }

    protected vx_channel_t(long l, boolean bl) {
        this.swigCMemOwn = bl;
        this.swigCPtr = l;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected static long getCPtr(vx_channel_t vx_channel_t2) {
        if (vx_channel_t2 != null) return vx_channel_t2.swigCPtr;
        return 0L;
    }

    public void delete() {
        synchronized (this) {
            if (this.swigCPtr != 0L && this.swigCMemOwn) {
                this.swigCMemOwn = false;
                VxClientProxyJNI.delete_vx_channel_t(this.swigCPtr);
            }
            this.swigCPtr = 0L;
            return;
        }
    }

    protected void finalize() {
        this.delete();
    }

    public int getActive_participants() {
        return VxClientProxyJNI.vx_channel_t_active_participants_get(this.swigCPtr, this);
    }

    public int getCapacity() {
        return VxClientProxyJNI.vx_channel_t_capacity_get(this.swigCPtr, this);
    }

    public String getChannel_desc() {
        return VxClientProxyJNI.vx_channel_t_channel_desc_get(this.swigCPtr, this);
    }

    public int getChannel_id() {
        return VxClientProxyJNI.vx_channel_t_channel_id_get(this.swigCPtr, this);
    }

    public String getChannel_name() {
        return VxClientProxyJNI.vx_channel_t_channel_name_get(this.swigCPtr, this);
    }

    public String getChannel_uri() {
        return VxClientProxyJNI.vx_channel_t_channel_uri_get(this.swigCPtr, this);
    }

    public int getClamping_dist() {
        return VxClientProxyJNI.vx_channel_t_clamping_dist_get(this.swigCPtr, this);
    }

    public int getDist_model() {
        return VxClientProxyJNI.vx_channel_t_dist_model_get(this.swigCPtr, this);
    }

    public int getEncrypt_audio() {
        return VxClientProxyJNI.vx_channel_t_encrypt_audio_get(this.swigCPtr, this);
    }

    public String getHost() {
        return VxClientProxyJNI.vx_channel_t_host_get(this.swigCPtr, this);
    }

    public int getIs_persistent() {
        return VxClientProxyJNI.vx_channel_t_is_persistent_get(this.swigCPtr, this);
    }

    public int getIs_protected() {
        return VxClientProxyJNI.vx_channel_t_is_protected_get(this.swigCPtr, this);
    }

    public int getLimit() {
        return VxClientProxyJNI.vx_channel_t_limit_get(this.swigCPtr, this);
    }

    public double getMax_gain() {
        return VxClientProxyJNI.vx_channel_t_max_gain_get(this.swigCPtr, this);
    }

    public int getMax_range() {
        return VxClientProxyJNI.vx_channel_t_max_range_get(this.swigCPtr, this);
    }

    public vx_channel_mode getMode() {
        return vx_channel_mode.swigToEnum(VxClientProxyJNI.vx_channel_t_mode_get(this.swigCPtr, this));
    }

    public String getModified() {
        return VxClientProxyJNI.vx_channel_t_modified_get(this.swigCPtr, this);
    }

    public String getOwner() {
        return VxClientProxyJNI.vx_channel_t_owner_get(this.swigCPtr, this);
    }

    public String getOwner_display_name() {
        return VxClientProxyJNI.vx_channel_t_owner_display_name_get(this.swigCPtr, this);
    }

    public String getOwner_user_name() {
        return VxClientProxyJNI.vx_channel_t_owner_user_name_get(this.swigCPtr, this);
    }

    public double getRoll_off() {
        return VxClientProxyJNI.vx_channel_t_roll_off_get(this.swigCPtr, this);
    }

    public int getSize() {
        return VxClientProxyJNI.vx_channel_t_size_get(this.swigCPtr, this);
    }

    public int getType() {
        return VxClientProxyJNI.vx_channel_t_type_get(this.swigCPtr, this);
    }

    public void setActive_participants(int n) {
        VxClientProxyJNI.vx_channel_t_active_participants_set(this.swigCPtr, this, n);
    }

    public void setCapacity(int n) {
        VxClientProxyJNI.vx_channel_t_capacity_set(this.swigCPtr, this, n);
    }

    public void setChannel_desc(String string2) {
        VxClientProxyJNI.vx_channel_t_channel_desc_set(this.swigCPtr, this, string2);
    }

    public void setChannel_id(int n) {
        VxClientProxyJNI.vx_channel_t_channel_id_set(this.swigCPtr, this, n);
    }

    public void setChannel_name(String string2) {
        VxClientProxyJNI.vx_channel_t_channel_name_set(this.swigCPtr, this, string2);
    }

    public void setChannel_uri(String string2) {
        VxClientProxyJNI.vx_channel_t_channel_uri_set(this.swigCPtr, this, string2);
    }

    public void setClamping_dist(int n) {
        VxClientProxyJNI.vx_channel_t_clamping_dist_set(this.swigCPtr, this, n);
    }

    public void setDist_model(int n) {
        VxClientProxyJNI.vx_channel_t_dist_model_set(this.swigCPtr, this, n);
    }

    public void setEncrypt_audio(int n) {
        VxClientProxyJNI.vx_channel_t_encrypt_audio_set(this.swigCPtr, this, n);
    }

    public void setHost(String string2) {
        VxClientProxyJNI.vx_channel_t_host_set(this.swigCPtr, this, string2);
    }

    public void setIs_persistent(int n) {
        VxClientProxyJNI.vx_channel_t_is_persistent_set(this.swigCPtr, this, n);
    }

    public void setIs_protected(int n) {
        VxClientProxyJNI.vx_channel_t_is_protected_set(this.swigCPtr, this, n);
    }

    public void setLimit(int n) {
        VxClientProxyJNI.vx_channel_t_limit_set(this.swigCPtr, this, n);
    }

    public void setMax_gain(double d) {
        VxClientProxyJNI.vx_channel_t_max_gain_set(this.swigCPtr, this, d);
    }

    public void setMax_range(int n) {
        VxClientProxyJNI.vx_channel_t_max_range_set(this.swigCPtr, this, n);
    }

    public void setMode(vx_channel_mode vx_channel_mode2) {
        VxClientProxyJNI.vx_channel_t_mode_set(this.swigCPtr, this, vx_channel_mode2.swigValue());
    }

    public void setModified(String string2) {
        VxClientProxyJNI.vx_channel_t_modified_set(this.swigCPtr, this, string2);
    }

    public void setOwner(String string2) {
        VxClientProxyJNI.vx_channel_t_owner_set(this.swigCPtr, this, string2);
    }

    public void setOwner_display_name(String string2) {
        VxClientProxyJNI.vx_channel_t_owner_display_name_set(this.swigCPtr, this, string2);
    }

    public void setOwner_user_name(String string2) {
        VxClientProxyJNI.vx_channel_t_owner_user_name_set(this.swigCPtr, this, string2);
    }

    public void setRoll_off(double d) {
        VxClientProxyJNI.vx_channel_t_roll_off_set(this.swigCPtr, this, d);
    }

    public void setSize(int n) {
        VxClientProxyJNI.vx_channel_t_size_set(this.swigCPtr, this, n);
    }

    public void setType(int n) {
        VxClientProxyJNI.vx_channel_t_type_set(this.swigCPtr, this, n);
    }
}

