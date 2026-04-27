.class Lcom/google/common/collect/MapConstraints$ConstrainedEntries$1;
.super Lcom/google/common/collect/TransformedIterator;
.source "MapConstraints.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/google/common/collect/MapConstraints$ConstrainedEntries;->iterator()Ljava/util/Iterator;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation

.annotation system Ldalvik/annotation/Signature;
    value = {
        "Lcom/google/common/collect/TransformedIterator",
        "<",
        "Ljava/util/Map$Entry",
        "<TK;TV;>;",
        "Ljava/util/Map$Entry",
        "<TK;TV;>;>;"
    }
.end annotation


# instance fields
.field final synthetic this$0:Lcom/google/common/collect/MapConstraints$ConstrainedEntries;


# direct methods
.method constructor <init>(Lcom/google/common/collect/MapConstraints$ConstrainedEntries;Ljava/util/Iterator;)V
    .locals 0

    .prologue
    .line 633
    .local p0, "this":Lcom/google/common/collect/MapConstraints$ConstrainedEntries$1;, "Lcom/google/common/collect/MapConstraints$ConstrainedEntries.1;"
    .local p2, "x0":Ljava/util/Iterator;, "Ljava/util/Iterator<+Ljava/util/Map$Entry<TK;TV;>;>;"
    iput-object p1, p0, Lcom/google/common/collect/MapConstraints$ConstrainedEntries$1;->this$0:Lcom/google/common/collect/MapConstraints$ConstrainedEntries;

    invoke-direct {p0, p2}, Lcom/google/common/collect/TransformedIterator;-><init>(Ljava/util/Iterator;)V

    return-void
.end method


# virtual methods
.method bridge synthetic transform(Ljava/lang/Object;)Ljava/lang/Object;
    .locals 1
    .param p1, "x0"    # Ljava/lang/Object;

    .prologue
    .line 633
    .local p0, "this":Lcom/google/common/collect/MapConstraints$ConstrainedEntries$1;, "Lcom/google/common/collect/MapConstraints$ConstrainedEntries.1;"
    check-cast p1, Ljava/util/Map$Entry;

    .end local p1    # "x0":Ljava/lang/Object;
    invoke-virtual {p0, p1}, Lcom/google/common/collect/MapConstraints$ConstrainedEntries$1;->transform(Ljava/util/Map$Entry;)Ljava/util/Map$Entry;

    move-result-object v0

    return-object v0
.end method

.method transform(Ljava/util/Map$Entry;)Ljava/util/Map$Entry;
    .locals 1
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Ljava/util/Map$Entry",
            "<TK;TV;>;)",
            "Ljava/util/Map$Entry",
            "<TK;TV;>;"
        }
    .end annotation

    .prologue
    .line 636
    .local p0, "this":Lcom/google/common/collect/MapConstraints$ConstrainedEntries$1;, "Lcom/google/common/collect/MapConstraints$ConstrainedEntries.1;"
    .local p1, "from":Ljava/util/Map$Entry;, "Ljava/util/Map$Entry<TK;TV;>;"
    iget-object v0, p0, Lcom/google/common/collect/MapConstraints$ConstrainedEntries$1;->this$0:Lcom/google/common/collect/MapConstraints$ConstrainedEntries;

    iget-object v0, v0, Lcom/google/common/collect/MapConstraints$ConstrainedEntries;->constraint:Lcom/google/common/collect/MapConstraint;

    invoke-static {p1, v0}, Lcom/google/common/collect/MapConstraints;->access$500(Ljava/util/Map$Entry;Lcom/google/common/collect/MapConstraint;)Ljava/util/Map$Entry;

    move-result-object v0

    return-object v0
.end method
