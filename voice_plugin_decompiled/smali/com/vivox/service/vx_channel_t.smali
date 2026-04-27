.class public Lcom/vivox/service/vx_channel_t;
.super Ljava/lang/Object;
.source "vx_channel_t.java"


# instance fields
.field protected swigCMemOwn:Z

.field private swigCPtr:J


# direct methods
.method public constructor <init>()V
    .locals 3

    .prologue
    .line 8
    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->new_vx_channel_t()J

    move-result-wide v0

    const/4 v2, 0x1

    invoke-direct {p0, v0, v1, v2}, Lcom/vivox/service/vx_channel_t;-><init>(JZ)V

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
    iput-boolean p3, p0, Lcom/vivox/service/vx_channel_t;->swigCMemOwn:Z

    .line 13
    iput-wide p1, p0, Lcom/vivox/service/vx_channel_t;->swigCPtr:J

    .line 14
    return-void
.end method

.method protected static getCPtr(Lcom/vivox/service/vx_channel_t;)J
    .locals 2
    .param p0, "com_vivox_service_vx_channel_t"    # Lcom/vivox/service/vx_channel_t;

    .prologue
    .line 17
    if-nez p0, :cond_0

    const-wide/16 v0, 0x0

    :goto_0
    return-wide v0

    :cond_0
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_t;->swigCPtr:J

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
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_t;->swigCPtr:J

    cmp-long v0, v0, v2

    if-eqz v0, :cond_0

    iget-boolean v0, p0, Lcom/vivox/service/vx_channel_t;->swigCMemOwn:Z

    if-eqz v0, :cond_0

    .line 22
    const/4 v0, 0x0

    iput-boolean v0, p0, Lcom/vivox/service/vx_channel_t;->swigCMemOwn:Z

    .line 23
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_t;->swigCPtr:J

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->delete_vx_channel_t(J)V

    .line 25
    :cond_0
    const-wide/16 v0, 0x0

    iput-wide v0, p0, Lcom/vivox/service/vx_channel_t;->swigCPtr:J
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
    invoke-virtual {p0}, Lcom/vivox/service/vx_channel_t;->delete()V

    .line 30
    return-void
.end method

.method public getActive_participants()I
    .locals 2

    .prologue
    .line 33
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_t_active_participants_get(JLcom/vivox/service/vx_channel_t;)I

    move-result v0

    return v0
.end method

.method public getCapacity()I
    .locals 2

    .prologue
    .line 37
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_t_capacity_get(JLcom/vivox/service/vx_channel_t;)I

    move-result v0

    return v0
.end method

