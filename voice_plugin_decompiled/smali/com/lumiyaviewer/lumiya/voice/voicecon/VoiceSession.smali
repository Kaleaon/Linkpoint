.class public Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;
.super Ljava/lang/Object;
.source "VoiceSession.java"


# instance fields
.field private disposed:Z

.field private final handle:Ljava/lang/String;

.field private final isIncoming:Z

.field private localMicActive:Z

.field private final messageController:Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;

.field private previousState:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo$VoiceChatState;

.field private final sessionGroupHandle:Ljava/lang/String;

.field private final speakers:Ljava/util/Set;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/Set",
            "<",
            "Ljava/util/UUID;",
            ">;"
        }
    .end annotation
.end field

.field private state:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo$VoiceChatState;

.field private final stateLock:Ljava/lang/Object;

.field private final voiceChannelInfo:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;


# direct methods
.method public constructor <init>(Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;Lcom/vivox/service/vx_evt_session_added_t;Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;)V
    .locals 5
    .param p1, "messageController"    # Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;
    .param p2, "addedSession"    # Lcom/vivox/service/vx_evt_session_added_t;
    .param p3, "voiceChannelInfo"    # Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;
        .annotation runtime Ljavax/annotation/Nullable;
        .end annotation
    .end param

    .prologue
    const/4 v1, 0x1

    const/4 v2, 0x0

    .line 33
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 25
    iput-boolean v2, p0, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;->disposed:Z

    .line 27
    new-instance v0, Ljava/lang/Object;

    invoke-direct {v0}, Ljava/lang/Object;-><init>()V

    iput-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;->stateLock:Ljava/lang/Object;

    .line 28
    sget-object v0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo$VoiceChatState;->Connecting:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo$VoiceChatState;

    iput-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;->previousState:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo$VoiceChatState;

    .line 29
    sget-object v0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo$VoiceChatState;->Connecting:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo$VoiceChatState;

    iput-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;->state:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo$VoiceChatState;

    .line 30
    new-instance v0, Ljava/util/HashSet;

    invoke-direct {v0}, Ljava/util/HashSet;-><init>()V

    iput-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;->speakers:Ljava/util/Set;

    .line 31
    iput-boolean v2, p0, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;->localMicActive:Z

    .line 34
    iput-object p1, p0, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;->messageController:Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;

    .line 35
    invoke-virtual {p2}, Lcom/vivox/service/vx_evt_session_added_t;->getSession_handle()Ljava/lang/String;

    move-result-object v0

    iput-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;->handle:Ljava/lang/String;

    .line 36
    invoke-virtual {p2}, Lcom/vivox/service/vx_evt_session_added_t;->getSessiongroup_handle()Ljava/lang/String;

    move-result-object v0

    iput-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;->sessionGroupHandle:Ljava/lang/String;

    .line 37
    if-eqz p3, :cond_0

    .end local p3    # "voiceChannelInfo":Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;
    :goto_0
    iput-object p3, p0, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;->voiceChannelInfo:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;

    .line 38
    invoke-virtual {p2}, Lcom/vivox/service/vx_evt_session_added_t;->getIncoming()I

    move-result v0

    if-eqz v0, :cond_1

    move v0, v1

    :goto_1
    iput-boolean v0, p0, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;->isIncoming:Z

    .line 40
    const-string v0, "Voice: created session: %s (uri %s)"

    const/4 v3, 0x2

    new-array v3, v3, [Ljava/lang/Object;

    iget-object v4, p0, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;->handle:Ljava/lang/String;

    aput-object v4, v3, v2

    iget-object v2, p0, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;->voiceChannelInfo:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;

    iget-object v2, v2, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;->voiceChannelURI:Ljava/lang/String;

    aput-object v2, v3, v1

    invoke-static {v0, v3}, Lcom/lumiyaviewer/lumiya/voice/Debug;->Printf(Ljava/lang/String;[Ljava/lang/Object;)V

    .line 41
    return-void

    .line 37
    .restart local p3    # "voiceChannelInfo":Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;
    :cond_0
    new-instance p3, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;

    .end local p3    # "voiceChannelInfo":Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;
    invoke-virtual {p2}, Lcom/vivox/service/vx_evt_session_added_t;->getUri()Ljava/lang/String;

    move-result-object v0

    invoke-direct {p3, v0, v2, v2}, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;-><init>(Ljava/lang/String;ZZ)V

    goto :goto_0

    :cond_1
    move v0, v2

    .line 38
    goto :goto_1
.end method


# virtual methods
.method public dispose()V
    .locals 3

    .prologue
    .line 44
    iget-boolean v1, p0, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;->disposed:Z

    if-nez v1, :cond_0

    .line 45
    const/4 v1, 0x1

    iput-boolean v1, p0, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;->disposed:Z

    .line 46
    new-instance v0, Lcom/vivox/service/vx_req_session_terminate_t;

    invoke-direct {v0}, Lcom/vivox/service/vx_req_session_terminate_t;-><init>()V

    .line 47
    .local v0, "req":Lcom/vivox/service/vx_req_session_terminate_t;
    iget-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;->handle:Ljava/lang/String;

    invoke-virtual {v0, v1}, Lcom/vivox/service/vx_req_session_terminate_t;->setSession_handle(Ljava/lang/String;)V

    .line 48
    iget-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;->messageController:Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;

    invoke-virtual {v0}, Lcom/vivox/service/vx_req_session_terminate_t;->getBase()Lcom/vivox/service/vx_req_base_t;

    move-result-object v2

    invoke-virtual {v1, v2}, Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;->sendRequestAndWait(Lcom/vivox/service/vx_req_base_t;)Lcom/vivox/service/vx_resp_base_t;

    .line 50
    .end local v0    # "req":Lcom/vivox/service/vx_req_session_terminate_t;
    :cond_0
    return-void
.end method

.method public getHandle()Ljava/lang/String;
    .locals 1

    .prologue
    .line 180
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;->handle:Ljava/lang/String;

    return-object v0
.end method

.method public getState()Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo$VoiceChatState;
    .locals 3

    .prologue
    .line 189
    iget-object v2, p0, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;->stateLock:Ljava/lang/Object;

    monitor-enter v2

    .line 190
    :try_start_0
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;->state:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo$VoiceChatState;

    .line 191
    .local v0, "state":Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo$VoiceChatState;
    monitor-exit v2

    .line 192
    return-object v0

    .line 191
    .end local v0    # "state":Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo$VoiceChatState;
    :catchall_0
    move-exception v1

    monitor-exit v2
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    throw v1
.end method

.method public getVoiceChannelInfo()Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;
    .locals 1

    .prologue
    .line 53
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;->voiceChannelInfo:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;

    return-object v0
.end method

.method public getVoiceChatInfo()Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;
    .locals 11
    .annotation runtime Ljavax/annotation/Nonnull;
    .end annotation

    .prologue
    const/4 v10, 0x1

    const/4 v9, 0x0

    .line 60
    iget-object v8, p0, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;->stateLock:Ljava/lang/Object;

    monitor-enter v8

    .line 62
    :try_start_0
    const-string v0, "Voice: got session state: %s (%s)"

    const/4 v1, 0x2

    new-array v1, v1, [Ljava/lang/Object;

    const/4 v2, 0x0

    iget-object v4, p0, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;->state:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo$VoiceChatState;

    aput-object v4, v1, v2

    const/4 v2, 0x1

    aput-object p0, v1, v2

    invoke-static {v0, v1}, Lcom/lumiyaviewer/lumiya/voice/Debug;->Printf(Ljava/lang/String;[Ljava/lang/Object;)V

    .line 64
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;->state:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo$VoiceChatState;

    sget-object v1, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo$VoiceChatState;->None:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo$VoiceChatState;

    if-ne v0, v1, :cond_0

    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;->previousState:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo$VoiceChatState;

    sget-object v1, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo$VoiceChatState;->None:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo$VoiceChatState;

    if-ne v0, v1, :cond_0

    .line 65
    invoke-static {}, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;->empty()Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;

    move-result-object v7

    .line 66
    .local v7, "result":Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;
    const-string v0, "Voice: returning empty session state"

    const/4 v1, 0x0

    new-array v1, v1, [Ljava/lang/Object;

    invoke-static {v0, v1}, Lcom/lumiyaviewer/lumiya/voice/Debug;->Printf(Ljava/lang/String;[Ljava/lang/Object;)V

    .line 78
    :goto_0
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;->state:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo$VoiceChatState;

    iput-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;->previousState:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo$VoiceChatState;

    .line 79
    monitor-exit v8
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    .line 81
    const-string v0, "Voice: returning session state: %s"

    new-array v1, v10, [Ljava/lang/Object;

    iget-object v2, v7, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;->state:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo$VoiceChatState;

    aput-object v2, v1, v9

    invoke-static {v0, v1}, Lcom/lumiyaviewer/lumiya/voice/Debug;->Printf(Ljava/lang/String;[Ljava/lang/Object;)V

    .line 83
    return-object v7

    .line 68
    .end local v7    # "result":Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;
    :cond_0
    const/4 v3, 0x0

    .line 70
    .local v3, "activeSpeakerID":Ljava/util/UUID;
    :try_start_1
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;->speakers:Ljava/util/Set;

    invoke-interface {v0}, Ljava/util/Set;->isEmpty()Z

    move-result v0

    if-nez v0, :cond_1

    .line 71
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;->speakers:Ljava/util/Set;

    invoke-interface {v0}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object v6

    .line 72
    .local v6, "it":Ljava/util/Iterator;, "Ljava/util/Iterator<Ljava/util/UUID;>;"
    invoke-interface {v6}, Ljava/util/Iterator;->hasNext()Z

    move-result v0

    if-eqz v0, :cond_1

    .line 73
    invoke-interface {v6}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v3

    .end local v3    # "activeSpeakerID":Ljava/util/UUID;
    check-cast v3, Ljava/util/UUID;

    .line 75
    .end local v6    # "it":Ljava/util/Iterator;, "Ljava/util/Iterator<Ljava/util/UUID;>;"
    .restart local v3    # "activeSpeakerID":Ljava/util/UUID;
    :cond_1
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;->state:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo$VoiceChatState;

    iget-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;->previousState:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo$VoiceChatState;

    iget-object v2, p0, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;->speakers:Ljava/util/Set;

    invoke-interface {v2}, Ljava/util/Set;->size()I

    move-result v2

    iget-object v4, p0, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;->voiceChannelInfo:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;

    iget-boolean v4, v4, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;->isConference:Z

    iget-boolean v5, p0, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;->localMicActive:Z

    invoke-static/range {v0 .. v5}, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;->create(Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo$VoiceChatState;Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo$VoiceChatState;ILjava/util/UUID;ZZ)Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;

    move-result-object v7

    .restart local v7    # "result":Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;
    goto :goto_0

    .line 79
    .end local v3    # "activeSpeakerID":Ljava/util/UUID;
    .end local v7    # "result":Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;
    :catchall_0
    move-exception v0

    monitor-exit v8
    :try_end_1
    .catchall {:try_start_1 .. :try_end_1} :catchall_0

    throw v0
.end method

.method public isIncoming()Z
    .locals 1

    .prologue
    .line 184
    iget-boolean v0, p0, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;->isIncoming:Z

    return v0
.end method

.method public mediaConnect()V
    .locals 3

    .prologue
    .line 164
    new-instance v0, Lcom/vivox/service/vx_req_session_media_connect_t;

    invoke-direct {v0}, Lcom/vivox/service/vx_req_session_media_connect_t;-><init>()V

    .line 165
    .local v0, "req":Lcom/vivox/service/vx_req_session_media_connect_t;
    iget-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;->handle:Ljava/lang/String;

    invoke-virtual {v0, v1}, Lcom/vivox/service/vx_req_session_media_connect_t;->setSession_handle(Ljava/lang/String;)V

    .line 166
    iget-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;->sessionGroupHandle:Ljava/lang/String;

    invoke-virtual {v0, v1}, Lcom/vivox/service/vx_req_session_media_connect_t;->setSessiongroup_handle(Ljava/lang/String;)V

    .line 167
    const/4 v1, 0x0

    invoke-virtual {v0, v1}, Lcom/vivox/service/vx_req_session_media_connect_t;->setSession_font_id(I)V

    .line 168
    iget-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;->messageController:Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;

    invoke-virtual {v0}, Lcom/vivox/service/vx_req_session_media_connect_t;->getBase()Lcom/vivox/service/vx_req_base_t;

    move-result-object v2

    invoke-virtual {v1, v2}, Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;->sendRequest(Lcom/vivox/service/vx_req_base_t;)V

    .line 169
    return-void
.end method

.method public mediaDisconnect(Lcom/vivox/service/vx_termination_status;)V
    .locals 3
    .param p1, "terminationStatus"    # Lcom/vivox/service/vx_termination_status;

    .prologue
    .line 172
    new-instance v0, Lcom/vivox/service/vx_req_session_media_disconnect_t;

    invoke-direct {v0}, Lcom/vivox/service/vx_req_session_media_disconnect_t;-><init>()V

    .line 173
    .local v0, "req":Lcom/vivox/service/vx_req_session_media_disconnect_t;
    iget-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;->handle:Ljava/lang/String;

    invoke-virtual {v0, v1}, Lcom/vivox/service/vx_req_session_media_disconnect_t;->setSession_handle(Ljava/lang/String;)V

    .line 174
    iget-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;->sessionGroupHandle:Ljava/lang/String;

    invoke-virtual {v0, v1}, Lcom/vivox/service/vx_req_session_media_disconnect_t;->setSessiongroup_handle(Ljava/lang/String;)V

    .line 175
    invoke-virtual {v0, p1}, Lcom/vivox/service/vx_req_session_media_disconnect_t;->setTermination_status(Lcom/vivox/service/vx_termination_status;)V

    .line 176
    iget-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;->messageController:Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;

    invoke-virtual {v0}, Lcom/vivox/service/vx_req_session_media_disconnect_t;->getBase()Lcom/vivox/service/vx_req_base_t;

    move-result-object v2

    invoke-virtual {v1, v2}, Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;->sendRequest(Lcom/vivox/service/vx_req_base_t;)V

    .line 177
    return-void
.end method

.method public set3DPosition(Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DPosition;Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DPosition;)V
    .locals 7
    .param p1, "speakerPosition"    # Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DPosition;
    .param p2, "listenerPosition"    # Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DPosition;

    .prologue
    const/4 v6, 0x2

    const/4 v5, 0x1

    const/4 v4, 0x0

    .line 121
    const-string v1, "Voice: set3D: speaker %s"

    new-array v2, v5, [Ljava/lang/Object;

    invoke-virtual {p1}, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DPosition;->toString()Ljava/lang/String;

    move-result-object v3

    aput-object v3, v2, v4

    invoke-static {v1, v2}, Lcom/lumiyaviewer/lumiya/voice/Debug;->Printf(Ljava/lang/String;[Ljava/lang/Object;)V

    .line 122
    const-string v1, "Voice: set3D: listener %s"

    new-array v2, v5, [Ljava/lang/Object;

    invoke-virtual {p2}, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DPosition;->toString()Ljava/lang/String;

    move-result-object v3

    aput-object v3, v2, v4

    invoke-static {v1, v2}, Lcom/lumiyaviewer/lumiya/voice/Debug;->Printf(Ljava/lang/String;[Ljava/lang/Object;)V

    .line 124
    new-instance v0, Lcom/vivox/service/vx_req_session_set_3d_position_t;

    invoke-direct {v0}, Lcom/vivox/service/vx_req_session_set_3d_position_t;-><init>()V

    .line 125
    .local v0, "req":Lcom/vivox/service/vx_req_session_set_3d_position_t;
    iget-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;->handle:Ljava/lang/String;

    invoke-virtual {v0, v1}, Lcom/vivox/service/vx_req_session_set_3d_position_t;->setSession_handle(Ljava/lang/String;)V

    .line 127
    iget-object v1, p1, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DPosition;->position:Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;

    iget v1, v1, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;->x:F

    float-to-double v2, v1

    invoke-static {v0, v4, v2, v3}, Lcom/vivox/service/VxClientProxy;->vx_req_session_set_3d_position_t_set_speaker_position_item(Lcom/vivox/service/vx_req_session_set_3d_position_t;ID)V

    .line 128
    iget-object v1, p1, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DPosition;->position:Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;

    iget v1, v1, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;->y:F

    float-to-double v2, v1

    invoke-static {v0, v5, v2, v3}, Lcom/vivox/service/VxClientProxy;->vx_req_session_set_3d_position_t_set_speaker_position_item(Lcom/vivox/service/vx_req_session_set_3d_position_t;ID)V

    .line 129
    iget-object v1, p1, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DPosition;->position:Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;

    iget v1, v1, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;->z:F

    float-to-double v2, v1

    invoke-static {v0, v6, v2, v3}, Lcom/vivox/service/VxClientProxy;->vx_req_session_set_3d_position_t_set_speaker_position_item(Lcom/vivox/service/vx_req_session_set_3d_position_t;ID)V

    .line 130
    iget-object v1, p1, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DPosition;->velocity:Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;

    iget v1, v1, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;->x:F

    float-to-double v2, v1

    invoke-static {v0, v4, v2, v3}, Lcom/vivox/service/VxClientProxy;->vx_req_session_set_3d_position_t_set_speaker_velocity_item(Lcom/vivox/service/vx_req_session_set_3d_position_t;ID)V

    .line 131
    iget-object v1, p1, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DPosition;->velocity:Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;

    iget v1, v1, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;->y:F

    float-to-double v2, v1

    invoke-static {v0, v5, v2, v3}, Lcom/vivox/service/VxClientProxy;->vx_req_session_set_3d_position_t_set_speaker_velocity_item(Lcom/vivox/service/vx_req_session_set_3d_position_t;ID)V

    .line 132
    iget-object v1, p1, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DPosition;->velocity:Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;

    iget v1, v1, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;->z:F

    float-to-double v2, v1

    invoke-static {v0, v6, v2, v3}, Lcom/vivox/service/VxClientProxy;->vx_req_session_set_3d_position_t_set_speaker_velocity_item(Lcom/vivox/service/vx_req_session_set_3d_position_t;ID)V

    .line 133
    iget-object v1, p1, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DPosition;->atOrientation:Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;

    iget v1, v1, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;->x:F

    float-to-double v2, v1

    invoke-static {v0, v4, v2, v3}, Lcom/vivox/service/VxClientProxy;->vx_req_session_set_3d_position_t_set_speaker_at_orientation_item(Lcom/vivox/service/vx_req_session_set_3d_position_t;ID)V

    .line 134
    iget-object v1, p1, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DPosition;->atOrientation:Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;

    iget v1, v1, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;->y:F

    float-to-double v2, v1

    invoke-static {v0, v5, v2, v3}, Lcom/vivox/service/VxClientProxy;->vx_req_session_set_3d_position_t_set_speaker_at_orientation_item(Lcom/vivox/service/vx_req_session_set_3d_position_t;ID)V

    .line 135
    iget-object v1, p1, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DPosition;->atOrientation:Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;

    iget v1, v1, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;->z:F

    float-to-double v2, v1

    invoke-static {v0, v6, v2, v3}, Lcom/vivox/service/VxClientProxy;->vx_req_session_set_3d_position_t_set_speaker_at_orientation_item(Lcom/vivox/service/vx_req_session_set_3d_position_t;ID)V

    .line 136
    iget-object v1, p1, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DPosition;->upOrientation:Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;

    iget v1, v1, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;->x:F

    float-to-double v2, v1

    invoke-static {v0, v4, v2, v3}, Lcom/vivox/service/VxClientProxy;->vx_req_session_set_3d_position_t_set_speaker_up_orientation_item(Lcom/vivox/service/vx_req_session_set_3d_position_t;ID)V

    .line 137
    iget-object v1, p1, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DPosition;->upOrientation:Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;

    iget v1, v1, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;->y:F

    float-to-double v2, v1

    invoke-static {v0, v5, v2, v3}, Lcom/vivox/service/VxClientProxy;->vx_req_session_set_3d_position_t_set_speaker_up_orientation_item(Lcom/vivox/service/vx_req_session_set_3d_position_t;ID)V

    .line 138
    iget-object v1, p1, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DPosition;->upOrientation:Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;

    iget v1, v1, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;->z:F

    float-to-double v2, v1

    invoke-static {v0, v6, v2, v3}, Lcom/vivox/service/VxClientProxy;->vx_req_session_set_3d_position_t_set_speaker_up_orientation_item(Lcom/vivox/service/vx_req_session_set_3d_position_t;ID)V

    .line 139
    iget-object v1, p1, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DPosition;->leftOrientation:Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;

    iget v1, v1, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;->x:F

    float-to-double v2, v1

    invoke-static {v0, v4, v2, v3}, Lcom/vivox/service/VxClientProxy;->vx_req_session_set_3d_position_t_set_speaker_left_orientation_item(Lcom/vivox/service/vx_req_session_set_3d_position_t;ID)V

    .line 140
    iget-object v1, p1, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DPosition;->leftOrientation:Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;

    iget v1, v1, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;->y:F

    float-to-double v2, v1

    invoke-static {v0, v5, v2, v3}, Lcom/vivox/service/VxClientProxy;->vx_req_session_set_3d_position_t_set_speaker_left_orientation_item(Lcom/vivox/service/vx_req_session_set_3d_position_t;ID)V

    .line 141
    iget-object v1, p1, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DPosition;->leftOrientation:Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;

    iget v1, v1, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;->z:F

    float-to-double v2, v1

    invoke-static {v0, v6, v2, v3}, Lcom/vivox/service/VxClientProxy;->vx_req_session_set_3d_position_t_set_speaker_left_orientation_item(Lcom/vivox/service/vx_req_session_set_3d_position_t;ID)V

    .line 143
    iget-object v1, p2, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DPosition;->position:Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;

    iget v1, v1, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;->x:F

    float-to-double v2, v1

    invoke-static {v0, v4, v2, v3}, Lcom/vivox/service/VxClientProxy;->vx_req_session_set_3d_position_t_set_listener_position_item(Lcom/vivox/service/vx_req_session_set_3d_position_t;ID)V

    .line 144
    iget-object v1, p2, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DPosition;->position:Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;

    iget v1, v1, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;->y:F

    float-to-double v2, v1

    invoke-static {v0, v5, v2, v3}, Lcom/vivox/service/VxClientProxy;->vx_req_session_set_3d_position_t_set_listener_position_item(Lcom/vivox/service/vx_req_session_set_3d_position_t;ID)V

    .line 145
    iget-object v1, p2, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DPosition;->position:Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;

    iget v1, v1, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;->z:F

    float-to-double v2, v1

    invoke-static {v0, v6, v2, v3}, Lcom/vivox/service/VxClientProxy;->vx_req_session_set_3d_position_t_set_listener_position_item(Lcom/vivox/service/vx_req_session_set_3d_position_t;ID)V

    .line 146
    iget-object v1, p2, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DPosition;->velocity:Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;

    iget v1, v1, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;->x:F

    float-to-double v2, v1

    invoke-static {v0, v4, v2, v3}, Lcom/vivox/service/VxClientProxy;->vx_req_session_set_3d_position_t_set_listener_velocity_item(Lcom/vivox/service/vx_req_session_set_3d_position_t;ID)V

    .line 147
    iget-object v1, p2, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DPosition;->velocity:Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;

    iget v1, v1, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;->y:F

    float-to-double v2, v1

    invoke-static {v0, v5, v2, v3}, Lcom/vivox/service/VxClientProxy;->vx_req_session_set_3d_position_t_set_listener_velocity_item(Lcom/vivox/service/vx_req_session_set_3d_position_t;ID)V

    .line 148
    iget-object v1, p2, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DPosition;->velocity:Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;

    iget v1, v1, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;->z:F

    float-to-double v2, v1

    invoke-static {v0, v6, v2, v3}, Lcom/vivox/service/VxClientProxy;->vx_req_session_set_3d_position_t_set_listener_velocity_item(Lcom/vivox/service/vx_req_session_set_3d_position_t;ID)V

    .line 149
    iget-object v1, p2, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DPosition;->atOrientation:Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;

    iget v1, v1, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;->x:F

    float-to-double v2, v1

    invoke-static {v0, v4, v2, v3}, Lcom/vivox/service/VxClientProxy;->vx_req_session_set_3d_position_t_set_listener_at_orientation_item(Lcom/vivox/service/vx_req_session_set_3d_position_t;ID)V

    .line 150
    iget-object v1, p2, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DPosition;->atOrientation:Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;

    iget v1, v1, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;->y:F

    float-to-double v2, v1

    invoke-static {v0, v5, v2, v3}, Lcom/vivox/service/VxClientProxy;->vx_req_session_set_3d_position_t_set_listener_at_orientation_item(Lcom/vivox/service/vx_req_session_set_3d_position_t;ID)V

    .line 151
    iget-object v1, p2, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DPosition;->atOrientation:Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;

    iget v1, v1, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;->z:F

    float-to-double v2, v1

    invoke-static {v0, v6, v2, v3}, Lcom/vivox/service/VxClientProxy;->vx_req_session_set_3d_position_t_set_listener_at_orientation_item(Lcom/vivox/service/vx_req_session_set_3d_position_t;ID)V

    .line 152
    iget-object v1, p2, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DPosition;->upOrientation:Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;

    iget v1, v1, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;->x:F

    float-to-double v2, v1

    invoke-static {v0, v4, v2, v3}, Lcom/vivox/service/VxClientProxy;->vx_req_session_set_3d_position_t_set_listener_up_orientation_item(Lcom/vivox/service/vx_req_session_set_3d_position_t;ID)V

    .line 153
    iget-object v1, p2, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DPosition;->upOrientation:Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;

    iget v1, v1, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;->y:F

    float-to-double v2, v1

    invoke-static {v0, v5, v2, v3}, Lcom/vivox/service/VxClientProxy;->vx_req_session_set_3d_position_t_set_listener_up_orientation_item(Lcom/vivox/service/vx_req_session_set_3d_position_t;ID)V

    .line 154
    iget-object v1, p2, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DPosition;->upOrientation:Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;

    iget v1, v1, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;->z:F

    float-to-double v2, v1

    invoke-static {v0, v6, v2, v3}, Lcom/vivox/service/VxClientProxy;->vx_req_session_set_3d_position_t_set_listener_up_orientation_item(Lcom/vivox/service/vx_req_session_set_3d_position_t;ID)V

    .line 155
    iget-object v1, p2, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DPosition;->leftOrientation:Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;

    iget v1, v1, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;->x:F

    float-to-double v2, v1

    invoke-static {v0, v4, v2, v3}, Lcom/vivox/service/VxClientProxy;->vx_req_session_set_3d_position_t_set_listener_left_orientation_item(Lcom/vivox/service/vx_req_session_set_3d_position_t;ID)V

    .line 156
    iget-object v1, p2, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DPosition;->leftOrientation:Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;

    iget v1, v1, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;->y:F

    float-to-double v2, v1

    invoke-static {v0, v5, v2, v3}, Lcom/vivox/service/VxClientProxy;->vx_req_session_set_3d_position_t_set_listener_left_orientation_item(Lcom/vivox/service/vx_req_session_set_3d_position_t;ID)V

    .line 157
    iget-object v1, p2, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DPosition;->leftOrientation:Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;

    iget v1, v1, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;->z:F

    float-to-double v2, v1

    invoke-static {v0, v6, v2, v3}, Lcom/vivox/service/VxClientProxy;->vx_req_session_set_3d_position_t_set_listener_left_orientation_item(Lcom/vivox/service/vx_req_session_set_3d_position_t;ID)V

    .line 159
    iget-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;->messageController:Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;

    invoke-virtual {v0}, Lcom/vivox/service/vx_req_session_set_3d_position_t;->getBase()Lcom/vivox/service/vx_req_base_t;

    move-result-object v2

    invoke-virtual {v1, v2}, Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;->sendRequest(Lcom/vivox/service/vx_req_base_t;)V

    .line 161
    return-void
.end method

.method public setLocalMicActive(Z)Z
    .locals 3
    .param p1, "localMicActive"    # Z

    .prologue
    .line 112
    iget-object v2, p0, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;->stateLock:Ljava/lang/Object;

    monitor-enter v2

    .line 113
    :try_start_0
    iget-boolean v1, p0, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;->localMicActive:Z

    if-eq p1, v1, :cond_0

    const/4 v0, 0x1

    .line 114
    .local v0, "changed":Z
    :goto_0
    iput-boolean p1, p0, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;->localMicActive:Z

    .line 115
    monitor-exit v2

    .line 116
    return v0

    .line 113
    .end local v0    # "changed":Z
    :cond_0
    const/4 v0, 0x0

    goto :goto_0

    .line 115
    :catchall_0
    move-exception v1

    monitor-exit v2
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    throw v1
.end method

.method public setSpeakerSpeaking(Ljava/util/UUID;Z)Z
    .locals 3
    .param p1, "speakerID"    # Ljava/util/UUID;
    .param p2, "isSpeaking"    # Z

    .prologue
    .line 101
    iget-object v2, p0, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;->stateLock:Ljava/lang/Object;

    monitor-enter v2

    .line 102
    if-eqz p2, :cond_0

    .line 103
    :try_start_0
    iget-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;->speakers:Ljava/util/Set;

    invoke-interface {v1, p1}, Ljava/util/Set;->add(Ljava/lang/Object;)Z

    move-result v0

    .line 106
    .local v0, "changed":Z
    :goto_0
    monitor-exit v2

    .line 107
    return v0

    .line 105
    .end local v0    # "changed":Z
    :cond_0
    iget-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;->speakers:Ljava/util/Set;

    invoke-interface {v1, p1}, Ljava/util/Set;->remove(Ljava/lang/Object;)Z

    move-result v0

    .restart local v0    # "changed":Z
    goto :goto_0

    .line 106
    .end local v0    # "changed":Z
    :catchall_0
    move-exception v1

    monitor-exit v2
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    throw v1
.end method

.method public setState(Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo$VoiceChatState;)Z
    .locals 6
    .param p1, "state"    # Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo$VoiceChatState;

    .prologue
    .line 87
    const/4 v0, 0x0

    .line 88
    .local v0, "changed":Z
    iget-object v2, p0, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;->stateLock:Ljava/lang/Object;

    monitor-enter v2

    .line 89
    :try_start_0
    iget-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;->state:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo$VoiceChatState;

    if-eq v1, p1, :cond_0

    .line 90
    iget-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;->state:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo$VoiceChatState;

    iput-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;->previousState:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo$VoiceChatState;

    .line 91
    iput-object p1, p0, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;->state:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo$VoiceChatState;

    .line 92
    const/4 v0, 0x1

    .line 93
    const-string v1, "Voice: new session state: %s (%s)"

    const/4 v3, 0x2

    new-array v3, v3, [Ljava/lang/Object;

    const/4 v4, 0x0

    iget-object v5, p0, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;->state:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo$VoiceChatState;

    aput-object v5, v3, v4

    const/4 v4, 0x1

    aput-object p0, v3, v4

    invoke-static {v1, v3}, Lcom/lumiyaviewer/lumiya/voice/Debug;->Printf(Ljava/lang/String;[Ljava/lang/Object;)V

    .line 95
    :cond_0
    monitor-exit v2

    .line 96
    return v0

    .line 95
    :catchall_0
    move-exception v1

    monitor-exit v2
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    throw v1
.end method
