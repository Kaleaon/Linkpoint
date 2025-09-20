.class public final Lcom/vivox/service/vx_event_type;
.super Ljava/lang/Object;
.source "vx_event_type.java"


# static fields
.field public static final evt_account_login_state_change:Lcom/vivox/service/vx_event_type;

.field public static final evt_aux_audio_properties:Lcom/vivox/service/vx_event_type;

.field public static final evt_buddy_and_group_list_changed:Lcom/vivox/service/vx_event_type;

.field public static final evt_buddy_changed:Lcom/vivox/service/vx_event_type;

.field public static final evt_buddy_group_changed:Lcom/vivox/service/vx_event_type;

.field public static final evt_buddy_presence:Lcom/vivox/service/vx_event_type;

.field public static final evt_idle_state_changed:Lcom/vivox/service/vx_event_type;

.field public static final evt_keyboard_mouse:Lcom/vivox/service/vx_event_type;

.field public static final evt_max:Lcom/vivox/service/vx_event_type;

.field public static final evt_media_completion:Lcom/vivox/service/vx_event_type;

.field public static final evt_media_stream_updated:Lcom/vivox/service/vx_event_type;

.field public static final evt_message:Lcom/vivox/service/vx_event_type;

.field public static final evt_network_message:Lcom/vivox/service/vx_event_type;

.field public static final evt_none:Lcom/vivox/service/vx_event_type;

.field public static final evt_participant_added:Lcom/vivox/service/vx_event_type;

.field public static final evt_participant_removed:Lcom/vivox/service/vx_event_type;

.field public static final evt_participant_updated:Lcom/vivox/service/vx_event_type;

.field public static final evt_server_app_data:Lcom/vivox/service/vx_event_type;

.field public static final evt_session_added:Lcom/vivox/service/vx_event_type;

.field public static final evt_session_notification:Lcom/vivox/service/vx_event_type;

.field public static final evt_session_removed:Lcom/vivox/service/vx_event_type;

.field public static final evt_session_updated:Lcom/vivox/service/vx_event_type;

.field public static final evt_sessiongroup_added:Lcom/vivox/service/vx_event_type;

.field public static final evt_sessiongroup_playback_frame_played:Lcom/vivox/service/vx_event_type;

.field public static final evt_sessiongroup_removed:Lcom/vivox/service/vx_event_type;

.field public static final evt_sessiongroup_updated:Lcom/vivox/service/vx_event_type;

.field public static final evt_subscription:Lcom/vivox/service/vx_event_type;

.field public static final evt_text_stream_updated:Lcom/vivox/service/vx_event_type;

.field public static final evt_user_app_data:Lcom/vivox/service/vx_event_type;

.field public static final evt_voice_service_connection_state_changed:Lcom/vivox/service/vx_event_type;

.field private static swigNext:I

