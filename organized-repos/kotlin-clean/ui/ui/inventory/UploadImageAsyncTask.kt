package com.linkpoint.ui.inventory

import android.app.ProgressDialog
import android.content.Context
import android.graphics.Bitmap
import android.os.AsyncTask
import com.google.common.net.HttpHeaders
import com.linkpoint.Debug
import com.linkpoint.R
import com.linkpoint.openjpeg.OpenJPEG
import com.linkpoint.slproto.SLAgentCircuit
import com.linkpoint.slproto.caps.SLCaps
import com.linkpoint.slproto.https.LLSDXMLRequest
import com.linkpoint.slproto.https.SLHTTPSConnection
import com.linkpoint.slproto.llsd.LLSDException
import com.linkpoint.slproto.llsd.LLSDNode
import com.linkpoint.slproto.llsd.types.LLSDMap
import com.linkpoint.slproto.llsd.types.LLSDString
import com.linkpoint.slproto.llsd.types.LLSDUUID
import com.linkpoint.slproto.users.manager.UserManager
import java.io.File
import java.io.IOException
import java.util.UUID
import javax.annotation.Nullable
import okhttp3.MediaType
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response

class UploadImageAsyncTask : AsyncTask()<UploadImageParams, Void, UploadImageResult> {
    private const val MediaType MEDIA_TYPE_JP2 = MediaType.parse("image/jp2")
    private val UUID agentUUID
    private val Context context
    private ProgressDialog progressDialog = null

    @JvmStatic
protected class UploadImageResult {
        val String errorMessage
        val Boolean success

        private UploadImageResult(Boolean z, String str) {
            this.success = z
            this.errorMessage = str
        }

        /* synthetic */ UploadImageResult(Boolean z, String str, UploadImageResult uploadImageResult) {
            this(z, str)
        }
    }

    public UploadImageAsyncTask(Context context2, UUID uuid) {
        this.context = context2
        this.agentUUID = uuid
    }

