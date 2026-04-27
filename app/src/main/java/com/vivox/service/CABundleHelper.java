/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.content.Context
 *  android.util.Log
 */
package com.vivox.service;

import android.content.Context;
import android.util.Log;
import com.vivox.service.VxClientProxy;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class CABundleHelper {
    public static final String TAG = "vivoxsdk";

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static Boolean useCABundleFromAssets(Context object) {
        int n;
        Object object2;
        InputStream inputStream;
        boolean bl = false;
        String string2 = object.getCacheDir().getAbsolutePath();
        try {
            inputStream = object.getAssets().open("ca-bundle.crt");
            object2 = new StringBuilder();
            object = new FileOutputStream(((StringBuilder)object2).append(string2).append("/ca-bundle.crt").toString());
            object2 = new byte[1024];
            while (true) {
                if ((n = inputStream.read((byte[])object2)) > 0) break block6;
                break;
            }
        }
        catch (Throwable throwable) {
            Log.e((String)TAG, (String)"caught exception", (Throwable)throwable);
            return false;
        }
        {
            block6: {
                block7: {
                    inputStream.close();
                    ((OutputStream)object).close();
                    if (VxClientProxy.vx_set_cert_data_dir(string2) != 0) break block7;
                    bl = true;
                }
                return bl;
            }
            ((OutputStream)object).write((byte[])object2, 0, n);
            continue;
        }
    }
}

