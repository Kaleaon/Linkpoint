.class Lcom/google/common/util/concurrent/Striped$SmallLazyStriped;
.super Lcom/google/common/util/concurrent/Striped$PowerOfTwoStriped;
.source "Striped.java"


# annotations
.annotation build Lcom/google/common/annotations/VisibleForTesting;
.end annotation

.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/google/common/util/concurrent/Striped;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x8
    name = "SmallLazyStriped"
.end annotation

.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Lcom/google/common/util/concurrent/Striped$SmallLazyStriped$ArrayReference;
    }
.end annotation

.annotation system Ldalvik/annotation/Signature;
    value = {
        "<",
        "L:Ljava/lang/Object;",
        ">",
        "Lcom/google/common/util/concurrent/Striped$PowerOfTwoStriped",
        "<T",
        "L;",
        ">;"
    }
.end annotation


# instance fields
.field final locks:Ljava/util/concurrent/atomic/AtomicReferenceArray;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/concurrent/atomic/AtomicReferenceArray",
            "<",
            "Lcom/google/common/util/concurrent/Striped$SmallLazyStriped$ArrayReference",
            "<+T",
            "L;",
            ">;>;"
        }
    .end annotation
.end field

.field final queue:Ljava/lang/ref/ReferenceQueue;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/lang/ref/ReferenceQueue",
            "<T",
            "L;",
            ">;"
        }
    .end annotation
.end field

.field final size:I

.field final supplier:Lcom/google/common/base/Supplier;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Lcom/google/common/base/Supplier",
            "<T",
            "L;",
            ">;"
        }
    .end annotation
.end field


# direct methods
.method constructor <init>(ILcom/google/common/base/Supplier;)V
    .locals 2
    .param p1, "stripes"    # I
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(I",
            "Lcom/google/common/base/Supplier",
            "<T",
            "L;",
            ">;)V"
        }
    .end annotation

    .prologue
    .line 348
    .local p0, "this":Lcom/google/common/util/concurrent/Striped$SmallLazyStriped;, "Lcom/google/common/util/concurrent/Striped$SmallLazyStriped<TL;>;"
    .local p2, "supplier":Lcom/google/common/base/Supplier;, "Lcom/google/common/base/Supplier<TL;>;"
    invoke-direct {p0, p1}, Lcom/google/common/util/concurrent/Striped$PowerOfTwoStriped;-><init>(I)V

    .line 345
    new-instance v0, Ljava/lang/ref/ReferenceQueue;

    invoke-direct {v0}, Ljava/lang/ref/ReferenceQueue;-><init>()V

    iput-object v0, p0, Lcom/google/common/util/concurrent/Striped$SmallLazyStriped;->queue:Ljava/lang/ref/ReferenceQueue;

    .line 349
    iget v0, p0, Lcom/google/common/util/concurrent/Striped$SmallLazyStriped;->mask:I

    const/4 v1, -0x1

    if-ne v0, v1, :cond_0

    const v0, 0x7fffffff

    :goto_0
    iput v0, p0, Lcom/google/common/util/concurrent/Striped$SmallLazyStriped;->size:I

    .line 350
    new-instance v0, Ljava/util/concurrent/atomic/AtomicReferenceArray;

    iget v1, p0, Lcom/google/common/util/concurrent/Striped$SmallLazyStriped;->size:I

    invoke-direct {v0, v1}, Ljava/util/concurrent/atomic/AtomicReferenceArray;-><init>(I)V

    iput-object v0, p0, Lcom/google/common/util/concurrent/Striped$SmallLazyStriped;->locks:Ljava/util/concurrent/atomic/AtomicReferenceArray;

    .line 351
    iput-object p2, p0, Lcom/google/common/util/concurrent/Striped$SmallLazyStriped;->supplier:Lcom/google/common/base/Supplier;

    .line 352
    return-void

    .line 349
    :cond_0
    iget v0, p0, Lcom/google/common/util/concurrent/Striped$SmallLazyStriped;->mask:I

    add-int/lit8 v0, v0, 0x1

    goto :goto_0
