.class final enum Lcom/google/common/hash/BloomFilterStrategies$2;
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
    .line 92
    const/4 v0, 0x0

    invoke-direct {p0, p1, p2, v0}, Lcom/google/common/hash/BloomFilterStrategies;-><init>(Ljava/lang/String;ILcom/google/common/hash/BloomFilterStrategies$1;)V

    return-void
.end method

.method private lowerEight([B)J
    .locals 8
    .param p1, "bytes"    # [B

    .prologue
    .line 131
    const/4 v0, 0x7

    aget-byte v0, p1, v0

    const/4 v1, 0x6

    aget-byte v1, p1, v1

    const/4 v2, 0x5

    aget-byte v2, p1, v2

    const/4 v3, 0x4

    aget-byte v3, p1, v3

    const/4 v4, 0x3

    aget-byte v4, p1, v4

    const/4 v5, 0x2

    aget-byte v5, p1, v5

    const/4 v6, 0x1

    aget-byte v6, p1, v6

    const/4 v7, 0x0

    aget-byte v7, p1, v7

    invoke-static/range {v0 .. v7}, Lcom/google/common/primitives/Longs;->fromBytes(BBBBBBBB)J

    move-result-wide v0

    return-wide v0
.end method

.method private upperEight([B)J
    .locals 8
    .param p1, "bytes"    # [B

    .prologue
    .line 136
    const/16 v0, 0xf

    aget-byte v0, p1, v0

    const/16 v1, 0xe

    aget-byte v1, p1, v1

    const/16 v2, 0xd

    aget-byte v2, p1, v2

    const/16 v3, 0xc

    aget-byte v3, p1, v3

    const/16 v4, 0xb

    aget-byte v4, p1, v4

    const/16 v5, 0xa

    aget-byte v5, p1, v5

    const/16 v6, 0x9

    aget-byte v6, p1, v6

    const/16 v7, 0x8

    aget-byte v7, p1, v7

    invoke-static/range {v0 .. v7}, Lcom/google/common/primitives/Longs;->fromBytes(BBBBBBBB)J

    move-result-wide v0

    return-wide v0
.end method


# virtual methods
.method public mightContain(Ljava/lang/Object;Lcom/google/common/hash/Funnel;ILcom/google/common/hash/BloomFilterStrategies$BitArray;)Z
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
    .line 114
    .local p1, "object":Ljava/lang/Object;, "TT;"
    .local p2, "funnel":Lcom/google/common/hash/Funnel;, "Lcom/google/common/hash/Funnel<-TT;>;"
    invoke-virtual/range {p4 .. p4}, Lcom/google/common/hash/BloomFilterStrategies$BitArray;->bitSize()J

    move-result-wide v2

    .line 115
    .local v2, "bitSize":J
    invoke-static {}, Lcom/google/common/hash/Hashing;->murmur3_128()Lcom/google/common/hash/HashFunction;

    move-result-object v12

    move-object/from16 v0, p2

    invoke-interface {v12, p1, v0}, Lcom/google/common/hash/HashFunction;->hashObject(Ljava/lang/Object;Lcom/google/common/hash/Funnel;)Lcom/google/common/hash/HashCode;

    move-result-object v12

    invoke-virtual {v12}, Lcom/google/common/hash/HashCode;->getBytesInternal()[B

    move-result-object v4

    .line 116
    .local v4, "bytes":[B
    invoke-direct {p0, v4}, Lcom/google/common/hash/BloomFilterStrategies$2;->lowerEight([B)J

    move-result-wide v8

    .line 117
    .local v8, "hash1":J
    invoke-direct {p0, v4}, Lcom/google/common/hash/BloomFilterStrategies$2;->upperEight([B)J

    move-result-wide v10

    .line 119
    .local v10, "hash2":J
    move-wide v6, v8

    .line 120
    .local v6, "combinedHash":J
    const/4 v5, 0x0

    .local v5, "i":I
    :goto_0
    move/from16 v0, p3

    if-ge v5, v0, :cond_1

    .line 122
    const-wide v12, 0x7fffffffffffffffL

    and-long/2addr v12, v6

    rem-long/2addr v12, v2

    move-object/from16 v0, p4

    invoke-virtual {v0, v12, v13}, Lcom/google/common/hash/BloomFilterStrategies$BitArray;->get(J)Z

    move-result v12

    if-nez v12, :cond_0

    .line 123
    const/4 v12, 0x0

    .line 127
    :goto_1
    return v12

    .line 125
    :cond_0
    add-long/2addr v6, v10

    .line 120
    add-int/lit8 v5, v5, 0x1

    goto :goto_0

    .line 127
    :cond_1
    const/4 v12, 0x1

    goto :goto_1
.end method

.method public put(Ljava/lang/Object;Lcom/google/common/hash/Funnel;ILcom/google/common/hash/BloomFilterStrategies$BitArray;)Z
    .locals 16
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
    .line 96
    .local p1, "object":Ljava/lang/Object;, "TT;"
    .local p2, "funnel":Lcom/google/common/hash/Funnel;, "Lcom/google/common/hash/Funnel<-TT;>;"
    invoke-virtual/range {p4 .. p4}, Lcom/google/common/hash/BloomFilterStrategies$BitArray;->bitSize()J

    move-result-wide v2

    .line 97
    .local v2, "bitSize":J
    invoke-static {}, Lcom/google/common/hash/Hashing;->murmur3_128()Lcom/google/common/hash/HashFunction;

    move-result-object v13

    move-object/from16 v0, p1

    move-object/from16 v1, p2

    invoke-interface {v13, v0, v1}, Lcom/google/common/hash/HashFunction;->hashObject(Ljava/lang/Object;Lcom/google/common/hash/Funnel;)Lcom/google/common/hash/HashCode;

    move-result-object v13

    invoke-virtual {v13}, Lcom/google/common/hash/HashCode;->getBytesInternal()[B

    move-result-object v5

    .line 98
    .local v5, "bytes":[B
    move-object/from16 v0, p0

    invoke-direct {v0, v5}, Lcom/google/common/hash/BloomFilterStrategies$2;->lowerEight([B)J

    move-result-wide v8

    .line 99
    .local v8, "hash1":J
    move-object/from16 v0, p0

    invoke-direct {v0, v5}, Lcom/google/common/hash/BloomFilterStrategies$2;->upperEight([B)J

    move-result-wide v10

    .line 101
    .local v10, "hash2":J
    const/4 v4, 0x0

    .line 102
    .local v4, "bitsChanged":Z
    move-wide v6, v8

    .line 103
    .local v6, "combinedHash":J
    const/4 v12, 0x0

    .local v12, "i":I
    :goto_0
    move/from16 v0, p3

    if-ge v12, v0, :cond_0

    .line 105
    const-wide v14, 0x7fffffffffffffffL

    and-long/2addr v14, v6

    rem-long/2addr v14, v2

    move-object/from16 v0, p4

    invoke-virtual {v0, v14, v15}, Lcom/google/common/hash/BloomFilterStrategies$BitArray;->set(J)Z

    move-result v13

    or-int/2addr v4, v13

    .line 106
    add-long/2addr v6, v10

    .line 103
    add-int/lit8 v12, v12, 0x1

    goto :goto_0

    .line 108
    :cond_0
    return v4
.end method
