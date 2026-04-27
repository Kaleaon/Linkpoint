.class public Lcom/vivox/service/vx_state_session_t;
.super Ljava/lang/Object;
.source "vx_state_session_t.java"


# instance fields
.field protected swigCMemOwn:Z

.field private swigCPtr:J


# direct methods
.method public constructor <init>()V
    .locals 3

    .prologue
    .line 8
    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->new_vx_state_session_t()J

    move-result-wide v0

    const/4 v2, 0x1

    invoke-direct {p0, v0, v1, v2}, Lcom/vivox/service/vx_state_session_t;-><init>(JZ)V

    .line 9
    return-void
.end method

.method protected constructor <init>(JZ)V
    .locals 1
    .param p1, "j"    # J
    .param p3, "z"    # Z

    .prologue
    .line 11
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 12
    iput-boolean p3, p0, Lcom/vivox/service/vx_state_session_t;->swigCMemOwn:Z

    .line 13
    iput-wide p1, p0, Lcom/vivox/service/vx_state_session_t;->swigCPtr:J

    .line 14
    return-void
.end method

.method protected static getCPtr(Lcom/vivox/service/vx_state_session_t;)J
    .locals 2
    .param p0, "com_vivox_service_vx_state_session_t"    # Lcom/vivox/service/vx_state_session_t;

    .prologue
    .line 17
    if-nez p0, :cond_0

    const-wide/16 v0, 0x0

    :goto_0
    return-wide v0

    :cond_0
    iget-wide v0, p0, Lcom/vivox/service/vx_state_session_t;->swigCPtr:J

    goto :goto_0
.end method


# virtual methods
.method public declared-synchronized delete()V
    .locals 4

    .prologue
    const-wide/16 v2, 0x0

    .line 21
    monitor-enter p0

    :try_start_0
    iget-wide v0, p0, Lcom/vivox/service/vx_state_session_t;->swigCPtr:J

    cmp-long v0, v0, v2

    if-eqz v0, :cond_0

    iget-boolean v0, p0, Lcom/vivox/service/vx_state_session_t;->swigCMemOwn:Z

    if-nez v0, :cond_1

    .line 22
    :cond_0
    const-wide/16 v0, 0x0

    iput-wide v0, p0, Lcom/vivox/service/vx_state_session_t;->swigCPtr:J
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    .line 27
    monitor-exit p0

    return-void

    .line 24
    :cond_1
    const/4 v0, 0x0

    :try_start_1
    iput-boolean v0, p0, Lcom/vivox/service/vx_state_session_t;->swigCMemOwn:Z

    .line 25
    new-instance v0, Ljava/lang/UnsupportedOperationException;

    const-string v1, "C++ destructor does not have public access"

    invoke-direct {v0, v1}, Ljava/lang/UnsupportedOperationException;-><init>(Ljava/lang/String;)V

    throw v0
    :try_end_1
    .catchall {:try_start_1 .. :try_end_1} :catchall_0

    .line 21
    :catchall_0
    move-exception v0

    monitor-exit p0

    throw v0
.end method

