// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.inventory;

import okhttp3.Response;
import javax.annotation.Nullable;
import android.content.DialogInterface$OnClickListener;
import android.app.AlertDialog$Builder;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDException;
import java.io.IOException;
import com.lumiyaviewer.lumiya.slproto.https.LLSDXMLRequest;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDUUID;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDMap;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDString;
import com.lumiyaviewer.lumiya.slproto.caps.SLCaps;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import java.io.File;
import com.lumiyaviewer.lumiya.openjpeg.OpenJPEG;
import android.graphics.Bitmap;
import com.lumiyaviewer.lumiya.Debug;
import android.content.DialogInterface;
import android.app.ProgressDialog;
import android.content.Context;
import java.util.UUID;
import okhttp3.MediaType;
import android.os.AsyncTask;

public class UploadImageAsyncTask extends AsyncTask<UploadImageParams, Void, UploadImageResult>
{
    private static final MediaType MEDIA_TYPE_JP2;
    private final UUID agentUUID;
    private final Context context;
    private ProgressDialog progressDialog;
    
    static {
        MEDIA_TYPE_JP2 = MediaType.parse("image/jp2");
    }
    
    public UploadImageAsyncTask(final Context context, final UUID agentUUID) {
        this.progressDialog = null;
        this.context = context;
        this.agentUUID = agentUUID;
    }
    
    protected UploadImageResult doInBackground(final UploadImageParams... array) {
        boolean b = true;
        String s = null;
        final int length = array.length;
        final int n = 0;
    Label_1005:
        while (true) {
            Label_1198: {
                if (n < length) {
                    final UploadImageParams uploadImageParams = array[n];
                    Bitmap bitmap = uploadImageParams.bitmap;
                    final int width = bitmap.getWidth();
                    final int height = bitmap.getHeight();
                    final int highestOneBit = Integer.highestOneBit(width);
                    final int highestOneBit2 = Integer.highestOneBit(height);
                    int n2;
                    if ((n2 = highestOneBit) != width) {
                        n2 = highestOneBit * 2;
                    }
                    int i = highestOneBit2;
                    int j = n2;
                    if (highestOneBit2 != height) {
                        i = highestOneBit2 * 2;
                        j = n2;
                    }
                    while (j > 1024 || i > 1024) {
                        j /= 2;
                        i /= 2;
                    }
                    if (j != bitmap.getWidth() || i != bitmap.getHeight()) {
                        Debug.Printf("UploadImage: scaled bitmap from %d x %d to %d x %d", bitmap.getWidth(), bitmap.getHeight(), j, i);
                        bitmap = Bitmap.createScaledBitmap(bitmap, j, i, true);
                    }
                    final int width2 = bitmap.getWidth();
                    final int height2 = bitmap.getHeight();
                    int n3;
                    if (bitmap.hasAlpha()) {
                        n3 = 4;
                    }
                    else {
                        n3 = 3;
                    }
                    final OpenJPEG openJPEG = new OpenJPEG(width2, height2, n3, n3, 0, 0);
                    final int[] array2 = new int[width2];
                    for (int k = 0; k < height2; ++k) {
                        bitmap.getPixels(array2, 0, width2, 0, k, width2, 1);
                        openJPEG.putPixelRow(height2 - 1 - k, array2, width2);
                    }
                    final File cacheDir = this.context.getCacheDir();
                    String s2 = s;
                    String s3 = s;
                    try {
                        final File tempFile = File.createTempFile("uploadtex", "j2k", cacheDir);
                        s2 = s;
                        s3 = s;
                        openJPEG.SaveJPEG2K(tempFile);
                        s2 = s;
                        s3 = s;
                        final UserManager userManager = UserManager.getUserManager(uploadImageParams.agentUUID);
                        if (userManager == null) {
                            goto Label_0822;
                        }
                        s2 = s;
                        s3 = s;
                        final SLAgentCircuit activeAgentCircuit = userManager.getActiveAgentCircuit();
                        if (activeAgentCircuit == null) {
                            break Label_1198;
                        }
                        s2 = s;
                        s3 = s;
                        final String capability = activeAgentCircuit.getCaps().getCapability(SLCaps.SLCapability.NewFileAgentInventory);
                        if (capability == null) {
                            break Label_1198;
                        }
                        s2 = s;
                        s3 = s;
                        s2 = s;
                        s3 = s;
                        s2 = s;
                        s3 = s;
                        s2 = s;
                        s3 = s;
                        final LLSDString llsdString = new LLSDString("texture");
                        s2 = s;
                        s3 = s;
                        final LLSDMap.LLSDMapEntry llsdMapEntry = new LLSDMap.LLSDMapEntry("asset_type", llsdString);
                        s2 = s;
                        s3 = s;
                        s2 = s;
                        s3 = s;
                        s2 = s;
                        s3 = s;
                        final LLSDString llsdString2 = new LLSDString("(No description)");
                        s2 = s;
                        s3 = s;
                        final LLSDMap.LLSDMapEntry llsdMapEntry2 = new LLSDMap.LLSDMapEntry("description", llsdString2);
                        s2 = s;
                        s3 = s;
                        s2 = s;
                        s3 = s;
                        s2 = s;
                        s3 = s;
                        final LLSDUUID llsduuid = new LLSDUUID(uploadImageParams.folderID);
                        s2 = s;
                        s3 = s;
                        final LLSDMap.LLSDMapEntry llsdMapEntry3 = new LLSDMap.LLSDMapEntry("folder_id", llsduuid);
                        s2 = s;
                        s3 = s;
                        s2 = s;
                        s3 = s;
                        s2 = s;
                        s3 = s;
                        final LLSDString llsdString3 = new LLSDString("texture");
                        s2 = s;
                        s3 = s;
                        final LLSDMap.LLSDMapEntry llsdMapEntry4 = new LLSDMap.LLSDMapEntry("inventory_type", llsdString3);
                        s2 = s;
                        s3 = s;
                        s2 = s;
                        s3 = s;
                        s2 = s;
                        s3 = s;
                        final LLSDString llsdString4 = new LLSDString(uploadImageParams.name);
                        s2 = s;
                        s3 = s;
                        final LLSDMap.LLSDMapEntry llsdMapEntry5 = new LLSDMap.LLSDMapEntry("name", llsdString4);
                        s2 = s;
                        s3 = s;
                        final LLSDMap llsdMap = new LLSDMap(new LLSDMap.LLSDMapEntry[] { llsdMapEntry, llsdMapEntry2, llsdMapEntry3, llsdMapEntry4, llsdMapEntry5 });
                        s2 = s;
                        s3 = s;
                        s2 = s;
                        s3 = s;
                        final LLSDXMLRequest llsdxmlRequest = new LLSDXMLRequest();
                        s2 = s;
                        s3 = s;
                        if (llsdxmlRequest.PerformRequest(capability, llsdMap) == null) {
                            s2 = s;
                            s3 = s;
                            s2 = s;
                            s3 = s;
                            final IOException ex = new IOException("Upload request refused");
                            s2 = s;
                            s3 = s;
                            throw ex;
                        }
                        goto Label_0828;
                    }
                    catch (final IOException ex2) {
                        s = s2;
                    }
                    catch (final LLSDException ex3) {
                        s = s3;
                    }
                    break Label_1198;
                }
                Label_1208: {
                    break Label_1208;
                    while (true) {
                        try {
                            final UploadImageParams uploadImageParams;
                            final UserManager userManager;
                            userManager.getInventoryManager().requestFolderUpdate(uploadImageParams.folderID);
                            final LLSDMap llsdMap;
                            ((Response)llsdMap).close();
                            while (true) {
                                final File tempFile;
                                tempFile.delete();
                                goto Label_0816;
                                b = false;
                                continue;
                                return new UploadImageResult(b, s, null);
                                b = false;
                                continue;
                            }
                        }
                        finally {
                            break Label_1005;
                        }
                        continue;
                        String s4 = s;
                        String s5 = s;
                        try {
                            final LLSDMap llsdMap;
                            ((Response)llsdMap).close();
                            s4 = s;
                            s5 = s;
                            throw;
                        }
                        catch (final IOException ex4) {
                            s = s4;
                            goto Label_0809;
                        }
                        catch (final LLSDException ex5) {
                            s = s5;
                            break;
                        }
                    }
                }
            }
            final LLSDException ex3;
            Debug.Warning(ex3);
            b = false;
            goto Label_0816;
            try {
                final LLSDMap llsdMap;
                if (!((Response)llsdMap).isSuccessful()) {
                    throw new IOException("Invalid HTTP response");
                }
                goto Label_1035;
            }
            finally {}
            continue Label_1005;
        }
    }
    
