.class public Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceLoginInfo;
.super Ljava/lang/Object;
.source "VoiceLoginInfo.java"


# instance fields
.field public final agentUUID:Ljava/util/UUID;
    .annotation runtime Ljavax/annotation/Nonnull;
    .end annotation
.end field

.field public final password:Ljava/lang/String;

.field public final userName:Ljava/lang/String;

.field public final voiceAccountServerName:Ljava/lang/String;

.field public final voiceSipUriHostname:Ljava/lang/String;


# direct methods
.method public constructor <init>(Landroid/os/Bundle;)V
    .locals 1
    .param p1, "bundle"    # Landroid/os/Bundle;

    .prologue
    .line 25
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 26
    const-string v0, "voiceSipUriHostname"

    invoke-virtual {p1, v0}, Landroid/os/Bundle;->getString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v0

    iput-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceLoginInfo;->voiceSipUriHostname:Ljava/lang/String;

    .line 27
    const-string v0, "voiceAccountServerName"

    invoke-virtual {p1, v0}, Landroid/os/Bundle;->getString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v0

    iput-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceLoginInfo;->voiceAccountServerName:Ljava/lang/String;

    .line 28
    const-string v0, "agentUUID"

    invoke-virtual {p1, v0}, Landroid/os/Bundle;->getString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v0

    invoke-static {v0}, Ljava/util/UUID;->fromString(Ljava/lang/String;)Ljava/util/UUID;

    move-result-object v0

    iput-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceLoginInfo;->agentUUID:Ljava/util/UUID;

    .line 29
    const-string v0, "userName"

    invoke-virtual {p1, v0}, Landroid/os/Bundle;->getString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v0

    iput-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceLoginInfo;->userName:Ljava/lang/String;

    .line 30
    const-string v0, "password"

    invoke-virtual {p1, v0}, Landroid/os/Bundle;->getString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v0

    iput-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceLoginInfo;->password:Ljava/lang/String;

    .line 31
    return-void
.end method

.method public constructor <init>(Ljava/lang/String;Ljava/lang/String;Ljava/util/UUID;Ljava/lang/String;Ljava/lang/String;)V
    .locals 0
    .param p1, "voiceSipUriHostname"    # Ljava/lang/String;
    .param p2, "voiceAccountServerName"    # Ljava/lang/String;
    .param p3, "agentUUID"    # Ljava/util/UUID;
        .annotation runtime Ljavax/annotation/Nonnull;
        .end annotation
    .end param
    .param p4, "userName"    # Ljava/lang/String;
    .param p5, "password"    # Ljava/lang/String;

    .prologue
    .line 17
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 18
    iput-object p1, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceLoginInfo;->voiceSipUriHostname:Ljava/lang/String;

    .line 19
    iput-object p2, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceLoginInfo;->voiceAccountServerName:Ljava/lang/String;

    .line 20
    iput-object p3, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceLoginInfo;->agentUUID:Ljava/util/UUID;

    .line 21
    iput-object p4, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceLoginInfo;->userName:Ljava/lang/String;

    .line 22
    iput-object p5, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceLoginInfo;->password:Ljava/lang/String;

    .line 23
    return-void
.end method


# virtual methods
.method public equals(Ljava/lang/Object;)Z
    .locals 5
    .param p1, "o"    # Ljava/lang/Object;

    .prologue
    const/4 v1, 0x1

    const/4 v2, 0x0

    .line 45
    if-ne p0, p1, :cond_1

    move v2, v1

    .line 56
    :cond_0
    :goto_0
    return v2

    .line 46
    :cond_1
    if-eqz p1, :cond_0

    invoke-virtual {p0}, Ljava/lang/Object;->getClass()Ljava/lang/Class;

    move-result-object v3

    invoke-virtual {p1}, Ljava/lang/Object;->getClass()Ljava/lang/Class;

    move-result-object v4

    if-ne v3, v4, :cond_0

    move-object v0, p1

    .line 48
    check-cast v0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceLoginInfo;

    .line 50
    .local v0, "that":Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceLoginInfo;
    iget-object v3, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceLoginInfo;->voiceSipUriHostname:Ljava/lang/String;

    if-eqz v3, :cond_6

    iget-object v3, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceLoginInfo;->voiceSipUriHostname:Ljava/lang/String;

    iget-object v4, v0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceLoginInfo;->voiceSipUriHostname:Ljava/lang/String;

    invoke-virtual {v3, v4}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v3

    if-eqz v3, :cond_0

    .line 52
    :cond_2
    iget-object v3, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceLoginInfo;->voiceAccountServerName:Ljava/lang/String;

    if-eqz v3, :cond_7

    iget-object v3, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceLoginInfo;->voiceAccountServerName:Ljava/lang/String;

    iget-object v4, v0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceLoginInfo;->voiceAccountServerName:Ljava/lang/String;

    invoke-virtual {v3, v4}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v3

    if-eqz v3, :cond_0

    .line 54
    :cond_3
    iget-object v3, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceLoginInfo;->agentUUID:Ljava/util/UUID;

    iget-object v4, v0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceLoginInfo;->agentUUID:Ljava/util/UUID;

    invoke-virtual {v3, v4}, Ljava/util/UUID;->equals(Ljava/lang/Object;)Z

    move-result v3

    if-eqz v3, :cond_0

    .line 55
    iget-object v3, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceLoginInfo;->userName:Ljava/lang/String;

    if-eqz v3, :cond_8

    iget-object v3, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceLoginInfo;->userName:Ljava/lang/String;

    iget-object v4, v0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceLoginInfo;->userName:Ljava/lang/String;

    invoke-virtual {v3, v4}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v3

    if-eqz v3, :cond_0

    .line 56
    :cond_4
    iget-object v3, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceLoginInfo;->password:Ljava/lang/String;

    if-eqz v3, :cond_9

    iget-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceLoginInfo;->password:Ljava/lang/String;

    iget-object v2, v0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceLoginInfo;->password:Ljava/lang/String;

    invoke-virtual {v1, v2}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v1

    :cond_5
    :goto_1
    move v2, v1

    goto :goto_0

    .line 50
    :cond_6
    iget-object v3, v0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceLoginInfo;->voiceSipUriHostname:Ljava/lang/String;

    if-eqz v3, :cond_2

    goto :goto_0

    .line 52
    :cond_7
    iget-object v3, v0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceLoginInfo;->voiceAccountServerName:Ljava/lang/String;

    if-eqz v3, :cond_3

    goto :goto_0

    .line 55
    :cond_8
    iget-object v3, v0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceLoginInfo;->userName:Ljava/lang/String;

    if-eqz v3, :cond_4

    goto :goto_0

    .line 56
    :cond_9
    iget-object v3, v0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceLoginInfo;->password:Ljava/lang/String;

    if-eqz v3, :cond_5

    move v1, v2

    goto :goto_1
