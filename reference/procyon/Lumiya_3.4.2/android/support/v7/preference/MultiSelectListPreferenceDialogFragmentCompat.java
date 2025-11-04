// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v7.preference;

import java.util.ArrayList;
import android.support.annotation.NonNull;
import android.content.DialogInterface;
import android.content.DialogInterface$OnMultiChoiceClickListener;
import android.support.v7.app.AlertDialog;
import java.util.Collection;
import android.os.Bundle;
import android.support.v7.preference.internal.AbstractMultiSelectListPreference;
import java.util.HashSet;
import java.util.Set;

public class MultiSelectListPreferenceDialogFragmentCompat extends PreferenceDialogFragmentCompat
{
    private static final String SAVE_STATE_CHANGED = "MultiSelectListPreferenceDialogFragmentCompat.changed";
    private static final String SAVE_STATE_ENTRIES = "MultiSelectListPreferenceDialogFragmentCompat.entries";
    private static final String SAVE_STATE_ENTRY_VALUES = "MultiSelectListPreferenceDialogFragmentCompat.entryValues";
    private static final String SAVE_STATE_VALUES = "MultiSelectListPreferenceDialogFragmentCompat.values";
    private CharSequence[] mEntries;
    private CharSequence[] mEntryValues;
    private Set<String> mNewValues;
    private boolean mPreferenceChanged;
    
    public MultiSelectListPreferenceDialogFragmentCompat() {
        this.mNewValues = new HashSet<String>();
    }
    
    private AbstractMultiSelectListPreference getListPreference() {
        return (AbstractMultiSelectListPreference)this.getPreference();
    }
    
    public static MultiSelectListPreferenceDialogFragmentCompat newInstance(final String s) {
        final MultiSelectListPreferenceDialogFragmentCompat multiSelectListPreferenceDialogFragmentCompat = new MultiSelectListPreferenceDialogFragmentCompat();
        final Bundle arguments = new Bundle(1);
        arguments.putString("key", s);
        multiSelectListPreferenceDialogFragmentCompat.setArguments(arguments);
        return multiSelectListPreferenceDialogFragmentCompat;
    }
    
    @Override
    public void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        if (bundle != null) {
            this.mNewValues.clear();
            this.mNewValues.addAll(bundle.getStringArrayList("MultiSelectListPreferenceDialogFragmentCompat.values"));
            this.mPreferenceChanged = bundle.getBoolean("MultiSelectListPreferenceDialogFragmentCompat.changed", false);
            this.mEntries = bundle.getCharSequenceArray("MultiSelectListPreferenceDialogFragmentCompat.entries");
            this.mEntryValues = bundle.getCharSequenceArray("MultiSelectListPreferenceDialogFragmentCompat.entryValues");
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
    protected void onPrepareDialogBuilder(final AlertDialog.Builder builder) {
        super.onPrepareDialogBuilder(builder);
        final int length = this.mEntryValues.length;
        final boolean[] array = new boolean[length];
        for (int i = 0; i < length; ++i) {
            array[i] = this.mNewValues.contains(this.mEntryValues[i].toString());
        }
        builder.setMultiChoiceItems(this.mEntries, array, (DialogInterface$OnMultiChoiceClickListener)new DialogInterface$OnMultiChoiceClickListener() {
            public void onClick(final DialogInterface dialogInterface, final int n, final boolean b) {
                if (!b) {
                    MultiSelectListPreferenceDialogFragmentCompat.this.mPreferenceChanged |= MultiSelectListPreferenceDialogFragmentCompat.this.mNewValues.remove(MultiSelectListPreferenceDialogFragmentCompat.this.mEntryValues[n].toString());
                }
                else {
                    MultiSelectListPreferenceDialogFragmentCompat.this.mPreferenceChanged |= MultiSelectListPreferenceDialogFragmentCompat.this.mNewValues.add(MultiSelectListPreferenceDialogFragmentCompat.this.mEntryValues[n].toString());
                }
            }
        });
    }
    
    @Override
    public void onSaveInstanceState(@NonNull final Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putStringArrayList("MultiSelectListPreferenceDialogFragmentCompat.values", new ArrayList((Collection<? extends E>)this.mNewValues));
        bundle.putBoolean("MultiSelectListPreferenceDialogFragmentCompat.changed", this.mPreferenceChanged);
        bundle.putCharSequenceArray("MultiSelectListPreferenceDialogFragmentCompat.entries", this.mEntries);
        bundle.putCharSequenceArray("MultiSelectListPreferenceDialogFragmentCompat.entryValues", this.mEntryValues);
    }
}
