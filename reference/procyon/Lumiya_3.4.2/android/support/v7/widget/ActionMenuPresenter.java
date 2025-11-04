// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v7.widget;

import android.os.Parcel;
import android.os.Parcelable$Creator;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.View$OnTouchListener;
import android.util.AttributeSet;
import android.support.v7.view.menu.ShowableListMenu;
import android.support.v7.view.menu.MenuPresenter;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.view.menu.SubMenuBuilder;
import android.os.Parcelable;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.v7.view.ActionBarPolicy;
import android.support.annotation.Nullable;
import android.support.annotation.NonNull;
import android.view.ViewGroup$LayoutParams;
import java.util.ArrayList;
import android.view.View$MeasureSpec;
import android.support.v7.view.menu.ActionMenuItemView;
import android.support.v7.view.menu.MenuItemImpl;
import android.view.ViewGroup;
import android.view.MenuItem;
import android.support.v7.view.menu.MenuView;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.appcompat.R;
import android.content.Context;
import android.view.View;
import android.graphics.drawable.Drawable;
import android.util.SparseBooleanArray;
import android.support.v4.view.ActionProvider;
import android.support.v7.view.menu.BaseMenuPresenter;

class ActionMenuPresenter extends BaseMenuPresenter implements SubUiVisibilityListener
{
    private static final String TAG = "ActionMenuPresenter";
    private final SparseBooleanArray mActionButtonGroups;
    ActionButtonSubmenu mActionButtonPopup;
    private int mActionItemWidthLimit;
    private boolean mExpandedActionViewsExclusive;
    private int mMaxItems;
    private boolean mMaxItemsSet;
    private int mMinCellSize;
    int mOpenSubMenuId;
    OverflowMenuButton mOverflowButton;
    OverflowPopup mOverflowPopup;
    private Drawable mPendingOverflowIcon;
    private boolean mPendingOverflowIconSet;
    private ActionMenuPopupCallback mPopupCallback;
    final PopupPresenterCallback mPopupPresenterCallback;
    OpenOverflowRunnable mPostedOpenRunnable;
    private boolean mReserveOverflow;
    private boolean mReserveOverflowSet;
    private View mScrapActionButtonView;
    private boolean mStrictWidthLimit;
    private int mWidthLimit;
    private boolean mWidthLimitSet;
    
    public ActionMenuPresenter(final Context context) {
        super(context, R.layout.abc_action_menu_layout, R.layout.abc_action_menu_item_layout);
        this.mActionButtonGroups = new SparseBooleanArray();
        this.mPopupPresenterCallback = new PopupPresenterCallback();
    }
    
    private View findViewForItem(final MenuItem menuItem) {
        final ViewGroup viewGroup = (ViewGroup)this.mMenuView;
        if (viewGroup != null) {
            for (int childCount = viewGroup.getChildCount(), i = 0; i < childCount; ++i) {
                final View child = viewGroup.getChildAt(i);
                if (child instanceof MenuView.ItemView && ((MenuView.ItemView)child).getItemData() == menuItem) {
                    return child;
                }
            }
            return null;
        }
        return null;
    }
    
    @Override
    public void bindItemView(final MenuItemImpl menuItemImpl, final MenuView.ItemView itemView) {
        itemView.initialize(menuItemImpl, 0);
        final ActionMenuView itemInvoker = (ActionMenuView)this.mMenuView;
        final ActionMenuItemView actionMenuItemView = (ActionMenuItemView)itemView;
        actionMenuItemView.setItemInvoker(itemInvoker);
        if (this.mPopupCallback == null) {
            this.mPopupCallback = new ActionMenuPopupCallback();
        }
        actionMenuItemView.setPopupCallback((ActionMenuItemView.PopupCallback)this.mPopupCallback);
    }
    
    public boolean dismissPopupMenus() {
        return this.hideOverflowMenu() | this.hideSubMenus();
    }
    
    public boolean filterLeftoverView(final ViewGroup viewGroup, final int n) {
        return viewGroup.getChildAt(n) != this.mOverflowButton && super.filterLeftoverView(viewGroup, n);
    }
    
