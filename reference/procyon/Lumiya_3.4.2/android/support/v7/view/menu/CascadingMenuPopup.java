// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v7.view.menu;

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import android.support.v4.view.GravityCompat;
import android.os.Parcelable;
import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.FrameLayout;
import android.os.Build$VERSION;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.graphics.Rect;
import android.support.v4.view.ViewCompat;
import android.support.annotation.Nullable;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.HeaderViewListAdapter;
import android.widget.AdapterView$OnItemClickListener;
import android.util.AttributeSet;
import android.support.v7.widget.MenuPopupWindow;
import android.content.res.Resources;
import android.support.v7.appcompat.R;
import android.os.SystemClock;
import android.view.MenuItem;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.LinkedList;
import android.support.annotation.StyleRes;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.view.ViewTreeObserver;
import android.os.Handler;
import java.util.List;
import android.support.v7.widget.MenuItemHoverListener;
import android.view.ViewTreeObserver$OnGlobalLayoutListener;
import android.content.Context;
import android.view.View$OnAttachStateChangeListener;
import android.view.View;
import android.widget.PopupWindow$OnDismissListener;
import android.view.View$OnKeyListener;

final class CascadingMenuPopup extends MenuPopup implements MenuPresenter, View$OnKeyListener, PopupWindow$OnDismissListener
{
    static final int HORIZ_POSITION_LEFT = 0;
    static final int HORIZ_POSITION_RIGHT = 1;
    static final int SUBMENU_TIMEOUT_MS = 200;
    private View mAnchorView;
    private final View$OnAttachStateChangeListener mAttachStateChangeListener;
    private final Context mContext;
    private int mDropDownGravity;
    private boolean mForceShowIcon;
    private final ViewTreeObserver$OnGlobalLayoutListener mGlobalLayoutListener;
    private boolean mHasXOffset;
    private boolean mHasYOffset;
    private int mLastPosition;
    private final MenuItemHoverListener mMenuItemHoverListener;
    private final int mMenuMaxWidth;
    private PopupWindow$OnDismissListener mOnDismissListener;
    private final boolean mOverflowOnly;
    private final List<MenuBuilder> mPendingMenus;
    private final int mPopupStyleAttr;
    private final int mPopupStyleRes;
    private Callback mPresenterCallback;
    private int mRawDropDownGravity;
    boolean mShouldCloseImmediately;
    private boolean mShowTitle;
    final List<CascadingMenuInfo> mShowingMenus;
    View mShownAnchorView;
    final Handler mSubMenuHoverHandler;
    private ViewTreeObserver mTreeObserver;
    private int mXOffset;
    private int mYOffset;
    
