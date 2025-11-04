// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v7.preference;

import android.os.Parcel;
import android.os.Parcelable$Creator;
import android.support.annotation.RestrictTo;
import android.text.TextUtils;
import android.widget.TextView;
import android.view.View;
import android.os.Parcelable;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.content.Context;

public abstract class TwoStatePreference extends Preference
{
    protected boolean mChecked;
    private boolean mCheckedSet;
    private boolean mDisableDependentsState;
    private CharSequence mSummaryOff;
    private CharSequence mSummaryOn;
    
    public TwoStatePreference(final Context context) {
        this(context, null);
    }
    
    public TwoStatePreference(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public TwoStatePreference(final Context context, final AttributeSet set, final int n) {
        this(context, set, n, 0);
    }
    
    public TwoStatePreference(final Context context, final AttributeSet set, final int n, final int n2) {
        super(context, set, n, n2);
    }
    
    public boolean getDisableDependentsState() {
        return this.mDisableDependentsState;
    }
    
    public CharSequence getSummaryOff() {
        return this.mSummaryOff;
    }
    
    public CharSequence getSummaryOn() {
        return this.mSummaryOn;
    }
    
    public boolean isChecked() {
        return this.mChecked;
    }
    
    @Override
    protected void onClick() {
        boolean b = false;
        super.onClick();
        if (!this.isChecked()) {
            b = true;
        }
        if (this.callChangeListener(b)) {
            this.setChecked(b);
        }
    }
    
    @Override
    protected Object onGetDefaultValue(final TypedArray typedArray, final int n) {
        return typedArray.getBoolean(n, false);
    }
    
    @Override
    protected void onRestoreInstanceState(final Parcelable parcelable) {
        if (parcelable != null && parcelable.getClass().equals(SavedState.class)) {
            final SavedState savedState = (SavedState)parcelable;
            super.onRestoreInstanceState(savedState.getSuperState());
            this.setChecked(savedState.checked);
            return;
        }
        super.onRestoreInstanceState(parcelable);
    }
    
    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable onSaveInstanceState = super.onSaveInstanceState();
        if (!this.isPersistent()) {
            final SavedState savedState = new SavedState(onSaveInstanceState);
            savedState.checked = this.isChecked();
            return (Parcelable)savedState;
        }
        return onSaveInstanceState;
    }
    
    @Override
    protected void onSetInitialValue(final boolean b, final Object o) {
        boolean checked;
        if (!b) {
            checked = (boolean)o;
        }
        else {
            checked = this.getPersistedBoolean(this.mChecked);
        }
        this.setChecked(checked);
    }
    
    public void setChecked(final boolean mChecked) {
        boolean b = false;
        if (this.mChecked != mChecked) {
            b = true;
        }
        if (b || !this.mCheckedSet) {
            this.mChecked = mChecked;
            this.mCheckedSet = true;
            this.persistBoolean(mChecked);
            if (b) {
                this.notifyDependencyChange(this.shouldDisableDependents());
                this.notifyChanged();
            }
        }
    }
    
    public void setDisableDependentsState(final boolean mDisableDependentsState) {
        this.mDisableDependentsState = mDisableDependentsState;
    }
    
    public void setSummaryOff(final int n) {
        this.setSummaryOff(this.getContext().getString(n));
    }
    
    public void setSummaryOff(final CharSequence mSummaryOff) {
        this.mSummaryOff = mSummaryOff;
        if (!this.isChecked()) {
            this.notifyChanged();
        }
    }
    
    public void setSummaryOn(final int n) {
        this.setSummaryOn(this.getContext().getString(n));
    }
    
    public void setSummaryOn(final CharSequence mSummaryOn) {
        this.mSummaryOn = mSummaryOn;
        if (this.isChecked()) {
            this.notifyChanged();
        }
    }
    
    @Override
    public boolean shouldDisableDependents() {
        final boolean b = false;
        boolean mChecked;
        if (!this.mDisableDependentsState) {
            if (this.mChecked) {
                mChecked = false;
            }
            else {
                mChecked = true;
            }
        }
        else {
            mChecked = this.mChecked;
        }
        return mChecked || super.shouldDisableDependents() || b;
    }
    
    protected void syncSummaryView(final PreferenceViewHolder preferenceViewHolder) {
        this.syncSummaryView(preferenceViewHolder.findViewById(16908304));
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    protected void syncSummaryView(final View view) {
        int visibility = 0;
        if (view instanceof TextView) {
            final TextView textView = (TextView)view;
            int n = 1;
            if (this.mChecked && !TextUtils.isEmpty(this.mSummaryOn)) {
                textView.setText(this.mSummaryOn);
                n = 0;
            }
            else if (!this.mChecked && !TextUtils.isEmpty(this.mSummaryOff)) {
                textView.setText(this.mSummaryOff);
                n = 0;
            }
            if (n != 0) {
                final CharSequence summary = this.getSummary();
                if (!TextUtils.isEmpty(summary)) {
                    textView.setText(summary);
                    n = 0;
                }
            }
            if (n != 0) {
                visibility = 8;
            }
            if (visibility != textView.getVisibility()) {
                textView.setVisibility(visibility);
            }
        }
    }
    
    static class SavedState extends BaseSavedState
    {
        public static final Parcelable$Creator<SavedState> CREATOR;
        boolean checked;
        
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
            boolean checked = true;
            super(parcel);
            if (parcel.readInt() != 1) {
                checked = false;
            }
            this.checked = checked;
        }
        
        public SavedState(final Parcelable parcelable) {
            super(parcelable);
        }
        
        public void writeToParcel(final Parcel parcel, int n) {
            final int n2 = 0;
            super.writeToParcel(parcel, n);
            if (!this.checked) {
                n = n2;
            }
            else {
                n = 1;
            }
            parcel.writeInt(n);
        }
    }
}
