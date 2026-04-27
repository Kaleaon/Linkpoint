.class public Lcom/vivox/service/vx_message_base_t;
.super Ljava/lang/Object;
.source "vx_message_base_t.java"


# instance fields
.field protected swigCMemOwn:Z

.field private swigCPtr:J


# direct methods
.method public constructor <init>()V
    .locals 3

    .prologue
    .line 10
    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->new_vx_message_base_t()J

    move-result-wide v0

    const/4 v2, 0x1

    invoke-direct {p0, v0, v1, v2}, Lcom/vivox/service/vx_message_base_t;-><init>(JZ)V

    .line 11
    return-void
.end method

.method protected constructor <init>(JZ)V
    .locals 1
    .param p1, "j"    # J
    .param p3, "z"    # Z

    .prologue
    .line 13
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 14
    iput-boolean p3, p0, Lcom/vivox/service/vx_message_base_t;->swigCMemOwn:Z

    .line 15
    iput-wide p1, p0, Lcom/vivox/service/vx_message_base_t;->swigCPtr:J

    .line 16
    return-void
.end method

.method public static getCPtr(Lcom/vivox/service/vx_message_base_t;)J
    .locals 2
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 19
    if-nez p0, :cond_0

    const-wide/16 v0, 0x0

    :goto_0
    return-wide v0

    :cond_0
    iget-wide v0, p0, Lcom/vivox/service/vx_message_base_t;->swigCPtr:J

    goto :goto_0
.end method


# virtual methods
.method public declared-synchronized delete()V
    .locals 4

    .prologue
    const-wide/16 v2, 0x0

    .line 23
    monitor-enter p0

    :try_start_0
    iget-wide v0, p0, Lcom/vivox/service/vx_message_base_t;->swigCPtr:J

    cmp-long v0, v0, v2

    if-eqz v0, :cond_0

    iget-boolean v0, p0, Lcom/vivox/service/vx_message_base_t;->swigCMemOwn:Z

    if-nez v0, :cond_1

    .line 24
    :cond_0
    const-wide/16 v0, 0x0

    iput-wide v0, p0, Lcom/vivox/service/vx_message_base_t;->swigCPtr:J
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    .line 29
    monitor-exit p0

    return-void

    .line 26
    :cond_1
    const/4 v0, 0x0

    :try_start_1
    iput-boolean v0, p0, Lcom/vivox/service/vx_message_base_t;->swigCMemOwn:Z

    .line 27
    new-instance v0, Ljava/lang/UnsupportedOperationException;

    const-string v1, "C++ destructor does not have public access"

    invoke-direct {v0, v1}, Ljava/lang/UnsupportedOperationException;-><init>(Ljava/lang/String;)V

    throw v0
    :try_end_1
    .catchall {:try_start_1 .. :try_end_1} :catchall_0

    .line 23
    :catchall_0
    move-exception v0

    monitor-exit p0

    throw v0
.end method

.method public getCreate_time_ms()Ljava/math/BigInteger;
    .locals 2

    .prologue
    .line 32
    iget-wide v0, p0, Lcom/vivox/service/vx_message_base_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t_create_time_ms_get(JLcom/vivox/service/vx_message_base_t;)Ljava/math/BigInteger;

    move-result-object v0

    return-object v0
.end method

.method public getLast_step_ms()Ljava/math/BigInteger;
    .locals 2

    .prologue
    .line 36
    iget-wide v0, p0, Lcom/vivox/service/vx_message_base_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t_last_step_ms_get(JLcom/vivox/service/vx_message_base_t;)Ljava/math/BigInteger;

    move-result-object v0

    return-object v0
.end method

.method public getSdk_handle()J
    .locals 2

    .prologue
    .line 40
    iget-wide v0, p0, Lcom/vivox/service/vx_message_base_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t_sdk_handle_get(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    return-wide v0
.end method

.method public getType()Lcom/vivox/service/vx_message_type;
    .locals 2

    .prologue
    .line 44
    iget-wide v0, p0, Lcom/vivox/service/vx_message_base_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t_type_get(JLcom/vivox/service/vx_message_base_t;)I

    move-result v0

    invoke-static {v0}, Lcom/vivox/service/vx_message_type;->swigToEnum(I)Lcom/vivox/service/vx_message_type;

    move-result-object v0

    return-object v0
.end method

.method public setCreate_time_ms(Ljava/math/BigInteger;)V
    .locals 2
    .param p1, "bigInteger"    # Ljava/math/BigInteger;

    .prologue
    .line 48
    iget-wide v0, p0, Lcom/vivox/service/vx_message_base_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t_create_time_ms_set(JLcom/vivox/service/vx_message_base_t;Ljava/math/BigInteger;)V

    .line 49
    return-void
.end method

.method public setLast_step_ms(Ljava/math/BigInteger;)V
    .locals 2
    .param p1, "bigInteger"    # Ljava/math/BigInteger;

    .prologue
    .line 52
    iget-wide v0, p0, Lcom/vivox/service/vx_message_base_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t_last_step_ms_set(JLcom/vivox/service/vx_message_base_t;Ljava/math/BigInteger;)V

    .line 53
    return-void
.end method

.method public setSdk_handle(J)V
    .locals 3
    .param p1, "j"    # J

    .prologue
    .line 56
    iget-wide v0, p0, Lcom/vivox/service/vx_message_base_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1, p2}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t_sdk_handle_set(JLcom/vivox/service/vx_message_base_t;J)V

    .line 57
    return-void
.end method

.method public setType(Lcom/vivox/service/vx_message_type;)V
    .locals 3
    .param p1, "com_vivox_service_vx_message_type"    # Lcom/vivox/service/vx_message_type;

    .prologue
    .line 60
    iget-wide v0, p0, Lcom/vivox/service/vx_message_base_t;->swigCPtr:J

    invoke-virtual {p1}, Lcom/vivox/service/vx_message_type;->swigValue()I

    move-result v2

    invoke-static {v0, v1, p0, v2}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t_type_set(JLcom/vivox/service/vx_message_base_t;I)V

    .line 61
    return-void
.end method
