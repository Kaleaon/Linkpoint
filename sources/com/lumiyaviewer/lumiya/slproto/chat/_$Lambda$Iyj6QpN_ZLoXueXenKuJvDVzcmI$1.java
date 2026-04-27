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
final class _$Lambda$Iyj6QpN_ZLoXueXenKuJvDVzcmI$1
implements TextFieldDialogBuilder.OnTextEnteredListener {
    private final Object -$f0;
    private final Object -$f1;

    private final /* synthetic */ void $m$0(String string) {
        ((SLChatTextBoxDialog)this.-$f0).lambda$-com_lumiyaviewer_lumiya_slproto_chat_SLChatTextBoxDialog_4223((UserManager)this.-$f1, string);
    }

    public /* synthetic */ _$Lambda$Iyj6QpN_ZLoXueXenKuJvDVzcmI$1(Object object, Object object2) {
        this.-$f0 = object;
        this.-$f1 = object2;
    }

    @Override
    public final void onTextEntered(String string) {
        this.$m$0(string);
    }
}
