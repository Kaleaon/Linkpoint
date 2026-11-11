package android.support.v7.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.annotation.StyleRes;
import android.support.v7.view.menu.ActionMenuItemView;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuItemImpl;
import android.support.v7.view.menu.MenuPresenter;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
/* loaded from: classes.dex */
public class ActionMenuView extends LinearLayoutCompat implements MenuBuilder.ItemInvoker, MenuView {
    static final int GENERATED_ITEM_PADDING = 4;
    static final int MIN_CELL_SIZE = 56;
    private static final String TAG = "ActionMenuView";
    private MenuPresenter.Callback mActionMenuPresenterCallback;
    private boolean mFormatItems;
    private int mFormatItemsWidth;
    private int mGeneratedItemPadding;
    private MenuBuilder mMenu;
    MenuBuilder.Callback mMenuBuilderCallback;
    private int mMinCellSize;
    OnMenuItemClickListener mOnMenuItemClickListener;
    private Context mPopupContext;
    private int mPopupTheme;
    private ActionMenuPresenter mPresenter;
    private boolean mReserveOverflow;

    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    /* loaded from: classes.dex */
    public interface ActionMenuChildView {
        boolean needsDividerAfter();

        boolean needsDividerBefore();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class ActionMenuPresenterCallback implements MenuPresenter.Callback {
        ActionMenuPresenterCallback() {
        }

        @Override // android.support.v7.view.menu.MenuPresenter.Callback
        public void onCloseMenu(MenuBuilder menuBuilder, boolean z) {
        }

        @Override // android.support.v7.view.menu.MenuPresenter.Callback
        public boolean onOpenSubMenu(MenuBuilder menuBuilder) {
            return false;
        }
    }

    /* loaded from: classes.dex */
    public static class LayoutParams extends LinearLayoutCompat.LayoutParams {
        @ViewDebug.ExportedProperty
        public int cellsUsed;
        @ViewDebug.ExportedProperty
        public boolean expandable;
        boolean expanded;
        @ViewDebug.ExportedProperty
        public int extraPixels;
        @ViewDebug.ExportedProperty
        public boolean isOverflowButton;
        @ViewDebug.ExportedProperty
        public boolean preventEdgeOffset;

        public LayoutParams(int i, int i2) {
            super(i, i2);
            this.isOverflowButton = false;
        }

        LayoutParams(int i, int i2, boolean z) {
            super(i, i2);
            this.isOverflowButton = z;
        }

        public LayoutParams(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
        }

        public LayoutParams(LayoutParams layoutParams) {
            super((ViewGroup.LayoutParams) layoutParams);
            this.isOverflowButton = layoutParams.isOverflowButton;
        }

        public LayoutParams(ViewGroup.LayoutParams layoutParams) {
            super(layoutParams);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class MenuBuilderCallback implements MenuBuilder.Callback {
        MenuBuilderCallback() {
        }

        @Override // android.support.v7.view.menu.MenuBuilder.Callback
        public boolean onMenuItemSelected(MenuBuilder menuBuilder, MenuItem menuItem) {
            return ActionMenuView.this.mOnMenuItemClickListener != null && ActionMenuView.this.mOnMenuItemClickListener.onMenuItemClick(menuItem);
        }

        @Override // android.support.v7.view.menu.MenuBuilder.Callback
        public void onMenuModeChange(MenuBuilder menuBuilder) {
            if (ActionMenuView.this.mMenuBuilderCallback == null) {
                return;
            }
            ActionMenuView.this.mMenuBuilderCallback.onMenuModeChange(menuBuilder);
        }
    }

    /* loaded from: classes.dex */
    public interface OnMenuItemClickListener {
        boolean onMenuItemClick(MenuItem menuItem);
    }

    public ActionMenuView(Context context) {
        this(context, null);
    }

    public ActionMenuView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setBaselineAligned(false);
        float f = context.getResources().getDisplayMetrics().density;
        this.mMinCellSize = (int) (56.0f * f);
        this.mGeneratedItemPadding = (int) (f * 4.0f);
        this.mPopupContext = context;
        this.mPopupTheme = 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int measureChildForCells(View view, int i, int i2, int i3, int i4) {
        int i5;
        LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
        int makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i3) - i4, View.MeasureSpec.getMode(i3));
        ActionMenuItemView actionMenuItemView = view instanceof ActionMenuItemView ? (ActionMenuItemView) view : null;
        boolean z = actionMenuItemView != null && actionMenuItemView.hasText();
        if (i2 > 0 && (!z || i2 >= 2)) {
            view.measure(View.MeasureSpec.makeMeasureSpec(i * i2, Integer.MIN_VALUE), makeMeasureSpec);
            int measuredWidth = view.getMeasuredWidth();
            i5 = measuredWidth / i;
            if (measuredWidth % i != 0) {
                i5++;
            }
            if (z && i5 < 2) {
                i5 = 2;
            }
        } else {
            i5 = 0;
        }
        layoutParams.expandable = !layoutParams.isOverflowButton && z;
        layoutParams.cellsUsed = i5;
        view.measure(View.MeasureSpec.makeMeasureSpec(i5 * i, 1073741824), makeMeasureSpec);
        return i5;
    }

