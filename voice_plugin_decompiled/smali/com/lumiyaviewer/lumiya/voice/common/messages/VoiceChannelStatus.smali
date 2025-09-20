.class public Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceChannelStatus;
.super Ljava/lang/Object;
.source "VoiceChannelStatus.java"

# interfaces
.implements Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessage;


# instance fields
.field public final channelInfo:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;
    .annotation runtime Ljavax/annotation/Nonnull;
    .end annotation
.end field

.field public final chatInfo:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;
    .annotation runtime Ljavax/annotation/Nonnull;
    .end annotation
.end field

.field public final errorMessage:Ljava/lang/String;
    .annotation runtime Ljavax/annotation/Nullable;
    .end annotation
.end field


# direct methods
.method public constructor <init>(Landroid/os/Bundle;)V
    .locals 2
    .param p1, "bundle"    # Landroid/os/Bundle;

    .prologue
    .line 23
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 24
    new-instance v0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;

    const-string v1, "channelInfo"

    invoke-virtual {p1, v1}, Landroid/os/Bundle;->getBundle(Ljava/lang/String;)Landroid/os/Bundle;

    move-result-object v1

    invoke-direct {v0, v1}, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;-><init>(Landroid/os/Bundle;)V

    iput-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceChannelStatus;->channelInfo:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;

    .line 25
    const-string v0, "chatInfo"

    invoke-virtual {p1, v0}, Landroid/os/Bundle;->getBundle(Ljava/lang/String;)Landroid/os/Bundle;

    move-result-object v0

    invoke-static {v0}, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;->create(Landroid/os/Bundle;)Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;

    move-result-object v0

    iput-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceChannelStatus;->chatInfo:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;

    .line 26
    const-string v0, "errorMessage"

    invoke-virtual {p1, v0}, Landroid/os/Bundle;->getString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v0

    iput-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceChannelStatus;->errorMessage:Ljava/lang/String;

    .line 27
    return-void
.end method

.method public constructor <init>(Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;Ljava/lang/String;)V
    .locals 0
    .param p1, "channelInfo"    # Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;
        .annotation runtime Ljavax/annotation/Nonnull;
        .end annotation
    .end param
    .param p2, "chatInfo"    # Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;
        .annotation runtime Ljavax/annotation/Nonnull;
        .end annotation
    .end param
    .param p3, "errorMessage"    # Ljava/lang/String;
        .annotation runtime Ljavax/annotation/Nullable;
        .end annotation
    .end param

    .prologue
    .line 17
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 18
    iput-object p1, p0, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceChannelStatus;->channelInfo:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;

    .line 19
    iput-object p2, p0, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceChannelStatus;->chatInfo:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;

    .line 20
    iput-object p3, p0, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceChannelStatus;->errorMessage:Ljava/lang/String;

    .line 21
    return-void
.end method


# virtual methods
.method public toBundle()Landroid/os/Bundle;
    .locals 3

    .prologue
    .line 31
    new-instance v0, Landroid/os/Bundle;

    invoke-direct {v0}, Landroid/os/Bundle;-><init>()V

    .line 32
    .local v0, "result":Landroid/os/Bundle;
    const-string v1, "channelInfo"

    iget-object v2, p0, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceChannelStatus;->channelInfo:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;

    invoke-virtual {v2}, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;->toBundle()Landroid/os/Bundle;

    move-result-object v2

    invoke-virtual {v0, v1, v2}, Landroid/os/Bundle;->putBundle(Ljava/lang/String;Landroid/os/Bundle;)V

    .line 33
    const-string v1, "chatInfo"

    iget-object v2, p0, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceChannelStatus;->chatInfo:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;

    invoke-virtual {v2}, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;->toBundle()Landroid/os/Bundle;

    move-result-object v2

    invoke-virtual {v0, v1, v2}, Landroid/os/Bundle;->putBundle(Ljava/lang/String;Landroid/os/Bundle;)V

    .line 34
    const-string v1, "errorMessage"

    iget-object v2, p0, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceChannelStatus;->errorMessage:Ljava/lang/String;

    invoke-virtual {v0, v1, v2}, Landroid/os/Bundle;->putString(Ljava/lang/String;Ljava/lang/String;)V

    .line 35
    return-object v0
.end method
