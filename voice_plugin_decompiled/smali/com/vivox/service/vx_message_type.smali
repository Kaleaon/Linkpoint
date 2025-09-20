.class public final Lcom/vivox/service/vx_message_type;
.super Ljava/lang/Object;
.source "vx_message_type.java"


# static fields
.field public static final msg_event:Lcom/vivox/service/vx_message_type;

.field public static final msg_none:Lcom/vivox/service/vx_message_type;

.field public static final msg_request:Lcom/vivox/service/vx_message_type;

.field public static final msg_response:Lcom/vivox/service/vx_message_type;

.field private static swigNext:I

.field private static swigValues:[Lcom/vivox/service/vx_message_type;


# instance fields
.field private final swigName:Ljava/lang/String;

.field private final swigValue:I


# direct methods
.method static constructor <clinit>()V
    .locals 4

    .prologue
    const/4 v3, 0x0

    .line 4
    sput v3, Lcom/vivox/service/vx_message_type;->swigNext:I

    .line 6
    new-instance v0, Lcom/vivox/service/vx_message_type;

    const-string v1, "msg_none"

    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->msg_none_get()I

    move-result v2

    invoke-direct {v0, v1, v2}, Lcom/vivox/service/vx_message_type;-><init>(Ljava/lang/String;I)V

    sput-object v0, Lcom/vivox/service/vx_message_type;->msg_none:Lcom/vivox/service/vx_message_type;

    .line 7
    new-instance v0, Lcom/vivox/service/vx_message_type;

    const-string v1, "msg_request"

    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->msg_request_get()I

    move-result v2

    invoke-direct {v0, v1, v2}, Lcom/vivox/service/vx_message_type;-><init>(Ljava/lang/String;I)V

    sput-object v0, Lcom/vivox/service/vx_message_type;->msg_request:Lcom/vivox/service/vx_message_type;

    .line 8
    new-instance v0, Lcom/vivox/service/vx_message_type;

    const-string v1, "msg_response"

    invoke-direct {v0, v1}, Lcom/vivox/service/vx_message_type;-><init>(Ljava/lang/String;)V

    sput-object v0, Lcom/vivox/service/vx_message_type;->msg_response:Lcom/vivox/service/vx_message_type;

    .line 9
    new-instance v0, Lcom/vivox/service/vx_message_type;

    const-string v1, "msg_event"

    invoke-direct {v0, v1}, Lcom/vivox/service/vx_message_type;-><init>(Ljava/lang/String;)V

    sput-object v0, Lcom/vivox/service/vx_message_type;->msg_event:Lcom/vivox/service/vx_message_type;

    .line 11
    const/4 v0, 0x4

    new-array v0, v0, [Lcom/vivox/service/vx_message_type;

    sget-object v1, Lcom/vivox/service/vx_message_type;->msg_none:Lcom/vivox/service/vx_message_type;

    aput-object v1, v0, v3

    const/4 v1, 0x1

    sget-object v2, Lcom/vivox/service/vx_message_type;->msg_request:Lcom/vivox/service/vx_message_type;

    aput-object v2, v0, v1

    const/4 v1, 0x2

    sget-object v2, Lcom/vivox/service/vx_message_type;->msg_response:Lcom/vivox/service/vx_message_type;

    aput-object v2, v0, v1

    const/4 v1, 0x3

    sget-object v2, Lcom/vivox/service/vx_message_type;->msg_event:Lcom/vivox/service/vx_message_type;

    aput-object v2, v0, v1

    sput-object v0, Lcom/vivox/service/vx_message_type;->swigValues:[Lcom/vivox/service/vx_message_type;

    return-void
.end method

.method private constructor <init>(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 16
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 17
    iput-object p1, p0, Lcom/vivox/service/vx_message_type;->swigName:Ljava/lang/String;

    .line 18
    sget v0, Lcom/vivox/service/vx_message_type;->swigNext:I

    .line 19
    .local v0, "i":I
    add-int/lit8 v1, v0, 0x1

    sput v1, Lcom/vivox/service/vx_message_type;->swigNext:I

    .line 20
    iput v0, p0, Lcom/vivox/service/vx_message_type;->swigValue:I

    .line 21
    return-void
.end method

.method private constructor <init>(Ljava/lang/String;I)V
    .locals 1
    .param p1, "str"    # Ljava/lang/String;
    .param p2, "i"    # I

    .prologue
    .line 23
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 24
    iput-object p1, p0, Lcom/vivox/service/vx_message_type;->swigName:Ljava/lang/String;

    .line 25
    iput p2, p0, Lcom/vivox/service/vx_message_type;->swigValue:I

    .line 26
    add-int/lit8 v0, p2, 0x1

    sput v0, Lcom/vivox/service/vx_message_type;->swigNext:I

    .line 27
    return-void
.end method

.method private constructor <init>(Ljava/lang/String;Lcom/vivox/service/vx_message_type;)V
    .locals 1
    .param p1, "str"    # Ljava/lang/String;
    .param p2, "com_vivox_service_vx_message_type"    # Lcom/vivox/service/vx_message_type;

    .prologue
    .line 29
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 30
    iput-object p1, p0, Lcom/vivox/service/vx_message_type;->swigName:Ljava/lang/String;

    .line 31
    iget v0, p2, Lcom/vivox/service/vx_message_type;->swigValue:I

    iput v0, p0, Lcom/vivox/service/vx_message_type;->swigValue:I

    .line 32
    iget v0, p0, Lcom/vivox/service/vx_message_type;->swigValue:I

    add-int/lit8 v0, v0, 0x1

    sput v0, Lcom/vivox/service/vx_message_type;->swigNext:I

    .line 33
    return-void
.end method

.method public static swigToEnum(I)Lcom/vivox/service/vx_message_type;
    .locals 4
    .param p0, "i"    # I

    .prologue
    .line 36
    sget-object v1, Lcom/vivox/service/vx_message_type;->swigValues:[Lcom/vivox/service/vx_message_type;

    array-length v1, v1

    if-ge p0, v1, :cond_0

    if-ltz p0, :cond_0

    sget-object v1, Lcom/vivox/service/vx_message_type;->swigValues:[Lcom/vivox/service/vx_message_type;

    aget-object v1, v1, p0

    iget v1, v1, Lcom/vivox/service/vx_message_type;->swigValue:I

    if-ne v1, p0, :cond_0

    .line 37
    sget-object v1, Lcom/vivox/service/vx_message_type;->swigValues:[Lcom/vivox/service/vx_message_type;

    aget-object v1, v1, p0

    .line 41
    :goto_0
    return-object v1

    .line 39
    :cond_0
    const/4 v0, 0x0

    .local v0, "i2":I
    :goto_1
    sget-object v1, Lcom/vivox/service/vx_message_type;->swigValues:[Lcom/vivox/service/vx_message_type;

    array-length v1, v1

    if-ge v0, v1, :cond_2

    .line 40
    sget-object v1, Lcom/vivox/service/vx_message_type;->swigValues:[Lcom/vivox/service/vx_message_type;

    aget-object v1, v1, v0

    iget v1, v1, Lcom/vivox/service/vx_message_type;->swigValue:I

    if-ne v1, p0, :cond_1

    .line 41
    sget-object v1, Lcom/vivox/service/vx_message_type;->swigValues:[Lcom/vivox/service/vx_message_type;

    aget-object v1, v1, v0

    goto :goto_0

    .line 39
    :cond_1
    add-int/lit8 v0, v0, 0x1

    goto :goto_1

    .line 44
    :cond_2
    new-instance v1, Ljava/lang/IllegalArgumentException;

    new-instance v2, Ljava/lang/StringBuilder;

    invoke-direct {v2}, Ljava/lang/StringBuilder;-><init>()V

    const-string v3, "No enum "

    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v2

    const-class v3, Lcom/vivox/service/vx_message_type;

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
    .line 48
    iget v0, p0, Lcom/vivox/service/vx_message_type;->swigValue:I

    return v0
.end method

.method public toString()Ljava/lang/String;
    .locals 1

    .prologue
    .line 52
    iget-object v0, p0, Lcom/vivox/service/vx_message_type;->swigName:Ljava/lang/String;

    return-object v0
.end method
