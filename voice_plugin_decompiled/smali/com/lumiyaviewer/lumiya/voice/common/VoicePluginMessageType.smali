.class public final enum Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;
.super Ljava/lang/Enum;
.source "VoicePluginMessageType.java"


# annotations
.annotation system Ldalvik/annotation/Signature;
    value = {
        "Ljava/lang/Enum",
        "<",
        "Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;",
        ">;"
    }
.end annotation


# static fields
.field private static final synthetic $VALUES:[Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;

.field public static final VOICE_PLUGIN_MESSAGE:I = 0xc8

.field public static final enum VoiceAcceptCall:Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;

.field public static final enum VoiceAudioProperties:Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;

.field public static final enum VoiceChannelClosed:Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;

.field public static final enum VoiceChannelStatus:Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;

.field public static final enum VoiceConnectChannel:Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;

.field public static final enum VoiceEnableMic:Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;

.field public static final enum VoiceInitialize:Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;

.field public static final enum VoiceInitializeReply:Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;

.field public static final enum VoiceLogin:Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;

.field public static final enum VoiceLoginStatus:Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;

.field public static final enum VoiceLogout:Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;

.field public static final enum VoiceRejectCall:Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;

.field public static final enum VoiceRinging:Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;

.field public static final enum VoiceSet3DPosition:Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;

.field public static final enum VoiceSetAudioProperties:Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;

.field public static final enum VoiceTerminateCall:Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;


# direct methods
.method static constructor <clinit>()V
    .locals 8

    .prologue
    const/4 v7, 0x4

    const/4 v6, 0x3

    const/4 v5, 0x2

    const/4 v4, 0x1

    const/4 v3, 0x0

    .line 5
    new-instance v0, Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;

    const-string v1, "VoiceInitialize"

    invoke-direct {v0, v1, v3}, Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;-><init>(Ljava/lang/String;I)V

    sput-object v0, Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;->VoiceInitialize:Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;

    .line 6
    new-instance v0, Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;

    const-string v1, "VoiceInitializeReply"

    invoke-direct {v0, v1, v4}, Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;-><init>(Ljava/lang/String;I)V

    sput-object v0, Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;->VoiceInitializeReply:Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;

    .line 7
    new-instance v0, Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;

    const-string v1, "VoiceLogin"

    invoke-direct {v0, v1, v5}, Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;-><init>(Ljava/lang/String;I)V

    sput-object v0, Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;->VoiceLogin:Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;

    .line 8
    new-instance v0, Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;

    const-string v1, "VoiceLogout"

    invoke-direct {v0, v1, v6}, Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;-><init>(Ljava/lang/String;I)V

    sput-object v0, Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;->VoiceLogout:Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;

    .line 9
    new-instance v0, Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;

    const-string v1, "VoiceConnectChannel"

    invoke-direct {v0, v1, v7}, Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;-><init>(Ljava/lang/String;I)V

    sput-object v0, Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;->VoiceConnectChannel:Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;

    .line 10
    new-instance v0, Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;

    const-string v1, "VoiceLoginStatus"

    const/4 v2, 0x5

    invoke-direct {v0, v1, v2}, Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;-><init>(Ljava/lang/String;I)V

    sput-object v0, Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;->VoiceLoginStatus:Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;

    .line 11
    new-instance v0, Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;

    const-string v1, "VoiceChannelStatus"

    const/4 v2, 0x6

    invoke-direct {v0, v1, v2}, Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;-><init>(Ljava/lang/String;I)V

    sput-object v0, Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;->VoiceChannelStatus:Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;

    .line 12
    new-instance v0, Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;

    const-string v1, "VoiceSet3DPosition"

    const/4 v2, 0x7

    invoke-direct {v0, v1, v2}, Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;-><init>(Ljava/lang/String;I)V

    sput-object v0, Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;->VoiceSet3DPosition:Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;

    .line 13
    new-instance v0, Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;

    const-string v1, "VoiceRinging"

    const/16 v2, 0x8

    invoke-direct {v0, v1, v2}, Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;-><init>(Ljava/lang/String;I)V

    sput-object v0, Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;->VoiceRinging:Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;

    .line 14
    new-instance v0, Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;

    const-string v1, "VoiceAcceptCall"

    const/16 v2, 0x9

    invoke-direct {v0, v1, v2}, Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;-><init>(Ljava/lang/String;I)V

    sput-object v0, Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;->VoiceAcceptCall:Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;

    .line 15
    new-instance v0, Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;

    const-string v1, "VoiceRejectCall"

    const/16 v2, 0xa

    invoke-direct {v0, v1, v2}, Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;-><init>(Ljava/lang/String;I)V

    sput-object v0, Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;->VoiceRejectCall:Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;

    .line 16
    new-instance v0, Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;

    const-string v1, "VoiceChannelClosed"

    const/16 v2, 0xb

    invoke-direct {v0, v1, v2}, Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;-><init>(Ljava/lang/String;I)V

    sput-object v0, Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;->VoiceChannelClosed:Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;

    .line 17
    new-instance v0, Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;

    const-string v1, "VoiceTerminateCall"

    const/16 v2, 0xc

    invoke-direct {v0, v1, v2}, Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;-><init>(Ljava/lang/String;I)V

    sput-object v0, Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;->VoiceTerminateCall:Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;

    .line 18
    new-instance v0, Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;

    const-string v1, "VoiceEnableMic"

    const/16 v2, 0xd

    invoke-direct {v0, v1, v2}, Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;-><init>(Ljava/lang/String;I)V

    sput-object v0, Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;->VoiceEnableMic:Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;

    .line 19
    new-instance v0, Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;

    const-string v1, "VoiceSetAudioProperties"

    const/16 v2, 0xe

    invoke-direct {v0, v1, v2}, Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;-><init>(Ljava/lang/String;I)V

    sput-object v0, Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;->VoiceSetAudioProperties:Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;

    .line 20
    new-instance v0, Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;

    const-string v1, "VoiceAudioProperties"

    const/16 v2, 0xf

    invoke-direct {v0, v1, v2}, Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;-><init>(Ljava/lang/String;I)V

    sput-object v0, Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;->VoiceAudioProperties:Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;

    .line 3
    const/16 v0, 0x10

    new-array v0, v0, [Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;

    sget-object v1, Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;->VoiceInitialize:Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;

    aput-object v1, v0, v3

    sget-object v1, Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;->VoiceInitializeReply:Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;

    aput-object v1, v0, v4

    sget-object v1, Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;->VoiceLogin:Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;

    aput-object v1, v0, v5

    sget-object v1, Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;->VoiceLogout:Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;

    aput-object v1, v0, v6

    sget-object v1, Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;->VoiceConnectChannel:Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;

    aput-object v1, v0, v7

    const/4 v1, 0x5

    sget-object v2, Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;->VoiceLoginStatus:Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;

    aput-object v2, v0, v1

    const/4 v1, 0x6

    sget-object v2, Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;->VoiceChannelStatus:Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;

    aput-object v2, v0, v1

    const/4 v1, 0x7

    sget-object v2, Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;->VoiceSet3DPosition:Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;

    aput-object v2, v0, v1

    const/16 v1, 0x8

    sget-object v2, Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;->VoiceRinging:Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;

    aput-object v2, v0, v1

    const/16 v1, 0x9

    sget-object v2, Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;->VoiceAcceptCall:Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;

    aput-object v2, v0, v1

    const/16 v1, 0xa

    sget-object v2, Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;->VoiceRejectCall:Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;

    aput-object v2, v0, v1

    const/16 v1, 0xb

    sget-object v2, Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;->VoiceChannelClosed:Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;

    aput-object v2, v0, v1

    const/16 v1, 0xc

    sget-object v2, Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;->VoiceTerminateCall:Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;

    aput-object v2, v0, v1

    const/16 v1, 0xd

    sget-object v2, Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;->VoiceEnableMic:Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;

    aput-object v2, v0, v1

    const/16 v1, 0xe

    sget-object v2, Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;->VoiceSetAudioProperties:Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;

    aput-object v2, v0, v1

    const/16 v1, 0xf

    sget-object v2, Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;->VoiceAudioProperties:Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;

    aput-object v2, v0, v1

    sput-object v0, Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;->$VALUES:[Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;

    return-void
.end method

.method private constructor <init>(Ljava/lang/String;I)V
    .locals 0
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "()V"
        }
    .end annotation

    .prologue
    .line 3
    invoke-direct {p0, p1, p2}, Ljava/lang/Enum;-><init>(Ljava/lang/String;I)V

    return-void
.end method

.method public static valueOf(Ljava/lang/String;)Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;
    .locals 1
    .param p0, "name"    # Ljava/lang/String;

    .prologue
    .line 3
    const-class v0, Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;

    invoke-static {v0, p0}, Ljava/lang/Enum;->valueOf(Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/Enum;

    move-result-object v0

    check-cast v0, Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;

    return-object v0
.end method

.method public static values()[Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;
    .locals 1

    .prologue
    .line 3
    sget-object v0, Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;->$VALUES:[Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;

    invoke-virtual {v0}, [Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;->clone()Ljava/lang/Object;

    move-result-object v0

    check-cast v0, [Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;

    return-object v0
.end method