.end method

.method private drainQueue()V
    .locals 5

    .prologue
    .line 382
    .local p0, "this":Lcom/google/common/util/concurrent/Striped$SmallLazyStriped;, "Lcom/google/common/util/concurrent/Striped$SmallLazyStriped<TL;>;"
    :goto_0
    iget-object v2, p0, Lcom/google/common/util/concurrent/Striped$SmallLazyStriped;->queue:Ljava/lang/ref/ReferenceQueue;

    invoke-virtual {v2}, Ljava/lang/ref/ReferenceQueue;->poll()Ljava/lang/ref/Reference;

    move-result-object v1

    .local v1, "ref":Ljava/lang/ref/Reference;, "Ljava/lang/ref/Reference<+TL;>;"
    if-eqz v1, :cond_0

    move-object v0, v1

    .line 384
    check-cast v0, Lcom/google/common/util/concurrent/Striped$SmallLazyStriped$ArrayReference;

    .line 387
    .local v0, "arrayRef":Lcom/google/common/util/concurrent/Striped$SmallLazyStriped$ArrayReference;, "Lcom/google/common/util/concurrent/Striped$SmallLazyStriped$ArrayReference<+TL;>;"
    iget-object v2, p0, Lcom/google/common/util/concurrent/Striped$SmallLazyStriped;->locks:Ljava/util/concurrent/atomic/AtomicReferenceArray;

    iget v3, v0, Lcom/google/common/util/concurrent/Striped$SmallLazyStriped$ArrayReference;->index:I

    const/4 v4, 0x0

    invoke-virtual {v2, v3, v0, v4}, Ljava/util/concurrent/atomic/AtomicReferenceArray;->compareAndSet(ILjava/lang/Object;Ljava/lang/Object;)Z

    goto :goto_0

    .line 389
    .end local v0    # "arrayRef":Lcom/google/common/util/concurrent/Striped$SmallLazyStriped$ArrayReference;, "Lcom/google/common/util/concurrent/Striped$SmallLazyStriped$ArrayReference<+TL;>;"
    :cond_0
    return-void
.end method


