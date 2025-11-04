// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v14.preference;

import java.util.ArrayList;
import android.support.annotation.NonNull;
import android.content.DialogInterface;
import android.content.DialogInterface$OnMultiChoiceClickListener;
import android.app.AlertDialog$Builder;
import java.util.Collection;
import android.os.Bundle;
import android.support.v7.preference.internal.AbstractMultiSelectListPreference;
import java.util.HashSet;
import java.util.Set;

public class MultiSelectListPreferenceDialogFragment extends PreferenceDialogFragment
{
    private static final String SAVE_STATE_CHANGED = "MultiSelectListPreferenceDialogFragment.changed";
    private static final String SAVE_STATE_ENTRIES = "MultiSelectListPreferenceDialogFragment.entries";
    private static final String SAVE_STATE_ENTRY_VALUES = "MultiSelectListPreferenceDialogFragment.entryValues";
    private static final String SAVE_STATE_VALUES = "MultiSelectListPreferenceDialogFragment.values";
    private CharSequence[] mEntries;
    private CharSequence[] mEntryValues;
    private Set<String> mNewValues;
    private boolean mPreferenceChanged;
    
    public MultiSelectListPreferenceDialogFragment() {
        this.mNewValues = new HashSet<String>();
    }
    
    private AbstractMultiSelectListPreference getListPreference() {
        return (AbstractMultiSelectListPreference)this.getPreference();
    }
    
    public static MultiSelectListPreferenceDialogFragment newInstance(final String s) {
        final MultiSelectListPreferenceDialogFragment multiSelectListPreferenceDialogFragment = new MultiSelectListPreferenceDialogFragment();
        final Bundle arguments = new Bundle(1);
        arguments.putString("key", s);
        multiSelectListPreferenceDialogFragment.setArguments(arguments);
        return multiSelectListPreferenceDialogFragment;
    }
    
    @Override
    public void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        if (bundle != null) {
            this.mNewValues.clear();
            this.mNewValues.addAll(bundle.getStringArrayList("MultiSelectListPreferenceDialogFragment.values"));
            this.mPreferenceChanged = bundle.getBoolean("MultiSelectListPreferenceDialogFragment.changed", false);
            this.mEntries = bundle.getCharSequenceArray("MultiSelectListPreferenceDialogFragment.entries");
            this.mEntryValues = bundle.getCharSequenceArray("MultiSelectListPreferenceDialogFragment.entryValues");
        }
        else {
            final AbstractMultiSelectListPreference listPreference = this.getListPreference();
            if (listPreference.getEntries() == null || listPreference.getEntryValues() == null) {
                throw new IllegalStateException("MultiSelectListPreference requires an entries array and an entryValues array.");
            }
            this.mNewValues.clear();
            this.mNewValues.addAll(listPreference.getValues());
            this.mPreferenceChanged = false;
            this.mEntries = listPreference.getEntries();
            this.mEntryValues = listPreference.getEntryValues();
        }
    }
    
    @Override
    public void onDialogClosed(final boolean b) {
        final AbstractMultiSelectListPreference listPreference = this.getListPreference();
        if (b && this.mPreferenceChanged) {
            final Set<String> mNewValues = this.mNewValues;
            if (listPreference.callChangeListener(mNewValues)) {
                listPreference.setValues(mNewValues);
            }
        }
        this.mPreferenceChanged = false;
    }
    
    @Override
    protected void onPrepareDialogBuilder(final AlertDialog$Builder alertDialog$Builder) {
        super.onPrepareDialogBuilder(alertDialog$Builder);
        final int length = this.mEntryValues.length;
        final boolean[] array = new boolean[length];
        for (int i = 0; i < length; ++i) {
            array[i] = this.mNewValues.contains(this.mEntryValues[i].toString());
        }
        alertDialog$Builder.setMultiChoiceItems(this.mEntries, array, (DialogInterface$OnMultiChoiceClickListener)new DialogInterface$OnMultiChoiceClickListener() {
            public void onClick(final DialogInterface dialogInterface, final int n, final boolean b) {
                if (!b) {
                    MultiSelectListPreferenceDialogFragment.this.mPreferenceChanged |= MultiSelectListPreferenceDialogFragment.this.mNewValues.remove(MultiSelectListPreferenceDialogFragment.this.mEntryValues[n].toString());
                }
                else {
                    MultiSelectListPreferenceDialogFragment.this.mPreferenceChanged |= MultiSelectListPreferenceDialogFragment.this.mNewValues.add(MultiSelectListPreferenceDialogFragment.this.mEntryValues[n].toString());
                }
            }
        });
    }
    
    @Override
    public void onSaveInstanceState(@NonNull final Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putStringArrayList("MultiSelectListPreferenceDialogFragment.values", new ArrayList((Collection<? extends E>)this.mNewValues));
        bundle.putBoolean("MultiSelectListPreferenceDialogFragment.changed", this.mPreferenceChanged);
        bundle.putCharSequenceArray("MultiSelectListPreferenceDialogFragment.entries", this.mEntries);
        bundle.putCharSequenceArray("MultiSelectListPreferenceDialogFragment.entryValues", this.mEntryValues);
    }
}
