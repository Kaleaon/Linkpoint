// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v7.widget;

import android.view.ViewDebug$ExportedProperty;
import android.view.ContextThemeWrapper;
import android.support.annotation.StyleRes;
import android.content.res.Configuration;
import android.view.MenuItem;
import android.support.v7.view.menu.MenuItemImpl;
import android.support.annotation.Nullable;
import android.graphics.drawable.Drawable;
import android.view.Menu;
import android.support.annotation.RestrictTo;
import android.view.accessibility.AccessibilityEvent;
import android.view.ViewGroup$LayoutParams;
import android.support.v7.view.menu.ActionMenuItemView;
import android.view.View$MeasureSpec;
import android.view.View;
import android.util.AttributeSet;
import android.content.Context;
import android.support.v7.view.menu.MenuPresenter;
import android.support.v7.view.menu.MenuView;
import android.support.v7.view.menu.MenuBuilder;

public class ActionMenuView extends LinearLayoutCompat implements ItemInvoker, MenuView
{
    static final int GENERATED_ITEM_PADDING = 4;
    static final int MIN_CELL_SIZE = 56;
    private static final String TAG = "ActionMenuView";
    private MenuPresenter.Callback mActionMenuPresenterCallback;
    private boolean mFormatItems;
    private int mFormatItemsWidth;
    private int mGeneratedItemPadding;
    private MenuBuilder mMenu;
    Callback mMenuBuilderCallback;
    private int mMinCellSize;
    OnMenuItemClickListener mOnMenuItemClickListener;
    private Context mPopupContext;
    private int mPopupTheme;
    private ActionMenuPresenter mPresenter;
    private boolean mReserveOverflow;
    
    public ActionMenuView(final Context context) {
        this(context, null);
    }
    
    public ActionMenuView(final Context mPopupContext, final AttributeSet set) {
        super(mPopupContext, set);
        this.setBaselineAligned(false);
        final float density = mPopupContext.getResources().getDisplayMetrics().density;
        this.mMinCellSize = (int)(56.0f * density);
        this.mGeneratedItemPadding = (int)(density * 4.0f);
        this.mPopupContext = mPopupContext;
        this.mPopupTheme = 0;
    }
    
    static int measureChildForCells(final View view, final int n, int measuredWidth, int n2, int n3) {
        ActionMenuItemView actionMenuItemView = null;
        boolean expandable = true;
        final LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
        final int measureSpec = View$MeasureSpec.makeMeasureSpec(View$MeasureSpec.getSize(n2) - n3, View$MeasureSpec.getMode(n2));
        if (view instanceof ActionMenuItemView) {
            actionMenuItemView = (ActionMenuItemView)view;
        }
        if (actionMenuItemView != null && actionMenuItemView.hasText()) {
            n3 = 1;
        }
        else {
            n3 = 0;
        }
        if (measuredWidth > 0 && (n3 == 0 || measuredWidth >= 2)) {
            view.measure(View$MeasureSpec.makeMeasureSpec(n * measuredWidth, Integer.MIN_VALUE), measureSpec);
            measuredWidth = view.getMeasuredWidth();
            n2 = measuredWidth / n;
            if (measuredWidth % n != 0) {
                ++n2;
            }
            measuredWidth = n2;
            if (n3 != 0 && (measuredWidth = n2) < 2) {
                measuredWidth = 2;
            }
        }
        else {
            measuredWidth = 0;
        }
        if (layoutParams.isOverflowButton || n3 == 0) {
            expandable = false;
        }
        layoutParams.expandable = expandable;
        layoutParams.cellsUsed = measuredWidth;
        view.measure(View$MeasureSpec.makeMeasureSpec(measuredWidth * n, 1073741824), measureSpec);
        return measuredWidth;
    }
    
