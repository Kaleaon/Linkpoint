.class public Lcom/vivox/service/SWIGTYPE_p_p_vx_req_sessiongroup_set_tx_no_session;
.super Ljava/lang/Object;
.source "SWIGTYPE_p_p_vx_req_sessiongroup_set_tx_no_session.java"


# instance fields
.field private swigCPtr:J


# direct methods
.method protected constructor <init>()V
    .locals 2

    .prologue
    .line 6
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 7
    const-wide/16 v0, 0x0

    iput-wide v0, p0, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_sessiongroup_set_tx_no_session;->swigCPtr:J

    .line 8
    return-void
.end method

.method protected constructor <init>(JZ)V
    .locals 1
    .param p1, "j"    # J
    .param p3, "z"    # Z

    .prologue
    .line 10
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 11
    iput-wide p1, p0, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_sessiongroup_set_tx_no_session;->swigCPtr:J

    .line 12
    return-void
.end method

.method protected static getCPtr(Lcom/vivox/service/SWIGTYPE_p_p_vx_req_sessiongroup_set_tx_no_session;)J
    .locals 2
    .param p0, "sWIGTYPE_p_p_vx_req_sessiongroup_set_tx_no_session"    # Lcom/vivox/service/SWIGTYPE_p_p_vx_req_sessiongroup_set_tx_no_session;

    .prologue
    .line 15
    if-nez p0, :cond_0

    const-wide/16 v0, 0x0

    :goto_0
    return-wide v0

    :cond_0
    iget-wide v0, p0, Lcom/vivox/service/SWIGTYPE_p_p_vx_req_sessiongroup_set_tx_no_session;->swigCPtr:J

    goto :goto_0
.end method
