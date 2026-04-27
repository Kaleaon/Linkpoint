.class Lcom/lumiyaviewer/lumiya/voice/VivoxController$1;
.super Ljava/lang/Object;
.source "VivoxController.java"

# interfaces
.implements Ljava/lang/Runnable;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/lumiyaviewer/lumiya/voice/VivoxController;->setAudioVoiceMode(Z)V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxController;


# direct methods
.method constructor <init>(Lcom/lumiyaviewer/lumiya/voice/VivoxController;)V
    .locals 0
    .param p1, "this$0"    # Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    .prologue
    .line 237
    iput-object p1, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$1;->this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public run()V
    .locals 4

    .prologue
    .line 240
    iget-object v2, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$1;->this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    invoke-static {v2}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->access$500(Lcom/lumiyaviewer/lumiya/voice/VivoxController;)Landroid/media/AudioManager;

    move-result-object v2

    if-eqz v2, :cond_0

    .line 241
    sget v2, Landroid/os/Build$VERSION;->SDK_INT:I

    const/16 v3, 0xb

    if-lt v2, v3, :cond_0

    .line 243
    :try_start_0
    iget-object v2, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$1;->this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    invoke-static {v2}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->access$500(Lcom/lumiyaviewer/lumiya/voice/VivoxController;)Landroid/media/AudioManager;

    move-result-object v2

    const/4 v3, 0x3

    invoke-virtual {v2, v3}, Landroid/media/AudioManager;->setMode(I)V
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    .line 249
    :cond_0
    :goto_0
    invoke-static {}, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->getServiceInstance()Lcom/lumiyaviewer/lumiya/voice/VoiceService;

    move-result-object v1

    .line 250
    .local v1, "serviceInstance":Lcom/lumiyaviewer/lumiya/voice/VoiceService;
    if-eqz v1, :cond_1

    .line 251
    const/4 v2, 0x1

    invoke-virtual {v1, v2}, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->listenForVolumeChanges(Z)V

    .line 252
    :cond_1
    return-void

    .line 244
    .end local v1    # "serviceInstance":Lcom/lumiyaviewer/lumiya/voice/VoiceService;
    :catch_0
    move-exception v0

    .line 245
    .local v0, "e":Ljava/lang/Exception;
    invoke-static {v0}, Lcom/lumiyaviewer/lumiya/voice/Debug;->Warning(Ljava/lang/Throwable;)V

    goto :goto_0
.end method
