.class public Lcom/lumiyaviewer/lumiya/voice/VivoxController;
.super Ljava/lang/Object;
.source "VivoxController.java"

# interfaces
.implements Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController$OnVivoxMessageListener;


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Lcom/lumiyaviewer/lumiya/voice/VivoxController$ControllerThread;,
        Lcom/lumiyaviewer/lumiya/voice/VivoxController$VivoxInitException;
    }
.end annotation


# static fields
.field private static instance:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

.field private static final instanceLock:Ljava/lang/Object;


# instance fields
.field private final audioManager:Landroid/media/AudioManager;

.field private bluetoothScoState:I

.field private connectedMessenger:Landroid/os/Messenger;
    .annotation runtime Ljavax/annotation/Nullable;
    .end annotation
.end field

.field private final controllerHandler:Landroid/os/Handler;

.field private controllerReady:Z

.field private final controllerReadyLock:Ljava/lang/Object;

.field private final controllerThread:Ljava/lang/Thread;

.field private final incomingMessengerRef:Ljava/util/concurrent/atomic/AtomicReference;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/concurrent/atomic/AtomicReference",
            "<",
            "Landroid/os/Messenger;",
            ">;"
        }
    .end annotation
.end field

.field private localMicEnabled:Z

.field private final mainThreadHandler:Landroid/os/Handler;

.field private final messageController:Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;

.field private final outgoingRequests:Ljava/util/Map;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/Map",
            "<",
            "Ljava/lang/String;",
            "Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;",
            ">;"
        }
    .end annotation
.end field

.field private voiceAccountConnection:Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceAccountConnection;
    .annotation runtime Ljavax/annotation/Nullable;
    .end annotation
.end field

.field private voiceConnector:Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceConnector;
    .annotation runtime Ljavax/annotation/Nullable;
    .end annotation
.end field

.field private final voiceSessions:Ljava/util/Map;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/Map",
            "<",
            "Ljava/lang/String;",
            "Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;",
            ">;"
        }
    .end annotation
.end field


# direct methods
.method static constructor <clinit>()V
    .locals 1

    .prologue
    .line 29
    new-instance v0, Ljava/lang/Object;

    invoke-direct {v0}, Ljava/lang/Object;-><init>()V

    sput-object v0, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->instanceLock:Ljava/lang/Object;

    .line 30
    const/4 v0, 0x0

    sput-object v0, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->instance:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    return-void
.end method

.method private constructor <init>(Landroid/content/Context;Landroid/os/Handler;)V
    .locals 6
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "mainThreadHandler"    # Landroid/os/Handler;
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Lcom/lumiyaviewer/lumiya/voice/VivoxController$VivoxInitException;
        }
    .end annotation

    .prologue
    const/4 v5, 0x0

    const/4 v4, 0x0

    .line 71
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 32
    new-instance v3, Ljava/util/concurrent/atomic/AtomicReference;

    invoke-direct {v3, v4}, Ljava/util/concurrent/atomic/AtomicReference;-><init>(Ljava/lang/Object;)V

    iput-object v3, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->incomingMessengerRef:Ljava/util/concurrent/atomic/AtomicReference;

    .line 38
    new-instance v3, Ljava/util/HashMap;

    invoke-direct {v3}, Ljava/util/HashMap;-><init>()V

    invoke-static {v3}, Ljava/util/Collections;->synchronizedMap(Ljava/util/Map;)Ljava/util/Map;

    move-result-object v3

    iput-object v3, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->voiceSessions:Ljava/util/Map;

    .line 39
    new-instance v3, Ljava/util/HashMap;

    invoke-direct {v3}, Ljava/util/HashMap;-><init>()V

    invoke-static {v3}, Ljava/util/Collections;->synchronizedMap(Ljava/util/Map;)Ljava/util/Map;

    move-result-object v3

    iput-object v3, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->outgoingRequests:Ljava/util/Map;

    .line 41
    new-instance v3, Ljava/lang/Object;

    invoke-direct {v3}, Ljava/lang/Object;-><init>()V

    iput-object v3, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->controllerReadyLock:Ljava/lang/Object;

    .line 43
    const/4 v3, -0x1

    iput v3, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->bluetoothScoState:I

    .line 44
    iput-boolean v5, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->controllerReady:Z

    .line 46
    iput-object v4, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->connectedMessenger:Landroid/os/Messenger;

    .line 47
    iput-object v4, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->voiceConnector:Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceConnector;

    .line 48
    iput-object v4, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->voiceAccountConnection:Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceAccountConnection;

    .line 49
    iput-boolean v5, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->localMicEnabled:Z

    .line 74
    :try_start_0
    invoke-static {}, Lcom/vivox/service/VxClientProxy;->vx_initialize()I

    move-result v2

    .line 75
    .local v2, "rc":I
    if-eqz v2, :cond_0

    .line 76
    new-instance v3, Lcom/lumiyaviewer/lumiya/voice/VivoxController$VivoxInitException;

    const-string v4, "Failed to initialize voice subsystem"

    invoke-direct {v3, v4}, Lcom/lumiyaviewer/lumiya/voice/VivoxController$VivoxInitException;-><init>(Ljava/lang/String;)V

    throw v3
    :try_end_0
    .catch Ljava/lang/UnsatisfiedLinkError; {:try_start_0 .. :try_end_0} :catch_0

    .line 77
    .end local v2    # "rc":I
    :catch_0
    move-exception v1

    .line 78
    .local v1, "e":Ljava/lang/UnsatisfiedLinkError;
    new-instance v3, Lcom/lumiyaviewer/lumiya/voice/VivoxController$VivoxInitException;

    const-string v4, "Failed to initialize voice subsystem"

    invoke-direct {v3, v4, v1}, Lcom/lumiyaviewer/lumiya/voice/VivoxController$VivoxInitException;-><init>(Ljava/lang/String;Ljava/lang/Throwable;)V

    throw v3

    .line 81
    .end local v1    # "e":Ljava/lang/UnsatisfiedLinkError;
    .restart local v2    # "rc":I
    :cond_0
    iput-object p2, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->mainThreadHandler:Landroid/os/Handler;

    .line 83
    const-string v3, "audio"

    invoke-virtual {p1, v3}, Landroid/content/Context;->getSystemService(Ljava/lang/String;)Ljava/lang/Object;

    move-result-object v3

    check-cast v3, Landroid/media/AudioManager;

    iput-object v3, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->audioManager:Landroid/media/AudioManager;

    .line 85
    new-instance v0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$ControllerThread;

    invoke-direct {v0, p0, v4}, Lcom/lumiyaviewer/lumiya/voice/VivoxController$ControllerThread;-><init>(Lcom/lumiyaviewer/lumiya/voice/VivoxController;Lcom/lumiyaviewer/lumiya/voice/VivoxController$1;)V

    .line 86
    .local v0, "cThread":Lcom/lumiyaviewer/lumiya/voice/VivoxController$ControllerThread;
    new-instance v3, Ljava/lang/Thread;

    const-string v4, "VivoxController"

    invoke-direct {v3, v0, v4}, Ljava/lang/Thread;-><init>(Ljava/lang/Runnable;Ljava/lang/String;)V

    iput-object v3, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->controllerThread:Ljava/lang/Thread;

    .line 87
    iget-object v3, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->controllerThread:Ljava/lang/Thread;

    invoke-virtual {v3}, Ljava/lang/Thread;->start()V

    .line 90
    :try_start_1
    iget-object v4, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->controllerReadyLock:Ljava/lang/Object;

    monitor-enter v4
    :try_end_1
    .catch Ljava/lang/InterruptedException; {:try_start_1 .. :try_end_1} :catch_1

    .line 91
    :goto_0
    :try_start_2
    iget-boolean v3, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->controllerReady:Z

    if-nez v3, :cond_1

    .line 92
    iget-object v3, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->controllerReadyLock:Ljava/lang/Object;

    invoke-virtual {v3}, Ljava/lang/Object;->wait()V

    goto :goto_0

    .line 96
    :catchall_0
    move-exception v3

    monitor-exit v4
    :try_end_2
    .catchall {:try_start_2 .. :try_end_2} :catchall_0

    :try_start_3
    throw v3
    :try_end_3
    .catch Ljava/lang/InterruptedException; {:try_start_3 .. :try_end_3} :catch_1

    .line 97
    :catch_1
    move-exception v1

    .line 98
    .local v1, "e":Ljava/lang/InterruptedException;
    new-instance v3, Lcom/lumiyaviewer/lumiya/voice/VivoxController$VivoxInitException;

    const-string v4, "Failed to initialize voice subsystem"

    invoke-direct {v3, v4, v1}, Lcom/lumiyaviewer/lumiya/voice/VivoxController$VivoxInitException;-><init>(Ljava/lang/String;Ljava/lang/Throwable;)V

    throw v3

    .line 94
    .end local v1    # "e":Ljava/lang/InterruptedException;
    :cond_1
    :try_start_4
    invoke-static {v0}, Lcom/lumiyaviewer/lumiya/voice/VivoxController$ControllerThread;->access$100(Lcom/lumiyaviewer/lumiya/voice/VivoxController$ControllerThread;)Landroid/os/Handler;

    move-result-object v3

    iput-object v3, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->controllerHandler:Landroid/os/Handler;

    .line 95
    invoke-static {v0}, Lcom/lumiyaviewer/lumiya/voice/VivoxController$ControllerThread;->access$200(Lcom/lumiyaviewer/lumiya/voice/VivoxController$ControllerThread;)Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;

    move-result-object v3

    iput-object v3, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->messageController:Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;

    .line 96
    monitor-exit v4
    :try_end_4
    .catchall {:try_start_4 .. :try_end_4} :catchall_0

    .line 100
    return-void
