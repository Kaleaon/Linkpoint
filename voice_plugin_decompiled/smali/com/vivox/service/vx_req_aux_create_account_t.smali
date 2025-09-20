.class public Lcom/vivox/service/vx_req_aux_create_account_t;
.super Ljava/lang/Object;
.source "vx_req_aux_create_account_t.java"


# instance fields
.field protected swigCMemOwn:Z

.field private swigCPtr:J


# direct methods
.method public constructor <init>()V
    .locals 3

    .prologue
    .line 8
    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->new_vx_req_aux_create_account_t()J

    move-result-wide v0

    const/4 v2, 0x1

    invoke-direct {p0, v0, v1, v2}, Lcom/vivox/service/vx_req_aux_create_account_t;-><init>(JZ)V

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
    iput-boolean p3, p0, Lcom/vivox/service/vx_req_aux_create_account_t;->swigCMemOwn:Z

    .line 13
    iput-wide p1, p0, Lcom/vivox/service/vx_req_aux_create_account_t;->swigCPtr:J

    .line 14
    return-void
.end method

.method protected static getCPtr(Lcom/vivox/service/vx_req_aux_create_account_t;)J
    .locals 2
    .param p0, "com_vivox_service_vx_req_aux_create_account_t"    # Lcom/vivox/service/vx_req_aux_create_account_t;

    .prologue
    .line 17
    if-nez p0, :cond_0

    const-wide/16 v0, 0x0

    :goto_0
    return-wide v0

    :cond_0
    iget-wide v0, p0, Lcom/vivox/service/vx_req_aux_create_account_t;->swigCPtr:J

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
    iget-wide v0, p0, Lcom/vivox/service/vx_req_aux_create_account_t;->swigCPtr:J

    cmp-long v0, v0, v2

    if-eqz v0, :cond_0

    iget-boolean v0, p0, Lcom/vivox/service/vx_req_aux_create_account_t;->swigCMemOwn:Z

    if-nez v0, :cond_1

    .line 22
    :cond_0
    const-wide/16 v0, 0x0

    iput-wide v0, p0, Lcom/vivox/service/vx_req_aux_create_account_t;->swigCPtr:J
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    .line 27
    monitor-exit p0

    return-void

    .line 24
    :cond_1
    const/4 v0, 0x0

    :try_start_1
    iput-boolean v0, p0, Lcom/vivox/service/vx_req_aux_create_account_t;->swigCMemOwn:Z

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

