/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.ND_TEST_TYPE
import com.vivox.service.SWIGTYPE_p_double
import com.vivox.service.SWIGTYPE_p_f_p_q_const__char_p_q_const__char_p_q_const__char__void
import com.vivox.service.SWIGTYPE_p_f_p_void__void
import com.vivox.service.SWIGTYPE_p_f_p_void_int_int__void
import com.vivox.service.SWIGTYPE_p_int
import com.vivox.service.SWIGTYPE_p_p_char
import com.vivox.service.SWIGTYPE_p_p_p_char
import com.vivox.service.SWIGTYPE_p_p_p_vx_auto_accept_rule
import com.vivox.service.SWIGTYPE_p_p_p_vx_block_rule
import com.vivox.service.SWIGTYPE_p_p_p_vx_buddy
import com.vivox.service.SWIGTYPE_p_p_p_vx_channel
import com.vivox.service.SWIGTYPE_p_p_p_vx_channel_favorite
import com.vivox.service.SWIGTYPE_p_p_p_vx_channel_favorite_group
import com.vivox.service.SWIGTYPE_p_p_p_vx_connectivity_test_result
import com.vivox.service.SWIGTYPE_p_p_p_vx_device
import com.vivox.service.SWIGTYPE_p_p_p_vx_group
import com.vivox.service.SWIGTYPE_p_p_p_vx_name_value_pair
import com.vivox.service.SWIGTYPE_p_p_p_vx_participant
import com.vivox.service.SWIGTYPE_p_p_p_vx_recording_frame
import com.vivox.service.SWIGTYPE_p_p_p_vx_state_account
import com.vivox.service.SWIGTYPE_p_p_p_vx_state_buddy
import com.vivox.service.SWIGTYPE_p_p_p_vx_state_buddy_contact
import com.vivox.service.SWIGTYPE_p_p_p_vx_state_buddy_group
import com.vivox.service.SWIGTYPE_p_p_p_vx_state_connector
import com.vivox.service.SWIGTYPE_p_p_p_vx_state_participant
import com.vivox.service.SWIGTYPE_p_p_p_vx_state_session
import com.vivox.service.SWIGTYPE_p_p_p_vx_state_sessiongroup
import com.vivox.service.SWIGTYPE_p_p_p_vx_user_channel
import com.vivox.service.SWIGTYPE_p_p_p_vx_voice_font
import com.vivox.service.SWIGTYPE_p_p_void
import com.vivox.service.SWIGTYPE_p_p_vx_account
import com.vivox.service.SWIGTYPE_p_p_vx_auto_accept_rule
import com.vivox.service.SWIGTYPE_p_p_vx_block_rule
import com.vivox.service.SWIGTYPE_p_p_vx_buddy
import com.vivox.service.SWIGTYPE_p_p_vx_channel
import com.vivox.service.SWIGTYPE_p_p_vx_channel_favorite
import com.vivox.service.SWIGTYPE_p_p_vx_channel_favorite_group
import com.vivox.service.SWIGTYPE_p_p_vx_connectivity_test_result
import com.vivox.service.SWIGTYPE_p_p_vx_device
import com.vivox.service.SWIGTYPE_p_p_vx_group
import com.vivox.service.SWIGTYPE_p_p_vx_message_base
import com.vivox.service.SWIGTYPE_p_p_vx_name_value_pair
import com.vivox.service.SWIGTYPE_p_p_vx_participant
import com.vivox.service.SWIGTYPE_p_p_vx_recording_frame
import com.vivox.service.SWIGTYPE_p_p_vx_req_account_anonymous_login
import com.vivox.service.SWIGTYPE_p_p_vx_req_account_authtoken_login
import com.vivox.service.SWIGTYPE_p_p_vx_req_account_buddy_delete
import com.vivox.service.SWIGTYPE_p_p_vx_req_account_buddy_search
import com.vivox.service.SWIGTYPE_p_p_vx_req_account_buddy_set
import com.vivox.service.SWIGTYPE_p_p_vx_req_account_buddygroup_delete
import com.vivox.service.SWIGTYPE_p_p_vx_req_account_buddygroup_set
import com.vivox.service.SWIGTYPE_p_p_vx_req_account_channel_add_acl
import com.vivox.service.SWIGTYPE_p_p_vx_req_account_channel_add_moderator
import com.vivox.service.SWIGTYPE_p_p_vx_req_account_channel_change_owner
import com.vivox.service.SWIGTYPE_p_p_vx_req_account_channel_create
import com.vivox.service.SWIGTYPE_p_p_vx_req_account_channel_delete
import com.vivox.service.SWIGTYPE_p_p_vx_req_account_channel_favorite_delete
import com.vivox.service.SWIGTYPE_p_p_vx_req_account_channel_favorite_group_delete
import com.vivox.service.SWIGTYPE_p_p_vx_req_account_channel_favorite_group_set
import com.vivox.service.SWIGTYPE_p_p_vx_req_account_channel_favorite_set
import com.vivox.service.SWIGTYPE_p_p_vx_req_account_channel_favorites_get_list
import com.vivox.service.SWIGTYPE_p_p_vx_req_account_channel_get_acl
import com.vivox.service.SWIGTYPE_p_p_vx_req_account_channel_get_info
import com.vivox.service.SWIGTYPE_p_p_vx_req_account_channel_get_moderators
import com.vivox.service.SWIGTYPE_p_p_vx_req_account_channel_get_participants
import com.vivox.service.SWIGTYPE_p_p_vx_req_account_channel_remove_acl
import com.vivox.service.SWIGTYPE_p_p_vx_req_account_channel_remove_moderator
import com.vivox.service.SWIGTYPE_p_p_vx_req_account_channel_search
import com.vivox.service.SWIGTYPE_p_p_vx_req_account_channel_update
import com.vivox.service.SWIGTYPE_p_p_vx_req_account_create_auto_accept_rule
import com.vivox.service.SWIGTYPE_p_p_vx_req_account_create_block_rule
import com.vivox.service.SWIGTYPE_p_p_vx_req_account_delete_auto_accept_rule
import com.vivox.service.SWIGTYPE_p_p_vx_req_account_delete_block_rule
import com.vivox.service.SWIGTYPE_p_p_vx_req_account_get_account
import com.vivox.service.SWIGTYPE_p_p_vx_req_account_get_session_fonts
import com.vivox.service.SWIGTYPE_p_p_vx_req_account_get_template_fonts
import com.vivox.service.SWIGTYPE_p_p_vx_req_account_list_auto_accept_rules
import com.vivox.service.SWIGTYPE_p_p_vx_req_account_list_block_rules
import com.vivox.service.SWIGTYPE_p_p_vx_req_account_list_buddies_and_groups
import com.vivox.service.SWIGTYPE_p_p_vx_req_account_login
import com.vivox.service.SWIGTYPE_p_p_vx_req_account_logout
import com.vivox.service.SWIGTYPE_p_p_vx_req_account_post_crash_dump
import com.vivox.service.SWIGTYPE_p_p_vx_req_account_send_message
import com.vivox.service.SWIGTYPE_p_p_vx_req_account_send_sms
import com.vivox.service.SWIGTYPE_p_p_vx_req_account_send_subscription_reply
import com.vivox.service.SWIGTYPE_p_p_vx_req_account_send_user_app_data
import com.vivox.service.SWIGTYPE_p_p_vx_req_account_set_login_properties
import com.vivox.service.SWIGTYPE_p_p_vx_req_account_set_presence
import com.vivox.service.SWIGTYPE_p_p_vx_req_account_update_account
import com.vivox.service.SWIGTYPE_p_p_vx_req_account_web_call
import com.vivox.service.SWIGTYPE_p_p_vx_req_aux_capture_audio_start
import com.vivox.service.SWIGTYPE_p_p_vx_req_aux_capture_audio_stop
import com.vivox.service.SWIGTYPE_p_p_vx_req_aux_connectivity_info
import com.vivox.service.SWIGTYPE_p_p_vx_req_aux_create_account
import com.vivox.service.SWIGTYPE_p_p_vx_req_aux_deactivate_account
import com.vivox.service.SWIGTYPE_p_p_vx_req_aux_diagnostic_state_dump
import com.vivox.service.SWIGTYPE_p_p_vx_req_aux_get_capture_devices
import com.vivox.service.SWIGTYPE_p_p_vx_req_aux_get_mic_level
import com.vivox.service.SWIGTYPE_p_p_vx_req_aux_get_render_devices
import com.vivox.service.SWIGTYPE_p_p_vx_req_aux_get_speaker_level
import com.vivox.service.SWIGTYPE_p_p_vx_req_aux_get_vad_properties
import com.vivox.service.SWIGTYPE_p_p_vx_req_aux_global_monitor_keyboard_mouse
import com.vivox.service.SWIGTYPE_p_p_vx_req_aux_notify_application_state_change
import com.vivox.service.SWIGTYPE_p_p_vx_req_aux_play_audio_buffer
import com.vivox.service.SWIGTYPE_p_p_vx_req_aux_reactivate_account
import com.vivox.service.SWIGTYPE_p_p_vx_req_aux_render_audio_modify
import com.vivox.service.SWIGTYPE_p_p_vx_req_aux_render_audio_start
import com.vivox.service.SWIGTYPE_p_p_vx_req_aux_render_audio_stop
import com.vivox.service.SWIGTYPE_p_p_vx_req_aux_reset_password
import com.vivox.service.SWIGTYPE_p_p_vx_req_aux_set_capture_device
import com.vivox.service.SWIGTYPE_p_p_vx_req_aux_set_idle_timeout
import com.vivox.service.SWIGTYPE_p_p_vx_req_aux_set_mic_level
import com.vivox.service.SWIGTYPE_p_p_vx_req_aux_set_render_device
import com.vivox.service.SWIGTYPE_p_p_vx_req_aux_set_speaker_level
import com.vivox.service.SWIGTYPE_p_p_vx_req_aux_set_vad_properties
import com.vivox.service.SWIGTYPE_p_p_vx_req_aux_start_buffer_capture
import com.vivox.service.SWIGTYPE_p_p_vx_req_channel_ban_user
import com.vivox.service.SWIGTYPE_p_p_vx_req_channel_get_banned_users
import com.vivox.service.SWIGTYPE_p_p_vx_req_channel_kick_user
import com.vivox.service.SWIGTYPE_p_p_vx_req_channel_mute_all_users
import com.vivox.service.SWIGTYPE_p_p_vx_req_channel_mute_user
import com.vivox.service.SWIGTYPE_p_p_vx_req_channel_set_lock_mode
import com.vivox.service.SWIGTYPE_p_p_vx_req_connector_create
import com.vivox.service.SWIGTYPE_p_p_vx_req_connector_get_local_audio_info
import com.vivox.service.SWIGTYPE_p_p_vx_req_connector_initiate_shutdown
import com.vivox.service.SWIGTYPE_p_p_vx_req_connector_mute_local_mic
import com.vivox.service.SWIGTYPE_p_p_vx_req_connector_mute_local_speaker
import com.vivox.service.SWIGTYPE_p_p_vx_req_connector_set_local_mic_volume
import com.vivox.service.SWIGTYPE_p_p_vx_req_connector_set_local_speaker_volume
import com.vivox.service.SWIGTYPE_p_p_vx_req_session_channel_invite_user
import com.vivox.service.SWIGTYPE_p_p_vx_req_session_create
import com.vivox.service.SWIGTYPE_p_p_vx_req_session_media_connect
import com.vivox.service.SWIGTYPE_p_p_vx_req_session_media_disconnect
import com.vivox.service.SWIGTYPE_p_p_vx_req_session_mute_local_speaker
import com.vivox.service.SWIGTYPE_p_p_vx_req_session_send_dtmf
import com.vivox.service.SWIGTYPE_p_p_vx_req_session_send_message
import com.vivox.service.SWIGTYPE_p_p_vx_req_session_send_notification
import com.vivox.service.SWIGTYPE_p_p_vx_req_session_set_3d_position
import com.vivox.service.SWIGTYPE_p_p_vx_req_session_set_local_speaker_volume
import com.vivox.service.SWIGTYPE_p_p_vx_req_session_set_participant_mute_for_me
import com.vivox.service.SWIGTYPE_p_p_vx_req_session_set_participant_volume_for_me
import com.vivox.service.SWIGTYPE_p_p_vx_req_session_set_voice_font
import com.vivox.service.SWIGTYPE_p_p_vx_req_session_terminate
import com.vivox.service.SWIGTYPE_p_p_vx_req_session_text_connect
import com.vivox.service.SWIGTYPE_p_p_vx_req_session_text_disconnect
import com.vivox.service.SWIGTYPE_p_p_vx_req_sessiongroup_add_session
import com.vivox.service.SWIGTYPE_p_p_vx_req_sessiongroup_control_audio_injection
import com.vivox.service.SWIGTYPE_p_p_vx_req_sessiongroup_control_playback
import com.vivox.service.SWIGTYPE_p_p_vx_req_sessiongroup_control_recording
import com.vivox.service.SWIGTYPE_p_p_vx_req_sessiongroup_create
import com.vivox.service.SWIGTYPE_p_p_vx_req_sessiongroup_get_stats
import com.vivox.service.SWIGTYPE_p_p_vx_req_sessiongroup_remove_session
import com.vivox.service.SWIGTYPE_p_p_vx_req_sessiongroup_reset_focus
import com.vivox.service.SWIGTYPE_p_p_vx_req_sessiongroup_set_focus
import com.vivox.service.SWIGTYPE_p_p_vx_req_sessiongroup_set_playback_options
import com.vivox.service.SWIGTYPE_p_p_vx_req_sessiongroup_set_session_3d_position
import com.vivox.service.SWIGTYPE_p_p_vx_req_sessiongroup_set_tx_all_sessions
import com.vivox.service.SWIGTYPE_p_p_vx_req_sessiongroup_set_tx_no_session
import com.vivox.service.SWIGTYPE_p_p_vx_req_sessiongroup_set_tx_session
import com.vivox.service.SWIGTYPE_p_p_vx_req_sessiongroup_terminate
import com.vivox.service.SWIGTYPE_p_p_vx_req_sessiongroup_unset_focus
import com.vivox.service.SWIGTYPE_p_p_vx_state_account
import com.vivox.service.SWIGTYPE_p_p_vx_state_buddy
import com.vivox.service.SWIGTYPE_p_p_vx_state_buddy_contact
import com.vivox.service.SWIGTYPE_p_p_vx_state_buddy_group
import com.vivox.service.SWIGTYPE_p_p_vx_state_connector
import com.vivox.service.SWIGTYPE_p_p_vx_state_participant
import com.vivox.service.SWIGTYPE_p_p_vx_state_session
import com.vivox.service.SWIGTYPE_p_p_vx_state_sessiongroup
import com.vivox.service.SWIGTYPE_p_p_vx_user_channel
import com.vivox.service.SWIGTYPE_p_p_vx_voice_font
import com.vivox.service.SWIGTYPE_p_sdk_string
import com.vivox.service.SWIGTYPE_p_time_t
import com.vivox.service.SWIGTYPE_p_unsigned_int
import com.vivox.service.SWIGTYPE_p_void
import com.vivox.service.SWIGTYPE_p_vx_recording_frame_type_t
import com.vivox.service.VxClientProxyConstants
import com.vivox.service.VxClientProxyJNI
import com.vivox.service.vx_account_t
import com.vivox.service.vx_auto_accept_rule_t
import com.vivox.service.vx_block_rule_t
import com.vivox.service.vx_buddy_t
import com.vivox.service.vx_channel_favorite_group_t
import com.vivox.service.vx_channel_favorite_t
import com.vivox.service.vx_channel_t
import com.vivox.service.vx_connectivity_test_result_t
import com.vivox.service.vx_device_t
import com.vivox.service.vx_event_type
import com.vivox.service.vx_evt_account_login_state_change_t
import com.vivox.service.vx_evt_aux_audio_properties_t
import com.vivox.service.vx_evt_base_t
import com.vivox.service.vx_evt_buddy_and_group_list_changed_t
import com.vivox.service.vx_evt_buddy_changed_t
import com.vivox.service.vx_evt_buddy_group_changed_t
import com.vivox.service.vx_evt_buddy_presence_t
import com.vivox.service.vx_evt_idle_state_changed_t
import com.vivox.service.vx_evt_keyboard_mouse_t
import com.vivox.service.vx_evt_media_completion_t
import com.vivox.service.vx_evt_media_stream_updated_t
import com.vivox.service.vx_evt_message_t
import com.vivox.service.vx_evt_network_message_t
import com.vivox.service.vx_evt_participant_added_t
import com.vivox.service.vx_evt_participant_removed_t
import com.vivox.service.vx_evt_participant_updated_t
import com.vivox.service.vx_evt_server_app_data_t
import com.vivox.service.vx_evt_session_added_t
import com.vivox.service.vx_evt_session_notification_t
import com.vivox.service.vx_evt_session_removed_t
import com.vivox.service.vx_evt_session_updated_t
import com.vivox.service.vx_evt_sessiongroup_added_t
import com.vivox.service.vx_evt_sessiongroup_playback_frame_played_t
import com.vivox.service.vx_evt_sessiongroup_removed_t
import com.vivox.service.vx_evt_sessiongroup_updated_t
import com.vivox.service.vx_evt_subscription_t
import com.vivox.service.vx_evt_text_stream_updated_t
import com.vivox.service.vx_evt_user_app_data_t
import com.vivox.service.vx_evt_voice_service_connection_state_changed_t
import com.vivox.service.vx_group_t
import com.vivox.service.vx_log_level
import com.vivox.service.vx_log_type
import com.vivox.service.vx_message_base_t
import com.vivox.service.vx_message_type
import com.vivox.service.vx_name_value_pair_t
import com.vivox.service.vx_participant_diagnostic_state_t
import com.vivox.service.vx_participant_t
import com.vivox.service.vx_recording_frame_t
import com.vivox.service.vx_recording_frame_type_t
import com.vivox.service.vx_req_account_anonymous_login_t
import com.vivox.service.vx_req_account_authtoken_login_t
import com.vivox.service.vx_req_account_buddy_delete_t
import com.vivox.service.vx_req_account_buddy_search_t
import com.vivox.service.vx_req_account_buddy_set_t
import com.vivox.service.vx_req_account_buddygroup_delete_t
import com.vivox.service.vx_req_account_buddygroup_set_t
import com.vivox.service.vx_req_account_channel_add_acl_t
import com.vivox.service.vx_req_account_channel_add_moderator_t
import com.vivox.service.vx_req_account_channel_change_owner_t
import com.vivox.service.vx_req_account_channel_create_t
import com.vivox.service.vx_req_account_channel_delete_t
import com.vivox.service.vx_req_account_channel_favorite_delete_t
import com.vivox.service.vx_req_account_channel_favorite_group_delete_t
import com.vivox.service.vx_req_account_channel_favorite_group_set_t
import com.vivox.service.vx_req_account_channel_favorite_set_t
import com.vivox.service.vx_req_account_channel_favorites_get_list_t
import com.vivox.service.vx_req_account_channel_get_acl_t
import com.vivox.service.vx_req_account_channel_get_info_t
import com.vivox.service.vx_req_account_channel_get_moderators_t
import com.vivox.service.vx_req_account_channel_get_participants_t
import com.vivox.service.vx_req_account_channel_remove_acl_t
import com.vivox.service.vx_req_account_channel_remove_moderator_t
import com.vivox.service.vx_req_account_channel_search_t
import com.vivox.service.vx_req_account_channel_update_t
import com.vivox.service.vx_req_account_create_auto_accept_rule_t
import com.vivox.service.vx_req_account_create_block_rule_t
import com.vivox.service.vx_req_account_delete_auto_accept_rule_t
import com.vivox.service.vx_req_account_delete_block_rule_t
import com.vivox.service.vx_req_account_get_account_t
import com.vivox.service.vx_req_account_get_session_fonts_t
import com.vivox.service.vx_req_account_get_template_fonts_t
import com.vivox.service.vx_req_account_list_auto_accept_rules_t
import com.vivox.service.vx_req_account_list_block_rules_t
import com.vivox.service.vx_req_account_list_buddies_and_groups_t
import com.vivox.service.vx_req_account_login_t
import com.vivox.service.vx_req_account_logout_t
import com.vivox.service.vx_req_account_post_crash_dump_t
import com.vivox.service.vx_req_account_send_message_t
import com.vivox.service.vx_req_account_send_sms_t
import com.vivox.service.vx_req_account_send_subscription_reply_t
import com.vivox.service.vx_req_account_send_user_app_data_t
import com.vivox.service.vx_req_account_set_login_properties_t
import com.vivox.service.vx_req_account_set_presence_t
import com.vivox.service.vx_req_account_update_account_t
import com.vivox.service.vx_req_account_web_call_t
import com.vivox.service.vx_req_aux_capture_audio_start_t
import com.vivox.service.vx_req_aux_capture_audio_stop_t
import com.vivox.service.vx_req_aux_connectivity_info_t
import com.vivox.service.vx_req_aux_create_account_t
import com.vivox.service.vx_req_aux_deactivate_account_t
import com.vivox.service.vx_req_aux_get_capture_devices_t
import com.vivox.service.vx_req_aux_get_mic_level_t
import com.vivox.service.vx_req_aux_get_render_devices_t
import com.vivox.service.vx_req_aux_get_speaker_level_t
import com.vivox.service.vx_req_aux_get_vad_properties_t
import com.vivox.service.vx_req_aux_global_monitor_keyboard_mouse_t
import com.vivox.service.vx_req_aux_play_audio_buffer_t
import com.vivox.service.vx_req_aux_reactivate_account_t
import com.vivox.service.vx_req_aux_render_audio_modify_t
import com.vivox.service.vx_req_aux_render_audio_start_t
import com.vivox.service.vx_req_aux_render_audio_stop_t
import com.vivox.service.vx_req_aux_reset_password_t
import com.vivox.service.vx_req_aux_set_capture_device_t
import com.vivox.service.vx_req_aux_set_idle_timeout_t
import com.vivox.service.vx_req_aux_set_mic_level_t
import com.vivox.service.vx_req_aux_set_render_device_t
import com.vivox.service.vx_req_aux_set_speaker_level_t
import com.vivox.service.vx_req_aux_set_vad_properties_t
import com.vivox.service.vx_req_aux_start_buffer_capture_t
import com.vivox.service.vx_req_base_t
import com.vivox.service.vx_req_channel_ban_user_t
import com.vivox.service.vx_req_channel_get_banned_users_t
import com.vivox.service.vx_req_channel_kick_user_t
import com.vivox.service.vx_req_channel_mute_all_users_t
import com.vivox.service.vx_req_channel_mute_user_t
import com.vivox.service.vx_req_channel_set_lock_mode_t
import com.vivox.service.vx_req_connector_create_t
import com.vivox.service.vx_req_connector_get_local_audio_info_t
import com.vivox.service.vx_req_connector_initiate_shutdown_t
import com.vivox.service.vx_req_connector_mute_local_mic_t
import com.vivox.service.vx_req_connector_mute_local_speaker_t
import com.vivox.service.vx_req_connector_set_local_mic_volume_t
import com.vivox.service.vx_req_connector_set_local_speaker_volume_t
import com.vivox.service.vx_req_session_channel_invite_user_t
import com.vivox.service.vx_req_session_create_t
import com.vivox.service.vx_req_session_media_connect_t
import com.vivox.service.vx_req_session_media_disconnect_t
import com.vivox.service.vx_req_session_mute_local_speaker_t
import com.vivox.service.vx_req_session_send_dtmf_t
import com.vivox.service.vx_req_session_send_message_t
import com.vivox.service.vx_req_session_send_notification_t
import com.vivox.service.vx_req_session_set_3d_position_t
import com.vivox.service.vx_req_session_set_local_speaker_volume_t
import com.vivox.service.vx_req_session_set_participant_mute_for_me_t
import com.vivox.service.vx_req_session_set_participant_volume_for_me_t
import com.vivox.service.vx_req_session_set_voice_font_t
import com.vivox.service.vx_req_session_terminate_t
import com.vivox.service.vx_req_session_text_connect_t
import com.vivox.service.vx_req_session_text_disconnect_t
import com.vivox.service.vx_req_sessiongroup_add_session_t
import com.vivox.service.vx_req_sessiongroup_control_audio_injection_t
import com.vivox.service.vx_req_sessiongroup_control_playback_t
import com.vivox.service.vx_req_sessiongroup_control_recording_t
import com.vivox.service.vx_req_sessiongroup_create_t
import com.vivox.service.vx_req_sessiongroup_get_stats_t
import com.vivox.service.vx_req_sessiongroup_remove_session_t
import com.vivox.service.vx_req_sessiongroup_reset_focus_t
import com.vivox.service.vx_req_sessiongroup_set_focus_t
import com.vivox.service.vx_req_sessiongroup_set_playback_options_t
import com.vivox.service.vx_req_sessiongroup_set_session_3d_position_t
import com.vivox.service.vx_req_sessiongroup_set_tx_all_sessions_t
import com.vivox.service.vx_req_sessiongroup_set_tx_no_session_t
import com.vivox.service.vx_req_sessiongroup_set_tx_session_t
import com.vivox.service.vx_req_sessiongroup_terminate_t
import com.vivox.service.vx_req_sessiongroup_unset_focus_t
import com.vivox.service.vx_request_type
import com.vivox.service.vx_resp_account_anonymous_login_t
import com.vivox.service.vx_resp_account_authtoken_login_t
import com.vivox.service.vx_resp_account_buddy_delete_t
import com.vivox.service.vx_resp_account_buddy_search_t
import com.vivox.service.vx_resp_account_buddy_set_t
import com.vivox.service.vx_resp_account_buddygroup_delete_t
import com.vivox.service.vx_resp_account_buddygroup_set_t
import com.vivox.service.vx_resp_account_channel_add_acl_t
import com.vivox.service.vx_resp_account_channel_add_moderator_t
import com.vivox.service.vx_resp_account_channel_change_owner_t
import com.vivox.service.vx_resp_account_channel_create_t
import com.vivox.service.vx_resp_account_channel_delete_t
import com.vivox.service.vx_resp_account_channel_favorite_delete_t
import com.vivox.service.vx_resp_account_channel_favorite_group_delete_t
import com.vivox.service.vx_resp_account_channel_favorite_group_set_t
import com.vivox.service.vx_resp_account_channel_favorite_set_t
import com.vivox.service.vx_resp_account_channel_favorites_get_list_t
import com.vivox.service.vx_resp_account_channel_get_acl_t
import com.vivox.service.vx_resp_account_channel_get_info_t
import com.vivox.service.vx_resp_account_channel_get_moderators_t
import com.vivox.service.vx_resp_account_channel_get_participants_t
import com.vivox.service.vx_resp_account_channel_remove_acl_t
import com.vivox.service.vx_resp_account_channel_remove_moderator_t
import com.vivox.service.vx_resp_account_channel_search_t
import com.vivox.service.vx_resp_account_channel_update_t
import com.vivox.service.vx_resp_account_create_auto_accept_rule_t
import com.vivox.service.vx_resp_account_create_block_rule_t
import com.vivox.service.vx_resp_account_delete_auto_accept_rule_t
import com.vivox.service.vx_resp_account_delete_block_rule_t
import com.vivox.service.vx_resp_account_get_account_t
import com.vivox.service.vx_resp_account_get_session_fonts_t
import com.vivox.service.vx_resp_account_get_template_fonts_t
import com.vivox.service.vx_resp_account_list_auto_accept_rules_t
import com.vivox.service.vx_resp_account_list_block_rules_t
import com.vivox.service.vx_resp_account_list_buddies_and_groups_t
import com.vivox.service.vx_resp_account_login_t
import com.vivox.service.vx_resp_account_logout_t
import com.vivox.service.vx_resp_account_post_crash_dump_t
import com.vivox.service.vx_resp_account_send_message_t
import com.vivox.service.vx_resp_account_send_sms_t
import com.vivox.service.vx_resp_account_send_subscription_reply_t
import com.vivox.service.vx_resp_account_send_user_app_data_t
import com.vivox.service.vx_resp_account_set_login_properties_t
import com.vivox.service.vx_resp_account_set_presence_t
import com.vivox.service.vx_resp_account_update_account_t
import com.vivox.service.vx_resp_account_web_call_t
import com.vivox.service.vx_resp_aux_capture_audio_start_t
import com.vivox.service.vx_resp_aux_capture_audio_stop_t
import com.vivox.service.vx_resp_aux_connectivity_info_t
import com.vivox.service.vx_resp_aux_create_account_t
import com.vivox.service.vx_resp_aux_deactivate_account_t
import com.vivox.service.vx_resp_aux_diagnostic_state_dump_t
import com.vivox.service.vx_resp_aux_get_capture_devices_t
import com.vivox.service.vx_resp_aux_get_mic_level_t
import com.vivox.service.vx_resp_aux_get_render_devices_t
import com.vivox.service.vx_resp_aux_get_speaker_level_t
import com.vivox.service.vx_resp_aux_get_vad_properties_t
import com.vivox.service.vx_resp_aux_global_monitor_keyboard_mouse_t
import com.vivox.service.vx_resp_aux_play_audio_buffer_t
import com.vivox.service.vx_resp_aux_reactivate_account_t
import com.vivox.service.vx_resp_aux_render_audio_modify_t
import com.vivox.service.vx_resp_aux_render_audio_start_t
import com.vivox.service.vx_resp_aux_render_audio_stop_t
import com.vivox.service.vx_resp_aux_reset_password_t
import com.vivox.service.vx_resp_aux_set_capture_device_t
import com.vivox.service.vx_resp_aux_set_idle_timeout_t
import com.vivox.service.vx_resp_aux_set_mic_level_t
import com.vivox.service.vx_resp_aux_set_render_device_t
import com.vivox.service.vx_resp_aux_set_speaker_level_t
import com.vivox.service.vx_resp_aux_set_vad_properties_t
import com.vivox.service.vx_resp_aux_start_buffer_capture_t
import com.vivox.service.vx_resp_base_t
import com.vivox.service.vx_resp_channel_ban_user_t
import com.vivox.service.vx_resp_channel_get_banned_users_t
import com.vivox.service.vx_resp_channel_kick_user_t
import com.vivox.service.vx_resp_channel_mute_all_users_t
import com.vivox.service.vx_resp_channel_mute_user_t
import com.vivox.service.vx_resp_channel_set_lock_mode_t
import com.vivox.service.vx_resp_connector_create_t
import com.vivox.service.vx_resp_connector_get_local_audio_info_t
import com.vivox.service.vx_resp_connector_initiate_shutdown_t
import com.vivox.service.vx_resp_connector_mute_local_mic_t
import com.vivox.service.vx_resp_connector_mute_local_speaker_t
import com.vivox.service.vx_resp_connector_set_local_mic_volume_t
import com.vivox.service.vx_resp_connector_set_local_speaker_volume_t
import com.vivox.service.vx_resp_session_channel_invite_user_t
import com.vivox.service.vx_resp_session_create_t
import com.vivox.service.vx_resp_session_media_connect_t
import com.vivox.service.vx_resp_session_media_disconnect_t
import com.vivox.service.vx_resp_session_mute_local_speaker_t
import com.vivox.service.vx_resp_session_send_dtmf_t
import com.vivox.service.vx_resp_session_send_message_t
import com.vivox.service.vx_resp_session_send_notification_t
import com.vivox.service.vx_resp_session_set_3d_position_t
import com.vivox.service.vx_resp_session_set_local_speaker_volume_t
import com.vivox.service.vx_resp_session_set_participant_mute_for_me_t
import com.vivox.service.vx_resp_session_set_participant_volume_for_me_t
import com.vivox.service.vx_resp_session_set_voice_font_t
import com.vivox.service.vx_resp_session_terminate_t
import com.vivox.service.vx_resp_session_text_connect_t
import com.vivox.service.vx_resp_session_text_disconnect_t
import com.vivox.service.vx_resp_sessiongroup_add_session_t
import com.vivox.service.vx_resp_sessiongroup_control_audio_injection_t
import com.vivox.service.vx_resp_sessiongroup_control_playback_t
import com.vivox.service.vx_resp_sessiongroup_control_recording_t
import com.vivox.service.vx_resp_sessiongroup_create_t
import com.vivox.service.vx_resp_sessiongroup_get_stats_t
import com.vivox.service.vx_resp_sessiongroup_remove_session_t
import com.vivox.service.vx_resp_sessiongroup_reset_focus_t
import com.vivox.service.vx_resp_sessiongroup_set_focus_t
import com.vivox.service.vx_resp_sessiongroup_set_playback_options_t
import com.vivox.service.vx_resp_sessiongroup_set_session_3d_position_t
import com.vivox.service.vx_resp_sessiongroup_set_tx_all_sessions_t
import com.vivox.service.vx_resp_sessiongroup_set_tx_no_session_t
import com.vivox.service.vx_resp_sessiongroup_set_tx_session_t
import com.vivox.service.vx_resp_sessiongroup_terminate_t
import com.vivox.service.vx_resp_sessiongroup_unset_focus_t
import com.vivox.service.vx_response_type
import com.vivox.service.vx_sdk_config_t
import com.vivox.service.vx_state_account_t
import com.vivox.service.vx_state_buddy_contact_t
import com.vivox.service.vx_state_buddy_group_t
import com.vivox.service.vx_state_buddy_t
import com.vivox.service.vx_state_connector_t
import com.vivox.service.vx_state_participant_t
import com.vivox.service.vx_state_session_t
import com.vivox.service.vx_state_sessiongroup_t
import com.vivox.service.vx_system_stats_t
import com.vivox.service.vx_user_channel_t
import com.vivox.service.vx_voice_font_t
import java.math.BigInteger

