/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.VxClientProxyJNI;

public final class vx_request_type {
    public static final vx_request_type req_account_anonymous_login = new vx_request_type("req_account_anonymous_login", VxClientProxyJNI.req_account_anonymous_login_get());
    public static final vx_request_type req_account_authtoken_login = new vx_request_type("req_account_authtoken_login", VxClientProxyJNI.req_account_authtoken_login_get());
    public static final vx_request_type req_account_buddy_delete = new vx_request_type("req_account_buddy_delete", VxClientProxyJNI.req_account_buddy_delete_get());
    public static final vx_request_type req_account_buddy_search = new vx_request_type("req_account_buddy_search", VxClientProxyJNI.req_account_buddy_search_get());
    public static final vx_request_type req_account_buddy_set = new vx_request_type("req_account_buddy_set", VxClientProxyJNI.req_account_buddy_set_get());
    public static final vx_request_type req_account_buddygroup_delete = new vx_request_type("req_account_buddygroup_delete", VxClientProxyJNI.req_account_buddygroup_delete_get());
    public static final vx_request_type req_account_buddygroup_set = new vx_request_type("req_account_buddygroup_set", VxClientProxyJNI.req_account_buddygroup_set_get());
    public static final vx_request_type req_account_channel_add_acl = new vx_request_type("req_account_channel_add_acl", VxClientProxyJNI.req_account_channel_add_acl_get());
    public static final vx_request_type req_account_channel_add_moderator = new vx_request_type("req_account_channel_add_moderator", VxClientProxyJNI.req_account_channel_add_moderator_get());
    public static final vx_request_type req_account_channel_change_owner = new vx_request_type("req_account_channel_change_owner", VxClientProxyJNI.req_account_channel_change_owner_get());
    public static final vx_request_type req_account_channel_create = new vx_request_type("req_account_channel_create", VxClientProxyJNI.req_account_channel_create_get());
    public static final vx_request_type req_account_channel_delete = new vx_request_type("req_account_channel_delete", VxClientProxyJNI.req_account_channel_delete_get());
    public static final vx_request_type req_account_channel_favorite_delete = new vx_request_type("req_account_channel_favorite_delete", VxClientProxyJNI.req_account_channel_favorite_delete_get());
    public static final vx_request_type req_account_channel_favorite_group_delete = new vx_request_type("req_account_channel_favorite_group_delete", VxClientProxyJNI.req_account_channel_favorite_group_delete_get());
    public static final vx_request_type req_account_channel_favorite_group_set = new vx_request_type("req_account_channel_favorite_group_set", VxClientProxyJNI.req_account_channel_favorite_group_set_get());
    public static final vx_request_type req_account_channel_favorite_set = new vx_request_type("req_account_channel_favorite_set", VxClientProxyJNI.req_account_channel_favorite_set_get());
    public static final vx_request_type req_account_channel_favorites_get_list = new vx_request_type("req_account_channel_favorites_get_list", VxClientProxyJNI.req_account_channel_favorites_get_list_get());
    public static final vx_request_type req_account_channel_get_acl = new vx_request_type("req_account_channel_get_acl", VxClientProxyJNI.req_account_channel_get_acl_get());
    public static final vx_request_type req_account_channel_get_info = new vx_request_type("req_account_channel_get_info", VxClientProxyJNI.req_account_channel_get_info_get());
    public static final vx_request_type req_account_channel_get_moderators = new vx_request_type("req_account_channel_get_moderators", VxClientProxyJNI.req_account_channel_get_moderators_get());
    public static final vx_request_type req_account_channel_get_participants = new vx_request_type("req_account_channel_get_participants", VxClientProxyJNI.req_account_channel_get_participants_get());
    public static final vx_request_type req_account_channel_remove_acl = new vx_request_type("req_account_channel_remove_acl", VxClientProxyJNI.req_account_channel_remove_acl_get());
    public static final vx_request_type req_account_channel_remove_moderator = new vx_request_type("req_account_channel_remove_moderator", VxClientProxyJNI.req_account_channel_remove_moderator_get());
    public static final vx_request_type req_account_channel_search = new vx_request_type("req_account_channel_search", VxClientProxyJNI.req_account_channel_search_get());
    public static final vx_request_type req_account_channel_update = new vx_request_type("req_account_channel_update", VxClientProxyJNI.req_account_channel_update_get());
    public static final vx_request_type req_account_create_auto_accept_rule = new vx_request_type("req_account_create_auto_accept_rule", VxClientProxyJNI.req_account_create_auto_accept_rule_get());
    public static final vx_request_type req_account_create_block_rule = new vx_request_type("req_account_create_block_rule", VxClientProxyJNI.req_account_create_block_rule_get());
    public static final vx_request_type req_account_delete_auto_accept_rule = new vx_request_type("req_account_delete_auto_accept_rule", VxClientProxyJNI.req_account_delete_auto_accept_rule_get());
    public static final vx_request_type req_account_delete_block_rule = new vx_request_type("req_account_delete_block_rule", VxClientProxyJNI.req_account_delete_block_rule_get());
    public static final vx_request_type req_account_get_account = new vx_request_type("req_account_get_account", VxClientProxyJNI.req_account_get_account_get());
    public static final vx_request_type req_account_get_session_fonts = new vx_request_type("req_account_get_session_fonts", VxClientProxyJNI.req_account_get_session_fonts_get());
    public static final vx_request_type req_account_get_template_fonts = new vx_request_type("req_account_get_template_fonts", VxClientProxyJNI.req_account_get_template_fonts_get());
    public static final vx_request_type req_account_list_auto_accept_rules = new vx_request_type("req_account_list_auto_accept_rules", VxClientProxyJNI.req_account_list_auto_accept_rules_get());
    public static final vx_request_type req_account_list_block_rules = new vx_request_type("req_account_list_block_rules", VxClientProxyJNI.req_account_list_block_rules_get());
    public static final vx_request_type req_account_list_buddies_and_groups = new vx_request_type("req_account_list_buddies_and_groups", VxClientProxyJNI.req_account_list_buddies_and_groups_get());
    public static final vx_request_type req_account_login = new vx_request_type("req_account_login", VxClientProxyJNI.req_account_login_get());
    public static final vx_request_type req_account_logout = new vx_request_type("req_account_logout", VxClientProxyJNI.req_account_logout_get());
    public static final vx_request_type req_account_post_crash_dump = new vx_request_type("req_account_post_crash_dump", VxClientProxyJNI.req_account_post_crash_dump_get());
    public static final vx_request_type req_account_send_message = new vx_request_type("req_account_send_message", VxClientProxyJNI.req_account_send_message_get());
    public static final vx_request_type req_account_send_sms = new vx_request_type("req_account_send_sms", VxClientProxyJNI.req_account_send_sms_get());
    public static final vx_request_type req_account_send_subscription_reply = new vx_request_type("req_account_send_subscription_reply", VxClientProxyJNI.req_account_send_subscription_reply_get());
    public static final vx_request_type req_account_send_user_app_data = new vx_request_type("req_account_send_user_app_data", VxClientProxyJNI.req_account_send_user_app_data_get());
    public static final vx_request_type req_account_set_login_properties = new vx_request_type("req_account_set_login_properties", VxClientProxyJNI.req_account_set_login_properties_get());
    public static final vx_request_type req_account_set_presence = new vx_request_type("req_account_set_presence", VxClientProxyJNI.req_account_set_presence_get());
    public static final vx_request_type req_account_update_account = new vx_request_type("req_account_update_account", VxClientProxyJNI.req_account_update_account_get());
    public static final vx_request_type req_account_web_call = new vx_request_type("req_account_web_call", VxClientProxyJNI.req_account_web_call_get());
    public static final vx_request_type req_aux_capture_audio_start = new vx_request_type("req_aux_capture_audio_start", VxClientProxyJNI.req_aux_capture_audio_start_get());
    public static final vx_request_type req_aux_capture_audio_stop = new vx_request_type("req_aux_capture_audio_stop", VxClientProxyJNI.req_aux_capture_audio_stop_get());
    public static final vx_request_type req_aux_connectivity_info = new vx_request_type("req_aux_connectivity_info", VxClientProxyJNI.req_aux_connectivity_info_get());
    public static final vx_request_type req_aux_create_account = new vx_request_type("req_aux_create_account", VxClientProxyJNI.req_aux_create_account_get());
    public static final vx_request_type req_aux_deactivate_account = new vx_request_type("req_aux_deactivate_account", VxClientProxyJNI.req_aux_deactivate_account_get());
    public static final vx_request_type req_aux_diagnostic_state_dump = new vx_request_type("req_aux_diagnostic_state_dump", VxClientProxyJNI.req_aux_diagnostic_state_dump_get());
    public static final vx_request_type req_aux_get_capture_devices = new vx_request_type("req_aux_get_capture_devices", VxClientProxyJNI.req_aux_get_capture_devices_get());
    public static final vx_request_type req_aux_get_mic_level = new vx_request_type("req_aux_get_mic_level", VxClientProxyJNI.req_aux_get_mic_level_get());
    public static final vx_request_type req_aux_get_render_devices = new vx_request_type("req_aux_get_render_devices", VxClientProxyJNI.req_aux_get_render_devices_get());
    public static final vx_request_type req_aux_get_speaker_level = new vx_request_type("req_aux_get_speaker_level", VxClientProxyJNI.req_aux_get_speaker_level_get());
    public static final vx_request_type req_aux_get_vad_properties = new vx_request_type("req_aux_get_vad_properties", VxClientProxyJNI.req_aux_get_vad_properties_get());
    public static final vx_request_type req_aux_global_monitor_keyboard_mouse = new vx_request_type("req_aux_global_monitor_keyboard_mouse", VxClientProxyJNI.req_aux_global_monitor_keyboard_mouse_get());
    public static final vx_request_type req_aux_notify_application_state_change = new vx_request_type("req_aux_notify_application_state_change", VxClientProxyJNI.req_aux_notify_application_state_change_get());
    public static final vx_request_type req_aux_play_audio_buffer = new vx_request_type("req_aux_play_audio_buffer", VxClientProxyJNI.req_aux_play_audio_buffer_get());
    public static final vx_request_type req_aux_reactivate_account = new vx_request_type("req_aux_reactivate_account", VxClientProxyJNI.req_aux_reactivate_account_get());
    public static final vx_request_type req_aux_render_audio_modify = new vx_request_type("req_aux_render_audio_modify", VxClientProxyJNI.req_aux_render_audio_modify_get());
    public static final vx_request_type req_aux_render_audio_start = new vx_request_type("req_aux_render_audio_start", VxClientProxyJNI.req_aux_render_audio_start_get());
    public static final vx_request_type req_aux_render_audio_stop = new vx_request_type("req_aux_render_audio_stop", VxClientProxyJNI.req_aux_render_audio_stop_get());
    public static final vx_request_type req_aux_reset_password = new vx_request_type("req_aux_reset_password", VxClientProxyJNI.req_aux_reset_password_get());
    public static final vx_request_type req_aux_set_capture_device = new vx_request_type("req_aux_set_capture_device", VxClientProxyJNI.req_aux_set_capture_device_get());
    public static final vx_request_type req_aux_set_idle_timeout = new vx_request_type("req_aux_set_idle_timeout", VxClientProxyJNI.req_aux_set_idle_timeout_get());
    public static final vx_request_type req_aux_set_mic_level = new vx_request_type("req_aux_set_mic_level", VxClientProxyJNI.req_aux_set_mic_level_get());
    public static final vx_request_type req_aux_set_render_device = new vx_request_type("req_aux_set_render_device", VxClientProxyJNI.req_aux_set_render_device_get());
    public static final vx_request_type req_aux_set_speaker_level = new vx_request_type("req_aux_set_speaker_level", VxClientProxyJNI.req_aux_set_speaker_level_get());
    public static final vx_request_type req_aux_set_vad_properties = new vx_request_type("req_aux_set_vad_properties", VxClientProxyJNI.req_aux_set_vad_properties_get());
    public static final vx_request_type req_aux_start_buffer_capture = new vx_request_type("req_aux_start_buffer_capture", VxClientProxyJNI.req_aux_start_buffer_capture_get());
    public static final vx_request_type req_channel_ban_user = new vx_request_type("req_channel_ban_user", VxClientProxyJNI.req_channel_ban_user_get());
    public static final vx_request_type req_channel_get_banned_users = new vx_request_type("req_channel_get_banned_users", VxClientProxyJNI.req_channel_get_banned_users_get());
    public static final vx_request_type req_channel_kick_user = new vx_request_type("req_channel_kick_user", VxClientProxyJNI.req_channel_kick_user_get());
    public static final vx_request_type req_channel_mute_all_users = new vx_request_type("req_channel_mute_all_users", VxClientProxyJNI.req_channel_mute_all_users_get());
    public static final vx_request_type req_channel_mute_user = new vx_request_type("req_channel_mute_user", VxClientProxyJNI.req_channel_mute_user_get());
    public static final vx_request_type req_channel_set_lock_mode = new vx_request_type("req_channel_set_lock_mode", VxClientProxyJNI.req_channel_set_lock_mode_get());
    public static final vx_request_type req_connector_create = new vx_request_type("req_connector_create", VxClientProxyJNI.req_connector_create_get());
    public static final vx_request_type req_connector_get_local_audio_info = new vx_request_type("req_connector_get_local_audio_info", VxClientProxyJNI.req_connector_get_local_audio_info_get());
    public static final vx_request_type req_connector_initiate_shutdown = new vx_request_type("req_connector_initiate_shutdown", VxClientProxyJNI.req_connector_initiate_shutdown_get());
    public static final vx_request_type req_connector_mute_local_mic = new vx_request_type("req_connector_mute_local_mic", VxClientProxyJNI.req_connector_mute_local_mic_get());
    public static final vx_request_type req_connector_mute_local_speaker = new vx_request_type("req_connector_mute_local_speaker", VxClientProxyJNI.req_connector_mute_local_speaker_get());
    public static final vx_request_type req_connector_set_local_mic_volume = new vx_request_type("req_connector_set_local_mic_volume", VxClientProxyJNI.req_connector_set_local_mic_volume_get());
    public static final vx_request_type req_connector_set_local_speaker_volume = new vx_request_type("req_connector_set_local_speaker_volume", VxClientProxyJNI.req_connector_set_local_speaker_volume_get());
    public static final vx_request_type req_max = new vx_request_type("req_max", VxClientProxyJNI.req_max_get());
    public static final vx_request_type req_none = new vx_request_type("req_none", VxClientProxyJNI.req_none_get());
    public static final vx_request_type req_session_channel_invite_user = new vx_request_type("req_session_channel_invite_user", VxClientProxyJNI.req_session_channel_invite_user_get());
    public static final vx_request_type req_session_create = new vx_request_type("req_session_create", VxClientProxyJNI.req_session_create_get());
    public static final vx_request_type req_session_media_connect = new vx_request_type("req_session_media_connect", VxClientProxyJNI.req_session_media_connect_get());
    public static final vx_request_type req_session_media_disconnect = new vx_request_type("req_session_media_disconnect", VxClientProxyJNI.req_session_media_disconnect_get());
    public static final vx_request_type req_session_mute_local_speaker = new vx_request_type("req_session_mute_local_speaker", VxClientProxyJNI.req_session_mute_local_speaker_get());
    public static final vx_request_type req_session_send_dtmf = new vx_request_type("req_session_send_dtmf", VxClientProxyJNI.req_session_send_dtmf_get());
    public static final vx_request_type req_session_send_message = new vx_request_type("req_session_send_message", VxClientProxyJNI.req_session_send_message_get());
    public static final vx_request_type req_session_send_notification = new vx_request_type("req_session_send_notification", VxClientProxyJNI.req_session_send_notification_get());
    public static final vx_request_type req_session_set_3d_position = new vx_request_type("req_session_set_3d_position", VxClientProxyJNI.req_session_set_3d_position_get());
    public static final vx_request_type req_session_set_local_speaker_volume = new vx_request_type("req_session_set_local_speaker_volume", VxClientProxyJNI.req_session_set_local_speaker_volume_get());
    public static final vx_request_type req_session_set_participant_mute_for_me = new vx_request_type("req_session_set_participant_mute_for_me", VxClientProxyJNI.req_session_set_participant_mute_for_me_get());
    public static final vx_request_type req_session_set_participant_volume_for_me = new vx_request_type("req_session_set_participant_volume_for_me", VxClientProxyJNI.req_session_set_participant_volume_for_me_get());
    public static final vx_request_type req_session_set_voice_font = new vx_request_type("req_session_set_voice_font", VxClientProxyJNI.req_session_set_voice_font_get());
    public static final vx_request_type req_session_terminate = new vx_request_type("req_session_terminate", VxClientProxyJNI.req_session_terminate_get());
    public static final vx_request_type req_session_text_connect = new vx_request_type("req_session_text_connect", VxClientProxyJNI.req_session_text_connect_get());
    public static final vx_request_type req_session_text_disconnect = new vx_request_type("req_session_text_disconnect", VxClientProxyJNI.req_session_text_disconnect_get());
    public static final vx_request_type req_sessiongroup_add_session = new vx_request_type("req_sessiongroup_add_session", VxClientProxyJNI.req_sessiongroup_add_session_get());
    public static final vx_request_type req_sessiongroup_control_audio_injection = new vx_request_type("req_sessiongroup_control_audio_injection", VxClientProxyJNI.req_sessiongroup_control_audio_injection_get());
    public static final vx_request_type req_sessiongroup_control_playback = new vx_request_type("req_sessiongroup_control_playback", VxClientProxyJNI.req_sessiongroup_control_playback_get());
    public static final vx_request_type req_sessiongroup_control_recording = new vx_request_type("req_sessiongroup_control_recording", VxClientProxyJNI.req_sessiongroup_control_recording_get());
    public static final vx_request_type req_sessiongroup_create = new vx_request_type("req_sessiongroup_create", VxClientProxyJNI.req_sessiongroup_create_get());
    public static final vx_request_type req_sessiongroup_get_stats = new vx_request_type("req_sessiongroup_get_stats", VxClientProxyJNI.req_sessiongroup_get_stats_get());
    public static final vx_request_type req_sessiongroup_remove_session = new vx_request_type("req_sessiongroup_remove_session", VxClientProxyJNI.req_sessiongroup_remove_session_get());
    public static final vx_request_type req_sessiongroup_reset_focus = new vx_request_type("req_sessiongroup_reset_focus", VxClientProxyJNI.req_sessiongroup_reset_focus_get());
    public static final vx_request_type req_sessiongroup_set_focus = new vx_request_type("req_sessiongroup_set_focus", VxClientProxyJNI.req_sessiongroup_set_focus_get());
    public static final vx_request_type req_sessiongroup_set_playback_options = new vx_request_type("req_sessiongroup_set_playback_options", VxClientProxyJNI.req_sessiongroup_set_playback_options_get());
    public static final vx_request_type req_sessiongroup_set_session_3d_position = new vx_request_type("req_sessiongroup_set_session_3d_position", VxClientProxyJNI.req_sessiongroup_set_session_3d_position_get());
    public static final vx_request_type req_sessiongroup_set_tx_all_sessions = new vx_request_type("req_sessiongroup_set_tx_all_sessions", VxClientProxyJNI.req_sessiongroup_set_tx_all_sessions_get());
    public static final vx_request_type req_sessiongroup_set_tx_no_session = new vx_request_type("req_sessiongroup_set_tx_no_session", VxClientProxyJNI.req_sessiongroup_set_tx_no_session_get());
    public static final vx_request_type req_sessiongroup_set_tx_session = new vx_request_type("req_sessiongroup_set_tx_session", VxClientProxyJNI.req_sessiongroup_set_tx_session_get());
    public static final vx_request_type req_sessiongroup_terminate = new vx_request_type("req_sessiongroup_terminate", VxClientProxyJNI.req_sessiongroup_terminate_get());
    public static final vx_request_type req_sessiongroup_unset_focus = new vx_request_type("req_sessiongroup_unset_focus", VxClientProxyJNI.req_sessiongroup_unset_focus_get());
    private static int swigNext = 0;
    private static vx_request_type[] swigValues = new vx_request_type[]{req_none, req_connector_create, req_connector_initiate_shutdown, req_account_login, req_account_logout, req_account_set_login_properties, req_sessiongroup_create, req_sessiongroup_terminate, req_sessiongroup_add_session, req_sessiongroup_remove_session, req_sessiongroup_set_focus, req_sessiongroup_unset_focus, req_sessiongroup_reset_focus, req_sessiongroup_set_tx_session, req_sessiongroup_set_tx_all_sessions, req_sessiongroup_set_tx_no_session, req_session_create, req_session_media_connect, req_session_media_disconnect, req_session_terminate, req_session_mute_local_speaker, req_session_set_local_speaker_volume, req_session_channel_invite_user, req_session_set_participant_volume_for_me, req_session_set_participant_mute_for_me, req_session_set_3d_position, req_session_set_voice_font, req_account_channel_create, req_account_channel_update, req_account_channel_delete, req_account_channel_favorites_get_list, req_account_channel_favorite_set, req_account_channel_favorite_delete, req_account_channel_favorite_group_set, req_account_channel_favorite_group_delete, req_account_channel_get_info, req_account_channel_search, req_account_buddy_search, req_account_channel_add_moderator, req_account_channel_remove_moderator, req_account_channel_get_moderators, req_account_channel_add_acl, req_account_channel_remove_acl, req_account_channel_get_acl, req_channel_mute_user, req_channel_ban_user, req_channel_get_banned_users, req_channel_kick_user, req_channel_mute_all_users, req_connector_mute_local_mic, req_connector_mute_local_speaker, req_connector_set_local_mic_volume, req_connector_set_local_speaker_volume, req_connector_get_local_audio_info, req_account_buddy_set, req_account_buddy_delete, req_account_buddygroup_set, req_account_buddygroup_delete, req_account_list_buddies_and_groups, req_session_send_message, req_account_set_presence, req_account_send_subscription_reply, req_session_send_notification, req_account_create_block_rule, req_account_delete_block_rule, req_account_list_block_rules, req_account_create_auto_accept_rule, req_account_delete_auto_accept_rule, req_account_list_auto_accept_rules, req_account_update_account, req_account_get_account, req_account_send_sms, req_aux_connectivity_info, req_aux_get_render_devices, req_aux_get_capture_devices, req_aux_set_render_device, req_aux_set_capture_device, req_aux_get_mic_level, req_aux_get_speaker_level, req_aux_set_mic_level, req_aux_set_speaker_level, req_aux_render_audio_start, req_aux_render_audio_stop, req_aux_capture_audio_start, req_aux_capture_audio_stop, req_aux_global_monitor_keyboard_mouse, req_aux_set_idle_timeout, req_aux_create_account, req_aux_reactivate_account, req_aux_deactivate_account, req_account_post_crash_dump, req_aux_reset_password, req_sessiongroup_set_session_3d_position, req_account_get_session_fonts, req_account_get_template_fonts, req_aux_start_buffer_capture, req_aux_play_audio_buffer, req_sessiongroup_control_recording, req_sessiongroup_control_playback, req_sessiongroup_set_playback_options, req_session_text_connect, req_session_text_disconnect, req_channel_set_lock_mode, req_aux_render_audio_modify, req_session_send_dtmf, req_aux_set_vad_properties, req_aux_get_vad_properties, req_sessiongroup_control_audio_injection, req_account_channel_change_owner, req_account_channel_get_participants, req_account_send_user_app_data, req_aux_diagnostic_state_dump, req_account_web_call, req_account_anonymous_login, req_account_authtoken_login, req_sessiongroup_get_stats, req_account_send_message, req_aux_notify_application_state_change, req_max};
    private final String swigName;
    private final int swigValue;

    private vx_request_type(String string2) {
        this.swigName = string2;
        int n = swigNext;
        swigNext = n + 1;
        this.swigValue = n;
    }

    private vx_request_type(String string2, int n) {
        this.swigName = string2;
        this.swigValue = n;
        swigNext = n + 1;
    }

    private vx_request_type(String string2, vx_request_type vx_request_type2) {
        this.swigName = string2;
        this.swigValue = vx_request_type2.swigValue;
        swigNext = this.swigValue + 1;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static vx_request_type swigToEnum(int n) {
        if (n < swigValues.length && n >= 0 && vx_request_type.swigValues[n].swigValue == n) {
            return swigValues[n];
        }
        int n2 = 0;
        while (n2 < swigValues.length) {
            if (vx_request_type.swigValues[n2].swigValue == n) {
                return swigValues[n2];
            }
            ++n2;
        }
        throw new IllegalArgumentException("No enum " + vx_request_type.class + " with value " + n);
    }

    public final int swigValue() {
        return this.swigValue;
    }

    public String toString() {
        return this.swigName;
    }
}

