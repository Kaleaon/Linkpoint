.class public Lcom/vivox/service/vx_system_stats_t;
.super Ljava/lang/Object;
.source "vx_system_stats_t.java"


# instance fields
.field protected swigCMemOwn:Z

.field private swigCPtr:J


# direct methods
.method public constructor <init>()V
    .locals 3

    .prologue
    .line 8
    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->new_vx_system_stats_t()J

    move-result-wide v0

    const/4 v2, 0x1

    invoke-direct {p0, v0, v1, v2}, Lcom/vivox/service/vx_system_stats_t;-><init>(JZ)V

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
    iput-boolean p3, p0, Lcom/vivox/service/vx_system_stats_t;->swigCMemOwn:Z

    .line 13
    iput-wide p1, p0, Lcom/vivox/service/vx_system_stats_t;->swigCPtr:J

    .line 14
    return-void
.end method

.method protected static getCPtr(Lcom/vivox/service/vx_system_stats_t;)J
    .locals 2
    .param p0, "com_vivox_service_vx_system_stats_t"    # Lcom/vivox/service/vx_system_stats_t;

    .prologue
    .line 17
    if-nez p0, :cond_0

    const-wide/16 v0, 0x0

    :goto_0
    return-wide v0

    :cond_0
    iget-wide v0, p0, Lcom/vivox/service/vx_system_stats_t;->swigCPtr:J

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
    iget-wide v0, p0, Lcom/vivox/service/vx_system_stats_t;->swigCPtr:J

    cmp-long v0, v0, v2

    if-eqz v0, :cond_0

    iget-boolean v0, p0, Lcom/vivox/service/vx_system_stats_t;->swigCMemOwn:Z

    if-nez v0, :cond_1

    .line 22
    :cond_0
    const-wide/16 v0, 0x0

    iput-wide v0, p0, Lcom/vivox/service/vx_system_stats_t;->swigCPtr:J
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    .line 27
    monitor-exit p0

    return-void

    .line 24
    :cond_1
    const/4 v0, 0x0

    :try_start_1
    iput-boolean v0, p0, Lcom/vivox/service/vx_system_stats_t;->swigCMemOwn:Z

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

.method public getAr_source_count()I
    .locals 2

    .prologue
    .line 30
    iget-wide v0, p0, Lcom/vivox/service/vx_system_stats_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_system_stats_t_ar_source_count_get(JLcom/vivox/service/vx_system_stats_t;)I

    move-result v0

    return v0
.end method

.method public getAr_source_free_buffers()Lcom/vivox/service/vx_stat_sample_t;
    .locals 4

    .prologue
    .line 34
    iget-wide v2, p0, Lcom/vivox/service/vx_system_stats_t;->swigCPtr:J

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_system_stats_t_ar_source_free_buffers_get(JLcom/vivox/service/vx_system_stats_t;)J

    move-result-wide v0

    .line 35
    .local v0, "vx_system_stats_t_ar_source_free_buffers_get":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_stat_sample_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_stat_sample_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public getAr_source_poll_count()I
    .locals 2

    .prologue
    .line 39
    iget-wide v0, p0, Lcom/vivox/service/vx_system_stats_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_system_stats_t_ar_source_poll_count_get(JLcom/vivox/service/vx_system_stats_t;)I

    move-result v0

    return v0
.end method

.method public getAr_source_queue_depth()Lcom/vivox/service/vx_stat_sample_t;
    .locals 4

    .prologue
    .line 43
    iget-wide v2, p0, Lcom/vivox/service/vx_system_stats_t;->swigCPtr:J

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_system_stats_t_ar_source_queue_depth_get(JLcom/vivox/service/vx_system_stats_t;)J

    move-result-wide v0

    .line 44
    .local v0, "vx_system_stats_t_ar_source_queue_depth_get":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_stat_sample_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_stat_sample_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public getAr_source_queue_limit()I
    .locals 2

    .prologue
    .line 48
    iget-wide v0, p0, Lcom/vivox/service/vx_system_stats_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_system_stats_t_ar_source_queue_limit_get(JLcom/vivox/service/vx_system_stats_t;)I

    move-result v0

    return v0
.end method

.method public getAr_source_queue_overflows()I
    .locals 2

    .prologue
    .line 52
    iget-wide v0, p0, Lcom/vivox/service/vx_system_stats_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_system_stats_t_ar_source_queue_overflows_get(JLcom/vivox/service/vx_system_stats_t;)I

    move-result v0

    return v0
.end method

.method public getSs_size()I
    .locals 2

    .prologue
    .line 56
    iget-wide v0, p0, Lcom/vivox/service/vx_system_stats_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_system_stats_t_ss_size_get(JLcom/vivox/service/vx_system_stats_t;)I

    move-result v0

    return v0
.end method

