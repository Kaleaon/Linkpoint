// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v7.preference;

import android.os.Parcelable;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.graphics.Canvas;
import android.graphics.Bitmap$Config;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.content.DialogInterface;
import android.widget.TextView;
import android.text.TextUtils;
import android.view.View;
import android.support.annotation.RestrictTo;
import android.app.Dialog;
import android.support.annotation.LayoutRes;
import android.graphics.drawable.BitmapDrawable;
import android.content.DialogInterface$OnClickListener;
import android.support.v4.app.DialogFragment;

public abstract class PreferenceDialogFragmentCompat extends DialogFragment implements DialogInterface$OnClickListener
{
    protected static final String ARG_KEY = "key";
    private static final String SAVE_STATE_ICON = "PreferenceDialogFragment.icon";
    private static final String SAVE_STATE_LAYOUT = "PreferenceDialogFragment.layout";
    private static final String SAVE_STATE_MESSAGE = "PreferenceDialogFragment.message";
    private static final String SAVE_STATE_NEGATIVE_TEXT = "PreferenceDialogFragment.negativeText";
    private static final String SAVE_STATE_POSITIVE_TEXT = "PreferenceDialogFragment.positiveText";
    private static final String SAVE_STATE_TITLE = "PreferenceDialogFragment.title";
    private BitmapDrawable mDialogIcon;
    @LayoutRes
    private int mDialogLayoutRes;
    private CharSequence mDialogMessage;
    private CharSequence mDialogTitle;
    private CharSequence mNegativeButtonText;
    private CharSequence mPositiveButtonText;
    private DialogPreference mPreference;
    private int mWhichButtonClicked;
    
    private void requestInputMethod(final Dialog dialog) {
        dialog.getWindow().setSoftInputMode(5);
    }
    
    public DialogPreference getPreference() {
        if (this.mPreference == null) {
            this.mPreference = (DialogPreference)((DialogPreference.TargetFragment)this.getTargetFragment()).findPreference(this.getArguments().getString("key"));
        }
        return this.mPreference;
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    protected boolean needInputMethod() {
        return false;
    }
    
    protected void onBindDialogView(View viewById) {
        viewById = viewById.findViewById(16908299);
        if (viewById != null) {
            final CharSequence mDialogMessage = this.mDialogMessage;
            int visibility = 8;
            if (!TextUtils.isEmpty(mDialogMessage)) {
                if (viewById instanceof TextView) {
                    ((TextView)viewById).setText(mDialogMessage);
                }
                visibility = 0;
            }
            if (viewById.getVisibility() != visibility) {
                viewById.setVisibility(visibility);
            }
        }
    }
    
    public void onClick(final DialogInterface dialogInterface, final int mWhichButtonClicked) {
        this.mWhichButtonClicked = mWhichButtonClicked;
    }
    
    @Override
    public void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        final Fragment targetFragment = this.getTargetFragment();
        if (targetFragment instanceof DialogPreference.TargetFragment) {
            final DialogPreference.TargetFragment targetFragment2 = (DialogPreference.TargetFragment)targetFragment;
            final String string = this.getArguments().getString("key");
            if (bundle != null) {
                this.mDialogTitle = bundle.getCharSequence("PreferenceDialogFragment.title");
                this.mPositiveButtonText = bundle.getCharSequence("PreferenceDialogFragment.positiveText");
                this.mNegativeButtonText = bundle.getCharSequence("PreferenceDialogFragment.negativeText");
                this.mDialogMessage = bundle.getCharSequence("PreferenceDialogFragment.message");
                this.mDialogLayoutRes = bundle.getInt("PreferenceDialogFragment.layout", 0);
                final Bitmap bitmap = (Bitmap)bundle.getParcelable("PreferenceDialogFragment.icon");
                if (bitmap != null) {
                    this.mDialogIcon = new BitmapDrawable(this.getResources(), bitmap);
                }
            }
            else {
                this.mPreference = (DialogPreference)targetFragment2.findPreference(string);
                this.mDialogTitle = this.mPreference.getDialogTitle();
                this.mPositiveButtonText = this.mPreference.getPositiveButtonText();
                this.mNegativeButtonText = this.mPreference.getNegativeButtonText();
                this.mDialogMessage = this.mPreference.getDialogMessage();
                this.mDialogLayoutRes = this.mPreference.getDialogLayoutResource();
                final Drawable dialogIcon = this.mPreference.getDialogIcon();
                if (dialogIcon != null && !(dialogIcon instanceof BitmapDrawable)) {
                    final Bitmap bitmap2 = Bitmap.createBitmap(dialogIcon.getIntrinsicWidth(), dialogIcon.getIntrinsicHeight(), Bitmap$Config.ARGB_8888);
                    final Canvas canvas = new Canvas(bitmap2);
                    dialogIcon.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                    dialogIcon.draw(canvas);
                    this.mDialogIcon = new BitmapDrawable(this.getResources(), bitmap2);
                }
                else {
                    this.mDialogIcon = (BitmapDrawable)dialogIcon;
                }
            }
            return;
        }
        throw new IllegalStateException("Target fragment must implement TargetFragment interface");
    }
    
    @NonNull
    @Override
    public Dialog onCreateDialog(final Bundle bundle) {
        final FragmentActivity activity = this.getActivity();
        this.mWhichButtonClicked = -2;
        final AlertDialog.Builder setNegativeButton = new AlertDialog.Builder((Context)activity).setTitle(this.mDialogTitle).setIcon((Drawable)this.mDialogIcon).setPositiveButton(this.mPositiveButtonText, (DialogInterface$OnClickListener)this).setNegativeButton(this.mNegativeButtonText, (DialogInterface$OnClickListener)this);
        final View onCreateDialogView = this.onCreateDialogView((Context)activity);
        if (onCreateDialogView == null) {
            setNegativeButton.setMessage(this.mDialogMessage);
        }
        else {
            this.onBindDialogView(onCreateDialogView);
            setNegativeButton.setView(onCreateDialogView);
        }
        this.onPrepareDialogBuilder(setNegativeButton);
        final AlertDialog create = setNegativeButton.create();
        if (this.needInputMethod()) {
            this.requestInputMethod(create);
        }
        return create;
    }
    
    protected View onCreateDialogView(final Context context) {
        final int mDialogLayoutRes = this.mDialogLayoutRes;
        if (mDialogLayoutRes != 0) {
            return LayoutInflater.from(context).inflate(mDialogLayoutRes, (ViewGroup)null);
        }
        return null;
    }
    
    public abstract void onDialogClosed(final boolean p0);
    
    @Override
    public void onDismiss(final DialogInterface dialogInterface) {
        super.onDismiss(dialogInterface);
        this.onDialogClosed(this.mWhichButtonClicked == -1);
    }
    
    protected void onPrepareDialogBuilder(final AlertDialog.Builder builder) {
    }
    
    @Override
    public void onSaveInstanceState(@NonNull final Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putCharSequence("PreferenceDialogFragment.title", this.mDialogTitle);
        bundle.putCharSequence("PreferenceDialogFragment.positiveText", this.mPositiveButtonText);
        bundle.putCharSequence("PreferenceDialogFragment.negativeText", this.mNegativeButtonText);
        bundle.putCharSequence("PreferenceDialogFragment.message", this.mDialogMessage);
        bundle.putInt("PreferenceDialogFragment.layout", this.mDialogLayoutRes);
        if (this.mDialogIcon != null) {
            bundle.putParcelable("PreferenceDialogFragment.icon", (Parcelable)this.mDialogIcon.getBitmap());
        }
    }
}
