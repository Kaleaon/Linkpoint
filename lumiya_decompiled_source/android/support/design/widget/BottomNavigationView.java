package android.support.design.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.R;
import android.support.design.internal.BottomNavigationMenu;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.internal.BottomNavigationPresenter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.AbsSavedState;
import android.support.v4.view.ViewCompat;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.view.SupportMenuInflater;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.TintTypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
/* loaded from: classes.dex */
public class BottomNavigationView extends FrameLayout {
    private static final int[] CHECKED_STATE_SET = {16842912};
    private static final int[] DISABLED_STATE_SET = {-16842910};
    private static final int MENU_PRESENTER_ID = 1;
    private final MenuBuilder mMenu;
    private MenuInflater mMenuInflater;
    private final BottomNavigationMenuView mMenuView;
    private final BottomNavigationPresenter mPresenter;
    private OnNavigationItemReselectedListener mReselectedListener;
    private OnNavigationItemSelectedListener mSelectedListener;

    /* loaded from: classes.dex */
    public interface OnNavigationItemReselectedListener {
        void onNavigationItemReselected(@NonNull MenuItem menuItem);
    }

    /* loaded from: classes.dex */
    public interface OnNavigationItemSelectedListener {
        boolean onNavigationItemSelected(@NonNull MenuItem menuItem);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class SavedState extends AbsSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.ClassLoaderCreator<SavedState>() { // from class: android.support.design.widget.BottomNavigationView.SavedState.1
            @Override // android.os.Parcelable.Creator
            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel, null);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.ClassLoaderCreator
            public SavedState createFromParcel(Parcel parcel, ClassLoader classLoader) {
                return new SavedState(parcel, classLoader);
            }

            @Override // android.os.Parcelable.Creator
            public SavedState[] newArray(int i) {
                return new SavedState[i];
            }
        };
        Bundle menuPresenterState;

        public SavedState(Parcel parcel, ClassLoader classLoader) {
            super(parcel, classLoader);
            readFromParcel(parcel, classLoader);
        }

        public SavedState(Parcelable parcelable) {
            super(parcelable);
        }

        private void readFromParcel(Parcel parcel, ClassLoader classLoader) {
            this.menuPresenterState = parcel.readBundle(classLoader);
        }

