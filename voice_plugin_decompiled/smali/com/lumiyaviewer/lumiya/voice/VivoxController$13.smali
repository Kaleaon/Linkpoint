.class Lcom/lumiyaviewer/lumiya/voice/VivoxController$13;
.super Ljava/lang/Object;
.source "VivoxController.java"

# interfaces
.implements Ljava/lang/Runnable;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/lumiyaviewer/lumiya/voice/VivoxController;->setBluetoothEnable(Z)V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

.field final synthetic val$enable:Z


# direct methods
.method constructor <init>(Lcom/lumiyaviewer/lumiya/voice/VivoxController;Z)V
    .locals 0
    .param p1, "this$0"    # Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    .prologue
    .line 599
    iput-object p1, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$13;->this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    iput-boolean p2, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$13;->val$enable:Z

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public run()V
    .locals 4

    .prologue
    .line 603
    :try_start_0
    iget-object v2, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$13;->this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    invoke-static {v2}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->access$500(Lcom/lumiyaviewer/lumiya/voice/VivoxController;)Landroid/media/AudioManager;

    move-result-object v2

    iget-boolean v3, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$13;->val$enable:Z

    invoke-virtual {v2, v3}, Landroid/media/AudioManager;->setBluetoothScoOn(Z)V
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    .line 607
    :goto_0
    invoke-static {}, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->getServiceInstance()Lcom/lumiyaviewer/lumiya/voice/VoiceService;

    move-result-object v1

    .line 608
    .local v1, "serviceInstance":Lcom/lumiyaviewer/lumiya/voice/VoiceService;
    if-eqz v1, :cond_0

    .line 609
    invoke-virtual {v1}, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->updateAudioProperties()V

    .line 610
    :cond_0
    return-void

    .line 604
    .end local v1    # "serviceInstance":Lcom/lumiyaviewer/lumiya/voice/VoiceService;
    :catch_0
    move-exception v0

    .line 605
    .local v0, "e":Ljava/lang/Exception;
    invoke-static {v0}, Lcom/lumiyaviewer/lumiya/voice/Debug;->Warning(Ljava/lang/Throwable;)V

    goto :goto_0
.end method
