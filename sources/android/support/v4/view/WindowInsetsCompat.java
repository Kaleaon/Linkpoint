package android.support.v4.view;

import android.graphics.Rect;
import android.os.Build;
import android.view.WindowInsets;

public class WindowInsetsCompat {
    private final Object mInsets;

    public WindowInsetsCompat(WindowInsetsCompat windowInsetsCompat) {
        WindowInsets windowInsets = null;
        if (Build.VERSION.SDK_INT < 20) {
            this.mInsets = null;
        } else {
            this.mInsets = windowInsetsCompat != null ? new WindowInsets((WindowInsets) windowInsetsCompat.mInsets) : windowInsets;
        }
    }

    private WindowInsetsCompat(Object obj) {
        this.mInsets = obj;
    }

    static Object unwrap(WindowInsetsCompat windowInsetsCompat) {
        if (windowInsetsCompat != null) {
            return windowInsetsCompat.mInsets;
        }
        return null;
    }

    static WindowInsetsCompat wrap(Object obj) {
        if (obj != null) {
            return new WindowInsetsCompat(obj);
        }
        return null;
    }

    public WindowInsetsCompat consumeStableInsets() {
        if (Build.VERSION.SDK_INT < 21) {
            return null;
        }
        return new WindowInsetsCompat((Object) ((WindowInsets) this.mInsets).consumeStableInsets());
    }

    public WindowInsetsCompat consumeSystemWindowInsets() {
        if (Build.VERSION.SDK_INT < 20) {
            return null;
        }
        return new WindowInsetsCompat((Object) ((WindowInsets) this.mInsets).consumeSystemWindowInsets());
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        WindowInsetsCompat windowInsetsCompat = (WindowInsetsCompat) obj;
        return this.mInsets != null ? this.mInsets.equals(windowInsetsCompat.mInsets) : windowInsetsCompat.mInsets == null;
    }

    public int getStableInsetBottom() {
        if (Build.VERSION.SDK_INT < 21) {
            return 0;
        }
        return ((WindowInsets) this.mInsets).getStableInsetBottom();
    }

    public int getStableInsetLeft() {
        if (Build.VERSION.SDK_INT < 21) {
            return 0;
        }
        return ((WindowInsets) this.mInsets).getStableInsetLeft();
    }

    public int getStableInsetRight() {
        if (Build.VERSION.SDK_INT < 21) {
            return 0;
        }
        return ((WindowInsets) this.mInsets).getStableInsetRight();
    }

    public int getStableInsetTop() {
        if (Build.VERSION.SDK_INT < 21) {
            return 0;
        }
        return ((WindowInsets) this.mInsets).getStableInsetTop();
    }

    public int getSystemWindowInsetBottom() {
        if (Build.VERSION.SDK_INT < 20) {
            return 0;
        }
        return ((WindowInsets) this.mInsets).getSystemWindowInsetBottom();
    }

    public int getSystemWindowInsetLeft() {
        if (Build.VERSION.SDK_INT < 20) {
            return 0;
        }
        return ((WindowInsets) this.mInsets).getSystemWindowInsetLeft();
    }

    public int getSystemWindowInsetRight() {
        if (Build.VERSION.SDK_INT < 20) {
            return 0;
        }
        return ((WindowInsets) this.mInsets).getSystemWindowInsetRight();
    }

    public int getSystemWindowInsetTop() {
        if (Build.VERSION.SDK_INT < 20) {
            return 0;
        }
        return ((WindowInsets) this.mInsets).getSystemWindowInsetTop();
    }

    public boolean hasInsets() {
        if (Build.VERSION.SDK_INT < 20) {
            return false;
        }
        return ((WindowInsets) this.mInsets).hasInsets();
    }

    public boolean hasStableInsets() {
        if (Build.VERSION.SDK_INT < 21) {
            return false;
        }
        return ((WindowInsets) this.mInsets).hasStableInsets();
    }

    public boolean hasSystemWindowInsets() {
        if (Build.VERSION.SDK_INT < 20) {
            return false;
        }
        return ((WindowInsets) this.mInsets).hasSystemWindowInsets();
    }

    public int hashCode() {
        if (this.mInsets != null) {
            return this.mInsets.hashCode();
        }
        return 0;
    }

    public boolean isConsumed() {
        if (Build.VERSION.SDK_INT < 21) {
            return false;
        }
        return ((WindowInsets) this.mInsets).isConsumed();
    }

    public boolean isRound() {
        if (Build.VERSION.SDK_INT < 20) {
            return false;
        }
        return ((WindowInsets) this.mInsets).isRound();
    }

    public WindowInsetsCompat replaceSystemWindowInsets(int i, int i2, int i3, int i4) {
        if (Build.VERSION.SDK_INT < 20) {
            return null;
        }
        return new WindowInsetsCompat((Object) ((WindowInsets) this.mInsets).replaceSystemWindowInsets(i, i2, i3, i4));
    }

    public WindowInsetsCompat replaceSystemWindowInsets(Rect rect) {
        if (Build.VERSION.SDK_INT < 21) {
            return null;
        }
        return new WindowInsetsCompat((Object) ((WindowInsets) this.mInsets).replaceSystemWindowInsets(rect));
    }
}
