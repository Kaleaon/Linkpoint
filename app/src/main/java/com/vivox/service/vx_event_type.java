/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.VxClientProxyJNI;

public final class vx_event_type {
    public static final vx_event_type evt_account_login_state_change;
    public static final vx_event_type evt_aux_audio_properties;
    public static final vx_event_type evt_buddy_and_group_list_changed;
    public static final vx_event_type evt_buddy_changed;
    public static final vx_event_type evt_buddy_group_changed;
    public static final vx_event_type evt_buddy_presence;
    public static final vx_event_type evt_idle_state_changed;
    public static final vx_event_type evt_keyboard_mouse;
    public static final vx_event_type evt_max;
    public static final vx_event_type evt_media_completion;
    public static final vx_event_type evt_media_stream_updated;
    public static final vx_event_type evt_message;
    public static final vx_event_type evt_network_message;
    public static final vx_event_type evt_none;
    public static final vx_event_type evt_participant_added;
    public static final vx_event_type evt_participant_removed;
    public static final vx_event_type evt_participant_updated;
    public static final vx_event_type evt_server_app_data;
    public static final vx_event_type evt_session_added;
    public static final vx_event_type evt_session_notification;
    public static final vx_event_type evt_session_removed;
    public static final vx_event_type evt_session_updated;
    public static final vx_event_type evt_sessiongroup_added;
    public static final vx_event_type evt_sessiongroup_playback_frame_played;
    public static final vx_event_type evt_sessiongroup_removed;
    public static final vx_event_type evt_sessiongroup_updated;
    public static final vx_event_type evt_subscription;
    public static final vx_event_type evt_text_stream_updated;
    public static final vx_event_type evt_user_app_data;
    public static final vx_event_type evt_voice_service_connection_state_changed;
    private static int swigNext;
    private static vx_event_type[] swigValues;
    private final String swigName;
    private final int swigValue;

