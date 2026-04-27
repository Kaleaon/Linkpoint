.class public Lcom/lumiyaviewer/lumiya/voice/VoicePermissionRequestActivity;
.super Landroid/support/v7/app/AppCompatActivity;
.source "VoicePermissionRequestActivity.java"


# static fields
.field private static final PERMISSION_AUDIO_REQUEST_CODE:I = 0x64

.field static final VOICE_INIT_MESSAGE:Ljava/lang/String; = "voiceInitMessage"

.field static final VOICE_INIT_REPLY_TO:Ljava/lang/String; = "voiceInitReplyTo"


# instance fields
.field private serviceBound:Z

.field private final serviceConnection:Landroid/content/ServiceConnection;

.field private serviceMessenger:Landroid/os/Messenger;


# direct methods
.method public constructor <init>()V
    .locals 1

    .prologue
    .line 17
    invoke-direct {p0}, Landroid/support/v7/app/AppCompatActivity;-><init>()V

    .line 24
    const/4 v0, 0x0

    iput-boolean v0, p0, Lcom/lumiyaviewer/lumiya/voice/VoicePermissionRequestActivity;->serviceBound:Z

    .line 25
    const/4 v0, 0x0

    iput-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VoicePermissionRequestActivity;->serviceMessenger:Landroid/os/Messenger;

    .line 42
    new-instance v0, Lcom/lumiyaviewer/lumiya/voice/VoicePermissionRequestActivity$1;

    invoke-direct {v0, p0}, Lcom/lumiyaviewer/lumiya/voice/VoicePermissionRequestActivity$1;-><init>(Lcom/lumiyaviewer/lumiya/voice/VoicePermissionRequestActivity;)V

    iput-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VoicePermissionRequestActivity;->serviceConnection:Landroid/content/ServiceConnection;

    return-void
.end method

.method static synthetic access$002(Lcom/lumiyaviewer/lumiya/voice/VoicePermissionRequestActivity;Landroid/os/Messenger;)Landroid/os/Messenger;
    .locals 0
    .param p0, "x0"    # Lcom/lumiyaviewer/lumiya/voice/VoicePermissionRequestActivity;
    .param p1, "x1"    # Landroid/os/Messenger;

    .prologue
    .line 17
    iput-object p1, p0, Lcom/lumiyaviewer/lumiya/voice/VoicePermissionRequestActivity;->serviceMessenger:Landroid/os/Messenger;

    return-object p1
.end method

.method private handlePermissionGranted(Z)V
    .locals 4
    .param p1, "granted"    # Z

    .prologue
    .line 72
    iget-object v3, p0, Lcom/lumiyaviewer/lumiya/voice/VoicePermissionRequestActivity;->serviceMessenger:Landroid/os/Messenger;

    if-eqz v3, :cond_0

    .line 73
    invoke-virtual {p0}, Lcom/lumiyaviewer/lumiya/voice/VoicePermissionRequestActivity;->getIntent()Landroid/content/Intent;

    move-result-object v1

    .line 74
    .local v1, "intent":Landroid/content/Intent;
    const-string v3, "voiceInitReplyTo"

    invoke-virtual {v1, v3}, Landroid/content/Intent;->hasExtra(Ljava/lang/String;)Z

    move-result v3

    if-eqz v3, :cond_0

    .line 75
    invoke-static {}, Landroid/os/Message;->obtain()Landroid/os/Message;

    move-result-object v2

    .line 76
    .local v2, "msg":Landroid/os/Message;
    const/16 v3, 0x12c

    iput v3, v2, Landroid/os/Message;->what:I

    .line 77
    if-eqz p1, :cond_1

    const/4 v3, 0x1

    :goto_0
    iput v3, v2, Landroid/os/Message;->arg1:I

    .line 78
    const-string v3, "voiceInitReplyTo"

    invoke-virtual {v1, v3}, Landroid/content/Intent;->getParcelableExtra(Ljava/lang/String;)Landroid/os/Parcelable;

    move-result-object v3

    iput-object v3, v2, Landroid/os/Message;->obj:Ljava/lang/Object;

    .line 80
    :try_start_0
    iget-object v3, p0, Lcom/lumiyaviewer/lumiya/voice/VoicePermissionRequestActivity;->serviceMessenger:Landroid/os/Messenger;

    invoke-virtual {v3, v2}, Landroid/os/Messenger;->send(Landroid/os/Message;)V
    :try_end_0
    .catch Landroid/os/RemoteException; {:try_start_0 .. :try_end_0} :catch_0

    .line 86
    .end local v1    # "intent":Landroid/content/Intent;
    .end local v2    # "msg":Landroid/os/Message;
    :cond_0
    :goto_1
    invoke-virtual {p0}, Lcom/lumiyaviewer/lumiya/voice/VoicePermissionRequestActivity;->finish()V

    .line 87
    return-void

    .line 77
    .restart local v1    # "intent":Landroid/content/Intent;
    .restart local v2    # "msg":Landroid/os/Message;
    :cond_1
    const/4 v3, 0x0

    goto :goto_0

    .line 81
    :catch_0
    move-exception v0

    .line 82
    .local v0, "e":Landroid/os/RemoteException;
    invoke-static {v0}, Lcom/lumiyaviewer/lumiya/voice/Debug;->Warning(Ljava/lang/Throwable;)V

    goto :goto_1
