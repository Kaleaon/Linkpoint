.class Lcom/lumiyaviewer/lumiya/voice/AudioStreamVolumeObserver$AudioStreamVolumeContentObserver;
.super Landroid/database/ContentObserver;
.source "AudioStreamVolumeObserver.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/lumiyaviewer/lumiya/voice/AudioStreamVolumeObserver;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0xa
    name = "AudioStreamVolumeContentObserver"
.end annotation


# instance fields
.field private final mAudioManager:Landroid/media/AudioManager;

.field private final mAudioStreamTypes:[I

.field private final mLastVolumes:[I

.field private final mListener:Lcom/lumiyaviewer/lumiya/voice/AudioStreamVolumeObserver$OnAudioStreamVolumeChangedListener;


# direct methods
.method constructor <init>(Landroid/os/Handler;Landroid/media/AudioManager;[ILcom/lumiyaviewer/lumiya/voice/AudioStreamVolumeObserver$OnAudioStreamVolumeChangedListener;)V
    .locals 4
    .param p1, "handler"    # Landroid/os/Handler;
        .annotation runtime Ljavax/annotation/Nonnull;
        .end annotation
    .end param
    .param p2, "audioManager"    # Landroid/media/AudioManager;
        .annotation runtime Ljavax/annotation/Nonnull;
        .end annotation
    .end param
    .param p3, "audioStreamTypes"    # [I
    .param p4, "listener"    # Lcom/lumiyaviewer/lumiya/voice/AudioStreamVolumeObserver$OnAudioStreamVolumeChangedListener;
        .annotation runtime Ljavax/annotation/Nonnull;
        .end annotation
    .end param

    .prologue
    .line 25
    invoke-direct {p0, p1}, Landroid/database/ContentObserver;-><init>(Landroid/os/Handler;)V

    .line 27
    iput-object p2, p0, Lcom/lumiyaviewer/lumiya/voice/AudioStreamVolumeObserver$AudioStreamVolumeContentObserver;->mAudioManager:Landroid/media/AudioManager;

    .line 28
    iput-object p3, p0, Lcom/lumiyaviewer/lumiya/voice/AudioStreamVolumeObserver$AudioStreamVolumeContentObserver;->mAudioStreamTypes:[I

    .line 29
    iput-object p4, p0, Lcom/lumiyaviewer/lumiya/voice/AudioStreamVolumeObserver$AudioStreamVolumeContentObserver;->mListener:Lcom/lumiyaviewer/lumiya/voice/AudioStreamVolumeObserver$OnAudioStreamVolumeChangedListener;

    .line 31
    iget-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/AudioStreamVolumeObserver$AudioStreamVolumeContentObserver;->mAudioStreamTypes:[I

    array-length v1, v1

    new-array v1, v1, [I

    iput-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/AudioStreamVolumeObserver$AudioStreamVolumeContentObserver;->mLastVolumes:[I

    .line 32
    const/4 v0, 0x0

    .local v0, "i":I
    :goto_0
    iget-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/AudioStreamVolumeObserver$AudioStreamVolumeContentObserver;->mAudioStreamTypes:[I

    array-length v1, v1

    if-ge v0, v1, :cond_0

    .line 33
    iget-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/AudioStreamVolumeObserver$AudioStreamVolumeContentObserver;->mLastVolumes:[I

    iget-object v2, p0, Lcom/lumiyaviewer/lumiya/voice/AudioStreamVolumeObserver$AudioStreamVolumeContentObserver;->mAudioManager:Landroid/media/AudioManager;

    iget-object v3, p0, Lcom/lumiyaviewer/lumiya/voice/AudioStreamVolumeObserver$AudioStreamVolumeContentObserver;->mAudioStreamTypes:[I

    aget v3, v3, v0

    invoke-virtual {v2, v3}, Landroid/media/AudioManager;->getStreamVolume(I)I

    move-result v2

    aput v2, v1, v0

    .line 32
    add-int/lit8 v0, v0, 0x1

    goto :goto_0

    .line 35
    :cond_0
    return-void
.end method


# virtual methods
.method public onChange(Z)V
    .locals 4
    .param p1, "selfChange"    # Z

    .prologue
    .line 39
    const/4 v1, 0x0

    .local v1, "i":I
    :goto_0
    iget-object v2, p0, Lcom/lumiyaviewer/lumiya/voice/AudioStreamVolumeObserver$AudioStreamVolumeContentObserver;->mAudioStreamTypes:[I

    array-length v2, v2

    if-ge v1, v2, :cond_1

    .line 40
    iget-object v2, p0, Lcom/lumiyaviewer/lumiya/voice/AudioStreamVolumeObserver$AudioStreamVolumeContentObserver;->mAudioManager:Landroid/media/AudioManager;

    iget-object v3, p0, Lcom/lumiyaviewer/lumiya/voice/AudioStreamVolumeObserver$AudioStreamVolumeContentObserver;->mAudioStreamTypes:[I

    aget v3, v3, v1

    invoke-virtual {v2, v3}, Landroid/media/AudioManager;->getStreamVolume(I)I

    move-result v0

    .line 41
    .local v0, "currentVolume":I
    iget-object v2, p0, Lcom/lumiyaviewer/lumiya/voice/AudioStreamVolumeObserver$AudioStreamVolumeContentObserver;->mLastVolumes:[I

    aget v2, v2, v1

    if-eq v0, v2, :cond_0

    .line 42
    iget-object v2, p0, Lcom/lumiyaviewer/lumiya/voice/AudioStreamVolumeObserver$AudioStreamVolumeContentObserver;->mLastVolumes:[I

    aput v0, v2, v1

    .line 43
    iget-object v2, p0, Lcom/lumiyaviewer/lumiya/voice/AudioStreamVolumeObserver$AudioStreamVolumeContentObserver;->mListener:Lcom/lumiyaviewer/lumiya/voice/AudioStreamVolumeObserver$OnAudioStreamVolumeChangedListener;

    iget-object v3, p0, Lcom/lumiyaviewer/lumiya/voice/AudioStreamVolumeObserver$AudioStreamVolumeContentObserver;->mAudioStreamTypes:[I

    aget v3, v3, v1

    invoke-interface {v2, v3, v0}, Lcom/lumiyaviewer/lumiya/voice/AudioStreamVolumeObserver$OnAudioStreamVolumeChangedListener;->onAudioStreamVolumeChanged(II)V

    .line 39
    :cond_0
    add-int/lit8 v1, v1, 0x1

    goto :goto_0

    .line 46
    .end local v0    # "currentVolume":I
    :cond_1
    return-void
.end method
