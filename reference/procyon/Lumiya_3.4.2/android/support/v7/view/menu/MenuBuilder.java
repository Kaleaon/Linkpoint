// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v7.view.menu;

import android.support.v4.view.ActionProvider;
import android.support.annotation.NonNull;
import java.util.Collection;
import android.view.KeyCharacterMap$KeyData;
import android.view.KeyEvent;
import android.view.SubMenu;
import java.util.List;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.Intent;
import android.content.ComponentName;
import android.view.MenuItem;
import android.support.v7.appcompat.R;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import java.util.Iterator;
import android.content.res.Resources;
import java.lang.ref.WeakReference;
import java.util.concurrent.CopyOnWriteArrayList;
import android.view.View;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.util.SparseArray;
import android.view.ContextMenu$ContextMenuInfo;
import android.content.Context;
import java.util.ArrayList;
import android.support.annotation.RestrictTo;
import android.support.v4.internal.view.SupportMenu;

@RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
public class MenuBuilder implements SupportMenu
{
    private static final String ACTION_VIEW_STATES_KEY = "android:menu:actionviewstates";
    private static final String EXPANDED_ACTION_VIEW_ID = "android:menu:expandedactionview";
    private static final String PRESENTER_KEY = "android:menu:presenters";
    private static final String TAG = "MenuBuilder";
    private static final int[] sCategoryToOrder;
    private ArrayList<MenuItemImpl> mActionItems;
    private Callback mCallback;
    private final Context mContext;
    private ContextMenu$ContextMenuInfo mCurrentMenuInfo;
    private int mDefaultShowAsAction;
    private MenuItemImpl mExpandedItem;
    private SparseArray<Parcelable> mFrozenViewStates;
    Drawable mHeaderIcon;
    CharSequence mHeaderTitle;
    View mHeaderView;
    private boolean mIsActionItemsStale;
    private boolean mIsClosing;
    private boolean mIsVisibleItemsStale;
    private ArrayList<MenuItemImpl> mItems;
    private boolean mItemsChangedWhileDispatchPrevented;
    private ArrayList<MenuItemImpl> mNonActionItems;
    private boolean mOptionalIconsVisible;
    private boolean mOverrideVisibleItems;
    private CopyOnWriteArrayList<WeakReference<MenuPresenter>> mPresenters;
    private boolean mPreventDispatchingItemsChanged;
    private boolean mQwertyMode;
    private final Resources mResources;
    private boolean mShortcutsVisible;
    private boolean mStructureChangedWhileDispatchPrevented;
    private ArrayList<MenuItemImpl> mTempShortcutItemList;
    private ArrayList<MenuItemImpl> mVisibleItems;
    
    static {
        sCategoryToOrder = new int[] { 1, 4, 5, 3, 2, 0 };
    }
    
    public MenuBuilder(final Context mContext) {
        this.mDefaultShowAsAction = 0;
        this.mPreventDispatchingItemsChanged = false;
        this.mItemsChangedWhileDispatchPrevented = false;
        this.mStructureChangedWhileDispatchPrevented = false;
        this.mOptionalIconsVisible = false;
        this.mIsClosing = false;
        this.mTempShortcutItemList = new ArrayList<MenuItemImpl>();
        this.mPresenters = new CopyOnWriteArrayList<WeakReference<MenuPresenter>>();
        this.mContext = mContext;
        this.mResources = mContext.getResources();
        this.mItems = new ArrayList<MenuItemImpl>();
        this.mVisibleItems = new ArrayList<MenuItemImpl>();
        this.mIsVisibleItemsStale = true;
        this.mActionItems = new ArrayList<MenuItemImpl>();
        this.mNonActionItems = new ArrayList<MenuItemImpl>();
        this.setShortcutsVisibleInner(this.mIsActionItemsStale = true);
    }
    
    private MenuItemImpl createNewMenuItem(final int n, final int n2, final int n3, final int n4, final CharSequence charSequence, final int n5) {
        return new MenuItemImpl(this, n, n2, n3, n4, charSequence, n5);
    }
    
    private void dispatchPresenterUpdate(final boolean b) {
        if (!this.mPresenters.isEmpty()) {
            this.stopDispatchingItemsChanged();
            for (final WeakReference o : this.mPresenters) {
                final MenuPresenter menuPresenter = (MenuPresenter)o.get();
                if (menuPresenter != null) {
                    menuPresenter.updateMenuView(b);
                }
                else {
                    this.mPresenters.remove(o);
                }
            }
            this.startDispatchingItemsChanged();
        }
    }
    
