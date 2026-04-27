package android.support.v7.preference;

import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.RestrictTo;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceGroup;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
public class PreferenceGroupAdapter extends RecyclerView.Adapter<PreferenceViewHolder> implements Preference.OnPreferenceChangeInternalListener, PreferenceGroup.PreferencePositionCallback {
    private static final String TAG = "PreferenceGroupAdapter";
    private Handler mHandler = new Handler();
    private PreferenceGroup mPreferenceGroup;
    private List<PreferenceLayout> mPreferenceLayouts;
    private List<Preference> mPreferenceList;
    private List<Preference> mPreferenceListInternal;
    private Runnable mSyncRunnable = new Runnable() {
        public void run() {
            PreferenceGroupAdapter.this.syncMyPreferences();
        }
    };
    private PreferenceLayout mTempPreferenceLayout = new PreferenceLayout();

    private static class PreferenceLayout {
        /* access modifiers changed from: private */
        public String name;
        /* access modifiers changed from: private */
        public int resId;
        /* access modifiers changed from: private */
        public int widgetResId;

        public PreferenceLayout() {
        }

        public PreferenceLayout(PreferenceLayout preferenceLayout) {
            this.resId = preferenceLayout.resId;
            this.widgetResId = preferenceLayout.widgetResId;
            this.name = preferenceLayout.name;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof PreferenceLayout)) {
                return false;
            }
            PreferenceLayout preferenceLayout = (PreferenceLayout) obj;
            return this.resId == preferenceLayout.resId && this.widgetResId == preferenceLayout.widgetResId && TextUtils.equals(this.name, preferenceLayout.name);
        }

        public int hashCode() {
            return ((((this.resId + 527) * 31) + this.widgetResId) * 31) + this.name.hashCode();
        }
    }

    public PreferenceGroupAdapter(PreferenceGroup preferenceGroup) {
        this.mPreferenceGroup = preferenceGroup;
        this.mPreferenceGroup.setOnPreferenceChangeInternalListener(this);
        this.mPreferenceList = new ArrayList();
        this.mPreferenceListInternal = new ArrayList();
        this.mPreferenceLayouts = new ArrayList();
        if (!(this.mPreferenceGroup instanceof PreferenceScreen)) {
            setHasStableIds(true);
        } else {
            setHasStableIds(((PreferenceScreen) this.mPreferenceGroup).shouldUseGeneratedIds());
        }
        syncMyPreferences();
    }

    private void addPreferenceClassName(Preference preference) {
        PreferenceLayout createPreferenceLayout = createPreferenceLayout(preference, (PreferenceLayout) null);
        if (!this.mPreferenceLayouts.contains(createPreferenceLayout)) {
            this.mPreferenceLayouts.add(createPreferenceLayout);
        }
    }

    private PreferenceLayout createPreferenceLayout(Preference preference, PreferenceLayout preferenceLayout) {
        if (preferenceLayout == null) {
            preferenceLayout = new PreferenceLayout();
        }
        String unused = preferenceLayout.name = preference.getClass().getName();
        int unused2 = preferenceLayout.resId = preference.getLayoutResource();
        int unused3 = preferenceLayout.widgetResId = preference.getWidgetLayoutResource();
        return preferenceLayout;
    }

    private void flattenPreferenceGroup(List<Preference> list, PreferenceGroup preferenceGroup) {
        preferenceGroup.sortPreferences();
        int preferenceCount = preferenceGroup.getPreferenceCount();
        for (int i = 0; i < preferenceCount; i++) {
            Preference preference = preferenceGroup.getPreference(i);
            list.add(preference);
            addPreferenceClassName(preference);
            if (preference instanceof PreferenceGroup) {
                PreferenceGroup preferenceGroup2 = (PreferenceGroup) preference;
                if (preferenceGroup2.isOnSameScreenAsChildren()) {
                    flattenPreferenceGroup(list, preferenceGroup2);
                }
            }
            preference.setOnPreferenceChangeInternalListener(this);
        }
    }

    /* access modifiers changed from: private */
    public void syncMyPreferences() {
        for (Preference onPreferenceChangeInternalListener : this.mPreferenceListInternal) {
            onPreferenceChangeInternalListener.setOnPreferenceChangeInternalListener((Preference.OnPreferenceChangeInternalListener) null);
        }
        ArrayList<Preference> arrayList = new ArrayList<>(this.mPreferenceListInternal.size());
        flattenPreferenceGroup(arrayList, this.mPreferenceGroup);
        final ArrayList arrayList2 = new ArrayList(arrayList.size());
        for (Preference preference : arrayList) {
            if (preference.isVisible()) {
                arrayList2.add(preference);
            }
        }
        final List<Preference> list = this.mPreferenceList;
        this.mPreferenceList = arrayList2;
        this.mPreferenceListInternal = arrayList;
        PreferenceManager preferenceManager = this.mPreferenceGroup.getPreferenceManager();
        if (preferenceManager == null || preferenceManager.getPreferenceComparisonCallback() == null) {
            notifyDataSetChanged();
        } else {
            final PreferenceManager.PreferenceComparisonCallback preferenceComparisonCallback = preferenceManager.getPreferenceComparisonCallback();
            DiffUtil.calculateDiff(new DiffUtil.Callback() {
                public boolean areContentsTheSame(int i, int i2) {
                    return preferenceComparisonCallback.arePreferenceContentsTheSame((Preference) list.get(i), (Preference) arrayList2.get(i2));
                }

                public boolean areItemsTheSame(int i, int i2) {
                    return preferenceComparisonCallback.arePreferenceItemsTheSame((Preference) list.get(i), (Preference) arrayList2.get(i2));
                }

                public int getNewListSize() {
                    return arrayList2.size();
                }

                public int getOldListSize() {
                    return list.size();
                }
            }).dispatchUpdatesTo((RecyclerView.Adapter) this);
        }
        for (Preference clearWasDetached : arrayList) {
            clearWasDetached.clearWasDetached();
        }
    }

    public Preference getItem(int i) {
        if (i >= 0 && i < getItemCount()) {
            return this.mPreferenceList.get(i);
        }
        return null;
    }

    public int getItemCount() {
        return this.mPreferenceList.size();
    }

    public long getItemId(int i) {
        if (hasStableIds()) {
            return getItem(i).getId();
        }
        return -1;
    }

    public int getItemViewType(int i) {
        this.mTempPreferenceLayout = createPreferenceLayout(getItem(i), this.mTempPreferenceLayout);
        int indexOf = this.mPreferenceLayouts.indexOf(this.mTempPreferenceLayout);
        if (indexOf != -1) {
            return indexOf;
        }
        int size = this.mPreferenceLayouts.size();
        this.mPreferenceLayouts.add(new PreferenceLayout(this.mTempPreferenceLayout));
        return size;
    }

    public int getPreferenceAdapterPosition(Preference preference) {
        int size = this.mPreferenceList.size();
        for (int i = 0; i < size; i++) {
            Preference preference2 = this.mPreferenceList.get(i);
            if (preference2 != null && preference2.equals(preference)) {
                return i;
            }
        }
        return -1;
    }

    public int getPreferenceAdapterPosition(String str) {
        int size = this.mPreferenceList.size();
        for (int i = 0; i < size; i++) {
            if (TextUtils.equals(str, this.mPreferenceList.get(i).getKey())) {
                return i;
            }
        }
        return -1;
    }

    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder, int i) {
        getItem(i).onBindViewHolder(preferenceViewHolder);
    }

    public PreferenceViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        PreferenceLayout preferenceLayout = this.mPreferenceLayouts.get(i);
        LayoutInflater from = LayoutInflater.from(viewGroup.getContext());
        TypedArray obtainStyledAttributes = viewGroup.getContext().obtainStyledAttributes((AttributeSet) null, R.styleable.BackgroundStyle);
        Drawable drawable = obtainStyledAttributes.getDrawable(R.styleable.BackgroundStyle_android_selectableItemBackground);
        if (drawable == null) {
            drawable = ContextCompat.getDrawable(viewGroup.getContext(), 17301602);
        }
        obtainStyledAttributes.recycle();
        View inflate = from.inflate(preferenceLayout.resId, viewGroup, false);
        if (inflate.getBackground() == null) {
            ViewCompat.setBackground(inflate, drawable);
        }
        ViewGroup viewGroup2 = (ViewGroup) inflate.findViewById(16908312);
        if (viewGroup2 != null) {
            if (preferenceLayout.widgetResId == 0) {
                viewGroup2.setVisibility(8);
            } else {
                from.inflate(preferenceLayout.widgetResId, viewGroup2);
            }
        }
        return new PreferenceViewHolder(inflate);
    }

    public void onPreferenceChange(Preference preference) {
        int indexOf = this.mPreferenceList.indexOf(preference);
        if (indexOf != -1) {
            notifyItemChanged(indexOf, preference);
        }
    }

    public void onPreferenceHierarchyChange(Preference preference) {
        this.mHandler.removeCallbacks(this.mSyncRunnable);
        this.mHandler.post(this.mSyncRunnable);
    }

    public void onPreferenceVisibilityChange(Preference preference) {
        int i;
        int i2 = 0;
        if (!this.mPreferenceListInternal.contains(preference)) {
            return;
        }
        if (!preference.isVisible()) {
            int size = this.mPreferenceList.size();
            while (i2 < size && !preference.equals(this.mPreferenceList.get(i2))) {
                i2++;
            }
            this.mPreferenceList.remove(i2);
            notifyItemRemoved(i2);
            return;
        }
        int i3 = -1;
        Iterator<Preference> it = this.mPreferenceListInternal.iterator();
        while (true) {
            i = i3;
            if (it.hasNext()) {
                Preference next = it.next();
                if (preference.equals(next)) {
                    break;
                }
                i3 = !next.isVisible() ? i : i + 1;
            } else {
                break;
            }
        }
        this.mPreferenceList.add(i + 1, preference);
        notifyItemInserted(i + 1);
    }
}