.end method

.method static synthetic access$1002(Lcom/lumiyaviewer/lumiya/voice/VivoxController;Z)Z
    .locals 0
    .param p0, "x0"    # Lcom/lumiyaviewer/lumiya/voice/VivoxController;
    .param p1, "x1"    # Z

    .prologue
    .line 27
    iput-boolean p1, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->localMicEnabled:Z

    return p1
.end method

.method static synthetic access$1102(Lcom/lumiyaviewer/lumiya/voice/VivoxController;Landroid/os/Messenger;)Landroid/os/Messenger;
    .locals 0
    .param p0, "x0"    # Lcom/lumiyaviewer/lumiya/voice/VivoxController;
    .param p1, "x1"    # Landroid/os/Messenger;

    .prologue
    .line 27
    iput-object p1, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->connectedMessenger:Landroid/os/Messenger;

    return-object p1
.end method

.method static synthetic access$1200(Lcom/lumiyaviewer/lumiya/voice/VivoxController;)Ljava/util/Map;
    .locals 1
    .param p0, "x0"    # Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    .prologue
    .line 27
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->voiceSessions:Ljava/util/Map;

    return-object v0
.end method

.method static synthetic access$1300(Lcom/lumiyaviewer/lumiya/voice/VivoxController;)Ljava/util/Map;
    .locals 1
    .param p0, "x0"    # Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    .prologue
    .line 27
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->outgoingRequests:Ljava/util/Map;

    return-object v0
.end method

.method static synthetic access$1400(Lcom/lumiyaviewer/lumiya/voice/VivoxController;Z)V
    .locals 0
    .param p0, "x0"    # Lcom/lumiyaviewer/lumiya/voice/VivoxController;
    .param p1, "x1"    # Z

    .prologue
    .line 27
    invoke-direct {p0, p1}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->setLocalMicEnabled(Z)V

    return-void
.end method

.method static synthetic access$1500(Lcom/lumiyaviewer/lumiya/voice/VivoxController;)Z
    .locals 1
    .param p0, "x0"    # Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    .prologue
    .line 27
    invoke-direct {p0}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->hasActiveSession()Z

    move-result v0

    return v0
.end method

.method static synthetic access$1600(Lcom/lumiyaviewer/lumiya/voice/VivoxController;)I
    .locals 1
    .param p0, "x0"    # Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    .prologue
    .line 27
    iget v0, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->bluetoothScoState:I

    return v0
.end method

.method static synthetic access$1602(Lcom/lumiyaviewer/lumiya/voice/VivoxController;I)I
    .locals 0
    .param p0, "x0"    # Lcom/lumiyaviewer/lumiya/voice/VivoxController;
    .param p1, "x1"    # I

    .prologue
    .line 27
    iput p1, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->bluetoothScoState:I

    return p1
.end method

.method static synthetic access$1700(Lcom/lumiyaviewer/lumiya/voice/VivoxController;Z)V
    .locals 0
    .param p0, "x0"    # Lcom/lumiyaviewer/lumiya/voice/VivoxController;
    .param p1, "x1"    # Z

    .prologue
    .line 27
    invoke-direct {p0, p1}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->setBluetoothEnable(Z)V

    return-void
.end method

.method static synthetic access$1800(Lcom/lumiyaviewer/lumiya/voice/VivoxController;)V
    .locals 0
    .param p0, "x0"    # Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    .prologue
    .line 27
    invoke-direct {p0}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->tryStartingBluetooth()V

    return-void
.end method

.method static synthetic access$300(Lcom/lumiyaviewer/lumiya/voice/VivoxController;)Ljava/lang/Object;
    .locals 1
    .param p0, "x0"    # Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    .prologue
    .line 27
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->controllerReadyLock:Ljava/lang/Object;

    return-object v0
.end method

.method static synthetic access$402(Lcom/lumiyaviewer/lumiya/voice/VivoxController;Z)Z
    .locals 0
    .param p0, "x0"    # Lcom/lumiyaviewer/lumiya/voice/VivoxController;
    .param p1, "x1"    # Z

    .prologue
    .line 27
    iput-boolean p1, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->controllerReady:Z

    return p1
.end method

.method static synthetic access$500(Lcom/lumiyaviewer/lumiya/voice/VivoxController;)Landroid/media/AudioManager;
    .locals 1
    .param p0, "x0"    # Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    .prologue
    .line 27
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->audioManager:Landroid/media/AudioManager;

    return-object v0