    private void dispatchRestoreInstanceState(final Bundle bundle) {
        final SparseArray sparseParcelableArray = bundle.getSparseParcelableArray("android:menu:presenters");
        if (sparseParcelableArray != null && !this.mPresenters.isEmpty()) {
            for (final WeakReference o : this.mPresenters) {
                final MenuPresenter menuPresenter = (MenuPresenter)o.get();
                if (menuPresenter != null) {
                    final int id = menuPresenter.getId();
                    if (id <= 0) {
                        continue;
                    }
                    final Parcelable parcelable = (Parcelable)sparseParcelableArray.get(id);
                    if (parcelable == null) {
                        continue;
                    }
                    menuPresenter.onRestoreInstanceState(parcelable);
                }
                else {
                    this.mPresenters.remove(o);
                }
            }
        }
    }
    
    private void dispatchSaveInstanceState(final Bundle bundle) {
        if (!this.mPresenters.isEmpty()) {
            final SparseArray sparseArray = new SparseArray();
            for (final WeakReference o : this.mPresenters) {
                final MenuPresenter menuPresenter = (MenuPresenter)o.get();
                if (menuPresenter != null) {
                    final int id = menuPresenter.getId();
                    if (id <= 0) {
                        continue;
                    }
                    final Parcelable onSaveInstanceState = menuPresenter.onSaveInstanceState();
                    if (onSaveInstanceState == null) {
                        continue;
                    }
                    sparseArray.put(id, (Object)onSaveInstanceState);
                }
                else {
                    this.mPresenters.remove(o);
                }
            }
            bundle.putSparseParcelableArray("android:menu:presenters", sparseArray);
        }
    }
    
    private boolean dispatchSubMenuSelected(final SubMenuBuilder subMenuBuilder, final MenuPresenter menuPresenter) {
        boolean b = false;
        if (!this.mPresenters.isEmpty()) {
            if (menuPresenter != null) {
                b = menuPresenter.onSubMenuSelected(subMenuBuilder);
            }
            for (final WeakReference o : this.mPresenters) {
                final MenuPresenter menuPresenter2 = (MenuPresenter)o.get();
                if (menuPresenter2 != null) {
                    if (b) {
                        continue;
                    }
                    b = menuPresenter2.onSubMenuSelected(subMenuBuilder);
                }
                else {
                    this.mPresenters.remove(o);
                }
            }
            return b;
        }
        return false;
    }
    
    private static int findInsertIndex(final ArrayList<MenuItemImpl> list, final int n) {
        for (int i = list.size() - 1; i >= 0; --i) {
            if (((MenuItemImpl)list.get(i)).getOrdering() <= n) {
                return i + 1;
            }
        }
        return 0;
    }
    
    private static int getOrdering(final int n) {
        final int n2 = (0xFFFF0000 & n) >> 16;
        if (n2 >= 0 && n2 < MenuBuilder.sCategoryToOrder.length) {
            return MenuBuilder.sCategoryToOrder[n2] << 16 | (0xFFFF & n);
        }
        throw new IllegalArgumentException("order does not contain a valid category.");
    }
    
    private void removeItemAtInt(final int index, final boolean b) {
        if (index >= 0 && index < this.mItems.size()) {
            this.mItems.remove(index);
            if (b) {
                this.onItemsChanged(true);
            }
        }
    }
    
    private void setHeaderInternal(final int n, final CharSequence mHeaderTitle, final int n2, final Drawable mHeaderIcon, final View mHeaderView) {
        final Resources resources = this.getResources();
        if (mHeaderView == null) {
            if (n <= 0) {
                if (mHeaderTitle != null) {
                    this.mHeaderTitle = mHeaderTitle;
                }
            }
            else {
                this.mHeaderTitle = resources.getText(n);
            }
            if (n2 <= 0) {
                if (mHeaderIcon != null) {
                    this.mHeaderIcon = mHeaderIcon;
                }
            }
            else {
                this.mHeaderIcon = ContextCompat.getDrawable(this.getContext(), n2);
            }
            this.mHeaderView = null;
        }
        else {
            this.mHeaderView = mHeaderView;
            this.mHeaderTitle = null;
            this.mHeaderIcon = null;
        }
        this.onItemsChanged(false);
    }
    
