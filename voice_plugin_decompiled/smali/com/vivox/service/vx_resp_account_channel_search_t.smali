.class public Lcom/vivox/service/vx_resp_account_channel_search_t;
.super Ljava/lang/Object;
.source "vx_resp_account_channel_search_t.java"


# instance fields
.field protected swigCMemOwn:Z

.field private swigCPtr:J


# direct methods
.method public constructor <init>()V
    .locals 3

    .prologue
    .line 8
    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->new_vx_resp_account_channel_search_t()J

    move-result-wide v0

    const/4 v2, 0x1

    invoke-direct {p0, v0, v1, v2}, Lcom/vivox/service/vx_resp_account_channel_search_t;-><init>(JZ)V

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
    iput-boolean p3, p0, Lcom/vivox/service/vx_resp_account_channel_search_t;->swigCMemOwn:Z

    .line 13
    iput-wide p1, p0, Lcom/vivox/service/vx_resp_account_channel_search_t;->swigCPtr:J

    .line 14
    return-void
.end method

.method protected static getCPtr(Lcom/vivox/service/vx_resp_account_channel_search_t;)J
    .locals 2
    .param p0, "com_vivox_service_vx_resp_account_channel_search_t"    # Lcom/vivox/service/vx_resp_account_channel_search_t;

    .prologue
    .line 17
    if-nez p0, :cond_0

    const-wide/16 v0, 0x0

    :goto_0
    return-wide v0

    :cond_0
    iget-wide v0, p0, Lcom/vivox/service/vx_resp_account_channel_search_t;->swigCPtr:J

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
    iget-wide v0, p0, Lcom/vivox/service/vx_resp_account_channel_search_t;->swigCPtr:J

    cmp-long v0, v0, v2

    if-eqz v0, :cond_0

    iget-boolean v0, p0, Lcom/vivox/service/vx_resp_account_channel_search_t;->swigCMemOwn:Z

    if-nez v0, :cond_1

    .line 22
    :cond_0
    const-wide/16 v0, 0x0

    iput-wide v0, p0, Lcom/vivox/service/vx_resp_account_channel_search_t;->swigCPtr:J
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    .line 27
    monitor-exit p0

    return-void

    .line 24
    :cond_1
    const/4 v0, 0x0

    :try_start_1
    iput-boolean v0, p0, Lcom/vivox/service/vx_resp_account_channel_search_t;->swigCMemOwn:Z

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
    iget-wide v2, p0, Lcom/vivox/service/vx_resp_account_channel_search_t;->swigCPtr:J

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_resp_account_channel_search_t_base_get(JLcom/vivox/service/vx_resp_account_channel_search_t;)J

    move-result-wide v0

    .line 31
    .local v0, "vx_resp_account_channel_search_t_base_get":J
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

.method public getChannel_count()I
    .locals 2

    .prologue
    .line 35
    iget-wide v0, p0, Lcom/vivox/service/vx_resp_account_channel_search_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_resp_account_channel_search_t_channel_count_get(JLcom/vivox/service/vx_resp_account_channel_search_t;)I

    move-result v0

    return v0
.end method

.method public getChannels()Lcom/vivox/service/SWIGTYPE_p_p_vx_channel;
    .locals 4

    .prologue
    .line 39
    iget-wide v2, p0, Lcom/vivox/service/vx_resp_account_channel_search_t;->swigCPtr:J

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_resp_account_channel_search_t_channels_get(JLcom/vivox/service/vx_resp_account_channel_search_t;)J

    move-result-wide v0

    .line 40
    .local v0, "vx_resp_account_channel_search_t_channels_get":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/SWIGTYPE_p_p_vx_channel;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/SWIGTYPE_p_p_vx_channel;-><init>(JZ)V

    goto :goto_0
.end method

.method public getFrom()I
    .locals 2

    .prologue
    .line 44
    iget-wide v0, p0, Lcom/vivox/service/vx_resp_account_channel_search_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_resp_account_channel_search_t_from_get(JLcom/vivox/service/vx_resp_account_channel_search_t;)I

    move-result v0

    return v0
.end method

.method public getPage()I
    .locals 2

    .prologue
    .line 48
    iget-wide v0, p0, Lcom/vivox/service/vx_resp_account_channel_search_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_resp_account_channel_search_t_page_get(JLcom/vivox/service/vx_resp_account_channel_search_t;)I

    move-result v0

    return v0
.end method

.method public getTo()I
    .locals 2

    .prologue
    .line 52
    iget-wide v0, p0, Lcom/vivox/service/vx_resp_account_channel_search_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_resp_account_channel_search_t_to_get(JLcom/vivox/service/vx_resp_account_channel_search_t;)I

    move-result v0

    return v0
.end method

.method public setBase(Lcom/vivox/service/vx_resp_base_t;)V
    .locals 6
    .param p1, "com_vivox_service_vx_resp_base_t"    # Lcom/vivox/service/vx_resp_base_t;

    .prologue
    .line 56
    iget-wide v0, p0, Lcom/vivox/service/vx_resp_account_channel_search_t;->swigCPtr:J

    invoke-static {p1}, Lcom/vivox/service/vx_resp_base_t;->getCPtr(Lcom/vivox/service/vx_resp_base_t;)J

    move-result-wide v3

    move-object v2, p0

    move-object v5, p1

    invoke-static/range {v0 .. v5}, Lcom/vivox/service/VxClientProxyJNI;->vx_resp_account_channel_search_t_base_set(JLcom/vivox/service/vx_resp_account_channel_search_t;JLcom/vivox/service/vx_resp_base_t;)V

    .line 57
    return-void
.end method

.method public setChannel_count(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 60
    iget-wide v0, p0, Lcom/vivox/service/vx_resp_account_channel_search_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_resp_account_channel_search_t_channel_count_set(JLcom/vivox/service/vx_resp_account_channel_search_t;I)V

    .line 61
    return-void
.end method

.method public setChannels(Lcom/vivox/service/SWIGTYPE_p_p_vx_channel;)V
    .locals 4
    .param p1, "sWIGTYPE_p_p_vx_channel"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_channel;

    .prologue
    .line 64
    iget-wide v0, p0, Lcom/vivox/service/vx_resp_account_channel_search_t;->swigCPtr:J

    invoke-static {p1}, Lcom/vivox/service/SWIGTYPE_p_p_vx_channel;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_channel;)J

    move-result-wide v2

    invoke-static {v0, v1, p0, v2, v3}, Lcom/vivox/service/VxClientProxyJNI;->vx_resp_account_channel_search_t_channels_set(JLcom/vivox/service/vx_resp_account_channel_search_t;J)V

    .line 65
    return-void
.end method

.method public setFrom(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 68
    iget-wide v0, p0, Lcom/vivox/service/vx_resp_account_channel_search_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_resp_account_channel_search_t_from_set(JLcom/vivox/service/vx_resp_account_channel_search_t;I)V

    .line 69
    return-void
.end method

.method public setPage(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 72
    iget-wide v0, p0, Lcom/vivox/service/vx_resp_account_channel_search_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_resp_account_channel_search_t_page_set(JLcom/vivox/service/vx_resp_account_channel_search_t;I)V

    .line 73
    return-void
.end method

.method public setTo(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 76
    iget-wide v0, p0, Lcom/vivox/service/vx_resp_account_channel_search_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_resp_account_channel_search_t_to_set(JLcom/vivox/service/vx_resp_account_channel_search_t;I)V

    .line 77
    return-void
.end method
