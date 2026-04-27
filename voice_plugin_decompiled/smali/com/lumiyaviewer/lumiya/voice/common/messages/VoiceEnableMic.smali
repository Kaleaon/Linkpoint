.class public Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceEnableMic;
.super Ljava/lang/Object;
.source "VoiceEnableMic.java"

# interfaces
.implements Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessage;


# instance fields
.field public final enableMic:Z


# direct methods
.method public constructor <init>(Landroid/os/Bundle;)V
    .locals 1
    .param p1, "bundle"    # Landroid/os/Bundle;

    .prologue
    .line 14
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 15
    const-string v0, "enableMic"

    invoke-virtual {p1, v0}, Landroid/os/Bundle;->getBoolean(Ljava/lang/String;)Z

    move-result v0

    iput-boolean v0, p0, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceEnableMic;->enableMic:Z

    .line 16
    return-void
.end method

.method public constructor <init>(Z)V
    .locals 0
    .param p1, "enableMic"    # Z

    .prologue
    .line 10
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 11
    iput-boolean p1, p0, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceEnableMic;->enableMic:Z

    .line 12
    return-void
.end method


# virtual methods
.method public toBundle()Landroid/os/Bundle;
    .locals 3

    .prologue
    .line 20
    new-instance v0, Landroid/os/Bundle;

    invoke-direct {v0}, Landroid/os/Bundle;-><init>()V

    .line 21
    .local v0, "bundle":Landroid/os/Bundle;
    const-string v1, "enableMic"

    iget-boolean v2, p0, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceEnableMic;->enableMic:Z

    invoke-virtual {v0, v1, v2}, Landroid/os/Bundle;->putBoolean(Ljava/lang/String;Z)V

    .line 22
    return-object v0
.end method