    private void setShortcutsVisibleInner(final boolean b) {
        final boolean b2 = true;
        boolean mShortcutsVisible = false;
        Label_0008: {
            if (b && this.mResources.getConfiguration().keyboard != 1) {
                mShortcutsVisible = b2;
                if (this.mResources.getBoolean(R.bool.abc_config_showMenuShortcutsWhenKeyboardPresent)) {
                    break Label_0008;
                }
            }
            mShortcutsVisible = false;
        }
        this.mShortcutsVisible = mShortcutsVisible;
    }
    
    public MenuItem add(final int n) {
        return this.addInternal(0, 0, 0, this.mResources.getString(n));
    }
    
    public MenuItem add(final int n, final int n2, final int n3, final int n4) {
        return this.addInternal(n, n2, n3, this.mResources.getString(n4));
    }
    
    public MenuItem add(final int n, final int n2, final int n3, final CharSequence charSequence) {
        return this.addInternal(n, n2, n3, charSequence);
    }
    
    public MenuItem add(final CharSequence charSequence) {
        return this.addInternal(0, 0, 0, charSequence);
    }
    
    public int addIntentOptions(final int n, final int n2, final int n3, final ComponentName componentName, final Intent[] array, final Intent intent, int i, final MenuItem[] array2) {
        final PackageManager packageManager = this.mContext.getPackageManager();
        final List queryIntentActivityOptions = packageManager.queryIntentActivityOptions(componentName, array, intent, 0);
        int size;
        if (queryIntentActivityOptions == null) {
            size = 0;
        }
        else {
            size = queryIntentActivityOptions.size();
        }
        if ((i & 0x1) == 0x0) {
            this.removeGroup(n);
        }
        ResolveInfo resolveInfo;
        Intent intent2;
        Intent intent3;
        MenuItem setIntent;
        for (i = 0; i < size; ++i) {
            resolveInfo = queryIntentActivityOptions.get(i);
            if (resolveInfo.specificIndex >= 0) {
                intent2 = array[resolveInfo.specificIndex];
            }
            else {
                intent2 = intent;
            }
            intent3 = new Intent(intent2);
            intent3.setComponent(new ComponentName(resolveInfo.activityInfo.applicationInfo.packageName, resolveInfo.activityInfo.name));
            setIntent = this.add(n, n2, n3, resolveInfo.loadLabel(packageManager)).setIcon(resolveInfo.loadIcon(packageManager)).setIntent(intent3);
            if (array2 != null && resolveInfo.specificIndex >= 0) {
                array2[resolveInfo.specificIndex] = setIntent;
            }
        }
        return size;
    }
    
    protected MenuItem addInternal(final int n, final int n2, final int n3, final CharSequence charSequence) {
        final int ordering = getOrdering(n3);
        final MenuItemImpl newMenuItem = this.createNewMenuItem(n, n2, n3, ordering, charSequence, this.mDefaultShowAsAction);
        if (this.mCurrentMenuInfo != null) {
            newMenuItem.setMenuInfo(this.mCurrentMenuInfo);
        }
        this.mItems.add(findInsertIndex(this.mItems, ordering), newMenuItem);
        this.onItemsChanged(true);
        return (MenuItem)newMenuItem;
    }
    
    public void addMenuPresenter(final MenuPresenter menuPresenter) {
        this.addMenuPresenter(menuPresenter, this.mContext);
    }
    
    public void addMenuPresenter(final MenuPresenter referent, final Context context) {
        this.mPresenters.add(new WeakReference<MenuPresenter>(referent));
        referent.initForMenu(context, this);
        this.mIsActionItemsStale = true;
    }
    
    public SubMenu addSubMenu(final int n) {
        return this.addSubMenu(0, 0, 0, this.mResources.getString(n));
    }
    
    public SubMenu addSubMenu(final int n, final int n2, final int n3, final int n4) {
        return this.addSubMenu(n, n2, n3, this.mResources.getString(n4));
    }
    
    public SubMenu addSubMenu(final int n, final int n2, final int n3, final CharSequence charSequence) {
        final MenuItemImpl menuItemImpl = (MenuItemImpl)this.addInternal(n, n2, n3, charSequence);
        final SubMenuBuilder subMenu = new SubMenuBuilder(this.mContext, this, menuItemImpl);
        menuItemImpl.setSubMenu(subMenu);
        return (SubMenu)subMenu;
    }
    
    public SubMenu addSubMenu(final CharSequence charSequence) {
        return this.addSubMenu(0, 0, 0, charSequence);
    }
    
    public void changeMenuMode() {
        if (this.mCallback != null) {
            this.mCallback.onMenuModeChange(this);
        }
    }
    
