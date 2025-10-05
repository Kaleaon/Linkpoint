package com.linkpoint.render.spatial
import java.util.*

import com.linkpoint.utils.InlineList
import javax.annotation.Nullable

class DrawEntryList : InlineList()<DrawListEntry> {
    private val EntryRemovalListener listener

    interface EntryRemovalListener {
        Unit onEntryRemovalRequested(DrawListEntry drawListEntry)
    }

    public DrawEntryList(EntryRemovalListener entryRemovalListener) {
        this.listener = entryRemovalListener
    }

    public Unit requestEntryRemoval(DrawListEntry drawListEntry) {
        if (this.listener != null) {
            this.listener.onEntryRemovalRequested(drawListEntry)
        }
    }
}
