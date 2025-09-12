/*
 * Decompiled with CFR 0.152.
 */
package com.lumiyaviewer.lumiya.slproto.chat;

import com.lumiyaviewer.lumiya.slproto.chat.SLChatTextBoxDialog;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.common.TextFieldDialogBuilder;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
final class _$Lambda$Iyj6QpN_ZLoXueXenKuJvDVzcmI
implements TextFieldDialogBuilder.OnTextCancelledListener {
    private final Object -$f0;
    private final Object -$f1;

    private final /* synthetic */ void $m$0() {
        ((SLChatTextBoxDialog)this.-$f0).lambda$-com_lumiyaviewer_lumiya_slproto_chat_SLChatTextBoxDialog_4314((UserManager)this.-$f1);
    }

    public /* synthetic */ _$Lambda$Iyj6QpN_ZLoXueXenKuJvDVzcmI(Object object, Object object2) {
        this.-$f0 = object;
        this.-$f1 = object2;
    }

    @Override
    public final void onTextCancelled() {
        this.$m$0();
    }
}
