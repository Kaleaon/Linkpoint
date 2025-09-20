.class public Lcom/vivox/service/vx_voice_font_t;
.super Ljava/lang/Object;
.source "vx_voice_font_t.java"


# instance fields
.field protected swigCMemOwn:Z

.field private swigCPtr:J


# direct methods
.method public constructor <init>()V
    .locals 3

    .prologue
    .line 8
    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->new_vx_voice_font_t()J

    move-result-wide v0

    const/4 v2, 0x1

    invoke-direct {p0, v0, v1, v2}, Lcom/vivox/service/vx_voice_font_t;-><init>(JZ)V

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
    iput-boolean p3, p0, Lcom/vivox/service/vx_voice_font_t;->swigCMemOwn:Z

    .line 13
    iput-wide p1, p0, Lcom/vivox/service/vx_voice_font_t;->swigCPtr:J

    .line 14
    return-void
.end method

.method protected static getCPtr(Lcom/vivox/service/vx_voice_font_t;)J
    .locals 2
    .param p0, "com_vivox_service_vx_voice_font_t"    # Lcom/vivox/service/vx_voice_font_t;

    .prologue
    .line 17
    if-nez p0, :cond_0

    const-wide/16 v0, 0x0

    :goto_0
    return-wide v0

    :cond_0
    iget-wide v0, p0, Lcom/vivox/service/vx_voice_font_t;->swigCPtr:J

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
    iget-wide v0, p0, Lcom/vivox/service/vx_voice_font_t;->swigCPtr:J

    cmp-long v0, v0, v2

    if-eqz v0, :cond_0

    iget-boolean v0, p0, Lcom/vivox/service/vx_voice_font_t;->swigCMemOwn:Z

    if-eqz v0, :cond_0

    .line 22
    const/4 v0, 0x0

    iput-boolean v0, p0, Lcom/vivox/service/vx_voice_font_t;->swigCMemOwn:Z

    .line 23
    iget-wide v0, p0, Lcom/vivox/service/vx_voice_font_t;->swigCPtr:J

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->delete_vx_voice_font_t(J)V

    .line 25
    :cond_0
    const-wide/16 v0, 0x0

    iput-wide v0, p0, Lcom/vivox/service/vx_voice_font_t;->swigCPtr:J
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
    invoke-virtual {p0}, Lcom/vivox/service/vx_voice_font_t;->delete()V

    .line 30
    return-void
.end method

