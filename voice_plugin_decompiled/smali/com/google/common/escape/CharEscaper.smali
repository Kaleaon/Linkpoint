.class public abstract Lcom/google/common/escape/CharEscaper;
.super Lcom/google/common/escape/Escaper;
.source "CharEscaper.java"


# annotations
.annotation build Lcom/google/common/annotations/Beta;
.end annotation

.annotation build Lcom/google/common/annotations/GwtCompatible;
.end annotation


# static fields
.field private static final DEST_PAD_MULTIPLIER:I = 0x2


# direct methods
.method protected constructor <init>()V
    .locals 0

    .prologue
    .line 49
    invoke-direct {p0}, Lcom/google/common/escape/Escaper;-><init>()V

    return-void
.end method

.method private static growBuffer([CII)[C
    .locals 2
    .param p0, "dest"    # [C
    .param p1, "index"    # I
    .param p2, "size"    # I

    .prologue
    const/4 v1, 0x0

    .line 163
    new-array v0, p2, [C

    .line 164
    .local v0, "copy":[C
    if-lez p1, :cond_0

    .line 165
    invoke-static {p0, v1, v0, v1, p1}, Ljava/lang/System;->arraycopy(Ljava/lang/Object;ILjava/lang/Object;II)V

    .line 167
    :cond_0
    return-object v0
.end method


# virtual methods
.method public escape(Ljava/lang/String;)Ljava/lang/String;
    .locals 3
    .param p1, "string"    # Ljava/lang/String;

    .prologue
    .line 59
    invoke-static {p1}, Lcom/google/common/base/Preconditions;->checkNotNull(Ljava/lang/Object;)Ljava/lang/Object;

    .line 61
    invoke-virtual {p1}, Ljava/lang/String;->length()I

    move-result v1

    .line 62
    .local v1, "length":I
    const/4 v0, 0x0

    .local v0, "index":I
    :goto_0
    if-ge v0, v1, :cond_0

    .line 63
    invoke-virtual {p1, v0}, Ljava/lang/String;->charAt(I)C

    move-result v2

    invoke-virtual {p0, v2}, Lcom/google/common/escape/CharEscaper;->escape(C)[C

    move-result-object v2

    if-eqz v2, :cond_1

    .line 64
    invoke-virtual {p0, p1, v0}, Lcom/google/common/escape/CharEscaper;->escapeSlow(Ljava/lang/String;I)Ljava/lang/String;

    move-result-object p1

    .line 67
    .end local p1    # "string":Ljava/lang/String;
    :cond_0
    return-object p1

    .line 62
    .restart local p1    # "string":Ljava/lang/String;
    :cond_1
    add-int/lit8 v0, v0, 0x1

    goto :goto_0
.end method

.method protected abstract escape(C)[C
.end method

.method protected final escapeSlow(Ljava/lang/String;I)Ljava/lang/String;
    .locals 12
    .param p1, "s"    # Ljava/lang/String;
    .param p2, "index"    # I

    .prologue
    const/4 v11, 0x0

    .line 82
    invoke-virtual {p1}, Ljava/lang/String;->length()I

    move-result v9

    .line 85
    .local v9, "slen":I
    invoke-static {}, Lcom/google/common/escape/Platform;->charBufferFromThreadLocal()[C

    move-result-object v2

    .line 86
    .local v2, "dest":[C
    array-length v4, v2

    .line 87
    .local v4, "destSize":I
    const/4 v3, 0x0

    .line 88
    .local v3, "destIndex":I
    const/4 v5, 0x0

    .line 92
    .local v5, "lastEscape":I
    :goto_0
    if-ge p2, v9, :cond_4

    .line 95
    invoke-virtual {p1, p2}, Ljava/lang/String;->charAt(I)C

    move-result v10

    invoke-virtual {p0, v10}, Lcom/google/common/escape/CharEscaper;->escape(C)[C

    move-result-object v6

    .line 98
    .local v6, "r":[C
    if-nez v6, :cond_0

    .line 92
    :goto_1
    add-int/lit8 p2, p2, 0x1

    goto :goto_0

    .line 100
    :cond_0
    array-length v7, v6

    .line 101
    .local v7, "rlen":I
    sub-int v1, p2, v5

    .line 106
    .local v1, "charsSkipped":I
    add-int v10, v3, v1

    add-int v8, v10, v7

    .line 107
    .local v8, "sizeNeeded":I
    if-ge v4, v8, :cond_1

    .line 108
    sub-int v10, v9, p2

    mul-int/lit8 v10, v10, 0x2

    add-int v4, v8, v10

    .line 109
    invoke-static {v2, v3, v4}, Lcom/google/common/escape/CharEscaper;->growBuffer([CII)[C

    move-result-object v2

    .line 113
    :cond_1
    if-lez v1, :cond_2

    .line 114
    invoke-virtual {p1, v5, p2, v2, v3}, Ljava/lang/String;->getChars(II[CI)V

    .line 115
    add-int/2addr v3, v1

    .line 119
    :cond_2
    if-lez v7, :cond_3

    .line 120
    invoke-static {v6, v11, v2, v3, v7}, Ljava/lang/System;->arraycopy(Ljava/lang/Object;ILjava/lang/Object;II)V

    .line 121
    add-int/2addr v3, v7

    .line 123
    :cond_3
    add-int/lit8 v5, p2, 0x1

    goto :goto_1

    .line 127
    .end local v1    # "charsSkipped":I
    .end local v6    # "r":[C
    .end local v7    # "rlen":I
    .end local v8    # "sizeNeeded":I
    :cond_4
    sub-int v0, v9, v5

    .line 128
    .local v0, "charsLeft":I
    if-lez v0, :cond_6

    .line 129
    add-int v8, v3, v0

    .line 130
    .restart local v8    # "sizeNeeded":I
    if-ge v4, v8, :cond_5

    .line 133
    invoke-static {v2, v3, v8}, Lcom/google/common/escape/CharEscaper;->growBuffer([CII)[C

    move-result-object v2

    .line 135
    :cond_5
    invoke-virtual {p1, v5, v9, v2, v3}, Ljava/lang/String;->getChars(II[CI)V

    .line 136
    move v3, v8

    .line 138
    .end local v8    # "sizeNeeded":I
    :cond_6
    new-instance v10, Ljava/lang/String;

    invoke-direct {v10, v2, v11, v3}, Ljava/lang/String;-><init>([CII)V

    return-object v10
.end method
