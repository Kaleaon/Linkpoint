// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v14.preference;

import android.support.annotation.NonNull;
import android.content.DialogInterface;
import android.content.DialogInterface$OnClickListener;
import android.app.AlertDialog$Builder;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;

public class ListPreferenceDialogFragment extends PreferenceDialogFragment
{
    private static final String SAVE_STATE_ENTRIES = "ListPreferenceDialogFragment.entries";
    private static final String SAVE_STATE_ENTRY_VALUES = "ListPreferenceDialogFragment.entryValues";
    private static final String SAVE_STATE_INDEX = "ListPreferenceDialogFragment.index";
    private int mClickedDialogEntryIndex;
    private CharSequence[] mEntries;
    private CharSequence[] mEntryValues;
    
    private ListPreference getListPreference() {
        return (ListPreference)this.getPreference();
    }
    
    public static ListPreferenceDialogFragment newInstance(final String s) {
        final ListPreferenceDialogFragment listPreferenceDialogFragment = new ListPreferenceDialogFragment();
        final Bundle arguments = new Bundle(1);
        arguments.putString("key", s);
        listPreferenceDialogFragment.setArguments(arguments);
        return listPreferenceDialogFragment;
    }
    
    @Override
    public void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        if (bundle != null) {
            this.mClickedDialogEntryIndex = bundle.getInt("ListPreferenceDialogFragment.index", 0);
            this.mEntries = bundle.getCharSequenceArray("ListPreferenceDialogFragment.entries");
            this.mEntryValues = bundle.getCharSequenceArray("ListPreferenceDialogFragment.entryValues");
        }
        else {
            final ListPreference listPreference = this.getListPreference();
            if (listPreference.getEntries() == null || listPreference.getEntryValues() == null) {
                throw new IllegalStateException("ListPreference requires an entries array and an entryValues array.");
            }
            this.mClickedDialogEntryIndex = listPreference.findIndexOfValue(listPreference.getValue());
            this.mEntries = listPreference.getEntries();
            this.mEntryValues = listPreference.getEntryValues();
        }
    }
    
    @Override
    public void onDialogClosed(final boolean b) {
        final ListPreference listPreference = this.getListPreference();
        if (b && this.mClickedDialogEntryIndex >= 0) {
            final String string = this.mEntryValues[this.mClickedDialogEntryIndex].toString();
            if (listPreference.callChangeListener(string)) {
                listPreference.setValue(string);
            }
        }
    }
    
    @Override
    protected void onPrepareDialogBuilder(final AlertDialog$Builder alertDialog$Builder) {
        super.onPrepareDialogBuilder(alertDialog$Builder);
        alertDialog$Builder.setSingleChoiceItems(this.mEntries, this.mClickedDialogEntryIndex, (DialogInterface$OnClickListener)new DialogInterface$OnClickListener() {
            public void onClick(final DialogInterface dialogInterface, final int n) {
                ListPreferenceDialogFragment.this.mClickedDialogEntryIndex = n;
                ListPreferenceDialogFragment.this.onClick(dialogInterface, -1);
                dialogInterface.dismiss();
            }
        });
        alertDialog$Builder.setPositiveButton((CharSequence)null, (DialogInterface$OnClickListener)null);
    }
    
    @Override
    public void onSaveInstanceState(@NonNull final Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt("ListPreferenceDialogFragment.index", this.mClickedDialogEntryIndex);
        bundle.putCharSequenceArray("ListPreferenceDialogFragment.entries", this.mEntries);
        bundle.putCharSequenceArray("ListPreferenceDialogFragment.entryValues", this.mEntryValues);
    }
}
