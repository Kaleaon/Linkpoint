// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.common;

import android.content.DialogInterface$OnClickListener;
import android.view.View;
import android.view.ViewGroup$LayoutParams;
import android.widget.FrameLayout$LayoutParams;
import android.util.TypedValue;
import android.widget.FrameLayout;
import android.app.AlertDialog$Builder;
import android.widget.EditText;
import android.content.DialogInterface;
import android.content.Context;

public class TextFieldDialogBuilder
{
    private OnTextCancelledListener cancelledListener;
    private final Context context;
    private String defaultText;
    private OnTextEnteredListener listener;
    private String title;
    
    public TextFieldDialogBuilder(final Context context) {
        this.title = null;
        this.defaultText = "";
        this.listener = null;
        this.cancelledListener = null;
        this.context = context;
    }
    
    public TextFieldDialogBuilder setDefaultText(final String defaultText) {
        this.defaultText = defaultText;
        return this;
    }
    
    public TextFieldDialogBuilder setOnTextCancelledListener(final OnTextCancelledListener cancelledListener) {
        this.cancelledListener = cancelledListener;
        return this;
    }
    
    public TextFieldDialogBuilder setOnTextEnteredListener(final OnTextEnteredListener listener) {
        this.listener = listener;
        return this;
    }
    
    public TextFieldDialogBuilder setTitle(final String title) {
        this.title = title;
        return this;
    }
    
    public void show() {
        final AlertDialog$Builder alertDialog$Builder = new AlertDialog$Builder(this.context);
        alertDialog$Builder.setTitle((CharSequence)this.title);
        final EditText editText = new EditText(this.context);
        editText.setText((CharSequence)this.defaultText);
        editText.setSingleLine(true);
        final FrameLayout view = new FrameLayout(this.context);
        final int n = (int)TypedValue.applyDimension(1, 10.0f, this.context.getResources().getDisplayMetrics());
        final FrameLayout$LayoutParams layoutParams = new FrameLayout$LayoutParams(-1, -2);
        layoutParams.leftMargin = n;
        layoutParams.rightMargin = n;
        editText.setLayoutParams((ViewGroup$LayoutParams)layoutParams);
        view.addView((View)editText);
        alertDialog$Builder.setView((View)view);
        alertDialog$Builder.setNegativeButton((CharSequence)"Cancel", (DialogInterface$OnClickListener)new _$Lambda$PTYOAfnVIwPVEdUoAgskdOeAqDw(this));
        alertDialog$Builder.setPositiveButton((CharSequence)"OK", (DialogInterface$OnClickListener)new _$Lambda$PTYOAfnVIwPVEdUoAgskdOeAqDw$1(this, editText));
        alertDialog$Builder.create().show();
    }
    
    public interface OnTextCancelledListener
    {
        void onTextCancelled();
    }
    
    public interface OnTextEnteredListener
    {
        void onTextEntered(final String p0);
    }
}
