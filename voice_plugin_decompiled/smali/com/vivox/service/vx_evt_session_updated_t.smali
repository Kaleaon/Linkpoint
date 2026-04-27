.class public Lcom/vivox/service/vx_evt_session_updated_t;
.super Ljava/lang/Object;
.source "vx_evt_session_updated_t.java"


# instance fields
.field protected swigCMemOwn:Z

.field private swigCPtr:J


# direct methods
.method public constructor <init>()V
    .locals 3

    .prologue
    .line 8
    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->new_vx_evt_session_updated_t()J

    move-result-wide v0

    const/4 v2, 0x1

    invoke-direct {p0, v0, v1, v2}, Lcom/vivox/service/vx_evt_session_updated_t;-><init>(JZ)V

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
    iput-boolean p3, p0, Lcom/vivox/service/vx_evt_session_updated_t;->swigCMemOwn:Z

    .line 13
    iput-wide p1, p0, Lcom/vivox/service/vx_evt_session_updated_t;->swigCPtr:J

    .line 14
    return-void
.end method

.method protected static getCPtr(Lcom/vivox/service/vx_evt_session_updated_t;)J
    .locals 2
    .param p0, "com_vivox_service_vx_evt_session_updated_t"    # Lcom/vivox/service/vx_evt_session_updated_t;

    .prologue
    .line 17
    if-nez p0, :cond_0

    const-wide/16 v0, 0x0

    :goto_0
    return-wide v0

    :cond_0
    iget-wide v0, p0, Lcom/vivox/service/vx_evt_session_updated_t;->swigCPtr:J

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
    iget-wide v0, p0, Lcom/vivox/service/vx_evt_session_updated_t;->swigCPtr:J

    cmp-long v0, v0, v2

    if-eqz v0, :cond_0

    iget-boolean v0, p0, Lcom/vivox/service/vx_evt_session_updated_t;->swigCMemOwn:Z

    if-nez v0, :cond_1

    .line 22
    :cond_0
    const-wide/16 v0, 0x0

    iput-wide v0, p0, Lcom/vivox/service/vx_evt_session_updated_t;->swigCPtr:J
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    .line 27
    monitor-exit p0

    return-void

    .line 24
    :cond_1
    const/4 v0, 0x0

    :try_start_1
    iput-boolean v0, p0, Lcom/vivox/service/vx_evt_session_updated_t;->swigCMemOwn:Z

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

.method public getBase()Lcom/vivox/service/vx_evt_base_t;
    .locals 4

    .prologue
    .line 30
    iget-wide v2, p0, Lcom/vivox/service/vx_evt_session_updated_t;->swigCPtr:J

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_evt_session_updated_t_base_get(JLcom/vivox/service/vx_evt_session_updated_t;)J

    move-result-wide v0

    .line 31
    .local v0, "vx_evt_session_updated_t_base_get":J
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

.method public getIs_ad_playing()I
    .locals 2

    .prologue
    .line 35
    iget-wide v0, p0, Lcom/vivox/service/vx_evt_session_updated_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_evt_session_updated_t_is_ad_playing_get(JLcom/vivox/service/vx_evt_session_updated_t;)I

    move-result v0

    return v0
.end method

.method public getIs_focused()I
    .locals 2

    .prologue
    .line 39
    iget-wide v0, p0, Lcom/vivox/service/vx_evt_session_updated_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_evt_session_updated_t_is_focused_get(JLcom/vivox/service/vx_evt_session_updated_t;)I

    move-result v0

    return v0
.end method

.method public getIs_muted()I
    .locals 2

    .prologue
    .line 43
    iget-wide v0, p0, Lcom/vivox/service/vx_evt_session_updated_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_evt_session_updated_t_is_muted_get(JLcom/vivox/service/vx_evt_session_updated_t;)I

    move-result v0

    return v0
.end method

.method public getIs_text_muted()I
    .locals 2

    .prologue
    .line 47
    iget-wide v0, p0, Lcom/vivox/service/vx_evt_session_updated_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_evt_session_updated_t_is_text_muted_get(JLcom/vivox/service/vx_evt_session_updated_t;)I

    move-result v0

    return v0
.end method

.method public getSession_font_id()I
    .locals 2

    .prologue
    .line 51
    iget-wide v0, p0, Lcom/vivox/service/vx_evt_session_updated_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_evt_session_updated_t_session_font_id_get(JLcom/vivox/service/vx_evt_session_updated_t;)I

    move-result v0

    return v0
.end method

