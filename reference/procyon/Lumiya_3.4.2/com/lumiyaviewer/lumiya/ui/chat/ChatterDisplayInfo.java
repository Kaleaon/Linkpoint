// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.chat;

import javax.annotation.Nullable;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.chat.contacts.ChatterItemViewBuilder;
import android.content.Context;

public interface ChatterDisplayInfo
{
    void buildView(final Context p0, final ChatterItemViewBuilder p1, final UserManager p2);
    
    ChatterID getChatterID(final UserManager p0);
    
    @Nullable
    String getDisplayName();
}
