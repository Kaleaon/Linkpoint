.class public Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceConnector;
.super Ljava/lang/Object;
.source "VoiceConnector.java"


# instance fields
.field private disposed:Z

.field private final handle:Ljava/lang/String;

.field private final messageController:Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;

.field private final voiceAccountServerName:Ljava/lang/String;


# direct methods
.method public constructor <init>(Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;Ljava/lang/String;)V
    .locals 9
    .param p1, "messageController"    # Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;
    .param p2, "voiceAccountServerName"    # Ljava/lang/String;
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceException;
        }
    .end annotation

    .prologue
    const/4 v8, 0x1

    const/4 v7, 0x0

    .line 15
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 13
    iput-boolean v7, p0, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceConnector;->disposed:Z

    .line 16
    iput-object p1, p0, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceConnector;->messageController:Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;

    .line 17
    iput-object p2, p0, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceConnector;->voiceAccountServerName:Ljava/lang/String;

    .line 19
    new-instance v2, Lcom/vivox/service/vx_req_connector_create_t;

    invoke-direct {v2}, Lcom/vivox/service/vx_req_connector_create_t;-><init>()V

    .line 20
    .local v2, "req":Lcom/vivox/service/vx_req_connector_create_t;
    sget-object v4, Lcom/vivox/service/vx_connector_mode;->connector_mode_normal:Lcom/vivox/service/vx_connector_mode;

    invoke-virtual {v2, v4}, Lcom/vivox/service/vx_req_connector_create_t;->setMode(Lcom/vivox/service/vx_connector_mode;)V

    .line 21
    invoke-virtual {v2, p2}, Lcom/vivox/service/vx_req_connector_create_t;->setAcct_mgmt_server(Ljava/lang/String;)V

    .line 22
    const/16 v4, 0x2ee0

    invoke-virtual {v2, v4}, Lcom/vivox/service/vx_req_connector_create_t;->setMinimum_port(I)V

    .line 23
    const v4, 0xfde8

    invoke-virtual {v2, v4}, Lcom/vivox/service/vx_req_connector_create_t;->setMaximum_port(I)V

    .line 24
    const-string v4, "V2 SDK"

    invoke-virtual {v2, v4}, Lcom/vivox/service/vx_req_connector_create_t;->setClient_name(Ljava/lang/String;)V

    .line 25
    sget-object v4, Lcom/vivox/service/vx_session_handle_type;->session_handle_type_legacy:Lcom/vivox/service/vx_session_handle_type;

    invoke-virtual {v2, v4}, Lcom/vivox/service/vx_req_connector_create_t;->setSession_handle_type(Lcom/vivox/service/vx_session_handle_type;)V

    .line 26
    const-string v4, ""

    invoke-virtual {v2, v4}, Lcom/vivox/service/vx_req_connector_create_t;->setApplication(Ljava/lang/String;)V

    .line 27
    const/16 v4, 0xc

    invoke-virtual {v2, v4}, Lcom/vivox/service/vx_req_connector_create_t;->setMax_calls(I)V

    .line 29
    invoke-virtual {v2}, Lcom/vivox/service/vx_req_connector_create_t;->getBase()Lcom/vivox/service/vx_req_base_t;

    move-result-object v4

    invoke-virtual {p1, v4}, Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;->sendRequestAndWait(Lcom/vivox/service/vx_req_base_t;)Lcom/vivox/service/vx_resp_base_t;

    move-result-object v3

    .line 30
    .local v3, "response":Lcom/vivox/service/vx_resp_base_t;
    if-nez v3, :cond_0

    .line 31
    new-instance v4, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceException;

    const-string v5, "Failed to create voice connector"

    invoke-direct {v4, v5}, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceException;-><init>(Ljava/lang/String;)V

    throw v4

    .line 33
    :cond_0
    invoke-virtual {v3}, Lcom/vivox/service/vx_resp_base_t;->getMessage()Lcom/vivox/service/vx_message_base_t;

    move-result-object v4

    invoke-static {v4}, Lcom/vivox/service/VxClientProxy;->vx_message_base_t2vx_resp_connector_create_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_connector_create_t;

    move-result-object v1

    .line 34
    .local v1, "connectorResponse":Lcom/vivox/service/vx_resp_connector_create_t;
    if-nez v1, :cond_1

    .line 35
    new-instance v4, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceException;

    const-string v5, "Failed to create voice connector"

    invoke-direct {v4, v5}, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceException;-><init>(Ljava/lang/String;)V

    throw v4

    .line 37
    :cond_1
    const-string v4, "Voice: status code %d"

    new-array v5, v8, [Ljava/lang/Object;

    invoke-virtual {v1}, Lcom/vivox/service/vx_resp_connector_create_t;->getBase()Lcom/vivox/service/vx_resp_base_t;

    move-result-object v6

    invoke-virtual {v6}, Lcom/vivox/service/vx_resp_base_t;->getStatus_code()I

    move-result v6

    invoke-static {v6}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v6

    aput-object v6, v5, v7

    invoke-static {v4, v5}, Lcom/lumiyaviewer/lumiya/voice/Debug;->Printf(Ljava/lang/String;[Ljava/lang/Object;)V

    .line 38
    const-string v4, "Voice: return code %d"

    new-array v5, v8, [Ljava/lang/Object;

    invoke-virtual {v1}, Lcom/vivox/service/vx_resp_connector_create_t;->getBase()Lcom/vivox/service/vx_resp_base_t;

    move-result-object v6

    invoke-virtual {v6}, Lcom/vivox/service/vx_resp_base_t;->getReturn_code()I

    move-result v6

    invoke-static {v6}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v6

    aput-object v6, v5, v7

    invoke-static {v4, v5}, Lcom/lumiyaviewer/lumiya/voice/Debug;->Printf(Ljava/lang/String;[Ljava/lang/Object;)V

    .line 39
    const-string v4, "Voice: status string %s"

    new-array v5, v8, [Ljava/lang/Object;

    invoke-virtual {v1}, Lcom/vivox/service/vx_resp_connector_create_t;->getBase()Lcom/vivox/service/vx_resp_base_t;

    move-result-object v6

    invoke-virtual {v6}, Lcom/vivox/service/vx_resp_base_t;->getStatus_string()Ljava/lang/String;

    move-result-object v6

    aput-object v6, v5, v7

    invoke-static {v4, v5}, Lcom/lumiyaviewer/lumiya/voice/Debug;->Printf(Ljava/lang/String;[Ljava/lang/Object;)V

    .line 40
    const-string v4, "Voice: ext status %s"

    new-array v5, v8, [Ljava/lang/Object;

    invoke-virtual {v1}, Lcom/vivox/service/vx_resp_connector_create_t;->getBase()Lcom/vivox/service/vx_resp_base_t;

    move-result-object v6

    invoke-virtual {v6}, Lcom/vivox/service/vx_resp_base_t;->getExtended_status_info()Ljava/lang/String;

    move-result-object v6

    aput-object v6, v5, v7

    invoke-static {v4, v5}, Lcom/lumiyaviewer/lumiya/voice/Debug;->Printf(Ljava/lang/String;[Ljava/lang/Object;)V

    .line 42
    invoke-virtual {v1}, Lcom/vivox/service/vx_resp_connector_create_t;->getConnector_handle()Ljava/lang/String;

    move-result-object v0

    .line 43
    .local v0, "connectorHandle":Ljava/lang/String;
    const-string v4, "Voice: got connector handle \'%s\'"

    new-array v5, v8, [Ljava/lang/Object;

    aput-object v0, v5, v7

    invoke-static {v4, v5}, Lcom/lumiyaviewer/lumiya/voice/Debug;->Printf(Ljava/lang/String;[Ljava/lang/Object;)V

    .line 45
    if-nez v0, :cond_2

    .line 46
    new-instance v4, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceException;

    const-string v5, "Failed to create voice connector"

    invoke-direct {v4, v5}, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceException;-><init>(Ljava/lang/String;)V

    throw v4

    .line 48
    :cond_2
    iput-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceConnector;->handle:Ljava/lang/String;

    .line 50
    return-void
.end method


# virtual methods
.method public createAccountConnection(Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceLoginInfo;)Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceAccountConnection;
    .locals 2
    .param p1, "voiceLoginInfo"    # Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceLoginInfo;
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceException;
        }
    .end annotation

    .prologue
    .line 71
    new-instance v0, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceAccountConnection;

    iget-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceConnector;->messageController:Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;

    invoke-direct {v0, v1, p0, p1}, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceAccountConnection;-><init>(Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceConnector;Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceLoginInfo;)V

    return-object v0
.end method

.method public dispose()V
    .locals 3

    .prologue
    .line 53
    iget-boolean v1, p0, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceConnector;->disposed:Z

    if-nez v1, :cond_0

    .line 54
    const/4 v1, 0x1

    iput-boolean v1, p0, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceConnector;->disposed:Z

    .line 55
    new-instance v0, Lcom/vivox/service/vx_req_connector_initiate_shutdown_t;

    invoke-direct {v0}, Lcom/vivox/service/vx_req_connector_initiate_shutdown_t;-><init>()V

    .line 56
    .local v0, "req":Lcom/vivox/service/vx_req_connector_initiate_shutdown_t;
    iget-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceConnector;->handle:Ljava/lang/String;

    invoke-virtual {v0, v1}, Lcom/vivox/service/vx_req_connector_initiate_shutdown_t;->setConnector_handle(Ljava/lang/String;)V

    .line 57
    const-string v1, "V2 SDK"

    invoke-virtual {v0, v1}, Lcom/vivox/service/vx_req_connector_initiate_shutdown_t;->setClient_name(Ljava/lang/String;)V

    .line 58
    iget-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceConnector;->messageController:Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;

    invoke-virtual {v0}, Lcom/vivox/service/vx_req_connector_initiate_shutdown_t;->getBase()Lcom/vivox/service/vx_req_base_t;

    move-result-object v2

    invoke-virtual {v1, v2}, Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;->sendRequestAndWait(Lcom/vivox/service/vx_req_base_t;)Lcom/vivox/service/vx_resp_base_t;

    .line 60
    .end local v0    # "req":Lcom/vivox/service/vx_req_connector_initiate_shutdown_t;
    :cond_0
    return-void
.end method

.method public getHandle()Ljava/lang/String;
    .locals 1

    .prologue
    .line 63
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceConnector;->handle:Ljava/lang/String;

    return-object v0
.end method

.method public getVoiceAccountServerName()Ljava/lang/String;
    .locals 1

    .prologue
    .line 67
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceConnector;->voiceAccountServerName:Ljava/lang/String;

    return-object v0
.end method

.method public setLocalMicVolume(I)V
    .locals 3
    .param p1, "volume"    # I

    .prologue
    .line 96
    new-instance v0, Lcom/vivox/service/vx_req_connector_set_local_mic_volume_t;

    invoke-direct {v0}, Lcom/vivox/service/vx_req_connector_set_local_mic_volume_t;-><init>()V

    .line 97
    .local v0, "req":Lcom/vivox/service/vx_req_connector_set_local_mic_volume_t;
    iget-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceConnector;->handle:Ljava/lang/String;

    invoke-virtual {v0, v1}, Lcom/vivox/service/vx_req_connector_set_local_mic_volume_t;->setConnector_handle(Ljava/lang/String;)V

    .line 98
    invoke-virtual {v0, p1}, Lcom/vivox/service/vx_req_connector_set_local_mic_volume_t;->setVolume(I)V

    .line 99
    iget-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceConnector;->messageController:Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;

    invoke-virtual {v0}, Lcom/vivox/service/vx_req_connector_set_local_mic_volume_t;->getBase()Lcom/vivox/service/vx_req_base_t;

    move-result-object v2

    invoke-virtual {v1, v2}, Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;->sendRequest(Lcom/vivox/service/vx_req_base_t;)V

    .line 100
    return-void
.end method

.method public setLocalSpeakerVolume(I)V
    .locals 3
    .param p1, "volume"    # I

    .prologue
    .line 89
    new-instance v0, Lcom/vivox/service/vx_req_connector_set_local_speaker_volume_t;

    invoke-direct {v0}, Lcom/vivox/service/vx_req_connector_set_local_speaker_volume_t;-><init>()V

    .line 90
    .local v0, "req":Lcom/vivox/service/vx_req_connector_set_local_speaker_volume_t;
    iget-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceConnector;->handle:Ljava/lang/String;

    invoke-virtual {v0, v1}, Lcom/vivox/service/vx_req_connector_set_local_speaker_volume_t;->setConnector_handle(Ljava/lang/String;)V

    .line 91
    invoke-virtual {v0, p1}, Lcom/vivox/service/vx_req_connector_set_local_speaker_volume_t;->setVolume(I)V

    .line 92
    iget-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceConnector;->messageController:Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;

    invoke-virtual {v0}, Lcom/vivox/service/vx_req_connector_set_local_speaker_volume_t;->getBase()Lcom/vivox/service/vx_req_base_t;

    move-result-object v2

    invoke-virtual {v1, v2}, Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;->sendRequest(Lcom/vivox/service/vx_req_base_t;)V

    .line 93
    return-void
.end method

.method public setMuteLocalMic(Z)V
    .locals 3
    .param p1, "mute"    # Z

    .prologue
    .line 82
    new-instance v0, Lcom/vivox/service/vx_req_connector_mute_local_mic_t;

    invoke-direct {v0}, Lcom/vivox/service/vx_req_connector_mute_local_mic_t;-><init>()V

    .line 83
    .local v0, "req":Lcom/vivox/service/vx_req_connector_mute_local_mic_t;
    iget-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceConnector;->handle:Ljava/lang/String;

    invoke-virtual {v0, v1}, Lcom/vivox/service/vx_req_connector_mute_local_mic_t;->setConnector_handle(Ljava/lang/String;)V

    .line 84
    if-eqz p1, :cond_0

    const/4 v1, 0x1

    :goto_0
    invoke-virtual {v0, v1}, Lcom/vivox/service/vx_req_connector_mute_local_mic_t;->setMute_level(I)V

    .line 85
    iget-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceConnector;->messageController:Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;

    invoke-virtual {v0}, Lcom/vivox/service/vx_req_connector_mute_local_mic_t;->getBase()Lcom/vivox/service/vx_req_base_t;

    move-result-object v2

    invoke-virtual {v1, v2}, Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;->sendRequest(Lcom/vivox/service/vx_req_base_t;)V

    .line 86
    return-void

    .line 84
    :cond_0
    const/4 v1, 0x0

    goto :goto_0
.end method

.method public setMuteLocalSpeaker(Z)V
    .locals 3
    .param p1, "mute"    # Z

    .prologue
    .line 75
    new-instance v0, Lcom/vivox/service/vx_req_connector_mute_local_speaker_t;

    invoke-direct {v0}, Lcom/vivox/service/vx_req_connector_mute_local_speaker_t;-><init>()V

    .line 76
    .local v0, "req":Lcom/vivox/service/vx_req_connector_mute_local_speaker_t;
    iget-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceConnector;->handle:Ljava/lang/String;

    invoke-virtual {v0, v1}, Lcom/vivox/service/vx_req_connector_mute_local_speaker_t;->setConnector_handle(Ljava/lang/String;)V

    .line 77
    if-eqz p1, :cond_0

    const/4 v1, 0x1

    :goto_0
    invoke-virtual {v0, v1}, Lcom/vivox/service/vx_req_connector_mute_local_speaker_t;->setMute_level(I)V

    .line 78
    iget-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceConnector;->messageController:Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;

    invoke-virtual {v0}, Lcom/vivox/service/vx_req_connector_mute_local_speaker_t;->getBase()Lcom/vivox/service/vx_req_base_t;

    move-result-object v2

    invoke-virtual {v1, v2}, Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;->sendRequest(Lcom/vivox/service/vx_req_base_t;)V

    .line 79
    return-void

    .line 77
    :cond_0
    const/4 v1, 0x0

    goto :goto_0
.end method
