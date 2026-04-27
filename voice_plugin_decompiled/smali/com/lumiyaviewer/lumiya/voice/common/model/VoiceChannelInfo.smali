.class public Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;
.super Ljava/lang/Object;
.source "VoiceChannelInfo.java"


# instance fields
.field public final isConference:Z

.field public final isSpatial:Z

.field public final voiceChannelURI:Ljava/lang/String;


# direct methods
.method public constructor <init>(Landroid/net/Uri;)V
    .locals 2
    .param p1, "uri"    # Landroid/net/Uri;

    .prologue
    .line 45
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 46
    const-string v0, "voiceChannelURI"

    invoke-virtual {p1, v0}, Landroid/net/Uri;->getQueryParameter(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v0

    iput-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;->voiceChannelURI:Ljava/lang/String;

    .line 47
    const-string v0, "isSpatial"

    invoke-virtual {p1, v0}, Landroid/net/Uri;->getQueryParameter(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v0

    const-string v1, "true"

    invoke-static {v0, v1}, Lcom/google/common/base/Objects;->equal(Ljava/lang/Object;Ljava/lang/Object;)Z

    move-result v0

    iput-boolean v0, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;->isSpatial:Z

    .line 48
    const-string v0, "isConference"

    invoke-virtual {p1, v0}, Landroid/net/Uri;->getQueryParameter(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v0

    const-string v1, "true"

    invoke-static {v0, v1}, Lcom/google/common/base/Objects;->equal(Ljava/lang/Object;Ljava/lang/Object;)Z

    move-result v0

    iput-boolean v0, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;->isConference:Z

    .line 49
    return-void
.end method

.method public constructor <init>(Landroid/os/Bundle;)V
    .locals 1
    .param p1, "bundle"    # Landroid/os/Bundle;

    .prologue
    .line 39
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 40
    const-string v0, "voiceChannelURI"

    invoke-virtual {p1, v0}, Landroid/os/Bundle;->getString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v0

    iput-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;->voiceChannelURI:Ljava/lang/String;

    .line 41
    const-string v0, "isSpatial"

    invoke-virtual {p1, v0}, Landroid/os/Bundle;->getBoolean(Ljava/lang/String;)Z

    move-result v0

    iput-boolean v0, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;->isSpatial:Z

    .line 42
    const-string v0, "isConference"

    invoke-virtual {p1, v0}, Landroid/os/Bundle;->getBoolean(Ljava/lang/String;)Z

    move-result v0

    iput-boolean v0, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;->isConference:Z

    .line 43
    return-void
.end method

.method public constructor <init>(Ljava/lang/String;ZZ)V
    .locals 0
    .param p1, "voiceChannelURI"    # Ljava/lang/String;
    .param p2, "isSpatial"    # Z
    .param p3, "isConference"    # Z

    .prologue
    .line 33
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 34
    iput-object p1, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;->voiceChannelURI:Ljava/lang/String;

    .line 35
    iput-boolean p2, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;->isSpatial:Z

    .line 36
    iput-boolean p3, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;->isConference:Z

    .line 37
    return-void
.end method

.method public constructor <init>(Ljava/util/UUID;Ljava/lang/String;)V
    .locals 7
    .param p1, "agentUUID"    # Ljava/util/UUID;
        .annotation runtime Ljavax/annotation/Nonnull;
        .end annotation
    .end param
    .param p2, "sipHost"    # Ljava/lang/String;
        .annotation runtime Ljavax/annotation/Nonnull;
        .end annotation
    .end param

    .prologue
    const/4 v6, 0x0

    .line 22
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 24
    const/4 v2, 0x2

    new-array v2, v2, [[B

    invoke-virtual {p1}, Ljava/util/UUID;->getMostSignificantBits()J

    move-result-wide v4

    invoke-static {v4, v5}, Lcom/google/common/primitives/Longs;->toByteArray(J)[B

    move-result-object v3

    aput-object v3, v2, v6

    const/4 v3, 0x1

    invoke-virtual {p1}, Ljava/util/UUID;->getLeastSignificantBits()J

    move-result-wide v4

    invoke-static {v4, v5}, Lcom/google/common/primitives/Longs;->toByteArray(J)[B

    move-result-object v4

    aput-object v4, v2, v3

    invoke-static {v2}, Lcom/google/common/primitives/Bytes;->concat([[B)[B

    move-result-object v1

    .line 25
    .local v1, "uuidBytes":[B
    new-instance v2, Ljava/lang/StringBuilder;

    invoke-direct {v2}, Ljava/lang/StringBuilder;-><init>()V

    const-string v3, "x"

    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v2

    invoke-static {v1, v6}, Lcom/lumiyaviewer/lumiya/base64/Base64;->encodeToString([BZ)Ljava/lang/String;

    move-result-object v3

    const/16 v4, 0x2b

    const/16 v5, 0x2d

    invoke-virtual {v3, v4, v5}, Ljava/lang/String;->replace(CC)Ljava/lang/String;

    move-result-object v3

    const/16 v4, 0x2f

    const/16 v5, 0x5f

    invoke-virtual {v3, v4, v5}, Ljava/lang/String;->replace(CC)Ljava/lang/String;

    move-result-object v3

    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v2

    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v0

    .line 27
    .local v0, "encodedUUID":Ljava/lang/String;
    new-instance v2, Ljava/lang/StringBuilder;

    invoke-direct {v2}, Ljava/lang/StringBuilder;-><init>()V

    const-string v3, "sip:"

    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v2

    invoke-virtual {v2, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v2

    const-string v3, "@"

    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v2

    invoke-virtual {v2, p2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v2

    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v2

    iput-object v2, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;->voiceChannelURI:Ljava/lang/String;

    .line 28
    iput-boolean v6, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;->isSpatial:Z

    .line 29
    iput-boolean v6, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;->isConference:Z

    .line 31
    return-void
.end method

.method public static agentUUIDFromURI(Ljava/lang/String;)Ljava/util/UUID;
    .locals 13
    .param p0, "sipURI"    # Ljava/lang/String;
    .annotation runtime Ljavax/annotation/Nullable;
    .end annotation

    .prologue
    const/16 v12, 0x10

    const/16 v11, 0x8

    const/4 v10, 0x0

    const/4 v9, -0x1

    .line 80
    invoke-static {p0}, Lcom/google/common/base/Strings;->nullToEmpty(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v3

    .line 82
    .local v3, "uri":Ljava/lang/String;
    const/16 v8, 0x3a

    invoke-virtual {v3, v8}, Ljava/lang/String;->indexOf(I)I

    move-result v2

    .line 83
    .local v2, "colonPos":I
    if-eq v2, v9, :cond_0

    .line 84
    add-int/lit8 v8, v2, 0x1

    invoke-virtual {v3, v8}, Ljava/lang/String;->substring(I)Ljava/lang/String;

    move-result-object v3

    .line 86
    :cond_0
    const/16 v8, 0x40

    invoke-virtual {v3, v8}, Ljava/lang/String;->indexOf(I)I

    move-result v0

    .line 87
    .local v0, "atPos":I
    if-eq v0, v9, :cond_1

    .line 88
    invoke-virtual {v3, v10, v0}, Ljava/lang/String;->substring(II)Ljava/lang/String;

    move-result-object v3

    .line 90
    :cond_1
    const-string v8, "x"

    invoke-virtual {v3, v8}, Ljava/lang/String;->startsWith(Ljava/lang/String;)Z

    move-result v8

    if-eqz v8, :cond_2

    .line 91
    const/4 v8, 0x1

    invoke-virtual {v3, v8}, Ljava/lang/String;->substring(I)Ljava/lang/String;

    move-result-object v3

    .line 93
    :cond_2
    const-string v8, "-"

    const-string v9, "+"

    invoke-virtual {v3, v8, v9}, Ljava/lang/String;->replace(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Ljava/lang/String;

    move-result-object v3

    .line 94
    const-string v8, "_"

    const-string v9, "/"

    invoke-virtual {v3, v8, v9}, Ljava/lang/String;->replace(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Ljava/lang/String;

    move-result-object v3

    .line 96
    invoke-static {v3}, Lcom/lumiyaviewer/lumiya/base64/Base64;->decode(Ljava/lang/String;)[B

    move-result-object v1

    .line 98
    .local v1, "bytes":[B
    if-eqz v1, :cond_3

    array-length v8, v1

    if-ne v8, v12, :cond_3

    .line 100
    invoke-static {v1, v10, v11}, Ljava/util/Arrays;->copyOfRange([BII)[B

    move-result-object v8

    invoke-static {v8}, Lcom/google/common/primitives/Longs;->fromByteArray([B)J

    move-result-wide v4

    .line 101
    .local v4, "uuidHigh":J
    invoke-static {v1, v11, v12}, Ljava/util/Arrays;->copyOfRange([BII)[B

    move-result-object v8

    invoke-static {v8}, Lcom/google/common/primitives/Longs;->fromByteArray([B)J

    move-result-wide v6

    .line 102
    .local v6, "uuidLow":J
    new-instance v8, Ljava/util/UUID;

    invoke-direct {v8, v4, v5, v6, v7}, Ljava/util/UUID;-><init>(JJ)V

    .line 105
    .end local v4    # "uuidHigh":J
    .end local v6    # "uuidLow":J
    :goto_0
    return-object v8

    :cond_3
    const/4 v8, 0x0

    goto :goto_0
.end method


# virtual methods
.method public appendToUri(Landroid/net/Uri$Builder;)V
    .locals 2
    .param p1, "builder"    # Landroid/net/Uri$Builder;

    .prologue
    .line 113
    const-string v0, "voiceChannelURI"

    iget-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;->voiceChannelURI:Ljava/lang/String;

    invoke-virtual {p1, v0, v1}, Landroid/net/Uri$Builder;->appendQueryParameter(Ljava/lang/String;Ljava/lang/String;)Landroid/net/Uri$Builder;

    .line 114
    const-string v1, "isSpatial"

    iget-boolean v0, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;->isSpatial:Z

    if-eqz v0, :cond_0

    const-string v0, "true"

    :goto_0
    invoke-virtual {p1, v1, v0}, Landroid/net/Uri$Builder;->appendQueryParameter(Ljava/lang/String;Ljava/lang/String;)Landroid/net/Uri$Builder;

    .line 115
    const-string v1, "isConference"

    iget-boolean v0, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;->isConference:Z

    if-eqz v0, :cond_1

    const-string v0, "true"

    :goto_1
    invoke-virtual {p1, v1, v0}, Landroid/net/Uri$Builder;->appendQueryParameter(Ljava/lang/String;Ljava/lang/String;)Landroid/net/Uri$Builder;

    .line 116
    return-void

    .line 114
    :cond_0
    const-string v0, "false"

    goto :goto_0

    .line 115
    :cond_1
    const-string v0, "false"

    goto :goto_1
.end method

.method public equals(Ljava/lang/Object;)Z
    .locals 5
    .param p1, "o"    # Ljava/lang/Object;

    .prologue
    const/4 v1, 0x1

    const/4 v2, 0x0

    .line 61
    if-ne p0, p1, :cond_1

    move v2, v1

    .line 68
    :cond_0
    :goto_0
    return v2

    .line 62
    :cond_1
    if-eqz p1, :cond_0

    invoke-virtual {p0}, Ljava/lang/Object;->getClass()Ljava/lang/Class;

    move-result-object v3

    invoke-virtual {p1}, Ljava/lang/Object;->getClass()Ljava/lang/Class;

    move-result-object v4

    if-ne v3, v4, :cond_0

    move-object v0, p1

    .line 64
    check-cast v0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;

    .line 66
    .local v0, "that":Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;
    iget-boolean v3, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;->isSpatial:Z

    iget-boolean v4, v0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;->isSpatial:Z

    if-ne v3, v4, :cond_0

    .line 67
    iget-boolean v3, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;->isConference:Z

    iget-boolean v4, v0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;->isConference:Z

    if-ne v3, v4, :cond_0

    .line 68
    iget-object v3, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;->voiceChannelURI:Ljava/lang/String;

    if-eqz v3, :cond_3

    iget-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;->voiceChannelURI:Ljava/lang/String;

    iget-object v2, v0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;->voiceChannelURI:Ljava/lang/String;

    invoke-virtual {v1, v2}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v1

    :cond_2
    :goto_1
    move v2, v1

    goto :goto_0

    :cond_3
    iget-object v3, v0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;->voiceChannelURI:Ljava/lang/String;

    if-eqz v3, :cond_2

    move v1, v2

    goto :goto_1
.end method

.method public getAgentUUID()Ljava/util/UUID;
    .locals 1
    .annotation runtime Ljavax/annotation/Nullable;
    .end annotation

    .prologue
    .line 109
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;->voiceChannelURI:Ljava/lang/String;

    invoke-static {v0}, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;->agentUUIDFromURI(Ljava/lang/String;)Ljava/util/UUID;

    move-result-object v0

    return-object v0
.end method

.method public hashCode()I
    .locals 5

    .prologue
    const/4 v3, 0x1

    const/4 v1, 0x0

    .line 73
    iget-object v2, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;->voiceChannelURI:Ljava/lang/String;

    if-eqz v2, :cond_0

    iget-object v2, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;->voiceChannelURI:Ljava/lang/String;

    invoke-virtual {v2}, Ljava/lang/String;->hashCode()I

    move-result v0

    .line 74
    .local v0, "result":I
    :goto_0
    mul-int/lit8 v4, v0, 0x1f

    iget-boolean v2, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;->isSpatial:Z

    if-eqz v2, :cond_1

    move v2, v3

    :goto_1
    add-int v0, v4, v2

    .line 75
    mul-int/lit8 v2, v0, 0x1f

    iget-boolean v4, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;->isConference:Z

    if-eqz v4, :cond_2

    :goto_2
    add-int v0, v2, v3

    .line 76
    return v0

    .end local v0    # "result":I
    :cond_0
    move v0, v1

    .line 73
    goto :goto_0

    .restart local v0    # "result":I
    :cond_1
    move v2, v1

    .line 74
    goto :goto_1

    :cond_2
    move v3, v1

    .line 75
    goto :goto_2
.end method

.method public toBundle()Landroid/os/Bundle;
    .locals 3

    .prologue
    .line 52
    new-instance v0, Landroid/os/Bundle;

    invoke-direct {v0}, Landroid/os/Bundle;-><init>()V

    .line 53
    .local v0, "result":Landroid/os/Bundle;
    const-string v1, "voiceChannelURI"

    iget-object v2, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;->voiceChannelURI:Ljava/lang/String;

    invoke-virtual {v0, v1, v2}, Landroid/os/Bundle;->putString(Ljava/lang/String;Ljava/lang/String;)V

    .line 54
    const-string v1, "isSpatial"

    iget-boolean v2, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;->isSpatial:Z

    invoke-virtual {v0, v1, v2}, Landroid/os/Bundle;->putBoolean(Ljava/lang/String;Z)V

    .line 55
    const-string v1, "isConference"

    iget-boolean v2, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;->isConference:Z

    invoke-virtual {v0, v1, v2}, Landroid/os/Bundle;->putBoolean(Ljava/lang/String;Z)V

    .line 56
    return-object v0
.end method
