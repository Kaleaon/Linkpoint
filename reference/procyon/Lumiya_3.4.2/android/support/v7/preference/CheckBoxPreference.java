// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v7.preference;

import android.support.annotation.RestrictTo;
import android.view.accessibility.AccessibilityManager;
import android.widget.Checkable;
import android.widget.CompoundButton$OnCheckedChangeListener;
import android.widget.CompoundButton;
import android.view.View;
import android.content.res.TypedArray;
import android.support.v4.content.res.TypedArrayUtils;
import android.util.AttributeSet;
import android.content.Context;

public class CheckBoxPreference extends TwoStatePreference
{
    private final Listener mListener;
    
    public CheckBoxPreference(final Context context) {
        this(context, null);
    }
    
    public CheckBoxPreference(final Context context, final AttributeSet set) {
        this(context, set, TypedArrayUtils.getAttr(context, R.attr.checkBoxPreferenceStyle, 16842895));
    }
    
    public CheckBoxPreference(final Context context, final AttributeSet set, final int n) {
        this(context, set, n, 0);
    }
    
    public CheckBoxPreference(final Context context, final AttributeSet set, final int n, final int n2) {
        super(context, set, n, n2);
        this.mListener = new Listener();
        final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, R.styleable.CheckBoxPreference, n, n2);
        this.setSummaryOn(TypedArrayUtils.getString(obtainStyledAttributes, R.styleable.CheckBoxPreference_summaryOn, R.styleable.CheckBoxPreference_android_summaryOn));
        this.setSummaryOff(TypedArrayUtils.getString(obtainStyledAttributes, R.styleable.CheckBoxPreference_summaryOff, R.styleable.CheckBoxPreference_android_summaryOff));
        this.setDisableDependentsState(TypedArrayUtils.getBoolean(obtainStyledAttributes, R.styleable.CheckBoxPreference_disableDependentsState, R.styleable.CheckBoxPreference_android_disableDependentsState, false));
        obtainStyledAttributes.recycle();
    }
    
    private void syncCheckboxView(final View view) {
        if (view instanceof CompoundButton) {
            ((CompoundButton)view).setOnCheckedChangeListener((CompoundButton$OnCheckedChangeListener)null);
        }
        if (view instanceof Checkable) {
            ((Checkable)view).setChecked(this.mChecked);
        }
        if (view instanceof CompoundButton) {
            ((CompoundButton)view).setOnCheckedChangeListener((CompoundButton$OnCheckedChangeListener)this.mListener);
        }
    }
    
    private void syncViewIfAccessibilityEnabled(final View view) {
        if (((AccessibilityManager)this.getContext().getSystemService("accessibility")).isEnabled()) {
            this.syncCheckboxView(view.findViewById(16908289));
            this.syncSummaryView(view.findViewById(16908304));
        }
    }
    
    @Override
    public void onBindViewHolder(final PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        this.syncCheckboxView(preferenceViewHolder.findViewById(16908289));
        this.syncSummaryView(preferenceViewHolder);
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    @Override
    protected void performClick(final View view) {
        super.performClick(view);
        this.syncViewIfAccessibilityEnabled(view);
    }
    
    private class Listener implements CompoundButton$OnCheckedChangeListener
    {
        public void onCheckedChanged(final CompoundButton compoundButton, final boolean b) {
            final boolean b2 = false;
            if (CheckBoxPreference.this.callChangeListener(b)) {
                CheckBoxPreference.this.setChecked(b);
                return;
            }
            compoundButton.setChecked(!b || b2);
        }
    }
}