.end method


# virtual methods
.method protected onCreate(Landroid/os/Bundle;)V
    .locals 4
    .param p1, "savedInstanceState"    # Landroid/os/Bundle;
        .annotation build Landroid/support/annotation/Nullable;
        .end annotation
    .end param

    .prologue
    const/4 v3, 0x1

    .line 30
    invoke-super {p0, p1}, Landroid/support/v7/app/AppCompatActivity;->onCreate(Landroid/os/Bundle;)V

    .line 32
    const-string v2, "android.permission.RECORD_AUDIO"

    invoke-static {p0, v2}, Landroid/support/v4/content/ContextCompat;->checkSelfPermission(Landroid/content/Context;Ljava/lang/String;)I

    move-result v1

    .line 33
    .local v1, "permissionCheck":I
    if-eqz v1, :cond_0

    .line 35
    new-instance v0, Landroid/content/Intent;

    const-class v2, Lcom/lumiyaviewer/lumiya/voice/VoiceService;

    invoke-direct {v0, p0, v2}, Landroid/content/Intent;-><init>(Landroid/content/Context;Ljava/lang/Class;)V

    .line 36
    .local v0, "intent":Landroid/content/Intent;
    iget-object v2, p0, Lcom/lumiyaviewer/lumiya/voice/VoicePermissionRequestActivity;->serviceConnection:Landroid/content/ServiceConnection;

    invoke-virtual {p0, v0, v2, v3}, Lcom/lumiyaviewer/lumiya/voice/VoicePermissionRequestActivity;->bindService(Landroid/content/Intent;Landroid/content/ServiceConnection;I)Z

    move-result v2

    iput-boolean v2, p0, Lcom/lumiyaviewer/lumiya/voice/VoicePermissionRequestActivity;->serviceBound:Z

    .line 40
    .end local v0    # "intent":Landroid/content/Intent;
    :goto_0
    return-void

    .line 38
    :cond_0
    invoke-direct {p0, v3}, Lcom/lumiyaviewer/lumiya/voice/VoicePermissionRequestActivity;->handlePermissionGranted(Z)V

    goto :goto_0
.end method

.method protected onDestroy()V
    .locals 1

    .prologue
    .line 91
    iget-boolean v0, p0, Lcom/lumiyaviewer/lumiya/voice/VoicePermissionRequestActivity;->serviceBound:Z

    if-eqz v0, :cond_0

    .line 92
    const/4 v0, 0x0

    iput-boolean v0, p0, Lcom/lumiyaviewer/lumiya/voice/VoicePermissionRequestActivity;->serviceBound:Z

    .line 93
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VoicePermissionRequestActivity;->serviceConnection:Landroid/content/ServiceConnection;

    invoke-virtual {p0, v0}, Lcom/lumiyaviewer/lumiya/voice/VoicePermissionRequestActivity;->unbindService(Landroid/content/ServiceConnection;)V

    .line 95
    :cond_0
    invoke-super {p0}, Landroid/support/v7/app/AppCompatActivity;->onDestroy()V

    .line 96
    return-void
.end method

.method public onRequestPermissionsResult(I[Ljava/lang/String;[I)V
    .locals 5
    .param p1, "requestCode"    # I
    .param p2, "permissions"    # [Ljava/lang/String;
        .annotation runtime Ljavax/annotation/Nonnull;
        .end annotation
    .end param
    .param p3, "grantResults"    # [I
        .annotation runtime Ljavax/annotation/Nonnull;
        .end annotation
    .end param

    .prologue
    const/4 v4, 0x1

    const/4 v3, 0x0

    .line 62
    const-string v0, "Cardboard: onRequestPermissionResult, code %d"

    new-array v1, v4, [Ljava/lang/Object;

    invoke-static {p1}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v2

    aput-object v2, v1, v3

    invoke-static {v0, v1}, Lcom/lumiyaviewer/lumiya/voice/Debug;->Printf(Ljava/lang/String;[Ljava/lang/Object;)V

    .line 63
    const/16 v0, 0x64

    if-ne p1, v0, :cond_0

    .line 64
    array-length v0, p3

    if-lez v0, :cond_1

    aget v0, p3, v3

    if-nez v0, :cond_1

    .line 65
    invoke-direct {p0, v4}, Lcom/lumiyaviewer/lumiya/voice/VoicePermissionRequestActivity;->handlePermissionGranted(Z)V

    .line 69
    :cond_0
    :goto_0
    return-void

    .line 67
    :cond_1
    invoke-direct {p0, v3}, Lcom/lumiyaviewer/lumiya/voice/VoicePermissionRequestActivity;->handlePermissionGranted(Z)V

    goto :goto_0
.end method
