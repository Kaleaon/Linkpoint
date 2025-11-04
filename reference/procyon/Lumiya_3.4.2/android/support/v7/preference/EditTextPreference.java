// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v7.preference;

import android.os.Parcel;
import android.os.Parcelable$Creator;
import android.text.TextUtils;
import android.os.Parcelable;
import android.content.res.TypedArray;
import android.support.v4.content.res.TypedArrayUtils;
import android.util.AttributeSet;
import android.content.Context;

public class EditTextPreference extends DialogPreference
{
    private String mText;
    
    public EditTextPreference(final Context context) {
        this(context, null);
    }
    
    public EditTextPreference(final Context context, final AttributeSet set) {
        this(context, set, TypedArrayUtils.getAttr(context, R.attr.editTextPreferenceStyle, 16842898));
    }
    
    public EditTextPreference(final Context context, final AttributeSet set, final int n) {
        this(context, set, n, 0);
    }
    
    public EditTextPreference(final Context context, final AttributeSet set, final int n, final int n2) {
        super(context, set, n, n2);
    }
    
    public String getText() {
        return this.mText;
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
            this.setText(savedState.text);
            return;
        }
        super.onRestoreInstanceState(parcelable);
    }
    
    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable onSaveInstanceState = super.onSaveInstanceState();
        if (!this.isPersistent()) {
            final SavedState savedState = new SavedState(onSaveInstanceState);
            savedState.text = this.getText();
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
            persistedString = this.getPersistedString(this.mText);
        }
        this.setText(persistedString);
    }
    
    public void setText(final String mText) {
        final boolean shouldDisableDependents = this.shouldDisableDependents();
        this.persistString(this.mText = mText);
        final boolean shouldDisableDependents2 = this.shouldDisableDependents();
        if (shouldDisableDependents2 != shouldDisableDependents) {
            this.notifyDependencyChange(shouldDisableDependents2);
        }
    }
    
    @Override
    public boolean shouldDisableDependents() {
        boolean b = false;
        if (TextUtils.isEmpty((CharSequence)this.mText) || super.shouldDisableDependents()) {
            b = true;
        }
        return b;
    }
    
    private static class SavedState extends BaseSavedState
    {
        public static final Parcelable$Creator<SavedState> CREATOR;
        String text;
        
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
            this.text = parcel.readString();
        }
        
        public SavedState(final Parcelable parcelable) {
            super(parcelable);
        }
        
        public void writeToParcel(final Parcel parcel, final int n) {
            super.writeToParcel(parcel, n);
            parcel.writeString(this.text);
        }
    }
}
