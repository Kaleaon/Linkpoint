// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v7.preference;

import android.support.annotation.NonNull;
import android.view.View;
import android.support.annotation.RestrictTo;
import android.os.Bundle;
import android.widget.EditText;

public class EditTextPreferenceDialogFragmentCompat extends PreferenceDialogFragmentCompat
{
    private static final String SAVE_STATE_TEXT = "EditTextPreferenceDialogFragment.text";
    private EditText mEditText;
    private CharSequence mText;
    
    private EditTextPreference getEditTextPreference() {
        return (EditTextPreference)this.getPreference();
    }
    
    public static EditTextPreferenceDialogFragmentCompat newInstance(final String s) {
        final EditTextPreferenceDialogFragmentCompat editTextPreferenceDialogFragmentCompat = new EditTextPreferenceDialogFragmentCompat();
        final Bundle arguments = new Bundle(1);
        arguments.putString("key", s);
        editTextPreferenceDialogFragmentCompat.setArguments(arguments);
        return editTextPreferenceDialogFragmentCompat;
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    @Override
    protected boolean needInputMethod() {
        return true;
    }
    
    @Override
    protected void onBindDialogView(final View view) {
        super.onBindDialogView(view);
        this.mEditText = (EditText)view.findViewById(16908291);
        if (this.mEditText != null) {
            this.mEditText.setText(this.mText);
            return;
        }
        throw new IllegalStateException("Dialog view must contain an EditText with id @android:id/edit");
    }
    
    @Override
    public void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        if (bundle != null) {
            this.mText = bundle.getCharSequence("EditTextPreferenceDialogFragment.text");
        }
        else {
            this.mText = this.getEditTextPreference().getText();
        }
    }
    
    @Override
    public void onDialogClosed(final boolean b) {
        if (b) {
            final String string = this.mEditText.getText().toString();
            if (this.getEditTextPreference().callChangeListener(string)) {
                this.getEditTextPreference().setText(string);
            }
        }
    }
    
    @Override
    public void onSaveInstanceState(@NonNull final Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putCharSequence("EditTextPreferenceDialogFragment.text", this.mText);
    }
}
