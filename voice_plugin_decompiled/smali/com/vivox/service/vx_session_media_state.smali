.class public final Lcom/vivox/service/vx_session_media_state;
.super Ljava/lang/Object;
.source "vx_session_media_state.java"


# static fields
.field public static final session_media_connected:Lcom/vivox/service/vx_session_media_state;

.field public static final session_media_connecting:Lcom/vivox/service/vx_session_media_state;

.field public static final session_media_disconnected:Lcom/vivox/service/vx_session_media_state;

.field public static final session_media_disconnecting:Lcom/vivox/service/vx_session_media_state;

.field public static final session_media_hold:Lcom/vivox/service/vx_session_media_state;

.field public static final session_media_none:Lcom/vivox/service/vx_session_media_state;

.field public static final session_media_refer:Lcom/vivox/service/vx_session_media_state;

.field public static final session_media_ringing:Lcom/vivox/service/vx_session_media_state;

.field private static swigNext:I

.field private static swigValues:[Lcom/vivox/service/vx_session_media_state;


# instance fields
.field private final swigName:Ljava/lang/String;

.field private final swigValue:I


# direct methods
.method static constructor <clinit>()V
    .locals 4

    .prologue
    const/4 v3, 0x0

    .line 5
    sput v3, Lcom/vivox/service/vx_session_media_state;->swigNext:I

    .line 7
    new-instance v0, Lcom/vivox/service/vx_session_media_state;

    const-string v1, "session_media_none"

    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->session_media_none_get()I

    move-result v2

    invoke-direct {v0, v1, v2}, Lcom/vivox/service/vx_session_media_state;-><init>(Ljava/lang/String;I)V

    sput-object v0, Lcom/vivox/service/vx_session_media_state;->session_media_none:Lcom/vivox/service/vx_session_media_state;

    .line 8
    new-instance v0, Lcom/vivox/service/vx_session_media_state;

    const-string v1, "session_media_disconnected"

    invoke-direct {v0, v1}, Lcom/vivox/service/vx_session_media_state;-><init>(Ljava/lang/String;)V

    sput-object v0, Lcom/vivox/service/vx_session_media_state;->session_media_disconnected:Lcom/vivox/service/vx_session_media_state;

    .line 9
    new-instance v0, Lcom/vivox/service/vx_session_media_state;

    const-string v1, "session_media_connected"

    invoke-direct {v0, v1}, Lcom/vivox/service/vx_session_media_state;-><init>(Ljava/lang/String;)V

    sput-object v0, Lcom/vivox/service/vx_session_media_state;->session_media_connected:Lcom/vivox/service/vx_session_media_state;

    .line 10
    new-instance v0, Lcom/vivox/service/vx_session_media_state;

    const-string v1, "session_media_ringing"

    invoke-direct {v0, v1}, Lcom/vivox/service/vx_session_media_state;-><init>(Ljava/lang/String;)V

    sput-object v0, Lcom/vivox/service/vx_session_media_state;->session_media_ringing:Lcom/vivox/service/vx_session_media_state;

    .line 11
    new-instance v0, Lcom/vivox/service/vx_session_media_state;

    const-string v1, "session_media_hold"

    invoke-direct {v0, v1}, Lcom/vivox/service/vx_session_media_state;-><init>(Ljava/lang/String;)V

    sput-object v0, Lcom/vivox/service/vx_session_media_state;->session_media_hold:Lcom/vivox/service/vx_session_media_state;

    .line 12
    new-instance v0, Lcom/vivox/service/vx_session_media_state;

    const-string v1, "session_media_refer"

    invoke-direct {v0, v1}, Lcom/vivox/service/vx_session_media_state;-><init>(Ljava/lang/String;)V

    sput-object v0, Lcom/vivox/service/vx_session_media_state;->session_media_refer:Lcom/vivox/service/vx_session_media_state;

    .line 13
    new-instance v0, Lcom/vivox/service/vx_session_media_state;

    const-string v1, "session_media_connecting"

    invoke-direct {v0, v1}, Lcom/vivox/service/vx_session_media_state;-><init>(Ljava/lang/String;)V

    sput-object v0, Lcom/vivox/service/vx_session_media_state;->session_media_connecting:Lcom/vivox/service/vx_session_media_state;

    .line 14
    new-instance v0, Lcom/vivox/service/vx_session_media_state;

    const-string v1, "session_media_disconnecting"

    invoke-direct {v0, v1}, Lcom/vivox/service/vx_session_media_state;-><init>(Ljava/lang/String;)V

    sput-object v0, Lcom/vivox/service/vx_session_media_state;->session_media_disconnecting:Lcom/vivox/service/vx_session_media_state;

    .line 16
    const/16 v0, 0x8

    new-array v0, v0, [Lcom/vivox/service/vx_session_media_state;

    sget-object v1, Lcom/vivox/service/vx_session_media_state;->session_media_none:Lcom/vivox/service/vx_session_media_state;

    aput-object v1, v0, v3

    const/4 v1, 0x1

    sget-object v2, Lcom/vivox/service/vx_session_media_state;->session_media_disconnected:Lcom/vivox/service/vx_session_media_state;

    aput-object v2, v0, v1

    const/4 v1, 0x2

    sget-object v2, Lcom/vivox/service/vx_session_media_state;->session_media_connected:Lcom/vivox/service/vx_session_media_state;

    aput-object v2, v0, v1

    const/4 v1, 0x3

    sget-object v2, Lcom/vivox/service/vx_session_media_state;->session_media_ringing:Lcom/vivox/service/vx_session_media_state;

    aput-object v2, v0, v1

    const/4 v1, 0x4

    sget-object v2, Lcom/vivox/service/vx_session_media_state;->session_media_hold:Lcom/vivox/service/vx_session_media_state;

    aput-object v2, v0, v1

    const/4 v1, 0x5

    sget-object v2, Lcom/vivox/service/vx_session_media_state;->session_media_refer:Lcom/vivox/service/vx_session_media_state;

    aput-object v2, v0, v1

    const/4 v1, 0x6

    sget-object v2, Lcom/vivox/service/vx_session_media_state;->session_media_connecting:Lcom/vivox/service/vx_session_media_state;

    aput-object v2, v0, v1

    const/4 v1, 0x7

    sget-object v2, Lcom/vivox/service/vx_session_media_state;->session_media_disconnecting:Lcom/vivox/service/vx_session_media_state;

    aput-object v2, v0, v1

    sput-object v0, Lcom/vivox/service/vx_session_media_state;->swigValues:[Lcom/vivox/service/vx_session_media_state;

    return-void
.end method

.method private constructor <init>(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 30
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 31
    iput-object p1, p0, Lcom/vivox/service/vx_session_media_state;->swigName:Ljava/lang/String;

    .line 32
    sget v0, Lcom/vivox/service/vx_session_media_state;->swigNext:I

    .line 33
    .local v0, "i":I
    add-int/lit8 v1, v0, 0x1

    sput v1, Lcom/vivox/service/vx_session_media_state;->swigNext:I

    .line 34
    iput v0, p0, Lcom/vivox/service/vx_session_media_state;->swigValue:I

    .line 35
    return-void
.end method

.method private constructor <init>(Ljava/lang/String;I)V
    .locals 1
    .param p1, "str"    # Ljava/lang/String;
    .param p2, "i"    # I

    .prologue
    .line 37
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 38
    iput-object p1, p0, Lcom/vivox/service/vx_session_media_state;->swigName:Ljava/lang/String;

    .line 39
    iput p2, p0, Lcom/vivox/service/vx_session_media_state;->swigValue:I

    .line 40
    add-int/lit8 v0, p2, 0x1

    sput v0, Lcom/vivox/service/vx_session_media_state;->swigNext:I

    .line 41
    return-void
.end method

.method private constructor <init>(Ljava/lang/String;Lcom/vivox/service/vx_session_media_state;)V
    .locals 1
    .param p1, "str"    # Ljava/lang/String;
    .param p2, "com_vivox_service_vx_session_media_state"    # Lcom/vivox/service/vx_session_media_state;

    .prologue
    .line 43
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 44
    iput-object p1, p0, Lcom/vivox/service/vx_session_media_state;->swigName:Ljava/lang/String;

    .line 45
    iget v0, p2, Lcom/vivox/service/vx_session_media_state;->swigValue:I

    iput v0, p0, Lcom/vivox/service/vx_session_media_state;->swigValue:I

    .line 46
    iget v0, p0, Lcom/vivox/service/vx_session_media_state;->swigValue:I

    add-int/lit8 v0, v0, 0x1

    sput v0, Lcom/vivox/service/vx_session_media_state;->swigNext:I

    .line 47
    return-void
.end method

.method public static swigToEnum(I)Lcom/vivox/service/vx_session_media_state;
    .locals 4
    .param p0, "i"    # I

    .prologue
    .line 50
    sget-object v1, Lcom/vivox/service/vx_session_media_state;->swigValues:[Lcom/vivox/service/vx_session_media_state;

    array-length v1, v1

    if-ge p0, v1, :cond_0

    if-ltz p0, :cond_0

    sget-object v1, Lcom/vivox/service/vx_session_media_state;->swigValues:[Lcom/vivox/service/vx_session_media_state;

    aget-object v1, v1, p0

    iget v1, v1, Lcom/vivox/service/vx_session_media_state;->swigValue:I

    if-ne v1, p0, :cond_0

    .line 51
    sget-object v1, Lcom/vivox/service/vx_session_media_state;->swigValues:[Lcom/vivox/service/vx_session_media_state;

    aget-object v1, v1, p0

    .line 55
    :goto_0
    return-object v1

    .line 53
    :cond_0
    const/4 v0, 0x0

    .local v0, "i2":I
    :goto_1
    sget-object v1, Lcom/vivox/service/vx_session_media_state;->swigValues:[Lcom/vivox/service/vx_session_media_state;

    array-length v1, v1

    if-ge v0, v1, :cond_2

    .line 54
    sget-object v1, Lcom/vivox/service/vx_session_media_state;->swigValues:[Lcom/vivox/service/vx_session_media_state;

    aget-object v1, v1, v0

    iget v1, v1, Lcom/vivox/service/vx_session_media_state;->swigValue:I

    if-ne v1, p0, :cond_1

    .line 55
    sget-object v1, Lcom/vivox/service/vx_session_media_state;->swigValues:[Lcom/vivox/service/vx_session_media_state;

    aget-object v1, v1, v0

    goto :goto_0

    .line 53
    :cond_1
    add-int/lit8 v0, v0, 0x1

    goto :goto_1

    .line 58
    :cond_2
    new-instance v1, Ljava/lang/IllegalArgumentException;

    new-instance v2, Ljava/lang/StringBuilder;

    invoke-direct {v2}, Ljava/lang/StringBuilder;-><init>()V

    const-string v3, "No enum "

    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v2

    const-class v3, Lcom/vivox/service/vx_session_media_state;

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
    .line 62
    iget v0, p0, Lcom/vivox/service/vx_session_media_state;->swigValue:I

    return v0
.end method

.method public toString()Ljava/lang/String;
    .locals 1

    .prologue
    .line 66
    iget-object v0, p0, Lcom/vivox/service/vx_session_media_state;->swigName:Ljava/lang/String;

    return-object v0
.end method
