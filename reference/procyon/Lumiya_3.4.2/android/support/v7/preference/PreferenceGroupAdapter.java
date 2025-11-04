// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v7.preference;

import android.view.View;
import android.graphics.drawable.Drawable;
import android.content.res.TypedArray;
import android.support.v4.view.ViewCompat;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.text.TextUtils;
import java.util.Iterator;
import android.support.v7.util.DiffUtil;
import java.util.ArrayList;
import java.util.List;
import android.os.Handler;
import android.support.annotation.RestrictTo;
import android.support.v7.widget.RecyclerView;

@RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
public class PreferenceGroupAdapter extends Adapter<PreferenceViewHolder> implements OnPreferenceChangeInternalListener, PreferencePositionCallback
{
    private static final String TAG = "PreferenceGroupAdapter";
    private Handler mHandler;
    private PreferenceGroup mPreferenceGroup;
    private List<PreferenceLayout> mPreferenceLayouts;
    private List<Preference> mPreferenceList;
    private List<Preference> mPreferenceListInternal;
    private Runnable mSyncRunnable;
    private PreferenceLayout mTempPreferenceLayout;
    
    public PreferenceGroupAdapter(final PreferenceGroup mPreferenceGroup) {
        this.mTempPreferenceLayout = new PreferenceLayout();
        this.mHandler = new Handler();
        this.mSyncRunnable = new Runnable() {
            @Override
            public void run() {
                PreferenceGroupAdapter.this.syncMyPreferences();
            }
        };
        (this.mPreferenceGroup = mPreferenceGroup).setOnPreferenceChangeInternalListener((Preference.OnPreferenceChangeInternalListener)this);
        this.mPreferenceList = new ArrayList<Preference>();
        this.mPreferenceListInternal = new ArrayList<Preference>();
        this.mPreferenceLayouts = new ArrayList<PreferenceLayout>();
        if (!(this.mPreferenceGroup instanceof PreferenceScreen)) {
            ((RecyclerView.Adapter)this).setHasStableIds(true);
        }
        else {
            ((RecyclerView.Adapter)this).setHasStableIds(((PreferenceScreen)this.mPreferenceGroup).shouldUseGeneratedIds());
        }
        this.syncMyPreferences();
    }
    
    private void addPreferenceClassName(final Preference preference) {
        final PreferenceLayout preferenceLayout = this.createPreferenceLayout(preference, null);
        if (!this.mPreferenceLayouts.contains(preferenceLayout)) {
            this.mPreferenceLayouts.add(preferenceLayout);
        }
    }
    
    private PreferenceLayout createPreferenceLayout(final Preference preference, final PreferenceLayout preferenceLayout) {
        PreferenceLayout preferenceLayout2 = preferenceLayout;
        if (preferenceLayout == null) {
            preferenceLayout2 = new PreferenceLayout();
        }
        preferenceLayout2.name = preference.getClass().getName();
        preferenceLayout2.resId = preference.getLayoutResource();
        preferenceLayout2.widgetResId = preference.getWidgetLayoutResource();
        return preferenceLayout2;
    }
    
    private void flattenPreferenceGroup(final List<Preference> list, final PreferenceGroup preferenceGroup) {
        preferenceGroup.sortPreferences();
        for (int preferenceCount = preferenceGroup.getPreferenceCount(), i = 0; i < preferenceCount; ++i) {
            final Preference preference = preferenceGroup.getPreference(i);
            list.add(preference);
            this.addPreferenceClassName(preference);
            if (preference instanceof PreferenceGroup) {
                final PreferenceGroup preferenceGroup2 = (PreferenceGroup)preference;
                if (preferenceGroup2.isOnSameScreenAsChildren()) {
                    this.flattenPreferenceGroup(list, preferenceGroup2);
                }
            }
            preference.setOnPreferenceChangeInternalListener((Preference.OnPreferenceChangeInternalListener)this);
        }
    }
    
