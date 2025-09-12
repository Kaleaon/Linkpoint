// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.modules.texuploader;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.https.LLSDXMLRequest;
import com.lumiyaviewer.lumiya.slproto.https.SLHTTPSConnection;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDException;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDUndefined;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class SLTextureUploadRequest
    implements Runnable
{
    public static interface TextureUploadCompleteListener
    {

        public abstract void OnTextureUploadComplete(SLTextureUploadRequest sltextureuploadrequest);
    }


    private static final MediaType MEDIA_TYPE_JP2 = MediaType.parse("image/x-j2c");
    private String capURL;
    TextureUploadCompleteListener onUploadComplete;
    private File sourceFile;
    private UUID textureID;
    private int textureLayer;

    public SLTextureUploadRequest(File file, int i)
    {
        onUploadComplete = null;
        sourceFile = file;
        textureLayer = i;
    }

    public UUID getTextureID()
    {
        return textureID;
    }

    public void run()
    {
        Object obj;
        obj = new LLSDXMLRequest();
        LLSDUndefined llsdundefined = new LLSDUndefined();
        obj = ((LLSDXMLRequest) (obj)).PerformRequest(capURL, llsdundefined).byKey("uploader").asString();
        Debug.Log((new StringBuilder()).append("TextureUploader: uploader URL = ").append(((String) (obj))).toString());
        obj = (new okhttp3.Request.Builder()).url(((String) (obj))).header("Accept", "application/llsd+xml").post(RequestBody.create(MEDIA_TYPE_JP2, sourceFile)).build();
        obj = SLHTTPSConnection.getOkHttpClient().newCall(((okhttp3.Request) (obj))).execute();
        if (obj != null) goto _L2; else goto _L1
_L1:
        Exception exception;
        try
        {
            throw new IOException("Null response");
        }
        // Misplaced declaration of an exception variable
        catch (Object obj)
        {
            Debug.Warning(((Throwable) (obj)));
        }
        // Misplaced declaration of an exception variable
        catch (Object obj)
        {
            Debug.Warning(((Throwable) (obj)));
        }
_L3:
        if (onUploadComplete != null)
        {
            onUploadComplete.OnTextureUploadComplete(this);
        }
        return;
_L2:
        if (!((Response) (obj)).isSuccessful())
        {
            throw new IOException((new StringBuilder()).append("Error code ").append(((Response) (obj)).code()).toString());
        }
        break MISSING_BLOCK_LABEL_193;
        exception;
        ((Response) (obj)).close();
        throw exception;
        LLSDNode llsdnode = LLSDNode.parseXML(((Response) (obj)).body().byteStream(), null);
        Debug.Log((new StringBuilder()).append("TextureUploader: LLSD response = ").append(llsdnode.serializeToXML()).toString());
        textureID = llsdnode.byKey("new_asset").asUUID();
        ((Response) (obj)).close();
          goto _L3
    }

    public void setCapURL(String s)
    {
        capURL = s;
    }

    public void setOnUploadComplete(TextureUploadCompleteListener textureuploadcompletelistener)
    {
        onUploadComplete = textureuploadcompletelistener;
    }

}