    private void onMeasureExactFormat(int i, int n) {
        final int mode = View$MeasureSpec.getMode(n);
        final int size = View$MeasureSpec.getSize(i);
        final int size2 = View$MeasureSpec.getSize(n);
        i = this.getPaddingLeft();
        final int paddingRight = this.getPaddingRight();
        final int n2 = this.getPaddingTop() + this.getPaddingBottom();
        final int childMeasureSpec = getChildMeasureSpec(n, n2, -2);
        final int n3 = size - (i + paddingRight);
        i = n3 / this.mMinCellSize;
        n = this.mMinCellSize;
        if (i != 0) {
            final int n4 = this.mMinCellSize + n3 % n / i;
            int a = 0;
            int a2 = 0;
            int n5 = 0;
            int n6 = 0;
            int n7 = 0;
            long j = 0L;
            final int childCount = this.getChildCount();
            int n11;
            int n13;
            for (int k = 0; k < childCount; ++k, n6 = i, i = n11, a2 = n, a = n13) {
                final View child = this.getChildAt(k);
                if (child.getVisibility() != 8) {
                    final boolean b = child instanceof ActionMenuItemView;
                    ++n6;
                    if (b) {
                        child.setPadding(this.mGeneratedItemPadding, 0, this.mGeneratedItemPadding, 0);
                    }
                    final LayoutParams layoutParams = (LayoutParams)child.getLayoutParams();
                    layoutParams.expanded = false;
                    layoutParams.extraPixels = 0;
                    layoutParams.cellsUsed = 0;
                    layoutParams.expandable = false;
                    layoutParams.leftMargin = 0;
                    layoutParams.rightMargin = 0;
                    layoutParams.preventEdgeOffset = (b && ((ActionMenuItemView)child).hasText());
                    if (!layoutParams.isOverflowButton) {
                        n = i;
                    }
                    else {
                        n = 1;
                    }
                    final int measureChildForCells = measureChildForCells(child, n4, n, childMeasureSpec, n2);
                    final int max = Math.max(a2, measureChildForCells);
                    if (!layoutParams.expandable) {
                        n = n5;
                    }
                    else {
                        n = n5 + 1;
                    }
                    int n8;
                    if (!layoutParams.isOverflowButton) {
                        n8 = n7;
                    }
                    else {
                        n8 = 1;
                    }
                    final int n9 = i - measureChildForCells;
                    final int max2 = Math.max(a, child.getMeasuredHeight());
                    if (measureChildForCells != 1) {
                        i = n6;
                        final int n10 = max2;
                        n11 = n9;
                        final int n12 = n8;
                        n13 = n10;
                        n7 = n12;
                        n5 = n;
                        n = max;
                    }
                    else {
                        final long n14 = 1 << k;
                        n5 = n;
                        j |= n14;
                        n = max;
                        i = n6;
                        n7 = n8;
                        n13 = max2;
                        n11 = n9;
                    }
                }
                else {
                    n13 = a;
                    n11 = i;
                    n = a2;
                    i = n6;
                }
            }
            final boolean b2 = n7 != 0 && n6 == 2;
            int n15 = 0;
            int n16;
            for (n16 = i; n5 > 0 && n16 > 0; n16 = n) {
                i = Integer.MAX_VALUE;
                long n17 = 0L;
                n = 0;
                int n21;
                for (int l = 0; l < childCount; l = n21) {
                    final LayoutParams layoutParams2 = (LayoutParams)this.getChildAt(l).getLayoutParams();
                    if (layoutParams2.expandable) {
                        if (layoutParams2.cellsUsed >= i) {
                            if (layoutParams2.cellsUsed != i) {
                                final int n18 = n;
                                n = i;
                                i = n18;
                            }
                            else {
                                n17 |= 1 << l;
                                final int n19 = n + 1;
                                n = i;
                                i = n19;
                            }
                        }
                        else {
                            n = layoutParams2.cellsUsed;
                            n17 = 1 << l;
                            i = 1;
                        }
                    }
                    else {
                        final int n20 = i;
                        i = n;
                        n = n20;
                    }
                    n21 = l + 1;
                    final int n22 = n;
                    n = i;
                    i = n22;
                }
                j |= n17;
                if (n > n16) {
                    break;
                }
                int n23 = 0;
                n = n16;
                while (n23 < childCount) {
                    final View child2 = this.getChildAt(n23);
                    final LayoutParams layoutParams3 = (LayoutParams)child2.getLayoutParams();
                    if (((long)(1 << n23) & n17) == 0x0L) {
                        if (layoutParams3.cellsUsed == i + 1) {
                            j |= 1 << n23;
                        }
                    }
                    else {
                        if (b2 && layoutParams3.preventEdgeOffset && n == 1) {
                            child2.setPadding(this.mGeneratedItemPadding + n4, 0, this.mGeneratedItemPadding, 0);
                        }
                        ++layoutParams3.cellsUsed;
                        layoutParams3.expanded = true;
                        --n;
                    }
                    ++n23;
                }
                n15 = 1;
            }
            if (n7 == 0 && n6 == 1) {
                i = 1;
            }
            else {
                i = 0;
            }
            int n27;
            if (n16 > 0 && j != 0L && (n16 < n6 - 1 || i != 0 || a2 > 1)) {
                final float n24 = (float)Long.bitCount(j);
                float n25;
                if (i != 0) {
                    n25 = n24;
                }
                else {
                    n25 = n24;
                    if ((0x1L & j) != 0x0L) {
                        if (((LayoutParams)this.getChildAt(0).getLayoutParams()).preventEdgeOffset) {
                            n25 = n24;
                        }
                        else {
                            n25 = n24 - 0.5f;
                        }
                    }
                    if (((long)(1 << childCount - 1) & j) != 0x0L) {
                        if (!((LayoutParams)this.getChildAt(childCount - 1).getLayoutParams()).preventEdgeOffset) {
                            n25 -= 0.5f;
                        }
                    }
                }
                if (n25 > 0.0f) {
                    n = (int)(n16 * n4 / n25);
                }
                else {
                    n = 0;
                }
                i = n15;
                int n26 = 0;
                while (true) {
                    n27 = i;
                    if (n26 >= childCount) {
                        break;
                    }
                    if (((long)(1 << n26) & j) != 0x0L) {
                        final View child3 = this.getChildAt(n26);
                        final LayoutParams layoutParams4 = (LayoutParams)child3.getLayoutParams();
                        if (!(child3 instanceof ActionMenuItemView)) {
                            if (!layoutParams4.isOverflowButton) {
                                if (n26 != 0) {
                                    layoutParams4.leftMargin = n / 2;
                                }
                                if (n26 != childCount - 1) {
                                    layoutParams4.rightMargin = n / 2;
                                }
                            }
                            else {
                                layoutParams4.extraPixels = n;
                                layoutParams4.expanded = true;
                                layoutParams4.rightMargin = -n / 2;
                                i = 1;
                            }
                        }
                        else {
                            layoutParams4.extraPixels = n;
                            layoutParams4.expanded = true;
                            if (n26 == 0 && !layoutParams4.preventEdgeOffset) {
                                layoutParams4.leftMargin = -n / 2;
                            }
                            i = 1;
                        }
                    }
                    ++n26;
                }
            }
            else {
                n27 = n15;
            }
            if (n27 != 0) {
                View child4;
                LayoutParams layoutParams5;
                for (i = 0; i < childCount; ++i) {
                    child4 = this.getChildAt(i);
                    layoutParams5 = (LayoutParams)child4.getLayoutParams();
                    if (layoutParams5.expanded) {
                        n = layoutParams5.cellsUsed;
                        child4.measure(View$MeasureSpec.makeMeasureSpec(layoutParams5.extraPixels + n * n4, 1073741824), childMeasureSpec);
                    }
                }
            }
            if (mode == 1073741824) {
                a = size2;
            }
            this.setMeasuredDimension(n3, a);
            return;
        }
        this.setMeasuredDimension(n3, 0);
    }
    
