.class Lcom/google/thirdparty/publicsuffix/TrieParser;
.super Ljava/lang/Object;
.source "TrieParser.java"


# annotations
.annotation build Lcom/google/common/annotations/GwtCompatible;
.end annotation


# static fields
.field private static final PREFIX_JOINER:Lcom/google/common/base/Joiner;


# direct methods
.method static constructor <clinit>()V
    .locals 1

    .prologue
    .line 32
    const-string v0, ""

    invoke-static {v0}, Lcom/google/common/base/Joiner;->on(Ljava/lang/String;)Lcom/google/common/base/Joiner;

    move-result-object v0

    sput-object v0, Lcom/google/thirdparty/publicsuffix/TrieParser;->PREFIX_JOINER:Lcom/google/common/base/Joiner;

    return-void
.end method

.method constructor <init>()V
    .locals 0

    .prologue
    .line 30
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method

.method private static doParseTrieToBuilder(Ljava/util/List;Ljava/lang/CharSequence;Lcom/google/common/collect/ImmutableMap$Builder;)I
    .locals 10
    .param p1, "encoded"    # Ljava/lang/CharSequence;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Ljava/util/List",
            "<",
            "Ljava/lang/CharSequence;",
            ">;",
            "Ljava/lang/CharSequence;",
            "Lcom/google/common/collect/ImmutableMap$Builder",
            "<",
            "Ljava/lang/String;",
            "Lcom/google/thirdparty/publicsuffix/PublicSuffixType;",
            ">;)I"
        }
    .end annotation

    .prologue
    .local p0, "stack":Ljava/util/List;, "Ljava/util/List<Ljava/lang/CharSequence;>;"
    .local p2, "builder":Lcom/google/common/collect/ImmutableMap$Builder;, "Lcom/google/common/collect/ImmutableMap$Builder<Ljava/lang/String;Lcom/google/thirdparty/publicsuffix/PublicSuffixType;>;"
    const/16 v9, 0x3a

    const/16 v8, 0x21

    const/4 v7, 0x0

    const/16 v6, 0x3f

    const/16 v5, 0x2c

    .line 65
    invoke-interface {p1}, Ljava/lang/CharSequence;->length()I

    move-result v2

    .line 66
    .local v2, "encodedLen":I
    const/4 v3, 0x0

    .line 67
    .local v3, "idx":I
    const/4 v0, 0x0

    .line 70
    .local v0, "c":C
    :goto_0
    if-ge v3, v2, :cond_0

    .line 71
    invoke-interface {p1, v3}, Ljava/lang/CharSequence;->charAt(I)C

    move-result v0

    .line 72
    const/16 v4, 0x26

    if-eq v0, v4, :cond_0

    if-eq v0, v6, :cond_0

    if-eq v0, v8, :cond_0

    if-eq v0, v9, :cond_0

    if-ne v0, v5, :cond_6

    .line 77
    :cond_0
    invoke-interface {p1, v7, v3}, Ljava/lang/CharSequence;->subSequence(II)Ljava/lang/CharSequence;

    move-result-object v4

    invoke-static {v4}, Lcom/google/thirdparty/publicsuffix/TrieParser;->reverse(Ljava/lang/CharSequence;)Ljava/lang/CharSequence;

    move-result-object v4

    invoke-interface {p0, v7, v4}, Ljava/util/List;->add(ILjava/lang/Object;)V

    .line 79
    if-eq v0, v8, :cond_1

    if-eq v0, v6, :cond_1

    if-eq v0, v9, :cond_1

    if-ne v0, v5, :cond_2

    .line 84
    :cond_1
    sget-object v4, Lcom/google/thirdparty/publicsuffix/TrieParser;->PREFIX_JOINER:Lcom/google/common/base/Joiner;

    invoke-virtual {v4, p0}, Lcom/google/common/base/Joiner;->join(Ljava/lang/Iterable;)Ljava/lang/String;

    move-result-object v1

    .line 85
    .local v1, "domain":Ljava/lang/String;
    invoke-virtual {v1}, Ljava/lang/String;->length()I

    move-result v4

    if-lez v4, :cond_2

    .line 86
    invoke-static {v0}, Lcom/google/thirdparty/publicsuffix/PublicSuffixType;->fromCode(C)Lcom/google/thirdparty/publicsuffix/PublicSuffixType;

    move-result-object v4

    invoke-virtual {p2, v1, v4}, Lcom/google/common/collect/ImmutableMap$Builder;->put(Ljava/lang/Object;Ljava/lang/Object;)Lcom/google/common/collect/ImmutableMap$Builder;

    .line 89
    .end local v1    # "domain":Ljava/lang/String;
    :cond_2
    add-int/lit8 v3, v3, 0x1

    .line 91
    if-eq v0, v6, :cond_5

    if-eq v0, v5, :cond_5

    .line 92
    :cond_3
    if-ge v3, v2, :cond_5

    .line 94
    invoke-interface {p1, v3, v2}, Ljava/lang/CharSequence;->subSequence(II)Ljava/lang/CharSequence;

    move-result-object v4

    invoke-static {p0, v4, p2}, Lcom/google/thirdparty/publicsuffix/TrieParser;->doParseTrieToBuilder(Ljava/util/List;Ljava/lang/CharSequence;Lcom/google/common/collect/ImmutableMap$Builder;)I

    move-result v4

    add-int/2addr v3, v4

    .line 95
    invoke-interface {p1, v3}, Ljava/lang/CharSequence;->charAt(I)C

    move-result v4

    if-eq v4, v6, :cond_4

    invoke-interface {p1, v3}, Ljava/lang/CharSequence;->charAt(I)C

    move-result v4

    if-ne v4, v5, :cond_3

    .line 97
    :cond_4
    add-int/lit8 v3, v3, 0x1

    .line 102
    :cond_5
    invoke-interface {p0, v7}, Ljava/util/List;->remove(I)Ljava/lang/Object;

    .line 103
    return v3

    .line 70
    :cond_6
    add-int/lit8 v3, v3, 0x1

    goto :goto_0
