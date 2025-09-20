.class public Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;
.super Ljava/lang/Object;
.source "VivoxMessageController.java"


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController$PendingRequest;,
        Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController$OnVivoxMessageListener;
    }
.end annotation


# instance fields
.field private final handler:Landroid/os/Handler;

.field private final listenForMessages:Ljava/util/concurrent/atomic/AtomicBoolean;

.field private final listener:Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController$OnVivoxMessageListener;

.field private final pendingRequests:Ljava/util/Map;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/Map",
            "<",
            "Ljava/lang/String;",
            "Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController$PendingRequest;",
            ">;"
        }
    .end annotation
.end field

.field private final receiveMessages:Ljava/lang/Runnable;

.field private final receiverThread:Ljava/lang/Thread;

.field private final requestId:Ljava/util/concurrent/atomic/AtomicInteger;


# direct methods
.method public constructor <init>(Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController$OnVivoxMessageListener;)V
    .locals 3
    .param p1, "listener"    # Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController$OnVivoxMessageListener;

    .prologue
    const/4 v1, 0x1

    .line 70
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 47
    new-instance v0, Ljava/util/concurrent/atomic/AtomicInteger;

    invoke-direct {v0, v1}, Ljava/util/concurrent/atomic/AtomicInteger;-><init>(I)V

    iput-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;->requestId:Ljava/util/concurrent/atomic/AtomicInteger;

    .line 48
    new-instance v0, Ljava/util/concurrent/atomic/AtomicBoolean;

    invoke-direct {v0, v1}, Ljava/util/concurrent/atomic/AtomicBoolean;-><init>(Z)V

    iput-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;->listenForMessages:Ljava/util/concurrent/atomic/AtomicBoolean;

    .line 51
    new-instance v0, Ljava/util/HashMap;

    invoke-direct {v0}, Ljava/util/HashMap;-><init>()V

    invoke-static {v0}, Ljava/util/Collections;->synchronizedMap(Ljava/util/Map;)Ljava/util/Map;

    move-result-object v0

    iput-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;->pendingRequests:Ljava/util/Map;

    .line 53
    new-instance v0, Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController$1;

    invoke-direct {v0, p0}, Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController$1;-><init>(Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;)V

    iput-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;->handler:Landroid/os/Handler;

    .line 76
    new-instance v0, Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController$2;

    invoke-direct {v0, p0}, Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController$2;-><init>(Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;)V

    iput-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;->receiveMessages:Ljava/lang/Runnable;

    .line 71
    iput-object p1, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;->listener:Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController$OnVivoxMessageListener;

    .line 72
    new-instance v0, Ljava/lang/Thread;

    iget-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;->receiveMessages:Ljava/lang/Runnable;

    const-string v2, "VivoxReceiveThread"

    invoke-direct {v0, v1, v2}, Ljava/lang/Thread;-><init>(Ljava/lang/Runnable;Ljava/lang/String;)V

    iput-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;->receiverThread:Ljava/lang/Thread;

    .line 73
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;->receiverThread:Ljava/lang/Thread;

    invoke-virtual {v0}, Ljava/lang/Thread;->start()V

    .line 74
    return-void
.end method

.method static synthetic access$000(Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;)Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController$OnVivoxMessageListener;
    .locals 1
    .param p0, "x0"    # Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;

    .prologue
    .line 14
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;->listener:Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController$OnVivoxMessageListener;

    return-object v0
.end method

.method static synthetic access$100(Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;)Ljava/util/concurrent/atomic/AtomicBoolean;
    .locals 1
    .param p0, "x0"    # Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;

    .prologue
    .line 14
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;->listenForMessages:Ljava/util/concurrent/atomic/AtomicBoolean;

    return-object v0
.end method

.method static synthetic access$200(Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;)Ljava/util/Map;
    .locals 1
    .param p0, "x0"    # Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;

    .prologue
    .line 14
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;->pendingRequests:Ljava/util/Map;

    return-object v0
.end method

.method static synthetic access$300(Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;)Landroid/os/Handler;
    .locals 1
    .param p0, "x0"    # Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;

    .prologue
    .line 14
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;->handler:Landroid/os/Handler;

    return-object v0
.end method

.method private getRequestID()Ljava/lang/String;
    .locals 1

    .prologue
    .line 111
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;->requestId:Ljava/util/concurrent/atomic/AtomicInteger;

    invoke-virtual {v0}, Ljava/util/concurrent/atomic/AtomicInteger;->getAndIncrement()I

    move-result v0

    invoke-static {v0}, Ljava/lang/Integer;->toString(I)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method


