.class public Lcom/vivox/service/vx_resp_aux_diagnostic_state_dump_t;
.super Ljava/lang/Object;
.source "vx_resp_aux_diagnostic_state_dump_t.java"


# instance fields
.field protected swigCMemOwn:Z

.field private swigCPtr:J


# direct methods
.method public constructor <init>()V
    .locals 3

    .prologue
    .line 8
    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->new_vx_resp_aux_diagnostic_state_dump_t()J

    move-result-wide v0

    const/4 v2, 0x1

    invoke-direct {p0, v0, v1, v2}, Lcom/vivox/service/vx_resp_aux_diagnostic_state_dump_t;-><init>(JZ)V

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
    iput-boolean p3, p0, Lcom/vivox/service/vx_resp_aux_diagnostic_state_dump_t;->swigCMemOwn:Z

    .line 13
    iput-wide p1, p0, Lcom/vivox/service/vx_resp_aux_diagnostic_state_dump_t;->swigCPtr:J

    .line 14
    return-void
.end method

.method protected static getCPtr(Lcom/vivox/service/vx_resp_aux_diagnostic_state_dump_t;)J
    .locals 2
    .param p0, "com_vivox_service_vx_resp_aux_diagnostic_state_dump_t"    # Lcom/vivox/service/vx_resp_aux_diagnostic_state_dump_t;

    .prologue
    .line 17
    if-nez p0, :cond_0

    const-wide/16 v0, 0x0

    :goto_0
    return-wide v0

    :cond_0
    iget-wide v0, p0, Lcom/vivox/service/vx_resp_aux_diagnostic_state_dump_t;->swigCPtr:J

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
    iget-wide v0, p0, Lcom/vivox/service/vx_resp_aux_diagnostic_state_dump_t;->swigCPtr:J

    cmp-long v0, v0, v2

    if-eqz v0, :cond_0

    iget-boolean v0, p0, Lcom/vivox/service/vx_resp_aux_diagnostic_state_dump_t;->swigCMemOwn:Z

    if-nez v0, :cond_1

    .line 22
    :cond_0
    const-wide/16 v0, 0x0

    iput-wide v0, p0, Lcom/vivox/service/vx_resp_aux_diagnostic_state_dump_t;->swigCPtr:J
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    .line 27
    monitor-exit p0

    return-void

    .line 24
    :cond_1
    const/4 v0, 0x0

    :try_start_1
    iput-boolean v0, p0, Lcom/vivox/service/vx_resp_aux_diagnostic_state_dump_t;->swigCMemOwn:Z

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

.method public getBase()Lcom/vivox/service/vx_resp_base_t;
    .locals 4

    .prologue
    .line 30
    iget-wide v2, p0, Lcom/vivox/service/vx_resp_aux_diagnostic_state_dump_t;->swigCPtr:J

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_resp_aux_diagnostic_state_dump_t_base_get(JLcom/vivox/service/vx_resp_aux_diagnostic_state_dump_t;)J

    move-result-wide v0

    .line 31
    .local v0, "vx_resp_aux_diagnostic_state_dump_t_base_get":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_base_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_base_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public getCurrent_capture_device()Lcom/vivox/service/vx_device_t;
    .locals 4

    .prologue
    .line 35
    iget-wide v2, p0, Lcom/vivox/service/vx_resp_aux_diagnostic_state_dump_t;->swigCPtr:J

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_resp_aux_diagnostic_state_dump_t_current_capture_device_get(JLcom/vivox/service/vx_resp_aux_diagnostic_state_dump_t;)J

    move-result-wide v0

    .line 36
    .local v0, "vx_resp_aux_diagnostic_state_dump_t_current_capture_device_get":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_device_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_device_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public getCurrent_render_device()Lcom/vivox/service/vx_device_t;
    .locals 4

    .prologue
    .line 40
    iget-wide v2, p0, Lcom/vivox/service/vx_resp_aux_diagnostic_state_dump_t;->swigCPtr:J

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_resp_aux_diagnostic_state_dump_t_current_render_device_get(JLcom/vivox/service/vx_resp_aux_diagnostic_state_dump_t;)J

    move-result-wide v0

    .line 41
    .local v0, "vx_resp_aux_diagnostic_state_dump_t_current_render_device_get":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_device_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_device_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public getEffective_capture_device()Lcom/vivox/service/vx_device_t;
    .locals 4

    .prologue
    .line 45
    iget-wide v2, p0, Lcom/vivox/service/vx_resp_aux_diagnostic_state_dump_t;->swigCPtr:J

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_resp_aux_diagnostic_state_dump_t_effective_capture_device_get(JLcom/vivox/service/vx_resp_aux_diagnostic_state_dump_t;)J

    move-result-wide v0

    .line 46
    .local v0, "vx_resp_aux_diagnostic_state_dump_t_effective_capture_device_get":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_device_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_device_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public getEffective_render_device()Lcom/vivox/service/vx_device_t;
    .locals 4

    .prologue
    .line 50
    iget-wide v2, p0, Lcom/vivox/service/vx_resp_aux_diagnostic_state_dump_t;->swigCPtr:J

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_resp_aux_diagnostic_state_dump_t_effective_render_device_get(JLcom/vivox/service/vx_resp_aux_diagnostic_state_dump_t;)J

    move-result-wide v0

    .line 51
    .local v0, "vx_resp_aux_diagnostic_state_dump_t_effective_render_device_get":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_device_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_device_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public getState_connector_count()I
    .locals 2

    .prologue
    .line 55
    iget-wide v0, p0, Lcom/vivox/service/vx_resp_aux_diagnostic_state_dump_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_resp_aux_diagnostic_state_dump_t_state_connector_count_get(JLcom/vivox/service/vx_resp_aux_diagnostic_state_dump_t;)I

    move-result v0

    return v0