.method public getSession_handle()Ljava/lang/String;
    .locals 2

    .prologue
    .line 55
    iget-wide v0, p0, Lcom/vivox/service/vx_evt_session_updated_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_evt_session_updated_t_session_handle_get(JLcom/vivox/service/vx_evt_session_updated_t;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public getSessiongroup_handle()Ljava/lang/String;
    .locals 2

    .prologue
    .line 59
    iget-wide v0, p0, Lcom/vivox/service/vx_evt_session_updated_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_evt_session_updated_t_sessiongroup_handle_get(JLcom/vivox/service/vx_evt_session_updated_t;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public getSpeaker_position()Lcom/vivox/service/SWIGTYPE_p_double;
    .locals 4

    .prologue
    .line 63
    iget-wide v2, p0, Lcom/vivox/service/vx_evt_session_updated_t;->swigCPtr:J

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_evt_session_updated_t_speaker_position_get(JLcom/vivox/service/vx_evt_session_updated_t;)J

    move-result-wide v0

    .line 64
    .local v0, "vx_evt_session_updated_t_speaker_position_get":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/SWIGTYPE_p_double;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/SWIGTYPE_p_double;-><init>(JZ)V

    goto :goto_0
.end method

.method public getTransmit_enabled()I
    .locals 2

    .prologue
    .line 68
    iget-wide v0, p0, Lcom/vivox/service/vx_evt_session_updated_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_evt_session_updated_t_transmit_enabled_get(JLcom/vivox/service/vx_evt_session_updated_t;)I

    move-result v0

    return v0
.end method

.method public getUri()Ljava/lang/String;
    .locals 2

    .prologue
    .line 72
    iget-wide v0, p0, Lcom/vivox/service/vx_evt_session_updated_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_evt_session_updated_t_uri_get(JLcom/vivox/service/vx_evt_session_updated_t;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public getVolume()I
    .locals 2

    .prologue
    .line 76
    iget-wide v0, p0, Lcom/vivox/service/vx_evt_session_updated_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_evt_session_updated_t_volume_get(JLcom/vivox/service/vx_evt_session_updated_t;)I

    move-result v0

    return v0
.end method

.method public setBase(Lcom/vivox/service/vx_evt_base_t;)V
    .locals 6
    .param p1, "com_vivox_service_vx_evt_base_t"    # Lcom/vivox/service/vx_evt_base_t;

    .prologue
    .line 80
    iget-wide v0, p0, Lcom/vivox/service/vx_evt_session_updated_t;->swigCPtr:J

    invoke-static {p1}, Lcom/vivox/service/vx_evt_base_t;->getCPtr(Lcom/vivox/service/vx_evt_base_t;)J

    move-result-wide v3

    move-object v2, p0

    move-object v5, p1

    invoke-static/range {v0 .. v5}, Lcom/vivox/service/VxClientProxyJNI;->vx_evt_session_updated_t_base_set(JLcom/vivox/service/vx_evt_session_updated_t;JLcom/vivox/service/vx_evt_base_t;)V

    .line 81
    return-void
.end method

.method public setIs_ad_playing(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 84
    iget-wide v0, p0, Lcom/vivox/service/vx_evt_session_updated_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_evt_session_updated_t_is_ad_playing_set(JLcom/vivox/service/vx_evt_session_updated_t;I)V

    .line 85
    return-void
.end method

.method public setIs_focused(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 88
    iget-wide v0, p0, Lcom/vivox/service/vx_evt_session_updated_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_evt_session_updated_t_is_focused_set(JLcom/vivox/service/vx_evt_session_updated_t;I)V

    .line 89
    return-void
.end method

.method public setIs_muted(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 92
    iget-wide v0, p0, Lcom/vivox/service/vx_evt_session_updated_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_evt_session_updated_t_is_muted_set(JLcom/vivox/service/vx_evt_session_updated_t;I)V

    .line 93
    return-void
.end method

.method public setIs_text_muted(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 96
    iget-wide v0, p0, Lcom/vivox/service/vx_evt_session_updated_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_evt_session_updated_t_is_text_muted_set(JLcom/vivox/service/vx_evt_session_updated_t;I)V

    .line 97
    return-void
.end method

.method public setSession_font_id(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 100
    iget-wide v0, p0, Lcom/vivox/service/vx_evt_session_updated_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_evt_session_updated_t_session_font_id_set(JLcom/vivox/service/vx_evt_session_updated_t;I)V

    .line 101
    return-void
.end method

.method public setSession_handle(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 104
    iget-wide v0, p0, Lcom/vivox/service/vx_evt_session_updated_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_evt_session_updated_t_session_handle_set(JLcom/vivox/service/vx_evt_session_updated_t;Ljava/lang/String;)V

    .line 105
    return-void
.end method

.method public setSessiongroup_handle(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 108
    iget-wide v0, p0, Lcom/vivox/service/vx_evt_session_updated_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_evt_session_updated_t_sessiongroup_handle_set(JLcom/vivox/service/vx_evt_session_updated_t;Ljava/lang/String;)V

    .line 109
    return-void
.end method

.method public setSpeaker_position(Lcom/vivox/service/SWIGTYPE_p_double;)V
    .locals 4
    .param p1, "sWIGTYPE_p_double"    # Lcom/vivox/service/SWIGTYPE_p_double;

    .prologue
    .line 112
    iget-wide v0, p0, Lcom/vivox/service/vx_evt_session_updated_t;->swigCPtr:J

    invoke-static {p1}, Lcom/vivox/service/SWIGTYPE_p_double;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_double;)J

    move-result-wide v2

    invoke-static {v0, v1, p0, v2, v3}, Lcom/vivox/service/VxClientProxyJNI;->vx_evt_session_updated_t_speaker_position_set(JLcom/vivox/service/vx_evt_session_updated_t;J)V

    .line 113
    return-void
.end method

.method public setTransmit_enabled(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 116
    iget-wide v0, p0, Lcom/vivox/service/vx_evt_session_updated_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_evt_session_updated_t_transmit_enabled_set(JLcom/vivox/service/vx_evt_session_updated_t;I)V

    .line 117
    return-void
.end method

.method public setUri(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 120
    iget-wide v0, p0, Lcom/vivox/service/vx_evt_session_updated_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_evt_session_updated_t_uri_set(JLcom/vivox/service/vx_evt_session_updated_t;Ljava/lang/String;)V

    .line 121
    return-void
.end method

.method public setVolume(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 124
    iget-wide v0, p0, Lcom/vivox/service/vx_evt_session_updated_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_evt_session_updated_t_volume_set(JLcom/vivox/service/vx_evt_session_updated_t;I)V

    .line 125
    return-void
.end method
