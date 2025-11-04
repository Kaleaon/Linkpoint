// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.view;

import android.view.MenuItem;

public final class MenuCompat
{
    private MenuCompat() {
    }
    
    @Deprecated
    public static void setShowAsAction(final MenuItem menuItem, final int showAsAction) {
        menuItem.setShowAsAction(showAsAction);
    }
}
