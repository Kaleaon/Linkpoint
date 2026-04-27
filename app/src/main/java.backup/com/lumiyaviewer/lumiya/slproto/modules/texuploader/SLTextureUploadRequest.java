package com.lumiyaviewer.lumiya.slproto.modules.texuploader;

import com.google.common.net.HttpHeaders;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.https.LLSDXMLRequest;
import com.lumiyaviewer.lumiya.slproto.https.SLHTTPSConnection;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDException;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDUndefined;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SLTextureUploadRequest implements Runnable {
    private static final MediaType MEDIA_TYPE_JP2 = MediaType.parse("image/x-j2c");
    private String capURL;
    TextureUploadCompleteListener onUploadComplete = null;
    private File sourceFile;
    private UUID textureID;
    private int textureLayer;

    public interface TextureUploadCompleteListener {
        void OnTextureUploadComplete(SLTextureUploadRequest sLTextureUploadRequest);
    }

    public SLTextureUploadRequest(File file, int i) {
        this.sourceFile = file;
        this.textureLayer = i;
    }

    public UUID getTextureID() {
        return this.textureID;
    }

    public void run() {
        Response execute;
        try {
            String asString = new LLSDXMLRequest().PerformRequest(this.capURL, new LLSDUndefined()).byKey("uploader").asString();
            Debug.Log("TextureUploader: uploader URL = " + asString);
            execute = SLHTTPSConnection.getOkHttpClient().newCall(new Request.Builder().url(asString).header(HttpHeaders.ACCEPT, "application/llsd+xml").post(RequestBody.create(MEDIA_TYPE_JP2, this.sourceFile)).build()).execute();
            if (execute == null) {
                throw new IOException("Null response");
            } else if (!execute.isSuccessful()) {
                throw new IOException("Error code " + execute.code());
            } else {
                LLSDNode parseXML = LLSDNode.parseXML(execute.body().byteStream(), (String) null);
                Debug.Log("TextureUploader: LLSD response = " + parseXML.serializeToXML());
                this.textureID = parseXML.byKey("new_asset").asUUID();
                execute.close();
                if (this.onUploadComplete != null) {
                    this.onUploadComplete.OnTextureUploadComplete(this);
                }
            }
        } catch (IOException e) {
            Debug.Warning(e);
        } catch (LLSDException e2) {
            Debug.Warning(e2);
        } catch (Throwable th) {
            execute.close();
            throw th;
        }
    }

    public void setCapURL(String str) {
        this.capURL = str;
    }

    public void setOnUploadComplete(TextureUploadCompleteListener textureUploadCompleteListener) {
        this.onUploadComplete = textureUploadCompleteListener;
    }
}
