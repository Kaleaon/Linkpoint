.class public Lcom/vivox/service/vx_state_account_t;
.super Ljava/lang/Object;
.source "vx_state_account_t.java"


# instance fields
.field protected swigCMemOwn:Z

.field private swigCPtr:J


# direct methods
.method public constructor <init>()V
    .locals 3

    .prologue
    .line 8
    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->new_vx_state_account_t()J

    move-result-wide v0

    const/4 v2, 0x1

    invoke-direct {p0, v0, v1, v2}, Lcom/vivox/service/vx_state_account_t;-><init>(JZ)V

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
    iput-boolean p3, p0, Lcom/vivox/service/vx_state_account_t;->swigCMemOwn:Z

    .line 13
    iput-wide p1, p0, Lcom/vivox/service/vx_state_account_t;->swigCPtr:J

    .line 14
    return-void
.end method

.method protected static getCPtr(Lcom/vivox/service/vx_state_account_t;)J
    .locals 2
    .param p0, "com_vivox_service_vx_state_account_t"    # Lcom/vivox/service/vx_state_account_t;

    .prologue
    .line 17
    if-nez p0, :cond_0

    const-wide/16 v0, 0x0

    :goto_0
    return-wide v0

    :cond_0
    iget-wide v0, p0, Lcom/vivox/service/vx_state_account_t;->swigCPtr:J

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
    iget-wide v0, p0, Lcom/vivox/service/vx_state_account_t;->swigCPtr:J

    cmp-long v0, v0, v2

    if-eqz v0, :cond_0

    iget-boolean v0, p0, Lcom/vivox/service/vx_state_account_t;->swigCMemOwn:Z

    if-nez v0, :cond_1

    .line 22
    :cond_0
    const-wide/16 v0, 0x0

    iput-wide v0, p0, Lcom/vivox/service/vx_state_account_t;->swigCPtr:J
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    .line 27
    monitor-exit p0

    return-void

    .line 24
    :cond_1
    const/4 v0, 0x0

    :try_start_1
    iput-boolean v0, p0, Lcom/vivox/service/vx_state_account_t;->swigCMemOwn:Z

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

