.class public Lcom/vivox/service/vx_req_connector_create_t;
.super Ljava/lang/Object;
.source "vx_req_connector_create_t.java"


# instance fields
.field protected swigCMemOwn:Z

.field private swigCPtr:J


# direct methods
.method public constructor <init>()V
    .locals 3

    .prologue
    .line 8
    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->new_vx_req_connector_create_t()J

    move-result-wide v0

    const/4 v2, 0x1

    invoke-direct {p0, v0, v1, v2}, Lcom/vivox/service/vx_req_connector_create_t;-><init>(JZ)V

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
    iput-boolean p3, p0, Lcom/vivox/service/vx_req_connector_create_t;->swigCMemOwn:Z

    .line 13
    iput-wide p1, p0, Lcom/vivox/service/vx_req_connector_create_t;->swigCPtr:J

    .line 14
    return-void
.end method

.method protected static getCPtr(Lcom/vivox/service/vx_req_connector_create_t;)J
    .locals 2
    .param p0, "com_vivox_service_vx_req_connector_create_t"    # Lcom/vivox/service/vx_req_connector_create_t;

    .prologue
    .line 17
    if-nez p0, :cond_0

    const-wide/16 v0, 0x0

    :goto_0
    return-wide v0

    :cond_0
    iget-wide v0, p0, Lcom/vivox/service/vx_req_connector_create_t;->swigCPtr:J

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
    iget-wide v0, p0, Lcom/vivox/service/vx_req_connector_create_t;->swigCPtr:J

    cmp-long v0, v0, v2

    if-eqz v0, :cond_0

    iget-boolean v0, p0, Lcom/vivox/service/vx_req_connector_create_t;->swigCMemOwn:Z

    if-nez v0, :cond_1

    .line 22
    :cond_0
    const-wide/16 v0, 0x0

    iput-wide v0, p0, Lcom/vivox/service/vx_req_connector_create_t;->swigCPtr:J
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    .line 27
    monitor-exit p0

    return-void

    .line 24
    :cond_1
    const/4 v0, 0x0

    :try_start_1
    iput-boolean v0, p0, Lcom/vivox/service/vx_req_connector_create_t;->swigCMemOwn:Z

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
    iget-wide v0, p0, Lcom/vivox/service/vx_req_connector_create_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_connector_create_t_acct_mgmt_server_get(JLcom/vivox/service/vx_req_connector_create_t;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public getAllow_cross_domain_logins()I
    .locals 2

    .prologue
    .line 34
    iget-wide v0, p0, Lcom/vivox/service/vx_req_connector_create_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_connector_create_t_allow_cross_domain_logins_get(JLcom/vivox/service/vx_req_connector_create_t;)I

    move-result v0

    return v0
.end method

.method public getApplication()Ljava/lang/String;
    .locals 2

    .prologue
    .line 38
    iget-wide v0, p0, Lcom/vivox/service/vx_req_connector_create_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_connector_create_t_application_get(JLcom/vivox/service/vx_req_connector_create_t;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public getAttempt_stun()Lcom/vivox/service/vx_attempt_stun;
    .locals 2

    .prologue
    .line 42
    iget-wide v0, p0, Lcom/vivox/service/vx_req_connector_create_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_connector_create_t_attempt_stun_get(JLcom/vivox/service/vx_req_connector_create_t;)I

    move-result v0

    invoke-static {v0}, Lcom/vivox/service/vx_attempt_stun;->swigToEnum(I)Lcom/vivox/service/vx_attempt_stun;

    move-result-object v0

    return-object v0
.end method

.method public getBase()Lcom/vivox/service/vx_req_base_t;
    .locals 4

    .prologue
    .line 46
    iget-wide v2, p0, Lcom/vivox/service/vx_req_connector_create_t;->swigCPtr:J

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_connector_create_t_base_get(JLcom/vivox/service/vx_req_connector_create_t;)J

    move-result-wide v0

    .line 47
    .local v0, "vx_req_connector_create_t_base_get":J
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

.method public getClient_name()Ljava/lang/String;
    .locals 2

    .prologue
    .line 51
    iget-wide v0, p0, Lcom/vivox/service/vx_req_connector_create_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_connector_create_t_client_name_get(JLcom/vivox/service/vx_req_connector_create_t;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public getDefault_codec()I
    .locals 2

    .prologue
    .line 55
    iget-wide v0, p0, Lcom/vivox/service/vx_req_connector_create_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_connector_create_t_default_codec_get(JLcom/vivox/service/vx_req_connector_create_t;)I

    move-result v0

    return v0
.end method

.method public getHttp_proxy_server_name()Ljava/lang/String;
    .locals 2

    .prologue
    .line 59
    iget-wide v0, p0, Lcom/vivox/service/vx_req_connector_create_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_connector_create_t_http_proxy_server_name_get(JLcom/vivox/service/vx_req_connector_create_t;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public getHttp_proxy_server_port()I
    .locals 2

    .prologue
    .line 63
    iget-wide v0, p0, Lcom/vivox/service/vx_req_connector_create_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_connector_create_t_http_proxy_server_port_get(JLcom/vivox/service/vx_req_connector_create_t;)I

    move-result v0

    return v0
.end method

.method public getLog_filename_prefix()Ljava/lang/String;
    .locals 2

    .prologue
    .line 67
    iget-wide v0, p0, Lcom/vivox/service/vx_req_connector_create_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_connector_create_t_log_filename_prefix_get(JLcom/vivox/service/vx_req_connector_create_t;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public getLog_filename_suffix()Ljava/lang/String;
    .locals 2

    .prologue
    .line 71
    iget-wide v0, p0, Lcom/vivox/service/vx_req_connector_create_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_connector_create_t_log_filename_suffix_get(JLcom/vivox/service/vx_req_connector_create_t;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public getLog_folder()Ljava/lang/String;
    .locals 2

    .prologue
    .line 75
    iget-wide v0, p0, Lcom/vivox/service/vx_req_connector_create_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_connector_create_t_log_folder_get(JLcom/vivox/service/vx_req_connector_create_t;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public getLog_level()I
    .locals 2

    .prologue
    .line 79
    iget-wide v0, p0, Lcom/vivox/service/vx_req_connector_create_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_connector_create_t_log_level_get(JLcom/vivox/service/vx_req_connector_create_t;)I

    move-result v0

    return v0
.end method

.method public getMax_calls()I
    .locals 2

    .prologue
    .line 83
    iget-wide v0, p0, Lcom/vivox/service/vx_req_connector_create_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_connector_create_t_max_calls_get(JLcom/vivox/service/vx_req_connector_create_t;)I

    move-result v0

    return v0
.end method

.method public getMaximum_port()I
    .locals 2

    .prologue
    .line 87
    iget-wide v0, p0, Lcom/vivox/service/vx_req_connector_create_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_connector_create_t_maximum_port_get(JLcom/vivox/service/vx_req_connector_create_t;)I

    move-result v0

    return v0
.end method

.method public getMedia_probe_server()Ljava/lang/String;
    .locals 2

    .prologue
    .line 91
    iget-wide v0, p0, Lcom/vivox/service/vx_req_connector_create_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_connector_create_t_media_probe_server_get(JLcom/vivox/service/vx_req_connector_create_t;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public getMinimum_port()I
    .locals 2

    .prologue
    .line 95
    iget-wide v0, p0, Lcom/vivox/service/vx_req_connector_create_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_connector_create_t_minimum_port_get(JLcom/vivox/service/vx_req_connector_create_t;)I

    move-result v0

    return v0
.end method

.method public getMode()Lcom/vivox/service/vx_connector_mode;
    .locals 2

    .prologue
    .line 99
    iget-wide v0, p0, Lcom/vivox/service/vx_req_connector_create_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_connector_create_t_mode_get(JLcom/vivox/service/vx_req_connector_create_t;)I

    move-result v0

    invoke-static {v0}, Lcom/vivox/service/vx_connector_mode;->swigToEnum(I)Lcom/vivox/service/vx_connector_mode;

    move-result-object v0

    return-object v0
.end method

.method public getSession_handle_type()Lcom/vivox/service/vx_session_handle_type;
    .locals 2

    .prologue
    .line 103
    iget-wide v0, p0, Lcom/vivox/service/vx_req_connector_create_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_connector_create_t_session_handle_type_get(JLcom/vivox/service/vx_req_connector_create_t;)I

    move-result v0

    invoke-static {v0}, Lcom/vivox/service/vx_session_handle_type;->swigToEnum(I)Lcom/vivox/service/vx_session_handle_type;

    move-result-object v0

    return-object v0
.end method

.method public getUser_agent_id()Ljava/lang/String;
    .locals 2

    .prologue
    .line 107
    iget-wide v0, p0, Lcom/vivox/service/vx_req_connector_create_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_connector_create_t_user_agent_id_get(JLcom/vivox/service/vx_req_connector_create_t;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public setAcct_mgmt_server(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 111
    iget-wide v0, p0, Lcom/vivox/service/vx_req_connector_create_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_connector_create_t_acct_mgmt_server_set(JLcom/vivox/service/vx_req_connector_create_t;Ljava/lang/String;)V

    .line 112
    return-void
.end method

.method public setAllow_cross_domain_logins(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 115
    iget-wide v0, p0, Lcom/vivox/service/vx_req_connector_create_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_connector_create_t_allow_cross_domain_logins_set(JLcom/vivox/service/vx_req_connector_create_t;I)V

    .line 116
    return-void
.end method

.method public setApplication(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 119
    iget-wide v0, p0, Lcom/vivox/service/vx_req_connector_create_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_connector_create_t_application_set(JLcom/vivox/service/vx_req_connector_create_t;Ljava/lang/String;)V

    .line 120
    return-void
.end method

.method public setAttempt_stun(Lcom/vivox/service/vx_attempt_stun;)V
    .locals 3
    .param p1, "com_vivox_service_vx_attempt_stun"    # Lcom/vivox/service/vx_attempt_stun;

    .prologue
    .line 123
    iget-wide v0, p0, Lcom/vivox/service/vx_req_connector_create_t;->swigCPtr:J

    invoke-virtual {p1}, Lcom/vivox/service/vx_attempt_stun;->swigValue()I

    move-result v2

    invoke-static {v0, v1, p0, v2}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_connector_create_t_attempt_stun_set(JLcom/vivox/service/vx_req_connector_create_t;I)V

    .line 124
    return-void
.end method

.method public setBase(Lcom/vivox/service/vx_req_base_t;)V
    .locals 6
    .param p1, "com_vivox_service_vx_req_base_t"    # Lcom/vivox/service/vx_req_base_t;

    .prologue
    .line 127
    iget-wide v0, p0, Lcom/vivox/service/vx_req_connector_create_t;->swigCPtr:J

    invoke-static {p1}, Lcom/vivox/service/vx_req_base_t;->getCPtr(Lcom/vivox/service/vx_req_base_t;)J

    move-result-wide v3

    move-object v2, p0

    move-object v5, p1

    invoke-static/range {v0 .. v5}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_connector_create_t_base_set(JLcom/vivox/service/vx_req_connector_create_t;JLcom/vivox/service/vx_req_base_t;)V

    .line 128
    return-void
.end method

.method public setClient_name(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 131
    iget-wide v0, p0, Lcom/vivox/service/vx_req_connector_create_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_connector_create_t_client_name_set(JLcom/vivox/service/vx_req_connector_create_t;Ljava/lang/String;)V

    .line 132
    return-void
.end method

.method public setDefault_codec(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 135
    iget-wide v0, p0, Lcom/vivox/service/vx_req_connector_create_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_connector_create_t_default_codec_set(JLcom/vivox/service/vx_req_connector_create_t;I)V

    .line 136
    return-void
.end method

.method public setHttp_proxy_server_name(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 139
    iget-wide v0, p0, Lcom/vivox/service/vx_req_connector_create_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_connector_create_t_http_proxy_server_name_set(JLcom/vivox/service/vx_req_connector_create_t;Ljava/lang/String;)V

    .line 140
    return-void
.end method

.method public setHttp_proxy_server_port(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 143
    iget-wide v0, p0, Lcom/vivox/service/vx_req_connector_create_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_connector_create_t_http_proxy_server_port_set(JLcom/vivox/service/vx_req_connector_create_t;I)V

    .line 144
    return-void
.end method

.method public setLog_filename_prefix(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 147
    iget-wide v0, p0, Lcom/vivox/service/vx_req_connector_create_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_connector_create_t_log_filename_prefix_set(JLcom/vivox/service/vx_req_connector_create_t;Ljava/lang/String;)V

    .line 148
    return-void
.end method

.method public setLog_filename_suffix(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 151
    iget-wide v0, p0, Lcom/vivox/service/vx_req_connector_create_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_connector_create_t_log_filename_suffix_set(JLcom/vivox/service/vx_req_connector_create_t;Ljava/lang/String;)V

    .line 152
    return-void
.end method

.method public setLog_folder(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 155
    iget-wide v0, p0, Lcom/vivox/service/vx_req_connector_create_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_connector_create_t_log_folder_set(JLcom/vivox/service/vx_req_connector_create_t;Ljava/lang/String;)V

    .line 156
    return-void
.end method

.method public setLog_level(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 159
    iget-wide v0, p0, Lcom/vivox/service/vx_req_connector_create_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_connector_create_t_log_level_set(JLcom/vivox/service/vx_req_connector_create_t;I)V

    .line 160
    return-void
.end method

.method public setMax_calls(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 163
    iget-wide v0, p0, Lcom/vivox/service/vx_req_connector_create_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_connector_create_t_max_calls_set(JLcom/vivox/service/vx_req_connector_create_t;I)V

    .line 164
    return-void
.end method

.method public setMaximum_port(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 167
    iget-wide v0, p0, Lcom/vivox/service/vx_req_connector_create_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_connector_create_t_maximum_port_set(JLcom/vivox/service/vx_req_connector_create_t;I)V

    .line 168
    return-void
.end method

.method public setMedia_probe_server(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 171
    iget-wide v0, p0, Lcom/vivox/service/vx_req_connector_create_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_connector_create_t_media_probe_server_set(JLcom/vivox/service/vx_req_connector_create_t;Ljava/lang/String;)V

    .line 172
    return-void
.end method

.method public setMinimum_port(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 175
    iget-wide v0, p0, Lcom/vivox/service/vx_req_connector_create_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_connector_create_t_minimum_port_set(JLcom/vivox/service/vx_req_connector_create_t;I)V

    .line 176
    return-void
.end method

.method public setMode(Lcom/vivox/service/vx_connector_mode;)V
    .locals 3
    .param p1, "com_vivox_service_vx_connector_mode"    # Lcom/vivox/service/vx_connector_mode;

    .prologue
    .line 179
    iget-wide v0, p0, Lcom/vivox/service/vx_req_connector_create_t;->swigCPtr:J

    invoke-virtual {p1}, Lcom/vivox/service/vx_connector_mode;->swigValue()I

    move-result v2

    invoke-static {v0, v1, p0, v2}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_connector_create_t_mode_set(JLcom/vivox/service/vx_req_connector_create_t;I)V

    .line 180
    return-void
.end method

.method public setSession_handle_type(Lcom/vivox/service/vx_session_handle_type;)V
    .locals 3
    .param p1, "com_vivox_service_vx_session_handle_type"    # Lcom/vivox/service/vx_session_handle_type;

    .prologue
    .line 183
    iget-wide v0, p0, Lcom/vivox/service/vx_req_connector_create_t;->swigCPtr:J

    invoke-virtual {p1}, Lcom/vivox/service/vx_session_handle_type;->swigValue()I

    move-result v2

    invoke-static {v0, v1, p0, v2}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_connector_create_t_session_handle_type_set(JLcom/vivox/service/vx_req_connector_create_t;I)V

    .line 184
    return-void
.end method

.method public setUser_agent_id(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 187
    iget-wide v0, p0, Lcom/vivox/service/vx_req_connector_create_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_connector_create_t_user_agent_id_set(JLcom/vivox/service/vx_req_connector_create_t;Ljava/lang/String;)V

    .line 188
    return-void
.end method
