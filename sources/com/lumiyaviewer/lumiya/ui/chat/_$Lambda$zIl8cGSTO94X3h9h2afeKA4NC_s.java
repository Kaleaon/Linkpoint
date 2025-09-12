/*
 * Decompiled with CFR 0.152.
 */
package com.lumiyaviewer.lumiya.ui.chat;

import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.slproto.users.manager.CurrentLocationInfo;
import com.lumiyaviewer.lumiya.ui.chat.ContactsFragment;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
final class _$Lambda$zIl8cGSTO94X3h9h2afeKA4NC_s
implements Subscription.OnData {
    private final Object -$f0;

    private final /* synthetic */ void $m$0(Object object) {
        ((ContactsFragment)this.-$f0).-com_lumiyaviewer_lumiya_ui_chat_ContactsFragment-mthref-0((CurrentLocationInfo)object);
    }

    public /* synthetic */ _$Lambda$zIl8cGSTO94X3h9h2afeKA4NC_s(Object object) {
        this.-$f0 = object;
    }

    public final void onData(Object object) {
        this.$m$0(object);
    }
}
