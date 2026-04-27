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
import java.util.concurrent.atomic.AtomicInteger;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
final class _$Lambda$qgA5NpRVpRFsQYZFFPT9VQYjWms$17
implements DialogInterface.OnClickListener {
    private final Object -$f0;
    private final Object -$f1;
    private final Object -$f2;

    private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int n) {
        ((GroupMainProfileTab)this.-$f0).lambda$-com_lumiyaviewer_lumiya_ui_chat_profiles_GroupMainProfileTab_11438((AtomicInteger)this.-$f1, (UUID)this.-$f2, dialogInterface, n);
    }

    public /* synthetic */ _$Lambda$qgA5NpRVpRFsQYZFFPT9VQYjWms$17(Object object, Object object2, Object object3) {
        this.-$f0 = object;
        this.-$f1 = object2;
        this.-$f2 = object3;
    }

    public final void onClick(DialogInterface dialogInterface, int n) {
        this.$m$0(dialogInterface, n);
    }
}
