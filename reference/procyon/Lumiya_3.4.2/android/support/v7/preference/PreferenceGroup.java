// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v7.preference;

import android.support.annotation.RestrictTo;
import android.text.TextUtils;
import android.os.Bundle;
import java.util.Collections;
import android.content.res.TypedArray;
import android.support.v4.content.res.TypedArrayUtils;
import java.util.ArrayList;
import android.util.AttributeSet;
import android.content.Context;
import java.util.List;
import android.support.v4.util.SimpleArrayMap;
import android.os.Handler;

public abstract class PreferenceGroup extends Preference
{
    private boolean mAttachedToHierarchy;
    private final Runnable mClearRecycleCacheRunnable;
    private int mCurrentPreferenceOrder;
    private final Handler mHandler;
    private final SimpleArrayMap<String, Long> mIdRecycleCache;
    private boolean mOrderingAsAdded;
    private List<Preference> mPreferenceList;
    
    public PreferenceGroup(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public PreferenceGroup(final Context context, final AttributeSet set, final int n) {
        this(context, set, n, 0);
    }
    
    public PreferenceGroup(final Context context, final AttributeSet set, final int n, final int n2) {
        super(context, set, n, n2);
        this.mOrderingAsAdded = true;
        this.mCurrentPreferenceOrder = 0;
        this.mAttachedToHierarchy = false;
        this.mIdRecycleCache = new SimpleArrayMap<String, Long>();
        this.mHandler = new Handler();
        this.mClearRecycleCacheRunnable = new Runnable() {
            @Override
            public void run() {
                synchronized (this) {
                    PreferenceGroup.this.mIdRecycleCache.clear();
                }
            }
        };
        this.mPreferenceList = new ArrayList<Preference>();
        final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, R.styleable.PreferenceGroup, n, n2);
        this.mOrderingAsAdded = TypedArrayUtils.getBoolean(obtainStyledAttributes, R.styleable.PreferenceGroup_orderingFromXml, R.styleable.PreferenceGroup_orderingFromXml, true);
        obtainStyledAttributes.recycle();
    }
    
    private boolean removePreferenceInt(final Preference preference) {
        while (true) {
            final boolean remove;
            synchronized (this) {
                preference.onPrepareForRemoval();
                if (preference.getParent() == this) {
                    preference.assignParent(null);
                }
                remove = this.mPreferenceList.remove(preference);
                if (!remove) {
                    return remove;
                }
            }
            final Preference preference2;
            final String key = preference2.getKey();
            if (key != null) {
                this.mIdRecycleCache.put(key, preference2.getId());
                this.mHandler.removeCallbacks(this.mClearRecycleCacheRunnable);
                this.mHandler.post(this.mClearRecycleCacheRunnable);
            }
            if (this.mAttachedToHierarchy) {
                preference2.onDetached();
                return remove;
            }
            return remove;
        }
    }
    
    public void addItemFromInflater(final Preference preference) {
        this.addPreference(preference);
    }
    
    public boolean addPreference(final Preference key) {
        if (this.mPreferenceList.contains(key)) {
            return true;
        }
    Label_0155_Outer:
        while (true) {
        Label_0035:
            while (true) {
                Label_0022: {
                    if (key.getOrder() != Integer.MAX_VALUE) {
                        break Label_0022;
                    }
                    Label_0107: {
                        break Label_0107;
                    Label_0099_Outer:
                        while (true) {
                            while (true) {
                            Label_0080_Outer:
                                while (true) {
                                    while (true) {
                                        final String key2;
                                        Label_0171: {
                                            synchronized (this) {
                                                int binarySearch = 0;
                                                this.mPreferenceList.add(binarySearch, key);
                                                monitorexit(this);
                                                final PreferenceManager preferenceManager = this.getPreferenceManager();
                                                key2 = key.getKey();
                                                if (key2 != null) {
                                                    break Label_0171;
                                                }
                                                final long n = preferenceManager.getNextId();
                                                key.onAttachedToHierarchy(preferenceManager, n);
                                                key.assignParent(this);
                                                if (!this.mAttachedToHierarchy) {
                                                    this.notifyHierarchyChanged();
                                                    return true;
                                                }
                                                break Label_0080_Outer;
                                                iftrue(Label_0135:)(this.mOrderingAsAdded);
                                                while (true) {
                                                    break Label_0114;
                                                    binarySearch = binarySearch * -1 - 1;
                                                    break Label_0035;
                                                    iftrue(Label_0022:)(!(key instanceof PreferenceGroup));
                                                    ((PreferenceGroup)key).setOrderingAsAdded(this.mOrderingAsAdded);
                                                    break;
                                                    Label_0135: {
                                                        binarySearch = this.mCurrentPreferenceOrder++;
                                                    }
                                                    key.setOrder(binarySearch);
                                                    continue Label_0155_Outer;
                                                }
                                            }
                                        }
                                        if (this.mIdRecycleCache.containsKey(key2)) {
                                            final long n = this.mIdRecycleCache.get(key2);
                                            this.mIdRecycleCache.remove(key2);
                                            continue Label_0099_Outer;
                                        }
                                        break;
                                    }
                                    continue Label_0080_Outer;
                                }
                                key.onAttached();
                                continue Label_0155_Outer;
                            }
                        }
                    }
                }
                int binarySearch = Collections.binarySearch(this.mPreferenceList, key);
                if (binarySearch < 0) {
                    continue;
                }
                break;
            }
            if (this.onPrepareAddPreference(key)) {
                continue Label_0155_Outer;
            }
            break;
        }
        return false;
    }
    
