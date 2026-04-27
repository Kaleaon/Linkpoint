.class public Lcom/vivox/service/vx_channel_favorite_t;
.super Ljava/lang/Object;
.source "vx_channel_favorite_t.java"


# instance fields
.field protected swigCMemOwn:Z

.field private swigCPtr:J


# direct methods
.method public constructor <init>()V
    .locals 3

    .prologue
    .line 8
    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->new_vx_channel_favorite_t()J

    move-result-wide v0

    const/4 v2, 0x1

    invoke-direct {p0, v0, v1, v2}, Lcom/vivox/service/vx_channel_favorite_t;-><init>(JZ)V

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
    iput-boolean p3, p0, Lcom/vivox/service/vx_channel_favorite_t;->swigCMemOwn:Z

    .line 13
    iput-wide p1, p0, Lcom/vivox/service/vx_channel_favorite_t;->swigCPtr:J

    .line 14
    return-void
.end method

.method protected static getCPtr(Lcom/vivox/service/vx_channel_favorite_t;)J
    .locals 2
    .param p0, "com_vivox_service_vx_channel_favorite_t"    # Lcom/vivox/service/vx_channel_favorite_t;

    .prologue
    .line 17
    if-nez p0, :cond_0

    const-wide/16 v0, 0x0

    :goto_0
    return-wide v0

    :cond_0
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_favorite_t;->swigCPtr:J

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
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_favorite_t;->swigCPtr:J

    cmp-long v0, v0, v2

    if-eqz v0, :cond_0

    iget-boolean v0, p0, Lcom/vivox/service/vx_channel_favorite_t;->swigCMemOwn:Z

    if-eqz v0, :cond_0

    .line 22
    const/4 v0, 0x0

    iput-boolean v0, p0, Lcom/vivox/service/vx_channel_favorite_t;->swigCMemOwn:Z

    .line 23
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_favorite_t;->swigCPtr:J

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->delete_vx_channel_favorite_t(J)V

    .line 25
    :cond_0
    const-wide/16 v0, 0x0

    iput-wide v0, p0, Lcom/vivox/service/vx_channel_favorite_t;->swigCPtr:J
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
    invoke-virtual {p0}, Lcom/vivox/service/vx_channel_favorite_t;->delete()V

    .line 30
    return-void
.end method

.method public getChannel_active_participants()I
    .locals 2

    .prologue
    .line 33
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_favorite_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_favorite_t_channel_active_participants_get(JLcom/vivox/service/vx_channel_favorite_t;)I

    move-result v0

    return v0
.end method

.method public getChannel_capacity()I
    .locals 2

    .prologue
    .line 37
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_favorite_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_favorite_t_channel_capacity_get(JLcom/vivox/service/vx_channel_favorite_t;)I

    move-result v0

    return v0
.end method

