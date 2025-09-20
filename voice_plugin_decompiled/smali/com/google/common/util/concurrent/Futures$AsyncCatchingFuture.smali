.class final Lcom/google/common/util/concurrent/Futures$AsyncCatchingFuture;
.super Lcom/google/common/util/concurrent/Futures$AbstractCatchingFuture;
.source "Futures.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/google/common/util/concurrent/Futures;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x18
    name = "AsyncCatchingFuture"
.end annotation

.annotation system Ldalvik/annotation/Signature;
    value = {
        "<V:",
        "Ljava/lang/Object;",
        "X:",
        "Ljava/lang/Throwable;",
        ">",
        "Lcom/google/common/util/concurrent/Futures$AbstractCatchingFuture",
        "<TV;TX;",
        "Lcom/google/common/util/concurrent/AsyncFunction",
        "<-TX;+TV;>;>;"
    }
.end annotation


# direct methods
.method constructor <init>(Lcom/google/common/util/concurrent/ListenableFuture;Ljava/lang/Class;Lcom/google/common/util/concurrent/AsyncFunction;)V
    .locals 0
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Lcom/google/common/util/concurrent/ListenableFuture",
            "<+TV;>;",
            "Ljava/lang/Class",
            "<TX;>;",
            "Lcom/google/common/util/concurrent/AsyncFunction",
            "<-TX;+TV;>;)V"
        }
    .end annotation

    .prologue
    .line 818
    .local p0, "this":Lcom/google/common/util/concurrent/Futures$AsyncCatchingFuture;, "Lcom/google/common/util/concurrent/Futures$AsyncCatchingFuture<TV;TX;>;"
    .local p1, "input":Lcom/google/common/util/concurrent/ListenableFuture;, "Lcom/google/common/util/concurrent/ListenableFuture<+TV;>;"
    .local p2, "exceptionType":Ljava/lang/Class;, "Ljava/lang/Class<TX;>;"
    .local p3, "fallback":Lcom/google/common/util/concurrent/AsyncFunction;, "Lcom/google/common/util/concurrent/AsyncFunction<-TX;+TV;>;"
    invoke-direct {p0, p1, p2, p3}, Lcom/google/common/util/concurrent/Futures$AbstractCatchingFuture;-><init>(Lcom/google/common/util/concurrent/ListenableFuture;Ljava/lang/Class;Ljava/lang/Object;)V

    .line 819
    return-void
.end method


# virtual methods
.method doFallback(Lcom/google/common/util/concurrent/AsyncFunction;Ljava/lang/Throwable;)V
    .locals 2
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Lcom/google/common/util/concurrent/AsyncFunction",
            "<-TX;+TV;>;TX;)V"
        }
    .end annotation

    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/lang/Exception;
        }
    .end annotation

    .prologue
    .line 823
    .local p0, "this":Lcom/google/common/util/concurrent/Futures$AsyncCatchingFuture;, "Lcom/google/common/util/concurrent/Futures$AsyncCatchingFuture<TV;TX;>;"
    .local p1, "fallback":Lcom/google/common/util/concurrent/AsyncFunction;, "Lcom/google/common/util/concurrent/AsyncFunction<-TX;+TV;>;"
    .local p2, "cause":Ljava/lang/Throwable;, "TX;"
    invoke-interface {p1, p2}, Lcom/google/common/util/concurrent/AsyncFunction;->apply(Ljava/lang/Object;)Lcom/google/common/util/concurrent/ListenableFuture;

    move-result-object v0

    .line 824
    .local v0, "replacement":Lcom/google/common/util/concurrent/ListenableFuture;, "Lcom/google/common/util/concurrent/ListenableFuture<+TV;>;"
    const-string v1, "AsyncFunction.apply returned null instead of a Future. Did you mean to return immediateFuture(null)?"

    invoke-static {v0, v1}, Lcom/google/common/base/Preconditions;->checkNotNull(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    .line 826
    invoke-virtual {p0, v0}, Lcom/google/common/util/concurrent/Futures$AsyncCatchingFuture;->setFuture(Lcom/google/common/util/concurrent/ListenableFuture;)Z

    .line 827
    return-void
.end method

.method bridge synthetic doFallback(Ljava/lang/Object;Ljava/lang/Throwable;)V
    .locals 0
    .param p1, "x0"    # Ljava/lang/Object;
    .param p2, "x1"    # Ljava/lang/Throwable;
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/lang/Exception;
        }
    .end annotation

    .prologue
    .line 813
    .local p0, "this":Lcom/google/common/util/concurrent/Futures$AsyncCatchingFuture;, "Lcom/google/common/util/concurrent/Futures$AsyncCatchingFuture<TV;TX;>;"
    check-cast p1, Lcom/google/common/util/concurrent/AsyncFunction;

    .end local p1    # "x0":Ljava/lang/Object;
    invoke-virtual {p0, p1, p2}, Lcom/google/common/util/concurrent/Futures$AsyncCatchingFuture;->doFallback(Lcom/google/common/util/concurrent/AsyncFunction;Ljava/lang/Throwable;)V

    return-void
.end method
