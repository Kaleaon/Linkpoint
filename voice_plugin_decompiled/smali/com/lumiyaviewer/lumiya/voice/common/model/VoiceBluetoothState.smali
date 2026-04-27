.class public final enum Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceBluetoothState;
.super Ljava/lang/Enum;
.source "VoiceBluetoothState.java"


# annotations
.annotation system Ldalvik/annotation/Signature;
    value = {
        "Ljava/lang/Enum",
        "<",
        "Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceBluetoothState;",
        ">;"
    }
.end annotation


# static fields
.field private static final synthetic $VALUES:[Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceBluetoothState;

.field public static final enum Active:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceBluetoothState;

.field public static final enum Connected:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceBluetoothState;

.field public static final enum Connecting:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceBluetoothState;

.field public static final enum Disconnected:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceBluetoothState;

.field public static final enum Error:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceBluetoothState;


# direct methods
.method static constructor <clinit>()V
    .locals 7

    .prologue
    const/4 v6, 0x4

    const/4 v5, 0x3

    const/4 v4, 0x2

    const/4 v3, 0x1

    const/4 v2, 0x0

    .line 5
    new-instance v0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceBluetoothState;

    const-string v1, "Disconnected"

    invoke-direct {v0, v1, v2}, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceBluetoothState;-><init>(Ljava/lang/String;I)V

    sput-object v0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceBluetoothState;->Disconnected:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceBluetoothState;

    .line 6
    new-instance v0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceBluetoothState;

    const-string v1, "Connecting"

    invoke-direct {v0, v1, v3}, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceBluetoothState;-><init>(Ljava/lang/String;I)V

    sput-object v0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceBluetoothState;->Connecting:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceBluetoothState;

    .line 7
    new-instance v0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceBluetoothState;

    const-string v1, "Connected"

    invoke-direct {v0, v1, v4}, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceBluetoothState;-><init>(Ljava/lang/String;I)V

    sput-object v0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceBluetoothState;->Connected:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceBluetoothState;

    .line 8
    new-instance v0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceBluetoothState;

    const-string v1, "Active"

    invoke-direct {v0, v1, v5}, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceBluetoothState;-><init>(Ljava/lang/String;I)V

    sput-object v0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceBluetoothState;->Active:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceBluetoothState;

    .line 9
    new-instance v0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceBluetoothState;

    const-string v1, "Error"

    invoke-direct {v0, v1, v6}, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceBluetoothState;-><init>(Ljava/lang/String;I)V

    sput-object v0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceBluetoothState;->Error:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceBluetoothState;

    .line 3
    const/4 v0, 0x5

    new-array v0, v0, [Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceBluetoothState;

    sget-object v1, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceBluetoothState;->Disconnected:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceBluetoothState;

    aput-object v1, v0, v2

    sget-object v1, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceBluetoothState;->Connecting:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceBluetoothState;

    aput-object v1, v0, v3

    sget-object v1, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceBluetoothState;->Connected:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceBluetoothState;

    aput-object v1, v0, v4

    sget-object v1, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceBluetoothState;->Active:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceBluetoothState;

    aput-object v1, v0, v5

    sget-object v1, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceBluetoothState;->Error:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceBluetoothState;

    aput-object v1, v0, v6

    sput-object v0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceBluetoothState;->$VALUES:[Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceBluetoothState;

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

.method public static valueOf(Ljava/lang/String;)Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceBluetoothState;
    .locals 1
    .param p0, "name"    # Ljava/lang/String;

    .prologue
    .line 3
    const-class v0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceBluetoothState;

    invoke-static {v0, p0}, Ljava/lang/Enum;->valueOf(Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/Enum;

    move-result-object v0

    check-cast v0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceBluetoothState;

    return-object v0
.end method

.method public static values()[Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceBluetoothState;
    .locals 1

    .prologue
    .line 3
    sget-object v0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceBluetoothState;->$VALUES:[Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceBluetoothState;

    invoke-virtual {v0}, [Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceBluetoothState;->clone()Ljava/lang/Object;

    move-result-object v0

    check-cast v0, [Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceBluetoothState;

    return-object v0
.end method
