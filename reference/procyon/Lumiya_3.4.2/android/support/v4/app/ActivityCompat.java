// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.app;

import android.app.SharedElementCallback$OnSharedElementsReadyListener;
import java.util.Map;
import java.util.List;
import android.content.Context;
import android.os.Parcelable;
import android.graphics.RectF;
import android.graphics.Matrix;
import android.view.View;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.content.IntentSender$SendIntentException;
import android.content.IntentSender;
import android.os.Bundle;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.content.Intent;
import android.net.Uri;
import android.os.Build$VERSION;
import android.app.Activity;
import android.support.v4.content.ContextCompat;

public class ActivityCompat extends ContextCompat
{
    protected ActivityCompat() {
    }
    
    public static void finishAffinity(final Activity activity) {
        if (Build$VERSION.SDK_INT < 16) {
            activity.finish();
        }
        else {
            activity.finishAffinity();
        }
    }
    
    public static void finishAfterTransition(final Activity activity) {
        if (Build$VERSION.SDK_INT < 21) {
            activity.finish();
        }
        else {
            activity.finishAfterTransition();
        }
    }
    
    @Nullable
    public static Uri getReferrer(final Activity activity) {
        if (Build$VERSION.SDK_INT >= 22) {
            return activity.getReferrer();
        }
        final Intent intent = activity.getIntent();
        final Uri uri = (Uri)intent.getParcelableExtra("android.intent.extra.REFERRER");
        if (uri != null) {
            return uri;
        }
        final String stringExtra = intent.getStringExtra("android.intent.extra.REFERRER_NAME");
        if (stringExtra == null) {
            return null;
        }
        return Uri.parse(stringExtra);
    }
    
    public static boolean invalidateOptionsMenu(final Activity activity) {
        activity.invalidateOptionsMenu();
        return true;
    }
    
    public static void postponeEnterTransition(final Activity activity) {
        if (Build$VERSION.SDK_INT >= 21) {
            activity.postponeEnterTransition();
        }
    }
    
    public static void requestPermissions(@NonNull final Activity activity, @NonNull final String[] array, @IntRange(from = 0L) final int n) {
        if (Build$VERSION.SDK_INT < 23) {
            if (activity instanceof OnRequestPermissionsResultCallback) {
                new Handler(Looper.getMainLooper()).post((Runnable)new Runnable() {
                    @Override
                    public void run() {
                        final int[] array = new int[array.length];
                        final PackageManager packageManager = activity.getPackageManager();
                        final String packageName = activity.getPackageName();
                        for (int length = array.length, i = 0; i < length; ++i) {
                            array[i] = packageManager.checkPermission(array[i], packageName);
                        }
                        ((OnRequestPermissionsResultCallback)activity).onRequestPermissionsResult(n, array, array);
                    }
                });
            }
        }
        else {
            if (activity instanceof RequestPermissionsRequestCodeValidator) {
                ((RequestPermissionsRequestCodeValidator)activity).validateRequestPermissionsRequestCode(n);
            }
            activity.requestPermissions(array, n);
        }
    }
    
    public static void setEnterSharedElementCallback(final Activity activity, final SharedElementCallback sharedElementCallback) {
        final android.app.SharedElementCallback sharedElementCallback2 = null;
        final android.app.SharedElementCallback sharedElementCallback3 = null;
        if (Build$VERSION.SDK_INT < 23) {
            if (Build$VERSION.SDK_INT >= 21) {
                android.app.SharedElementCallback enterSharedElementCallback;
                if (sharedElementCallback == null) {
                    enterSharedElementCallback = sharedElementCallback2;
                }
                else {
                    enterSharedElementCallback = new SharedElementCallback21Impl(sharedElementCallback);
                }
                activity.setEnterSharedElementCallback(enterSharedElementCallback);
            }
        }
        else {
            android.app.SharedElementCallback enterSharedElementCallback2;
            if (sharedElementCallback == null) {
                enterSharedElementCallback2 = sharedElementCallback3;
            }
            else {
                enterSharedElementCallback2 = new SharedElementCallback23Impl(sharedElementCallback);
            }
            activity.setEnterSharedElementCallback(enterSharedElementCallback2);
        }
    }
    