    @Override
    protected boolean checkLayoutParams(final ViewGroup$LayoutParams viewGroup$LayoutParams) {
        boolean b = false;
        if (viewGroup$LayoutParams != null && viewGroup$LayoutParams instanceof LayoutParams) {
            b = true;
        }
        return b;
    }
    
    public void dismissPopupMenus() {
        if (this.mPresenter != null) {
            this.mPresenter.dismissPopupMenus();
        }
    }
    
    public boolean dispatchPopulateAccessibilityEvent(final AccessibilityEvent accessibilityEvent) {
        return false;
    }
    
    protected LayoutParams generateDefaultLayoutParams() {
        final LayoutParams layoutParams = new LayoutParams(-2, -2);
        layoutParams.gravity = 16;
        return layoutParams;
    }
    
    public LayoutParams generateLayoutParams(final AttributeSet set) {
        return new LayoutParams(this.getContext(), set);
    }
    
    protected LayoutParams generateLayoutParams(final ViewGroup$LayoutParams viewGroup$LayoutParams) {
        if (viewGroup$LayoutParams == null) {
            return this.generateDefaultLayoutParams();
        }
        LayoutParams layoutParams;
        if (!(viewGroup$LayoutParams instanceof LayoutParams)) {
            layoutParams = new LayoutParams(viewGroup$LayoutParams);
        }
        else {
            layoutParams = new LayoutParams((LayoutParams)viewGroup$LayoutParams);
        }
        if (layoutParams.gravity <= 0) {
            layoutParams.gravity = 16;
        }
        return layoutParams;
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    public LayoutParams generateOverflowButtonLayoutParams() {
        final LayoutParams generateDefaultLayoutParams = this.generateDefaultLayoutParams();
        generateDefaultLayoutParams.isOverflowButton = true;
        return generateDefaultLayoutParams;
    }
    
    public Menu getMenu() {
        if (this.mMenu == null) {
            final Context context = this.getContext();
            (this.mMenu = new MenuBuilder(context)).setCallback((MenuBuilder.Callback)new MenuBuilderCallback());
            (this.mPresenter = new ActionMenuPresenter(context)).setReserveOverflow(true);
            final ActionMenuPresenter mPresenter = this.mPresenter;
            MenuPresenter.Callback mActionMenuPresenterCallback;
            if (this.mActionMenuPresenterCallback == null) {
                mActionMenuPresenterCallback = new ActionMenuPresenterCallback();
            }
            else {
                mActionMenuPresenterCallback = this.mActionMenuPresenterCallback;
            }
            mPresenter.setCallback(mActionMenuPresenterCallback);
            this.mMenu.addMenuPresenter(this.mPresenter, this.mPopupContext);
            this.mPresenter.setMenuView(this);
        }
        return (Menu)this.mMenu;
    }
    
    @Nullable
    public Drawable getOverflowIcon() {
        this.getMenu();
        return this.mPresenter.getOverflowIcon();
    }
    
    public int getPopupTheme() {
        return this.mPopupTheme;
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    @Override
    public int getWindowAnimations() {
        return 0;
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    protected boolean hasSupportDividerBeforeChildAt(final int n) {
        boolean b = false;
        if (n != 0) {
            final View child = this.getChildAt(n - 1);
            final View child2 = this.getChildAt(n);
            if (n < this.getChildCount() && child instanceof ActionMenuChildView) {
                b = (((ActionMenuChildView)child).needsDividerAfter() | false);
            }
            if (n > 0 && child2 instanceof ActionMenuChildView) {
                b |= ((ActionMenuChildView)child2).needsDividerBefore();
            }
            return b;
        }
        return false;
    }
    
    public boolean hideOverflowMenu() {
        boolean b = false;
        if (this.mPresenter != null && this.mPresenter.hideOverflowMenu()) {
            b = true;
        }
        return b;
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    @Override
    public void initialize(final MenuBuilder mMenu) {
        this.mMenu = mMenu;
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    @Override
    public boolean invokeItem(final MenuItemImpl menuItemImpl) {
        return this.mMenu.performItemAction((MenuItem)menuItemImpl, 0);
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    public boolean isOverflowMenuShowPending() {
        boolean b = false;
        if (this.mPresenter != null && this.mPresenter.isOverflowMenuShowPending()) {
            b = true;
        }
        return b;
    }
    
    public boolean isOverflowMenuShowing() {
        boolean b = false;
        if (this.mPresenter != null && this.mPresenter.isOverflowMenuShowing()) {
            b = true;
        }
        return b;
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    public boolean isOverflowReserved() {
        return this.mReserveOverflow;
    }
    
    public void onConfigurationChanged(final Configuration configuration) {
        super.onConfigurationChanged(configuration);
        if (this.mPresenter != null) {
            this.mPresenter.updateMenuView(false);
            if (this.mPresenter.isOverflowMenuShowing()) {
                this.mPresenter.hideOverflowMenu();
                this.mPresenter.showOverflowMenu();
            }
        }
    }
    
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.dismissPopupMenus();
    }
    
    @Override
    protected void onLayout(final boolean b, int b2, int i, int max, int n) {
        if (!this.mFormatItems) {
            super.onLayout(b, b2, i, max, n);
            return;
        }
        final int childCount = this.getChildCount();
        final int n2 = (n - i) / 2;
        final int dividerWidth = this.getDividerWidth();
        i = 0;
        int n3 = max - b2 - this.getPaddingRight() - this.getPaddingLeft();
        n = 0;
        final boolean layoutRtl = ViewUtils.isLayoutRtl((View)this);
        int n12;
        for (int j = 0; j < childCount; j = n12) {
            final View child = this.getChildAt(j);
            int n6;
            if (child.getVisibility() != 8) {
                final LayoutParams layoutParams = (LayoutParams)child.getLayoutParams();
                if (!layoutParams.isOverflowButton) {
                    final int measuredWidth = child.getMeasuredWidth();
                    final int leftMargin = layoutParams.leftMargin;
                    final int rightMargin = layoutParams.rightMargin;
                    this.hasSupportDividerBeforeChildAt(j);
                    final int n4 = i + 1;
                    final int n5 = n3 - (rightMargin + (measuredWidth + leftMargin));
                    i = n;
                    n6 = n4;
                    n = n5;
                }
                else {
                    n = child.getMeasuredWidth();
                    if (this.hasSupportDividerBeforeChildAt(j)) {
                        n += dividerWidth;
                    }
                    final int measuredHeight = child.getMeasuredHeight();
                    int n7;
                    int n8;
                    if (!layoutRtl) {
                        n7 = this.getWidth() - this.getPaddingRight() - layoutParams.rightMargin;
                        n8 = n7 - n;
                    }
                    else {
                        n8 = this.getPaddingLeft() + layoutParams.leftMargin;
                        n7 = n8 + n;
                    }
                    final int n9 = n2 - measuredHeight / 2;
                    child.layout(n8, n9, n7, measuredHeight + n9);
                    n = n3 - n;
                    final int n10 = 1;
                    n6 = i;
                    i = n10;
                }
            }
            else {
                final int n11 = i;
                i = n;
                n = n3;
                n6 = n11;
            }
            n12 = j + 1;
            final int n13 = n6;
            n3 = n;
            n = i;
            i = n13;
        }
        if (childCount == 1 && n == 0) {
            final View child2 = this.getChildAt(0);
            i = child2.getMeasuredWidth();
            n = child2.getMeasuredHeight();
            b2 = (max - b2) / 2 - i / 2;
            max = n2 - n / 2;
            child2.layout(b2, max, i + b2, n + max);
            return;
        }
        if (n == 0) {
            b2 = 1;
        }
        else {
            b2 = 0;
        }
        b2 = i - b2;
        if (b2 <= 0) {
            b2 = 0;
        }
        else {
            b2 = n3 / b2;
        }
        max = Math.max(0, b2);
        if (!layoutRtl) {
            b2 = this.getPaddingLeft();
            View child3;
            LayoutParams layoutParams2;
            int measuredWidth2;
            int measuredHeight2;
            for (i = 0; i < childCount; ++i) {
                child3 = this.getChildAt(i);
                layoutParams2 = (LayoutParams)child3.getLayoutParams();
                if (child3.getVisibility() != 8) {
                    if (!layoutParams2.isOverflowButton) {
                        b2 += layoutParams2.leftMargin;
                        measuredWidth2 = child3.getMeasuredWidth();
                        measuredHeight2 = child3.getMeasuredHeight();
                        n = n2 - measuredHeight2 / 2;
                        child3.layout(b2, n, b2 + measuredWidth2, measuredHeight2 + n);
                        b2 += layoutParams2.rightMargin + measuredWidth2 + max;
                    }
                }
            }
        }
        else {
            b2 = this.getWidth() - this.getPaddingRight();
            View child4;
            LayoutParams layoutParams3;
            int n14;
            int n15;
            for (i = 0; i < childCount; ++i) {
                child4 = this.getChildAt(i);
                layoutParams3 = (LayoutParams)child4.getLayoutParams();
                if (child4.getVisibility() != 8) {
                    if (!layoutParams3.isOverflowButton) {
                        n14 = b2 - layoutParams3.rightMargin;
                        n = child4.getMeasuredWidth();
                        b2 = child4.getMeasuredHeight();
                        n15 = n2 - b2 / 2;
                        child4.layout(n14 - n, n15, n14, b2 + n15);
                        b2 = n14 - (layoutParams3.leftMargin + n + max);
                    }
                }
            }
        }
    }
    
    @Override
    protected void onMeasure(final int n, final int n2) {
        final boolean mFormatItems = this.mFormatItems;
        this.mFormatItems = (View$MeasureSpec.getMode(n) == 1073741824);
        if (mFormatItems != this.mFormatItems) {
            this.mFormatItemsWidth = 0;
        }
        final int size = View$MeasureSpec.getSize(n);
        if (this.mFormatItems && this.mMenu != null && size != this.mFormatItemsWidth) {
            this.mFormatItemsWidth = size;
            this.mMenu.onItemsChanged(true);
        }
        final int childCount = this.getChildCount();
        if (this.mFormatItems && childCount > 0) {
            this.onMeasureExactFormat(n, n2);
        }
        else {
            for (int i = 0; i < childCount; ++i) {
                final LayoutParams layoutParams = (LayoutParams)this.getChildAt(i).getLayoutParams();
                layoutParams.rightMargin = 0;
                layoutParams.leftMargin = 0;
            }
            super.onMeasure(n, n2);
        }
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    public MenuBuilder peekMenu() {
        return this.mMenu;
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    public void setExpandedActionViewsExclusive(final boolean expandedActionViewsExclusive) {
        this.mPresenter.setExpandedActionViewsExclusive(expandedActionViewsExclusive);
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    public void setMenuCallbacks(final MenuPresenter.Callback mActionMenuPresenterCallback, final Callback mMenuBuilderCallback) {
        this.mActionMenuPresenterCallback = mActionMenuPresenterCallback;
        this.mMenuBuilderCallback = mMenuBuilderCallback;
    }
    
    public void setOnMenuItemClickListener(final OnMenuItemClickListener mOnMenuItemClickListener) {
        this.mOnMenuItemClickListener = mOnMenuItemClickListener;
    }
    
    public void setOverflowIcon(@Nullable final Drawable overflowIcon) {
        this.getMenu();
        this.mPresenter.setOverflowIcon(overflowIcon);
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    public void setOverflowReserved(final boolean mReserveOverflow) {
        this.mReserveOverflow = mReserveOverflow;
    }
    
    public void setPopupTheme(@StyleRes final int mPopupTheme) {
        if (this.mPopupTheme != mPopupTheme) {
            if ((this.mPopupTheme = mPopupTheme) != 0) {
                this.mPopupContext = (Context)new ContextThemeWrapper(this.getContext(), mPopupTheme);
            }
            else {
                this.mPopupContext = this.getContext();
            }
        }
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    public void setPresenter(final ActionMenuPresenter mPresenter) {
        (this.mPresenter = mPresenter).setMenuView(this);
    }
    
    public boolean showOverflowMenu() {
        boolean b = false;
        if (this.mPresenter != null && this.mPresenter.showOverflowMenu()) {
            b = true;
        }
        return b;
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    public interface ActionMenuChildView
    {
        boolean needsDividerAfter();
        
        boolean needsDividerBefore();
    }
    
    private static class ActionMenuPresenterCallback implements MenuPresenter.Callback
    {
        ActionMenuPresenterCallback() {
        }
        
        @Override
        public void onCloseMenu(final MenuBuilder menuBuilder, final boolean b) {
        }
        
        @Override
        public boolean onOpenSubMenu(final MenuBuilder menuBuilder) {
            return false;
        }
    }
    
    public static class LayoutParams extends LinearLayoutCompat.LayoutParams
    {
        @ViewDebug$ExportedProperty
        public int cellsUsed;
        @ViewDebug$ExportedProperty
        public boolean expandable;
        boolean expanded;
        @ViewDebug$ExportedProperty
        public int extraPixels;
        @ViewDebug$ExportedProperty
        public boolean isOverflowButton;
        @ViewDebug$ExportedProperty
        public boolean preventEdgeOffset;
        
        public LayoutParams(final int n, final int n2) {
            super(n, n2);
            this.isOverflowButton = false;
        }
        
        LayoutParams(final int n, final int n2, final boolean isOverflowButton) {
            super(n, n2);
            this.isOverflowButton = isOverflowButton;
        }
        
        public LayoutParams(final Context context, final AttributeSet set) {
            super(context, set);
        }
        
        public LayoutParams(final LayoutParams layoutParams) {
            super((ViewGroup$LayoutParams)layoutParams);
            this.isOverflowButton = layoutParams.isOverflowButton;
        }
        
        public LayoutParams(final ViewGroup$LayoutParams viewGroup$LayoutParams) {
            super(viewGroup$LayoutParams);
        }
    }
    
    private class MenuBuilderCallback implements Callback
    {
        MenuBuilderCallback() {
        }
        
        @Override
        public boolean onMenuItemSelected(final MenuBuilder menuBuilder, final MenuItem menuItem) {
            boolean b = false;
            if (ActionMenuView.this.mOnMenuItemClickListener != null && ActionMenuView.this.mOnMenuItemClickListener.onMenuItemClick(menuItem)) {
                b = true;
            }
            return b;
        }
        
        @Override
        public void onMenuModeChange(final MenuBuilder menuBuilder) {
            if (ActionMenuView.this.mMenuBuilderCallback != null) {
                ActionMenuView.this.mMenuBuilderCallback.onMenuModeChange(menuBuilder);
            }
        }
    }
    
    public interface OnMenuItemClickListener
    {
        boolean onMenuItemClick(final MenuItem p0);
    }
}
