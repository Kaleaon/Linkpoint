.class public Lcom/vivox/service/CABundleHelper;
.super Ljava/lang/Object;
.source "CABundleHelper.java"


# static fields
.field public static final TAG:Ljava/lang/String; = "vivoxsdk"


# direct methods
.method public constructor <init>()V
    .locals 0

    .prologue
    .line 9
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method

.method public static useCABundleFromAssets(Landroid/content/Context;)Ljava/lang/Boolean;
    .locals 10
    .param p0, "context"    # Landroid/content/Context;

    .prologue
    const/4 v9, 0x0

    .line 13
    const/4 v6, 0x0

    .line 14
    .local v6, "z":Z
    invoke-virtual {p0}, Landroid/content/Context;->getCacheDir()Ljava/io/File;

    move-result-object v7

    invoke-virtual {v7}, Ljava/io/File;->getAbsolutePath()Ljava/lang/String;

    move-result-object v0

    .line 16
    .local v0, "absolutePath":Ljava/lang/String;
    :try_start_0
    invoke-virtual {p0}, Landroid/content/Context;->getAssets()Landroid/content/res/AssetManager;

    move-result-object v7

    const-string v8, "ca-bundle.crt"

    invoke-virtual {v7, v8}, Landroid/content/res/AssetManager;->open(Ljava/lang/String;)Ljava/io/InputStream;

    move-result-object v4

    .line 17
    .local v4, "open":Ljava/io/InputStream;
    new-instance v3, Ljava/io/FileOutputStream;

    new-instance v7, Ljava/lang/StringBuilder;

    invoke-direct {v7}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {v7, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v7

    const-string v8, "/ca-bundle.crt"

    invoke-virtual {v7, v8}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v7

    invoke-virtual {v7}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v7

    invoke-direct {v3, v7}, Ljava/io/FileOutputStream;-><init>(Ljava/lang/String;)V

    .line 18
    .local v3, "fileOutputStream":Ljava/io/OutputStream;
    const/16 v7, 0x400

    new-array v1, v7, [B

    .line 20
    .local v1, "bArr":[B
    :goto_0
    invoke-virtual {v4, v1}, Ljava/io/InputStream;->read([B)I

    move-result v5

    .line 21
    .local v5, "read":I
    if-gtz v5, :cond_1

    .line 26
    invoke-virtual {v4}, Ljava/io/InputStream;->close()V

    .line 27
    invoke-virtual {v3}, Ljava/io/OutputStream;->close()V

    .line 28
    invoke-static {v0}, Lcom/vivox/service/VxClientProxy;->vx_set_cert_data_dir(Ljava/lang/String;)I

    move-result v7

    if-nez v7, :cond_0

    .line 29
    const/4 v6, 0x1

    .line 31
    :cond_0
    invoke-static {v6}, Ljava/lang/Boolean;->valueOf(Z)Ljava/lang/Boolean;

    move-result-object v7

    .line 34
    .end local v1    # "bArr":[B
    .end local v3    # "fileOutputStream":Ljava/io/OutputStream;
    .end local v4    # "open":Ljava/io/InputStream;
    .end local v5    # "read":I
    :goto_1
    return-object v7

    .line 24
    .restart local v1    # "bArr":[B
    .restart local v3    # "fileOutputStream":Ljava/io/OutputStream;
    .restart local v4    # "open":Ljava/io/InputStream;
    .restart local v5    # "read":I
    :cond_1
    const/4 v7, 0x0

    invoke-virtual {v3, v1, v7, v5}, Ljava/io/OutputStream;->write([BII)V
    :try_end_0
    .catch Ljava/lang/Throwable; {:try_start_0 .. :try_end_0} :catch_0

    goto :goto_0

    .line 32
    .end local v1    # "bArr":[B
    .end local v3    # "fileOutputStream":Ljava/io/OutputStream;
    .end local v4    # "open":Ljava/io/InputStream;
    .end local v5    # "read":I
    :catch_0
    move-exception v2

    .line 33
    .local v2, "e":Ljava/lang/Throwable;
    const-string v7, "vivoxsdk"

    const-string v8, "caught exception"

    invoke-static {v7, v8, v2}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I

    .line 34
    invoke-static {v9}, Ljava/lang/Boolean;->valueOf(Z)Ljava/lang/Boolean;

    move-result-object v7

    goto :goto_1
.end method
