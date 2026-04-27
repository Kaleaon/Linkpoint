.class public Lcom/vivox/service/vx_participant_t;
.super Ljava/lang/Object;
.source "vx_participant_t.java"


# instance fields
.field protected swigCMemOwn:Z

.field private swigCPtr:J


# direct methods
.method public constructor <init>()V
    .locals 3

    .prologue
    .line 8
    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->new_vx_participant_t()J

    move-result-wide v0

    const/4 v2, 0x1

    invoke-direct {p0, v0, v1, v2}, Lcom/vivox/service/vx_participant_t;-><init>(JZ)V

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
    iput-boolean p3, p0, Lcom/vivox/service/vx_participant_t;->swigCMemOwn:Z

    .line 13
    iput-wide p1, p0, Lcom/vivox/service/vx_participant_t;->swigCPtr:J

    .line 14
    return-void
.end method

.method protected static getCPtr(Lcom/vivox/service/vx_participant_t;)J
    .locals 2
    .param p0, "com_vivox_service_vx_participant_t"    # Lcom/vivox/service/vx_participant_t;

    .prologue
    .line 17
    if-nez p0, :cond_0

    const-wide/16 v0, 0x0

    :goto_0
    return-wide v0

    :cond_0
    iget-wide v0, p0, Lcom/vivox/service/vx_participant_t;->swigCPtr:J

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
    iget-wide v0, p0, Lcom/vivox/service/vx_participant_t;->swigCPtr:J

    cmp-long v0, v0, v2

    if-eqz v0, :cond_0

    iget-boolean v0, p0, Lcom/vivox/service/vx_participant_t;->swigCMemOwn:Z

    if-eqz v0, :cond_0

    .line 22
    const/4 v0, 0x0

    iput-boolean v0, p0, Lcom/vivox/service/vx_participant_t;->swigCMemOwn:Z

    .line 23
    iget-wide v0, p0, Lcom/vivox/service/vx_participant_t;->swigCPtr:J

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->delete_vx_participant_t(J)V

    .line 25
    :cond_0
    const-wide/16 v0, 0x0

    iput-wide v0, p0, Lcom/vivox/service/vx_participant_t;->swigCPtr:J
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
    invoke-virtual {p0}, Lcom/vivox/service/vx_participant_t;->delete()V

    .line 30
    return-void
.end method

.method public getAccount_id()I
    .locals 2

    .prologue
    .line 33
    iget-wide v0, p0, Lcom/vivox/service/vx_participant_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_participant_t_account_id_get(JLcom/vivox/service/vx_participant_t;)I

    move-result v0

    return v0
.end method

