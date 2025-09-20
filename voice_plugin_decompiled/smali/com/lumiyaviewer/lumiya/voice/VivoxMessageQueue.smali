.class public Lcom/lumiyaviewer/lumiya/voice/VivoxMessageQueue;
.super Ljava/lang/Object;
.source "VivoxMessageQueue.java"


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Lcom/lumiyaviewer/lumiya/voice/VivoxMessageQueue$InstanceHolder;
    }
.end annotation


# instance fields
.field private final messageLock:Ljava/lang/Object;


# direct methods
.method private constructor <init>()V
    .locals 2

    .prologue
    .line 16
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 10
    new-instance v0, Ljava/lang/Object;

    invoke-direct {v0}, Ljava/lang/Object;-><init>()V

    iput-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxMessageQueue;->messageLock:Ljava/lang/Object;

    .line 17
    const-string v0, "com/lumiyaviewer/lumiya/voice/VivoxMessageQueue"

    const-string v1, "handle_notification"

    invoke-static {v0, v1}, Lcom/vivox/service/VxClientProxy;->register_message_notification_handler(Ljava/lang/String;Ljava/lang/String;)I

    .line 18
    return-void
.end method

.method synthetic constructor <init>(Lcom/lumiyaviewer/lumiya/voice/VivoxMessageQueue$1;)V
    .locals 0
    .param p1, "x0"    # Lcom/lumiyaviewer/lumiya/voice/VivoxMessageQueue$1;

    .prologue
    .line 8
    invoke-direct {p0}, Lcom/lumiyaviewer/lumiya/voice/VivoxMessageQueue;-><init>()V

    return-void
.end method

.method public static getInstance()Lcom/lumiyaviewer/lumiya/voice/VivoxMessageQueue;
    .locals 1

    .prologue
    .line 21
    invoke-static {}, Lcom/lumiyaviewer/lumiya/voice/VivoxMessageQueue$InstanceHolder;->access$100()Lcom/lumiyaviewer/lumiya/voice/VivoxMessageQueue;

    move-result-object v0

    return-object v0
.end method

.method private handleNotification()V
    .locals 2

    .prologue
    .line 25
    iget-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxMessageQueue;->messageLock:Ljava/lang/Object;

    monitor-enter v1

    .line 26
    :try_start_0
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxMessageQueue;->messageLock:Ljava/lang/Object;

    invoke-virtual {v0}, Ljava/lang/Object;->notifyAll()V

    .line 27
    monitor-exit v1

    .line 28
    return-void

    .line 27
    :catchall_0
    move-exception v0

    monitor-exit v1
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    throw v0
.end method

.method public static handle_notification()V
    .locals 1

    .prologue
    .line 31
    invoke-static {}, Lcom/lumiyaviewer/lumiya/voice/VivoxMessageQueue;->getInstance()Lcom/lumiyaviewer/lumiya/voice/VivoxMessageQueue;

    move-result-object v0

    invoke-direct {v0}, Lcom/lumiyaviewer/lumiya/voice/VivoxMessageQueue;->handleNotification()V

    .line 32
    return-void
.end method


# virtual methods
.method public getMessage()Lcom/vivox/service/vx_message_base_t;
    .locals 5
    .annotation runtime Ljavax/annotation/Nullable;
    .end annotation

    .prologue
    .line 36
    iget-object v3, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxMessageQueue;->messageLock:Ljava/lang/Object;

    monitor-enter v3

    .line 38
    :goto_0
    :try_start_0
    invoke-static {}, Lcom/vivox/service/VxClientProxy;->get_next_message_no_wait()Lcom/vivox/service/vx_message_base_t;

    move-result-object v1

    .line 39
    .local v1, "result":Lcom/vivox/service/vx_message_base_t;
    if-eqz v1, :cond_0

    .line 40
    const-string v2, "Voice: got message from Vivox"

    const/4 v4, 0x0

    new-array v4, v4, [Ljava/lang/Object;

    invoke-static {v2, v4}, Lcom/lumiyaviewer/lumiya/voice/Debug;->Printf(Ljava/lang/String;[Ljava/lang/Object;)V

    .line 51
    monitor-exit v3

    .line 52
    return-object v1

    .line 43
    :cond_0
    const-string v2, "Voice: waiting for Vivox event"

    const/4 v4, 0x0

    new-array v4, v4, [Ljava/lang/Object;

    invoke-static {v2, v4}, Lcom/lumiyaviewer/lumiya/voice/Debug;->Printf(Ljava/lang/String;[Ljava/lang/Object;)V
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    .line 45
    :try_start_1
    iget-object v2, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxMessageQueue;->messageLock:Ljava/lang/Object;

    invoke-virtual {v2}, Ljava/lang/Object;->wait()V
    :try_end_1
    .catch Ljava/lang/InterruptedException; {:try_start_1 .. :try_end_1} :catch_0
    .catchall {:try_start_1 .. :try_end_1} :catchall_0

    .line 49
    :goto_1
    :try_start_2
    const-string v2, "Voice: got Vivox event"

    const/4 v4, 0x0

    new-array v4, v4, [Ljava/lang/Object;

    invoke-static {v2, v4}, Lcom/lumiyaviewer/lumiya/voice/Debug;->Printf(Ljava/lang/String;[Ljava/lang/Object;)V

    goto :goto_0

    .line 51
    .end local v1    # "result":Lcom/vivox/service/vx_message_base_t;
    :catchall_0
    move-exception v2

    monitor-exit v3
    :try_end_2
    .catchall {:try_start_2 .. :try_end_2} :catchall_0

    throw v2

    .line 46
    .restart local v1    # "result":Lcom/vivox/service/vx_message_base_t;
    :catch_0
    move-exception v0

    .line 47
    .local v0, "e":Ljava/lang/InterruptedException;
    const/4 v1, 0x0

    goto :goto_1
.end method