    @Override
    public boolean flagActionItems() {
        int size;
        ArrayList<MenuItemImpl> visibleItems;
        if (this.mMenu == null) {
            size = 0;
            visibleItems = null;
        }
        else {
            visibleItems = this.mMenu.getVisibleItems();
            size = visibleItems.size();
        }
        int mMaxItems = this.mMaxItems;
        final int mActionItemWidthLimit = this.mActionItemWidthLimit;
        final int measureSpec = View$MeasureSpec.makeMeasureSpec(0, 0);
        final ViewGroup viewGroup = (ViewGroup)this.mMenuView;
        int n = 0;
        int n2 = 0;
        boolean b = false;
        for (int i = 0; i < size; ++i) {
            final MenuItemImpl menuItemImpl = visibleItems.get(i);
            if (!menuItemImpl.requiresActionButton()) {
                if (!menuItemImpl.requestsActionButton()) {
                    b = true;
                }
                else {
                    ++n2;
                }
            }
            else {
                ++n;
            }
            if (this.mExpandedActionViewsExclusive && menuItemImpl.isActionViewExpanded()) {
                mMaxItems = 0;
            }
        }
        if (this.mReserveOverflow) {
            if (b || n + n2 > mMaxItems) {
                --mMaxItems;
            }
        }
        final int n3 = mMaxItems - n;
        final SparseBooleanArray mActionButtonGroups = this.mActionButtonGroups;
        mActionButtonGroups.clear();
        int n4 = 0;
        int n5;
        if (!this.mStrictWidthLimit) {
            n5 = 0;
        }
        else {
            n4 = mActionItemWidthLimit / this.mMinCellSize;
            n5 = mActionItemWidthLimit % this.mMinCellSize / n4 + this.mMinCellSize;
        }
        int j = 0;
        final int n6 = 0;
        int n7 = n4;
        int n8 = n3;
        int n9 = mActionItemWidthLimit;
        int n10 = n6;
        while (j < size) {
            final MenuItemImpl menuItemImpl2 = visibleItems.get(j);
            int n12;
            int n13;
            if (!menuItemImpl2.requiresActionButton()) {
                if (!menuItemImpl2.requestsActionButton()) {
                    menuItemImpl2.setIsActionButton(false);
                    final int n11 = n9;
                    n12 = n8;
                    n13 = n11;
                }
                else {
                    final int groupId = menuItemImpl2.getGroupId();
                    final boolean value = mActionButtonGroups.get(groupId);
                    boolean isActionButton = false;
                    Label_0486: {
                        if (n8 > 0 || value) {
                            if (n9 > 0) {
                                if (!this.mStrictWidthLimit || n7 > 0) {
                                    isActionButton = true;
                                    break Label_0486;
                                }
                            }
                        }
                        isActionButton = false;
                    }
                    int n15;
                    int n16;
                    if (!isActionButton) {
                        final int n14 = n7;
                        n15 = n10;
                        n16 = n14;
                    }
                    else {
                        final View itemView = this.getItemView(menuItemImpl2, this.mScrapActionButtonView, viewGroup);
                        if (this.mScrapActionButtonView == null) {
                            this.mScrapActionButtonView = itemView;
                        }
                        if (!this.mStrictWidthLimit) {
                            itemView.measure(measureSpec, measureSpec);
                        }
                        else {
                            final int measureChildForCells = ActionMenuView.measureChildForCells(itemView, n5, n7, measureSpec, 0);
                            final int n17 = n7 -= measureChildForCells;
                            if (measureChildForCells == 0) {
                                isActionButton = false;
                                n7 = n17;
                            }
                        }
                        final int measuredWidth = itemView.getMeasuredWidth();
                        final int n18 = n9 - measuredWidth;
                        if (n10 == 0) {
                            n10 = measuredWidth;
                        }
                        if (!this.mStrictWidthLimit) {
                            isActionButton &= (n18 + n10 > 0);
                            final int n19 = n10;
                            n16 = n7;
                            n15 = n19;
                            n9 = n18;
                        }
                        else {
                            isActionButton &= (n18 >= 0);
                            final int n20 = n10;
                            n16 = n7;
                            n15 = n20;
                            n9 = n18;
                        }
                    }
                    if (isActionButton && groupId != 0) {
                        mActionButtonGroups.put(groupId, true);
                    }
                    else if (value) {
                        mActionButtonGroups.put(groupId, false);
                        for (int k = 0; k < j; ++k) {
                            final MenuItemImpl menuItemImpl3 = visibleItems.get(k);
                            if (menuItemImpl3.getGroupId() == groupId) {
                                if (menuItemImpl3.isActionButton()) {
                                    ++n8;
                                }
                                menuItemImpl3.setIsActionButton(false);
                            }
                        }
                    }
                    if (isActionButton) {
                        --n8;
                    }
                    menuItemImpl2.setIsActionButton(isActionButton);
                    final int n21 = n15;
                    final int n22 = n9;
                    n12 = n8;
                    n13 = n22;
                    n7 = n16;
                    n10 = n21;
                }
            }
            else {
                final View itemView2 = this.getItemView(menuItemImpl2, this.mScrapActionButtonView, viewGroup);
                if (this.mScrapActionButtonView == null) {
                    this.mScrapActionButtonView = itemView2;
                }
                if (!this.mStrictWidthLimit) {
                    itemView2.measure(measureSpec, measureSpec);
                }
                else {
                    n7 -= ActionMenuView.measureChildForCells(itemView2, n5, n7, measureSpec, 0);
                }
                final int measuredWidth2 = itemView2.getMeasuredWidth();
                if (n10 == 0) {
                    n10 = measuredWidth2;
                }
                final int groupId2 = menuItemImpl2.getGroupId();
                if (groupId2 != 0) {
                    mActionButtonGroups.put(groupId2, true);
                }
                menuItemImpl2.setIsActionButton(true);
                final int n23 = n9 - measuredWidth2;
                n12 = n8;
                n13 = n23;
            }
            final int n24 = j + 1;
            final int n25 = n12;
            n9 = n13;
            n8 = n25;
            j = n24;
        }
        return true;
    }
    
