.class public Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceAudioProperties;
.super Ljava/lang/Object;
.source "VoiceAudioProperties.java"

# interfaces
.implements Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessage;


# instance fields
.field public final bluetoothState:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceBluetoothState;
    .annotation runtime Ljavax/annotation/Nonnull;
    .end annotation
.end field

.field public final speakerVolume:F

.field public final speakerphoneOn:Z


# direct methods
.method public constructor <init>(FZLcom/lumiyaviewer/lumiya/voice/common/model/VoiceBluetoothState;)V
    .locals 0
    .param p1, "speakerVolume"    # F
    .param p2, "speakerphoneOn"    # Z
    .param p3, "bluetoothState"    # Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceBluetoothState;
        .annotation runtime Ljavax/annotation/Nonnull;
        .end annotation
    .end param

    .prologue
    .line 15
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 16
    iput p1, p0, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceAudioProperties;->speakerVolume:F

    .line 17
    iput-boolean p2, p0, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceAudioProperties;->speakerphoneOn:Z

    .line 18
    iput-object p3, p0, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceAudioProperties;->bluetoothState:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceBluetoothState;

    .line 19
    return-void
.end method

.method public constructor <init>(Landroid/os/Bundle;)V
    .locals 3
    .param p1, "bundle"    # Landroid/os/Bundle;

    .prologue
    .line 21
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 22
    const-string v2, "speakerVolume"

    invoke-virtual {p1, v2}, Landroid/os/Bundle;->getFloat(Ljava/lang/String;)F

    move-result v2

    iput v2, p0, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceAudioProperties;->speakerVolume:F

    .line 23
    const-string v2, "speakerphoneOn"

    invoke-virtual {p1, v2}, Landroid/os/Bundle;->getBoolean(Ljava/lang/String;)Z

    move-result v2

    iput-boolean v2, p0, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceAudioProperties;->speakerphoneOn:Z

    .line 26
    :try_start_0
    const-string v2, "bluetoothState"

    invoke-virtual {p1, v2}, Landroid/os/Bundle;->getString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v2

    invoke-static {v2}, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceBluetoothState;->valueOf(Ljava/lang/String;)Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceBluetoothState;
    :try_end_0
    .catch Ljava/lang/IllegalArgumentException; {:try_start_0 .. :try_end_0} :catch_0

    move-result-object v0

    .line 30
    .local v0, "bluetoothState":Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceBluetoothState;
    :goto_0
    iput-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceAudioProperties;->bluetoothState:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceBluetoothState;

    .line 31
    return-void

    .line 27
    .end local v0    # "bluetoothState":Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceBluetoothState;
    :catch_0
    move-exception v1

    .line 28
    .local v1, "e":Ljava/lang/IllegalArgumentException;
    sget-object v0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceBluetoothState;->Error:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceBluetoothState;

    .restart local v0    # "bluetoothState":Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceBluetoothState;
    goto :goto_0
.end method


# virtual methods
.method public toBundle()Landroid/os/Bundle;
    .locals 3

    .prologue
    .line 36
    new-instance v0, Landroid/os/Bundle;

    invoke-direct {v0}, Landroid/os/Bundle;-><init>()V

    .line 37
    .local v0, "bundle":Landroid/os/Bundle;
    const-string v1, "speakerVolume"

    iget v2, p0, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceAudioProperties;->speakerVolume:F

    invoke-virtual {v0, v1, v2}, Landroid/os/Bundle;->putFloat(Ljava/lang/String;F)V

    .line 38
    const-string v1, "speakerphoneOn"

    iget-boolean v2, p0, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceAudioProperties;->speakerphoneOn:Z

    invoke-virtual {v0, v1, v2}, Landroid/os/Bundle;->putBoolean(Ljava/lang/String;Z)V

    .line 39
    const-string v1, "bluetoothState"

    iget-object v2, p0, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceAudioProperties;->bluetoothState:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceBluetoothState;

    invoke-virtual {v2}, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceBluetoothState;->name()Ljava/lang/String;

    move-result-object v2

    invoke-virtual {v0, v1, v2}, Landroid/os/Bundle;->putString(Ljava/lang/String;Ljava/lang/String;)V

    .line 40
    return-object v0
.end method
