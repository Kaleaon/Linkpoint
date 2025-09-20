.class public Lcom/vivox/service/vx_generic_credentials;
.super Ljava/lang/Object;
.source "vx_generic_credentials.java"


# instance fields
.field protected swigCMemOwn:Z

.field private swigCPtr:J


# direct methods
.method public constructor <init>()V
    .locals 3

    .prologue
    .line 8
    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->new_vx_generic_credentials()J

    move-result-wide v0

    const/4 v2, 0x1

    invoke-direct {p0, v0, v1, v2}, Lcom/vivox/service/vx_generic_credentials;-><init>(JZ)V

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
    iput-boolean p3, p0, Lcom/vivox/service/vx_generic_credentials;->swigCMemOwn:Z

    .line 13
    iput-wide p1, p0, Lcom/vivox/service/vx_generic_credentials;->swigCPtr:J

    .line 14
    return-void
.end method

.method protected static getCPtr(Lcom/vivox/service/vx_generic_credentials;)J
    .locals 2
    .param p0, "com_vivox_service_vx_generic_credentials"    # Lcom/vivox/service/vx_generic_credentials;

    .prologue
    .line 17
    if-nez p0, :cond_0

    const-wide/16 v0, 0x0

    :goto_0
    return-wide v0

    :cond_0
    iget-wide v0, p0, Lcom/vivox/service/vx_generic_credentials;->swigCPtr:J

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
    iget-wide v0, p0, Lcom/vivox/service/vx_generic_credentials;->swigCPtr:J

    cmp-long v0, v0, v2

    if-eqz v0, :cond_0

    iget-boolean v0, p0, Lcom/vivox/service/vx_generic_credentials;->swigCMemOwn:Z

    if-nez v0, :cond_1

    .line 22
    :cond_0
    const-wide/16 v0, 0x0

    iput-wide v0, p0, Lcom/vivox/service/vx_generic_credentials;->swigCPtr:J
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    .line 27
    monitor-exit p0

    return-void

    .line 24
    :cond_1
    const/4 v0, 0x0

    :try_start_1
    iput-boolean v0, p0, Lcom/vivox/service/vx_generic_credentials;->swigCMemOwn:Z

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

.method public getAdmin_password()Ljava/lang/String;
    .locals 2

    .prologue
    .line 30
    iget-wide v0, p0, Lcom/vivox/service/vx_generic_credentials;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_generic_credentials_admin_password_get(JLcom/vivox/service/vx_generic_credentials;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public getAdmin_username()Ljava/lang/String;
    .locals 2

    .prologue
    .line 34
    iget-wide v0, p0, Lcom/vivox/service/vx_generic_credentials;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_generic_credentials_admin_username_get(JLcom/vivox/service/vx_generic_credentials;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public getGrant_document()Ljava/lang/String;
    .locals 2

    .prologue
    .line 38
    iget-wide v0, p0, Lcom/vivox/service/vx_generic_credentials;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_generic_credentials_grant_document_get(JLcom/vivox/service/vx_generic_credentials;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public getServer_url()Ljava/lang/String;
    .locals 2

    .prologue
    .line 42
    iget-wide v0, p0, Lcom/vivox/service/vx_generic_credentials;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_generic_credentials_server_url_get(JLcom/vivox/service/vx_generic_credentials;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public setAdmin_password(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 46
    iget-wide v0, p0, Lcom/vivox/service/vx_generic_credentials;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_generic_credentials_admin_password_set(JLcom/vivox/service/vx_generic_credentials;Ljava/lang/String;)V

    .line 47
    return-void
.end method

.method public setAdmin_username(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 50
    iget-wide v0, p0, Lcom/vivox/service/vx_generic_credentials;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_generic_credentials_admin_username_set(JLcom/vivox/service/vx_generic_credentials;Ljava/lang/String;)V

    .line 51
    return-void
.end method

.method public setGrant_document(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 54
    iget-wide v0, p0, Lcom/vivox/service/vx_generic_credentials;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_generic_credentials_grant_document_set(JLcom/vivox/service/vx_generic_credentials;Ljava/lang/String;)V

    .line 55
    return-void
.end method

.method public setServer_url(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 58
    iget-wide v0, p0, Lcom/vivox/service/vx_generic_credentials;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_generic_credentials_server_url_set(JLcom/vivox/service/vx_generic_credentials;Ljava/lang/String;)V

    .line 59
    return-void
.end method
