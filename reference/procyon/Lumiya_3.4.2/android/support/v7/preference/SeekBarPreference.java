// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v7.preference;

import android.os.Parcel;
import android.os.Parcelable$Creator;
import android.os.Parcelable;
import android.content.res.TypedArray;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.util.AttributeSet;
import android.content.Context;
import android.widget.TextView;
import android.view.View$OnKeyListener;
import android.widget.SeekBar$OnSeekBarChangeListener;
import android.widget.SeekBar;

public class SeekBarPreference extends Preference
{
    private static final String TAG = "SeekBarPreference";
    private boolean mAdjustable;
    private int mMax;
    private int mMin;
    private SeekBar mSeekBar;
    private SeekBar$OnSeekBarChangeListener mSeekBarChangeListener;
    private int mSeekBarIncrement;
    private View$OnKeyListener mSeekBarKeyListener;
    private int mSeekBarValue;
    private TextView mSeekBarValueTextView;
    private boolean mShowSeekBarValue;
    private boolean mTrackingTouch;
    
    public SeekBarPreference(final Context context) {
        this(context, null);
    }
    
    public SeekBarPreference(final Context context, final AttributeSet set) {
        this(context, set, R.attr.seekBarPreferenceStyle);
    }
    
    public SeekBarPreference(final Context context, final AttributeSet set, final int n) {
        this(context, set, n, 0);
    }
    