.end method

.method public hashCode()I
    .locals 4

    .prologue
    const/4 v1, 0x0

    .line 61
    iget-object v2, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceLoginInfo;->voiceSipUriHostname:Ljava/lang/String;

    if-eqz v2, :cond_1

    iget-object v2, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceLoginInfo;->voiceSipUriHostname:Ljava/lang/String;

    invoke-virtual {v2}, Ljava/lang/String;->hashCode()I

    move-result v0

    .line 62
    .local v0, "result":I
    :goto_0
    mul-int/lit8 v3, v0, 0x1f

    iget-object v2, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceLoginInfo;->voiceAccountServerName:Ljava/lang/String;

    if-eqz v2, :cond_2

    iget-object v2, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceLoginInfo;->voiceAccountServerName:Ljava/lang/String;

    invoke-virtual {v2}, Ljava/lang/String;->hashCode()I

    move-result v2

    :goto_1
    add-int v0, v3, v2

    .line 63
    mul-int/lit8 v2, v0, 0x1f

    iget-object v3, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceLoginInfo;->agentUUID:Ljava/util/UUID;

    invoke-virtual {v3}, Ljava/util/UUID;->hashCode()I

    move-result v3

    add-int v0, v2, v3

    .line 64
    mul-int/lit8 v3, v0, 0x1f

    iget-object v2, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceLoginInfo;->userName:Ljava/lang/String;

    if-eqz v2, :cond_3

    iget-object v2, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceLoginInfo;->userName:Ljava/lang/String;

    invoke-virtual {v2}, Ljava/lang/String;->hashCode()I

    move-result v2

    :goto_2
    add-int v0, v3, v2

    .line 65
    mul-int/lit8 v2, v0, 0x1f

    iget-object v3, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceLoginInfo;->password:Ljava/lang/String;

    if-eqz v3, :cond_0

    iget-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceLoginInfo;->password:Ljava/lang/String;

    invoke-virtual {v1}, Ljava/lang/String;->hashCode()I

    move-result v1

    :cond_0
    add-int v0, v2, v1

    .line 66
    return v0

    .end local v0    # "result":I
    :cond_1
    move v0, v1

    .line 61
    goto :goto_0

    .restart local v0    # "result":I
    :cond_2
    move v2, v1

    .line 62
    goto :goto_1

    :cond_3
    move v2, v1

    .line 64
    goto :goto_2
.end method

.method public toBundle()Landroid/os/Bundle;
    .locals 3

    .prologue
    .line 34
    new-instance v0, Landroid/os/Bundle;

    invoke-direct {v0}, Landroid/os/Bundle;-><init>()V

    .line 35
    .local v0, "result":Landroid/os/Bundle;
    const-string v1, "voiceSipUriHostname"

    iget-object v2, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceLoginInfo;->voiceSipUriHostname:Ljava/lang/String;

    invoke-virtual {v0, v1, v2}, Landroid/os/Bundle;->putString(Ljava/lang/String;Ljava/lang/String;)V

    .line 36
    const-string v1, "voiceAccountServerName"

    iget-object v2, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceLoginInfo;->voiceAccountServerName:Ljava/lang/String;

    invoke-virtual {v0, v1, v2}, Landroid/os/Bundle;->putString(Ljava/lang/String;Ljava/lang/String;)V

    .line 37
    const-string v1, "agentUUID"

    iget-object v2, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceLoginInfo;->agentUUID:Ljava/util/UUID;

    invoke-virtual {v2}, Ljava/util/UUID;->toString()Ljava/lang/String;

    move-result-object v2

    invoke-virtual {v0, v1, v2}, Landroid/os/Bundle;->putString(Ljava/lang/String;Ljava/lang/String;)V

    .line 38
    const-string v1, "userName"

    iget-object v2, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceLoginInfo;->userName:Ljava/lang/String;

    invoke-virtual {v0, v1, v2}, Landroid/os/Bundle;->putString(Ljava/lang/String;Ljava/lang/String;)V

    .line 39
    const-string v1, "password"

    iget-object v2, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceLoginInfo;->password:Ljava/lang/String;

    invoke-virtual {v0, v1, v2}, Landroid/os/Bundle;->putString(Ljava/lang/String;Ljava/lang/String;)V

    .line 40
    return-object v0
.end method
