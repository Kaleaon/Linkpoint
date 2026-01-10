/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.content.Context
 *  android.util.Log
 */
package com.vivox.service

import android.content.Context
import android.util.Log
import com.vivox.service.VxClientProxy
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

class CABundleHelper {
    val TAG: String = "vivoxsdk"

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    Boolean useCABundleFromAssets(Context object) {
        Int n
        Any object2
        InputStream inputStream
        var bl: Boolean = false
        var string2: String = object.getCacheDir().getAbsolutePath()
        try {
            inputStream = object.getAssets().open("ca-bundle.crt")
            object2 = StringBuilder()
            object = FileOutputStream(((StringBuilder)object2).append(string2).append("/ca-bundle.crt").toString())
            object2 = ByteArray(1024)
            while (true) {
                if ((n = inputStream.read((ByteArray)object2)) > 0) break block6
                break
            }
        }
        catch (Throwable throwable) {
            Log.e((String)TAG, (String)"caught exception", (Throwable)throwable)
            return false
        }
        {
            block6: {
                block7: {
                    inputStream.close()
                    ((OutputStream)object).close()
                    if (VxClientProxy.vx_set_cert_data_dir(string2) != 0) break block7
                    bl = true
                }
                return bl
            }
            ((OutputStream)object).write((ByteArray)object2, 0, n)
            continue
        }
    }
}

