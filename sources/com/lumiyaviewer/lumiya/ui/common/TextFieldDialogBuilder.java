// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.common;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.text.Editable;
import android.util.TypedValue;
import android.widget.EditText;
import android.widget.FrameLayout;

public class TextFieldDialogBuilder
{
    public static interface OnTextCancelledListener
    {

        public abstract void onTextCancelled();
    }

    public static interface OnTextEnteredListener
    {

        public abstract void onTextEntered(String s);
    }


    private OnTextCancelledListener cancelledListener;
    private final Context context;
    private String defaultText;
    private OnTextEnteredListener listener;
    private String title;

    public TextFieldDialogBuilder(Context context1)
    {
        title = null;
        defaultText = "";
        listener = null;
        cancelledListener = null;
        context = context1;
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_common_TextFieldDialogBuilder_2200(DialogInterface dialoginterface, int i)
    {
        dialoginterface.dismiss();
        if (cancelledListener != null)
        {
            cancelledListener.onTextCancelled();
        }
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_common_TextFieldDialogBuilder_2416(EditText edittext, DialogInterface dialoginterface, int i)
    {
        dialoginterface.dismiss();
        if (listener != null)
        {
            listener.onTextEntered(edittext.getText().toString());
        }
    }

    public TextFieldDialogBuilder setDefaultText(String s)
    {
        defaultText = s;
        return this;
    }

    public TextFieldDialogBuilder setOnTextCancelledListener(OnTextCancelledListener ontextcancelledlistener)
    {
        cancelledListener = ontextcancelledlistener;
        return this;
    }

    public TextFieldDialogBuilder setOnTextEnteredListener(OnTextEnteredListener ontextenteredlistener)
    {
        listener = ontextenteredlistener;
        return this;
    }

    public TextFieldDialogBuilder setTitle(String s)
    {
        title = s;
        return this;
    }

    public void show()
    {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setTitle(title);
        EditText edittext = new EditText(context);
        edittext.setText(defaultText);
        edittext.setSingleLine(true);
        FrameLayout framelayout = new FrameLayout(context);
        int i = (int)TypedValue.applyDimension(1, 10F, context.getResources().getDisplayMetrics());
        android.widget.FrameLayout.LayoutParams layoutparams = new android.widget.FrameLayout.LayoutParams(-1, -2);
        layoutparams.leftMargin = i;
        layoutparams.rightMargin = i;
        edittext.setLayoutParams(layoutparams);
        framelayout.addView(edittext);
        builder.setView(framelayout);
        builder.setNegativeButton("Cancel", new _2D_.Lambda.PTYOAfnVIwPVEdUoAgskdOeAqDw(this));
        builder.setPositiveButton("OK", new _2D_.Lambda.PTYOAfnVIwPVEdUoAgskdOeAqDw._cls1(this, edittext));
        builder.create().show();
    }
}
