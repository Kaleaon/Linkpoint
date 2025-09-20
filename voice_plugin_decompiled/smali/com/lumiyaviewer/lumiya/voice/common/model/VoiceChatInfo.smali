.class public Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;
.super Ljava/lang/Object;
.source "VoiceChatInfo.java"


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo$VoiceChatState;
    }
.end annotation


# static fields
.field private static final emptyChatState:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;

.field private static final interner:Lcom/google/common/collect/Interner;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Lcom/google/common/collect/Interner",
            "<",
            "Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;",
            ">;"
        }
    .end annotation
.end field


# instance fields
.field public final activeSpeakerID:Ljava/util/UUID;
    .annotation runtime Ljavax/annotation/Nullable;
    .end annotation
.end field

.field public final isConference:Z

.field public final localMicActive:Z

.field public final numActiveSpeakers:I

.field public final previousState:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo$VoiceChatState;
    .annotation runtime Ljavax/annotation/Nonnull;
    .end annotation
.end field

.field public final state:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo$VoiceChatState;
    .annotation runtime Ljavax/annotation/Nonnull;
    .end annotation
.end field


# direct methods
.method static constructor <clinit>()V
    .locals 8

    .prologue
    const/4 v3, 0x0

    .line 27
    invoke-static {}, Lcom/google/common/collect/Interners;->newWeakInterner()Lcom/google/common/collect/Interner;

    move-result-object v0

    sput-object v0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;->interner:Lcom/google/common/collect/Interner;

    .line 28
    sget-object v7, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;->interner:Lcom/google/common/collect/Interner;

    new-instance v0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;

    sget-object v1, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo$VoiceChatState;->None:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo$VoiceChatState;

    sget-object v2, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo$VoiceChatState;->None:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo$VoiceChatState;

    const/4 v4, 0x0

    move v5, v3

    move v6, v3

    invoke-direct/range {v0 .. v6}, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;-><init>(Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo$VoiceChatState;Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo$VoiceChatState;ILjava/util/UUID;ZZ)V

    invoke-interface {v7, v0}, Lcom/google/common/collect/Interner;->intern(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;

    sput-object v0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;->emptyChatState:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;

    return-void
.end method

.method private constructor <init>(Landroid/os/Bundle;)V
    .locals 2
    .param p1, "bundle"    # Landroid/os/Bundle;

    .prologue
    .line 39
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 40
    const-string v1, "state"

    invoke-virtual {p1, v1}, Landroid/os/Bundle;->getString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v1

    invoke-static {v1}, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo$VoiceChatState;->valueOf(Ljava/lang/String;)Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo$VoiceChatState;

    move-result-object v1

    iput-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;->state:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo$VoiceChatState;

    .line 41
    const-string v1, "previousState"

    invoke-virtual {p1, v1}, Landroid/os/Bundle;->getString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v1

    invoke-static {v1}, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo$VoiceChatState;->valueOf(Ljava/lang/String;)Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo$VoiceChatState;

    move-result-object v1

    iput-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;->previousState:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo$VoiceChatState;

    .line 42
    const-string v1, "numActiveSpeakers"

    invoke-virtual {p1, v1}, Landroid/os/Bundle;->getInt(Ljava/lang/String;)I

    move-result v1

    iput v1, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;->numActiveSpeakers:I

    .line 43
    const-string v1, "activeSpeakerID"

    invoke-virtual {p1, v1}, Landroid/os/Bundle;->getString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v0

    .line 44
    .local v0, "activeSpeakerID":Ljava/lang/String;
    if-eqz v0, :cond_0

    invoke-static {v0}, Ljava/util/UUID;->fromString(Ljava/lang/String;)Ljava/util/UUID;

    move-result-object v1

    :goto_0
    iput-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;->activeSpeakerID:Ljava/util/UUID;

    .line 45
    const-string v1, "isConference"

    invoke-virtual {p1, v1}, Landroid/os/Bundle;->getBoolean(Ljava/lang/String;)Z

    move-result v1

    iput-boolean v1, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;->isConference:Z

    .line 46
    const-string v1, "localMicActive"

    invoke-virtual {p1, v1}, Landroid/os/Bundle;->getBoolean(Ljava/lang/String;)Z

    move-result v1

    iput-boolean v1, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;->localMicActive:Z

    .line 47
    return-void

    .line 44
    :cond_0
    const/4 v1, 0x0

    goto :goto_0
.end method

.method private constructor <init>(Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo$VoiceChatState;Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo$VoiceChatState;ILjava/util/UUID;ZZ)V
    .locals 0
    .param p1, "state"    # Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo$VoiceChatState;
        .annotation runtime Ljavax/annotation/Nonnull;
        .end annotation
    .end param
    .param p2, "previousState"    # Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo$VoiceChatState;
        .annotation runtime Ljavax/annotation/Nonnull;
        .end annotation
    .end param
    .param p3, "numActiveSpeakers"    # I
    .param p4, "activeSpeakerID"    # Ljava/util/UUID;
        .annotation runtime Ljavax/annotation/Nullable;
        .end annotation
    .end param
    .param p5, "isConference"    # Z
    .param p6, "localMicActive"    # Z

    .prologue
    .line 30
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 31
    iput-object p1, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;->state:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo$VoiceChatState;

    .line 32
    iput-object p2, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;->previousState:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo$VoiceChatState;

    .line 33
    iput p3, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;->numActiveSpeakers:I

    .line 34
    iput-object p4, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;->activeSpeakerID:Ljava/util/UUID;

    .line 35
    iput-boolean p5, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;->isConference:Z

    .line 36
    iput-boolean p6, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;->localMicActive:Z

    .line 37
    return-void
.end method

.method public static create(Landroid/os/Bundle;)Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;
    .locals 2
    .param p0, "bundle"    # Landroid/os/Bundle;
    .annotation runtime Ljavax/annotation/Nonnull;
    .end annotation

    .prologue
    .line 54
    sget-object v0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;->interner:Lcom/google/common/collect/Interner;

    new-instance v1, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;

    invoke-direct {v1, p0}, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;-><init>(Landroid/os/Bundle;)V

    invoke-interface {v0, v1}, Lcom/google/common/collect/Interner;->intern(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;

    return-object v0
.end method

.method public static create(Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo$VoiceChatState;Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo$VoiceChatState;ILjava/util/UUID;ZZ)Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;
    .locals 8
    .param p0, "state"    # Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo$VoiceChatState;
        .annotation runtime Ljavax/annotation/Nonnull;
        .end annotation
    .end param
    .param p1, "previousState"    # Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo$VoiceChatState;
        .annotation runtime Ljavax/annotation/Nonnull;
        .end annotation
    .end param
    .param p2, "numActiveSpeakers"    # I
    .param p3, "activeSpeakerID"    # Ljava/util/UUID;
        .annotation runtime Ljavax/annotation/Nullable;
        .end annotation
    .end param
    .param p4, "isConference"    # Z
    .param p5, "localMicActive"    # Z
    .annotation runtime Ljavax/annotation/Nonnull;
    .end annotation

    .prologue
    .line 50
    sget-object v7, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;->interner:Lcom/google/common/collect/Interner;

    new-instance v0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;

    move-object v1, p0

    move-object v2, p1

    move v3, p2

    move-object v4, p3

    move v5, p4

    move v6, p5

    invoke-direct/range {v0 .. v6}, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;-><init>(Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo$VoiceChatState;Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo$VoiceChatState;ILjava/util/UUID;ZZ)V

    invoke-interface {v7, v0}, Lcom/google/common/collect/Interner;->intern(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;

    return-object v0
.end method

.method public static empty()Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;
    .locals 1
    .annotation runtime Ljavax/annotation/Nonnull;
    .end annotation

    .prologue
    .line 58
    sget-object v0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;->emptyChatState:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;

    return-object v0
.end method


# virtual methods
.method public equals(Ljava/lang/Object;)Z
    .locals 5
    .param p1, "o"    # Ljava/lang/Object;

    .prologue
    const/4 v1, 0x1

    const/4 v2, 0x0

    .line 74
    if-ne p0, p1, :cond_1

    move v2, v1

    .line 84
    :cond_0
    :goto_0
    return v2

    .line 75
    :cond_1
    if-eqz p1, :cond_0

    invoke-virtual {p0}, Ljava/lang/Object;->getClass()Ljava/lang/Class;

    move-result-object v3

    invoke-virtual {p1}, Ljava/lang/Object;->getClass()Ljava/lang/Class;

    move-result-object v4

    if-ne v3, v4, :cond_0

    move-object v0, p1

    .line 77
    check-cast v0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;

    .line 79
    .local v0, "that":Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;
    iget v3, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;->numActiveSpeakers:I

    iget v4, v0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;->numActiveSpeakers:I

    if-ne v3, v4, :cond_0

    .line 80
    iget-boolean v3, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;->isConference:Z

    iget-boolean v4, v0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;->isConference:Z

    if-ne v3, v4, :cond_0

    .line 81
    iget-boolean v3, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;->localMicActive:Z

    iget-boolean v4, v0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;->localMicActive:Z

    if-ne v3, v4, :cond_0

    .line 82
    iget-object v3, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;->state:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo$VoiceChatState;

    iget-object v4, v0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;->state:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo$VoiceChatState;

    if-ne v3, v4, :cond_0

    .line 83
    iget-object v3, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;->previousState:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo$VoiceChatState;

    iget-object v4, v0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;->previousState:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo$VoiceChatState;

    if-ne v3, v4, :cond_0

    .line 84
    iget-object v3, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;->activeSpeakerID:Ljava/util/UUID;

    if-eqz v3, :cond_3

    iget-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;->activeSpeakerID:Ljava/util/UUID;

    iget-object v2, v0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;->activeSpeakerID:Ljava/util/UUID;

    invoke-virtual {v1, v2}, Ljava/util/UUID;->equals(Ljava/lang/Object;)Z

    move-result v1

    :cond_2
    :goto_1
    move v2, v1

    goto :goto_0

    :cond_3
    iget-object v3, v0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;->activeSpeakerID:Ljava/util/UUID;

    if-eqz v3, :cond_2

    move v1, v2

    goto :goto_1
.end method

.method public hashCode()I
    .locals 5

    .prologue
    const/4 v3, 0x1

    const/4 v2, 0x0

    .line 89
    iget-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;->state:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo$VoiceChatState;

    invoke-virtual {v1}, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo$VoiceChatState;->hashCode()I

    move-result v0

    .line 90
    .local v0, "result":I
    mul-int/lit8 v1, v0, 0x1f

    iget-object v4, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;->previousState:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo$VoiceChatState;

    invoke-virtual {v4}, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo$VoiceChatState;->hashCode()I

    move-result v4

    add-int v0, v1, v4

    .line 91
    mul-int/lit8 v1, v0, 0x1f

    iget v4, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;->numActiveSpeakers:I

    add-int v0, v1, v4

    .line 92
    mul-int/lit8 v4, v0, 0x1f

    iget-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;->activeSpeakerID:Ljava/util/UUID;

    if-eqz v1, :cond_0

    iget-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;->activeSpeakerID:Ljava/util/UUID;

    invoke-virtual {v1}, Ljava/util/UUID;->hashCode()I

    move-result v1

    :goto_0
    add-int v0, v4, v1

    .line 93
    mul-int/lit8 v4, v0, 0x1f

    iget-boolean v1, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;->isConference:Z

    if-eqz v1, :cond_1

    move v1, v3

    :goto_1
    add-int v0, v4, v1

    .line 94
    mul-int/lit8 v1, v0, 0x1f

    iget-boolean v4, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;->localMicActive:Z

    if-eqz v4, :cond_2

    :goto_2
    add-int v0, v1, v3

    .line 95
    return v0

    :cond_0
    move v1, v2

    .line 92
    goto :goto_0

    :cond_1
    move v1, v2

    .line 93
    goto :goto_1

    :cond_2
    move v3, v2

    .line 94
    goto :goto_2
.end method

.method public toBundle()Landroid/os/Bundle;
    .locals 3

    .prologue
    .line 62
    new-instance v0, Landroid/os/Bundle;

    invoke-direct {v0}, Landroid/os/Bundle;-><init>()V

    .line 63
    .local v0, "bundle":Landroid/os/Bundle;
    const-string v1, "state"

    iget-object v2, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;->state:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo$VoiceChatState;

    invoke-virtual {v2}, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo$VoiceChatState;->toString()Ljava/lang/String;

    move-result-object v2

    invoke-virtual {v0, v1, v2}, Landroid/os/Bundle;->putString(Ljava/lang/String;Ljava/lang/String;)V

    .line 64
    const-string v1, "previousState"

    iget-object v2, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;->previousState:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo$VoiceChatState;

    invoke-virtual {v2}, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo$VoiceChatState;->toString()Ljava/lang/String;

    move-result-object v2

    invoke-virtual {v0, v1, v2}, Landroid/os/Bundle;->putString(Ljava/lang/String;Ljava/lang/String;)V

    .line 65
    const-string v1, "numActiveSpeakers"

    iget v2, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;->numActiveSpeakers:I

    invoke-virtual {v0, v1, v2}, Landroid/os/Bundle;->putInt(Ljava/lang/String;I)V

    .line 66
    const-string v2, "activeSpeakerID"

    iget-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;->activeSpeakerID:Ljava/util/UUID;

    if-eqz v1, :cond_0

    iget-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;->activeSpeakerID:Ljava/util/UUID;

    invoke-virtual {v1}, Ljava/util/UUID;->toString()Ljava/lang/String;

    move-result-object v1

    :goto_0
    invoke-virtual {v0, v2, v1}, Landroid/os/Bundle;->putString(Ljava/lang/String;Ljava/lang/String;)V

    .line 67
    const-string v1, "isConference"

    iget-boolean v2, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;->isConference:Z

    invoke-virtual {v0, v1, v2}, Landroid/os/Bundle;->putBoolean(Ljava/lang/String;Z)V

    .line 68
    const-string v1, "localMicActive"

    iget-boolean v2, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;->localMicActive:Z

    invoke-virtual {v0, v1, v2}, Landroid/os/Bundle;->putBoolean(Ljava/lang/String;Z)V

    .line 69
    return-object v0

    .line 66
    :cond_0
    const/4 v1, 0x0

    goto :goto_0
.end method

.method public toString()Ljava/lang/String;
    .locals 2

    .prologue
    .line 100
    new-instance v0, Ljava/lang/StringBuilder;

    invoke-direct {v0}, Ljava/lang/StringBuilder;-><init>()V

    const-string v1, "VoiceChatInfo{state="

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v0

    iget-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;->state:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo$VoiceChatState;

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    move-result-object v0

    const-string v1, ", previousState="

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v0

    iget-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;->previousState:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo$VoiceChatState;

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    move-result-object v0

    const-string v1, ", numActiveSpeakers="

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v0

    iget v1, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;->numActiveSpeakers:I

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    move-result-object v0

    const-string v1, ", activeSpeakerID="

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v0

    iget-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;->activeSpeakerID:Ljava/util/UUID;

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    move-result-object v0

    const-string v1, ", isConference="

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v0

    iget-boolean v1, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;->isConference:Z

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Z)Ljava/lang/StringBuilder;

    move-result-object v0

    const-string v1, ", localMicActive="

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v0

    iget-boolean v1, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;->localMicActive:Z

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Z)Ljava/lang/StringBuilder;

    move-result-object v0

    const/16 v1, 0x7d

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(C)Ljava/lang/StringBuilder;

    move-result-object v0

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method