    protected void dispatchRestoreInstanceState(final Bundle bundle) {
        super.dispatchRestoreInstanceState(bundle);
        for (int preferenceCount = this.getPreferenceCount(), i = 0; i < preferenceCount; ++i) {
            this.getPreference(i).dispatchRestoreInstanceState(bundle);
        }
    }
    
    protected void dispatchSaveInstanceState(final Bundle bundle) {
        super.dispatchSaveInstanceState(bundle);
        for (int preferenceCount = this.getPreferenceCount(), i = 0; i < preferenceCount; ++i) {
            this.getPreference(i).dispatchSaveInstanceState(bundle);
        }
    }
    
    public Preference findPreference(final CharSequence anObject) {
        if (!TextUtils.equals((CharSequence)this.getKey(), anObject)) {
            for (int preferenceCount = this.getPreferenceCount(), i = 0; i < preferenceCount; ++i) {
                final Preference preference = this.getPreference(i);
                final String key = preference.getKey();
                if (key != null && key.equals(anObject)) {
                    return preference;
                }
                if (preference instanceof PreferenceGroup) {
                    final Preference preference2 = ((PreferenceGroup)preference).findPreference(anObject);
                    if (preference2 != null) {
                        return preference2;
                    }
                }
            }
            return null;
        }
        return this;
    }
    
    public Preference getPreference(final int n) {
        return this.mPreferenceList.get(n);
    }
    
    public int getPreferenceCount() {
        return this.mPreferenceList.size();
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    public boolean isAttached() {
        return this.mAttachedToHierarchy;
    }
    
    protected boolean isOnSameScreenAsChildren() {
        return true;
    }
    
    public boolean isOrderingAsAdded() {
        return this.mOrderingAsAdded;
    }
    
    @Override
    public void notifyDependencyChange(final boolean b) {
        super.notifyDependencyChange(b);
        for (int preferenceCount = this.getPreferenceCount(), i = 0; i < preferenceCount; ++i) {
            this.getPreference(i).onParentChanged(this, b);
        }
    }
    
    @Override
    public void onAttached() {
        super.onAttached();
        this.mAttachedToHierarchy = true;
        for (int preferenceCount = this.getPreferenceCount(), i = 0; i < preferenceCount; ++i) {
            this.getPreference(i).onAttached();
        }
    }
    
    @Override
    public void onDetached() {
        int i = 0;
        super.onDetached();
        this.mAttachedToHierarchy = false;
        while (i < this.getPreferenceCount()) {
            this.getPreference(i).onDetached();
            ++i;
        }
    }
    
    protected boolean onPrepareAddPreference(final Preference preference) {
        preference.onParentChanged(this, this.shouldDisableDependents());
        return true;
    }
    
    public void removeAll() {
        synchronized (this) {
            final List<Preference> mPreferenceList = this.mPreferenceList;
            for (int i = mPreferenceList.size() - 1; i >= 0; --i) {
                this.removePreferenceInt((Preference)mPreferenceList.get(0));
            }
            monitorexit(this);
            this.notifyHierarchyChanged();
        }
    }
    
    public boolean removePreference(final Preference preference) {
        final boolean removePreferenceInt = this.removePreferenceInt(preference);
        this.notifyHierarchyChanged();
        return removePreferenceInt;
    }
    
    public void setOrderingAsAdded(final boolean mOrderingAsAdded) {
        this.mOrderingAsAdded = mOrderingAsAdded;
    }
    
    void sortPreferences() {
        synchronized (this) {
            Collections.sort(this.mPreferenceList);
        }
    }
    
    public interface PreferencePositionCallback
    {
        int getPreferenceAdapterPosition(final Preference p0);
        
        int getPreferenceAdapterPosition(final String p0);
    }
}
