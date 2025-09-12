/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.content.DialogInterface
 *  android.content.DialogInterface$OnClickListener
 */
package com.lumiyaviewer.lumiya.ui.chat.profiles;

import android.content.DialogInterface;
import com.lumiyaviewer.lumiya.ui.chat.profiles.GroupMainProfileTab;
import java.util.UUID;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
final class _$Lambda$qgA5NpRVpRFsQYZFFPT9VQYjWms$16
implements DialogInterface.OnClickListener {
    private final Object -$f0;
    private final Object -$f1;

    private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int n) {
        ((GroupMainProfileTab)this.-$f0).lambda$-com_lumiyaviewer_lumiya_ui_chat_profiles_GroupMainProfileTab_6577((UUID)this.-$f1, dialogInterface, n);
    }

    public /* synthetic */ _$Lambda$qgA5NpRVpRFsQYZFFPT9VQYjWms$16(Object object, Object object2) {
        this.-$f0 = object;
        this.-$f1 = object2;
    }

    public final void onClick(DialogInterface dialogInterface, int n) {
        this.$m$0(dialogInterface, n);
    }
}