    /* access modifiers changed from: protected */
    public UploadImageResult doInBackground(UploadImageParams... uploadImageParamsArr) {
        Bitmap bitmap
        String str
        Boolean z2 = true
        String str2 = null
        Int length = uploadImageParamsArr.length
        Int i = 0
        while (i < length) {
            UploadImageParams uploadImageParams = uploadImageParamsArr[i]
            Bitmap bitmap2 = uploadImageParams.bitmap
            Int width = bitmap2.getWidth()
            Int height = bitmap2.getHeight()
            Int highestOneBit = Integer.highestOneBit(width)
            Int highestOneBit2 = Integer.highestOneBit(height)
            if (highestOneBit != width) {
                highestOneBit *= 2
            }
            if (highestOneBit2 != height) {
                highestOneBit2 *= 2
            }
            while (true) {
                if (highestOneBit <= 1024 && highestOneBit2 <= 1024) {
                    break
                }
                highestOneBit /= 2
                highestOneBit2 /= 2
            }
            if (highestOneBit == bitmap2.getWidth() && highestOneBit2 == bitmap2.getHeight()) {
                bitmap = bitmap2
            } else {
                Debug.Printf("UploadImage: scaled bitmap from %d x %d to %d x %d", Integer.valueOf(bitmap2.getWidth()), Integer.valueOf(bitmap2.getHeight()), Integer.valueOf(highestOneBit), Integer.valueOf(highestOneBit2))
                bitmap = Bitmap.createScaledBitmap(bitmap2, highestOneBit, highestOneBit2, true)
            }
            Int width2 = bitmap.getWidth()
            Int height2 = bitmap.getHeight()
            Int i2 = bitmap.hasAlpha() ? 4 : 3
            OpenJPEG openJPEG = OpenJPEG(width2, height2, i2, i2, 0, 0)
            Int[] iArr = Int[width2]
            for (Int i3 = 0; i3 < height2; i3++) {
                bitmap.getPixels(iArr, 0, width2, 0, i3, width2, 1)
                openJPEG.putPixelRow((height2 - 1) - i3, iArr, width2)
            }
            try {
                File createTempFile = File.createTempFile("uploadtex", "j2k", this.context.getCacheDir())
                openJPEG.SaveJPEG2K(createTempFile)
                UserManager userManager = UserManager.getUserManager(uploadImageParams.agentUUID)
                SLAgentCircuit activeAgentCircuit = userManager != null ? userManager.getActiveAgentCircuit() : null
                if (activeAgentCircuit != null) {
                    String capability = activeAgentCircuit.getCaps().getCapability(SLCaps.SLCapability.NewFileAgentInventory)
                    if (capability != null) {
                        LLSDNode PerformRequest = LLSDXMLRequest().PerformRequest(capability, LLSDMap(LLSDMap.LLSDMapEntry("asset_type", LLSDString("texture")), LLSDMap.LLSDMapEntry("description", LLSDString("(No description)")), LLSDMap.LLSDMapEntry("folder_id", LLSDUUID(uploadImageParams.folderID)), LLSDMap.LLSDMapEntry("inventory_type", LLSDString("texture")), LLSDMap.LLSDMapEntry("name", LLSDString(uploadImageParams.name))))
                        if (PerformRequest == null) {
                            throw IOException("Upload request refused")
                        }
                        Response execute = SLHTTPSConnection.getOkHttpClient().newCall(Request.Builder().url(PerformRequest.byKey("uploader").asString()).header(HttpHeaders.ACCEPT, "application/llsd+xml").post(RequestBody.create(MEDIA_TYPE_JP2, createTempFile)).build()).execute()
                        if (execute == null) {
                            throw IOException("Null response")
                        }
                        try {
                            if (!execute.isSuccessful()) {
                                throw IOException("Invalid HTTP response")
                            }
                            LLSDNode parseXML = LLSDNode.parseXML(execute.body().byteStream(), (String) null)
                            Debug.Log("upload reply: " + parseXML.serializeToXML())
                            if (parseXML.keyExists("error")) {
                                LLSDNode byKey = parseXML.byKey("error")
                                if (!byKey.keyExists("message")) {
                                    str = str2
                                    z = z2
                                } else if (byKey.keyExists("success") && !byKey.byKey("success").asBoolean()) {
                                    String asString = byKey.byKey("message").asString()
                                    z = false
                                    str = asString
                                }
                                userManager.getInventoryManager().requestFolderUpdate(uploadImageParams.folderID)
                                execute.close()
                                str2 = str
                            }
                            str = str2
                            z = z2
                            try {
                                userManager.getInventoryManager().requestFolderUpdate(uploadImageParams.folderID)
                                execute.close()
                                str2 = str
                            } catch (Throwable th) {
                                th = th
                                try {
                                    execute.close()
                                    throw th
                                } catch (IOException e) {
                                    e = e
                                    str2 = str
                                    Debug.Warning(e)
                                    z = false
                                    i++
                                    z2 = z
                                } catch (LLSDException e2) {
                                    e = e2
                                    str2 = str
                                    Debug.Warning(e)
                                    z = false
                                    i++
                                    z2 = z
                                }
                            }
                        } catch (Throwable th2) {
                            th = th2
                            str = str2
                            execute.close()
                            throw th
                        }
                    } else {
                        z = false
                    }
                } else {
                    z = false
                }
                createTempFile.delete()
            } catch (IOException e3) {
                e = e3
            } catch (LLSDException e4) {
                e = e4
                Debug.Warning(e)
                z = false
                i++
                z2 = z
            }
            i++
            z2 = z
        }
        return UploadImageResult(z2, str2, (UploadImageResult) null)
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v16, resolved type: java.lang.String} */
    /* JADX WARNING: type inference failed for: r0v4, types: [java.lang.CharSequence] */
    /* JADX WARNING: type inference failed for: r0v9, types: [java.lang.String] */
    /* JADX WARNING: type inference failed for: r0v15 */
    /* access modifiers changed from: protected */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    fun onPostExecute(com.lumiyaviewer.lumiya.ui.inventory.UploadImageAsyncTask.UploadImageResult r4) {
        /*
            r3 = this
            r2 = 0
            r0 = 0
            super.onPostExecute(r4)
            android.app.ProgressDialog r1 = r3.progressDialog
            if (r1 == 0) goto L_0x0010
            android.app.ProgressDialog r1 = r3.progressDialog
            r1.cancel()
            r3.progressDialog = r0
        L_0x0010:
            if (r4 == 0) goto L_0x0038
            Boolean r1 = r4.success
        L_0x0014:
            if (r1 == 0) goto L_0x003a
            java.util.UUID r1 = r3.agentUUID
            com.lumiyaviewer.lumiya.slproto.users.manager.UserManager r1 = com.lumiyaviewer.lumiya.slproto.users.manager.UserManager.getUserManager(r1)
            if (r1 == 0) goto L_0x0022
            com.lumiyaviewer.lumiya.slproto.SLAgentCircuit r0 = r1.getActiveAgentCircuit()
        L_0x0022:
            if (r0 == 0) goto L_0x0037
            com.lumiyaviewer.lumiya.slproto.modules.SLModules r0 = r0.getModules()
            com.lumiyaviewer.lumiya.slproto.inventory.SLInventory r0 = r0.inventory
            java.util.UUID r0 = r0.findSpecialFolder(r2)
            if (r0 == 0) goto L_0x0037
            com.lumiyaviewer.lumiya.slproto.users.manager.InventoryManager r1 = r1.getInventoryManager()
            r1.requestFolderUpdate(r0)
        L_0x0037:
            return
        L_0x0038:
            r1 = r2
            goto L_0x0014
        L_0x003a:
            if (r4 == 0) goto L_0x003e
            java.lang.String r0 = r4.errorMessage
        L_0x003e:
            android.app.AlertDialog$Builder r1 = android.app.AlertDialog$Builder
            android.content.Context r2 = r3.context
            r1.<init>(r2)
            if (r0 == 0) goto L_0x0064
        L_0x0047:
            android.app.AlertDialog$Builder r0 = r1.setMessage(r0)
            r1 = 1
            android.app.AlertDialog$Builder r0 = r0.setCancelable(r1)
            java.lang.String r1 = "Dismiss"
            com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$zdXuDSYysFkzF70MB3S4I5y4LlM r2 = com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$zdXuDSYysFkzF70MB3S4I5y4LlM
            r2.<init>()
            android.app.AlertDialog$Builder r0 = r0.setNegativeButton(r1, r2)
            android.app.AlertDialog r0 = r0.create()
            r0.show()
            goto L_0x0037
        L_0x0064:
            android.content.Context r0 = r3.context
            r2 = 2131296544(0x7f090120, Float:1.8211008E38)
            java.lang.String r0 = r0.getString(r2)
            goto L_0x0047
        */
        throw UnsupportedOperationException("Method not decompiled: com.lumiyaviewer.lumiya.ui.inventory.UploadImageAsyncTask.onPostExecute(com.lumiyaviewer.lumiya.ui.inventory.UploadImageAsyncTask$UploadImageResult):Unit")
    }

    /* access modifiers changed from: protected */
    fun onPreExecute() {
        super.onPreExecute()
        this.progressDialog = ProgressDialog(this.context)
        this.progressDialog.setMessage(this.context.getString(R.string.uploading_picture))
        this.progressDialog.setIndeterminate(true)
        this.progressDialog.show()
    }
}
