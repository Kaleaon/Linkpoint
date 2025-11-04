// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v7.preference;

import android.support.annotation.NonNull;
import android.os.Parcel;
import android.os.Parcelable$Creator;
import android.text.TextUtils;
import android.support.annotation.ArrayRes;
import android.os.Parcelable;
import android.content.res.TypedArray;
import android.support.v4.content.res.TypedArrayUtils;
import android.util.AttributeSet;
import android.content.Context;

public class ListPreference extends DialogPreference
{
    private CharSequence[] mEntries;
    private CharSequence[] mEntryValues;
    private String mSummary;
    private String mValue;
    private boolean mValueSet;
    
    public ListPreference(final Context context) {
        this(context, null);
    }
    
    public ListPreference(final Context context, final AttributeSet set) {
        this(context, set, TypedArrayUtils.getAttr(context, R.attr.dialogPreferenceStyle, 16842897));
    }
    
    public ListPreference(final Context context, final AttributeSet set, final int n) {
        this(context, set, n, 0);
    }
    
    public ListPreference(final Context context, final AttributeSet set, final int n, final int n2) {
        super(context, set, n, n2);
        final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, R.styleable.ListPreference, n, n2);
        this.mEntries = TypedArrayUtils.getTextArray(obtainStyledAttributes, R.styleable.ListPreference_entries, R.styleable.ListPreference_android_entries);
        this.mEntryValues = TypedArrayUtils.getTextArray(obtainStyledAttributes, R.styleable.ListPreference_entryValues, R.styleable.ListPreference_android_entryValues);
        obtainStyledAttributes.recycle();
        final TypedArray obtainStyledAttributes2 = context.obtainStyledAttributes(set, R.styleable.Preference, n, n2);
        this.mSummary = TypedArrayUtils.getString(obtainStyledAttributes2, R.styleable.Preference_summary, R.styleable.Preference_android_summary);
        obtainStyledAttributes2.recycle();
    }
    
    private int getValueIndex() {
        return this.findIndexOfValue(this.mValue);
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
    
    public CharSequence[] getEntries() {
        return this.mEntries;
    }
    
    public CharSequence getEntry() {
        CharSequence charSequence = null;
        final int valueIndex = this.getValueIndex();
        if (valueIndex >= 0 && this.mEntries != null) {
            charSequence = this.mEntries[valueIndex];
        }
        return charSequence;
    }
    
    public CharSequence[] getEntryValues() {
        return this.mEntryValues;
    }
    
    @Override
    public CharSequence getSummary() {
        CharSequence entry = this.getEntry();
        if (this.mSummary != null) {
            final String mSummary = this.mSummary;
            if (entry == null) {
                entry = "";
            }
            return String.format(mSummary, entry);
        }
        return super.getSummary();
    }
    
    public String getValue() {
        return this.mValue;
    }
    
    @Override
    protected Object onGetDefaultValue(final TypedArray typedArray, final int n) {
        return typedArray.getString(n);
    }
    
    @Override
    protected void onRestoreInstanceState(final Parcelable parcelable) {
        if (parcelable != null && parcelable.getClass().equals(SavedState.class)) {
            final SavedState savedState = (SavedState)parcelable;
            super.onRestoreInstanceState(savedState.getSuperState());
            this.setValue(savedState.value);
            return;
        }
        super.onRestoreInstanceState(parcelable);
    }
    
    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable onSaveInstanceState = super.onSaveInstanceState();
        if (!this.isPersistent()) {
            final SavedState savedState = new SavedState(onSaveInstanceState);
            savedState.value = this.getValue();
            return (Parcelable)savedState;
        }
        return onSaveInstanceState;
    }
    
    @Override
    protected void onSetInitialValue(final boolean b, final Object o) {
        String persistedString;
        if (!b) {
            persistedString = (String)o;
        }
        else {
            persistedString = this.getPersistedString(this.mValue);
        }
        this.setValue(persistedString);
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
    public void setSummary(final CharSequence summary) {
        super.setSummary(summary);
        if (summary == null && this.mSummary != null) {
            this.mSummary = null;
        }
        else if (summary != null && !summary.equals(this.mSummary)) {
            this.mSummary = summary.toString();
        }
    }
    
    public void setValue(final String mValue) {
        boolean b = false;
        if (!TextUtils.equals((CharSequence)this.mValue, (CharSequence)mValue)) {
            b = true;
        }
        if (b || !this.mValueSet) {
            this.mValue = mValue;
            this.mValueSet = true;
            this.persistString(mValue);
            if (b) {
                this.notifyChanged();
            }
        }
    }
    
    public void setValueIndex(final int n) {
        if (this.mEntryValues != null) {
            this.setValue(this.mEntryValues[n].toString());
        }
    }
    
    private static class SavedState extends BaseSavedState
    {
        public static final Parcelable$Creator<SavedState> CREATOR;
        String value;
        
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
            this.value = parcel.readString();
        }
        
        public SavedState(final Parcelable parcelable) {
            super(parcelable);
        }
        
        public void writeToParcel(@NonNull final Parcel parcel, final int n) {
            super.writeToParcel(parcel, n);
            parcel.writeString(this.value);
        }
    }
}
