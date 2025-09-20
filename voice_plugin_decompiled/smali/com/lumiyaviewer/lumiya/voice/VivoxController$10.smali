.class Lcom/lumiyaviewer/lumiya/voice/VivoxController$10;
.super Ljava/lang/Object;
.source "VivoxController.java"

# interfaces
.implements Ljava/lang/Runnable;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/lumiyaviewer/lumiya/voice/VivoxController;->Logout(Landroid/os/Messenger;)V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

.field final synthetic val$replyTo:Landroid/os/Messenger;


# direct methods
.method constructor <init>(Lcom/lumiyaviewer/lumiya/voice/VivoxController;Landroid/os/Messenger;)V
    .locals 0
    .param p1, "this$0"    # Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    .prologue
    .line 519
    iput-object p1, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$10;->this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    iput-object p2, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$10;->val$replyTo:Landroid/os/Messenger;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public run()V
    .locals 5

    .prologue
    const/4 v4, 0x0

    const/4 v3, 0x0

    .line 523
    const-string v0, "Voice: logging out."

    new-array v1, v4, [Ljava/lang/Object;

    invoke-static {v0, v1}, Lcom/lumiyaviewer/lumiya/voice/Debug;->Printf(Ljava/lang/String;[Ljava/lang/Object;)V

    .line 524
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$10;->this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    invoke-static {v0, v4}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->access$1400(Lcom/lumiyaviewer/lumiya/voice/VivoxController;Z)V

    .line 526
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$10;->this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    invoke-static {v0}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->access$600(Lcom/lumiyaviewer/lumiya/voice/VivoxController;)Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceAccountConnection;

    move-result-object v0

    if-eqz v0, :cond_0

    .line 527
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$10;->this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    invoke-static {v0}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->access$700(Lcom/lumiyaviewer/lumiya/voice/VivoxController;)V

    .line 528
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$10;->this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    invoke-static {v0}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->access$600(Lcom/lumiyaviewer/lumiya/voice/VivoxController;)Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceAccountConnection;

    move-result-object v0

    invoke-virtual {v0}, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceAccountConnection;->dispose()V

    .line 529
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$10;->this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    invoke-static {v0, v3}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->access$602(Lcom/lumiyaviewer/lumiya/voice/VivoxController;Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceAccountConnection;)Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceAccountConnection;

    .line 531
    :cond_0
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$10;->this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    invoke-static {v0}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->access$800(Lcom/lumiyaviewer/lumiya/voice/VivoxController;)Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceConnector;

    move-result-object v0

    if-eqz v0, :cond_1

    .line 532
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$10;->this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    invoke-static {v0}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->access$800(Lcom/lumiyaviewer/lumiya/voice/VivoxController;)Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceConnector;

    move-result-object v0

    invoke-virtual {v0}, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceConnector;->dispose()V

    .line 533
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$10;->this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    invoke-static {v0, v3}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->access$802(Lcom/lumiyaviewer/lumiya/voice/VivoxController;Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceConnector;)Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceConnector;

    .line 536
    :cond_1
    const-string v0, "Voice: logged out."

    new-array v1, v4, [Ljava/lang/Object;

    invoke-static {v0, v1}, Lcom/lumiyaviewer/lumiya/voice/Debug;->Printf(Ljava/lang/String;[Ljava/lang/Object;)V

    .line 538
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$10;->val$replyTo:Landroid/os/Messenger;

    sget-object v1, Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;->VoiceLoginStatus:Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;

    new-instance v2, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceLoginStatus;

    invoke-direct {v2, v3, v4, v3}, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceLoginStatus;-><init>(Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceLoginInfo;ZLjava/lang/String;)V

    invoke-static {v0, v1, v2, v3}, Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessenger;->sendMessage(Landroid/os/Messenger;Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessage;Landroid/os/Messenger;)Z

    .line 539
    return-void
.end method
