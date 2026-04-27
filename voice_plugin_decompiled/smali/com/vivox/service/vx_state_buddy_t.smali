.class public Lcom/vivox/service/vx_state_buddy_t;
.super Ljava/lang/Object;
.source "vx_state_buddy_t.java"


# instance fields
.field protected swigCMemOwn:Z

.field private swigCPtr:J


# direct methods
.method public constructor <init>()V
    .locals 3

    .prologue
    .line 8
    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->new_vx_state_buddy_t()J

    move-result-wide v0

    const/4 v2, 0x1

    invoke-direct {p0, v0, v1, v2}, Lcom/vivox/service/vx_state_buddy_t;-><init>(JZ)V

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
    iput-boolean p3, p0, Lcom/vivox/service/vx_state_buddy_t;->swigCMemOwn:Z

    .line 13
    iput-wide p1, p0, Lcom/vivox/service/vx_state_buddy_t;->swigCPtr:J

    .line 14
    return-void
.end method

.method protected static getCPtr(Lcom/vivox/service/vx_state_buddy_t;)J
    .locals 2
    .param p0, "com_vivox_service_vx_state_buddy_t"    # Lcom/vivox/service/vx_state_buddy_t;

    .prologue
    .line 17
    if-nez p0, :cond_0

    const-wide/16 v0, 0x0

    :goto_0
    return-wide v0

    :cond_0
    iget-wide v0, p0, Lcom/vivox/service/vx_state_buddy_t;->swigCPtr:J

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
    iget-wide v0, p0, Lcom/vivox/service/vx_state_buddy_t;->swigCPtr:J

    cmp-long v0, v0, v2

    if-eqz v0, :cond_0

    iget-boolean v0, p0, Lcom/vivox/service/vx_state_buddy_t;->swigCMemOwn:Z

    if-nez v0, :cond_1

    .line 22
    :cond_0
    const-wide/16 v0, 0x0

    iput-wide v0, p0, Lcom/vivox/service/vx_state_buddy_t;->swigCPtr:J
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    .line 27
    monitor-exit p0

    return-void

    .line 24
    :cond_1
    const/4 v0, 0x0

    :try_start_1
    iput-boolean v0, p0, Lcom/vivox/service/vx_state_buddy_t;->swigCMemOwn:Z

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

.method public getBuddy_data()Ljava/lang/String;
    .locals 2

    .prologue
    .line 30
    iget-wide v0, p0, Lcom/vivox/service/vx_state_buddy_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_buddy_t_buddy_data_get(JLcom/vivox/service/vx_state_buddy_t;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public getBuddy_uri()Ljava/lang/String;
    .locals 2

    .prologue
    .line 34
    iget-wide v0, p0, Lcom/vivox/service/vx_state_buddy_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_buddy_t_buddy_uri_get(JLcom/vivox/service/vx_state_buddy_t;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public getDisplay_name()Ljava/lang/String;
    .locals 2

    .prologue
    .line 38
    iget-wide v0, p0, Lcom/vivox/service/vx_state_buddy_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_buddy_t_display_name_get(JLcom/vivox/service/vx_state_buddy_t;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public getParent_group_id()I
    .locals 2

    .prologue
    .line 42
    iget-wide v0, p0, Lcom/vivox/service/vx_state_buddy_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_buddy_t_parent_group_id_get(JLcom/vivox/service/vx_state_buddy_t;)I

    move-result v0

    return v0
.end method

.method public getState_buddy_contact_count()I
    .locals 2

    .prologue
    .line 46
    iget-wide v0, p0, Lcom/vivox/service/vx_state_buddy_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_buddy_t_state_buddy_contact_count_get(JLcom/vivox/service/vx_state_buddy_t;)I

    move-result v0

    return v0
.end method

.method public getState_buddy_contacts()Lcom/vivox/service/SWIGTYPE_p_p_vx_state_buddy_contact;
    .locals 4

    .prologue
    .line 50
    iget-wide v2, p0, Lcom/vivox/service/vx_state_buddy_t;->swigCPtr:J

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_buddy_t_state_buddy_contacts_get(JLcom/vivox/service/vx_state_buddy_t;)J

    move-result-wide v0

    .line 51
    .local v0, "vx_state_buddy_t_state_buddy_contacts_get":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/SWIGTYPE_p_p_vx_state_buddy_contact;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/SWIGTYPE_p_p_vx_state_buddy_contact;-><init>(JZ)V

    goto :goto_0
.end method

.method public setBuddy_data(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 55
    iget-wide v0, p0, Lcom/vivox/service/vx_state_buddy_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_buddy_t_buddy_data_set(JLcom/vivox/service/vx_state_buddy_t;Ljava/lang/String;)V

    .line 56
    return-void
.end method

.method public setBuddy_uri(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 59
    iget-wide v0, p0, Lcom/vivox/service/vx_state_buddy_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_buddy_t_buddy_uri_set(JLcom/vivox/service/vx_state_buddy_t;Ljava/lang/String;)V

    .line 60
    return-void
.end method

.method public setDisplay_name(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 63
    iget-wide v0, p0, Lcom/vivox/service/vx_state_buddy_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_buddy_t_display_name_set(JLcom/vivox/service/vx_state_buddy_t;Ljava/lang/String;)V

    .line 64
    return-void
.end method

.method public setParent_group_id(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 67
    iget-wide v0, p0, Lcom/vivox/service/vx_state_buddy_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_buddy_t_parent_group_id_set(JLcom/vivox/service/vx_state_buddy_t;I)V

    .line 68
    return-void
.end method

.method public setState_buddy_contact_count(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 71
    iget-wide v0, p0, Lcom/vivox/service/vx_state_buddy_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_buddy_t_state_buddy_contact_count_set(JLcom/vivox/service/vx_state_buddy_t;I)V

    .line 72
    return-void
.end method

.method public setState_buddy_contacts(Lcom/vivox/service/SWIGTYPE_p_p_vx_state_buddy_contact;)V
    .locals 4
    .param p1, "sWIGTYPE_p_p_vx_state_buddy_contact"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_state_buddy_contact;

    .prologue
    .line 75
    iget-wide v0, p0, Lcom/vivox/service/vx_state_buddy_t;->swigCPtr:J

    invoke-static {p1}, Lcom/vivox/service/SWIGTYPE_p_p_vx_state_buddy_contact;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_state_buddy_contact;)J

    move-result-wide v2

    invoke-static {v0, v1, p0, v2, v3}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_buddy_t_state_buddy_contacts_set(JLcom/vivox/service/vx_state_buddy_t;J)V

    .line 76
    return-void
.end method