# virtual methods
.method public getAt(I)Ljava/lang/Object;
    .locals 7
    .param p1, "index"    # I
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(I)T",
            "L;"
        }
    .end annotation

    .prologue
    .local p0, "this":Lcom/google/common/util/concurrent/Striped$SmallLazyStriped;, "Lcom/google/common/util/concurrent/Striped$SmallLazyStriped<TL;>;"
    const/4 v4, 0x0

    .line 355
    iget v5, p0, Lcom/google/common/util/concurrent/Striped$SmallLazyStriped;->size:I

    const v6, 0x7fffffff

    if-eq v5, v6, :cond_0

    .line 356
    invoke-virtual {p0}, Lcom/google/common/util/concurrent/Striped$SmallLazyStriped;->size()I

    move-result v5

    invoke-static {p1, v5}, Lcom/google/common/base/Preconditions;->checkElementIndex(II)I

    .line 358
    :cond_0
    iget-object v5, p0, Lcom/google/common/util/concurrent/Striped$SmallLazyStriped;->locks:Ljava/util/concurrent/atomic/AtomicReferenceArray;

    invoke-virtual {v5, p1}, Ljava/util/concurrent/atomic/AtomicReferenceArray;->get(I)Ljava/lang/Object;

    move-result-object v2

    check-cast v2, Lcom/google/common/util/concurrent/Striped$SmallLazyStriped$ArrayReference;

    .line 359
    .local v2, "existingRef":Lcom/google/common/util/concurrent/Striped$SmallLazyStriped$ArrayReference;, "Lcom/google/common/util/concurrent/Striped$SmallLazyStriped$ArrayReference<+TL;>;"
    if-nez v2, :cond_1

    move-object v1, v4

    .line 360
    .local v1, "existing":Ljava/lang/Object;, "TL;"
    :goto_0
    if-eqz v1, :cond_2

    move-object v0, v1

    .line 374
    :goto_1
    return-object v0

    .line 359
    .end local v1    # "existing":Ljava/lang/Object;, "TL;"
    :cond_1
    invoke-virtual {v2}, Lcom/google/common/util/concurrent/Striped$SmallLazyStriped$ArrayReference;->get()Ljava/lang/Object;

    move-result-object v1

    goto :goto_0

    .line 363
    .restart local v1    # "existing":Ljava/lang/Object;, "TL;"
    :cond_2
    iget-object v5, p0, Lcom/google/common/util/concurrent/Striped$SmallLazyStriped;->supplier:Lcom/google/common/base/Supplier;

    invoke-interface {v5}, Lcom/google/common/base/Supplier;->get()Ljava/lang/Object;

    move-result-object v0

    .line 364
    .local v0, "created":Ljava/lang/Object;, "TL;"
    new-instance v3, Lcom/google/common/util/concurrent/Striped$SmallLazyStriped$ArrayReference;

    iget-object v5, p0, Lcom/google/common/util/concurrent/Striped$SmallLazyStriped;->queue:Ljava/lang/ref/ReferenceQueue;

    invoke-direct {v3, v0, p1, v5}, Lcom/google/common/util/concurrent/Striped$SmallLazyStriped$ArrayReference;-><init>(Ljava/lang/Object;ILjava/lang/ref/ReferenceQueue;)V

    .line 365
    .local v3, "newRef":Lcom/google/common/util/concurrent/Striped$SmallLazyStriped$ArrayReference;, "Lcom/google/common/util/concurrent/Striped$SmallLazyStriped$ArrayReference<TL;>;"
    :cond_3
    iget-object v5, p0, Lcom/google/common/util/concurrent/Striped$SmallLazyStriped;->locks:Ljava/util/concurrent/atomic/AtomicReferenceArray;

    invoke-virtual {v5, p1, v2, v3}, Ljava/util/concurrent/atomic/AtomicReferenceArray;->compareAndSet(ILjava/lang/Object;Ljava/lang/Object;)Z

    move-result v5

    if-nez v5, :cond_5

    .line 367
    iget-object v5, p0, Lcom/google/common/util/concurrent/Striped$SmallLazyStriped;->locks:Ljava/util/concurrent/atomic/AtomicReferenceArray;

    invoke-virtual {v5, p1}, Ljava/util/concurrent/atomic/AtomicReferenceArray;->get(I)Ljava/lang/Object;

    move-result-object v2

    .end local v2    # "existingRef":Lcom/google/common/util/concurrent/Striped$SmallLazyStriped$ArrayReference;, "Lcom/google/common/util/concurrent/Striped$SmallLazyStriped$ArrayReference<+TL;>;"
    check-cast v2, Lcom/google/common/util/concurrent/Striped$SmallLazyStriped$ArrayReference;

    .line 368
    .restart local v2    # "existingRef":Lcom/google/common/util/concurrent/Striped$SmallLazyStriped$ArrayReference;, "Lcom/google/common/util/concurrent/Striped$SmallLazyStriped$ArrayReference<+TL;>;"
    if-nez v2, :cond_4

    move-object v1, v4

    .line 369
    :goto_2
    if-eqz v1, :cond_3

    move-object v0, v1

    .line 370
    goto :goto_1

    .line 368
    :cond_4
    invoke-virtual {v2}, Lcom/google/common/util/concurrent/Striped$SmallLazyStriped$ArrayReference;->get()Ljava/lang/Object;

    move-result-object v1

    goto :goto_2

    .line 373
    :cond_5
    invoke-direct {p0}, Lcom/google/common/util/concurrent/Striped$SmallLazyStriped;->drainQueue()V

    goto :goto_1
.end method

.method public size()I
    .locals 1

    .prologue
    .line 392
    .local p0, "this":Lcom/google/common/util/concurrent/Striped$SmallLazyStriped;, "Lcom/google/common/util/concurrent/Striped$SmallLazyStriped<TL;>;"
    iget v0, p0, Lcom/google/common/util/concurrent/Striped$SmallLazyStriped;->size:I

    return v0
.end method
