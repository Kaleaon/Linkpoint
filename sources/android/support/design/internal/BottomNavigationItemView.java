package android.support.design.internal;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import android.support.design.R;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.PointerIconCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.view.menu.MenuItemImpl;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.TooltipCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
public class BottomNavigationItemView extends FrameLayout implements MenuView.ItemView {
    private static final int[] CHECKED_STATE_SET = {16842912};
    public static final int INVALID_ITEM_POSITION = -1;
    private final int mDefaultMargin;
    private ImageView mIcon;
    private ColorStateList mIconTint;
    private MenuItemImpl mItemData;
    private int mItemPosition;
    private final TextView mLargeLabel;
    private final float mScaleDownFactor;
    private final float mScaleUpFactor;
    private final int mShiftAmount;
    private boolean mShiftingMode;
    private final TextView mSmallLabel;

    public BottomNavigationItemView(@NonNull Context context) {
        this(context, (AttributeSet) null);
    }

    public BottomNavigationItemView(@NonNull Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public BottomNavigationItemView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mItemPosition = -1;
        Resources resources = getResources();
        int dimensionPixelSize = resources.getDimensionPixelSize(R.dimen.design_bottom_navigation_text_size);
        int dimensionPixelSize2 = resources.getDimensionPixelSize(R.dimen.design_bottom_navigation_active_text_size);
        this.mDefaultMargin = resources.getDimensionPixelSize(R.dimen.design_bottom_navigation_margin);
        this.mShiftAmount = dimensionPixelSize - dimensionPixelSize2;
        this.mScaleUpFactor = (((float) dimensionPixelSize2) * 1.0f) / ((float) dimensionPixelSize);
        this.mScaleDownFactor = (((float) dimensionPixelSize) * 1.0f) / ((float) dimensionPixelSize2);
        LayoutInflater.from(context).inflate(R.layout.design_bottom_navigation_item, this, true);
        setBackgroundResource(R.drawable.design_bottom_navigation_item_background);
        this.mIcon = (ImageView) findViewById(R.id.icon);
        this.mSmallLabel = (TextView) findViewById(R.id.smallLabel);
        this.mLargeLabel = (TextView) findViewById(R.id.largeLabel);
    }

    public MenuItemImpl getItemData() {
        return this.mItemData;
    }

    public int getItemPosition() {
        return this.mItemPosition;
    }

    public void initialize(MenuItemImpl menuItemImpl, int i) {
        this.mItemData = menuItemImpl;
        setCheckable(menuItemImpl.isCheckable());
        setChecked(menuItemImpl.isChecked());
        setEnabled(menuItemImpl.isEnabled());
        setIcon(menuItemImpl.getIcon());
        setTitle(menuItemImpl.getTitle());
        setId(menuItemImpl.getItemId());
        setContentDescription(menuItemImpl.getContentDescription());
        TooltipCompat.setTooltipText(this, menuItemImpl.getTooltipText());
    }

    public int[] onCreateDrawableState(int i) {
        int[] onCreateDrawableState = super.onCreateDrawableState(i + 1);
        if (this.mItemData != null && this.mItemData.isCheckable() && this.mItemData.isChecked()) {
            mergeDrawableStates(onCreateDrawableState, CHECKED_STATE_SET);
        }
        return onCreateDrawableState;
    }

    public boolean prefersCondensedTitle() {
        return false;
    }

    public void setCheckable(boolean z) {
        refreshDrawableState();
    }

