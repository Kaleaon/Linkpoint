// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v7.app;

import android.support.v7.view.WindowCallbackWrapper;
import android.widget.AdapterView$OnItemSelectedListener;
import android.widget.SpinnerAdapter;
import android.view.ViewGroup$LayoutParams;
import android.view.LayoutInflater;
import android.support.annotation.Nullable;
import android.graphics.drawable.Drawable;
import android.view.ViewGroup;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.content.res.Configuration;
import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPresenter;
import android.view.Menu;
import android.support.v7.widget.ToolbarWidgetWrapper;
import android.view.MenuItem;
import android.view.Window$Callback;
import java.util.ArrayList;
import android.support.v7.widget.Toolbar;
import android.support.v7.view.menu.ListMenuPresenter;
import android.support.v7.widget.DecorToolbar;

class ToolbarActionBar extends ActionBar
{
    DecorToolbar mDecorToolbar;
    private boolean mLastMenuVisibility;
    private ListMenuPresenter mListMenuPresenter;
    private boolean mMenuCallbackSet;
    private final Toolbar.OnMenuItemClickListener mMenuClicker;
    private final Runnable mMenuInvalidator;
    private ArrayList<OnMenuVisibilityListener> mMenuVisibilityListeners;
    boolean mToolbarMenuPrepared;
    Window$Callback mWindowCallback;
    
