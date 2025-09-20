.class public Lcom/vivox/service/vx_evt_participant_updated_t;
.super Ljava/lang/Object;
.source "vx_evt_participant_updated_t.java"


# instance fields
.field protected swigCMemOwn:Z

.field private swigCPtr:J


# direct methods
.method public constructor <init>()V
    .locals 3

    .prologue
    .line 8
    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->new_vx_evt_participant_updated_t()J

    move-result-wide v0

    const/4 v2, 0x1

    invoke-direct {p0, v0, v1, v2}, Lcom/vivox/service/vx_evt_participant_updated_t;-><init>(JZ)V

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
    iput-boolean p3, p0, Lcom/vivox/service/vx_evt_participant_updated_t;->swigCMemOwn:Z

    .line 13
    iput-wide p1, p0, Lcom/vivox/service/vx_evt_participant_updated_t;->swigCPtr:J

    .line 14
    return-void
.end method

.method protected static getCPtr(Lcom/vivox/service/vx_evt_participant_updated_t;)J
    .locals 2
    .param p0, "com_vivox_service_vx_evt_participant_updated_t"    # Lcom/vivox/service/vx_evt_participant_updated_t;

    .prologue
    .line 17
    if-nez p0, :cond_0

    const-wide/16 v0, 0x0

    :goto_0
    return-wide v0

    :cond_0
    iget-wide v0, p0, Lcom/vivox/service/vx_evt_participant_updated_t;->swigCPtr:J

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
    iget-wide v0, p0, Lcom/vivox/service/vx_evt_participant_updated_t;->swigCPtr:J

    cmp-long v0, v0, v2

    if-eqz v0, :cond_0

    iget-boolean v0, p0, Lcom/vivox/service/vx_evt_participant_updated_t;->swigCMemOwn:Z

    if-nez v0, :cond_1

    .line 22
    :cond_0
    const-wide/16 v0, 0x0

    iput-wide v0, p0, Lcom/vivox/service/vx_evt_participant_updated_t;->swigCPtr:J
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    .line 27
    monitor-exit p0

    return-void

    .line 24
    :cond_1
    const/4 v0, 0x0

    :try_start_1
    iput-boolean v0, p0, Lcom/vivox/service/vx_evt_participant_updated_t;->swigCMemOwn:Z

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

.method public getActive_media()I
    .locals 2

    .prologue
    .line 30
    iget-wide v0, p0, Lcom/vivox/service/vx_evt_participant_updated_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_evt_participant_updated_t_active_media_get(JLcom/vivox/service/vx_evt_participant_updated_t;)I

    move-result v0

    return v0
.end method

