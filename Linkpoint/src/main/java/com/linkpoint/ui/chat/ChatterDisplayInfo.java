package com.linkpoint.ui.chat;

import android.content.Context;
import com.linkpoint.slproto.users.ChatterID;
import com.linkpoint.slproto.users.manager.UserManager;
import com.linkpoint.ui.chat.contacts.ChatterItemViewBuilder;
import javax.annotation.Nullable;

public interface ChatterDisplayInfo {
    void buildView(Context context, ChatterItemViewBuilder chatterItemViewBuilder, UserManager userManager);

    ChatterID getChatterID(UserManager userManager);

    @Nullable
    String getDisplayName();
}
