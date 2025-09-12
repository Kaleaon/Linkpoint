// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.inventory;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.openjpeg.OpenJPEG;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.caps.SLCaps;
import com.lumiyaviewer.lumiya.slproto.https.LLSDXMLRequest;
import com.lumiyaviewer.lumiya.slproto.https.SLHTTPSConnection;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventory;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDException;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDMap;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDString;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDUUID;
import com.lumiyaviewer.lumiya.slproto.modules.SLModules;
import com.lumiyaviewer.lumiya.slproto.users.manager.InventoryManager;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.inventory:
//            UploadImageParams

public class UploadImageAsyncTask extends AsyncTask
{
    protected static class UploadImageResult
    {

        public final String errorMessage;
        public final boolean success;

        private UploadImageResult(boolean flag, String s)
        {
            success = flag;
            errorMessage = s;
        }

        UploadImageResult(boolean flag, String s, UploadImageResult uploadimageresult)
        {
            this(flag, s);
        }
    }


    private static final MediaType MEDIA_TYPE_JP2 = MediaType.parse("image/jp2");
    private final UUID agentUUID;
    private final Context context;
    private ProgressDialog progressDialog;

    public UploadImageAsyncTask(Context context1, UUID uuid)
    {
        progressDialog = null;
        context = context1;
        agentUUID = uuid;
    }

    static void lambda$_2D_com_lumiyaviewer_lumiya_ui_inventory_UploadImageAsyncTask_9334(DialogInterface dialoginterface, int i)
    {
        dialoginterface.cancel();
    }

    protected transient UploadImageResult doInBackground(UploadImageParams auploadimageparams[])
    {
        Object obj;
        int k;
        int j1;
        boolean flag;
        flag = true;
        obj = null;
        j1 = auploadimageparams.length;
        k = 0;
_L11:
        if (k >= j1) goto _L2; else goto _L1
_L1:
        Object obj1;
        Object obj2;
        Object obj3;
        UploadImageParams uploadimageparams;
        File file;
        uploadimageparams = auploadimageparams[k];
        obj1 = uploadimageparams.bitmap;
        int l = ((Bitmap) (obj1)).getWidth();
        int k1 = ((Bitmap) (obj1)).getHeight();
        int j = Integer.highestOneBit(l);
        int i1 = Integer.highestOneBit(k1);
        int i = j;
        if (j != l)
        {
            i = j * 2;
        }
        j = i1;
        l = i;
        if (i1 != k1)
        {
            j = i1 * 2;
            l = i;
        }
        for (; l > 1024 || j > 1024; j /= 2)
        {
            l /= 2;
        }

        if (l != ((Bitmap) (obj1)).getWidth() || j != ((Bitmap) (obj1)).getHeight())
        {
            Debug.Printf("UploadImage: scaled bitmap from %d x %d to %d x %d", new Object[] {
                Integer.valueOf(((Bitmap) (obj1)).getWidth()), Integer.valueOf(((Bitmap) (obj1)).getHeight()), Integer.valueOf(l), Integer.valueOf(j)
            });
            obj1 = Bitmap.createScaledBitmap(((Bitmap) (obj1)), l, j, true);
        }
        j = ((Bitmap) (obj1)).getWidth();
        l = ((Bitmap) (obj1)).getHeight();
        int ai[];
        if (((Bitmap) (obj1)).hasAlpha())
        {
            i = 4;
        } else
        {
            i = 3;
        }
        obj3 = new OpenJPEG(j, l, i, i, 0, 0);
        ai = new int[j];
        for (i = 0; i < l; i++)
        {
            ((Bitmap) (obj1)).getPixels(ai, 0, j, 0, i, j, 1);
            ((OpenJPEG) (obj3)).putPixelRow(l - 1 - i, ai, j);
        }

        file = context.getCacheDir();
        obj1 = obj;
        obj2 = obj;
        file = File.createTempFile("uploadtex", "j2k", file);
        obj1 = obj;
        obj2 = obj;
        ((OpenJPEG) (obj3)).SaveJPEG2K(file);
        obj1 = obj;
        obj2 = obj;
        UserManager usermanager = UserManager.getUserManager(uploadimageparams.agentUUID);
        if (usermanager == null) goto _L4; else goto _L3
_L3:
        obj1 = obj;
        obj2 = obj;
        obj3 = usermanager.getActiveAgentCircuit();
_L12:
        if (obj3 == null) goto _L6; else goto _L5
_L5:
        obj1 = obj;
        obj2 = obj;
        obj3 = ((SLAgentCircuit) (obj3)).getCaps().getCapability(com.lumiyaviewer.lumiya.slproto.caps.SLCaps.SLCapability.NewFileAgentInventory);
        if (obj3 == null) goto _L8; else goto _L7
_L7:
        obj1 = obj;
        obj2 = obj;
        Object obj4 = new LLSDMap(new com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDMap.LLSDMapEntry[] {
            new com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDMap.LLSDMapEntry("asset_type", new LLSDString("texture")), new com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDMap.LLSDMapEntry("description", new LLSDString("(No description)")), new com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDMap.LLSDMapEntry("folder_id", new LLSDUUID(uploadimageparams.folderID)), new com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDMap.LLSDMapEntry("inventory_type", new LLSDString("texture")), new com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDMap.LLSDMapEntry("name", new LLSDString(uploadimageparams.name))
        });
        obj1 = obj;
        obj2 = obj;
        obj3 = (new LLSDXMLRequest()).PerformRequest(((String) (obj3)), ((LLSDNode) (obj4)));
        if (obj3 != null) goto _L10; else goto _L9
_L9:
        obj1 = obj;
        obj2 = obj;
        throw new IOException("Upload request refused");
        obj2;
        obj = obj1;
        obj1 = obj2;
_L17:
        Debug.Warning(((Throwable) (obj1)));
        flag = false;
_L15:
        k++;
          goto _L11
_L4:
        obj3 = null;
          goto _L12
_L10:
        obj1 = obj;
        obj2 = obj;
        obj3 = ((LLSDNode) (obj3)).byKey("uploader").asString();
        obj1 = obj;
        obj2 = obj;
        obj3 = (new okhttp3.Request.Builder()).url(((String) (obj3))).header("Accept", "application/llsd+xml").post(RequestBody.create(MEDIA_TYPE_JP2, file)).build();
        obj1 = obj;
        obj2 = obj;
        obj4 = SLHTTPSConnection.getOkHttpClient().newCall(((okhttp3.Request) (obj3))).execute();
        if (obj4 != null) goto _L14; else goto _L13
_L13:
        obj1 = obj;
        obj2 = obj;
        throw new IOException("Null response");
        obj1;
        obj = obj2;
_L19:
        Debug.Warning(((Throwable) (obj1)));
        flag = false;
          goto _L15
_L14:
        if (!((Response) (obj4)).isSuccessful())
        {
            throw new IOException("Invalid HTTP response");
        }
          goto _L16
        Exception exception;
        exception;
_L20:
        obj1 = obj;
        obj2 = obj;
        ((Response) (obj4)).close();
        obj1 = obj;
        obj2 = obj;
        throw exception;
        obj2;
        obj = obj1;
        obj1 = obj2;
          goto _L17
_L16:
        obj1 = LLSDNode.parseXML(((Response) (obj4)).body().byteStream(), null);
        Debug.Log((new StringBuilder()).append("upload reply: ").append(((LLSDNode) (obj1)).serializeToXML()).toString());
        if (!((LLSDNode) (obj1)).keyExists("error"))
        {
            break MISSING_BLOCK_LABEL_872;
        }
        obj1 = ((LLSDNode) (obj1)).byKey("error");
        if (!((LLSDNode) (obj1)).keyExists("message") || (!((LLSDNode) (obj1)).keyExists("success") || ((LLSDNode) (obj1)).byKey("success").asBoolean()))
        {
            break MISSING_BLOCK_LABEL_872;
        }
        obj1 = ((LLSDNode) (obj1)).byKey("message").asString();
        flag = false;
        obj = obj1;
        usermanager.getInventoryManager().requestFolderUpdate(uploadimageparams.folderID);
        obj1 = obj;
        obj2 = obj;
        ((Response) (obj4)).close();
_L18:
        obj1 = obj;
        obj2 = obj;
        file.delete();
          goto _L15
_L8:
        flag = false;
          goto _L18
_L6:
        flag = false;
          goto _L18
_L2:
        return new UploadImageResult(flag, ((String) (obj)), null);
        obj1;
        obj = obj2;
          goto _L19
        exception;
          goto _L20
    }

