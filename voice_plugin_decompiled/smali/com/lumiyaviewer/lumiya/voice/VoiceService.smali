.class public Lcom/lumiyaviewer/lumiya/voice/VoiceService;
.super Landroid/app/Service;
.source "VoiceService.java"


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Lcom/lumiyaviewer/lumiya/voice/VoiceService$IncomingHandler;
    }
.end annotation


# static fields
.field public static final MESSAGE_PERMISSION_RESULTS:I = 0x12c

.field private static final REQUIRED_APP_VERSION:I = 0x3c

.field public static final STREAM_TYPE_BLUETOOTH:I = 0x6

.field private static serviceInstance:Lcom/lumiyaviewer/lumiya/voice/VoiceService;


# instance fields
.field private audioManager:Landroid/media/AudioManager;

.field private audioStreamVolumeObserver:Lcom/lumiyaviewer/lumiya/voice/AudioStreamVolumeObserver;

.field private final audioVolumeChangeListener:Lcom/lumiyaviewer/lumiya/voice/AudioStreamVolumeObserver$OnAudioStreamVolumeChangedListener;

.field private bluetoothReceiverRegistered:Z

.field private final bluetoothScoIntentReceiver:Landroid/content/BroadcastReceiver;

.field private final bluetoothScoState:Ljava/util/concurrent/atomic/AtomicInteger;

.field private isServiceBound:Z

.field private final mMessenger:Landroid/os/Messenger;

.field private final mainThreadHandler:Landroid/os/Handler;

.field private toAppMessenger:Landroid/os/Messenger;

.field private vivoxController:Lcom/lumiyaviewer/lumiya/voice/VivoxController;


# direct methods
.method static constructor <clinit>()V
    .locals 1

    .prologue
    .line 30
    const/4 v0, 0x0

    sput-object v0, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->serviceInstance:Lcom/lumiyaviewer/lumiya/voice/VoiceService;

    return-void
.end method

.method public constructor <init>()V
    .locals 3

    .prologue
    const/4 v1, 0x0

    const/4 v2, 0x0

    .line 23
    invoke-direct {p0}, Landroid/app/Service;-><init>()V

    .line 32
    new-instance v0, Landroid/os/Handler;

    invoke-direct {v0}, Landroid/os/Handler;-><init>()V

    iput-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->mainThreadHandler:Landroid/os/Handler;

    .line 33
    iput-object v2, p0, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->vivoxController:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    .line 34
    iput-boolean v1, p0, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->isServiceBound:Z

    .line 35
    iput-object v2, p0, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->toAppMessenger:Landroid/os/Messenger;

    .line 36
    iput-boolean v1, p0, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->bluetoothReceiverRegistered:Z

    .line 37
    new-instance v0, Ljava/util/concurrent/atomic/AtomicInteger;

    const/4 v1, -0x1

    invoke-direct {v0, v1}, Ljava/util/concurrent/atomic/AtomicInteger;-><init>(I)V

    iput-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->bluetoothScoState:Ljava/util/concurrent/atomic/AtomicInteger;

    .line 38
    iput-object v2, p0, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->audioManager:Landroid/media/AudioManager;

    .line 39
    iput-object v2, p0, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->audioStreamVolumeObserver:Lcom/lumiyaviewer/lumiya/voice/AudioStreamVolumeObserver;

    .line 102
    new-instance v0, Landroid/os/Messenger;

    new-instance v1, Lcom/lumiyaviewer/lumiya/voice/VoiceService$IncomingHandler;

    invoke-direct {v1, p0, v2}, Lcom/lumiyaviewer/lumiya/voice/VoiceService$IncomingHandler;-><init>(Lcom/lumiyaviewer/lumiya/voice/VoiceService;Lcom/lumiyaviewer/lumiya/voice/VoiceService$1;)V

    invoke-direct {v0, v1}, Landroid/os/Messenger;-><init>(Landroid/os/Handler;)V

    iput-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->mMessenger:Landroid/os/Messenger;

    .line 150
    new-instance v0, Lcom/lumiyaviewer/lumiya/voice/VoiceService$1;

    invoke-direct {v0, p0}, Lcom/lumiyaviewer/lumiya/voice/VoiceService$1;-><init>(Lcom/lumiyaviewer/lumiya/voice/VoiceService;)V

    iput-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->bluetoothScoIntentReceiver:Landroid/content/BroadcastReceiver;

    .line 333
    new-instance v0, Lcom/lumiyaviewer/lumiya/voice/VoiceService$2;

    invoke-direct {v0, p0}, Lcom/lumiyaviewer/lumiya/voice/VoiceService$2;-><init>(Lcom/lumiyaviewer/lumiya/voice/VoiceService;)V

    iput-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->audioVolumeChangeListener:Lcom/lumiyaviewer/lumiya/voice/AudioStreamVolumeObserver$OnAudioStreamVolumeChangedListener;

    return-void
.end method

.method static synthetic access$000(Lcom/lumiyaviewer/lumiya/voice/VoiceService;Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceInitialize;Landroid/os/Messenger;)V
    .locals 0
    .param p0, "x0"    # Lcom/lumiyaviewer/lumiya/voice/VoiceService;
    .param p1, "x1"    # Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceInitialize;
    .param p2, "x2"    # Landroid/os/Messenger;

    .prologue
    .line 23
    invoke-direct {p0, p1, p2}, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->onVoiceInitialize(Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceInitialize;Landroid/os/Messenger;)V

    return-void
