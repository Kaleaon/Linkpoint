/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.VxClientProxyJNI;
import com.vivox.service.vx_channel_mode;
import com.vivox.service.vx_channel_type;
import com.vivox.service.vx_req_base_t;

public class vx_req_account_channel_create_t {
    protected boolean swigCMemOwn;
    private long swigCPtr;

    public vx_req_account_channel_create_t() {
        this(VxClientProxyJNI.new_vx_req_account_channel_create_t(), true);
    }

    protected vx_req_account_channel_create_t(long l, boolean bl) {
        this.swigCMemOwn = bl;
        this.swigCPtr = l;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected static long getCPtr(vx_req_account_channel_create_t vx_req_account_channel_create_t2) {
        if (vx_req_account_channel_create_t2 != null) return vx_req_account_channel_create_t2.swigCPtr;
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

    public String getAccount_handle() {
        return VxClientProxyJNI.vx_req_account_channel_create_t_account_handle_get(this.swigCPtr, this);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public vx_req_base_t getBase() {
        long l = VxClientProxyJNI.vx_req_account_channel_create_t_base_get(this.swigCPtr, this);
        if (l != 0L) return new vx_req_base_t(l, false);
        return null;
    }

    public int getCapacity() {
        return VxClientProxyJNI.vx_req_account_channel_create_t_capacity_get(this.swigCPtr, this);
    }

    public String getChannel_desc() {
        return VxClientProxyJNI.vx_req_account_channel_create_t_channel_desc_get(this.swigCPtr, this);
    }

    public vx_channel_mode getChannel_mode() {
        return vx_channel_mode.swigToEnum(VxClientProxyJNI.vx_req_account_channel_create_t_channel_mode_get(this.swigCPtr, this));
    }

    public String getChannel_name() {
        return VxClientProxyJNI.vx_req_account_channel_create_t_channel_name_get(this.swigCPtr, this);
    }

    public vx_channel_type getChannel_type() {
        return vx_channel_type.swigToEnum(VxClientProxyJNI.vx_req_account_channel_create_t_channel_type_get(this.swigCPtr, this));
    }

    public int getClamping_dist() {
        return VxClientProxyJNI.vx_req_account_channel_create_t_clamping_dist_get(this.swigCPtr, this);
    }

    public int getDist_model() {
        return VxClientProxyJNI.vx_req_account_channel_create_t_dist_model_get(this.swigCPtr, this);
    }

    public int getEncrypt_audio() {
        return VxClientProxyJNI.vx_req_account_channel_create_t_encrypt_audio_get(this.swigCPtr, this);
    }

    public double getMax_gain() {
        return VxClientProxyJNI.vx_req_account_channel_create_t_max_gain_get(this.swigCPtr, this);
    }

    public int getMax_participants() {
        return VxClientProxyJNI.vx_req_account_channel_create_t_max_participants_get(this.swigCPtr, this);
    }

    public int getMax_range() {
        return VxClientProxyJNI.vx_req_account_channel_create_t_max_range_get(this.swigCPtr, this);
    }

    public String getProtected_password() {
        return VxClientProxyJNI.vx_req_account_channel_create_t_protected_password_get(this.swigCPtr, this);
    }

    public double getRoll_off() {
        return VxClientProxyJNI.vx_req_account_channel_create_t_roll_off_get(this.swigCPtr, this);
    }

    public int getSet_persistent() {
        return VxClientProxyJNI.vx_req_account_channel_create_t_set_persistent_get(this.swigCPtr, this);
    }

    public int getSet_protected() {
        return VxClientProxyJNI.vx_req_account_channel_create_t_set_protected_get(this.swigCPtr, this);
    }

    public void setAccount_handle(String string2) {
        VxClientProxyJNI.vx_req_account_channel_create_t_account_handle_set(this.swigCPtr, this, string2);
    }

    public void setBase(vx_req_base_t vx_req_base_t2) {
        VxClientProxyJNI.vx_req_account_channel_create_t_base_set(this.swigCPtr, this, vx_req_base_t.getCPtr(vx_req_base_t2), vx_req_base_t2);
    }

    public void setCapacity(int n) {
        VxClientProxyJNI.vx_req_account_channel_create_t_capacity_set(this.swigCPtr, this, n);
    }

    public void setChannel_desc(String string2) {
        VxClientProxyJNI.vx_req_account_channel_create_t_channel_desc_set(this.swigCPtr, this, string2);
    }

    public void setChannel_mode(vx_channel_mode vx_channel_mode2) {
        VxClientProxyJNI.vx_req_account_channel_create_t_channel_mode_set(this.swigCPtr, this, vx_channel_mode2.swigValue());
    }

    public void setChannel_name(String string2) {
        VxClientProxyJNI.vx_req_account_channel_create_t_channel_name_set(this.swigCPtr, this, string2);
    }

    public void setChannel_type(vx_channel_type vx_channel_type2) {
        VxClientProxyJNI.vx_req_account_channel_create_t_channel_type_set(this.swigCPtr, this, vx_channel_type2.swigValue());
    }

    public void setClamping_dist(int n) {
        VxClientProxyJNI.vx_req_account_channel_create_t_clamping_dist_set(this.swigCPtr, this, n);
    }

    public void setDist_model(int n) {
        VxClientProxyJNI.vx_req_account_channel_create_t_dist_model_set(this.swigCPtr, this, n);
    }

    public void setEncrypt_audio(int n) {
        VxClientProxyJNI.vx_req_account_channel_create_t_encrypt_audio_set(this.swigCPtr, this, n);
    }

    public void setMax_gain(double d) {
        VxClientProxyJNI.vx_req_account_channel_create_t_max_gain_set(this.swigCPtr, this, d);
    }

    public void setMax_participants(int n) {
        VxClientProxyJNI.vx_req_account_channel_create_t_max_participants_set(this.swigCPtr, this, n);
    }

    public void setMax_range(int n) {
        VxClientProxyJNI.vx_req_account_channel_create_t_max_range_set(this.swigCPtr, this, n);
    }

    public void setProtected_password(String string2) {
        VxClientProxyJNI.vx_req_account_channel_create_t_protected_password_set(this.swigCPtr, this, string2);
    }

    public void setRoll_off(double d) {
        VxClientProxyJNI.vx_req_account_channel_create_t_roll_off_set(this.swigCPtr, this, d);
    }

    public void setSet_persistent(int n) {
        VxClientProxyJNI.vx_req_account_channel_create_t_set_persistent_set(this.swigCPtr, this, n);
    }

    public void setSet_protected(int n) {
        VxClientProxyJNI.vx_req_account_channel_create_t_set_protected_set(this.swigCPtr, this, n);
    }
}

