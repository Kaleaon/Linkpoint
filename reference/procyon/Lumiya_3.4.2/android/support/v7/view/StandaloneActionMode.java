// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v7.view;

import android.support.v7.view.menu.MenuPopupHelper;
import android.view.MenuItem;
import android.support.v7.view.menu.SubMenuBuilder;
import android.view.MenuInflater;
import android.view.Menu;
import android.view.View;
import java.lang.ref.WeakReference;
import android.support.v7.widget.ActionBarContextView;
import android.content.Context;
import android.support.annotation.RestrictTo;
import android.support.v7.view.menu.MenuBuilder;

@RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
public class StandaloneActionMode extends ActionMode implements MenuBuilder.Callback
{
    private ActionMode.Callback mCallback;
    private Context mContext;
    private ActionBarContextView mContextView;
    private WeakReference<View> mCustomView;
    private boolean mFinished;
    private boolean mFocusable;
    private MenuBuilder mMenu;
    
    public StandaloneActionMode(final Context mContext, final ActionBarContextView mContextView, final ActionMode.Callback mCallback, final boolean mFocusable) {
        this.mContext = mContext;
        this.mContextView = mContextView;
        this.mCallback = mCallback;
        (this.mMenu = new MenuBuilder(mContextView.getContext()).setDefaultShowAsAction(1)).setCallback((MenuBuilder.Callback)this);
        this.mFocusable = mFocusable;
    }
    
    @Override
    public void finish() {
        if (!this.mFinished) {
            this.mFinished = true;
            this.mContextView.sendAccessibilityEvent(32);
            this.mCallback.onDestroyActionMode(this);
        }
    }
    
    @Override
    public View getCustomView() {
        View view = null;
        if (this.mCustomView != null) {
            view = this.mCustomView.get();
        }
        return view;
    }
    
    @Override
    public Menu getMenu() {
        return (Menu)this.mMenu;
    }
    
    @Override
    public MenuInflater getMenuInflater() {
        return new SupportMenuInflater(this.mContextView.getContext());
    }
    
    @Override
    public CharSequence getSubtitle() {
        return this.mContextView.getSubtitle();
    }
    
    @Override
    public CharSequence getTitle() {
        return this.mContextView.getTitle();
    }
    
    @Override
    public void invalidate() {
        this.mCallback.onPrepareActionMode(this, (Menu)this.mMenu);
    }
    
    @Override
    public boolean isTitleOptional() {
        return this.mContextView.isTitleOptional();
    }
    
    @Override
    public boolean isUiFocusable() {
        return this.mFocusable;
    }
    
    public void onCloseMenu(final MenuBuilder menuBuilder, final boolean b) {
    }
    
    public void onCloseSubMenu(final SubMenuBuilder subMenuBuilder) {
    }
    
    @Override
    public boolean onMenuItemSelected(final MenuBuilder menuBuilder, final MenuItem menuItem) {
        return this.mCallback.onActionItemClicked(this, menuItem);
    }
    
    @Override
    public void onMenuModeChange(final MenuBuilder menuBuilder) {
        this.invalidate();
        this.mContextView.showOverflowMenu();
    }
    
    public boolean onSubMenuSelected(final SubMenuBuilder subMenuBuilder) {
        if (subMenuBuilder.hasVisibleItems()) {
            new MenuPopupHelper(this.mContextView.getContext(), subMenuBuilder).show();
            return true;
        }
        return true;
    }
    
    @Override
    public void setCustomView(final View view) {
        final WeakReference<View> weakReference = null;
        this.mContextView.setCustomView(view);
        WeakReference<View> mCustomView;
        if (view == null) {
            mCustomView = weakReference;
        }
        else {
            mCustomView = new WeakReference<View>(view);
        }
        this.mCustomView = mCustomView;
    }
    
    @Override
    public void setSubtitle(final int n) {
        this.setSubtitle(this.mContext.getString(n));
    }
    
    @Override
    public void setSubtitle(final CharSequence subtitle) {
        this.mContextView.setSubtitle(subtitle);
    }
    
    @Override
    public void setTitle(final int n) {
        this.setTitle(this.mContext.getString(n));
    }
    
    @Override
    public void setTitle(final CharSequence title) {
        this.mContextView.setTitle(title);
    }
    
    @Override
    public void setTitleOptionalHint(final boolean b) {
        super.setTitleOptionalHint(b);
        this.mContextView.setTitleOptional(b);
    }
}
