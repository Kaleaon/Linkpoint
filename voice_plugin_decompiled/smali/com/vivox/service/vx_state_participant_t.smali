.class public Lcom/vivox/service/vx_state_participant_t;
.super Ljava/lang/Object;
.source "vx_state_participant_t.java"


# instance fields
.field protected swigCMemOwn:Z

.field private swigCPtr:J


# direct methods
.method public constructor <init>()V
    .locals 3

    .prologue
    .line 8
    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->new_vx_state_participant_t()J

    move-result-wide v0

    const/4 v2, 0x1

    invoke-direct {p0, v0, v1, v2}, Lcom/vivox/service/vx_state_participant_t;-><init>(JZ)V

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
    iput-boolean p3, p0, Lcom/vivox/service/vx_state_participant_t;->swigCMemOwn:Z

    .line 13
    iput-wide p1, p0, Lcom/vivox/service/vx_state_participant_t;->swigCPtr:J

    .line 14
    return-void
.end method

.method protected static getCPtr(Lcom/vivox/service/vx_state_participant_t;)J
    .locals 2
    .param p0, "com_vivox_service_vx_state_participant_t"    # Lcom/vivox/service/vx_state_participant_t;

    .prologue
    .line 17
    if-nez p0, :cond_0

    const-wide/16 v0, 0x0

    :goto_0
    return-wide v0

    :cond_0
    iget-wide v0, p0, Lcom/vivox/service/vx_state_participant_t;->swigCPtr:J

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
    iget-wide v0, p0, Lcom/vivox/service/vx_state_participant_t;->swigCPtr:J

    cmp-long v0, v0, v2

    if-eqz v0, :cond_0

    iget-boolean v0, p0, Lcom/vivox/service/vx_state_participant_t;->swigCMemOwn:Z

    if-nez v0, :cond_1

    .line 22
    :cond_0
    const-wide/16 v0, 0x0

    iput-wide v0, p0, Lcom/vivox/service/vx_state_participant_t;->swigCPtr:J
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    .line 27
    monitor-exit p0

    return-void

    .line 24
    :cond_1
    const/4 v0, 0x0

    :try_start_1
    iput-boolean v0, p0, Lcom/vivox/service/vx_state_participant_t;->swigCMemOwn:Z

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

