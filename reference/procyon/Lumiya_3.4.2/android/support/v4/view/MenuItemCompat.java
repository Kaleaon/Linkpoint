// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.view;

import android.support.annotation.RequiresApi;
import android.view.MenuItem$OnActionExpandListener;
import android.graphics.PorterDuff$Mode;
import android.content.res.ColorStateList;
import android.view.View;
import android.util.Log;
import android.support.v4.internal.view.SupportMenuItem;
import android.view.MenuItem;
import android.os.Build$VERSION;

public final class MenuItemCompat
{
    static final MenuVersionImpl IMPL;
    @Deprecated
    public static final int SHOW_AS_ACTION_ALWAYS = 2;
    @Deprecated
    public static final int SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW = 8;
    @Deprecated
    public static final int SHOW_AS_ACTION_IF_ROOM = 1;
    @Deprecated
    public static final int SHOW_AS_ACTION_NEVER = 0;
    @Deprecated
    public static final int SHOW_AS_ACTION_WITH_TEXT = 4;
    private static final String TAG = "MenuItemCompat";
    
    static {
        if (Build$VERSION.SDK_INT < 26) {
            IMPL = (MenuVersionImpl)new MenuItemCompatBaseImpl();
        }
        else {
            IMPL = (MenuVersionImpl)new MenuItemCompatApi26Impl();
        }
    }
    
    private MenuItemCompat() {
    }
    
    @Deprecated
    public static boolean collapseActionView(final MenuItem menuItem) {
        return menuItem.collapseActionView();
    }
    
    @Deprecated
    public static boolean expandActionView(final MenuItem menuItem) {
        return menuItem.expandActionView();
    }
    
    public static ActionProvider getActionProvider(final MenuItem menuItem) {
        if (!(menuItem instanceof SupportMenuItem)) {
            Log.w("MenuItemCompat", "getActionProvider: item does not implement SupportMenuItem; returning null");
            return null;
        }
        return ((SupportMenuItem)menuItem).getSupportActionProvider();
    }
    
    @Deprecated
    public static View getActionView(final MenuItem menuItem) {
        return menuItem.getActionView();
    }
    
    public static int getAlphabeticModifiers(final MenuItem menuItem) {
        if (!(menuItem instanceof SupportMenuItem)) {
            return MenuItemCompat.IMPL.getAlphabeticModifiers(menuItem);
        }
        return ((SupportMenuItem)menuItem).getAlphabeticModifiers();
    }
    
    public static CharSequence getContentDescription(final MenuItem menuItem) {
        if (!(menuItem instanceof SupportMenuItem)) {
            return MenuItemCompat.IMPL.getContentDescription(menuItem);
        }
        return ((SupportMenuItem)menuItem).getContentDescription();
    }
    
    public static ColorStateList getIconTintList(final MenuItem menuItem) {
        if (!(menuItem instanceof SupportMenuItem)) {
            return MenuItemCompat.IMPL.getIconTintList(menuItem);
        }
        return ((SupportMenuItem)menuItem).getIconTintList();
    }
    
    public static PorterDuff$Mode getIconTintMode(final MenuItem menuItem) {
        if (!(menuItem instanceof SupportMenuItem)) {
            return MenuItemCompat.IMPL.getIconTintMode(menuItem);
        }
        return ((SupportMenuItem)menuItem).getIconTintMode();
    }
    
    public static int getNumericModifiers(final MenuItem menuItem) {
        if (!(menuItem instanceof SupportMenuItem)) {
            return MenuItemCompat.IMPL.getNumericModifiers(menuItem);
        }
        return ((SupportMenuItem)menuItem).getNumericModifiers();
    }
    
    public static CharSequence getTooltipText(final MenuItem menuItem) {
        if (!(menuItem instanceof SupportMenuItem)) {
            return MenuItemCompat.IMPL.getTooltipText(menuItem);
        }
        return ((SupportMenuItem)menuItem).getTooltipText();
    }
    
    @Deprecated
    public static boolean isActionViewExpanded(final MenuItem menuItem) {
        return menuItem.isActionViewExpanded();
    }
    
    public static MenuItem setActionProvider(final MenuItem menuItem, final ActionProvider supportActionProvider) {
        if (!(menuItem instanceof SupportMenuItem)) {
            Log.w("MenuItemCompat", "setActionProvider: item does not implement SupportMenuItem; ignoring");
            return menuItem;
        }
        return (MenuItem)((SupportMenuItem)menuItem).setSupportActionProvider(supportActionProvider);
    }
    
