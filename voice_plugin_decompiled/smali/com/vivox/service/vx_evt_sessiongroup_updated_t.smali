.class public Lcom/vivox/service/vx_evt_sessiongroup_updated_t;
.super Ljava/lang/Object;
.source "vx_evt_sessiongroup_updated_t.java"


# instance fields
.field protected swigCMemOwn:Z

.field private swigCPtr:J


# direct methods
.method public constructor <init>()V
    .locals 3

    .prologue
    .line 8
    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->new_vx_evt_sessiongroup_updated_t()J

    move-result-wide v0

    const/4 v2, 0x1

    invoke-direct {p0, v0, v1, v2}, Lcom/vivox/service/vx_evt_sessiongroup_updated_t;-><init>(JZ)V

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
    iput-boolean p3, p0, Lcom/vivox/service/vx_evt_sessiongroup_updated_t;->swigCMemOwn:Z

    .line 13
    iput-wide p1, p0, Lcom/vivox/service/vx_evt_sessiongroup_updated_t;->swigCPtr:J

    .line 14
    return-void
.end method

.method protected static getCPtr(Lcom/vivox/service/vx_evt_sessiongroup_updated_t;)J
    .locals 2
    .param p0, "com_vivox_service_vx_evt_sessiongroup_updated_t"    # Lcom/vivox/service/vx_evt_sessiongroup_updated_t;

    .prologue
    .line 17
    if-nez p0, :cond_0

    const-wide/16 v0, 0x0

    :goto_0
    return-wide v0

    :cond_0
    iget-wide v0, p0, Lcom/vivox/service/vx_evt_sessiongroup_updated_t;->swigCPtr:J

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
    iget-wide v0, p0, Lcom/vivox/service/vx_evt_sessiongroup_updated_t;->swigCPtr:J

    cmp-long v0, v0, v2

    if-eqz v0, :cond_0

    iget-boolean v0, p0, Lcom/vivox/service/vx_evt_sessiongroup_updated_t;->swigCMemOwn:Z

    if-nez v0, :cond_1

    .line 22
    :cond_0
    const-wide/16 v0, 0x0

    iput-wide v0, p0, Lcom/vivox/service/vx_evt_sessiongroup_updated_t;->swigCPtr:J
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    .line 27
    monitor-exit p0

    return-void

    .line 24
    :cond_1
    const/4 v0, 0x0

    :try_start_1
    iput-boolean v0, p0, Lcom/vivox/service/vx_evt_sessiongroup_updated_t;->swigCMemOwn:Z

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
    iget-wide v2, p0, Lcom/vivox/service/vx_evt_sessiongroup_updated_t;->swigCPtr:J

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_evt_sessiongroup_updated_t_base_get(JLcom/vivox/service/vx_evt_sessiongroup_updated_t;)J

    move-result-wide v0

    .line 31
    .local v0, "vx_evt_sessiongroup_updated_t_base_get":J
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

.method public getCurrent_playback_mode()Lcom/vivox/service/vx_sessiongroup_playback_mode;
    .locals 2

    .prologue
    .line 35
    iget-wide v0, p0, Lcom/vivox/service/vx_evt_sessiongroup_updated_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_evt_sessiongroup_updated_t_current_playback_mode_get(JLcom/vivox/service/vx_evt_sessiongroup_updated_t;)I

    move-result v0

    invoke-static {v0}, Lcom/vivox/service/vx_sessiongroup_playback_mode;->swigToEnum(I)Lcom/vivox/service/vx_sessiongroup_playback_mode;

    move-result-object v0

    return-object v0
.end method

.method public getCurrent_playback_speed()D
    .locals 2

    .prologue
    .line 39
    iget-wide v0, p0, Lcom/vivox/service/vx_evt_sessiongroup_updated_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_evt_sessiongroup_updated_t_current_playback_speed_get(JLcom/vivox/service/vx_evt_sessiongroup_updated_t;)D

    move-result-wide v0

    return-wide v0
.end method

