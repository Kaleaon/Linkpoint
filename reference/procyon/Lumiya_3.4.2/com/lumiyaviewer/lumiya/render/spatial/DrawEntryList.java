// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.render.spatial;

import com.lumiyaviewer.lumiya.utils.InlineListEntry;
import javax.annotation.Nullable;
import com.lumiyaviewer.lumiya.utils.InlineList;

public class DrawEntryList extends InlineList<DrawListEntry>
{
    @Nullable
    private final EntryRemovalListener listener;
    
    public DrawEntryList(@Nullable final EntryRemovalListener listener) {
        this.listener = listener;
    }
    
    @Override
    public void requestEntryRemoval(final DrawListEntry drawListEntry) {
        if (this.listener != null) {
            this.listener.onEntryRemovalRequested(drawListEntry);
        }
    }
    
    public interface EntryRemovalListener
    {
        void onEntryRemovalRequested(final DrawListEntry p0);
    }
}