    @Override
    public View getItemView(final MenuItemImpl menuItemImpl, View itemView, final ViewGroup viewGroup) {
        int visibility = 0;
        final View actionView = menuItemImpl.getActionView();
        if (actionView != null && !menuItemImpl.hasCollapsibleActionView()) {
            itemView = actionView;
        }
        else {
            itemView = super.getItemView(menuItemImpl, itemView, viewGroup);
        }
        if (menuItemImpl.isActionViewExpanded()) {
            visibility = 8;
        }
        itemView.setVisibility(visibility);
        final ActionMenuView actionMenuView = (ActionMenuView)viewGroup;
        final ViewGroup$LayoutParams layoutParams = itemView.getLayoutParams();
        if (!actionMenuView.checkLayoutParams(layoutParams)) {
            itemView.setLayoutParams((ViewGroup$LayoutParams)actionMenuView.generateLayoutParams(layoutParams));
        }
        return itemView;
    }
    
    @Override
    public MenuView getMenuView(final ViewGroup viewGroup) {
        final MenuView mMenuView = this.mMenuView;
        final MenuView menuView = super.getMenuView(viewGroup);
        if (mMenuView != menuView) {
            ((ActionMenuView)menuView).setPresenter(this);
        }
        return menuView;
    }
    
    public Drawable getOverflowIcon() {
        if (this.mOverflowButton != null) {
            return this.mOverflowButton.getDrawable();
        }
        if (!this.mPendingOverflowIconSet) {
            return null;
        }
        return this.mPendingOverflowIcon;
    }
    
    public boolean hideOverflowMenu() {
        if (this.mPostedOpenRunnable != null && this.mMenuView != null) {
            ((View)this.mMenuView).removeCallbacks((Runnable)this.mPostedOpenRunnable);
            this.mPostedOpenRunnable = null;
            return true;
        }
        final OverflowPopup mOverflowPopup = this.mOverflowPopup;
        if (mOverflowPopup == null) {
            return false;
        }
        mOverflowPopup.dismiss();
        return true;
    }
    
