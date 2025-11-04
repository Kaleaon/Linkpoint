// 
// Decompiled by Procyon v0.6.0
// 

package android.support.design.internal;

import android.graphics.drawable.Drawable$ConstantState;
import android.widget.TextView;
import android.support.v4.widget.TextViewCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.TooltipCompat;
import android.view.ViewStub;
import android.graphics.drawable.ColorDrawable;
import android.util.TypedValue;
import android.graphics.drawable.StateListDrawable;
import android.view.ViewGroup$LayoutParams;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v4.view.ViewCompat;
import android.view.ViewGroup;
import android.support.design.R;
import android.view.LayoutInflater;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.view.View;
import android.util.AttributeSet;
import android.content.Context;
import android.widget.CheckedTextView;
import android.support.v7.view.menu.MenuItemImpl;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.widget.FrameLayout;
import android.support.v4.view.AccessibilityDelegateCompat;
import android.support.annotation.RestrictTo;
import android.support.v7.view.menu.MenuView;

@RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
public class NavigationMenuItemView extends ForegroundLinearLayout implements ItemView
{
    private static final int[] CHECKED_STATE_SET;
    private final AccessibilityDelegateCompat mAccessibilityDelegate;
    private FrameLayout mActionArea;
    boolean mCheckable;
    private Drawable mEmptyDrawable;
    private boolean mHasIconTintList;
    private final int mIconSize;
    private ColorStateList mIconTintList;
    private MenuItemImpl mItemData;
    private boolean mNeedsEmptyIcon;
    private final CheckedTextView mTextView;
    
    static {
        CHECKED_STATE_SET = new int[] { 16842912 };
    }
    
    public NavigationMenuItemView(final Context context) {
        this(context, null);
    }
    
    public NavigationMenuItemView(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public NavigationMenuItemView(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
        this.mAccessibilityDelegate = new AccessibilityDelegateCompat() {
            @Override
            public void onInitializeAccessibilityNodeInfo(final View view, final AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
                super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfoCompat);
                accessibilityNodeInfoCompat.setCheckable(NavigationMenuItemView.this.mCheckable);
            }
        };
        this.setOrientation(0);
        LayoutInflater.from(context).inflate(R.layout.design_navigation_menu_item, (ViewGroup)this, true);
        this.mIconSize = context.getResources().getDimensionPixelSize(R.dimen.design_navigation_icon_size);
        (this.mTextView = (CheckedTextView)this.findViewById(R.id.design_menu_item_text)).setDuplicateParentStateEnabled(true);
        ViewCompat.setAccessibilityDelegate((View)this.mTextView, this.mAccessibilityDelegate);
    }
    
    private void adjustAppearance() {
        if (!this.shouldExpandActionArea()) {
            this.mTextView.setVisibility(0);
            if (this.mActionArea != null) {
                final LayoutParams layoutParams = (LayoutParams)this.mActionArea.getLayoutParams();
                layoutParams.width = -2;
                this.mActionArea.setLayoutParams((ViewGroup$LayoutParams)layoutParams);
            }
        }
        else {
            this.mTextView.setVisibility(8);
            if (this.mActionArea != null) {
                final LayoutParams layoutParams2 = (LayoutParams)this.mActionArea.getLayoutParams();
                layoutParams2.width = -1;
                this.mActionArea.setLayoutParams((ViewGroup$LayoutParams)layoutParams2);
            }
        }
    }
    
    private StateListDrawable createDefaultBackground() {
        final TypedValue typedValue = new TypedValue();
        if (!this.getContext().getTheme().resolveAttribute(android.support.v7.appcompat.R.attr.colorControlHighlight, typedValue, true)) {
            return null;
        }
        final StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(NavigationMenuItemView.CHECKED_STATE_SET, (Drawable)new ColorDrawable(typedValue.data));
        stateListDrawable.addState(NavigationMenuItemView.EMPTY_STATE_SET, (Drawable)new ColorDrawable(0));
        return stateListDrawable;
    }
    
    private void setActionView(final View view) {
        if (view != null) {
            if (this.mActionArea == null) {
                this.mActionArea = (FrameLayout)((ViewStub)this.findViewById(R.id.design_menu_item_action_area_stub)).inflate();
            }
            this.mActionArea.removeAllViews();
            this.mActionArea.addView(view);
        }
    }
    
