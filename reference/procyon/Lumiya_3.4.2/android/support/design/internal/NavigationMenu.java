// 
// Decompiled by Procyon v0.6.0
// 

package android.support.design.internal;

import android.support.v7.view.menu.SubMenuBuilder;
import android.support.v7.view.menu.MenuItemImpl;
import android.view.SubMenu;
import android.content.Context;
import android.support.annotation.RestrictTo;
import android.support.v7.view.menu.MenuBuilder;

@RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
public class NavigationMenu extends MenuBuilder
{
    public NavigationMenu(final Context context) {
        super(context);
    }
    
    @Override
    public SubMenu addSubMenu(final int n, final int n2, final int n3, final CharSequence charSequence) {
        final MenuItemImpl menuItemImpl = (MenuItemImpl)this.addInternal(n, n2, n3, charSequence);
        final NavigationSubMenu subMenu = new NavigationSubMenu(this.getContext(), this, menuItemImpl);
        menuItemImpl.setSubMenu(subMenu);
        return (SubMenu)subMenu;
    }
}