    private void onMeasureExactFormat(int i, int i2) {
        long j;
        float f;
        boolean z;
        boolean z2;
        int i3;
        int i4;
        int i5;
        int i6;
        long j2;
        int i7;
        int i8;
        int i9;
        int mode = View.MeasureSpec.getMode(i2);
        int size = View.MeasureSpec.getSize(i);
        int size2 = View.MeasureSpec.getSize(i2);
        int paddingLeft = getPaddingLeft() + getPaddingRight();
        int paddingTop = getPaddingTop() + getPaddingBottom();
        int childMeasureSpec = getChildMeasureSpec(i2, paddingTop, -2);
        int i10 = size - paddingLeft;
        int i11 = i10 / this.mMinCellSize;
        int i12 = i10 % this.mMinCellSize;
        if (i11 == 0) {
            setMeasuredDimension(i10, 0);
            return;
        }
        int i13 = this.mMinCellSize + (i12 / i11);
        int i14 = 0;
        int i15 = 0;
        int i16 = 0;
        int i17 = 0;
        boolean z3 = false;
        long j3 = 0;
        int childCount = getChildCount();
        int i18 = 0;
        while (i18 < childCount) {
            View childAt = getChildAt(i18);
            if (childAt.getVisibility() != 8) {
                boolean z4 = childAt instanceof ActionMenuItemView;
                int i19 = i17 + 1;
                if (z4) {
                    childAt.setPadding(this.mGeneratedItemPadding, 0, this.mGeneratedItemPadding, 0);
                }
                LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
                layoutParams.expanded = false;
                layoutParams.extraPixels = 0;
                layoutParams.cellsUsed = 0;
                layoutParams.expandable = false;
                layoutParams.leftMargin = 0;
                layoutParams.rightMargin = 0;
                layoutParams.preventEdgeOffset = z4 && ((ActionMenuItemView) childAt).hasText();
                int measureChildForCells = measureChildForCells(childAt, i13, !layoutParams.isOverflowButton ? i11 : 1, childMeasureSpec, paddingTop);
                int max = Math.max(i15, measureChildForCells);
                int i20 = !layoutParams.expandable ? i16 : i16 + 1;
                boolean z5 = !layoutParams.isOverflowButton ? z3 : true;
                int i21 = i11 - measureChildForCells;
                int max2 = Math.max(i14, childAt.getMeasuredHeight());
                if (measureChildForCells != 1) {
                    i6 = i19;
                    i9 = max;
                    long j4 = j3;
                    i7 = max2;
                    i8 = i21;
                    z3 = z5;
                    i16 = i20;
                    j2 = j4;
                } else {
                    long j5 = (1 << i18) | j3;
                    i7 = max2;
                    i8 = i21;
                    i16 = i20;
                    z3 = z5;
                    j2 = j5;
                    i9 = max;
                    i6 = i19;
                }
            } else {
                i6 = i17;
                j2 = j3;
                i7 = i14;
                i8 = i11;
                i9 = i15;
            }
            i18++;
            i15 = i9;
            i14 = i7;
            i11 = i8;
            j3 = j2;
            i17 = i6;
        }
        boolean z6 = z3 && i17 == 2;
        boolean z7 = false;
        long j6 = j3;
        int i22 = i11;
        while (i16 > 0 && i22 > 0) {
            int i23 = Integer.MAX_VALUE;
            long j7 = 0;
            int i24 = 0;
            int i25 = 0;
            while (i25 < childCount) {
                LayoutParams layoutParams2 = (LayoutParams) getChildAt(i25).getLayoutParams();
                if (!layoutParams2.expandable) {
                    i4 = i24;
                    i5 = i23;
                } else if (layoutParams2.cellsUsed < i23) {
                    i5 = layoutParams2.cellsUsed;
                    j7 = 1 << i25;
                    i4 = 1;
                } else if (layoutParams2.cellsUsed != i23) {
                    i4 = i24;
                    i5 = i23;
                } else {
                    j7 |= 1 << i25;
                    i4 = i24 + 1;
                    i5 = i23;
                }
                i25++;
                i23 = i5;
                i24 = i4;
            }
            long j8 = j6 | j7;
            if (i24 > i22) {
                j = j8;
                break;
            }
            int i26 = i23 + 1;
            int i27 = 0;
            int i28 = i22;
            long j9 = j8;
            while (i27 < childCount) {
                View childAt2 = getChildAt(i27);
                LayoutParams layoutParams3 = (LayoutParams) childAt2.getLayoutParams();
                if (((1 << i27) & j7) != 0) {
                    if (z6 && layoutParams3.preventEdgeOffset && i28 == 1) {
                        childAt2.setPadding(this.mGeneratedItemPadding + i13, 0, this.mGeneratedItemPadding, 0);
                    }
                    layoutParams3.cellsUsed++;
                    layoutParams3.expanded = true;
                    i3 = i28 - 1;
                } else if (layoutParams3.cellsUsed != i26) {
                    i3 = i28;
                } else {
                    j9 |= 1 << i27;
                    i3 = i28;
                }
                i27++;
                i28 = i3;
            }
            j6 = j9;
            z7 = true;
            i22 = i28;
        }
        j = j6;
        boolean z8 = !z3 && i17 == 1;
        if (i22 > 0 && j != 0 && (i22 < i17 - 1 || z8 || i15 > 1)) {
            float bitCount = Long.bitCount(j);
            if (z8) {
                f = bitCount;
            } else {
                if ((1 & j) != 0 && !((LayoutParams) getChildAt(0).getLayoutParams()).preventEdgeOffset) {
                    bitCount -= 0.5f;
                }
                f = (((long) (1 << (childCount + (-1)))) & j) != 0 ? ((LayoutParams) getChildAt(childCount + (-1)).getLayoutParams()).preventEdgeOffset ? bitCount : bitCount - 0.5f : bitCount;
            }
            int i29 = f > 0.0f ? (int) ((i22 * i13) / f) : 0;
            z = z7;
            int i30 = 0;
            while (i30 < childCount) {
                if (((1 << i30) & j) == 0) {
                    z2 = z;
                } else {
                    View childAt3 = getChildAt(i30);
                    LayoutParams layoutParams4 = (LayoutParams) childAt3.getLayoutParams();
                    if (childAt3 instanceof ActionMenuItemView) {
                        layoutParams4.extraPixels = i29;
                        layoutParams4.expanded = true;
                        if (i30 == 0 && !layoutParams4.preventEdgeOffset) {
                            layoutParams4.leftMargin = (-i29) / 2;
                        }
                        z2 = true;
                    } else if (layoutParams4.isOverflowButton) {
                        layoutParams4.extraPixels = i29;
                        layoutParams4.expanded = true;
                        layoutParams4.rightMargin = (-i29) / 2;
                        z2 = true;
                    } else {
                        if (i30 != 0) {
                            layoutParams4.leftMargin = i29 / 2;
                        }
                        if (i30 == childCount - 1) {
                            z2 = z;
                        } else {
                            layoutParams4.rightMargin = i29 / 2;
                            z2 = z;
                        }
                    }
                }
                i30++;
                z = z2;
            }
        } else {
            z = z7;
        }
        if (z) {
            int i31 = 0;
            while (true) {
                int i32 = i31;
                if (i32 >= childCount) {
                    break;
                }
                View childAt4 = getChildAt(i32);
                LayoutParams layoutParams5 = (LayoutParams) childAt4.getLayoutParams();
                if (layoutParams5.expanded) {
                    childAt4.measure(View.MeasureSpec.makeMeasureSpec(layoutParams5.extraPixels + (layoutParams5.cellsUsed * i13), 1073741824), childMeasureSpec);
                }
                i31 = i32 + 1;
            }
        }
        if (mode == 1073741824) {
            i14 = size2;
        }
        setMeasuredDimension(i10, i14);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v7.widget.LinearLayoutCompat, android.view.ViewGroup
    public boolean checkLayoutParams(ViewGroup.LayoutParams layoutParams) {
        return layoutParams != null && (layoutParams instanceof LayoutParams);
    }

    public void dismissPopupMenus() {
        if (this.mPresenter == null) {
            return;
        }
        this.mPresenter.dismissPopupMenus();
    }

    @Override // android.view.View
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v7.widget.LinearLayoutCompat, android.view.ViewGroup
    public LayoutParams generateDefaultLayoutParams() {
        LayoutParams layoutParams = new LayoutParams(-2, -2);
        layoutParams.gravity = 16;
        return layoutParams;
    }

    @Override // android.support.v7.widget.LinearLayoutCompat, android.view.ViewGroup
    public LayoutParams generateLayoutParams(AttributeSet attributeSet) {
        return new LayoutParams(getContext(), attributeSet);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v7.widget.LinearLayoutCompat, android.view.ViewGroup
    public LayoutParams generateLayoutParams(ViewGroup.LayoutParams layoutParams) {
        if (layoutParams == null) {
            return generateDefaultLayoutParams();
        }
        LayoutParams layoutParams2 = !(layoutParams instanceof LayoutParams) ? new LayoutParams(layoutParams) : new LayoutParams((LayoutParams) layoutParams);
        if (layoutParams2.gravity <= 0) {
            layoutParams2.gravity = 16;
        }
        return layoutParams2;
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public LayoutParams generateOverflowButtonLayoutParams() {
        LayoutParams generateDefaultLayoutParams = generateDefaultLayoutParams();
        generateDefaultLayoutParams.isOverflowButton = true;
        return generateDefaultLayoutParams;
    }

    public Menu getMenu() {
        if (this.mMenu == null) {
            Context context = getContext();
            this.mMenu = new MenuBuilder(context);
            this.mMenu.setCallback(new MenuBuilderCallback());
            this.mPresenter = new ActionMenuPresenter(context);
            this.mPresenter.setReserveOverflow(true);
            this.mPresenter.setCallback(this.mActionMenuPresenterCallback == null ? new ActionMenuPresenterCallback() : this.mActionMenuPresenterCallback);
            this.mMenu.addMenuPresenter(this.mPresenter, this.mPopupContext);
            this.mPresenter.setMenuView(this);
        }
        return this.mMenu;
    }

    @Nullable
    public Drawable getOverflowIcon() {
        getMenu();
        return this.mPresenter.getOverflowIcon();
    }

    public int getPopupTheme() {
        return this.mPopupTheme;
    }

    @Override // android.support.v7.view.menu.MenuView
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public int getWindowAnimations() {
        return 0;
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    protected boolean hasSupportDividerBeforeChildAt(int i) {
        boolean z = false;
        if (i != 0) {
            View childAt = getChildAt(i - 1);
            View childAt2 = getChildAt(i);
            if (i < getChildCount() && (childAt instanceof ActionMenuChildView)) {
                z = ((ActionMenuChildView) childAt).needsDividerAfter() | false;
            }
            return (i > 0 && (childAt2 instanceof ActionMenuChildView)) ? ((ActionMenuChildView) childAt2).needsDividerBefore() | z : z;
        }
        return false;
    }

    public boolean hideOverflowMenu() {
        return this.mPresenter != null && this.mPresenter.hideOverflowMenu();
    }

    @Override // android.support.v7.view.menu.MenuView
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public void initialize(MenuBuilder menuBuilder) {
        this.mMenu = menuBuilder;
    }

    @Override // android.support.v7.view.menu.MenuBuilder.ItemInvoker
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public boolean invokeItem(MenuItemImpl menuItemImpl) {
        return this.mMenu.performItemAction(menuItemImpl, 0);
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public boolean isOverflowMenuShowPending() {
        return this.mPresenter != null && this.mPresenter.isOverflowMenuShowPending();
    }

    public boolean isOverflowMenuShowing() {
        return this.mPresenter != null && this.mPresenter.isOverflowMenuShowing();
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public boolean isOverflowReserved() {
        return this.mReserveOverflow;
    }

    @Override // android.view.View
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        if (this.mPresenter == null) {
            return;
        }
        this.mPresenter.updateMenuView(false);
        if (this.mPresenter.isOverflowMenuShowing()) {
            this.mPresenter.hideOverflowMenu();
            this.mPresenter.showOverflowMenu();
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        dismissPopupMenus();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v7.widget.LinearLayoutCompat, android.view.ViewGroup, android.view.View
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int i5;
        int i6;
        boolean z2;
        int i7;
        int i8;
        int paddingLeft;
        int i9;
        if (!this.mFormatItems) {
            super.onLayout(z, i, i2, i3, i4);
            return;
        }
        int childCount = getChildCount();
        int i10 = (i4 - i2) / 2;
        int dividerWidth = getDividerWidth();
        int i11 = 0;
        int paddingRight = ((i3 - i) - getPaddingRight()) - getPaddingLeft();
        boolean z3 = false;
        boolean isLayoutRtl = ViewUtils.isLayoutRtl(this);
        int i12 = 0;
        while (i12 < childCount) {
            View childAt = getChildAt(i12);
            if (childAt.getVisibility() != 8) {
                LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
                if (layoutParams.isOverflowButton) {
                    int measuredWidth = childAt.getMeasuredWidth();
                    if (hasSupportDividerBeforeChildAt(i12)) {
                        measuredWidth += dividerWidth;
                    }
                    int measuredHeight = childAt.getMeasuredHeight();
                    if (isLayoutRtl) {
                        paddingLeft = getPaddingLeft() + layoutParams.leftMargin;
                        i9 = paddingLeft + measuredWidth;
                    } else {
                        i9 = (getWidth() - getPaddingRight()) - layoutParams.rightMargin;
                        paddingLeft = i9 - measuredWidth;
                    }
                    int i13 = i10 - (measuredHeight / 2);
                    childAt.layout(paddingLeft, i13, i9, measuredHeight + i13);
                    i8 = paddingRight - measuredWidth;
                    z2 = true;
                    i7 = i11;
                } else {
                    int i14 = layoutParams.rightMargin;
                    hasSupportDividerBeforeChildAt(i12);
                    i7 = i11 + 1;
                    boolean z4 = z3;
                    i8 = paddingRight - (i14 + (childAt.getMeasuredWidth() + layoutParams.leftMargin));
                    z2 = z4;
                }
            } else {
                z2 = z3;
                i7 = i11;
                i8 = paddingRight;
            }
            i12++;
            i11 = i7;
            paddingRight = i8;
            z3 = z2;
        }
        if (childCount == 1 && !z3) {
            View childAt2 = getChildAt(0);
            int measuredWidth2 = childAt2.getMeasuredWidth();
            int measuredHeight2 = childAt2.getMeasuredHeight();
            int i15 = ((i3 - i) / 2) - (measuredWidth2 / 2);
            int i16 = i10 - (measuredHeight2 / 2);
            childAt2.layout(i15, i16, measuredWidth2 + i15, measuredHeight2 + i16);
            return;
        }
        int i17 = i11 - (!z3 ? 1 : 0);
        int max = Math.max(0, i17 <= 0 ? 0 : paddingRight / i17);
        if (isLayoutRtl) {
            int width = getWidth() - getPaddingRight();
            int i18 = 0;
            while (i18 < childCount) {
                View childAt3 = getChildAt(i18);
                LayoutParams layoutParams2 = (LayoutParams) childAt3.getLayoutParams();
                if (childAt3.getVisibility() == 8) {
                    i5 = width;
                } else if (layoutParams2.isOverflowButton) {
                    i5 = width;
                } else {
                    int i19 = width - layoutParams2.rightMargin;
                    int measuredWidth3 = childAt3.getMeasuredWidth();
                    int measuredHeight3 = childAt3.getMeasuredHeight();
                    int i20 = i10 - (measuredHeight3 / 2);
                    childAt3.layout(i19 - measuredWidth3, i20, i19, measuredHeight3 + i20);
                    i5 = i19 - ((layoutParams2.leftMargin + measuredWidth3) + max);
                }
                i18++;
                width = i5;
            }
            return;
        }
        int paddingLeft2 = getPaddingLeft();
        int i21 = 0;
        while (i21 < childCount) {
            View childAt4 = getChildAt(i21);
            LayoutParams layoutParams3 = (LayoutParams) childAt4.getLayoutParams();
            if (childAt4.getVisibility() == 8) {
                i6 = paddingLeft2;
            } else if (layoutParams3.isOverflowButton) {
                i6 = paddingLeft2;
            } else {
                int i22 = paddingLeft2 + layoutParams3.leftMargin;
                int measuredWidth4 = childAt4.getMeasuredWidth();
                int measuredHeight4 = childAt4.getMeasuredHeight();
                int i23 = i10 - (measuredHeight4 / 2);
                childAt4.layout(i22, i23, i22 + measuredWidth4, measuredHeight4 + i23);
                i6 = layoutParams3.rightMargin + measuredWidth4 + max + i22;
            }
            i21++;
            paddingLeft2 = i6;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v7.widget.LinearLayoutCompat, android.view.View
    public void onMeasure(int i, int i2) {
        boolean z = this.mFormatItems;
        this.mFormatItems = View.MeasureSpec.getMode(i) == 1073741824;
        if (z != this.mFormatItems) {
            this.mFormatItemsWidth = 0;
        }
        int size = View.MeasureSpec.getSize(i);
        if (this.mFormatItems && this.mMenu != null && size != this.mFormatItemsWidth) {
            this.mFormatItemsWidth = size;
            this.mMenu.onItemsChanged(true);
        }
        int childCount = getChildCount();
        if (this.mFormatItems && childCount > 0) {
            onMeasureExactFormat(i, i2);
            return;
        }
        for (int i3 = 0; i3 < childCount; i3++) {
            LayoutParams layoutParams = (LayoutParams) getChildAt(i3).getLayoutParams();
            layoutParams.rightMargin = 0;
            layoutParams.leftMargin = 0;
        }
        super.onMeasure(i, i2);
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public MenuBuilder peekMenu() {
        return this.mMenu;
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public void setExpandedActionViewsExclusive(boolean z) {
        this.mPresenter.setExpandedActionViewsExclusive(z);
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public void setMenuCallbacks(MenuPresenter.Callback callback, MenuBuilder.Callback callback2) {
        this.mActionMenuPresenterCallback = callback;
        this.mMenuBuilderCallback = callback2;
    }

    public void setOnMenuItemClickListener(OnMenuItemClickListener onMenuItemClickListener) {
        this.mOnMenuItemClickListener = onMenuItemClickListener;
    }

    public void setOverflowIcon(@Nullable Drawable drawable) {
        getMenu();
        this.mPresenter.setOverflowIcon(drawable);
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public void setOverflowReserved(boolean z) {
        this.mReserveOverflow = z;
    }

    public void setPopupTheme(@StyleRes int i) {
        if (this.mPopupTheme == i) {
            return;
        }
        this.mPopupTheme = i;
        if (i != 0) {
            this.mPopupContext = new ContextThemeWrapper(getContext(), i);
        } else {
            this.mPopupContext = getContext();
        }
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public void setPresenter(ActionMenuPresenter actionMenuPresenter) {
        this.mPresenter = actionMenuPresenter;
        this.mPresenter.setMenuView(this);
    }

    public boolean showOverflowMenu() {
        return this.mPresenter != null && this.mPresenter.showOverflowMenu();
    }
}