.method public getDescription()Ljava/lang/String;
    .locals 2

    .prologue
    .line 33
    iget-wide v0, p0, Lcom/vivox/service/vx_voice_font_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_voice_font_t_description_get(JLcom/vivox/service/vx_voice_font_t;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public getExpiration_date()Ljava/lang/String;
    .locals 2

    .prologue
    .line 37
    iget-wide v0, p0, Lcom/vivox/service/vx_voice_font_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_voice_font_t_expiration_date_get(JLcom/vivox/service/vx_voice_font_t;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public getExpired()I
    .locals 2

    .prologue
    .line 41
    iget-wide v0, p0, Lcom/vivox/service/vx_voice_font_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_voice_font_t_expired_get(JLcom/vivox/service/vx_voice_font_t;)I

    move-result v0

    return v0
.end method

.method public getFont_delta()Ljava/lang/String;
    .locals 2

    .prologue
    .line 45
    iget-wide v0, p0, Lcom/vivox/service/vx_voice_font_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_voice_font_t_font_delta_get(JLcom/vivox/service/vx_voice_font_t;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public getFont_rules()Ljava/lang/String;
    .locals 2

    .prologue
    .line 49
    iget-wide v0, p0, Lcom/vivox/service/vx_voice_font_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_voice_font_t_font_rules_get(JLcom/vivox/service/vx_voice_font_t;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public getId()I
    .locals 2

    .prologue
    .line 53
    iget-wide v0, p0, Lcom/vivox/service/vx_voice_font_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_voice_font_t_id_get(JLcom/vivox/service/vx_voice_font_t;)I

    move-result v0

    return v0
.end method

.method public getName()Ljava/lang/String;
    .locals 2

    .prologue
    .line 57
    iget-wide v0, p0, Lcom/vivox/service/vx_voice_font_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_voice_font_t_name_get(JLcom/vivox/service/vx_voice_font_t;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public getParent_id()I
    .locals 2

    .prologue
    .line 61
    iget-wide v0, p0, Lcom/vivox/service/vx_voice_font_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_voice_font_t_parent_id_get(JLcom/vivox/service/vx_voice_font_t;)I

    move-result v0

    return v0
.end method

.method public getStatus()Lcom/vivox/service/vx_font_status;
    .locals 2

    .prologue
    .line 65
    iget-wide v0, p0, Lcom/vivox/service/vx_voice_font_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_voice_font_t_status_get(JLcom/vivox/service/vx_voice_font_t;)I

    move-result v0

    invoke-static {v0}, Lcom/vivox/service/vx_font_status;->swigToEnum(I)Lcom/vivox/service/vx_font_status;

    move-result-object v0

    return-object v0
.end method

.method public getType()Lcom/vivox/service/vx_font_type;
    .locals 2

    .prologue
    .line 69
    iget-wide v0, p0, Lcom/vivox/service/vx_voice_font_t;->swigCPtr:J

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_voice_font_t_type_get(JLcom/vivox/service/vx_voice_font_t;)I

    move-result v0

    invoke-static {v0}, Lcom/vivox/service/vx_font_type;->swigToEnum(I)Lcom/vivox/service/vx_font_type;

    move-result-object v0

    return-object v0
.end method

.method public setDescription(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 73
    iget-wide v0, p0, Lcom/vivox/service/vx_voice_font_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_voice_font_t_description_set(JLcom/vivox/service/vx_voice_font_t;Ljava/lang/String;)V

    .line 74
    return-void
.end method

.method public setExpiration_date(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 77
    iget-wide v0, p0, Lcom/vivox/service/vx_voice_font_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_voice_font_t_expiration_date_set(JLcom/vivox/service/vx_voice_font_t;Ljava/lang/String;)V

    .line 78
    return-void
.end method

.method public setExpired(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 81
    iget-wide v0, p0, Lcom/vivox/service/vx_voice_font_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_voice_font_t_expired_set(JLcom/vivox/service/vx_voice_font_t;I)V

    .line 82
    return-void
.end method

.method public setFont_delta(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 85
    iget-wide v0, p0, Lcom/vivox/service/vx_voice_font_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_voice_font_t_font_delta_set(JLcom/vivox/service/vx_voice_font_t;Ljava/lang/String;)V

    .line 86
    return-void
.end method

.method public setFont_rules(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 89
    iget-wide v0, p0, Lcom/vivox/service/vx_voice_font_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_voice_font_t_font_rules_set(JLcom/vivox/service/vx_voice_font_t;Ljava/lang/String;)V

    .line 90
    return-void
.end method

.method public setId(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 93
    iget-wide v0, p0, Lcom/vivox/service/vx_voice_font_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_voice_font_t_id_set(JLcom/vivox/service/vx_voice_font_t;I)V

    .line 94
    return-void
.end method

.method public setName(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 97
    iget-wide v0, p0, Lcom/vivox/service/vx_voice_font_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_voice_font_t_name_set(JLcom/vivox/service/vx_voice_font_t;Ljava/lang/String;)V

    .line 98
    return-void
.end method

.method public setParent_id(I)V
    .locals 2
    .param p1, "i"    # I

    .prologue
    .line 101
    iget-wide v0, p0, Lcom/vivox/service/vx_voice_font_t;->swigCPtr:J

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_voice_font_t_parent_id_set(JLcom/vivox/service/vx_voice_font_t;I)V

    .line 102
    return-void
.end method

.method public setStatus(Lcom/vivox/service/vx_font_status;)V
    .locals 3
    .param p1, "com_vivox_service_vx_font_status"    # Lcom/vivox/service/vx_font_status;

    .prologue
    .line 105
    iget-wide v0, p0, Lcom/vivox/service/vx_voice_font_t;->swigCPtr:J

    invoke-virtual {p1}, Lcom/vivox/service/vx_font_status;->swigValue()I

    move-result v2

    invoke-static {v0, v1, p0, v2}, Lcom/vivox/service/VxClientProxyJNI;->vx_voice_font_t_status_set(JLcom/vivox/service/vx_voice_font_t;I)V

    .line 106
    return-void
.end method

.method public setType(Lcom/vivox/service/vx_font_type;)V
    .locals 3
    .param p1, "com_vivox_service_vx_font_type"    # Lcom/vivox/service/vx_font_type;

    .prologue
    .line 109
    iget-wide v0, p0, Lcom/vivox/service/vx_voice_font_t;->swigCPtr:J

    invoke-virtual {p1}, Lcom/vivox/service/vx_font_type;->swigValue()I

    move-result v2

    invoke-static {v0, v1, p0, v2}, Lcom/vivox/service/VxClientProxyJNI;->vx_voice_font_t_type_set(JLcom/vivox/service/vx_voice_font_t;I)V

    .line 110
    return-void
.end method