.end method

.method public getState_connectors()Lcom/vivox/service/SWIGTYPE_p_p_vx_state_connector;
    .locals 4

    .prologue
    .line 59
    iget-wide v2, p0, Lcom/vivox/service/vx_resp_aux_diagnostic_state_dump_t;->swigCPtr:J

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_resp_aux_diagnostic_state_dump_t_state_connectors_get(JLcom/vivox/service/vx_resp_aux_diagnostic_state_dump_t;)J

    move-result-wide v0

    .line 60
    .local v0, "vx_resp_aux_diagnostic_state_dump_t_state_connectors_get":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/SWIGTYPE_p_p_vx_state_connector;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/SWIGTYPE_p_p_vx_state_connector;-><init>(JZ)V

    goto :goto_0
.end method

.method public setBase(Lcom/vivox/service/vx_resp_base_t;)V
    .locals 6
    .param p1, "com_vivox_service_vx_resp_base_t"    # Lcom/vivox/service/vx_resp_base_t;

    .prologue
    .line 64
    iget-wide v0, p0, Lcom/vivox/service/vx_resp_aux_diagnostic_state_dump_t;->swigCPtr:J

    invoke-static {p1}, Lcom/vivox/service/vx_resp_base_t;->getCPtr(Lcom/vivox/service/vx_resp_base_t;)J

    move-result-wide v3

    move-object v2, p0

    move-object v5, p1

    invoke-static/range {v0 .. v5}, Lcom/vivox/service/VxClientProxyJNI;->vx_resp_aux_diagnostic_state_dump_t_base_set(JLcom/vivox/service/vx_resp_aux_diagnostic_state_dump_t;JLcom/vivox/service/vx_resp_base_t;)V

    .line 65
    return-void
.end method

.method public setCurrent_capture_device(Lcom/vivox/service/vx_device_t;)V
    .locals 6
    .param p1, "com_vivox_service_vx_device_t"    # Lcom/vivox/service/vx_device_t;

    .prologue
    .line 68
    iget-wide v0, p0, Lcom/vivox/service/vx_resp_aux_diagnostic_state_dump_t;->swigCPtr:J

    invoke-static {p1}, Lcom/vivox/service/vx_device_t;->getCPtr(Lcom/vivox/service/vx_device_t;)J

    move-result-wide v3

    move-object v2, p0

    move-object v5, p1

    invoke-static/range {v0 .. v5}, Lcom/vivox/service/VxClientProxyJNI;->vx_resp_aux_diagnostic_state_dump_t_current_capture_device_set(JLcom/vivox/service/vx_resp_aux_diagnostic_state_dump_t;JLcom/vivox/service/vx_device_t;)V

    .line 69
    return-void
