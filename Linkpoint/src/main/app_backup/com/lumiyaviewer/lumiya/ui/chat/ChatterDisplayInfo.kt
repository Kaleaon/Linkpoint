package com.lumiyaviewer.lumiya.ui.chat

import android.content.Context
import com.lumiyaviewer.lumiya.slproto.users.ChatterID
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager
import com.lumiyaviewer.lumiya.ui.chat.contacts.ChatterItemViewBuilder

interface ChatterDisplayInfo {
    fun buildView(context: Context, chatterItemViewBuilder: ChatterItemViewBuilder, userManager: UserManager)

    fun getChatterID(userManager: UserManager): ChatterID

    fun getDisplayName(): String?
}