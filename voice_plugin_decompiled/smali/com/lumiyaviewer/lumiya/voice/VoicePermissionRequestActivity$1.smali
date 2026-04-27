.class Lcom/lumiyaviewer/lumiya/voice/VoicePermissionRequestActivity$1;
.super Ljava/lang/Object;
.source "VoicePermissionRequestActivity.java"

# interfaces
.implements Landroid/content/ServiceConnection;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/lumiyaviewer/lumiya/voice/VoicePermissionRequestActivity;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/lumiyaviewer/lumiya/voice/VoicePermissionRequestActivity;


# direct methods
.method constructor <init>(Lcom/lumiyaviewer/lumiya/voice/VoicePermissionRequestActivity;)V
    .locals 0
    .param p1, "this$0"    # Lcom/lumiyaviewer/lumiya/voice/VoicePermissionRequestActivity;

    .prologue
    .line 42
    iput-object p1, p0, Lcom/lumiyaviewer/lumiya/voice/VoicePermissionRequestActivity$1;->this$0:Lcom/lumiyaviewer/lumiya/voice/VoicePermissionRequestActivity;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public onServiceConnected(Landroid/content/ComponentName;Landroid/os/IBinder;)V
    .locals 4
    .param p1, "componentName"    # Landroid/content/ComponentName;
    .param p2, "iBinder"    # Landroid/os/IBinder;

    .prologue
    .line 45
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VoicePermissionRequestActivity$1;->this$0:Lcom/lumiyaviewer/lumiya/voice/VoicePermissionRequestActivity;

    new-instance v1, Landroid/os/Messenger;

    invoke-direct {v1, p2}, Landroid/os/Messenger;-><init>(Landroid/os/IBinder;)V

    invoke-static {v0, v1}, Lcom/lumiyaviewer/lumiya/voice/VoicePermissionRequestActivity;->access$002(Lcom/lumiyaviewer/lumiya/voice/VoicePermissionRequestActivity;Landroid/os/Messenger;)Landroid/os/Messenger;

    .line 46
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VoicePermissionRequestActivity$1;->this$0:Lcom/lumiyaviewer/lumiya/voice/VoicePermissionRequestActivity;

    const/4 v1, 0x1

    new-array v1, v1, [Ljava/lang/String;

    const/4 v2, 0x0

    const-string v3, "android.permission.RECORD_AUDIO"

    aput-object v3, v1, v2

    const/16 v2, 0x64

    invoke-static {v0, v1, v2}, Landroid/support/v4/app/ActivityCompat;->requestPermissions(Landroid/app/Activity;[Ljava/lang/String;I)V

    .line 51
    return-void
.end method

.method public onServiceDisconnected(Landroid/content/ComponentName;)V
    .locals 2
    .param p1, "componentName"    # Landroid/content/ComponentName;

    .prologue
    .line 55
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VoicePermissionRequestActivity$1;->this$0:Lcom/lumiyaviewer/lumiya/voice/VoicePermissionRequestActivity;

    const/4 v1, 0x0

    invoke-static {v0, v1}, Lcom/lumiyaviewer/lumiya/voice/VoicePermissionRequestActivity;->access$002(Lcom/lumiyaviewer/lumiya/voice/VoicePermissionRequestActivity;Landroid/os/Messenger;)Landroid/os/Messenger;

    .line 56
    return-void
.end method
