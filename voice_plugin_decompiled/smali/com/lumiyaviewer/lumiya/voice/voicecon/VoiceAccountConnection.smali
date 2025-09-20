.class public Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceAccountConnection;
.super Ljava/lang/Object;
.source "VoiceAccountConnection.java"


# instance fields
.field private disposed:Z

.field private final handle:Ljava/lang/String;

.field private final messageController:Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;

.field private final voiceLoginInfo:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceLoginInfo;


# direct methods
.method public constructor <init>(Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceConnector;Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceLoginInfo;)V
    .locals 9
    .param p1, "messageController"    # Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;
    .param p2, "voiceConnector"    # Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceConnector;
    .param p3, "voiceLoginInfo"    # Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceLoginInfo;
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceException;
        }
    .end annotation

    .prologue
    const/4 v8, 0x1

    const/4 v7, 0x0

    .line 18
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 16
    iput-boolean v7, p0, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceAccountConnection;->disposed:Z

    .line 19
    iput-object p1, p0, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceAccountConnection;->messageController:Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;

    .line 20
    iput-object p3, p0, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceAccountConnection;->voiceLoginInfo:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceLoginInfo;

    .line 22
    new-instance v3, Lcom/vivox/service/vx_req_account_login_t;

    invoke-direct {v3}, Lcom/vivox/service/vx_req_account_login_t;-><init>()V

    .line 23
    .local v3, "req":Lcom/vivox/service/vx_req_account_login_t;
    iget-object v4, p3, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceLoginInfo;->voiceAccountServerName:Ljava/lang/String;

    invoke-virtual {v3, v4}, Lcom/vivox/service/vx_req_account_login_t;->setAcct_mgmt_server(Ljava/lang/String;)V

    .line 24
    invoke-virtual {p2}, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceConnector;->getHandle()Ljava/lang/String;

    move-result-object v4

    invoke-virtual {v3, v4}, Lcom/vivox/service/vx_req_account_login_t;->setConnector_handle(Ljava/lang/String;)V

    .line 25
    iget-object v4, p3, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceLoginInfo;->userName:Ljava/lang/String;

    invoke-virtual {v3, v4}, Lcom/vivox/service/vx_req_account_login_t;->setAcct_name(Ljava/lang/String;)V

    .line 26
    iget-object v4, p3, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceLoginInfo;->password:Ljava/lang/String;

    invoke-virtual {v3, v4}, Lcom/vivox/service/vx_req_account_login_t;->setAcct_password(Ljava/lang/String;)V

    .line 27
    sget-object v4, Lcom/vivox/service/vx_session_answer_mode;->mode_verify_answer:Lcom/vivox/service/vx_session_answer_mode;

    invoke-virtual {v3, v4}, Lcom/vivox/service/vx_req_account_login_t;->setAnswer_mode(Lcom/vivox/service/vx_session_answer_mode;)V

    .line 28
    invoke-virtual {v3, v7}, Lcom/vivox/service/vx_req_account_login_t;->setEnable_buddies_and_presence(I)V

    .line 29
    sget-object v4, Lcom/vivox/service/vx_buddy_management_mode;->mode_application:Lcom/vivox/service/vx_buddy_management_mode;

    invoke-virtual {v3, v4}, Lcom/vivox/service/vx_req_account_login_t;->setBuddy_management_mode(Lcom/vivox/service/vx_buddy_management_mode;)V

    .line 30
    const/16 v4, 0xa

    invoke-virtual {v3, v4}, Lcom/vivox/service/vx_req_account_login_t;->setParticipant_property_frequency(I)V

    .line 32
    const-string v4, "Voice: sending login request"

    new-array v5, v7, [Ljava/lang/Object;

    invoke-static {v4, v5}, Lcom/lumiyaviewer/lumiya/voice/Debug;->Printf(Ljava/lang/String;[Ljava/lang/Object;)V

    .line 34
    invoke-virtual {v3}, Lcom/vivox/service/vx_req_account_login_t;->getBase()Lcom/vivox/service/vx_req_base_t;

    move-result-object v4

    invoke-virtual {p1, v4}, Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;->sendRequestAndWait(Lcom/vivox/service/vx_req_base_t;)Lcom/vivox/service/vx_resp_base_t;

    move-result-object v1

    .line 35
    .local v1, "accountLoginResponse":Lcom/vivox/service/vx_resp_base_t;
    if-nez v1, :cond_0

    .line 36
    new-instance v4, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceException;

    const-string v5, "Failed to login"

    invoke-direct {v4, v5}, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceException;-><init>(Ljava/lang/String;)V

    throw v4

    .line 38
    :cond_0
    const-string v4, "Voice: got login response %s"

    new-array v5, v8, [Ljava/lang/Object;

    aput-object v1, v5, v7

    invoke-static {v4, v5}, Lcom/lumiyaviewer/lumiya/voice/Debug;->Printf(Ljava/lang/String;[Ljava/lang/Object;)V

    .line 40
    invoke-virtual {v1}, Lcom/vivox/service/vx_resp_base_t;->getMessage()Lcom/vivox/service/vx_message_base_t;

    move-result-object v4

    invoke-static {v4}, Lcom/vivox/service/VxClientProxy;->vx_message_base_t2vx_resp_account_login_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_account_login_t;

    move-result-object v2

    .line 41
    .local v2, "loginResponse":Lcom/vivox/service/vx_resp_account_login_t;
    if-nez v2, :cond_1

    .line 42
    new-instance v4, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceException;

    const-string v5, "Failed to login"

    invoke-direct {v4, v5}, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceException;-><init>(Ljava/lang/String;)V

    throw v4

    .line 44
    :cond_1
    const-string v4, "Voice: status code %d"

    new-array v5, v8, [Ljava/lang/Object;

    invoke-virtual {v2}, Lcom/vivox/service/vx_resp_account_login_t;->getBase()Lcom/vivox/service/vx_resp_base_t;

    move-result-object v6

    invoke-virtual {v6}, Lcom/vivox/service/vx_resp_base_t;->getStatus_code()I

    move-result v6

    invoke-static {v6}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v6

    aput-object v6, v5, v7

    invoke-static {v4, v5}, Lcom/lumiyaviewer/lumiya/voice/Debug;->Printf(Ljava/lang/String;[Ljava/lang/Object;)V

    .line 45
    const-string v4, "Voice: return code %d"

    new-array v5, v8, [Ljava/lang/Object;

    invoke-virtual {v2}, Lcom/vivox/service/vx_resp_account_login_t;->getBase()Lcom/vivox/service/vx_resp_base_t;

    move-result-object v6

    invoke-virtual {v6}, Lcom/vivox/service/vx_resp_base_t;->getReturn_code()I

    move-result v6

    invoke-static {v6}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v6

    aput-object v6, v5, v7

    invoke-static {v4, v5}, Lcom/lumiyaviewer/lumiya/voice/Debug;->Printf(Ljava/lang/String;[Ljava/lang/Object;)V

    .line 46
    const-string v4, "Voice: status string %s"

    new-array v5, v8, [Ljava/lang/Object;

    invoke-virtual {v2}, Lcom/vivox/service/vx_resp_account_login_t;->getBase()Lcom/vivox/service/vx_resp_base_t;

    move-result-object v6

    invoke-virtual {v6}, Lcom/vivox/service/vx_resp_base_t;->getStatus_string()Ljava/lang/String;

    move-result-object v6

    aput-object v6, v5, v7

    invoke-static {v4, v5}, Lcom/lumiyaviewer/lumiya/voice/Debug;->Printf(Ljava/lang/String;[Ljava/lang/Object;)V

    .line 47
    const-string v4, "Voice: ext status %s"

    new-array v5, v8, [Ljava/lang/Object;

    invoke-virtual {v2}, Lcom/vivox/service/vx_resp_account_login_t;->getBase()Lcom/vivox/service/vx_resp_base_t;

    move-result-object v6

    invoke-virtual {v6}, Lcom/vivox/service/vx_resp_base_t;->getExtended_status_info()Ljava/lang/String;

    move-result-object v6

    aput-object v6, v5, v7

    invoke-static {v4, v5}, Lcom/lumiyaviewer/lumiya/voice/Debug;->Printf(Ljava/lang/String;[Ljava/lang/Object;)V

    .line 49
    invoke-virtual {v2}, Lcom/vivox/service/vx_resp_account_login_t;->getAccount_handle()Ljava/lang/String;

    move-result-object v0

    .line 51
    .local v0, "accountHandle":Ljava/lang/String;
    if-nez v0, :cond_2

    .line 52
    new-instance v4, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceException;

    const-string v5, "Failed to login"

    invoke-direct {v4, v5}, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceException;-><init>(Ljava/lang/String;)V

    throw v4

    .line 54
    :cond_2
    iput-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceAccountConnection;->handle:Ljava/lang/String;

    .line 56
    return-void
.end method


# virtual methods
.method public createVoiceSession(Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;Ljava/lang/String;)V
    .locals 8
    .param p1, "channelInfo"    # Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;
    .param p2, "channelCredentials"    # Ljava/lang/String;
        .annotation runtime Ljavax/annotation/Nullable;
        .end annotation
    .end param
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceException;
        }
    .end annotation

    .prologue
    const/4 v7, 0x1

    const/4 v6, 0x0

    .line 80
    new-instance v0, Lcom/vivox/service/vx_req_session_create_t;

    invoke-direct {v0}, Lcom/vivox/service/vx_req_session_create_t;-><init>()V

    .line 81
    .local v0, "createSessionRequest":Lcom/vivox/service/vx_req_session_create_t;
    iget-object v4, p0, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceAccountConnection;->handle:Ljava/lang/String;

    invoke-virtual {v0, v4}, Lcom/vivox/service/vx_req_session_create_t;->setAccount_handle(Ljava/lang/String;)V

    .line 82
    if-eqz p2, :cond_0

    .line 83
    invoke-virtual {v0, p2}, Lcom/vivox/service/vx_req_session_create_t;->setPassword(Ljava/lang/String;)V

    .line 84
    sget-object v4, Lcom/vivox/service/vx_password_hash_algorithm_t;->password_hash_algorithm_sha1_username_hash:Lcom/vivox/service/vx_password_hash_algorithm_t;

    invoke-virtual {v0, v4}, Lcom/vivox/service/vx_req_session_create_t;->setPassword_hash_algorithm(Lcom/vivox/service/vx_password_hash_algorithm_t;)V

    .line 87
    :cond_0
    iget-object v4, p1, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;->voiceChannelURI:Ljava/lang/String;

    invoke-virtual {v0, v4}, Lcom/vivox/service/vx_req_session_create_t;->setUri(Ljava/lang/String;)V

    .line 88
    invoke-virtual {v0, v7}, Lcom/vivox/service/vx_req_session_create_t;->setConnect_audio(I)V

    .line 89
    invoke-virtual {v0, v6}, Lcom/vivox/service/vx_req_session_create_t;->setConnect_text(I)V

    .line 90
    invoke-virtual {v0, v6}, Lcom/vivox/service/vx_req_session_create_t;->setSession_font_id(I)V

    .line 92
    const-string v4, "Voice: sending session create request"

    new-array v5, v6, [Ljava/lang/Object;

    invoke-static {v4, v5}, Lcom/lumiyaviewer/lumiya/voice/Debug;->Printf(Ljava/lang/String;[Ljava/lang/Object;)V

    .line 94
    iget-object v4, p0, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceAccountConnection;->messageController:Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;

    invoke-virtual {v0}, Lcom/vivox/service/vx_req_session_create_t;->getBase()Lcom/vivox/service/vx_req_base_t;

    move-result-object v5

    invoke-virtual {v4, v5}, Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;->sendRequestAndWait(Lcom/vivox/service/vx_req_base_t;)Lcom/vivox/service/vx_resp_base_t;

    move-result-object v1

    .line 96
    .local v1, "sessionCreateResponse":Lcom/vivox/service/vx_resp_base_t;
    const-string v4, "Voice: got session create response: %s"

    new-array v5, v7, [Ljava/lang/Object;

    aput-object v1, v5, v6

    invoke-static {v4, v5}, Lcom/lumiyaviewer/lumiya/voice/Debug;->Printf(Ljava/lang/String;[Ljava/lang/Object;)V

    .line 98
    if-nez v1, :cond_1

    .line 99
    new-instance v4, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceException;

    const-string v5, "Failed to create voice session"

    invoke-direct {v4, v5}, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceException;-><init>(Ljava/lang/String;)V

    throw v4

    .line 101
    :cond_1
    invoke-virtual {v1}, Lcom/vivox/service/vx_resp_base_t;->getMessage()Lcom/vivox/service/vx_message_base_t;

    move-result-object v4

    invoke-static {v4}, Lcom/vivox/service/VxClientProxy;->vx_message_base_t2vx_resp_session_create_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_session_create_t;

    move-result-object v3

    .line 102
    .local v3, "sessionResponse":Lcom/vivox/service/vx_resp_session_create_t;
    if-nez v3, :cond_2

    .line 103
    new-instance v4, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceException;

    const-string v5, "Failed to create voice session"

    invoke-direct {v4, v5}, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceException;-><init>(Ljava/lang/String;)V

    throw v4

    .line 105
    :cond_2
    invoke-virtual {v3}, Lcom/vivox/service/vx_resp_session_create_t;->getSession_handle()Ljava/lang/String;

    move-result-object v2

    .line 107
    .local v2, "sessionHandle":Ljava/lang/String;
    if-nez v2, :cond_3

    .line 108
    new-instance v4, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceException;

    const-string v5, "Failed to create voice session"

    invoke-direct {v4, v5}, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceException;-><init>(Ljava/lang/String;)V

    throw v4

    .line 109
    :cond_3
    return-void
.end method

.method public dispose()V
    .locals 3

    .prologue
    .line 59
    iget-boolean v1, p0, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceAccountConnection;->disposed:Z

    if-nez v1, :cond_0

    .line 60
    const/4 v1, 0x1

    iput-boolean v1, p0, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceAccountConnection;->disposed:Z

    .line 62
    new-instance v0, Lcom/vivox/service/vx_req_account_logout_t;

    invoke-direct {v0}, Lcom/vivox/service/vx_req_account_logout_t;-><init>()V

    .line 63
    .local v0, "req":Lcom/vivox/service/vx_req_account_logout_t;
    iget-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceAccountConnection;->handle:Ljava/lang/String;

    invoke-virtual {v0, v1}, Lcom/vivox/service/vx_req_account_logout_t;->setAccount_handle(Ljava/lang/String;)V

    .line 64
    const-string v1, ""

    invoke-virtual {v0, v1}, Lcom/vivox/service/vx_req_account_logout_t;->setLogout_reason(Ljava/lang/String;)V

    .line 66
    iget-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceAccountConnection;->messageController:Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;

    invoke-virtual {v0}, Lcom/vivox/service/vx_req_account_logout_t;->getBase()Lcom/vivox/service/vx_req_base_t;

    move-result-object v2

    invoke-virtual {v1, v2}, Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;->sendRequestAndWait(Lcom/vivox/service/vx_req_base_t;)Lcom/vivox/service/vx_resp_base_t;

    .line 68
    .end local v0    # "req":Lcom/vivox/service/vx_req_account_logout_t;
    :cond_0
    return-void
.end method

.method public getHandle()Ljava/lang/String;
    .locals 1

    .prologue
    .line 71
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceAccountConnection;->handle:Ljava/lang/String;

    return-object v0
.end method

.method public getVoiceLoginInfo()Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceLoginInfo;
    .locals 1

    .prologue
    .line 75
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceAccountConnection;->voiceLoginInfo:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceLoginInfo;

    return-object v0
.end method
