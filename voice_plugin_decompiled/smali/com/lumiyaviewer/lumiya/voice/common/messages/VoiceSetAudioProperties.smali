.class public Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceSetAudioProperties;
.super Ljava/lang/Object;
.source "VoiceSetAudioProperties.java"

# interfaces
.implements Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessage;


# instance fields
.field public final audioDevice:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceAudioDevice;
    .annotation runtime Ljavax/annotation/Nullable;
    .end annotation
.end field

.field public final speakerVolume:F

.field public final speakerVolumeValid:Z


# direct methods
.method public constructor <init>(FZLcom/lumiyaviewer/lumiya/voice/common/model/VoiceAudioDevice;)V
    .locals 0
    .param p1, "speakerVolume"    # F
    .param p2, "speakerVolumeValid"    # Z
    .param p3, "audioDevice"    # Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceAudioDevice;
        .annotation runtime Ljavax/annotation/Nullable;
        .end annotation
    .end param

    .prologue
    .line 16
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 17
    iput p1, p0, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceSetAudioProperties;->speakerVolume:F

    .line 18
    iput-boolean p2, p0, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceSetAudioProperties;->speakerVolumeValid:Z

    .line 19
    iput-object p3, p0, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceSetAudioProperties;->audioDevice:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceAudioDevice;

    .line 20
    return-void
.end method

.method public constructor <init>(Landroid/os/Bundle;)V
    .locals 3
    .param p1, "bundle"    # Landroid/os/Bundle;

    .prologue
    .line 22
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 23
    const-string v2, "speakerVolume"

    invoke-virtual {p1, v2}, Landroid/os/Bundle;->containsKey(Ljava/lang/String;)Z

    move-result v2

    iput-boolean v2, p0, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceSetAudioProperties;->speakerVolumeValid:Z

    .line 24
    iget-boolean v2, p0, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceSetAudioProperties;->speakerVolumeValid:Z

    if-eqz v2, :cond_1

    const-string v2, "speakerVolume"

    invoke-virtual {p1, v2}, Landroid/os/Bundle;->getFloat(Ljava/lang/String;)F

    move-result v2

    :goto_0
    iput v2, p0, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceSetAudioProperties;->speakerVolume:F

    .line 26
    const/4 v0, 0x0

    .line 27
    .local v0, "audioDevice":Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceAudioDevice;
    const-string v2, "audioDevice"

    invoke-virtual {p1, v2}, Landroid/os/Bundle;->containsKey(Ljava/lang/String;)Z

    move-result v2

    if-eqz v2, :cond_0

    .line 29
    :try_start_0
    const-string v2, "audioDevice"

    invoke-virtual {p1, v2}, Landroid/os/Bundle;->getString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v2

    invoke-static {v2}, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceAudioDevice;->valueOf(Ljava/lang/String;)Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceAudioDevice;
    :try_end_0
    .catch Ljava/lang/IllegalArgumentException; {:try_start_0 .. :try_end_0} :catch_0

    move-result-object v0

    .line 35
    :cond_0
    :goto_1
    iput-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceSetAudioProperties;->audioDevice:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceAudioDevice;

    .line 36
    return-void

    .line 24
    .end local v0    # "audioDevice":Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceAudioDevice;
    :cond_1
    const/high16 v2, 0x7fc00000    # Float.NaN

    goto :goto_0

    .line 30
    .restart local v0    # "audioDevice":Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceAudioDevice;
    :catch_0
    move-exception v1

    .line 31
    .local v1, "e":Ljava/lang/IllegalArgumentException;
    const/4 v0, 0x0

    goto :goto_1
.end method


# virtual methods
.method public toBundle()Landroid/os/Bundle;
    .locals 3

    .prologue
    .line 40
    new-instance v0, Landroid/os/Bundle;

    invoke-direct {v0}, Landroid/os/Bundle;-><init>()V

    .line 41
    .local v0, "bundle":Landroid/os/Bundle;
    iget-boolean v1, p0, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceSetAudioProperties;->speakerVolumeValid:Z

    if-eqz v1, :cond_0

    .line 42
    const-string v1, "speakerVolume"

    iget v2, p0, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceSetAudioProperties;->speakerVolume:F

    invoke-virtual {v0, v1, v2}, Landroid/os/Bundle;->putFloat(Ljava/lang/String;F)V

    .line 43
    :cond_0
    iget-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceSetAudioProperties;->audioDevice:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceAudioDevice;

    if-eqz v1, :cond_1

    .line 44
    const-string v1, "audioDevice"

    iget-object v2, p0, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceSetAudioProperties;->audioDevice:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceAudioDevice;

    invoke-virtual {v2}, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceAudioDevice;->name()Ljava/lang/String;

    move-result-object v2

    invoke-virtual {v0, v1, v2}, Landroid/os/Bundle;->putString(Ljava/lang/String;Ljava/lang/String;)V

    .line 45
    :cond_1
    return-object v0
.end method
