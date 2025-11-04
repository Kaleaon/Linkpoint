// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v7.preference;

import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v4.content.res.TypedArrayUtils;
import android.util.AttributeSet;
import android.content.Context;

public class PreferenceCategory extends PreferenceGroup
{
    private static final String TAG = "PreferenceCategory";
    
    public PreferenceCategory(final Context context) {
        this(context, null);
    }
    
    public PreferenceCategory(final Context context, final AttributeSet set) {
        this(context, set, TypedArrayUtils.getAttr(context, R.attr.preferenceCategoryStyle, 16842892));
    }
    
    public PreferenceCategory(final Context context, final AttributeSet set, final int n) {
        this(context, set, n, 0);
    }
    
    public PreferenceCategory(final Context context, final AttributeSet set, final int n, final int n2) {
        super(context, set, n, n2);
    }
    
    @Override
    public boolean isEnabled() {
        return false;
    }
    
    @Override
    public void onInitializeAccessibilityNodeInfo(final AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfoCompat);
        final AccessibilityNodeInfoCompat.CollectionItemInfoCompat collectionItemInfo = accessibilityNodeInfoCompat.getCollectionItemInfo();
        if (collectionItemInfo != null) {
            accessibilityNodeInfoCompat.setCollectionItemInfo(AccessibilityNodeInfoCompat.CollectionItemInfoCompat.obtain(collectionItemInfo.getRowIndex(), collectionItemInfo.getRowSpan(), collectionItemInfo.getColumnIndex(), collectionItemInfo.getColumnSpan(), true, collectionItemInfo.isSelected()));
        }
    }
    
    @Override
    protected boolean onPrepareAddPreference(final Preference preference) {
        if (!(preference instanceof PreferenceCategory)) {
            return super.onPrepareAddPreference(preference);
        }
        throw new IllegalArgumentException("Cannot add a PreferenceCategory directly to a PreferenceCategory");
    }
    
    @Override
    public boolean shouldDisableDependents() {
        boolean b = false;
        if (!super.isEnabled()) {
            b = true;
        }
        return b;
    }
}
