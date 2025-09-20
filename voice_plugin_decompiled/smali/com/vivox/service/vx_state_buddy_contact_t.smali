.class public Lcom/vivox/service/vx_state_buddy_contact_t;
.super Ljava/lang/Object;
.source "vx_state_buddy_contact_t.java"


# instance fields
.field protected swigCMemOwn:Z

.field private swigCPtr:J


# direct methods
.method public constructor <init>()V
    .locals 3

    .prologue
    .line 8
    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->new_vx_state_buddy_contact_t()J

    move-result-wide v0

    const/4 v2, 0x1

    invoke-direct {p0, v0, v1, v2}, Lcom/vivox/service/vx_state_buddy_contact_t;-><init>(JZ)V

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
    iput-boolean p3, p0, Lcom/vivox/service/vx_state_buddy_contact_t;->swigCMemOwn:Z

    .line 13
    iput-wide p1, p0, Lcom/vivox/service/vx_state_buddy_contact_t;->swigCPtr:J

    .line 14
    return-void
.end method

.method protected static getCPtr(Lcom/vivox/service/vx_state_buddy_contact_t;)J
    .locals 2
    .param p0, "com_vivox_service_vx_state_buddy_contact_t"    # Lcom/vivox/service/vx_state_buddy_contact_t;

    .prologue
    .line 17
    if-nez p0, :cond_0

    const-wide/16 v0, 0x0

    :goto_0
    return-wide v0

    :cond_0
    iget-wide v0, p0, Lcom/vivox/service/vx_state_buddy_contact_t;->swigCPtr:J

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
    iget-wide v0, p0, Lcom/vivox/service/vx_state_buddy_contact_t;->swigCPtr:J

    cmp-long v0, v0, v2

    if-eqz v0, :cond_0

    iget-boolean v0, p0, Lcom/vivox/service/vx_state_buddy_contact_t;->swigCMemOwn:Z

    if-nez v0, :cond_1

    .line 22
    :cond_0
    const-wide/16 v0, 0x0

    iput-wide v0, p0, Lcom/vivox/service/vx_state_buddy_contact_t;->swigCPtr:J
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    .line 27
    monitor-exit p0

    return-void

    .line 24
    :cond_1
    const/4 v0, 0x0

    :try_start_1
    iput-boolean v0, p0, Lcom/vivox/service/vx_state_buddy_contact_t;->swigCMemOwn:Z

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

.method public getApplication()Ljava/lang/String;
    .locals 2

    .prologue
    .line 30
    iget-wide v0, p0, Lcom/vivox/service/vx_state_buddy_contact_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_buddy_contact_t_application_get(JLcom/vivox/service/vx_state_buddy_contact_t;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public getContact()Ljava/lang/String;
    .locals 2

    .prologue
    .line 34
    iget-wide v0, p0, Lcom/vivox/service/vx_state_buddy_contact_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_buddy_contact_t_contact_get(JLcom/vivox/service/vx_state_buddy_contact_t;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public getCustom_message()Ljava/lang/String;
    .locals 2

    .prologue
    .line 38
    iget-wide v0, p0, Lcom/vivox/service/vx_state_buddy_contact_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_buddy_contact_t_custom_message_get(JLcom/vivox/service/vx_state_buddy_contact_t;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public getDisplay_name()Ljava/lang/String;
    .locals 2

    .prologue
    .line 42
    iget-wide v0, p0, Lcom/vivox/service/vx_state_buddy_contact_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_buddy_contact_t_display_name_get(JLcom/vivox/service/vx_state_buddy_contact_t;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public getId()Ljava/lang/String;
    .locals 2

    .prologue
    .line 46
    iget-wide v0, p0, Lcom/vivox/service/vx_state_buddy_contact_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_buddy_contact_t_id_get(JLcom/vivox/service/vx_state_buddy_contact_t;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public getPresence()Lcom/vivox/service/vx_buddy_presence_state;
    .locals 2

    .prologue
    .line 50
    iget-wide v0, p0, Lcom/vivox/service/vx_state_buddy_contact_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_buddy_contact_t_presence_get(JLcom/vivox/service/vx_state_buddy_contact_t;)I

    move-result v0

    invoke-static {v0}, Lcom/vivox/service/vx_buddy_presence_state;->swigToEnum(I)Lcom/vivox/service/vx_buddy_presence_state;

    move-result-object v0

    return-object v0
.end method

.method public getPriority()Ljava/lang/String;
    .locals 2

    .prologue
    .line 54
    iget-wide v0, p0, Lcom/vivox/service/vx_state_buddy_contact_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_buddy_contact_t_priority_get(JLcom/vivox/service/vx_state_buddy_contact_t;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public setApplication(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 58
    iget-wide v0, p0, Lcom/vivox/service/vx_state_buddy_contact_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_buddy_contact_t_application_set(JLcom/vivox/service/vx_state_buddy_contact_t;Ljava/lang/String;)V

    .line 59
    return-void
.end method

.method public setContact(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 62
    iget-wide v0, p0, Lcom/vivox/service/vx_state_buddy_contact_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_buddy_contact_t_contact_set(JLcom/vivox/service/vx_state_buddy_contact_t;Ljava/lang/String;)V

    .line 63
    return-void
.end method

.method public setCustom_message(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 66
    iget-wide v0, p0, Lcom/vivox/service/vx_state_buddy_contact_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_buddy_contact_t_custom_message_set(JLcom/vivox/service/vx_state_buddy_contact_t;Ljava/lang/String;)V

    .line 67
    return-void
.end method

.method public setDisplay_name(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 70
    iget-wide v0, p0, Lcom/vivox/service/vx_state_buddy_contact_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_buddy_contact_t_display_name_set(JLcom/vivox/service/vx_state_buddy_contact_t;Ljava/lang/String;)V

    .line 71
    return-void
.end method

.method public setId(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 74
    iget-wide v0, p0, Lcom/vivox/service/vx_state_buddy_contact_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_buddy_contact_t_id_set(JLcom/vivox/service/vx_state_buddy_contact_t;Ljava/lang/String;)V

    .line 75
    return-void
.end method

.method public setPresence(Lcom/vivox/service/vx_buddy_presence_state;)V
    .locals 3
    .param p1, "com_vivox_service_vx_buddy_presence_state"    # Lcom/vivox/service/vx_buddy_presence_state;

    .prologue
    .line 78
    iget-wide v0, p0, Lcom/vivox/service/vx_state_buddy_contact_t;->swigCPtr:J

    invoke-virtual {p1}, Lcom/vivox/service/vx_buddy_presence_state;->swigValue()I

    move-result v2

    invoke-static {v0, v1, p0, v2}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_buddy_contact_t_presence_set(JLcom/vivox/service/vx_state_buddy_contact_t;I)V

    .line 79
    return-void
.end method

.method public setPriority(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 82
    iget-wide v0, p0, Lcom/vivox/service/vx_state_buddy_contact_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_buddy_contact_t_priority_set(JLcom/vivox/service/vx_state_buddy_contact_t;Ljava/lang/String;)V

    .line 83
    return-void
.end method
