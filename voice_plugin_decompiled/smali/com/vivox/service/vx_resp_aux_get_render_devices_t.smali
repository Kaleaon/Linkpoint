.class public Lcom/vivox/service/vx_resp_aux_get_render_devices_t;
.super Ljava/lang/Object;
.source "vx_resp_aux_get_render_devices_t.java"


# instance fields
.field protected swigCMemOwn:Z

.field private swigCPtr:J


# direct methods
.method public constructor <init>()V
    .locals 3

    .prologue
    .line 8
    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->new_vx_resp_aux_get_render_devices_t()J

    move-result-wide v0

    const/4 v2, 0x1

    invoke-direct {p0, v0, v1, v2}, Lcom/vivox/service/vx_resp_aux_get_render_devices_t;-><init>(JZ)V

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
    iput-boolean p3, p0, Lcom/vivox/service/vx_resp_aux_get_render_devices_t;->swigCMemOwn:Z

    .line 13
    iput-wide p1, p0, Lcom/vivox/service/vx_resp_aux_get_render_devices_t;->swigCPtr:J

    .line 14
    return-void
.end method

.method protected static getCPtr(Lcom/vivox/service/vx_resp_aux_get_render_devices_t;)J
    .locals 2
    .param p0, "com_vivox_service_vx_resp_aux_get_render_devices_t"    # Lcom/vivox/service/vx_resp_aux_get_render_devices_t;

    .prologue
    .line 17
    if-nez p0, :cond_0

    const-wide/16 v0, 0x0

    :goto_0
    return-wide v0

    :cond_0
    iget-wide v0, p0, Lcom/vivox/service/vx_resp_aux_get_render_devices_t;->swigCPtr:J

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
    iget-wide v0, p0, Lcom/vivox/service/vx_resp_aux_get_render_devices_t;->swigCPtr:J

    cmp-long v0, v0, v2

    if-eqz v0, :cond_0

    iget-boolean v0, p0, Lcom/vivox/service/vx_resp_aux_get_render_devices_t;->swigCMemOwn:Z

    if-nez v0, :cond_1

    .line 22
    :cond_0
    const-wide/16 v0, 0x0

    iput-wide v0, p0, Lcom/vivox/service/vx_resp_aux_get_render_devices_t;->swigCPtr:J
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    .line 27
    monitor-exit p0

    return-void

    .line 24
    :cond_1
    const/4 v0, 0x0

    :try_start_1
    iput-boolean v0, p0, Lcom/vivox/service/vx_resp_aux_get_render_devices_t;->swigCMemOwn:Z

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
    iget-wide v2, p0, Lcom/vivox/service/vx_resp_aux_get_render_devices_t;->swigCPtr:J

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_resp_aux_get_render_devices_t_base_get(JLcom/vivox/service/vx_resp_aux_get_render_devices_t;)J

    move-result-wide v0

    .line 31
    .local v0, "vx_resp_aux_get_render_devices_t_base_get":J
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

.method public getCount()I
    .locals 2

    .prologue
    .line 35
    iget-wide v0, p0, Lcom/vivox/service/vx_resp_aux_get_render_devices_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_resp_aux_get_render_devices_t_count_get(JLcom/vivox/service/vx_resp_aux_get_render_devices_t;)I

    move-result v0

    return v0
.end method

.method public getCurrent_render_device()Lcom/vivox/service/vx_device_t;
    .locals 4

    .prologue
    .line 39
    iget-wide v2, p0, Lcom/vivox/service/vx_resp_aux_get_render_devices_t;->swigCPtr:J

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_resp_aux_get_render_devices_t_current_render_device_get(JLcom/vivox/service/vx_resp_aux_get_render_devices_t;)J

    move-result-wide v0

    .line 40
    .local v0, "vx_resp_aux_get_render_devices_t_current_render_device_get":J
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
    .line 44
    iget-wide v2, p0, Lcom/vivox/service/vx_resp_aux_get_render_devices_t;->swigCPtr:J

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_resp_aux_get_render_devices_t_effective_render_device_get(JLcom/vivox/service/vx_resp_aux_get_render_devices_t;)J

    move-result-wide v0

    .line 45
    .local v0, "vx_resp_aux_get_render_devices_t_effective_render_device_get":J
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