.method public getAlias_username()Ljava/lang/String;
    .locals 2

    .prologue
    .line 34
    iget-wide v0, p0, Lcom/vivox/service/vx_evt_participant_updated_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_evt_participant_updated_t_alias_username_get(JLcom/vivox/service/vx_evt_participant_updated_t;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public getBase()Lcom/vivox/service/vx_evt_base_t;
    .locals 4

    .prologue
    .line 38
    iget-wide v2, p0, Lcom/vivox/service/vx_evt_participant_updated_t;->swigCPtr:J

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_evt_participant_updated_t_base_get(JLcom/vivox/service/vx_evt_participant_updated_t;)J

    move-result-wide v0

    .line 39
    .local v0, "vx_evt_participant_updated_t_base_get":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_evt_base_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_evt_base_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public getDiagnostic_state_count()I
    .locals 2

    .prologue
    .line 43
    iget-wide v0, p0, Lcom/vivox/service/vx_evt_participant_updated_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_evt_participant_updated_t_diagnostic_state_count_get(JLcom/vivox/service/vx_evt_participant_updated_t;)I

    move-result v0

    return v0
.end method

.method public getDiagnostic_states()Lcom/vivox/service/SWIGTYPE_p_vx_participant_diagnostic_state_t;
    .locals 4

    .prologue
    .line 47
    iget-wide v2, p0, Lcom/vivox/service/vx_evt_participant_updated_t;->swigCPtr:J

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_evt_participant_updated_t_diagnostic_states_get(JLcom/vivox/service/vx_evt_participant_updated_t;)J

    move-result-wide v0

    .line 48
    .local v0, "vx_evt_participant_updated_t_diagnostic_states_get":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/SWIGTYPE_p_vx_participant_diagnostic_state_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/SWIGTYPE_p_vx_participant_diagnostic_state_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public getEnergy()D
    .locals 2

    .prologue
    .line 52
    iget-wide v0, p0, Lcom/vivox/service/vx_evt_participant_updated_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_evt_participant_updated_t_energy_get(JLcom/vivox/service/vx_evt_participant_updated_t;)D

    move-result-wide v0

    return-wide v0
.end method

.method public getIs_moderator_muted()I
    .locals 2

    .prologue
    .line 56
    iget-wide v0, p0, Lcom/vivox/service/vx_evt_participant_updated_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_evt_participant_updated_t_is_moderator_muted_get(JLcom/vivox/service/vx_evt_participant_updated_t;)I

    move-result v0

    return v0
.end method

.method public getIs_moderator_text_muted()I
    .locals 2

    .prologue
    .line 60
    iget-wide v0, p0, Lcom/vivox/service/vx_evt_participant_updated_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_evt_participant_updated_t_is_moderator_text_muted_get(JLcom/vivox/service/vx_evt_participant_updated_t;)I

    move-result v0

    return v0
.end method

.method public getIs_muted_for_me()I
    .locals 2

    .prologue
    .line 64
    iget-wide v0, p0, Lcom/vivox/service/vx_evt_participant_updated_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_evt_participant_updated_t_is_muted_for_me_get(JLcom/vivox/service/vx_evt_participant_updated_t;)I

    move-result v0

    return v0
.end method

.method public getIs_speaking()I
    .locals 2

    .prologue
    .line 68
    iget-wide v0, p0, Lcom/vivox/service/vx_evt_participant_updated_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_evt_participant_updated_t_is_speaking_get(JLcom/vivox/service/vx_evt_participant_updated_t;)I

    move-result v0

    return v0
.end method

.method public getIs_text_muted_for_me()I
    .locals 2

    .prologue
    .line 72
    iget-wide v0, p0, Lcom/vivox/service/vx_evt_participant_updated_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_evt_participant_updated_t_is_text_muted_for_me_get(JLcom/vivox/service/vx_evt_participant_updated_t;)I

    move-result v0

    return v0
.end method

.method public getParticipant_uri()Ljava/lang/String;
    .locals 2

    .prologue
    .line 76
    iget-wide v0, p0, Lcom/vivox/service/vx_evt_participant_updated_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_evt_participant_updated_t_participant_uri_get(JLcom/vivox/service/vx_evt_participant_updated_t;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public getSession_handle()Ljava/lang/String;
    .locals 2

    .prologue
    .line 80
    iget-wide v0, p0, Lcom/vivox/service/vx_evt_participant_updated_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_evt_participant_updated_t_session_handle_get(JLcom/vivox/service/vx_evt_participant_updated_t;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public getSessiongroup_handle()Ljava/lang/String;
    .locals 2

    .prologue
    .line 84
    iget-wide v0, p0, Lcom/vivox/service/vx_evt_participant_updated_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_evt_participant_updated_t_sessiongroup_handle_get(JLcom/vivox/service/vx_evt_participant_updated_t;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public getType()Lcom/vivox/service/vx_participant_type;
    .locals 2

    .prologue
    .line 88
    iget-wide v0, p0, Lcom/vivox/service/vx_evt_participant_updated_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_evt_participant_updated_t_type_get(JLcom/vivox/service/vx_evt_participant_updated_t;)I

    move-result v0

    invoke-static {v0}, Lcom/vivox/service/vx_participant_type;->swigToEnum(I)Lcom/vivox/service/vx_participant_type;

    move-result-object v0

    return-object v0
.end method

.method public getVolume()I
    .locals 2

    .prologue
    .line 92
    iget-wide v0, p0, Lcom/vivox/service/vx_evt_participant_updated_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_evt_participant_updated_t_volume_get(JLcom/vivox/service/vx_evt_participant_updated_t;)I

    move-result v0

    return v0
.end method

.method public setActive_media(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 96
    iget-wide v0, p0, Lcom/vivox/service/vx_evt_participant_updated_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_evt_participant_updated_t_active_media_set(JLcom/vivox/service/vx_evt_participant_updated_t;I)V

    .line 97
    return-void
.end method

.method public setAlias_username(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 100
    iget-wide v0, p0, Lcom/vivox/service/vx_evt_participant_updated_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_evt_participant_updated_t_alias_username_set(JLcom/vivox/service/vx_evt_participant_updated_t;Ljava/lang/String;)V

    .line 101
    return-void
.end method

.method public setBase(Lcom/vivox/service/vx_evt_base_t;)V
    .locals 6
    .param p1, "com_vivox_service_vx_evt_base_t"    # Lcom/vivox/service/vx_evt_base_t;

    .prologue
    .line 104
    iget-wide v0, p0, Lcom/vivox/service/vx_evt_participant_updated_t;->swigCPtr:J

    invoke-static {p1}, Lcom/vivox/service/vx_evt_base_t;->getCPtr(Lcom/vivox/service/vx_evt_base_t;)J

    move-result-wide v3

    move-object v2, p0

    move-object v5, p1

    invoke-static/range {v0 .. v5}, Lcom/vivox/service/VxClientProxyJNI;->vx_evt_participant_updated_t_base_set(JLcom/vivox/service/vx_evt_participant_updated_t;JLcom/vivox/service/vx_evt_base_t;)V

    .line 105
    return-void
.end method

.method public setDiagnostic_state_count(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 108
    iget-wide v0, p0, Lcom/vivox/service/vx_evt_participant_updated_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_evt_participant_updated_t_diagnostic_state_count_set(JLcom/vivox/service/vx_evt_participant_updated_t;I)V

    .line 109
    return-void
.end method

.method public setDiagnostic_states(Lcom/vivox/service/SWIGTYPE_p_vx_participant_diagnostic_state_t;)V
    .locals 4
    .param p1, "sWIGTYPE_p_vx_participant_diagnostic_state_t"    # Lcom/vivox/service/SWIGTYPE_p_vx_participant_diagnostic_state_t;

    .prologue
    .line 112
    iget-wide v0, p0, Lcom/vivox/service/vx_evt_participant_updated_t;->swigCPtr:J

    invoke-static {p1}, Lcom/vivox/service/SWIGTYPE_p_vx_participant_diagnostic_state_t;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_vx_participant_diagnostic_state_t;)J

    move-result-wide v2

    invoke-static {v0, v1, p0, v2, v3}, Lcom/vivox/service/VxClientProxyJNI;->vx_evt_participant_updated_t_diagnostic_states_set(JLcom/vivox/service/vx_evt_participant_updated_t;J)V

    .line 113
    return-void
.end method

.method public setEnergy(D)V
    .locals 3
    .param p1, "d"    # D

    .prologue
    .line 116
    iget-wide v0, p0, Lcom/vivox/service/vx_evt_participant_updated_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1, p2}, Lcom/vivox/service/VxClientProxyJNI;->vx_evt_participant_updated_t_energy_set(JLcom/vivox/service/vx_evt_participant_updated_t;D)V

    .line 117
    return-void
.end method

.method public setIs_moderator_muted(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 120
    iget-wide v0, p0, Lcom/vivox/service/vx_evt_participant_updated_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_evt_participant_updated_t_is_moderator_muted_set(JLcom/vivox/service/vx_evt_participant_updated_t;I)V

    .line 121
    return-void
.end method

.method public setIs_moderator_text_muted(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 124
    iget-wide v0, p0, Lcom/vivox/service/vx_evt_participant_updated_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_evt_participant_updated_t_is_moderator_text_muted_set(JLcom/vivox/service/vx_evt_participant_updated_t;I)V

    .line 125
    return-void
.end method

.method public setIs_muted_for_me(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 128
    iget-wide v0, p0, Lcom/vivox/service/vx_evt_participant_updated_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_evt_participant_updated_t_is_muted_for_me_set(JLcom/vivox/service/vx_evt_participant_updated_t;I)V

    .line 129
    return-void
.end method

.method public setIs_speaking(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 132
    iget-wide v0, p0, Lcom/vivox/service/vx_evt_participant_updated_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_evt_participant_updated_t_is_speaking_set(JLcom/vivox/service/vx_evt_participant_updated_t;I)V

    .line 133
    return-void
.end method

.method public setIs_text_muted_for_me(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 136
    iget-wide v0, p0, Lcom/vivox/service/vx_evt_participant_updated_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_evt_participant_updated_t_is_text_muted_for_me_set(JLcom/vivox/service/vx_evt_participant_updated_t;I)V

    .line 137
    return-void
.end method

.method public setParticipant_uri(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 140
    iget-wide v0, p0, Lcom/vivox/service/vx_evt_participant_updated_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_evt_participant_updated_t_participant_uri_set(JLcom/vivox/service/vx_evt_participant_updated_t;Ljava/lang/String;)V

    .line 141
    return-void
.end method

.method public setSession_handle(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 144
    iget-wide v0, p0, Lcom/vivox/service/vx_evt_participant_updated_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_evt_participant_updated_t_session_handle_set(JLcom/vivox/service/vx_evt_participant_updated_t;Ljava/lang/String;)V

    .line 145
    return-void
.end method

.method public setSessiongroup_handle(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 148
    iget-wide v0, p0, Lcom/vivox/service/vx_evt_participant_updated_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_evt_participant_updated_t_sessiongroup_handle_set(JLcom/vivox/service/vx_evt_participant_updated_t;Ljava/lang/String;)V

    .line 149
    return-void
.end method

.method public setType(Lcom/vivox/service/vx_participant_type;)V
    .locals 3
    .param p1, "com_vivox_service_vx_participant_type"    # Lcom/vivox/service/vx_participant_type;

    .prologue
    .line 152
    iget-wide v0, p0, Lcom/vivox/service/vx_evt_participant_updated_t;->swigCPtr:J

    invoke-virtual {p1}, Lcom/vivox/service/vx_participant_type;->swigValue()I

    move-result v2

    invoke-static {v0, v1, p0, v2}, Lcom/vivox/service/VxClientProxyJNI;->vx_evt_participant_updated_t_type_set(JLcom/vivox/service/vx_evt_participant_updated_t;I)V

    .line 153
    return-void
.end method

.method public setVolume(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 156
    iget-wide v0, p0, Lcom/vivox/service/vx_evt_participant_updated_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_evt_participant_updated_t_volume_set(JLcom/vivox/service/vx_evt_participant_updated_t;I)V

    .line 157
    return-void
.end method
