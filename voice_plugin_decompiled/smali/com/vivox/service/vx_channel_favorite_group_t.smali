.class public Lcom/vivox/service/vx_channel_favorite_group_t;
.super Ljava/lang/Object;
.source "vx_channel_favorite_group_t.java"


# instance fields
.field protected swigCMemOwn:Z

.field private swigCPtr:J


# direct methods
.method public constructor <init>()V
    .locals 3

    .prologue
    .line 8
    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->new_vx_channel_favorite_group_t()J

    move-result-wide v0

    const/4 v2, 0x1

    invoke-direct {p0, v0, v1, v2}, Lcom/vivox/service/vx_channel_favorite_group_t;-><init>(JZ)V

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
    iput-boolean p3, p0, Lcom/vivox/service/vx_channel_favorite_group_t;->swigCMemOwn:Z

    .line 13
    iput-wide p1, p0, Lcom/vivox/service/vx_channel_favorite_group_t;->swigCPtr:J

    .line 14
    return-void
.end method

.method protected static getCPtr(Lcom/vivox/service/vx_channel_favorite_group_t;)J
    .locals 2
    .param p0, "com_vivox_service_vx_channel_favorite_group_t"    # Lcom/vivox/service/vx_channel_favorite_group_t;

    .prologue
    .line 17
    if-nez p0, :cond_0

    const-wide/16 v0, 0x0

    :goto_0
    return-wide v0

    :cond_0
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_favorite_group_t;->swigCPtr:J

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
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_favorite_group_t;->swigCPtr:J

    cmp-long v0, v0, v2

    if-eqz v0, :cond_0

    iget-boolean v0, p0, Lcom/vivox/service/vx_channel_favorite_group_t;->swigCMemOwn:Z

    if-eqz v0, :cond_0

    .line 22
    const/4 v0, 0x0

    iput-boolean v0, p0, Lcom/vivox/service/vx_channel_favorite_group_t;->swigCMemOwn:Z

    .line 23
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_favorite_group_t;->swigCPtr:J

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->delete_vx_channel_favorite_group_t(J)V

    .line 25
    :cond_0
    const-wide/16 v0, 0x0

    iput-wide v0, p0, Lcom/vivox/service/vx_channel_favorite_group_t;->swigCPtr:J
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
    invoke-virtual {p0}, Lcom/vivox/service/vx_channel_favorite_group_t;->delete()V

    .line 30
    return-void
.end method

.method public getFavorite_group_data()Ljava/lang/String;
    .locals 2

    .prologue
    .line 33
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_favorite_group_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_favorite_group_t_favorite_group_data_get(JLcom/vivox/service/vx_channel_favorite_group_t;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public getFavorite_group_id()I
    .locals 2

    .prologue
    .line 37
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_favorite_group_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_favorite_group_t_favorite_group_id_get(JLcom/vivox/service/vx_channel_favorite_group_t;)I

    move-result v0

    return v0
.end method

.method public getFavorite_group_modified()Ljava/lang/String;
    .locals 2

    .prologue
    .line 41
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_favorite_group_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_favorite_group_t_favorite_group_modified_get(JLcom/vivox/service/vx_channel_favorite_group_t;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public getFavorite_group_name()Ljava/lang/String;
    .locals 2

    .prologue
    .line 45
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_favorite_group_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_favorite_group_t_favorite_group_name_get(JLcom/vivox/service/vx_channel_favorite_group_t;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public setFavorite_group_data(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 49
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_favorite_group_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_favorite_group_t_favorite_group_data_set(JLcom/vivox/service/vx_channel_favorite_group_t;Ljava/lang/String;)V

    .line 50
    return-void
.end method

.method public setFavorite_group_id(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 53
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_favorite_group_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_favorite_group_t_favorite_group_id_set(JLcom/vivox/service/vx_channel_favorite_group_t;I)V

    .line 54
    return-void
.end method

.method public setFavorite_group_modified(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 57
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_favorite_group_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_favorite_group_t_favorite_group_modified_set(JLcom/vivox/service/vx_channel_favorite_group_t;Ljava/lang/String;)V

    .line 58
    return-void
.end method

.method public setFavorite_group_name(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 61
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_favorite_group_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_favorite_group_t_favorite_group_name_set(JLcom/vivox/service/vx_channel_favorite_group_t;Ljava/lang/String;)V

    .line 62
    return-void
.end method