.method public getChannel_desc()Ljava/lang/String;
    .locals 2

    .prologue
    .line 41
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_t_channel_desc_get(JLcom/vivox/service/vx_channel_t;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public getChannel_id()I
    .locals 2

    .prologue
    .line 45
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_t_channel_id_get(JLcom/vivox/service/vx_channel_t;)I

    move-result v0

    return v0
.end method

.method public getChannel_name()Ljava/lang/String;
    .locals 2

    .prologue
    .line 49
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_t_channel_name_get(JLcom/vivox/service/vx_channel_t;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public getChannel_uri()Ljava/lang/String;
    .locals 2

    .prologue
    .line 53
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_t_channel_uri_get(JLcom/vivox/service/vx_channel_t;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public getClamping_dist()I
    .locals 2

    .prologue
    .line 57
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_t_clamping_dist_get(JLcom/vivox/service/vx_channel_t;)I

    move-result v0

    return v0
.end method

.method public getDist_model()I
    .locals 2

    .prologue
    .line 61
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_t_dist_model_get(JLcom/vivox/service/vx_channel_t;)I

    move-result v0

    return v0
.end method

.method public getEncrypt_audio()I
    .locals 2

    .prologue
    .line 65
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_t_encrypt_audio_get(JLcom/vivox/service/vx_channel_t;)I

    move-result v0

    return v0
.end method

.method public getHost()Ljava/lang/String;
    .locals 2

    .prologue
    .line 69
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_t_host_get(JLcom/vivox/service/vx_channel_t;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public getIs_persistent()I
    .locals 2

    .prologue
    .line 73
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_t_is_persistent_get(JLcom/vivox/service/vx_channel_t;)I

    move-result v0

    return v0
.end method

.method public getIs_protected()I
    .locals 2

    .prologue
    .line 77
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_t_is_protected_get(JLcom/vivox/service/vx_channel_t;)I

    move-result v0

    return v0
.end method

.method public getLimit()I
    .locals 2

    .prologue
    .line 81
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_t_limit_get(JLcom/vivox/service/vx_channel_t;)I

    move-result v0

    return v0
.end method

.method public getMax_gain()D
    .locals 2

    .prologue
    .line 85
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_t_max_gain_get(JLcom/vivox/service/vx_channel_t;)D

    move-result-wide v0

    return-wide v0
.end method

.method public getMax_range()I
    .locals 2

    .prologue
    .line 89
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_t_max_range_get(JLcom/vivox/service/vx_channel_t;)I

    move-result v0

    return v0
.end method

.method public getMode()Lcom/vivox/service/vx_channel_mode;
    .locals 2

    .prologue
    .line 93
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_t_mode_get(JLcom/vivox/service/vx_channel_t;)I

    move-result v0

    invoke-static {v0}, Lcom/vivox/service/vx_channel_mode;->swigToEnum(I)Lcom/vivox/service/vx_channel_mode;

    move-result-object v0

    return-object v0
.end method

.method public getModified()Ljava/lang/String;
    .locals 2

    .prologue
    .line 97
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_t_modified_get(JLcom/vivox/service/vx_channel_t;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public getOwner()Ljava/lang/String;
    .locals 2

    .prologue
    .line 101
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_t_owner_get(JLcom/vivox/service/vx_channel_t;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public getOwner_display_name()Ljava/lang/String;
    .locals 2

    .prologue
    .line 105
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_t_owner_display_name_get(JLcom/vivox/service/vx_channel_t;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public getOwner_user_name()Ljava/lang/String;
    .locals 2

    .prologue
    .line 109
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_t_owner_user_name_get(JLcom/vivox/service/vx_channel_t;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public getRoll_off()D
    .locals 2

    .prologue
    .line 113
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_t_roll_off_get(JLcom/vivox/service/vx_channel_t;)D

    move-result-wide v0

    return-wide v0
.end method

.method public getSize()I
    .locals 2

    .prologue
    .line 117
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_t_size_get(JLcom/vivox/service/vx_channel_t;)I

    move-result v0

    return v0
.end method

.method public getType()I
    .locals 2

    .prologue
    .line 121
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_t_type_get(JLcom/vivox/service/vx_channel_t;)I

    move-result v0

    return v0
.end method

.method public setActive_participants(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 125
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_t_active_participants_set(JLcom/vivox/service/vx_channel_t;I)V

    .line 126
    return-void
.end method

.method public setCapacity(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 129
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_t_capacity_set(JLcom/vivox/service/vx_channel_t;I)V

    .line 130
    return-void
.end method

.method public setChannel_desc(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 133
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_t_channel_desc_set(JLcom/vivox/service/vx_channel_t;Ljava/lang/String;)V

    .line 134
    return-void
.end method

.method public setChannel_id(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 137
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_t_channel_id_set(JLcom/vivox/service/vx_channel_t;I)V

    .line 138
    return-void
.end method

.method public setChannel_name(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 141
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_t_channel_name_set(JLcom/vivox/service/vx_channel_t;Ljava/lang/String;)V

    .line 142
    return-void
.end method

.method public setChannel_uri(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 145
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_t_channel_uri_set(JLcom/vivox/service/vx_channel_t;Ljava/lang/String;)V

    .line 146
    return-void
.end method

.method public setClamping_dist(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 149
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_t_clamping_dist_set(JLcom/vivox/service/vx_channel_t;I)V

    .line 150
    return-void
.end method

.method public setDist_model(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 153
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_t_dist_model_set(JLcom/vivox/service/vx_channel_t;I)V

    .line 154
    return-void
.end method

.method public setEncrypt_audio(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 157
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_t_encrypt_audio_set(JLcom/vivox/service/vx_channel_t;I)V

    .line 158
    return-void
.end method

.method public setHost(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 161
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_t_host_set(JLcom/vivox/service/vx_channel_t;Ljava/lang/String;)V

    .line 162
    return-void
.end method

.method public setIs_persistent(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 165
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_t_is_persistent_set(JLcom/vivox/service/vx_channel_t;I)V

    .line 166
    return-void
.end method

.method public setIs_protected(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 169
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_t_is_protected_set(JLcom/vivox/service/vx_channel_t;I)V

    .line 170
    return-void
.end method

.method public setLimit(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 173
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_t_limit_set(JLcom/vivox/service/vx_channel_t;I)V

    .line 174
    return-void
.end method

.method public setMax_gain(D)V
    .locals 3
    .param p1, "d"    # D

    .prologue
    .line 177
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1, p2}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_t_max_gain_set(JLcom/vivox/service/vx_channel_t;D)V

    .line 178
    return-void
.end method

.method public setMax_range(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 181
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_t_max_range_set(JLcom/vivox/service/vx_channel_t;I)V

    .line 182
    return-void
.end method

.method public setMode(Lcom/vivox/service/vx_channel_mode;)V
    .locals 3
    .param p1, "com_vivox_service_vx_channel_mode"    # Lcom/vivox/service/vx_channel_mode;

    .prologue
    .line 185
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_t;->swigCPtr:J

    invoke-virtual {p1}, Lcom/vivox/service/vx_channel_mode;->swigValue()I

    move-result v2

    invoke-static {v0, v1, p0, v2}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_t_mode_set(JLcom/vivox/service/vx_channel_t;I)V

    .line 186
    return-void
.end method

.method public setModified(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 189
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_t_modified_set(JLcom/vivox/service/vx_channel_t;Ljava/lang/String;)V

    .line 190
    return-void
.end method

.method public setOwner(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 193
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_t_owner_set(JLcom/vivox/service/vx_channel_t;Ljava/lang/String;)V

    .line 194
    return-void
.end method

.method public setOwner_display_name(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 197
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_t_owner_display_name_set(JLcom/vivox/service/vx_channel_t;Ljava/lang/String;)V

    .line 198
    return-void
.end method

.method public setOwner_user_name(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 201
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_t_owner_user_name_set(JLcom/vivox/service/vx_channel_t;Ljava/lang/String;)V

    .line 202
    return-void
.end method

.method public setRoll_off(D)V
    .locals 3
    .param p1, "d"    # D

    .prologue
    .line 205
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1, p2}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_t_roll_off_set(JLcom/vivox/service/vx_channel_t;D)V

    .line 206
    return-void
.end method

.method public setSize(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 209
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_t_size_set(JLcom/vivox/service/vx_channel_t;I)V

    .line 210
    return-void
.end method

.method public setType(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 213
    iget-wide v0, p0, Lcom/vivox/service/vx_channel_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_t_type_set(JLcom/vivox/service/vx_channel_t;I)V

    .line 214
    return-void
.end method
