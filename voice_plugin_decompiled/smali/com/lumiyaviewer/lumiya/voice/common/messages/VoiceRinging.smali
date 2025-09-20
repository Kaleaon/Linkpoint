.class public Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceRinging;
.super Ljava/lang/Object;
.source "VoiceRinging.java"

# interfaces
.implements Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessage;


# instance fields
.field public final agentUUID:Ljava/util/UUID;
    .annotation runtime Ljavax/annotation/Nullable;
    .end annotation
.end field

.field public final sessionHandle:Ljava/lang/String;
    .annotation runtime Ljavax/annotation/Nonnull;
    .end annotation
.end field

.field public final voiceChannelInfo:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;


# direct methods
.method public constructor <init>(Landroid/net/Uri;)V
    .locals 2
    .param p1, "uri"    # Landroid/net/Uri;

    .prologue
    .line 31
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 32
    const-string v1, "sessionHandle"

    invoke-virtual {p1, v1}, Landroid/net/Uri;->getQueryParameter(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v1

    iput-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceRinging;->sessionHandle:Ljava/lang/String;

    .line 33
    const-string v1, "agentUUID"

    invoke-virtual {p1, v1}, Landroid/net/Uri;->getQueryParameter(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v0

    .line 34
    .local v0, "agentUUIDString":Ljava/lang/String;
    if-eqz v0, :cond_0

    invoke-static {v0}, Ljava/util/UUID;->fromString(Ljava/lang/String;)Ljava/util/UUID;

    move-result-object v1

    :goto_0
    iput-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceRinging;->agentUUID:Ljava/util/UUID;

    .line 35
    new-instance v1, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;

    invoke-direct {v1, p1}, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;-><init>(Landroid/net/Uri;)V

    iput-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceRinging;->voiceChannelInfo:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;

    .line 36
    return-void

    .line 34
    :cond_0
    const/4 v1, 0x0

    goto :goto_0
.end method

.method public constructor <init>(Landroid/os/Bundle;)V
    .locals 3
    .param p1, "bundle"    # Landroid/os/Bundle;

    .prologue
    .line 24
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 25
    const-string v1, "sessionHandle"

    invoke-virtual {p1, v1}, Landroid/os/Bundle;->getString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v1

    iput-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceRinging;->sessionHandle:Ljava/lang/String;

    .line 26
    new-instance v1, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;

    const-string v2, "voiceChannelInfo"

    invoke-virtual {p1, v2}, Landroid/os/Bundle;->getBundle(Ljava/lang/String;)Landroid/os/Bundle;

    move-result-object v2

    invoke-direct {v1, v2}, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;-><init>(Landroid/os/Bundle;)V

    iput-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceRinging;->voiceChannelInfo:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;

    .line 27
    const-string v1, "agentUUID"

    invoke-virtual {p1, v1}, Landroid/os/Bundle;->getString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v0

    .line 28
    .local v0, "agentUUIDString":Ljava/lang/String;
    if-eqz v0, :cond_0

    invoke-static {v0}, Ljava/util/UUID;->fromString(Ljava/lang/String;)Ljava/util/UUID;

    move-result-object v1

    :goto_0
    iput-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceRinging;->agentUUID:Ljava/util/UUID;

    .line 29
    return-void

    .line 28
    :cond_0
    const/4 v1, 0x0

    goto :goto_0
.end method

.method public constructor <init>(Ljava/lang/String;Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;Ljava/util/UUID;)V
    .locals 0
    .param p1, "sessionHandle"    # Ljava/lang/String;
        .annotation runtime Ljavax/annotation/Nonnull;
        .end annotation
    .end param
    .param p2, "voiceChannelInfo"    # Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;
    .param p3, "agentUUID"    # Ljava/util/UUID;
        .annotation runtime Ljavax/annotation/Nullable;
        .end annotation
    .end param

    .prologue
    .line 18
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 19
    iput-object p1, p0, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceRinging;->sessionHandle:Ljava/lang/String;

    .line 20
    iput-object p2, p0, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceRinging;->voiceChannelInfo:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;

    .line 21
    iput-object p3, p0, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceRinging;->agentUUID:Ljava/util/UUID;

    .line 22
    return-void
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
    const-string v1, "sessionHandle"

    iget-object v2, p0, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceRinging;->sessionHandle:Ljava/lang/String;

    invoke-virtual {v0, v1, v2}, Landroid/os/Bundle;->putString(Ljava/lang/String;Ljava/lang/String;)V

    .line 42
    const-string v1, "voiceChannelInfo"

    iget-object v2, p0, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceRinging;->voiceChannelInfo:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;

    invoke-virtual {v2}, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;->toBundle()Landroid/os/Bundle;

    move-result-object v2

    invoke-virtual {v0, v1, v2}, Landroid/os/Bundle;->putBundle(Ljava/lang/String;Landroid/os/Bundle;)V

    .line 43
    const-string v2, "agentUUID"

    iget-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceRinging;->agentUUID:Ljava/util/UUID;

    if-eqz v1, :cond_0

    iget-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceRinging;->agentUUID:Ljava/util/UUID;

    invoke-virtual {v1}, Ljava/util/UUID;->toString()Ljava/lang/String;

    move-result-object v1

    :goto_0
    invoke-virtual {v0, v2, v1}, Landroid/os/Bundle;->putString(Ljava/lang/String;Ljava/lang/String;)V

    .line 44
    return-object v0

    .line 43
    :cond_0
    const/4 v1, 0x0

    goto :goto_0
.end method

.method public toUri()Landroid/net/Uri;
    .locals 4

    .prologue
    .line 49
    new-instance v1, Landroid/net/Uri$Builder;

    invoke-direct {v1}, Landroid/net/Uri$Builder;-><init>()V

    const-string v2, "com.lumiyaviewer.lumiya"

    .line 50
    invoke-virtual {v1, v2}, Landroid/net/Uri$Builder;->scheme(Ljava/lang/String;)Landroid/net/Uri$Builder;

    move-result-object v1

    const-string v2, "voice"

    .line 51
    invoke-virtual {v1, v2}, Landroid/net/Uri$Builder;->authority(Ljava/lang/String;)Landroid/net/Uri$Builder;

    move-result-object v1

    const-string v2, "sessionHandle"

    iget-object v3, p0, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceRinging;->sessionHandle:Ljava/lang/String;

    .line 52
    invoke-virtual {v1, v2, v3}, Landroid/net/Uri$Builder;->appendQueryParameter(Ljava/lang/String;Ljava/lang/String;)Landroid/net/Uri$Builder;

    move-result-object v0

    .line 53
    .local v0, "builder":Landroid/net/Uri$Builder;
    iget-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceRinging;->agentUUID:Ljava/util/UUID;

    if-eqz v1, :cond_0

    .line 54
    const-string v1, "agentUUID"

    iget-object v2, p0, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceRinging;->agentUUID:Ljava/util/UUID;

    invoke-virtual {v2}, Ljava/util/UUID;->toString()Ljava/lang/String;

    move-result-object v2

    invoke-virtual {v0, v1, v2}, Landroid/net/Uri$Builder;->appendQueryParameter(Ljava/lang/String;Ljava/lang/String;)Landroid/net/Uri$Builder;

    .line 56
    :cond_0
    iget-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceRinging;->voiceChannelInfo:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;

    invoke-virtual {v1, v0}, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;->appendToUri(Landroid/net/Uri$Builder;)V

    .line 58
    invoke-virtual {v0}, Landroid/net/Uri$Builder;->build()Landroid/net/Uri;

    move-result-object v1

    return-object v1
.end method
