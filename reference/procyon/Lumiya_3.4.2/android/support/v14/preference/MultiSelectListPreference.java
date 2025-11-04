// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v14.preference;

import android.support.annotation.NonNull;
import java.util.Collections;
import android.os.Parcel;
import android.os.Parcelable$Creator;
import android.support.v7.preference.Preference;
import java.util.Collection;
import android.support.annotation.ArrayRes;
import android.os.Parcelable;
import android.content.res.TypedArray;
import java.util.HashSet;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v7.preference.R;
import android.util.AttributeSet;
import android.content.Context;
import java.util.Set;
import android.support.v7.preference.internal.AbstractMultiSelectListPreference;

public class MultiSelectListPreference extends AbstractMultiSelectListPreference
{
    private CharSequence[] mEntries;
    private CharSequence[] mEntryValues;
    private Set<String> mValues;
    
    public MultiSelectListPreference(final Context context) {
        this(context, null);
    }
    
    public MultiSelectListPreference(final Context context, final AttributeSet set) {
        this(context, set, TypedArrayUtils.getAttr(context, R.attr.dialogPreferenceStyle, 16842897));
    }
    
    public MultiSelectListPreference(final Context context, final AttributeSet set, final int n) {
        this(context, set, n, 0);
    }
    
    public MultiSelectListPreference(final Context context, final AttributeSet set, final int n, final int n2) {
        super(context, set, n, n2);
        this.mValues = new HashSet<String>();
        final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, R.styleable.MultiSelectListPreference, n, n2);
        this.mEntries = TypedArrayUtils.getTextArray(obtainStyledAttributes, R.styleable.MultiSelectListPreference_entries, R.styleable.MultiSelectListPreference_android_entries);
        this.mEntryValues = TypedArrayUtils.getTextArray(obtainStyledAttributes, R.styleable.MultiSelectListPreference_entryValues, R.styleable.MultiSelectListPreference_android_entryValues);
        obtainStyledAttributes.recycle();
    }
    
    public int findIndexOfValue(final String obj) {
        if (obj != null && this.mEntryValues != null) {
            int length = this.mEntryValues.length;
            int n;
            do {
                n = length - 1;
                if (n < 0) {
                    return -1;
                }
                length = n;
            } while (!this.mEntryValues[n].equals(obj));
            return n;
        }
        return -1;
    }
    
    @Override
    public CharSequence[] getEntries() {
        return this.mEntries;
    }
    
    @Override
    public CharSequence[] getEntryValues() {
        return this.mEntryValues;
    }
    
    protected boolean[] getSelectedItems() {
        final CharSequence[] mEntryValues = this.mEntryValues;
        final int length = mEntryValues.length;
        final Set<String> mValues = this.mValues;
        final boolean[] array = new boolean[length];
        for (int i = 0; i < length; ++i) {
            array[i] = mValues.contains(mEntryValues[i].toString());
        }
        return array;
    }
    
    @Override
    public Set<String> getValues() {
        return this.mValues;
    }
    
    @Override
    protected Object onGetDefaultValue(final TypedArray typedArray, int i) {
        final CharSequence[] textArray = typedArray.getTextArray(i);
        final HashSet set = new HashSet();
        int length;
        for (length = textArray.length, i = 0; i < length; ++i) {
            set.add(textArray[i].toString());
        }
        return set;
    }
    
    @Override
    protected void onRestoreInstanceState(final Parcelable parcelable) {
        if (parcelable != null && parcelable.getClass().equals(SavedState.class)) {
            final SavedState savedState = (SavedState)parcelable;
            super.onRestoreInstanceState(savedState.getSuperState());
            this.setValues(savedState.values);
            return;
        }
        super.onRestoreInstanceState(parcelable);
    }
    
    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable onSaveInstanceState = super.onSaveInstanceState();
        if (!this.isPersistent()) {
            final SavedState savedState = new SavedState(onSaveInstanceState);
            savedState.values = this.getValues();
            return (Parcelable)savedState;
        }
        return onSaveInstanceState;
    }
    
    @Override
    protected void onSetInitialValue(final boolean b, final Object o) {
        Set<String> persistedStringSet;
        if (!b) {
            persistedStringSet = (Set)o;
        }
        else {
            persistedStringSet = this.getPersistedStringSet(this.mValues);
        }
        this.setValues(persistedStringSet);
    }
    
    public void setEntries(@ArrayRes final int n) {
        this.setEntries(this.getContext().getResources().getTextArray(n));
    }
    
    public void setEntries(final CharSequence[] mEntries) {
        this.mEntries = mEntries;
    }
    
    public void setEntryValues(@ArrayRes final int n) {
        this.setEntryValues(this.getContext().getResources().getTextArray(n));
    }
    
    public void setEntryValues(final CharSequence[] mEntryValues) {
        this.mEntryValues = mEntryValues;
    }
    
    @Override
    public void setValues(final Set<String> set) {
        this.mValues.clear();
        this.mValues.addAll(set);
        this.persistStringSet(set);
    }
    
    private static class SavedState extends BaseSavedState
    {
        public static final Parcelable$Creator<SavedState> CREATOR;
        Set<String> values;
        
        static {
            CREATOR = (Parcelable$Creator)new Parcelable$Creator<SavedState>() {
                public SavedState createFromParcel(final Parcel parcel) {
                    return new SavedState(parcel);
                }
                
                public SavedState[] newArray(final int n) {
                    return new SavedState[n];
                }
            };
        }
        
        public SavedState(final Parcel parcel) {
            super(parcel);
            final int int1 = parcel.readInt();
            this.values = new HashSet<String>();
            final String[] elements = new String[int1];
            parcel.readStringArray(elements);
            Collections.addAll(this.values, elements);
        }
        
        public SavedState(final Parcelable parcelable) {
            super(parcelable);
        }
        
        public void writeToParcel(@NonNull final Parcel parcel, final int n) {
            super.writeToParcel(parcel, n);
            parcel.writeInt(this.values.size());
            parcel.writeStringArray((String[])this.values.toArray(new String[this.values.size()]));
        }
    }
}