# virtual methods
.method public sendRequest(Lcom/vivox/service/vx_req_base_t;)V
    .locals 4
    .param p1, "vxRequest"    # Lcom/vivox/service/vx_req_base_t;

    .prologue
    .line 143
    invoke-direct {p0}, Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;->getRequestID()Ljava/lang/String;

    move-result-object v0

    .line 144
    .local v0, "requestID":Ljava/lang/String;
    const-string v1, "Voice: sending request with cookie \'%s\'"

    const/4 v2, 0x1

    new-array v2, v2, [Ljava/lang/Object;

    const/4 v3, 0x0

    aput-object v0, v2, v3

    invoke-static {v1, v2}, Lcom/lumiyaviewer/lumiya/voice/Debug;->Printf(Ljava/lang/String;[Ljava/lang/Object;)V

    .line 145
    invoke-static {p1, v0}, Lcom/vivox/service/VxClientProxy;->set_request_cookie(Lcom/vivox/service/vx_req_base_t;Ljava/lang/String;)V

    .line 146
    invoke-static {p1}, Lcom/vivox/service/VxClientProxy;->vx_issue_request(Lcom/vivox/service/vx_req_base_t;)I

    .line 147
    return-void
.end method

.method public sendRequestAndWait(Lcom/vivox/service/vx_req_base_t;)Lcom/vivox/service/vx_resp_base_t;
    .locals 9
    .param p1, "vxRequest"    # Lcom/vivox/service/vx_req_base_t;
    .annotation runtime Ljavax/annotation/Nullable;
    .end annotation

    .prologue
    const/4 v7, 0x1

    const/4 v8, 0x0

    const/4 v4, 0x0

    .line 121
    invoke-direct {p0}, Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;->getRequestID()Ljava/lang/String;

    move-result-object v2

    .line 122
    .local v2, "requestID":Ljava/lang/String;
    const-string v5, "Voice: sending request with cookie \'%s\'"

    new-array v6, v7, [Ljava/lang/Object;

    aput-object v2, v6, v8

    invoke-static {v5, v6}, Lcom/lumiyaviewer/lumiya/voice/Debug;->Printf(Ljava/lang/String;[Ljava/lang/Object;)V

    .line 123
    invoke-static {p1, v2}, Lcom/vivox/service/VxClientProxy;->set_request_cookie(Lcom/vivox/service/vx_req_base_t;Ljava/lang/String;)V

    .line 125
    new-instance v1, Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController$PendingRequest;

    invoke-direct {v1, v4}, Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController$PendingRequest;-><init>(Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController$1;)V

    .line 126
    .local v1, "pendingRequest":Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController$PendingRequest;
    iget-object v5, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;->pendingRequests:Ljava/util/Map;

    invoke-interface {v5, v2, v1}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    .line 128
    invoke-static {p1}, Lcom/vivox/service/VxClientProxy;->vx_issue_request(Lcom/vivox/service/vx_req_base_t;)I

    move-result v3

    .line 129
    .local v3, "ret":I
    if-eqz v3, :cond_0

    .line 130
    const-string v5, "Voice: vx_issue_request returned %d"

    new-array v6, v7, [Ljava/lang/Object;

    invoke-static {v3}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v7

    aput-object v7, v6, v8

    invoke-static {v5, v6}, Lcom/lumiyaviewer/lumiya/voice/Debug;->Printf(Ljava/lang/String;[Ljava/lang/Object;)V

    .line 137
    :goto_0
    return-object v4

    .line 135
    :cond_0
    :try_start_0
    invoke-virtual {v1}, Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController$PendingRequest;->waitResult()Lcom/vivox/service/vx_resp_base_t;
    :try_end_0
    .catch Ljava/lang/InterruptedException; {:try_start_0 .. :try_end_0} :catch_0

    move-result-object v4

    goto :goto_0

    .line 136
    :catch_0
    move-exception v0

    .line 137
    .local v0, "e":Ljava/lang/InterruptedException;
    goto :goto_0
.end method

.method public stop()V
    .locals 2

    .prologue
    .line 115
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;->listenForMessages:Ljava/util/concurrent/atomic/AtomicBoolean;

    const/4 v1, 0x0

    invoke-virtual {v0, v1}, Ljava/util/concurrent/atomic/AtomicBoolean;->set(Z)V

    .line 116
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;->receiverThread:Ljava/lang/Thread;

    invoke-virtual {v0}, Ljava/lang/Thread;->interrupt()V

    .line 117
    return-void
.end method