.method public getAccount_handle()Ljava/lang/String;
    .locals 2

    .prologue
    .line 30
    iget-wide v0, p0, Lcom/vivox/service/vx_state_account_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_account_t_account_handle_get(JLcom/vivox/service/vx_state_account_t;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public getAccount_uri()Ljava/lang/String;
    .locals 2

    .prologue
    .line 34
    iget-wide v0, p0, Lcom/vivox/service/vx_state_account_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_account_t_account_uri_get(JLcom/vivox/service/vx_state_account_t;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public getDisplay_name()Ljava/lang/String;
    .locals 2

    .prologue
    .line 38
    iget-wide v0, p0, Lcom/vivox/service/vx_state_account_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_account_t_display_name_get(JLcom/vivox/service/vx_state_account_t;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public getIs_anonymous_login()I
    .locals 2

    .prologue
    .line 42
    iget-wide v0, p0, Lcom/vivox/service/vx_state_account_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_account_t_is_anonymous_login_get(JLcom/vivox/service/vx_state_account_t;)I

    move-result v0

    return v0
.end method

.method public getState()Lcom/vivox/service/vx_login_state_change_state;
    .locals 2

    .prologue
    .line 46
    iget-wide v0, p0, Lcom/vivox/service/vx_state_account_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_account_t_state_get(JLcom/vivox/service/vx_state_account_t;)I

    move-result v0

    invoke-static {v0}, Lcom/vivox/service/vx_login_state_change_state;->swigToEnum(I)Lcom/vivox/service/vx_login_state_change_state;

    move-result-object v0

    return-object v0
.end method

.method public getState_buddies()Lcom/vivox/service/SWIGTYPE_p_p_vx_state_buddy;
    .locals 4

    .prologue
    .line 50
    iget-wide v2, p0, Lcom/vivox/service/vx_state_account_t;->swigCPtr:J

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_account_t_state_buddies_get(JLcom/vivox/service/vx_state_account_t;)J

    move-result-wide v0

    .line 51
    .local v0, "vx_state_account_t_state_buddies_get":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/SWIGTYPE_p_p_vx_state_buddy;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/SWIGTYPE_p_p_vx_state_buddy;-><init>(JZ)V

    goto :goto_0
.end method

.method public getState_buddy_count()I
    .locals 2

    .prologue
    .line 55
    iget-wide v0, p0, Lcom/vivox/service/vx_state_account_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_account_t_state_buddy_count_get(JLcom/vivox/service/vx_state_account_t;)I

    move-result v0

    return v0
.end method

.method public getState_buddy_group_count()I
    .locals 2

    .prologue
    .line 59
    iget-wide v0, p0, Lcom/vivox/service/vx_state_account_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_account_t_state_buddy_group_count_get(JLcom/vivox/service/vx_state_account_t;)I

    move-result v0

    return v0
.end method

.method public getState_buddy_groups()Lcom/vivox/service/SWIGTYPE_p_p_vx_state_buddy_group;
    .locals 4

    .prologue
    .line 63
    iget-wide v2, p0, Lcom/vivox/service/vx_state_account_t;->swigCPtr:J

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_account_t_state_buddy_groups_get(JLcom/vivox/service/vx_state_account_t;)J

    move-result-wide v0

    .line 64
    .local v0, "vx_state_account_t_state_buddy_groups_get":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/SWIGTYPE_p_p_vx_state_buddy_group;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/SWIGTYPE_p_p_vx_state_buddy_group;-><init>(JZ)V

    goto :goto_0
.end method

.method public getState_sessiongroups()Lcom/vivox/service/SWIGTYPE_p_p_vx_state_sessiongroup;
    .locals 4

    .prologue
    .line 68
    iget-wide v2, p0, Lcom/vivox/service/vx_state_account_t;->swigCPtr:J

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_account_t_state_sessiongroups_get(JLcom/vivox/service/vx_state_account_t;)J

    move-result-wide v0

    .line 69
    .local v0, "vx_state_account_t_state_sessiongroups_get":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/SWIGTYPE_p_p_vx_state_sessiongroup;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/SWIGTYPE_p_p_vx_state_sessiongroup;-><init>(JZ)V

    goto :goto_0
.end method

.method public getState_sessiongroups_count()I
    .locals 2

    .prologue
    .line 73
    iget-wide v0, p0, Lcom/vivox/service/vx_state_account_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_account_t_state_sessiongroups_count_get(JLcom/vivox/service/vx_state_account_t;)I

    move-result v0

    return v0
.end method

.method public setAccount_handle(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 77
    iget-wide v0, p0, Lcom/vivox/service/vx_state_account_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_account_t_account_handle_set(JLcom/vivox/service/vx_state_account_t;Ljava/lang/String;)V

    .line 78
    return-void
.end method

.method public setAccount_uri(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 81
    iget-wide v0, p0, Lcom/vivox/service/vx_state_account_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_account_t_account_uri_set(JLcom/vivox/service/vx_state_account_t;Ljava/lang/String;)V

    .line 82
    return-void
.end method

.method public setDisplay_name(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 85
    iget-wide v0, p0, Lcom/vivox/service/vx_state_account_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_account_t_display_name_set(JLcom/vivox/service/vx_state_account_t;Ljava/lang/String;)V

    .line 86
    return-void
.end method

.method public setIs_anonymous_login(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 89
    iget-wide v0, p0, Lcom/vivox/service/vx_state_account_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_account_t_is_anonymous_login_set(JLcom/vivox/service/vx_state_account_t;I)V

    .line 90
    return-void
.end method

.method public setState(Lcom/vivox/service/vx_login_state_change_state;)V
    .locals 3
    .param p1, "com_vivox_service_vx_login_state_change_state"    # Lcom/vivox/service/vx_login_state_change_state;

    .prologue
    .line 93
    iget-wide v0, p0, Lcom/vivox/service/vx_state_account_t;->swigCPtr:J

    invoke-virtual {p1}, Lcom/vivox/service/vx_login_state_change_state;->swigValue()I

    move-result v2

    invoke-static {v0, v1, p0, v2}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_account_t_state_set(JLcom/vivox/service/vx_state_account_t;I)V

    .line 94
    return-void
.end method

.method public setState_buddies(Lcom/vivox/service/SWIGTYPE_p_p_vx_state_buddy;)V
    .locals 4
    .param p1, "sWIGTYPE_p_p_vx_state_buddy"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_state_buddy;

    .prologue
    .line 97
    iget-wide v0, p0, Lcom/vivox/service/vx_state_account_t;->swigCPtr:J

    invoke-static {p1}, Lcom/vivox/service/SWIGTYPE_p_p_vx_state_buddy;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_state_buddy;)J

    move-result-wide v2

    invoke-static {v0, v1, p0, v2, v3}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_account_t_state_buddies_set(JLcom/vivox/service/vx_state_account_t;J)V

    .line 98
    return-void
.end method

.method public setState_buddy_count(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 101
    iget-wide v0, p0, Lcom/vivox/service/vx_state_account_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_account_t_state_buddy_count_set(JLcom/vivox/service/vx_state_account_t;I)V

    .line 102
    return-void
.end method

.method public setState_buddy_group_count(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 105
    iget-wide v0, p0, Lcom/vivox/service/vx_state_account_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_account_t_state_buddy_group_count_set(JLcom/vivox/service/vx_state_account_t;I)V

    .line 106
    return-void
.end method

.method public setState_buddy_groups(Lcom/vivox/service/SWIGTYPE_p_p_vx_state_buddy_group;)V
    .locals 4
    .param p1, "sWIGTYPE_p_p_vx_state_buddy_group"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_state_buddy_group;

    .prologue
    .line 109
    iget-wide v0, p0, Lcom/vivox/service/vx_state_account_t;->swigCPtr:J

    invoke-static {p1}, Lcom/vivox/service/SWIGTYPE_p_p_vx_state_buddy_group;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_state_buddy_group;)J

    move-result-wide v2

    invoke-static {v0, v1, p0, v2, v3}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_account_t_state_buddy_groups_set(JLcom/vivox/service/vx_state_account_t;J)V

    .line 110
    return-void
.end method

.method public setState_sessiongroups(Lcom/vivox/service/SWIGTYPE_p_p_vx_state_sessiongroup;)V
    .locals 4
    .param p1, "sWIGTYPE_p_p_vx_state_sessiongroup"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_state_sessiongroup;

    .prologue
    .line 113
    iget-wide v0, p0, Lcom/vivox/service/vx_state_account_t;->swigCPtr:J

    invoke-static {p1}, Lcom/vivox/service/SWIGTYPE_p_p_vx_state_sessiongroup;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_state_sessiongroup;)J

    move-result-wide v2

    invoke-static {v0, v1, p0, v2, v3}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_account_t_state_sessiongroups_set(JLcom/vivox/service/vx_state_account_t;J)V

    .line 114
    return-void
.end method

.method public setState_sessiongroups_count(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 117
    iget-wide v0, p0, Lcom/vivox/service/vx_state_account_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_account_t_state_sessiongroups_count_set(JLcom/vivox/service/vx_state_account_t;I)V

    .line 118
    return-void
.end method