    public void clear() {
        if (this.mExpandedItem != null) {
            this.collapseItemActionView(this.mExpandedItem);
        }
        this.mItems.clear();
        this.onItemsChanged(true);
    }
    
    public void clearAll() {
        this.mPreventDispatchingItemsChanged = true;
        this.clear();
        this.clearHeader();
        this.mPreventDispatchingItemsChanged = false;
        this.mItemsChangedWhileDispatchPrevented = false;
        this.mStructureChangedWhileDispatchPrevented = false;
        this.onItemsChanged(true);
    }
    
    public void clearHeader() {
        this.mHeaderIcon = null;
        this.mHeaderTitle = null;
        this.mHeaderView = null;
        this.onItemsChanged(false);
    }
    
    public void close() {
        this.close(true);
    }
    
    public final void close(final boolean b) {
        if (!this.mIsClosing) {
            this.mIsClosing = true;
            for (final WeakReference o : this.mPresenters) {
                final MenuPresenter menuPresenter = (MenuPresenter)o.get();
                if (menuPresenter != null) {
                    menuPresenter.onCloseMenu(this, b);
                }
                else {
                    this.mPresenters.remove(o);
                }
            }
            this.mIsClosing = false;
        }
    }
    
    public boolean collapseItemActionView(final MenuItemImpl menuItemImpl) {
        if (!this.mPresenters.isEmpty() && this.mExpandedItem == menuItemImpl) {
            this.stopDispatchingItemsChanged();
            final Iterator<WeakReference<MenuPresenter>> iterator = this.mPresenters.iterator();
            boolean collapseItemActionView = false;
            while (iterator.hasNext()) {
                final WeakReference o = iterator.next();
                final MenuPresenter menuPresenter = (MenuPresenter)o.get();
                if (menuPresenter != null) {
                    collapseItemActionView = menuPresenter.collapseItemActionView(this, menuItemImpl);
                    if (!collapseItemActionView) {
                        continue;
                    }
                    break;
                }
                else {
                    this.mPresenters.remove(o);
                }
            }
            this.startDispatchingItemsChanged();
            if (collapseItemActionView) {
                this.mExpandedItem = null;
            }
            return collapseItemActionView;
        }
        return false;
    }
    
    boolean dispatchMenuItemSelected(final MenuBuilder menuBuilder, final MenuItem menuItem) {
        boolean b = false;
        if (this.mCallback != null && this.mCallback.onMenuItemSelected(menuBuilder, menuItem)) {
            b = true;
        }
        return b;
    }
    
    public boolean expandItemActionView(final MenuItemImpl mExpandedItem) {
        if (!this.mPresenters.isEmpty()) {
            this.stopDispatchingItemsChanged();
            final Iterator<WeakReference<MenuPresenter>> iterator = this.mPresenters.iterator();
            boolean expandItemActionView = false;
            while (iterator.hasNext()) {
                final WeakReference o = iterator.next();
                final MenuPresenter menuPresenter = (MenuPresenter)o.get();
                if (menuPresenter != null) {
                    expandItemActionView = menuPresenter.expandItemActionView(this, mExpandedItem);
                    if (!expandItemActionView) {
                        continue;
                    }
                    break;
                }
                else {
                    this.mPresenters.remove(o);
                }
            }
            this.startDispatchingItemsChanged();
            if (expandItemActionView) {
                this.mExpandedItem = mExpandedItem;
            }
            return expandItemActionView;
        }
        return false;
    }
    
    public int findGroupIndex(final int n) {
        return this.findGroupIndex(n, 0);
    }
    
    public int findGroupIndex(final int n, int i) {
        final int size = this.size();
        if (i < 0) {
            i = 0;
        }
        while (i < size) {
            if (this.mItems.get(i).getGroupId() == n) {
                return i;
            }
            ++i;
        }
        return -1;
    }
    
    public MenuItem findItem(final int n) {
        for (int size = this.size(), i = 0; i < size; ++i) {
            final MenuItemImpl menuItemImpl = this.mItems.get(i);
            if (menuItemImpl.getItemId() == n) {
                return (MenuItem)menuItemImpl;
            }
            if (menuItemImpl.hasSubMenu()) {
                final MenuItem item = menuItemImpl.getSubMenu().findItem(n);
                if (item != null) {
                    return item;
                }
            }
        }
        return null;
    }
    
    public int findItemIndex(final int n) {
        for (int size = this.size(), i = 0; i < size; ++i) {
            if (this.mItems.get(i).getItemId() == n) {
                return i;
            }
        }
        return -1;
    }
    
