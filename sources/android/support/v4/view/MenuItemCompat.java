package android.support.v4.view;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.internal.view.SupportMenuItem;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

public final class MenuItemCompat {
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

    @RequiresApi(26)
    static class MenuItemCompatApi26Impl extends MenuItemCompatBaseImpl {
        MenuItemCompatApi26Impl() {
        }

        public int getAlphabeticModifiers(MenuItem menuItem) {
            return menuItem.getAlphabeticModifiers();
        }

        public CharSequence getContentDescription(MenuItem menuItem) {
            return menuItem.getContentDescription();
        }

        public ColorStateList getIconTintList(MenuItem menuItem) {
            return menuItem.getIconTintList();
        }

        public PorterDuff.Mode getIconTintMode(MenuItem menuItem) {
            return menuItem.getIconTintMode();
        }

        public int getNumericModifiers(MenuItem menuItem) {
            return menuItem.getNumericModifiers();
        }

        public CharSequence getTooltipText(MenuItem menuItem) {
            return menuItem.getTooltipText();
        }

        public void setAlphabeticShortcut(MenuItem menuItem, char c, int i) {
            menuItem.setAlphabeticShortcut(c, i);
        }

        public void setContentDescription(MenuItem menuItem, CharSequence charSequence) {
            menuItem.setContentDescription(charSequence);
        }

        public void setIconTintList(MenuItem menuItem, ColorStateList colorStateList) {
            menuItem.setIconTintList(colorStateList);
        }

        public void setIconTintMode(MenuItem menuItem, PorterDuff.Mode mode) {
            menuItem.setIconTintMode(mode);
        }

        public void setNumericShortcut(MenuItem menuItem, char c, int i) {
            menuItem.setNumericShortcut(c, i);
        }

        public void setShortcut(MenuItem menuItem, char c, char c2, int i, int i2) {
            menuItem.setShortcut(c, c2, i, i2);
        }

        public void setTooltipText(MenuItem menuItem, CharSequence charSequence) {
            menuItem.setTooltipText(charSequence);
        }
    }

    static class MenuItemCompatBaseImpl implements MenuVersionImpl {
        MenuItemCompatBaseImpl() {
        }

        public int getAlphabeticModifiers(MenuItem menuItem) {
            return 0;
        }

        public CharSequence getContentDescription(MenuItem menuItem) {
            return null;
        }

        public ColorStateList getIconTintList(MenuItem menuItem) {
            return null;
        }

        public PorterDuff.Mode getIconTintMode(MenuItem menuItem) {
            return null;
        }

        public int getNumericModifiers(MenuItem menuItem) {
            return 0;
        }

        public CharSequence getTooltipText(MenuItem menuItem) {
            return null;
        }

        public void setAlphabeticShortcut(MenuItem menuItem, char c, int i) {
        }

        public void setContentDescription(MenuItem menuItem, CharSequence charSequence) {
        }

        public void setIconTintList(MenuItem menuItem, ColorStateList colorStateList) {
        }

        public void setIconTintMode(MenuItem menuItem, PorterDuff.Mode mode) {
        }

        public void setNumericShortcut(MenuItem menuItem, char c, int i) {
        }

        public void setShortcut(MenuItem menuItem, char c, char c2, int i, int i2) {
        }

        public void setTooltipText(MenuItem menuItem, CharSequence charSequence) {
        }
    }

    interface MenuVersionImpl {
        int getAlphabeticModifiers(MenuItem menuItem);

        CharSequence getContentDescription(MenuItem menuItem);

        ColorStateList getIconTintList(MenuItem menuItem);

        PorterDuff.Mode getIconTintMode(MenuItem menuItem);

        int getNumericModifiers(MenuItem menuItem);

        CharSequence getTooltipText(MenuItem menuItem);

        void setAlphabeticShortcut(MenuItem menuItem, char c, int i);

        void setContentDescription(MenuItem menuItem, CharSequence charSequence);

        void setIconTintList(MenuItem menuItem, ColorStateList colorStateList);

        void setIconTintMode(MenuItem menuItem, PorterDuff.Mode mode);

        void setNumericShortcut(MenuItem menuItem, char c, int i);

        void setShortcut(MenuItem menuItem, char c, char c2, int i, int i2);

        void setTooltipText(MenuItem menuItem, CharSequence charSequence);
    }

    @Deprecated
    public interface OnActionExpandListener {
        boolean onMenuItemActionCollapse(MenuItem menuItem);

        boolean onMenuItemActionExpand(MenuItem menuItem);
    }

    static {
        if (Build.VERSION.SDK_INT < 26) {
            IMPL = new MenuItemCompatBaseImpl();
        } else {
            IMPL = new MenuItemCompatApi26Impl();
        }
    }

    private MenuItemCompat() {
    }

    @Deprecated
    public static boolean collapseActionView(MenuItem menuItem) {
        return menuItem.collapseActionView();
    }

    @Deprecated
    public static boolean expandActionView(MenuItem menuItem) {
        return menuItem.expandActionView();
    }

    public static ActionProvider getActionProvider(MenuItem menuItem) {
        if (menuItem instanceof SupportMenuItem) {
            return ((SupportMenuItem) menuItem).getSupportActionProvider();
        }
        Log.w(TAG, "getActionProvider: item does not implement SupportMenuItem; returning null");
        return null;
    }

