.class Lcom/google/common/collect/Constraints$ConstrainedListIterator;
.super Lcom/google/common/collect/ForwardingListIterator;
.source "Constraints.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/google/common/collect/Constraints;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x8
    name = "ConstrainedListIterator"
.end annotation

.annotation system Ldalvik/annotation/Signature;
    value = {
        "<E:",
        "Ljava/lang/Object;",
        ">",
        "Lcom/google/common/collect/ForwardingListIterator",
        "<TE;>;"
    }
.end annotation


# instance fields
.field private final constraint:Lcom/google/common/collect/Constraint;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Lcom/google/common/collect/Constraint",
            "<-TE;>;"
        }
    .end annotation
.end field

.field private final delegate:Ljava/util/ListIterator;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/ListIterator",
            "<TE;>;"
        }
    .end annotation
.end field


# direct methods
.method public constructor <init>(Ljava/util/ListIterator;Lcom/google/common/collect/Constraint;)V
    .locals 0
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Ljava/util/ListIterator",
            "<TE;>;",
            "Lcom/google/common/collect/Constraint",
            "<-TE;>;)V"
        }
    .end annotation

    .prologue
    .line 291
    .local p0, "this":Lcom/google/common/collect/Constraints$ConstrainedListIterator;, "Lcom/google/common/collect/Constraints$ConstrainedListIterator<TE;>;"
    .local p1, "delegate":Ljava/util/ListIterator;, "Ljava/util/ListIterator<TE;>;"
    .local p2, "constraint":Lcom/google/common/collect/Constraint;, "Lcom/google/common/collect/Constraint<-TE;>;"
    invoke-direct {p0}, Lcom/google/common/collect/ForwardingListIterator;-><init>()V

    .line 292
    iput-object p1, p0, Lcom/google/common/collect/Constraints$ConstrainedListIterator;->delegate:Ljava/util/ListIterator;

    .line 293
    iput-object p2, p0, Lcom/google/common/collect/Constraints$ConstrainedListIterator;->constraint:Lcom/google/common/collect/Constraint;

    .line 294
    return-void
.end method


# virtual methods
.method public add(Ljava/lang/Object;)V
    .locals 1
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(TE;)V"
        }
    .end annotation

    .prologue
    .line 303
    .local p0, "this":Lcom/google/common/collect/Constraints$ConstrainedListIterator;, "Lcom/google/common/collect/Constraints$ConstrainedListIterator<TE;>;"
    .local p1, "element":Ljava/lang/Object;, "TE;"
    iget-object v0, p0, Lcom/google/common/collect/Constraints$ConstrainedListIterator;->constraint:Lcom/google/common/collect/Constraint;

    invoke-interface {v0, p1}, Lcom/google/common/collect/Constraint;->checkElement(Ljava/lang/Object;)Ljava/lang/Object;

    .line 304
    iget-object v0, p0, Lcom/google/common/collect/Constraints$ConstrainedListIterator;->delegate:Ljava/util/ListIterator;

    invoke-interface {v0, p1}, Ljava/util/ListIterator;->add(Ljava/lang/Object;)V

    .line 305
    return-void
.end method

.method protected bridge synthetic delegate()Ljava/lang/Object;
    .locals 1

    .prologue
    .line 287
    .local p0, "this":Lcom/google/common/collect/Constraints$ConstrainedListIterator;, "Lcom/google/common/collect/Constraints$ConstrainedListIterator<TE;>;"
    invoke-virtual {p0}, Lcom/google/common/collect/Constraints$ConstrainedListIterator;->delegate()Ljava/util/ListIterator;

    move-result-object v0

    return-object v0
.end method

.method protected bridge synthetic delegate()Ljava/util/Iterator;
    .locals 1

    .prologue
    .line 287
    .local p0, "this":Lcom/google/common/collect/Constraints$ConstrainedListIterator;, "Lcom/google/common/collect/Constraints$ConstrainedListIterator<TE;>;"
    invoke-virtual {p0}, Lcom/google/common/collect/Constraints$ConstrainedListIterator;->delegate()Ljava/util/ListIterator;

    move-result-object v0

    return-object v0
.end method

.method protected delegate()Ljava/util/ListIterator;
    .locals 1
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "()",
            "Ljava/util/ListIterator",
            "<TE;>;"
        }
    .end annotation

    .prologue
    .line 298
    .local p0, "this":Lcom/google/common/collect/Constraints$ConstrainedListIterator;, "Lcom/google/common/collect/Constraints$ConstrainedListIterator<TE;>;"
    iget-object v0, p0, Lcom/google/common/collect/Constraints$ConstrainedListIterator;->delegate:Ljava/util/ListIterator;

    return-object v0
.end method

.method public set(Ljava/lang/Object;)V
    .locals 1
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(TE;)V"
        }
    .end annotation

    .prologue
    .line 309
    .local p0, "this":Lcom/google/common/collect/Constraints$ConstrainedListIterator;, "Lcom/google/common/collect/Constraints$ConstrainedListIterator<TE;>;"
    .local p1, "element":Ljava/lang/Object;, "TE;"
    iget-object v0, p0, Lcom/google/common/collect/Constraints$ConstrainedListIterator;->constraint:Lcom/google/common/collect/Constraint;

    invoke-interface {v0, p1}, Lcom/google/common/collect/Constraint;->checkElement(Ljava/lang/Object;)Ljava/lang/Object;

    .line 310
    iget-object v0, p0, Lcom/google/common/collect/Constraints$ConstrainedListIterator;->delegate:Ljava/util/ListIterator;

    invoke-interface {v0, p1}, Ljava/util/ListIterator;->set(Ljava/lang/Object;)V

    .line 311
    return-void
.end method
