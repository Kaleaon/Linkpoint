.class Lcom/lumiyaviewer/lumiya/voice/VivoxController$8;
.super Ljava/lang/Object;
.source "VivoxController.java"

# interfaces
.implements Ljava/lang/Runnable;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/lumiyaviewer/lumiya/voice/VivoxController;->TerminateCall(Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceTerminateCall;)V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

.field final synthetic val$message:Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceTerminateCall;


# direct methods
.method constructor <init>(Lcom/lumiyaviewer/lumiya/voice/VivoxController;Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceTerminateCall;)V
    .locals 0
    .param p1, "this$0"    # Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    .prologue
    .line 460
    iput-object p1, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$8;->this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    iput-object p2, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$8;->val$message:Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceTerminateCall;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public run()V
    .locals 6

    .prologue
    .line 463
    new-instance v2, Ljava/util/ArrayList;

    invoke-direct {v2}, Ljava/util/ArrayList;-><init>()V

    .line 464
    .local v2, "terminateSessions":Ljava/util/List;, "Ljava/util/List<Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;>;"
    iget-object v3, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$8;->this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    invoke-static {v3}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->access$1200(Lcom/lumiyaviewer/lumiya/voice/VivoxController;)Ljava/util/Map;

    move-result-object v3

    invoke-interface {v3}, Ljava/util/Map;->values()Ljava/util/Collection;

    move-result-object v3

    invoke-interface {v3}, Ljava/util/Collection;->iterator()Ljava/util/Iterator;

    move-result-object v3

    :cond_0
    :goto_0
    invoke-interface {v3}, Ljava/util/Iterator;->hasNext()Z

    move-result v4

    if-eqz v4, :cond_1

    invoke-interface {v3}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;

    .line 465
    .local v0, "session":Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;
    invoke-virtual {v0}, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;->getVoiceChannelInfo()Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;

    move-result-object v4

    iget-object v4, v4, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;->voiceChannelURI:Ljava/lang/String;

    iget-object v5, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$8;->val$message:Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceTerminateCall;

    iget-object v5, v5, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceTerminateCall;->channelInfo:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;

    iget-object v5, v5, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;->voiceChannelURI:Ljava/lang/String;

    invoke-static {v4, v5}, Lcom/google/common/base/Objects;->equal(Ljava/lang/Object;Ljava/lang/Object;)Z

    move-result v4

    if-eqz v4, :cond_0

    .line 466
    invoke-interface {v2, v0}, Ljava/util/List;->add(Ljava/lang/Object;)Z

    goto :goto_0

    .line 469
    .end local v0    # "session":Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;
    :cond_1
    invoke-interface {v2}, Ljava/util/List;->iterator()Ljava/util/Iterator;

    move-result-object v3

    :goto_1
    invoke-interface {v3}, Ljava/util/Iterator;->hasNext()Z

    move-result v4

    if-eqz v4, :cond_2

    invoke-interface {v3}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;

    .line 470
    .local v1, "terminateSession":Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;
    sget-object v4, Lcom/vivox/service/vx_termination_status;->termination_status_none:Lcom/vivox/service/vx_termination_status;

    invoke-virtual {v1, v4}, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;->mediaDisconnect(Lcom/vivox/service/vx_termination_status;)V

    .line 471
    invoke-virtual {v1}, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;->dispose()V

    goto :goto_1

    .line 473
    .end local v1    # "terminateSession":Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;
    :cond_2
    iget-object v3, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$8;->this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    const/4 v4, 0x0

    invoke-static {v3, v4}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->access$1400(Lcom/lumiyaviewer/lumiya/voice/VivoxController;Z)V

    .line 474
    return-void
.end method