    @Deprecated
    public static View getActionView(MenuItem menuItem) {
        return menuItem.getActionView();
    }

    public static int getAlphabeticModifiers(MenuItem menuItem) {
        return !(menuItem instanceof SupportMenuItem) ? IMPL.getAlphabeticModifiers(menuItem) : ((SupportMenuItem) menuItem).getAlphabeticModifiers();
    }

    public static CharSequence getContentDescription(MenuItem menuItem) {
        return !(menuItem instanceof SupportMenuItem) ? IMPL.getContentDescription(menuItem) : ((SupportMenuItem) menuItem).getContentDescription();
    }

    public static ColorStateList getIconTintList(MenuItem menuItem) {
        return !(menuItem instanceof SupportMenuItem) ? IMPL.getIconTintList(menuItem) : ((SupportMenuItem) menuItem).getIconTintList();
    }

    public static PorterDuff.Mode getIconTintMode(MenuItem menuItem) {
        return !(menuItem instanceof SupportMenuItem) ? IMPL.getIconTintMode(menuItem) : ((SupportMenuItem) menuItem).getIconTintMode();
    }

    public static int getNumericModifiers(MenuItem menuItem) {
        return !(menuItem instanceof SupportMenuItem) ? IMPL.getNumericModifiers(menuItem) : ((SupportMenuItem) menuItem).getNumericModifiers();
    }

    public static CharSequence getTooltipText(MenuItem menuItem) {
        return !(menuItem instanceof SupportMenuItem) ? IMPL.getTooltipText(menuItem) : ((SupportMenuItem) menuItem).getTooltipText();
    }

    @Deprecated
    public static boolean isActionViewExpanded(MenuItem menuItem) {
        return menuItem.isActionViewExpanded();
    }

    public static MenuItem setActionProvider(MenuItem menuItem, ActionProvider actionProvider) {
        if (menuItem instanceof SupportMenuItem) {
            return ((SupportMenuItem) menuItem).setSupportActionProvider(actionProvider);
        }
        Log.w(TAG, "setActionProvider: item does not implement SupportMenuItem; ignoring");
        return menuItem;
    }

    @Deprecated
    public static MenuItem setActionView(MenuItem menuItem, int i) {
        return menuItem.setActionView(i);
    }

    @Deprecated
    public static MenuItem setActionView(MenuItem menuItem, View view) {
        return menuItem.setActionView(view);
    }

    public static void setAlphabeticShortcut(MenuItem menuItem, char c, int i) {
        if (!(menuItem instanceof SupportMenuItem)) {
            IMPL.setAlphabeticShortcut(menuItem, c, i);
        } else {
            ((SupportMenuItem) menuItem).setAlphabeticShortcut(c, i);
        }
    }

    public static void setContentDescription(MenuItem menuItem, CharSequence charSequence) {
        if (!(menuItem instanceof SupportMenuItem)) {
            IMPL.setContentDescription(menuItem, charSequence);
        } else {
            ((SupportMenuItem) menuItem).setContentDescription(charSequence);
        }
    }

    public static void setIconTintList(MenuItem menuItem, ColorStateList colorStateList) {
        if (!(menuItem instanceof SupportMenuItem)) {
            IMPL.setIconTintList(menuItem, colorStateList);
        } else {
            ((SupportMenuItem) menuItem).setIconTintList(colorStateList);
        }
    }

    public static void setIconTintMode(MenuItem menuItem, PorterDuff.Mode mode) {
        if (!(menuItem instanceof SupportMenuItem)) {
            IMPL.setIconTintMode(menuItem, mode);
        } else {
            ((SupportMenuItem) menuItem).setIconTintMode(mode);
        }
    }

    public static void setNumericShortcut(MenuItem menuItem, char c, int i) {
        if (!(menuItem instanceof SupportMenuItem)) {
            IMPL.setNumericShortcut(menuItem, c, i);
        } else {
            ((SupportMenuItem) menuItem).setNumericShortcut(c, i);
        }
    }

    @Deprecated
    public static MenuItem setOnActionExpandListener(MenuItem menuItem, final OnActionExpandListener onActionExpandListener) {
        return menuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                return onActionExpandListener.onMenuItemActionCollapse(menuItem);
            }

            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                return onActionExpandListener.onMenuItemActionExpand(menuItem);
            }
        });
    }

    public static void setShortcut(MenuItem menuItem, char c, char c2, int i, int i2) {
        if (!(menuItem instanceof SupportMenuItem)) {
            IMPL.setShortcut(menuItem, c, c2, i, i2);
        } else {
            ((SupportMenuItem) menuItem).setShortcut(c, c2, i, i2);
        }
    }

    @Deprecated
    public static void setShowAsAction(MenuItem menuItem, int i) {
        menuItem.setShowAsAction(i);
    }

    public static void setTooltipText(MenuItem menuItem, CharSequence charSequence) {
        if (!(menuItem instanceof SupportMenuItem)) {
            IMPL.setTooltipText(menuItem, charSequence);
        } else {
            ((SupportMenuItem) menuItem).setTooltipText(charSequence);
        }
    }
}
