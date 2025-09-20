.class final enum Lcom/google/common/hash/BloomFilterStrategies$1;
.super Lcom/google/common/hash/BloomFilterStrategies;
.source "BloomFilterStrategies.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/google/common/hash/BloomFilterStrategies;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x4008
    name = null
.end annotation


# direct methods
.method constructor <init>(Ljava/lang/String;I)V
    .locals 1

    .prologue
    .line 44
    const/4 v0, 0x0

    invoke-direct {p0, p1, p2, v0}, Lcom/google/common/hash/BloomFilterStrategies;-><init>(Ljava/lang/String;ILcom/google/common/hash/BloomFilterStrategies$1;)V

    return-void
.end method


# virtual methods
.method public mightContain(Ljava/lang/Object;Lcom/google/common/hash/Funnel;ILcom/google/common/hash/BloomFilterStrategies$BitArray;)Z
    .locals 10
    .param p3, "numHashFunctions"    # I
    .param p4, "bits"    # Lcom/google/common/hash/BloomFilterStrategies$BitArray;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "<T:",
            "Ljava/lang/Object;",
            ">(TT;",
            "Lcom/google/common/hash/Funnel",
            "<-TT;>;I",
            "Lcom/google/common/hash/BloomFilterStrategies$BitArray;",
            ")Z"
        }
    .end annotation

    .prologue
    .line 68
    .local p1, "object":Ljava/lang/Object;, "TT;"
    .local p2, "funnel":Lcom/google/common/hash/Funnel;, "Lcom/google/common/hash/Funnel<-TT;>;"
    invoke-virtual {p4}, Lcom/google/common/hash/BloomFilterStrategies$BitArray;->bitSize()J

    move-result-wide v0

    .line 69
    .local v0, "bitSize":J
    invoke-static {}, Lcom/google/common/hash/Hashing;->murmur3_128()Lcom/google/common/hash/HashFunction;

    move-result-object v8

    invoke-interface {v8, p1, p2}, Lcom/google/common/hash/HashFunction;->hashObject(Ljava/lang/Object;Lcom/google/common/hash/Funnel;)Lcom/google/common/hash/HashCode;

    move-result-object v8

    invoke-virtual {v8}, Lcom/google/common/hash/HashCode;->asLong()J

    move-result-wide v6

    .line 70
    .local v6, "hash64":J
    long-to-int v3, v6

    .line 71
    .local v3, "hash1":I
    const/16 v8, 0x20

    ushr-long v8, v6, v8

    long-to-int v4, v8

    .line 73
    .local v4, "hash2":I
    const/4 v5, 0x1

    .local v5, "i":I
    :goto_0
    if-gt v5, p3, :cond_2

    .line 74
    mul-int v8, v5, v4

    add-int v2, v3, v8

    .line 76
    .local v2, "combinedHash":I
    if-gez v2, :cond_0

    .line 77
    xor-int/lit8 v2, v2, -0x1

    .line 79
    :cond_0
    int-to-long v8, v2

    rem-long/2addr v8, v0

    invoke-virtual {p4, v8, v9}, Lcom/google/common/hash/BloomFilterStrategies$BitArray;->get(J)Z

    move-result v8

    if-nez v8, :cond_1

    .line 80
    const/4 v8, 0x0

    .line 83
    .end local v2    # "combinedHash":I
    :goto_1
    return v8

    .line 73
    .restart local v2    # "combinedHash":I
    :cond_1
    add-int/lit8 v5, v5, 0x1

    goto :goto_0

    .line 83
    .end local v2    # "combinedHash":I
    :cond_2
    const/4 v8, 0x1

    goto :goto_1
.end method

.method public put(Ljava/lang/Object;Lcom/google/common/hash/Funnel;ILcom/google/common/hash/BloomFilterStrategies$BitArray;)Z
    .locals 14
    .param p3, "numHashFunctions"    # I
    .param p4, "bits"    # Lcom/google/common/hash/BloomFilterStrategies$BitArray;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "<T:",
            "Ljava/lang/Object;",
            ">(TT;",
            "Lcom/google/common/hash/Funnel",
            "<-TT;>;I",
            "Lcom/google/common/hash/BloomFilterStrategies$BitArray;",
            ")Z"
        }
    .end annotation

    .prologue
    .line 48
    .local p1, "object":Ljava/lang/Object;, "TT;"
    .local p2, "funnel":Lcom/google/common/hash/Funnel;, "Lcom/google/common/hash/Funnel<-TT;>;"
    invoke-virtual/range {p4 .. p4}, Lcom/google/common/hash/BloomFilterStrategies$BitArray;->bitSize()J

    move-result-wide v2

    .line 49
    .local v2, "bitSize":J
    invoke-static {}, Lcom/google/common/hash/Hashing;->murmur3_128()Lcom/google/common/hash/HashFunction;

    move-result-object v11

    move-object/from16 v0, p2

    invoke-interface {v11, p1, v0}, Lcom/google/common/hash/HashFunction;->hashObject(Ljava/lang/Object;Lcom/google/common/hash/Funnel;)Lcom/google/common/hash/HashCode;

    move-result-object v11

    invoke-virtual {v11}, Lcom/google/common/hash/HashCode;->asLong()J

    move-result-wide v8

    .line 50
    .local v8, "hash64":J
    long-to-int v6, v8

    .line 51
    .local v6, "hash1":I
    const/16 v11, 0x20

    ushr-long v12, v8, v11

    long-to-int v7, v12

    .line 53
    .local v7, "hash2":I
    const/4 v4, 0x0

    .line 54
    .local v4, "bitsChanged":Z
    const/4 v10, 0x1

    .local v10, "i":I
    :goto_0
    move/from16 v0, p3

    if-gt v10, v0, :cond_1

    .line 55
    mul-int v11, v10, v7

    add-int v5, v6, v11

    .line 57
    .local v5, "combinedHash":I
    if-gez v5, :cond_0

    .line 58
    xor-int/lit8 v5, v5, -0x1

    .line 60
    :cond_0
    int-to-long v12, v5

    rem-long/2addr v12, v2

    move-object/from16 v0, p4

    invoke-virtual {v0, v12, v13}, Lcom/google/common/hash/BloomFilterStrategies$BitArray;->set(J)Z

    move-result v11

    or-int/2addr v4, v11

    .line 54
    add-int/lit8 v10, v10, 0x1

    goto :goto_0

    .line 62
    .end local v5    # "combinedHash":I
    :cond_1
    return v4
.end method