    MenuItemImpl findItemWithShortcutForKey(final int n, final KeyEvent keyEvent) {
        final ArrayList<MenuItemImpl> mTempShortcutItemList = this.mTempShortcutItemList;
        mTempShortcutItemList.clear();
        this.findItemsWithShortcutForKey(mTempShortcutItemList, n, keyEvent);
        if (mTempShortcutItemList.isEmpty()) {
            return null;
        }
        final int metaState = keyEvent.getMetaState();
        final KeyCharacterMap$KeyData keyCharacterMap$KeyData = new KeyCharacterMap$KeyData();
        keyEvent.getKeyData(keyCharacterMap$KeyData);
        final int size = mTempShortcutItemList.size();
        if (size != 1) {
            final boolean qwertyMode = this.isQwertyMode();
            int i = 0;
            while (i < size) {
                final MenuItemImpl menuItemImpl = mTempShortcutItemList.get(i);
                char c;
                if (!qwertyMode) {
                    c = menuItemImpl.getNumericShortcut();
                }
                else {
                    c = menuItemImpl.getAlphabeticShortcut();
                }
                if (c != keyCharacterMap$KeyData.meta[0] || (metaState & 0x2) != 0x0) {
                    if (c != keyCharacterMap$KeyData.meta[2] || (metaState & 0x2) == 0x0) {
                        if (!qwertyMode || c != '\b' || n != 67) {
                            ++i;
                            continue;
                        }
                    }
                }
                return menuItemImpl;
            }
            return null;
        }
        return (MenuItemImpl)mTempShortcutItemList.get(0);
    }
    
    void findItemsWithShortcutForKey(final List<MenuItemImpl> list, final int n, final KeyEvent keyEvent) {
        final boolean qwertyMode = this.isQwertyMode();
        final int modifiers = keyEvent.getModifiers();
        final KeyCharacterMap$KeyData keyCharacterMap$KeyData = new KeyCharacterMap$KeyData();
        if (!keyEvent.getKeyData(keyCharacterMap$KeyData) && n != 67) {
            return;
        }
        for (int size = this.mItems.size(), i = 0; i < size; ++i) {
            final MenuItemImpl menuItemImpl = this.mItems.get(i);
            if (menuItemImpl.hasSubMenu()) {
                ((MenuBuilder)menuItemImpl.getSubMenu()).findItemsWithShortcutForKey(list, n, keyEvent);
            }
            char c;
            if (!qwertyMode) {
                c = menuItemImpl.getNumericShortcut();
            }
            else {
                c = menuItemImpl.getAlphabeticShortcut();
            }
            int n2;
            if (!qwertyMode) {
                n2 = menuItemImpl.getNumericModifiers();
            }
            else {
                n2 = menuItemImpl.getAlphabeticModifiers();
            }
            boolean b;
            if ((modifiers & 0x1100F) != (n2 & 0x1100F)) {
                b = false;
            }
            else {
                b = true;
            }
            if (b && c != '\0') {
                if (c != keyCharacterMap$KeyData.meta[0] && c != keyCharacterMap$KeyData.meta[2]) {
                    if (!qwertyMode || c != '\b') {
                        continue;
                    }
                    if (n != 67) {
                        continue;
                    }
                }
                if (menuItemImpl.isEnabled()) {
                    list.add(menuItemImpl);
                }
            }
        }
    }
    
    public void flagActionItems() {
        final ArrayList<MenuItemImpl> visibleItems = this.getVisibleItems();
        if (this.mIsActionItemsStale) {
            final Iterator<WeakReference<MenuPresenter>> iterator = this.mPresenters.iterator();
            boolean b = false;
            while (iterator.hasNext()) {
                final WeakReference o = iterator.next();
                final MenuPresenter menuPresenter = (MenuPresenter)o.get();
                if (menuPresenter != null) {
                    b |= menuPresenter.flagActionItems();
                }
                else {
                    this.mPresenters.remove(o);
                }
            }
            if (!b) {
                this.mActionItems.clear();
                this.mNonActionItems.clear();
                this.mNonActionItems.addAll(this.getVisibleItems());
            }
            else {
                this.mActionItems.clear();
                this.mNonActionItems.clear();
                for (int size = visibleItems.size(), i = 0; i < size; ++i) {
                    final MenuItemImpl menuItemImpl = visibleItems.get(i);
                    if (!menuItemImpl.isActionButton()) {
                        this.mNonActionItems.add(menuItemImpl);
                    }
                    else {
                        this.mActionItems.add(menuItemImpl);
                    }
                }
            }
            this.mIsActionItemsStale = false;
        }
    }
    
