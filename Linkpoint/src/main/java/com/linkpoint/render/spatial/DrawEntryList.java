package com.linkpoint.render.spatial;
import java.util.*;

import com.linkpoint.utils.InlineList;
import javax.annotation.Nullable;

public class DrawEntryList extends InlineList<DrawListEntry> {
    @Nullable
    private final EntryRemovalListener listener;

    public interface EntryRemovalListener {
        void onEntryRemovalRequested(DrawListEntry drawListEntry);
    }

    public DrawEntryList(@Nullable EntryRemovalListener entryRemovalListener) {
        this.listener = entryRemovalListener;
    }

    public void requestEntryRemoval(DrawListEntry drawListEntry) {
        if (this.listener != null) {
            this.listener.onEntryRemovalRequested(drawListEntry);
        }
    }
}
