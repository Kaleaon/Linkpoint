// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.os;

import java.io.IOException;
import android.util.Log;
import android.os.Environment;
import android.os.Build$VERSION;
import java.io.File;

public final class EnvironmentCompat
{
    public static final String MEDIA_UNKNOWN = "unknown";
    private static final String TAG = "EnvironmentCompat";
    
    private EnvironmentCompat() {
    }
    
    public static String getStorageState(final File file) {
        Label_0029: {
            if (Build$VERSION.SDK_INT >= 19) {
                break Label_0029;
            }
            try {
                if (!file.getCanonicalPath().startsWith(Environment.getExternalStorageDirectory().getCanonicalPath())) {
                    return "unknown";
                }
                return Environment.getExternalStorageState();
                return Environment.getStorageState(file);
            }
            catch (final IOException obj) {
                Log.w("EnvironmentCompat", "Failed to resolve canonical path: " + obj);
                return "unknown";
            }
        }
    }
}
