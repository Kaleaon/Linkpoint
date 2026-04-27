.class synthetic Lcom/lumiyaviewer/lumiya/voice/VivoxController$15;
.super Ljava/lang/Object;
.source "VivoxController.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/lumiyaviewer/lumiya/voice/VivoxController;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x1008
    name = null
.end annotation


# static fields
.field static final synthetic $SwitchMap$com$lumiyaviewer$lumiya$voice$common$model$VoiceAudioDevice:[I


# direct methods
.method static constructor <clinit>()V
    .locals 3

    .prologue
    .line 549
    invoke-static {}, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceAudioDevice;->values()[Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceAudioDevice;

    move-result-object v0

    array-length v0, v0

    new-array v0, v0, [I

    sput-object v0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$15;->$SwitchMap$com$lumiyaviewer$lumiya$voice$common$model$VoiceAudioDevice:[I

    :try_start_0
    sget-object v0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$15;->$SwitchMap$com$lumiyaviewer$lumiya$voice$common$model$VoiceAudioDevice:[I

    sget-object v1, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceAudioDevice;->Default:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceAudioDevice;

    invoke-virtual {v1}, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceAudioDevice;->ordinal()I

    move-result v1

    const/4 v2, 0x1

    aput v2, v0, v1
    :try_end_0
    .catch Ljava/lang/NoSuchFieldError; {:try_start_0 .. :try_end_0} :catch_2

    :goto_0
    :try_start_1
    sget-object v0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$15;->$SwitchMap$com$lumiyaviewer$lumiya$voice$common$model$VoiceAudioDevice:[I

    sget-object v1, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceAudioDevice;->Loudspeaker:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceAudioDevice;

    invoke-virtual {v1}, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceAudioDevice;->ordinal()I

    move-result v1

    const/4 v2, 0x2

    aput v2, v0, v1
    :try_end_1
    .catch Ljava/lang/NoSuchFieldError; {:try_start_1 .. :try_end_1} :catch_1

    :goto_1
    :try_start_2
    sget-object v0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$15;->$SwitchMap$com$lumiyaviewer$lumiya$voice$common$model$VoiceAudioDevice:[I

    sget-object v1, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceAudioDevice;->Bluetooth:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceAudioDevice;

    invoke-virtual {v1}, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceAudioDevice;->ordinal()I

    move-result v1

    const/4 v2, 0x3

    aput v2, v0, v1
    :try_end_2
    .catch Ljava/lang/NoSuchFieldError; {:try_start_2 .. :try_end_2} :catch_0

    :goto_2
    return-void

    :catch_0
    move-exception v0

    goto :goto_2

    :catch_1
    move-exception v0

    goto :goto_1

    :catch_2
    move-exception v0

    goto :goto_0
.end method
