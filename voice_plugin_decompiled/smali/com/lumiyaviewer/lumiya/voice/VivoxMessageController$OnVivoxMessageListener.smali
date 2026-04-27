.class public interface abstract Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController$OnVivoxMessageListener;
.super Ljava/lang/Object;
.source "VivoxMessageController.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/lumiyaviewer/lumiya/voice/VivoxMessageController;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x609
    name = "OnVivoxMessageListener"
.end annotation


# virtual methods
.method public abstract onVivoxEvent(Lcom/vivox/service/vx_evt_base_t;)V
.end method

.method public abstract onVivoxMessage(Lcom/vivox/service/vx_message_base_t;)V
.end method