.method public getChannel_description()Ljava/lang/String;
    .locals 2

    .prologue
    .line 41
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_favorite_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_favorite_t_channel_description_get(JLcom/vivox/service/vx_channel_favorite_t;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public getChannel_is_persistent()I
    .locals 2

    .prologue
    .line 45
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_favorite_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_favorite_t_channel_is_persistent_get(JLcom/vivox/service/vx_channel_favorite_t;)I

    move-result v0

    return v0
.end method

.method public getChannel_is_protected()I
    .locals 2

    .prologue
    .line 49
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_favorite_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_favorite_t_channel_is_protected_get(JLcom/vivox/service/vx_channel_favorite_t;)I

    move-result v0

    return v0
.end method

.method public getChannel_limit()I
    .locals 2

    .prologue
    .line 53
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_favorite_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_favorite_t_channel_limit_get(JLcom/vivox/service/vx_channel_favorite_t;)I

    move-result v0

    return v0
.end method

.method public getChannel_modified()Ljava/lang/String;
    .locals 2

    .prologue
    .line 57
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_favorite_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_favorite_t_channel_modified_get(JLcom/vivox/service/vx_channel_favorite_t;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public getChannel_owner()Ljava/lang/String;
    .locals 2

    .prologue
    .line 61
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_favorite_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_favorite_t_channel_owner_get(JLcom/vivox/service/vx_channel_favorite_t;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public getChannel_owner_display_name()Ljava/lang/String;
    .locals 2

    .prologue
    .line 65
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_favorite_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_favorite_t_channel_owner_display_name_get(JLcom/vivox/service/vx_channel_favorite_t;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public getChannel_owner_user_name()Ljava/lang/String;
    .locals 2

    .prologue
    .line 69
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_favorite_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_favorite_t_channel_owner_user_name_get(JLcom/vivox/service/vx_channel_favorite_t;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public getChannel_size()I
    .locals 2

    .prologue
    .line 73
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_favorite_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_favorite_t_channel_size_get(JLcom/vivox/service/vx_channel_favorite_t;)I

    move-result v0

    return v0
.end method

.method public getChannel_uri()Ljava/lang/String;
    .locals 2

    .prologue
    .line 77
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_favorite_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_favorite_t_channel_uri_get(JLcom/vivox/service/vx_channel_favorite_t;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public getFavorite_data()Ljava/lang/String;
    .locals 2

    .prologue
    .line 81
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_favorite_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_favorite_t_favorite_data_get(JLcom/vivox/service/vx_channel_favorite_t;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public getFavorite_display_name()Ljava/lang/String;
    .locals 2

    .prologue
    .line 85
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_favorite_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_favorite_t_favorite_display_name_get(JLcom/vivox/service/vx_channel_favorite_t;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public getFavorite_group_id()I
    .locals 2

    .prologue
    .line 89
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_favorite_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_favorite_t_favorite_group_id_get(JLcom/vivox/service/vx_channel_favorite_t;)I

    move-result v0

    return v0
.end method

.method public getFavorite_id()I
    .locals 2

    .prologue
    .line 93
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_favorite_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_favorite_t_favorite_id_get(JLcom/vivox/service/vx_channel_favorite_t;)I

    move-result v0

    return v0
.end method

.method public setChannel_active_participants(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 97
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_favorite_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_favorite_t_channel_active_participants_set(JLcom/vivox/service/vx_channel_favorite_t;I)V

    .line 98
    return-void
.end method

.method public setChannel_capacity(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 101
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_favorite_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_favorite_t_channel_capacity_set(JLcom/vivox/service/vx_channel_favorite_t;I)V

    .line 102
    return-void
.end method

.method public setChannel_description(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 105
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_favorite_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_favorite_t_channel_description_set(JLcom/vivox/service/vx_channel_favorite_t;Ljava/lang/String;)V

    .line 106
    return-void
.end method

.method public setChannel_is_persistent(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 109
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_favorite_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_favorite_t_channel_is_persistent_set(JLcom/vivox/service/vx_channel_favorite_t;I)V

    .line 110
    return-void
.end method

.method public setChannel_is_protected(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 113
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_favorite_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_favorite_t_channel_is_protected_set(JLcom/vivox/service/vx_channel_favorite_t;I)V

    .line 114
    return-void
.end method

.method public setChannel_limit(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 117
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_favorite_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_favorite_t_channel_limit_set(JLcom/vivox/service/vx_channel_favorite_t;I)V

    .line 118
    return-void
.end method

.method public setChannel_modified(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 121
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_favorite_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_favorite_t_channel_modified_set(JLcom/vivox/service/vx_channel_favorite_t;Ljava/lang/String;)V

    .line 122
    return-void
.end method

.method public setChannel_owner(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 125
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_favorite_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_favorite_t_channel_owner_set(JLcom/vivox/service/vx_channel_favorite_t;Ljava/lang/String;)V

    .line 126
    return-void
.end method

.method public setChannel_owner_display_name(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 129
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_favorite_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_favorite_t_channel_owner_display_name_set(JLcom/vivox/service/vx_channel_favorite_t;Ljava/lang/String;)V

    .line 130
    return-void
.end method

.method public setChannel_owner_user_name(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 133
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_favorite_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_favorite_t_channel_owner_user_name_set(JLcom/vivox/service/vx_channel_favorite_t;Ljava/lang/String;)V

    .line 134
    return-void
.end method

.method public setChannel_size(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 137
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_favorite_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_favorite_t_channel_size_set(JLcom/vivox/service/vx_channel_favorite_t;I)V

    .line 138
    return-void
.end method

.method public setChannel_uri(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 141
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_favorite_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_favorite_t_channel_uri_set(JLcom/vivox/service/vx_channel_favorite_t;Ljava/lang/String;)V

    .line 142
    return-void
.end method

.method public setFavorite_data(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 145
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_favorite_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_favorite_t_favorite_data_set(JLcom/vivox/service/vx_channel_favorite_t;Ljava/lang/String;)V

    .line 146
    return-void
.end method

.method public setFavorite_display_name(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 149
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_favorite_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_favorite_t_favorite_display_name_set(JLcom/vivox/service/vx_channel_favorite_t;Ljava/lang/String;)V

    .line 150
    return-void
.end method

.method public setFavorite_group_id(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 153
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_favorite_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_favorite_t_favorite_group_id_set(JLcom/vivox/service/vx_channel_favorite_t;I)V

    .line 154
    return-void
.end method

.method public setFavorite_id(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 157
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_favorite_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_favorite_t_favorite_id_set(JLcom/vivox/service/vx_channel_favorite_t;I)V

    .line 158
    return-void
.end method
