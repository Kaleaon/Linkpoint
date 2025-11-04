// 
// Decompiled by Procyon v0.6.0
// 

package android.support.design.widget;

import android.os.Parcel;
import android.os.Parcelable$ClassLoaderCreator;
import android.os.Parcelable$Creator;
import android.support.v4.view.AbsSavedState;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.support.annotation.Nullable;
import android.support.annotation.DrawableRes;
import android.support.v7.view.SupportMenuInflater;
import android.support.v7.content.res.AppCompatResources;
import android.util.TypedValue;
import android.content.res.ColorStateList;
import android.support.v4.content.ContextCompat;
import android.view.MenuItem;
import android.os.Build$VERSION;
import android.view.View;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.TintTypedArray;
import android.support.design.R;
import android.support.v7.view.menu.MenuPresenter;
import android.view.ViewGroup$LayoutParams;
import android.widget.FrameLayout$LayoutParams;
import android.support.design.internal.BottomNavigationMenu;
import android.util.AttributeSet;
import android.content.Context;
import android.support.design.internal.BottomNavigationPresenter;
import android.support.design.internal.BottomNavigationMenuView;
import android.view.MenuInflater;
import android.support.v7.view.menu.MenuBuilder;
import android.widget.FrameLayout;

public class BottomNavigationView extends FrameLayout
{
    private static final int[] CHECKED_STATE_SET;
    private static final int[] DISABLED_STATE_SET;
    private static final int MENU_PRESENTER_ID = 1;
    private final MenuBuilder mMenu;
    private MenuInflater mMenuInflater;
    private final BottomNavigationMenuView mMenuView;
    private final BottomNavigationPresenter mPresenter;
    private OnNavigationItemReselectedListener mReselectedListener;
    private OnNavigationItemSelectedListener mSelectedListener;
    
    static {
        CHECKED_STATE_SET = new int[] { 16842912 };
        DISABLED_STATE_SET = new int[] { -16842910 };
    }
    
    public BottomNavigationView(final Context context) {
        this(context, null);
    }
    
