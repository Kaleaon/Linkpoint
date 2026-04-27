.class public Lcom/vivox/service/vx_resp_sessiongroup_get_stats_t;
.super Ljava/lang/Object;
.source "vx_resp_sessiongroup_get_stats_t.java"


# instance fields
.field protected swigCMemOwn:Z

.field private swigCPtr:J


# direct methods
.method public constructor <init>()V
    .locals 3

    .prologue
    .line 8
    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->new_vx_resp_sessiongroup_get_stats_t()J

    move-result-wide v0

    const/4 v2, 0x1

    invoke-direct {p0, v0, v1, v2}, Lcom/vivox/service/vx_resp_sessiongroup_get_stats_t;-><init>(JZ)V

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
    iput-boolean p3, p0, Lcom/vivox/service/vx_resp_sessiongroup_get_stats_t;->swigCMemOwn:Z

    .line 13
    iput-wide p1, p0, Lcom/vivox/service/vx_resp_sessiongroup_get_stats_t;->swigCPtr:J

    .line 14
    return-void
.end method

.method protected static getCPtr(Lcom/vivox/service/vx_resp_sessiongroup_get_stats_t;)J
    .locals 2
    .param p0, "com_vivox_service_vx_resp_sessiongroup_get_stats_t"    # Lcom/vivox/service/vx_resp_sessiongroup_get_stats_t;

    .prologue
    .line 17
    if-nez p0, :cond_0

    const-wide/16 v0, 0x0

    :goto_0
    return-wide v0

    :cond_0
    iget-wide v0, p0, Lcom/vivox/service/vx_resp_sessiongroup_get_stats_t;->swigCPtr:J

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
    iget-wide v0, p0, Lcom/vivox/service/vx_resp_sessiongroup_get_stats_t;->swigCPtr:J

    cmp-long v0, v0, v2

    if-eqz v0, :cond_0

    iget-boolean v0, p0, Lcom/vivox/service/vx_resp_sessiongroup_get_stats_t;->swigCMemOwn:Z

    if-nez v0, :cond_1

    .line 22
    :cond_0
    const-wide/16 v0, 0x0

    iput-wide v0, p0, Lcom/vivox/service/vx_resp_sessiongroup_get_stats_t;->swigCPtr:J
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    .line 27
    monitor-exit p0

    return-void

    .line 24
    :cond_1
    const/4 v0, 0x0

    :try_start_1
    iput-boolean v0, p0, Lcom/vivox/service/vx_resp_sessiongroup_get_stats_t;->swigCMemOwn:Z

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
    iget-wide v2, p0, Lcom/vivox/service/vx_resp_sessiongroup_get_stats_t;->swigCPtr:J

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_resp_sessiongroup_get_stats_t_base_get(JLcom/vivox/service/vx_resp_sessiongroup_get_stats_t;)J

    move-result-wide v0

    .line 31
    .local v0, "vx_resp_sessiongroup_get_stats_t_base_get":J
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

