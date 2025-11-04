// 
// Decompiled by Procyon v0.6.0
// 

package android.support.design.internal;

import android.view.SubMenu;
import android.support.v7.view.menu.MenuItemImpl;
import android.view.MenuItem;
import android.content.Context;
import android.support.annotation.RestrictTo;
import android.support.v7.view.menu.MenuBuilder;

@RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
public final class BottomNavigationMenu extends MenuBuilder
{
    public static final int MAX_ITEM_COUNT = 5;
    
    public BottomNavigationMenu(final Context context) {
        super(context);
    }
    
    @Override
    protected MenuItem addInternal(final int n, final int n2, final int n3, final CharSequence charSequence) {
        if (this.size() + 1 <= 5) {
            this.stopDispatchingItemsChanged();
            final MenuItem addInternal = super.addInternal(n, n2, n3, charSequence);
            if (addInternal instanceof MenuItemImpl) {
                ((MenuItemImpl)addInternal).setExclusiveCheckable(true);
            }
            this.startDispatchingItemsChanged();
            return addInternal;
        }
        throw new IllegalArgumentException("Maximum number of items supported by BottomNavigationView is 5. Limit can be checked with BottomNavigationView#getMaxItemCount()");
    }
    
    @Override
    public SubMenu addSubMenu(final int n, final int n2, final int n3, final CharSequence charSequence) {
        throw new UnsupportedOperationException("BottomNavigationView does not support submenus");
    }
}
