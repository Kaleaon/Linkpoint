/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.content.DialogInterface
 *  android.content.DialogInterface$OnClickListener
 *  android.widget.EditText
 */
package com.lumiyaviewer.lumiya.ui.common;

import android.content.DialogInterface;
import android.widget.EditText;
import com.lumiyaviewer.lumiya.ui.common.TextFieldDialogBuilder;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
final class _$Lambda$PTYOAfnVIwPVEdUoAgskdOeAqDw$1
implements DialogInterface.OnClickListener {
    private final Object -$f0;
    private final Object -$f1;

    private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int n) {
        ((TextFieldDialogBuilder)this.-$f0).lambda$-com_lumiyaviewer_lumiya_ui_common_TextFieldDialogBuilder_2416((EditText)this.-$f1, dialogInterface, n);
    }

    public /* synthetic */ _$Lambda$PTYOAfnVIwPVEdUoAgskdOeAqDw$1(Object object, Object object2) {
        this.-$f0 = object;
        this.-$f1 = object2;
    }

    public final void onClick(DialogInterface dialogInterface, int n) {
        this.$m$0(dialogInterface, n);
    }
}
