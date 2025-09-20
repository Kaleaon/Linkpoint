.class public Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DPosition;
.super Ljava/lang/Object;
.source "Voice3DPosition.java"


# annotations
.annotation build Ljavax/annotation/concurrent/Immutable;
.end annotation


# instance fields
.field public final atOrientation:Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;
    .annotation runtime Ljavax/annotation/Nonnull;
    .end annotation
.end field

.field public final leftOrientation:Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;
    .annotation runtime Ljavax/annotation/Nonnull;
    .end annotation
.end field

.field public final position:Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;
    .annotation runtime Ljavax/annotation/Nonnull;
    .end annotation
.end field

.field public final upOrientation:Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;
    .annotation runtime Ljavax/annotation/Nonnull;
    .end annotation
.end field

.field public final velocity:Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;
    .annotation runtime Ljavax/annotation/Nonnull;
    .end annotation
.end field


# direct methods
.method public constructor <init>(Landroid/os/Bundle;)V
    .locals 2
    .param p1, "bundle"    # Landroid/os/Bundle;

    .prologue
    .line 25
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 26
    new-instance v0, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;

    const-string v1, "position"

    invoke-virtual {p1, v1}, Landroid/os/Bundle;->getBundle(Ljava/lang/String;)Landroid/os/Bundle;

    move-result-object v1

    invoke-direct {v0, v1}, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;-><init>(Landroid/os/Bundle;)V

    iput-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DPosition;->position:Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;

    .line 27
    new-instance v0, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;

    const-string v1, "velocity"

    invoke-virtual {p1, v1}, Landroid/os/Bundle;->getBundle(Ljava/lang/String;)Landroid/os/Bundle;

    move-result-object v1

    invoke-direct {v0, v1}, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;-><init>(Landroid/os/Bundle;)V

    iput-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DPosition;->velocity:Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;

    .line 28
    new-instance v0, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;

    const-string v1, "atOrientation"

    invoke-virtual {p1, v1}, Landroid/os/Bundle;->getBundle(Ljava/lang/String;)Landroid/os/Bundle;

    move-result-object v1

    invoke-direct {v0, v1}, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;-><init>(Landroid/os/Bundle;)V

    iput-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DPosition;->atOrientation:Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;

    .line 29
    new-instance v0, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;

    const-string v1, "upOrientation"

    invoke-virtual {p1, v1}, Landroid/os/Bundle;->getBundle(Ljava/lang/String;)Landroid/os/Bundle;

    move-result-object v1

    invoke-direct {v0, v1}, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;-><init>(Landroid/os/Bundle;)V

    iput-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DPosition;->upOrientation:Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;

    .line 30
    new-instance v0, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;

    const-string v1, "leftOrientation"

    invoke-virtual {p1, v1}, Landroid/os/Bundle;->getBundle(Ljava/lang/String;)Landroid/os/Bundle;

    move-result-object v1

    invoke-direct {v0, v1}, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;-><init>(Landroid/os/Bundle;)V

    iput-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DPosition;->leftOrientation:Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;

    .line 31
    return-void
.end method

.method public constructor <init>(Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;)V
    .locals 0
    .param p1, "position"    # Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;
        .annotation runtime Ljavax/annotation/Nonnull;
        .end annotation
    .end param
    .param p2, "velocity"    # Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;
        .annotation runtime Ljavax/annotation/Nonnull;
        .end annotation
    .end param
    .param p3, "atOrientation"    # Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;
        .annotation runtime Ljavax/annotation/Nonnull;
        .end annotation
    .end param
    .param p4, "upOrientation"    # Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;
        .annotation runtime Ljavax/annotation/Nonnull;
        .end annotation
    .end param
    .param p5, "leftOrientation"    # Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;
        .annotation runtime Ljavax/annotation/Nonnull;
        .end annotation
    .end param

    .prologue
    .line 17
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 18
    iput-object p1, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DPosition;->position:Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;

    .line 19
    iput-object p2, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DPosition;->velocity:Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;

    .line 20
    iput-object p3, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DPosition;->atOrientation:Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;

    .line 21
    iput-object p4, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DPosition;->upOrientation:Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;

    .line 22
    iput-object p5, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DPosition;->leftOrientation:Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;

    .line 23
    return-void