.method public getCurrent_recording_filename()Ljava/lang/String;
    .locals 2

    .prologue
    .line 43
    iget-wide v0, p0, Lcom/vivox/service/vx_evt_sessiongroup_updated_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_evt_sessiongroup_updated_t_current_recording_filename_get(JLcom/vivox/service/vx_evt_sessiongroup_updated_t;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public getFirst_frame_timestamp_us()J
    .locals 2

    .prologue
    .line 47
    iget-wide v0, p0, Lcom/vivox/service/vx_evt_sessiongroup_updated_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_evt_sessiongroup_updated_t_first_frame_timestamp_us_get(JLcom/vivox/service/vx_evt_sessiongroup_updated_t;)J

    move-result-wide v0

    return-wide v0
.end method

.method public getFirst_loop_frame()I
    .locals 2

    .prologue
    .line 51
    iget-wide v0, p0, Lcom/vivox/service/vx_evt_sessiongroup_updated_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_evt_sessiongroup_updated_t_first_loop_frame_get(JLcom/vivox/service/vx_evt_sessiongroup_updated_t;)I

    move-result v0

    return v0
.end method

.method public getIn_delayed_playback()I
    .locals 2

    .prologue
    .line 55
    iget-wide v0, p0, Lcom/vivox/service/vx_evt_sessiongroup_updated_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_evt_sessiongroup_updated_t_in_delayed_playback_get(JLcom/vivox/service/vx_evt_sessiongroup_updated_t;)I

    move-result v0

    return v0
.end method

.method public getLast_loop_frame_played()I
    .locals 2

    .prologue
    .line 59
    iget-wide v0, p0, Lcom/vivox/service/vx_evt_sessiongroup_updated_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_evt_sessiongroup_updated_t_last_loop_frame_played_get(JLcom/vivox/service/vx_evt_sessiongroup_updated_t;)I

    move-result v0

    return v0
.end method

.method public getLoop_buffer_capacity()I
    .locals 2

    .prologue
    .line 63
    iget-wide v0, p0, Lcom/vivox/service/vx_evt_sessiongroup_updated_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_evt_sessiongroup_updated_t_loop_buffer_capacity_get(JLcom/vivox/service/vx_evt_sessiongroup_updated_t;)I

    move-result v0

    return v0
.end method

.method public getPlayback_paused()I
    .locals 2

    .prologue
    .line 67
    iget-wide v0, p0, Lcom/vivox/service/vx_evt_sessiongroup_updated_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_evt_sessiongroup_updated_t_playback_paused_get(JLcom/vivox/service/vx_evt_sessiongroup_updated_t;)I

    move-result v0

    return v0
.end method

.method public getSessiongroup_handle()Ljava/lang/String;
    .locals 2

    .prologue
    .line 71
    iget-wide v0, p0, Lcom/vivox/service/vx_evt_sessiongroup_updated_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_evt_sessiongroup_updated_t_sessiongroup_handle_get(JLcom/vivox/service/vx_evt_sessiongroup_updated_t;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public getTotal_loop_frames_captured()I
    .locals 2

    .prologue
    .line 75
    iget-wide v0, p0, Lcom/vivox/service/vx_evt_sessiongroup_updated_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_evt_sessiongroup_updated_t_total_loop_frames_captured_get(JLcom/vivox/service/vx_evt_sessiongroup_updated_t;)I

    move-result v0

    return v0
.end method

.method public getTotal_recorded_frames()I
    .locals 2

    .prologue
    .line 79
    iget-wide v0, p0, Lcom/vivox/service/vx_evt_sessiongroup_updated_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_evt_sessiongroup_updated_t_total_recorded_frames_get(JLcom/vivox/service/vx_evt_sessiongroup_updated_t;)I

    move-result v0

    return v0
.end method

.method public setBase(Lcom/vivox/service/vx_evt_base_t;)V
    .locals 6
    .param p1, "com_vivox_service_vx_evt_base_t"    # Lcom/vivox/service/vx_evt_base_t;

    .prologue
    .line 83
    iget-wide v0, p0, Lcom/vivox/service/vx_evt_sessiongroup_updated_t;->swigCPtr:J

    invoke-static {p1}, Lcom/vivox/service/vx_evt_base_t;->getCPtr(Lcom/vivox/service/vx_evt_base_t;)J

    move-result-wide v3

    move-object v2, p0

    move-object v5, p1

    invoke-static/range {v0 .. v5}, Lcom/vivox/service/VxClientProxyJNI;->vx_evt_sessiongroup_updated_t_base_set(JLcom/vivox/service/vx_evt_sessiongroup_updated_t;JLcom/vivox/service/vx_evt_base_t;)V

    .line 84
    return-void
.end method

.method public setCurrent_playback_mode(Lcom/vivox/service/vx_sessiongroup_playback_mode;)V
    .locals 3
    .param p1, "com_vivox_service_vx_sessiongroup_playback_mode"    # Lcom/vivox/service/vx_sessiongroup_playback_mode;

    .prologue
    .line 87
    iget-wide v0, p0, Lcom/vivox/service/vx_evt_sessiongroup_updated_t;->swigCPtr:J

    invoke-virtual {p1}, Lcom/vivox/service/vx_sessiongroup_playback_mode;->swigValue()I

    move-result v2

    invoke-static {v0, v1, p0, v2}, Lcom/vivox/service/VxClientProxyJNI;->vx_evt_sessiongroup_updated_t_current_playback_mode_set(JLcom/vivox/service/vx_evt_sessiongroup_updated_t;I)V

    .line 88
    return-void
.end method

.method public setCurrent_playback_speed(D)V
    .locals 3
    .param p1, "d"    # D

    .prologue
    .line 91
    iget-wide v0, p0, Lcom/vivox/service/vx_evt_sessiongroup_updated_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1, p2}, Lcom/vivox/service/VxClientProxyJNI;->vx_evt_sessiongroup_updated_t_current_playback_speed_set(JLcom/vivox/service/vx_evt_sessiongroup_updated_t;D)V

    .line 92
    return-void
.end method

.method public setCurrent_recording_filename(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 95
    iget-wide v0, p0, Lcom/vivox/service/vx_evt_sessiongroup_updated_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_evt_sessiongroup_updated_t_current_recording_filename_set(JLcom/vivox/service/vx_evt_sessiongroup_updated_t;Ljava/lang/String;)V

    .line 96
    return-void
.end method

.method public setFirst_frame_timestamp_us(J)V
    .locals 3
    .param p1, "j"    # J

    .prologue
    .line 99
    iget-wide v0, p0, Lcom/vivox/service/vx_evt_sessiongroup_updated_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1, p2}, Lcom/vivox/service/VxClientProxyJNI;->vx_evt_sessiongroup_updated_t_first_frame_timestamp_us_set(JLcom/vivox/service/vx_evt_sessiongroup_updated_t;J)V

    .line 100
    return-void
.end method

.method public setFirst_loop_frame(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 103
    iget-wide v0, p0, Lcom/vivox/service/vx_evt_sessiongroup_updated_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_evt_sessiongroup_updated_t_first_loop_frame_set(JLcom/vivox/service/vx_evt_sessiongroup_updated_t;I)V

    .line 104
    return-void
.end method

.method public setIn_delayed_playback(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 107
    iget-wide v0, p0, Lcom/vivox/service/vx_evt_sessiongroup_updated_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_evt_sessiongroup_updated_t_in_delayed_playback_set(JLcom/vivox/service/vx_evt_sessiongroup_updated_t;I)V

    .line 108
    return-void
.end method

.method public setLast_loop_frame_played(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 111
    iget-wide v0, p0, Lcom/vivox/service/vx_evt_sessiongroup_updated_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_evt_sessiongroup_updated_t_last_loop_frame_played_set(JLcom/vivox/service/vx_evt_sessiongroup_updated_t;I)V

    .line 112
    return-void
.end method

.method public setLoop_buffer_capacity(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 115
    iget-wide v0, p0, Lcom/vivox/service/vx_evt_sessiongroup_updated_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_evt_sessiongroup_updated_t_loop_buffer_capacity_set(JLcom/vivox/service/vx_evt_sessiongroup_updated_t;I)V

    .line 116
    return-void
.end method

.method public setPlayback_paused(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 119
    iget-wide v0, p0, Lcom/vivox/service/vx_evt_sessiongroup_updated_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_evt_sessiongroup_updated_t_playback_paused_set(JLcom/vivox/service/vx_evt_sessiongroup_updated_t;I)V

    .line 120
    return-void
.end method

.method public setSessiongroup_handle(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 123
    iget-wide v0, p0, Lcom/vivox/service/vx_evt_sessiongroup_updated_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_evt_sessiongroup_updated_t_sessiongroup_handle_set(JLcom/vivox/service/vx_evt_sessiongroup_updated_t;Ljava/lang/String;)V

    .line 124
    return-void
.end method

.method public setTotal_loop_frames_captured(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 127
    iget-wide v0, p0, Lcom/vivox/service/vx_evt_sessiongroup_updated_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_evt_sessiongroup_updated_t_total_loop_frames_captured_set(JLcom/vivox/service/vx_evt_sessiongroup_updated_t;I)V

    .line 128
    return-void
.end method

.method public setTotal_recorded_frames(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 131
    iget-wide v0, p0, Lcom/vivox/service/vx_evt_sessiongroup_updated_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_evt_sessiongroup_updated_t_total_recorded_frames_set(JLcom/vivox/service/vx_evt_sessiongroup_updated_t;I)V

    .line 132
    return-void
.end method