.end method

.method static synthetic access$100(Lcom/lumiyaviewer/lumiya/voice/VoiceService;Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceLogin;Landroid/os/Messenger;)V
    .locals 0
    .param p0, "x0"    # Lcom/lumiyaviewer/lumiya/voice/VoiceService;
    .param p1, "x1"    # Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceLogin;
    .param p2, "x2"    # Landroid/os/Messenger;

    .prologue
    .line 23
    invoke-direct {p0, p1, p2}, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->onVoiceLogin(Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceLogin;Landroid/os/Messenger;)V

    return-void
.end method

.method static synthetic access$1000(Lcom/lumiyaviewer/lumiya/voice/VoiceService;Landroid/os/Messenger;Z)V
    .locals 0
    .param p0, "x0"    # Lcom/lumiyaviewer/lumiya/voice/VoiceService;
    .param p1, "x1"    # Landroid/os/Messenger;
    .param p2, "x2"    # Z

    .prologue
    .line 23
    invoke-direct {p0, p1, p2}, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->onVoicePermissionResults(Landroid/os/Messenger;Z)V

    return-void
.end method

.method static synthetic access$1200(Lcom/lumiyaviewer/lumiya/voice/VoiceService;Landroid/content/Intent;)V
    .locals 0
    .param p0, "x0"    # Lcom/lumiyaviewer/lumiya/voice/VoiceService;
    .param p1, "x1"    # Landroid/content/Intent;

    .prologue
    .line 23
    invoke-direct {p0, p1}, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->handleBluetoothStateIntent(Landroid/content/Intent;)V

    return-void
.end method

.method static synthetic access$200(Lcom/lumiyaviewer/lumiya/voice/VoiceService;Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceConnectChannel;Landroid/os/Messenger;)V
    .locals 0
    .param p0, "x0"    # Lcom/lumiyaviewer/lumiya/voice/VoiceService;
    .param p1, "x1"    # Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceConnectChannel;
    .param p2, "x2"    # Landroid/os/Messenger;

    .prologue
    .line 23
    invoke-direct {p0, p1, p2}, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->onVoiceConnectChannel(Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceConnectChannel;Landroid/os/Messenger;)V

    return-void
.end method

.method static synthetic access$300(Lcom/lumiyaviewer/lumiya/voice/VoiceService;Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceSet3DPosition;Landroid/os/Messenger;)V
    .locals 0
    .param p0, "x0"    # Lcom/lumiyaviewer/lumiya/voice/VoiceService;
    .param p1, "x1"    # Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceSet3DPosition;
    .param p2, "x2"    # Landroid/os/Messenger;

    .prologue
    .line 23
    invoke-direct {p0, p1, p2}, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->onVoiceSet3DPosition(Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceSet3DPosition;Landroid/os/Messenger;)V

    return-void
.end method

.method static synthetic access$400(Lcom/lumiyaviewer/lumiya/voice/VoiceService;Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceRejectCall;Landroid/os/Messenger;)V
    .locals 0
    .param p0, "x0"    # Lcom/lumiyaviewer/lumiya/voice/VoiceService;
    .param p1, "x1"    # Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceRejectCall;
    .param p2, "x2"    # Landroid/os/Messenger;

    .prologue
    .line 23
    invoke-direct {p0, p1, p2}, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->onVoiceRejectCall(Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceRejectCall;Landroid/os/Messenger;)V

    return-void
.end method

.method static synthetic access$500(Lcom/lumiyaviewer/lumiya/voice/VoiceService;Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceAcceptCall;Landroid/os/Messenger;)V
    .locals 0
    .param p0, "x0"    # Lcom/lumiyaviewer/lumiya/voice/VoiceService;
    .param p1, "x1"    # Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceAcceptCall;
    .param p2, "x2"    # Landroid/os/Messenger;

    .prologue
    .line 23
    invoke-direct {p0, p1, p2}, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->onVoiceAcceptCall(Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceAcceptCall;Landroid/os/Messenger;)V

    return-void
.end method

.method static synthetic access$600(Lcom/lumiyaviewer/lumiya/voice/VoiceService;Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceTerminateCall;Landroid/os/Messenger;)V
    .locals 0
    .param p0, "x0"    # Lcom/lumiyaviewer/lumiya/voice/VoiceService;
    .param p1, "x1"    # Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceTerminateCall;
    .param p2, "x2"    # Landroid/os/Messenger;

    .prologue
    .line 23
    invoke-direct {p0, p1, p2}, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->onVoiceTerminateCall(Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceTerminateCall;Landroid/os/Messenger;)V

    return-void
.end method

.method static synthetic access$700(Lcom/lumiyaviewer/lumiya/voice/VoiceService;Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceEnableMic;)V
    .locals 0
    .param p0, "x0"    # Lcom/lumiyaviewer/lumiya/voice/VoiceService;
    .param p1, "x1"    # Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceEnableMic;

    .prologue
    .line 23
    invoke-direct {p0, p1}, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->onVoiceEnableMic(Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceEnableMic;)V

    return-void
.end method

