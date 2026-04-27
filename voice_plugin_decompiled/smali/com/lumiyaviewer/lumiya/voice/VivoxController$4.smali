.class Lcom/lumiyaviewer/lumiya/voice/VivoxController$4;
.super Ljava/lang/Object;
.source "VivoxController.java"

# interfaces
.implements Ljava/lang/Runnable;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/lumiyaviewer/lumiya/voice/VivoxController;->ConnectChannel(Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;Ljava/lang/String;Landroid/os/Messenger;)V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

.field final synthetic val$channelCredentials:Ljava/lang/String;

.field final synthetic val$replyTo:Landroid/os/Messenger;

.field final synthetic val$voiceChannelInfo:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;


# direct methods
.method constructor <init>(Lcom/lumiyaviewer/lumiya/voice/VivoxController;Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;Ljava/lang/String;Landroid/os/Messenger;)V
    .locals 0
    .param p1, "this$0"    # Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    .prologue
    .line 362
    iput-object p1, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$4;->this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    iput-object p2, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$4;->val$voiceChannelInfo:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;

    iput-object p3, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$4;->val$channelCredentials:Ljava/lang/String;

    iput-object p4, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$4;->val$replyTo:Landroid/os/Messenger;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public run()V
    .locals 11

    .prologue
    .line 367
    :try_start_0
    iget-object v5, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$4;->this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    invoke-static {v5}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->access$600(Lcom/lumiyaviewer/lumiya/voice/VivoxController;)Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceAccountConnection;

    move-result-object v5

    if-nez v5, :cond_1

    .line 368
    new-instance v5, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceException;

    const-string v6, "Not logged in"

    invoke-direct {v5, v6}, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceException;-><init>(Ljava/lang/String;)V

    throw v5
    :try_end_0
    .catch Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceException; {:try_start_0 .. :try_end_0} :catch_0

    .line 390
    :catch_0
    move-exception v0

    .line 391
    .local v0, "e":Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceException;
    iget-object v5, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$4;->val$replyTo:Landroid/os/Messenger;

    sget-object v6, Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;->VoiceChannelStatus:Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;

    new-instance v7, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceChannelStatus;

    iget-object v8, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$4;->val$voiceChannelInfo:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;

    invoke-static {}, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;->empty()Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;

    move-result-object v9

    invoke-virtual {v0}, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceException;->getMessage()Ljava/lang/String;

    move-result-object v10

    invoke-direct {v7, v8, v9, v10}, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceChannelStatus;-><init>(Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;Ljava/lang/String;)V

    const/4 v8, 0x0

    invoke-static {v5, v6, v7, v8}, Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessenger;->sendMessage(Landroid/os/Messenger;Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessage;Landroid/os/Messenger;)Z

    .line 394
    .end local v0    # "e":Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceException;
    :cond_0
    :goto_0
    return-void

    .line 370
    :cond_1
    :try_start_1
    new-instance v3, Ljava/util/ArrayList;

    invoke-direct {v3}, Ljava/util/ArrayList;-><init>()V

    .line 371
    .local v3, "otherSessions":Ljava/util/List;, "Ljava/util/List<Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;>;"
    const/4 v1, 0x0

    .line 373
    .local v1, "existingSession":Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;
    iget-object v5, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$4;->this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    invoke-static {v5}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->access$1200(Lcom/lumiyaviewer/lumiya/voice/VivoxController;)Ljava/util/Map;

    move-result-object v5

    invoke-interface {v5}, Ljava/util/Map;->values()Ljava/util/Collection;

    move-result-object v5

    invoke-interface {v5}, Ljava/util/Collection;->iterator()Ljava/util/Iterator;

    move-result-object v5

    :goto_1
    invoke-interface {v5}, Ljava/util/Iterator;->hasNext()Z

    move-result v6

    if-eqz v6, :cond_3

    invoke-interface {v5}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v4

    check-cast v4, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;

    .line 374
    .local v4, "session":Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;
    invoke-virtual {v4}, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;->getVoiceChannelInfo()Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;

    move-result-object v6

    iget-object v7, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$4;->val$voiceChannelInfo:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;

    invoke-virtual {v6, v7}, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;->equals(Ljava/lang/Object;)Z

    move-result v6

    if-eqz v6, :cond_2

    .line 375
    move-object v1, v4

    goto :goto_1

    .line 377
    :cond_2
    invoke-interface {v3, v4}, Ljava/util/List;->add(Ljava/lang/Object;)Z

    goto :goto_1

    .line 380
    .end local v4    # "session":Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;
    :cond_3
    invoke-interface {v3}, Ljava/util/List;->iterator()Ljava/util/Iterator;

    move-result-object v5

    :goto_2
    invoke-interface {v5}, Ljava/util/Iterator;->hasNext()Z

    move-result v6

    if-eqz v6, :cond_4

    invoke-interface {v5}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v2

    check-cast v2, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;

    .line 381
    .local v2, "otherSession":Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;
    sget-object v6, Lcom/vivox/service/vx_termination_status;->termination_status_none:Lcom/vivox/service/vx_termination_status;

    invoke-virtual {v2, v6}, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;->mediaDisconnect(Lcom/vivox/service/vx_termination_status;)V

    .line 382
    invoke-virtual {v2}, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;->dispose()V

    goto :goto_2

    .line 385
    .end local v2    # "otherSession":Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;
    :cond_4
    if-nez v1, :cond_0

    .line 386
    iget-object v5, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$4;->this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    invoke-static {v5}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->access$1300(Lcom/lumiyaviewer/lumiya/voice/VivoxController;)Ljava/util/Map;

    move-result-object v5

    iget-object v6, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$4;->val$voiceChannelInfo:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;

    iget-object v6, v6, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;->voiceChannelURI:Ljava/lang/String;

    iget-object v7, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$4;->val$voiceChannelInfo:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;

    invoke-interface {v5, v6, v7}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    .line 387
    iget-object v5, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$4;->this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    invoke-static {v5}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->access$600(Lcom/lumiyaviewer/lumiya/voice/VivoxController;)Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceAccountConnection;

    move-result-object v5

    iget-object v6, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$4;->val$voiceChannelInfo:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;

    iget-object v7, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$4;->val$channelCredentials:Ljava/lang/String;

    invoke-virtual {v5, v6, v7}, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceAccountConnection;->createVoiceSession(Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;Ljava/lang/String;)V
    :try_end_1
    .catch Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceException; {:try_start_1 .. :try_end_1} :catch_0

    goto :goto_0
.end method