.method public getDurable_media_id()Ljava/lang/String;
    .locals 2

    .prologue
    .line 30
    iget-wide v0, p0, Lcom/vivox/service/vx_state_session_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_session_t_durable_media_id_get(JLcom/vivox/service/vx_state_session_t;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public getHas_audio()I
    .locals 2

    .prologue
    .line 34
    iget-wide v0, p0, Lcom/vivox/service/vx_state_session_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_session_t_has_audio_get(JLcom/vivox/service/vx_state_session_t;)I

    move-result v0

    return v0
.end method

.method public getHas_text()I
    .locals 2

    .prologue
    .line 38
    iget-wide v0, p0, Lcom/vivox/service/vx_state_session_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_session_t_has_text_get(JLcom/vivox/service/vx_state_session_t;)I

    move-result v0

    return v0
.end method

.method public getIs_audio_muted_for_me()I
    .locals 2

    .prologue
    .line 42
    iget-wide v0, p0, Lcom/vivox/service/vx_state_session_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_session_t_is_audio_muted_for_me_get(JLcom/vivox/service/vx_state_session_t;)I

    move-result v0

    return v0
.end method

.method public getIs_connected()I
    .locals 2

    .prologue
    .line 46
    iget-wide v0, p0, Lcom/vivox/service/vx_state_session_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_session_t_is_connected_get(JLcom/vivox/service/vx_state_session_t;)I

    move-result v0

    return v0
.end method

.method public getIs_focused()I
    .locals 2

    .prologue
    .line 50
    iget-wide v0, p0, Lcom/vivox/service/vx_state_session_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_session_t_is_focused_get(JLcom/vivox/service/vx_state_session_t;)I

    move-result v0

    return v0
.end method

.method public getIs_incoming()I
    .locals 2

    .prologue
    .line 54
    iget-wide v0, p0, Lcom/vivox/service/vx_state_session_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_session_t_is_incoming_get(JLcom/vivox/service/vx_state_session_t;)I

    move-result v0

    return v0
.end method

.method public getIs_positional()I
    .locals 2

    .prologue
    .line 58
    iget-wide v0, p0, Lcom/vivox/service/vx_state_session_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_session_t_is_positional_get(JLcom/vivox/service/vx_state_session_t;)I

    move-result v0

    return v0
.end method

.method public getIs_text_muted_for_me()I
    .locals 2

    .prologue
    .line 62
    iget-wide v0, p0, Lcom/vivox/service/vx_state_session_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_session_t_is_text_muted_for_me_get(JLcom/vivox/service/vx_state_session_t;)I

    move-result v0

    return v0
.end method

.method public getIs_transmitting()I
    .locals 2

    .prologue
    .line 66
    iget-wide v0, p0, Lcom/vivox/service/vx_state_session_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_session_t_is_transmitting_get(JLcom/vivox/service/vx_state_session_t;)I

    move-result v0

    return v0
.end method

.method public getName()Ljava/lang/String;
    .locals 2

    .prologue
    .line 70
    iget-wide v0, p0, Lcom/vivox/service/vx_state_session_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_session_t_name_get(JLcom/vivox/service/vx_state_session_t;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public getSession_font_id()I
    .locals 2

    .prologue
    .line 74
    iget-wide v0, p0, Lcom/vivox/service/vx_state_session_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_session_t_session_font_id_get(JLcom/vivox/service/vx_state_session_t;)I

    move-result v0

    return v0
.end method

.method public getSession_handle()Ljava/lang/String;
    .locals 2

    .prologue
    .line 78
    iget-wide v0, p0, Lcom/vivox/service/vx_state_session_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_session_t_session_handle_get(JLcom/vivox/service/vx_state_session_t;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public getState_participant_count()I
    .locals 2

    .prologue
    .line 82
    iget-wide v0, p0, Lcom/vivox/service/vx_state_session_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_session_t_state_participant_count_get(JLcom/vivox/service/vx_state_session_t;)I

    move-result v0

    return v0
.end method

.method public getState_participants()Lcom/vivox/service/SWIGTYPE_p_p_vx_state_participant;
    .locals 4

    .prologue
    .line 86
    iget-wide v2, p0, Lcom/vivox/service/vx_state_session_t;->swigCPtr:J

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_session_t_state_participants_get(JLcom/vivox/service/vx_state_session_t;)J

    move-result-wide v0

    .line 87
    .local v0, "vx_state_session_t_state_participants_get":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/SWIGTYPE_p_p_vx_state_participant;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/SWIGTYPE_p_p_vx_state_participant;-><init>(JZ)V

    goto :goto_0
.end method

.method public getUri()Ljava/lang/String;
    .locals 2

    .prologue
    .line 91
    iget-wide v0, p0, Lcom/vivox/service/vx_state_session_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_session_t_uri_get(JLcom/vivox/service/vx_state_session_t;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public getVolume()I
    .locals 2

    .prologue
    .line 95
    iget-wide v0, p0, Lcom/vivox/service/vx_state_session_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_session_t_volume_get(JLcom/vivox/service/vx_state_session_t;)I

    move-result v0

    return v0
.end method

.method public setDurable_media_id(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 99
    iget-wide v0, p0, Lcom/vivox/service/vx_state_session_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_session_t_durable_media_id_set(JLcom/vivox/service/vx_state_session_t;Ljava/lang/String;)V

    .line 100
    return-void
.end method

.method public setHas_audio(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 103
    iget-wide v0, p0, Lcom/vivox/service/vx_state_session_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_session_t_has_audio_set(JLcom/vivox/service/vx_state_session_t;I)V

    .line 104
    return-void
.end method

.method public setHas_text(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 107
    iget-wide v0, p0, Lcom/vivox/service/vx_state_session_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_session_t_has_text_set(JLcom/vivox/service/vx_state_session_t;I)V

    .line 108
    return-void
.end method

.method public setIs_audio_muted_for_me(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 111
    iget-wide v0, p0, Lcom/vivox/service/vx_state_session_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_session_t_is_audio_muted_for_me_set(JLcom/vivox/service/vx_state_session_t;I)V

    .line 112
    return-void
.end method

.method public setIs_connected(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 115
    iget-wide v0, p0, Lcom/vivox/service/vx_state_session_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_session_t_is_connected_set(JLcom/vivox/service/vx_state_session_t;I)V

    .line 116
    return-void
.end method

.method public setIs_focused(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 119
    iget-wide v0, p0, Lcom/vivox/service/vx_state_session_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_session_t_is_focused_set(JLcom/vivox/service/vx_state_session_t;I)V

    .line 120
    return-void
.end method

.method public setIs_incoming(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 123
    iget-wide v0, p0, Lcom/vivox/service/vx_state_session_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_session_t_is_incoming_set(JLcom/vivox/service/vx_state_session_t;I)V

    .line 124
    return-void
.end method

.method public setIs_positional(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 127
    iget-wide v0, p0, Lcom/vivox/service/vx_state_session_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_session_t_is_positional_set(JLcom/vivox/service/vx_state_session_t;I)V

    .line 128
    return-void
.end method

.method public setIs_text_muted_for_me(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 131
    iget-wide v0, p0, Lcom/vivox/service/vx_state_session_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_session_t_is_text_muted_for_me_set(JLcom/vivox/service/vx_state_session_t;I)V

    .line 132
    return-void
.end method

.method public setIs_transmitting(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 135
    iget-wide v0, p0, Lcom/vivox/service/vx_state_session_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_session_t_is_transmitting_set(JLcom/vivox/service/vx_state_session_t;I)V

    .line 136
    return-void
.end method

.method public setName(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 139
    iget-wide v0, p0, Lcom/vivox/service/vx_state_session_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_session_t_name_set(JLcom/vivox/service/vx_state_session_t;Ljava/lang/String;)V

    .line 140
    return-void
.end method

.method public setSession_font_id(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 143
    iget-wide v0, p0, Lcom/vivox/service/vx_state_session_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_session_t_session_font_id_set(JLcom/vivox/service/vx_state_session_t;I)V

    .line 144
    return-void
.end method

.method public setSession_handle(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 147
    iget-wide v0, p0, Lcom/vivox/service/vx_state_session_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_session_t_session_handle_set(JLcom/vivox/service/vx_state_session_t;Ljava/lang/String;)V

    .line 148
    return-void
.end method

.method public setState_participant_count(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 151
    iget-wide v0, p0, Lcom/vivox/service/vx_state_session_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_session_t_state_participant_count_set(JLcom/vivox/service/vx_state_session_t;I)V

    .line 152
    return-void
.end method

.method public setState_participants(Lcom/vivox/service/SWIGTYPE_p_p_vx_state_participant;)V
    .locals 4
    .param p1, "sWIGTYPE_p_p_vx_state_participant"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_state_participant;

    .prologue
    .line 155
    iget-wide v0, p0, Lcom/vivox/service/vx_state_session_t;->swigCPtr:J

    invoke-static {p1}, Lcom/vivox/service/SWIGTYPE_p_p_vx_state_participant;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_state_participant;)J

    move-result-wide v2

    invoke-static {v0, v1, p0, v2, v3}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_session_t_state_participants_set(JLcom/vivox/service/vx_state_session_t;J)V

    .line 156
    return-void
.end method

.method public setUri(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 159
    iget-wide v0, p0, Lcom/vivox/service/vx_state_session_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_session_t_uri_set(JLcom/vivox/service/vx_state_session_t;Ljava/lang/String;)V

    .line 160
    return-void
.end method

.method public setVolume(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 163
    iget-wide v0, p0, Lcom/vivox/service/vx_state_session_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_session_t_volume_set(JLcom/vivox/service/vx_state_session_t;I)V

    .line 164
    return-void
.end method
