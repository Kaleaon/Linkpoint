// 
// Decompiled by Procyon v0.6.0
// 

package android.support.design.internal;

import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuItemImpl;
import android.content.Context;
import android.support.annotation.RestrictTo;
import android.support.v7.view.menu.SubMenuBuilder;

@RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
public class NavigationSubMenu extends SubMenuBuilder
{
    public NavigationSubMenu(final Context context, final NavigationMenu navigationMenu, final MenuItemImpl menuItemImpl) {
        super(context, navigationMenu, menuItemImpl);
    }
    
    @Override
    public void onItemsChanged(final boolean b) {
        super.onItemsChanged(b);
        ((MenuBuilder)this.getParentMenu()).onItemsChanged(b);
    }
}