.method public getRender_devices()Lcom/vivox/service/SWIGTYPE_p_p_vx_device;
    .locals 4

    .prologue
    .line 49
    iget-wide v2, p0, Lcom/vivox/service/vx_resp_aux_get_render_devices_t;->swigCPtr:J

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_resp_aux_get_render_devices_t_render_devices_get(JLcom/vivox/service/vx_resp_aux_get_render_devices_t;)J

    move-result-wide v0

    .line 50
    .local v0, "vx_resp_aux_get_render_devices_t_render_devices_get":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/SWIGTYPE_p_p_vx_device;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/SWIGTYPE_p_p_vx_device;-><init>(JZ)V

    goto :goto_0
.end method

.method public setBase(Lcom/vivox/service/vx_resp_base_t;)V
    .locals 6
    .param p1, "com_vivox_service_vx_resp_base_t"    # Lcom/vivox/service/vx_resp_base_t;

    .prologue
    .line 54
    iget-wide v0, p0, Lcom/vivox/service/vx_resp_aux_get_render_devices_t;->swigCPtr:J

    invoke-static {p1}, Lcom/vivox/service/vx_resp_base_t;->getCPtr(Lcom/vivox/service/vx_resp_base_t;)J

    move-result-wide v3

    move-object v2, p0

    move-object v5, p1

    invoke-static/range {v0 .. v5}, Lcom/vivox/service/VxClientProxyJNI;->vx_resp_aux_get_render_devices_t_base_set(JLcom/vivox/service/vx_resp_aux_get_render_devices_t;JLcom/vivox/service/vx_resp_base_t;)V

    .line 55
    return-void
.end method

.method public setCount(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 58
    iget-wide v0, p0, Lcom/vivox/service/vx_resp_aux_get_render_devices_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_resp_aux_get_render_devices_t_count_set(JLcom/vivox/service/vx_resp_aux_get_render_devices_t;I)V

    .line 59
    return-void
.end method

.method public setCurrent_render_device(Lcom/vivox/service/vx_device_t;)V
    .locals 6
    .param p1, "com_vivox_service_vx_device_t"    # Lcom/vivox/service/vx_device_t;

    .prologue
    .line 62
    iget-wide v0, p0, Lcom/vivox/service/vx_resp_aux_get_render_devices_t;->swigCPtr:J

    invoke-static {p1}, Lcom/vivox/service/vx_device_t;->getCPtr(Lcom/vivox/service/vx_device_t;)J

    move-result-wide v3

    move-object v2, p0

    move-object v5, p1

    invoke-static/range {v0 .. v5}, Lcom/vivox/service/VxClientProxyJNI;->vx_resp_aux_get_render_devices_t_current_render_device_set(JLcom/vivox/service/vx_resp_aux_get_render_devices_t;JLcom/vivox/service/vx_device_t;)V

    .line 63
    return-void
.end method

.method public setEffective_render_device(Lcom/vivox/service/vx_device_t;)V
    .locals 6
    .param p1, "com_vivox_service_vx_device_t"    # Lcom/vivox/service/vx_device_t;

    .prologue
    .line 66
    iget-wide v0, p0, Lcom/vivox/service/vx_resp_aux_get_render_devices_t;->swigCPtr:J

    invoke-static {p1}, Lcom/vivox/service/vx_device_t;->getCPtr(Lcom/vivox/service/vx_device_t;)J

    move-result-wide v3

    move-object v2, p0

    move-object v5, p1

    invoke-static/range {v0 .. v5}, Lcom/vivox/service/VxClientProxyJNI;->vx_resp_aux_get_render_devices_t_effective_render_device_set(JLcom/vivox/service/vx_resp_aux_get_render_devices_t;JLcom/vivox/service/vx_device_t;)V

    .line 67
    return-void
.end method

.method public setRender_devices(Lcom/vivox/service/SWIGTYPE_p_p_vx_device;)V
    .locals 4
    .param p1, "sWIGTYPE_p_p_vx_device"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_device;

    .prologue
    .line 70
    iget-wide v0, p0, Lcom/vivox/service/vx_resp_aux_get_render_devices_t;->swigCPtr:J

    invoke-static {p1}, Lcom/vivox/service/SWIGTYPE_p_p_vx_device;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_device;)J

    move-result-wide v2

    invoke-static {v0, v1, p0, v2, v3}, Lcom/vivox/service/VxClientProxyJNI;->vx_resp_aux_get_render_devices_t_render_devices_set(JLcom/vivox/service/vx_resp_aux_get_render_devices_t;J)V

    .line 71
    return-void
.end method
