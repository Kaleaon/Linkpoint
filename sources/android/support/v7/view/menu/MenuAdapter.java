package android.support.v7.view.menu;

import android.support.annotation.RestrictTo;
import android.support.v7.appcompat.R;
import android.support.v7.view.menu.MenuView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import java.util.ArrayList;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
public class MenuAdapter extends BaseAdapter {
    static final int ITEM_LAYOUT = R.layout.abc_popup_menu_item_layout;
    MenuBuilder mAdapterMenu;
    private int mExpandedIndex = -1;
    private boolean mForceShowIcon;
    private final LayoutInflater mInflater;
    private final boolean mOverflowOnly;

    public MenuAdapter(MenuBuilder menuBuilder, LayoutInflater layoutInflater, boolean z) {
        this.mOverflowOnly = z;
        this.mInflater = layoutInflater;
        this.mAdapterMenu = menuBuilder;
        findExpandedIndex();
    }

    /* access modifiers changed from: package-private */
    public void findExpandedIndex() {
        MenuItemImpl expandedItem = this.mAdapterMenu.getExpandedItem();
        if (expandedItem != null) {
            ArrayList<MenuItemImpl> nonActionItems = this.mAdapterMenu.getNonActionItems();
            int size = nonActionItems.size();
            int i = 0;
            while (i < size) {
                if (nonActionItems.get(i) != expandedItem) {
                    i++;
                } else {
                    this.mExpandedIndex = i;
                    return;
                }
            }
        }
        this.mExpandedIndex = -1;
    }

    public MenuBuilder getAdapterMenu() {
        return this.mAdapterMenu;
    }

    public int getCount() {
        ArrayList<MenuItemImpl> visibleItems = !this.mOverflowOnly ? this.mAdapterMenu.getVisibleItems() : this.mAdapterMenu.getNonActionItems();
        return this.mExpandedIndex >= 0 ? visibleItems.size() - 1 : visibleItems.size();
    }

    public boolean getForceShowIcon() {
        return this.mForceShowIcon;
    }

    public MenuItemImpl getItem(int i) {
        ArrayList<MenuItemImpl> visibleItems = !this.mOverflowOnly ? this.mAdapterMenu.getVisibleItems() : this.mAdapterMenu.getNonActionItems();
        if (this.mExpandedIndex >= 0 && i >= this.mExpandedIndex) {
            i++;
        }
        return visibleItems.get(i);
    }

    public long getItemId(int i) {
        return (long) i;
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        View inflate = view != null ? view : this.mInflater.inflate(ITEM_LAYOUT, viewGroup, false);
        MenuView.ItemView itemView = (MenuView.ItemView) inflate;
        if (this.mForceShowIcon) {
            ((ListMenuItemView) inflate).setForceShowIcon(true);
        }
        itemView.initialize(getItem(i), 0);
        return inflate;
    }

    public void notifyDataSetChanged() {
        findExpandedIndex();
        super.notifyDataSetChanged();
    }

    public void setForceShowIcon(boolean z) {
        this.mForceShowIcon = z;
    }
}
