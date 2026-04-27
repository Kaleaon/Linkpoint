.class public Lcom/vivox/service/vx_stat_thread_t;
.super Ljava/lang/Object;
.source "vx_stat_thread_t.java"


# instance fields
.field protected swigCMemOwn:Z

.field private swigCPtr:J


# direct methods
.method public constructor <init>()V
    .locals 3

    .prologue
    .line 8
    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->new_vx_stat_thread_t()J

    move-result-wide v0

    const/4 v2, 0x1

    invoke-direct {p0, v0, v1, v2}, Lcom/vivox/service/vx_stat_thread_t;-><init>(JZ)V

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
    iput-boolean p3, p0, Lcom/vivox/service/vx_stat_thread_t;->swigCMemOwn:Z

    .line 13
    iput-wide p1, p0, Lcom/vivox/service/vx_stat_thread_t;->swigCPtr:J

    .line 14
    return-void
.end method

.method protected static getCPtr(Lcom/vivox/service/vx_stat_thread_t;)J
    .locals 2
    .param p0, "com_vivox_service_vx_stat_thread_t"    # Lcom/vivox/service/vx_stat_thread_t;

    .prologue
    .line 17
    if-nez p0, :cond_0

    const-wide/16 v0, 0x0

    :goto_0
    return-wide v0

    :cond_0
    iget-wide v0, p0, Lcom/vivox/service/vx_stat_thread_t;->swigCPtr:J

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
    iget-wide v0, p0, Lcom/vivox/service/vx_stat_thread_t;->swigCPtr:J

    cmp-long v0, v0, v2

    if-eqz v0, :cond_0

    iget-boolean v0, p0, Lcom/vivox/service/vx_stat_thread_t;->swigCMemOwn:Z

    if-nez v0, :cond_1

    .line 22
    :cond_0
    const-wide/16 v0, 0x0

    iput-wide v0, p0, Lcom/vivox/service/vx_stat_thread_t;->swigCPtr:J
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    .line 27
    monitor-exit p0

    return-void

    .line 24
    :cond_1
    const/4 v0, 0x0

    :try_start_1
    iput-boolean v0, p0, Lcom/vivox/service/vx_stat_thread_t;->swigCMemOwn:Z

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

.method public getCount_poll_gte_25ms()I
    .locals 2

    .prologue
    .line 30
    iget-wide v0, p0, Lcom/vivox/service/vx_stat_thread_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_stat_thread_t_count_poll_gte_25ms_get(JLcom/vivox/service/vx_stat_thread_t;)I

    move-result v0

    return v0
.end method

.method public getCount_poll_lt_10ms()I
    .locals 2

    .prologue
    .line 34
    iget-wide v0, p0, Lcom/vivox/service/vx_stat_thread_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_stat_thread_t_count_poll_lt_10ms_get(JLcom/vivox/service/vx_stat_thread_t;)I

    move-result v0

    return v0
.end method

.method public getCount_poll_lt_16ms()I
    .locals 2

    .prologue
    .line 38
    iget-wide v0, p0, Lcom/vivox/service/vx_stat_thread_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_stat_thread_t_count_poll_lt_16ms_get(JLcom/vivox/service/vx_stat_thread_t;)I

    move-result v0

    return v0
.end method

.method public getCount_poll_lt_1ms()I
    .locals 2

    .prologue
    .line 42
    iget-wide v0, p0, Lcom/vivox/service/vx_stat_thread_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_stat_thread_t_count_poll_lt_1ms_get(JLcom/vivox/service/vx_stat_thread_t;)I

    move-result v0

    return v0
.end method

.method public getCount_poll_lt_20ms()I
    .locals 2

    .prologue
    .line 46
    iget-wide v0, p0, Lcom/vivox/service/vx_stat_thread_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_stat_thread_t_count_poll_lt_20ms_get(JLcom/vivox/service/vx_stat_thread_t;)I

    move-result v0

    return v0
.end method

.method public getCount_poll_lt_25ms()I
    .locals 2

    .prologue
    .line 50
    iget-wide v0, p0, Lcom/vivox/service/vx_stat_thread_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_stat_thread_t_count_poll_lt_25ms_get(JLcom/vivox/service/vx_stat_thread_t;)I

    move-result v0

    return v0
.end method

.method public getCount_poll_lt_5ms()I
    .locals 2

    .prologue
    .line 54
    iget-wide v0, p0, Lcom/vivox/service/vx_stat_thread_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_stat_thread_t_count_poll_lt_5ms_get(JLcom/vivox/service/vx_stat_thread_t;)I

    move-result v0

    return v0
.end method

.method public getInterval()I
    .locals 2

    .prologue
    .line 58
    iget-wide v0, p0, Lcom/vivox/service/vx_stat_thread_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_stat_thread_t_interval_get(JLcom/vivox/service/vx_stat_thread_t;)I

    move-result v0

    return v0
.end method

.method public setCount_poll_gte_25ms(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 62
    iget-wide v0, p0, Lcom/vivox/service/vx_stat_thread_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_stat_thread_t_count_poll_gte_25ms_set(JLcom/vivox/service/vx_stat_thread_t;I)V

    .line 63
    return-void
.end method

.method public setCount_poll_lt_10ms(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 66
    iget-wide v0, p0, Lcom/vivox/service/vx_stat_thread_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_stat_thread_t_count_poll_lt_10ms_set(JLcom/vivox/service/vx_stat_thread_t;I)V

    .line 67
    return-void
.end method

.method public setCount_poll_lt_16ms(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 70
    iget-wide v0, p0, Lcom/vivox/service/vx_stat_thread_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_stat_thread_t_count_poll_lt_16ms_set(JLcom/vivox/service/vx_stat_thread_t;I)V

    .line 71
    return-void
.end method

.method public setCount_poll_lt_1ms(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 74
    iget-wide v0, p0, Lcom/vivox/service/vx_stat_thread_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_stat_thread_t_count_poll_lt_1ms_set(JLcom/vivox/service/vx_stat_thread_t;I)V

    .line 75
    return-void
.end method

.method public setCount_poll_lt_20ms(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 78
    iget-wide v0, p0, Lcom/vivox/service/vx_stat_thread_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_stat_thread_t_count_poll_lt_20ms_set(JLcom/vivox/service/vx_stat_thread_t;I)V

    .line 79
    return-void
.end method

.method public setCount_poll_lt_25ms(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 82
    iget-wide v0, p0, Lcom/vivox/service/vx_stat_thread_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_stat_thread_t_count_poll_lt_25ms_set(JLcom/vivox/service/vx_stat_thread_t;I)V

    .line 83
    return-void
.end method

.method public setCount_poll_lt_5ms(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 86
    iget-wide v0, p0, Lcom/vivox/service/vx_stat_thread_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_stat_thread_t_count_poll_lt_5ms_set(JLcom/vivox/service/vx_stat_thread_t;I)V

    .line 87
    return-void
.end method

.method public setInterval(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 90
    iget-wide v0, p0, Lcom/vivox/service/vx_stat_thread_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_stat_thread_t_interval_set(JLcom/vivox/service/vx_stat_thread_t;I)V

    .line 91
    return-void
.end method