    protected void onPostExecute(final UploadImageResult uploadImageResult) {
        final String s = null;
        final SLAgentCircuit slAgentCircuit = null;
        super.onPostExecute((Object)uploadImageResult);
        if (this.progressDialog != null) {
            this.progressDialog.cancel();
            this.progressDialog = null;
        }
        if (uploadImageResult != null && uploadImageResult.success) {
            final UserManager userManager = UserManager.getUserManager(this.agentUUID);
            SLAgentCircuit activeAgentCircuit = slAgentCircuit;
            if (userManager != null) {
                activeAgentCircuit = userManager.getActiveAgentCircuit();
            }
            if (activeAgentCircuit != null) {
                final UUID specialFolder = activeAgentCircuit.getModules().inventory.findSpecialFolder(0);
                if (specialFolder != null) {
                    userManager.getInventoryManager().requestFolderUpdate(specialFolder);
                }
            }
        }
        else {
            String message = s;
            if (uploadImageResult != null) {
                message = uploadImageResult.errorMessage;
            }
            final AlertDialog$Builder alertDialog$Builder = new AlertDialog$Builder(this.context);
            if (message == null) {
                message = this.context.getString(2131296544);
            }
            alertDialog$Builder.setMessage((CharSequence)message).setCancelable(true).setNegativeButton((CharSequence)"Dismiss", (DialogInterface$OnClickListener)new _$Lambda$zdXuDSYysFkzF70MB3S4I5y4LlM()).create().show();
        }
    }
    
    protected void onPreExecute() {
        super.onPreExecute();
        (this.progressDialog = new ProgressDialog(this.context)).setMessage((CharSequence)this.context.getString(2131297127));
        this.progressDialog.setIndeterminate(true);
        this.progressDialog.show();
    }
    
    protected static class UploadImageResult
    {
        @Nullable
        public final String errorMessage;
        public final boolean success;
        
        private UploadImageResult(final boolean success, @Nullable final String errorMessage) {
            this.success = success;
            this.errorMessage = errorMessage;
        }
    }
}
