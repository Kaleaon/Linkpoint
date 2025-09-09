package android.support.v4.graphics;

import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.NonNull;

public final class PaintCompat {
    private PaintCompat() {
    }

    public static boolean hasGlyph(@NonNull Paint paint, @NonNull String str) {
        return Build.VERSION.SDK_INT < 23 ? PaintCompatApi14.hasGlyph(paint, str) : paint.hasGlyph(str);
    }
}
