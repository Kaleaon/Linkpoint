.class public Ljavax/annotation/Nonnegative$Checker;
.super Ljava/lang/Object;
.source "Nonnegative.java"

# interfaces
.implements Ljavax/annotation/meta/TypeQualifierValidator;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Ljavax/annotation/Nonnegative;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x9
    name = "Checker"
.end annotation

.annotation system Ldalvik/annotation/Signature;
    value = {
        "Ljava/lang/Object;",
        "Ljavax/annotation/meta/TypeQualifierValidator",
        "<",
        "Ljavax/annotation/Nonnegative;",
        ">;"
    }
.end annotation


# direct methods
.method public constructor <init>()V
    .locals 0

    .prologue
    .line 18
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public bridge synthetic forConstantValue(Ljava/lang/annotation/Annotation;Ljava/lang/Object;)Ljavax/annotation/meta/When;
    .locals 1
    .param p1, "x0"    # Ljava/lang/annotation/Annotation;
    .param p2, "x1"    # Ljava/lang/Object;

    .prologue
    .line 18
    check-cast p1, Ljavax/annotation/Nonnegative;

    .end local p1    # "x0":Ljava/lang/annotation/Annotation;
    invoke-virtual {p0, p1, p2}, Ljavax/annotation/Nonnegative$Checker;->forConstantValue(Ljavax/annotation/Nonnegative;Ljava/lang/Object;)Ljavax/annotation/meta/When;

    move-result-object v0

    return-object v0
.end method

.method public forConstantValue(Ljavax/annotation/Nonnegative;Ljava/lang/Object;)Ljavax/annotation/meta/When;
    .locals 8
    .param p1, "annotation"    # Ljavax/annotation/Nonnegative;
    .param p2, "v"    # Ljava/lang/Object;

    .prologue
    const/4 v0, 0x1

    const/4 v2, 0x0

    .line 21
    instance-of v3, p2, Ljava/lang/Number;

    if-nez v3, :cond_0

    .line 22
    sget-object v2, Ljavax/annotation/meta/When;->NEVER:Ljavax/annotation/meta/When;

    .line 37
    :goto_0
    return-object v2

    :cond_0
    move-object v1, p2

    .line 24
    check-cast v1, Ljava/lang/Number;

    .line 25
    .local v1, "value":Ljava/lang/Number;
    instance-of v3, v1, Ljava/lang/Long;

    if-eqz v3, :cond_2

    .line 26
    invoke-virtual {v1}, Ljava/lang/Number;->longValue()J

    move-result-wide v4

    const-wide/16 v6, 0x0

    cmp-long v3, v4, v6

    if-gez v3, :cond_1

    .line 34
    .local v0, "isNegative":Z
    :goto_1
    if-eqz v0, :cond_8

    .line 35
    sget-object v2, Ljavax/annotation/meta/When;->NEVER:Ljavax/annotation/meta/When;

    goto :goto_0

    .end local v0    # "isNegative":Z
    :cond_1
    move v0, v2

    .line 26
    goto :goto_1

    .line 27
    :cond_2
    instance-of v3, v1, Ljava/lang/Double;

    if-eqz v3, :cond_4

    .line 28
    invoke-virtual {v1}, Ljava/lang/Number;->doubleValue()D

    move-result-wide v4

    const-wide/16 v6, 0x0

    cmpg-double v3, v4, v6

    if-gez v3, :cond_3

    .restart local v0    # "isNegative":Z
    :goto_2
    goto :goto_1

    .end local v0    # "isNegative":Z
    :cond_3
    move v0, v2

    goto :goto_2

    .line 29
    :cond_4
    instance-of v3, v1, Ljava/lang/Float;

    if-eqz v3, :cond_6

    .line 30
    invoke-virtual {v1}, Ljava/lang/Number;->floatValue()F

    move-result v3

    const/4 v4, 0x0

    cmpg-float v3, v3, v4

    if-gez v3, :cond_5

    .restart local v0    # "isNegative":Z
    :goto_3
    goto :goto_1

    .end local v0    # "isNegative":Z
    :cond_5
    move v0, v2

    goto :goto_3

    .line 32
    :cond_6
    invoke-virtual {v1}, Ljava/lang/Number;->intValue()I

    move-result v3

    if-gez v3, :cond_7

    .restart local v0    # "isNegative":Z
    :goto_4
    goto :goto_1

    .end local v0    # "isNegative":Z
    :cond_7
    move v0, v2

    goto :goto_4

    .line 37
    .restart local v0    # "isNegative":Z
    :cond_8
    sget-object v2, Ljavax/annotation/meta/When;->ALWAYS:Ljavax/annotation/meta/When;

    goto :goto_0
.end method
