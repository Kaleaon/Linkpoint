.class public final Lcom/vivox/service/vx_subscription_type;
.super Ljava/lang/Object;
.source "vx_subscription_type.java"


# static fields
.field public static final subscription_presence:Lcom/vivox/service/vx_subscription_type;

.field private static swigNext:I

.field private static swigValues:[Lcom/vivox/service/vx_subscription_type;


# instance fields
.field private final swigName:Ljava/lang/String;

.field private final swigValue:I


# direct methods
.method static constructor <clinit>()V
    .locals 4

    .prologue
    const/4 v3, 0x0

    .line 4
    new-instance v0, Lcom/vivox/service/vx_subscription_type;

    const-string v1, "subscription_presence"

    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->subscription_presence_get()I

    move-result v2

    invoke-direct {v0, v1, v2}, Lcom/vivox/service/vx_subscription_type;-><init>(Ljava/lang/String;I)V

    sput-object v0, Lcom/vivox/service/vx_subscription_type;->subscription_presence:Lcom/vivox/service/vx_subscription_type;

    .line 5
    sput v3, Lcom/vivox/service/vx_subscription_type;->swigNext:I

    .line 6
    const/4 v0, 0x1

    new-array v0, v0, [Lcom/vivox/service/vx_subscription_type;

    sget-object v1, Lcom/vivox/service/vx_subscription_type;->subscription_presence:Lcom/vivox/service/vx_subscription_type;

    aput-object v1, v0, v3

    sput-object v0, Lcom/vivox/service/vx_subscription_type;->swigValues:[Lcom/vivox/service/vx_subscription_type;

    return-void
.end method

.method private constructor <init>(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 10
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 11
    iput-object p1, p0, Lcom/vivox/service/vx_subscription_type;->swigName:Ljava/lang/String;

    .line 12
    sget v0, Lcom/vivox/service/vx_subscription_type;->swigNext:I

    .line 13
    .local v0, "i":I
    add-int/lit8 v1, v0, 0x1

    sput v1, Lcom/vivox/service/vx_subscription_type;->swigNext:I

    .line 14
    iput v0, p0, Lcom/vivox/service/vx_subscription_type;->swigValue:I

    .line 15
    return-void
.end method

.method private constructor <init>(Ljava/lang/String;I)V
    .locals 1
    .param p1, "str"    # Ljava/lang/String;
    .param p2, "i"    # I

    .prologue
    .line 17
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 18
    iput-object p1, p0, Lcom/vivox/service/vx_subscription_type;->swigName:Ljava/lang/String;

    .line 19
    iput p2, p0, Lcom/vivox/service/vx_subscription_type;->swigValue:I

    .line 20
    add-int/lit8 v0, p2, 0x1

    sput v0, Lcom/vivox/service/vx_subscription_type;->swigNext:I

    .line 21
    return-void
.end method

.method private constructor <init>(Ljava/lang/String;Lcom/vivox/service/vx_subscription_type;)V
    .locals 1
    .param p1, "str"    # Ljava/lang/String;
    .param p2, "com_vivox_service_vx_subscription_type"    # Lcom/vivox/service/vx_subscription_type;

    .prologue
    .line 23
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 24
    iput-object p1, p0, Lcom/vivox/service/vx_subscription_type;->swigName:Ljava/lang/String;

    .line 25
    iget v0, p2, Lcom/vivox/service/vx_subscription_type;->swigValue:I

    iput v0, p0, Lcom/vivox/service/vx_subscription_type;->swigValue:I

    .line 26
    iget v0, p0, Lcom/vivox/service/vx_subscription_type;->swigValue:I

    add-int/lit8 v0, v0, 0x1

    sput v0, Lcom/vivox/service/vx_subscription_type;->swigNext:I

    .line 27
    return-void
.end method

.method public static swigToEnum(I)Lcom/vivox/service/vx_subscription_type;
    .locals 4
    .param p0, "i"    # I

    .prologue
    .line 30
    sget-object v1, Lcom/vivox/service/vx_subscription_type;->swigValues:[Lcom/vivox/service/vx_subscription_type;

    array-length v1, v1

    if-ge p0, v1, :cond_0

    if-ltz p0, :cond_0

    sget-object v1, Lcom/vivox/service/vx_subscription_type;->swigValues:[Lcom/vivox/service/vx_subscription_type;

    aget-object v1, v1, p0

    iget v1, v1, Lcom/vivox/service/vx_subscription_type;->swigValue:I

    if-ne v1, p0, :cond_0

    .line 31
    sget-object v1, Lcom/vivox/service/vx_subscription_type;->swigValues:[Lcom/vivox/service/vx_subscription_type;

    aget-object v1, v1, p0

    .line 35
    :goto_0
    return-object v1

    .line 33
    :cond_0
    const/4 v0, 0x0

    .local v0, "i2":I
    :goto_1
    sget-object v1, Lcom/vivox/service/vx_subscription_type;->swigValues:[Lcom/vivox/service/vx_subscription_type;

    array-length v1, v1

    if-ge v0, v1, :cond_2

    .line 34
    sget-object v1, Lcom/vivox/service/vx_subscription_type;->swigValues:[Lcom/vivox/service/vx_subscription_type;

    aget-object v1, v1, v0

    iget v1, v1, Lcom/vivox/service/vx_subscription_type;->swigValue:I

    if-ne v1, p0, :cond_1

    .line 35
    sget-object v1, Lcom/vivox/service/vx_subscription_type;->swigValues:[Lcom/vivox/service/vx_subscription_type;

    aget-object v1, v1, v0

    goto :goto_0

    .line 33
    :cond_1
    add-int/lit8 v0, v0, 0x1

    goto :goto_1

    .line 38
    :cond_2
    new-instance v1, Ljava/lang/IllegalArgumentException;

    new-instance v2, Ljava/lang/StringBuilder;

    invoke-direct {v2}, Ljava/lang/StringBuilder;-><init>()V

    const-string v3, "No enum "

    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v2

    const-class v3, Lcom/vivox/service/vx_subscription_type;

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
    .line 42
    iget v0, p0, Lcom/vivox/service/vx_subscription_type;->swigValue:I

    return v0
.end method

.method public toString()Ljava/lang/String;
    .locals 1

    .prologue
    .line 46
    iget-object v0, p0, Lcom/vivox/service/vx_subscription_type;->swigName:Ljava/lang/String;

    return-object v0
.end method
