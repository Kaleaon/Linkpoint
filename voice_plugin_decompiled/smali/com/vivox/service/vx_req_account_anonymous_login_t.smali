.class public Lcom/vivox/service/vx_req_account_anonymous_login_t;
.super Ljava/lang/Object;
.source "vx_req_account_anonymous_login_t.java"


# instance fields
.field protected swigCMemOwn:Z

.field private swigCPtr:J


# direct methods
.method public constructor <init>()V
    .locals 3

    .prologue
    .line 8
    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->new_vx_req_account_anonymous_login_t()J

    move-result-wide v0

    const/4 v2, 0x1

    invoke-direct {p0, v0, v1, v2}, Lcom/vivox/service/vx_req_account_anonymous_login_t;-><init>(JZ)V

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
    iput-boolean p3, p0, Lcom/vivox/service/vx_req_account_anonymous_login_t;->swigCMemOwn:Z

    .line 13
    iput-wide p1, p0, Lcom/vivox/service/vx_req_account_anonymous_login_t;->swigCPtr:J

    .line 14
    return-void
.end method

.method protected static getCPtr(Lcom/vivox/service/vx_req_account_anonymous_login_t;)J
    .locals 2
    .param p0, "com_vivox_service_vx_req_account_anonymous_login_t"    # Lcom/vivox/service/vx_req_account_anonymous_login_t;

    .prologue
    .line 17
    if-nez p0, :cond_0

    const-wide/16 v0, 0x0

    :goto_0
    return-wide v0

    :cond_0
    iget-wide v0, p0, Lcom/vivox/service/vx_req_account_anonymous_login_t;->swigCPtr:J

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
    iget-wide v0, p0, Lcom/vivox/service/vx_req_account_anonymous_login_t;->swigCPtr:J

    cmp-long v0, v0, v2

    if-eqz v0, :cond_0

    iget-boolean v0, p0, Lcom/vivox/service/vx_req_account_anonymous_login_t;->swigCMemOwn:Z

    if-nez v0, :cond_1

    .line 22
    :cond_0
    const-wide/16 v0, 0x0

    iput-wide v0, p0, Lcom/vivox/service/vx_req_account_anonymous_login_t;->swigCPtr:J
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    .line 27
    monitor-exit p0

    return-void

    .line 24
    :cond_1
    const/4 v0, 0x0

    :try_start_1
    iput-boolean v0, p0, Lcom/vivox/service/vx_req_account_anonymous_login_t;->swigCMemOwn:Z

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

