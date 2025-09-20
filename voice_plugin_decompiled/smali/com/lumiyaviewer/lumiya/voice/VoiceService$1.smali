.class Lcom/lumiyaviewer/lumiya/voice/VoiceService$1;
.super Landroid/content/BroadcastReceiver;
.source "VoiceService.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/lumiyaviewer/lumiya/voice/VoiceService;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/lumiyaviewer/lumiya/voice/VoiceService;


# direct methods
.method constructor <init>(Lcom/lumiyaviewer/lumiya/voice/VoiceService;)V
    .locals 0
    .param p1, "this$0"    # Lcom/lumiyaviewer/lumiya/voice/VoiceService;

    .prologue
    .line 150
    iput-object p1, p0, Lcom/lumiyaviewer/lumiya/voice/VoiceService$1;->this$0:Lcom/lumiyaviewer/lumiya/voice/VoiceService;

    invoke-direct {p0}, Landroid/content/BroadcastReceiver;-><init>()V

    return-void
.end method


# virtual methods
.method public onReceive(Landroid/content/Context;Landroid/content/Intent;)V
    .locals 1
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "intent"    # Landroid/content/Intent;

    .prologue
    .line 153
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VoiceService$1;->this$0:Lcom/lumiyaviewer/lumiya/voice/VoiceService;

    invoke-static {v0, p2}, Lcom/lumiyaviewer/lumiya/voice/VoiceService;->access$1200(Lcom/lumiyaviewer/lumiya/voice/VoiceService;Landroid/content/Intent;)V

    .line 154
    return-void
.end method
