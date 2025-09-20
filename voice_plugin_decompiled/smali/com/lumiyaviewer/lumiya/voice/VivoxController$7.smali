.class Lcom/lumiyaviewer/lumiya/voice/VivoxController$7;
.super Ljava/lang/Object;
.source "VivoxController.java"

# interfaces
.implements Ljava/lang/Runnable;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/lumiyaviewer/lumiya/voice/VivoxController;->AcceptCall(Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceAcceptCall;)V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

.field final synthetic val$message:Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceAcceptCall;


# direct methods
.method constructor <init>(Lcom/lumiyaviewer/lumiya/voice/VivoxController;Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceAcceptCall;)V
    .locals 0
    .param p1, "this$0"    # Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    .prologue
    .line 431
    iput-object p1, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$7;->this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    iput-object p2, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$7;->val$message:Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceAcceptCall;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public run()V
    .locals 8

    .prologue
    const/4 v7, 0x1

    const/4 v6, 0x0

    .line 434
    const/4 v1, 0x0

    .line 436
    .local v1, "session":Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;
    iget-object v2, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$7;->val$message:Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceAcceptCall;

    iget-object v2, v2, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceAcceptCall;->sessionHandle:Ljava/lang/String;

    if-eqz v2, :cond_1

    .line 437
    const-string v2, "Voice: trying to accept session with handle %s"

    new-array v3, v7, [Ljava/lang/Object;

    iget-object v4, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$7;->val$message:Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceAcceptCall;

    iget-object v4, v4, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceAcceptCall;->sessionHandle:Ljava/lang/String;

    aput-object v4, v3, v6

    invoke-static {v2, v3}, Lcom/lumiyaviewer/lumiya/voice/Debug;->Printf(Ljava/lang/String;[Ljava/lang/Object;)V

    .line 438
    iget-object v2, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$7;->this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    invoke-static {v2}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->access$1200(Lcom/lumiyaviewer/lumiya/voice/VivoxController;)Ljava/util/Map;

    move-result-object v2

    iget-object v3, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$7;->val$message:Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceAcceptCall;

    iget-object v3, v3, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceAcceptCall;->sessionHandle:Ljava/lang/String;

    invoke-interface {v2, v3}, Ljava/util/Map;->get(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v1

    .end local v1    # "session":Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;
    check-cast v1, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;

    .line 450
    .restart local v1    # "session":Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;
    :cond_0
    :goto_0
    if-eqz v1, :cond_3

    .line 451
    const-string v2, "Voice: accepting session"

    new-array v3, v6, [Ljava/lang/Object;

    invoke-static {v2, v3}, Lcom/lumiyaviewer/lumiya/voice/Debug;->Printf(Ljava/lang/String;[Ljava/lang/Object;)V

    .line 452
    invoke-virtual {v1}, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;->mediaConnect()V

    .line 455
    :goto_1
    return-void

    .line 440
    :cond_1
    const-string v2, "Voice: trying to accept session with uri %s"

    new-array v3, v7, [Ljava/lang/Object;

    iget-object v4, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$7;->val$message:Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceAcceptCall;

    iget-object v4, v4, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceAcceptCall;->voiceChannelInfo:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;

    iget-object v4, v4, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;->voiceChannelURI:Ljava/lang/String;

    aput-object v4, v3, v6

    invoke-static {v2, v3}, Lcom/lumiyaviewer/lumiya/voice/Debug;->Printf(Ljava/lang/String;[Ljava/lang/Object;)V

    .line 441
    iget-object v2, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$7;->this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    invoke-static {v2}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->access$1200(Lcom/lumiyaviewer/lumiya/voice/VivoxController;)Ljava/util/Map;

    move-result-object v2

    invoke-interface {v2}, Ljava/util/Map;->values()Ljava/util/Collection;

    move-result-object v2

    invoke-interface {v2}, Ljava/util/Collection;->iterator()Ljava/util/Iterator;

    move-result-object v2

    :cond_2
    invoke-interface {v2}, Ljava/util/Iterator;->hasNext()Z

    move-result v3

    if-eqz v3, :cond_0

    invoke-interface {v2}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;

    .line 442
    .local v0, "s":Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;
    const-string v3, "Voice: existing session with uri %s"

    new-array v4, v7, [Ljava/lang/Object;

    invoke-virtual {v0}, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;->getVoiceChannelInfo()Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;

    move-result-object v5

    iget-object v5, v5, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;->voiceChannelURI:Ljava/lang/String;

    aput-object v5, v4, v6

    invoke-static {v3, v4}, Lcom/lumiyaviewer/lumiya/voice/Debug;->Printf(Ljava/lang/String;[Ljava/lang/Object;)V

    .line 443
    invoke-virtual {v0}, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;->getVoiceChannelInfo()Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;

    move-result-object v3

    iget-object v3, v3, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;->voiceChannelURI:Ljava/lang/String;

    iget-object v4, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$7;->val$message:Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceAcceptCall;

    iget-object v4, v4, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceAcceptCall;->voiceChannelInfo:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;

    iget-object v4, v4, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;->voiceChannelURI:Ljava/lang/String;

    invoke-static {v3, v4}, Lcom/google/common/base/Objects;->equal(Ljava/lang/Object;Ljava/lang/Object;)Z

    move-result v3

    if-eqz v3, :cond_2

    .line 444
    move-object v1, v0

    .line 445
    goto :goto_0

    .line 454
    .end local v0    # "s":Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;
    :cond_3
    const-string v2, "Voice: no session to accept"

    new-array v3, v6, [Ljava/lang/Object;

    invoke-static {v2, v3}, Lcom/lumiyaviewer/lumiya/voice/Debug;->Printf(Ljava/lang/String;[Ljava/lang/Object;)V

    goto :goto_1
.end method