    static {
        swigNext = 0;
        evt_account_login_state_change = new vx_event_type("evt_account_login_state_change", VxClientProxyJNI.evt_account_login_state_change_get());
        evt_aux_audio_properties = new vx_event_type("evt_aux_audio_properties", VxClientProxyJNI.evt_aux_audio_properties_get());
        evt_buddy_and_group_list_changed = new vx_event_type("evt_buddy_and_group_list_changed", VxClientProxyJNI.evt_buddy_and_group_list_changed_get());
        evt_buddy_changed = new vx_event_type("evt_buddy_changed", VxClientProxyJNI.evt_buddy_changed_get());
        evt_buddy_group_changed = new vx_event_type("evt_buddy_group_changed", VxClientProxyJNI.evt_buddy_group_changed_get());
        evt_buddy_presence = new vx_event_type("evt_buddy_presence", VxClientProxyJNI.evt_buddy_presence_get());
        evt_idle_state_changed = new vx_event_type("evt_idle_state_changed", VxClientProxyJNI.evt_idle_state_changed_get());
        evt_keyboard_mouse = new vx_event_type("evt_keyboard_mouse", VxClientProxyJNI.evt_keyboard_mouse_get());
        evt_max = new vx_event_type("evt_max", VxClientProxyJNI.evt_max_get());
        evt_media_completion = new vx_event_type("evt_media_completion", VxClientProxyJNI.evt_media_completion_get());
        evt_media_stream_updated = new vx_event_type("evt_media_stream_updated", VxClientProxyJNI.evt_media_stream_updated_get());
        evt_message = new vx_event_type("evt_message", VxClientProxyJNI.evt_message_get());
        evt_network_message = new vx_event_type("evt_network_message", VxClientProxyJNI.evt_network_message_get());
        evt_none = new vx_event_type("evt_none", VxClientProxyJNI.evt_none_get());
        evt_participant_added = new vx_event_type("evt_participant_added", VxClientProxyJNI.evt_participant_added_get());
        evt_participant_removed = new vx_event_type("evt_participant_removed", VxClientProxyJNI.evt_participant_removed_get());
        evt_participant_updated = new vx_event_type("evt_participant_updated", VxClientProxyJNI.evt_participant_updated_get());
        evt_server_app_data = new vx_event_type("evt_server_app_data", VxClientProxyJNI.evt_server_app_data_get());
        evt_session_added = new vx_event_type("evt_session_added", VxClientProxyJNI.evt_session_added_get());
        evt_session_notification = new vx_event_type("evt_session_notification", VxClientProxyJNI.evt_session_notification_get());
        evt_session_removed = new vx_event_type("evt_session_removed", VxClientProxyJNI.evt_session_removed_get());
        evt_session_updated = new vx_event_type("evt_session_updated", VxClientProxyJNI.evt_session_updated_get());
        evt_sessiongroup_added = new vx_event_type("evt_sessiongroup_added", VxClientProxyJNI.evt_sessiongroup_added_get());
        evt_sessiongroup_playback_frame_played = new vx_event_type("evt_sessiongroup_playback_frame_played", VxClientProxyJNI.evt_sessiongroup_playback_frame_played_get());
        evt_sessiongroup_removed = new vx_event_type("evt_sessiongroup_removed", VxClientProxyJNI.evt_sessiongroup_removed_get());
        evt_sessiongroup_updated = new vx_event_type("evt_sessiongroup_updated", VxClientProxyJNI.evt_sessiongroup_updated_get());
        evt_subscription = new vx_event_type("evt_subscription", VxClientProxyJNI.evt_subscription_get());
        evt_text_stream_updated = new vx_event_type("evt_text_stream_updated", VxClientProxyJNI.evt_text_stream_updated_get());
        evt_user_app_data = new vx_event_type("evt_user_app_data", VxClientProxyJNI.evt_user_app_data_get());
        evt_voice_service_connection_state_changed = new vx_event_type("evt_voice_service_connection_state_changed", VxClientProxyJNI.evt_voice_service_connection_state_changed_get());
        swigValues = new vx_event_type[]{evt_none, evt_account_login_state_change, evt_buddy_presence, evt_subscription, evt_session_notification, evt_message, evt_aux_audio_properties, evt_buddy_changed, evt_buddy_group_changed, evt_buddy_and_group_list_changed, evt_keyboard_mouse, evt_idle_state_changed, evt_media_stream_updated, evt_text_stream_updated, evt_sessiongroup_added, evt_sessiongroup_removed, evt_session_added, evt_session_removed, evt_participant_added, evt_participant_removed, evt_participant_updated, evt_sessiongroup_playback_frame_played, evt_session_updated, evt_sessiongroup_updated, evt_media_completion, evt_server_app_data, evt_user_app_data, evt_network_message, evt_voice_service_connection_state_changed, evt_max};
    }

    private vx_event_type(String string2) {
        this.swigName = string2;
        int n = swigNext;
        swigNext = n + 1;
        this.swigValue = n;
    }

    private vx_event_type(String string2, int n) {
        this.swigName = string2;
        this.swigValue = n;
        swigNext = n + 1;
    }

    private vx_event_type(String string2, vx_event_type vx_event_type2) {
        this.swigName = string2;
        this.swigValue = vx_event_type2.swigValue;
        swigNext = this.swigValue + 1;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static vx_event_type swigToEnum(int n) {
        if (n < swigValues.length && n >= 0 && vx_event_type.swigValues[n].swigValue == n) {
            return swigValues[n];
        }
        int n2 = 0;
        while (n2 < swigValues.length) {
            if (vx_event_type.swigValues[n2].swigValue == n) {
                return swigValues[n2];
            }
            ++n2;
        }
        throw new IllegalArgumentException("No enum " + vx_event_type.class + " with value " + n);
    }

    public final int swigValue() {
        return this.swigValue;
    }

    public String toString() {
        return this.swigName;
    }
}

