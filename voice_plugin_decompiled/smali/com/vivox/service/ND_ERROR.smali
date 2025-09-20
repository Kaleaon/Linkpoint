.class public final Lcom/vivox/service/ND_ERROR;
.super Ljava/lang/Object;
.source "ND_ERROR.java"


# static fields
.field public static final ND_E_CANT_CONNECT_TO_ECHO_SERVER:Lcom/vivox/service/ND_ERROR;

.field public static final ND_E_CANT_CONTACT_STUN_SERVER_ON_UDP_PORT_3478:Lcom/vivox/service/ND_ERROR;

.field public static final ND_E_CANT_CONVERT_LOCAL_IP_ADDRESS:Lcom/vivox/service/ND_ERROR;

.field public static final ND_E_CANT_CREATE_TCP_SOCKET:Lcom/vivox/service/ND_ERROR;

.field public static final ND_E_CANT_FIND_SENDECHO2_PROCADDR:Lcom/vivox/service/ND_ERROR;

.field public static final ND_E_CANT_ICMP:Lcom/vivox/service/ND_ERROR;

.field public static final ND_E_CANT_LOAD_ICMP_LIBRARY:Lcom/vivox/service/ND_ERROR;

.field public static final ND_E_CANT_RESOLVE_ROOT_DNS_SERVER:Lcom/vivox/service/ND_ERROR;

.field public static final ND_E_CANT_RESOLVE_VIVOX_UDP_SERVER:Lcom/vivox/service/ND_ERROR;

.field public static final ND_E_ECHO_SERVER_CANT_ALLOCATE_MEDIA_SOCKET:Lcom/vivox/service/ND_ERROR;

.field public static final ND_E_ECHO_SERVER_CANT_ALLOCATE_SIP_SOCKET:Lcom/vivox/service/ND_ERROR;

.field public static final ND_E_ECHO_SERVER_CANT_RESOLVE_NAME:Lcom/vivox/service/ND_ERROR;

.field public static final ND_E_ECHO_SERVER_LOGIN_RECV_FAILED:Lcom/vivox/service/ND_ERROR;

.field public static final ND_E_ECHO_SERVER_LOGIN_RECV_FAILED_TIMEOUT:Lcom/vivox/service/ND_ERROR;

.field public static final ND_E_ECHO_SERVER_LOGIN_RESPONSE_FAILED_STATUS:Lcom/vivox/service/ND_ERROR;

.field public static final ND_E_ECHO_SERVER_LOGIN_RESPONSE_MISSING_AUDIORTCP:Lcom/vivox/service/ND_ERROR;

.field public static final ND_E_ECHO_SERVER_LOGIN_RESPONSE_MISSING_AUDIORTP:Lcom/vivox/service/ND_ERROR;

.field public static final ND_E_ECHO_SERVER_LOGIN_RESPONSE_MISSING_SESSIONID:Lcom/vivox/service/ND_ERROR;

.field public static final ND_E_ECHO_SERVER_LOGIN_RESPONSE_MISSING_SIPPORT:Lcom/vivox/service/ND_ERROR;

.field public static final ND_E_ECHO_SERVER_LOGIN_RESPONSE_MISSING_STATUS:Lcom/vivox/service/ND_ERROR;

.field public static final ND_E_ECHO_SERVER_LOGIN_RESPONSE_MISSING_VIDEORTCP:Lcom/vivox/service/ND_ERROR;

.field public static final ND_E_ECHO_SERVER_LOGIN_RESPONSE_MISSING_VIDEORTP:Lcom/vivox/service/ND_ERROR;

.field public static final ND_E_ECHO_SERVER_LOGIN_SEND_FAILED:Lcom/vivox/service/ND_ERROR;

.field public static final ND_E_ECHO_SERVER_SIP_MALFORMED_TCP_PACKET:Lcom/vivox/service/ND_ERROR;

.field public static final ND_E_ECHO_SERVER_SIP_NO_TCP:Lcom/vivox/service/ND_ERROR;

.field public static final ND_E_ECHO_SERVER_SIP_NO_UDP:Lcom/vivox/service/ND_ERROR;

.field public static final ND_E_ECHO_SERVER_SIP_NO_UDP_OR_TCP:Lcom/vivox/service/ND_ERROR;

.field public static final ND_E_ECHO_SERVER_SIP_TCP_PACKETS_DIFFERENT:Lcom/vivox/service/ND_ERROR;

.field public static final ND_E_ECHO_SERVER_SIP_TCP_PACKETS_DIFFERENT_SIZE:Lcom/vivox/service/ND_ERROR;

.field public static final ND_E_ECHO_SERVER_SIP_TCP_RECV_FAILED:Lcom/vivox/service/ND_ERROR;

.field public static final ND_E_ECHO_SERVER_SIP_TCP_SEND_FAILED:Lcom/vivox/service/ND_ERROR;

.field public static final ND_E_ECHO_SERVER_SIP_UDP_DATA_DIFFERENT:Lcom/vivox/service/ND_ERROR;

.field public static final ND_E_ECHO_SERVER_SIP_UDP_DIFFERENT_LENGTH:Lcom/vivox/service/ND_ERROR;

.field public static final ND_E_ECHO_SERVER_SIP_UDP_RECV_FAILED:Lcom/vivox/service/ND_ERROR;

.field public static final ND_E_ECHO_SERVER_SIP_UDP_SEND_FAILED:Lcom/vivox/service/ND_ERROR;

.field public static final ND_E_ECHO_SERVER_TCP_SET_ASYNC_FAILED:Lcom/vivox/service/ND_ERROR;

.field public static final ND_E_ECHO_SERVER_UDP_SET_ASYNC_FAILED:Lcom/vivox/service/ND_ERROR;