    public ArrayList<MenuItemImpl> getActionItems() {
        this.flagActionItems();
        return this.mActionItems;
    }
    
    protected String getActionViewStatesKey() {
        return "android:menu:actionviewstates";
    }
    
    public Context getContext() {
        return this.mContext;
    }
    
    public MenuItemImpl getExpandedItem() {
        return this.mExpandedItem;
    }
    
    public Drawable getHeaderIcon() {
        return this.mHeaderIcon;
    }
    
    public CharSequence getHeaderTitle() {
        return this.mHeaderTitle;
    }
    
    public View getHeaderView() {
        return this.mHeaderView;
    }
    
    public MenuItem getItem(final int index) {
        return (MenuItem)this.mItems.get(index);
    }
    
    public ArrayList<MenuItemImpl> getNonActionItems() {
        this.flagActionItems();
        return this.mNonActionItems;
    }
    
    boolean getOptionalIconsVisible() {
        return this.mOptionalIconsVisible;
    }
    
    Resources getResources() {
        return this.mResources;
    }
    
    public MenuBuilder getRootMenu() {
        return this;
    }
    
    @NonNull
    public ArrayList<MenuItemImpl> getVisibleItems() {
        if (this.mIsVisibleItemsStale) {
            this.mVisibleItems.clear();
            for (int size = this.mItems.size(), i = 0; i < size; ++i) {
                final MenuItemImpl e = this.mItems.get(i);
                if (e.isVisible()) {
                    this.mVisibleItems.add(e);
                }
            }
            this.mIsVisibleItemsStale = false;
            this.mIsActionItemsStale = true;
            return this.mVisibleItems;
        }
        return this.mVisibleItems;
    }
    
