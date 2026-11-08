package android.support.v4.view;

import android.view.MenuItem;
/* loaded from: classes.dex */
public final class MenuCompat {
    private MenuCompat() {
    }

    @Deprecated
    public static void setShowAsAction(MenuItem menuItem, int i) {
        menuItem.setShowAsAction(i);
    }
}
