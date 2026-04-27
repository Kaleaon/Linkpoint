.class abstract Lcom/google/common/util/concurrent/Futures$AbstractCatchingFuture;
.super Lcom/google/common/util/concurrent/AbstractFuture$TrustedFuture;
.source "Futures.java"

# interfaces
.implements Ljava/lang/Runnable;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/google/common/util/concurrent/Futures;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x40a
    name = "AbstractCatchingFuture"
.end annotation

.annotation system Ldalvik/annotation/Signature;
    value = {
        "<V:",
        "Ljava/lang/Object;",
        "X:",
        "Ljava/lang/Throwable;",
        "F:",
        "Ljava/lang/Object;",
        ">",
        "Lcom/google/common/util/concurrent/AbstractFuture$TrustedFuture",
        "<TV;>;",
        "Ljava/lang/Runnable;"
    }
.end annotation


# instance fields
.field exceptionType:Ljava/lang/Class;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/lang/Class",
            "<TX;>;"
        }
    .end annotation

    .annotation runtime Ljavax/annotation/Nullable;
    .end annotation
.end field

.field fallback:Ljava/lang/Object;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "TF;"
        }
    .end annotation

    .annotation runtime Ljavax/annotation/Nullable;
    .end annotation
.end field

.field inputFuture:Lcom/google/common/util/concurrent/ListenableFuture;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Lcom/google/common/util/concurrent/ListenableFuture",
            "<+TV;>;"
        }
    .end annotation

    .annotation runtime Ljavax/annotation/Nullable;
    .end annotation
.end field


