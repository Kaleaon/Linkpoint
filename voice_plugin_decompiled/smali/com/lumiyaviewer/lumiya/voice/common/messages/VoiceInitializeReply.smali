.class public Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceInitializeReply;
.super Ljava/lang/Object;
.source "VoiceInitializeReply.java"

# interfaces
.implements Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessage;


# instance fields
.field public final appVersionOk:Z

.field public final errorMessage:Ljava/lang/String;
    .annotation runtime Ljavax/annotation/Nullable;
    .end annotation
.end field

.field public final pluginVersionCode:I


# direct methods
.method public constructor <init>(ILjava/lang/String;Z)V
    .locals 0
    .param p1, "pluginVersionCode"    # I
    .param p2, "errorMessage"    # Ljava/lang/String;
        .annotation runtime Ljavax/annotation/Nullable;
        .end annotation
    .end param
    .param p3, "appVersionOk"    # Z

    .prologue
    .line 14
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 15
    iput p1, p0, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceInitializeReply;->pluginVersionCode:I

    .line 16
    iput-object p2, p0, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceInitializeReply;->errorMessage:Ljava/lang/String;

    .line 17
    iput-boolean p3, p0, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceInitializeReply;->appVersionOk:Z

    .line 18
    return-void
.end method

.method public constructor <init>(Landroid/os/Bundle;)V
    .locals 1
    .param p1, "bundle"    # Landroid/os/Bundle;

    .prologue
    .line 20
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 21
    const-string v0, "pluginVersionCode"

    invoke-virtual {p1, v0}, Landroid/os/Bundle;->getInt(Ljava/lang/String;)I

    move-result v0

    iput v0, p0, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceInitializeReply;->pluginVersionCode:I

    .line 22
    const-string v0, "errorMessage"

    invoke-virtual {p1, v0}, Landroid/os/Bundle;->getString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v0

    iput-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceInitializeReply;->errorMessage:Ljava/lang/String;

    .line 23
    const-string v0, "appVersionOk"

    invoke-virtual {p1, v0}, Landroid/os/Bundle;->getBoolean(Ljava/lang/String;)Z

    move-result v0

    iput-boolean v0, p0, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceInitializeReply;->appVersionOk:Z

    .line 24
    return-void
.end method


# virtual methods
.method public toBundle()Landroid/os/Bundle;
    .locals 3

    .prologue
    .line 28
    new-instance v0, Landroid/os/Bundle;

    invoke-direct {v0}, Landroid/os/Bundle;-><init>()V

    .line 29
    .local v0, "result":Landroid/os/Bundle;
    const-string v1, "pluginVersionCode"

    iget v2, p0, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceInitializeReply;->pluginVersionCode:I

    invoke-virtual {v0, v1, v2}, Landroid/os/Bundle;->putInt(Ljava/lang/String;I)V

    .line 30
    const-string v1, "errorMessage"

    iget-object v2, p0, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceInitializeReply;->errorMessage:Ljava/lang/String;

    invoke-virtual {v0, v1, v2}, Landroid/os/Bundle;->putString(Ljava/lang/String;Ljava/lang/String;)V

    .line 31
    const-string v1, "appVersionOk"

    iget-boolean v2, p0, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceInitializeReply;->appVersionOk:Z

    invoke-virtual {v0, v1, v2}, Landroid/os/Bundle;->putBoolean(Ljava/lang/String;Z)V

    .line 32
    return-object v0
.end method