.method public getAge()Ljava/lang/String;
    .locals 2

    .prologue
    .line 30
    iget-wide v0, p0, Lcom/vivox/service/vx_req_aux_create_account_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_aux_create_account_t_age_get(JLcom/vivox/service/vx_req_aux_create_account_t;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public getBase()Lcom/vivox/service/vx_req_base_t;
    .locals 4

    .prologue
    .line 34
    iget-wide v2, p0, Lcom/vivox/service/vx_req_aux_create_account_t;->swigCPtr:J

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_aux_create_account_t_base_get(JLcom/vivox/service/vx_req_aux_create_account_t;)J

    move-result-wide v0

    .line 35
    .local v0, "vx_req_aux_create_account_t_base_get":J
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

.method public getCredentials()Lcom/vivox/service/vx_generic_credentials;
    .locals 4

    .prologue
    .line 39
    iget-wide v2, p0, Lcom/vivox/service/vx_req_aux_create_account_t;->swigCPtr:J

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_aux_create_account_t_credentials_get(JLcom/vivox/service/vx_req_aux_create_account_t;)J

    move-result-wide v0

    .line 40
    .local v0, "vx_req_aux_create_account_t_credentials_get":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_generic_credentials;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_generic_credentials;-><init>(JZ)V

    goto :goto_0
.end method

.method public getDisplayname()Ljava/lang/String;
    .locals 2

    .prologue
    .line 44
    iget-wide v0, p0, Lcom/vivox/service/vx_req_aux_create_account_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_aux_create_account_t_displayname_get(JLcom/vivox/service/vx_req_aux_create_account_t;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public getEmail()Ljava/lang/String;
    .locals 2

    .prologue
    .line 48
    iget-wide v0, p0, Lcom/vivox/service/vx_req_aux_create_account_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_aux_create_account_t_email_get(JLcom/vivox/service/vx_req_aux_create_account_t;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public getExt_id()Ljava/lang/String;
    .locals 2

    .prologue
    .line 52
    iget-wide v0, p0, Lcom/vivox/service/vx_req_aux_create_account_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_aux_create_account_t_ext_id_get(JLcom/vivox/service/vx_req_aux_create_account_t;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public getExt_profile()Ljava/lang/String;
    .locals 2

    .prologue
    .line 56
    iget-wide v0, p0, Lcom/vivox/service/vx_req_aux_create_account_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_aux_create_account_t_ext_profile_get(JLcom/vivox/service/vx_req_aux_create_account_t;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public getFirstname()Ljava/lang/String;
    .locals 2

    .prologue
    .line 60
    iget-wide v0, p0, Lcom/vivox/service/vx_req_aux_create_account_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_aux_create_account_t_firstname_get(JLcom/vivox/service/vx_req_aux_create_account_t;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public getGender()Ljava/lang/String;
    .locals 2

    .prologue
    .line 64
    iget-wide v0, p0, Lcom/vivox/service/vx_req_aux_create_account_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_aux_create_account_t_gender_get(JLcom/vivox/service/vx_req_aux_create_account_t;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public getLang()Ljava/lang/String;
    .locals 2

    .prologue
    .line 68
    iget-wide v0, p0, Lcom/vivox/service/vx_req_aux_create_account_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_aux_create_account_t_lang_get(JLcom/vivox/service/vx_req_aux_create_account_t;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public getLastname()Ljava/lang/String;
    .locals 2

    .prologue
    .line 72
    iget-wide v0, p0, Lcom/vivox/service/vx_req_aux_create_account_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_aux_create_account_t_lastname_get(JLcom/vivox/service/vx_req_aux_create_account_t;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public getNumber()Ljava/lang/String;
    .locals 2

    .prologue
    .line 76
    iget-wide v0, p0, Lcom/vivox/service/vx_req_aux_create_account_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_aux_create_account_t_number_get(JLcom/vivox/service/vx_req_aux_create_account_t;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public getPassword()Ljava/lang/String;
    .locals 2

    .prologue
    .line 80
    iget-wide v0, p0, Lcom/vivox/service/vx_req_aux_create_account_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_aux_create_account_t_password_get(JLcom/vivox/service/vx_req_aux_create_account_t;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public getPhone()Ljava/lang/String;
    .locals 2

    .prologue
    .line 84
    iget-wide v0, p0, Lcom/vivox/service/vx_req_aux_create_account_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_aux_create_account_t_phone_get(JLcom/vivox/service/vx_req_aux_create_account_t;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public getTimezone()Ljava/lang/String;
    .locals 2

    .prologue
    .line 88
    iget-wide v0, p0, Lcom/vivox/service/vx_req_aux_create_account_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_aux_create_account_t_timezone_get(JLcom/vivox/service/vx_req_aux_create_account_t;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public getUser_name()Ljava/lang/String;
    .locals 2

    .prologue
    .line 92
    iget-wide v0, p0, Lcom/vivox/service/vx_req_aux_create_account_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_aux_create_account_t_user_name_get(JLcom/vivox/service/vx_req_aux_create_account_t;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public setAge(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 96
    iget-wide v0, p0, Lcom/vivox/service/vx_req_aux_create_account_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_aux_create_account_t_age_set(JLcom/vivox/service/vx_req_aux_create_account_t;Ljava/lang/String;)V

    .line 97
    return-void
.end method

.method public setBase(Lcom/vivox/service/vx_req_base_t;)V
    .locals 6
    .param p1, "com_vivox_service_vx_req_base_t"    # Lcom/vivox/service/vx_req_base_t;

    .prologue
    .line 100
    iget-wide v0, p0, Lcom/vivox/service/vx_req_aux_create_account_t;->swigCPtr:J

    invoke-static {p1}, Lcom/vivox/service/vx_req_base_t;->getCPtr(Lcom/vivox/service/vx_req_base_t;)J

    move-result-wide v3

    move-object v2, p0

    move-object v5, p1

    invoke-static/range {v0 .. v5}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_aux_create_account_t_base_set(JLcom/vivox/service/vx_req_aux_create_account_t;JLcom/vivox/service/vx_req_base_t;)V

    .line 101
    return-void
.end method

.method public setCredentials(Lcom/vivox/service/vx_generic_credentials;)V
    .locals 6
    .param p1, "com_vivox_service_vx_generic_credentials"    # Lcom/vivox/service/vx_generic_credentials;

    .prologue
    .line 104
    iget-wide v0, p0, Lcom/vivox/service/vx_req_aux_create_account_t;->swigCPtr:J

    invoke-static {p1}, Lcom/vivox/service/vx_generic_credentials;->getCPtr(Lcom/vivox/service/vx_generic_credentials;)J

    move-result-wide v3

    move-object v2, p0

    move-object v5, p1

    invoke-static/range {v0 .. v5}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_aux_create_account_t_credentials_set(JLcom/vivox/service/vx_req_aux_create_account_t;JLcom/vivox/service/vx_generic_credentials;)V

    .line 105
    return-void
.end method

.method public setDisplayname(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 108
    iget-wide v0, p0, Lcom/vivox/service/vx_req_aux_create_account_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_aux_create_account_t_displayname_set(JLcom/vivox/service/vx_req_aux_create_account_t;Ljava/lang/String;)V

    .line 109
    return-void
.end method

.method public setEmail(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 112
    iget-wide v0, p0, Lcom/vivox/service/vx_req_aux_create_account_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_aux_create_account_t_email_set(JLcom/vivox/service/vx_req_aux_create_account_t;Ljava/lang/String;)V

    .line 113
    return-void
.end method

.method public setExt_id(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 116
    iget-wide v0, p0, Lcom/vivox/service/vx_req_aux_create_account_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_aux_create_account_t_ext_id_set(JLcom/vivox/service/vx_req_aux_create_account_t;Ljava/lang/String;)V

    .line 117
    return-void
.end method

.method public setExt_profile(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 120
    iget-wide v0, p0, Lcom/vivox/service/vx_req_aux_create_account_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_aux_create_account_t_ext_profile_set(JLcom/vivox/service/vx_req_aux_create_account_t;Ljava/lang/String;)V

    .line 121
    return-void
.end method

.method public setFirstname(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 124
    iget-wide v0, p0, Lcom/vivox/service/vx_req_aux_create_account_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_aux_create_account_t_firstname_set(JLcom/vivox/service/vx_req_aux_create_account_t;Ljava/lang/String;)V

    .line 125
    return-void
.end method

.method public setGender(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 128
    iget-wide v0, p0, Lcom/vivox/service/vx_req_aux_create_account_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_aux_create_account_t_gender_set(JLcom/vivox/service/vx_req_aux_create_account_t;Ljava/lang/String;)V

    .line 129
    return-void
.end method

.method public setLang(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 132
    iget-wide v0, p0, Lcom/vivox/service/vx_req_aux_create_account_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_aux_create_account_t_lang_set(JLcom/vivox/service/vx_req_aux_create_account_t;Ljava/lang/String;)V

    .line 133
    return-void
.end method

.method public setLastname(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 136
    iget-wide v0, p0, Lcom/vivox/service/vx_req_aux_create_account_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_aux_create_account_t_lastname_set(JLcom/vivox/service/vx_req_aux_create_account_t;Ljava/lang/String;)V

    .line 137
    return-void
.end method

.method public setNumber(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 140
    iget-wide v0, p0, Lcom/vivox/service/vx_req_aux_create_account_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_aux_create_account_t_number_set(JLcom/vivox/service/vx_req_aux_create_account_t;Ljava/lang/String;)V

    .line 141
    return-void
.end method

.method public setPassword(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 144
    iget-wide v0, p0, Lcom/vivox/service/vx_req_aux_create_account_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_aux_create_account_t_password_set(JLcom/vivox/service/vx_req_aux_create_account_t;Ljava/lang/String;)V

    .line 145
    return-void
.end method

.method public setPhone(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 148
    iget-wide v0, p0, Lcom/vivox/service/vx_req_aux_create_account_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_aux_create_account_t_phone_set(JLcom/vivox/service/vx_req_aux_create_account_t;Ljava/lang/String;)V

    .line 149
    return-void
.end method

.method public setTimezone(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 152
    iget-wide v0, p0, Lcom/vivox/service/vx_req_aux_create_account_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_aux_create_account_t_timezone_set(JLcom/vivox/service/vx_req_aux_create_account_t;Ljava/lang/String;)V

    .line 153
    return-void
.end method

.method public setUser_name(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 156
    iget-wide v0, p0, Lcom/vivox/service/vx_req_aux_create_account_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_aux_create_account_t_user_name_set(JLcom/vivox/service/vx_req_aux_create_account_t;Ljava/lang/String;)V

    .line 157
    return-void
.end method
