// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.modules.texuploader;

import okhttp3.Response;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDException;
import java.io.IOException;
import okhttp3.RequestBody;
import okhttp3.Request;
import com.lumiyaviewer.lumiya.slproto.https.SLHTTPSConnection;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDUndefined;
import com.lumiyaviewer.lumiya.slproto.https.LLSDXMLRequest;
import java.util.UUID;
import java.io.File;
import okhttp3.MediaType;

public class SLTextureUploadRequest implements Runnable
{
    private static final MediaType MEDIA_TYPE_JP2;
    private String capURL;
    TextureUploadCompleteListener onUploadComplete;
    private File sourceFile;
    private UUID textureID;
    private int textureLayer;
    
    static {
        MEDIA_TYPE_JP2 = MediaType.parse("image/x-j2c");
    }
    
    public SLTextureUploadRequest(final File sourceFile, final int textureLayer) {
        this.onUploadComplete = null;
        this.sourceFile = sourceFile;
        this.textureLayer = textureLayer;
    }
    
    public UUID getTextureID() {
        return this.textureID;
    }
    
    @Override
    public void run() {
        Response execute = null;
        try {
            final String string = new LLSDXMLRequest().PerformRequest(this.capURL, new LLSDUndefined()).byKey("uploader").asString();
            Debug.Log("TextureUploader: uploader URL = " + string);
            execute = SLHTTPSConnection.getOkHttpClient().newCall(new Request.Builder().url(string).header("Accept", "application/llsd+xml").post(RequestBody.create(SLTextureUploadRequest.MEDIA_TYPE_JP2, this.sourceFile)).build()).execute();
            if (execute == null) {
                throw new IOException("Null response");
            }
        }
        catch (final IOException ex) {
            Debug.Warning(ex);
        }
        catch (final LLSDException ex2) {
            Debug.Warning(ex2);
            goto Label_0129;
        }
        try {
            if (!execute.isSuccessful()) {
                throw new IOException("Error code " + execute.code());
            }
        }
        finally {}
        final LLSDNode xml = LLSDNode.parseXML(execute.body().byteStream(), null);
        Debug.Log("TextureUploader: LLSD response = " + xml.serializeToXML());
        this.textureID = xml.byKey("new_asset").asUUID();
        execute.close();
        goto Label_0129;
    }
    
    public void setCapURL(final String capURL) {
        this.capURL = capURL;
    }
    
    public void setOnUploadComplete(final TextureUploadCompleteListener onUploadComplete) {
        this.onUploadComplete = onUploadComplete;
    }
    
    public interface TextureUploadCompleteListener
    {
        void OnTextureUploadComplete(final SLTextureUploadRequest p0);
    }
}