.method public getCall_id()Ljava/lang/String;
    .locals 2

    .prologue
    .line 35
    iget-wide v0, p0, Lcom/vivox/service/vx_resp_sessiongroup_get_stats_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_resp_sessiongroup_get_stats_t_call_id_get(JLcom/vivox/service/vx_resp_sessiongroup_get_stats_t;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public getCurrent_bars()I
    .locals 2

    .prologue
    .line 39
    iget-wide v0, p0, Lcom/vivox/service/vx_resp_sessiongroup_get_stats_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_resp_sessiongroup_get_stats_t_current_bars_get(JLcom/vivox/service/vx_resp_sessiongroup_get_stats_t;)I

    move-result v0

    return v0
.end method

.method public getIncoming_discarded()I
    .locals 2

    .prologue
    .line 43
    iget-wide v0, p0, Lcom/vivox/service/vx_resp_sessiongroup_get_stats_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_resp_sessiongroup_get_stats_t_incoming_discarded_get(JLcom/vivox/service/vx_resp_sessiongroup_get_stats_t;)I

    move-result v0

    return v0
.end method

.method public getIncoming_expected()I
    .locals 2

    .prologue
    .line 47
    iget-wide v0, p0, Lcom/vivox/service/vx_resp_sessiongroup_get_stats_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_resp_sessiongroup_get_stats_t_incoming_expected_get(JLcom/vivox/service/vx_resp_sessiongroup_get_stats_t;)I

    move-result v0

    return v0
.end method

.method public getIncoming_out_of_time()I
    .locals 2

    .prologue
    .line 51
    iget-wide v0, p0, Lcom/vivox/service/vx_resp_sessiongroup_get_stats_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_resp_sessiongroup_get_stats_t_incoming_out_of_time_get(JLcom/vivox/service/vx_resp_sessiongroup_get_stats_t;)I

    move-result v0

    return v0
.end method

.method public getIncoming_packetloss()I
    .locals 2

    .prologue
    .line 55
    iget-wide v0, p0, Lcom/vivox/service/vx_resp_sessiongroup_get_stats_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_resp_sessiongroup_get_stats_t_incoming_packetloss_get(JLcom/vivox/service/vx_resp_sessiongroup_get_stats_t;)I

    move-result v0

    return v0
.end method

.method public getIncoming_received()I
    .locals 2

    .prologue
    .line 59
    iget-wide v0, p0, Lcom/vivox/service/vx_resp_sessiongroup_get_stats_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_resp_sessiongroup_get_stats_t_incoming_received_get(JLcom/vivox/service/vx_resp_sessiongroup_get_stats_t;)I

    move-result v0

    return v0
.end method

.method public getInsufficient_bandwidth()I
    .locals 2

    .prologue
    .line 63
    iget-wide v0, p0, Lcom/vivox/service/vx_resp_sessiongroup_get_stats_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_resp_sessiongroup_get_stats_t_insufficient_bandwidth_get(JLcom/vivox/service/vx_resp_sessiongroup_get_stats_t;)I

    move-result v0

    return v0
.end method

.method public getMax_bars()I
    .locals 2

    .prologue
    .line 67
    iget-wide v0, p0, Lcom/vivox/service/vx_resp_sessiongroup_get_stats_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_resp_sessiongroup_get_stats_t_max_bars_get(JLcom/vivox/service/vx_resp_sessiongroup_get_stats_t;)I

    move-result v0

    return v0
.end method

.method public getMin_bars()I
    .locals 2

    .prologue
    .line 71
    iget-wide v0, p0, Lcom/vivox/service/vx_resp_sessiongroup_get_stats_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_resp_sessiongroup_get_stats_t_min_bars_get(JLcom/vivox/service/vx_resp_sessiongroup_get_stats_t;)I

    move-result v0

    return v0
.end method

.method public getOutgoing_sent()I
    .locals 2

    .prologue
    .line 75
    iget-wide v0, p0, Lcom/vivox/service/vx_resp_sessiongroup_get_stats_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_resp_sessiongroup_get_stats_t_outgoing_sent_get(JLcom/vivox/service/vx_resp_sessiongroup_get_stats_t;)I

    move-result v0

    return v0
.end method

.method public getPk_loss()I
    .locals 2

    .prologue
    .line 79
    iget-wide v0, p0, Lcom/vivox/service/vx_resp_sessiongroup_get_stats_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_resp_sessiongroup_get_stats_t_pk_loss_get(JLcom/vivox/service/vx_resp_sessiongroup_get_stats_t;)I

    move-result v0

    return v0
.end method

.method public getRender_device_errors()I
    .locals 2

    .prologue
    .line 83
    iget-wide v0, p0, Lcom/vivox/service/vx_resp_sessiongroup_get_stats_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_resp_sessiongroup_get_stats_t_render_device_errors_get(JLcom/vivox/service/vx_resp_sessiongroup_get_stats_t;)I

    move-result v0

    return v0
.end method

.method public getRender_device_overruns()I
    .locals 2

    .prologue
    .line 87
    iget-wide v0, p0, Lcom/vivox/service/vx_resp_sessiongroup_get_stats_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_resp_sessiongroup_get_stats_t_render_device_overruns_get(JLcom/vivox/service/vx_resp_sessiongroup_get_stats_t;)I

    move-result v0

    return v0
.end method

.method public getRender_device_underruns()I
    .locals 2

    .prologue
    .line 91
    iget-wide v0, p0, Lcom/vivox/service/vx_resp_sessiongroup_get_stats_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_resp_sessiongroup_get_stats_t_render_device_underruns_get(JLcom/vivox/service/vx_resp_sessiongroup_get_stats_t;)I

    move-result v0

    return v0
.end method

.method public setBase(Lcom/vivox/service/vx_resp_base_t;)V
    .locals 6
    .param p1, "com_vivox_service_vx_resp_base_t"    # Lcom/vivox/service/vx_resp_base_t;

    .prologue
    .line 95
    iget-wide v0, p0, Lcom/vivox/service/vx_resp_sessiongroup_get_stats_t;->swigCPtr:J

    invoke-static {p1}, Lcom/vivox/service/vx_resp_base_t;->getCPtr(Lcom/vivox/service/vx_resp_base_t;)J

    move-result-wide v3

    move-object v2, p0

    move-object v5, p1

    invoke-static/range {v0 .. v5}, Lcom/vivox/service/VxClientProxyJNI;->vx_resp_sessiongroup_get_stats_t_base_set(JLcom/vivox/service/vx_resp_sessiongroup_get_stats_t;JLcom/vivox/service/vx_resp_base_t;)V

    .line 96
    return-void
.end method

.method public setCall_id(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 99
    iget-wide v0, p0, Lcom/vivox/service/vx_resp_sessiongroup_get_stats_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_resp_sessiongroup_get_stats_t_call_id_set(JLcom/vivox/service/vx_resp_sessiongroup_get_stats_t;Ljava/lang/String;)V

    .line 100
    return-void
.end method

.method public setCurrent_bars(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 103
    iget-wide v0, p0, Lcom/vivox/service/vx_resp_sessiongroup_get_stats_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_resp_sessiongroup_get_stats_t_current_bars_set(JLcom/vivox/service/vx_resp_sessiongroup_get_stats_t;I)V

    .line 104
    return-void
.end method

.method public setIncoming_discarded(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 107
    iget-wide v0, p0, Lcom/vivox/service/vx_resp_sessiongroup_get_stats_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_resp_sessiongroup_get_stats_t_incoming_discarded_set(JLcom/vivox/service/vx_resp_sessiongroup_get_stats_t;I)V

    .line 108
    return-void
.end method

.method public setIncoming_expected(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 111
    iget-wide v0, p0, Lcom/vivox/service/vx_resp_sessiongroup_get_stats_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_resp_sessiongroup_get_stats_t_incoming_expected_set(JLcom/vivox/service/vx_resp_sessiongroup_get_stats_t;I)V

    .line 112
    return-void
.end method

.method public setIncoming_out_of_time(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 115
    iget-wide v0, p0, Lcom/vivox/service/vx_resp_sessiongroup_get_stats_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_resp_sessiongroup_get_stats_t_incoming_out_of_time_set(JLcom/vivox/service/vx_resp_sessiongroup_get_stats_t;I)V

    .line 116
    return-void
.end method

.method public setIncoming_packetloss(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 119
    iget-wide v0, p0, Lcom/vivox/service/vx_resp_sessiongroup_get_stats_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_resp_sessiongroup_get_stats_t_incoming_packetloss_set(JLcom/vivox/service/vx_resp_sessiongroup_get_stats_t;I)V

    .line 120
    return-void
.end method

.method public setIncoming_received(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 123
    iget-wide v0, p0, Lcom/vivox/service/vx_resp_sessiongroup_get_stats_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_resp_sessiongroup_get_stats_t_incoming_received_set(JLcom/vivox/service/vx_resp_sessiongroup_get_stats_t;I)V

    .line 124
    return-void
.end method

.method public setInsufficient_bandwidth(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 127
    iget-wide v0, p0, Lcom/vivox/service/vx_resp_sessiongroup_get_stats_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_resp_sessiongroup_get_stats_t_insufficient_bandwidth_set(JLcom/vivox/service/vx_resp_sessiongroup_get_stats_t;I)V

    .line 128
    return-void
.end method

.method public setMax_bars(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 131
    iget-wide v0, p0, Lcom/vivox/service/vx_resp_sessiongroup_get_stats_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_resp_sessiongroup_get_stats_t_max_bars_set(JLcom/vivox/service/vx_resp_sessiongroup_get_stats_t;I)V

    .line 132
    return-void
.end method

.method public setMin_bars(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 135
    iget-wide v0, p0, Lcom/vivox/service/vx_resp_sessiongroup_get_stats_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_resp_sessiongroup_get_stats_t_min_bars_set(JLcom/vivox/service/vx_resp_sessiongroup_get_stats_t;I)V

    .line 136
    return-void
.end method

.method public setOutgoing_sent(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 139
    iget-wide v0, p0, Lcom/vivox/service/vx_resp_sessiongroup_get_stats_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_resp_sessiongroup_get_stats_t_outgoing_sent_set(JLcom/vivox/service/vx_resp_sessiongroup_get_stats_t;I)V

    .line 140
    return-void
.end method

.method public setPk_loss(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 143
    iget-wide v0, p0, Lcom/vivox/service/vx_resp_sessiongroup_get_stats_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_resp_sessiongroup_get_stats_t_pk_loss_set(JLcom/vivox/service/vx_resp_sessiongroup_get_stats_t;I)V

    .line 144
    return-void
.end method

.method public setRender_device_errors(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 147
    iget-wide v0, p0, Lcom/vivox/service/vx_resp_sessiongroup_get_stats_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_resp_sessiongroup_get_stats_t_render_device_errors_set(JLcom/vivox/service/vx_resp_sessiongroup_get_stats_t;I)V

    .line 148
    return-void
.end method

.method public setRender_device_overruns(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 151
    iget-wide v0, p0, Lcom/vivox/service/vx_resp_sessiongroup_get_stats_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_resp_sessiongroup_get_stats_t_render_device_overruns_set(JLcom/vivox/service/vx_resp_sessiongroup_get_stats_t;I)V

    .line 152
    return-void
.end method

.method public setRender_device_underruns(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 155
    iget-wide v0, p0, Lcom/vivox/service/vx_resp_sessiongroup_get_stats_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_resp_sessiongroup_get_stats_t_render_device_underruns_set(JLcom/vivox/service/vx_resp_sessiongroup_get_stats_t;I)V

    .line 156
    return-void
.end method
