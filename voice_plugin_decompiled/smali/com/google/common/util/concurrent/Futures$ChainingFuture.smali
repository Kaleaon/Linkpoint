.class final Lcom/google/common/util/concurrent/Futures$ChainingFuture;
.super Lcom/google/common/util/concurrent/Futures$AbstractChainingFuture;
.source "Futures.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/google/common/util/concurrent/Futures;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x1a
    name = "ChainingFuture"
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
        "Lcom/google/common/base/Function",
        "<-TI;+TO;>;>;"
    }
.end annotation


# direct methods
.method constructor <init>(Lcom/google/common/util/concurrent/ListenableFuture;Lcom/google/common/base/Function;)V
    .locals 0
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Lcom/google/common/util/concurrent/ListenableFuture",
            "<+TI;>;",
            "Lcom/google/common/base/Function",
            "<-TI;+TO;>;)V"
        }
    .end annotation

    .prologue
    .line 1458
    .local p0, "this":Lcom/google/common/util/concurrent/Futures$ChainingFuture;, "Lcom/google/common/util/concurrent/Futures$ChainingFuture<TI;TO;>;"
    .local p1, "inputFuture":Lcom/google/common/util/concurrent/ListenableFuture;, "Lcom/google/common/util/concurrent/ListenableFuture<+TI;>;"
    .local p2, "function":Lcom/google/common/base/Function;, "Lcom/google/common/base/Function<-TI;+TO;>;"
    invoke-direct {p0, p1, p2}, Lcom/google/common/util/concurrent/Futures$AbstractChainingFuture;-><init>(Lcom/google/common/util/concurrent/ListenableFuture;Ljava/lang/Object;)V

    .line 1459
    return-void
.end method


# virtual methods
.method doTransform(Lcom/google/common/base/Function;Ljava/lang/Object;)V
    .locals 1
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Lcom/google/common/base/Function",
            "<-TI;+TO;>;TI;)V"
        }
    .end annotation

    .prologue
    .line 1464
    .local p0, "this":Lcom/google/common/util/concurrent/Futures$ChainingFuture;, "Lcom/google/common/util/concurrent/Futures$ChainingFuture<TI;TO;>;"
    .local p1, "function":Lcom/google/common/base/Function;, "Lcom/google/common/base/Function<-TI;+TO;>;"
    .local p2, "input":Ljava/lang/Object;, "TI;"
    invoke-interface {p1, p2}, Lcom/google/common/base/Function;->apply(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v0

    invoke-virtual {p0, v0}, Lcom/google/common/util/concurrent/Futures$ChainingFuture;->set(Ljava/lang/Object;)Z

    .line 1465
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
    .line 1453
    .local p0, "this":Lcom/google/common/util/concurrent/Futures$ChainingFuture;, "Lcom/google/common/util/concurrent/Futures$ChainingFuture<TI;TO;>;"
    check-cast p1, Lcom/google/common/base/Function;

    .end local p1    # "x0":Ljava/lang/Object;
    invoke-virtual {p0, p1, p2}, Lcom/google/common/util/concurrent/Futures$ChainingFuture;->doTransform(Lcom/google/common/base/Function;Ljava/lang/Object;)V

    return-void
.end method
