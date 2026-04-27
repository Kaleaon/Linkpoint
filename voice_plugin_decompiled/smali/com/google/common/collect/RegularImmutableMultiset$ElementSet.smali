.class final Lcom/google/common/collect/RegularImmutableMultiset$ElementSet;
.super Lcom/google/common/collect/ImmutableSet$Indexed;
.source "RegularImmutableMultiset.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/google/common/collect/RegularImmutableMultiset;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x12
    name = "ElementSet"
.end annotation

.annotation system Ldalvik/annotation/Signature;
    value = {
        "Lcom/google/common/collect/ImmutableSet$Indexed",
        "<TE;>;"
    }
.end annotation


# instance fields
.field final synthetic this$0:Lcom/google/common/collect/RegularImmutableMultiset;


# direct methods
.method private constructor <init>(Lcom/google/common/collect/RegularImmutableMultiset;)V
    .locals 0

    .prologue
    .line 145
    .local p0, "this":Lcom/google/common/collect/RegularImmutableMultiset$ElementSet;, "Lcom/google/common/collect/RegularImmutableMultiset<TE;>.ElementSet;"
    iput-object p1, p0, Lcom/google/common/collect/RegularImmutableMultiset$ElementSet;->this$0:Lcom/google/common/collect/RegularImmutableMultiset;

    invoke-direct {p0}, Lcom/google/common/collect/ImmutableSet$Indexed;-><init>()V

    return-void
.end method

.method synthetic constructor <init>(Lcom/google/common/collect/RegularImmutableMultiset;Lcom/google/common/collect/RegularImmutableMultiset$1;)V
    .locals 0
    .param p1, "x0"    # Lcom/google/common/collect/RegularImmutableMultiset;
    .param p2, "x1"    # Lcom/google/common/collect/RegularImmutableMultiset$1;

    .prologue
    .line 145
    .local p0, "this":Lcom/google/common/collect/RegularImmutableMultiset$ElementSet;, "Lcom/google/common/collect/RegularImmutableMultiset<TE;>.ElementSet;"
    invoke-direct {p0, p1}, Lcom/google/common/collect/RegularImmutableMultiset$ElementSet;-><init>(Lcom/google/common/collect/RegularImmutableMultiset;)V

    return-void
.end method


# virtual methods
.method public contains(Ljava/lang/Object;)Z
    .locals 1
    .param p1, "object"    # Ljava/lang/Object;
        .annotation runtime Ljavax/annotation/Nullable;
        .end annotation
    .end param

    .prologue
    .line 154
    .local p0, "this":Lcom/google/common/collect/RegularImmutableMultiset$ElementSet;, "Lcom/google/common/collect/RegularImmutableMultiset<TE;>.ElementSet;"
    iget-object v0, p0, Lcom/google/common/collect/RegularImmutableMultiset$ElementSet;->this$0:Lcom/google/common/collect/RegularImmutableMultiset;

    invoke-virtual {v0, p1}, Lcom/google/common/collect/RegularImmutableMultiset;->contains(Ljava/lang/Object;)Z

    move-result v0

    return v0
.end method

.method get(I)Ljava/lang/Object;
    .locals 1
    .param p1, "index"    # I
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(I)TE;"
        }
    .end annotation

    .prologue
    .line 149
    .local p0, "this":Lcom/google/common/collect/RegularImmutableMultiset$ElementSet;, "Lcom/google/common/collect/RegularImmutableMultiset<TE;>.ElementSet;"
    iget-object v0, p0, Lcom/google/common/collect/RegularImmutableMultiset$ElementSet;->this$0:Lcom/google/common/collect/RegularImmutableMultiset;

    invoke-static {v0}, Lcom/google/common/collect/RegularImmutableMultiset;->access$100(Lcom/google/common/collect/RegularImmutableMultiset;)[Lcom/google/common/collect/Multisets$ImmutableEntry;

    move-result-object v0

    aget-object v0, v0, p1

    invoke-virtual {v0}, Lcom/google/common/collect/Multisets$ImmutableEntry;->getElement()Ljava/lang/Object;

    move-result-object v0

    return-object v0
.end method

.method isPartialView()Z
    .locals 1

    .prologue
    .line 159
    .local p0, "this":Lcom/google/common/collect/RegularImmutableMultiset$ElementSet;, "Lcom/google/common/collect/RegularImmutableMultiset<TE;>.ElementSet;"
    const/4 v0, 0x1

    return v0
.end method

.method public size()I
    .locals 1

    .prologue
    .line 164
    .local p0, "this":Lcom/google/common/collect/RegularImmutableMultiset$ElementSet;, "Lcom/google/common/collect/RegularImmutableMultiset<TE;>.ElementSet;"
    iget-object v0, p0, Lcom/google/common/collect/RegularImmutableMultiset$ElementSet;->this$0:Lcom/google/common/collect/RegularImmutableMultiset;

    invoke-static {v0}, Lcom/google/common/collect/RegularImmutableMultiset;->access$100(Lcom/google/common/collect/RegularImmutableMultiset;)[Lcom/google/common/collect/Multisets$ImmutableEntry;

    move-result-object v0

    array-length v0, v0

    return v0
.end method
