// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v7.widget;

import android.support.v7.view.ContextThemeWrapper;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.content.Context;
import android.support.annotation.Nullable;
import android.content.res.Resources$Theme;
import android.widget.SpinnerAdapter;

public interface ThemedSpinnerAdapter extends SpinnerAdapter
{
    @Nullable
    Resources$Theme getDropDownViewTheme();
    
    void setDropDownViewTheme(@Nullable final Resources$Theme p0);
    
    public static final class Helper
    {
        private final Context mContext;
        private LayoutInflater mDropDownInflater;
        private final LayoutInflater mInflater;
        
        public Helper(@NonNull final Context mContext) {
            this.mContext = mContext;
            this.mInflater = LayoutInflater.from(mContext);
        }
        
        @NonNull
        public LayoutInflater getDropDownViewInflater() {
            LayoutInflater layoutInflater;
            if (this.mDropDownInflater == null) {
                layoutInflater = this.mInflater;
            }
            else {
                layoutInflater = this.mDropDownInflater;
            }
            return layoutInflater;
        }
        
        @Nullable
        public Resources$Theme getDropDownViewTheme() {
            Resources$Theme theme = null;
            if (this.mDropDownInflater != null) {
                theme = this.mDropDownInflater.getContext().getTheme();
            }
            return theme;
        }
        
        public void setDropDownViewTheme(@Nullable final Resources$Theme resources$Theme) {
            if (resources$Theme != null) {
                if (resources$Theme != this.mContext.getTheme()) {
                    this.mDropDownInflater = LayoutInflater.from((Context)new ContextThemeWrapper(this.mContext, resources$Theme));
                }
                else {
                    this.mDropDownInflater = this.mInflater;
                }
            }
            else {
                this.mDropDownInflater = null;
            }
        }
    }
}
