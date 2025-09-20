.class public Lcom/vivox/service/vx_req_session_set_3d_position_t;
.super Ljava/lang/Object;
.source "vx_req_session_set_3d_position_t.java"


# instance fields
.field protected swigCMemOwn:Z

.field private swigCPtr:J


# direct methods
.method public constructor <init>()V
    .locals 3

    .prologue
    .line 8
    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->new_vx_req_session_set_3d_position_t()J

    move-result-wide v0

    const/4 v2, 0x1

    invoke-direct {p0, v0, v1, v2}, Lcom/vivox/service/vx_req_session_set_3d_position_t;-><init>(JZ)V

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
    iput-boolean p3, p0, Lcom/vivox/service/vx_req_session_set_3d_position_t;->swigCMemOwn:Z

    .line 13
    iput-wide p1, p0, Lcom/vivox/service/vx_req_session_set_3d_position_t;->swigCPtr:J

    .line 14
    return-void
.end method

.method protected static getCPtr(Lcom/vivox/service/vx_req_session_set_3d_position_t;)J
    .locals 2
    .param p0, "com_vivox_service_vx_req_session_set_3d_position_t"    # Lcom/vivox/service/vx_req_session_set_3d_position_t;

    .prologue
    .line 17
    if-nez p0, :cond_0

    const-wide/16 v0, 0x0

    :goto_0
    return-wide v0

    :cond_0
    iget-wide v0, p0, Lcom/vivox/service/vx_req_session_set_3d_position_t;->swigCPtr:J

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
    iget-wide v0, p0, Lcom/vivox/service/vx_req_session_set_3d_position_t;->swigCPtr:J

    cmp-long v0, v0, v2

    if-eqz v0, :cond_0

    iget-boolean v0, p0, Lcom/vivox/service/vx_req_session_set_3d_position_t;->swigCMemOwn:Z

    if-nez v0, :cond_1

    .line 22
    :cond_0
    const-wide/16 v0, 0x0

    iput-wide v0, p0, Lcom/vivox/service/vx_req_session_set_3d_position_t;->swigCPtr:J
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    .line 27
    monitor-exit p0

    return-void

    .line 24
    :cond_1
    const/4 v0, 0x0

    :try_start_1
    iput-boolean v0, p0, Lcom/vivox/service/vx_req_session_set_3d_position_t;->swigCMemOwn:Z

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

.method public getBase()Lcom/vivox/service/vx_req_base_t;
    .locals 4

    .prologue
    .line 30
    iget-wide v2, p0, Lcom/vivox/service/vx_req_session_set_3d_position_t;->swigCPtr:J

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_session_set_3d_position_t_base_get(JLcom/vivox/service/vx_req_session_set_3d_position_t;)J

    move-result-wide v0

    .line 31
    .local v0, "vx_req_session_set_3d_position_t_base_get":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_base_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_base_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public getListener_at_orientation()Lcom/vivox/service/SWIGTYPE_p_double;
    .locals 4

    .prologue
    .line 35
    iget-wide v2, p0, Lcom/vivox/service/vx_req_session_set_3d_position_t;->swigCPtr:J

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_session_set_3d_position_t_listener_at_orientation_get(JLcom/vivox/service/vx_req_session_set_3d_position_t;)J

    move-result-wide v0

    .line 36
    .local v0, "vx_req_session_set_3d_position_t_listener_at_orientation_get":J
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

.method public getListener_left_orientation()Lcom/vivox/service/SWIGTYPE_p_double;
    .locals 4

    .prologue
    .line 40
    iget-wide v2, p0, Lcom/vivox/service/vx_req_session_set_3d_position_t;->swigCPtr:J

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_session_set_3d_position_t_listener_left_orientation_get(JLcom/vivox/service/vx_req_session_set_3d_position_t;)J

    move-result-wide v0

    .line 41
    .local v0, "vx_req_session_set_3d_position_t_listener_left_orientation_get":J
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