    public boolean hasVisibleItems() {
        if (!this.mOverrideVisibleItems) {
            for (int size = this.size(), i = 0; i < size; ++i) {
                if (this.mItems.get(i).isVisible()) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }
    
    boolean isQwertyMode() {
        return this.mQwertyMode;
    }
    
    public boolean isShortcutKey(final int n, final KeyEvent keyEvent) {
        return this.findItemWithShortcutForKey(n, keyEvent) != null;
    }
    
    public boolean isShortcutsVisible() {
        return this.mShortcutsVisible;
    }
    
    void onItemActionRequestChanged(final MenuItemImpl menuItemImpl) {
        this.onItemsChanged(this.mIsActionItemsStale = true);
    }
    
    void onItemVisibleChanged(final MenuItemImpl menuItemImpl) {
        this.onItemsChanged(this.mIsVisibleItemsStale = true);
    }
    
    public void onItemsChanged(final boolean b) {
        if (this.mPreventDispatchingItemsChanged) {
            this.mItemsChangedWhileDispatchPrevented = true;
            if (b) {
                this.mStructureChangedWhileDispatchPrevented = true;
            }
        }
        else {
            if (b) {
                this.mIsVisibleItemsStale = true;
                this.mIsActionItemsStale = true;
            }
            this.dispatchPresenterUpdate(b);
        }
    }
    
    public boolean performIdentifierAction(final int n, final int n2) {
        return this.performItemAction(this.findItem(n), n2);
    }
    
    public boolean performItemAction(final MenuItem menuItem, final int n) {
        return this.performItemAction(menuItem, null, n);
    }
    
    public boolean performItemAction(final MenuItem menuItem, final MenuPresenter menuPresenter, final int n) {
        final MenuItemImpl menuItemImpl = (MenuItemImpl)menuItem;
        if (menuItemImpl != null && menuItemImpl.isEnabled()) {
            boolean invoke = menuItemImpl.invoke();
            final ActionProvider supportActionProvider = menuItemImpl.getSupportActionProvider();
            boolean b;
            if (supportActionProvider != null && supportActionProvider.hasSubMenu()) {
                b = true;
            }
            else {
                b = false;
            }
            if (!menuItemImpl.hasCollapsibleActionView()) {
                if (!menuItemImpl.hasSubMenu() && !b) {
                    if ((n & 0x1) == 0x0) {
                        this.close(true);
                    }
                }
                else {
                    if ((n & 0x4) == 0x0) {
                        this.close(false);
                    }
                    if (!menuItemImpl.hasSubMenu()) {
                        menuItemImpl.setSubMenu(new SubMenuBuilder(this.getContext(), this, menuItemImpl));
                    }
                    final SubMenuBuilder subMenuBuilder = (SubMenuBuilder)menuItemImpl.getSubMenu();
                    if (b) {
                        supportActionProvider.onPrepareSubMenu((SubMenu)subMenuBuilder);
                    }
                    invoke |= this.dispatchSubMenuSelected(subMenuBuilder, menuPresenter);
                    if (!invoke) {
                        this.close(true);
                    }
                }
            }
            else {
                final boolean b2 = invoke |= menuItemImpl.expandActionView();
                if (b2) {
                    this.close(true);
                    invoke = b2;
                }
            }
            return invoke;
        }
        return false;
    }
    
    public boolean performShortcut(final int n, final KeyEvent keyEvent, final int n2) {
        boolean performItemAction = false;
        final MenuItemImpl itemWithShortcutForKey = this.findItemWithShortcutForKey(n, keyEvent);
        if (itemWithShortcutForKey != null) {
            performItemAction = this.performItemAction((MenuItem)itemWithShortcutForKey, n2);
        }
        if ((n2 & 0x2) != 0x0) {
            this.close(true);
        }
        return performItemAction;
    }
    
    public void removeGroup(final int n) {
        final int groupIndex = this.findGroupIndex(n);
        if (groupIndex >= 0) {
            for (int size = this.mItems.size(), n2 = 0; n2 < size - groupIndex && this.mItems.get(groupIndex).getGroupId() == n; ++n2) {
                this.removeItemAtInt(groupIndex, false);
            }
            this.onItemsChanged(true);
        }
    }
    
    public void removeItem(final int n) {
        this.removeItemAtInt(this.findItemIndex(n), true);
    }
    
    public void removeItemAt(final int n) {
        this.removeItemAtInt(n, true);
    }
    
    public void removeMenuPresenter(final MenuPresenter menuPresenter) {
        for (final WeakReference o : this.mPresenters) {
            final MenuPresenter menuPresenter2 = (MenuPresenter)o.get();
            if (menuPresenter2 != null && menuPresenter2 != menuPresenter) {
                continue;
            }
            this.mPresenters.remove(o);
        }
    }
    
    public void restoreActionViewStates(final Bundle bundle) {
        if (bundle != null) {
            final SparseArray sparseParcelableArray = bundle.getSparseParcelableArray(this.getActionViewStatesKey());
            for (int size = this.size(), i = 0; i < size; ++i) {
                final MenuItem item = this.getItem(i);
                final View actionView = item.getActionView();
                if (actionView != null && actionView.getId() != -1) {
                    actionView.restoreHierarchyState(sparseParcelableArray);
                }
                if (item.hasSubMenu()) {
                    ((SubMenuBuilder)item.getSubMenu()).restoreActionViewStates(bundle);
                }
            }
            final int int1 = bundle.getInt("android:menu:expandedactionview");
            if (int1 > 0) {
                final MenuItem item2 = this.findItem(int1);
                if (item2 != null) {
                    item2.expandActionView();
                }
            }
        }
    }
    
    public void restorePresenterStates(final Bundle bundle) {
        this.dispatchRestoreInstanceState(bundle);
    }
    
    public void saveActionViewStates(final Bundle bundle) {
        final int size = this.size();
        int i = 0;
        SparseArray sparseArray = null;
        while (i < size) {
            final MenuItem item = this.getItem(i);
            final View actionView = item.getActionView();
            if (actionView != null && actionView.getId() != -1) {
                if (sparseArray == null) {
                    sparseArray = new SparseArray();
                }
                actionView.saveHierarchyState(sparseArray);
                if (item.isActionViewExpanded()) {
                    bundle.putInt("android:menu:expandedactionview", item.getItemId());
                }
            }
            if (item.hasSubMenu()) {
                ((SubMenuBuilder)item.getSubMenu()).saveActionViewStates(bundle);
            }
            ++i;
        }
        if (sparseArray != null) {
            bundle.putSparseParcelableArray(this.getActionViewStatesKey(), sparseArray);
        }
    }
    
    public void savePresenterStates(final Bundle bundle) {
        this.dispatchSaveInstanceState(bundle);
    }
    
    public void setCallback(final Callback mCallback) {
        this.mCallback = mCallback;
    }
    
    public void setCurrentMenuInfo(final ContextMenu$ContextMenuInfo mCurrentMenuInfo) {
        this.mCurrentMenuInfo = mCurrentMenuInfo;
    }
    
    public MenuBuilder setDefaultShowAsAction(final int mDefaultShowAsAction) {
        this.mDefaultShowAsAction = mDefaultShowAsAction;
        return this;
    }
    
    void setExclusiveItemChecked(final MenuItem menuItem) {
        final int groupId = menuItem.getGroupId();
        final int size = this.mItems.size();
        this.stopDispatchingItemsChanged();
        for (int i = 0; i < size; ++i) {
            final MenuItemImpl menuItemImpl = this.mItems.get(i);
            if (menuItemImpl.getGroupId() == groupId && menuItemImpl.isExclusiveCheckable() && menuItemImpl.isCheckable()) {
                menuItemImpl.setCheckedInt(menuItemImpl == menuItem);
            }
        }
        this.startDispatchingItemsChanged();
    }
    
    public void setGroupCheckable(final int n, final boolean checkable, final boolean exclusiveCheckable) {
        for (int size = this.mItems.size(), i = 0; i < size; ++i) {
            final MenuItemImpl menuItemImpl = this.mItems.get(i);
            if (menuItemImpl.getGroupId() == n) {
                menuItemImpl.setExclusiveCheckable(exclusiveCheckable);
                menuItemImpl.setCheckable(checkable);
            }
        }
    }
    
    public void setGroupEnabled(final int n, final boolean enabled) {
        for (int size = this.mItems.size(), i = 0; i < size; ++i) {
            final MenuItemImpl menuItemImpl = this.mItems.get(i);
            if (menuItemImpl.getGroupId() == n) {
                menuItemImpl.setEnabled(enabled);
            }
        }
    }
    
    public void setGroupVisible(final int n, final boolean visibleInt) {
        final int size = this.mItems.size();
        int i = 0;
        int n2 = 0;
        while (i < size) {
            final MenuItemImpl menuItemImpl = this.mItems.get(i);
            if (menuItemImpl.getGroupId() == n) {
                if (menuItemImpl.setVisibleInt(visibleInt)) {
                    n2 = 1;
                }
            }
            ++i;
        }
        if (n2 != 0) {
            this.onItemsChanged(true);
        }
    }
    
    protected MenuBuilder setHeaderIconInt(final int n) {
        this.setHeaderInternal(0, null, n, null, null);
        return this;
    }
    
    protected MenuBuilder setHeaderIconInt(final Drawable drawable) {
        this.setHeaderInternal(0, null, 0, drawable, null);
        return this;
    }
    
    protected MenuBuilder setHeaderTitleInt(final int n) {
        this.setHeaderInternal(n, null, 0, null, null);
        return this;
    }
    
    protected MenuBuilder setHeaderTitleInt(final CharSequence charSequence) {
        this.setHeaderInternal(0, charSequence, 0, null, null);
        return this;
    }
    
    protected MenuBuilder setHeaderViewInt(final View view) {
        this.setHeaderInternal(0, null, 0, null, view);
        return this;
    }
    
    public void setOptionalIconsVisible(final boolean mOptionalIconsVisible) {
        this.mOptionalIconsVisible = mOptionalIconsVisible;
    }
    
    public void setOverrideVisibleItems(final boolean mOverrideVisibleItems) {
        this.mOverrideVisibleItems = mOverrideVisibleItems;
    }
    
    public void setQwertyMode(final boolean mQwertyMode) {
        this.mQwertyMode = mQwertyMode;
        this.onItemsChanged(false);
    }
    
    public void setShortcutsVisible(final boolean shortcutsVisibleInner) {
        if (this.mShortcutsVisible != shortcutsVisibleInner) {
            this.setShortcutsVisibleInner(shortcutsVisibleInner);
            this.onItemsChanged(false);
        }
    }
    
    public int size() {
        return this.mItems.size();
    }
    
    public void startDispatchingItemsChanged() {
        this.mPreventDispatchingItemsChanged = false;
        if (this.mItemsChangedWhileDispatchPrevented) {
            this.mItemsChangedWhileDispatchPrevented = false;
            this.onItemsChanged(this.mStructureChangedWhileDispatchPrevented);
        }
    }
    
    public void stopDispatchingItemsChanged() {
        if (!this.mPreventDispatchingItemsChanged) {
            this.mPreventDispatchingItemsChanged = true;
            this.mItemsChangedWhileDispatchPrevented = false;
            this.mStructureChangedWhileDispatchPrevented = false;
        }
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    public interface Callback
    {
        boolean onMenuItemSelected(final MenuBuilder p0, final MenuItem p1);
        
        void onMenuModeChange(final MenuBuilder p0);
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    public interface ItemInvoker
    {
        boolean invokeItem(final MenuItemImpl p0);
    }
}
