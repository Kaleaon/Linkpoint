// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v7.preference;

import android.support.annotation.NonNull;
import android.widget.SpinnerAdapter;
import android.support.annotation.RestrictTo;
import android.view.View;
import android.widget.AdapterView;
import android.util.AttributeSet;
import android.widget.Spinner;
import android.widget.AdapterView$OnItemSelectedListener;
import android.content.Context;
import android.widget.ArrayAdapter;

public class DropDownPreference extends ListPreference
{
    private final ArrayAdapter mAdapter;
    private final Context mContext;
    private final AdapterView$OnItemSelectedListener mItemSelectedListener;
    private Spinner mSpinner;
    
    public DropDownPreference(final Context context) {
        this(context, null);
    }
    
    public DropDownPreference(final Context context, final AttributeSet set) {
        this(context, set, R.attr.dropdownPreferenceStyle);
    }
    
    public DropDownPreference(final Context context, final AttributeSet set, final int n) {
        this(context, set, n, 0);
    }
    
    public DropDownPreference(final Context mContext, final AttributeSet set, final int n, final int n2) {
        super(mContext, set, n, n2);
        this.mItemSelectedListener = (AdapterView$OnItemSelectedListener)new AdapterView$OnItemSelectedListener() {
            public void onItemSelected(final AdapterView<?> adapterView, final View view, final int n, final long n2) {
                if (n >= 0) {
                    final String string = DropDownPreference.this.getEntryValues()[n].toString();
                    if (!string.equals(DropDownPreference.this.getValue()) && DropDownPreference.this.callChangeListener(string)) {
                        DropDownPreference.this.setValue(string);
                    }
                }
            }
            
            public void onNothingSelected(final AdapterView<?> adapterView) {
            }
        };
        this.mContext = mContext;
        this.mAdapter = this.createAdapter();
        this.updateEntries();
    }
    
    private void updateEntries() {
        this.mAdapter.clear();
        if (this.getEntries() != null) {
            final CharSequence[] entries = this.getEntries();
            for (int length = entries.length, i = 0; i < length; ++i) {
                this.mAdapter.add((Object)entries[i].toString());
            }
        }
    }
    
    protected ArrayAdapter createAdapter() {
        return new ArrayAdapter(this.mContext, 17367049);
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    public int findSpinnerIndexOfValue(final String obj) {
        final CharSequence[] entryValues = this.getEntryValues();
        if (obj != null && entryValues != null) {
            int length = entryValues.length;
            int n;
            do {
                n = length - 1;
                if (n < 0) {
                    return -1;
                }
                length = n;
            } while (!entryValues[n].equals(obj));
            return n;
        }
        return -1;
    }
    
    @Override
    protected void notifyChanged() {
        super.notifyChanged();
        this.mAdapter.notifyDataSetChanged();
    }
    
    @Override
    public void onBindViewHolder(final PreferenceViewHolder preferenceViewHolder) {
        (this.mSpinner = (Spinner)preferenceViewHolder.itemView.findViewById(R.id.spinner)).setAdapter((SpinnerAdapter)this.mAdapter);
        this.mSpinner.setOnItemSelectedListener(this.mItemSelectedListener);
        this.mSpinner.setSelection(this.findSpinnerIndexOfValue(this.getValue()));
        super.onBindViewHolder(preferenceViewHolder);
    }
    
    @Override
    protected void onClick() {
        this.mSpinner.performClick();
    }
    
    @Override
    public void setEntries(@NonNull final CharSequence[] entries) {
        super.setEntries(entries);
        this.updateEntries();
    }
    
    @Override
    public void setValueIndex(final int n) {
        this.setValue(this.getEntryValues()[n].toString());
    }
}