.method public getTicker_thread()Lcom/vivox/service/vx_stat_thread_t;
    .locals 4

    .prologue
    .line 60
    iget-wide v2, p0, Lcom/vivox/service/vx_system_stats_t;->swigCPtr:J

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_system_stats_t_ticker_thread_get(JLcom/vivox/service/vx_system_stats_t;)J

    move-result-wide v0

    .line 61
    .local v0, "vx_system_stats_t_ticker_thread_get":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_stat_thread_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_stat_thread_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public getVp_thread()Lcom/vivox/service/vx_stat_thread_t;
    .locals 4

    .prologue
    .line 65
    iget-wide v2, p0, Lcom/vivox/service/vx_system_stats_t;->swigCPtr:J

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_system_stats_t_vp_thread_get(JLcom/vivox/service/vx_system_stats_t;)J

    move-result-wide v0

    .line 66
    .local v0, "vx_system_stats_t_vp_thread_get":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_stat_thread_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_stat_thread_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public setAr_source_count(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 70
    iget-wide v0, p0, Lcom/vivox/service/vx_system_stats_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_system_stats_t_ar_source_count_set(JLcom/vivox/service/vx_system_stats_t;I)V

    .line 71
    return-void
.end method

.method public setAr_source_free_buffers(Lcom/vivox/service/vx_stat_sample_t;)V
    .locals 6
    .param p1, "com_vivox_service_vx_stat_sample_t"    # Lcom/vivox/service/vx_stat_sample_t;

    .prologue
    .line 74
    iget-wide v0, p0, Lcom/vivox/service/vx_system_stats_t;->swigCPtr:J

    invoke-static {p1}, Lcom/vivox/service/vx_stat_sample_t;->getCPtr(Lcom/vivox/service/vx_stat_sample_t;)J

    move-result-wide v3

    move-object v2, p0

    move-object v5, p1

    invoke-static/range {v0 .. v5}, Lcom/vivox/service/VxClientProxyJNI;->vx_system_stats_t_ar_source_free_buffers_set(JLcom/vivox/service/vx_system_stats_t;JLcom/vivox/service/vx_stat_sample_t;)V

    .line 75
    return-void
.end method

.method public setAr_source_poll_count(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 78
    iget-wide v0, p0, Lcom/vivox/service/vx_system_stats_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_system_stats_t_ar_source_poll_count_set(JLcom/vivox/service/vx_system_stats_t;I)V

    .line 79
    return-void
.end method

.method public setAr_source_queue_depth(Lcom/vivox/service/vx_stat_sample_t;)V
    .locals 6
    .param p1, "com_vivox_service_vx_stat_sample_t"    # Lcom/vivox/service/vx_stat_sample_t;

    .prologue
    .line 82
    iget-wide v0, p0, Lcom/vivox/service/vx_system_stats_t;->swigCPtr:J

    invoke-static {p1}, Lcom/vivox/service/vx_stat_sample_t;->getCPtr(Lcom/vivox/service/vx_stat_sample_t;)J

    move-result-wide v3

    move-object v2, p0

    move-object v5, p1

    invoke-static/range {v0 .. v5}, Lcom/vivox/service/VxClientProxyJNI;->vx_system_stats_t_ar_source_queue_depth_set(JLcom/vivox/service/vx_system_stats_t;JLcom/vivox/service/vx_stat_sample_t;)V

    .line 83
    return-void
.end method

.method public setAr_source_queue_limit(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 86
    iget-wide v0, p0, Lcom/vivox/service/vx_system_stats_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_system_stats_t_ar_source_queue_limit_set(JLcom/vivox/service/vx_system_stats_t;I)V

    .line 87
    return-void
.end method

.method public setAr_source_queue_overflows(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 90
    iget-wide v0, p0, Lcom/vivox/service/vx_system_stats_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_system_stats_t_ar_source_queue_overflows_set(JLcom/vivox/service/vx_system_stats_t;I)V

    .line 91
    return-void
.end method

.method public setSs_size(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 94
    iget-wide v0, p0, Lcom/vivox/service/vx_system_stats_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_system_stats_t_ss_size_set(JLcom/vivox/service/vx_system_stats_t;I)V

    .line 95
    return-void
.end method

.method public setTicker_thread(Lcom/vivox/service/vx_stat_thread_t;)V
    .locals 6
    .param p1, "com_vivox_service_vx_stat_thread_t"    # Lcom/vivox/service/vx_stat_thread_t;

    .prologue
    .line 98
    iget-wide v0, p0, Lcom/vivox/service/vx_system_stats_t;->swigCPtr:J

    invoke-static {p1}, Lcom/vivox/service/vx_stat_thread_t;->getCPtr(Lcom/vivox/service/vx_stat_thread_t;)J

    move-result-wide v3

    move-object v2, p0

    move-object v5, p1

    invoke-static/range {v0 .. v5}, Lcom/vivox/service/VxClientProxyJNI;->vx_system_stats_t_ticker_thread_set(JLcom/vivox/service/vx_system_stats_t;JLcom/vivox/service/vx_stat_thread_t;)V

    .line 99
    return-void
.end method

.method public setVp_thread(Lcom/vivox/service/vx_stat_thread_t;)V
    .locals 6
    .param p1, "com_vivox_service_vx_stat_thread_t"    # Lcom/vivox/service/vx_stat_thread_t;

    .prologue
    .line 102
    iget-wide v0, p0, Lcom/vivox/service/vx_system_stats_t;->swigCPtr:J

    invoke-static {p1}, Lcom/vivox/service/vx_stat_thread_t;->getCPtr(Lcom/vivox/service/vx_stat_thread_t;)J

    move-result-wide v3

    move-object v2, p0

    move-object v5, p1

    invoke-static/range {v0 .. v5}, Lcom/vivox/service/VxClientProxyJNI;->vx_system_stats_t_vp_thread_set(JLcom/vivox/service/vx_system_stats_t;JLcom/vivox/service/vx_stat_thread_t;)V

    .line 103
    return-void
.end method