.field private static swigValues:[Lcom/vivox/service/vx_event_type;


# instance fields
.field private final swigName:Ljava/lang/String;

.field private final swigValue:I


# direct methods
.method static constructor <clinit>()V
    .locals 4

    .prologue
    const/4 v3, 0x0

    .line 5
    sput v3, Lcom/vivox/service/vx_event_type;->swigNext:I

    .line 7
    new-instance v0, Lcom/vivox/service/vx_event_type;

    const-string v1, "evt_account_login_state_change"

    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->evt_account_login_state_change_get()I

    move-result v2

    invoke-direct {v0, v1, v2}, Lcom/vivox/service/vx_event_type;-><init>(Ljava/lang/String;I)V

    sput-object v0, Lcom/vivox/service/vx_event_type;->evt_account_login_state_change:Lcom/vivox/service/vx_event_type;

    .line 8
    new-instance v0, Lcom/vivox/service/vx_event_type;

    const-string v1, "evt_aux_audio_properties"

    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->evt_aux_audio_properties_get()I

    move-result v2

    invoke-direct {v0, v1, v2}, Lcom/vivox/service/vx_event_type;-><init>(Ljava/lang/String;I)V

    sput-object v0, Lcom/vivox/service/vx_event_type;->evt_aux_audio_properties:Lcom/vivox/service/vx_event_type;

    .line 9
    new-instance v0, Lcom/vivox/service/vx_event_type;

    const-string v1, "evt_buddy_and_group_list_changed"

    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->evt_buddy_and_group_list_changed_get()I

    move-result v2

    invoke-direct {v0, v1, v2}, Lcom/vivox/service/vx_event_type;-><init>(Ljava/lang/String;I)V

    sput-object v0, Lcom/vivox/service/vx_event_type;->evt_buddy_and_group_list_changed:Lcom/vivox/service/vx_event_type;

    .line 10
    new-instance v0, Lcom/vivox/service/vx_event_type;

    const-string v1, "evt_buddy_changed"

    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->evt_buddy_changed_get()I

    move-result v2

    invoke-direct {v0, v1, v2}, Lcom/vivox/service/vx_event_type;-><init>(Ljava/lang/String;I)V

    sput-object v0, Lcom/vivox/service/vx_event_type;->evt_buddy_changed:Lcom/vivox/service/vx_event_type;

    .line 11
    new-instance v0, Lcom/vivox/service/vx_event_type;

    const-string v1, "evt_buddy_group_changed"

    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->evt_buddy_group_changed_get()I

    move-result v2

    invoke-direct {v0, v1, v2}, Lcom/vivox/service/vx_event_type;-><init>(Ljava/lang/String;I)V

    sput-object v0, Lcom/vivox/service/vx_event_type;->evt_buddy_group_changed:Lcom/vivox/service/vx_event_type;

    .line 12
    new-instance v0, Lcom/vivox/service/vx_event_type;

    const-string v1, "evt_buddy_presence"

    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->evt_buddy_presence_get()I

    move-result v2

    invoke-direct {v0, v1, v2}, Lcom/vivox/service/vx_event_type;-><init>(Ljava/lang/String;I)V

    sput-object v0, Lcom/vivox/service/vx_event_type;->evt_buddy_presence:Lcom/vivox/service/vx_event_type;

    .line 13
    new-instance v0, Lcom/vivox/service/vx_event_type;

    const-string v1, "evt_idle_state_changed"

    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->evt_idle_state_changed_get()I

    move-result v2

    invoke-direct {v0, v1, v2}, Lcom/vivox/service/vx_event_type;-><init>(Ljava/lang/String;I)V

    sput-object v0, Lcom/vivox/service/vx_event_type;->evt_idle_state_changed:Lcom/vivox/service/vx_event_type;

    .line 14
    new-instance v0, Lcom/vivox/service/vx_event_type;

    const-string v1, "evt_keyboard_mouse"

    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->evt_keyboard_mouse_get()I

    move-result v2

    invoke-direct {v0, v1, v2}, Lcom/vivox/service/vx_event_type;-><init>(Ljava/lang/String;I)V

    sput-object v0, Lcom/vivox/service/vx_event_type;->evt_keyboard_mouse:Lcom/vivox/service/vx_event_type;

    .line 15
    new-instance v0, Lcom/vivox/service/vx_event_type;

    const-string v1, "evt_max"

    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->evt_max_get()I

    move-result v2

    invoke-direct {v0, v1, v2}, Lcom/vivox/service/vx_event_type;-><init>(Ljava/lang/String;I)V

    sput-object v0, Lcom/vivox/service/vx_event_type;->evt_max:Lcom/vivox/service/vx_event_type;

    .line 16
    new-instance v0, Lcom/vivox/service/vx_event_type;

    const-string v1, "evt_media_completion"

    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->evt_media_completion_get()I

    move-result v2

    invoke-direct {v0, v1, v2}, Lcom/vivox/service/vx_event_type;-><init>(Ljava/lang/String;I)V

    sput-object v0, Lcom/vivox/service/vx_event_type;->evt_media_completion:Lcom/vivox/service/vx_event_type;

    .line 17
    new-instance v0, Lcom/vivox/service/vx_event_type;

    const-string v1, "evt_media_stream_updated"

    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->evt_media_stream_updated_get()I

    move-result v2

    invoke-direct {v0, v1, v2}, Lcom/vivox/service/vx_event_type;-><init>(Ljava/lang/String;I)V

    sput-object v0, Lcom/vivox/service/vx_event_type;->evt_media_stream_updated:Lcom/vivox/service/vx_event_type;

    .line 18
    new-instance v0, Lcom/vivox/service/vx_event_type;

    const-string v1, "evt_message"

    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->evt_message_get()I

    move-result v2

    invoke-direct {v0, v1, v2}, Lcom/vivox/service/vx_event_type;-><init>(Ljava/lang/String;I)V

    sput-object v0, Lcom/vivox/service/vx_event_type;->evt_message:Lcom/vivox/service/vx_event_type;

    .line 19
    new-instance v0, Lcom/vivox/service/vx_event_type;

    const-string v1, "evt_network_message"

    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->evt_network_message_get()I

    move-result v2

    invoke-direct {v0, v1, v2}, Lcom/vivox/service/vx_event_type;-><init>(Ljava/lang/String;I)V

    sput-object v0, Lcom/vivox/service/vx_event_type;->evt_network_message:Lcom/vivox/service/vx_event_type;

    .line 20
    new-instance v0, Lcom/vivox/service/vx_event_type;

    const-string v1, "evt_none"

    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->evt_none_get()I

    move-result v2

    invoke-direct {v0, v1, v2}, Lcom/vivox/service/vx_event_type;-><init>(Ljava/lang/String;I)V

    sput-object v0, Lcom/vivox/service/vx_event_type;->evt_none:Lcom/vivox/service/vx_event_type;

    .line 21
    new-instance v0, Lcom/vivox/service/vx_event_type;

    const-string v1, "evt_participant_added"

    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->evt_participant_added_get()I

    move-result v2

    invoke-direct {v0, v1, v2}, Lcom/vivox/service/vx_event_type;-><init>(Ljava/lang/String;I)V

    sput-object v0, Lcom/vivox/service/vx_event_type;->evt_participant_added:Lcom/vivox/service/vx_event_type;

    .line 22
    new-instance v0, Lcom/vivox/service/vx_event_type;

    const-string v1, "evt_participant_removed"

    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->evt_participant_removed_get()I

    move-result v2

    invoke-direct {v0, v1, v2}, Lcom/vivox/service/vx_event_type;-><init>(Ljava/lang/String;I)V

    sput-object v0, Lcom/vivox/service/vx_event_type;->evt_participant_removed:Lcom/vivox/service/vx_event_type;

    .line 23
    new-instance v0, Lcom/vivox/service/vx_event_type;

    const-string v1, "evt_participant_updated"

    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->evt_participant_updated_get()I

    move-result v2

    invoke-direct {v0, v1, v2}, Lcom/vivox/service/vx_event_type;-><init>(Ljava/lang/String;I)V

    sput-object v0, Lcom/vivox/service/vx_event_type;->evt_participant_updated:Lcom/vivox/service/vx_event_type;

    .line 24
    new-instance v0, Lcom/vivox/service/vx_event_type;

    const-string v1, "evt_server_app_data"

    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->evt_server_app_data_get()I

    move-result v2

    invoke-direct {v0, v1, v2}, Lcom/vivox/service/vx_event_type;-><init>(Ljava/lang/String;I)V

    sput-object v0, Lcom/vivox/service/vx_event_type;->evt_server_app_data:Lcom/vivox/service/vx_event_type;

    .line 25
    new-instance v0, Lcom/vivox/service/vx_event_type;

    const-string v1, "evt_session_added"

    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->evt_session_added_get()I

    move-result v2

    invoke-direct {v0, v1, v2}, Lcom/vivox/service/vx_event_type;-><init>(Ljava/lang/String;I)V

    sput-object v0, Lcom/vivox/service/vx_event_type;->evt_session_added:Lcom/vivox/service/vx_event_type;

    .line 26
    new-instance v0, Lcom/vivox/service/vx_event_type;

    const-string v1, "evt_session_notification"

    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->evt_session_notification_get()I

    move-result v2

    invoke-direct {v0, v1, v2}, Lcom/vivox/service/vx_event_type;-><init>(Ljava/lang/String;I)V

    sput-object v0, Lcom/vivox/service/vx_event_type;->evt_session_notification:Lcom/vivox/service/vx_event_type;

    .line 27
    new-instance v0, Lcom/vivox/service/vx_event_type;

    const-string v1, "evt_session_removed"

    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->evt_session_removed_get()I

    move-result v2

    invoke-direct {v0, v1, v2}, Lcom/vivox/service/vx_event_type;-><init>(Ljava/lang/String;I)V

    sput-object v0, Lcom/vivox/service/vx_event_type;->evt_session_removed:Lcom/vivox/service/vx_event_type;

    .line 28
    new-instance v0, Lcom/vivox/service/vx_event_type;

    const-string v1, "evt_session_updated"

    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->evt_session_updated_get()I

    move-result v2

    invoke-direct {v0, v1, v2}, Lcom/vivox/service/vx_event_type;-><init>(Ljava/lang/String;I)V

    sput-object v0, Lcom/vivox/service/vx_event_type;->evt_session_updated:Lcom/vivox/service/vx_event_type;

    .line 29
    new-instance v0, Lcom/vivox/service/vx_event_type;

    const-string v1, "evt_sessiongroup_added"

    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->evt_sessiongroup_added_get()I

    move-result v2

    invoke-direct {v0, v1, v2}, Lcom/vivox/service/vx_event_type;-><init>(Ljava/lang/String;I)V

    sput-object v0, Lcom/vivox/service/vx_event_type;->evt_sessiongroup_added:Lcom/vivox/service/vx_event_type;

    .line 30
    new-instance v0, Lcom/vivox/service/vx_event_type;

    const-string v1, "evt_sessiongroup_playback_frame_played"

    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->evt_sessiongroup_playback_frame_played_get()I

    move-result v2

    invoke-direct {v0, v1, v2}, Lcom/vivox/service/vx_event_type;-><init>(Ljava/lang/String;I)V

    sput-object v0, Lcom/vivox/service/vx_event_type;->evt_sessiongroup_playback_frame_played:Lcom/vivox/service/vx_event_type;

    .line 31
    new-instance v0, Lcom/vivox/service/vx_event_type;

    const-string v1, "evt_sessiongroup_removed"

    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->evt_sessiongroup_removed_get()I

    move-result v2

    invoke-direct {v0, v1, v2}, Lcom/vivox/service/vx_event_type;-><init>(Ljava/lang/String;I)V

    sput-object v0, Lcom/vivox/service/vx_event_type;->evt_sessiongroup_removed:Lcom/vivox/service/vx_event_type;

    .line 32
    new-instance v0, Lcom/vivox/service/vx_event_type;

    const-string v1, "evt_sessiongroup_updated"

    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->evt_sessiongroup_updated_get()I

    move-result v2

    invoke-direct {v0, v1, v2}, Lcom/vivox/service/vx_event_type;-><init>(Ljava/lang/String;I)V

    sput-object v0, Lcom/vivox/service/vx_event_type;->evt_sessiongroup_updated:Lcom/vivox/service/vx_event_type;

    .line 33
    new-instance v0, Lcom/vivox/service/vx_event_type;

    const-string v1, "evt_subscription"

    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->evt_subscription_get()I

    move-result v2

    invoke-direct {v0, v1, v2}, Lcom/vivox/service/vx_event_type;-><init>(Ljava/lang/String;I)V

    sput-object v0, Lcom/vivox/service/vx_event_type;->evt_subscription:Lcom/vivox/service/vx_event_type;

    .line 34
    new-instance v0, Lcom/vivox/service/vx_event_type;

    const-string v1, "evt_text_stream_updated"

    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->evt_text_stream_updated_get()I

    move-result v2

    invoke-direct {v0, v1, v2}, Lcom/vivox/service/vx_event_type;-><init>(Ljava/lang/String;I)V

    sput-object v0, Lcom/vivox/service/vx_event_type;->evt_text_stream_updated:Lcom/vivox/service/vx_event_type;

    .line 35
    new-instance v0, Lcom/vivox/service/vx_event_type;

    const-string v1, "evt_user_app_data"

    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->evt_user_app_data_get()I

    move-result v2

    invoke-direct {v0, v1, v2}, Lcom/vivox/service/vx_event_type;-><init>(Ljava/lang/String;I)V

    sput-object v0, Lcom/vivox/service/vx_event_type;->evt_user_app_data:Lcom/vivox/service/vx_event_type;

    .line 36
    new-instance v0, Lcom/vivox/service/vx_event_type;

    const-string v1, "evt_voice_service_connection_state_changed"

    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->evt_voice_service_connection_state_changed_get()I

    move-result v2

    invoke-direct {v0, v1, v2}, Lcom/vivox/service/vx_event_type;-><init>(Ljava/lang/String;I)V

    sput-object v0, Lcom/vivox/service/vx_event_type;->evt_voice_service_connection_state_changed:Lcom/vivox/service/vx_event_type;

    .line 38
    const/16 v0, 0x1e

    new-array v0, v0, [Lcom/vivox/service/vx_event_type;

    sget-object v1, Lcom/vivox/service/vx_event_type;->evt_none:Lcom/vivox/service/vx_event_type;

    aput-object v1, v0, v3

    const/4 v1, 0x1

    sget-object v2, Lcom/vivox/service/vx_event_type;->evt_account_login_state_change:Lcom/vivox/service/vx_event_type;

    aput-object v2, v0, v1

    const/4 v1, 0x2

    sget-object v2, Lcom/vivox/service/vx_event_type;->evt_buddy_presence:Lcom/vivox/service/vx_event_type;

    aput-object v2, v0, v1

    const/4 v1, 0x3

    sget-object v2, Lcom/vivox/service/vx_event_type;->evt_subscription:Lcom/vivox/service/vx_event_type;

    aput-object v2, v0, v1

    const/4 v1, 0x4

    sget-object v2, Lcom/vivox/service/vx_event_type;->evt_session_notification:Lcom/vivox/service/vx_event_type;

    aput-object v2, v0, v1

    const/4 v1, 0x5

    sget-object v2, Lcom/vivox/service/vx_event_type;->evt_message:Lcom/vivox/service/vx_event_type;

    aput-object v2, v0, v1

    const/4 v1, 0x6

    sget-object v2, Lcom/vivox/service/vx_event_type;->evt_aux_audio_properties:Lcom/vivox/service/vx_event_type;

    aput-object v2, v0, v1

    const/4 v1, 0x7

    sget-object v2, Lcom/vivox/service/vx_event_type;->evt_buddy_changed:Lcom/vivox/service/vx_event_type;

    aput-object v2, v0, v1

    const/16 v1, 0x8

    sget-object v2, Lcom/vivox/service/vx_event_type;->evt_buddy_group_changed:Lcom/vivox/service/vx_event_type;

    aput-object v2, v0, v1

    const/16 v1, 0x9

    sget-object v2, Lcom/vivox/service/vx_event_type;->evt_buddy_and_group_list_changed:Lcom/vivox/service/vx_event_type;

    aput-object v2, v0, v1

    const/16 v1, 0xa

    sget-object v2, Lcom/vivox/service/vx_event_type;->evt_keyboard_mouse:Lcom/vivox/service/vx_event_type;

    aput-object v2, v0, v1

    const/16 v1, 0xb

    sget-object v2, Lcom/vivox/service/vx_event_type;->evt_idle_state_changed:Lcom/vivox/service/vx_event_type;

    aput-object v2, v0, v1

    const/16 v1, 0xc

    sget-object v2, Lcom/vivox/service/vx_event_type;->evt_media_stream_updated:Lcom/vivox/service/vx_event_type;

    aput-object v2, v0, v1

    const/16 v1, 0xd

    sget-object v2, Lcom/vivox/service/vx_event_type;->evt_text_stream_updated:Lcom/vivox/service/vx_event_type;

    aput-object v2, v0, v1

    const/16 v1, 0xe

    sget-object v2, Lcom/vivox/service/vx_event_type;->evt_sessiongroup_added:Lcom/vivox/service/vx_event_type;

    aput-object v2, v0, v1

    const/16 v1, 0xf

    sget-object v2, Lcom/vivox/service/vx_event_type;->evt_sessiongroup_removed:Lcom/vivox/service/vx_event_type;

    aput-object v2, v0, v1

    const/16 v1, 0x10

    sget-object v2, Lcom/vivox/service/vx_event_type;->evt_session_added:Lcom/vivox/service/vx_event_type;

    aput-object v2, v0, v1

    const/16 v1, 0x11

    sget-object v2, Lcom/vivox/service/vx_event_type;->evt_session_removed:Lcom/vivox/service/vx_event_type;

    aput-object v2, v0, v1

    const/16 v1, 0x12

    sget-object v2, Lcom/vivox/service/vx_event_type;->evt_participant_added:Lcom/vivox/service/vx_event_type;

    aput-object v2, v0, v1

    const/16 v1, 0x13

    sget-object v2, Lcom/vivox/service/vx_event_type;->evt_participant_removed:Lcom/vivox/service/vx_event_type;

    aput-object v2, v0, v1

    const/16 v1, 0x14

    sget-object v2, Lcom/vivox/service/vx_event_type;->evt_participant_updated:Lcom/vivox/service/vx_event_type;

    aput-object v2, v0, v1

    const/16 v1, 0x15

    sget-object v2, Lcom/vivox/service/vx_event_type;->evt_sessiongroup_playback_frame_played:Lcom/vivox/service/vx_event_type;

    aput-object v2, v0, v1

    const/16 v1, 0x16

    sget-object v2, Lcom/vivox/service/vx_event_type;->evt_session_updated:Lcom/vivox/service/vx_event_type;

    aput-object v2, v0, v1

    const/16 v1, 0x17

    sget-object v2, Lcom/vivox/service/vx_event_type;->evt_sessiongroup_updated:Lcom/vivox/service/vx_event_type;

    aput-object v2, v0, v1

    const/16 v1, 0x18

    sget-object v2, Lcom/vivox/service/vx_event_type;->evt_media_completion:Lcom/vivox/service/vx_event_type;

    aput-object v2, v0, v1

    const/16 v1, 0x19

    sget-object v2, Lcom/vivox/service/vx_event_type;->evt_server_app_data:Lcom/vivox/service/vx_event_type;

    aput-object v2, v0, v1

    const/16 v1, 0x1a

    sget-object v2, Lcom/vivox/service/vx_event_type;->evt_user_app_data:Lcom/vivox/service/vx_event_type;

    aput-object v2, v0, v1

    const/16 v1, 0x1b

    sget-object v2, Lcom/vivox/service/vx_event_type;->evt_network_message:Lcom/vivox/service/vx_event_type;

    aput-object v2, v0, v1

    const/16 v1, 0x1c

    sget-object v2, Lcom/vivox/service/vx_event_type;->evt_voice_service_connection_state_changed:Lcom/vivox/service/vx_event_type;

    aput-object v2, v0, v1

    const/16 v1, 0x1d

    sget-object v2, Lcom/vivox/service/vx_event_type;->evt_max:Lcom/vivox/service/vx_event_type;

    aput-object v2, v0, v1

    sput-object v0, Lcom/vivox/service/vx_event_type;->swigValues:[Lcom/vivox/service/vx_event_type;

    return-void
.end method

.method private constructor <init>(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 43
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 44
    iput-object p1, p0, Lcom/vivox/service/vx_event_type;->swigName:Ljava/lang/String;

    .line 45
    sget v0, Lcom/vivox/service/vx_event_type;->swigNext:I

    .line 46
    .local v0, "i":I
    add-int/lit8 v1, v0, 0x1

    sput v1, Lcom/vivox/service/vx_event_type;->swigNext:I

    .line 47
    iput v0, p0, Lcom/vivox/service/vx_event_type;->swigValue:I

    .line 48
    return-void
.end method

.method private constructor <init>(Ljava/lang/String;I)V
    .locals 1
    .param p1, "str"    # Ljava/lang/String;
    .param p2, "i"    # I

    .prologue
    .line 50
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 51
    iput-object p1, p0, Lcom/vivox/service/vx_event_type;->swigName:Ljava/lang/String;

    .line 52
    iput p2, p0, Lcom/vivox/service/vx_event_type;->swigValue:I

    .line 53
    add-int/lit8 v0, p2, 0x1

    sput v0, Lcom/vivox/service/vx_event_type;->swigNext:I

    .line 54
    return-void
.end method

.method private constructor <init>(Ljava/lang/String;Lcom/vivox/service/vx_event_type;)V
    .locals 1
    .param p1, "str"    # Ljava/lang/String;
    .param p2, "com_vivox_service_vx_event_type"    # Lcom/vivox/service/vx_event_type;

    .prologue
    .line 56
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 57
    iput-object p1, p0, Lcom/vivox/service/vx_event_type;->swigName:Ljava/lang/String;

    .line 58
    iget v0, p2, Lcom/vivox/service/vx_event_type;->swigValue:I

    iput v0, p0, Lcom/vivox/service/vx_event_type;->swigValue:I

    .line 59
    iget v0, p0, Lcom/vivox/service/vx_event_type;->swigValue:I

    add-int/lit8 v0, v0, 0x1

    sput v0, Lcom/vivox/service/vx_event_type;->swigNext:I

    .line 60
    return-void
.end method

.method public static swigToEnum(I)Lcom/vivox/service/vx_event_type;
    .locals 4
    .param p0, "i"    # I

    .prologue
    .line 63
    sget-object v1, Lcom/vivox/service/vx_event_type;->swigValues:[Lcom/vivox/service/vx_event_type;

    array-length v1, v1

    if-ge p0, v1, :cond_0

    if-ltz p0, :cond_0

    sget-object v1, Lcom/vivox/service/vx_event_type;->swigValues:[Lcom/vivox/service/vx_event_type;

    aget-object v1, v1, p0

    iget v1, v1, Lcom/vivox/service/vx_event_type;->swigValue:I

    if-ne v1, p0, :cond_0

    .line 64
    sget-object v1, Lcom/vivox/service/vx_event_type;->swigValues:[Lcom/vivox/service/vx_event_type;

    aget-object v1, v1, p0

    .line 68
    :goto_0
    return-object v1

    .line 66
    :cond_0
    const/4 v0, 0x0

    .local v0, "i2":I
    :goto_1
    sget-object v1, Lcom/vivox/service/vx_event_type;->swigValues:[Lcom/vivox/service/vx_event_type;

    array-length v1, v1

    if-ge v0, v1, :cond_2

    .line 67
    sget-object v1, Lcom/vivox/service/vx_event_type;->swigValues:[Lcom/vivox/service/vx_event_type;

    aget-object v1, v1, v0

    iget v1, v1, Lcom/vivox/service/vx_event_type;->swigValue:I

    if-ne v1, p0, :cond_1

    .line 68
    sget-object v1, Lcom/vivox/service/vx_event_type;->swigValues:[Lcom/vivox/service/vx_event_type;

    aget-object v1, v1, v0

    goto :goto_0

    .line 66
    :cond_1
    add-int/lit8 v0, v0, 0x1

    goto :goto_1

    .line 71
    :cond_2
    new-instance v1, Ljava/lang/IllegalArgumentException;

    new-instance v2, Ljava/lang/StringBuilder;

    invoke-direct {v2}, Ljava/lang/StringBuilder;-><init>()V

    const-string v3, "No enum "

    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v2

    const-class v3, Lcom/vivox/service/vx_event_type;

    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    move-result-object v2

    const-string v3, " with value "

    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v2

    invoke-virtual {v2, p0}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    move-result-object v2

    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v2

    invoke-direct {v1, v2}, Ljava/lang/IllegalArgumentException;-><init>(Ljava/lang/String;)V

    throw v1
.end method


# virtual methods
.method public final swigValue()I
    .locals 1

    .prologue
    .line 75
    iget v0, p0, Lcom/vivox/service/vx_event_type;->swigValue:I

    return v0
.end method

.method public toString()Ljava/lang/String;
    .locals 1

    .prologue
    .line 79
    iget-object v0, p0, Lcom/vivox/service/vx_event_type;->swigName:Ljava/lang/String;

    return-object v0
.end method
