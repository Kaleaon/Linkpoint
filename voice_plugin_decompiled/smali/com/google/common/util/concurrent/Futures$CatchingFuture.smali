.class final Lcom/google/common/util/concurrent/Futures$CatchingFuture;
.super Lcom/google/common/util/concurrent/Futures$AbstractCatchingFuture;
.source "Futures.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/google/common/util/concurrent/Futures;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x18
    name = "CatchingFuture"
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
        "Lcom/google/common/base/Function",
        "<-TX;+TV;>;>;"
    }
.end annotation


# direct methods
.method constructor <init>(Lcom/google/common/util/concurrent/ListenableFuture;Ljava/lang/Class;Lcom/google/common/base/Function;)V
    .locals 0
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Lcom/google/common/util/concurrent/ListenableFuture",
            "<+TV;>;",
            "Ljava/lang/Class",
            "<TX;>;",
            "Lcom/google/common/base/Function",
            "<-TX;+TV;>;)V"
        }
    .end annotation

    .prologue
    .line 838
    .local p0, "this":Lcom/google/common/util/concurrent/Futures$CatchingFuture;, "Lcom/google/common/util/concurrent/Futures$CatchingFuture<TV;TX;>;"
    .local p1, "input":Lcom/google/common/util/concurrent/ListenableFuture;, "Lcom/google/common/util/concurrent/ListenableFuture<+TV;>;"
    .local p2, "exceptionType":Ljava/lang/Class;, "Ljava/lang/Class<TX;>;"
    .local p3, "fallback":Lcom/google/common/base/Function;, "Lcom/google/common/base/Function<-TX;+TV;>;"
    invoke-direct {p0, p1, p2, p3}, Lcom/google/common/util/concurrent/Futures$AbstractCatchingFuture;-><init>(Lcom/google/common/util/concurrent/ListenableFuture;Ljava/lang/Class;Ljava/lang/Object;)V

    .line 839
    return-void
.end method


# virtual methods
.method doFallback(Lcom/google/common/base/Function;Ljava/lang/Throwable;)V
    .locals 1
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Lcom/google/common/base/Function",
            "<-TX;+TV;>;TX;)V"
        }
    .end annotation

    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/lang/Exception;
        }
    .end annotation

    .prologue
    .line 842
    .local p0, "this":Lcom/google/common/util/concurrent/Futures$CatchingFuture;, "Lcom/google/common/util/concurrent/Futures$CatchingFuture<TV;TX;>;"
    .local p1, "fallback":Lcom/google/common/base/Function;, "Lcom/google/common/base/Function<-TX;+TV;>;"
    .local p2, "cause":Ljava/lang/Throwable;, "TX;"
    invoke-interface {p1, p2}, Lcom/google/common/base/Function;->apply(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v0

    .line 843
    .local v0, "replacement":Ljava/lang/Object;, "TV;"
    invoke-virtual {p0, v0}, Lcom/google/common/util/concurrent/Futures$CatchingFuture;->set(Ljava/lang/Object;)Z

    .line 844
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
    .line 834
    .local p0, "this":Lcom/google/common/util/concurrent/Futures$CatchingFuture;, "Lcom/google/common/util/concurrent/Futures$CatchingFuture<TV;TX;>;"
    check-cast p1, Lcom/google/common/base/Function;

    .end local p1    # "x0":Ljava/lang/Object;
    invoke-virtual {p0, p1, p2}, Lcom/google/common/util/concurrent/Futures$CatchingFuture;->doFallback(Lcom/google/common/base/Function;Ljava/lang/Throwable;)V

    return-void
.end method
