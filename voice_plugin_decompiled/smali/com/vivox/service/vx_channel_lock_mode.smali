.class public final Lcom/vivox/service/vx_channel_lock_mode;
.super Ljava/lang/Object;
.source "vx_channel_lock_mode.java"


# static fields
.field public static final channel_lock:Lcom/vivox/service/vx_channel_lock_mode;

.field public static final channel_unlock:Lcom/vivox/service/vx_channel_lock_mode;

.field private static swigNext:I

.field private static swigValues:[Lcom/vivox/service/vx_channel_lock_mode;


# instance fields
.field private final swigName:Ljava/lang/String;

.field private final swigValue:I


# direct methods
.method static constructor <clinit>()V
    .locals 4

    .prologue
    const/4 v3, 0x0

    .line 4
    new-instance v0, Lcom/vivox/service/vx_channel_lock_mode;

    const-string v1, "channel_lock"

    invoke-direct {v0, v1}, Lcom/vivox/service/vx_channel_lock_mode;-><init>(Ljava/lang/String;)V

    sput-object v0, Lcom/vivox/service/vx_channel_lock_mode;->channel_lock:Lcom/vivox/service/vx_channel_lock_mode;

    .line 5
    new-instance v0, Lcom/vivox/service/vx_channel_lock_mode;

    const-string v1, "channel_unlock"

    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->channel_unlock_get()I

    move-result v2

    invoke-direct {v0, v1, v2}, Lcom/vivox/service/vx_channel_lock_mode;-><init>(Ljava/lang/String;I)V

    sput-object v0, Lcom/vivox/service/vx_channel_lock_mode;->channel_unlock:Lcom/vivox/service/vx_channel_lock_mode;

    .line 6
    sput v3, Lcom/vivox/service/vx_channel_lock_mode;->swigNext:I

    .line 7
    const/4 v0, 0x2

    new-array v0, v0, [Lcom/vivox/service/vx_channel_lock_mode;

    sget-object v1, Lcom/vivox/service/vx_channel_lock_mode;->channel_unlock:Lcom/vivox/service/vx_channel_lock_mode;

    aput-object v1, v0, v3

    const/4 v1, 0x1

    sget-object v2, Lcom/vivox/service/vx_channel_lock_mode;->channel_lock:Lcom/vivox/service/vx_channel_lock_mode;

    aput-object v2, v0, v1

    sput-object v0, Lcom/vivox/service/vx_channel_lock_mode;->swigValues:[Lcom/vivox/service/vx_channel_lock_mode;

    return-void
.end method

.method private constructor <init>(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 11
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 12
    iput-object p1, p0, Lcom/vivox/service/vx_channel_lock_mode;->swigName:Ljava/lang/String;

    .line 13
    sget v0, Lcom/vivox/service/vx_channel_lock_mode;->swigNext:I

    .line 14
    .local v0, "i":I
    add-int/lit8 v1, v0, 0x1

    sput v1, Lcom/vivox/service/vx_channel_lock_mode;->swigNext:I

    .line 15
    iput v0, p0, Lcom/vivox/service/vx_channel_lock_mode;->swigValue:I

    .line 16
    return-void
.end method

.method private constructor <init>(Ljava/lang/String;I)V
    .locals 1
    .param p1, "str"    # Ljava/lang/String;
    .param p2, "i"    # I

    .prologue
    .line 18
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 19
    iput-object p1, p0, Lcom/vivox/service/vx_channel_lock_mode;->swigName:Ljava/lang/String;

    .line 20
    iput p2, p0, Lcom/vivox/service/vx_channel_lock_mode;->swigValue:I

    .line 21
    add-int/lit8 v0, p2, 0x1

    sput v0, Lcom/vivox/service/vx_channel_lock_mode;->swigNext:I

    .line 22
    return-void
.end method

.method private constructor <init>(Ljava/lang/String;Lcom/vivox/service/vx_channel_lock_mode;)V
    .locals 1
    .param p1, "str"    # Ljava/lang/String;
    .param p2, "com_vivox_service_vx_channel_lock_mode"    # Lcom/vivox/service/vx_channel_lock_mode;

    .prologue
    .line 24
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 25
    iput-object p1, p0, Lcom/vivox/service/vx_channel_lock_mode;->swigName:Ljava/lang/String;

    .line 26
    iget v0, p2, Lcom/vivox/service/vx_channel_lock_mode;->swigValue:I

    iput v0, p0, Lcom/vivox/service/vx_channel_lock_mode;->swigValue:I

    .line 27
    iget v0, p0, Lcom/vivox/service/vx_channel_lock_mode;->swigValue:I

    add-int/lit8 v0, v0, 0x1

    sput v0, Lcom/vivox/service/vx_channel_lock_mode;->swigNext:I

    .line 28
    return-void
.end method

.method public static swigToEnum(I)Lcom/vivox/service/vx_channel_lock_mode;
    .locals 4
    .param p0, "i"    # I

    .prologue
    .line 31
    sget-object v1, Lcom/vivox/service/vx_channel_lock_mode;->swigValues:[Lcom/vivox/service/vx_channel_lock_mode;

    array-length v1, v1

    if-ge p0, v1, :cond_0

    if-ltz p0, :cond_0

    sget-object v1, Lcom/vivox/service/vx_channel_lock_mode;->swigValues:[Lcom/vivox/service/vx_channel_lock_mode;

    aget-object v1, v1, p0

    iget v1, v1, Lcom/vivox/service/vx_channel_lock_mode;->swigValue:I

    if-ne v1, p0, :cond_0

    .line 32
    sget-object v1, Lcom/vivox/service/vx_channel_lock_mode;->swigValues:[Lcom/vivox/service/vx_channel_lock_mode;

    aget-object v1, v1, p0

    .line 36
    :goto_0
    return-object v1

    .line 34
    :cond_0
    const/4 v0, 0x0

    .local v0, "i2":I
    :goto_1
    sget-object v1, Lcom/vivox/service/vx_channel_lock_mode;->swigValues:[Lcom/vivox/service/vx_channel_lock_mode;

    array-length v1, v1

    if-ge v0, v1, :cond_2

    .line 35
    sget-object v1, Lcom/vivox/service/vx_channel_lock_mode;->swigValues:[Lcom/vivox/service/vx_channel_lock_mode;

    aget-object v1, v1, v0

    iget v1, v1, Lcom/vivox/service/vx_channel_lock_mode;->swigValue:I

    if-ne v1, p0, :cond_1

    .line 36
    sget-object v1, Lcom/vivox/service/vx_channel_lock_mode;->swigValues:[Lcom/vivox/service/vx_channel_lock_mode;

    aget-object v1, v1, v0

    goto :goto_0

    .line 34
    :cond_1
    add-int/lit8 v0, v0, 0x1

    goto :goto_1

    .line 39
    :cond_2
    new-instance v1, Ljava/lang/IllegalArgumentException;

    new-instance v2, Ljava/lang/StringBuilder;

    invoke-direct {v2}, Ljava/lang/StringBuilder;-><init>()V

    const-string v3, "No enum "

    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v2

    const-class v3, Lcom/vivox/service/vx_channel_lock_mode;

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
    .line 43
    iget v0, p0, Lcom/vivox/service/vx_channel_lock_mode;->swigValue:I

    return v0
.end method

.method public toString()Ljava/lang/String;
    .locals 1

    .prologue
    .line 47
    iget-object v0, p0, Lcom/vivox/service/vx_channel_lock_mode;->swigName:Ljava/lang/String;

    return-object v0
.end method
