.class Lcom/lumiyaviewer/lumiya/voice/VivoxController$12;
.super Ljava/lang/Object;
.source "VivoxController.java"

# interfaces
.implements Ljava/lang/Runnable;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/lumiyaviewer/lumiya/voice/VivoxController;->onBluetoothScoStateChanged(I)V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

.field final synthetic val$state:I


# direct methods
.method constructor <init>(Lcom/lumiyaviewer/lumiya/voice/VivoxController;I)V
    .locals 0
    .param p1, "this$0"    # Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    .prologue
    .line 587
    iput-object p1, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$12;->this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    iput p2, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$12;->val$state:I

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public run()V
    .locals 3

    .prologue
    const/4 v2, 0x1

    .line 590
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$12;->this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    iget v1, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$12;->val$state:I

    invoke-static {v0, v1}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->access$1602(Lcom/lumiyaviewer/lumiya/voice/VivoxController;I)I

    .line 591
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$12;->this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    invoke-static {v0}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->access$1600(Lcom/lumiyaviewer/lumiya/voice/VivoxController;)I

    move-result v0

    if-ne v0, v2, :cond_0

    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$12;->this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    invoke-static {v0}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->access$500(Lcom/lumiyaviewer/lumiya/voice/VivoxController;)Landroid/media/AudioManager;

    move-result-object v0

    if-eqz v0, :cond_0

    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$12;->this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    invoke-static {v0}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->access$1500(Lcom/lumiyaviewer/lumiya/voice/VivoxController;)Z

    move-result v0

    if-eqz v0, :cond_0

    .line 592
    iget-object v0, p0, Lcom/lumiyaviewer/lumiya/voice/VivoxController$12;->this$0:Lcom/lumiyaviewer/lumiya/voice/VivoxController;

    invoke-static {v0, v2}, Lcom/lumiyaviewer/lumiya/voice/VivoxController;->access$1700(Lcom/lumiyaviewer/lumiya/voice/VivoxController;Z)V

    .line 594
    :cond_0
    return-void
.end method
