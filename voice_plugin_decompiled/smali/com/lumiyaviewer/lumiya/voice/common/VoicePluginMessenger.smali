.class public Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessenger;
.super Ljava/lang/Object;
.source "VoicePluginMessenger.java"


# direct methods
.method public constructor <init>()V
    .locals 0

    .prologue
    .line 8
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method

.method public static sendMessage(Landroid/os/Messenger;Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessage;Landroid/os/Messenger;)Z
    .locals 6
    .param p0, "messenger"    # Landroid/os/Messenger;
    .param p1, "type"    # Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;
    .param p2, "message"    # Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessage;
    .param p3, "replyTo"    # Landroid/os/Messenger;

    .prologue
    const/4 v3, 0x0

    .line 12
    if-eqz p0, :cond_0

    .line 13
    new-instance v0, Landroid/os/Bundle;

    invoke-direct {v0}, Landroid/os/Bundle;-><init>()V

    .line 14
    .local v0, "bundle":Landroid/os/Bundle;
    const-string v4, "messageType"

    invoke-virtual {p1}, Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;->toString()Ljava/lang/String;

    move-result-object v5

    invoke-virtual {v0, v4, v5}, Landroid/os/Bundle;->putString(Ljava/lang/String;Ljava/lang/String;)V

    .line 15
    const-string v4, "message"

    invoke-interface {p2}, Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessage;->toBundle()Landroid/os/Bundle;

    move-result-object v5

    invoke-virtual {v0, v4, v5}, Landroid/os/Bundle;->putBundle(Ljava/lang/String;Landroid/os/Bundle;)V

    .line 16
    const/4 v4, 0x0

    const/16 v5, 0xc8

    invoke-static {v4, v5, v0}, Landroid/os/Message;->obtain(Landroid/os/Handler;ILjava/lang/Object;)Landroid/os/Message;

    move-result-object v2

    .line 17
    .local v2, "msg":Landroid/os/Message;
    iput-object p3, v2, Landroid/os/Message;->replyTo:Landroid/os/Messenger;

    .line 19
    :try_start_0
    invoke-virtual {p0, v2}, Landroid/os/Messenger;->send(Landroid/os/Message;)V
    :try_end_0
    .catch Landroid/os/RemoteException; {:try_start_0 .. :try_end_0} :catch_0

    .line 20
    const/4 v3, 0x1

    .line 26
    .end local v0    # "bundle":Landroid/os/Bundle;
    .end local v2    # "msg":Landroid/os/Message;
    :cond_0
    :goto_0
    return v3

    .line 21
    .restart local v0    # "bundle":Landroid/os/Bundle;
    .restart local v2    # "msg":Landroid/os/Message;
    :catch_0
    move-exception v1

    .line 22
    .local v1, "e":Landroid/os/RemoteException;
    goto :goto_0
.end method
