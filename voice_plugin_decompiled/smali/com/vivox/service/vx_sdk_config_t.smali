.class public Lcom/vivox/service/vx_sdk_config_t;
.super Ljava/lang/Object;
.source "vx_sdk_config_t.java"


# instance fields
.field protected swigCMemOwn:Z

.field private swigCPtr:J


# direct methods
.method public constructor <init>()V
    .locals 3

    .prologue
    .line 8
    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->new_vx_sdk_config_t()J

    move-result-wide v0

    const/4 v2, 0x1

    invoke-direct {p0, v0, v1, v2}, Lcom/vivox/service/vx_sdk_config_t;-><init>(JZ)V

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
    iput-boolean p3, p0, Lcom/vivox/service/vx_sdk_config_t;->swigCMemOwn:Z

    .line 13
    iput-wide p1, p0, Lcom/vivox/service/vx_sdk_config_t;->swigCPtr:J

    .line 14
    return-void
.end method

.method protected static getCPtr(Lcom/vivox/service/vx_sdk_config_t;)J
    .locals 2
    .param p0, "com_vivox_service_vx_sdk_config_t"    # Lcom/vivox/service/vx_sdk_config_t;

    .prologue
    .line 17
    if-nez p0, :cond_0

    const-wide/16 v0, 0x0

    :goto_0
    return-wide v0

    :cond_0
    iget-wide v0, p0, Lcom/vivox/service/vx_sdk_config_t;->swigCPtr:J

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
    iget-wide v0, p0, Lcom/vivox/service/vx_sdk_config_t;->swigCPtr:J

    cmp-long v0, v0, v2

    if-eqz v0, :cond_0

    iget-boolean v0, p0, Lcom/vivox/service/vx_sdk_config_t;->swigCMemOwn:Z

    if-nez v0, :cond_1

    .line 22
    :cond_0
    const-wide/16 v0, 0x0

    iput-wide v0, p0, Lcom/vivox/service/vx_sdk_config_t;->swigCPtr:J
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    .line 27
    monitor-exit p0

    return-void

    .line 24
    :cond_1
    const/4 v0, 0x0

    :try_start_1
    iput-boolean v0, p0, Lcom/vivox/service/vx_sdk_config_t;->swigCMemOwn:Z

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

.method public getAllow_shared_capture_devices()I
    .locals 2

    .prologue
    .line 30
    iget-wide v0, p0, Lcom/vivox/service/vx_sdk_config_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_sdk_config_t_allow_shared_capture_devices_get(JLcom/vivox/service/vx_sdk_config_t;)I

    move-result v0

    return v0
.end method

