.class final Lcom/google/common/util/concurrent/Futures$TimeoutFuture$Fire;
.super Ljava/lang/Object;
.source "Futures.java"

# interfaces
.implements Ljava/lang/Runnable;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/google/common/util/concurrent/Futures$TimeoutFuture;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x1a
    name = "Fire"
.end annotation

.annotation system Ldalvik/annotation/Signature;
    value = {
        "<V:",
        "Ljava/lang/Object;",
        ">",
        "Ljava/lang/Object;",
        "Ljava/lang/Runnable;"
    }
.end annotation


# instance fields
.field timeoutFutureRef:Lcom/google/common/util/concurrent/Futures$TimeoutFuture;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Lcom/google/common/util/concurrent/Futures$TimeoutFuture",
            "<TV;>;"
        }
    .end annotation

    .annotation runtime Ljavax/annotation/Nullable;
    .end annotation
.end field


# direct methods
.method constructor <init>(Lcom/google/common/util/concurrent/Futures$TimeoutFuture;)V
    .locals 0
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Lcom/google/common/util/concurrent/Futures$TimeoutFuture",
            "<TV;>;)V"
        }
    .end annotation

    .prologue
    .line 910
    .local p0, "this":Lcom/google/common/util/concurrent/Futures$TimeoutFuture$Fire;, "Lcom/google/common/util/concurrent/Futures$TimeoutFuture$Fire<TV;>;"
    .local p1, "timeoutFuture":Lcom/google/common/util/concurrent/Futures$TimeoutFuture;, "Lcom/google/common/util/concurrent/Futures$TimeoutFuture<TV;>;"
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 911
    iput-object p1, p0, Lcom/google/common/util/concurrent/Futures$TimeoutFuture$Fire;->timeoutFutureRef:Lcom/google/common/util/concurrent/Futures$TimeoutFuture;

    .line 912
    return-void
.end method


# virtual methods
.method public run()V
    .locals 6

    .prologue
    .local p0, "this":Lcom/google/common/util/concurrent/Futures$TimeoutFuture$Fire;, "Lcom/google/common/util/concurrent/Futures$TimeoutFuture$Fire<TV;>;"
    const/4 v5, 0x1

    .line 917
    iget-object v1, p0, Lcom/google/common/util/concurrent/Futures$TimeoutFuture$Fire;->timeoutFutureRef:Lcom/google/common/util/concurrent/Futures$TimeoutFuture;

    .line 918
    .local v1, "timeoutFuture":Lcom/google/common/util/concurrent/Futures$TimeoutFuture;, "Lcom/google/common/util/concurrent/Futures$TimeoutFuture<TV;>;"
    if-nez v1, :cond_1

    .line 950
    :cond_0
    :goto_0
    return-void

    .line 921
    :cond_1
    iget-object v0, v1, Lcom/google/common/util/concurrent/Futures$TimeoutFuture;->delegateRef:Lcom/google/common/util/concurrent/ListenableFuture;

    .line 922
    .local v0, "delegate":Lcom/google/common/util/concurrent/ListenableFuture;, "Lcom/google/common/util/concurrent/ListenableFuture<TV;>;"
    if-eqz v0, :cond_0

    .line 938
    const/4 v2, 0x0

    iput-object v2, p0, Lcom/google/common/util/concurrent/Futures$TimeoutFuture$Fire;->timeoutFutureRef:Lcom/google/common/util/concurrent/Futures$TimeoutFuture;

    .line 939
    invoke-interface {v0}, Lcom/google/common/util/concurrent/ListenableFuture;->isDone()Z

    move-result v2

    if-eqz v2, :cond_2

    .line 940
    invoke-virtual {v1, v0}, Lcom/google/common/util/concurrent/Futures$TimeoutFuture;->setFuture(Lcom/google/common/util/concurrent/ListenableFuture;)Z

    goto :goto_0

    .line 945
    :cond_2
    :try_start_0
    new-instance v2, Ljava/util/concurrent/TimeoutException;

    new-instance v3, Ljava/lang/StringBuilder;

    invoke-direct {v3}, Ljava/lang/StringBuilder;-><init>()V

    const-string v4, "Future timed out: "

    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v3

    invoke-virtual {v3, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    move-result-object v3

    invoke-virtual {v3}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v3

    invoke-direct {v2, v3}, Ljava/util/concurrent/TimeoutException;-><init>(Ljava/lang/String;)V

    invoke-virtual {v1, v2}, Lcom/google/common/util/concurrent/Futures$TimeoutFuture;->setException(Ljava/lang/Throwable;)Z
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    .line 947
    invoke-interface {v0, v5}, Lcom/google/common/util/concurrent/ListenableFuture;->cancel(Z)Z

    goto :goto_0

    :catchall_0
    move-exception v2

    invoke-interface {v0, v5}, Lcom/google/common/util/concurrent/ListenableFuture;->cancel(Z)Z

    throw v2
.end method