.end method

.method public setCurrent_render_device(Lcom/vivox/service/vx_device_t;)V
    .locals 6
    .param p1, "com_vivox_service_vx_device_t"    # Lcom/vivox/service/vx_device_t;

    .prologue
    .line 72
    iget-wide v0, p0, Lcom/vivox/service/vx_resp_aux_diagnostic_state_dump_t;->swigCPtr:J

    invoke-static {p1}, Lcom/vivox/service/vx_device_t;->getCPtr(Lcom/vivox/service/vx_device_t;)J

    move-result-wide v3

    move-object v2, p0

    move-object v5, p1

    invoke-static/range {v0 .. v5}, Lcom/vivox/service/VxClientProxyJNI;->vx_resp_aux_diagnostic_state_dump_t_current_render_device_set(JLcom/vivox/service/vx_resp_aux_diagnostic_state_dump_t;JLcom/vivox/service/vx_device_t;)V

    .line 73
    return-void
.end method

.method public setEffective_capture_device(Lcom/vivox/service/vx_device_t;)V
    .locals 6
    .param p1, "com_vivox_service_vx_device_t"    # Lcom/vivox/service/vx_device_t;

    .prologue
    .line 76
    iget-wide v0, p0, Lcom/vivox/service/vx_resp_aux_diagnostic_state_dump_t;->swigCPtr:J

    invoke-static {p1}, Lcom/vivox/service/vx_device_t;->getCPtr(Lcom/vivox/service/vx_device_t;)J

    move-result-wide v3

    move-object v2, p0

    move-object v5, p1

    invoke-static/range {v0 .. v5}, Lcom/vivox/service/VxClientProxyJNI;->vx_resp_aux_diagnostic_state_dump_t_effective_capture_device_set(JLcom/vivox/service/vx_resp_aux_diagnostic_state_dump_t;JLcom/vivox/service/vx_device_t;)V

    .line 77
    return-void
.end method

.method public setEffective_render_device(Lcom/vivox/service/vx_device_t;)V
    .locals 6
    .param p1, "com_vivox_service_vx_device_t"    # Lcom/vivox/service/vx_device_t;

    .prologue
    .line 80
    iget-wide v0, p0, Lcom/vivox/service/vx_resp_aux_diagnostic_state_dump_t;->swigCPtr:J

    invoke-static {p1}, Lcom/vivox/service/vx_device_t;->getCPtr(Lcom/vivox/service/vx_device_t;)J

    move-result-wide v3

    move-object v2, p0

    move-object v5, p1

    invoke-static/range {v0 .. v5}, Lcom/vivox/service/VxClientProxyJNI;->vx_resp_aux_diagnostic_state_dump_t_effective_render_device_set(JLcom/vivox/service/vx_resp_aux_diagnostic_state_dump_t;JLcom/vivox/service/vx_device_t;)V

    .line 81
    return-void
.end method

.method public setState_connector_count(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 84
    iget-wide v0, p0, Lcom/vivox/service/vx_resp_aux_diagnostic_state_dump_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_resp_aux_diagnostic_state_dump_t_state_connector_count_set(JLcom/vivox/service/vx_resp_aux_diagnostic_state_dump_t;I)V

    .line 85
    return-void
.end method

.method public setState_connectors(Lcom/vivox/service/SWIGTYPE_p_p_vx_state_connector;)V
    .locals 4
    .param p1, "sWIGTYPE_p_p_vx_state_connector"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_state_connector;

    .prologue
    .line 88
    iget-wide v0, p0, Lcom/vivox/service/vx_resp_aux_diagnostic_state_dump_t;->swigCPtr:J

    invoke-static {p1}, Lcom/vivox/service/SWIGTYPE_p_p_vx_state_connector;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_state_connector;)J

    move-result-wide v2

    invoke-static {v0, v1, p0, v2, v3}, Lcom/vivox/service/VxClientProxyJNI;->vx_resp_aux_diagnostic_state_dump_t_state_connectors_set(JLcom/vivox/service/vx_resp_aux_diagnostic_state_dump_t;J)V

    .line 89
    return-void
.end method
