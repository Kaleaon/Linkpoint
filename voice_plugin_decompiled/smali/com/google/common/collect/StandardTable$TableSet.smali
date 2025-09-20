.class abstract Lcom/google/common/collect/StandardTable$TableSet;
.super Lcom/google/common/collect/Sets$ImprovedAbstractSet;
.source "StandardTable.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/google/common/collect/StandardTable;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x402
    name = "TableSet"
.end annotation

.annotation system Ldalvik/annotation/Signature;
    value = {
        "<T:",
        "Ljava/lang/Object;",
        ">",
        "Lcom/google/common/collect/Sets$ImprovedAbstractSet",
        "<TT;>;"
    }
.end annotation


# instance fields
.field final synthetic this$0:Lcom/google/common/collect/StandardTable;


# direct methods
.method private constructor <init>(Lcom/google/common/collect/StandardTable;)V
    .locals 0

    .prologue
    .line 203
    .local p0, "this":Lcom/google/common/collect/StandardTable$TableSet;, "Lcom/google/common/collect/StandardTable<TR;TC;TV;>.TableSet<TT;>;"
    iput-object p1, p0, Lcom/google/common/collect/StandardTable$TableSet;->this$0:Lcom/google/common/collect/StandardTable;

    invoke-direct {p0}, Lcom/google/common/collect/Sets$ImprovedAbstractSet;-><init>()V

    return-void
.end method

.method synthetic constructor <init>(Lcom/google/common/collect/StandardTable;Lcom/google/common/collect/StandardTable$1;)V
    .locals 0
    .param p1, "x0"    # Lcom/google/common/collect/StandardTable;
    .param p2, "x1"    # Lcom/google/common/collect/StandardTable$1;

    .prologue
    .line 203
    .local p0, "this":Lcom/google/common/collect/StandardTable$TableSet;, "Lcom/google/common/collect/StandardTable<TR;TC;TV;>.TableSet<TT;>;"
    invoke-direct {p0, p1}, Lcom/google/common/collect/StandardTable$TableSet;-><init>(Lcom/google/common/collect/StandardTable;)V

    return-void
.end method


# virtual methods
.method public clear()V
    .locals 1

    .prologue
    .line 211
    .local p0, "this":Lcom/google/common/collect/StandardTable$TableSet;, "Lcom/google/common/collect/StandardTable<TR;TC;TV;>.TableSet<TT;>;"
    iget-object v0, p0, Lcom/google/common/collect/StandardTable$TableSet;->this$0:Lcom/google/common/collect/StandardTable;

    iget-object v0, v0, Lcom/google/common/collect/StandardTable;->backingMap:Ljava/util/Map;

    invoke-interface {v0}, Ljava/util/Map;->clear()V

    .line 212
    return-void
.end method

.method public isEmpty()Z
    .locals 1

    .prologue
    .line 206
    .local p0, "this":Lcom/google/common/collect/StandardTable$TableSet;, "Lcom/google/common/collect/StandardTable<TR;TC;TV;>.TableSet<TT;>;"
    iget-object v0, p0, Lcom/google/common/collect/StandardTable$TableSet;->this$0:Lcom/google/common/collect/StandardTable;

    iget-object v0, v0, Lcom/google/common/collect/StandardTable;->backingMap:Ljava/util/Map;

    invoke-interface {v0}, Ljava/util/Map;->isEmpty()Z

    move-result v0

    return v0
.end method