    public SeekBarPreference(final Context context, final AttributeSet set, final int n, final int n2) {
        super(context, set, n, n2);
        this.mSeekBarChangeListener = (SeekBar$OnSeekBarChangeListener)new SeekBar$OnSeekBarChangeListener() {
            public void onProgressChanged(final SeekBar seekBar, final int n, final boolean b) {
                if (b && !SeekBarPreference.this.mTrackingTouch) {
                    SeekBarPreference.this.syncValueInternal(seekBar);
                }
            }
            
            public void onStartTrackingTouch(final SeekBar seekBar) {
                SeekBarPreference.this.mTrackingTouch = true;
            }
            
            public void onStopTrackingTouch(final SeekBar seekBar) {
                SeekBarPreference.this.mTrackingTouch = false;
                if (seekBar.getProgress() + SeekBarPreference.this.mMin != SeekBarPreference.this.mSeekBarValue) {
                    SeekBarPreference.this.syncValueInternal(seekBar);
                }
            }
        };
        this.mSeekBarKeyListener = (View$OnKeyListener)new View$OnKeyListener() {
            public boolean onKey(final View view, final int n, final KeyEvent keyEvent) {
                if (keyEvent.getAction() != 0) {
                    return false;
                }
                if (!SeekBarPreference.this.mAdjustable && (n == 21 || n == 22)) {
                    return false;
                }
                if (n == 23 || n == 66) {
                    return false;
                }
                if (SeekBarPreference.this.mSeekBar != null) {
                    return SeekBarPreference.this.mSeekBar.onKeyDown(n, keyEvent);
                }
                Log.e("SeekBarPreference", "SeekBar view is null and hence cannot be adjusted.");
                return false;
            }
        };
        final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, R.styleable.SeekBarPreference, n, n2);
        this.mMin = obtainStyledAttributes.getInt(R.styleable.SeekBarPreference_min, 0);
        this.setMax(obtainStyledAttributes.getInt(R.styleable.SeekBarPreference_android_max, 100));
        this.setSeekBarIncrement(obtainStyledAttributes.getInt(R.styleable.SeekBarPreference_seekBarIncrement, 0));
        this.mAdjustable = obtainStyledAttributes.getBoolean(R.styleable.SeekBarPreference_adjustable, true);
        this.mShowSeekBarValue = obtainStyledAttributes.getBoolean(R.styleable.SeekBarPreference_showSeekBarValue, true);
        obtainStyledAttributes.recycle();
    }
    
    private void setValueInternal(int mSeekBarValue, final boolean b) {
        if (mSeekBarValue < this.mMin) {
            mSeekBarValue = this.mMin;
        }
        if (mSeekBarValue > this.mMax) {
            mSeekBarValue = this.mMax;
        }
        if (mSeekBarValue != this.mSeekBarValue) {
            this.mSeekBarValue = mSeekBarValue;
            if (this.mSeekBarValueTextView != null) {
                this.mSeekBarValueTextView.setText((CharSequence)String.valueOf(this.mSeekBarValue));
            }
            this.persistInt(mSeekBarValue);
            if (b) {
                this.notifyChanged();
            }
        }
    }
    
    private void syncValueInternal(final SeekBar seekBar) {
        final int i = this.mMin + seekBar.getProgress();
        if (i != this.mSeekBarValue) {
            if (!this.callChangeListener(i)) {
                seekBar.setProgress(this.mSeekBarValue - this.mMin);
            }
            else {
                this.setValueInternal(i, false);
            }
        }
    }
    
    public int getMax() {
        return this.mMax;
    }
    
    public int getMin() {
        return this.mMin;
    }
    
    public final int getSeekBarIncrement() {
        return this.mSeekBarIncrement;
    }
    
    public int getValue() {
        return this.mSeekBarValue;
    }
    
    public boolean isAdjustable() {
        return this.mAdjustable;
    }
    
    @Override
    public void onBindViewHolder(final PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        preferenceViewHolder.itemView.setOnKeyListener(this.mSeekBarKeyListener);
        this.mSeekBar = (SeekBar)preferenceViewHolder.findViewById(R.id.seekbar);
        this.mSeekBarValueTextView = (TextView)preferenceViewHolder.findViewById(R.id.seekbar_value);
        if (!this.mShowSeekBarValue) {
            this.mSeekBarValueTextView.setVisibility(8);
            this.mSeekBarValueTextView = null;
        }
        else {
            this.mSeekBarValueTextView.setVisibility(0);
        }
        if (this.mSeekBar != null) {
            this.mSeekBar.setOnSeekBarChangeListener(this.mSeekBarChangeListener);
            this.mSeekBar.setMax(this.mMax - this.mMin);
            if (this.mSeekBarIncrement == 0) {
                this.mSeekBarIncrement = this.mSeekBar.getKeyProgressIncrement();
            }
            else {
                this.mSeekBar.setKeyProgressIncrement(this.mSeekBarIncrement);
            }
            this.mSeekBar.setProgress(this.mSeekBarValue - this.mMin);
            if (this.mSeekBarValueTextView != null) {
                this.mSeekBarValueTextView.setText((CharSequence)String.valueOf(this.mSeekBarValue));
            }
            this.mSeekBar.setEnabled(this.isEnabled());
            return;
        }
        Log.e("SeekBarPreference", "SeekBar view is null in onBindViewHolder.");
    }
    
    @Override
    protected Object onGetDefaultValue(final TypedArray typedArray, final int n) {
        return typedArray.getInt(n, 0);
    }
    
    @Override
    protected void onRestoreInstanceState(final Parcelable parcelable) {
        if (parcelable.getClass().equals(SavedState.class)) {
            final SavedState savedState = (SavedState)parcelable;
            super.onRestoreInstanceState(savedState.getSuperState());
            this.mSeekBarValue = savedState.seekBarValue;
            this.mMin = savedState.min;
            this.mMax = savedState.max;
            this.notifyChanged();
            return;
        }
        super.onRestoreInstanceState(parcelable);
    }
    
    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable onSaveInstanceState = super.onSaveInstanceState();
        if (!this.isPersistent()) {
            final SavedState savedState = new SavedState(onSaveInstanceState);
            savedState.seekBarValue = this.mSeekBarValue;
            savedState.min = this.mMin;
            savedState.max = this.mMax;
            return (Parcelable)savedState;
        }
        return onSaveInstanceState;
    }
    
    @Override
    protected void onSetInitialValue(final boolean b, final Object o) {
        int value;
        if (!b) {
            value = (int)o;
        }
        else {
            value = this.getPersistedInt(this.mSeekBarValue);
        }
        this.setValue(value);
    }
    
    public void setAdjustable(final boolean mAdjustable) {
        this.mAdjustable = mAdjustable;
    }
    
    public final void setMax(int mMin) {
        if (mMin < this.mMin) {
            mMin = this.mMin;
        }
        if (mMin != this.mMax) {
            this.mMax = mMin;
            this.notifyChanged();
        }
    }
    
    public void setMin(int mMax) {
        if (mMax > this.mMax) {
            mMax = this.mMax;
        }
        if (mMax != this.mMin) {
            this.mMin = mMax;
            this.notifyChanged();
        }
    }
    
    public final void setSeekBarIncrement(final int a) {
        if (a != this.mSeekBarIncrement) {
            this.mSeekBarIncrement = Math.min(this.mMax - this.mMin, Math.abs(a));
            this.notifyChanged();
        }
    }
    
    public void setValue(final int n) {
        this.setValueInternal(n, true);
    }
    
    private static class SavedState extends BaseSavedState
    {
        public static final Parcelable$Creator<SavedState> CREATOR;
        int max;
        int min;
        int seekBarValue;
        
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
            this.seekBarValue = parcel.readInt();
            this.min = parcel.readInt();
            this.max = parcel.readInt();
        }
        
        public SavedState(final Parcelable parcelable) {
            super(parcelable);
        }
        
        public void writeToParcel(final Parcel parcel, final int n) {
            super.writeToParcel(parcel, n);
            parcel.writeInt(this.seekBarValue);
            parcel.writeInt(this.min);
            parcel.writeInt(this.max);
        }
    }
}
