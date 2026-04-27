.class public abstract Lcom/google/common/collect/MultimapBuilder$SortedSetMultimapBuilder;
.super Lcom/google/common/collect/MultimapBuilder$SetMultimapBuilder;
.source "MultimapBuilder.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/google/common/collect/MultimapBuilder;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x409
    name = "SortedSetMultimapBuilder"
.end annotation

.annotation system Ldalvik/annotation/Signature;
    value = {
        "<K0:",
        "Ljava/lang/Object;",
        "V0:",
        "Ljava/lang/Object;",
        ">",
        "Lcom/google/common/collect/MultimapBuilder$SetMultimapBuilder",
        "<TK0;TV0;>;"
    }
.end annotation


# direct methods
.method constructor <init>()V
    .locals 0

    .prologue
    .line 474
    .local p0, "this":Lcom/google/common/collect/MultimapBuilder$SortedSetMultimapBuilder;, "Lcom/google/common/collect/MultimapBuilder$SortedSetMultimapBuilder<TK0;TV0;>;"
    invoke-direct {p0}, Lcom/google/common/collect/MultimapBuilder$SetMultimapBuilder;-><init>()V

    return-void
.end method


# virtual methods
.method public bridge synthetic build()Lcom/google/common/collect/Multimap;
    .locals 1

    .prologue
    .line 473
    .local p0, "this":Lcom/google/common/collect/MultimapBuilder$SortedSetMultimapBuilder;, "Lcom/google/common/collect/MultimapBuilder$SortedSetMultimapBuilder<TK0;TV0;>;"
    invoke-virtual {p0}, Lcom/google/common/collect/MultimapBuilder$SortedSetMultimapBuilder;->build()Lcom/google/common/collect/SortedSetMultimap;

    move-result-object v0

    return-object v0
.end method

.method public bridge synthetic build(Lcom/google/common/collect/Multimap;)Lcom/google/common/collect/Multimap;
    .locals 1
    .param p1, "x0"    # Lcom/google/common/collect/Multimap;

    .prologue
    .line 473
    .local p0, "this":Lcom/google/common/collect/MultimapBuilder$SortedSetMultimapBuilder;, "Lcom/google/common/collect/MultimapBuilder$SortedSetMultimapBuilder<TK0;TV0;>;"
    invoke-virtual {p0, p1}, Lcom/google/common/collect/MultimapBuilder$SortedSetMultimapBuilder;->build(Lcom/google/common/collect/Multimap;)Lcom/google/common/collect/SortedSetMultimap;

    move-result-object v0

    return-object v0
.end method

.method public bridge synthetic build()Lcom/google/common/collect/SetMultimap;
    .locals 1

    .prologue
    .line 473
    .local p0, "this":Lcom/google/common/collect/MultimapBuilder$SortedSetMultimapBuilder;, "Lcom/google/common/collect/MultimapBuilder$SortedSetMultimapBuilder<TK0;TV0;>;"
    invoke-virtual {p0}, Lcom/google/common/collect/MultimapBuilder$SortedSetMultimapBuilder;->build()Lcom/google/common/collect/SortedSetMultimap;

    move-result-object v0

    return-object v0
.end method

.method public bridge synthetic build(Lcom/google/common/collect/Multimap;)Lcom/google/common/collect/SetMultimap;
    .locals 1
    .param p1, "x0"    # Lcom/google/common/collect/Multimap;

    .prologue
    .line 473
    .local p0, "this":Lcom/google/common/collect/MultimapBuilder$SortedSetMultimapBuilder;, "Lcom/google/common/collect/MultimapBuilder$SortedSetMultimapBuilder<TK0;TV0;>;"
    invoke-virtual {p0, p1}, Lcom/google/common/collect/MultimapBuilder$SortedSetMultimapBuilder;->build(Lcom/google/common/collect/Multimap;)Lcom/google/common/collect/SortedSetMultimap;

    move-result-object v0

    return-object v0
.end method

.method public abstract build()Lcom/google/common/collect/SortedSetMultimap;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "<K:TK0;V:TV0;>()",
            "Lcom/google/common/collect/SortedSetMultimap",
            "<TK;TV;>;"
        }
    .end annotation
.end method

.method public build(Lcom/google/common/collect/Multimap;)Lcom/google/common/collect/SortedSetMultimap;
    .locals 1
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "<K:TK0;V:TV0;>(",
            "Lcom/google/common/collect/Multimap",
            "<+TK;+TV;>;)",
            "Lcom/google/common/collect/SortedSetMultimap",
            "<TK;TV;>;"
        }
    .end annotation

    .prologue
    .line 482
    .local p0, "this":Lcom/google/common/collect/MultimapBuilder$SortedSetMultimapBuilder;, "Lcom/google/common/collect/MultimapBuilder$SortedSetMultimapBuilder<TK0;TV0;>;"
    .local p1, "multimap":Lcom/google/common/collect/Multimap;, "Lcom/google/common/collect/Multimap<+TK;+TV;>;"
    invoke-super {p0, p1}, Lcom/google/common/collect/MultimapBuilder$SetMultimapBuilder;->build(Lcom/google/common/collect/Multimap;)Lcom/google/common/collect/SetMultimap;

    move-result-object v0

    check-cast v0, Lcom/google/common/collect/SortedSetMultimap;

    return-object v0
.end method
