.class Lcom/lumiyaviewer/lumiya/voice/VoiceService$IncomingHandler;
.super Landroid/os/Handler;
.source "VoiceService.java"


# annotations
.annotation build Landroid/annotation/SuppressLint;
    value = {
        "HandlerLeak"
    }
.end annotation

.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/lumiyaviewer/lumiya/voice/VoiceService;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x2
    name = "IncomingHandler"
.end annotation


# instance fields
.field final synthetic this$0:Lcom/lumiyaviewer/lumiya/voice/VoiceService;


# direct methods
.method private constructor <init>(Lcom/lumiyaviewer/lumiya/voice/VoiceService;)V
    .locals 0

    .prologue
    .line 46
    iput-object p1, p0, Lcom/lumiyaviewer/lumiya/voice/VoiceService$IncomingHandler;->this$0:Lcom/lumiyaviewer/lumiya/voice/VoiceService;

    invoke-direct {p0}, Landroid/os/Handler;-><init>()V

    return-void
.end method

.method synthetic constructor <init>(Lcom/lumiyaviewer/lumiya/voice/VoiceService;Lcom/lumiyaviewer/lumiya/voice/VoiceService$1;)V
    .locals 0
    .param p1, "x0"    # Lcom/lumiyaviewer/lumiya/voice/VoiceService;
    .param p2, "x1"    # Lcom/lumiyaviewer/lumiya/voice/VoiceService$1;

    .prologue
    .line 46
    invoke-direct {p0, p1}, Lcom/lumiyaviewer/lumiya/voice/VoiceService$IncomingHandler;-><init>(Lcom/lumiyaviewer/lumiya/voice/VoiceService;)V

    return-void
.end method


