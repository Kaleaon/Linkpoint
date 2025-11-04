// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.view;

import android.graphics.Rect;
import android.view.WindowInsets;
import android.os.Build$VERSION;

public class WindowInsetsCompat
{
    private final Object mInsets;
    
    public WindowInsetsCompat(final WindowInsetsCompat windowInsetsCompat) {
        Object mInsets = null;
        if (Build$VERSION.SDK_INT < 20) {
            this.mInsets = null;
        }
        else {
            if (windowInsetsCompat != null) {
                mInsets = new WindowInsets((WindowInsets)windowInsetsCompat.mInsets);
            }
            this.mInsets = mInsets;
        }
    }
    
    private WindowInsetsCompat(final Object mInsets) {
        this.mInsets = mInsets;
    }
    
    static Object unwrap(final WindowInsetsCompat windowInsetsCompat) {
        Object mInsets = null;
        if (windowInsetsCompat != null) {
            mInsets = windowInsetsCompat.mInsets;
        }
        return mInsets;
    }
    
    static WindowInsetsCompat wrap(final Object o) {
        WindowInsetsCompat windowInsetsCompat = null;
        if (o != null) {
            windowInsetsCompat = new WindowInsetsCompat(o);
        }
        return windowInsetsCompat;
    }
    
    public WindowInsetsCompat consumeStableInsets() {
        if (Build$VERSION.SDK_INT < 21) {
            return null;
        }
        return new WindowInsetsCompat(((WindowInsets)this.mInsets).consumeStableInsets());
    }
    
    public WindowInsetsCompat consumeSystemWindowInsets() {
        if (Build$VERSION.SDK_INT < 20) {
            return null;
        }
        return new WindowInsetsCompat(((WindowInsets)this.mInsets).consumeSystemWindowInsets());
    }
    
    @Override
    public boolean equals(final Object o) {
        boolean equals = false;
        if (this == o) {
            return true;
        }
        if (o != null && this.getClass() == o.getClass()) {
            final WindowInsetsCompat windowInsetsCompat = (WindowInsetsCompat)o;
            if (this.mInsets != null) {
                equals = this.mInsets.equals(windowInsetsCompat.mInsets);
            }
            else if (windowInsetsCompat.mInsets == null) {
                equals = true;
            }
            return equals;
        }
        return false;
    }
    
    public int getStableInsetBottom() {
        if (Build$VERSION.SDK_INT < 21) {
            return 0;
        }
        return ((WindowInsets)this.mInsets).getStableInsetBottom();
    }
    
    public int getStableInsetLeft() {
        if (Build$VERSION.SDK_INT < 21) {
            return 0;
        }
        return ((WindowInsets)this.mInsets).getStableInsetLeft();
    }
    
    public int getStableInsetRight() {
        if (Build$VERSION.SDK_INT < 21) {
            return 0;
        }
        return ((WindowInsets)this.mInsets).getStableInsetRight();
    }
    
    public int getStableInsetTop() {
        if (Build$VERSION.SDK_INT < 21) {
            return 0;
        }
        return ((WindowInsets)this.mInsets).getStableInsetTop();
    }
    
    public int getSystemWindowInsetBottom() {
        if (Build$VERSION.SDK_INT < 20) {
            return 0;
        }
        return ((WindowInsets)this.mInsets).getSystemWindowInsetBottom();
    }
    
    public int getSystemWindowInsetLeft() {
        if (Build$VERSION.SDK_INT < 20) {
            return 0;
        }
        return ((WindowInsets)this.mInsets).getSystemWindowInsetLeft();
    }
    
    public int getSystemWindowInsetRight() {
        if (Build$VERSION.SDK_INT < 20) {
            return 0;
        }
        return ((WindowInsets)this.mInsets).getSystemWindowInsetRight();
    }
    
    public int getSystemWindowInsetTop() {
        if (Build$VERSION.SDK_INT < 20) {
            return 0;
        }
        return ((WindowInsets)this.mInsets).getSystemWindowInsetTop();
    }
    
    public boolean hasInsets() {
        return Build$VERSION.SDK_INT >= 20 && ((WindowInsets)this.mInsets).hasInsets();
    }
    
    public boolean hasStableInsets() {
        return Build$VERSION.SDK_INT >= 21 && ((WindowInsets)this.mInsets).hasStableInsets();
    }
    
    public boolean hasSystemWindowInsets() {
        return Build$VERSION.SDK_INT >= 20 && ((WindowInsets)this.mInsets).hasSystemWindowInsets();
    }
    
    @Override
    public int hashCode() {
        int hashCode;
        if (this.mInsets != null) {
            hashCode = this.mInsets.hashCode();
        }
        else {
            hashCode = 0;
        }
        return hashCode;
    }
    
    public boolean isConsumed() {
        return Build$VERSION.SDK_INT >= 21 && ((WindowInsets)this.mInsets).isConsumed();
    }
    
    public boolean isRound() {
        return Build$VERSION.SDK_INT >= 20 && ((WindowInsets)this.mInsets).isRound();
    }
    
    public WindowInsetsCompat replaceSystemWindowInsets(final int n, final int n2, final int n3, final int n4) {
        if (Build$VERSION.SDK_INT < 20) {
            return null;
        }
        return new WindowInsetsCompat(((WindowInsets)this.mInsets).replaceSystemWindowInsets(n, n2, n3, n4));
    }
    
    public WindowInsetsCompat replaceSystemWindowInsets(final Rect rect) {
        if (Build$VERSION.SDK_INT < 21) {
            return null;
        }
        return new WindowInsetsCompat(((WindowInsets)this.mInsets).replaceSystemWindowInsets(rect));
    }
}