    public boolean hideSubMenus() {
        if (this.mActionButtonPopup == null) {
            return false;
        }
        this.mActionButtonPopup.dismiss();
        return true;
    }
    
    @Override
    public void initForMenu(@NonNull final Context context, @Nullable final MenuBuilder menuBuilder) {
        super.initForMenu(context, menuBuilder);
        final Resources resources = context.getResources();
        final ActionBarPolicy value = ActionBarPolicy.get(context);
        if (!this.mReserveOverflowSet) {
            this.mReserveOverflow = value.showsOverflowMenuButton();
        }
        if (!this.mWidthLimitSet) {
            this.mWidthLimit = value.getEmbeddedMenuWidthLimit();
        }
        if (!this.mMaxItemsSet) {
            this.mMaxItems = value.getMaxActionButtons();
        }
        int mWidthLimit = this.mWidthLimit;
        if (!this.mReserveOverflow) {
            this.mOverflowButton = null;
        }
        else {
            if (this.mOverflowButton == null) {
                this.mOverflowButton = new OverflowMenuButton(this.mSystemContext);
                if (this.mPendingOverflowIconSet) {
                    this.mOverflowButton.setImageDrawable(this.mPendingOverflowIcon);
                    this.mPendingOverflowIcon = null;
                    this.mPendingOverflowIconSet = false;
                }
                final int measureSpec = View$MeasureSpec.makeMeasureSpec(0, 0);
                this.mOverflowButton.measure(measureSpec, measureSpec);
            }
            mWidthLimit -= this.mOverflowButton.getMeasuredWidth();
        }
        this.mActionItemWidthLimit = mWidthLimit;
        this.mMinCellSize = (int)(resources.getDisplayMetrics().density * 56.0f);
        this.mScrapActionButtonView = null;
    }
    
    public boolean isOverflowMenuShowPending() {
        boolean b = false;
        if (this.mPostedOpenRunnable != null || this.isOverflowMenuShowing()) {
            b = true;
        }
        return b;
    }
    
    public boolean isOverflowMenuShowing() {
        boolean b = false;
        if (this.mOverflowPopup != null && this.mOverflowPopup.isShowing()) {
            b = true;
        }
        return b;
    }
    
    public boolean isOverflowReserved() {
        return this.mReserveOverflow;
    }
    
    @Override
    public void onCloseMenu(final MenuBuilder menuBuilder, final boolean b) {
        this.dismissPopupMenus();
        super.onCloseMenu(menuBuilder, b);
    }
    
    public void onConfigurationChanged(final Configuration configuration) {
        if (!this.mMaxItemsSet) {
            this.mMaxItems = ActionBarPolicy.get(this.mContext).getMaxActionButtons();
        }
        if (this.mMenu != null) {
            this.mMenu.onItemsChanged(true);
        }
    }
    
    @Override
    public void onRestoreInstanceState(final Parcelable parcelable) {
        if (parcelable instanceof SavedState) {
            final SavedState savedState = (SavedState)parcelable;
            if (savedState.openSubMenuId > 0) {
                final MenuItem item = this.mMenu.findItem(savedState.openSubMenuId);
                if (item != null) {
                    this.onSubMenuSelected((SubMenuBuilder)item.getSubMenu());
                }
            }
        }
    }
    
    @Override
    public Parcelable onSaveInstanceState() {
        final SavedState savedState = new SavedState();
        savedState.openSubMenuId = this.mOpenSubMenuId;
        return (Parcelable)savedState;
    }
    
    @Override
    public boolean onSubMenuSelected(final SubMenuBuilder subMenuBuilder) {
        if (!subMenuBuilder.hasVisibleItems()) {
            return false;
        }
        SubMenuBuilder subMenuBuilder2;
        for (subMenuBuilder2 = subMenuBuilder; subMenuBuilder2.getParentMenu() != this.mMenu; subMenuBuilder2 = (SubMenuBuilder)subMenuBuilder2.getParentMenu()) {}
        final View viewForItem = this.findViewForItem(subMenuBuilder2.getItem());
        if (viewForItem != null) {
            this.mOpenSubMenuId = subMenuBuilder.getItem().getItemId();
            while (true) {
                for (int size = subMenuBuilder.size(), i = 0; i < size; ++i) {
                    final MenuItem item = subMenuBuilder.getItem(i);
                    if (item.isVisible() && item.getIcon() != null) {
                        final boolean forceShowIcon = true;
                        (this.mActionButtonPopup = new ActionButtonSubmenu(this.mContext, subMenuBuilder, viewForItem)).setForceShowIcon(forceShowIcon);
                        this.mActionButtonPopup.show();
                        super.onSubMenuSelected(subMenuBuilder);
                        return true;
                    }
                }
                final boolean forceShowIcon = false;
                continue;
            }
        }
        return false;
    }
    