    public static void setExitSharedElementCallback(final Activity activity, final SharedElementCallback sharedElementCallback) {
        final android.app.SharedElementCallback sharedElementCallback2 = null;
        final android.app.SharedElementCallback sharedElementCallback3 = null;
        if (Build$VERSION.SDK_INT < 23) {
            if (Build$VERSION.SDK_INT >= 21) {
                android.app.SharedElementCallback exitSharedElementCallback;
                if (sharedElementCallback == null) {
                    exitSharedElementCallback = sharedElementCallback2;
                }
                else {
                    exitSharedElementCallback = new SharedElementCallback21Impl(sharedElementCallback);
                }
                activity.setExitSharedElementCallback(exitSharedElementCallback);
            }
        }
        else {
            android.app.SharedElementCallback exitSharedElementCallback2;
            if (sharedElementCallback == null) {
                exitSharedElementCallback2 = sharedElementCallback3;
            }
            else {
                exitSharedElementCallback2 = new SharedElementCallback23Impl(sharedElementCallback);
            }
            activity.setExitSharedElementCallback(exitSharedElementCallback2);
        }
    }
    
    public static boolean shouldShowRequestPermissionRationale(@NonNull final Activity activity, @NonNull final String s) {
        return Build$VERSION.SDK_INT >= 23 && activity.shouldShowRequestPermissionRationale(s);
    }
    
    public static void startActivityForResult(final Activity activity, final Intent intent, final int n, @Nullable final Bundle bundle) {
        if (Build$VERSION.SDK_INT < 16) {
            activity.startActivityForResult(intent, n);
        }
        else {
            activity.startActivityForResult(intent, n, bundle);
        }
    }
    
    public static void startIntentSenderForResult(final Activity activity, final IntentSender intentSender, final int n, final Intent intent, final int n2, final int n3, final int n4, @Nullable final Bundle bundle) throws IntentSender$SendIntentException {
        if (Build$VERSION.SDK_INT < 16) {
            activity.startIntentSenderForResult(intentSender, n, intent, n2, n3, n4);
        }
        else {
            activity.startIntentSenderForResult(intentSender, n, intent, n2, n3, n4, bundle);
        }
    }
    
    public static void startPostponedEnterTransition(final Activity activity) {
        if (Build$VERSION.SDK_INT >= 21) {
            activity.startPostponedEnterTransition();
        }
    }
    
    public interface OnRequestPermissionsResultCallback
    {
        void onRequestPermissionsResult(final int p0, @NonNull final String[] p1, @NonNull final int[] p2);
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    public interface RequestPermissionsRequestCodeValidator
    {
        void validateRequestPermissionsRequestCode(final int p0);
    }
    
    @RequiresApi(21)
    private static class SharedElementCallback21Impl extends android.app.SharedElementCallback
    {
        protected SharedElementCallback mCallback;
        
        public SharedElementCallback21Impl(final SharedElementCallback mCallback) {
            this.mCallback = mCallback;
        }
        
        public Parcelable onCaptureSharedElementSnapshot(final View view, final Matrix matrix, final RectF rectF) {
            return this.mCallback.onCaptureSharedElementSnapshot(view, matrix, rectF);
        }
        
        public View onCreateSnapshotView(final Context context, final Parcelable parcelable) {
            return this.mCallback.onCreateSnapshotView(context, parcelable);
        }
        
        public void onMapSharedElements(final List<String> list, final Map<String, View> map) {
            this.mCallback.onMapSharedElements(list, map);
        }
        
        public void onRejectSharedElements(final List<View> list) {
            this.mCallback.onRejectSharedElements(list);
        }
        
        public void onSharedElementEnd(final List<String> list, final List<View> list2, final List<View> list3) {
            this.mCallback.onSharedElementEnd(list, list2, list3);
        }
        
        public void onSharedElementStart(final List<String> list, final List<View> list2, final List<View> list3) {
            this.mCallback.onSharedElementStart(list, list2, list3);
        }
    }
    
    @RequiresApi(23)
    private static class SharedElementCallback23Impl extends SharedElementCallback21Impl
    {
        public SharedElementCallback23Impl(final SharedElementCallback sharedElementCallback) {
            super(sharedElementCallback);
        }
        
        public void onSharedElementsArrived(final List<String> list, final List<View> list2, final SharedElementCallback$OnSharedElementsReadyListener sharedElementCallback$OnSharedElementsReadyListener) {
            this.mCallback.onSharedElementsArrived(list, list2, (SharedElementCallback.OnSharedElementsReadyListener)new SharedElementCallback.OnSharedElementsReadyListener() {
                @Override
                public void onSharedElementsReady() {
                    sharedElementCallback$OnSharedElementsReadyListener.onSharedElementsReady();
                }
            });
        }
    }
}