.method public getDisplay_name()Ljava/lang/String;
    .locals 2

    .prologue
    .line 30
    iget-wide v0, p0, Lcom/vivox/service/vx_state_participant_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_participant_t_display_name_get(JLcom/vivox/service/vx_state_participant_t;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public getEnergy()D
    .locals 2

    .prologue
    .line 34
    iget-wide v0, p0, Lcom/vivox/service/vx_state_participant_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_participant_t_energy_get(JLcom/vivox/service/vx_state_participant_t;)D

    move-result-wide v0

    return-wide v0
.end method

.method public getIs_anonymous_login()I
    .locals 2

    .prologue
    .line 38
    iget-wide v0, p0, Lcom/vivox/service/vx_state_participant_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_participant_t_is_anonymous_login_get(JLcom/vivox/service/vx_state_participant_t;)I

    move-result v0

    return v0
.end method

.method public getIs_audio_enabled()I
    .locals 2

    .prologue
    .line 42
    iget-wide v0, p0, Lcom/vivox/service/vx_state_participant_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_participant_t_is_audio_enabled_get(JLcom/vivox/service/vx_state_participant_t;)I

    move-result v0

    return v0
.end method

.method public getIs_audio_moderator_muted()I
    .locals 2

    .prologue
    .line 46
    iget-wide v0, p0, Lcom/vivox/service/vx_state_participant_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_participant_t_is_audio_moderator_muted_get(JLcom/vivox/service/vx_state_participant_t;)I

    move-result v0

    return v0
.end method

.method public getIs_audio_muted_for_me()I
    .locals 2

    .prologue
    .line 50
    iget-wide v0, p0, Lcom/vivox/service/vx_state_participant_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_participant_t_is_audio_muted_for_me_get(JLcom/vivox/service/vx_state_participant_t;)I

    move-result v0

    return v0
.end method

.method public getIs_hand_raised()I
    .locals 2

    .prologue
    .line 54
    iget-wide v0, p0, Lcom/vivox/service/vx_state_participant_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_participant_t_is_hand_raised_get(JLcom/vivox/service/vx_state_participant_t;)I

    move-result v0

    return v0
.end method

.method public getIs_speaking()I
    .locals 2

    .prologue
    .line 58
    iget-wide v0, p0, Lcom/vivox/service/vx_state_participant_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_participant_t_is_speaking_get(JLcom/vivox/service/vx_state_participant_t;)I

    move-result v0

    return v0
.end method

.method public getIs_text_enabled()I
    .locals 2

    .prologue
    .line 62
    iget-wide v0, p0, Lcom/vivox/service/vx_state_participant_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_participant_t_is_text_enabled_get(JLcom/vivox/service/vx_state_participant_t;)I

    move-result v0

    return v0
.end method

.method public getIs_text_moderator_muted()I
    .locals 2

    .prologue
    .line 66
    iget-wide v0, p0, Lcom/vivox/service/vx_state_participant_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_participant_t_is_text_moderator_muted_get(JLcom/vivox/service/vx_state_participant_t;)I

    move-result v0

    return v0
.end method

.method public getIs_text_muted_for_me()I
    .locals 2

    .prologue
    .line 70
    iget-wide v0, p0, Lcom/vivox/service/vx_state_participant_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_participant_t_is_text_muted_for_me_get(JLcom/vivox/service/vx_state_participant_t;)I

    move-result v0

    return v0
.end method

.method public getIs_typing()I
    .locals 2

    .prologue
    .line 74
    iget-wide v0, p0, Lcom/vivox/service/vx_state_participant_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_participant_t_is_typing_get(JLcom/vivox/service/vx_state_participant_t;)I

    move-result v0

    return v0
.end method

.method public getType()Lcom/vivox/service/vx_participant_type;
    .locals 2

    .prologue
    .line 78
    iget-wide v0, p0, Lcom/vivox/service/vx_state_participant_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_participant_t_type_get(JLcom/vivox/service/vx_state_participant_t;)I

    move-result v0

    invoke-static {v0}, Lcom/vivox/service/vx_participant_type;->swigToEnum(I)Lcom/vivox/service/vx_participant_type;

    move-result-object v0

    return-object v0
.end method

.method public getUri()Ljava/lang/String;
    .locals 2

    .prologue
    .line 82
    iget-wide v0, p0, Lcom/vivox/service/vx_state_participant_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_participant_t_uri_get(JLcom/vivox/service/vx_state_participant_t;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public getVolume()I
    .locals 2

    .prologue
    .line 86
    iget-wide v0, p0, Lcom/vivox/service/vx_state_participant_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_participant_t_volume_get(JLcom/vivox/service/vx_state_participant_t;)I

    move-result v0

    return v0
.end method

.method public setDisplay_name(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 90
    iget-wide v0, p0, Lcom/vivox/service/vx_state_participant_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_participant_t_display_name_set(JLcom/vivox/service/vx_state_participant_t;Ljava/lang/String;)V

    .line 91
    return-void
.end method

.method public setEnergy(D)V
    .locals 3
    .param p1, "d"    # D

    .prologue
    .line 94
    iget-wide v0, p0, Lcom/vivox/service/vx_state_participant_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1, p2}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_participant_t_energy_set(JLcom/vivox/service/vx_state_participant_t;D)V

    .line 95
    return-void
.end method

.method public setIs_anonymous_login(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 98
    iget-wide v0, p0, Lcom/vivox/service/vx_state_participant_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_participant_t_is_anonymous_login_set(JLcom/vivox/service/vx_state_participant_t;I)V

    .line 99
    return-void
.end method

.method public setIs_audio_enabled(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 102
    iget-wide v0, p0, Lcom/vivox/service/vx_state_participant_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_participant_t_is_audio_enabled_set(JLcom/vivox/service/vx_state_participant_t;I)V

    .line 103
    return-void
.end method

.method public setIs_audio_moderator_muted(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 106
    iget-wide v0, p0, Lcom/vivox/service/vx_state_participant_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_participant_t_is_audio_moderator_muted_set(JLcom/vivox/service/vx_state_participant_t;I)V

    .line 107
    return-void
.end method

.method public setIs_audio_muted_for_me(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 110
    iget-wide v0, p0, Lcom/vivox/service/vx_state_participant_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_participant_t_is_audio_muted_for_me_set(JLcom/vivox/service/vx_state_participant_t;I)V

    .line 111
    return-void
.end method

.method public setIs_hand_raised(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 114
    iget-wide v0, p0, Lcom/vivox/service/vx_state_participant_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_participant_t_is_hand_raised_set(JLcom/vivox/service/vx_state_participant_t;I)V

    .line 115
    return-void
.end method

.method public setIs_speaking(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 118
    iget-wide v0, p0, Lcom/vivox/service/vx_state_participant_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_participant_t_is_speaking_set(JLcom/vivox/service/vx_state_participant_t;I)V

    .line 119
    return-void
.end method

.method public setIs_text_enabled(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 122
    iget-wide v0, p0, Lcom/vivox/service/vx_state_participant_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_participant_t_is_text_enabled_set(JLcom/vivox/service/vx_state_participant_t;I)V

    .line 123
    return-void
.end method

.method public setIs_text_moderator_muted(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 126
    iget-wide v0, p0, Lcom/vivox/service/vx_state_participant_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_participant_t_is_text_moderator_muted_set(JLcom/vivox/service/vx_state_participant_t;I)V

    .line 127
    return-void
.end method

.method public setIs_text_muted_for_me(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 130
    iget-wide v0, p0, Lcom/vivox/service/vx_state_participant_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_participant_t_is_text_muted_for_me_set(JLcom/vivox/service/vx_state_participant_t;I)V

    .line 131
    return-void
.end method

.method public setIs_typing(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 134
    iget-wide v0, p0, Lcom/vivox/service/vx_state_participant_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_participant_t_is_typing_set(JLcom/vivox/service/vx_state_participant_t;I)V

    .line 135
    return-void
.end method

.method public setType(Lcom/vivox/service/vx_participant_type;)V
    .locals 3
    .param p1, "com_vivox_service_vx_participant_type"    # Lcom/vivox/service/vx_participant_type;

    .prologue
    .line 138
    iget-wide v0, p0, Lcom/vivox/service/vx_state_participant_t;->swigCPtr:J

    invoke-virtual {p1}, Lcom/vivox/service/vx_participant_type;->swigValue()I

    move-result v2

    invoke-static {v0, v1, p0, v2}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_participant_t_type_set(JLcom/vivox/service/vx_state_participant_t;I)V

    .line 139
    return-void
.end method

.method public setUri(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 142
    iget-wide v0, p0, Lcom/vivox/service/vx_state_participant_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_participant_t_uri_set(JLcom/vivox/service/vx_state_participant_t;Ljava/lang/String;)V

    .line 143
    return-void
.end method

.method public setVolume(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 146
    iget-wide v0, p0, Lcom/vivox/service/vx_state_participant_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_participant_t_volume_set(JLcom/vivox/service/vx_state_participant_t;I)V

    .line 147
    return-void
.end method
