package com.lumiyaviewer.lumiya.ui.inventory;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import com.google.common.net.HttpHeaders;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.openjpeg.OpenJPEG;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.caps.SLCaps;
import com.lumiyaviewer.lumiya.slproto.https.LLSDXMLRequest;
import com.lumiyaviewer.lumiya.slproto.https.SLHTTPSConnection;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDException;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDMap;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDString;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDUUID;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import javax.annotation.Nullable;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/* loaded from: classes.dex */
public class UploadImageAsyncTask extends AsyncTask<UploadImageParams, Void, UploadImageResult> {
    private static final MediaType MEDIA_TYPE_JP2 = MediaType.parse("image/jp2");
    private final UUID agentUUID;
    private final Context context;
    private ProgressDialog progressDialog = null;

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public static class UploadImageResult {

        @Nullable
        public final String errorMessage;
        public final boolean success;

        private UploadImageResult(boolean z, @Nullable String str) {
            this.success = z;
            this.errorMessage = str;
        }

        /* synthetic */ UploadImageResult(boolean z, String str, UploadImageResult uploadImageResult) {
            this(z, str);
        }
    }

    public UploadImageAsyncTask(Context context, UUID uuid) {
        this.context = context;
        this.agentUUID = uuid;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.os.AsyncTask
    public UploadImageResult doInBackground(UploadImageParams... uploadImageParamsArr) {
        Bitmap createScaledBitmap;
        boolean z;
        String str;
        boolean z2 = true;
        String str2 = null;
        int length = uploadImageParamsArr.length;
        int i = 0;
        while (i < length) {
            UploadImageParams uploadImageParams = uploadImageParamsArr[i];
            Bitmap bitmap = uploadImageParams.bitmap;
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int highestOneBit = Integer.highestOneBit(width);
            int highestOneBit2 = Integer.highestOneBit(height);
            if (highestOneBit != width) {
                highestOneBit *= 2;
            }
            if (highestOneBit2 != height) {
                highestOneBit2 *= 2;
            }
            while (true) {
                if (highestOneBit <= 1024 && highestOneBit2 <= 1024) {
                    break;
                }
                highestOneBit /= 2;
                highestOneBit2 /= 2;
            }
            if (highestOneBit == bitmap.getWidth() && highestOneBit2 == bitmap.getHeight()) {
                createScaledBitmap = bitmap;
            } else {
                Debug.Printf("UploadImage: scaled bitmap from %d x %d to %d x %d", Integer.valueOf(bitmap.getWidth()), Integer.valueOf(bitmap.getHeight()), Integer.valueOf(highestOneBit), Integer.valueOf(highestOneBit2));
                createScaledBitmap = Bitmap.createScaledBitmap(bitmap, highestOneBit, highestOneBit2, true);
            }
            int width2 = createScaledBitmap.getWidth();
            int height2 = createScaledBitmap.getHeight();
            int i2 = createScaledBitmap.hasAlpha() ? 4 : 3;
            OpenJPEG openJPEG = new OpenJPEG(width2, height2, i2, i2, 0, 0);
            int[] iArr = new int[width2];
            for (int i3 = 0; i3 < height2; i3++) {
                createScaledBitmap.getPixels(iArr, 0, width2, 0, i3, width2, 1);
                openJPEG.putPixelRow((height2 - 1) - i3, iArr, width2);
            }
            try {
                File createTempFile = File.createTempFile("uploadtex", "j2k", this.context.getCacheDir());
                openJPEG.SaveJPEG2K(createTempFile);
                UserManager userManager = UserManager.getUserManager(uploadImageParams.agentUUID);
                SLAgentCircuit activeAgentCircuit = userManager != null ? userManager.getActiveAgentCircuit() : null;
                if (activeAgentCircuit != null) {
                    String capability = activeAgentCircuit.getCaps().getCapability(SLCaps.SLCapability.NewFileAgentInventory);
                    if (capability != null) {
                        LLSDNode PerformRequest = new LLSDXMLRequest().PerformRequest(capability, new LLSDMap(new LLSDMap.LLSDMapEntry("asset_type", new LLSDString("texture")), new LLSDMap.LLSDMapEntry("description", new LLSDString("(No description)")), new LLSDMap.LLSDMapEntry("folder_id", new LLSDUUID(uploadImageParams.folderID)), new LLSDMap.LLSDMapEntry("inventory_type", new LLSDString("texture")), new LLSDMap.LLSDMapEntry("name", new LLSDString(uploadImageParams.name))));
                        if (PerformRequest == null) {
                            throw new IOException("Upload request refused");
                        }
                        Response execute = SLHTTPSConnection.getOkHttpClient().newCall(new Request.Builder().url(PerformRequest.byKey("uploader").asString()).header(HttpHeaders.ACCEPT, "application/llsd+xml").post(RequestBody.create(MEDIA_TYPE_JP2, createTempFile)).build()).execute();
                        if (execute == null) {
                            throw new IOException("Null response");
                        }
                        try {
                            if (!execute.isSuccessful()) {
                                throw new IOException("Invalid HTTP response");
                                break;
                            }
                            LLSDNode parseXML = LLSDNode.parseXML(execute.body().byteStream(), null);
                            Debug.Log("upload reply: " + parseXML.serializeToXML());
                            try {
                                try {
                                    if (parseXML.keyExists("error")) {
                                        LLSDNode byKey = parseXML.byKey("error");
                                        if (!byKey.keyExists("message")) {
                                            str = str2;
                                            z = z2;
                                        } else if (byKey.keyExists("success") && !byKey.byKey("success").asBoolean()) {
                                            String asString = byKey.byKey("message").asString();
                                            z = false;
                                            str = asString;
                                        }
                                        userManager.getInventoryManager().requestFolderUpdate(uploadImageParams.folderID);
                                        execute.close();
                                        str2 = str;
                                    }
                                    execute.close();
                                    str2 = str;
                                } catch (LLSDException e) {
                                    e = e;
                                    str2 = str;
                                    Debug.Warning(e);
                                    z = false;
                                    i++;
                                    z2 = z;
                                } catch (IOException e2) {
                                    e = e2;
                                    str2 = str;
                                    Debug.Warning(e);
                                    z = false;
                                    i++;
                                    z2 = z;
                                }
                                userManager.getInventoryManager().requestFolderUpdate(uploadImageParams.folderID);
                            } catch (Throwable th) {
                                th = th;
                                execute.close();
                                throw th;
                                break;
                            }
                            str = str2;
                            z = z2;
                        } catch (Throwable th2) {
                            th = th2;
                            str = str2;
                        }
                    } else {
                        z = false;
                    }
                } else {
                    z = false;
                }
                createTempFile.delete();
            } catch (LLSDException e3) {
                e = e3;
            } catch (IOException e4) {
                e = e4;
            }
            i++;
            z2 = z;
        }
        return new UploadImageResult(z2, str2, null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.os.AsyncTask
    public void onPostExecute(UploadImageResult uploadImageResult) {
        UUID findSpecialFolder;
        super.onPostExecute((UploadImageAsyncTask) uploadImageResult);
        if (this.progressDialog != null) {
            this.progressDialog.cancel();
            this.progressDialog = null;
        }
        if (!(uploadImageResult != null ? uploadImageResult.success : false)) {
            String str = uploadImageResult != null ? uploadImageResult.errorMessage : null;
            AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
            if (str == null) {
                str = this.context.getString(R.string.failed_to_upload_picture);
            }
            builder.setMessage(str).setCancelable(true).setNegativeButton("Dismiss", new DialogInterface.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$zdXuDSYysFkzF70MB3S4I5y4LlM
                private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }

                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    $m$0(dialogInterface, i);
                }
            }).create().show();
            return;
        }
        UserManager userManager = UserManager.getUserManager(this.agentUUID);
        SLAgentCircuit activeAgentCircuit = userManager != null ? userManager.getActiveAgentCircuit() : null;
        if (activeAgentCircuit == null || (findSpecialFolder = activeAgentCircuit.getModules().inventory.findSpecialFolder(0)) == null) {
            return;
        }
        userManager.getInventoryManager().requestFolderUpdate(findSpecialFolder);
    }

    @Override // android.os.AsyncTask
    protected void onPreExecute() {
        super.onPreExecute();
        this.progressDialog = new ProgressDialog(this.context);
        this.progressDialog.setMessage(this.context.getString(R.string.uploading_picture));
        this.progressDialog.setIndeterminate(true);
        this.progressDialog.show();
    }
}