.method static synthetic access$800(Lcom/lumiyaviewer/lumiya/voice/VoiceService;Landroid/os/Messenger;)V
    .locals 0
    .param p0, "x0"    # Lcom/lumiyaviewer/lumiya/voice/VoiceService;
    .param p1, "x1"    # Landroid/os/Messenger;

    .prologue
    .line 23
    invoke-direct {p0, p1}, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->onVoiceLogout(Landroid/os/Messenger;)V

    return-void
.end method

.method static synthetic access$900(Lcom/lumiyaviewer/lumiya/voice/VoiceService;Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceSetAudioProperties;)V
    .locals 0
    .param p0, "x0"    # Lcom/lumiyaviewer/lumiya/voice/VoiceService;
    .param p1, "x1"    # Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceSetAudioProperties;

    .prologue
    .line 23
    invoke-direct {p0, p1}, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->onVoiceSetAudioProperties(Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceSetAudioProperties;)V

    return-void
.end method

.method static getServiceInstance()Lcom/lumiyaviewer/lumiya/voice/VoiceService;
    .locals 1
    .annotation runtime Ljavax/annotation/Nullable;
    .end annotation

    .prologue
    .line 42
    sget-object v0, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->serviceInstance:Lcom/lumiyaviewer/lumiya/voice/VoiceService;

    return-object v0
.end method

.method private handleBluetoothStateIntent(Landroid/content/Intent;)V
    .locals 3
    .param p1, "intent"    # Landroid/content/Intent;

    .prologue
    .line 158
    const-string v1, "android.media.extra.SCO_AUDIO_STATE"

    const/4 v2, 0x0

    invoke-virtual {p1, v1, v2}, Landroid/content/Intent;->getIntExtra(Ljava/lang/String;I)I

    move-result v0

    .line 159
    .local v0, "state":I
    iget-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->bluetoothScoState:Ljava/util/concurrent/atomic/AtomicInteger;

    invoke-virtual {v1, v0}, Ljava/util/concurrent/atomic/AtomicInteger;->set(I)V

    .line 160
    iget-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->vivoxController:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    if-eqz v1, :cond_0

    .line 161
    iget-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->vivoxController:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    invoke-virtual {v1, v0}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->onBluetoothScoStateChanged(I)V

    .line 163
    :cond_0
    invoke-virtual {p0}, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->updateAudioProperties()V

    .line 164
    return-void
.end method

.method private onVoiceAcceptCall(Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceAcceptCall;Landroid/os/Messenger;)V
    .locals 1
    .param p1, "message"    # Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceAcceptCall;
    .param p2, "replyTo"    # Landroid/os/Messenger;

    .prologue
    .line 299
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->vivoxController:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    if-eqz v0, :cond_0

    .line 300
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->vivoxController:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    invoke-virtual {v0, p1}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->AcceptCall(Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceAcceptCall;)V

    .line 301
    :cond_0
    return-void
.end method

.method private onVoiceConnectChannel(Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceConnectChannel;Landroid/os/Messenger;)V
    .locals 3
    .param p1, "message"    # Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceConnectChannel;
    .param p2, "replyTo"    # Landroid/os/Messenger;

    .prologue
    .line 284
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->vivoxController:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    if-eqz v0, :cond_0

    .line 285
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->vivoxController:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    iget-object v1, p1, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceConnectChannel;->voiceChannelInfo:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;

    iget-object v2, p1, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceConnectChannel;->channelCredentials:Ljava/lang/String;

    invoke-virtual {v0, v1, v2, p2}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->ConnectChannel(Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceChannelInfo;Ljava/lang/String;Landroid/os/Messenger;)V

    .line 286
    :cond_0
    return-void
.end method

.method private onVoiceEnableMic(Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceEnableMic;)V
    .locals 1
    .param p1, "message"    # Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceEnableMic;

    .prologue
    .line 309
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->vivoxController:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    if-eqz v0, :cond_0

    .line 310
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->vivoxController:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    invoke-virtual {v0, p1}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->EnableVoiceMic(Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceEnableMic;)V

    .line 311
    :cond_0
    return-void
.end method