    private void syncMyPreferences() {
        final Iterator<Preference> iterator = this.mPreferenceListInternal.iterator();
        while (iterator.hasNext()) {
            iterator.next().setOnPreferenceChangeInternalListener(null);
        }
        final ArrayList mPreferenceListInternal = new ArrayList(this.mPreferenceListInternal.size());
        this.flattenPreferenceGroup(mPreferenceListInternal, this.mPreferenceGroup);
        final ArrayList mPreferenceList = new ArrayList(mPreferenceListInternal.size());
        for (final Preference preference : mPreferenceListInternal) {
            if (preference.isVisible()) {
                mPreferenceList.add((Object)preference);
            }
        }
        final List<Preference> mPreferenceList2 = this.mPreferenceList;
        this.mPreferenceList = (List<Preference>)mPreferenceList;
        this.mPreferenceListInternal = mPreferenceListInternal;
        final PreferenceManager preferenceManager = this.mPreferenceGroup.getPreferenceManager();
        if (preferenceManager != null && preferenceManager.getPreferenceComparisonCallback() != null) {
            DiffUtil.calculateDiff((DiffUtil.Callback)new DiffUtil.Callback() {
                final /* synthetic */ PreferenceManager.PreferenceComparisonCallback val$comparisonCallback = preferenceManager.getPreferenceComparisonCallback();
                
                @Override
                public boolean areContentsTheSame(final int n, final int n2) {
                    return this.val$comparisonCallback.arePreferenceContentsTheSame(mPreferenceList2.get(n), mPreferenceList.get(n2));
                }
                
                @Override
                public boolean areItemsTheSame(final int n, final int n2) {
                    return this.val$comparisonCallback.arePreferenceItemsTheSame(mPreferenceList2.get(n), mPreferenceList.get(n2));
                }
                
                @Override
                public int getNewListSize() {
                    return mPreferenceList.size();
                }
                
                @Override
                public int getOldListSize() {
                    return mPreferenceList2.size();
                }
            }).dispatchUpdatesTo(this);
        }
        else {
            ((RecyclerView.Adapter)this).notifyDataSetChanged();
        }
        final Iterator iterator3 = mPreferenceListInternal.iterator();
        while (iterator3.hasNext()) {
            ((Preference)iterator3.next()).clearWasDetached();
        }
    }
    
    public Preference getItem(final int n) {
        if (n >= 0 && n < this.getItemCount()) {
            return this.mPreferenceList.get(n);
        }
        return null;
    }
    
    @Override
    public int getItemCount() {
        return this.mPreferenceList.size();
    }
    
    @Override
    public long getItemId(final int n) {
        if (((RecyclerView.Adapter)this).hasStableIds()) {
            return this.getItem(n).getId();
        }
        return -1L;
    }
    
    @Override
    public int getItemViewType(int n) {
        this.mTempPreferenceLayout = this.createPreferenceLayout(this.getItem(n), this.mTempPreferenceLayout);
        n = this.mPreferenceLayouts.indexOf(this.mTempPreferenceLayout);
        if (n == -1) {
            n = this.mPreferenceLayouts.size();
            this.mPreferenceLayouts.add(new PreferenceLayout(this.mTempPreferenceLayout));
            return n;
        }
        return n;
    }
    
    @Override
    public int getPreferenceAdapterPosition(final Preference obj) {
        for (int size = this.mPreferenceList.size(), i = 0; i < size; ++i) {
            final Preference preference = this.mPreferenceList.get(i);
            if (preference != null && preference.equals(obj)) {
                return i;
            }
        }
        return -1;
    }
    
    @Override
    public int getPreferenceAdapterPosition(final String s) {
        for (int size = this.mPreferenceList.size(), i = 0; i < size; ++i) {
            if (TextUtils.equals((CharSequence)s, (CharSequence)this.mPreferenceList.get(i).getKey())) {
                return i;
            }
        }
        return -1;
    }
    
    public void onBindViewHolder(final PreferenceViewHolder preferenceViewHolder, final int n) {
        this.getItem(n).onBindViewHolder(preferenceViewHolder);
    }
    