# virtual methods
.method public handleMessage(Landroid/os/Message;)V
    .locals 8
    .param p1, "msg"    # Landroid/os/Message;

    .prologue
    const/4 v4, 0x1

    const/4 v5, 0x0

    .line 51
    iget v3, p1, Landroid/os/Message;->what:I

    const/16 v6, 0xc8

    if-ne v3, v6, :cond_1

    iget-object v3, p1, Landroid/os/Message;->obj:Ljava/lang/Object;

    instance-of v3, v3, Landroid/os/Bundle;

    if-eqz v3, :cond_1

    .line 52
    iget-object v0, p1, Landroid/os/Message;->obj:Ljava/lang/Object;

    check-cast v0, Landroid/os/Bundle;

    .line 53
    .local v0, "bundle":Landroid/os/Bundle;
    const-string v3, "message"

    invoke-virtual {v0, v3}, Landroid/os/Bundle;->containsKey(Ljava/lang/String;)Z

    move-result v3

    if-eqz v3, :cond_0

    const-string v3, "messageType"

    invoke-virtual {v0, v3}, Landroid/os/Bundle;->containsKey(Ljava/lang/String;)Z

    move-result v3

    if-eqz v3, :cond_0

    .line 55
    :try_start_0
    const-string v3, "messageType"

    invoke-virtual {v0, v3}, Landroid/os/Bundle;->getString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v3

    invoke-static {v3}, Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;->valueOf(Ljava/lang/String;)Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;

    move-result-object v2

    .line 57
    .local v2, "type":Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;
    const-string v3, "Voice: service message: %s"

    const/4 v4, 0x1

    new-array v4, v4, [Ljava/lang/Object;

    const/4 v5, 0x0

    aput-object v2, v4, v5

    invoke-static {v3, v4}, Lcom/lumiyaviewer/lumiya/voice/Debug;->Printf(Ljava/lang/String;[Ljava/lang/Object;)V

    .line 59
    sget-object v3, Lcom/lumiyaviewer/lumiya/voice/VoiceService$3;->$SwitchMap$com$lumiyaviewer$lumiya$voice$common$VoicePluginMessageType:[I

    invoke-virtual {v2}, Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;->ordinal()I

    move-result v4

    aget v3, v3, v4

    packed-switch v3, :pswitch_data_0

    .line 99
    .end local v0    # "bundle":Landroid/os/Bundle;
    .end local v2    # "type":Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;
    :cond_0
    :goto_0
    return-void

    .line 61
    .restart local v0    # "bundle":Landroid/os/Bundle;
    .restart local v2    # "type":Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;
    :pswitch_0
    iget-object v3, p0, Lcom/lumiyaviewer/lumiya/voice/VoiceService$IncomingHandler;->this$0:Lcom/lumiyaviewer/lumiya/voice/VoiceService;

    new-instance v4, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceInitialize;

    const-string v5, "message"

    invoke-virtual {v0, v5}, Landroid/os/Bundle;->getBundle(Ljava/lang/String;)Landroid/os/Bundle;

    move-result-object v5

    invoke-direct {v4, v5}, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceInitialize;-><init>(Landroid/os/Bundle;)V

    iget-object v5, p1, Landroid/os/Message;->replyTo:Landroid/os/Messenger;

    invoke-static {v3, v4, v5}, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->access$000(Lcom/lumiyaviewer/lumiya/voice/VoiceService;Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceInitialize;Landroid/os/Messenger;)V
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    goto :goto_0

    .line 91
    .end local v2    # "type":Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;
    :catch_0
    move-exception v1

    .line 92
    .local v1, "e":Ljava/lang/Exception;
    invoke-static {v1}, Lcom/lumiyaviewer/lumiya/voice/Debug;->Warning(Ljava/lang/Throwable;)V

    goto :goto_0

    .line 64
    .end local v1    # "e":Ljava/lang/Exception;
    .restart local v2    # "type":Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;
    :pswitch_1
    :try_start_1
    iget-object v3, p0, Lcom/lumiyaviewer/lumiya/voice/VoiceService$IncomingHandler;->this$0:Lcom/lumiyaviewer/lumiya/voice/VoiceService;

    new-instance v4, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceLogin;

    const-string v5, "message"

    invoke-virtual {v0, v5}, Landroid/os/Bundle;->getBundle(Ljava/lang/String;)Landroid/os/Bundle;

    move-result-object v5

    invoke-direct {v4, v5}, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceLogin;-><init>(Landroid/os/Bundle;)V

    iget-object v5, p1, Landroid/os/Message;->replyTo:Landroid/os/Messenger;

    invoke-static {v3, v4, v5}, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->access$100(Lcom/lumiyaviewer/lumiya/voice/VoiceService;Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceLogin;Landroid/os/Messenger;)V

    goto :goto_0

    .line 67
    :pswitch_2
    iget-object v3, p0, Lcom/lumiyaviewer/lumiya/voice/VoiceService$IncomingHandler;->this$0:Lcom/lumiyaviewer/lumiya/voice/VoiceService;

    new-instance v4, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceConnectChannel;

    const-string v5, "message"

    invoke-virtual {v0, v5}, Landroid/os/Bundle;->getBundle(Ljava/lang/String;)Landroid/os/Bundle;

    move-result-object v5

    invoke-direct {v4, v5}, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceConnectChannel;-><init>(Landroid/os/Bundle;)V

    iget-object v5, p1, Landroid/os/Message;->replyTo:Landroid/os/Messenger;

    invoke-static {v3, v4, v5}, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->access$200(Lcom/lumiyaviewer/lumiya/voice/VoiceService;Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceConnectChannel;Landroid/os/Messenger;)V

    goto :goto_0

    .line 70
    :pswitch_3
    iget-object v3, p0, Lcom/lumiyaviewer/lumiya/voice/VoiceService$IncomingHandler;->this$0:Lcom/lumiyaviewer/lumiya/voice/VoiceService;

    new-instance v4, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceSet3DPosition;

    const-string v5, "message"

    invoke-virtual {v0, v5}, Landroid/os/Bundle;->getBundle(Ljava/lang/String;)Landroid/os/Bundle;

    move-result-object v5

    invoke-direct {v4, v5}, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceSet3DPosition;-><init>(Landroid/os/Bundle;)V

    iget-object v5, p1, Landroid/os/Message;->replyTo:Landroid/os/Messenger;

    invoke-static {v3, v4, v5}, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->access$300(Lcom/lumiyaviewer/lumiya/voice/VoiceService;Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceSet3DPosition;Landroid/os/Messenger;)V

    goto :goto_0

    .line 73
    :pswitch_4
    iget-object v3, p0, Lcom/lumiyaviewer/lumiya/voice/VoiceService$IncomingHandler;->this$0:Lcom/lumiyaviewer/lumiya/voice/VoiceService;

    new-instance v4, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceRejectCall;

    const-string v5, "message"

    invoke-virtual {v0, v5}, Landroid/os/Bundle;->getBundle(Ljava/lang/String;)Landroid/os/Bundle;

    move-result-object v5

    invoke-direct {v4, v5}, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceRejectCall;-><init>(Landroid/os/Bundle;)V

    iget-object v5, p1, Landroid/os/Message;->replyTo:Landroid/os/Messenger;

    invoke-static {v3, v4, v5}, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->access$400(Lcom/lumiyaviewer/lumiya/voice/VoiceService;Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceRejectCall;Landroid/os/Messenger;)V

    goto :goto_0

    .line 76
    :pswitch_5
    iget-object v3, p0, Lcom/lumiyaviewer/lumiya/voice/VoiceService$IncomingHandler;->this$0:Lcom/lumiyaviewer/lumiya/voice/VoiceService;

    new-instance v4, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceAcceptCall;

    const-string v5, "message"

    invoke-virtual {v0, v5}, Landroid/os/Bundle;->getBundle(Ljava/lang/String;)Landroid/os/Bundle;

    move-result-object v5

    invoke-direct {v4, v5}, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceAcceptCall;-><init>(Landroid/os/Bundle;)V

    iget-object v5, p1, Landroid/os/Message;->replyTo:Landroid/os/Messenger;

    invoke-static {v3, v4, v5}, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->access$500(Lcom/lumiyaviewer/lumiya/voice/VoiceService;Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceAcceptCall;Landroid/os/Messenger;)V

    goto :goto_0

    .line 79
    :pswitch_6
    iget-object v3, p0, Lcom/lumiyaviewer/lumiya/voice/VoiceService$IncomingHandler;->this$0:Lcom/lumiyaviewer/lumiya/voice/VoiceService;

    new-instance v4, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceTerminateCall;

    const-string v5, "message"

    invoke-virtual {v0, v5}, Landroid/os/Bundle;->getBundle(Ljava/lang/String;)Landroid/os/Bundle;

    move-result-object v5

    invoke-direct {v4, v5}, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceTerminateCall;-><init>(Landroid/os/Bundle;)V

    iget-object v5, p1, Landroid/os/Message;->replyTo:Landroid/os/Messenger;

    invoke-static {v3, v4, v5}, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->access$600(Lcom/lumiyaviewer/lumiya/voice/VoiceService;Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceTerminateCall;Landroid/os/Messenger;)V

    goto/16 :goto_0

    .line 82
    :pswitch_7
    iget-object v3, p0, Lcom/lumiyaviewer/lumiya/voice/VoiceService$IncomingHandler;->this$0:Lcom/lumiyaviewer/lumiya/voice/VoiceService;

    new-instance v4, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceEnableMic;

    const-string v5, "message"

    invoke-virtual {v0, v5}, Landroid/os/Bundle;->getBundle(Ljava/lang/String;)Landroid/os/Bundle;

    move-result-object v5

    invoke-direct {v4, v5}, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceEnableMic;-><init>(Landroid/os/Bundle;)V

    invoke-static {v3, v4}, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->access$700(Lcom/lumiyaviewer/lumiya/voice/VoiceService;Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceEnableMic;)V

    goto/16 :goto_0

    .line 85
    :pswitch_8
    iget-object v3, p0, Lcom/lumiyaviewer/lumiya/voice/VoiceService$IncomingHandler;->this$0:Lcom/lumiyaviewer/lumiya/voice/VoiceService;

    iget-object v4, p1, Landroid/os/Message;->replyTo:Landroid/os/Messenger;

    invoke-static {v3, v4}, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->access$800(Lcom/lumiyaviewer/lumiya/voice/VoiceService;Landroid/os/Messenger;)V

    goto/16 :goto_0

    .line 88
    :pswitch_9
    iget-object v3, p0, Lcom/lumiyaviewer/lumiya/voice/VoiceService$IncomingHandler;->this$0:Lcom/lumiyaviewer/lumiya/voice/VoiceService;

    new-instance v4, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceSetAudioProperties;

    const-string v5, "message"

    invoke-virtual {v0, v5}, Landroid/os/Bundle;->getBundle(Ljava/lang/String;)Landroid/os/Bundle;

    move-result-object v5

    invoke-direct {v4, v5}, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceSetAudioProperties;-><init>(Landroid/os/Bundle;)V

    invoke-static {v3, v4}, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->access$900(Lcom/lumiyaviewer/lumiya/voice/VoiceService;Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceSetAudioProperties;)V
    :try_end_1
    .catch Ljava/lang/Exception; {:try_start_1 .. :try_end_1} :catch_0

    goto/16 :goto_0

    .line 95
    .end local v0    # "bundle":Landroid/os/Bundle;
    .end local v2    # "type":Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;
    :cond_1
    iget v3, p1, Landroid/os/Message;->what:I

    const/16 v6, 0x12c

    if-ne v3, v6, :cond_0

    iget-object v3, p1, Landroid/os/Message;->obj:Ljava/lang/Object;

    instance-of v3, v3, Landroid/os/Messenger;

    if-eqz v3, :cond_0

    .line 96
    iget-object v6, p0, Lcom/lumiyaviewer/lumiya/voice/VoiceService$IncomingHandler;->this$0:Lcom/lumiyaviewer/lumiya/voice/VoiceService;

    iget-object v3, p1, Landroid/os/Message;->obj:Ljava/lang/Object;

    check-cast v3, Landroid/os/Messenger;

    iget v7, p1, Landroid/os/Message;->arg1:I

    if-eqz v7, :cond_2

    :goto_1
    invoke-static {v6, v3, v4}, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->access$1000(Lcom/lumiyaviewer/lumiya/voice/VoiceService;Landroid/os/Messenger;Z)V

    goto/16 :goto_0

    :cond_2
    move v4, v5

    goto :goto_1

    .line 59
    :pswitch_data_0
    .packed-switch 0x1
        :pswitch_0
        :pswitch_1
        :pswitch_2
        :pswitch_3
        :pswitch_4
        :pswitch_5
        :pswitch_6
        :pswitch_7
        :pswitch_8
        :pswitch_9
    .end packed-switch
.end method
