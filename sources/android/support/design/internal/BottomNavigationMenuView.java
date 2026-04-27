package android.support.design.internal;

import android.animation.TimeInterpolator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.design.R;
import android.support.transition.AutoTransition;
import android.support.transition.TransitionManager;
import android.support.transition.TransitionSet;
import android.support.v4.util.Pools;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuItemImpl;
import android.support.v7.view.menu.MenuView;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
public class BottomNavigationMenuView extends ViewGroup implements MenuView {
    private static final long ACTIVE_ANIMATION_DURATION_MS = 115;
    private final int mActiveItemMaxWidth;
    private BottomNavigationItemView[] mButtons;
    private final int mInactiveItemMaxWidth;
    private final int mInactiveItemMinWidth;
    private int mItemBackgroundRes;
    private final int mItemHeight;
    private ColorStateList mItemIconTint;
    private final Pools.Pool<BottomNavigationItemView> mItemPool;
    private ColorStateList mItemTextColor;
    /* access modifiers changed from: private */
    public MenuBuilder mMenu;
    private final View.OnClickListener mOnClickListener;
    /* access modifiers changed from: private */
    public BottomNavigationPresenter mPresenter;
    private int mSelectedItemId;
    private int mSelectedItemPosition;
    private final TransitionSet mSet;
    private boolean mShiftingMode;
    private int[] mTempChildWidths;

    public BottomNavigationMenuView(Context context) {
        this(context, (AttributeSet) null);
    }