.method public getApp_id()Ljava/lang/String;
    .locals 2

    .prologue
    .line 34
    iget-wide v0, p0, Lcom/vivox/service/vx_sdk_config_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_sdk_config_t_app_id_get(JLcom/vivox/service/vx_sdk_config_t;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public getCert_data_dir()Ljava/lang/String;
    .locals 2

    .prologue
    .line 38
    iget-wide v0, p0, Lcom/vivox/service/vx_sdk_config_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_sdk_config_t_cert_data_dir_get(JLcom/vivox/service/vx_sdk_config_t;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public getMax_logins_per_user()I
    .locals 2

    .prologue
    .line 42
    iget-wide v0, p0, Lcom/vivox/service/vx_sdk_config_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_sdk_config_t_max_logins_per_user_get(JLcom/vivox/service/vx_sdk_config_t;)I

    move-result v0

    return v0
.end method

.method public getNum_codec_threads()I
    .locals 2

    .prologue
    .line 46
    iget-wide v0, p0, Lcom/vivox/service/vx_sdk_config_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_sdk_config_t_num_codec_threads_get(JLcom/vivox/service/vx_sdk_config_t;)I

    move-result v0

    return v0
.end method

.method public getNum_voice_threads()I
    .locals 2

    .prologue
    .line 50
    iget-wide v0, p0, Lcom/vivox/service/vx_sdk_config_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_sdk_config_t_num_voice_threads_get(JLcom/vivox/service/vx_sdk_config_t;)I

    move-result v0

    return v0
.end method

.method public getNum_web_threads()I
    .locals 2

    .prologue
    .line 54
    iget-wide v0, p0, Lcom/vivox/service/vx_sdk_config_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_sdk_config_t_num_web_threads_get(JLcom/vivox/service/vx_sdk_config_t;)I

    move-result v0

    return v0
.end method

.method public getRender_source_initial_buffer_count()I
    .locals 2

    .prologue
    .line 58
    iget-wide v0, p0, Lcom/vivox/service/vx_sdk_config_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_sdk_config_t_render_source_initial_buffer_count_get(JLcom/vivox/service/vx_sdk_config_t;)I

    move-result v0

    return v0
.end method

.method public getRender_source_queue_depth_max()I
    .locals 2

    .prologue
    .line 62
    iget-wide v0, p0, Lcom/vivox/service/vx_sdk_config_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_sdk_config_t_render_source_queue_depth_max_get(JLcom/vivox/service/vx_sdk_config_t;)I

    move-result v0

    return v0
.end method

.method public getUpstream_jitter_frame_count()I
    .locals 2

    .prologue
    .line 66
    iget-wide v0, p0, Lcom/vivox/service/vx_sdk_config_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_sdk_config_t_upstream_jitter_frame_count_get(JLcom/vivox/service/vx_sdk_config_t;)I

    move-result v0

    return v0
.end method

.method public setAllow_shared_capture_devices(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 70
    iget-wide v0, p0, Lcom/vivox/service/vx_sdk_config_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_sdk_config_t_allow_shared_capture_devices_set(JLcom/vivox/service/vx_sdk_config_t;I)V

    .line 71
    return-void
.end method

.method public setApp_id(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 74
    iget-wide v0, p0, Lcom/vivox/service/vx_sdk_config_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_sdk_config_t_app_id_set(JLcom/vivox/service/vx_sdk_config_t;Ljava/lang/String;)V

    .line 75
    return-void
.end method

.method public setCert_data_dir(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 78
    iget-wide v0, p0, Lcom/vivox/service/vx_sdk_config_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_sdk_config_t_cert_data_dir_set(JLcom/vivox/service/vx_sdk_config_t;Ljava/lang/String;)V

    .line 79
    return-void
.end method

.method public setMax_logins_per_user(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 82
    iget-wide v0, p0, Lcom/vivox/service/vx_sdk_config_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_sdk_config_t_max_logins_per_user_set(JLcom/vivox/service/vx_sdk_config_t;I)V

    .line 83
    return-void
.end method

.method public setNum_codec_threads(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 86
    iget-wide v0, p0, Lcom/vivox/service/vx_sdk_config_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_sdk_config_t_num_codec_threads_set(JLcom/vivox/service/vx_sdk_config_t;I)V

    .line 87
    return-void
.end method

.method public setNum_voice_threads(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 90
    iget-wide v0, p0, Lcom/vivox/service/vx_sdk_config_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_sdk_config_t_num_voice_threads_set(JLcom/vivox/service/vx_sdk_config_t;I)V

    .line 91
    return-void
.end method

.method public setNum_web_threads(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 94
    iget-wide v0, p0, Lcom/vivox/service/vx_sdk_config_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_sdk_config_t_num_web_threads_set(JLcom/vivox/service/vx_sdk_config_t;I)V

    .line 95
    return-void
.end method

.method public setRender_source_initial_buffer_count(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 98
    iget-wide v0, p0, Lcom/vivox/service/vx_sdk_config_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_sdk_config_t_render_source_initial_buffer_count_set(JLcom/vivox/service/vx_sdk_config_t;I)V

    .line 99
    return-void
.end method

.method public setRender_source_queue_depth_max(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 102
    iget-wide v0, p0, Lcom/vivox/service/vx_sdk_config_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_sdk_config_t_render_source_queue_depth_max_set(JLcom/vivox/service/vx_sdk_config_t;I)V

    .line 103
    return-void
.end method

.method public setUpstream_jitter_frame_count(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 106
    iget-wide v0, p0, Lcom/vivox/service/vx_sdk_config_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_sdk_config_t_upstream_jitter_frame_count_set(JLcom/vivox/service/vx_sdk_config_t;I)V

    .line 107
    return-void
.end method
