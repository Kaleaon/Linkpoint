package com.linkpoint.ui.chat

import android.content.Context
import com.linkpoint.slproto.users.ChatterID
import com.linkpoint.slproto.users.manager.UserManager
import com.linkpoint.ui.chat.contacts.ChatterItemViewBuilder

interface ChatterDisplayInfo {
    fun buildView(context: Context, viewBuilder: ChatterItemViewBuilder, userManager: UserManager)
    fun getChatterID(userManager: UserManager): ChatterID
    fun getDisplayName(): String?
}