.method public getListener_position()Lcom/vivox/service/SWIGTYPE_p_double;
    .locals 4

    .prologue
    .line 45
    iget-wide v2, p0, Lcom/vivox/service/vx_req_session_set_3d_position_t;->swigCPtr:J

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_session_set_3d_position_t_listener_position_get(JLcom/vivox/service/vx_req_session_set_3d_position_t;)J

    move-result-wide v0

    .line 46
    .local v0, "vx_req_session_set_3d_position_t_listener_position_get":J
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

.method public getListener_up_orientation()Lcom/vivox/service/SWIGTYPE_p_double;
    .locals 4

    .prologue
    .line 50
    iget-wide v2, p0, Lcom/vivox/service/vx_req_session_set_3d_position_t;->swigCPtr:J

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_session_set_3d_position_t_listener_up_orientation_get(JLcom/vivox/service/vx_req_session_set_3d_position_t;)J

    move-result-wide v0

    .line 51
    .local v0, "vx_req_session_set_3d_position_t_listener_up_orientation_get":J
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

.method public getListener_velocity()Lcom/vivox/service/SWIGTYPE_p_double;
    .locals 4

    .prologue
    .line 55
    iget-wide v2, p0, Lcom/vivox/service/vx_req_session_set_3d_position_t;->swigCPtr:J

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_session_set_3d_position_t_listener_velocity_get(JLcom/vivox/service/vx_req_session_set_3d_position_t;)J

    move-result-wide v0

    .line 56
    .local v0, "vx_req_session_set_3d_position_t_listener_velocity_get":J
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

.method public getReq_disposition_type()Lcom/vivox/service/req_disposition_type_t;
    .locals 2

    .prologue
    .line 60
    iget-wide v0, p0, Lcom/vivox/service/vx_req_session_set_3d_position_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_session_set_3d_position_t_req_disposition_type_get(JLcom/vivox/service/vx_req_session_set_3d_position_t;)I

    move-result v0

    invoke-static {v0}, Lcom/vivox/service/req_disposition_type_t;->swigToEnum(I)Lcom/vivox/service/req_disposition_type_t;

    move-result-object v0

    return-object v0
.end method

