package android.support.v4.graphics.drawable;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.RestrictTo;
import android.support.annotation.VisibleForTesting;
import android.support.v4.view.ViewCompat;

public class IconCompat {
    private static final float ADAPTIVE_ICON_INSET_FACTOR = 0.25f;
    private static final int AMBIENT_SHADOW_ALPHA = 30;
    private static final float BLUR_FACTOR = 0.010416667f;
    private static final float DEFAULT_VIEW_PORT_SCALE = 0.6666667f;
    private static final float ICON_DIAMETER_FACTOR = 0.9166667f;
    private static final int KEY_SHADOW_ALPHA = 61;
    private static final float KEY_SHADOW_OFFSET_FACTOR = 0.020833334f;
    private static final int TYPE_ADAPTIVE_BITMAP = 5;
    private static final int TYPE_BITMAP = 1;
    private static final int TYPE_DATA = 3;
    private static final int TYPE_RESOURCE = 2;
    private static final int TYPE_URI = 4;
    private int mInt1;
    private int mInt2;
    private Object mObj1;
    private final int mType;

    private IconCompat(int i) {
        this.mType = i;
    }

    @VisibleForTesting
    static Bitmap createLegacyIconFromAdaptiveIcon(Bitmap bitmap) {
        int min = (int) (((float) Math.min(bitmap.getWidth(), bitmap.getHeight())) * DEFAULT_VIEW_PORT_SCALE);
        Bitmap createBitmap = Bitmap.createBitmap(min, min, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        Paint paint = new Paint(3);
        float f = ((float) min) * 0.5f;
        float f2 = ICON_DIAMETER_FACTOR * f;
        float f3 = ((float) min) * BLUR_FACTOR;
        paint.setColor(0);
        paint.setShadowLayer(f3, 0.0f, ((float) min) * KEY_SHADOW_OFFSET_FACTOR, 1023410176);
        canvas.drawCircle(f, f, f2, paint);
        paint.setShadowLayer(f3, 0.0f, 0.0f, 503316480);
        canvas.drawCircle(f, f, f2, paint);
        paint.clearShadowLayer();
        paint.setColor(ViewCompat.MEASURED_STATE_MASK);
        BitmapShader bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        Matrix matrix = new Matrix();
        matrix.setTranslate((float) ((-(bitmap.getWidth() - min)) / 2), (float) ((-(bitmap.getHeight() - min)) / 2));
        bitmapShader.setLocalMatrix(matrix);
        paint.setShader(bitmapShader);
        canvas.drawCircle(f, f, f2, paint);
        canvas.setBitmap((Bitmap) null);
        return createBitmap;
    }

    public static IconCompat createWithAdaptiveBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            IconCompat iconCompat = new IconCompat(5);
            iconCompat.mObj1 = bitmap;
            return iconCompat;
        }
        throw new IllegalArgumentException("Bitmap must not be null.");
    }

    public static IconCompat createWithBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            IconCompat iconCompat = new IconCompat(1);
            iconCompat.mObj1 = bitmap;
            return iconCompat;
        }
        throw new IllegalArgumentException("Bitmap must not be null.");
    }

    public static IconCompat createWithContentUri(Uri uri) {
        if (uri != null) {
            return createWithContentUri(uri.toString());
        }
        throw new IllegalArgumentException("Uri must not be null.");
    }

    public static IconCompat createWithContentUri(String str) {
        if (str != null) {
            IconCompat iconCompat = new IconCompat(4);
            iconCompat.mObj1 = str;
            return iconCompat;
        }
        throw new IllegalArgumentException("Uri must not be null.");
    }

    public static IconCompat createWithData(byte[] bArr, int i, int i2) {
        if (bArr != null) {
            IconCompat iconCompat = new IconCompat(3);
            iconCompat.mObj1 = bArr;
            iconCompat.mInt1 = i;
            iconCompat.mInt2 = i2;
            return iconCompat;
        }
        throw new IllegalArgumentException("Data must not be null.");
    }

    public static IconCompat createWithResource(Context context, @DrawableRes int i) {
        if (context != null) {
            IconCompat iconCompat = new IconCompat(2);
            iconCompat.mInt1 = i;
            iconCompat.mObj1 = context;
            return iconCompat;
        }
        throw new IllegalArgumentException("Context must not be null.");
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public void addToShortcutIntent(Intent intent) {
        switch (this.mType) {
            case 1:
                intent.putExtra("android.intent.extra.shortcut.ICON", (Bitmap) this.mObj1);
                return;
            case 2:
                intent.putExtra("android.intent.extra.shortcut.ICON_RESOURCE", Intent.ShortcutIconResource.fromContext((Context) this.mObj1, this.mInt1));
                return;
            case 5:
                intent.putExtra("android.intent.extra.shortcut.ICON", createLegacyIconFromAdaptiveIcon((Bitmap) this.mObj1));
                return;
            default:
                throw new IllegalArgumentException("Icon type not supported for intent shortcuts");
        }
    }

    @TargetApi(26)
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public Icon toIcon() {
        switch (this.mType) {
            case 1:
                return Icon.createWithBitmap((Bitmap) this.mObj1);
            case 2:
                return Icon.createWithResource((Context) this.mObj1, this.mInt1);
            case 3:
                return Icon.createWithData((byte[]) this.mObj1, this.mInt1, this.mInt2);
            case 4:
                return Icon.createWithContentUri((String) this.mObj1);
            case 5:
                return Build.VERSION.SDK_INT < 26 ? Icon.createWithBitmap(createLegacyIconFromAdaptiveIcon((Bitmap) this.mObj1)) : Icon.createWithAdaptiveBitmap((Bitmap) this.mObj1);
            default:
                throw new IllegalArgumentException("Unknown type");
        }
    }
}
