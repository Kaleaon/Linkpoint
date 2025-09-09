package android.support.v4.app;

import android.app.Activity;
import android.app.SharedElementCallback;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.content.ContextCompat;
import android.view.View;
import java.util.List;
import java.util.Map;

public class ActivityCompat extends ContextCompat {

    public interface OnRequestPermissionsResultCallback {
        void onRequestPermissionsResult(int i, @NonNull String[] strArr, @NonNull int[] iArr);
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public interface RequestPermissionsRequestCodeValidator {
        void validateRequestPermissionsRequestCode(int i);
    }

    @RequiresApi(21)
    private static class SharedElementCallback21Impl extends SharedElementCallback {
        protected SharedElementCallback mCallback;

        public SharedElementCallback21Impl(SharedElementCallback sharedElementCallback) {
            this.mCallback = sharedElementCallback;
        }

        public Parcelable onCaptureSharedElementSnapshot(View view, Matrix matrix, RectF rectF) {
            return this.mCallback.onCaptureSharedElementSnapshot(view, matrix, rectF);
        }

        public View onCreateSnapshotView(Context context, Parcelable parcelable) {
            return this.mCallback.onCreateSnapshotView(context, parcelable);
        }

        public void onMapSharedElements(List<String> list, Map<String, View> map) {
            this.mCallback.onMapSharedElements(list, map);
        }

        public void onRejectSharedElements(List<View> list) {
            this.mCallback.onRejectSharedElements(list);
        }

        public void onSharedElementEnd(List<String> list, List<View> list2, List<View> list3) {
            this.mCallback.onSharedElementEnd(list, list2, list3);
        }

        public void onSharedElementStart(List<String> list, List<View> list2, List<View> list3) {
            this.mCallback.onSharedElementStart(list, list2, list3);
        }
    }

    @RequiresApi(23)
    private static class SharedElementCallback23Impl extends SharedElementCallback21Impl {
        public SharedElementCallback23Impl(SharedElementCallback sharedElementCallback) {
            super(sharedElementCallback);
        }

        public void onSharedElementsArrived(List<String> list, List<View> list2, final SharedElementCallback.OnSharedElementsReadyListener onSharedElementsReadyListener) {
            this.mCallback.onSharedElementsArrived(list, list2, new SharedElementCallback.OnSharedElementsReadyListener() {
                public void onSharedElementsReady() {
                    onSharedElementsReadyListener.onSharedElementsReady();
                }
            });
        }
    }

    protected ActivityCompat() {
    }

    public static void finishAffinity(Activity activity) {
        if (Build.VERSION.SDK_INT < 16) {
            activity.finish();
        } else {
            activity.finishAffinity();
        }
    }

    public static void finishAfterTransition(Activity activity) {
        if (Build.VERSION.SDK_INT < 21) {
            activity.finish();
        } else {
            activity.finishAfterTransition();
        }
    }

    @Nullable
    public static Uri getReferrer(Activity activity) {
        if (Build.VERSION.SDK_INT >= 22) {
            return activity.getReferrer();
        }
        Intent intent = activity.getIntent();
        Uri uri = (Uri) intent.getParcelableExtra("android.intent.extra.REFERRER");
        if (uri != null) {
            return uri;
        }
        String stringExtra = intent.getStringExtra("android.intent.extra.REFERRER_NAME");
        if (stringExtra == null) {
            return null;
        }
        return Uri.parse(stringExtra);
    }

    public static boolean invalidateOptionsMenu(Activity activity) {
        activity.invalidateOptionsMenu();
        return true;
    }

    public static void postponeEnterTransition(Activity activity) {
        if (Build.VERSION.SDK_INT >= 21) {
            activity.postponeEnterTransition();
        }
    }

    public static void requestPermissions(@NonNull final Activity activity, @NonNull final String[] strArr, @IntRange(from = 0) final int i) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (activity instanceof RequestPermissionsRequestCodeValidator) {
                ((RequestPermissionsRequestCodeValidator) activity).validateRequestPermissionsRequestCode(i);
            }
            activity.requestPermissions(strArr, i);
        } else if (activity instanceof OnRequestPermissionsResultCallback) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                public void run() {
                    int[] iArr = new int[strArr.length];
                    PackageManager packageManager = activity.getPackageManager();
                    String packageName = activity.getPackageName();
                    int length = strArr.length;
                    for (int i = 0; i < length; i++) {
                        iArr[i] = packageManager.checkPermission(strArr[i], packageName);
                    }
                    ((OnRequestPermissionsResultCallback) activity).onRequestPermissionsResult(i, strArr, iArr);
                }
            });
        }
    }

    public static void setEnterSharedElementCallback(Activity activity, SharedElementCallback sharedElementCallback) {
        android.app.SharedElementCallback sharedElementCallback2 = null;
        if (Build.VERSION.SDK_INT >= 23) {
            if (sharedElementCallback != null) {
                sharedElementCallback2 = new SharedElementCallback23Impl(sharedElementCallback);
            }
            activity.setEnterSharedElementCallback(sharedElementCallback2);
        } else if (Build.VERSION.SDK_INT >= 21) {
            if (sharedElementCallback != null) {
                sharedElementCallback2 = new SharedElementCallback21Impl(sharedElementCallback);
            }
            activity.setEnterSharedElementCallback(sharedElementCallback2);
        }
    }

    public static void setExitSharedElementCallback(Activity activity, SharedElementCallback sharedElementCallback) {
        android.app.SharedElementCallback sharedElementCallback2 = null;
        if (Build.VERSION.SDK_INT >= 23) {
            if (sharedElementCallback != null) {
                sharedElementCallback2 = new SharedElementCallback23Impl(sharedElementCallback);
            }
            activity.setExitSharedElementCallback(sharedElementCallback2);
        } else if (Build.VERSION.SDK_INT >= 21) {
            if (sharedElementCallback != null) {
                sharedElementCallback2 = new SharedElementCallback21Impl(sharedElementCallback);
            }
            activity.setExitSharedElementCallback(sharedElementCallback2);
        }
    }

    public static boolean shouldShowRequestPermissionRationale(@NonNull Activity activity, @NonNull String str) {
        if (Build.VERSION.SDK_INT < 23) {
            return false;
        }
        return activity.shouldShowRequestPermissionRationale(str);
    }

    public static void startActivityForResult(Activity activity, Intent intent, int i, @Nullable Bundle bundle) {
        if (Build.VERSION.SDK_INT < 16) {
            activity.startActivityForResult(intent, i);
        } else {
            activity.startActivityForResult(intent, i, bundle);
        }
    }

    public static void startIntentSenderForResult(Activity activity, IntentSender intentSender, int i, Intent intent, int i2, int i3, int i4, @Nullable Bundle bundle) throws IntentSender.SendIntentException {
        if (Build.VERSION.SDK_INT < 16) {
            activity.startIntentSenderForResult(intentSender, i, intent, i2, i3, i4);
        } else {
            activity.startIntentSenderForResult(intentSender, i, intent, i2, i3, i4, bundle);
        }
    }

    public static void startPostponedEnterTransition(Activity activity) {
        if (Build.VERSION.SDK_INT >= 21) {
            activity.startPostponedEnterTransition();
        }
    }
}
