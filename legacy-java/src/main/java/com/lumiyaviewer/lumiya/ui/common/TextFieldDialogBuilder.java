package com.lumiyaviewer.lumiya.ui.common;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.TypedValue;
import android.widget.EditText;
import android.widget.FrameLayout;

/* loaded from: classes.dex */
public class TextFieldDialogBuilder {
    private final Context context;
    private String title = null;
    private String defaultText = "";
    private OnTextEnteredListener listener = null;
    private OnTextCancelledListener cancelledListener = null;

    /* loaded from: classes.dex */
    public interface OnTextCancelledListener {
        void onTextCancelled();
    }

    /* loaded from: classes.dex */
    public interface OnTextEnteredListener {
        void onTextEntered(String str);
    }

    public TextFieldDialogBuilder(Context context) {
        this.context = context;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_common_TextFieldDialogBuilder_2200, reason: not valid java name */
    public /* synthetic */ void m559x76be5af0(DialogInterface dialogInterface, int i) {
        dialogInterface.dismiss();
        if (this.cancelledListener != null) {
            this.cancelledListener.onTextCancelled();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_common_TextFieldDialogBuilder_2416, reason: not valid java name */
    public /* synthetic */ void m560x76be6297(EditText editText, DialogInterface dialogInterface, int i) {
        dialogInterface.dismiss();
        if (this.listener != null) {
            this.listener.onTextEntered(editText.getText().toString());
        }
    }

    public TextFieldDialogBuilder setDefaultText(String str) {
        this.defaultText = str;
        return this;
    }

    public TextFieldDialogBuilder setOnTextCancelledListener(OnTextCancelledListener onTextCancelledListener) {
        this.cancelledListener = onTextCancelledListener;
        return this;
    }

    public TextFieldDialogBuilder setOnTextEnteredListener(OnTextEnteredListener onTextEnteredListener) {
        this.listener = onTextEnteredListener;
        return this;
    }

    public TextFieldDialogBuilder setTitle(String str) {
        this.title = str;
        return this;
    }

    public void show() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
        builder.setTitle(this.title);
        final EditText editText = new EditText(this.context);
        editText.setText(this.defaultText);
        editText.setSingleLine(true);
        FrameLayout frameLayout = new FrameLayout(this.context);
        int applyDimension = (int) TypedValue.applyDimension(1, 10.0f, this.context.getResources().getDisplayMetrics());
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-1, -2);
        layoutParams.leftMargin = applyDimension;
        layoutParams.rightMargin = applyDimension;
        editText.setLayoutParams(layoutParams);
        frameLayout.addView(editText);
        builder.setView(frameLayout);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.common.-$Lambda$PTYOAfnVIwPVEdUoAgskdOeAqDw
            private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int i) {
                ((TextFieldDialogBuilder) this).m559x76be5af0(dialogInterface, i);
            }

            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                $m$0(dialogInterface, i);
            }
        });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.common.-$Lambda$PTYOAfnVIwPVEdUoAgskdOeAqDw.1
            private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int i) {
                ((TextFieldDialogBuilder) this).m560x76be6297((EditText) editText, dialogInterface, i);
            }

            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                $m$0(dialogInterface, i);
            }
        });
        builder.create().show();
    }
}
