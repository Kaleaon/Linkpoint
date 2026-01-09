/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.vx_account_t
import com.vivox.service.vx_auto_accept_rule_t
import com.vivox.service.vx_block_rule_t
import com.vivox.service.vx_buddy_t
import com.vivox.service.vx_channel_favorite_group_t
import com.vivox.service.vx_channel_favorite_t
import com.vivox.service.vx_channel_t
import com.vivox.service.vx_connectivity_test_result_t
import com.vivox.service.vx_device_t
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
import com.vivox.service.vx_generic_credentials
import com.vivox.service.vx_group_t
import com.vivox.service.vx_message_base_t
import com.vivox.service.vx_name_value_pair_t
import com.vivox.service.vx_participant_t
import com.vivox.service.vx_recording_frame_t
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
import com.vivox.service.vx_req_aux_diagnostic_state_dump_t
import com.vivox.service.vx_req_aux_get_capture_devices_t
import com.vivox.service.vx_req_aux_get_mic_level_t
import com.vivox.service.vx_req_aux_get_render_devices_t
import com.vivox.service.vx_req_aux_get_speaker_level_t
import com.vivox.service.vx_req_aux_get_vad_properties_t
import com.vivox.service.vx_req_aux_global_monitor_keyboard_mouse_t
import com.vivox.service.vx_req_aux_notify_application_state_change_t
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
import com.vivox.service.vx_resp_aux_notify_application_state_change_t
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
import com.vivox.service.vx_sdk_config_t
import com.vivox.service.vx_stat_sample_t
import com.vivox.service.vx_stat_thread_t
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

class VxClientProxyJNI {
    {
        System.loadLibrary("vxaudio-jni")
        System.loadLibrary("sndfile")
        System.loadLibrary("oRTP")
        System.loadLibrary("vivoxsdk")
        System.loadLibrary("VxClient")
    }

    VxClientProxyJNI() {
    }

    native String BUILD_DATE_get()

    native String BUILD_HOST_get()

    native String BUILD_PERSON_get()

    native Int ND_E_NO_ERROR_get()

    native String SDK_BRANCH_get()

    native String SDK_VERSION_get()

    native Int SUBVERSION_CHANGE_get()

    native String SUBVERSION_DATE_get()

    native Int VERSION_BUILD_get()

    native Int VERSION_MAJOR_get()

    native Int VERSION_MICRO_get()

    native Int VERSION_MINOR_get()

    native String VIVOX_BUILD_TYPE_get()

    native Int VIVOX_SDK_ACCOUNT_CHANNEL_CREATE_AND_INVITE_OBSOLETE_get()

    native Int VIVOX_SDK_EVT_SESSION_PARTICIPANT_LIST_OBSOLETE_get()

    native Int VIVOX_SDK_HAS_ACCOUNT_SEND_MSG_get()

    native Int VIVOX_SDK_HAS_AUX_DIAGNOSTIC_STATE_get()

    native Int VIVOX_SDK_HAS_CLIENT_SIDE_RECORDING_V2_get()

    native Int VIVOX_SDK_HAS_CLIENT_SIDE_RECORDING_get()

    native Int VIVOX_SDK_HAS_CRASH_REPORTING_get()

    native Int VIVOX_SDK_HAS_FRAME_TOTALS_get()

    native Int VIVOX_SDK_HAS_GENERIC_APP_NOTIFICATIONS_ONLY_get()

    native Int VIVOX_SDK_HAS_GROUP_IM_get()

    native Int VIVOX_SDK_HAS_INTEGRATED_PROXY_get()

    native Int VIVOX_SDK_HAS_MUTE_SCOPE_get()

    native Int VIVOX_SDK_HAS_NETWORK_MESSAGE_get()

    native Int VIVOX_SDK_HAS_NO_CHANNEL_FOLDERS_get()

    native Int VIVOX_SDK_HAS_NO_SCORE_get()

    native Int VIVOX_SDK_HAS_PARTICIPANT_TYPE_get()

    native Int VIVOX_SDK_HAS_VOICE_FONTS_get()

    native Int VIVOX_SDK_NO_IS_AD_PLAYING_get()

    native Int VIVOX_SDK_NO_LEGACY_RECORDING_get()

    native Int VIVOX_SDK_SESSION_CHANNEL_GET_PARTICIPANTS_OBSOLETE_get()

    native Int VIVOX_SDK_SESSION_CONNECT_OBSOLETE_get()

    native Int VIVOX_SDK_SESSION_GET_LOCAL_AUDIO_INFO_OBSOLETE_get()

    native Int VIVOX_SDK_SESSION_MEDIA_RINGBACK_OBSOLETE_get()

    native Int VIVOX_SDK_SESSION_RENDER_AUDIO_OBSOLETE_get()

    native Int VIVOX_V_V2_AUDIO_DATA_MONO_PCMU_8000_COLLAPSED_get()

    native Int VIVOX_V_V2_AUDIO_DATA_MONO_PCMU_8000_EXPANDED_get()

    native Int VIVOX_V_V2_AUDIO_DATA_MONO_PCMU_get()

    native Int VIVOX_V_V2_AUDIO_DATA_MONO_SIREN14_32000_EXPANDED_get()

    native Int VIVOX_V_V2_AUDIO_DATA_MONO_SIREN14_32000_get()

    native Int VXCLIENT_SWIG_VERSION_get()

    native Int VX_E_ACCOUNT_MISCONFIGURED_get()

    native Int VX_E_ALREADY_LOGGED_IN_get()

    native Int VX_E_ALREADY_LOGGED_OUT_get()

    native Int VX_E_BUFSIZE_get()

    native Int VX_E_CALL_CREATION_FAILED_get()

    native Int VX_E_CAPACITY_EXCEEDED_get()

    native Int VX_E_CAPTURE_DEVICE_IN_USE_get()

    native Int VX_E_CHANNEL_URI_REQUIRED_get()

    native Int VX_E_CROSS_DOMAIN_LOGINS_DISABLED_get()

    native Int VX_E_FAILED_TO_CONNECT_TO_SERVER_get()

    native Int VX_E_FAILED_TO_CONNECT_TO_VOICE_SERVICE_get()

    native Int VX_E_FAILED_TO_SEND_REQUEST_TO_VOICE_SERVICE_get()

    native Int VX_E_FAILED_get()

    native Int VX_E_FILE_CORRUPT_get()

    native Int VX_E_FILE_OPEN_FAILED_get()

    native Int VX_E_FILE_WRITE_FAILED_REACHED_MAX_FILESIZE_get()

    native Int VX_E_FILE_WRITE_FAILED_get()

    native Int VX_E_INSUFFICIENT_PRIVILEGE_get()

    native Int VX_E_INVALID_APP_TOKEN_get()

    native Int VX_E_INVALID_ARGUMENT_get()

    native Int VX_E_INVALID_AUTH_TOKEN_get()

    native Int VX_E_INVALID_CAPTURE_DEVICE_FOR_REQUESTED_OPERATION_get()

    native Int VX_E_INVALID_CAPTURE_DEVICE_SPECIFIER_get()

    native Int VX_E_INVALID_CODEC_TYPE_get()

    native Int VX_E_INVALID_CONNECTOR_STATE_get()

    native Int VX_E_INVALID_FILE_OPERATION_get()

    native Int VX_E_INVALID_MASK_get()

    native Int VX_E_INVALID_MEDIA_FORMAT_get()

    native Int VX_E_INVALID_RENDER_DEVICE_SPECIFIER_get()

    native Int VX_E_INVALID_SDK_HANDLE_get()

    native Int VX_E_INVALID_SESSION_STATE_get()

    native Int VX_E_INVALID_SUBSCRIPTION_RULE_TYPE_get()

    native Int VX_E_INVALID_USERNAME_OR_PASSWORD_get()

    native Int VX_E_INVALID_XML_get()

    native Int VX_E_LOGIN_FAILED_get()

    native Int VX_E_LOOP_MODE_RECORDING_NOT_ENABLED_get()

    native Int VX_E_MAXIMUM_NUMBER_OF_CALLS_EXCEEEDED_get()

    native Int VX_E_MAX_CONNECTOR_LIMIT_EXCEEDED_get()

    native Int VX_E_MAX_HTTP_DATA_RESPONSE_SIZE_EXCEEDED_get()

    native Int VX_E_MAX_LOGINS_PER_USER_EXCEEDED_get()

    native Int VX_E_MAX_PLAYBACK_SESSIONGROUPS_EXCEEDED_get()

    native Int VX_E_MAX_SESSION_LIMIT_EXCEEDED_get()

    native Int VX_E_MEDIA_DISCONNECT_NOT_ALLOWED_get()

    native Int VX_E_MULTI_CHANNEL_DENIED_get()

    native Int VX_E_NOT_IMPL_get()

    native Int VX_E_NOT_INITIALIZED_get()

    native Int VX_E_NOT_LOGGED_IN_get()

    native Int VX_E_NO_CAPTURE_DEVICES_FOUND_get()

    native Int VX_E_NO_EXIST_get()

    native Int VX_E_NO_MORE_FRAMES_get()

    native Int VX_E_NO_RENDER_DEVICES_FOUND_get()

    native Int VX_E_NO_SESSION_PORTS_AVAILABLE_get()

    native Int VX_E_NO_SUCH_SESSION_get()

    native Int VX_E_PRELOGIN_INFO_NOT_RETURNED_get()

    native Int VX_E_RECORDING_ALREADY_ACTIVE_get()

    native Int VX_E_RECORDING_LOOP_BUFFER_EMPTY_get()

    native Int VX_E_RENDER_CONTEXT_DOES_NOT_EXIST_get()

    native Int VX_E_RENDER_DEVICE_DOES_NOT_EXIST_get()

    native Int VX_E_RENDER_DEVICE_IN_USE_get()

    native Int VX_E_RENDER_SOURCE_DOES_NOT_EXIST_get()

    native Int VX_E_REQUESTCONTEXT_NOT_FOUND_get()

    native Int VX_E_REQUEST_CANCELLED_get()

    native Int VX_E_REQUEST_NOT_SUPPORTED_get()

    native Int VX_E_REQUEST_TYPE_NOT_SUPPORTED_get()

    native Int VX_E_RTP_TIMEOUT_get()

    native Int VX_E_SESSIONGROUP_NOT_FOUND_get()

    native Int VX_E_SESSIONGROUP_TRANSMIT_NOT_ALLOWED_get()

    native Int VX_E_SESSION_CHANNEL_TEXT_DENIED_get()

    native Int VX_E_SESSION_CREATE_PENDING_get()

    native Int VX_E_SESSION_DOES_NOT_HAVE_AUDIO_get()

    native Int VX_E_SESSION_DOES_NOT_HAVE_TEXT_get()

    native Int VX_E_SESSION_IS_NOT_3D_get()

    native Int VX_E_SESSION_MAX_get()

    native Int VX_E_SESSION_MEDIA_CONNECT_FAILED_get()

    native Int VX_E_SESSION_MEDIA_DISCONNECT_FAILED_get()

    native Int VX_E_SESSION_MESSAGE_BUILD_FAILED_get()

    native Int VX_E_SESSION_MSG_CONTENT_TYPE_FAILED_get()

    native Int VX_E_SESSION_MUST_HAVE_MEDIA_get()

    native Int VX_E_SESSION_TERMINATE_PENDING_get()

    native Int VX_E_SESSION_TEXT_DENIED_get()

    native Int VX_E_SESSION_TEXT_DISABLED_get()

    native Int VX_E_STREAM_READ_FAILED_get()

    native Int VX_E_SUBSCRIPTION_NOT_FOUND_get()

    native Int VX_E_TERMINATESESSION_NOT_VALID_get()

    native Int VX_E_TEXT_CONNECT_NOT_ALLOWED_get()

    native Int VX_E_TEXT_DISABLED_get()

    native Int VX_E_TEXT_DISCONNECT_NOT_ALLOWED_get()

    native Int VX_E_UNABLE_TO_OPEN_CAPTURE_DEVICE_get()

    native Int VX_E_UNEXPECTED_END_OF_FILE_get()

    native Int VX_E_VOICE_FONT_NOT_FOUND_get()

    native Int VX_E_WRONG_CONNECTOR_get()

    native Int VX_MEDIA_FLAGS_AUDIO_get()

    native Int VX_MEDIA_FLAGS_TEXT_get()

    native Int VX_RECORDING_FRAME_TYPE_CONTROL_get()

    native Int VX_RECORDING_FRAME_TYPE_DELTA_get()

    native Int VX_SESSIONGROUP_AUDIO_INJECTION_CONTROL_MAX_get()

    native Int VX_SESSIONGROUP_AUDIO_INJECTION_CONTROL_MIN_get()

    native Int VX_SESSIONGROUP_AUDIO_INJECTION_CONTROL_RESTART_get()

    native Int VX_SESSIONGROUP_AUDIO_INJECTION_CONTROL_START_get()

    native Int VX_SESSIONGROUP_AUDIO_INJECTION_CONTROL_STOP_get()

    native Int VX_SESSIONGROUP_PLAYBACK_CONTROL_PAUSE_get()

    native Int VX_SESSIONGROUP_PLAYBACK_CONTROL_START_get()

    native Int VX_SESSIONGROUP_PLAYBACK_CONTROL_STOP_get()

    native Int VX_SESSIONGROUP_PLAYBACK_CONTROL_UNPAUSE_get()

    native Int VX_SESSIONGROUP_PLAYBACK_MODE_NORMAL_get()

    native Int VX_SESSIONGROUP_PLAYBACK_MODE_VOX_get()

    native Int VX_SESSIONGROUP_RECORDING_CONTROL_FLUSH_TO_FILE_get()

    native Int VX_SESSIONGROUP_RECORDING_CONTROL_START_get()

    native Int VX_SESSIONGROUP_RECORDING_CONTROL_STOP_get()

    native Int aux_audio_properties_none_get()

    native Int aux_buffer_audio_capture_get()

    native Int aux_buffer_audio_render_get()

    native Int buddy_presence_away_get()

    native Int buddy_presence_brb_get()

    native Int buddy_presence_busy_get()

    native Int buddy_presence_closed_get()

    native Int buddy_presence_custom_get()

    native Int buddy_presence_offline_get()

    native Int buddy_presence_online_get()

    native Int buddy_presence_online_slc_get()

    native Int buddy_presence_onthephone_get()

    native Int buddy_presence_outtolunch_get()

    native Int buddy_presence_pending_get()

    native Int buddy_presence_unknown_get()

    native Int change_type_delete_get()

    native Int change_type_set_get()

    native Int channel_mode_auditorium_get()

    native Int channel_mode_lecture_get()

    native Int channel_mode_none_get()

    native Int channel_mode_normal_get()

    native Int channel_mode_open_get()

    native Int channel_mode_presentation_get()

    native Int channel_moderation_type_all_get()

    native Int channel_moderation_type_current_user_get()

    native Int channel_search_type_all_get()

    native Int channel_search_type_non_positional_get()

    native Int channel_search_type_positional_get()

    native Int channel_type_normal_get()

    native Int channel_type_positional_get()

    native Int channel_unlock_get()

    native Unit clear_stats()

    native Int connector_mode_normal_get()

    native Unit delete_vx_account_t(Long var0)

    native Unit delete_vx_auto_accept_rule_t(Long var0)

    native Unit delete_vx_block_rule_t(Long var0)

    native Unit delete_vx_buddy_t(Long var0)

    native Unit delete_vx_channel_favorite_group_t(Long var0)

    native Unit delete_vx_channel_favorite_t(Long var0)

    native Unit delete_vx_channel_t(Long var0)

    native Unit delete_vx_device_t(Long var0)

    native Unit delete_vx_group_t(Long var0)

    native Unit delete_vx_participant_t(Long var0)

    native Unit delete_vx_recording_frame_t(Long var0)

    native Unit delete_vx_voice_font_t(Long var0)

    native Unit destroy_evt(Long var0, vx_evt_base_t var2)

    native Unit destroy_message(Long var0, vx_message_base_t var2)

    native Unit destroy_req(Long var0, vx_req_base_t var2)

    native Unit destroy_resp(Long var0, vx_resp_base_t var2)

    native Int diagnostic_dump_level_all_get()

    native Int dtmf_0_get()

    native Int dtmf_1_get()

    native Int dtmf_2_get()

    native Int dtmf_3_get()

    native Int dtmf_4_get()

    native Int dtmf_5_get()

    native Int dtmf_6_get()

    native Int dtmf_7_get()

    native Int dtmf_8_get()

    native Int dtmf_9_get()

    native Int dtmf_A_get()

    native Int dtmf_B_get()

    native Int dtmf_C_get()

    native Int dtmf_D_get()

    native Int dtmf_max_get()

    native Int dtmf_pound_get()

    native Int dtmf_star_get()

    native Int evt_account_login_state_change_get()

    native Int evt_aux_audio_properties_get()

    native Int evt_buddy_and_group_list_changed_get()

    native Int evt_buddy_changed_get()

    native Int evt_buddy_group_changed_get()

    native Int evt_buddy_presence_get()

    native Int evt_idle_state_changed_get()

    native Int evt_keyboard_mouse_get()

    native Int evt_max_get()

    native Int evt_media_completion_get()

    native Int evt_media_stream_updated_get()

    native Int evt_message_get()

    native Int evt_network_message_get()

    native Int evt_none_get()

    native Int evt_participant_added_get()

    native Int evt_participant_removed_get()

    native Int evt_participant_updated_get()

    native Int evt_server_app_data_get()

    native Int evt_session_added_get()

    native Int evt_session_notification_get()

    native Int evt_session_removed_get()

    native Int evt_session_updated_get()

    native Int evt_sessiongroup_added_get()

    native Int evt_sessiongroup_playback_frame_played_get()

    native Int evt_sessiongroup_removed_get()

    native Int evt_sessiongroup_updated_get()

    native Int evt_subscription_get()

    native Int evt_text_stream_updated_get()

    native Int evt_user_app_data_get()

    native Int evt_voice_service_connection_state_changed_get()

    native Unit free_sdk_string(Long var0)

    native String get_java_system_property(String var0)

    native Long get_next_message(Int var0)

    native Long get_next_message_no_wait()

    native Long get_stats()

    native Int log_error_get()

    native Int log_to_none_get()

    native Int login_state_error_get()

    native Int login_state_logged_in_get()

    native Int login_state_logged_out_get()

    native Int login_state_logging_in_get()

    native Int login_state_logging_out_get()

    native Int login_state_resetting_get()

    native Int media_codec_type_nm_get()

    native Int media_codec_type_none_get()

    native Int media_codec_type_pcmu_get()

    native Int media_codec_type_siren14_get()

    native Int media_codec_type_speex_get()

    native Int media_completion_type_none_get()

    native Int media_ringback_busy_get()

    native Int media_ringback_none_get()

    native Int media_ringback_ringing_get()

    native Int media_type_none_get()

    native Int message_none_get()

    native Long message_to_xml(Long var0, vx_message_base_t var2)

    native Int mode_auto_accept_get()

    native Int mode_auto_add_get()

    native Int mode_auto_answer_get()

    native Int mode_busy_answer_get()

    native Int mode_none_get()

    native Int mode_verify_answer_get()

    native Int msg_evt_subtype(Long var0, vx_message_base_t var2)

    native Int msg_none_get()

    native Int msg_req_subtype(Long var0, vx_message_base_t var2)

    native Int msg_request_get()

    native Int msg_resp_subtype(Long var0, vx_message_base_t var2)

    native Int mute_scope_all_get()

    native Int mute_scope_audio_get()

    native Int mute_scope_text_get()

    native Long name_value_pairs_create(Int var0)

    native Long new_vx_account_t()

    native Long new_vx_auto_accept_rule_t()

    native Long new_vx_block_rule_t()

    native Long new_vx_buddy_t()

    native Long new_vx_channel_favorite_group_t()

    native Long new_vx_channel_favorite_t()

    native Long new_vx_channel_t()

    native Long new_vx_connectivity_test_result_t()

    native Long new_vx_device_t()

    native Long new_vx_evt_account_login_state_change_t()

    native Long new_vx_evt_aux_audio_properties_t()

    native Long new_vx_evt_base_t()

    native Long new_vx_evt_buddy_and_group_list_changed_t()

    native Long new_vx_evt_buddy_changed_t()

    native Long new_vx_evt_buddy_group_changed_t()

    native Long new_vx_evt_buddy_presence_t()

    native Long new_vx_evt_idle_state_changed_t()

    native Long new_vx_evt_keyboard_mouse_t()

    native Long new_vx_evt_media_completion_t()

    native Long new_vx_evt_media_stream_updated_t()

    native Long new_vx_evt_message_t()

    native Long new_vx_evt_network_message_t()

    native Long new_vx_evt_participant_added_t()

    native Long new_vx_evt_participant_removed_t()

    native Long new_vx_evt_participant_updated_t()

    native Long new_vx_evt_server_app_data_t()

    native Long new_vx_evt_session_added_t()

    native Long new_vx_evt_session_notification_t()

    native Long new_vx_evt_session_removed_t()

    native Long new_vx_evt_session_updated_t()

    native Long new_vx_evt_sessiongroup_added_t()

    native Long new_vx_evt_sessiongroup_playback_frame_played_t()

    native Long new_vx_evt_sessiongroup_removed_t()

    native Long new_vx_evt_sessiongroup_updated_t()

    native Long new_vx_evt_subscription_t()

    native Long new_vx_evt_text_stream_updated_t()

    native Long new_vx_evt_user_app_data_t()

    native Long new_vx_evt_voice_service_connection_state_changed_t()

    native Long new_vx_generic_credentials()

    native Long new_vx_group_t()

    native Long new_vx_message_base_t()

    native Long new_vx_name_value_pair_t()

    native Long new_vx_participant_t()

    native Long new_vx_recording_frame_t()

    native Long new_vx_req_account_anonymous_login_t()

    native Long new_vx_req_account_authtoken_login_t()

    native Long new_vx_req_account_buddy_delete_t()

    native Long new_vx_req_account_buddy_search_t()

    native Long new_vx_req_account_buddy_set_t()

    native Long new_vx_req_account_buddygroup_delete_t()

    native Long new_vx_req_account_buddygroup_set_t()

    native Long new_vx_req_account_channel_add_acl_t()

    native Long new_vx_req_account_channel_add_moderator_t()

    native Long new_vx_req_account_channel_change_owner_t()

    native Long new_vx_req_account_channel_create_t()

    native Long new_vx_req_account_channel_delete_t()

    native Long new_vx_req_account_channel_favorite_delete_t()

    native Long new_vx_req_account_channel_favorite_group_delete_t()

    native Long new_vx_req_account_channel_favorite_group_set_t()

    native Long new_vx_req_account_channel_favorite_set_t()

    native Long new_vx_req_account_channel_favorites_get_list_t()

    native Long new_vx_req_account_channel_get_acl_t()

    native Long new_vx_req_account_channel_get_info_t()

    native Long new_vx_req_account_channel_get_moderators_t()

    native Long new_vx_req_account_channel_get_participants_t()

    native Long new_vx_req_account_channel_remove_acl_t()

    native Long new_vx_req_account_channel_remove_moderator_t()

    native Long new_vx_req_account_channel_search_t()

    native Long new_vx_req_account_channel_update_t()

    native Long new_vx_req_account_create_auto_accept_rule_t()

    native Long new_vx_req_account_create_block_rule_t()

    native Long new_vx_req_account_delete_auto_accept_rule_t()

    native Long new_vx_req_account_delete_block_rule_t()

    native Long new_vx_req_account_get_account_t()

    native Long new_vx_req_account_get_session_fonts_t()

    native Long new_vx_req_account_get_template_fonts_t()

    native Long new_vx_req_account_list_auto_accept_rules_t()

    native Long new_vx_req_account_list_block_rules_t()

    native Long new_vx_req_account_list_buddies_and_groups_t()

    native Long new_vx_req_account_login_t()

    native Long new_vx_req_account_logout_t()

    native Long new_vx_req_account_post_crash_dump_t()

    native Long new_vx_req_account_send_message_t()

    native Long new_vx_req_account_send_sms_t()

    native Long new_vx_req_account_send_subscription_reply_t()

    native Long new_vx_req_account_send_user_app_data_t()

    native Long new_vx_req_account_set_login_properties_t()

    native Long new_vx_req_account_set_presence_t()

    native Long new_vx_req_account_update_account_t()

    native Long new_vx_req_account_web_call_t()

    native Long new_vx_req_aux_capture_audio_start_t()

    native Long new_vx_req_aux_capture_audio_stop_t()

    native Long new_vx_req_aux_connectivity_info_t()

    native Long new_vx_req_aux_create_account_t()

    native Long new_vx_req_aux_deactivate_account_t()

    native Long new_vx_req_aux_diagnostic_state_dump_t()

    native Long new_vx_req_aux_get_capture_devices_t()

    native Long new_vx_req_aux_get_mic_level_t()

    native Long new_vx_req_aux_get_render_devices_t()

    native Long new_vx_req_aux_get_speaker_level_t()

    native Long new_vx_req_aux_get_vad_properties_t()

    native Long new_vx_req_aux_global_monitor_keyboard_mouse_t()

    native Long new_vx_req_aux_notify_application_state_change_t()

    native Long new_vx_req_aux_play_audio_buffer_t()

    native Long new_vx_req_aux_reactivate_account_t()

    native Long new_vx_req_aux_render_audio_modify_t()

    native Long new_vx_req_aux_render_audio_start_t()

    native Long new_vx_req_aux_render_audio_stop_t()

    native Long new_vx_req_aux_reset_password_t()

    native Long new_vx_req_aux_set_capture_device_t()

    native Long new_vx_req_aux_set_idle_timeout_t()

    native Long new_vx_req_aux_set_mic_level_t()

    native Long new_vx_req_aux_set_render_device_t()

    native Long new_vx_req_aux_set_speaker_level_t()

    native Long new_vx_req_aux_set_vad_properties_t()

    native Long new_vx_req_aux_start_buffer_capture_t()

    native Long new_vx_req_base_t()

    native Long new_vx_req_channel_ban_user_t()

    native Long new_vx_req_channel_get_banned_users_t()

    native Long new_vx_req_channel_kick_user_t()

    native Long new_vx_req_channel_mute_all_users_t()

    native Long new_vx_req_channel_mute_user_t()

    native Long new_vx_req_channel_set_lock_mode_t()

    native Long new_vx_req_connector_create_t()

    native Long new_vx_req_connector_get_local_audio_info_t()

    native Long new_vx_req_connector_initiate_shutdown_t()

    native Long new_vx_req_connector_mute_local_mic_t()

    native Long new_vx_req_connector_mute_local_speaker_t()

    native Long new_vx_req_connector_set_local_mic_volume_t()

    native Long new_vx_req_connector_set_local_speaker_volume_t()

    native Long new_vx_req_session_channel_invite_user_t()

    native Long new_vx_req_session_create_t()

    native Long new_vx_req_session_media_connect_t()

    native Long new_vx_req_session_media_disconnect_t()

    native Long new_vx_req_session_mute_local_speaker_t()

    native Long new_vx_req_session_send_dtmf_t()

    native Long new_vx_req_session_send_message_t()

    native Long new_vx_req_session_send_notification_t()

    native Long new_vx_req_session_set_3d_position_t()

    native Long new_vx_req_session_set_local_speaker_volume_t()

    native Long new_vx_req_session_set_participant_mute_for_me_t()

    native Long new_vx_req_session_set_participant_volume_for_me_t()

    native Long new_vx_req_session_set_voice_font_t()

    native Long new_vx_req_session_terminate_t()

    native Long new_vx_req_session_text_connect_t()

    native Long new_vx_req_session_text_disconnect_t()

    native Long new_vx_req_sessiongroup_add_session_t()

    native Long new_vx_req_sessiongroup_control_audio_injection_t()

    native Long new_vx_req_sessiongroup_control_playback_t()

    native Long new_vx_req_sessiongroup_control_recording_t()

    native Long new_vx_req_sessiongroup_create_t()

    native Long new_vx_req_sessiongroup_get_stats_t()

    native Long new_vx_req_sessiongroup_remove_session_t()

    native Long new_vx_req_sessiongroup_reset_focus_t()

    native Long new_vx_req_sessiongroup_set_focus_t()

    native Long new_vx_req_sessiongroup_set_playback_options_t()

    native Long new_vx_req_sessiongroup_set_session_3d_position_t()

    native Long new_vx_req_sessiongroup_set_tx_all_sessions_t()

    native Long new_vx_req_sessiongroup_set_tx_no_session_t()

    native Long new_vx_req_sessiongroup_set_tx_session_t()

    native Long new_vx_req_sessiongroup_terminate_t()

    native Long new_vx_req_sessiongroup_unset_focus_t()

    native Long new_vx_resp_account_anonymous_login_t()

    native Long new_vx_resp_account_authtoken_login_t()

    native Long new_vx_resp_account_buddy_delete_t()

    native Long new_vx_resp_account_buddy_search_t()

    native Long new_vx_resp_account_buddy_set_t()

    native Long new_vx_resp_account_buddygroup_delete_t()

    native Long new_vx_resp_account_buddygroup_set_t()

    native Long new_vx_resp_account_channel_add_acl_t()

    native Long new_vx_resp_account_channel_add_moderator_t()

    native Long new_vx_resp_account_channel_change_owner_t()

    native Long new_vx_resp_account_channel_create_t()

    native Long new_vx_resp_account_channel_delete_t()

    native Long new_vx_resp_account_channel_favorite_delete_t()

    native Long new_vx_resp_account_channel_favorite_group_delete_t()

    native Long new_vx_resp_account_channel_favorite_group_set_t()

    native Long new_vx_resp_account_channel_favorite_set_t()

    native Long new_vx_resp_account_channel_favorites_get_list_t()

    native Long new_vx_resp_account_channel_get_acl_t()

    native Long new_vx_resp_account_channel_get_info_t()

    native Long new_vx_resp_account_channel_get_moderators_t()

    native Long new_vx_resp_account_channel_get_participants_t()

    native Long new_vx_resp_account_channel_remove_acl_t()

    native Long new_vx_resp_account_channel_remove_moderator_t()

    native Long new_vx_resp_account_channel_search_t()

    native Long new_vx_resp_account_channel_update_t()

    native Long new_vx_resp_account_create_auto_accept_rule_t()

    native Long new_vx_resp_account_create_block_rule_t()

    native Long new_vx_resp_account_delete_auto_accept_rule_t()

    native Long new_vx_resp_account_delete_block_rule_t()

    native Long new_vx_resp_account_get_account_t()

    native Long new_vx_resp_account_get_session_fonts_t()

    native Long new_vx_resp_account_get_template_fonts_t()

    native Long new_vx_resp_account_list_auto_accept_rules_t()

    native Long new_vx_resp_account_list_block_rules_t()

    native Long new_vx_resp_account_list_buddies_and_groups_t()

    native Long new_vx_resp_account_login_t()

    native Long new_vx_resp_account_logout_t()

    native Long new_vx_resp_account_post_crash_dump_t()

    native Long new_vx_resp_account_send_message_t()

    native Long new_vx_resp_account_send_sms_t()

    native Long new_vx_resp_account_send_subscription_reply_t()

    native Long new_vx_resp_account_send_user_app_data_t()

    native Long new_vx_resp_account_set_login_properties_t()

    native Long new_vx_resp_account_set_presence_t()

    native Long new_vx_resp_account_update_account_t()

    native Long new_vx_resp_account_web_call_t()

    native Long new_vx_resp_aux_capture_audio_start_t()

    native Long new_vx_resp_aux_capture_audio_stop_t()

    native Long new_vx_resp_aux_connectivity_info_t()

    native Long new_vx_resp_aux_create_account_t()

    native Long new_vx_resp_aux_deactivate_account_t()

    native Long new_vx_resp_aux_diagnostic_state_dump_t()

    native Long new_vx_resp_aux_get_capture_devices_t()

    native Long new_vx_resp_aux_get_mic_level_t()

    native Long new_vx_resp_aux_get_render_devices_t()

    native Long new_vx_resp_aux_get_speaker_level_t()

    native Long new_vx_resp_aux_get_vad_properties_t()

    native Long new_vx_resp_aux_global_monitor_keyboard_mouse_t()

    native Long new_vx_resp_aux_notify_application_state_change_t()

    native Long new_vx_resp_aux_play_audio_buffer_t()

    native Long new_vx_resp_aux_reactivate_account_t()

    native Long new_vx_resp_aux_render_audio_modify_t()

    native Long new_vx_resp_aux_render_audio_start_t()

    native Long new_vx_resp_aux_render_audio_stop_t()

    native Long new_vx_resp_aux_reset_password_t()

    native Long new_vx_resp_aux_set_capture_device_t()

    native Long new_vx_resp_aux_set_idle_timeout_t()

    native Long new_vx_resp_aux_set_mic_level_t()

    native Long new_vx_resp_aux_set_render_device_t()

    native Long new_vx_resp_aux_set_speaker_level_t()

    native Long new_vx_resp_aux_set_vad_properties_t()

    native Long new_vx_resp_aux_start_buffer_capture_t()

    native Long new_vx_resp_base_t()

    native Long new_vx_resp_channel_ban_user_t()

    native Long new_vx_resp_channel_get_banned_users_t()

    native Long new_vx_resp_channel_kick_user_t()

    native Long new_vx_resp_channel_mute_all_users_t()

    native Long new_vx_resp_channel_mute_user_t()

    native Long new_vx_resp_channel_set_lock_mode_t()

    native Long new_vx_resp_connector_create_t()

    native Long new_vx_resp_connector_get_local_audio_info_t()

    native Long new_vx_resp_connector_initiate_shutdown_t()

    native Long new_vx_resp_connector_mute_local_mic_t()

    native Long new_vx_resp_connector_mute_local_speaker_t()

    native Long new_vx_resp_connector_set_local_mic_volume_t()

    native Long new_vx_resp_connector_set_local_speaker_volume_t()

    native Long new_vx_resp_session_channel_invite_user_t()

    native Long new_vx_resp_session_create_t()

    native Long new_vx_resp_session_media_connect_t()

    native Long new_vx_resp_session_media_disconnect_t()

    native Long new_vx_resp_session_mute_local_speaker_t()

    native Long new_vx_resp_session_send_dtmf_t()

    native Long new_vx_resp_session_send_message_t()

    native Long new_vx_resp_session_send_notification_t()

    native Long new_vx_resp_session_set_3d_position_t()

    native Long new_vx_resp_session_set_local_speaker_volume_t()

    native Long new_vx_resp_session_set_participant_mute_for_me_t()

    native Long new_vx_resp_session_set_participant_volume_for_me_t()

    native Long new_vx_resp_session_set_voice_font_t()

    native Long new_vx_resp_session_terminate_t()

    native Long new_vx_resp_session_text_connect_t()

    native Long new_vx_resp_session_text_disconnect_t()

    native Long new_vx_resp_sessiongroup_add_session_t()

    native Long new_vx_resp_sessiongroup_control_audio_injection_t()

    native Long new_vx_resp_sessiongroup_control_playback_t()

    native Long new_vx_resp_sessiongroup_control_recording_t()

    native Long new_vx_resp_sessiongroup_create_t()

    native Long new_vx_resp_sessiongroup_get_stats_t()

    native Long new_vx_resp_sessiongroup_remove_session_t()

    native Long new_vx_resp_sessiongroup_reset_focus_t()

    native Long new_vx_resp_sessiongroup_set_focus_t()

    native Long new_vx_resp_sessiongroup_set_playback_options_t()

    native Long new_vx_resp_sessiongroup_set_session_3d_position_t()

    native Long new_vx_resp_sessiongroup_set_tx_all_sessions_t()

    native Long new_vx_resp_sessiongroup_set_tx_no_session_t()

    native Long new_vx_resp_sessiongroup_set_tx_session_t()

    native Long new_vx_resp_sessiongroup_terminate_t()

    native Long new_vx_resp_sessiongroup_unset_focus_t()

    native Long new_vx_sdk_config_t()

    native Long new_vx_stat_sample_t()

    native Long new_vx_stat_thread_t()

    native Long new_vx_state_account_t()

    native Long new_vx_state_buddy_contact_t()

    native Long new_vx_state_buddy_group_t()

    native Long new_vx_state_buddy_t()

    native Long new_vx_state_connector_t()

    native Long new_vx_state_participant_t()

    native Long new_vx_state_session_t()

    native Long new_vx_state_sessiongroup_t()

    native Long new_vx_system_stats_t()

    native Long new_vx_user_channel_t()

    native Long new_vx_voice_font_t()

    native Int notification_hand_lowered_get()

    native Int notification_hand_raised_get()

    native Int notification_max_get()

    native Int notification_min_get()

    native Int notification_not_typing_get()

    native Int notification_typing_get()

    native Int op_none_get()

    native Int op_safeupdate_get()

    native Int orientation_default_get()

    native Int orientation_legacy_get()

    native Int orientation_vivox_get()

    native Int part_focus_get()

    native Int part_moderator_get()

    native Int part_user_get()

    native Int participant_banned_get()

    native Int participant_diagnostic_state_speaking_while_mic_muted_get()

    native Int participant_diagnostic_state_speaking_while_mic_volume_zero_get()

    native Int participant_kicked_get()

    native Int participant_left_get()

    native Int participant_moderator_get()

    native Int participant_owner_get()

    native Int participant_timeout_get()

    native Int participant_user_get()

    native Int register_logging_handler(String var0, String var1, Int var2)

    native Int register_message_notification_handler(String var0, String var1)

    native Int req_account_anonymous_login_get()

    native Int req_account_authtoken_login_get()

    native Int req_account_buddy_delete_get()

    native Int req_account_buddy_search_get()

    native Int req_account_buddy_set_get()

    native Int req_account_buddygroup_delete_get()

    native Int req_account_buddygroup_set_get()

    native Int req_account_channel_add_acl_get()

    native Int req_account_channel_add_moderator_get()

    native Int req_account_channel_change_owner_get()

    native Int req_account_channel_create_get()

    native Int req_account_channel_delete_get()

    native Int req_account_channel_favorite_delete_get()

    native Int req_account_channel_favorite_group_delete_get()

    native Int req_account_channel_favorite_group_set_get()

    native Int req_account_channel_favorite_set_get()

    native Int req_account_channel_favorites_get_list_get()

    native Int req_account_channel_get_acl_get()

    native Int req_account_channel_get_info_get()

    native Int req_account_channel_get_moderators_get()

    native Int req_account_channel_get_participants_get()

    native Int req_account_channel_remove_acl_get()

    native Int req_account_channel_remove_moderator_get()

    native Int req_account_channel_search_get()

    native Int req_account_channel_update_get()

    native Int req_account_create_auto_accept_rule_get()

    native Int req_account_create_block_rule_get()

    native Int req_account_delete_auto_accept_rule_get()

    native Int req_account_delete_block_rule_get()

    native Int req_account_get_account_get()

    native Int req_account_get_session_fonts_get()

    native Int req_account_get_template_fonts_get()

    native Int req_account_list_auto_accept_rules_get()

    native Int req_account_list_block_rules_get()

    native Int req_account_list_buddies_and_groups_get()

    native Int req_account_login_get()

    native Int req_account_logout_get()

    native Int req_account_post_crash_dump_get()

    native Int req_account_send_message_get()

    native Int req_account_send_sms_get()

    native Int req_account_send_subscription_reply_get()

    native Int req_account_send_user_app_data_get()

    native Int req_account_set_login_properties_get()

    native Int req_account_set_presence_get()

    native Int req_account_update_account_get()

    native Int req_account_web_call_get()

    native Int req_aux_capture_audio_start_get()

    native Int req_aux_capture_audio_stop_get()

    native Int req_aux_connectivity_info_get()

    native Int req_aux_create_account_get()

    native Int req_aux_deactivate_account_get()

    native Int req_aux_diagnostic_state_dump_get()

    native Int req_aux_get_capture_devices_get()

    native Int req_aux_get_mic_level_get()

    native Int req_aux_get_render_devices_get()

    native Int req_aux_get_speaker_level_get()

    native Int req_aux_get_vad_properties_get()

    native Int req_aux_global_monitor_keyboard_mouse_get()

    native Int req_aux_notify_application_state_change_get()

    native Int req_aux_play_audio_buffer_get()

    native Int req_aux_reactivate_account_get()

    native Int req_aux_render_audio_modify_get()

    native Int req_aux_render_audio_start_get()

    native Int req_aux_render_audio_stop_get()

    native Int req_aux_reset_password_get()

    native Int req_aux_set_capture_device_get()

    native Int req_aux_set_idle_timeout_get()

    native Int req_aux_set_mic_level_get()

    native Int req_aux_set_render_device_get()

    native Int req_aux_set_speaker_level_get()

    native Int req_aux_set_vad_properties_get()

    native Int req_aux_start_buffer_capture_get()

    native Int req_channel_ban_user_get()

    native Int req_channel_get_banned_users_get()

    native Int req_channel_kick_user_get()

    native Int req_channel_mute_all_users_get()

    native Int req_channel_mute_user_get()

    native Int req_channel_set_lock_mode_get()

    native Int req_connector_create_get()

    native Int req_connector_get_local_audio_info_get()

    native Int req_connector_initiate_shutdown_get()

    native Int req_connector_mute_local_mic_get()

    native Int req_connector_mute_local_speaker_get()

    native Int req_connector_set_local_mic_volume_get()

    native Int req_connector_set_local_speaker_volume_get()

    native Int req_max_get()

    native Int req_none_get()

    native Int req_session_channel_invite_user_get()

    native Int req_session_create_get()

    native Int req_session_media_connect_get()

    native Int req_session_media_disconnect_get()

    native Int req_session_mute_local_speaker_get()

    native Int req_session_send_dtmf_get()

    native Int req_session_send_message_get()

    native Int req_session_send_notification_get()

    native Int req_session_set_3d_position_get()

    native Int req_session_set_local_speaker_volume_get()

    native Int req_session_set_participant_mute_for_me_get()

    native Int req_session_set_participant_volume_for_me_get()

    native Int req_session_set_voice_font_get()

    native Int req_session_terminate_get()

    native Int req_session_text_connect_get()

    native Int req_session_text_disconnect_get()

    native Int req_sessiongroup_add_session_get()

    native Int req_sessiongroup_control_audio_injection_get()

    native Int req_sessiongroup_control_playback_get()

    native Int req_sessiongroup_control_recording_get()

    native Int req_sessiongroup_create_get()

    native Int req_sessiongroup_get_stats_get()

    native Int req_sessiongroup_remove_session_get()

    native Int req_sessiongroup_reset_focus_get()

    native Int req_sessiongroup_set_focus_get()

    native Int req_sessiongroup_set_playback_options_get()

    native Int req_sessiongroup_set_session_3d_position_get()

    native Int req_sessiongroup_set_tx_all_sessions_get()

    native Int req_sessiongroup_set_tx_no_session_get()

    native Int req_sessiongroup_set_tx_session_get()

    native Int req_sessiongroup_terminate_get()

    native Int req_sessiongroup_unset_focus_get()

    native Long request_to_xml(Long var0, vx_req_base_t var2)

    native Int resp_account_anonymous_login_get()

    native Int resp_account_authtoken_login_get()

    native Int resp_account_buddy_delete_get()

    native Int resp_account_buddy_search_get()

    native Int resp_account_buddy_set_get()

    native Int resp_account_buddygroup_delete_get()

    native Int resp_account_buddygroup_set_get()

    native Int resp_account_channel_add_acl_get()

    native Int resp_account_channel_add_moderator_get()

    native Int resp_account_channel_change_owner_get()

    native Int resp_account_channel_create_get()

    native Int resp_account_channel_delete_get()

    native Int resp_account_channel_favorite_delete_get()

    native Int resp_account_channel_favorite_group_delete_get()

    native Int resp_account_channel_favorite_group_set_get()

    native Int resp_account_channel_favorite_set_get()

    native Int resp_account_channel_favorites_get_list_get()

    native Int resp_account_channel_get_acl_get()

    native Int resp_account_channel_get_info_get()

    native Int resp_account_channel_get_list_get()

    native Int resp_account_channel_get_moderators_get()

    native Int resp_account_channel_get_participants_get()

    native Int resp_account_channel_remove_acl_get()

    native Int resp_account_channel_remove_moderator_get()

    native Int resp_account_channel_search_get()

    native Int resp_account_channel_update_get()

    native Int resp_account_create_auto_accept_rule_get()

    native Int resp_account_create_block_rule_get()

    native Int resp_account_delete_auto_accept_rule_get()

    native Int resp_account_delete_block_rule_get()

    native Int resp_account_get_account_get()

    native Int resp_account_get_session_fonts_get()

    native Int resp_account_get_template_fonts_get()

    native Int resp_account_list_auto_accept_rules_get()

    native Int resp_account_list_block_rules_get()

    native Int resp_account_list_buddies_and_groups_get()

    native Int resp_account_login_get()

    native Int resp_account_logout_get()

    native Int resp_account_post_crash_dump_get()

    native Int resp_account_send_message_get()

    native Int resp_account_send_sms_get()

    native Int resp_account_send_subscription_reply_get()

    native Int resp_account_send_user_app_data_get()

    native Int resp_account_set_login_properties_get()

    native Int resp_account_set_presence_get()

    native Int resp_account_update_account_get()

    native Int resp_account_web_call_get()

    native Int resp_aux_capture_audio_start_get()

    native Int resp_aux_capture_audio_stop_get()

    native Int resp_aux_connectivity_info_get()

    native Int resp_aux_create_account_get()

    native Int resp_aux_deactivate_account_get()

    native Int resp_aux_diagnostic_state_dump_get()

    native Int resp_aux_get_capture_devices_get()

    native Int resp_aux_get_mic_level_get()

    native Int resp_aux_get_render_devices_get()

    native Int resp_aux_get_speaker_level_get()

    native Int resp_aux_get_vad_properties_get()

    native Int resp_aux_global_monitor_keyboard_mouse_get()

    native Int resp_aux_notify_application_state_change_get()

    native Int resp_aux_play_audio_buffer_get()

    native Int resp_aux_reactivate_account_get()

    native Int resp_aux_render_audio_modify_get()

    native Int resp_aux_render_audio_start_get()

    native Int resp_aux_render_audio_stop_get()

    native Int resp_aux_reset_password_get()

    native Int resp_aux_set_capture_device_get()

    native Int resp_aux_set_idle_timeout_get()

    native Int resp_aux_set_mic_level_get()

    native Int resp_aux_set_render_device_get()

    native Int resp_aux_set_speaker_level_get()

    native Int resp_aux_set_vad_properties_get()

    native Int resp_aux_start_buffer_capture_get()

    native Int resp_channel_ban_user_get()

    native Int resp_channel_get_banned_users_get()

    native Int resp_channel_kick_user_get()

    native Int resp_channel_mute_all_users_get()

    native Int resp_channel_mute_user_get()

    native Int resp_channel_set_lock_mode_get()

    native Int resp_connector_create_get()

    native Int resp_connector_get_local_audio_info_get()

    native Int resp_connector_initiate_shutdown_get()

    native Int resp_connector_mute_local_mic_get()

    native Int resp_connector_mute_local_speaker_get()

    native Int resp_connector_set_local_mic_volume_get()

    native Int resp_connector_set_local_speaker_volume_get()

    native Int resp_max_get()

    native Int resp_none_get()

    native Int resp_session_channel_invite_user_get()

    native Int resp_session_create_get()

    native Int resp_session_media_connect_get()

    native Int resp_session_media_disconnect_get()

    native Int resp_session_mute_local_speaker_get()

    native Int resp_session_send_dtmf_get()

    native Int resp_session_send_message_get()

    native Int resp_session_send_notification_get()

    native Int resp_session_set_3d_position_get()

    native Int resp_session_set_local_speaker_volume_get()

    native Int resp_session_set_participant_mute_for_me_get()

    native Int resp_session_set_participant_volume_for_me_get()

    native Int resp_session_set_voice_font_get()

    native Int resp_session_terminate_get()

    native Int resp_session_text_connect_get()

    native Int resp_session_text_disconnect_get()

    native Int resp_sessiongroup_add_session_get()

    native Int resp_sessiongroup_control_audio_injection_get()

    native Int resp_sessiongroup_control_playback_get()

    native Int resp_sessiongroup_control_recording_get()

    native Int resp_sessiongroup_create_get()

    native Int resp_sessiongroup_get_stats_get()

    native Int resp_sessiongroup_remove_session_get()

    native Int resp_sessiongroup_reset_focus_get()

    native Int resp_sessiongroup_set_focus_get()

    native Int resp_sessiongroup_set_playback_options_get()

    native Int resp_sessiongroup_set_session_3d_position_get()

    native Int resp_sessiongroup_set_tx_all_sessions_get()

    native Int resp_sessiongroup_set_tx_no_session_get()

    native Int resp_sessiongroup_set_tx_session_get()

    native Int resp_sessiongroup_terminate_get()

    native Int resp_sessiongroup_unset_focus_get()

    native Int rule_none_get()

    native String sdk_string_to_string(Long var0)

    native Int session_handle_type_unique_get()

    native Int session_media_none_get()

    native Int session_notification_none_get()

    native Int session_text_disconnected_get()

    native Int sessiongroup_audio_injection_get()

    native Int sessiongroup_type_normal_get()

    native Int sessiongroup_type_playback_get()

    native Unit set_request_cookie(Long var0, vx_req_base_t var2, String var3)

    native Int status_free_get()

    native Int status_none_get()

    native Int status_not_free_get()

    native Int subscription_presence_get()

    native Int termination_status_none_get()

    native Int text_mode_disabled_get()

    native Int type_none_get()

    native Int type_root_get()

    native Int type_user_get()

    native Unit vx_account_create(Long var0)

    native Unit vx_account_free(Long var0, vx_account_t var2)

    native String vx_account_t_carrier_get(Long var0, vx_account_t var2)

    native Unit vx_account_t_carrier_set(Long var0, vx_account_t var2, String var3)

    native String vx_account_t_created_date_get(Long var0, vx_account_t var2)

    native Unit vx_account_t_created_date_set(Long var0, vx_account_t var2, String var3)

    native String vx_account_t_displayname_get(Long var0, vx_account_t var2)

    native Unit vx_account_t_displayname_set(Long var0, vx_account_t var2, String var3)

    native String vx_account_t_email_get(Long var0, vx_account_t var2)

    native Unit vx_account_t_email_set(Long var0, vx_account_t var2, String var3)

    native String vx_account_t_firstname_get(Long var0, vx_account_t var2)

    native Unit vx_account_t_firstname_set(Long var0, vx_account_t var2, String var3)

    native String vx_account_t_lastname_get(Long var0, vx_account_t var2)

    native Unit vx_account_t_lastname_set(Long var0, vx_account_t var2, String var3)

    native String vx_account_t_phone_get(Long var0, vx_account_t var2)

    native Unit vx_account_t_phone_set(Long var0, vx_account_t var2, String var3)

    native String vx_account_t_uri_get(Long var0, vx_account_t var2)

    native Unit vx_account_t_uri_set(Long var0, vx_account_t var2, String var3)

    native String vx_account_t_username_get(Long var0, vx_account_t var2)

    native Unit vx_account_t_username_set(Long var0, vx_account_t var2, String var3)

    native Int vx_alloc_sdk_handle(String var0, Int var1, Long var2)

    native Int vx_apply_font_to_file(String var0, String var1, String var2)

    native Int vx_apply_font_to_file_return_energy_ratio(String var0, String var1, String var2, Long var3)

    native Int vx_apply_font_to_vxz_file_return_energy_ratio(String var0, String var1, String var2, Long var3)

    native Unit vx_auto_accept_rule_create(Long var0)

    native Unit vx_auto_accept_rule_free(Long var0, vx_auto_accept_rule_t var2)

    native String vx_auto_accept_rule_t_auto_accept_mask_get(Long var0, vx_auto_accept_rule_t var2)

    native Unit vx_auto_accept_rule_t_auto_accept_mask_set(Long var0, vx_auto_accept_rule_t var2, String var3)

    native String vx_auto_accept_rule_t_auto_accept_nickname_get(Long var0, vx_auto_accept_rule_t var2)

    native Unit vx_auto_accept_rule_t_auto_accept_nickname_set(Long var0, vx_auto_accept_rule_t var2, String var3)

    native Int vx_auto_accept_rule_t_auto_add_as_buddy_get(Long var0, vx_auto_accept_rule_t var2)

    native Unit vx_auto_accept_rule_t_auto_add_as_buddy_set(Long var0, vx_auto_accept_rule_t var2, Int var3)

    native Unit vx_auto_accept_rules_create(Int var0, Long var1)

    native Unit vx_auto_accept_rules_free(Long var0, Int var2)

    native Unit vx_block_rule_create(Long var0)

    native Unit vx_block_rule_free(Long var0, vx_block_rule_t var2)

    native String vx_block_rule_t_block_mask_get(Long var0, vx_block_rule_t var2)

    native Unit vx_block_rule_t_block_mask_set(Long var0, vx_block_rule_t var2, String var3)

    native Int vx_block_rule_t_presence_only_get(Long var0, vx_block_rule_t var2)

    native Unit vx_block_rule_t_presence_only_set(Long var0, vx_block_rule_t var2, Int var3)

    native Unit vx_block_rules_create(Int var0, Long var1)

    native Unit vx_block_rules_free(Long var0, Int var2)

    native Unit vx_buddy_create(Long var0)

    native Unit vx_buddy_free(Long var0, vx_buddy_t var2)

    native Unit vx_buddy_list_create(Int var0, Long var1)

    native Unit vx_buddy_list_free(Long var0, Int var2)

    native Int vx_buddy_t_account_id_get(Long var0, vx_buddy_t var2)

    native Unit vx_buddy_t_account_id_set(Long var0, vx_buddy_t var2, Int var3)

    native String vx_buddy_t_account_name_get(Long var0, vx_buddy_t var2)

    native Unit vx_buddy_t_account_name_set(Long var0, vx_buddy_t var2, String var3)

    native String vx_buddy_t_buddy_data_get(Long var0, vx_buddy_t var2)

    native Unit vx_buddy_t_buddy_data_set(Long var0, vx_buddy_t var2, String var3)

    native String vx_buddy_t_buddy_uri_get(Long var0, vx_buddy_t var2)

    native Unit vx_buddy_t_buddy_uri_set(Long var0, vx_buddy_t var2, String var3)

    native String vx_buddy_t_display_name_get(Long var0, vx_buddy_t var2)

    native Unit vx_buddy_t_display_name_set(Long var0, vx_buddy_t var2, String var3)

    native Int vx_buddy_t_parent_group_id_get(Long var0, vx_buddy_t var2)

    native Unit vx_buddy_t_parent_group_id_set(Long var0, vx_buddy_t var2, Int var3)

    native Unit vx_channel_create(Long var0)

    native Unit vx_channel_favorite_create(Long var0)

    native Unit vx_channel_favorite_free(Long var0, vx_channel_favorite_t var2)

    native Unit vx_channel_favorite_group_create(Long var0)

    native Unit vx_channel_favorite_group_free(Long var0, vx_channel_favorite_group_t var2)

    native Unit vx_channel_favorite_group_list_create(Int var0, Long var1)

    native Unit vx_channel_favorite_group_list_free(Long var0, Int var2)

    native String vx_channel_favorite_group_t_favorite_group_data_get(Long var0, vx_channel_favorite_group_t var2)

    native Unit vx_channel_favorite_group_t_favorite_group_data_set(Long var0, vx_channel_favorite_group_t var2, String var3)

    native Int vx_channel_favorite_group_t_favorite_group_id_get(Long var0, vx_channel_favorite_group_t var2)

    native Unit vx_channel_favorite_group_t_favorite_group_id_set(Long var0, vx_channel_favorite_group_t var2, Int var3)

    native String vx_channel_favorite_group_t_favorite_group_modified_get(Long var0, vx_channel_favorite_group_t var2)

    native Unit vx_channel_favorite_group_t_favorite_group_modified_set(Long var0, vx_channel_favorite_group_t var2, String var3)

    native String vx_channel_favorite_group_t_favorite_group_name_get(Long var0, vx_channel_favorite_group_t var2)

    native Unit vx_channel_favorite_group_t_favorite_group_name_set(Long var0, vx_channel_favorite_group_t var2, String var3)

    native Unit vx_channel_favorite_list_create(Int var0, Long var1)

    native Unit vx_channel_favorite_list_free(Long var0, Int var2)

    native Int vx_channel_favorite_t_channel_active_participants_get(Long var0, vx_channel_favorite_t var2)

    native Unit vx_channel_favorite_t_channel_active_participants_set(Long var0, vx_channel_favorite_t var2, Int var3)

    native Int vx_channel_favorite_t_channel_capacity_get(Long var0, vx_channel_favorite_t var2)

    native Unit vx_channel_favorite_t_channel_capacity_set(Long var0, vx_channel_favorite_t var2, Int var3)

    native String vx_channel_favorite_t_channel_description_get(Long var0, vx_channel_favorite_t var2)

    native Unit vx_channel_favorite_t_channel_description_set(Long var0, vx_channel_favorite_t var2, String var3)

    native Int vx_channel_favorite_t_channel_is_persistent_get(Long var0, vx_channel_favorite_t var2)

    native Unit vx_channel_favorite_t_channel_is_persistent_set(Long var0, vx_channel_favorite_t var2, Int var3)

    native Int vx_channel_favorite_t_channel_is_protected_get(Long var0, vx_channel_favorite_t var2)

    native Unit vx_channel_favorite_t_channel_is_protected_set(Long var0, vx_channel_favorite_t var2, Int var3)

    native Int vx_channel_favorite_t_channel_limit_get(Long var0, vx_channel_favorite_t var2)

    native Unit vx_channel_favorite_t_channel_limit_set(Long var0, vx_channel_favorite_t var2, Int var3)

    native String vx_channel_favorite_t_channel_modified_get(Long var0, vx_channel_favorite_t var2)

    native Unit vx_channel_favorite_t_channel_modified_set(Long var0, vx_channel_favorite_t var2, String var3)

    native String vx_channel_favorite_t_channel_owner_display_name_get(Long var0, vx_channel_favorite_t var2)

    native Unit vx_channel_favorite_t_channel_owner_display_name_set(Long var0, vx_channel_favorite_t var2, String var3)

    native String vx_channel_favorite_t_channel_owner_get(Long var0, vx_channel_favorite_t var2)

    native Unit vx_channel_favorite_t_channel_owner_set(Long var0, vx_channel_favorite_t var2, String var3)

    native String vx_channel_favorite_t_channel_owner_user_name_get(Long var0, vx_channel_favorite_t var2)

    native Unit vx_channel_favorite_t_channel_owner_user_name_set(Long var0, vx_channel_favorite_t var2, String var3)

    native Int vx_channel_favorite_t_channel_size_get(Long var0, vx_channel_favorite_t var2)

    native Unit vx_channel_favorite_t_channel_size_set(Long var0, vx_channel_favorite_t var2, Int var3)

    native String vx_channel_favorite_t_channel_uri_get(Long var0, vx_channel_favorite_t var2)

    native Unit vx_channel_favorite_t_channel_uri_set(Long var0, vx_channel_favorite_t var2, String var3)

    native String vx_channel_favorite_t_favorite_data_get(Long var0, vx_channel_favorite_t var2)

    native Unit vx_channel_favorite_t_favorite_data_set(Long var0, vx_channel_favorite_t var2, String var3)

    native String vx_channel_favorite_t_favorite_display_name_get(Long var0, vx_channel_favorite_t var2)

    native Unit vx_channel_favorite_t_favorite_display_name_set(Long var0, vx_channel_favorite_t var2, String var3)

    native Int vx_channel_favorite_t_favorite_group_id_get(Long var0, vx_channel_favorite_t var2)

    native Unit vx_channel_favorite_t_favorite_group_id_set(Long var0, vx_channel_favorite_t var2, Int var3)

    native Int vx_channel_favorite_t_favorite_id_get(Long var0, vx_channel_favorite_t var2)

    native Unit vx_channel_favorite_t_favorite_id_set(Long var0, vx_channel_favorite_t var2, Int var3)

    native Unit vx_channel_free(Long var0, vx_channel_t var2)

    native Unit vx_channel_list_create(Int var0, Long var1)

    native Unit vx_channel_list_free(Long var0, Int var2)

    native Int vx_channel_t_active_participants_get(Long var0, vx_channel_t var2)

    native Unit vx_channel_t_active_participants_set(Long var0, vx_channel_t var2, Int var3)

    native Int vx_channel_t_capacity_get(Long var0, vx_channel_t var2)

    native Unit vx_channel_t_capacity_set(Long var0, vx_channel_t var2, Int var3)

    native String vx_channel_t_channel_desc_get(Long var0, vx_channel_t var2)

    native Unit vx_channel_t_channel_desc_set(Long var0, vx_channel_t var2, String var3)

    native Int vx_channel_t_channel_id_get(Long var0, vx_channel_t var2)

    native Unit vx_channel_t_channel_id_set(Long var0, vx_channel_t var2, Int var3)

    native String vx_channel_t_channel_name_get(Long var0, vx_channel_t var2)

    native Unit vx_channel_t_channel_name_set(Long var0, vx_channel_t var2, String var3)

    native String vx_channel_t_channel_uri_get(Long var0, vx_channel_t var2)

    native Unit vx_channel_t_channel_uri_set(Long var0, vx_channel_t var2, String var3)

    native Int vx_channel_t_clamping_dist_get(Long var0, vx_channel_t var2)

    native Unit vx_channel_t_clamping_dist_set(Long var0, vx_channel_t var2, Int var3)

    native Int vx_channel_t_dist_model_get(Long var0, vx_channel_t var2)

    native Unit vx_channel_t_dist_model_set(Long var0, vx_channel_t var2, Int var3)

    native Int vx_channel_t_encrypt_audio_get(Long var0, vx_channel_t var2)

    native Unit vx_channel_t_encrypt_audio_set(Long var0, vx_channel_t var2, Int var3)

    native String vx_channel_t_host_get(Long var0, vx_channel_t var2)

    native Unit vx_channel_t_host_set(Long var0, vx_channel_t var2, String var3)

    native Int vx_channel_t_is_persistent_get(Long var0, vx_channel_t var2)

    native Unit vx_channel_t_is_persistent_set(Long var0, vx_channel_t var2, Int var3)

    native Int vx_channel_t_is_protected_get(Long var0, vx_channel_t var2)

    native Unit vx_channel_t_is_protected_set(Long var0, vx_channel_t var2, Int var3)

    native Int vx_channel_t_limit_get(Long var0, vx_channel_t var2)

    native Unit vx_channel_t_limit_set(Long var0, vx_channel_t var2, Int var3)

    native Double vx_channel_t_max_gain_get(Long var0, vx_channel_t var2)

    native Unit vx_channel_t_max_gain_set(Long var0, vx_channel_t var2, Double var3)

    native Int vx_channel_t_max_range_get(Long var0, vx_channel_t var2)

    native Unit vx_channel_t_max_range_set(Long var0, vx_channel_t var2, Int var3)

    native Int vx_channel_t_mode_get(Long var0, vx_channel_t var2)

    native Unit vx_channel_t_mode_set(Long var0, vx_channel_t var2, Int var3)

    native String vx_channel_t_modified_get(Long var0, vx_channel_t var2)

    native Unit vx_channel_t_modified_set(Long var0, vx_channel_t var2, String var3)

    native String vx_channel_t_owner_display_name_get(Long var0, vx_channel_t var2)

    native Unit vx_channel_t_owner_display_name_set(Long var0, vx_channel_t var2, String var3)

    native String vx_channel_t_owner_get(Long var0, vx_channel_t var2)

    native Unit vx_channel_t_owner_set(Long var0, vx_channel_t var2, String var3)

    native String vx_channel_t_owner_user_name_get(Long var0, vx_channel_t var2)

    native Unit vx_channel_t_owner_user_name_set(Long var0, vx_channel_t var2, String var3)

    native Double vx_channel_t_roll_off_get(Long var0, vx_channel_t var2)

    native Unit vx_channel_t_roll_off_set(Long var0, vx_channel_t var2, Double var3)

    native Int vx_channel_t_size_get(Long var0, vx_channel_t var2)

    native Unit vx_channel_t_size_set(Long var0, vx_channel_t var2, Int var3)

    native Int vx_channel_t_type_get(Long var0, vx_channel_t var2)

    native Unit vx_channel_t_type_set(Long var0, vx_channel_t var2, Int var3)

    native Unit vx_connectivity_test_result_create(Long var0, Int var2)

    native Unit vx_connectivity_test_result_free(Long var0, vx_connectivity_test_result_t var2)

    native String vx_connectivity_test_result_t_test_additional_info_get(Long var0, vx_connectivity_test_result_t var2)

    native Unit vx_connectivity_test_result_t_test_additional_info_set(Long var0, vx_connectivity_test_result_t var2, String var3)

    native Int vx_connectivity_test_result_t_test_error_code_get(Long var0, vx_connectivity_test_result_t var2)

    native Unit vx_connectivity_test_result_t_test_error_code_set(Long var0, vx_connectivity_test_result_t var2, Int var3)

    native Int vx_connectivity_test_result_t_test_type_get(Long var0, vx_connectivity_test_result_t var2)

    native Unit vx_connectivity_test_result_t_test_type_set(Long var0, vx_connectivity_test_result_t var2, Int var3)

    native Unit vx_connectivity_test_results_create(Int var0, Long var1)

    native Unit vx_connectivity_test_results_free(Long var0, Int var2)

    native Unit vx_cookie_create(String var0, Long var1)

    native Unit vx_cookie_free(Long var0)

    native Long vx_copy_audioBuffer(Long var0)

    native Int vx_create_account(String var0, String var1, String var2, String var3, String var4)

    native Int vx_delete_crash_dump(Int var0)

    native Unit vx_device_create(Long var0)

    native Unit vx_device_free(Long var0, vx_device_t var2)

    native String vx_device_t_device_get(Long var0, vx_device_t var2)

    native Unit vx_device_t_device_set(Long var0, vx_device_t var2, String var3)

    native Int vx_device_t_device_type_get(Long var0, vx_device_t var2)

    native Unit vx_device_t_device_type_set(Long var0, vx_device_t var2, Int var3)

    native String vx_device_t_display_name_get(Long var0, vx_device_t var2)

    native Unit vx_device_t_display_name_set(Long var0, vx_device_t var2, String var3)

    native Int vx_device_type_default_system_get()

    native Int vx_device_type_null_get()

    native Int vx_device_type_specific_device_get()

    native Unit vx_devices_create(Int var0, Long var1)

    native Unit vx_devices_free(Long var0, Int var2)

    native Unit vx_event_to_xml(Long var0, Long var2)

    native String vx_evt_account_login_state_change_t_account_handle_get(Long var0, vx_evt_account_login_state_change_t var2)

    native Unit vx_evt_account_login_state_change_t_account_handle_set(Long var0, vx_evt_account_login_state_change_t var2, String var3)

    native Long vx_evt_account_login_state_change_t_base_get(Long var0, vx_evt_account_login_state_change_t var2)

    native Unit vx_evt_account_login_state_change_t_base_set(Long var0, vx_evt_account_login_state_change_t var2, Long var3, vx_evt_base_t var5)

    native String vx_evt_account_login_state_change_t_cookie_get(Long var0, vx_evt_account_login_state_change_t var2)

    native Unit vx_evt_account_login_state_change_t_cookie_set(Long var0, vx_evt_account_login_state_change_t var2, String var3)

    native Int vx_evt_account_login_state_change_t_state_get(Long var0, vx_evt_account_login_state_change_t var2)

    native Unit vx_evt_account_login_state_change_t_state_set(Long var0, vx_evt_account_login_state_change_t var2, Int var3)

    native Int vx_evt_account_login_state_change_t_status_code_get(Long var0, vx_evt_account_login_state_change_t var2)

    native Unit vx_evt_account_login_state_change_t_status_code_set(Long var0, vx_evt_account_login_state_change_t var2, Int var3)

    native String vx_evt_account_login_state_change_t_status_string_get(Long var0, vx_evt_account_login_state_change_t var2)

    native Unit vx_evt_account_login_state_change_t_status_string_set(Long var0, vx_evt_account_login_state_change_t var2, String var3)

    native Long vx_evt_account_login_state_change_t_vcookie_get(Long var0, vx_evt_account_login_state_change_t var2)

    native Unit vx_evt_account_login_state_change_t_vcookie_set(Long var0, vx_evt_account_login_state_change_t var2, Long var3)

    native Long vx_evt_aux_audio_properties_t_base_get(Long var0, vx_evt_aux_audio_properties_t var2)

    native Unit vx_evt_aux_audio_properties_t_base_set(Long var0, vx_evt_aux_audio_properties_t var2, Long var3, vx_evt_base_t var5)

    native Double vx_evt_aux_audio_properties_t_mic_energy_get(Long var0, vx_evt_aux_audio_properties_t var2)

    native Unit vx_evt_aux_audio_properties_t_mic_energy_set(Long var0, vx_evt_aux_audio_properties_t var2, Double var3)

    native Int vx_evt_aux_audio_properties_t_mic_is_active_get(Long var0, vx_evt_aux_audio_properties_t var2)

    native Unit vx_evt_aux_audio_properties_t_mic_is_active_set(Long var0, vx_evt_aux_audio_properties_t var2, Int var3)

    native Int vx_evt_aux_audio_properties_t_mic_volume_get(Long var0, vx_evt_aux_audio_properties_t var2)

    native Unit vx_evt_aux_audio_properties_t_mic_volume_set(Long var0, vx_evt_aux_audio_properties_t var2, Int var3)

    native Double vx_evt_aux_audio_properties_t_speaker_energy_get(Long var0, vx_evt_aux_audio_properties_t var2)

    native Unit vx_evt_aux_audio_properties_t_speaker_energy_set(Long var0, vx_evt_aux_audio_properties_t var2, Double var3)

    native Int vx_evt_aux_audio_properties_t_speaker_is_active_get(Long var0, vx_evt_aux_audio_properties_t var2)

    native Unit vx_evt_aux_audio_properties_t_speaker_is_active_set(Long var0, vx_evt_aux_audio_properties_t var2, Int var3)

    native Int vx_evt_aux_audio_properties_t_speaker_volume_get(Long var0, vx_evt_aux_audio_properties_t var2)

    native Unit vx_evt_aux_audio_properties_t_speaker_volume_set(Long var0, vx_evt_aux_audio_properties_t var2, Int var3)

    native Int vx_evt_aux_audio_properties_t_state_get(Long var0, vx_evt_aux_audio_properties_t var2)

    native Unit vx_evt_aux_audio_properties_t_state_set(Long var0, vx_evt_aux_audio_properties_t var2, Int var3)

    native String vx_evt_base_t_extended_status_info_get(Long var0, vx_evt_base_t var2)

    native Unit vx_evt_base_t_extended_status_info_set(Long var0, vx_evt_base_t var2, String var3)

    native Long vx_evt_base_t_message_get(Long var0, vx_evt_base_t var2)

    native Unit vx_evt_base_t_message_set(Long var0, vx_evt_base_t var2, Long var3, vx_message_base_t var5)

    native Int vx_evt_base_t_type_get(Long var0, vx_evt_base_t var2)

    native Unit vx_evt_base_t_type_set(Long var0, vx_evt_base_t var2, Int var3)

    native String vx_evt_buddy_and_group_list_changed_t_account_handle_get(Long var0, vx_evt_buddy_and_group_list_changed_t var2)

    native Unit vx_evt_buddy_and_group_list_changed_t_account_handle_set(Long var0, vx_evt_buddy_and_group_list_changed_t var2, String var3)

    native Long vx_evt_buddy_and_group_list_changed_t_base_get(Long var0, vx_evt_buddy_and_group_list_changed_t var2)

    native Unit vx_evt_buddy_and_group_list_changed_t_base_set(Long var0, vx_evt_buddy_and_group_list_changed_t var2, Long var3, vx_evt_base_t var5)

    native Long vx_evt_buddy_and_group_list_changed_t_buddies_get(Long var0, vx_evt_buddy_and_group_list_changed_t var2)

    native Unit vx_evt_buddy_and_group_list_changed_t_buddies_set(Long var0, vx_evt_buddy_and_group_list_changed_t var2, Long var3)

    native Int vx_evt_buddy_and_group_list_changed_t_buddy_count_get(Long var0, vx_evt_buddy_and_group_list_changed_t var2)

    native Unit vx_evt_buddy_and_group_list_changed_t_buddy_count_set(Long var0, vx_evt_buddy_and_group_list_changed_t var2, Int var3)

    native Long vx_evt_buddy_and_group_list_changed_t_get_buddies_item(Long var0, vx_evt_buddy_and_group_list_changed_t var2, Int var3)

    native Long vx_evt_buddy_and_group_list_changed_t_get_groups_item(Long var0, vx_evt_buddy_and_group_list_changed_t var2, Int var3)

    native Int vx_evt_buddy_and_group_list_changed_t_group_count_get(Long var0, vx_evt_buddy_and_group_list_changed_t var2)

    native Unit vx_evt_buddy_and_group_list_changed_t_group_count_set(Long var0, vx_evt_buddy_and_group_list_changed_t var2, Int var3)

    native Long vx_evt_buddy_and_group_list_changed_t_groups_get(Long var0, vx_evt_buddy_and_group_list_changed_t var2)

    native Unit vx_evt_buddy_and_group_list_changed_t_groups_set(Long var0, vx_evt_buddy_and_group_list_changed_t var2, Long var3)

    native String vx_evt_buddy_changed_t_account_handle_get(Long var0, vx_evt_buddy_changed_t var2)

    native Unit vx_evt_buddy_changed_t_account_handle_set(Long var0, vx_evt_buddy_changed_t var2, String var3)

    native Int vx_evt_buddy_changed_t_account_id_get(Long var0, vx_evt_buddy_changed_t var2)

    native Unit vx_evt_buddy_changed_t_account_id_set(Long var0, vx_evt_buddy_changed_t var2, Int var3)

    native Long vx_evt_buddy_changed_t_base_get(Long var0, vx_evt_buddy_changed_t var2)

    native Unit vx_evt_buddy_changed_t_base_set(Long var0, vx_evt_buddy_changed_t var2, Long var3, vx_evt_base_t var5)

    native String vx_evt_buddy_changed_t_buddy_data_get(Long var0, vx_evt_buddy_changed_t var2)

    native Unit vx_evt_buddy_changed_t_buddy_data_set(Long var0, vx_evt_buddy_changed_t var2, String var3)

    native String vx_evt_buddy_changed_t_buddy_uri_get(Long var0, vx_evt_buddy_changed_t var2)

    native Unit vx_evt_buddy_changed_t_buddy_uri_set(Long var0, vx_evt_buddy_changed_t var2, String var3)

    native Int vx_evt_buddy_changed_t_change_type_get(Long var0, vx_evt_buddy_changed_t var2)

    native Unit vx_evt_buddy_changed_t_change_type_set(Long var0, vx_evt_buddy_changed_t var2, Int var3)

    native String vx_evt_buddy_changed_t_display_name_get(Long var0, vx_evt_buddy_changed_t var2)

    native Unit vx_evt_buddy_changed_t_display_name_set(Long var0, vx_evt_buddy_changed_t var2, String var3)

    native Int vx_evt_buddy_changed_t_group_id_get(Long var0, vx_evt_buddy_changed_t var2)

    native Unit vx_evt_buddy_changed_t_group_id_set(Long var0, vx_evt_buddy_changed_t var2, Int var3)

    native String vx_evt_buddy_group_changed_t_account_handle_get(Long var0, vx_evt_buddy_group_changed_t var2)

    native Unit vx_evt_buddy_group_changed_t_account_handle_set(Long var0, vx_evt_buddy_group_changed_t var2, String var3)

    native Long vx_evt_buddy_group_changed_t_base_get(Long var0, vx_evt_buddy_group_changed_t var2)

    native Unit vx_evt_buddy_group_changed_t_base_set(Long var0, vx_evt_buddy_group_changed_t var2, Long var3, vx_evt_base_t var5)

    native Int vx_evt_buddy_group_changed_t_change_type_get(Long var0, vx_evt_buddy_group_changed_t var2)

    native Unit vx_evt_buddy_group_changed_t_change_type_set(Long var0, vx_evt_buddy_group_changed_t var2, Int var3)

    native String vx_evt_buddy_group_changed_t_group_data_get(Long var0, vx_evt_buddy_group_changed_t var2)

    native Unit vx_evt_buddy_group_changed_t_group_data_set(Long var0, vx_evt_buddy_group_changed_t var2, String var3)

    native Int vx_evt_buddy_group_changed_t_group_id_get(Long var0, vx_evt_buddy_group_changed_t var2)

    native Unit vx_evt_buddy_group_changed_t_group_id_set(Long var0, vx_evt_buddy_group_changed_t var2, Int var3)

    native String vx_evt_buddy_group_changed_t_group_name_get(Long var0, vx_evt_buddy_group_changed_t var2)

    native Unit vx_evt_buddy_group_changed_t_group_name_set(Long var0, vx_evt_buddy_group_changed_t var2, String var3)

    native String vx_evt_buddy_presence_t_account_handle_get(Long var0, vx_evt_buddy_presence_t var2)

    native Unit vx_evt_buddy_presence_t_account_handle_set(Long var0, vx_evt_buddy_presence_t var2, String var3)

    native String vx_evt_buddy_presence_t_application_get(Long var0, vx_evt_buddy_presence_t var2)

    native Unit vx_evt_buddy_presence_t_application_set(Long var0, vx_evt_buddy_presence_t var2, String var3)

    native Long vx_evt_buddy_presence_t_base_get(Long var0, vx_evt_buddy_presence_t var2)

    native Unit vx_evt_buddy_presence_t_base_set(Long var0, vx_evt_buddy_presence_t var2, Long var3, vx_evt_base_t var5)

    native String vx_evt_buddy_presence_t_buddy_uri_get(Long var0, vx_evt_buddy_presence_t var2)

    native Unit vx_evt_buddy_presence_t_buddy_uri_set(Long var0, vx_evt_buddy_presence_t var2, String var3)

    native String vx_evt_buddy_presence_t_contact_get(Long var0, vx_evt_buddy_presence_t var2)

    native Unit vx_evt_buddy_presence_t_contact_set(Long var0, vx_evt_buddy_presence_t var2, String var3)

    native String vx_evt_buddy_presence_t_custom_message_get(Long var0, vx_evt_buddy_presence_t var2)

    native Unit vx_evt_buddy_presence_t_custom_message_set(Long var0, vx_evt_buddy_presence_t var2, String var3)

    native String vx_evt_buddy_presence_t_displayname_get(Long var0, vx_evt_buddy_presence_t var2)

    native Unit vx_evt_buddy_presence_t_displayname_set(Long var0, vx_evt_buddy_presence_t var2, String var3)

    native String vx_evt_buddy_presence_t_id_get(Long var0, vx_evt_buddy_presence_t var2)

    native Unit vx_evt_buddy_presence_t_id_set(Long var0, vx_evt_buddy_presence_t var2, String var3)

    native Int vx_evt_buddy_presence_t_presence_get(Long var0, vx_evt_buddy_presence_t var2)

    native Unit vx_evt_buddy_presence_t_presence_set(Long var0, vx_evt_buddy_presence_t var2, Int var3)

    native String vx_evt_buddy_presence_t_priority_get(Long var0, vx_evt_buddy_presence_t var2)

    native Unit vx_evt_buddy_presence_t_priority_set(Long var0, vx_evt_buddy_presence_t var2, String var3)

    native Int vx_evt_buddy_presence_t_state_get(Long var0, vx_evt_buddy_presence_t var2)

    native Unit vx_evt_buddy_presence_t_state_set(Long var0, vx_evt_buddy_presence_t var2, Int var3)

    native Long vx_evt_idle_state_changed_t_base_get(Long var0, vx_evt_idle_state_changed_t var2)

    native Unit vx_evt_idle_state_changed_t_base_set(Long var0, vx_evt_idle_state_changed_t var2, Long var3, vx_evt_base_t var5)

    native Int vx_evt_idle_state_changed_t_is_idle_get(Long var0, vx_evt_idle_state_changed_t var2)

    native Unit vx_evt_idle_state_changed_t_is_idle_set(Long var0, vx_evt_idle_state_changed_t var2, Int var3)

    native Long vx_evt_keyboard_mouse_t_base_get(Long var0, vx_evt_keyboard_mouse_t var2)

    native Unit vx_evt_keyboard_mouse_t_base_set(Long var0, vx_evt_keyboard_mouse_t var2, Long var3, vx_evt_base_t var5)

    native Int vx_evt_keyboard_mouse_t_is_down_get(Long var0, vx_evt_keyboard_mouse_t var2)

    native Unit vx_evt_keyboard_mouse_t_is_down_set(Long var0, vx_evt_keyboard_mouse_t var2, Int var3)

    native String vx_evt_keyboard_mouse_t_name_get(Long var0, vx_evt_keyboard_mouse_t var2)

    native Unit vx_evt_keyboard_mouse_t_name_set(Long var0, vx_evt_keyboard_mouse_t var2, String var3)

    native Long vx_evt_media_completion_t_base_get(Long var0, vx_evt_media_completion_t var2)

    native Unit vx_evt_media_completion_t_base_set(Long var0, vx_evt_media_completion_t var2, Long var3, vx_evt_base_t var5)

    native Int vx_evt_media_completion_t_completion_type_get(Long var0, vx_evt_media_completion_t var2)

    native Unit vx_evt_media_completion_t_completion_type_set(Long var0, vx_evt_media_completion_t var2, Int var3)

    native String vx_evt_media_completion_t_sessiongroup_handle_get(Long var0, vx_evt_media_completion_t var2)

    native Unit vx_evt_media_completion_t_sessiongroup_handle_set(Long var0, vx_evt_media_completion_t var2, String var3)

    native Long vx_evt_media_stream_updated_t_base_get(Long var0, vx_evt_media_stream_updated_t var2)

    native Unit vx_evt_media_stream_updated_t_base_set(Long var0, vx_evt_media_stream_updated_t var2, Long var3, vx_evt_base_t var5)

    native String vx_evt_media_stream_updated_t_durable_media_id_get(Long var0, vx_evt_media_stream_updated_t var2)

    native Unit vx_evt_media_stream_updated_t_durable_media_id_set(Long var0, vx_evt_media_stream_updated_t var2, String var3)

    native Int vx_evt_media_stream_updated_t_incoming_get(Long var0, vx_evt_media_stream_updated_t var2)

    native Unit vx_evt_media_stream_updated_t_incoming_set(Long var0, vx_evt_media_stream_updated_t var2, Int var3)

    native String vx_evt_media_stream_updated_t_media_probe_server_get(Long var0, vx_evt_media_stream_updated_t var2)

    native Unit vx_evt_media_stream_updated_t_media_probe_server_set(Long var0, vx_evt_media_stream_updated_t var2, String var3)

    native String vx_evt_media_stream_updated_t_session_handle_get(Long var0, vx_evt_media_stream_updated_t var2)

    native Unit vx_evt_media_stream_updated_t_session_handle_set(Long var0, vx_evt_media_stream_updated_t var2, String var3)

    native String vx_evt_media_stream_updated_t_sessiongroup_handle_get(Long var0, vx_evt_media_stream_updated_t var2)

    native Unit vx_evt_media_stream_updated_t_sessiongroup_handle_set(Long var0, vx_evt_media_stream_updated_t var2, String var3)

    native Int vx_evt_media_stream_updated_t_state_get(Long var0, vx_evt_media_stream_updated_t var2)

    native Unit vx_evt_media_stream_updated_t_state_set(Long var0, vx_evt_media_stream_updated_t var2, Int var3)

    native Int vx_evt_media_stream_updated_t_status_code_get(Long var0, vx_evt_media_stream_updated_t var2)

    native Unit vx_evt_media_stream_updated_t_status_code_set(Long var0, vx_evt_media_stream_updated_t var2, Int var3)

    native String vx_evt_media_stream_updated_t_status_string_get(Long var0, vx_evt_media_stream_updated_t var2)

    native Unit vx_evt_media_stream_updated_t_status_string_set(Long var0, vx_evt_media_stream_updated_t var2, String var3)

    native String vx_evt_message_t_alias_username_get(Long var0, vx_evt_message_t var2)

    native Unit vx_evt_message_t_alias_username_set(Long var0, vx_evt_message_t var2, String var3)

    native String vx_evt_message_t_application_get(Long var0, vx_evt_message_t var2)

    native Unit vx_evt_message_t_application_set(Long var0, vx_evt_message_t var2, String var3)

    native Long vx_evt_message_t_base_get(Long var0, vx_evt_message_t var2)

    native Unit vx_evt_message_t_base_set(Long var0, vx_evt_message_t var2, Long var3, vx_evt_base_t var5)

    native String vx_evt_message_t_message_body_get(Long var0, vx_evt_message_t var2)

    native Unit vx_evt_message_t_message_body_set(Long var0, vx_evt_message_t var2, String var3)

    native String vx_evt_message_t_message_header_get(Long var0, vx_evt_message_t var2)

    native Unit vx_evt_message_t_message_header_set(Long var0, vx_evt_message_t var2, String var3)

    native String vx_evt_message_t_participant_displayname_get(Long var0, vx_evt_message_t var2)

    native Unit vx_evt_message_t_participant_displayname_set(Long var0, vx_evt_message_t var2, String var3)

    native String vx_evt_message_t_participant_uri_get(Long var0, vx_evt_message_t var2)

    native Unit vx_evt_message_t_participant_uri_set(Long var0, vx_evt_message_t var2, String var3)

    native String vx_evt_message_t_session_handle_get(Long var0, vx_evt_message_t var2)

    native Unit vx_evt_message_t_session_handle_set(Long var0, vx_evt_message_t var2, String var3)

    native String vx_evt_message_t_sessiongroup_handle_get(Long var0, vx_evt_message_t var2)

    native Unit vx_evt_message_t_sessiongroup_handle_set(Long var0, vx_evt_message_t var2, String var3)

    native Int vx_evt_message_t_state_get(Long var0, vx_evt_message_t var2)

    native Unit vx_evt_message_t_state_set(Long var0, vx_evt_message_t var2, Int var3)

    native String vx_evt_network_message_t_account_handle_get(Long var0, vx_evt_network_message_t var2)

    native Unit vx_evt_network_message_t_account_handle_set(Long var0, vx_evt_network_message_t var2, String var3)

    native Long vx_evt_network_message_t_base_get(Long var0, vx_evt_network_message_t var2)

    native Unit vx_evt_network_message_t_base_set(Long var0, vx_evt_network_message_t var2, Long var3, vx_evt_base_t var5)

    native String vx_evt_network_message_t_content_get(Long var0, vx_evt_network_message_t var2)

    native Unit vx_evt_network_message_t_content_set(Long var0, vx_evt_network_message_t var2, String var3)

    native String vx_evt_network_message_t_content_type_get(Long var0, vx_evt_network_message_t var2)

    native Unit vx_evt_network_message_t_content_type_set(Long var0, vx_evt_network_message_t var2, String var3)

    native Int vx_evt_network_message_t_network_message_type_get(Long var0, vx_evt_network_message_t var2)

    native Unit vx_evt_network_message_t_network_message_type_set(Long var0, vx_evt_network_message_t var2, Int var3)

    native String vx_evt_network_message_t_receiver_alias_username_get(Long var0, vx_evt_network_message_t var2)

    native Unit vx_evt_network_message_t_receiver_alias_username_set(Long var0, vx_evt_network_message_t var2, String var3)

    native String vx_evt_network_message_t_sender_alias_username_get(Long var0, vx_evt_network_message_t var2)

    native Unit vx_evt_network_message_t_sender_alias_username_set(Long var0, vx_evt_network_message_t var2, String var3)

    native String vx_evt_network_message_t_sender_display_name_get(Long var0, vx_evt_network_message_t var2)

    native Unit vx_evt_network_message_t_sender_display_name_set(Long var0, vx_evt_network_message_t var2, String var3)

    native String vx_evt_network_message_t_sender_uri_get(Long var0, vx_evt_network_message_t var2)

    native Unit vx_evt_network_message_t_sender_uri_set(Long var0, vx_evt_network_message_t var2, String var3)

    native Int vx_evt_network_message_type_admin_message_get()

    native Int vx_evt_network_message_type_offline_message_get()

    native Int vx_evt_network_message_type_sessionless_message_get()

    native String vx_evt_participant_added_t_account_name_get(Long var0, vx_evt_participant_added_t var2)

    native Unit vx_evt_participant_added_t_account_name_set(Long var0, vx_evt_participant_added_t var2, String var3)

    native String vx_evt_participant_added_t_alias_username_get(Long var0, vx_evt_participant_added_t var2)

    native Unit vx_evt_participant_added_t_alias_username_set(Long var0, vx_evt_participant_added_t var2, String var3)

    native String vx_evt_participant_added_t_application_get(Long var0, vx_evt_participant_added_t var2)

    native Unit vx_evt_participant_added_t_application_set(Long var0, vx_evt_participant_added_t var2, String var3)

    native Long vx_evt_participant_added_t_base_get(Long var0, vx_evt_participant_added_t var2)

    native Unit vx_evt_participant_added_t_base_set(Long var0, vx_evt_participant_added_t var2, Long var3, vx_evt_base_t var5)

    native String vx_evt_participant_added_t_display_name_get(Long var0, vx_evt_participant_added_t var2)

    native Unit vx_evt_participant_added_t_display_name_set(Long var0, vx_evt_participant_added_t var2, String var3)

    native String vx_evt_participant_added_t_displayname_get(Long var0, vx_evt_participant_added_t var2)

    native Unit vx_evt_participant_added_t_displayname_set(Long var0, vx_evt_participant_added_t var2, String var3)

    native Int vx_evt_participant_added_t_is_anonymous_login_get(Long var0, vx_evt_participant_added_t var2)

    native Unit vx_evt_participant_added_t_is_anonymous_login_set(Long var0, vx_evt_participant_added_t var2, Int var3)

    native Int vx_evt_participant_added_t_participant_type_get(Long var0, vx_evt_participant_added_t var2)

    native Unit vx_evt_participant_added_t_participant_type_set(Long var0, vx_evt_participant_added_t var2, Int var3)

    native String vx_evt_participant_added_t_participant_uri_get(Long var0, vx_evt_participant_added_t var2)

    native Unit vx_evt_participant_added_t_participant_uri_set(Long var0, vx_evt_participant_added_t var2, String var3)

    native String vx_evt_participant_added_t_session_handle_get(Long var0, vx_evt_participant_added_t var2)

    native Unit vx_evt_participant_added_t_session_handle_set(Long var0, vx_evt_participant_added_t var2, String var3)

    native String vx_evt_participant_added_t_sessiongroup_handle_get(Long var0, vx_evt_participant_added_t var2)

    native Unit vx_evt_participant_added_t_sessiongroup_handle_set(Long var0, vx_evt_participant_added_t var2, String var3)

    native String vx_evt_participant_removed_t_account_name_get(Long var0, vx_evt_participant_removed_t var2)

    native Unit vx_evt_participant_removed_t_account_name_set(Long var0, vx_evt_participant_removed_t var2, String var3)

    native String vx_evt_participant_removed_t_alias_username_get(Long var0, vx_evt_participant_removed_t var2)

    native Unit vx_evt_participant_removed_t_alias_username_set(Long var0, vx_evt_participant_removed_t var2, String var3)

    native Long vx_evt_participant_removed_t_base_get(Long var0, vx_evt_participant_removed_t var2)

    native Unit vx_evt_participant_removed_t_base_set(Long var0, vx_evt_participant_removed_t var2, Long var3, vx_evt_base_t var5)

    native String vx_evt_participant_removed_t_participant_uri_get(Long var0, vx_evt_participant_removed_t var2)

    native Unit vx_evt_participant_removed_t_participant_uri_set(Long var0, vx_evt_participant_removed_t var2, String var3)

    native Int vx_evt_participant_removed_t_reason_get(Long var0, vx_evt_participant_removed_t var2)

    native Unit vx_evt_participant_removed_t_reason_set(Long var0, vx_evt_participant_removed_t var2, Int var3)

    native String vx_evt_participant_removed_t_session_handle_get(Long var0, vx_evt_participant_removed_t var2)

    native Unit vx_evt_participant_removed_t_session_handle_set(Long var0, vx_evt_participant_removed_t var2, String var3)

    native String vx_evt_participant_removed_t_sessiongroup_handle_get(Long var0, vx_evt_participant_removed_t var2)

    native Unit vx_evt_participant_removed_t_sessiongroup_handle_set(Long var0, vx_evt_participant_removed_t var2, String var3)

    native Int vx_evt_participant_updated_t_active_media_get(Long var0, vx_evt_participant_updated_t var2)

    native Unit vx_evt_participant_updated_t_active_media_set(Long var0, vx_evt_participant_updated_t var2, Int var3)

    native String vx_evt_participant_updated_t_alias_username_get(Long var0, vx_evt_participant_updated_t var2)

    native Unit vx_evt_participant_updated_t_alias_username_set(Long var0, vx_evt_participant_updated_t var2, String var3)

    native Long vx_evt_participant_updated_t_base_get(Long var0, vx_evt_participant_updated_t var2)

    native Unit vx_evt_participant_updated_t_base_set(Long var0, vx_evt_participant_updated_t var2, Long var3, vx_evt_base_t var5)

    native Int vx_evt_participant_updated_t_diagnostic_state_count_get(Long var0, vx_evt_participant_updated_t var2)

    native Unit vx_evt_participant_updated_t_diagnostic_state_count_set(Long var0, vx_evt_participant_updated_t var2, Int var3)

    native Long vx_evt_participant_updated_t_diagnostic_states_get(Long var0, vx_evt_participant_updated_t var2)

    native Unit vx_evt_participant_updated_t_diagnostic_states_set(Long var0, vx_evt_participant_updated_t var2, Long var3)

    native Double vx_evt_participant_updated_t_energy_get(Long var0, vx_evt_participant_updated_t var2)

    native Unit vx_evt_participant_updated_t_energy_set(Long var0, vx_evt_participant_updated_t var2, Double var3)

    native Int vx_evt_participant_updated_t_get_diagnostic_states_item(Long var0, vx_evt_participant_updated_t var2, Int var3)

    native Int vx_evt_participant_updated_t_is_moderator_muted_get(Long var0, vx_evt_participant_updated_t var2)

    native Unit vx_evt_participant_updated_t_is_moderator_muted_set(Long var0, vx_evt_participant_updated_t var2, Int var3)

    native Int vx_evt_participant_updated_t_is_moderator_text_muted_get(Long var0, vx_evt_participant_updated_t var2)

    native Unit vx_evt_participant_updated_t_is_moderator_text_muted_set(Long var0, vx_evt_participant_updated_t var2, Int var3)

    native Int vx_evt_participant_updated_t_is_muted_for_me_get(Long var0, vx_evt_participant_updated_t var2)

    native Unit vx_evt_participant_updated_t_is_muted_for_me_set(Long var0, vx_evt_participant_updated_t var2, Int var3)

    native Int vx_evt_participant_updated_t_is_speaking_get(Long var0, vx_evt_participant_updated_t var2)

    native Unit vx_evt_participant_updated_t_is_speaking_set(Long var0, vx_evt_participant_updated_t var2, Int var3)

    native Int vx_evt_participant_updated_t_is_text_muted_for_me_get(Long var0, vx_evt_participant_updated_t var2)

    native Unit vx_evt_participant_updated_t_is_text_muted_for_me_set(Long var0, vx_evt_participant_updated_t var2, Int var3)

    native String vx_evt_participant_updated_t_participant_uri_get(Long var0, vx_evt_participant_updated_t var2)

    native Unit vx_evt_participant_updated_t_participant_uri_set(Long var0, vx_evt_participant_updated_t var2, String var3)

    native String vx_evt_participant_updated_t_session_handle_get(Long var0, vx_evt_participant_updated_t var2)

    native Unit vx_evt_participant_updated_t_session_handle_set(Long var0, vx_evt_participant_updated_t var2, String var3)

    native String vx_evt_participant_updated_t_sessiongroup_handle_get(Long var0, vx_evt_participant_updated_t var2)

    native Unit vx_evt_participant_updated_t_sessiongroup_handle_set(Long var0, vx_evt_participant_updated_t var2, String var3)

    native Int vx_evt_participant_updated_t_type_get(Long var0, vx_evt_participant_updated_t var2)

    native Unit vx_evt_participant_updated_t_type_set(Long var0, vx_evt_participant_updated_t var2, Int var3)

    native Int vx_evt_participant_updated_t_volume_get(Long var0, vx_evt_participant_updated_t var2)

    native Unit vx_evt_participant_updated_t_volume_set(Long var0, vx_evt_participant_updated_t var2, Int var3)

    native String vx_evt_server_app_data_t_account_handle_get(Long var0, vx_evt_server_app_data_t var2)

    native Unit vx_evt_server_app_data_t_account_handle_set(Long var0, vx_evt_server_app_data_t var2, String var3)

    native Long vx_evt_server_app_data_t_base_get(Long var0, vx_evt_server_app_data_t var2)

    native Unit vx_evt_server_app_data_t_base_set(Long var0, vx_evt_server_app_data_t var2, Long var3, vx_evt_base_t var5)

    native String vx_evt_server_app_data_t_content_get(Long var0, vx_evt_server_app_data_t var2)

    native Unit vx_evt_server_app_data_t_content_set(Long var0, vx_evt_server_app_data_t var2, String var3)

    native String vx_evt_server_app_data_t_content_type_get(Long var0, vx_evt_server_app_data_t var2)

    native Unit vx_evt_server_app_data_t_content_type_set(Long var0, vx_evt_server_app_data_t var2, String var3)

    native String vx_evt_session_added_t_alias_username_get(Long var0, vx_evt_session_added_t var2)

    native Unit vx_evt_session_added_t_alias_username_set(Long var0, vx_evt_session_added_t var2, String var3)

    native String vx_evt_session_added_t_application_get(Long var0, vx_evt_session_added_t var2)

    native Unit vx_evt_session_added_t_application_set(Long var0, vx_evt_session_added_t var2, String var3)

    native Long vx_evt_session_added_t_base_get(Long var0, vx_evt_session_added_t var2)

    native Unit vx_evt_session_added_t_base_set(Long var0, vx_evt_session_added_t var2, Long var3, vx_evt_base_t var5)

    native String vx_evt_session_added_t_channel_name_get(Long var0, vx_evt_session_added_t var2)

    native Unit vx_evt_session_added_t_channel_name_set(Long var0, vx_evt_session_added_t var2, String var3)

    native String vx_evt_session_added_t_displayname_get(Long var0, vx_evt_session_added_t var2)

    native Unit vx_evt_session_added_t_displayname_set(Long var0, vx_evt_session_added_t var2, String var3)

    native Int vx_evt_session_added_t_incoming_get(Long var0, vx_evt_session_added_t var2)

    native Unit vx_evt_session_added_t_incoming_set(Long var0, vx_evt_session_added_t var2, Int var3)

    native Int vx_evt_session_added_t_is_channel_get(Long var0, vx_evt_session_added_t var2)

    native Unit vx_evt_session_added_t_is_channel_set(Long var0, vx_evt_session_added_t var2, Int var3)

    native String vx_evt_session_added_t_session_handle_get(Long var0, vx_evt_session_added_t var2)

    native Unit vx_evt_session_added_t_session_handle_set(Long var0, vx_evt_session_added_t var2, String var3)

    native String vx_evt_session_added_t_sessiongroup_handle_get(Long var0, vx_evt_session_added_t var2)

    native Unit vx_evt_session_added_t_sessiongroup_handle_set(Long var0, vx_evt_session_added_t var2, String var3)

    native String vx_evt_session_added_t_uri_get(Long var0, vx_evt_session_added_t var2)

    native Unit vx_evt_session_added_t_uri_set(Long var0, vx_evt_session_added_t var2, String var3)

    native Long vx_evt_session_notification_t_base_get(Long var0, vx_evt_session_notification_t var2)

    native Unit vx_evt_session_notification_t_base_set(Long var0, vx_evt_session_notification_t var2, Long var3, vx_evt_base_t var5)

    native Int vx_evt_session_notification_t_notification_type_get(Long var0, vx_evt_session_notification_t var2)

    native Unit vx_evt_session_notification_t_notification_type_set(Long var0, vx_evt_session_notification_t var2, Int var3)

    native String vx_evt_session_notification_t_participant_uri_get(Long var0, vx_evt_session_notification_t var2)

    native Unit vx_evt_session_notification_t_participant_uri_set(Long var0, vx_evt_session_notification_t var2, String var3)

    native String vx_evt_session_notification_t_session_handle_get(Long var0, vx_evt_session_notification_t var2)

    native Unit vx_evt_session_notification_t_session_handle_set(Long var0, vx_evt_session_notification_t var2, String var3)

    native Int vx_evt_session_notification_t_state_get(Long var0, vx_evt_session_notification_t var2)

    native Unit vx_evt_session_notification_t_state_set(Long var0, vx_evt_session_notification_t var2, Int var3)

    native Long vx_evt_session_removed_t_base_get(Long var0, vx_evt_session_removed_t var2)

    native Unit vx_evt_session_removed_t_base_set(Long var0, vx_evt_session_removed_t var2, Long var3, vx_evt_base_t var5)

    native String vx_evt_session_removed_t_session_handle_get(Long var0, vx_evt_session_removed_t var2)

    native Unit vx_evt_session_removed_t_session_handle_set(Long var0, vx_evt_session_removed_t var2, String var3)

    native String vx_evt_session_removed_t_sessiongroup_handle_get(Long var0, vx_evt_session_removed_t var2)

    native Unit vx_evt_session_removed_t_sessiongroup_handle_set(Long var0, vx_evt_session_removed_t var2, String var3)

    native String vx_evt_session_removed_t_uri_get(Long var0, vx_evt_session_removed_t var2)

    native Unit vx_evt_session_removed_t_uri_set(Long var0, vx_evt_session_removed_t var2, String var3)

    native Long vx_evt_session_updated_t_base_get(Long var0, vx_evt_session_updated_t var2)

    native Unit vx_evt_session_updated_t_base_set(Long var0, vx_evt_session_updated_t var2, Long var3, vx_evt_base_t var5)

    native Double vx_evt_session_updated_t_get_speaker_position_item(Long var0, vx_evt_session_updated_t var2, Int var3)

    native Int vx_evt_session_updated_t_is_ad_playing_get(Long var0, vx_evt_session_updated_t var2)

    native Unit vx_evt_session_updated_t_is_ad_playing_set(Long var0, vx_evt_session_updated_t var2, Int var3)

    native Int vx_evt_session_updated_t_is_focused_get(Long var0, vx_evt_session_updated_t var2)

    native Unit vx_evt_session_updated_t_is_focused_set(Long var0, vx_evt_session_updated_t var2, Int var3)

    native Int vx_evt_session_updated_t_is_muted_get(Long var0, vx_evt_session_updated_t var2)

    native Unit vx_evt_session_updated_t_is_muted_set(Long var0, vx_evt_session_updated_t var2, Int var3)

    native Int vx_evt_session_updated_t_is_text_muted_get(Long var0, vx_evt_session_updated_t var2)

    native Unit vx_evt_session_updated_t_is_text_muted_set(Long var0, vx_evt_session_updated_t var2, Int var3)

    native Int vx_evt_session_updated_t_session_font_id_get(Long var0, vx_evt_session_updated_t var2)

    native Unit vx_evt_session_updated_t_session_font_id_set(Long var0, vx_evt_session_updated_t var2, Int var3)

    native String vx_evt_session_updated_t_session_handle_get(Long var0, vx_evt_session_updated_t var2)

    native Unit vx_evt_session_updated_t_session_handle_set(Long var0, vx_evt_session_updated_t var2, String var3)

    native String vx_evt_session_updated_t_sessiongroup_handle_get(Long var0, vx_evt_session_updated_t var2)

    native Unit vx_evt_session_updated_t_sessiongroup_handle_set(Long var0, vx_evt_session_updated_t var2, String var3)

    native Long vx_evt_session_updated_t_speaker_position_get(Long var0, vx_evt_session_updated_t var2)

    native Unit vx_evt_session_updated_t_speaker_position_set(Long var0, vx_evt_session_updated_t var2, Long var3)

    native Int vx_evt_session_updated_t_transmit_enabled_get(Long var0, vx_evt_session_updated_t var2)

    native Unit vx_evt_session_updated_t_transmit_enabled_set(Long var0, vx_evt_session_updated_t var2, Int var3)

    native String vx_evt_session_updated_t_uri_get(Long var0, vx_evt_session_updated_t var2)

    native Unit vx_evt_session_updated_t_uri_set(Long var0, vx_evt_session_updated_t var2, String var3)

    native Int vx_evt_session_updated_t_volume_get(Long var0, vx_evt_session_updated_t var2)

    native Unit vx_evt_session_updated_t_volume_set(Long var0, vx_evt_session_updated_t var2, Int var3)

    native String vx_evt_sessiongroup_added_t_account_handle_get(Long var0, vx_evt_sessiongroup_added_t var2)

    native Unit vx_evt_sessiongroup_added_t_account_handle_set(Long var0, vx_evt_sessiongroup_added_t var2, String var3)

    native String vx_evt_sessiongroup_added_t_alias_username_get(Long var0, vx_evt_sessiongroup_added_t var2)

    native Unit vx_evt_sessiongroup_added_t_alias_username_set(Long var0, vx_evt_sessiongroup_added_t var2, String var3)

    native Long vx_evt_sessiongroup_added_t_base_get(Long var0, vx_evt_sessiongroup_added_t var2)

    native Unit vx_evt_sessiongroup_added_t_base_set(Long var0, vx_evt_sessiongroup_added_t var2, Long var3, vx_evt_base_t var5)

    native String vx_evt_sessiongroup_added_t_sessiongroup_handle_get(Long var0, vx_evt_sessiongroup_added_t var2)

    native Unit vx_evt_sessiongroup_added_t_sessiongroup_handle_set(Long var0, vx_evt_sessiongroup_added_t var2, String var3)

    native Int vx_evt_sessiongroup_added_t_type_get(Long var0, vx_evt_sessiongroup_added_t var2)

    native Unit vx_evt_sessiongroup_added_t_type_set(Long var0, vx_evt_sessiongroup_added_t var2, Int var3)

    native Long vx_evt_sessiongroup_playback_frame_played_t_base_get(Long var0, vx_evt_sessiongroup_playback_frame_played_t var2)

    native Unit vx_evt_sessiongroup_playback_frame_played_t_base_set(Long var0, vx_evt_sessiongroup_playback_frame_played_t var2, Long var3, vx_evt_base_t var5)

    native Int vx_evt_sessiongroup_playback_frame_played_t_current_frame_get(Long var0, vx_evt_sessiongroup_playback_frame_played_t var2)

    native Unit vx_evt_sessiongroup_playback_frame_played_t_current_frame_set(Long var0, vx_evt_sessiongroup_playback_frame_played_t var2, Int var3)

    native Int vx_evt_sessiongroup_playback_frame_played_t_first_frame_get(Long var0, vx_evt_sessiongroup_playback_frame_played_t var2)

    native Unit vx_evt_sessiongroup_playback_frame_played_t_first_frame_set(Long var0, vx_evt_sessiongroup_playback_frame_played_t var2, Int var3)

    native String vx_evt_sessiongroup_playback_frame_played_t_sessiongroup_handle_get(Long var0, vx_evt_sessiongroup_playback_frame_played_t var2)

    native Unit vx_evt_sessiongroup_playback_frame_played_t_sessiongroup_handle_set(Long var0, vx_evt_sessiongroup_playback_frame_played_t var2, String var3)

    native Int vx_evt_sessiongroup_playback_frame_played_t_total_frames_get(Long var0, vx_evt_sessiongroup_playback_frame_played_t var2)

    native Unit vx_evt_sessiongroup_playback_frame_played_t_total_frames_set(Long var0, vx_evt_sessiongroup_playback_frame_played_t var2, Int var3)

    native Long vx_evt_sessiongroup_removed_t_base_get(Long var0, vx_evt_sessiongroup_removed_t var2)

    native Unit vx_evt_sessiongroup_removed_t_base_set(Long var0, vx_evt_sessiongroup_removed_t var2, Long var3, vx_evt_base_t var5)

    native String vx_evt_sessiongroup_removed_t_sessiongroup_handle_get(Long var0, vx_evt_sessiongroup_removed_t var2)

    native Unit vx_evt_sessiongroup_removed_t_sessiongroup_handle_set(Long var0, vx_evt_sessiongroup_removed_t var2, String var3)

    native Long vx_evt_sessiongroup_updated_t_base_get(Long var0, vx_evt_sessiongroup_updated_t var2)

    native Unit vx_evt_sessiongroup_updated_t_base_set(Long var0, vx_evt_sessiongroup_updated_t var2, Long var3, vx_evt_base_t var5)

    native Int vx_evt_sessiongroup_updated_t_current_playback_mode_get(Long var0, vx_evt_sessiongroup_updated_t var2)

    native Unit vx_evt_sessiongroup_updated_t_current_playback_mode_set(Long var0, vx_evt_sessiongroup_updated_t var2, Int var3)

    native Double vx_evt_sessiongroup_updated_t_current_playback_speed_get(Long var0, vx_evt_sessiongroup_updated_t var2)

    native Unit vx_evt_sessiongroup_updated_t_current_playback_speed_set(Long var0, vx_evt_sessiongroup_updated_t var2, Double var3)

    native String vx_evt_sessiongroup_updated_t_current_recording_filename_get(Long var0, vx_evt_sessiongroup_updated_t var2)

    native Unit vx_evt_sessiongroup_updated_t_current_recording_filename_set(Long var0, vx_evt_sessiongroup_updated_t var2, String var3)

    native Long vx_evt_sessiongroup_updated_t_first_frame_timestamp_us_get(Long var0, vx_evt_sessiongroup_updated_t var2)

    native Unit vx_evt_sessiongroup_updated_t_first_frame_timestamp_us_set(Long var0, vx_evt_sessiongroup_updated_t var2, Long var3)

    native Int vx_evt_sessiongroup_updated_t_first_loop_frame_get(Long var0, vx_evt_sessiongroup_updated_t var2)

    native Unit vx_evt_sessiongroup_updated_t_first_loop_frame_set(Long var0, vx_evt_sessiongroup_updated_t var2, Int var3)

    native Int vx_evt_sessiongroup_updated_t_in_delayed_playback_get(Long var0, vx_evt_sessiongroup_updated_t var2)

    native Unit vx_evt_sessiongroup_updated_t_in_delayed_playback_set(Long var0, vx_evt_sessiongroup_updated_t var2, Int var3)

    native Int vx_evt_sessiongroup_updated_t_last_loop_frame_played_get(Long var0, vx_evt_sessiongroup_updated_t var2)

    native Unit vx_evt_sessiongroup_updated_t_last_loop_frame_played_set(Long var0, vx_evt_sessiongroup_updated_t var2, Int var3)

    native Int vx_evt_sessiongroup_updated_t_loop_buffer_capacity_get(Long var0, vx_evt_sessiongroup_updated_t var2)

    native Unit vx_evt_sessiongroup_updated_t_loop_buffer_capacity_set(Long var0, vx_evt_sessiongroup_updated_t var2, Int var3)

    native Int vx_evt_sessiongroup_updated_t_playback_paused_get(Long var0, vx_evt_sessiongroup_updated_t var2)

    native Unit vx_evt_sessiongroup_updated_t_playback_paused_set(Long var0, vx_evt_sessiongroup_updated_t var2, Int var3)

    native String vx_evt_sessiongroup_updated_t_sessiongroup_handle_get(Long var0, vx_evt_sessiongroup_updated_t var2)

    native Unit vx_evt_sessiongroup_updated_t_sessiongroup_handle_set(Long var0, vx_evt_sessiongroup_updated_t var2, String var3)

    native Int vx_evt_sessiongroup_updated_t_total_loop_frames_captured_get(Long var0, vx_evt_sessiongroup_updated_t var2)

    native Unit vx_evt_sessiongroup_updated_t_total_loop_frames_captured_set(Long var0, vx_evt_sessiongroup_updated_t var2, Int var3)

    native Int vx_evt_sessiongroup_updated_t_total_recorded_frames_get(Long var0, vx_evt_sessiongroup_updated_t var2)

    native Unit vx_evt_sessiongroup_updated_t_total_recorded_frames_set(Long var0, vx_evt_sessiongroup_updated_t var2, Int var3)

    native String vx_evt_subscription_t_account_handle_get(Long var0, vx_evt_subscription_t var2)

    native Unit vx_evt_subscription_t_account_handle_set(Long var0, vx_evt_subscription_t var2, String var3)

    native String vx_evt_subscription_t_application_get(Long var0, vx_evt_subscription_t var2)

    native Unit vx_evt_subscription_t_application_set(Long var0, vx_evt_subscription_t var2, String var3)

    native Long vx_evt_subscription_t_base_get(Long var0, vx_evt_subscription_t var2)

    native Unit vx_evt_subscription_t_base_set(Long var0, vx_evt_subscription_t var2, Long var3, vx_evt_base_t var5)

    native String vx_evt_subscription_t_buddy_uri_get(Long var0, vx_evt_subscription_t var2)

    native Unit vx_evt_subscription_t_buddy_uri_set(Long var0, vx_evt_subscription_t var2, String var3)

    native String vx_evt_subscription_t_displayname_get(Long var0, vx_evt_subscription_t var2)

    native Unit vx_evt_subscription_t_displayname_set(Long var0, vx_evt_subscription_t var2, String var3)

    native String vx_evt_subscription_t_message_get(Long var0, vx_evt_subscription_t var2)

    native Unit vx_evt_subscription_t_message_set(Long var0, vx_evt_subscription_t var2, String var3)

    native String vx_evt_subscription_t_subscription_handle_get(Long var0, vx_evt_subscription_t var2)

    native Unit vx_evt_subscription_t_subscription_handle_set(Long var0, vx_evt_subscription_t var2, String var3)

    native Int vx_evt_subscription_t_subscription_type_get(Long var0, vx_evt_subscription_t var2)

    native Unit vx_evt_subscription_t_subscription_type_set(Long var0, vx_evt_subscription_t var2, Int var3)

    native Long vx_evt_text_stream_updated_t_base_get(Long var0, vx_evt_text_stream_updated_t var2)

    native Unit vx_evt_text_stream_updated_t_base_set(Long var0, vx_evt_text_stream_updated_t var2, Long var3, vx_evt_base_t var5)

    native Int vx_evt_text_stream_updated_t_enabled_get(Long var0, vx_evt_text_stream_updated_t var2)

    native Unit vx_evt_text_stream_updated_t_enabled_set(Long var0, vx_evt_text_stream_updated_t var2, Int var3)

    native Int vx_evt_text_stream_updated_t_incoming_get(Long var0, vx_evt_text_stream_updated_t var2)

    native Unit vx_evt_text_stream_updated_t_incoming_set(Long var0, vx_evt_text_stream_updated_t var2, Int var3)

    native String vx_evt_text_stream_updated_t_session_handle_get(Long var0, vx_evt_text_stream_updated_t var2)

    native Unit vx_evt_text_stream_updated_t_session_handle_set(Long var0, vx_evt_text_stream_updated_t var2, String var3)

    native String vx_evt_text_stream_updated_t_sessiongroup_handle_get(Long var0, vx_evt_text_stream_updated_t var2)

    native Unit vx_evt_text_stream_updated_t_sessiongroup_handle_set(Long var0, vx_evt_text_stream_updated_t var2, String var3)

    native Int vx_evt_text_stream_updated_t_state_get(Long var0, vx_evt_text_stream_updated_t var2)

    native Unit vx_evt_text_stream_updated_t_state_set(Long var0, vx_evt_text_stream_updated_t var2, Int var3)

    native Int vx_evt_text_stream_updated_t_status_code_get(Long var0, vx_evt_text_stream_updated_t var2)

    native Unit vx_evt_text_stream_updated_t_status_code_set(Long var0, vx_evt_text_stream_updated_t var2, Int var3)

    native String vx_evt_text_stream_updated_t_status_string_get(Long var0, vx_evt_text_stream_updated_t var2)

    native Unit vx_evt_text_stream_updated_t_status_string_set(Long var0, vx_evt_text_stream_updated_t var2, String var3)

    native String vx_evt_user_app_data_t_account_handle_get(Long var0, vx_evt_user_app_data_t var2)

    native Unit vx_evt_user_app_data_t_account_handle_set(Long var0, vx_evt_user_app_data_t var2, String var3)

    native Long vx_evt_user_app_data_t_base_get(Long var0, vx_evt_user_app_data_t var2)

    native Unit vx_evt_user_app_data_t_base_set(Long var0, vx_evt_user_app_data_t var2, Long var3, vx_evt_base_t var5)

    native String vx_evt_user_app_data_t_content_get(Long var0, vx_evt_user_app_data_t var2)

    native Unit vx_evt_user_app_data_t_content_set(Long var0, vx_evt_user_app_data_t var2, String var3)

    native String vx_evt_user_app_data_t_content_type_get(Long var0, vx_evt_user_app_data_t var2)

    native Unit vx_evt_user_app_data_t_content_type_set(Long var0, vx_evt_user_app_data_t var2, String var3)

    native String vx_evt_user_app_data_t_from_uri_get(Long var0, vx_evt_user_app_data_t var2)

    native Unit vx_evt_user_app_data_t_from_uri_set(Long var0, vx_evt_user_app_data_t var2, String var3)

    native Long vx_evt_voice_service_connection_state_changed_t_base_get(Long var0, vx_evt_voice_service_connection_state_changed_t var2)

    native Unit vx_evt_voice_service_connection_state_changed_t_base_set(Long var0, vx_evt_voice_service_connection_state_changed_t var2, Long var3, vx_evt_base_t var5)

    native Int vx_evt_voice_service_connection_state_changed_t_connected_get(Long var0, vx_evt_voice_service_connection_state_changed_t var2)

    native Unit vx_evt_voice_service_connection_state_changed_t_connected_set(Long var0, vx_evt_voice_service_connection_state_changed_t var2, Int var3)

    native String vx_evt_voice_service_connection_state_changed_t_data_directory_get(Long var0, vx_evt_voice_service_connection_state_changed_t var2)

    native Unit vx_evt_voice_service_connection_state_changed_t_data_directory_set(Long var0, vx_evt_voice_service_connection_state_changed_t var2, String var3)

    native Int vx_evt_voice_service_connection_state_changed_t_network_is_down_get(Long var0, vx_evt_voice_service_connection_state_changed_t var2)

    native Unit vx_evt_voice_service_connection_state_changed_t_network_is_down_set(Long var0, vx_evt_voice_service_connection_state_changed_t var2, Int var3)

    native Int vx_evt_voice_service_connection_state_changed_t_network_test_completed_get(Long var0, vx_evt_voice_service_connection_state_changed_t var2)

    native Unit vx_evt_voice_service_connection_state_changed_t_network_test_completed_set(Long var0, vx_evt_voice_service_connection_state_changed_t var2, Int var3)

    native Int vx_evt_voice_service_connection_state_changed_t_network_test_run_get(Long var0, vx_evt_voice_service_connection_state_changed_t var2)

    native Unit vx_evt_voice_service_connection_state_changed_t_network_test_run_set(Long var0, vx_evt_voice_service_connection_state_changed_t var2, Int var3)

    native Int vx_evt_voice_service_connection_state_changed_t_network_test_state_get(Long var0, vx_evt_voice_service_connection_state_changed_t var2)

    native Unit vx_evt_voice_service_connection_state_changed_t_network_test_state_set(Long var0, vx_evt_voice_service_connection_state_changed_t var2, Int var3)

    native String vx_evt_voice_service_connection_state_changed_t_platform_get(Long var0, vx_evt_voice_service_connection_state_changed_t var2)

    native Unit vx_evt_voice_service_connection_state_changed_t_platform_set(Long var0, vx_evt_voice_service_connection_state_changed_t var2, String var3)

    native String vx_evt_voice_service_connection_state_changed_t_version_get(Long var0, vx_evt_voice_service_connection_state_changed_t var2)

    native Unit vx_evt_voice_service_connection_state_changed_t_version_set(Long var0, vx_evt_voice_service_connection_state_changed_t var2, String var3)

    native Int vx_export_audioBuffer_to_wav_file(Long var0, String var2)

    native Int vx_export_vxr_expanded(String var0, String var1, Long var2, Long var4)

    native Int vx_export_vxr_mixed(String var0, String var1, Long var2, Long var4)

    native Unit vx_free(String var0)

    native Unit vx_free_audioBuffer(Long var0)

    native Int vx_free_sdk_handle(Long var0)

    native String vx_generic_credentials_admin_password_get(Long var0, vx_generic_credentials var2)

    native Unit vx_generic_credentials_admin_password_set(Long var0, vx_generic_credentials var2, String var3)

    native String vx_generic_credentials_admin_username_get(Long var0, vx_generic_credentials var2)

    native Unit vx_generic_credentials_admin_username_set(Long var0, vx_generic_credentials var2, String var3)

    native String vx_generic_credentials_grant_document_get(Long var0, vx_generic_credentials var2)

    native Unit vx_generic_credentials_grant_document_set(Long var0, vx_generic_credentials var2, String var3)

    native String vx_generic_credentials_server_url_get(Long var0, vx_generic_credentials var2)

    native Unit vx_generic_credentials_server_url_set(Long var0, vx_generic_credentials var2, String var3)

    native Double vx_get_audioBuffer_duration(Long var0)

    native Int vx_get_crash_dump_count()

    native Int vx_get_crash_dump_generation()

    native Long vx_get_crash_dump_timestamp(Int var0)

    native Int vx_get_default_config(Long var0, vx_sdk_config_t var2)

    native String vx_get_error_string(Int var0)

    native Int vx_get_message(Long var0)

    native Int vx_get_message_type(String var0)

    native Int vx_get_preferred_codec(Long var0)

    native String vx_get_sdk_version_info()

    native Int vx_get_system_stats(Long var0, vx_system_stats_t var2)

    native BigInteger vx_get_time_ms()

    native Unit vx_group_create(Long var0)

    native Unit vx_group_free(Long var0, vx_group_t var2)

    native Unit vx_group_list_create(Int var0, Long var1)

    native Unit vx_group_list_free(Long var0, Int var2)

    native String vx_group_t_group_data_get(Long var0, vx_group_t var2)

    native Unit vx_group_t_group_data_set(Long var0, vx_group_t var2, String var3)

    native Int vx_group_t_group_id_get(Long var0, vx_group_t var2)

    native Unit vx_group_t_group_id_set(Long var0, vx_group_t var2, Int var3)

    native String vx_group_t_group_name_get(Long var0, vx_group_t var2)

    native Unit vx_group_t_group_name_set(Long var0, vx_group_t var2, String var3)

    native Int vx_initialize()

    native Int vx_initialize2(Long var0, vx_sdk_config_t var2)

    native Int vx_issue_request(Long var0, vx_req_base_t var2)

    native Long vx_message_base_t2vx_evt_account_login_state_change_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_evt_aux_audio_properties_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_evt_buddy_and_group_list_changed_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_evt_buddy_changed_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_evt_buddy_group_changed_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_evt_buddy_presence_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_evt_idle_state_changed_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_evt_keyboard_mouse_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_evt_media_completion_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_evt_media_stream_updated_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_evt_message_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_evt_network_message_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_evt_participant_added_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_evt_participant_removed_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_evt_participant_updated_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_evt_server_app_data_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_evt_session_added_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_evt_session_notification_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_evt_session_removed_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_evt_session_updated_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_evt_sessiongroup_added_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_evt_sessiongroup_playback_frame_played_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_evt_sessiongroup_removed_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_evt_sessiongroup_updated_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_evt_subscription_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_evt_text_stream_updated_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_evt_user_app_data_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_evt_voice_service_connection_state_changed_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_account_anonymous_login_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_account_authtoken_login_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_account_buddy_delete_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_account_buddy_search_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_account_buddy_set_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_account_buddygroup_delete_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_account_buddygroup_set_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_account_channel_add_acl_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_account_channel_add_moderator_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_account_channel_change_owner_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_account_channel_create_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_account_channel_delete_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_account_channel_favorite_delete_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_account_channel_favorite_group_delete_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_account_channel_favorite_group_set_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_account_channel_favorite_set_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_account_channel_favorites_get_list_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_account_channel_get_acl_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_account_channel_get_info_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_account_channel_get_moderators_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_account_channel_get_participants_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_account_channel_remove_acl_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_account_channel_remove_moderator_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_account_channel_search_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_account_channel_update_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_account_create_auto_accept_rule_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_account_create_block_rule_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_account_delete_auto_accept_rule_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_account_delete_block_rule_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_account_get_account_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_account_get_session_fonts_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_account_get_template_fonts_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_account_list_auto_accept_rules_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_account_list_block_rules_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_account_list_buddies_and_groups_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_account_login_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_account_logout_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_account_post_crash_dump_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_account_send_message_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_account_send_sms_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_account_send_subscription_reply_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_account_send_user_app_data_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_account_set_login_properties_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_account_set_presence_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_account_update_account_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_account_web_call_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_aux_capture_audio_start_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_aux_capture_audio_stop_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_aux_connectivity_info_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_aux_create_account_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_aux_deactivate_account_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_aux_get_capture_devices_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_aux_get_mic_level_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_aux_get_render_devices_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_aux_get_speaker_level_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_aux_get_vad_properties_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_aux_global_monitor_keyboard_mouse_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_aux_play_audio_buffer_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_aux_reactivate_account_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_aux_render_audio_modify_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_aux_render_audio_start_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_aux_render_audio_stop_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_aux_reset_password_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_aux_set_capture_device_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_aux_set_idle_timeout_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_aux_set_mic_level_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_aux_set_render_device_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_aux_set_speaker_level_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_aux_set_vad_properties_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_aux_start_buffer_capture_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_channel_ban_user_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_channel_get_banned_users_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_channel_kick_user_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_channel_mute_all_users_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_channel_mute_user_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_channel_set_lock_mode_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_connector_create_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_connector_get_local_audio_info_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_connector_initiate_shutdown_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_connector_mute_local_mic_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_connector_mute_local_speaker_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_connector_set_local_mic_volume_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_connector_set_local_speaker_volume_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_session_channel_invite_user_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_session_create_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_session_media_connect_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_session_media_disconnect_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_session_mute_local_speaker_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_session_send_dtmf_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_session_send_message_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_session_send_notification_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_session_set_3d_position_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_session_set_local_speaker_volume_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_session_set_participant_mute_for_me_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_session_set_participant_volume_for_me_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_session_set_voice_font_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_session_terminate_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_session_text_connect_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_session_text_disconnect_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_sessiongroup_add_session_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_sessiongroup_control_audio_injection_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_sessiongroup_control_playback_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_sessiongroup_control_recording_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_sessiongroup_create_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_sessiongroup_get_stats_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_sessiongroup_remove_session_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_sessiongroup_reset_focus_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_sessiongroup_set_focus_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_sessiongroup_set_playback_options_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_sessiongroup_set_session_3d_position_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_sessiongroup_set_tx_all_sessions_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_sessiongroup_set_tx_no_session_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_sessiongroup_set_tx_session_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_sessiongroup_terminate_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_req_sessiongroup_unset_focus_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_account_anonymous_login_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_account_authtoken_login_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_account_buddy_delete_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_account_buddy_search_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_account_buddy_set_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_account_buddygroup_delete_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_account_buddygroup_set_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_account_channel_add_acl_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_account_channel_add_moderator_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_account_channel_change_owner_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_account_channel_create_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_account_channel_delete_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_account_channel_favorite_delete_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_account_channel_favorite_group_delete_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_account_channel_favorite_group_set_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_account_channel_favorite_set_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_account_channel_favorites_get_list_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_account_channel_get_acl_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_account_channel_get_info_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_account_channel_get_moderators_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_account_channel_get_participants_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_account_channel_remove_acl_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_account_channel_remove_moderator_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_account_channel_search_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_account_channel_update_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_account_create_auto_accept_rule_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_account_create_block_rule_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_account_delete_auto_accept_rule_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_account_delete_block_rule_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_account_get_account_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_account_get_session_fonts_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_account_get_template_fonts_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_account_list_auto_accept_rules_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_account_list_block_rules_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_account_list_buddies_and_groups_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_account_login_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_account_logout_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_account_post_crash_dump_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_account_send_message_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_account_send_sms_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_account_send_subscription_reply_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_account_send_user_app_data_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_account_set_login_properties_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_account_set_presence_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_account_update_account_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_account_web_call_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_aux_capture_audio_start_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_aux_capture_audio_stop_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_aux_connectivity_info_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_aux_create_account_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_aux_deactivate_account_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_aux_diagnostic_state_dump_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_aux_get_capture_devices_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_aux_get_mic_level_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_aux_get_render_devices_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_aux_get_speaker_level_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_aux_get_vad_properties_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_aux_global_monitor_keyboard_mouse_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_aux_play_audio_buffer_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_aux_reactivate_account_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_aux_render_audio_modify_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_aux_render_audio_start_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_aux_render_audio_stop_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_aux_reset_password_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_aux_set_capture_device_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_aux_set_idle_timeout_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_aux_set_mic_level_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_aux_set_render_device_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_aux_set_speaker_level_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_aux_set_vad_properties_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_aux_start_buffer_capture_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_channel_ban_user_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_channel_get_banned_users_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_channel_kick_user_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_channel_mute_all_users_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_channel_mute_user_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_channel_set_lock_mode_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_connector_create_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_connector_get_local_audio_info_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_connector_initiate_shutdown_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_connector_mute_local_mic_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_connector_mute_local_speaker_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_connector_set_local_mic_volume_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_connector_set_local_speaker_volume_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_session_channel_invite_user_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_session_create_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_session_media_connect_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_session_media_disconnect_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_session_mute_local_speaker_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_session_send_dtmf_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_session_send_message_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_session_send_notification_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_session_set_3d_position_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_session_set_local_speaker_volume_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_session_set_participant_mute_for_me_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_session_set_participant_volume_for_me_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_session_set_voice_font_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_session_terminate_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_session_text_connect_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_session_text_disconnect_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_sessiongroup_add_session_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_sessiongroup_control_audio_injection_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_sessiongroup_control_playback_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_sessiongroup_control_recording_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_sessiongroup_create_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_sessiongroup_get_stats_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_sessiongroup_remove_session_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_sessiongroup_reset_focus_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_sessiongroup_set_focus_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_sessiongroup_set_playback_options_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_sessiongroup_set_session_3d_position_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_sessiongroup_set_tx_all_sessions_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_sessiongroup_set_tx_no_session_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_sessiongroup_set_tx_session_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_sessiongroup_terminate_t(Long var0, vx_message_base_t var2)

    native Long vx_message_base_t2vx_resp_sessiongroup_unset_focus_t(Long var0, vx_message_base_t var2)

    native BigInteger vx_message_base_t_create_time_ms_get(Long var0, vx_message_base_t var2)

    native Unit vx_message_base_t_create_time_ms_set(Long var0, vx_message_base_t var2, BigInteger var3)

    native BigInteger vx_message_base_t_last_step_ms_get(Long var0, vx_message_base_t var2)

    native Unit vx_message_base_t_last_step_ms_set(Long var0, vx_message_base_t var2, BigInteger var3)

    native Long vx_message_base_t_sdk_handle_get(Long var0, vx_message_base_t var2)

    native Unit vx_message_base_t_sdk_handle_set(Long var0, vx_message_base_t var2, Long var3)

    native Int vx_message_base_t_type_get(Long var0, vx_message_base_t var2)

    native Unit vx_message_base_t_type_set(Long var0, vx_message_base_t var2, Int var3)

    native Unit vx_name_value_pair_create(Long var0)

    native Unit vx_name_value_pair_free(Long var0, vx_name_value_pair_t var2)

    native String vx_name_value_pair_t_name_get(Long var0, vx_name_value_pair_t var2)

    native Unit vx_name_value_pair_t_name_set(Long var0, vx_name_value_pair_t var2, String var3)

    native String vx_name_value_pair_t_value_get(Long var0, vx_name_value_pair_t var2)

    native Unit vx_name_value_pair_t_value_set(Long var0, vx_name_value_pair_t var2, String var3)

    native Unit vx_name_value_pairs_create(Int var0, Long var1)

    native Unit vx_name_value_pairs_free(Long var0, Int var2)

    native Long vx_name_value_pairs_t_get_list_item(Long var0, Int var2)

    native Unit vx_name_value_pairs_t_set_list_item(Long var0, Int var2, Long var3, vx_name_value_pair_t var5)

    native Unit vx_on_application_exit()

    native Unit vx_participant_create(Long var0)

    native Unit vx_participant_free(Long var0, vx_participant_t var2)

    native Unit vx_participant_list_create(Int var0, Long var1)

    native Unit vx_participant_list_free(Long var0, Int var2)

    native Int vx_participant_t_account_id_get(Long var0, vx_participant_t var2)

    native Unit vx_participant_t_account_id_set(Long var0, vx_participant_t var2, Int var3)

    native String vx_participant_t_display_name_get(Long var0, vx_participant_t var2)

    native Unit vx_participant_t_display_name_set(Long var0, vx_participant_t var2, String var3)

    native String vx_participant_t_first_name_get(Long var0, vx_participant_t var2)

    native Unit vx_participant_t_first_name_set(Long var0, vx_participant_t var2, String var3)

    native Int vx_participant_t_is_moderator_get(Long var0, vx_participant_t var2)

    native Int vx_participant_t_is_moderator_muted_get(Long var0, vx_participant_t var2)

    native Unit vx_participant_t_is_moderator_muted_set(Long var0, vx_participant_t var2, Int var3)

    native Unit vx_participant_t_is_moderator_set(Long var0, vx_participant_t var2, Int var3)

    native Int vx_participant_t_is_moderator_text_muted_get(Long var0, vx_participant_t var2)

    native Unit vx_participant_t_is_moderator_text_muted_set(Long var0, vx_participant_t var2, Int var3)

    native Int vx_participant_t_is_muted_for_me_get(Long var0, vx_participant_t var2)

    native Unit vx_participant_t_is_muted_for_me_set(Long var0, vx_participant_t var2, Int var3)

    native Int vx_participant_t_is_owner_get(Long var0, vx_participant_t var2)

    native Unit vx_participant_t_is_owner_set(Long var0, vx_participant_t var2, Int var3)

    native String vx_participant_t_last_name_get(Long var0, vx_participant_t var2)

    native Unit vx_participant_t_last_name_set(Long var0, vx_participant_t var2, String var3)

    native String vx_participant_t_uri_get(Long var0, vx_participant_t var2)

    native Unit vx_participant_t_uri_set(Long var0, vx_participant_t var2, String var3)

    native String vx_participant_t_username_get(Long var0, vx_participant_t var2)

    native Unit vx_participant_t_username_set(Long var0, vx_participant_t var2, String var3)

    native String vx_read_crash_dump(Int var0)

    native Unit vx_recording_frame_create(Long var0)

    native Unit vx_recording_frame_free(Long var0, vx_recording_frame_t var2)

    native Unit vx_recording_frame_list_create(Int var0, Long var1)

    native Unit vx_recording_frame_list_free(Long var0, Int var2)

    native Long vx_recording_frame_t_frame_data_get(Long var0, vx_recording_frame_t var2)

    native Unit vx_recording_frame_t_frame_data_set(Long var0, vx_recording_frame_t var2, Long var3)

    native Int vx_recording_frame_t_frame_size_get(Long var0, vx_recording_frame_t var2)

    native Unit vx_recording_frame_t_frame_size_set(Long var0, vx_recording_frame_t var2, Int var3)

    native Int vx_recording_frame_t_frame_type_get(Long var0, vx_recording_frame_t var2)

    native Unit vx_recording_frame_t_frame_type_set(Long var0, vx_recording_frame_t var2, Int var3)

    native Unit vx_register_logging_initialization(Int var0, String var1, String var2, String var3, Int var4, Long var5)

    native Unit vx_register_message_notification_handler(Long var0, Long var2)

    native Unit vx_req_account_anonymous_login_create(Long var0)

    native String vx_req_account_anonymous_login_t_acct_mgmt_server_get(Long var0, vx_req_account_anonymous_login_t var2)

    native Unit vx_req_account_anonymous_login_t_acct_mgmt_server_set(Long var0, vx_req_account_anonymous_login_t var2, String var3)

    native String vx_req_account_anonymous_login_t_application_override_get(Long var0, vx_req_account_anonymous_login_t var2)

    native Unit vx_req_account_anonymous_login_t_application_override_set(Long var0, vx_req_account_anonymous_login_t var2, String var3)

    native String vx_req_account_anonymous_login_t_application_token_get(Long var0, vx_req_account_anonymous_login_t var2)

    native Unit vx_req_account_anonymous_login_t_application_token_set(Long var0, vx_req_account_anonymous_login_t var2, String var3)

    native Int vx_req_account_anonymous_login_t_autopost_crash_dumps_get(Long var0, vx_req_account_anonymous_login_t var2)

    native Unit vx_req_account_anonymous_login_t_autopost_crash_dumps_set(Long var0, vx_req_account_anonymous_login_t var2, Int var3)

    native Long vx_req_account_anonymous_login_t_base_get(Long var0, vx_req_account_anonymous_login_t var2)

    native Unit vx_req_account_anonymous_login_t_base_set(Long var0, vx_req_account_anonymous_login_t var2, Long var3, vx_req_base_t var5)

    native Int vx_req_account_anonymous_login_t_buddy_management_mode_get(Long var0, vx_req_account_anonymous_login_t var2)

    native Unit vx_req_account_anonymous_login_t_buddy_management_mode_set(Long var0, vx_req_account_anonymous_login_t var2, Int var3)

    native String vx_req_account_anonymous_login_t_connector_handle_get(Long var0, vx_req_account_anonymous_login_t var2)

    native Unit vx_req_account_anonymous_login_t_connector_handle_set(Long var0, vx_req_account_anonymous_login_t var2, String var3)

    native String vx_req_account_anonymous_login_t_displayname_get(Long var0, vx_req_account_anonymous_login_t var2)

    native Unit vx_req_account_anonymous_login_t_displayname_set(Long var0, vx_req_account_anonymous_login_t var2, String var3)

    native Int vx_req_account_anonymous_login_t_enable_buddies_and_presence_get(Long var0, vx_req_account_anonymous_login_t var2)

    native Unit vx_req_account_anonymous_login_t_enable_buddies_and_presence_set(Long var0, vx_req_account_anonymous_login_t var2, Int var3)

    native Int vx_req_account_anonymous_login_t_participant_property_frequency_get(Long var0, vx_req_account_anonymous_login_t var2)

    native Unit vx_req_account_anonymous_login_t_participant_property_frequency_set(Long var0, vx_req_account_anonymous_login_t var2, Int var3)

    native Unit vx_req_account_authtoken_login_create(Long var0)

    native String vx_req_account_authtoken_login_t_acct_mgmt_server_get(Long var0, vx_req_account_authtoken_login_t var2)

    native Unit vx_req_account_authtoken_login_t_acct_mgmt_server_set(Long var0, vx_req_account_authtoken_login_t var2, String var3)

    native Int vx_req_account_authtoken_login_t_answer_mode_get(Long var0, vx_req_account_authtoken_login_t var2)

    native Unit vx_req_account_authtoken_login_t_answer_mode_set(Long var0, vx_req_account_authtoken_login_t var2, Int var3)

    native String vx_req_account_authtoken_login_t_application_override_get(Long var0, vx_req_account_authtoken_login_t var2)

    native Unit vx_req_account_authtoken_login_t_application_override_set(Long var0, vx_req_account_authtoken_login_t var2, String var3)

    native String vx_req_account_authtoken_login_t_application_token_get(Long var0, vx_req_account_authtoken_login_t var2)

    native Unit vx_req_account_authtoken_login_t_application_token_set(Long var0, vx_req_account_authtoken_login_t var2, String var3)

    native String vx_req_account_authtoken_login_t_authtoken_get(Long var0, vx_req_account_authtoken_login_t var2)

    native Unit vx_req_account_authtoken_login_t_authtoken_set(Long var0, vx_req_account_authtoken_login_t var2, String var3)

    native Int vx_req_account_authtoken_login_t_autopost_crash_dumps_get(Long var0, vx_req_account_authtoken_login_t var2)

    native Unit vx_req_account_authtoken_login_t_autopost_crash_dumps_set(Long var0, vx_req_account_authtoken_login_t var2, Int var3)

    native Long vx_req_account_authtoken_login_t_base_get(Long var0, vx_req_account_authtoken_login_t var2)

    native Unit vx_req_account_authtoken_login_t_base_set(Long var0, vx_req_account_authtoken_login_t var2, Long var3, vx_req_base_t var5)

    native Int vx_req_account_authtoken_login_t_buddy_management_mode_get(Long var0, vx_req_account_authtoken_login_t var2)

    native Unit vx_req_account_authtoken_login_t_buddy_management_mode_set(Long var0, vx_req_account_authtoken_login_t var2, Int var3)

    native String vx_req_account_authtoken_login_t_connector_handle_get(Long var0, vx_req_account_authtoken_login_t var2)

    native Unit vx_req_account_authtoken_login_t_connector_handle_set(Long var0, vx_req_account_authtoken_login_t var2, String var3)

    native Int vx_req_account_authtoken_login_t_enable_buddies_and_presence_get(Long var0, vx_req_account_authtoken_login_t var2)

    native Unit vx_req_account_authtoken_login_t_enable_buddies_and_presence_set(Long var0, vx_req_account_authtoken_login_t var2, Int var3)

    native Int vx_req_account_authtoken_login_t_enable_text_get(Long var0, vx_req_account_authtoken_login_t var2)

    native Unit vx_req_account_authtoken_login_t_enable_text_set(Long var0, vx_req_account_authtoken_login_t var2, Int var3)

    native Int vx_req_account_authtoken_login_t_participant_property_frequency_get(Long var0, vx_req_account_authtoken_login_t var2)

    native Unit vx_req_account_authtoken_login_t_participant_property_frequency_set(Long var0, vx_req_account_authtoken_login_t var2, Int var3)

    native Unit vx_req_account_buddy_delete_create(Long var0)

    native String vx_req_account_buddy_delete_t_account_handle_get(Long var0, vx_req_account_buddy_delete_t var2)

    native Unit vx_req_account_buddy_delete_t_account_handle_set(Long var0, vx_req_account_buddy_delete_t var2, String var3)

    native Long vx_req_account_buddy_delete_t_base_get(Long var0, vx_req_account_buddy_delete_t var2)

    native Unit vx_req_account_buddy_delete_t_base_set(Long var0, vx_req_account_buddy_delete_t var2, Long var3, vx_req_base_t var5)

    native String vx_req_account_buddy_delete_t_buddy_uri_get(Long var0, vx_req_account_buddy_delete_t var2)

    native Unit vx_req_account_buddy_delete_t_buddy_uri_set(Long var0, vx_req_account_buddy_delete_t var2, String var3)

    native Unit vx_req_account_buddy_search_create(Long var0)

    native String vx_req_account_buddy_search_t_account_handle_get(Long var0, vx_req_account_buddy_search_t var2)

    native Unit vx_req_account_buddy_search_t_account_handle_set(Long var0, vx_req_account_buddy_search_t var2, String var3)

    native Long vx_req_account_buddy_search_t_base_get(Long var0, vx_req_account_buddy_search_t var2)

    native Unit vx_req_account_buddy_search_t_base_set(Long var0, vx_req_account_buddy_search_t var2, Long var3, vx_req_base_t var5)

    native Int vx_req_account_buddy_search_t_begins_with_get(Long var0, vx_req_account_buddy_search_t var2)

    native Unit vx_req_account_buddy_search_t_begins_with_set(Long var0, vx_req_account_buddy_search_t var2, Int var3)

    native String vx_req_account_buddy_search_t_buddy_display_name_get(Long var0, vx_req_account_buddy_search_t var2)

    native Unit vx_req_account_buddy_search_t_buddy_display_name_set(Long var0, vx_req_account_buddy_search_t var2, String var3)

    native String vx_req_account_buddy_search_t_buddy_email_get(Long var0, vx_req_account_buddy_search_t var2)

    native Unit vx_req_account_buddy_search_t_buddy_email_set(Long var0, vx_req_account_buddy_search_t var2, String var3)

    native String vx_req_account_buddy_search_t_buddy_first_name_get(Long var0, vx_req_account_buddy_search_t var2)

    native Unit vx_req_account_buddy_search_t_buddy_first_name_set(Long var0, vx_req_account_buddy_search_t var2, String var3)

    native String vx_req_account_buddy_search_t_buddy_last_name_get(Long var0, vx_req_account_buddy_search_t var2)

    native Unit vx_req_account_buddy_search_t_buddy_last_name_set(Long var0, vx_req_account_buddy_search_t var2, String var3)

    native String vx_req_account_buddy_search_t_buddy_user_name_get(Long var0, vx_req_account_buddy_search_t var2)

    native Unit vx_req_account_buddy_search_t_buddy_user_name_set(Long var0, vx_req_account_buddy_search_t var2, String var3)

    native Int vx_req_account_buddy_search_t_page_number_get(Long var0, vx_req_account_buddy_search_t var2)

    native Unit vx_req_account_buddy_search_t_page_number_set(Long var0, vx_req_account_buddy_search_t var2, Int var3)

    native Int vx_req_account_buddy_search_t_page_size_get(Long var0, vx_req_account_buddy_search_t var2)

    native Unit vx_req_account_buddy_search_t_page_size_set(Long var0, vx_req_account_buddy_search_t var2, Int var3)

    native Unit vx_req_account_buddy_set_create(Long var0)

    native String vx_req_account_buddy_set_t_account_handle_get(Long var0, vx_req_account_buddy_set_t var2)

    native Unit vx_req_account_buddy_set_t_account_handle_set(Long var0, vx_req_account_buddy_set_t var2, String var3)

    native Long vx_req_account_buddy_set_t_base_get(Long var0, vx_req_account_buddy_set_t var2)

    native Unit vx_req_account_buddy_set_t_base_set(Long var0, vx_req_account_buddy_set_t var2, Long var3, vx_req_base_t var5)

    native String vx_req_account_buddy_set_t_buddy_data_get(Long var0, vx_req_account_buddy_set_t var2)

    native Unit vx_req_account_buddy_set_t_buddy_data_set(Long var0, vx_req_account_buddy_set_t var2, String var3)

    native String vx_req_account_buddy_set_t_buddy_uri_get(Long var0, vx_req_account_buddy_set_t var2)

    native Unit vx_req_account_buddy_set_t_buddy_uri_set(Long var0, vx_req_account_buddy_set_t var2, String var3)

    native String vx_req_account_buddy_set_t_display_name_get(Long var0, vx_req_account_buddy_set_t var2)

    native Unit vx_req_account_buddy_set_t_display_name_set(Long var0, vx_req_account_buddy_set_t var2, String var3)

    native Int vx_req_account_buddy_set_t_group_id_get(Long var0, vx_req_account_buddy_set_t var2)

    native Unit vx_req_account_buddy_set_t_group_id_set(Long var0, vx_req_account_buddy_set_t var2, Int var3)

    native String vx_req_account_buddy_set_t_message_get(Long var0, vx_req_account_buddy_set_t var2)

    native Unit vx_req_account_buddy_set_t_message_set(Long var0, vx_req_account_buddy_set_t var2, String var3)

    native Unit vx_req_account_buddygroup_delete_create(Long var0)

    native String vx_req_account_buddygroup_delete_t_account_handle_get(Long var0, vx_req_account_buddygroup_delete_t var2)

    native Unit vx_req_account_buddygroup_delete_t_account_handle_set(Long var0, vx_req_account_buddygroup_delete_t var2, String var3)

    native Long vx_req_account_buddygroup_delete_t_base_get(Long var0, vx_req_account_buddygroup_delete_t var2)

    native Unit vx_req_account_buddygroup_delete_t_base_set(Long var0, vx_req_account_buddygroup_delete_t var2, Long var3, vx_req_base_t var5)

    native Int vx_req_account_buddygroup_delete_t_group_id_get(Long var0, vx_req_account_buddygroup_delete_t var2)

    native Unit vx_req_account_buddygroup_delete_t_group_id_set(Long var0, vx_req_account_buddygroup_delete_t var2, Int var3)

    native Unit vx_req_account_buddygroup_set_create(Long var0)

    native String vx_req_account_buddygroup_set_t_account_handle_get(Long var0, vx_req_account_buddygroup_set_t var2)

    native Unit vx_req_account_buddygroup_set_t_account_handle_set(Long var0, vx_req_account_buddygroup_set_t var2, String var3)

    native Long vx_req_account_buddygroup_set_t_base_get(Long var0, vx_req_account_buddygroup_set_t var2)

    native Unit vx_req_account_buddygroup_set_t_base_set(Long var0, vx_req_account_buddygroup_set_t var2, Long var3, vx_req_base_t var5)

    native String vx_req_account_buddygroup_set_t_group_data_get(Long var0, vx_req_account_buddygroup_set_t var2)

    native Unit vx_req_account_buddygroup_set_t_group_data_set(Long var0, vx_req_account_buddygroup_set_t var2, String var3)

    native Int vx_req_account_buddygroup_set_t_group_id_get(Long var0, vx_req_account_buddygroup_set_t var2)

    native Unit vx_req_account_buddygroup_set_t_group_id_set(Long var0, vx_req_account_buddygroup_set_t var2, Int var3)

    native String vx_req_account_buddygroup_set_t_group_name_get(Long var0, vx_req_account_buddygroup_set_t var2)

    native Unit vx_req_account_buddygroup_set_t_group_name_set(Long var0, vx_req_account_buddygroup_set_t var2, String var3)

    native Unit vx_req_account_channel_add_acl_create(Long var0)

    native String vx_req_account_channel_add_acl_t_account_handle_get(Long var0, vx_req_account_channel_add_acl_t var2)

    native Unit vx_req_account_channel_add_acl_t_account_handle_set(Long var0, vx_req_account_channel_add_acl_t var2, String var3)

    native String vx_req_account_channel_add_acl_t_acl_uri_get(Long var0, vx_req_account_channel_add_acl_t var2)

    native Unit vx_req_account_channel_add_acl_t_acl_uri_set(Long var0, vx_req_account_channel_add_acl_t var2, String var3)

    native Long vx_req_account_channel_add_acl_t_base_get(Long var0, vx_req_account_channel_add_acl_t var2)

    native Unit vx_req_account_channel_add_acl_t_base_set(Long var0, vx_req_account_channel_add_acl_t var2, Long var3, vx_req_base_t var5)

    native String vx_req_account_channel_add_acl_t_channel_uri_get(Long var0, vx_req_account_channel_add_acl_t var2)

    native Unit vx_req_account_channel_add_acl_t_channel_uri_set(Long var0, vx_req_account_channel_add_acl_t var2, String var3)

    native Unit vx_req_account_channel_add_moderator_create(Long var0)

    native String vx_req_account_channel_add_moderator_t_account_handle_get(Long var0, vx_req_account_channel_add_moderator_t var2)

    native Unit vx_req_account_channel_add_moderator_t_account_handle_set(Long var0, vx_req_account_channel_add_moderator_t var2, String var3)

    native Long vx_req_account_channel_add_moderator_t_base_get(Long var0, vx_req_account_channel_add_moderator_t var2)

    native Unit vx_req_account_channel_add_moderator_t_base_set(Long var0, vx_req_account_channel_add_moderator_t var2, Long var3, vx_req_base_t var5)

    native String vx_req_account_channel_add_moderator_t_channel_name_get(Long var0, vx_req_account_channel_add_moderator_t var2)

    native Unit vx_req_account_channel_add_moderator_t_channel_name_set(Long var0, vx_req_account_channel_add_moderator_t var2, String var3)

    native String vx_req_account_channel_add_moderator_t_channel_uri_get(Long var0, vx_req_account_channel_add_moderator_t var2)

    native Unit vx_req_account_channel_add_moderator_t_channel_uri_set(Long var0, vx_req_account_channel_add_moderator_t var2, String var3)

    native String vx_req_account_channel_add_moderator_t_moderator_uri_get(Long var0, vx_req_account_channel_add_moderator_t var2)

    native Unit vx_req_account_channel_add_moderator_t_moderator_uri_set(Long var0, vx_req_account_channel_add_moderator_t var2, String var3)

    native Unit vx_req_account_channel_change_owner_create(Long var0)

    native String vx_req_account_channel_change_owner_t_account_handle_get(Long var0, vx_req_account_channel_change_owner_t var2)

    native Unit vx_req_account_channel_change_owner_t_account_handle_set(Long var0, vx_req_account_channel_change_owner_t var2, String var3)

    native Long vx_req_account_channel_change_owner_t_base_get(Long var0, vx_req_account_channel_change_owner_t var2)

    native Unit vx_req_account_channel_change_owner_t_base_set(Long var0, vx_req_account_channel_change_owner_t var2, Long var3, vx_req_base_t var5)

    native String vx_req_account_channel_change_owner_t_channel_uri_get(Long var0, vx_req_account_channel_change_owner_t var2)

    native Unit vx_req_account_channel_change_owner_t_channel_uri_set(Long var0, vx_req_account_channel_change_owner_t var2, String var3)

    native String vx_req_account_channel_change_owner_t_new_owner_uri_get(Long var0, vx_req_account_channel_change_owner_t var2)

    native Unit vx_req_account_channel_change_owner_t_new_owner_uri_set(Long var0, vx_req_account_channel_change_owner_t var2, String var3)

    native Unit vx_req_account_channel_create_create(Long var0)

    native String vx_req_account_channel_create_t_account_handle_get(Long var0, vx_req_account_channel_create_t var2)

    native Unit vx_req_account_channel_create_t_account_handle_set(Long var0, vx_req_account_channel_create_t var2, String var3)

    native Long vx_req_account_channel_create_t_base_get(Long var0, vx_req_account_channel_create_t var2)

    native Unit vx_req_account_channel_create_t_base_set(Long var0, vx_req_account_channel_create_t var2, Long var3, vx_req_base_t var5)

    native Int vx_req_account_channel_create_t_capacity_get(Long var0, vx_req_account_channel_create_t var2)

    native Unit vx_req_account_channel_create_t_capacity_set(Long var0, vx_req_account_channel_create_t var2, Int var3)

    native String vx_req_account_channel_create_t_channel_desc_get(Long var0, vx_req_account_channel_create_t var2)

    native Unit vx_req_account_channel_create_t_channel_desc_set(Long var0, vx_req_account_channel_create_t var2, String var3)

    native Int vx_req_account_channel_create_t_channel_mode_get(Long var0, vx_req_account_channel_create_t var2)

    native Unit vx_req_account_channel_create_t_channel_mode_set(Long var0, vx_req_account_channel_create_t var2, Int var3)

    native String vx_req_account_channel_create_t_channel_name_get(Long var0, vx_req_account_channel_create_t var2)

    native Unit vx_req_account_channel_create_t_channel_name_set(Long var0, vx_req_account_channel_create_t var2, String var3)

    native Int vx_req_account_channel_create_t_channel_type_get(Long var0, vx_req_account_channel_create_t var2)

    native Unit vx_req_account_channel_create_t_channel_type_set(Long var0, vx_req_account_channel_create_t var2, Int var3)

    native Int vx_req_account_channel_create_t_clamping_dist_get(Long var0, vx_req_account_channel_create_t var2)

    native Unit vx_req_account_channel_create_t_clamping_dist_set(Long var0, vx_req_account_channel_create_t var2, Int var3)

    native Int vx_req_account_channel_create_t_dist_model_get(Long var0, vx_req_account_channel_create_t var2)

    native Unit vx_req_account_channel_create_t_dist_model_set(Long var0, vx_req_account_channel_create_t var2, Int var3)

    native Int vx_req_account_channel_create_t_encrypt_audio_get(Long var0, vx_req_account_channel_create_t var2)

    native Unit vx_req_account_channel_create_t_encrypt_audio_set(Long var0, vx_req_account_channel_create_t var2, Int var3)

    native Double vx_req_account_channel_create_t_max_gain_get(Long var0, vx_req_account_channel_create_t var2)

    native Unit vx_req_account_channel_create_t_max_gain_set(Long var0, vx_req_account_channel_create_t var2, Double var3)

    native Int vx_req_account_channel_create_t_max_participants_get(Long var0, vx_req_account_channel_create_t var2)

    native Unit vx_req_account_channel_create_t_max_participants_set(Long var0, vx_req_account_channel_create_t var2, Int var3)

    native Int vx_req_account_channel_create_t_max_range_get(Long var0, vx_req_account_channel_create_t var2)

    native Unit vx_req_account_channel_create_t_max_range_set(Long var0, vx_req_account_channel_create_t var2, Int var3)

    native String vx_req_account_channel_create_t_protected_password_get(Long var0, vx_req_account_channel_create_t var2)

    native Unit vx_req_account_channel_create_t_protected_password_set(Long var0, vx_req_account_channel_create_t var2, String var3)

    native Double vx_req_account_channel_create_t_roll_off_get(Long var0, vx_req_account_channel_create_t var2)

    native Unit vx_req_account_channel_create_t_roll_off_set(Long var0, vx_req_account_channel_create_t var2, Double var3)

    native Int vx_req_account_channel_create_t_set_persistent_get(Long var0, vx_req_account_channel_create_t var2)

    native Unit vx_req_account_channel_create_t_set_persistent_set(Long var0, vx_req_account_channel_create_t var2, Int var3)

    native Int vx_req_account_channel_create_t_set_protected_get(Long var0, vx_req_account_channel_create_t var2)

    native Unit vx_req_account_channel_create_t_set_protected_set(Long var0, vx_req_account_channel_create_t var2, Int var3)

    native Unit vx_req_account_channel_delete_create(Long var0)

    native String vx_req_account_channel_delete_t_account_handle_get(Long var0, vx_req_account_channel_delete_t var2)

    native Unit vx_req_account_channel_delete_t_account_handle_set(Long var0, vx_req_account_channel_delete_t var2, String var3)

    native Long vx_req_account_channel_delete_t_base_get(Long var0, vx_req_account_channel_delete_t var2)

    native Unit vx_req_account_channel_delete_t_base_set(Long var0, vx_req_account_channel_delete_t var2, Long var3, vx_req_base_t var5)

    native String vx_req_account_channel_delete_t_channel_uri_get(Long var0, vx_req_account_channel_delete_t var2)

    native Unit vx_req_account_channel_delete_t_channel_uri_set(Long var0, vx_req_account_channel_delete_t var2, String var3)

    native Unit vx_req_account_channel_favorite_delete_create(Long var0)

    native String vx_req_account_channel_favorite_delete_t_account_handle_get(Long var0, vx_req_account_channel_favorite_delete_t var2)

    native Unit vx_req_account_channel_favorite_delete_t_account_handle_set(Long var0, vx_req_account_channel_favorite_delete_t var2, String var3)

    native Long vx_req_account_channel_favorite_delete_t_base_get(Long var0, vx_req_account_channel_favorite_delete_t var2)

    native Unit vx_req_account_channel_favorite_delete_t_base_set(Long var0, vx_req_account_channel_favorite_delete_t var2, Long var3, vx_req_base_t var5)

    native Int vx_req_account_channel_favorite_delete_t_channel_favorite_id_get(Long var0, vx_req_account_channel_favorite_delete_t var2)

    native Unit vx_req_account_channel_favorite_delete_t_channel_favorite_id_set(Long var0, vx_req_account_channel_favorite_delete_t var2, Int var3)

    native Unit vx_req_account_channel_favorite_group_delete_create(Long var0)

    native String vx_req_account_channel_favorite_group_delete_t_account_handle_get(Long var0, vx_req_account_channel_favorite_group_delete_t var2)

    native Unit vx_req_account_channel_favorite_group_delete_t_account_handle_set(Long var0, vx_req_account_channel_favorite_group_delete_t var2, String var3)

    native Long vx_req_account_channel_favorite_group_delete_t_base_get(Long var0, vx_req_account_channel_favorite_group_delete_t var2)

    native Unit vx_req_account_channel_favorite_group_delete_t_base_set(Long var0, vx_req_account_channel_favorite_group_delete_t var2, Long var3, vx_req_base_t var5)

    native Int vx_req_account_channel_favorite_group_delete_t_channel_favorite_group_id_get(Long var0, vx_req_account_channel_favorite_group_delete_t var2)

    native Unit vx_req_account_channel_favorite_group_delete_t_channel_favorite_group_id_set(Long var0, vx_req_account_channel_favorite_group_delete_t var2, Int var3)

    native Unit vx_req_account_channel_favorite_group_set_create(Long var0)

    native String vx_req_account_channel_favorite_group_set_t_account_handle_get(Long var0, vx_req_account_channel_favorite_group_set_t var2)

    native Unit vx_req_account_channel_favorite_group_set_t_account_handle_set(Long var0, vx_req_account_channel_favorite_group_set_t var2, String var3)

    native Long vx_req_account_channel_favorite_group_set_t_base_get(Long var0, vx_req_account_channel_favorite_group_set_t var2)

    native Unit vx_req_account_channel_favorite_group_set_t_base_set(Long var0, vx_req_account_channel_favorite_group_set_t var2, Long var3, vx_req_base_t var5)

    native String vx_req_account_channel_favorite_group_set_t_channel_favorite_group_data_get(Long var0, vx_req_account_channel_favorite_group_set_t var2)

    native Unit vx_req_account_channel_favorite_group_set_t_channel_favorite_group_data_set(Long var0, vx_req_account_channel_favorite_group_set_t var2, String var3)

    native Int vx_req_account_channel_favorite_group_set_t_channel_favorite_group_id_get(Long var0, vx_req_account_channel_favorite_group_set_t var2)

    native Unit vx_req_account_channel_favorite_group_set_t_channel_favorite_group_id_set(Long var0, vx_req_account_channel_favorite_group_set_t var2, Int var3)

    native String vx_req_account_channel_favorite_group_set_t_channel_favorite_group_name_get(Long var0, vx_req_account_channel_favorite_group_set_t var2)

    native Unit vx_req_account_channel_favorite_group_set_t_channel_favorite_group_name_set(Long var0, vx_req_account_channel_favorite_group_set_t var2, String var3)

    native Unit vx_req_account_channel_favorite_set_create(Long var0)

    native String vx_req_account_channel_favorite_set_t_account_handle_get(Long var0, vx_req_account_channel_favorite_set_t var2)

    native Unit vx_req_account_channel_favorite_set_t_account_handle_set(Long var0, vx_req_account_channel_favorite_set_t var2, String var3)

    native Long vx_req_account_channel_favorite_set_t_base_get(Long var0, vx_req_account_channel_favorite_set_t var2)

    native Unit vx_req_account_channel_favorite_set_t_base_set(Long var0, vx_req_account_channel_favorite_set_t var2, Long var3, vx_req_base_t var5)

    native String vx_req_account_channel_favorite_set_t_channel_favorite_data_get(Long var0, vx_req_account_channel_favorite_set_t var2)

    native Unit vx_req_account_channel_favorite_set_t_channel_favorite_data_set(Long var0, vx_req_account_channel_favorite_set_t var2, String var3)

    native Int vx_req_account_channel_favorite_set_t_channel_favorite_group_id_get(Long var0, vx_req_account_channel_favorite_set_t var2)

    native Unit vx_req_account_channel_favorite_set_t_channel_favorite_group_id_set(Long var0, vx_req_account_channel_favorite_set_t var2, Int var3)

    native Int vx_req_account_channel_favorite_set_t_channel_favorite_id_get(Long var0, vx_req_account_channel_favorite_set_t var2)

    native Unit vx_req_account_channel_favorite_set_t_channel_favorite_id_set(Long var0, vx_req_account_channel_favorite_set_t var2, Int var3)

    native String vx_req_account_channel_favorite_set_t_channel_favorite_label_get(Long var0, vx_req_account_channel_favorite_set_t var2)

    native Unit vx_req_account_channel_favorite_set_t_channel_favorite_label_set(Long var0, vx_req_account_channel_favorite_set_t var2, String var3)

    native String vx_req_account_channel_favorite_set_t_channel_favorite_uri_get(Long var0, vx_req_account_channel_favorite_set_t var2)

    native Unit vx_req_account_channel_favorite_set_t_channel_favorite_uri_set(Long var0, vx_req_account_channel_favorite_set_t var2, String var3)

    native Unit vx_req_account_channel_favorites_get_list_create(Long var0)

    native String vx_req_account_channel_favorites_get_list_t_account_handle_get(Long var0, vx_req_account_channel_favorites_get_list_t var2)

    native Unit vx_req_account_channel_favorites_get_list_t_account_handle_set(Long var0, vx_req_account_channel_favorites_get_list_t var2, String var3)

    native Long vx_req_account_channel_favorites_get_list_t_base_get(Long var0, vx_req_account_channel_favorites_get_list_t var2)

    native Unit vx_req_account_channel_favorites_get_list_t_base_set(Long var0, vx_req_account_channel_favorites_get_list_t var2, Long var3, vx_req_base_t var5)

    native Unit vx_req_account_channel_get_acl_create(Long var0)

    native String vx_req_account_channel_get_acl_t_account_handle_get(Long var0, vx_req_account_channel_get_acl_t var2)

    native Unit vx_req_account_channel_get_acl_t_account_handle_set(Long var0, vx_req_account_channel_get_acl_t var2, String var3)

    native Long vx_req_account_channel_get_acl_t_base_get(Long var0, vx_req_account_channel_get_acl_t var2)

    native Unit vx_req_account_channel_get_acl_t_base_set(Long var0, vx_req_account_channel_get_acl_t var2, Long var3, vx_req_base_t var5)

    native String vx_req_account_channel_get_acl_t_channel_uri_get(Long var0, vx_req_account_channel_get_acl_t var2)

    native Unit vx_req_account_channel_get_acl_t_channel_uri_set(Long var0, vx_req_account_channel_get_acl_t var2, String var3)

    native Unit vx_req_account_channel_get_info_create(Long var0)

    native String vx_req_account_channel_get_info_t_account_handle_get(Long var0, vx_req_account_channel_get_info_t var2)

    native Unit vx_req_account_channel_get_info_t_account_handle_set(Long var0, vx_req_account_channel_get_info_t var2, String var3)

    native Long vx_req_account_channel_get_info_t_base_get(Long var0, vx_req_account_channel_get_info_t var2)

    native Unit vx_req_account_channel_get_info_t_base_set(Long var0, vx_req_account_channel_get_info_t var2, Long var3, vx_req_base_t var5)

    native String vx_req_account_channel_get_info_t_channel_uri_get(Long var0, vx_req_account_channel_get_info_t var2)

    native Unit vx_req_account_channel_get_info_t_channel_uri_set(Long var0, vx_req_account_channel_get_info_t var2, String var3)

    native Unit vx_req_account_channel_get_moderators_create(Long var0)

    native String vx_req_account_channel_get_moderators_t_account_handle_get(Long var0, vx_req_account_channel_get_moderators_t var2)

    native Unit vx_req_account_channel_get_moderators_t_account_handle_set(Long var0, vx_req_account_channel_get_moderators_t var2, String var3)

    native Long vx_req_account_channel_get_moderators_t_base_get(Long var0, vx_req_account_channel_get_moderators_t var2)

    native Unit vx_req_account_channel_get_moderators_t_base_set(Long var0, vx_req_account_channel_get_moderators_t var2, Long var3, vx_req_base_t var5)

    native String vx_req_account_channel_get_moderators_t_channel_uri_get(Long var0, vx_req_account_channel_get_moderators_t var2)

    native Unit vx_req_account_channel_get_moderators_t_channel_uri_set(Long var0, vx_req_account_channel_get_moderators_t var2, String var3)

    native Unit vx_req_account_channel_get_participants_create(Long var0)

    native String vx_req_account_channel_get_participants_t_account_handle_get(Long var0, vx_req_account_channel_get_participants_t var2)

    native Unit vx_req_account_channel_get_participants_t_account_handle_set(Long var0, vx_req_account_channel_get_participants_t var2, String var3)

    native Long vx_req_account_channel_get_participants_t_base_get(Long var0, vx_req_account_channel_get_participants_t var2)

    native Unit vx_req_account_channel_get_participants_t_base_set(Long var0, vx_req_account_channel_get_participants_t var2, Long var3, vx_req_base_t var5)

    native String vx_req_account_channel_get_participants_t_channel_uri_get(Long var0, vx_req_account_channel_get_participants_t var2)

    native Unit vx_req_account_channel_get_participants_t_channel_uri_set(Long var0, vx_req_account_channel_get_participants_t var2, String var3)

    native Int vx_req_account_channel_get_participants_t_page_number_get(Long var0, vx_req_account_channel_get_participants_t var2)

    native Unit vx_req_account_channel_get_participants_t_page_number_set(Long var0, vx_req_account_channel_get_participants_t var2, Int var3)

    native Int vx_req_account_channel_get_participants_t_page_size_get(Long var0, vx_req_account_channel_get_participants_t var2)

    native Unit vx_req_account_channel_get_participants_t_page_size_set(Long var0, vx_req_account_channel_get_participants_t var2, Int var3)

    native Unit vx_req_account_channel_remove_acl_create(Long var0)

    native String vx_req_account_channel_remove_acl_t_account_handle_get(Long var0, vx_req_account_channel_remove_acl_t var2)

    native Unit vx_req_account_channel_remove_acl_t_account_handle_set(Long var0, vx_req_account_channel_remove_acl_t var2, String var3)

    native String vx_req_account_channel_remove_acl_t_acl_uri_get(Long var0, vx_req_account_channel_remove_acl_t var2)

    native Unit vx_req_account_channel_remove_acl_t_acl_uri_set(Long var0, vx_req_account_channel_remove_acl_t var2, String var3)

    native Long vx_req_account_channel_remove_acl_t_base_get(Long var0, vx_req_account_channel_remove_acl_t var2)

    native Unit vx_req_account_channel_remove_acl_t_base_set(Long var0, vx_req_account_channel_remove_acl_t var2, Long var3, vx_req_base_t var5)

    native String vx_req_account_channel_remove_acl_t_channel_uri_get(Long var0, vx_req_account_channel_remove_acl_t var2)

    native Unit vx_req_account_channel_remove_acl_t_channel_uri_set(Long var0, vx_req_account_channel_remove_acl_t var2, String var3)

    native Unit vx_req_account_channel_remove_moderator_create(Long var0)

    native String vx_req_account_channel_remove_moderator_t_account_handle_get(Long var0, vx_req_account_channel_remove_moderator_t var2)

    native Unit vx_req_account_channel_remove_moderator_t_account_handle_set(Long var0, vx_req_account_channel_remove_moderator_t var2, String var3)

    native Long vx_req_account_channel_remove_moderator_t_base_get(Long var0, vx_req_account_channel_remove_moderator_t var2)

    native Unit vx_req_account_channel_remove_moderator_t_base_set(Long var0, vx_req_account_channel_remove_moderator_t var2, Long var3, vx_req_base_t var5)

    native String vx_req_account_channel_remove_moderator_t_channel_name_get(Long var0, vx_req_account_channel_remove_moderator_t var2)

    native Unit vx_req_account_channel_remove_moderator_t_channel_name_set(Long var0, vx_req_account_channel_remove_moderator_t var2, String var3)

    native String vx_req_account_channel_remove_moderator_t_channel_uri_get(Long var0, vx_req_account_channel_remove_moderator_t var2)

    native Unit vx_req_account_channel_remove_moderator_t_channel_uri_set(Long var0, vx_req_account_channel_remove_moderator_t var2, String var3)

    native String vx_req_account_channel_remove_moderator_t_moderator_uri_get(Long var0, vx_req_account_channel_remove_moderator_t var2)

    native Unit vx_req_account_channel_remove_moderator_t_moderator_uri_set(Long var0, vx_req_account_channel_remove_moderator_t var2, String var3)

    native Unit vx_req_account_channel_search_create(Long var0)

    native String vx_req_account_channel_search_t_account_handle_get(Long var0, vx_req_account_channel_search_t var2)

    native Unit vx_req_account_channel_search_t_account_handle_set(Long var0, vx_req_account_channel_search_t var2, String var3)

    native Long vx_req_account_channel_search_t_base_get(Long var0, vx_req_account_channel_search_t var2)

    native Unit vx_req_account_channel_search_t_base_set(Long var0, vx_req_account_channel_search_t var2, Long var3, vx_req_base_t var5)

    native Int vx_req_account_channel_search_t_begins_with_get(Long var0, vx_req_account_channel_search_t var2)

    native Unit vx_req_account_channel_search_t_begins_with_set(Long var0, vx_req_account_channel_search_t var2, Int var3)

    native Int vx_req_account_channel_search_t_channel_active_get(Long var0, vx_req_account_channel_search_t var2)

    native Unit vx_req_account_channel_search_t_channel_active_set(Long var0, vx_req_account_channel_search_t var2, Int var3)

    native String vx_req_account_channel_search_t_channel_description_get(Long var0, vx_req_account_channel_search_t var2)

    native Unit vx_req_account_channel_search_t_channel_description_set(Long var0, vx_req_account_channel_search_t var2, String var3)

    native String vx_req_account_channel_search_t_channel_name_get(Long var0, vx_req_account_channel_search_t var2)

    native Unit vx_req_account_channel_search_t_channel_name_set(Long var0, vx_req_account_channel_search_t var2, String var3)

    native Int vx_req_account_channel_search_t_channel_type_get(Long var0, vx_req_account_channel_search_t var2)

    native Unit vx_req_account_channel_search_t_channel_type_set(Long var0, vx_req_account_channel_search_t var2, Int var3)

    native Int vx_req_account_channel_search_t_moderation_type_get(Long var0, vx_req_account_channel_search_t var2)

    native Unit vx_req_account_channel_search_t_moderation_type_set(Long var0, vx_req_account_channel_search_t var2, Int var3)

    native Int vx_req_account_channel_search_t_page_number_get(Long var0, vx_req_account_channel_search_t var2)

    native Unit vx_req_account_channel_search_t_page_number_set(Long var0, vx_req_account_channel_search_t var2, Int var3)

    native Int vx_req_account_channel_search_t_page_size_get(Long var0, vx_req_account_channel_search_t var2)

    native Unit vx_req_account_channel_search_t_page_size_set(Long var0, vx_req_account_channel_search_t var2, Int var3)

    native Unit vx_req_account_channel_update_create(Long var0)

    native String vx_req_account_channel_update_t_account_handle_get(Long var0, vx_req_account_channel_update_t var2)

    native Unit vx_req_account_channel_update_t_account_handle_set(Long var0, vx_req_account_channel_update_t var2, String var3)

    native Long vx_req_account_channel_update_t_base_get(Long var0, vx_req_account_channel_update_t var2)

    native Unit vx_req_account_channel_update_t_base_set(Long var0, vx_req_account_channel_update_t var2, Long var3, vx_req_base_t var5)

    native Int vx_req_account_channel_update_t_capacity_get(Long var0, vx_req_account_channel_update_t var2)

    native Unit vx_req_account_channel_update_t_capacity_set(Long var0, vx_req_account_channel_update_t var2, Int var3)

    native String vx_req_account_channel_update_t_channel_desc_get(Long var0, vx_req_account_channel_update_t var2)

    native Unit vx_req_account_channel_update_t_channel_desc_set(Long var0, vx_req_account_channel_update_t var2, String var3)

    native Int vx_req_account_channel_update_t_channel_mode_get(Long var0, vx_req_account_channel_update_t var2)

    native Unit vx_req_account_channel_update_t_channel_mode_set(Long var0, vx_req_account_channel_update_t var2, Int var3)

    native String vx_req_account_channel_update_t_channel_name_get(Long var0, vx_req_account_channel_update_t var2)

    native Unit vx_req_account_channel_update_t_channel_name_set(Long var0, vx_req_account_channel_update_t var2, String var3)

    native String vx_req_account_channel_update_t_channel_uri_get(Long var0, vx_req_account_channel_update_t var2)

    native Unit vx_req_account_channel_update_t_channel_uri_set(Long var0, vx_req_account_channel_update_t var2, String var3)

    native Int vx_req_account_channel_update_t_clamping_dist_get(Long var0, vx_req_account_channel_update_t var2)

    native Unit vx_req_account_channel_update_t_clamping_dist_set(Long var0, vx_req_account_channel_update_t var2, Int var3)

    native Int vx_req_account_channel_update_t_dist_model_get(Long var0, vx_req_account_channel_update_t var2)

    native Unit vx_req_account_channel_update_t_dist_model_set(Long var0, vx_req_account_channel_update_t var2, Int var3)

    native Int vx_req_account_channel_update_t_encrypt_audio_get(Long var0, vx_req_account_channel_update_t var2)

    native Unit vx_req_account_channel_update_t_encrypt_audio_set(Long var0, vx_req_account_channel_update_t var2, Int var3)

    native Double vx_req_account_channel_update_t_max_gain_get(Long var0, vx_req_account_channel_update_t var2)

    native Unit vx_req_account_channel_update_t_max_gain_set(Long var0, vx_req_account_channel_update_t var2, Double var3)

    native Int vx_req_account_channel_update_t_max_participants_get(Long var0, vx_req_account_channel_update_t var2)

    native Unit vx_req_account_channel_update_t_max_participants_set(Long var0, vx_req_account_channel_update_t var2, Int var3)

    native Int vx_req_account_channel_update_t_max_range_get(Long var0, vx_req_account_channel_update_t var2)

    native Unit vx_req_account_channel_update_t_max_range_set(Long var0, vx_req_account_channel_update_t var2, Int var3)

    native String vx_req_account_channel_update_t_protected_password_get(Long var0, vx_req_account_channel_update_t var2)

    native Unit vx_req_account_channel_update_t_protected_password_set(Long var0, vx_req_account_channel_update_t var2, String var3)

    native Double vx_req_account_channel_update_t_roll_off_get(Long var0, vx_req_account_channel_update_t var2)

    native Unit vx_req_account_channel_update_t_roll_off_set(Long var0, vx_req_account_channel_update_t var2, Double var3)

    native Int vx_req_account_channel_update_t_set_persistent_get(Long var0, vx_req_account_channel_update_t var2)

    native Unit vx_req_account_channel_update_t_set_persistent_set(Long var0, vx_req_account_channel_update_t var2, Int var3)

    native Int vx_req_account_channel_update_t_set_protected_get(Long var0, vx_req_account_channel_update_t var2)

    native Unit vx_req_account_channel_update_t_set_protected_set(Long var0, vx_req_account_channel_update_t var2, Int var3)

    native Unit vx_req_account_create_auto_accept_rule_create(Long var0)

    native String vx_req_account_create_auto_accept_rule_t_account_handle_get(Long var0, vx_req_account_create_auto_accept_rule_t var2)

    native Unit vx_req_account_create_auto_accept_rule_t_account_handle_set(Long var0, vx_req_account_create_auto_accept_rule_t var2, String var3)

    native String vx_req_account_create_auto_accept_rule_t_auto_accept_mask_get(Long var0, vx_req_account_create_auto_accept_rule_t var2)

    native Unit vx_req_account_create_auto_accept_rule_t_auto_accept_mask_set(Long var0, vx_req_account_create_auto_accept_rule_t var2, String var3)

    native String vx_req_account_create_auto_accept_rule_t_auto_accept_nickname_get(Long var0, vx_req_account_create_auto_accept_rule_t var2)

    native Unit vx_req_account_create_auto_accept_rule_t_auto_accept_nickname_set(Long var0, vx_req_account_create_auto_accept_rule_t var2, String var3)

    native Int vx_req_account_create_auto_accept_rule_t_auto_add_as_buddy_get(Long var0, vx_req_account_create_auto_accept_rule_t var2)

    native Unit vx_req_account_create_auto_accept_rule_t_auto_add_as_buddy_set(Long var0, vx_req_account_create_auto_accept_rule_t var2, Int var3)

    native Long vx_req_account_create_auto_accept_rule_t_base_get(Long var0, vx_req_account_create_auto_accept_rule_t var2)

    native Unit vx_req_account_create_auto_accept_rule_t_base_set(Long var0, vx_req_account_create_auto_accept_rule_t var2, Long var3, vx_req_base_t var5)

    native Unit vx_req_account_create_block_rule_create(Long var0)

    native String vx_req_account_create_block_rule_t_account_handle_get(Long var0, vx_req_account_create_block_rule_t var2)

    native Unit vx_req_account_create_block_rule_t_account_handle_set(Long var0, vx_req_account_create_block_rule_t var2, String var3)

    native Long vx_req_account_create_block_rule_t_base_get(Long var0, vx_req_account_create_block_rule_t var2)

    native Unit vx_req_account_create_block_rule_t_base_set(Long var0, vx_req_account_create_block_rule_t var2, Long var3, vx_req_base_t var5)

    native String vx_req_account_create_block_rule_t_block_mask_get(Long var0, vx_req_account_create_block_rule_t var2)

    native Unit vx_req_account_create_block_rule_t_block_mask_set(Long var0, vx_req_account_create_block_rule_t var2, String var3)

    native Int vx_req_account_create_block_rule_t_presence_only_get(Long var0, vx_req_account_create_block_rule_t var2)

    native Unit vx_req_account_create_block_rule_t_presence_only_set(Long var0, vx_req_account_create_block_rule_t var2, Int var3)

    native Unit vx_req_account_delete_auto_accept_rule_create(Long var0)

    native String vx_req_account_delete_auto_accept_rule_t_account_handle_get(Long var0, vx_req_account_delete_auto_accept_rule_t var2)

    native Unit vx_req_account_delete_auto_accept_rule_t_account_handle_set(Long var0, vx_req_account_delete_auto_accept_rule_t var2, String var3)

    native String vx_req_account_delete_auto_accept_rule_t_auto_accept_mask_get(Long var0, vx_req_account_delete_auto_accept_rule_t var2)

    native Unit vx_req_account_delete_auto_accept_rule_t_auto_accept_mask_set(Long var0, vx_req_account_delete_auto_accept_rule_t var2, String var3)

    native Long vx_req_account_delete_auto_accept_rule_t_base_get(Long var0, vx_req_account_delete_auto_accept_rule_t var2)

    native Unit vx_req_account_delete_auto_accept_rule_t_base_set(Long var0, vx_req_account_delete_auto_accept_rule_t var2, Long var3, vx_req_base_t var5)

    native Unit vx_req_account_delete_block_rule_create(Long var0)

    native String vx_req_account_delete_block_rule_t_account_handle_get(Long var0, vx_req_account_delete_block_rule_t var2)

    native Unit vx_req_account_delete_block_rule_t_account_handle_set(Long var0, vx_req_account_delete_block_rule_t var2, String var3)

    native Long vx_req_account_delete_block_rule_t_base_get(Long var0, vx_req_account_delete_block_rule_t var2)

    native Unit vx_req_account_delete_block_rule_t_base_set(Long var0, vx_req_account_delete_block_rule_t var2, Long var3, vx_req_base_t var5)

    native String vx_req_account_delete_block_rule_t_block_mask_get(Long var0, vx_req_account_delete_block_rule_t var2)

    native Unit vx_req_account_delete_block_rule_t_block_mask_set(Long var0, vx_req_account_delete_block_rule_t var2, String var3)

    native Unit vx_req_account_get_account_create(Long var0)

    native String vx_req_account_get_account_t_account_handle_get(Long var0, vx_req_account_get_account_t var2)

    native Unit vx_req_account_get_account_t_account_handle_set(Long var0, vx_req_account_get_account_t var2, String var3)

    native Long vx_req_account_get_account_t_base_get(Long var0, vx_req_account_get_account_t var2)

    native Unit vx_req_account_get_account_t_base_set(Long var0, vx_req_account_get_account_t var2, Long var3, vx_req_base_t var5)

    native String vx_req_account_get_account_t_uri_get(Long var0, vx_req_account_get_account_t var2)

    native Unit vx_req_account_get_account_t_uri_set(Long var0, vx_req_account_get_account_t var2, String var3)

    native Unit vx_req_account_get_session_fonts_create(Long var0)

    native String vx_req_account_get_session_fonts_t_account_handle_get(Long var0, vx_req_account_get_session_fonts_t var2)

    native Unit vx_req_account_get_session_fonts_t_account_handle_set(Long var0, vx_req_account_get_session_fonts_t var2, String var3)

    native Long vx_req_account_get_session_fonts_t_base_get(Long var0, vx_req_account_get_session_fonts_t var2)

    native Unit vx_req_account_get_session_fonts_t_base_set(Long var0, vx_req_account_get_session_fonts_t var2, Long var3, vx_req_base_t var5)

    native Unit vx_req_account_get_template_fonts_create(Long var0)

    native String vx_req_account_get_template_fonts_t_account_handle_get(Long var0, vx_req_account_get_template_fonts_t var2)

    native Unit vx_req_account_get_template_fonts_t_account_handle_set(Long var0, vx_req_account_get_template_fonts_t var2, String var3)

    native Long vx_req_account_get_template_fonts_t_base_get(Long var0, vx_req_account_get_template_fonts_t var2)

    native Unit vx_req_account_get_template_fonts_t_base_set(Long var0, vx_req_account_get_template_fonts_t var2, Long var3, vx_req_base_t var5)

    native Unit vx_req_account_list_auto_accept_rules_create(Long var0)

    native String vx_req_account_list_auto_accept_rules_t_account_handle_get(Long var0, vx_req_account_list_auto_accept_rules_t var2)

    native Unit vx_req_account_list_auto_accept_rules_t_account_handle_set(Long var0, vx_req_account_list_auto_accept_rules_t var2, String var3)

    native Long vx_req_account_list_auto_accept_rules_t_base_get(Long var0, vx_req_account_list_auto_accept_rules_t var2)

    native Unit vx_req_account_list_auto_accept_rules_t_base_set(Long var0, vx_req_account_list_auto_accept_rules_t var2, Long var3, vx_req_base_t var5)

    native Unit vx_req_account_list_block_rules_create(Long var0)

    native String vx_req_account_list_block_rules_t_account_handle_get(Long var0, vx_req_account_list_block_rules_t var2)

    native Unit vx_req_account_list_block_rules_t_account_handle_set(Long var0, vx_req_account_list_block_rules_t var2, String var3)

    native Long vx_req_account_list_block_rules_t_base_get(Long var0, vx_req_account_list_block_rules_t var2)

    native Unit vx_req_account_list_block_rules_t_base_set(Long var0, vx_req_account_list_block_rules_t var2, Long var3, vx_req_base_t var5)

    native Unit vx_req_account_list_buddies_and_groups_create(Long var0)

    native String vx_req_account_list_buddies_and_groups_t_account_handle_get(Long var0, vx_req_account_list_buddies_and_groups_t var2)

    native Unit vx_req_account_list_buddies_and_groups_t_account_handle_set(Long var0, vx_req_account_list_buddies_and_groups_t var2, String var3)

    native Long vx_req_account_list_buddies_and_groups_t_base_get(Long var0, vx_req_account_list_buddies_and_groups_t var2)

    native Unit vx_req_account_list_buddies_and_groups_t_base_set(Long var0, vx_req_account_list_buddies_and_groups_t var2, Long var3, vx_req_base_t var5)

    native Unit vx_req_account_login_create(Long var0)

    native String vx_req_account_login_t_acct_mgmt_server_get(Long var0, vx_req_account_login_t var2)

    native Unit vx_req_account_login_t_acct_mgmt_server_set(Long var0, vx_req_account_login_t var2, String var3)

    native String vx_req_account_login_t_acct_name_get(Long var0, vx_req_account_login_t var2)

    native Unit vx_req_account_login_t_acct_name_set(Long var0, vx_req_account_login_t var2, String var3)

    native String vx_req_account_login_t_acct_password_get(Long var0, vx_req_account_login_t var2)

    native Unit vx_req_account_login_t_acct_password_set(Long var0, vx_req_account_login_t var2, String var3)

    native Int vx_req_account_login_t_answer_mode_get(Long var0, vx_req_account_login_t var2)

    native Unit vx_req_account_login_t_answer_mode_set(Long var0, vx_req_account_login_t var2, Int var3)

    native String vx_req_account_login_t_application_override_get(Long var0, vx_req_account_login_t var2)

    native Unit vx_req_account_login_t_application_override_set(Long var0, vx_req_account_login_t var2, String var3)

    native String vx_req_account_login_t_application_token_get(Long var0, vx_req_account_login_t var2)

    native Unit vx_req_account_login_t_application_token_set(Long var0, vx_req_account_login_t var2, String var3)

    native Int vx_req_account_login_t_autopost_crash_dumps_get(Long var0, vx_req_account_login_t var2)

    native Unit vx_req_account_login_t_autopost_crash_dumps_set(Long var0, vx_req_account_login_t var2, Int var3)

    native Long vx_req_account_login_t_base_get(Long var0, vx_req_account_login_t var2)

    native Unit vx_req_account_login_t_base_set(Long var0, vx_req_account_login_t var2, Long var3, vx_req_base_t var5)

    native Int vx_req_account_login_t_buddy_management_mode_get(Long var0, vx_req_account_login_t var2)

    native Unit vx_req_account_login_t_buddy_management_mode_set(Long var0, vx_req_account_login_t var2, Int var3)

    native String vx_req_account_login_t_connector_handle_get(Long var0, vx_req_account_login_t var2)

    native Unit vx_req_account_login_t_connector_handle_set(Long var0, vx_req_account_login_t var2, String var3)

    native Int vx_req_account_login_t_enable_buddies_and_presence_get(Long var0, vx_req_account_login_t var2)

    native Unit vx_req_account_login_t_enable_buddies_and_presence_set(Long var0, vx_req_account_login_t var2, Int var3)

    native Int vx_req_account_login_t_enable_client_ringback_get(Long var0, vx_req_account_login_t var2)

    native Unit vx_req_account_login_t_enable_client_ringback_set(Long var0, vx_req_account_login_t var2, Int var3)

    native Int vx_req_account_login_t_enable_text_get(Long var0, vx_req_account_login_t var2)

    native Unit vx_req_account_login_t_enable_text_set(Long var0, vx_req_account_login_t var2, Int var3)

    native Int vx_req_account_login_t_participant_property_frequency_get(Long var0, vx_req_account_login_t var2)

    native Unit vx_req_account_login_t_participant_property_frequency_set(Long var0, vx_req_account_login_t var2, Int var3)

    native Unit vx_req_account_logout_create(Long var0)

    native String vx_req_account_logout_t_account_handle_get(Long var0, vx_req_account_logout_t var2)

    native Unit vx_req_account_logout_t_account_handle_set(Long var0, vx_req_account_logout_t var2, String var3)

    native Long vx_req_account_logout_t_base_get(Long var0, vx_req_account_logout_t var2)

    native Unit vx_req_account_logout_t_base_set(Long var0, vx_req_account_logout_t var2, Long var3, vx_req_base_t var5)

    native String vx_req_account_logout_t_logout_reason_get(Long var0, vx_req_account_logout_t var2)

    native Unit vx_req_account_logout_t_logout_reason_set(Long var0, vx_req_account_logout_t var2, String var3)

    native Unit vx_req_account_post_crash_dump_create(Long var0)

    native String vx_req_account_post_crash_dump_t_account_handle_get(Long var0, vx_req_account_post_crash_dump_t var2)

    native Unit vx_req_account_post_crash_dump_t_account_handle_set(Long var0, vx_req_account_post_crash_dump_t var2, String var3)

    native Long vx_req_account_post_crash_dump_t_base_get(Long var0, vx_req_account_post_crash_dump_t var2)

    native Unit vx_req_account_post_crash_dump_t_base_set(Long var0, vx_req_account_post_crash_dump_t var2, Long var3, vx_req_base_t var5)

    native String vx_req_account_post_crash_dump_t_crash_dump_get(Long var0, vx_req_account_post_crash_dump_t var2)

    native Unit vx_req_account_post_crash_dump_t_crash_dump_set(Long var0, vx_req_account_post_crash_dump_t var2, String var3)

    native Unit vx_req_account_send_message_create(Long var0)

    native String vx_req_account_send_message_t_account_handle_get(Long var0, vx_req_account_send_message_t var2)

    native Unit vx_req_account_send_message_t_account_handle_set(Long var0, vx_req_account_send_message_t var2, String var3)

    native String vx_req_account_send_message_t_alias_username_get(Long var0, vx_req_account_send_message_t var2)

    native Unit vx_req_account_send_message_t_alias_username_set(Long var0, vx_req_account_send_message_t var2, String var3)

    native Long vx_req_account_send_message_t_base_get(Long var0, vx_req_account_send_message_t var2)

    native Unit vx_req_account_send_message_t_base_set(Long var0, vx_req_account_send_message_t var2, Long var3, vx_req_base_t var5)

    native String vx_req_account_send_message_t_message_body_get(Long var0, vx_req_account_send_message_t var2)

    native Unit vx_req_account_send_message_t_message_body_set(Long var0, vx_req_account_send_message_t var2, String var3)

    native String vx_req_account_send_message_t_message_header_get(Long var0, vx_req_account_send_message_t var2)

    native Unit vx_req_account_send_message_t_message_header_set(Long var0, vx_req_account_send_message_t var2, String var3)

    native String vx_req_account_send_message_t_user_uri_get(Long var0, vx_req_account_send_message_t var2)

    native Unit vx_req_account_send_message_t_user_uri_set(Long var0, vx_req_account_send_message_t var2, String var3)

    native Unit vx_req_account_send_sms_create(Long var0)

    native String vx_req_account_send_sms_t_account_handle_get(Long var0, vx_req_account_send_sms_t var2)

    native Unit vx_req_account_send_sms_t_account_handle_set(Long var0, vx_req_account_send_sms_t var2, String var3)

    native Long vx_req_account_send_sms_t_base_get(Long var0, vx_req_account_send_sms_t var2)

    native Unit vx_req_account_send_sms_t_base_set(Long var0, vx_req_account_send_sms_t var2, Long var3, vx_req_base_t var5)

    native String vx_req_account_send_sms_t_content_get(Long var0, vx_req_account_send_sms_t var2)

    native Unit vx_req_account_send_sms_t_content_set(Long var0, vx_req_account_send_sms_t var2, String var3)

    native String vx_req_account_send_sms_t_recipient_uri_get(Long var0, vx_req_account_send_sms_t var2)

    native Unit vx_req_account_send_sms_t_recipient_uri_set(Long var0, vx_req_account_send_sms_t var2, String var3)

    native Unit vx_req_account_send_subscription_reply_create(Long var0)

    native String vx_req_account_send_subscription_reply_t_account_handle_get(Long var0, vx_req_account_send_subscription_reply_t var2)

    native Unit vx_req_account_send_subscription_reply_t_account_handle_set(Long var0, vx_req_account_send_subscription_reply_t var2, String var3)

    native Int vx_req_account_send_subscription_reply_t_auto_accept_get(Long var0, vx_req_account_send_subscription_reply_t var2)

    native Unit vx_req_account_send_subscription_reply_t_auto_accept_set(Long var0, vx_req_account_send_subscription_reply_t var2, Int var3)

    native Long vx_req_account_send_subscription_reply_t_base_get(Long var0, vx_req_account_send_subscription_reply_t var2)

    native Unit vx_req_account_send_subscription_reply_t_base_set(Long var0, vx_req_account_send_subscription_reply_t var2, Long var3, vx_req_base_t var5)

    native String vx_req_account_send_subscription_reply_t_buddy_uri_get(Long var0, vx_req_account_send_subscription_reply_t var2)

    native Unit vx_req_account_send_subscription_reply_t_buddy_uri_set(Long var0, vx_req_account_send_subscription_reply_t var2, String var3)

    native Int vx_req_account_send_subscription_reply_t_rule_type_get(Long var0, vx_req_account_send_subscription_reply_t var2)

    native Unit vx_req_account_send_subscription_reply_t_rule_type_set(Long var0, vx_req_account_send_subscription_reply_t var2, Int var3)

    native String vx_req_account_send_subscription_reply_t_subscription_handle_get(Long var0, vx_req_account_send_subscription_reply_t var2)

    native Unit vx_req_account_send_subscription_reply_t_subscription_handle_set(Long var0, vx_req_account_send_subscription_reply_t var2, String var3)

    native Unit vx_req_account_send_user_app_data_create(Long var0)

    native String vx_req_account_send_user_app_data_t_account_handle_get(Long var0, vx_req_account_send_user_app_data_t var2)

    native Unit vx_req_account_send_user_app_data_t_account_handle_set(Long var0, vx_req_account_send_user_app_data_t var2, String var3)

    native Long vx_req_account_send_user_app_data_t_base_get(Long var0, vx_req_account_send_user_app_data_t var2)

    native Unit vx_req_account_send_user_app_data_t_base_set(Long var0, vx_req_account_send_user_app_data_t var2, Long var3, vx_req_base_t var5)

    native String vx_req_account_send_user_app_data_t_content_get(Long var0, vx_req_account_send_user_app_data_t var2)

    native Unit vx_req_account_send_user_app_data_t_content_set(Long var0, vx_req_account_send_user_app_data_t var2, String var3)

    native String vx_req_account_send_user_app_data_t_content_type_get(Long var0, vx_req_account_send_user_app_data_t var2)

    native Unit vx_req_account_send_user_app_data_t_content_type_set(Long var0, vx_req_account_send_user_app_data_t var2, String var3)

    native String vx_req_account_send_user_app_data_t_to_uri_get(Long var0, vx_req_account_send_user_app_data_t var2)

    native Unit vx_req_account_send_user_app_data_t_to_uri_set(Long var0, vx_req_account_send_user_app_data_t var2, String var3)

    native Unit vx_req_account_set_login_properties_create(Long var0)

    native String vx_req_account_set_login_properties_t_account_handle_get(Long var0, vx_req_account_set_login_properties_t var2)

    native Unit vx_req_account_set_login_properties_t_account_handle_set(Long var0, vx_req_account_set_login_properties_t var2, String var3)

    native Int vx_req_account_set_login_properties_t_answer_mode_get(Long var0, vx_req_account_set_login_properties_t var2)

    native Unit vx_req_account_set_login_properties_t_answer_mode_set(Long var0, vx_req_account_set_login_properties_t var2, Int var3)

    native Long vx_req_account_set_login_properties_t_base_get(Long var0, vx_req_account_set_login_properties_t var2)

    native Unit vx_req_account_set_login_properties_t_base_set(Long var0, vx_req_account_set_login_properties_t var2, Long var3, vx_req_base_t var5)

    native Int vx_req_account_set_login_properties_t_participant_property_frequency_get(Long var0, vx_req_account_set_login_properties_t var2)

    native Unit vx_req_account_set_login_properties_t_participant_property_frequency_set(Long var0, vx_req_account_set_login_properties_t var2, Int var3)

    native Unit vx_req_account_set_presence_create(Long var0)

    native String vx_req_account_set_presence_t_account_handle_get(Long var0, vx_req_account_set_presence_t var2)

    native Unit vx_req_account_set_presence_t_account_handle_set(Long var0, vx_req_account_set_presence_t var2, String var3)

    native Long vx_req_account_set_presence_t_base_get(Long var0, vx_req_account_set_presence_t var2)

    native Unit vx_req_account_set_presence_t_base_set(Long var0, vx_req_account_set_presence_t var2, Long var3, vx_req_base_t var5)

    native String vx_req_account_set_presence_t_custom_message_get(Long var0, vx_req_account_set_presence_t var2)

    native Unit vx_req_account_set_presence_t_custom_message_set(Long var0, vx_req_account_set_presence_t var2, String var3)

    native Int vx_req_account_set_presence_t_presence_get(Long var0, vx_req_account_set_presence_t var2)

    native Unit vx_req_account_set_presence_t_presence_set(Long var0, vx_req_account_set_presence_t var2, Int var3)

    native Unit vx_req_account_update_account_create(Long var0)

    native String vx_req_account_update_account_t_account_handle_get(Long var0, vx_req_account_update_account_t var2)

    native Unit vx_req_account_update_account_t_account_handle_set(Long var0, vx_req_account_update_account_t var2, String var3)

    native Long vx_req_account_update_account_t_base_get(Long var0, vx_req_account_update_account_t var2)

    native Unit vx_req_account_update_account_t_base_set(Long var0, vx_req_account_update_account_t var2, Long var3, vx_req_base_t var5)

    native String vx_req_account_update_account_t_displayname_get(Long var0, vx_req_account_update_account_t var2)

    native Unit vx_req_account_update_account_t_displayname_set(Long var0, vx_req_account_update_account_t var2, String var3)

    native Unit vx_req_account_web_call_create(Long var0)

    native String vx_req_account_web_call_t_account_handle_get(Long var0, vx_req_account_web_call_t var2)

    native Unit vx_req_account_web_call_t_account_handle_set(Long var0, vx_req_account_web_call_t var2, String var3)

    native Long vx_req_account_web_call_t_base_get(Long var0, vx_req_account_web_call_t var2)

    native Unit vx_req_account_web_call_t_base_set(Long var0, vx_req_account_web_call_t var2, Long var3, vx_req_base_t var5)

    native Int vx_req_account_web_call_t_parameter_count_get(Long var0, vx_req_account_web_call_t var2)

    native Unit vx_req_account_web_call_t_parameter_count_set(Long var0, vx_req_account_web_call_t var2, Int var3)

    native Long vx_req_account_web_call_t_parameters_get(Long var0, vx_req_account_web_call_t var2)

    native Unit vx_req_account_web_call_t_parameters_set(Long var0, vx_req_account_web_call_t var2, Long var3)

    native String vx_req_account_web_call_t_relative_path_get(Long var0, vx_req_account_web_call_t var2)

    native Unit vx_req_account_web_call_t_relative_path_set(Long var0, vx_req_account_web_call_t var2, String var3)

    native Unit vx_req_aux_capture_audio_start_create(Long var0)

    native Long vx_req_aux_capture_audio_start_t_base_get(Long var0, vx_req_aux_capture_audio_start_t var2)

    native Unit vx_req_aux_capture_audio_start_t_base_set(Long var0, vx_req_aux_capture_audio_start_t var2, Long var3, vx_req_base_t var5)

    native Int vx_req_aux_capture_audio_start_t_duration_get(Long var0, vx_req_aux_capture_audio_start_t var2)

    native Unit vx_req_aux_capture_audio_start_t_duration_set(Long var0, vx_req_aux_capture_audio_start_t var2, Int var3)

    native Unit vx_req_aux_capture_audio_stop_create(Long var0)

    native Long vx_req_aux_capture_audio_stop_t_base_get(Long var0, vx_req_aux_capture_audio_stop_t var2)

    native Unit vx_req_aux_capture_audio_stop_t_base_set(Long var0, vx_req_aux_capture_audio_stop_t var2, Long var3, vx_req_base_t var5)

    native Unit vx_req_aux_connectivity_info_create(Long var0)

    native String vx_req_aux_connectivity_info_t_acct_mgmt_server_get(Long var0, vx_req_aux_connectivity_info_t var2)

    native Unit vx_req_aux_connectivity_info_t_acct_mgmt_server_set(Long var0, vx_req_aux_connectivity_info_t var2, String var3)

    native Long vx_req_aux_connectivity_info_t_base_get(Long var0, vx_req_aux_connectivity_info_t var2)

    native Unit vx_req_aux_connectivity_info_t_base_set(Long var0, vx_req_aux_connectivity_info_t var2, Long var3, vx_req_base_t var5)

    native Int vx_req_aux_connectivity_info_t_echo_port_get(Long var0, vx_req_aux_connectivity_info_t var2)

    native Unit vx_req_aux_connectivity_info_t_echo_port_set(Long var0, vx_req_aux_connectivity_info_t var2, Int var3)

    native String vx_req_aux_connectivity_info_t_echo_server_get(Long var0, vx_req_aux_connectivity_info_t var2)

    native Unit vx_req_aux_connectivity_info_t_echo_server_set(Long var0, vx_req_aux_connectivity_info_t var2, String var3)

    native String vx_req_aux_connectivity_info_t_stun_server_get(Long var0, vx_req_aux_connectivity_info_t var2)

    native Unit vx_req_aux_connectivity_info_t_stun_server_set(Long var0, vx_req_aux_connectivity_info_t var2, String var3)

    native Int vx_req_aux_connectivity_info_t_timeout_get(Long var0, vx_req_aux_connectivity_info_t var2)

    native Unit vx_req_aux_connectivity_info_t_timeout_set(Long var0, vx_req_aux_connectivity_info_t var2, Int var3)

    native String vx_req_aux_connectivity_info_t_well_known_ip_get(Long var0, vx_req_aux_connectivity_info_t var2)

    native Unit vx_req_aux_connectivity_info_t_well_known_ip_set(Long var0, vx_req_aux_connectivity_info_t var2, String var3)

    native Unit vx_req_aux_create_account_create(Long var0)

    native String vx_req_aux_create_account_t_age_get(Long var0, vx_req_aux_create_account_t var2)

    native Unit vx_req_aux_create_account_t_age_set(Long var0, vx_req_aux_create_account_t var2, String var3)

    native Long vx_req_aux_create_account_t_base_get(Long var0, vx_req_aux_create_account_t var2)

    native Unit vx_req_aux_create_account_t_base_set(Long var0, vx_req_aux_create_account_t var2, Long var3, vx_req_base_t var5)

    native Long vx_req_aux_create_account_t_credentials_get(Long var0, vx_req_aux_create_account_t var2)

    native Unit vx_req_aux_create_account_t_credentials_set(Long var0, vx_req_aux_create_account_t var2, Long var3, vx_generic_credentials var5)

    native String vx_req_aux_create_account_t_displayname_get(Long var0, vx_req_aux_create_account_t var2)

    native Unit vx_req_aux_create_account_t_displayname_set(Long var0, vx_req_aux_create_account_t var2, String var3)

    native String vx_req_aux_create_account_t_email_get(Long var0, vx_req_aux_create_account_t var2)

    native Unit vx_req_aux_create_account_t_email_set(Long var0, vx_req_aux_create_account_t var2, String var3)

    native String vx_req_aux_create_account_t_ext_id_get(Long var0, vx_req_aux_create_account_t var2)

    native Unit vx_req_aux_create_account_t_ext_id_set(Long var0, vx_req_aux_create_account_t var2, String var3)

    native String vx_req_aux_create_account_t_ext_profile_get(Long var0, vx_req_aux_create_account_t var2)

    native Unit vx_req_aux_create_account_t_ext_profile_set(Long var0, vx_req_aux_create_account_t var2, String var3)

    native String vx_req_aux_create_account_t_firstname_get(Long var0, vx_req_aux_create_account_t var2)

    native Unit vx_req_aux_create_account_t_firstname_set(Long var0, vx_req_aux_create_account_t var2, String var3)

    native String vx_req_aux_create_account_t_gender_get(Long var0, vx_req_aux_create_account_t var2)

    native Unit vx_req_aux_create_account_t_gender_set(Long var0, vx_req_aux_create_account_t var2, String var3)

    native String vx_req_aux_create_account_t_lang_get(Long var0, vx_req_aux_create_account_t var2)

    native Unit vx_req_aux_create_account_t_lang_set(Long var0, vx_req_aux_create_account_t var2, String var3)

    native String vx_req_aux_create_account_t_lastname_get(Long var0, vx_req_aux_create_account_t var2)

    native Unit vx_req_aux_create_account_t_lastname_set(Long var0, vx_req_aux_create_account_t var2, String var3)

    native String vx_req_aux_create_account_t_number_get(Long var0, vx_req_aux_create_account_t var2)

    native Unit vx_req_aux_create_account_t_number_set(Long var0, vx_req_aux_create_account_t var2, String var3)

    native String vx_req_aux_create_account_t_password_get(Long var0, vx_req_aux_create_account_t var2)

    native Unit vx_req_aux_create_account_t_password_set(Long var0, vx_req_aux_create_account_t var2, String var3)

    native String vx_req_aux_create_account_t_phone_get(Long var0, vx_req_aux_create_account_t var2)

    native Unit vx_req_aux_create_account_t_phone_set(Long var0, vx_req_aux_create_account_t var2, String var3)

    native String vx_req_aux_create_account_t_timezone_get(Long var0, vx_req_aux_create_account_t var2)

    native Unit vx_req_aux_create_account_t_timezone_set(Long var0, vx_req_aux_create_account_t var2, String var3)

    native String vx_req_aux_create_account_t_user_name_get(Long var0, vx_req_aux_create_account_t var2)

    native Unit vx_req_aux_create_account_t_user_name_set(Long var0, vx_req_aux_create_account_t var2, String var3)

    native Unit vx_req_aux_deactivate_account_create(Long var0)

    native Long vx_req_aux_deactivate_account_t_base_get(Long var0, vx_req_aux_deactivate_account_t var2)

    native Unit vx_req_aux_deactivate_account_t_base_set(Long var0, vx_req_aux_deactivate_account_t var2, Long var3, vx_req_base_t var5)

    native Long vx_req_aux_deactivate_account_t_credentials_get(Long var0, vx_req_aux_deactivate_account_t var2)

    native Unit vx_req_aux_deactivate_account_t_credentials_set(Long var0, vx_req_aux_deactivate_account_t var2, Long var3, vx_generic_credentials var5)

    native String vx_req_aux_deactivate_account_t_user_name_get(Long var0, vx_req_aux_deactivate_account_t var2)

    native Unit vx_req_aux_deactivate_account_t_user_name_set(Long var0, vx_req_aux_deactivate_account_t var2, String var3)

    native Unit vx_req_aux_diagnostic_state_dump_create(Long var0)

    native Long vx_req_aux_diagnostic_state_dump_t_base_get(Long var0, vx_req_aux_diagnostic_state_dump_t var2)

    native Unit vx_req_aux_diagnostic_state_dump_t_base_set(Long var0, vx_req_aux_diagnostic_state_dump_t var2, Long var3, vx_req_base_t var5)

    native Int vx_req_aux_diagnostic_state_dump_t_level_get(Long var0, vx_req_aux_diagnostic_state_dump_t var2)

    native Unit vx_req_aux_diagnostic_state_dump_t_level_set(Long var0, vx_req_aux_diagnostic_state_dump_t var2, Int var3)

    native Unit vx_req_aux_get_capture_devices_create(Long var0)

    native Long vx_req_aux_get_capture_devices_t_base_get(Long var0, vx_req_aux_get_capture_devices_t var2)

    native Unit vx_req_aux_get_capture_devices_t_base_set(Long var0, vx_req_aux_get_capture_devices_t var2, Long var3, vx_req_base_t var5)

    native Unit vx_req_aux_get_mic_level_create(Long var0)

    native Long vx_req_aux_get_mic_level_t_base_get(Long var0, vx_req_aux_get_mic_level_t var2)

    native Unit vx_req_aux_get_mic_level_t_base_set(Long var0, vx_req_aux_get_mic_level_t var2, Long var3, vx_req_base_t var5)

    native Unit vx_req_aux_get_render_devices_create(Long var0)

    native Long vx_req_aux_get_render_devices_t_base_get(Long var0, vx_req_aux_get_render_devices_t var2)

    native Unit vx_req_aux_get_render_devices_t_base_set(Long var0, vx_req_aux_get_render_devices_t var2, Long var3, vx_req_base_t var5)

    native Unit vx_req_aux_get_speaker_level_create(Long var0)

    native Long vx_req_aux_get_speaker_level_t_base_get(Long var0, vx_req_aux_get_speaker_level_t var2)

    native Unit vx_req_aux_get_speaker_level_t_base_set(Long var0, vx_req_aux_get_speaker_level_t var2, Long var3, vx_req_base_t var5)

    native Unit vx_req_aux_get_vad_properties_create(Long var0)

    native Long vx_req_aux_get_vad_properties_t_base_get(Long var0, vx_req_aux_get_vad_properties_t var2)

    native Unit vx_req_aux_get_vad_properties_t_base_set(Long var0, vx_req_aux_get_vad_properties_t var2, Long var3, vx_req_base_t var5)

    native Unit vx_req_aux_global_monitor_keyboard_mouse_create(Long var0)

    native Long vx_req_aux_global_monitor_keyboard_mouse_t_base_get(Long var0, vx_req_aux_global_monitor_keyboard_mouse_t var2)

    native Unit vx_req_aux_global_monitor_keyboard_mouse_t_base_set(Long var0, vx_req_aux_global_monitor_keyboard_mouse_t var2, Long var3, vx_req_base_t var5)

    native Int vx_req_aux_global_monitor_keyboard_mouse_t_code_count_get(Long var0, vx_req_aux_global_monitor_keyboard_mouse_t var2)

    native Unit vx_req_aux_global_monitor_keyboard_mouse_t_code_count_set(Long var0, vx_req_aux_global_monitor_keyboard_mouse_t var2, Int var3)

    native Long vx_req_aux_global_monitor_keyboard_mouse_t_codes_get(Long var0, vx_req_aux_global_monitor_keyboard_mouse_t var2)

    native Unit vx_req_aux_global_monitor_keyboard_mouse_t_codes_set(Long var0, vx_req_aux_global_monitor_keyboard_mouse_t var2, Long var3)

    native String vx_req_aux_global_monitor_keyboard_mouse_t_name_get(Long var0, vx_req_aux_global_monitor_keyboard_mouse_t var2)

    native Unit vx_req_aux_global_monitor_keyboard_mouse_t_name_set(Long var0, vx_req_aux_global_monitor_keyboard_mouse_t var2, String var3)

    native Unit vx_req_aux_global_monitor_keyboard_mouse_t_set_codes_item(Long var0, vx_req_aux_global_monitor_keyboard_mouse_t var2, Int var3, Int var4)

    native Unit vx_req_aux_notify_application_state_change_create(Long var0)

    native Long vx_req_aux_notify_application_state_change_t_base_get(Long var0, vx_req_aux_notify_application_state_change_t var2)

    native Unit vx_req_aux_notify_application_state_change_t_base_set(Long var0, vx_req_aux_notify_application_state_change_t var2, Long var3, vx_req_base_t var5)

    native Int vx_req_aux_notify_application_state_change_t_notification_type_get(Long var0, vx_req_aux_notify_application_state_change_t var2)

    native Unit vx_req_aux_notify_application_state_change_t_notification_type_set(Long var0, vx_req_aux_notify_application_state_change_t var2, Int var3)

    native Unit vx_req_aux_play_audio_buffer_create(Long var0)

    native String vx_req_aux_play_audio_buffer_t_account_handle_get(Long var0, vx_req_aux_play_audio_buffer_t var2)

    native Unit vx_req_aux_play_audio_buffer_t_account_handle_set(Long var0, vx_req_aux_play_audio_buffer_t var2, String var3)

    native Long vx_req_aux_play_audio_buffer_t_base_get(Long var0, vx_req_aux_play_audio_buffer_t var2)

    native Unit vx_req_aux_play_audio_buffer_t_base_set(Long var0, vx_req_aux_play_audio_buffer_t var2, Long var3, vx_req_base_t var5)

    native String vx_req_aux_play_audio_buffer_t_font_delta_get(Long var0, vx_req_aux_play_audio_buffer_t var2)

    native Unit vx_req_aux_play_audio_buffer_t_font_delta_set(Long var0, vx_req_aux_play_audio_buffer_t var2, String var3)

    native Int vx_req_aux_play_audio_buffer_t_template_font_id_get(Long var0, vx_req_aux_play_audio_buffer_t var2)

    native Unit vx_req_aux_play_audio_buffer_t_template_font_id_set(Long var0, vx_req_aux_play_audio_buffer_t var2, Int var3)

    native Unit vx_req_aux_reactivate_account_create(Long var0)

    native Long vx_req_aux_reactivate_account_t_base_get(Long var0, vx_req_aux_reactivate_account_t var2)

    native Unit vx_req_aux_reactivate_account_t_base_set(Long var0, vx_req_aux_reactivate_account_t var2, Long var3, vx_req_base_t var5)

    native Long vx_req_aux_reactivate_account_t_credentials_get(Long var0, vx_req_aux_reactivate_account_t var2)

    native Unit vx_req_aux_reactivate_account_t_credentials_set(Long var0, vx_req_aux_reactivate_account_t var2, Long var3, vx_generic_credentials var5)

    native String vx_req_aux_reactivate_account_t_user_name_get(Long var0, vx_req_aux_reactivate_account_t var2)

    native Unit vx_req_aux_reactivate_account_t_user_name_set(Long var0, vx_req_aux_reactivate_account_t var2, String var3)

    native Unit vx_req_aux_render_audio_modify_create(Long var0)

    native Long vx_req_aux_render_audio_modify_t_base_get(Long var0, vx_req_aux_render_audio_modify_t var2)

    native Unit vx_req_aux_render_audio_modify_t_base_set(Long var0, vx_req_aux_render_audio_modify_t var2, Long var3, vx_req_base_t var5)

    native String vx_req_aux_render_audio_modify_t_font_str_get(Long var0, vx_req_aux_render_audio_modify_t var2)

    native Unit vx_req_aux_render_audio_modify_t_font_str_set(Long var0, vx_req_aux_render_audio_modify_t var2, String var3)

    native Unit vx_req_aux_render_audio_start_create(Long var0)

    native Long vx_req_aux_render_audio_start_t_base_get(Long var0, vx_req_aux_render_audio_start_t var2)

    native Unit vx_req_aux_render_audio_start_t_base_set(Long var0, vx_req_aux_render_audio_start_t var2, Long var3, vx_req_base_t var5)

    native Int vx_req_aux_render_audio_start_t_loop_get(Long var0, vx_req_aux_render_audio_start_t var2)

    native Unit vx_req_aux_render_audio_start_t_loop_set(Long var0, vx_req_aux_render_audio_start_t var2, Int var3)

    native String vx_req_aux_render_audio_start_t_path_get(Long var0, vx_req_aux_render_audio_start_t var2)

    native Unit vx_req_aux_render_audio_start_t_path_set(Long var0, vx_req_aux_render_audio_start_t var2, String var3)

    native String vx_req_aux_render_audio_start_t_sound_file_path_get(Long var0, vx_req_aux_render_audio_start_t var2)

    native Unit vx_req_aux_render_audio_start_t_sound_file_path_set(Long var0, vx_req_aux_render_audio_start_t var2, String var3)

    native Unit vx_req_aux_render_audio_stop_create(Long var0)

    native Long vx_req_aux_render_audio_stop_t_base_get(Long var0, vx_req_aux_render_audio_stop_t var2)

    native Unit vx_req_aux_render_audio_stop_t_base_set(Long var0, vx_req_aux_render_audio_stop_t var2, Long var3, vx_req_base_t var5)

    native Unit vx_req_aux_reset_password_create(Long var0)

    native Long vx_req_aux_reset_password_t_base_get(Long var0, vx_req_aux_reset_password_t var2)

    native Unit vx_req_aux_reset_password_t_base_set(Long var0, vx_req_aux_reset_password_t var2, Long var3, vx_req_base_t var5)

    native String vx_req_aux_reset_password_t_server_url_get(Long var0, vx_req_aux_reset_password_t var2)

    native Unit vx_req_aux_reset_password_t_server_url_set(Long var0, vx_req_aux_reset_password_t var2, String var3)

    native String vx_req_aux_reset_password_t_user_email_get(Long var0, vx_req_aux_reset_password_t var2)

    native Unit vx_req_aux_reset_password_t_user_email_set(Long var0, vx_req_aux_reset_password_t var2, String var3)

    native String vx_req_aux_reset_password_t_user_uri_get(Long var0, vx_req_aux_reset_password_t var2)

    native Unit vx_req_aux_reset_password_t_user_uri_set(Long var0, vx_req_aux_reset_password_t var2, String var3)

    native Unit vx_req_aux_set_capture_device_create(Long var0)

    native Long vx_req_aux_set_capture_device_t_base_get(Long var0, vx_req_aux_set_capture_device_t var2)

    native Unit vx_req_aux_set_capture_device_t_base_set(Long var0, vx_req_aux_set_capture_device_t var2, Long var3, vx_req_base_t var5)

    native String vx_req_aux_set_capture_device_t_capture_device_specifier_get(Long var0, vx_req_aux_set_capture_device_t var2)

    native Unit vx_req_aux_set_capture_device_t_capture_device_specifier_set(Long var0, vx_req_aux_set_capture_device_t var2, String var3)

    native Unit vx_req_aux_set_idle_timeout_create(Long var0)

    native Long vx_req_aux_set_idle_timeout_t_base_get(Long var0, vx_req_aux_set_idle_timeout_t var2)

    native Unit vx_req_aux_set_idle_timeout_t_base_set(Long var0, vx_req_aux_set_idle_timeout_t var2, Long var3, vx_req_base_t var5)

    native Int vx_req_aux_set_idle_timeout_t_seconds_get(Long var0, vx_req_aux_set_idle_timeout_t var2)

    native Unit vx_req_aux_set_idle_timeout_t_seconds_set(Long var0, vx_req_aux_set_idle_timeout_t var2, Int var3)

    native Unit vx_req_aux_set_mic_level_create(Long var0)

    native Long vx_req_aux_set_mic_level_t_base_get(Long var0, vx_req_aux_set_mic_level_t var2)

    native Unit vx_req_aux_set_mic_level_t_base_set(Long var0, vx_req_aux_set_mic_level_t var2, Long var3, vx_req_base_t var5)

    native Int vx_req_aux_set_mic_level_t_level_get(Long var0, vx_req_aux_set_mic_level_t var2)

    native Unit vx_req_aux_set_mic_level_t_level_set(Long var0, vx_req_aux_set_mic_level_t var2, Int var3)

    native Unit vx_req_aux_set_render_device_create(Long var0)

    native Long vx_req_aux_set_render_device_t_base_get(Long var0, vx_req_aux_set_render_device_t var2)

    native Unit vx_req_aux_set_render_device_t_base_set(Long var0, vx_req_aux_set_render_device_t var2, Long var3, vx_req_base_t var5)

    native String vx_req_aux_set_render_device_t_render_device_specifier_get(Long var0, vx_req_aux_set_render_device_t var2)

    native Unit vx_req_aux_set_render_device_t_render_device_specifier_set(Long var0, vx_req_aux_set_render_device_t var2, String var3)

    native Unit vx_req_aux_set_speaker_level_create(Long var0)

    native Long vx_req_aux_set_speaker_level_t_base_get(Long var0, vx_req_aux_set_speaker_level_t var2)

    native Unit vx_req_aux_set_speaker_level_t_base_set(Long var0, vx_req_aux_set_speaker_level_t var2, Long var3, vx_req_base_t var5)

    native Int vx_req_aux_set_speaker_level_t_level_get(Long var0, vx_req_aux_set_speaker_level_t var2)

    native Unit vx_req_aux_set_speaker_level_t_level_set(Long var0, vx_req_aux_set_speaker_level_t var2, Int var3)

    native Unit vx_req_aux_set_vad_properties_create(Long var0)

    native Long vx_req_aux_set_vad_properties_t_base_get(Long var0, vx_req_aux_set_vad_properties_t var2)

    native Unit vx_req_aux_set_vad_properties_t_base_set(Long var0, vx_req_aux_set_vad_properties_t var2, Long var3, vx_req_base_t var5)

    native Int vx_req_aux_set_vad_properties_t_vad_hangover_get(Long var0, vx_req_aux_set_vad_properties_t var2)

    native Unit vx_req_aux_set_vad_properties_t_vad_hangover_set(Long var0, vx_req_aux_set_vad_properties_t var2, Int var3)

    native Int vx_req_aux_set_vad_properties_t_vad_sensitivity_get(Long var0, vx_req_aux_set_vad_properties_t var2)

    native Unit vx_req_aux_set_vad_properties_t_vad_sensitivity_set(Long var0, vx_req_aux_set_vad_properties_t var2, Int var3)

    native Unit vx_req_aux_start_buffer_capture_create(Long var0)

    native Long vx_req_aux_start_buffer_capture_t_base_get(Long var0, vx_req_aux_start_buffer_capture_t var2)

    native Unit vx_req_aux_start_buffer_capture_t_base_set(Long var0, vx_req_aux_start_buffer_capture_t var2, Long var3, vx_req_base_t var5)

    native String vx_req_base_t_cookie_get(Long var0, vx_req_base_t var2)

    native Unit vx_req_base_t_cookie_set(Long var0, vx_req_base_t var2, String var3)

    native Long vx_req_base_t_message_get(Long var0, vx_req_base_t var2)

    native Unit vx_req_base_t_message_set(Long var0, vx_req_base_t var2, Long var3, vx_message_base_t var5)

    native Int vx_req_base_t_type_get(Long var0, vx_req_base_t var2)

    native Unit vx_req_base_t_type_set(Long var0, vx_req_base_t var2, Int var3)

    native Long vx_req_base_t_vcookie_get(Long var0, vx_req_base_t var2)

    native Unit vx_req_base_t_vcookie_set(Long var0, vx_req_base_t var2, Long var3)

    native Unit vx_req_channel_ban_user_create(Long var0)

    native String vx_req_channel_ban_user_t_account_handle_get(Long var0, vx_req_channel_ban_user_t var2)

    native Unit vx_req_channel_ban_user_t_account_handle_set(Long var0, vx_req_channel_ban_user_t var2, String var3)

    native Long vx_req_channel_ban_user_t_base_get(Long var0, vx_req_channel_ban_user_t var2)

    native Unit vx_req_channel_ban_user_t_base_set(Long var0, vx_req_channel_ban_user_t var2, Long var3, vx_req_base_t var5)

    native String vx_req_channel_ban_user_t_channel_name_get(Long var0, vx_req_channel_ban_user_t var2)

    native Unit vx_req_channel_ban_user_t_channel_name_set(Long var0, vx_req_channel_ban_user_t var2, String var3)

    native String vx_req_channel_ban_user_t_channel_uri_get(Long var0, vx_req_channel_ban_user_t var2)

    native Unit vx_req_channel_ban_user_t_channel_uri_set(Long var0, vx_req_channel_ban_user_t var2, String var3)

    native String vx_req_channel_ban_user_t_participant_uri_get(Long var0, vx_req_channel_ban_user_t var2)

    native Unit vx_req_channel_ban_user_t_participant_uri_set(Long var0, vx_req_channel_ban_user_t var2, String var3)

    native Int vx_req_channel_ban_user_t_set_banned_get(Long var0, vx_req_channel_ban_user_t var2)

    native Unit vx_req_channel_ban_user_t_set_banned_set(Long var0, vx_req_channel_ban_user_t var2, Int var3)

    native Unit vx_req_channel_get_banned_users_create(Long var0)

    native String vx_req_channel_get_banned_users_t_account_handle_get(Long var0, vx_req_channel_get_banned_users_t var2)

    native Unit vx_req_channel_get_banned_users_t_account_handle_set(Long var0, vx_req_channel_get_banned_users_t var2, String var3)

    native Long vx_req_channel_get_banned_users_t_base_get(Long var0, vx_req_channel_get_banned_users_t var2)

    native Unit vx_req_channel_get_banned_users_t_base_set(Long var0, vx_req_channel_get_banned_users_t var2, Long var3, vx_req_base_t var5)

    native String vx_req_channel_get_banned_users_t_channel_uri_get(Long var0, vx_req_channel_get_banned_users_t var2)

    native Unit vx_req_channel_get_banned_users_t_channel_uri_set(Long var0, vx_req_channel_get_banned_users_t var2, String var3)

    native Unit vx_req_channel_kick_user_create(Long var0)

    native String vx_req_channel_kick_user_t_account_handle_get(Long var0, vx_req_channel_kick_user_t var2)

    native Unit vx_req_channel_kick_user_t_account_handle_set(Long var0, vx_req_channel_kick_user_t var2, String var3)

    native Long vx_req_channel_kick_user_t_base_get(Long var0, vx_req_channel_kick_user_t var2)

    native Unit vx_req_channel_kick_user_t_base_set(Long var0, vx_req_channel_kick_user_t var2, Long var3, vx_req_base_t var5)

    native String vx_req_channel_kick_user_t_channel_name_get(Long var0, vx_req_channel_kick_user_t var2)

    native Unit vx_req_channel_kick_user_t_channel_name_set(Long var0, vx_req_channel_kick_user_t var2, String var3)

    native String vx_req_channel_kick_user_t_channel_uri_get(Long var0, vx_req_channel_kick_user_t var2)

    native Unit vx_req_channel_kick_user_t_channel_uri_set(Long var0, vx_req_channel_kick_user_t var2, String var3)

    native String vx_req_channel_kick_user_t_participant_uri_get(Long var0, vx_req_channel_kick_user_t var2)

    native Unit vx_req_channel_kick_user_t_participant_uri_set(Long var0, vx_req_channel_kick_user_t var2, String var3)

    native Unit vx_req_channel_mute_all_users_create(Long var0)

    native String vx_req_channel_mute_all_users_t_account_handle_get(Long var0, vx_req_channel_mute_all_users_t var2)

    native Unit vx_req_channel_mute_all_users_t_account_handle_set(Long var0, vx_req_channel_mute_all_users_t var2, String var3)

    native Long vx_req_channel_mute_all_users_t_base_get(Long var0, vx_req_channel_mute_all_users_t var2)

    native Unit vx_req_channel_mute_all_users_t_base_set(Long var0, vx_req_channel_mute_all_users_t var2, Long var3, vx_req_base_t var5)

    native String vx_req_channel_mute_all_users_t_channel_name_get(Long var0, vx_req_channel_mute_all_users_t var2)

    native Unit vx_req_channel_mute_all_users_t_channel_name_set(Long var0, vx_req_channel_mute_all_users_t var2, String var3)

    native String vx_req_channel_mute_all_users_t_channel_uri_get(Long var0, vx_req_channel_mute_all_users_t var2)

    native Unit vx_req_channel_mute_all_users_t_channel_uri_set(Long var0, vx_req_channel_mute_all_users_t var2, String var3)

    native Int vx_req_channel_mute_all_users_t_scope_get(Long var0, vx_req_channel_mute_all_users_t var2)

    native Unit vx_req_channel_mute_all_users_t_scope_set(Long var0, vx_req_channel_mute_all_users_t var2, Int var3)

    native Int vx_req_channel_mute_all_users_t_set_muted_get(Long var0, vx_req_channel_mute_all_users_t var2)

    native Unit vx_req_channel_mute_all_users_t_set_muted_set(Long var0, vx_req_channel_mute_all_users_t var2, Int var3)

    native Unit vx_req_channel_mute_user_create(Long var0)

    native String vx_req_channel_mute_user_t_account_handle_get(Long var0, vx_req_channel_mute_user_t var2)

    native Unit vx_req_channel_mute_user_t_account_handle_set(Long var0, vx_req_channel_mute_user_t var2, String var3)

    native Long vx_req_channel_mute_user_t_base_get(Long var0, vx_req_channel_mute_user_t var2)

    native Unit vx_req_channel_mute_user_t_base_set(Long var0, vx_req_channel_mute_user_t var2, Long var3, vx_req_base_t var5)

    native String vx_req_channel_mute_user_t_channel_name_get(Long var0, vx_req_channel_mute_user_t var2)

    native Unit vx_req_channel_mute_user_t_channel_name_set(Long var0, vx_req_channel_mute_user_t var2, String var3)

    native String vx_req_channel_mute_user_t_channel_uri_get(Long var0, vx_req_channel_mute_user_t var2)

    native Unit vx_req_channel_mute_user_t_channel_uri_set(Long var0, vx_req_channel_mute_user_t var2, String var3)

    native String vx_req_channel_mute_user_t_participant_uri_get(Long var0, vx_req_channel_mute_user_t var2)

    native Unit vx_req_channel_mute_user_t_participant_uri_set(Long var0, vx_req_channel_mute_user_t var2, String var3)

    native Int vx_req_channel_mute_user_t_scope_get(Long var0, vx_req_channel_mute_user_t var2)

    native Unit vx_req_channel_mute_user_t_scope_set(Long var0, vx_req_channel_mute_user_t var2, Int var3)

    native Int vx_req_channel_mute_user_t_set_muted_get(Long var0, vx_req_channel_mute_user_t var2)

    native Unit vx_req_channel_mute_user_t_set_muted_set(Long var0, vx_req_channel_mute_user_t var2, Int var3)

    native Unit vx_req_channel_set_lock_mode_create(Long var0)

    native String vx_req_channel_set_lock_mode_t_account_handle_get(Long var0, vx_req_channel_set_lock_mode_t var2)

    native Unit vx_req_channel_set_lock_mode_t_account_handle_set(Long var0, vx_req_channel_set_lock_mode_t var2, String var3)

    native Long vx_req_channel_set_lock_mode_t_base_get(Long var0, vx_req_channel_set_lock_mode_t var2)

    native Unit vx_req_channel_set_lock_mode_t_base_set(Long var0, vx_req_channel_set_lock_mode_t var2, Long var3, vx_req_base_t var5)

    native String vx_req_channel_set_lock_mode_t_channel_uri_get(Long var0, vx_req_channel_set_lock_mode_t var2)

    native Unit vx_req_channel_set_lock_mode_t_channel_uri_set(Long var0, vx_req_channel_set_lock_mode_t var2, String var3)

    native Int vx_req_channel_set_lock_mode_t_lock_mode_get(Long var0, vx_req_channel_set_lock_mode_t var2)

    native Unit vx_req_channel_set_lock_mode_t_lock_mode_set(Long var0, vx_req_channel_set_lock_mode_t var2, Int var3)

    native Unit vx_req_connector_create_create(Long var0)

    native String vx_req_connector_create_t_acct_mgmt_server_get(Long var0, vx_req_connector_create_t var2)

    native Unit vx_req_connector_create_t_acct_mgmt_server_set(Long var0, vx_req_connector_create_t var2, String var3)

    native Int vx_req_connector_create_t_allow_cross_domain_logins_get(Long var0, vx_req_connector_create_t var2)

    native Unit vx_req_connector_create_t_allow_cross_domain_logins_set(Long var0, vx_req_connector_create_t var2, Int var3)

    native String vx_req_connector_create_t_application_get(Long var0, vx_req_connector_create_t var2)

    native Unit vx_req_connector_create_t_application_set(Long var0, vx_req_connector_create_t var2, String var3)

    native Int vx_req_connector_create_t_attempt_stun_get(Long var0, vx_req_connector_create_t var2)

    native Unit vx_req_connector_create_t_attempt_stun_set(Long var0, vx_req_connector_create_t var2, Int var3)

    native Long vx_req_connector_create_t_base_get(Long var0, vx_req_connector_create_t var2)

    native Unit vx_req_connector_create_t_base_set(Long var0, vx_req_connector_create_t var2, Long var3, vx_req_base_t var5)

    native String vx_req_connector_create_t_client_name_get(Long var0, vx_req_connector_create_t var2)

    native Unit vx_req_connector_create_t_client_name_set(Long var0, vx_req_connector_create_t var2, String var3)

    native Int vx_req_connector_create_t_default_codec_get(Long var0, vx_req_connector_create_t var2)

    native Unit vx_req_connector_create_t_default_codec_set(Long var0, vx_req_connector_create_t var2, Int var3)

    native String vx_req_connector_create_t_http_proxy_server_name_get(Long var0, vx_req_connector_create_t var2)

    native Unit vx_req_connector_create_t_http_proxy_server_name_set(Long var0, vx_req_connector_create_t var2, String var3)

    native Int vx_req_connector_create_t_http_proxy_server_port_get(Long var0, vx_req_connector_create_t var2)

    native Unit vx_req_connector_create_t_http_proxy_server_port_set(Long var0, vx_req_connector_create_t var2, Int var3)

    native String vx_req_connector_create_t_log_filename_prefix_get(Long var0, vx_req_connector_create_t var2)

    native Unit vx_req_connector_create_t_log_filename_prefix_set(Long var0, vx_req_connector_create_t var2, String var3)

    native String vx_req_connector_create_t_log_filename_suffix_get(Long var0, vx_req_connector_create_t var2)

    native Unit vx_req_connector_create_t_log_filename_suffix_set(Long var0, vx_req_connector_create_t var2, String var3)

    native String vx_req_connector_create_t_log_folder_get(Long var0, vx_req_connector_create_t var2)

    native Unit vx_req_connector_create_t_log_folder_set(Long var0, vx_req_connector_create_t var2, String var3)

    native Int vx_req_connector_create_t_log_level_get(Long var0, vx_req_connector_create_t var2)

    native Unit vx_req_connector_create_t_log_level_set(Long var0, vx_req_connector_create_t var2, Int var3)

    native Int vx_req_connector_create_t_max_calls_get(Long var0, vx_req_connector_create_t var2)

    native Unit vx_req_connector_create_t_max_calls_set(Long var0, vx_req_connector_create_t var2, Int var3)

    native Int vx_req_connector_create_t_maximum_port_get(Long var0, vx_req_connector_create_t var2)

    native Unit vx_req_connector_create_t_maximum_port_set(Long var0, vx_req_connector_create_t var2, Int var3)

    native String vx_req_connector_create_t_media_probe_server_get(Long var0, vx_req_connector_create_t var2)

    native Unit vx_req_connector_create_t_media_probe_server_set(Long var0, vx_req_connector_create_t var2, String var3)

    native Int vx_req_connector_create_t_minimum_port_get(Long var0, vx_req_connector_create_t var2)

    native Unit vx_req_connector_create_t_minimum_port_set(Long var0, vx_req_connector_create_t var2, Int var3)

    native Int vx_req_connector_create_t_mode_get(Long var0, vx_req_connector_create_t var2)

    native Unit vx_req_connector_create_t_mode_set(Long var0, vx_req_connector_create_t var2, Int var3)

    native Int vx_req_connector_create_t_session_handle_type_get(Long var0, vx_req_connector_create_t var2)

    native Unit vx_req_connector_create_t_session_handle_type_set(Long var0, vx_req_connector_create_t var2, Int var3)

    native String vx_req_connector_create_t_user_agent_id_get(Long var0, vx_req_connector_create_t var2)

    native Unit vx_req_connector_create_t_user_agent_id_set(Long var0, vx_req_connector_create_t var2, String var3)

    native Unit vx_req_connector_get_local_audio_info_create(Long var0)

    native Long vx_req_connector_get_local_audio_info_t_base_get(Long var0, vx_req_connector_get_local_audio_info_t var2)

    native Unit vx_req_connector_get_local_audio_info_t_base_set(Long var0, vx_req_connector_get_local_audio_info_t var2, Long var3, vx_req_base_t var5)

    native String vx_req_connector_get_local_audio_info_t_connector_handle_get(Long var0, vx_req_connector_get_local_audio_info_t var2)

    native Unit vx_req_connector_get_local_audio_info_t_connector_handle_set(Long var0, vx_req_connector_get_local_audio_info_t var2, String var3)

    native Unit vx_req_connector_initiate_shutdown_create(Long var0)

    native Long vx_req_connector_initiate_shutdown_t_base_get(Long var0, vx_req_connector_initiate_shutdown_t var2)

    native Unit vx_req_connector_initiate_shutdown_t_base_set(Long var0, vx_req_connector_initiate_shutdown_t var2, Long var3, vx_req_base_t var5)

    native String vx_req_connector_initiate_shutdown_t_client_name_get(Long var0, vx_req_connector_initiate_shutdown_t var2)

    native Unit vx_req_connector_initiate_shutdown_t_client_name_set(Long var0, vx_req_connector_initiate_shutdown_t var2, String var3)

    native String vx_req_connector_initiate_shutdown_t_connector_handle_get(Long var0, vx_req_connector_initiate_shutdown_t var2)

    native Unit vx_req_connector_initiate_shutdown_t_connector_handle_set(Long var0, vx_req_connector_initiate_shutdown_t var2, String var3)

    native Unit vx_req_connector_mute_local_mic_create(Long var0)

    native Long vx_req_connector_mute_local_mic_t_base_get(Long var0, vx_req_connector_mute_local_mic_t var2)

    native Unit vx_req_connector_mute_local_mic_t_base_set(Long var0, vx_req_connector_mute_local_mic_t var2, Long var3, vx_req_base_t var5)

    native String vx_req_connector_mute_local_mic_t_connector_handle_get(Long var0, vx_req_connector_mute_local_mic_t var2)

    native Unit vx_req_connector_mute_local_mic_t_connector_handle_set(Long var0, vx_req_connector_mute_local_mic_t var2, String var3)

    native Int vx_req_connector_mute_local_mic_t_mute_level_get(Long var0, vx_req_connector_mute_local_mic_t var2)

    native Unit vx_req_connector_mute_local_mic_t_mute_level_set(Long var0, vx_req_connector_mute_local_mic_t var2, Int var3)

    native Unit vx_req_connector_mute_local_speaker_create(Long var0)

    native Long vx_req_connector_mute_local_speaker_t_base_get(Long var0, vx_req_connector_mute_local_speaker_t var2)

    native Unit vx_req_connector_mute_local_speaker_t_base_set(Long var0, vx_req_connector_mute_local_speaker_t var2, Long var3, vx_req_base_t var5)

    native String vx_req_connector_mute_local_speaker_t_connector_handle_get(Long var0, vx_req_connector_mute_local_speaker_t var2)

    native Unit vx_req_connector_mute_local_speaker_t_connector_handle_set(Long var0, vx_req_connector_mute_local_speaker_t var2, String var3)

    native Int vx_req_connector_mute_local_speaker_t_mute_level_get(Long var0, vx_req_connector_mute_local_speaker_t var2)

    native Unit vx_req_connector_mute_local_speaker_t_mute_level_set(Long var0, vx_req_connector_mute_local_speaker_t var2, Int var3)

    native Unit vx_req_connector_set_local_mic_volume_create(Long var0)

    native Long vx_req_connector_set_local_mic_volume_t_base_get(Long var0, vx_req_connector_set_local_mic_volume_t var2)

    native Unit vx_req_connector_set_local_mic_volume_t_base_set(Long var0, vx_req_connector_set_local_mic_volume_t var2, Long var3, vx_req_base_t var5)

    native String vx_req_connector_set_local_mic_volume_t_connector_handle_get(Long var0, vx_req_connector_set_local_mic_volume_t var2)

    native Unit vx_req_connector_set_local_mic_volume_t_connector_handle_set(Long var0, vx_req_connector_set_local_mic_volume_t var2, String var3)

    native Int vx_req_connector_set_local_mic_volume_t_volume_get(Long var0, vx_req_connector_set_local_mic_volume_t var2)

    native Unit vx_req_connector_set_local_mic_volume_t_volume_set(Long var0, vx_req_connector_set_local_mic_volume_t var2, Int var3)

    native Unit vx_req_connector_set_local_speaker_volume_create(Long var0)

    native Long vx_req_connector_set_local_speaker_volume_t_base_get(Long var0, vx_req_connector_set_local_speaker_volume_t var2)

    native Unit vx_req_connector_set_local_speaker_volume_t_base_set(Long var0, vx_req_connector_set_local_speaker_volume_t var2, Long var3, vx_req_base_t var5)

    native String vx_req_connector_set_local_speaker_volume_t_connector_handle_get(Long var0, vx_req_connector_set_local_speaker_volume_t var2)

    native Unit vx_req_connector_set_local_speaker_volume_t_connector_handle_set(Long var0, vx_req_connector_set_local_speaker_volume_t var2, String var3)

    native Int vx_req_connector_set_local_speaker_volume_t_volume_get(Long var0, vx_req_connector_set_local_speaker_volume_t var2)

    native Unit vx_req_connector_set_local_speaker_volume_t_volume_set(Long var0, vx_req_connector_set_local_speaker_volume_t var2, Int var3)

    native Unit vx_req_session_channel_invite_user_create(Long var0)

    native Long vx_req_session_channel_invite_user_t_base_get(Long var0, vx_req_session_channel_invite_user_t var2)

    native Unit vx_req_session_channel_invite_user_t_base_set(Long var0, vx_req_session_channel_invite_user_t var2, Long var3, vx_req_base_t var5)

    native String vx_req_session_channel_invite_user_t_participant_uri_get(Long var0, vx_req_session_channel_invite_user_t var2)

    native Unit vx_req_session_channel_invite_user_t_participant_uri_set(Long var0, vx_req_session_channel_invite_user_t var2, String var3)

    native String vx_req_session_channel_invite_user_t_session_handle_get(Long var0, vx_req_session_channel_invite_user_t var2)

    native Unit vx_req_session_channel_invite_user_t_session_handle_set(Long var0, vx_req_session_channel_invite_user_t var2, String var3)

    native Unit vx_req_session_create_create(Long var0)

    native String vx_req_session_create_t_account_handle_get(Long var0, vx_req_session_create_t var2)

    native Unit vx_req_session_create_t_account_handle_set(Long var0, vx_req_session_create_t var2, String var3)

    native String vx_req_session_create_t_alias_username_get(Long var0, vx_req_session_create_t var2)

    native Unit vx_req_session_create_t_alias_username_set(Long var0, vx_req_session_create_t var2, String var3)

    native Long vx_req_session_create_t_base_get(Long var0, vx_req_session_create_t var2)

    native Unit vx_req_session_create_t_base_set(Long var0, vx_req_session_create_t var2, Long var3, vx_req_base_t var5)

    native Int vx_req_session_create_t_connect_audio_get(Long var0, vx_req_session_create_t var2)

    native Unit vx_req_session_create_t_connect_audio_set(Long var0, vx_req_session_create_t var2, Int var3)

    native Int vx_req_session_create_t_connect_text_get(Long var0, vx_req_session_create_t var2)

    native Unit vx_req_session_create_t_connect_text_set(Long var0, vx_req_session_create_t var2, Int var3)

    native Int vx_req_session_create_t_jitter_compensation_get(Long var0, vx_req_session_create_t var2)

    native Unit vx_req_session_create_t_jitter_compensation_set(Long var0, vx_req_session_create_t var2, Int var3)

    native Int vx_req_session_create_t_join_audio_get(Long var0, vx_req_session_create_t var2)

    native Unit vx_req_session_create_t_join_audio_set(Long var0, vx_req_session_create_t var2, Int var3)

    native Int vx_req_session_create_t_join_text_get(Long var0, vx_req_session_create_t var2)

    native Unit vx_req_session_create_t_join_text_set(Long var0, vx_req_session_create_t var2, Int var3)

    native String vx_req_session_create_t_name_get(Long var0, vx_req_session_create_t var2)

    native Unit vx_req_session_create_t_name_set(Long var0, vx_req_session_create_t var2, String var3)

    native String vx_req_session_create_t_password_get(Long var0, vx_req_session_create_t var2)

    native Int vx_req_session_create_t_password_hash_algorithm_get(Long var0, vx_req_session_create_t var2)

    native Unit vx_req_session_create_t_password_hash_algorithm_set(Long var0, vx_req_session_create_t var2, Int var3)

    native Unit vx_req_session_create_t_password_set(Long var0, vx_req_session_create_t var2, String var3)

    native Int vx_req_session_create_t_session_font_id_get(Long var0, vx_req_session_create_t var2)

    native Unit vx_req_session_create_t_session_font_id_set(Long var0, vx_req_session_create_t var2, Int var3)

    native String vx_req_session_create_t_uri_get(Long var0, vx_req_session_create_t var2)

    native Unit vx_req_session_create_t_uri_set(Long var0, vx_req_session_create_t var2, String var3)

    native Unit vx_req_session_media_connect_create(Long var0)

    native Long vx_req_session_media_connect_t_base_get(Long var0, vx_req_session_media_connect_t var2)

    native Unit vx_req_session_media_connect_t_base_set(Long var0, vx_req_session_media_connect_t var2, Long var3, vx_req_base_t var5)

    native String vx_req_session_media_connect_t_capture_device_id_get(Long var0, vx_req_session_media_connect_t var2)

    native Unit vx_req_session_media_connect_t_capture_device_id_set(Long var0, vx_req_session_media_connect_t var2, String var3)

    native Int vx_req_session_media_connect_t_jitter_compensation_get(Long var0, vx_req_session_media_connect_t var2)

    native Unit vx_req_session_media_connect_t_jitter_compensation_set(Long var0, vx_req_session_media_connect_t var2, Int var3)

    native Int vx_req_session_media_connect_t_loop_mode_duration_seconds_get(Long var0, vx_req_session_media_connect_t var2)

    native Unit vx_req_session_media_connect_t_loop_mode_duration_seconds_set(Long var0, vx_req_session_media_connect_t var2, Int var3)

    native Int vx_req_session_media_connect_t_media_get(Long var0, vx_req_session_media_connect_t var2)

    native Unit vx_req_session_media_connect_t_media_set(Long var0, vx_req_session_media_connect_t var2, Int var3)

    native String vx_req_session_media_connect_t_render_device_id_get(Long var0, vx_req_session_media_connect_t var2)

    native Unit vx_req_session_media_connect_t_render_device_id_set(Long var0, vx_req_session_media_connect_t var2, String var3)

    native Int vx_req_session_media_connect_t_session_font_id_get(Long var0, vx_req_session_media_connect_t var2)

    native Unit vx_req_session_media_connect_t_session_font_id_set(Long var0, vx_req_session_media_connect_t var2, Int var3)

    native String vx_req_session_media_connect_t_session_handle_get(Long var0, vx_req_session_media_connect_t var2)

    native Unit vx_req_session_media_connect_t_session_handle_set(Long var0, vx_req_session_media_connect_t var2, String var3)

    native String vx_req_session_media_connect_t_sessiongroup_handle_get(Long var0, vx_req_session_media_connect_t var2)

    native Unit vx_req_session_media_connect_t_sessiongroup_handle_set(Long var0, vx_req_session_media_connect_t var2, String var3)

    native Unit vx_req_session_media_disconnect_create(Long var0)

    native Long vx_req_session_media_disconnect_t_base_get(Long var0, vx_req_session_media_disconnect_t var2)

    native Unit vx_req_session_media_disconnect_t_base_set(Long var0, vx_req_session_media_disconnect_t var2, Long var3, vx_req_base_t var5)

    native Int vx_req_session_media_disconnect_t_media_get(Long var0, vx_req_session_media_disconnect_t var2)

    native Unit vx_req_session_media_disconnect_t_media_set(Long var0, vx_req_session_media_disconnect_t var2, Int var3)

    native String vx_req_session_media_disconnect_t_session_handle_get(Long var0, vx_req_session_media_disconnect_t var2)

    native Unit vx_req_session_media_disconnect_t_session_handle_set(Long var0, vx_req_session_media_disconnect_t var2, String var3)

    native String vx_req_session_media_disconnect_t_sessiongroup_handle_get(Long var0, vx_req_session_media_disconnect_t var2)

    native Unit vx_req_session_media_disconnect_t_sessiongroup_handle_set(Long var0, vx_req_session_media_disconnect_t var2, String var3)

    native Int vx_req_session_media_disconnect_t_termination_status_get(Long var0, vx_req_session_media_disconnect_t var2)

    native Unit vx_req_session_media_disconnect_t_termination_status_set(Long var0, vx_req_session_media_disconnect_t var2, Int var3)

    native Unit vx_req_session_mute_local_speaker_create(Long var0)

    native Long vx_req_session_mute_local_speaker_t_base_get(Long var0, vx_req_session_mute_local_speaker_t var2)

    native Unit vx_req_session_mute_local_speaker_t_base_set(Long var0, vx_req_session_mute_local_speaker_t var2, Long var3, vx_req_base_t var5)

    native Int vx_req_session_mute_local_speaker_t_mute_level_get(Long var0, vx_req_session_mute_local_speaker_t var2)

    native Unit vx_req_session_mute_local_speaker_t_mute_level_set(Long var0, vx_req_session_mute_local_speaker_t var2, Int var3)

    native Int vx_req_session_mute_local_speaker_t_scope_get(Long var0, vx_req_session_mute_local_speaker_t var2)

    native Unit vx_req_session_mute_local_speaker_t_scope_set(Long var0, vx_req_session_mute_local_speaker_t var2, Int var3)

    native String vx_req_session_mute_local_speaker_t_session_handle_get(Long var0, vx_req_session_mute_local_speaker_t var2)

    native Unit vx_req_session_mute_local_speaker_t_session_handle_set(Long var0, vx_req_session_mute_local_speaker_t var2, String var3)

    native Unit vx_req_session_send_dtmf_create(Long var0)

    native Long vx_req_session_send_dtmf_t_base_get(Long var0, vx_req_session_send_dtmf_t var2)

    native Unit vx_req_session_send_dtmf_t_base_set(Long var0, vx_req_session_send_dtmf_t var2, Long var3, vx_req_base_t var5)

    native Int vx_req_session_send_dtmf_t_dtmf_type_get(Long var0, vx_req_session_send_dtmf_t var2)

    native Unit vx_req_session_send_dtmf_t_dtmf_type_set(Long var0, vx_req_session_send_dtmf_t var2, Int var3)

    native String vx_req_session_send_dtmf_t_session_handle_get(Long var0, vx_req_session_send_dtmf_t var2)

    native Unit vx_req_session_send_dtmf_t_session_handle_set(Long var0, vx_req_session_send_dtmf_t var2, String var3)

    native Unit vx_req_session_send_message_create(Long var0)

    native Long vx_req_session_send_message_t_base_get(Long var0, vx_req_session_send_message_t var2)

    native Unit vx_req_session_send_message_t_base_set(Long var0, vx_req_session_send_message_t var2, Long var3, vx_req_base_t var5)

    native String vx_req_session_send_message_t_message_body_get(Long var0, vx_req_session_send_message_t var2)

    native Unit vx_req_session_send_message_t_message_body_set(Long var0, vx_req_session_send_message_t var2, String var3)

    native String vx_req_session_send_message_t_message_header_get(Long var0, vx_req_session_send_message_t var2)

    native Unit vx_req_session_send_message_t_message_header_set(Long var0, vx_req_session_send_message_t var2, String var3)

    native String vx_req_session_send_message_t_session_handle_get(Long var0, vx_req_session_send_message_t var2)

    native Unit vx_req_session_send_message_t_session_handle_set(Long var0, vx_req_session_send_message_t var2, String var3)

    native Unit vx_req_session_send_notification_create(Long var0)

    native Long vx_req_session_send_notification_t_base_get(Long var0, vx_req_session_send_notification_t var2)

    native Unit vx_req_session_send_notification_t_base_set(Long var0, vx_req_session_send_notification_t var2, Long var3, vx_req_base_t var5)

    native Int vx_req_session_send_notification_t_notification_type_get(Long var0, vx_req_session_send_notification_t var2)

    native Unit vx_req_session_send_notification_t_notification_type_set(Long var0, vx_req_session_send_notification_t var2, Int var3)

    native String vx_req_session_send_notification_t_session_handle_get(Long var0, vx_req_session_send_notification_t var2)

    native Unit vx_req_session_send_notification_t_session_handle_set(Long var0, vx_req_session_send_notification_t var2, String var3)

    native Unit vx_req_session_set_3d_position_create(Long var0)

    native Long vx_req_session_set_3d_position_t_base_get(Long var0, vx_req_session_set_3d_position_t var2)

    native Unit vx_req_session_set_3d_position_t_base_set(Long var0, vx_req_session_set_3d_position_t var2, Long var3, vx_req_base_t var5)

    native Double vx_req_session_set_3d_position_t_get_listener_at_orientation_item(Long var0, vx_req_session_set_3d_position_t var2, Int var3)

    native Double vx_req_session_set_3d_position_t_get_listener_left_orientation_item(Long var0, vx_req_session_set_3d_position_t var2, Int var3)

    native Double vx_req_session_set_3d_position_t_get_listener_position_item(Long var0, vx_req_session_set_3d_position_t var2, Int var3)

    native Double vx_req_session_set_3d_position_t_get_listener_up_orientation_item(Long var0, vx_req_session_set_3d_position_t var2, Int var3)

    native Double vx_req_session_set_3d_position_t_get_listener_velocity_item(Long var0, vx_req_session_set_3d_position_t var2, Int var3)

    native Double vx_req_session_set_3d_position_t_get_speaker_at_orientation_item(Long var0, vx_req_session_set_3d_position_t var2, Int var3)

    native Double vx_req_session_set_3d_position_t_get_speaker_left_orientation_item(Long var0, vx_req_session_set_3d_position_t var2, Int var3)

    native Double vx_req_session_set_3d_position_t_get_speaker_position_item(Long var0, vx_req_session_set_3d_position_t var2, Int var3)

    native Double vx_req_session_set_3d_position_t_get_speaker_up_orientation_item(Long var0, vx_req_session_set_3d_position_t var2, Int var3)

    native Double vx_req_session_set_3d_position_t_get_speaker_velocity_item(Long var0, vx_req_session_set_3d_position_t var2, Int var3)

    native Long vx_req_session_set_3d_position_t_listener_at_orientation_get(Long var0, vx_req_session_set_3d_position_t var2)

    native Unit vx_req_session_set_3d_position_t_listener_at_orientation_set(Long var0, vx_req_session_set_3d_position_t var2, Long var3)

    native Long vx_req_session_set_3d_position_t_listener_left_orientation_get(Long var0, vx_req_session_set_3d_position_t var2)

    native Unit vx_req_session_set_3d_position_t_listener_left_orientation_set(Long var0, vx_req_session_set_3d_position_t var2, Long var3)

    native Long vx_req_session_set_3d_position_t_listener_position_get(Long var0, vx_req_session_set_3d_position_t var2)

    native Unit vx_req_session_set_3d_position_t_listener_position_set(Long var0, vx_req_session_set_3d_position_t var2, Long var3)

    native Long vx_req_session_set_3d_position_t_listener_up_orientation_get(Long var0, vx_req_session_set_3d_position_t var2)

    native Unit vx_req_session_set_3d_position_t_listener_up_orientation_set(Long var0, vx_req_session_set_3d_position_t var2, Long var3)

    native Long vx_req_session_set_3d_position_t_listener_velocity_get(Long var0, vx_req_session_set_3d_position_t var2)

    native Unit vx_req_session_set_3d_position_t_listener_velocity_set(Long var0, vx_req_session_set_3d_position_t var2, Long var3)

    native Int vx_req_session_set_3d_position_t_req_disposition_type_get(Long var0, vx_req_session_set_3d_position_t var2)

    native Unit vx_req_session_set_3d_position_t_req_disposition_type_set(Long var0, vx_req_session_set_3d_position_t var2, Int var3)

    native String vx_req_session_set_3d_position_t_session_handle_get(Long var0, vx_req_session_set_3d_position_t var2)

    native Unit vx_req_session_set_3d_position_t_session_handle_set(Long var0, vx_req_session_set_3d_position_t var2, String var3)

    native Unit vx_req_session_set_3d_position_t_set_listener_at_orientation_item(Long var0, vx_req_session_set_3d_position_t var2, Int var3, Double var4)

    native Unit vx_req_session_set_3d_position_t_set_listener_left_orientation_item(Long var0, vx_req_session_set_3d_position_t var2, Int var3, Double var4)

    native Unit vx_req_session_set_3d_position_t_set_listener_position_item(Long var0, vx_req_session_set_3d_position_t var2, Int var3, Double var4)

    native Unit vx_req_session_set_3d_position_t_set_listener_up_orientation_item(Long var0, vx_req_session_set_3d_position_t var2, Int var3, Double var4)

    native Unit vx_req_session_set_3d_position_t_set_listener_velocity_item(Long var0, vx_req_session_set_3d_position_t var2, Int var3, Double var4)

    native Unit vx_req_session_set_3d_position_t_set_speaker_at_orientation_item(Long var0, vx_req_session_set_3d_position_t var2, Int var3, Double var4)

    native Unit vx_req_session_set_3d_position_t_set_speaker_left_orientation_item(Long var0, vx_req_session_set_3d_position_t var2, Int var3, Double var4)

    native Unit vx_req_session_set_3d_position_t_set_speaker_position_item(Long var0, vx_req_session_set_3d_position_t var2, Int var3, Double var4)

    native Unit vx_req_session_set_3d_position_t_set_speaker_up_orientation_item(Long var0, vx_req_session_set_3d_position_t var2, Int var3, Double var4)

    native Unit vx_req_session_set_3d_position_t_set_speaker_velocity_item(Long var0, vx_req_session_set_3d_position_t var2, Int var3, Double var4)

    native Long vx_req_session_set_3d_position_t_speaker_at_orientation_get(Long var0, vx_req_session_set_3d_position_t var2)

    native Unit vx_req_session_set_3d_position_t_speaker_at_orientation_set(Long var0, vx_req_session_set_3d_position_t var2, Long var3)

    native Long vx_req_session_set_3d_position_t_speaker_left_orientation_get(Long var0, vx_req_session_set_3d_position_t var2)

    native Unit vx_req_session_set_3d_position_t_speaker_left_orientation_set(Long var0, vx_req_session_set_3d_position_t var2, Long var3)

    native Long vx_req_session_set_3d_position_t_speaker_position_get(Long var0, vx_req_session_set_3d_position_t var2)

    native Unit vx_req_session_set_3d_position_t_speaker_position_set(Long var0, vx_req_session_set_3d_position_t var2, Long var3)

    native Long vx_req_session_set_3d_position_t_speaker_up_orientation_get(Long var0, vx_req_session_set_3d_position_t var2)

    native Unit vx_req_session_set_3d_position_t_speaker_up_orientation_set(Long var0, vx_req_session_set_3d_position_t var2, Long var3)

    native Long vx_req_session_set_3d_position_t_speaker_velocity_get(Long var0, vx_req_session_set_3d_position_t var2)

    native Unit vx_req_session_set_3d_position_t_speaker_velocity_set(Long var0, vx_req_session_set_3d_position_t var2, Long var3)

    native Int vx_req_session_set_3d_position_t_type_get(Long var0, vx_req_session_set_3d_position_t var2)

    native Unit vx_req_session_set_3d_position_t_type_set(Long var0, vx_req_session_set_3d_position_t var2, Int var3)

    native Unit vx_req_session_set_local_speaker_volume_create(Long var0)

    native Long vx_req_session_set_local_speaker_volume_t_base_get(Long var0, vx_req_session_set_local_speaker_volume_t var2)

    native Unit vx_req_session_set_local_speaker_volume_t_base_set(Long var0, vx_req_session_set_local_speaker_volume_t var2, Long var3, vx_req_base_t var5)

    native String vx_req_session_set_local_speaker_volume_t_session_handle_get(Long var0, vx_req_session_set_local_speaker_volume_t var2)

    native Unit vx_req_session_set_local_speaker_volume_t_session_handle_set(Long var0, vx_req_session_set_local_speaker_volume_t var2, String var3)

    native Int vx_req_session_set_local_speaker_volume_t_volume_get(Long var0, vx_req_session_set_local_speaker_volume_t var2)

    native Unit vx_req_session_set_local_speaker_volume_t_volume_set(Long var0, vx_req_session_set_local_speaker_volume_t var2, Int var3)

    native Unit vx_req_session_set_participant_mute_for_me_create(Long var0)

    native Long vx_req_session_set_participant_mute_for_me_t_base_get(Long var0, vx_req_session_set_participant_mute_for_me_t var2)

    native Unit vx_req_session_set_participant_mute_for_me_t_base_set(Long var0, vx_req_session_set_participant_mute_for_me_t var2, Long var3, vx_req_base_t var5)

    native Int vx_req_session_set_participant_mute_for_me_t_mute_get(Long var0, vx_req_session_set_participant_mute_for_me_t var2)

    native Unit vx_req_session_set_participant_mute_for_me_t_mute_set(Long var0, vx_req_session_set_participant_mute_for_me_t var2, Int var3)

    native String vx_req_session_set_participant_mute_for_me_t_participant_uri_get(Long var0, vx_req_session_set_participant_mute_for_me_t var2)

    native Unit vx_req_session_set_participant_mute_for_me_t_participant_uri_set(Long var0, vx_req_session_set_participant_mute_for_me_t var2, String var3)

    native Int vx_req_session_set_participant_mute_for_me_t_scope_get(Long var0, vx_req_session_set_participant_mute_for_me_t var2)

    native Unit vx_req_session_set_participant_mute_for_me_t_scope_set(Long var0, vx_req_session_set_participant_mute_for_me_t var2, Int var3)

    native String vx_req_session_set_participant_mute_for_me_t_session_handle_get(Long var0, vx_req_session_set_participant_mute_for_me_t var2)

    native Unit vx_req_session_set_participant_mute_for_me_t_session_handle_set(Long var0, vx_req_session_set_participant_mute_for_me_t var2, String var3)

    native Unit vx_req_session_set_participant_volume_for_me_create(Long var0)

    native Long vx_req_session_set_participant_volume_for_me_t_base_get(Long var0, vx_req_session_set_participant_volume_for_me_t var2)

    native Unit vx_req_session_set_participant_volume_for_me_t_base_set(Long var0, vx_req_session_set_participant_volume_for_me_t var2, Long var3, vx_req_base_t var5)

    native String vx_req_session_set_participant_volume_for_me_t_participant_uri_get(Long var0, vx_req_session_set_participant_volume_for_me_t var2)

    native Unit vx_req_session_set_participant_volume_for_me_t_participant_uri_set(Long var0, vx_req_session_set_participant_volume_for_me_t var2, String var3)

    native String vx_req_session_set_participant_volume_for_me_t_session_handle_get(Long var0, vx_req_session_set_participant_volume_for_me_t var2)

    native Unit vx_req_session_set_participant_volume_for_me_t_session_handle_set(Long var0, vx_req_session_set_participant_volume_for_me_t var2, String var3)

    native Int vx_req_session_set_participant_volume_for_me_t_volume_get(Long var0, vx_req_session_set_participant_volume_for_me_t var2)

    native Unit vx_req_session_set_participant_volume_for_me_t_volume_set(Long var0, vx_req_session_set_participant_volume_for_me_t var2, Int var3)

    native Unit vx_req_session_set_voice_font_create(Long var0)

    native Long vx_req_session_set_voice_font_t_base_get(Long var0, vx_req_session_set_voice_font_t var2)

    native Unit vx_req_session_set_voice_font_t_base_set(Long var0, vx_req_session_set_voice_font_t var2, Long var3, vx_req_base_t var5)

    native Int vx_req_session_set_voice_font_t_session_font_id_get(Long var0, vx_req_session_set_voice_font_t var2)

    native Unit vx_req_session_set_voice_font_t_session_font_id_set(Long var0, vx_req_session_set_voice_font_t var2, Int var3)

    native String vx_req_session_set_voice_font_t_session_handle_get(Long var0, vx_req_session_set_voice_font_t var2)

    native Unit vx_req_session_set_voice_font_t_session_handle_set(Long var0, vx_req_session_set_voice_font_t var2, String var3)

    native Unit vx_req_session_terminate_create(Long var0)

    native Long vx_req_session_terminate_t_base_get(Long var0, vx_req_session_terminate_t var2)

    native Unit vx_req_session_terminate_t_base_set(Long var0, vx_req_session_terminate_t var2, Long var3, vx_req_base_t var5)

    native String vx_req_session_terminate_t_session_handle_get(Long var0, vx_req_session_terminate_t var2)

    native Unit vx_req_session_terminate_t_session_handle_set(Long var0, vx_req_session_terminate_t var2, String var3)

    native Unit vx_req_session_text_connect_create(Long var0)

    native Long vx_req_session_text_connect_t_base_get(Long var0, vx_req_session_text_connect_t var2)

    native Unit vx_req_session_text_connect_t_base_set(Long var0, vx_req_session_text_connect_t var2, Long var3, vx_req_base_t var5)

    native String vx_req_session_text_connect_t_session_handle_get(Long var0, vx_req_session_text_connect_t var2)

    native Unit vx_req_session_text_connect_t_session_handle_set(Long var0, vx_req_session_text_connect_t var2, String var3)

    native String vx_req_session_text_connect_t_sessiongroup_handle_get(Long var0, vx_req_session_text_connect_t var2)

    native Unit vx_req_session_text_connect_t_sessiongroup_handle_set(Long var0, vx_req_session_text_connect_t var2, String var3)

    native Unit vx_req_session_text_disconnect_create(Long var0)

    native Long vx_req_session_text_disconnect_t_base_get(Long var0, vx_req_session_text_disconnect_t var2)

    native Unit vx_req_session_text_disconnect_t_base_set(Long var0, vx_req_session_text_disconnect_t var2, Long var3, vx_req_base_t var5)

    native String vx_req_session_text_disconnect_t_session_handle_get(Long var0, vx_req_session_text_disconnect_t var2)

    native Unit vx_req_session_text_disconnect_t_session_handle_set(Long var0, vx_req_session_text_disconnect_t var2, String var3)

    native String vx_req_session_text_disconnect_t_sessiongroup_handle_get(Long var0, vx_req_session_text_disconnect_t var2)

    native Unit vx_req_session_text_disconnect_t_sessiongroup_handle_set(Long var0, vx_req_session_text_disconnect_t var2, String var3)

    native Unit vx_req_sessiongroup_add_session_create(Long var0)

    native Long vx_req_sessiongroup_add_session_t_base_get(Long var0, vx_req_sessiongroup_add_session_t var2)

    native Unit vx_req_sessiongroup_add_session_t_base_set(Long var0, vx_req_sessiongroup_add_session_t var2, Long var3, vx_req_base_t var5)

    native Int vx_req_sessiongroup_add_session_t_connect_audio_get(Long var0, vx_req_sessiongroup_add_session_t var2)

    native Unit vx_req_sessiongroup_add_session_t_connect_audio_set(Long var0, vx_req_sessiongroup_add_session_t var2, Int var3)

    native Int vx_req_sessiongroup_add_session_t_connect_text_get(Long var0, vx_req_sessiongroup_add_session_t var2)

    native Unit vx_req_sessiongroup_add_session_t_connect_text_set(Long var0, vx_req_sessiongroup_add_session_t var2, Int var3)

    native Int vx_req_sessiongroup_add_session_t_jitter_compensation_get(Long var0, vx_req_sessiongroup_add_session_t var2)

    native Unit vx_req_sessiongroup_add_session_t_jitter_compensation_set(Long var0, vx_req_sessiongroup_add_session_t var2, Int var3)

    native String vx_req_sessiongroup_add_session_t_name_get(Long var0, vx_req_sessiongroup_add_session_t var2)

    native Unit vx_req_sessiongroup_add_session_t_name_set(Long var0, vx_req_sessiongroup_add_session_t var2, String var3)

    native String vx_req_sessiongroup_add_session_t_password_get(Long var0, vx_req_sessiongroup_add_session_t var2)

    native Int vx_req_sessiongroup_add_session_t_password_hash_algorithm_get(Long var0, vx_req_sessiongroup_add_session_t var2)

    native Unit vx_req_sessiongroup_add_session_t_password_hash_algorithm_set(Long var0, vx_req_sessiongroup_add_session_t var2, Int var3)

    native Unit vx_req_sessiongroup_add_session_t_password_set(Long var0, vx_req_sessiongroup_add_session_t var2, String var3)

    native Int vx_req_sessiongroup_add_session_t_session_font_id_get(Long var0, vx_req_sessiongroup_add_session_t var2)

    native Unit vx_req_sessiongroup_add_session_t_session_font_id_set(Long var0, vx_req_sessiongroup_add_session_t var2, Int var3)

    native String vx_req_sessiongroup_add_session_t_sessiongroup_handle_get(Long var0, vx_req_sessiongroup_add_session_t var2)

    native Unit vx_req_sessiongroup_add_session_t_sessiongroup_handle_set(Long var0, vx_req_sessiongroup_add_session_t var2, String var3)

    native String vx_req_sessiongroup_add_session_t_uri_get(Long var0, vx_req_sessiongroup_add_session_t var2)

    native Unit vx_req_sessiongroup_add_session_t_uri_set(Long var0, vx_req_sessiongroup_add_session_t var2, String var3)

    native Unit vx_req_sessiongroup_control_audio_injection_create(Long var0)

    native Int vx_req_sessiongroup_control_audio_injection_t_audio_injection_control_type_get(Long var0, vx_req_sessiongroup_control_audio_injection_t var2)

    native Unit vx_req_sessiongroup_control_audio_injection_t_audio_injection_control_type_set(Long var0, vx_req_sessiongroup_control_audio_injection_t var2, Int var3)

    native Long vx_req_sessiongroup_control_audio_injection_t_base_get(Long var0, vx_req_sessiongroup_control_audio_injection_t var2)

    native Unit vx_req_sessiongroup_control_audio_injection_t_base_set(Long var0, vx_req_sessiongroup_control_audio_injection_t var2, Long var3, vx_req_base_t var5)

    native String vx_req_sessiongroup_control_audio_injection_t_filename_get(Long var0, vx_req_sessiongroup_control_audio_injection_t var2)

    native Unit vx_req_sessiongroup_control_audio_injection_t_filename_set(Long var0, vx_req_sessiongroup_control_audio_injection_t var2, String var3)

    native String vx_req_sessiongroup_control_audio_injection_t_sessiongroup_handle_get(Long var0, vx_req_sessiongroup_control_audio_injection_t var2)

    native Unit vx_req_sessiongroup_control_audio_injection_t_sessiongroup_handle_set(Long var0, vx_req_sessiongroup_control_audio_injection_t var2, String var3)

    native Unit vx_req_sessiongroup_control_playback_create(Long var0)

    native Long vx_req_sessiongroup_control_playback_t_base_get(Long var0, vx_req_sessiongroup_control_playback_t var2)

    native Unit vx_req_sessiongroup_control_playback_t_base_set(Long var0, vx_req_sessiongroup_control_playback_t var2, Long var3, vx_req_base_t var5)

    native String vx_req_sessiongroup_control_playback_t_filename_get(Long var0, vx_req_sessiongroup_control_playback_t var2)

    native Unit vx_req_sessiongroup_control_playback_t_filename_set(Long var0, vx_req_sessiongroup_control_playback_t var2, String var3)

    native Int vx_req_sessiongroup_control_playback_t_playback_control_type_get(Long var0, vx_req_sessiongroup_control_playback_t var2)

    native Unit vx_req_sessiongroup_control_playback_t_playback_control_type_set(Long var0, vx_req_sessiongroup_control_playback_t var2, Int var3)

    native String vx_req_sessiongroup_control_playback_t_sessiongroup_handle_get(Long var0, vx_req_sessiongroup_control_playback_t var2)

    native Unit vx_req_sessiongroup_control_playback_t_sessiongroup_handle_set(Long var0, vx_req_sessiongroup_control_playback_t var2, String var3)

    native Unit vx_req_sessiongroup_control_recording_create(Long var0)

    native Long vx_req_sessiongroup_control_recording_t_base_get(Long var0, vx_req_sessiongroup_control_recording_t var2)

    native Unit vx_req_sessiongroup_control_recording_t_base_set(Long var0, vx_req_sessiongroup_control_recording_t var2, Long var3, vx_req_base_t var5)

    native Int vx_req_sessiongroup_control_recording_t_delta_frames_per_control_frame_get(Long var0, vx_req_sessiongroup_control_recording_t var2)

    native Unit vx_req_sessiongroup_control_recording_t_delta_frames_per_control_frame_set(Long var0, vx_req_sessiongroup_control_recording_t var2, Int var3)

    native Int vx_req_sessiongroup_control_recording_t_enable_audio_recording_events_get(Long var0, vx_req_sessiongroup_control_recording_t var2)

    native Unit vx_req_sessiongroup_control_recording_t_enable_audio_recording_events_set(Long var0, vx_req_sessiongroup_control_recording_t var2, Int var3)

    native String vx_req_sessiongroup_control_recording_t_filename_get(Long var0, vx_req_sessiongroup_control_recording_t var2)

    native Unit vx_req_sessiongroup_control_recording_t_filename_set(Long var0, vx_req_sessiongroup_control_recording_t var2, String var3)

    native Int vx_req_sessiongroup_control_recording_t_loop_mode_duration_seconds_get(Long var0, vx_req_sessiongroup_control_recording_t var2)

    native Unit vx_req_sessiongroup_control_recording_t_loop_mode_duration_seconds_set(Long var0, vx_req_sessiongroup_control_recording_t var2, Int var3)

    native Int vx_req_sessiongroup_control_recording_t_recording_control_type_get(Long var0, vx_req_sessiongroup_control_recording_t var2)

    native Unit vx_req_sessiongroup_control_recording_t_recording_control_type_set(Long var0, vx_req_sessiongroup_control_recording_t var2, Int var3)

    native String vx_req_sessiongroup_control_recording_t_sessiongroup_handle_get(Long var0, vx_req_sessiongroup_control_recording_t var2)

    native Unit vx_req_sessiongroup_control_recording_t_sessiongroup_handle_set(Long var0, vx_req_sessiongroup_control_recording_t var2, String var3)

    native Unit vx_req_sessiongroup_create_create(Long var0)

    native String vx_req_sessiongroup_create_t_account_handle_get(Long var0, vx_req_sessiongroup_create_t var2)

    native Unit vx_req_sessiongroup_create_t_account_handle_set(Long var0, vx_req_sessiongroup_create_t var2, String var3)

    native String vx_req_sessiongroup_create_t_alias_username_get(Long var0, vx_req_sessiongroup_create_t var2)

    native Unit vx_req_sessiongroup_create_t_alias_username_set(Long var0, vx_req_sessiongroup_create_t var2, String var3)

    native Long vx_req_sessiongroup_create_t_base_get(Long var0, vx_req_sessiongroup_create_t var2)

    native Unit vx_req_sessiongroup_create_t_base_set(Long var0, vx_req_sessiongroup_create_t var2, Long var3, vx_req_base_t var5)

    native String vx_req_sessiongroup_create_t_capture_device_id_get(Long var0, vx_req_sessiongroup_create_t var2)

    native Unit vx_req_sessiongroup_create_t_capture_device_id_set(Long var0, vx_req_sessiongroup_create_t var2, String var3)

    native Int vx_req_sessiongroup_create_t_loop_mode_duration_seconds_get(Long var0, vx_req_sessiongroup_create_t var2)

    native Unit vx_req_sessiongroup_create_t_loop_mode_duration_seconds_set(Long var0, vx_req_sessiongroup_create_t var2, Int var3)

    native String vx_req_sessiongroup_create_t_render_device_id_get(Long var0, vx_req_sessiongroup_create_t var2)

    native Unit vx_req_sessiongroup_create_t_render_device_id_set(Long var0, vx_req_sessiongroup_create_t var2, String var3)

    native Int vx_req_sessiongroup_create_t_type_get(Long var0, vx_req_sessiongroup_create_t var2)

    native Unit vx_req_sessiongroup_create_t_type_set(Long var0, vx_req_sessiongroup_create_t var2, Int var3)

    native Unit vx_req_sessiongroup_get_stats_create(Long var0)

    native Long vx_req_sessiongroup_get_stats_t_base_get(Long var0, vx_req_sessiongroup_get_stats_t var2)

    native Unit vx_req_sessiongroup_get_stats_t_base_set(Long var0, vx_req_sessiongroup_get_stats_t var2, Long var3, vx_req_base_t var5)

    native String vx_req_sessiongroup_get_stats_t_sessiongroup_handle_get(Long var0, vx_req_sessiongroup_get_stats_t var2)

    native Unit vx_req_sessiongroup_get_stats_t_sessiongroup_handle_set(Long var0, vx_req_sessiongroup_get_stats_t var2, String var3)

    native Unit vx_req_sessiongroup_remove_session_create(Long var0)

    native Long vx_req_sessiongroup_remove_session_t_base_get(Long var0, vx_req_sessiongroup_remove_session_t var2)

    native Unit vx_req_sessiongroup_remove_session_t_base_set(Long var0, vx_req_sessiongroup_remove_session_t var2, Long var3, vx_req_base_t var5)

    native String vx_req_sessiongroup_remove_session_t_session_handle_get(Long var0, vx_req_sessiongroup_remove_session_t var2)

    native Unit vx_req_sessiongroup_remove_session_t_session_handle_set(Long var0, vx_req_sessiongroup_remove_session_t var2, String var3)

    native String vx_req_sessiongroup_remove_session_t_sessiongroup_handle_get(Long var0, vx_req_sessiongroup_remove_session_t var2)

    native Unit vx_req_sessiongroup_remove_session_t_sessiongroup_handle_set(Long var0, vx_req_sessiongroup_remove_session_t var2, String var3)

    native Unit vx_req_sessiongroup_reset_focus_create(Long var0)

    native Long vx_req_sessiongroup_reset_focus_t_base_get(Long var0, vx_req_sessiongroup_reset_focus_t var2)

    native Unit vx_req_sessiongroup_reset_focus_t_base_set(Long var0, vx_req_sessiongroup_reset_focus_t var2, Long var3, vx_req_base_t var5)

    native String vx_req_sessiongroup_reset_focus_t_sessiongroup_handle_get(Long var0, vx_req_sessiongroup_reset_focus_t var2)

    native Unit vx_req_sessiongroup_reset_focus_t_sessiongroup_handle_set(Long var0, vx_req_sessiongroup_reset_focus_t var2, String var3)

    native Unit vx_req_sessiongroup_set_focus_create(Long var0)

    native Long vx_req_sessiongroup_set_focus_t_base_get(Long var0, vx_req_sessiongroup_set_focus_t var2)

    native Unit vx_req_sessiongroup_set_focus_t_base_set(Long var0, vx_req_sessiongroup_set_focus_t var2, Long var3, vx_req_base_t var5)

    native String vx_req_sessiongroup_set_focus_t_session_handle_get(Long var0, vx_req_sessiongroup_set_focus_t var2)

    native Unit vx_req_sessiongroup_set_focus_t_session_handle_set(Long var0, vx_req_sessiongroup_set_focus_t var2, String var3)

    native String vx_req_sessiongroup_set_focus_t_sessiongroup_handle_get(Long var0, vx_req_sessiongroup_set_focus_t var2)

    native Unit vx_req_sessiongroup_set_focus_t_sessiongroup_handle_set(Long var0, vx_req_sessiongroup_set_focus_t var2, String var3)

    native Unit vx_req_sessiongroup_set_playback_options_create(Long var0)

    native Long vx_req_sessiongroup_set_playback_options_t_base_get(Long var0, vx_req_sessiongroup_set_playback_options_t var2)

    native Unit vx_req_sessiongroup_set_playback_options_t_base_set(Long var0, vx_req_sessiongroup_set_playback_options_t var2, Long var3, vx_req_base_t var5)

    native Int vx_req_sessiongroup_set_playback_options_t_frame_number_get(Long var0, vx_req_sessiongroup_set_playback_options_t var2)

    native Unit vx_req_sessiongroup_set_playback_options_t_frame_number_set(Long var0, vx_req_sessiongroup_set_playback_options_t var2, Int var3)

    native Int vx_req_sessiongroup_set_playback_options_t_playback_mode_get(Long var0, vx_req_sessiongroup_set_playback_options_t var2)

    native Unit vx_req_sessiongroup_set_playback_options_t_playback_mode_set(Long var0, vx_req_sessiongroup_set_playback_options_t var2, Int var3)

    native Double vx_req_sessiongroup_set_playback_options_t_playback_speed_get(Long var0, vx_req_sessiongroup_set_playback_options_t var2)

    native Unit vx_req_sessiongroup_set_playback_options_t_playback_speed_set(Long var0, vx_req_sessiongroup_set_playback_options_t var2, Double var3)

    native String vx_req_sessiongroup_set_playback_options_t_sessiongroup_handle_get(Long var0, vx_req_sessiongroup_set_playback_options_t var2)

    native Unit vx_req_sessiongroup_set_playback_options_t_sessiongroup_handle_set(Long var0, vx_req_sessiongroup_set_playback_options_t var2, String var3)

    native Unit vx_req_sessiongroup_set_session_3d_position_create(Long var0)

    native Long vx_req_sessiongroup_set_session_3d_position_t_base_get(Long var0, vx_req_sessiongroup_set_session_3d_position_t var2)

    native Unit vx_req_sessiongroup_set_session_3d_position_t_base_set(Long var0, vx_req_sessiongroup_set_session_3d_position_t var2, Long var3, vx_req_base_t var5)

    native Double vx_req_sessiongroup_set_session_3d_position_t_get_speaker_position_item(Long var0, vx_req_sessiongroup_set_session_3d_position_t var2, Int var3)

    native String vx_req_sessiongroup_set_session_3d_position_t_session_handle_get(Long var0, vx_req_sessiongroup_set_session_3d_position_t var2)

    native Unit vx_req_sessiongroup_set_session_3d_position_t_session_handle_set(Long var0, vx_req_sessiongroup_set_session_3d_position_t var2, String var3)

    native String vx_req_sessiongroup_set_session_3d_position_t_sessiongroup_handle_get(Long var0, vx_req_sessiongroup_set_session_3d_position_t var2)

    native Unit vx_req_sessiongroup_set_session_3d_position_t_sessiongroup_handle_set(Long var0, vx_req_sessiongroup_set_session_3d_position_t var2, String var3)

    native Unit vx_req_sessiongroup_set_session_3d_position_t_set_speaker_position_item(Long var0, vx_req_sessiongroup_set_session_3d_position_t var2, Int var3, Double var4)

    native Long vx_req_sessiongroup_set_session_3d_position_t_speaker_at_orientation_get(Long var0, vx_req_sessiongroup_set_session_3d_position_t var2)

    native Unit vx_req_sessiongroup_set_session_3d_position_t_speaker_at_orientation_set(Long var0, vx_req_sessiongroup_set_session_3d_position_t var2, Long var3)

    native Long vx_req_sessiongroup_set_session_3d_position_t_speaker_position_get(Long var0, vx_req_sessiongroup_set_session_3d_position_t var2)

    native Unit vx_req_sessiongroup_set_session_3d_position_t_speaker_position_set(Long var0, vx_req_sessiongroup_set_session_3d_position_t var2, Long var3)

    native Unit vx_req_sessiongroup_set_tx_all_sessions_create(Long var0)

    native Long vx_req_sessiongroup_set_tx_all_sessions_t_base_get(Long var0, vx_req_sessiongroup_set_tx_all_sessions_t var2)

    native Unit vx_req_sessiongroup_set_tx_all_sessions_t_base_set(Long var0, vx_req_sessiongroup_set_tx_all_sessions_t var2, Long var3, vx_req_base_t var5)

    native String vx_req_sessiongroup_set_tx_all_sessions_t_sessiongroup_handle_get(Long var0, vx_req_sessiongroup_set_tx_all_sessions_t var2)

    native Unit vx_req_sessiongroup_set_tx_all_sessions_t_sessiongroup_handle_set(Long var0, vx_req_sessiongroup_set_tx_all_sessions_t var2, String var3)

    native Unit vx_req_sessiongroup_set_tx_no_session_create(Long var0)

    native Long vx_req_sessiongroup_set_tx_no_session_t_base_get(Long var0, vx_req_sessiongroup_set_tx_no_session_t var2)

    native Unit vx_req_sessiongroup_set_tx_no_session_t_base_set(Long var0, vx_req_sessiongroup_set_tx_no_session_t var2, Long var3, vx_req_base_t var5)

    native String vx_req_sessiongroup_set_tx_no_session_t_sessiongroup_handle_get(Long var0, vx_req_sessiongroup_set_tx_no_session_t var2)

    native Unit vx_req_sessiongroup_set_tx_no_session_t_sessiongroup_handle_set(Long var0, vx_req_sessiongroup_set_tx_no_session_t var2, String var3)

    native Unit vx_req_sessiongroup_set_tx_session_create(Long var0)

    native Long vx_req_sessiongroup_set_tx_session_t_base_get(Long var0, vx_req_sessiongroup_set_tx_session_t var2)

    native Unit vx_req_sessiongroup_set_tx_session_t_base_set(Long var0, vx_req_sessiongroup_set_tx_session_t var2, Long var3, vx_req_base_t var5)

    native String vx_req_sessiongroup_set_tx_session_t_session_handle_get(Long var0, vx_req_sessiongroup_set_tx_session_t var2)

    native Unit vx_req_sessiongroup_set_tx_session_t_session_handle_set(Long var0, vx_req_sessiongroup_set_tx_session_t var2, String var3)

    native String vx_req_sessiongroup_set_tx_session_t_sessiongroup_handle_get(Long var0, vx_req_sessiongroup_set_tx_session_t var2)

    native Unit vx_req_sessiongroup_set_tx_session_t_sessiongroup_handle_set(Long var0, vx_req_sessiongroup_set_tx_session_t var2, String var3)

    native Unit vx_req_sessiongroup_terminate_create(Long var0)

    native Long vx_req_sessiongroup_terminate_t_base_get(Long var0, vx_req_sessiongroup_terminate_t var2)

    native Unit vx_req_sessiongroup_terminate_t_base_set(Long var0, vx_req_sessiongroup_terminate_t var2, Long var3, vx_req_base_t var5)

    native String vx_req_sessiongroup_terminate_t_sessiongroup_handle_get(Long var0, vx_req_sessiongroup_terminate_t var2)

    native Unit vx_req_sessiongroup_terminate_t_sessiongroup_handle_set(Long var0, vx_req_sessiongroup_terminate_t var2, String var3)

    native Unit vx_req_sessiongroup_unset_focus_create(Long var0)

    native Long vx_req_sessiongroup_unset_focus_t_base_get(Long var0, vx_req_sessiongroup_unset_focus_t var2)

    native Unit vx_req_sessiongroup_unset_focus_t_base_set(Long var0, vx_req_sessiongroup_unset_focus_t var2, Long var3, vx_req_base_t var5)

    native String vx_req_sessiongroup_unset_focus_t_session_handle_get(Long var0, vx_req_sessiongroup_unset_focus_t var2)

    native Unit vx_req_sessiongroup_unset_focus_t_session_handle_set(Long var0, vx_req_sessiongroup_unset_focus_t var2, String var3)

    native String vx_req_sessiongroup_unset_focus_t_sessiongroup_handle_get(Long var0, vx_req_sessiongroup_unset_focus_t var2)

    native Unit vx_req_sessiongroup_unset_focus_t_sessiongroup_handle_set(Long var0, vx_req_sessiongroup_unset_focus_t var2, String var3)

    native Unit vx_request_to_xml(Long var0, Long var2)

    native String vx_resp_account_anonymous_login_t_account_handle_get(Long var0, vx_resp_account_anonymous_login_t var2)

    native Unit vx_resp_account_anonymous_login_t_account_handle_set(Long var0, vx_resp_account_anonymous_login_t var2, String var3)

    native Int vx_resp_account_anonymous_login_t_account_id_get(Long var0, vx_resp_account_anonymous_login_t var2)

    native Unit vx_resp_account_anonymous_login_t_account_id_set(Long var0, vx_resp_account_anonymous_login_t var2, Int var3)

    native Long vx_resp_account_anonymous_login_t_base_get(Long var0, vx_resp_account_anonymous_login_t var2)

    native Unit vx_resp_account_anonymous_login_t_base_set(Long var0, vx_resp_account_anonymous_login_t var2, Long var3, vx_resp_base_t var5)

    native String vx_resp_account_anonymous_login_t_displayname_get(Long var0, vx_resp_account_anonymous_login_t var2)

    native Unit vx_resp_account_anonymous_login_t_displayname_set(Long var0, vx_resp_account_anonymous_login_t var2, String var3)

    native String vx_resp_account_anonymous_login_t_uri_get(Long var0, vx_resp_account_anonymous_login_t var2)

    native Unit vx_resp_account_anonymous_login_t_uri_set(Long var0, vx_resp_account_anonymous_login_t var2, String var3)

    native String vx_resp_account_authtoken_login_t_account_handle_get(Long var0, vx_resp_account_authtoken_login_t var2)

    native Unit vx_resp_account_authtoken_login_t_account_handle_set(Long var0, vx_resp_account_authtoken_login_t var2, String var3)

    native Int vx_resp_account_authtoken_login_t_account_id_get(Long var0, vx_resp_account_authtoken_login_t var2)

    native Unit vx_resp_account_authtoken_login_t_account_id_set(Long var0, vx_resp_account_authtoken_login_t var2, Int var3)

    native Long vx_resp_account_authtoken_login_t_base_get(Long var0, vx_resp_account_authtoken_login_t var2)

    native Unit vx_resp_account_authtoken_login_t_base_set(Long var0, vx_resp_account_authtoken_login_t var2, Long var3, vx_resp_base_t var5)

    native String vx_resp_account_authtoken_login_t_display_name_get(Long var0, vx_resp_account_authtoken_login_t var2)

    native Unit vx_resp_account_authtoken_login_t_display_name_set(Long var0, vx_resp_account_authtoken_login_t var2, String var3)

    native Int vx_resp_account_authtoken_login_t_num_aliases_get(Long var0, vx_resp_account_authtoken_login_t var2)

    native Unit vx_resp_account_authtoken_login_t_num_aliases_set(Long var0, vx_resp_account_authtoken_login_t var2, Int var3)

    native String vx_resp_account_authtoken_login_t_uri_get(Long var0, vx_resp_account_authtoken_login_t var2)

    native Unit vx_resp_account_authtoken_login_t_uri_set(Long var0, vx_resp_account_authtoken_login_t var2, String var3)

    native String vx_resp_account_authtoken_login_t_user_name_get(Long var0, vx_resp_account_authtoken_login_t var2)

    native Unit vx_resp_account_authtoken_login_t_user_name_set(Long var0, vx_resp_account_authtoken_login_t var2, String var3)

    native Long vx_resp_account_buddy_delete_t_base_get(Long var0, vx_resp_account_buddy_delete_t var2)

    native Unit vx_resp_account_buddy_delete_t_base_set(Long var0, vx_resp_account_buddy_delete_t var2, Long var3, vx_resp_base_t var5)

    native Long vx_resp_account_buddy_search_t_base_get(Long var0, vx_resp_account_buddy_search_t var2)

    native Unit vx_resp_account_buddy_search_t_base_set(Long var0, vx_resp_account_buddy_search_t var2, Long var3, vx_resp_base_t var5)

    native Long vx_resp_account_buddy_search_t_buddies_get(Long var0, vx_resp_account_buddy_search_t var2)

    native Unit vx_resp_account_buddy_search_t_buddies_set(Long var0, vx_resp_account_buddy_search_t var2, Long var3)

    native Int vx_resp_account_buddy_search_t_buddy_count_get(Long var0, vx_resp_account_buddy_search_t var2)

    native Unit vx_resp_account_buddy_search_t_buddy_count_set(Long var0, vx_resp_account_buddy_search_t var2, Int var3)

    native Int vx_resp_account_buddy_search_t_from_get(Long var0, vx_resp_account_buddy_search_t var2)

    native Unit vx_resp_account_buddy_search_t_from_set(Long var0, vx_resp_account_buddy_search_t var2, Int var3)

    native Long vx_resp_account_buddy_search_t_get_buddies_item(Long var0, vx_resp_account_buddy_search_t var2, Int var3)

    native Int vx_resp_account_buddy_search_t_page_get(Long var0, vx_resp_account_buddy_search_t var2)

    native Unit vx_resp_account_buddy_search_t_page_set(Long var0, vx_resp_account_buddy_search_t var2, Int var3)

    native Int vx_resp_account_buddy_search_t_to_get(Long var0, vx_resp_account_buddy_search_t var2)

    native Unit vx_resp_account_buddy_search_t_to_set(Long var0, vx_resp_account_buddy_search_t var2, Int var3)

    native Int vx_resp_account_buddy_set_t_account_id_get(Long var0, vx_resp_account_buddy_set_t var2)

    native Unit vx_resp_account_buddy_set_t_account_id_set(Long var0, vx_resp_account_buddy_set_t var2, Int var3)

    native Long vx_resp_account_buddy_set_t_base_get(Long var0, vx_resp_account_buddy_set_t var2)

    native Unit vx_resp_account_buddy_set_t_base_set(Long var0, vx_resp_account_buddy_set_t var2, Long var3, vx_resp_base_t var5)

    native Long vx_resp_account_buddygroup_delete_t_base_get(Long var0, vx_resp_account_buddygroup_delete_t var2)

    native Unit vx_resp_account_buddygroup_delete_t_base_set(Long var0, vx_resp_account_buddygroup_delete_t var2, Long var3, vx_resp_base_t var5)

    native Long vx_resp_account_buddygroup_set_t_base_get(Long var0, vx_resp_account_buddygroup_set_t var2)

    native Unit vx_resp_account_buddygroup_set_t_base_set(Long var0, vx_resp_account_buddygroup_set_t var2, Long var3, vx_resp_base_t var5)

    native Int vx_resp_account_buddygroup_set_t_group_id_get(Long var0, vx_resp_account_buddygroup_set_t var2)

    native Unit vx_resp_account_buddygroup_set_t_group_id_set(Long var0, vx_resp_account_buddygroup_set_t var2, Int var3)

    native Long vx_resp_account_channel_add_acl_t_base_get(Long var0, vx_resp_account_channel_add_acl_t var2)

    native Unit vx_resp_account_channel_add_acl_t_base_set(Long var0, vx_resp_account_channel_add_acl_t var2, Long var3, vx_resp_base_t var5)

    native Long vx_resp_account_channel_add_moderator_t_base_get(Long var0, vx_resp_account_channel_add_moderator_t var2)

    native Unit vx_resp_account_channel_add_moderator_t_base_set(Long var0, vx_resp_account_channel_add_moderator_t var2, Long var3, vx_resp_base_t var5)

    native Long vx_resp_account_channel_change_owner_t_base_get(Long var0, vx_resp_account_channel_change_owner_t var2)

    native Unit vx_resp_account_channel_change_owner_t_base_set(Long var0, vx_resp_account_channel_change_owner_t var2, Long var3, vx_resp_base_t var5)

    native Long vx_resp_account_channel_create_t_base_get(Long var0, vx_resp_account_channel_create_t var2)

    native Unit vx_resp_account_channel_create_t_base_set(Long var0, vx_resp_account_channel_create_t var2, Long var3, vx_resp_base_t var5)

    native String vx_resp_account_channel_create_t_channel_uri_get(Long var0, vx_resp_account_channel_create_t var2)

    native Unit vx_resp_account_channel_create_t_channel_uri_set(Long var0, vx_resp_account_channel_create_t var2, String var3)

    native Long vx_resp_account_channel_delete_t_base_get(Long var0, vx_resp_account_channel_delete_t var2)

    native Unit vx_resp_account_channel_delete_t_base_set(Long var0, vx_resp_account_channel_delete_t var2, Long var3, vx_resp_base_t var5)

    native Long vx_resp_account_channel_favorite_delete_t_base_get(Long var0, vx_resp_account_channel_favorite_delete_t var2)

    native Unit vx_resp_account_channel_favorite_delete_t_base_set(Long var0, vx_resp_account_channel_favorite_delete_t var2, Long var3, vx_resp_base_t var5)

    native Long vx_resp_account_channel_favorite_group_delete_t_base_get(Long var0, vx_resp_account_channel_favorite_group_delete_t var2)

    native Unit vx_resp_account_channel_favorite_group_delete_t_base_set(Long var0, vx_resp_account_channel_favorite_group_delete_t var2, Long var3, vx_resp_base_t var5)

    native Long vx_resp_account_channel_favorite_group_set_t_base_get(Long var0, vx_resp_account_channel_favorite_group_set_t var2)

    native Unit vx_resp_account_channel_favorite_group_set_t_base_set(Long var0, vx_resp_account_channel_favorite_group_set_t var2, Long var3, vx_resp_base_t var5)

    native Int vx_resp_account_channel_favorite_group_set_t_group_id_get(Long var0, vx_resp_account_channel_favorite_group_set_t var2)

    native Unit vx_resp_account_channel_favorite_group_set_t_group_id_set(Long var0, vx_resp_account_channel_favorite_group_set_t var2, Int var3)

    native Long vx_resp_account_channel_favorite_set_t_base_get(Long var0, vx_resp_account_channel_favorite_set_t var2)

    native Unit vx_resp_account_channel_favorite_set_t_base_set(Long var0, vx_resp_account_channel_favorite_set_t var2, Long var3, vx_resp_base_t var5)

    native Int vx_resp_account_channel_favorite_set_t_channel_favorite_id_get(Long var0, vx_resp_account_channel_favorite_set_t var2)

    native Unit vx_resp_account_channel_favorite_set_t_channel_favorite_id_set(Long var0, vx_resp_account_channel_favorite_set_t var2, Int var3)

    native Long vx_resp_account_channel_favorites_get_list_t_base_get(Long var0, vx_resp_account_channel_favorites_get_list_t var2)

    native Unit vx_resp_account_channel_favorites_get_list_t_base_set(Long var0, vx_resp_account_channel_favorites_get_list_t var2, Long var3, vx_resp_base_t var5)

    native Int vx_resp_account_channel_favorites_get_list_t_favorite_count_get(Long var0, vx_resp_account_channel_favorites_get_list_t var2)

    native Unit vx_resp_account_channel_favorites_get_list_t_favorite_count_set(Long var0, vx_resp_account_channel_favorites_get_list_t var2, Int var3)

    native Long vx_resp_account_channel_favorites_get_list_t_favorites_get(Long var0, vx_resp_account_channel_favorites_get_list_t var2)

    native Unit vx_resp_account_channel_favorites_get_list_t_favorites_set(Long var0, vx_resp_account_channel_favorites_get_list_t var2, Long var3)

    native Long vx_resp_account_channel_favorites_get_list_t_get_favorites_item(Long var0, vx_resp_account_channel_favorites_get_list_t var2, Int var3)

    native Long vx_resp_account_channel_favorites_get_list_t_get_groups_item(Long var0, vx_resp_account_channel_favorites_get_list_t var2, Int var3)

    native Int vx_resp_account_channel_favorites_get_list_t_group_count_get(Long var0, vx_resp_account_channel_favorites_get_list_t var2)

    native Unit vx_resp_account_channel_favorites_get_list_t_group_count_set(Long var0, vx_resp_account_channel_favorites_get_list_t var2, Int var3)

    native Long vx_resp_account_channel_favorites_get_list_t_groups_get(Long var0, vx_resp_account_channel_favorites_get_list_t var2)

    native Unit vx_resp_account_channel_favorites_get_list_t_groups_set(Long var0, vx_resp_account_channel_favorites_get_list_t var2, Long var3)

    native Long vx_resp_account_channel_get_acl_t_base_get(Long var0, vx_resp_account_channel_get_acl_t var2)

    native Unit vx_resp_account_channel_get_acl_t_base_set(Long var0, vx_resp_account_channel_get_acl_t var2, Long var3, vx_resp_base_t var5)

    native Long vx_resp_account_channel_get_acl_t_get_participants_item(Long var0, vx_resp_account_channel_get_acl_t var2, Int var3)

    native Long vx_resp_account_channel_get_acl_t_participants_get(Long var0, vx_resp_account_channel_get_acl_t var2)

    native Unit vx_resp_account_channel_get_acl_t_participants_set(Long var0, vx_resp_account_channel_get_acl_t var2, Long var3)

    native Int vx_resp_account_channel_get_acl_t_participants_size_get(Long var0, vx_resp_account_channel_get_acl_t var2)

    native Unit vx_resp_account_channel_get_acl_t_participants_size_set(Long var0, vx_resp_account_channel_get_acl_t var2, Int var3)

    native Long vx_resp_account_channel_get_info_t_base_get(Long var0, vx_resp_account_channel_get_info_t var2)

    native Unit vx_resp_account_channel_get_info_t_base_set(Long var0, vx_resp_account_channel_get_info_t var2, Long var3, vx_resp_base_t var5)

    native Long vx_resp_account_channel_get_info_t_channel_get(Long var0, vx_resp_account_channel_get_info_t var2)

    native Unit vx_resp_account_channel_get_info_t_channel_set(Long var0, vx_resp_account_channel_get_info_t var2, Long var3, vx_channel_t var5)

    native Long vx_resp_account_channel_get_moderators_t_base_get(Long var0, vx_resp_account_channel_get_moderators_t var2)

    native Unit vx_resp_account_channel_get_moderators_t_base_set(Long var0, vx_resp_account_channel_get_moderators_t var2, Long var3, vx_resp_base_t var5)

    native Long vx_resp_account_channel_get_moderators_t_get_participants_item(Long var0, vx_resp_account_channel_get_moderators_t var2, Int var3)

    native Long vx_resp_account_channel_get_moderators_t_participants_get(Long var0, vx_resp_account_channel_get_moderators_t var2)

    native Unit vx_resp_account_channel_get_moderators_t_participants_set(Long var0, vx_resp_account_channel_get_moderators_t var2, Long var3)

    native Int vx_resp_account_channel_get_moderators_t_participants_size_get(Long var0, vx_resp_account_channel_get_moderators_t var2)

    native Unit vx_resp_account_channel_get_moderators_t_participants_size_set(Long var0, vx_resp_account_channel_get_moderators_t var2, Int var3)

    native Long vx_resp_account_channel_get_participants_t_base_get(Long var0, vx_resp_account_channel_get_participants_t var2)

    native Unit vx_resp_account_channel_get_participants_t_base_set(Long var0, vx_resp_account_channel_get_participants_t var2, Long var3, vx_resp_base_t var5)

    native Int vx_resp_account_channel_get_participants_t_from_get(Long var0, vx_resp_account_channel_get_participants_t var2)

    native Unit vx_resp_account_channel_get_participants_t_from_set(Long var0, vx_resp_account_channel_get_participants_t var2, Int var3)

    native Long vx_resp_account_channel_get_participants_t_get_participants_item(Long var0, vx_resp_account_channel_get_participants_t var2, Int var3)

    native Int vx_resp_account_channel_get_participants_t_page_get(Long var0, vx_resp_account_channel_get_participants_t var2)

    native Unit vx_resp_account_channel_get_participants_t_page_set(Long var0, vx_resp_account_channel_get_participants_t var2, Int var3)

    native Int vx_resp_account_channel_get_participants_t_participant_count_get(Long var0, vx_resp_account_channel_get_participants_t var2)

    native Unit vx_resp_account_channel_get_participants_t_participant_count_set(Long var0, vx_resp_account_channel_get_participants_t var2, Int var3)

    native Long vx_resp_account_channel_get_participants_t_participants_get(Long var0, vx_resp_account_channel_get_participants_t var2)

    native Unit vx_resp_account_channel_get_participants_t_participants_set(Long var0, vx_resp_account_channel_get_participants_t var2, Long var3)

    native Int vx_resp_account_channel_get_participants_t_to_get(Long var0, vx_resp_account_channel_get_participants_t var2)

    native Unit vx_resp_account_channel_get_participants_t_to_set(Long var0, vx_resp_account_channel_get_participants_t var2, Int var3)

    native Int vx_resp_account_channel_get_participants_t_total_get(Long var0, vx_resp_account_channel_get_participants_t var2)

    native Unit vx_resp_account_channel_get_participants_t_total_set(Long var0, vx_resp_account_channel_get_participants_t var2, Int var3)

    native Long vx_resp_account_channel_remove_acl_t_base_get(Long var0, vx_resp_account_channel_remove_acl_t var2)

    native Unit vx_resp_account_channel_remove_acl_t_base_set(Long var0, vx_resp_account_channel_remove_acl_t var2, Long var3, vx_resp_base_t var5)

    native Long vx_resp_account_channel_remove_moderator_t_base_get(Long var0, vx_resp_account_channel_remove_moderator_t var2)

    native Unit vx_resp_account_channel_remove_moderator_t_base_set(Long var0, vx_resp_account_channel_remove_moderator_t var2, Long var3, vx_resp_base_t var5)

    native Long vx_resp_account_channel_search_t_base_get(Long var0, vx_resp_account_channel_search_t var2)

    native Unit vx_resp_account_channel_search_t_base_set(Long var0, vx_resp_account_channel_search_t var2, Long var3, vx_resp_base_t var5)

    native Int vx_resp_account_channel_search_t_channel_count_get(Long var0, vx_resp_account_channel_search_t var2)

    native Unit vx_resp_account_channel_search_t_channel_count_set(Long var0, vx_resp_account_channel_search_t var2, Int var3)

    native Long vx_resp_account_channel_search_t_channels_get(Long var0, vx_resp_account_channel_search_t var2)

    native Unit vx_resp_account_channel_search_t_channels_set(Long var0, vx_resp_account_channel_search_t var2, Long var3)

    native Int vx_resp_account_channel_search_t_from_get(Long var0, vx_resp_account_channel_search_t var2)

    native Unit vx_resp_account_channel_search_t_from_set(Long var0, vx_resp_account_channel_search_t var2, Int var3)

    native Long vx_resp_account_channel_search_t_get_channels_item(Long var0, vx_resp_account_channel_search_t var2, Int var3)

    native Int vx_resp_account_channel_search_t_page_get(Long var0, vx_resp_account_channel_search_t var2)

    native Unit vx_resp_account_channel_search_t_page_set(Long var0, vx_resp_account_channel_search_t var2, Int var3)

    native Int vx_resp_account_channel_search_t_to_get(Long var0, vx_resp_account_channel_search_t var2)

    native Unit vx_resp_account_channel_search_t_to_set(Long var0, vx_resp_account_channel_search_t var2, Int var3)

    native Long vx_resp_account_channel_update_t_base_get(Long var0, vx_resp_account_channel_update_t var2)

    native Unit vx_resp_account_channel_update_t_base_set(Long var0, vx_resp_account_channel_update_t var2, Long var3, vx_resp_base_t var5)

    native Long vx_resp_account_create_auto_accept_rule_t_base_get(Long var0, vx_resp_account_create_auto_accept_rule_t var2)

    native Unit vx_resp_account_create_auto_accept_rule_t_base_set(Long var0, vx_resp_account_create_auto_accept_rule_t var2, Long var3, vx_resp_base_t var5)

    native Long vx_resp_account_create_block_rule_t_base_get(Long var0, vx_resp_account_create_block_rule_t var2)

    native Unit vx_resp_account_create_block_rule_t_base_set(Long var0, vx_resp_account_create_block_rule_t var2, Long var3, vx_resp_base_t var5)

    native Long vx_resp_account_delete_auto_accept_rule_t_base_get(Long var0, vx_resp_account_delete_auto_accept_rule_t var2)

    native Unit vx_resp_account_delete_auto_accept_rule_t_base_set(Long var0, vx_resp_account_delete_auto_accept_rule_t var2, Long var3, vx_resp_base_t var5)

    native Long vx_resp_account_delete_block_rule_t_base_get(Long var0, vx_resp_account_delete_block_rule_t var2)

    native Unit vx_resp_account_delete_block_rule_t_base_set(Long var0, vx_resp_account_delete_block_rule_t var2, Long var3, vx_resp_base_t var5)

    native Long vx_resp_account_get_account_t_account_get(Long var0, vx_resp_account_get_account_t var2)

    native Unit vx_resp_account_get_account_t_account_set(Long var0, vx_resp_account_get_account_t var2, Long var3, vx_account_t var5)

    native Long vx_resp_account_get_account_t_base_get(Long var0, vx_resp_account_get_account_t var2)

    native Unit vx_resp_account_get_account_t_base_set(Long var0, vx_resp_account_get_account_t var2, Long var3, vx_resp_base_t var5)

    native Long vx_resp_account_get_session_fonts_t_base_get(Long var0, vx_resp_account_get_session_fonts_t var2)

    native Unit vx_resp_account_get_session_fonts_t_base_set(Long var0, vx_resp_account_get_session_fonts_t var2, Long var3, vx_resp_base_t var5)

    native Long vx_resp_account_get_session_fonts_t_get_session_fonts_item(Long var0, vx_resp_account_get_session_fonts_t var2, Int var3)

    native Int vx_resp_account_get_session_fonts_t_session_font_count_get(Long var0, vx_resp_account_get_session_fonts_t var2)

    native Unit vx_resp_account_get_session_fonts_t_session_font_count_set(Long var0, vx_resp_account_get_session_fonts_t var2, Int var3)

    native Long vx_resp_account_get_session_fonts_t_session_fonts_get(Long var0, vx_resp_account_get_session_fonts_t var2)

    native Unit vx_resp_account_get_session_fonts_t_session_fonts_set(Long var0, vx_resp_account_get_session_fonts_t var2, Long var3)

    native Long vx_resp_account_get_template_fonts_t_base_get(Long var0, vx_resp_account_get_template_fonts_t var2)

    native Unit vx_resp_account_get_template_fonts_t_base_set(Long var0, vx_resp_account_get_template_fonts_t var2, Long var3, vx_resp_base_t var5)

    native Long vx_resp_account_get_template_fonts_t_get_template_fonts_item(Long var0, vx_resp_account_get_template_fonts_t var2, Int var3)

    native Int vx_resp_account_get_template_fonts_t_template_font_count_get(Long var0, vx_resp_account_get_template_fonts_t var2)

    native Unit vx_resp_account_get_template_fonts_t_template_font_count_set(Long var0, vx_resp_account_get_template_fonts_t var2, Int var3)

    native Long vx_resp_account_get_template_fonts_t_template_fonts_get(Long var0, vx_resp_account_get_template_fonts_t var2)

    native Unit vx_resp_account_get_template_fonts_t_template_fonts_set(Long var0, vx_resp_account_get_template_fonts_t var2, Long var3)

    native Long vx_resp_account_list_auto_accept_rules_t_auto_accept_rules_get(Long var0, vx_resp_account_list_auto_accept_rules_t var2)

    native Unit vx_resp_account_list_auto_accept_rules_t_auto_accept_rules_set(Long var0, vx_resp_account_list_auto_accept_rules_t var2, Long var3)

    native Long vx_resp_account_list_auto_accept_rules_t_base_get(Long var0, vx_resp_account_list_auto_accept_rules_t var2)

    native Unit vx_resp_account_list_auto_accept_rules_t_base_set(Long var0, vx_resp_account_list_auto_accept_rules_t var2, Long var3, vx_resp_base_t var5)

    native Long vx_resp_account_list_auto_accept_rules_t_get_auto_accept_rules_item(Long var0, vx_resp_account_list_auto_accept_rules_t var2, Int var3)

    native Int vx_resp_account_list_auto_accept_rules_t_rule_count_get(Long var0, vx_resp_account_list_auto_accept_rules_t var2)

    native Unit vx_resp_account_list_auto_accept_rules_t_rule_count_set(Long var0, vx_resp_account_list_auto_accept_rules_t var2, Int var3)

    native Long vx_resp_account_list_block_rules_t_base_get(Long var0, vx_resp_account_list_block_rules_t var2)

    native Unit vx_resp_account_list_block_rules_t_base_set(Long var0, vx_resp_account_list_block_rules_t var2, Long var3, vx_resp_base_t var5)

    native Long vx_resp_account_list_block_rules_t_block_rules_get(Long var0, vx_resp_account_list_block_rules_t var2)

    native Unit vx_resp_account_list_block_rules_t_block_rules_set(Long var0, vx_resp_account_list_block_rules_t var2, Long var3)

    native Long vx_resp_account_list_block_rules_t_get_block_rules_item(Long var0, vx_resp_account_list_block_rules_t var2, Int var3)

    native Int vx_resp_account_list_block_rules_t_rule_count_get(Long var0, vx_resp_account_list_block_rules_t var2)

    native Unit vx_resp_account_list_block_rules_t_rule_count_set(Long var0, vx_resp_account_list_block_rules_t var2, Int var3)

    native Long vx_resp_account_list_buddies_and_groups_t_base_get(Long var0, vx_resp_account_list_buddies_and_groups_t var2)

    native Unit vx_resp_account_list_buddies_and_groups_t_base_set(Long var0, vx_resp_account_list_buddies_and_groups_t var2, Long var3, vx_resp_base_t var5)

    native Long vx_resp_account_list_buddies_and_groups_t_buddies_get(Long var0, vx_resp_account_list_buddies_and_groups_t var2)

    native Unit vx_resp_account_list_buddies_and_groups_t_buddies_set(Long var0, vx_resp_account_list_buddies_and_groups_t var2, Long var3)

    native Int vx_resp_account_list_buddies_and_groups_t_buddy_count_get(Long var0, vx_resp_account_list_buddies_and_groups_t var2)

    native Unit vx_resp_account_list_buddies_and_groups_t_buddy_count_set(Long var0, vx_resp_account_list_buddies_and_groups_t var2, Int var3)

    native Long vx_resp_account_list_buddies_and_groups_t_get_buddies_item(Long var0, vx_resp_account_list_buddies_and_groups_t var2, Int var3)

    native Long vx_resp_account_list_buddies_and_groups_t_get_groups_item(Long var0, vx_resp_account_list_buddies_and_groups_t var2, Int var3)

    native Int vx_resp_account_list_buddies_and_groups_t_group_count_get(Long var0, vx_resp_account_list_buddies_and_groups_t var2)

    native Unit vx_resp_account_list_buddies_and_groups_t_group_count_set(Long var0, vx_resp_account_list_buddies_and_groups_t var2, Int var3)

    native Long vx_resp_account_list_buddies_and_groups_t_groups_get(Long var0, vx_resp_account_list_buddies_and_groups_t var2)

    native Unit vx_resp_account_list_buddies_and_groups_t_groups_set(Long var0, vx_resp_account_list_buddies_and_groups_t var2, Long var3)

    native String vx_resp_account_login_t_account_handle_get(Long var0, vx_resp_account_login_t var2)

    native Unit vx_resp_account_login_t_account_handle_set(Long var0, vx_resp_account_login_t var2, String var3)

    native Int vx_resp_account_login_t_account_id_get(Long var0, vx_resp_account_login_t var2)

    native Unit vx_resp_account_login_t_account_id_set(Long var0, vx_resp_account_login_t var2, Int var3)

    native Long vx_resp_account_login_t_base_get(Long var0, vx_resp_account_login_t var2)

    native Unit vx_resp_account_login_t_base_set(Long var0, vx_resp_account_login_t var2, Long var3, vx_resp_base_t var5)

    native String vx_resp_account_login_t_display_name_get(Long var0, vx_resp_account_login_t var2)

    native Unit vx_resp_account_login_t_display_name_set(Long var0, vx_resp_account_login_t var2, String var3)

    native Int vx_resp_account_login_t_num_aliases_get(Long var0, vx_resp_account_login_t var2)

    native Unit vx_resp_account_login_t_num_aliases_set(Long var0, vx_resp_account_login_t var2, Int var3)

    native String vx_resp_account_login_t_uri_get(Long var0, vx_resp_account_login_t var2)

    native Unit vx_resp_account_login_t_uri_set(Long var0, vx_resp_account_login_t var2, String var3)

    native Long vx_resp_account_logout_t_base_get(Long var0, vx_resp_account_logout_t var2)

    native Unit vx_resp_account_logout_t_base_set(Long var0, vx_resp_account_logout_t var2, Long var3, vx_resp_base_t var5)

    native Long vx_resp_account_post_crash_dump_t_base_get(Long var0, vx_resp_account_post_crash_dump_t var2)

    native Unit vx_resp_account_post_crash_dump_t_base_set(Long var0, vx_resp_account_post_crash_dump_t var2, Long var3, vx_resp_base_t var5)

    native Long vx_resp_account_send_message_t_base_get(Long var0, vx_resp_account_send_message_t var2)

    native Unit vx_resp_account_send_message_t_base_set(Long var0, vx_resp_account_send_message_t var2, Long var3, vx_resp_base_t var5)

    native Long vx_resp_account_send_sms_t_base_get(Long var0, vx_resp_account_send_sms_t var2)

    native Unit vx_resp_account_send_sms_t_base_set(Long var0, vx_resp_account_send_sms_t var2, Long var3, vx_resp_base_t var5)

    native Long vx_resp_account_send_subscription_reply_t_base_get(Long var0, vx_resp_account_send_subscription_reply_t var2)

    native Unit vx_resp_account_send_subscription_reply_t_base_set(Long var0, vx_resp_account_send_subscription_reply_t var2, Long var3, vx_resp_base_t var5)

    native Long vx_resp_account_send_user_app_data_t_base_get(Long var0, vx_resp_account_send_user_app_data_t var2)

    native Unit vx_resp_account_send_user_app_data_t_base_set(Long var0, vx_resp_account_send_user_app_data_t var2, Long var3, vx_resp_base_t var5)

    native Long vx_resp_account_set_login_properties_t_base_get(Long var0, vx_resp_account_set_login_properties_t var2)

    native Unit vx_resp_account_set_login_properties_t_base_set(Long var0, vx_resp_account_set_login_properties_t var2, Long var3, vx_resp_base_t var5)

    native Long vx_resp_account_set_presence_t_base_get(Long var0, vx_resp_account_set_presence_t var2)

    native Unit vx_resp_account_set_presence_t_base_set(Long var0, vx_resp_account_set_presence_t var2, Long var3, vx_resp_base_t var5)

    native Long vx_resp_account_update_account_t_base_get(Long var0, vx_resp_account_update_account_t var2)

    native Unit vx_resp_account_update_account_t_base_set(Long var0, vx_resp_account_update_account_t var2, Long var3, vx_resp_base_t var5)

    native Long vx_resp_account_web_call_t_base_get(Long var0, vx_resp_account_web_call_t var2)

    native Unit vx_resp_account_web_call_t_base_set(Long var0, vx_resp_account_web_call_t var2, Long var3, vx_resp_base_t var5)

    native String vx_resp_account_web_call_t_content_get(Long var0, vx_resp_account_web_call_t var2)

    native Int vx_resp_account_web_call_t_content_length_get(Long var0, vx_resp_account_web_call_t var2)

    native Unit vx_resp_account_web_call_t_content_length_set(Long var0, vx_resp_account_web_call_t var2, Int var3)

    native Unit vx_resp_account_web_call_t_content_set(Long var0, vx_resp_account_web_call_t var2, String var3)

    native String vx_resp_account_web_call_t_content_type_get(Long var0, vx_resp_account_web_call_t var2)

    native Unit vx_resp_account_web_call_t_content_type_set(Long var0, vx_resp_account_web_call_t var2, String var3)

    native Long vx_resp_aux_capture_audio_start_t_base_get(Long var0, vx_resp_aux_capture_audio_start_t var2)

    native Unit vx_resp_aux_capture_audio_start_t_base_set(Long var0, vx_resp_aux_capture_audio_start_t var2, Long var3, vx_resp_base_t var5)

    native Long vx_resp_aux_capture_audio_stop_t_audioBufferPtr_get(Long var0, vx_resp_aux_capture_audio_stop_t var2)

    native Unit vx_resp_aux_capture_audio_stop_t_audioBufferPtr_set(Long var0, vx_resp_aux_capture_audio_stop_t var2, Long var3)

    native Long vx_resp_aux_capture_audio_stop_t_base_get(Long var0, vx_resp_aux_capture_audio_stop_t var2)

    native Unit vx_resp_aux_capture_audio_stop_t_base_set(Long var0, vx_resp_aux_capture_audio_stop_t var2, Long var3, vx_resp_base_t var5)

    native Long vx_resp_aux_connectivity_info_t_base_get(Long var0, vx_resp_aux_connectivity_info_t var2)

    native Unit vx_resp_aux_connectivity_info_t_base_set(Long var0, vx_resp_aux_connectivity_info_t var2, Long var3, vx_resp_base_t var5)

    native Int vx_resp_aux_connectivity_info_t_count_get(Long var0, vx_resp_aux_connectivity_info_t var2)

    native Unit vx_resp_aux_connectivity_info_t_count_set(Long var0, vx_resp_aux_connectivity_info_t var2, Int var3)

    native Int vx_resp_aux_connectivity_info_t_echo_port_get(Long var0, vx_resp_aux_connectivity_info_t var2)

    native Unit vx_resp_aux_connectivity_info_t_echo_port_set(Long var0, vx_resp_aux_connectivity_info_t var2, Int var3)

    native String vx_resp_aux_connectivity_info_t_echo_server_get(Long var0, vx_resp_aux_connectivity_info_t var2)

    native Unit vx_resp_aux_connectivity_info_t_echo_server_set(Long var0, vx_resp_aux_connectivity_info_t var2, String var3)

    native Int vx_resp_aux_connectivity_info_t_first_sip_port_get(Long var0, vx_resp_aux_connectivity_info_t var2)

    native Unit vx_resp_aux_connectivity_info_t_first_sip_port_set(Long var0, vx_resp_aux_connectivity_info_t var2, Int var3)

    native Long vx_resp_aux_connectivity_info_t_get_test_results_item(Long var0, vx_resp_aux_connectivity_info_t var2, Int var3)

    native Int vx_resp_aux_connectivity_info_t_rtcp_port_get(Long var0, vx_resp_aux_connectivity_info_t var2)

    native Unit vx_resp_aux_connectivity_info_t_rtcp_port_set(Long var0, vx_resp_aux_connectivity_info_t var2, Int var3)

    native Int vx_resp_aux_connectivity_info_t_rtp_port_get(Long var0, vx_resp_aux_connectivity_info_t var2)

    native Unit vx_resp_aux_connectivity_info_t_rtp_port_set(Long var0, vx_resp_aux_connectivity_info_t var2, Int var3)

    native Int vx_resp_aux_connectivity_info_t_second_sip_port_get(Long var0, vx_resp_aux_connectivity_info_t var2)

    native Unit vx_resp_aux_connectivity_info_t_second_sip_port_set(Long var0, vx_resp_aux_connectivity_info_t var2, Int var3)

    native String vx_resp_aux_connectivity_info_t_stun_server_get(Long var0, vx_resp_aux_connectivity_info_t var2)

    native Unit vx_resp_aux_connectivity_info_t_stun_server_set(Long var0, vx_resp_aux_connectivity_info_t var2, String var3)

    native Long vx_resp_aux_connectivity_info_t_test_results_get(Long var0, vx_resp_aux_connectivity_info_t var2)

    native Unit vx_resp_aux_connectivity_info_t_test_results_set(Long var0, vx_resp_aux_connectivity_info_t var2, Long var3)

    native Int vx_resp_aux_connectivity_info_t_timeout_get(Long var0, vx_resp_aux_connectivity_info_t var2)

    native Unit vx_resp_aux_connectivity_info_t_timeout_set(Long var0, vx_resp_aux_connectivity_info_t var2, Int var3)

    native String vx_resp_aux_connectivity_info_t_well_known_ip_get(Long var0, vx_resp_aux_connectivity_info_t var2)

    native Unit vx_resp_aux_connectivity_info_t_well_known_ip_set(Long var0, vx_resp_aux_connectivity_info_t var2, String var3)

    native Long vx_resp_aux_create_account_t_base_get(Long var0, vx_resp_aux_create_account_t var2)

    native Unit vx_resp_aux_create_account_t_base_set(Long var0, vx_resp_aux_create_account_t var2, Long var3, vx_resp_base_t var5)

    native Long vx_resp_aux_deactivate_account_t_base_get(Long var0, vx_resp_aux_deactivate_account_t var2)

    native Unit vx_resp_aux_deactivate_account_t_base_set(Long var0, vx_resp_aux_deactivate_account_t var2, Long var3, vx_resp_base_t var5)

    native Long vx_resp_aux_diagnostic_state_dump_t_base_get(Long var0, vx_resp_aux_diagnostic_state_dump_t var2)

    native Unit vx_resp_aux_diagnostic_state_dump_t_base_set(Long var0, vx_resp_aux_diagnostic_state_dump_t var2, Long var3, vx_resp_base_t var5)

    native Long vx_resp_aux_diagnostic_state_dump_t_current_capture_device_get(Long var0, vx_resp_aux_diagnostic_state_dump_t var2)

    native Unit vx_resp_aux_diagnostic_state_dump_t_current_capture_device_set(Long var0, vx_resp_aux_diagnostic_state_dump_t var2, Long var3, vx_device_t var5)

    native Long vx_resp_aux_diagnostic_state_dump_t_current_render_device_get(Long var0, vx_resp_aux_diagnostic_state_dump_t var2)

    native Unit vx_resp_aux_diagnostic_state_dump_t_current_render_device_set(Long var0, vx_resp_aux_diagnostic_state_dump_t var2, Long var3, vx_device_t var5)

    native Long vx_resp_aux_diagnostic_state_dump_t_effective_capture_device_get(Long var0, vx_resp_aux_diagnostic_state_dump_t var2)

    native Unit vx_resp_aux_diagnostic_state_dump_t_effective_capture_device_set(Long var0, vx_resp_aux_diagnostic_state_dump_t var2, Long var3, vx_device_t var5)

    native Long vx_resp_aux_diagnostic_state_dump_t_effective_render_device_get(Long var0, vx_resp_aux_diagnostic_state_dump_t var2)

    native Unit vx_resp_aux_diagnostic_state_dump_t_effective_render_device_set(Long var0, vx_resp_aux_diagnostic_state_dump_t var2, Long var3, vx_device_t var5)

    native Long vx_resp_aux_diagnostic_state_dump_t_get_state_connectors_item(Long var0, vx_resp_aux_diagnostic_state_dump_t var2, Int var3)

    native Int vx_resp_aux_diagnostic_state_dump_t_state_connector_count_get(Long var0, vx_resp_aux_diagnostic_state_dump_t var2)

    native Unit vx_resp_aux_diagnostic_state_dump_t_state_connector_count_set(Long var0, vx_resp_aux_diagnostic_state_dump_t var2, Int var3)

    native Long vx_resp_aux_diagnostic_state_dump_t_state_connectors_get(Long var0, vx_resp_aux_diagnostic_state_dump_t var2)

    native Unit vx_resp_aux_diagnostic_state_dump_t_state_connectors_set(Long var0, vx_resp_aux_diagnostic_state_dump_t var2, Long var3)

    native Long vx_resp_aux_get_capture_devices_t_base_get(Long var0, vx_resp_aux_get_capture_devices_t var2)

    native Unit vx_resp_aux_get_capture_devices_t_base_set(Long var0, vx_resp_aux_get_capture_devices_t var2, Long var3, vx_resp_base_t var5)

    native Long vx_resp_aux_get_capture_devices_t_capture_devices_get(Long var0, vx_resp_aux_get_capture_devices_t var2)

    native Unit vx_resp_aux_get_capture_devices_t_capture_devices_set(Long var0, vx_resp_aux_get_capture_devices_t var2, Long var3)

    native Int vx_resp_aux_get_capture_devices_t_count_get(Long var0, vx_resp_aux_get_capture_devices_t var2)

    native Unit vx_resp_aux_get_capture_devices_t_count_set(Long var0, vx_resp_aux_get_capture_devices_t var2, Int var3)

    native Long vx_resp_aux_get_capture_devices_t_current_capture_device_get(Long var0, vx_resp_aux_get_capture_devices_t var2)

    native Unit vx_resp_aux_get_capture_devices_t_current_capture_device_set(Long var0, vx_resp_aux_get_capture_devices_t var2, Long var3, vx_device_t var5)

    native Long vx_resp_aux_get_capture_devices_t_effective_capture_device_get(Long var0, vx_resp_aux_get_capture_devices_t var2)

    native Unit vx_resp_aux_get_capture_devices_t_effective_capture_device_set(Long var0, vx_resp_aux_get_capture_devices_t var2, Long var3, vx_device_t var5)

    native Long vx_resp_aux_get_capture_devices_t_get_capture_devices_item(Long var0, vx_resp_aux_get_capture_devices_t var2, Int var3)

    native Long vx_resp_aux_get_mic_level_t_base_get(Long var0, vx_resp_aux_get_mic_level_t var2)

    native Unit vx_resp_aux_get_mic_level_t_base_set(Long var0, vx_resp_aux_get_mic_level_t var2, Long var3, vx_resp_base_t var5)

    native Int vx_resp_aux_get_mic_level_t_level_get(Long var0, vx_resp_aux_get_mic_level_t var2)

    native Unit vx_resp_aux_get_mic_level_t_level_set(Long var0, vx_resp_aux_get_mic_level_t var2, Int var3)

    native Long vx_resp_aux_get_render_devices_t_base_get(Long var0, vx_resp_aux_get_render_devices_t var2)

    native Unit vx_resp_aux_get_render_devices_t_base_set(Long var0, vx_resp_aux_get_render_devices_t var2, Long var3, vx_resp_base_t var5)

    native Int vx_resp_aux_get_render_devices_t_count_get(Long var0, vx_resp_aux_get_render_devices_t var2)

    native Unit vx_resp_aux_get_render_devices_t_count_set(Long var0, vx_resp_aux_get_render_devices_t var2, Int var3)

    native Long vx_resp_aux_get_render_devices_t_current_render_device_get(Long var0, vx_resp_aux_get_render_devices_t var2)

    native Unit vx_resp_aux_get_render_devices_t_current_render_device_set(Long var0, vx_resp_aux_get_render_devices_t var2, Long var3, vx_device_t var5)

    native Long vx_resp_aux_get_render_devices_t_effective_render_device_get(Long var0, vx_resp_aux_get_render_devices_t var2)

    native Unit vx_resp_aux_get_render_devices_t_effective_render_device_set(Long var0, vx_resp_aux_get_render_devices_t var2, Long var3, vx_device_t var5)

    native Long vx_resp_aux_get_render_devices_t_get_render_devices_item(Long var0, vx_resp_aux_get_render_devices_t var2, Int var3)

    native Long vx_resp_aux_get_render_devices_t_render_devices_get(Long var0, vx_resp_aux_get_render_devices_t var2)

    native Unit vx_resp_aux_get_render_devices_t_render_devices_set(Long var0, vx_resp_aux_get_render_devices_t var2, Long var3)

    native Long vx_resp_aux_get_speaker_level_t_base_get(Long var0, vx_resp_aux_get_speaker_level_t var2)

    native Unit vx_resp_aux_get_speaker_level_t_base_set(Long var0, vx_resp_aux_get_speaker_level_t var2, Long var3, vx_resp_base_t var5)

    native Int vx_resp_aux_get_speaker_level_t_level_get(Long var0, vx_resp_aux_get_speaker_level_t var2)

    native Unit vx_resp_aux_get_speaker_level_t_level_set(Long var0, vx_resp_aux_get_speaker_level_t var2, Int var3)

    native Long vx_resp_aux_get_vad_properties_t_base_get(Long var0, vx_resp_aux_get_vad_properties_t var2)

    native Unit vx_resp_aux_get_vad_properties_t_base_set(Long var0, vx_resp_aux_get_vad_properties_t var2, Long var3, vx_resp_base_t var5)

    native Int vx_resp_aux_get_vad_properties_t_vad_hangover_get(Long var0, vx_resp_aux_get_vad_properties_t var2)

    native Unit vx_resp_aux_get_vad_properties_t_vad_hangover_set(Long var0, vx_resp_aux_get_vad_properties_t var2, Int var3)

    native Int vx_resp_aux_get_vad_properties_t_vad_sensitivity_get(Long var0, vx_resp_aux_get_vad_properties_t var2)

    native Unit vx_resp_aux_get_vad_properties_t_vad_sensitivity_set(Long var0, vx_resp_aux_get_vad_properties_t var2, Int var3)

    native Long vx_resp_aux_global_monitor_keyboard_mouse_t_base_get(Long var0, vx_resp_aux_global_monitor_keyboard_mouse_t var2)

    native Unit vx_resp_aux_global_monitor_keyboard_mouse_t_base_set(Long var0, vx_resp_aux_global_monitor_keyboard_mouse_t var2, Long var3, vx_resp_base_t var5)

    native Long vx_resp_aux_notify_application_state_change_t_base_get(Long var0, vx_resp_aux_notify_application_state_change_t var2)

    native Unit vx_resp_aux_notify_application_state_change_t_base_set(Long var0, vx_resp_aux_notify_application_state_change_t var2, Long var3, vx_resp_base_t var5)

    native Long vx_resp_aux_play_audio_buffer_t_base_get(Long var0, vx_resp_aux_play_audio_buffer_t var2)

    native Unit vx_resp_aux_play_audio_buffer_t_base_set(Long var0, vx_resp_aux_play_audio_buffer_t var2, Long var3, vx_resp_base_t var5)

    native Long vx_resp_aux_reactivate_account_t_base_get(Long var0, vx_resp_aux_reactivate_account_t var2)

    native Unit vx_resp_aux_reactivate_account_t_base_set(Long var0, vx_resp_aux_reactivate_account_t var2, Long var3, vx_resp_base_t var5)

    native Long vx_resp_aux_render_audio_modify_t_base_get(Long var0, vx_resp_aux_render_audio_modify_t var2)

    native Unit vx_resp_aux_render_audio_modify_t_base_set(Long var0, vx_resp_aux_render_audio_modify_t var2, Long var3, vx_resp_base_t var5)

    native Long vx_resp_aux_render_audio_start_t_base_get(Long var0, vx_resp_aux_render_audio_start_t var2)

    native Unit vx_resp_aux_render_audio_start_t_base_set(Long var0, vx_resp_aux_render_audio_start_t var2, Long var3, vx_resp_base_t var5)

    native Long vx_resp_aux_render_audio_stop_t_base_get(Long var0, vx_resp_aux_render_audio_stop_t var2)

    native Unit vx_resp_aux_render_audio_stop_t_base_set(Long var0, vx_resp_aux_render_audio_stop_t var2, Long var3, vx_resp_base_t var5)

    native Long vx_resp_aux_reset_password_t_base_get(Long var0, vx_resp_aux_reset_password_t var2)

    native Unit vx_resp_aux_reset_password_t_base_set(Long var0, vx_resp_aux_reset_password_t var2, Long var3, vx_resp_base_t var5)

    native Long vx_resp_aux_set_capture_device_t_base_get(Long var0, vx_resp_aux_set_capture_device_t var2)

    native Unit vx_resp_aux_set_capture_device_t_base_set(Long var0, vx_resp_aux_set_capture_device_t var2, Long var3, vx_resp_base_t var5)

    native Long vx_resp_aux_set_idle_timeout_t_base_get(Long var0, vx_resp_aux_set_idle_timeout_t var2)

    native Unit vx_resp_aux_set_idle_timeout_t_base_set(Long var0, vx_resp_aux_set_idle_timeout_t var2, Long var3, vx_resp_base_t var5)

    native Long vx_resp_aux_set_mic_level_t_base_get(Long var0, vx_resp_aux_set_mic_level_t var2)

    native Unit vx_resp_aux_set_mic_level_t_base_set(Long var0, vx_resp_aux_set_mic_level_t var2, Long var3, vx_resp_base_t var5)

    native Long vx_resp_aux_set_render_device_t_base_get(Long var0, vx_resp_aux_set_render_device_t var2)

    native Unit vx_resp_aux_set_render_device_t_base_set(Long var0, vx_resp_aux_set_render_device_t var2, Long var3, vx_resp_base_t var5)

    native Long vx_resp_aux_set_speaker_level_t_base_get(Long var0, vx_resp_aux_set_speaker_level_t var2)

    native Unit vx_resp_aux_set_speaker_level_t_base_set(Long var0, vx_resp_aux_set_speaker_level_t var2, Long var3, vx_resp_base_t var5)

    native Long vx_resp_aux_set_vad_properties_t_base_get(Long var0, vx_resp_aux_set_vad_properties_t var2)

    native Unit vx_resp_aux_set_vad_properties_t_base_set(Long var0, vx_resp_aux_set_vad_properties_t var2, Long var3, vx_resp_base_t var5)

    native Long vx_resp_aux_start_buffer_capture_t_base_get(Long var0, vx_resp_aux_start_buffer_capture_t var2)

    native Unit vx_resp_aux_start_buffer_capture_t_base_set(Long var0, vx_resp_aux_start_buffer_capture_t var2, Long var3, vx_resp_base_t var5)

    native String vx_resp_base_t_extended_status_info_get(Long var0, vx_resp_base_t var2)

    native Unit vx_resp_base_t_extended_status_info_set(Long var0, vx_resp_base_t var2, String var3)

    native Long vx_resp_base_t_message_get(Long var0, vx_resp_base_t var2)

    native Unit vx_resp_base_t_message_set(Long var0, vx_resp_base_t var2, Long var3, vx_message_base_t var5)

    native Long vx_resp_base_t_request_get(Long var0, vx_resp_base_t var2)

    native Unit vx_resp_base_t_request_set(Long var0, vx_resp_base_t var2, Long var3, vx_req_base_t var5)

    native Int vx_resp_base_t_return_code_get(Long var0, vx_resp_base_t var2)

    native Unit vx_resp_base_t_return_code_set(Long var0, vx_resp_base_t var2, Int var3)

    native Int vx_resp_base_t_status_code_get(Long var0, vx_resp_base_t var2)

    native Unit vx_resp_base_t_status_code_set(Long var0, vx_resp_base_t var2, Int var3)

    native String vx_resp_base_t_status_string_get(Long var0, vx_resp_base_t var2)

    native Unit vx_resp_base_t_status_string_set(Long var0, vx_resp_base_t var2, String var3)

    native Int vx_resp_base_t_type_get(Long var0, vx_resp_base_t var2)

    native Unit vx_resp_base_t_type_set(Long var0, vx_resp_base_t var2, Int var3)

    native Long vx_resp_channel_ban_user_t_base_get(Long var0, vx_resp_channel_ban_user_t var2)

    native Unit vx_resp_channel_ban_user_t_base_set(Long var0, vx_resp_channel_ban_user_t var2, Long var3, vx_resp_base_t var5)

    native Int vx_resp_channel_get_banned_users_t_banned_users_count_get(Long var0, vx_resp_channel_get_banned_users_t var2)

    native Unit vx_resp_channel_get_banned_users_t_banned_users_count_set(Long var0, vx_resp_channel_get_banned_users_t var2, Int var3)

    native Long vx_resp_channel_get_banned_users_t_banned_users_get(Long var0, vx_resp_channel_get_banned_users_t var2)

    native Unit vx_resp_channel_get_banned_users_t_banned_users_set(Long var0, vx_resp_channel_get_banned_users_t var2, Long var3)

    native Long vx_resp_channel_get_banned_users_t_base_get(Long var0, vx_resp_channel_get_banned_users_t var2)

    native Unit vx_resp_channel_get_banned_users_t_base_set(Long var0, vx_resp_channel_get_banned_users_t var2, Long var3, vx_resp_base_t var5)

    native Long vx_resp_channel_get_banned_users_t_get_banned_users_item(Long var0, vx_resp_channel_get_banned_users_t var2, Int var3)

    native Long vx_resp_channel_kick_user_t_base_get(Long var0, vx_resp_channel_kick_user_t var2)

    native Unit vx_resp_channel_kick_user_t_base_set(Long var0, vx_resp_channel_kick_user_t var2, Long var3, vx_resp_base_t var5)

    native Long vx_resp_channel_mute_all_users_t_base_get(Long var0, vx_resp_channel_mute_all_users_t var2)

    native Unit vx_resp_channel_mute_all_users_t_base_set(Long var0, vx_resp_channel_mute_all_users_t var2, Long var3, vx_resp_base_t var5)

    native Long vx_resp_channel_mute_user_t_base_get(Long var0, vx_resp_channel_mute_user_t var2)

    native Unit vx_resp_channel_mute_user_t_base_set(Long var0, vx_resp_channel_mute_user_t var2, Long var3, vx_resp_base_t var5)

    native Long vx_resp_channel_set_lock_mode_t_base_get(Long var0, vx_resp_channel_set_lock_mode_t var2)

    native Unit vx_resp_channel_set_lock_mode_t_base_set(Long var0, vx_resp_channel_set_lock_mode_t var2, Long var3, vx_resp_base_t var5)

    native Long vx_resp_connector_create_t_base_get(Long var0, vx_resp_connector_create_t var2)

    native Unit vx_resp_connector_create_t_base_set(Long var0, vx_resp_connector_create_t var2, Long var3, vx_resp_base_t var5)

    native String vx_resp_connector_create_t_connector_handle_get(Long var0, vx_resp_connector_create_t var2)

    native Unit vx_resp_connector_create_t_connector_handle_set(Long var0, vx_resp_connector_create_t var2, String var3)

    native String vx_resp_connector_create_t_version_id_get(Long var0, vx_resp_connector_create_t var2)

    native Unit vx_resp_connector_create_t_version_id_set(Long var0, vx_resp_connector_create_t var2, String var3)

    native Long vx_resp_connector_get_local_audio_info_t_base_get(Long var0, vx_resp_connector_get_local_audio_info_t var2)

    native Unit vx_resp_connector_get_local_audio_info_t_base_set(Long var0, vx_resp_connector_get_local_audio_info_t var2, Long var3, vx_resp_base_t var5)

    native Int vx_resp_connector_get_local_audio_info_t_is_mic_muted_get(Long var0, vx_resp_connector_get_local_audio_info_t var2)

    native Unit vx_resp_connector_get_local_audio_info_t_is_mic_muted_set(Long var0, vx_resp_connector_get_local_audio_info_t var2, Int var3)

    native Int vx_resp_connector_get_local_audio_info_t_is_speaker_muted_get(Long var0, vx_resp_connector_get_local_audio_info_t var2)

    native Unit vx_resp_connector_get_local_audio_info_t_is_speaker_muted_set(Long var0, vx_resp_connector_get_local_audio_info_t var2, Int var3)

    native Int vx_resp_connector_get_local_audio_info_t_mic_volume_get(Long var0, vx_resp_connector_get_local_audio_info_t var2)

    native Unit vx_resp_connector_get_local_audio_info_t_mic_volume_set(Long var0, vx_resp_connector_get_local_audio_info_t var2, Int var3)

    native Int vx_resp_connector_get_local_audio_info_t_speaker_volume_get(Long var0, vx_resp_connector_get_local_audio_info_t var2)

    native Unit vx_resp_connector_get_local_audio_info_t_speaker_volume_set(Long var0, vx_resp_connector_get_local_audio_info_t var2, Int var3)

    native Long vx_resp_connector_initiate_shutdown_t_base_get(Long var0, vx_resp_connector_initiate_shutdown_t var2)

    native Unit vx_resp_connector_initiate_shutdown_t_base_set(Long var0, vx_resp_connector_initiate_shutdown_t var2, Long var3, vx_resp_base_t var5)

    native String vx_resp_connector_initiate_shutdown_t_client_name_get(Long var0, vx_resp_connector_initiate_shutdown_t var2)

    native Unit vx_resp_connector_initiate_shutdown_t_client_name_set(Long var0, vx_resp_connector_initiate_shutdown_t var2, String var3)

    native Long vx_resp_connector_mute_local_mic_t_base_get(Long var0, vx_resp_connector_mute_local_mic_t var2)

    native Unit vx_resp_connector_mute_local_mic_t_base_set(Long var0, vx_resp_connector_mute_local_mic_t var2, Long var3, vx_resp_base_t var5)

    native Long vx_resp_connector_mute_local_speaker_t_base_get(Long var0, vx_resp_connector_mute_local_speaker_t var2)

    native Unit vx_resp_connector_mute_local_speaker_t_base_set(Long var0, vx_resp_connector_mute_local_speaker_t var2, Long var3, vx_resp_base_t var5)

    native Long vx_resp_connector_set_local_mic_volume_t_base_get(Long var0, vx_resp_connector_set_local_mic_volume_t var2)

    native Unit vx_resp_connector_set_local_mic_volume_t_base_set(Long var0, vx_resp_connector_set_local_mic_volume_t var2, Long var3, vx_resp_base_t var5)

    native Long vx_resp_connector_set_local_speaker_volume_t_base_get(Long var0, vx_resp_connector_set_local_speaker_volume_t var2)

    native Unit vx_resp_connector_set_local_speaker_volume_t_base_set(Long var0, vx_resp_connector_set_local_speaker_volume_t var2, Long var3, vx_resp_base_t var5)

    native Long vx_resp_session_channel_invite_user_t_base_get(Long var0, vx_resp_session_channel_invite_user_t var2)

    native Unit vx_resp_session_channel_invite_user_t_base_set(Long var0, vx_resp_session_channel_invite_user_t var2, Long var3, vx_resp_base_t var5)

    native Long vx_resp_session_create_t_base_get(Long var0, vx_resp_session_create_t var2)

    native Unit vx_resp_session_create_t_base_set(Long var0, vx_resp_session_create_t var2, Long var3, vx_resp_base_t var5)

    native String vx_resp_session_create_t_session_handle_get(Long var0, vx_resp_session_create_t var2)

    native Unit vx_resp_session_create_t_session_handle_set(Long var0, vx_resp_session_create_t var2, String var3)

    native String vx_resp_session_create_t_sessiongroup_handle_get(Long var0, vx_resp_session_create_t var2)

    native Unit vx_resp_session_create_t_sessiongroup_handle_set(Long var0, vx_resp_session_create_t var2, String var3)

    native Long vx_resp_session_media_connect_t_base_get(Long var0, vx_resp_session_media_connect_t var2)

    native Unit vx_resp_session_media_connect_t_base_set(Long var0, vx_resp_session_media_connect_t var2, Long var3, vx_resp_base_t var5)

    native Long vx_resp_session_media_disconnect_t_base_get(Long var0, vx_resp_session_media_disconnect_t var2)

    native Unit vx_resp_session_media_disconnect_t_base_set(Long var0, vx_resp_session_media_disconnect_t var2, Long var3, vx_resp_base_t var5)

    native Long vx_resp_session_mute_local_speaker_t_base_get(Long var0, vx_resp_session_mute_local_speaker_t var2)

    native Unit vx_resp_session_mute_local_speaker_t_base_set(Long var0, vx_resp_session_mute_local_speaker_t var2, Long var3, vx_resp_base_t var5)

    native Long vx_resp_session_send_dtmf_t_base_get(Long var0, vx_resp_session_send_dtmf_t var2)

    native Unit vx_resp_session_send_dtmf_t_base_set(Long var0, vx_resp_session_send_dtmf_t var2, Long var3, vx_resp_base_t var5)

    native Long vx_resp_session_send_message_t_base_get(Long var0, vx_resp_session_send_message_t var2)

    native Unit vx_resp_session_send_message_t_base_set(Long var0, vx_resp_session_send_message_t var2, Long var3, vx_resp_base_t var5)

    native Long vx_resp_session_send_notification_t_base_get(Long var0, vx_resp_session_send_notification_t var2)

    native Unit vx_resp_session_send_notification_t_base_set(Long var0, vx_resp_session_send_notification_t var2, Long var3, vx_resp_base_t var5)

    native Long vx_resp_session_set_3d_position_t_base_get(Long var0, vx_resp_session_set_3d_position_t var2)

    native Unit vx_resp_session_set_3d_position_t_base_set(Long var0, vx_resp_session_set_3d_position_t var2, Long var3, vx_resp_base_t var5)

    native Long vx_resp_session_set_local_speaker_volume_t_base_get(Long var0, vx_resp_session_set_local_speaker_volume_t var2)

    native Unit vx_resp_session_set_local_speaker_volume_t_base_set(Long var0, vx_resp_session_set_local_speaker_volume_t var2, Long var3, vx_resp_base_t var5)

    native Long vx_resp_session_set_participant_mute_for_me_t_base_get(Long var0, vx_resp_session_set_participant_mute_for_me_t var2)

    native Unit vx_resp_session_set_participant_mute_for_me_t_base_set(Long var0, vx_resp_session_set_participant_mute_for_me_t var2, Long var3, vx_resp_base_t var5)

    native Long vx_resp_session_set_participant_volume_for_me_t_base_get(Long var0, vx_resp_session_set_participant_volume_for_me_t var2)

    native Unit vx_resp_session_set_participant_volume_for_me_t_base_set(Long var0, vx_resp_session_set_participant_volume_for_me_t var2, Long var3, vx_resp_base_t var5)

    native Long vx_resp_session_set_voice_font_t_base_get(Long var0, vx_resp_session_set_voice_font_t var2)

    native Unit vx_resp_session_set_voice_font_t_base_set(Long var0, vx_resp_session_set_voice_font_t var2, Long var3, vx_resp_base_t var5)

    native Long vx_resp_session_terminate_t_base_get(Long var0, vx_resp_session_terminate_t var2)

    native Unit vx_resp_session_terminate_t_base_set(Long var0, vx_resp_session_terminate_t var2, Long var3, vx_resp_base_t var5)

    native Long vx_resp_session_text_connect_t_base_get(Long var0, vx_resp_session_text_connect_t var2)

    native Unit vx_resp_session_text_connect_t_base_set(Long var0, vx_resp_session_text_connect_t var2, Long var3, vx_resp_base_t var5)

    native Long vx_resp_session_text_disconnect_t_base_get(Long var0, vx_resp_session_text_disconnect_t var2)

    native Unit vx_resp_session_text_disconnect_t_base_set(Long var0, vx_resp_session_text_disconnect_t var2, Long var3, vx_resp_base_t var5)

    native Long vx_resp_sessiongroup_add_session_t_base_get(Long var0, vx_resp_sessiongroup_add_session_t var2)

    native Unit vx_resp_sessiongroup_add_session_t_base_set(Long var0, vx_resp_sessiongroup_add_session_t var2, Long var3, vx_resp_base_t var5)

    native String vx_resp_sessiongroup_add_session_t_session_handle_get(Long var0, vx_resp_sessiongroup_add_session_t var2)

    native Unit vx_resp_sessiongroup_add_session_t_session_handle_set(Long var0, vx_resp_sessiongroup_add_session_t var2, String var3)

    native Long vx_resp_sessiongroup_control_audio_injection_t_base_get(Long var0, vx_resp_sessiongroup_control_audio_injection_t var2)

    native Unit vx_resp_sessiongroup_control_audio_injection_t_base_set(Long var0, vx_resp_sessiongroup_control_audio_injection_t var2, Long var3, vx_resp_base_t var5)

    native Long vx_resp_sessiongroup_control_playback_t_base_get(Long var0, vx_resp_sessiongroup_control_playback_t var2)

    native Unit vx_resp_sessiongroup_control_playback_t_base_set(Long var0, vx_resp_sessiongroup_control_playback_t var2, Long var3, vx_resp_base_t var5)

    native Long vx_resp_sessiongroup_control_recording_t_base_get(Long var0, vx_resp_sessiongroup_control_recording_t var2)

    native Unit vx_resp_sessiongroup_control_recording_t_base_set(Long var0, vx_resp_sessiongroup_control_recording_t var2, Long var3, vx_resp_base_t var5)

    native Long vx_resp_sessiongroup_create_t_base_get(Long var0, vx_resp_sessiongroup_create_t var2)

    native Unit vx_resp_sessiongroup_create_t_base_set(Long var0, vx_resp_sessiongroup_create_t var2, Long var3, vx_resp_base_t var5)

    native String vx_resp_sessiongroup_create_t_sessiongroup_handle_get(Long var0, vx_resp_sessiongroup_create_t var2)

    native Unit vx_resp_sessiongroup_create_t_sessiongroup_handle_set(Long var0, vx_resp_sessiongroup_create_t var2, String var3)

    native Long vx_resp_sessiongroup_get_stats_t_base_get(Long var0, vx_resp_sessiongroup_get_stats_t var2)

    native Unit vx_resp_sessiongroup_get_stats_t_base_set(Long var0, vx_resp_sessiongroup_get_stats_t var2, Long var3, vx_resp_base_t var5)

    native String vx_resp_sessiongroup_get_stats_t_call_id_get(Long var0, vx_resp_sessiongroup_get_stats_t var2)

    native Unit vx_resp_sessiongroup_get_stats_t_call_id_set(Long var0, vx_resp_sessiongroup_get_stats_t var2, String var3)

    native Int vx_resp_sessiongroup_get_stats_t_current_bars_get(Long var0, vx_resp_sessiongroup_get_stats_t var2)

    native Unit vx_resp_sessiongroup_get_stats_t_current_bars_set(Long var0, vx_resp_sessiongroup_get_stats_t var2, Int var3)

    native Int vx_resp_sessiongroup_get_stats_t_incoming_discarded_get(Long var0, vx_resp_sessiongroup_get_stats_t var2)

    native Unit vx_resp_sessiongroup_get_stats_t_incoming_discarded_set(Long var0, vx_resp_sessiongroup_get_stats_t var2, Int var3)

    native Int vx_resp_sessiongroup_get_stats_t_incoming_expected_get(Long var0, vx_resp_sessiongroup_get_stats_t var2)

    native Unit vx_resp_sessiongroup_get_stats_t_incoming_expected_set(Long var0, vx_resp_sessiongroup_get_stats_t var2, Int var3)

    native Int vx_resp_sessiongroup_get_stats_t_incoming_out_of_time_get(Long var0, vx_resp_sessiongroup_get_stats_t var2)

    native Unit vx_resp_sessiongroup_get_stats_t_incoming_out_of_time_set(Long var0, vx_resp_sessiongroup_get_stats_t var2, Int var3)

    native Int vx_resp_sessiongroup_get_stats_t_incoming_packetloss_get(Long var0, vx_resp_sessiongroup_get_stats_t var2)

    native Unit vx_resp_sessiongroup_get_stats_t_incoming_packetloss_set(Long var0, vx_resp_sessiongroup_get_stats_t var2, Int var3)

    native Int vx_resp_sessiongroup_get_stats_t_incoming_received_get(Long var0, vx_resp_sessiongroup_get_stats_t var2)

    native Unit vx_resp_sessiongroup_get_stats_t_incoming_received_set(Long var0, vx_resp_sessiongroup_get_stats_t var2, Int var3)

    native Int vx_resp_sessiongroup_get_stats_t_insufficient_bandwidth_get(Long var0, vx_resp_sessiongroup_get_stats_t var2)

    native Unit vx_resp_sessiongroup_get_stats_t_insufficient_bandwidth_set(Long var0, vx_resp_sessiongroup_get_stats_t var2, Int var3)

    native Int vx_resp_sessiongroup_get_stats_t_max_bars_get(Long var0, vx_resp_sessiongroup_get_stats_t var2)

    native Unit vx_resp_sessiongroup_get_stats_t_max_bars_set(Long var0, vx_resp_sessiongroup_get_stats_t var2, Int var3)

    native Int vx_resp_sessiongroup_get_stats_t_min_bars_get(Long var0, vx_resp_sessiongroup_get_stats_t var2)

    native Unit vx_resp_sessiongroup_get_stats_t_min_bars_set(Long var0, vx_resp_sessiongroup_get_stats_t var2, Int var3)

    native Int vx_resp_sessiongroup_get_stats_t_outgoing_sent_get(Long var0, vx_resp_sessiongroup_get_stats_t var2)

    native Unit vx_resp_sessiongroup_get_stats_t_outgoing_sent_set(Long var0, vx_resp_sessiongroup_get_stats_t var2, Int var3)

    native Int vx_resp_sessiongroup_get_stats_t_pk_loss_get(Long var0, vx_resp_sessiongroup_get_stats_t var2)

    native Unit vx_resp_sessiongroup_get_stats_t_pk_loss_set(Long var0, vx_resp_sessiongroup_get_stats_t var2, Int var3)

    native Int vx_resp_sessiongroup_get_stats_t_render_device_errors_get(Long var0, vx_resp_sessiongroup_get_stats_t var2)

    native Unit vx_resp_sessiongroup_get_stats_t_render_device_errors_set(Long var0, vx_resp_sessiongroup_get_stats_t var2, Int var3)

    native Int vx_resp_sessiongroup_get_stats_t_render_device_overruns_get(Long var0, vx_resp_sessiongroup_get_stats_t var2)

    native Unit vx_resp_sessiongroup_get_stats_t_render_device_overruns_set(Long var0, vx_resp_sessiongroup_get_stats_t var2, Int var3)

    native Int vx_resp_sessiongroup_get_stats_t_render_device_underruns_get(Long var0, vx_resp_sessiongroup_get_stats_t var2)

    native Unit vx_resp_sessiongroup_get_stats_t_render_device_underruns_set(Long var0, vx_resp_sessiongroup_get_stats_t var2, Int var3)

    native Long vx_resp_sessiongroup_remove_session_t_base_get(Long var0, vx_resp_sessiongroup_remove_session_t var2)

    native Unit vx_resp_sessiongroup_remove_session_t_base_set(Long var0, vx_resp_sessiongroup_remove_session_t var2, Long var3, vx_resp_base_t var5)

    native Long vx_resp_sessiongroup_reset_focus_t_base_get(Long var0, vx_resp_sessiongroup_reset_focus_t var2)

    native Unit vx_resp_sessiongroup_reset_focus_t_base_set(Long var0, vx_resp_sessiongroup_reset_focus_t var2, Long var3, vx_resp_base_t var5)

    native Long vx_resp_sessiongroup_set_focus_t_base_get(Long var0, vx_resp_sessiongroup_set_focus_t var2)

    native Unit vx_resp_sessiongroup_set_focus_t_base_set(Long var0, vx_resp_sessiongroup_set_focus_t var2, Long var3, vx_resp_base_t var5)

    native Long vx_resp_sessiongroup_set_playback_options_t_base_get(Long var0, vx_resp_sessiongroup_set_playback_options_t var2)

    native Unit vx_resp_sessiongroup_set_playback_options_t_base_set(Long var0, vx_resp_sessiongroup_set_playback_options_t var2, Long var3, vx_resp_base_t var5)

    native Long vx_resp_sessiongroup_set_session_3d_position_t_base_get(Long var0, vx_resp_sessiongroup_set_session_3d_position_t var2)

    native Unit vx_resp_sessiongroup_set_session_3d_position_t_base_set(Long var0, vx_resp_sessiongroup_set_session_3d_position_t var2, Long var3, vx_resp_base_t var5)

    native Long vx_resp_sessiongroup_set_tx_all_sessions_t_base_get(Long var0, vx_resp_sessiongroup_set_tx_all_sessions_t var2)

    native Unit vx_resp_sessiongroup_set_tx_all_sessions_t_base_set(Long var0, vx_resp_sessiongroup_set_tx_all_sessions_t var2, Long var3, vx_resp_base_t var5)

    native Long vx_resp_sessiongroup_set_tx_no_session_t_base_get(Long var0, vx_resp_sessiongroup_set_tx_no_session_t var2)

    native Unit vx_resp_sessiongroup_set_tx_no_session_t_base_set(Long var0, vx_resp_sessiongroup_set_tx_no_session_t var2, Long var3, vx_resp_base_t var5)

    native Long vx_resp_sessiongroup_set_tx_session_t_base_get(Long var0, vx_resp_sessiongroup_set_tx_session_t var2)

    native Unit vx_resp_sessiongroup_set_tx_session_t_base_set(Long var0, vx_resp_sessiongroup_set_tx_session_t var2, Long var3, vx_resp_base_t var5)

    native Long vx_resp_sessiongroup_terminate_t_base_get(Long var0, vx_resp_sessiongroup_terminate_t var2)

    native Unit vx_resp_sessiongroup_terminate_t_base_set(Long var0, vx_resp_sessiongroup_terminate_t var2, Long var3, vx_resp_base_t var5)

    native Long vx_resp_sessiongroup_unset_focus_t_base_get(Long var0, vx_resp_sessiongroup_unset_focus_t var2)

    native Unit vx_resp_sessiongroup_unset_focus_t_base_set(Long var0, vx_resp_sessiongroup_unset_focus_t var2, Long var3, vx_resp_base_t var5)

    native Unit vx_response_to_xml(Long var0, Long var2)

    native Int vx_sdk_config_t_allow_shared_capture_devices_get(Long var0, vx_sdk_config_t var2)

    native Unit vx_sdk_config_t_allow_shared_capture_devices_set(Long var0, vx_sdk_config_t var2, Int var3)

    native String vx_sdk_config_t_app_id_get(Long var0, vx_sdk_config_t var2)

    native Unit vx_sdk_config_t_app_id_set(Long var0, vx_sdk_config_t var2, String var3)

    native String vx_sdk_config_t_cert_data_dir_get(Long var0, vx_sdk_config_t var2)

    native Unit vx_sdk_config_t_cert_data_dir_set(Long var0, vx_sdk_config_t var2, String var3)

    native Int vx_sdk_config_t_max_logins_per_user_get(Long var0, vx_sdk_config_t var2)

    native Unit vx_sdk_config_t_max_logins_per_user_set(Long var0, vx_sdk_config_t var2, Int var3)

    native Int vx_sdk_config_t_num_codec_threads_get(Long var0, vx_sdk_config_t var2)

    native Unit vx_sdk_config_t_num_codec_threads_set(Long var0, vx_sdk_config_t var2, Int var3)

    native Int vx_sdk_config_t_num_voice_threads_get(Long var0, vx_sdk_config_t var2)

    native Unit vx_sdk_config_t_num_voice_threads_set(Long var0, vx_sdk_config_t var2, Int var3)

    native Int vx_sdk_config_t_num_web_threads_get(Long var0, vx_sdk_config_t var2)

    native Unit vx_sdk_config_t_num_web_threads_set(Long var0, vx_sdk_config_t var2, Int var3)

    native Int vx_sdk_config_t_render_source_initial_buffer_count_get(Long var0, vx_sdk_config_t var2)

    native Unit vx_sdk_config_t_render_source_initial_buffer_count_set(Long var0, vx_sdk_config_t var2, Int var3)

    native Int vx_sdk_config_t_render_source_queue_depth_max_get(Long var0, vx_sdk_config_t var2)

    native Unit vx_sdk_config_t_render_source_queue_depth_max_set(Long var0, vx_sdk_config_t var2, Int var3)

    native Int vx_sdk_config_t_upstream_jitter_frame_count_get(Long var0, vx_sdk_config_t var2)

    native Unit vx_sdk_config_t_upstream_jitter_frame_count_set(Long var0, vx_sdk_config_t var2, Int var3)

    native Int vx_set_cert_data(String var0)

    native Int vx_set_cert_data_dir(String var0)

    native Unit vx_set_crash_dump_generation_enabled(Int var0)

    native Int vx_set_out_of_process_server_address(String var0, Int var1)

    native Int vx_set_preferred_codec(Int var0)

    native Double vx_stat_sample_t_last_get(Long var0, vx_stat_sample_t var2)

    native Unit vx_stat_sample_t_last_set(Long var0, vx_stat_sample_t var2, Double var3)

    native Double vx_stat_sample_t_max_get(Long var0, vx_stat_sample_t var2)

    native Unit vx_stat_sample_t_max_set(Long var0, vx_stat_sample_t var2, Double var3)

    native Double vx_stat_sample_t_mean_get(Long var0, vx_stat_sample_t var2)

    native Unit vx_stat_sample_t_mean_set(Long var0, vx_stat_sample_t var2, Double var3)

    native Double vx_stat_sample_t_min_get(Long var0, vx_stat_sample_t var2)

    native Unit vx_stat_sample_t_min_set(Long var0, vx_stat_sample_t var2, Double var3)

    native Double vx_stat_sample_t_sample_count_get(Long var0, vx_stat_sample_t var2)

    native Unit vx_stat_sample_t_sample_count_set(Long var0, vx_stat_sample_t var2, Double var3)

    native Double vx_stat_sample_t_stddev_get(Long var0, vx_stat_sample_t var2)

    native Unit vx_stat_sample_t_stddev_set(Long var0, vx_stat_sample_t var2, Double var3)

    native Double vx_stat_sample_t_sum_get(Long var0, vx_stat_sample_t var2)

    native Double vx_stat_sample_t_sum_of_squares_get(Long var0, vx_stat_sample_t var2)

    native Unit vx_stat_sample_t_sum_of_squares_set(Long var0, vx_stat_sample_t var2, Double var3)

    native Unit vx_stat_sample_t_sum_set(Long var0, vx_stat_sample_t var2, Double var3)

    native Int vx_stat_thread_t_count_poll_gte_25ms_get(Long var0, vx_stat_thread_t var2)

    native Unit vx_stat_thread_t_count_poll_gte_25ms_set(Long var0, vx_stat_thread_t var2, Int var3)

    native Int vx_stat_thread_t_count_poll_lt_10ms_get(Long var0, vx_stat_thread_t var2)

    native Unit vx_stat_thread_t_count_poll_lt_10ms_set(Long var0, vx_stat_thread_t var2, Int var3)

    native Int vx_stat_thread_t_count_poll_lt_16ms_get(Long var0, vx_stat_thread_t var2)

    native Unit vx_stat_thread_t_count_poll_lt_16ms_set(Long var0, vx_stat_thread_t var2, Int var3)

    native Int vx_stat_thread_t_count_poll_lt_1ms_get(Long var0, vx_stat_thread_t var2)

    native Unit vx_stat_thread_t_count_poll_lt_1ms_set(Long var0, vx_stat_thread_t var2, Int var3)

    native Int vx_stat_thread_t_count_poll_lt_20ms_get(Long var0, vx_stat_thread_t var2)

    native Unit vx_stat_thread_t_count_poll_lt_20ms_set(Long var0, vx_stat_thread_t var2, Int var3)

    native Int vx_stat_thread_t_count_poll_lt_25ms_get(Long var0, vx_stat_thread_t var2)

    native Unit vx_stat_thread_t_count_poll_lt_25ms_set(Long var0, vx_stat_thread_t var2, Int var3)

    native Int vx_stat_thread_t_count_poll_lt_5ms_get(Long var0, vx_stat_thread_t var2)

    native Unit vx_stat_thread_t_count_poll_lt_5ms_set(Long var0, vx_stat_thread_t var2, Int var3)

    native Int vx_stat_thread_t_interval_get(Long var0, vx_stat_thread_t var2)

    native Unit vx_stat_thread_t_interval_set(Long var0, vx_stat_thread_t var2, Int var3)

    native Unit vx_state_account_create(Long var0)

    native Unit vx_state_account_free(Long var0, vx_state_account_t var2)

    native Unit vx_state_account_list_create(Int var0, Long var1)

    native Unit vx_state_account_list_free(Long var0, Int var2)

    native String vx_state_account_t_account_handle_get(Long var0, vx_state_account_t var2)

    native Unit vx_state_account_t_account_handle_set(Long var0, vx_state_account_t var2, String var3)

    native String vx_state_account_t_account_uri_get(Long var0, vx_state_account_t var2)

    native Unit vx_state_account_t_account_uri_set(Long var0, vx_state_account_t var2, String var3)

    native String vx_state_account_t_display_name_get(Long var0, vx_state_account_t var2)

    native Unit vx_state_account_t_display_name_set(Long var0, vx_state_account_t var2, String var3)

    native Long vx_state_account_t_get_state_sessiongroups_item(Long var0, vx_state_account_t var2, Int var3)

    native Int vx_state_account_t_is_anonymous_login_get(Long var0, vx_state_account_t var2)

    native Unit vx_state_account_t_is_anonymous_login_set(Long var0, vx_state_account_t var2, Int var3)

    native Long vx_state_account_t_state_buddies_get(Long var0, vx_state_account_t var2)

    native Unit vx_state_account_t_state_buddies_set(Long var0, vx_state_account_t var2, Long var3)

    native Int vx_state_account_t_state_buddy_count_get(Long var0, vx_state_account_t var2)

    native Unit vx_state_account_t_state_buddy_count_set(Long var0, vx_state_account_t var2, Int var3)

    native Int vx_state_account_t_state_buddy_group_count_get(Long var0, vx_state_account_t var2)

    native Unit vx_state_account_t_state_buddy_group_count_set(Long var0, vx_state_account_t var2, Int var3)

    native Long vx_state_account_t_state_buddy_groups_get(Long var0, vx_state_account_t var2)

    native Unit vx_state_account_t_state_buddy_groups_set(Long var0, vx_state_account_t var2, Long var3)

    native Int vx_state_account_t_state_get(Long var0, vx_state_account_t var2)

    native Int vx_state_account_t_state_sessiongroups_count_get(Long var0, vx_state_account_t var2)

    native Unit vx_state_account_t_state_sessiongroups_count_set(Long var0, vx_state_account_t var2, Int var3)

    native Long vx_state_account_t_state_sessiongroups_get(Long var0, vx_state_account_t var2)

    native Unit vx_state_account_t_state_sessiongroups_set(Long var0, vx_state_account_t var2, Long var3)

    native Unit vx_state_account_t_state_set(Long var0, vx_state_account_t var2, Int var3)

    native Unit vx_state_buddy_contact_create(Long var0)

    native Unit vx_state_buddy_contact_free(Long var0, vx_state_buddy_contact_t var2)

    native Unit vx_state_buddy_contact_list_create(Int var0, Long var1)

    native Unit vx_state_buddy_contact_list_free(Long var0, Int var2)

    native String vx_state_buddy_contact_t_application_get(Long var0, vx_state_buddy_contact_t var2)

    native Unit vx_state_buddy_contact_t_application_set(Long var0, vx_state_buddy_contact_t var2, String var3)

    native String vx_state_buddy_contact_t_contact_get(Long var0, vx_state_buddy_contact_t var2)

    native Unit vx_state_buddy_contact_t_contact_set(Long var0, vx_state_buddy_contact_t var2, String var3)

    native String vx_state_buddy_contact_t_custom_message_get(Long var0, vx_state_buddy_contact_t var2)

    native Unit vx_state_buddy_contact_t_custom_message_set(Long var0, vx_state_buddy_contact_t var2, String var3)

    native String vx_state_buddy_contact_t_display_name_get(Long var0, vx_state_buddy_contact_t var2)

    native Unit vx_state_buddy_contact_t_display_name_set(Long var0, vx_state_buddy_contact_t var2, String var3)

    native String vx_state_buddy_contact_t_id_get(Long var0, vx_state_buddy_contact_t var2)

    native Unit vx_state_buddy_contact_t_id_set(Long var0, vx_state_buddy_contact_t var2, String var3)

    native Int vx_state_buddy_contact_t_presence_get(Long var0, vx_state_buddy_contact_t var2)

    native Unit vx_state_buddy_contact_t_presence_set(Long var0, vx_state_buddy_contact_t var2, Int var3)

    native String vx_state_buddy_contact_t_priority_get(Long var0, vx_state_buddy_contact_t var2)

    native Unit vx_state_buddy_contact_t_priority_set(Long var0, vx_state_buddy_contact_t var2, String var3)

    native Unit vx_state_buddy_create(Long var0)

    native Unit vx_state_buddy_free(Long var0, vx_state_buddy_t var2)

    native Unit vx_state_buddy_group_create(Long var0)

    native Unit vx_state_buddy_group_free(Long var0, vx_state_buddy_group_t var2)

    native Unit vx_state_buddy_group_list_create(Int var0, Long var1)

    native Unit vx_state_buddy_group_list_free(Long var0, Int var2)

    native String vx_state_buddy_group_t_group_data_get(Long var0, vx_state_buddy_group_t var2)

    native Unit vx_state_buddy_group_t_group_data_set(Long var0, vx_state_buddy_group_t var2, String var3)

    native Int vx_state_buddy_group_t_group_id_get(Long var0, vx_state_buddy_group_t var2)

    native Unit vx_state_buddy_group_t_group_id_set(Long var0, vx_state_buddy_group_t var2, Int var3)

    native String vx_state_buddy_group_t_group_name_get(Long var0, vx_state_buddy_group_t var2)

    native Unit vx_state_buddy_group_t_group_name_set(Long var0, vx_state_buddy_group_t var2, String var3)

    native Unit vx_state_buddy_list_create(Int var0, Long var1)

    native Unit vx_state_buddy_list_free(Long var0, Int var2)

    native String vx_state_buddy_t_buddy_data_get(Long var0, vx_state_buddy_t var2)

    native Unit vx_state_buddy_t_buddy_data_set(Long var0, vx_state_buddy_t var2, String var3)

    native String vx_state_buddy_t_buddy_uri_get(Long var0, vx_state_buddy_t var2)

    native Unit vx_state_buddy_t_buddy_uri_set(Long var0, vx_state_buddy_t var2, String var3)

    native String vx_state_buddy_t_display_name_get(Long var0, vx_state_buddy_t var2)

    native Unit vx_state_buddy_t_display_name_set(Long var0, vx_state_buddy_t var2, String var3)

    native Int vx_state_buddy_t_parent_group_id_get(Long var0, vx_state_buddy_t var2)

    native Unit vx_state_buddy_t_parent_group_id_set(Long var0, vx_state_buddy_t var2, Int var3)

    native Int vx_state_buddy_t_state_buddy_contact_count_get(Long var0, vx_state_buddy_t var2)

    native Unit vx_state_buddy_t_state_buddy_contact_count_set(Long var0, vx_state_buddy_t var2, Int var3)

    native Long vx_state_buddy_t_state_buddy_contacts_get(Long var0, vx_state_buddy_t var2)

    native Unit vx_state_buddy_t_state_buddy_contacts_set(Long var0, vx_state_buddy_t var2, Long var3)

    native Unit vx_state_connector_create(Long var0)

    native Unit vx_state_connector_free(Long var0, vx_state_connector_t var2)

    native Unit vx_state_connector_list_create(Int var0, Long var1)

    native Unit vx_state_connector_list_free(Long var0, Int var2)

    native String vx_state_connector_t_connector_handle_get(Long var0, vx_state_connector_t var2)

    native Unit vx_state_connector_t_connector_handle_set(Long var0, vx_state_connector_t var2, String var3)

    native Long vx_state_connector_t_get_state_accounts_item(Long var0, vx_state_connector_t var2, Int var3)

    native Int vx_state_connector_t_mic_mute_get(Long var0, vx_state_connector_t var2)

    native Unit vx_state_connector_t_mic_mute_set(Long var0, vx_state_connector_t var2, Int var3)

    native Int vx_state_connector_t_mic_vol_get(Long var0, vx_state_connector_t var2)

    native Unit vx_state_connector_t_mic_vol_set(Long var0, vx_state_connector_t var2, Int var3)

    native Int vx_state_connector_t_speaker_mute_get(Long var0, vx_state_connector_t var2)

    native Unit vx_state_connector_t_speaker_mute_set(Long var0, vx_state_connector_t var2, Int var3)

    native Int vx_state_connector_t_speaker_vol_get(Long var0, vx_state_connector_t var2)

    native Unit vx_state_connector_t_speaker_vol_set(Long var0, vx_state_connector_t var2, Int var3)

    native Int vx_state_connector_t_state_accounts_count_get(Long var0, vx_state_connector_t var2)

    native Unit vx_state_connector_t_state_accounts_count_set(Long var0, vx_state_connector_t var2, Int var3)

    native Long vx_state_connector_t_state_accounts_get(Long var0, vx_state_connector_t var2)

    native Unit vx_state_connector_t_state_accounts_set(Long var0, vx_state_connector_t var2, Long var3)

    native Unit vx_state_participant_create(Long var0)

    native Unit vx_state_participant_free(Long var0, vx_state_participant_t var2)

    native Unit vx_state_participant_list_create(Int var0, Long var1)

    native Unit vx_state_participant_list_free(Long var0, Int var2)

    native String vx_state_participant_t_display_name_get(Long var0, vx_state_participant_t var2)

    native Unit vx_state_participant_t_display_name_set(Long var0, vx_state_participant_t var2, String var3)

    native Double vx_state_participant_t_energy_get(Long var0, vx_state_participant_t var2)

    native Unit vx_state_participant_t_energy_set(Long var0, vx_state_participant_t var2, Double var3)

    native Int vx_state_participant_t_is_anonymous_login_get(Long var0, vx_state_participant_t var2)

    native Unit vx_state_participant_t_is_anonymous_login_set(Long var0, vx_state_participant_t var2, Int var3)

    native Int vx_state_participant_t_is_audio_enabled_get(Long var0, vx_state_participant_t var2)

    native Unit vx_state_participant_t_is_audio_enabled_set(Long var0, vx_state_participant_t var2, Int var3)

    native Int vx_state_participant_t_is_audio_moderator_muted_get(Long var0, vx_state_participant_t var2)

    native Unit vx_state_participant_t_is_audio_moderator_muted_set(Long var0, vx_state_participant_t var2, Int var3)

    native Int vx_state_participant_t_is_audio_muted_for_me_get(Long var0, vx_state_participant_t var2)

    native Unit vx_state_participant_t_is_audio_muted_for_me_set(Long var0, vx_state_participant_t var2, Int var3)

    native Int vx_state_participant_t_is_hand_raised_get(Long var0, vx_state_participant_t var2)

    native Unit vx_state_participant_t_is_hand_raised_set(Long var0, vx_state_participant_t var2, Int var3)

    native Int vx_state_participant_t_is_speaking_get(Long var0, vx_state_participant_t var2)

    native Unit vx_state_participant_t_is_speaking_set(Long var0, vx_state_participant_t var2, Int var3)

    native Int vx_state_participant_t_is_text_enabled_get(Long var0, vx_state_participant_t var2)

    native Unit vx_state_participant_t_is_text_enabled_set(Long var0, vx_state_participant_t var2, Int var3)

    native Int vx_state_participant_t_is_text_moderator_muted_get(Long var0, vx_state_participant_t var2)

    native Unit vx_state_participant_t_is_text_moderator_muted_set(Long var0, vx_state_participant_t var2, Int var3)

    native Int vx_state_participant_t_is_text_muted_for_me_get(Long var0, vx_state_participant_t var2)

    native Unit vx_state_participant_t_is_text_muted_for_me_set(Long var0, vx_state_participant_t var2, Int var3)

    native Int vx_state_participant_t_is_typing_get(Long var0, vx_state_participant_t var2)

    native Unit vx_state_participant_t_is_typing_set(Long var0, vx_state_participant_t var2, Int var3)

    native Int vx_state_participant_t_type_get(Long var0, vx_state_participant_t var2)

    native Unit vx_state_participant_t_type_set(Long var0, vx_state_participant_t var2, Int var3)

    native String vx_state_participant_t_uri_get(Long var0, vx_state_participant_t var2)

    native Unit vx_state_participant_t_uri_set(Long var0, vx_state_participant_t var2, String var3)

    native Int vx_state_participant_t_volume_get(Long var0, vx_state_participant_t var2)

    native Unit vx_state_participant_t_volume_set(Long var0, vx_state_participant_t var2, Int var3)

    native Unit vx_state_session_create(Long var0)

    native Unit vx_state_session_free(Long var0, vx_state_session_t var2)

    native Unit vx_state_session_list_create(Int var0, Long var1)

    native Unit vx_state_session_list_free(Long var0, Int var2)

    native String vx_state_session_t_durable_media_id_get(Long var0, vx_state_session_t var2)

    native Unit vx_state_session_t_durable_media_id_set(Long var0, vx_state_session_t var2, String var3)

    native Long vx_state_session_t_get_state_participants_item(Long var0, vx_state_session_t var2, Int var3)

    native Int vx_state_session_t_has_audio_get(Long var0, vx_state_session_t var2)

    native Unit vx_state_session_t_has_audio_set(Long var0, vx_state_session_t var2, Int var3)

    native Int vx_state_session_t_has_text_get(Long var0, vx_state_session_t var2)

    native Unit vx_state_session_t_has_text_set(Long var0, vx_state_session_t var2, Int var3)

    native Int vx_state_session_t_is_audio_muted_for_me_get(Long var0, vx_state_session_t var2)

    native Unit vx_state_session_t_is_audio_muted_for_me_set(Long var0, vx_state_session_t var2, Int var3)

    native Int vx_state_session_t_is_connected_get(Long var0, vx_state_session_t var2)

    native Unit vx_state_session_t_is_connected_set(Long var0, vx_state_session_t var2, Int var3)

    native Int vx_state_session_t_is_focused_get(Long var0, vx_state_session_t var2)

    native Unit vx_state_session_t_is_focused_set(Long var0, vx_state_session_t var2, Int var3)

    native Int vx_state_session_t_is_incoming_get(Long var0, vx_state_session_t var2)

    native Unit vx_state_session_t_is_incoming_set(Long var0, vx_state_session_t var2, Int var3)

    native Int vx_state_session_t_is_positional_get(Long var0, vx_state_session_t var2)

    native Unit vx_state_session_t_is_positional_set(Long var0, vx_state_session_t var2, Int var3)

    native Int vx_state_session_t_is_text_muted_for_me_get(Long var0, vx_state_session_t var2)

    native Unit vx_state_session_t_is_text_muted_for_me_set(Long var0, vx_state_session_t var2, Int var3)

    native Int vx_state_session_t_is_transmitting_get(Long var0, vx_state_session_t var2)

    native Unit vx_state_session_t_is_transmitting_set(Long var0, vx_state_session_t var2, Int var3)

    native String vx_state_session_t_name_get(Long var0, vx_state_session_t var2)

    native Unit vx_state_session_t_name_set(Long var0, vx_state_session_t var2, String var3)

    native Int vx_state_session_t_session_font_id_get(Long var0, vx_state_session_t var2)

    native Unit vx_state_session_t_session_font_id_set(Long var0, vx_state_session_t var2, Int var3)

    native String vx_state_session_t_session_handle_get(Long var0, vx_state_session_t var2)

    native Unit vx_state_session_t_session_handle_set(Long var0, vx_state_session_t var2, String var3)

    native Int vx_state_session_t_state_participant_count_get(Long var0, vx_state_session_t var2)

    native Unit vx_state_session_t_state_participant_count_set(Long var0, vx_state_session_t var2, Int var3)

    native Long vx_state_session_t_state_participants_get(Long var0, vx_state_session_t var2)

    native Unit vx_state_session_t_state_participants_set(Long var0, vx_state_session_t var2, Long var3)

    native String vx_state_session_t_uri_get(Long var0, vx_state_session_t var2)

    native Unit vx_state_session_t_uri_set(Long var0, vx_state_session_t var2, String var3)

    native Int vx_state_session_t_volume_get(Long var0, vx_state_session_t var2)

    native Unit vx_state_session_t_volume_set(Long var0, vx_state_session_t var2, Int var3)

    native Unit vx_state_sessiongroup_create(Long var0)

    native Unit vx_state_sessiongroup_free(Long var0, vx_state_sessiongroup_t var2)

    native Unit vx_state_sessiongroup_list_create(Int var0, Long var1)

    native Unit vx_state_sessiongroup_list_free(Long var0, Int var2)

    native Int vx_state_sessiongroup_t_current_playback_mode_get(Long var0, vx_state_sessiongroup_t var2)

    native Unit vx_state_sessiongroup_t_current_playback_mode_set(Long var0, vx_state_sessiongroup_t var2, Int var3)

    native Double vx_state_sessiongroup_t_current_playback_speed_get(Long var0, vx_state_sessiongroup_t var2)

    native Unit vx_state_sessiongroup_t_current_playback_speed_set(Long var0, vx_state_sessiongroup_t var2, Double var3)

    native String vx_state_sessiongroup_t_current_recording_filename_get(Long var0, vx_state_sessiongroup_t var2)

    native Unit vx_state_sessiongroup_t_current_recording_filename_set(Long var0, vx_state_sessiongroup_t var2, String var3)

    native Int vx_state_sessiongroup_t_first_loop_frame_get(Long var0, vx_state_sessiongroup_t var2)

    native Unit vx_state_sessiongroup_t_first_loop_frame_set(Long var0, vx_state_sessiongroup_t var2, Int var3)

    native Long vx_state_sessiongroup_t_get_state_sessions_item(Long var0, vx_state_sessiongroup_t var2, Int var3)

    native Int vx_state_sessiongroup_t_in_delayed_playback_get(Long var0, vx_state_sessiongroup_t var2)

    native Unit vx_state_sessiongroup_t_in_delayed_playback_set(Long var0, vx_state_sessiongroup_t var2, Int var3)

    native Int vx_state_sessiongroup_t_last_loop_frame_played_get(Long var0, vx_state_sessiongroup_t var2)

    native Unit vx_state_sessiongroup_t_last_loop_frame_played_set(Long var0, vx_state_sessiongroup_t var2, Int var3)

    native Int vx_state_sessiongroup_t_loop_buffer_capacity_get(Long var0, vx_state_sessiongroup_t var2)

    native Unit vx_state_sessiongroup_t_loop_buffer_capacity_set(Long var0, vx_state_sessiongroup_t var2, Int var3)

    native Int vx_state_sessiongroup_t_playback_paused_get(Long var0, vx_state_sessiongroup_t var2)

    native Unit vx_state_sessiongroup_t_playback_paused_set(Long var0, vx_state_sessiongroup_t var2, Int var3)

    native String vx_state_sessiongroup_t_sessiongroup_handle_get(Long var0, vx_state_sessiongroup_t var2)

    native Unit vx_state_sessiongroup_t_sessiongroup_handle_set(Long var0, vx_state_sessiongroup_t var2, String var3)

    native Int vx_state_sessiongroup_t_state_sessions_count_get(Long var0, vx_state_sessiongroup_t var2)

    native Unit vx_state_sessiongroup_t_state_sessions_count_set(Long var0, vx_state_sessiongroup_t var2, Int var3)

    native Long vx_state_sessiongroup_t_state_sessions_get(Long var0, vx_state_sessiongroup_t var2)

    native Unit vx_state_sessiongroup_t_state_sessions_set(Long var0, vx_state_sessiongroup_t var2, Long var3)

    native Int vx_state_sessiongroup_t_total_loop_frames_captured_get(Long var0, vx_state_sessiongroup_t var2)

    native Unit vx_state_sessiongroup_t_total_loop_frames_captured_set(Long var0, vx_state_sessiongroup_t var2, Int var3)

    native Int vx_state_sessiongroup_t_total_recorded_frames_get(Long var0, vx_state_sessiongroup_t var2)

    native Unit vx_state_sessiongroup_t_total_recorded_frames_set(Long var0, vx_state_sessiongroup_t var2, Int var3)

    native String vx_strdup(String var0)

    native Unit vx_string_list_create(Int var0, Long var1)

    native Unit vx_string_list_free(Long var0)

    native Int vx_system_stats_t_ar_source_count_get(Long var0, vx_system_stats_t var2)

    native Unit vx_system_stats_t_ar_source_count_set(Long var0, vx_system_stats_t var2, Int var3)

    native Long vx_system_stats_t_ar_source_free_buffers_get(Long var0, vx_system_stats_t var2)

    native Unit vx_system_stats_t_ar_source_free_buffers_set(Long var0, vx_system_stats_t var2, Long var3, vx_stat_sample_t var5)

    native Int vx_system_stats_t_ar_source_poll_count_get(Long var0, vx_system_stats_t var2)

    native Unit vx_system_stats_t_ar_source_poll_count_set(Long var0, vx_system_stats_t var2, Int var3)

    native Long vx_system_stats_t_ar_source_queue_depth_get(Long var0, vx_system_stats_t var2)

    native Unit vx_system_stats_t_ar_source_queue_depth_set(Long var0, vx_system_stats_t var2, Long var3, vx_stat_sample_t var5)

    native Int vx_system_stats_t_ar_source_queue_limit_get(Long var0, vx_system_stats_t var2)

    native Unit vx_system_stats_t_ar_source_queue_limit_set(Long var0, vx_system_stats_t var2, Int var3)

    native Int vx_system_stats_t_ar_source_queue_overflows_get(Long var0, vx_system_stats_t var2)

    native Unit vx_system_stats_t_ar_source_queue_overflows_set(Long var0, vx_system_stats_t var2, Int var3)

    native Int vx_system_stats_t_ss_size_get(Long var0, vx_system_stats_t var2)

    native Unit vx_system_stats_t_ss_size_set(Long var0, vx_system_stats_t var2, Int var3)

    native Long vx_system_stats_t_ticker_thread_get(Long var0, vx_system_stats_t var2)

    native Unit vx_system_stats_t_ticker_thread_set(Long var0, vx_system_stats_t var2, Long var3, vx_stat_thread_t var5)

    native Long vx_system_stats_t_vp_thread_get(Long var0, vx_system_stats_t var2)

    native Unit vx_system_stats_t_vp_thread_set(Long var0, vx_system_stats_t var2, Long var3, vx_stat_thread_t var5)

    native Int vx_uninitialize()

    native Unit vx_unregister_logging_handler(Long var0, Long var2)

    native Unit vx_unregister_message_notification_handler(Long var0, Long var2)

    native Unit vx_user_channel_create(Long var0)

    native Unit vx_user_channel_free(Long var0, vx_user_channel_t var2)

    native String vx_user_channel_t_name_get(Long var0, vx_user_channel_t var2)

    native Unit vx_user_channel_t_name_set(Long var0, vx_user_channel_t var2, String var3)

    native String vx_user_channel_t_uri_get(Long var0, vx_user_channel_t var2)

    native Unit vx_user_channel_t_uri_set(Long var0, vx_user_channel_t var2, String var3)

    native Unit vx_user_channels_create(Int var0, Long var1)

    native Unit vx_user_channels_free(Long var0, Int var2)

    native Unit vx_voice_font_create(Long var0)

    native Unit vx_voice_font_free(Long var0, vx_voice_font_t var2)

    native Unit vx_voice_font_list_create(Int var0, Long var1)

    native Unit vx_voice_font_list_free(Long var0, Int var2)

    native String vx_voice_font_t_description_get(Long var0, vx_voice_font_t var2)

    native Unit vx_voice_font_t_description_set(Long var0, vx_voice_font_t var2, String var3)

    native String vx_voice_font_t_expiration_date_get(Long var0, vx_voice_font_t var2)

    native Unit vx_voice_font_t_expiration_date_set(Long var0, vx_voice_font_t var2, String var3)

    native Int vx_voice_font_t_expired_get(Long var0, vx_voice_font_t var2)

    native Unit vx_voice_font_t_expired_set(Long var0, vx_voice_font_t var2, Int var3)

    native String vx_voice_font_t_font_delta_get(Long var0, vx_voice_font_t var2)

    native Unit vx_voice_font_t_font_delta_set(Long var0, vx_voice_font_t var2, String var3)

    native String vx_voice_font_t_font_rules_get(Long var0, vx_voice_font_t var2)

    native Unit vx_voice_font_t_font_rules_set(Long var0, vx_voice_font_t var2, String var3)

    native Int vx_voice_font_t_id_get(Long var0, vx_voice_font_t var2)

    native Unit vx_voice_font_t_id_set(Long var0, vx_voice_font_t var2, Int var3)

    native String vx_voice_font_t_name_get(Long var0, vx_voice_font_t var2)

    native Unit vx_voice_font_t_name_set(Long var0, vx_voice_font_t var2, String var3)

    native Int vx_voice_font_t_parent_id_get(Long var0, vx_voice_font_t var2)

    native Unit vx_voice_font_t_parent_id_set(Long var0, vx_voice_font_t var2, Int var3)

    native Int vx_voice_font_t_status_get(Long var0, vx_voice_font_t var2)

    native Unit vx_voice_font_t_status_set(Long var0, vx_voice_font_t var2, Int var3)

    native Int vx_voice_font_t_type_get(Long var0, vx_voice_font_t var2)

    native Unit vx_voice_font_t_type_set(Long var0, vx_voice_font_t var2, Int var3)

    native Int vx_vxr_file_close(Long var0)

    native Int vx_vxr_file_get_frame_count(Long var0, Long var2)

    native Int vx_vxr_file_move_to_frame(Long var0, Int var2)

    native Int vx_vxr_file_open(String var0, String var1, Long var2)

    native Int vx_vxr_file_read_frame(Long var0, Long var2, Int var4, Long var5, Long var7)

    native Int vx_vxr_file_write_frame(Long var0, Int var2, Long var3, Int var5)

    native Long vx_wait_for_message(Int var0)

    native Int vx_xml_to_event(String var0, Long var1, Long var3)

    native Int vx_xml_to_request(String var0, Long var1, Long var3)

    native Int vx_xml_to_response(String var0, Long var1, Long var3)

    native Long xml_to_request(String var0)
}

