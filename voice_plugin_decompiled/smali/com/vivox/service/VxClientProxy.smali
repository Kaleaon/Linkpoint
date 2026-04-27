.class public Lcom/vivox/service/VxClientProxy;
.super Ljava/lang/Object;
.source "VxClientProxy.java"

# interfaces
.implements Lcom/vivox/service/VxClientProxyConstants;


# direct methods
.method public constructor <init>()V
    .locals 0

    .prologue
    .line 5
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method

.method public static clear_stats()V
    .locals 0

    .prologue
    .line 7
    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->clear_stats()V

    .line 8
    return-void
.end method

.method public static destroy_evt(Lcom/vivox/service/vx_evt_base_t;)V
    .locals 2
    .param p0, "com_vivox_service_vx_evt_base_t"    # Lcom/vivox/service/vx_evt_base_t;

    .prologue
    .line 11
    invoke-static {p0}, Lcom/vivox/service/vx_evt_base_t;->getCPtr(Lcom/vivox/service/vx_evt_base_t;)J

    move-result-wide v0

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->destroy_evt(JLcom/vivox/service/vx_evt_base_t;)V

    .line 12
    return-void
.end method

.method public static destroy_message(Lcom/vivox/service/vx_message_base_t;)V
    .locals 2
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 15
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->destroy_message(JLcom/vivox/service/vx_message_base_t;)V

    .line 16
    return-void
.end method

.method public static destroy_req(Lcom/vivox/service/vx_req_base_t;)V
    .locals 2
    .param p0, "com_vivox_service_vx_req_base_t"    # Lcom/vivox/service/vx_req_base_t;

    .prologue
    .line 19
    invoke-static {p0}, Lcom/vivox/service/vx_req_base_t;->getCPtr(Lcom/vivox/service/vx_req_base_t;)J

    move-result-wide v0

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->destroy_req(JLcom/vivox/service/vx_req_base_t;)V

    .line 20
    return-void
.end method

.method public static destroy_resp(Lcom/vivox/service/vx_resp_base_t;)V
    .locals 2
    .param p0, "com_vivox_service_vx_resp_base_t"    # Lcom/vivox/service/vx_resp_base_t;

    .prologue
    .line 23
    invoke-static {p0}, Lcom/vivox/service/vx_resp_base_t;->getCPtr(Lcom/vivox/service/vx_resp_base_t;)J

    move-result-wide v0

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->destroy_resp(JLcom/vivox/service/vx_resp_base_t;)V

    .line 24
    return-void
.end method

.method public static free_sdk_string(Lcom/vivox/service/SWIGTYPE_p_sdk_string;)V
    .locals 2
    .param p0, "sWIGTYPE_p_sdk_string"    # Lcom/vivox/service/SWIGTYPE_p_sdk_string;

    .prologue
    .line 27
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_sdk_string;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_sdk_string;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->free_sdk_string(J)V

    .line 28
    return-void
.end method