.end method

.method static synthetic access$600(Lcom/lumiyaviewer/lumiya/voice/VivoxController;)Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceAccountConnection;
    .locals 1
    .param p0, "x0"    # Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    .prologue
    .line 27
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->voiceAccountConnection:Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceAccountConnection;

    return-object v0
.end method

.method static synthetic access$602(Lcom/lumiyaviewer/lumiya/voice/VivoxController;Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceAccountConnection;)Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceAccountConnection;
    .locals 0
    .param p0, "x0"    # Lcom/lumiyaviewer/lumiya/voice/VivoxController;
    .param p1, "x1"    # Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceAccountConnection;

    .prologue
    .line 27
    iput-object p1, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->voiceAccountConnection:Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceAccountConnection;

    return-object p1
.end method

.method static synthetic access$700(Lcom/lumiyaviewer/lumiya/voice/VivoxController;)V
    .locals 0
    .param p0, "x0"    # Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    .prologue
    .line 27
    invoke-direct {p0}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->closeAllSessions()V

    return-void
.end method

.method static synthetic access$800(Lcom/lumiyaviewer/lumiya/voice/VivoxController;)Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceConnector;
    .locals 1
    .param p0, "x0"    # Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    .prologue
    .line 27
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->voiceConnector:Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceConnector;

    return-object v0
.end method

.method static synthetic access$802(Lcom/lumiyaviewer/lumiya/voice/VivoxController;Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceConnector;)Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceConnector;
    .locals 0
    .param p0, "x0"    # Lcom/lumiyaviewer/lumiya/voice/VivoxController;
    .param p1, "x1"    # Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceConnector;

    .prologue
    .line 27
    iput-object p1, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->voiceConnector:Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceConnector;

    return-object p1
.end method

.method static synthetic access$900(Lcom/lumiyaviewer/lumiya/voice/VivoxController;)Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;
    .locals 1
    .param p0, "x0"    # Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    .prologue
    .line 27
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->messageController:Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;

    return-object v0
.end method

.method private closeAllSessions()V
    .locals 4

    .prologue
    .line 300
    iget-object v2, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->voiceSessions:Ljava/util/Map;

    invoke-interface {v2}, Ljava/util/Map;->values()Ljava/util/Collection;

    move-result-object v2

    invoke-static {v2}, Lcom/google/common/collect/ImmutableList;->copyOf(Ljava/util/Collection;)Lcom/google/common/collect/ImmutableList;

    move-result-object v0

    .line 301
    .local v0, "activeSessions":Lcom/google/common/collect/ImmutableList;, "Lcom/google/common/collect/ImmutableList<Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;>;"
    invoke-virtual {v0}, Lcom/google/common/collect/ImmutableList;->iterator()Lcom/google/common/collect/UnmodifiableIterator;

    move-result-object v2

    :goto_0
    invoke-interface {v2}, Ljava/util/Iterator;->hasNext()Z

    move-result v3

    if-eqz v3, :cond_0

    invoke-interface {v2}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;

    .line 302
    .local v1, "session":Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;
    invoke-virtual {v1}, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;->dispose()V

    goto :goto_0

    .line 303
    .end local v1    # "session":Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;
    :cond_0
    return-void
.end method

.method public static getInstance(Landroid/content/Context;Landroid/os/Handler;Landroid/os/Messenger;)Lcom/lumiyaviewer/lumiya/voice/VivoxController;
    .locals 2
    .param p0, "context"    # Landroid/content/Context;
    .param p1, "mainThreadHandler"    # Landroid/os/Handler;
    .param p2, "incomingMessenger"    # Landroid/os/Messenger;
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Lcom/lumiyaviewer/lumiya/voice/VivoxController$VivoxInitException;
        }
    .end annotation

    .prologue
    .line 63
    sget-object v1, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->instanceLock:Ljava/lang/Object;

    monitor-enter v1

    .line 64
    :try_start_0
    sget-object v0, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->instance:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    if-nez v0, :cond_0

    .line 65
    new-instance v0, Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    invoke-direct {v0, p0, p1}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;-><init>(Landroid/content/Context;Landroid/os/Handler;)V

    sput-object v0, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->instance:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    .line 66
    :cond_0
    monitor-exit v1
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    .line 67
    sget-object v0, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->instance:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    invoke-virtual {v0, p2}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->setIncomingMessenger(Landroid/os/Messenger;)V

    .line 68
    sget-object v0, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->instance:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    return-object v0

    .line 66
    :catchall_0
    move-exception v0

    :try_start_1
    monitor-exit v1
    :try_end_1
    .catchall {:try_start_1 .. :try_end_1} :catchall_0

    throw v0
.end method

.method private hasActiveSession()Z
    .locals 5

    .prologue
    .line 491
    const/4 v0, 0x0

    .line 492
    .local v0, "hasActiveSession":Z
    iget-object v2, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->voiceSessions:Ljava/util/Map;

    invoke-interface {v2}, Ljava/util/Map;->values()Ljava/util/Collection;

    move-result-object v2

    invoke-interface {v2}, Ljava/util/Collection;->iterator()Ljava/util/Iterator;

    move-result-object v2

    :cond_0
    invoke-interface {v2}, Ljava/util/Iterator;->hasNext()Z

    move-result v3

    if-eqz v3, :cond_1

    invoke-interface {v2}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;

    .line 493
    .local v1, "session":Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;
    invoke-virtual {v1}, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;->getState()Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo$VoiceChatState;

    move-result-object v3

    sget-object v4, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo$VoiceChatState;->Active:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo$VoiceChatState;

    if-ne v3, v4, :cond_0

    .line 494
    const/4 v0, 0x1

    .line 498
    .end local v1    # "session":Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;
    :cond_1
    return v0
.end method

.method private setAudioVoiceMode(Z)V
    .locals 3
    .param p1, "voice"    # Z

    .prologue
    const/4 v2, 0x1

    .line 236
    if-eqz p1, :cond_1

    .line 237
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->mainThreadHandler:Landroid/os/Handler;

    new-instance v1, Lcom/lumiyaviewer/lumiya/voice/VivoxController$1;

    invoke-direct {v1, p0}, Lcom/lumiyaviewer/lumiya/voice/VivoxController$1;-><init>(Lcom/lumiyaviewer/lumiya/voice/VivoxController;)V

    invoke-virtual {v0, v1}, Landroid/os/Handler;->post(Ljava/lang/Runnable;)Z

    .line 255
    iget v0, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->bluetoothScoState:I

    if-ne v0, v2, :cond_0

    .line 256
    invoke-direct {p0, v2}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->setBluetoothEnable(Z)V

    .line 287
    :goto_0
    return-void

    .line 258
    :cond_0
    invoke-direct {p0}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->tryStartingBluetooth()V

    goto :goto_0

    .line 261
    :cond_1
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->mainThreadHandler:Landroid/os/Handler;

    new-instance v1, Lcom/lumiyaviewer/lumiya/voice/VivoxController$2;

    invoke-direct {v1, p0}, Lcom/lumiyaviewer/lumiya/voice/VivoxController$2;-><init>(Lcom/lumiyaviewer/lumiya/voice/VivoxController;)V

    invoke-virtual {v0, v1}, Landroid/os/Handler;->post(Ljava/lang/Runnable;)Z

    goto :goto_0