    public void setChecked(boolean z) {
        this.mLargeLabel.setPivotX((float) (this.mLargeLabel.getWidth() / 2));
        this.mLargeLabel.setPivotY((float) this.mLargeLabel.getBaseline());
        this.mSmallLabel.setPivotX((float) (this.mSmallLabel.getWidth() / 2));
        this.mSmallLabel.setPivotY((float) this.mSmallLabel.getBaseline());
        if (this.mShiftingMode) {
            if (!z) {
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.mIcon.getLayoutParams();
                layoutParams.gravity = 17;
                layoutParams.topMargin = this.mDefaultMargin;
                this.mIcon.setLayoutParams(layoutParams);
                this.mLargeLabel.setVisibility(4);
                this.mLargeLabel.setScaleX(0.5f);
                this.mLargeLabel.setScaleY(0.5f);
            } else {
                FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.mIcon.getLayoutParams();
                layoutParams2.gravity = 49;
                layoutParams2.topMargin = this.mDefaultMargin;
                this.mIcon.setLayoutParams(layoutParams2);
                this.mLargeLabel.setVisibility(0);
                this.mLargeLabel.setScaleX(1.0f);
                this.mLargeLabel.setScaleY(1.0f);
            }
            this.mSmallLabel.setVisibility(4);
        } else if (!z) {
            FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) this.mIcon.getLayoutParams();
            layoutParams3.gravity = 49;
            layoutParams3.topMargin = this.mDefaultMargin;
            this.mIcon.setLayoutParams(layoutParams3);
            this.mLargeLabel.setVisibility(4);
            this.mSmallLabel.setVisibility(0);
            this.mLargeLabel.setScaleX(this.mScaleDownFactor);
            this.mLargeLabel.setScaleY(this.mScaleDownFactor);
            this.mSmallLabel.setScaleX(1.0f);
            this.mSmallLabel.setScaleY(1.0f);
        } else {
            FrameLayout.LayoutParams layoutParams4 = (FrameLayout.LayoutParams) this.mIcon.getLayoutParams();
            layoutParams4.gravity = 49;
            layoutParams4.topMargin = this.mDefaultMargin + this.mShiftAmount;
            this.mIcon.setLayoutParams(layoutParams4);
            this.mLargeLabel.setVisibility(0);
            this.mSmallLabel.setVisibility(4);
            this.mLargeLabel.setScaleX(1.0f);
            this.mLargeLabel.setScaleY(1.0f);
            this.mSmallLabel.setScaleX(this.mScaleUpFactor);
            this.mSmallLabel.setScaleY(this.mScaleUpFactor);
        }
        refreshDrawableState();
    }

    public void setEnabled(boolean z) {
        super.setEnabled(z);
        this.mSmallLabel.setEnabled(z);
        this.mLargeLabel.setEnabled(z);
        this.mIcon.setEnabled(z);
        if (!z) {
            ViewCompat.setPointerIcon(this, (PointerIconCompat) null);
        } else {
            ViewCompat.setPointerIcon(this, PointerIconCompat.getSystemIcon(getContext(), 1002));
        }
    }

    public void setIcon(Drawable drawable) {
        if (drawable != null) {
            Drawable.ConstantState constantState = drawable.getConstantState();
            if (constantState != null) {
                drawable = constantState.newDrawable();
            }
            drawable = DrawableCompat.wrap(drawable).mutate();
            DrawableCompat.setTintList(drawable, this.mIconTint);
        }
        this.mIcon.setImageDrawable(drawable);
    }

    public void setIconTintList(ColorStateList colorStateList) {
        this.mIconTint = colorStateList;
        if (this.mItemData != null) {
            setIcon(this.mItemData.getIcon());
        }
    }

    public void setItemBackground(int i) {
        ViewCompat.setBackground(this, i != 0 ? ContextCompat.getDrawable(getContext(), i) : null);
    }

    public void setItemPosition(int i) {
        this.mItemPosition = i;
    }

    public void setShiftingMode(boolean z) {
        this.mShiftingMode = z;
    }

    public void setShortcut(boolean z, char c) {
    }

    public void setTextColor(ColorStateList colorStateList) {
        this.mSmallLabel.setTextColor(colorStateList);
        this.mLargeLabel.setTextColor(colorStateList);
    }

    public void setTitle(CharSequence charSequence) {
        this.mSmallLabel.setText(charSequence);
        this.mLargeLabel.setText(charSequence);
    }

    public boolean showsIcon() {
        return true;
    }
}