        @Override // android.support.v4.view.AbsSavedState, android.os.Parcelable
        public void writeToParcel(@NonNull Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            parcel.writeBundle(this.menuPresenterState);
        }
    }

    public BottomNavigationView(Context context) {
        this(context, null);
    }

    public BottomNavigationView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public BottomNavigationView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mPresenter = new BottomNavigationPresenter();
        ThemeUtils.checkAppCompatTheme(context);
        this.mMenu = new BottomNavigationMenu(context);
        this.mMenuView = new BottomNavigationMenuView(context);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-2, -2);
        layoutParams.gravity = 17;
        this.mMenuView.setLayoutParams(layoutParams);
        this.mPresenter.setBottomNavigationMenuView(this.mMenuView);
        this.mPresenter.setId(1);
        this.mMenuView.setPresenter(this.mPresenter);
        this.mMenu.addMenuPresenter(this.mPresenter);
        this.mPresenter.initForMenu(getContext(), this.mMenu);
        TintTypedArray obtainStyledAttributes = TintTypedArray.obtainStyledAttributes(context, attributeSet, R.styleable.BottomNavigationView, i, R.style.Widget_Design_BottomNavigationView);
        if (obtainStyledAttributes.hasValue(R.styleable.BottomNavigationView_itemIconTint)) {
            this.mMenuView.setIconTintList(obtainStyledAttributes.getColorStateList(R.styleable.BottomNavigationView_itemIconTint));
        } else {
            this.mMenuView.setIconTintList(createDefaultColorStateList(16842808));
        }
        if (obtainStyledAttributes.hasValue(R.styleable.BottomNavigationView_itemTextColor)) {
            this.mMenuView.setItemTextColor(obtainStyledAttributes.getColorStateList(R.styleable.BottomNavigationView_itemTextColor));
        } else {
            this.mMenuView.setItemTextColor(createDefaultColorStateList(16842808));
        }
        if (obtainStyledAttributes.hasValue(R.styleable.BottomNavigationView_elevation)) {
            ViewCompat.setElevation(this, obtainStyledAttributes.getDimensionPixelSize(R.styleable.BottomNavigationView_elevation, 0));
        }
        this.mMenuView.setItemBackgroundRes(obtainStyledAttributes.getResourceId(R.styleable.BottomNavigationView_itemBackground, 0));
        if (obtainStyledAttributes.hasValue(R.styleable.BottomNavigationView_menu)) {
            inflateMenu(obtainStyledAttributes.getResourceId(R.styleable.BottomNavigationView_menu, 0));
        }
        obtainStyledAttributes.recycle();
        addView(this.mMenuView, layoutParams);
        if (Build.VERSION.SDK_INT < 21) {
            addCompatibilityTopDivider(context);
        }
        this.mMenu.setCallback(new MenuBuilder.Callback() { // from class: android.support.design.widget.BottomNavigationView.1
            @Override // android.support.v7.view.menu.MenuBuilder.Callback
            public boolean onMenuItemSelected(MenuBuilder menuBuilder, MenuItem menuItem) {
                if (BottomNavigationView.this.mReselectedListener == null || menuItem.getItemId() != BottomNavigationView.this.getSelectedItemId()) {
                    return (BottomNavigationView.this.mSelectedListener == null || BottomNavigationView.this.mSelectedListener.onNavigationItemSelected(menuItem)) ? false : true;
                }
                BottomNavigationView.this.mReselectedListener.onNavigationItemReselected(menuItem);
                return true;
            }

            @Override // android.support.v7.view.menu.MenuBuilder.Callback
            public void onMenuModeChange(MenuBuilder menuBuilder) {
            }
        });
    }

    private void addCompatibilityTopDivider(Context context) {
        View view = new View(context);
        view.setBackgroundColor(ContextCompat.getColor(context, R.color.design_bottom_navigation_shadow_color));
        view.setLayoutParams(new FrameLayout.LayoutParams(-1, getResources().getDimensionPixelSize(R.dimen.design_bottom_navigation_shadow_height)));
        addView(view);
    }

    private ColorStateList createDefaultColorStateList(int i) {
        TypedValue typedValue = new TypedValue();
        if (getContext().getTheme().resolveAttribute(i, typedValue, true)) {
            ColorStateList colorStateList = AppCompatResources.getColorStateList(getContext(), typedValue.resourceId);
            if (getContext().getTheme().resolveAttribute(android.support.v7.appcompat.R.attr.colorPrimary, typedValue, true)) {
                int i2 = typedValue.data;
                int defaultColor = colorStateList.getDefaultColor();
                return new ColorStateList(new int[][]{DISABLED_STATE_SET, CHECKED_STATE_SET, EMPTY_STATE_SET}, new int[]{colorStateList.getColorForState(DISABLED_STATE_SET, defaultColor), i2, defaultColor});
            }
            return null;
        }
        return null;
    }

    private MenuInflater getMenuInflater() {
        if (this.mMenuInflater == null) {
            this.mMenuInflater = new SupportMenuInflater(getContext());
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
        return this.mMenu;
    }

    @IdRes
    public int getSelectedItemId() {
        return this.mMenuView.getSelectedItemId();
    }

    public void inflateMenu(int i) {
        this.mPresenter.setUpdateSuspended(true);
        getMenuInflater().inflate(i, this.mMenu);
        this.mPresenter.setUpdateSuspended(false);
        this.mPresenter.updateMenuView(true);
    }

    @Override // android.view.View
    protected void onRestoreInstanceState(Parcelable parcelable) {
        if (!(parcelable instanceof SavedState)) {
            super.onRestoreInstanceState(parcelable);
            return;
        }
        SavedState savedState = (SavedState) parcelable;
        super.onRestoreInstanceState(savedState.getSuperState());
        this.mMenu.restorePresenterStates(savedState.menuPresenterState);
    }

    @Override // android.view.View
    protected Parcelable onSaveInstanceState() {
        SavedState savedState = new SavedState(super.onSaveInstanceState());
        savedState.menuPresenterState = new Bundle();
        this.mMenu.savePresenterStates(savedState.menuPresenterState);
        return savedState;
    }

    public void setItemBackgroundResource(@DrawableRes int i) {
        this.mMenuView.setItemBackgroundRes(i);
    }

    public void setItemIconTintList(@Nullable ColorStateList colorStateList) {
        this.mMenuView.setIconTintList(colorStateList);
    }

    public void setItemTextColor(@Nullable ColorStateList colorStateList) {
        this.mMenuView.setItemTextColor(colorStateList);
    }

    public void setOnNavigationItemReselectedListener(@Nullable OnNavigationItemReselectedListener onNavigationItemReselectedListener) {
        this.mReselectedListener = onNavigationItemReselectedListener;
    }

    public void setOnNavigationItemSelectedListener(@Nullable OnNavigationItemSelectedListener onNavigationItemSelectedListener) {
        this.mSelectedListener = onNavigationItemSelectedListener;
    }

    public void setSelectedItemId(@IdRes int i) {
        MenuItem findItem = this.mMenu.findItem(i);
        if (findItem == null || this.mMenu.performItemAction(findItem, this.mPresenter, 0)) {
            return;
        }
        findItem.setChecked(true);
    }
}