    @Override
    public void onSubUiVisibilityChanged(final boolean b) {
        if (!b) {
            if (this.mMenu != null) {
                this.mMenu.close(false);
            }
        }
        else {
            super.onSubMenuSelected(null);
        }
    }
    
    public void setExpandedActionViewsExclusive(final boolean mExpandedActionViewsExclusive) {
        this.mExpandedActionViewsExclusive = mExpandedActionViewsExclusive;
    }
    
    public void setItemLimit(final int mMaxItems) {
        this.mMaxItems = mMaxItems;
        this.mMaxItemsSet = true;
    }
    
    public void setMenuView(final ActionMenuView mMenuView) {
        ((ActionMenuView)(this.mMenuView = mMenuView)).initialize(this.mMenu);
    }
    
    public void setOverflowIcon(final Drawable drawable) {
        if (this.mOverflowButton == null) {
            this.mPendingOverflowIconSet = true;
            this.mPendingOverflowIcon = drawable;
        }
        else {
            this.mOverflowButton.setImageDrawable(drawable);
        }
    }
    
    public void setReserveOverflow(final boolean mReserveOverflow) {
        this.mReserveOverflow = mReserveOverflow;
        this.mReserveOverflowSet = true;
    }
    
    public void setWidthLimit(final int mWidthLimit, final boolean mStrictWidthLimit) {
        this.mWidthLimit = mWidthLimit;
        this.mStrictWidthLimit = mStrictWidthLimit;
        this.mWidthLimitSet = true;
    }
    
    @Override
    public boolean shouldIncludeItem(final int n, final MenuItemImpl menuItemImpl) {
        return menuItemImpl.isActionButton();
    }
    
    public boolean showOverflowMenu() {
        if (this.mReserveOverflow && !this.isOverflowMenuShowing() && this.mMenu != null && this.mMenuView != null && this.mPostedOpenRunnable == null && !this.mMenu.getNonActionItems().isEmpty()) {
            this.mPostedOpenRunnable = new OpenOverflowRunnable(new OverflowPopup(this.mContext, this.mMenu, (View)this.mOverflowButton, true));
            ((View)this.mMenuView).post((Runnable)this.mPostedOpenRunnable);
            super.onSubMenuSelected(null);
            return true;
        }
        return false;
    }
    
