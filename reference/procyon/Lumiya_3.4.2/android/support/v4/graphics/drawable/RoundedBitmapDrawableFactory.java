// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.graphics.drawable;

import android.support.v4.graphics.BitmapCompat;
import android.support.v4.view.GravityCompat;
import android.graphics.Rect;
import android.util.Log;
import android.graphics.BitmapFactory;
import java.io.InputStream;
import android.os.Build$VERSION;
import android.graphics.Bitmap;
import android.content.res.Resources;

public final class RoundedBitmapDrawableFactory
{
    private static final String TAG = "RoundedBitmapDrawableFa";
    
    private RoundedBitmapDrawableFactory() {
    }
    
    public static RoundedBitmapDrawable create(final Resources resources, final Bitmap bitmap) {
        if (Build$VERSION.SDK_INT < 21) {
            return new DefaultRoundedBitmapDrawable(resources, bitmap);
        }
        return new RoundedBitmapDrawable21(resources, bitmap);
    }
    
    public static RoundedBitmapDrawable create(final Resources resources, final InputStream obj) {
        final RoundedBitmapDrawable create = create(resources, BitmapFactory.decodeStream(obj));
        if (create.getBitmap() == null) {
            Log.w("RoundedBitmapDrawableFa", "RoundedBitmapDrawable cannot decode " + obj);
        }
        return create;
    }
    
    public static RoundedBitmapDrawable create(final Resources resources, final String str) {
        final RoundedBitmapDrawable create = create(resources, BitmapFactory.decodeFile(str));
        if (create.getBitmap() == null) {
            Log.w("RoundedBitmapDrawableFa", "RoundedBitmapDrawable cannot decode " + str);
        }
        return create;
    }
    
    private static class DefaultRoundedBitmapDrawable extends RoundedBitmapDrawable
    {
        DefaultRoundedBitmapDrawable(final Resources resources, final Bitmap bitmap) {
            super(resources, bitmap);
        }
        
        @Override
        void gravityCompatApply(final int n, final int n2, final int n3, final Rect rect, final Rect rect2) {
            GravityCompat.apply(n, n2, n3, rect, rect2, 0);
        }
        
        @Override
        public boolean hasMipMap() {
            boolean b = false;
            if (this.mBitmap != null && BitmapCompat.hasMipMap(this.mBitmap)) {
                b = true;
            }
            return b;
        }
        
        @Override
        public void setMipMap(final boolean b) {
            if (this.mBitmap != null) {
                BitmapCompat.setHasMipMap(this.mBitmap, b);
                this.invalidateSelf();
            }
        }
    }
}
