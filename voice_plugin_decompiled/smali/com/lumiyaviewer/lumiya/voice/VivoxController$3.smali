.class Lcom/lumiyaviewer/lumiya/voice/VivoxController$3;
.super Ljava/lang/Object;
.source "VivoxController.java"

# interfaces
.implements Ljava/lang/Runnable;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/lumiyaviewer/lumiya/voice/VivoxController;->Login(Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceLoginInfo;Landroid/os/Messenger;)V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

.field final synthetic val$replyTo:Landroid/os/Messenger;

.field final synthetic val$voiceLoginInfo:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceLoginInfo;


# direct methods
.method constructor <init>(Lcom/lumiyaviewer/lumiya/voice/VivoxController;Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceLoginInfo;Landroid/os/Messenger;)V
    .locals 0
    .param p1, "this$0"    # Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    .prologue
    .line 306
    iput-object p1, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$3;->this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    iput-object p2, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$3;->val$voiceLoginInfo:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceLoginInfo;

    iput-object p3, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$3;->val$replyTo:Landroid/os/Messenger;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public run()V
    .locals 9

    .prologue
    const/4 v8, 0x0

    const/4 v7, 0x0

    .line 312
    :try_start_0
    const-string v1, "Voice: Logging in."

    const/4 v2, 0x0

    new-array v2, v2, [Ljava/lang/Object;

    invoke-static {v1, v2}, Lcom/lumiyaviewer/lumiya/voice/Debug;->Printf(Ljava/lang/String;[Ljava/lang/Object;)V

    .line 314
    iget-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$3;->this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    invoke-static {v1}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->access$600(Lcom/lumiyaviewer/lumiya/voice/VivoxController;)Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceAccountConnection;

    move-result-object v1

    if-eqz v1, :cond_0

    iget-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$3;->val$voiceLoginInfo:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceLoginInfo;

    iget-object v2, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$3;->this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    invoke-static {v2}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->access$600(Lcom/lumiyaviewer/lumiya/voice/VivoxController;)Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceAccountConnection;

    move-result-object v2

    invoke-virtual {v2}, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceAccountConnection;->getVoiceLoginInfo()Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceLoginInfo;

    move-result-object v2

    invoke-static {v1, v2}, Lcom/google/common/base/Objects;->equal(Ljava/lang/Object;Ljava/lang/Object;)Z

    move-result v1

    if-nez v1, :cond_0

    .line 315
    iget-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$3;->this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    invoke-static {v1}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->access$700(Lcom/lumiyaviewer/lumiya/voice/VivoxController;)V

    .line 316
    iget-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$3;->this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    invoke-static {v1}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->access$600(Lcom/lumiyaviewer/lumiya/voice/VivoxController;)Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceAccountConnection;

    move-result-object v1

    invoke-virtual {v1}, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceAccountConnection;->dispose()V

    .line 317
    iget-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$3;->this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    const/4 v2, 0x0

    invoke-static {v1, v2}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->access$602(Lcom/lumiyaviewer/lumiya/voice/VivoxController;Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceAccountConnection;)Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceAccountConnection;

    .line 320
    :cond_0
    iget-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$3;->this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    invoke-static {v1}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->access$800(Lcom/lumiyaviewer/lumiya/voice/VivoxController;)Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceConnector;

    move-result-object v1

    if-eqz v1, :cond_2

    iget-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$3;->val$voiceLoginInfo:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceLoginInfo;

    iget-object v1, v1, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceLoginInfo;->voiceAccountServerName:Ljava/lang/String;

    iget-object v2, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$3;->this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    invoke-static {v2}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->access$800(Lcom/lumiyaviewer/lumiya/voice/VivoxController;)Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceConnector;

    move-result-object v2

    invoke-virtual {v2}, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceConnector;->getVoiceAccountServerName()Ljava/lang/String;

    move-result-object v2

    invoke-static {v1, v2}, Lcom/google/common/base/Objects;->equal(Ljava/lang/Object;Ljava/lang/Object;)Z

    move-result v1

    if-nez v1, :cond_2

    .line 322
    iget-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$3;->this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    invoke-static {v1}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->access$700(Lcom/lumiyaviewer/lumiya/voice/VivoxController;)V

    .line 323
    iget-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$3;->this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    invoke-static {v1}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->access$600(Lcom/lumiyaviewer/lumiya/voice/VivoxController;)Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceAccountConnection;

    move-result-object v1

    if-eqz v1, :cond_1

    .line 324
    iget-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$3;->this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    invoke-static {v1}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->access$600(Lcom/lumiyaviewer/lumiya/voice/VivoxController;)Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceAccountConnection;

    move-result-object v1

    invoke-virtual {v1}, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceAccountConnection;->dispose()V

    .line 325
    iget-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$3;->this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    const/4 v2, 0x0

    invoke-static {v1, v2}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->access$602(Lcom/lumiyaviewer/lumiya/voice/VivoxController;Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceAccountConnection;)Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceAccountConnection;

    .line 328
    :cond_1
    iget-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$3;->this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    invoke-static {v1}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->access$800(Lcom/lumiyaviewer/lumiya/voice/VivoxController;)Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceConnector;

    move-result-object v1

    invoke-virtual {v1}, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceConnector;->dispose()V

    .line 329
    iget-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$3;->this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    const/4 v2, 0x0

    invoke-static {v1, v2}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->access$802(Lcom/lumiyaviewer/lumiya/voice/VivoxController;Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceConnector;)Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceConnector;

    .line 333
    :cond_2
    iget-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$3;->this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    invoke-static {v1}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->access$800(Lcom/lumiyaviewer/lumiya/voice/VivoxController;)Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceConnector;

    move-result-object v1

    if-nez v1, :cond_3

    .line 334
    const-string v1, "Voice: Creating voice connector."

    const/4 v2, 0x0

    new-array v2, v2, [Ljava/lang/Object;

    invoke-static {v1, v2}, Lcom/lumiyaviewer/lumiya/voice/Debug;->Printf(Ljava/lang/String;[Ljava/lang/Object;)V

    .line 335
    iget-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$3;->this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    new-instance v2, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceConnector;

    iget-object v3, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$3;->this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    invoke-static {v3}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->access$900(Lcom/lumiyaviewer/lumiya/voice/VivoxController;)Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;

    move-result-object v3

    iget-object v4, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$3;->val$voiceLoginInfo:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceLoginInfo;

    iget-object v4, v4, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceLoginInfo;->voiceAccountServerName:Ljava/lang/String;

    invoke-direct {v2, v3, v4}, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceConnector;-><init>(Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;Ljava/lang/String;)V

    invoke-static {v1, v2}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->access$802(Lcom/lumiyaviewer/lumiya/voice/VivoxController;Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceConnector;)Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceConnector;

    .line 336
    const-string v1, "Voice: Voice connector created."

    const/4 v2, 0x0

    new-array v2, v2, [Ljava/lang/Object;

    invoke-static {v1, v2}, Lcom/lumiyaviewer/lumiya/voice/Debug;->Printf(Ljava/lang/String;[Ljava/lang/Object;)V

    .line 339
    :cond_3
    iget-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$3;->this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    invoke-static {v1}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->access$800(Lcom/lumiyaviewer/lumiya/voice/VivoxController;)Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceConnector;

    move-result-object v1

    const/4 v2, 0x1

    invoke-virtual {v1, v2}, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceConnector;->setMuteLocalMic(Z)V

    .line 340
    iget-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$3;->this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    const/4 v2, 0x0

    invoke-static {v1, v2}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->access$1002(Lcom/lumiyaviewer/lumiya/voice/VivoxController;Z)Z

    .line 342
    iget-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$3;->this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    invoke-static {v1}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->access$600(Lcom/lumiyaviewer/lumiya/voice/VivoxController;)Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceAccountConnection;

    move-result-object v1

    if-nez v1, :cond_4

    .line 343
    const-string v1, "Voice: Creating voice account connection."

    const/4 v2, 0x0

    new-array v2, v2, [Ljava/lang/Object;

    invoke-static {v1, v2}, Lcom/lumiyaviewer/lumiya/voice/Debug;->Printf(Ljava/lang/String;[Ljava/lang/Object;)V

    .line 344
    iget-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$3;->this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    iget-object v2, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$3;->this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    invoke-static {v2}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->access$800(Lcom/lumiyaviewer/lumiya/voice/VivoxController;)Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceConnector;

    move-result-object v2

    iget-object v3, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$3;->val$voiceLoginInfo:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceLoginInfo;

    invoke-virtual {v2, v3}, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceConnector;->createAccountConnection(Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceLoginInfo;)Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceAccountConnection;

    move-result-object v2

    invoke-static {v1, v2}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->access$602(Lcom/lumiyaviewer/lumiya/voice/VivoxController;Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceAccountConnection;)Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceAccountConnection;

    .line 345
    const-string v1, "Voice: Voice account connection created."

    const/4 v2, 0x0

    new-array v2, v2, [Ljava/lang/Object;

    invoke-static {v1, v2}, Lcom/lumiyaviewer/lumiya/voice/Debug;->Printf(Ljava/lang/String;[Ljava/lang/Object;)V

    .line 348
    :cond_4
    iget-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$3;->this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    iget-object v2, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$3;->val$replyTo:Landroid/os/Messenger;

    invoke-static {v1, v2}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->access$1102(Lcom/lumiyaviewer/lumiya/voice/VivoxController;Landroid/os/Messenger;)Landroid/os/Messenger;

    .line 349
    iget-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$3;->val$replyTo:Landroid/os/Messenger;

    sget-object v2, Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;->VoiceLoginStatus:Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;

    new-instance v3, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceLoginStatus;

    iget-object v4, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$3;->val$voiceLoginInfo:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceLoginInfo;

    const/4 v5, 0x1

    const/4 v6, 0x0

    invoke-direct {v3, v4, v5, v6}, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceLoginStatus;-><init>(Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceLoginInfo;ZLjava/lang/String;)V

    const/4 v4, 0x0

    invoke-static {v1, v2, v3, v4}, Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessenger;->sendMessage(Landroid/os/Messenger;Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessage;Landroid/os/Messenger;)Z
    :try_end_0
    .catch Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceException; {:try_start_0 .. :try_end_0} :catch_0

    .line 357
    :goto_0
    return-void

    .line 351
    :catch_0
    move-exception v0

    .line 352
    .local v0, "e":Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceException;
    invoke-static {v0}, Lcom/lumiyaviewer/lumiya/voice/Debug;->Warning(Ljava/lang/Throwable;)V

    .line 353
    iget-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$3;->this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    invoke-static {v1, v8}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->access$1102(Lcom/lumiyaviewer/lumiya/voice/VivoxController;Landroid/os/Messenger;)Landroid/os/Messenger;

    .line 354
    iget-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$3;->val$replyTo:Landroid/os/Messenger;

    sget-object v2, Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;->VoiceLoginStatus:Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;

    new-instance v3, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceLoginStatus;

    iget-object v4, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$3;->val$voiceLoginInfo:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceLoginInfo;

    invoke-virtual {v0}, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceException;->getMessage()Ljava/lang/String;

    move-result-object v5

    invoke-direct {v3, v4, v7, v5}, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceLoginStatus;-><init>(Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceLoginInfo;ZLjava/lang/String;)V

    invoke-static {v1, v2, v3, v8}, Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessenger;->sendMessage(Landroid/os/Messenger;Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessage;Landroid/os/Messenger;)Z

    goto :goto_0
.end method
