.class public Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceInitialize;
.super Ljava/lang/Object;
.source "VoiceInitialize.java"

# interfaces
.implements Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessage;


# instance fields
.field public final appVersionCode:I


# direct methods
.method public constructor <init>(I)V
    .locals 0
    .param p1, "appVersionCode"    # I

    .prologue
    .line 11
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 12
    iput p1, p0, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceInitialize;->appVersionCode:I

    .line 13
    return-void
.end method

.method public constructor <init>(Landroid/os/Bundle;)V
    .locals 1
    .param p1, "bundle"    # Landroid/os/Bundle;

    .prologue
    .line 15
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 16
    const-string v0, "appVersionCode"

    invoke-virtual {p1, v0}, Landroid/os/Bundle;->getInt(Ljava/lang/String;)I

    move-result v0

    iput v0, p0, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceInitialize;->appVersionCode:I

    .line 17
    return-void
.end method


# virtual methods
.method public toBundle()Landroid/os/Bundle;
    .locals 3

    .prologue
    .line 21
    new-instance v0, Landroid/os/Bundle;

    invoke-direct {v0}, Landroid/os/Bundle;-><init>()V

    .line 22
    .local v0, "result":Landroid/os/Bundle;
    const-string v1, "appVersionCode"

    iget v2, p0, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceInitialize;->appVersionCode:I

    invoke-virtual {v0, v1, v2}, Landroid/os/Bundle;->putInt(Ljava/lang/String;I)V

    .line 23
    return-object v0
.end method
