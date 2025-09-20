.class Lcom/lumiyaviewer/lumiya/voice/VivoxController$9;
.super Ljava/lang/Object;
.source "VivoxController.java"

# interfaces
.implements Ljava/lang/Runnable;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/lumiyaviewer/lumiya/voice/VivoxController;->EnableVoiceMic(Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceEnableMic;)V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

.field final synthetic val$message:Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceEnableMic;


# direct methods
.method constructor <init>(Lcom/lumiyaviewer/lumiya/voice/VivoxController;Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceEnableMic;)V
    .locals 0
    .param p1, "this$0"    # Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    .prologue
    .line 502
    iput-object p1, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$9;->this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    iput-object p2, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$9;->val$message:Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceEnableMic;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public run()V
    .locals 5

    .prologue
    const/4 v4, 0x1

    const/4 v3, 0x0

    .line 505
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$9;->this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    invoke-static {v0}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->access$800(Lcom/lumiyaviewer/lumiya/voice/VivoxController;)Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceConnector;

    move-result-object v0

    if-eqz v0, :cond_0

    .line 506
    const-string v0, "Voice: local mic enabling: %b"

    new-array v1, v4, [Ljava/lang/Object;

    iget-object v2, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$9;->val$message:Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceEnableMic;

    iget-boolean v2, v2, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceEnableMic;->enableMic:Z

    invoke-static {v2}, Ljava/lang/Boolean;->valueOf(Z)Ljava/lang/Boolean;

    move-result-object v2

    aput-object v2, v1, v3

    invoke-static {v0, v1}, Lcom/lumiyaviewer/lumiya/voice/Debug;->Printf(Ljava/lang/String;[Ljava/lang/Object;)V

    .line 507
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$9;->val$message:Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceEnableMic;

    iget-boolean v0, v0, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceEnableMic;->enableMic:Z

    if-eqz v0, :cond_1

    .line 508
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$9;->this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    invoke-static {v0}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->access$1500(Lcom/lumiyaviewer/lumiya/voice/VivoxController;)Z

    move-result v0

    if-eqz v0, :cond_0

    .line 509
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$9;->this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    invoke-static {v0, v4}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->access$1400(Lcom/lumiyaviewer/lumiya/voice/VivoxController;Z)V

    .line 514
    :cond_0
    :goto_0
    return-void

    .line 511
    :cond_1
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$9;->this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    invoke-static {v0, v3}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->access$1400(Lcom/lumiyaviewer/lumiya/voice/VivoxController;Z)V

    goto :goto_0
.end method