class VxClientProxy
: VxClientProxyConstants {
    Unit clear_stats() {
        VxClientProxyJNI.clear_stats()
    }

    Unit destroy_evt(vx_evt_base_t vx_evt_base_t2) {
        VxClientProxyJNI.destroy_evt(vx_evt_base_t.getCPtr(vx_evt_base_t2), vx_evt_base_t2)
    }

    Unit destroy_message(vx_message_base_t vx_message_base_t2) {
        VxClientProxyJNI.destroy_message(vx_message_base_t.getCPtr(vx_message_base_t2), vx_message_base_t2)
    }

    Unit destroy_req(vx_req_base_t vx_req_base_t2) {
        VxClientProxyJNI.destroy_req(vx_req_base_t.getCPtr(vx_req_base_t2), vx_req_base_t2)
    }

    Unit destroy_resp(vx_resp_base_t vx_resp_base_t2) {
        VxClientProxyJNI.destroy_resp(vx_resp_base_t.getCPtr(vx_resp_base_t2), vx_resp_base_t2)
    }

    Unit free_sdk_string(SWIGTYPE_p_sdk_string sWIGTYPE_p_sdk_string) {
        VxClientProxyJNI.free_sdk_string(SWIGTYPE_p_sdk_string.getCPtr(sWIGTYPE_p_sdk_string))
    }

    String get_java_system_property(String string2) {
        return VxClientProxyJNI.get_java_system_property(string2)
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_message_base_t get_next_message(Int n) {
        var l: Long = VxClientProxyJNI.get_next_message(n)
        if (l != 0L) return vx_message_base_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_message_base_t get_next_message_no_wait() {
        var l: Long = VxClientProxyJNI.get_next_message_no_wait()
        if (l != 0L) return vx_message_base_t(l, false)
        return null
    }

    SWIGTYPE_p_sdk_string get_stats() {
        return SWIGTYPE_p_sdk_string(VxClientProxyJNI.get_stats(), true)
    }

    SWIGTYPE_p_sdk_string message_to_xml(vx_message_base_t vx_message_base_t2) {
        return SWIGTYPE_p_sdk_string(VxClientProxyJNI.message_to_xml(vx_message_base_t.getCPtr(vx_message_base_t2), vx_message_base_t2), true)
    }

    vx_event_type msg_evt_subtype(vx_message_base_t vx_message_base_t2) {
        return vx_event_type.swigToEnum(VxClientProxyJNI.msg_evt_subtype(vx_message_base_t.getCPtr(vx_message_base_t2), vx_message_base_t2))
    }

    vx_request_type msg_req_subtype(vx_message_base_t vx_message_base_t2) {
        return vx_request_type.swigToEnum(VxClientProxyJNI.msg_req_subtype(vx_message_base_t.getCPtr(vx_message_base_t2), vx_message_base_t2))
    }

    vx_response_type msg_resp_subtype(vx_message_base_t vx_message_base_t2) {
        return vx_response_type.swigToEnum(VxClientProxyJNI.msg_resp_subtype(vx_message_base_t.getCPtr(vx_message_base_t2), vx_message_base_t2))
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    SWIGTYPE_p_p_vx_name_value_pair name_value_pairs_create(Int n) {
        var l: Long = VxClientProxyJNI.name_value_pairs_create(n)
        if (l != 0L) return SWIGTYPE_p_p_vx_name_value_pair(l, false)
        return null
    }

    Int register_logging_handler(String string2, String string3, vx_log_level vx_log_level2) {
        return VxClientProxyJNI.register_logging_handler(string2, string3, vx_log_level2.swigValue())
    }

    Int register_message_notification_handler(String string2, String string3) {
        return VxClientProxyJNI.register_message_notification_handler(string2, string3)
    }

    SWIGTYPE_p_sdk_string request_to_xml(vx_req_base_t vx_req_base_t2) {
        return SWIGTYPE_p_sdk_string(VxClientProxyJNI.request_to_xml(vx_req_base_t.getCPtr(vx_req_base_t2), vx_req_base_t2), true)
    }

    String sdk_string_to_string(SWIGTYPE_p_sdk_string sWIGTYPE_p_sdk_string) {
        return VxClientProxyJNI.sdk_string_to_string(SWIGTYPE_p_sdk_string.getCPtr(sWIGTYPE_p_sdk_string))
    }

    Unit set_request_cookie(vx_req_base_t vx_req_base_t2, String string2) {
        VxClientProxyJNI.set_request_cookie(vx_req_base_t.getCPtr(vx_req_base_t2), vx_req_base_t2, string2)
    }

    Unit vx_account_create(SWIGTYPE_p_p_vx_account sWIGTYPE_p_p_vx_account) {
        VxClientProxyJNI.vx_account_create(SWIGTYPE_p_p_vx_account.getCPtr(sWIGTYPE_p_p_vx_account))
    }

    Unit vx_account_free(vx_account_t vx_account_t2) {
        VxClientProxyJNI.vx_account_free(vx_account_t.getCPtr(vx_account_t2), vx_account_t2)
    }

    Int vx_alloc_sdk_handle(String string2, Int n, SWIGTYPE_p_unsigned_int sWIGTYPE_p_unsigned_int) {
        return VxClientProxyJNI.vx_alloc_sdk_handle(string2, n, SWIGTYPE_p_unsigned_int.getCPtr(sWIGTYPE_p_unsigned_int))
    }

    Int vx_apply_font_to_file(String string2, String string3, String string4) {
        return VxClientProxyJNI.vx_apply_font_to_file(string2, string3, string4)
    }

    Int vx_apply_font_to_file_return_energy_ratio(String string2, String string3, String string4, SWIGTYPE_p_double sWIGTYPE_p_double) {
        return VxClientProxyJNI.vx_apply_font_to_file_return_energy_ratio(string2, string3, string4, SWIGTYPE_p_double.getCPtr(sWIGTYPE_p_double))
    }

    Int vx_apply_font_to_vxz_file_return_energy_ratio(String string2, String string3, String string4, SWIGTYPE_p_double sWIGTYPE_p_double) {
        return VxClientProxyJNI.vx_apply_font_to_vxz_file_return_energy_ratio(string2, string3, string4, SWIGTYPE_p_double.getCPtr(sWIGTYPE_p_double))
    }

    Unit vx_auto_accept_rule_create(SWIGTYPE_p_p_vx_auto_accept_rule sWIGTYPE_p_p_vx_auto_accept_rule) {
        VxClientProxyJNI.vx_auto_accept_rule_create(SWIGTYPE_p_p_vx_auto_accept_rule.getCPtr(sWIGTYPE_p_p_vx_auto_accept_rule))
    }

    Unit vx_auto_accept_rule_free(vx_auto_accept_rule_t vx_auto_accept_rule_t2) {
        VxClientProxyJNI.vx_auto_accept_rule_free(vx_auto_accept_rule_t.getCPtr(vx_auto_accept_rule_t2), vx_auto_accept_rule_t2)
    }

    Unit vx_auto_accept_rules_create(Int n, SWIGTYPE_p_p_p_vx_auto_accept_rule sWIGTYPE_p_p_p_vx_auto_accept_rule) {
        VxClientProxyJNI.vx_auto_accept_rules_create(n, SWIGTYPE_p_p_p_vx_auto_accept_rule.getCPtr(sWIGTYPE_p_p_p_vx_auto_accept_rule))
    }

    Unit vx_auto_accept_rules_free(SWIGTYPE_p_p_vx_auto_accept_rule sWIGTYPE_p_p_vx_auto_accept_rule, Int n) {
        VxClientProxyJNI.vx_auto_accept_rules_free(SWIGTYPE_p_p_vx_auto_accept_rule.getCPtr(sWIGTYPE_p_p_vx_auto_accept_rule), n)
    }

    Unit vx_block_rule_create(SWIGTYPE_p_p_vx_block_rule sWIGTYPE_p_p_vx_block_rule) {
        VxClientProxyJNI.vx_block_rule_create(SWIGTYPE_p_p_vx_block_rule.getCPtr(sWIGTYPE_p_p_vx_block_rule))
    }

    Unit vx_block_rule_free(vx_block_rule_t vx_block_rule_t2) {
        VxClientProxyJNI.vx_block_rule_free(vx_block_rule_t.getCPtr(vx_block_rule_t2), vx_block_rule_t2)
    }

    Unit vx_block_rules_create(Int n, SWIGTYPE_p_p_p_vx_block_rule sWIGTYPE_p_p_p_vx_block_rule) {
        VxClientProxyJNI.vx_block_rules_create(n, SWIGTYPE_p_p_p_vx_block_rule.getCPtr(sWIGTYPE_p_p_p_vx_block_rule))
    }

    Unit vx_block_rules_free(SWIGTYPE_p_p_vx_block_rule sWIGTYPE_p_p_vx_block_rule, Int n) {
        VxClientProxyJNI.vx_block_rules_free(SWIGTYPE_p_p_vx_block_rule.getCPtr(sWIGTYPE_p_p_vx_block_rule), n)
    }

    Unit vx_buddy_create(SWIGTYPE_p_p_vx_buddy sWIGTYPE_p_p_vx_buddy) {
        VxClientProxyJNI.vx_buddy_create(SWIGTYPE_p_p_vx_buddy.getCPtr(sWIGTYPE_p_p_vx_buddy))
    }

    Unit vx_buddy_free(vx_buddy_t vx_buddy_t2) {
        VxClientProxyJNI.vx_buddy_free(vx_buddy_t.getCPtr(vx_buddy_t2), vx_buddy_t2)
    }

    Unit vx_buddy_list_create(Int n, SWIGTYPE_p_p_p_vx_buddy sWIGTYPE_p_p_p_vx_buddy) {
        VxClientProxyJNI.vx_buddy_list_create(n, SWIGTYPE_p_p_p_vx_buddy.getCPtr(sWIGTYPE_p_p_p_vx_buddy))
    }

    Unit vx_buddy_list_free(SWIGTYPE_p_p_vx_buddy sWIGTYPE_p_p_vx_buddy, Int n) {
        VxClientProxyJNI.vx_buddy_list_free(SWIGTYPE_p_p_vx_buddy.getCPtr(sWIGTYPE_p_p_vx_buddy), n)
    }

    Unit vx_channel_create(SWIGTYPE_p_p_vx_channel sWIGTYPE_p_p_vx_channel) {
        VxClientProxyJNI.vx_channel_create(SWIGTYPE_p_p_vx_channel.getCPtr(sWIGTYPE_p_p_vx_channel))
    }

    Unit vx_channel_favorite_create(SWIGTYPE_p_p_vx_channel_favorite sWIGTYPE_p_p_vx_channel_favorite) {
        VxClientProxyJNI.vx_channel_favorite_create(SWIGTYPE_p_p_vx_channel_favorite.getCPtr(sWIGTYPE_p_p_vx_channel_favorite))
    }

    Unit vx_channel_favorite_free(vx_channel_favorite_t vx_channel_favorite_t2) {
        VxClientProxyJNI.vx_channel_favorite_free(vx_channel_favorite_t.getCPtr(vx_channel_favorite_t2), vx_channel_favorite_t2)
    }

    Unit vx_channel_favorite_group_create(SWIGTYPE_p_p_vx_channel_favorite_group sWIGTYPE_p_p_vx_channel_favorite_group) {
        VxClientProxyJNI.vx_channel_favorite_group_create(SWIGTYPE_p_p_vx_channel_favorite_group.getCPtr(sWIGTYPE_p_p_vx_channel_favorite_group))
    }

    Unit vx_channel_favorite_group_free(vx_channel_favorite_group_t vx_channel_favorite_group_t2) {
        VxClientProxyJNI.vx_channel_favorite_group_free(vx_channel_favorite_group_t.getCPtr(vx_channel_favorite_group_t2), vx_channel_favorite_group_t2)
    }

    Unit vx_channel_favorite_group_list_create(Int n, SWIGTYPE_p_p_p_vx_channel_favorite_group sWIGTYPE_p_p_p_vx_channel_favorite_group) {
        VxClientProxyJNI.vx_channel_favorite_group_list_create(n, SWIGTYPE_p_p_p_vx_channel_favorite_group.getCPtr(sWIGTYPE_p_p_p_vx_channel_favorite_group))
    }

    Unit vx_channel_favorite_group_list_free(SWIGTYPE_p_p_vx_channel_favorite_group sWIGTYPE_p_p_vx_channel_favorite_group, Int n) {
        VxClientProxyJNI.vx_channel_favorite_group_list_free(SWIGTYPE_p_p_vx_channel_favorite_group.getCPtr(sWIGTYPE_p_p_vx_channel_favorite_group), n)
    }

    Unit vx_channel_favorite_list_create(Int n, SWIGTYPE_p_p_p_vx_channel_favorite sWIGTYPE_p_p_p_vx_channel_favorite) {
        VxClientProxyJNI.vx_channel_favorite_list_create(n, SWIGTYPE_p_p_p_vx_channel_favorite.getCPtr(sWIGTYPE_p_p_p_vx_channel_favorite))
    }

    Unit vx_channel_favorite_list_free(SWIGTYPE_p_p_vx_channel_favorite sWIGTYPE_p_p_vx_channel_favorite, Int n) {
        VxClientProxyJNI.vx_channel_favorite_list_free(SWIGTYPE_p_p_vx_channel_favorite.getCPtr(sWIGTYPE_p_p_vx_channel_favorite), n)
    }

    Unit vx_channel_free(vx_channel_t vx_channel_t2) {
        VxClientProxyJNI.vx_channel_free(vx_channel_t.getCPtr(vx_channel_t2), vx_channel_t2)
    }

    Unit vx_channel_list_create(Int n, SWIGTYPE_p_p_p_vx_channel sWIGTYPE_p_p_p_vx_channel) {
        VxClientProxyJNI.vx_channel_list_create(n, SWIGTYPE_p_p_p_vx_channel.getCPtr(sWIGTYPE_p_p_p_vx_channel))
    }

    Unit vx_channel_list_free(SWIGTYPE_p_p_vx_channel sWIGTYPE_p_p_vx_channel, Int n) {
        VxClientProxyJNI.vx_channel_list_free(SWIGTYPE_p_p_vx_channel.getCPtr(sWIGTYPE_p_p_vx_channel), n)
    }

    Unit vx_connectivity_test_result_create(SWIGTYPE_p_p_vx_connectivity_test_result sWIGTYPE_p_p_vx_connectivity_test_result, ND_TEST_TYPE nD_TEST_TYPE) {
        VxClientProxyJNI.vx_connectivity_test_result_create(SWIGTYPE_p_p_vx_connectivity_test_result.getCPtr(sWIGTYPE_p_p_vx_connectivity_test_result), nD_TEST_TYPE.swigValue())
    }

    Unit vx_connectivity_test_result_free(vx_connectivity_test_result_t vx_connectivity_test_result_t2) {
        VxClientProxyJNI.vx_connectivity_test_result_free(vx_connectivity_test_result_t.getCPtr(vx_connectivity_test_result_t2), vx_connectivity_test_result_t2)
    }

    Unit vx_connectivity_test_results_create(Int n, SWIGTYPE_p_p_p_vx_connectivity_test_result sWIGTYPE_p_p_p_vx_connectivity_test_result) {
        VxClientProxyJNI.vx_connectivity_test_results_create(n, SWIGTYPE_p_p_p_vx_connectivity_test_result.getCPtr(sWIGTYPE_p_p_p_vx_connectivity_test_result))
    }

    Unit vx_connectivity_test_results_free(SWIGTYPE_p_p_vx_connectivity_test_result sWIGTYPE_p_p_vx_connectivity_test_result, Int n) {
        VxClientProxyJNI.vx_connectivity_test_results_free(SWIGTYPE_p_p_vx_connectivity_test_result.getCPtr(sWIGTYPE_p_p_vx_connectivity_test_result), n)
    }

    Unit vx_cookie_create(String string2, SWIGTYPE_p_p_char sWIGTYPE_p_p_char) {
        VxClientProxyJNI.vx_cookie_create(string2, SWIGTYPE_p_p_char.getCPtr(sWIGTYPE_p_p_char))
    }

    Unit vx_cookie_free(SWIGTYPE_p_p_char sWIGTYPE_p_p_char) {
        VxClientProxyJNI.vx_cookie_free(SWIGTYPE_p_p_char.getCPtr(sWIGTYPE_p_p_char))
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    SWIGTYPE_p_void vx_copy_audioBuffer(SWIGTYPE_p_void sWIGTYPE_p_void) {
        var l: Long = VxClientProxyJNI.vx_copy_audioBuffer(SWIGTYPE_p_void.getCPtr(sWIGTYPE_p_void))
        if (l != 0L) return SWIGTYPE_p_void(l, false)
        return null
    }

    Int vx_create_account(String string2, String string3, String string4, String string5, String string6) {
        return VxClientProxyJNI.vx_create_account(string2, string3, string4, string5, string6)
    }

    Int vx_delete_crash_dump(Int n) {
        return VxClientProxyJNI.vx_delete_crash_dump(n)
    }

    Unit vx_device_create(SWIGTYPE_p_p_vx_device sWIGTYPE_p_p_vx_device) {
        VxClientProxyJNI.vx_device_create(SWIGTYPE_p_p_vx_device.getCPtr(sWIGTYPE_p_p_vx_device))
    }

    Unit vx_device_free(vx_device_t vx_device_t2) {
        VxClientProxyJNI.vx_device_free(vx_device_t.getCPtr(vx_device_t2), vx_device_t2)
    }

    Unit vx_devices_create(Int n, SWIGTYPE_p_p_p_vx_device sWIGTYPE_p_p_p_vx_device) {
        VxClientProxyJNI.vx_devices_create(n, SWIGTYPE_p_p_p_vx_device.getCPtr(sWIGTYPE_p_p_p_vx_device))
    }

    Unit vx_devices_free(SWIGTYPE_p_p_vx_device sWIGTYPE_p_p_vx_device, Int n) {
        VxClientProxyJNI.vx_devices_free(SWIGTYPE_p_p_vx_device.getCPtr(sWIGTYPE_p_p_vx_device), n)
    }

    Unit vx_event_to_xml(SWIGTYPE_p_void sWIGTYPE_p_void, SWIGTYPE_p_p_char sWIGTYPE_p_p_char) {
        VxClientProxyJNI.vx_event_to_xml(SWIGTYPE_p_void.getCPtr(sWIGTYPE_p_void), SWIGTYPE_p_p_char.getCPtr(sWIGTYPE_p_p_char))
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_buddy_t vx_evt_buddy_and_group_list_changed_t_get_buddies_item(vx_evt_buddy_and_group_list_changed_t object, Int n) {
        var l: Long = VxClientProxyJNI.vx_evt_buddy_and_group_list_changed_t_get_buddies_item(vx_evt_buddy_and_group_list_changed_t.getCPtr((vx_evt_buddy_and_group_list_changed_t)object), (vx_evt_buddy_and_group_list_changed_t)object, n)
        if (l != 0L) return vx_buddy_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_group_t vx_evt_buddy_and_group_list_changed_t_get_groups_item(vx_evt_buddy_and_group_list_changed_t object, Int n) {
        var l: Long = VxClientProxyJNI.vx_evt_buddy_and_group_list_changed_t_get_groups_item(vx_evt_buddy_and_group_list_changed_t.getCPtr((vx_evt_buddy_and_group_list_changed_t)object), (vx_evt_buddy_and_group_list_changed_t)object, n)
        if (l != 0L) return vx_group_t(l, false)
        return null
    }

    vx_participant_diagnostic_state_t vx_evt_participant_updated_t_get_diagnostic_states_item(vx_evt_participant_updated_t vx_evt_participant_updated_t2, Int n) {
        return vx_participant_diagnostic_state_t.swigToEnum(VxClientProxyJNI.vx_evt_participant_updated_t_get_diagnostic_states_item(vx_evt_participant_updated_t.getCPtr(vx_evt_participant_updated_t2), vx_evt_participant_updated_t2, n))
    }

    Double vx_evt_session_updated_t_get_speaker_position_item(vx_evt_session_updated_t vx_evt_session_updated_t2, Int n) {
        return VxClientProxyJNI.vx_evt_session_updated_t_get_speaker_position_item(vx_evt_session_updated_t.getCPtr(vx_evt_session_updated_t2), vx_evt_session_updated_t2, n)
    }

    Int vx_export_audioBuffer_to_wav_file(SWIGTYPE_p_void sWIGTYPE_p_void, String string2) {
        return VxClientProxyJNI.vx_export_audioBuffer_to_wav_file(SWIGTYPE_p_void.getCPtr(sWIGTYPE_p_void), string2)
    }

    Int vx_export_vxr_expanded(String string2, String string3, SWIGTYPE_p_f_p_void_int_int__void sWIGTYPE_p_f_p_void_int_int__void, SWIGTYPE_p_void sWIGTYPE_p_void) {
        return VxClientProxyJNI.vx_export_vxr_expanded(string2, string3, SWIGTYPE_p_f_p_void_int_int__void.getCPtr(sWIGTYPE_p_f_p_void_int_int__void), SWIGTYPE_p_void.getCPtr(sWIGTYPE_p_void))
    }

    Int vx_export_vxr_mixed(String string2, String string3, SWIGTYPE_p_f_p_void_int_int__void sWIGTYPE_p_f_p_void_int_int__void, SWIGTYPE_p_void sWIGTYPE_p_void) {
        return VxClientProxyJNI.vx_export_vxr_mixed(string2, string3, SWIGTYPE_p_f_p_void_int_int__void.getCPtr(sWIGTYPE_p_f_p_void_int_int__void), SWIGTYPE_p_void.getCPtr(sWIGTYPE_p_void))
    }

    Unit vx_free(String string2) {
        VxClientProxyJNI.vx_free(string2)
    }

    Unit vx_free_audioBuffer(SWIGTYPE_p_p_void sWIGTYPE_p_p_void) {
        VxClientProxyJNI.vx_free_audioBuffer(SWIGTYPE_p_p_void.getCPtr(sWIGTYPE_p_p_void))
    }

    Int vx_free_sdk_handle(Long l) {
        return VxClientProxyJNI.vx_free_sdk_handle(l)
    }

    Double vx_get_audioBuffer_duration(SWIGTYPE_p_void sWIGTYPE_p_void) {
        return VxClientProxyJNI.vx_get_audioBuffer_duration(SWIGTYPE_p_void.getCPtr(sWIGTYPE_p_void))
    }

    Int vx_get_crash_dump_count() {
        return VxClientProxyJNI.vx_get_crash_dump_count()
    }

    Int vx_get_crash_dump_generation() {
        return VxClientProxyJNI.vx_get_crash_dump_generation()
    }

    SWIGTYPE_p_time_t vx_get_crash_dump_timestamp(Int n) {
        return SWIGTYPE_p_time_t(VxClientProxyJNI.vx_get_crash_dump_timestamp(n), true)
    }

    Int vx_get_default_config(vx_sdk_config_t vx_sdk_config_t2) {
        return VxClientProxyJNI.vx_get_default_config(vx_sdk_config_t.getCPtr(vx_sdk_config_t2), vx_sdk_config_t2)
    }

    String vx_get_error_string(Int n) {
        return VxClientProxyJNI.vx_get_error_string(n)
    }

    Int vx_get_message(SWIGTYPE_p_p_vx_message_base sWIGTYPE_p_p_vx_message_base) {
        return VxClientProxyJNI.vx_get_message(SWIGTYPE_p_p_vx_message_base.getCPtr(sWIGTYPE_p_p_vx_message_base))
    }

    vx_message_type vx_get_message_type(String string2) {
        return vx_message_type.swigToEnum(VxClientProxyJNI.vx_get_message_type(string2))
    }

    Int vx_get_preferred_codec(SWIGTYPE_p_int sWIGTYPE_p_int) {
        return VxClientProxyJNI.vx_get_preferred_codec(SWIGTYPE_p_int.getCPtr(sWIGTYPE_p_int))
    }

    String vx_get_sdk_version_info() {
        return VxClientProxyJNI.vx_get_sdk_version_info()
    }

    Int vx_get_system_stats(vx_system_stats_t vx_system_stats_t2) {
        return VxClientProxyJNI.vx_get_system_stats(vx_system_stats_t.getCPtr(vx_system_stats_t2), vx_system_stats_t2)
    }

    BigInteger vx_get_time_ms() {
        return VxClientProxyJNI.vx_get_time_ms()
    }

    Unit vx_group_create(SWIGTYPE_p_p_vx_group sWIGTYPE_p_p_vx_group) {
        VxClientProxyJNI.vx_group_create(SWIGTYPE_p_p_vx_group.getCPtr(sWIGTYPE_p_p_vx_group))
    }

    Unit vx_group_free(vx_group_t vx_group_t2) {
        VxClientProxyJNI.vx_group_free(vx_group_t.getCPtr(vx_group_t2), vx_group_t2)
    }

    Unit vx_group_list_create(Int n, SWIGTYPE_p_p_p_vx_group sWIGTYPE_p_p_p_vx_group) {
        VxClientProxyJNI.vx_group_list_create(n, SWIGTYPE_p_p_p_vx_group.getCPtr(sWIGTYPE_p_p_p_vx_group))
    }

    Unit vx_group_list_free(SWIGTYPE_p_p_vx_group sWIGTYPE_p_p_vx_group, Int n) {
        VxClientProxyJNI.vx_group_list_free(SWIGTYPE_p_p_vx_group.getCPtr(sWIGTYPE_p_p_vx_group), n)
    }

    Int vx_initialize() {
        return VxClientProxyJNI.vx_initialize()
    }

    Int vx_initialize2(vx_sdk_config_t vx_sdk_config_t2) {
        return VxClientProxyJNI.vx_initialize2(vx_sdk_config_t.getCPtr(vx_sdk_config_t2), vx_sdk_config_t2)
    }

    Int vx_issue_request(vx_req_base_t vx_req_base_t2) {
        return VxClientProxyJNI.vx_issue_request(vx_req_base_t.getCPtr(vx_req_base_t2), vx_req_base_t2)
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_evt_account_login_state_change_t vx_message_base_t2vx_evt_account_login_state_change_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_evt_account_login_state_change_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_evt_account_login_state_change_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_evt_aux_audio_properties_t vx_message_base_t2vx_evt_aux_audio_properties_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_evt_aux_audio_properties_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_evt_aux_audio_properties_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_evt_buddy_and_group_list_changed_t vx_message_base_t2vx_evt_buddy_and_group_list_changed_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_evt_buddy_and_group_list_changed_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_evt_buddy_and_group_list_changed_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_evt_buddy_changed_t vx_message_base_t2vx_evt_buddy_changed_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_evt_buddy_changed_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_evt_buddy_changed_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_evt_buddy_group_changed_t vx_message_base_t2vx_evt_buddy_group_changed_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_evt_buddy_group_changed_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_evt_buddy_group_changed_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_evt_buddy_presence_t vx_message_base_t2vx_evt_buddy_presence_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_evt_buddy_presence_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_evt_buddy_presence_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_evt_idle_state_changed_t vx_message_base_t2vx_evt_idle_state_changed_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_evt_idle_state_changed_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_evt_idle_state_changed_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_evt_keyboard_mouse_t vx_message_base_t2vx_evt_keyboard_mouse_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_evt_keyboard_mouse_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_evt_keyboard_mouse_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_evt_media_completion_t vx_message_base_t2vx_evt_media_completion_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_evt_media_completion_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_evt_media_completion_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_evt_media_stream_updated_t vx_message_base_t2vx_evt_media_stream_updated_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_evt_media_stream_updated_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_evt_media_stream_updated_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_evt_message_t vx_message_base_t2vx_evt_message_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_evt_message_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_evt_message_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_evt_network_message_t vx_message_base_t2vx_evt_network_message_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_evt_network_message_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_evt_network_message_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_evt_participant_added_t vx_message_base_t2vx_evt_participant_added_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_evt_participant_added_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_evt_participant_added_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_evt_participant_removed_t vx_message_base_t2vx_evt_participant_removed_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_evt_participant_removed_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_evt_participant_removed_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_evt_participant_updated_t vx_message_base_t2vx_evt_participant_updated_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_evt_participant_updated_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_evt_participant_updated_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_evt_server_app_data_t vx_message_base_t2vx_evt_server_app_data_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_evt_server_app_data_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_evt_server_app_data_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_evt_session_added_t vx_message_base_t2vx_evt_session_added_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_evt_session_added_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_evt_session_added_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_evt_session_notification_t vx_message_base_t2vx_evt_session_notification_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_evt_session_notification_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_evt_session_notification_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_evt_session_removed_t vx_message_base_t2vx_evt_session_removed_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_evt_session_removed_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_evt_session_removed_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_evt_session_updated_t vx_message_base_t2vx_evt_session_updated_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_evt_session_updated_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_evt_session_updated_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_evt_sessiongroup_added_t vx_message_base_t2vx_evt_sessiongroup_added_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_evt_sessiongroup_added_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_evt_sessiongroup_added_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_evt_sessiongroup_playback_frame_played_t vx_message_base_t2vx_evt_sessiongroup_playback_frame_played_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_evt_sessiongroup_playback_frame_played_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_evt_sessiongroup_playback_frame_played_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_evt_sessiongroup_removed_t vx_message_base_t2vx_evt_sessiongroup_removed_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_evt_sessiongroup_removed_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_evt_sessiongroup_removed_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_evt_sessiongroup_updated_t vx_message_base_t2vx_evt_sessiongroup_updated_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_evt_sessiongroup_updated_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_evt_sessiongroup_updated_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_evt_subscription_t vx_message_base_t2vx_evt_subscription_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_evt_subscription_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_evt_subscription_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_evt_text_stream_updated_t vx_message_base_t2vx_evt_text_stream_updated_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_evt_text_stream_updated_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_evt_text_stream_updated_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_evt_user_app_data_t vx_message_base_t2vx_evt_user_app_data_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_evt_user_app_data_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_evt_user_app_data_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_evt_voice_service_connection_state_changed_t vx_message_base_t2vx_evt_voice_service_connection_state_changed_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_evt_voice_service_connection_state_changed_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_evt_voice_service_connection_state_changed_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_account_anonymous_login_t vx_message_base_t2vx_req_account_anonymous_login_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_account_anonymous_login_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_account_anonymous_login_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_account_authtoken_login_t vx_message_base_t2vx_req_account_authtoken_login_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_account_authtoken_login_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_account_authtoken_login_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_account_buddy_delete_t vx_message_base_t2vx_req_account_buddy_delete_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_account_buddy_delete_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_account_buddy_delete_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_account_buddy_search_t vx_message_base_t2vx_req_account_buddy_search_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_account_buddy_search_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_account_buddy_search_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_account_buddy_set_t vx_message_base_t2vx_req_account_buddy_set_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_account_buddy_set_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_account_buddy_set_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_account_buddygroup_delete_t vx_message_base_t2vx_req_account_buddygroup_delete_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_account_buddygroup_delete_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_account_buddygroup_delete_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_account_buddygroup_set_t vx_message_base_t2vx_req_account_buddygroup_set_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_account_buddygroup_set_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_account_buddygroup_set_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_account_channel_add_acl_t vx_message_base_t2vx_req_account_channel_add_acl_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_account_channel_add_acl_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_account_channel_add_acl_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_account_channel_add_moderator_t vx_message_base_t2vx_req_account_channel_add_moderator_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_account_channel_add_moderator_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_account_channel_add_moderator_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_account_channel_change_owner_t vx_message_base_t2vx_req_account_channel_change_owner_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_account_channel_change_owner_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_account_channel_change_owner_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_account_channel_create_t vx_message_base_t2vx_req_account_channel_create_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_account_channel_create_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_account_channel_create_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_account_channel_delete_t vx_message_base_t2vx_req_account_channel_delete_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_account_channel_delete_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_account_channel_delete_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_account_channel_favorite_delete_t vx_message_base_t2vx_req_account_channel_favorite_delete_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_account_channel_favorite_delete_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_account_channel_favorite_delete_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_account_channel_favorite_group_delete_t vx_message_base_t2vx_req_account_channel_favorite_group_delete_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_account_channel_favorite_group_delete_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_account_channel_favorite_group_delete_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_account_channel_favorite_group_set_t vx_message_base_t2vx_req_account_channel_favorite_group_set_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_account_channel_favorite_group_set_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_account_channel_favorite_group_set_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_account_channel_favorite_set_t vx_message_base_t2vx_req_account_channel_favorite_set_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_account_channel_favorite_set_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_account_channel_favorite_set_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_account_channel_favorites_get_list_t vx_message_base_t2vx_req_account_channel_favorites_get_list_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_account_channel_favorites_get_list_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_account_channel_favorites_get_list_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_account_channel_get_acl_t vx_message_base_t2vx_req_account_channel_get_acl_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_account_channel_get_acl_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_account_channel_get_acl_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_account_channel_get_info_t vx_message_base_t2vx_req_account_channel_get_info_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_account_channel_get_info_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_account_channel_get_info_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_account_channel_get_moderators_t vx_message_base_t2vx_req_account_channel_get_moderators_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_account_channel_get_moderators_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_account_channel_get_moderators_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_account_channel_get_participants_t vx_message_base_t2vx_req_account_channel_get_participants_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_account_channel_get_participants_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_account_channel_get_participants_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_account_channel_remove_acl_t vx_message_base_t2vx_req_account_channel_remove_acl_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_account_channel_remove_acl_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_account_channel_remove_acl_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_account_channel_remove_moderator_t vx_message_base_t2vx_req_account_channel_remove_moderator_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_account_channel_remove_moderator_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_account_channel_remove_moderator_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_account_channel_search_t vx_message_base_t2vx_req_account_channel_search_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_account_channel_search_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_account_channel_search_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_account_channel_update_t vx_message_base_t2vx_req_account_channel_update_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_account_channel_update_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_account_channel_update_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_account_create_auto_accept_rule_t vx_message_base_t2vx_req_account_create_auto_accept_rule_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_account_create_auto_accept_rule_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_account_create_auto_accept_rule_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_account_create_block_rule_t vx_message_base_t2vx_req_account_create_block_rule_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_account_create_block_rule_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_account_create_block_rule_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_account_delete_auto_accept_rule_t vx_message_base_t2vx_req_account_delete_auto_accept_rule_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_account_delete_auto_accept_rule_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_account_delete_auto_accept_rule_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_account_delete_block_rule_t vx_message_base_t2vx_req_account_delete_block_rule_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_account_delete_block_rule_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_account_delete_block_rule_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_account_get_account_t vx_message_base_t2vx_req_account_get_account_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_account_get_account_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_account_get_account_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_account_get_session_fonts_t vx_message_base_t2vx_req_account_get_session_fonts_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_account_get_session_fonts_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_account_get_session_fonts_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_account_get_template_fonts_t vx_message_base_t2vx_req_account_get_template_fonts_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_account_get_template_fonts_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_account_get_template_fonts_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_account_list_auto_accept_rules_t vx_message_base_t2vx_req_account_list_auto_accept_rules_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_account_list_auto_accept_rules_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_account_list_auto_accept_rules_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_account_list_block_rules_t vx_message_base_t2vx_req_account_list_block_rules_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_account_list_block_rules_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_account_list_block_rules_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_account_list_buddies_and_groups_t vx_message_base_t2vx_req_account_list_buddies_and_groups_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_account_list_buddies_and_groups_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_account_list_buddies_and_groups_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_account_login_t vx_message_base_t2vx_req_account_login_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_account_login_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_account_login_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_account_logout_t vx_message_base_t2vx_req_account_logout_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_account_logout_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_account_logout_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_account_post_crash_dump_t vx_message_base_t2vx_req_account_post_crash_dump_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_account_post_crash_dump_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_account_post_crash_dump_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_account_send_message_t vx_message_base_t2vx_req_account_send_message_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_account_send_message_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_account_send_message_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_account_send_sms_t vx_message_base_t2vx_req_account_send_sms_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_account_send_sms_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_account_send_sms_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_account_send_subscription_reply_t vx_message_base_t2vx_req_account_send_subscription_reply_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_account_send_subscription_reply_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_account_send_subscription_reply_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_account_send_user_app_data_t vx_message_base_t2vx_req_account_send_user_app_data_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_account_send_user_app_data_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_account_send_user_app_data_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_account_set_login_properties_t vx_message_base_t2vx_req_account_set_login_properties_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_account_set_login_properties_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_account_set_login_properties_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_account_set_presence_t vx_message_base_t2vx_req_account_set_presence_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_account_set_presence_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_account_set_presence_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_account_update_account_t vx_message_base_t2vx_req_account_update_account_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_account_update_account_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_account_update_account_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_account_web_call_t vx_message_base_t2vx_req_account_web_call_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_account_web_call_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_account_web_call_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_aux_capture_audio_start_t vx_message_base_t2vx_req_aux_capture_audio_start_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_aux_capture_audio_start_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_aux_capture_audio_start_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_aux_capture_audio_stop_t vx_message_base_t2vx_req_aux_capture_audio_stop_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_aux_capture_audio_stop_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_aux_capture_audio_stop_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_aux_connectivity_info_t vx_message_base_t2vx_req_aux_connectivity_info_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_aux_connectivity_info_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_aux_connectivity_info_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_aux_create_account_t vx_message_base_t2vx_req_aux_create_account_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_aux_create_account_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_aux_create_account_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_aux_deactivate_account_t vx_message_base_t2vx_req_aux_deactivate_account_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_aux_deactivate_account_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_aux_deactivate_account_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_aux_get_capture_devices_t vx_message_base_t2vx_req_aux_get_capture_devices_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_aux_get_capture_devices_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_aux_get_capture_devices_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_aux_get_mic_level_t vx_message_base_t2vx_req_aux_get_mic_level_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_aux_get_mic_level_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_aux_get_mic_level_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_aux_get_render_devices_t vx_message_base_t2vx_req_aux_get_render_devices_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_aux_get_render_devices_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_aux_get_render_devices_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_aux_get_speaker_level_t vx_message_base_t2vx_req_aux_get_speaker_level_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_aux_get_speaker_level_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_aux_get_speaker_level_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_aux_get_vad_properties_t vx_message_base_t2vx_req_aux_get_vad_properties_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_aux_get_vad_properties_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_aux_get_vad_properties_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_aux_global_monitor_keyboard_mouse_t vx_message_base_t2vx_req_aux_global_monitor_keyboard_mouse_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_aux_global_monitor_keyboard_mouse_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_aux_global_monitor_keyboard_mouse_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_aux_play_audio_buffer_t vx_message_base_t2vx_req_aux_play_audio_buffer_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_aux_play_audio_buffer_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_aux_play_audio_buffer_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_aux_reactivate_account_t vx_message_base_t2vx_req_aux_reactivate_account_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_aux_reactivate_account_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_aux_reactivate_account_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_aux_render_audio_modify_t vx_message_base_t2vx_req_aux_render_audio_modify_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_aux_render_audio_modify_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_aux_render_audio_modify_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_aux_render_audio_start_t vx_message_base_t2vx_req_aux_render_audio_start_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_aux_render_audio_start_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_aux_render_audio_start_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_aux_render_audio_stop_t vx_message_base_t2vx_req_aux_render_audio_stop_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_aux_render_audio_stop_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_aux_render_audio_stop_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_aux_reset_password_t vx_message_base_t2vx_req_aux_reset_password_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_aux_reset_password_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_aux_reset_password_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_aux_set_capture_device_t vx_message_base_t2vx_req_aux_set_capture_device_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_aux_set_capture_device_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_aux_set_capture_device_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_aux_set_idle_timeout_t vx_message_base_t2vx_req_aux_set_idle_timeout_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_aux_set_idle_timeout_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_aux_set_idle_timeout_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_aux_set_mic_level_t vx_message_base_t2vx_req_aux_set_mic_level_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_aux_set_mic_level_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_aux_set_mic_level_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_aux_set_render_device_t vx_message_base_t2vx_req_aux_set_render_device_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_aux_set_render_device_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_aux_set_render_device_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_aux_set_speaker_level_t vx_message_base_t2vx_req_aux_set_speaker_level_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_aux_set_speaker_level_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_aux_set_speaker_level_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_aux_set_vad_properties_t vx_message_base_t2vx_req_aux_set_vad_properties_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_aux_set_vad_properties_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_aux_set_vad_properties_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_aux_start_buffer_capture_t vx_message_base_t2vx_req_aux_start_buffer_capture_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_aux_start_buffer_capture_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_aux_start_buffer_capture_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_channel_ban_user_t vx_message_base_t2vx_req_channel_ban_user_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_channel_ban_user_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_channel_ban_user_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_channel_get_banned_users_t vx_message_base_t2vx_req_channel_get_banned_users_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_channel_get_banned_users_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_channel_get_banned_users_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_channel_kick_user_t vx_message_base_t2vx_req_channel_kick_user_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_channel_kick_user_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_channel_kick_user_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_channel_mute_all_users_t vx_message_base_t2vx_req_channel_mute_all_users_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_channel_mute_all_users_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_channel_mute_all_users_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_channel_mute_user_t vx_message_base_t2vx_req_channel_mute_user_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_channel_mute_user_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_channel_mute_user_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_channel_set_lock_mode_t vx_message_base_t2vx_req_channel_set_lock_mode_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_channel_set_lock_mode_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_channel_set_lock_mode_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_connector_create_t vx_message_base_t2vx_req_connector_create_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_connector_create_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_connector_create_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_connector_get_local_audio_info_t vx_message_base_t2vx_req_connector_get_local_audio_info_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_connector_get_local_audio_info_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_connector_get_local_audio_info_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_connector_initiate_shutdown_t vx_message_base_t2vx_req_connector_initiate_shutdown_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_connector_initiate_shutdown_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_connector_initiate_shutdown_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_connector_mute_local_mic_t vx_message_base_t2vx_req_connector_mute_local_mic_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_connector_mute_local_mic_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_connector_mute_local_mic_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_connector_mute_local_speaker_t vx_message_base_t2vx_req_connector_mute_local_speaker_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_connector_mute_local_speaker_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_connector_mute_local_speaker_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_connector_set_local_mic_volume_t vx_message_base_t2vx_req_connector_set_local_mic_volume_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_connector_set_local_mic_volume_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_connector_set_local_mic_volume_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_connector_set_local_speaker_volume_t vx_message_base_t2vx_req_connector_set_local_speaker_volume_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_connector_set_local_speaker_volume_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_connector_set_local_speaker_volume_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_session_channel_invite_user_t vx_message_base_t2vx_req_session_channel_invite_user_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_session_channel_invite_user_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_session_channel_invite_user_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_session_create_t vx_message_base_t2vx_req_session_create_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_session_create_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_session_create_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_session_media_connect_t vx_message_base_t2vx_req_session_media_connect_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_session_media_connect_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_session_media_connect_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_session_media_disconnect_t vx_message_base_t2vx_req_session_media_disconnect_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_session_media_disconnect_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_session_media_disconnect_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_session_mute_local_speaker_t vx_message_base_t2vx_req_session_mute_local_speaker_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_session_mute_local_speaker_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_session_mute_local_speaker_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_session_send_dtmf_t vx_message_base_t2vx_req_session_send_dtmf_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_session_send_dtmf_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_session_send_dtmf_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_session_send_message_t vx_message_base_t2vx_req_session_send_message_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_session_send_message_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_session_send_message_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_session_send_notification_t vx_message_base_t2vx_req_session_send_notification_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_session_send_notification_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_session_send_notification_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_session_set_3d_position_t vx_message_base_t2vx_req_session_set_3d_position_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_session_set_3d_position_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_session_set_3d_position_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_session_set_local_speaker_volume_t vx_message_base_t2vx_req_session_set_local_speaker_volume_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_session_set_local_speaker_volume_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_session_set_local_speaker_volume_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_session_set_participant_mute_for_me_t vx_message_base_t2vx_req_session_set_participant_mute_for_me_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_session_set_participant_mute_for_me_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_session_set_participant_mute_for_me_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_session_set_participant_volume_for_me_t vx_message_base_t2vx_req_session_set_participant_volume_for_me_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_session_set_participant_volume_for_me_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_session_set_participant_volume_for_me_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_session_set_voice_font_t vx_message_base_t2vx_req_session_set_voice_font_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_session_set_voice_font_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_session_set_voice_font_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_session_terminate_t vx_message_base_t2vx_req_session_terminate_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_session_terminate_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_session_terminate_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_session_text_connect_t vx_message_base_t2vx_req_session_text_connect_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_session_text_connect_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_session_text_connect_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_session_text_disconnect_t vx_message_base_t2vx_req_session_text_disconnect_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_session_text_disconnect_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_session_text_disconnect_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_sessiongroup_add_session_t vx_message_base_t2vx_req_sessiongroup_add_session_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_sessiongroup_add_session_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_sessiongroup_add_session_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_sessiongroup_control_audio_injection_t vx_message_base_t2vx_req_sessiongroup_control_audio_injection_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_sessiongroup_control_audio_injection_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_sessiongroup_control_audio_injection_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_sessiongroup_control_playback_t vx_message_base_t2vx_req_sessiongroup_control_playback_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_sessiongroup_control_playback_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_sessiongroup_control_playback_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_sessiongroup_control_recording_t vx_message_base_t2vx_req_sessiongroup_control_recording_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_sessiongroup_control_recording_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_sessiongroup_control_recording_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_sessiongroup_create_t vx_message_base_t2vx_req_sessiongroup_create_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_sessiongroup_create_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_sessiongroup_create_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_sessiongroup_get_stats_t vx_message_base_t2vx_req_sessiongroup_get_stats_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_sessiongroup_get_stats_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_sessiongroup_get_stats_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_sessiongroup_remove_session_t vx_message_base_t2vx_req_sessiongroup_remove_session_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_sessiongroup_remove_session_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_sessiongroup_remove_session_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_sessiongroup_reset_focus_t vx_message_base_t2vx_req_sessiongroup_reset_focus_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_sessiongroup_reset_focus_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_sessiongroup_reset_focus_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_sessiongroup_set_focus_t vx_message_base_t2vx_req_sessiongroup_set_focus_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_sessiongroup_set_focus_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_sessiongroup_set_focus_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_sessiongroup_set_playback_options_t vx_message_base_t2vx_req_sessiongroup_set_playback_options_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_sessiongroup_set_playback_options_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_sessiongroup_set_playback_options_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_sessiongroup_set_session_3d_position_t vx_message_base_t2vx_req_sessiongroup_set_session_3d_position_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_sessiongroup_set_session_3d_position_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_sessiongroup_set_session_3d_position_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_sessiongroup_set_tx_all_sessions_t vx_message_base_t2vx_req_sessiongroup_set_tx_all_sessions_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_sessiongroup_set_tx_all_sessions_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_sessiongroup_set_tx_all_sessions_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_sessiongroup_set_tx_no_session_t vx_message_base_t2vx_req_sessiongroup_set_tx_no_session_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_sessiongroup_set_tx_no_session_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_sessiongroup_set_tx_no_session_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_sessiongroup_set_tx_session_t vx_message_base_t2vx_req_sessiongroup_set_tx_session_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_sessiongroup_set_tx_session_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_sessiongroup_set_tx_session_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_sessiongroup_terminate_t vx_message_base_t2vx_req_sessiongroup_terminate_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_sessiongroup_terminate_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_sessiongroup_terminate_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_sessiongroup_unset_focus_t vx_message_base_t2vx_req_sessiongroup_unset_focus_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_req_sessiongroup_unset_focus_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_req_sessiongroup_unset_focus_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_account_anonymous_login_t vx_message_base_t2vx_resp_account_anonymous_login_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_account_anonymous_login_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_account_anonymous_login_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_account_authtoken_login_t vx_message_base_t2vx_resp_account_authtoken_login_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_account_authtoken_login_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_account_authtoken_login_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_account_buddy_delete_t vx_message_base_t2vx_resp_account_buddy_delete_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_account_buddy_delete_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_account_buddy_delete_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_account_buddy_search_t vx_message_base_t2vx_resp_account_buddy_search_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_account_buddy_search_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_account_buddy_search_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_account_buddy_set_t vx_message_base_t2vx_resp_account_buddy_set_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_account_buddy_set_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_account_buddy_set_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_account_buddygroup_delete_t vx_message_base_t2vx_resp_account_buddygroup_delete_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_account_buddygroup_delete_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_account_buddygroup_delete_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_account_buddygroup_set_t vx_message_base_t2vx_resp_account_buddygroup_set_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_account_buddygroup_set_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_account_buddygroup_set_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_account_channel_add_acl_t vx_message_base_t2vx_resp_account_channel_add_acl_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_account_channel_add_acl_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_account_channel_add_acl_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_account_channel_add_moderator_t vx_message_base_t2vx_resp_account_channel_add_moderator_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_account_channel_add_moderator_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_account_channel_add_moderator_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_account_channel_change_owner_t vx_message_base_t2vx_resp_account_channel_change_owner_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_account_channel_change_owner_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_account_channel_change_owner_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_account_channel_create_t vx_message_base_t2vx_resp_account_channel_create_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_account_channel_create_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_account_channel_create_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_account_channel_delete_t vx_message_base_t2vx_resp_account_channel_delete_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_account_channel_delete_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_account_channel_delete_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_account_channel_favorite_delete_t vx_message_base_t2vx_resp_account_channel_favorite_delete_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_account_channel_favorite_delete_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_account_channel_favorite_delete_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_account_channel_favorite_group_delete_t vx_message_base_t2vx_resp_account_channel_favorite_group_delete_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_account_channel_favorite_group_delete_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_account_channel_favorite_group_delete_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_account_channel_favorite_group_set_t vx_message_base_t2vx_resp_account_channel_favorite_group_set_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_account_channel_favorite_group_set_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_account_channel_favorite_group_set_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_account_channel_favorite_set_t vx_message_base_t2vx_resp_account_channel_favorite_set_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_account_channel_favorite_set_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_account_channel_favorite_set_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_account_channel_favorites_get_list_t vx_message_base_t2vx_resp_account_channel_favorites_get_list_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_account_channel_favorites_get_list_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_account_channel_favorites_get_list_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_account_channel_get_acl_t vx_message_base_t2vx_resp_account_channel_get_acl_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_account_channel_get_acl_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_account_channel_get_acl_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_account_channel_get_info_t vx_message_base_t2vx_resp_account_channel_get_info_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_account_channel_get_info_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_account_channel_get_info_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_account_channel_get_moderators_t vx_message_base_t2vx_resp_account_channel_get_moderators_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_account_channel_get_moderators_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_account_channel_get_moderators_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_account_channel_get_participants_t vx_message_base_t2vx_resp_account_channel_get_participants_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_account_channel_get_participants_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_account_channel_get_participants_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_account_channel_remove_acl_t vx_message_base_t2vx_resp_account_channel_remove_acl_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_account_channel_remove_acl_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_account_channel_remove_acl_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_account_channel_remove_moderator_t vx_message_base_t2vx_resp_account_channel_remove_moderator_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_account_channel_remove_moderator_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_account_channel_remove_moderator_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_account_channel_search_t vx_message_base_t2vx_resp_account_channel_search_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_account_channel_search_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_account_channel_search_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_account_channel_update_t vx_message_base_t2vx_resp_account_channel_update_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_account_channel_update_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_account_channel_update_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_account_create_auto_accept_rule_t vx_message_base_t2vx_resp_account_create_auto_accept_rule_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_account_create_auto_accept_rule_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_account_create_auto_accept_rule_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_account_create_block_rule_t vx_message_base_t2vx_resp_account_create_block_rule_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_account_create_block_rule_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_account_create_block_rule_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_account_delete_auto_accept_rule_t vx_message_base_t2vx_resp_account_delete_auto_accept_rule_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_account_delete_auto_accept_rule_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_account_delete_auto_accept_rule_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_account_delete_block_rule_t vx_message_base_t2vx_resp_account_delete_block_rule_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_account_delete_block_rule_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_account_delete_block_rule_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_account_get_account_t vx_message_base_t2vx_resp_account_get_account_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_account_get_account_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_account_get_account_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_account_get_session_fonts_t vx_message_base_t2vx_resp_account_get_session_fonts_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_account_get_session_fonts_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_account_get_session_fonts_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_account_get_template_fonts_t vx_message_base_t2vx_resp_account_get_template_fonts_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_account_get_template_fonts_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_account_get_template_fonts_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_account_list_auto_accept_rules_t vx_message_base_t2vx_resp_account_list_auto_accept_rules_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_account_list_auto_accept_rules_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_account_list_auto_accept_rules_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_account_list_block_rules_t vx_message_base_t2vx_resp_account_list_block_rules_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_account_list_block_rules_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_account_list_block_rules_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_account_list_buddies_and_groups_t vx_message_base_t2vx_resp_account_list_buddies_and_groups_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_account_list_buddies_and_groups_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_account_list_buddies_and_groups_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_account_login_t vx_message_base_t2vx_resp_account_login_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_account_login_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_account_login_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_account_logout_t vx_message_base_t2vx_resp_account_logout_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_account_logout_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_account_logout_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_account_post_crash_dump_t vx_message_base_t2vx_resp_account_post_crash_dump_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_account_post_crash_dump_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_account_post_crash_dump_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_account_send_message_t vx_message_base_t2vx_resp_account_send_message_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_account_send_message_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_account_send_message_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_account_send_sms_t vx_message_base_t2vx_resp_account_send_sms_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_account_send_sms_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_account_send_sms_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_account_send_subscription_reply_t vx_message_base_t2vx_resp_account_send_subscription_reply_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_account_send_subscription_reply_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_account_send_subscription_reply_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_account_send_user_app_data_t vx_message_base_t2vx_resp_account_send_user_app_data_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_account_send_user_app_data_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_account_send_user_app_data_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_account_set_login_properties_t vx_message_base_t2vx_resp_account_set_login_properties_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_account_set_login_properties_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_account_set_login_properties_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_account_set_presence_t vx_message_base_t2vx_resp_account_set_presence_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_account_set_presence_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_account_set_presence_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_account_update_account_t vx_message_base_t2vx_resp_account_update_account_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_account_update_account_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_account_update_account_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_account_web_call_t vx_message_base_t2vx_resp_account_web_call_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_account_web_call_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_account_web_call_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_aux_capture_audio_start_t vx_message_base_t2vx_resp_aux_capture_audio_start_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_aux_capture_audio_start_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_aux_capture_audio_start_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_aux_capture_audio_stop_t vx_message_base_t2vx_resp_aux_capture_audio_stop_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_aux_capture_audio_stop_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_aux_capture_audio_stop_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_aux_connectivity_info_t vx_message_base_t2vx_resp_aux_connectivity_info_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_aux_connectivity_info_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_aux_connectivity_info_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_aux_create_account_t vx_message_base_t2vx_resp_aux_create_account_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_aux_create_account_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_aux_create_account_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_aux_deactivate_account_t vx_message_base_t2vx_resp_aux_deactivate_account_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_aux_deactivate_account_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_aux_deactivate_account_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_aux_diagnostic_state_dump_t vx_message_base_t2vx_resp_aux_diagnostic_state_dump_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_aux_diagnostic_state_dump_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_aux_diagnostic_state_dump_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_aux_get_capture_devices_t vx_message_base_t2vx_resp_aux_get_capture_devices_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_aux_get_capture_devices_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_aux_get_capture_devices_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_aux_get_mic_level_t vx_message_base_t2vx_resp_aux_get_mic_level_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_aux_get_mic_level_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_aux_get_mic_level_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_aux_get_render_devices_t vx_message_base_t2vx_resp_aux_get_render_devices_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_aux_get_render_devices_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_aux_get_render_devices_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_aux_get_speaker_level_t vx_message_base_t2vx_resp_aux_get_speaker_level_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_aux_get_speaker_level_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_aux_get_speaker_level_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_aux_get_vad_properties_t vx_message_base_t2vx_resp_aux_get_vad_properties_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_aux_get_vad_properties_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_aux_get_vad_properties_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_aux_global_monitor_keyboard_mouse_t vx_message_base_t2vx_resp_aux_global_monitor_keyboard_mouse_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_aux_global_monitor_keyboard_mouse_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_aux_global_monitor_keyboard_mouse_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_aux_play_audio_buffer_t vx_message_base_t2vx_resp_aux_play_audio_buffer_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_aux_play_audio_buffer_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_aux_play_audio_buffer_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_aux_reactivate_account_t vx_message_base_t2vx_resp_aux_reactivate_account_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_aux_reactivate_account_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_aux_reactivate_account_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_aux_render_audio_modify_t vx_message_base_t2vx_resp_aux_render_audio_modify_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_aux_render_audio_modify_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_aux_render_audio_modify_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_aux_render_audio_start_t vx_message_base_t2vx_resp_aux_render_audio_start_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_aux_render_audio_start_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_aux_render_audio_start_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_aux_render_audio_stop_t vx_message_base_t2vx_resp_aux_render_audio_stop_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_aux_render_audio_stop_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_aux_render_audio_stop_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_aux_reset_password_t vx_message_base_t2vx_resp_aux_reset_password_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_aux_reset_password_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_aux_reset_password_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_aux_set_capture_device_t vx_message_base_t2vx_resp_aux_set_capture_device_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_aux_set_capture_device_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_aux_set_capture_device_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_aux_set_idle_timeout_t vx_message_base_t2vx_resp_aux_set_idle_timeout_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_aux_set_idle_timeout_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_aux_set_idle_timeout_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_aux_set_mic_level_t vx_message_base_t2vx_resp_aux_set_mic_level_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_aux_set_mic_level_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_aux_set_mic_level_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_aux_set_render_device_t vx_message_base_t2vx_resp_aux_set_render_device_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_aux_set_render_device_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_aux_set_render_device_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_aux_set_speaker_level_t vx_message_base_t2vx_resp_aux_set_speaker_level_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_aux_set_speaker_level_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_aux_set_speaker_level_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_aux_set_vad_properties_t vx_message_base_t2vx_resp_aux_set_vad_properties_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_aux_set_vad_properties_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_aux_set_vad_properties_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_aux_start_buffer_capture_t vx_message_base_t2vx_resp_aux_start_buffer_capture_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_aux_start_buffer_capture_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_aux_start_buffer_capture_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_channel_ban_user_t vx_message_base_t2vx_resp_channel_ban_user_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_channel_ban_user_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_channel_ban_user_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_channel_get_banned_users_t vx_message_base_t2vx_resp_channel_get_banned_users_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_channel_get_banned_users_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_channel_get_banned_users_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_channel_kick_user_t vx_message_base_t2vx_resp_channel_kick_user_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_channel_kick_user_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_channel_kick_user_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_channel_mute_all_users_t vx_message_base_t2vx_resp_channel_mute_all_users_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_channel_mute_all_users_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_channel_mute_all_users_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_channel_mute_user_t vx_message_base_t2vx_resp_channel_mute_user_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_channel_mute_user_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_channel_mute_user_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_channel_set_lock_mode_t vx_message_base_t2vx_resp_channel_set_lock_mode_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_channel_set_lock_mode_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_channel_set_lock_mode_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_connector_create_t vx_message_base_t2vx_resp_connector_create_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_connector_create_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_connector_create_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_connector_get_local_audio_info_t vx_message_base_t2vx_resp_connector_get_local_audio_info_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_connector_get_local_audio_info_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_connector_get_local_audio_info_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_connector_initiate_shutdown_t vx_message_base_t2vx_resp_connector_initiate_shutdown_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_connector_initiate_shutdown_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_connector_initiate_shutdown_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_connector_mute_local_mic_t vx_message_base_t2vx_resp_connector_mute_local_mic_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_connector_mute_local_mic_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_connector_mute_local_mic_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_connector_mute_local_speaker_t vx_message_base_t2vx_resp_connector_mute_local_speaker_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_connector_mute_local_speaker_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_connector_mute_local_speaker_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_connector_set_local_mic_volume_t vx_message_base_t2vx_resp_connector_set_local_mic_volume_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_connector_set_local_mic_volume_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_connector_set_local_mic_volume_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_connector_set_local_speaker_volume_t vx_message_base_t2vx_resp_connector_set_local_speaker_volume_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_connector_set_local_speaker_volume_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_connector_set_local_speaker_volume_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_session_channel_invite_user_t vx_message_base_t2vx_resp_session_channel_invite_user_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_session_channel_invite_user_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_session_channel_invite_user_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_session_create_t vx_message_base_t2vx_resp_session_create_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_session_create_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_session_create_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_session_media_connect_t vx_message_base_t2vx_resp_session_media_connect_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_session_media_connect_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_session_media_connect_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_session_media_disconnect_t vx_message_base_t2vx_resp_session_media_disconnect_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_session_media_disconnect_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_session_media_disconnect_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_session_mute_local_speaker_t vx_message_base_t2vx_resp_session_mute_local_speaker_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_session_mute_local_speaker_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_session_mute_local_speaker_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_session_send_dtmf_t vx_message_base_t2vx_resp_session_send_dtmf_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_session_send_dtmf_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_session_send_dtmf_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_session_send_message_t vx_message_base_t2vx_resp_session_send_message_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_session_send_message_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_session_send_message_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_session_send_notification_t vx_message_base_t2vx_resp_session_send_notification_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_session_send_notification_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_session_send_notification_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_session_set_3d_position_t vx_message_base_t2vx_resp_session_set_3d_position_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_session_set_3d_position_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_session_set_3d_position_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_session_set_local_speaker_volume_t vx_message_base_t2vx_resp_session_set_local_speaker_volume_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_session_set_local_speaker_volume_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_session_set_local_speaker_volume_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_session_set_participant_mute_for_me_t vx_message_base_t2vx_resp_session_set_participant_mute_for_me_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_session_set_participant_mute_for_me_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_session_set_participant_mute_for_me_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_session_set_participant_volume_for_me_t vx_message_base_t2vx_resp_session_set_participant_volume_for_me_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_session_set_participant_volume_for_me_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_session_set_participant_volume_for_me_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_session_set_voice_font_t vx_message_base_t2vx_resp_session_set_voice_font_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_session_set_voice_font_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_session_set_voice_font_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_session_terminate_t vx_message_base_t2vx_resp_session_terminate_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_session_terminate_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_session_terminate_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_session_text_connect_t vx_message_base_t2vx_resp_session_text_connect_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_session_text_connect_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_session_text_connect_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_session_text_disconnect_t vx_message_base_t2vx_resp_session_text_disconnect_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_session_text_disconnect_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_session_text_disconnect_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_sessiongroup_add_session_t vx_message_base_t2vx_resp_sessiongroup_add_session_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_sessiongroup_add_session_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_sessiongroup_add_session_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_sessiongroup_control_audio_injection_t vx_message_base_t2vx_resp_sessiongroup_control_audio_injection_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_sessiongroup_control_audio_injection_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_sessiongroup_control_audio_injection_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_sessiongroup_control_playback_t vx_message_base_t2vx_resp_sessiongroup_control_playback_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_sessiongroup_control_playback_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_sessiongroup_control_playback_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_sessiongroup_control_recording_t vx_message_base_t2vx_resp_sessiongroup_control_recording_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_sessiongroup_control_recording_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_sessiongroup_control_recording_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_sessiongroup_create_t vx_message_base_t2vx_resp_sessiongroup_create_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_sessiongroup_create_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_sessiongroup_create_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_sessiongroup_get_stats_t vx_message_base_t2vx_resp_sessiongroup_get_stats_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_sessiongroup_get_stats_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_sessiongroup_get_stats_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_sessiongroup_remove_session_t vx_message_base_t2vx_resp_sessiongroup_remove_session_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_sessiongroup_remove_session_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_sessiongroup_remove_session_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_sessiongroup_reset_focus_t vx_message_base_t2vx_resp_sessiongroup_reset_focus_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_sessiongroup_reset_focus_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_sessiongroup_reset_focus_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_sessiongroup_set_focus_t vx_message_base_t2vx_resp_sessiongroup_set_focus_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_sessiongroup_set_focus_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_sessiongroup_set_focus_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_sessiongroup_set_playback_options_t vx_message_base_t2vx_resp_sessiongroup_set_playback_options_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_sessiongroup_set_playback_options_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_sessiongroup_set_playback_options_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_sessiongroup_set_session_3d_position_t vx_message_base_t2vx_resp_sessiongroup_set_session_3d_position_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_sessiongroup_set_session_3d_position_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_sessiongroup_set_session_3d_position_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_sessiongroup_set_tx_all_sessions_t vx_message_base_t2vx_resp_sessiongroup_set_tx_all_sessions_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_sessiongroup_set_tx_all_sessions_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_sessiongroup_set_tx_all_sessions_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_sessiongroup_set_tx_no_session_t vx_message_base_t2vx_resp_sessiongroup_set_tx_no_session_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_sessiongroup_set_tx_no_session_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_sessiongroup_set_tx_no_session_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_sessiongroup_set_tx_session_t vx_message_base_t2vx_resp_sessiongroup_set_tx_session_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_sessiongroup_set_tx_session_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_sessiongroup_set_tx_session_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_sessiongroup_terminate_t vx_message_base_t2vx_resp_sessiongroup_terminate_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_sessiongroup_terminate_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_sessiongroup_terminate_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_resp_sessiongroup_unset_focus_t vx_message_base_t2vx_resp_sessiongroup_unset_focus_t(vx_message_base_t object) {
        var l: Long = VxClientProxyJNI.vx_message_base_t2vx_resp_sessiongroup_unset_focus_t(vx_message_base_t.getCPtr((vx_message_base_t)object), (vx_message_base_t)object)
        if (l != 0L) return vx_resp_sessiongroup_unset_focus_t(l, false)
        return null
    }

    Unit vx_name_value_pair_create(SWIGTYPE_p_p_vx_name_value_pair sWIGTYPE_p_p_vx_name_value_pair) {
        VxClientProxyJNI.vx_name_value_pair_create(SWIGTYPE_p_p_vx_name_value_pair.getCPtr(sWIGTYPE_p_p_vx_name_value_pair))
    }

    Unit vx_name_value_pair_free(vx_name_value_pair_t vx_name_value_pair_t2) {
        VxClientProxyJNI.vx_name_value_pair_free(vx_name_value_pair_t.getCPtr(vx_name_value_pair_t2), vx_name_value_pair_t2)
    }

    Unit vx_name_value_pairs_create(Int n, SWIGTYPE_p_p_p_vx_name_value_pair sWIGTYPE_p_p_p_vx_name_value_pair) {
        VxClientProxyJNI.vx_name_value_pairs_create(n, SWIGTYPE_p_p_p_vx_name_value_pair.getCPtr(sWIGTYPE_p_p_p_vx_name_value_pair))
    }

    Unit vx_name_value_pairs_free(SWIGTYPE_p_p_vx_name_value_pair sWIGTYPE_p_p_vx_name_value_pair, Int n) {
        VxClientProxyJNI.vx_name_value_pairs_free(SWIGTYPE_p_p_vx_name_value_pair.getCPtr(sWIGTYPE_p_p_vx_name_value_pair), n)
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_name_value_pair_t vx_name_value_pairs_t_get_list_item(SWIGTYPE_p_p_vx_name_value_pair object, Int n) {
        var l: Long = VxClientProxyJNI.vx_name_value_pairs_t_get_list_item(SWIGTYPE_p_p_vx_name_value_pair.getCPtr((SWIGTYPE_p_p_vx_name_value_pair)object), n)
        if (l != 0L) return vx_name_value_pair_t(l, false)
        return null
    }

    Unit vx_name_value_pairs_t_set_list_item(SWIGTYPE_p_p_vx_name_value_pair sWIGTYPE_p_p_vx_name_value_pair, Int n, vx_name_value_pair_t vx_name_value_pair_t2) {
        VxClientProxyJNI.vx_name_value_pairs_t_set_list_item(SWIGTYPE_p_p_vx_name_value_pair.getCPtr(sWIGTYPE_p_p_vx_name_value_pair), n, vx_name_value_pair_t.getCPtr(vx_name_value_pair_t2), vx_name_value_pair_t2)
    }

    Unit vx_on_application_exit() {
        VxClientProxyJNI.vx_on_application_exit()
    }

    Unit vx_participant_create(SWIGTYPE_p_p_vx_participant sWIGTYPE_p_p_vx_participant) {
        VxClientProxyJNI.vx_participant_create(SWIGTYPE_p_p_vx_participant.getCPtr(sWIGTYPE_p_p_vx_participant))
    }

    Unit vx_participant_free(vx_participant_t vx_participant_t2) {
        VxClientProxyJNI.vx_participant_free(vx_participant_t.getCPtr(vx_participant_t2), vx_participant_t2)
    }

    Unit vx_participant_list_create(Int n, SWIGTYPE_p_p_p_vx_participant sWIGTYPE_p_p_p_vx_participant) {
        VxClientProxyJNI.vx_participant_list_create(n, SWIGTYPE_p_p_p_vx_participant.getCPtr(sWIGTYPE_p_p_p_vx_participant))
    }

    Unit vx_participant_list_free(SWIGTYPE_p_p_vx_participant sWIGTYPE_p_p_vx_participant, Int n) {
        VxClientProxyJNI.vx_participant_list_free(SWIGTYPE_p_p_vx_participant.getCPtr(sWIGTYPE_p_p_vx_participant), n)
    }

    String vx_read_crash_dump(Int n) {
        return VxClientProxyJNI.vx_read_crash_dump(n)
    }

    Unit vx_recording_frame_create(SWIGTYPE_p_p_vx_recording_frame sWIGTYPE_p_p_vx_recording_frame) {
        VxClientProxyJNI.vx_recording_frame_create(SWIGTYPE_p_p_vx_recording_frame.getCPtr(sWIGTYPE_p_p_vx_recording_frame))
    }

    Unit vx_recording_frame_free(vx_recording_frame_t vx_recording_frame_t2) {
        VxClientProxyJNI.vx_recording_frame_free(vx_recording_frame_t.getCPtr(vx_recording_frame_t2), vx_recording_frame_t2)
    }

    Unit vx_recording_frame_list_create(Int n, SWIGTYPE_p_p_p_vx_recording_frame sWIGTYPE_p_p_p_vx_recording_frame) {
        VxClientProxyJNI.vx_recording_frame_list_create(n, SWIGTYPE_p_p_p_vx_recording_frame.getCPtr(sWIGTYPE_p_p_p_vx_recording_frame))
    }

    Unit vx_recording_frame_list_free(SWIGTYPE_p_p_vx_recording_frame sWIGTYPE_p_p_vx_recording_frame, Int n) {
        VxClientProxyJNI.vx_recording_frame_list_free(SWIGTYPE_p_p_vx_recording_frame.getCPtr(sWIGTYPE_p_p_vx_recording_frame), n)
    }

    Unit vx_register_logging_initialization(vx_log_type vx_log_type2, String string2, String string3, String string4, Int n, SWIGTYPE_p_f_p_q_const__char_p_q_const__char_p_q_const__char__void sWIGTYPE_p_f_p_q_const__char_p_q_const__char_p_q_const__char__void) {
        VxClientProxyJNI.vx_register_logging_initialization(vx_log_type2.swigValue(), string2, string3, string4, n, SWIGTYPE_p_f_p_q_const__char_p_q_const__char_p_q_const__char__void.getCPtr(sWIGTYPE_p_f_p_q_const__char_p_q_const__char_p_q_const__char__void))
    }

    Unit vx_register_message_notification_handler(SWIGTYPE_p_f_p_void__void sWIGTYPE_p_f_p_void__void, SWIGTYPE_p_void sWIGTYPE_p_void) {
        VxClientProxyJNI.vx_register_message_notification_handler(SWIGTYPE_p_f_p_void__void.getCPtr(sWIGTYPE_p_f_p_void__void), SWIGTYPE_p_void.getCPtr(sWIGTYPE_p_void))
    }

    Unit vx_req_account_anonymous_login_create(SWIGTYPE_p_p_vx_req_account_anonymous_login sWIGTYPE_p_p_vx_req_account_anonymous_login) {
        VxClientProxyJNI.vx_req_account_anonymous_login_create(SWIGTYPE_p_p_vx_req_account_anonymous_login.getCPtr(sWIGTYPE_p_p_vx_req_account_anonymous_login))
    }

    Unit vx_req_account_authtoken_login_create(SWIGTYPE_p_p_vx_req_account_authtoken_login sWIGTYPE_p_p_vx_req_account_authtoken_login) {
        VxClientProxyJNI.vx_req_account_authtoken_login_create(SWIGTYPE_p_p_vx_req_account_authtoken_login.getCPtr(sWIGTYPE_p_p_vx_req_account_authtoken_login))
    }

    Unit vx_req_account_buddy_delete_create(SWIGTYPE_p_p_vx_req_account_buddy_delete sWIGTYPE_p_p_vx_req_account_buddy_delete) {
        VxClientProxyJNI.vx_req_account_buddy_delete_create(SWIGTYPE_p_p_vx_req_account_buddy_delete.getCPtr(sWIGTYPE_p_p_vx_req_account_buddy_delete))
    }

    Unit vx_req_account_buddy_search_create(SWIGTYPE_p_p_vx_req_account_buddy_search sWIGTYPE_p_p_vx_req_account_buddy_search) {
        VxClientProxyJNI.vx_req_account_buddy_search_create(SWIGTYPE_p_p_vx_req_account_buddy_search.getCPtr(sWIGTYPE_p_p_vx_req_account_buddy_search))
    }

    Unit vx_req_account_buddy_set_create(SWIGTYPE_p_p_vx_req_account_buddy_set sWIGTYPE_p_p_vx_req_account_buddy_set) {
        VxClientProxyJNI.vx_req_account_buddy_set_create(SWIGTYPE_p_p_vx_req_account_buddy_set.getCPtr(sWIGTYPE_p_p_vx_req_account_buddy_set))
    }

    Unit vx_req_account_buddygroup_delete_create(SWIGTYPE_p_p_vx_req_account_buddygroup_delete sWIGTYPE_p_p_vx_req_account_buddygroup_delete) {
        VxClientProxyJNI.vx_req_account_buddygroup_delete_create(SWIGTYPE_p_p_vx_req_account_buddygroup_delete.getCPtr(sWIGTYPE_p_p_vx_req_account_buddygroup_delete))
    }

    Unit vx_req_account_buddygroup_set_create(SWIGTYPE_p_p_vx_req_account_buddygroup_set sWIGTYPE_p_p_vx_req_account_buddygroup_set) {
        VxClientProxyJNI.vx_req_account_buddygroup_set_create(SWIGTYPE_p_p_vx_req_account_buddygroup_set.getCPtr(sWIGTYPE_p_p_vx_req_account_buddygroup_set))
    }

    Unit vx_req_account_channel_add_acl_create(SWIGTYPE_p_p_vx_req_account_channel_add_acl sWIGTYPE_p_p_vx_req_account_channel_add_acl) {
        VxClientProxyJNI.vx_req_account_channel_add_acl_create(SWIGTYPE_p_p_vx_req_account_channel_add_acl.getCPtr(sWIGTYPE_p_p_vx_req_account_channel_add_acl))
    }

    Unit vx_req_account_channel_add_moderator_create(SWIGTYPE_p_p_vx_req_account_channel_add_moderator sWIGTYPE_p_p_vx_req_account_channel_add_moderator) {
        VxClientProxyJNI.vx_req_account_channel_add_moderator_create(SWIGTYPE_p_p_vx_req_account_channel_add_moderator.getCPtr(sWIGTYPE_p_p_vx_req_account_channel_add_moderator))
    }

    Unit vx_req_account_channel_change_owner_create(SWIGTYPE_p_p_vx_req_account_channel_change_owner sWIGTYPE_p_p_vx_req_account_channel_change_owner) {
        VxClientProxyJNI.vx_req_account_channel_change_owner_create(SWIGTYPE_p_p_vx_req_account_channel_change_owner.getCPtr(sWIGTYPE_p_p_vx_req_account_channel_change_owner))
    }

    Unit vx_req_account_channel_create_create(SWIGTYPE_p_p_vx_req_account_channel_create sWIGTYPE_p_p_vx_req_account_channel_create) {
        VxClientProxyJNI.vx_req_account_channel_create_create(SWIGTYPE_p_p_vx_req_account_channel_create.getCPtr(sWIGTYPE_p_p_vx_req_account_channel_create))
    }

    Unit vx_req_account_channel_delete_create(SWIGTYPE_p_p_vx_req_account_channel_delete sWIGTYPE_p_p_vx_req_account_channel_delete) {
        VxClientProxyJNI.vx_req_account_channel_delete_create(SWIGTYPE_p_p_vx_req_account_channel_delete.getCPtr(sWIGTYPE_p_p_vx_req_account_channel_delete))
    }

    Unit vx_req_account_channel_favorite_delete_create(SWIGTYPE_p_p_vx_req_account_channel_favorite_delete sWIGTYPE_p_p_vx_req_account_channel_favorite_delete) {
        VxClientProxyJNI.vx_req_account_channel_favorite_delete_create(SWIGTYPE_p_p_vx_req_account_channel_favorite_delete.getCPtr(sWIGTYPE_p_p_vx_req_account_channel_favorite_delete))
    }

    Unit vx_req_account_channel_favorite_group_delete_create(SWIGTYPE_p_p_vx_req_account_channel_favorite_group_delete sWIGTYPE_p_p_vx_req_account_channel_favorite_group_delete) {
        VxClientProxyJNI.vx_req_account_channel_favorite_group_delete_create(SWIGTYPE_p_p_vx_req_account_channel_favorite_group_delete.getCPtr(sWIGTYPE_p_p_vx_req_account_channel_favorite_group_delete))
    }

    Unit vx_req_account_channel_favorite_group_set_create(SWIGTYPE_p_p_vx_req_account_channel_favorite_group_set sWIGTYPE_p_p_vx_req_account_channel_favorite_group_set) {
        VxClientProxyJNI.vx_req_account_channel_favorite_group_set_create(SWIGTYPE_p_p_vx_req_account_channel_favorite_group_set.getCPtr(sWIGTYPE_p_p_vx_req_account_channel_favorite_group_set))
    }

    Unit vx_req_account_channel_favorite_set_create(SWIGTYPE_p_p_vx_req_account_channel_favorite_set sWIGTYPE_p_p_vx_req_account_channel_favorite_set) {
        VxClientProxyJNI.vx_req_account_channel_favorite_set_create(SWIGTYPE_p_p_vx_req_account_channel_favorite_set.getCPtr(sWIGTYPE_p_p_vx_req_account_channel_favorite_set))
    }

    Unit vx_req_account_channel_favorites_get_list_create(SWIGTYPE_p_p_vx_req_account_channel_favorites_get_list sWIGTYPE_p_p_vx_req_account_channel_favorites_get_list) {
        VxClientProxyJNI.vx_req_account_channel_favorites_get_list_create(SWIGTYPE_p_p_vx_req_account_channel_favorites_get_list.getCPtr(sWIGTYPE_p_p_vx_req_account_channel_favorites_get_list))
    }

    Unit vx_req_account_channel_get_acl_create(SWIGTYPE_p_p_vx_req_account_channel_get_acl sWIGTYPE_p_p_vx_req_account_channel_get_acl) {
        VxClientProxyJNI.vx_req_account_channel_get_acl_create(SWIGTYPE_p_p_vx_req_account_channel_get_acl.getCPtr(sWIGTYPE_p_p_vx_req_account_channel_get_acl))
    }

    Unit vx_req_account_channel_get_info_create(SWIGTYPE_p_p_vx_req_account_channel_get_info sWIGTYPE_p_p_vx_req_account_channel_get_info) {
        VxClientProxyJNI.vx_req_account_channel_get_info_create(SWIGTYPE_p_p_vx_req_account_channel_get_info.getCPtr(sWIGTYPE_p_p_vx_req_account_channel_get_info))
    }

    Unit vx_req_account_channel_get_moderators_create(SWIGTYPE_p_p_vx_req_account_channel_get_moderators sWIGTYPE_p_p_vx_req_account_channel_get_moderators) {
        VxClientProxyJNI.vx_req_account_channel_get_moderators_create(SWIGTYPE_p_p_vx_req_account_channel_get_moderators.getCPtr(sWIGTYPE_p_p_vx_req_account_channel_get_moderators))
    }

    Unit vx_req_account_channel_get_participants_create(SWIGTYPE_p_p_vx_req_account_channel_get_participants sWIGTYPE_p_p_vx_req_account_channel_get_participants) {
        VxClientProxyJNI.vx_req_account_channel_get_participants_create(SWIGTYPE_p_p_vx_req_account_channel_get_participants.getCPtr(sWIGTYPE_p_p_vx_req_account_channel_get_participants))
    }

    Unit vx_req_account_channel_remove_acl_create(SWIGTYPE_p_p_vx_req_account_channel_remove_acl sWIGTYPE_p_p_vx_req_account_channel_remove_acl) {
        VxClientProxyJNI.vx_req_account_channel_remove_acl_create(SWIGTYPE_p_p_vx_req_account_channel_remove_acl.getCPtr(sWIGTYPE_p_p_vx_req_account_channel_remove_acl))
    }

    Unit vx_req_account_channel_remove_moderator_create(SWIGTYPE_p_p_vx_req_account_channel_remove_moderator sWIGTYPE_p_p_vx_req_account_channel_remove_moderator) {
        VxClientProxyJNI.vx_req_account_channel_remove_moderator_create(SWIGTYPE_p_p_vx_req_account_channel_remove_moderator.getCPtr(sWIGTYPE_p_p_vx_req_account_channel_remove_moderator))
    }

    Unit vx_req_account_channel_search_create(SWIGTYPE_p_p_vx_req_account_channel_search sWIGTYPE_p_p_vx_req_account_channel_search) {
        VxClientProxyJNI.vx_req_account_channel_search_create(SWIGTYPE_p_p_vx_req_account_channel_search.getCPtr(sWIGTYPE_p_p_vx_req_account_channel_search))
    }

    Unit vx_req_account_channel_update_create(SWIGTYPE_p_p_vx_req_account_channel_update sWIGTYPE_p_p_vx_req_account_channel_update) {
        VxClientProxyJNI.vx_req_account_channel_update_create(SWIGTYPE_p_p_vx_req_account_channel_update.getCPtr(sWIGTYPE_p_p_vx_req_account_channel_update))
    }

    Unit vx_req_account_create_auto_accept_rule_create(SWIGTYPE_p_p_vx_req_account_create_auto_accept_rule sWIGTYPE_p_p_vx_req_account_create_auto_accept_rule) {
        VxClientProxyJNI.vx_req_account_create_auto_accept_rule_create(SWIGTYPE_p_p_vx_req_account_create_auto_accept_rule.getCPtr(sWIGTYPE_p_p_vx_req_account_create_auto_accept_rule))
    }

    Unit vx_req_account_create_block_rule_create(SWIGTYPE_p_p_vx_req_account_create_block_rule sWIGTYPE_p_p_vx_req_account_create_block_rule) {
        VxClientProxyJNI.vx_req_account_create_block_rule_create(SWIGTYPE_p_p_vx_req_account_create_block_rule.getCPtr(sWIGTYPE_p_p_vx_req_account_create_block_rule))
    }

    Unit vx_req_account_delete_auto_accept_rule_create(SWIGTYPE_p_p_vx_req_account_delete_auto_accept_rule sWIGTYPE_p_p_vx_req_account_delete_auto_accept_rule) {
        VxClientProxyJNI.vx_req_account_delete_auto_accept_rule_create(SWIGTYPE_p_p_vx_req_account_delete_auto_accept_rule.getCPtr(sWIGTYPE_p_p_vx_req_account_delete_auto_accept_rule))
    }

    Unit vx_req_account_delete_block_rule_create(SWIGTYPE_p_p_vx_req_account_delete_block_rule sWIGTYPE_p_p_vx_req_account_delete_block_rule) {
        VxClientProxyJNI.vx_req_account_delete_block_rule_create(SWIGTYPE_p_p_vx_req_account_delete_block_rule.getCPtr(sWIGTYPE_p_p_vx_req_account_delete_block_rule))
    }

    Unit vx_req_account_get_account_create(SWIGTYPE_p_p_vx_req_account_get_account sWIGTYPE_p_p_vx_req_account_get_account) {
        VxClientProxyJNI.vx_req_account_get_account_create(SWIGTYPE_p_p_vx_req_account_get_account.getCPtr(sWIGTYPE_p_p_vx_req_account_get_account))
    }

    Unit vx_req_account_get_session_fonts_create(SWIGTYPE_p_p_vx_req_account_get_session_fonts sWIGTYPE_p_p_vx_req_account_get_session_fonts) {
        VxClientProxyJNI.vx_req_account_get_session_fonts_create(SWIGTYPE_p_p_vx_req_account_get_session_fonts.getCPtr(sWIGTYPE_p_p_vx_req_account_get_session_fonts))
    }

    Unit vx_req_account_get_template_fonts_create(SWIGTYPE_p_p_vx_req_account_get_template_fonts sWIGTYPE_p_p_vx_req_account_get_template_fonts) {
        VxClientProxyJNI.vx_req_account_get_template_fonts_create(SWIGTYPE_p_p_vx_req_account_get_template_fonts.getCPtr(sWIGTYPE_p_p_vx_req_account_get_template_fonts))
    }

    Unit vx_req_account_list_auto_accept_rules_create(SWIGTYPE_p_p_vx_req_account_list_auto_accept_rules sWIGTYPE_p_p_vx_req_account_list_auto_accept_rules) {
        VxClientProxyJNI.vx_req_account_list_auto_accept_rules_create(SWIGTYPE_p_p_vx_req_account_list_auto_accept_rules.getCPtr(sWIGTYPE_p_p_vx_req_account_list_auto_accept_rules))
    }

    Unit vx_req_account_list_block_rules_create(SWIGTYPE_p_p_vx_req_account_list_block_rules sWIGTYPE_p_p_vx_req_account_list_block_rules) {
        VxClientProxyJNI.vx_req_account_list_block_rules_create(SWIGTYPE_p_p_vx_req_account_list_block_rules.getCPtr(sWIGTYPE_p_p_vx_req_account_list_block_rules))
    }

    Unit vx_req_account_list_buddies_and_groups_create(SWIGTYPE_p_p_vx_req_account_list_buddies_and_groups sWIGTYPE_p_p_vx_req_account_list_buddies_and_groups) {
        VxClientProxyJNI.vx_req_account_list_buddies_and_groups_create(SWIGTYPE_p_p_vx_req_account_list_buddies_and_groups.getCPtr(sWIGTYPE_p_p_vx_req_account_list_buddies_and_groups))
    }

    Unit vx_req_account_login_create(SWIGTYPE_p_p_vx_req_account_login sWIGTYPE_p_p_vx_req_account_login) {
        VxClientProxyJNI.vx_req_account_login_create(SWIGTYPE_p_p_vx_req_account_login.getCPtr(sWIGTYPE_p_p_vx_req_account_login))
    }

    Unit vx_req_account_logout_create(SWIGTYPE_p_p_vx_req_account_logout sWIGTYPE_p_p_vx_req_account_logout) {
        VxClientProxyJNI.vx_req_account_logout_create(SWIGTYPE_p_p_vx_req_account_logout.getCPtr(sWIGTYPE_p_p_vx_req_account_logout))
    }

    Unit vx_req_account_post_crash_dump_create(SWIGTYPE_p_p_vx_req_account_post_crash_dump sWIGTYPE_p_p_vx_req_account_post_crash_dump) {
        VxClientProxyJNI.vx_req_account_post_crash_dump_create(SWIGTYPE_p_p_vx_req_account_post_crash_dump.getCPtr(sWIGTYPE_p_p_vx_req_account_post_crash_dump))
    }

    Unit vx_req_account_send_message_create(SWIGTYPE_p_p_vx_req_account_send_message sWIGTYPE_p_p_vx_req_account_send_message) {
        VxClientProxyJNI.vx_req_account_send_message_create(SWIGTYPE_p_p_vx_req_account_send_message.getCPtr(sWIGTYPE_p_p_vx_req_account_send_message))
    }

    Unit vx_req_account_send_sms_create(SWIGTYPE_p_p_vx_req_account_send_sms sWIGTYPE_p_p_vx_req_account_send_sms) {
        VxClientProxyJNI.vx_req_account_send_sms_create(SWIGTYPE_p_p_vx_req_account_send_sms.getCPtr(sWIGTYPE_p_p_vx_req_account_send_sms))
    }

    Unit vx_req_account_send_subscription_reply_create(SWIGTYPE_p_p_vx_req_account_send_subscription_reply sWIGTYPE_p_p_vx_req_account_send_subscription_reply) {
        VxClientProxyJNI.vx_req_account_send_subscription_reply_create(SWIGTYPE_p_p_vx_req_account_send_subscription_reply.getCPtr(sWIGTYPE_p_p_vx_req_account_send_subscription_reply))
    }

    Unit vx_req_account_send_user_app_data_create(SWIGTYPE_p_p_vx_req_account_send_user_app_data sWIGTYPE_p_p_vx_req_account_send_user_app_data) {
        VxClientProxyJNI.vx_req_account_send_user_app_data_create(SWIGTYPE_p_p_vx_req_account_send_user_app_data.getCPtr(sWIGTYPE_p_p_vx_req_account_send_user_app_data))
    }

    Unit vx_req_account_set_login_properties_create(SWIGTYPE_p_p_vx_req_account_set_login_properties sWIGTYPE_p_p_vx_req_account_set_login_properties) {
        VxClientProxyJNI.vx_req_account_set_login_properties_create(SWIGTYPE_p_p_vx_req_account_set_login_properties.getCPtr(sWIGTYPE_p_p_vx_req_account_set_login_properties))
    }

    Unit vx_req_account_set_presence_create(SWIGTYPE_p_p_vx_req_account_set_presence sWIGTYPE_p_p_vx_req_account_set_presence) {
        VxClientProxyJNI.vx_req_account_set_presence_create(SWIGTYPE_p_p_vx_req_account_set_presence.getCPtr(sWIGTYPE_p_p_vx_req_account_set_presence))
    }

    Unit vx_req_account_update_account_create(SWIGTYPE_p_p_vx_req_account_update_account sWIGTYPE_p_p_vx_req_account_update_account) {
        VxClientProxyJNI.vx_req_account_update_account_create(SWIGTYPE_p_p_vx_req_account_update_account.getCPtr(sWIGTYPE_p_p_vx_req_account_update_account))
    }

    Unit vx_req_account_web_call_create(SWIGTYPE_p_p_vx_req_account_web_call sWIGTYPE_p_p_vx_req_account_web_call) {
        VxClientProxyJNI.vx_req_account_web_call_create(SWIGTYPE_p_p_vx_req_account_web_call.getCPtr(sWIGTYPE_p_p_vx_req_account_web_call))
    }

    Unit vx_req_aux_capture_audio_start_create(SWIGTYPE_p_p_vx_req_aux_capture_audio_start sWIGTYPE_p_p_vx_req_aux_capture_audio_start) {
        VxClientProxyJNI.vx_req_aux_capture_audio_start_create(SWIGTYPE_p_p_vx_req_aux_capture_audio_start.getCPtr(sWIGTYPE_p_p_vx_req_aux_capture_audio_start))
    }

    Unit vx_req_aux_capture_audio_stop_create(SWIGTYPE_p_p_vx_req_aux_capture_audio_stop sWIGTYPE_p_p_vx_req_aux_capture_audio_stop) {
        VxClientProxyJNI.vx_req_aux_capture_audio_stop_create(SWIGTYPE_p_p_vx_req_aux_capture_audio_stop.getCPtr(sWIGTYPE_p_p_vx_req_aux_capture_audio_stop))
    }

    Unit vx_req_aux_connectivity_info_create(SWIGTYPE_p_p_vx_req_aux_connectivity_info sWIGTYPE_p_p_vx_req_aux_connectivity_info) {
        VxClientProxyJNI.vx_req_aux_connectivity_info_create(SWIGTYPE_p_p_vx_req_aux_connectivity_info.getCPtr(sWIGTYPE_p_p_vx_req_aux_connectivity_info))
    }

    Unit vx_req_aux_create_account_create(SWIGTYPE_p_p_vx_req_aux_create_account sWIGTYPE_p_p_vx_req_aux_create_account) {
        VxClientProxyJNI.vx_req_aux_create_account_create(SWIGTYPE_p_p_vx_req_aux_create_account.getCPtr(sWIGTYPE_p_p_vx_req_aux_create_account))
    }

    Unit vx_req_aux_deactivate_account_create(SWIGTYPE_p_p_vx_req_aux_deactivate_account sWIGTYPE_p_p_vx_req_aux_deactivate_account) {
        VxClientProxyJNI.vx_req_aux_deactivate_account_create(SWIGTYPE_p_p_vx_req_aux_deactivate_account.getCPtr(sWIGTYPE_p_p_vx_req_aux_deactivate_account))
    }

    Unit vx_req_aux_diagnostic_state_dump_create(SWIGTYPE_p_p_vx_req_aux_diagnostic_state_dump sWIGTYPE_p_p_vx_req_aux_diagnostic_state_dump) {
        VxClientProxyJNI.vx_req_aux_diagnostic_state_dump_create(SWIGTYPE_p_p_vx_req_aux_diagnostic_state_dump.getCPtr(sWIGTYPE_p_p_vx_req_aux_diagnostic_state_dump))
    }

    Unit vx_req_aux_get_capture_devices_create(SWIGTYPE_p_p_vx_req_aux_get_capture_devices sWIGTYPE_p_p_vx_req_aux_get_capture_devices) {
        VxClientProxyJNI.vx_req_aux_get_capture_devices_create(SWIGTYPE_p_p_vx_req_aux_get_capture_devices.getCPtr(sWIGTYPE_p_p_vx_req_aux_get_capture_devices))
    }

    Unit vx_req_aux_get_mic_level_create(SWIGTYPE_p_p_vx_req_aux_get_mic_level sWIGTYPE_p_p_vx_req_aux_get_mic_level) {
        VxClientProxyJNI.vx_req_aux_get_mic_level_create(SWIGTYPE_p_p_vx_req_aux_get_mic_level.getCPtr(sWIGTYPE_p_p_vx_req_aux_get_mic_level))
    }

    Unit vx_req_aux_get_render_devices_create(SWIGTYPE_p_p_vx_req_aux_get_render_devices sWIGTYPE_p_p_vx_req_aux_get_render_devices) {
        VxClientProxyJNI.vx_req_aux_get_render_devices_create(SWIGTYPE_p_p_vx_req_aux_get_render_devices.getCPtr(sWIGTYPE_p_p_vx_req_aux_get_render_devices))
    }

    Unit vx_req_aux_get_speaker_level_create(SWIGTYPE_p_p_vx_req_aux_get_speaker_level sWIGTYPE_p_p_vx_req_aux_get_speaker_level) {
        VxClientProxyJNI.vx_req_aux_get_speaker_level_create(SWIGTYPE_p_p_vx_req_aux_get_speaker_level.getCPtr(sWIGTYPE_p_p_vx_req_aux_get_speaker_level))
    }

    Unit vx_req_aux_get_vad_properties_create(SWIGTYPE_p_p_vx_req_aux_get_vad_properties sWIGTYPE_p_p_vx_req_aux_get_vad_properties) {
        VxClientProxyJNI.vx_req_aux_get_vad_properties_create(SWIGTYPE_p_p_vx_req_aux_get_vad_properties.getCPtr(sWIGTYPE_p_p_vx_req_aux_get_vad_properties))
    }

    Unit vx_req_aux_global_monitor_keyboard_mouse_create(SWIGTYPE_p_p_vx_req_aux_global_monitor_keyboard_mouse sWIGTYPE_p_p_vx_req_aux_global_monitor_keyboard_mouse) {
        VxClientProxyJNI.vx_req_aux_global_monitor_keyboard_mouse_create(SWIGTYPE_p_p_vx_req_aux_global_monitor_keyboard_mouse.getCPtr(sWIGTYPE_p_p_vx_req_aux_global_monitor_keyboard_mouse))
    }

    Unit vx_req_aux_global_monitor_keyboard_mouse_t_set_codes_item(vx_req_aux_global_monitor_keyboard_mouse_t vx_req_aux_global_monitor_keyboard_mouse_t2, Int n, Int n2) {
        VxClientProxyJNI.vx_req_aux_global_monitor_keyboard_mouse_t_set_codes_item(vx_req_aux_global_monitor_keyboard_mouse_t.getCPtr(vx_req_aux_global_monitor_keyboard_mouse_t2), vx_req_aux_global_monitor_keyboard_mouse_t2, n, n2)
    }

    Unit vx_req_aux_notify_application_state_change_create(SWIGTYPE_p_p_vx_req_aux_notify_application_state_change sWIGTYPE_p_p_vx_req_aux_notify_application_state_change) {
        VxClientProxyJNI.vx_req_aux_notify_application_state_change_create(SWIGTYPE_p_p_vx_req_aux_notify_application_state_change.getCPtr(sWIGTYPE_p_p_vx_req_aux_notify_application_state_change))
    }

    Unit vx_req_aux_play_audio_buffer_create(SWIGTYPE_p_p_vx_req_aux_play_audio_buffer sWIGTYPE_p_p_vx_req_aux_play_audio_buffer) {
        VxClientProxyJNI.vx_req_aux_play_audio_buffer_create(SWIGTYPE_p_p_vx_req_aux_play_audio_buffer.getCPtr(sWIGTYPE_p_p_vx_req_aux_play_audio_buffer))
    }

    Unit vx_req_aux_reactivate_account_create(SWIGTYPE_p_p_vx_req_aux_reactivate_account sWIGTYPE_p_p_vx_req_aux_reactivate_account) {
        VxClientProxyJNI.vx_req_aux_reactivate_account_create(SWIGTYPE_p_p_vx_req_aux_reactivate_account.getCPtr(sWIGTYPE_p_p_vx_req_aux_reactivate_account))
    }

    Unit vx_req_aux_render_audio_modify_create(SWIGTYPE_p_p_vx_req_aux_render_audio_modify sWIGTYPE_p_p_vx_req_aux_render_audio_modify) {
        VxClientProxyJNI.vx_req_aux_render_audio_modify_create(SWIGTYPE_p_p_vx_req_aux_render_audio_modify.getCPtr(sWIGTYPE_p_p_vx_req_aux_render_audio_modify))
    }

    Unit vx_req_aux_render_audio_start_create(SWIGTYPE_p_p_vx_req_aux_render_audio_start sWIGTYPE_p_p_vx_req_aux_render_audio_start) {
        VxClientProxyJNI.vx_req_aux_render_audio_start_create(SWIGTYPE_p_p_vx_req_aux_render_audio_start.getCPtr(sWIGTYPE_p_p_vx_req_aux_render_audio_start))
    }

    Unit vx_req_aux_render_audio_stop_create(SWIGTYPE_p_p_vx_req_aux_render_audio_stop sWIGTYPE_p_p_vx_req_aux_render_audio_stop) {
        VxClientProxyJNI.vx_req_aux_render_audio_stop_create(SWIGTYPE_p_p_vx_req_aux_render_audio_stop.getCPtr(sWIGTYPE_p_p_vx_req_aux_render_audio_stop))
    }

    Unit vx_req_aux_reset_password_create(SWIGTYPE_p_p_vx_req_aux_reset_password sWIGTYPE_p_p_vx_req_aux_reset_password) {
        VxClientProxyJNI.vx_req_aux_reset_password_create(SWIGTYPE_p_p_vx_req_aux_reset_password.getCPtr(sWIGTYPE_p_p_vx_req_aux_reset_password))
    }

    Unit vx_req_aux_set_capture_device_create(SWIGTYPE_p_p_vx_req_aux_set_capture_device sWIGTYPE_p_p_vx_req_aux_set_capture_device) {
        VxClientProxyJNI.vx_req_aux_set_capture_device_create(SWIGTYPE_p_p_vx_req_aux_set_capture_device.getCPtr(sWIGTYPE_p_p_vx_req_aux_set_capture_device))
    }

    Unit vx_req_aux_set_idle_timeout_create(SWIGTYPE_p_p_vx_req_aux_set_idle_timeout sWIGTYPE_p_p_vx_req_aux_set_idle_timeout) {
        VxClientProxyJNI.vx_req_aux_set_idle_timeout_create(SWIGTYPE_p_p_vx_req_aux_set_idle_timeout.getCPtr(sWIGTYPE_p_p_vx_req_aux_set_idle_timeout))
    }

    Unit vx_req_aux_set_mic_level_create(SWIGTYPE_p_p_vx_req_aux_set_mic_level sWIGTYPE_p_p_vx_req_aux_set_mic_level) {
        VxClientProxyJNI.vx_req_aux_set_mic_level_create(SWIGTYPE_p_p_vx_req_aux_set_mic_level.getCPtr(sWIGTYPE_p_p_vx_req_aux_set_mic_level))
    }

    Unit vx_req_aux_set_render_device_create(SWIGTYPE_p_p_vx_req_aux_set_render_device sWIGTYPE_p_p_vx_req_aux_set_render_device) {
        VxClientProxyJNI.vx_req_aux_set_render_device_create(SWIGTYPE_p_p_vx_req_aux_set_render_device.getCPtr(sWIGTYPE_p_p_vx_req_aux_set_render_device))
    }

    Unit vx_req_aux_set_speaker_level_create(SWIGTYPE_p_p_vx_req_aux_set_speaker_level sWIGTYPE_p_p_vx_req_aux_set_speaker_level) {
        VxClientProxyJNI.vx_req_aux_set_speaker_level_create(SWIGTYPE_p_p_vx_req_aux_set_speaker_level.getCPtr(sWIGTYPE_p_p_vx_req_aux_set_speaker_level))
    }

    Unit vx_req_aux_set_vad_properties_create(SWIGTYPE_p_p_vx_req_aux_set_vad_properties sWIGTYPE_p_p_vx_req_aux_set_vad_properties) {
        VxClientProxyJNI.vx_req_aux_set_vad_properties_create(SWIGTYPE_p_p_vx_req_aux_set_vad_properties.getCPtr(sWIGTYPE_p_p_vx_req_aux_set_vad_properties))
    }

    Unit vx_req_aux_start_buffer_capture_create(SWIGTYPE_p_p_vx_req_aux_start_buffer_capture sWIGTYPE_p_p_vx_req_aux_start_buffer_capture) {
        VxClientProxyJNI.vx_req_aux_start_buffer_capture_create(SWIGTYPE_p_p_vx_req_aux_start_buffer_capture.getCPtr(sWIGTYPE_p_p_vx_req_aux_start_buffer_capture))
    }

    Unit vx_req_channel_ban_user_create(SWIGTYPE_p_p_vx_req_channel_ban_user sWIGTYPE_p_p_vx_req_channel_ban_user) {
        VxClientProxyJNI.vx_req_channel_ban_user_create(SWIGTYPE_p_p_vx_req_channel_ban_user.getCPtr(sWIGTYPE_p_p_vx_req_channel_ban_user))
    }

    Unit vx_req_channel_get_banned_users_create(SWIGTYPE_p_p_vx_req_channel_get_banned_users sWIGTYPE_p_p_vx_req_channel_get_banned_users) {
        VxClientProxyJNI.vx_req_channel_get_banned_users_create(SWIGTYPE_p_p_vx_req_channel_get_banned_users.getCPtr(sWIGTYPE_p_p_vx_req_channel_get_banned_users))
    }

    Unit vx_req_channel_kick_user_create(SWIGTYPE_p_p_vx_req_channel_kick_user sWIGTYPE_p_p_vx_req_channel_kick_user) {
        VxClientProxyJNI.vx_req_channel_kick_user_create(SWIGTYPE_p_p_vx_req_channel_kick_user.getCPtr(sWIGTYPE_p_p_vx_req_channel_kick_user))
    }

    Unit vx_req_channel_mute_all_users_create(SWIGTYPE_p_p_vx_req_channel_mute_all_users sWIGTYPE_p_p_vx_req_channel_mute_all_users) {
        VxClientProxyJNI.vx_req_channel_mute_all_users_create(SWIGTYPE_p_p_vx_req_channel_mute_all_users.getCPtr(sWIGTYPE_p_p_vx_req_channel_mute_all_users))
    }

    Unit vx_req_channel_mute_user_create(SWIGTYPE_p_p_vx_req_channel_mute_user sWIGTYPE_p_p_vx_req_channel_mute_user) {
        VxClientProxyJNI.vx_req_channel_mute_user_create(SWIGTYPE_p_p_vx_req_channel_mute_user.getCPtr(sWIGTYPE_p_p_vx_req_channel_mute_user))
    }

    Unit vx_req_channel_set_lock_mode_create(SWIGTYPE_p_p_vx_req_channel_set_lock_mode sWIGTYPE_p_p_vx_req_channel_set_lock_mode) {
        VxClientProxyJNI.vx_req_channel_set_lock_mode_create(SWIGTYPE_p_p_vx_req_channel_set_lock_mode.getCPtr(sWIGTYPE_p_p_vx_req_channel_set_lock_mode))
    }

    Unit vx_req_connector_create_create(SWIGTYPE_p_p_vx_req_connector_create sWIGTYPE_p_p_vx_req_connector_create) {
        VxClientProxyJNI.vx_req_connector_create_create(SWIGTYPE_p_p_vx_req_connector_create.getCPtr(sWIGTYPE_p_p_vx_req_connector_create))
    }

    Unit vx_req_connector_get_local_audio_info_create(SWIGTYPE_p_p_vx_req_connector_get_local_audio_info sWIGTYPE_p_p_vx_req_connector_get_local_audio_info) {
        VxClientProxyJNI.vx_req_connector_get_local_audio_info_create(SWIGTYPE_p_p_vx_req_connector_get_local_audio_info.getCPtr(sWIGTYPE_p_p_vx_req_connector_get_local_audio_info))
    }

    Unit vx_req_connector_initiate_shutdown_create(SWIGTYPE_p_p_vx_req_connector_initiate_shutdown sWIGTYPE_p_p_vx_req_connector_initiate_shutdown) {
        VxClientProxyJNI.vx_req_connector_initiate_shutdown_create(SWIGTYPE_p_p_vx_req_connector_initiate_shutdown.getCPtr(sWIGTYPE_p_p_vx_req_connector_initiate_shutdown))
    }

    Unit vx_req_connector_mute_local_mic_create(SWIGTYPE_p_p_vx_req_connector_mute_local_mic sWIGTYPE_p_p_vx_req_connector_mute_local_mic) {
        VxClientProxyJNI.vx_req_connector_mute_local_mic_create(SWIGTYPE_p_p_vx_req_connector_mute_local_mic.getCPtr(sWIGTYPE_p_p_vx_req_connector_mute_local_mic))
    }

    Unit vx_req_connector_mute_local_speaker_create(SWIGTYPE_p_p_vx_req_connector_mute_local_speaker sWIGTYPE_p_p_vx_req_connector_mute_local_speaker) {
        VxClientProxyJNI.vx_req_connector_mute_local_speaker_create(SWIGTYPE_p_p_vx_req_connector_mute_local_speaker.getCPtr(sWIGTYPE_p_p_vx_req_connector_mute_local_speaker))
    }

    Unit vx_req_connector_set_local_mic_volume_create(SWIGTYPE_p_p_vx_req_connector_set_local_mic_volume sWIGTYPE_p_p_vx_req_connector_set_local_mic_volume) {
        VxClientProxyJNI.vx_req_connector_set_local_mic_volume_create(SWIGTYPE_p_p_vx_req_connector_set_local_mic_volume.getCPtr(sWIGTYPE_p_p_vx_req_connector_set_local_mic_volume))
    }

    Unit vx_req_connector_set_local_speaker_volume_create(SWIGTYPE_p_p_vx_req_connector_set_local_speaker_volume sWIGTYPE_p_p_vx_req_connector_set_local_speaker_volume) {
        VxClientProxyJNI.vx_req_connector_set_local_speaker_volume_create(SWIGTYPE_p_p_vx_req_connector_set_local_speaker_volume.getCPtr(sWIGTYPE_p_p_vx_req_connector_set_local_speaker_volume))
    }

    Unit vx_req_session_channel_invite_user_create(SWIGTYPE_p_p_vx_req_session_channel_invite_user sWIGTYPE_p_p_vx_req_session_channel_invite_user) {
        VxClientProxyJNI.vx_req_session_channel_invite_user_create(SWIGTYPE_p_p_vx_req_session_channel_invite_user.getCPtr(sWIGTYPE_p_p_vx_req_session_channel_invite_user))
    }

    Unit vx_req_session_create_create(SWIGTYPE_p_p_vx_req_session_create sWIGTYPE_p_p_vx_req_session_create) {
        VxClientProxyJNI.vx_req_session_create_create(SWIGTYPE_p_p_vx_req_session_create.getCPtr(sWIGTYPE_p_p_vx_req_session_create))
    }

    Unit vx_req_session_media_connect_create(SWIGTYPE_p_p_vx_req_session_media_connect sWIGTYPE_p_p_vx_req_session_media_connect) {
        VxClientProxyJNI.vx_req_session_media_connect_create(SWIGTYPE_p_p_vx_req_session_media_connect.getCPtr(sWIGTYPE_p_p_vx_req_session_media_connect))
    }

    Unit vx_req_session_media_disconnect_create(SWIGTYPE_p_p_vx_req_session_media_disconnect sWIGTYPE_p_p_vx_req_session_media_disconnect) {
        VxClientProxyJNI.vx_req_session_media_disconnect_create(SWIGTYPE_p_p_vx_req_session_media_disconnect.getCPtr(sWIGTYPE_p_p_vx_req_session_media_disconnect))
    }

    Unit vx_req_session_mute_local_speaker_create(SWIGTYPE_p_p_vx_req_session_mute_local_speaker sWIGTYPE_p_p_vx_req_session_mute_local_speaker) {
        VxClientProxyJNI.vx_req_session_mute_local_speaker_create(SWIGTYPE_p_p_vx_req_session_mute_local_speaker.getCPtr(sWIGTYPE_p_p_vx_req_session_mute_local_speaker))
    }

    Unit vx_req_session_send_dtmf_create(SWIGTYPE_p_p_vx_req_session_send_dtmf sWIGTYPE_p_p_vx_req_session_send_dtmf) {
        VxClientProxyJNI.vx_req_session_send_dtmf_create(SWIGTYPE_p_p_vx_req_session_send_dtmf.getCPtr(sWIGTYPE_p_p_vx_req_session_send_dtmf))
    }

    Unit vx_req_session_send_message_create(SWIGTYPE_p_p_vx_req_session_send_message sWIGTYPE_p_p_vx_req_session_send_message) {
        VxClientProxyJNI.vx_req_session_send_message_create(SWIGTYPE_p_p_vx_req_session_send_message.getCPtr(sWIGTYPE_p_p_vx_req_session_send_message))
    }

    Unit vx_req_session_send_notification_create(SWIGTYPE_p_p_vx_req_session_send_notification sWIGTYPE_p_p_vx_req_session_send_notification) {
        VxClientProxyJNI.vx_req_session_send_notification_create(SWIGTYPE_p_p_vx_req_session_send_notification.getCPtr(sWIGTYPE_p_p_vx_req_session_send_notification))
    }

    Unit vx_req_session_set_3d_position_create(SWIGTYPE_p_p_vx_req_session_set_3d_position sWIGTYPE_p_p_vx_req_session_set_3d_position) {
        VxClientProxyJNI.vx_req_session_set_3d_position_create(SWIGTYPE_p_p_vx_req_session_set_3d_position.getCPtr(sWIGTYPE_p_p_vx_req_session_set_3d_position))
    }

    Double vx_req_session_set_3d_position_t_get_listener_at_orientation_item(vx_req_session_set_3d_position_t vx_req_session_set_3d_position_t2, Int n) {
        return VxClientProxyJNI.vx_req_session_set_3d_position_t_get_listener_at_orientation_item(vx_req_session_set_3d_position_t.getCPtr(vx_req_session_set_3d_position_t2), vx_req_session_set_3d_position_t2, n)
    }

    Double vx_req_session_set_3d_position_t_get_listener_left_orientation_item(vx_req_session_set_3d_position_t vx_req_session_set_3d_position_t2, Int n) {
        return VxClientProxyJNI.vx_req_session_set_3d_position_t_get_listener_left_orientation_item(vx_req_session_set_3d_position_t.getCPtr(vx_req_session_set_3d_position_t2), vx_req_session_set_3d_position_t2, n)
    }

    Double vx_req_session_set_3d_position_t_get_listener_position_item(vx_req_session_set_3d_position_t vx_req_session_set_3d_position_t2, Int n) {
        return VxClientProxyJNI.vx_req_session_set_3d_position_t_get_listener_position_item(vx_req_session_set_3d_position_t.getCPtr(vx_req_session_set_3d_position_t2), vx_req_session_set_3d_position_t2, n)
    }

    Double vx_req_session_set_3d_position_t_get_listener_up_orientation_item(vx_req_session_set_3d_position_t vx_req_session_set_3d_position_t2, Int n) {
        return VxClientProxyJNI.vx_req_session_set_3d_position_t_get_listener_up_orientation_item(vx_req_session_set_3d_position_t.getCPtr(vx_req_session_set_3d_position_t2), vx_req_session_set_3d_position_t2, n)
    }

    Double vx_req_session_set_3d_position_t_get_listener_velocity_item(vx_req_session_set_3d_position_t vx_req_session_set_3d_position_t2, Int n) {
        return VxClientProxyJNI.vx_req_session_set_3d_position_t_get_listener_velocity_item(vx_req_session_set_3d_position_t.getCPtr(vx_req_session_set_3d_position_t2), vx_req_session_set_3d_position_t2, n)
    }

    Double vx_req_session_set_3d_position_t_get_speaker_at_orientation_item(vx_req_session_set_3d_position_t vx_req_session_set_3d_position_t2, Int n) {
        return VxClientProxyJNI.vx_req_session_set_3d_position_t_get_speaker_at_orientation_item(vx_req_session_set_3d_position_t.getCPtr(vx_req_session_set_3d_position_t2), vx_req_session_set_3d_position_t2, n)
    }

    Double vx_req_session_set_3d_position_t_get_speaker_left_orientation_item(vx_req_session_set_3d_position_t vx_req_session_set_3d_position_t2, Int n) {
        return VxClientProxyJNI.vx_req_session_set_3d_position_t_get_speaker_left_orientation_item(vx_req_session_set_3d_position_t.getCPtr(vx_req_session_set_3d_position_t2), vx_req_session_set_3d_position_t2, n)
    }

    Double vx_req_session_set_3d_position_t_get_speaker_position_item(vx_req_session_set_3d_position_t vx_req_session_set_3d_position_t2, Int n) {
        return VxClientProxyJNI.vx_req_session_set_3d_position_t_get_speaker_position_item(vx_req_session_set_3d_position_t.getCPtr(vx_req_session_set_3d_position_t2), vx_req_session_set_3d_position_t2, n)
    }

    Double vx_req_session_set_3d_position_t_get_speaker_up_orientation_item(vx_req_session_set_3d_position_t vx_req_session_set_3d_position_t2, Int n) {
        return VxClientProxyJNI.vx_req_session_set_3d_position_t_get_speaker_up_orientation_item(vx_req_session_set_3d_position_t.getCPtr(vx_req_session_set_3d_position_t2), vx_req_session_set_3d_position_t2, n)
    }

    Double vx_req_session_set_3d_position_t_get_speaker_velocity_item(vx_req_session_set_3d_position_t vx_req_session_set_3d_position_t2, Int n) {
        return VxClientProxyJNI.vx_req_session_set_3d_position_t_get_speaker_velocity_item(vx_req_session_set_3d_position_t.getCPtr(vx_req_session_set_3d_position_t2), vx_req_session_set_3d_position_t2, n)
    }

    Unit vx_req_session_set_3d_position_t_set_listener_at_orientation_item(vx_req_session_set_3d_position_t vx_req_session_set_3d_position_t2, Int n, Double d) {
        VxClientProxyJNI.vx_req_session_set_3d_position_t_set_listener_at_orientation_item(vx_req_session_set_3d_position_t.getCPtr(vx_req_session_set_3d_position_t2), vx_req_session_set_3d_position_t2, n, d)
    }

    Unit vx_req_session_set_3d_position_t_set_listener_left_orientation_item(vx_req_session_set_3d_position_t vx_req_session_set_3d_position_t2, Int n, Double d) {
        VxClientProxyJNI.vx_req_session_set_3d_position_t_set_listener_left_orientation_item(vx_req_session_set_3d_position_t.getCPtr(vx_req_session_set_3d_position_t2), vx_req_session_set_3d_position_t2, n, d)
    }

    Unit vx_req_session_set_3d_position_t_set_listener_position_item(vx_req_session_set_3d_position_t vx_req_session_set_3d_position_t2, Int n, Double d) {
        VxClientProxyJNI.vx_req_session_set_3d_position_t_set_listener_position_item(vx_req_session_set_3d_position_t.getCPtr(vx_req_session_set_3d_position_t2), vx_req_session_set_3d_position_t2, n, d)
    }

    Unit vx_req_session_set_3d_position_t_set_listener_up_orientation_item(vx_req_session_set_3d_position_t vx_req_session_set_3d_position_t2, Int n, Double d) {
        VxClientProxyJNI.vx_req_session_set_3d_position_t_set_listener_up_orientation_item(vx_req_session_set_3d_position_t.getCPtr(vx_req_session_set_3d_position_t2), vx_req_session_set_3d_position_t2, n, d)
    }

    Unit vx_req_session_set_3d_position_t_set_listener_velocity_item(vx_req_session_set_3d_position_t vx_req_session_set_3d_position_t2, Int n, Double d) {
        VxClientProxyJNI.vx_req_session_set_3d_position_t_set_listener_velocity_item(vx_req_session_set_3d_position_t.getCPtr(vx_req_session_set_3d_position_t2), vx_req_session_set_3d_position_t2, n, d)
    }

    Unit vx_req_session_set_3d_position_t_set_speaker_at_orientation_item(vx_req_session_set_3d_position_t vx_req_session_set_3d_position_t2, Int n, Double d) {
        VxClientProxyJNI.vx_req_session_set_3d_position_t_set_speaker_at_orientation_item(vx_req_session_set_3d_position_t.getCPtr(vx_req_session_set_3d_position_t2), vx_req_session_set_3d_position_t2, n, d)
    }

    Unit vx_req_session_set_3d_position_t_set_speaker_left_orientation_item(vx_req_session_set_3d_position_t vx_req_session_set_3d_position_t2, Int n, Double d) {
        VxClientProxyJNI.vx_req_session_set_3d_position_t_set_speaker_left_orientation_item(vx_req_session_set_3d_position_t.getCPtr(vx_req_session_set_3d_position_t2), vx_req_session_set_3d_position_t2, n, d)
    }

    Unit vx_req_session_set_3d_position_t_set_speaker_position_item(vx_req_session_set_3d_position_t vx_req_session_set_3d_position_t2, Int n, Double d) {
        VxClientProxyJNI.vx_req_session_set_3d_position_t_set_speaker_position_item(vx_req_session_set_3d_position_t.getCPtr(vx_req_session_set_3d_position_t2), vx_req_session_set_3d_position_t2, n, d)
    }

    Unit vx_req_session_set_3d_position_t_set_speaker_up_orientation_item(vx_req_session_set_3d_position_t vx_req_session_set_3d_position_t2, Int n, Double d) {
        VxClientProxyJNI.vx_req_session_set_3d_position_t_set_speaker_up_orientation_item(vx_req_session_set_3d_position_t.getCPtr(vx_req_session_set_3d_position_t2), vx_req_session_set_3d_position_t2, n, d)
    }

    Unit vx_req_session_set_3d_position_t_set_speaker_velocity_item(vx_req_session_set_3d_position_t vx_req_session_set_3d_position_t2, Int n, Double d) {
        VxClientProxyJNI.vx_req_session_set_3d_position_t_set_speaker_velocity_item(vx_req_session_set_3d_position_t.getCPtr(vx_req_session_set_3d_position_t2), vx_req_session_set_3d_position_t2, n, d)
    }

    Unit vx_req_session_set_local_speaker_volume_create(SWIGTYPE_p_p_vx_req_session_set_local_speaker_volume sWIGTYPE_p_p_vx_req_session_set_local_speaker_volume) {
        VxClientProxyJNI.vx_req_session_set_local_speaker_volume_create(SWIGTYPE_p_p_vx_req_session_set_local_speaker_volume.getCPtr(sWIGTYPE_p_p_vx_req_session_set_local_speaker_volume))
    }

    Unit vx_req_session_set_participant_mute_for_me_create(SWIGTYPE_p_p_vx_req_session_set_participant_mute_for_me sWIGTYPE_p_p_vx_req_session_set_participant_mute_for_me) {
        VxClientProxyJNI.vx_req_session_set_participant_mute_for_me_create(SWIGTYPE_p_p_vx_req_session_set_participant_mute_for_me.getCPtr(sWIGTYPE_p_p_vx_req_session_set_participant_mute_for_me))
    }

    Unit vx_req_session_set_participant_volume_for_me_create(SWIGTYPE_p_p_vx_req_session_set_participant_volume_for_me sWIGTYPE_p_p_vx_req_session_set_participant_volume_for_me) {
        VxClientProxyJNI.vx_req_session_set_participant_volume_for_me_create(SWIGTYPE_p_p_vx_req_session_set_participant_volume_for_me.getCPtr(sWIGTYPE_p_p_vx_req_session_set_participant_volume_for_me))
    }

    Unit vx_req_session_set_voice_font_create(SWIGTYPE_p_p_vx_req_session_set_voice_font sWIGTYPE_p_p_vx_req_session_set_voice_font) {
        VxClientProxyJNI.vx_req_session_set_voice_font_create(SWIGTYPE_p_p_vx_req_session_set_voice_font.getCPtr(sWIGTYPE_p_p_vx_req_session_set_voice_font))
    }

    Unit vx_req_session_terminate_create(SWIGTYPE_p_p_vx_req_session_terminate sWIGTYPE_p_p_vx_req_session_terminate) {
        VxClientProxyJNI.vx_req_session_terminate_create(SWIGTYPE_p_p_vx_req_session_terminate.getCPtr(sWIGTYPE_p_p_vx_req_session_terminate))
    }

    Unit vx_req_session_text_connect_create(SWIGTYPE_p_p_vx_req_session_text_connect sWIGTYPE_p_p_vx_req_session_text_connect) {
        VxClientProxyJNI.vx_req_session_text_connect_create(SWIGTYPE_p_p_vx_req_session_text_connect.getCPtr(sWIGTYPE_p_p_vx_req_session_text_connect))
    }

    Unit vx_req_session_text_disconnect_create(SWIGTYPE_p_p_vx_req_session_text_disconnect sWIGTYPE_p_p_vx_req_session_text_disconnect) {
        VxClientProxyJNI.vx_req_session_text_disconnect_create(SWIGTYPE_p_p_vx_req_session_text_disconnect.getCPtr(sWIGTYPE_p_p_vx_req_session_text_disconnect))
    }

    Unit vx_req_sessiongroup_add_session_create(SWIGTYPE_p_p_vx_req_sessiongroup_add_session sWIGTYPE_p_p_vx_req_sessiongroup_add_session) {
        VxClientProxyJNI.vx_req_sessiongroup_add_session_create(SWIGTYPE_p_p_vx_req_sessiongroup_add_session.getCPtr(sWIGTYPE_p_p_vx_req_sessiongroup_add_session))
    }

    Unit vx_req_sessiongroup_control_audio_injection_create(SWIGTYPE_p_p_vx_req_sessiongroup_control_audio_injection sWIGTYPE_p_p_vx_req_sessiongroup_control_audio_injection) {
        VxClientProxyJNI.vx_req_sessiongroup_control_audio_injection_create(SWIGTYPE_p_p_vx_req_sessiongroup_control_audio_injection.getCPtr(sWIGTYPE_p_p_vx_req_sessiongroup_control_audio_injection))
    }

    Unit vx_req_sessiongroup_control_playback_create(SWIGTYPE_p_p_vx_req_sessiongroup_control_playback sWIGTYPE_p_p_vx_req_sessiongroup_control_playback) {
        VxClientProxyJNI.vx_req_sessiongroup_control_playback_create(SWIGTYPE_p_p_vx_req_sessiongroup_control_playback.getCPtr(sWIGTYPE_p_p_vx_req_sessiongroup_control_playback))
    }

    Unit vx_req_sessiongroup_control_recording_create(SWIGTYPE_p_p_vx_req_sessiongroup_control_recording sWIGTYPE_p_p_vx_req_sessiongroup_control_recording) {
        VxClientProxyJNI.vx_req_sessiongroup_control_recording_create(SWIGTYPE_p_p_vx_req_sessiongroup_control_recording.getCPtr(sWIGTYPE_p_p_vx_req_sessiongroup_control_recording))
    }

    Unit vx_req_sessiongroup_create_create(SWIGTYPE_p_p_vx_req_sessiongroup_create sWIGTYPE_p_p_vx_req_sessiongroup_create) {
        VxClientProxyJNI.vx_req_sessiongroup_create_create(SWIGTYPE_p_p_vx_req_sessiongroup_create.getCPtr(sWIGTYPE_p_p_vx_req_sessiongroup_create))
    }

    Unit vx_req_sessiongroup_get_stats_create(SWIGTYPE_p_p_vx_req_sessiongroup_get_stats sWIGTYPE_p_p_vx_req_sessiongroup_get_stats) {
        VxClientProxyJNI.vx_req_sessiongroup_get_stats_create(SWIGTYPE_p_p_vx_req_sessiongroup_get_stats.getCPtr(sWIGTYPE_p_p_vx_req_sessiongroup_get_stats))
    }

    Unit vx_req_sessiongroup_remove_session_create(SWIGTYPE_p_p_vx_req_sessiongroup_remove_session sWIGTYPE_p_p_vx_req_sessiongroup_remove_session) {
        VxClientProxyJNI.vx_req_sessiongroup_remove_session_create(SWIGTYPE_p_p_vx_req_sessiongroup_remove_session.getCPtr(sWIGTYPE_p_p_vx_req_sessiongroup_remove_session))
    }

    Unit vx_req_sessiongroup_reset_focus_create(SWIGTYPE_p_p_vx_req_sessiongroup_reset_focus sWIGTYPE_p_p_vx_req_sessiongroup_reset_focus) {
        VxClientProxyJNI.vx_req_sessiongroup_reset_focus_create(SWIGTYPE_p_p_vx_req_sessiongroup_reset_focus.getCPtr(sWIGTYPE_p_p_vx_req_sessiongroup_reset_focus))
    }

    Unit vx_req_sessiongroup_set_focus_create(SWIGTYPE_p_p_vx_req_sessiongroup_set_focus sWIGTYPE_p_p_vx_req_sessiongroup_set_focus) {
        VxClientProxyJNI.vx_req_sessiongroup_set_focus_create(SWIGTYPE_p_p_vx_req_sessiongroup_set_focus.getCPtr(sWIGTYPE_p_p_vx_req_sessiongroup_set_focus))
    }

    Unit vx_req_sessiongroup_set_playback_options_create(SWIGTYPE_p_p_vx_req_sessiongroup_set_playback_options sWIGTYPE_p_p_vx_req_sessiongroup_set_playback_options) {
        VxClientProxyJNI.vx_req_sessiongroup_set_playback_options_create(SWIGTYPE_p_p_vx_req_sessiongroup_set_playback_options.getCPtr(sWIGTYPE_p_p_vx_req_sessiongroup_set_playback_options))
    }

    Unit vx_req_sessiongroup_set_session_3d_position_create(SWIGTYPE_p_p_vx_req_sessiongroup_set_session_3d_position sWIGTYPE_p_p_vx_req_sessiongroup_set_session_3d_position) {
        VxClientProxyJNI.vx_req_sessiongroup_set_session_3d_position_create(SWIGTYPE_p_p_vx_req_sessiongroup_set_session_3d_position.getCPtr(sWIGTYPE_p_p_vx_req_sessiongroup_set_session_3d_position))
    }

    Double vx_req_sessiongroup_set_session_3d_position_t_get_speaker_position_item(vx_req_sessiongroup_set_session_3d_position_t vx_req_sessiongroup_set_session_3d_position_t2, Int n) {
        return VxClientProxyJNI.vx_req_sessiongroup_set_session_3d_position_t_get_speaker_position_item(vx_req_sessiongroup_set_session_3d_position_t.getCPtr(vx_req_sessiongroup_set_session_3d_position_t2), vx_req_sessiongroup_set_session_3d_position_t2, n)
    }

    Unit vx_req_sessiongroup_set_session_3d_position_t_set_speaker_position_item(vx_req_sessiongroup_set_session_3d_position_t vx_req_sessiongroup_set_session_3d_position_t2, Int n, Double d) {
        VxClientProxyJNI.vx_req_sessiongroup_set_session_3d_position_t_set_speaker_position_item(vx_req_sessiongroup_set_session_3d_position_t.getCPtr(vx_req_sessiongroup_set_session_3d_position_t2), vx_req_sessiongroup_set_session_3d_position_t2, n, d)
    }

    Unit vx_req_sessiongroup_set_tx_all_sessions_create(SWIGTYPE_p_p_vx_req_sessiongroup_set_tx_all_sessions sWIGTYPE_p_p_vx_req_sessiongroup_set_tx_all_sessions) {
        VxClientProxyJNI.vx_req_sessiongroup_set_tx_all_sessions_create(SWIGTYPE_p_p_vx_req_sessiongroup_set_tx_all_sessions.getCPtr(sWIGTYPE_p_p_vx_req_sessiongroup_set_tx_all_sessions))
    }

    Unit vx_req_sessiongroup_set_tx_no_session_create(SWIGTYPE_p_p_vx_req_sessiongroup_set_tx_no_session sWIGTYPE_p_p_vx_req_sessiongroup_set_tx_no_session) {
        VxClientProxyJNI.vx_req_sessiongroup_set_tx_no_session_create(SWIGTYPE_p_p_vx_req_sessiongroup_set_tx_no_session.getCPtr(sWIGTYPE_p_p_vx_req_sessiongroup_set_tx_no_session))
    }

    Unit vx_req_sessiongroup_set_tx_session_create(SWIGTYPE_p_p_vx_req_sessiongroup_set_tx_session sWIGTYPE_p_p_vx_req_sessiongroup_set_tx_session) {
        VxClientProxyJNI.vx_req_sessiongroup_set_tx_session_create(SWIGTYPE_p_p_vx_req_sessiongroup_set_tx_session.getCPtr(sWIGTYPE_p_p_vx_req_sessiongroup_set_tx_session))
    }

    Unit vx_req_sessiongroup_terminate_create(SWIGTYPE_p_p_vx_req_sessiongroup_terminate sWIGTYPE_p_p_vx_req_sessiongroup_terminate) {
        VxClientProxyJNI.vx_req_sessiongroup_terminate_create(SWIGTYPE_p_p_vx_req_sessiongroup_terminate.getCPtr(sWIGTYPE_p_p_vx_req_sessiongroup_terminate))
    }

    Unit vx_req_sessiongroup_unset_focus_create(SWIGTYPE_p_p_vx_req_sessiongroup_unset_focus sWIGTYPE_p_p_vx_req_sessiongroup_unset_focus) {
        VxClientProxyJNI.vx_req_sessiongroup_unset_focus_create(SWIGTYPE_p_p_vx_req_sessiongroup_unset_focus.getCPtr(sWIGTYPE_p_p_vx_req_sessiongroup_unset_focus))
    }

    Unit vx_request_to_xml(SWIGTYPE_p_void sWIGTYPE_p_void, SWIGTYPE_p_p_char sWIGTYPE_p_p_char) {
        VxClientProxyJNI.vx_request_to_xml(SWIGTYPE_p_void.getCPtr(sWIGTYPE_p_void), SWIGTYPE_p_p_char.getCPtr(sWIGTYPE_p_p_char))
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_buddy_t vx_resp_account_buddy_search_t_get_buddies_item(vx_resp_account_buddy_search_t object, Int n) {
        var l: Long = VxClientProxyJNI.vx_resp_account_buddy_search_t_get_buddies_item(vx_resp_account_buddy_search_t.getCPtr((vx_resp_account_buddy_search_t)object), (vx_resp_account_buddy_search_t)object, n)
        if (l != 0L) return vx_buddy_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_channel_favorite_t vx_resp_account_channel_favorites_get_list_t_get_favorites_item(vx_resp_account_channel_favorites_get_list_t object, Int n) {
        var l: Long = VxClientProxyJNI.vx_resp_account_channel_favorites_get_list_t_get_favorites_item(vx_resp_account_channel_favorites_get_list_t.getCPtr((vx_resp_account_channel_favorites_get_list_t)object), (vx_resp_account_channel_favorites_get_list_t)object, n)
        if (l != 0L) return vx_channel_favorite_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_channel_favorite_group_t vx_resp_account_channel_favorites_get_list_t_get_groups_item(vx_resp_account_channel_favorites_get_list_t object, Int n) {
        var l: Long = VxClientProxyJNI.vx_resp_account_channel_favorites_get_list_t_get_groups_item(vx_resp_account_channel_favorites_get_list_t.getCPtr((vx_resp_account_channel_favorites_get_list_t)object), (vx_resp_account_channel_favorites_get_list_t)object, n)
        if (l != 0L) return vx_channel_favorite_group_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_participant_t vx_resp_account_channel_get_acl_t_get_participants_item(vx_resp_account_channel_get_acl_t object, Int n) {
        var l: Long = VxClientProxyJNI.vx_resp_account_channel_get_acl_t_get_participants_item(vx_resp_account_channel_get_acl_t.getCPtr((vx_resp_account_channel_get_acl_t)object), (vx_resp_account_channel_get_acl_t)object, n)
        if (l != 0L) return vx_participant_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_participant_t vx_resp_account_channel_get_moderators_t_get_participants_item(vx_resp_account_channel_get_moderators_t object, Int n) {
        var l: Long = VxClientProxyJNI.vx_resp_account_channel_get_moderators_t_get_participants_item(vx_resp_account_channel_get_moderators_t.getCPtr((vx_resp_account_channel_get_moderators_t)object), (vx_resp_account_channel_get_moderators_t)object, n)
        if (l != 0L) return vx_participant_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_participant_t vx_resp_account_channel_get_participants_t_get_participants_item(vx_resp_account_channel_get_participants_t object, Int n) {
        var l: Long = VxClientProxyJNI.vx_resp_account_channel_get_participants_t_get_participants_item(vx_resp_account_channel_get_participants_t.getCPtr((vx_resp_account_channel_get_participants_t)object), (vx_resp_account_channel_get_participants_t)object, n)
        if (l != 0L) return vx_participant_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_channel_t vx_resp_account_channel_search_t_get_channels_item(vx_resp_account_channel_search_t object, Int n) {
        var l: Long = VxClientProxyJNI.vx_resp_account_channel_search_t_get_channels_item(vx_resp_account_channel_search_t.getCPtr((vx_resp_account_channel_search_t)object), (vx_resp_account_channel_search_t)object, n)
        if (l != 0L) return vx_channel_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_voice_font_t vx_resp_account_get_session_fonts_t_get_session_fonts_item(vx_resp_account_get_session_fonts_t object, Int n) {
        var l: Long = VxClientProxyJNI.vx_resp_account_get_session_fonts_t_get_session_fonts_item(vx_resp_account_get_session_fonts_t.getCPtr((vx_resp_account_get_session_fonts_t)object), (vx_resp_account_get_session_fonts_t)object, n)
        if (l != 0L) return vx_voice_font_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_voice_font_t vx_resp_account_get_template_fonts_t_get_template_fonts_item(vx_resp_account_get_template_fonts_t object, Int n) {
        var l: Long = VxClientProxyJNI.vx_resp_account_get_template_fonts_t_get_template_fonts_item(vx_resp_account_get_template_fonts_t.getCPtr((vx_resp_account_get_template_fonts_t)object), (vx_resp_account_get_template_fonts_t)object, n)
        if (l != 0L) return vx_voice_font_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_auto_accept_rule_t vx_resp_account_list_auto_accept_rules_t_get_auto_accept_rules_item(vx_resp_account_list_auto_accept_rules_t object, Int n) {
        var l: Long = VxClientProxyJNI.vx_resp_account_list_auto_accept_rules_t_get_auto_accept_rules_item(vx_resp_account_list_auto_accept_rules_t.getCPtr((vx_resp_account_list_auto_accept_rules_t)object), (vx_resp_account_list_auto_accept_rules_t)object, n)
        if (l != 0L) return vx_auto_accept_rule_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_block_rule_t vx_resp_account_list_block_rules_t_get_block_rules_item(vx_resp_account_list_block_rules_t object, Int n) {
        var l: Long = VxClientProxyJNI.vx_resp_account_list_block_rules_t_get_block_rules_item(vx_resp_account_list_block_rules_t.getCPtr((vx_resp_account_list_block_rules_t)object), (vx_resp_account_list_block_rules_t)object, n)
        if (l != 0L) return vx_block_rule_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_buddy_t vx_resp_account_list_buddies_and_groups_t_get_buddies_item(vx_resp_account_list_buddies_and_groups_t object, Int n) {
        var l: Long = VxClientProxyJNI.vx_resp_account_list_buddies_and_groups_t_get_buddies_item(vx_resp_account_list_buddies_and_groups_t.getCPtr((vx_resp_account_list_buddies_and_groups_t)object), (vx_resp_account_list_buddies_and_groups_t)object, n)
        if (l != 0L) return vx_buddy_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_group_t vx_resp_account_list_buddies_and_groups_t_get_groups_item(vx_resp_account_list_buddies_and_groups_t object, Int n) {
        var l: Long = VxClientProxyJNI.vx_resp_account_list_buddies_and_groups_t_get_groups_item(vx_resp_account_list_buddies_and_groups_t.getCPtr((vx_resp_account_list_buddies_and_groups_t)object), (vx_resp_account_list_buddies_and_groups_t)object, n)
        if (l != 0L) return vx_group_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_connectivity_test_result_t vx_resp_aux_connectivity_info_t_get_test_results_item(vx_resp_aux_connectivity_info_t object, Int n) {
        var l: Long = VxClientProxyJNI.vx_resp_aux_connectivity_info_t_get_test_results_item(vx_resp_aux_connectivity_info_t.getCPtr((vx_resp_aux_connectivity_info_t)object), (vx_resp_aux_connectivity_info_t)object, n)
        if (l != 0L) return vx_connectivity_test_result_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_state_connector_t vx_resp_aux_diagnostic_state_dump_t_get_state_connectors_item(vx_resp_aux_diagnostic_state_dump_t object, Int n) {
        var l: Long = VxClientProxyJNI.vx_resp_aux_diagnostic_state_dump_t_get_state_connectors_item(vx_resp_aux_diagnostic_state_dump_t.getCPtr((vx_resp_aux_diagnostic_state_dump_t)object), (vx_resp_aux_diagnostic_state_dump_t)object, n)
        if (l != 0L) return vx_state_connector_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_device_t vx_resp_aux_get_capture_devices_t_get_capture_devices_item(vx_resp_aux_get_capture_devices_t object, Int n) {
        var l: Long = VxClientProxyJNI.vx_resp_aux_get_capture_devices_t_get_capture_devices_item(vx_resp_aux_get_capture_devices_t.getCPtr((vx_resp_aux_get_capture_devices_t)object), (vx_resp_aux_get_capture_devices_t)object, n)
        if (l != 0L) return vx_device_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_device_t vx_resp_aux_get_render_devices_t_get_render_devices_item(vx_resp_aux_get_render_devices_t object, Int n) {
        var l: Long = VxClientProxyJNI.vx_resp_aux_get_render_devices_t_get_render_devices_item(vx_resp_aux_get_render_devices_t.getCPtr((vx_resp_aux_get_render_devices_t)object), (vx_resp_aux_get_render_devices_t)object, n)
        if (l != 0L) return vx_device_t(l, false)
        return null
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_participant_t vx_resp_channel_get_banned_users_t_get_banned_users_item(vx_resp_channel_get_banned_users_t object, Int n) {
        var l: Long = VxClientProxyJNI.vx_resp_channel_get_banned_users_t_get_banned_users_item(vx_resp_channel_get_banned_users_t.getCPtr((vx_resp_channel_get_banned_users_t)object), (vx_resp_channel_get_banned_users_t)object, n)
        if (l != 0L) return vx_participant_t(l, false)
        return null
    }

    Unit vx_response_to_xml(SWIGTYPE_p_void sWIGTYPE_p_void, SWIGTYPE_p_p_char sWIGTYPE_p_p_char) {
        VxClientProxyJNI.vx_response_to_xml(SWIGTYPE_p_void.getCPtr(sWIGTYPE_p_void), SWIGTYPE_p_p_char.getCPtr(sWIGTYPE_p_p_char))
    }

    Int vx_set_cert_data(String string2) {
        return VxClientProxyJNI.vx_set_cert_data(string2)
    }

    Int vx_set_cert_data_dir(String string2) {
        return VxClientProxyJNI.vx_set_cert_data_dir(string2)
    }

    Unit vx_set_crash_dump_generation_enabled(Int n) {
        VxClientProxyJNI.vx_set_crash_dump_generation_enabled(n)
    }

    Int vx_set_out_of_process_server_address(String string2, Int n) {
        return VxClientProxyJNI.vx_set_out_of_process_server_address(string2, n)
    }

    Int vx_set_preferred_codec(Int n) {
        return VxClientProxyJNI.vx_set_preferred_codec(n)
    }

    Unit vx_state_account_create(SWIGTYPE_p_p_vx_state_account sWIGTYPE_p_p_vx_state_account) {
        VxClientProxyJNI.vx_state_account_create(SWIGTYPE_p_p_vx_state_account.getCPtr(sWIGTYPE_p_p_vx_state_account))
    }

    Unit vx_state_account_free(vx_state_account_t vx_state_account_t2) {
        VxClientProxyJNI.vx_state_account_free(vx_state_account_t.getCPtr(vx_state_account_t2), vx_state_account_t2)
    }

    Unit vx_state_account_list_create(Int n, SWIGTYPE_p_p_p_vx_state_account sWIGTYPE_p_p_p_vx_state_account) {
        VxClientProxyJNI.vx_state_account_list_create(n, SWIGTYPE_p_p_p_vx_state_account.getCPtr(sWIGTYPE_p_p_p_vx_state_account))
    }

    Unit vx_state_account_list_free(SWIGTYPE_p_p_vx_state_account sWIGTYPE_p_p_vx_state_account, Int n) {
        VxClientProxyJNI.vx_state_account_list_free(SWIGTYPE_p_p_vx_state_account.getCPtr(sWIGTYPE_p_p_vx_state_account), n)
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_state_sessiongroup_t vx_state_account_t_get_state_sessiongroups_item(vx_state_account_t object, Int n) {
        var l: Long = VxClientProxyJNI.vx_state_account_t_get_state_sessiongroups_item(vx_state_account_t.getCPtr((vx_state_account_t)object), (vx_state_account_t)object, n)
        if (l != 0L) return vx_state_sessiongroup_t(l, false)
        return null
    }

    Unit vx_state_buddy_contact_create(SWIGTYPE_p_p_vx_state_buddy_contact sWIGTYPE_p_p_vx_state_buddy_contact) {
        VxClientProxyJNI.vx_state_buddy_contact_create(SWIGTYPE_p_p_vx_state_buddy_contact.getCPtr(sWIGTYPE_p_p_vx_state_buddy_contact))
    }

    Unit vx_state_buddy_contact_free(vx_state_buddy_contact_t vx_state_buddy_contact_t2) {
        VxClientProxyJNI.vx_state_buddy_contact_free(vx_state_buddy_contact_t.getCPtr(vx_state_buddy_contact_t2), vx_state_buddy_contact_t2)
    }

    Unit vx_state_buddy_contact_list_create(Int n, SWIGTYPE_p_p_p_vx_state_buddy_contact sWIGTYPE_p_p_p_vx_state_buddy_contact) {
        VxClientProxyJNI.vx_state_buddy_contact_list_create(n, SWIGTYPE_p_p_p_vx_state_buddy_contact.getCPtr(sWIGTYPE_p_p_p_vx_state_buddy_contact))
    }

    Unit vx_state_buddy_contact_list_free(SWIGTYPE_p_p_vx_state_buddy_contact sWIGTYPE_p_p_vx_state_buddy_contact, Int n) {
        VxClientProxyJNI.vx_state_buddy_contact_list_free(SWIGTYPE_p_p_vx_state_buddy_contact.getCPtr(sWIGTYPE_p_p_vx_state_buddy_contact), n)
    }

    Unit vx_state_buddy_create(SWIGTYPE_p_p_vx_state_buddy sWIGTYPE_p_p_vx_state_buddy) {
        VxClientProxyJNI.vx_state_buddy_create(SWIGTYPE_p_p_vx_state_buddy.getCPtr(sWIGTYPE_p_p_vx_state_buddy))
    }

    Unit vx_state_buddy_free(vx_state_buddy_t vx_state_buddy_t2) {
        VxClientProxyJNI.vx_state_buddy_free(vx_state_buddy_t.getCPtr(vx_state_buddy_t2), vx_state_buddy_t2)
    }

    Unit vx_state_buddy_group_create(SWIGTYPE_p_p_vx_state_buddy_group sWIGTYPE_p_p_vx_state_buddy_group) {
        VxClientProxyJNI.vx_state_buddy_group_create(SWIGTYPE_p_p_vx_state_buddy_group.getCPtr(sWIGTYPE_p_p_vx_state_buddy_group))
    }

    Unit vx_state_buddy_group_free(vx_state_buddy_group_t vx_state_buddy_group_t2) {
        VxClientProxyJNI.vx_state_buddy_group_free(vx_state_buddy_group_t.getCPtr(vx_state_buddy_group_t2), vx_state_buddy_group_t2)
    }

    Unit vx_state_buddy_group_list_create(Int n, SWIGTYPE_p_p_p_vx_state_buddy_group sWIGTYPE_p_p_p_vx_state_buddy_group) {
        VxClientProxyJNI.vx_state_buddy_group_list_create(n, SWIGTYPE_p_p_p_vx_state_buddy_group.getCPtr(sWIGTYPE_p_p_p_vx_state_buddy_group))
    }

    Unit vx_state_buddy_group_list_free(SWIGTYPE_p_p_vx_state_buddy_group sWIGTYPE_p_p_vx_state_buddy_group, Int n) {
        VxClientProxyJNI.vx_state_buddy_group_list_free(SWIGTYPE_p_p_vx_state_buddy_group.getCPtr(sWIGTYPE_p_p_vx_state_buddy_group), n)
    }

    Unit vx_state_buddy_list_create(Int n, SWIGTYPE_p_p_p_vx_state_buddy sWIGTYPE_p_p_p_vx_state_buddy) {
        VxClientProxyJNI.vx_state_buddy_list_create(n, SWIGTYPE_p_p_p_vx_state_buddy.getCPtr(sWIGTYPE_p_p_p_vx_state_buddy))
    }

    Unit vx_state_buddy_list_free(SWIGTYPE_p_p_vx_state_buddy sWIGTYPE_p_p_vx_state_buddy, Int n) {
        VxClientProxyJNI.vx_state_buddy_list_free(SWIGTYPE_p_p_vx_state_buddy.getCPtr(sWIGTYPE_p_p_vx_state_buddy), n)
    }

    Unit vx_state_connector_create(SWIGTYPE_p_p_vx_state_connector sWIGTYPE_p_p_vx_state_connector) {
        VxClientProxyJNI.vx_state_connector_create(SWIGTYPE_p_p_vx_state_connector.getCPtr(sWIGTYPE_p_p_vx_state_connector))
    }

    Unit vx_state_connector_free(vx_state_connector_t vx_state_connector_t2) {
        VxClientProxyJNI.vx_state_connector_free(vx_state_connector_t.getCPtr(vx_state_connector_t2), vx_state_connector_t2)
    }

    Unit vx_state_connector_list_create(Int n, SWIGTYPE_p_p_p_vx_state_connector sWIGTYPE_p_p_p_vx_state_connector) {
        VxClientProxyJNI.vx_state_connector_list_create(n, SWIGTYPE_p_p_p_vx_state_connector.getCPtr(sWIGTYPE_p_p_p_vx_state_connector))
    }

    Unit vx_state_connector_list_free(SWIGTYPE_p_p_vx_state_connector sWIGTYPE_p_p_vx_state_connector, Int n) {
        VxClientProxyJNI.vx_state_connector_list_free(SWIGTYPE_p_p_vx_state_connector.getCPtr(sWIGTYPE_p_p_vx_state_connector), n)
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_state_account_t vx_state_connector_t_get_state_accounts_item(vx_state_connector_t object, Int n) {
        var l: Long = VxClientProxyJNI.vx_state_connector_t_get_state_accounts_item(vx_state_connector_t.getCPtr((vx_state_connector_t)object), (vx_state_connector_t)object, n)
        if (l != 0L) return vx_state_account_t(l, false)
        return null
    }

    Unit vx_state_participant_create(SWIGTYPE_p_p_vx_state_participant sWIGTYPE_p_p_vx_state_participant) {
        VxClientProxyJNI.vx_state_participant_create(SWIGTYPE_p_p_vx_state_participant.getCPtr(sWIGTYPE_p_p_vx_state_participant))
    }

    Unit vx_state_participant_free(vx_state_participant_t vx_state_participant_t2) {
        VxClientProxyJNI.vx_state_participant_free(vx_state_participant_t.getCPtr(vx_state_participant_t2), vx_state_participant_t2)
    }

    Unit vx_state_participant_list_create(Int n, SWIGTYPE_p_p_p_vx_state_participant sWIGTYPE_p_p_p_vx_state_participant) {
        VxClientProxyJNI.vx_state_participant_list_create(n, SWIGTYPE_p_p_p_vx_state_participant.getCPtr(sWIGTYPE_p_p_p_vx_state_participant))
    }

    Unit vx_state_participant_list_free(SWIGTYPE_p_p_vx_state_participant sWIGTYPE_p_p_vx_state_participant, Int n) {
        VxClientProxyJNI.vx_state_participant_list_free(SWIGTYPE_p_p_vx_state_participant.getCPtr(sWIGTYPE_p_p_vx_state_participant), n)
    }

    Unit vx_state_session_create(SWIGTYPE_p_p_vx_state_session sWIGTYPE_p_p_vx_state_session) {
        VxClientProxyJNI.vx_state_session_create(SWIGTYPE_p_p_vx_state_session.getCPtr(sWIGTYPE_p_p_vx_state_session))
    }

    Unit vx_state_session_free(vx_state_session_t vx_state_session_t2) {
        VxClientProxyJNI.vx_state_session_free(vx_state_session_t.getCPtr(vx_state_session_t2), vx_state_session_t2)
    }

    Unit vx_state_session_list_create(Int n, SWIGTYPE_p_p_p_vx_state_session sWIGTYPE_p_p_p_vx_state_session) {
        VxClientProxyJNI.vx_state_session_list_create(n, SWIGTYPE_p_p_p_vx_state_session.getCPtr(sWIGTYPE_p_p_p_vx_state_session))
    }

    Unit vx_state_session_list_free(SWIGTYPE_p_p_vx_state_session sWIGTYPE_p_p_vx_state_session, Int n) {
        VxClientProxyJNI.vx_state_session_list_free(SWIGTYPE_p_p_vx_state_session.getCPtr(sWIGTYPE_p_p_vx_state_session), n)
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_state_participant_t vx_state_session_t_get_state_participants_item(vx_state_session_t object, Int n) {
        var l: Long = VxClientProxyJNI.vx_state_session_t_get_state_participants_item(vx_state_session_t.getCPtr((vx_state_session_t)object), (vx_state_session_t)object, n)
        if (l != 0L) return vx_state_participant_t(l, false)
        return null
    }

    Unit vx_state_sessiongroup_create(SWIGTYPE_p_p_vx_state_sessiongroup sWIGTYPE_p_p_vx_state_sessiongroup) {
        VxClientProxyJNI.vx_state_sessiongroup_create(SWIGTYPE_p_p_vx_state_sessiongroup.getCPtr(sWIGTYPE_p_p_vx_state_sessiongroup))
    }

    Unit vx_state_sessiongroup_free(vx_state_sessiongroup_t vx_state_sessiongroup_t2) {
        VxClientProxyJNI.vx_state_sessiongroup_free(vx_state_sessiongroup_t.getCPtr(vx_state_sessiongroup_t2), vx_state_sessiongroup_t2)
    }

    Unit vx_state_sessiongroup_list_create(Int n, SWIGTYPE_p_p_p_vx_state_sessiongroup sWIGTYPE_p_p_p_vx_state_sessiongroup) {
        VxClientProxyJNI.vx_state_sessiongroup_list_create(n, SWIGTYPE_p_p_p_vx_state_sessiongroup.getCPtr(sWIGTYPE_p_p_p_vx_state_sessiongroup))
    }

    Unit vx_state_sessiongroup_list_free(SWIGTYPE_p_p_vx_state_sessiongroup sWIGTYPE_p_p_vx_state_sessiongroup, Int n) {
        VxClientProxyJNI.vx_state_sessiongroup_list_free(SWIGTYPE_p_p_vx_state_sessiongroup.getCPtr(sWIGTYPE_p_p_vx_state_sessiongroup), n)
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_state_session_t vx_state_sessiongroup_t_get_state_sessions_item(vx_state_sessiongroup_t object, Int n) {
        var l: Long = VxClientProxyJNI.vx_state_sessiongroup_t_get_state_sessions_item(vx_state_sessiongroup_t.getCPtr((vx_state_sessiongroup_t)object), (vx_state_sessiongroup_t)object, n)
        if (l != 0L) return vx_state_session_t(l, false)
        return null
    }

    String vx_strdup(String string2) {
        return VxClientProxyJNI.vx_strdup(string2)
    }

    Unit vx_string_list_create(Int n, SWIGTYPE_p_p_p_char sWIGTYPE_p_p_p_char) {
        VxClientProxyJNI.vx_string_list_create(n, SWIGTYPE_p_p_p_char.getCPtr(sWIGTYPE_p_p_p_char))
    }

    Unit vx_string_list_free(SWIGTYPE_p_p_char sWIGTYPE_p_p_char) {
        VxClientProxyJNI.vx_string_list_free(SWIGTYPE_p_p_char.getCPtr(sWIGTYPE_p_p_char))
    }

    Int vx_uninitialize() {
        return VxClientProxyJNI.vx_uninitialize()
    }

    Unit vx_unregister_logging_handler(SWIGTYPE_p_f_p_void__void sWIGTYPE_p_f_p_void__void, SWIGTYPE_p_void sWIGTYPE_p_void) {
        VxClientProxyJNI.vx_unregister_logging_handler(SWIGTYPE_p_f_p_void__void.getCPtr(sWIGTYPE_p_f_p_void__void), SWIGTYPE_p_void.getCPtr(sWIGTYPE_p_void))
    }

    Unit vx_unregister_message_notification_handler(SWIGTYPE_p_f_p_void__void sWIGTYPE_p_f_p_void__void, SWIGTYPE_p_void sWIGTYPE_p_void) {
        VxClientProxyJNI.vx_unregister_message_notification_handler(SWIGTYPE_p_f_p_void__void.getCPtr(sWIGTYPE_p_f_p_void__void), SWIGTYPE_p_void.getCPtr(sWIGTYPE_p_void))
    }

    Unit vx_user_channel_create(SWIGTYPE_p_p_vx_user_channel sWIGTYPE_p_p_vx_user_channel) {
        VxClientProxyJNI.vx_user_channel_create(SWIGTYPE_p_p_vx_user_channel.getCPtr(sWIGTYPE_p_p_vx_user_channel))
    }

    Unit vx_user_channel_free(vx_user_channel_t vx_user_channel_t2) {
        VxClientProxyJNI.vx_user_channel_free(vx_user_channel_t.getCPtr(vx_user_channel_t2), vx_user_channel_t2)
    }

    Unit vx_user_channels_create(Int n, SWIGTYPE_p_p_p_vx_user_channel sWIGTYPE_p_p_p_vx_user_channel) {
        VxClientProxyJNI.vx_user_channels_create(n, SWIGTYPE_p_p_p_vx_user_channel.getCPtr(sWIGTYPE_p_p_p_vx_user_channel))
    }

    Unit vx_user_channels_free(SWIGTYPE_p_p_vx_user_channel sWIGTYPE_p_p_vx_user_channel, Int n) {
        VxClientProxyJNI.vx_user_channels_free(SWIGTYPE_p_p_vx_user_channel.getCPtr(sWIGTYPE_p_p_vx_user_channel), n)
    }

    Unit vx_voice_font_create(SWIGTYPE_p_p_vx_voice_font sWIGTYPE_p_p_vx_voice_font) {
        VxClientProxyJNI.vx_voice_font_create(SWIGTYPE_p_p_vx_voice_font.getCPtr(sWIGTYPE_p_p_vx_voice_font))
    }

    Unit vx_voice_font_free(vx_voice_font_t vx_voice_font_t2) {
        VxClientProxyJNI.vx_voice_font_free(vx_voice_font_t.getCPtr(vx_voice_font_t2), vx_voice_font_t2)
    }

    Unit vx_voice_font_list_create(Int n, SWIGTYPE_p_p_p_vx_voice_font sWIGTYPE_p_p_p_vx_voice_font) {
        VxClientProxyJNI.vx_voice_font_list_create(n, SWIGTYPE_p_p_p_vx_voice_font.getCPtr(sWIGTYPE_p_p_p_vx_voice_font))
    }

    Unit vx_voice_font_list_free(SWIGTYPE_p_p_vx_voice_font sWIGTYPE_p_p_vx_voice_font, Int n) {
        VxClientProxyJNI.vx_voice_font_list_free(SWIGTYPE_p_p_vx_voice_font.getCPtr(sWIGTYPE_p_p_vx_voice_font), n)
    }

    Int vx_vxr_file_close(SWIGTYPE_p_void sWIGTYPE_p_void) {
        return VxClientProxyJNI.vx_vxr_file_close(SWIGTYPE_p_void.getCPtr(sWIGTYPE_p_void))
    }

    Int vx_vxr_file_get_frame_count(SWIGTYPE_p_void sWIGTYPE_p_void, SWIGTYPE_p_int sWIGTYPE_p_int) {
        return VxClientProxyJNI.vx_vxr_file_get_frame_count(SWIGTYPE_p_void.getCPtr(sWIGTYPE_p_void), SWIGTYPE_p_int.getCPtr(sWIGTYPE_p_int))
    }

    Int vx_vxr_file_move_to_frame(SWIGTYPE_p_void sWIGTYPE_p_void, Int n) {
        return VxClientProxyJNI.vx_vxr_file_move_to_frame(SWIGTYPE_p_void.getCPtr(sWIGTYPE_p_void), n)
    }

    Int vx_vxr_file_open(String string2, String string3, SWIGTYPE_p_p_void sWIGTYPE_p_p_void) {
        return VxClientProxyJNI.vx_vxr_file_open(string2, string3, SWIGTYPE_p_p_void.getCPtr(sWIGTYPE_p_p_void))
    }

    Int vx_vxr_file_read_frame(SWIGTYPE_p_void sWIGTYPE_p_void, SWIGTYPE_p_void sWIGTYPE_p_void2, Int n, SWIGTYPE_p_int sWIGTYPE_p_int, SWIGTYPE_p_vx_recording_frame_type_t sWIGTYPE_p_vx_recording_frame_type_t) {
        return VxClientProxyJNI.vx_vxr_file_read_frame(SWIGTYPE_p_void.getCPtr(sWIGTYPE_p_void), SWIGTYPE_p_void.getCPtr(sWIGTYPE_p_void2), n, SWIGTYPE_p_int.getCPtr(sWIGTYPE_p_int), SWIGTYPE_p_vx_recording_frame_type_t.getCPtr(sWIGTYPE_p_vx_recording_frame_type_t))
    }

    Int vx_vxr_file_write_frame(SWIGTYPE_p_void sWIGTYPE_p_void, vx_recording_frame_type_t vx_recording_frame_type_t2, SWIGTYPE_p_void sWIGTYPE_p_void2, Int n) {
        return VxClientProxyJNI.vx_vxr_file_write_frame(SWIGTYPE_p_void.getCPtr(sWIGTYPE_p_void), vx_recording_frame_type_t2.swigValue(), SWIGTYPE_p_void.getCPtr(sWIGTYPE_p_void2), n)
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_message_base_t vx_wait_for_message(Int n) {
        var l: Long = VxClientProxyJNI.vx_wait_for_message(n)
        if (l != 0L) return vx_message_base_t(l, false)
        return null
    }

    vx_event_type vx_xml_to_event(String string2, SWIGTYPE_p_p_void sWIGTYPE_p_p_void, SWIGTYPE_p_p_char sWIGTYPE_p_p_char) {
        return vx_event_type.swigToEnum(VxClientProxyJNI.vx_xml_to_event(string2, SWIGTYPE_p_p_void.getCPtr(sWIGTYPE_p_p_void), SWIGTYPE_p_p_char.getCPtr(sWIGTYPE_p_p_char)))
    }

    vx_request_type vx_xml_to_request(String string2, SWIGTYPE_p_p_void sWIGTYPE_p_p_void, SWIGTYPE_p_p_char sWIGTYPE_p_p_char) {
        return vx_request_type.swigToEnum(VxClientProxyJNI.vx_xml_to_request(string2, SWIGTYPE_p_p_void.getCPtr(sWIGTYPE_p_p_void), SWIGTYPE_p_p_char.getCPtr(sWIGTYPE_p_p_char)))
    }

    vx_response_type vx_xml_to_response(String string2, SWIGTYPE_p_p_void sWIGTYPE_p_p_void, SWIGTYPE_p_p_char sWIGTYPE_p_p_char) {
        return vx_response_type.swigToEnum(VxClientProxyJNI.vx_xml_to_response(string2, SWIGTYPE_p_p_void.getCPtr(sWIGTYPE_p_p_void), SWIGTYPE_p_p_char.getCPtr(sWIGTYPE_p_p_char)))
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_req_base_t xml_to_request(String object) {
        var l: Long = VxClientProxyJNI.xml_to_request((String)object)
        if (l != 0L) return vx_req_base_t(l, false)
        return null
    }
}