.field public static final ND_E_NO_ERROR:Lcom/vivox/service/ND_ERROR;

.field public static final ND_E_NO_INTERFACE:Lcom/vivox/service/ND_ERROR;

.field public static final ND_E_NO_INTERFACE_WITH_GATEWAY:Lcom/vivox/service/ND_ERROR;

.field public static final ND_E_NO_INTERFACE_WITH_ROUTE:Lcom/vivox/service/ND_ERROR;

.field public static final ND_E_TEST_NOT_RUN:Lcom/vivox/service/ND_ERROR;

.field public static final ND_E_TIMEOUT:Lcom/vivox/service/ND_ERROR;

.field private static swigNext:I

.field private static swigValues:[Lcom/vivox/service/ND_ERROR;


# instance fields
.field private final swigName:Ljava/lang/String;

.field private final swigValue:I


# direct methods
.method static constructor <clinit>()V
    .locals 4

    .prologue
    const/4 v3, 0x0

    .line 4
    new-instance v0, Lcom/vivox/service/ND_ERROR;

    const-string v1, "ND_E_CANT_CONNECT_TO_ECHO_SERVER"

    invoke-direct {v0, v1}, Lcom/vivox/service/ND_ERROR;-><init>(Ljava/lang/String;)V

    sput-object v0, Lcom/vivox/service/ND_ERROR;->ND_E_CANT_CONNECT_TO_ECHO_SERVER:Lcom/vivox/service/ND_ERROR;

    .line 5
    new-instance v0, Lcom/vivox/service/ND_ERROR;

    const-string v1, "ND_E_CANT_CONTACT_STUN_SERVER_ON_UDP_PORT_3478"

    invoke-direct {v0, v1}, Lcom/vivox/service/ND_ERROR;-><init>(Ljava/lang/String;)V

    sput-object v0, Lcom/vivox/service/ND_ERROR;->ND_E_CANT_CONTACT_STUN_SERVER_ON_UDP_PORT_3478:Lcom/vivox/service/ND_ERROR;

    .line 6
    new-instance v0, Lcom/vivox/service/ND_ERROR;

    const-string v1, "ND_E_CANT_CONVERT_LOCAL_IP_ADDRESS"

    invoke-direct {v0, v1}, Lcom/vivox/service/ND_ERROR;-><init>(Ljava/lang/String;)V

    sput-object v0, Lcom/vivox/service/ND_ERROR;->ND_E_CANT_CONVERT_LOCAL_IP_ADDRESS:Lcom/vivox/service/ND_ERROR;

    .line 7
    new-instance v0, Lcom/vivox/service/ND_ERROR;

    const-string v1, "ND_E_CANT_CREATE_TCP_SOCKET"

    invoke-direct {v0, v1}, Lcom/vivox/service/ND_ERROR;-><init>(Ljava/lang/String;)V

    sput-object v0, Lcom/vivox/service/ND_ERROR;->ND_E_CANT_CREATE_TCP_SOCKET:Lcom/vivox/service/ND_ERROR;

    .line 8
    new-instance v0, Lcom/vivox/service/ND_ERROR;

    const-string v1, "ND_E_CANT_FIND_SENDECHO2_PROCADDR"

    invoke-direct {v0, v1}, Lcom/vivox/service/ND_ERROR;-><init>(Ljava/lang/String;)V

    sput-object v0, Lcom/vivox/service/ND_ERROR;->ND_E_CANT_FIND_SENDECHO2_PROCADDR:Lcom/vivox/service/ND_ERROR;

    .line 9
    new-instance v0, Lcom/vivox/service/ND_ERROR;

    const-string v1, "ND_E_CANT_ICMP"

    invoke-direct {v0, v1}, Lcom/vivox/service/ND_ERROR;-><init>(Ljava/lang/String;)V

    sput-object v0, Lcom/vivox/service/ND_ERROR;->ND_E_CANT_ICMP:Lcom/vivox/service/ND_ERROR;

    .line 10
    new-instance v0, Lcom/vivox/service/ND_ERROR;

    const-string v1, "ND_E_CANT_LOAD_ICMP_LIBRARY"

    invoke-direct {v0, v1}, Lcom/vivox/service/ND_ERROR;-><init>(Ljava/lang/String;)V

    sput-object v0, Lcom/vivox/service/ND_ERROR;->ND_E_CANT_LOAD_ICMP_LIBRARY:Lcom/vivox/service/ND_ERROR;

    .line 11
    new-instance v0, Lcom/vivox/service/ND_ERROR;

    const-string v1, "ND_E_CANT_RESOLVE_ROOT_DNS_SERVER"

    invoke-direct {v0, v1}, Lcom/vivox/service/ND_ERROR;-><init>(Ljava/lang/String;)V

    sput-object v0, Lcom/vivox/service/ND_ERROR;->ND_E_CANT_RESOLVE_ROOT_DNS_SERVER:Lcom/vivox/service/ND_ERROR;

    .line 12
    new-instance v0, Lcom/vivox/service/ND_ERROR;

    const-string v1, "ND_E_CANT_RESOLVE_VIVOX_UDP_SERVER"

    invoke-direct {v0, v1}, Lcom/vivox/service/ND_ERROR;-><init>(Ljava/lang/String;)V

    sput-object v0, Lcom/vivox/service/ND_ERROR;->ND_E_CANT_RESOLVE_VIVOX_UDP_SERVER:Lcom/vivox/service/ND_ERROR;

    .line 13
    new-instance v0, Lcom/vivox/service/ND_ERROR;

    const-string v1, "ND_E_ECHO_SERVER_CANT_ALLOCATE_MEDIA_SOCKET"

    invoke-direct {v0, v1}, Lcom/vivox/service/ND_ERROR;-><init>(Ljava/lang/String;)V

    sput-object v0, Lcom/vivox/service/ND_ERROR;->ND_E_ECHO_SERVER_CANT_ALLOCATE_MEDIA_SOCKET:Lcom/vivox/service/ND_ERROR;

    .line 14
    new-instance v0, Lcom/vivox/service/ND_ERROR;

    const-string v1, "ND_E_ECHO_SERVER_CANT_ALLOCATE_SIP_SOCKET"

    invoke-direct {v0, v1}, Lcom/vivox/service/ND_ERROR;-><init>(Ljava/lang/String;)V

    sput-object v0, Lcom/vivox/service/ND_ERROR;->ND_E_ECHO_SERVER_CANT_ALLOCATE_SIP_SOCKET:Lcom/vivox/service/ND_ERROR;

    .line 15
    new-instance v0, Lcom/vivox/service/ND_ERROR;

    const-string v1, "ND_E_ECHO_SERVER_CANT_RESOLVE_NAME"

    invoke-direct {v0, v1}, Lcom/vivox/service/ND_ERROR;-><init>(Ljava/lang/String;)V

    sput-object v0, Lcom/vivox/service/ND_ERROR;->ND_E_ECHO_SERVER_CANT_RESOLVE_NAME:Lcom/vivox/service/ND_ERROR;

    .line 16
    new-instance v0, Lcom/vivox/service/ND_ERROR;

    const-string v1, "ND_E_ECHO_SERVER_LOGIN_RECV_FAILED"

    invoke-direct {v0, v1}, Lcom/vivox/service/ND_ERROR;-><init>(Ljava/lang/String;)V

    sput-object v0, Lcom/vivox/service/ND_ERROR;->ND_E_ECHO_SERVER_LOGIN_RECV_FAILED:Lcom/vivox/service/ND_ERROR;

    .line 17
    new-instance v0, Lcom/vivox/service/ND_ERROR;

    const-string v1, "ND_E_ECHO_SERVER_LOGIN_RECV_FAILED_TIMEOUT"

    invoke-direct {v0, v1}, Lcom/vivox/service/ND_ERROR;-><init>(Ljava/lang/String;)V

    sput-object v0, Lcom/vivox/service/ND_ERROR;->ND_E_ECHO_SERVER_LOGIN_RECV_FAILED_TIMEOUT:Lcom/vivox/service/ND_ERROR;

    .line 18
    new-instance v0, Lcom/vivox/service/ND_ERROR;

    const-string v1, "ND_E_ECHO_SERVER_LOGIN_RESPONSE_FAILED_STATUS"

    invoke-direct {v0, v1}, Lcom/vivox/service/ND_ERROR;-><init>(Ljava/lang/String;)V

    sput-object v0, Lcom/vivox/service/ND_ERROR;->ND_E_ECHO_SERVER_LOGIN_RESPONSE_FAILED_STATUS:Lcom/vivox/service/ND_ERROR;

    .line 19
    new-instance v0, Lcom/vivox/service/ND_ERROR;

    const-string v1, "ND_E_ECHO_SERVER_LOGIN_RESPONSE_MISSING_AUDIORTCP"

    invoke-direct {v0, v1}, Lcom/vivox/service/ND_ERROR;-><init>(Ljava/lang/String;)V

    sput-object v0, Lcom/vivox/service/ND_ERROR;->ND_E_ECHO_SERVER_LOGIN_RESPONSE_MISSING_AUDIORTCP:Lcom/vivox/service/ND_ERROR;

    .line 20
    new-instance v0, Lcom/vivox/service/ND_ERROR;

    const-string v1, "ND_E_ECHO_SERVER_LOGIN_RESPONSE_MISSING_AUDIORTP"

    invoke-direct {v0, v1}, Lcom/vivox/service/ND_ERROR;-><init>(Ljava/lang/String;)V

    sput-object v0, Lcom/vivox/service/ND_ERROR;->ND_E_ECHO_SERVER_LOGIN_RESPONSE_MISSING_AUDIORTP:Lcom/vivox/service/ND_ERROR;

    .line 21
    new-instance v0, Lcom/vivox/service/ND_ERROR;

    const-string v1, "ND_E_ECHO_SERVER_LOGIN_RESPONSE_MISSING_SESSIONID"

    invoke-direct {v0, v1}, Lcom/vivox/service/ND_ERROR;-><init>(Ljava/lang/String;)V

    sput-object v0, Lcom/vivox/service/ND_ERROR;->ND_E_ECHO_SERVER_LOGIN_RESPONSE_MISSING_SESSIONID:Lcom/vivox/service/ND_ERROR;

    .line 22
    new-instance v0, Lcom/vivox/service/ND_ERROR;

    const-string v1, "ND_E_ECHO_SERVER_LOGIN_RESPONSE_MISSING_SIPPORT"

    invoke-direct {v0, v1}, Lcom/vivox/service/ND_ERROR;-><init>(Ljava/lang/String;)V

    sput-object v0, Lcom/vivox/service/ND_ERROR;->ND_E_ECHO_SERVER_LOGIN_RESPONSE_MISSING_SIPPORT:Lcom/vivox/service/ND_ERROR;

    .line 23
    new-instance v0, Lcom/vivox/service/ND_ERROR;

    const-string v1, "ND_E_ECHO_SERVER_LOGIN_RESPONSE_MISSING_STATUS"

    invoke-direct {v0, v1}, Lcom/vivox/service/ND_ERROR;-><init>(Ljava/lang/String;)V

    sput-object v0, Lcom/vivox/service/ND_ERROR;->ND_E_ECHO_SERVER_LOGIN_RESPONSE_MISSING_STATUS:Lcom/vivox/service/ND_ERROR;

    .line 24
    new-instance v0, Lcom/vivox/service/ND_ERROR;

    const-string v1, "ND_E_ECHO_SERVER_LOGIN_RESPONSE_MISSING_VIDEORTCP"

    invoke-direct {v0, v1}, Lcom/vivox/service/ND_ERROR;-><init>(Ljava/lang/String;)V

    sput-object v0, Lcom/vivox/service/ND_ERROR;->ND_E_ECHO_SERVER_LOGIN_RESPONSE_MISSING_VIDEORTCP:Lcom/vivox/service/ND_ERROR;

    .line 25
    new-instance v0, Lcom/vivox/service/ND_ERROR;

    const-string v1, "ND_E_ECHO_SERVER_LOGIN_RESPONSE_MISSING_VIDEORTP"

    invoke-direct {v0, v1}, Lcom/vivox/service/ND_ERROR;-><init>(Ljava/lang/String;)V

    sput-object v0, Lcom/vivox/service/ND_ERROR;->ND_E_ECHO_SERVER_LOGIN_RESPONSE_MISSING_VIDEORTP:Lcom/vivox/service/ND_ERROR;

    .line 26
    new-instance v0, Lcom/vivox/service/ND_ERROR;

    const-string v1, "ND_E_ECHO_SERVER_LOGIN_SEND_FAILED"

    invoke-direct {v0, v1}, Lcom/vivox/service/ND_ERROR;-><init>(Ljava/lang/String;)V

    sput-object v0, Lcom/vivox/service/ND_ERROR;->ND_E_ECHO_SERVER_LOGIN_SEND_FAILED:Lcom/vivox/service/ND_ERROR;

    .line 27
    new-instance v0, Lcom/vivox/service/ND_ERROR;

    const-string v1, "ND_E_ECHO_SERVER_SIP_MALFORMED_TCP_PACKET"

    invoke-direct {v0, v1}, Lcom/vivox/service/ND_ERROR;-><init>(Ljava/lang/String;)V

    sput-object v0, Lcom/vivox/service/ND_ERROR;->ND_E_ECHO_SERVER_SIP_MALFORMED_TCP_PACKET:Lcom/vivox/service/ND_ERROR;

    .line 28
    new-instance v0, Lcom/vivox/service/ND_ERROR;

    const-string v1, "ND_E_ECHO_SERVER_SIP_NO_TCP"

    invoke-direct {v0, v1}, Lcom/vivox/service/ND_ERROR;-><init>(Ljava/lang/String;)V

    sput-object v0, Lcom/vivox/service/ND_ERROR;->ND_E_ECHO_SERVER_SIP_NO_TCP:Lcom/vivox/service/ND_ERROR;

    .line 29
    new-instance v0, Lcom/vivox/service/ND_ERROR;

    const-string v1, "ND_E_ECHO_SERVER_SIP_NO_UDP"

    invoke-direct {v0, v1}, Lcom/vivox/service/ND_ERROR;-><init>(Ljava/lang/String;)V

    sput-object v0, Lcom/vivox/service/ND_ERROR;->ND_E_ECHO_SERVER_SIP_NO_UDP:Lcom/vivox/service/ND_ERROR;

    .line 30
    new-instance v0, Lcom/vivox/service/ND_ERROR;

    const-string v1, "ND_E_ECHO_SERVER_SIP_NO_UDP_OR_TCP"

    invoke-direct {v0, v1}, Lcom/vivox/service/ND_ERROR;-><init>(Ljava/lang/String;)V

    sput-object v0, Lcom/vivox/service/ND_ERROR;->ND_E_ECHO_SERVER_SIP_NO_UDP_OR_TCP:Lcom/vivox/service/ND_ERROR;

    .line 31
    new-instance v0, Lcom/vivox/service/ND_ERROR;

    const-string v1, "ND_E_ECHO_SERVER_SIP_TCP_PACKETS_DIFFERENT"

    invoke-direct {v0, v1}, Lcom/vivox/service/ND_ERROR;-><init>(Ljava/lang/String;)V

    sput-object v0, Lcom/vivox/service/ND_ERROR;->ND_E_ECHO_SERVER_SIP_TCP_PACKETS_DIFFERENT:Lcom/vivox/service/ND_ERROR;

    .line 32
    new-instance v0, Lcom/vivox/service/ND_ERROR;

    const-string v1, "ND_E_ECHO_SERVER_SIP_TCP_PACKETS_DIFFERENT_SIZE"

    invoke-direct {v0, v1}, Lcom/vivox/service/ND_ERROR;-><init>(Ljava/lang/String;)V

    sput-object v0, Lcom/vivox/service/ND_ERROR;->ND_E_ECHO_SERVER_SIP_TCP_PACKETS_DIFFERENT_SIZE:Lcom/vivox/service/ND_ERROR;

    .line 33
    new-instance v0, Lcom/vivox/service/ND_ERROR;

    const-string v1, "ND_E_ECHO_SERVER_SIP_TCP_RECV_FAILED"

    invoke-direct {v0, v1}, Lcom/vivox/service/ND_ERROR;-><init>(Ljava/lang/String;)V

    sput-object v0, Lcom/vivox/service/ND_ERROR;->ND_E_ECHO_SERVER_SIP_TCP_RECV_FAILED:Lcom/vivox/service/ND_ERROR;

    .line 34
    new-instance v0, Lcom/vivox/service/ND_ERROR;

    const-string v1, "ND_E_ECHO_SERVER_SIP_TCP_SEND_FAILED"

    invoke-direct {v0, v1}, Lcom/vivox/service/ND_ERROR;-><init>(Ljava/lang/String;)V

    sput-object v0, Lcom/vivox/service/ND_ERROR;->ND_E_ECHO_SERVER_SIP_TCP_SEND_FAILED:Lcom/vivox/service/ND_ERROR;

    .line 35
    new-instance v0, Lcom/vivox/service/ND_ERROR;

    const-string v1, "ND_E_ECHO_SERVER_SIP_UDP_DATA_DIFFERENT"

    invoke-direct {v0, v1}, Lcom/vivox/service/ND_ERROR;-><init>(Ljava/lang/String;)V

    sput-object v0, Lcom/vivox/service/ND_ERROR;->ND_E_ECHO_SERVER_SIP_UDP_DATA_DIFFERENT:Lcom/vivox/service/ND_ERROR;

    .line 36
    new-instance v0, Lcom/vivox/service/ND_ERROR;

    const-string v1, "ND_E_ECHO_SERVER_SIP_UDP_DIFFERENT_LENGTH"

    invoke-direct {v0, v1}, Lcom/vivox/service/ND_ERROR;-><init>(Ljava/lang/String;)V

    sput-object v0, Lcom/vivox/service/ND_ERROR;->ND_E_ECHO_SERVER_SIP_UDP_DIFFERENT_LENGTH:Lcom/vivox/service/ND_ERROR;

    .line 37
    new-instance v0, Lcom/vivox/service/ND_ERROR;

    const-string v1, "ND_E_ECHO_SERVER_SIP_UDP_RECV_FAILED"

    invoke-direct {v0, v1}, Lcom/vivox/service/ND_ERROR;-><init>(Ljava/lang/String;)V

    sput-object v0, Lcom/vivox/service/ND_ERROR;->ND_E_ECHO_SERVER_SIP_UDP_RECV_FAILED:Lcom/vivox/service/ND_ERROR;

    .line 38
    new-instance v0, Lcom/vivox/service/ND_ERROR;

    const-string v1, "ND_E_ECHO_SERVER_SIP_UDP_SEND_FAILED"

    invoke-direct {v0, v1}, Lcom/vivox/service/ND_ERROR;-><init>(Ljava/lang/String;)V

    sput-object v0, Lcom/vivox/service/ND_ERROR;->ND_E_ECHO_SERVER_SIP_UDP_SEND_FAILED:Lcom/vivox/service/ND_ERROR;

    .line 39
    new-instance v0, Lcom/vivox/service/ND_ERROR;

    const-string v1, "ND_E_ECHO_SERVER_TCP_SET_ASYNC_FAILED"

    invoke-direct {v0, v1}, Lcom/vivox/service/ND_ERROR;-><init>(Ljava/lang/String;)V

    sput-object v0, Lcom/vivox/service/ND_ERROR;->ND_E_ECHO_SERVER_TCP_SET_ASYNC_FAILED:Lcom/vivox/service/ND_ERROR;

    .line 40
    new-instance v0, Lcom/vivox/service/ND_ERROR;

    const-string v1, "ND_E_ECHO_SERVER_UDP_SET_ASYNC_FAILED"

    invoke-direct {v0, v1}, Lcom/vivox/service/ND_ERROR;-><init>(Ljava/lang/String;)V

    sput-object v0, Lcom/vivox/service/ND_ERROR;->ND_E_ECHO_SERVER_UDP_SET_ASYNC_FAILED:Lcom/vivox/service/ND_ERROR;

    .line 41
    new-instance v0, Lcom/vivox/service/ND_ERROR;

    const-string v1, "ND_E_NO_ERROR"

    invoke-static {}, Lcom/vivox/service/VxClientProxyJNI;->ND_E_NO_ERROR_get()I

    move-result v2

    invoke-direct {v0, v1, v2}, Lcom/vivox/service/ND_ERROR;-><init>(Ljava/lang/String;I)V

    sput-object v0, Lcom/vivox/service/ND_ERROR;->ND_E_NO_ERROR:Lcom/vivox/service/ND_ERROR;

    .line 42
    new-instance v0, Lcom/vivox/service/ND_ERROR;

    const-string v1, "ND_E_NO_INTERFACE"

    invoke-direct {v0, v1}, Lcom/vivox/service/ND_ERROR;-><init>(Ljava/lang/String;)V

    sput-object v0, Lcom/vivox/service/ND_ERROR;->ND_E_NO_INTERFACE:Lcom/vivox/service/ND_ERROR;

    .line 43
    new-instance v0, Lcom/vivox/service/ND_ERROR;

    const-string v1, "ND_E_NO_INTERFACE_WITH_GATEWAY"

    invoke-direct {v0, v1}, Lcom/vivox/service/ND_ERROR;-><init>(Ljava/lang/String;)V

    sput-object v0, Lcom/vivox/service/ND_ERROR;->ND_E_NO_INTERFACE_WITH_GATEWAY:Lcom/vivox/service/ND_ERROR;

    .line 44
    new-instance v0, Lcom/vivox/service/ND_ERROR;

    const-string v1, "ND_E_NO_INTERFACE_WITH_ROUTE"

    invoke-direct {v0, v1}, Lcom/vivox/service/ND_ERROR;-><init>(Ljava/lang/String;)V

    sput-object v0, Lcom/vivox/service/ND_ERROR;->ND_E_NO_INTERFACE_WITH_ROUTE:Lcom/vivox/service/ND_ERROR;

    .line 45
    new-instance v0, Lcom/vivox/service/ND_ERROR;

    const-string v1, "ND_E_TEST_NOT_RUN"

    invoke-direct {v0, v1}, Lcom/vivox/service/ND_ERROR;-><init>(Ljava/lang/String;)V

    sput-object v0, Lcom/vivox/service/ND_ERROR;->ND_E_TEST_NOT_RUN:Lcom/vivox/service/ND_ERROR;

    .line 46
    new-instance v0, Lcom/vivox/service/ND_ERROR;

    const-string v1, "ND_E_TIMEOUT"

    invoke-direct {v0, v1}, Lcom/vivox/service/ND_ERROR;-><init>(Ljava/lang/String;)V

    sput-object v0, Lcom/vivox/service/ND_ERROR;->ND_E_TIMEOUT:Lcom/vivox/service/ND_ERROR;

    .line 47
    sput v3, Lcom/vivox/service/ND_ERROR;->swigNext:I

    .line 48
    const/16 v0, 0x2b

    new-array v0, v0, [Lcom/vivox/service/ND_ERROR;

    sget-object v1, Lcom/vivox/service/ND_ERROR;->ND_E_NO_ERROR:Lcom/vivox/service/ND_ERROR;

    aput-object v1, v0, v3

    const/4 v1, 0x1

    sget-object v2, Lcom/vivox/service/ND_ERROR;->ND_E_TEST_NOT_RUN:Lcom/vivox/service/ND_ERROR;

    aput-object v2, v0, v1

    const/4 v1, 0x2

    sget-object v2, Lcom/vivox/service/ND_ERROR;->ND_E_NO_INTERFACE:Lcom/vivox/service/ND_ERROR;

    aput-object v2, v0, v1

    const/4 v1, 0x3

    sget-object v2, Lcom/vivox/service/ND_ERROR;->ND_E_NO_INTERFACE_WITH_GATEWAY:Lcom/vivox/service/ND_ERROR;

    aput-object v2, v0, v1

    const/4 v1, 0x4

    sget-object v2, Lcom/vivox/service/ND_ERROR;->ND_E_NO_INTERFACE_WITH_ROUTE:Lcom/vivox/service/ND_ERROR;

    aput-object v2, v0, v1

    const/4 v1, 0x5

    sget-object v2, Lcom/vivox/service/ND_ERROR;->ND_E_TIMEOUT:Lcom/vivox/service/ND_ERROR;

    aput-object v2, v0, v1

    const/4 v1, 0x6

    sget-object v2, Lcom/vivox/service/ND_ERROR;->ND_E_CANT_ICMP:Lcom/vivox/service/ND_ERROR;

    aput-object v2, v0, v1

    const/4 v1, 0x7

    sget-object v2, Lcom/vivox/service/ND_ERROR;->ND_E_CANT_RESOLVE_VIVOX_UDP_SERVER:Lcom/vivox/service/ND_ERROR;

    aput-object v2, v0, v1

    const/16 v1, 0x8

    sget-object v2, Lcom/vivox/service/ND_ERROR;->ND_E_CANT_RESOLVE_ROOT_DNS_SERVER:Lcom/vivox/service/ND_ERROR;

    aput-object v2, v0, v1

    const/16 v1, 0x9

    sget-object v2, Lcom/vivox/service/ND_ERROR;->ND_E_CANT_CONVERT_LOCAL_IP_ADDRESS:Lcom/vivox/service/ND_ERROR;

    aput-object v2, v0, v1

    const/16 v1, 0xa

    sget-object v2, Lcom/vivox/service/ND_ERROR;->ND_E_CANT_CONTACT_STUN_SERVER_ON_UDP_PORT_3478:Lcom/vivox/service/ND_ERROR;

    aput-object v2, v0, v1

    const/16 v1, 0xb

    sget-object v2, Lcom/vivox/service/ND_ERROR;->ND_E_CANT_CREATE_TCP_SOCKET:Lcom/vivox/service/ND_ERROR;

    aput-object v2, v0, v1

    const/16 v1, 0xc

    sget-object v2, Lcom/vivox/service/ND_ERROR;->ND_E_CANT_LOAD_ICMP_LIBRARY:Lcom/vivox/service/ND_ERROR;

    aput-object v2, v0, v1

    const/16 v1, 0xd

    sget-object v2, Lcom/vivox/service/ND_ERROR;->ND_E_CANT_FIND_SENDECHO2_PROCADDR:Lcom/vivox/service/ND_ERROR;

    aput-object v2, v0, v1

    const/16 v1, 0xe

    sget-object v2, Lcom/vivox/service/ND_ERROR;->ND_E_CANT_CONNECT_TO_ECHO_SERVER:Lcom/vivox/service/ND_ERROR;

    aput-object v2, v0, v1

    const/16 v1, 0xf

    sget-object v2, Lcom/vivox/service/ND_ERROR;->ND_E_ECHO_SERVER_LOGIN_SEND_FAILED:Lcom/vivox/service/ND_ERROR;

    aput-object v2, v0, v1

    const/16 v1, 0x10

    sget-object v2, Lcom/vivox/service/ND_ERROR;->ND_E_ECHO_SERVER_LOGIN_RECV_FAILED:Lcom/vivox/service/ND_ERROR;

    aput-object v2, v0, v1

    const/16 v1, 0x11

    sget-object v2, Lcom/vivox/service/ND_ERROR;->ND_E_ECHO_SERVER_LOGIN_RESPONSE_MISSING_STATUS:Lcom/vivox/service/ND_ERROR;

    aput-object v2, v0, v1

    const/16 v1, 0x12

    sget-object v2, Lcom/vivox/service/ND_ERROR;->ND_E_ECHO_SERVER_LOGIN_RESPONSE_FAILED_STATUS:Lcom/vivox/service/ND_ERROR;

    aput-object v2, v0, v1

    const/16 v1, 0x13

    sget-object v2, Lcom/vivox/service/ND_ERROR;->ND_E_ECHO_SERVER_LOGIN_RESPONSE_MISSING_SESSIONID:Lcom/vivox/service/ND_ERROR;

    aput-object v2, v0, v1

    const/16 v1, 0x14

    sget-object v2, Lcom/vivox/service/ND_ERROR;->ND_E_ECHO_SERVER_LOGIN_RESPONSE_MISSING_SIPPORT:Lcom/vivox/service/ND_ERROR;

    aput-object v2, v0, v1

    const/16 v1, 0x15

    sget-object v2, Lcom/vivox/service/ND_ERROR;->ND_E_ECHO_SERVER_LOGIN_RESPONSE_MISSING_AUDIORTP:Lcom/vivox/service/ND_ERROR;

    aput-object v2, v0, v1

    const/16 v1, 0x16

    sget-object v2, Lcom/vivox/service/ND_ERROR;->ND_E_ECHO_SERVER_LOGIN_RESPONSE_MISSING_AUDIORTCP:Lcom/vivox/service/ND_ERROR;

    aput-object v2, v0, v1

    const/16 v1, 0x17

    sget-object v2, Lcom/vivox/service/ND_ERROR;->ND_E_ECHO_SERVER_LOGIN_RESPONSE_MISSING_VIDEORTP:Lcom/vivox/service/ND_ERROR;

    aput-object v2, v0, v1

    const/16 v1, 0x18

    sget-object v2, Lcom/vivox/service/ND_ERROR;->ND_E_ECHO_SERVER_LOGIN_RESPONSE_MISSING_VIDEORTCP:Lcom/vivox/service/ND_ERROR;

    aput-object v2, v0, v1

    const/16 v1, 0x19

    sget-object v2, Lcom/vivox/service/ND_ERROR;->ND_E_ECHO_SERVER_CANT_ALLOCATE_SIP_SOCKET:Lcom/vivox/service/ND_ERROR;

    aput-object v2, v0, v1

    const/16 v1, 0x1a

    sget-object v2, Lcom/vivox/service/ND_ERROR;->ND_E_ECHO_SERVER_CANT_ALLOCATE_MEDIA_SOCKET:Lcom/vivox/service/ND_ERROR;

    aput-object v2, v0, v1

    const/16 v1, 0x1b

    sget-object v2, Lcom/vivox/service/ND_ERROR;->ND_E_ECHO_SERVER_SIP_UDP_SEND_FAILED:Lcom/vivox/service/ND_ERROR;

    aput-object v2, v0, v1

    const/16 v1, 0x1c

    sget-object v2, Lcom/vivox/service/ND_ERROR;->ND_E_ECHO_SERVER_SIP_UDP_RECV_FAILED:Lcom/vivox/service/ND_ERROR;

    aput-object v2, v0, v1

    const/16 v1, 0x1d

    sget-object v2, Lcom/vivox/service/ND_ERROR;->ND_E_ECHO_SERVER_SIP_TCP_SEND_FAILED:Lcom/vivox/service/ND_ERROR;

    aput-object v2, v0, v1

    const/16 v1, 0x1e

    sget-object v2, Lcom/vivox/service/ND_ERROR;->ND_E_ECHO_SERVER_SIP_TCP_RECV_FAILED:Lcom/vivox/service/ND_ERROR;

    aput-object v2, v0, v1

    const/16 v1, 0x1f

    sget-object v2, Lcom/vivox/service/ND_ERROR;->ND_E_ECHO_SERVER_SIP_NO_UDP_OR_TCP:Lcom/vivox/service/ND_ERROR;

    aput-object v2, v0, v1

    const/16 v1, 0x20

    sget-object v2, Lcom/vivox/service/ND_ERROR;->ND_E_ECHO_SERVER_SIP_NO_UDP:Lcom/vivox/service/ND_ERROR;

    aput-object v2, v0, v1

    const/16 v1, 0x21

    sget-object v2, Lcom/vivox/service/ND_ERROR;->ND_E_ECHO_SERVER_SIP_NO_TCP:Lcom/vivox/service/ND_ERROR;

    aput-object v2, v0, v1

    const/16 v1, 0x22

    sget-object v2, Lcom/vivox/service/ND_ERROR;->ND_E_ECHO_SERVER_SIP_MALFORMED_TCP_PACKET:Lcom/vivox/service/ND_ERROR;

    aput-object v2, v0, v1

    const/16 v1, 0x23

    sget-object v2, Lcom/vivox/service/ND_ERROR;->ND_E_ECHO_SERVER_SIP_UDP_DIFFERENT_LENGTH:Lcom/vivox/service/ND_ERROR;

    aput-object v2, v0, v1

    const/16 v1, 0x24

    sget-object v2, Lcom/vivox/service/ND_ERROR;->ND_E_ECHO_SERVER_SIP_UDP_DATA_DIFFERENT:Lcom/vivox/service/ND_ERROR;

    aput-object v2, v0, v1

    const/16 v1, 0x25

    sget-object v2, Lcom/vivox/service/ND_ERROR;->ND_E_ECHO_SERVER_SIP_TCP_PACKETS_DIFFERENT:Lcom/vivox/service/ND_ERROR;

    aput-object v2, v0, v1

    const/16 v1, 0x26

    sget-object v2, Lcom/vivox/service/ND_ERROR;->ND_E_ECHO_SERVER_SIP_TCP_PACKETS_DIFFERENT_SIZE:Lcom/vivox/service/ND_ERROR;

    aput-object v2, v0, v1

    const/16 v1, 0x27

    sget-object v2, Lcom/vivox/service/ND_ERROR;->ND_E_ECHO_SERVER_LOGIN_RECV_FAILED_TIMEOUT:Lcom/vivox/service/ND_ERROR;

    aput-object v2, v0, v1

    const/16 v1, 0x28

    sget-object v2, Lcom/vivox/service/ND_ERROR;->ND_E_ECHO_SERVER_TCP_SET_ASYNC_FAILED:Lcom/vivox/service/ND_ERROR;

    aput-object v2, v0, v1

    const/16 v1, 0x29

    sget-object v2, Lcom/vivox/service/ND_ERROR;->ND_E_ECHO_SERVER_UDP_SET_ASYNC_FAILED:Lcom/vivox/service/ND_ERROR;

    aput-object v2, v0, v1

    const/16 v1, 0x2a

    sget-object v2, Lcom/vivox/service/ND_ERROR;->ND_E_ECHO_SERVER_CANT_RESOLVE_NAME:Lcom/vivox/service/ND_ERROR;

    aput-object v2, v0, v1

    sput-object v0, Lcom/vivox/service/ND_ERROR;->swigValues:[Lcom/vivox/service/ND_ERROR;

    return-void
.end method

.method private constructor <init>(Ljava/lang/String;)V
    .locals 2
    .param p1, "str"    # Ljava/lang/String;

    .prologue
    .line 52
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 53
    iput-object p1, p0, Lcom/vivox/service/ND_ERROR;->swigName:Ljava/lang/String;

    .line 54
    sget v0, Lcom/vivox/service/ND_ERROR;->swigNext:I

    .line 55
    .local v0, "i":I
    add-int/lit8 v1, v0, 0x1

    sput v1, Lcom/vivox/service/ND_ERROR;->swigNext:I

    .line 56
    iput v0, p0, Lcom/vivox/service/ND_ERROR;->swigValue:I

    .line 57
    return-void
.end method

.method private constructor <init>(Ljava/lang/String;I)V
    .locals 1
    .param p1, "str"    # Ljava/lang/String;
    .param p2, "i"    # I

    .prologue
    .line 59
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 60
    iput-object p1, p0, Lcom/vivox/service/ND_ERROR;->swigName:Ljava/lang/String;

    .line 61
    iput p2, p0, Lcom/vivox/service/ND_ERROR;->swigValue:I

    .line 62
    add-int/lit8 v0, p2, 0x1

    sput v0, Lcom/vivox/service/ND_ERROR;->swigNext:I

    .line 63
    return-void
.end method

.method private constructor <init>(Ljava/lang/String;Lcom/vivox/service/ND_ERROR;)V
    .locals 1
    .param p1, "str"    # Ljava/lang/String;
    .param p2, "nd_error"    # Lcom/vivox/service/ND_ERROR;

    .prologue
    .line 65
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 66
    iput-object p1, p0, Lcom/vivox/service/ND_ERROR;->swigName:Ljava/lang/String;

    .line 67
    iget v0, p2, Lcom/vivox/service/ND_ERROR;->swigValue:I

    iput v0, p0, Lcom/vivox/service/ND_ERROR;->swigValue:I

    .line 68
    iget v0, p0, Lcom/vivox/service/ND_ERROR;->swigValue:I

    add-int/lit8 v0, v0, 0x1

    sput v0, Lcom/vivox/service/ND_ERROR;->swigNext:I

    .line 69
    return-void
.end method

.method public static swigToEnum(I)Lcom/vivox/service/ND_ERROR;
    .locals 4
    .param p0, "i"    # I

    .prologue
    .line 72
    sget-object v1, Lcom/vivox/service/ND_ERROR;->swigValues:[Lcom/vivox/service/ND_ERROR;

    array-length v1, v1

    if-ge p0, v1, :cond_0

    if-ltz p0, :cond_0

    sget-object v1, Lcom/vivox/service/ND_ERROR;->swigValues:[Lcom/vivox/service/ND_ERROR;

    aget-object v1, v1, p0

    iget v1, v1, Lcom/vivox/service/ND_ERROR;->swigValue:I

    if-ne v1, p0, :cond_0

    .line 73
    sget-object v1, Lcom/vivox/service/ND_ERROR;->swigValues:[Lcom/vivox/service/ND_ERROR;

    aget-object v1, v1, p0

    .line 77
    :goto_0
    return-object v1

    .line 75
    :cond_0
    const/4 v0, 0x0

    .local v0, "i2":I
    :goto_1
    sget-object v1, Lcom/vivox/service/ND_ERROR;->swigValues:[Lcom/vivox/service/ND_ERROR;

    array-length v1, v1

    if-ge v0, v1, :cond_2

    .line 76
    sget-object v1, Lcom/vivox/service/ND_ERROR;->swigValues:[Lcom/vivox/service/ND_ERROR;

    aget-object v1, v1, v0

    iget v1, v1, Lcom/vivox/service/ND_ERROR;->swigValue:I

    if-ne v1, p0, :cond_1

    .line 77
    sget-object v1, Lcom/vivox/service/ND_ERROR;->swigValues:[Lcom/vivox/service/ND_ERROR;

    aget-object v1, v1, v0

    goto :goto_0

    .line 75
    :cond_1
    add-int/lit8 v0, v0, 0x1

    goto :goto_1

    .line 80
    :cond_2
    new-instance v1, Ljava/lang/IllegalArgumentException;

    new-instance v2, Ljava/lang/StringBuilder;

    invoke-direct {v2}, Ljava/lang/StringBuilder;-><init>()V

    const-string v3, "No enum "

    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v2

    const-class v3, Lcom/vivox/service/ND_ERROR;

    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    move-result-object v2

    const-string v3, " with value "

    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v2

    invoke-virtual {v2, p0}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    move-result-object v2

    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v2

    invoke-direct {v1, v2}, Ljava/lang/IllegalArgumentException;-><init>(Ljava/lang/String;)V

    throw v1
.end method


# virtual methods
.method public final swigValue()I
    .locals 1

    .prologue
    .line 84
    iget v0, p0, Lcom/vivox/service/ND_ERROR;->swigValue:I

    return v0
.end method

.method public toString()Ljava/lang/String;
    .locals 1

    .prologue
    .line 88
    iget-object v0, p0, Lcom/vivox/service/ND_ERROR;->swigName:Ljava/lang/String;

    return-object v0
.end method