.method public static get_java_system_property(Ljava/lang/String;)Ljava/lang/String;
    .locals 1
    .param p0, "str"    # Ljava/lang/String;

    .prologue
    .line 31
    invoke-static {p0}, Lcom/vivox/service/VxClientProxyJNI;->get_java_system_property(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public static get_next_message(I)Lcom/vivox/service/vx_message_base_t;
    .locals 4
    .param p0, "i"    # I

    .prologue
    .line 35
    invoke-static {p0}, Lcom/vivox/service/VxClientProxyJNI;->get_next_message(I)J

    move-result-wide v0

    .line 36
    .local v0, "j":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_message_base_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_message_base_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static get_next_message_no_wait()Lcom/vivox/service/vx_message_base_t;
    .locals 4

    .prologue
    .line 40
    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->get_next_message_no_wait()J

    move-result-wide v0

    .line 41
    .local v0, "j":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_message_base_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_message_base_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static get_stats()Lcom/vivox/service/SWIGTYPE_p_sdk_string;
    .locals 4

    .prologue
    .line 45
    new-instance v0, Lcom/vivox/service/SWIGTYPE_p_sdk_string;

    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->get_stats()J

    move-result-wide v2

    const/4 v1, 0x1

    invoke-direct {v0, v2, v3, v1}, Lcom/vivox/service/SWIGTYPE_p_sdk_string;-><init>(JZ)V

    return-object v0
.end method

.method public static message_to_xml(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/SWIGTYPE_p_sdk_string;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 49
    new-instance v0, Lcom/vivox/service/SWIGTYPE_p_sdk_string;

    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->message_to_xml(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    const/4 v1, 0x1

    invoke-direct {v0, v2, v3, v1}, Lcom/vivox/service/SWIGTYPE_p_sdk_string;-><init>(JZ)V

    return-object v0
.end method

.method public static msg_evt_subtype(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_event_type;
    .locals 2
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 53
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->msg_evt_subtype(JLcom/vivox/service/vx_message_base_t;)I

    move-result v0

    invoke-static {v0}, Lcom/vivox/service/vx_event_type;->swigToEnum(I)Lcom/vivox/service/vx_event_type;

    move-result-object v0

    return-object v0
.end method

.method public static msg_req_subtype(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_request_type;
    .locals 2
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 57
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->msg_req_subtype(JLcom/vivox/service/vx_message_base_t;)I

    move-result v0

    invoke-static {v0}, Lcom/vivox/service/vx_request_type;->swigToEnum(I)Lcom/vivox/service/vx_request_type;

    move-result-object v0

    return-object v0
.end method

.method public static msg_resp_subtype(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_response_type;
    .locals 2
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 61
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->msg_resp_subtype(JLcom/vivox/service/vx_message_base_t;)I

    move-result v0

    invoke-static {v0}, Lcom/vivox/service/vx_response_type;->swigToEnum(I)Lcom/vivox/service/vx_response_type;

    move-result-object v0

    return-object v0
.end method

.method public static name_value_pairs_create(I)Lcom/vivox/service/SWIGTYPE_p_p_vx_name_value_pair;
    .locals 4
    .param p0, "i"    # I

    .prologue
    .line 65
    invoke-static {p0}, Lcom/vivox/service/VxClientProxyJNI;->name_value_pairs_create(I)J

    move-result-wide v0

    .line 66
    .local v0, "name_value_pairs_create":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/SWIGTYPE_p_p_vx_name_value_pair;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/SWIGTYPE_p_p_vx_name_value_pair;-><init>(JZ)V

    goto :goto_0
.end method

.method public static register_logging_handler(Ljava/lang/String;Ljava/lang/String;Lcom/vivox/service/vx_log_level;)I
    .locals 1
    .param p0, "str"    # Ljava/lang/String;
    .param p1, "str2"    # Ljava/lang/String;
    .param p2, "com_vivox_service_vx_log_level"    # Lcom/vivox/service/vx_log_level;

    .prologue
    .line 70
    invoke-virtual {p2}, Lcom/vivox/service/vx_log_level;->swigValue()I

    move-result v0

    invoke-static {p0, p1, v0}, Lcom/vivox/service/VxClientProxyJNI;->register_logging_handler(Ljava/lang/String;Ljava/lang/String;I)I

    move-result v0

    return v0
.end method

.method public static register_message_notification_handler(Ljava/lang/String;Ljava/lang/String;)I
    .locals 1
    .param p0, "str"    # Ljava/lang/String;
    .param p1, "str2"    # Ljava/lang/String;

    .prologue
    .line 74
    invoke-static {p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->register_message_notification_handler(Ljava/lang/String;Ljava/lang/String;)I

    move-result v0

    return v0
.end method

.method public static request_to_xml(Lcom/vivox/service/vx_req_base_t;)Lcom/vivox/service/SWIGTYPE_p_sdk_string;
    .locals 4
    .param p0, "com_vivox_service_vx_req_base_t"    # Lcom/vivox/service/vx_req_base_t;

    .prologue
    .line 78
    new-instance v0, Lcom/vivox/service/SWIGTYPE_p_sdk_string;

    invoke-static {p0}, Lcom/vivox/service/vx_req_base_t;->getCPtr(Lcom/vivox/service/vx_req_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->request_to_xml(JLcom/vivox/service/vx_req_base_t;)J

    move-result-wide v2

    const/4 v1, 0x1

    invoke-direct {v0, v2, v3, v1}, Lcom/vivox/service/SWIGTYPE_p_sdk_string;-><init>(JZ)V

    return-object v0
.end method

.method public static sdk_string_to_string(Lcom/vivox/service/SWIGTYPE_p_sdk_string;)Ljava/lang/String;
    .locals 2
    .param p0, "sWIGTYPE_p_sdk_string"    # Lcom/vivox/service/SWIGTYPE_p_sdk_string;

    .prologue
    .line 82
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_sdk_string;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_sdk_string;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->sdk_string_to_string(J)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public static set_request_cookie(Lcom/vivox/service/vx_req_base_t;Ljava/lang/String;)V
    .locals 2
    .param p0, "com_vivox_service_vx_req_base_t"    # Lcom/vivox/service/vx_req_base_t;
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 86
    invoke-static {p0}, Lcom/vivox/service/vx_req_base_t;->getCPtr(Lcom/vivox/service/vx_req_base_t;)J

    move-result-wide v0

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->set_request_cookie(JLcom/vivox/service/vx_req_base_t;Ljava/lang/String;)V

    .line 87
    return-void
.end method

.method public static vx_account_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_account;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_account"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_account;

    .prologue
    .line 90
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_account;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_account;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_account_create(J)V

    .line 91
    return-void
.end method

.method public static vx_account_free(Lcom/vivox/service/vx_account_t;)V
    .locals 2
    .param p0, "com_vivox_service_vx_account_t"    # Lcom/vivox/service/vx_account_t;

    .prologue
    .line 94
    invoke-static {p0}, Lcom/vivox/service/vx_account_t;->getCPtr(Lcom/vivox/service/vx_account_t;)J

    move-result-wide v0

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_account_free(JLcom/vivox/service/vx_account_t;)V

    .line 95
    return-void
.end method

.method public static vx_alloc_sdk_handle(Ljava/lang/String;ILcom/vivox/service/SWIGTYPE_p_unsigned_int;)I
    .locals 2
    .param p0, "str"    # Ljava/lang/String;
    .param p1, "i"    # I
    .param p2, "sWIGTYPE_p_unsigned_int"    # Lcom/vivox/service/SWIGTYPE_p_unsigned_int;

    .prologue
    .line 98
    invoke-static {p2}, Lcom/vivox/service/SWIGTYPE_p_unsigned_int;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_unsigned_int;)J

    move-result-wide v0

    invoke-static {p0, p1, v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_alloc_sdk_handle(Ljava/lang/String;IJ)I

    move-result v0

    return v0
.end method

.method public static vx_apply_font_to_file(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)I
    .locals 1
    .param p0, "str"    # Ljava/lang/String;
    .param p1, "str2"    # Ljava/lang/String;
    .param p2, "str3"    # Ljava/lang/String;

    .prologue
    .line 102
    invoke-static {p0, p1, p2}, Lcom/vivox/service/VxClientProxyJNI;->vx_apply_font_to_file(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)I

    move-result v0

    return v0
.end method

.method public static vx_apply_font_to_file_return_energy_ratio(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lcom/vivox/service/SWIGTYPE_p_double;)I
    .locals 2
    .param p0, "str"    # Ljava/lang/String;
    .param p1, "str2"    # Ljava/lang/String;
    .param p2, "str3"    # Ljava/lang/String;
    .param p3, "sWIGTYPE_p_double"    # Lcom/vivox/service/SWIGTYPE_p_double;

    .prologue
    .line 106
    invoke-static {p3}, Lcom/vivox/service/SWIGTYPE_p_double;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_double;)J

    move-result-wide v0

    invoke-static {p0, p1, p2, v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_apply_font_to_file_return_energy_ratio(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;J)I

    move-result v0

    return v0
.end method

.method public static vx_apply_font_to_vxz_file_return_energy_ratio(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lcom/vivox/service/SWIGTYPE_p_double;)I
    .locals 2
    .param p0, "str"    # Ljava/lang/String;
    .param p1, "str2"    # Ljava/lang/String;
    .param p2, "str3"    # Ljava/lang/String;
    .param p3, "sWIGTYPE_p_double"    # Lcom/vivox/service/SWIGTYPE_p_double;

    .prologue
    .line 110
    invoke-static {p3}, Lcom/vivox/service/SWIGTYPE_p_double;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_double;)J

    move-result-wide v0

    invoke-static {p0, p1, p2, v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_apply_font_to_vxz_file_return_energy_ratio(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;J)I

    move-result v0

    return v0
.end method

.method public static vx_auto_accept_rule_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_auto_accept_rule;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_auto_accept_rule"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_auto_accept_rule;

    .prologue
    .line 114
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_auto_accept_rule;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_auto_accept_rule;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_auto_accept_rule_create(J)V

    .line 115
    return-void
.end method

.method public static vx_auto_accept_rule_free(Lcom/vivox/service/vx_auto_accept_rule_t;)V
    .locals 2
    .param p0, "com_vivox_service_vx_auto_accept_rule_t"    # Lcom/vivox/service/vx_auto_accept_rule_t;

    .prologue
    .line 118
    invoke-static {p0}, Lcom/vivox/service/vx_auto_accept_rule_t;->getCPtr(Lcom/vivox/service/vx_auto_accept_rule_t;)J

    move-result-wide v0

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_auto_accept_rule_free(JLcom/vivox/service/vx_auto_accept_rule_t;)V

    .line 119
    return-void
.end method

.method public static vx_auto_accept_rules_create(ILcom/vivox/service/SWIGTYPE_p_p_p_vx_auto_accept_rule;)V
    .locals 2
    .param p0, "i"    # I
    .param p1, "sWIGTYPE_p_p_p_vx_auto_accept_rule"    # Lcom/vivox/service/SWIGTYPE_p_p_p_vx_auto_accept_rule;

    .prologue
    .line 122
    invoke-static {p1}, Lcom/vivox/service/SWIGTYPE_p_p_p_vx_auto_accept_rule;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_p_vx_auto_accept_rule;)J

    move-result-wide v0

    invoke-static {p0, v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_auto_accept_rules_create(IJ)V

    .line 123
    return-void
.end method

.method public static vx_auto_accept_rules_free(Lcom/vivox/service/SWIGTYPE_p_p_vx_auto_accept_rule;I)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_auto_accept_rule"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_auto_accept_rule;
    .param p1, "i"    # I

    .prologue
    .line 126
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_auto_accept_rule;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_auto_accept_rule;)J

    move-result-wide v0

    invoke-static {v0, v1, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_auto_accept_rules_free(JI)V

    .line 127
    return-void
.end method

.method public static vx_block_rule_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_block_rule;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_block_rule"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_block_rule;

    .prologue
    .line 130
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_block_rule;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_block_rule;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_block_rule_create(J)V

    .line 131
    return-void
.end method

.method public static vx_block_rule_free(Lcom/vivox/service/vx_block_rule_t;)V
    .locals 2
    .param p0, "com_vivox_service_vx_block_rule_t"    # Lcom/vivox/service/vx_block_rule_t;

    .prologue
    .line 134
    invoke-static {p0}, Lcom/vivox/service/vx_block_rule_t;->getCPtr(Lcom/vivox/service/vx_block_rule_t;)J

    move-result-wide v0

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_block_rule_free(JLcom/vivox/service/vx_block_rule_t;)V

    .line 135
    return-void
.end method

.method public static vx_block_rules_create(ILcom/vivox/service/SWIGTYPE_p_p_p_vx_block_rule;)V
    .locals 2
    .param p0, "i"    # I
    .param p1, "sWIGTYPE_p_p_p_vx_block_rule"    # Lcom/vivox/service/SWIGTYPE_p_p_p_vx_block_rule;

    .prologue
    .line 138
    invoke-static {p1}, Lcom/vivox/service/SWIGTYPE_p_p_p_vx_block_rule;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_p_vx_block_rule;)J

    move-result-wide v0

    invoke-static {p0, v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_block_rules_create(IJ)V

    .line 139
    return-void
.end method

.method public static vx_block_rules_free(Lcom/vivox/service/SWIGTYPE_p_p_vx_block_rule;I)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_block_rule"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_block_rule;
    .param p1, "i"    # I

    .prologue
    .line 142
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_block_rule;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_block_rule;)J

    move-result-wide v0

    invoke-static {v0, v1, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_block_rules_free(JI)V

    .line 143
    return-void
.end method

.method public static vx_buddy_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_buddy;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_buddy"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_buddy;

    .prologue
    .line 146
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_buddy;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_buddy;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_buddy_create(J)V

    .line 147
    return-void
.end method

.method public static vx_buddy_free(Lcom/vivox/service/vx_buddy_t;)V
    .locals 2
    .param p0, "com_vivox_service_vx_buddy_t"    # Lcom/vivox/service/vx_buddy_t;

    .prologue
    .line 150
    invoke-static {p0}, Lcom/vivox/service/vx_buddy_t;->getCPtr(Lcom/vivox/service/vx_buddy_t;)J

    move-result-wide v0

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_buddy_free(JLcom/vivox/service/vx_buddy_t;)V

    .line 151
    return-void
.end method

.method public static vx_buddy_list_create(ILcom/vivox/service/SWIGTYPE_p_p_p_vx_buddy;)V
    .locals 2
    .param p0, "i"    # I
    .param p1, "sWIGTYPE_p_p_p_vx_buddy"    # Lcom/vivox/service/SWIGTYPE_p_p_p_vx_buddy;

    .prologue
    .line 154
    invoke-static {p1}, Lcom/vivox/service/SWIGTYPE_p_p_p_vx_buddy;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_p_vx_buddy;)J

    move-result-wide v0

    invoke-static {p0, v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_buddy_list_create(IJ)V

    .line 155
    return-void
.end method

.method public static vx_buddy_list_free(Lcom/vivox/service/SWIGTYPE_p_p_vx_buddy;I)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_buddy"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_buddy;
    .param p1, "i"    # I

    .prologue
    .line 158
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_buddy;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_buddy;)J

    move-result-wide v0

    invoke-static {v0, v1, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_buddy_list_free(JI)V

    .line 159
    return-void
.end method

.method public static vx_channel_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_channel;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_channel"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_channel;

    .prologue
    .line 162
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_channel;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_channel;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_create(J)V

    .line 163
    return-void
.end method

.method public static vx_channel_favorite_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_channel_favorite;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_channel_favorite"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_channel_favorite;

    .prologue
    .line 166
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_channel_favorite;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_channel_favorite;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_favorite_create(J)V

    .line 167
    return-void
.end method

.method public static vx_channel_favorite_free(Lcom/vivox/service/vx_channel_favorite_t;)V
    .locals 2
    .param p0, "com_vivox_service_vx_channel_favorite_t"    # Lcom/vivox/service/vx_channel_favorite_t;

    .prologue
    .line 170
    invoke-static {p0}, Lcom/vivox/service/vx_channel_favorite_t;->getCPtr(Lcom/vivox/service/vx_channel_favorite_t;)J

    move-result-wide v0

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_favorite_free(JLcom/vivox/service/vx_channel_favorite_t;)V

    .line 171
    return-void
.end method

.method public static vx_channel_favorite_group_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_channel_favorite_group;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_channel_favorite_group"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_channel_favorite_group;

    .prologue
    .line 174
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_channel_favorite_group;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_channel_favorite_group;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_favorite_group_create(J)V

    .line 175
    return-void
.end method

.method public static vx_channel_favorite_group_free(Lcom/vivox/service/vx_channel_favorite_group_t;)V
    .locals 2
    .param p0, "com_vivox_service_vx_channel_favorite_group_t"    # Lcom/vivox/service/vx_channel_favorite_group_t;

    .prologue
    .line 178
    invoke-static {p0}, Lcom/vivox/service/vx_channel_favorite_group_t;->getCPtr(Lcom/vivox/service/vx_channel_favorite_group_t;)J

    move-result-wide v0

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_favorite_group_free(JLcom/vivox/service/vx_channel_favorite_group_t;)V

    .line 179
    return-void
.end method

.method public static vx_channel_favorite_group_list_create(ILcom/vivox/service/SWIGTYPE_p_p_p_vx_channel_favorite_group;)V
    .locals 2
    .param p0, "i"    # I
    .param p1, "sWIGTYPE_p_p_p_vx_channel_favorite_group"    # Lcom/vivox/service/SWIGTYPE_p_p_p_vx_channel_favorite_group;

    .prologue
    .line 182
    invoke-static {p1}, Lcom/vivox/service/SWIGTYPE_p_p_p_vx_channel_favorite_group;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_p_vx_channel_favorite_group;)J

    move-result-wide v0

    invoke-static {p0, v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_favorite_group_list_create(IJ)V

    .line 183
    return-void
.end method

.method public static vx_channel_favorite_group_list_free(Lcom/vivox/service/SWIGTYPE_p_p_vx_channel_favorite_group;I)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_channel_favorite_group"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_channel_favorite_group;
    .param p1, "i"    # I

    .prologue
    .line 186
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_channel_favorite_group;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_channel_favorite_group;)J

    move-result-wide v0

    invoke-static {v0, v1, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_favorite_group_list_free(JI)V

    .line 187
    return-void
.end method

.method public static vx_channel_favorite_list_create(ILcom/vivox/service/SWIGTYPE_p_p_p_vx_channel_favorite;)V
    .locals 2
    .param p0, "i"    # I
    .param p1, "sWIGTYPE_p_p_p_vx_channel_favorite"    # Lcom/vivox/service/SWIGTYPE_p_p_p_vx_channel_favorite;

    .prologue
    .line 190
    invoke-static {p1}, Lcom/vivox/service/SWIGTYPE_p_p_p_vx_channel_favorite;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_p_vx_channel_favorite;)J

    move-result-wide v0

    invoke-static {p0, v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_favorite_list_create(IJ)V

    .line 191
    return-void
.end method

.method public static vx_channel_favorite_list_free(Lcom/vivox/service/SWIGTYPE_p_p_vx_channel_favorite;I)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_channel_favorite"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_channel_favorite;
    .param p1, "i"    # I

    .prologue
    .line 194
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_channel_favorite;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_channel_favorite;)J

    move-result-wide v0

    invoke-static {v0, v1, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_favorite_list_free(JI)V

    .line 195
    return-void
.end method

.method public static vx_channel_free(Lcom/vivox/service/vx_channel_t;)V
    .locals 2
    .param p0, "com_vivox_service_vx_channel_t"    # Lcom/vivox/service/vx_channel_t;

    .prologue
    .line 198
    invoke-static {p0}, Lcom/vivox/service/vx_channel_t;->getCPtr(Lcom/vivox/service/vx_channel_t;)J

    move-result-wide v0

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_free(JLcom/vivox/service/vx_channel_t;)V

    .line 199
    return-void
.end method

.method public static vx_channel_list_create(ILcom/vivox/service/SWIGTYPE_p_p_p_vx_channel;)V
    .locals 2
    .param p0, "i"    # I
    .param p1, "sWIGTYPE_p_p_p_vx_channel"    # Lcom/vivox/service/SWIGTYPE_p_p_p_vx_channel;

    .prologue
    .line 202
    invoke-static {p1}, Lcom/vivox/service/SWIGTYPE_p_p_p_vx_channel;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_p_vx_channel;)J

    move-result-wide v0

    invoke-static {p0, v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_list_create(IJ)V

    .line 203
    return-void
.end method

.method public static vx_channel_list_free(Lcom/vivox/service/SWIGTYPE_p_p_vx_channel;I)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_channel"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_channel;
    .param p1, "i"    # I

    .prologue
    .line 206
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_channel;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_channel;)J

    move-result-wide v0

    invoke-static {v0, v1, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_channel_list_free(JI)V

    .line 207
    return-void
.end method

.method public static vx_connectivity_test_result_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_connectivity_test_result;Lcom/vivox/service/ND_TEST_TYPE;)V
    .locals 3
    .param p0, "sWIGTYPE_p_p_vx_connectivity_test_result"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_connectivity_test_result;
    .param p1, "nd_test_type"    # Lcom/vivox/service/ND_TEST_TYPE;

    .prologue
    .line 210
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_connectivity_test_result;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_connectivity_test_result;)J

    move-result-wide v0

    invoke-virtual {p1}, Lcom/vivox/service/ND_TEST_TYPE;->swigValue()I

    move-result v2

    invoke-static {v0, v1, v2}, Lcom/vivox/service/VxClientProxyJNI;->vx_connectivity_test_result_create(JI)V

    .line 211
    return-void
.end method

.method public static vx_connectivity_test_result_free(Lcom/vivox/service/vx_connectivity_test_result_t;)V
    .locals 2
    .param p0, "com_vivox_service_vx_connectivity_test_result_t"    # Lcom/vivox/service/vx_connectivity_test_result_t;

    .prologue
    .line 214
    invoke-static {p0}, Lcom/vivox/service/vx_connectivity_test_result_t;->getCPtr(Lcom/vivox/service/vx_connectivity_test_result_t;)J

    move-result-wide v0

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_connectivity_test_result_free(JLcom/vivox/service/vx_connectivity_test_result_t;)V

    .line 215
    return-void
.end method

.method public static vx_connectivity_test_results_create(ILcom/vivox/service/SWIGTYPE_p_p_p_vx_connectivity_test_result;)V
    .locals 2
    .param p0, "i"    # I
    .param p1, "sWIGTYPE_p_p_p_vx_connectivity_test_result"    # Lcom/vivox/service/SWIGTYPE_p_p_p_vx_connectivity_test_result;

    .prologue
    .line 218
    invoke-static {p1}, Lcom/vivox/service/SWIGTYPE_p_p_p_vx_connectivity_test_result;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_p_vx_connectivity_test_result;)J

    move-result-wide v0

    invoke-static {p0, v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_connectivity_test_results_create(IJ)V

    .line 219
    return-void
.end method

.method public static vx_connectivity_test_results_free(Lcom/vivox/service/SWIGTYPE_p_p_vx_connectivity_test_result;I)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_connectivity_test_result"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_connectivity_test_result;
    .param p1, "i"    # I

    .prologue
    .line 222
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_connectivity_test_result;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_connectivity_test_result;)J

    move-result-wide v0

    invoke-static {v0, v1, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_connectivity_test_results_free(JI)V

    .line 223
    return-void
.end method

.method public static vx_cookie_create(Ljava/lang/String;Lcom/vivox/service/SWIGTYPE_p_p_char;)V
    .locals 2
    .param p0, "str"    # Ljava/lang/String;
    .param p1, "sWIGTYPE_p_p_char"    # Lcom/vivox/service/SWIGTYPE_p_p_char;

    .prologue
    .line 226
    invoke-static {p1}, Lcom/vivox/service/SWIGTYPE_p_p_char;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_char;)J

    move-result-wide v0

    invoke-static {p0, v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_cookie_create(Ljava/lang/String;J)V

    .line 227
    return-void
.end method

.method public static vx_cookie_free(Lcom/vivox/service/SWIGTYPE_p_p_char;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_char"    # Lcom/vivox/service/SWIGTYPE_p_p_char;

    .prologue
    .line 230
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_char;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_char;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_cookie_free(J)V

    .line 231
    return-void
.end method

.method public static vx_copy_audioBuffer(Lcom/vivox/service/SWIGTYPE_p_void;)Lcom/vivox/service/SWIGTYPE_p_void;
    .locals 4
    .param p0, "sWIGTYPE_p_void"    # Lcom/vivox/service/SWIGTYPE_p_void;

    .prologue
    .line 234
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_void;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_void;)J

    move-result-wide v2

    invoke-static {v2, v3}, Lcom/vivox/service/VxClientProxyJNI;->vx_copy_audioBuffer(J)J

    move-result-wide v0

    .line 235
    .local v0, "vx_copy_audioBuffer":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/SWIGTYPE_p_void;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/SWIGTYPE_p_void;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_create_account(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)I
    .locals 1
    .param p0, "str"    # Ljava/lang/String;
    .param p1, "str2"    # Ljava/lang/String;
    .param p2, "str3"    # Ljava/lang/String;
    .param p3, "str4"    # Ljava/lang/String;
    .param p4, "str5"    # Ljava/lang/String;

    .prologue
    .line 239
    invoke-static {p0, p1, p2, p3, p4}, Lcom/vivox/service/VxClientProxyJNI;->vx_create_account(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)I

    move-result v0

    return v0
.end method

.method public static vx_delete_crash_dump(I)I
    .locals 1
    .param p0, "i"    # I

    .prologue
    .line 243
    invoke-static {p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_delete_crash_dump(I)I

    move-result v0

    return v0
.end method

.method public static vx_device_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_device;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_device"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_device;

    .prologue
    .line 247
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_device;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_device;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_device_create(J)V

    .line 248
    return-void
.end method

.method public static vx_device_free(Lcom/vivox/service/vx_device_t;)V
    .locals 2
    .param p0, "com_vivox_service_vx_device_t"    # Lcom/vivox/service/vx_device_t;

    .prologue
    .line 251
    invoke-static {p0}, Lcom/vivox/service/vx_device_t;->getCPtr(Lcom/vivox/service/vx_device_t;)J

    move-result-wide v0

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_device_free(JLcom/vivox/service/vx_device_t;)V

    .line 252
    return-void
.end method

.method public static vx_devices_create(ILcom/vivox/service/SWIGTYPE_p_p_p_vx_device;)V
    .locals 2
    .param p0, "i"    # I
    .param p1, "sWIGTYPE_p_p_p_vx_device"    # Lcom/vivox/service/SWIGTYPE_p_p_p_vx_device;

    .prologue
    .line 255
    invoke-static {p1}, Lcom/vivox/service/SWIGTYPE_p_p_p_vx_device;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_p_vx_device;)J

    move-result-wide v0

    invoke-static {p0, v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_devices_create(IJ)V

    .line 256
    return-void
.end method

.method public static vx_devices_free(Lcom/vivox/service/SWIGTYPE_p_p_vx_device;I)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_device"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_device;
    .param p1, "i"    # I

    .prologue
    .line 259
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_device;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_device;)J

    move-result-wide v0

    invoke-static {v0, v1, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_devices_free(JI)V

    .line 260
    return-void
.end method

.method public static vx_event_to_xml(Lcom/vivox/service/SWIGTYPE_p_void;Lcom/vivox/service/SWIGTYPE_p_p_char;)V
    .locals 4
    .param p0, "sWIGTYPE_p_void"    # Lcom/vivox/service/SWIGTYPE_p_void;
    .param p1, "sWIGTYPE_p_p_char"    # Lcom/vivox/service/SWIGTYPE_p_p_char;

    .prologue
    .line 263
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_void;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_void;)J

    move-result-wide v0

    invoke-static {p1}, Lcom/vivox/service/SWIGTYPE_p_p_char;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_char;)J

    move-result-wide v2

    invoke-static {v0, v1, v2, v3}, Lcom/vivox/service/VxClientProxyJNI;->vx_event_to_xml(JJ)V

    .line 264
    return-void
.end method

.method public static vx_evt_buddy_and_group_list_changed_t_get_buddies_item(Lcom/vivox/service/vx_evt_buddy_and_group_list_changed_t;I)Lcom/vivox/service/vx_buddy_t;
    .locals 4
    .param p0, "com_vivox_service_vx_evt_buddy_and_group_list_changed_t"    # Lcom/vivox/service/vx_evt_buddy_and_group_list_changed_t;
    .param p1, "i"    # I

    .prologue
    .line 267
    invoke-static {p0}, Lcom/vivox/service/vx_evt_buddy_and_group_list_changed_t;->getCPtr(Lcom/vivox/service/vx_evt_buddy_and_group_list_changed_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_evt_buddy_and_group_list_changed_t_get_buddies_item(JLcom/vivox/service/vx_evt_buddy_and_group_list_changed_t;I)J

    move-result-wide v0

    .line 268
    .local v0, "vx_evt_buddy_and_group_list_changed_t_get_buddies_item":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_buddy_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_buddy_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_evt_buddy_and_group_list_changed_t_get_groups_item(Lcom/vivox/service/vx_evt_buddy_and_group_list_changed_t;I)Lcom/vivox/service/vx_group_t;
    .locals 4
    .param p0, "com_vivox_service_vx_evt_buddy_and_group_list_changed_t"    # Lcom/vivox/service/vx_evt_buddy_and_group_list_changed_t;
    .param p1, "i"    # I

    .prologue
    .line 272
    invoke-static {p0}, Lcom/vivox/service/vx_evt_buddy_and_group_list_changed_t;->getCPtr(Lcom/vivox/service/vx_evt_buddy_and_group_list_changed_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_evt_buddy_and_group_list_changed_t_get_groups_item(JLcom/vivox/service/vx_evt_buddy_and_group_list_changed_t;I)J

    move-result-wide v0

    .line 273
    .local v0, "vx_evt_buddy_and_group_list_changed_t_get_groups_item":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_group_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_group_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_evt_participant_updated_t_get_diagnostic_states_item(Lcom/vivox/service/vx_evt_participant_updated_t;I)Lcom/vivox/service/vx_participant_diagnostic_state_t;
    .locals 2
    .param p0, "com_vivox_service_vx_evt_participant_updated_t"    # Lcom/vivox/service/vx_evt_participant_updated_t;
    .param p1, "i"    # I

    .prologue
    .line 277
    invoke-static {p0}, Lcom/vivox/service/vx_evt_participant_updated_t;->getCPtr(Lcom/vivox/service/vx_evt_participant_updated_t;)J

    move-result-wide v0

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_evt_participant_updated_t_get_diagnostic_states_item(JLcom/vivox/service/vx_evt_participant_updated_t;I)I

    move-result v0

    invoke-static {v0}, Lcom/vivox/service/vx_participant_diagnostic_state_t;->swigToEnum(I)Lcom/vivox/service/vx_participant_diagnostic_state_t;

    move-result-object v0

    return-object v0
.end method

.method public static vx_evt_session_updated_t_get_speaker_position_item(Lcom/vivox/service/vx_evt_session_updated_t;I)D
    .locals 2
    .param p0, "com_vivox_service_vx_evt_session_updated_t"    # Lcom/vivox/service/vx_evt_session_updated_t;
    .param p1, "i"    # I

    .prologue
    .line 281
    invoke-static {p0}, Lcom/vivox/service/vx_evt_session_updated_t;->getCPtr(Lcom/vivox/service/vx_evt_session_updated_t;)J

    move-result-wide v0

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_evt_session_updated_t_get_speaker_position_item(JLcom/vivox/service/vx_evt_session_updated_t;I)D

    move-result-wide v0

    return-wide v0
.end method

.method public static vx_export_audioBuffer_to_wav_file(Lcom/vivox/service/SWIGTYPE_p_void;Ljava/lang/String;)I
    .locals 2
    .param p0, "sWIGTYPE_p_void"    # Lcom/vivox/service/SWIGTYPE_p_void;
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 285
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_void;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_void;)J

    move-result-wide v0

    invoke-static {v0, v1, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_export_audioBuffer_to_wav_file(JLjava/lang/String;)I

    move-result v0

    return v0
.end method

.method public static vx_export_vxr_expanded(Ljava/lang/String;Ljava/lang/String;Lcom/vivox/service/SWIGTYPE_p_f_p_void_int_int__void;Lcom/vivox/service/SWIGTYPE_p_void;)I
    .locals 6
    .param p0, "str"    # Ljava/lang/String;
    .param p1, "str2"    # Ljava/lang/String;
    .param p2, "sWIGTYPE_p_f_p_void_int_int__void"    # Lcom/vivox/service/SWIGTYPE_p_f_p_void_int_int__void;
    .param p3, "sWIGTYPE_p_void"    # Lcom/vivox/service/SWIGTYPE_p_void;

    .prologue
    .line 289
    invoke-static {p2}, Lcom/vivox/service/SWIGTYPE_p_f_p_void_int_int__void;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_f_p_void_int_int__void;)J

    move-result-wide v2

    invoke-static {p3}, Lcom/vivox/service/SWIGTYPE_p_void;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_void;)J

    move-result-wide v4

    move-object v0, p0

    move-object v1, p1

    invoke-static/range {v0 .. v5}, Lcom/vivox/service/VxClientProxyJNI;->vx_export_vxr_expanded(Ljava/lang/String;Ljava/lang/String;JJ)I

    move-result v0

    return v0
.end method

.method public static vx_export_vxr_mixed(Ljava/lang/String;Ljava/lang/String;Lcom/vivox/service/SWIGTYPE_p_f_p_void_int_int__void;Lcom/vivox/service/SWIGTYPE_p_void;)I
    .locals 6
    .param p0, "str"    # Ljava/lang/String;
    .param p1, "str2"    # Ljava/lang/String;
    .param p2, "sWIGTYPE_p_f_p_void_int_int__void"    # Lcom/vivox/service/SWIGTYPE_p_f_p_void_int_int__void;
    .param p3, "sWIGTYPE_p_void"    # Lcom/vivox/service/SWIGTYPE_p_void;

    .prologue
    .line 293
    invoke-static {p2}, Lcom/vivox/service/SWIGTYPE_p_f_p_void_int_int__void;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_f_p_void_int_int__void;)J

    move-result-wide v2

    invoke-static {p3}, Lcom/vivox/service/SWIGTYPE_p_void;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_void;)J

    move-result-wide v4

    move-object v0, p0

    move-object v1, p1

    invoke-static/range {v0 .. v5}, Lcom/vivox/service/VxClientProxyJNI;->vx_export_vxr_mixed(Ljava/lang/String;Ljava/lang/String;JJ)I

    move-result v0

    return v0
.end method

.method public static vx_free(Ljava/lang/String;)V
    .locals 0
    .param p0, "str"    # Ljava/lang/String;

    .prologue
    .line 297
    invoke-static {p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_free(Ljava/lang/String;)V

    .line 298
    return-void
.end method

.method public static vx_free_audioBuffer(Lcom/vivox/service/SWIGTYPE_p_p_void;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_void"    # Lcom/vivox/service/SWIGTYPE_p_p_void;

    .prologue
    .line 301
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_void;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_void;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_free_audioBuffer(J)V

    .line 302
    return-void
.end method

.method public static vx_free_sdk_handle(J)I
    .locals 2
    .param p0, "j"    # J

    .prologue
    .line 305
    invoke-static {p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_free_sdk_handle(J)I

    move-result v0

    return v0
.end method

.method public static vx_get_audioBuffer_duration(Lcom/vivox/service/SWIGTYPE_p_void;)D
    .locals 2
    .param p0, "sWIGTYPE_p_void"    # Lcom/vivox/service/SWIGTYPE_p_void;

    .prologue
    .line 309
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_void;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_void;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_get_audioBuffer_duration(J)D

    move-result-wide v0

    return-wide v0
.end method

.method public static vx_get_crash_dump_count()I
    .locals 1

    .prologue
    .line 313
    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->vx_get_crash_dump_count()I

    move-result v0

    return v0
.end method

.method public static vx_get_crash_dump_generation()I
    .locals 1

    .prologue
    .line 317
    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->vx_get_crash_dump_generation()I

    move-result v0

    return v0
.end method

.method public static vx_get_crash_dump_timestamp(I)Lcom/vivox/service/SWIGTYPE_p_time_t;
    .locals 4
    .param p0, "i"    # I

    .prologue
    .line 321
    new-instance v0, Lcom/vivox/service/SWIGTYPE_p_time_t;

    invoke-static {p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_get_crash_dump_timestamp(I)J

    move-result-wide v2

    const/4 v1, 0x1

    invoke-direct {v0, v2, v3, v1}, Lcom/vivox/service/SWIGTYPE_p_time_t;-><init>(JZ)V

    return-object v0
.end method

.method public static vx_get_default_config(Lcom/vivox/service/vx_sdk_config_t;)I
    .locals 2
    .param p0, "com_vivox_service_vx_sdk_config_t"    # Lcom/vivox/service/vx_sdk_config_t;

    .prologue
    .line 325
    invoke-static {p0}, Lcom/vivox/service/vx_sdk_config_t;->getCPtr(Lcom/vivox/service/vx_sdk_config_t;)J

    move-result-wide v0

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_get_default_config(JLcom/vivox/service/vx_sdk_config_t;)I

    move-result v0

    return v0
.end method

.method public static vx_get_error_string(I)Ljava/lang/String;
    .locals 1
    .param p0, "i"    # I

    .prologue
    .line 329
    invoke-static {p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_get_error_string(I)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public static vx_get_message(Lcom/vivox/service/SWIGTYPE_p_p_vx_message_base;)I
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_message_base"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_message_base;

    .prologue
    .line 333
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_message_base;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_message_base;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_get_message(J)I

    move-result v0

    return v0
.end method

.method public static vx_get_message_type(Ljava/lang/String;)Lcom/vivox/service/vx_message_type;
    .locals 1
    .param p0, "str"    # Ljava/lang/String;

    .prologue
    .line 337
    invoke-static {p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_get_message_type(Ljava/lang/String;)I

    move-result v0

    invoke-static {v0}, Lcom/vivox/service/vx_message_type;->swigToEnum(I)Lcom/vivox/service/vx_message_type;

    move-result-object v0

    return-object v0
.end method

.method public static vx_get_preferred_codec(Lcom/vivox/service/SWIGTYPE_p_int;)I
    .locals 2
    .param p0, "sWIGTYPE_p_int"    # Lcom/vivox/service/SWIGTYPE_p_int;

    .prologue
    .line 341
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_int;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_int;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_get_preferred_codec(J)I

    move-result v0

    return v0
.end method

.method public static vx_get_sdk_version_info()Ljava/lang/String;
    .locals 1

    .prologue
    .line 345
    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->vx_get_sdk_version_info()Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public static vx_get_system_stats(Lcom/vivox/service/vx_system_stats_t;)I
    .locals 2
    .param p0, "com_vivox_service_vx_system_stats_t"    # Lcom/vivox/service/vx_system_stats_t;

    .prologue
    .line 349
    invoke-static {p0}, Lcom/vivox/service/vx_system_stats_t;->getCPtr(Lcom/vivox/service/vx_system_stats_t;)J

    move-result-wide v0

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_get_system_stats(JLcom/vivox/service/vx_system_stats_t;)I

    move-result v0

    return v0
.end method

.method public static vx_get_time_ms()Ljava/math/BigInteger;
    .locals 1

    .prologue
    .line 353
    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->vx_get_time_ms()Ljava/math/BigInteger;

    move-result-object v0

    return-object v0
.end method

.method public static vx_group_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_group;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_group"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_group;

    .prologue
    .line 357
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_group;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_group;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_group_create(J)V

    .line 358
    return-void
.end method

.method public static vx_group_free(Lcom/vivox/service/vx_group_t;)V
    .locals 2
    .param p0, "com_vivox_service_vx_group_t"    # Lcom/vivox/service/vx_group_t;

    .prologue
    .line 361
    invoke-static {p0}, Lcom/vivox/service/vx_group_t;->getCPtr(Lcom/vivox/service/vx_group_t;)J

    move-result-wide v0

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_group_free(JLcom/vivox/service/vx_group_t;)V

    .line 362
    return-void
.end method

.method public static vx_group_list_create(ILcom/vivox/service/SWIGTYPE_p_p_p_vx_group;)V
    .locals 2
    .param p0, "i"    # I
    .param p1, "sWIGTYPE_p_p_p_vx_group"    # Lcom/vivox/service/SWIGTYPE_p_p_p_vx_group;

    .prologue
    .line 365
    invoke-static {p1}, Lcom/vivox/service/SWIGTYPE_p_p_p_vx_group;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_p_vx_group;)J

    move-result-wide v0

    invoke-static {p0, v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_group_list_create(IJ)V

    .line 366
    return-void
.end method

.method public static vx_group_list_free(Lcom/vivox/service/SWIGTYPE_p_p_vx_group;I)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_group"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_group;
    .param p1, "i"    # I

    .prologue
    .line 369
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_group;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_group;)J

    move-result-wide v0

    invoke-static {v0, v1, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_group_list_free(JI)V

    .line 370
    return-void
.end method

.method public static vx_initialize()I
    .locals 1

    .prologue
    .line 373
    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->vx_initialize()I

    move-result v0

    return v0
.end method

.method public static vx_initialize2(Lcom/vivox/service/vx_sdk_config_t;)I
    .locals 2
    .param p0, "com_vivox_service_vx_sdk_config_t"    # Lcom/vivox/service/vx_sdk_config_t;

    .prologue
    .line 377
    invoke-static {p0}, Lcom/vivox/service/vx_sdk_config_t;->getCPtr(Lcom/vivox/service/vx_sdk_config_t;)J

    move-result-wide v0

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_initialize2(JLcom/vivox/service/vx_sdk_config_t;)I

    move-result v0

    return v0
.end method

.method public static vx_issue_request(Lcom/vivox/service/vx_req_base_t;)I
    .locals 2
    .param p0, "com_vivox_service_vx_req_base_t"    # Lcom/vivox/service/vx_req_base_t;

    .prologue
    .line 381
    invoke-static {p0}, Lcom/vivox/service/vx_req_base_t;->getCPtr(Lcom/vivox/service/vx_req_base_t;)J

    move-result-wide v0

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_issue_request(JLcom/vivox/service/vx_req_base_t;)I

    move-result v0

    return v0
.end method

.method public static vx_message_base_t2vx_evt_account_login_state_change_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_evt_account_login_state_change_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 385
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_evt_account_login_state_change_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 386
    .local v0, "vx_message_base_t2vx_evt_account_login_state_change_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_evt_account_login_state_change_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_evt_account_login_state_change_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_evt_aux_audio_properties_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_evt_aux_audio_properties_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 390
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_evt_aux_audio_properties_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 391
    .local v0, "vx_message_base_t2vx_evt_aux_audio_properties_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_evt_aux_audio_properties_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_evt_aux_audio_properties_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_evt_buddy_and_group_list_changed_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_evt_buddy_and_group_list_changed_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 395
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_evt_buddy_and_group_list_changed_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 396
    .local v0, "vx_message_base_t2vx_evt_buddy_and_group_list_changed_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_evt_buddy_and_group_list_changed_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_evt_buddy_and_group_list_changed_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_evt_buddy_changed_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_evt_buddy_changed_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 400
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_evt_buddy_changed_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 401
    .local v0, "vx_message_base_t2vx_evt_buddy_changed_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_evt_buddy_changed_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_evt_buddy_changed_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_evt_buddy_group_changed_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_evt_buddy_group_changed_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 405
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_evt_buddy_group_changed_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 406
    .local v0, "vx_message_base_t2vx_evt_buddy_group_changed_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_evt_buddy_group_changed_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_evt_buddy_group_changed_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_evt_buddy_presence_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_evt_buddy_presence_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 410
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_evt_buddy_presence_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 411
    .local v0, "vx_message_base_t2vx_evt_buddy_presence_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_evt_buddy_presence_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_evt_buddy_presence_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_evt_idle_state_changed_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_evt_idle_state_changed_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 415
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_evt_idle_state_changed_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 416
    .local v0, "vx_message_base_t2vx_evt_idle_state_changed_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_evt_idle_state_changed_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_evt_idle_state_changed_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_evt_keyboard_mouse_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_evt_keyboard_mouse_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 420
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_evt_keyboard_mouse_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 421
    .local v0, "vx_message_base_t2vx_evt_keyboard_mouse_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_evt_keyboard_mouse_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_evt_keyboard_mouse_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_evt_media_completion_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_evt_media_completion_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 425
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_evt_media_completion_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 426
    .local v0, "vx_message_base_t2vx_evt_media_completion_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_evt_media_completion_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_evt_media_completion_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_evt_media_stream_updated_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_evt_media_stream_updated_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 430
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_evt_media_stream_updated_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 431
    .local v0, "vx_message_base_t2vx_evt_media_stream_updated_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_evt_media_stream_updated_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_evt_media_stream_updated_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_evt_message_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_evt_message_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 435
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_evt_message_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 436
    .local v0, "vx_message_base_t2vx_evt_message_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_evt_message_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_evt_message_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_evt_network_message_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_evt_network_message_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 440
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_evt_network_message_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 441
    .local v0, "vx_message_base_t2vx_evt_network_message_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_evt_network_message_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_evt_network_message_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_evt_participant_added_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_evt_participant_added_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 445
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_evt_participant_added_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 446
    .local v0, "vx_message_base_t2vx_evt_participant_added_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_evt_participant_added_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_evt_participant_added_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_evt_participant_removed_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_evt_participant_removed_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 450
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_evt_participant_removed_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 451
    .local v0, "vx_message_base_t2vx_evt_participant_removed_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_evt_participant_removed_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_evt_participant_removed_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_evt_participant_updated_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_evt_participant_updated_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 455
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_evt_participant_updated_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 456
    .local v0, "vx_message_base_t2vx_evt_participant_updated_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_evt_participant_updated_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_evt_participant_updated_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_evt_server_app_data_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_evt_server_app_data_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 460
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_evt_server_app_data_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 461
    .local v0, "vx_message_base_t2vx_evt_server_app_data_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_evt_server_app_data_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_evt_server_app_data_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_evt_session_added_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_evt_session_added_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 465
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_evt_session_added_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 466
    .local v0, "vx_message_base_t2vx_evt_session_added_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_evt_session_added_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_evt_session_added_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_evt_session_notification_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_evt_session_notification_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 470
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_evt_session_notification_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 471
    .local v0, "vx_message_base_t2vx_evt_session_notification_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_evt_session_notification_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_evt_session_notification_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_evt_session_removed_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_evt_session_removed_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 475
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_evt_session_removed_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 476
    .local v0, "vx_message_base_t2vx_evt_session_removed_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_evt_session_removed_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_evt_session_removed_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_evt_session_updated_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_evt_session_updated_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 480
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_evt_session_updated_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 481
    .local v0, "vx_message_base_t2vx_evt_session_updated_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_evt_session_updated_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_evt_session_updated_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_evt_sessiongroup_added_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_evt_sessiongroup_added_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 485
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_evt_sessiongroup_added_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 486
    .local v0, "vx_message_base_t2vx_evt_sessiongroup_added_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_evt_sessiongroup_added_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_evt_sessiongroup_added_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_evt_sessiongroup_playback_frame_played_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_evt_sessiongroup_playback_frame_played_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 490
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_evt_sessiongroup_playback_frame_played_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 491
    .local v0, "vx_message_base_t2vx_evt_sessiongroup_playback_frame_played_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_evt_sessiongroup_playback_frame_played_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_evt_sessiongroup_playback_frame_played_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_evt_sessiongroup_removed_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_evt_sessiongroup_removed_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 495
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_evt_sessiongroup_removed_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 496
    .local v0, "vx_message_base_t2vx_evt_sessiongroup_removed_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_evt_sessiongroup_removed_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_evt_sessiongroup_removed_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_evt_sessiongroup_updated_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_evt_sessiongroup_updated_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 500
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_evt_sessiongroup_updated_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 501
    .local v0, "vx_message_base_t2vx_evt_sessiongroup_updated_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_evt_sessiongroup_updated_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_evt_sessiongroup_updated_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_evt_subscription_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_evt_subscription_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 505
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_evt_subscription_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 506
    .local v0, "vx_message_base_t2vx_evt_subscription_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_evt_subscription_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_evt_subscription_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_evt_text_stream_updated_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_evt_text_stream_updated_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 510
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_evt_text_stream_updated_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 511
    .local v0, "vx_message_base_t2vx_evt_text_stream_updated_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_evt_text_stream_updated_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_evt_text_stream_updated_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_evt_user_app_data_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_evt_user_app_data_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 515
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_evt_user_app_data_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 516
    .local v0, "vx_message_base_t2vx_evt_user_app_data_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_evt_user_app_data_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_evt_user_app_data_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_evt_voice_service_connection_state_changed_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_evt_voice_service_connection_state_changed_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 520
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_evt_voice_service_connection_state_changed_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 521
    .local v0, "vx_message_base_t2vx_evt_voice_service_connection_state_changed_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_evt_voice_service_connection_state_changed_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_evt_voice_service_connection_state_changed_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_account_anonymous_login_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_account_anonymous_login_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 525
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_account_anonymous_login_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 526
    .local v0, "vx_message_base_t2vx_req_account_anonymous_login_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_account_anonymous_login_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_account_anonymous_login_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_account_authtoken_login_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_account_authtoken_login_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 530
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_account_authtoken_login_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 531
    .local v0, "vx_message_base_t2vx_req_account_authtoken_login_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_account_authtoken_login_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_account_authtoken_login_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_account_buddy_delete_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_account_buddy_delete_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 535
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_account_buddy_delete_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 536
    .local v0, "vx_message_base_t2vx_req_account_buddy_delete_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_account_buddy_delete_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_account_buddy_delete_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_account_buddy_search_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_account_buddy_search_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 540
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_account_buddy_search_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 541
    .local v0, "vx_message_base_t2vx_req_account_buddy_search_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_account_buddy_search_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_account_buddy_search_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_account_buddy_set_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_account_buddy_set_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 545
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_account_buddy_set_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 546
    .local v0, "vx_message_base_t2vx_req_account_buddy_set_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_account_buddy_set_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_account_buddy_set_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_account_buddygroup_delete_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_account_buddygroup_delete_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 550
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_account_buddygroup_delete_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 551
    .local v0, "vx_message_base_t2vx_req_account_buddygroup_delete_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_account_buddygroup_delete_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_account_buddygroup_delete_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_account_buddygroup_set_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_account_buddygroup_set_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 555
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_account_buddygroup_set_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 556
    .local v0, "vx_message_base_t2vx_req_account_buddygroup_set_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_account_buddygroup_set_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_account_buddygroup_set_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_account_channel_add_acl_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_account_channel_add_acl_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 560
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_account_channel_add_acl_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 561
    .local v0, "vx_message_base_t2vx_req_account_channel_add_acl_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_account_channel_add_acl_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_account_channel_add_acl_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_account_channel_add_moderator_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_account_channel_add_moderator_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 565
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_account_channel_add_moderator_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 566
    .local v0, "vx_message_base_t2vx_req_account_channel_add_moderator_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_account_channel_add_moderator_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_account_channel_add_moderator_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_account_channel_change_owner_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_account_channel_change_owner_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 570
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_account_channel_change_owner_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 571
    .local v0, "vx_message_base_t2vx_req_account_channel_change_owner_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_account_channel_change_owner_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_account_channel_change_owner_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_account_channel_create_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_account_channel_create_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 575
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_account_channel_create_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 576
    .local v0, "vx_message_base_t2vx_req_account_channel_create_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_account_channel_create_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_account_channel_create_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_account_channel_delete_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_account_channel_delete_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 580
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_account_channel_delete_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 581
    .local v0, "vx_message_base_t2vx_req_account_channel_delete_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_account_channel_delete_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_account_channel_delete_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_account_channel_favorite_delete_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_account_channel_favorite_delete_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 585
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_account_channel_favorite_delete_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 586
    .local v0, "vx_message_base_t2vx_req_account_channel_favorite_delete_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_account_channel_favorite_delete_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_account_channel_favorite_delete_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_account_channel_favorite_group_delete_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_account_channel_favorite_group_delete_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 590
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_account_channel_favorite_group_delete_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 591
    .local v0, "vx_message_base_t2vx_req_account_channel_favorite_group_delete_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_account_channel_favorite_group_delete_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_account_channel_favorite_group_delete_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_account_channel_favorite_group_set_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_account_channel_favorite_group_set_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 595
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_account_channel_favorite_group_set_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 596
    .local v0, "vx_message_base_t2vx_req_account_channel_favorite_group_set_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_account_channel_favorite_group_set_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_account_channel_favorite_group_set_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_account_channel_favorite_set_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_account_channel_favorite_set_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 600
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_account_channel_favorite_set_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 601
    .local v0, "vx_message_base_t2vx_req_account_channel_favorite_set_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_account_channel_favorite_set_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_account_channel_favorite_set_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_account_channel_favorites_get_list_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_account_channel_favorites_get_list_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 605
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_account_channel_favorites_get_list_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 606
    .local v0, "vx_message_base_t2vx_req_account_channel_favorites_get_list_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_account_channel_favorites_get_list_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_account_channel_favorites_get_list_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_account_channel_get_acl_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_account_channel_get_acl_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 610
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_account_channel_get_acl_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 611
    .local v0, "vx_message_base_t2vx_req_account_channel_get_acl_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_account_channel_get_acl_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_account_channel_get_acl_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_account_channel_get_info_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_account_channel_get_info_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 615
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_account_channel_get_info_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 616
    .local v0, "vx_message_base_t2vx_req_account_channel_get_info_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_account_channel_get_info_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_account_channel_get_info_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_account_channel_get_moderators_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_account_channel_get_moderators_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 620
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_account_channel_get_moderators_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 621
    .local v0, "vx_message_base_t2vx_req_account_channel_get_moderators_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_account_channel_get_moderators_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_account_channel_get_moderators_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_account_channel_get_participants_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_account_channel_get_participants_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 625
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_account_channel_get_participants_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 626
    .local v0, "vx_message_base_t2vx_req_account_channel_get_participants_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_account_channel_get_participants_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_account_channel_get_participants_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_account_channel_remove_acl_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_account_channel_remove_acl_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 630
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_account_channel_remove_acl_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 631
    .local v0, "vx_message_base_t2vx_req_account_channel_remove_acl_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_account_channel_remove_acl_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_account_channel_remove_acl_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_account_channel_remove_moderator_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_account_channel_remove_moderator_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 635
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_account_channel_remove_moderator_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 636
    .local v0, "vx_message_base_t2vx_req_account_channel_remove_moderator_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_account_channel_remove_moderator_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_account_channel_remove_moderator_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_account_channel_search_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_account_channel_search_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 640
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_account_channel_search_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 641
    .local v0, "vx_message_base_t2vx_req_account_channel_search_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_account_channel_search_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_account_channel_search_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_account_channel_update_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_account_channel_update_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 645
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_account_channel_update_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 646
    .local v0, "vx_message_base_t2vx_req_account_channel_update_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_account_channel_update_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_account_channel_update_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_account_create_auto_accept_rule_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_account_create_auto_accept_rule_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 650
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_account_create_auto_accept_rule_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 651
    .local v0, "vx_message_base_t2vx_req_account_create_auto_accept_rule_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_account_create_auto_accept_rule_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_account_create_auto_accept_rule_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_account_create_block_rule_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_account_create_block_rule_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 655
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_account_create_block_rule_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 656
    .local v0, "vx_message_base_t2vx_req_account_create_block_rule_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_account_create_block_rule_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_account_create_block_rule_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_account_delete_auto_accept_rule_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_account_delete_auto_accept_rule_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 660
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_account_delete_auto_accept_rule_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 661
    .local v0, "vx_message_base_t2vx_req_account_delete_auto_accept_rule_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_account_delete_auto_accept_rule_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_account_delete_auto_accept_rule_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_account_delete_block_rule_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_account_delete_block_rule_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 665
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_account_delete_block_rule_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 666
    .local v0, "vx_message_base_t2vx_req_account_delete_block_rule_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_account_delete_block_rule_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_account_delete_block_rule_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_account_get_account_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_account_get_account_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 670
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_account_get_account_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 671
    .local v0, "vx_message_base_t2vx_req_account_get_account_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_account_get_account_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_account_get_account_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_account_get_session_fonts_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_account_get_session_fonts_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 675
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_account_get_session_fonts_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 676
    .local v0, "vx_message_base_t2vx_req_account_get_session_fonts_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_account_get_session_fonts_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_account_get_session_fonts_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_account_get_template_fonts_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_account_get_template_fonts_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 680
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_account_get_template_fonts_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 681
    .local v0, "vx_message_base_t2vx_req_account_get_template_fonts_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_account_get_template_fonts_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_account_get_template_fonts_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_account_list_auto_accept_rules_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_account_list_auto_accept_rules_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 685
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_account_list_auto_accept_rules_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 686
    .local v0, "vx_message_base_t2vx_req_account_list_auto_accept_rules_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_account_list_auto_accept_rules_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_account_list_auto_accept_rules_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_account_list_block_rules_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_account_list_block_rules_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 690
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_account_list_block_rules_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 691
    .local v0, "vx_message_base_t2vx_req_account_list_block_rules_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_account_list_block_rules_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_account_list_block_rules_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_account_list_buddies_and_groups_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_account_list_buddies_and_groups_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 695
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_account_list_buddies_and_groups_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 696
    .local v0, "vx_message_base_t2vx_req_account_list_buddies_and_groups_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_account_list_buddies_and_groups_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_account_list_buddies_and_groups_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_account_login_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_account_login_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 700
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_account_login_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 701
    .local v0, "vx_message_base_t2vx_req_account_login_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_account_login_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_account_login_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_account_logout_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_account_logout_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 705
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_account_logout_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 706
    .local v0, "vx_message_base_t2vx_req_account_logout_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_account_logout_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_account_logout_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_account_post_crash_dump_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_account_post_crash_dump_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 710
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_account_post_crash_dump_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 711
    .local v0, "vx_message_base_t2vx_req_account_post_crash_dump_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_account_post_crash_dump_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_account_post_crash_dump_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_account_send_message_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_account_send_message_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 715
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_account_send_message_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 716
    .local v0, "vx_message_base_t2vx_req_account_send_message_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_account_send_message_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_account_send_message_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_account_send_sms_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_account_send_sms_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 720
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_account_send_sms_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 721
    .local v0, "vx_message_base_t2vx_req_account_send_sms_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_account_send_sms_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_account_send_sms_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_account_send_subscription_reply_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_account_send_subscription_reply_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 725
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_account_send_subscription_reply_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 726
    .local v0, "vx_message_base_t2vx_req_account_send_subscription_reply_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_account_send_subscription_reply_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_account_send_subscription_reply_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_account_send_user_app_data_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_account_send_user_app_data_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 730
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_account_send_user_app_data_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 731
    .local v0, "vx_message_base_t2vx_req_account_send_user_app_data_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_account_send_user_app_data_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_account_send_user_app_data_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_account_set_login_properties_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_account_set_login_properties_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 735
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_account_set_login_properties_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 736
    .local v0, "vx_message_base_t2vx_req_account_set_login_properties_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_account_set_login_properties_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_account_set_login_properties_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_account_set_presence_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_account_set_presence_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 740
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_account_set_presence_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 741
    .local v0, "vx_message_base_t2vx_req_account_set_presence_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_account_set_presence_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_account_set_presence_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_account_update_account_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_account_update_account_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 745
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_account_update_account_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 746
    .local v0, "vx_message_base_t2vx_req_account_update_account_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_account_update_account_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_account_update_account_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_account_web_call_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_account_web_call_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 750
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_account_web_call_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 751
    .local v0, "vx_message_base_t2vx_req_account_web_call_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_account_web_call_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_account_web_call_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_aux_capture_audio_start_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_aux_capture_audio_start_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 755
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_aux_capture_audio_start_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 756
    .local v0, "vx_message_base_t2vx_req_aux_capture_audio_start_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_aux_capture_audio_start_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_aux_capture_audio_start_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_aux_capture_audio_stop_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_aux_capture_audio_stop_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 760
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_aux_capture_audio_stop_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 761
    .local v0, "vx_message_base_t2vx_req_aux_capture_audio_stop_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_aux_capture_audio_stop_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_aux_capture_audio_stop_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_aux_connectivity_info_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_aux_connectivity_info_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 765
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_aux_connectivity_info_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 766
    .local v0, "vx_message_base_t2vx_req_aux_connectivity_info_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_aux_connectivity_info_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_aux_connectivity_info_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_aux_create_account_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_aux_create_account_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 770
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_aux_create_account_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 771
    .local v0, "vx_message_base_t2vx_req_aux_create_account_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_aux_create_account_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_aux_create_account_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_aux_deactivate_account_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_aux_deactivate_account_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 775
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_aux_deactivate_account_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 776
    .local v0, "vx_message_base_t2vx_req_aux_deactivate_account_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_aux_deactivate_account_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_aux_deactivate_account_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_aux_get_capture_devices_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_aux_get_capture_devices_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 780
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_aux_get_capture_devices_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 781
    .local v0, "vx_message_base_t2vx_req_aux_get_capture_devices_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_aux_get_capture_devices_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_aux_get_capture_devices_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_aux_get_mic_level_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_aux_get_mic_level_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 785
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_aux_get_mic_level_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 786
    .local v0, "vx_message_base_t2vx_req_aux_get_mic_level_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_aux_get_mic_level_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_aux_get_mic_level_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_aux_get_render_devices_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_aux_get_render_devices_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 790
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_aux_get_render_devices_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 791
    .local v0, "vx_message_base_t2vx_req_aux_get_render_devices_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_aux_get_render_devices_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_aux_get_render_devices_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_aux_get_speaker_level_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_aux_get_speaker_level_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 795
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_aux_get_speaker_level_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 796
    .local v0, "vx_message_base_t2vx_req_aux_get_speaker_level_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_aux_get_speaker_level_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_aux_get_speaker_level_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_aux_get_vad_properties_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_aux_get_vad_properties_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 800
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_aux_get_vad_properties_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 801
    .local v0, "vx_message_base_t2vx_req_aux_get_vad_properties_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_aux_get_vad_properties_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_aux_get_vad_properties_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_aux_global_monitor_keyboard_mouse_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_aux_global_monitor_keyboard_mouse_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 805
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_aux_global_monitor_keyboard_mouse_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 806
    .local v0, "vx_message_base_t2vx_req_aux_global_monitor_keyboard_mouse_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_aux_global_monitor_keyboard_mouse_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_aux_global_monitor_keyboard_mouse_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_aux_play_audio_buffer_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_aux_play_audio_buffer_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 810
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_aux_play_audio_buffer_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 811
    .local v0, "vx_message_base_t2vx_req_aux_play_audio_buffer_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_aux_play_audio_buffer_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_aux_play_audio_buffer_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_aux_reactivate_account_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_aux_reactivate_account_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 815
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_aux_reactivate_account_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 816
    .local v0, "vx_message_base_t2vx_req_aux_reactivate_account_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_aux_reactivate_account_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_aux_reactivate_account_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_aux_render_audio_modify_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_aux_render_audio_modify_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 820
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_aux_render_audio_modify_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 821
    .local v0, "vx_message_base_t2vx_req_aux_render_audio_modify_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_aux_render_audio_modify_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_aux_render_audio_modify_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_aux_render_audio_start_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_aux_render_audio_start_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 825
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_aux_render_audio_start_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 826
    .local v0, "vx_message_base_t2vx_req_aux_render_audio_start_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_aux_render_audio_start_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_aux_render_audio_start_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_aux_render_audio_stop_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_aux_render_audio_stop_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 830
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_aux_render_audio_stop_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 831
    .local v0, "vx_message_base_t2vx_req_aux_render_audio_stop_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_aux_render_audio_stop_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_aux_render_audio_stop_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_aux_reset_password_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_aux_reset_password_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 835
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_aux_reset_password_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 836
    .local v0, "vx_message_base_t2vx_req_aux_reset_password_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_aux_reset_password_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_aux_reset_password_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_aux_set_capture_device_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_aux_set_capture_device_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 840
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_aux_set_capture_device_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 841
    .local v0, "vx_message_base_t2vx_req_aux_set_capture_device_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_aux_set_capture_device_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_aux_set_capture_device_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_aux_set_idle_timeout_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_aux_set_idle_timeout_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 845
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_aux_set_idle_timeout_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 846
    .local v0, "vx_message_base_t2vx_req_aux_set_idle_timeout_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_aux_set_idle_timeout_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_aux_set_idle_timeout_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_aux_set_mic_level_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_aux_set_mic_level_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 850
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_aux_set_mic_level_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 851
    .local v0, "vx_message_base_t2vx_req_aux_set_mic_level_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_aux_set_mic_level_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_aux_set_mic_level_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_aux_set_render_device_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_aux_set_render_device_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 855
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_aux_set_render_device_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 856
    .local v0, "vx_message_base_t2vx_req_aux_set_render_device_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_aux_set_render_device_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_aux_set_render_device_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_aux_set_speaker_level_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_aux_set_speaker_level_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 860
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_aux_set_speaker_level_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 861
    .local v0, "vx_message_base_t2vx_req_aux_set_speaker_level_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_aux_set_speaker_level_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_aux_set_speaker_level_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_aux_set_vad_properties_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_aux_set_vad_properties_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 865
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_aux_set_vad_properties_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 866
    .local v0, "vx_message_base_t2vx_req_aux_set_vad_properties_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_aux_set_vad_properties_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_aux_set_vad_properties_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_aux_start_buffer_capture_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_aux_start_buffer_capture_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 870
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_aux_start_buffer_capture_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 871
    .local v0, "vx_message_base_t2vx_req_aux_start_buffer_capture_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_aux_start_buffer_capture_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_aux_start_buffer_capture_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_channel_ban_user_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_channel_ban_user_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 875
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_channel_ban_user_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 876
    .local v0, "vx_message_base_t2vx_req_channel_ban_user_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_channel_ban_user_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_channel_ban_user_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_channel_get_banned_users_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_channel_get_banned_users_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 880
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_channel_get_banned_users_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 881
    .local v0, "vx_message_base_t2vx_req_channel_get_banned_users_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_channel_get_banned_users_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_channel_get_banned_users_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_channel_kick_user_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_channel_kick_user_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 885
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_channel_kick_user_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 886
    .local v0, "vx_message_base_t2vx_req_channel_kick_user_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_channel_kick_user_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_channel_kick_user_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_channel_mute_all_users_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_channel_mute_all_users_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 890
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_channel_mute_all_users_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 891
    .local v0, "vx_message_base_t2vx_req_channel_mute_all_users_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_channel_mute_all_users_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_channel_mute_all_users_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_channel_mute_user_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_channel_mute_user_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 895
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_channel_mute_user_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 896
    .local v0, "vx_message_base_t2vx_req_channel_mute_user_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_channel_mute_user_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_channel_mute_user_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_channel_set_lock_mode_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_channel_set_lock_mode_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 900
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_channel_set_lock_mode_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 901
    .local v0, "vx_message_base_t2vx_req_channel_set_lock_mode_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_channel_set_lock_mode_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_channel_set_lock_mode_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_connector_create_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_connector_create_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 905
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_connector_create_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 906
    .local v0, "vx_message_base_t2vx_req_connector_create_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_connector_create_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_connector_create_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_connector_get_local_audio_info_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_connector_get_local_audio_info_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 910
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_connector_get_local_audio_info_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 911
    .local v0, "vx_message_base_t2vx_req_connector_get_local_audio_info_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_connector_get_local_audio_info_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_connector_get_local_audio_info_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_connector_initiate_shutdown_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_connector_initiate_shutdown_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 915
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_connector_initiate_shutdown_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 916
    .local v0, "vx_message_base_t2vx_req_connector_initiate_shutdown_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_connector_initiate_shutdown_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_connector_initiate_shutdown_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_connector_mute_local_mic_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_connector_mute_local_mic_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 920
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_connector_mute_local_mic_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 921
    .local v0, "vx_message_base_t2vx_req_connector_mute_local_mic_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_connector_mute_local_mic_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_connector_mute_local_mic_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_connector_mute_local_speaker_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_connector_mute_local_speaker_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 925
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_connector_mute_local_speaker_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 926
    .local v0, "vx_message_base_t2vx_req_connector_mute_local_speaker_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_connector_mute_local_speaker_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_connector_mute_local_speaker_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_connector_set_local_mic_volume_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_connector_set_local_mic_volume_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 930
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_connector_set_local_mic_volume_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 931
    .local v0, "vx_message_base_t2vx_req_connector_set_local_mic_volume_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_connector_set_local_mic_volume_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_connector_set_local_mic_volume_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_connector_set_local_speaker_volume_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_connector_set_local_speaker_volume_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 935
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_connector_set_local_speaker_volume_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 936
    .local v0, "vx_message_base_t2vx_req_connector_set_local_speaker_volume_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_connector_set_local_speaker_volume_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_connector_set_local_speaker_volume_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_session_channel_invite_user_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_session_channel_invite_user_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 940
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_session_channel_invite_user_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 941
    .local v0, "vx_message_base_t2vx_req_session_channel_invite_user_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_session_channel_invite_user_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_session_channel_invite_user_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_session_create_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_session_create_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 945
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_session_create_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 946
    .local v0, "vx_message_base_t2vx_req_session_create_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_session_create_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_session_create_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_session_media_connect_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_session_media_connect_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 950
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_session_media_connect_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 951
    .local v0, "vx_message_base_t2vx_req_session_media_connect_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_session_media_connect_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_session_media_connect_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_session_media_disconnect_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_session_media_disconnect_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 955
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_session_media_disconnect_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 956
    .local v0, "vx_message_base_t2vx_req_session_media_disconnect_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_session_media_disconnect_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_session_media_disconnect_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_session_mute_local_speaker_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_session_mute_local_speaker_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 960
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_session_mute_local_speaker_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 961
    .local v0, "vx_message_base_t2vx_req_session_mute_local_speaker_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_session_mute_local_speaker_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_session_mute_local_speaker_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_session_send_dtmf_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_session_send_dtmf_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 965
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_session_send_dtmf_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 966
    .local v0, "vx_message_base_t2vx_req_session_send_dtmf_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_session_send_dtmf_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_session_send_dtmf_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_session_send_message_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_session_send_message_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 970
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_session_send_message_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 971
    .local v0, "vx_message_base_t2vx_req_session_send_message_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_session_send_message_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_session_send_message_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_session_send_notification_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_session_send_notification_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 975
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_session_send_notification_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 976
    .local v0, "vx_message_base_t2vx_req_session_send_notification_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_session_send_notification_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_session_send_notification_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_session_set_3d_position_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_session_set_3d_position_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 980
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_session_set_3d_position_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 981
    .local v0, "vx_message_base_t2vx_req_session_set_3d_position_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_session_set_3d_position_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_session_set_3d_position_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_session_set_local_speaker_volume_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_session_set_local_speaker_volume_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 985
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_session_set_local_speaker_volume_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 986
    .local v0, "vx_message_base_t2vx_req_session_set_local_speaker_volume_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_session_set_local_speaker_volume_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_session_set_local_speaker_volume_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_session_set_participant_mute_for_me_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_session_set_participant_mute_for_me_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 990
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_session_set_participant_mute_for_me_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 991
    .local v0, "vx_message_base_t2vx_req_session_set_participant_mute_for_me_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_session_set_participant_mute_for_me_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_session_set_participant_mute_for_me_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_session_set_participant_volume_for_me_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_session_set_participant_volume_for_me_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 995
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_session_set_participant_volume_for_me_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 996
    .local v0, "vx_message_base_t2vx_req_session_set_participant_volume_for_me_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_session_set_participant_volume_for_me_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_session_set_participant_volume_for_me_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_session_set_voice_font_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_session_set_voice_font_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1000
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_session_set_voice_font_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1001
    .local v0, "vx_message_base_t2vx_req_session_set_voice_font_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_session_set_voice_font_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_session_set_voice_font_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_session_terminate_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_session_terminate_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1005
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_session_terminate_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1006
    .local v0, "vx_message_base_t2vx_req_session_terminate_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_session_terminate_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_session_terminate_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_session_text_connect_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_session_text_connect_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1010
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_session_text_connect_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1011
    .local v0, "vx_message_base_t2vx_req_session_text_connect_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_session_text_connect_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_session_text_connect_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_session_text_disconnect_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_session_text_disconnect_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1015
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_session_text_disconnect_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1016
    .local v0, "vx_message_base_t2vx_req_session_text_disconnect_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_session_text_disconnect_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_session_text_disconnect_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_sessiongroup_add_session_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_sessiongroup_add_session_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1020
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_sessiongroup_add_session_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1021
    .local v0, "vx_message_base_t2vx_req_sessiongroup_add_session_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_sessiongroup_add_session_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_sessiongroup_add_session_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_sessiongroup_control_audio_injection_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_sessiongroup_control_audio_injection_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1025
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_sessiongroup_control_audio_injection_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1026
    .local v0, "vx_message_base_t2vx_req_sessiongroup_control_audio_injection_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_sessiongroup_control_audio_injection_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_sessiongroup_control_audio_injection_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_sessiongroup_control_playback_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_sessiongroup_control_playback_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1030
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_sessiongroup_control_playback_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1031
    .local v0, "vx_message_base_t2vx_req_sessiongroup_control_playback_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_sessiongroup_control_playback_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_sessiongroup_control_playback_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_sessiongroup_control_recording_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_sessiongroup_control_recording_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1035
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_sessiongroup_control_recording_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1036
    .local v0, "vx_message_base_t2vx_req_sessiongroup_control_recording_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_sessiongroup_control_recording_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_sessiongroup_control_recording_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_sessiongroup_create_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_sessiongroup_create_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1040
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_sessiongroup_create_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1041
    .local v0, "vx_message_base_t2vx_req_sessiongroup_create_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_sessiongroup_create_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_sessiongroup_create_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_sessiongroup_get_stats_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_sessiongroup_get_stats_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1045
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_sessiongroup_get_stats_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1046
    .local v0, "vx_message_base_t2vx_req_sessiongroup_get_stats_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_sessiongroup_get_stats_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_sessiongroup_get_stats_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_sessiongroup_remove_session_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_sessiongroup_remove_session_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1050
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_sessiongroup_remove_session_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1051
    .local v0, "vx_message_base_t2vx_req_sessiongroup_remove_session_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_sessiongroup_remove_session_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_sessiongroup_remove_session_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_sessiongroup_reset_focus_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_sessiongroup_reset_focus_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1055
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_sessiongroup_reset_focus_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1056
    .local v0, "vx_message_base_t2vx_req_sessiongroup_reset_focus_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_sessiongroup_reset_focus_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_sessiongroup_reset_focus_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_sessiongroup_set_focus_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_sessiongroup_set_focus_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1060
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_sessiongroup_set_focus_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1061
    .local v0, "vx_message_base_t2vx_req_sessiongroup_set_focus_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_sessiongroup_set_focus_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_sessiongroup_set_focus_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_sessiongroup_set_playback_options_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_sessiongroup_set_playback_options_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1065
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_sessiongroup_set_playback_options_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1066
    .local v0, "vx_message_base_t2vx_req_sessiongroup_set_playback_options_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_sessiongroup_set_playback_options_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_sessiongroup_set_playback_options_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_sessiongroup_set_session_3d_position_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_sessiongroup_set_session_3d_position_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1070
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_sessiongroup_set_session_3d_position_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1071
    .local v0, "vx_message_base_t2vx_req_sessiongroup_set_session_3d_position_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_sessiongroup_set_session_3d_position_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_sessiongroup_set_session_3d_position_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_sessiongroup_set_tx_all_sessions_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_sessiongroup_set_tx_all_sessions_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1075
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_sessiongroup_set_tx_all_sessions_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1076
    .local v0, "vx_message_base_t2vx_req_sessiongroup_set_tx_all_sessions_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_sessiongroup_set_tx_all_sessions_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_sessiongroup_set_tx_all_sessions_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_sessiongroup_set_tx_no_session_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_sessiongroup_set_tx_no_session_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1080
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_sessiongroup_set_tx_no_session_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1081
    .local v0, "vx_message_base_t2vx_req_sessiongroup_set_tx_no_session_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_sessiongroup_set_tx_no_session_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_sessiongroup_set_tx_no_session_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_sessiongroup_set_tx_session_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_sessiongroup_set_tx_session_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1085
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_sessiongroup_set_tx_session_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1086
    .local v0, "vx_message_base_t2vx_req_sessiongroup_set_tx_session_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_sessiongroup_set_tx_session_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_sessiongroup_set_tx_session_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_sessiongroup_terminate_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_sessiongroup_terminate_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1090
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_sessiongroup_terminate_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1091
    .local v0, "vx_message_base_t2vx_req_sessiongroup_terminate_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_sessiongroup_terminate_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_sessiongroup_terminate_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_req_sessiongroup_unset_focus_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_req_sessiongroup_unset_focus_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1095
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_req_sessiongroup_unset_focus_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1096
    .local v0, "vx_message_base_t2vx_req_sessiongroup_unset_focus_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_req_sessiongroup_unset_focus_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_req_sessiongroup_unset_focus_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_account_anonymous_login_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_account_anonymous_login_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1100
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_account_anonymous_login_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1101
    .local v0, "vx_message_base_t2vx_resp_account_anonymous_login_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_account_anonymous_login_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_account_anonymous_login_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_account_authtoken_login_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_account_authtoken_login_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1105
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_account_authtoken_login_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1106
    .local v0, "vx_message_base_t2vx_resp_account_authtoken_login_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_account_authtoken_login_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_account_authtoken_login_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_account_buddy_delete_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_account_buddy_delete_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1110
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_account_buddy_delete_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1111
    .local v0, "vx_message_base_t2vx_resp_account_buddy_delete_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_account_buddy_delete_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_account_buddy_delete_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_account_buddy_search_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_account_buddy_search_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1115
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_account_buddy_search_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1116
    .local v0, "vx_message_base_t2vx_resp_account_buddy_search_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_account_buddy_search_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_account_buddy_search_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_account_buddy_set_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_account_buddy_set_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1120
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_account_buddy_set_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1121
    .local v0, "vx_message_base_t2vx_resp_account_buddy_set_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_account_buddy_set_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_account_buddy_set_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_account_buddygroup_delete_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_account_buddygroup_delete_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1125
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_account_buddygroup_delete_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1126
    .local v0, "vx_message_base_t2vx_resp_account_buddygroup_delete_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_account_buddygroup_delete_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_account_buddygroup_delete_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_account_buddygroup_set_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_account_buddygroup_set_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1130
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_account_buddygroup_set_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1131
    .local v0, "vx_message_base_t2vx_resp_account_buddygroup_set_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_account_buddygroup_set_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_account_buddygroup_set_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_account_channel_add_acl_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_account_channel_add_acl_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1135
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_account_channel_add_acl_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1136
    .local v0, "vx_message_base_t2vx_resp_account_channel_add_acl_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_account_channel_add_acl_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_account_channel_add_acl_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_account_channel_add_moderator_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_account_channel_add_moderator_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1140
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_account_channel_add_moderator_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1141
    .local v0, "vx_message_base_t2vx_resp_account_channel_add_moderator_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_account_channel_add_moderator_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_account_channel_add_moderator_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_account_channel_change_owner_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_account_channel_change_owner_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1145
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_account_channel_change_owner_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1146
    .local v0, "vx_message_base_t2vx_resp_account_channel_change_owner_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_account_channel_change_owner_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_account_channel_change_owner_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_account_channel_create_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_account_channel_create_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1150
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_account_channel_create_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1151
    .local v0, "vx_message_base_t2vx_resp_account_channel_create_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_account_channel_create_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_account_channel_create_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_account_channel_delete_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_account_channel_delete_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1155
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_account_channel_delete_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1156
    .local v0, "vx_message_base_t2vx_resp_account_channel_delete_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_account_channel_delete_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_account_channel_delete_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_account_channel_favorite_delete_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_account_channel_favorite_delete_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1160
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_account_channel_favorite_delete_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1161
    .local v0, "vx_message_base_t2vx_resp_account_channel_favorite_delete_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_account_channel_favorite_delete_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_account_channel_favorite_delete_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_account_channel_favorite_group_delete_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_account_channel_favorite_group_delete_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1165
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_account_channel_favorite_group_delete_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1166
    .local v0, "vx_message_base_t2vx_resp_account_channel_favorite_group_delete_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_account_channel_favorite_group_delete_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_account_channel_favorite_group_delete_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_account_channel_favorite_group_set_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_account_channel_favorite_group_set_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1170
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_account_channel_favorite_group_set_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1171
    .local v0, "vx_message_base_t2vx_resp_account_channel_favorite_group_set_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_account_channel_favorite_group_set_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_account_channel_favorite_group_set_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_account_channel_favorite_set_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_account_channel_favorite_set_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1175
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_account_channel_favorite_set_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1176
    .local v0, "vx_message_base_t2vx_resp_account_channel_favorite_set_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_account_channel_favorite_set_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_account_channel_favorite_set_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_account_channel_favorites_get_list_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_account_channel_favorites_get_list_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1180
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_account_channel_favorites_get_list_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1181
    .local v0, "vx_message_base_t2vx_resp_account_channel_favorites_get_list_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_account_channel_favorites_get_list_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_account_channel_favorites_get_list_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_account_channel_get_acl_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_account_channel_get_acl_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1185
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_account_channel_get_acl_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1186
    .local v0, "vx_message_base_t2vx_resp_account_channel_get_acl_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_account_channel_get_acl_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_account_channel_get_acl_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_account_channel_get_info_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_account_channel_get_info_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1190
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_account_channel_get_info_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1191
    .local v0, "vx_message_base_t2vx_resp_account_channel_get_info_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_account_channel_get_info_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_account_channel_get_info_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_account_channel_get_moderators_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_account_channel_get_moderators_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1195
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_account_channel_get_moderators_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1196
    .local v0, "vx_message_base_t2vx_resp_account_channel_get_moderators_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_account_channel_get_moderators_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_account_channel_get_moderators_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_account_channel_get_participants_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_account_channel_get_participants_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1200
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_account_channel_get_participants_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1201
    .local v0, "vx_message_base_t2vx_resp_account_channel_get_participants_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_account_channel_get_participants_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_account_channel_get_participants_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_account_channel_remove_acl_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_account_channel_remove_acl_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1205
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_account_channel_remove_acl_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1206
    .local v0, "vx_message_base_t2vx_resp_account_channel_remove_acl_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_account_channel_remove_acl_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_account_channel_remove_acl_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_account_channel_remove_moderator_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_account_channel_remove_moderator_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1210
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_account_channel_remove_moderator_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1211
    .local v0, "vx_message_base_t2vx_resp_account_channel_remove_moderator_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_account_channel_remove_moderator_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_account_channel_remove_moderator_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_account_channel_search_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_account_channel_search_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1215
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_account_channel_search_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1216
    .local v0, "vx_message_base_t2vx_resp_account_channel_search_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_account_channel_search_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_account_channel_search_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_account_channel_update_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_account_channel_update_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1220
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_account_channel_update_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1221
    .local v0, "vx_message_base_t2vx_resp_account_channel_update_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_account_channel_update_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_account_channel_update_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_account_create_auto_accept_rule_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_account_create_auto_accept_rule_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1225
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_account_create_auto_accept_rule_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1226
    .local v0, "vx_message_base_t2vx_resp_account_create_auto_accept_rule_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_account_create_auto_accept_rule_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_account_create_auto_accept_rule_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_account_create_block_rule_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_account_create_block_rule_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1230
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_account_create_block_rule_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1231
    .local v0, "vx_message_base_t2vx_resp_account_create_block_rule_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_account_create_block_rule_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_account_create_block_rule_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_account_delete_auto_accept_rule_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_account_delete_auto_accept_rule_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1235
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_account_delete_auto_accept_rule_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1236
    .local v0, "vx_message_base_t2vx_resp_account_delete_auto_accept_rule_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_account_delete_auto_accept_rule_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_account_delete_auto_accept_rule_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_account_delete_block_rule_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_account_delete_block_rule_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1240
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_account_delete_block_rule_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1241
    .local v0, "vx_message_base_t2vx_resp_account_delete_block_rule_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_account_delete_block_rule_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_account_delete_block_rule_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_account_get_account_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_account_get_account_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1245
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_account_get_account_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1246
    .local v0, "vx_message_base_t2vx_resp_account_get_account_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_account_get_account_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_account_get_account_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_account_get_session_fonts_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_account_get_session_fonts_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1250
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_account_get_session_fonts_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1251
    .local v0, "vx_message_base_t2vx_resp_account_get_session_fonts_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_account_get_session_fonts_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_account_get_session_fonts_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_account_get_template_fonts_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_account_get_template_fonts_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1255
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_account_get_template_fonts_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1256
    .local v0, "vx_message_base_t2vx_resp_account_get_template_fonts_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_account_get_template_fonts_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_account_get_template_fonts_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_account_list_auto_accept_rules_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_account_list_auto_accept_rules_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1260
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_account_list_auto_accept_rules_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1261
    .local v0, "vx_message_base_t2vx_resp_account_list_auto_accept_rules_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_account_list_auto_accept_rules_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_account_list_auto_accept_rules_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_account_list_block_rules_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_account_list_block_rules_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1265
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_account_list_block_rules_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1266
    .local v0, "vx_message_base_t2vx_resp_account_list_block_rules_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_account_list_block_rules_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_account_list_block_rules_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_account_list_buddies_and_groups_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_account_list_buddies_and_groups_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1270
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_account_list_buddies_and_groups_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1271
    .local v0, "vx_message_base_t2vx_resp_account_list_buddies_and_groups_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_account_list_buddies_and_groups_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_account_list_buddies_and_groups_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_account_login_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_account_login_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1275
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_account_login_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1276
    .local v0, "vx_message_base_t2vx_resp_account_login_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_account_login_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_account_login_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_account_logout_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_account_logout_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1280
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_account_logout_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1281
    .local v0, "vx_message_base_t2vx_resp_account_logout_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_account_logout_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_account_logout_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_account_post_crash_dump_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_account_post_crash_dump_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1285
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_account_post_crash_dump_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1286
    .local v0, "vx_message_base_t2vx_resp_account_post_crash_dump_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_account_post_crash_dump_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_account_post_crash_dump_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_account_send_message_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_account_send_message_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1290
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_account_send_message_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1291
    .local v0, "vx_message_base_t2vx_resp_account_send_message_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_account_send_message_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_account_send_message_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_account_send_sms_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_account_send_sms_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1295
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_account_send_sms_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1296
    .local v0, "vx_message_base_t2vx_resp_account_send_sms_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_account_send_sms_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_account_send_sms_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_account_send_subscription_reply_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_account_send_subscription_reply_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1300
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_account_send_subscription_reply_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1301
    .local v0, "vx_message_base_t2vx_resp_account_send_subscription_reply_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_account_send_subscription_reply_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_account_send_subscription_reply_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_account_send_user_app_data_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_account_send_user_app_data_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1305
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_account_send_user_app_data_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1306
    .local v0, "vx_message_base_t2vx_resp_account_send_user_app_data_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_account_send_user_app_data_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_account_send_user_app_data_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_account_set_login_properties_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_account_set_login_properties_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1310
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_account_set_login_properties_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1311
    .local v0, "vx_message_base_t2vx_resp_account_set_login_properties_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_account_set_login_properties_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_account_set_login_properties_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_account_set_presence_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_account_set_presence_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1315
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_account_set_presence_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1316
    .local v0, "vx_message_base_t2vx_resp_account_set_presence_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_account_set_presence_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_account_set_presence_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_account_update_account_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_account_update_account_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1320
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_account_update_account_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1321
    .local v0, "vx_message_base_t2vx_resp_account_update_account_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_account_update_account_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_account_update_account_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_account_web_call_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_account_web_call_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1325
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_account_web_call_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1326
    .local v0, "vx_message_base_t2vx_resp_account_web_call_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_account_web_call_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_account_web_call_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_aux_capture_audio_start_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_aux_capture_audio_start_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1330
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_aux_capture_audio_start_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1331
    .local v0, "vx_message_base_t2vx_resp_aux_capture_audio_start_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_aux_capture_audio_start_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_aux_capture_audio_start_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_aux_capture_audio_stop_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_aux_capture_audio_stop_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1335
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_aux_capture_audio_stop_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1336
    .local v0, "vx_message_base_t2vx_resp_aux_capture_audio_stop_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_aux_capture_audio_stop_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_aux_capture_audio_stop_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_aux_connectivity_info_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_aux_connectivity_info_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1340
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_aux_connectivity_info_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1341
    .local v0, "vx_message_base_t2vx_resp_aux_connectivity_info_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_aux_connectivity_info_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_aux_connectivity_info_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_aux_create_account_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_aux_create_account_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1345
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_aux_create_account_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1346
    .local v0, "vx_message_base_t2vx_resp_aux_create_account_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_aux_create_account_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_aux_create_account_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_aux_deactivate_account_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_aux_deactivate_account_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1350
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_aux_deactivate_account_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1351
    .local v0, "vx_message_base_t2vx_resp_aux_deactivate_account_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_aux_deactivate_account_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_aux_deactivate_account_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_aux_diagnostic_state_dump_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_aux_diagnostic_state_dump_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1355
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_aux_diagnostic_state_dump_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1356
    .local v0, "vx_message_base_t2vx_resp_aux_diagnostic_state_dump_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_aux_diagnostic_state_dump_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_aux_diagnostic_state_dump_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_aux_get_capture_devices_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_aux_get_capture_devices_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1360
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_aux_get_capture_devices_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1361
    .local v0, "vx_message_base_t2vx_resp_aux_get_capture_devices_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_aux_get_capture_devices_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_aux_get_capture_devices_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_aux_get_mic_level_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_aux_get_mic_level_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1365
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_aux_get_mic_level_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1366
    .local v0, "vx_message_base_t2vx_resp_aux_get_mic_level_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_aux_get_mic_level_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_aux_get_mic_level_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_aux_get_render_devices_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_aux_get_render_devices_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1370
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_aux_get_render_devices_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1371
    .local v0, "vx_message_base_t2vx_resp_aux_get_render_devices_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_aux_get_render_devices_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_aux_get_render_devices_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_aux_get_speaker_level_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_aux_get_speaker_level_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1375
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_aux_get_speaker_level_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1376
    .local v0, "vx_message_base_t2vx_resp_aux_get_speaker_level_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_aux_get_speaker_level_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_aux_get_speaker_level_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_aux_get_vad_properties_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_aux_get_vad_properties_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1380
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_aux_get_vad_properties_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1381
    .local v0, "vx_message_base_t2vx_resp_aux_get_vad_properties_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_aux_get_vad_properties_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_aux_get_vad_properties_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_aux_global_monitor_keyboard_mouse_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_aux_global_monitor_keyboard_mouse_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1385
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_aux_global_monitor_keyboard_mouse_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1386
    .local v0, "vx_message_base_t2vx_resp_aux_global_monitor_keyboard_mouse_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_aux_global_monitor_keyboard_mouse_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_aux_global_monitor_keyboard_mouse_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_aux_play_audio_buffer_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_aux_play_audio_buffer_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1390
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_aux_play_audio_buffer_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1391
    .local v0, "vx_message_base_t2vx_resp_aux_play_audio_buffer_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_aux_play_audio_buffer_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_aux_play_audio_buffer_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_aux_reactivate_account_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_aux_reactivate_account_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1395
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_aux_reactivate_account_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1396
    .local v0, "vx_message_base_t2vx_resp_aux_reactivate_account_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_aux_reactivate_account_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_aux_reactivate_account_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_aux_render_audio_modify_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_aux_render_audio_modify_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1400
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_aux_render_audio_modify_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1401
    .local v0, "vx_message_base_t2vx_resp_aux_render_audio_modify_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_aux_render_audio_modify_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_aux_render_audio_modify_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_aux_render_audio_start_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_aux_render_audio_start_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1405
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_aux_render_audio_start_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1406
    .local v0, "vx_message_base_t2vx_resp_aux_render_audio_start_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_aux_render_audio_start_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_aux_render_audio_start_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_aux_render_audio_stop_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_aux_render_audio_stop_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1410
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_aux_render_audio_stop_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1411
    .local v0, "vx_message_base_t2vx_resp_aux_render_audio_stop_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_aux_render_audio_stop_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_aux_render_audio_stop_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_aux_reset_password_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_aux_reset_password_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1415
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_aux_reset_password_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1416
    .local v0, "vx_message_base_t2vx_resp_aux_reset_password_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_aux_reset_password_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_aux_reset_password_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_aux_set_capture_device_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_aux_set_capture_device_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1420
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_aux_set_capture_device_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1421
    .local v0, "vx_message_base_t2vx_resp_aux_set_capture_device_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_aux_set_capture_device_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_aux_set_capture_device_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_aux_set_idle_timeout_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_aux_set_idle_timeout_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1425
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_aux_set_idle_timeout_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1426
    .local v0, "vx_message_base_t2vx_resp_aux_set_idle_timeout_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_aux_set_idle_timeout_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_aux_set_idle_timeout_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_aux_set_mic_level_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_aux_set_mic_level_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1430
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_aux_set_mic_level_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1431
    .local v0, "vx_message_base_t2vx_resp_aux_set_mic_level_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_aux_set_mic_level_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_aux_set_mic_level_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_aux_set_render_device_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_aux_set_render_device_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1435
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_aux_set_render_device_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1436
    .local v0, "vx_message_base_t2vx_resp_aux_set_render_device_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_aux_set_render_device_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_aux_set_render_device_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_aux_set_speaker_level_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_aux_set_speaker_level_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1440
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_aux_set_speaker_level_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1441
    .local v0, "vx_message_base_t2vx_resp_aux_set_speaker_level_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_aux_set_speaker_level_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_aux_set_speaker_level_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_aux_set_vad_properties_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_aux_set_vad_properties_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1445
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_aux_set_vad_properties_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1446
    .local v0, "vx_message_base_t2vx_resp_aux_set_vad_properties_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_aux_set_vad_properties_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_aux_set_vad_properties_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_aux_start_buffer_capture_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_aux_start_buffer_capture_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1450
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_aux_start_buffer_capture_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1451
    .local v0, "vx_message_base_t2vx_resp_aux_start_buffer_capture_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_aux_start_buffer_capture_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_aux_start_buffer_capture_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_channel_ban_user_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_channel_ban_user_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1455
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_channel_ban_user_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1456
    .local v0, "vx_message_base_t2vx_resp_channel_ban_user_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_channel_ban_user_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_channel_ban_user_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_channel_get_banned_users_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_channel_get_banned_users_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1460
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_channel_get_banned_users_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1461
    .local v0, "vx_message_base_t2vx_resp_channel_get_banned_users_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_channel_get_banned_users_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_channel_get_banned_users_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_channel_kick_user_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_channel_kick_user_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1465
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_channel_kick_user_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1466
    .local v0, "vx_message_base_t2vx_resp_channel_kick_user_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_channel_kick_user_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_channel_kick_user_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_channel_mute_all_users_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_channel_mute_all_users_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1470
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_channel_mute_all_users_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1471
    .local v0, "vx_message_base_t2vx_resp_channel_mute_all_users_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_channel_mute_all_users_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_channel_mute_all_users_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_channel_mute_user_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_channel_mute_user_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1475
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_channel_mute_user_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1476
    .local v0, "vx_message_base_t2vx_resp_channel_mute_user_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_channel_mute_user_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_channel_mute_user_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_channel_set_lock_mode_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_channel_set_lock_mode_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1480
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_channel_set_lock_mode_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1481
    .local v0, "vx_message_base_t2vx_resp_channel_set_lock_mode_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_channel_set_lock_mode_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_channel_set_lock_mode_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_connector_create_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_connector_create_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1485
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_connector_create_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1486
    .local v0, "vx_message_base_t2vx_resp_connector_create_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_connector_create_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_connector_create_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_connector_get_local_audio_info_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_connector_get_local_audio_info_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1490
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_connector_get_local_audio_info_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1491
    .local v0, "vx_message_base_t2vx_resp_connector_get_local_audio_info_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_connector_get_local_audio_info_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_connector_get_local_audio_info_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_connector_initiate_shutdown_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_connector_initiate_shutdown_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1495
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_connector_initiate_shutdown_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1496
    .local v0, "vx_message_base_t2vx_resp_connector_initiate_shutdown_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_connector_initiate_shutdown_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_connector_initiate_shutdown_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_connector_mute_local_mic_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_connector_mute_local_mic_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1500
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_connector_mute_local_mic_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1501
    .local v0, "vx_message_base_t2vx_resp_connector_mute_local_mic_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_connector_mute_local_mic_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_connector_mute_local_mic_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_connector_mute_local_speaker_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_connector_mute_local_speaker_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1505
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_connector_mute_local_speaker_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1506
    .local v0, "vx_message_base_t2vx_resp_connector_mute_local_speaker_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_connector_mute_local_speaker_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_connector_mute_local_speaker_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_connector_set_local_mic_volume_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_connector_set_local_mic_volume_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1510
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_connector_set_local_mic_volume_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1511
    .local v0, "vx_message_base_t2vx_resp_connector_set_local_mic_volume_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_connector_set_local_mic_volume_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_connector_set_local_mic_volume_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_connector_set_local_speaker_volume_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_connector_set_local_speaker_volume_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1515
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_connector_set_local_speaker_volume_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1516
    .local v0, "vx_message_base_t2vx_resp_connector_set_local_speaker_volume_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_connector_set_local_speaker_volume_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_connector_set_local_speaker_volume_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_session_channel_invite_user_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_session_channel_invite_user_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1520
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_session_channel_invite_user_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1521
    .local v0, "vx_message_base_t2vx_resp_session_channel_invite_user_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_session_channel_invite_user_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_session_channel_invite_user_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_session_create_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_session_create_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1525
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_session_create_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1526
    .local v0, "vx_message_base_t2vx_resp_session_create_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_session_create_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_session_create_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_session_media_connect_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_session_media_connect_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1530
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_session_media_connect_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1531
    .local v0, "vx_message_base_t2vx_resp_session_media_connect_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_session_media_connect_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_session_media_connect_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_session_media_disconnect_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_session_media_disconnect_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1535
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_session_media_disconnect_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1536
    .local v0, "vx_message_base_t2vx_resp_session_media_disconnect_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_session_media_disconnect_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_session_media_disconnect_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_session_mute_local_speaker_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_session_mute_local_speaker_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1540
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_session_mute_local_speaker_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1541
    .local v0, "vx_message_base_t2vx_resp_session_mute_local_speaker_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_session_mute_local_speaker_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_session_mute_local_speaker_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_session_send_dtmf_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_session_send_dtmf_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1545
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_session_send_dtmf_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1546
    .local v0, "vx_message_base_t2vx_resp_session_send_dtmf_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_session_send_dtmf_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_session_send_dtmf_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_session_send_message_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_session_send_message_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1550
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_session_send_message_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1551
    .local v0, "vx_message_base_t2vx_resp_session_send_message_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_session_send_message_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_session_send_message_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_session_send_notification_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_session_send_notification_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1555
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_session_send_notification_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1556
    .local v0, "vx_message_base_t2vx_resp_session_send_notification_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_session_send_notification_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_session_send_notification_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_session_set_3d_position_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_session_set_3d_position_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1560
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_session_set_3d_position_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1561
    .local v0, "vx_message_base_t2vx_resp_session_set_3d_position_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_session_set_3d_position_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_session_set_3d_position_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_session_set_local_speaker_volume_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_session_set_local_speaker_volume_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1565
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_session_set_local_speaker_volume_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1566
    .local v0, "vx_message_base_t2vx_resp_session_set_local_speaker_volume_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_session_set_local_speaker_volume_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_session_set_local_speaker_volume_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_session_set_participant_mute_for_me_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_session_set_participant_mute_for_me_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1570
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_session_set_participant_mute_for_me_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1571
    .local v0, "vx_message_base_t2vx_resp_session_set_participant_mute_for_me_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_session_set_participant_mute_for_me_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_session_set_participant_mute_for_me_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_session_set_participant_volume_for_me_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_session_set_participant_volume_for_me_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1575
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_session_set_participant_volume_for_me_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1576
    .local v0, "vx_message_base_t2vx_resp_session_set_participant_volume_for_me_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_session_set_participant_volume_for_me_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_session_set_participant_volume_for_me_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_session_set_voice_font_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_session_set_voice_font_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1580
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_session_set_voice_font_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1581
    .local v0, "vx_message_base_t2vx_resp_session_set_voice_font_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_session_set_voice_font_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_session_set_voice_font_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_session_terminate_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_session_terminate_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1585
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_session_terminate_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1586
    .local v0, "vx_message_base_t2vx_resp_session_terminate_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_session_terminate_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_session_terminate_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_session_text_connect_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_session_text_connect_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1590
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_session_text_connect_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1591
    .local v0, "vx_message_base_t2vx_resp_session_text_connect_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_session_text_connect_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_session_text_connect_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_session_text_disconnect_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_session_text_disconnect_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1595
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_session_text_disconnect_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1596
    .local v0, "vx_message_base_t2vx_resp_session_text_disconnect_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_session_text_disconnect_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_session_text_disconnect_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_sessiongroup_add_session_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_sessiongroup_add_session_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1600
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_sessiongroup_add_session_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1601
    .local v0, "vx_message_base_t2vx_resp_sessiongroup_add_session_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_sessiongroup_add_session_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_sessiongroup_add_session_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_sessiongroup_control_audio_injection_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_sessiongroup_control_audio_injection_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1605
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_sessiongroup_control_audio_injection_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1606
    .local v0, "vx_message_base_t2vx_resp_sessiongroup_control_audio_injection_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_sessiongroup_control_audio_injection_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_sessiongroup_control_audio_injection_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_sessiongroup_control_playback_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_sessiongroup_control_playback_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1610
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_sessiongroup_control_playback_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1611
    .local v0, "vx_message_base_t2vx_resp_sessiongroup_control_playback_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_sessiongroup_control_playback_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_sessiongroup_control_playback_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_sessiongroup_control_recording_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_sessiongroup_control_recording_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1615
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_sessiongroup_control_recording_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1616
    .local v0, "vx_message_base_t2vx_resp_sessiongroup_control_recording_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_sessiongroup_control_recording_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_sessiongroup_control_recording_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_sessiongroup_create_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_sessiongroup_create_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1620
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_sessiongroup_create_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1621
    .local v0, "vx_message_base_t2vx_resp_sessiongroup_create_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_sessiongroup_create_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_sessiongroup_create_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_sessiongroup_get_stats_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_sessiongroup_get_stats_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1625
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_sessiongroup_get_stats_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1626
    .local v0, "vx_message_base_t2vx_resp_sessiongroup_get_stats_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_sessiongroup_get_stats_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_sessiongroup_get_stats_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_sessiongroup_remove_session_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_sessiongroup_remove_session_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1630
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_sessiongroup_remove_session_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1631
    .local v0, "vx_message_base_t2vx_resp_sessiongroup_remove_session_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_sessiongroup_remove_session_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_sessiongroup_remove_session_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_sessiongroup_reset_focus_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_sessiongroup_reset_focus_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1635
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_sessiongroup_reset_focus_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1636
    .local v0, "vx_message_base_t2vx_resp_sessiongroup_reset_focus_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_sessiongroup_reset_focus_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_sessiongroup_reset_focus_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_sessiongroup_set_focus_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_sessiongroup_set_focus_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1640
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_sessiongroup_set_focus_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1641
    .local v0, "vx_message_base_t2vx_resp_sessiongroup_set_focus_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_sessiongroup_set_focus_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_sessiongroup_set_focus_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_sessiongroup_set_playback_options_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_sessiongroup_set_playback_options_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1645
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_sessiongroup_set_playback_options_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1646
    .local v0, "vx_message_base_t2vx_resp_sessiongroup_set_playback_options_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_sessiongroup_set_playback_options_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_sessiongroup_set_playback_options_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_sessiongroup_set_session_3d_position_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_sessiongroup_set_session_3d_position_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1650
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_sessiongroup_set_session_3d_position_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1651
    .local v0, "vx_message_base_t2vx_resp_sessiongroup_set_session_3d_position_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_sessiongroup_set_session_3d_position_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_sessiongroup_set_session_3d_position_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_sessiongroup_set_tx_all_sessions_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_sessiongroup_set_tx_all_sessions_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1655
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_sessiongroup_set_tx_all_sessions_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1656
    .local v0, "vx_message_base_t2vx_resp_sessiongroup_set_tx_all_sessions_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_sessiongroup_set_tx_all_sessions_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_sessiongroup_set_tx_all_sessions_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_sessiongroup_set_tx_no_session_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_sessiongroup_set_tx_no_session_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1660
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_sessiongroup_set_tx_no_session_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1661
    .local v0, "vx_message_base_t2vx_resp_sessiongroup_set_tx_no_session_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_sessiongroup_set_tx_no_session_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_sessiongroup_set_tx_no_session_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_sessiongroup_set_tx_session_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_sessiongroup_set_tx_session_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1665
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_sessiongroup_set_tx_session_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1666
    .local v0, "vx_message_base_t2vx_resp_sessiongroup_set_tx_session_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_sessiongroup_set_tx_session_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_sessiongroup_set_tx_session_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_sessiongroup_terminate_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_sessiongroup_terminate_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1670
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_sessiongroup_terminate_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1671
    .local v0, "vx_message_base_t2vx_resp_sessiongroup_terminate_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_sessiongroup_terminate_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_sessiongroup_terminate_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_message_base_t2vx_resp_sessiongroup_unset_focus_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_resp_sessiongroup_unset_focus_t;
    .locals 4
    .param p0, "com_vivox_service_vx_message_base_t"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 1675
    invoke-static {p0}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_message_base_t2vx_resp_sessiongroup_unset_focus_t(JLcom/vivox/service/vx_message_base_t;)J

    move-result-wide v0

    .line 1676
    .local v0, "vx_message_base_t2vx_resp_sessiongroup_unset_focus_t":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_resp_sessiongroup_unset_focus_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_resp_sessiongroup_unset_focus_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_name_value_pair_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_name_value_pair;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_name_value_pair"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_name_value_pair;

    .prologue
    .line 1680
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_name_value_pair;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_name_value_pair;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_name_value_pair_create(J)V

    .line 1681
    return-void
.end method

.method public static vx_name_value_pair_free(Lcom/vivox/service/vx_name_value_pair_t;)V
    .locals 2
    .param p0, "com_vivox_service_vx_name_value_pair_t"    # Lcom/vivox/service/vx_name_value_pair_t;

    .prologue
    .line 1684
    invoke-static {p0}, Lcom/vivox/service/vx_name_value_pair_t;->getCPtr(Lcom/vivox/service/vx_name_value_pair_t;)J

    move-result-wide v0

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_name_value_pair_free(JLcom/vivox/service/vx_name_value_pair_t;)V

    .line 1685
    return-void
.end method

.method public static vx_name_value_pairs_create(ILcom/vivox/service/SWIGTYPE_p_p_p_vx_name_value_pair;)V
    .locals 2
    .param p0, "i"    # I
    .param p1, "sWIGTYPE_p_p_p_vx_name_value_pair"    # Lcom/vivox/service/SWIGTYPE_p_p_p_vx_name_value_pair;

    .prologue
    .line 1688
    invoke-static {p1}, Lcom/vivox/service/SWIGTYPE_p_p_p_vx_name_value_pair;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_p_vx_name_value_pair;)J

    move-result-wide v0

    invoke-static {p0, v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_name_value_pairs_create(IJ)V

    .line 1689
    return-void
.end method

.method public static vx_name_value_pairs_free(Lcom/vivox/service/SWIGTYPE_p_p_vx_name_value_pair;I)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_name_value_pair"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_name_value_pair;
    .param p1, "i"    # I

    .prologue
    .line 1692
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_name_value_pair;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_name_value_pair;)J

    move-result-wide v0

    invoke-static {v0, v1, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_name_value_pairs_free(JI)V

    .line 1693
    return-void
.end method

.method public static vx_name_value_pairs_t_get_list_item(Lcom/vivox/service/SWIGTYPE_p_p_vx_name_value_pair;I)Lcom/vivox/service/vx_name_value_pair_t;
    .locals 4
    .param p0, "sWIGTYPE_p_p_vx_name_value_pair"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_name_value_pair;
    .param p1, "i"    # I

    .prologue
    .line 1696
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_name_value_pair;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_name_value_pair;)J

    move-result-wide v2

    invoke-static {v2, v3, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_name_value_pairs_t_get_list_item(JI)J

    move-result-wide v0

    .line 1697
    .local v0, "vx_name_value_pairs_t_get_list_item":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_name_value_pair_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_name_value_pair_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_name_value_pairs_t_set_list_item(Lcom/vivox/service/SWIGTYPE_p_p_vx_name_value_pair;ILcom/vivox/service/vx_name_value_pair_t;)V
    .locals 6
    .param p0, "sWIGTYPE_p_p_vx_name_value_pair"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_name_value_pair;
    .param p1, "i"    # I
    .param p2, "com_vivox_service_vx_name_value_pair_t"    # Lcom/vivox/service/vx_name_value_pair_t;

    .prologue
    .line 1701
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_name_value_pair;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_name_value_pair;)J

    move-result-wide v0

    invoke-static {p2}, Lcom/vivox/service/vx_name_value_pair_t;->getCPtr(Lcom/vivox/service/vx_name_value_pair_t;)J

    move-result-wide v3

    move v2, p1

    move-object v5, p2

    invoke-static/range {v0 .. v5}, Lcom/vivox/service/VxClientProxyJNI;->vx_name_value_pairs_t_set_list_item(JIJLcom/vivox/service/vx_name_value_pair_t;)V

    .line 1702
    return-void
.end method

.method public static vx_on_application_exit()V
    .locals 0

    .prologue
    .line 1705
    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->vx_on_application_exit()V

    .line 1706
    return-void
.end method

.method public static vx_participant_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_participant;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_participant"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_participant;

    .prologue
    .line 1709
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_participant;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_participant;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_participant_create(J)V

    .line 1710
    return-void
.end method

.method public static vx_participant_free(Lcom/vivox/service/vx_participant_t;)V
    .locals 2
    .param p0, "com_vivox_service_vx_participant_t"    # Lcom/vivox/service/vx_participant_t;

    .prologue
    .line 1713
    invoke-static {p0}, Lcom/vivox/service/vx_participant_t;->getCPtr(Lcom/vivox/service/vx_participant_t;)J

    move-result-wide v0

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_participant_free(JLcom/vivox/service/vx_participant_t;)V

    .line 1714
    return-void
.end method

.method public static vx_participant_list_create(ILcom/vivox/service/SWIGTYPE_p_p_p_vx_participant;)V
    .locals 2
    .param p0, "i"    # I
    .param p1, "sWIGTYPE_p_p_p_vx_participant"    # Lcom/vivox/service/SWIGTYPE_p_p_p_vx_participant;

    .prologue
    .line 1717
    invoke-static {p1}, Lcom/vivox/service/SWIGTYPE_p_p_p_vx_participant;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_p_vx_participant;)J

    move-result-wide v0

    invoke-static {p0, v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_participant_list_create(IJ)V

    .line 1718
    return-void
.end method

.method public static vx_participant_list_free(Lcom/vivox/service/SWIGTYPE_p_p_vx_participant;I)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_participant"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_participant;
    .param p1, "i"    # I

    .prologue
    .line 1721
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_participant;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_participant;)J

    move-result-wide v0

    invoke-static {v0, v1, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_participant_list_free(JI)V

    .line 1722
    return-void
.end method

.method public static vx_read_crash_dump(I)Ljava/lang/String;
    .locals 1
    .param p0, "i"    # I

    .prologue
    .line 1725
    invoke-static {p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_read_crash_dump(I)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public static vx_recording_frame_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_recording_frame;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_recording_frame"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_recording_frame;

    .prologue
    .line 1729
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_recording_frame;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_recording_frame;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_recording_frame_create(J)V

    .line 1730
    return-void
.end method

.method public static vx_recording_frame_free(Lcom/vivox/service/vx_recording_frame_t;)V
    .locals 2
    .param p0, "com_vivox_service_vx_recording_frame_t"    # Lcom/vivox/service/vx_recording_frame_t;

    .prologue
    .line 1733
    invoke-static {p0}, Lcom/vivox/service/vx_recording_frame_t;->getCPtr(Lcom/vivox/service/vx_recording_frame_t;)J

    move-result-wide v0

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_recording_frame_free(JLcom/vivox/service/vx_recording_frame_t;)V

    .line 1734
    return-void
.end method

.method public static vx_recording_frame_list_create(ILcom/vivox/service/SWIGTYPE_p_p_p_vx_recording_frame;)V
    .locals 2
    .param p0, "i"    # I
    .param p1, "sWIGTYPE_p_p_p_vx_recording_frame"    # Lcom/vivox/service/SWIGTYPE_p_p_p_vx_recording_frame;

    .prologue
    .line 1737
    invoke-static {p1}, Lcom/vivox/service/SWIGTYPE_p_p_p_vx_recording_frame;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_p_vx_recording_frame;)J

    move-result-wide v0

    invoke-static {p0, v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_recording_frame_list_create(IJ)V

    .line 1738
    return-void
.end method

.method public static vx_recording_frame_list_free(Lcom/vivox/service/SWIGTYPE_p_p_vx_recording_frame;I)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_recording_frame"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_recording_frame;
    .param p1, "i"    # I

    .prologue
    .line 1741
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_recording_frame;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_recording_frame;)J

    move-result-wide v0

    invoke-static {v0, v1, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_recording_frame_list_free(JI)V

    .line 1742
    return-void
.end method

.method public static vx_register_logging_initialization(Lcom/vivox/service/vx_log_type;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ILcom/vivox/service/SWIGTYPE_p_f_p_q_const__char_p_q_const__char_p_q_const__char__void;)V
    .locals 8
    .param p0, "com_vivox_service_vx_log_type"    # Lcom/vivox/service/vx_log_type;
    .param p1, "str"    # Ljava/lang/String;
    .param p2, "str2"    # Ljava/lang/String;
    .param p3, "str3"    # Ljava/lang/String;
    .param p4, "i"    # I
    .param p5, "sWIGTYPE_p_f_p_q_const__char_p_q_const__char_p_q_const__char__void"    # Lcom/vivox/service/SWIGTYPE_p_f_p_q_const__char_p_q_const__char_p_q_const__char__void;

    .prologue
    .line 1745
    invoke-virtual {p0}, Lcom/vivox/service/vx_log_type;->swigValue()I

    move-result v1

    invoke-static {p5}, Lcom/vivox/service/SWIGTYPE_p_f_p_q_const__char_p_q_const__char_p_q_const__char__void;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_f_p_q_const__char_p_q_const__char_p_q_const__char__void;)J

    move-result-wide v6

    move-object v2, p1

    move-object v3, p2

    move-object v4, p3

    move v5, p4

    invoke-static/range {v1 .. v7}, Lcom/vivox/service/VxClientProxyJNI;->vx_register_logging_initialization(ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;IJ)V

    .line 1746
    return-void
.end method

.method public static vx_register_message_notification_handler(Lcom/vivox/service/SWIGTYPE_p_f_p_void__void;Lcom/vivox/service/SWIGTYPE_p_void;)V
    .locals 4
    .param p0, "sWIGTYPE_p_f_p_void__void"    # Lcom/vivox/service/SWIGTYPE_p_f_p_void__void;
    .param p1, "sWIGTYPE_p_void"    # Lcom/vivox/service/SWIGTYPE_p_void;

    .prologue
    .line 1749
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_f_p_void__void;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_f_p_void__void;)J

    move-result-wide v0

    invoke-static {p1}, Lcom/vivox/service/SWIGTYPE_p_void;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_void;)J

    move-result-wide v2

    invoke-static {v0, v1, v2, v3}, Lcom/vivox/service/VxClientProxyJNI;->vx_register_message_notification_handler(JJ)V

    .line 1750
    return-void
.end method

.method public static vx_req_account_anonymous_login_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_anonymous_login;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_account_anonymous_login"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_anonymous_login;

    .prologue
    .line 1753
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_anonymous_login;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_anonymous_login;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_account_anonymous_login_create(J)V

    .line 1754
    return-void
.end method

.method public static vx_req_account_authtoken_login_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_authtoken_login;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_account_authtoken_login"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_authtoken_login;

    .prologue
    .line 1757
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_authtoken_login;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_authtoken_login;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_account_authtoken_login_create(J)V

    .line 1758
    return-void
.end method

.method public static vx_req_account_buddy_delete_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_buddy_delete;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_account_buddy_delete"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_buddy_delete;

    .prologue
    .line 1761
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_buddy_delete;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_buddy_delete;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_account_buddy_delete_create(J)V

    .line 1762
    return-void
.end method

.method public static vx_req_account_buddy_search_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_buddy_search;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_account_buddy_search"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_buddy_search;

    .prologue
    .line 1765
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_buddy_search;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_buddy_search;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_account_buddy_search_create(J)V

    .line 1766
    return-void
.end method

.method public static vx_req_account_buddy_set_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_buddy_set;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_account_buddy_set"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_buddy_set;

    .prologue
    .line 1769
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_buddy_set;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_buddy_set;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_account_buddy_set_create(J)V

    .line 1770
    return-void
.end method

.method public static vx_req_account_buddygroup_delete_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_buddygroup_delete;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_account_buddygroup_delete"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_buddygroup_delete;

    .prologue
    .line 1773
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_buddygroup_delete;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_buddygroup_delete;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_account_buddygroup_delete_create(J)V

    .line 1774
    return-void
.end method

.method public static vx_req_account_buddygroup_set_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_buddygroup_set;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_account_buddygroup_set"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_buddygroup_set;

    .prologue
    .line 1777
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_buddygroup_set;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_buddygroup_set;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_account_buddygroup_set_create(J)V

    .line 1778
    return-void
.end method

.method public static vx_req_account_channel_add_acl_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_channel_add_acl;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_account_channel_add_acl"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_channel_add_acl;

    .prologue
    .line 1781
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_channel_add_acl;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_channel_add_acl;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_account_channel_add_acl_create(J)V

    .line 1782
    return-void
.end method

.method public static vx_req_account_channel_add_moderator_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_channel_add_moderator;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_account_channel_add_moderator"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_channel_add_moderator;

    .prologue
    .line 1785
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_channel_add_moderator;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_channel_add_moderator;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_account_channel_add_moderator_create(J)V

    .line 1786
    return-void
.end method

.method public static vx_req_account_channel_change_owner_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_channel_change_owner;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_account_channel_change_owner"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_channel_change_owner;

    .prologue
    .line 1789
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_channel_change_owner;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_channel_change_owner;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_account_channel_change_owner_create(J)V

    .line 1790
    return-void
.end method

.method public static vx_req_account_channel_create_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_channel_create;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_account_channel_create"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_channel_create;

    .prologue
    .line 1793
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_channel_create;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_channel_create;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_account_channel_create_create(J)V

    .line 1794
    return-void
.end method

.method public static vx_req_account_channel_delete_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_channel_delete;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_account_channel_delete"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_channel_delete;

    .prologue
    .line 1797
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_channel_delete;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_channel_delete;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_account_channel_delete_create(J)V

    .line 1798
    return-void
.end method

.method public static vx_req_account_channel_favorite_delete_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_channel_favorite_delete;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_account_channel_favorite_delete"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_channel_favorite_delete;

    .prologue
    .line 1801
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_channel_favorite_delete;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_channel_favorite_delete;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_account_channel_favorite_delete_create(J)V

    .line 1802
    return-void
.end method

.method public static vx_req_account_channel_favorite_group_delete_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_channel_favorite_group_delete;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_account_channel_favorite_group_delete"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_channel_favorite_group_delete;

    .prologue
    .line 1805
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_channel_favorite_group_delete;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_channel_favorite_group_delete;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_account_channel_favorite_group_delete_create(J)V

    .line 1806
    return-void
.end method

.method public static vx_req_account_channel_favorite_group_set_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_channel_favorite_group_set;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_account_channel_favorite_group_set"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_channel_favorite_group_set;

    .prologue
    .line 1809
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_channel_favorite_group_set;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_channel_favorite_group_set;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_account_channel_favorite_group_set_create(J)V

    .line 1810
    return-void
.end method

.method public static vx_req_account_channel_favorite_set_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_channel_favorite_set;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_account_channel_favorite_set"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_channel_favorite_set;

    .prologue
    .line 1813
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_channel_favorite_set;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_channel_favorite_set;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_account_channel_favorite_set_create(J)V

    .line 1814
    return-void
.end method

.method public static vx_req_account_channel_favorites_get_list_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_channel_favorites_get_list;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_account_channel_favorites_get_list"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_channel_favorites_get_list;

    .prologue
    .line 1817
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_channel_favorites_get_list;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_channel_favorites_get_list;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_account_channel_favorites_get_list_create(J)V

    .line 1818
    return-void
.end method

.method public static vx_req_account_channel_get_acl_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_channel_get_acl;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_account_channel_get_acl"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_channel_get_acl;

    .prologue
    .line 1821
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_channel_get_acl;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_channel_get_acl;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_account_channel_get_acl_create(J)V

    .line 1822
    return-void
.end method

.method public static vx_req_account_channel_get_info_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_channel_get_info;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_account_channel_get_info"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_channel_get_info;

    .prologue
    .line 1825
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_channel_get_info;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_channel_get_info;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_account_channel_get_info_create(J)V

    .line 1826
    return-void
.end method

.method public static vx_req_account_channel_get_moderators_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_channel_get_moderators;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_account_channel_get_moderators"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_channel_get_moderators;

    .prologue
    .line 1829
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_channel_get_moderators;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_channel_get_moderators;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_account_channel_get_moderators_create(J)V

    .line 1830
    return-void
.end method

.method public static vx_req_account_channel_get_participants_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_channel_get_participants;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_account_channel_get_participants"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_channel_get_participants;

    .prologue
    .line 1833
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_channel_get_participants;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_channel_get_participants;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_account_channel_get_participants_create(J)V

    .line 1834
    return-void
.end method

.method public static vx_req_account_channel_remove_acl_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_channel_remove_acl;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_account_channel_remove_acl"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_channel_remove_acl;

    .prologue
    .line 1837
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_channel_remove_acl;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_channel_remove_acl;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_account_channel_remove_acl_create(J)V

    .line 1838
    return-void
.end method

.method public static vx_req_account_channel_remove_moderator_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_channel_remove_moderator;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_account_channel_remove_moderator"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_channel_remove_moderator;

    .prologue
    .line 1841
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_channel_remove_moderator;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_channel_remove_moderator;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_account_channel_remove_moderator_create(J)V

    .line 1842
    return-void
.end method

.method public static vx_req_account_channel_search_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_channel_search;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_account_channel_search"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_channel_search;

    .prologue
    .line 1845
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_channel_search;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_channel_search;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_account_channel_search_create(J)V

    .line 1846
    return-void
.end method

.method public static vx_req_account_channel_update_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_channel_update;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_account_channel_update"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_channel_update;

    .prologue
    .line 1849
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_channel_update;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_channel_update;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_account_channel_update_create(J)V

    .line 1850
    return-void
.end method

.method public static vx_req_account_create_auto_accept_rule_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_create_auto_accept_rule;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_account_create_auto_accept_rule"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_create_auto_accept_rule;

    .prologue
    .line 1853
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_create_auto_accept_rule;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_create_auto_accept_rule;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_account_create_auto_accept_rule_create(J)V

    .line 1854
    return-void
.end method

.method public static vx_req_account_create_block_rule_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_create_block_rule;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_account_create_block_rule"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_create_block_rule;

    .prologue
    .line 1857
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_create_block_rule;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_create_block_rule;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_account_create_block_rule_create(J)V

    .line 1858
    return-void
.end method

.method public static vx_req_account_delete_auto_accept_rule_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_delete_auto_accept_rule;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_account_delete_auto_accept_rule"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_delete_auto_accept_rule;

    .prologue
    .line 1861
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_delete_auto_accept_rule;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_delete_auto_accept_rule;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_account_delete_auto_accept_rule_create(J)V

    .line 1862
    return-void
.end method

.method public static vx_req_account_delete_block_rule_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_delete_block_rule;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_account_delete_block_rule"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_delete_block_rule;

    .prologue
    .line 1865
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_delete_block_rule;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_delete_block_rule;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_account_delete_block_rule_create(J)V

    .line 1866
    return-void
.end method

.method public static vx_req_account_get_account_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_get_account;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_account_get_account"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_get_account;

    .prologue
    .line 1869
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_get_account;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_get_account;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_account_get_account_create(J)V

    .line 1870
    return-void
.end method

.method public static vx_req_account_get_session_fonts_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_get_session_fonts;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_account_get_session_fonts"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_get_session_fonts;

    .prologue
    .line 1873
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_get_session_fonts;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_get_session_fonts;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_account_get_session_fonts_create(J)V

    .line 1874
    return-void
.end method

.method public static vx_req_account_get_template_fonts_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_get_template_fonts;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_account_get_template_fonts"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_get_template_fonts;

    .prologue
    .line 1877
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_get_template_fonts;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_get_template_fonts;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_account_get_template_fonts_create(J)V

    .line 1878
    return-void
.end method

.method public static vx_req_account_list_auto_accept_rules_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_list_auto_accept_rules;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_account_list_auto_accept_rules"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_list_auto_accept_rules;

    .prologue
    .line 1881
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_list_auto_accept_rules;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_list_auto_accept_rules;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_account_list_auto_accept_rules_create(J)V

    .line 1882
    return-void
.end method

.method public static vx_req_account_list_block_rules_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_list_block_rules;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_account_list_block_rules"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_list_block_rules;

    .prologue
    .line 1885
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_list_block_rules;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_list_block_rules;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_account_list_block_rules_create(J)V

    .line 1886
    return-void
.end method

.method public static vx_req_account_list_buddies_and_groups_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_list_buddies_and_groups;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_account_list_buddies_and_groups"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_list_buddies_and_groups;

    .prologue
    .line 1889
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_list_buddies_and_groups;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_list_buddies_and_groups;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_account_list_buddies_and_groups_create(J)V

    .line 1890
    return-void
.end method

.method public static vx_req_account_login_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_login;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_account_login"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_login;

    .prologue
    .line 1893
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_login;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_login;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_account_login_create(J)V

    .line 1894
    return-void
.end method

.method public static vx_req_account_logout_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_logout;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_account_logout"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_logout;

    .prologue
    .line 1897
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_logout;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_logout;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_account_logout_create(J)V

    .line 1898
    return-void
.end method

.method public static vx_req_account_post_crash_dump_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_post_crash_dump;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_account_post_crash_dump"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_post_crash_dump;

    .prologue
    .line 1901
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_post_crash_dump;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_post_crash_dump;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_account_post_crash_dump_create(J)V

    .line 1902
    return-void
.end method

.method public static vx_req_account_send_message_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_send_message;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_account_send_message"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_send_message;

    .prologue
    .line 1905
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_send_message;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_send_message;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_account_send_message_create(J)V

    .line 1906
    return-void
.end method

.method public static vx_req_account_send_sms_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_send_sms;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_account_send_sms"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_send_sms;

    .prologue
    .line 1909
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_send_sms;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_send_sms;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_account_send_sms_create(J)V

    .line 1910
    return-void
.end method

.method public static vx_req_account_send_subscription_reply_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_send_subscription_reply;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_account_send_subscription_reply"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_send_subscription_reply;

    .prologue
    .line 1913
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_send_subscription_reply;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_send_subscription_reply;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_account_send_subscription_reply_create(J)V

    .line 1914
    return-void
.end method

.method public static vx_req_account_send_user_app_data_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_send_user_app_data;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_account_send_user_app_data"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_send_user_app_data;

    .prologue
    .line 1917
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_send_user_app_data;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_send_user_app_data;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_account_send_user_app_data_create(J)V

    .line 1918
    return-void
.end method

.method public static vx_req_account_set_login_properties_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_set_login_properties;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_account_set_login_properties"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_set_login_properties;

    .prologue
    .line 1921
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_set_login_properties;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_set_login_properties;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_account_set_login_properties_create(J)V

    .line 1922
    return-void
.end method

.method public static vx_req_account_set_presence_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_set_presence;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_account_set_presence"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_set_presence;

    .prologue
    .line 1925
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_set_presence;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_set_presence;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_account_set_presence_create(J)V

    .line 1926
    return-void
.end method

.method public static vx_req_account_update_account_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_update_account;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_account_update_account"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_update_account;

    .prologue
    .line 1929
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_update_account;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_update_account;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_account_update_account_create(J)V

    .line 1930
    return-void
.end method

.method public static vx_req_account_web_call_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_web_call;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_account_web_call"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_web_call;

    .prologue
    .line 1933
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_web_call;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_account_web_call;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_account_web_call_create(J)V

    .line 1934
    return-void
.end method

.method public static vx_req_aux_capture_audio_start_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_capture_audio_start;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_aux_capture_audio_start"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_capture_audio_start;

    .prologue
    .line 1937
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_capture_audio_start;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_capture_audio_start;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_aux_capture_audio_start_create(J)V

    .line 1938
    return-void
.end method

.method public static vx_req_aux_capture_audio_stop_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_capture_audio_stop;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_aux_capture_audio_stop"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_capture_audio_stop;

    .prologue
    .line 1941
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_capture_audio_stop;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_capture_audio_stop;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_aux_capture_audio_stop_create(J)V

    .line 1942
    return-void
.end method

.method public static vx_req_aux_connectivity_info_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_connectivity_info;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_aux_connectivity_info"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_connectivity_info;

    .prologue
    .line 1945
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_connectivity_info;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_connectivity_info;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_aux_connectivity_info_create(J)V

    .line 1946
    return-void
.end method

.method public static vx_req_aux_create_account_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_create_account;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_aux_create_account"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_create_account;

    .prologue
    .line 1949
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_create_account;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_create_account;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_aux_create_account_create(J)V

    .line 1950
    return-void
.end method

.method public static vx_req_aux_deactivate_account_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_deactivate_account;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_aux_deactivate_account"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_deactivate_account;

    .prologue
    .line 1953
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_deactivate_account;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_deactivate_account;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_aux_deactivate_account_create(J)V

    .line 1954
    return-void
.end method

.method public static vx_req_aux_diagnostic_state_dump_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_diagnostic_state_dump;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_aux_diagnostic_state_dump"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_diagnostic_state_dump;

    .prologue
    .line 1957
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_diagnostic_state_dump;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_diagnostic_state_dump;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_aux_diagnostic_state_dump_create(J)V

    .line 1958
    return-void
.end method

.method public static vx_req_aux_get_capture_devices_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_get_capture_devices;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_aux_get_capture_devices"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_get_capture_devices;

    .prologue
    .line 1961
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_get_capture_devices;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_get_capture_devices;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_aux_get_capture_devices_create(J)V

    .line 1962
    return-void
.end method

.method public static vx_req_aux_get_mic_level_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_get_mic_level;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_aux_get_mic_level"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_get_mic_level;

    .prologue
    .line 1965
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_get_mic_level;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_get_mic_level;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_aux_get_mic_level_create(J)V

    .line 1966
    return-void
.end method

.method public static vx_req_aux_get_render_devices_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_get_render_devices;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_aux_get_render_devices"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_get_render_devices;

    .prologue
    .line 1969
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_get_render_devices;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_get_render_devices;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_aux_get_render_devices_create(J)V

    .line 1970
    return-void
.end method

.method public static vx_req_aux_get_speaker_level_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_get_speaker_level;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_aux_get_speaker_level"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_get_speaker_level;

    .prologue
    .line 1973
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_get_speaker_level;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_get_speaker_level;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_aux_get_speaker_level_create(J)V

    .line 1974
    return-void
.end method

.method public static vx_req_aux_get_vad_properties_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_get_vad_properties;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_aux_get_vad_properties"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_get_vad_properties;

    .prologue
    .line 1977
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_get_vad_properties;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_get_vad_properties;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_aux_get_vad_properties_create(J)V

    .line 1978
    return-void
.end method

.method public static vx_req_aux_global_monitor_keyboard_mouse_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_global_monitor_keyboard_mouse;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_aux_global_monitor_keyboard_mouse"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_global_monitor_keyboard_mouse;

    .prologue
    .line 1981
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_global_monitor_keyboard_mouse;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_global_monitor_keyboard_mouse;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_aux_global_monitor_keyboard_mouse_create(J)V

    .line 1982
    return-void
.end method

.method public static vx_req_aux_global_monitor_keyboard_mouse_t_set_codes_item(Lcom/vivox/service/vx_req_aux_global_monitor_keyboard_mouse_t;II)V
    .locals 2
    .param p0, "com_vivox_service_vx_req_aux_global_monitor_keyboard_mouse_t"    # Lcom/vivox/service/vx_req_aux_global_monitor_keyboard_mouse_t;
    .param p1, "i"    # I
    .param p2, "i2"    # I

    .prologue
    .line 1985
    invoke-static {p0}, Lcom/vivox/service/vx_req_aux_global_monitor_keyboard_mouse_t;->getCPtr(Lcom/vivox/service/vx_req_aux_global_monitor_keyboard_mouse_t;)J

    move-result-wide v0

    invoke-static {v0, v1, p0, p1, p2}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_aux_global_monitor_keyboard_mouse_t_set_codes_item(JLcom/vivox/service/vx_req_aux_global_monitor_keyboard_mouse_t;II)V

    .line 1986
    return-void
.end method

.method public static vx_req_aux_notify_application_state_change_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_notify_application_state_change;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_aux_notify_application_state_change"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_notify_application_state_change;

    .prologue
    .line 1989
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_notify_application_state_change;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_notify_application_state_change;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_aux_notify_application_state_change_create(J)V

    .line 1990
    return-void
.end method

.method public static vx_req_aux_play_audio_buffer_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_play_audio_buffer;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_aux_play_audio_buffer"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_play_audio_buffer;

    .prologue
    .line 1993
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_play_audio_buffer;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_play_audio_buffer;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_aux_play_audio_buffer_create(J)V

    .line 1994
    return-void
.end method

.method public static vx_req_aux_reactivate_account_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_reactivate_account;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_aux_reactivate_account"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_reactivate_account;

    .prologue
    .line 1997
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_reactivate_account;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_reactivate_account;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_aux_reactivate_account_create(J)V

    .line 1998
    return-void
.end method

.method public static vx_req_aux_render_audio_modify_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_render_audio_modify;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_aux_render_audio_modify"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_render_audio_modify;

    .prologue
    .line 2001
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_render_audio_modify;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_render_audio_modify;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_aux_render_audio_modify_create(J)V

    .line 2002
    return-void
.end method

.method public static vx_req_aux_render_audio_start_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_render_audio_start;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_aux_render_audio_start"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_render_audio_start;

    .prologue
    .line 2005
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_render_audio_start;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_render_audio_start;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_aux_render_audio_start_create(J)V

    .line 2006
    return-void
.end method

.method public static vx_req_aux_render_audio_stop_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_render_audio_stop;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_aux_render_audio_stop"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_render_audio_stop;

    .prologue
    .line 2009
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_render_audio_stop;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_render_audio_stop;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_aux_render_audio_stop_create(J)V

    .line 2010
    return-void
.end method

.method public static vx_req_aux_reset_password_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_reset_password;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_aux_reset_password"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_reset_password;

    .prologue
    .line 2013
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_reset_password;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_reset_password;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_aux_reset_password_create(J)V

    .line 2014
    return-void
.end method

.method public static vx_req_aux_set_capture_device_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_set_capture_device;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_aux_set_capture_device"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_set_capture_device;

    .prologue
    .line 2017
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_set_capture_device;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_set_capture_device;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_aux_set_capture_device_create(J)V

    .line 2018
    return-void
.end method

.method public static vx_req_aux_set_idle_timeout_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_set_idle_timeout;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_aux_set_idle_timeout"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_set_idle_timeout;

    .prologue
    .line 2021
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_set_idle_timeout;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_set_idle_timeout;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_aux_set_idle_timeout_create(J)V

    .line 2022
    return-void
.end method

.method public static vx_req_aux_set_mic_level_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_set_mic_level;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_aux_set_mic_level"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_set_mic_level;

    .prologue
    .line 2025
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_set_mic_level;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_set_mic_level;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_aux_set_mic_level_create(J)V

    .line 2026
    return-void
.end method

.method public static vx_req_aux_set_render_device_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_set_render_device;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_aux_set_render_device"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_set_render_device;

    .prologue
    .line 2029
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_set_render_device;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_set_render_device;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_aux_set_render_device_create(J)V

    .line 2030
    return-void
.end method

.method public static vx_req_aux_set_speaker_level_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_set_speaker_level;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_aux_set_speaker_level"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_set_speaker_level;

    .prologue
    .line 2033
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_set_speaker_level;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_set_speaker_level;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_aux_set_speaker_level_create(J)V

    .line 2034
    return-void
.end method

.method public static vx_req_aux_set_vad_properties_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_set_vad_properties;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_aux_set_vad_properties"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_set_vad_properties;

    .prologue
    .line 2037
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_set_vad_properties;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_set_vad_properties;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_aux_set_vad_properties_create(J)V

    .line 2038
    return-void
.end method

.method public static vx_req_aux_start_buffer_capture_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_start_buffer_capture;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_aux_start_buffer_capture"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_start_buffer_capture;

    .prologue
    .line 2041
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_start_buffer_capture;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_aux_start_buffer_capture;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_aux_start_buffer_capture_create(J)V

    .line 2042
    return-void
.end method

.method public static vx_req_channel_ban_user_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_channel_ban_user;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_channel_ban_user"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_channel_ban_user;

    .prologue
    .line 2045
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_channel_ban_user;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_channel_ban_user;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_channel_ban_user_create(J)V

    .line 2046
    return-void
.end method

.method public static vx_req_channel_get_banned_users_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_channel_get_banned_users;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_channel_get_banned_users"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_channel_get_banned_users;

    .prologue
    .line 2049
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_channel_get_banned_users;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_channel_get_banned_users;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_channel_get_banned_users_create(J)V

    .line 2050
    return-void
.end method

.method public static vx_req_channel_kick_user_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_channel_kick_user;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_channel_kick_user"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_channel_kick_user;

    .prologue
    .line 2053
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_channel_kick_user;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_channel_kick_user;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_channel_kick_user_create(J)V

    .line 2054
    return-void
.end method

.method public static vx_req_channel_mute_all_users_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_channel_mute_all_users;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_channel_mute_all_users"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_channel_mute_all_users;

    .prologue
    .line 2057
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_channel_mute_all_users;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_channel_mute_all_users;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_channel_mute_all_users_create(J)V

    .line 2058
    return-void
.end method

.method public static vx_req_channel_mute_user_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_channel_mute_user;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_channel_mute_user"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_channel_mute_user;

    .prologue
    .line 2061
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_channel_mute_user;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_channel_mute_user;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_channel_mute_user_create(J)V

    .line 2062
    return-void
.end method

.method public static vx_req_channel_set_lock_mode_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_channel_set_lock_mode;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_channel_set_lock_mode"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_channel_set_lock_mode;

    .prologue
    .line 2065
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_channel_set_lock_mode;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_channel_set_lock_mode;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_channel_set_lock_mode_create(J)V

    .line 2066
    return-void
.end method

.method public static vx_req_connector_create_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_connector_create;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_connector_create"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_connector_create;

    .prologue
    .line 2069
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_connector_create;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_connector_create;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_connector_create_create(J)V

    .line 2070
    return-void
.end method

.method public static vx_req_connector_get_local_audio_info_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_connector_get_local_audio_info;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_connector_get_local_audio_info"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_connector_get_local_audio_info;

    .prologue
    .line 2073
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_connector_get_local_audio_info;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_connector_get_local_audio_info;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_connector_get_local_audio_info_create(J)V

    .line 2074
    return-void
.end method

.method public static vx_req_connector_initiate_shutdown_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_connector_initiate_shutdown;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_connector_initiate_shutdown"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_connector_initiate_shutdown;

    .prologue
    .line 2077
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_connector_initiate_shutdown;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_connector_initiate_shutdown;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_connector_initiate_shutdown_create(J)V

    .line 2078
    return-void
.end method

.method public static vx_req_connector_mute_local_mic_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_connector_mute_local_mic;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_connector_mute_local_mic"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_connector_mute_local_mic;

    .prologue
    .line 2081
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_connector_mute_local_mic;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_connector_mute_local_mic;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_connector_mute_local_mic_create(J)V

    .line 2082
    return-void
.end method

.method public static vx_req_connector_mute_local_speaker_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_connector_mute_local_speaker;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_connector_mute_local_speaker"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_connector_mute_local_speaker;

    .prologue
    .line 2085
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_connector_mute_local_speaker;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_connector_mute_local_speaker;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_connector_mute_local_speaker_create(J)V

    .line 2086
    return-void
.end method

.method public static vx_req_connector_set_local_mic_volume_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_connector_set_local_mic_volume;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_connector_set_local_mic_volume"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_connector_set_local_mic_volume;

    .prologue
    .line 2089
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_connector_set_local_mic_volume;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_connector_set_local_mic_volume;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_connector_set_local_mic_volume_create(J)V

    .line 2090
    return-void
.end method

.method public static vx_req_connector_set_local_speaker_volume_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_connector_set_local_speaker_volume;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_connector_set_local_speaker_volume"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_connector_set_local_speaker_volume;

    .prologue
    .line 2093
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_connector_set_local_speaker_volume;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_connector_set_local_speaker_volume;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_connector_set_local_speaker_volume_create(J)V

    .line 2094
    return-void
.end method

.method public static vx_req_session_channel_invite_user_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_session_channel_invite_user;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_session_channel_invite_user"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_session_channel_invite_user;

    .prologue
    .line 2097
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_session_channel_invite_user;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_session_channel_invite_user;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_session_channel_invite_user_create(J)V

    .line 2098
    return-void
.end method

.method public static vx_req_session_create_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_session_create;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_session_create"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_session_create;

    .prologue
    .line 2101
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_session_create;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_session_create;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_session_create_create(J)V

    .line 2102
    return-void
.end method

.method public static vx_req_session_media_connect_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_session_media_connect;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_session_media_connect"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_session_media_connect;

    .prologue
    .line 2105
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_session_media_connect;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_session_media_connect;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_session_media_connect_create(J)V

    .line 2106
    return-void
.end method

.method public static vx_req_session_media_disconnect_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_session_media_disconnect;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_session_media_disconnect"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_session_media_disconnect;

    .prologue
    .line 2109
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_session_media_disconnect;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_session_media_disconnect;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_session_media_disconnect_create(J)V

    .line 2110
    return-void
.end method

.method public static vx_req_session_mute_local_speaker_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_session_mute_local_speaker;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_session_mute_local_speaker"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_session_mute_local_speaker;

    .prologue
    .line 2113
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_session_mute_local_speaker;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_session_mute_local_speaker;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_session_mute_local_speaker_create(J)V

    .line 2114
    return-void
.end method

.method public static vx_req_session_send_dtmf_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_session_send_dtmf;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_session_send_dtmf"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_session_send_dtmf;

    .prologue
    .line 2117
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_session_send_dtmf;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_session_send_dtmf;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_session_send_dtmf_create(J)V

    .line 2118
    return-void
.end method

.method public static vx_req_session_send_message_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_session_send_message;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_session_send_message"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_session_send_message;

    .prologue
    .line 2121
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_session_send_message;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_session_send_message;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_session_send_message_create(J)V

    .line 2122
    return-void
.end method

.method public static vx_req_session_send_notification_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_session_send_notification;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_session_send_notification"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_session_send_notification;

    .prologue
    .line 2125
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_session_send_notification;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_session_send_notification;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_session_send_notification_create(J)V

    .line 2126
    return-void
.end method

.method public static vx_req_session_set_3d_position_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_session_set_3d_position;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_session_set_3d_position"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_session_set_3d_position;

    .prologue
    .line 2129
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_session_set_3d_position;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_session_set_3d_position;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_session_set_3d_position_create(J)V

    .line 2130
    return-void
.end method

.method public static vx_req_session_set_3d_position_t_get_listener_at_orientation_item(Lcom/vivox/service/vx_req_session_set_3d_position_t;I)D
    .locals 2
    .param p0, "com_vivox_service_vx_req_session_set_3d_position_t"    # Lcom/vivox/service/vx_req_session_set_3d_position_t;
    .param p1, "i"    # I

    .prologue
    .line 2133
    invoke-static {p0}, Lcom/vivox/service/vx_req_session_set_3d_position_t;->getCPtr(Lcom/vivox/service/vx_req_session_set_3d_position_t;)J

    move-result-wide v0

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_session_set_3d_position_t_get_listener_at_orientation_item(JLcom/vivox/service/vx_req_session_set_3d_position_t;I)D

    move-result-wide v0

    return-wide v0
.end method

.method public static vx_req_session_set_3d_position_t_get_listener_left_orientation_item(Lcom/vivox/service/vx_req_session_set_3d_position_t;I)D
    .locals 2
    .param p0, "com_vivox_service_vx_req_session_set_3d_position_t"    # Lcom/vivox/service/vx_req_session_set_3d_position_t;
    .param p1, "i"    # I

    .prologue
    .line 2137
    invoke-static {p0}, Lcom/vivox/service/vx_req_session_set_3d_position_t;->getCPtr(Lcom/vivox/service/vx_req_session_set_3d_position_t;)J

    move-result-wide v0

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_session_set_3d_position_t_get_listener_left_orientation_item(JLcom/vivox/service/vx_req_session_set_3d_position_t;I)D

    move-result-wide v0

    return-wide v0
.end method

.method public static vx_req_session_set_3d_position_t_get_listener_position_item(Lcom/vivox/service/vx_req_session_set_3d_position_t;I)D
    .locals 2
    .param p0, "com_vivox_service_vx_req_session_set_3d_position_t"    # Lcom/vivox/service/vx_req_session_set_3d_position_t;
    .param p1, "i"    # I

    .prologue
    .line 2141
    invoke-static {p0}, Lcom/vivox/service/vx_req_session_set_3d_position_t;->getCPtr(Lcom/vivox/service/vx_req_session_set_3d_position_t;)J

    move-result-wide v0

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_session_set_3d_position_t_get_listener_position_item(JLcom/vivox/service/vx_req_session_set_3d_position_t;I)D

    move-result-wide v0

    return-wide v0
.end method

.method public static vx_req_session_set_3d_position_t_get_listener_up_orientation_item(Lcom/vivox/service/vx_req_session_set_3d_position_t;I)D
    .locals 2
    .param p0, "com_vivox_service_vx_req_session_set_3d_position_t"    # Lcom/vivox/service/vx_req_session_set_3d_position_t;
    .param p1, "i"    # I

    .prologue
    .line 2145
    invoke-static {p0}, Lcom/vivox/service/vx_req_session_set_3d_position_t;->getCPtr(Lcom/vivox/service/vx_req_session_set_3d_position_t;)J

    move-result-wide v0

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_session_set_3d_position_t_get_listener_up_orientation_item(JLcom/vivox/service/vx_req_session_set_3d_position_t;I)D

    move-result-wide v0

    return-wide v0
.end method

.method public static vx_req_session_set_3d_position_t_get_listener_velocity_item(Lcom/vivox/service/vx_req_session_set_3d_position_t;I)D
    .locals 2
    .param p0, "com_vivox_service_vx_req_session_set_3d_position_t"    # Lcom/vivox/service/vx_req_session_set_3d_position_t;
    .param p1, "i"    # I

    .prologue
    .line 2149
    invoke-static {p0}, Lcom/vivox/service/vx_req_session_set_3d_position_t;->getCPtr(Lcom/vivox/service/vx_req_session_set_3d_position_t;)J

    move-result-wide v0

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_session_set_3d_position_t_get_listener_velocity_item(JLcom/vivox/service/vx_req_session_set_3d_position_t;I)D

    move-result-wide v0

    return-wide v0
.end method

.method public static vx_req_session_set_3d_position_t_get_speaker_at_orientation_item(Lcom/vivox/service/vx_req_session_set_3d_position_t;I)D
    .locals 2
    .param p0, "com_vivox_service_vx_req_session_set_3d_position_t"    # Lcom/vivox/service/vx_req_session_set_3d_position_t;
    .param p1, "i"    # I

    .prologue
    .line 2153
    invoke-static {p0}, Lcom/vivox/service/vx_req_session_set_3d_position_t;->getCPtr(Lcom/vivox/service/vx_req_session_set_3d_position_t;)J

    move-result-wide v0

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_session_set_3d_position_t_get_speaker_at_orientation_item(JLcom/vivox/service/vx_req_session_set_3d_position_t;I)D

    move-result-wide v0

    return-wide v0
.end method

.method public static vx_req_session_set_3d_position_t_get_speaker_left_orientation_item(Lcom/vivox/service/vx_req_session_set_3d_position_t;I)D
    .locals 2
    .param p0, "com_vivox_service_vx_req_session_set_3d_position_t"    # Lcom/vivox/service/vx_req_session_set_3d_position_t;
    .param p1, "i"    # I

    .prologue
    .line 2157
    invoke-static {p0}, Lcom/vivox/service/vx_req_session_set_3d_position_t;->getCPtr(Lcom/vivox/service/vx_req_session_set_3d_position_t;)J

    move-result-wide v0

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_session_set_3d_position_t_get_speaker_left_orientation_item(JLcom/vivox/service/vx_req_session_set_3d_position_t;I)D

    move-result-wide v0

    return-wide v0
.end method

.method public static vx_req_session_set_3d_position_t_get_speaker_position_item(Lcom/vivox/service/vx_req_session_set_3d_position_t;I)D
    .locals 2
    .param p0, "com_vivox_service_vx_req_session_set_3d_position_t"    # Lcom/vivox/service/vx_req_session_set_3d_position_t;
    .param p1, "i"    # I

    .prologue
    .line 2161
    invoke-static {p0}, Lcom/vivox/service/vx_req_session_set_3d_position_t;->getCPtr(Lcom/vivox/service/vx_req_session_set_3d_position_t;)J

    move-result-wide v0

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_session_set_3d_position_t_get_speaker_position_item(JLcom/vivox/service/vx_req_session_set_3d_position_t;I)D

    move-result-wide v0

    return-wide v0
.end method

.method public static vx_req_session_set_3d_position_t_get_speaker_up_orientation_item(Lcom/vivox/service/vx_req_session_set_3d_position_t;I)D
    .locals 2
    .param p0, "com_vivox_service_vx_req_session_set_3d_position_t"    # Lcom/vivox/service/vx_req_session_set_3d_position_t;
    .param p1, "i"    # I

    .prologue
    .line 2165
    invoke-static {p0}, Lcom/vivox/service/vx_req_session_set_3d_position_t;->getCPtr(Lcom/vivox/service/vx_req_session_set_3d_position_t;)J

    move-result-wide v0

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_session_set_3d_position_t_get_speaker_up_orientation_item(JLcom/vivox/service/vx_req_session_set_3d_position_t;I)D

    move-result-wide v0

    return-wide v0
.end method

.method public static vx_req_session_set_3d_position_t_get_speaker_velocity_item(Lcom/vivox/service/vx_req_session_set_3d_position_t;I)D
    .locals 2
    .param p0, "com_vivox_service_vx_req_session_set_3d_position_t"    # Lcom/vivox/service/vx_req_session_set_3d_position_t;
    .param p1, "i"    # I

    .prologue
    .line 2169
    invoke-static {p0}, Lcom/vivox/service/vx_req_session_set_3d_position_t;->getCPtr(Lcom/vivox/service/vx_req_session_set_3d_position_t;)J

    move-result-wide v0

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_session_set_3d_position_t_get_speaker_velocity_item(JLcom/vivox/service/vx_req_session_set_3d_position_t;I)D

    move-result-wide v0

    return-wide v0
.end method

.method public static vx_req_session_set_3d_position_t_set_listener_at_orientation_item(Lcom/vivox/service/vx_req_session_set_3d_position_t;ID)V
    .locals 6
    .param p0, "com_vivox_service_vx_req_session_set_3d_position_t"    # Lcom/vivox/service/vx_req_session_set_3d_position_t;
    .param p1, "i"    # I
    .param p2, "d"    # D

    .prologue
    .line 2173
    invoke-static {p0}, Lcom/vivox/service/vx_req_session_set_3d_position_t;->getCPtr(Lcom/vivox/service/vx_req_session_set_3d_position_t;)J

    move-result-wide v0

    move-object v2, p0

    move v3, p1

    move-wide v4, p2

    invoke-static/range {v0 .. v5}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_session_set_3d_position_t_set_listener_at_orientation_item(JLcom/vivox/service/vx_req_session_set_3d_position_t;ID)V

    .line 2174
    return-void
.end method

.method public static vx_req_session_set_3d_position_t_set_listener_left_orientation_item(Lcom/vivox/service/vx_req_session_set_3d_position_t;ID)V
    .locals 6
    .param p0, "com_vivox_service_vx_req_session_set_3d_position_t"    # Lcom/vivox/service/vx_req_session_set_3d_position_t;
    .param p1, "i"    # I
    .param p2, "d"    # D

    .prologue
    .line 2177
    invoke-static {p0}, Lcom/vivox/service/vx_req_session_set_3d_position_t;->getCPtr(Lcom/vivox/service/vx_req_session_set_3d_position_t;)J

    move-result-wide v0

    move-object v2, p0

    move v3, p1

    move-wide v4, p2

    invoke-static/range {v0 .. v5}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_session_set_3d_position_t_set_listener_left_orientation_item(JLcom/vivox/service/vx_req_session_set_3d_position_t;ID)V

    .line 2178
    return-void
.end method

.method public static vx_req_session_set_3d_position_t_set_listener_position_item(Lcom/vivox/service/vx_req_session_set_3d_position_t;ID)V
    .locals 6
    .param p0, "com_vivox_service_vx_req_session_set_3d_position_t"    # Lcom/vivox/service/vx_req_session_set_3d_position_t;
    .param p1, "i"    # I
    .param p2, "d"    # D

    .prologue
    .line 2181
    invoke-static {p0}, Lcom/vivox/service/vx_req_session_set_3d_position_t;->getCPtr(Lcom/vivox/service/vx_req_session_set_3d_position_t;)J

    move-result-wide v0

    move-object v2, p0

    move v3, p1

    move-wide v4, p2

    invoke-static/range {v0 .. v5}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_session_set_3d_position_t_set_listener_position_item(JLcom/vivox/service/vx_req_session_set_3d_position_t;ID)V

    .line 2182
    return-void
.end method

.method public static vx_req_session_set_3d_position_t_set_listener_up_orientation_item(Lcom/vivox/service/vx_req_session_set_3d_position_t;ID)V
    .locals 6
    .param p0, "com_vivox_service_vx_req_session_set_3d_position_t"    # Lcom/vivox/service/vx_req_session_set_3d_position_t;
    .param p1, "i"    # I
    .param p2, "d"    # D

    .prologue
    .line 2185
    invoke-static {p0}, Lcom/vivox/service/vx_req_session_set_3d_position_t;->getCPtr(Lcom/vivox/service/vx_req_session_set_3d_position_t;)J

    move-result-wide v0

    move-object v2, p0

    move v3, p1

    move-wide v4, p2

    invoke-static/range {v0 .. v5}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_session_set_3d_position_t_set_listener_up_orientation_item(JLcom/vivox/service/vx_req_session_set_3d_position_t;ID)V

    .line 2186
    return-void
.end method

.method public static vx_req_session_set_3d_position_t_set_listener_velocity_item(Lcom/vivox/service/vx_req_session_set_3d_position_t;ID)V
    .locals 6
    .param p0, "com_vivox_service_vx_req_session_set_3d_position_t"    # Lcom/vivox/service/vx_req_session_set_3d_position_t;
    .param p1, "i"    # I
    .param p2, "d"    # D

    .prologue
    .line 2189
    invoke-static {p0}, Lcom/vivox/service/vx_req_session_set_3d_position_t;->getCPtr(Lcom/vivox/service/vx_req_session_set_3d_position_t;)J

    move-result-wide v0

    move-object v2, p0

    move v3, p1

    move-wide v4, p2

    invoke-static/range {v0 .. v5}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_session_set_3d_position_t_set_listener_velocity_item(JLcom/vivox/service/vx_req_session_set_3d_position_t;ID)V

    .line 2190
    return-void
.end method

.method public static vx_req_session_set_3d_position_t_set_speaker_at_orientation_item(Lcom/vivox/service/vx_req_session_set_3d_position_t;ID)V
    .locals 6
    .param p0, "com_vivox_service_vx_req_session_set_3d_position_t"    # Lcom/vivox/service/vx_req_session_set_3d_position_t;
    .param p1, "i"    # I
    .param p2, "d"    # D

    .prologue
    .line 2193
    invoke-static {p0}, Lcom/vivox/service/vx_req_session_set_3d_position_t;->getCPtr(Lcom/vivox/service/vx_req_session_set_3d_position_t;)J

    move-result-wide v0

    move-object v2, p0

    move v3, p1

    move-wide v4, p2

    invoke-static/range {v0 .. v5}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_session_set_3d_position_t_set_speaker_at_orientation_item(JLcom/vivox/service/vx_req_session_set_3d_position_t;ID)V

    .line 2194
    return-void
.end method

.method public static vx_req_session_set_3d_position_t_set_speaker_left_orientation_item(Lcom/vivox/service/vx_req_session_set_3d_position_t;ID)V
    .locals 6
    .param p0, "com_vivox_service_vx_req_session_set_3d_position_t"    # Lcom/vivox/service/vx_req_session_set_3d_position_t;
    .param p1, "i"    # I
    .param p2, "d"    # D

    .prologue
    .line 2197
    invoke-static {p0}, Lcom/vivox/service/vx_req_session_set_3d_position_t;->getCPtr(Lcom/vivox/service/vx_req_session_set_3d_position_t;)J

    move-result-wide v0

    move-object v2, p0

    move v3, p1

    move-wide v4, p2

    invoke-static/range {v0 .. v5}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_session_set_3d_position_t_set_speaker_left_orientation_item(JLcom/vivox/service/vx_req_session_set_3d_position_t;ID)V

    .line 2198
    return-void
.end method

.method public static vx_req_session_set_3d_position_t_set_speaker_position_item(Lcom/vivox/service/vx_req_session_set_3d_position_t;ID)V
    .locals 6
    .param p0, "com_vivox_service_vx_req_session_set_3d_position_t"    # Lcom/vivox/service/vx_req_session_set_3d_position_t;
    .param p1, "i"    # I
    .param p2, "d"    # D

    .prologue
    .line 2201
    invoke-static {p0}, Lcom/vivox/service/vx_req_session_set_3d_position_t;->getCPtr(Lcom/vivox/service/vx_req_session_set_3d_position_t;)J

    move-result-wide v0

    move-object v2, p0

    move v3, p1

    move-wide v4, p2

    invoke-static/range {v0 .. v5}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_session_set_3d_position_t_set_speaker_position_item(JLcom/vivox/service/vx_req_session_set_3d_position_t;ID)V

    .line 2202
    return-void
.end method

.method public static vx_req_session_set_3d_position_t_set_speaker_up_orientation_item(Lcom/vivox/service/vx_req_session_set_3d_position_t;ID)V
    .locals 6
    .param p0, "com_vivox_service_vx_req_session_set_3d_position_t"    # Lcom/vivox/service/vx_req_session_set_3d_position_t;
    .param p1, "i"    # I
    .param p2, "d"    # D

    .prologue
    .line 2205
    invoke-static {p0}, Lcom/vivox/service/vx_req_session_set_3d_position_t;->getCPtr(Lcom/vivox/service/vx_req_session_set_3d_position_t;)J

    move-result-wide v0

    move-object v2, p0

    move v3, p1

    move-wide v4, p2

    invoke-static/range {v0 .. v5}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_session_set_3d_position_t_set_speaker_up_orientation_item(JLcom/vivox/service/vx_req_session_set_3d_position_t;ID)V

    .line 2206
    return-void
.end method

.method public static vx_req_session_set_3d_position_t_set_speaker_velocity_item(Lcom/vivox/service/vx_req_session_set_3d_position_t;ID)V
    .locals 6
    .param p0, "com_vivox_service_vx_req_session_set_3d_position_t"    # Lcom/vivox/service/vx_req_session_set_3d_position_t;
    .param p1, "i"    # I
    .param p2, "d"    # D

    .prologue
    .line 2209
    invoke-static {p0}, Lcom/vivox/service/vx_req_session_set_3d_position_t;->getCPtr(Lcom/vivox/service/vx_req_session_set_3d_position_t;)J

    move-result-wide v0

    move-object v2, p0

    move v3, p1

    move-wide v4, p2

    invoke-static/range {v0 .. v5}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_session_set_3d_position_t_set_speaker_velocity_item(JLcom/vivox/service/vx_req_session_set_3d_position_t;ID)V

    .line 2210
    return-void
.end method

.method public static vx_req_session_set_local_speaker_volume_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_session_set_local_speaker_volume;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_session_set_local_speaker_volume"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_session_set_local_speaker_volume;

    .prologue
    .line 2213
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_session_set_local_speaker_volume;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_session_set_local_speaker_volume;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_session_set_local_speaker_volume_create(J)V

    .line 2214
    return-void
.end method

.method public static vx_req_session_set_participant_mute_for_me_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_session_set_participant_mute_for_me;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_session_set_participant_mute_for_me"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_session_set_participant_mute_for_me;

    .prologue
    .line 2217
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_session_set_participant_mute_for_me;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_session_set_participant_mute_for_me;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_session_set_participant_mute_for_me_create(J)V

    .line 2218
    return-void
.end method

.method public static vx_req_session_set_participant_volume_for_me_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_session_set_participant_volume_for_me;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_session_set_participant_volume_for_me"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_session_set_participant_volume_for_me;

    .prologue
    .line 2221
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_session_set_participant_volume_for_me;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_session_set_participant_volume_for_me;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_session_set_participant_volume_for_me_create(J)V

    .line 2222
    return-void
.end method

.method public static vx_req_session_set_voice_font_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_session_set_voice_font;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_session_set_voice_font"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_session_set_voice_font;

    .prologue
    .line 2225
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_session_set_voice_font;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_session_set_voice_font;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_session_set_voice_font_create(J)V

    .line 2226
    return-void
.end method

.method public static vx_req_session_terminate_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_session_terminate;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_session_terminate"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_session_terminate;

    .prologue
    .line 2229
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_session_terminate;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_session_terminate;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_session_terminate_create(J)V

    .line 2230
    return-void
.end method

.method public static vx_req_session_text_connect_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_session_text_connect;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_session_text_connect"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_session_text_connect;

    .prologue
    .line 2233
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_session_text_connect;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_session_text_connect;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_session_text_connect_create(J)V

    .line 2234
    return-void
.end method

.method public static vx_req_session_text_disconnect_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_session_text_disconnect;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_session_text_disconnect"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_session_text_disconnect;

    .prologue
    .line 2237
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_session_text_disconnect;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_session_text_disconnect;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_session_text_disconnect_create(J)V

    .line 2238
    return-void
.end method

.method public static vx_req_sessiongroup_add_session_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_sessiongroup_add_session;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_sessiongroup_add_session"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_sessiongroup_add_session;

    .prologue
    .line 2241
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_sessiongroup_add_session;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_sessiongroup_add_session;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_sessiongroup_add_session_create(J)V

    .line 2242
    return-void
.end method

.method public static vx_req_sessiongroup_control_audio_injection_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_sessiongroup_control_audio_injection;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_sessiongroup_control_audio_injection"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_sessiongroup_control_audio_injection;

    .prologue
    .line 2245
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_sessiongroup_control_audio_injection;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_sessiongroup_control_audio_injection;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_sessiongroup_control_audio_injection_create(J)V

    .line 2246
    return-void
.end method

.method public static vx_req_sessiongroup_control_playback_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_sessiongroup_control_playback;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_sessiongroup_control_playback"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_sessiongroup_control_playback;

    .prologue
    .line 2249
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_sessiongroup_control_playback;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_sessiongroup_control_playback;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_sessiongroup_control_playback_create(J)V

    .line 2250
    return-void
.end method

.method public static vx_req_sessiongroup_control_recording_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_sessiongroup_control_recording;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_sessiongroup_control_recording"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_sessiongroup_control_recording;

    .prologue
    .line 2253
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_sessiongroup_control_recording;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_sessiongroup_control_recording;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_sessiongroup_control_recording_create(J)V

    .line 2254
    return-void
.end method

.method public static vx_req_sessiongroup_create_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_sessiongroup_create;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_sessiongroup_create"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_sessiongroup_create;

    .prologue
    .line 2257
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_sessiongroup_create;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_sessiongroup_create;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_sessiongroup_create_create(J)V

    .line 2258
    return-void
.end method

.method public static vx_req_sessiongroup_get_stats_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_sessiongroup_get_stats;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_sessiongroup_get_stats"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_sessiongroup_get_stats;

    .prologue
    .line 2261
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_sessiongroup_get_stats;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_sessiongroup_get_stats;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_sessiongroup_get_stats_create(J)V

    .line 2262
    return-void
.end method

.method public static vx_req_sessiongroup_remove_session_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_sessiongroup_remove_session;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_sessiongroup_remove_session"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_sessiongroup_remove_session;

    .prologue
    .line 2265
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_sessiongroup_remove_session;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_sessiongroup_remove_session;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_sessiongroup_remove_session_create(J)V

    .line 2266
    return-void
.end method

.method public static vx_req_sessiongroup_reset_focus_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_sessiongroup_reset_focus;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_sessiongroup_reset_focus"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_sessiongroup_reset_focus;

    .prologue
    .line 2269
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_sessiongroup_reset_focus;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_sessiongroup_reset_focus;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_sessiongroup_reset_focus_create(J)V

    .line 2270
    return-void
.end method

.method public static vx_req_sessiongroup_set_focus_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_sessiongroup_set_focus;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_sessiongroup_set_focus"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_sessiongroup_set_focus;

    .prologue
    .line 2273
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_sessiongroup_set_focus;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_sessiongroup_set_focus;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_sessiongroup_set_focus_create(J)V

    .line 2274
    return-void
.end method

.method public static vx_req_sessiongroup_set_playback_options_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_sessiongroup_set_playback_options;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_sessiongroup_set_playback_options"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_sessiongroup_set_playback_options;

    .prologue
    .line 2277
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_sessiongroup_set_playback_options;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_sessiongroup_set_playback_options;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_sessiongroup_set_playback_options_create(J)V

    .line 2278
    return-void
.end method

.method public static vx_req_sessiongroup_set_session_3d_position_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_sessiongroup_set_session_3d_position;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_sessiongroup_set_session_3d_position"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_sessiongroup_set_session_3d_position;

    .prologue
    .line 2281
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_sessiongroup_set_session_3d_position;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_sessiongroup_set_session_3d_position;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_sessiongroup_set_session_3d_position_create(J)V

    .line 2282
    return-void
.end method

.method public static vx_req_sessiongroup_set_session_3d_position_t_get_speaker_position_item(Lcom/vivox/service/vx_req_sessiongroup_set_session_3d_position_t;I)D
    .locals 2
    .param p0, "com_vivox_service_vx_req_sessiongroup_set_session_3d_position_t"    # Lcom/vivox/service/vx_req_sessiongroup_set_session_3d_position_t;
    .param p1, "i"    # I

    .prologue
    .line 2285
    invoke-static {p0}, Lcom/vivox/service/vx_req_sessiongroup_set_session_3d_position_t;->getCPtr(Lcom/vivox/service/vx_req_sessiongroup_set_session_3d_position_t;)J

    move-result-wide v0

    invoke-static {v0, v1, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_sessiongroup_set_session_3d_position_t_get_speaker_position_item(JLcom/vivox/service/vx_req_sessiongroup_set_session_3d_position_t;I)D

    move-result-wide v0

    return-wide v0
.end method

.method public static vx_req_sessiongroup_set_session_3d_position_t_set_speaker_position_item(Lcom/vivox/service/vx_req_sessiongroup_set_session_3d_position_t;ID)V
    .locals 6
    .param p0, "com_vivox_service_vx_req_sessiongroup_set_session_3d_position_t"    # Lcom/vivox/service/vx_req_sessiongroup_set_session_3d_position_t;
    .param p1, "i"    # I
    .param p2, "d"    # D

    .prologue
    .line 2289
    invoke-static {p0}, Lcom/vivox/service/vx_req_sessiongroup_set_session_3d_position_t;->getCPtr(Lcom/vivox/service/vx_req_sessiongroup_set_session_3d_position_t;)J

    move-result-wide v0

    move-object v2, p0

    move v3, p1

    move-wide v4, p2

    invoke-static/range {v0 .. v5}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_sessiongroup_set_session_3d_position_t_set_speaker_position_item(JLcom/vivox/service/vx_req_sessiongroup_set_session_3d_position_t;ID)V

    .line 2290
    return-void
.end method

.method public static vx_req_sessiongroup_set_tx_all_sessions_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_sessiongroup_set_tx_all_sessions;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_sessiongroup_set_tx_all_sessions"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_sessiongroup_set_tx_all_sessions;

    .prologue
    .line 2293
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_sessiongroup_set_tx_all_sessions;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_sessiongroup_set_tx_all_sessions;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_sessiongroup_set_tx_all_sessions_create(J)V

    .line 2294
    return-void
.end method

.method public static vx_req_sessiongroup_set_tx_no_session_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_sessiongroup_set_tx_no_session;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_sessiongroup_set_tx_no_session"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_sessiongroup_set_tx_no_session;

    .prologue
    .line 2297
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_sessiongroup_set_tx_no_session;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_sessiongroup_set_tx_no_session;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_sessiongroup_set_tx_no_session_create(J)V

    .line 2298
    return-void
.end method

.method public static vx_req_sessiongroup_set_tx_session_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_sessiongroup_set_tx_session;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_sessiongroup_set_tx_session"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_sessiongroup_set_tx_session;

    .prologue
    .line 2301
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_sessiongroup_set_tx_session;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_sessiongroup_set_tx_session;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_sessiongroup_set_tx_session_create(J)V

    .line 2302
    return-void
.end method

.method public static vx_req_sessiongroup_terminate_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_sessiongroup_terminate;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_sessiongroup_terminate"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_sessiongroup_terminate;

    .prologue
    .line 2305
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_sessiongroup_terminate;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_sessiongroup_terminate;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_sessiongroup_terminate_create(J)V

    .line 2306
    return-void
.end method

.method public static vx_req_sessiongroup_unset_focus_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_sessiongroup_unset_focus;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_sessiongroup_unset_focus"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_sessiongroup_unset_focus;

    .prologue
    .line 2309
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_sessiongroup_unset_focus;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_sessiongroup_unset_focus;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_req_sessiongroup_unset_focus_create(J)V

    .line 2310
    return-void
.end method

.method public static vx_request_to_xml(Lcom/vivox/service/SWIGTYPE_p_void;Lcom/vivox/service/SWIGTYPE_p_p_char;)V
    .locals 4
    .param p0, "sWIGTYPE_p_void"    # Lcom/vivox/service/SWIGTYPE_p_void;
    .param p1, "sWIGTYPE_p_p_char"    # Lcom/vivox/service/SWIGTYPE_p_p_char;

    .prologue
    .line 2313
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_void;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_void;)J

    move-result-wide v0

    invoke-static {p1}, Lcom/vivox/service/SWIGTYPE_p_p_char;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_char;)J

    move-result-wide v2

    invoke-static {v0, v1, v2, v3}, Lcom/vivox/service/VxClientProxyJNI;->vx_request_to_xml(JJ)V

    .line 2314
    return-void
.end method

.method public static vx_resp_account_buddy_search_t_get_buddies_item(Lcom/vivox/service/vx_resp_account_buddy_search_t;I)Lcom/vivox/service/vx_buddy_t;
    .locals 4
    .param p0, "com_vivox_service_vx_resp_account_buddy_search_t"    # Lcom/vivox/service/vx_resp_account_buddy_search_t;
    .param p1, "i"    # I

    .prologue
    .line 2317
    invoke-static {p0}, Lcom/vivox/service/vx_resp_account_buddy_search_t;->getCPtr(Lcom/vivox/service/vx_resp_account_buddy_search_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_resp_account_buddy_search_t_get_buddies_item(JLcom/vivox/service/vx_resp_account_buddy_search_t;I)J

    move-result-wide v0

    .line 2318
    .local v0, "vx_resp_account_buddy_search_t_get_buddies_item":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_buddy_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_buddy_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_resp_account_channel_favorites_get_list_t_get_favorites_item(Lcom/vivox/service/vx_resp_account_channel_favorites_get_list_t;I)Lcom/vivox/service/vx_channel_favorite_t;
    .locals 4
    .param p0, "com_vivox_service_vx_resp_account_channel_favorites_get_list_t"    # Lcom/vivox/service/vx_resp_account_channel_favorites_get_list_t;
    .param p1, "i"    # I

    .prologue
    .line 2322
    invoke-static {p0}, Lcom/vivox/service/vx_resp_account_channel_favorites_get_list_t;->getCPtr(Lcom/vivox/service/vx_resp_account_channel_favorites_get_list_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_resp_account_channel_favorites_get_list_t_get_favorites_item(JLcom/vivox/service/vx_resp_account_channel_favorites_get_list_t;I)J

    move-result-wide v0

    .line 2323
    .local v0, "vx_resp_account_channel_favorites_get_list_t_get_favorites_item":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_channel_favorite_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_channel_favorite_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_resp_account_channel_favorites_get_list_t_get_groups_item(Lcom/vivox/service/vx_resp_account_channel_favorites_get_list_t;I)Lcom/vivox/service/vx_channel_favorite_group_t;
    .locals 4
    .param p0, "com_vivox_service_vx_resp_account_channel_favorites_get_list_t"    # Lcom/vivox/service/vx_resp_account_channel_favorites_get_list_t;
    .param p1, "i"    # I

    .prologue
    .line 2327
    invoke-static {p0}, Lcom/vivox/service/vx_resp_account_channel_favorites_get_list_t;->getCPtr(Lcom/vivox/service/vx_resp_account_channel_favorites_get_list_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_resp_account_channel_favorites_get_list_t_get_groups_item(JLcom/vivox/service/vx_resp_account_channel_favorites_get_list_t;I)J

    move-result-wide v0

    .line 2328
    .local v0, "vx_resp_account_channel_favorites_get_list_t_get_groups_item":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_channel_favorite_group_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_channel_favorite_group_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_resp_account_channel_get_acl_t_get_participants_item(Lcom/vivox/service/vx_resp_account_channel_get_acl_t;I)Lcom/vivox/service/vx_participant_t;
    .locals 4
    .param p0, "com_vivox_service_vx_resp_account_channel_get_acl_t"    # Lcom/vivox/service/vx_resp_account_channel_get_acl_t;
    .param p1, "i"    # I

    .prologue
    .line 2332
    invoke-static {p0}, Lcom/vivox/service/vx_resp_account_channel_get_acl_t;->getCPtr(Lcom/vivox/service/vx_resp_account_channel_get_acl_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_resp_account_channel_get_acl_t_get_participants_item(JLcom/vivox/service/vx_resp_account_channel_get_acl_t;I)J

    move-result-wide v0

    .line 2333
    .local v0, "vx_resp_account_channel_get_acl_t_get_participants_item":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_participant_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_participant_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_resp_account_channel_get_moderators_t_get_participants_item(Lcom/vivox/service/vx_resp_account_channel_get_moderators_t;I)Lcom/vivox/service/vx_participant_t;
    .locals 4
    .param p0, "com_vivox_service_vx_resp_account_channel_get_moderators_t"    # Lcom/vivox/service/vx_resp_account_channel_get_moderators_t;
    .param p1, "i"    # I

    .prologue
    .line 2337
    invoke-static {p0}, Lcom/vivox/service/vx_resp_account_channel_get_moderators_t;->getCPtr(Lcom/vivox/service/vx_resp_account_channel_get_moderators_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_resp_account_channel_get_moderators_t_get_participants_item(JLcom/vivox/service/vx_resp_account_channel_get_moderators_t;I)J

    move-result-wide v0

    .line 2338
    .local v0, "vx_resp_account_channel_get_moderators_t_get_participants_item":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_participant_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_participant_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_resp_account_channel_get_participants_t_get_participants_item(Lcom/vivox/service/vx_resp_account_channel_get_participants_t;I)Lcom/vivox/service/vx_participant_t;
    .locals 4
    .param p0, "com_vivox_service_vx_resp_account_channel_get_participants_t"    # Lcom/vivox/service/vx_resp_account_channel_get_participants_t;
    .param p1, "i"    # I

    .prologue
    .line 2342
    invoke-static {p0}, Lcom/vivox/service/vx_resp_account_channel_get_participants_t;->getCPtr(Lcom/vivox/service/vx_resp_account_channel_get_participants_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_resp_account_channel_get_participants_t_get_participants_item(JLcom/vivox/service/vx_resp_account_channel_get_participants_t;I)J

    move-result-wide v0

    .line 2343
    .local v0, "vx_resp_account_channel_get_participants_t_get_participants_item":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_participant_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_participant_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_resp_account_channel_search_t_get_channels_item(Lcom/vivox/service/vx_resp_account_channel_search_t;I)Lcom/vivox/service/vx_channel_t;
    .locals 4
    .param p0, "com_vivox_service_vx_resp_account_channel_search_t"    # Lcom/vivox/service/vx_resp_account_channel_search_t;
    .param p1, "i"    # I

    .prologue
    .line 2347
    invoke-static {p0}, Lcom/vivox/service/vx_resp_account_channel_search_t;->getCPtr(Lcom/vivox/service/vx_resp_account_channel_search_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_resp_account_channel_search_t_get_channels_item(JLcom/vivox/service/vx_resp_account_channel_search_t;I)J

    move-result-wide v0

    .line 2348
    .local v0, "vx_resp_account_channel_search_t_get_channels_item":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_channel_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_channel_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_resp_account_get_session_fonts_t_get_session_fonts_item(Lcom/vivox/service/vx_resp_account_get_session_fonts_t;I)Lcom/vivox/service/vx_voice_font_t;
    .locals 4
    .param p0, "com_vivox_service_vx_resp_account_get_session_fonts_t"    # Lcom/vivox/service/vx_resp_account_get_session_fonts_t;
    .param p1, "i"    # I

    .prologue
    .line 2352
    invoke-static {p0}, Lcom/vivox/service/vx_resp_account_get_session_fonts_t;->getCPtr(Lcom/vivox/service/vx_resp_account_get_session_fonts_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_resp_account_get_session_fonts_t_get_session_fonts_item(JLcom/vivox/service/vx_resp_account_get_session_fonts_t;I)J

    move-result-wide v0

    .line 2353
    .local v0, "vx_resp_account_get_session_fonts_t_get_session_fonts_item":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_voice_font_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_voice_font_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_resp_account_get_template_fonts_t_get_template_fonts_item(Lcom/vivox/service/vx_resp_account_get_template_fonts_t;I)Lcom/vivox/service/vx_voice_font_t;
    .locals 4
    .param p0, "com_vivox_service_vx_resp_account_get_template_fonts_t"    # Lcom/vivox/service/vx_resp_account_get_template_fonts_t;
    .param p1, "i"    # I

    .prologue
    .line 2357
    invoke-static {p0}, Lcom/vivox/service/vx_resp_account_get_template_fonts_t;->getCPtr(Lcom/vivox/service/vx_resp_account_get_template_fonts_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_resp_account_get_template_fonts_t_get_template_fonts_item(JLcom/vivox/service/vx_resp_account_get_template_fonts_t;I)J

    move-result-wide v0

    .line 2358
    .local v0, "vx_resp_account_get_template_fonts_t_get_template_fonts_item":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_voice_font_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_voice_font_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_resp_account_list_auto_accept_rules_t_get_auto_accept_rules_item(Lcom/vivox/service/vx_resp_account_list_auto_accept_rules_t;I)Lcom/vivox/service/vx_auto_accept_rule_t;
    .locals 4
    .param p0, "com_vivox_service_vx_resp_account_list_auto_accept_rules_t"    # Lcom/vivox/service/vx_resp_account_list_auto_accept_rules_t;
    .param p1, "i"    # I

    .prologue
    .line 2362
    invoke-static {p0}, Lcom/vivox/service/vx_resp_account_list_auto_accept_rules_t;->getCPtr(Lcom/vivox/service/vx_resp_account_list_auto_accept_rules_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_resp_account_list_auto_accept_rules_t_get_auto_accept_rules_item(JLcom/vivox/service/vx_resp_account_list_auto_accept_rules_t;I)J

    move-result-wide v0

    .line 2363
    .local v0, "vx_resp_account_list_auto_accept_rules_t_get_auto_accept_rules_item":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_auto_accept_rule_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_auto_accept_rule_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_resp_account_list_block_rules_t_get_block_rules_item(Lcom/vivox/service/vx_resp_account_list_block_rules_t;I)Lcom/vivox/service/vx_block_rule_t;
    .locals 4
    .param p0, "com_vivox_service_vx_resp_account_list_block_rules_t"    # Lcom/vivox/service/vx_resp_account_list_block_rules_t;
    .param p1, "i"    # I

    .prologue
    .line 2367
    invoke-static {p0}, Lcom/vivox/service/vx_resp_account_list_block_rules_t;->getCPtr(Lcom/vivox/service/vx_resp_account_list_block_rules_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_resp_account_list_block_rules_t_get_block_rules_item(JLcom/vivox/service/vx_resp_account_list_block_rules_t;I)J

    move-result-wide v0

    .line 2368
    .local v0, "vx_resp_account_list_block_rules_t_get_block_rules_item":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_block_rule_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_block_rule_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_resp_account_list_buddies_and_groups_t_get_buddies_item(Lcom/vivox/service/vx_resp_account_list_buddies_and_groups_t;I)Lcom/vivox/service/vx_buddy_t;
    .locals 4
    .param p0, "com_vivox_service_vx_resp_account_list_buddies_and_groups_t"    # Lcom/vivox/service/vx_resp_account_list_buddies_and_groups_t;
    .param p1, "i"    # I

    .prologue
    .line 2372
    invoke-static {p0}, Lcom/vivox/service/vx_resp_account_list_buddies_and_groups_t;->getCPtr(Lcom/vivox/service/vx_resp_account_list_buddies_and_groups_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_resp_account_list_buddies_and_groups_t_get_buddies_item(JLcom/vivox/service/vx_resp_account_list_buddies_and_groups_t;I)J

    move-result-wide v0

    .line 2373
    .local v0, "vx_resp_account_list_buddies_and_groups_t_get_buddies_item":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_buddy_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_buddy_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_resp_account_list_buddies_and_groups_t_get_groups_item(Lcom/vivox/service/vx_resp_account_list_buddies_and_groups_t;I)Lcom/vivox/service/vx_group_t;
    .locals 4
    .param p0, "com_vivox_service_vx_resp_account_list_buddies_and_groups_t"    # Lcom/vivox/service/vx_resp_account_list_buddies_and_groups_t;
    .param p1, "i"    # I

    .prologue
    .line 2377
    invoke-static {p0}, Lcom/vivox/service/vx_resp_account_list_buddies_and_groups_t;->getCPtr(Lcom/vivox/service/vx_resp_account_list_buddies_and_groups_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_resp_account_list_buddies_and_groups_t_get_groups_item(JLcom/vivox/service/vx_resp_account_list_buddies_and_groups_t;I)J

    move-result-wide v0

    .line 2378
    .local v0, "vx_resp_account_list_buddies_and_groups_t_get_groups_item":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_group_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_group_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_resp_aux_connectivity_info_t_get_test_results_item(Lcom/vivox/service/vx_resp_aux_connectivity_info_t;I)Lcom/vivox/service/vx_connectivity_test_result_t;
    .locals 4
    .param p0, "com_vivox_service_vx_resp_aux_connectivity_info_t"    # Lcom/vivox/service/vx_resp_aux_connectivity_info_t;
    .param p1, "i"    # I

    .prologue
    .line 2382
    invoke-static {p0}, Lcom/vivox/service/vx_resp_aux_connectivity_info_t;->getCPtr(Lcom/vivox/service/vx_resp_aux_connectivity_info_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_resp_aux_connectivity_info_t_get_test_results_item(JLcom/vivox/service/vx_resp_aux_connectivity_info_t;I)J

    move-result-wide v0

    .line 2383
    .local v0, "vx_resp_aux_connectivity_info_t_get_test_results_item":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_connectivity_test_result_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_connectivity_test_result_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_resp_aux_diagnostic_state_dump_t_get_state_connectors_item(Lcom/vivox/service/vx_resp_aux_diagnostic_state_dump_t;I)Lcom/vivox/service/vx_state_connector_t;
    .locals 4
    .param p0, "com_vivox_service_vx_resp_aux_diagnostic_state_dump_t"    # Lcom/vivox/service/vx_resp_aux_diagnostic_state_dump_t;
    .param p1, "i"    # I

    .prologue
    .line 2387
    invoke-static {p0}, Lcom/vivox/service/vx_resp_aux_diagnostic_state_dump_t;->getCPtr(Lcom/vivox/service/vx_resp_aux_diagnostic_state_dump_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_resp_aux_diagnostic_state_dump_t_get_state_connectors_item(JLcom/vivox/service/vx_resp_aux_diagnostic_state_dump_t;I)J

    move-result-wide v0

    .line 2388
    .local v0, "vx_resp_aux_diagnostic_state_dump_t_get_state_connectors_item":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_state_connector_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_state_connector_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_resp_aux_get_capture_devices_t_get_capture_devices_item(Lcom/vivox/service/vx_resp_aux_get_capture_devices_t;I)Lcom/vivox/service/vx_device_t;
    .locals 4
    .param p0, "com_vivox_service_vx_resp_aux_get_capture_devices_t"    # Lcom/vivox/service/vx_resp_aux_get_capture_devices_t;
    .param p1, "i"    # I

    .prologue
    .line 2392
    invoke-static {p0}, Lcom/vivox/service/vx_resp_aux_get_capture_devices_t;->getCPtr(Lcom/vivox/service/vx_resp_aux_get_capture_devices_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_resp_aux_get_capture_devices_t_get_capture_devices_item(JLcom/vivox/service/vx_resp_aux_get_capture_devices_t;I)J

    move-result-wide v0

    .line 2393
    .local v0, "vx_resp_aux_get_capture_devices_t_get_capture_devices_item":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_device_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_device_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_resp_aux_get_render_devices_t_get_render_devices_item(Lcom/vivox/service/vx_resp_aux_get_render_devices_t;I)Lcom/vivox/service/vx_device_t;
    .locals 4
    .param p0, "com_vivox_service_vx_resp_aux_get_render_devices_t"    # Lcom/vivox/service/vx_resp_aux_get_render_devices_t;
    .param p1, "i"    # I

    .prologue
    .line 2397
    invoke-static {p0}, Lcom/vivox/service/vx_resp_aux_get_render_devices_t;->getCPtr(Lcom/vivox/service/vx_resp_aux_get_render_devices_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_resp_aux_get_render_devices_t_get_render_devices_item(JLcom/vivox/service/vx_resp_aux_get_render_devices_t;I)J

    move-result-wide v0

    .line 2398
    .local v0, "vx_resp_aux_get_render_devices_t_get_render_devices_item":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_device_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_device_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_resp_channel_get_banned_users_t_get_banned_users_item(Lcom/vivox/service/vx_resp_channel_get_banned_users_t;I)Lcom/vivox/service/vx_participant_t;
    .locals 4
    .param p0, "com_vivox_service_vx_resp_channel_get_banned_users_t"    # Lcom/vivox/service/vx_resp_channel_get_banned_users_t;
    .param p1, "i"    # I

    .prologue
    .line 2402
    invoke-static {p0}, Lcom/vivox/service/vx_resp_channel_get_banned_users_t;->getCPtr(Lcom/vivox/service/vx_resp_channel_get_banned_users_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_resp_channel_get_banned_users_t_get_banned_users_item(JLcom/vivox/service/vx_resp_channel_get_banned_users_t;I)J

    move-result-wide v0

    .line 2403
    .local v0, "vx_resp_channel_get_banned_users_t_get_banned_users_item":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_participant_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_participant_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_response_to_xml(Lcom/vivox/service/SWIGTYPE_p_void;Lcom/vivox/service/SWIGTYPE_p_p_char;)V
    .locals 4
    .param p0, "sWIGTYPE_p_void"    # Lcom/vivox/service/SWIGTYPE_p_void;
    .param p1, "sWIGTYPE_p_p_char"    # Lcom/vivox/service/SWIGTYPE_p_p_char;

    .prologue
    .line 2407
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_void;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_void;)J

    move-result-wide v0

    invoke-static {p1}, Lcom/vivox/service/SWIGTYPE_p_p_char;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_char;)J

    move-result-wide v2

    invoke-static {v0, v1, v2, v3}, Lcom/vivox/service/VxClientProxyJNI;->vx_response_to_xml(JJ)V

    .line 2408
    return-void
.end method

.method public static vx_set_cert_data(Ljava/lang/String;)I
    .locals 1
    .param p0, "str"    # Ljava/lang/String;

    .prologue
    .line 2411
    invoke-static {p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_set_cert_data(Ljava/lang/String;)I

    move-result v0

    return v0
.end method

.method public static vx_set_cert_data_dir(Ljava/lang/String;)I
    .locals 1
    .param p0, "str"    # Ljava/lang/String;

    .prologue
    .line 2415
    invoke-static {p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_set_cert_data_dir(Ljava/lang/String;)I

    move-result v0

    return v0
.end method

.method public static vx_set_crash_dump_generation_enabled(I)V
    .locals 0
    .param p0, "i"    # I

    .prologue
    .line 2419
    invoke-static {p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_set_crash_dump_generation_enabled(I)V

    .line 2420
    return-void
.end method

.method public static vx_set_out_of_process_server_address(Ljava/lang/String;I)I
    .locals 1
    .param p0, "str"    # Ljava/lang/String;
    .param p1, "i"    # I

    .prologue
    .line 2423
    invoke-static {p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_set_out_of_process_server_address(Ljava/lang/String;I)I

    move-result v0

    return v0
.end method

.method public static vx_set_preferred_codec(I)I
    .locals 1
    .param p0, "i"    # I

    .prologue
    .line 2427
    invoke-static {p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_set_preferred_codec(I)I

    move-result v0

    return v0
.end method

.method public static vx_state_account_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_state_account;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_state_account"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_state_account;

    .prologue
    .line 2431
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_state_account;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_state_account;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_account_create(J)V

    .line 2432
    return-void
.end method

.method public static vx_state_account_free(Lcom/vivox/service/vx_state_account_t;)V
    .locals 2
    .param p0, "com_vivox_service_vx_state_account_t"    # Lcom/vivox/service/vx_state_account_t;

    .prologue
    .line 2435
    invoke-static {p0}, Lcom/vivox/service/vx_state_account_t;->getCPtr(Lcom/vivox/service/vx_state_account_t;)J

    move-result-wide v0

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_account_free(JLcom/vivox/service/vx_state_account_t;)V

    .line 2436
    return-void
.end method

.method public static vx_state_account_list_create(ILcom/vivox/service/SWIGTYPE_p_p_p_vx_state_account;)V
    .locals 2
    .param p0, "i"    # I
    .param p1, "sWIGTYPE_p_p_p_vx_state_account"    # Lcom/vivox/service/SWIGTYPE_p_p_p_vx_state_account;

    .prologue
    .line 2439
    invoke-static {p1}, Lcom/vivox/service/SWIGTYPE_p_p_p_vx_state_account;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_p_vx_state_account;)J

    move-result-wide v0

    invoke-static {p0, v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_account_list_create(IJ)V

    .line 2440
    return-void
.end method

.method public static vx_state_account_list_free(Lcom/vivox/service/SWIGTYPE_p_p_vx_state_account;I)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_state_account"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_state_account;
    .param p1, "i"    # I

    .prologue
    .line 2443
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_state_account;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_state_account;)J

    move-result-wide v0

    invoke-static {v0, v1, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_account_list_free(JI)V

    .line 2444
    return-void
.end method

.method public static vx_state_account_t_get_state_sessiongroups_item(Lcom/vivox/service/vx_state_account_t;I)Lcom/vivox/service/vx_state_sessiongroup_t;
    .locals 4
    .param p0, "com_vivox_service_vx_state_account_t"    # Lcom/vivox/service/vx_state_account_t;
    .param p1, "i"    # I

    .prologue
    .line 2447
    invoke-static {p0}, Lcom/vivox/service/vx_state_account_t;->getCPtr(Lcom/vivox/service/vx_state_account_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_account_t_get_state_sessiongroups_item(JLcom/vivox/service/vx_state_account_t;I)J

    move-result-wide v0

    .line 2448
    .local v0, "vx_state_account_t_get_state_sessiongroups_item":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_state_sessiongroup_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_state_sessiongroup_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_state_buddy_contact_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_state_buddy_contact;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_state_buddy_contact"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_state_buddy_contact;

    .prologue
    .line 2452
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_state_buddy_contact;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_state_buddy_contact;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_buddy_contact_create(J)V

    .line 2453
    return-void
.end method

.method public static vx_state_buddy_contact_free(Lcom/vivox/service/vx_state_buddy_contact_t;)V
    .locals 2
    .param p0, "com_vivox_service_vx_state_buddy_contact_t"    # Lcom/vivox/service/vx_state_buddy_contact_t;

    .prologue
    .line 2456
    invoke-static {p0}, Lcom/vivox/service/vx_state_buddy_contact_t;->getCPtr(Lcom/vivox/service/vx_state_buddy_contact_t;)J

    move-result-wide v0

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_buddy_contact_free(JLcom/vivox/service/vx_state_buddy_contact_t;)V

    .line 2457
    return-void
.end method

.method public static vx_state_buddy_contact_list_create(ILcom/vivox/service/SWIGTYPE_p_p_p_vx_state_buddy_contact;)V
    .locals 2
    .param p0, "i"    # I
    .param p1, "sWIGTYPE_p_p_p_vx_state_buddy_contact"    # Lcom/vivox/service/SWIGTYPE_p_p_p_vx_state_buddy_contact;

    .prologue
    .line 2460
    invoke-static {p1}, Lcom/vivox/service/SWIGTYPE_p_p_p_vx_state_buddy_contact;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_p_vx_state_buddy_contact;)J

    move-result-wide v0

    invoke-static {p0, v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_buddy_contact_list_create(IJ)V

    .line 2461
    return-void
.end method

.method public static vx_state_buddy_contact_list_free(Lcom/vivox/service/SWIGTYPE_p_p_vx_state_buddy_contact;I)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_state_buddy_contact"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_state_buddy_contact;
    .param p1, "i"    # I

    .prologue
    .line 2464
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_state_buddy_contact;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_state_buddy_contact;)J

    move-result-wide v0

    invoke-static {v0, v1, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_buddy_contact_list_free(JI)V

    .line 2465
    return-void
.end method

.method public static vx_state_buddy_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_state_buddy;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_state_buddy"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_state_buddy;

    .prologue
    .line 2468
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_state_buddy;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_state_buddy;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_buddy_create(J)V

    .line 2469
    return-void
.end method

.method public static vx_state_buddy_free(Lcom/vivox/service/vx_state_buddy_t;)V
    .locals 2
    .param p0, "com_vivox_service_vx_state_buddy_t"    # Lcom/vivox/service/vx_state_buddy_t;

    .prologue
    .line 2472
    invoke-static {p0}, Lcom/vivox/service/vx_state_buddy_t;->getCPtr(Lcom/vivox/service/vx_state_buddy_t;)J

    move-result-wide v0

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_buddy_free(JLcom/vivox/service/vx_state_buddy_t;)V

    .line 2473
    return-void
.end method

.method public static vx_state_buddy_group_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_state_buddy_group;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_state_buddy_group"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_state_buddy_group;

    .prologue
    .line 2476
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_state_buddy_group;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_state_buddy_group;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_buddy_group_create(J)V

    .line 2477
    return-void
.end method

.method public static vx_state_buddy_group_free(Lcom/vivox/service/vx_state_buddy_group_t;)V
    .locals 2
    .param p0, "com_vivox_service_vx_state_buddy_group_t"    # Lcom/vivox/service/vx_state_buddy_group_t;

    .prologue
    .line 2480
    invoke-static {p0}, Lcom/vivox/service/vx_state_buddy_group_t;->getCPtr(Lcom/vivox/service/vx_state_buddy_group_t;)J

    move-result-wide v0

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_buddy_group_free(JLcom/vivox/service/vx_state_buddy_group_t;)V

    .line 2481
    return-void
.end method

.method public static vx_state_buddy_group_list_create(ILcom/vivox/service/SWIGTYPE_p_p_p_vx_state_buddy_group;)V
    .locals 2
    .param p0, "i"    # I
    .param p1, "sWIGTYPE_p_p_p_vx_state_buddy_group"    # Lcom/vivox/service/SWIGTYPE_p_p_p_vx_state_buddy_group;

    .prologue
    .line 2484
    invoke-static {p1}, Lcom/vivox/service/SWIGTYPE_p_p_p_vx_state_buddy_group;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_p_vx_state_buddy_group;)J

    move-result-wide v0

    invoke-static {p0, v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_buddy_group_list_create(IJ)V

    .line 2485
    return-void
.end method

.method public static vx_state_buddy_group_list_free(Lcom/vivox/service/SWIGTYPE_p_p_vx_state_buddy_group;I)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_state_buddy_group"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_state_buddy_group;
    .param p1, "i"    # I

    .prologue
    .line 2488
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_state_buddy_group;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_state_buddy_group;)J

    move-result-wide v0

    invoke-static {v0, v1, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_buddy_group_list_free(JI)V

    .line 2489
    return-void
.end method

.method public static vx_state_buddy_list_create(ILcom/vivox/service/SWIGTYPE_p_p_p_vx_state_buddy;)V
    .locals 2
    .param p0, "i"    # I
    .param p1, "sWIGTYPE_p_p_p_vx_state_buddy"    # Lcom/vivox/service/SWIGTYPE_p_p_p_vx_state_buddy;

    .prologue
    .line 2492
    invoke-static {p1}, Lcom/vivox/service/SWIGTYPE_p_p_p_vx_state_buddy;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_p_vx_state_buddy;)J

    move-result-wide v0

    invoke-static {p0, v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_buddy_list_create(IJ)V

    .line 2493
    return-void
.end method

.method public static vx_state_buddy_list_free(Lcom/vivox/service/SWIGTYPE_p_p_vx_state_buddy;I)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_state_buddy"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_state_buddy;
    .param p1, "i"    # I

    .prologue
    .line 2496
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_state_buddy;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_state_buddy;)J

    move-result-wide v0

    invoke-static {v0, v1, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_buddy_list_free(JI)V

    .line 2497
    return-void
.end method

.method public static vx_state_connector_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_state_connector;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_state_connector"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_state_connector;

    .prologue
    .line 2500
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_state_connector;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_state_connector;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_connector_create(J)V

    .line 2501
    return-void
.end method

.method public static vx_state_connector_free(Lcom/vivox/service/vx_state_connector_t;)V
    .locals 2
    .param p0, "com_vivox_service_vx_state_connector_t"    # Lcom/vivox/service/vx_state_connector_t;

    .prologue
    .line 2504
    invoke-static {p0}, Lcom/vivox/service/vx_state_connector_t;->getCPtr(Lcom/vivox/service/vx_state_connector_t;)J

    move-result-wide v0

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_connector_free(JLcom/vivox/service/vx_state_connector_t;)V

    .line 2505
    return-void
.end method

.method public static vx_state_connector_list_create(ILcom/vivox/service/SWIGTYPE_p_p_p_vx_state_connector;)V
    .locals 2
    .param p0, "i"    # I
    .param p1, "sWIGTYPE_p_p_p_vx_state_connector"    # Lcom/vivox/service/SWIGTYPE_p_p_p_vx_state_connector;

    .prologue
    .line 2508
    invoke-static {p1}, Lcom/vivox/service/SWIGTYPE_p_p_p_vx_state_connector;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_p_vx_state_connector;)J

    move-result-wide v0

    invoke-static {p0, v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_connector_list_create(IJ)V

    .line 2509
    return-void
.end method

.method public static vx_state_connector_list_free(Lcom/vivox/service/SWIGTYPE_p_p_vx_state_connector;I)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_state_connector"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_state_connector;
    .param p1, "i"    # I

    .prologue
    .line 2512
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_state_connector;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_state_connector;)J

    move-result-wide v0

    invoke-static {v0, v1, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_connector_list_free(JI)V

    .line 2513
    return-void
.end method

.method public static vx_state_connector_t_get_state_accounts_item(Lcom/vivox/service/vx_state_connector_t;I)Lcom/vivox/service/vx_state_account_t;
    .locals 4
    .param p0, "com_vivox_service_vx_state_connector_t"    # Lcom/vivox/service/vx_state_connector_t;
    .param p1, "i"    # I

    .prologue
    .line 2516
    invoke-static {p0}, Lcom/vivox/service/vx_state_connector_t;->getCPtr(Lcom/vivox/service/vx_state_connector_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_connector_t_get_state_accounts_item(JLcom/vivox/service/vx_state_connector_t;I)J

    move-result-wide v0

    .line 2517
    .local v0, "vx_state_connector_t_get_state_accounts_item":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_state_account_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_state_account_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_state_participant_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_state_participant;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_state_participant"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_state_participant;

    .prologue
    .line 2521
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_state_participant;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_state_participant;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_participant_create(J)V

    .line 2522
    return-void
.end method

.method public static vx_state_participant_free(Lcom/vivox/service/vx_state_participant_t;)V
    .locals 2
    .param p0, "com_vivox_service_vx_state_participant_t"    # Lcom/vivox/service/vx_state_participant_t;

    .prologue
    .line 2525
    invoke-static {p0}, Lcom/vivox/service/vx_state_participant_t;->getCPtr(Lcom/vivox/service/vx_state_participant_t;)J

    move-result-wide v0

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_participant_free(JLcom/vivox/service/vx_state_participant_t;)V

    .line 2526
    return-void
.end method

.method public static vx_state_participant_list_create(ILcom/vivox/service/SWIGTYPE_p_p_p_vx_state_participant;)V
    .locals 2
    .param p0, "i"    # I
    .param p1, "sWIGTYPE_p_p_p_vx_state_participant"    # Lcom/vivox/service/SWIGTYPE_p_p_p_vx_state_participant;

    .prologue
    .line 2529
    invoke-static {p1}, Lcom/vivox/service/SWIGTYPE_p_p_p_vx_state_participant;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_p_vx_state_participant;)J

    move-result-wide v0

    invoke-static {p0, v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_participant_list_create(IJ)V

    .line 2530
    return-void
.end method

.method public static vx_state_participant_list_free(Lcom/vivox/service/SWIGTYPE_p_p_vx_state_participant;I)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_state_participant"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_state_participant;
    .param p1, "i"    # I

    .prologue
    .line 2533
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_state_participant;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_state_participant;)J

    move-result-wide v0

    invoke-static {v0, v1, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_participant_list_free(JI)V

    .line 2534
    return-void
.end method

.method public static vx_state_session_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_state_session;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_state_session"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_state_session;

    .prologue
    .line 2537
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_state_session;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_state_session;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_session_create(J)V

    .line 2538
    return-void
.end method

.method public static vx_state_session_free(Lcom/vivox/service/vx_state_session_t;)V
    .locals 2
    .param p0, "com_vivox_service_vx_state_session_t"    # Lcom/vivox/service/vx_state_session_t;

    .prologue
    .line 2541
    invoke-static {p0}, Lcom/vivox/service/vx_state_session_t;->getCPtr(Lcom/vivox/service/vx_state_session_t;)J

    move-result-wide v0

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_session_free(JLcom/vivox/service/vx_state_session_t;)V

    .line 2542
    return-void
.end method

.method public static vx_state_session_list_create(ILcom/vivox/service/SWIGTYPE_p_p_p_vx_state_session;)V
    .locals 2
    .param p0, "i"    # I
    .param p1, "sWIGTYPE_p_p_p_vx_state_session"    # Lcom/vivox/service/SWIGTYPE_p_p_p_vx_state_session;

    .prologue
    .line 2545
    invoke-static {p1}, Lcom/vivox/service/SWIGTYPE_p_p_p_vx_state_session;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_p_vx_state_session;)J

    move-result-wide v0

    invoke-static {p0, v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_session_list_create(IJ)V

    .line 2546
    return-void
.end method

.method public static vx_state_session_list_free(Lcom/vivox/service/SWIGTYPE_p_p_vx_state_session;I)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_state_session"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_state_session;
    .param p1, "i"    # I

    .prologue
    .line 2549
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_state_session;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_state_session;)J

    move-result-wide v0

    invoke-static {v0, v1, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_session_list_free(JI)V

    .line 2550
    return-void
.end method

.method public static vx_state_session_t_get_state_participants_item(Lcom/vivox/service/vx_state_session_t;I)Lcom/vivox/service/vx_state_participant_t;
    .locals 4
    .param p0, "com_vivox_service_vx_state_session_t"    # Lcom/vivox/service/vx_state_session_t;
    .param p1, "i"    # I

    .prologue
    .line 2553
    invoke-static {p0}, Lcom/vivox/service/vx_state_session_t;->getCPtr(Lcom/vivox/service/vx_state_session_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_session_t_get_state_participants_item(JLcom/vivox/service/vx_state_session_t;I)J

    move-result-wide v0

    .line 2554
    .local v0, "vx_state_session_t_get_state_participants_item":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_state_participant_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_state_participant_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_state_sessiongroup_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_state_sessiongroup;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_state_sessiongroup"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_state_sessiongroup;

    .prologue
    .line 2558
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_state_sessiongroup;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_state_sessiongroup;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_sessiongroup_create(J)V

    .line 2559
    return-void
.end method

.method public static vx_state_sessiongroup_free(Lcom/vivox/service/vx_state_sessiongroup_t;)V
    .locals 2
    .param p0, "com_vivox_service_vx_state_sessiongroup_t"    # Lcom/vivox/service/vx_state_sessiongroup_t;

    .prologue
    .line 2562
    invoke-static {p0}, Lcom/vivox/service/vx_state_sessiongroup_t;->getCPtr(Lcom/vivox/service/vx_state_sessiongroup_t;)J

    move-result-wide v0

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_sessiongroup_free(JLcom/vivox/service/vx_state_sessiongroup_t;)V

    .line 2563
    return-void
.end method

.method public static vx_state_sessiongroup_list_create(ILcom/vivox/service/SWIGTYPE_p_p_p_vx_state_sessiongroup;)V
    .locals 2
    .param p0, "i"    # I
    .param p1, "sWIGTYPE_p_p_p_vx_state_sessiongroup"    # Lcom/vivox/service/SWIGTYPE_p_p_p_vx_state_sessiongroup;

    .prologue
    .line 2566
    invoke-static {p1}, Lcom/vivox/service/SWIGTYPE_p_p_p_vx_state_sessiongroup;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_p_vx_state_sessiongroup;)J

    move-result-wide v0

    invoke-static {p0, v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_sessiongroup_list_create(IJ)V

    .line 2567
    return-void
.end method

.method public static vx_state_sessiongroup_list_free(Lcom/vivox/service/SWIGTYPE_p_p_vx_state_sessiongroup;I)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_state_sessiongroup"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_state_sessiongroup;
    .param p1, "i"    # I

    .prologue
    .line 2570
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_state_sessiongroup;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_state_sessiongroup;)J

    move-result-wide v0

    invoke-static {v0, v1, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_sessiongroup_list_free(JI)V

    .line 2571
    return-void
.end method

.method public static vx_state_sessiongroup_t_get_state_sessions_item(Lcom/vivox/service/vx_state_sessiongroup_t;I)Lcom/vivox/service/vx_state_session_t;
    .locals 4
    .param p0, "com_vivox_service_vx_state_sessiongroup_t"    # Lcom/vivox/service/vx_state_sessiongroup_t;
    .param p1, "i"    # I

    .prologue
    .line 2574
    invoke-static {p0}, Lcom/vivox/service/vx_state_sessiongroup_t;->getCPtr(Lcom/vivox/service/vx_state_sessiongroup_t;)J

    move-result-wide v2

    invoke-static {v2, v3, p0, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_state_sessiongroup_t_get_state_sessions_item(JLcom/vivox/service/vx_state_sessiongroup_t;I)J

    move-result-wide v0

    .line 2575
    .local v0, "vx_state_sessiongroup_t_get_state_sessions_item":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_state_session_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_state_session_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_strdup(Ljava/lang/String;)Ljava/lang/String;
    .locals 1
    .param p0, "str"    # Ljava/lang/String;

    .prologue
    .line 2579
    invoke-static {p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_strdup(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public static vx_string_list_create(ILcom/vivox/service/SWIGTYPE_p_p_p_char;)V
    .locals 2
    .param p0, "i"    # I
    .param p1, "sWIGTYPE_p_p_p_char"    # Lcom/vivox/service/SWIGTYPE_p_p_p_char;

    .prologue
    .line 2583
    invoke-static {p1}, Lcom/vivox/service/SWIGTYPE_p_p_p_char;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_p_char;)J

    move-result-wide v0

    invoke-static {p0, v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_string_list_create(IJ)V

    .line 2584
    return-void
.end method

.method public static vx_string_list_free(Lcom/vivox/service/SWIGTYPE_p_p_char;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_char"    # Lcom/vivox/service/SWIGTYPE_p_p_char;

    .prologue
    .line 2587
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_char;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_char;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_string_list_free(J)V

    .line 2588
    return-void
.end method

.method public static vx_uninitialize()I
    .locals 1

    .prologue
    .line 2591
    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->vx_uninitialize()I

    move-result v0

    return v0
.end method

.method public static vx_unregister_logging_handler(Lcom/vivox/service/SWIGTYPE_p_f_p_void__void;Lcom/vivox/service/SWIGTYPE_p_void;)V
    .locals 4
    .param p0, "sWIGTYPE_p_f_p_void__void"    # Lcom/vivox/service/SWIGTYPE_p_f_p_void__void;
    .param p1, "sWIGTYPE_p_void"    # Lcom/vivox/service/SWIGTYPE_p_void;

    .prologue
    .line 2595
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_f_p_void__void;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_f_p_void__void;)J

    move-result-wide v0

    invoke-static {p1}, Lcom/vivox/service/SWIGTYPE_p_void;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_void;)J

    move-result-wide v2

    invoke-static {v0, v1, v2, v3}, Lcom/vivox/service/VxClientProxyJNI;->vx_unregister_logging_handler(JJ)V

    .line 2596
    return-void
.end method

.method public static vx_unregister_message_notification_handler(Lcom/vivox/service/SWIGTYPE_p_f_p_void__void;Lcom/vivox/service/SWIGTYPE_p_void;)V
    .locals 4
    .param p0, "sWIGTYPE_p_f_p_void__void"    # Lcom/vivox/service/SWIGTYPE_p_f_p_void__void;
    .param p1, "sWIGTYPE_p_void"    # Lcom/vivox/service/SWIGTYPE_p_void;

    .prologue
    .line 2599
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_f_p_void__void;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_f_p_void__void;)J

    move-result-wide v0

    invoke-static {p1}, Lcom/vivox/service/SWIGTYPE_p_void;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_void;)J

    move-result-wide v2

    invoke-static {v0, v1, v2, v3}, Lcom/vivox/service/VxClientProxyJNI;->vx_unregister_message_notification_handler(JJ)V

    .line 2600
    return-void
.end method

.method public static vx_user_channel_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_user_channel;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_user_channel"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_user_channel;

    .prologue
    .line 2603
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_user_channel;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_user_channel;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_user_channel_create(J)V

    .line 2604
    return-void
.end method

.method public static vx_user_channel_free(Lcom/vivox/service/vx_user_channel_t;)V
    .locals 2
    .param p0, "com_vivox_service_vx_user_channel_t"    # Lcom/vivox/service/vx_user_channel_t;

    .prologue
    .line 2607
    invoke-static {p0}, Lcom/vivox/service/vx_user_channel_t;->getCPtr(Lcom/vivox/service/vx_user_channel_t;)J

    move-result-wide v0

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_user_channel_free(JLcom/vivox/service/vx_user_channel_t;)V

    .line 2608
    return-void
.end method

.method public static vx_user_channels_create(ILcom/vivox/service/SWIGTYPE_p_p_p_vx_user_channel;)V
    .locals 2
    .param p0, "i"    # I
    .param p1, "sWIGTYPE_p_p_p_vx_user_channel"    # Lcom/vivox/service/SWIGTYPE_p_p_p_vx_user_channel;

    .prologue
    .line 2611
    invoke-static {p1}, Lcom/vivox/service/SWIGTYPE_p_p_p_vx_user_channel;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_p_vx_user_channel;)J

    move-result-wide v0

    invoke-static {p0, v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_user_channels_create(IJ)V

    .line 2612
    return-void
.end method

.method public static vx_user_channels_free(Lcom/vivox/service/SWIGTYPE_p_p_vx_user_channel;I)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_user_channel"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_user_channel;
    .param p1, "i"    # I

    .prologue
    .line 2615
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_user_channel;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_user_channel;)J

    move-result-wide v0

    invoke-static {v0, v1, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_user_channels_free(JI)V

    .line 2616
    return-void
.end method

.method public static vx_voice_font_create(Lcom/vivox/service/SWIGTYPE_p_p_vx_voice_font;)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_voice_font"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_voice_font;

    .prologue
    .line 2619
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_voice_font;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_voice_font;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_voice_font_create(J)V

    .line 2620
    return-void
.end method

.method public static vx_voice_font_free(Lcom/vivox/service/vx_voice_font_t;)V
    .locals 2
    .param p0, "com_vivox_service_vx_voice_font_t"    # Lcom/vivox/service/vx_voice_font_t;

    .prologue
    .line 2623
    invoke-static {p0}, Lcom/vivox/service/vx_voice_font_t;->getCPtr(Lcom/vivox/service/vx_voice_font_t;)J

    move-result-wide v0

    invoke-static {v0, v1, p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_voice_font_free(JLcom/vivox/service/vx_voice_font_t;)V

    .line 2624
    return-void
.end method

.method public static vx_voice_font_list_create(ILcom/vivox/service/SWIGTYPE_p_p_p_vx_voice_font;)V
    .locals 2
    .param p0, "i"    # I
    .param p1, "sWIGTYPE_p_p_p_vx_voice_font"    # Lcom/vivox/service/SWIGTYPE_p_p_p_vx_voice_font;

    .prologue
    .line 2627
    invoke-static {p1}, Lcom/vivox/service/SWIGTYPE_p_p_p_vx_voice_font;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_p_vx_voice_font;)J

    move-result-wide v0

    invoke-static {p0, v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_voice_font_list_create(IJ)V

    .line 2628
    return-void
.end method

.method public static vx_voice_font_list_free(Lcom/vivox/service/SWIGTYPE_p_p_vx_voice_font;I)V
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_voice_font"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_voice_font;
    .param p1, "i"    # I

    .prologue
    .line 2631
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_p_vx_voice_font;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_voice_font;)J

    move-result-wide v0

    invoke-static {v0, v1, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_voice_font_list_free(JI)V

    .line 2632
    return-void
.end method

.method public static vx_vxr_file_close(Lcom/vivox/service/SWIGTYPE_p_void;)I
    .locals 2
    .param p0, "sWIGTYPE_p_void"    # Lcom/vivox/service/SWIGTYPE_p_void;

    .prologue
    .line 2635
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_void;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_void;)J

    move-result-wide v0

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_vxr_file_close(J)I

    move-result v0

    return v0
.end method

.method public static vx_vxr_file_get_frame_count(Lcom/vivox/service/SWIGTYPE_p_void;Lcom/vivox/service/SWIGTYPE_p_int;)I
    .locals 4
    .param p0, "sWIGTYPE_p_void"    # Lcom/vivox/service/SWIGTYPE_p_void;
    .param p1, "sWIGTYPE_p_int"    # Lcom/vivox/service/SWIGTYPE_p_int;

    .prologue
    .line 2639
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_void;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_void;)J

    move-result-wide v0

    invoke-static {p1}, Lcom/vivox/service/SWIGTYPE_p_int;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_int;)J

    move-result-wide v2

    invoke-static {v0, v1, v2, v3}, Lcom/vivox/service/VxClientProxyJNI;->vx_vxr_file_get_frame_count(JJ)I

    move-result v0

    return v0
.end method

.method public static vx_vxr_file_move_to_frame(Lcom/vivox/service/SWIGTYPE_p_void;I)I
    .locals 2
    .param p0, "sWIGTYPE_p_void"    # Lcom/vivox/service/SWIGTYPE_p_void;
    .param p1, "i"    # I

    .prologue
    .line 2643
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_void;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_void;)J

    move-result-wide v0

    invoke-static {v0, v1, p1}, Lcom/vivox/service/VxClientProxyJNI;->vx_vxr_file_move_to_frame(JI)I

    move-result v0

    return v0
.end method

.method public static vx_vxr_file_open(Ljava/lang/String;Ljava/lang/String;Lcom/vivox/service/SWIGTYPE_p_p_void;)I
    .locals 2
    .param p0, "str"    # Ljava/lang/String;
    .param p1, "str2"    # Ljava/lang/String;
    .param p2, "sWIGTYPE_p_p_void"    # Lcom/vivox/service/SWIGTYPE_p_p_void;

    .prologue
    .line 2647
    invoke-static {p2}, Lcom/vivox/service/SWIGTYPE_p_p_void;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_void;)J

    move-result-wide v0

    invoke-static {p0, p1, v0, v1}, Lcom/vivox/service/VxClientProxyJNI;->vx_vxr_file_open(Ljava/lang/String;Ljava/lang/String;J)I

    move-result v0

    return v0
.end method

.method public static vx_vxr_file_read_frame(Lcom/vivox/service/SWIGTYPE_p_void;Lcom/vivox/service/SWIGTYPE_p_void;ILcom/vivox/service/SWIGTYPE_p_int;Lcom/vivox/service/SWIGTYPE_p_vx_recording_frame_type_t;)I
    .locals 9
    .param p0, "sWIGTYPE_p_void"    # Lcom/vivox/service/SWIGTYPE_p_void;
    .param p1, "sWIGTYPE_p_void2"    # Lcom/vivox/service/SWIGTYPE_p_void;
    .param p2, "i"    # I
    .param p3, "sWIGTYPE_p_int"    # Lcom/vivox/service/SWIGTYPE_p_int;
    .param p4, "sWIGTYPE_p_vx_recording_frame_type_t"    # Lcom/vivox/service/SWIGTYPE_p_vx_recording_frame_type_t;

    .prologue
    .line 2651
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_void;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_void;)J

    move-result-wide v0

    invoke-static {p1}, Lcom/vivox/service/SWIGTYPE_p_void;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_void;)J

    move-result-wide v2

    invoke-static {p3}, Lcom/vivox/service/SWIGTYPE_p_int;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_int;)J

    move-result-wide v5

    invoke-static {p4}, Lcom/vivox/service/SWIGTYPE_p_vx_recording_frame_type_t;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_vx_recording_frame_type_t;)J

    move-result-wide v7

    move v4, p2

    invoke-static/range {v0 .. v8}, Lcom/vivox/service/VxClientProxyJNI;->vx_vxr_file_read_frame(JJIJJ)I

    move-result v0

    return v0
.end method

.method public static vx_vxr_file_write_frame(Lcom/vivox/service/SWIGTYPE_p_void;Lcom/vivox/service/vx_recording_frame_type_t;Lcom/vivox/service/SWIGTYPE_p_void;I)I
    .locals 6
    .param p0, "sWIGTYPE_p_void"    # Lcom/vivox/service/SWIGTYPE_p_void;
    .param p1, "com_vivox_service_vx_recording_frame_type_t"    # Lcom/vivox/service/vx_recording_frame_type_t;
    .param p2, "sWIGTYPE_p_void2"    # Lcom/vivox/service/SWIGTYPE_p_void;
    .param p3, "i"    # I

    .prologue
    .line 2655
    invoke-static {p0}, Lcom/vivox/service/SWIGTYPE_p_void;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_void;)J

    move-result-wide v0

    invoke-virtual {p1}, Lcom/vivox/service/vx_recording_frame_type_t;->swigValue()I

    move-result v2

    invoke-static {p2}, Lcom/vivox/service/SWIGTYPE_p_void;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_void;)J

    move-result-wide v3

    move v5, p3

    invoke-static/range {v0 .. v5}, Lcom/vivox/service/VxClientProxyJNI;->vx_vxr_file_write_frame(JIJI)I

    move-result v0

    return v0
.end method

.method public static vx_wait_for_message(I)Lcom/vivox/service/vx_message_base_t;
    .locals 4
    .param p0, "i"    # I

    .prologue
    .line 2659
    invoke-static {p0}, Lcom/vivox/service/VxClientProxyJNI;->vx_wait_for_message(I)J

    move-result-wide v0

    .line 2660
    .local v0, "vx_wait_for_message":J
    const-wide/16 v2, 0x0

    cmp-long v2, v0, v2

    if-nez v2, :cond_0

    const/4 v2, 0x0

    :goto_0
    return-object v2

    :cond_0
    new-instance v2, Lcom/vivox/service/vx_message_base_t;

    const/4 v3, 0x0

    invoke-direct {v2, v0, v1, v3}, Lcom/vivox/service/vx_message_base_t;-><init>(JZ)V

    goto :goto_0
.end method

.method public static vx_xml_to_event(Ljava/lang/String;Lcom/vivox/service/SWIGTYPE_p_p_void;Lcom/vivox/service/SWIGTYPE_p_p_char;)Lcom/vivox/service/vx_event_type;
    .locals 4
    .param p0, "str"    # Ljava/lang/String;
    .param p1, "sWIGTYPE_p_p_void"    # Lcom/vivox/service/SWIGTYPE_p_p_void;
    .param p2, "sWIGTYPE_p_p_char"    # Lcom/vivox/service/SWIGTYPE_p_p_char;

    .prologue
    .line 2664
    invoke-static {p1}, Lcom/vivox/service/SWIGTYPE_p_p_void;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_void;)J

    move-result-wide v0

    invoke-static {p2}, Lcom/vivox/service/SWIGTYPE_p_p_char;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_char;)J

    move-result-wide v2

    invoke-static {p0, v0, v1, v2, v3}, Lcom/vivox/service/VxClientProxyJNI;->vx_xml_to_event(Ljava/lang/String;JJ)I

    move-result v0

    invoke-static {v0}, Lcom/vivox/service/vx_event_type;->swigToEnum(I)Lcom/vivox/service/vx_event_type;

    move-result-object v0

    return-object v0
.end method

.method public static vx_xml_to_request(Ljava/lang/String;Lcom/vivox/service/SWIGTYPE_p_p_void;Lcom/vivox/service/SWIGTYPE_p_p_char;)Lcom/vivox/service/vx_request_type;
    .locals 4
    .param p0, "str"    # Ljava/lang/String;
    .param p1, "sWIGTYPE_p_p_void"    # Lcom/vivox/service/SWIGTYPE_p_p_void;
    .param p2, "sWIGTYPE_p_p_char"    # Lcom/vivox/service/SWIGTYPE_p_p_char;

    .prologue
    .line 2668
    invoke-static {p1}, Lcom/vivox/service/SWIGTYPE_p_p_void;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_void;)J

    move-result-wide v0

    invoke-static {p2}, Lcom/vivox/service/SWIGTYPE_p_p_char;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_char;)J

    move-result-wide v2

    invoke-static {p0, v0, v1, v2, v3}, Lcom/vivox/service/VxClientProxyJNI;->vx_xml_to_request(Ljava/lang/String;JJ)I

    move-result v0

    invoke-static {v0}, Lcom/vivox/service/vx_request_type;->swigToEnum(I)Lcom/vivox/service/vx_request_type;

    move-result-object v0

    return-object v0
.end method

.method public static vx_xml_to_response(Ljava/lang/String;Lcom/vivox/service/SWIGTYPE_p_p_void;Lcom/vivox/service/SWIGTYPE_p_p_char;)Lcom/vivox/service/vx_response_type;
    .locals 4
    .param p0, "str"    # Ljava/lang/String;
    .param p1, "sWIGTYPE_p_p_void"    # Lcom/vivox/service/SWIGTYPE_p_p_void;
    .param p2, "sWIGTYPE_p_p_char"    # Lcom/vivox/service/SWIGTYPE_p_p_char;

    .prologue
    .line 2672
    invoke-static {p1}, Lcom/vivox/service/SWIGTYPE_p_p_void;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_void;)J

    move-result-wide v0

    invoke-static {p2}, Lcom/vivox/service/SWIGTYPE_p_p_char;->getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_char;)J

    move-result-wide v2

    invoke-static {p0, v0, v1, v2, v3}, Lcom/vivox/service/VxClientProxyJNI;->vx_xml_to_response(Ljava/lang/String;JJ)I

    move-result v0

    invoke-static {v0}, Lcom/vivox/service/vx_response_type;->swigToEnum(I)Lcom/vivox/service/vx_response_type;

    move-result-object v0

    return-object v0
.end method

.method public static xml_to_request(Ljava/lang/String;)Lcom/vivox/service/vx_req_base_t;
    .locals 4
    .param p0, "str"    # Ljava/lang/String;

    .prologue
    .line 2676
    invoke-static {p0}, Lcom/vivox/service/VxClientProxyJNI;->xml_to_request(Ljava/lang/String;)J

    move-result-wide v0

    .line 2677
    .local v0, "xml_to_request":J
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