.method private onVoiceInitialize(Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceInitialize;Landroid/os/Messenger;)V
    .locals 9
    .param p1, "message"    # Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceInitialize;
    .param p2, "replyTo"    # Landroid/os/Messenger;

    .prologue
    .line 223
    :try_start_0
    invoke-virtual {p0}, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->getPackageManager()Landroid/content/pm/PackageManager;

    move-result-object v4

    invoke-virtual {p0}, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->getPackageName()Ljava/lang/String;

    move-result-object v5

    const/4 v6, 0x0

    invoke-virtual {v4, v5, v6}, Landroid/content/pm/PackageManager;->getPackageInfo(Ljava/lang/String;I)Landroid/content/pm/PackageInfo;

    move-result-object v2

    .line 225
    .local v2, "pInfo":Landroid/content/pm/PackageInfo;
    iget v4, p1, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceInitialize;->appVersionCode:I

    const/16 v5, 0x3c

    if-ge v4, v5, :cond_0

    .line 226
    sget-object v4, Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;->VoiceInitializeReply:Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;

    new-instance v5, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceInitializeReply;

    iget v6, v2, Landroid/content/pm/PackageInfo;->versionCode:I

    const v7, 0x7f060021

    invoke-virtual {p0, v7}, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->getString(I)Ljava/lang/String;

    move-result-object v7

    const/4 v8, 0x0

    invoke-direct {v5, v6, v7, v8}, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceInitializeReply;-><init>(ILjava/lang/String;Z)V

    const/4 v6, 0x0

    invoke-static {p2, v4, v5, v6}, Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessenger;->sendMessage(Landroid/os/Messenger;Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessage;Landroid/os/Messenger;)Z

    .line 245
    .end local v2    # "pInfo":Landroid/content/pm/PackageInfo;
    :goto_0
    return-void

    .line 229
    .restart local v2    # "pInfo":Landroid/content/pm/PackageInfo;
    :cond_0
    const-string v4, "android.permission.RECORD_AUDIO"

    invoke-static {p0, v4}, Landroid/support/v4/content/ContextCompat;->checkSelfPermission(Landroid/content/Context;Ljava/lang/String;)I

    move-result v3

    .line 230
    .local v3, "permissionCheck":I
    if-eqz v3, :cond_1

    .line 232
    new-instance v1, Landroid/content/Intent;

    const-class v4, Lcom/lumiyaviewer/lumiya/voice/VoicePermissionRequestActivity;

    invoke-direct {v1, p0, v4}, Landroid/content/Intent;-><init>(Landroid/content/Context;Ljava/lang/Class;)V

    .line 233
    .local v1, "intent":Landroid/content/Intent;
    const/high16 v4, 0x50000000

    invoke-virtual {v1, v4}, Landroid/content/Intent;->addFlags(I)Landroid/content/Intent;

    .line 234
    const-string v4, "voiceInitMessage"

    invoke-virtual {p1}, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceInitialize;->toBundle()Landroid/os/Bundle;

    move-result-object v5

    invoke-virtual {v1, v4, v5}, Landroid/content/Intent;->putExtra(Ljava/lang/String;Landroid/os/Bundle;)Landroid/content/Intent;

    .line 235
    const-string v4, "voiceInitReplyTo"

    invoke-virtual {v1, v4, p2}, Landroid/content/Intent;->putExtra(Ljava/lang/String;Landroid/os/Parcelable;)Landroid/content/Intent;

    .line 236
    invoke-virtual {p0, v1}, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->startActivity(Landroid/content/Intent;)V
    :try_end_0
    .catch Landroid/content/pm/PackageManager$NameNotFoundException; {:try_start_0 .. :try_end_0} :catch_0

    goto :goto_0

    .line 242
    .end local v1    # "intent":Landroid/content/Intent;
    .end local v2    # "pInfo":Landroid/content/pm/PackageInfo;
    .end local v3    # "permissionCheck":I
    :catch_0
    move-exception v0

    .line 243
    .local v0, "e":Landroid/content/pm/PackageManager$NameNotFoundException;
    invoke-static {v0}, Lcom/lumiyaviewer/lumiya/voice/Debug;->Warning(Ljava/lang/Throwable;)V

    goto :goto_0

    .line 239
    .end local v0    # "e":Landroid/content/pm/PackageManager$NameNotFoundException;
    .restart local v2    # "pInfo":Landroid/content/pm/PackageInfo;
    .restart local v3    # "permissionCheck":I
    :cond_1
    const/4 v4, 0x1

    :try_start_1
    invoke-direct {p0, p2, v4}, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->onVoicePermissionResults(Landroid/os/Messenger;Z)V
    :try_end_1
    .catch Landroid/content/pm/PackageManager$NameNotFoundException; {:try_start_1 .. :try_end_1} :catch_0

    goto :goto_0
.end method

.method private onVoiceLogin(Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceLogin;Landroid/os/Messenger;)V
    .locals 2
    .param p1, "message"    # Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceLogin;
    .param p2, "replyTo"    # Landroid/os/Messenger;

    .prologue
    .line 279
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->vivoxController:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    if-eqz v0, :cond_0

    .line 280
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->vivoxController:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    iget-object v1, p1, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceLogin;->voiceLoginInfo:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceLoginInfo;

    invoke-virtual {v0, v1, p2}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->Login(Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceLoginInfo;Landroid/os/Messenger;)V

    .line 281
    :cond_0
    return-void
.end method

.method private onVoiceLogout(Landroid/os/Messenger;)V
    .locals 1
    .param p1, "replyTo"    # Landroid/os/Messenger;

    .prologue
    .line 314
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->vivoxController:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    if-eqz v0, :cond_0

    .line 315
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->vivoxController:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    invoke-virtual {v0, p1}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->Logout(Landroid/os/Messenger;)V

    .line 316
    :cond_0
    return-void
.end method

