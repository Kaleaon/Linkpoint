.class public final Lcom/vivox/service/vx_login_state_change_state;
.super Ljava/lang/Object;
.source "vx_login_state_change_state.java"


# static fields
.field public static final login_state_error:Lcom/vivox/service/vx_login_state_change_state;

.field public static final login_state_logged_in:Lcom/vivox/service/vx_login_state_change_state;

.field public static final login_state_logged_out:Lcom/vivox/service/vx_login_state_change_state;

.field public static final login_state_logging_in:Lcom/vivox/service/vx_login_state_change_state;

.field public static final login_state_logging_out:Lcom/vivox/service/vx_login_state_change_state;

.field public static final login_state_resetting:Lcom/vivox/service/vx_login_state_change_state;

.field private static swigNext:I

.field private static swigValues:[Lcom/vivox/service/vx_login_state_change_state;


# instance fields
.field private final swigName:Ljava/lang/String;

.field private final swigValue:I


# direct methods
.method static constructor <clinit>()V
    .locals 4

    .prologue
    const/4 v3, 0x0

    .line 4
    new-instance v0, Lcom/vivox/service/vx_login_state_change_state;

    const-string v1, "login_state_error"

    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->login_state_error_get()I

    move-result v2

    invoke-direct {v0, v1, v2}, Lcom/vivox/service/vx_login_state_change_state;-><init>(Ljava/lang/String;I)V

    sput-object v0, Lcom/vivox/service/vx_login_state_change_state;->login_state_error:Lcom/vivox/service/vx_login_state_change_state;

    .line 5
    new-instance v0, Lcom/vivox/service/vx_login_state_change_state;

    const-string v1, "login_state_logged_in"

    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->login_state_logged_in_get()I

    move-result v2

    invoke-direct {v0, v1, v2}, Lcom/vivox/service/vx_login_state_change_state;-><init>(Ljava/lang/String;I)V

    sput-object v0, Lcom/vivox/service/vx_login_state_change_state;->login_state_logged_in:Lcom/vivox/service/vx_login_state_change_state;

    .line 6
    new-instance v0, Lcom/vivox/service/vx_login_state_change_state;

    const-string v1, "login_state_logged_out"

    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->login_state_logged_out_get()I

    move-result v2

    invoke-direct {v0, v1, v2}, Lcom/vivox/service/vx_login_state_change_state;-><init>(Ljava/lang/String;I)V

    sput-object v0, Lcom/vivox/service/vx_login_state_change_state;->login_state_logged_out:Lcom/vivox/service/vx_login_state_change_state;

    .line 7
    new-instance v0, Lcom/vivox/service/vx_login_state_change_state;

    const-string v1, "login_state_logging_in"

    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->login_state_logging_in_get()I

    move-result v2

    invoke-direct {v0, v1, v2}, Lcom/vivox/service/vx_login_state_change_state;-><init>(Ljava/lang/String;I)V

    sput-object v0, Lcom/vivox/service/vx_login_state_change_state;->login_state_logging_in:Lcom/vivox/service/vx_login_state_change_state;

    .line 8
    new-instance v0, Lcom/vivox/service/vx_login_state_change_state;

    const-string v1, "login_state_logging_out"

    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->login_state_logging_out_get()I

    move-result v2

    invoke-direct {v0, v1, v2}, Lcom/vivox/service/vx_login_state_change_state;-><init>(Ljava/lang/String;I)V

    sput-object v0, Lcom/vivox/service/vx_login_state_change_state;->login_state_logging_out:Lcom/vivox/service/vx_login_state_change_state;

    .line 9
    new-instance v0, Lcom/vivox/service/vx_login_state_change_state;

    const-string v1, "login_state_resetting"

    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->login_state_resetting_get()I

    move-result v2

    invoke-direct {v0, v1, v2}, Lcom/vivox/service/vx_login_state_change_state;-><init>(Ljava/lang/String;I)V

    sput-object v0, Lcom/vivox/service/vx_login_state_change_state;->login_state_resetting:Lcom/vivox/service/vx_login_state_change_state;

    .line 10
    sput v3, Lcom/vivox/service/vx_login_state_change_state;->swigNext:I

    .line 11
    const/4 v0, 0x6

    new-array v0, v0, [Lcom/vivox/service/vx_login_state_change_state;

    sget-object v1, Lcom/vivox/service/vx_login_state_change_state;->login_state_logged_out:Lcom/vivox/service/vx_login_state_change_state;

    aput-object v1, v0, v3

    const/4 v1, 0x1

    sget-object v2, Lcom/vivox/service/vx_login_state_change_state;->login_state_logged_in:Lcom/vivox/service/vx_login_state_change_state;

    aput-object v2, v0, v1

    const/4 v1, 0x2

    sget-object v2, Lcom/vivox/service/vx_login_state_change_state;->login_state_logging_in:Lcom/vivox/service/vx_login_state_change_state;

    aput-object v2, v0, v1

    const/4 v1, 0x3

    sget-object v2, Lcom/vivox/service/vx_login_state_change_state;->login_state_logging_out:Lcom/vivox/service/vx_login_state_change_state;

    aput-object v2, v0, v1

    const/4 v1, 0x4

    sget-object v2, Lcom/vivox/service/vx_login_state_change_state;->login_state_resetting:Lcom/vivox/service/vx_login_state_change_state;

    aput-object v2, v0, v1

    const/4 v1, 0x5

    sget-object v2, Lcom/vivox/service/vx_login_state_change_state;->login_state_error:Lcom/vivox/service/vx_login_state_change_state;

    aput-object v2, v0, v1

    sput-object v0, Lcom/vivox/service/vx_login_state_change_state;->swigValues:[Lcom/vivox/service/vx_login_state_change_state;

    return-void
.end method

.method private constructor <init>(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 15
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 16
    iput-object p1, p0, Lcom/vivox/service/vx_login_state_change_state;->swigName:Ljava/lang/String;

    .line 17
    sget v0, Lcom/vivox/service/vx_login_state_change_state;->swigNext:I

    .line 18
    .local v0, "i":I
    add-int/lit8 v1, v0, 0x1

    sput v1, Lcom/vivox/service/vx_login_state_change_state;->swigNext:I

    .line 19
    iput v0, p0, Lcom/vivox/service/vx_login_state_change_state;->swigValue:I

    .line 20
    return-void
.end method

.method private constructor <init>(Ljava/lang/String;I)V
    .locals 1
    .param p1, "str"    # Ljava/lang/String;
    .param p2, "i"    # I

    .prologue
    .line 22
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 23
    iput-object p1, p0, Lcom/vivox/service/vx_login_state_change_state;->swigName:Ljava/lang/String;

    .line 24
    iput p2, p0, Lcom/vivox/service/vx_login_state_change_state;->swigValue:I

    .line 25
    add-int/lit8 v0, p2, 0x1

    sput v0, Lcom/vivox/service/vx_login_state_change_state;->swigNext:I

    .line 26
    return-void
.end method

.method private constructor <init>(Ljava/lang/String;Lcom/vivox/service/vx_login_state_change_state;)V
    .locals 1
    .param p1, "str"    # Ljava/lang/String;
    .param p2, "com_vivox_service_vx_login_state_change_state"    # Lcom/vivox/service/vx_login_state_change_state;

    .prologue
    .line 28
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 29
    iput-object p1, p0, Lcom/vivox/service/vx_login_state_change_state;->swigName:Ljava/lang/String;

    .line 30
    iget v0, p2, Lcom/vivox/service/vx_login_state_change_state;->swigValue:I

    iput v0, p0, Lcom/vivox/service/vx_login_state_change_state;->swigValue:I

    .line 31
    iget v0, p0, Lcom/vivox/service/vx_login_state_change_state;->swigValue:I

    add-int/lit8 v0, v0, 0x1

    sput v0, Lcom/vivox/service/vx_login_state_change_state;->swigNext:I

    .line 32
    return-void
.end method

.method public static swigToEnum(I)Lcom/vivox/service/vx_login_state_change_state;
    .locals 4
    .param p0, "i"    # I

    .prologue
    .line 35
    sget-object v1, Lcom/vivox/service/vx_login_state_change_state;->swigValues:[Lcom/vivox/service/vx_login_state_change_state;

    array-length v1, v1

    if-ge p0, v1, :cond_0

    if-ltz p0, :cond_0

    sget-object v1, Lcom/vivox/service/vx_login_state_change_state;->swigValues:[Lcom/vivox/service/vx_login_state_change_state;

    aget-object v1, v1, p0

    iget v1, v1, Lcom/vivox/service/vx_login_state_change_state;->swigValue:I

    if-ne v1, p0, :cond_0

    .line 36
    sget-object v1, Lcom/vivox/service/vx_login_state_change_state;->swigValues:[Lcom/vivox/service/vx_login_state_change_state;

    aget-object v1, v1, p0

    .line 40
    :goto_0
    return-object v1

    .line 38
    :cond_0
    const/4 v0, 0x0

    .local v0, "i2":I
    :goto_1
    sget-object v1, Lcom/vivox/service/vx_login_state_change_state;->swigValues:[Lcom/vivox/service/vx_login_state_change_state;

    array-length v1, v1

    if-ge v0, v1, :cond_2

    .line 39
    sget-object v1, Lcom/vivox/service/vx_login_state_change_state;->swigValues:[Lcom/vivox/service/vx_login_state_change_state;

    aget-object v1, v1, v0

    iget v1, v1, Lcom/vivox/service/vx_login_state_change_state;->swigValue:I

    if-ne v1, p0, :cond_1

    .line 40
    sget-object v1, Lcom/vivox/service/vx_login_state_change_state;->swigValues:[Lcom/vivox/service/vx_login_state_change_state;

    aget-object v1, v1, v0

    goto :goto_0

    .line 38
    :cond_1
    add-int/lit8 v0, v0, 0x1

    goto :goto_1

    .line 43
    :cond_2
    new-instance v1, Ljava/lang/IllegalArgumentException;

    new-instance v2, Ljava/lang/StringBuilder;

    invoke-direct {v2}, Ljava/lang/StringBuilder;-><init>()V

    const-string v3, "No enum "

    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v2

    const-class v3, Lcom/vivox/service/vx_login_state_change_state;

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
    .line 47
    iget v0, p0, Lcom/vivox/service/vx_login_state_change_state;->swigValue:I

    return v0
.end method

.method public toString()Ljava/lang/String;
    .locals 1

    .prologue
    .line 51
    iget-object v0, p0, Lcom/vivox/service/vx_login_state_change_state;->swigName:Ljava/lang/String;

    return-object v0
.end method
