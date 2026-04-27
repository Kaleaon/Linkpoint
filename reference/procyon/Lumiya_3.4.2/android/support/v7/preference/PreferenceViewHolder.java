// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v7.preference;

import android.support.annotation.IdRes;
import android.support.annotation.RestrictTo;
import android.view.View;
import android.util.SparseArray;
import android.support.v7.widget.RecyclerView;

public class PreferenceViewHolder extends ViewHolder
{
    private final SparseArray<View> mCachedViews;
    private boolean mDividerAllowedAbove;
    private boolean mDividerAllowedBelow;
    
    PreferenceViewHolder(final View view) {
        super(view);
        (this.mCachedViews = (SparseArray<View>)new SparseArray(4)).put(16908310, (Object)view.findViewById(16908310));
        this.mCachedViews.put(16908304, (Object)view.findViewById(16908304));
        this.mCachedViews.put(16908294, (Object)view.findViewById(16908294));
        this.mCachedViews.put(R.id.icon_frame, (Object)view.findViewById(R.id.icon_frame));
        this.mCachedViews.put(16908350, (Object)view.findViewById(16908350));
    }
    
    @RestrictTo({ RestrictTo.Scope.TESTS })
    public static PreferenceViewHolder createInstanceForTests(final View view) {
        return new PreferenceViewHolder(view);
    }
    
    public View findViewById(@IdRes final int n) {
        final View view = (View)this.mCachedViews.get(n);
        if (view == null) {
            final View viewById = this.itemView.findViewById(n);
            if (viewById != null) {
                this.mCachedViews.put(n, (Object)viewById);
            }
            return viewById;
        }
        return view;
    }
    
    public boolean isDividerAllowedAbove() {
        return this.mDividerAllowedAbove;
    }
    
    public boolean isDividerAllowedBelow() {
        return this.mDividerAllowedBelow;
    }
    
    public void setDividerAllowedAbove(final boolean mDividerAllowedAbove) {
        this.mDividerAllowedAbove = mDividerAllowedAbove;
    }
    
    public void setDividerAllowedBelow(final boolean mDividerAllowedBelow) {
        this.mDividerAllowedBelow = mDividerAllowedBelow;
    }
}