.end method

.method private setBluetoothEnable(Z)V
    .locals 2
    .param p1, "enable"    # Z

    .prologue
    .line 599
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->mainThreadHandler:Landroid/os/Handler;

    new-instance v1, Lcom/lumiyaviewer/lumiya/voice/VivoxController$13;

    invoke-direct {v1, p0, p1}, Lcom/lumiyaviewer/lumiya/voice/VivoxController$13;-><init>(Lcom/lumiyaviewer/lumiya/voice/VivoxController;Z)V

    invoke-virtual {v0, v1}, Landroid/os/Handler;->post(Ljava/lang/Runnable;)Z

    .line 612
    return-void
.end method

.method private setLocalMicEnabled(Z)V
    .locals 3
    .param p1, "enabled"    # Z

    .prologue
    .line 479
    iget-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->voiceConnector:Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceConnector;

    if-eqz v1, :cond_2

    .line 480
    iget-object v2, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->voiceConnector:Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceConnector;

    if-nez p1, :cond_1

    const/4 v1, 0x1

    :goto_0
    invoke-virtual {v2, v1}, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceConnector;->setMuteLocalMic(Z)V

    .line 481
    iput-boolean p1, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->localMicEnabled:Z

    .line 482
    iget-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->voiceSessions:Ljava/util/Map;

    invoke-interface {v1}, Ljava/util/Map;->values()Ljava/util/Collection;

    move-result-object v1

    invoke-interface {v1}, Ljava/util/Collection;->iterator()Ljava/util/Iterator;

    move-result-object v1

    :cond_0
    :goto_1
    invoke-interface {v1}, Ljava/util/Iterator;->hasNext()Z

    move-result v2

    if-eqz v2, :cond_2

    invoke-interface {v1}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;

    .line 483
    .local v0, "session":Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;
    invoke-virtual {v0, p1}, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;->setLocalMicActive(Z)Z

    move-result v2

    if-eqz v2, :cond_0

    .line 484
    invoke-direct {p0, v0}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->updateSessionState(Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;)V

    goto :goto_1

    .line 480
    .end local v0    # "session":Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;
    :cond_1
    const/4 v1, 0x0

    goto :goto_0

    .line 488
    :cond_2
    return-void
.end method

.method private tryStartingBluetooth()V
    .locals 2

    .prologue
    .line 615
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->mainThreadHandler:Landroid/os/Handler;

    new-instance v1, Lcom/lumiyaviewer/lumiya/voice/VivoxController$14;

    invoke-direct {v1, p0}, Lcom/lumiyaviewer/lumiya/voice/VivoxController$14;-><init>(Lcom/lumiyaviewer/lumiya/voice/VivoxController;)V

    invoke-virtual {v0, v1}, Landroid/os/Handler;->post(Ljava/lang/Runnable;)Z

    .line 628
    return-void
.end method

