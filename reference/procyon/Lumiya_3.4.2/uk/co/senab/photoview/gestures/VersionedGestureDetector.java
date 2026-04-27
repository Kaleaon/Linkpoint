// 
// Decompiled by Procyon v0.6.0
// 

package uk.co.senab.photoview.gestures;

import android.os.Build$VERSION;
import android.content.Context;

public final class VersionedGestureDetector
{
    public static GestureDetector newInstance(final Context context, final OnGestureListener onGestureListener) {
        final int sdk_INT = Build$VERSION.SDK_INT;
        CupcakeGestureDetector cupcakeGestureDetector;
        if (sdk_INT >= 5) {
            if (sdk_INT >= 8) {
                cupcakeGestureDetector = new FroyoGestureDetector(context);
            }
            else {
                cupcakeGestureDetector = new EclairGestureDetector(context);
            }
        }
        else {
            cupcakeGestureDetector = new CupcakeGestureDetector(context);
        }
        cupcakeGestureDetector.setOnGestureListener(onGestureListener);
        return cupcakeGestureDetector;
    }
}
