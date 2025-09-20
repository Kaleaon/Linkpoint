.class Lcom/lumiyaviewer/lumiya/voice/VoiceService$2;
.super Ljava/lang/Object;
.source "VoiceService.java"

# interfaces
.implements Lcom/lumiyaviewer/lumiya/voice/AudioStreamVolumeObserver$OnAudioStreamVolumeChangedListener;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/lumiyaviewer/lumiya/voice/VoiceService;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/lumiyaviewer/lumiya/voice/VoiceService;


# direct methods
.method constructor <init>(Lcom/lumiyaviewer/lumiya/voice/VoiceService;)V
    .locals 0
    .param p1, "this$0"    # Lcom/lumiyaviewer/lumiya/voice/VoiceService;

    .prologue
    .line 333
    iput-object p1, p0, Lcom/lumiyaviewer/lumiya/voice/VoiceService$2;->this$0:Lcom/lumiyaviewer/lumiya/voice/VoiceService;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public onAudioStreamVolumeChanged(II)V
    .locals 4
    .param p1, "audioStreamType"    # I
    .param p2, "volume"    # I

    .prologue
    .line 336
    const-string v0, "Voice: audio volume changed: %d"

    const/4 v1, 0x1

    new-array v1, v1, [Ljava/lang/Object;

    const/4 v2, 0x0

    invoke-static {p2}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v3

    aput-object v3, v1, v2

    invoke-static {v0, v1}, Lcom/lumiyaviewer/lumiya/voice/Debug;->Printf(Ljava/lang/String;[Ljava/lang/Object;)V

    .line 337
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VoiceService$2;->this$0:Lcom/lumiyaviewer/lumiya/voice/VoiceService;

    invoke-virtual {v0}, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->updateAudioProperties()V

    .line 338
    return-void
.end method
