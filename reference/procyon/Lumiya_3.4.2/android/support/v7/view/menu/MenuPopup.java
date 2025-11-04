// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v7.view.menu;

import android.widget.PopupWindow$OnDismissListener;
import android.widget.AdapterView;
import android.support.annotation.Nullable;
import android.support.annotation.NonNull;
import android.widget.HeaderViewListAdapter;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.view.View$MeasureSpec;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.graphics.Rect;
import android.widget.AdapterView$OnItemClickListener;

abstract class MenuPopup implements ShowableListMenu, MenuPresenter, AdapterView$OnItemClickListener
{
    private Rect mEpicenterBounds;
    
    protected static int measureIndividualMenuWidth(final ListAdapter listAdapter, final ViewGroup viewGroup, final Context context, final int n) {
        final int measureSpec = View$MeasureSpec.makeMeasureSpec(0, 0);
        final int measureSpec2 = View$MeasureSpec.makeMeasureSpec(0, 0);
        final int count = listAdapter.getCount();
        int i = 0;
        int n2 = 0;
        final View view = null;
        int n3 = 0;
        Object o = viewGroup;
        View view2 = view;
        while (i < count) {
            final int itemViewType = listAdapter.getItemViewType(i);
            if (itemViewType != n2) {
                view2 = null;
                n2 = itemViewType;
            }
            if (o == null) {
                o = new FrameLayout(context);
            }
            view2 = listAdapter.getView(i, view2, (ViewGroup)o);
            view2.measure(measureSpec, measureSpec2);
            final int measuredWidth = view2.getMeasuredWidth();
            if (measuredWidth >= n) {
                return n;
            }
            if (measuredWidth > n3) {
                n3 = measuredWidth;
            }
            ++i;
        }
        return n3;
    }
    
    protected static boolean shouldPreserveIconSpacing(final MenuBuilder menuBuilder) {
        boolean b = false;
        for (int size = menuBuilder.size(), i = 0; i < size; ++i) {
            final MenuItem item = menuBuilder.getItem(i);
            if (item.isVisible() && item.getIcon() != null) {
                b = true;
                break;
            }
        }
        return b;
    }
    
    protected static MenuAdapter toMenuAdapter(final ListAdapter listAdapter) {
        if (!(listAdapter instanceof HeaderViewListAdapter)) {
            return (MenuAdapter)listAdapter;
        }
        return (MenuAdapter)((HeaderViewListAdapter)listAdapter).getWrappedAdapter();
    }
    
    public abstract void addMenu(final MenuBuilder p0);
    
    protected boolean closeMenuOnSubMenuOpened() {
        return true;
    }
    
    @Override
    public boolean collapseItemActionView(final MenuBuilder menuBuilder, final MenuItemImpl menuItemImpl) {
        return false;
    }
    
    @Override
    public boolean expandItemActionView(final MenuBuilder menuBuilder, final MenuItemImpl menuItemImpl) {
        return false;
    }
    
    public Rect getEpicenterBounds() {
        return this.mEpicenterBounds;
    }
    
    @Override
    public int getId() {
        return 0;
    }
    
    @Override
    public MenuView getMenuView(final ViewGroup viewGroup) {
        throw new UnsupportedOperationException("MenuPopups manage their own views");
    }
    
    @Override
    public void initForMenu(@NonNull final Context context, @Nullable final MenuBuilder menuBuilder) {
    }
    
    public void onItemClick(final AdapterView<?> adapterView, final View view, int n, final long n2) {
        final int n3 = 0;
        final ListAdapter listAdapter = (ListAdapter)adapterView.getAdapter();
        final MenuBuilder mAdapterMenu = toMenuAdapter(listAdapter).mAdapterMenu;
        final MenuItem menuItem = (MenuItem)listAdapter.getItem(n);
        n = n3;
        if (!this.closeMenuOnSubMenuOpened()) {
            n = 4;
        }
        mAdapterMenu.performItemAction(menuItem, this, n);
    }
    
    public abstract void setAnchorView(final View p0);
    
    public void setEpicenterBounds(final Rect mEpicenterBounds) {
        this.mEpicenterBounds = mEpicenterBounds;
    }
    
    public abstract void setForceShowIcon(final boolean p0);
    
    public abstract void setGravity(final int p0);
    
    public abstract void setHorizontalOffset(final int p0);
    
    public abstract void setOnDismissListener(final PopupWindow$OnDismissListener p0);
    
    public abstract void setShowTitle(final boolean p0);
    
    public abstract void setVerticalOffset(final int p0);
}
