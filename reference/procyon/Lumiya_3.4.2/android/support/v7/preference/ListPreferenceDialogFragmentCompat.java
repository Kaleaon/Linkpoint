// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v7.preference;

import android.support.annotation.NonNull;
import android.content.DialogInterface;
import android.content.DialogInterface$OnClickListener;
import android.support.v7.app.AlertDialog;
import java.util.ArrayList;
import android.os.Bundle;

public class ListPreferenceDialogFragmentCompat extends PreferenceDialogFragmentCompat
{
    private static final String SAVE_STATE_ENTRIES = "ListPreferenceDialogFragment.entries";
    private static final String SAVE_STATE_ENTRY_VALUES = "ListPreferenceDialogFragment.entryValues";
    private static final String SAVE_STATE_INDEX = "ListPreferenceDialogFragment.index";
    private int mClickedDialogEntryIndex;
    private CharSequence[] mEntries;
    private CharSequence[] mEntryValues;
    
    private static CharSequence[] getCharSequenceArray(final Bundle bundle, final String s) {
        final CharSequence[] array = null;
        final ArrayList stringArrayList = bundle.getStringArrayList(s);
        CharSequence[] array2 = array;
        if (stringArrayList != null) {
            array2 = stringArrayList.toArray(new CharSequence[stringArrayList.size()]);
        }
        return array2;
    }
    
    private ListPreference getListPreference() {
        return (ListPreference)this.getPreference();
    }
    
    public static ListPreferenceDialogFragmentCompat newInstance(final String s) {
        final ListPreferenceDialogFragmentCompat listPreferenceDialogFragmentCompat = new ListPreferenceDialogFragmentCompat();
        final Bundle arguments = new Bundle(1);
        arguments.putString("key", s);
        listPreferenceDialogFragmentCompat.setArguments(arguments);
        return listPreferenceDialogFragmentCompat;
    }
    
    private static void putCharSequenceArray(final Bundle bundle, final String s, final CharSequence[] array) {
        final ArrayList list = new ArrayList(array.length);
        for (int length = array.length, i = 0; i < length; ++i) {
            list.add(array[i].toString());
        }
        bundle.putStringArrayList(s, list);
    }
    
    @Override
    public void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        if (bundle != null) {
            this.mClickedDialogEntryIndex = bundle.getInt("ListPreferenceDialogFragment.index", 0);
            this.mEntries = getCharSequenceArray(bundle, "ListPreferenceDialogFragment.entries");
            this.mEntryValues = getCharSequenceArray(bundle, "ListPreferenceDialogFragment.entryValues");
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
    protected void onPrepareDialogBuilder(final AlertDialog.Builder builder) {
        super.onPrepareDialogBuilder(builder);
        builder.setSingleChoiceItems(this.mEntries, this.mClickedDialogEntryIndex, (DialogInterface$OnClickListener)new DialogInterface$OnClickListener() {
            public void onClick(final DialogInterface dialogInterface, final int n) {
                ListPreferenceDialogFragmentCompat.this.mClickedDialogEntryIndex = n;
                ListPreferenceDialogFragmentCompat.this.onClick(dialogInterface, -1);
                dialogInterface.dismiss();
            }
        });
        builder.setPositiveButton(null, null);
    }
    
    @Override
    public void onSaveInstanceState(@NonNull final Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt("ListPreferenceDialogFragment.index", this.mClickedDialogEntryIndex);
        putCharSequenceArray(bundle, "ListPreferenceDialogFragment.entries", this.mEntries);
        putCharSequenceArray(bundle, "ListPreferenceDialogFragment.entryValues", this.mEntryValues);
    }
}
