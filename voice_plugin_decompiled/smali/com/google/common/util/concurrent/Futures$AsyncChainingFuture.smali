.class final Lcom/google/common/util/concurrent/Futures$AsyncChainingFuture;
.super Lcom/google/common/util/concurrent/Futures$AbstractChainingFuture;
.source "Futures.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/google/common/util/concurrent/Futures;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x1a
    name = "AsyncChainingFuture"
.end annotation

.annotation system Ldalvik/annotation/Signature;
    value = {
        "<I:",
        "Ljava/lang/Object;",
        "O:",
        "Ljava/lang/Object;",
        ">",
        "Lcom/google/common/util/concurrent/Futures$AbstractChainingFuture",
        "<TI;TO;",
        "Lcom/google/common/util/concurrent/AsyncFunction",
        "<-TI;+TO;>;>;"
    }
.end annotation


# direct methods
.method constructor <init>(Lcom/google/common/util/concurrent/ListenableFuture;Lcom/google/common/util/concurrent/AsyncFunction;)V
    .locals 0
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Lcom/google/common/util/concurrent/ListenableFuture",
            "<+TI;>;",
            "Lcom/google/common/util/concurrent/AsyncFunction",
            "<-TI;+TO;>;)V"
        }
    .end annotation

    .prologue
    .line 1437
    .local p0, "this":Lcom/google/common/util/concurrent/Futures$AsyncChainingFuture;, "Lcom/google/common/util/concurrent/Futures$AsyncChainingFuture<TI;TO;>;"
    .local p1, "inputFuture":Lcom/google/common/util/concurrent/ListenableFuture;, "Lcom/google/common/util/concurrent/ListenableFuture<+TI;>;"
    .local p2, "function":Lcom/google/common/util/concurrent/AsyncFunction;, "Lcom/google/common/util/concurrent/AsyncFunction<-TI;+TO;>;"
    invoke-direct {p0, p1, p2}, Lcom/google/common/util/concurrent/Futures$AbstractChainingFuture;-><init>(Lcom/google/common/util/concurrent/ListenableFuture;Ljava/lang/Object;)V

    .line 1438
    return-void
.end method


# virtual methods
.method doTransform(Lcom/google/common/util/concurrent/AsyncFunction;Ljava/lang/Object;)V
    .locals 2
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Lcom/google/common/util/concurrent/AsyncFunction",
            "<-TI;+TO;>;TI;)V"
        }
    .end annotation

    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/lang/Exception;
        }
    .end annotation

    .prologue
    .line 1442
    .local p0, "this":Lcom/google/common/util/concurrent/Futures$AsyncChainingFuture;, "Lcom/google/common/util/concurrent/Futures$AsyncChainingFuture<TI;TO;>;"
    .local p1, "function":Lcom/google/common/util/concurrent/AsyncFunction;, "Lcom/google/common/util/concurrent/AsyncFunction<-TI;+TO;>;"
    .local p2, "input":Ljava/lang/Object;, "TI;"
    invoke-interface {p1, p2}, Lcom/google/common/util/concurrent/AsyncFunction;->apply(Ljava/lang/Object;)Lcom/google/common/util/concurrent/ListenableFuture;

    move-result-object v0

    .line 1443
    .local v0, "outputFuture":Lcom/google/common/util/concurrent/ListenableFuture;, "Lcom/google/common/util/concurrent/ListenableFuture<+TO;>;"
    const-string v1, "AsyncFunction.apply returned null instead of a Future. Did you mean to return immediateFuture(null)?"

    invoke-static {v0, v1}, Lcom/google/common/base/Preconditions;->checkNotNull(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    .line 1445
    invoke-virtual {p0, v0}, Lcom/google/common/util/concurrent/Futures$AsyncChainingFuture;->setFuture(Lcom/google/common/util/concurrent/ListenableFuture;)Z

    .line 1446
    return-void
.end method

.method bridge synthetic doTransform(Ljava/lang/Object;Ljava/lang/Object;)V
    .locals 0
    .param p1, "x0"    # Ljava/lang/Object;
    .param p2, "x1"    # Ljava/lang/Object;
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/lang/Exception;
        }
    .end annotation

    .prologue
    .line 1433
    .local p0, "this":Lcom/google/common/util/concurrent/Futures$AsyncChainingFuture;, "Lcom/google/common/util/concurrent/Futures$AsyncChainingFuture<TI;TO;>;"
    check-cast p1, Lcom/google/common/util/concurrent/AsyncFunction;

    .end local p1    # "x0":Ljava/lang/Object;
    invoke-virtual {p0, p1, p2}, Lcom/google/common/util/concurrent/Futures$AsyncChainingFuture;->doTransform(Lcom/google/common/util/concurrent/AsyncFunction;Ljava/lang/Object;)V

    return-void
.end method