    protected volatile Object doInBackground(Object aobj[])
    {
        return doInBackground((UploadImageParams[])aobj);
    }

    protected void onPostExecute(UploadImageResult uploadimageresult)
    {
        UserManager usermanager = null;
        Object obj = null;
        super.onPostExecute(uploadimageresult);
        if (progressDialog != null)
        {
            progressDialog.cancel();
            progressDialog = null;
        }
        boolean flag;
        if (uploadimageresult != null)
        {
            flag = uploadimageresult.success;
        } else
        {
            flag = false;
        }
        if (flag)
        {
            usermanager = UserManager.getUserManager(agentUUID);
            uploadimageresult = ((UploadImageResult) (obj));
            if (usermanager != null)
            {
                uploadimageresult = usermanager.getActiveAgentCircuit();
            }
            if (uploadimageresult != null)
            {
                uploadimageresult = uploadimageresult.getModules().inventory.findSpecialFolder(0);
                if (uploadimageresult != null)
                {
                    usermanager.getInventoryManager().requestFolderUpdate(uploadimageresult);
                }
            }
            return;
        }
        obj = usermanager;
        if (uploadimageresult != null)
        {
            obj = uploadimageresult.errorMessage;
        }
        uploadimageresult = new android.app.AlertDialog.Builder(context);
        if (obj == null)
        {
            obj = context.getString(0x7f090120);
        }
        uploadimageresult.setMessage(((CharSequence) (obj))).setCancelable(true).setNegativeButton("Dismiss", new _2D_.Lambda.zdXuDSYysFkzF70MB3S4I5y4LlM()).create().show();
    }

    protected volatile void onPostExecute(Object obj)
    {
        onPostExecute((UploadImageResult)obj);
    }

    protected void onPreExecute()
    {
        super.onPreExecute();
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(context.getString(0x7f090367));
        progressDialog.setIndeterminate(true);
        progressDialog.show();
    }

}
