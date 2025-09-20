.class Lcom/lumiyaviewer/lumiya/voice/VivoxController$5;
.super Ljava/lang/Object;
.source "VivoxController.java"

# interfaces
.implements Ljava/lang/Runnable;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/lumiyaviewer/lumiya/voice/VivoxController;->Set3DPosition(Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceSet3DPosition;)V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

.field final synthetic val$message:Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceSet3DPosition;


# direct methods
.method constructor <init>(Lcom/lumiyaviewer/lumiya/voice/VivoxController;Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceSet3DPosition;)V
    .locals 0
    .param p1, "this$0"    # Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    .prologue
    .line 399
    iput-object p1, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$5;->this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    iput-object p2, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$5;->val$message:Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceSet3DPosition;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public run()V
    .locals 5

    .prologue
    .line 403
    iget-object v2, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$5;->this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    invoke-static {v2}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->access$1200(Lcom/lumiyaviewer/lumiya/voice/VivoxController;)Ljava/util/Map;

    move-result-object v2

    invoke-interface {v2}, Ljava/util/Map;->values()Ljava/util/Collection;

    move-result-object v2

    invoke-interface {v2}, Ljava/util/Collection;->iterator()Ljava/util/Iterator;

    move-result-object v2

    :cond_0
    :goto_0
    invoke-interface {v2}, Ljava/util/Iterator;->hasNext()Z

    move-result v3

    if-eqz v3, :cond_1

    invoke-interface {v2}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;

    .line 404
    .local v0, "session":Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;
    invoke-virtual {v0}, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;->getVoiceChannelInfo()Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;

    move-result-object v1

    .line 405
    .local v1, "voiceChannelInfo":Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;
    iget-boolean v3, v1, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;->isSpatial:Z

    if-eqz v3, :cond_0

    iget-object v3, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$5;->val$message:Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceSet3DPosition;

    iget-object v3, v3, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceSet3DPosition;->voiceChannelInfo:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;

    invoke-static {v1, v3}, Lcom/google/common/base/Objects;->equal(Ljava/lang/Object;Ljava/lang/Object;)Z

    move-result v3

    if-eqz v3, :cond_0

    .line 406
    iget-object v3, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$5;->val$message:Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceSet3DPosition;

    iget-object v3, v3, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceSet3DPosition;->speakerPosition:Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DPosition;

    iget-object v4, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$5;->val$message:Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceSet3DPosition;

    iget-object v4, v4, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceSet3DPosition;->listenerPosition:Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DPosition;

    invoke-virtual {v0, v3, v4}, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;->set3DPosition(Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DPosition;Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DPosition;)V

    goto :goto_0

    .line 410
    .end local v0    # "session":Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;
    .end local v1    # "voiceChannelInfo":Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;
    :cond_1
    return-void
.end method
