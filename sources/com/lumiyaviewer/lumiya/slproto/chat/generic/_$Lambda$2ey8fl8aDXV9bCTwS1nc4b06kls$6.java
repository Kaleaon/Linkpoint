/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.content.Context
 *  android.view.MenuItem
 */
package com.lumiyaviewer.lumiya.slproto.chat.generic;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
final class _$Lambda$2ey8fl8aDXV9bCTwS1nc4b06kls$6
implements PopupMenu.OnMenuItemClickListener {
    private final Object -$f0;
    private final Object -$f1;

    private final /* synthetic */ boolean $m$0(MenuItem menuItem) {
        return ((SLChatEvent)this.-$f0).lambda$-com_lumiyaviewer_lumiya_slproto_chat_generic_SLChatEvent_21084((Context)this.-$f1, menuItem);
    }

    public /* synthetic */ _$Lambda$2ey8fl8aDXV9bCTwS1nc4b06kls$6(Object object, Object object2) {
        this.-$f0 = object;
        this.-$f1 = object2;
    }

    @Override
    public final boolean onMenuItemClick(MenuItem menuItem) {
        return this.$m$0(menuItem);
    }
}
