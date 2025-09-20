.class Lcom/lumiyaviewer/lumiya/voice/VivoxController$11;
.super Ljava/lang/Object;
.source "VivoxController.java"

# interfaces
.implements Ljava/lang/Runnable;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/lumiyaviewer/lumiya/voice/VivoxController;->SetAudioProperties(Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceSetAudioProperties;)V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

.field final synthetic val$message:Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceSetAudioProperties;


# direct methods
.method constructor <init>(Lcom/lumiyaviewer/lumiya/voice/VivoxController;Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceSetAudioProperties;)V
    .locals 0
    .param p1, "this$0"    # Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    .prologue
    .line 545
    iput-object p1, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$11;->this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    iput-object p2, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$11;->val$message:Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceSetAudioProperties;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public run()V
    .locals 5

    .prologue
    const/4 v4, 0x1

    .line 548
    iget-object v2, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$11;->this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    invoke-static {v2}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->access$500(Lcom/lumiyaviewer/lumiya/voice/VivoxController;)Landroid/media/AudioManager;

    move-result-object v2

    if-eqz v2, :cond_0

    iget-object v2, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$11;->val$message:Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceSetAudioProperties;

    iget-object v2, v2, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceSetAudioProperties;->audioDevice:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceAudioDevice;

    if-eqz v2, :cond_0

    iget-object v2, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$11;->this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    invoke-static {v2}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->access$1500(Lcom/lumiyaviewer/lumiya/voice/VivoxController;)Z

    move-result v2

    if-eqz v2, :cond_0

    .line 549
    sget-object v2, Lcom/lumiyaviewer/lumiya/voice/VivoxController$15;->$SwitchMap$com$lumiyaviewer$lumiya$voice$common$model$VoiceAudioDevice:[I

    iget-object v3, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$11;->val$message:Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceSetAudioProperties;

    iget-object v3, v3, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceSetAudioProperties;->audioDevice:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceAudioDevice;

    invoke-virtual {v3}, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceAudioDevice;->ordinal()I

    move-result v3

    aget v2, v2, v3

    packed-switch v2, :pswitch_data_0

    .line 575
    :cond_0
    :goto_0
    invoke-static {}, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->getServiceInstance()Lcom/lumiyaviewer/lumiya/voice/VoiceService;

    move-result-object v1

    .line 576
    .local v1, "serviceInstance":Lcom/lumiyaviewer/lumiya/voice/VoiceService;
    if-eqz v1, :cond_2

    .line 577
    iget-object v2, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$11;->val$message:Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceSetAudioProperties;

    iget-boolean v2, v2, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceSetAudioProperties;->speakerVolumeValid:Z

    if-eqz v2, :cond_1

    .line 578
    iget-object v2, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$11;->val$message:Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceSetAudioProperties;

    iget v2, v2, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceSetAudioProperties;->speakerVolume:F

    invoke-virtual {v1, v2}, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->setVolume(F)V

    .line 579
    :cond_1
    invoke-virtual {v1}, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->updateAudioProperties()V

    .line 582
    :cond_2
    return-void

    .line 552
    .end local v1    # "serviceInstance":Lcom/lumiyaviewer/lumiya/voice/VoiceService;
    :pswitch_0
    :try_start_0
    iget-object v2, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$11;->this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    invoke-static {v2}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->access$500(Lcom/lumiyaviewer/lumiya/voice/VivoxController;)Landroid/media/AudioManager;

    move-result-object v2

    const/4 v3, 0x0

    invoke-virtual {v2, v3}, Landroid/media/AudioManager;->setSpeakerphoneOn(Z)V

    .line 553
    iget-object v2, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$11;->this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    invoke-static {v2}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->access$500(Lcom/lumiyaviewer/lumiya/voice/VivoxController;)Landroid/media/AudioManager;

    move-result-object v2

    const/4 v3, 0x0

    invoke-virtual {v2, v3}, Landroid/media/AudioManager;->setBluetoothScoOn(Z)V
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    goto :goto_0

    .line 554
    :catch_0
    move-exception v0

    .line 555
    .local v0, "e":Ljava/lang/Exception;
    invoke-static {v0}, Lcom/lumiyaviewer/lumiya/voice/Debug;->Warning(Ljava/lang/Throwable;)V

    goto :goto_0

    .line 560
    .end local v0    # "e":Ljava/lang/Exception;
    :pswitch_1
    :try_start_1
    iget-object v2, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$11;->this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    invoke-static {v2}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->access$500(Lcom/lumiyaviewer/lumiya/voice/VivoxController;)Landroid/media/AudioManager;

    move-result-object v2

    const/4 v3, 0x1

    invoke-virtual {v2, v3}, Landroid/media/AudioManager;->setSpeakerphoneOn(Z)V

    .line 561
    iget-object v2, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$11;->this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    invoke-static {v2}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->access$500(Lcom/lumiyaviewer/lumiya/voice/VivoxController;)Landroid/media/AudioManager;

    move-result-object v2

    const/4 v3, 0x0

    invoke-virtual {v2, v3}, Landroid/media/AudioManager;->setBluetoothScoOn(Z)V
    :try_end_1
    .catch Ljava/lang/Exception; {:try_start_1 .. :try_end_1} :catch_1

    goto :goto_0

    .line 562
    :catch_1
    move-exception v0

    .line 563
    .restart local v0    # "e":Ljava/lang/Exception;
    invoke-static {v0}, Lcom/lumiyaviewer/lumiya/voice/Debug;->Warning(Ljava/lang/Throwable;)V

    goto :goto_0

    .line 567
    .end local v0    # "e":Ljava/lang/Exception;
    :pswitch_2
    iget-object v2, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$11;->this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    invoke-static {v2}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->access$1600(Lcom/lumiyaviewer/lumiya/voice/VivoxController;)I

    move-result v2

    if-ne v2, v4, :cond_3

    .line 568
    iget-object v2, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$11;->this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    invoke-static {v2, v4}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->access$1700(Lcom/lumiyaviewer/lumiya/voice/VivoxController;Z)V

    goto :goto_0

    .line 570
    :cond_3
    iget-object v2, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$11;->this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    invoke-static {v2}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->access$1800(Lcom/lumiyaviewer/lumiya/voice/VivoxController;)V

    goto :goto_0

    .line 549
    nop

    :pswitch_data_0
    .packed-switch 0x1
        :pswitch_0
        :pswitch_1
        :pswitch_2
    .end packed-switch
.end method