.method public getSession_handle()Ljava/lang/String;
    .locals 2

    .prologue
    .line 64
    iget-wide v0, p0, Lcom/vivox/service/vx_req_session_set_3d_position_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_session_set_3d_position_t_session_handle_get(JLcom/vivox/service/vx_req_session_set_3d_position_t;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public getSpeaker_at_orientation()Lcom/vivox/service/SWIGTYPE_p_double;
    .locals 4

    .prologue
    .line 68
    iget-wide v2, p0, Lcom/vivox/service/vx_req_session_set_3d_position_t;->swigCPtr:J

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_session_set_3d_position_t_speaker_at_orientation_get(JLcom/vivox/service/vx_req_session_set_3d_position_t;)J

    move-result-wide v0

    .line 69
    .local v0, "vx_req_session_set_3d_position_t_speaker_at_orientation_get":J
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

.method public getSpeaker_left_orientation()Lcom/vivox/service/SWIGTYPE_p_double;
    .locals 4

    .prologue
    .line 73
    iget-wide v2, p0, Lcom/vivox/service/vx_req_session_set_3d_position_t;->swigCPtr:J

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_session_set_3d_position_t_speaker_left_orientation_get(JLcom/vivox/service/vx_req_session_set_3d_position_t;)J

    move-result-wide v0

    .line 74
    .local v0, "vx_req_session_set_3d_position_t_speaker_left_orientation_get":J
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

.method public getSpeaker_position()Lcom/vivox/service/SWIGTYPE_p_double;
    .locals 4

    .prologue
    .line 78
    iget-wide v2, p0, Lcom/vivox/service/vx_req_session_set_3d_position_t;->swigCPtr:J

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_session_set_3d_position_t_speaker_position_get(JLcom/vivox/service/vx_req_session_set_3d_position_t;)J

    move-result-wide v0

    .line 79
    .local v0, "vx_req_session_set_3d_position_t_speaker_position_get":J
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

.method public getSpeaker_up_orientation()Lcom/vivox/service/SWIGTYPE_p_double;
    .locals 4

    .prologue
    .line 83
    iget-wide v2, p0, Lcom/vivox/service/vx_req_session_set_3d_position_t;->swigCPtr:J

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_session_set_3d_position_t_speaker_up_orientation_get(JLcom/vivox/service/vx_req_session_set_3d_position_t;)J

    move-result-wide v0

    .line 84
    .local v0, "vx_req_session_set_3d_position_t_speaker_up_orientation_get":J
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

.method public getSpeaker_velocity()Lcom/vivox/service/SWIGTYPE_p_double;
    .locals 4

    .prologue
    .line 88
    iget-wide v2, p0, Lcom/vivox/service/vx_req_session_set_3d_position_t;->swigCPtr:J

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_session_set_3d_position_t_speaker_velocity_get(JLcom/vivox/service/vx_req_session_set_3d_position_t;)J

    move-result-wide v0

    .line 89
    .local v0, "vx_req_session_set_3d_position_t_speaker_velocity_get":J
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

.method public getType()Lcom/vivox/service/orientation_type;
    .locals 2

    .prologue
    .line 93
    iget-wide v0, p0, Lcom/vivox/service/vx_req_session_set_3d_position_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_session_set_3d_position_t_type_get(JLcom/vivox/service/vx_req_session_set_3d_position_t;)I

    move-result v0

    invoke-static {v0}, Lcom/vivox/service/orientation_type;->swigToEnum(I)Lcom/vivox/service/orientation_type;

    move-result-object v0

    return-object v0
.end method

.method public setBase(Lcom/vivox/service/vx_req_base_t;)V
    .locals 6
    .param p1, "com_vivox_service_vx_req_base_t"    # Lcom/vivox/service/vx_req_base_t;

    .prologue
    .line 97
    iget-wide v0, p0, Lcom/vivox/service/vx_req_session_set_3d_position_t;->swigCPtr:J

    invoke-static {p1}, Lcom/vivox/service/vx_req_base_t;->getCPtr(Lcom/vivox/service/vx_req_base_t;)J

    move-result-wide v3

    move-object v2, p0

    move-object v5, p1

    invoke-static/range {v0 .. v5}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_session_set_3d_position_t_base_set(JLcom/vivox/service/vx_req_session_set_3d_position_t;JLcom/vivox/service/vx_req_base_t;)V

    .line 98
    return-void
.end method

.method public setListener_at_orientation(Lcom/vivox/service/SWIGTYPE_p_double;)V
    .locals 4
    .param p1, "sWIGTYPE_p_double"    # Lcom/vivox/service/SWIGTYPE_p_double;

    .prologue
    .line 101
    iget-wide v0, p0, Lcom/vivox/service/vx_req_session_set_3d_position_t;->swigCPtr:J

    invoke-static {p1}, Lcom/vivox/service/SWIGTYPE_p_double;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_double;)J

    move-result-wide v2

    invoke-static {v0, v1, p0, v2, v3}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_session_set_3d_position_t_listener_at_orientation_set(JLcom/vivox/service/vx_req_session_set_3d_position_t;J)V

    .line 102
    return-void
.end method

.method public setListener_left_orientation(Lcom/vivox/service/SWIGTYPE_p_double;)V
    .locals 4
    .param p1, "sWIGTYPE_p_double"    # Lcom/vivox/service/SWIGTYPE_p_double;

    .prologue
    .line 105
    iget-wide v0, p0, Lcom/vivox/service/vx_req_session_set_3d_position_t;->swigCPtr:J

    invoke-static {p1}, Lcom/vivox/service/SWIGTYPE_p_double;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_double;)J

    move-result-wide v2

    invoke-static {v0, v1, p0, v2, v3}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_session_set_3d_position_t_listener_left_orientation_set(JLcom/vivox/service/vx_req_session_set_3d_position_t;J)V

    .line 106
    return-void
