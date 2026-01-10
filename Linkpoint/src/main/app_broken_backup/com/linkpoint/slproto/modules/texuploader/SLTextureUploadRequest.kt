package com.linkpoint.slproto.modules.texuploader

import com.google.common.net.HttpHeaders
import com.linkpoint.Debug
import com.linkpoint.slproto.https.LLSDXMLRequest
import com.linkpoint.slproto.https.SLHTTPSConnection
import com.linkpoint.slproto.llsd.LLSDException
import com.linkpoint.slproto.llsd.LLSDNode
import com.linkpoint.slproto.llsd.types.LLSDUndefined
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
        fun OnTextureUploadComplete(SLTextureUploadRequest sLTextureUploadRequest)
    }

    SLTextureUploadRequest(File file, Int i) {
        this.sourceFile = file
        this.textureLayer = i
    }

    fun getTextureID(): UUID {
        return this.textureID
    }

    fun run()  {
        Response execute
        try {
            var asString: String = LLSDXMLRequest().PerformRequest(this.capURL, LLSDUndefined()).byKey("uploader").asString()
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

    fun setCapURL(String str)  {
        this.capURL = str
    }

    fun setOnUploadComplete(TextureUploadCompleteListener textureUploadCompleteListener)  {
        this.onUploadComplete = textureUploadCompleteListener
    }
}
