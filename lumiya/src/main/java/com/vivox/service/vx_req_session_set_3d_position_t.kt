/*
import java.util.*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.SWIGTYPE_p_double
import com.vivox.service.VxClientProxyJNI
import com.vivox.service.orientation_type
import com.vivox.service.req_disposition_type_t
import com.vivox.service.vx_req_base_t

class vx_req_session_set_3d_position_t {
    protected Boolean swigCMemOwn
    private Long swigCPtr

    vx_req_session_set_3d_position_t() {
        this(VxClientProxyJNI.new_vx_req_session_set_3d_position_t(), true)
    }

    protected vx_req_session_set_3d_position_t(Long l, Boolean bl) {
        this.swigCMemOwn = bl
        this.swigCPtr = l
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected Long getCPtr(vx_req_session_set_3d_position_t vx_req_session_set_3d_position_t2) {
        if (vx_req_session_set_3d_position_t2 != null) return vx_req_session_set_3d_position_t2.swigCPtr
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

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_base_t getBase() {
        Long l = VxClientProxyJNI.vx_req_session_set_3d_position_t_base_get(this.swigCPtr, this)
        if (l != 0L) return vx_req_base_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    SWIGTYPE_p_double getListener_at_orientation() {
        Long l = VxClientProxyJNI.vx_req_session_set_3d_position_t_listener_at_orientation_get(this.swigCPtr, this)
        if (l != 0L) return SWIGTYPE_p_double(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    SWIGTYPE_p_double getListener_left_orientation() {
        Long l = VxClientProxyJNI.vx_req_session_set_3d_position_t_listener_left_orientation_get(this.swigCPtr, this)
        if (l != 0L) return SWIGTYPE_p_double(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    SWIGTYPE_p_double getListener_position() {
        Long l = VxClientProxyJNI.vx_req_session_set_3d_position_t_listener_position_get(this.swigCPtr, this)
        if (l != 0L) return SWIGTYPE_p_double(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    SWIGTYPE_p_double getListener_up_orientation() {
        Long l = VxClientProxyJNI.vx_req_session_set_3d_position_t_listener_up_orientation_get(this.swigCPtr, this)
        if (l != 0L) return SWIGTYPE_p_double(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    SWIGTYPE_p_double getListener_velocity() {
        Long l = VxClientProxyJNI.vx_req_session_set_3d_position_t_listener_velocity_get(this.swigCPtr, this)
        if (l != 0L) return SWIGTYPE_p_double(l, false)
        return null
    }

    req_disposition_type_t getReq_disposition_type() {
        return req_disposition_type_t.swigToEnum(VxClientProxyJNI.vx_req_session_set_3d_position_t_req_disposition_type_get(this.swigCPtr, this))
    }

    String getSession_handle() {
        return VxClientProxyJNI.vx_req_session_set_3d_position_t_session_handle_get(this.swigCPtr, this)
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    SWIGTYPE_p_double getSpeaker_at_orientation() {
        Long l = VxClientProxyJNI.vx_req_session_set_3d_position_t_speaker_at_orientation_get(this.swigCPtr, this)
        if (l != 0L) return SWIGTYPE_p_double(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    SWIGTYPE_p_double getSpeaker_left_orientation() {
        Long l = VxClientProxyJNI.vx_req_session_set_3d_position_t_speaker_left_orientation_get(this.swigCPtr, this)
        if (l != 0L) return SWIGTYPE_p_double(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    SWIGTYPE_p_double getSpeaker_position() {
        Long l = VxClientProxyJNI.vx_req_session_set_3d_position_t_speaker_position_get(this.swigCPtr, this)
        if (l != 0L) return SWIGTYPE_p_double(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    SWIGTYPE_p_double getSpeaker_up_orientation() {
        Long l = VxClientProxyJNI.vx_req_session_set_3d_position_t_speaker_up_orientation_get(this.swigCPtr, this)
        if (l != 0L) return SWIGTYPE_p_double(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    SWIGTYPE_p_double getSpeaker_velocity() {
        Long l = VxClientProxyJNI.vx_req_session_set_3d_position_t_speaker_velocity_get(this.swigCPtr, this)
        if (l != 0L) return SWIGTYPE_p_double(l, false)
        return null
    }

    orientation_type getType() {
        return orientation_type.swigToEnum(VxClientProxyJNI.vx_req_session_set_3d_position_t_type_get(this.swigCPtr, this))
    }

    Unit setBase(vx_req_base_t vx_req_base_t2) {
        VxClientProxyJNI.vx_req_session_set_3d_position_t_base_set(this.swigCPtr, this, vx_req_base_t.getCPtr(vx_req_base_t2), vx_req_base_t2)
    }

    Unit setListener_at_orientation(SWIGTYPE_p_double sWIGTYPE_p_double) {
        VxClientProxyJNI.vx_req_session_set_3d_position_t_listener_at_orientation_set(this.swigCPtr, this, SWIGTYPE_p_double.getCPtr(sWIGTYPE_p_double))
    }

    Unit setListener_left_orientation(SWIGTYPE_p_double sWIGTYPE_p_double) {
        VxClientProxyJNI.vx_req_session_set_3d_position_t_listener_left_orientation_set(this.swigCPtr, this, SWIGTYPE_p_double.getCPtr(sWIGTYPE_p_double))
    }

    Unit setListener_position(SWIGTYPE_p_double sWIGTYPE_p_double) {
        VxClientProxyJNI.vx_req_session_set_3d_position_t_listener_position_set(this.swigCPtr, this, SWIGTYPE_p_double.getCPtr(sWIGTYPE_p_double))
    }

    Unit setListener_up_orientation(SWIGTYPE_p_double sWIGTYPE_p_double) {
        VxClientProxyJNI.vx_req_session_set_3d_position_t_listener_up_orientation_set(this.swigCPtr, this, SWIGTYPE_p_double.getCPtr(sWIGTYPE_p_double))
    }

    Unit setListener_velocity(SWIGTYPE_p_double sWIGTYPE_p_double) {
        VxClientProxyJNI.vx_req_session_set_3d_position_t_listener_velocity_set(this.swigCPtr, this, SWIGTYPE_p_double.getCPtr(sWIGTYPE_p_double))
    }

    Unit setReq_disposition_type(req_disposition_type_t req_disposition_type_t2) {
        VxClientProxyJNI.vx_req_session_set_3d_position_t_req_disposition_type_set(this.swigCPtr, this, req_disposition_type_t2.swigValue())
    }

    Unit setSession_handle(String string2) {
        VxClientProxyJNI.vx_req_session_set_3d_position_t_session_handle_set(this.swigCPtr, this, string2)
    }

    Unit setSpeaker_at_orientation(SWIGTYPE_p_double sWIGTYPE_p_double) {
        VxClientProxyJNI.vx_req_session_set_3d_position_t_speaker_at_orientation_set(this.swigCPtr, this, SWIGTYPE_p_double.getCPtr(sWIGTYPE_p_double))
    }

    Unit setSpeaker_left_orientation(SWIGTYPE_p_double sWIGTYPE_p_double) {
        VxClientProxyJNI.vx_req_session_set_3d_position_t_speaker_left_orientation_set(this.swigCPtr, this, SWIGTYPE_p_double.getCPtr(sWIGTYPE_p_double))
    }

    Unit setSpeaker_position(SWIGTYPE_p_double sWIGTYPE_p_double) {
        VxClientProxyJNI.vx_req_session_set_3d_position_t_speaker_position_set(this.swigCPtr, this, SWIGTYPE_p_double.getCPtr(sWIGTYPE_p_double))
    }

    Unit setSpeaker_up_orientation(SWIGTYPE_p_double sWIGTYPE_p_double) {
        VxClientProxyJNI.vx_req_session_set_3d_position_t_speaker_up_orientation_set(this.swigCPtr, this, SWIGTYPE_p_double.getCPtr(sWIGTYPE_p_double))
    }

    Unit setSpeaker_velocity(SWIGTYPE_p_double sWIGTYPE_p_double) {
        VxClientProxyJNI.vx_req_session_set_3d_position_t_speaker_velocity_set(this.swigCPtr, this, SWIGTYPE_p_double.getCPtr(sWIGTYPE_p_double))
    }

    Unit setType(orientation_type orientation_type2) {
        VxClientProxyJNI.vx_req_session_set_3d_position_t_type_set(this.swigCPtr, this, orientation_type2.swigValue())
    }
}

