.class Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController$2;
.super Ljava/lang/Object;
.source "VivoxMessageController.java"

# interfaces
.implements Ljava/lang/Runnable;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;


# direct methods
.method constructor <init>(Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;)V
    .locals 0
    .param p1, "this$0"    # Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;

    .prologue
    .line 76
    iput-object p1, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController$2;->this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public run()V
    .locals 12

    .prologue
    const/4 v11, 0x1

    const/4 v10, 0x0

    .line 79
    invoke-static {}, Lcom/lumiyaviewer/lumiya/voice/VivoxMessageQueue;->getInstance()Lcom/lumiyaviewer/lumiya/voice/VivoxMessageQueue;

    move-result-object v1

    .line 80
    .local v1, "messageQueue":Lcom/lumiyaviewer/lumiya/voice/VivoxMessageQueue;
    :cond_0
    :goto_0
    iget-object v8, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController$2;->this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;

    invoke-static {v8}, Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;->access$100(Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;)Ljava/util/concurrent/atomic/AtomicBoolean;

    move-result-object v8

    invoke-virtual {v8}, Ljava/util/concurrent/atomic/AtomicBoolean;->get()Z

    move-result v8

    if-eqz v8, :cond_2

    .line 81
    invoke-virtual {v1}, Lcom/lumiyaviewer/lumiya/voice/VivoxMessageQueue;->getMessage()Lcom/vivox/service/vx_message_base_t;

    move-result-object v6

    .line 82
    .local v6, "vxMessage":Lcom/vivox/service/vx_message_base_t;
    if-eqz v6, :cond_0

    .line 84
    invoke-virtual {v6}, Lcom/vivox/service/vx_message_base_t;->getType()Lcom/vivox/service/vx_message_type;

    move-result-object v7

    .line 85
    .local v7, "vxMessageType":Lcom/vivox/service/vx_message_type;
    const-string v8, "Voice: got vxMessage (%s)"

    new-array v9, v11, [Ljava/lang/Object;

    aput-object v7, v9, v10

    invoke-static {v8, v9}, Lcom/lumiyaviewer/lumiya/voice/Debug;->Printf(Ljava/lang/String;[Ljava/lang/Object;)V

    .line 87
    const/4 v0, 0x0

    .line 89
    .local v0, "handled":Z
    sget-object v8, Lcom/vivox/service/vx_message_type;->msg_response:Lcom/vivox/service/vx_message_type;

    if-ne v7, v8, :cond_1

    .line 90
    new-instance v5, Lcom/vivox/service/vx_resp_base_t;

    invoke-static {v6}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v8

    invoke-direct {v5, v8, v9, v10}, Lcom/vivox/service/vx_resp_base_t;-><init>(JZ)V

    .line 91
    .local v5, "response":Lcom/vivox/service/vx_resp_base_t;
    invoke-virtual {v5}, Lcom/vivox/service/vx_resp_base_t;->getRequest()Lcom/vivox/service/vx_req_base_t;

    move-result-object v3

    .line 92
    .local v3, "request":Lcom/vivox/service/vx_req_base_t;
    if-eqz v3, :cond_1

    .line 93
    invoke-virtual {v3}, Lcom/vivox/service/vx_req_base_t;->getCookie()Ljava/lang/String;

    move-result-object v4

    .line 94
    .local v4, "requestCookie":Ljava/lang/String;
    const-string v8, "Voice: response, requestCookie \'%s\'"

    new-array v9, v11, [Ljava/lang/Object;

    aput-object v4, v9, v10

    invoke-static {v8, v9}, Lcom/lumiyaviewer/lumiya/voice/Debug;->Printf(Ljava/lang/String;[Ljava/lang/Object;)V

    .line 95
    iget-object v8, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController$2;->this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;

    invoke-static {v8}, Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;->access$200(Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;)Ljava/util/Map;

    move-result-object v8

    invoke-interface {v8, v4}, Ljava/util/Map;->remove(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v2

    check-cast v2, Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController$PendingRequest;

    .line 96
    .local v2, "pendingRequest":Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController$PendingRequest;
    if-eqz v2, :cond_1

    .line 97
    invoke-virtual {v2, v5}, Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController$PendingRequest;->signalRequestCompleted(Lcom/vivox/service/vx_resp_base_t;)V

    .line 98
    const/4 v0, 0x1

    .line 103
    .end local v2    # "pendingRequest":Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController$PendingRequest;
    .end local v3    # "request":Lcom/vivox/service/vx_req_base_t;
    .end local v4    # "requestCookie":Ljava/lang/String;
    .end local v5    # "response":Lcom/vivox/service/vx_resp_base_t;
    :cond_1
    if-nez v0, :cond_0

    .line 104
    iget-object v8, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController$2;->this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;

    invoke-static {v8}, Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;->access$300(Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;)Landroid/os/Handler;

    move-result-object v8

    iget-object v9, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController$2;->this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;

    invoke-static {v9}, Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;->access$300(Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;)Landroid/os/Handler;

    move-result-object v9

    invoke-virtual {v9, v11, v6}, Landroid/os/Handler;->obtainMessage(ILjava/lang/Object;)Landroid/os/Message;

    move-result-object v9

    invoke-virtual {v8, v9}, Landroid/os/Handler;->sendMessage(Landroid/os/Message;)Z

    goto :goto_0

    .line 107
    .end local v0    # "handled":Z
    .end local v6    # "vxMessage":Lcom/vivox/service/vx_message_base_t;
    .end local v7    # "vxMessageType":Lcom/vivox/service/vx_message_type;
    :cond_2
    return-void
.end method
