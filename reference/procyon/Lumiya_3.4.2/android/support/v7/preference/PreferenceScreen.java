// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v7.preference;

import android.support.annotation.RestrictTo;
import android.support.v4.content.res.TypedArrayUtils;
import android.util.AttributeSet;
import android.content.Context;

public final class PreferenceScreen extends PreferenceGroup
{
    private boolean mShouldUseGeneratedIds;
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    public PreferenceScreen(final Context context, final AttributeSet set) {
        super(context, set, TypedArrayUtils.getAttr(context, R.attr.preferenceScreenStyle, 16842891));
        this.mShouldUseGeneratedIds = true;
    }
    
    @Override
    protected boolean isOnSameScreenAsChildren() {
        return false;
    }
    
    @Override
    protected void onClick() {
        if (this.getIntent() == null && this.getFragment() == null && this.getPreferenceCount() != 0) {
            final PreferenceManager.OnNavigateToScreenListener onNavigateToScreenListener = this.getPreferenceManager().getOnNavigateToScreenListener();
            if (onNavigateToScreenListener != null) {
                onNavigateToScreenListener.onNavigateToScreen(this);
            }
        }
    }
    
    public void setShouldUseGeneratedIds(final boolean mShouldUseGeneratedIds) {
        if (!this.isAttached()) {
            this.mShouldUseGeneratedIds = mShouldUseGeneratedIds;
            return;
        }
        throw new IllegalStateException("Cannot change the usage of generated IDs while attached to the preference hierarchy");
    }
    
    public boolean shouldUseGeneratedIds() {
        return this.mShouldUseGeneratedIds;
    }
}
