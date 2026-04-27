.class public final Lcom/vivox/service/vx_buddy_presence_state;
.super Ljava/lang/Object;
.source "vx_buddy_presence_state.java"


# static fields
.field public static final buddy_presence_away:Lcom/vivox/service/vx_buddy_presence_state;

.field public static final buddy_presence_brb:Lcom/vivox/service/vx_buddy_presence_state;

.field public static final buddy_presence_busy:Lcom/vivox/service/vx_buddy_presence_state;

.field public static final buddy_presence_closed:Lcom/vivox/service/vx_buddy_presence_state;

.field public static final buddy_presence_custom:Lcom/vivox/service/vx_buddy_presence_state;

.field public static final buddy_presence_offline:Lcom/vivox/service/vx_buddy_presence_state;

.field public static final buddy_presence_online:Lcom/vivox/service/vx_buddy_presence_state;

.field public static final buddy_presence_online_slc:Lcom/vivox/service/vx_buddy_presence_state;

.field public static final buddy_presence_onthephone:Lcom/vivox/service/vx_buddy_presence_state;

.field public static final buddy_presence_outtolunch:Lcom/vivox/service/vx_buddy_presence_state;

.field public static final buddy_presence_pending:Lcom/vivox/service/vx_buddy_presence_state;

.field public static final buddy_presence_unknown:Lcom/vivox/service/vx_buddy_presence_state;

.field private static swigNext:I

