.class Lcom/lumiyaviewer/lumiya/voice/VivoxMessageQueue$InstanceHolder;
.super Ljava/lang/Object;
.source "VivoxMessageQueue.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/lumiyaviewer/lumiya/voice/VivoxMessageQueue;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0xa
    name = "InstanceHolder"
.end annotation


# static fields
.field private static final instance:Lcom/lumiyaviewer/lumiya/voice/VivoxMessageQueue;


# direct methods
.method static constructor <clinit>()V
    .locals 2

    .prologue
    .line 13
    new-instance v0, Lcom/lumiyaviewer/lumiya/voice/VivoxMessageQueue;

    const/4 v1, 0x0

    invoke-direct {v0, v1}, Lcom/lumiyaviewer/lumiya/voice/VivoxMessageQueue;-><init>(Lcom/lumiyaviewer/lumiya/voice/VivoxMessageQueue$1;)V

    sput-object v0, Lcom/lumiyaviewer/lumiya/voice/VivoxMessageQueue$InstanceHolder;->instance:Lcom/lumiyaviewer/lumiya/voice/VivoxMessageQueue;

    return-void
.end method

.method private constructor <init>()V
    .locals 0

    .prologue
    .line 12
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method

.method static synthetic access$100()Lcom/lumiyaviewer/lumiya/voice/VivoxMessageQueue;
    .locals 1

    .prologue
    .line 12
    sget-object v0, Lcom/lumiyaviewer/lumiya/voice/VivoxMessageQueue$InstanceHolder;->instance:Lcom/lumiyaviewer/lumiya/voice/VivoxMessageQueue;

    return-object v0
.end method
