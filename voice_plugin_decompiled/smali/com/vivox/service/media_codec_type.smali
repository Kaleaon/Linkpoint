.class public final Lcom/vivox/service/media_codec_type;
.super Ljava/lang/Object;
.source "media_codec_type.java"


# static fields
.field public static final media_codec_type_nm:Lcom/vivox/service/media_codec_type;

.field public static final media_codec_type_none:Lcom/vivox/service/media_codec_type;

.field public static final media_codec_type_pcmu:Lcom/vivox/service/media_codec_type;

.field public static final media_codec_type_siren14:Lcom/vivox/service/media_codec_type;

.field public static final media_codec_type_speex:Lcom/vivox/service/media_codec_type;

.field private static swigNext:I

.field private static swigValues:[Lcom/vivox/service/media_codec_type;


# instance fields
.field private final swigName:Ljava/lang/String;

.field private final swigValue:I


# direct methods
.method static constructor <clinit>()V
    .locals 4

    .prologue
    const/4 v3, 0x0

    .line 4
    new-instance v0, Lcom/vivox/service/media_codec_type;

    const-string v1, "media_codec_type_nm"

    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->media_codec_type_nm_get()I

    move-result v2

    invoke-direct {v0, v1, v2}, Lcom/vivox/service/media_codec_type;-><init>(Ljava/lang/String;I)V

    sput-object v0, Lcom/vivox/service/media_codec_type;->media_codec_type_nm:Lcom/vivox/service/media_codec_type;

    .line 5
    new-instance v0, Lcom/vivox/service/media_codec_type;

    const-string v1, "media_codec_type_none"

    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->media_codec_type_none_get()I

    move-result v2

    invoke-direct {v0, v1, v2}, Lcom/vivox/service/media_codec_type;-><init>(Ljava/lang/String;I)V

    sput-object v0, Lcom/vivox/service/media_codec_type;->media_codec_type_none:Lcom/vivox/service/media_codec_type;

    .line 6
    new-instance v0, Lcom/vivox/service/media_codec_type;

    const-string v1, "media_codec_type_pcmu"

    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->media_codec_type_pcmu_get()I

    move-result v2

    invoke-direct {v0, v1, v2}, Lcom/vivox/service/media_codec_type;-><init>(Ljava/lang/String;I)V

    sput-object v0, Lcom/vivox/service/media_codec_type;->media_codec_type_pcmu:Lcom/vivox/service/media_codec_type;

    .line 7
    new-instance v0, Lcom/vivox/service/media_codec_type;

    const-string v1, "media_codec_type_siren14"

    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->media_codec_type_siren14_get()I

    move-result v2

    invoke-direct {v0, v1, v2}, Lcom/vivox/service/media_codec_type;-><init>(Ljava/lang/String;I)V

    sput-object v0, Lcom/vivox/service/media_codec_type;->media_codec_type_siren14:Lcom/vivox/service/media_codec_type;

    .line 8
    new-instance v0, Lcom/vivox/service/media_codec_type;

    const-string v1, "media_codec_type_speex"

    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->media_codec_type_speex_get()I

    move-result v2

    invoke-direct {v0, v1, v2}, Lcom/vivox/service/media_codec_type;-><init>(Ljava/lang/String;I)V

    sput-object v0, Lcom/vivox/service/media_codec_type;->media_codec_type_speex:Lcom/vivox/service/media_codec_type;

    .line 9
    sput v3, Lcom/vivox/service/media_codec_type;->swigNext:I

    .line 10
    const/4 v0, 0x5

    new-array v0, v0, [Lcom/vivox/service/media_codec_type;

    sget-object v1, Lcom/vivox/service/media_codec_type;->media_codec_type_none:Lcom/vivox/service/media_codec_type;

    aput-object v1, v0, v3

    const/4 v1, 0x1

    sget-object v2, Lcom/vivox/service/media_codec_type;->media_codec_type_siren14:Lcom/vivox/service/media_codec_type;

    aput-object v2, v0, v1

    const/4 v1, 0x2

    sget-object v2, Lcom/vivox/service/media_codec_type;->media_codec_type_pcmu:Lcom/vivox/service/media_codec_type;

    aput-object v2, v0, v1

    const/4 v1, 0x3

    sget-object v2, Lcom/vivox/service/media_codec_type;->media_codec_type_nm:Lcom/vivox/service/media_codec_type;

    aput-object v2, v0, v1

    const/4 v1, 0x4

    sget-object v2, Lcom/vivox/service/media_codec_type;->media_codec_type_speex:Lcom/vivox/service/media_codec_type;

    aput-object v2, v0, v1

    sput-object v0, Lcom/vivox/service/media_codec_type;->swigValues:[Lcom/vivox/service/media_codec_type;

    return-void
.end method

.method private constructor <init>(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 14
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 15
    iput-object p1, p0, Lcom/vivox/service/media_codec_type;->swigName:Ljava/lang/String;

    .line 16
    sget v0, Lcom/vivox/service/media_codec_type;->swigNext:I

    .line 17
    .local v0, "i":I
    add-int/lit8 v1, v0, 0x1

    sput v1, Lcom/vivox/service/media_codec_type;->swigNext:I

    .line 18
    iput v0, p0, Lcom/vivox/service/media_codec_type;->swigValue:I

    .line 19
    return-void
.end method

.method private constructor <init>(Ljava/lang/String;I)V
    .locals 1
    .param p1, "str"    # Ljava/lang/String;
    .param p2, "i"    # I

    .prologue
    .line 21
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 22
    iput-object p1, p0, Lcom/vivox/service/media_codec_type;->swigName:Ljava/lang/String;

    .line 23
    iput p2, p0, Lcom/vivox/service/media_codec_type;->swigValue:I

    .line 24
    add-int/lit8 v0, p2, 0x1

    sput v0, Lcom/vivox/service/media_codec_type;->swigNext:I

    .line 25
    return-void
.end method

.method private constructor <init>(Ljava/lang/String;Lcom/vivox/service/media_codec_type;)V
    .locals 1
    .param p1, "str"    # Ljava/lang/String;
    .param p2, "com_vivox_service_media_codec_type"    # Lcom/vivox/service/media_codec_type;

    .prologue
    .line 27
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 28
    iput-object p1, p0, Lcom/vivox/service/media_codec_type;->swigName:Ljava/lang/String;

    .line 29
    iget v0, p2, Lcom/vivox/service/media_codec_type;->swigValue:I

    iput v0, p0, Lcom/vivox/service/media_codec_type;->swigValue:I

    .line 30
    iget v0, p0, Lcom/vivox/service/media_codec_type;->swigValue:I

    add-int/lit8 v0, v0, 0x1

    sput v0, Lcom/vivox/service/media_codec_type;->swigNext:I

    .line 31
    return-void
.end method

.method public static swigToEnum(I)Lcom/vivox/service/media_codec_type;
    .locals 4
    .param p0, "i"    # I

    .prologue
    .line 34
    sget-object v1, Lcom/vivox/service/media_codec_type;->swigValues:[Lcom/vivox/service/media_codec_type;

    array-length v1, v1

    if-ge p0, v1, :cond_0

    if-ltz p0, :cond_0

    sget-object v1, Lcom/vivox/service/media_codec_type;->swigValues:[Lcom/vivox/service/media_codec_type;

    aget-object v1, v1, p0

    iget v1, v1, Lcom/vivox/service/media_codec_type;->swigValue:I

    if-ne v1, p0, :cond_0

    .line 35
    sget-object v1, Lcom/vivox/service/media_codec_type;->swigValues:[Lcom/vivox/service/media_codec_type;

    aget-object v1, v1, p0

    .line 39
    :goto_0
    return-object v1

    .line 37
    :cond_0
    const/4 v0, 0x0

    .local v0, "i2":I
    :goto_1
    sget-object v1, Lcom/vivox/service/media_codec_type;->swigValues:[Lcom/vivox/service/media_codec_type;

    array-length v1, v1

    if-ge v0, v1, :cond_2

    .line 38
    sget-object v1, Lcom/vivox/service/media_codec_type;->swigValues:[Lcom/vivox/service/media_codec_type;

    aget-object v1, v1, v0

    iget v1, v1, Lcom/vivox/service/media_codec_type;->swigValue:I

    if-ne v1, p0, :cond_1

    .line 39
    sget-object v1, Lcom/vivox/service/media_codec_type;->swigValues:[Lcom/vivox/service/media_codec_type;

    aget-object v1, v1, v0

    goto :goto_0

    .line 37
    :cond_1
    add-int/lit8 v0, v0, 0x1

    goto :goto_1

    .line 42
    :cond_2
    new-instance v1, Ljava/lang/IllegalArgumentException;

    new-instance v2, Ljava/lang/StringBuilder;

    invoke-direct {v2}, Ljava/lang/StringBuilder;-><init>()V

    const-string v3, "No enum "

    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v2

    const-class v3, Lcom/vivox/service/media_codec_type;

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
    .line 46
    iget v0, p0, Lcom/vivox/service/media_codec_type;->swigValue:I

    return v0
.end method

.method public toString()Ljava/lang/String;
    .locals 1

    .prologue
    .line 50
    iget-object v0, p0, Lcom/vivox/service/media_codec_type;->swigName:Ljava/lang/String;

    return-object v0
.end method
