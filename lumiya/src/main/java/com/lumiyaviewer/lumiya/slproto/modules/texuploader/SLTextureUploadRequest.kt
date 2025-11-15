package com.lumiyaviewer.lumiya.slproto.modules.texuploader

import com.google.common.net.HttpHeaders
import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.slproto.https.LLSDXMLRequest
import com.lumiyaviewer.lumiya.slproto.https.SLHTTPSConnection
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDException
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDUndefined
import java.io.File
import java.io.IOException
import java.util.UUID
import okhttp3.MediaType
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response

class SLTextureUploadRequest : Runnable {
    private MediaType MEDIA_TYPE_JP2 = MediaType.parse("image/x-j2c")
    private String capURL
    TextureUploadCompleteListener onUploadComplete = null
    private File sourceFile
    private UUID textureID
    private Int textureLayer

    interface TextureUploadCompleteListener {
        Unit OnTextureUploadComplete(SLTextureUploadRequest sLTextureUploadRequest)
    }

    SLTextureUploadRequest(File file, Int i) {
        this.sourceFile = file
        this.textureLayer = i
    }

    UUID getTextureID() {
        return this.textureID
    }

    Unit run() {
        Response execute
        try {
            String asString = LLSDXMLRequest().PerformRequest(this.capURL, LLSDUndefined()).byKey("uploader").asString()
            Debug.Log("TextureUploader: uploader URL = " + asString)
            execute = SLHTTPSConnection.getOkHttpClient().newCall(Request.Builder().url(asString).header(HttpHeaders.ACCEPT, "application/llsd+xml").post(RequestBody.create(MEDIA_TYPE_JP2, this.sourceFile)).build()).execute()
            if (execute == null) {
                throw IOException("Null response")
            } else if (!execute.isSuccessful()) {
                throw IOException("Error code " + execute.code())
            } else {
                LLSDNode parseXML = LLSDNode.parseXML(execute.body().byteStream(), (String) null)
                Debug.Log("TextureUploader: LLSD response = " + parseXML.serializeToXML())
                this.textureID = parseXML.byKey("new_asset").asUUID()
                execute.close()
                if (this.onUploadComplete != null) {
                    this.onUploadComplete.OnTextureUploadComplete(this)
                }
            }
        } catch (IOException e) {
            Debug.Warning(e)
        } catch (LLSDException e2) {
            Debug.Warning(e2)
        } catch (Throwable th) {
            execute.close()
            throw th
        }
    }

    Unit setCapURL(String str) {
        this.capURL = str
    }

    Unit setOnUploadComplete(TextureUploadCompleteListener textureUploadCompleteListener) {
        this.onUploadComplete = textureUploadCompleteListener
    }
}
