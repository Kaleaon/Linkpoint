.class Lcom/lumiyaviewer/lumiya/voice/VivoxController$6;
.super Ljava/lang/Object;
.source "VivoxController.java"

# interfaces
.implements Ljava/lang/Runnable;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/lumiyaviewer/lumiya/voice/VivoxController;->RejectCall(Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceRejectCall;)V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

.field final synthetic val$message:Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceRejectCall;


# direct methods
.method constructor <init>(Lcom/lumiyaviewer/lumiya/voice/VivoxController;Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceRejectCall;)V
    .locals 0
    .param p1, "this$0"    # Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    .prologue
    .line 415
    iput-object p1, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$6;->this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    iput-object p2, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$6;->val$message:Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceRejectCall;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public run()V
    .locals 5

    .prologue
    const/4 v4, 0x0

    .line 418
    const-string v1, "Voice: requested to reject session with handle %s"

    const/4 v2, 0x1

    new-array v2, v2, [Ljava/lang/Object;

    iget-object v3, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$6;->val$message:Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceRejectCall;

    iget-object v3, v3, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceRejectCall;->sessionHandle:Ljava/lang/String;

    aput-object v3, v2, v4

    invoke-static {v1, v2}, Lcom/lumiyaviewer/lumiya/voice/Debug;->Printf(Ljava/lang/String;[Ljava/lang/Object;)V

    .line 419
    iget-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$6;->this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    invoke-static {v1}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->access$1200(Lcom/lumiyaviewer/lumiya/voice/VivoxController;)Ljava/util/Map;

    move-result-object v1

    iget-object v2, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$6;->val$message:Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceRejectCall;

    iget-object v2, v2, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceRejectCall;->sessionHandle:Ljava/lang/String;

    invoke-interface {v1, v2}, Ljava/util/Map;->get(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;

    .line 420
    .local v0, "session":Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;
    if-eqz v0, :cond_0

    .line 421
    const-string v1, "Voice: terminating session"

    new-array v2, v4, [Ljava/lang/Object;

    invoke-static {v1, v2}, Lcom/lumiyaviewer/lumiya/voice/Debug;->Printf(Ljava/lang/String;[Ljava/lang/Object;)V

    .line 422
    sget-object v1, Lcom/vivox/service/vx_termination_status;->termination_status_busy:Lcom/vivox/service/vx_termination_status;

    invoke-virtual {v0, v1}, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;->mediaDisconnect(Lcom/vivox/service/vx_termination_status;)V

    .line 425
    :goto_0
    return-void

    .line 424
    :cond_0
    const-string v1, "Voice: no session to terminate"

    new-array v2, v4, [Ljava/lang/Object;

    invoke-static {v1, v2}, Lcom/lumiyaviewer/lumiya/voice/Debug;->Printf(Ljava/lang/String;[Ljava/lang/Object;)V

    goto :goto_0
.end method