    public BottomNavigationMenuView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mItemPool = new Pools.SynchronizedPool(5);
        this.mShiftingMode = true;
        this.mSelectedItemId = 0;
        this.mSelectedItemPosition = 0;
        Resources resources = getResources();
        this.mInactiveItemMaxWidth = resources.getDimensionPixelSize(R.dimen.design_bottom_navigation_item_max_width);
        this.mInactiveItemMinWidth = resources.getDimensionPixelSize(R.dimen.design_bottom_navigation_item_min_width);
        this.mActiveItemMaxWidth = resources.getDimensionPixelSize(R.dimen.design_bottom_navigation_active_item_max_width);
        this.mItemHeight = resources.getDimensionPixelSize(R.dimen.design_bottom_navigation_height);
        this.mSet = new AutoTransition();
        this.mSet.setOrdering(0);
        this.mSet.setDuration((long) ACTIVE_ANIMATION_DURATION_MS);
        this.mSet.setInterpolator((TimeInterpolator) new FastOutSlowInInterpolator());
        this.mSet.addTransition(new TextScale());
        this.mOnClickListener = new View.OnClickListener() {
            public void onClick(View view) {
                MenuItemImpl itemData = ((BottomNavigationItemView) view).getItemData();
                if (!BottomNavigationMenuView.this.mMenu.performItemAction(itemData, BottomNavigationMenuView.this.mPresenter, 0)) {
                    itemData.setChecked(true);
                }
            }
        };
        this.mTempChildWidths = new int[5];
    }

    private BottomNavigationItemView getNewItem() {
        BottomNavigationItemView acquire = this.mItemPool.acquire();
        return acquire != null ? acquire : new BottomNavigationItemView(getContext());
    }

    public void buildMenuView() {
        removeAllViews();
        if (this.mButtons != null) {
            for (BottomNavigationItemView release : this.mButtons) {
                this.mItemPool.release(release);
            }
        }
        if (this.mMenu.size() != 0) {
            this.mButtons = new BottomNavigationItemView[this.mMenu.size()];
            this.mShiftingMode = this.mMenu.size() > 3;
            for (int i = 0; i < this.mMenu.size(); i++) {
                this.mPresenter.setUpdateSuspended(true);
                this.mMenu.getItem(i).setCheckable(true);
                this.mPresenter.setUpdateSuspended(false);
                BottomNavigationItemView newItem = getNewItem();
                this.mButtons[i] = newItem;
                newItem.setIconTintList(this.mItemIconTint);
                newItem.setTextColor(this.mItemTextColor);
                newItem.setItemBackground(this.mItemBackgroundRes);
                newItem.setShiftingMode(this.mShiftingMode);
                newItem.initialize((MenuItemImpl) this.mMenu.getItem(i), 0);
                newItem.setItemPosition(i);
                newItem.setOnClickListener(this.mOnClickListener);
                addView(newItem);
            }
            this.mSelectedItemPosition = Math.min(this.mMenu.size() - 1, this.mSelectedItemPosition);
            this.mMenu.getItem(this.mSelectedItemPosition).setChecked(true);
            return;
        }
        this.mSelectedItemId = 0;
        this.mSelectedItemPosition = 0;
        this.mButtons = null;
    }

    @Nullable
    public ColorStateList getIconTintList() {
        return this.mItemIconTint;
    }

    public int getItemBackgroundRes() {
        return this.mItemBackgroundRes;
    }

    public ColorStateList getItemTextColor() {
        return this.mItemTextColor;
    }

    public int getSelectedItemId() {
        return this.mSelectedItemId;
    }

    public int getWindowAnimations() {
        return 0;
    }

    public void initialize(MenuBuilder menuBuilder) {
        this.mMenu = menuBuilder;
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int childCount = getChildCount();
        int i5 = i3 - i;
        int i6 = i4 - i2;
        int i7 = 0;
        for (int i8 = 0; i8 < childCount; i8++) {
            View childAt = getChildAt(i8);
            if (childAt.getVisibility() != 8) {
                if (ViewCompat.getLayoutDirection(this) != 1) {
                    childAt.layout(i7, 0, childAt.getMeasuredWidth() + i7, i6);
                } else {
                    childAt.layout((i5 - i7) - childAt.getMeasuredWidth(), 0, i5 - i7, i6);
                }
                i7 += childAt.getMeasuredWidth();
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        int i3;
        int size = View.MeasureSpec.getSize(i);
        int childCount = getChildCount();
        int makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(this.mItemHeight, 1073741824);
        if (!this.mShiftingMode) {
            int min = Math.min(size / (childCount != 0 ? childCount : 1), this.mActiveItemMaxWidth);
            int i4 = size - (min * childCount);
            for (int i5 = 0; i5 < childCount; i5++) {
                this.mTempChildWidths[i5] = min;
                if (i4 > 0) {
                    int[] iArr = this.mTempChildWidths;
                    iArr[i5] = iArr[i5] + 1;
                    i4--;
                }
            }
        } else {
            int i6 = childCount - 1;
            int min2 = Math.min(size - (this.mInactiveItemMinWidth * i6), this.mActiveItemMaxWidth);
            int min3 = Math.min((size - min2) / i6, this.mInactiveItemMaxWidth);
            int i7 = (size - min2) - (i6 * min3);
            int i8 = 0;
            while (i8 < childCount) {
                this.mTempChildWidths[i8] = i8 != this.mSelectedItemPosition ? min3 : min2;
                if (i7 <= 0) {
                    i3 = i7;
                } else {
                    int[] iArr2 = this.mTempChildWidths;
                    iArr2[i8] = iArr2[i8] + 1;
                    i3 = i7 - 1;
                }
                i8++;
                i7 = i3;
            }
        }
        int i9 = 0;
        for (int i10 = 0; i10 < childCount; i10++) {
            View childAt = getChildAt(i10);
            if (childAt.getVisibility() != 8) {
                childAt.measure(View.MeasureSpec.makeMeasureSpec(this.mTempChildWidths[i10], 1073741824), makeMeasureSpec);
                childAt.getLayoutParams().width = childAt.getMeasuredWidth();
                i9 += childAt.getMeasuredWidth();
            }
        }
        setMeasuredDimension(View.resolveSizeAndState(i9, View.MeasureSpec.makeMeasureSpec(i9, 1073741824), 0), View.resolveSizeAndState(this.mItemHeight, makeMeasureSpec, 0));
    }

    public void setIconTintList(ColorStateList colorStateList) {
        this.mItemIconTint = colorStateList;
        if (this.mButtons != null) {
            for (BottomNavigationItemView iconTintList : this.mButtons) {
                iconTintList.setIconTintList(colorStateList);
            }
        }
    }

    public void setItemBackgroundRes(int i) {
        this.mItemBackgroundRes = i;
        if (this.mButtons != null) {
            for (BottomNavigationItemView itemBackground : this.mButtons) {
                itemBackground.setItemBackground(i);
            }
        }
    }

    public void setItemTextColor(ColorStateList colorStateList) {
        this.mItemTextColor = colorStateList;
        if (this.mButtons != null) {
            for (BottomNavigationItemView textColor : this.mButtons) {
                textColor.setTextColor(colorStateList);
            }
        }
    }

    public void setPresenter(BottomNavigationPresenter bottomNavigationPresenter) {
        this.mPresenter = bottomNavigationPresenter;
    }

    /* access modifiers changed from: package-private */
    public void tryRestoreSelectedItemId(int i) {
        int size = this.mMenu.size();
        int i2 = 0;
        while (i2 < size) {
            MenuItem item = this.mMenu.getItem(i2);
            if (i != item.getItemId()) {
                i2++;
            } else {
                this.mSelectedItemId = i;
                this.mSelectedItemPosition = i2;
                item.setChecked(true);
                return;
            }
        }
    }

    public void updateMenuView() {
        int size = this.mMenu.size();
        if (size == this.mButtons.length) {
            int i = this.mSelectedItemId;
            for (int i2 = 0; i2 < size; i2++) {
                MenuItem item = this.mMenu.getItem(i2);
                if (item.isChecked()) {
                    this.mSelectedItemId = item.getItemId();
                    this.mSelectedItemPosition = i2;
                }
            }
            if (i != this.mSelectedItemId) {
                TransitionManager.beginDelayedTransition(this, this.mSet);
            }
            for (int i3 = 0; i3 < size; i3++) {
                this.mPresenter.setUpdateSuspended(true);
                this.mButtons[i3].initialize((MenuItemImpl) this.mMenu.getItem(i3), 0);
                this.mPresenter.setUpdateSuspended(false);
            }
            return;
        }
        buildMenuView();
    }
}