.method public getAcct_mgmt_server()Ljava/lang/String;
    .locals 2

    .prologue
    .line 30
    iget-wide v0, p0, Lcom/vivox/service/vx_req_account_anonymous_login_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_account_anonymous_login_t_acct_mgmt_server_get(JLcom/vivox/service/vx_req_account_anonymous_login_t;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public getApplication_override()Ljava/lang/String;
    .locals 2

    .prologue
    .line 34
    iget-wide v0, p0, Lcom/vivox/service/vx_req_account_anonymous_login_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_account_anonymous_login_t_application_override_get(JLcom/vivox/service/vx_req_account_anonymous_login_t;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public getApplication_token()Ljava/lang/String;
    .locals 2

    .prologue
    .line 38
    iget-wide v0, p0, Lcom/vivox/service/vx_req_account_anonymous_login_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_account_anonymous_login_t_application_token_get(JLcom/vivox/service/vx_req_account_anonymous_login_t;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public getAutopost_crash_dumps()I
    .locals 2

    .prologue
    .line 42
    iget-wide v0, p0, Lcom/vivox/service/vx_req_account_anonymous_login_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_account_anonymous_login_t_autopost_crash_dumps_get(JLcom/vivox/service/vx_req_account_anonymous_login_t;)I

    move-result v0

    return v0
.end method

.method public getBase()Lcom/vivox/service/vx_req_base_t;
    .locals 4

    .prologue
    .line 46
    iget-wide v2, p0, Lcom/vivox/service/vx_req_account_anonymous_login_t;->swigCPtr:J

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_account_anonymous_login_t_base_get(JLcom/vivox/service/vx_req_account_anonymous_login_t;)J

    move-result-wide v0

    .line 47
    .local v0, "vx_req_account_anonymous_login_t_base_get":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_base_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_base_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public getBuddy_management_mode()Lcom/vivox/service/vx_buddy_management_mode;
    .locals 2

    .prologue
    .line 51
    iget-wide v0, p0, Lcom/vivox/service/vx_req_account_anonymous_login_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_account_anonymous_login_t_buddy_management_mode_get(JLcom/vivox/service/vx_req_account_anonymous_login_t;)I

    move-result v0

    invoke-static {v0}, Lcom/vivox/service/vx_buddy_management_mode;->swigToEnum(I)Lcom/vivox/service/vx_buddy_management_mode;

    move-result-object v0

    return-object v0
.end method

.method public getConnector_handle()Ljava/lang/String;
    .locals 2

    .prologue
    .line 55
    iget-wide v0, p0, Lcom/vivox/service/vx_req_account_anonymous_login_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_account_anonymous_login_t_connector_handle_get(JLcom/vivox/service/vx_req_account_anonymous_login_t;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public getDisplayname()Ljava/lang/String;
    .locals 2

    .prologue
    .line 59
    iget-wide v0, p0, Lcom/vivox/service/vx_req_account_anonymous_login_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_account_anonymous_login_t_displayname_get(JLcom/vivox/service/vx_req_account_anonymous_login_t;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public getEnable_buddies_and_presence()I
    .locals 2

    .prologue
    .line 63
    iget-wide v0, p0, Lcom/vivox/service/vx_req_account_anonymous_login_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_account_anonymous_login_t_enable_buddies_and_presence_get(JLcom/vivox/service/vx_req_account_anonymous_login_t;)I

    move-result v0

    return v0
.end method

.method public getParticipant_property_frequency()I
    .locals 2

    .prologue
    .line 67
    iget-wide v0, p0, Lcom/vivox/service/vx_req_account_anonymous_login_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_account_anonymous_login_t_participant_property_frequency_get(JLcom/vivox/service/vx_req_account_anonymous_login_t;)I

    move-result v0

    return v0
.end method

.method public setAcct_mgmt_server(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 71
    iget-wide v0, p0, Lcom/vivox/service/vx_req_account_anonymous_login_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_account_anonymous_login_t_acct_mgmt_server_set(JLcom/vivox/service/vx_req_account_anonymous_login_t;Ljava/lang/String;)V

    .line 72
    return-void
.end method

.method public setApplication_override(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 75
    iget-wide v0, p0, Lcom/vivox/service/vx_req_account_anonymous_login_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_account_anonymous_login_t_application_override_set(JLcom/vivox/service/vx_req_account_anonymous_login_t;Ljava/lang/String;)V

    .line 76
    return-void
.end method

.method public setApplication_token(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 79
    iget-wide v0, p0, Lcom/vivox/service/vx_req_account_anonymous_login_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_account_anonymous_login_t_application_token_set(JLcom/vivox/service/vx_req_account_anonymous_login_t;Ljava/lang/String;)V

    .line 80
    return-void
.end method

.method public setAutopost_crash_dumps(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 83
    iget-wide v0, p0, Lcom/vivox/service/vx_req_account_anonymous_login_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_account_anonymous_login_t_autopost_crash_dumps_set(JLcom/vivox/service/vx_req_account_anonymous_login_t;I)V

    .line 84
    return-void
.end method

.method public setBase(Lcom/vivox/service/vx_req_base_t;)V
    .locals 6
    .param p1, "com_vivox_service_vx_req_base_t"    # Lcom/vivox/service/vx_req_base_t;

    .prologue
    .line 87
    iget-wide v0, p0, Lcom/vivox/service/vx_req_account_anonymous_login_t;->swigCPtr:J

    invoke-static {p1}, Lcom/vivox/service/vx_req_base_t;->getCPtr(Lcom/vivox/service/vx_req_base_t;)J

    move-result-wide v3

    move-object v2, p0

    move-object v5, p1

    invoke-static/range {v0 .. v5}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_account_anonymous_login_t_base_set(JLcom/vivox/service/vx_req_account_anonymous_login_t;JLcom/vivox/service/vx_req_base_t;)V

    .line 88
    return-void
.end method

.method public setBuddy_management_mode(Lcom/vivox/service/vx_buddy_management_mode;)V
    .locals 3
    .param p1, "com_vivox_service_vx_buddy_management_mode"    # Lcom/vivox/service/vx_buddy_management_mode;

    .prologue
    .line 91
    iget-wide v0, p0, Lcom/vivox/service/vx_req_account_anonymous_login_t;->swigCPtr:J

    invoke-virtual {p1}, Lcom/vivox/service/vx_buddy_management_mode;->swigValue()I

    move-result v2

    invoke-static {v0, v1, p0, v2}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_account_anonymous_login_t_buddy_management_mode_set(JLcom/vivox/service/vx_req_account_anonymous_login_t;I)V

    .line 92
    return-void
.end method

.method public setConnector_handle(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 95
    iget-wide v0, p0, Lcom/vivox/service/vx_req_account_anonymous_login_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_account_anonymous_login_t_connector_handle_set(JLcom/vivox/service/vx_req_account_anonymous_login_t;Ljava/lang/String;)V

    .line 96
    return-void
.end method

.method public setDisplayname(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 99
    iget-wide v0, p0, Lcom/vivox/service/vx_req_account_anonymous_login_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_account_anonymous_login_t_displayname_set(JLcom/vivox/service/vx_req_account_anonymous_login_t;Ljava/lang/String;)V

    .line 100
    return-void
.end method

.method public setEnable_buddies_and_presence(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 103
    iget-wide v0, p0, Lcom/vivox/service/vx_req_account_anonymous_login_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_account_anonymous_login_t_enable_buddies_and_presence_set(JLcom/vivox/service/vx_req_account_anonymous_login_t;I)V

    .line 104
    return-void
.end method

.method public setParticipant_property_frequency(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 107
    iget-wide v0, p0, Lcom/vivox/service/vx_req_account_anonymous_login_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_account_anonymous_login_t_participant_property_frequency_set(JLcom/vivox/service/vx_req_account_anonymous_login_t;I)V

    .line 108
    return-void
.end method