.end method


# virtual methods
.method public toBundle()Landroid/os/Bundle;
    .locals 3

    .prologue
    .line 34
    new-instance v0, Landroid/os/Bundle;

    invoke-direct {v0}, Landroid/os/Bundle;-><init>()V

    .line 35
    .local v0, "bundle":Landroid/os/Bundle;
    const-string v1, "position"

    iget-object v2, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DPosition;->position:Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;

    invoke-virtual {v2}, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;->toBundle()Landroid/os/Bundle;

    move-result-object v2

    invoke-virtual {v0, v1, v2}, Landroid/os/Bundle;->putBundle(Ljava/lang/String;Landroid/os/Bundle;)V

    .line 36
    const-string v1, "velocity"

    iget-object v2, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DPosition;->velocity:Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;

    invoke-virtual {v2}, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;->toBundle()Landroid/os/Bundle;

    move-result-object v2

    invoke-virtual {v0, v1, v2}, Landroid/os/Bundle;->putBundle(Ljava/lang/String;Landroid/os/Bundle;)V

    .line 37
    const-string v1, "atOrientation"

    iget-object v2, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DPosition;->atOrientation:Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;

    invoke-virtual {v2}, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;->toBundle()Landroid/os/Bundle;

    move-result-object v2

    invoke-virtual {v0, v1, v2}, Landroid/os/Bundle;->putBundle(Ljava/lang/String;Landroid/os/Bundle;)V

    .line 38
    const-string v1, "upOrientation"

    iget-object v2, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DPosition;->upOrientation:Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;

    invoke-virtual {v2}, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;->toBundle()Landroid/os/Bundle;

    move-result-object v2

    invoke-virtual {v0, v1, v2}, Landroid/os/Bundle;->putBundle(Ljava/lang/String;Landroid/os/Bundle;)V

    .line 39
    const-string v1, "leftOrientation"

    iget-object v2, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DPosition;->leftOrientation:Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;

    invoke-virtual {v2}, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;->toBundle()Landroid/os/Bundle;

    move-result-object v2

    invoke-virtual {v0, v1, v2}, Landroid/os/Bundle;->putBundle(Ljava/lang/String;Landroid/os/Bundle;)V

    .line 40
    return-object v0
.end method

.method public toString()Ljava/lang/String;
    .locals 4

    .prologue
    .line 45
    const-string v0, "(pos %s vel %s at %s up %s left %s)"

    const/4 v1, 0x5

    new-array v1, v1, [Ljava/lang/Object;

    const/4 v2, 0x0

    iget-object v3, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DPosition;->position:Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;

    .line 46
    invoke-virtual {v3}, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;->toString()Ljava/lang/String;

    move-result-object v3

    aput-object v3, v1, v2

    const/4 v2, 0x1

    iget-object v3, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DPosition;->velocity:Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;

    .line 47
    invoke-virtual {v3}, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;->toString()Ljava/lang/String;

    move-result-object v3

    aput-object v3, v1, v2

    const/4 v2, 0x2

    iget-object v3, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DPosition;->atOrientation:Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;

    .line 48
    invoke-virtual {v3}, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;->toString()Ljava/lang/String;

    move-result-object v3

    aput-object v3, v1, v2

    const/4 v2, 0x3

    iget-object v3, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DPosition;->upOrientation:Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;

    .line 49
    invoke-virtual {v3}, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;->toString()Ljava/lang/String;

    move-result-object v3

    aput-object v3, v1, v2

    const/4 v2, 0x4

    iget-object v3, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DPosition;->leftOrientation:Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;

    .line 50
    invoke-virtual {v3}, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;->toString()Ljava/lang/String;

    move-result-object v3

    aput-object v3, v1, v2

    .line 45
    invoke-static {v0, v1}, Ljava/lang/String;->format(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method