    ToolbarActionBar(final Toolbar toolbar, final CharSequence windowTitle, final Window$Callback window$Callback) {
        this.mMenuVisibilityListeners = new ArrayList<OnMenuVisibilityListener>();
        this.mMenuInvalidator = new Runnable() {
            @Override
            public void run() {
                ToolbarActionBar.this.populateOptionsMenu();
            }
        };
        this.mMenuClicker = new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final MenuItem menuItem) {
                return ToolbarActionBar.this.mWindowCallback.onMenuItemSelected(0, menuItem);
            }
        };
        this.mDecorToolbar = new ToolbarWidgetWrapper(toolbar, false);
        this.mWindowCallback = (Window$Callback)new ToolbarCallbackWrapper(window$Callback);
        this.mDecorToolbar.setWindowCallback(this.mWindowCallback);
        toolbar.setOnMenuItemClickListener(this.mMenuClicker);
        this.mDecorToolbar.setWindowTitle(windowTitle);
    }
    
    private Menu getMenu() {
        if (!this.mMenuCallbackSet) {
            this.mDecorToolbar.setMenuCallbacks(new ActionMenuPresenterCallback(), new MenuBuilderCallback());
            this.mMenuCallbackSet = true;
        }
        return this.mDecorToolbar.getMenu();
    }
    
    @Override
    public void addOnMenuVisibilityListener(final OnMenuVisibilityListener e) {
        this.mMenuVisibilityListeners.add(e);
    }
    
    @Override
    public void addTab(final Tab tab) {
        throw new UnsupportedOperationException("Tabs are not supported in toolbar action bars");
    }
    
    @Override
    public void addTab(final Tab tab, final int n) {
        throw new UnsupportedOperationException("Tabs are not supported in toolbar action bars");
    }
    
    @Override
    public void addTab(final Tab tab, final int n, final boolean b) {
        throw new UnsupportedOperationException("Tabs are not supported in toolbar action bars");
    }
    
    @Override
    public void addTab(final Tab tab, final boolean b) {
        throw new UnsupportedOperationException("Tabs are not supported in toolbar action bars");
    }
    
    @Override
    public boolean closeOptionsMenu() {
        return this.mDecorToolbar.hideOverflowMenu();
    }
    
    @Override
    public boolean collapseActionView() {
        if (!this.mDecorToolbar.hasExpandedActionView()) {
            return false;
        }
        this.mDecorToolbar.collapseActionView();
        return true;
    }
    
    @Override
    public void dispatchMenuVisibilityChanged(final boolean mLastMenuVisibility) {
        if (mLastMenuVisibility != this.mLastMenuVisibility) {
            this.mLastMenuVisibility = mLastMenuVisibility;
            for (int size = this.mMenuVisibilityListeners.size(), i = 0; i < size; ++i) {
                this.mMenuVisibilityListeners.get(i).onMenuVisibilityChanged(mLastMenuVisibility);
            }
        }
    }
    
    @Override
    public View getCustomView() {
        return this.mDecorToolbar.getCustomView();
    }
    
    @Override
    public int getDisplayOptions() {
        return this.mDecorToolbar.getDisplayOptions();
    }
    
    @Override
    public float getElevation() {
        return ViewCompat.getElevation((View)this.mDecorToolbar.getViewGroup());
    }
    
    @Override
    public int getHeight() {
        return this.mDecorToolbar.getHeight();
    }
    
    @Override
    public int getNavigationItemCount() {
        return 0;
    }
    
    @Override
    public int getNavigationMode() {
        return 0;
    }
    
    @Override
    public int getSelectedNavigationIndex() {
        return -1;
    }
    
    @Override
    public Tab getSelectedTab() {
        throw new UnsupportedOperationException("Tabs are not supported in toolbar action bars");
    }
    
    @Override
    public CharSequence getSubtitle() {
        return this.mDecorToolbar.getSubtitle();
    }
    
    @Override
    public Tab getTabAt(final int n) {
        throw new UnsupportedOperationException("Tabs are not supported in toolbar action bars");
    }
    
    @Override
    public int getTabCount() {
        return 0;
    }
    
    @Override
    public Context getThemedContext() {
        return this.mDecorToolbar.getContext();
    }
    
    @Override
    public CharSequence getTitle() {
        return this.mDecorToolbar.getTitle();
    }
    
    public Window$Callback getWrappedWindowCallback() {
        return this.mWindowCallback;
    }
    
    @Override
    public void hide() {
        this.mDecorToolbar.setVisibility(8);
    }
    
    @Override
    public boolean invalidateOptionsMenu() {
        this.mDecorToolbar.getViewGroup().removeCallbacks(this.mMenuInvalidator);
        ViewCompat.postOnAnimation((View)this.mDecorToolbar.getViewGroup(), this.mMenuInvalidator);
        return true;
    }
    
    @Override
    public boolean isShowing() {
        boolean b = false;
        if (this.mDecorToolbar.getVisibility() == 0) {
            b = true;
        }
        return b;
    }
    
    @Override
    public boolean isTitleTruncated() {
        return super.isTitleTruncated();
    }
    
    @Override
    public Tab newTab() {
        throw new UnsupportedOperationException("Tabs are not supported in toolbar action bars");
    }
    
    @Override
    public void onConfigurationChanged(final Configuration configuration) {
        super.onConfigurationChanged(configuration);
    }
    
    @Override
    void onDestroy() {
        this.mDecorToolbar.getViewGroup().removeCallbacks(this.mMenuInvalidator);
    }
    
    @Override
    public boolean onKeyShortcut(final int n, final KeyEvent keyEvent) {
        final Menu menu = this.getMenu();
        if (menu == null) {
            return false;
        }
        int deviceId;
        if (keyEvent == null) {
            deviceId = -1;
        }
        else {
            deviceId = keyEvent.getDeviceId();
        }
        menu.setQwertyMode(KeyCharacterMap.load(deviceId).getKeyboardType() != 1);
        return menu.performShortcut(n, keyEvent, 0);
    }
    
    @Override
    public boolean onMenuKeyEvent(final KeyEvent keyEvent) {
        if (keyEvent.getAction() == 1) {
            this.openOptionsMenu();
        }
        return true;
    }
    
    @Override
    public boolean openOptionsMenu() {
        return this.mDecorToolbar.showOverflowMenu();
    }
    
    void populateOptionsMenu() {
        MenuBuilder menuBuilder = null;
        final Menu menu = this.getMenu();
        Label_0049: {
            if (menu instanceof MenuBuilder) {
                break Label_0049;
            }
        Label_0018_Outer:
            while (true) {
                Label_0057: {
                    if (menuBuilder != null) {
                        break Label_0057;
                    }
                    try {
                        while (true) {
                            menu.clear();
                            if (!this.mWindowCallback.onCreatePanelMenu(0, menu) || !this.mWindowCallback.onPreparePanel(0, (View)null, menu)) {
                                menu.clear();
                            }
                            if (menuBuilder != null) {
                                menuBuilder.startDispatchingItemsChanged();
                            }
                            return;
                            menuBuilder = (MenuBuilder)menu;
                            continue Label_0018_Outer;
                            menuBuilder.stopDispatchingItemsChanged();
                            continue;
                        }
                    }
                    finally {
                        while (true) {
                            if (menuBuilder == null) {
                                break Label_0096;
                            }
                            menuBuilder.startDispatchingItemsChanged();
                            break Label_0096;
                            continue;
                        }
                    }
                }
                break;
            }
        }
    }
    
    @Override
    public void removeAllTabs() {
        throw new UnsupportedOperationException("Tabs are not supported in toolbar action bars");
    }
    
    @Override
    public void removeOnMenuVisibilityListener(final OnMenuVisibilityListener o) {
        this.mMenuVisibilityListeners.remove(o);
    }
    
    @Override
    public void removeTab(final Tab tab) {
        throw new UnsupportedOperationException("Tabs are not supported in toolbar action bars");
    }
    
    @Override
    public void removeTabAt(final int n) {
        throw new UnsupportedOperationException("Tabs are not supported in toolbar action bars");
    }
    
    public boolean requestFocus() {
        final ViewGroup viewGroup = this.mDecorToolbar.getViewGroup();
        if (viewGroup != null && !viewGroup.hasFocus()) {
            viewGroup.requestFocus();
            return true;
        }
        return false;
    }
    
    @Override
    public void selectTab(final Tab tab) {
        throw new UnsupportedOperationException("Tabs are not supported in toolbar action bars");
    }
    
    @Override
    public void setBackgroundDrawable(@Nullable final Drawable backgroundDrawable) {
        this.mDecorToolbar.setBackgroundDrawable(backgroundDrawable);
    }
    
    @Override
    public void setCustomView(final int n) {
        this.setCustomView(LayoutInflater.from(this.mDecorToolbar.getContext()).inflate(n, this.mDecorToolbar.getViewGroup(), false));
    }
    
    @Override
    public void setCustomView(final View view) {
        this.setCustomView(view, new LayoutParams(-2, -2));
    }
    
    @Override
    public void setCustomView(final View customView, final LayoutParams layoutParams) {
        if (customView != null) {
            customView.setLayoutParams((ViewGroup$LayoutParams)layoutParams);
        }
        this.mDecorToolbar.setCustomView(customView);
    }
    
    @Override
    public void setDefaultDisplayHomeAsUpEnabled(final boolean b) {
    }
    
    @Override
    public void setDisplayHomeAsUpEnabled(final boolean b) {
        int n = 0;
        if (b) {
            n = 4;
        }
        this.setDisplayOptions(n, 4);
    }
    
    @Override
    public void setDisplayOptions(final int n) {
        this.setDisplayOptions(n, -1);
    }
    
    @Override
    public void setDisplayOptions(final int n, final int n2) {
        this.mDecorToolbar.setDisplayOptions((this.mDecorToolbar.getDisplayOptions() & ~n2) | (n & n2));
    }
    
    @Override
    public void setDisplayShowCustomEnabled(final boolean b) {
        int n = 0;
        if (b) {
            n = 16;
        }
        this.setDisplayOptions(n, 16);
    }
    
    @Override
    public void setDisplayShowHomeEnabled(final boolean b) {
        int n = 0;
        if (b) {
            n = 2;
        }
        this.setDisplayOptions(n, 2);
    }
    
    @Override
    public void setDisplayShowTitleEnabled(final boolean b) {
        int n = 0;
        if (b) {
            n = 8;
        }
        this.setDisplayOptions(n, 8);
    }
    
    @Override
    public void setDisplayUseLogoEnabled(final boolean b) {
        int n = 0;
        if (b) {
            n = 1;
        }
        this.setDisplayOptions(n, 1);
    }
    
    @Override
    public void setElevation(final float n) {
        ViewCompat.setElevation((View)this.mDecorToolbar.getViewGroup(), n);
    }
    
    @Override
    public void setHomeActionContentDescription(final int navigationContentDescription) {
        this.mDecorToolbar.setNavigationContentDescription(navigationContentDescription);
    }
    
    @Override
    public void setHomeActionContentDescription(final CharSequence navigationContentDescription) {
        this.mDecorToolbar.setNavigationContentDescription(navigationContentDescription);
    }
    
    @Override
    public void setHomeAsUpIndicator(final int navigationIcon) {
        this.mDecorToolbar.setNavigationIcon(navigationIcon);
    }
    
    @Override
    public void setHomeAsUpIndicator(final Drawable navigationIcon) {
        this.mDecorToolbar.setNavigationIcon(navigationIcon);
    }
    
    @Override
    public void setHomeButtonEnabled(final boolean b) {
    }
    
    @Override
    public void setIcon(final int icon) {
        this.mDecorToolbar.setIcon(icon);
    }
    
    @Override
    public void setIcon(final Drawable icon) {
        this.mDecorToolbar.setIcon(icon);
    }
    
    @Override
    public void setListNavigationCallbacks(final SpinnerAdapter spinnerAdapter, final OnNavigationListener onNavigationListener) {
        this.mDecorToolbar.setDropdownParams(spinnerAdapter, (AdapterView$OnItemSelectedListener)new NavItemSelectedListener(onNavigationListener));
    }
    
    @Override
    public void setLogo(final int logo) {
        this.mDecorToolbar.setLogo(logo);
    }
    
    @Override
    public void setLogo(final Drawable logo) {
        this.mDecorToolbar.setLogo(logo);
    }
    
    @Override
    public void setNavigationMode(final int navigationMode) {
        if (navigationMode != 2) {
            this.mDecorToolbar.setNavigationMode(navigationMode);
            return;
        }
        throw new IllegalArgumentException("Tabs not supported in this configuration");
    }
    
    @Override
    public void setSelectedNavigationItem(final int dropdownSelectedPosition) {
        switch (this.mDecorToolbar.getNavigationMode()) {
            default: {
                throw new IllegalStateException("setSelectedNavigationIndex not valid for current navigation mode");
            }
            case 1: {
                this.mDecorToolbar.setDropdownSelectedPosition(dropdownSelectedPosition);
            }
        }
    }
    
    @Override
    public void setShowHideAnimationEnabled(final boolean b) {
    }
    
    @Override
    public void setSplitBackgroundDrawable(final Drawable drawable) {
    }
    
    @Override
    public void setStackedBackgroundDrawable(final Drawable drawable) {
    }
    
    @Override
    public void setSubtitle(final int n) {
        final DecorToolbar mDecorToolbar = this.mDecorToolbar;
        CharSequence text;
        if (n == 0) {
            text = null;
        }
        else {
            text = this.mDecorToolbar.getContext().getText(n);
        }
        mDecorToolbar.setSubtitle(text);
    }
    
    @Override
    public void setSubtitle(final CharSequence subtitle) {
        this.mDecorToolbar.setSubtitle(subtitle);
    }
    
    @Override
    public void setTitle(final int n) {
        final DecorToolbar mDecorToolbar = this.mDecorToolbar;
        CharSequence text;
        if (n == 0) {
            text = null;
        }
        else {
            text = this.mDecorToolbar.getContext().getText(n);
        }
        mDecorToolbar.setTitle(text);
    }
    
    @Override
    public void setTitle(final CharSequence title) {
        this.mDecorToolbar.setTitle(title);
    }
    
    @Override
    public void setWindowTitle(final CharSequence windowTitle) {
        this.mDecorToolbar.setWindowTitle(windowTitle);
    }
    
    @Override
    public void show() {
        this.mDecorToolbar.setVisibility(0);
    }
    
    private final class ActionMenuPresenterCallback implements Callback
    {
        private boolean mClosingActionMenu;
        
        ActionMenuPresenterCallback() {
        }
        
        @Override
        public void onCloseMenu(final MenuBuilder menuBuilder, final boolean b) {
            if (!this.mClosingActionMenu) {
                this.mClosingActionMenu = true;
                ToolbarActionBar.this.mDecorToolbar.dismissPopupMenus();
                if (ToolbarActionBar.this.mWindowCallback != null) {
                    ToolbarActionBar.this.mWindowCallback.onPanelClosed(108, (Menu)menuBuilder);
                }
                this.mClosingActionMenu = false;
            }
        }
        
        @Override
        public boolean onOpenSubMenu(final MenuBuilder menuBuilder) {
            if (ToolbarActionBar.this.mWindowCallback == null) {
                return false;
            }
            ToolbarActionBar.this.mWindowCallback.onMenuOpened(108, (Menu)menuBuilder);
            return true;
        }
    }
    
    private final class MenuBuilderCallback implements Callback
    {
        MenuBuilderCallback() {
        }
        
        @Override
        public boolean onMenuItemSelected(final MenuBuilder menuBuilder, final MenuItem menuItem) {
            return false;
        }
        
        @Override
        public void onMenuModeChange(final MenuBuilder menuBuilder) {
            if (ToolbarActionBar.this.mWindowCallback != null) {
                if (!ToolbarActionBar.this.mDecorToolbar.isOverflowMenuShowing()) {
                    if (ToolbarActionBar.this.mWindowCallback.onPreparePanel(0, (View)null, (Menu)menuBuilder)) {
                        ToolbarActionBar.this.mWindowCallback.onMenuOpened(108, (Menu)menuBuilder);
                    }
                }
                else {
                    ToolbarActionBar.this.mWindowCallback.onPanelClosed(108, (Menu)menuBuilder);
                }
            }
        }
    }
    
    private class ToolbarCallbackWrapper extends WindowCallbackWrapper
    {
        public ToolbarCallbackWrapper(final Window$Callback window$Callback) {
            super(window$Callback);
        }
        
        @Override
        public View onCreatePanelView(final int n) {
            if (n != 0) {
                return super.onCreatePanelView(n);
            }
            return new View(ToolbarActionBar.this.mDecorToolbar.getContext());
        }
        
        @Override
        public boolean onPreparePanel(final int n, final View view, final Menu menu) {
            final boolean onPreparePanel = super.onPreparePanel(n, view, menu);
            if (onPreparePanel && !ToolbarActionBar.this.mToolbarMenuPrepared) {
                ToolbarActionBar.this.mDecorToolbar.setMenuPrepared();
                ToolbarActionBar.this.mToolbarMenuPrepared = true;
            }
            return onPreparePanel;
        }
    }
}