    private boolean shouldExpandActionArea() {
        return this.mItemData.getTitle() == null && this.mItemData.getIcon() == null && this.mItemData.getActionView() != null;
    }
    
    @Override
    public MenuItemImpl getItemData() {
        return this.mItemData;
    }
    
    @Override
    public void initialize(final MenuItemImpl mItemData, int visibility) {
        visibility = 0;
        this.mItemData = mItemData;
        if (!mItemData.isVisible()) {
            visibility = 8;
        }
        this.setVisibility(visibility);
        if (this.getBackground() == null) {
            ViewCompat.setBackground((View)this, (Drawable)this.createDefaultBackground());
        }
        this.setCheckable(mItemData.isCheckable());
        this.setChecked(mItemData.isChecked());
        ((MenuView.ItemView)this).setEnabled(mItemData.isEnabled());
        this.setTitle(mItemData.getTitle());
        this.setIcon(mItemData.getIcon());
        this.setActionView(mItemData.getActionView());
        this.setContentDescription(mItemData.getContentDescription());
        TooltipCompat.setTooltipText((View)this, mItemData.getTooltipText());
        this.adjustAppearance();
    }
    
    protected int[] onCreateDrawableState(final int n) {
        final int[] onCreateDrawableState = super.onCreateDrawableState(n + 1);
        if (this.mItemData != null && this.mItemData.isCheckable() && this.mItemData.isChecked()) {
            mergeDrawableStates(onCreateDrawableState, NavigationMenuItemView.CHECKED_STATE_SET);
        }
        return onCreateDrawableState;
    }
    
    @Override
    public boolean prefersCondensedTitle() {
        return false;
    }
    
    public void recycle() {
        if (this.mActionArea != null) {
            this.mActionArea.removeAllViews();
        }
        this.mTextView.setCompoundDrawables((Drawable)null, (Drawable)null, (Drawable)null, (Drawable)null);
    }
    
    @Override
    public void setCheckable(final boolean mCheckable) {
        this.refreshDrawableState();
        if (this.mCheckable != mCheckable) {
            this.mCheckable = mCheckable;
            this.mAccessibilityDelegate.sendAccessibilityEvent((View)this.mTextView, 2048);
        }
    }
    
    @Override
    public void setChecked(final boolean checked) {
        this.refreshDrawableState();
        this.mTextView.setChecked(checked);
    }
    
    @Override
    public void setIcon(Drawable drawable) {
        if (drawable == null) {
            if (this.mNeedsEmptyIcon) {
                if (this.mEmptyDrawable == null) {
                    this.mEmptyDrawable = ResourcesCompat.getDrawable(this.getResources(), R.drawable.navigation_empty_icon, this.getContext().getTheme());
                    if (this.mEmptyDrawable != null) {
                        this.mEmptyDrawable.setBounds(0, 0, this.mIconSize, this.mIconSize);
                    }
                }
                drawable = this.mEmptyDrawable;
            }
        }
        else {
            if (this.mHasIconTintList) {
                final Drawable$ConstantState constantState = drawable.getConstantState();
                if (constantState != null) {
                    drawable = constantState.newDrawable();
                }
                drawable = DrawableCompat.wrap(drawable).mutate();
                DrawableCompat.setTintList(drawable, this.mIconTintList);
            }
            drawable.setBounds(0, 0, this.mIconSize, this.mIconSize);
        }
        TextViewCompat.setCompoundDrawablesRelative((TextView)this.mTextView, drawable, null, null, null);
    }
    
    void setIconTintList(final ColorStateList mIconTintList) {
        this.mIconTintList = mIconTintList;
        this.mHasIconTintList = (this.mIconTintList != null);
        if (this.mItemData != null) {
            this.setIcon(this.mItemData.getIcon());
        }
    }
    
    public void setNeedsEmptyIcon(final boolean mNeedsEmptyIcon) {
        this.mNeedsEmptyIcon = mNeedsEmptyIcon;
    }
    
    @Override
    public void setShortcut(final boolean b, final char c) {
    }
    
    public void setTextAppearance(final int n) {
        TextViewCompat.setTextAppearance((TextView)this.mTextView, n);
    }
    
    public void setTextColor(final ColorStateList textColor) {
        this.mTextView.setTextColor(textColor);
    }
    
    @Override
    public void setTitle(final CharSequence text) {
        this.mTextView.setText(text);
    }
    
    @Override
    public boolean showsIcon() {
        return true;
    }
}