.end method

.method public setListener_position(Lcom/vivox/service/SWIGTYPE_p_double;)V
    .locals 4
    .param p1, "sWIGTYPE_p_double"    # Lcom/vivox/service/SWIGTYPE_p_double;

    .prologue
    .line 109
    iget-wide v0, p0, Lcom/vivox/service/vx_req_session_set_3d_position_t;->swigCPtr:J

    invoke-static {p1}, Lcom/vivox/service/SWIGTYPE_p_double;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_double;)J

    move-result-wide v2

    invoke-static {v0, v1, p0, v2, v3}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_session_set_3d_position_t_listener_position_set(JLcom/vivox/service/vx_req_session_set_3d_position_t;J)V

    .line 110
    return-void
.end method

.method public setListener_up_orientation(Lcom/vivox/service/SWIGTYPE_p_double;)V
    .locals 4
    .param p1, "sWIGTYPE_p_double"    # Lcom/vivox/service/SWIGTYPE_p_double;

    .prologue
    .line 113
    iget-wide v0, p0, Lcom/vivox/service/vx_req_session_set_3d_position_t;->swigCPtr:J

    invoke-static {p1}, Lcom/vivox/service/SWIGTYPE_p_double;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_double;)J

    move-result-wide v2

    invoke-static {v0, v1, p0, v2, v3}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_session_set_3d_position_t_listener_up_orientation_set(JLcom/vivox/service/vx_req_session_set_3d_position_t;J)V

    .line 114
    return-void
.end method

.method public setListener_velocity(Lcom/vivox/service/SWIGTYPE_p_double;)V
    .locals 4
    .param p1, "sWIGTYPE_p_double"    # Lcom/vivox/service/SWIGTYPE_p_double;

    .prologue
    .line 117
    iget-wide v0, p0, Lcom/vivox/service/vx_req_session_set_3d_position_t;->swigCPtr:J

    invoke-static {p1}, Lcom/vivox/service/SWIGTYPE_p_double;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_double;)J

    move-result-wide v2

    invoke-static {v0, v1, p0, v2, v3}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_session_set_3d_position_t_listener_velocity_set(JLcom/vivox/service/vx_req_session_set_3d_position_t;J)V

    .line 118
    return-void
.end method

.method public setReq_disposition_type(Lcom/vivox/service/req_disposition_type_t;)V
    .locals 3
    .param p1, "com_vivox_service_req_disposition_type_t"    # Lcom/vivox/service/req_disposition_type_t;

    .prologue
    .line 121
    iget-wide v0, p0, Lcom/vivox/service/vx_req_session_set_3d_position_t;->swigCPtr:J

    invoke-virtual {p1}, Lcom/vivox/service/req_disposition_type_t;->swigValue()I

    move-result v2

    invoke-static {v0, v1, p0, v2}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_session_set_3d_position_t_req_disposition_type_set(JLcom/vivox/service/vx_req_session_set_3d_position_t;I)V

    .line 122
    return-void
.end method

.method public setSession_handle(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 125
    iget-wide v0, p0, Lcom/vivox/service/vx_req_session_set_3d_position_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_session_set_3d_position_t_session_handle_set(JLcom/vivox/service/vx_req_session_set_3d_position_t;Ljava/lang/String;)V

    .line 126
    return-void
.end method

.method public setSpeaker_at_orientation(Lcom/vivox/service/SWIGTYPE_p_double;)V
    .locals 4
    .param p1, "sWIGTYPE_p_double"    # Lcom/vivox/service/SWIGTYPE_p_double;

    .prologue
    .line 129
    iget-wide v0, p0, Lcom/vivox/service/vx_req_session_set_3d_position_t;->swigCPtr:J

    invoke-static {p1}, Lcom/vivox/service/SWIGTYPE_p_double;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_double;)J

    move-result-wide v2

    invoke-static {v0, v1, p0, v2, v3}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_session_set_3d_position_t_speaker_at_orientation_set(JLcom/vivox/service/vx_req_session_set_3d_position_t;J)V

    .line 130
    return-void
