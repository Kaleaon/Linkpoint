package com.lumiyaviewer.lumiya.render.spatial;

import com.lumiyaviewer.lumiya.utils.InlineList;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
public class DrawEntryList extends InlineList<DrawListEntry> {
    @Nullable
    private final EntryRemovalListener listener;

    /* loaded from: classes.dex */
    public interface EntryRemovalListener {
        void onEntryRemovalRequested(DrawListEntry drawListEntry);
    }

    public DrawEntryList(@Nullable EntryRemovalListener entryRemovalListener) {
        this.listener = entryRemovalListener;
    }

    @Override // com.lumiyaviewer.lumiya.utils.InlineList
    public void requestEntryRemoval(DrawListEntry drawListEntry) {
        if (this.listener != null) {
            this.listener.onEntryRemovalRequested(drawListEntry);
        }
    }
}