    public CascadingMenuPopup(@NonNull final Context mContext, @NonNull final View mAnchorView, @AttrRes final int mPopupStyleAttr, @StyleRes final int mPopupStyleRes, final boolean mOverflowOnly) {
        this.mPendingMenus = new LinkedList<MenuBuilder>();
        this.mShowingMenus = new ArrayList<CascadingMenuInfo>();
        this.mGlobalLayoutListener = (ViewTreeObserver$OnGlobalLayoutListener)new ViewTreeObserver$OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                if (CascadingMenuPopup.this.isShowing() && CascadingMenuPopup.this.mShowingMenus.size() > 0 && !((CascadingMenuInfo)CascadingMenuPopup.this.mShowingMenus.get(0)).window.isModal()) {
                    final View mShownAnchorView = CascadingMenuPopup.this.mShownAnchorView;
                    if (mShownAnchorView != null && mShownAnchorView.isShown()) {
                        final Iterator<CascadingMenuInfo> iterator = CascadingMenuPopup.this.mShowingMenus.iterator();
                        while (iterator.hasNext()) {
                            ((CascadingMenuInfo)iterator.next()).window.show();
                        }
                    }
                    else {
                        CascadingMenuPopup.this.dismiss();
                    }
                }
            }
        };
        this.mAttachStateChangeListener = (View$OnAttachStateChangeListener)new View$OnAttachStateChangeListener() {
            public void onViewAttachedToWindow(final View view) {
            }
            
            public void onViewDetachedFromWindow(final View view) {
                if (CascadingMenuPopup.this.mTreeObserver != null) {
                    if (!CascadingMenuPopup.this.mTreeObserver.isAlive()) {
                        CascadingMenuPopup.this.mTreeObserver = view.getViewTreeObserver();
                    }
                    CascadingMenuPopup.this.mTreeObserver.removeGlobalOnLayoutListener(CascadingMenuPopup.this.mGlobalLayoutListener);
                }
                view.removeOnAttachStateChangeListener((View$OnAttachStateChangeListener)this);
            }
        };
        this.mMenuItemHoverListener = new MenuItemHoverListener() {
            @Override
            public void onItemHoverEnter(@NonNull final MenuBuilder menuBuilder, @NonNull final MenuItem menuItem) {
                CascadingMenuPopup.this.mSubMenuHoverHandler.removeCallbacksAndMessages((Object)null);
                int i = 0;
                while (true) {
                    while (i < CascadingMenuPopup.this.mShowingMenus.size()) {
                        if (menuBuilder != ((CascadingMenuInfo)CascadingMenuPopup.this.mShowingMenus.get(i)).menu) {
                            ++i;
                        }
                        else {
                            if (i != -1) {
                                CascadingMenuInfo cascadingMenuInfo;
                                if (++i >= CascadingMenuPopup.this.mShowingMenus.size()) {
                                    cascadingMenuInfo = null;
                                }
                                else {
                                    cascadingMenuInfo = (CascadingMenuInfo)CascadingMenuPopup.this.mShowingMenus.get(i);
                                }
                                CascadingMenuPopup.this.mSubMenuHoverHandler.postAtTime((Runnable)new Runnable() {
                                    @Override
                                    public void run() {
                                        if (cascadingMenuInfo != null) {
                                            CascadingMenuPopup.this.mShouldCloseImmediately = true;
                                            cascadingMenuInfo.menu.close(false);
                                            CascadingMenuPopup.this.mShouldCloseImmediately = false;
                                        }
                                        if (menuItem.isEnabled() && menuItem.hasSubMenu()) {
                                            menuBuilder.performItemAction(menuItem, 4);
                                        }
                                    }
                                }, (Object)menuBuilder, SystemClock.uptimeMillis() + 200L);
                            }
                            return;
                        }
                    }
                    i = -1;
                    continue;
                }
            }
            
            @Override
            public void onItemHoverExit(@NonNull final MenuBuilder menuBuilder, @NonNull final MenuItem menuItem) {
                CascadingMenuPopup.this.mSubMenuHoverHandler.removeCallbacksAndMessages((Object)menuBuilder);
            }
        };
        this.mRawDropDownGravity = 0;
        this.mDropDownGravity = 0;
        this.mContext = mContext;
        this.mAnchorView = mAnchorView;
        this.mPopupStyleAttr = mPopupStyleAttr;
        this.mPopupStyleRes = mPopupStyleRes;
        this.mOverflowOnly = mOverflowOnly;
        this.mForceShowIcon = false;
        this.mLastPosition = this.getInitialMenuPosition();
        final Resources resources = mContext.getResources();
        this.mMenuMaxWidth = Math.max(resources.getDisplayMetrics().widthPixels / 2, resources.getDimensionPixelSize(R.dimen.abc_config_prefDialogWidth));
        this.mSubMenuHoverHandler = new Handler();
    }
    
    private MenuPopupWindow createPopupWindow() {
        final MenuPopupWindow menuPopupWindow = new MenuPopupWindow(this.mContext, null, this.mPopupStyleAttr, this.mPopupStyleRes);
        menuPopupWindow.setHoverListener(this.mMenuItemHoverListener);
        menuPopupWindow.setOnItemClickListener((AdapterView$OnItemClickListener)this);
        menuPopupWindow.setOnDismissListener((PopupWindow$OnDismissListener)this);
        menuPopupWindow.setAnchorView(this.mAnchorView);
        menuPopupWindow.setDropDownGravity(this.mDropDownGravity);
        menuPopupWindow.setModal(true);
        menuPopupWindow.setInputMethodMode(2);
        return menuPopupWindow;
    }
    
    private int findIndexOfAddedMenu(@NonNull final MenuBuilder menuBuilder) {
        for (int size = this.mShowingMenus.size(), i = 0; i < size; ++i) {
            if (menuBuilder == this.mShowingMenus.get(i).menu) {
                return i;
            }
        }
        return -1;
    }
    
    private MenuItem findMenuItemForSubmenu(@NonNull final MenuBuilder menuBuilder, @NonNull final MenuBuilder menuBuilder2) {
        for (int i = 0; i < menuBuilder.size(); ++i) {
            final MenuItem item = menuBuilder.getItem(i);
            if (item.hasSubMenu() && menuBuilder2 == item.getSubMenu()) {
                return item;
            }
        }
        return null;
    }
    
    @Nullable
    private View findParentViewForSubmenu(@NonNull final CascadingMenuInfo cascadingMenuInfo, @NonNull final MenuBuilder menuBuilder) {
        int i = 0;
        final MenuItem menuItemForSubmenu = this.findMenuItemForSubmenu(cascadingMenuInfo.menu, menuBuilder);
        if (menuItemForSubmenu != null) {
            final ListView listView = cascadingMenuInfo.getListView();
            final ListAdapter adapter = listView.getAdapter();
            MenuAdapter menuAdapter;
            int headersCount;
            if (!(adapter instanceof HeaderViewListAdapter)) {
                menuAdapter = (MenuAdapter)adapter;
                headersCount = 0;
            }
            else {
                final HeaderViewListAdapter headerViewListAdapter = (HeaderViewListAdapter)adapter;
                headersCount = headerViewListAdapter.getHeadersCount();
                menuAdapter = (MenuAdapter)headerViewListAdapter.getWrappedAdapter();
            }
            while (true) {
                while (i < menuAdapter.getCount()) {
                    if (menuItemForSubmenu != menuAdapter.getItem(i)) {
                        ++i;
                    }
                    else {
                        if (i == -1) {
                            return null;
                        }
                        final int n = i + headersCount - listView.getFirstVisiblePosition();
                        if (n >= 0 && n < listView.getChildCount()) {
                            return listView.getChildAt(n);
                        }
                        return null;
                    }
                }
                i = -1;
                continue;
            }
        }
        return null;
    }
    
    private int getInitialMenuPosition() {
        int n = 1;
        if (ViewCompat.getLayoutDirection(this.mAnchorView) == 1) {
            n = 0;
        }
        return n;
    }
    
    private int getNextMenuPosition(final int n) {
        final ListView listView = this.mShowingMenus.get(this.mShowingMenus.size() - 1).getListView();
        final int[] array = new int[2];
        listView.getLocationOnScreen(array);
        final Rect rect = new Rect();
        this.mShownAnchorView.getWindowVisibleDisplayFrame(rect);
        if (this.mLastPosition != 1) {
            if (array[0] - n >= 0) {
                return 0;
            }
            return 1;
        }
        else {
            if (listView.getWidth() + array[0] + n <= rect.right) {
                return 1;
            }
            return 0;
        }
    }
    
    private void showMenu(@NonNull final MenuBuilder menuBuilder) {
        final LayoutInflater from = LayoutInflater.from(this.mContext);
        final MenuAdapter adapter = new MenuAdapter(menuBuilder, from, this.mOverflowOnly);
        if (!this.isShowing() && this.mForceShowIcon) {
            adapter.setForceShowIcon(true);
        }
        else if (this.isShowing()) {
            adapter.setForceShowIcon(MenuPopup.shouldPreserveIconSpacing(menuBuilder));
        }
        final int measureIndividualMenuWidth = MenuPopup.measureIndividualMenuWidth((ListAdapter)adapter, null, this.mContext, this.mMenuMaxWidth);
        final MenuPopupWindow popupWindow = this.createPopupWindow();
        popupWindow.setAdapter((ListAdapter)adapter);
        popupWindow.setContentWidth(measureIndividualMenuWidth);
        popupWindow.setDropDownGravity(this.mDropDownGravity);
        View parentViewForSubmenu;
        CascadingMenuInfo cascadingMenuInfo;
        if (this.mShowingMenus.size() <= 0) {
            parentViewForSubmenu = null;
            cascadingMenuInfo = null;
        }
        else {
            cascadingMenuInfo = this.mShowingMenus.get(this.mShowingMenus.size() - 1);
            parentViewForSubmenu = this.findParentViewForSubmenu(cascadingMenuInfo, menuBuilder);
        }
        if (parentViewForSubmenu == null) {
            if (this.mHasXOffset) {
                popupWindow.setHorizontalOffset(this.mXOffset);
            }
            if (this.mHasYOffset) {
                popupWindow.setVerticalOffset(this.mYOffset);
            }
            popupWindow.setEpicenterBounds(this.getEpicenterBounds());
        }
        else {
            popupWindow.setTouchModal(false);
            popupWindow.setEnterTransition(null);
            final int nextMenuPosition = this.getNextMenuPosition(measureIndividualMenuWidth);
            boolean b;
            if (nextMenuPosition != 1) {
                b = false;
            }
            else {
                b = true;
            }
            this.mLastPosition = nextMenuPosition;
            int n;
            int verticalOffset;
            if (Build$VERSION.SDK_INT < 26) {
                final int[] array = new int[2];
                this.mAnchorView.getLocationOnScreen(array);
                final int[] array2 = new int[2];
                parentViewForSubmenu.getLocationOnScreen(array2);
                n = array2[0] - array[0];
                verticalOffset = array2[1] - array[1];
            }
            else {
                popupWindow.setAnchorView(parentViewForSubmenu);
                verticalOffset = 0;
                n = 0;
            }
            int horizontalOffset;
            if ((this.mDropDownGravity & 0x5) != 0x5) {
                if (!b) {
                    horizontalOffset = n - measureIndividualMenuWidth;
                }
                else {
                    horizontalOffset = parentViewForSubmenu.getWidth() + n;
                }
            }
            else if (!b) {
                horizontalOffset = n - parentViewForSubmenu.getWidth();
            }
            else {
                horizontalOffset = n + measureIndividualMenuWidth;
            }
            popupWindow.setHorizontalOffset(horizontalOffset);
            popupWindow.setOverlapAnchor(true);
            popupWindow.setVerticalOffset(verticalOffset);
        }
        this.mShowingMenus.add(new CascadingMenuInfo(popupWindow, menuBuilder, this.mLastPosition));
        popupWindow.show();
        final ListView listView = popupWindow.getListView();
        listView.setOnKeyListener((View$OnKeyListener)this);
        if (cascadingMenuInfo == null && this.mShowTitle && menuBuilder.getHeaderTitle() != null) {
            final FrameLayout frameLayout = (FrameLayout)from.inflate(R.layout.abc_popup_menu_header_item_layout, (ViewGroup)listView, false);
            final TextView textView = (TextView)frameLayout.findViewById(16908310);
            frameLayout.setEnabled(false);
            textView.setText(menuBuilder.getHeaderTitle());
            listView.addHeaderView((View)frameLayout, (Object)null, false);
            popupWindow.show();
        }
    }
    
    @Override
    public void addMenu(final MenuBuilder menuBuilder) {
        menuBuilder.addMenuPresenter(this, this.mContext);
        if (!this.isShowing()) {
            this.mPendingMenus.add(menuBuilder);
        }
        else {
            this.showMenu(menuBuilder);
        }
    }
    
    @Override
    protected boolean closeMenuOnSubMenuOpened() {
        return false;
    }
    
    public void dismiss() {
        int size = this.mShowingMenus.size();
        if (size > 0) {
            final CascadingMenuInfo[] array = this.mShowingMenus.toArray(new CascadingMenuInfo[size]);
            while (true) {
                final int n = size - 1;
                if (n < 0) {
                    break;
                }
                final CascadingMenuInfo cascadingMenuInfo = array[n];
                size = n;
                if (!cascadingMenuInfo.window.isShowing()) {
                    continue;
                }
                cascadingMenuInfo.window.dismiss();
                size = n;
            }
        }
    }
    
    @Override
    public boolean flagActionItems() {
        return false;
    }
    
    public ListView getListView() {
        ListView listView;
        if (!this.mShowingMenus.isEmpty()) {
            listView = this.mShowingMenus.get(this.mShowingMenus.size() - 1).getListView();
        }
        else {
            listView = null;
        }
        return listView;
    }
    
    public boolean isShowing() {
        return this.mShowingMenus.size() > 0 && this.mShowingMenus.get(0).window.isShowing();
    }
    
    @Override
    public void onCloseMenu(final MenuBuilder menuBuilder, final boolean b) {
        final int indexOfAddedMenu = this.findIndexOfAddedMenu(menuBuilder);
        if (indexOfAddedMenu >= 0) {
            final int n = indexOfAddedMenu + 1;
            if (n < this.mShowingMenus.size()) {
                this.mShowingMenus.get(n).menu.close(false);
            }
            final CascadingMenuInfo cascadingMenuInfo = this.mShowingMenus.remove(indexOfAddedMenu);
            cascadingMenuInfo.menu.removeMenuPresenter(this);
            if (this.mShouldCloseImmediately) {
                cascadingMenuInfo.window.setExitTransition(null);
                cascadingMenuInfo.window.setAnimationStyle(0);
            }
            cascadingMenuInfo.window.dismiss();
            final int size = this.mShowingMenus.size();
            if (size <= 0) {
                this.mLastPosition = this.getInitialMenuPosition();
            }
            else {
                this.mLastPosition = this.mShowingMenus.get(size - 1).position;
            }
            if (size != 0) {
                if (b) {
                    this.mShowingMenus.get(0).menu.close(false);
                }
            }
            else {
                this.dismiss();
                if (this.mPresenterCallback != null) {
                    this.mPresenterCallback.onCloseMenu(menuBuilder, true);
                }
                if (this.mTreeObserver != null) {
                    if (this.mTreeObserver.isAlive()) {
                        this.mTreeObserver.removeGlobalOnLayoutListener(this.mGlobalLayoutListener);
                    }
                    this.mTreeObserver = null;
                }
                this.mShownAnchorView.removeOnAttachStateChangeListener(this.mAttachStateChangeListener);
                this.mOnDismissListener.onDismiss();
            }
        }
    }
    
    public void onDismiss() {
        while (true) {
            for (int size = this.mShowingMenus.size(), i = 0; i < size; ++i) {
                final CascadingMenuInfo cascadingMenuInfo;
                if (!(cascadingMenuInfo = this.mShowingMenus.get(i)).window.isShowing()) {
                    if (cascadingMenuInfo != null) {
                        cascadingMenuInfo.menu.close(false);
                    }
                    return;
                }
            }
            CascadingMenuInfo cascadingMenuInfo = null;
            continue;
        }
    }
    
    public boolean onKey(final View view, final int n, final KeyEvent keyEvent) {
        if (keyEvent.getAction() == 1 && n == 82) {
            this.dismiss();
            return true;
        }
        return false;
    }
    
    @Override
    public void onRestoreInstanceState(final Parcelable parcelable) {
    }
    
    @Override
    public Parcelable onSaveInstanceState() {
        return null;
    }
    
    @Override
    public boolean onSubMenuSelected(final SubMenuBuilder subMenuBuilder) {
        for (final CascadingMenuInfo cascadingMenuInfo : this.mShowingMenus) {
            if (subMenuBuilder == cascadingMenuInfo.menu) {
                cascadingMenuInfo.getListView().requestFocus();
                return true;
            }
        }
        if (!subMenuBuilder.hasVisibleItems()) {
            return false;
        }
        this.addMenu(subMenuBuilder);
        if (this.mPresenterCallback != null) {
            this.mPresenterCallback.onOpenSubMenu(subMenuBuilder);
        }
        return true;
    }
    
    @Override
    public void setAnchorView(@NonNull final View mAnchorView) {
        if (this.mAnchorView != mAnchorView) {
            this.mAnchorView = mAnchorView;
            this.mDropDownGravity = GravityCompat.getAbsoluteGravity(this.mRawDropDownGravity, ViewCompat.getLayoutDirection(this.mAnchorView));
        }
    }
    
    @Override
    public void setCallback(final Callback mPresenterCallback) {
        this.mPresenterCallback = mPresenterCallback;
    }
    
    @Override
    public void setForceShowIcon(final boolean mForceShowIcon) {
        this.mForceShowIcon = mForceShowIcon;
    }
    
    @Override
    public void setGravity(final int mRawDropDownGravity) {
        if (this.mRawDropDownGravity != mRawDropDownGravity) {
            this.mRawDropDownGravity = mRawDropDownGravity;
            this.mDropDownGravity = GravityCompat.getAbsoluteGravity(mRawDropDownGravity, ViewCompat.getLayoutDirection(this.mAnchorView));
        }
    }
    
    @Override
    public void setHorizontalOffset(final int mxOffset) {
        this.mHasXOffset = true;
        this.mXOffset = mxOffset;
    }
    
    @Override
    public void setOnDismissListener(final PopupWindow$OnDismissListener mOnDismissListener) {
        this.mOnDismissListener = mOnDismissListener;
    }
    
    @Override
    public void setShowTitle(final boolean mShowTitle) {
        this.mShowTitle = mShowTitle;
    }
    
    @Override
    public void setVerticalOffset(final int myOffset) {
        this.mHasYOffset = true;
        this.mYOffset = myOffset;
    }
    
    public void show() {
        if (!this.isShowing()) {
            final Iterator<MenuBuilder> iterator = this.mPendingMenus.iterator();
            while (iterator.hasNext()) {
                this.showMenu(iterator.next());
            }
            this.mPendingMenus.clear();
            this.mShownAnchorView = this.mAnchorView;
            if (this.mShownAnchorView != null) {
                int n;
                if (this.mTreeObserver != null) {
                    n = 0;
                }
                else {
                    n = 1;
                }
                this.mTreeObserver = this.mShownAnchorView.getViewTreeObserver();
                if (n != 0) {
                    this.mTreeObserver.addOnGlobalLayoutListener(this.mGlobalLayoutListener);
                }
                this.mShownAnchorView.addOnAttachStateChangeListener(this.mAttachStateChangeListener);
            }
        }
    }
    
    @Override
    public void updateMenuView(final boolean b) {
        final Iterator<CascadingMenuInfo> iterator = this.mShowingMenus.iterator();
        while (iterator.hasNext()) {
            MenuPopup.toMenuAdapter(iterator.next().getListView().getAdapter()).notifyDataSetChanged();
        }
    }
    
    private static class CascadingMenuInfo
    {
        public final MenuBuilder menu;
        public final int position;
        public final MenuPopupWindow window;
        
        public CascadingMenuInfo(@NonNull final MenuPopupWindow window, @NonNull final MenuBuilder menu, final int position) {
            this.window = window;
            this.menu = menu;
            this.position = position;
        }
        
        public ListView getListView() {
            return this.window.getListView();
        }
    }
    
    @Retention(RetentionPolicy.SOURCE)
    public @interface HorizPosition {
    }
}