    public BottomNavigationView(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public BottomNavigationView(final Context context, final AttributeSet set, int resourceId) {
        super(context, set, resourceId);
        this.mPresenter = new BottomNavigationPresenter();
        ThemeUtils.checkAppCompatTheme(context);
        this.mMenu = new BottomNavigationMenu(context);
        this.mMenuView = new BottomNavigationMenuView(context);
        final FrameLayout$LayoutParams layoutParams = new FrameLayout$LayoutParams(-2, -2);
        layoutParams.gravity = 17;
        this.mMenuView.setLayoutParams((ViewGroup$LayoutParams)layoutParams);
        this.mPresenter.setBottomNavigationMenuView(this.mMenuView);
        this.mPresenter.setId(1);
        this.mMenuView.setPresenter(this.mPresenter);
        this.mMenu.addMenuPresenter(this.mPresenter);
        this.mPresenter.initForMenu(this.getContext(), this.mMenu);
        final TintTypedArray obtainStyledAttributes = TintTypedArray.obtainStyledAttributes(context, set, R.styleable.BottomNavigationView, resourceId, R.style.Widget_Design_BottomNavigationView);
        if (!obtainStyledAttributes.hasValue(R.styleable.BottomNavigationView_itemIconTint)) {
            this.mMenuView.setIconTintList(this.createDefaultColorStateList(16842808));
        }
        else {
            this.mMenuView.setIconTintList(obtainStyledAttributes.getColorStateList(R.styleable.BottomNavigationView_itemIconTint));
        }
        if (!obtainStyledAttributes.hasValue(R.styleable.BottomNavigationView_itemTextColor)) {
            this.mMenuView.setItemTextColor(this.createDefaultColorStateList(16842808));
        }
        else {
            this.mMenuView.setItemTextColor(obtainStyledAttributes.getColorStateList(R.styleable.BottomNavigationView_itemTextColor));
        }
        if (obtainStyledAttributes.hasValue(R.styleable.BottomNavigationView_elevation)) {
            ViewCompat.setElevation((View)this, (float)obtainStyledAttributes.getDimensionPixelSize(R.styleable.BottomNavigationView_elevation, 0));
        }
        resourceId = obtainStyledAttributes.getResourceId(R.styleable.BottomNavigationView_itemBackground, 0);
        this.mMenuView.setItemBackgroundRes(resourceId);
        if (obtainStyledAttributes.hasValue(R.styleable.BottomNavigationView_menu)) {
            this.inflateMenu(obtainStyledAttributes.getResourceId(R.styleable.BottomNavigationView_menu, 0));
        }
        obtainStyledAttributes.recycle();
        this.addView((View)this.mMenuView, (ViewGroup$LayoutParams)layoutParams);
        if (Build$VERSION.SDK_INT < 21) {
            this.addCompatibilityTopDivider(context);
        }
        this.mMenu.setCallback((MenuBuilder.Callback)new MenuBuilder.Callback() {
            @Override
            public boolean onMenuItemSelected(final MenuBuilder menuBuilder, final MenuItem menuItem) {
                boolean b = true;
                if (BottomNavigationView.this.mReselectedListener != null && menuItem.getItemId() == BottomNavigationView.this.getSelectedItemId()) {
                    BottomNavigationView.this.mReselectedListener.onNavigationItemReselected(menuItem);
                    return true;
                }
                if (BottomNavigationView.this.mSelectedListener == null || BottomNavigationView.this.mSelectedListener.onNavigationItemSelected(menuItem)) {
                    b = false;
                }
                return b;
            }
            
            @Override
            public void onMenuModeChange(final MenuBuilder menuBuilder) {
            }
        });
    }
    
    private void addCompatibilityTopDivider(final Context context) {
        final View view = new View(context);
        view.setBackgroundColor(ContextCompat.getColor(context, R.color.design_bottom_navigation_shadow_color));
        view.setLayoutParams((ViewGroup$LayoutParams)new FrameLayout$LayoutParams(-1, this.getResources().getDimensionPixelSize(R.dimen.design_bottom_navigation_shadow_height)));
        this.addView(view);
    }
    
    private ColorStateList createDefaultColorStateList(int defaultColor) {
        final TypedValue typedValue = new TypedValue();
        if (!this.getContext().getTheme().resolveAttribute(defaultColor, typedValue, true)) {
            return null;
        }
        final ColorStateList colorStateList = AppCompatResources.getColorStateList(this.getContext(), typedValue.resourceId);
        if (this.getContext().getTheme().resolveAttribute(android.support.v7.appcompat.R.attr.colorPrimary, typedValue, true)) {
            final int data = typedValue.data;
            defaultColor = colorStateList.getDefaultColor();
            return new ColorStateList(new int[][] { BottomNavigationView.DISABLED_STATE_SET, BottomNavigationView.CHECKED_STATE_SET, BottomNavigationView.EMPTY_STATE_SET }, new int[] { colorStateList.getColorForState(BottomNavigationView.DISABLED_STATE_SET, defaultColor), data, defaultColor });
        }
        return null;
    }
    
    private MenuInflater getMenuInflater() {
        if (this.mMenuInflater == null) {
            this.mMenuInflater = new SupportMenuInflater(this.getContext());
        }
        return this.mMenuInflater;
    }
    
    @DrawableRes
    public int getItemBackgroundResource() {
        return this.mMenuView.getItemBackgroundRes();
    }
    
    @Nullable
    public ColorStateList getItemIconTintList() {
        return this.mMenuView.getIconTintList();
    }
    
    @Nullable
    public ColorStateList getItemTextColor() {
        return this.mMenuView.getItemTextColor();
    }
    
    public int getMaxItemCount() {
        return 5;
    }
    
    @NonNull
    public Menu getMenu() {
        return (Menu)this.mMenu;
    }
    
    @IdRes
    public int getSelectedItemId() {
        return this.mMenuView.getSelectedItemId();
    }
    
    public void inflateMenu(final int n) {
        this.mPresenter.setUpdateSuspended(true);
        this.getMenuInflater().inflate(n, (Menu)this.mMenu);
        this.mPresenter.setUpdateSuspended(false);
        this.mPresenter.updateMenuView(true);
    }
    
    protected void onRestoreInstanceState(final Parcelable parcelable) {
        if (parcelable instanceof SavedState) {
            final SavedState savedState = (SavedState)parcelable;
            super.onRestoreInstanceState(savedState.getSuperState());
            this.mMenu.restorePresenterStates(savedState.menuPresenterState);
            return;
        }
        super.onRestoreInstanceState(parcelable);
    }
    
    protected Parcelable onSaveInstanceState() {
        final SavedState savedState = new SavedState(super.onSaveInstanceState());
        savedState.menuPresenterState = new Bundle();
        this.mMenu.savePresenterStates(savedState.menuPresenterState);
        return (Parcelable)savedState;
    }
    
    public void setItemBackgroundResource(@DrawableRes final int itemBackgroundRes) {
        this.mMenuView.setItemBackgroundRes(itemBackgroundRes);
    }
    
    public void setItemIconTintList(@Nullable final ColorStateList iconTintList) {
        this.mMenuView.setIconTintList(iconTintList);
    }
    
    public void setItemTextColor(@Nullable final ColorStateList itemTextColor) {
        this.mMenuView.setItemTextColor(itemTextColor);
    }
    
    public void setOnNavigationItemReselectedListener(@Nullable final OnNavigationItemReselectedListener mReselectedListener) {
        this.mReselectedListener = mReselectedListener;
    }
    
    public void setOnNavigationItemSelectedListener(@Nullable final OnNavigationItemSelectedListener mSelectedListener) {
        this.mSelectedListener = mSelectedListener;
    }
    
    public void setSelectedItemId(@IdRes final int n) {
        final MenuItem item = this.mMenu.findItem(n);
        if (item != null && !this.mMenu.performItemAction(item, this.mPresenter, 0)) {
            item.setChecked(true);
        }
    }
    
    public interface OnNavigationItemReselectedListener
    {
        void onNavigationItemReselected(@NonNull final MenuItem p0);
    }
    
    public interface OnNavigationItemSelectedListener
    {
        boolean onNavigationItemSelected(@NonNull final MenuItem p0);
    }
    
    static class SavedState extends AbsSavedState
    {
        public static final Parcelable$Creator<SavedState> CREATOR;
        Bundle menuPresenterState;
        
        static {
            CREATOR = (Parcelable$Creator)new Parcelable$ClassLoaderCreator<SavedState>() {
                public SavedState createFromParcel(final Parcel parcel) {
                    return new SavedState(parcel, null);
                }
                
                public SavedState createFromParcel(final Parcel parcel, final ClassLoader classLoader) {
                    return new SavedState(parcel, classLoader);
                }
                
                public SavedState[] newArray(final int n) {
                    return new SavedState[n];
                }
            };
        }
        
        public SavedState(final Parcel parcel, final ClassLoader classLoader) {
            super(parcel, classLoader);
            this.readFromParcel(parcel, classLoader);
        }
        
        public SavedState(final Parcelable parcelable) {
            super(parcelable);
        }
        
        private void readFromParcel(final Parcel parcel, final ClassLoader classLoader) {
            this.menuPresenterState = parcel.readBundle(classLoader);
        }
        
        @Override
        public void writeToParcel(@NonNull final Parcel parcel, final int n) {
            super.writeToParcel(parcel, n);
            parcel.writeBundle(this.menuPresenterState);
        }
    }
}
