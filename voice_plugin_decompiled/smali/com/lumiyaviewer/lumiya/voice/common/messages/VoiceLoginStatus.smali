.class public Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceLoginStatus;
.super Ljava/lang/Object;
.source "VoiceLoginStatus.java"

# interfaces
.implements Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessage;


# instance fields
.field public final errorMessage:Ljava/lang/String;
    .annotation runtime Ljavax/annotation/Nullable;
    .end annotation
.end field

.field public final loggedIn:Z

.field public final voiceLoginInfo:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceLoginInfo;
    .annotation runtime Ljavax/annotation/Nullable;
    .end annotation
.end field


# direct methods
.method public constructor <init>(Landroid/os/Bundle;)V
    .locals 2
    .param p1, "bundle"    # Landroid/os/Bundle;

    .prologue
    .line 21
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 22
    const-string v1, "voiceLoginInfo"

    invoke-virtual {p1, v1}, Landroid/os/Bundle;->getBundle(Ljava/lang/String;)Landroid/os/Bundle;

    move-result-object v0

    .line 23
    .local v0, "voiceLoginInfo":Landroid/os/Bundle;
    if-eqz v0, :cond_0

    new-instance v1, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceLoginInfo;

    invoke-direct {v1, v0}, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceLoginInfo;-><init>(Landroid/os/Bundle;)V

    :goto_0
    iput-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceLoginStatus;->voiceLoginInfo:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceLoginInfo;

    .line 24
    const-string v1, "loggedIn"

    invoke-virtual {p1, v1}, Landroid/os/Bundle;->getBoolean(Ljava/lang/String;)Z

    move-result v1

    iput-boolean v1, p0, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceLoginStatus;->loggedIn:Z

    .line 25
    const-string v1, "errorMessage"

    invoke-virtual {p1, v1}, Landroid/os/Bundle;->getString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v1

    iput-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceLoginStatus;->errorMessage:Ljava/lang/String;

    .line 26
    return-void

    .line 23
    :cond_0
    const/4 v1, 0x0

    goto :goto_0
.end method

.method public constructor <init>(Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceLoginInfo;ZLjava/lang/String;)V
    .locals 0
    .param p1, "voiceLoginInfo"    # Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceLoginInfo;
        .annotation runtime Ljavax/annotation/Nullable;
        .end annotation
    .end param
    .param p2, "loggedIn"    # Z
    .param p3, "errorMessage"    # Ljava/lang/String;
        .annotation runtime Ljavax/annotation/Nullable;
        .end annotation
    .end param

    .prologue
    .line 15
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 16
    iput-object p1, p0, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceLoginStatus;->voiceLoginInfo:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceLoginInfo;

    .line 17
    iput-boolean p2, p0, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceLoginStatus;->loggedIn:Z

    .line 18
    iput-object p3, p0, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceLoginStatus;->errorMessage:Ljava/lang/String;

    .line 19
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
    const-string v2, "voiceLoginInfo"

    iget-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceLoginStatus;->voiceLoginInfo:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceLoginInfo;

    if-eqz v1, :cond_0

    iget-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceLoginStatus;->voiceLoginInfo:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceLoginInfo;

    invoke-virtual {v1}, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceLoginInfo;->toBundle()Landroid/os/Bundle;

    move-result-object v1

    :goto_0
    invoke-virtual {v0, v2, v1}, Landroid/os/Bundle;->putBundle(Ljava/lang/String;Landroid/os/Bundle;)V

    .line 33
    const-string v1, "loggedIn"

    iget-boolean v2, p0, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceLoginStatus;->loggedIn:Z

    invoke-virtual {v0, v1, v2}, Landroid/os/Bundle;->putBoolean(Ljava/lang/String;Z)V

    .line 34
    const-string v1, "errorMessage"

    iget-object v2, p0, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceLoginStatus;->errorMessage:Ljava/lang/String;

    invoke-virtual {v0, v1, v2}, Landroid/os/Bundle;->putString(Ljava/lang/String;Ljava/lang/String;)V

    .line 35
    return-object v0

    .line 32
    :cond_0
    const/4 v1, 0x0

    goto :goto_0
.end method
