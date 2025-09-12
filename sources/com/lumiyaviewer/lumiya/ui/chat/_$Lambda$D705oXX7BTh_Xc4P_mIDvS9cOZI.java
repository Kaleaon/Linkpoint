/*
 * Decompiled with CFR 0.152.
 */
package com.lumiyaviewer.lumiya.ui.chat;

import com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever;
import com.lumiyaviewer.lumiya.ui.chat.ExportChatHistoryTask;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
final class _$Lambda$D705oXX7BTh_Xc4P_mIDvS9cOZI
implements ChatterNameRetriever.OnChatterNameUpdated {
    private final Object -$f0;

    private final /* synthetic */ void $m$0(ChatterNameRetriever chatterNameRetriever) {
        ((ExportChatHistoryTask)((Object)this.-$f0)).lambda$-com_lumiyaviewer_lumiya_ui_chat_ExportChatHistoryTask_2020(chatterNameRetriever);
    }

    public /* synthetic */ _$Lambda$D705oXX7BTh_Xc4P_mIDvS9cOZI(Object object) {
        this.-$f0 = object;
    }

    @Override
    public final void onChatterNameUpdated(ChatterNameRetriever chatterNameRetriever) {
        this.$m$0(chatterNameRetriever);
    }
}