.method private onVoicePermissionResults(Landroid/os/Messenger;Z)V
    .locals 7
    .param p1, "replyTo"    # Landroid/os/Messenger;
    .param p2, "granted"    # Z

    .prologue
    .line 249
    :try_start_0
    invoke-virtual {p0}, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->getPackageManager()Landroid/content/pm/PackageManager;

    move-result-object v3

    invoke-virtual {p0}, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->getPackageName()Ljava/lang/String;

    move-result-object v4

    const/4 v5, 0x0

    invoke-virtual {v3, v4, v5}, Landroid/content/pm/PackageManager;->getPackageInfo(Ljava/lang/String;I)Landroid/content/pm/PackageInfo;

    move-result-object v2

    .line 250
    .local v2, "pInfo":Landroid/content/pm/PackageInfo;
    const/4 v1, 0x0

    .line 252
    .local v1, "errorMessage":Ljava/lang/String;
    if-eqz p2, :cond_2

    .line 253
    iget-object v3, p0, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->vivoxController:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    if-nez v3, :cond_0

    .line 254
    const-string v3, "Voice: Creating new Vivox controller"

    const/4 v4, 0x0

    new-array v4, v4, [Ljava/lang/Object;

    invoke-static {v3, v4}, Lcom/lumiyaviewer/lumiya/voice/Debug;->Printf(Ljava/lang/String;[Ljava/lang/Object;)V
    :try_end_0
    .catch Landroid/content/pm/PackageManager$NameNotFoundException; {:try_start_0 .. :try_end_0} :catch_1

    .line 256
    :try_start_1
    iget-object v3, p0, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->mainThreadHandler:Landroid/os/Handler;

    iget-object v4, p0, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->mMessenger:Landroid/os/Messenger;

    invoke-static {p0, v3, v4}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->getInstance(Landroid/content/Context;Landroid/os/Handler;Landroid/os/Messenger;)Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    move-result-object v3

    iput-object v3, p0, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->vivoxController:Lcom/lumiyaviewer/lumiya/voice/VivoxController;
    :try_end_1
    .catch Lcom/lumiyaviewer/lumiya/voice/VivoxController$VivoxInitException; {:try_start_1 .. :try_end_1} :catch_0
    .catch Landroid/content/pm/PackageManager$NameNotFoundException; {:try_start_1 .. :try_end_1} :catch_1

    .line 265
    :cond_0
    :goto_0
    :try_start_2
    sget-object v3, Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;->VoiceInitializeReply:Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;

    new-instance v4, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceInitializeReply;

    iget v5, v2, Landroid/content/pm/PackageInfo;->versionCode:I

    const/4 v6, 0x1

    invoke-direct {v4, v5, v1, v6}, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceInitializeReply;-><init>(ILjava/lang/String;Z)V

    const/4 v5, 0x0

    invoke-static {p1, v3, v4, v5}, Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessenger;->sendMessage(Landroid/os/Messenger;Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessage;Landroid/os/Messenger;)Z

    .line 267
    if-nez v1, :cond_1

    .line 268
    iput-object p1, p0, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->toAppMessenger:Landroid/os/Messenger;

    .line 269
    invoke-direct {p0}, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->registerForBluetoothScoIntentBroadcast()V

    .line 270
    invoke-virtual {p0}, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->updateAudioProperties()V

    .line 276
    .end local v1    # "errorMessage":Ljava/lang/String;
    .end local v2    # "pInfo":Landroid/content/pm/PackageInfo;
    :cond_1
    :goto_1
    return-void

    .line 257
    .restart local v1    # "errorMessage":Ljava/lang/String;
    .restart local v2    # "pInfo":Landroid/content/pm/PackageInfo;
    :catch_0
    move-exception v0

    .line 258
    .local v0, "e":Lcom/lumiyaviewer/lumiya/voice/VivoxController$VivoxInitException;
    invoke-static {v0}, Lcom/lumiyaviewer/lumiya/voice/Debug;->Warning(Ljava/lang/Throwable;)V

    .line 259
    invoke-virtual {v0}, Lcom/lumiyaviewer/lumiya/voice/VivoxController$VivoxInitException;->getMessage()Ljava/lang/String;

    move-result-object v1

    .line 260
    goto :goto_0

    .line 263
    .end local v0    # "e":Lcom/lumiyaviewer/lumiya/voice/VivoxController$VivoxInitException;
    :cond_2
    const v3, 0x7f060023

    invoke-virtual {p0, v3}, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->getString(I)Ljava/lang/String;
    :try_end_2
    .catch Landroid/content/pm/PackageManager$NameNotFoundException; {:try_start_2 .. :try_end_2} :catch_1

    move-result-object v1

    goto :goto_0

    .line 273
    .end local v1    # "errorMessage":Ljava/lang/String;
    .end local v2    # "pInfo":Landroid/content/pm/PackageInfo;
    :catch_1
    move-exception v0

    .line 274
    .local v0, "e":Landroid/content/pm/PackageManager$NameNotFoundException;
    invoke-static {v0}, Lcom/lumiyaviewer/lumiya/voice/Debug;->Warning(Ljava/lang/Throwable;)V

    goto :goto_1
.end method

.method private onVoiceRejectCall(Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceRejectCall;Landroid/os/Messenger;)V
    .locals 1
    .param p1, "message"    # Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceRejectCall;
    .param p2, "replyTo"    # Landroid/os/Messenger;

    .prologue
    .line 294
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->vivoxController:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    if-eqz v0, :cond_0

    .line 295
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->vivoxController:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    invoke-virtual {v0, p1}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->RejectCall(Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceRejectCall;)V

    .line 296
    :cond_0
    return-void
.end method

.method private onVoiceSet3DPosition(Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceSet3DPosition;Landroid/os/Messenger;)V
    .locals 1
    .param p1, "message"    # Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceSet3DPosition;
    .param p2, "replyTo"    # Landroid/os/Messenger;

    .prologue
    .line 289
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->vivoxController:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    if-eqz v0, :cond_0

    .line 290
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->vivoxController:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    invoke-virtual {v0, p1}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->Set3DPosition(Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceSet3DPosition;)V

    .line 291
    :cond_0
    return-void
.end method

.method private onVoiceSetAudioProperties(Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceSetAudioProperties;)V
    .locals 1
    .param p1, "message"    # Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceSetAudioProperties;

    .prologue
    .line 319
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->vivoxController:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    if-eqz v0, :cond_0

    .line 320
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->vivoxController:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    invoke-virtual {v0, p1}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->SetAudioProperties(Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceSetAudioProperties;)V

    .line 321
    :cond_0
    return-void
.end method

.method private onVoiceTerminateCall(Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceTerminateCall;Landroid/os/Messenger;)V
    .locals 1
    .param p1, "message"    # Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceTerminateCall;
    .param p2, "replyTo"    # Landroid/os/Messenger;

    .prologue
    .line 304
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->vivoxController:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    if-eqz v0, :cond_0

    .line 305
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->vivoxController:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    invoke-virtual {v0, p1}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->TerminateCall(Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceTerminateCall;)V

    .line 306
    :cond_0
    return-void
.end method

.method private registerForBluetoothScoIntentBroadcast()V
    .locals 5

    .prologue
    .line 206
    sget v3, Landroid/os/Build$VERSION;->SDK_INT:I

    const/16 v4, 0xe

    if-lt v3, v4, :cond_0

    iget-boolean v3, p0, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->bluetoothReceiverRegistered:Z

    if-nez v3, :cond_0

    .line 208
    const/4 v3, 0x1

    :try_start_0
    iput-boolean v3, p0, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->bluetoothReceiverRegistered:Z

    .line 209
    new-instance v1, Landroid/content/IntentFilter;

    const-string v3, "android.media.ACTION_SCO_AUDIO_STATE_UPDATED"

    invoke-direct {v1, v3}, Landroid/content/IntentFilter;-><init>(Ljava/lang/String;)V

    .line 210
    .local v1, "filter":Landroid/content/IntentFilter;
    iget-object v3, p0, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->bluetoothScoIntentReceiver:Landroid/content/BroadcastReceiver;

    invoke-virtual {p0, v3, v1}, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->registerReceiver(Landroid/content/BroadcastReceiver;Landroid/content/IntentFilter;)Landroid/content/Intent;

    move-result-object v2

    .line 211
    .local v2, "initialIntent":Landroid/content/Intent;
    if-eqz v2, :cond_0

    .line 212
    invoke-direct {p0, v2}, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->handleBluetoothStateIntent(Landroid/content/Intent;)V
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    .line 218
    .end local v1    # "filter":Landroid/content/IntentFilter;
    .end local v2    # "initialIntent":Landroid/content/Intent;
    :cond_0
    :goto_0
    return-void

    .line 214
    :catch_0
    move-exception v0

    .line 215
    .local v0, "e":Ljava/lang/Exception;
    invoke-static {v0}, Lcom/lumiyaviewer/lumiya/voice/Debug;->Warning(Ljava/lang/Throwable;)V

    goto :goto_0
.end method


# virtual methods
.method listenForVolumeChanges(Z)V
    .locals 3
    .param p1, "enable"    # Z

    .prologue
    .line 324
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->audioStreamVolumeObserver:Lcom/lumiyaviewer/lumiya/voice/AudioStreamVolumeObserver;

    if-eqz v0, :cond_0

    .line 325
    if-eqz p1, :cond_1

    .line 326
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->audioStreamVolumeObserver:Lcom/lumiyaviewer/lumiya/voice/AudioStreamVolumeObserver;

    const/4 v1, 0x2

    new-array v1, v1, [I

    fill-array-data v1, :array_0

    iget-object v2, p0, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->audioVolumeChangeListener:Lcom/lumiyaviewer/lumiya/voice/AudioStreamVolumeObserver$OnAudioStreamVolumeChangedListener;

    invoke-virtual {v0, v1, v2}, Lcom/lumiyaviewer/lumiya/voice/AudioStreamVolumeObserver;->start([ILcom/lumiyaviewer/lumiya/voice/AudioStreamVolumeObserver$OnAudioStreamVolumeChangedListener;)V

    .line 327
    invoke-virtual {p0}, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->updateAudioProperties()V

    .line 331
    :cond_0
    :goto_0
    return-void

    .line 329
    :cond_1
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->audioStreamVolumeObserver:Lcom/lumiyaviewer/lumiya/voice/AudioStreamVolumeObserver;

    invoke-virtual {v0}, Lcom/lumiyaviewer/lumiya/voice/AudioStreamVolumeObserver;->stop()V

    goto :goto_0

    .line 326
    nop

    :array_0
    .array-data 4
        0x0
        0x6
    .end array-data
.end method

.method public onBind(Landroid/content/Intent;)Landroid/os/IBinder;
    .locals 1
    .param p1, "intent"    # Landroid/content/Intent;

    .prologue
    .line 114
    const/4 v0, 0x1

    iput-boolean v0, p0, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->isServiceBound:Z

    .line 115
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->mMessenger:Landroid/os/Messenger;

    invoke-virtual {v0}, Landroid/os/Messenger;->getBinder()Landroid/os/IBinder;

    move-result-object v0

    return-object v0
.end method

.method public onCreate()V
    .locals 1

    .prologue
    .line 106
    invoke-super {p0}, Landroid/app/Service;->onCreate()V

    .line 107
    sput-object p0, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->serviceInstance:Lcom/lumiyaviewer/lumiya/voice/VoiceService;

    .line 108
    const-string v0, "audio"

    invoke-virtual {p0, v0}, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->getSystemService(Ljava/lang/String;)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Landroid/media/AudioManager;

    iput-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->audioManager:Landroid/media/AudioManager;

    .line 109
    new-instance v0, Lcom/lumiyaviewer/lumiya/voice/AudioStreamVolumeObserver;

    invoke-direct {v0, p0}, Lcom/lumiyaviewer/lumiya/voice/AudioStreamVolumeObserver;-><init>(Landroid/content/Context;)V

    iput-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->audioStreamVolumeObserver:Lcom/lumiyaviewer/lumiya/voice/AudioStreamVolumeObserver;

    .line 110
    return-void
.end method

.method public onDestroy()V
    .locals 2

    .prologue
    const/4 v1, 0x0

    .line 129
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->vivoxController:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    if-eqz v0, :cond_0

    .line 130
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->vivoxController:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    invoke-virtual {v0, v1}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->setIncomingMessenger(Landroid/os/Messenger;)V

    .line 131
    :cond_0
    iget-boolean v0, p0, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->bluetoothReceiverRegistered:Z

    if-eqz v0, :cond_1

    .line 132
    const/4 v0, 0x0

    iput-boolean v0, p0, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->bluetoothReceiverRegistered:Z

    .line 133
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->bluetoothScoIntentReceiver:Landroid/content/BroadcastReceiver;

    invoke-virtual {p0, v0}, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->unregisterReceiver(Landroid/content/BroadcastReceiver;)V

    .line 135
    :cond_1
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->audioStreamVolumeObserver:Lcom/lumiyaviewer/lumiya/voice/AudioStreamVolumeObserver;

    if-eqz v0, :cond_2

    .line 136
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->audioStreamVolumeObserver:Lcom/lumiyaviewer/lumiya/voice/AudioStreamVolumeObserver;

    invoke-virtual {v0}, Lcom/lumiyaviewer/lumiya/voice/AudioStreamVolumeObserver;->stop()V

    .line 137
    iput-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->audioStreamVolumeObserver:Lcom/lumiyaviewer/lumiya/voice/AudioStreamVolumeObserver;

    .line 139
    :cond_2
    iput-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->toAppMessenger:Landroid/os/Messenger;

    .line 140
    iput-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->audioManager:Landroid/media/AudioManager;

    .line 141
    sput-object v1, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->serviceInstance:Lcom/lumiyaviewer/lumiya/voice/VoiceService;

    .line 142
    invoke-super {p0}, Landroid/app/Service;->onDestroy()V

    .line 143
    return-void
.end method

.method public onStartCommand(Landroid/content/Intent;II)I
    .locals 1
    .param p1, "intent"    # Landroid/content/Intent;
    .param p2, "flags"    # I
    .param p3, "startId"    # I

    .prologue
    .line 147
    const/4 v0, 0x2

    return v0
.end method

.method public onUnbind(Landroid/content/Intent;)Z
    .locals 3
    .param p1, "intent"    # Landroid/content/Intent;

    .prologue
    const/4 v2, 0x0

    .line 120
    const-string v0, "Voice: service is unbound"

    new-array v1, v2, [Ljava/lang/Object;

    invoke-static {v0, v1}, Lcom/lumiyaviewer/lumiya/voice/Debug;->Printf(Ljava/lang/String;[Ljava/lang/Object;)V

    .line 121
    iput-boolean v2, p0, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->isServiceBound:Z

    .line 122
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->vivoxController:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    if-eqz v0, :cond_0

    .line 123
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->vivoxController:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    const/4 v1, 0x0

    invoke-virtual {v0, v1}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->Logout(Landroid/os/Messenger;)V

    .line 124
    :cond_0
    invoke-super {p0, p1}, Landroid/app/Service;->onUnbind(Landroid/content/Intent;)Z

    move-result v0

    return v0
.end method

.method public setVolume(F)V
    .locals 6
    .param p1, "speakerVolume"    # F

    .prologue
    const/4 v4, 0x0

    .line 342
    iget-object v5, p0, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->audioManager:Landroid/media/AudioManager;

    if-eqz v5, :cond_0

    .line 343
    iget-object v5, p0, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->audioManager:Landroid/media/AudioManager;

    invoke-virtual {v5}, Landroid/media/AudioManager;->isBluetoothScoOn()Z

    move-result v0

    .line 344
    .local v0, "bluetoothScoOn":Z
    if-eqz v0, :cond_1

    const/4 v2, 0x6

    .line 345
    .local v2, "streamType":I
    :goto_0
    iget-object v5, p0, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->audioManager:Landroid/media/AudioManager;

    invoke-virtual {v5, v2}, Landroid/media/AudioManager;->getStreamMaxVolume(I)I

    move-result v1

    .line 346
    .local v1, "maxVolume":I
    int-to-float v5, v1

    mul-float/2addr v5, p1

    invoke-static {v5}, Ljava/lang/Math;->round(F)I

    move-result v3

    .line 347
    .local v3, "targetVolume":I
    iget-object v5, p0, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->audioManager:Landroid/media/AudioManager;

    invoke-virtual {v5, v2, v3, v4}, Landroid/media/AudioManager;->setStreamVolume(III)V

    .line 350
    .end local v0    # "bluetoothScoOn":Z
    .end local v1    # "maxVolume":I
    .end local v2    # "streamType":I
    .end local v3    # "targetVolume":I
    :cond_0
    return-void

    .restart local v0    # "bluetoothScoOn":Z
    :cond_1
    move v2, v4

    .line 344
    goto :goto_0
.end method

.method updateAudioProperties()V
    .locals 11

    .prologue
    .line 167
    iget-object v8, p0, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->vivoxController:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    if-eqz v8, :cond_0

    iget-object v8, p0, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->audioManager:Landroid/media/AudioManager;

    if-eqz v8, :cond_0

    .line 168
    iget-object v1, p0, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->toAppMessenger:Landroid/os/Messenger;

    .line 169
    .local v1, "messenger":Landroid/os/Messenger;
    if-eqz v1, :cond_0

    .line 171
    iget-object v8, p0, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->audioManager:Landroid/media/AudioManager;

    invoke-virtual {v8}, Landroid/media/AudioManager;->isBluetoothScoOn()Z

    move-result v0

    .line 172
    .local v0, "bluetoothScoOn":Z
    if-eqz v0, :cond_1

    const/4 v6, 0x6

    .line 174
    .local v6, "streamType":I
    :goto_0
    iget-object v8, p0, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->audioManager:Landroid/media/AudioManager;

    invoke-virtual {v8, v6}, Landroid/media/AudioManager;->getStreamVolume(I)I

    move-result v7

    .line 175
    .local v7, "streamVolume":I
    iget-object v8, p0, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->audioManager:Landroid/media/AudioManager;

    invoke-virtual {v8, v6}, Landroid/media/AudioManager;->getStreamMaxVolume(I)I

    move-result v5

    .line 176
    .local v5, "streamMaxVolume":I
    int-to-float v8, v7

    int-to-float v9, v5

    div-float v2, v8, v9

    .line 177
    .local v2, "speakerVolume":F
    iget-object v8, p0, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->audioManager:Landroid/media/AudioManager;

    invoke-virtual {v8}, Landroid/media/AudioManager;->isSpeakerphoneOn()Z

    move-result v3

    .line 180
    .local v3, "speakerphoneOn":Z
    if-eqz v0, :cond_2

    .line 181
    sget-object v4, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceBluetoothState;->Active:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceBluetoothState;

    .line 199
    .local v4, "state":Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceBluetoothState;
    :goto_1
    sget-object v8, Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;->VoiceAudioProperties:Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;

    new-instance v9, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceAudioProperties;

    invoke-direct {v9, v2, v3, v4}, Lcom/lumiyaviewer/lumiya/voice/common/messages/VoiceAudioProperties;-><init>(FZLcom/lumiyaviewer/lumiya/voice/common/model/VoiceBluetoothState;)V

    const/4 v10, 0x0

    invoke-static {v1, v8, v9, v10}, Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessenger;->sendMessage(Landroid/os/Messenger;Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType;Lcom/lumiyaviewer/lumiya/voice/common/VoicePluginMessage;Landroid/os/Messenger;)Z

    .line 203
    .end local v0    # "bluetoothScoOn":Z
    .end local v1    # "messenger":Landroid/os/Messenger;
    .end local v2    # "speakerVolume":F
    .end local v3    # "speakerphoneOn":Z
    .end local v4    # "state":Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceBluetoothState;
    .end local v5    # "streamMaxVolume":I
    .end local v6    # "streamType":I
    .end local v7    # "streamVolume":I
    :cond_0
    return-void

    .line 172
    .restart local v0    # "bluetoothScoOn":Z
    .restart local v1    # "messenger":Landroid/os/Messenger;
    :cond_1
    const/4 v6, 0x0

    goto :goto_0

    .line 183
    .restart local v2    # "speakerVolume":F
    .restart local v3    # "speakerphoneOn":Z
    .restart local v5    # "streamMaxVolume":I
    .restart local v6    # "streamType":I
    .restart local v7    # "streamVolume":I
    :cond_2
    iget-object v8, p0, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->bluetoothScoState:Ljava/util/concurrent/atomic/AtomicInteger;

    invoke-virtual {v8}, Ljava/util/concurrent/atomic/AtomicInteger;->get()I

    move-result v8

    packed-switch v8, :pswitch_data_0

    .line 194
    sget-object v4, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceBluetoothState;->Error:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceBluetoothState;

    .restart local v4    # "state":Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceBluetoothState;
    goto :goto_1

    .line 185
    .end local v4    # "state":Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceBluetoothState;
    :pswitch_0
    sget-object v4, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceBluetoothState;->Connected:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceBluetoothState;

    .line 186
    .restart local v4    # "state":Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceBluetoothState;
    goto :goto_1

    .line 188
    .end local v4    # "state":Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceBluetoothState;
    :pswitch_1
    sget-object v4, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceBluetoothState;->Connecting:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceBluetoothState;

    .line 189
    .restart local v4    # "state":Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceBluetoothState;
    goto :goto_1

    .line 191
    .end local v4    # "state":Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceBluetoothState;
    :pswitch_2
    sget-object v4, Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceBluetoothState;->Disconnected:Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceBluetoothState;

    .line 192
    .restart local v4    # "state":Lcom/lumiyaviewer/lumiya/voice/common/model/VoiceBluetoothState;
    goto :goto_1

    .line 183
    :pswitch_data_0
    .packed-switch 0x0
        :pswitch_2
        :pswitch_0
        :pswitch_1
    .end packed-switch
.end method
