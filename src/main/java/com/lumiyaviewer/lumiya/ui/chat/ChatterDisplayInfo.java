package com.lumiyaviewer.lumiya.ui.chat;

import android.content.Context;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.chat.contacts.ChatterItemViewBuilder;
import javax.annotation.Nullable;

public interface ChatterDisplayInfo {
    void buildView(Context context, ChatterItemViewBuilder chatterItemViewBuilder, UserManager userManager);

    ChatterID getChatterID(UserManager userManager);

    @Nullable
    String getDisplayName();
}
