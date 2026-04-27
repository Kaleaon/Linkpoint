.class public Lcom/vivox/service/vx_device_t;
.super Ljava/lang/Object;
.source "vx_device_t.java"


# instance fields
.field protected swigCMemOwn:Z

.field private swigCPtr:J


# direct methods
.method public constructor <init>()V
    .locals 3

    .prologue
    .line 8
    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->new_vx_device_t()J

    move-result-wide v0

    const/4 v2, 0x1

    invoke-direct {p0, v0, v1, v2}, Lcom/vivox/service/vx_device_t;-><init>(JZ)V

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
    iput-boolean p3, p0, Lcom/vivox/service/vx_device_t;->swigCMemOwn:Z

    .line 13
    iput-wide p1, p0, Lcom/vivox/service/vx_device_t;->swigCPtr:J

    .line 14
    return-void
.end method

.method protected static getCPtr(Lcom/vivox/service/vx_device_t;)J
    .locals 2
    .param p0, "com_vivox_service_vx_device_t"    # Lcom/vivox/service/vx_device_t;

    .prologue
    .line 17
    if-nez p0, :cond_0

    const-wide/16 v0, 0x0

    :goto_0
    return-wide v0

    :cond_0
    iget-wide v0, p0, Lcom/vivox/service/vx_device_t;->swigCPtr:J

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
    iget-wide v0, p0, Lcom/vivox/service/vx_device_t;->swigCPtr:J

    cmp-long v0, v0, v2

    if-eqz v0, :cond_0

    iget-boolean v0, p0, Lcom/vivox/service/vx_device_t;->swigCMemOwn:Z

    if-eqz v0, :cond_0

    .line 22
    const/4 v0, 0x0

    iput-boolean v0, p0, Lcom/vivox/service/vx_device_t;->swigCMemOwn:Z

    .line 23
    iget-wide v0, p0, Lcom/vivox/service/vx_device_t;->swigCPtr:J

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->delete_vx_device_t(J)V

    .line 25
    :cond_0
    const-wide/16 v0, 0x0

    iput-wide v0, p0, Lcom/vivox/service/vx_device_t;->swigCPtr:J
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    .line 26
    monitor-exit p0

    return-void

    .line 21
    :catchall_0
    move-exception v0

    monitor-exit p0

    throw v0
.end method

.method protected finalize()V
    .locals 0

    .prologue
    .line 29
    invoke-virtual {p0}, Lcom/vivox/service/vx_device_t;->delete()V

    .line 30
    return-void
.end method

.method public getDevice()Ljava/lang/String;
    .locals 2

    .prologue
    .line 33
    iget-wide v0, p0, Lcom/vivox/service/vx_device_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_device_t_device_get(JLcom/vivox/service/vx_device_t;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public getDevice_type()Lcom/vivox/service/vx_device_type_t;
    .locals 2

    .prologue
    .line 37
    iget-wide v0, p0, Lcom/vivox/service/vx_device_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_device_t_device_type_get(JLcom/vivox/service/vx_device_t;)I

    move-result v0

    invoke-static {v0}, Lcom/vivox/service/vx_device_type_t;->swigToEnum(I)Lcom/vivox/service/vx_device_type_t;

    move-result-object v0

    return-object v0
.end method

.method public getDisplay_name()Ljava/lang/String;
    .locals 2

    .prologue
    .line 41
    iget-wide v0, p0, Lcom/vivox/service/vx_device_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_device_t_display_name_get(JLcom/vivox/service/vx_device_t;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public setDevice(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 45
    iget-wide v0, p0, Lcom/vivox/service/vx_device_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_device_t_device_set(JLcom/vivox/service/vx_device_t;Ljava/lang/String;)V

    .line 46
    return-void
.end method

.method public setDevice_type(Lcom/vivox/service/vx_device_type_t;)V
    .locals 3
    .param p1, "com_vivox_service_vx_device_type_t"    # Lcom/vivox/service/vx_device_type_t;

    .prologue
    .line 49
    iget-wide v0, p0, Lcom/vivox/service/vx_device_t;->swigCPtr:J

    invoke-virtual {p1}, Lcom/vivox/service/vx_device_type_t;->swigValue()I

    move-result v2

    invoke-static {v0, v1, p0, v2}, Lcom/vivox/service/VxClientProxyJNI;->vx_device_t_device_type_set(JLcom/vivox/service/vx_device_t;I)V

    .line 50
    return-void
.end method

.method public setDisplay_name(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 53
    iget-wide v0, p0, Lcom/vivox/service/vx_device_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_device_t_display_name_set(JLcom/vivox/service/vx_device_t;Ljava/lang/String;)V

    .line 54
    return-void
.end method
