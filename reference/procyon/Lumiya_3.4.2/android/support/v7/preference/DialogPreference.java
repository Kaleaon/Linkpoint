// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v7.preference;

import android.support.v4.content.ContextCompat;
import android.content.res.TypedArray;
import android.support.v4.content.res.TypedArrayUtils;
import android.util.AttributeSet;
import android.content.Context;
import android.graphics.drawable.Drawable;

public abstract class DialogPreference extends Preference
{
    private Drawable mDialogIcon;
    private int mDialogLayoutResId;
    private CharSequence mDialogMessage;
    private CharSequence mDialogTitle;
    private CharSequence mNegativeButtonText;
    private CharSequence mPositiveButtonText;
    
    public DialogPreference(final Context context) {
        this(context, null);
    }
    
    public DialogPreference(final Context context, final AttributeSet set) {
        this(context, set, TypedArrayUtils.getAttr(context, R.attr.dialogPreferenceStyle, 16842897));
    }
    
    public DialogPreference(final Context context, final AttributeSet set, final int n) {
        this(context, set, n, 0);
    }
    
    public DialogPreference(final Context context, final AttributeSet set, final int n, final int n2) {
        super(context, set, n, n2);
        final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, R.styleable.DialogPreference, n, n2);
        this.mDialogTitle = TypedArrayUtils.getString(obtainStyledAttributes, R.styleable.DialogPreference_dialogTitle, R.styleable.DialogPreference_android_dialogTitle);
        if (this.mDialogTitle == null) {
            this.mDialogTitle = this.getTitle();
        }
        this.mDialogMessage = TypedArrayUtils.getString(obtainStyledAttributes, R.styleable.DialogPreference_dialogMessage, R.styleable.DialogPreference_android_dialogMessage);
        this.mDialogIcon = TypedArrayUtils.getDrawable(obtainStyledAttributes, R.styleable.DialogPreference_dialogIcon, R.styleable.DialogPreference_android_dialogIcon);
        this.mPositiveButtonText = TypedArrayUtils.getString(obtainStyledAttributes, R.styleable.DialogPreference_positiveButtonText, R.styleable.DialogPreference_android_positiveButtonText);
        this.mNegativeButtonText = TypedArrayUtils.getString(obtainStyledAttributes, R.styleable.DialogPreference_negativeButtonText, R.styleable.DialogPreference_android_negativeButtonText);
        this.mDialogLayoutResId = TypedArrayUtils.getResourceId(obtainStyledAttributes, R.styleable.DialogPreference_dialogLayout, R.styleable.DialogPreference_android_dialogLayout, 0);
        obtainStyledAttributes.recycle();
    }
    
    public Drawable getDialogIcon() {
        return this.mDialogIcon;
    }
    
    public int getDialogLayoutResource() {
        return this.mDialogLayoutResId;
    }
    
    public CharSequence getDialogMessage() {
        return this.mDialogMessage;
    }
    
    public CharSequence getDialogTitle() {
        return this.mDialogTitle;
    }
    
    public CharSequence getNegativeButtonText() {
        return this.mNegativeButtonText;
    }
    
    public CharSequence getPositiveButtonText() {
        return this.mPositiveButtonText;
    }
    
    @Override
    protected void onClick() {
        this.getPreferenceManager().showDialog(this);
    }
    
    public void setDialogIcon(final int n) {
        this.mDialogIcon = ContextCompat.getDrawable(this.getContext(), n);
    }
    
    public void setDialogIcon(final Drawable mDialogIcon) {
        this.mDialogIcon = mDialogIcon;
    }
    
    public void setDialogLayoutResource(final int mDialogLayoutResId) {
        this.mDialogLayoutResId = mDialogLayoutResId;
    }
    
    public void setDialogMessage(final int n) {
        this.setDialogMessage(this.getContext().getString(n));
    }
    
    public void setDialogMessage(final CharSequence mDialogMessage) {
        this.mDialogMessage = mDialogMessage;
    }
    
    public void setDialogTitle(final int n) {
        this.setDialogTitle(this.getContext().getString(n));
    }
    
    public void setDialogTitle(final CharSequence mDialogTitle) {
        this.mDialogTitle = mDialogTitle;
    }
    
    public void setNegativeButtonText(final int n) {
        this.setNegativeButtonText(this.getContext().getString(n));
    }
    
    public void setNegativeButtonText(final CharSequence mNegativeButtonText) {
        this.mNegativeButtonText = mNegativeButtonText;
    }
    
    public void setPositiveButtonText(final int n) {
        this.setPositiveButtonText(this.getContext().getString(n));
    }
    
    public void setPositiveButtonText(final CharSequence mPositiveButtonText) {
        this.mPositiveButtonText = mPositiveButtonText;
    }
    
    public interface TargetFragment
    {
        Preference findPreference(final CharSequence p0);
    }
}