.field private static swigValues:[Lcom/vivox/service/vx_buddy_presence_state;


# instance fields
.field private final swigName:Ljava/lang/String;

.field private final swigValue:I


# direct methods
.method static constructor <clinit>()V
    .locals 4

    .prologue
    const/4 v3, 0x0

    .line 4
    new-instance v0, Lcom/vivox/service/vx_buddy_presence_state;

    const-string v1, "buddy_presence_away"

    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->buddy_presence_away_get()I

    move-result v2

    invoke-direct {v0, v1, v2}, Lcom/vivox/service/vx_buddy_presence_state;-><init>(Ljava/lang/String;I)V

    sput-object v0, Lcom/vivox/service/vx_buddy_presence_state;->buddy_presence_away:Lcom/vivox/service/vx_buddy_presence_state;

    .line 5
    new-instance v0, Lcom/vivox/service/vx_buddy_presence_state;

    const-string v1, "buddy_presence_brb"

    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->buddy_presence_brb_get()I

    move-result v2

    invoke-direct {v0, v1, v2}, Lcom/vivox/service/vx_buddy_presence_state;-><init>(Ljava/lang/String;I)V

    sput-object v0, Lcom/vivox/service/vx_buddy_presence_state;->buddy_presence_brb:Lcom/vivox/service/vx_buddy_presence_state;

    .line 6
    new-instance v0, Lcom/vivox/service/vx_buddy_presence_state;

    const-string v1, "buddy_presence_busy"

    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->buddy_presence_busy_get()I

    move-result v2

    invoke-direct {v0, v1, v2}, Lcom/vivox/service/vx_buddy_presence_state;-><init>(Ljava/lang/String;I)V

    sput-object v0, Lcom/vivox/service/vx_buddy_presence_state;->buddy_presence_busy:Lcom/vivox/service/vx_buddy_presence_state;

    .line 7
    new-instance v0, Lcom/vivox/service/vx_buddy_presence_state;

    const-string v1, "buddy_presence_closed"

    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->buddy_presence_closed_get()I

    move-result v2

    invoke-direct {v0, v1, v2}, Lcom/vivox/service/vx_buddy_presence_state;-><init>(Ljava/lang/String;I)V

    sput-object v0, Lcom/vivox/service/vx_buddy_presence_state;->buddy_presence_closed:Lcom/vivox/service/vx_buddy_presence_state;

    .line 8
    new-instance v0, Lcom/vivox/service/vx_buddy_presence_state;

    const-string v1, "buddy_presence_custom"

    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->buddy_presence_custom_get()I

    move-result v2

    invoke-direct {v0, v1, v2}, Lcom/vivox/service/vx_buddy_presence_state;-><init>(Ljava/lang/String;I)V

    sput-object v0, Lcom/vivox/service/vx_buddy_presence_state;->buddy_presence_custom:Lcom/vivox/service/vx_buddy_presence_state;

    .line 9
    new-instance v0, Lcom/vivox/service/vx_buddy_presence_state;

    const-string v1, "buddy_presence_offline"

    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->buddy_presence_offline_get()I

    move-result v2

    invoke-direct {v0, v1, v2}, Lcom/vivox/service/vx_buddy_presence_state;-><init>(Ljava/lang/String;I)V

    sput-object v0, Lcom/vivox/service/vx_buddy_presence_state;->buddy_presence_offline:Lcom/vivox/service/vx_buddy_presence_state;

    .line 10
    new-instance v0, Lcom/vivox/service/vx_buddy_presence_state;

    const-string v1, "buddy_presence_online"

    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->buddy_presence_online_get()I

    move-result v2

    invoke-direct {v0, v1, v2}, Lcom/vivox/service/vx_buddy_presence_state;-><init>(Ljava/lang/String;I)V

    sput-object v0, Lcom/vivox/service/vx_buddy_presence_state;->buddy_presence_online:Lcom/vivox/service/vx_buddy_presence_state;

    .line 11
    new-instance v0, Lcom/vivox/service/vx_buddy_presence_state;

    const-string v1, "buddy_presence_online_slc"

    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->buddy_presence_online_slc_get()I

    move-result v2

    invoke-direct {v0, v1, v2}, Lcom/vivox/service/vx_buddy_presence_state;-><init>(Ljava/lang/String;I)V

    sput-object v0, Lcom/vivox/service/vx_buddy_presence_state;->buddy_presence_online_slc:Lcom/vivox/service/vx_buddy_presence_state;

    .line 12
    new-instance v0, Lcom/vivox/service/vx_buddy_presence_state;

    const-string v1, "buddy_presence_onthephone"

    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->buddy_presence_onthephone_get()I

    move-result v2

    invoke-direct {v0, v1, v2}, Lcom/vivox/service/vx_buddy_presence_state;-><init>(Ljava/lang/String;I)V

    sput-object v0, Lcom/vivox/service/vx_buddy_presence_state;->buddy_presence_onthephone:Lcom/vivox/service/vx_buddy_presence_state;

    .line 13
    new-instance v0, Lcom/vivox/service/vx_buddy_presence_state;

    const-string v1, "buddy_presence_outtolunch"

    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->buddy_presence_outtolunch_get()I

    move-result v2

    invoke-direct {v0, v1, v2}, Lcom/vivox/service/vx_buddy_presence_state;-><init>(Ljava/lang/String;I)V

    sput-object v0, Lcom/vivox/service/vx_buddy_presence_state;->buddy_presence_outtolunch:Lcom/vivox/service/vx_buddy_presence_state;

    .line 14
    new-instance v0, Lcom/vivox/service/vx_buddy_presence_state;

    const-string v1, "buddy_presence_pending"

    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->buddy_presence_pending_get()I

    move-result v2

    invoke-direct {v0, v1, v2}, Lcom/vivox/service/vx_buddy_presence_state;-><init>(Ljava/lang/String;I)V

    sput-object v0, Lcom/vivox/service/vx_buddy_presence_state;->buddy_presence_pending:Lcom/vivox/service/vx_buddy_presence_state;

    .line 15
    new-instance v0, Lcom/vivox/service/vx_buddy_presence_state;

    const-string v1, "buddy_presence_unknown"

    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->buddy_presence_unknown_get()I

    move-result v2

    invoke-direct {v0, v1, v2}, Lcom/vivox/service/vx_buddy_presence_state;-><init>(Ljava/lang/String;I)V

    sput-object v0, Lcom/vivox/service/vx_buddy_presence_state;->buddy_presence_unknown:Lcom/vivox/service/vx_buddy_presence_state;

    .line 16
    sput v3, Lcom/vivox/service/vx_buddy_presence_state;->swigNext:I

    .line 17
    const/16 v0, 0xc

    new-array v0, v0, [Lcom/vivox/service/vx_buddy_presence_state;

    sget-object v1, Lcom/vivox/service/vx_buddy_presence_state;->buddy_presence_unknown:Lcom/vivox/service/vx_buddy_presence_state;

    aput-object v1, v0, v3

    const/4 v1, 0x1

    sget-object v2, Lcom/vivox/service/vx_buddy_presence_state;->buddy_presence_pending:Lcom/vivox/service/vx_buddy_presence_state;

    aput-object v2, v0, v1

    const/4 v1, 0x2

    sget-object v2, Lcom/vivox/service/vx_buddy_presence_state;->buddy_presence_online:Lcom/vivox/service/vx_buddy_presence_state;

    aput-object v2, v0, v1

    const/4 v1, 0x3

    sget-object v2, Lcom/vivox/service/vx_buddy_presence_state;->buddy_presence_busy:Lcom/vivox/service/vx_buddy_presence_state;

    aput-object v2, v0, v1

    const/4 v1, 0x4

    sget-object v2, Lcom/vivox/service/vx_buddy_presence_state;->buddy_presence_brb:Lcom/vivox/service/vx_buddy_presence_state;

    aput-object v2, v0, v1

    const/4 v1, 0x5

    sget-object v2, Lcom/vivox/service/vx_buddy_presence_state;->buddy_presence_away:Lcom/vivox/service/vx_buddy_presence_state;

    aput-object v2, v0, v1

    const/4 v1, 0x6

    sget-object v2, Lcom/vivox/service/vx_buddy_presence_state;->buddy_presence_onthephone:Lcom/vivox/service/vx_buddy_presence_state;

    aput-object v2, v0, v1

    const/4 v1, 0x7

    sget-object v2, Lcom/vivox/service/vx_buddy_presence_state;->buddy_presence_outtolunch:Lcom/vivox/service/vx_buddy_presence_state;

    aput-object v2, v0, v1

    const/16 v1, 0x8

    sget-object v2, Lcom/vivox/service/vx_buddy_presence_state;->buddy_presence_custom:Lcom/vivox/service/vx_buddy_presence_state;

    aput-object v2, v0, v1

    const/16 v1, 0x9

    sget-object v2, Lcom/vivox/service/vx_buddy_presence_state;->buddy_presence_online_slc:Lcom/vivox/service/vx_buddy_presence_state;

    aput-object v2, v0, v1

    const/16 v1, 0xa

    sget-object v2, Lcom/vivox/service/vx_buddy_presence_state;->buddy_presence_closed:Lcom/vivox/service/vx_buddy_presence_state;

    aput-object v2, v0, v1

    const/16 v1, 0xb

    sget-object v2, Lcom/vivox/service/vx_buddy_presence_state;->buddy_presence_offline:Lcom/vivox/service/vx_buddy_presence_state;

    aput-object v2, v0, v1

    sput-object v0, Lcom/vivox/service/vx_buddy_presence_state;->swigValues:[Lcom/vivox/service/vx_buddy_presence_state;

    return-void
.end method

.method private constructor <init>(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 21
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 22
    iput-object p1, p0, Lcom/vivox/service/vx_buddy_presence_state;->swigName:Ljava/lang/String;

    .line 23
    sget v0, Lcom/vivox/service/vx_buddy_presence_state;->swigNext:I

    .line 24
    .local v0, "i":I
    add-int/lit8 v1, v0, 0x1

    sput v1, Lcom/vivox/service/vx_buddy_presence_state;->swigNext:I

    .line 25
    iput v0, p0, Lcom/vivox/service/vx_buddy_presence_state;->swigValue:I

    .line 26
    return-void
.end method

.method private constructor <init>(Ljava/lang/String;I)V
    .locals 1
    .param p1, "str"    # Ljava/lang/String;
    .param p2, "i"    # I

    .prologue
    .line 28
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 29
    iput-object p1, p0, Lcom/vivox/service/vx_buddy_presence_state;->swigName:Ljava/lang/String;

    .line 30
    iput p2, p0, Lcom/vivox/service/vx_buddy_presence_state;->swigValue:I

    .line 31
    add-int/lit8 v0, p2, 0x1

    sput v0, Lcom/vivox/service/vx_buddy_presence_state;->swigNext:I

    .line 32
    return-void
.end method

.method private constructor <init>(Ljava/lang/String;Lcom/vivox/service/vx_buddy_presence_state;)V
    .locals 1
    .param p1, "str"    # Ljava/lang/String;
    .param p2, "com_vivox_service_vx_buddy_presence_state"    # Lcom/vivox/service/vx_buddy_presence_state;

    .prologue
    .line 34
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 35
    iput-object p1, p0, Lcom/vivox/service/vx_buddy_presence_state;->swigName:Ljava/lang/String;

    .line 36
    iget v0, p2, Lcom/vivox/service/vx_buddy_presence_state;->swigValue:I

    iput v0, p0, Lcom/vivox/service/vx_buddy_presence_state;->swigValue:I

    .line 37
    iget v0, p0, Lcom/vivox/service/vx_buddy_presence_state;->swigValue:I

    add-int/lit8 v0, v0, 0x1

    sput v0, Lcom/vivox/service/vx_buddy_presence_state;->swigNext:I

    .line 38
    return-void
.end method

.method public static swigToEnum(I)Lcom/vivox/service/vx_buddy_presence_state;
    .locals 4
    .param p0, "i"    # I

    .prologue
    .line 41
    sget-object v1, Lcom/vivox/service/vx_buddy_presence_state;->swigValues:[Lcom/vivox/service/vx_buddy_presence_state;

    array-length v1, v1

    if-ge p0, v1, :cond_0

    if-ltz p0, :cond_0

    sget-object v1, Lcom/vivox/service/vx_buddy_presence_state;->swigValues:[Lcom/vivox/service/vx_buddy_presence_state;

    aget-object v1, v1, p0

    iget v1, v1, Lcom/vivox/service/vx_buddy_presence_state;->swigValue:I

    if-ne v1, p0, :cond_0

    .line 42
    sget-object v1, Lcom/vivox/service/vx_buddy_presence_state;->swigValues:[Lcom/vivox/service/vx_buddy_presence_state;

    aget-object v1, v1, p0

    .line 46
    :goto_0
    return-object v1

    .line 44
    :cond_0
    const/4 v0, 0x0

    .local v0, "i2":I
    :goto_1
    sget-object v1, Lcom/vivox/service/vx_buddy_presence_state;->swigValues:[Lcom/vivox/service/vx_buddy_presence_state;

    array-length v1, v1

    if-ge v0, v1, :cond_2

    .line 45
    sget-object v1, Lcom/vivox/service/vx_buddy_presence_state;->swigValues:[Lcom/vivox/service/vx_buddy_presence_state;

    aget-object v1, v1, v0

    iget v1, v1, Lcom/vivox/service/vx_buddy_presence_state;->swigValue:I

    if-ne v1, p0, :cond_1

    .line 46
    sget-object v1, Lcom/vivox/service/vx_buddy_presence_state;->swigValues:[Lcom/vivox/service/vx_buddy_presence_state;

    aget-object v1, v1, v0

    goto :goto_0

    .line 44
    :cond_1
    add-int/lit8 v0, v0, 0x1

    goto :goto_1

    .line 49
    :cond_2
    new-instance v1, Ljava/lang/IllegalArgumentException;

    new-instance v2, Ljava/lang/StringBuilder;

    invoke-direct {v2}, Ljava/lang/StringBuilder;-><init>()V

    const-string v3, "No enum "

    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v2

    const-class v3, Lcom/vivox/service/vx_buddy_presence_state;

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
    .line 53
    iget v0, p0, Lcom/vivox/service/vx_buddy_presence_state;->swigValue:I

    return v0
.end method

.method public toString()Ljava/lang/String;
    .locals 1

    .prologue
    .line 57
    iget-object v0, p0, Lcom/vivox/service/vx_buddy_presence_state;->swigName:Ljava/lang/String;

    return-object v0
.end method