.end method

.method static parseTrie(Ljava/lang/CharSequence;)Lcom/google/common/collect/ImmutableMap;
    .locals 5
    .param p0, "encoded"    # Ljava/lang/CharSequence;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Ljava/lang/CharSequence;",
            ")",
            "Lcom/google/common/collect/ImmutableMap",
            "<",
            "Ljava/lang/String;",
            "Lcom/google/thirdparty/publicsuffix/PublicSuffixType;",
            ">;"
        }
    .end annotation

    .prologue
    .line 39
    invoke-static {}, Lcom/google/common/collect/ImmutableMap;->builder()Lcom/google/common/collect/ImmutableMap$Builder;

    move-result-object v0

    .line 40
    .local v0, "builder":Lcom/google/common/collect/ImmutableMap$Builder;, "Lcom/google/common/collect/ImmutableMap$Builder<Ljava/lang/String;Lcom/google/thirdparty/publicsuffix/PublicSuffixType;>;"
    invoke-interface {p0}, Ljava/lang/CharSequence;->length()I

    move-result v1

    .line 41
    .local v1, "encodedLen":I
    const/4 v2, 0x0

    .line 42
    .local v2, "idx":I
    :goto_0
    if-ge v2, v1, :cond_0

    .line 43
    invoke-static {}, Lcom/google/common/collect/Lists;->newLinkedList()Ljava/util/LinkedList;

    move-result-object v3

    invoke-interface {p0, v2, v1}, Ljava/lang/CharSequence;->subSequence(II)Ljava/lang/CharSequence;

    move-result-object v4

    invoke-static {v3, v4, v0}, Lcom/google/thirdparty/publicsuffix/TrieParser;->doParseTrieToBuilder(Ljava/util/List;Ljava/lang/CharSequence;Lcom/google/common/collect/ImmutableMap$Builder;)I

    move-result v3

    add-int/2addr v2, v3

    goto :goto_0

    .line 48
    :cond_0
    invoke-virtual {v0}, Lcom/google/common/collect/ImmutableMap$Builder;->build()Lcom/google/common/collect/ImmutableMap;

    move-result-object v3

    return-object v3
.end method

.method private static reverse(Ljava/lang/CharSequence;)Ljava/lang/CharSequence;
    .locals 5
    .param p0, "s"    # Ljava/lang/CharSequence;

    .prologue
    .line 112
    invoke-interface {p0}, Ljava/lang/CharSequence;->length()I

    move-result v2

    .line 113
    .local v2, "length":I
    const/4 v3, 0x1

    if-gt v2, v3, :cond_0

    .line 127
    .end local p0    # "s":Ljava/lang/CharSequence;
    :goto_0
    return-object p0

    .line 117
    .restart local p0    # "s":Ljava/lang/CharSequence;
    :cond_0
    new-array v0, v2, [C

    .line 118
    .local v0, "buffer":[C
    const/4 v3, 0x0

    add-int/lit8 v4, v2, -0x1

    invoke-interface {p0, v4}, Ljava/lang/CharSequence;->charAt(I)C

    move-result v4

    aput-char v4, v0, v3

    .line 120
    const/4 v1, 0x1

    .local v1, "i":I
    :goto_1
    if-ge v1, v2, :cond_2

    .line 121
    add-int/lit8 v3, v2, -0x1

    sub-int/2addr v3, v1

    invoke-interface {p0, v3}, Ljava/lang/CharSequence;->charAt(I)C

    move-result v3

    aput-char v3, v0, v1

    .line 122
    aget-char v3, v0, v1

    add-int/lit8 v4, v1, -0x1

    aget-char v4, v0, v4

    invoke-static {v3, v4}, Ljava/lang/Character;->isSurrogatePair(CC)Z

    move-result v3

    if-eqz v3, :cond_1

    .line 123
    add-int/lit8 v3, v1, -0x1

    invoke-static {v0, v3, v1}, Lcom/google/thirdparty/publicsuffix/TrieParser;->swap([CII)V

    .line 120
    :cond_1
    add-int/lit8 v1, v1, 0x1

    goto :goto_1

    .line 127
    :cond_2
    new-instance p0, Ljava/lang/String;

    .end local p0    # "s":Ljava/lang/CharSequence;
    invoke-direct {p0, v0}, Ljava/lang/String;-><init>([C)V

    goto :goto_0
.end method

.method private static swap([CII)V
    .locals 2
    .param p0, "buffer"    # [C
    .param p1, "f"    # I
    .param p2, "s"    # I

    .prologue
    .line 131
    aget-char v0, p0, p1

    .line 132
    .local v0, "tmp":C
    aget-char v1, p0, p2

    aput-char v1, p0, p1

    .line 133
    aput-char v0, p0, p2

    .line 134
    return-void
.end method