    @Override
    public void updateMenuView(final boolean b) {
        final boolean b2 = false;
        super.updateMenuView(b);
        ((View)this.mMenuView).requestLayout();
        if (this.mMenu != null) {
            final ArrayList<MenuItemImpl> actionItems = this.mMenu.getActionItems();
            for (int size = actionItems.size(), i = 0; i < size; ++i) {
                final ActionProvider supportActionProvider = actionItems.get(i).getSupportActionProvider();
                if (supportActionProvider != null) {
                    supportActionProvider.setSubUiVisibilityListener((ActionProvider.SubUiVisibilityListener)this);
                }
            }
        }
        ArrayList<MenuItemImpl> nonActionItems;
        if (this.mMenu == null) {
            nonActionItems = null;
        }
        else {
            nonActionItems = this.mMenu.getNonActionItems();
        }
        int n;
        if (!this.mReserveOverflow) {
            n = (b2 ? 1 : 0);
        }
        else {
            n = (b2 ? 1 : 0);
            if (nonActionItems != null) {
                final int size2 = nonActionItems.size();
                if (size2 != 1) {
                    n = (b2 ? 1 : 0);
                    if (size2 > 0) {
                        n = 1;
                    }
                }
                else if (nonActionItems.get(0).isActionViewExpanded()) {
                    n = 0;
                }
                else {
                    n = 1;
                }
            }
        }
        if (n == 0) {
            if (this.mOverflowButton != null && this.mOverflowButton.getParent() == this.mMenuView) {
                ((ViewGroup)this.mMenuView).removeView((View)this.mOverflowButton);
            }
        }
        else {
            if (this.mOverflowButton == null) {
                this.mOverflowButton = new OverflowMenuButton(this.mSystemContext);
            }
            final ViewGroup viewGroup = (ViewGroup)this.mOverflowButton.getParent();
            if (viewGroup != this.mMenuView) {
                if (viewGroup != null) {
                    viewGroup.removeView((View)this.mOverflowButton);
                }
                final ActionMenuView actionMenuView = (ActionMenuView)this.mMenuView;
                actionMenuView.addView((View)this.mOverflowButton, (ViewGroup$LayoutParams)actionMenuView.generateOverflowButtonLayoutParams());
            }
        }
        ((ActionMenuView)this.mMenuView).setOverflowReserved(this.mReserveOverflow);
    }
    
    private class ActionButtonSubmenu extends MenuPopupHelper
    {
        public ActionButtonSubmenu(final Context context, final SubMenuBuilder subMenuBuilder, final View view) {
            super(context, subMenuBuilder, view, false, R.attr.actionOverflowMenuStyle);
            if (!((MenuItemImpl)subMenuBuilder.getItem()).isActionButton()) {
                Object mOverflowButton;
                if (ActionMenuPresenter.this.mOverflowButton != null) {
                    mOverflowButton = ActionMenuPresenter.this.mOverflowButton;
                }
                else {
                    mOverflowButton = ActionMenuPresenter.this.mMenuView;
                }
                this.setAnchorView((View)mOverflowButton);
            }
            this.setPresenterCallback(ActionMenuPresenter.this.mPopupPresenterCallback);
        }
        
        @Override
        protected void onDismiss() {
            ActionMenuPresenter.this.mActionButtonPopup = null;
            ActionMenuPresenter.this.mOpenSubMenuId = 0;
            super.onDismiss();
        }
    }
    
    private class ActionMenuPopupCallback extends PopupCallback
    {
        ActionMenuPopupCallback() {
        }
        
        @Override
        public ShowableListMenu getPopup() {
            ShowableListMenu popup = null;
            if (ActionMenuPresenter.this.mActionButtonPopup != null) {
                popup = ActionMenuPresenter.this.mActionButtonPopup.getPopup();
            }
            return popup;
        }
    }
    
    private class OpenOverflowRunnable implements Runnable
    {
        private OverflowPopup mPopup;
        
        public OpenOverflowRunnable(final OverflowPopup mPopup) {
            this.mPopup = mPopup;
        }
        
        @Override
        public void run() {
            if (ActionMenuPresenter.this.mMenu != null) {
                ActionMenuPresenter.this.mMenu.changeMenuMode();
            }
            final View view = (View)ActionMenuPresenter.this.mMenuView;
            if (view != null && view.getWindowToken() != null && this.mPopup.tryShow()) {
                ActionMenuPresenter.this.mOverflowPopup = this.mPopup;
            }
            ActionMenuPresenter.this.mPostedOpenRunnable = null;
        }
    }
    
    private class OverflowMenuButton extends AppCompatImageView implements ActionMenuChildView
    {
        private final float[] mTempPts;
        
        public OverflowMenuButton(final Context context) {
            super(context, null, R.attr.actionOverflowButtonStyle);
            this.mTempPts = new float[2];
            this.setClickable(true);
            this.setFocusable(true);
            this.setVisibility(0);
            this.setEnabled(true);
            TooltipCompat.setTooltipText((View)this, this.getContentDescription());
            this.setOnTouchListener((View$OnTouchListener)new ForwardingListener(this) {
                @Override
                public ShowableListMenu getPopup() {
                    if (ActionMenuPresenter.this.mOverflowPopup != null) {
                        return ActionMenuPresenter.this.mOverflowPopup.getPopup();
                    }
                    return null;
                }
                
                public boolean onForwardingStarted() {
                    ActionMenuPresenter.this.showOverflowMenu();
                    return true;
                }
                
                public boolean onForwardingStopped() {
                    if (ActionMenuPresenter.this.mPostedOpenRunnable == null) {
                        ActionMenuPresenter.this.hideOverflowMenu();
                        return true;
                    }
                    return false;
                }
            });
        }
        
