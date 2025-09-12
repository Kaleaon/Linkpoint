/*
 * Decompiled with CFR 0.152.
 */
package com.lumiyaviewer.lumiya.ui.chat.profiles;

import com.lumiyaviewer.lumiya.slproto.messages.PickInfoReply;
import com.lumiyaviewer.lumiya.slproto.users.manager.AvatarPickKey;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.chat.profiles.UserPickFragment;
import com.lumiyaviewer.lumiya.ui.common.TextFieldDialogBuilder;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
final class _$Lambda$pe_zD6dKvPMIxwvN5gLJ2hSMvgo$6
implements TextFieldDialogBuilder.OnTextEnteredListener {
    private final Object -$f0;
    private final Object -$f1;
    private final Object -$f2;

    private final /* synthetic */ void $m$0(String string) {
        UserPickFragment.lambda$-com_lumiyaviewer_lumiya_ui_chat_profiles_UserPickFragment_13525((UserManager)this.-$f0, (AvatarPickKey)this.-$f1, (PickInfoReply)this.-$f2, string);
    }

    public /* synthetic */ _$Lambda$pe_zD6dKvPMIxwvN5gLJ2hSMvgo$6(Object object, Object object2, Object object3) {
        this.-$f0 = object;
        this.-$f1 = object2;
        this.-$f2 = object3;
    }

    @Override
    public final void onTextEntered(String string) {
        this.$m$0(string);
    }
}
