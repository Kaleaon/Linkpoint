.class Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController$PendingRequest;
.super Ljava/lang/Object;
.source "VivoxMessageController.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0xa
    name = "PendingRequest"
.end annotation


# instance fields
.field private final monitor:Ljava/lang/Object;

.field private result:Lcom/vivox/service/vx_resp_base_t;


# direct methods
.method private constructor <init>()V
    .locals 1

    .prologue
    .line 21
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 23
    new-instance v0, Ljava/lang/Object;

    invoke-direct {v0}, Ljava/lang/Object;-><init>()V

    iput-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController$PendingRequest;->monitor:Ljava/lang/Object;

    .line 24
    const/4 v0, 0x0

    iput-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController$PendingRequest;->result:Lcom/vivox/service/vx_resp_base_t;

    return-void
.end method

.method synthetic constructor <init>(Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController$1;)V
    .locals 0
    .param p1, "x0"    # Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController$1;

    .prologue
    .line 21
    invoke-direct {p0}, Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController$PendingRequest;-><init>()V

    return-void
.end method


# virtual methods
.method public signalRequestCompleted(Lcom/vivox/service/vx_resp_base_t;)V
    .locals 2
    .param p1, "response"    # Lcom/vivox/service/vx_resp_base_t;

    .prologue
    .line 27
    iget-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController$PendingRequest;->monitor:Ljava/lang/Object;

    monitor-enter v1

    .line 28
    :try_start_0
    iput-object p1, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController$PendingRequest;->result:Lcom/vivox/service/vx_resp_base_t;

    .line 29
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController$PendingRequest;->monitor:Ljava/lang/Object;

    invoke-virtual {v0}, Ljava/lang/Object;->notifyAll()V

    .line 30
    monitor-exit v1

    .line 31
    return-void

    .line 30
    :catchall_0
    move-exception v0

    monitor-exit v1
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    throw v0
.end method

.method public waitResult()Lcom/vivox/service/vx_resp_base_t;
    .locals 3
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/lang/InterruptedException;
        }
    .end annotation

    .prologue
    .line 36
    iget-object v2, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController$PendingRequest;->monitor:Ljava/lang/Object;

    monitor-enter v2

    .line 37
    :goto_0
    :try_start_0
    iget-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController$PendingRequest;->result:Lcom/vivox/service/vx_resp_base_t;

    if-nez v1, :cond_0

    .line 38
    iget-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController$PendingRequest;->monitor:Ljava/lang/Object;

    invoke-virtual {v1}, Ljava/lang/Object;->wait()V

    goto :goto_0

    .line 40
    :catchall_0
    move-exception v1

    monitor-exit v2
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    throw v1

    .line 39
    :cond_0
    :try_start_1
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController$PendingRequest;->result:Lcom/vivox/service/vx_resp_base_t;

    .line 40
    .local v0, "rc":Lcom/vivox/service/vx_resp_base_t;
    monitor-exit v2
    :try_end_1
    .catchall {:try_start_1 .. :try_end_1} :catchall_0

    .line 42
    return-object v0
.end method