        @Override
        public boolean needsDividerAfter() {
            return false;
        }
        
        @Override
        public boolean needsDividerBefore() {
            return false;
        }
        
        public boolean performClick() {
            if (!super.performClick()) {
                this.playSoundEffect(0);
                ActionMenuPresenter.this.showOverflowMenu();
                return true;
            }
            return true;
        }
        
        protected boolean setFrame(int n, int height, int paddingBottom, int paddingTop) {
            final boolean setFrame = super.setFrame(n, height, paddingBottom, paddingTop);
            final Drawable drawable = this.getDrawable();
            final Drawable background = this.getBackground();
            if (drawable != null && background != null) {
                final int width = this.getWidth();
                height = this.getHeight();
                n = Math.max(width, height) / 2;
                final int paddingLeft = this.getPaddingLeft();
                final int paddingRight = this.getPaddingRight();
                paddingTop = this.getPaddingTop();
                paddingBottom = this.getPaddingBottom();
                final int n2 = (width + (paddingLeft - paddingRight)) / 2;
                height = (height + (paddingTop - paddingBottom)) / 2;
                DrawableCompat.setHotspotBounds(background, n2 - n, height - n, n2 + n, height + n);
            }
            return setFrame;
        }
    }
    
    private class OverflowPopup extends MenuPopupHelper
    {
        public OverflowPopup(final Context context, final MenuBuilder menuBuilder, final View view, final boolean b) {
            super(context, menuBuilder, view, b, R.attr.actionOverflowMenuStyle);
            this.setGravity(8388613);
            this.setPresenterCallback(ActionMenuPresenter.this.mPopupPresenterCallback);
        }
        
        @Override
        protected void onDismiss() {
            if (ActionMenuPresenter.this.mMenu != null) {
                ActionMenuPresenter.this.mMenu.close();
            }
            ActionMenuPresenter.this.mOverflowPopup = null;
            super.onDismiss();
        }
    }
    
    private class PopupPresenterCallback implements Callback
    {
        PopupPresenterCallback() {
        }
        
        @Override
        public void onCloseMenu(final MenuBuilder menuBuilder, final boolean b) {
            if (menuBuilder instanceof SubMenuBuilder) {
                menuBuilder.getRootMenu().close(false);
            }
            final Callback callback = ActionMenuPresenter.this.getCallback();
            if (callback != null) {
                callback.onCloseMenu(menuBuilder, b);
            }
        }
        
        @Override
        public boolean onOpenSubMenu(final MenuBuilder menuBuilder) {
            if (menuBuilder != null) {
                ActionMenuPresenter.this.mOpenSubMenuId = ((SubMenuBuilder)menuBuilder).getItem().getItemId();
                final Callback callback = ActionMenuPresenter.this.getCallback();
                return callback != null && callback.onOpenSubMenu(menuBuilder);
            }
            return false;
        }
    }
    
    private static class SavedState implements Parcelable
    {
        public static final Parcelable$Creator<SavedState> CREATOR;
        public int openSubMenuId;
        
        static {
            CREATOR = (Parcelable$Creator)new Parcelable$Creator<SavedState>() {
                public SavedState createFromParcel(final Parcel parcel) {
                    return new SavedState(parcel);
                }
                
                public SavedState[] newArray(final int n) {
                    return new SavedState[n];
                }
            };
        }
        
        SavedState() {
        }
        
        SavedState(final Parcel parcel) {
            this.openSubMenuId = parcel.readInt();
        }
        
        public int describeContents() {
            return 0;
        }
        
        public void writeToParcel(final Parcel parcel, final int n) {
            parcel.writeInt(this.openSubMenuId);
        }
    }
}
