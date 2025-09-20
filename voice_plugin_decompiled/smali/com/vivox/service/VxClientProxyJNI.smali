.class Lcom/vivox/service/VxClientProxyJNI;
.super Ljava/lang/Object;
.source "VxClientProxyJNI.java"


# direct methods
.method static constructor <clinit>()V
    .locals 1

    .prologue
    .line 7
    const-string v0, "vxaudio-jni"

    invoke-static {v0}, Ljava/lang/System;->loadLibrary(Ljava/lang/String;)V

    .line 8
    const-string v0, "sndfile"

    invoke-static {v0}, Ljava/lang/System;->loadLibrary(Ljava/lang/String;)V

    .line 9
    const-string v0, "oRTP"

    invoke-static {v0}, Ljava/lang/System;->loadLibrary(Ljava/lang/String;)V

    .line 10
    const-string v0, "vivoxsdk"

    invoke-static {v0}, Ljava/lang/System;->loadLibrary(Ljava/lang/String;)V

    .line 11
    const-string v0, "VxClient"

    invoke-static {v0}, Ljava/lang/System;->loadLibrary(Ljava/lang/String;)V

    .line 12
    return-void
.end method

.method constructor <init>()V
    .locals 0

    .prologue
    .line 14
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 15
    return-void
.end method

.method public static final native BUILD_DATE_get()Ljava/lang/String;
.end method

.method public static final native BUILD_HOST_get()Ljava/lang/String;
.end method

.method public static final native BUILD_PERSON_get()Ljava/lang/String;
.end method

.method public static final native ND_E_NO_ERROR_get()I
.end method

.method public static final native SDK_BRANCH_get()Ljava/lang/String;
.end method

.method public static final native SDK_VERSION_get()Ljava/lang/String;
.end method

.method public static final native SUBVERSION_CHANGE_get()I
.end method

.method public static final native SUBVERSION_DATE_get()Ljava/lang/String;
.end method

.method public static final native VERSION_BUILD_get()I
.end method

.method public static final native VERSION_MAJOR_get()I
.end method

.method public static final native VERSION_MICRO_get()I
.end method

.method public static final native VERSION_MINOR_get()I
.end method

.method public static final native VIVOX_BUILD_TYPE_get()Ljava/lang/String;
.end method

.method public static final native VIVOX_SDK_ACCOUNT_CHANNEL_CREATE_AND_INVITE_OBSOLETE_get()I
.end method

.method public static final native VIVOX_SDK_EVT_SESSION_PARTICIPANT_LIST_OBSOLETE_get()I
.end method

.method public static final native VIVOX_SDK_HAS_ACCOUNT_SEND_MSG_get()I
.end method

.method public static final native VIVOX_SDK_HAS_AUX_DIAGNOSTIC_STATE_get()I
.end method

.method public static final native VIVOX_SDK_HAS_CLIENT_SIDE_RECORDING_V2_get()I
.end method

.method public static final native VIVOX_SDK_HAS_CLIENT_SIDE_RECORDING_get()I
.end method

.method public static final native VIVOX_SDK_HAS_CRASH_REPORTING_get()I
.end method

.method public static final native VIVOX_SDK_HAS_FRAME_TOTALS_get()I
.end method

.method public static final native VIVOX_SDK_HAS_GENERIC_APP_NOTIFICATIONS_ONLY_get()I
.end method

.method public static final native VIVOX_SDK_HAS_GROUP_IM_get()I
.end method

.method public static final native VIVOX_SDK_HAS_INTEGRATED_PROXY_get()I
.end method

.method public static final native VIVOX_SDK_HAS_MUTE_SCOPE_get()I
.end method

.method public static final native VIVOX_SDK_HAS_NETWORK_MESSAGE_get()I
.end method

.method public static final native VIVOX_SDK_HAS_NO_CHANNEL_FOLDERS_get()I
.end method

.method public static final native VIVOX_SDK_HAS_NO_SCORE_get()I
.end method

.method public static final native VIVOX_SDK_HAS_PARTICIPANT_TYPE_get()I
.end method

.method public static final native VIVOX_SDK_HAS_VOICE_FONTS_get()I
.end method

.method public static final native VIVOX_SDK_NO_IS_AD_PLAYING_get()I
.end method

.method public static final native VIVOX_SDK_NO_LEGACY_RECORDING_get()I
.end method

.method public static final native VIVOX_SDK_SESSION_CHANNEL_GET_PARTICIPANTS_OBSOLETE_get()I
.end method

.method public static final native VIVOX_SDK_SESSION_CONNECT_OBSOLETE_get()I
.end method

.method public static final native VIVOX_SDK_SESSION_GET_LOCAL_AUDIO_INFO_OBSOLETE_get()I
.end method

.method public static final native VIVOX_SDK_SESSION_MEDIA_RINGBACK_OBSOLETE_get()I
.end method

.method public static final native VIVOX_SDK_SESSION_RENDER_AUDIO_OBSOLETE_get()I
.end method

.method public static final native VIVOX_V_V2_AUDIO_DATA_MONO_PCMU_8000_COLLAPSED_get()I
.end method

.method public static final native VIVOX_V_V2_AUDIO_DATA_MONO_PCMU_8000_EXPANDED_get()I
.end method

.method public static final native VIVOX_V_V2_AUDIO_DATA_MONO_PCMU_get()I
.end method

.method public static final native VIVOX_V_V2_AUDIO_DATA_MONO_SIREN14_32000_EXPANDED_get()I
.end method

.method public static final native VIVOX_V_V2_AUDIO_DATA_MONO_SIREN14_32000_get()I
.end method

.method public static final native VXCLIENT_SWIG_VERSION_get()I
.end method

.method public static final native VX_E_ACCOUNT_MISCONFIGURED_get()I
.end method

.method public static final native VX_E_ALREADY_LOGGED_IN_get()I
.end method

.method public static final native VX_E_ALREADY_LOGGED_OUT_get()I
.end method

.method public static final native VX_E_BUFSIZE_get()I
.end method

.method public static final native VX_E_CALL_CREATION_FAILED_get()I
.end method

.method public static final native VX_E_CAPACITY_EXCEEDED_get()I
.end method

.method public static final native VX_E_CAPTURE_DEVICE_IN_USE_get()I
.end method

.method public static final native VX_E_CHANNEL_URI_REQUIRED_get()I
.end method

.method public static final native VX_E_CROSS_DOMAIN_LOGINS_DISABLED_get()I
.end method

.method public static final native VX_E_FAILED_TO_CONNECT_TO_SERVER_get()I
.end method

.method public static final native VX_E_FAILED_TO_CONNECT_TO_VOICE_SERVICE_get()I
.end method

.method public static final native VX_E_FAILED_TO_SEND_REQUEST_TO_VOICE_SERVICE_get()I
.end method

.method public static final native VX_E_FAILED_get()I
.end method

.method public static final native VX_E_FILE_CORRUPT_get()I
.end method

.method public static final native VX_E_FILE_OPEN_FAILED_get()I
.end method

.method public static final native VX_E_FILE_WRITE_FAILED_REACHED_MAX_FILESIZE_get()I
.end method

.method public static final native VX_E_FILE_WRITE_FAILED_get()I
.end method

.method public static final native VX_E_INSUFFICIENT_PRIVILEGE_get()I
.end method

.method public static final native VX_E_INVALID_APP_TOKEN_get()I
.end method

.method public static final native VX_E_INVALID_ARGUMENT_get()I
.end method

.method public static final native VX_E_INVALID_AUTH_TOKEN_get()I
.end method

.method public static final native VX_E_INVALID_CAPTURE_DEVICE_FOR_REQUESTED_OPERATION_get()I
.end method

.method public static final native VX_E_INVALID_CAPTURE_DEVICE_SPECIFIER_get()I
.end method

.method public static final native VX_E_INVALID_CODEC_TYPE_get()I
.end method

.method public static final native VX_E_INVALID_CONNECTOR_STATE_get()I
.end method

.method public static final native VX_E_INVALID_FILE_OPERATION_get()I
.end method

.method public static final native VX_E_INVALID_MASK_get()I
.end method

.method public static final native VX_E_INVALID_MEDIA_FORMAT_get()I
.end method

.method public static final native VX_E_INVALID_RENDER_DEVICE_SPECIFIER_get()I
.end method

.method public static final native VX_E_INVALID_SDK_HANDLE_get()I
.end method

.method public static final native VX_E_INVALID_SESSION_STATE_get()I
.end method

.method public static final native VX_E_INVALID_SUBSCRIPTION_RULE_TYPE_get()I
.end method

.method public static final native VX_E_INVALID_USERNAME_OR_PASSWORD_get()I
.end method

.method public static final native VX_E_INVALID_XML_get()I
.end method

.method public static final native VX_E_LOGIN_FAILED_get()I
.end method

.method public static final native VX_E_LOOP_MODE_RECORDING_NOT_ENABLED_get()I
.end method

.method public static final native VX_E_MAXIMUM_NUMBER_OF_CALLS_EXCEEEDED_get()I
.end method

.method public static final native VX_E_MAX_CONNECTOR_LIMIT_EXCEEDED_get()I
.end method

.method public static final native VX_E_MAX_HTTP_DATA_RESPONSE_SIZE_EXCEEDED_get()I
.end method

.method public static final native VX_E_MAX_LOGINS_PER_USER_EXCEEDED_get()I
.end method

.method public static final native VX_E_MAX_PLAYBACK_SESSIONGROUPS_EXCEEDED_get()I
.end method

.method public static final native VX_E_MAX_SESSION_LIMIT_EXCEEDED_get()I
.end method

.method public static final native VX_E_MEDIA_DISCONNECT_NOT_ALLOWED_get()I
.end method

.method public static final native VX_E_MULTI_CHANNEL_DENIED_get()I
.end method

.method public static final native VX_E_NOT_IMPL_get()I
.end method

.method public static final native VX_E_NOT_INITIALIZED_get()I
.end method

.method public static final native VX_E_NOT_LOGGED_IN_get()I
.end method

.method public static final native VX_E_NO_CAPTURE_DEVICES_FOUND_get()I
.end method

.method public static final native VX_E_NO_EXIST_get()I
.end method

.method public static final native VX_E_NO_MORE_FRAMES_get()I
.end method

.method public static final native VX_E_NO_RENDER_DEVICES_FOUND_get()I
.end method

.method public static final native VX_E_NO_SESSION_PORTS_AVAILABLE_get()I
.end method

.method public static final native VX_E_NO_SUCH_SESSION_get()I
.end method

.method public static final native VX_E_PRELOGIN_INFO_NOT_RETURNED_get()I
.end method

.method public static final native VX_E_RECORDING_ALREADY_ACTIVE_get()I
.end method

.method public static final native VX_E_RECORDING_LOOP_BUFFER_EMPTY_get()I
.end method

.method public static final native VX_E_RENDER_CONTEXT_DOES_NOT_EXIST_get()I
.end method

.method public static final native VX_E_RENDER_DEVICE_DOES_NOT_EXIST_get()I
.end method

.method public static final native VX_E_RENDER_DEVICE_IN_USE_get()I
.end method

.method public static final native VX_E_RENDER_SOURCE_DOES_NOT_EXIST_get()I
.end method

.method public static final native VX_E_REQUESTCONTEXT_NOT_FOUND_get()I
.end method

.method public static final native VX_E_REQUEST_CANCELLED_get()I
.end method

.method public static final native VX_E_REQUEST_NOT_SUPPORTED_get()I
.end method

.method public static final native VX_E_REQUEST_TYPE_NOT_SUPPORTED_get()I
.end method

.method public static final native VX_E_RTP_TIMEOUT_get()I
.end method

.method public static final native VX_E_SESSIONGROUP_NOT_FOUND_get()I
.end method

.method public static final native VX_E_SESSIONGROUP_TRANSMIT_NOT_ALLOWED_get()I
.end method

.method public static final native VX_E_SESSION_CHANNEL_TEXT_DENIED_get()I
.end method

.method public static final native VX_E_SESSION_CREATE_PENDING_get()I
.end method

.method public static final native VX_E_SESSION_DOES_NOT_HAVE_AUDIO_get()I
.end method

.method public static final native VX_E_SESSION_DOES_NOT_HAVE_TEXT_get()I
.end method

.method public static final native VX_E_SESSION_IS_NOT_3D_get()I
.end method

.method public static final native VX_E_SESSION_MAX_get()I
.end method

.method public static final native VX_E_SESSION_MEDIA_CONNECT_FAILED_get()I
.end method

.method public static final native VX_E_SESSION_MEDIA_DISCONNECT_FAILED_get()I
.end method

.method public static final native VX_E_SESSION_MESSAGE_BUILD_FAILED_get()I
.end method

.method public static final native VX_E_SESSION_MSG_CONTENT_TYPE_FAILED_get()I
.end method

.method public static final native VX_E_SESSION_MUST_HAVE_MEDIA_get()I
.end method

.method public static final native VX_E_SESSION_TERMINATE_PENDING_get()I
.end method

.method public static final native VX_E_SESSION_TEXT_DENIED_get()I
.end method

.method public static final native VX_E_SESSION_TEXT_DISABLED_get()I
.end method

.method public static final native VX_E_STREAM_READ_FAILED_get()I
.end method

.method public static final native VX_E_SUBSCRIPTION_NOT_FOUND_get()I
.end method

.method public static final native VX_E_TERMINATESESSION_NOT_VALID_get()I
.end method

.method public static final native VX_E_TEXT_CONNECT_NOT_ALLOWED_get()I
.end method

.method public static final native VX_E_TEXT_DISABLED_get()I
.end method

.method public static final native VX_E_TEXT_DISCONNECT_NOT_ALLOWED_get()I
.end method

.method public static final native VX_E_UNABLE_TO_OPEN_CAPTURE_DEVICE_get()I
.end method

.method public static final native VX_E_UNEXPECTED_END_OF_FILE_get()I
.end method

.method public static final native VX_E_VOICE_FONT_NOT_FOUND_get()I
.end method

.method public static final native VX_E_WRONG_CONNECTOR_get()I
.end method

.method public static final native VX_MEDIA_FLAGS_AUDIO_get()I
.end method

.method public static final native VX_MEDIA_FLAGS_TEXT_get()I
.end method

.method public static final native VX_RECORDING_FRAME_TYPE_CONTROL_get()I
.end method

.method public static final native VX_RECORDING_FRAME_TYPE_DELTA_get()I
.end method

.method public static final native VX_SESSIONGROUP_AUDIO_INJECTION_CONTROL_MAX_get()I
.end method

.method public static final native VX_SESSIONGROUP_AUDIO_INJECTION_CONTROL_MIN_get()I
.end method

.method public static final native VX_SESSIONGROUP_AUDIO_INJECTION_CONTROL_RESTART_get()I
.end method

.method public static final native VX_SESSIONGROUP_AUDIO_INJECTION_CONTROL_START_get()I
.end method

.method public static final native VX_SESSIONGROUP_AUDIO_INJECTION_CONTROL_STOP_get()I
.end method

.method public static final native VX_SESSIONGROUP_PLAYBACK_CONTROL_PAUSE_get()I
.end method

.method public static final native VX_SESSIONGROUP_PLAYBACK_CONTROL_START_get()I
.end method

.method public static final native VX_SESSIONGROUP_PLAYBACK_CONTROL_STOP_get()I
.end method

.method public static final native VX_SESSIONGROUP_PLAYBACK_CONTROL_UNPAUSE_get()I
.end method

.method public static final native VX_SESSIONGROUP_PLAYBACK_MODE_NORMAL_get()I
.end method

.method public static final native VX_SESSIONGROUP_PLAYBACK_MODE_VOX_get()I
.end method

.method public static final native VX_SESSIONGROUP_RECORDING_CONTROL_FLUSH_TO_FILE_get()I
.end method

.method public static final native VX_SESSIONGROUP_RECORDING_CONTROL_START_get()I
.end method

.method public static final native VX_SESSIONGROUP_RECORDING_CONTROL_STOP_get()I
.end method

.method public static final native aux_audio_properties_none_get()I
.end method

.method public static final native aux_buffer_audio_capture_get()I
.end method

.method public static final native aux_buffer_audio_render_get()I
.end method

.method public static final native buddy_presence_away_get()I
.end method

.method public static final native buddy_presence_brb_get()I
.end method

.method public static final native buddy_presence_busy_get()I
.end method

.method public static final native buddy_presence_closed_get()I
.end method

.method public static final native buddy_presence_custom_get()I
.end method

.method public static final native buddy_presence_offline_get()I
.end method

.method public static final native buddy_presence_online_get()I
.end method

.method public static final native buddy_presence_online_slc_get()I
.end method

.method public static final native buddy_presence_onthephone_get()I
.end method

.method public static final native buddy_presence_outtolunch_get()I
.end method

.method public static final native buddy_presence_pending_get()I
.end method

.method public static final native buddy_presence_unknown_get()I
.end method

.method public static final native change_type_delete_get()I
.end method

.method public static final native change_type_set_get()I
.end method

.method public static final native channel_mode_auditorium_get()I
.end method

.method public static final native channel_mode_lecture_get()I
.end method

.method public static final native channel_mode_none_get()I
.end method

.method public static final native channel_mode_normal_get()I
.end method

.method public static final native channel_mode_open_get()I
.end method

.method public static final native channel_mode_presentation_get()I
.end method

.method public static final native channel_moderation_type_all_get()I
.end method

.method public static final native channel_moderation_type_current_user_get()I
.end method

.method public static final native channel_search_type_all_get()I
.end method

.method public static final native channel_search_type_non_positional_get()I
.end method

.method public static final native channel_search_type_positional_get()I
.end method

.method public static final native channel_type_normal_get()I
.end method

.method public static final native channel_type_positional_get()I
.end method

.method public static final native channel_unlock_get()I
.end method

.method public static final native clear_stats()V
.end method

.method public static final native connector_mode_normal_get()I
.end method

.method public static final native delete_vx_account_t(J)V
.end method

.method public static final native delete_vx_auto_accept_rule_t(J)V
.end method

.method public static final native delete_vx_block_rule_t(J)V
.end method

.method public static final native delete_vx_buddy_t(J)V
.end method

.method public static final native delete_vx_channel_favorite_group_t(J)V
.end method

.method public static final native delete_vx_channel_favorite_t(J)V
.end method

.method public static final native delete_vx_channel_t(J)V
.end method

.method public static final native delete_vx_device_t(J)V
.end method

.method public static final native delete_vx_group_t(J)V
.end method

.method public static final native delete_vx_participant_t(J)V
.end method

.method public static final native delete_vx_recording_frame_t(J)V
.end method

.method public static final native delete_vx_voice_font_t(J)V
.end method

.method public static final native destroy_evt(JLcom/vivox/service/vx_evt_base_t;)V
.end method

.method public static final native destroy_message(JLcom/vivox/service/vx_message_base_t;)V
.end method

.method public static final native destroy_req(JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native destroy_resp(JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native diagnostic_dump_level_all_get()I
.end method

.method public static final native dtmf_0_get()I
.end method

.method public static final native dtmf_1_get()I
.end method

.method public static final native dtmf_2_get()I
.end method

.method public static final native dtmf_3_get()I
.end method

.method public static final native dtmf_4_get()I
.end method

.method public static final native dtmf_5_get()I
.end method

.method public static final native dtmf_6_get()I
.end method

.method public static final native dtmf_7_get()I
.end method

.method public static final native dtmf_8_get()I
.end method

.method public static final native dtmf_9_get()I
.end method

.method public static final native dtmf_A_get()I
.end method

.method public static final native dtmf_B_get()I
.end method

.method public static final native dtmf_C_get()I
.end method

.method public static final native dtmf_D_get()I
.end method

.method public static final native dtmf_max_get()I
.end method

.method public static final native dtmf_pound_get()I
.end method

.method public static final native dtmf_star_get()I
.end method

.method public static final native evt_account_login_state_change_get()I
.end method

.method public static final native evt_aux_audio_properties_get()I
.end method

.method public static final native evt_buddy_and_group_list_changed_get()I
.end method

.method public static final native evt_buddy_changed_get()I
.end method

.method public static final native evt_buddy_group_changed_get()I
.end method

.method public static final native evt_buddy_presence_get()I
.end method

.method public static final native evt_idle_state_changed_get()I
.end method

.method public static final native evt_keyboard_mouse_get()I
.end method

.method public static final native evt_max_get()I
.end method

.method public static final native evt_media_completion_get()I
.end method

.method public static final native evt_media_stream_updated_get()I
.end method

.method public static final native evt_message_get()I
.end method

.method public static final native evt_network_message_get()I
.end method

.method public static final native evt_none_get()I
.end method

.method public static final native evt_participant_added_get()I
.end method

.method public static final native evt_participant_removed_get()I
.end method

.method public static final native evt_participant_updated_get()I
.end method

.method public static final native evt_server_app_data_get()I
.end method

.method public static final native evt_session_added_get()I
.end method

.method public static final native evt_session_notification_get()I
.end method

.method public static final native evt_session_removed_get()I
.end method

.method public static final native evt_session_updated_get()I
.end method

.method public static final native evt_sessiongroup_added_get()I
.end method

.method public static final native evt_sessiongroup_playback_frame_played_get()I
.end method

.method public static final native evt_sessiongroup_removed_get()I
.end method

.method public static final native evt_sessiongroup_updated_get()I
.end method

.method public static final native evt_subscription_get()I
.end method

.method public static final native evt_text_stream_updated_get()I
.end method

.method public static final native evt_user_app_data_get()I
.end method

.method public static final native evt_voice_service_connection_state_changed_get()I
.end method

.method public static final native free_sdk_string(J)V
.end method

.method public static final native get_java_system_property(Ljava/lang/String;)Ljava/lang/String;
.end method

.method public static final native get_next_message(I)J
.end method

.method public static final native get_next_message_no_wait()J
.end method

.method public static final native get_stats()J
.end method

.method public static final native log_error_get()I
.end method

.method public static final native log_to_none_get()I
.end method

.method public static final native login_state_error_get()I
.end method

.method public static final native login_state_logged_in_get()I
.end method

.method public static final native login_state_logged_out_get()I
.end method

.method public static final native login_state_logging_in_get()I
.end method

.method public static final native login_state_logging_out_get()I
.end method

.method public static final native login_state_resetting_get()I
.end method

.method public static final native media_codec_type_nm_get()I
.end method

.method public static final native media_codec_type_none_get()I
.end method

.method public static final native media_codec_type_pcmu_get()I
.end method

.method public static final native media_codec_type_siren14_get()I
.end method

.method public static final native media_codec_type_speex_get()I
.end method

.method public static final native media_completion_type_none_get()I
.end method

.method public static final native media_ringback_busy_get()I
.end method

.method public static final native media_ringback_none_get()I
.end method

.method public static final native media_ringback_ringing_get()I
.end method

.method public static final native media_type_none_get()I
.end method

.method public static final native message_none_get()I
.end method

.method public static final native message_to_xml(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native mode_auto_accept_get()I
.end method

.method public static final native mode_auto_add_get()I
.end method

.method public static final native mode_auto_answer_get()I
.end method

.method public static final native mode_busy_answer_get()I
.end method

.method public static final native mode_none_get()I
.end method

.method public static final native mode_verify_answer_get()I
.end method

.method public static final native msg_evt_subtype(JLcom/vivox/service/vx_message_base_t;)I
.end method

.method public static final native msg_none_get()I
.end method

.method public static final native msg_req_subtype(JLcom/vivox/service/vx_message_base_t;)I
.end method

.method public static final native msg_request_get()I
.end method

.method public static final native msg_resp_subtype(JLcom/vivox/service/vx_message_base_t;)I
.end method

.method public static final native mute_scope_all_get()I
.end method

.method public static final native mute_scope_audio_get()I
.end method

.method public static final native mute_scope_text_get()I
.end method

.method public static final native name_value_pairs_create(I)J
.end method

.method public static final native new_vx_account_t()J
.end method

.method public static final native new_vx_auto_accept_rule_t()J
.end method

.method public static final native new_vx_block_rule_t()J
.end method

.method public static final native new_vx_buddy_t()J
.end method

.method public static final native new_vx_channel_favorite_group_t()J
.end method

.method public static final native new_vx_channel_favorite_t()J
.end method

.method public static final native new_vx_channel_t()J
.end method

.method public static final native new_vx_connectivity_test_result_t()J
.end method

.method public static final native new_vx_device_t()J
.end method

.method public static final native new_vx_evt_account_login_state_change_t()J
.end method

.method public static final native new_vx_evt_aux_audio_properties_t()J
.end method

.method public static final native new_vx_evt_base_t()J
.end method

.method public static final native new_vx_evt_buddy_and_group_list_changed_t()J
.end method

.method public static final native new_vx_evt_buddy_changed_t()J
.end method

.method public static final native new_vx_evt_buddy_group_changed_t()J
.end method

.method public static final native new_vx_evt_buddy_presence_t()J
.end method

.method public static final native new_vx_evt_idle_state_changed_t()J
.end method

.method public static final native new_vx_evt_keyboard_mouse_t()J
.end method

.method public static final native new_vx_evt_media_completion_t()J
.end method

.method public static final native new_vx_evt_media_stream_updated_t()J
.end method

.method public static final native new_vx_evt_message_t()J
.end method

.method public static final native new_vx_evt_network_message_t()J
.end method

.method public static final native new_vx_evt_participant_added_t()J
.end method

.method public static final native new_vx_evt_participant_removed_t()J
.end method

.method public static final native new_vx_evt_participant_updated_t()J
.end method

.method public static final native new_vx_evt_server_app_data_t()J
.end method

.method public static final native new_vx_evt_session_added_t()J
.end method

.method public static final native new_vx_evt_session_notification_t()J
.end method

.method public static final native new_vx_evt_session_removed_t()J
.end method

.method public static final native new_vx_evt_session_updated_t()J
.end method

.method public static final native new_vx_evt_sessiongroup_added_t()J
.end method

.method public static final native new_vx_evt_sessiongroup_playback_frame_played_t()J
.end method

.method public static final native new_vx_evt_sessiongroup_removed_t()J
.end method

.method public static final native new_vx_evt_sessiongroup_updated_t()J
.end method

.method public static final native new_vx_evt_subscription_t()J
.end method

.method public static final native new_vx_evt_text_stream_updated_t()J
.end method

.method public static final native new_vx_evt_user_app_data_t()J
.end method

.method public static final native new_vx_evt_voice_service_connection_state_changed_t()J
.end method

.method public static final native new_vx_generic_credentials()J
.end method

.method public static final native new_vx_group_t()J
.end method

.method public static final native new_vx_message_base_t()J
.end method

.method public static final native new_vx_name_value_pair_t()J
.end method

.method public static final native new_vx_participant_t()J
.end method

.method public static final native new_vx_recording_frame_t()J
.end method

.method public static final native new_vx_req_account_anonymous_login_t()J
.end method

.method public static final native new_vx_req_account_authtoken_login_t()J
.end method

.method public static final native new_vx_req_account_buddy_delete_t()J
.end method

.method public static final native new_vx_req_account_buddy_search_t()J
.end method

.method public static final native new_vx_req_account_buddy_set_t()J
.end method

.method public static final native new_vx_req_account_buddygroup_delete_t()J
.end method

.method public static final native new_vx_req_account_buddygroup_set_t()J
.end method

.method public static final native new_vx_req_account_channel_add_acl_t()J
.end method

.method public static final native new_vx_req_account_channel_add_moderator_t()J
.end method

.method public static final native new_vx_req_account_channel_change_owner_t()J
.end method

.method public static final native new_vx_req_account_channel_create_t()J
.end method

.method public static final native new_vx_req_account_channel_delete_t()J
.end method

.method public static final native new_vx_req_account_channel_favorite_delete_t()J
.end method

.method public static final native new_vx_req_account_channel_favorite_group_delete_t()J
.end method

.method public static final native new_vx_req_account_channel_favorite_group_set_t()J
.end method

.method public static final native new_vx_req_account_channel_favorite_set_t()J
.end method

.method public static final native new_vx_req_account_channel_favorites_get_list_t()J
.end method

.method public static final native new_vx_req_account_channel_get_acl_t()J
.end method

.method public static final native new_vx_req_account_channel_get_info_t()J
.end method

.method public static final native new_vx_req_account_channel_get_moderators_t()J
.end method

.method public static final native new_vx_req_account_channel_get_participants_t()J
.end method

.method public static final native new_vx_req_account_channel_remove_acl_t()J
.end method

.method public static final native new_vx_req_account_channel_remove_moderator_t()J
.end method

.method public static final native new_vx_req_account_channel_search_t()J
.end method

.method public static final native new_vx_req_account_channel_update_t()J
.end method

.method public static final native new_vx_req_account_create_auto_accept_rule_t()J
.end method

.method public static final native new_vx_req_account_create_block_rule_t()J
.end method

.method public static final native new_vx_req_account_delete_auto_accept_rule_t()J
.end method

.method public static final native new_vx_req_account_delete_block_rule_t()J
.end method

.method public static final native new_vx_req_account_get_account_t()J
.end method

.method public static final native new_vx_req_account_get_session_fonts_t()J
.end method

.method public static final native new_vx_req_account_get_template_fonts_t()J
.end method

.method public static final native new_vx_req_account_list_auto_accept_rules_t()J
.end method

.method public static final native new_vx_req_account_list_block_rules_t()J
.end method

.method public static final native new_vx_req_account_list_buddies_and_groups_t()J
.end method

.method public static final native new_vx_req_account_login_t()J
.end method

.method public static final native new_vx_req_account_logout_t()J
.end method

.method public static final native new_vx_req_account_post_crash_dump_t()J
.end method

.method public static final native new_vx_req_account_send_message_t()J
.end method

.method public static final native new_vx_req_account_send_sms_t()J
.end method

.method public static final native new_vx_req_account_send_subscription_reply_t()J
.end method

.method public static final native new_vx_req_account_send_user_app_data_t()J
.end method

.method public static final native new_vx_req_account_set_login_properties_t()J
.end method

.method public static final native new_vx_req_account_set_presence_t()J
.end method

.method public static final native new_vx_req_account_update_account_t()J
.end method

.method public static final native new_vx_req_account_web_call_t()J
.end method

.method public static final native new_vx_req_aux_capture_audio_start_t()J
.end method

.method public static final native new_vx_req_aux_capture_audio_stop_t()J
.end method

.method public static final native new_vx_req_aux_connectivity_info_t()J
.end method

.method public static final native new_vx_req_aux_create_account_t()J
.end method

.method public static final native new_vx_req_aux_deactivate_account_t()J
.end method

.method public static final native new_vx_req_aux_diagnostic_state_dump_t()J
.end method

.method public static final native new_vx_req_aux_get_capture_devices_t()J
.end method

.method public static final native new_vx_req_aux_get_mic_level_t()J
.end method

.method public static final native new_vx_req_aux_get_render_devices_t()J
.end method

.method public static final native new_vx_req_aux_get_speaker_level_t()J
.end method

.method public static final native new_vx_req_aux_get_vad_properties_t()J
.end method

.method public static final native new_vx_req_aux_global_monitor_keyboard_mouse_t()J
.end method

.method public static final native new_vx_req_aux_notify_application_state_change_t()J
.end method

.method public static final native new_vx_req_aux_play_audio_buffer_t()J
.end method

.method public static final native new_vx_req_aux_reactivate_account_t()J
.end method

.method public static final native new_vx_req_aux_render_audio_modify_t()J
.end method

.method public static final native new_vx_req_aux_render_audio_start_t()J
.end method

.method public static final native new_vx_req_aux_render_audio_stop_t()J
.end method

.method public static final native new_vx_req_aux_reset_password_t()J
.end method

.method public static final native new_vx_req_aux_set_capture_device_t()J
.end method

.method public static final native new_vx_req_aux_set_idle_timeout_t()J
.end method

.method public static final native new_vx_req_aux_set_mic_level_t()J
.end method

.method public static final native new_vx_req_aux_set_render_device_t()J
.end method

.method public static final native new_vx_req_aux_set_speaker_level_t()J
.end method

.method public static final native new_vx_req_aux_set_vad_properties_t()J
.end method

.method public static final native new_vx_req_aux_start_buffer_capture_t()J
.end method

.method public static final native new_vx_req_base_t()J
.end method

.method public static final native new_vx_req_channel_ban_user_t()J
.end method

.method public static final native new_vx_req_channel_get_banned_users_t()J
.end method

.method public static final native new_vx_req_channel_kick_user_t()J
.end method

.method public static final native new_vx_req_channel_mute_all_users_t()J
.end method

.method public static final native new_vx_req_channel_mute_user_t()J
.end method

.method public static final native new_vx_req_channel_set_lock_mode_t()J
.end method

.method public static final native new_vx_req_connector_create_t()J
.end method

.method public static final native new_vx_req_connector_get_local_audio_info_t()J
.end method

.method public static final native new_vx_req_connector_initiate_shutdown_t()J
.end method

.method public static final native new_vx_req_connector_mute_local_mic_t()J
.end method

.method public static final native new_vx_req_connector_mute_local_speaker_t()J
.end method

.method public static final native new_vx_req_connector_set_local_mic_volume_t()J
.end method

.method public static final native new_vx_req_connector_set_local_speaker_volume_t()J
.end method

.method public static final native new_vx_req_session_channel_invite_user_t()J
.end method

.method public static final native new_vx_req_session_create_t()J
.end method

.method public static final native new_vx_req_session_media_connect_t()J
.end method

.method public static final native new_vx_req_session_media_disconnect_t()J
.end method

.method public static final native new_vx_req_session_mute_local_speaker_t()J
.end method

.method public static final native new_vx_req_session_send_dtmf_t()J
.end method

.method public static final native new_vx_req_session_send_message_t()J
.end method

.method public static final native new_vx_req_session_send_notification_t()J
.end method

.method public static final native new_vx_req_session_set_3d_position_t()J
.end method

.method public static final native new_vx_req_session_set_local_speaker_volume_t()J
.end method

.method public static final native new_vx_req_session_set_participant_mute_for_me_t()J
.end method

.method public static final native new_vx_req_session_set_participant_volume_for_me_t()J
.end method

.method public static final native new_vx_req_session_set_voice_font_t()J
.end method

.method public static final native new_vx_req_session_terminate_t()J
.end method

.method public static final native new_vx_req_session_text_connect_t()J
.end method

.method public static final native new_vx_req_session_text_disconnect_t()J
.end method

.method public static final native new_vx_req_sessiongroup_add_session_t()J
.end method

.method public static final native new_vx_req_sessiongroup_control_audio_injection_t()J
.end method

.method public static final native new_vx_req_sessiongroup_control_playback_t()J
.end method

.method public static final native new_vx_req_sessiongroup_control_recording_t()J
.end method

.method public static final native new_vx_req_sessiongroup_create_t()J
.end method

.method public static final native new_vx_req_sessiongroup_get_stats_t()J
.end method

.method public static final native new_vx_req_sessiongroup_remove_session_t()J
.end method

.method public static final native new_vx_req_sessiongroup_reset_focus_t()J
.end method

.method public static final native new_vx_req_sessiongroup_set_focus_t()J
.end method

.method public static final native new_vx_req_sessiongroup_set_playback_options_t()J
.end method

.method public static final native new_vx_req_sessiongroup_set_session_3d_position_t()J
.end method

.method public static final native new_vx_req_sessiongroup_set_tx_all_sessions_t()J
.end method

.method public static final native new_vx_req_sessiongroup_set_tx_no_session_t()J
.end method

.method public static final native new_vx_req_sessiongroup_set_tx_session_t()J
.end method

.method public static final native new_vx_req_sessiongroup_terminate_t()J
.end method

.method public static final native new_vx_req_sessiongroup_unset_focus_t()J
.end method

.method public static final native new_vx_resp_account_anonymous_login_t()J
.end method

.method public static final native new_vx_resp_account_authtoken_login_t()J
.end method

.method public static final native new_vx_resp_account_buddy_delete_t()J
.end method

.method public static final native new_vx_resp_account_buddy_search_t()J
.end method

.method public static final native new_vx_resp_account_buddy_set_t()J
.end method

.method public static final native new_vx_resp_account_buddygroup_delete_t()J
.end method

.method public static final native new_vx_resp_account_buddygroup_set_t()J
.end method

.method public static final native new_vx_resp_account_channel_add_acl_t()J
.end method

.method public static final native new_vx_resp_account_channel_add_moderator_t()J
.end method

.method public static final native new_vx_resp_account_channel_change_owner_t()J
.end method

.method public static final native new_vx_resp_account_channel_create_t()J
.end method

.method public static final native new_vx_resp_account_channel_delete_t()J
.end method

.method public static final native new_vx_resp_account_channel_favorite_delete_t()J
.end method

.method public static final native new_vx_resp_account_channel_favorite_group_delete_t()J
.end method

.method public static final native new_vx_resp_account_channel_favorite_group_set_t()J
.end method

.method public static final native new_vx_resp_account_channel_favorite_set_t()J
.end method

.method public static final native new_vx_resp_account_channel_favorites_get_list_t()J
.end method

.method public static final native new_vx_resp_account_channel_get_acl_t()J
.end method

.method public static final native new_vx_resp_account_channel_get_info_t()J
.end method

.method public static final native new_vx_resp_account_channel_get_moderators_t()J
.end method

.method public static final native new_vx_resp_account_channel_get_participants_t()J
.end method

.method public static final native new_vx_resp_account_channel_remove_acl_t()J
.end method

.method public static final native new_vx_resp_account_channel_remove_moderator_t()J
.end method

.method public static final native new_vx_resp_account_channel_search_t()J
.end method

.method public static final native new_vx_resp_account_channel_update_t()J
.end method

.method public static final native new_vx_resp_account_create_auto_accept_rule_t()J
.end method

.method public static final native new_vx_resp_account_create_block_rule_t()J
.end method

.method public static final native new_vx_resp_account_delete_auto_accept_rule_t()J
.end method

.method public static final native new_vx_resp_account_delete_block_rule_t()J
.end method

.method public static final native new_vx_resp_account_get_account_t()J
.end method

.method public static final native new_vx_resp_account_get_session_fonts_t()J
.end method

.method public static final native new_vx_resp_account_get_template_fonts_t()J
.end method

.method public static final native new_vx_resp_account_list_auto_accept_rules_t()J
.end method

.method public static final native new_vx_resp_account_list_block_rules_t()J
.end method

.method public static final native new_vx_resp_account_list_buddies_and_groups_t()J
.end method

.method public static final native new_vx_resp_account_login_t()J
.end method

.method public static final native new_vx_resp_account_logout_t()J
.end method

.method public static final native new_vx_resp_account_post_crash_dump_t()J
.end method

.method public static final native new_vx_resp_account_send_message_t()J
.end method

.method public static final native new_vx_resp_account_send_sms_t()J
.end method

.method public static final native new_vx_resp_account_send_subscription_reply_t()J
.end method

.method public static final native new_vx_resp_account_send_user_app_data_t()J
.end method

.method public static final native new_vx_resp_account_set_login_properties_t()J
.end method

.method public static final native new_vx_resp_account_set_presence_t()J
.end method

.method public static final native new_vx_resp_account_update_account_t()J
.end method

.method public static final native new_vx_resp_account_web_call_t()J
.end method

.method public static final native new_vx_resp_aux_capture_audio_start_t()J
.end method

.method public static final native new_vx_resp_aux_capture_audio_stop_t()J
.end method

.method public static final native new_vx_resp_aux_connectivity_info_t()J
.end method

.method public static final native new_vx_resp_aux_create_account_t()J
.end method

.method public static final native new_vx_resp_aux_deactivate_account_t()J
.end method

.method public static final native new_vx_resp_aux_diagnostic_state_dump_t()J
.end method

.method public static final native new_vx_resp_aux_get_capture_devices_t()J
.end method

.method public static final native new_vx_resp_aux_get_mic_level_t()J
.end method

.method public static final native new_vx_resp_aux_get_render_devices_t()J
.end method

.method public static final native new_vx_resp_aux_get_speaker_level_t()J
.end method

.method public static final native new_vx_resp_aux_get_vad_properties_t()J
.end method

.method public static final native new_vx_resp_aux_global_monitor_keyboard_mouse_t()J
.end method

.method public static final native new_vx_resp_aux_notify_application_state_change_t()J
.end method

.method public static final native new_vx_resp_aux_play_audio_buffer_t()J
.end method

.method public static final native new_vx_resp_aux_reactivate_account_t()J
.end method

.method public static final native new_vx_resp_aux_render_audio_modify_t()J
.end method

.method public static final native new_vx_resp_aux_render_audio_start_t()J
.end method

.method public static final native new_vx_resp_aux_render_audio_stop_t()J
.end method

.method public static final native new_vx_resp_aux_reset_password_t()J
.end method

.method public static final native new_vx_resp_aux_set_capture_device_t()J
.end method

.method public static final native new_vx_resp_aux_set_idle_timeout_t()J
.end method

.method public static final native new_vx_resp_aux_set_mic_level_t()J
.end method

.method public static final native new_vx_resp_aux_set_render_device_t()J
.end method

.method public static final native new_vx_resp_aux_set_speaker_level_t()J
.end method

.method public static final native new_vx_resp_aux_set_vad_properties_t()J
.end method

.method public static final native new_vx_resp_aux_start_buffer_capture_t()J
.end method

.method public static final native new_vx_resp_base_t()J
.end method

.method public static final native new_vx_resp_channel_ban_user_t()J
.end method

.method public static final native new_vx_resp_channel_get_banned_users_t()J
.end method

.method public static final native new_vx_resp_channel_kick_user_t()J
.end method

.method public static final native new_vx_resp_channel_mute_all_users_t()J
.end method

.method public static final native new_vx_resp_channel_mute_user_t()J
.end method

.method public static final native new_vx_resp_channel_set_lock_mode_t()J
.end method

.method public static final native new_vx_resp_connector_create_t()J
.end method

.method public static final native new_vx_resp_connector_get_local_audio_info_t()J
.end method

.method public static final native new_vx_resp_connector_initiate_shutdown_t()J
.end method

.method public static final native new_vx_resp_connector_mute_local_mic_t()J
.end method

.method public static final native new_vx_resp_connector_mute_local_speaker_t()J
.end method

.method public static final native new_vx_resp_connector_set_local_mic_volume_t()J
.end method

.method public static final native new_vx_resp_connector_set_local_speaker_volume_t()J
.end method

.method public static final native new_vx_resp_session_channel_invite_user_t()J
.end method

.method public static final native new_vx_resp_session_create_t()J
.end method

.method public static final native new_vx_resp_session_media_connect_t()J
.end method

.method public static final native new_vx_resp_session_media_disconnect_t()J
.end method

.method public static final native new_vx_resp_session_mute_local_speaker_t()J
.end method

.method public static final native new_vx_resp_session_send_dtmf_t()J
.end method

.method public static final native new_vx_resp_session_send_message_t()J
.end method

.method public static final native new_vx_resp_session_send_notification_t()J
.end method

.method public static final native new_vx_resp_session_set_3d_position_t()J
.end method

.method public static final native new_vx_resp_session_set_local_speaker_volume_t()J
.end method

.method public static final native new_vx_resp_session_set_participant_mute_for_me_t()J
.end method

.method public static final native new_vx_resp_session_set_participant_volume_for_me_t()J
.end method

.method public static final native new_vx_resp_session_set_voice_font_t()J
.end method

.method public static final native new_vx_resp_session_terminate_t()J
.end method

.method public static final native new_vx_resp_session_text_connect_t()J
.end method

.method public static final native new_vx_resp_session_text_disconnect_t()J
.end method

.method public static final native new_vx_resp_sessiongroup_add_session_t()J
.end method

.method public static final native new_vx_resp_sessiongroup_control_audio_injection_t()J
.end method

.method public static final native new_vx_resp_sessiongroup_control_playback_t()J
.end method

.method public static final native new_vx_resp_sessiongroup_control_recording_t()J
.end method

.method public static final native new_vx_resp_sessiongroup_create_t()J
.end method

.method public static final native new_vx_resp_sessiongroup_get_stats_t()J
.end method

.method public static final native new_vx_resp_sessiongroup_remove_session_t()J
.end method

.method public static final native new_vx_resp_sessiongroup_reset_focus_t()J
.end method

.method public static final native new_vx_resp_sessiongroup_set_focus_t()J
.end method

.method public static final native new_vx_resp_sessiongroup_set_playback_options_t()J
.end method

.method public static final native new_vx_resp_sessiongroup_set_session_3d_position_t()J
.end method

.method public static final native new_vx_resp_sessiongroup_set_tx_all_sessions_t()J
.end method

.method public static final native new_vx_resp_sessiongroup_set_tx_no_session_t()J
.end method

.method public static final native new_vx_resp_sessiongroup_set_tx_session_t()J
.end method

.method public static final native new_vx_resp_sessiongroup_terminate_t()J
.end method

.method public static final native new_vx_resp_sessiongroup_unset_focus_t()J
.end method

.method public static final native new_vx_sdk_config_t()J
.end method

.method public static final native new_vx_stat_sample_t()J
.end method

.method public static final native new_vx_stat_thread_t()J
.end method

.method public static final native new_vx_state_account_t()J
.end method

.method public static final native new_vx_state_buddy_contact_t()J
.end method

.method public static final native new_vx_state_buddy_group_t()J
.end method

.method public static final native new_vx_state_buddy_t()J
.end method

.method public static final native new_vx_state_connector_t()J
.end method

.method public static final native new_vx_state_participant_t()J
.end method

.method public static final native new_vx_state_session_t()J
.end method

.method public static final native new_vx_state_sessiongroup_t()J
.end method

.method public static final native new_vx_system_stats_t()J
.end method

.method public static final native new_vx_user_channel_t()J
.end method

.method public static final native new_vx_voice_font_t()J
.end method

.method public static final native notification_hand_lowered_get()I
.end method

.method public static final native notification_hand_raised_get()I
.end method

.method public static final native notification_max_get()I
.end method

.method public static final native notification_min_get()I
.end method

.method public static final native notification_not_typing_get()I
.end method

.method public static final native notification_typing_get()I
.end method

.method public static final native op_none_get()I
.end method

.method public static final native op_safeupdate_get()I
.end method

.method public static final native orientation_default_get()I
.end method

.method public static final native orientation_legacy_get()I
.end method

.method public static final native orientation_vivox_get()I
.end method

.method public static final native part_focus_get()I
.end method

.method public static final native part_moderator_get()I
.end method

.method public static final native part_user_get()I
.end method

.method public static final native participant_banned_get()I
.end method

.method public static final native participant_diagnostic_state_speaking_while_mic_muted_get()I
.end method

.method public static final native participant_diagnostic_state_speaking_while_mic_volume_zero_get()I
.end method

.method public static final native participant_kicked_get()I
.end method

.method public static final native participant_left_get()I
.end method

.method public static final native participant_moderator_get()I
.end method

.method public static final native participant_owner_get()I
.end method

.method public static final native participant_timeout_get()I
.end method

.method public static final native participant_user_get()I
.end method

.method public static final native register_logging_handler(Ljava/lang/String;Ljava/lang/String;I)I
.end method

.method public static final native register_message_notification_handler(Ljava/lang/String;Ljava/lang/String;)I
.end method

.method public static final native req_account_anonymous_login_get()I
.end method

.method public static final native req_account_authtoken_login_get()I
.end method

.method public static final native req_account_buddy_delete_get()I
.end method

.method public static final native req_account_buddy_search_get()I
.end method

.method public static final native req_account_buddy_set_get()I
.end method

.method public static final native req_account_buddygroup_delete_get()I
.end method

.method public static final native req_account_buddygroup_set_get()I
.end method

.method public static final native req_account_channel_add_acl_get()I
.end method

.method public static final native req_account_channel_add_moderator_get()I
.end method

.method public static final native req_account_channel_change_owner_get()I
.end method

.method public static final native req_account_channel_create_get()I
.end method

.method public static final native req_account_channel_delete_get()I
.end method

.method public static final native req_account_channel_favorite_delete_get()I
.end method

.method public static final native req_account_channel_favorite_group_delete_get()I
.end method

.method public static final native req_account_channel_favorite_group_set_get()I
.end method

.method public static final native req_account_channel_favorite_set_get()I
.end method

.method public static final native req_account_channel_favorites_get_list_get()I
.end method

.method public static final native req_account_channel_get_acl_get()I
.end method

.method public static final native req_account_channel_get_info_get()I
.end method

.method public static final native req_account_channel_get_moderators_get()I
.end method

.method public static final native req_account_channel_get_participants_get()I
.end method

.method public static final native req_account_channel_remove_acl_get()I
.end method

.method public static final native req_account_channel_remove_moderator_get()I
.end method

.method public static final native req_account_channel_search_get()I
.end method

.method public static final native req_account_channel_update_get()I
.end method

.method public static final native req_account_create_auto_accept_rule_get()I
.end method

.method public static final native req_account_create_block_rule_get()I
.end method

.method public static final native req_account_delete_auto_accept_rule_get()I
.end method

.method public static final native req_account_delete_block_rule_get()I
.end method

.method public static final native req_account_get_account_get()I
.end method

.method public static final native req_account_get_session_fonts_get()I
.end method

.method public static final native req_account_get_template_fonts_get()I
.end method

.method public static final native req_account_list_auto_accept_rules_get()I
.end method

.method public static final native req_account_list_block_rules_get()I
.end method

.method public static final native req_account_list_buddies_and_groups_get()I
.end method

.method public static final native req_account_login_get()I
.end method

.method public static final native req_account_logout_get()I
.end method

.method public static final native req_account_post_crash_dump_get()I
.end method

.method public static final native req_account_send_message_get()I
.end method

.method public static final native req_account_send_sms_get()I
.end method

.method public static final native req_account_send_subscription_reply_get()I
.end method

.method public static final native req_account_send_user_app_data_get()I
.end method

.method public static final native req_account_set_login_properties_get()I
.end method

.method public static final native req_account_set_presence_get()I
.end method

.method public static final native req_account_update_account_get()I
.end method

.method public static final native req_account_web_call_get()I
.end method

.method public static final native req_aux_capture_audio_start_get()I
.end method

.method public static final native req_aux_capture_audio_stop_get()I
.end method

.method public static final native req_aux_connectivity_info_get()I
.end method

.method public static final native req_aux_create_account_get()I
.end method

.method public static final native req_aux_deactivate_account_get()I
.end method

.method public static final native req_aux_diagnostic_state_dump_get()I
.end method

.method public static final native req_aux_get_capture_devices_get()I
.end method

.method public static final native req_aux_get_mic_level_get()I
.end method

.method public static final native req_aux_get_render_devices_get()I
.end method

.method public static final native req_aux_get_speaker_level_get()I
.end method

.method public static final native req_aux_get_vad_properties_get()I
.end method

.method public static final native req_aux_global_monitor_keyboard_mouse_get()I
.end method

.method public static final native req_aux_notify_application_state_change_get()I
.end method

.method public static final native req_aux_play_audio_buffer_get()I
.end method

.method public static final native req_aux_reactivate_account_get()I
.end method

.method public static final native req_aux_render_audio_modify_get()I
.end method

.method public static final native req_aux_render_audio_start_get()I
.end method

.method public static final native req_aux_render_audio_stop_get()I
.end method

.method public static final native req_aux_reset_password_get()I
.end method

.method public static final native req_aux_set_capture_device_get()I
.end method

.method public static final native req_aux_set_idle_timeout_get()I
.end method

.method public static final native req_aux_set_mic_level_get()I
.end method

.method public static final native req_aux_set_render_device_get()I
.end method

.method public static final native req_aux_set_speaker_level_get()I
.end method

.method public static final native req_aux_set_vad_properties_get()I
.end method

.method public static final native req_aux_start_buffer_capture_get()I
.end method

.method public static final native req_channel_ban_user_get()I
.end method

.method public static final native req_channel_get_banned_users_get()I
.end method

.method public static final native req_channel_kick_user_get()I
.end method

.method public static final native req_channel_mute_all_users_get()I
.end method

.method public static final native req_channel_mute_user_get()I
.end method

.method public static final native req_channel_set_lock_mode_get()I
.end method

.method public static final native req_connector_create_get()I
.end method

.method public static final native req_connector_get_local_audio_info_get()I
.end method

.method public static final native req_connector_initiate_shutdown_get()I
.end method

.method public static final native req_connector_mute_local_mic_get()I
.end method

.method public static final native req_connector_mute_local_speaker_get()I
.end method

.method public static final native req_connector_set_local_mic_volume_get()I
.end method

.method public static final native req_connector_set_local_speaker_volume_get()I
.end method

.method public static final native req_max_get()I
.end method

.method public static final native req_none_get()I
.end method

.method public static final native req_session_channel_invite_user_get()I
.end method

.method public static final native req_session_create_get()I
.end method

.method public static final native req_session_media_connect_get()I
.end method

.method public static final native req_session_media_disconnect_get()I
.end method

.method public static final native req_session_mute_local_speaker_get()I
.end method

.method public static final native req_session_send_dtmf_get()I
.end method

.method public static final native req_session_send_message_get()I
.end method

.method public static final native req_session_send_notification_get()I
.end method

.method public static final native req_session_set_3d_position_get()I
.end method

.method public static final native req_session_set_local_speaker_volume_get()I
.end method

.method public static final native req_session_set_participant_mute_for_me_get()I
.end method

.method public static final native req_session_set_participant_volume_for_me_get()I
.end method

.method public static final native req_session_set_voice_font_get()I
.end method

.method public static final native req_session_terminate_get()I
.end method

.method public static final native req_session_text_connect_get()I
.end method

.method public static final native req_session_text_disconnect_get()I
.end method

.method public static final native req_sessiongroup_add_session_get()I
.end method

.method public static final native req_sessiongroup_control_audio_injection_get()I
.end method

.method public static final native req_sessiongroup_control_playback_get()I
.end method

.method public static final native req_sessiongroup_control_recording_get()I
.end method

.method public static final native req_sessiongroup_create_get()I
.end method

.method public static final native req_sessiongroup_get_stats_get()I
.end method

.method public static final native req_sessiongroup_remove_session_get()I
.end method

.method public static final native req_sessiongroup_reset_focus_get()I
.end method

.method public static final native req_sessiongroup_set_focus_get()I
.end method

.method public static final native req_sessiongroup_set_playback_options_get()I
.end method

.method public static final native req_sessiongroup_set_session_3d_position_get()I
.end method

.method public static final native req_sessiongroup_set_tx_all_sessions_get()I
.end method

.method public static final native req_sessiongroup_set_tx_no_session_get()I
.end method

.method public static final native req_sessiongroup_set_tx_session_get()I
.end method

.method public static final native req_sessiongroup_terminate_get()I
.end method

.method public static final native req_sessiongroup_unset_focus_get()I
.end method

.method public static final native request_to_xml(JLcom/vivox/service/vx_req_base_t;)J
.end method

.method public static final native resp_account_anonymous_login_get()I
.end method

.method public static final native resp_account_authtoken_login_get()I
.end method

.method public static final native resp_account_buddy_delete_get()I
.end method

.method public static final native resp_account_buddy_search_get()I
.end method

.method public static final native resp_account_buddy_set_get()I
.end method

.method public static final native resp_account_buddygroup_delete_get()I
.end method

.method public static final native resp_account_buddygroup_set_get()I
.end method

.method public static final native resp_account_channel_add_acl_get()I
.end method

.method public static final native resp_account_channel_add_moderator_get()I
.end method

.method public static final native resp_account_channel_change_owner_get()I
.end method

.method public static final native resp_account_channel_create_get()I
.end method

.method public static final native resp_account_channel_delete_get()I
.end method

.method public static final native resp_account_channel_favorite_delete_get()I
.end method

.method public static final native resp_account_channel_favorite_group_delete_get()I
.end method

.method public static final native resp_account_channel_favorite_group_set_get()I
.end method

.method public static final native resp_account_channel_favorite_set_get()I
.end method

.method public static final native resp_account_channel_favorites_get_list_get()I
.end method

.method public static final native resp_account_channel_get_acl_get()I
.end method

.method public static final native resp_account_channel_get_info_get()I
.end method

.method public static final native resp_account_channel_get_list_get()I
.end method

.method public static final native resp_account_channel_get_moderators_get()I
.end method

.method public static final native resp_account_channel_get_participants_get()I
.end method

.method public static final native resp_account_channel_remove_acl_get()I
.end method

.method public static final native resp_account_channel_remove_moderator_get()I
.end method

.method public static final native resp_account_channel_search_get()I
.end method

.method public static final native resp_account_channel_update_get()I
.end method

.method public static final native resp_account_create_auto_accept_rule_get()I
.end method

.method public static final native resp_account_create_block_rule_get()I
.end method

.method public static final native resp_account_delete_auto_accept_rule_get()I
.end method

.method public static final native resp_account_delete_block_rule_get()I
.end method

.method public static final native resp_account_get_account_get()I
.end method

.method public static final native resp_account_get_session_fonts_get()I
.end method

.method public static final native resp_account_get_template_fonts_get()I
.end method

.method public static final native resp_account_list_auto_accept_rules_get()I
.end method

.method public static final native resp_account_list_block_rules_get()I
.end method

.method public static final native resp_account_list_buddies_and_groups_get()I
.end method

.method public static final native resp_account_login_get()I
.end method

.method public static final native resp_account_logout_get()I
.end method

.method public static final native resp_account_post_crash_dump_get()I
.end method

.method public static final native resp_account_send_message_get()I
.end method

.method public static final native resp_account_send_sms_get()I
.end method

.method public static final native resp_account_send_subscription_reply_get()I
.end method

.method public static final native resp_account_send_user_app_data_get()I
.end method

.method public static final native resp_account_set_login_properties_get()I
.end method

.method public static final native resp_account_set_presence_get()I
.end method

.method public static final native resp_account_update_account_get()I
.end method

.method public static final native resp_account_web_call_get()I
.end method

.method public static final native resp_aux_capture_audio_start_get()I
.end method

.method public static final native resp_aux_capture_audio_stop_get()I
.end method

.method public static final native resp_aux_connectivity_info_get()I
.end method

.method public static final native resp_aux_create_account_get()I
.end method

.method public static final native resp_aux_deactivate_account_get()I
.end method

.method public static final native resp_aux_diagnostic_state_dump_get()I
.end method

.method public static final native resp_aux_get_capture_devices_get()I
.end method

.method public static final native resp_aux_get_mic_level_get()I
.end method

.method public static final native resp_aux_get_render_devices_get()I
.end method

.method public static final native resp_aux_get_speaker_level_get()I
.end method

.method public static final native resp_aux_get_vad_properties_get()I
.end method

.method public static final native resp_aux_global_monitor_keyboard_mouse_get()I
.end method

.method public static final native resp_aux_notify_application_state_change_get()I
.end method

.method public static final native resp_aux_play_audio_buffer_get()I
.end method

.method public static final native resp_aux_reactivate_account_get()I
.end method

.method public static final native resp_aux_render_audio_modify_get()I
.end method

.method public static final native resp_aux_render_audio_start_get()I
.end method

.method public static final native resp_aux_render_audio_stop_get()I
.end method

.method public static final native resp_aux_reset_password_get()I
.end method

.method public static final native resp_aux_set_capture_device_get()I
.end method

.method public static final native resp_aux_set_idle_timeout_get()I
.end method

.method public static final native resp_aux_set_mic_level_get()I
.end method

.method public static final native resp_aux_set_render_device_get()I
.end method

.method public static final native resp_aux_set_speaker_level_get()I
.end method

.method public static final native resp_aux_set_vad_properties_get()I
.end method

.method public static final native resp_aux_start_buffer_capture_get()I
.end method

.method public static final native resp_channel_ban_user_get()I
.end method

.method public static final native resp_channel_get_banned_users_get()I
.end method

.method public static final native resp_channel_kick_user_get()I
.end method

.method public static final native resp_channel_mute_all_users_get()I
.end method

.method public static final native resp_channel_mute_user_get()I
.end method

.method public static final native resp_channel_set_lock_mode_get()I
.end method

.method public static final native resp_connector_create_get()I
.end method

.method public static final native resp_connector_get_local_audio_info_get()I
.end method

.method public static final native resp_connector_initiate_shutdown_get()I
.end method

.method public static final native resp_connector_mute_local_mic_get()I
.end method

.method public static final native resp_connector_mute_local_speaker_get()I
.end method

.method public static final native resp_connector_set_local_mic_volume_get()I
.end method

.method public static final native resp_connector_set_local_speaker_volume_get()I
.end method

.method public static final native resp_max_get()I
.end method

.method public static final native resp_none_get()I
.end method

.method public static final native resp_session_channel_invite_user_get()I
.end method

.method public static final native resp_session_create_get()I
.end method

.method public static final native resp_session_media_connect_get()I
.end method

.method public static final native resp_session_media_disconnect_get()I
.end method

.method public static final native resp_session_mute_local_speaker_get()I
.end method

.method public static final native resp_session_send_dtmf_get()I
.end method

.method public static final native resp_session_send_message_get()I
.end method

.method public static final native resp_session_send_notification_get()I
.end method

.method public static final native resp_session_set_3d_position_get()I
.end method

.method public static final native resp_session_set_local_speaker_volume_get()I
.end method

.method public static final native resp_session_set_participant_mute_for_me_get()I
.end method

.method public static final native resp_session_set_participant_volume_for_me_get()I
.end method

.method public static final native resp_session_set_voice_font_get()I
.end method

.method public static final native resp_session_terminate_get()I
.end method

.method public static final native resp_session_text_connect_get()I
.end method

.method public static final native resp_session_text_disconnect_get()I
.end method

.method public static final native resp_sessiongroup_add_session_get()I
.end method

.method public static final native resp_sessiongroup_control_audio_injection_get()I
.end method

.method public static final native resp_sessiongroup_control_playback_get()I
.end method

.method public static final native resp_sessiongroup_control_recording_get()I
.end method

.method public static final native resp_sessiongroup_create_get()I
.end method

.method public static final native resp_sessiongroup_get_stats_get()I
.end method

.method public static final native resp_sessiongroup_remove_session_get()I
.end method

.method public static final native resp_sessiongroup_reset_focus_get()I
.end method

.method public static final native resp_sessiongroup_set_focus_get()I
.end method

.method public static final native resp_sessiongroup_set_playback_options_get()I
.end method

.method public static final native resp_sessiongroup_set_session_3d_position_get()I
.end method

.method public static final native resp_sessiongroup_set_tx_all_sessions_get()I
.end method

.method public static final native resp_sessiongroup_set_tx_no_session_get()I
.end method

.method public static final native resp_sessiongroup_set_tx_session_get()I
.end method

.method public static final native resp_sessiongroup_terminate_get()I
.end method

.method public static final native resp_sessiongroup_unset_focus_get()I
.end method

.method public static final native rule_none_get()I
.end method

.method public static final native sdk_string_to_string(J)Ljava/lang/String;
.end method

.method public static final native session_handle_type_unique_get()I
.end method

.method public static final native session_media_none_get()I
.end method

.method public static final native session_notification_none_get()I
.end method

.method public static final native session_text_disconnected_get()I
.end method

.method public static final native sessiongroup_audio_injection_get()I
.end method

.method public static final native sessiongroup_type_normal_get()I
.end method

.method public static final native sessiongroup_type_playback_get()I
.end method

.method public static final native set_request_cookie(JLcom/vivox/service/vx_req_base_t;Ljava/lang/String;)V
.end method

.method public static final native status_free_get()I
.end method

.method public static final native status_none_get()I
.end method

.method public static final native status_not_free_get()I
.end method

.method public static final native subscription_presence_get()I
.end method

.method public static final native termination_status_none_get()I
.end method

.method public static final native text_mode_disabled_get()I
.end method

.method public static final native type_none_get()I
.end method

.method public static final native type_root_get()I
.end method

.method public static final native type_user_get()I
.end method

.method public static final native vx_account_create(J)V
.end method

.method public static final native vx_account_free(JLcom/vivox/service/vx_account_t;)V
.end method

.method public static final native vx_account_t_carrier_get(JLcom/vivox/service/vx_account_t;)Ljava/lang/String;
.end method

.method public static final native vx_account_t_carrier_set(JLcom/vivox/service/vx_account_t;Ljava/lang/String;)V
.end method

.method public static final native vx_account_t_created_date_get(JLcom/vivox/service/vx_account_t;)Ljava/lang/String;
.end method

.method public static final native vx_account_t_created_date_set(JLcom/vivox/service/vx_account_t;Ljava/lang/String;)V
.end method

.method public static final native vx_account_t_displayname_get(JLcom/vivox/service/vx_account_t;)Ljava/lang/String;
.end method

.method public static final native vx_account_t_displayname_set(JLcom/vivox/service/vx_account_t;Ljava/lang/String;)V
.end method

.method public static final native vx_account_t_email_get(JLcom/vivox/service/vx_account_t;)Ljava/lang/String;
.end method

.method public static final native vx_account_t_email_set(JLcom/vivox/service/vx_account_t;Ljava/lang/String;)V
.end method

.method public static final native vx_account_t_firstname_get(JLcom/vivox/service/vx_account_t;)Ljava/lang/String;
.end method

.method public static final native vx_account_t_firstname_set(JLcom/vivox/service/vx_account_t;Ljava/lang/String;)V
.end method

.method public static final native vx_account_t_lastname_get(JLcom/vivox/service/vx_account_t;)Ljava/lang/String;
.end method

.method public static final native vx_account_t_lastname_set(JLcom/vivox/service/vx_account_t;Ljava/lang/String;)V
.end method

.method public static final native vx_account_t_phone_get(JLcom/vivox/service/vx_account_t;)Ljava/lang/String;
.end method

.method public static final native vx_account_t_phone_set(JLcom/vivox/service/vx_account_t;Ljava/lang/String;)V
.end method

.method public static final native vx_account_t_uri_get(JLcom/vivox/service/vx_account_t;)Ljava/lang/String;
.end method

.method public static final native vx_account_t_uri_set(JLcom/vivox/service/vx_account_t;Ljava/lang/String;)V
.end method

.method public static final native vx_account_t_username_get(JLcom/vivox/service/vx_account_t;)Ljava/lang/String;
.end method

.method public static final native vx_account_t_username_set(JLcom/vivox/service/vx_account_t;Ljava/lang/String;)V
.end method

.method public static final native vx_alloc_sdk_handle(Ljava/lang/String;IJ)I
.end method

.method public static final native vx_apply_font_to_file(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)I
.end method

.method public static final native vx_apply_font_to_file_return_energy_ratio(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;J)I
.end method

.method public static final native vx_apply_font_to_vxz_file_return_energy_ratio(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;J)I
.end method

.method public static final native vx_auto_accept_rule_create(J)V
.end method

.method public static final native vx_auto_accept_rule_free(JLcom/vivox/service/vx_auto_accept_rule_t;)V
.end method

.method public static final native vx_auto_accept_rule_t_auto_accept_mask_get(JLcom/vivox/service/vx_auto_accept_rule_t;)Ljava/lang/String;
.end method

.method public static final native vx_auto_accept_rule_t_auto_accept_mask_set(JLcom/vivox/service/vx_auto_accept_rule_t;Ljava/lang/String;)V
.end method

.method public static final native vx_auto_accept_rule_t_auto_accept_nickname_get(JLcom/vivox/service/vx_auto_accept_rule_t;)Ljava/lang/String;
.end method

.method public static final native vx_auto_accept_rule_t_auto_accept_nickname_set(JLcom/vivox/service/vx_auto_accept_rule_t;Ljava/lang/String;)V
.end method

.method public static final native vx_auto_accept_rule_t_auto_add_as_buddy_get(JLcom/vivox/service/vx_auto_accept_rule_t;)I
.end method

.method public static final native vx_auto_accept_rule_t_auto_add_as_buddy_set(JLcom/vivox/service/vx_auto_accept_rule_t;I)V
.end method

.method public static final native vx_auto_accept_rules_create(IJ)V
.end method

.method public static final native vx_auto_accept_rules_free(JI)V
.end method

.method public static final native vx_block_rule_create(J)V
.end method

.method public static final native vx_block_rule_free(JLcom/vivox/service/vx_block_rule_t;)V
.end method

.method public static final native vx_block_rule_t_block_mask_get(JLcom/vivox/service/vx_block_rule_t;)Ljava/lang/String;
.end method

.method public static final native vx_block_rule_t_block_mask_set(JLcom/vivox/service/vx_block_rule_t;Ljava/lang/String;)V
.end method

.method public static final native vx_block_rule_t_presence_only_get(JLcom/vivox/service/vx_block_rule_t;)I
.end method

.method public static final native vx_block_rule_t_presence_only_set(JLcom/vivox/service/vx_block_rule_t;I)V
.end method

.method public static final native vx_block_rules_create(IJ)V
.end method

.method public static final native vx_block_rules_free(JI)V
.end method

.method public static final native vx_buddy_create(J)V
.end method

.method public static final native vx_buddy_free(JLcom/vivox/service/vx_buddy_t;)V
.end method

.method public static final native vx_buddy_list_create(IJ)V
.end method

.method public static final native vx_buddy_list_free(JI)V
.end method

.method public static final native vx_buddy_t_account_id_get(JLcom/vivox/service/vx_buddy_t;)I
.end method

.method public static final native vx_buddy_t_account_id_set(JLcom/vivox/service/vx_buddy_t;I)V
.end method

.method public static final native vx_buddy_t_account_name_get(JLcom/vivox/service/vx_buddy_t;)Ljava/lang/String;
.end method

.method public static final native vx_buddy_t_account_name_set(JLcom/vivox/service/vx_buddy_t;Ljava/lang/String;)V
.end method

.method public static final native vx_buddy_t_buddy_data_get(JLcom/vivox/service/vx_buddy_t;)Ljava/lang/String;
.end method

.method public static final native vx_buddy_t_buddy_data_set(JLcom/vivox/service/vx_buddy_t;Ljava/lang/String;)V
.end method

.method public static final native vx_buddy_t_buddy_uri_get(JLcom/vivox/service/vx_buddy_t;)Ljava/lang/String;
.end method

.method public static final native vx_buddy_t_buddy_uri_set(JLcom/vivox/service/vx_buddy_t;Ljava/lang/String;)V
.end method

.method public static final native vx_buddy_t_display_name_get(JLcom/vivox/service/vx_buddy_t;)Ljava/lang/String;
.end method

.method public static final native vx_buddy_t_display_name_set(JLcom/vivox/service/vx_buddy_t;Ljava/lang/String;)V
.end method

.method public static final native vx_buddy_t_parent_group_id_get(JLcom/vivox/service/vx_buddy_t;)I
.end method

.method public static final native vx_buddy_t_parent_group_id_set(JLcom/vivox/service/vx_buddy_t;I)V
.end method

.method public static final native vx_channel_create(J)V
.end method

.method public static final native vx_channel_favorite_create(J)V
.end method

.method public static final native vx_channel_favorite_free(JLcom/vivox/service/vx_channel_favorite_t;)V
.end method

.method public static final native vx_channel_favorite_group_create(J)V
.end method

.method public static final native vx_channel_favorite_group_free(JLcom/vivox/service/vx_channel_favorite_group_t;)V
.end method

.method public static final native vx_channel_favorite_group_list_create(IJ)V
.end method

.method public static final native vx_channel_favorite_group_list_free(JI)V
.end method

.method public static final native vx_channel_favorite_group_t_favorite_group_data_get(JLcom/vivox/service/vx_channel_favorite_group_t;)Ljava/lang/String;
.end method

.method public static final native vx_channel_favorite_group_t_favorite_group_data_set(JLcom/vivox/service/vx_channel_favorite_group_t;Ljava/lang/String;)V
.end method

.method public static final native vx_channel_favorite_group_t_favorite_group_id_get(JLcom/vivox/service/vx_channel_favorite_group_t;)I
.end method

.method public static final native vx_channel_favorite_group_t_favorite_group_id_set(JLcom/vivox/service/vx_channel_favorite_group_t;I)V
.end method

.method public static final native vx_channel_favorite_group_t_favorite_group_modified_get(JLcom/vivox/service/vx_channel_favorite_group_t;)Ljava/lang/String;
.end method

.method public static final native vx_channel_favorite_group_t_favorite_group_modified_set(JLcom/vivox/service/vx_channel_favorite_group_t;Ljava/lang/String;)V
.end method

.method public static final native vx_channel_favorite_group_t_favorite_group_name_get(JLcom/vivox/service/vx_channel_favorite_group_t;)Ljava/lang/String;
.end method

.method public static final native vx_channel_favorite_group_t_favorite_group_name_set(JLcom/vivox/service/vx_channel_favorite_group_t;Ljava/lang/String;)V
.end method

.method public static final native vx_channel_favorite_list_create(IJ)V
.end method

.method public static final native vx_channel_favorite_list_free(JI)V
.end method

.method public static final native vx_channel_favorite_t_channel_active_participants_get(JLcom/vivox/service/vx_channel_favorite_t;)I
.end method

.method public static final native vx_channel_favorite_t_channel_active_participants_set(JLcom/vivox/service/vx_channel_favorite_t;I)V
.end method

.method public static final native vx_channel_favorite_t_channel_capacity_get(JLcom/vivox/service/vx_channel_favorite_t;)I
.end method

.method public static final native vx_channel_favorite_t_channel_capacity_set(JLcom/vivox/service/vx_channel_favorite_t;I)V
.end method

.method public static final native vx_channel_favorite_t_channel_description_get(JLcom/vivox/service/vx_channel_favorite_t;)Ljava/lang/String;
.end method

.method public static final native vx_channel_favorite_t_channel_description_set(JLcom/vivox/service/vx_channel_favorite_t;Ljava/lang/String;)V
.end method

.method public static final native vx_channel_favorite_t_channel_is_persistent_get(JLcom/vivox/service/vx_channel_favorite_t;)I
.end method

.method public static final native vx_channel_favorite_t_channel_is_persistent_set(JLcom/vivox/service/vx_channel_favorite_t;I)V
.end method

.method public static final native vx_channel_favorite_t_channel_is_protected_get(JLcom/vivox/service/vx_channel_favorite_t;)I
.end method

.method public static final native vx_channel_favorite_t_channel_is_protected_set(JLcom/vivox/service/vx_channel_favorite_t;I)V
.end method

.method public static final native vx_channel_favorite_t_channel_limit_get(JLcom/vivox/service/vx_channel_favorite_t;)I
.end method

.method public static final native vx_channel_favorite_t_channel_limit_set(JLcom/vivox/service/vx_channel_favorite_t;I)V
.end method

.method public static final native vx_channel_favorite_t_channel_modified_get(JLcom/vivox/service/vx_channel_favorite_t;)Ljava/lang/String;
.end method

.method public static final native vx_channel_favorite_t_channel_modified_set(JLcom/vivox/service/vx_channel_favorite_t;Ljava/lang/String;)V
.end method

.method public static final native vx_channel_favorite_t_channel_owner_display_name_get(JLcom/vivox/service/vx_channel_favorite_t;)Ljava/lang/String;
.end method

.method public static final native vx_channel_favorite_t_channel_owner_display_name_set(JLcom/vivox/service/vx_channel_favorite_t;Ljava/lang/String;)V
.end method

.method public static final native vx_channel_favorite_t_channel_owner_get(JLcom/vivox/service/vx_channel_favorite_t;)Ljava/lang/String;
.end method

.method public static final native vx_channel_favorite_t_channel_owner_set(JLcom/vivox/service/vx_channel_favorite_t;Ljava/lang/String;)V
.end method

.method public static final native vx_channel_favorite_t_channel_owner_user_name_get(JLcom/vivox/service/vx_channel_favorite_t;)Ljava/lang/String;
.end method

.method public static final native vx_channel_favorite_t_channel_owner_user_name_set(JLcom/vivox/service/vx_channel_favorite_t;Ljava/lang/String;)V
.end method

.method public static final native vx_channel_favorite_t_channel_size_get(JLcom/vivox/service/vx_channel_favorite_t;)I
.end method

.method public static final native vx_channel_favorite_t_channel_size_set(JLcom/vivox/service/vx_channel_favorite_t;I)V
.end method

.method public static final native vx_channel_favorite_t_channel_uri_get(JLcom/vivox/service/vx_channel_favorite_t;)Ljava/lang/String;
.end method

.method public static final native vx_channel_favorite_t_channel_uri_set(JLcom/vivox/service/vx_channel_favorite_t;Ljava/lang/String;)V
.end method

.method public static final native vx_channel_favorite_t_favorite_data_get(JLcom/vivox/service/vx_channel_favorite_t;)Ljava/lang/String;
.end method

.method public static final native vx_channel_favorite_t_favorite_data_set(JLcom/vivox/service/vx_channel_favorite_t;Ljava/lang/String;)V
.end method

.method public static final native vx_channel_favorite_t_favorite_display_name_get(JLcom/vivox/service/vx_channel_favorite_t;)Ljava/lang/String;
.end method

.method public static final native vx_channel_favorite_t_favorite_display_name_set(JLcom/vivox/service/vx_channel_favorite_t;Ljava/lang/String;)V
.end method

.method public static final native vx_channel_favorite_t_favorite_group_id_get(JLcom/vivox/service/vx_channel_favorite_t;)I
.end method

.method public static final native vx_channel_favorite_t_favorite_group_id_set(JLcom/vivox/service/vx_channel_favorite_t;I)V
.end method

.method public static final native vx_channel_favorite_t_favorite_id_get(JLcom/vivox/service/vx_channel_favorite_t;)I
.end method

.method public static final native vx_channel_favorite_t_favorite_id_set(JLcom/vivox/service/vx_channel_favorite_t;I)V
.end method

.method public static final native vx_channel_free(JLcom/vivox/service/vx_channel_t;)V
.end method

.method public static final native vx_channel_list_create(IJ)V
.end method

.method public static final native vx_channel_list_free(JI)V
.end method

.method public static final native vx_channel_t_active_participants_get(JLcom/vivox/service/vx_channel_t;)I
.end method

.method public static final native vx_channel_t_active_participants_set(JLcom/vivox/service/vx_channel_t;I)V
.end method

.method public static final native vx_channel_t_capacity_get(JLcom/vivox/service/vx_channel_t;)I
.end method

.method public static final native vx_channel_t_capacity_set(JLcom/vivox/service/vx_channel_t;I)V
.end method

.method public static final native vx_channel_t_channel_desc_get(JLcom/vivox/service/vx_channel_t;)Ljava/lang/String;
.end method

.method public static final native vx_channel_t_channel_desc_set(JLcom/vivox/service/vx_channel_t;Ljava/lang/String;)V
.end method

.method public static final native vx_channel_t_channel_id_get(JLcom/vivox/service/vx_channel_t;)I
.end method

.method public static final native vx_channel_t_channel_id_set(JLcom/vivox/service/vx_channel_t;I)V
.end method

.method public static final native vx_channel_t_channel_name_get(JLcom/vivox/service/vx_channel_t;)Ljava/lang/String;
.end method

.method public static final native vx_channel_t_channel_name_set(JLcom/vivox/service/vx_channel_t;Ljava/lang/String;)V
.end method

.method public static final native vx_channel_t_channel_uri_get(JLcom/vivox/service/vx_channel_t;)Ljava/lang/String;
.end method

.method public static final native vx_channel_t_channel_uri_set(JLcom/vivox/service/vx_channel_t;Ljava/lang/String;)V
.end method

.method public static final native vx_channel_t_clamping_dist_get(JLcom/vivox/service/vx_channel_t;)I
.end method

.method public static final native vx_channel_t_clamping_dist_set(JLcom/vivox/service/vx_channel_t;I)V
.end method

.method public static final native vx_channel_t_dist_model_get(JLcom/vivox/service/vx_channel_t;)I
.end method

.method public static final native vx_channel_t_dist_model_set(JLcom/vivox/service/vx_channel_t;I)V
.end method

.method public static final native vx_channel_t_encrypt_audio_get(JLcom/vivox/service/vx_channel_t;)I
.end method

.method public static final native vx_channel_t_encrypt_audio_set(JLcom/vivox/service/vx_channel_t;I)V
.end method

.method public static final native vx_channel_t_host_get(JLcom/vivox/service/vx_channel_t;)Ljava/lang/String;
.end method

.method public static final native vx_channel_t_host_set(JLcom/vivox/service/vx_channel_t;Ljava/lang/String;)V
.end method

.method public static final native vx_channel_t_is_persistent_get(JLcom/vivox/service/vx_channel_t;)I
.end method

.method public static final native vx_channel_t_is_persistent_set(JLcom/vivox/service/vx_channel_t;I)V
.end method

.method public static final native vx_channel_t_is_protected_get(JLcom/vivox/service/vx_channel_t;)I
.end method

.method public static final native vx_channel_t_is_protected_set(JLcom/vivox/service/vx_channel_t;I)V
.end method

.method public static final native vx_channel_t_limit_get(JLcom/vivox/service/vx_channel_t;)I
.end method

.method public static final native vx_channel_t_limit_set(JLcom/vivox/service/vx_channel_t;I)V
.end method

.method public static final native vx_channel_t_max_gain_get(JLcom/vivox/service/vx_channel_t;)D
.end method

.method public static final native vx_channel_t_max_gain_set(JLcom/vivox/service/vx_channel_t;D)V
.end method

.method public static final native vx_channel_t_max_range_get(JLcom/vivox/service/vx_channel_t;)I
.end method

.method public static final native vx_channel_t_max_range_set(JLcom/vivox/service/vx_channel_t;I)V
.end method

.method public static final native vx_channel_t_mode_get(JLcom/vivox/service/vx_channel_t;)I
.end method

.method public static final native vx_channel_t_mode_set(JLcom/vivox/service/vx_channel_t;I)V
.end method

.method public static final native vx_channel_t_modified_get(JLcom/vivox/service/vx_channel_t;)Ljava/lang/String;
.end method

.method public static final native vx_channel_t_modified_set(JLcom/vivox/service/vx_channel_t;Ljava/lang/String;)V
.end method

.method public static final native vx_channel_t_owner_display_name_get(JLcom/vivox/service/vx_channel_t;)Ljava/lang/String;
.end method

.method public static final native vx_channel_t_owner_display_name_set(JLcom/vivox/service/vx_channel_t;Ljava/lang/String;)V
.end method

.method public static final native vx_channel_t_owner_get(JLcom/vivox/service/vx_channel_t;)Ljava/lang/String;
.end method

.method public static final native vx_channel_t_owner_set(JLcom/vivox/service/vx_channel_t;Ljava/lang/String;)V
.end method

.method public static final native vx_channel_t_owner_user_name_get(JLcom/vivox/service/vx_channel_t;)Ljava/lang/String;
.end method

.method public static final native vx_channel_t_owner_user_name_set(JLcom/vivox/service/vx_channel_t;Ljava/lang/String;)V
.end method

.method public static final native vx_channel_t_roll_off_get(JLcom/vivox/service/vx_channel_t;)D
.end method

.method public static final native vx_channel_t_roll_off_set(JLcom/vivox/service/vx_channel_t;D)V
.end method

.method public static final native vx_channel_t_size_get(JLcom/vivox/service/vx_channel_t;)I
.end method

.method public static final native vx_channel_t_size_set(JLcom/vivox/service/vx_channel_t;I)V
.end method

.method public static final native vx_channel_t_type_get(JLcom/vivox/service/vx_channel_t;)I
.end method

.method public static final native vx_channel_t_type_set(JLcom/vivox/service/vx_channel_t;I)V
.end method

.method public static final native vx_connectivity_test_result_create(JI)V
.end method

.method public static final native vx_connectivity_test_result_free(JLcom/vivox/service/vx_connectivity_test_result_t;)V
.end method

.method public static final native vx_connectivity_test_result_t_test_additional_info_get(JLcom/vivox/service/vx_connectivity_test_result_t;)Ljava/lang/String;
.end method

.method public static final native vx_connectivity_test_result_t_test_additional_info_set(JLcom/vivox/service/vx_connectivity_test_result_t;Ljava/lang/String;)V
.end method

.method public static final native vx_connectivity_test_result_t_test_error_code_get(JLcom/vivox/service/vx_connectivity_test_result_t;)I
.end method

.method public static final native vx_connectivity_test_result_t_test_error_code_set(JLcom/vivox/service/vx_connectivity_test_result_t;I)V
.end method

.method public static final native vx_connectivity_test_result_t_test_type_get(JLcom/vivox/service/vx_connectivity_test_result_t;)I
.end method

.method public static final native vx_connectivity_test_result_t_test_type_set(JLcom/vivox/service/vx_connectivity_test_result_t;I)V
.end method

.method public static final native vx_connectivity_test_results_create(IJ)V
.end method

.method public static final native vx_connectivity_test_results_free(JI)V
.end method

.method public static final native vx_cookie_create(Ljava/lang/String;J)V
.end method

.method public static final native vx_cookie_free(J)V
.end method

.method public static final native vx_copy_audioBuffer(J)J
.end method

.method public static final native vx_create_account(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)I
.end method

.method public static final native vx_delete_crash_dump(I)I
.end method

.method public static final native vx_device_create(J)V
.end method

.method public static final native vx_device_free(JLcom/vivox/service/vx_device_t;)V
.end method

.method public static final native vx_device_t_device_get(JLcom/vivox/service/vx_device_t;)Ljava/lang/String;
.end method

.method public static final native vx_device_t_device_set(JLcom/vivox/service/vx_device_t;Ljava/lang/String;)V
.end method

.method public static final native vx_device_t_device_type_get(JLcom/vivox/service/vx_device_t;)I
.end method

.method public static final native vx_device_t_device_type_set(JLcom/vivox/service/vx_device_t;I)V
.end method

.method public static final native vx_device_t_display_name_get(JLcom/vivox/service/vx_device_t;)Ljava/lang/String;
.end method

.method public static final native vx_device_t_display_name_set(JLcom/vivox/service/vx_device_t;Ljava/lang/String;)V
.end method

.method public static final native vx_device_type_default_system_get()I
.end method

.method public static final native vx_device_type_null_get()I
.end method

.method public static final native vx_device_type_specific_device_get()I
.end method

.method public static final native vx_devices_create(IJ)V
.end method

.method public static final native vx_devices_free(JI)V
.end method

.method public static final native vx_event_to_xml(JJ)V
.end method

.method public static final native vx_evt_account_login_state_change_t_account_handle_get(JLcom/vivox/service/vx_evt_account_login_state_change_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_account_login_state_change_t_account_handle_set(JLcom/vivox/service/vx_evt_account_login_state_change_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_account_login_state_change_t_base_get(JLcom/vivox/service/vx_evt_account_login_state_change_t;)J
.end method

.method public static final native vx_evt_account_login_state_change_t_base_set(JLcom/vivox/service/vx_evt_account_login_state_change_t;JLcom/vivox/service/vx_evt_base_t;)V
.end method

.method public static final native vx_evt_account_login_state_change_t_cookie_get(JLcom/vivox/service/vx_evt_account_login_state_change_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_account_login_state_change_t_cookie_set(JLcom/vivox/service/vx_evt_account_login_state_change_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_account_login_state_change_t_state_get(JLcom/vivox/service/vx_evt_account_login_state_change_t;)I
.end method

.method public static final native vx_evt_account_login_state_change_t_state_set(JLcom/vivox/service/vx_evt_account_login_state_change_t;I)V
.end method

.method public static final native vx_evt_account_login_state_change_t_status_code_get(JLcom/vivox/service/vx_evt_account_login_state_change_t;)I
.end method

.method public static final native vx_evt_account_login_state_change_t_status_code_set(JLcom/vivox/service/vx_evt_account_login_state_change_t;I)V
.end method

.method public static final native vx_evt_account_login_state_change_t_status_string_get(JLcom/vivox/service/vx_evt_account_login_state_change_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_account_login_state_change_t_status_string_set(JLcom/vivox/service/vx_evt_account_login_state_change_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_account_login_state_change_t_vcookie_get(JLcom/vivox/service/vx_evt_account_login_state_change_t;)J
.end method

.method public static final native vx_evt_account_login_state_change_t_vcookie_set(JLcom/vivox/service/vx_evt_account_login_state_change_t;J)V
.end method

.method public static final native vx_evt_aux_audio_properties_t_base_get(JLcom/vivox/service/vx_evt_aux_audio_properties_t;)J
.end method

.method public static final native vx_evt_aux_audio_properties_t_base_set(JLcom/vivox/service/vx_evt_aux_audio_properties_t;JLcom/vivox/service/vx_evt_base_t;)V
.end method

.method public static final native vx_evt_aux_audio_properties_t_mic_energy_get(JLcom/vivox/service/vx_evt_aux_audio_properties_t;)D
.end method

.method public static final native vx_evt_aux_audio_properties_t_mic_energy_set(JLcom/vivox/service/vx_evt_aux_audio_properties_t;D)V
.end method

.method public static final native vx_evt_aux_audio_properties_t_mic_is_active_get(JLcom/vivox/service/vx_evt_aux_audio_properties_t;)I
.end method

.method public static final native vx_evt_aux_audio_properties_t_mic_is_active_set(JLcom/vivox/service/vx_evt_aux_audio_properties_t;I)V
.end method

.method public static final native vx_evt_aux_audio_properties_t_mic_volume_get(JLcom/vivox/service/vx_evt_aux_audio_properties_t;)I
.end method

.method public static final native vx_evt_aux_audio_properties_t_mic_volume_set(JLcom/vivox/service/vx_evt_aux_audio_properties_t;I)V
.end method

.method public static final native vx_evt_aux_audio_properties_t_speaker_energy_get(JLcom/vivox/service/vx_evt_aux_audio_properties_t;)D
.end method

.method public static final native vx_evt_aux_audio_properties_t_speaker_energy_set(JLcom/vivox/service/vx_evt_aux_audio_properties_t;D)V
.end method

.method public static final native vx_evt_aux_audio_properties_t_speaker_is_active_get(JLcom/vivox/service/vx_evt_aux_audio_properties_t;)I
.end method

.method public static final native vx_evt_aux_audio_properties_t_speaker_is_active_set(JLcom/vivox/service/vx_evt_aux_audio_properties_t;I)V
.end method

.method public static final native vx_evt_aux_audio_properties_t_speaker_volume_get(JLcom/vivox/service/vx_evt_aux_audio_properties_t;)I
.end method

.method public static final native vx_evt_aux_audio_properties_t_speaker_volume_set(JLcom/vivox/service/vx_evt_aux_audio_properties_t;I)V
.end method

.method public static final native vx_evt_aux_audio_properties_t_state_get(JLcom/vivox/service/vx_evt_aux_audio_properties_t;)I
.end method

.method public static final native vx_evt_aux_audio_properties_t_state_set(JLcom/vivox/service/vx_evt_aux_audio_properties_t;I)V
.end method

.method public static final native vx_evt_base_t_extended_status_info_get(JLcom/vivox/service/vx_evt_base_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_base_t_extended_status_info_set(JLcom/vivox/service/vx_evt_base_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_base_t_message_get(JLcom/vivox/service/vx_evt_base_t;)J
.end method

.method public static final native vx_evt_base_t_message_set(JLcom/vivox/service/vx_evt_base_t;JLcom/vivox/service/vx_message_base_t;)V
.end method

.method public static final native vx_evt_base_t_type_get(JLcom/vivox/service/vx_evt_base_t;)I
.end method

.method public static final native vx_evt_base_t_type_set(JLcom/vivox/service/vx_evt_base_t;I)V
.end method

.method public static final native vx_evt_buddy_and_group_list_changed_t_account_handle_get(JLcom/vivox/service/vx_evt_buddy_and_group_list_changed_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_buddy_and_group_list_changed_t_account_handle_set(JLcom/vivox/service/vx_evt_buddy_and_group_list_changed_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_buddy_and_group_list_changed_t_base_get(JLcom/vivox/service/vx_evt_buddy_and_group_list_changed_t;)J
.end method

.method public static final native vx_evt_buddy_and_group_list_changed_t_base_set(JLcom/vivox/service/vx_evt_buddy_and_group_list_changed_t;JLcom/vivox/service/vx_evt_base_t;)V
.end method

.method public static final native vx_evt_buddy_and_group_list_changed_t_buddies_get(JLcom/vivox/service/vx_evt_buddy_and_group_list_changed_t;)J
.end method

.method public static final native vx_evt_buddy_and_group_list_changed_t_buddies_set(JLcom/vivox/service/vx_evt_buddy_and_group_list_changed_t;J)V
.end method

.method public static final native vx_evt_buddy_and_group_list_changed_t_buddy_count_get(JLcom/vivox/service/vx_evt_buddy_and_group_list_changed_t;)I
.end method

.method public static final native vx_evt_buddy_and_group_list_changed_t_buddy_count_set(JLcom/vivox/service/vx_evt_buddy_and_group_list_changed_t;I)V
.end method

.method public static final native vx_evt_buddy_and_group_list_changed_t_get_buddies_item(JLcom/vivox/service/vx_evt_buddy_and_group_list_changed_t;I)J
.end method

.method public static final native vx_evt_buddy_and_group_list_changed_t_get_groups_item(JLcom/vivox/service/vx_evt_buddy_and_group_list_changed_t;I)J
.end method

.method public static final native vx_evt_buddy_and_group_list_changed_t_group_count_get(JLcom/vivox/service/vx_evt_buddy_and_group_list_changed_t;)I
.end method

.method public static final native vx_evt_buddy_and_group_list_changed_t_group_count_set(JLcom/vivox/service/vx_evt_buddy_and_group_list_changed_t;I)V
.end method

.method public static final native vx_evt_buddy_and_group_list_changed_t_groups_get(JLcom/vivox/service/vx_evt_buddy_and_group_list_changed_t;)J
.end method

.method public static final native vx_evt_buddy_and_group_list_changed_t_groups_set(JLcom/vivox/service/vx_evt_buddy_and_group_list_changed_t;J)V
.end method

.method public static final native vx_evt_buddy_changed_t_account_handle_get(JLcom/vivox/service/vx_evt_buddy_changed_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_buddy_changed_t_account_handle_set(JLcom/vivox/service/vx_evt_buddy_changed_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_buddy_changed_t_account_id_get(JLcom/vivox/service/vx_evt_buddy_changed_t;)I
.end method

.method public static final native vx_evt_buddy_changed_t_account_id_set(JLcom/vivox/service/vx_evt_buddy_changed_t;I)V
.end method

.method public static final native vx_evt_buddy_changed_t_base_get(JLcom/vivox/service/vx_evt_buddy_changed_t;)J
.end method

.method public static final native vx_evt_buddy_changed_t_base_set(JLcom/vivox/service/vx_evt_buddy_changed_t;JLcom/vivox/service/vx_evt_base_t;)V
.end method

.method public static final native vx_evt_buddy_changed_t_buddy_data_get(JLcom/vivox/service/vx_evt_buddy_changed_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_buddy_changed_t_buddy_data_set(JLcom/vivox/service/vx_evt_buddy_changed_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_buddy_changed_t_buddy_uri_get(JLcom/vivox/service/vx_evt_buddy_changed_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_buddy_changed_t_buddy_uri_set(JLcom/vivox/service/vx_evt_buddy_changed_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_buddy_changed_t_change_type_get(JLcom/vivox/service/vx_evt_buddy_changed_t;)I
.end method

.method public static final native vx_evt_buddy_changed_t_change_type_set(JLcom/vivox/service/vx_evt_buddy_changed_t;I)V
.end method

.method public static final native vx_evt_buddy_changed_t_display_name_get(JLcom/vivox/service/vx_evt_buddy_changed_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_buddy_changed_t_display_name_set(JLcom/vivox/service/vx_evt_buddy_changed_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_buddy_changed_t_group_id_get(JLcom/vivox/service/vx_evt_buddy_changed_t;)I
.end method

.method public static final native vx_evt_buddy_changed_t_group_id_set(JLcom/vivox/service/vx_evt_buddy_changed_t;I)V
.end method

.method public static final native vx_evt_buddy_group_changed_t_account_handle_get(JLcom/vivox/service/vx_evt_buddy_group_changed_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_buddy_group_changed_t_account_handle_set(JLcom/vivox/service/vx_evt_buddy_group_changed_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_buddy_group_changed_t_base_get(JLcom/vivox/service/vx_evt_buddy_group_changed_t;)J
.end method

.method public static final native vx_evt_buddy_group_changed_t_base_set(JLcom/vivox/service/vx_evt_buddy_group_changed_t;JLcom/vivox/service/vx_evt_base_t;)V
.end method

.method public static final native vx_evt_buddy_group_changed_t_change_type_get(JLcom/vivox/service/vx_evt_buddy_group_changed_t;)I
.end method

.method public static final native vx_evt_buddy_group_changed_t_change_type_set(JLcom/vivox/service/vx_evt_buddy_group_changed_t;I)V
.end method

.method public static final native vx_evt_buddy_group_changed_t_group_data_get(JLcom/vivox/service/vx_evt_buddy_group_changed_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_buddy_group_changed_t_group_data_set(JLcom/vivox/service/vx_evt_buddy_group_changed_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_buddy_group_changed_t_group_id_get(JLcom/vivox/service/vx_evt_buddy_group_changed_t;)I
.end method

.method public static final native vx_evt_buddy_group_changed_t_group_id_set(JLcom/vivox/service/vx_evt_buddy_group_changed_t;I)V
.end method

.method public static final native vx_evt_buddy_group_changed_t_group_name_get(JLcom/vivox/service/vx_evt_buddy_group_changed_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_buddy_group_changed_t_group_name_set(JLcom/vivox/service/vx_evt_buddy_group_changed_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_buddy_presence_t_account_handle_get(JLcom/vivox/service/vx_evt_buddy_presence_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_buddy_presence_t_account_handle_set(JLcom/vivox/service/vx_evt_buddy_presence_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_buddy_presence_t_application_get(JLcom/vivox/service/vx_evt_buddy_presence_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_buddy_presence_t_application_set(JLcom/vivox/service/vx_evt_buddy_presence_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_buddy_presence_t_base_get(JLcom/vivox/service/vx_evt_buddy_presence_t;)J
.end method

.method public static final native vx_evt_buddy_presence_t_base_set(JLcom/vivox/service/vx_evt_buddy_presence_t;JLcom/vivox/service/vx_evt_base_t;)V
.end method

.method public static final native vx_evt_buddy_presence_t_buddy_uri_get(JLcom/vivox/service/vx_evt_buddy_presence_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_buddy_presence_t_buddy_uri_set(JLcom/vivox/service/vx_evt_buddy_presence_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_buddy_presence_t_contact_get(JLcom/vivox/service/vx_evt_buddy_presence_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_buddy_presence_t_contact_set(JLcom/vivox/service/vx_evt_buddy_presence_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_buddy_presence_t_custom_message_get(JLcom/vivox/service/vx_evt_buddy_presence_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_buddy_presence_t_custom_message_set(JLcom/vivox/service/vx_evt_buddy_presence_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_buddy_presence_t_displayname_get(JLcom/vivox/service/vx_evt_buddy_presence_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_buddy_presence_t_displayname_set(JLcom/vivox/service/vx_evt_buddy_presence_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_buddy_presence_t_id_get(JLcom/vivox/service/vx_evt_buddy_presence_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_buddy_presence_t_id_set(JLcom/vivox/service/vx_evt_buddy_presence_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_buddy_presence_t_presence_get(JLcom/vivox/service/vx_evt_buddy_presence_t;)I
.end method

.method public static final native vx_evt_buddy_presence_t_presence_set(JLcom/vivox/service/vx_evt_buddy_presence_t;I)V
.end method

.method public static final native vx_evt_buddy_presence_t_priority_get(JLcom/vivox/service/vx_evt_buddy_presence_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_buddy_presence_t_priority_set(JLcom/vivox/service/vx_evt_buddy_presence_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_buddy_presence_t_state_get(JLcom/vivox/service/vx_evt_buddy_presence_t;)I
.end method

.method public static final native vx_evt_buddy_presence_t_state_set(JLcom/vivox/service/vx_evt_buddy_presence_t;I)V
.end method

.method public static final native vx_evt_idle_state_changed_t_base_get(JLcom/vivox/service/vx_evt_idle_state_changed_t;)J
.end method

.method public static final native vx_evt_idle_state_changed_t_base_set(JLcom/vivox/service/vx_evt_idle_state_changed_t;JLcom/vivox/service/vx_evt_base_t;)V
.end method

.method public static final native vx_evt_idle_state_changed_t_is_idle_get(JLcom/vivox/service/vx_evt_idle_state_changed_t;)I
.end method

.method public static final native vx_evt_idle_state_changed_t_is_idle_set(JLcom/vivox/service/vx_evt_idle_state_changed_t;I)V
.end method

.method public static final native vx_evt_keyboard_mouse_t_base_get(JLcom/vivox/service/vx_evt_keyboard_mouse_t;)J
.end method

.method public static final native vx_evt_keyboard_mouse_t_base_set(JLcom/vivox/service/vx_evt_keyboard_mouse_t;JLcom/vivox/service/vx_evt_base_t;)V
.end method

.method public static final native vx_evt_keyboard_mouse_t_is_down_get(JLcom/vivox/service/vx_evt_keyboard_mouse_t;)I
.end method

.method public static final native vx_evt_keyboard_mouse_t_is_down_set(JLcom/vivox/service/vx_evt_keyboard_mouse_t;I)V
.end method

.method public static final native vx_evt_keyboard_mouse_t_name_get(JLcom/vivox/service/vx_evt_keyboard_mouse_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_keyboard_mouse_t_name_set(JLcom/vivox/service/vx_evt_keyboard_mouse_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_media_completion_t_base_get(JLcom/vivox/service/vx_evt_media_completion_t;)J
.end method

.method public static final native vx_evt_media_completion_t_base_set(JLcom/vivox/service/vx_evt_media_completion_t;JLcom/vivox/service/vx_evt_base_t;)V
.end method

.method public static final native vx_evt_media_completion_t_completion_type_get(JLcom/vivox/service/vx_evt_media_completion_t;)I
.end method

.method public static final native vx_evt_media_completion_t_completion_type_set(JLcom/vivox/service/vx_evt_media_completion_t;I)V
.end method

.method public static final native vx_evt_media_completion_t_sessiongroup_handle_get(JLcom/vivox/service/vx_evt_media_completion_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_media_completion_t_sessiongroup_handle_set(JLcom/vivox/service/vx_evt_media_completion_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_media_stream_updated_t_base_get(JLcom/vivox/service/vx_evt_media_stream_updated_t;)J
.end method

.method public static final native vx_evt_media_stream_updated_t_base_set(JLcom/vivox/service/vx_evt_media_stream_updated_t;JLcom/vivox/service/vx_evt_base_t;)V
.end method

.method public static final native vx_evt_media_stream_updated_t_durable_media_id_get(JLcom/vivox/service/vx_evt_media_stream_updated_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_media_stream_updated_t_durable_media_id_set(JLcom/vivox/service/vx_evt_media_stream_updated_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_media_stream_updated_t_incoming_get(JLcom/vivox/service/vx_evt_media_stream_updated_t;)I
.end method

.method public static final native vx_evt_media_stream_updated_t_incoming_set(JLcom/vivox/service/vx_evt_media_stream_updated_t;I)V
.end method

.method public static final native vx_evt_media_stream_updated_t_media_probe_server_get(JLcom/vivox/service/vx_evt_media_stream_updated_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_media_stream_updated_t_media_probe_server_set(JLcom/vivox/service/vx_evt_media_stream_updated_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_media_stream_updated_t_session_handle_get(JLcom/vivox/service/vx_evt_media_stream_updated_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_media_stream_updated_t_session_handle_set(JLcom/vivox/service/vx_evt_media_stream_updated_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_media_stream_updated_t_sessiongroup_handle_get(JLcom/vivox/service/vx_evt_media_stream_updated_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_media_stream_updated_t_sessiongroup_handle_set(JLcom/vivox/service/vx_evt_media_stream_updated_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_media_stream_updated_t_state_get(JLcom/vivox/service/vx_evt_media_stream_updated_t;)I
.end method

.method public static final native vx_evt_media_stream_updated_t_state_set(JLcom/vivox/service/vx_evt_media_stream_updated_t;I)V
.end method

.method public static final native vx_evt_media_stream_updated_t_status_code_get(JLcom/vivox/service/vx_evt_media_stream_updated_t;)I
.end method

.method public static final native vx_evt_media_stream_updated_t_status_code_set(JLcom/vivox/service/vx_evt_media_stream_updated_t;I)V
.end method

.method public static final native vx_evt_media_stream_updated_t_status_string_get(JLcom/vivox/service/vx_evt_media_stream_updated_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_media_stream_updated_t_status_string_set(JLcom/vivox/service/vx_evt_media_stream_updated_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_message_t_alias_username_get(JLcom/vivox/service/vx_evt_message_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_message_t_alias_username_set(JLcom/vivox/service/vx_evt_message_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_message_t_application_get(JLcom/vivox/service/vx_evt_message_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_message_t_application_set(JLcom/vivox/service/vx_evt_message_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_message_t_base_get(JLcom/vivox/service/vx_evt_message_t;)J
.end method

.method public static final native vx_evt_message_t_base_set(JLcom/vivox/service/vx_evt_message_t;JLcom/vivox/service/vx_evt_base_t;)V
.end method

.method public static final native vx_evt_message_t_message_body_get(JLcom/vivox/service/vx_evt_message_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_message_t_message_body_set(JLcom/vivox/service/vx_evt_message_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_message_t_message_header_get(JLcom/vivox/service/vx_evt_message_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_message_t_message_header_set(JLcom/vivox/service/vx_evt_message_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_message_t_participant_displayname_get(JLcom/vivox/service/vx_evt_message_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_message_t_participant_displayname_set(JLcom/vivox/service/vx_evt_message_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_message_t_participant_uri_get(JLcom/vivox/service/vx_evt_message_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_message_t_participant_uri_set(JLcom/vivox/service/vx_evt_message_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_message_t_session_handle_get(JLcom/vivox/service/vx_evt_message_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_message_t_session_handle_set(JLcom/vivox/service/vx_evt_message_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_message_t_sessiongroup_handle_get(JLcom/vivox/service/vx_evt_message_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_message_t_sessiongroup_handle_set(JLcom/vivox/service/vx_evt_message_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_message_t_state_get(JLcom/vivox/service/vx_evt_message_t;)I
.end method

.method public static final native vx_evt_message_t_state_set(JLcom/vivox/service/vx_evt_message_t;I)V
.end method

.method public static final native vx_evt_network_message_t_account_handle_get(JLcom/vivox/service/vx_evt_network_message_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_network_message_t_account_handle_set(JLcom/vivox/service/vx_evt_network_message_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_network_message_t_base_get(JLcom/vivox/service/vx_evt_network_message_t;)J
.end method

.method public static final native vx_evt_network_message_t_base_set(JLcom/vivox/service/vx_evt_network_message_t;JLcom/vivox/service/vx_evt_base_t;)V
.end method

.method public static final native vx_evt_network_message_t_content_get(JLcom/vivox/service/vx_evt_network_message_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_network_message_t_content_set(JLcom/vivox/service/vx_evt_network_message_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_network_message_t_content_type_get(JLcom/vivox/service/vx_evt_network_message_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_network_message_t_content_type_set(JLcom/vivox/service/vx_evt_network_message_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_network_message_t_network_message_type_get(JLcom/vivox/service/vx_evt_network_message_t;)I
.end method

.method public static final native vx_evt_network_message_t_network_message_type_set(JLcom/vivox/service/vx_evt_network_message_t;I)V
.end method

.method public static final native vx_evt_network_message_t_receiver_alias_username_get(JLcom/vivox/service/vx_evt_network_message_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_network_message_t_receiver_alias_username_set(JLcom/vivox/service/vx_evt_network_message_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_network_message_t_sender_alias_username_get(JLcom/vivox/service/vx_evt_network_message_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_network_message_t_sender_alias_username_set(JLcom/vivox/service/vx_evt_network_message_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_network_message_t_sender_display_name_get(JLcom/vivox/service/vx_evt_network_message_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_network_message_t_sender_display_name_set(JLcom/vivox/service/vx_evt_network_message_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_network_message_t_sender_uri_get(JLcom/vivox/service/vx_evt_network_message_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_network_message_t_sender_uri_set(JLcom/vivox/service/vx_evt_network_message_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_network_message_type_admin_message_get()I
.end method

.method public static final native vx_evt_network_message_type_offline_message_get()I
.end method

.method public static final native vx_evt_network_message_type_sessionless_message_get()I
.end method

.method public static final native vx_evt_participant_added_t_account_name_get(JLcom/vivox/service/vx_evt_participant_added_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_participant_added_t_account_name_set(JLcom/vivox/service/vx_evt_participant_added_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_participant_added_t_alias_username_get(JLcom/vivox/service/vx_evt_participant_added_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_participant_added_t_alias_username_set(JLcom/vivox/service/vx_evt_participant_added_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_participant_added_t_application_get(JLcom/vivox/service/vx_evt_participant_added_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_participant_added_t_application_set(JLcom/vivox/service/vx_evt_participant_added_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_participant_added_t_base_get(JLcom/vivox/service/vx_evt_participant_added_t;)J
.end method

.method public static final native vx_evt_participant_added_t_base_set(JLcom/vivox/service/vx_evt_participant_added_t;JLcom/vivox/service/vx_evt_base_t;)V
.end method

.method public static final native vx_evt_participant_added_t_display_name_get(JLcom/vivox/service/vx_evt_participant_added_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_participant_added_t_display_name_set(JLcom/vivox/service/vx_evt_participant_added_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_participant_added_t_displayname_get(JLcom/vivox/service/vx_evt_participant_added_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_participant_added_t_displayname_set(JLcom/vivox/service/vx_evt_participant_added_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_participant_added_t_is_anonymous_login_get(JLcom/vivox/service/vx_evt_participant_added_t;)I
.end method

.method public static final native vx_evt_participant_added_t_is_anonymous_login_set(JLcom/vivox/service/vx_evt_participant_added_t;I)V
.end method

.method public static final native vx_evt_participant_added_t_participant_type_get(JLcom/vivox/service/vx_evt_participant_added_t;)I
.end method

.method public static final native vx_evt_participant_added_t_participant_type_set(JLcom/vivox/service/vx_evt_participant_added_t;I)V
.end method

.method public static final native vx_evt_participant_added_t_participant_uri_get(JLcom/vivox/service/vx_evt_participant_added_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_participant_added_t_participant_uri_set(JLcom/vivox/service/vx_evt_participant_added_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_participant_added_t_session_handle_get(JLcom/vivox/service/vx_evt_participant_added_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_participant_added_t_session_handle_set(JLcom/vivox/service/vx_evt_participant_added_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_participant_added_t_sessiongroup_handle_get(JLcom/vivox/service/vx_evt_participant_added_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_participant_added_t_sessiongroup_handle_set(JLcom/vivox/service/vx_evt_participant_added_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_participant_removed_t_account_name_get(JLcom/vivox/service/vx_evt_participant_removed_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_participant_removed_t_account_name_set(JLcom/vivox/service/vx_evt_participant_removed_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_participant_removed_t_alias_username_get(JLcom/vivox/service/vx_evt_participant_removed_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_participant_removed_t_alias_username_set(JLcom/vivox/service/vx_evt_participant_removed_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_participant_removed_t_base_get(JLcom/vivox/service/vx_evt_participant_removed_t;)J
.end method

.method public static final native vx_evt_participant_removed_t_base_set(JLcom/vivox/service/vx_evt_participant_removed_t;JLcom/vivox/service/vx_evt_base_t;)V
.end method

.method public static final native vx_evt_participant_removed_t_participant_uri_get(JLcom/vivox/service/vx_evt_participant_removed_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_participant_removed_t_participant_uri_set(JLcom/vivox/service/vx_evt_participant_removed_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_participant_removed_t_reason_get(JLcom/vivox/service/vx_evt_participant_removed_t;)I
.end method

.method public static final native vx_evt_participant_removed_t_reason_set(JLcom/vivox/service/vx_evt_participant_removed_t;I)V
.end method

.method public static final native vx_evt_participant_removed_t_session_handle_get(JLcom/vivox/service/vx_evt_participant_removed_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_participant_removed_t_session_handle_set(JLcom/vivox/service/vx_evt_participant_removed_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_participant_removed_t_sessiongroup_handle_get(JLcom/vivox/service/vx_evt_participant_removed_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_participant_removed_t_sessiongroup_handle_set(JLcom/vivox/service/vx_evt_participant_removed_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_participant_updated_t_active_media_get(JLcom/vivox/service/vx_evt_participant_updated_t;)I
.end method

.method public static final native vx_evt_participant_updated_t_active_media_set(JLcom/vivox/service/vx_evt_participant_updated_t;I)V
.end method

.method public static final native vx_evt_participant_updated_t_alias_username_get(JLcom/vivox/service/vx_evt_participant_updated_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_participant_updated_t_alias_username_set(JLcom/vivox/service/vx_evt_participant_updated_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_participant_updated_t_base_get(JLcom/vivox/service/vx_evt_participant_updated_t;)J
.end method

.method public static final native vx_evt_participant_updated_t_base_set(JLcom/vivox/service/vx_evt_participant_updated_t;JLcom/vivox/service/vx_evt_base_t;)V
.end method

.method public static final native vx_evt_participant_updated_t_diagnostic_state_count_get(JLcom/vivox/service/vx_evt_participant_updated_t;)I
.end method

.method public static final native vx_evt_participant_updated_t_diagnostic_state_count_set(JLcom/vivox/service/vx_evt_participant_updated_t;I)V
.end method

.method public static final native vx_evt_participant_updated_t_diagnostic_states_get(JLcom/vivox/service/vx_evt_participant_updated_t;)J
.end method

.method public static final native vx_evt_participant_updated_t_diagnostic_states_set(JLcom/vivox/service/vx_evt_participant_updated_t;J)V
.end method

.method public static final native vx_evt_participant_updated_t_energy_get(JLcom/vivox/service/vx_evt_participant_updated_t;)D
.end method

.method public static final native vx_evt_participant_updated_t_energy_set(JLcom/vivox/service/vx_evt_participant_updated_t;D)V
.end method

.method public static final native vx_evt_participant_updated_t_get_diagnostic_states_item(JLcom/vivox/service/vx_evt_participant_updated_t;I)I
.end method

.method public static final native vx_evt_participant_updated_t_is_moderator_muted_get(JLcom/vivox/service/vx_evt_participant_updated_t;)I
.end method

.method public static final native vx_evt_participant_updated_t_is_moderator_muted_set(JLcom/vivox/service/vx_evt_participant_updated_t;I)V
.end method

.method public static final native vx_evt_participant_updated_t_is_moderator_text_muted_get(JLcom/vivox/service/vx_evt_participant_updated_t;)I
.end method

.method public static final native vx_evt_participant_updated_t_is_moderator_text_muted_set(JLcom/vivox/service/vx_evt_participant_updated_t;I)V
.end method

.method public static final native vx_evt_participant_updated_t_is_muted_for_me_get(JLcom/vivox/service/vx_evt_participant_updated_t;)I
.end method

.method public static final native vx_evt_participant_updated_t_is_muted_for_me_set(JLcom/vivox/service/vx_evt_participant_updated_t;I)V
.end method

.method public static final native vx_evt_participant_updated_t_is_speaking_get(JLcom/vivox/service/vx_evt_participant_updated_t;)I
.end method

.method public static final native vx_evt_participant_updated_t_is_speaking_set(JLcom/vivox/service/vx_evt_participant_updated_t;I)V
.end method

.method public static final native vx_evt_participant_updated_t_is_text_muted_for_me_get(JLcom/vivox/service/vx_evt_participant_updated_t;)I
.end method

.method public static final native vx_evt_participant_updated_t_is_text_muted_for_me_set(JLcom/vivox/service/vx_evt_participant_updated_t;I)V
.end method

.method public static final native vx_evt_participant_updated_t_participant_uri_get(JLcom/vivox/service/vx_evt_participant_updated_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_participant_updated_t_participant_uri_set(JLcom/vivox/service/vx_evt_participant_updated_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_participant_updated_t_session_handle_get(JLcom/vivox/service/vx_evt_participant_updated_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_participant_updated_t_session_handle_set(JLcom/vivox/service/vx_evt_participant_updated_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_participant_updated_t_sessiongroup_handle_get(JLcom/vivox/service/vx_evt_participant_updated_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_participant_updated_t_sessiongroup_handle_set(JLcom/vivox/service/vx_evt_participant_updated_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_participant_updated_t_type_get(JLcom/vivox/service/vx_evt_participant_updated_t;)I
.end method

.method public static final native vx_evt_participant_updated_t_type_set(JLcom/vivox/service/vx_evt_participant_updated_t;I)V
.end method

.method public static final native vx_evt_participant_updated_t_volume_get(JLcom/vivox/service/vx_evt_participant_updated_t;)I
.end method

.method public static final native vx_evt_participant_updated_t_volume_set(JLcom/vivox/service/vx_evt_participant_updated_t;I)V
.end method

.method public static final native vx_evt_server_app_data_t_account_handle_get(JLcom/vivox/service/vx_evt_server_app_data_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_server_app_data_t_account_handle_set(JLcom/vivox/service/vx_evt_server_app_data_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_server_app_data_t_base_get(JLcom/vivox/service/vx_evt_server_app_data_t;)J
.end method

.method public static final native vx_evt_server_app_data_t_base_set(JLcom/vivox/service/vx_evt_server_app_data_t;JLcom/vivox/service/vx_evt_base_t;)V
.end method

.method public static final native vx_evt_server_app_data_t_content_get(JLcom/vivox/service/vx_evt_server_app_data_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_server_app_data_t_content_set(JLcom/vivox/service/vx_evt_server_app_data_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_server_app_data_t_content_type_get(JLcom/vivox/service/vx_evt_server_app_data_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_server_app_data_t_content_type_set(JLcom/vivox/service/vx_evt_server_app_data_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_session_added_t_alias_username_get(JLcom/vivox/service/vx_evt_session_added_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_session_added_t_alias_username_set(JLcom/vivox/service/vx_evt_session_added_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_session_added_t_application_get(JLcom/vivox/service/vx_evt_session_added_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_session_added_t_application_set(JLcom/vivox/service/vx_evt_session_added_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_session_added_t_base_get(JLcom/vivox/service/vx_evt_session_added_t;)J
.end method

.method public static final native vx_evt_session_added_t_base_set(JLcom/vivox/service/vx_evt_session_added_t;JLcom/vivox/service/vx_evt_base_t;)V
.end method

.method public static final native vx_evt_session_added_t_channel_name_get(JLcom/vivox/service/vx_evt_session_added_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_session_added_t_channel_name_set(JLcom/vivox/service/vx_evt_session_added_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_session_added_t_displayname_get(JLcom/vivox/service/vx_evt_session_added_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_session_added_t_displayname_set(JLcom/vivox/service/vx_evt_session_added_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_session_added_t_incoming_get(JLcom/vivox/service/vx_evt_session_added_t;)I
.end method

.method public static final native vx_evt_session_added_t_incoming_set(JLcom/vivox/service/vx_evt_session_added_t;I)V
.end method

.method public static final native vx_evt_session_added_t_is_channel_get(JLcom/vivox/service/vx_evt_session_added_t;)I
.end method

.method public static final native vx_evt_session_added_t_is_channel_set(JLcom/vivox/service/vx_evt_session_added_t;I)V
.end method

.method public static final native vx_evt_session_added_t_session_handle_get(JLcom/vivox/service/vx_evt_session_added_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_session_added_t_session_handle_set(JLcom/vivox/service/vx_evt_session_added_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_session_added_t_sessiongroup_handle_get(JLcom/vivox/service/vx_evt_session_added_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_session_added_t_sessiongroup_handle_set(JLcom/vivox/service/vx_evt_session_added_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_session_added_t_uri_get(JLcom/vivox/service/vx_evt_session_added_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_session_added_t_uri_set(JLcom/vivox/service/vx_evt_session_added_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_session_notification_t_base_get(JLcom/vivox/service/vx_evt_session_notification_t;)J
.end method

.method public static final native vx_evt_session_notification_t_base_set(JLcom/vivox/service/vx_evt_session_notification_t;JLcom/vivox/service/vx_evt_base_t;)V
.end method

.method public static final native vx_evt_session_notification_t_notification_type_get(JLcom/vivox/service/vx_evt_session_notification_t;)I
.end method

.method public static final native vx_evt_session_notification_t_notification_type_set(JLcom/vivox/service/vx_evt_session_notification_t;I)V
.end method

.method public static final native vx_evt_session_notification_t_participant_uri_get(JLcom/vivox/service/vx_evt_session_notification_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_session_notification_t_participant_uri_set(JLcom/vivox/service/vx_evt_session_notification_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_session_notification_t_session_handle_get(JLcom/vivox/service/vx_evt_session_notification_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_session_notification_t_session_handle_set(JLcom/vivox/service/vx_evt_session_notification_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_session_notification_t_state_get(JLcom/vivox/service/vx_evt_session_notification_t;)I
.end method

.method public static final native vx_evt_session_notification_t_state_set(JLcom/vivox/service/vx_evt_session_notification_t;I)V
.end method

.method public static final native vx_evt_session_removed_t_base_get(JLcom/vivox/service/vx_evt_session_removed_t;)J
.end method

.method public static final native vx_evt_session_removed_t_base_set(JLcom/vivox/service/vx_evt_session_removed_t;JLcom/vivox/service/vx_evt_base_t;)V
.end method

.method public static final native vx_evt_session_removed_t_session_handle_get(JLcom/vivox/service/vx_evt_session_removed_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_session_removed_t_session_handle_set(JLcom/vivox/service/vx_evt_session_removed_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_session_removed_t_sessiongroup_handle_get(JLcom/vivox/service/vx_evt_session_removed_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_session_removed_t_sessiongroup_handle_set(JLcom/vivox/service/vx_evt_session_removed_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_session_removed_t_uri_get(JLcom/vivox/service/vx_evt_session_removed_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_session_removed_t_uri_set(JLcom/vivox/service/vx_evt_session_removed_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_session_updated_t_base_get(JLcom/vivox/service/vx_evt_session_updated_t;)J
.end method

.method public static final native vx_evt_session_updated_t_base_set(JLcom/vivox/service/vx_evt_session_updated_t;JLcom/vivox/service/vx_evt_base_t;)V
.end method

.method public static final native vx_evt_session_updated_t_get_speaker_position_item(JLcom/vivox/service/vx_evt_session_updated_t;I)D
.end method

.method public static final native vx_evt_session_updated_t_is_ad_playing_get(JLcom/vivox/service/vx_evt_session_updated_t;)I
.end method

.method public static final native vx_evt_session_updated_t_is_ad_playing_set(JLcom/vivox/service/vx_evt_session_updated_t;I)V
.end method

.method public static final native vx_evt_session_updated_t_is_focused_get(JLcom/vivox/service/vx_evt_session_updated_t;)I
.end method

.method public static final native vx_evt_session_updated_t_is_focused_set(JLcom/vivox/service/vx_evt_session_updated_t;I)V
.end method

.method public static final native vx_evt_session_updated_t_is_muted_get(JLcom/vivox/service/vx_evt_session_updated_t;)I
.end method

.method public static final native vx_evt_session_updated_t_is_muted_set(JLcom/vivox/service/vx_evt_session_updated_t;I)V
.end method

.method public static final native vx_evt_session_updated_t_is_text_muted_get(JLcom/vivox/service/vx_evt_session_updated_t;)I
.end method

.method public static final native vx_evt_session_updated_t_is_text_muted_set(JLcom/vivox/service/vx_evt_session_updated_t;I)V
.end method

.method public static final native vx_evt_session_updated_t_session_font_id_get(JLcom/vivox/service/vx_evt_session_updated_t;)I
.end method

.method public static final native vx_evt_session_updated_t_session_font_id_set(JLcom/vivox/service/vx_evt_session_updated_t;I)V
.end method

.method public static final native vx_evt_session_updated_t_session_handle_get(JLcom/vivox/service/vx_evt_session_updated_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_session_updated_t_session_handle_set(JLcom/vivox/service/vx_evt_session_updated_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_session_updated_t_sessiongroup_handle_get(JLcom/vivox/service/vx_evt_session_updated_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_session_updated_t_sessiongroup_handle_set(JLcom/vivox/service/vx_evt_session_updated_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_session_updated_t_speaker_position_get(JLcom/vivox/service/vx_evt_session_updated_t;)J
.end method

.method public static final native vx_evt_session_updated_t_speaker_position_set(JLcom/vivox/service/vx_evt_session_updated_t;J)V
.end method

.method public static final native vx_evt_session_updated_t_transmit_enabled_get(JLcom/vivox/service/vx_evt_session_updated_t;)I
.end method

.method public static final native vx_evt_session_updated_t_transmit_enabled_set(JLcom/vivox/service/vx_evt_session_updated_t;I)V
.end method

.method public static final native vx_evt_session_updated_t_uri_get(JLcom/vivox/service/vx_evt_session_updated_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_session_updated_t_uri_set(JLcom/vivox/service/vx_evt_session_updated_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_session_updated_t_volume_get(JLcom/vivox/service/vx_evt_session_updated_t;)I
.end method

.method public static final native vx_evt_session_updated_t_volume_set(JLcom/vivox/service/vx_evt_session_updated_t;I)V
.end method

.method public static final native vx_evt_sessiongroup_added_t_account_handle_get(JLcom/vivox/service/vx_evt_sessiongroup_added_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_sessiongroup_added_t_account_handle_set(JLcom/vivox/service/vx_evt_sessiongroup_added_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_sessiongroup_added_t_alias_username_get(JLcom/vivox/service/vx_evt_sessiongroup_added_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_sessiongroup_added_t_alias_username_set(JLcom/vivox/service/vx_evt_sessiongroup_added_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_sessiongroup_added_t_base_get(JLcom/vivox/service/vx_evt_sessiongroup_added_t;)J
.end method

.method public static final native vx_evt_sessiongroup_added_t_base_set(JLcom/vivox/service/vx_evt_sessiongroup_added_t;JLcom/vivox/service/vx_evt_base_t;)V
.end method

.method public static final native vx_evt_sessiongroup_added_t_sessiongroup_handle_get(JLcom/vivox/service/vx_evt_sessiongroup_added_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_sessiongroup_added_t_sessiongroup_handle_set(JLcom/vivox/service/vx_evt_sessiongroup_added_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_sessiongroup_added_t_type_get(JLcom/vivox/service/vx_evt_sessiongroup_added_t;)I
.end method

.method public static final native vx_evt_sessiongroup_added_t_type_set(JLcom/vivox/service/vx_evt_sessiongroup_added_t;I)V
.end method

.method public static final native vx_evt_sessiongroup_playback_frame_played_t_base_get(JLcom/vivox/service/vx_evt_sessiongroup_playback_frame_played_t;)J
.end method

.method public static final native vx_evt_sessiongroup_playback_frame_played_t_base_set(JLcom/vivox/service/vx_evt_sessiongroup_playback_frame_played_t;JLcom/vivox/service/vx_evt_base_t;)V
.end method

.method public static final native vx_evt_sessiongroup_playback_frame_played_t_current_frame_get(JLcom/vivox/service/vx_evt_sessiongroup_playback_frame_played_t;)I
.end method

.method public static final native vx_evt_sessiongroup_playback_frame_played_t_current_frame_set(JLcom/vivox/service/vx_evt_sessiongroup_playback_frame_played_t;I)V
.end method

.method public static final native vx_evt_sessiongroup_playback_frame_played_t_first_frame_get(JLcom/vivox/service/vx_evt_sessiongroup_playback_frame_played_t;)I
.end method

.method public static final native vx_evt_sessiongroup_playback_frame_played_t_first_frame_set(JLcom/vivox/service/vx_evt_sessiongroup_playback_frame_played_t;I)V
.end method

.method public static final native vx_evt_sessiongroup_playback_frame_played_t_sessiongroup_handle_get(JLcom/vivox/service/vx_evt_sessiongroup_playback_frame_played_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_sessiongroup_playback_frame_played_t_sessiongroup_handle_set(JLcom/vivox/service/vx_evt_sessiongroup_playback_frame_played_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_sessiongroup_playback_frame_played_t_total_frames_get(JLcom/vivox/service/vx_evt_sessiongroup_playback_frame_played_t;)I
.end method

.method public static final native vx_evt_sessiongroup_playback_frame_played_t_total_frames_set(JLcom/vivox/service/vx_evt_sessiongroup_playback_frame_played_t;I)V
.end method

.method public static final native vx_evt_sessiongroup_removed_t_base_get(JLcom/vivox/service/vx_evt_sessiongroup_removed_t;)J
.end method

.method public static final native vx_evt_sessiongroup_removed_t_base_set(JLcom/vivox/service/vx_evt_sessiongroup_removed_t;JLcom/vivox/service/vx_evt_base_t;)V
.end method

.method public static final native vx_evt_sessiongroup_removed_t_sessiongroup_handle_get(JLcom/vivox/service/vx_evt_sessiongroup_removed_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_sessiongroup_removed_t_sessiongroup_handle_set(JLcom/vivox/service/vx_evt_sessiongroup_removed_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_sessiongroup_updated_t_base_get(JLcom/vivox/service/vx_evt_sessiongroup_updated_t;)J
.end method

.method public static final native vx_evt_sessiongroup_updated_t_base_set(JLcom/vivox/service/vx_evt_sessiongroup_updated_t;JLcom/vivox/service/vx_evt_base_t;)V
.end method

.method public static final native vx_evt_sessiongroup_updated_t_current_playback_mode_get(JLcom/vivox/service/vx_evt_sessiongroup_updated_t;)I
.end method

.method public static final native vx_evt_sessiongroup_updated_t_current_playback_mode_set(JLcom/vivox/service/vx_evt_sessiongroup_updated_t;I)V
.end method

.method public static final native vx_evt_sessiongroup_updated_t_current_playback_speed_get(JLcom/vivox/service/vx_evt_sessiongroup_updated_t;)D
.end method

.method public static final native vx_evt_sessiongroup_updated_t_current_playback_speed_set(JLcom/vivox/service/vx_evt_sessiongroup_updated_t;D)V
.end method

.method public static final native vx_evt_sessiongroup_updated_t_current_recording_filename_get(JLcom/vivox/service/vx_evt_sessiongroup_updated_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_sessiongroup_updated_t_current_recording_filename_set(JLcom/vivox/service/vx_evt_sessiongroup_updated_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_sessiongroup_updated_t_first_frame_timestamp_us_get(JLcom/vivox/service/vx_evt_sessiongroup_updated_t;)J
.end method

.method public static final native vx_evt_sessiongroup_updated_t_first_frame_timestamp_us_set(JLcom/vivox/service/vx_evt_sessiongroup_updated_t;J)V
.end method

.method public static final native vx_evt_sessiongroup_updated_t_first_loop_frame_get(JLcom/vivox/service/vx_evt_sessiongroup_updated_t;)I
.end method

.method public static final native vx_evt_sessiongroup_updated_t_first_loop_frame_set(JLcom/vivox/service/vx_evt_sessiongroup_updated_t;I)V
.end method

.method public static final native vx_evt_sessiongroup_updated_t_in_delayed_playback_get(JLcom/vivox/service/vx_evt_sessiongroup_updated_t;)I
.end method

.method public static final native vx_evt_sessiongroup_updated_t_in_delayed_playback_set(JLcom/vivox/service/vx_evt_sessiongroup_updated_t;I)V
.end method

.method public static final native vx_evt_sessiongroup_updated_t_last_loop_frame_played_get(JLcom/vivox/service/vx_evt_sessiongroup_updated_t;)I
.end method

.method public static final native vx_evt_sessiongroup_updated_t_last_loop_frame_played_set(JLcom/vivox/service/vx_evt_sessiongroup_updated_t;I)V
.end method

.method public static final native vx_evt_sessiongroup_updated_t_loop_buffer_capacity_get(JLcom/vivox/service/vx_evt_sessiongroup_updated_t;)I
.end method

.method public static final native vx_evt_sessiongroup_updated_t_loop_buffer_capacity_set(JLcom/vivox/service/vx_evt_sessiongroup_updated_t;I)V
.end method

.method public static final native vx_evt_sessiongroup_updated_t_playback_paused_get(JLcom/vivox/service/vx_evt_sessiongroup_updated_t;)I
.end method

.method public static final native vx_evt_sessiongroup_updated_t_playback_paused_set(JLcom/vivox/service/vx_evt_sessiongroup_updated_t;I)V
.end method

.method public static final native vx_evt_sessiongroup_updated_t_sessiongroup_handle_get(JLcom/vivox/service/vx_evt_sessiongroup_updated_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_sessiongroup_updated_t_sessiongroup_handle_set(JLcom/vivox/service/vx_evt_sessiongroup_updated_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_sessiongroup_updated_t_total_loop_frames_captured_get(JLcom/vivox/service/vx_evt_sessiongroup_updated_t;)I
.end method

.method public static final native vx_evt_sessiongroup_updated_t_total_loop_frames_captured_set(JLcom/vivox/service/vx_evt_sessiongroup_updated_t;I)V
.end method

.method public static final native vx_evt_sessiongroup_updated_t_total_recorded_frames_get(JLcom/vivox/service/vx_evt_sessiongroup_updated_t;)I
.end method

.method public static final native vx_evt_sessiongroup_updated_t_total_recorded_frames_set(JLcom/vivox/service/vx_evt_sessiongroup_updated_t;I)V
.end method

.method public static final native vx_evt_subscription_t_account_handle_get(JLcom/vivox/service/vx_evt_subscription_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_subscription_t_account_handle_set(JLcom/vivox/service/vx_evt_subscription_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_subscription_t_application_get(JLcom/vivox/service/vx_evt_subscription_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_subscription_t_application_set(JLcom/vivox/service/vx_evt_subscription_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_subscription_t_base_get(JLcom/vivox/service/vx_evt_subscription_t;)J
.end method

.method public static final native vx_evt_subscription_t_base_set(JLcom/vivox/service/vx_evt_subscription_t;JLcom/vivox/service/vx_evt_base_t;)V
.end method

.method public static final native vx_evt_subscription_t_buddy_uri_get(JLcom/vivox/service/vx_evt_subscription_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_subscription_t_buddy_uri_set(JLcom/vivox/service/vx_evt_subscription_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_subscription_t_displayname_get(JLcom/vivox/service/vx_evt_subscription_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_subscription_t_displayname_set(JLcom/vivox/service/vx_evt_subscription_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_subscription_t_message_get(JLcom/vivox/service/vx_evt_subscription_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_subscription_t_message_set(JLcom/vivox/service/vx_evt_subscription_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_subscription_t_subscription_handle_get(JLcom/vivox/service/vx_evt_subscription_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_subscription_t_subscription_handle_set(JLcom/vivox/service/vx_evt_subscription_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_subscription_t_subscription_type_get(JLcom/vivox/service/vx_evt_subscription_t;)I
.end method

.method public static final native vx_evt_subscription_t_subscription_type_set(JLcom/vivox/service/vx_evt_subscription_t;I)V
.end method

.method public static final native vx_evt_text_stream_updated_t_base_get(JLcom/vivox/service/vx_evt_text_stream_updated_t;)J
.end method

.method public static final native vx_evt_text_stream_updated_t_base_set(JLcom/vivox/service/vx_evt_text_stream_updated_t;JLcom/vivox/service/vx_evt_base_t;)V
.end method

.method public static final native vx_evt_text_stream_updated_t_enabled_get(JLcom/vivox/service/vx_evt_text_stream_updated_t;)I
.end method

.method public static final native vx_evt_text_stream_updated_t_enabled_set(JLcom/vivox/service/vx_evt_text_stream_updated_t;I)V
.end method

.method public static final native vx_evt_text_stream_updated_t_incoming_get(JLcom/vivox/service/vx_evt_text_stream_updated_t;)I
.end method

.method public static final native vx_evt_text_stream_updated_t_incoming_set(JLcom/vivox/service/vx_evt_text_stream_updated_t;I)V
.end method

.method public static final native vx_evt_text_stream_updated_t_session_handle_get(JLcom/vivox/service/vx_evt_text_stream_updated_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_text_stream_updated_t_session_handle_set(JLcom/vivox/service/vx_evt_text_stream_updated_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_text_stream_updated_t_sessiongroup_handle_get(JLcom/vivox/service/vx_evt_text_stream_updated_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_text_stream_updated_t_sessiongroup_handle_set(JLcom/vivox/service/vx_evt_text_stream_updated_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_text_stream_updated_t_state_get(JLcom/vivox/service/vx_evt_text_stream_updated_t;)I
.end method

.method public static final native vx_evt_text_stream_updated_t_state_set(JLcom/vivox/service/vx_evt_text_stream_updated_t;I)V
.end method

.method public static final native vx_evt_text_stream_updated_t_status_code_get(JLcom/vivox/service/vx_evt_text_stream_updated_t;)I
.end method

.method public static final native vx_evt_text_stream_updated_t_status_code_set(JLcom/vivox/service/vx_evt_text_stream_updated_t;I)V
.end method

.method public static final native vx_evt_text_stream_updated_t_status_string_get(JLcom/vivox/service/vx_evt_text_stream_updated_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_text_stream_updated_t_status_string_set(JLcom/vivox/service/vx_evt_text_stream_updated_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_user_app_data_t_account_handle_get(JLcom/vivox/service/vx_evt_user_app_data_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_user_app_data_t_account_handle_set(JLcom/vivox/service/vx_evt_user_app_data_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_user_app_data_t_base_get(JLcom/vivox/service/vx_evt_user_app_data_t;)J
.end method

.method public static final native vx_evt_user_app_data_t_base_set(JLcom/vivox/service/vx_evt_user_app_data_t;JLcom/vivox/service/vx_evt_base_t;)V
.end method

.method public static final native vx_evt_user_app_data_t_content_get(JLcom/vivox/service/vx_evt_user_app_data_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_user_app_data_t_content_set(JLcom/vivox/service/vx_evt_user_app_data_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_user_app_data_t_content_type_get(JLcom/vivox/service/vx_evt_user_app_data_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_user_app_data_t_content_type_set(JLcom/vivox/service/vx_evt_user_app_data_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_user_app_data_t_from_uri_get(JLcom/vivox/service/vx_evt_user_app_data_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_user_app_data_t_from_uri_set(JLcom/vivox/service/vx_evt_user_app_data_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_voice_service_connection_state_changed_t_base_get(JLcom/vivox/service/vx_evt_voice_service_connection_state_changed_t;)J
.end method

.method public static final native vx_evt_voice_service_connection_state_changed_t_base_set(JLcom/vivox/service/vx_evt_voice_service_connection_state_changed_t;JLcom/vivox/service/vx_evt_base_t;)V
.end method

.method public static final native vx_evt_voice_service_connection_state_changed_t_connected_get(JLcom/vivox/service/vx_evt_voice_service_connection_state_changed_t;)I
.end method

.method public static final native vx_evt_voice_service_connection_state_changed_t_connected_set(JLcom/vivox/service/vx_evt_voice_service_connection_state_changed_t;I)V
.end method

.method public static final native vx_evt_voice_service_connection_state_changed_t_data_directory_get(JLcom/vivox/service/vx_evt_voice_service_connection_state_changed_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_voice_service_connection_state_changed_t_data_directory_set(JLcom/vivox/service/vx_evt_voice_service_connection_state_changed_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_voice_service_connection_state_changed_t_network_is_down_get(JLcom/vivox/service/vx_evt_voice_service_connection_state_changed_t;)I
.end method

.method public static final native vx_evt_voice_service_connection_state_changed_t_network_is_down_set(JLcom/vivox/service/vx_evt_voice_service_connection_state_changed_t;I)V
.end method

.method public static final native vx_evt_voice_service_connection_state_changed_t_network_test_completed_get(JLcom/vivox/service/vx_evt_voice_service_connection_state_changed_t;)I
.end method

.method public static final native vx_evt_voice_service_connection_state_changed_t_network_test_completed_set(JLcom/vivox/service/vx_evt_voice_service_connection_state_changed_t;I)V
.end method

.method public static final native vx_evt_voice_service_connection_state_changed_t_network_test_run_get(JLcom/vivox/service/vx_evt_voice_service_connection_state_changed_t;)I
.end method

.method public static final native vx_evt_voice_service_connection_state_changed_t_network_test_run_set(JLcom/vivox/service/vx_evt_voice_service_connection_state_changed_t;I)V
.end method

.method public static final native vx_evt_voice_service_connection_state_changed_t_network_test_state_get(JLcom/vivox/service/vx_evt_voice_service_connection_state_changed_t;)I
.end method

.method public static final native vx_evt_voice_service_connection_state_changed_t_network_test_state_set(JLcom/vivox/service/vx_evt_voice_service_connection_state_changed_t;I)V
.end method

.method public static final native vx_evt_voice_service_connection_state_changed_t_platform_get(JLcom/vivox/service/vx_evt_voice_service_connection_state_changed_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_voice_service_connection_state_changed_t_platform_set(JLcom/vivox/service/vx_evt_voice_service_connection_state_changed_t;Ljava/lang/String;)V
.end method

.method public static final native vx_evt_voice_service_connection_state_changed_t_version_get(JLcom/vivox/service/vx_evt_voice_service_connection_state_changed_t;)Ljava/lang/String;
.end method

.method public static final native vx_evt_voice_service_connection_state_changed_t_version_set(JLcom/vivox/service/vx_evt_voice_service_connection_state_changed_t;Ljava/lang/String;)V
.end method

.method public static final native vx_export_audioBuffer_to_wav_file(JLjava/lang/String;)I
.end method

.method public static final native vx_export_vxr_expanded(Ljava/lang/String;Ljava/lang/String;JJ)I
.end method

.method public static final native vx_export_vxr_mixed(Ljava/lang/String;Ljava/lang/String;JJ)I
.end method

.method public static final native vx_free(Ljava/lang/String;)V
.end method

.method public static final native vx_free_audioBuffer(J)V
.end method

.method public static final native vx_free_sdk_handle(J)I
.end method

.method public static final native vx_generic_credentials_admin_password_get(JLcom/vivox/service/vx_generic_credentials;)Ljava/lang/String;
.end method

.method public static final native vx_generic_credentials_admin_password_set(JLcom/vivox/service/vx_generic_credentials;Ljava/lang/String;)V
.end method

.method public static final native vx_generic_credentials_admin_username_get(JLcom/vivox/service/vx_generic_credentials;)Ljava/lang/String;
.end method

.method public static final native vx_generic_credentials_admin_username_set(JLcom/vivox/service/vx_generic_credentials;Ljava/lang/String;)V
.end method

.method public static final native vx_generic_credentials_grant_document_get(JLcom/vivox/service/vx_generic_credentials;)Ljava/lang/String;
.end method

.method public static final native vx_generic_credentials_grant_document_set(JLcom/vivox/service/vx_generic_credentials;Ljava/lang/String;)V
.end method

.method public static final native vx_generic_credentials_server_url_get(JLcom/vivox/service/vx_generic_credentials;)Ljava/lang/String;
.end method

.method public static final native vx_generic_credentials_server_url_set(JLcom/vivox/service/vx_generic_credentials;Ljava/lang/String;)V
.end method

.method public static final native vx_get_audioBuffer_duration(J)D
.end method

.method public static final native vx_get_crash_dump_count()I
.end method

.method public static final native vx_get_crash_dump_generation()I
.end method

.method public static final native vx_get_crash_dump_timestamp(I)J
.end method

.method public static final native vx_get_default_config(JLcom/vivox/service/vx_sdk_config_t;)I
.end method

.method public static final native vx_get_error_string(I)Ljava/lang/String;
.end method

.method public static final native vx_get_message(J)I
.end method

.method public static final native vx_get_message_type(Ljava/lang/String;)I
.end method

.method public static final native vx_get_preferred_codec(J)I
.end method

.method public static final native vx_get_sdk_version_info()Ljava/lang/String;
.end method

.method public static final native vx_get_system_stats(JLcom/vivox/service/vx_system_stats_t;)I
.end method

.method public static final native vx_get_time_ms()Ljava/math/BigInteger;
.end method

.method public static final native vx_group_create(J)V
.end method

.method public static final native vx_group_free(JLcom/vivox/service/vx_group_t;)V
.end method

.method public static final native vx_group_list_create(IJ)V
.end method

.method public static final native vx_group_list_free(JI)V
.end method

.method public static final native vx_group_t_group_data_get(JLcom/vivox/service/vx_group_t;)Ljava/lang/String;
.end method

.method public static final native vx_group_t_group_data_set(JLcom/vivox/service/vx_group_t;Ljava/lang/String;)V
.end method

.method public static final native vx_group_t_group_id_get(JLcom/vivox/service/vx_group_t;)I
.end method

.method public static final native vx_group_t_group_id_set(JLcom/vivox/service/vx_group_t;I)V
.end method

.method public static final native vx_group_t_group_name_get(JLcom/vivox/service/vx_group_t;)Ljava/lang/String;
.end method

.method public static final native vx_group_t_group_name_set(JLcom/vivox/service/vx_group_t;Ljava/lang/String;)V
.end method

.method public static final native vx_initialize()I
.end method

.method public static final native vx_initialize2(JLcom/vivox/service/vx_sdk_config_t;)I
.end method

.method public static final native vx_issue_request(JLcom/vivox/service/vx_req_base_t;)I
.end method

.method public static final native vx_message_base_t2vx_evt_account_login_state_change_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_evt_aux_audio_properties_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_evt_buddy_and_group_list_changed_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_evt_buddy_changed_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_evt_buddy_group_changed_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_evt_buddy_presence_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_evt_idle_state_changed_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_evt_keyboard_mouse_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_evt_media_completion_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_evt_media_stream_updated_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_evt_message_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_evt_network_message_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_evt_participant_added_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_evt_participant_removed_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_evt_participant_updated_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_evt_server_app_data_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_evt_session_added_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_evt_session_notification_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_evt_session_removed_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_evt_session_updated_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_evt_sessiongroup_added_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_evt_sessiongroup_playback_frame_played_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_evt_sessiongroup_removed_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_evt_sessiongroup_updated_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_evt_subscription_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_evt_text_stream_updated_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_evt_user_app_data_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_evt_voice_service_connection_state_changed_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_account_anonymous_login_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_account_authtoken_login_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_account_buddy_delete_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_account_buddy_search_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_account_buddy_set_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_account_buddygroup_delete_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_account_buddygroup_set_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_account_channel_add_acl_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_account_channel_add_moderator_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_account_channel_change_owner_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_account_channel_create_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_account_channel_delete_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_account_channel_favorite_delete_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_account_channel_favorite_group_delete_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_account_channel_favorite_group_set_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_account_channel_favorite_set_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_account_channel_favorites_get_list_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_account_channel_get_acl_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_account_channel_get_info_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_account_channel_get_moderators_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_account_channel_get_participants_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_account_channel_remove_acl_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_account_channel_remove_moderator_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_account_channel_search_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_account_channel_update_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_account_create_auto_accept_rule_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_account_create_block_rule_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_account_delete_auto_accept_rule_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_account_delete_block_rule_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_account_get_account_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_account_get_session_fonts_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_account_get_template_fonts_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_account_list_auto_accept_rules_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_account_list_block_rules_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_account_list_buddies_and_groups_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_account_login_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_account_logout_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_account_post_crash_dump_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_account_send_message_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_account_send_sms_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_account_send_subscription_reply_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_account_send_user_app_data_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_account_set_login_properties_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_account_set_presence_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_account_update_account_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_account_web_call_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_aux_capture_audio_start_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_aux_capture_audio_stop_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_aux_connectivity_info_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_aux_create_account_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_aux_deactivate_account_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_aux_get_capture_devices_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_aux_get_mic_level_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_aux_get_render_devices_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_aux_get_speaker_level_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_aux_get_vad_properties_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_aux_global_monitor_keyboard_mouse_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_aux_play_audio_buffer_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_aux_reactivate_account_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_aux_render_audio_modify_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_aux_render_audio_start_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_aux_render_audio_stop_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_aux_reset_password_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_aux_set_capture_device_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_aux_set_idle_timeout_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_aux_set_mic_level_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_aux_set_render_device_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_aux_set_speaker_level_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_aux_set_vad_properties_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_aux_start_buffer_capture_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_channel_ban_user_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_channel_get_banned_users_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_channel_kick_user_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_channel_mute_all_users_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_channel_mute_user_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_channel_set_lock_mode_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_connector_create_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_connector_get_local_audio_info_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_connector_initiate_shutdown_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_connector_mute_local_mic_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_connector_mute_local_speaker_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_connector_set_local_mic_volume_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_connector_set_local_speaker_volume_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_session_channel_invite_user_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_session_create_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_session_media_connect_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_session_media_disconnect_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_session_mute_local_speaker_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_session_send_dtmf_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_session_send_message_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_session_send_notification_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_session_set_3d_position_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_session_set_local_speaker_volume_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_session_set_participant_mute_for_me_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_session_set_participant_volume_for_me_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_session_set_voice_font_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_session_terminate_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_session_text_connect_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_session_text_disconnect_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_sessiongroup_add_session_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_sessiongroup_control_audio_injection_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_sessiongroup_control_playback_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_sessiongroup_control_recording_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_sessiongroup_create_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_sessiongroup_get_stats_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_sessiongroup_remove_session_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_sessiongroup_reset_focus_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_sessiongroup_set_focus_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_sessiongroup_set_playback_options_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_sessiongroup_set_session_3d_position_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_sessiongroup_set_tx_all_sessions_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_sessiongroup_set_tx_no_session_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_sessiongroup_set_tx_session_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_sessiongroup_terminate_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_req_sessiongroup_unset_focus_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_account_anonymous_login_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_account_authtoken_login_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_account_buddy_delete_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_account_buddy_search_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_account_buddy_set_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_account_buddygroup_delete_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_account_buddygroup_set_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_account_channel_add_acl_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_account_channel_add_moderator_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_account_channel_change_owner_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_account_channel_create_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_account_channel_delete_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_account_channel_favorite_delete_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_account_channel_favorite_group_delete_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_account_channel_favorite_group_set_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_account_channel_favorite_set_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_account_channel_favorites_get_list_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_account_channel_get_acl_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_account_channel_get_info_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_account_channel_get_moderators_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_account_channel_get_participants_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_account_channel_remove_acl_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_account_channel_remove_moderator_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_account_channel_search_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_account_channel_update_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_account_create_auto_accept_rule_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_account_create_block_rule_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_account_delete_auto_accept_rule_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_account_delete_block_rule_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_account_get_account_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_account_get_session_fonts_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_account_get_template_fonts_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_account_list_auto_accept_rules_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_account_list_block_rules_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_account_list_buddies_and_groups_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_account_login_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_account_logout_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_account_post_crash_dump_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_account_send_message_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_account_send_sms_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_account_send_subscription_reply_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_account_send_user_app_data_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_account_set_login_properties_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_account_set_presence_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_account_update_account_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_account_web_call_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_aux_capture_audio_start_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_aux_capture_audio_stop_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_aux_connectivity_info_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_aux_create_account_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_aux_deactivate_account_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_aux_diagnostic_state_dump_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_aux_get_capture_devices_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_aux_get_mic_level_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_aux_get_render_devices_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_aux_get_speaker_level_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_aux_get_vad_properties_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_aux_global_monitor_keyboard_mouse_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_aux_play_audio_buffer_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_aux_reactivate_account_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_aux_render_audio_modify_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_aux_render_audio_start_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_aux_render_audio_stop_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_aux_reset_password_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_aux_set_capture_device_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_aux_set_idle_timeout_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_aux_set_mic_level_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_aux_set_render_device_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_aux_set_speaker_level_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_aux_set_vad_properties_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_aux_start_buffer_capture_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_channel_ban_user_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_channel_get_banned_users_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_channel_kick_user_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_channel_mute_all_users_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_channel_mute_user_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_channel_set_lock_mode_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_connector_create_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_connector_get_local_audio_info_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_connector_initiate_shutdown_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_connector_mute_local_mic_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_connector_mute_local_speaker_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_connector_set_local_mic_volume_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_connector_set_local_speaker_volume_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_session_channel_invite_user_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_session_create_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_session_media_connect_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_session_media_disconnect_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_session_mute_local_speaker_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_session_send_dtmf_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_session_send_message_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_session_send_notification_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_session_set_3d_position_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_session_set_local_speaker_volume_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_session_set_participant_mute_for_me_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_session_set_participant_volume_for_me_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_session_set_voice_font_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_session_terminate_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_session_text_connect_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_session_text_disconnect_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_sessiongroup_add_session_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_sessiongroup_control_audio_injection_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_sessiongroup_control_playback_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_sessiongroup_control_recording_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_sessiongroup_create_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_sessiongroup_get_stats_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_sessiongroup_remove_session_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_sessiongroup_reset_focus_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_sessiongroup_set_focus_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_sessiongroup_set_playback_options_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_sessiongroup_set_session_3d_position_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_sessiongroup_set_tx_all_sessions_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_sessiongroup_set_tx_no_session_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_sessiongroup_set_tx_session_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_sessiongroup_terminate_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t2vx_resp_sessiongroup_unset_focus_t(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t_create_time_ms_get(JLcom/vivox/service/vx_message_base_t;)Ljava/math/BigInteger;
.end method

.method public static final native vx_message_base_t_create_time_ms_set(JLcom/vivox/service/vx_message_base_t;Ljava/math/BigInteger;)V
.end method

.method public static final native vx_message_base_t_last_step_ms_get(JLcom/vivox/service/vx_message_base_t;)Ljava/math/BigInteger;
.end method

.method public static final native vx_message_base_t_last_step_ms_set(JLcom/vivox/service/vx_message_base_t;Ljava/math/BigInteger;)V
.end method

.method public static final native vx_message_base_t_sdk_handle_get(JLcom/vivox/service/vx_message_base_t;)J
.end method

.method public static final native vx_message_base_t_sdk_handle_set(JLcom/vivox/service/vx_message_base_t;J)V
.end method

.method public static final native vx_message_base_t_type_get(JLcom/vivox/service/vx_message_base_t;)I
.end method

.method public static final native vx_message_base_t_type_set(JLcom/vivox/service/vx_message_base_t;I)V
.end method

.method public static final native vx_name_value_pair_create(J)V
.end method

.method public static final native vx_name_value_pair_free(JLcom/vivox/service/vx_name_value_pair_t;)V
.end method

.method public static final native vx_name_value_pair_t_name_get(JLcom/vivox/service/vx_name_value_pair_t;)Ljava/lang/String;
.end method

.method public static final native vx_name_value_pair_t_name_set(JLcom/vivox/service/vx_name_value_pair_t;Ljava/lang/String;)V
.end method

.method public static final native vx_name_value_pair_t_value_get(JLcom/vivox/service/vx_name_value_pair_t;)Ljava/lang/String;
.end method

.method public static final native vx_name_value_pair_t_value_set(JLcom/vivox/service/vx_name_value_pair_t;Ljava/lang/String;)V
.end method

.method public static final native vx_name_value_pairs_create(IJ)V
.end method

.method public static final native vx_name_value_pairs_free(JI)V
.end method

.method public static final native vx_name_value_pairs_t_get_list_item(JI)J
.end method

.method public static final native vx_name_value_pairs_t_set_list_item(JIJLcom/vivox/service/vx_name_value_pair_t;)V
.end method

.method public static final native vx_on_application_exit()V
.end method

.method public static final native vx_participant_create(J)V
.end method

.method public static final native vx_participant_free(JLcom/vivox/service/vx_participant_t;)V
.end method

.method public static final native vx_participant_list_create(IJ)V
.end method

.method public static final native vx_participant_list_free(JI)V
.end method

.method public static final native vx_participant_t_account_id_get(JLcom/vivox/service/vx_participant_t;)I
.end method

.method public static final native vx_participant_t_account_id_set(JLcom/vivox/service/vx_participant_t;I)V
.end method

.method public static final native vx_participant_t_display_name_get(JLcom/vivox/service/vx_participant_t;)Ljava/lang/String;
.end method

.method public static final native vx_participant_t_display_name_set(JLcom/vivox/service/vx_participant_t;Ljava/lang/String;)V
.end method

.method public static final native vx_participant_t_first_name_get(JLcom/vivox/service/vx_participant_t;)Ljava/lang/String;
.end method

.method public static final native vx_participant_t_first_name_set(JLcom/vivox/service/vx_participant_t;Ljava/lang/String;)V
.end method

.method public static final native vx_participant_t_is_moderator_get(JLcom/vivox/service/vx_participant_t;)I
.end method

.method public static final native vx_participant_t_is_moderator_muted_get(JLcom/vivox/service/vx_participant_t;)I
.end method

.method public static final native vx_participant_t_is_moderator_muted_set(JLcom/vivox/service/vx_participant_t;I)V
.end method

.method public static final native vx_participant_t_is_moderator_set(JLcom/vivox/service/vx_participant_t;I)V
.end method

.method public static final native vx_participant_t_is_moderator_text_muted_get(JLcom/vivox/service/vx_participant_t;)I
.end method

.method public static final native vx_participant_t_is_moderator_text_muted_set(JLcom/vivox/service/vx_participant_t;I)V
.end method

.method public static final native vx_participant_t_is_muted_for_me_get(JLcom/vivox/service/vx_participant_t;)I
.end method

.method public static final native vx_participant_t_is_muted_for_me_set(JLcom/vivox/service/vx_participant_t;I)V
.end method

.method public static final native vx_participant_t_is_owner_get(JLcom/vivox/service/vx_participant_t;)I
.end method

.method public static final native vx_participant_t_is_owner_set(JLcom/vivox/service/vx_participant_t;I)V
.end method

.method public static final native vx_participant_t_last_name_get(JLcom/vivox/service/vx_participant_t;)Ljava/lang/String;
.end method

.method public static final native vx_participant_t_last_name_set(JLcom/vivox/service/vx_participant_t;Ljava/lang/String;)V
.end method

.method public static final native vx_participant_t_uri_get(JLcom/vivox/service/vx_participant_t;)Ljava/lang/String;
.end method

.method public static final native vx_participant_t_uri_set(JLcom/vivox/service/vx_participant_t;Ljava/lang/String;)V
.end method

.method public static final native vx_participant_t_username_get(JLcom/vivox/service/vx_participant_t;)Ljava/lang/String;
.end method

.method public static final native vx_participant_t_username_set(JLcom/vivox/service/vx_participant_t;Ljava/lang/String;)V
.end method

.method public static final native vx_read_crash_dump(I)Ljava/lang/String;
.end method

.method public static final native vx_recording_frame_create(J)V
.end method

.method public static final native vx_recording_frame_free(JLcom/vivox/service/vx_recording_frame_t;)V
.end method

.method public static final native vx_recording_frame_list_create(IJ)V
.end method

.method public static final native vx_recording_frame_list_free(JI)V
.end method

.method public static final native vx_recording_frame_t_frame_data_get(JLcom/vivox/service/vx_recording_frame_t;)J
.end method

.method public static final native vx_recording_frame_t_frame_data_set(JLcom/vivox/service/vx_recording_frame_t;J)V
.end method

.method public static final native vx_recording_frame_t_frame_size_get(JLcom/vivox/service/vx_recording_frame_t;)I
.end method

.method public static final native vx_recording_frame_t_frame_size_set(JLcom/vivox/service/vx_recording_frame_t;I)V
.end method

.method public static final native vx_recording_frame_t_frame_type_get(JLcom/vivox/service/vx_recording_frame_t;)I
.end method

.method public static final native vx_recording_frame_t_frame_type_set(JLcom/vivox/service/vx_recording_frame_t;I)V
.end method

.method public static final native vx_register_logging_initialization(ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;IJ)V
.end method

.method public static final native vx_register_message_notification_handler(JJ)V
.end method

.method public static final native vx_req_account_anonymous_login_create(J)V
.end method

.method public static final native vx_req_account_anonymous_login_t_acct_mgmt_server_get(JLcom/vivox/service/vx_req_account_anonymous_login_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_anonymous_login_t_acct_mgmt_server_set(JLcom/vivox/service/vx_req_account_anonymous_login_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_anonymous_login_t_application_override_get(JLcom/vivox/service/vx_req_account_anonymous_login_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_anonymous_login_t_application_override_set(JLcom/vivox/service/vx_req_account_anonymous_login_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_anonymous_login_t_application_token_get(JLcom/vivox/service/vx_req_account_anonymous_login_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_anonymous_login_t_application_token_set(JLcom/vivox/service/vx_req_account_anonymous_login_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_anonymous_login_t_autopost_crash_dumps_get(JLcom/vivox/service/vx_req_account_anonymous_login_t;)I
.end method

.method public static final native vx_req_account_anonymous_login_t_autopost_crash_dumps_set(JLcom/vivox/service/vx_req_account_anonymous_login_t;I)V
.end method

.method public static final native vx_req_account_anonymous_login_t_base_get(JLcom/vivox/service/vx_req_account_anonymous_login_t;)J
.end method

.method public static final native vx_req_account_anonymous_login_t_base_set(JLcom/vivox/service/vx_req_account_anonymous_login_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_account_anonymous_login_t_buddy_management_mode_get(JLcom/vivox/service/vx_req_account_anonymous_login_t;)I
.end method

.method public static final native vx_req_account_anonymous_login_t_buddy_management_mode_set(JLcom/vivox/service/vx_req_account_anonymous_login_t;I)V
.end method

.method public static final native vx_req_account_anonymous_login_t_connector_handle_get(JLcom/vivox/service/vx_req_account_anonymous_login_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_anonymous_login_t_connector_handle_set(JLcom/vivox/service/vx_req_account_anonymous_login_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_anonymous_login_t_displayname_get(JLcom/vivox/service/vx_req_account_anonymous_login_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_anonymous_login_t_displayname_set(JLcom/vivox/service/vx_req_account_anonymous_login_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_anonymous_login_t_enable_buddies_and_presence_get(JLcom/vivox/service/vx_req_account_anonymous_login_t;)I
.end method

.method public static final native vx_req_account_anonymous_login_t_enable_buddies_and_presence_set(JLcom/vivox/service/vx_req_account_anonymous_login_t;I)V
.end method

.method public static final native vx_req_account_anonymous_login_t_participant_property_frequency_get(JLcom/vivox/service/vx_req_account_anonymous_login_t;)I
.end method

.method public static final native vx_req_account_anonymous_login_t_participant_property_frequency_set(JLcom/vivox/service/vx_req_account_anonymous_login_t;I)V
.end method

.method public static final native vx_req_account_authtoken_login_create(J)V
.end method

.method public static final native vx_req_account_authtoken_login_t_acct_mgmt_server_get(JLcom/vivox/service/vx_req_account_authtoken_login_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_authtoken_login_t_acct_mgmt_server_set(JLcom/vivox/service/vx_req_account_authtoken_login_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_authtoken_login_t_answer_mode_get(JLcom/vivox/service/vx_req_account_authtoken_login_t;)I
.end method

.method public static final native vx_req_account_authtoken_login_t_answer_mode_set(JLcom/vivox/service/vx_req_account_authtoken_login_t;I)V
.end method

.method public static final native vx_req_account_authtoken_login_t_application_override_get(JLcom/vivox/service/vx_req_account_authtoken_login_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_authtoken_login_t_application_override_set(JLcom/vivox/service/vx_req_account_authtoken_login_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_authtoken_login_t_application_token_get(JLcom/vivox/service/vx_req_account_authtoken_login_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_authtoken_login_t_application_token_set(JLcom/vivox/service/vx_req_account_authtoken_login_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_authtoken_login_t_authtoken_get(JLcom/vivox/service/vx_req_account_authtoken_login_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_authtoken_login_t_authtoken_set(JLcom/vivox/service/vx_req_account_authtoken_login_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_authtoken_login_t_autopost_crash_dumps_get(JLcom/vivox/service/vx_req_account_authtoken_login_t;)I
.end method

.method public static final native vx_req_account_authtoken_login_t_autopost_crash_dumps_set(JLcom/vivox/service/vx_req_account_authtoken_login_t;I)V
.end method

.method public static final native vx_req_account_authtoken_login_t_base_get(JLcom/vivox/service/vx_req_account_authtoken_login_t;)J
.end method

.method public static final native vx_req_account_authtoken_login_t_base_set(JLcom/vivox/service/vx_req_account_authtoken_login_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_account_authtoken_login_t_buddy_management_mode_get(JLcom/vivox/service/vx_req_account_authtoken_login_t;)I
.end method

.method public static final native vx_req_account_authtoken_login_t_buddy_management_mode_set(JLcom/vivox/service/vx_req_account_authtoken_login_t;I)V
.end method

.method public static final native vx_req_account_authtoken_login_t_connector_handle_get(JLcom/vivox/service/vx_req_account_authtoken_login_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_authtoken_login_t_connector_handle_set(JLcom/vivox/service/vx_req_account_authtoken_login_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_authtoken_login_t_enable_buddies_and_presence_get(JLcom/vivox/service/vx_req_account_authtoken_login_t;)I
.end method

.method public static final native vx_req_account_authtoken_login_t_enable_buddies_and_presence_set(JLcom/vivox/service/vx_req_account_authtoken_login_t;I)V
.end method

.method public static final native vx_req_account_authtoken_login_t_enable_text_get(JLcom/vivox/service/vx_req_account_authtoken_login_t;)I
.end method

.method public static final native vx_req_account_authtoken_login_t_enable_text_set(JLcom/vivox/service/vx_req_account_authtoken_login_t;I)V
.end method

.method public static final native vx_req_account_authtoken_login_t_participant_property_frequency_get(JLcom/vivox/service/vx_req_account_authtoken_login_t;)I
.end method

.method public static final native vx_req_account_authtoken_login_t_participant_property_frequency_set(JLcom/vivox/service/vx_req_account_authtoken_login_t;I)V
.end method

.method public static final native vx_req_account_buddy_delete_create(J)V
.end method

.method public static final native vx_req_account_buddy_delete_t_account_handle_get(JLcom/vivox/service/vx_req_account_buddy_delete_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_buddy_delete_t_account_handle_set(JLcom/vivox/service/vx_req_account_buddy_delete_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_buddy_delete_t_base_get(JLcom/vivox/service/vx_req_account_buddy_delete_t;)J
.end method

.method public static final native vx_req_account_buddy_delete_t_base_set(JLcom/vivox/service/vx_req_account_buddy_delete_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_account_buddy_delete_t_buddy_uri_get(JLcom/vivox/service/vx_req_account_buddy_delete_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_buddy_delete_t_buddy_uri_set(JLcom/vivox/service/vx_req_account_buddy_delete_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_buddy_search_create(J)V
.end method

.method public static final native vx_req_account_buddy_search_t_account_handle_get(JLcom/vivox/service/vx_req_account_buddy_search_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_buddy_search_t_account_handle_set(JLcom/vivox/service/vx_req_account_buddy_search_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_buddy_search_t_base_get(JLcom/vivox/service/vx_req_account_buddy_search_t;)J
.end method

.method public static final native vx_req_account_buddy_search_t_base_set(JLcom/vivox/service/vx_req_account_buddy_search_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_account_buddy_search_t_begins_with_get(JLcom/vivox/service/vx_req_account_buddy_search_t;)I
.end method

.method public static final native vx_req_account_buddy_search_t_begins_with_set(JLcom/vivox/service/vx_req_account_buddy_search_t;I)V
.end method

.method public static final native vx_req_account_buddy_search_t_buddy_display_name_get(JLcom/vivox/service/vx_req_account_buddy_search_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_buddy_search_t_buddy_display_name_set(JLcom/vivox/service/vx_req_account_buddy_search_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_buddy_search_t_buddy_email_get(JLcom/vivox/service/vx_req_account_buddy_search_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_buddy_search_t_buddy_email_set(JLcom/vivox/service/vx_req_account_buddy_search_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_buddy_search_t_buddy_first_name_get(JLcom/vivox/service/vx_req_account_buddy_search_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_buddy_search_t_buddy_first_name_set(JLcom/vivox/service/vx_req_account_buddy_search_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_buddy_search_t_buddy_last_name_get(JLcom/vivox/service/vx_req_account_buddy_search_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_buddy_search_t_buddy_last_name_set(JLcom/vivox/service/vx_req_account_buddy_search_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_buddy_search_t_buddy_user_name_get(JLcom/vivox/service/vx_req_account_buddy_search_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_buddy_search_t_buddy_user_name_set(JLcom/vivox/service/vx_req_account_buddy_search_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_buddy_search_t_page_number_get(JLcom/vivox/service/vx_req_account_buddy_search_t;)I
.end method

.method public static final native vx_req_account_buddy_search_t_page_number_set(JLcom/vivox/service/vx_req_account_buddy_search_t;I)V
.end method

.method public static final native vx_req_account_buddy_search_t_page_size_get(JLcom/vivox/service/vx_req_account_buddy_search_t;)I
.end method

.method public static final native vx_req_account_buddy_search_t_page_size_set(JLcom/vivox/service/vx_req_account_buddy_search_t;I)V
.end method

.method public static final native vx_req_account_buddy_set_create(J)V
.end method

.method public static final native vx_req_account_buddy_set_t_account_handle_get(JLcom/vivox/service/vx_req_account_buddy_set_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_buddy_set_t_account_handle_set(JLcom/vivox/service/vx_req_account_buddy_set_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_buddy_set_t_base_get(JLcom/vivox/service/vx_req_account_buddy_set_t;)J
.end method

.method public static final native vx_req_account_buddy_set_t_base_set(JLcom/vivox/service/vx_req_account_buddy_set_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_account_buddy_set_t_buddy_data_get(JLcom/vivox/service/vx_req_account_buddy_set_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_buddy_set_t_buddy_data_set(JLcom/vivox/service/vx_req_account_buddy_set_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_buddy_set_t_buddy_uri_get(JLcom/vivox/service/vx_req_account_buddy_set_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_buddy_set_t_buddy_uri_set(JLcom/vivox/service/vx_req_account_buddy_set_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_buddy_set_t_display_name_get(JLcom/vivox/service/vx_req_account_buddy_set_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_buddy_set_t_display_name_set(JLcom/vivox/service/vx_req_account_buddy_set_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_buddy_set_t_group_id_get(JLcom/vivox/service/vx_req_account_buddy_set_t;)I
.end method

.method public static final native vx_req_account_buddy_set_t_group_id_set(JLcom/vivox/service/vx_req_account_buddy_set_t;I)V
.end method

.method public static final native vx_req_account_buddy_set_t_message_get(JLcom/vivox/service/vx_req_account_buddy_set_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_buddy_set_t_message_set(JLcom/vivox/service/vx_req_account_buddy_set_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_buddygroup_delete_create(J)V
.end method

.method public static final native vx_req_account_buddygroup_delete_t_account_handle_get(JLcom/vivox/service/vx_req_account_buddygroup_delete_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_buddygroup_delete_t_account_handle_set(JLcom/vivox/service/vx_req_account_buddygroup_delete_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_buddygroup_delete_t_base_get(JLcom/vivox/service/vx_req_account_buddygroup_delete_t;)J
.end method

.method public static final native vx_req_account_buddygroup_delete_t_base_set(JLcom/vivox/service/vx_req_account_buddygroup_delete_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_account_buddygroup_delete_t_group_id_get(JLcom/vivox/service/vx_req_account_buddygroup_delete_t;)I
.end method

.method public static final native vx_req_account_buddygroup_delete_t_group_id_set(JLcom/vivox/service/vx_req_account_buddygroup_delete_t;I)V
.end method

.method public static final native vx_req_account_buddygroup_set_create(J)V
.end method

.method public static final native vx_req_account_buddygroup_set_t_account_handle_get(JLcom/vivox/service/vx_req_account_buddygroup_set_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_buddygroup_set_t_account_handle_set(JLcom/vivox/service/vx_req_account_buddygroup_set_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_buddygroup_set_t_base_get(JLcom/vivox/service/vx_req_account_buddygroup_set_t;)J
.end method

.method public static final native vx_req_account_buddygroup_set_t_base_set(JLcom/vivox/service/vx_req_account_buddygroup_set_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_account_buddygroup_set_t_group_data_get(JLcom/vivox/service/vx_req_account_buddygroup_set_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_buddygroup_set_t_group_data_set(JLcom/vivox/service/vx_req_account_buddygroup_set_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_buddygroup_set_t_group_id_get(JLcom/vivox/service/vx_req_account_buddygroup_set_t;)I
.end method

.method public static final native vx_req_account_buddygroup_set_t_group_id_set(JLcom/vivox/service/vx_req_account_buddygroup_set_t;I)V
.end method

.method public static final native vx_req_account_buddygroup_set_t_group_name_get(JLcom/vivox/service/vx_req_account_buddygroup_set_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_buddygroup_set_t_group_name_set(JLcom/vivox/service/vx_req_account_buddygroup_set_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_channel_add_acl_create(J)V
.end method

.method public static final native vx_req_account_channel_add_acl_t_account_handle_get(JLcom/vivox/service/vx_req_account_channel_add_acl_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_channel_add_acl_t_account_handle_set(JLcom/vivox/service/vx_req_account_channel_add_acl_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_channel_add_acl_t_acl_uri_get(JLcom/vivox/service/vx_req_account_channel_add_acl_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_channel_add_acl_t_acl_uri_set(JLcom/vivox/service/vx_req_account_channel_add_acl_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_channel_add_acl_t_base_get(JLcom/vivox/service/vx_req_account_channel_add_acl_t;)J
.end method

.method public static final native vx_req_account_channel_add_acl_t_base_set(JLcom/vivox/service/vx_req_account_channel_add_acl_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_account_channel_add_acl_t_channel_uri_get(JLcom/vivox/service/vx_req_account_channel_add_acl_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_channel_add_acl_t_channel_uri_set(JLcom/vivox/service/vx_req_account_channel_add_acl_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_channel_add_moderator_create(J)V
.end method

.method public static final native vx_req_account_channel_add_moderator_t_account_handle_get(JLcom/vivox/service/vx_req_account_channel_add_moderator_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_channel_add_moderator_t_account_handle_set(JLcom/vivox/service/vx_req_account_channel_add_moderator_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_channel_add_moderator_t_base_get(JLcom/vivox/service/vx_req_account_channel_add_moderator_t;)J
.end method

.method public static final native vx_req_account_channel_add_moderator_t_base_set(JLcom/vivox/service/vx_req_account_channel_add_moderator_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_account_channel_add_moderator_t_channel_name_get(JLcom/vivox/service/vx_req_account_channel_add_moderator_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_channel_add_moderator_t_channel_name_set(JLcom/vivox/service/vx_req_account_channel_add_moderator_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_channel_add_moderator_t_channel_uri_get(JLcom/vivox/service/vx_req_account_channel_add_moderator_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_channel_add_moderator_t_channel_uri_set(JLcom/vivox/service/vx_req_account_channel_add_moderator_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_channel_add_moderator_t_moderator_uri_get(JLcom/vivox/service/vx_req_account_channel_add_moderator_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_channel_add_moderator_t_moderator_uri_set(JLcom/vivox/service/vx_req_account_channel_add_moderator_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_channel_change_owner_create(J)V
.end method

.method public static final native vx_req_account_channel_change_owner_t_account_handle_get(JLcom/vivox/service/vx_req_account_channel_change_owner_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_channel_change_owner_t_account_handle_set(JLcom/vivox/service/vx_req_account_channel_change_owner_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_channel_change_owner_t_base_get(JLcom/vivox/service/vx_req_account_channel_change_owner_t;)J
.end method

.method public static final native vx_req_account_channel_change_owner_t_base_set(JLcom/vivox/service/vx_req_account_channel_change_owner_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_account_channel_change_owner_t_channel_uri_get(JLcom/vivox/service/vx_req_account_channel_change_owner_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_channel_change_owner_t_channel_uri_set(JLcom/vivox/service/vx_req_account_channel_change_owner_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_channel_change_owner_t_new_owner_uri_get(JLcom/vivox/service/vx_req_account_channel_change_owner_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_channel_change_owner_t_new_owner_uri_set(JLcom/vivox/service/vx_req_account_channel_change_owner_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_channel_create_create(J)V
.end method

.method public static final native vx_req_account_channel_create_t_account_handle_get(JLcom/vivox/service/vx_req_account_channel_create_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_channel_create_t_account_handle_set(JLcom/vivox/service/vx_req_account_channel_create_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_channel_create_t_base_get(JLcom/vivox/service/vx_req_account_channel_create_t;)J
.end method

.method public static final native vx_req_account_channel_create_t_base_set(JLcom/vivox/service/vx_req_account_channel_create_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_account_channel_create_t_capacity_get(JLcom/vivox/service/vx_req_account_channel_create_t;)I
.end method

.method public static final native vx_req_account_channel_create_t_capacity_set(JLcom/vivox/service/vx_req_account_channel_create_t;I)V
.end method

.method public static final native vx_req_account_channel_create_t_channel_desc_get(JLcom/vivox/service/vx_req_account_channel_create_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_channel_create_t_channel_desc_set(JLcom/vivox/service/vx_req_account_channel_create_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_channel_create_t_channel_mode_get(JLcom/vivox/service/vx_req_account_channel_create_t;)I
.end method

.method public static final native vx_req_account_channel_create_t_channel_mode_set(JLcom/vivox/service/vx_req_account_channel_create_t;I)V
.end method

.method public static final native vx_req_account_channel_create_t_channel_name_get(JLcom/vivox/service/vx_req_account_channel_create_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_channel_create_t_channel_name_set(JLcom/vivox/service/vx_req_account_channel_create_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_channel_create_t_channel_type_get(JLcom/vivox/service/vx_req_account_channel_create_t;)I
.end method

.method public static final native vx_req_account_channel_create_t_channel_type_set(JLcom/vivox/service/vx_req_account_channel_create_t;I)V
.end method

.method public static final native vx_req_account_channel_create_t_clamping_dist_get(JLcom/vivox/service/vx_req_account_channel_create_t;)I
.end method

.method public static final native vx_req_account_channel_create_t_clamping_dist_set(JLcom/vivox/service/vx_req_account_channel_create_t;I)V
.end method

.method public static final native vx_req_account_channel_create_t_dist_model_get(JLcom/vivox/service/vx_req_account_channel_create_t;)I
.end method

.method public static final native vx_req_account_channel_create_t_dist_model_set(JLcom/vivox/service/vx_req_account_channel_create_t;I)V
.end method

.method public static final native vx_req_account_channel_create_t_encrypt_audio_get(JLcom/vivox/service/vx_req_account_channel_create_t;)I
.end method

.method public static final native vx_req_account_channel_create_t_encrypt_audio_set(JLcom/vivox/service/vx_req_account_channel_create_t;I)V
.end method

.method public static final native vx_req_account_channel_create_t_max_gain_get(JLcom/vivox/service/vx_req_account_channel_create_t;)D
.end method

.method public static final native vx_req_account_channel_create_t_max_gain_set(JLcom/vivox/service/vx_req_account_channel_create_t;D)V
.end method

.method public static final native vx_req_account_channel_create_t_max_participants_get(JLcom/vivox/service/vx_req_account_channel_create_t;)I
.end method

.method public static final native vx_req_account_channel_create_t_max_participants_set(JLcom/vivox/service/vx_req_account_channel_create_t;I)V
.end method

.method public static final native vx_req_account_channel_create_t_max_range_get(JLcom/vivox/service/vx_req_account_channel_create_t;)I
.end method

.method public static final native vx_req_account_channel_create_t_max_range_set(JLcom/vivox/service/vx_req_account_channel_create_t;I)V
.end method

.method public static final native vx_req_account_channel_create_t_protected_password_get(JLcom/vivox/service/vx_req_account_channel_create_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_channel_create_t_protected_password_set(JLcom/vivox/service/vx_req_account_channel_create_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_channel_create_t_roll_off_get(JLcom/vivox/service/vx_req_account_channel_create_t;)D
.end method

.method public static final native vx_req_account_channel_create_t_roll_off_set(JLcom/vivox/service/vx_req_account_channel_create_t;D)V
.end method

.method public static final native vx_req_account_channel_create_t_set_persistent_get(JLcom/vivox/service/vx_req_account_channel_create_t;)I
.end method

.method public static final native vx_req_account_channel_create_t_set_persistent_set(JLcom/vivox/service/vx_req_account_channel_create_t;I)V
.end method

.method public static final native vx_req_account_channel_create_t_set_protected_get(JLcom/vivox/service/vx_req_account_channel_create_t;)I
.end method

.method public static final native vx_req_account_channel_create_t_set_protected_set(JLcom/vivox/service/vx_req_account_channel_create_t;I)V
.end method

.method public static final native vx_req_account_channel_delete_create(J)V
.end method

.method public static final native vx_req_account_channel_delete_t_account_handle_get(JLcom/vivox/service/vx_req_account_channel_delete_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_channel_delete_t_account_handle_set(JLcom/vivox/service/vx_req_account_channel_delete_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_channel_delete_t_base_get(JLcom/vivox/service/vx_req_account_channel_delete_t;)J
.end method

.method public static final native vx_req_account_channel_delete_t_base_set(JLcom/vivox/service/vx_req_account_channel_delete_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_account_channel_delete_t_channel_uri_get(JLcom/vivox/service/vx_req_account_channel_delete_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_channel_delete_t_channel_uri_set(JLcom/vivox/service/vx_req_account_channel_delete_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_channel_favorite_delete_create(J)V
.end method

.method public static final native vx_req_account_channel_favorite_delete_t_account_handle_get(JLcom/vivox/service/vx_req_account_channel_favorite_delete_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_channel_favorite_delete_t_account_handle_set(JLcom/vivox/service/vx_req_account_channel_favorite_delete_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_channel_favorite_delete_t_base_get(JLcom/vivox/service/vx_req_account_channel_favorite_delete_t;)J
.end method

.method public static final native vx_req_account_channel_favorite_delete_t_base_set(JLcom/vivox/service/vx_req_account_channel_favorite_delete_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_account_channel_favorite_delete_t_channel_favorite_id_get(JLcom/vivox/service/vx_req_account_channel_favorite_delete_t;)I
.end method

.method public static final native vx_req_account_channel_favorite_delete_t_channel_favorite_id_set(JLcom/vivox/service/vx_req_account_channel_favorite_delete_t;I)V
.end method

.method public static final native vx_req_account_channel_favorite_group_delete_create(J)V
.end method

.method public static final native vx_req_account_channel_favorite_group_delete_t_account_handle_get(JLcom/vivox/service/vx_req_account_channel_favorite_group_delete_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_channel_favorite_group_delete_t_account_handle_set(JLcom/vivox/service/vx_req_account_channel_favorite_group_delete_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_channel_favorite_group_delete_t_base_get(JLcom/vivox/service/vx_req_account_channel_favorite_group_delete_t;)J
.end method

.method public static final native vx_req_account_channel_favorite_group_delete_t_base_set(JLcom/vivox/service/vx_req_account_channel_favorite_group_delete_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_account_channel_favorite_group_delete_t_channel_favorite_group_id_get(JLcom/vivox/service/vx_req_account_channel_favorite_group_delete_t;)I
.end method

.method public static final native vx_req_account_channel_favorite_group_delete_t_channel_favorite_group_id_set(JLcom/vivox/service/vx_req_account_channel_favorite_group_delete_t;I)V
.end method

.method public static final native vx_req_account_channel_favorite_group_set_create(J)V
.end method

.method public static final native vx_req_account_channel_favorite_group_set_t_account_handle_get(JLcom/vivox/service/vx_req_account_channel_favorite_group_set_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_channel_favorite_group_set_t_account_handle_set(JLcom/vivox/service/vx_req_account_channel_favorite_group_set_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_channel_favorite_group_set_t_base_get(JLcom/vivox/service/vx_req_account_channel_favorite_group_set_t;)J
.end method

.method public static final native vx_req_account_channel_favorite_group_set_t_base_set(JLcom/vivox/service/vx_req_account_channel_favorite_group_set_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_account_channel_favorite_group_set_t_channel_favorite_group_data_get(JLcom/vivox/service/vx_req_account_channel_favorite_group_set_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_channel_favorite_group_set_t_channel_favorite_group_data_set(JLcom/vivox/service/vx_req_account_channel_favorite_group_set_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_channel_favorite_group_set_t_channel_favorite_group_id_get(JLcom/vivox/service/vx_req_account_channel_favorite_group_set_t;)I
.end method

.method public static final native vx_req_account_channel_favorite_group_set_t_channel_favorite_group_id_set(JLcom/vivox/service/vx_req_account_channel_favorite_group_set_t;I)V
.end method

.method public static final native vx_req_account_channel_favorite_group_set_t_channel_favorite_group_name_get(JLcom/vivox/service/vx_req_account_channel_favorite_group_set_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_channel_favorite_group_set_t_channel_favorite_group_name_set(JLcom/vivox/service/vx_req_account_channel_favorite_group_set_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_channel_favorite_set_create(J)V
.end method

.method public static final native vx_req_account_channel_favorite_set_t_account_handle_get(JLcom/vivox/service/vx_req_account_channel_favorite_set_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_channel_favorite_set_t_account_handle_set(JLcom/vivox/service/vx_req_account_channel_favorite_set_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_channel_favorite_set_t_base_get(JLcom/vivox/service/vx_req_account_channel_favorite_set_t;)J
.end method

.method public static final native vx_req_account_channel_favorite_set_t_base_set(JLcom/vivox/service/vx_req_account_channel_favorite_set_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_account_channel_favorite_set_t_channel_favorite_data_get(JLcom/vivox/service/vx_req_account_channel_favorite_set_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_channel_favorite_set_t_channel_favorite_data_set(JLcom/vivox/service/vx_req_account_channel_favorite_set_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_channel_favorite_set_t_channel_favorite_group_id_get(JLcom/vivox/service/vx_req_account_channel_favorite_set_t;)I
.end method

.method public static final native vx_req_account_channel_favorite_set_t_channel_favorite_group_id_set(JLcom/vivox/service/vx_req_account_channel_favorite_set_t;I)V
.end method

.method public static final native vx_req_account_channel_favorite_set_t_channel_favorite_id_get(JLcom/vivox/service/vx_req_account_channel_favorite_set_t;)I
.end method

.method public static final native vx_req_account_channel_favorite_set_t_channel_favorite_id_set(JLcom/vivox/service/vx_req_account_channel_favorite_set_t;I)V
.end method

.method public static final native vx_req_account_channel_favorite_set_t_channel_favorite_label_get(JLcom/vivox/service/vx_req_account_channel_favorite_set_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_channel_favorite_set_t_channel_favorite_label_set(JLcom/vivox/service/vx_req_account_channel_favorite_set_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_channel_favorite_set_t_channel_favorite_uri_get(JLcom/vivox/service/vx_req_account_channel_favorite_set_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_channel_favorite_set_t_channel_favorite_uri_set(JLcom/vivox/service/vx_req_account_channel_favorite_set_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_channel_favorites_get_list_create(J)V
.end method

.method public static final native vx_req_account_channel_favorites_get_list_t_account_handle_get(JLcom/vivox/service/vx_req_account_channel_favorites_get_list_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_channel_favorites_get_list_t_account_handle_set(JLcom/vivox/service/vx_req_account_channel_favorites_get_list_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_channel_favorites_get_list_t_base_get(JLcom/vivox/service/vx_req_account_channel_favorites_get_list_t;)J
.end method

.method public static final native vx_req_account_channel_favorites_get_list_t_base_set(JLcom/vivox/service/vx_req_account_channel_favorites_get_list_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_account_channel_get_acl_create(J)V
.end method

.method public static final native vx_req_account_channel_get_acl_t_account_handle_get(JLcom/vivox/service/vx_req_account_channel_get_acl_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_channel_get_acl_t_account_handle_set(JLcom/vivox/service/vx_req_account_channel_get_acl_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_channel_get_acl_t_base_get(JLcom/vivox/service/vx_req_account_channel_get_acl_t;)J
.end method

.method public static final native vx_req_account_channel_get_acl_t_base_set(JLcom/vivox/service/vx_req_account_channel_get_acl_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_account_channel_get_acl_t_channel_uri_get(JLcom/vivox/service/vx_req_account_channel_get_acl_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_channel_get_acl_t_channel_uri_set(JLcom/vivox/service/vx_req_account_channel_get_acl_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_channel_get_info_create(J)V
.end method

.method public static final native vx_req_account_channel_get_info_t_account_handle_get(JLcom/vivox/service/vx_req_account_channel_get_info_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_channel_get_info_t_account_handle_set(JLcom/vivox/service/vx_req_account_channel_get_info_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_channel_get_info_t_base_get(JLcom/vivox/service/vx_req_account_channel_get_info_t;)J
.end method

.method public static final native vx_req_account_channel_get_info_t_base_set(JLcom/vivox/service/vx_req_account_channel_get_info_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_account_channel_get_info_t_channel_uri_get(JLcom/vivox/service/vx_req_account_channel_get_info_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_channel_get_info_t_channel_uri_set(JLcom/vivox/service/vx_req_account_channel_get_info_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_channel_get_moderators_create(J)V
.end method

.method public static final native vx_req_account_channel_get_moderators_t_account_handle_get(JLcom/vivox/service/vx_req_account_channel_get_moderators_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_channel_get_moderators_t_account_handle_set(JLcom/vivox/service/vx_req_account_channel_get_moderators_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_channel_get_moderators_t_base_get(JLcom/vivox/service/vx_req_account_channel_get_moderators_t;)J
.end method

.method public static final native vx_req_account_channel_get_moderators_t_base_set(JLcom/vivox/service/vx_req_account_channel_get_moderators_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_account_channel_get_moderators_t_channel_uri_get(JLcom/vivox/service/vx_req_account_channel_get_moderators_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_channel_get_moderators_t_channel_uri_set(JLcom/vivox/service/vx_req_account_channel_get_moderators_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_channel_get_participants_create(J)V
.end method

.method public static final native vx_req_account_channel_get_participants_t_account_handle_get(JLcom/vivox/service/vx_req_account_channel_get_participants_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_channel_get_participants_t_account_handle_set(JLcom/vivox/service/vx_req_account_channel_get_participants_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_channel_get_participants_t_base_get(JLcom/vivox/service/vx_req_account_channel_get_participants_t;)J
.end method

.method public static final native vx_req_account_channel_get_participants_t_base_set(JLcom/vivox/service/vx_req_account_channel_get_participants_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_account_channel_get_participants_t_channel_uri_get(JLcom/vivox/service/vx_req_account_channel_get_participants_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_channel_get_participants_t_channel_uri_set(JLcom/vivox/service/vx_req_account_channel_get_participants_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_channel_get_participants_t_page_number_get(JLcom/vivox/service/vx_req_account_channel_get_participants_t;)I
.end method

.method public static final native vx_req_account_channel_get_participants_t_page_number_set(JLcom/vivox/service/vx_req_account_channel_get_participants_t;I)V
.end method

.method public static final native vx_req_account_channel_get_participants_t_page_size_get(JLcom/vivox/service/vx_req_account_channel_get_participants_t;)I
.end method

.method public static final native vx_req_account_channel_get_participants_t_page_size_set(JLcom/vivox/service/vx_req_account_channel_get_participants_t;I)V
.end method

.method public static final native vx_req_account_channel_remove_acl_create(J)V
.end method

.method public static final native vx_req_account_channel_remove_acl_t_account_handle_get(JLcom/vivox/service/vx_req_account_channel_remove_acl_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_channel_remove_acl_t_account_handle_set(JLcom/vivox/service/vx_req_account_channel_remove_acl_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_channel_remove_acl_t_acl_uri_get(JLcom/vivox/service/vx_req_account_channel_remove_acl_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_channel_remove_acl_t_acl_uri_set(JLcom/vivox/service/vx_req_account_channel_remove_acl_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_channel_remove_acl_t_base_get(JLcom/vivox/service/vx_req_account_channel_remove_acl_t;)J
.end method

.method public static final native vx_req_account_channel_remove_acl_t_base_set(JLcom/vivox/service/vx_req_account_channel_remove_acl_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_account_channel_remove_acl_t_channel_uri_get(JLcom/vivox/service/vx_req_account_channel_remove_acl_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_channel_remove_acl_t_channel_uri_set(JLcom/vivox/service/vx_req_account_channel_remove_acl_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_channel_remove_moderator_create(J)V
.end method

.method public static final native vx_req_account_channel_remove_moderator_t_account_handle_get(JLcom/vivox/service/vx_req_account_channel_remove_moderator_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_channel_remove_moderator_t_account_handle_set(JLcom/vivox/service/vx_req_account_channel_remove_moderator_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_channel_remove_moderator_t_base_get(JLcom/vivox/service/vx_req_account_channel_remove_moderator_t;)J
.end method

.method public static final native vx_req_account_channel_remove_moderator_t_base_set(JLcom/vivox/service/vx_req_account_channel_remove_moderator_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_account_channel_remove_moderator_t_channel_name_get(JLcom/vivox/service/vx_req_account_channel_remove_moderator_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_channel_remove_moderator_t_channel_name_set(JLcom/vivox/service/vx_req_account_channel_remove_moderator_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_channel_remove_moderator_t_channel_uri_get(JLcom/vivox/service/vx_req_account_channel_remove_moderator_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_channel_remove_moderator_t_channel_uri_set(JLcom/vivox/service/vx_req_account_channel_remove_moderator_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_channel_remove_moderator_t_moderator_uri_get(JLcom/vivox/service/vx_req_account_channel_remove_moderator_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_channel_remove_moderator_t_moderator_uri_set(JLcom/vivox/service/vx_req_account_channel_remove_moderator_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_channel_search_create(J)V
.end method

.method public static final native vx_req_account_channel_search_t_account_handle_get(JLcom/vivox/service/vx_req_account_channel_search_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_channel_search_t_account_handle_set(JLcom/vivox/service/vx_req_account_channel_search_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_channel_search_t_base_get(JLcom/vivox/service/vx_req_account_channel_search_t;)J
.end method

.method public static final native vx_req_account_channel_search_t_base_set(JLcom/vivox/service/vx_req_account_channel_search_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_account_channel_search_t_begins_with_get(JLcom/vivox/service/vx_req_account_channel_search_t;)I
.end method

.method public static final native vx_req_account_channel_search_t_begins_with_set(JLcom/vivox/service/vx_req_account_channel_search_t;I)V
.end method

.method public static final native vx_req_account_channel_search_t_channel_active_get(JLcom/vivox/service/vx_req_account_channel_search_t;)I
.end method

.method public static final native vx_req_account_channel_search_t_channel_active_set(JLcom/vivox/service/vx_req_account_channel_search_t;I)V
.end method

.method public static final native vx_req_account_channel_search_t_channel_description_get(JLcom/vivox/service/vx_req_account_channel_search_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_channel_search_t_channel_description_set(JLcom/vivox/service/vx_req_account_channel_search_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_channel_search_t_channel_name_get(JLcom/vivox/service/vx_req_account_channel_search_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_channel_search_t_channel_name_set(JLcom/vivox/service/vx_req_account_channel_search_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_channel_search_t_channel_type_get(JLcom/vivox/service/vx_req_account_channel_search_t;)I
.end method

.method public static final native vx_req_account_channel_search_t_channel_type_set(JLcom/vivox/service/vx_req_account_channel_search_t;I)V
.end method

.method public static final native vx_req_account_channel_search_t_moderation_type_get(JLcom/vivox/service/vx_req_account_channel_search_t;)I
.end method

.method public static final native vx_req_account_channel_search_t_moderation_type_set(JLcom/vivox/service/vx_req_account_channel_search_t;I)V
.end method

.method public static final native vx_req_account_channel_search_t_page_number_get(JLcom/vivox/service/vx_req_account_channel_search_t;)I
.end method

.method public static final native vx_req_account_channel_search_t_page_number_set(JLcom/vivox/service/vx_req_account_channel_search_t;I)V
.end method

.method public static final native vx_req_account_channel_search_t_page_size_get(JLcom/vivox/service/vx_req_account_channel_search_t;)I
.end method

.method public static final native vx_req_account_channel_search_t_page_size_set(JLcom/vivox/service/vx_req_account_channel_search_t;I)V
.end method

.method public static final native vx_req_account_channel_update_create(J)V
.end method

.method public static final native vx_req_account_channel_update_t_account_handle_get(JLcom/vivox/service/vx_req_account_channel_update_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_channel_update_t_account_handle_set(JLcom/vivox/service/vx_req_account_channel_update_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_channel_update_t_base_get(JLcom/vivox/service/vx_req_account_channel_update_t;)J
.end method

.method public static final native vx_req_account_channel_update_t_base_set(JLcom/vivox/service/vx_req_account_channel_update_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_account_channel_update_t_capacity_get(JLcom/vivox/service/vx_req_account_channel_update_t;)I
.end method

.method public static final native vx_req_account_channel_update_t_capacity_set(JLcom/vivox/service/vx_req_account_channel_update_t;I)V
.end method

.method public static final native vx_req_account_channel_update_t_channel_desc_get(JLcom/vivox/service/vx_req_account_channel_update_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_channel_update_t_channel_desc_set(JLcom/vivox/service/vx_req_account_channel_update_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_channel_update_t_channel_mode_get(JLcom/vivox/service/vx_req_account_channel_update_t;)I
.end method

.method public static final native vx_req_account_channel_update_t_channel_mode_set(JLcom/vivox/service/vx_req_account_channel_update_t;I)V
.end method

.method public static final native vx_req_account_channel_update_t_channel_name_get(JLcom/vivox/service/vx_req_account_channel_update_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_channel_update_t_channel_name_set(JLcom/vivox/service/vx_req_account_channel_update_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_channel_update_t_channel_uri_get(JLcom/vivox/service/vx_req_account_channel_update_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_channel_update_t_channel_uri_set(JLcom/vivox/service/vx_req_account_channel_update_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_channel_update_t_clamping_dist_get(JLcom/vivox/service/vx_req_account_channel_update_t;)I
.end method

.method public static final native vx_req_account_channel_update_t_clamping_dist_set(JLcom/vivox/service/vx_req_account_channel_update_t;I)V
.end method

.method public static final native vx_req_account_channel_update_t_dist_model_get(JLcom/vivox/service/vx_req_account_channel_update_t;)I
.end method

.method public static final native vx_req_account_channel_update_t_dist_model_set(JLcom/vivox/service/vx_req_account_channel_update_t;I)V
.end method

.method public static final native vx_req_account_channel_update_t_encrypt_audio_get(JLcom/vivox/service/vx_req_account_channel_update_t;)I
.end method

.method public static final native vx_req_account_channel_update_t_encrypt_audio_set(JLcom/vivox/service/vx_req_account_channel_update_t;I)V
.end method

.method public static final native vx_req_account_channel_update_t_max_gain_get(JLcom/vivox/service/vx_req_account_channel_update_t;)D
.end method

.method public static final native vx_req_account_channel_update_t_max_gain_set(JLcom/vivox/service/vx_req_account_channel_update_t;D)V
.end method

.method public static final native vx_req_account_channel_update_t_max_participants_get(JLcom/vivox/service/vx_req_account_channel_update_t;)I
.end method

.method public static final native vx_req_account_channel_update_t_max_participants_set(JLcom/vivox/service/vx_req_account_channel_update_t;I)V
.end method

.method public static final native vx_req_account_channel_update_t_max_range_get(JLcom/vivox/service/vx_req_account_channel_update_t;)I
.end method

.method public static final native vx_req_account_channel_update_t_max_range_set(JLcom/vivox/service/vx_req_account_channel_update_t;I)V
.end method

.method public static final native vx_req_account_channel_update_t_protected_password_get(JLcom/vivox/service/vx_req_account_channel_update_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_channel_update_t_protected_password_set(JLcom/vivox/service/vx_req_account_channel_update_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_channel_update_t_roll_off_get(JLcom/vivox/service/vx_req_account_channel_update_t;)D
.end method

.method public static final native vx_req_account_channel_update_t_roll_off_set(JLcom/vivox/service/vx_req_account_channel_update_t;D)V
.end method

.method public static final native vx_req_account_channel_update_t_set_persistent_get(JLcom/vivox/service/vx_req_account_channel_update_t;)I
.end method

.method public static final native vx_req_account_channel_update_t_set_persistent_set(JLcom/vivox/service/vx_req_account_channel_update_t;I)V
.end method

.method public static final native vx_req_account_channel_update_t_set_protected_get(JLcom/vivox/service/vx_req_account_channel_update_t;)I
.end method

.method public static final native vx_req_account_channel_update_t_set_protected_set(JLcom/vivox/service/vx_req_account_channel_update_t;I)V
.end method

.method public static final native vx_req_account_create_auto_accept_rule_create(J)V
.end method

.method public static final native vx_req_account_create_auto_accept_rule_t_account_handle_get(JLcom/vivox/service/vx_req_account_create_auto_accept_rule_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_create_auto_accept_rule_t_account_handle_set(JLcom/vivox/service/vx_req_account_create_auto_accept_rule_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_create_auto_accept_rule_t_auto_accept_mask_get(JLcom/vivox/service/vx_req_account_create_auto_accept_rule_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_create_auto_accept_rule_t_auto_accept_mask_set(JLcom/vivox/service/vx_req_account_create_auto_accept_rule_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_create_auto_accept_rule_t_auto_accept_nickname_get(JLcom/vivox/service/vx_req_account_create_auto_accept_rule_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_create_auto_accept_rule_t_auto_accept_nickname_set(JLcom/vivox/service/vx_req_account_create_auto_accept_rule_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_create_auto_accept_rule_t_auto_add_as_buddy_get(JLcom/vivox/service/vx_req_account_create_auto_accept_rule_t;)I
.end method

.method public static final native vx_req_account_create_auto_accept_rule_t_auto_add_as_buddy_set(JLcom/vivox/service/vx_req_account_create_auto_accept_rule_t;I)V
.end method

.method public static final native vx_req_account_create_auto_accept_rule_t_base_get(JLcom/vivox/service/vx_req_account_create_auto_accept_rule_t;)J
.end method

.method public static final native vx_req_account_create_auto_accept_rule_t_base_set(JLcom/vivox/service/vx_req_account_create_auto_accept_rule_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_account_create_block_rule_create(J)V
.end method

.method public static final native vx_req_account_create_block_rule_t_account_handle_get(JLcom/vivox/service/vx_req_account_create_block_rule_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_create_block_rule_t_account_handle_set(JLcom/vivox/service/vx_req_account_create_block_rule_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_create_block_rule_t_base_get(JLcom/vivox/service/vx_req_account_create_block_rule_t;)J
.end method

.method public static final native vx_req_account_create_block_rule_t_base_set(JLcom/vivox/service/vx_req_account_create_block_rule_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_account_create_block_rule_t_block_mask_get(JLcom/vivox/service/vx_req_account_create_block_rule_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_create_block_rule_t_block_mask_set(JLcom/vivox/service/vx_req_account_create_block_rule_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_create_block_rule_t_presence_only_get(JLcom/vivox/service/vx_req_account_create_block_rule_t;)I
.end method

.method public static final native vx_req_account_create_block_rule_t_presence_only_set(JLcom/vivox/service/vx_req_account_create_block_rule_t;I)V
.end method

.method public static final native vx_req_account_delete_auto_accept_rule_create(J)V
.end method

.method public static final native vx_req_account_delete_auto_accept_rule_t_account_handle_get(JLcom/vivox/service/vx_req_account_delete_auto_accept_rule_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_delete_auto_accept_rule_t_account_handle_set(JLcom/vivox/service/vx_req_account_delete_auto_accept_rule_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_delete_auto_accept_rule_t_auto_accept_mask_get(JLcom/vivox/service/vx_req_account_delete_auto_accept_rule_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_delete_auto_accept_rule_t_auto_accept_mask_set(JLcom/vivox/service/vx_req_account_delete_auto_accept_rule_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_delete_auto_accept_rule_t_base_get(JLcom/vivox/service/vx_req_account_delete_auto_accept_rule_t;)J
.end method

.method public static final native vx_req_account_delete_auto_accept_rule_t_base_set(JLcom/vivox/service/vx_req_account_delete_auto_accept_rule_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_account_delete_block_rule_create(J)V
.end method

.method public static final native vx_req_account_delete_block_rule_t_account_handle_get(JLcom/vivox/service/vx_req_account_delete_block_rule_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_delete_block_rule_t_account_handle_set(JLcom/vivox/service/vx_req_account_delete_block_rule_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_delete_block_rule_t_base_get(JLcom/vivox/service/vx_req_account_delete_block_rule_t;)J
.end method

.method public static final native vx_req_account_delete_block_rule_t_base_set(JLcom/vivox/service/vx_req_account_delete_block_rule_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_account_delete_block_rule_t_block_mask_get(JLcom/vivox/service/vx_req_account_delete_block_rule_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_delete_block_rule_t_block_mask_set(JLcom/vivox/service/vx_req_account_delete_block_rule_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_get_account_create(J)V
.end method

.method public static final native vx_req_account_get_account_t_account_handle_get(JLcom/vivox/service/vx_req_account_get_account_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_get_account_t_account_handle_set(JLcom/vivox/service/vx_req_account_get_account_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_get_account_t_base_get(JLcom/vivox/service/vx_req_account_get_account_t;)J
.end method

.method public static final native vx_req_account_get_account_t_base_set(JLcom/vivox/service/vx_req_account_get_account_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_account_get_account_t_uri_get(JLcom/vivox/service/vx_req_account_get_account_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_get_account_t_uri_set(JLcom/vivox/service/vx_req_account_get_account_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_get_session_fonts_create(J)V
.end method

.method public static final native vx_req_account_get_session_fonts_t_account_handle_get(JLcom/vivox/service/vx_req_account_get_session_fonts_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_get_session_fonts_t_account_handle_set(JLcom/vivox/service/vx_req_account_get_session_fonts_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_get_session_fonts_t_base_get(JLcom/vivox/service/vx_req_account_get_session_fonts_t;)J
.end method

.method public static final native vx_req_account_get_session_fonts_t_base_set(JLcom/vivox/service/vx_req_account_get_session_fonts_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_account_get_template_fonts_create(J)V
.end method

.method public static final native vx_req_account_get_template_fonts_t_account_handle_get(JLcom/vivox/service/vx_req_account_get_template_fonts_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_get_template_fonts_t_account_handle_set(JLcom/vivox/service/vx_req_account_get_template_fonts_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_get_template_fonts_t_base_get(JLcom/vivox/service/vx_req_account_get_template_fonts_t;)J
.end method

.method public static final native vx_req_account_get_template_fonts_t_base_set(JLcom/vivox/service/vx_req_account_get_template_fonts_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_account_list_auto_accept_rules_create(J)V
.end method

.method public static final native vx_req_account_list_auto_accept_rules_t_account_handle_get(JLcom/vivox/service/vx_req_account_list_auto_accept_rules_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_list_auto_accept_rules_t_account_handle_set(JLcom/vivox/service/vx_req_account_list_auto_accept_rules_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_list_auto_accept_rules_t_base_get(JLcom/vivox/service/vx_req_account_list_auto_accept_rules_t;)J
.end method

.method public static final native vx_req_account_list_auto_accept_rules_t_base_set(JLcom/vivox/service/vx_req_account_list_auto_accept_rules_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_account_list_block_rules_create(J)V
.end method

.method public static final native vx_req_account_list_block_rules_t_account_handle_get(JLcom/vivox/service/vx_req_account_list_block_rules_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_list_block_rules_t_account_handle_set(JLcom/vivox/service/vx_req_account_list_block_rules_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_list_block_rules_t_base_get(JLcom/vivox/service/vx_req_account_list_block_rules_t;)J
.end method

.method public static final native vx_req_account_list_block_rules_t_base_set(JLcom/vivox/service/vx_req_account_list_block_rules_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_account_list_buddies_and_groups_create(J)V
.end method

.method public static final native vx_req_account_list_buddies_and_groups_t_account_handle_get(JLcom/vivox/service/vx_req_account_list_buddies_and_groups_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_list_buddies_and_groups_t_account_handle_set(JLcom/vivox/service/vx_req_account_list_buddies_and_groups_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_list_buddies_and_groups_t_base_get(JLcom/vivox/service/vx_req_account_list_buddies_and_groups_t;)J
.end method

.method public static final native vx_req_account_list_buddies_and_groups_t_base_set(JLcom/vivox/service/vx_req_account_list_buddies_and_groups_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_account_login_create(J)V
.end method

.method public static final native vx_req_account_login_t_acct_mgmt_server_get(JLcom/vivox/service/vx_req_account_login_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_login_t_acct_mgmt_server_set(JLcom/vivox/service/vx_req_account_login_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_login_t_acct_name_get(JLcom/vivox/service/vx_req_account_login_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_login_t_acct_name_set(JLcom/vivox/service/vx_req_account_login_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_login_t_acct_password_get(JLcom/vivox/service/vx_req_account_login_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_login_t_acct_password_set(JLcom/vivox/service/vx_req_account_login_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_login_t_answer_mode_get(JLcom/vivox/service/vx_req_account_login_t;)I
.end method

.method public static final native vx_req_account_login_t_answer_mode_set(JLcom/vivox/service/vx_req_account_login_t;I)V
.end method

.method public static final native vx_req_account_login_t_application_override_get(JLcom/vivox/service/vx_req_account_login_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_login_t_application_override_set(JLcom/vivox/service/vx_req_account_login_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_login_t_application_token_get(JLcom/vivox/service/vx_req_account_login_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_login_t_application_token_set(JLcom/vivox/service/vx_req_account_login_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_login_t_autopost_crash_dumps_get(JLcom/vivox/service/vx_req_account_login_t;)I
.end method

.method public static final native vx_req_account_login_t_autopost_crash_dumps_set(JLcom/vivox/service/vx_req_account_login_t;I)V
.end method

.method public static final native vx_req_account_login_t_base_get(JLcom/vivox/service/vx_req_account_login_t;)J
.end method

.method public static final native vx_req_account_login_t_base_set(JLcom/vivox/service/vx_req_account_login_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_account_login_t_buddy_management_mode_get(JLcom/vivox/service/vx_req_account_login_t;)I
.end method

.method public static final native vx_req_account_login_t_buddy_management_mode_set(JLcom/vivox/service/vx_req_account_login_t;I)V
.end method

.method public static final native vx_req_account_login_t_connector_handle_get(JLcom/vivox/service/vx_req_account_login_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_login_t_connector_handle_set(JLcom/vivox/service/vx_req_account_login_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_login_t_enable_buddies_and_presence_get(JLcom/vivox/service/vx_req_account_login_t;)I
.end method

.method public static final native vx_req_account_login_t_enable_buddies_and_presence_set(JLcom/vivox/service/vx_req_account_login_t;I)V
.end method

.method public static final native vx_req_account_login_t_enable_client_ringback_get(JLcom/vivox/service/vx_req_account_login_t;)I
.end method

.method public static final native vx_req_account_login_t_enable_client_ringback_set(JLcom/vivox/service/vx_req_account_login_t;I)V
.end method

.method public static final native vx_req_account_login_t_enable_text_get(JLcom/vivox/service/vx_req_account_login_t;)I
.end method

.method public static final native vx_req_account_login_t_enable_text_set(JLcom/vivox/service/vx_req_account_login_t;I)V
.end method

.method public static final native vx_req_account_login_t_participant_property_frequency_get(JLcom/vivox/service/vx_req_account_login_t;)I
.end method

.method public static final native vx_req_account_login_t_participant_property_frequency_set(JLcom/vivox/service/vx_req_account_login_t;I)V
.end method

.method public static final native vx_req_account_logout_create(J)V
.end method

.method public static final native vx_req_account_logout_t_account_handle_get(JLcom/vivox/service/vx_req_account_logout_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_logout_t_account_handle_set(JLcom/vivox/service/vx_req_account_logout_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_logout_t_base_get(JLcom/vivox/service/vx_req_account_logout_t;)J
.end method

.method public static final native vx_req_account_logout_t_base_set(JLcom/vivox/service/vx_req_account_logout_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_account_logout_t_logout_reason_get(JLcom/vivox/service/vx_req_account_logout_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_logout_t_logout_reason_set(JLcom/vivox/service/vx_req_account_logout_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_post_crash_dump_create(J)V
.end method

.method public static final native vx_req_account_post_crash_dump_t_account_handle_get(JLcom/vivox/service/vx_req_account_post_crash_dump_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_post_crash_dump_t_account_handle_set(JLcom/vivox/service/vx_req_account_post_crash_dump_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_post_crash_dump_t_base_get(JLcom/vivox/service/vx_req_account_post_crash_dump_t;)J
.end method

.method public static final native vx_req_account_post_crash_dump_t_base_set(JLcom/vivox/service/vx_req_account_post_crash_dump_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_account_post_crash_dump_t_crash_dump_get(JLcom/vivox/service/vx_req_account_post_crash_dump_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_post_crash_dump_t_crash_dump_set(JLcom/vivox/service/vx_req_account_post_crash_dump_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_send_message_create(J)V
.end method

.method public static final native vx_req_account_send_message_t_account_handle_get(JLcom/vivox/service/vx_req_account_send_message_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_send_message_t_account_handle_set(JLcom/vivox/service/vx_req_account_send_message_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_send_message_t_alias_username_get(JLcom/vivox/service/vx_req_account_send_message_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_send_message_t_alias_username_set(JLcom/vivox/service/vx_req_account_send_message_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_send_message_t_base_get(JLcom/vivox/service/vx_req_account_send_message_t;)J
.end method

.method public static final native vx_req_account_send_message_t_base_set(JLcom/vivox/service/vx_req_account_send_message_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_account_send_message_t_message_body_get(JLcom/vivox/service/vx_req_account_send_message_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_send_message_t_message_body_set(JLcom/vivox/service/vx_req_account_send_message_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_send_message_t_message_header_get(JLcom/vivox/service/vx_req_account_send_message_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_send_message_t_message_header_set(JLcom/vivox/service/vx_req_account_send_message_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_send_message_t_user_uri_get(JLcom/vivox/service/vx_req_account_send_message_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_send_message_t_user_uri_set(JLcom/vivox/service/vx_req_account_send_message_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_send_sms_create(J)V
.end method

.method public static final native vx_req_account_send_sms_t_account_handle_get(JLcom/vivox/service/vx_req_account_send_sms_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_send_sms_t_account_handle_set(JLcom/vivox/service/vx_req_account_send_sms_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_send_sms_t_base_get(JLcom/vivox/service/vx_req_account_send_sms_t;)J
.end method

.method public static final native vx_req_account_send_sms_t_base_set(JLcom/vivox/service/vx_req_account_send_sms_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_account_send_sms_t_content_get(JLcom/vivox/service/vx_req_account_send_sms_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_send_sms_t_content_set(JLcom/vivox/service/vx_req_account_send_sms_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_send_sms_t_recipient_uri_get(JLcom/vivox/service/vx_req_account_send_sms_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_send_sms_t_recipient_uri_set(JLcom/vivox/service/vx_req_account_send_sms_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_send_subscription_reply_create(J)V
.end method

.method public static final native vx_req_account_send_subscription_reply_t_account_handle_get(JLcom/vivox/service/vx_req_account_send_subscription_reply_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_send_subscription_reply_t_account_handle_set(JLcom/vivox/service/vx_req_account_send_subscription_reply_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_send_subscription_reply_t_auto_accept_get(JLcom/vivox/service/vx_req_account_send_subscription_reply_t;)I
.end method

.method public static final native vx_req_account_send_subscription_reply_t_auto_accept_set(JLcom/vivox/service/vx_req_account_send_subscription_reply_t;I)V
.end method

.method public static final native vx_req_account_send_subscription_reply_t_base_get(JLcom/vivox/service/vx_req_account_send_subscription_reply_t;)J
.end method

.method public static final native vx_req_account_send_subscription_reply_t_base_set(JLcom/vivox/service/vx_req_account_send_subscription_reply_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_account_send_subscription_reply_t_buddy_uri_get(JLcom/vivox/service/vx_req_account_send_subscription_reply_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_send_subscription_reply_t_buddy_uri_set(JLcom/vivox/service/vx_req_account_send_subscription_reply_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_send_subscription_reply_t_rule_type_get(JLcom/vivox/service/vx_req_account_send_subscription_reply_t;)I
.end method

.method public static final native vx_req_account_send_subscription_reply_t_rule_type_set(JLcom/vivox/service/vx_req_account_send_subscription_reply_t;I)V
.end method

.method public static final native vx_req_account_send_subscription_reply_t_subscription_handle_get(JLcom/vivox/service/vx_req_account_send_subscription_reply_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_send_subscription_reply_t_subscription_handle_set(JLcom/vivox/service/vx_req_account_send_subscription_reply_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_send_user_app_data_create(J)V
.end method

.method public static final native vx_req_account_send_user_app_data_t_account_handle_get(JLcom/vivox/service/vx_req_account_send_user_app_data_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_send_user_app_data_t_account_handle_set(JLcom/vivox/service/vx_req_account_send_user_app_data_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_send_user_app_data_t_base_get(JLcom/vivox/service/vx_req_account_send_user_app_data_t;)J
.end method

.method public static final native vx_req_account_send_user_app_data_t_base_set(JLcom/vivox/service/vx_req_account_send_user_app_data_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_account_send_user_app_data_t_content_get(JLcom/vivox/service/vx_req_account_send_user_app_data_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_send_user_app_data_t_content_set(JLcom/vivox/service/vx_req_account_send_user_app_data_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_send_user_app_data_t_content_type_get(JLcom/vivox/service/vx_req_account_send_user_app_data_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_send_user_app_data_t_content_type_set(JLcom/vivox/service/vx_req_account_send_user_app_data_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_send_user_app_data_t_to_uri_get(JLcom/vivox/service/vx_req_account_send_user_app_data_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_send_user_app_data_t_to_uri_set(JLcom/vivox/service/vx_req_account_send_user_app_data_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_set_login_properties_create(J)V
.end method

.method public static final native vx_req_account_set_login_properties_t_account_handle_get(JLcom/vivox/service/vx_req_account_set_login_properties_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_set_login_properties_t_account_handle_set(JLcom/vivox/service/vx_req_account_set_login_properties_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_set_login_properties_t_answer_mode_get(JLcom/vivox/service/vx_req_account_set_login_properties_t;)I
.end method

.method public static final native vx_req_account_set_login_properties_t_answer_mode_set(JLcom/vivox/service/vx_req_account_set_login_properties_t;I)V
.end method

.method public static final native vx_req_account_set_login_properties_t_base_get(JLcom/vivox/service/vx_req_account_set_login_properties_t;)J
.end method

.method public static final native vx_req_account_set_login_properties_t_base_set(JLcom/vivox/service/vx_req_account_set_login_properties_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_account_set_login_properties_t_participant_property_frequency_get(JLcom/vivox/service/vx_req_account_set_login_properties_t;)I
.end method

.method public static final native vx_req_account_set_login_properties_t_participant_property_frequency_set(JLcom/vivox/service/vx_req_account_set_login_properties_t;I)V
.end method

.method public static final native vx_req_account_set_presence_create(J)V
.end method

.method public static final native vx_req_account_set_presence_t_account_handle_get(JLcom/vivox/service/vx_req_account_set_presence_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_set_presence_t_account_handle_set(JLcom/vivox/service/vx_req_account_set_presence_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_set_presence_t_base_get(JLcom/vivox/service/vx_req_account_set_presence_t;)J
.end method

.method public static final native vx_req_account_set_presence_t_base_set(JLcom/vivox/service/vx_req_account_set_presence_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_account_set_presence_t_custom_message_get(JLcom/vivox/service/vx_req_account_set_presence_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_set_presence_t_custom_message_set(JLcom/vivox/service/vx_req_account_set_presence_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_set_presence_t_presence_get(JLcom/vivox/service/vx_req_account_set_presence_t;)I
.end method

.method public static final native vx_req_account_set_presence_t_presence_set(JLcom/vivox/service/vx_req_account_set_presence_t;I)V
.end method

.method public static final native vx_req_account_update_account_create(J)V
.end method

.method public static final native vx_req_account_update_account_t_account_handle_get(JLcom/vivox/service/vx_req_account_update_account_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_update_account_t_account_handle_set(JLcom/vivox/service/vx_req_account_update_account_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_update_account_t_base_get(JLcom/vivox/service/vx_req_account_update_account_t;)J
.end method

.method public static final native vx_req_account_update_account_t_base_set(JLcom/vivox/service/vx_req_account_update_account_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_account_update_account_t_displayname_get(JLcom/vivox/service/vx_req_account_update_account_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_update_account_t_displayname_set(JLcom/vivox/service/vx_req_account_update_account_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_web_call_create(J)V
.end method

.method public static final native vx_req_account_web_call_t_account_handle_get(JLcom/vivox/service/vx_req_account_web_call_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_web_call_t_account_handle_set(JLcom/vivox/service/vx_req_account_web_call_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_account_web_call_t_base_get(JLcom/vivox/service/vx_req_account_web_call_t;)J
.end method

.method public static final native vx_req_account_web_call_t_base_set(JLcom/vivox/service/vx_req_account_web_call_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_account_web_call_t_parameter_count_get(JLcom/vivox/service/vx_req_account_web_call_t;)I
.end method

.method public static final native vx_req_account_web_call_t_parameter_count_set(JLcom/vivox/service/vx_req_account_web_call_t;I)V
.end method

.method public static final native vx_req_account_web_call_t_parameters_get(JLcom/vivox/service/vx_req_account_web_call_t;)J
.end method

.method public static final native vx_req_account_web_call_t_parameters_set(JLcom/vivox/service/vx_req_account_web_call_t;J)V
.end method

.method public static final native vx_req_account_web_call_t_relative_path_get(JLcom/vivox/service/vx_req_account_web_call_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_account_web_call_t_relative_path_set(JLcom/vivox/service/vx_req_account_web_call_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_aux_capture_audio_start_create(J)V
.end method

.method public static final native vx_req_aux_capture_audio_start_t_base_get(JLcom/vivox/service/vx_req_aux_capture_audio_start_t;)J
.end method

.method public static final native vx_req_aux_capture_audio_start_t_base_set(JLcom/vivox/service/vx_req_aux_capture_audio_start_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_aux_capture_audio_start_t_duration_get(JLcom/vivox/service/vx_req_aux_capture_audio_start_t;)I
.end method

.method public static final native vx_req_aux_capture_audio_start_t_duration_set(JLcom/vivox/service/vx_req_aux_capture_audio_start_t;I)V
.end method

.method public static final native vx_req_aux_capture_audio_stop_create(J)V
.end method

.method public static final native vx_req_aux_capture_audio_stop_t_base_get(JLcom/vivox/service/vx_req_aux_capture_audio_stop_t;)J
.end method

.method public static final native vx_req_aux_capture_audio_stop_t_base_set(JLcom/vivox/service/vx_req_aux_capture_audio_stop_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_aux_connectivity_info_create(J)V
.end method

.method public static final native vx_req_aux_connectivity_info_t_acct_mgmt_server_get(JLcom/vivox/service/vx_req_aux_connectivity_info_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_aux_connectivity_info_t_acct_mgmt_server_set(JLcom/vivox/service/vx_req_aux_connectivity_info_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_aux_connectivity_info_t_base_get(JLcom/vivox/service/vx_req_aux_connectivity_info_t;)J
.end method

.method public static final native vx_req_aux_connectivity_info_t_base_set(JLcom/vivox/service/vx_req_aux_connectivity_info_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_aux_connectivity_info_t_echo_port_get(JLcom/vivox/service/vx_req_aux_connectivity_info_t;)I
.end method

.method public static final native vx_req_aux_connectivity_info_t_echo_port_set(JLcom/vivox/service/vx_req_aux_connectivity_info_t;I)V
.end method

.method public static final native vx_req_aux_connectivity_info_t_echo_server_get(JLcom/vivox/service/vx_req_aux_connectivity_info_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_aux_connectivity_info_t_echo_server_set(JLcom/vivox/service/vx_req_aux_connectivity_info_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_aux_connectivity_info_t_stun_server_get(JLcom/vivox/service/vx_req_aux_connectivity_info_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_aux_connectivity_info_t_stun_server_set(JLcom/vivox/service/vx_req_aux_connectivity_info_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_aux_connectivity_info_t_timeout_get(JLcom/vivox/service/vx_req_aux_connectivity_info_t;)I
.end method

.method public static final native vx_req_aux_connectivity_info_t_timeout_set(JLcom/vivox/service/vx_req_aux_connectivity_info_t;I)V
.end method

.method public static final native vx_req_aux_connectivity_info_t_well_known_ip_get(JLcom/vivox/service/vx_req_aux_connectivity_info_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_aux_connectivity_info_t_well_known_ip_set(JLcom/vivox/service/vx_req_aux_connectivity_info_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_aux_create_account_create(J)V
.end method

.method public static final native vx_req_aux_create_account_t_age_get(JLcom/vivox/service/vx_req_aux_create_account_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_aux_create_account_t_age_set(JLcom/vivox/service/vx_req_aux_create_account_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_aux_create_account_t_base_get(JLcom/vivox/service/vx_req_aux_create_account_t;)J
.end method

.method public static final native vx_req_aux_create_account_t_base_set(JLcom/vivox/service/vx_req_aux_create_account_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_aux_create_account_t_credentials_get(JLcom/vivox/service/vx_req_aux_create_account_t;)J
.end method

.method public static final native vx_req_aux_create_account_t_credentials_set(JLcom/vivox/service/vx_req_aux_create_account_t;JLcom/vivox/service/vx_generic_credentials;)V
.end method

.method public static final native vx_req_aux_create_account_t_displayname_get(JLcom/vivox/service/vx_req_aux_create_account_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_aux_create_account_t_displayname_set(JLcom/vivox/service/vx_req_aux_create_account_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_aux_create_account_t_email_get(JLcom/vivox/service/vx_req_aux_create_account_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_aux_create_account_t_email_set(JLcom/vivox/service/vx_req_aux_create_account_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_aux_create_account_t_ext_id_get(JLcom/vivox/service/vx_req_aux_create_account_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_aux_create_account_t_ext_id_set(JLcom/vivox/service/vx_req_aux_create_account_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_aux_create_account_t_ext_profile_get(JLcom/vivox/service/vx_req_aux_create_account_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_aux_create_account_t_ext_profile_set(JLcom/vivox/service/vx_req_aux_create_account_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_aux_create_account_t_firstname_get(JLcom/vivox/service/vx_req_aux_create_account_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_aux_create_account_t_firstname_set(JLcom/vivox/service/vx_req_aux_create_account_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_aux_create_account_t_gender_get(JLcom/vivox/service/vx_req_aux_create_account_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_aux_create_account_t_gender_set(JLcom/vivox/service/vx_req_aux_create_account_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_aux_create_account_t_lang_get(JLcom/vivox/service/vx_req_aux_create_account_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_aux_create_account_t_lang_set(JLcom/vivox/service/vx_req_aux_create_account_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_aux_create_account_t_lastname_get(JLcom/vivox/service/vx_req_aux_create_account_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_aux_create_account_t_lastname_set(JLcom/vivox/service/vx_req_aux_create_account_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_aux_create_account_t_number_get(JLcom/vivox/service/vx_req_aux_create_account_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_aux_create_account_t_number_set(JLcom/vivox/service/vx_req_aux_create_account_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_aux_create_account_t_password_get(JLcom/vivox/service/vx_req_aux_create_account_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_aux_create_account_t_password_set(JLcom/vivox/service/vx_req_aux_create_account_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_aux_create_account_t_phone_get(JLcom/vivox/service/vx_req_aux_create_account_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_aux_create_account_t_phone_set(JLcom/vivox/service/vx_req_aux_create_account_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_aux_create_account_t_timezone_get(JLcom/vivox/service/vx_req_aux_create_account_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_aux_create_account_t_timezone_set(JLcom/vivox/service/vx_req_aux_create_account_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_aux_create_account_t_user_name_get(JLcom/vivox/service/vx_req_aux_create_account_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_aux_create_account_t_user_name_set(JLcom/vivox/service/vx_req_aux_create_account_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_aux_deactivate_account_create(J)V
.end method

.method public static final native vx_req_aux_deactivate_account_t_base_get(JLcom/vivox/service/vx_req_aux_deactivate_account_t;)J
.end method

.method public static final native vx_req_aux_deactivate_account_t_base_set(JLcom/vivox/service/vx_req_aux_deactivate_account_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_aux_deactivate_account_t_credentials_get(JLcom/vivox/service/vx_req_aux_deactivate_account_t;)J
.end method

.method public static final native vx_req_aux_deactivate_account_t_credentials_set(JLcom/vivox/service/vx_req_aux_deactivate_account_t;JLcom/vivox/service/vx_generic_credentials;)V
.end method

.method public static final native vx_req_aux_deactivate_account_t_user_name_get(JLcom/vivox/service/vx_req_aux_deactivate_account_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_aux_deactivate_account_t_user_name_set(JLcom/vivox/service/vx_req_aux_deactivate_account_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_aux_diagnostic_state_dump_create(J)V
.end method

.method public static final native vx_req_aux_diagnostic_state_dump_t_base_get(JLcom/vivox/service/vx_req_aux_diagnostic_state_dump_t;)J
.end method

.method public static final native vx_req_aux_diagnostic_state_dump_t_base_set(JLcom/vivox/service/vx_req_aux_diagnostic_state_dump_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_aux_diagnostic_state_dump_t_level_get(JLcom/vivox/service/vx_req_aux_diagnostic_state_dump_t;)I
.end method

.method public static final native vx_req_aux_diagnostic_state_dump_t_level_set(JLcom/vivox/service/vx_req_aux_diagnostic_state_dump_t;I)V
.end method

.method public static final native vx_req_aux_get_capture_devices_create(J)V
.end method

.method public static final native vx_req_aux_get_capture_devices_t_base_get(JLcom/vivox/service/vx_req_aux_get_capture_devices_t;)J
.end method

.method public static final native vx_req_aux_get_capture_devices_t_base_set(JLcom/vivox/service/vx_req_aux_get_capture_devices_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_aux_get_mic_level_create(J)V
.end method

.method public static final native vx_req_aux_get_mic_level_t_base_get(JLcom/vivox/service/vx_req_aux_get_mic_level_t;)J
.end method

.method public static final native vx_req_aux_get_mic_level_t_base_set(JLcom/vivox/service/vx_req_aux_get_mic_level_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_aux_get_render_devices_create(J)V
.end method

.method public static final native vx_req_aux_get_render_devices_t_base_get(JLcom/vivox/service/vx_req_aux_get_render_devices_t;)J
.end method

.method public static final native vx_req_aux_get_render_devices_t_base_set(JLcom/vivox/service/vx_req_aux_get_render_devices_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_aux_get_speaker_level_create(J)V
.end method

.method public static final native vx_req_aux_get_speaker_level_t_base_get(JLcom/vivox/service/vx_req_aux_get_speaker_level_t;)J
.end method

.method public static final native vx_req_aux_get_speaker_level_t_base_set(JLcom/vivox/service/vx_req_aux_get_speaker_level_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_aux_get_vad_properties_create(J)V
.end method

.method public static final native vx_req_aux_get_vad_properties_t_base_get(JLcom/vivox/service/vx_req_aux_get_vad_properties_t;)J
.end method

.method public static final native vx_req_aux_get_vad_properties_t_base_set(JLcom/vivox/service/vx_req_aux_get_vad_properties_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_aux_global_monitor_keyboard_mouse_create(J)V
.end method

.method public static final native vx_req_aux_global_monitor_keyboard_mouse_t_base_get(JLcom/vivox/service/vx_req_aux_global_monitor_keyboard_mouse_t;)J
.end method

.method public static final native vx_req_aux_global_monitor_keyboard_mouse_t_base_set(JLcom/vivox/service/vx_req_aux_global_monitor_keyboard_mouse_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_aux_global_monitor_keyboard_mouse_t_code_count_get(JLcom/vivox/service/vx_req_aux_global_monitor_keyboard_mouse_t;)I
.end method

.method public static final native vx_req_aux_global_monitor_keyboard_mouse_t_code_count_set(JLcom/vivox/service/vx_req_aux_global_monitor_keyboard_mouse_t;I)V
.end method

.method public static final native vx_req_aux_global_monitor_keyboard_mouse_t_codes_get(JLcom/vivox/service/vx_req_aux_global_monitor_keyboard_mouse_t;)J
.end method

.method public static final native vx_req_aux_global_monitor_keyboard_mouse_t_codes_set(JLcom/vivox/service/vx_req_aux_global_monitor_keyboard_mouse_t;J)V
.end method

.method public static final native vx_req_aux_global_monitor_keyboard_mouse_t_name_get(JLcom/vivox/service/vx_req_aux_global_monitor_keyboard_mouse_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_aux_global_monitor_keyboard_mouse_t_name_set(JLcom/vivox/service/vx_req_aux_global_monitor_keyboard_mouse_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_aux_global_monitor_keyboard_mouse_t_set_codes_item(JLcom/vivox/service/vx_req_aux_global_monitor_keyboard_mouse_t;II)V
.end method

.method public static final native vx_req_aux_notify_application_state_change_create(J)V
.end method

.method public static final native vx_req_aux_notify_application_state_change_t_base_get(JLcom/vivox/service/vx_req_aux_notify_application_state_change_t;)J
.end method

.method public static final native vx_req_aux_notify_application_state_change_t_base_set(JLcom/vivox/service/vx_req_aux_notify_application_state_change_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_aux_notify_application_state_change_t_notification_type_get(JLcom/vivox/service/vx_req_aux_notify_application_state_change_t;)I
.end method

.method public static final native vx_req_aux_notify_application_state_change_t_notification_type_set(JLcom/vivox/service/vx_req_aux_notify_application_state_change_t;I)V
.end method

.method public static final native vx_req_aux_play_audio_buffer_create(J)V
.end method

.method public static final native vx_req_aux_play_audio_buffer_t_account_handle_get(JLcom/vivox/service/vx_req_aux_play_audio_buffer_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_aux_play_audio_buffer_t_account_handle_set(JLcom/vivox/service/vx_req_aux_play_audio_buffer_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_aux_play_audio_buffer_t_base_get(JLcom/vivox/service/vx_req_aux_play_audio_buffer_t;)J
.end method

.method public static final native vx_req_aux_play_audio_buffer_t_base_set(JLcom/vivox/service/vx_req_aux_play_audio_buffer_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_aux_play_audio_buffer_t_font_delta_get(JLcom/vivox/service/vx_req_aux_play_audio_buffer_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_aux_play_audio_buffer_t_font_delta_set(JLcom/vivox/service/vx_req_aux_play_audio_buffer_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_aux_play_audio_buffer_t_template_font_id_get(JLcom/vivox/service/vx_req_aux_play_audio_buffer_t;)I
.end method

.method public static final native vx_req_aux_play_audio_buffer_t_template_font_id_set(JLcom/vivox/service/vx_req_aux_play_audio_buffer_t;I)V
.end method

.method public static final native vx_req_aux_reactivate_account_create(J)V
.end method

.method public static final native vx_req_aux_reactivate_account_t_base_get(JLcom/vivox/service/vx_req_aux_reactivate_account_t;)J
.end method

.method public static final native vx_req_aux_reactivate_account_t_base_set(JLcom/vivox/service/vx_req_aux_reactivate_account_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_aux_reactivate_account_t_credentials_get(JLcom/vivox/service/vx_req_aux_reactivate_account_t;)J
.end method

.method public static final native vx_req_aux_reactivate_account_t_credentials_set(JLcom/vivox/service/vx_req_aux_reactivate_account_t;JLcom/vivox/service/vx_generic_credentials;)V
.end method

.method public static final native vx_req_aux_reactivate_account_t_user_name_get(JLcom/vivox/service/vx_req_aux_reactivate_account_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_aux_reactivate_account_t_user_name_set(JLcom/vivox/service/vx_req_aux_reactivate_account_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_aux_render_audio_modify_create(J)V
.end method

.method public static final native vx_req_aux_render_audio_modify_t_base_get(JLcom/vivox/service/vx_req_aux_render_audio_modify_t;)J
.end method

.method public static final native vx_req_aux_render_audio_modify_t_base_set(JLcom/vivox/service/vx_req_aux_render_audio_modify_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_aux_render_audio_modify_t_font_str_get(JLcom/vivox/service/vx_req_aux_render_audio_modify_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_aux_render_audio_modify_t_font_str_set(JLcom/vivox/service/vx_req_aux_render_audio_modify_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_aux_render_audio_start_create(J)V
.end method

.method public static final native vx_req_aux_render_audio_start_t_base_get(JLcom/vivox/service/vx_req_aux_render_audio_start_t;)J
.end method

.method public static final native vx_req_aux_render_audio_start_t_base_set(JLcom/vivox/service/vx_req_aux_render_audio_start_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_aux_render_audio_start_t_loop_get(JLcom/vivox/service/vx_req_aux_render_audio_start_t;)I
.end method

.method public static final native vx_req_aux_render_audio_start_t_loop_set(JLcom/vivox/service/vx_req_aux_render_audio_start_t;I)V
.end method

.method public static final native vx_req_aux_render_audio_start_t_path_get(JLcom/vivox/service/vx_req_aux_render_audio_start_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_aux_render_audio_start_t_path_set(JLcom/vivox/service/vx_req_aux_render_audio_start_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_aux_render_audio_start_t_sound_file_path_get(JLcom/vivox/service/vx_req_aux_render_audio_start_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_aux_render_audio_start_t_sound_file_path_set(JLcom/vivox/service/vx_req_aux_render_audio_start_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_aux_render_audio_stop_create(J)V
.end method

.method public static final native vx_req_aux_render_audio_stop_t_base_get(JLcom/vivox/service/vx_req_aux_render_audio_stop_t;)J
.end method

.method public static final native vx_req_aux_render_audio_stop_t_base_set(JLcom/vivox/service/vx_req_aux_render_audio_stop_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_aux_reset_password_create(J)V
.end method

.method public static final native vx_req_aux_reset_password_t_base_get(JLcom/vivox/service/vx_req_aux_reset_password_t;)J
.end method

.method public static final native vx_req_aux_reset_password_t_base_set(JLcom/vivox/service/vx_req_aux_reset_password_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_aux_reset_password_t_server_url_get(JLcom/vivox/service/vx_req_aux_reset_password_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_aux_reset_password_t_server_url_set(JLcom/vivox/service/vx_req_aux_reset_password_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_aux_reset_password_t_user_email_get(JLcom/vivox/service/vx_req_aux_reset_password_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_aux_reset_password_t_user_email_set(JLcom/vivox/service/vx_req_aux_reset_password_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_aux_reset_password_t_user_uri_get(JLcom/vivox/service/vx_req_aux_reset_password_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_aux_reset_password_t_user_uri_set(JLcom/vivox/service/vx_req_aux_reset_password_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_aux_set_capture_device_create(J)V
.end method

.method public static final native vx_req_aux_set_capture_device_t_base_get(JLcom/vivox/service/vx_req_aux_set_capture_device_t;)J
.end method

.method public static final native vx_req_aux_set_capture_device_t_base_set(JLcom/vivox/service/vx_req_aux_set_capture_device_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_aux_set_capture_device_t_capture_device_specifier_get(JLcom/vivox/service/vx_req_aux_set_capture_device_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_aux_set_capture_device_t_capture_device_specifier_set(JLcom/vivox/service/vx_req_aux_set_capture_device_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_aux_set_idle_timeout_create(J)V
.end method

.method public static final native vx_req_aux_set_idle_timeout_t_base_get(JLcom/vivox/service/vx_req_aux_set_idle_timeout_t;)J
.end method

.method public static final native vx_req_aux_set_idle_timeout_t_base_set(JLcom/vivox/service/vx_req_aux_set_idle_timeout_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_aux_set_idle_timeout_t_seconds_get(JLcom/vivox/service/vx_req_aux_set_idle_timeout_t;)I
.end method

.method public static final native vx_req_aux_set_idle_timeout_t_seconds_set(JLcom/vivox/service/vx_req_aux_set_idle_timeout_t;I)V
.end method

.method public static final native vx_req_aux_set_mic_level_create(J)V
.end method

.method public static final native vx_req_aux_set_mic_level_t_base_get(JLcom/vivox/service/vx_req_aux_set_mic_level_t;)J
.end method

.method public static final native vx_req_aux_set_mic_level_t_base_set(JLcom/vivox/service/vx_req_aux_set_mic_level_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_aux_set_mic_level_t_level_get(JLcom/vivox/service/vx_req_aux_set_mic_level_t;)I
.end method

.method public static final native vx_req_aux_set_mic_level_t_level_set(JLcom/vivox/service/vx_req_aux_set_mic_level_t;I)V
.end method

.method public static final native vx_req_aux_set_render_device_create(J)V
.end method

.method public static final native vx_req_aux_set_render_device_t_base_get(JLcom/vivox/service/vx_req_aux_set_render_device_t;)J
.end method

.method public static final native vx_req_aux_set_render_device_t_base_set(JLcom/vivox/service/vx_req_aux_set_render_device_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_aux_set_render_device_t_render_device_specifier_get(JLcom/vivox/service/vx_req_aux_set_render_device_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_aux_set_render_device_t_render_device_specifier_set(JLcom/vivox/service/vx_req_aux_set_render_device_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_aux_set_speaker_level_create(J)V
.end method

.method public static final native vx_req_aux_set_speaker_level_t_base_get(JLcom/vivox/service/vx_req_aux_set_speaker_level_t;)J
.end method

.method public static final native vx_req_aux_set_speaker_level_t_base_set(JLcom/vivox/service/vx_req_aux_set_speaker_level_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_aux_set_speaker_level_t_level_get(JLcom/vivox/service/vx_req_aux_set_speaker_level_t;)I
.end method

.method public static final native vx_req_aux_set_speaker_level_t_level_set(JLcom/vivox/service/vx_req_aux_set_speaker_level_t;I)V
.end method

.method public static final native vx_req_aux_set_vad_properties_create(J)V
.end method

.method public static final native vx_req_aux_set_vad_properties_t_base_get(JLcom/vivox/service/vx_req_aux_set_vad_properties_t;)J
.end method

.method public static final native vx_req_aux_set_vad_properties_t_base_set(JLcom/vivox/service/vx_req_aux_set_vad_properties_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_aux_set_vad_properties_t_vad_hangover_get(JLcom/vivox/service/vx_req_aux_set_vad_properties_t;)I
.end method

.method public static final native vx_req_aux_set_vad_properties_t_vad_hangover_set(JLcom/vivox/service/vx_req_aux_set_vad_properties_t;I)V
.end method

.method public static final native vx_req_aux_set_vad_properties_t_vad_sensitivity_get(JLcom/vivox/service/vx_req_aux_set_vad_properties_t;)I
.end method

.method public static final native vx_req_aux_set_vad_properties_t_vad_sensitivity_set(JLcom/vivox/service/vx_req_aux_set_vad_properties_t;I)V
.end method

.method public static final native vx_req_aux_start_buffer_capture_create(J)V
.end method

.method public static final native vx_req_aux_start_buffer_capture_t_base_get(JLcom/vivox/service/vx_req_aux_start_buffer_capture_t;)J
.end method

.method public static final native vx_req_aux_start_buffer_capture_t_base_set(JLcom/vivox/service/vx_req_aux_start_buffer_capture_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_base_t_cookie_get(JLcom/vivox/service/vx_req_base_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_base_t_cookie_set(JLcom/vivox/service/vx_req_base_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_base_t_message_get(JLcom/vivox/service/vx_req_base_t;)J
.end method

.method public static final native vx_req_base_t_message_set(JLcom/vivox/service/vx_req_base_t;JLcom/vivox/service/vx_message_base_t;)V
.end method

.method public static final native vx_req_base_t_type_get(JLcom/vivox/service/vx_req_base_t;)I
.end method

.method public static final native vx_req_base_t_type_set(JLcom/vivox/service/vx_req_base_t;I)V
.end method

.method public static final native vx_req_base_t_vcookie_get(JLcom/vivox/service/vx_req_base_t;)J
.end method

.method public static final native vx_req_base_t_vcookie_set(JLcom/vivox/service/vx_req_base_t;J)V
.end method

.method public static final native vx_req_channel_ban_user_create(J)V
.end method

.method public static final native vx_req_channel_ban_user_t_account_handle_get(JLcom/vivox/service/vx_req_channel_ban_user_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_channel_ban_user_t_account_handle_set(JLcom/vivox/service/vx_req_channel_ban_user_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_channel_ban_user_t_base_get(JLcom/vivox/service/vx_req_channel_ban_user_t;)J
.end method

.method public static final native vx_req_channel_ban_user_t_base_set(JLcom/vivox/service/vx_req_channel_ban_user_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_channel_ban_user_t_channel_name_get(JLcom/vivox/service/vx_req_channel_ban_user_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_channel_ban_user_t_channel_name_set(JLcom/vivox/service/vx_req_channel_ban_user_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_channel_ban_user_t_channel_uri_get(JLcom/vivox/service/vx_req_channel_ban_user_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_channel_ban_user_t_channel_uri_set(JLcom/vivox/service/vx_req_channel_ban_user_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_channel_ban_user_t_participant_uri_get(JLcom/vivox/service/vx_req_channel_ban_user_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_channel_ban_user_t_participant_uri_set(JLcom/vivox/service/vx_req_channel_ban_user_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_channel_ban_user_t_set_banned_get(JLcom/vivox/service/vx_req_channel_ban_user_t;)I
.end method

.method public static final native vx_req_channel_ban_user_t_set_banned_set(JLcom/vivox/service/vx_req_channel_ban_user_t;I)V
.end method

.method public static final native vx_req_channel_get_banned_users_create(J)V
.end method

.method public static final native vx_req_channel_get_banned_users_t_account_handle_get(JLcom/vivox/service/vx_req_channel_get_banned_users_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_channel_get_banned_users_t_account_handle_set(JLcom/vivox/service/vx_req_channel_get_banned_users_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_channel_get_banned_users_t_base_get(JLcom/vivox/service/vx_req_channel_get_banned_users_t;)J
.end method

.method public static final native vx_req_channel_get_banned_users_t_base_set(JLcom/vivox/service/vx_req_channel_get_banned_users_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_channel_get_banned_users_t_channel_uri_get(JLcom/vivox/service/vx_req_channel_get_banned_users_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_channel_get_banned_users_t_channel_uri_set(JLcom/vivox/service/vx_req_channel_get_banned_users_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_channel_kick_user_create(J)V
.end method

.method public static final native vx_req_channel_kick_user_t_account_handle_get(JLcom/vivox/service/vx_req_channel_kick_user_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_channel_kick_user_t_account_handle_set(JLcom/vivox/service/vx_req_channel_kick_user_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_channel_kick_user_t_base_get(JLcom/vivox/service/vx_req_channel_kick_user_t;)J
.end method

.method public static final native vx_req_channel_kick_user_t_base_set(JLcom/vivox/service/vx_req_channel_kick_user_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_channel_kick_user_t_channel_name_get(JLcom/vivox/service/vx_req_channel_kick_user_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_channel_kick_user_t_channel_name_set(JLcom/vivox/service/vx_req_channel_kick_user_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_channel_kick_user_t_channel_uri_get(JLcom/vivox/service/vx_req_channel_kick_user_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_channel_kick_user_t_channel_uri_set(JLcom/vivox/service/vx_req_channel_kick_user_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_channel_kick_user_t_participant_uri_get(JLcom/vivox/service/vx_req_channel_kick_user_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_channel_kick_user_t_participant_uri_set(JLcom/vivox/service/vx_req_channel_kick_user_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_channel_mute_all_users_create(J)V
.end method

.method public static final native vx_req_channel_mute_all_users_t_account_handle_get(JLcom/vivox/service/vx_req_channel_mute_all_users_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_channel_mute_all_users_t_account_handle_set(JLcom/vivox/service/vx_req_channel_mute_all_users_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_channel_mute_all_users_t_base_get(JLcom/vivox/service/vx_req_channel_mute_all_users_t;)J
.end method

.method public static final native vx_req_channel_mute_all_users_t_base_set(JLcom/vivox/service/vx_req_channel_mute_all_users_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_channel_mute_all_users_t_channel_name_get(JLcom/vivox/service/vx_req_channel_mute_all_users_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_channel_mute_all_users_t_channel_name_set(JLcom/vivox/service/vx_req_channel_mute_all_users_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_channel_mute_all_users_t_channel_uri_get(JLcom/vivox/service/vx_req_channel_mute_all_users_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_channel_mute_all_users_t_channel_uri_set(JLcom/vivox/service/vx_req_channel_mute_all_users_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_channel_mute_all_users_t_scope_get(JLcom/vivox/service/vx_req_channel_mute_all_users_t;)I
.end method

.method public static final native vx_req_channel_mute_all_users_t_scope_set(JLcom/vivox/service/vx_req_channel_mute_all_users_t;I)V
.end method

.method public static final native vx_req_channel_mute_all_users_t_set_muted_get(JLcom/vivox/service/vx_req_channel_mute_all_users_t;)I
.end method

.method public static final native vx_req_channel_mute_all_users_t_set_muted_set(JLcom/vivox/service/vx_req_channel_mute_all_users_t;I)V
.end method

.method public static final native vx_req_channel_mute_user_create(J)V
.end method

.method public static final native vx_req_channel_mute_user_t_account_handle_get(JLcom/vivox/service/vx_req_channel_mute_user_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_channel_mute_user_t_account_handle_set(JLcom/vivox/service/vx_req_channel_mute_user_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_channel_mute_user_t_base_get(JLcom/vivox/service/vx_req_channel_mute_user_t;)J
.end method

.method public static final native vx_req_channel_mute_user_t_base_set(JLcom/vivox/service/vx_req_channel_mute_user_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_channel_mute_user_t_channel_name_get(JLcom/vivox/service/vx_req_channel_mute_user_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_channel_mute_user_t_channel_name_set(JLcom/vivox/service/vx_req_channel_mute_user_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_channel_mute_user_t_channel_uri_get(JLcom/vivox/service/vx_req_channel_mute_user_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_channel_mute_user_t_channel_uri_set(JLcom/vivox/service/vx_req_channel_mute_user_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_channel_mute_user_t_participant_uri_get(JLcom/vivox/service/vx_req_channel_mute_user_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_channel_mute_user_t_participant_uri_set(JLcom/vivox/service/vx_req_channel_mute_user_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_channel_mute_user_t_scope_get(JLcom/vivox/service/vx_req_channel_mute_user_t;)I
.end method

.method public static final native vx_req_channel_mute_user_t_scope_set(JLcom/vivox/service/vx_req_channel_mute_user_t;I)V
.end method

.method public static final native vx_req_channel_mute_user_t_set_muted_get(JLcom/vivox/service/vx_req_channel_mute_user_t;)I
.end method

.method public static final native vx_req_channel_mute_user_t_set_muted_set(JLcom/vivox/service/vx_req_channel_mute_user_t;I)V
.end method

.method public static final native vx_req_channel_set_lock_mode_create(J)V
.end method

.method public static final native vx_req_channel_set_lock_mode_t_account_handle_get(JLcom/vivox/service/vx_req_channel_set_lock_mode_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_channel_set_lock_mode_t_account_handle_set(JLcom/vivox/service/vx_req_channel_set_lock_mode_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_channel_set_lock_mode_t_base_get(JLcom/vivox/service/vx_req_channel_set_lock_mode_t;)J
.end method

.method public static final native vx_req_channel_set_lock_mode_t_base_set(JLcom/vivox/service/vx_req_channel_set_lock_mode_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_channel_set_lock_mode_t_channel_uri_get(JLcom/vivox/service/vx_req_channel_set_lock_mode_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_channel_set_lock_mode_t_channel_uri_set(JLcom/vivox/service/vx_req_channel_set_lock_mode_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_channel_set_lock_mode_t_lock_mode_get(JLcom/vivox/service/vx_req_channel_set_lock_mode_t;)I
.end method

.method public static final native vx_req_channel_set_lock_mode_t_lock_mode_set(JLcom/vivox/service/vx_req_channel_set_lock_mode_t;I)V
.end method

.method public static final native vx_req_connector_create_create(J)V
.end method

.method public static final native vx_req_connector_create_t_acct_mgmt_server_get(JLcom/vivox/service/vx_req_connector_create_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_connector_create_t_acct_mgmt_server_set(JLcom/vivox/service/vx_req_connector_create_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_connector_create_t_allow_cross_domain_logins_get(JLcom/vivox/service/vx_req_connector_create_t;)I
.end method

.method public static final native vx_req_connector_create_t_allow_cross_domain_logins_set(JLcom/vivox/service/vx_req_connector_create_t;I)V
.end method

.method public static final native vx_req_connector_create_t_application_get(JLcom/vivox/service/vx_req_connector_create_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_connector_create_t_application_set(JLcom/vivox/service/vx_req_connector_create_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_connector_create_t_attempt_stun_get(JLcom/vivox/service/vx_req_connector_create_t;)I
.end method

.method public static final native vx_req_connector_create_t_attempt_stun_set(JLcom/vivox/service/vx_req_connector_create_t;I)V
.end method

.method public static final native vx_req_connector_create_t_base_get(JLcom/vivox/service/vx_req_connector_create_t;)J
.end method

.method public static final native vx_req_connector_create_t_base_set(JLcom/vivox/service/vx_req_connector_create_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_connector_create_t_client_name_get(JLcom/vivox/service/vx_req_connector_create_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_connector_create_t_client_name_set(JLcom/vivox/service/vx_req_connector_create_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_connector_create_t_default_codec_get(JLcom/vivox/service/vx_req_connector_create_t;)I
.end method

.method public static final native vx_req_connector_create_t_default_codec_set(JLcom/vivox/service/vx_req_connector_create_t;I)V
.end method

.method public static final native vx_req_connector_create_t_http_proxy_server_name_get(JLcom/vivox/service/vx_req_connector_create_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_connector_create_t_http_proxy_server_name_set(JLcom/vivox/service/vx_req_connector_create_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_connector_create_t_http_proxy_server_port_get(JLcom/vivox/service/vx_req_connector_create_t;)I
.end method

.method public static final native vx_req_connector_create_t_http_proxy_server_port_set(JLcom/vivox/service/vx_req_connector_create_t;I)V
.end method

.method public static final native vx_req_connector_create_t_log_filename_prefix_get(JLcom/vivox/service/vx_req_connector_create_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_connector_create_t_log_filename_prefix_set(JLcom/vivox/service/vx_req_connector_create_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_connector_create_t_log_filename_suffix_get(JLcom/vivox/service/vx_req_connector_create_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_connector_create_t_log_filename_suffix_set(JLcom/vivox/service/vx_req_connector_create_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_connector_create_t_log_folder_get(JLcom/vivox/service/vx_req_connector_create_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_connector_create_t_log_folder_set(JLcom/vivox/service/vx_req_connector_create_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_connector_create_t_log_level_get(JLcom/vivox/service/vx_req_connector_create_t;)I
.end method

.method public static final native vx_req_connector_create_t_log_level_set(JLcom/vivox/service/vx_req_connector_create_t;I)V
.end method

.method public static final native vx_req_connector_create_t_max_calls_get(JLcom/vivox/service/vx_req_connector_create_t;)I
.end method

.method public static final native vx_req_connector_create_t_max_calls_set(JLcom/vivox/service/vx_req_connector_create_t;I)V
.end method

.method public static final native vx_req_connector_create_t_maximum_port_get(JLcom/vivox/service/vx_req_connector_create_t;)I
.end method

.method public static final native vx_req_connector_create_t_maximum_port_set(JLcom/vivox/service/vx_req_connector_create_t;I)V
.end method

.method public static final native vx_req_connector_create_t_media_probe_server_get(JLcom/vivox/service/vx_req_connector_create_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_connector_create_t_media_probe_server_set(JLcom/vivox/service/vx_req_connector_create_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_connector_create_t_minimum_port_get(JLcom/vivox/service/vx_req_connector_create_t;)I
.end method

.method public static final native vx_req_connector_create_t_minimum_port_set(JLcom/vivox/service/vx_req_connector_create_t;I)V
.end method

.method public static final native vx_req_connector_create_t_mode_get(JLcom/vivox/service/vx_req_connector_create_t;)I
.end method

.method public static final native vx_req_connector_create_t_mode_set(JLcom/vivox/service/vx_req_connector_create_t;I)V
.end method

.method public static final native vx_req_connector_create_t_session_handle_type_get(JLcom/vivox/service/vx_req_connector_create_t;)I
.end method

.method public static final native vx_req_connector_create_t_session_handle_type_set(JLcom/vivox/service/vx_req_connector_create_t;I)V
.end method

.method public static final native vx_req_connector_create_t_user_agent_id_get(JLcom/vivox/service/vx_req_connector_create_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_connector_create_t_user_agent_id_set(JLcom/vivox/service/vx_req_connector_create_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_connector_get_local_audio_info_create(J)V
.end method

.method public static final native vx_req_connector_get_local_audio_info_t_base_get(JLcom/vivox/service/vx_req_connector_get_local_audio_info_t;)J
.end method

.method public static final native vx_req_connector_get_local_audio_info_t_base_set(JLcom/vivox/service/vx_req_connector_get_local_audio_info_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_connector_get_local_audio_info_t_connector_handle_get(JLcom/vivox/service/vx_req_connector_get_local_audio_info_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_connector_get_local_audio_info_t_connector_handle_set(JLcom/vivox/service/vx_req_connector_get_local_audio_info_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_connector_initiate_shutdown_create(J)V
.end method

.method public static final native vx_req_connector_initiate_shutdown_t_base_get(JLcom/vivox/service/vx_req_connector_initiate_shutdown_t;)J
.end method

.method public static final native vx_req_connector_initiate_shutdown_t_base_set(JLcom/vivox/service/vx_req_connector_initiate_shutdown_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_connector_initiate_shutdown_t_client_name_get(JLcom/vivox/service/vx_req_connector_initiate_shutdown_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_connector_initiate_shutdown_t_client_name_set(JLcom/vivox/service/vx_req_connector_initiate_shutdown_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_connector_initiate_shutdown_t_connector_handle_get(JLcom/vivox/service/vx_req_connector_initiate_shutdown_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_connector_initiate_shutdown_t_connector_handle_set(JLcom/vivox/service/vx_req_connector_initiate_shutdown_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_connector_mute_local_mic_create(J)V
.end method

.method public static final native vx_req_connector_mute_local_mic_t_base_get(JLcom/vivox/service/vx_req_connector_mute_local_mic_t;)J
.end method

.method public static final native vx_req_connector_mute_local_mic_t_base_set(JLcom/vivox/service/vx_req_connector_mute_local_mic_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_connector_mute_local_mic_t_connector_handle_get(JLcom/vivox/service/vx_req_connector_mute_local_mic_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_connector_mute_local_mic_t_connector_handle_set(JLcom/vivox/service/vx_req_connector_mute_local_mic_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_connector_mute_local_mic_t_mute_level_get(JLcom/vivox/service/vx_req_connector_mute_local_mic_t;)I
.end method

.method public static final native vx_req_connector_mute_local_mic_t_mute_level_set(JLcom/vivox/service/vx_req_connector_mute_local_mic_t;I)V
.end method

.method public static final native vx_req_connector_mute_local_speaker_create(J)V
.end method

.method public static final native vx_req_connector_mute_local_speaker_t_base_get(JLcom/vivox/service/vx_req_connector_mute_local_speaker_t;)J
.end method

.method public static final native vx_req_connector_mute_local_speaker_t_base_set(JLcom/vivox/service/vx_req_connector_mute_local_speaker_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_connector_mute_local_speaker_t_connector_handle_get(JLcom/vivox/service/vx_req_connector_mute_local_speaker_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_connector_mute_local_speaker_t_connector_handle_set(JLcom/vivox/service/vx_req_connector_mute_local_speaker_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_connector_mute_local_speaker_t_mute_level_get(JLcom/vivox/service/vx_req_connector_mute_local_speaker_t;)I
.end method

.method public static final native vx_req_connector_mute_local_speaker_t_mute_level_set(JLcom/vivox/service/vx_req_connector_mute_local_speaker_t;I)V
.end method

.method public static final native vx_req_connector_set_local_mic_volume_create(J)V
.end method

.method public static final native vx_req_connector_set_local_mic_volume_t_base_get(JLcom/vivox/service/vx_req_connector_set_local_mic_volume_t;)J
.end method

.method public static final native vx_req_connector_set_local_mic_volume_t_base_set(JLcom/vivox/service/vx_req_connector_set_local_mic_volume_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_connector_set_local_mic_volume_t_connector_handle_get(JLcom/vivox/service/vx_req_connector_set_local_mic_volume_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_connector_set_local_mic_volume_t_connector_handle_set(JLcom/vivox/service/vx_req_connector_set_local_mic_volume_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_connector_set_local_mic_volume_t_volume_get(JLcom/vivox/service/vx_req_connector_set_local_mic_volume_t;)I
.end method

.method public static final native vx_req_connector_set_local_mic_volume_t_volume_set(JLcom/vivox/service/vx_req_connector_set_local_mic_volume_t;I)V
.end method

.method public static final native vx_req_connector_set_local_speaker_volume_create(J)V
.end method

.method public static final native vx_req_connector_set_local_speaker_volume_t_base_get(JLcom/vivox/service/vx_req_connector_set_local_speaker_volume_t;)J
.end method

.method public static final native vx_req_connector_set_local_speaker_volume_t_base_set(JLcom/vivox/service/vx_req_connector_set_local_speaker_volume_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_connector_set_local_speaker_volume_t_connector_handle_get(JLcom/vivox/service/vx_req_connector_set_local_speaker_volume_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_connector_set_local_speaker_volume_t_connector_handle_set(JLcom/vivox/service/vx_req_connector_set_local_speaker_volume_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_connector_set_local_speaker_volume_t_volume_get(JLcom/vivox/service/vx_req_connector_set_local_speaker_volume_t;)I
.end method

.method public static final native vx_req_connector_set_local_speaker_volume_t_volume_set(JLcom/vivox/service/vx_req_connector_set_local_speaker_volume_t;I)V
.end method

.method public static final native vx_req_session_channel_invite_user_create(J)V
.end method

.method public static final native vx_req_session_channel_invite_user_t_base_get(JLcom/vivox/service/vx_req_session_channel_invite_user_t;)J
.end method

.method public static final native vx_req_session_channel_invite_user_t_base_set(JLcom/vivox/service/vx_req_session_channel_invite_user_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_session_channel_invite_user_t_participant_uri_get(JLcom/vivox/service/vx_req_session_channel_invite_user_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_session_channel_invite_user_t_participant_uri_set(JLcom/vivox/service/vx_req_session_channel_invite_user_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_session_channel_invite_user_t_session_handle_get(JLcom/vivox/service/vx_req_session_channel_invite_user_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_session_channel_invite_user_t_session_handle_set(JLcom/vivox/service/vx_req_session_channel_invite_user_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_session_create_create(J)V
.end method

.method public static final native vx_req_session_create_t_account_handle_get(JLcom/vivox/service/vx_req_session_create_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_session_create_t_account_handle_set(JLcom/vivox/service/vx_req_session_create_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_session_create_t_alias_username_get(JLcom/vivox/service/vx_req_session_create_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_session_create_t_alias_username_set(JLcom/vivox/service/vx_req_session_create_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_session_create_t_base_get(JLcom/vivox/service/vx_req_session_create_t;)J
.end method

.method public static final native vx_req_session_create_t_base_set(JLcom/vivox/service/vx_req_session_create_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_session_create_t_connect_audio_get(JLcom/vivox/service/vx_req_session_create_t;)I
.end method

.method public static final native vx_req_session_create_t_connect_audio_set(JLcom/vivox/service/vx_req_session_create_t;I)V
.end method

.method public static final native vx_req_session_create_t_connect_text_get(JLcom/vivox/service/vx_req_session_create_t;)I
.end method

.method public static final native vx_req_session_create_t_connect_text_set(JLcom/vivox/service/vx_req_session_create_t;I)V
.end method

.method public static final native vx_req_session_create_t_jitter_compensation_get(JLcom/vivox/service/vx_req_session_create_t;)I
.end method

.method public static final native vx_req_session_create_t_jitter_compensation_set(JLcom/vivox/service/vx_req_session_create_t;I)V
.end method

.method public static final native vx_req_session_create_t_join_audio_get(JLcom/vivox/service/vx_req_session_create_t;)I
.end method

.method public static final native vx_req_session_create_t_join_audio_set(JLcom/vivox/service/vx_req_session_create_t;I)V
.end method

.method public static final native vx_req_session_create_t_join_text_get(JLcom/vivox/service/vx_req_session_create_t;)I
.end method

.method public static final native vx_req_session_create_t_join_text_set(JLcom/vivox/service/vx_req_session_create_t;I)V
.end method

.method public static final native vx_req_session_create_t_name_get(JLcom/vivox/service/vx_req_session_create_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_session_create_t_name_set(JLcom/vivox/service/vx_req_session_create_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_session_create_t_password_get(JLcom/vivox/service/vx_req_session_create_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_session_create_t_password_hash_algorithm_get(JLcom/vivox/service/vx_req_session_create_t;)I
.end method

.method public static final native vx_req_session_create_t_password_hash_algorithm_set(JLcom/vivox/service/vx_req_session_create_t;I)V
.end method

.method public static final native vx_req_session_create_t_password_set(JLcom/vivox/service/vx_req_session_create_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_session_create_t_session_font_id_get(JLcom/vivox/service/vx_req_session_create_t;)I
.end method

.method public static final native vx_req_session_create_t_session_font_id_set(JLcom/vivox/service/vx_req_session_create_t;I)V
.end method

.method public static final native vx_req_session_create_t_uri_get(JLcom/vivox/service/vx_req_session_create_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_session_create_t_uri_set(JLcom/vivox/service/vx_req_session_create_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_session_media_connect_create(J)V
.end method

.method public static final native vx_req_session_media_connect_t_base_get(JLcom/vivox/service/vx_req_session_media_connect_t;)J
.end method

.method public static final native vx_req_session_media_connect_t_base_set(JLcom/vivox/service/vx_req_session_media_connect_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_session_media_connect_t_capture_device_id_get(JLcom/vivox/service/vx_req_session_media_connect_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_session_media_connect_t_capture_device_id_set(JLcom/vivox/service/vx_req_session_media_connect_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_session_media_connect_t_jitter_compensation_get(JLcom/vivox/service/vx_req_session_media_connect_t;)I
.end method

.method public static final native vx_req_session_media_connect_t_jitter_compensation_set(JLcom/vivox/service/vx_req_session_media_connect_t;I)V
.end method

.method public static final native vx_req_session_media_connect_t_loop_mode_duration_seconds_get(JLcom/vivox/service/vx_req_session_media_connect_t;)I
.end method

.method public static final native vx_req_session_media_connect_t_loop_mode_duration_seconds_set(JLcom/vivox/service/vx_req_session_media_connect_t;I)V
.end method

.method public static final native vx_req_session_media_connect_t_media_get(JLcom/vivox/service/vx_req_session_media_connect_t;)I
.end method

.method public static final native vx_req_session_media_connect_t_media_set(JLcom/vivox/service/vx_req_session_media_connect_t;I)V
.end method

.method public static final native vx_req_session_media_connect_t_render_device_id_get(JLcom/vivox/service/vx_req_session_media_connect_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_session_media_connect_t_render_device_id_set(JLcom/vivox/service/vx_req_session_media_connect_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_session_media_connect_t_session_font_id_get(JLcom/vivox/service/vx_req_session_media_connect_t;)I
.end method

.method public static final native vx_req_session_media_connect_t_session_font_id_set(JLcom/vivox/service/vx_req_session_media_connect_t;I)V
.end method

.method public static final native vx_req_session_media_connect_t_session_handle_get(JLcom/vivox/service/vx_req_session_media_connect_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_session_media_connect_t_session_handle_set(JLcom/vivox/service/vx_req_session_media_connect_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_session_media_connect_t_sessiongroup_handle_get(JLcom/vivox/service/vx_req_session_media_connect_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_session_media_connect_t_sessiongroup_handle_set(JLcom/vivox/service/vx_req_session_media_connect_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_session_media_disconnect_create(J)V
.end method

.method public static final native vx_req_session_media_disconnect_t_base_get(JLcom/vivox/service/vx_req_session_media_disconnect_t;)J
.end method

.method public static final native vx_req_session_media_disconnect_t_base_set(JLcom/vivox/service/vx_req_session_media_disconnect_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_session_media_disconnect_t_media_get(JLcom/vivox/service/vx_req_session_media_disconnect_t;)I
.end method

.method public static final native vx_req_session_media_disconnect_t_media_set(JLcom/vivox/service/vx_req_session_media_disconnect_t;I)V
.end method

.method public static final native vx_req_session_media_disconnect_t_session_handle_get(JLcom/vivox/service/vx_req_session_media_disconnect_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_session_media_disconnect_t_session_handle_set(JLcom/vivox/service/vx_req_session_media_disconnect_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_session_media_disconnect_t_sessiongroup_handle_get(JLcom/vivox/service/vx_req_session_media_disconnect_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_session_media_disconnect_t_sessiongroup_handle_set(JLcom/vivox/service/vx_req_session_media_disconnect_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_session_media_disconnect_t_termination_status_get(JLcom/vivox/service/vx_req_session_media_disconnect_t;)I
.end method

.method public static final native vx_req_session_media_disconnect_t_termination_status_set(JLcom/vivox/service/vx_req_session_media_disconnect_t;I)V
.end method

.method public static final native vx_req_session_mute_local_speaker_create(J)V
.end method

.method public static final native vx_req_session_mute_local_speaker_t_base_get(JLcom/vivox/service/vx_req_session_mute_local_speaker_t;)J
.end method

.method public static final native vx_req_session_mute_local_speaker_t_base_set(JLcom/vivox/service/vx_req_session_mute_local_speaker_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_session_mute_local_speaker_t_mute_level_get(JLcom/vivox/service/vx_req_session_mute_local_speaker_t;)I
.end method

.method public static final native vx_req_session_mute_local_speaker_t_mute_level_set(JLcom/vivox/service/vx_req_session_mute_local_speaker_t;I)V
.end method

.method public static final native vx_req_session_mute_local_speaker_t_scope_get(JLcom/vivox/service/vx_req_session_mute_local_speaker_t;)I
.end method

.method public static final native vx_req_session_mute_local_speaker_t_scope_set(JLcom/vivox/service/vx_req_session_mute_local_speaker_t;I)V
.end method

.method public static final native vx_req_session_mute_local_speaker_t_session_handle_get(JLcom/vivox/service/vx_req_session_mute_local_speaker_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_session_mute_local_speaker_t_session_handle_set(JLcom/vivox/service/vx_req_session_mute_local_speaker_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_session_send_dtmf_create(J)V
.end method

.method public static final native vx_req_session_send_dtmf_t_base_get(JLcom/vivox/service/vx_req_session_send_dtmf_t;)J
.end method

.method public static final native vx_req_session_send_dtmf_t_base_set(JLcom/vivox/service/vx_req_session_send_dtmf_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_session_send_dtmf_t_dtmf_type_get(JLcom/vivox/service/vx_req_session_send_dtmf_t;)I
.end method

.method public static final native vx_req_session_send_dtmf_t_dtmf_type_set(JLcom/vivox/service/vx_req_session_send_dtmf_t;I)V
.end method

.method public static final native vx_req_session_send_dtmf_t_session_handle_get(JLcom/vivox/service/vx_req_session_send_dtmf_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_session_send_dtmf_t_session_handle_set(JLcom/vivox/service/vx_req_session_send_dtmf_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_session_send_message_create(J)V
.end method

.method public static final native vx_req_session_send_message_t_base_get(JLcom/vivox/service/vx_req_session_send_message_t;)J
.end method

.method public static final native vx_req_session_send_message_t_base_set(JLcom/vivox/service/vx_req_session_send_message_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_session_send_message_t_message_body_get(JLcom/vivox/service/vx_req_session_send_message_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_session_send_message_t_message_body_set(JLcom/vivox/service/vx_req_session_send_message_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_session_send_message_t_message_header_get(JLcom/vivox/service/vx_req_session_send_message_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_session_send_message_t_message_header_set(JLcom/vivox/service/vx_req_session_send_message_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_session_send_message_t_session_handle_get(JLcom/vivox/service/vx_req_session_send_message_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_session_send_message_t_session_handle_set(JLcom/vivox/service/vx_req_session_send_message_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_session_send_notification_create(J)V
.end method

.method public static final native vx_req_session_send_notification_t_base_get(JLcom/vivox/service/vx_req_session_send_notification_t;)J
.end method

.method public static final native vx_req_session_send_notification_t_base_set(JLcom/vivox/service/vx_req_session_send_notification_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_session_send_notification_t_notification_type_get(JLcom/vivox/service/vx_req_session_send_notification_t;)I
.end method

.method public static final native vx_req_session_send_notification_t_notification_type_set(JLcom/vivox/service/vx_req_session_send_notification_t;I)V
.end method

.method public static final native vx_req_session_send_notification_t_session_handle_get(JLcom/vivox/service/vx_req_session_send_notification_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_session_send_notification_t_session_handle_set(JLcom/vivox/service/vx_req_session_send_notification_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_session_set_3d_position_create(J)V
.end method

.method public static final native vx_req_session_set_3d_position_t_base_get(JLcom/vivox/service/vx_req_session_set_3d_position_t;)J
.end method

.method public static final native vx_req_session_set_3d_position_t_base_set(JLcom/vivox/service/vx_req_session_set_3d_position_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_session_set_3d_position_t_get_listener_at_orientation_item(JLcom/vivox/service/vx_req_session_set_3d_position_t;I)D
.end method

.method public static final native vx_req_session_set_3d_position_t_get_listener_left_orientation_item(JLcom/vivox/service/vx_req_session_set_3d_position_t;I)D
.end method

.method public static final native vx_req_session_set_3d_position_t_get_listener_position_item(JLcom/vivox/service/vx_req_session_set_3d_position_t;I)D
.end method

.method public static final native vx_req_session_set_3d_position_t_get_listener_up_orientation_item(JLcom/vivox/service/vx_req_session_set_3d_position_t;I)D
.end method

.method public static final native vx_req_session_set_3d_position_t_get_listener_velocity_item(JLcom/vivox/service/vx_req_session_set_3d_position_t;I)D
.end method

.method public static final native vx_req_session_set_3d_position_t_get_speaker_at_orientation_item(JLcom/vivox/service/vx_req_session_set_3d_position_t;I)D
.end method

.method public static final native vx_req_session_set_3d_position_t_get_speaker_left_orientation_item(JLcom/vivox/service/vx_req_session_set_3d_position_t;I)D
.end method

.method public static final native vx_req_session_set_3d_position_t_get_speaker_position_item(JLcom/vivox/service/vx_req_session_set_3d_position_t;I)D
.end method

.method public static final native vx_req_session_set_3d_position_t_get_speaker_up_orientation_item(JLcom/vivox/service/vx_req_session_set_3d_position_t;I)D
.end method

.method public static final native vx_req_session_set_3d_position_t_get_speaker_velocity_item(JLcom/vivox/service/vx_req_session_set_3d_position_t;I)D
.end method

.method public static final native vx_req_session_set_3d_position_t_listener_at_orientation_get(JLcom/vivox/service/vx_req_session_set_3d_position_t;)J
.end method

.method public static final native vx_req_session_set_3d_position_t_listener_at_orientation_set(JLcom/vivox/service/vx_req_session_set_3d_position_t;J)V
.end method

.method public static final native vx_req_session_set_3d_position_t_listener_left_orientation_get(JLcom/vivox/service/vx_req_session_set_3d_position_t;)J
.end method

.method public static final native vx_req_session_set_3d_position_t_listener_left_orientation_set(JLcom/vivox/service/vx_req_session_set_3d_position_t;J)V
.end method

.method public static final native vx_req_session_set_3d_position_t_listener_position_get(JLcom/vivox/service/vx_req_session_set_3d_position_t;)J
.end method

.method public static final native vx_req_session_set_3d_position_t_listener_position_set(JLcom/vivox/service/vx_req_session_set_3d_position_t;J)V
.end method

.method public static final native vx_req_session_set_3d_position_t_listener_up_orientation_get(JLcom/vivox/service/vx_req_session_set_3d_position_t;)J
.end method

.method public static final native vx_req_session_set_3d_position_t_listener_up_orientation_set(JLcom/vivox/service/vx_req_session_set_3d_position_t;J)V
.end method

.method public static final native vx_req_session_set_3d_position_t_listener_velocity_get(JLcom/vivox/service/vx_req_session_set_3d_position_t;)J
.end method

.method public static final native vx_req_session_set_3d_position_t_listener_velocity_set(JLcom/vivox/service/vx_req_session_set_3d_position_t;J)V
.end method

.method public static final native vx_req_session_set_3d_position_t_req_disposition_type_get(JLcom/vivox/service/vx_req_session_set_3d_position_t;)I
.end method

.method public static final native vx_req_session_set_3d_position_t_req_disposition_type_set(JLcom/vivox/service/vx_req_session_set_3d_position_t;I)V
.end method

.method public static final native vx_req_session_set_3d_position_t_session_handle_get(JLcom/vivox/service/vx_req_session_set_3d_position_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_session_set_3d_position_t_session_handle_set(JLcom/vivox/service/vx_req_session_set_3d_position_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_session_set_3d_position_t_set_listener_at_orientation_item(JLcom/vivox/service/vx_req_session_set_3d_position_t;ID)V
.end method

.method public static final native vx_req_session_set_3d_position_t_set_listener_left_orientation_item(JLcom/vivox/service/vx_req_session_set_3d_position_t;ID)V
.end method

.method public static final native vx_req_session_set_3d_position_t_set_listener_position_item(JLcom/vivox/service/vx_req_session_set_3d_position_t;ID)V
.end method

.method public static final native vx_req_session_set_3d_position_t_set_listener_up_orientation_item(JLcom/vivox/service/vx_req_session_set_3d_position_t;ID)V
.end method

.method public static final native vx_req_session_set_3d_position_t_set_listener_velocity_item(JLcom/vivox/service/vx_req_session_set_3d_position_t;ID)V
.end method

.method public static final native vx_req_session_set_3d_position_t_set_speaker_at_orientation_item(JLcom/vivox/service/vx_req_session_set_3d_position_t;ID)V
.end method

.method public static final native vx_req_session_set_3d_position_t_set_speaker_left_orientation_item(JLcom/vivox/service/vx_req_session_set_3d_position_t;ID)V
.end method

.method public static final native vx_req_session_set_3d_position_t_set_speaker_position_item(JLcom/vivox/service/vx_req_session_set_3d_position_t;ID)V
.end method

.method public static final native vx_req_session_set_3d_position_t_set_speaker_up_orientation_item(JLcom/vivox/service/vx_req_session_set_3d_position_t;ID)V
.end method

.method public static final native vx_req_session_set_3d_position_t_set_speaker_velocity_item(JLcom/vivox/service/vx_req_session_set_3d_position_t;ID)V
.end method

.method public static final native vx_req_session_set_3d_position_t_speaker_at_orientation_get(JLcom/vivox/service/vx_req_session_set_3d_position_t;)J
.end method

.method public static final native vx_req_session_set_3d_position_t_speaker_at_orientation_set(JLcom/vivox/service/vx_req_session_set_3d_position_t;J)V
.end method

.method public static final native vx_req_session_set_3d_position_t_speaker_left_orientation_get(JLcom/vivox/service/vx_req_session_set_3d_position_t;)J
.end method

.method public static final native vx_req_session_set_3d_position_t_speaker_left_orientation_set(JLcom/vivox/service/vx_req_session_set_3d_position_t;J)V
.end method

.method public static final native vx_req_session_set_3d_position_t_speaker_position_get(JLcom/vivox/service/vx_req_session_set_3d_position_t;)J
.end method

.method public static final native vx_req_session_set_3d_position_t_speaker_position_set(JLcom/vivox/service/vx_req_session_set_3d_position_t;J)V
.end method

.method public static final native vx_req_session_set_3d_position_t_speaker_up_orientation_get(JLcom/vivox/service/vx_req_session_set_3d_position_t;)J
.end method

.method public static final native vx_req_session_set_3d_position_t_speaker_up_orientation_set(JLcom/vivox/service/vx_req_session_set_3d_position_t;J)V
.end method

.method public static final native vx_req_session_set_3d_position_t_speaker_velocity_get(JLcom/vivox/service/vx_req_session_set_3d_position_t;)J
.end method

.method public static final native vx_req_session_set_3d_position_t_speaker_velocity_set(JLcom/vivox/service/vx_req_session_set_3d_position_t;J)V
.end method

.method public static final native vx_req_session_set_3d_position_t_type_get(JLcom/vivox/service/vx_req_session_set_3d_position_t;)I
.end method

.method public static final native vx_req_session_set_3d_position_t_type_set(JLcom/vivox/service/vx_req_session_set_3d_position_t;I)V
.end method

.method public static final native vx_req_session_set_local_speaker_volume_create(J)V
.end method

.method public static final native vx_req_session_set_local_speaker_volume_t_base_get(JLcom/vivox/service/vx_req_session_set_local_speaker_volume_t;)J
.end method

.method public static final native vx_req_session_set_local_speaker_volume_t_base_set(JLcom/vivox/service/vx_req_session_set_local_speaker_volume_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_session_set_local_speaker_volume_t_session_handle_get(JLcom/vivox/service/vx_req_session_set_local_speaker_volume_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_session_set_local_speaker_volume_t_session_handle_set(JLcom/vivox/service/vx_req_session_set_local_speaker_volume_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_session_set_local_speaker_volume_t_volume_get(JLcom/vivox/service/vx_req_session_set_local_speaker_volume_t;)I
.end method

.method public static final native vx_req_session_set_local_speaker_volume_t_volume_set(JLcom/vivox/service/vx_req_session_set_local_speaker_volume_t;I)V
.end method

.method public static final native vx_req_session_set_participant_mute_for_me_create(J)V
.end method

.method public static final native vx_req_session_set_participant_mute_for_me_t_base_get(JLcom/vivox/service/vx_req_session_set_participant_mute_for_me_t;)J
.end method

.method public static final native vx_req_session_set_participant_mute_for_me_t_base_set(JLcom/vivox/service/vx_req_session_set_participant_mute_for_me_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_session_set_participant_mute_for_me_t_mute_get(JLcom/vivox/service/vx_req_session_set_participant_mute_for_me_t;)I
.end method

.method public static final native vx_req_session_set_participant_mute_for_me_t_mute_set(JLcom/vivox/service/vx_req_session_set_participant_mute_for_me_t;I)V
.end method

.method public static final native vx_req_session_set_participant_mute_for_me_t_participant_uri_get(JLcom/vivox/service/vx_req_session_set_participant_mute_for_me_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_session_set_participant_mute_for_me_t_participant_uri_set(JLcom/vivox/service/vx_req_session_set_participant_mute_for_me_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_session_set_participant_mute_for_me_t_scope_get(JLcom/vivox/service/vx_req_session_set_participant_mute_for_me_t;)I
.end method

.method public static final native vx_req_session_set_participant_mute_for_me_t_scope_set(JLcom/vivox/service/vx_req_session_set_participant_mute_for_me_t;I)V
.end method

.method public static final native vx_req_session_set_participant_mute_for_me_t_session_handle_get(JLcom/vivox/service/vx_req_session_set_participant_mute_for_me_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_session_set_participant_mute_for_me_t_session_handle_set(JLcom/vivox/service/vx_req_session_set_participant_mute_for_me_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_session_set_participant_volume_for_me_create(J)V
.end method

.method public static final native vx_req_session_set_participant_volume_for_me_t_base_get(JLcom/vivox/service/vx_req_session_set_participant_volume_for_me_t;)J
.end method

.method public static final native vx_req_session_set_participant_volume_for_me_t_base_set(JLcom/vivox/service/vx_req_session_set_participant_volume_for_me_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_session_set_participant_volume_for_me_t_participant_uri_get(JLcom/vivox/service/vx_req_session_set_participant_volume_for_me_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_session_set_participant_volume_for_me_t_participant_uri_set(JLcom/vivox/service/vx_req_session_set_participant_volume_for_me_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_session_set_participant_volume_for_me_t_session_handle_get(JLcom/vivox/service/vx_req_session_set_participant_volume_for_me_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_session_set_participant_volume_for_me_t_session_handle_set(JLcom/vivox/service/vx_req_session_set_participant_volume_for_me_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_session_set_participant_volume_for_me_t_volume_get(JLcom/vivox/service/vx_req_session_set_participant_volume_for_me_t;)I
.end method

.method public static final native vx_req_session_set_participant_volume_for_me_t_volume_set(JLcom/vivox/service/vx_req_session_set_participant_volume_for_me_t;I)V
.end method

.method public static final native vx_req_session_set_voice_font_create(J)V
.end method

.method public static final native vx_req_session_set_voice_font_t_base_get(JLcom/vivox/service/vx_req_session_set_voice_font_t;)J
.end method

.method public static final native vx_req_session_set_voice_font_t_base_set(JLcom/vivox/service/vx_req_session_set_voice_font_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_session_set_voice_font_t_session_font_id_get(JLcom/vivox/service/vx_req_session_set_voice_font_t;)I
.end method

.method public static final native vx_req_session_set_voice_font_t_session_font_id_set(JLcom/vivox/service/vx_req_session_set_voice_font_t;I)V
.end method

.method public static final native vx_req_session_set_voice_font_t_session_handle_get(JLcom/vivox/service/vx_req_session_set_voice_font_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_session_set_voice_font_t_session_handle_set(JLcom/vivox/service/vx_req_session_set_voice_font_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_session_terminate_create(J)V
.end method

.method public static final native vx_req_session_terminate_t_base_get(JLcom/vivox/service/vx_req_session_terminate_t;)J
.end method

.method public static final native vx_req_session_terminate_t_base_set(JLcom/vivox/service/vx_req_session_terminate_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_session_terminate_t_session_handle_get(JLcom/vivox/service/vx_req_session_terminate_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_session_terminate_t_session_handle_set(JLcom/vivox/service/vx_req_session_terminate_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_session_text_connect_create(J)V
.end method

.method public static final native vx_req_session_text_connect_t_base_get(JLcom/vivox/service/vx_req_session_text_connect_t;)J
.end method

.method public static final native vx_req_session_text_connect_t_base_set(JLcom/vivox/service/vx_req_session_text_connect_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_session_text_connect_t_session_handle_get(JLcom/vivox/service/vx_req_session_text_connect_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_session_text_connect_t_session_handle_set(JLcom/vivox/service/vx_req_session_text_connect_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_session_text_connect_t_sessiongroup_handle_get(JLcom/vivox/service/vx_req_session_text_connect_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_session_text_connect_t_sessiongroup_handle_set(JLcom/vivox/service/vx_req_session_text_connect_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_session_text_disconnect_create(J)V
.end method

.method public static final native vx_req_session_text_disconnect_t_base_get(JLcom/vivox/service/vx_req_session_text_disconnect_t;)J
.end method

.method public static final native vx_req_session_text_disconnect_t_base_set(JLcom/vivox/service/vx_req_session_text_disconnect_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_session_text_disconnect_t_session_handle_get(JLcom/vivox/service/vx_req_session_text_disconnect_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_session_text_disconnect_t_session_handle_set(JLcom/vivox/service/vx_req_session_text_disconnect_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_session_text_disconnect_t_sessiongroup_handle_get(JLcom/vivox/service/vx_req_session_text_disconnect_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_session_text_disconnect_t_sessiongroup_handle_set(JLcom/vivox/service/vx_req_session_text_disconnect_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_sessiongroup_add_session_create(J)V
.end method

.method public static final native vx_req_sessiongroup_add_session_t_base_get(JLcom/vivox/service/vx_req_sessiongroup_add_session_t;)J
.end method

.method public static final native vx_req_sessiongroup_add_session_t_base_set(JLcom/vivox/service/vx_req_sessiongroup_add_session_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_sessiongroup_add_session_t_connect_audio_get(JLcom/vivox/service/vx_req_sessiongroup_add_session_t;)I
.end method

.method public static final native vx_req_sessiongroup_add_session_t_connect_audio_set(JLcom/vivox/service/vx_req_sessiongroup_add_session_t;I)V
.end method

.method public static final native vx_req_sessiongroup_add_session_t_connect_text_get(JLcom/vivox/service/vx_req_sessiongroup_add_session_t;)I
.end method

.method public static final native vx_req_sessiongroup_add_session_t_connect_text_set(JLcom/vivox/service/vx_req_sessiongroup_add_session_t;I)V
.end method

.method public static final native vx_req_sessiongroup_add_session_t_jitter_compensation_get(JLcom/vivox/service/vx_req_sessiongroup_add_session_t;)I
.end method

.method public static final native vx_req_sessiongroup_add_session_t_jitter_compensation_set(JLcom/vivox/service/vx_req_sessiongroup_add_session_t;I)V
.end method

.method public static final native vx_req_sessiongroup_add_session_t_name_get(JLcom/vivox/service/vx_req_sessiongroup_add_session_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_sessiongroup_add_session_t_name_set(JLcom/vivox/service/vx_req_sessiongroup_add_session_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_sessiongroup_add_session_t_password_get(JLcom/vivox/service/vx_req_sessiongroup_add_session_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_sessiongroup_add_session_t_password_hash_algorithm_get(JLcom/vivox/service/vx_req_sessiongroup_add_session_t;)I
.end method

.method public static final native vx_req_sessiongroup_add_session_t_password_hash_algorithm_set(JLcom/vivox/service/vx_req_sessiongroup_add_session_t;I)V
.end method

.method public static final native vx_req_sessiongroup_add_session_t_password_set(JLcom/vivox/service/vx_req_sessiongroup_add_session_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_sessiongroup_add_session_t_session_font_id_get(JLcom/vivox/service/vx_req_sessiongroup_add_session_t;)I
.end method

.method public static final native vx_req_sessiongroup_add_session_t_session_font_id_set(JLcom/vivox/service/vx_req_sessiongroup_add_session_t;I)V
.end method

.method public static final native vx_req_sessiongroup_add_session_t_sessiongroup_handle_get(JLcom/vivox/service/vx_req_sessiongroup_add_session_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_sessiongroup_add_session_t_sessiongroup_handle_set(JLcom/vivox/service/vx_req_sessiongroup_add_session_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_sessiongroup_add_session_t_uri_get(JLcom/vivox/service/vx_req_sessiongroup_add_session_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_sessiongroup_add_session_t_uri_set(JLcom/vivox/service/vx_req_sessiongroup_add_session_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_sessiongroup_control_audio_injection_create(J)V
.end method

.method public static final native vx_req_sessiongroup_control_audio_injection_t_audio_injection_control_type_get(JLcom/vivox/service/vx_req_sessiongroup_control_audio_injection_t;)I
.end method

.method public static final native vx_req_sessiongroup_control_audio_injection_t_audio_injection_control_type_set(JLcom/vivox/service/vx_req_sessiongroup_control_audio_injection_t;I)V
.end method

.method public static final native vx_req_sessiongroup_control_audio_injection_t_base_get(JLcom/vivox/service/vx_req_sessiongroup_control_audio_injection_t;)J
.end method

.method public static final native vx_req_sessiongroup_control_audio_injection_t_base_set(JLcom/vivox/service/vx_req_sessiongroup_control_audio_injection_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_sessiongroup_control_audio_injection_t_filename_get(JLcom/vivox/service/vx_req_sessiongroup_control_audio_injection_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_sessiongroup_control_audio_injection_t_filename_set(JLcom/vivox/service/vx_req_sessiongroup_control_audio_injection_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_sessiongroup_control_audio_injection_t_sessiongroup_handle_get(JLcom/vivox/service/vx_req_sessiongroup_control_audio_injection_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_sessiongroup_control_audio_injection_t_sessiongroup_handle_set(JLcom/vivox/service/vx_req_sessiongroup_control_audio_injection_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_sessiongroup_control_playback_create(J)V
.end method

.method public static final native vx_req_sessiongroup_control_playback_t_base_get(JLcom/vivox/service/vx_req_sessiongroup_control_playback_t;)J
.end method

.method public static final native vx_req_sessiongroup_control_playback_t_base_set(JLcom/vivox/service/vx_req_sessiongroup_control_playback_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_sessiongroup_control_playback_t_filename_get(JLcom/vivox/service/vx_req_sessiongroup_control_playback_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_sessiongroup_control_playback_t_filename_set(JLcom/vivox/service/vx_req_sessiongroup_control_playback_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_sessiongroup_control_playback_t_playback_control_type_get(JLcom/vivox/service/vx_req_sessiongroup_control_playback_t;)I
.end method

.method public static final native vx_req_sessiongroup_control_playback_t_playback_control_type_set(JLcom/vivox/service/vx_req_sessiongroup_control_playback_t;I)V
.end method

.method public static final native vx_req_sessiongroup_control_playback_t_sessiongroup_handle_get(JLcom/vivox/service/vx_req_sessiongroup_control_playback_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_sessiongroup_control_playback_t_sessiongroup_handle_set(JLcom/vivox/service/vx_req_sessiongroup_control_playback_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_sessiongroup_control_recording_create(J)V
.end method

.method public static final native vx_req_sessiongroup_control_recording_t_base_get(JLcom/vivox/service/vx_req_sessiongroup_control_recording_t;)J
.end method

.method public static final native vx_req_sessiongroup_control_recording_t_base_set(JLcom/vivox/service/vx_req_sessiongroup_control_recording_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_sessiongroup_control_recording_t_delta_frames_per_control_frame_get(JLcom/vivox/service/vx_req_sessiongroup_control_recording_t;)I
.end method

.method public static final native vx_req_sessiongroup_control_recording_t_delta_frames_per_control_frame_set(JLcom/vivox/service/vx_req_sessiongroup_control_recording_t;I)V
.end method

.method public static final native vx_req_sessiongroup_control_recording_t_enable_audio_recording_events_get(JLcom/vivox/service/vx_req_sessiongroup_control_recording_t;)I
.end method

.method public static final native vx_req_sessiongroup_control_recording_t_enable_audio_recording_events_set(JLcom/vivox/service/vx_req_sessiongroup_control_recording_t;I)V
.end method

.method public static final native vx_req_sessiongroup_control_recording_t_filename_get(JLcom/vivox/service/vx_req_sessiongroup_control_recording_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_sessiongroup_control_recording_t_filename_set(JLcom/vivox/service/vx_req_sessiongroup_control_recording_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_sessiongroup_control_recording_t_loop_mode_duration_seconds_get(JLcom/vivox/service/vx_req_sessiongroup_control_recording_t;)I
.end method

.method public static final native vx_req_sessiongroup_control_recording_t_loop_mode_duration_seconds_set(JLcom/vivox/service/vx_req_sessiongroup_control_recording_t;I)V
.end method

.method public static final native vx_req_sessiongroup_control_recording_t_recording_control_type_get(JLcom/vivox/service/vx_req_sessiongroup_control_recording_t;)I
.end method

.method public static final native vx_req_sessiongroup_control_recording_t_recording_control_type_set(JLcom/vivox/service/vx_req_sessiongroup_control_recording_t;I)V
.end method

.method public static final native vx_req_sessiongroup_control_recording_t_sessiongroup_handle_get(JLcom/vivox/service/vx_req_sessiongroup_control_recording_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_sessiongroup_control_recording_t_sessiongroup_handle_set(JLcom/vivox/service/vx_req_sessiongroup_control_recording_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_sessiongroup_create_create(J)V
.end method

.method public static final native vx_req_sessiongroup_create_t_account_handle_get(JLcom/vivox/service/vx_req_sessiongroup_create_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_sessiongroup_create_t_account_handle_set(JLcom/vivox/service/vx_req_sessiongroup_create_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_sessiongroup_create_t_alias_username_get(JLcom/vivox/service/vx_req_sessiongroup_create_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_sessiongroup_create_t_alias_username_set(JLcom/vivox/service/vx_req_sessiongroup_create_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_sessiongroup_create_t_base_get(JLcom/vivox/service/vx_req_sessiongroup_create_t;)J
.end method

.method public static final native vx_req_sessiongroup_create_t_base_set(JLcom/vivox/service/vx_req_sessiongroup_create_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_sessiongroup_create_t_capture_device_id_get(JLcom/vivox/service/vx_req_sessiongroup_create_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_sessiongroup_create_t_capture_device_id_set(JLcom/vivox/service/vx_req_sessiongroup_create_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_sessiongroup_create_t_loop_mode_duration_seconds_get(JLcom/vivox/service/vx_req_sessiongroup_create_t;)I
.end method

.method public static final native vx_req_sessiongroup_create_t_loop_mode_duration_seconds_set(JLcom/vivox/service/vx_req_sessiongroup_create_t;I)V
.end method

.method public static final native vx_req_sessiongroup_create_t_render_device_id_get(JLcom/vivox/service/vx_req_sessiongroup_create_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_sessiongroup_create_t_render_device_id_set(JLcom/vivox/service/vx_req_sessiongroup_create_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_sessiongroup_create_t_type_get(JLcom/vivox/service/vx_req_sessiongroup_create_t;)I
.end method

.method public static final native vx_req_sessiongroup_create_t_type_set(JLcom/vivox/service/vx_req_sessiongroup_create_t;I)V
.end method

.method public static final native vx_req_sessiongroup_get_stats_create(J)V
.end method

.method public static final native vx_req_sessiongroup_get_stats_t_base_get(JLcom/vivox/service/vx_req_sessiongroup_get_stats_t;)J
.end method

.method public static final native vx_req_sessiongroup_get_stats_t_base_set(JLcom/vivox/service/vx_req_sessiongroup_get_stats_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_sessiongroup_get_stats_t_sessiongroup_handle_get(JLcom/vivox/service/vx_req_sessiongroup_get_stats_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_sessiongroup_get_stats_t_sessiongroup_handle_set(JLcom/vivox/service/vx_req_sessiongroup_get_stats_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_sessiongroup_remove_session_create(J)V
.end method

.method public static final native vx_req_sessiongroup_remove_session_t_base_get(JLcom/vivox/service/vx_req_sessiongroup_remove_session_t;)J
.end method

.method public static final native vx_req_sessiongroup_remove_session_t_base_set(JLcom/vivox/service/vx_req_sessiongroup_remove_session_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_sessiongroup_remove_session_t_session_handle_get(JLcom/vivox/service/vx_req_sessiongroup_remove_session_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_sessiongroup_remove_session_t_session_handle_set(JLcom/vivox/service/vx_req_sessiongroup_remove_session_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_sessiongroup_remove_session_t_sessiongroup_handle_get(JLcom/vivox/service/vx_req_sessiongroup_remove_session_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_sessiongroup_remove_session_t_sessiongroup_handle_set(JLcom/vivox/service/vx_req_sessiongroup_remove_session_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_sessiongroup_reset_focus_create(J)V
.end method

.method public static final native vx_req_sessiongroup_reset_focus_t_base_get(JLcom/vivox/service/vx_req_sessiongroup_reset_focus_t;)J
.end method

.method public static final native vx_req_sessiongroup_reset_focus_t_base_set(JLcom/vivox/service/vx_req_sessiongroup_reset_focus_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_sessiongroup_reset_focus_t_sessiongroup_handle_get(JLcom/vivox/service/vx_req_sessiongroup_reset_focus_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_sessiongroup_reset_focus_t_sessiongroup_handle_set(JLcom/vivox/service/vx_req_sessiongroup_reset_focus_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_sessiongroup_set_focus_create(J)V
.end method

.method public static final native vx_req_sessiongroup_set_focus_t_base_get(JLcom/vivox/service/vx_req_sessiongroup_set_focus_t;)J
.end method

.method public static final native vx_req_sessiongroup_set_focus_t_base_set(JLcom/vivox/service/vx_req_sessiongroup_set_focus_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_sessiongroup_set_focus_t_session_handle_get(JLcom/vivox/service/vx_req_sessiongroup_set_focus_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_sessiongroup_set_focus_t_session_handle_set(JLcom/vivox/service/vx_req_sessiongroup_set_focus_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_sessiongroup_set_focus_t_sessiongroup_handle_get(JLcom/vivox/service/vx_req_sessiongroup_set_focus_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_sessiongroup_set_focus_t_sessiongroup_handle_set(JLcom/vivox/service/vx_req_sessiongroup_set_focus_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_sessiongroup_set_playback_options_create(J)V
.end method

.method public static final native vx_req_sessiongroup_set_playback_options_t_base_get(JLcom/vivox/service/vx_req_sessiongroup_set_playback_options_t;)J
.end method

.method public static final native vx_req_sessiongroup_set_playback_options_t_base_set(JLcom/vivox/service/vx_req_sessiongroup_set_playback_options_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_sessiongroup_set_playback_options_t_frame_number_get(JLcom/vivox/service/vx_req_sessiongroup_set_playback_options_t;)I
.end method

.method public static final native vx_req_sessiongroup_set_playback_options_t_frame_number_set(JLcom/vivox/service/vx_req_sessiongroup_set_playback_options_t;I)V
.end method

.method public static final native vx_req_sessiongroup_set_playback_options_t_playback_mode_get(JLcom/vivox/service/vx_req_sessiongroup_set_playback_options_t;)I
.end method

.method public static final native vx_req_sessiongroup_set_playback_options_t_playback_mode_set(JLcom/vivox/service/vx_req_sessiongroup_set_playback_options_t;I)V
.end method

.method public static final native vx_req_sessiongroup_set_playback_options_t_playback_speed_get(JLcom/vivox/service/vx_req_sessiongroup_set_playback_options_t;)D
.end method

.method public static final native vx_req_sessiongroup_set_playback_options_t_playback_speed_set(JLcom/vivox/service/vx_req_sessiongroup_set_playback_options_t;D)V
.end method

.method public static final native vx_req_sessiongroup_set_playback_options_t_sessiongroup_handle_get(JLcom/vivox/service/vx_req_sessiongroup_set_playback_options_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_sessiongroup_set_playback_options_t_sessiongroup_handle_set(JLcom/vivox/service/vx_req_sessiongroup_set_playback_options_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_sessiongroup_set_session_3d_position_create(J)V
.end method

.method public static final native vx_req_sessiongroup_set_session_3d_position_t_base_get(JLcom/vivox/service/vx_req_sessiongroup_set_session_3d_position_t;)J
.end method

.method public static final native vx_req_sessiongroup_set_session_3d_position_t_base_set(JLcom/vivox/service/vx_req_sessiongroup_set_session_3d_position_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_sessiongroup_set_session_3d_position_t_get_speaker_position_item(JLcom/vivox/service/vx_req_sessiongroup_set_session_3d_position_t;I)D
.end method

.method public static final native vx_req_sessiongroup_set_session_3d_position_t_session_handle_get(JLcom/vivox/service/vx_req_sessiongroup_set_session_3d_position_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_sessiongroup_set_session_3d_position_t_session_handle_set(JLcom/vivox/service/vx_req_sessiongroup_set_session_3d_position_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_sessiongroup_set_session_3d_position_t_sessiongroup_handle_get(JLcom/vivox/service/vx_req_sessiongroup_set_session_3d_position_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_sessiongroup_set_session_3d_position_t_sessiongroup_handle_set(JLcom/vivox/service/vx_req_sessiongroup_set_session_3d_position_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_sessiongroup_set_session_3d_position_t_set_speaker_position_item(JLcom/vivox/service/vx_req_sessiongroup_set_session_3d_position_t;ID)V
.end method

.method public static final native vx_req_sessiongroup_set_session_3d_position_t_speaker_at_orientation_get(JLcom/vivox/service/vx_req_sessiongroup_set_session_3d_position_t;)J
.end method

.method public static final native vx_req_sessiongroup_set_session_3d_position_t_speaker_at_orientation_set(JLcom/vivox/service/vx_req_sessiongroup_set_session_3d_position_t;J)V
.end method

.method public static final native vx_req_sessiongroup_set_session_3d_position_t_speaker_position_get(JLcom/vivox/service/vx_req_sessiongroup_set_session_3d_position_t;)J
.end method

.method public static final native vx_req_sessiongroup_set_session_3d_position_t_speaker_position_set(JLcom/vivox/service/vx_req_sessiongroup_set_session_3d_position_t;J)V
.end method

.method public static final native vx_req_sessiongroup_set_tx_all_sessions_create(J)V
.end method

.method public static final native vx_req_sessiongroup_set_tx_all_sessions_t_base_get(JLcom/vivox/service/vx_req_sessiongroup_set_tx_all_sessions_t;)J
.end method

.method public static final native vx_req_sessiongroup_set_tx_all_sessions_t_base_set(JLcom/vivox/service/vx_req_sessiongroup_set_tx_all_sessions_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_sessiongroup_set_tx_all_sessions_t_sessiongroup_handle_get(JLcom/vivox/service/vx_req_sessiongroup_set_tx_all_sessions_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_sessiongroup_set_tx_all_sessions_t_sessiongroup_handle_set(JLcom/vivox/service/vx_req_sessiongroup_set_tx_all_sessions_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_sessiongroup_set_tx_no_session_create(J)V
.end method

.method public static final native vx_req_sessiongroup_set_tx_no_session_t_base_get(JLcom/vivox/service/vx_req_sessiongroup_set_tx_no_session_t;)J
.end method

.method public static final native vx_req_sessiongroup_set_tx_no_session_t_base_set(JLcom/vivox/service/vx_req_sessiongroup_set_tx_no_session_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_sessiongroup_set_tx_no_session_t_sessiongroup_handle_get(JLcom/vivox/service/vx_req_sessiongroup_set_tx_no_session_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_sessiongroup_set_tx_no_session_t_sessiongroup_handle_set(JLcom/vivox/service/vx_req_sessiongroup_set_tx_no_session_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_sessiongroup_set_tx_session_create(J)V
.end method

.method public static final native vx_req_sessiongroup_set_tx_session_t_base_get(JLcom/vivox/service/vx_req_sessiongroup_set_tx_session_t;)J
.end method

.method public static final native vx_req_sessiongroup_set_tx_session_t_base_set(JLcom/vivox/service/vx_req_sessiongroup_set_tx_session_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_sessiongroup_set_tx_session_t_session_handle_get(JLcom/vivox/service/vx_req_sessiongroup_set_tx_session_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_sessiongroup_set_tx_session_t_session_handle_set(JLcom/vivox/service/vx_req_sessiongroup_set_tx_session_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_sessiongroup_set_tx_session_t_sessiongroup_handle_get(JLcom/vivox/service/vx_req_sessiongroup_set_tx_session_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_sessiongroup_set_tx_session_t_sessiongroup_handle_set(JLcom/vivox/service/vx_req_sessiongroup_set_tx_session_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_sessiongroup_terminate_create(J)V
.end method

.method public static final native vx_req_sessiongroup_terminate_t_base_get(JLcom/vivox/service/vx_req_sessiongroup_terminate_t;)J
.end method

.method public static final native vx_req_sessiongroup_terminate_t_base_set(JLcom/vivox/service/vx_req_sessiongroup_terminate_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_sessiongroup_terminate_t_sessiongroup_handle_get(JLcom/vivox/service/vx_req_sessiongroup_terminate_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_sessiongroup_terminate_t_sessiongroup_handle_set(JLcom/vivox/service/vx_req_sessiongroup_terminate_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_sessiongroup_unset_focus_create(J)V
.end method

.method public static final native vx_req_sessiongroup_unset_focus_t_base_get(JLcom/vivox/service/vx_req_sessiongroup_unset_focus_t;)J
.end method

.method public static final native vx_req_sessiongroup_unset_focus_t_base_set(JLcom/vivox/service/vx_req_sessiongroup_unset_focus_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_req_sessiongroup_unset_focus_t_session_handle_get(JLcom/vivox/service/vx_req_sessiongroup_unset_focus_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_sessiongroup_unset_focus_t_session_handle_set(JLcom/vivox/service/vx_req_sessiongroup_unset_focus_t;Ljava/lang/String;)V
.end method

.method public static final native vx_req_sessiongroup_unset_focus_t_sessiongroup_handle_get(JLcom/vivox/service/vx_req_sessiongroup_unset_focus_t;)Ljava/lang/String;
.end method

.method public static final native vx_req_sessiongroup_unset_focus_t_sessiongroup_handle_set(JLcom/vivox/service/vx_req_sessiongroup_unset_focus_t;Ljava/lang/String;)V
.end method

.method public static final native vx_request_to_xml(JJ)V
.end method

.method public static final native vx_resp_account_anonymous_login_t_account_handle_get(JLcom/vivox/service/vx_resp_account_anonymous_login_t;)Ljava/lang/String;
.end method

.method public static final native vx_resp_account_anonymous_login_t_account_handle_set(JLcom/vivox/service/vx_resp_account_anonymous_login_t;Ljava/lang/String;)V
.end method

.method public static final native vx_resp_account_anonymous_login_t_account_id_get(JLcom/vivox/service/vx_resp_account_anonymous_login_t;)I
.end method

.method public static final native vx_resp_account_anonymous_login_t_account_id_set(JLcom/vivox/service/vx_resp_account_anonymous_login_t;I)V
.end method

.method public static final native vx_resp_account_anonymous_login_t_base_get(JLcom/vivox/service/vx_resp_account_anonymous_login_t;)J
.end method

.method public static final native vx_resp_account_anonymous_login_t_base_set(JLcom/vivox/service/vx_resp_account_anonymous_login_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_account_anonymous_login_t_displayname_get(JLcom/vivox/service/vx_resp_account_anonymous_login_t;)Ljava/lang/String;
.end method

.method public static final native vx_resp_account_anonymous_login_t_displayname_set(JLcom/vivox/service/vx_resp_account_anonymous_login_t;Ljava/lang/String;)V
.end method

.method public static final native vx_resp_account_anonymous_login_t_uri_get(JLcom/vivox/service/vx_resp_account_anonymous_login_t;)Ljava/lang/String;
.end method

.method public static final native vx_resp_account_anonymous_login_t_uri_set(JLcom/vivox/service/vx_resp_account_anonymous_login_t;Ljava/lang/String;)V
.end method

.method public static final native vx_resp_account_authtoken_login_t_account_handle_get(JLcom/vivox/service/vx_resp_account_authtoken_login_t;)Ljava/lang/String;
.end method

.method public static final native vx_resp_account_authtoken_login_t_account_handle_set(JLcom/vivox/service/vx_resp_account_authtoken_login_t;Ljava/lang/String;)V
.end method

.method public static final native vx_resp_account_authtoken_login_t_account_id_get(JLcom/vivox/service/vx_resp_account_authtoken_login_t;)I
.end method

.method public static final native vx_resp_account_authtoken_login_t_account_id_set(JLcom/vivox/service/vx_resp_account_authtoken_login_t;I)V
.end method

.method public static final native vx_resp_account_authtoken_login_t_base_get(JLcom/vivox/service/vx_resp_account_authtoken_login_t;)J
.end method

.method public static final native vx_resp_account_authtoken_login_t_base_set(JLcom/vivox/service/vx_resp_account_authtoken_login_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_account_authtoken_login_t_display_name_get(JLcom/vivox/service/vx_resp_account_authtoken_login_t;)Ljava/lang/String;
.end method

.method public static final native vx_resp_account_authtoken_login_t_display_name_set(JLcom/vivox/service/vx_resp_account_authtoken_login_t;Ljava/lang/String;)V
.end method

.method public static final native vx_resp_account_authtoken_login_t_num_aliases_get(JLcom/vivox/service/vx_resp_account_authtoken_login_t;)I
.end method

.method public static final native vx_resp_account_authtoken_login_t_num_aliases_set(JLcom/vivox/service/vx_resp_account_authtoken_login_t;I)V
.end method

.method public static final native vx_resp_account_authtoken_login_t_uri_get(JLcom/vivox/service/vx_resp_account_authtoken_login_t;)Ljava/lang/String;
.end method

.method public static final native vx_resp_account_authtoken_login_t_uri_set(JLcom/vivox/service/vx_resp_account_authtoken_login_t;Ljava/lang/String;)V
.end method

.method public static final native vx_resp_account_authtoken_login_t_user_name_get(JLcom/vivox/service/vx_resp_account_authtoken_login_t;)Ljava/lang/String;
.end method

.method public static final native vx_resp_account_authtoken_login_t_user_name_set(JLcom/vivox/service/vx_resp_account_authtoken_login_t;Ljava/lang/String;)V
.end method

.method public static final native vx_resp_account_buddy_delete_t_base_get(JLcom/vivox/service/vx_resp_account_buddy_delete_t;)J
.end method

.method public static final native vx_resp_account_buddy_delete_t_base_set(JLcom/vivox/service/vx_resp_account_buddy_delete_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_account_buddy_search_t_base_get(JLcom/vivox/service/vx_resp_account_buddy_search_t;)J
.end method

.method public static final native vx_resp_account_buddy_search_t_base_set(JLcom/vivox/service/vx_resp_account_buddy_search_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_account_buddy_search_t_buddies_get(JLcom/vivox/service/vx_resp_account_buddy_search_t;)J
.end method

.method public static final native vx_resp_account_buddy_search_t_buddies_set(JLcom/vivox/service/vx_resp_account_buddy_search_t;J)V
.end method

.method public static final native vx_resp_account_buddy_search_t_buddy_count_get(JLcom/vivox/service/vx_resp_account_buddy_search_t;)I
.end method

.method public static final native vx_resp_account_buddy_search_t_buddy_count_set(JLcom/vivox/service/vx_resp_account_buddy_search_t;I)V
.end method

.method public static final native vx_resp_account_buddy_search_t_from_get(JLcom/vivox/service/vx_resp_account_buddy_search_t;)I
.end method

.method public static final native vx_resp_account_buddy_search_t_from_set(JLcom/vivox/service/vx_resp_account_buddy_search_t;I)V
.end method

.method public static final native vx_resp_account_buddy_search_t_get_buddies_item(JLcom/vivox/service/vx_resp_account_buddy_search_t;I)J
.end method

.method public static final native vx_resp_account_buddy_search_t_page_get(JLcom/vivox/service/vx_resp_account_buddy_search_t;)I
.end method

.method public static final native vx_resp_account_buddy_search_t_page_set(JLcom/vivox/service/vx_resp_account_buddy_search_t;I)V
.end method

.method public static final native vx_resp_account_buddy_search_t_to_get(JLcom/vivox/service/vx_resp_account_buddy_search_t;)I
.end method

.method public static final native vx_resp_account_buddy_search_t_to_set(JLcom/vivox/service/vx_resp_account_buddy_search_t;I)V
.end method

.method public static final native vx_resp_account_buddy_set_t_account_id_get(JLcom/vivox/service/vx_resp_account_buddy_set_t;)I
.end method

.method public static final native vx_resp_account_buddy_set_t_account_id_set(JLcom/vivox/service/vx_resp_account_buddy_set_t;I)V
.end method

.method public static final native vx_resp_account_buddy_set_t_base_get(JLcom/vivox/service/vx_resp_account_buddy_set_t;)J
.end method

.method public static final native vx_resp_account_buddy_set_t_base_set(JLcom/vivox/service/vx_resp_account_buddy_set_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_account_buddygroup_delete_t_base_get(JLcom/vivox/service/vx_resp_account_buddygroup_delete_t;)J
.end method

.method public static final native vx_resp_account_buddygroup_delete_t_base_set(JLcom/vivox/service/vx_resp_account_buddygroup_delete_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_account_buddygroup_set_t_base_get(JLcom/vivox/service/vx_resp_account_buddygroup_set_t;)J
.end method

.method public static final native vx_resp_account_buddygroup_set_t_base_set(JLcom/vivox/service/vx_resp_account_buddygroup_set_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_account_buddygroup_set_t_group_id_get(JLcom/vivox/service/vx_resp_account_buddygroup_set_t;)I
.end method

.method public static final native vx_resp_account_buddygroup_set_t_group_id_set(JLcom/vivox/service/vx_resp_account_buddygroup_set_t;I)V
.end method

.method public static final native vx_resp_account_channel_add_acl_t_base_get(JLcom/vivox/service/vx_resp_account_channel_add_acl_t;)J
.end method

.method public static final native vx_resp_account_channel_add_acl_t_base_set(JLcom/vivox/service/vx_resp_account_channel_add_acl_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_account_channel_add_moderator_t_base_get(JLcom/vivox/service/vx_resp_account_channel_add_moderator_t;)J
.end method

.method public static final native vx_resp_account_channel_add_moderator_t_base_set(JLcom/vivox/service/vx_resp_account_channel_add_moderator_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_account_channel_change_owner_t_base_get(JLcom/vivox/service/vx_resp_account_channel_change_owner_t;)J
.end method

.method public static final native vx_resp_account_channel_change_owner_t_base_set(JLcom/vivox/service/vx_resp_account_channel_change_owner_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_account_channel_create_t_base_get(JLcom/vivox/service/vx_resp_account_channel_create_t;)J
.end method

.method public static final native vx_resp_account_channel_create_t_base_set(JLcom/vivox/service/vx_resp_account_channel_create_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_account_channel_create_t_channel_uri_get(JLcom/vivox/service/vx_resp_account_channel_create_t;)Ljava/lang/String;
.end method

.method public static final native vx_resp_account_channel_create_t_channel_uri_set(JLcom/vivox/service/vx_resp_account_channel_create_t;Ljava/lang/String;)V
.end method

.method public static final native vx_resp_account_channel_delete_t_base_get(JLcom/vivox/service/vx_resp_account_channel_delete_t;)J
.end method

.method public static final native vx_resp_account_channel_delete_t_base_set(JLcom/vivox/service/vx_resp_account_channel_delete_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_account_channel_favorite_delete_t_base_get(JLcom/vivox/service/vx_resp_account_channel_favorite_delete_t;)J
.end method

.method public static final native vx_resp_account_channel_favorite_delete_t_base_set(JLcom/vivox/service/vx_resp_account_channel_favorite_delete_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_account_channel_favorite_group_delete_t_base_get(JLcom/vivox/service/vx_resp_account_channel_favorite_group_delete_t;)J
.end method

.method public static final native vx_resp_account_channel_favorite_group_delete_t_base_set(JLcom/vivox/service/vx_resp_account_channel_favorite_group_delete_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_account_channel_favorite_group_set_t_base_get(JLcom/vivox/service/vx_resp_account_channel_favorite_group_set_t;)J
.end method

.method public static final native vx_resp_account_channel_favorite_group_set_t_base_set(JLcom/vivox/service/vx_resp_account_channel_favorite_group_set_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_account_channel_favorite_group_set_t_group_id_get(JLcom/vivox/service/vx_resp_account_channel_favorite_group_set_t;)I
.end method

.method public static final native vx_resp_account_channel_favorite_group_set_t_group_id_set(JLcom/vivox/service/vx_resp_account_channel_favorite_group_set_t;I)V
.end method

.method public static final native vx_resp_account_channel_favorite_set_t_base_get(JLcom/vivox/service/vx_resp_account_channel_favorite_set_t;)J
.end method

.method public static final native vx_resp_account_channel_favorite_set_t_base_set(JLcom/vivox/service/vx_resp_account_channel_favorite_set_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_account_channel_favorite_set_t_channel_favorite_id_get(JLcom/vivox/service/vx_resp_account_channel_favorite_set_t;)I
.end method

.method public static final native vx_resp_account_channel_favorite_set_t_channel_favorite_id_set(JLcom/vivox/service/vx_resp_account_channel_favorite_set_t;I)V
.end method

.method public static final native vx_resp_account_channel_favorites_get_list_t_base_get(JLcom/vivox/service/vx_resp_account_channel_favorites_get_list_t;)J
.end method

.method public static final native vx_resp_account_channel_favorites_get_list_t_base_set(JLcom/vivox/service/vx_resp_account_channel_favorites_get_list_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_account_channel_favorites_get_list_t_favorite_count_get(JLcom/vivox/service/vx_resp_account_channel_favorites_get_list_t;)I
.end method

.method public static final native vx_resp_account_channel_favorites_get_list_t_favorite_count_set(JLcom/vivox/service/vx_resp_account_channel_favorites_get_list_t;I)V
.end method

.method public static final native vx_resp_account_channel_favorites_get_list_t_favorites_get(JLcom/vivox/service/vx_resp_account_channel_favorites_get_list_t;)J
.end method

.method public static final native vx_resp_account_channel_favorites_get_list_t_favorites_set(JLcom/vivox/service/vx_resp_account_channel_favorites_get_list_t;J)V
.end method

.method public static final native vx_resp_account_channel_favorites_get_list_t_get_favorites_item(JLcom/vivox/service/vx_resp_account_channel_favorites_get_list_t;I)J
.end method

.method public static final native vx_resp_account_channel_favorites_get_list_t_get_groups_item(JLcom/vivox/service/vx_resp_account_channel_favorites_get_list_t;I)J
.end method

.method public static final native vx_resp_account_channel_favorites_get_list_t_group_count_get(JLcom/vivox/service/vx_resp_account_channel_favorites_get_list_t;)I
.end method

.method public static final native vx_resp_account_channel_favorites_get_list_t_group_count_set(JLcom/vivox/service/vx_resp_account_channel_favorites_get_list_t;I)V
.end method

.method public static final native vx_resp_account_channel_favorites_get_list_t_groups_get(JLcom/vivox/service/vx_resp_account_channel_favorites_get_list_t;)J
.end method

.method public static final native vx_resp_account_channel_favorites_get_list_t_groups_set(JLcom/vivox/service/vx_resp_account_channel_favorites_get_list_t;J)V
.end method

.method public static final native vx_resp_account_channel_get_acl_t_base_get(JLcom/vivox/service/vx_resp_account_channel_get_acl_t;)J
.end method

.method public static final native vx_resp_account_channel_get_acl_t_base_set(JLcom/vivox/service/vx_resp_account_channel_get_acl_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_account_channel_get_acl_t_get_participants_item(JLcom/vivox/service/vx_resp_account_channel_get_acl_t;I)J
.end method

.method public static final native vx_resp_account_channel_get_acl_t_participants_get(JLcom/vivox/service/vx_resp_account_channel_get_acl_t;)J
.end method

.method public static final native vx_resp_account_channel_get_acl_t_participants_set(JLcom/vivox/service/vx_resp_account_channel_get_acl_t;J)V
.end method

.method public static final native vx_resp_account_channel_get_acl_t_participants_size_get(JLcom/vivox/service/vx_resp_account_channel_get_acl_t;)I
.end method

.method public static final native vx_resp_account_channel_get_acl_t_participants_size_set(JLcom/vivox/service/vx_resp_account_channel_get_acl_t;I)V
.end method

.method public static final native vx_resp_account_channel_get_info_t_base_get(JLcom/vivox/service/vx_resp_account_channel_get_info_t;)J
.end method

.method public static final native vx_resp_account_channel_get_info_t_base_set(JLcom/vivox/service/vx_resp_account_channel_get_info_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_account_channel_get_info_t_channel_get(JLcom/vivox/service/vx_resp_account_channel_get_info_t;)J
.end method

.method public static final native vx_resp_account_channel_get_info_t_channel_set(JLcom/vivox/service/vx_resp_account_channel_get_info_t;JLcom/vivox/service/vx_channel_t;)V
.end method

.method public static final native vx_resp_account_channel_get_moderators_t_base_get(JLcom/vivox/service/vx_resp_account_channel_get_moderators_t;)J
.end method

.method public static final native vx_resp_account_channel_get_moderators_t_base_set(JLcom/vivox/service/vx_resp_account_channel_get_moderators_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_account_channel_get_moderators_t_get_participants_item(JLcom/vivox/service/vx_resp_account_channel_get_moderators_t;I)J
.end method

.method public static final native vx_resp_account_channel_get_moderators_t_participants_get(JLcom/vivox/service/vx_resp_account_channel_get_moderators_t;)J
.end method

.method public static final native vx_resp_account_channel_get_moderators_t_participants_set(JLcom/vivox/service/vx_resp_account_channel_get_moderators_t;J)V
.end method

.method public static final native vx_resp_account_channel_get_moderators_t_participants_size_get(JLcom/vivox/service/vx_resp_account_channel_get_moderators_t;)I
.end method

.method public static final native vx_resp_account_channel_get_moderators_t_participants_size_set(JLcom/vivox/service/vx_resp_account_channel_get_moderators_t;I)V
.end method

.method public static final native vx_resp_account_channel_get_participants_t_base_get(JLcom/vivox/service/vx_resp_account_channel_get_participants_t;)J
.end method

.method public static final native vx_resp_account_channel_get_participants_t_base_set(JLcom/vivox/service/vx_resp_account_channel_get_participants_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_account_channel_get_participants_t_from_get(JLcom/vivox/service/vx_resp_account_channel_get_participants_t;)I
.end method

.method public static final native vx_resp_account_channel_get_participants_t_from_set(JLcom/vivox/service/vx_resp_account_channel_get_participants_t;I)V
.end method

.method public static final native vx_resp_account_channel_get_participants_t_get_participants_item(JLcom/vivox/service/vx_resp_account_channel_get_participants_t;I)J
.end method

.method public static final native vx_resp_account_channel_get_participants_t_page_get(JLcom/vivox/service/vx_resp_account_channel_get_participants_t;)I
.end method

.method public static final native vx_resp_account_channel_get_participants_t_page_set(JLcom/vivox/service/vx_resp_account_channel_get_participants_t;I)V
.end method

.method public static final native vx_resp_account_channel_get_participants_t_participant_count_get(JLcom/vivox/service/vx_resp_account_channel_get_participants_t;)I
.end method

.method public static final native vx_resp_account_channel_get_participants_t_participant_count_set(JLcom/vivox/service/vx_resp_account_channel_get_participants_t;I)V
.end method

.method public static final native vx_resp_account_channel_get_participants_t_participants_get(JLcom/vivox/service/vx_resp_account_channel_get_participants_t;)J
.end method

.method public static final native vx_resp_account_channel_get_participants_t_participants_set(JLcom/vivox/service/vx_resp_account_channel_get_participants_t;J)V
.end method

.method public static final native vx_resp_account_channel_get_participants_t_to_get(JLcom/vivox/service/vx_resp_account_channel_get_participants_t;)I
.end method

.method public static final native vx_resp_account_channel_get_participants_t_to_set(JLcom/vivox/service/vx_resp_account_channel_get_participants_t;I)V
.end method

.method public static final native vx_resp_account_channel_get_participants_t_total_get(JLcom/vivox/service/vx_resp_account_channel_get_participants_t;)I
.end method

.method public static final native vx_resp_account_channel_get_participants_t_total_set(JLcom/vivox/service/vx_resp_account_channel_get_participants_t;I)V
.end method

.method public static final native vx_resp_account_channel_remove_acl_t_base_get(JLcom/vivox/service/vx_resp_account_channel_remove_acl_t;)J
.end method

.method public static final native vx_resp_account_channel_remove_acl_t_base_set(JLcom/vivox/service/vx_resp_account_channel_remove_acl_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_account_channel_remove_moderator_t_base_get(JLcom/vivox/service/vx_resp_account_channel_remove_moderator_t;)J
.end method

.method public static final native vx_resp_account_channel_remove_moderator_t_base_set(JLcom/vivox/service/vx_resp_account_channel_remove_moderator_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_account_channel_search_t_base_get(JLcom/vivox/service/vx_resp_account_channel_search_t;)J
.end method

.method public static final native vx_resp_account_channel_search_t_base_set(JLcom/vivox/service/vx_resp_account_channel_search_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_account_channel_search_t_channel_count_get(JLcom/vivox/service/vx_resp_account_channel_search_t;)I
.end method

.method public static final native vx_resp_account_channel_search_t_channel_count_set(JLcom/vivox/service/vx_resp_account_channel_search_t;I)V
.end method

.method public static final native vx_resp_account_channel_search_t_channels_get(JLcom/vivox/service/vx_resp_account_channel_search_t;)J
.end method

.method public static final native vx_resp_account_channel_search_t_channels_set(JLcom/vivox/service/vx_resp_account_channel_search_t;J)V
.end method

.method public static final native vx_resp_account_channel_search_t_from_get(JLcom/vivox/service/vx_resp_account_channel_search_t;)I
.end method

.method public static final native vx_resp_account_channel_search_t_from_set(JLcom/vivox/service/vx_resp_account_channel_search_t;I)V
.end method

.method public static final native vx_resp_account_channel_search_t_get_channels_item(JLcom/vivox/service/vx_resp_account_channel_search_t;I)J
.end method

.method public static final native vx_resp_account_channel_search_t_page_get(JLcom/vivox/service/vx_resp_account_channel_search_t;)I
.end method

.method public static final native vx_resp_account_channel_search_t_page_set(JLcom/vivox/service/vx_resp_account_channel_search_t;I)V
.end method

.method public static final native vx_resp_account_channel_search_t_to_get(JLcom/vivox/service/vx_resp_account_channel_search_t;)I
.end method

.method public static final native vx_resp_account_channel_search_t_to_set(JLcom/vivox/service/vx_resp_account_channel_search_t;I)V
.end method

.method public static final native vx_resp_account_channel_update_t_base_get(JLcom/vivox/service/vx_resp_account_channel_update_t;)J
.end method

.method public static final native vx_resp_account_channel_update_t_base_set(JLcom/vivox/service/vx_resp_account_channel_update_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_account_create_auto_accept_rule_t_base_get(JLcom/vivox/service/vx_resp_account_create_auto_accept_rule_t;)J
.end method

.method public static final native vx_resp_account_create_auto_accept_rule_t_base_set(JLcom/vivox/service/vx_resp_account_create_auto_accept_rule_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_account_create_block_rule_t_base_get(JLcom/vivox/service/vx_resp_account_create_block_rule_t;)J
.end method

.method public static final native vx_resp_account_create_block_rule_t_base_set(JLcom/vivox/service/vx_resp_account_create_block_rule_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_account_delete_auto_accept_rule_t_base_get(JLcom/vivox/service/vx_resp_account_delete_auto_accept_rule_t;)J
.end method

.method public static final native vx_resp_account_delete_auto_accept_rule_t_base_set(JLcom/vivox/service/vx_resp_account_delete_auto_accept_rule_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_account_delete_block_rule_t_base_get(JLcom/vivox/service/vx_resp_account_delete_block_rule_t;)J
.end method

.method public static final native vx_resp_account_delete_block_rule_t_base_set(JLcom/vivox/service/vx_resp_account_delete_block_rule_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_account_get_account_t_account_get(JLcom/vivox/service/vx_resp_account_get_account_t;)J
.end method

.method public static final native vx_resp_account_get_account_t_account_set(JLcom/vivox/service/vx_resp_account_get_account_t;JLcom/vivox/service/vx_account_t;)V
.end method

.method public static final native vx_resp_account_get_account_t_base_get(JLcom/vivox/service/vx_resp_account_get_account_t;)J
.end method

.method public static final native vx_resp_account_get_account_t_base_set(JLcom/vivox/service/vx_resp_account_get_account_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_account_get_session_fonts_t_base_get(JLcom/vivox/service/vx_resp_account_get_session_fonts_t;)J
.end method

.method public static final native vx_resp_account_get_session_fonts_t_base_set(JLcom/vivox/service/vx_resp_account_get_session_fonts_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_account_get_session_fonts_t_get_session_fonts_item(JLcom/vivox/service/vx_resp_account_get_session_fonts_t;I)J
.end method

.method public static final native vx_resp_account_get_session_fonts_t_session_font_count_get(JLcom/vivox/service/vx_resp_account_get_session_fonts_t;)I
.end method

.method public static final native vx_resp_account_get_session_fonts_t_session_font_count_set(JLcom/vivox/service/vx_resp_account_get_session_fonts_t;I)V
.end method

.method public static final native vx_resp_account_get_session_fonts_t_session_fonts_get(JLcom/vivox/service/vx_resp_account_get_session_fonts_t;)J
.end method

.method public static final native vx_resp_account_get_session_fonts_t_session_fonts_set(JLcom/vivox/service/vx_resp_account_get_session_fonts_t;J)V
.end method

.method public static final native vx_resp_account_get_template_fonts_t_base_get(JLcom/vivox/service/vx_resp_account_get_template_fonts_t;)J
.end method

.method public static final native vx_resp_account_get_template_fonts_t_base_set(JLcom/vivox/service/vx_resp_account_get_template_fonts_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_account_get_template_fonts_t_get_template_fonts_item(JLcom/vivox/service/vx_resp_account_get_template_fonts_t;I)J
.end method

.method public static final native vx_resp_account_get_template_fonts_t_template_font_count_get(JLcom/vivox/service/vx_resp_account_get_template_fonts_t;)I
.end method

.method public static final native vx_resp_account_get_template_fonts_t_template_font_count_set(JLcom/vivox/service/vx_resp_account_get_template_fonts_t;I)V
.end method

.method public static final native vx_resp_account_get_template_fonts_t_template_fonts_get(JLcom/vivox/service/vx_resp_account_get_template_fonts_t;)J
.end method

.method public static final native vx_resp_account_get_template_fonts_t_template_fonts_set(JLcom/vivox/service/vx_resp_account_get_template_fonts_t;J)V
.end method

.method public static final native vx_resp_account_list_auto_accept_rules_t_auto_accept_rules_get(JLcom/vivox/service/vx_resp_account_list_auto_accept_rules_t;)J
.end method

.method public static final native vx_resp_account_list_auto_accept_rules_t_auto_accept_rules_set(JLcom/vivox/service/vx_resp_account_list_auto_accept_rules_t;J)V
.end method

.method public static final native vx_resp_account_list_auto_accept_rules_t_base_get(JLcom/vivox/service/vx_resp_account_list_auto_accept_rules_t;)J
.end method

.method public static final native vx_resp_account_list_auto_accept_rules_t_base_set(JLcom/vivox/service/vx_resp_account_list_auto_accept_rules_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_account_list_auto_accept_rules_t_get_auto_accept_rules_item(JLcom/vivox/service/vx_resp_account_list_auto_accept_rules_t;I)J
.end method

.method public static final native vx_resp_account_list_auto_accept_rules_t_rule_count_get(JLcom/vivox/service/vx_resp_account_list_auto_accept_rules_t;)I
.end method

.method public static final native vx_resp_account_list_auto_accept_rules_t_rule_count_set(JLcom/vivox/service/vx_resp_account_list_auto_accept_rules_t;I)V
.end method

.method public static final native vx_resp_account_list_block_rules_t_base_get(JLcom/vivox/service/vx_resp_account_list_block_rules_t;)J
.end method

.method public static final native vx_resp_account_list_block_rules_t_base_set(JLcom/vivox/service/vx_resp_account_list_block_rules_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_account_list_block_rules_t_block_rules_get(JLcom/vivox/service/vx_resp_account_list_block_rules_t;)J
.end method

.method public static final native vx_resp_account_list_block_rules_t_block_rules_set(JLcom/vivox/service/vx_resp_account_list_block_rules_t;J)V
.end method

.method public static final native vx_resp_account_list_block_rules_t_get_block_rules_item(JLcom/vivox/service/vx_resp_account_list_block_rules_t;I)J
.end method

.method public static final native vx_resp_account_list_block_rules_t_rule_count_get(JLcom/vivox/service/vx_resp_account_list_block_rules_t;)I
.end method

.method public static final native vx_resp_account_list_block_rules_t_rule_count_set(JLcom/vivox/service/vx_resp_account_list_block_rules_t;I)V
.end method

.method public static final native vx_resp_account_list_buddies_and_groups_t_base_get(JLcom/vivox/service/vx_resp_account_list_buddies_and_groups_t;)J
.end method

.method public static final native vx_resp_account_list_buddies_and_groups_t_base_set(JLcom/vivox/service/vx_resp_account_list_buddies_and_groups_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_account_list_buddies_and_groups_t_buddies_get(JLcom/vivox/service/vx_resp_account_list_buddies_and_groups_t;)J
.end method

.method public static final native vx_resp_account_list_buddies_and_groups_t_buddies_set(JLcom/vivox/service/vx_resp_account_list_buddies_and_groups_t;J)V
.end method

.method public static final native vx_resp_account_list_buddies_and_groups_t_buddy_count_get(JLcom/vivox/service/vx_resp_account_list_buddies_and_groups_t;)I
.end method

.method public static final native vx_resp_account_list_buddies_and_groups_t_buddy_count_set(JLcom/vivox/service/vx_resp_account_list_buddies_and_groups_t;I)V
.end method

.method public static final native vx_resp_account_list_buddies_and_groups_t_get_buddies_item(JLcom/vivox/service/vx_resp_account_list_buddies_and_groups_t;I)J
.end method

.method public static final native vx_resp_account_list_buddies_and_groups_t_get_groups_item(JLcom/vivox/service/vx_resp_account_list_buddies_and_groups_t;I)J
.end method

.method public static final native vx_resp_account_list_buddies_and_groups_t_group_count_get(JLcom/vivox/service/vx_resp_account_list_buddies_and_groups_t;)I
.end method

.method public static final native vx_resp_account_list_buddies_and_groups_t_group_count_set(JLcom/vivox/service/vx_resp_account_list_buddies_and_groups_t;I)V
.end method

.method public static final native vx_resp_account_list_buddies_and_groups_t_groups_get(JLcom/vivox/service/vx_resp_account_list_buddies_and_groups_t;)J
.end method

.method public static final native vx_resp_account_list_buddies_and_groups_t_groups_set(JLcom/vivox/service/vx_resp_account_list_buddies_and_groups_t;J)V
.end method

.method public static final native vx_resp_account_login_t_account_handle_get(JLcom/vivox/service/vx_resp_account_login_t;)Ljava/lang/String;
.end method

.method public static final native vx_resp_account_login_t_account_handle_set(JLcom/vivox/service/vx_resp_account_login_t;Ljava/lang/String;)V
.end method

.method public static final native vx_resp_account_login_t_account_id_get(JLcom/vivox/service/vx_resp_account_login_t;)I
.end method

.method public static final native vx_resp_account_login_t_account_id_set(JLcom/vivox/service/vx_resp_account_login_t;I)V
.end method

.method public static final native vx_resp_account_login_t_base_get(JLcom/vivox/service/vx_resp_account_login_t;)J
.end method

.method public static final native vx_resp_account_login_t_base_set(JLcom/vivox/service/vx_resp_account_login_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_account_login_t_display_name_get(JLcom/vivox/service/vx_resp_account_login_t;)Ljava/lang/String;
.end method

.method public static final native vx_resp_account_login_t_display_name_set(JLcom/vivox/service/vx_resp_account_login_t;Ljava/lang/String;)V
.end method

.method public static final native vx_resp_account_login_t_num_aliases_get(JLcom/vivox/service/vx_resp_account_login_t;)I
.end method

.method public static final native vx_resp_account_login_t_num_aliases_set(JLcom/vivox/service/vx_resp_account_login_t;I)V
.end method

.method public static final native vx_resp_account_login_t_uri_get(JLcom/vivox/service/vx_resp_account_login_t;)Ljava/lang/String;
.end method

.method public static final native vx_resp_account_login_t_uri_set(JLcom/vivox/service/vx_resp_account_login_t;Ljava/lang/String;)V
.end method

.method public static final native vx_resp_account_logout_t_base_get(JLcom/vivox/service/vx_resp_account_logout_t;)J
.end method

.method public static final native vx_resp_account_logout_t_base_set(JLcom/vivox/service/vx_resp_account_logout_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_account_post_crash_dump_t_base_get(JLcom/vivox/service/vx_resp_account_post_crash_dump_t;)J
.end method

.method public static final native vx_resp_account_post_crash_dump_t_base_set(JLcom/vivox/service/vx_resp_account_post_crash_dump_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_account_send_message_t_base_get(JLcom/vivox/service/vx_resp_account_send_message_t;)J
.end method

.method public static final native vx_resp_account_send_message_t_base_set(JLcom/vivox/service/vx_resp_account_send_message_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_account_send_sms_t_base_get(JLcom/vivox/service/vx_resp_account_send_sms_t;)J
.end method

.method public static final native vx_resp_account_send_sms_t_base_set(JLcom/vivox/service/vx_resp_account_send_sms_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_account_send_subscription_reply_t_base_get(JLcom/vivox/service/vx_resp_account_send_subscription_reply_t;)J
.end method

.method public static final native vx_resp_account_send_subscription_reply_t_base_set(JLcom/vivox/service/vx_resp_account_send_subscription_reply_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_account_send_user_app_data_t_base_get(JLcom/vivox/service/vx_resp_account_send_user_app_data_t;)J
.end method

.method public static final native vx_resp_account_send_user_app_data_t_base_set(JLcom/vivox/service/vx_resp_account_send_user_app_data_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_account_set_login_properties_t_base_get(JLcom/vivox/service/vx_resp_account_set_login_properties_t;)J
.end method

.method public static final native vx_resp_account_set_login_properties_t_base_set(JLcom/vivox/service/vx_resp_account_set_login_properties_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_account_set_presence_t_base_get(JLcom/vivox/service/vx_resp_account_set_presence_t;)J
.end method

.method public static final native vx_resp_account_set_presence_t_base_set(JLcom/vivox/service/vx_resp_account_set_presence_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_account_update_account_t_base_get(JLcom/vivox/service/vx_resp_account_update_account_t;)J
.end method

.method public static final native vx_resp_account_update_account_t_base_set(JLcom/vivox/service/vx_resp_account_update_account_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_account_web_call_t_base_get(JLcom/vivox/service/vx_resp_account_web_call_t;)J
.end method

.method public static final native vx_resp_account_web_call_t_base_set(JLcom/vivox/service/vx_resp_account_web_call_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_account_web_call_t_content_get(JLcom/vivox/service/vx_resp_account_web_call_t;)Ljava/lang/String;
.end method

.method public static final native vx_resp_account_web_call_t_content_length_get(JLcom/vivox/service/vx_resp_account_web_call_t;)I
.end method

.method public static final native vx_resp_account_web_call_t_content_length_set(JLcom/vivox/service/vx_resp_account_web_call_t;I)V
.end method

.method public static final native vx_resp_account_web_call_t_content_set(JLcom/vivox/service/vx_resp_account_web_call_t;Ljava/lang/String;)V
.end method

.method public static final native vx_resp_account_web_call_t_content_type_get(JLcom/vivox/service/vx_resp_account_web_call_t;)Ljava/lang/String;
.end method

.method public static final native vx_resp_account_web_call_t_content_type_set(JLcom/vivox/service/vx_resp_account_web_call_t;Ljava/lang/String;)V
.end method

.method public static final native vx_resp_aux_capture_audio_start_t_base_get(JLcom/vivox/service/vx_resp_aux_capture_audio_start_t;)J
.end method

.method public static final native vx_resp_aux_capture_audio_start_t_base_set(JLcom/vivox/service/vx_resp_aux_capture_audio_start_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_aux_capture_audio_stop_t_audioBufferPtr_get(JLcom/vivox/service/vx_resp_aux_capture_audio_stop_t;)J
.end method

.method public static final native vx_resp_aux_capture_audio_stop_t_audioBufferPtr_set(JLcom/vivox/service/vx_resp_aux_capture_audio_stop_t;J)V
.end method

.method public static final native vx_resp_aux_capture_audio_stop_t_base_get(JLcom/vivox/service/vx_resp_aux_capture_audio_stop_t;)J
.end method

.method public static final native vx_resp_aux_capture_audio_stop_t_base_set(JLcom/vivox/service/vx_resp_aux_capture_audio_stop_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_aux_connectivity_info_t_base_get(JLcom/vivox/service/vx_resp_aux_connectivity_info_t;)J
.end method

.method public static final native vx_resp_aux_connectivity_info_t_base_set(JLcom/vivox/service/vx_resp_aux_connectivity_info_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_aux_connectivity_info_t_count_get(JLcom/vivox/service/vx_resp_aux_connectivity_info_t;)I
.end method

.method public static final native vx_resp_aux_connectivity_info_t_count_set(JLcom/vivox/service/vx_resp_aux_connectivity_info_t;I)V
.end method

.method public static final native vx_resp_aux_connectivity_info_t_echo_port_get(JLcom/vivox/service/vx_resp_aux_connectivity_info_t;)I
.end method

.method public static final native vx_resp_aux_connectivity_info_t_echo_port_set(JLcom/vivox/service/vx_resp_aux_connectivity_info_t;I)V
.end method

.method public static final native vx_resp_aux_connectivity_info_t_echo_server_get(JLcom/vivox/service/vx_resp_aux_connectivity_info_t;)Ljava/lang/String;
.end method

.method public static final native vx_resp_aux_connectivity_info_t_echo_server_set(JLcom/vivox/service/vx_resp_aux_connectivity_info_t;Ljava/lang/String;)V
.end method

.method public static final native vx_resp_aux_connectivity_info_t_first_sip_port_get(JLcom/vivox/service/vx_resp_aux_connectivity_info_t;)I
.end method

.method public static final native vx_resp_aux_connectivity_info_t_first_sip_port_set(JLcom/vivox/service/vx_resp_aux_connectivity_info_t;I)V
.end method

.method public static final native vx_resp_aux_connectivity_info_t_get_test_results_item(JLcom/vivox/service/vx_resp_aux_connectivity_info_t;I)J
.end method

.method public static final native vx_resp_aux_connectivity_info_t_rtcp_port_get(JLcom/vivox/service/vx_resp_aux_connectivity_info_t;)I
.end method

.method public static final native vx_resp_aux_connectivity_info_t_rtcp_port_set(JLcom/vivox/service/vx_resp_aux_connectivity_info_t;I)V
.end method

.method public static final native vx_resp_aux_connectivity_info_t_rtp_port_get(JLcom/vivox/service/vx_resp_aux_connectivity_info_t;)I
.end method

.method public static final native vx_resp_aux_connectivity_info_t_rtp_port_set(JLcom/vivox/service/vx_resp_aux_connectivity_info_t;I)V
.end method

.method public static final native vx_resp_aux_connectivity_info_t_second_sip_port_get(JLcom/vivox/service/vx_resp_aux_connectivity_info_t;)I
.end method

.method public static final native vx_resp_aux_connectivity_info_t_second_sip_port_set(JLcom/vivox/service/vx_resp_aux_connectivity_info_t;I)V
.end method

.method public static final native vx_resp_aux_connectivity_info_t_stun_server_get(JLcom/vivox/service/vx_resp_aux_connectivity_info_t;)Ljava/lang/String;
.end method

.method public static final native vx_resp_aux_connectivity_info_t_stun_server_set(JLcom/vivox/service/vx_resp_aux_connectivity_info_t;Ljava/lang/String;)V
.end method

.method public static final native vx_resp_aux_connectivity_info_t_test_results_get(JLcom/vivox/service/vx_resp_aux_connectivity_info_t;)J
.end method

.method public static final native vx_resp_aux_connectivity_info_t_test_results_set(JLcom/vivox/service/vx_resp_aux_connectivity_info_t;J)V
.end method

.method public static final native vx_resp_aux_connectivity_info_t_timeout_get(JLcom/vivox/service/vx_resp_aux_connectivity_info_t;)I
.end method

.method public static final native vx_resp_aux_connectivity_info_t_timeout_set(JLcom/vivox/service/vx_resp_aux_connectivity_info_t;I)V
.end method

.method public static final native vx_resp_aux_connectivity_info_t_well_known_ip_get(JLcom/vivox/service/vx_resp_aux_connectivity_info_t;)Ljava/lang/String;
.end method

.method public static final native vx_resp_aux_connectivity_info_t_well_known_ip_set(JLcom/vivox/service/vx_resp_aux_connectivity_info_t;Ljava/lang/String;)V
.end method

.method public static final native vx_resp_aux_create_account_t_base_get(JLcom/vivox/service/vx_resp_aux_create_account_t;)J
.end method

.method public static final native vx_resp_aux_create_account_t_base_set(JLcom/vivox/service/vx_resp_aux_create_account_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_aux_deactivate_account_t_base_get(JLcom/vivox/service/vx_resp_aux_deactivate_account_t;)J
.end method

.method public static final native vx_resp_aux_deactivate_account_t_base_set(JLcom/vivox/service/vx_resp_aux_deactivate_account_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_aux_diagnostic_state_dump_t_base_get(JLcom/vivox/service/vx_resp_aux_diagnostic_state_dump_t;)J
.end method

.method public static final native vx_resp_aux_diagnostic_state_dump_t_base_set(JLcom/vivox/service/vx_resp_aux_diagnostic_state_dump_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_aux_diagnostic_state_dump_t_current_capture_device_get(JLcom/vivox/service/vx_resp_aux_diagnostic_state_dump_t;)J
.end method

.method public static final native vx_resp_aux_diagnostic_state_dump_t_current_capture_device_set(JLcom/vivox/service/vx_resp_aux_diagnostic_state_dump_t;JLcom/vivox/service/vx_device_t;)V
.end method

.method public static final native vx_resp_aux_diagnostic_state_dump_t_current_render_device_get(JLcom/vivox/service/vx_resp_aux_diagnostic_state_dump_t;)J
.end method

.method public static final native vx_resp_aux_diagnostic_state_dump_t_current_render_device_set(JLcom/vivox/service/vx_resp_aux_diagnostic_state_dump_t;JLcom/vivox/service/vx_device_t;)V
.end method

.method public static final native vx_resp_aux_diagnostic_state_dump_t_effective_capture_device_get(JLcom/vivox/service/vx_resp_aux_diagnostic_state_dump_t;)J
.end method

.method public static final native vx_resp_aux_diagnostic_state_dump_t_effective_capture_device_set(JLcom/vivox/service/vx_resp_aux_diagnostic_state_dump_t;JLcom/vivox/service/vx_device_t;)V
.end method

.method public static final native vx_resp_aux_diagnostic_state_dump_t_effective_render_device_get(JLcom/vivox/service/vx_resp_aux_diagnostic_state_dump_t;)J
.end method

.method public static final native vx_resp_aux_diagnostic_state_dump_t_effective_render_device_set(JLcom/vivox/service/vx_resp_aux_diagnostic_state_dump_t;JLcom/vivox/service/vx_device_t;)V
.end method

.method public static final native vx_resp_aux_diagnostic_state_dump_t_get_state_connectors_item(JLcom/vivox/service/vx_resp_aux_diagnostic_state_dump_t;I)J
.end method

.method public static final native vx_resp_aux_diagnostic_state_dump_t_state_connector_count_get(JLcom/vivox/service/vx_resp_aux_diagnostic_state_dump_t;)I
.end method

.method public static final native vx_resp_aux_diagnostic_state_dump_t_state_connector_count_set(JLcom/vivox/service/vx_resp_aux_diagnostic_state_dump_t;I)V
.end method

.method public static final native vx_resp_aux_diagnostic_state_dump_t_state_connectors_get(JLcom/vivox/service/vx_resp_aux_diagnostic_state_dump_t;)J
.end method

.method public static final native vx_resp_aux_diagnostic_state_dump_t_state_connectors_set(JLcom/vivox/service/vx_resp_aux_diagnostic_state_dump_t;J)V
.end method

.method public static final native vx_resp_aux_get_capture_devices_t_base_get(JLcom/vivox/service/vx_resp_aux_get_capture_devices_t;)J
.end method

.method public static final native vx_resp_aux_get_capture_devices_t_base_set(JLcom/vivox/service/vx_resp_aux_get_capture_devices_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_aux_get_capture_devices_t_capture_devices_get(JLcom/vivox/service/vx_resp_aux_get_capture_devices_t;)J
.end method

.method public static final native vx_resp_aux_get_capture_devices_t_capture_devices_set(JLcom/vivox/service/vx_resp_aux_get_capture_devices_t;J)V
.end method

.method public static final native vx_resp_aux_get_capture_devices_t_count_get(JLcom/vivox/service/vx_resp_aux_get_capture_devices_t;)I
.end method

.method public static final native vx_resp_aux_get_capture_devices_t_count_set(JLcom/vivox/service/vx_resp_aux_get_capture_devices_t;I)V
.end method

.method public static final native vx_resp_aux_get_capture_devices_t_current_capture_device_get(JLcom/vivox/service/vx_resp_aux_get_capture_devices_t;)J
.end method

.method public static final native vx_resp_aux_get_capture_devices_t_current_capture_device_set(JLcom/vivox/service/vx_resp_aux_get_capture_devices_t;JLcom/vivox/service/vx_device_t;)V
.end method

.method public static final native vx_resp_aux_get_capture_devices_t_effective_capture_device_get(JLcom/vivox/service/vx_resp_aux_get_capture_devices_t;)J
.end method

.method public static final native vx_resp_aux_get_capture_devices_t_effective_capture_device_set(JLcom/vivox/service/vx_resp_aux_get_capture_devices_t;JLcom/vivox/service/vx_device_t;)V
.end method

.method public static final native vx_resp_aux_get_capture_devices_t_get_capture_devices_item(JLcom/vivox/service/vx_resp_aux_get_capture_devices_t;I)J
.end method

.method public static final native vx_resp_aux_get_mic_level_t_base_get(JLcom/vivox/service/vx_resp_aux_get_mic_level_t;)J
.end method

.method public static final native vx_resp_aux_get_mic_level_t_base_set(JLcom/vivox/service/vx_resp_aux_get_mic_level_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_aux_get_mic_level_t_level_get(JLcom/vivox/service/vx_resp_aux_get_mic_level_t;)I
.end method

.method public static final native vx_resp_aux_get_mic_level_t_level_set(JLcom/vivox/service/vx_resp_aux_get_mic_level_t;I)V
.end method

.method public static final native vx_resp_aux_get_render_devices_t_base_get(JLcom/vivox/service/vx_resp_aux_get_render_devices_t;)J
.end method

.method public static final native vx_resp_aux_get_render_devices_t_base_set(JLcom/vivox/service/vx_resp_aux_get_render_devices_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_aux_get_render_devices_t_count_get(JLcom/vivox/service/vx_resp_aux_get_render_devices_t;)I
.end method

.method public static final native vx_resp_aux_get_render_devices_t_count_set(JLcom/vivox/service/vx_resp_aux_get_render_devices_t;I)V
.end method

.method public static final native vx_resp_aux_get_render_devices_t_current_render_device_get(JLcom/vivox/service/vx_resp_aux_get_render_devices_t;)J
.end method

.method public static final native vx_resp_aux_get_render_devices_t_current_render_device_set(JLcom/vivox/service/vx_resp_aux_get_render_devices_t;JLcom/vivox/service/vx_device_t;)V
.end method

.method public static final native vx_resp_aux_get_render_devices_t_effective_render_device_get(JLcom/vivox/service/vx_resp_aux_get_render_devices_t;)J
.end method

.method public static final native vx_resp_aux_get_render_devices_t_effective_render_device_set(JLcom/vivox/service/vx_resp_aux_get_render_devices_t;JLcom/vivox/service/vx_device_t;)V
.end method

.method public static final native vx_resp_aux_get_render_devices_t_get_render_devices_item(JLcom/vivox/service/vx_resp_aux_get_render_devices_t;I)J
.end method

.method public static final native vx_resp_aux_get_render_devices_t_render_devices_get(JLcom/vivox/service/vx_resp_aux_get_render_devices_t;)J
.end method

.method public static final native vx_resp_aux_get_render_devices_t_render_devices_set(JLcom/vivox/service/vx_resp_aux_get_render_devices_t;J)V
.end method

.method public static final native vx_resp_aux_get_speaker_level_t_base_get(JLcom/vivox/service/vx_resp_aux_get_speaker_level_t;)J
.end method

.method public static final native vx_resp_aux_get_speaker_level_t_base_set(JLcom/vivox/service/vx_resp_aux_get_speaker_level_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_aux_get_speaker_level_t_level_get(JLcom/vivox/service/vx_resp_aux_get_speaker_level_t;)I
.end method

.method public static final native vx_resp_aux_get_speaker_level_t_level_set(JLcom/vivox/service/vx_resp_aux_get_speaker_level_t;I)V
.end method

.method public static final native vx_resp_aux_get_vad_properties_t_base_get(JLcom/vivox/service/vx_resp_aux_get_vad_properties_t;)J
.end method

.method public static final native vx_resp_aux_get_vad_properties_t_base_set(JLcom/vivox/service/vx_resp_aux_get_vad_properties_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_aux_get_vad_properties_t_vad_hangover_get(JLcom/vivox/service/vx_resp_aux_get_vad_properties_t;)I
.end method

.method public static final native vx_resp_aux_get_vad_properties_t_vad_hangover_set(JLcom/vivox/service/vx_resp_aux_get_vad_properties_t;I)V
.end method

.method public static final native vx_resp_aux_get_vad_properties_t_vad_sensitivity_get(JLcom/vivox/service/vx_resp_aux_get_vad_properties_t;)I
.end method

.method public static final native vx_resp_aux_get_vad_properties_t_vad_sensitivity_set(JLcom/vivox/service/vx_resp_aux_get_vad_properties_t;I)V
.end method

.method public static final native vx_resp_aux_global_monitor_keyboard_mouse_t_base_get(JLcom/vivox/service/vx_resp_aux_global_monitor_keyboard_mouse_t;)J
.end method

.method public static final native vx_resp_aux_global_monitor_keyboard_mouse_t_base_set(JLcom/vivox/service/vx_resp_aux_global_monitor_keyboard_mouse_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_aux_notify_application_state_change_t_base_get(JLcom/vivox/service/vx_resp_aux_notify_application_state_change_t;)J
.end method

.method public static final native vx_resp_aux_notify_application_state_change_t_base_set(JLcom/vivox/service/vx_resp_aux_notify_application_state_change_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_aux_play_audio_buffer_t_base_get(JLcom/vivox/service/vx_resp_aux_play_audio_buffer_t;)J
.end method

.method public static final native vx_resp_aux_play_audio_buffer_t_base_set(JLcom/vivox/service/vx_resp_aux_play_audio_buffer_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_aux_reactivate_account_t_base_get(JLcom/vivox/service/vx_resp_aux_reactivate_account_t;)J
.end method

.method public static final native vx_resp_aux_reactivate_account_t_base_set(JLcom/vivox/service/vx_resp_aux_reactivate_account_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_aux_render_audio_modify_t_base_get(JLcom/vivox/service/vx_resp_aux_render_audio_modify_t;)J
.end method

.method public static final native vx_resp_aux_render_audio_modify_t_base_set(JLcom/vivox/service/vx_resp_aux_render_audio_modify_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_aux_render_audio_start_t_base_get(JLcom/vivox/service/vx_resp_aux_render_audio_start_t;)J
.end method

.method public static final native vx_resp_aux_render_audio_start_t_base_set(JLcom/vivox/service/vx_resp_aux_render_audio_start_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_aux_render_audio_stop_t_base_get(JLcom/vivox/service/vx_resp_aux_render_audio_stop_t;)J
.end method

.method public static final native vx_resp_aux_render_audio_stop_t_base_set(JLcom/vivox/service/vx_resp_aux_render_audio_stop_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_aux_reset_password_t_base_get(JLcom/vivox/service/vx_resp_aux_reset_password_t;)J
.end method

.method public static final native vx_resp_aux_reset_password_t_base_set(JLcom/vivox/service/vx_resp_aux_reset_password_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_aux_set_capture_device_t_base_get(JLcom/vivox/service/vx_resp_aux_set_capture_device_t;)J
.end method

.method public static final native vx_resp_aux_set_capture_device_t_base_set(JLcom/vivox/service/vx_resp_aux_set_capture_device_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_aux_set_idle_timeout_t_base_get(JLcom/vivox/service/vx_resp_aux_set_idle_timeout_t;)J
.end method

.method public static final native vx_resp_aux_set_idle_timeout_t_base_set(JLcom/vivox/service/vx_resp_aux_set_idle_timeout_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_aux_set_mic_level_t_base_get(JLcom/vivox/service/vx_resp_aux_set_mic_level_t;)J
.end method

.method public static final native vx_resp_aux_set_mic_level_t_base_set(JLcom/vivox/service/vx_resp_aux_set_mic_level_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_aux_set_render_device_t_base_get(JLcom/vivox/service/vx_resp_aux_set_render_device_t;)J
.end method

.method public static final native vx_resp_aux_set_render_device_t_base_set(JLcom/vivox/service/vx_resp_aux_set_render_device_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_aux_set_speaker_level_t_base_get(JLcom/vivox/service/vx_resp_aux_set_speaker_level_t;)J
.end method

.method public static final native vx_resp_aux_set_speaker_level_t_base_set(JLcom/vivox/service/vx_resp_aux_set_speaker_level_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_aux_set_vad_properties_t_base_get(JLcom/vivox/service/vx_resp_aux_set_vad_properties_t;)J
.end method

.method public static final native vx_resp_aux_set_vad_properties_t_base_set(JLcom/vivox/service/vx_resp_aux_set_vad_properties_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_aux_start_buffer_capture_t_base_get(JLcom/vivox/service/vx_resp_aux_start_buffer_capture_t;)J
.end method

.method public static final native vx_resp_aux_start_buffer_capture_t_base_set(JLcom/vivox/service/vx_resp_aux_start_buffer_capture_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_base_t_extended_status_info_get(JLcom/vivox/service/vx_resp_base_t;)Ljava/lang/String;
.end method

.method public static final native vx_resp_base_t_extended_status_info_set(JLcom/vivox/service/vx_resp_base_t;Ljava/lang/String;)V
.end method

.method public static final native vx_resp_base_t_message_get(JLcom/vivox/service/vx_resp_base_t;)J
.end method

.method public static final native vx_resp_base_t_message_set(JLcom/vivox/service/vx_resp_base_t;JLcom/vivox/service/vx_message_base_t;)V
.end method

.method public static final native vx_resp_base_t_request_get(JLcom/vivox/service/vx_resp_base_t;)J
.end method

.method public static final native vx_resp_base_t_request_set(JLcom/vivox/service/vx_resp_base_t;JLcom/vivox/service/vx_req_base_t;)V
.end method

.method public static final native vx_resp_base_t_return_code_get(JLcom/vivox/service/vx_resp_base_t;)I
.end method

.method public static final native vx_resp_base_t_return_code_set(JLcom/vivox/service/vx_resp_base_t;I)V
.end method

.method public static final native vx_resp_base_t_status_code_get(JLcom/vivox/service/vx_resp_base_t;)I
.end method

.method public static final native vx_resp_base_t_status_code_set(JLcom/vivox/service/vx_resp_base_t;I)V
.end method

.method public static final native vx_resp_base_t_status_string_get(JLcom/vivox/service/vx_resp_base_t;)Ljava/lang/String;
.end method

.method public static final native vx_resp_base_t_status_string_set(JLcom/vivox/service/vx_resp_base_t;Ljava/lang/String;)V
.end method

.method public static final native vx_resp_base_t_type_get(JLcom/vivox/service/vx_resp_base_t;)I
.end method

.method public static final native vx_resp_base_t_type_set(JLcom/vivox/service/vx_resp_base_t;I)V
.end method

.method public static final native vx_resp_channel_ban_user_t_base_get(JLcom/vivox/service/vx_resp_channel_ban_user_t;)J
.end method

.method public static final native vx_resp_channel_ban_user_t_base_set(JLcom/vivox/service/vx_resp_channel_ban_user_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_channel_get_banned_users_t_banned_users_count_get(JLcom/vivox/service/vx_resp_channel_get_banned_users_t;)I
.end method

.method public static final native vx_resp_channel_get_banned_users_t_banned_users_count_set(JLcom/vivox/service/vx_resp_channel_get_banned_users_t;I)V
.end method

.method public static final native vx_resp_channel_get_banned_users_t_banned_users_get(JLcom/vivox/service/vx_resp_channel_get_banned_users_t;)J
.end method

.method public static final native vx_resp_channel_get_banned_users_t_banned_users_set(JLcom/vivox/service/vx_resp_channel_get_banned_users_t;J)V
.end method

.method public static final native vx_resp_channel_get_banned_users_t_base_get(JLcom/vivox/service/vx_resp_channel_get_banned_users_t;)J
.end method

.method public static final native vx_resp_channel_get_banned_users_t_base_set(JLcom/vivox/service/vx_resp_channel_get_banned_users_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_channel_get_banned_users_t_get_banned_users_item(JLcom/vivox/service/vx_resp_channel_get_banned_users_t;I)J
.end method

.method public static final native vx_resp_channel_kick_user_t_base_get(JLcom/vivox/service/vx_resp_channel_kick_user_t;)J
.end method

.method public static final native vx_resp_channel_kick_user_t_base_set(JLcom/vivox/service/vx_resp_channel_kick_user_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_channel_mute_all_users_t_base_get(JLcom/vivox/service/vx_resp_channel_mute_all_users_t;)J
.end method

.method public static final native vx_resp_channel_mute_all_users_t_base_set(JLcom/vivox/service/vx_resp_channel_mute_all_users_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_channel_mute_user_t_base_get(JLcom/vivox/service/vx_resp_channel_mute_user_t;)J
.end method

.method public static final native vx_resp_channel_mute_user_t_base_set(JLcom/vivox/service/vx_resp_channel_mute_user_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_channel_set_lock_mode_t_base_get(JLcom/vivox/service/vx_resp_channel_set_lock_mode_t;)J
.end method

.method public static final native vx_resp_channel_set_lock_mode_t_base_set(JLcom/vivox/service/vx_resp_channel_set_lock_mode_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_connector_create_t_base_get(JLcom/vivox/service/vx_resp_connector_create_t;)J
.end method

.method public static final native vx_resp_connector_create_t_base_set(JLcom/vivox/service/vx_resp_connector_create_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_connector_create_t_connector_handle_get(JLcom/vivox/service/vx_resp_connector_create_t;)Ljava/lang/String;
.end method

.method public static final native vx_resp_connector_create_t_connector_handle_set(JLcom/vivox/service/vx_resp_connector_create_t;Ljava/lang/String;)V
.end method

.method public static final native vx_resp_connector_create_t_version_id_get(JLcom/vivox/service/vx_resp_connector_create_t;)Ljava/lang/String;
.end method

.method public static final native vx_resp_connector_create_t_version_id_set(JLcom/vivox/service/vx_resp_connector_create_t;Ljava/lang/String;)V
.end method

.method public static final native vx_resp_connector_get_local_audio_info_t_base_get(JLcom/vivox/service/vx_resp_connector_get_local_audio_info_t;)J
.end method

.method public static final native vx_resp_connector_get_local_audio_info_t_base_set(JLcom/vivox/service/vx_resp_connector_get_local_audio_info_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_connector_get_local_audio_info_t_is_mic_muted_get(JLcom/vivox/service/vx_resp_connector_get_local_audio_info_t;)I
.end method

.method public static final native vx_resp_connector_get_local_audio_info_t_is_mic_muted_set(JLcom/vivox/service/vx_resp_connector_get_local_audio_info_t;I)V
.end method

.method public static final native vx_resp_connector_get_local_audio_info_t_is_speaker_muted_get(JLcom/vivox/service/vx_resp_connector_get_local_audio_info_t;)I
.end method

.method public static final native vx_resp_connector_get_local_audio_info_t_is_speaker_muted_set(JLcom/vivox/service/vx_resp_connector_get_local_audio_info_t;I)V
.end method

.method public static final native vx_resp_connector_get_local_audio_info_t_mic_volume_get(JLcom/vivox/service/vx_resp_connector_get_local_audio_info_t;)I
.end method

.method public static final native vx_resp_connector_get_local_audio_info_t_mic_volume_set(JLcom/vivox/service/vx_resp_connector_get_local_audio_info_t;I)V
.end method

.method public static final native vx_resp_connector_get_local_audio_info_t_speaker_volume_get(JLcom/vivox/service/vx_resp_connector_get_local_audio_info_t;)I
.end method

.method public static final native vx_resp_connector_get_local_audio_info_t_speaker_volume_set(JLcom/vivox/service/vx_resp_connector_get_local_audio_info_t;I)V
.end method

.method public static final native vx_resp_connector_initiate_shutdown_t_base_get(JLcom/vivox/service/vx_resp_connector_initiate_shutdown_t;)J
.end method

.method public static final native vx_resp_connector_initiate_shutdown_t_base_set(JLcom/vivox/service/vx_resp_connector_initiate_shutdown_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_connector_initiate_shutdown_t_client_name_get(JLcom/vivox/service/vx_resp_connector_initiate_shutdown_t;)Ljava/lang/String;
.end method

.method public static final native vx_resp_connector_initiate_shutdown_t_client_name_set(JLcom/vivox/service/vx_resp_connector_initiate_shutdown_t;Ljava/lang/String;)V
.end method

.method public static final native vx_resp_connector_mute_local_mic_t_base_get(JLcom/vivox/service/vx_resp_connector_mute_local_mic_t;)J
.end method

.method public static final native vx_resp_connector_mute_local_mic_t_base_set(JLcom/vivox/service/vx_resp_connector_mute_local_mic_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_connector_mute_local_speaker_t_base_get(JLcom/vivox/service/vx_resp_connector_mute_local_speaker_t;)J
.end method

.method public static final native vx_resp_connector_mute_local_speaker_t_base_set(JLcom/vivox/service/vx_resp_connector_mute_local_speaker_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_connector_set_local_mic_volume_t_base_get(JLcom/vivox/service/vx_resp_connector_set_local_mic_volume_t;)J
.end method

.method public static final native vx_resp_connector_set_local_mic_volume_t_base_set(JLcom/vivox/service/vx_resp_connector_set_local_mic_volume_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_connector_set_local_speaker_volume_t_base_get(JLcom/vivox/service/vx_resp_connector_set_local_speaker_volume_t;)J
.end method

.method public static final native vx_resp_connector_set_local_speaker_volume_t_base_set(JLcom/vivox/service/vx_resp_connector_set_local_speaker_volume_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_session_channel_invite_user_t_base_get(JLcom/vivox/service/vx_resp_session_channel_invite_user_t;)J
.end method

.method public static final native vx_resp_session_channel_invite_user_t_base_set(JLcom/vivox/service/vx_resp_session_channel_invite_user_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_session_create_t_base_get(JLcom/vivox/service/vx_resp_session_create_t;)J
.end method

.method public static final native vx_resp_session_create_t_base_set(JLcom/vivox/service/vx_resp_session_create_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_session_create_t_session_handle_get(JLcom/vivox/service/vx_resp_session_create_t;)Ljava/lang/String;
.end method

.method public static final native vx_resp_session_create_t_session_handle_set(JLcom/vivox/service/vx_resp_session_create_t;Ljava/lang/String;)V
.end method

.method public static final native vx_resp_session_create_t_sessiongroup_handle_get(JLcom/vivox/service/vx_resp_session_create_t;)Ljava/lang/String;
.end method

.method public static final native vx_resp_session_create_t_sessiongroup_handle_set(JLcom/vivox/service/vx_resp_session_create_t;Ljava/lang/String;)V
.end method

.method public static final native vx_resp_session_media_connect_t_base_get(JLcom/vivox/service/vx_resp_session_media_connect_t;)J
.end method

.method public static final native vx_resp_session_media_connect_t_base_set(JLcom/vivox/service/vx_resp_session_media_connect_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_session_media_disconnect_t_base_get(JLcom/vivox/service/vx_resp_session_media_disconnect_t;)J
.end method

.method public static final native vx_resp_session_media_disconnect_t_base_set(JLcom/vivox/service/vx_resp_session_media_disconnect_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_session_mute_local_speaker_t_base_get(JLcom/vivox/service/vx_resp_session_mute_local_speaker_t;)J
.end method

.method public static final native vx_resp_session_mute_local_speaker_t_base_set(JLcom/vivox/service/vx_resp_session_mute_local_speaker_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_session_send_dtmf_t_base_get(JLcom/vivox/service/vx_resp_session_send_dtmf_t;)J
.end method

.method public static final native vx_resp_session_send_dtmf_t_base_set(JLcom/vivox/service/vx_resp_session_send_dtmf_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_session_send_message_t_base_get(JLcom/vivox/service/vx_resp_session_send_message_t;)J
.end method

.method public static final native vx_resp_session_send_message_t_base_set(JLcom/vivox/service/vx_resp_session_send_message_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_session_send_notification_t_base_get(JLcom/vivox/service/vx_resp_session_send_notification_t;)J
.end method

.method public static final native vx_resp_session_send_notification_t_base_set(JLcom/vivox/service/vx_resp_session_send_notification_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_session_set_3d_position_t_base_get(JLcom/vivox/service/vx_resp_session_set_3d_position_t;)J
.end method

.method public static final native vx_resp_session_set_3d_position_t_base_set(JLcom/vivox/service/vx_resp_session_set_3d_position_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_session_set_local_speaker_volume_t_base_get(JLcom/vivox/service/vx_resp_session_set_local_speaker_volume_t;)J
.end method

.method public static final native vx_resp_session_set_local_speaker_volume_t_base_set(JLcom/vivox/service/vx_resp_session_set_local_speaker_volume_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_session_set_participant_mute_for_me_t_base_get(JLcom/vivox/service/vx_resp_session_set_participant_mute_for_me_t;)J
.end method

.method public static final native vx_resp_session_set_participant_mute_for_me_t_base_set(JLcom/vivox/service/vx_resp_session_set_participant_mute_for_me_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_session_set_participant_volume_for_me_t_base_get(JLcom/vivox/service/vx_resp_session_set_participant_volume_for_me_t;)J
.end method

.method public static final native vx_resp_session_set_participant_volume_for_me_t_base_set(JLcom/vivox/service/vx_resp_session_set_participant_volume_for_me_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_session_set_voice_font_t_base_get(JLcom/vivox/service/vx_resp_session_set_voice_font_t;)J
.end method

.method public static final native vx_resp_session_set_voice_font_t_base_set(JLcom/vivox/service/vx_resp_session_set_voice_font_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_session_terminate_t_base_get(JLcom/vivox/service/vx_resp_session_terminate_t;)J
.end method

.method public static final native vx_resp_session_terminate_t_base_set(JLcom/vivox/service/vx_resp_session_terminate_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_session_text_connect_t_base_get(JLcom/vivox/service/vx_resp_session_text_connect_t;)J
.end method

.method public static final native vx_resp_session_text_connect_t_base_set(JLcom/vivox/service/vx_resp_session_text_connect_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_session_text_disconnect_t_base_get(JLcom/vivox/service/vx_resp_session_text_disconnect_t;)J
.end method

.method public static final native vx_resp_session_text_disconnect_t_base_set(JLcom/vivox/service/vx_resp_session_text_disconnect_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_sessiongroup_add_session_t_base_get(JLcom/vivox/service/vx_resp_sessiongroup_add_session_t;)J
.end method

.method public static final native vx_resp_sessiongroup_add_session_t_base_set(JLcom/vivox/service/vx_resp_sessiongroup_add_session_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_sessiongroup_add_session_t_session_handle_get(JLcom/vivox/service/vx_resp_sessiongroup_add_session_t;)Ljava/lang/String;
.end method

.method public static final native vx_resp_sessiongroup_add_session_t_session_handle_set(JLcom/vivox/service/vx_resp_sessiongroup_add_session_t;Ljava/lang/String;)V
.end method

.method public static final native vx_resp_sessiongroup_control_audio_injection_t_base_get(JLcom/vivox/service/vx_resp_sessiongroup_control_audio_injection_t;)J
.end method

.method public static final native vx_resp_sessiongroup_control_audio_injection_t_base_set(JLcom/vivox/service/vx_resp_sessiongroup_control_audio_injection_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_sessiongroup_control_playback_t_base_get(JLcom/vivox/service/vx_resp_sessiongroup_control_playback_t;)J
.end method

.method public static final native vx_resp_sessiongroup_control_playback_t_base_set(JLcom/vivox/service/vx_resp_sessiongroup_control_playback_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_sessiongroup_control_recording_t_base_get(JLcom/vivox/service/vx_resp_sessiongroup_control_recording_t;)J
.end method

.method public static final native vx_resp_sessiongroup_control_recording_t_base_set(JLcom/vivox/service/vx_resp_sessiongroup_control_recording_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_sessiongroup_create_t_base_get(JLcom/vivox/service/vx_resp_sessiongroup_create_t;)J
.end method

.method public static final native vx_resp_sessiongroup_create_t_base_set(JLcom/vivox/service/vx_resp_sessiongroup_create_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_sessiongroup_create_t_sessiongroup_handle_get(JLcom/vivox/service/vx_resp_sessiongroup_create_t;)Ljava/lang/String;
.end method

.method public static final native vx_resp_sessiongroup_create_t_sessiongroup_handle_set(JLcom/vivox/service/vx_resp_sessiongroup_create_t;Ljava/lang/String;)V
.end method

.method public static final native vx_resp_sessiongroup_get_stats_t_base_get(JLcom/vivox/service/vx_resp_sessiongroup_get_stats_t;)J
.end method

.method public static final native vx_resp_sessiongroup_get_stats_t_base_set(JLcom/vivox/service/vx_resp_sessiongroup_get_stats_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_sessiongroup_get_stats_t_call_id_get(JLcom/vivox/service/vx_resp_sessiongroup_get_stats_t;)Ljava/lang/String;
.end method

.method public static final native vx_resp_sessiongroup_get_stats_t_call_id_set(JLcom/vivox/service/vx_resp_sessiongroup_get_stats_t;Ljava/lang/String;)V
.end method

.method public static final native vx_resp_sessiongroup_get_stats_t_current_bars_get(JLcom/vivox/service/vx_resp_sessiongroup_get_stats_t;)I
.end method

.method public static final native vx_resp_sessiongroup_get_stats_t_current_bars_set(JLcom/vivox/service/vx_resp_sessiongroup_get_stats_t;I)V
.end method

.method public static final native vx_resp_sessiongroup_get_stats_t_incoming_discarded_get(JLcom/vivox/service/vx_resp_sessiongroup_get_stats_t;)I
.end method

.method public static final native vx_resp_sessiongroup_get_stats_t_incoming_discarded_set(JLcom/vivox/service/vx_resp_sessiongroup_get_stats_t;I)V
.end method

.method public static final native vx_resp_sessiongroup_get_stats_t_incoming_expected_get(JLcom/vivox/service/vx_resp_sessiongroup_get_stats_t;)I
.end method

.method public static final native vx_resp_sessiongroup_get_stats_t_incoming_expected_set(JLcom/vivox/service/vx_resp_sessiongroup_get_stats_t;I)V
.end method

.method public static final native vx_resp_sessiongroup_get_stats_t_incoming_out_of_time_get(JLcom/vivox/service/vx_resp_sessiongroup_get_stats_t;)I
.end method

.method public static final native vx_resp_sessiongroup_get_stats_t_incoming_out_of_time_set(JLcom/vivox/service/vx_resp_sessiongroup_get_stats_t;I)V
.end method

.method public static final native vx_resp_sessiongroup_get_stats_t_incoming_packetloss_get(JLcom/vivox/service/vx_resp_sessiongroup_get_stats_t;)I
.end method

.method public static final native vx_resp_sessiongroup_get_stats_t_incoming_packetloss_set(JLcom/vivox/service/vx_resp_sessiongroup_get_stats_t;I)V
.end method

.method public static final native vx_resp_sessiongroup_get_stats_t_incoming_received_get(JLcom/vivox/service/vx_resp_sessiongroup_get_stats_t;)I
.end method

.method public static final native vx_resp_sessiongroup_get_stats_t_incoming_received_set(JLcom/vivox/service/vx_resp_sessiongroup_get_stats_t;I)V
.end method

.method public static final native vx_resp_sessiongroup_get_stats_t_insufficient_bandwidth_get(JLcom/vivox/service/vx_resp_sessiongroup_get_stats_t;)I
.end method

.method public static final native vx_resp_sessiongroup_get_stats_t_insufficient_bandwidth_set(JLcom/vivox/service/vx_resp_sessiongroup_get_stats_t;I)V
.end method

.method public static final native vx_resp_sessiongroup_get_stats_t_max_bars_get(JLcom/vivox/service/vx_resp_sessiongroup_get_stats_t;)I
.end method

.method public static final native vx_resp_sessiongroup_get_stats_t_max_bars_set(JLcom/vivox/service/vx_resp_sessiongroup_get_stats_t;I)V
.end method

.method public static final native vx_resp_sessiongroup_get_stats_t_min_bars_get(JLcom/vivox/service/vx_resp_sessiongroup_get_stats_t;)I
.end method

.method public static final native vx_resp_sessiongroup_get_stats_t_min_bars_set(JLcom/vivox/service/vx_resp_sessiongroup_get_stats_t;I)V
.end method

.method public static final native vx_resp_sessiongroup_get_stats_t_outgoing_sent_get(JLcom/vivox/service/vx_resp_sessiongroup_get_stats_t;)I
.end method

.method public static final native vx_resp_sessiongroup_get_stats_t_outgoing_sent_set(JLcom/vivox/service/vx_resp_sessiongroup_get_stats_t;I)V
.end method

.method public static final native vx_resp_sessiongroup_get_stats_t_pk_loss_get(JLcom/vivox/service/vx_resp_sessiongroup_get_stats_t;)I
.end method

.method public static final native vx_resp_sessiongroup_get_stats_t_pk_loss_set(JLcom/vivox/service/vx_resp_sessiongroup_get_stats_t;I)V
.end method

.method public static final native vx_resp_sessiongroup_get_stats_t_render_device_errors_get(JLcom/vivox/service/vx_resp_sessiongroup_get_stats_t;)I
.end method

.method public static final native vx_resp_sessiongroup_get_stats_t_render_device_errors_set(JLcom/vivox/service/vx_resp_sessiongroup_get_stats_t;I)V
.end method

.method public static final native vx_resp_sessiongroup_get_stats_t_render_device_overruns_get(JLcom/vivox/service/vx_resp_sessiongroup_get_stats_t;)I
.end method

.method public static final native vx_resp_sessiongroup_get_stats_t_render_device_overruns_set(JLcom/vivox/service/vx_resp_sessiongroup_get_stats_t;I)V
.end method

.method public static final native vx_resp_sessiongroup_get_stats_t_render_device_underruns_get(JLcom/vivox/service/vx_resp_sessiongroup_get_stats_t;)I
.end method

.method public static final native vx_resp_sessiongroup_get_stats_t_render_device_underruns_set(JLcom/vivox/service/vx_resp_sessiongroup_get_stats_t;I)V
.end method

.method public static final native vx_resp_sessiongroup_remove_session_t_base_get(JLcom/vivox/service/vx_resp_sessiongroup_remove_session_t;)J
.end method

.method public static final native vx_resp_sessiongroup_remove_session_t_base_set(JLcom/vivox/service/vx_resp_sessiongroup_remove_session_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_sessiongroup_reset_focus_t_base_get(JLcom/vivox/service/vx_resp_sessiongroup_reset_focus_t;)J
.end method

.method public static final native vx_resp_sessiongroup_reset_focus_t_base_set(JLcom/vivox/service/vx_resp_sessiongroup_reset_focus_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_sessiongroup_set_focus_t_base_get(JLcom/vivox/service/vx_resp_sessiongroup_set_focus_t;)J
.end method

.method public static final native vx_resp_sessiongroup_set_focus_t_base_set(JLcom/vivox/service/vx_resp_sessiongroup_set_focus_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_sessiongroup_set_playback_options_t_base_get(JLcom/vivox/service/vx_resp_sessiongroup_set_playback_options_t;)J
.end method

.method public static final native vx_resp_sessiongroup_set_playback_options_t_base_set(JLcom/vivox/service/vx_resp_sessiongroup_set_playback_options_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_sessiongroup_set_session_3d_position_t_base_get(JLcom/vivox/service/vx_resp_sessiongroup_set_session_3d_position_t;)J
.end method

.method public static final native vx_resp_sessiongroup_set_session_3d_position_t_base_set(JLcom/vivox/service/vx_resp_sessiongroup_set_session_3d_position_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_sessiongroup_set_tx_all_sessions_t_base_get(JLcom/vivox/service/vx_resp_sessiongroup_set_tx_all_sessions_t;)J
.end method

.method public static final native vx_resp_sessiongroup_set_tx_all_sessions_t_base_set(JLcom/vivox/service/vx_resp_sessiongroup_set_tx_all_sessions_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_sessiongroup_set_tx_no_session_t_base_get(JLcom/vivox/service/vx_resp_sessiongroup_set_tx_no_session_t;)J
.end method

.method public static final native vx_resp_sessiongroup_set_tx_no_session_t_base_set(JLcom/vivox/service/vx_resp_sessiongroup_set_tx_no_session_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_sessiongroup_set_tx_session_t_base_get(JLcom/vivox/service/vx_resp_sessiongroup_set_tx_session_t;)J
.end method

.method public static final native vx_resp_sessiongroup_set_tx_session_t_base_set(JLcom/vivox/service/vx_resp_sessiongroup_set_tx_session_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_sessiongroup_terminate_t_base_get(JLcom/vivox/service/vx_resp_sessiongroup_terminate_t;)J
.end method

.method public static final native vx_resp_sessiongroup_terminate_t_base_set(JLcom/vivox/service/vx_resp_sessiongroup_terminate_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_resp_sessiongroup_unset_focus_t_base_get(JLcom/vivox/service/vx_resp_sessiongroup_unset_focus_t;)J
.end method

.method public static final native vx_resp_sessiongroup_unset_focus_t_base_set(JLcom/vivox/service/vx_resp_sessiongroup_unset_focus_t;JLcom/vivox/service/vx_resp_base_t;)V
.end method

.method public static final native vx_response_to_xml(JJ)V
.end method

.method public static final native vx_sdk_config_t_allow_shared_capture_devices_get(JLcom/vivox/service/vx_sdk_config_t;)I
.end method

.method public static final native vx_sdk_config_t_allow_shared_capture_devices_set(JLcom/vivox/service/vx_sdk_config_t;I)V
.end method

.method public static final native vx_sdk_config_t_app_id_get(JLcom/vivox/service/vx_sdk_config_t;)Ljava/lang/String;
.end method

.method public static final native vx_sdk_config_t_app_id_set(JLcom/vivox/service/vx_sdk_config_t;Ljava/lang/String;)V
.end method

.method public static final native vx_sdk_config_t_cert_data_dir_get(JLcom/vivox/service/vx_sdk_config_t;)Ljava/lang/String;
.end method

.method public static final native vx_sdk_config_t_cert_data_dir_set(JLcom/vivox/service/vx_sdk_config_t;Ljava/lang/String;)V
.end method

.method public static final native vx_sdk_config_t_max_logins_per_user_get(JLcom/vivox/service/vx_sdk_config_t;)I
.end method

.method public static final native vx_sdk_config_t_max_logins_per_user_set(JLcom/vivox/service/vx_sdk_config_t;I)V
.end method

.method public static final native vx_sdk_config_t_num_codec_threads_get(JLcom/vivox/service/vx_sdk_config_t;)I
.end method

.method public static final native vx_sdk_config_t_num_codec_threads_set(JLcom/vivox/service/vx_sdk_config_t;I)V
.end method

.method public static final native vx_sdk_config_t_num_voice_threads_get(JLcom/vivox/service/vx_sdk_config_t;)I
.end method

.method public static final native vx_sdk_config_t_num_voice_threads_set(JLcom/vivox/service/vx_sdk_config_t;I)V
.end method

.method public static final native vx_sdk_config_t_num_web_threads_get(JLcom/vivox/service/vx_sdk_config_t;)I
.end method

.method public static final native vx_sdk_config_t_num_web_threads_set(JLcom/vivox/service/vx_sdk_config_t;I)V
.end method

.method public static final native vx_sdk_config_t_render_source_initial_buffer_count_get(JLcom/vivox/service/vx_sdk_config_t;)I
.end method

.method public static final native vx_sdk_config_t_render_source_initial_buffer_count_set(JLcom/vivox/service/vx_sdk_config_t;I)V
.end method

.method public static final native vx_sdk_config_t_render_source_queue_depth_max_get(JLcom/vivox/service/vx_sdk_config_t;)I
.end method

.method public static final native vx_sdk_config_t_render_source_queue_depth_max_set(JLcom/vivox/service/vx_sdk_config_t;I)V
.end method

.method public static final native vx_sdk_config_t_upstream_jitter_frame_count_get(JLcom/vivox/service/vx_sdk_config_t;)I
.end method

.method public static final native vx_sdk_config_t_upstream_jitter_frame_count_set(JLcom/vivox/service/vx_sdk_config_t;I)V
.end method

.method public static final native vx_set_cert_data(Ljava/lang/String;)I
.end method

.method public static final native vx_set_cert_data_dir(Ljava/lang/String;)I
.end method

.method public static final native vx_set_crash_dump_generation_enabled(I)V
.end method

.method public static final native vx_set_out_of_process_server_address(Ljava/lang/String;I)I
.end method

.method public static final native vx_set_preferred_codec(I)I
.end method

.method public static final native vx_stat_sample_t_last_get(JLcom/vivox/service/vx_stat_sample_t;)D
.end method

.method public static final native vx_stat_sample_t_last_set(JLcom/vivox/service/vx_stat_sample_t;D)V
.end method

.method public static final native vx_stat_sample_t_max_get(JLcom/vivox/service/vx_stat_sample_t;)D
.end method

.method public static final native vx_stat_sample_t_max_set(JLcom/vivox/service/vx_stat_sample_t;D)V
.end method

.method public static final native vx_stat_sample_t_mean_get(JLcom/vivox/service/vx_stat_sample_t;)D
.end method

.method public static final native vx_stat_sample_t_mean_set(JLcom/vivox/service/vx_stat_sample_t;D)V
.end method

.method public static final native vx_stat_sample_t_min_get(JLcom/vivox/service/vx_stat_sample_t;)D
.end method

.method public static final native vx_stat_sample_t_min_set(JLcom/vivox/service/vx_stat_sample_t;D)V
.end method

.method public static final native vx_stat_sample_t_sample_count_get(JLcom/vivox/service/vx_stat_sample_t;)D
.end method

.method public static final native vx_stat_sample_t_sample_count_set(JLcom/vivox/service/vx_stat_sample_t;D)V
.end method

.method public static final native vx_stat_sample_t_stddev_get(JLcom/vivox/service/vx_stat_sample_t;)D
.end method

.method public static final native vx_stat_sample_t_stddev_set(JLcom/vivox/service/vx_stat_sample_t;D)V
.end method

.method public static final native vx_stat_sample_t_sum_get(JLcom/vivox/service/vx_stat_sample_t;)D
.end method

.method public static final native vx_stat_sample_t_sum_of_squares_get(JLcom/vivox/service/vx_stat_sample_t;)D
.end method

.method public static final native vx_stat_sample_t_sum_of_squares_set(JLcom/vivox/service/vx_stat_sample_t;D)V
.end method

.method public static final native vx_stat_sample_t_sum_set(JLcom/vivox/service/vx_stat_sample_t;D)V
.end method

.method public static final native vx_stat_thread_t_count_poll_gte_25ms_get(JLcom/vivox/service/vx_stat_thread_t;)I
.end method

.method public static final native vx_stat_thread_t_count_poll_gte_25ms_set(JLcom/vivox/service/vx_stat_thread_t;I)V
.end method

.method public static final native vx_stat_thread_t_count_poll_lt_10ms_get(JLcom/vivox/service/vx_stat_thread_t;)I
.end method

.method public static final native vx_stat_thread_t_count_poll_lt_10ms_set(JLcom/vivox/service/vx_stat_thread_t;I)V
.end method

.method public static final native vx_stat_thread_t_count_poll_lt_16ms_get(JLcom/vivox/service/vx_stat_thread_t;)I
.end method

.method public static final native vx_stat_thread_t_count_poll_lt_16ms_set(JLcom/vivox/service/vx_stat_thread_t;I)V
.end method

.method public static final native vx_stat_thread_t_count_poll_lt_1ms_get(JLcom/vivox/service/vx_stat_thread_t;)I
.end method

.method public static final native vx_stat_thread_t_count_poll_lt_1ms_set(JLcom/vivox/service/vx_stat_thread_t;I)V
.end method

.method public static final native vx_stat_thread_t_count_poll_lt_20ms_get(JLcom/vivox/service/vx_stat_thread_t;)I
.end method

.method public static final native vx_stat_thread_t_count_poll_lt_20ms_set(JLcom/vivox/service/vx_stat_thread_t;I)V
.end method

.method public static final native vx_stat_thread_t_count_poll_lt_25ms_get(JLcom/vivox/service/vx_stat_thread_t;)I
.end method

.method public static final native vx_stat_thread_t_count_poll_lt_25ms_set(JLcom/vivox/service/vx_stat_thread_t;I)V
.end method

.method public static final native vx_stat_thread_t_count_poll_lt_5ms_get(JLcom/vivox/service/vx_stat_thread_t;)I
.end method

.method public static final native vx_stat_thread_t_count_poll_lt_5ms_set(JLcom/vivox/service/vx_stat_thread_t;I)V
.end method

.method public static final native vx_stat_thread_t_interval_get(JLcom/vivox/service/vx_stat_thread_t;)I
.end method

.method public static final native vx_stat_thread_t_interval_set(JLcom/vivox/service/vx_stat_thread_t;I)V
.end method

.method public static final native vx_state_account_create(J)V
.end method

.method public static final native vx_state_account_free(JLcom/vivox/service/vx_state_account_t;)V
.end method

.method public static final native vx_state_account_list_create(IJ)V
.end method

.method public static final native vx_state_account_list_free(JI)V
.end method

.method public static final native vx_state_account_t_account_handle_get(JLcom/vivox/service/vx_state_account_t;)Ljava/lang/String;
.end method

.method public static final native vx_state_account_t_account_handle_set(JLcom/vivox/service/vx_state_account_t;Ljava/lang/String;)V
.end method

.method public static final native vx_state_account_t_account_uri_get(JLcom/vivox/service/vx_state_account_t;)Ljava/lang/String;
.end method

.method public static final native vx_state_account_t_account_uri_set(JLcom/vivox/service/vx_state_account_t;Ljava/lang/String;)V
.end method

.method public static final native vx_state_account_t_display_name_get(JLcom/vivox/service/vx_state_account_t;)Ljava/lang/String;
.end method

.method public static final native vx_state_account_t_display_name_set(JLcom/vivox/service/vx_state_account_t;Ljava/lang/String;)V
.end method

.method public static final native vx_state_account_t_get_state_sessiongroups_item(JLcom/vivox/service/vx_state_account_t;I)J
.end method

.method public static final native vx_state_account_t_is_anonymous_login_get(JLcom/vivox/service/vx_state_account_t;)I
.end method

.method public static final native vx_state_account_t_is_anonymous_login_set(JLcom/vivox/service/vx_state_account_t;I)V
.end method

.method public static final native vx_state_account_t_state_buddies_get(JLcom/vivox/service/vx_state_account_t;)J
.end method

.method public static final native vx_state_account_t_state_buddies_set(JLcom/vivox/service/vx_state_account_t;J)V
.end method

.method public static final native vx_state_account_t_state_buddy_count_get(JLcom/vivox/service/vx_state_account_t;)I
.end method

.method public static final native vx_state_account_t_state_buddy_count_set(JLcom/vivox/service/vx_state_account_t;I)V
.end method

.method public static final native vx_state_account_t_state_buddy_group_count_get(JLcom/vivox/service/vx_state_account_t;)I
.end method

.method public static final native vx_state_account_t_state_buddy_group_count_set(JLcom/vivox/service/vx_state_account_t;I)V
.end method

.method public static final native vx_state_account_t_state_buddy_groups_get(JLcom/vivox/service/vx_state_account_t;)J
.end method

.method public static final native vx_state_account_t_state_buddy_groups_set(JLcom/vivox/service/vx_state_account_t;J)V
.end method

.method public static final native vx_state_account_t_state_get(JLcom/vivox/service/vx_state_account_t;)I
.end method

.method public static final native vx_state_account_t_state_sessiongroups_count_get(JLcom/vivox/service/vx_state_account_t;)I
.end method

.method public static final native vx_state_account_t_state_sessiongroups_count_set(JLcom/vivox/service/vx_state_account_t;I)V
.end method

.method public static final native vx_state_account_t_state_sessiongroups_get(JLcom/vivox/service/vx_state_account_t;)J
.end method

.method public static final native vx_state_account_t_state_sessiongroups_set(JLcom/vivox/service/vx_state_account_t;J)V
.end method

.method public static final native vx_state_account_t_state_set(JLcom/vivox/service/vx_state_account_t;I)V
.end method

.method public static final native vx_state_buddy_contact_create(J)V
.end method

.method public static final native vx_state_buddy_contact_free(JLcom/vivox/service/vx_state_buddy_contact_t;)V
.end method

.method public static final native vx_state_buddy_contact_list_create(IJ)V
.end method

.method public static final native vx_state_buddy_contact_list_free(JI)V
.end method

.method public static final native vx_state_buddy_contact_t_application_get(JLcom/vivox/service/vx_state_buddy_contact_t;)Ljava/lang/String;
.end method

.method public static final native vx_state_buddy_contact_t_application_set(JLcom/vivox/service/vx_state_buddy_contact_t;Ljava/lang/String;)V
.end method

.method public static final native vx_state_buddy_contact_t_contact_get(JLcom/vivox/service/vx_state_buddy_contact_t;)Ljava/lang/String;
.end method

.method public static final native vx_state_buddy_contact_t_contact_set(JLcom/vivox/service/vx_state_buddy_contact_t;Ljava/lang/String;)V
.end method

.method public static final native vx_state_buddy_contact_t_custom_message_get(JLcom/vivox/service/vx_state_buddy_contact_t;)Ljava/lang/String;
.end method

.method public static final native vx_state_buddy_contact_t_custom_message_set(JLcom/vivox/service/vx_state_buddy_contact_t;Ljava/lang/String;)V
.end method

.method public static final native vx_state_buddy_contact_t_display_name_get(JLcom/vivox/service/vx_state_buddy_contact_t;)Ljava/lang/String;
.end method

.method public static final native vx_state_buddy_contact_t_display_name_set(JLcom/vivox/service/vx_state_buddy_contact_t;Ljava/lang/String;)V
.end method

.method public static final native vx_state_buddy_contact_t_id_get(JLcom/vivox/service/vx_state_buddy_contact_t;)Ljava/lang/String;
.end method

.method public static final native vx_state_buddy_contact_t_id_set(JLcom/vivox/service/vx_state_buddy_contact_t;Ljava/lang/String;)V
.end method

.method public static final native vx_state_buddy_contact_t_presence_get(JLcom/vivox/service/vx_state_buddy_contact_t;)I
.end method

.method public static final native vx_state_buddy_contact_t_presence_set(JLcom/vivox/service/vx_state_buddy_contact_t;I)V
.end method

.method public static final native vx_state_buddy_contact_t_priority_get(JLcom/vivox/service/vx_state_buddy_contact_t;)Ljava/lang/String;
.end method

.method public static final native vx_state_buddy_contact_t_priority_set(JLcom/vivox/service/vx_state_buddy_contact_t;Ljava/lang/String;)V
.end method

.method public static final native vx_state_buddy_create(J)V
.end method

.method public static final native vx_state_buddy_free(JLcom/vivox/service/vx_state_buddy_t;)V
.end method

.method public static final native vx_state_buddy_group_create(J)V
.end method

.method public static final native vx_state_buddy_group_free(JLcom/vivox/service/vx_state_buddy_group_t;)V
.end method

.method public static final native vx_state_buddy_group_list_create(IJ)V
.end method

.method public static final native vx_state_buddy_group_list_free(JI)V
.end method

.method public static final native vx_state_buddy_group_t_group_data_get(JLcom/vivox/service/vx_state_buddy_group_t;)Ljava/lang/String;
.end method

.method public static final native vx_state_buddy_group_t_group_data_set(JLcom/vivox/service/vx_state_buddy_group_t;Ljava/lang/String;)V
.end method

.method public static final native vx_state_buddy_group_t_group_id_get(JLcom/vivox/service/vx_state_buddy_group_t;)I
.end method

.method public static final native vx_state_buddy_group_t_group_id_set(JLcom/vivox/service/vx_state_buddy_group_t;I)V
.end method

.method public static final native vx_state_buddy_group_t_group_name_get(JLcom/vivox/service/vx_state_buddy_group_t;)Ljava/lang/String;
.end method

.method public static final native vx_state_buddy_group_t_group_name_set(JLcom/vivox/service/vx_state_buddy_group_t;Ljava/lang/String;)V
.end method

.method public static final native vx_state_buddy_list_create(IJ)V
.end method

.method public static final native vx_state_buddy_list_free(JI)V
.end method

.method public static final native vx_state_buddy_t_buddy_data_get(JLcom/vivox/service/vx_state_buddy_t;)Ljava/lang/String;
.end method

.method public static final native vx_state_buddy_t_buddy_data_set(JLcom/vivox/service/vx_state_buddy_t;Ljava/lang/String;)V
.end method

.method public static final native vx_state_buddy_t_buddy_uri_get(JLcom/vivox/service/vx_state_buddy_t;)Ljava/lang/String;
.end method

.method public static final native vx_state_buddy_t_buddy_uri_set(JLcom/vivox/service/vx_state_buddy_t;Ljava/lang/String;)V
.end method

.method public static final native vx_state_buddy_t_display_name_get(JLcom/vivox/service/vx_state_buddy_t;)Ljava/lang/String;
.end method

.method public static final native vx_state_buddy_t_display_name_set(JLcom/vivox/service/vx_state_buddy_t;Ljava/lang/String;)V
.end method

.method public static final native vx_state_buddy_t_parent_group_id_get(JLcom/vivox/service/vx_state_buddy_t;)I
.end method

.method public static final native vx_state_buddy_t_parent_group_id_set(JLcom/vivox/service/vx_state_buddy_t;I)V
.end method

.method public static final native vx_state_buddy_t_state_buddy_contact_count_get(JLcom/vivox/service/vx_state_buddy_t;)I
.end method

.method public static final native vx_state_buddy_t_state_buddy_contact_count_set(JLcom/vivox/service/vx_state_buddy_t;I)V
.end method

.method public static final native vx_state_buddy_t_state_buddy_contacts_get(JLcom/vivox/service/vx_state_buddy_t;)J
.end method

.method public static final native vx_state_buddy_t_state_buddy_contacts_set(JLcom/vivox/service/vx_state_buddy_t;J)V
.end method

.method public static final native vx_state_connector_create(J)V
.end method

.method public static final native vx_state_connector_free(JLcom/vivox/service/vx_state_connector_t;)V
.end method

.method public static final native vx_state_connector_list_create(IJ)V
.end method

.method public static final native vx_state_connector_list_free(JI)V
.end method

.method public static final native vx_state_connector_t_connector_handle_get(JLcom/vivox/service/vx_state_connector_t;)Ljava/lang/String;
.end method

.method public static final native vx_state_connector_t_connector_handle_set(JLcom/vivox/service/vx_state_connector_t;Ljava/lang/String;)V
.end method

.method public static final native vx_state_connector_t_get_state_accounts_item(JLcom/vivox/service/vx_state_connector_t;I)J
.end method

.method public static final native vx_state_connector_t_mic_mute_get(JLcom/vivox/service/vx_state_connector_t;)I
.end method

.method public static final native vx_state_connector_t_mic_mute_set(JLcom/vivox/service/vx_state_connector_t;I)V
.end method

.method public static final native vx_state_connector_t_mic_vol_get(JLcom/vivox/service/vx_state_connector_t;)I
.end method

.method public static final native vx_state_connector_t_mic_vol_set(JLcom/vivox/service/vx_state_connector_t;I)V
.end method

.method public static final native vx_state_connector_t_speaker_mute_get(JLcom/vivox/service/vx_state_connector_t;)I
.end method

.method public static final native vx_state_connector_t_speaker_mute_set(JLcom/vivox/service/vx_state_connector_t;I)V
.end method

.method public static final native vx_state_connector_t_speaker_vol_get(JLcom/vivox/service/vx_state_connector_t;)I
.end method

.method public static final native vx_state_connector_t_speaker_vol_set(JLcom/vivox/service/vx_state_connector_t;I)V
.end method

.method public static final native vx_state_connector_t_state_accounts_count_get(JLcom/vivox/service/vx_state_connector_t;)I
.end method

.method public static final native vx_state_connector_t_state_accounts_count_set(JLcom/vivox/service/vx_state_connector_t;I)V
.end method

.method public static final native vx_state_connector_t_state_accounts_get(JLcom/vivox/service/vx_state_connector_t;)J
.end method

.method public static final native vx_state_connector_t_state_accounts_set(JLcom/vivox/service/vx_state_connector_t;J)V
.end method

.method public static final native vx_state_participant_create(J)V
.end method

.method public static final native vx_state_participant_free(JLcom/vivox/service/vx_state_participant_t;)V
.end method

.method public static final native vx_state_participant_list_create(IJ)V
.end method

.method public static final native vx_state_participant_list_free(JI)V
.end method

.method public static final native vx_state_participant_t_display_name_get(JLcom/vivox/service/vx_state_participant_t;)Ljava/lang/String;
.end method

.method public static final native vx_state_participant_t_display_name_set(JLcom/vivox/service/vx_state_participant_t;Ljava/lang/String;)V
.end method

.method public static final native vx_state_participant_t_energy_get(JLcom/vivox/service/vx_state_participant_t;)D
.end method

.method public static final native vx_state_participant_t_energy_set(JLcom/vivox/service/vx_state_participant_t;D)V
.end method

.method public static final native vx_state_participant_t_is_anonymous_login_get(JLcom/vivox/service/vx_state_participant_t;)I
.end method

.method public static final native vx_state_participant_t_is_anonymous_login_set(JLcom/vivox/service/vx_state_participant_t;I)V
.end method

.method public static final native vx_state_participant_t_is_audio_enabled_get(JLcom/vivox/service/vx_state_participant_t;)I
.end method

.method public static final native vx_state_participant_t_is_audio_enabled_set(JLcom/vivox/service/vx_state_participant_t;I)V
.end method

.method public static final native vx_state_participant_t_is_audio_moderator_muted_get(JLcom/vivox/service/vx_state_participant_t;)I
.end method

.method public static final native vx_state_participant_t_is_audio_moderator_muted_set(JLcom/vivox/service/vx_state_participant_t;I)V
.end method

.method public static final native vx_state_participant_t_is_audio_muted_for_me_get(JLcom/vivox/service/vx_state_participant_t;)I
.end method

.method public static final native vx_state_participant_t_is_audio_muted_for_me_set(JLcom/vivox/service/vx_state_participant_t;I)V
.end method

.method public static final native vx_state_participant_t_is_hand_raised_get(JLcom/vivox/service/vx_state_participant_t;)I
.end method

.method public static final native vx_state_participant_t_is_hand_raised_set(JLcom/vivox/service/vx_state_participant_t;I)V
.end method

.method public static final native vx_state_participant_t_is_speaking_get(JLcom/vivox/service/vx_state_participant_t;)I
.end method

.method public static final native vx_state_participant_t_is_speaking_set(JLcom/vivox/service/vx_state_participant_t;I)V
.end method

.method public static final native vx_state_participant_t_is_text_enabled_get(JLcom/vivox/service/vx_state_participant_t;)I
.end method

.method public static final native vx_state_participant_t_is_text_enabled_set(JLcom/vivox/service/vx_state_participant_t;I)V
.end method

.method public static final native vx_state_participant_t_is_text_moderator_muted_get(JLcom/vivox/service/vx_state_participant_t;)I
.end method

.method public static final native vx_state_participant_t_is_text_moderator_muted_set(JLcom/vivox/service/vx_state_participant_t;I)V
.end method

.method public static final native vx_state_participant_t_is_text_muted_for_me_get(JLcom/vivox/service/vx_state_participant_t;)I
.end method

.method public static final native vx_state_participant_t_is_text_muted_for_me_set(JLcom/vivox/service/vx_state_participant_t;I)V
.end method

.method public static final native vx_state_participant_t_is_typing_get(JLcom/vivox/service/vx_state_participant_t;)I
.end method

.method public static final native vx_state_participant_t_is_typing_set(JLcom/vivox/service/vx_state_participant_t;I)V
.end method

.method public static final native vx_state_participant_t_type_get(JLcom/vivox/service/vx_state_participant_t;)I
.end method

.method public static final native vx_state_participant_t_type_set(JLcom/vivox/service/vx_state_participant_t;I)V
.end method

.method public static final native vx_state_participant_t_uri_get(JLcom/vivox/service/vx_state_participant_t;)Ljava/lang/String;
.end method

.method public static final native vx_state_participant_t_uri_set(JLcom/vivox/service/vx_state_participant_t;Ljava/lang/String;)V
.end method

.method public static final native vx_state_participant_t_volume_get(JLcom/vivox/service/vx_state_participant_t;)I
.end method

.method public static final native vx_state_participant_t_volume_set(JLcom/vivox/service/vx_state_participant_t;I)V
.end method

.method public static final native vx_state_session_create(J)V
.end method

.method public static final native vx_state_session_free(JLcom/vivox/service/vx_state_session_t;)V
.end method

.method public static final native vx_state_session_list_create(IJ)V
.end method

.method public static final native vx_state_session_list_free(JI)V
.end method

.method public static final native vx_state_session_t_durable_media_id_get(JLcom/vivox/service/vx_state_session_t;)Ljava/lang/String;
.end method

.method public static final native vx_state_session_t_durable_media_id_set(JLcom/vivox/service/vx_state_session_t;Ljava/lang/String;)V
.end method

.method public static final native vx_state_session_t_get_state_participants_item(JLcom/vivox/service/vx_state_session_t;I)J
.end method

.method public static final native vx_state_session_t_has_audio_get(JLcom/vivox/service/vx_state_session_t;)I
.end method

.method public static final native vx_state_session_t_has_audio_set(JLcom/vivox/service/vx_state_session_t;I)V
.end method

.method public static final native vx_state_session_t_has_text_get(JLcom/vivox/service/vx_state_session_t;)I
.end method

.method public static final native vx_state_session_t_has_text_set(JLcom/vivox/service/vx_state_session_t;I)V
.end method

.method public static final native vx_state_session_t_is_audio_muted_for_me_get(JLcom/vivox/service/vx_state_session_t;)I
.end method

.method public static final native vx_state_session_t_is_audio_muted_for_me_set(JLcom/vivox/service/vx_state_session_t;I)V
.end method

.method public static final native vx_state_session_t_is_connected_get(JLcom/vivox/service/vx_state_session_t;)I
.end method

.method public static final native vx_state_session_t_is_connected_set(JLcom/vivox/service/vx_state_session_t;I)V
.end method

.method public static final native vx_state_session_t_is_focused_get(JLcom/vivox/service/vx_state_session_t;)I
.end method

.method public static final native vx_state_session_t_is_focused_set(JLcom/vivox/service/vx_state_session_t;I)V
.end method

.method public static final native vx_state_session_t_is_incoming_get(JLcom/vivox/service/vx_state_session_t;)I
.end method

.method public static final native vx_state_session_t_is_incoming_set(JLcom/vivox/service/vx_state_session_t;I)V
.end method

.method public static final native vx_state_session_t_is_positional_get(JLcom/vivox/service/vx_state_session_t;)I
.end method

.method public static final native vx_state_session_t_is_positional_set(JLcom/vivox/service/vx_state_session_t;I)V
.end method

.method public static final native vx_state_session_t_is_text_muted_for_me_get(JLcom/vivox/service/vx_state_session_t;)I
.end method

.method public static final native vx_state_session_t_is_text_muted_for_me_set(JLcom/vivox/service/vx_state_session_t;I)V
.end method

.method public static final native vx_state_session_t_is_transmitting_get(JLcom/vivox/service/vx_state_session_t;)I
.end method

.method public static final native vx_state_session_t_is_transmitting_set(JLcom/vivox/service/vx_state_session_t;I)V
.end method

.method public static final native vx_state_session_t_name_get(JLcom/vivox/service/vx_state_session_t;)Ljava/lang/String;
.end method

.method public static final native vx_state_session_t_name_set(JLcom/vivox/service/vx_state_session_t;Ljava/lang/String;)V
.end method

.method public static final native vx_state_session_t_session_font_id_get(JLcom/vivox/service/vx_state_session_t;)I
.end method

.method public static final native vx_state_session_t_session_font_id_set(JLcom/vivox/service/vx_state_session_t;I)V
.end method

.method public static final native vx_state_session_t_session_handle_get(JLcom/vivox/service/vx_state_session_t;)Ljava/lang/String;
.end method

.method public static final native vx_state_session_t_session_handle_set(JLcom/vivox/service/vx_state_session_t;Ljava/lang/String;)V
.end method

.method public static final native vx_state_session_t_state_participant_count_get(JLcom/vivox/service/vx_state_session_t;)I
.end method

.method public static final native vx_state_session_t_state_participant_count_set(JLcom/vivox/service/vx_state_session_t;I)V
.end method

.method public static final native vx_state_session_t_state_participants_get(JLcom/vivox/service/vx_state_session_t;)J
.end method

.method public static final native vx_state_session_t_state_participants_set(JLcom/vivox/service/vx_state_session_t;J)V
.end method

.method public static final native vx_state_session_t_uri_get(JLcom/vivox/service/vx_state_session_t;)Ljava/lang/String;
.end method

.method public static final native vx_state_session_t_uri_set(JLcom/vivox/service/vx_state_session_t;Ljava/lang/String;)V
.end method

.method public static final native vx_state_session_t_volume_get(JLcom/vivox/service/vx_state_session_t;)I
.end method

.method public static final native vx_state_session_t_volume_set(JLcom/vivox/service/vx_state_session_t;I)V
.end method

.method public static final native vx_state_sessiongroup_create(J)V
.end method

.method public static final native vx_state_sessiongroup_free(JLcom/vivox/service/vx_state_sessiongroup_t;)V
.end method

.method public static final native vx_state_sessiongroup_list_create(IJ)V
.end method

.method public static final native vx_state_sessiongroup_list_free(JI)V
.end method

.method public static final native vx_state_sessiongroup_t_current_playback_mode_get(JLcom/vivox/service/vx_state_sessiongroup_t;)I
.end method

.method public static final native vx_state_sessiongroup_t_current_playback_mode_set(JLcom/vivox/service/vx_state_sessiongroup_t;I)V
.end method

.method public static final native vx_state_sessiongroup_t_current_playback_speed_get(JLcom/vivox/service/vx_state_sessiongroup_t;)D
.end method

.method public static final native vx_state_sessiongroup_t_current_playback_speed_set(JLcom/vivox/service/vx_state_sessiongroup_t;D)V
.end method

.method public static final native vx_state_sessiongroup_t_current_recording_filename_get(JLcom/vivox/service/vx_state_sessiongroup_t;)Ljava/lang/String;
.end method

.method public static final native vx_state_sessiongroup_t_current_recording_filename_set(JLcom/vivox/service/vx_state_sessiongroup_t;Ljava/lang/String;)V
.end method

.method public static final native vx_state_sessiongroup_t_first_loop_frame_get(JLcom/vivox/service/vx_state_sessiongroup_t;)I
.end method

.method public static final native vx_state_sessiongroup_t_first_loop_frame_set(JLcom/vivox/service/vx_state_sessiongroup_t;I)V
.end method

.method public static final native vx_state_sessiongroup_t_get_state_sessions_item(JLcom/vivox/service/vx_state_sessiongroup_t;I)J
.end method

.method public static final native vx_state_sessiongroup_t_in_delayed_playback_get(JLcom/vivox/service/vx_state_sessiongroup_t;)I
.end method

.method public static final native vx_state_sessiongroup_t_in_delayed_playback_set(JLcom/vivox/service/vx_state_sessiongroup_t;I)V
.end method

.method public static final native vx_state_sessiongroup_t_last_loop_frame_played_get(JLcom/vivox/service/vx_state_sessiongroup_t;)I
.end method

.method public static final native vx_state_sessiongroup_t_last_loop_frame_played_set(JLcom/vivox/service/vx_state_sessiongroup_t;I)V
.end method

.method public static final native vx_state_sessiongroup_t_loop_buffer_capacity_get(JLcom/vivox/service/vx_state_sessiongroup_t;)I
.end method

.method public static final native vx_state_sessiongroup_t_loop_buffer_capacity_set(JLcom/vivox/service/vx_state_sessiongroup_t;I)V
.end method

.method public static final native vx_state_sessiongroup_t_playback_paused_get(JLcom/vivox/service/vx_state_sessiongroup_t;)I
.end method

.method public static final native vx_state_sessiongroup_t_playback_paused_set(JLcom/vivox/service/vx_state_sessiongroup_t;I)V
.end method

.method public static final native vx_state_sessiongroup_t_sessiongroup_handle_get(JLcom/vivox/service/vx_state_sessiongroup_t;)Ljava/lang/String;
.end method

.method public static final native vx_state_sessiongroup_t_sessiongroup_handle_set(JLcom/vivox/service/vx_state_sessiongroup_t;Ljava/lang/String;)V
.end method

.method public static final native vx_state_sessiongroup_t_state_sessions_count_get(JLcom/vivox/service/vx_state_sessiongroup_t;)I
.end method

.method public static final native vx_state_sessiongroup_t_state_sessions_count_set(JLcom/vivox/service/vx_state_sessiongroup_t;I)V
.end method

.method public static final native vx_state_sessiongroup_t_state_sessions_get(JLcom/vivox/service/vx_state_sessiongroup_t;)J
.end method

.method public static final native vx_state_sessiongroup_t_state_sessions_set(JLcom/vivox/service/vx_state_sessiongroup_t;J)V
.end method

.method public static final native vx_state_sessiongroup_t_total_loop_frames_captured_get(JLcom/vivox/service/vx_state_sessiongroup_t;)I
.end method

.method public static final native vx_state_sessiongroup_t_total_loop_frames_captured_set(JLcom/vivox/service/vx_state_sessiongroup_t;I)V
.end method

.method public static final native vx_state_sessiongroup_t_total_recorded_frames_get(JLcom/vivox/service/vx_state_sessiongroup_t;)I
.end method

.method public static final native vx_state_sessiongroup_t_total_recorded_frames_set(JLcom/vivox/service/vx_state_sessiongroup_t;I)V
.end method

.method public static final native vx_strdup(Ljava/lang/String;)Ljava/lang/String;
.end method

.method public static final native vx_string_list_create(IJ)V
.end method

.method public static final native vx_string_list_free(J)V
.end method

.method public static final native vx_system_stats_t_ar_source_count_get(JLcom/vivox/service/vx_system_stats_t;)I
.end method

.method public static final native vx_system_stats_t_ar_source_count_set(JLcom/vivox/service/vx_system_stats_t;I)V
.end method

.method public static final native vx_system_stats_t_ar_source_free_buffers_get(JLcom/vivox/service/vx_system_stats_t;)J
.end method

.method public static final native vx_system_stats_t_ar_source_free_buffers_set(JLcom/vivox/service/vx_system_stats_t;JLcom/vivox/service/vx_stat_sample_t;)V
.end method

.method public static final native vx_system_stats_t_ar_source_poll_count_get(JLcom/vivox/service/vx_system_stats_t;)I
.end method

.method public static final native vx_system_stats_t_ar_source_poll_count_set(JLcom/vivox/service/vx_system_stats_t;I)V
.end method

.method public static final native vx_system_stats_t_ar_source_queue_depth_get(JLcom/vivox/service/vx_system_stats_t;)J
.end method

.method public static final native vx_system_stats_t_ar_source_queue_depth_set(JLcom/vivox/service/vx_system_stats_t;JLcom/vivox/service/vx_stat_sample_t;)V
.end method

.method public static final native vx_system_stats_t_ar_source_queue_limit_get(JLcom/vivox/service/vx_system_stats_t;)I
.end method

.method public static final native vx_system_stats_t_ar_source_queue_limit_set(JLcom/vivox/service/vx_system_stats_t;I)V
.end method

.method public static final native vx_system_stats_t_ar_source_queue_overflows_get(JLcom/vivox/service/vx_system_stats_t;)I
.end method

.method public static final native vx_system_stats_t_ar_source_queue_overflows_set(JLcom/vivox/service/vx_system_stats_t;I)V
.end method

.method public static final native vx_system_stats_t_ss_size_get(JLcom/vivox/service/vx_system_stats_t;)I
.end method

.method public static final native vx_system_stats_t_ss_size_set(JLcom/vivox/service/vx_system_stats_t;I)V
.end method

.method public static final native vx_system_stats_t_ticker_thread_get(JLcom/vivox/service/vx_system_stats_t;)J
.end method

.method public static final native vx_system_stats_t_ticker_thread_set(JLcom/vivox/service/vx_system_stats_t;JLcom/vivox/service/vx_stat_thread_t;)V
.end method

.method public static final native vx_system_stats_t_vp_thread_get(JLcom/vivox/service/vx_system_stats_t;)J
.end method

.method public static final native vx_system_stats_t_vp_thread_set(JLcom/vivox/service/vx_system_stats_t;JLcom/vivox/service/vx_stat_thread_t;)V
.end method

.method public static final native vx_uninitialize()I
.end method

.method public static final native vx_unregister_logging_handler(JJ)V
.end method

.method public static final native vx_unregister_message_notification_handler(JJ)V
.end method

.method public static final native vx_user_channel_create(J)V
.end method

.method public static final native vx_user_channel_free(JLcom/vivox/service/vx_user_channel_t;)V
.end method

.method public static final native vx_user_channel_t_name_get(JLcom/vivox/service/vx_user_channel_t;)Ljava/lang/String;
.end method

.method public static final native vx_user_channel_t_name_set(JLcom/vivox/service/vx_user_channel_t;Ljava/lang/String;)V
.end method

.method public static final native vx_user_channel_t_uri_get(JLcom/vivox/service/vx_user_channel_t;)Ljava/lang/String;
.end method

.method public static final native vx_user_channel_t_uri_set(JLcom/vivox/service/vx_user_channel_t;Ljava/lang/String;)V
.end method

.method public static final native vx_user_channels_create(IJ)V
.end method

.method public static final native vx_user_channels_free(JI)V
.end method

.method public static final native vx_voice_font_create(J)V
.end method

.method public static final native vx_voice_font_free(JLcom/vivox/service/vx_voice_font_t;)V
.end method

.method public static final native vx_voice_font_list_create(IJ)V
.end method

.method public static final native vx_voice_font_list_free(JI)V
.end method

.method public static final native vx_voice_font_t_description_get(JLcom/vivox/service/vx_voice_font_t;)Ljava/lang/String;
.end method

.method public static final native vx_voice_font_t_description_set(JLcom/vivox/service/vx_voice_font_t;Ljava/lang/String;)V
.end method

.method public static final native vx_voice_font_t_expiration_date_get(JLcom/vivox/service/vx_voice_font_t;)Ljava/lang/String;
.end method

.method public static final native vx_voice_font_t_expiration_date_set(JLcom/vivox/service/vx_voice_font_t;Ljava/lang/String;)V
.end method

.method public static final native vx_voice_font_t_expired_get(JLcom/vivox/service/vx_voice_font_t;)I
.end method

.method public static final native vx_voice_font_t_expired_set(JLcom/vivox/service/vx_voice_font_t;I)V
.end method

.method public static final native vx_voice_font_t_font_delta_get(JLcom/vivox/service/vx_voice_font_t;)Ljava/lang/String;
.end method

.method public static final native vx_voice_font_t_font_delta_set(JLcom/vivox/service/vx_voice_font_t;Ljava/lang/String;)V
.end method

.method public static final native vx_voice_font_t_font_rules_get(JLcom/vivox/service/vx_voice_font_t;)Ljava/lang/String;
.end method

.method public static final native vx_voice_font_t_font_rules_set(JLcom/vivox/service/vx_voice_font_t;Ljava/lang/String;)V
.end method

.method public static final native vx_voice_font_t_id_get(JLcom/vivox/service/vx_voice_font_t;)I
.end method

.method public static final native vx_voice_font_t_id_set(JLcom/vivox/service/vx_voice_font_t;I)V
.end method

.method public static final native vx_voice_font_t_name_get(JLcom/vivox/service/vx_voice_font_t;)Ljava/lang/String;
.end method

.method public static final native vx_voice_font_t_name_set(JLcom/vivox/service/vx_voice_font_t;Ljava/lang/String;)V
.end method

.method public static final native vx_voice_font_t_parent_id_get(JLcom/vivox/service/vx_voice_font_t;)I
.end method

.method public static final native vx_voice_font_t_parent_id_set(JLcom/vivox/service/vx_voice_font_t;I)V
.end method

.method public static final native vx_voice_font_t_status_get(JLcom/vivox/service/vx_voice_font_t;)I
.end method

.method public static final native vx_voice_font_t_status_set(JLcom/vivox/service/vx_voice_font_t;I)V
.end method

.method public static final native vx_voice_font_t_type_get(JLcom/vivox/service/vx_voice_font_t;)I
.end method

.method public static final native vx_voice_font_t_type_set(JLcom/vivox/service/vx_voice_font_t;I)V
.end method

.method public static final native vx_vxr_file_close(J)I
.end method

.method public static final native vx_vxr_file_get_frame_count(JJ)I
.end method

.method public static final native vx_vxr_file_move_to_frame(JI)I
.end method

.method public static final native vx_vxr_file_open(Ljava/lang/String;Ljava/lang/String;J)I
.end method

.method public static final native vx_vxr_file_read_frame(JJIJJ)I
.end method

.method public static final native vx_vxr_file_write_frame(JIJI)I
.end method

.method public static final native vx_wait_for_message(I)J
.end method

.method public static final native vx_xml_to_event(Ljava/lang/String;JJ)I
.end method

.method public static final native vx_xml_to_request(Ljava/lang/String;JJ)I
.end method

.method public static final native vx_xml_to_response(Ljava/lang/String;JJ)I
.end method

.method public static final native xml_to_request(Ljava/lang/String;)J
.end method
