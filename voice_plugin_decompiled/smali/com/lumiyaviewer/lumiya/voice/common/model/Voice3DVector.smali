.class public Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;
.super Ljava/lang/Object;
.source "Voice3DVector.java"


# annotations
.annotation build Ljavax/annotation/concurrent/Immutable;
.end annotation


# instance fields
.field public final x:F

.field public final y:F

.field public final z:F


# direct methods
.method public constructor <init>(FFF)V
    .locals 0
    .param p1, "x"    # F
    .param p2, "y"    # F
    .param p3, "z"    # F

    .prologue
    .line 15
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 16
    iput p1, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;->x:F

    .line 17
    iput p2, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;->y:F

    .line 18
    iput p3, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;->z:F

    .line 19
    return-void
.end method

.method public constructor <init>(Landroid/os/Bundle;)V
    .locals 1
    .param p1, "bundle"    # Landroid/os/Bundle;

    .prologue
    .line 25
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 26
    const-string v0, "x"

    invoke-virtual {p1, v0}, Landroid/os/Bundle;->getFloat(Ljava/lang/String;)F

    move-result v0

    iput v0, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;->x:F

    .line 27
    const-string v0, "y"

    invoke-virtual {p1, v0}, Landroid/os/Bundle;->getFloat(Ljava/lang/String;)F

    move-result v0

    iput v0, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;->y:F

    .line 28
    const-string v0, "z"

    invoke-virtual {p1, v0}, Landroid/os/Bundle;->getFloat(Ljava/lang/String;)F

    move-result v0

    iput v0, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;->z:F

    .line 29
    return-void
.end method

.method public static fromLLCoords(FFF)Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;
    .locals 2
    .param p0, "x"    # F
    .param p1, "y"    # F
    .param p2, "z"    # F

    .prologue
    .line 22
    new-instance v0, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;

    neg-float v1, p1

    invoke-direct {v0, p0, p2, v1}, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;-><init>(FFF)V

    return-object v0
.end method


# virtual methods
.method public toBundle()Landroid/os/Bundle;
    .locals 3

    .prologue
    .line 32
    new-instance v0, Landroid/os/Bundle;

    invoke-direct {v0}, Landroid/os/Bundle;-><init>()V

    .line 33
    .local v0, "bundle":Landroid/os/Bundle;
    const-string v1, "x"

    iget v2, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;->x:F

    invoke-virtual {v0, v1, v2}, Landroid/os/Bundle;->putFloat(Ljava/lang/String;F)V

    .line 34
    const-string v1, "y"

    iget v2, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;->y:F

    invoke-virtual {v0, v1, v2}, Landroid/os/Bundle;->putFloat(Ljava/lang/String;F)V

    .line 35
    const-string v1, "z"

    iget v2, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;->z:F

    invoke-virtual {v0, v1, v2}, Landroid/os/Bundle;->putFloat(Ljava/lang/String;F)V

    .line 36
    return-object v0
.end method

.method public toString()Ljava/lang/String;
    .locals 4
    .annotation build Landroid/annotation/SuppressLint;
        value = {
            "DefaultLocale"
        }
    .end annotation

    .prologue
    .line 42
    const-string v0, "(%.2f, %.2f, %.2f)"

    const/4 v1, 0x3

    new-array v1, v1, [Ljava/lang/Object;

    const/4 v2, 0x0

    iget v3, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;->x:F

    invoke-static {v3}, Ljava/lang/Float;->valueOf(F)Ljava/lang/Float;

    move-result-object v3

    aput-object v3, v1, v2

    const/4 v2, 0x1

    iget v3, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;->y:F

    invoke-static {v3}, Ljava/lang/Float;->valueOf(F)Ljava/lang/Float;

    move-result-object v3

    aput-object v3, v1, v2

    const/4 v2, 0x2

    iget v3, p0, Lcom/lumiyaviewer/lumiya/voice/common/model/Voice3DVector;->z:F

    invoke-static {v3}, Ljava/lang/Float;->valueOf(F)Ljava/lang/Float;

    move-result-object v3

    aput-object v3, v1, v2

    invoke-static {v0, v1}, Ljava/lang/String;->format(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method
