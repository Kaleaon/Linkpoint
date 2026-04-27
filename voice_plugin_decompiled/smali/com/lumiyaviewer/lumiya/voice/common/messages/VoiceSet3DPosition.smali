.class public Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceSet3DPosition;
.super Ljava/lang/Object;
.source "VoiceSet3DPosition.java"

# interfaces
.implements Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessage;


# instance fields
.field public final listenerPosition:Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DPosition;
    .annotation runtime Ljavax/annotation/Nonnull;
    .end annotation
.end field

.field public final speakerPosition:Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DPosition;
    .annotation runtime Ljavax/annotation/Nonnull;
    .end annotation
.end field

.field public final voiceChannelInfo:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;
    .annotation runtime Ljavax/annotation/Nonnull;
    .end annotation
.end field


# direct methods
.method public constructor <init>(Landroid/os/Bundle;)V
    .locals 2
    .param p1, "bundle"    # Landroid/os/Bundle;

    .prologue
    .line 22
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 23
    new-instance v0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;

    const-string v1, "voiceChannelInfo"

    invoke-virtual {p1, v1}, Landroid/os/Bundle;->getBundle(Ljava/lang/String;)Landroid/os/Bundle;

    move-result-object v1

    invoke-direct {v0, v1}, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;-><init>(Landroid/os/Bundle;)V

    iput-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceSet3DPosition;->voiceChannelInfo:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;

    .line 24
    new-instance v0, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DPosition;

    const-string v1, "speakerPosition"

    invoke-virtual {p1, v1}, Landroid/os/Bundle;->getBundle(Ljava/lang/String;)Landroid/os/Bundle;

    move-result-object v1

    invoke-direct {v0, v1}, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DPosition;-><init>(Landroid/os/Bundle;)V

    iput-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceSet3DPosition;->speakerPosition:Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DPosition;

    .line 25
    new-instance v0, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DPosition;

    const-string v1, "listenerPosition"

    invoke-virtual {p1, v1}, Landroid/os/Bundle;->getBundle(Ljava/lang/String;)Landroid/os/Bundle;

    move-result-object v1

    invoke-direct {v0, v1}, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DPosition;-><init>(Landroid/os/Bundle;)V

    iput-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceSet3DPosition;->listenerPosition:Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DPosition;

    .line 26
    return-void
.end method

.method public constructor <init>(Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DPosition;Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DPosition;)V
    .locals 0
    .param p1, "voiceChannelInfo"    # Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;
        .annotation runtime Ljavax/annotation/Nonnull;
        .end annotation
    .end param
    .param p2, "speakerPosition"    # Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DPosition;
        .annotation runtime Ljavax/annotation/Nonnull;
        .end annotation
    .end param
    .param p3, "listenerPosition"    # Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DPosition;
        .annotation runtime Ljavax/annotation/Nonnull;
        .end annotation
    .end param

    .prologue
    .line 16
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 17
    iput-object p1, p0, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceSet3DPosition;->voiceChannelInfo:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;

    .line 18
    iput-object p2, p0, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceSet3DPosition;->speakerPosition:Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DPosition;

    .line 19
    iput-object p3, p0, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceSet3DPosition;->listenerPosition:Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DPosition;

    .line 20
    return-void
.end method


# virtual methods
.method public toBundle()Landroid/os/Bundle;
    .locals 3

    .prologue
    .line 30
    new-instance v0, Landroid/os/Bundle;

    invoke-direct {v0}, Landroid/os/Bundle;-><init>()V

    .line 31
    .local v0, "bundle":Landroid/os/Bundle;
    const-string v1, "voiceChannelInfo"

    iget-object v2, p0, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceSet3DPosition;->voiceChannelInfo:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;

    invoke-virtual {v2}, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;->toBundle()Landroid/os/Bundle;

    move-result-object v2

    invoke-virtual {v0, v1, v2}, Landroid/os/Bundle;->putBundle(Ljava/lang/String;Landroid/os/Bundle;)V

    .line 32
    const-string v1, "speakerPosition"

    iget-object v2, p0, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceSet3DPosition;->speakerPosition:Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DPosition;

    invoke-virtual {v2}, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DPosition;->toBundle()Landroid/os/Bundle;

    move-result-object v2

    invoke-virtual {v0, v1, v2}, Landroid/os/Bundle;->putBundle(Ljava/lang/String;Landroid/os/Bundle;)V

    .line 33
    const-string v1, "listenerPosition"

    iget-object v2, p0, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceSet3DPosition;->listenerPosition:Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DPosition;

    invoke-virtual {v2}, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DPosition;->toBundle()Landroid/os/Bundle;

    move-result-object v2

    invoke-virtual {v0, v1, v2}, Landroid/os/Bundle;->putBundle(Ljava/lang/String;Landroid/os/Bundle;)V

    .line 34
    return-object v0
.end method
