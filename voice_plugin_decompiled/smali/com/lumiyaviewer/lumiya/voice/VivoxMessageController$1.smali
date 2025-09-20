.class Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController$1;
.super Landroid/os/Handler;
.source "VivoxMessageController.java"


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
    .line 53
    iput-object p1, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController$1;->this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;

    invoke-direct {p0}, Landroid/os/Handler;-><init>()V

    return-void
.end method


# virtual methods
.method public handleMessage(Landroid/os/Message;)V
    .locals 6
    .param p1, "msg"    # Landroid/os/Message;

    .prologue
    .line 56
    iget-object v3, p1, Landroid/os/Message;->obj:Ljava/lang/Object;

    instance-of v3, v3, Lcom/vivox/service/vx_message_base_t;

    if-eqz v3, :cond_0

    .line 58
    iget-object v1, p1, Landroid/os/Message;->obj:Ljava/lang/Object;

    check-cast v1, Lcom/vivox/service/vx_message_base_t;

    .line 59
    .local v1, "vxMessage":Lcom/vivox/service/vx_message_base_t;
    invoke-virtual {v1}, Lcom/vivox/service/vx_message_base_t;->getType()Lcom/vivox/service/vx_message_type;

    move-result-object v2

    .line 61
    .local v2, "vxMessageType":Lcom/vivox/service/vx_message_type;
    sget-object v3, Lcom/vivox/service/vx_message_type;->msg_event:Lcom/vivox/service/vx_message_type;

    if-ne v2, v3, :cond_1

    .line 62
    new-instance v0, Lcom/vivox/service/vx_evt_base_t;

    invoke-static {v1}, Lcom/vivox/service/vx_message_base_t;->getCPtr(Lcom/vivox/service/vx_message_base_t;)J

    move-result-wide v4

    const/4 v3, 0x0

    invoke-direct {v0, v4, v5, v3}, Lcom/vivox/service/vx_evt_base_t;-><init>(JZ)V

    .line 63
    .local v0, "vxEvent":Lcom/vivox/service/vx_evt_base_t;
    iget-object v3, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController$1;->this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;

    invoke-static {v3}, Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;->access$000(Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;)Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController$OnVivoxMessageListener;

    move-result-object v3

    invoke-interface {v3, v0}, Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController$OnVivoxMessageListener;->onVivoxEvent(Lcom/vivox/service/vx_evt_base_t;)V

    .line 67
    .end local v0    # "vxEvent":Lcom/vivox/service/vx_evt_base_t;
    .end local v1    # "vxMessage":Lcom/vivox/service/vx_message_base_t;
    .end local v2    # "vxMessageType":Lcom/vivox/service/vx_message_type;
    :cond_0
    :goto_0
    return-void

    .line 65
    .restart local v1    # "vxMessage":Lcom/vivox/service/vx_message_base_t;
    .restart local v2    # "vxMessageType":Lcom/vivox/service/vx_message_type;
    :cond_1
    iget-object v3, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController$1;->this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;

    invoke-static {v3}, Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;->access$000(Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;)Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController$OnVivoxMessageListener;

    move-result-object v3

    invoke-interface {v3, v1}, Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController$OnVivoxMessageListener;->onVivoxMessage(Lcom/vivox/service/vx_message_base_t;)V

    goto :goto_0
.end method
