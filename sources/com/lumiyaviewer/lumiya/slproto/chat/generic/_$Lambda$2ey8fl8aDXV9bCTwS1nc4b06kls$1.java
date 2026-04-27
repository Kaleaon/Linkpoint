/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.view.View
 */
package com.lumiyaviewer.lumiya.slproto.chat.generic;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.lumiyaviewer.lumiya.slproto.chat.generic.ChatEventViewHolder;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;

final class _$Lambda$2ey8fl8aDXV9bCTwS1nc4b06kls$1
implements ChatEventViewHolder.Factory {
    private final /* synthetic */ ChatEventViewHolder $m$0(View view, RecyclerView.Adapter adapter) {
        return SLChatEvent.ChatMessageViewType.-com_lumiyaviewer_lumiya_slproto_chat_generic_SLChatEvent$ChatMessageViewType-mthref-1(view, adapter);
    }

    @Override
    public final ChatEventViewHolder createViewHolder(View view, RecyclerView.Adapter adapter) {
        return this.$m$0(view, adapter);
    }
}