    @Deprecated
    public static MenuItem setActionView(final MenuItem menuItem, final int actionView) {
        return menuItem.setActionView(actionView);
    }
    
    @Deprecated
    public static MenuItem setActionView(final MenuItem menuItem, final View actionView) {
        return menuItem.setActionView(actionView);
    }
    
    public static void setAlphabeticShortcut(final MenuItem menuItem, final char c, final int n) {
        if (!(menuItem instanceof SupportMenuItem)) {
            MenuItemCompat.IMPL.setAlphabeticShortcut(menuItem, c, n);
        }
        else {
            ((SupportMenuItem)menuItem).setAlphabeticShortcut(c, n);
        }
    }
    
    public static void setContentDescription(final MenuItem menuItem, final CharSequence contentDescription) {
        if (!(menuItem instanceof SupportMenuItem)) {
            MenuItemCompat.IMPL.setContentDescription(menuItem, contentDescription);
        }
        else {
            ((SupportMenuItem)menuItem).setContentDescription(contentDescription);
        }
    }
    
    public static void setIconTintList(final MenuItem menuItem, final ColorStateList iconTintList) {
        if (!(menuItem instanceof SupportMenuItem)) {
            MenuItemCompat.IMPL.setIconTintList(menuItem, iconTintList);
        }
        else {
            ((SupportMenuItem)menuItem).setIconTintList(iconTintList);
        }
    }
    
    public static void setIconTintMode(final MenuItem menuItem, final PorterDuff$Mode iconTintMode) {
        if (!(menuItem instanceof SupportMenuItem)) {
            MenuItemCompat.IMPL.setIconTintMode(menuItem, iconTintMode);
        }
        else {
            ((SupportMenuItem)menuItem).setIconTintMode(iconTintMode);
        }
    }
    
    public static void setNumericShortcut(final MenuItem menuItem, final char c, final int n) {
        if (!(menuItem instanceof SupportMenuItem)) {
            MenuItemCompat.IMPL.setNumericShortcut(menuItem, c, n);
        }
        else {
            ((SupportMenuItem)menuItem).setNumericShortcut(c, n);
        }
    }
    
    @Deprecated
    public static MenuItem setOnActionExpandListener(final MenuItem menuItem, final OnActionExpandListener onActionExpandListener) {
        return menuItem.setOnActionExpandListener((MenuItem$OnActionExpandListener)new MenuItem$OnActionExpandListener() {
            public boolean onMenuItemActionCollapse(final MenuItem menuItem) {
                return onActionExpandListener.onMenuItemActionCollapse(menuItem);
            }
            
            public boolean onMenuItemActionExpand(final MenuItem menuItem) {
                return onActionExpandListener.onMenuItemActionExpand(menuItem);
            }
        });
    }
    
    public static void setShortcut(final MenuItem menuItem, final char c, final char c2, final int n, final int n2) {
        if (!(menuItem instanceof SupportMenuItem)) {
            MenuItemCompat.IMPL.setShortcut(menuItem, c, c2, n, n2);
        }
        else {
            ((SupportMenuItem)menuItem).setShortcut(c, c2, n, n2);
        }
    }
    
    @Deprecated
    public static void setShowAsAction(final MenuItem menuItem, final int showAsAction) {
        menuItem.setShowAsAction(showAsAction);
    }
    
    public static void setTooltipText(final MenuItem menuItem, final CharSequence tooltipText) {
        if (!(menuItem instanceof SupportMenuItem)) {
            MenuItemCompat.IMPL.setTooltipText(menuItem, tooltipText);
        }
        else {
            ((SupportMenuItem)menuItem).setTooltipText(tooltipText);
        }
    }
    
    @RequiresApi(26)
    static class MenuItemCompatApi26Impl extends MenuItemCompatBaseImpl
    {
        @Override
        public int getAlphabeticModifiers(final MenuItem menuItem) {
            return menuItem.getAlphabeticModifiers();
        }
        
        @Override
        public CharSequence getContentDescription(final MenuItem menuItem) {
            return menuItem.getContentDescription();
        }
        
        @Override
        public ColorStateList getIconTintList(final MenuItem menuItem) {
            return menuItem.getIconTintList();
        }
        