.method public getDisplay_name()Ljava/lang/String;
    .locals 2

    .prologue
    .line 37
    iget-wide v0, p0, Lcom/vivox/service/vx_participant_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_participant_t_display_name_get(JLcom/vivox/service/vx_participant_t;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public getFirst_name()Ljava/lang/String;
    .locals 2

    .prologue
    .line 41
    iget-wide v0, p0, Lcom/vivox/service/vx_participant_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_participant_t_first_name_get(JLcom/vivox/service/vx_participant_t;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public getIs_moderator()I
    .locals 2

    .prologue
    .line 45
    iget-wide v0, p0, Lcom/vivox/service/vx_participant_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_participant_t_is_moderator_get(JLcom/vivox/service/vx_participant_t;)I

    move-result v0

    return v0
.end method

.method public getIs_moderator_muted()I
    .locals 2

    .prologue
    .line 49
    iget-wide v0, p0, Lcom/vivox/service/vx_participant_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_participant_t_is_moderator_muted_get(JLcom/vivox/service/vx_participant_t;)I

    move-result v0

    return v0
.end method

.method public getIs_moderator_text_muted()I
    .locals 2

    .prologue
    .line 53
    iget-wide v0, p0, Lcom/vivox/service/vx_participant_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_participant_t_is_moderator_text_muted_get(JLcom/vivox/service/vx_participant_t;)I

    move-result v0

    return v0
.end method

.method public getIs_muted_for_me()I
    .locals 2

    .prologue
    .line 57
    iget-wide v0, p0, Lcom/vivox/service/vx_participant_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_participant_t_is_muted_for_me_get(JLcom/vivox/service/vx_participant_t;)I

    move-result v0

    return v0
.end method

.method public getIs_owner()I
    .locals 2

    .prologue
    .line 61
    iget-wide v0, p0, Lcom/vivox/service/vx_participant_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_participant_t_is_owner_get(JLcom/vivox/service/vx_participant_t;)I

    move-result v0

    return v0
.end method

.method public getLast_name()Ljava/lang/String;
    .locals 2

    .prologue
    .line 65
    iget-wide v0, p0, Lcom/vivox/service/vx_participant_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_participant_t_last_name_get(JLcom/vivox/service/vx_participant_t;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public getUri()Ljava/lang/String;
    .locals 2

    .prologue
    .line 69
    iget-wide v0, p0, Lcom/vivox/service/vx_participant_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_participant_t_uri_get(JLcom/vivox/service/vx_participant_t;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public getUsername()Ljava/lang/String;
    .locals 2

    .prologue
    .line 73
    iget-wide v0, p0, Lcom/vivox/service/vx_participant_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_participant_t_username_get(JLcom/vivox/service/vx_participant_t;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public setAccount_id(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 77
    iget-wide v0, p0, Lcom/vivox/service/vx_participant_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_participant_t_account_id_set(JLcom/vivox/service/vx_participant_t;I)V

    .line 78
    return-void
.end method

.method public setDisplay_name(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 81
    iget-wide v0, p0, Lcom/vivox/service/vx_participant_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_participant_t_display_name_set(JLcom/vivox/service/vx_participant_t;Ljava/lang/String;)V

    .line 82
    return-void
.end method

.method public setFirst_name(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 85
    iget-wide v0, p0, Lcom/vivox/service/vx_participant_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_participant_t_first_name_set(JLcom/vivox/service/vx_participant_t;Ljava/lang/String;)V

    .line 86
    return-void
.end method

.method public setIs_moderator(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 89
    iget-wide v0, p0, Lcom/vivox/service/vx_participant_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_participant_t_is_moderator_set(JLcom/vivox/service/vx_participant_t;I)V

    .line 90
    return-void
.end method

.method public setIs_moderator_muted(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 93
    iget-wide v0, p0, Lcom/vivox/service/vx_participant_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_participant_t_is_moderator_muted_set(JLcom/vivox/service/vx_participant_t;I)V

    .line 94
    return-void
.end method

.method public setIs_moderator_text_muted(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 97
    iget-wide v0, p0, Lcom/vivox/service/vx_participant_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_participant_t_is_moderator_text_muted_set(JLcom/vivox/service/vx_participant_t;I)V

    .line 98
    return-void
.end method

.method public setIs_muted_for_me(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 101
    iget-wide v0, p0, Lcom/vivox/service/vx_participant_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_participant_t_is_muted_for_me_set(JLcom/vivox/service/vx_participant_t;I)V

    .line 102
    return-void
.end method

.method public setIs_owner(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 105
    iget-wide v0, p0, Lcom/vivox/service/vx_participant_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_participant_t_is_owner_set(JLcom/vivox/service/vx_participant_t;I)V

    .line 106
    return-void
.end method

.method public setLast_name(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 109
    iget-wide v0, p0, Lcom/vivox/service/vx_participant_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_participant_t_last_name_set(JLcom/vivox/service/vx_participant_t;Ljava/lang/String;)V

    .line 110
    return-void
.end method

.method public setUri(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 113
    iget-wide v0, p0, Lcom/vivox/service/vx_participant_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_participant_t_uri_set(JLcom/vivox/service/vx_participant_t;Ljava/lang/String;)V

    .line 114
    return-void
.end method

.method public setUsername(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 117
    iget-wide v0, p0, Lcom/vivox/service/vx_participant_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_participant_t_username_set(JLcom/vivox/service/vx_participant_t;Ljava/lang/String;)V

    .line 118
    return-void
.end method
