package uk.co.senab.photoview.gestures;

import android.content.Context;
import android.os.Build;

public final class VersionedGestureDetector {
    public static GestureDetector newInstance(Context context, OnGestureListener onGestureListener) {
        int i = Build.VERSION.SDK_INT;
        GestureDetector froyoGestureDetector = i >= 5 ? i >= 8 ? new FroyoGestureDetector(context) : new EclairGestureDetector(context) : new CupcakeGestureDetector(context);
        froyoGestureDetector.setOnGestureListener(onGestureListener);
        return froyoGestureDetector;
    }
}
