.class public Lcom/lumiyaviewer/lumiya/voice/AudioStreamVolumeObserver;
.super Ljava/lang/Object;
.source "AudioStreamVolumeObserver.java"


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Lcom/lumiyaviewer/lumiya/voice/AudioStreamVolumeObserver$AudioStreamVolumeContentObserver;,
        Lcom/lumiyaviewer/lumiya/voice/AudioStreamVolumeObserver$OnAudioStreamVolumeChangedListener;
    }
.end annotation


# instance fields
.field private mAudioStreamVolumeContentObserver:Lcom/lumiyaviewer/lumiya/voice/AudioStreamVolumeObserver$AudioStreamVolumeContentObserver;

.field private final mContext:Landroid/content/Context;


# direct methods
.method public constructor <init>(Landroid/content/Context;)V
    .locals 0
    .param p1, "context"    # Landroid/content/Context;
        .annotation runtime Ljavax/annotation/Nonnull;
        .end annotation
    .end param

    .prologue
    .line 53
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 54
    iput-object p1, p0, Lcom/lumiyaviewer/lumiya/voice/AudioStreamVolumeObserver;->mContext:Landroid/content/Context;

    .line 55
    return-void
.end method


# virtual methods
.method public start([ILcom/lumiyaviewer/lumiya/voice/AudioStreamVolumeObserver$OnAudioStreamVolumeChangedListener;)V
    .locals 6
    .param p1, "audioStreamTypes"    # [I
    .param p2, "listener"    # Lcom/lumiyaviewer/lumiya/voice/AudioStreamVolumeObserver$OnAudioStreamVolumeChangedListener;
        .annotation runtime Ljavax/annotation/Nonnull;
        .end annotation
    .end param

    .prologue
    .line 58
    invoke-virtual {p0}, Lcom/lumiyaviewer/lumiya/voice/AudioStreamVolumeObserver;->stop()V

    .line 60
    new-instance v1, Landroid/os/Handler;

    invoke-direct {v1}, Landroid/os/Handler;-><init>()V

    .line 61
    .local v1, "handler":Landroid/os/Handler;
    iget-object v2, p0, Lcom/lumiyaviewer/lumiya/voice/AudioStreamVolumeObserver;->mContext:Landroid/content/Context;

    const-string v3, "audio"

    invoke-virtual {v2, v3}, Landroid/content/Context;->getSystemService(Ljava/lang/String;)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Landroid/media/AudioManager;

    .line 63
    .local v0, "audioManager":Landroid/media/AudioManager;
    new-instance v2, Lcom/lumiyaviewer/lumiya/voice/AudioStreamVolumeObserver$AudioStreamVolumeContentObserver;

    invoke-direct {v2, v1, v0, p1, p2}, Lcom/lumiyaviewer/lumiya/voice/AudioStreamVolumeObserver$AudioStreamVolumeContentObserver;-><init>(Landroid/os/Handler;Landroid/media/AudioManager;[ILcom/lumiyaviewer/lumiya/voice/AudioStreamVolumeObserver$OnAudioStreamVolumeChangedListener;)V

    iput-object v2, p0, Lcom/lumiyaviewer/lumiya/voice/AudioStreamVolumeObserver;->mAudioStreamVolumeContentObserver:Lcom/lumiyaviewer/lumiya/voice/AudioStreamVolumeObserver$AudioStreamVolumeContentObserver;

    .line 65
    iget-object v2, p0, Lcom/lumiyaviewer/lumiya/voice/AudioStreamVolumeObserver;->mContext:Landroid/content/Context;

    invoke-virtual {v2}, Landroid/content/Context;->getContentResolver()Landroid/content/ContentResolver;

    move-result-object v2

    sget-object v3, Landroid/provider/Settings$System;->CONTENT_URI:Landroid/net/Uri;

    const/4 v4, 0x1

    iget-object v5, p0, Lcom/lumiyaviewer/lumiya/voice/AudioStreamVolumeObserver;->mAudioStreamVolumeContentObserver:Lcom/lumiyaviewer/lumiya/voice/AudioStreamVolumeObserver$AudioStreamVolumeContentObserver;

    .line 66
    invoke-virtual {v2, v3, v4, v5}, Landroid/content/ContentResolver;->registerContentObserver(Landroid/net/Uri;ZLandroid/database/ContentObserver;)V

    .line 67
    return-void
.end method

.method public stop()V
    .locals 2

    .prologue
    .line 70
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/AudioStreamVolumeObserver;->mAudioStreamVolumeContentObserver:Lcom/lumiyaviewer/lumiya/voice/AudioStreamVolumeObserver$AudioStreamVolumeContentObserver;

    if-nez v0, :cond_0

    .line 77
    :goto_0
    return-void

    .line 74
    :cond_0
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/AudioStreamVolumeObserver;->mContext:Landroid/content/Context;

    invoke-virtual {v0}, Landroid/content/Context;->getContentResolver()Landroid/content/ContentResolver;

    move-result-object v0

    iget-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/AudioStreamVolumeObserver;->mAudioStreamVolumeContentObserver:Lcom/lumiyaviewer/lumiya/voice/AudioStreamVolumeObserver$AudioStreamVolumeContentObserver;

    .line 75
    invoke-virtual {v0, v1}, Landroid/content/ContentResolver;->unregisterContentObserver(Landroid/database/ContentObserver;)V

    .line 76
    const/4 v0, 0x0

    iput-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/AudioStreamVolumeObserver;->mAudioStreamVolumeContentObserver:Lcom/lumiyaviewer/lumiya/voice/AudioStreamVolumeObserver$AudioStreamVolumeContentObserver;

    goto :goto_0
.end method
