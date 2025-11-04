// 
// Decompiled by Procyon v0.6.0
// 

package android.support.design.internal;

import android.support.annotation.NonNull;
import android.os.Parcel;
import android.os.Parcelable$Creator;
import android.support.v7.view.menu.SubMenuBuilder;
import android.os.Parcelable;
import android.content.Context;
import android.support.v7.view.menu.MenuView;
import android.view.ViewGroup;
import android.support.v7.view.menu.MenuItemImpl;
import android.support.v7.view.menu.MenuBuilder;
import android.support.annotation.RestrictTo;
import android.support.v7.view.menu.MenuPresenter;

@RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
public class BottomNavigationPresenter implements MenuPresenter
{
    private int mId;
    private MenuBuilder mMenu;
    private BottomNavigationMenuView mMenuView;
    private boolean mUpdateSuspended;
    
    public BottomNavigationPresenter() {
        this.mUpdateSuspended = false;
    }
    
    @Override
    public boolean collapseItemActionView(final MenuBuilder menuBuilder, final MenuItemImpl menuItemImpl) {
        return false;
    }
    
    @Override
    public boolean expandItemActionView(final MenuBuilder menuBuilder, final MenuItemImpl menuItemImpl) {
        return false;
    }
    
    @Override
    public boolean flagActionItems() {
        return false;
    }
    
    @Override
    public int getId() {
        return this.mId;
    }
    
    @Override
    public MenuView getMenuView(final ViewGroup viewGroup) {
        return this.mMenuView;
    }
    
    @Override
    public void initForMenu(final Context context, final MenuBuilder mMenu) {
        this.mMenuView.initialize(this.mMenu);
        this.mMenu = mMenu;
    }
    
    @Override
    public void onCloseMenu(final MenuBuilder menuBuilder, final boolean b) {
    }
    
    @Override
    public void onRestoreInstanceState(final Parcelable parcelable) {
        if (parcelable instanceof SavedState) {
            this.mMenuView.tryRestoreSelectedItemId(((SavedState)parcelable).selectedItemId);
        }
    }
    
    @Override
    public Parcelable onSaveInstanceState() {
        final SavedState savedState = new SavedState();
        savedState.selectedItemId = this.mMenuView.getSelectedItemId();
        return (Parcelable)savedState;
    }
    
    @Override
    public boolean onSubMenuSelected(final SubMenuBuilder subMenuBuilder) {
        return false;
    }
    
    public void setBottomNavigationMenuView(final BottomNavigationMenuView mMenuView) {
        this.mMenuView = mMenuView;
    }
    
    @Override
    public void setCallback(final Callback callback) {
    }
    
    public void setId(final int mId) {
        this.mId = mId;
    }
    
    public void setUpdateSuspended(final boolean mUpdateSuspended) {
        this.mUpdateSuspended = mUpdateSuspended;
    }
    
    @Override
    public void updateMenuView(final boolean b) {
        if (!this.mUpdateSuspended) {
            if (!b) {
                this.mMenuView.updateMenuView();
            }
            else {
                this.mMenuView.buildMenuView();
            }
        }
    }
    
    static class SavedState implements Parcelable
    {
        public static final Parcelable$Creator<SavedState> CREATOR;
        int selectedItemId;
        
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
            this.selectedItemId = parcel.readInt();
        }
        
        public int describeContents() {
            return 0;
        }
        
        public void writeToParcel(@NonNull final Parcel parcel, final int n) {
            parcel.writeInt(this.selectedItemId);
        }
    }
}
