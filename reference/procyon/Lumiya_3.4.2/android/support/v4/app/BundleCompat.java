// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.app;

import java.lang.reflect.InvocationTargetException;
import android.util.Log;
import java.lang.reflect.Method;
import android.os.Build$VERSION;
import android.os.IBinder;
import android.os.Bundle;

public final class BundleCompat
{
    private BundleCompat() {
    }
    
    public static IBinder getBinder(final Bundle bundle, final String s) {
        if (Build$VERSION.SDK_INT < 18) {
            return BundleCompatBaseImpl.getBinder(bundle, s);
        }
        return bundle.getBinder(s);
    }
    
    public static void putBinder(final Bundle bundle, final String s, final IBinder binder) {
        if (Build$VERSION.SDK_INT < 18) {
            BundleCompatBaseImpl.putBinder(bundle, s, binder);
        }
        else {
            bundle.putBinder(s, binder);
        }
    }
    
    static class BundleCompatBaseImpl
    {
        private static final String TAG = "BundleCompatBaseImpl";
        private static Method sGetIBinderMethod;
        private static boolean sGetIBinderMethodFetched;
        private static Method sPutIBinderMethod;
        private static boolean sPutIBinderMethodFetched;
        
        public static IBinder getBinder(final Bundle obj, final String s) {
            Label_0060: {
                Block_2: {
                    Label_0006: {
                        if (!BundleCompatBaseImpl.sGetIBinderMethodFetched) {
                            while (true) {
                                try {
                                    (BundleCompatBaseImpl.sGetIBinderMethod = Bundle.class.getMethod("getIBinder", String.class)).setAccessible(true);
                                    BundleCompatBaseImpl.sGetIBinderMethodFetched = true;
                                    break Label_0006;
                                }
                                catch (final NoSuchMethodException ex) {
                                    Log.i("BundleCompatBaseImpl", "Failed to retrieve getIBinder method", (Throwable)ex);
                                    continue;
                                }
                                break;
                            }
                            break Block_2;
                        }
                    }
                    if (BundleCompatBaseImpl.sGetIBinderMethod != null) {
                        break Label_0060;
                    }
                    return null;
                }
                try {
                    return (IBinder)BundleCompatBaseImpl.sGetIBinderMethod.invoke(obj, s);
                }
                catch (final InvocationTargetException | IllegalAccessException | IllegalArgumentException ex2) {
                    Log.i("BundleCompatBaseImpl", "Failed to invoke getIBinder via reflection", (Throwable)ex2);
                    BundleCompatBaseImpl.sGetIBinderMethod = null;
                    return null;
                }
            }
        }
        
        public static void putBinder(final Bundle obj, final String s, final IBinder binder) {
            Label_0064: {
                Block_2: {
                    Label_0006: {
                        if (!BundleCompatBaseImpl.sPutIBinderMethodFetched) {
                            while (true) {
                                try {
                                    (BundleCompatBaseImpl.sPutIBinderMethod = Bundle.class.getMethod("putIBinder", String.class, IBinder.class)).setAccessible(true);
                                    BundleCompatBaseImpl.sPutIBinderMethodFetched = true;
                                    break Label_0006;
                                }
                                catch (final NoSuchMethodException ex) {
                                    Log.i("BundleCompatBaseImpl", "Failed to retrieve putIBinder method", (Throwable)ex);
                                    continue;
                                }
                                break;
                            }
                            break Block_2;
                        }
                    }
                    if (BundleCompatBaseImpl.sPutIBinderMethod != null) {
                        break Label_0064;
                    }
                    return;
                }
                try {
                    BundleCompatBaseImpl.sPutIBinderMethod.invoke(obj, s, binder);
                }
                catch (final InvocationTargetException | IllegalAccessException | IllegalArgumentException ex2) {
                    Log.i("BundleCompatBaseImpl", "Failed to invoke putIBinder via reflection", (Throwable)ex2);
                    BundleCompatBaseImpl.sPutIBinderMethod = null;
                }
            }
        }
    }
}