.method private updateSessionState(Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;)V
    .locals 6
    .param p1, "session"    # Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;

    .prologue
    .line 290
    iget-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->connectedMessenger:Landroid/os/Messenger;

    if-eqz v1, :cond_0

    .line 291
    invoke-virtual {p1}, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;->getVoiceChatInfo()Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;

    move-result-object v0

    .line 292
    .local v0, "voiceChatInfo":Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;
    const-string v1, "Voice: Updating session state: %s"

    const/4 v2, 0x1

    new-array v2, v2, [Ljava/lang/Object;

    const/4 v3, 0x0

    aput-object v0, v2, v3

    invoke-static {v1, v2}, Lcom/lumiyaviewer/lumiya/voice/Debug;->Printf(Ljava/lang/String;[Ljava/lang/Object;)V

    .line 293
    iget-object v2, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->connectedMessenger:Landroid/os/Messenger;

    sget-object v3, Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;->VoiceChannelStatus:Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;

    new-instance v4, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceChannelStatus;

    .line 295
    invoke-virtual {p1}, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;->getVoiceChannelInfo()Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;

    move-result-object v1

    const/4 v5, 0x0

    invoke-direct {v4, v1, v0, v5}, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceChannelStatus;-><init>(Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;Ljava/lang/String;)V

    iget-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->incomingMessengerRef:Ljava/util/concurrent/atomic/AtomicReference;

    invoke-virtual {v1}, Ljava/util/concurrent/atomic/AtomicReference;->get()Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Landroid/os/Messenger;

    .line 293
    invoke-static {v2, v3, v4, v1}, Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessenger;->sendMessage(Landroid/os/Messenger;Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessage;Landroid/os/Messenger;)Z

    .line 297
    .end local v0    # "voiceChatInfo":Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo;
    :cond_0
    return-void
.end method


# virtual methods
.method public AcceptCall(Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceAcceptCall;)V
    .locals 2
    .param p1, "message"    # Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceAcceptCall;

    .prologue
    .line 431
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->controllerHandler:Landroid/os/Handler;

    new-instance v1, Lcom/lumiyaviewer/lumiya/voice/VivoxController$7;

    invoke-direct {v1, p0, p1}, Lcom/lumiyaviewer/lumiya/voice/VivoxController$7;-><init>(Lcom/lumiyaviewer/lumiya/voice/VivoxController;Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceAcceptCall;)V

    invoke-virtual {v0, v1}, Landroid/os/Handler;->post(Ljava/lang/Runnable;)Z

    .line 457
    return-void
.end method

.method public ConnectChannel(Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;Ljava/lang/String;Landroid/os/Messenger;)V
    .locals 2
    .param p1, "voiceChannelInfo"    # Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;
    .param p2, "channelCredentials"    # Ljava/lang/String;
        .annotation runtime Ljavax/annotation/Nullable;
        .end annotation
    .end param
    .param p3, "replyTo"    # Landroid/os/Messenger;

    .prologue
    .line 362
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->controllerHandler:Landroid/os/Handler;

    new-instance v1, Lcom/lumiyaviewer/lumiya/voice/VivoxController$4;

    invoke-direct {v1, p0, p1, p2, p3}, Lcom/lumiyaviewer/lumiya/voice/VivoxController$4;-><init>(Lcom/lumiyaviewer/lumiya/voice/VivoxController;Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;Ljava/lang/String;Landroid/os/Messenger;)V

    invoke-virtual {v0, v1}, Landroid/os/Handler;->post(Ljava/lang/Runnable;)Z

    .line 396
    return-void
.end method

.method public EnableVoiceMic(Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceEnableMic;)V
    .locals 2
    .param p1, "message"    # Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceEnableMic;

    .prologue
    .line 502
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->controllerHandler:Landroid/os/Handler;

    new-instance v1, Lcom/lumiyaviewer/lumiya/voice/VivoxController$9;

    invoke-direct {v1, p0, p1}, Lcom/lumiyaviewer/lumiya/voice/VivoxController$9;-><init>(Lcom/lumiyaviewer/lumiya/voice/VivoxController;Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceEnableMic;)V

    invoke-virtual {v0, v1}, Landroid/os/Handler;->post(Ljava/lang/Runnable;)Z

    .line 516
    return-void
.end method

.method public Login(Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceLoginInfo;Landroid/os/Messenger;)V
    .locals 2
    .param p1, "voiceLoginInfo"    # Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceLoginInfo;
    .param p2, "replyTo"    # Landroid/os/Messenger;

    .prologue
    .line 306
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->controllerHandler:Landroid/os/Handler;

    new-instance v1, Lcom/lumiyaviewer/lumiya/voice/VivoxController$3;

    invoke-direct {v1, p0, p1, p2}, Lcom/lumiyaviewer/lumiya/voice/VivoxController$3;-><init>(Lcom/lumiyaviewer/lumiya/voice/VivoxController;Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceLoginInfo;Landroid/os/Messenger;)V

    invoke-virtual {v0, v1}, Landroid/os/Handler;->post(Ljava/lang/Runnable;)Z

    .line 359
    return-void
.end method

.method public Logout(Landroid/os/Messenger;)V
    .locals 2
    .param p1, "replyTo"    # Landroid/os/Messenger;

    .prologue
    .line 519
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->controllerHandler:Landroid/os/Handler;

    new-instance v1, Lcom/lumiyaviewer/lumiya/voice/VivoxController$10;

    invoke-direct {v1, p0, p1}, Lcom/lumiyaviewer/lumiya/voice/VivoxController$10;-><init>(Lcom/lumiyaviewer/lumiya/voice/VivoxController;Landroid/os/Messenger;)V

    invoke-virtual {v0, v1}, Landroid/os/Handler;->post(Ljava/lang/Runnable;)Z

    .line 542
    return-void
.end method

.method public RejectCall(Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceRejectCall;)V
    .locals 2
    .param p1, "message"    # Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceRejectCall;

    .prologue
    .line 415
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->controllerHandler:Landroid/os/Handler;

    new-instance v1, Lcom/lumiyaviewer/lumiya/voice/VivoxController$6;

    invoke-direct {v1, p0, p1}, Lcom/lumiyaviewer/lumiya/voice/VivoxController$6;-><init>(Lcom/lumiyaviewer/lumiya/voice/VivoxController;Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceRejectCall;)V

    invoke-virtual {v0, v1}, Landroid/os/Handler;->post(Ljava/lang/Runnable;)Z

    .line 428
    return-void
.end method

.method public Set3DPosition(Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceSet3DPosition;)V
    .locals 2
    .param p1, "message"    # Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceSet3DPosition;

    .prologue
    .line 399
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->controllerHandler:Landroid/os/Handler;

    new-instance v1, Lcom/lumiyaviewer/lumiya/voice/VivoxController$5;

    invoke-direct {v1, p0, p1}, Lcom/lumiyaviewer/lumiya/voice/VivoxController$5;-><init>(Lcom/lumiyaviewer/lumiya/voice/VivoxController;Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceSet3DPosition;)V

    invoke-virtual {v0, v1}, Landroid/os/Handler;->post(Ljava/lang/Runnable;)Z

    .line 412
    return-void
.end method

.method public SetAudioProperties(Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceSetAudioProperties;)V
    .locals 2
    .param p1, "message"    # Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceSetAudioProperties;

    .prologue
    .line 545
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->controllerHandler:Landroid/os/Handler;

    new-instance v1, Lcom/lumiyaviewer/lumiya/voice/VivoxController$11;

    invoke-direct {v1, p0, p1}, Lcom/lumiyaviewer/lumiya/voice/VivoxController$11;-><init>(Lcom/lumiyaviewer/lumiya/voice/VivoxController;Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceSetAudioProperties;)V

    invoke-virtual {v0, v1}, Landroid/os/Handler;->post(Ljava/lang/Runnable;)Z

    .line 584
    return-void
.end method

.method public TerminateCall(Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceTerminateCall;)V
    .locals 2
    .param p1, "message"    # Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceTerminateCall;

    .prologue
    .line 460
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->controllerHandler:Landroid/os/Handler;

    new-instance v1, Lcom/lumiyaviewer/lumiya/voice/VivoxController$8;

    invoke-direct {v1, p0, p1}, Lcom/lumiyaviewer/lumiya/voice/VivoxController$8;-><init>(Lcom/lumiyaviewer/lumiya/voice/VivoxController;Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceTerminateCall;)V

    invoke-virtual {v0, v1}, Landroid/os/Handler;->post(Ljava/lang/Runnable;)Z

    .line 476
    return-void
.end method

.method getIncomingMessenger()Landroid/os/Messenger;
    .locals 1

    .prologue
    .line 107
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->incomingMessengerRef:Ljava/util/concurrent/atomic/AtomicReference;

    invoke-virtual {v0}, Ljava/util/concurrent/atomic/AtomicReference;->get()Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Landroid/os/Messenger;

    return-object v0
.end method

.method onBluetoothScoStateChanged(I)V
    .locals 2
    .param p1, "state"    # I

    .prologue
    .line 587
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->controllerHandler:Landroid/os/Handler;

    new-instance v1, Lcom/lumiyaviewer/lumiya/voice/VivoxController$12;

    invoke-direct {v1, p0, p1}, Lcom/lumiyaviewer/lumiya/voice/VivoxController$12;-><init>(Lcom/lumiyaviewer/lumiya/voice/VivoxController;I)V

    invoke-virtual {v0, v1}, Landroid/os/Handler;->post(Ljava/lang/Runnable;)Z

    .line 596
    return-void
.end method

.method public onVivoxEvent(Lcom/vivox/service/vx_evt_base_t;)V
    .locals 24
    .param p1, "vxEvent"    # Lcom/vivox/service/vx_evt_base_t;

    .prologue
    .line 136
    const-string v19, "Voice: got vivox event: %s"

    const/16 v20, 0x1

    move/from16 v0, v20

    new-array v0, v0, [Ljava/lang/Object;

    move-object/from16 v20, v0

    const/16 v21, 0x0

    aput-object p1, v20, v21

    invoke-static/range {v19 .. v20}, Lcom/lumiyaviewer/lumiya/voice/Debug;->Printf(Ljava/lang/String;[Ljava/lang/Object;)V

    .line 137
    if-eqz p1, :cond_0

    .line 138
    invoke-virtual/range {p1 .. p1}, Lcom/vivox/service/vx_evt_base_t;->getType()Lcom/vivox/service/vx_event_type;

    move-result-object v16

    .line 139
    .local v16, "type":Lcom/vivox/service/vx_event_type;
    const-string v19, "Voice: vx_event_type %s"

    const/16 v20, 0x1

    move/from16 v0, v20

    new-array v0, v0, [Ljava/lang/Object;

    move-object/from16 v20, v0

    const/16 v21, 0x0

    aput-object v16, v20, v21

    invoke-static/range {v19 .. v20}, Lcom/lumiyaviewer/lumiya/voice/Debug;->Printf(Ljava/lang/String;[Ljava/lang/Object;)V

    .line 141
    sget-object v19, Lcom/vivox/service/vx_event_type;->evt_session_added:Lcom/vivox/service/vx_event_type;

    move-object/from16 v0, v16

    move-object/from16 v1, v19

    if-ne v0, v1, :cond_1

    .line 143
    invoke-virtual/range {p1 .. p1}, Lcom/vivox/service/vx_evt_base_t;->getMessage()Lcom/vivox/service/vx_message_base_t;

    move-result-object v19

    invoke-static/range {v19 .. v19}, Lcom/vivox/service/VxClientProxy;->vx_message_base_t2vx_evt_session_added_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_evt_session_added_t;

    move-result-object v12

    .line 144
    .local v12, "sessionAdded":Lcom/vivox/service/vx_evt_session_added_t;
    if-eqz v12, :cond_0

    .line 146
    move-object/from16 v0, p0

    iget-object v0, v0, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->outgoingRequests:Ljava/util/Map;

    move-object/from16 v19, v0

    invoke-virtual {v12}, Lcom/vivox/service/vx_evt_session_added_t;->getUri()Ljava/lang/String;

    move-result-object v20

    invoke-interface/range {v19 .. v20}, Ljava/util/Map;->remove(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v9

    check-cast v9, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;

    .line 148
    .local v9, "outgoingRequest":Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;
    new-instance v8, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;

    move-object/from16 v0, p0

    iget-object v0, v0, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->messageController:Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;

    move-object/from16 v19, v0

    move-object/from16 v0, v19

    invoke-direct {v8, v0, v12, v9}, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;-><init>(Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;Lcom/vivox/service/vx_evt_session_added_t;Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;)V

    .line 149
    .local v8, "newSession":Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;
    move-object/from16 v0, p0

    iget-object v0, v0, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->voiceSessions:Ljava/util/Map;

    move-object/from16 v19, v0

    invoke-virtual {v8}, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;->getHandle()Ljava/lang/String;

    move-result-object v20

    move-object/from16 v0, v19

    move-object/from16 v1, v20

    invoke-interface {v0, v1, v8}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    .line 150
    const-string v19, "Voice: session added: %s (%s)"

    const/16 v20, 0x2

    move/from16 v0, v20

    new-array v0, v0, [Ljava/lang/Object;

    move-object/from16 v20, v0

    const/16 v21, 0x0

    invoke-virtual {v8}, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;->getHandle()Ljava/lang/String;

    move-result-object v22

    aput-object v22, v20, v21

    const/16 v21, 0x1

    invoke-virtual {v8}, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;->getVoiceChannelInfo()Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;

    move-result-object v22

    move-object/from16 v0, v22

    iget-object v0, v0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;->voiceChannelURI:Ljava/lang/String;

    move-object/from16 v22, v0

    aput-object v22, v20, v21

    invoke-static/range {v19 .. v20}, Lcom/lumiyaviewer/lumiya/voice/Debug;->Printf(Ljava/lang/String;[Ljava/lang/Object;)V

    .line 151
    move-object/from16 v0, p0

    invoke-direct {v0, v8}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->updateSessionState(Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;)V

    .line 233
    .end local v8    # "newSession":Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;
    .end local v9    # "outgoingRequest":Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;
    .end local v12    # "sessionAdded":Lcom/vivox/service/vx_evt_session_added_t;
    .end local v16    # "type":Lcom/vivox/service/vx_event_type;
    :cond_0
    :goto_0
    return-void

    .line 155
    .restart local v16    # "type":Lcom/vivox/service/vx_event_type;
    :cond_1
    sget-object v19, Lcom/vivox/service/vx_event_type;->evt_session_removed:Lcom/vivox/service/vx_event_type;

    move-object/from16 v0, v16

    move-object/from16 v1, v19

    if-ne v0, v1, :cond_3

    .line 157
    invoke-virtual/range {p1 .. p1}, Lcom/vivox/service/vx_evt_base_t;->getMessage()Lcom/vivox/service/vx_message_base_t;

    move-result-object v19

    invoke-static/range {v19 .. v19}, Lcom/vivox/service/VxClientProxy;->vx_message_base_t2vx_evt_session_removed_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_evt_session_removed_t;

    move-result-object v13

    .line 158
    .local v13, "sessionRemoved":Lcom/vivox/service/vx_evt_session_removed_t;
    if-eqz v13, :cond_0

    .line 159
    move-object/from16 v0, p0

    iget-object v0, v0, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->voiceSessions:Ljava/util/Map;

    move-object/from16 v19, v0

    invoke-virtual {v13}, Lcom/vivox/service/vx_evt_session_removed_t;->getSession_handle()Ljava/lang/String;

    move-result-object v20

    invoke-interface/range {v19 .. v20}, Ljava/util/Map;->remove(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v11

    check-cast v11, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;

    .line 160
    .local v11, "session":Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;
    if-eqz v11, :cond_0

    .line 161
    const-string v19, "Voice: session removed: %s"

    const/16 v20, 0x1

    move/from16 v0, v20

    new-array v0, v0, [Ljava/lang/Object;

    move-object/from16 v20, v0

    const/16 v21, 0x0

    invoke-virtual {v11}, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;->getVoiceChannelInfo()Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;

    move-result-object v22

    move-object/from16 v0, v22

    iget-object v0, v0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;->voiceChannelURI:Ljava/lang/String;

    move-object/from16 v22, v0

    aput-object v22, v20, v21

    invoke-static/range {v19 .. v20}, Lcom/lumiyaviewer/lumiya/voice/Debug;->Printf(Ljava/lang/String;[Ljava/lang/Object;)V

    .line 162
    sget-object v19, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo$VoiceChatState;->None:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo$VoiceChatState;

    move-object/from16 v0, v19

    invoke-virtual {v11, v0}, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;->setState(Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo$VoiceChatState;)Z

    move-result v19

    if-eqz v19, :cond_2

    .line 163
    move-object/from16 v0, p0

    invoke-direct {v0, v11}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->updateSessionState(Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;)V

    .line 165
    :cond_2
    move-object/from16 v0, p0

    iget-object v0, v0, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->connectedMessenger:Landroid/os/Messenger;

    move-object/from16 v20, v0

    sget-object v21, Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;->VoiceChannelClosed:Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;

    new-instance v22, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceChannelClosed;

    .line 167
    invoke-virtual {v11}, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;->getVoiceChannelInfo()Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;

    move-result-object v19

    move-object/from16 v0, v22

    move-object/from16 v1, v19

    invoke-direct {v0, v1}, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceChannelClosed;-><init>(Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;)V

    move-object/from16 v0, p0

    iget-object v0, v0, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->incomingMessengerRef:Ljava/util/concurrent/atomic/AtomicReference;

    move-object/from16 v19, v0

    invoke-virtual/range {v19 .. v19}, Ljava/util/concurrent/atomic/AtomicReference;->get()Ljava/lang/Object;

    move-result-object v19

    check-cast v19, Landroid/os/Messenger;

    .line 165
    move-object/from16 v0, v20

    move-object/from16 v1, v21

    move-object/from16 v2, v22

    move-object/from16 v3, v19

    invoke-static {v0, v1, v2, v3}, Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessenger;->sendMessage(Landroid/os/Messenger;Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessage;Landroid/os/Messenger;)Z

    goto :goto_0

    .line 172
    .end local v11    # "session":Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;
    .end local v13    # "sessionRemoved":Lcom/vivox/service/vx_evt_session_removed_t;
    :cond_3
    sget-object v19, Lcom/vivox/service/vx_event_type;->evt_participant_updated:Lcom/vivox/service/vx_event_type;

    move-object/from16 v0, v16

    move-object/from16 v1, v19

    if-ne v0, v1, :cond_5

    .line 174
    invoke-virtual/range {p1 .. p1}, Lcom/vivox/service/vx_evt_base_t;->getMessage()Lcom/vivox/service/vx_message_base_t;

    move-result-object v19

    invoke-static/range {v19 .. v19}, Lcom/vivox/service/VxClientProxy;->vx_message_base_t2vx_evt_participant_updated_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_evt_participant_updated_t;

    move-result-object v10

    .line 175
    .local v10, "participantUpdated":Lcom/vivox/service/vx_evt_participant_updated_t;
    if-eqz v10, :cond_0

    .line 176
    move-object/from16 v0, p0

    iget-object v0, v0, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->voiceAccountConnection:Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceAccountConnection;

    move-object/from16 v17, v0

    .line 177
    .local v17, "voiceAccountConnection":Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceAccountConnection;
    move-object/from16 v0, p0

    iget-object v0, v0, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->voiceSessions:Ljava/util/Map;

    move-object/from16 v19, v0

    invoke-virtual {v10}, Lcom/vivox/service/vx_evt_participant_updated_t;->getSession_handle()Ljava/lang/String;

    move-result-object v20

    invoke-interface/range {v19 .. v20}, Ljava/util/Map;->get(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v11

    check-cast v11, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;

    .line 178
    .restart local v11    # "session":Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;
    if-eqz v11, :cond_0

    if-eqz v17, :cond_0

    .line 179
    invoke-virtual {v10}, Lcom/vivox/service/vx_evt_participant_updated_t;->getIs_speaking()I

    move-result v19

    if-eqz v19, :cond_4

    const/4 v6, 0x1

    .line 180
    .local v6, "isSpeaking":Z
    :goto_1
    invoke-virtual {v10}, Lcom/vivox/service/vx_evt_participant_updated_t;->getParticipant_uri()Ljava/lang/String;

    move-result-object v19

    invoke-static/range {v19 .. v19}, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;->agentUUIDFromURI(Ljava/lang/String;)Ljava/util/UUID;

    move-result-object v14

    .line 181
    .local v14, "speakerID":Ljava/util/UUID;
    invoke-virtual/range {v17 .. v17}, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceAccountConnection;->getVoiceLoginInfo()Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceLoginInfo;

    move-result-object v19

    move-object/from16 v0, v19

    iget-object v0, v0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceLoginInfo;->agentUUID:Ljava/util/UUID;

    move-object/from16 v19, v0

    move-object/from16 v0, v19

    invoke-static {v14, v0}, Lcom/google/common/base/Objects;->equal(Ljava/lang/Object;Ljava/lang/Object;)Z

    move-result v5

    .line 182
    .local v5, "isMine":Z
    const-string v19, "Voice: speaking %b, speakerID %s (mine: %b)"

    const/16 v20, 0x3

    move/from16 v0, v20

    new-array v0, v0, [Ljava/lang/Object;

    move-object/from16 v20, v0

    const/16 v21, 0x0

    .line 183
    invoke-static {v6}, Ljava/lang/Boolean;->valueOf(Z)Ljava/lang/Boolean;

    move-result-object v22

    aput-object v22, v20, v21

    const/16 v21, 0x1

    aput-object v14, v20, v21

    const/16 v21, 0x2

    invoke-static {v5}, Ljava/lang/Boolean;->valueOf(Z)Ljava/lang/Boolean;

    move-result-object v22

    aput-object v22, v20, v21

    .line 182
    invoke-static/range {v19 .. v20}, Lcom/lumiyaviewer/lumiya/voice/Debug;->Printf(Ljava/lang/String;[Ljava/lang/Object;)V

    .line 184
    if-eqz v14, :cond_0

    if-nez v5, :cond_0

    .line 185
    invoke-virtual {v11, v14, v6}, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;->setSpeakerSpeaking(Ljava/util/UUID;Z)Z

    move-result v19

    if-eqz v19, :cond_0

    .line 186
    move-object/from16 v0, p0

    invoke-direct {v0, v11}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->updateSessionState(Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;)V

    goto/16 :goto_0

    .line 179
    .end local v5    # "isMine":Z
    .end local v6    # "isSpeaking":Z
    .end local v14    # "speakerID":Ljava/util/UUID;
    :cond_4
    const/4 v6, 0x0

    goto :goto_1

    .line 192
    .end local v10    # "participantUpdated":Lcom/vivox/service/vx_evt_participant_updated_t;
    .end local v11    # "session":Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;
    .end local v17    # "voiceAccountConnection":Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceAccountConnection;
    :cond_5
    sget-object v19, Lcom/vivox/service/vx_event_type;->evt_media_stream_updated:Lcom/vivox/service/vx_event_type;

    move-object/from16 v0, v16

    move-object/from16 v1, v19

    if-ne v0, v1, :cond_0

    .line 193
    invoke-virtual/range {p1 .. p1}, Lcom/vivox/service/vx_evt_base_t;->getMessage()Lcom/vivox/service/vx_message_base_t;

    move-result-object v19

    invoke-static/range {v19 .. v19}, Lcom/vivox/service/VxClientProxy;->vx_message_base_t2vx_evt_media_stream_updated_t(Lcom/vivox/service/vx_message_base_t;)Lcom/vivox/service/vx_evt_media_stream_updated_t;

    move-result-object v7

    .line 194
    .local v7, "mediaStreamUpdated":Lcom/vivox/service/vx_evt_media_stream_updated_t;
    if-eqz v7, :cond_0

    .line 195
    invoke-virtual {v7}, Lcom/vivox/service/vx_evt_media_stream_updated_t;->getState()Lcom/vivox/service/vx_session_media_state;

    move-result-object v15

    .line 196
    .local v15, "state":Lcom/vivox/service/vx_session_media_state;
    const-string v19, "Voice: media stream updated, state %s"

    const/16 v20, 0x1

    move/from16 v0, v20

    new-array v0, v0, [Ljava/lang/Object;

    move-object/from16 v20, v0

    const/16 v21, 0x0

    aput-object v15, v20, v21

    invoke-static/range {v19 .. v20}, Lcom/lumiyaviewer/lumiya/voice/Debug;->Printf(Ljava/lang/String;[Ljava/lang/Object;)V

    .line 198
    sget-object v19, Lcom/vivox/service/vx_session_media_state;->session_media_ringing:Lcom/vivox/service/vx_session_media_state;

    move-object/from16 v0, v19

    if-ne v15, v0, :cond_7

    .line 200
    move-object/from16 v0, p0

    iget-object v0, v0, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->voiceSessions:Ljava/util/Map;

    move-object/from16 v19, v0

    invoke-virtual {v7}, Lcom/vivox/service/vx_evt_media_stream_updated_t;->getSession_handle()Ljava/lang/String;

    move-result-object v20

    invoke-interface/range {v19 .. v20}, Ljava/util/Map;->get(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v18

    check-cast v18, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;

    .line 201
    .local v18, "voiceSession":Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;
    if-eqz v18, :cond_0

    invoke-virtual/range {v18 .. v18}, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;->isIncoming()Z

    move-result v19

    if-eqz v19, :cond_0

    .line 202
    invoke-virtual/range {v18 .. v18}, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;->getVoiceChannelInfo()Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;

    move-result-object v19

    invoke-virtual/range {v19 .. v19}, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;->getAgentUUID()Ljava/util/UUID;

    move-result-object v4

    .line 203
    .local v4, "agentUUID":Ljava/util/UUID;
    if-eqz v4, :cond_0

    move-object/from16 v0, p0

    iget-object v0, v0, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->connectedMessenger:Landroid/os/Messenger;

    move-object/from16 v19, v0

    if-eqz v19, :cond_0

    .line 204
    sget-object v19, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo$VoiceChatState;->Ringing:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo$VoiceChatState;

    invoke-virtual/range {v18 .. v19}, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;->setState(Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo$VoiceChatState;)Z

    move-result v19

    if-eqz v19, :cond_6

    .line 205
    move-object/from16 v0, p0

    move-object/from16 v1, v18

    invoke-direct {v0, v1}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->updateSessionState(Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;)V

    .line 206
    :cond_6
    move-object/from16 v0, p0

    iget-object v0, v0, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->connectedMessenger:Landroid/os/Messenger;

    move-object/from16 v20, v0

    sget-object v21, Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;->VoiceRinging:Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;

    new-instance v22, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceRinging;

    .line 208
    invoke-virtual/range {v18 .. v18}, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;->getHandle()Ljava/lang/String;

    move-result-object v19

    invoke-virtual/range {v18 .. v18}, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;->getVoiceChannelInfo()Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;

    move-result-object v23

    move-object/from16 v0, v22

    move-object/from16 v1, v19

    move-object/from16 v2, v23

    invoke-direct {v0, v1, v2, v4}, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceRinging;-><init>(Ljava/lang/String;Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;Ljava/util/UUID;)V

    move-object/from16 v0, p0

    iget-object v0, v0, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->incomingMessengerRef:Ljava/util/concurrent/atomic/AtomicReference;

    move-object/from16 v19, v0

    .line 209
    invoke-virtual/range {v19 .. v19}, Ljava/util/concurrent/atomic/AtomicReference;->get()Ljava/lang/Object;

    move-result-object v19

    check-cast v19, Landroid/os/Messenger;

    .line 206
    move-object/from16 v0, v20

    move-object/from16 v1, v21

    move-object/from16 v2, v22

    move-object/from16 v3, v19

    invoke-static {v0, v1, v2, v3}, Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessenger;->sendMessage(Landroid/os/Messenger;Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessage;Landroid/os/Messenger;)Z

    goto/16 :goto_0

    .line 214
    .end local v4    # "agentUUID":Ljava/util/UUID;
    .end local v18    # "voiceSession":Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;
    :cond_7
    sget-object v19, Lcom/vivox/service/vx_session_media_state;->session_media_connected:Lcom/vivox/service/vx_session_media_state;

    move-object/from16 v0, v19

    if-ne v15, v0, :cond_a

    .line 216
    move-object/from16 v0, p0

    iget-object v0, v0, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->voiceSessions:Ljava/util/Map;

    move-object/from16 v19, v0

    invoke-virtual {v7}, Lcom/vivox/service/vx_evt_media_stream_updated_t;->getSession_handle()Ljava/lang/String;

    move-result-object v20

    invoke-interface/range {v19 .. v20}, Ljava/util/Map;->get(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v18

    check-cast v18, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;

    .line 217
    .restart local v18    # "voiceSession":Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;
    if-eqz v18, :cond_0

    .line 218
    sget-object v19, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo$VoiceChatState;->Active:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo$VoiceChatState;

    invoke-virtual/range {v18 .. v19}, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;->setState(Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo$VoiceChatState;)Z

    move-result v19

    if-eqz v19, :cond_8

    .line 219
    move-object/from16 v0, p0

    move-object/from16 v1, v18

    invoke-direct {v0, v1}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->updateSessionState(Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;)V

    .line 220
    :cond_8
    invoke-virtual/range {v18 .. v18}, Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;->getVoiceChannelInfo()Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;

    move-result-object v19

    move-object/from16 v0, v19

    iget-boolean v0, v0, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;->isConference:Z

    move/from16 v19, v0

    if-nez v19, :cond_9

    .line 221
    const/16 v19, 0x1

    move-object/from16 v0, p0

    move/from16 v1, v19

    invoke-direct {v0, v1}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->setLocalMicEnabled(Z)V

    .line 223
    :cond_9
    const/16 v19, 0x1

    move-object/from16 v0, p0

    move/from16 v1, v19

    invoke-direct {v0, v1}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->setAudioVoiceMode(Z)V

    goto/16 :goto_0

    .line 226
    .end local v18    # "voiceSession":Lcom/lumiyaviewer/lumiya/voice/voicecon/VoiceSession;
    :cond_a
    sget-object v19, Lcom/vivox/service/vx_session_media_state;->session_media_disconnected:Lcom/vivox/service/vx_session_media_state;

    move-object/from16 v0, v19

    if-ne v15, v0, :cond_0

    .line 227
    const/16 v19, 0x0

    move-object/from16 v0, p0

    move/from16 v1, v19

    invoke-direct {v0, v1}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->setAudioVoiceMode(Z)V

    goto/16 :goto_0
.end method

.method public onVivoxMessage(Lcom/vivox/service/vx_message_base_t;)V
    .locals 3
    .param p1, "vxMessage"    # Lcom/vivox/service/vx_message_base_t;

    .prologue
    .line 131
    const-string v0, "Voice: got vivox message: %s"

    const/4 v1, 0x1

    new-array v1, v1, [Ljava/lang/Object;

    const/4 v2, 0x0

    aput-object p1, v1, v2

    invoke-static {v0, v1}, Lcom/lumiyaviewer/lumiya/voice/Debug;->Printf(Ljava/lang/String;[Ljava/lang/Object;)V

    .line 132
    return-void
.end method

.method setIncomingMessenger(Landroid/os/Messenger;)V
    .locals 1
    .param p1, "incomingMessenger"    # Landroid/os/Messenger;

    .prologue
    .line 103
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->incomingMessengerRef:Ljava/util/concurrent/atomic/AtomicReference;

    invoke-virtual {v0, p1}, Ljava/util/concurrent/atomic/AtomicReference;->set(Ljava/lang/Object;)V

    .line 104
    return-void
.end method