        @Override
        public PorterDuff$Mode getIconTintMode(final MenuItem menuItem) {
            return menuItem.getIconTintMode();
        }
        
        @Override
        public int getNumericModifiers(final MenuItem menuItem) {
            return menuItem.getNumericModifiers();
        }
        
        @Override
        public CharSequence getTooltipText(final MenuItem menuItem) {
            return menuItem.getTooltipText();
        }
        
        @Override
        public void setAlphabeticShortcut(final MenuItem menuItem, final char c, final int n) {
            menuItem.setAlphabeticShortcut(c, n);
        }
        
        @Override
        public void setContentDescription(final MenuItem menuItem, final CharSequence contentDescription) {
            menuItem.setContentDescription(contentDescription);
        }
        
        @Override
        public void setIconTintList(final MenuItem menuItem, final ColorStateList iconTintList) {
            menuItem.setIconTintList(iconTintList);
        }
        
        @Override
        public void setIconTintMode(final MenuItem menuItem, final PorterDuff$Mode iconTintMode) {
            menuItem.setIconTintMode(iconTintMode);
        }
        
        @Override
        public void setNumericShortcut(final MenuItem menuItem, final char c, final int n) {
            menuItem.setNumericShortcut(c, n);
        }
        
        @Override
        public void setShortcut(final MenuItem menuItem, final char c, final char c2, final int n, final int n2) {
            menuItem.setShortcut(c, c2, n, n2);
        }
        
        @Override
        public void setTooltipText(final MenuItem menuItem, final CharSequence tooltipText) {
            menuItem.setTooltipText(tooltipText);
        }
    }
    
    static class MenuItemCompatBaseImpl implements MenuVersionImpl
    {
        @Override
        public int getAlphabeticModifiers(final MenuItem menuItem) {
            return 0;
        }
        
        @Override
        public CharSequence getContentDescription(final MenuItem menuItem) {
            return null;
        }
        
        @Override
        public ColorStateList getIconTintList(final MenuItem menuItem) {
            return null;
        }
        
        @Override
        public PorterDuff$Mode getIconTintMode(final MenuItem menuItem) {
            return null;
        }
        
        @Override
        public int getNumericModifiers(final MenuItem menuItem) {
            return 0;
        }
        
        @Override
        public CharSequence getTooltipText(final MenuItem menuItem) {
            return null;
        }
        
        @Override
        public void setAlphabeticShortcut(final MenuItem menuItem, final char c, final int n) {
        }
        
        @Override
        public void setContentDescription(final MenuItem menuItem, final CharSequence charSequence) {
        }
        
        @Override
        public void setIconTintList(final MenuItem menuItem, final ColorStateList list) {
        }
        
        @Override
        public void setIconTintMode(final MenuItem menuItem, final PorterDuff$Mode porterDuff$Mode) {
        }
        
        @Override
        public void setNumericShortcut(final MenuItem menuItem, final char c, final int n) {
        }
        
        @Override
        public void setShortcut(final MenuItem menuItem, final char c, final char c2, final int n, final int n2) {
        }
        
        @Override
        public void setTooltipText(final MenuItem menuItem, final CharSequence charSequence) {
        }
    }
    
    interface MenuVersionImpl
    {
        int getAlphabeticModifiers(final MenuItem p0);
        
        CharSequence getContentDescription(final MenuItem p0);
        
        ColorStateList getIconTintList(final MenuItem p0);
        
        PorterDuff$Mode getIconTintMode(final MenuItem p0);
        
        int getNumericModifiers(final MenuItem p0);
        
        CharSequence getTooltipText(final MenuItem p0);
        
        void setAlphabeticShortcut(final MenuItem p0, final char p1, final int p2);
        
        void setContentDescription(final MenuItem p0, final CharSequence p1);
        
        void setIconTintList(final MenuItem p0, final ColorStateList p1);
        
        void setIconTintMode(final MenuItem p0, final PorterDuff$Mode p1);
        
        void setNumericShortcut(final MenuItem p0, final char p1, final int p2);
        
        void setShortcut(final MenuItem p0, final char p1, final char p2, final int p3, final int p4);
        
        void setTooltipText(final MenuItem p0, final CharSequence p1);
    }
    
    @Deprecated
    public interface OnActionExpandListener
    {
        boolean onMenuItemActionCollapse(final MenuItem p0);
        
        boolean onMenuItemActionExpand(final MenuItem p0);
    }
}
