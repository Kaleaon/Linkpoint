.class Lcom/lumiyaviewer/lumiya/voice/VivoxController$ControllerThread;
.super Ljava/lang/Object;
.source "VivoxController.java"

# interfaces
.implements Ljava/lang/Runnable;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/lumiyaviewer/lumiya/voice/VivoxController;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x2
    name = "ControllerThread"
.end annotation


# instance fields
.field private handler:Landroid/os/Handler;

.field private messageController:Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;

.field final synthetic this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxController;


# direct methods
.method private constructor <init>(Lcom/lumiyaviewer/lumiya/voice/VivoxController;)V
    .locals 0

    .prologue
    .line 110
    iput-object p1, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$ControllerThread;->this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method

.method synthetic constructor <init>(Lcom/lumiyaviewer/lumiya/voice/VivoxController;Lcom/lumiyaviewer/lumiya/voice/VivoxController$1;)V
    .locals 0
    .param p1, "x0"    # Lcom/lumiyaviewer/lumiya/voice/VivoxController;
    .param p2, "x1"    # Lcom/lumiyaviewer/lumiya/voice/VivoxController$1;

    .prologue
    .line 110
    invoke-direct {p0, p1}, Lcom/lumiyaviewer/lumiya/voice/VivoxController$ControllerThread;-><init>(Lcom/lumiyaviewer/lumiya/voice/VivoxController;)V

    return-void
.end method

.method static synthetic access$100(Lcom/lumiyaviewer/lumiya/voice/VivoxController$ControllerThread;)Landroid/os/Handler;
    .locals 1
    .param p0, "x0"    # Lcom/lumiyaviewer/lumiya/voice/VivoxController$ControllerThread;

    .prologue
    .line 110
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$ControllerThread;->handler:Landroid/os/Handler;

    return-object v0
.end method

.method static synthetic access$200(Lcom/lumiyaviewer/lumiya/voice/VivoxController$ControllerThread;)Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;
    .locals 1
    .param p0, "x0"    # Lcom/lumiyaviewer/lumiya/voice/VivoxController$ControllerThread;

    .prologue
    .line 110
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$ControllerThread;->messageController:Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;

    return-object v0
.end method


# virtual methods
.method public run()V
    .locals 3

    .prologue
    .line 117
    invoke-static {}, Landroid/os/Looper;->prepare()V

    .line 118
    new-instance v0, Landroid/os/Handler;

    invoke-direct {v0}, Landroid/os/Handler;-><init>()V

    iput-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$ControllerThread;->handler:Landroid/os/Handler;

    .line 119
    new-instance v0, Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;

    iget-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$ControllerThread;->this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    invoke-direct {v0, v1}, Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;-><init>(Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController$OnVivoxMessageListener;)V

    iput-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$ControllerThread;->messageController:Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;

    .line 120
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$ControllerThread;->this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    invoke-static {v0}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->access$300(Lcom/lumiyaviewer/lumiya/voice/VivoxController;)Ljava/lang/Object;

    move-result-object v1

    monitor-enter v1

    .line 121
    :try_start_0
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$ControllerThread;->this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    const/4 v2, 0x1

    invoke-static {v0, v2}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->access$402(Lcom/lumiyaviewer/lumiya/voice/VivoxController;Z)Z

    .line 122
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$ControllerThread;->this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    invoke-static {v0}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->access$300(Lcom/lumiyaviewer/lumiya/voice/VivoxController;)Ljava/lang/Object;

    move-result-object v0

    invoke-virtual {v0}, Ljava/lang/Object;->notifyAll()V

    .line 123
    monitor-exit v1
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    .line 124
    invoke-static {}, Landroid/os/Looper;->loop()V

    .line 125
    return-void

    .line 123
    :catchall_0
    move-exception v0

    :try_start_1
    monitor-exit v1
    :try_end_1
    .catchall {:try_start_1 .. :try_end_1} :catchall_0

    throw v0
.end method
