package uk.co.senab.photoview.scrollerproxy;

import android.content.Context;
import android.os.Build;

public abstract class ScrollerProxy {
    public static ScrollerProxy getScroller(Context context) {
        return Build.VERSION.SDK_INT >= 9 ? Build.VERSION.SDK_INT >= 14 ? new IcsScroller(context) : new GingerScroller(context) : new PreGingerScroller(context);
    }

    public abstract boolean computeScrollOffset();

    public abstract void fling(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10);

    public abstract void forceFinished(boolean z);

    public abstract int getCurrX();

    public abstract int getCurrY();

    public abstract boolean isFinished();
}