    public PreferenceViewHolder onCreateViewHolder(final ViewGroup viewGroup, final int n) {
        final PreferenceLayout preferenceLayout = this.mPreferenceLayouts.get(n);
        final LayoutInflater from = LayoutInflater.from(viewGroup.getContext());
        final TypedArray obtainStyledAttributes = viewGroup.getContext().obtainStyledAttributes((AttributeSet)null, R.styleable.BackgroundStyle);
        Drawable drawable = obtainStyledAttributes.getDrawable(R.styleable.BackgroundStyle_android_selectableItemBackground);
        if (drawable == null) {
            drawable = ContextCompat.getDrawable(viewGroup.getContext(), 17301602);
        }
        obtainStyledAttributes.recycle();
        final View inflate = from.inflate(preferenceLayout.resId, viewGroup, false);
        if (inflate.getBackground() == null) {
            ViewCompat.setBackground(inflate, drawable);
        }
        final ViewGroup viewGroup2 = (ViewGroup)inflate.findViewById(16908312);
        if (viewGroup2 != null) {
            if (preferenceLayout.widgetResId == 0) {
                viewGroup2.setVisibility(8);
            }
            else {
                from.inflate(preferenceLayout.widgetResId, viewGroup2);
            }
        }
        return new PreferenceViewHolder(inflate);
    }
    
    @Override
    public void onPreferenceChange(final Preference preference) {
        final int index = this.mPreferenceList.indexOf(preference);
        if (index != -1) {
            ((RecyclerView.Adapter)this).notifyItemChanged(index, preference);
        }
    }
    
    @Override
    public void onPreferenceHierarchyChange(final Preference preference) {
        this.mHandler.removeCallbacks(this.mSyncRunnable);
        this.mHandler.post(this.mSyncRunnable);
    }
    
    @Override
    public void onPreferenceVisibilityChange(final Preference preference) {
        int n = 0;
        if (this.mPreferenceListInternal.contains(preference)) {
            if (!preference.isVisible()) {
                while (n < this.mPreferenceList.size() && !preference.equals(this.mPreferenceList.get(n))) {
                    ++n;
                }
                this.mPreferenceList.remove(n);
                ((RecyclerView.Adapter)this).notifyItemRemoved(n);
            }
            else {
                final Iterator<Preference> iterator = this.mPreferenceListInternal.iterator();
                int n2 = -1;
                while (iterator.hasNext()) {
                    final Preference obj = iterator.next();
                    if (preference.equals(obj)) {
                        break;
                    }
                    if (!obj.isVisible()) {
                        continue;
                    }
                    ++n2;
                }
                this.mPreferenceList.add(n2 + 1, preference);
                ((RecyclerView.Adapter)this).notifyItemInserted(n2 + 1);
            }
        }
    }
    
    private static class PreferenceLayout
    {
        private String name;
        private int resId;
        private int widgetResId;
        
        public PreferenceLayout() {
        }
        
        public PreferenceLayout(final PreferenceLayout preferenceLayout) {
            this.resId = preferenceLayout.resId;
            this.widgetResId = preferenceLayout.widgetResId;
            this.name = preferenceLayout.name;
        }
        
        @Override
        public boolean equals(final Object o) {
            final boolean b = false;
            if (o instanceof PreferenceLayout) {
                final PreferenceLayout preferenceLayout = (PreferenceLayout)o;
                boolean b2;
                if (this.resId != preferenceLayout.resId) {
                    b2 = b;
                }
                else {
                    b2 = b;
                    if (this.widgetResId == preferenceLayout.widgetResId) {
                        b2 = b;
                        if (TextUtils.equals((CharSequence)this.name, (CharSequence)preferenceLayout.name)) {
                            b2 = true;
                        }
                    }
                }
                return b2;
            }
            return false;
        }
        
        @Override
        public int hashCode() {
            return ((this.resId + 527) * 31 + this.widgetResId) * 31 + this.name.hashCode();
        }
    }
}
