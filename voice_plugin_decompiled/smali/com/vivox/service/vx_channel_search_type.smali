.class public final Lcom/vivox/service/vx_channel_search_type;
.super Ljava/lang/Object;
.source "vx_channel_search_type.java"


# static fields
.field public static final channel_search_type_all:Lcom/vivox/service/vx_channel_search_type;

.field public static final channel_search_type_non_positional:Lcom/vivox/service/vx_channel_search_type;

.field public static final channel_search_type_positional:Lcom/vivox/service/vx_channel_search_type;

.field private static swigNext:I

.field private static swigValues:[Lcom/vivox/service/vx_channel_search_type;


# instance fields
.field private final swigName:Ljava/lang/String;

.field private final swigValue:I


# direct methods
.method static constructor <clinit>()V
    .locals 4

    .prologue
    const/4 v3, 0x0

    .line 4
    new-instance v0, Lcom/vivox/service/vx_channel_search_type;

    const-string v1, "channel_search_type_all"

    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->channel_search_type_all_get()I

    move-result v2

    invoke-direct {v0, v1, v2}, Lcom/vivox/service/vx_channel_search_type;-><init>(Ljava/lang/String;I)V

    sput-object v0, Lcom/vivox/service/vx_channel_search_type;->channel_search_type_all:Lcom/vivox/service/vx_channel_search_type;

    .line 5
    new-instance v0, Lcom/vivox/service/vx_channel_search_type;

    const-string v1, "channel_search_type_non_positional"

    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->channel_search_type_non_positional_get()I

    move-result v2

    invoke-direct {v0, v1, v2}, Lcom/vivox/service/vx_channel_search_type;-><init>(Ljava/lang/String;I)V

    sput-object v0, Lcom/vivox/service/vx_channel_search_type;->channel_search_type_non_positional:Lcom/vivox/service/vx_channel_search_type;

    .line 6
    new-instance v0, Lcom/vivox/service/vx_channel_search_type;

    const-string v1, "channel_search_type_positional"

    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->channel_search_type_positional_get()I

    move-result v2

    invoke-direct {v0, v1, v2}, Lcom/vivox/service/vx_channel_search_type;-><init>(Ljava/lang/String;I)V

    sput-object v0, Lcom/vivox/service/vx_channel_search_type;->channel_search_type_positional:Lcom/vivox/service/vx_channel_search_type;

    .line 7
    sput v3, Lcom/vivox/service/vx_channel_search_type;->swigNext:I

    .line 8
    const/4 v0, 0x3

    new-array v0, v0, [Lcom/vivox/service/vx_channel_search_type;

    sget-object v1, Lcom/vivox/service/vx_channel_search_type;->channel_search_type_all:Lcom/vivox/service/vx_channel_search_type;

    aput-object v1, v0, v3

    const/4 v1, 0x1

    sget-object v2, Lcom/vivox/service/vx_channel_search_type;->channel_search_type_non_positional:Lcom/vivox/service/vx_channel_search_type;

    aput-object v2, v0, v1

    const/4 v1, 0x2

    sget-object v2, Lcom/vivox/service/vx_channel_search_type;->channel_search_type_positional:Lcom/vivox/service/vx_channel_search_type;

    aput-object v2, v0, v1

    sput-object v0, Lcom/vivox/service/vx_channel_search_type;->swigValues:[Lcom/vivox/service/vx_channel_search_type;

    return-void
.end method

.method private constructor <init>(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 12
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 13
    iput-object p1, p0, Lcom/vivox/service/vx_channel_search_type;->swigName:Ljava/lang/String;

    .line 14
    sget v0, Lcom/vivox/service/vx_channel_search_type;->swigNext:I

    .line 15
    .local v0, "i":I
    add-int/lit8 v1, v0, 0x1

    sput v1, Lcom/vivox/service/vx_channel_search_type;->swigNext:I

    .line 16
    iput v0, p0, Lcom/vivox/service/vx_channel_search_type;->swigValue:I

    .line 17
    return-void
.end method

.method private constructor <init>(Ljava/lang/String;I)V
    .locals 1
    .param p1, "str"    # Ljava/lang/String;
    .param p2, "i"    # I

    .prologue
    .line 19
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 20
    iput-object p1, p0, Lcom/vivox/service/vx_channel_search_type;->swigName:Ljava/lang/String;

    .line 21
    iput p2, p0, Lcom/vivox/service/vx_channel_search_type;->swigValue:I

    .line 22
    add-int/lit8 v0, p2, 0x1

    sput v0, Lcom/vivox/service/vx_channel_search_type;->swigNext:I

    .line 23
    return-void
.end method

.method private constructor <init>(Ljava/lang/String;Lcom/vivox/service/vx_channel_search_type;)V
    .locals 1
    .param p1, "str"    # Ljava/lang/String;
    .param p2, "com_vivox_service_vx_channel_search_type"    # Lcom/vivox/service/vx_channel_search_type;

    .prologue
    .line 25
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 26
    iput-object p1, p0, Lcom/vivox/service/vx_channel_search_type;->swigName:Ljava/lang/String;

    .line 27
    iget v0, p2, Lcom/vivox/service/vx_channel_search_type;->swigValue:I

    iput v0, p0, Lcom/vivox/service/vx_channel_search_type;->swigValue:I

    .line 28
    iget v0, p0, Lcom/vivox/service/vx_channel_search_type;->swigValue:I

    add-int/lit8 v0, v0, 0x1

    sput v0, Lcom/vivox/service/vx_channel_search_type;->swigNext:I

    .line 29
    return-void
.end method

.method public static swigToEnum(I)Lcom/vivox/service/vx_channel_search_type;
    .locals 4
    .param p0, "i"    # I

    .prologue
    .line 32
    sget-object v1, Lcom/vivox/service/vx_channel_search_type;->swigValues:[Lcom/vivox/service/vx_channel_search_type;

    array-length v1, v1

    if-ge p0, v1, :cond_0

    if-ltz p0, :cond_0

    sget-object v1, Lcom/vivox/service/vx_channel_search_type;->swigValues:[Lcom/vivox/service/vx_channel_search_type;

    aget-object v1, v1, p0

    iget v1, v1, Lcom/vivox/service/vx_channel_search_type;->swigValue:I

    if-ne v1, p0, :cond_0

    .line 33
    sget-object v1, Lcom/vivox/service/vx_channel_search_type;->swigValues:[Lcom/vivox/service/vx_channel_search_type;

    aget-object v1, v1, p0

    .line 37
    :goto_0
    return-object v1

    .line 35
    :cond_0
    const/4 v0, 0x0

    .local v0, "i2":I
    :goto_1
    sget-object v1, Lcom/vivox/service/vx_channel_search_type;->swigValues:[Lcom/vivox/service/vx_channel_search_type;

    array-length v1, v1

    if-ge v0, v1, :cond_2

    .line 36
    sget-object v1, Lcom/vivox/service/vx_channel_search_type;->swigValues:[Lcom/vivox/service/vx_channel_search_type;

    aget-object v1, v1, v0

    iget v1, v1, Lcom/vivox/service/vx_channel_search_type;->swigValue:I

    if-ne v1, p0, :cond_1

    .line 37
    sget-object v1, Lcom/vivox/service/vx_channel_search_type;->swigValues:[Lcom/vivox/service/vx_channel_search_type;

    aget-object v1, v1, v0

    goto :goto_0

    .line 35
    :cond_1
    add-int/lit8 v0, v0, 0x1

    goto :goto_1

    .line 40
    :cond_2
    new-instance v1, Ljava/lang/IllegalArgumentException;

    new-instance v2, Ljava/lang/StringBuilder;

    invoke-direct {v2}, Ljava/lang/StringBuilder;-><init>()V

    const-string v3, "No enum "

    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v2

    const-class v3, Lcom/vivox/service/vx_channel_search_type;

    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    move-result-object v2

    const-string v3, " with value "

    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v2

    invoke-virtual {v2, p0}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    move-result-object v2

    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v2

    invoke-direct {v1, v2}, Ljava/lang/IllegalArgumentException;-><init>(Ljava/lang/String;)V

    throw v1
.end method


# virtual methods
.method public final swigValue()I
    .locals 1

    .prologue
    .line 44
    iget v0, p0, Lcom/vivox/service/vx_channel_search_type;->swigValue:I

    return v0
.end method

.method public toString()Ljava/lang/String;
    .locals 1

    .prologue
    .line 48
    iget-object v0, p0, Lcom/vivox/service/vx_channel_search_type;->swigName:Ljava/lang/String;

    return-object v0
.end method