.end method

.method public setSpeaker_left_orientation(Lcom/vivox/service/SWIGTYPE_p_double;)V
    .locals 4
    .param p1, "sWIGTYPE_p_double"    # Lcom/vivox/service/SWIGTYPE_p_double;

    .prologue
    .line 133
    iget-wide v0, p0, Lcom/vivox/service/vx_req_session_set_3d_position_t;->swigCPtr:J

    invoke-static {p1}, Lcom/vivox/service/SWIGTYPE_p_double;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_double;)J

    move-result-wide v2

    invoke-static {v0, v1, p0, v2, v3}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_session_set_3d_position_t_speaker_left_orientation_set(JLcom/vivox/service/vx_req_session_set_3d_position_t;J)V

    .line 134
    return-void
.end method

.method public setSpeaker_position(Lcom/vivox/service/SWIGTYPE_p_double;)V
    .locals 4
    .param p1, "sWIGTYPE_p_double"    # Lcom/vivox/service/SWIGTYPE_p_double;

    .prologue
    .line 137
    iget-wide v0, p0, Lcom/vivox/service/vx_req_session_set_3d_position_t;->swigCPtr:J

    invoke-static {p1}, Lcom/vivox/service/SWIGTYPE_p_double;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_double;)J

    move-result-wide v2

    invoke-static {v0, v1, p0, v2, v3}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_session_set_3d_position_t_speaker_position_set(JLcom/vivox/service/vx_req_session_set_3d_position_t;J)V

    .line 138
    return-void
.end method

.method public setSpeaker_up_orientation(Lcom/vivox/service/SWIGTYPE_p_double;)V
    .locals 4
    .param p1, "sWIGTYPE_p_double"    # Lcom/vivox/service/SWIGTYPE_p_double;

    .prologue
    .line 141
    iget-wide v0, p0, Lcom/vivox/service/vx_req_session_set_3d_position_t;->swigCPtr:J

    invoke-static {p1}, Lcom/vivox/service/SWIGTYPE_p_double;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_double;)J

    move-result-wide v2

    invoke-static {v0, v1, p0, v2, v3}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_session_set_3d_position_t_speaker_up_orientation_set(JLcom/vivox/service/vx_req_session_set_3d_position_t;J)V

    .line 142
    return-void
.end method

.method public setSpeaker_velocity(Lcom/vivox/service/SWIGTYPE_p_double;)V
    .locals 4
    .param p1, "sWIGTYPE_p_double"    # Lcom/vivox/service/SWIGTYPE_p_double;

    .prologue
    .line 145
    iget-wide v0, p0, Lcom/vivox/service/vx_req_session_set_3d_position_t;->swigCPtr:J

    invoke-static {p1}, Lcom/vivox/service/SWIGTYPE_p_double;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_double;)J

    move-result-wide v2

    invoke-static {v0, v1, p0, v2, v3}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_session_set_3d_position_t_speaker_velocity_set(JLcom/vivox/service/vx_req_session_set_3d_position_t;J)V

    .line 146
    return-void
.end method

.method public setType(Lcom/vivox/service/orientation_type;)V
    .locals 3
    .param p1, "com_vivox_service_orientation_type"    # Lcom/vivox/service/orientation_type;

    .prologue
    .line 149
    iget-wide v0, p0, Lcom/vivox/service/vx_req_session_set_3d_position_t;->swigCPtr:J

    invoke-virtual {p1}, Lcom/vivox/service/orientation_type;->swigValue()I

    move-result v2

    invoke-static {v0, v1, p0, v2}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_session_set_3d_position_t_type_set(JLcom/vivox/service/vx_req_session_set_3d_position_t;I)V

    .line 150
    return-void
.end method