# direct methods
.method constructor <init>(Lcom/google/common/util/concurrent/ListenableFuture;Ljava/lang/Class;Ljava/lang/Object;)V
    .locals 1
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Lcom/google/common/util/concurrent/ListenableFuture",
            "<+TV;>;",
            "Ljava/lang/Class",
            "<TX;>;TF;)V"
        }
    .end annotation

    .prologue
    .line 758
    .local p0, "this":Lcom/google/common/util/concurrent/Futures$AbstractCatchingFuture;, "Lcom/google/common/util/concurrent/Futures$AbstractCatchingFuture<TV;TX;TF;>;"
    .local p1, "inputFuture":Lcom/google/common/util/concurrent/ListenableFuture;, "Lcom/google/common/util/concurrent/ListenableFuture<+TV;>;"
    .local p2, "exceptionType":Ljava/lang/Class;, "Ljava/lang/Class<TX;>;"
    .local p3, "fallback":Ljava/lang/Object;, "TF;"
    invoke-direct {p0}, Lcom/google/common/util/concurrent/AbstractFuture$TrustedFuture;-><init>()V

    .line 759
    invoke-static {p1}, Lcom/google/common/base/Preconditions;->checkNotNull(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Lcom/google/common/util/concurrent/ListenableFuture;

    iput-object v0, p0, Lcom/google/common/util/concurrent/Futures$AbstractCatchingFuture;->inputFuture:Lcom/google/common/util/concurrent/ListenableFuture;

    .line 760
    invoke-static {p2}, Lcom/google/common/base/Preconditions;->checkNotNull(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Ljava/lang/Class;

    iput-object v0, p0, Lcom/google/common/util/concurrent/Futures$AbstractCatchingFuture;->exceptionType:Ljava/lang/Class;

    .line 761
    invoke-static {p3}, Lcom/google/common/base/Preconditions;->checkNotNull(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v0

    iput-object v0, p0, Lcom/google/common/util/concurrent/Futures$AbstractCatchingFuture;->fallback:Ljava/lang/Object;

    .line 762
    return-void
.end method


# virtual methods
.method abstract doFallback(Ljava/lang/Object;Ljava/lang/Throwable;)V
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(TF;TX;)V"
        }
    .end annotation

    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/lang/Exception;
        }
    .end annotation
.end method

.method final done()V
    .locals 2

    .prologue
    .local p0, "this":Lcom/google/common/util/concurrent/Futures$AbstractCatchingFuture;, "Lcom/google/common/util/concurrent/Futures$AbstractCatchingFuture<TV;TX;TF;>;"
    const/4 v1, 0x0

    .line 802
    iget-object v0, p0, Lcom/google/common/util/concurrent/Futures$AbstractCatchingFuture;->inputFuture:Lcom/google/common/util/concurrent/ListenableFuture;

    invoke-virtual {p0, v0}, Lcom/google/common/util/concurrent/Futures$AbstractCatchingFuture;->maybePropagateCancellation(Ljava/util/concurrent/Future;)V

    .line 803
    iput-object v1, p0, Lcom/google/common/util/concurrent/Futures$AbstractCatchingFuture;->inputFuture:Lcom/google/common/util/concurrent/ListenableFuture;

    .line 804
    iput-object v1, p0, Lcom/google/common/util/concurrent/Futures$AbstractCatchingFuture;->exceptionType:Ljava/lang/Class;

    .line 805
    iput-object v1, p0, Lcom/google/common/util/concurrent/Futures$AbstractCatchingFuture;->fallback:Ljava/lang/Object;

    .line 806
    return-void
.end method

.method public final run()V
    .locals 11

    .prologue
    .local p0, "this":Lcom/google/common/util/concurrent/Futures$AbstractCatchingFuture;, "Lcom/google/common/util/concurrent/Futures$AbstractCatchingFuture<TV;TX;TF;>;"
    const/4 v10, 0x0

    const/4 v6, 0x1

    const/4 v7, 0x0

    .line 765
    iget-object v4, p0, Lcom/google/common/util/concurrent/Futures$AbstractCatchingFuture;->inputFuture:Lcom/google/common/util/concurrent/ListenableFuture;

    .line 766
    .local v4, "localInputFuture":Lcom/google/common/util/concurrent/ListenableFuture;, "Lcom/google/common/util/concurrent/ListenableFuture<+TV;>;"
    iget-object v2, p0, Lcom/google/common/util/concurrent/Futures$AbstractCatchingFuture;->exceptionType:Ljava/lang/Class;

    .line 767
    .local v2, "localExceptionType":Ljava/lang/Class;, "Ljava/lang/Class<TX;>;"
    iget-object v3, p0, Lcom/google/common/util/concurrent/Futures$AbstractCatchingFuture;->fallback:Ljava/lang/Object;

    .line 768
    .local v3, "localFallback":Ljava/lang/Object;, "TF;"
    if-nez v4, :cond_0

    move v9, v6

    :goto_0
    if-nez v2, :cond_1

    move v8, v6

    :goto_1
    or-int/2addr v8, v9

    if-nez v3, :cond_2

    :goto_2
    or-int/2addr v6, v8

    invoke-virtual {p0}, Lcom/google/common/util/concurrent/Futures$AbstractCatchingFuture;->isCancelled()Z

    move-result v7

    or-int/2addr v6, v7

    if-eqz v6, :cond_3

    .line 796
    :goto_3
    return-void

    :cond_0
    move v9, v7

    .line 768
    goto :goto_0

    :cond_1
    move v8, v7

    goto :goto_1

    :cond_2
    move v6, v7

    goto :goto_2

    .line 772
    :cond_3
    iput-object v10, p0, Lcom/google/common/util/concurrent/Futures$AbstractCatchingFuture;->inputFuture:Lcom/google/common/util/concurrent/ListenableFuture;

    .line 773
    iput-object v10, p0, Lcom/google/common/util/concurrent/Futures$AbstractCatchingFuture;->exceptionType:Ljava/lang/Class;

    .line 774
    iput-object v10, p0, Lcom/google/common/util/concurrent/Futures$AbstractCatchingFuture;->fallback:Ljava/lang/Object;

    .line 778
    :try_start_0
    invoke-static {v4}, Lcom/google/common/util/concurrent/Uninterruptibles;->getUninterruptibly(Ljava/util/concurrent/Future;)Ljava/lang/Object;

    move-result-object v6

    invoke-virtual {p0, v6}, Lcom/google/common/util/concurrent/Futures$AbstractCatchingFuture;->set(Ljava/lang/Object;)Z
    :try_end_0
    .catch Ljava/util/concurrent/ExecutionException; {:try_start_0 .. :try_end_0} :catch_0
    .catch Ljava/lang/Throwable; {:try_start_0 .. :try_end_0} :catch_2

    goto :goto_3

    .line 780
    :catch_0
    move-exception v1

    .line 781
    .local v1, "e":Ljava/util/concurrent/ExecutionException;
    invoke-virtual {v1}, Ljava/util/concurrent/ExecutionException;->getCause()Ljava/lang/Throwable;

    move-result-object v5

    .line 786
    .end local v1    # "e":Ljava/util/concurrent/ExecutionException;
    .local v5, "throwable":Ljava/lang/Throwable;
    :goto_4
    :try_start_1
    invoke-static {v5, v2}, Lcom/google/common/util/concurrent/Platform;->isInstanceOfThrowableClass(Ljava/lang/Throwable;Ljava/lang/Class;)Z

    move-result v6

    if-eqz v6, :cond_4

    .line 788
    move-object v0, v5

    .line 789
    .local v0, "castThrowable":Ljava/lang/Throwable;, "TX;"
    invoke-virtual {p0, v3, v0}, Lcom/google/common/util/concurrent/Futures$AbstractCatchingFuture;->doFallback(Ljava/lang/Object;Ljava/lang/Throwable;)V
    :try_end_1
    .catch Ljava/lang/Throwable; {:try_start_1 .. :try_end_1} :catch_1

    goto :goto_3

    .line 793
    .end local v0    # "castThrowable":Ljava/lang/Throwable;, "TX;"
    :catch_1
    move-exception v1

    .line 794
    .local v1, "e":Ljava/lang/Throwable;
    invoke-virtual {p0, v1}, Lcom/google/common/util/concurrent/Futures$AbstractCatchingFuture;->setException(Ljava/lang/Throwable;)Z

    goto :goto_3

    .line 782
    .end local v1    # "e":Ljava/lang/Throwable;
    .end local v5    # "throwable":Ljava/lang/Throwable;
    :catch_2
    move-exception v1

    .line 783
    .restart local v1    # "e":Ljava/lang/Throwable;
    move-object v5, v1

    .restart local v5    # "throwable":Ljava/lang/Throwable;
    goto :goto_4

    .line 791
    .end local v1    # "e":Ljava/lang/Throwable;
    :cond_4
    :try_start_2
    invoke-virtual {p0, v5}, Lcom/google/common/util/concurrent/Futures$AbstractCatchingFuture;->setException(Ljava/lang/Throwable;)Z
    :try_end_2
    .catch Ljava/lang/Throwable; {:try_start_2 .. :try_end_2} :catch_1

    goto :goto_3
.end method
